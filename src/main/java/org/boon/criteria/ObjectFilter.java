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

package org.boon.criteria;


import org.boon.Boon;
import org.boon.core.Conversions;
import org.boon.core.Predicate;
import org.boon.core.Typ;
import org.boon.core.Type;
import org.boon.core.reflection.BeanUtils;
import org.boon.core.reflection.Invoker;
import org.boon.core.reflection.fields.FieldAccess;
import org.boon.criteria.internal.*;

import java.util.*;

import static org.boon.Boon.fromJson;
import static org.boon.Boon.iterator;
import static org.boon.Exceptions.die;
import static org.boon.Lists.atIndex;
import static org.boon.Ok.okOrDie;


/**
 * Filters objects based on Criteria and Predicates.
 * Also allows construction of predicates.
 */
public class ObjectFilter {


    /**
     * Does the object match this expression.
     * An expression is a collection of criteria.
     * @param obj object in question
     * @param exp expression
     * @return true or false
     */
    public static boolean matches( Object obj, Criteria... exp ) {
        return ObjectFilter.and( exp ).test( obj );
    }

    /**
     * Does the object match this predicate
     * @param obj object in question
     * @param exp predicate
     * @return true or false
     */
    public static boolean matches( Object obj, Predicate exp ) {
        return exp.test(obj);
    }


    public static boolean matches( Object obj, List<Criteria> expressions ) {
        return ObjectFilter.and( expressions.toArray( new Criteria[expressions.size()] ) ).test( obj );
    }

    public static <T> List<T> filter( Collection<T> items, List<Criteria> expressions ) {
        if ( items.size() == 0 ) {
            return Collections.EMPTY_LIST;
        }

        List<T> results = new ArrayList<>();
        for ( T item : items ) {
            if (ObjectFilter.and( expressions.toArray( new Criteria[expressions.size()] ) ).test( item )) {
                results.add( item );
            }
        }
        return results;
    }

    public static <T> List<T> filter( Collection<T> items, Criteria... exp ) {
        if ( items.size() == 0 ) {
            return Collections.EMPTY_LIST;
        }

        List<T> results = new ArrayList<>();
        for ( T item : items ) {
            if ( ObjectFilter.and( exp ).test( item ) ) {
                results.add( item );
            }
        }
        return results;
    }

    public static Not not( Criteria expression ) {
        return new Not( expression );
    }

    public static Group and( Criteria... expressions ) {
        return new Group.And( expressions );
    }



    public static Group or( Criteria... expressions ) {
        return new Group.Or( expressions );
    }

    public static Criterion eqNestedAdvanced( final Object value, final Object... path ) {
        return new Criterion<Object>( Boon.joinBy( '.', path ), Operator.EQUAL, value ) {
            @Override
            public boolean resolve( Map<String, FieldAccess> fields, Object owner ) {
                Object v = BeanUtils.getPropByPath(path);
                if ( v instanceof List ) {
                    List list = ( List ) v;
                    for ( Object i : list ) {
                        if ( i.equals( value ) ) {
                            return false;
                        }
                    }
                    return true;
                } else {
                    return value.equals( v );
                }

            }

        };
    }

    public static Criterion eqNested( final Object value, final String... path ) {
        return new Criterion<Object>( Boon.joinBy( '.', path ), Operator.EQUAL, value ) {
            @Override
            public boolean resolve( Map<String, FieldAccess> fields, Object owner ) {
                Object v = BeanUtils.getPropertyValue(owner, path);
                if ( v instanceof List ) {
                    List list = ( List ) v;
                    for ( Object i : list ) {
                        if ( i.equals( value ) ) {
                            return false;
                        }
                    }
                    return true;
                } else {
                    return value.equals( v );
                }

            }

        };
    }

    public static Criterion eq( final Object name, final Object value ) {
        return new Criterion<Object>( name.toString(), Operator.EQUAL, value ) {
            @Override
            public boolean resolve( Map<String, FieldAccess> fields, Object owner ) {


                Object compareValue = getValueToCompare(fields, name, value, owner);
                return value.equals( compareValue );
            }
        };
    }

    public static Criterion typeOf( final String className ) {
        return new Criterion<Object>( "_type", Operator.EQUAL, className ) {
            @Override
            public boolean resolve( Map<String, FieldAccess> fields, Object owner ) {

                Class cls = owner.getClass();
                while ( cls != Typ.object ) {
                    String simpleName = cls.getSimpleName();
                    String name = cls.getName();
                    if ( simpleName.equals( className ) || name.equals( className ) ) {
                        return true;
                    }


                    Class[] interfaces = cls.getInterfaces();
                    for ( Class<?> i : interfaces ) {
                        simpleName = i.getSimpleName();
                        name = i.getName();

                        if ( simpleName.equals( className ) || name.equals( className ) ) {
                            return true;
                        }
                    }
                    cls = cls.getSuperclass();
                }
                return false;
            }
        };
    }

    public static Criterion instanceOf( final Class<?> cls ) {
        return new Criterion<Object>( "_type", Operator.EQUAL, cls.getName() ) {
            @Override
            public boolean resolve( Map<String, FieldAccess> fields, Object owner ) {
                return Typ.isSuperClass( owner.getClass(), cls );
            }
        };
    }

    public static Criterion implementsInterface( final Class<?> cls ) {
        return new Criterion<Object>( "_type", Operator.EQUAL, cls.getName() ) {
            @Override
            public boolean resolve( Map<String, FieldAccess> fields, Object owner ) {
                return Typ.implementsInterface( owner.getClass(), cls );
            }
        };
    }

    public static Criterion notEq( final Object name, final Object value ) {
        return new Criterion<Object>( name.toString(), Operator.NOT_EQUAL, value ) {
            @Override
            public boolean resolve( Map<String, FieldAccess> fields, Object owner ) {
                FieldAccess field = fields.get( name );

                return !value.equals( field.getValue( owner ) );
            }
        };
    }

    public static Criterion notIn( final Object name, final Object... values ) {
        return new Criterion<Object>( name.toString(), Operator.NOT_IN, values ) {

            HashSet set = new HashSet<>();

            {
                for ( Object value : values ) {
                    set.add( value );
                }
            }

            @Override
            public boolean resolve( Map<String, FieldAccess> fields, Object owner ) {
                FieldAccess field = fields.get( name );

                Object value = field.getValue( owner );
                if ( value == null ) {
                    return false;
                }
                return !set.contains( value );
            }
        };
    }

