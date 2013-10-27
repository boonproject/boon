package org.boon.datarepo.impl;

import org.boon.Sets;
import org.boon.core.reflection.Conversions;
import org.boon.datarepo.Filter;
import org.boon.datarepo.LookupIndex;
import org.boon.datarepo.ResultSet;
import org.boon.datarepo.SearchableCollection;
import org.boon.criteria.*;
import org.boon.datarepo.spi.FilterComposer;
import org.boon.datarepo.spi.ResultSetInternal;
import org.boon.datarepo.spi.SearchIndex;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.boon.criteria.CriteriaFactory.instanceOf;
import static org.boon.criteria.CriteriaFactory.not;

import org.boon.core.reflection.fields.FieldAccess;

/**
 * This class should be renamed mother of all beasts.
 * This class is the reason I have no hair.
 * It implements the first cut of a decent criteria plan.
 * @author Rick Hightower
 */
public class FilterDefault implements Filter, FilterComposer {

    private Set<Operator> indexedOperators =
            Sets.set(Operator.BETWEEN, Operator.EQUAL, Operator.STARTS_WITH,
                    Operator.GREATER_THAN, Operator.GREATER_THAN_EQUAL,
                    Operator.LESS_THAN, Operator.LESS_THAN_EQUAL);

    private Map<String, FieldAccess> fields;
    private SearchableCollection searchableCollection;
    private Map<String, SearchIndex> searchIndexMap;
    private Map<String, LookupIndex> lookupIndexMap;


    /**
     * Seems innocent enough. Give me some criteria expressions,
     * and i will give you a nice results set.
     * @see ResultSet
     * @see org.boon.criteria.Criteria
     * @param expressions list of expressions
     * @return result set
     */
    @Override
    public ResultSet filter(Criteria... expressions) {
        try {
            Criteria.fields(this.fields);
            return mainQueryPlan(expressions);
        } finally {
            Criteria.clearFields();
        }
    }


    /**
     * This is the main criteria plan in case the name was not
     * obvious.
     *
     * @author Rick Hightower
     * @param expressions
     * @return
     */
    private ResultSet mainQueryPlan(Criteria[] expressions) {

        ResultSetInternal results = new ResultSetImpl(this.fields);

        /* I am sure this looked easy to read when I wrote it.
         * If there is only one expression and first expression is a group then
         * the group is that first expression otherwise wrap
         * all of the expressions in an and clause. */
        Group group = expressions.length ==
                1 && expressions[0] instanceof Group
                ? (Group) expressions[0] : CriteriaFactory.and(expressions);

        /**
         * Run the filter on the group.
         */
        doFilterGroup(group, results);

        return results;
    }


    private void orPlanWithIndex(Criterion criterion, ResultSetInternal results) {


        Operator operator = criterion.getOperator();
        if (operator == Operator.EQUAL && lookupIndexMap.get(criterion.getName()) != null) {
            doFilterWithIndex(criterion, fields, results);
        } else if (this.isIndexed(criterion.getName()) && Sets.in(operator, indexedOperators)) {
            doFilterWithIndex(criterion, fields, results);
        } else {
            List list = QueryFactory.filter(this.searchableCollection.all(), criterion);
            results.addResults(list);
        }

    }

    @Override
    public void invalidate() {

    }

    /**
     * Run the filter on the group.
     * @param group here is the group
     * @param results here are the results
     */
    private void doFilterGroup(Group group, ResultSetInternal results) {
        /* The group was n or group so handle it that way. */
        if ( group.getGrouping() == Grouping.OR ) {
            /* nice short method name, or. */
            or( group.getExpressions(), fields, results );
        } else {
            /* create a result internal (why?), wrap the fields in the result set
            internal, and pass that to the and method.
             */
            ResultSetInternal resultsForAnd = new ResultSetImpl(fields);
            and( group.getExpressions(), fields, resultsForAnd );
            results.addResults( resultsForAnd.asList() );
        }
    }

    private void or(Criteria[] expressions,
                    Map<String, FieldAccess> fields, ResultSetInternal results) {


        for (Criteria expression : expressions) {
            if (expression instanceof Criterion) {
                orPlanWithIndex((Criterion) expression, results);
            } else if (expression instanceof Group) {
                doFilterGroup((Group) expression, results);
            }
        }
    }


    private void and(Criteria[] expressions, Map<String, FieldAccess> fields, ResultSetInternal resultSet) {

        Set<Criteria> expressionSet = Sets.set(expressions);


        boolean foundIndex = applyIndexedFiltersForAnd(expressions, fields, expressionSet, resultSet);
        applyLinearSearch(expressionSet, resultSet, foundIndex);
        applyGroups(expressionSet, resultSet);


    }


