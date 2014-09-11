/*
 * Copyright 2013-2014 Richard M. Hightower
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * __________                              _____          __   .__
 * \______   \ ____   ____   ____   /\    /     \ _____  |  | _|__| ____    ____
 *  |    |  _//  _ \ /  _ \ /    \  \/   /  \ /  \\__  \ |  |/ /  |/    \  / ___\
 *  |    |   (  <_> |  <_> )   |  \ /\  /    Y    \/ __ \|    <|  |   |  \/ /_/  >
 *  |______  /\____/ \____/|___|  / \/  \____|__  (____  /__|_ \__|___|  /\___  /
 *         \/                   \/              \/     \/     \/       \//_____/
 *      ____.                     ___________   _____    ______________.___.
 *     |    |____ ___  _______    \_   _____/  /  _  \  /   _____/\__  |   |
 *     |    \__  \\  \/ /\__  \    |    __)_  /  /_\  \ \_____  \  /   |   |
 * /\__|    |/ __ \\   /  / __ \_  |        \/    |    \/        \ \____   |
 * \________(____  /\_/  (____  / /_______  /\____|__  /_______  / / ______|
 *               \/           \/          \/         \/        \/  \/
 */

package org.boon.datarepo.impl;

import org.boon.Sets;
import org.boon.core.Conversions;
import org.boon.core.reflection.fields.FieldAccess;
import org.boon.criteria.ObjectFilter;
import org.boon.criteria.Criterion;
import org.boon.criteria.internal.*;
import org.boon.datarepo.Filter;
import org.boon.datarepo.LookupIndex;
import org.boon.datarepo.ResultSet;
import org.boon.datarepo.SearchableCollection;
import org.boon.datarepo.spi.FilterComposer;
import org.boon.datarepo.spi.ResultSetInternal;
import org.boon.datarepo.spi.SearchIndex;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.boon.criteria.ObjectFilter.instanceOf;
import static org.boon.criteria.ObjectFilter.not;

/**
 * This class should be renamed mother of all beasts.
 * This class is the reason I have no hair.
 * It implements the first cut of a decent criteria plan.
 *
 * @author Rick Hightower
 */
public class FilterDefault implements Filter, FilterComposer {

    private Set<Operator> indexedOperators =
            Sets.set( Operator.BETWEEN, Operator.EQUAL, Operator.STARTS_WITH,
                    Operator.GREATER_THAN, Operator.GREATER_THAN_EQUAL,
                    Operator.LESS_THAN, Operator.LESS_THAN_EQUAL );

    private Map<String, FieldAccess> fields;
    private SearchableCollection searchableCollection;
    private Map<String, SearchIndex> searchIndexMap;
    private Map<String, LookupIndex> lookupIndexMap;


    /**
     * Seems innocent enough. Give me some criteria expressions,
     * and i will give you a nice results set.
     *
     * @param expressions listStream of expressions
     * @return result set
     * @see ResultSet
     * @see org.boon.criteria.internal.Criteria
     */
    @Override
    public ResultSet filter( Criteria... expressions ) {
        try {
            return mainQueryPlan( expressions );
        } finally {
        }
    }


    /**
     * This is the main criteria plan in case the name was not
     * obvious.
     *
     * @param expressions
     * @return
     * @author Rick Hightower
     */
    private ResultSet mainQueryPlan( Criteria[] expressions ) {



        ResultSetInternal results = new ResultSetImpl( this.fields );

        if (expressions == null || expressions.length == 0) {
            results.addResults ( searchableCollection.all() );
        }

        /* I am sure this looked easy to read when I wrote it.
         * If there is only one expression and first expression is a group then
         * the group is that first expression otherwise wrap
         * all of the expressions in an and clause. */
        Group group = expressions.length ==
                1 && expressions[ 0 ] instanceof Group
                ? ( Group ) expressions[ 0 ] : ObjectFilter.and( expressions );

        /**
         * Run the filter on the group.
         */
        doFilterGroup( group, results );

        return results;
    }