    public static Criterion in( final Object name, final Object... values ) {
        return new Criterion<Object>( name.toString(), Operator.IN, values ) {
            HashSet set = new HashSet<>();

            {
                for ( Object value : values ) {
                    set.add( value );
                }
            }

            @Override
            public boolean resolve( Map<String, FieldAccess> fields, Object owner ) {
                FieldAccess field = fields.get( name );

                return set.contains( field.getValue( owner ) );
            }
        };
    }

    public static Criterion lt( final Object name, final Object value ) {
        return new Criterion<Object>( name.toString(), Operator.LESS_THAN, value ) {
            @Override
            public boolean resolve( Map<String, FieldAccess> fields, Object owner ) {
                FieldAccess field = fields.get( name );

                return ( ( Comparable ) value ).compareTo( field.getValue( owner ) ) > 0;
            }
        };
    }

    public static Criterion lte( final Object name, final Object value ) {
        return new Criterion<Object>( name.toString(), Operator.LESS_THAN_EQUAL, value ) {
            @Override
            public boolean resolve( Map<String, FieldAccess> fields, Object owner ) {
                FieldAccess field = fields.get( name );

                return ( ( Comparable ) value ).compareTo( field.getValue( owner ) ) >= 0;

            }
        };
    }

    public static Criterion gt( final Object name, final Object value ) {
        return new Criterion<Object>( name.toString(), Operator.GREATER_THAN, value ) {
            @Override
            public boolean resolve( Map<String, FieldAccess> fields, Object owner ) {
                FieldAccess field = fields.get( name );

                return ( ( Comparable ) value ).compareTo( field.getValue( owner ) ) < 0;
            }
        };
    }

    public static Criterion gte( final Object name, final Object value ) {
        return new Criterion<Object>( name.toString(), Operator.GREATER_THAN_EQUAL, value ) {
            @Override
            public boolean resolve( Map<String, FieldAccess> fields, Object owner ) {
                FieldAccess field = fields.get( name );

                return ( ( Comparable ) value ).compareTo( field.getValue( owner ) ) <= 0;
            }
        };
    }

    public static Criterion between( final Object name, final Object value, final Object value2 ) {
        return new Criterion<Object>( name.toString(), Operator.BETWEEN, value, value2 ) {
            @Override
            public boolean resolve( Map<String, FieldAccess> fields, Object owner ) {
                FieldAccess field = fields.get( name );

                return ( ( Comparable ) value ).compareTo( field.getValue( owner ) ) <= 0 &&
                        ( ( Comparable ) value2 ).compareTo( field.getValue( owner ) ) >= 0;
            }
        };
    }

    public static Criterion between( Class clazz, Object name, final String svalue, final String svalue2 ) {

        FieldAccess field = null;
        Criterion c = null;

        if (clazz != null) {
            field = BeanUtils.getField(clazz, name.toString());
        }

        if ( field == null ) {
            c  = between( name, svalue, svalue2 );
        } else {

            Object o = Conversions.coerce( field.type(), svalue );
            Object o2 = Conversions.coerce( field.type(), svalue2 );

            c =  between( name, o, o2 );

        }

        c.initByClass( clazz );


        return c;
    }

    public static Criterion between( final Object name, final String svalue, final String svalue2 ) {
        return new Criterion<Object>( name.toString(), Operator.BETWEEN, svalue, svalue2 ) {
            @Override
            public boolean resolve( Map<String, FieldAccess> fields, Object owner ) {
                FieldAccess field = fields.get( name );

                return ( ( Comparable ) value ).compareTo( field.getValue( owner ) ) <= 0 &&
                        ( ( Comparable ) values[ 1 ] ).compareTo( field.getValue( owner ) ) >= 0;
            }
        };
    }

    public static Criterion gt( final Object name, String svalue ) {
        return new Criterion<Object>( name.toString(), Operator.GREATER_THAN, svalue ) {
            @Override
            public boolean resolve( Map<String, FieldAccess> fields, Object owner ) {
                FieldAccess field = fields.get( name );

                Object fieldValue = field.getValue( owner );

                return ( ( Comparable ) value ).compareTo( fieldValue ) < 0;
            }
        };
    }

    public static Criterion startsWith( final Object name, final Object value ) {
        return new Criterion<Object>( name.toString(), Operator.STARTS_WITH, value ) {
            String sValue = value instanceof String ? ( String ) value : value.toString();

            @Override
            public boolean resolve( Map<String, FieldAccess> fields, Object owner ) {

                FieldAccess field = fields.get( name );
                Object itemValue = field.getValue( owner );
                String sItemvalue = itemValue instanceof String ? ( String ) itemValue : itemValue.toString();
                return sItemvalue.startsWith( sValue );
            }
        };
    }

    public static Criterion endsWith( final Object name, final Object value ) {
        return new Criterion<Object>( name.toString(), Operator.ENDS_WITH, value ) {
            String sValue = value instanceof String ? ( String ) value : value.toString();

            @Override
            public boolean resolve( Map<String, FieldAccess> fields, Object owner ) {
                FieldAccess field = fields.get( name );
                Object itemValue = field.getValue( owner );
                String sItemvalue = itemValue instanceof String ? ( String ) itemValue : itemValue.toString();
                return sItemvalue.endsWith( sValue );
            }
        };
    }

    public static Criterion notContains( final Object name, final Object value ) {
        return doContains( name, value, true );
    }

    public static Criterion contains( final Object name, final Object value ) {
        return doContains( name, value, false );
    }

    private static Criterion doContains( final Object name, final Object value, final boolean not ) {
        return new Criterion<Object>( name.toString(), not ? Operator.NOT_CONTAINS : Operator.CONTAINS, value ) {
            String sValue = value instanceof String ? ( String ) value : value.toString();

            @Override
            public boolean resolve( Map<String, FieldAccess> fields, Object owner ) {

                boolean returnVal;
                FieldAccess field = fields.get( name );
                if ( Typ.implementsInterface( field.type(), Typ.collection ) ) {
                    Collection collection = ( Collection ) field.getValue( owner );
                    returnVal = collection.contains( value );
                } else if ( field.type().isArray() ) {
                    returnVal = false;
                    Object array = ( Object ) field.getValue( owner );
                    Iterator iter = iterator( array );
                    while ( iter.hasNext() ) {
                        Object i = iter.next();
                        if ( i.equals( value ) ) {
                            returnVal = true;
                        }
                    }
                } else {
                    Object itemValue = field.getValue( owner );
                    String sItemvalue = itemValue instanceof String ? ( String ) itemValue : itemValue.toString();
                    returnVal = sItemvalue.contains( sValue );
                }
                return not ? !returnVal : returnVal;
            }
        };
    }