    private boolean applyIndexedFiltersForAnd(Criteria[] expressions, Map<String, FieldAccess> fields, Set<Criteria> expressionSet, ResultSetInternal resultSet) {
        Criterion criteria = null;
        boolean foundIndex = false;

        if (expressions.length == 1 && expressions[0] instanceof Criterion) {
            criteria = (Criterion) expressions[0];
            foundIndex = doFilterWithIndex(criteria, fields, resultSet);
            if (foundIndex) {
                expressionSet.remove(criteria);
            }
            return foundIndex;
        }


        for (Criteria expression : expressions) {
            /*
             * See if the criteria has an index
             */
            if (expression instanceof Criterion) {
                criteria = (Criterion) expression;

                foundIndex = doFilterWithIndex(criteria, fields, resultSet);
                if (foundIndex) {
                    expressionSet.remove(criteria);
                }
                /* if it is less than 20, just linear search the rest. */
                if (resultSet.lastSize() < 20) {
                    resultSet.andResults(); //consolidate now
                    return foundIndex;
                } else if (resultSet.lastSize() > 0) {
                    //No op
                }

            }
        }
        if (foundIndex) {
            resultSet.andResults();
        }
        return foundIndex;
    }


//    private List applyGroupsWithIndexesForAnd(List items, Set<Query> expressionSet) {
//
//        List<HashSet> listOfSets = new ArrayList();
//        listOfSets.add(new HashSet(items));
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
//                        if (!this.isIndexed(c.getName())) {
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
//                List list = doFilterGroup((Group) expression);
//                if (list.size() > 0) {
//                    listOfSets.add(new HashSet(list));
//                    expressionsWeEvaluated.add(expression);
//                }
//            }
//        }
//        List results = reduceToResults(listOfSets);
//        expressionSet.removeAll(expressionsWeEvaluated);
//
//        return results;
//    }


    private void applyGroups(Set<Criteria> expressionSet, ResultSetInternal resultSet) {

        if (expressionSet.size() == 0) {
            return;
        }


        for (Criteria expression : expressionSet) {

            if (expression instanceof Group) {
                doFilterGroup((Group) expression, resultSet);
            }
        }
    }


    private void applyLinearSearch(Set<Criteria> expressionSet, ResultSetInternal resultSet, boolean foundIndex) {

        if (expressionSet.size() == 0) {
            return;
        }

        Criteria[] expressions = Conversions.array(Criteria.class, QueryFactory.filter(expressionSet, not(instanceOf(Group.class))));

        if (foundIndex) {
            resultSet.filterAndPrune(CriteriaFactory.and(expressions));
        } else {
            resultSet.addResults(
                    QueryFactory.filter(searchableCollection.all(),
                            CriteriaFactory.and(expressions))
            );
        }
        for (Criteria expression : expressions) {
            expressionSet.remove(expression);
        }

    }


    private boolean isIndexed(String name) {
        return searchIndexMap.containsKey(name);
    }

    private boolean doFilterWithIndex(Criterion criterion, Map<String, FieldAccess> fields, ResultSetInternal resultSet) {
        String name = criterion.getName();
        Object value = criterion.getValue();
        Operator operator = criterion.getOperator();
        SearchIndex searchIndex = searchIndexMap.get(name);
        LookupIndex lookupIndex = lookupIndexMap.get(name);
        List resultList = null;
        boolean foundIndex = false;

        if (lookupIndex != null && operator == Operator.EQUAL) {
            foundIndex = true;
            resultList = lookupIndex.getAll(value);
            if (resultList != null) {
                resultSet.addResults(resultList);
                return foundIndex;
            } else {
                resultSet.addResults(Collections.EMPTY_LIST);
                return foundIndex;
            }
        }

        if (searchIndex == null) {
            return false;
        }

        foundIndex = true;

        if (!criterion.isInitialized()) {
            criterion.init(this.fields);
        }

        switch (operator) {
            case EQUAL:
                resultList = processResultsFromIndex(searchIndex, searchIndex.findEquals(value));
                break;
            case STARTS_WITH:
                resultList = searchIndex.findStartsWith(value);
                break;

            case GREATER_THAN:
                resultList = searchIndex.findGreaterThan(value);
                break;

            case GREATER_THAN_EQUAL:
                resultList = searchIndex.findGreaterThanEqual(value);
                break;

            case LESS_THAN:
                resultList = searchIndex.findLessThan(value);
                break;

            case LESS_THAN_EQUAL:
                resultList = searchIndex.findLessThanEqual(value);
                break;

            case BETWEEN:
                resultList = searchIndex.findBetween(criterion.getValue(), criterion.getValues()[1]);
                break;
        }

        if (resultList != null) {
            resultSet.addResults(resultList);
            return foundIndex;
        } else {
            return foundIndex;
        }
    }

    private List processResultsFromIndex(SearchIndex searchIndex, List results) {
        if (searchIndex.isPrimaryKeyOnly()) {
            //TODO iterate through list and lookup items from keys, and put those in the actual results
            return null;
        } else {
            return results;
        }
    }


    @Override
    public void setSearchableCollection(SearchableCollection searchableCollection) {
        this.searchableCollection = searchableCollection;
    }

    @Override
    public void setFields(Map<String, FieldAccess> fields) {
        this.fields = fields;
    }

    @Override
    public void setSearchIndexMap(Map<String, SearchIndex> searchIndexMap) {
        this.searchIndexMap = searchIndexMap;
    }

    @Override
    public void setLookupIndexMap(Map<String, LookupIndex> lookupIndexMap) {
        this.lookupIndexMap = lookupIndexMap;
    }

    @Override
    public void init() {

    }
}