    private void orPlanWithIndex( Criterion criterion, ResultSetInternal results ) {


        Operator operator = criterion.getOperator();
        if ( operator == Operator.EQUAL && lookupIndexMap.get( criterion.getName() ) != null ) {
            doFilterWithIndex( criterion, fields, results );
        } else if ( this.isIndexed( criterion.getName() ) && Sets.in( operator, indexedOperators ) ) {
            doFilterWithIndex( criterion, fields, results );
        } else {
            List list = QueryFactory.filter( this.searchableCollection.all(), criterion );
            results.addResults( list );
        }

    }

    @Override
    public void invalidate() {

    }

    /**
     * Run the filter on the group.
     *
     * @param group   here is the group
     * @param results here are the results
     */
    private void doFilterGroup( Group group, ResultSetInternal results ) {
        /* The group was n or group so handle it that way. */
        if ( group.getGrouping() == Grouping.OR ) {
            /* nice short method name, or. */
            or( group.getExpressions(), fields, results );
        } else {
            /* create a result internal (why?), wrap the fields in the result set
            internal, and pass that to the and method.
             */
            ResultSetInternal resultsForAnd = new ResultSetImpl( fields );
            and( group.getExpressions(), fields, resultsForAnd );
            results.addResults( resultsForAnd.asList() );
        }
    }

    private void or( List<Criteria> expressions,
                     Map<String, FieldAccess> fields, ResultSetInternal results ) {


        for ( Criteria expression : expressions ) {
            if ( expression instanceof Criterion ) {
                orPlanWithIndex( ( Criterion ) expression, results );
            } else if ( expression instanceof Group ) {
                doFilterGroup( ( Group ) expression, results );
            }
        }
    }


    private void and( List<Criteria> expressions, Map<String, FieldAccess> fields, ResultSetInternal resultSet ) {

        Set<Criteria> expressionSet = Sets.set( expressions );


        boolean foundIndex = applyIndexedFiltersForAnd( expressions, fields, expressionSet, resultSet );

        resultSet.andResults();
        applyLinearSearch(expressionSet, resultSet, foundIndex);
        applyGroups( expressionSet, resultSet );


    }


    private boolean applyIndexedFiltersForAnd( List<Criteria> expressions, Map<String, FieldAccess> fields, Set<Criteria> expressionSet, ResultSetInternal resultSet ) {
        Criterion criteria = null;
        boolean foundIndex = false;


        if ( expressions.size() == 1 && expressions.get(0) instanceof Criterion ) {
            criteria = ( Criterion ) expressions.get(0);
            foundIndex = doFilterWithIndex( criteria, fields, resultSet );
            if ( foundIndex ) {
                expressionSet.remove( criteria );
            }

            return foundIndex;
        }

        int foundCount =0 ;

        for ( Criteria expression : expressions ) {
            /*
             * See if the criteria has an index
             */
            if ( expression instanceof Criterion ) {
                criteria = ( Criterion ) expression;


                if (doFilterWithIndex( criteria, fields, resultSet )) {
                    foundCount++;
                }

            }
        }
        return foundCount > 0;
    }


//    private List applyGroupsWithIndexesForAnd(List items, Set<Query> expressionSet) {
//
//        List<HashSet> listOfSets = new ArrayList();
//        listOfSets.addObject(new HashSet(items));
//
//        List<Query> expressionsWeEvaluated = new ArrayList<>();
//
//        outer:
//        for (Query expression : expressionSet) {
//
//            if (expression instanceof Group) {
//                Group group = (Group) expression;
//                for (Query innerExpression : group.getExpressions()) {
//                    //Don't allow non-index Criterion to avoid too many scans
//                    if (innerExpression instanceof Criterion) {
//                        Criterion c = (Criterion) innerExpression;
//                        if (!this.isIndexed(c.name())) {
//                            continue outer;
//                        }
//                    }
//                    //Don't allow any ors to avoid long scans, at this point
//                    //This is simple for now, it does not recusively look for indexes, future one should.
//                    else if (innerExpression instanceof Group) {
//                        continue;
//                    }
//                }
//
//
//                /*
//                At this point, this group should be indexed only
//                 */
//                List listStream = doFilterGroup((Group) expression);
//                if (listStream.size() > 0) {
//                    listOfSets.addObject(new HashSet(listStream));
//                    expressionsWeEvaluated.addObject(expression);
//                }
//            }
//        }
//        List results = reduceToResults(listOfSets);
//        expressionSet.removeAll(expressionsWeEvaluated);
//
//        return results;
//    }