    public static Criterion notEmpty( final Object name ) {
        return doEmpty( name, true );
    }

    public static Criterion empty( final Object name ) {
        return doEmpty( name, false );
    }

    private static Criterion doEmpty( final Object name, final boolean not ) {
        return new Criterion<Object>( name.toString(), not ? Operator.NOT_EMPTY : Operator.IS_EMPTY, "" ) {
            String sValue = value instanceof String ? ( String ) value : value.toString();

            @Override
            public boolean resolve( Map<String, FieldAccess> fields, Object owner ) {

                boolean returnVal;
                FieldAccess field = fields.get( name );
                if ( Typ.implementsInterface( field.type(), Typ.collection ) ) {
                    Collection collection = ( Collection ) field.getValue( owner );
                    returnVal = collection == null || collection.isEmpty();
                } else if ( field.type().isArray() ) {
                    Object array = ( Object ) field.getValue( owner );
                    returnVal = array == null || Boon.len( array ) == 0;
                } else {
                    Object obj = ( Object ) field.getValue( owner );
                    returnVal = obj == null || Boon.len( obj ) == 0;
                }
                return not ? !returnVal : returnVal;
            }
        };
    }

    public static Criterion notNull( final Object name ) {
        return doIsNull( name, true );
    }

    public static Criterion isNull( final Object name ) {
        return doIsNull( name, false );
    }

    private static Criterion doIsNull( final Object name, final boolean not ) {
        return new Criterion<Object>( name.toString(), not ? Operator.NOT_NULL : Operator.IS_NULL, "" ) {
            String sValue = value instanceof String ? ( String ) value : value.toString();

            @Override
            public boolean resolve( Map<String, FieldAccess> fields, Object owner ) {

                FieldAccess field = fields.get( name );
                boolean isNull = field.getValue( owner ) == null;
                return not ? !isNull : isNull;
            }
        };
    }


    //    I am going to addObject date handling, but not now. TODO
//
//    public static Query betweenYears(Object name, int year1, int year2) {
//
//        return new Criterion<Object>(name.toString(), Operator.EQUAL, year1) {
//            @Override
//            public boolean resolve(Map<String, FieldAccess> fields, Object owner) {
//                return set.equals(field.readNestedValue(owner)) ;
//            }
//        };
//    }


    // TODO regex suppot
//    public static Query matches(Object name, Object set) {
//        return new Criterion<Object>(name.toString(), Operator.EQUAL, set) {
//            @Override
//            public boolean resolve(Map<String, FieldAccess> fields, Object owner) {
//                return set.equals(field.readNestedValue(owner)) ;
//            }
//        };
//    }


    //
    //Boolean


    //
    //
    //
    // LONG


    //Char
    //
    //
    //

    //Int Int Int
    //
    //
    //
    public static Criterion eqInt( final Object name, final int compareValue ) {
        return new Criterion.PrimitiveCriterion( name.toString(), Operator.EQUAL, compareValue ) {
            @Override
            public boolean resolve( final Map<String, FieldAccess> fields, final Object owner ) {
                FieldAccess field = fields.get( name );
                int value = field.getInt( owner );
                return value == compareValue;
            }

        };
    }

    public static Criterion notEqInt( final Object name, final int compareValue ) {
        return new Criterion.PrimitiveCriterion( name.toString(), Operator.NOT_EQUAL, compareValue ) {
            @Override
            public boolean resolve( final Map<String, FieldAccess> fields, final Object owner ) {
                FieldAccess field = fields.get( name );
                int value = field.getInt( owner );
                return value != compareValue;
            }
        };
    }

    public static Criterion notInInts( final Object name, final int... compareValues ) {
        return new Criterion.PrimitiveCriterion( name.toString(), Operator.NOT_IN, compareValues ) {
            @Override
            public boolean resolve( final Map<String, FieldAccess> fields, final Object owner ) {
                FieldAccess field = fields.get( name );
                int value = field.getInt( owner );

                for ( int compareValue : compareValues ) {
                    if ( value == compareValue ) {
                        return false;
                    }
                }
                return true;
            }
        };
    }