    private void applyGroups( Set<Criteria> expressionSet, ResultSetInternal resultSet ) {

        if ( expressionSet.size() == 0 ) {
            return;
        }


        for ( Criteria expression : expressionSet ) {

            if ( expression instanceof Group ) {
                doFilterGroup( ( Group ) expression, resultSet );
            }
        }
    }


    private void applyLinearSearch( Set<Criteria> expressionSet, ResultSetInternal resultSet, boolean foundIndex ) {

        if ( expressionSet.size() == 0 ) {
            return;
        }

        Criteria[] expressions = Conversions.array( Criteria.class, QueryFactory.filter( expressionSet, not( instanceOf( Group.class ) ) ) );

        if ( foundIndex ) {
            resultSet.filterAndPrune( ObjectFilter.and( expressions ) );
        } else {
            resultSet.addResults(
                    QueryFactory.filter( searchableCollection.all(),
                            ObjectFilter.and( expressions ) )
            );
        }
        for ( Criteria expression : expressions ) {
            expressionSet.remove( expression );
        }

    }


    private boolean isIndexed( String name ) {
        return searchIndexMap.containsKey( name );
    }

    private boolean doFilterWithIndex( Criterion criterion, Map<String, FieldAccess> fields, ResultSetInternal resultSet ) {


        boolean indexed = indexedOperators.contains( criterion.getOperator() );

        if ( !indexed ) {
            return false;
        }


        String name = criterion.getName();
        Object value = criterion.getValue();
        Operator operator = criterion.getOperator();
        SearchIndex searchIndex = searchIndexMap.get( name );
        LookupIndex lookupIndex = lookupIndexMap.get( name );
        List resultList = null;
        boolean foundIndex;

        if ( lookupIndex != null && operator == Operator.EQUAL ) {
            foundIndex = true;
            resultList = lookupIndex.getAll( value );
            if ( resultList != null ) {
                resultSet.addResults( resultList );
                return foundIndex;
            } else {
                resultSet.addResults( Collections.EMPTY_LIST );
                return foundIndex;
            }
        }

        if ( searchIndex == null ) {
            return false;
        }

        foundIndex = true;


        switch ( operator ) {
            case EQUAL:
                resultList = processResultsFromIndex( searchIndex, searchIndex.findEquals( value ) );
                break;
            case STARTS_WITH:
                resultList = searchIndex.findStartsWith( value );
                break;

            case GREATER_THAN:
                resultList = searchIndex.findGreaterThan( value );
                break;

            case GREATER_THAN_EQUAL:
                resultList = searchIndex.findGreaterThanEqual( value );
                break;

            case LESS_THAN:
                resultList = searchIndex.findLessThan( value );
                break;

            case LESS_THAN_EQUAL:
                resultList = searchIndex.findLessThanEqual( value );
                break;

            case BETWEEN:
                resultList = searchIndex.findBetween( criterion.getValue(), criterion.getValues()[ 1 ] );
                break;


        }

        criterion.clean();

        if ( resultList != null ) {
            resultSet.addResults( resultList );
            return foundIndex;
        } else {
            return foundIndex;
        }

    }

    private List processResultsFromIndex( SearchIndex searchIndex, List results ) {
        if ( searchIndex.isPrimaryKeyOnly() ) {
            //TODO iterate through listStream and lookup items from keys, and put those in the actual results
            return null;
        } else {
            return results;
        }
    }


    @Override
    public void setSearchableCollection( SearchableCollection searchableCollection ) {
        this.searchableCollection = searchableCollection;
    }

    @Override
    public void setFields( Map<String, FieldAccess> fields ) {
        this.fields = fields;
    }

    @Override
    public void setSearchIndexMap( Map<String, SearchIndex> searchIndexMap ) {
        this.searchIndexMap = searchIndexMap;
    }

    @Override
    public void setLookupIndexMap( Map<String, LookupIndex> lookupIndexMap ) {
        this.lookupIndexMap = lookupIndexMap;
    }

    @Override
    public void init() {

    }
}