    public static Criterion inInts( final Object name, final int... compareValues ) {
        return new Criterion.PrimitiveCriterion( name.toString(), Operator.IN, compareValues ) {
            @Override
            public boolean resolve( final Map<String, FieldAccess> fields, final Object owner ) {
                FieldAccess field = fields.get( name );
                int value = field.getInt( owner );

                for ( int compareValue : compareValues ) {
                    if ( value == compareValue ) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    public static Criterion ltInt( final Object name, final int compareValue ) {
        return new Criterion.PrimitiveCriterion( name.toString(), Operator.LESS_THAN, compareValue ) {
            @Override
            public boolean resolve( final Map<String, FieldAccess> fields, final Object owner ) {
                FieldAccess field = fields.get( name );
                int value = field.getInt( owner );
                return value < compareValue;
            }
        };
    }

    public static Criterion lteInt( final Object name, final int compareValue ) {
        return new Criterion.PrimitiveCriterion( name.toString(), Operator.LESS_THAN_EQUAL, compareValue ) {
            @Override
            public boolean resolve( final Map<String, FieldAccess> fields, final Object owner ) {
                FieldAccess field = fields.get( name );
                int value = field.getInt( owner );
                return value <= compareValue;
            }
        };
    }

    public static Criterion gtInt( final Object name, final int compareValue ) {
        return new Criterion.PrimitiveCriterion( name.toString(), Operator.GREATER_THAN, compareValue ) {
            @Override
            public boolean resolve( final Map<String, FieldAccess> fields, final Object owner ) {
                FieldAccess field = fields.get( name );
                int value = field.getInt( owner );
                return value > compareValue;
            }
        };
    }

    public static Criterion gteInt( final Object name, final int compareValue ) {
        return new Criterion.PrimitiveCriterion( name.toString(), Operator.GREATER_THAN_EQUAL, compareValue ) {
            @Override
            public boolean resolve( final Map<String, FieldAccess> fields, final Object owner ) {
                FieldAccess field = fields.get( name );
                int value = field.getInt( owner );
                return value >= compareValue;
            }
        };
    }

    public static Criterion betweenInt( final Object name, final int start, final int stop ) {
        return new Criterion.PrimitiveCriterion( name.toString(), Operator.BETWEEN, start, stop ) {
            @Override
            public boolean resolve( final Map<String, FieldAccess> fields, final Object owner ) {
                FieldAccess field = fields.get( name );
                int value = field.getInt( owner );
                return value >= start && value <= stop;
            }
        };
    }

    //Float
    //
    //
    //
    public static Criterion eqFloat( final Object name, final float compareValue ) {
        return new Criterion.PrimitiveCriterion( name.toString(), Operator.EQUAL, compareValue ) {
            @Override
            public boolean resolve( final Map<String, FieldAccess> fields, final Object owner ) {
                FieldAccess field = fields.get( name );
                float value = field.getFloat( owner );
                return value == compareValue;
            }

        };
    }

    public static Criterion notEqFloat( final Object name, final float compareValue ) {
        return new Criterion.PrimitiveCriterion( name.toString(), Operator.NOT_EQUAL, compareValue ) {
            @Override
            public boolean resolve( final Map<String, FieldAccess> fields, final Object owner ) {
                FieldAccess field = fields.get( name );
                float value = field.getFloat( owner );
                return value != compareValue;
            }
        };
    }

    public static Criterion notInFloats( final Object name, final float... compareValues ) {
        return new Criterion.PrimitiveCriterion( name.toString(), Operator.NOT_IN, compareValues ) {
            @Override
            public boolean resolve( final Map<String, FieldAccess> fields, final Object owner ) {
                FieldAccess field = fields.get( name );
                float value = field.getFloat( owner );

                for ( float compareValue : compareValues ) {
                    if ( value == compareValue ) {
                        return false;
                    }
                }
                return true;
            }
        };
    }

    public static Criterion inFloats( final Object name, final float... compareValues ) {
        return new Criterion.PrimitiveCriterion( name.toString(), Operator.IN, compareValues ) {
            @Override
            public boolean resolve( final Map<String, FieldAccess> fields, final Object owner ) {
                FieldAccess field = fields.get( name );
                float value = field.getFloat( owner );

                for ( float compareValue : compareValues ) {
                    if ( value == compareValue ) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    public static Criterion ltFloat( final Object name, final float compareValue ) {
        return new Criterion.PrimitiveCriterion( name.toString(), Operator.LESS_THAN, compareValue ) {
            @Override
            public boolean resolve( final Map<String, FieldAccess> fields, final Object owner ) {
                FieldAccess field = fields.get( name );
                float value = field.getFloat( owner );
                return value < compareValue;
            }
        };
    }

    public static Criterion lteFloat( final Object name, final float compareValue ) {
        return new Criterion.PrimitiveCriterion( name.toString(), Operator.LESS_THAN_EQUAL, compareValue ) {
            @Override
            public boolean resolve( final Map<String, FieldAccess> fields, final Object owner ) {
                FieldAccess field = fields.get( name );
                float value = field.getFloat( owner );
                return value <= compareValue;
            }
        };
    }

    public static Criterion gtFloat( final Object name, final float compareValue ) {
        return new Criterion.PrimitiveCriterion( name.toString(), Operator.GREATER_THAN, compareValue ) {
            @Override
            public boolean resolve( final Map<String, FieldAccess> fields, final Object owner ) {
                FieldAccess field = fields.get( name );
                float value = field.getFloat( owner );
                return value > compareValue;
            }
        };
    }

    public static Criterion gteFloat( final Object name, final float compareValue ) {
        return new Criterion.PrimitiveCriterion( name.toString(), Operator.GREATER_THAN_EQUAL, compareValue ) {
            @Override
            public boolean resolve( final Map<String, FieldAccess> fields, final Object owner ) {
                FieldAccess field = fields.get( name );
                float value = field.getFloat( owner );
                return value >= compareValue;
            }
        };
    }

    public static Criterion betweenFloat( final Object name, final float start, final float stop ) {
        return new Criterion.PrimitiveCriterion( name.toString(), Operator.BETWEEN, start, stop ) {
            @Override
            public boolean resolve( final Map<String, FieldAccess> fields, final Object owner ) {
                FieldAccess field = fields.get( name );
                float value = field.getFloat( owner );
                return value >= start && value <= stop;
            }
        };
    }

    public static Criterion isTrue( final Object name ) {
        return eqBoolean( name, true );
    }

    public static Criterion isFalse( final Object name ) {
        return eqBoolean( name, false );
    }

    public static Criterion eqBoolean( final Object name, final boolean compareValue ) {
        return new Criterion.PrimitiveCriterion( name.toString(), Operator.EQUAL, compareValue ) {
            @Override
            public boolean resolve( final Map<String, FieldAccess> fields, final Object owner ) {
                FieldAccess field = fields.get( name );
                boolean value = field.getBoolean( owner );
                return value == compareValue;
            }

        };
    }

    public static Criterion notEqBoolean( final Object name, final boolean compareValue ) {
        return new Criterion.PrimitiveCriterion( name.toString(), Operator.NOT_EQUAL, compareValue ) {
            @Override
            public boolean resolve( final Map<String, FieldAccess> fields, final Object owner ) {
                FieldAccess field = fields.get( name );
                boolean value = field.getBoolean( owner );
                return value != compareValue;
            }
        };
    }

    //Double
    //
    //
    //
    public static Criterion eqDouble( final Object name, final double compareValue ) {
        return new Criterion.PrimitiveCriterion( name.toString(), Operator.EQUAL, compareValue ) {
            @Override
            public boolean resolve( final Map<String, FieldAccess> fields, final Object owner ) {
                FieldAccess field = fields.get( name );
                double value = field.getDouble( owner );
                return value == compareValue;
            }

        };
    }

    public static Criterion notEqDouble( final Object name, final double compareValue ) {
        return new Criterion.PrimitiveCriterion( name.toString(), Operator.NOT_EQUAL, compareValue ) {
            @Override
            public boolean resolve( final Map<String, FieldAccess> fields, final Object owner ) {
                FieldAccess field = fields.get( name );
                double value = field.getDouble( owner );
                return value != compareValue;
            }
        };
    }

    public static Criterion notInDoubles( final Object name, final double... compareValues ) {
        return new Criterion.PrimitiveCriterion( name.toString(), Operator.NOT_IN, compareValues ) {
            @Override
            public boolean resolve( final Map<String, FieldAccess> fields, final Object owner ) {
                FieldAccess field = fields.get( name );
                double value = field.getDouble( owner );

                for ( double compareValue : compareValues ) {
                    if ( value == compareValue ) {
                        return false;
                    }
                }
                return true;
            }
        };
    }

    public static Criterion inDoubles( final Object name, final double... compareValues ) {
        return new Criterion.PrimitiveCriterion( name.toString(), Operator.IN, compareValues ) {
            @Override
            public boolean resolve( final Map<String, FieldAccess> fields, final Object owner ) {
                FieldAccess field = fields.get( name );
                double value = field.getDouble( owner );

                for ( double compareValue : compareValues ) {
                    if ( value == compareValue ) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    public static Criterion ltDouble( final Object name, final double compareValue ) {
        return new Criterion.PrimitiveCriterion( name.toString(), Operator.LESS_THAN, compareValue ) {
            @Override
            public boolean resolve( final Map<String, FieldAccess> fields, final Object owner ) {
                FieldAccess field = fields.get( name );
                double value = field.getDouble( owner );
                return value < compareValue;
            }
        };
    }

    public static Criterion lteDouble( final Object name, final double compareValue ) {
        return new Criterion.PrimitiveCriterion( name.toString(), Operator.LESS_THAN_EQUAL, compareValue ) {
            @Override
            public boolean resolve( final Map<String, FieldAccess> fields, final Object owner ) {
                FieldAccess field = fields.get( name );
                double value = field.getDouble( owner );
                return value <= compareValue;
            }
        };
    }

    public static Criterion gtDouble( final Object name, final double compareValue ) {
        return new Criterion.PrimitiveCriterion( name.toString(), Operator.GREATER_THAN, compareValue ) {
            @Override
            public boolean resolve( final Map<String, FieldAccess> fields, final Object owner ) {
                FieldAccess field = fields.get( name );
                double value = field.getDouble( owner );
                return value > compareValue;
            }
        };
    }

    public static Criterion gteDouble( final Object name, final double compareValue ) {
        return new Criterion.PrimitiveCriterion( name.toString(), Operator.GREATER_THAN_EQUAL, compareValue ) {
            @Override
            public boolean resolve( final Map<String, FieldAccess> fields, final Object owner ) {
                FieldAccess field = fields.get( name );
                double value = field.getDouble( owner );
                return value >= compareValue;
            }
        };
    }

    public static Criterion betweenDouble( final Object name, final double start, final double stop ) {
        return new Criterion.PrimitiveCriterion( name.toString(), Operator.BETWEEN, start, stop ) {
            @Override
            public boolean resolve( final Map<String, FieldAccess> fields, final Object owner ) {
                FieldAccess field = fields.get( name );
                double value = field.getDouble( owner );
                return value >= start && value <= stop;
            }
        };
    }

    //Short
    //
    //
    //
    public static Criterion eqShort( final Object name, final short compareValue ) {
        return new Criterion.PrimitiveCriterion( name.toString(), Operator.EQUAL, compareValue ) {
            @Override
            public boolean resolve( final Map<String, FieldAccess> fields, final Object owner ) {
                FieldAccess field = fields.get( name );
                short value = field.getShort( owner );
                return value == compareValue;
            }

        };
    }

    public static Criterion notEqShort( final Object name, final short compareValue ) {
        return new Criterion.PrimitiveCriterion( name.toString(), Operator.NOT_EQUAL, compareValue ) {
            @Override
            public boolean resolve( final Map<String, FieldAccess> fields, final Object owner ) {
                FieldAccess field = fields.get( name );
                short value = field.getShort( owner );
                return value != compareValue;
            }
        };
    }

    public static Criterion notInShorts( final Object name, final short... compareValues ) {
        return new Criterion.PrimitiveCriterion( name.toString(), Operator.NOT_IN, compareValues ) {
            @Override
            public boolean resolve( final Map<String, FieldAccess> fields, final Object owner ) {
                FieldAccess field = fields.get( name );
                short value = field.getShort( owner );

                for ( short compareValue : compareValues ) {
                    if ( value == compareValue ) {
                        return false;
                    }
                }
                return true;
            }
        };
    }

    public static Criterion inShorts( final Object name, final short... compareValues ) {
        return new Criterion.PrimitiveCriterion( name.toString(), Operator.IN, compareValues ) {
            @Override
            public boolean resolve( final Map<String, FieldAccess> fields, final Object owner ) {
                FieldAccess field = fields.get( name );
                short value = field.getShort( owner );

                for ( short compareValue : compareValues ) {
                    if ( value == compareValue ) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    public static Criterion ltShort( final Object name, final short compareValue ) {
        return new Criterion.PrimitiveCriterion( name.toString(), Operator.LESS_THAN, compareValue ) {
            @Override
            public boolean resolve( final Map<String, FieldAccess> fields, final Object owner ) {
                FieldAccess field = fields.get( name );
                short value = field.getShort( owner );
                return value < compareValue;
            }
        };
    }

    public static Criterion lteShort( final Object name, final short compareValue ) {
        return new Criterion.PrimitiveCriterion( name.toString(), Operator.LESS_THAN_EQUAL, compareValue ) {
            @Override
            public boolean resolve( final Map<String, FieldAccess> fields, final Object owner ) {
                FieldAccess field = fields.get( name );
                short value = field.getShort( owner );
                return value <= compareValue;
            }
        };
    }

    public static Criterion gtShort( final Object name, final short compareValue ) {
        return new Criterion.PrimitiveCriterion( name.toString(), Operator.GREATER_THAN, compareValue ) {
            @Override
            public boolean resolve( final Map<String, FieldAccess> fields, final Object owner ) {
                FieldAccess field = fields.get( name );
                short value = field.getShort( owner );
                return value > compareValue;
            }
        };
    }

    public static Criterion gteShort( final Object name, final short compareValue ) {
        return new Criterion.PrimitiveCriterion( name.toString(), Operator.GREATER_THAN_EQUAL, compareValue ) {
            @Override
            public boolean resolve( final Map<String, FieldAccess> fields, final Object owner ) {
                FieldAccess field = fields.get( name );
                short value = field.getShort( owner );
                return value >= compareValue;
            }
        };
    }

    public static Criterion betweenShort( final Object name, final short start, final short stop ) {
        return new Criterion.PrimitiveCriterion( name.toString(), Operator.BETWEEN, start, stop ) {
            @Override
            public boolean resolve( final Map<String, FieldAccess> fields, final Object owner ) {
                FieldAccess field = fields.get( name );
                short value = field.getShort( owner );
                return value >= start && value <= stop;
            }
        };
    }

    //Byte
    //
    //
    //
    public static Criterion eqByte( final Object name, final byte compareValue ) {
        return new Criterion.PrimitiveCriterion( name.toString(), Operator.EQUAL, compareValue ) {
            @Override
            public boolean resolve( final Map<String, FieldAccess> fields, final Object owner ) {
                FieldAccess field = fields.get( name );
                byte value = field.getByte( owner );
                return value == compareValue;
            }

        };
    }

    public static Criterion notEqByte( final Object name, final byte compareValue ) {
        return new Criterion.PrimitiveCriterion( name.toString(), Operator.NOT_EQUAL, compareValue ) {
            @Override
            public boolean resolve( final Map<String, FieldAccess> fields, final Object owner ) {
                FieldAccess field = fields.get( name );
                byte value = field.getByte( owner );
                return value != compareValue;
            }
        };
    }

    public static Criterion notInBytes( final Object name, final byte... compareValues ) {
        return new Criterion.PrimitiveCriterion( name.toString(), Operator.NOT_IN, compareValues ) {
            @Override
            public boolean resolve( final Map<String, FieldAccess> fields, final Object owner ) {
                FieldAccess field = fields.get( name );
                byte value = field.getByte( owner );

                for ( byte compareValue : compareValues ) {
                    if ( value == compareValue ) {
                        return false;
                    }
                }
                return true;
            }
        };
    }

    public static Criterion inBytes( final Object name, final byte... compareValues ) {
        return new Criterion.PrimitiveCriterion( name.toString(), Operator.IN, compareValues ) {
            @Override
            public boolean resolve( final Map<String, FieldAccess> fields, final Object owner ) {
                FieldAccess field = fields.get( name );
                byte value = field.getByte( owner );

                for ( byte compareValue : compareValues ) {
                    if ( value == compareValue ) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    public static Criterion ltByte( final Object name, final byte compareValue ) {
        return new Criterion.PrimitiveCriterion( name.toString(), Operator.LESS_THAN, compareValue ) {
            @Override
            public boolean resolve( final Map<String, FieldAccess> fields, final Object owner ) {
                FieldAccess field = fields.get( name );
                byte value = field.getByte( owner );
                return value < compareValue;
            }
        };
    }

    public static Criterion lteByte( final Object name, final byte compareValue ) {
        return new Criterion.PrimitiveCriterion( name.toString(), Operator.LESS_THAN_EQUAL, compareValue ) {
            @Override
            public boolean resolve( final Map<String, FieldAccess> fields, final Object owner ) {
                FieldAccess field = fields.get( name );
                byte value = field.getByte( owner );
                return value <= compareValue;
            }
        };
    }

    public static Criterion gtByte( final Object name, final byte compareValue ) {
        return new Criterion.PrimitiveCriterion( name.toString(), Operator.GREATER_THAN, compareValue ) {
            @Override
            public boolean resolve( final Map<String, FieldAccess> fields, final Object owner ) {
                FieldAccess field = fields.get( name );
                byte value = field.getByte( owner );
                return value > compareValue;
            }
        };
    }

    public static Criterion gteByte( final Object name, final byte compareValue ) {
        return new Criterion.PrimitiveCriterion( name.toString(), Operator.GREATER_THAN_EQUAL, compareValue ) {
            @Override
            public boolean resolve( final Map<String, FieldAccess> fields, final Object owner ) {
                FieldAccess field = fields.get( name );
                byte value = field.getByte( owner );
                return value >= compareValue;
            }
        };
    }

    public static Criterion betweenByte( final Object name, final byte start, final byte stop ) {
        return new Criterion.PrimitiveCriterion( name.toString(), Operator.BETWEEN, start, stop ) {
            @Override
            public boolean resolve( final Map<String, FieldAccess> fields, final Object owner ) {
                FieldAccess field = fields.get( name );
                byte value = field.getByte( owner );
                return value >= start && value <= stop;
            }
        };
    }

    //Long
    //
    //
    //
    public static Criterion eqLong( final Object name, final long compareValue ) {
        return new Criterion.PrimitiveCriterion( name.toString(), Operator.EQUAL, compareValue ) {
            @Override
            public boolean resolve( final Map<String, FieldAccess> fields, final Object owner ) {
                FieldAccess field = fields.get( name );
                long value = field.getLong( owner );
                return value == compareValue;
            }

        };
    }

    public static Criterion notEqLong( final Object name, final long compareValue ) {
        return new Criterion.PrimitiveCriterion( name.toString(), Operator.NOT_EQUAL, compareValue ) {
            @Override
            public boolean resolve( final Map<String, FieldAccess> fields, final Object owner ) {
                FieldAccess field = fields.get( name );
                long value = field.getLong( owner );
                return value != compareValue;
            }
        };
    }

    public static Criterion notInLongs( final Object name, final long... compareValues ) {
        return new Criterion.PrimitiveCriterion( name.toString(), Operator.NOT_IN, compareValues ) {
            @Override
            public boolean resolve( final Map<String, FieldAccess> fields, final Object owner ) {
                FieldAccess field = fields.get( name );
                long value = field.getLong( owner );

                for ( long compareValue : compareValues ) {
                    if ( value == compareValue ) {
                        return false;
                    }
                }
                return true;
            }
        };
    }

    public static Criterion inLongs( final Object name, final long... compareValues ) {
        return new Criterion.PrimitiveCriterion( name.toString(), Operator.IN, compareValues ) {
            @Override
            public boolean resolve( final Map<String, FieldAccess> fields, final Object owner ) {
                FieldAccess field = fields.get( name );
                long value = field.getLong( owner );

                for ( long compareValue : compareValues ) {
                    if ( value == compareValue ) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

//
//    public static Criterion notInLongs( final Object name, final long... compareValues ) {
//        return new Criterion.PrimitiveCriterion( name.toString(), Operator.NOT_IN, compareValues ) {
//            @Override
//            public boolean resolve( final Map<String, FieldAccess> fields, final Object owner ) {
//                FieldAccess field = fields.get( name );
//                byte value = field.getLong( owner );
//
//                for ( long compareValue : compareValues ) {
//                    if ( value == compareValue ) {
//                        return false;
//                    }
//                }
//                return true;
//            }
//        };
//    }


    public static Criterion ltLong( final Object name, final long compareValue ) {
        return new Criterion.PrimitiveCriterion( name.toString(), Operator.LESS_THAN, compareValue ) {
            @Override
            public boolean resolve( final Map<String, FieldAccess> fields, final Object owner ) {
                FieldAccess field = fields.get( name );
                long value = field.getLong( owner );
                return value < compareValue;
            }
        };
    }

    public static Criterion lteLong( final Object name, final long compareValue ) {
        return new Criterion.PrimitiveCriterion( name.toString(), Operator.LESS_THAN_EQUAL, compareValue ) {
            @Override
            public boolean resolve( final Map<String, FieldAccess> fields, final Object owner ) {
                FieldAccess field = fields.get( name );
                long value = field.getLong( owner );
                return value <= compareValue;
            }
        };
    }

    public static Criterion gtLong( final Object name, final long compareValue ) {
        return new Criterion.PrimitiveCriterion( name.toString(), Operator.GREATER_THAN, compareValue ) {
            @Override
            public boolean resolve( final Map<String, FieldAccess> fields, final Object owner ) {
                FieldAccess field = fields.get( name );
                long value = field.getLong( owner );
                return value > compareValue;
            }
        };
    }

    public static Criterion gteLong( final Object name, final long compareValue ) {
        return new Criterion.PrimitiveCriterion( name.toString(), Operator.GREATER_THAN_EQUAL, compareValue ) {
            @Override
            public boolean resolve( final Map<String, FieldAccess> fields, final Object owner ) {
                FieldAccess field = fields.get( name );
                long value = field.getLong( owner );
                return value >= compareValue;
            }
        };
    }

    public static Criterion betweenLong( final Object name, final long start, final long stop ) {
        return new Criterion.PrimitiveCriterion( name.toString(), Operator.BETWEEN, start, stop ) {
            @Override
            public boolean resolve( final Map<String, FieldAccess> fields, final Object owner ) {
                FieldAccess field = fields.get( name );
                long value = field.getLong( owner );
                return value >= start && value <= stop;
            }
        };
    }

    //
    //
    //
    //
    public static Criterion eqChar( final Object name, final char compareValue ) {
        return new Criterion.PrimitiveCriterion( name.toString(), Operator.EQUAL, compareValue ) {
            @Override
            public boolean resolve( final Map<String, FieldAccess> fields, final Object owner ) {
                FieldAccess field = fields.get( name );
                char value = field.getChar( owner );
                return value == compareValue;
            }

        };
    }

    public static Criterion notEqChar( final Object name, final char compareValue ) {
        return new Criterion.PrimitiveCriterion( name.toString(), Operator.NOT_EQUAL, compareValue ) {
            @Override
            public boolean resolve( final Map<String, FieldAccess> fields, final Object owner ) {
                FieldAccess field = fields.get( name );
                char value = field.getChar( owner );
                return value != compareValue;
            }
        };
    }

    public static Criterion notInChars( final Object name, final char... compareValues ) {
        return new Criterion.PrimitiveCriterion( name.toString(), Operator.NOT_IN, compareValues ) {
            @Override
            public boolean resolve( final Map<String, FieldAccess> fields, final Object owner ) {
                FieldAccess field = fields.get( name );
                char value = field.getChar( owner );

                for ( char compareValue : compareValues ) {
                    if ( value == compareValue ) {
                        return false;
                    }
                }
                return true;
            }
        };
    }

    public static Criterion inChars( final Object name, final char... compareValues ) {
        return new Criterion.PrimitiveCriterion( name.toString(), Operator.IN, compareValues ) {
            @Override
            public boolean resolve( final Map<String, FieldAccess> fields, final Object owner ) {
                FieldAccess field = fields.get( name );
                char value = field.getChar( owner );

                for ( char compareValue : compareValues ) {
                    if ( value == compareValue ) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    public static Criterion ltChar( final Object name, final char compareValue ) {
        return new Criterion.PrimitiveCriterion( name.toString(), Operator.LESS_THAN, compareValue ) {
            @Override
            public boolean resolve( final Map<String, FieldAccess> fields, final Object owner ) {
                FieldAccess field = fields.get( name );
                char value = field.getChar( owner );
                return value < compareValue;
            }
        };
    }

    public static Criterion lteChar( final Object name, final char compareValue ) {
        return new Criterion.PrimitiveCriterion( name.toString(), Operator.LESS_THAN_EQUAL, compareValue ) {
            @Override
            public boolean resolve( final Map<String, FieldAccess> fields, final Object owner ) {
                FieldAccess field = fields.get( name );
                char value = field.getChar( owner );
                return value <= compareValue;
            }
        };
    }

    public static Criterion gtChar( final Object name, final char compareValue ) {
        return new Criterion.PrimitiveCriterion( name.toString(), Operator.GREATER_THAN, compareValue ) {
            @Override
            public boolean resolve( final Map<String, FieldAccess> fields, final Object owner ) {
                FieldAccess field = fields.get( name );
                char value = field.getChar( owner );
                return value > compareValue;
            }
        };
    }

    public static Criterion gteChar( final Object name, final char compareValue ) {
        return new Criterion.PrimitiveCriterion( name.toString(), Operator.GREATER_THAN_EQUAL, compareValue ) {
            @Override
            public boolean resolve( final Map<String, FieldAccess> fields, final Object owner ) {
                FieldAccess field = fields.get( name );
                char value = field.getChar( owner );
                return value >= compareValue;
            }
        };
    }

    public static Criterion betweenChar( final Object name, final char start, final char stop ) {
        return new Criterion.PrimitiveCriterion( name.toString(), Operator.BETWEEN, start, stop ) {
            @Override
            public boolean resolve( final Map<String, FieldAccess> fields, final Object owner ) {
                FieldAccess field = fields.get( name );
                char value = field.getChar( owner );
                return value >= start && value <= stop;
            }
        };
    }


    /**
     * Make criteria configurable
     * @param operator
     * @param type
     * @param values
     * @return
     */
    public static Criteria createCriteria(String name, Operator operator, Type type, List<?> values) {

        okOrDie("Values must be passed", values);

        Object value = values.get(0);

        switch (operator) {
            case EQUAL:
                switch (type){
                    case CHAR:
                        return eqChar(name, Conversions.toChar(value));

                    case BYTE:
                        return eqByte(name, Conversions.toByte(value));
                    case BOOLEAN:
                        return eqBoolean(name, Conversions.toBoolean(value));
                    case INT:
                        return eqInt(name, Conversions.toInt(value));
                    case FLOAT:
                        return eqFloat(name, Conversions.toFloat(value));
                    case SHORT:
                        return eqShort(name, Conversions.toShort(value));
                    case DOUBLE:
                        return eqDouble(name, Conversions.toDouble(value));
                    case LONG:
                        return eqLong(name, Conversions.toLong(value));

                    default:
                        return eq(name, value);
                }
            case NOT_EQUAL:
                switch (type){
                    case CHAR:
                        return notEqChar(name, Conversions.toChar(value));
                    case BYTE:
                        return notEqByte(name, Conversions.toByte(value));
                    case BOOLEAN:
                        return notEqBoolean(name, Conversions.toBoolean(value));
                    case INT:
                        return notEqInt(name, Conversions.toInt(value));
                    case FLOAT:
                        return notEqFloat(name, Conversions.toFloat(value));
                    case SHORT:
                        return notEqShort(name, Conversions.toShort(value));
                    case DOUBLE:
                        return notEqDouble(name, Conversions.toDouble(value));
                    case LONG:
                        return notEqLong(name, Conversions.toLong(value));

                    default:
                        return notEq(name, value);
                }
            case GREATER_THAN:
                switch (type){
                    case CHAR:
                        return gtChar(name, Conversions.toChar(value));
                    case BYTE:
                        return gtByte(name, Conversions.toByte(value));
                    case INT:
                        return gtInt(name, Conversions.toInt(value));
                    case FLOAT:
                        return gtFloat(name, Conversions.toFloat(value));
                    case SHORT:
                        return gtShort(name, Conversions.toShort(value));
                    case DOUBLE:
                        return gtDouble(name, Conversions.toDouble(value));
                    case LONG:
                        return gtLong(name, Conversions.toLong(value));

                    default:
                        return gt(name, value);
                }
            case LESS_THAN:
                switch (type){
                    case CHAR:
                        return ltChar(name, Conversions.toChar(value));
                    case BYTE:
                        return ltByte(name, Conversions.toByte(value));
                    case INT:
                        return ltInt(name, Conversions.toInt(value));
                    case FLOAT:
                        return ltFloat(name, Conversions.toFloat(value));
                    case SHORT:
                        return ltShort(name, Conversions.toShort(value));
                    case DOUBLE:
                        return ltDouble(name, Conversions.toDouble(value));
                    case LONG:
                        return ltLong(name, Conversions.toLong(value));

                    default:
                        return lt(name, value);
                }
            case GREATER_THAN_EQUAL:
                switch (type){
                    case CHAR:
                        return gteChar(name, Conversions.toChar(value));
                    case BYTE:
                        return gteByte(name, Conversions.toByte(value));
                    case INT:
                        return gteInt(name, Conversions.toInt(value));
                    case FLOAT:
                        return gteFloat(name, Conversions.toFloat(value));
                    case SHORT:
                        return gteShort(name, Conversions.toShort(value));
                    case DOUBLE:
                        return gteDouble(name, Conversions.toDouble(value));
                    case LONG:
                        return gteLong(name, Conversions.toLong(value));

                    default:
                        return gte(name, value);
                }
            case LESS_THAN_EQUAL:
                switch (type){
                    case CHAR:
                        return lteChar(name, Conversions.toChar(value));
                    case BYTE:
                        return lteByte(name, Conversions.toByte(value));
                    case INT:
                        return lteInt(name, Conversions.toInt(value));
                    case FLOAT:
                        return lteFloat(name, Conversions.toFloat(value));
                    case SHORT:
                        return lteShort(name, Conversions.toShort(value));
                    case DOUBLE:
                        return lteDouble(name, Conversions.toDouble(value));
                    case LONG:
                        return lteLong(name, Conversions.toLong(value));

                    default:
                        return lte(name, value);
                }

            case BETWEEN:

                okOrDie("Values must be at least 2 in size", values.size()>1);
                Object value2 = values.get(1);
                switch (type){
                    case CHAR:
                        return betweenChar(name, Conversions.toChar(value),
                                Conversions.toChar(value2));
                    case BYTE:
                        return betweenByte(name, Conversions.toByte(value),
                                Conversions.toByte(value2));
                    case INT:
                        return betweenInt(name, Conversions.toInt(value),
                                Conversions.toInt(value2));
                    case FLOAT:
                        return betweenFloat(name, Conversions.toFloat(value),
                                Conversions.toFloat(value2));
                    case SHORT:
                        return betweenShort(name, Conversions.toShort(value),
                                Conversions.toShort(value2));
                    case DOUBLE:
                        return betweenDouble(name, Conversions.toDouble(value),
                                Conversions.toDouble(value2));
                    case LONG:
                        return betweenLong(name, Conversions.toLong(value),
                                Conversions.toLong(value2));

                    default:
                        return between(name, value, value2);
                }
            case IN:

                switch (type){
                    case CHAR:
                        return inChars(name, Conversions.carray(values));
                    case BYTE:
                        return inBytes(name, Conversions.barray(values));
                    case INT:
                        return inInts(name, Conversions.iarray(values));
                    case FLOAT:
                        return inFloats(name, Conversions.farray(values));
                    case SHORT:
                        return inShorts(name, Conversions.sarray(values));
                    case DOUBLE:
                        return inDoubles(name, Conversions.darray(values));
                    case LONG:
                        return inLongs(name, Conversions.larray(values));

                    default:
                        return in(name, Conversions.toArray(Object.class, (List<Object>)values));
                }
            case NOT_IN:

                switch (type){
                    case CHAR:
                        return notInChars(name, Conversions.carray(values));
                    case BYTE:
                        return notInBytes(name, Conversions.barray(values));
                    case INT:
                        return notInInts(name, Conversions.iarray(values));
                    case FLOAT:
                        return notInFloats(name, Conversions.farray(values));
                    case SHORT:
                        return notInShorts(name, Conversions.sarray(values));
                    case DOUBLE:
                        return notInDoubles(name, Conversions.darray(values));
                    case LONG:
                        return notInLongs(name, Conversions.larray(values));

                    default:
                        return notIn(name, Conversions.toArray(Object.class, (List<Object>) values));
                }

            case CONTAINS:
                return contains(name, value);

            case NOT_CONTAINS:
                return notContains(name, value);


            case STARTS_WITH:
                return startsWith(name, value);

            case ENDS_WITH:
                return endsWith(name, value);

            case NOT_EMPTY:
                return notEmpty(name);

            case IS_EMPTY:
                return empty(name);



        }



        return die(Criteria.class, "Not Found", name, operator, type, values);
    }


    /**
     * Make criteria configurable
     * @param operator
     * @param values
     * @return
     */
    public static Criteria createCriteriaFromClass(String name, Class<?> cls, Operator operator,  List<?> values) {

        if (operator == Operator.AND) {
            return new Group.And(cls, values);
        } else if (operator == Operator.OR) {

            return new Group.Or(cls, values);
        } else {
            FieldAccess fieldAccess = BeanUtils.idxField(cls, name);
            return createCriteria(name, operator, fieldAccess.typeEnum(), values);
        }
    }


    public static Criteria criteriaFromList(List<?> list) {

        List<Object> args = new ArrayList(list);

        Object o = atIndex(args, -1);
        if (! (o instanceof List) ) {
            atIndex(args, -1, Collections.singletonList(o));
        }


        return (Criteria) Invoker.invokeFromList(ObjectFilter.class,
                "createCriteriaFromClass", args);

    }

    public static Criteria criteriaFromJson(String json) {

        return (Criteria) Invoker.invokeFromObject(ObjectFilter.class,
                "createCriteriaFromClass", fromJson(json));

    }

}
