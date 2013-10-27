package org.boon.criteria;


import org.boon.core.Typ;
import org.boon.core.reflection.Conversions;
import org.boon.core.reflection.Reflection;
import org.boon.core.reflection.fields.FieldAccess;

import java.util.*;

import static org.boon.core.reflection.Reflection.iterator;

public class CriteriaFactory {
    public static Not not(Criteria expression) {
        return new Not(expression);
    }

    public static Group and(Criteria... expressions) {
        return new Group.And(expressions);
    }

    public static Group or(Criteria... expressions) {
        return new Group.Or(expressions);
    }

    public static Criterion eqNested(final Object value, final Object... path) {
        return new Criterion<Object>(Reflection.joinBy('.', path), Operator.EQUAL, value) {
            @Override
            public boolean resolve(Map<String, FieldAccess> fields, Object owner) {
                Object v = Reflection.getPropByPath(path);
                if (v instanceof List) {
                    List list = (List) v;
                    for (Object i : list) {
                        if (i.equals(value)) {
                            return false;
                        }
                    }
                    return true;
                } else {
                    return value.equals(v);
                }

            }

        };
    }

    public static Criterion eq(final Object name, final Object value) {
        return new Criterion<Object>(name.toString(), Operator.EQUAL, value) {
            @Override
            public boolean resolve(Map<String, FieldAccess> fields, Object owner) {
                FieldAccess field = fields.get(name);
                return value.equals(field.getValue(owner));
            }
        };
    }

    public static Criterion typeOf(final String className) {
        return new Criterion<Object>("_type", Operator.EQUAL, className) {
            @Override
            public boolean resolve(Map<String, FieldAccess> fields, Object owner) {

                Class cls = owner.getClass();
                while (cls != Typ.object) {
                    String simpleName = cls.getSimpleName();
                    String name = cls.getName();
                    if (simpleName.equals(className) || name.equals(className)) {
                        return true;
                    }


                    Class[] interfaces = cls.getInterfaces();
                    for (Class<?> i : interfaces) {
                        simpleName = i.getSimpleName();
                        name = i.getName();

                        if (simpleName.equals(className) || name.equals(className)) {
                            return true;
                        }
                    }
                    cls = cls.getSuperclass();
                }
                return false;
            }
        };
    }

    public static Criterion instanceOf(final Class<?> cls) {
        return new Criterion<Object>("_type", Operator.EQUAL, cls.getName()) {
            @Override
            public boolean resolve(Map<String, FieldAccess> fields, Object owner) {
                return Conversions.isSuperClass(owner.getClass(), cls);
            }
        };
    }

    public static Criterion implementsInterface(final Class<?> cls) {
        return new Criterion<Object>("_type", Operator.EQUAL, cls.getName()) {
            @Override
            public boolean resolve(Map<String, FieldAccess> fields, Object owner) {
                return Conversions.implementsInterface(owner.getClass(), cls);
            }
        };
    }

    public static Criterion notEq(final Object name, final Object value) {
        return new Criterion<Object>(name.toString(), Operator.NOT_EQUAL, value) {
            @Override
            public boolean resolve(Map<String, FieldAccess> fields, Object owner) {
                FieldAccess field = fields.get(name);

                return !value.equals(field.getValue(owner));
            }
        };
    }

    public static Criterion notIn(final Object name, final Object... values) {
        return new Criterion<Object>(name.toString(), Operator.NOT_IN, values) {

            HashSet set = new HashSet<>();

            {
                for (Object value : values) {
                    set.add(value);
                }
            }

            @Override
            public boolean resolve(Map<String, FieldAccess> fields, Object owner) {
                FieldAccess field = fields.get(name);

                Object value = field.getValue(owner);
                if (value == null) {
                    return false;
                }
                return !set.contains(value);
            }
        };
    }

    public static Criterion in(final Object name, final Object... values) {
        return new Criterion<Object>(name.toString(), Operator.IN, values) {
            HashSet set = new HashSet<>();

            {
                for (Object value : values) {
                    set.add(value);
                }
            }

            @Override
            public boolean resolve(Map<String, FieldAccess> fields, Object owner) {
                FieldAccess field = fields.get(name);

                return set.contains(field.getValue(owner));
            }
        };
    }

    public static Criterion lt(final Object name, final Object value) {
        return new Criterion<Object>(name.toString(), Operator.LESS_THAN, value) {
            @Override
            public boolean resolve(Map<String, FieldAccess> fields, Object owner) {
                FieldAccess field = fields.get(name);

                return ((Comparable) value).compareTo(field.getValue(owner)) > 0;
            }
        };
    }

    public static Criterion lte(final Object name, final Object value) {
        return new Criterion<Object>(name.toString(), Operator.LESS_THAN_EQUAL, value) {
            @Override
            public boolean resolve(Map<String, FieldAccess> fields, Object owner) {
                FieldAccess field = fields.get(name);

                return ((Comparable) value).compareTo(field.getValue(owner)) >= 0;

            }
        };
    }

    public static Criterion gt(final Object name, final Object value) {
        return new Criterion<Object>(name.toString(), Operator.GREATER_THAN, value) {
            @Override
            public boolean resolve(Map<String, FieldAccess> fields, Object owner) {
                FieldAccess field = fields.get(name);

                return ((Comparable) value).compareTo(field.getValue(owner)) < 0;
            }
        };
    }

    public static Criterion gte(final Object name, final Object value) {
        return new Criterion<Object>(name.toString(), Operator.GREATER_THAN_EQUAL, value) {
            @Override
            public boolean resolve(Map<String, FieldAccess> fields, Object owner) {
                FieldAccess field = fields.get(name);

                return ((Comparable) value).compareTo(field.getValue(owner)) <= 0;
            }
        };
    }

    public static Criterion between(final Object name, final Object value, final Object value2) {
        return new Criterion<Object>(name.toString(), Operator.BETWEEN, value, value2) {
            @Override
            public boolean resolve(Map<String, FieldAccess> fields, Object owner) {
                FieldAccess field = fields.get(name);

                return ((Comparable) value).compareTo(field.getValue(owner)) <= 0 &&
                        ((Comparable) value2).compareTo(field.getValue(owner)) >= 0;
            }
        };
    }

    public static Criterion between(Class clazz, Object name, final String svalue, final String svalue2) {
        Criterion c = between(name, svalue, svalue2);
        c.init(clazz);
        return c;
    }

    public static Criterion between(final Object name, final String svalue, final String svalue2) {
        return new Criterion<Object>(name.toString(), Operator.BETWEEN, svalue, svalue2) {
            @Override
            public boolean resolve(Map<String, FieldAccess> fields, Object owner) {
                FieldAccess field = fields.get(name);

                return ((Comparable) value).compareTo(field.getValue(owner)) <= 0 &&
                        ((Comparable) values[1]).compareTo(field.getValue(owner)) >= 0;
            }
        };
    }

    public static Criterion gt(final Object name, String svalue) {
        return new Criterion<Object>(name.toString(), Operator.GREATER_THAN, svalue) {
            @Override
            public boolean resolve(Map<String, FieldAccess> fields, Object owner) {
                FieldAccess field = fields.get(name);

                Object fieldValue = field.getValue(owner);

                return ((Comparable) value).compareTo(fieldValue) < 0;
            }
        };
    }

    public static Criterion startsWith(final Object name, final Object value) {
        return new Criterion<Object>(name.toString(), Operator.STARTS_WITH, value) {
            String sValue = value instanceof String ? (String) value : value.toString();

            @Override
            public boolean resolve(Map<String, FieldAccess> fields, Object owner) {
                FieldAccess field = fields.get(name);
                Object itemValue = field.getValue(owner);
                String sItemvalue = itemValue instanceof String ? (String) itemValue : itemValue.toString();
                return sItemvalue.startsWith(sValue);
            }
        };
    }

    public static Criterion endsWith(final Object name, final Object value) {
        return new Criterion<Object>(name.toString(), Operator.ENDS_WITH, value) {
            String sValue = value instanceof String ? (String) value : value.toString();

            @Override
            public boolean resolve(Map<String, FieldAccess> fields, Object owner) {
                FieldAccess field = fields.get(name);
                Object itemValue = field.getValue(owner);
                String sItemvalue = itemValue instanceof String ? (String) itemValue : itemValue.toString();
                return sItemvalue.endsWith(sValue);
            }
        };
    }

    public static Criterion notContains(final Object name, final Object value) {
        return doContains(name, value, true);
    }

    public static Criterion contains(final Object name, final Object value) {
        return doContains(name, value, false);
    }

    private static Criterion doContains(final Object name, final Object value, final boolean not) {
        return new Criterion<Object>(name.toString(), not ? Operator.NOT_CONTAINS : Operator.CONTAINS, value) {
            String sValue = value instanceof String ? (String) value : value.toString();

            @Override
            public boolean resolve(Map<String, FieldAccess> fields, Object owner) {

                boolean returnVal;
                FieldAccess field = fields.get(name);
                if (Conversions.implementsInterface(field.getType(), Typ.collection)) {
                    Collection collection = (Collection) field.getValue(owner);
                    returnVal = collection.contains(value);
                } else if (field.getType().isArray()) {
                    returnVal = false;
                    Object array = (Object) field.getValue(owner);
                    Iterator iter = iterator(array);
                    while (iter.hasNext()) {
                        Object i = iter.next();
                        if (i.equals(value)) {
                            returnVal = true;
                        }
                    }
                } else {
                    Object itemValue = field.getValue(owner);
                    String sItemvalue = itemValue instanceof String ? (String) itemValue : itemValue.toString();
                    returnVal = sItemvalue.contains(sValue);
                }
                return not ? !returnVal : returnVal;
            }
        };
    }

    public static Criterion notEmpty(final Object name) {
        return doEmpty(name, true);
    }

    public static Criterion empty(final Object name) {
        return doEmpty(name, false);
    }

    private static Criterion doEmpty(final Object name, final boolean not) {
        return new Criterion<Object>(name.toString(), not ? Operator.NOT_EMPTY : Operator.IS_EMPTY, "") {
            String sValue = value instanceof String ? (String) value : value.toString();

            @Override
            public boolean resolve(Map<String, FieldAccess> fields, Object owner) {

                boolean returnVal;
                FieldAccess field = fields.get(name);
                if (Conversions.implementsInterface(field.getType(), Typ.collection)) {
                    Collection collection = (Collection) field.getValue(owner);
                    returnVal = collection == null || collection.isEmpty();
                } else if (field.getType().isArray()) {
                    Object array = (Object) field.getValue(owner);
                    returnVal = array == null || Reflection.len(array) == 0;
                } else {
                    Object obj = (Object) field.getValue(owner);
                    returnVal = obj == null || Reflection.len(obj) == 0;
                }
                return not ? !returnVal : returnVal;
            }
        };
    }

    public static Criterion notNull(final Object name) {
        return doIsNull(name, true);
    }

    public static Criterion isNull(final Object name) {
        return doIsNull(name, false);
    }

    private static Criterion doIsNull(final Object name, final boolean not) {
        return new Criterion<Object>(name.toString(), not ? Operator.NOT_NULL : Operator.IS_NULL, "") {
            String sValue = value instanceof String ? (String) value : value.toString();

            @Override
            public boolean resolve(Map<String, FieldAccess> fields, Object owner) {

                FieldAccess field = fields.get(name);
                boolean isNull = field.getValue(owner) == null;
                return not ? !isNull : isNull;
            }
        };
    }




    //    I am going to add date handling, but not now. TODO
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
    static Criterion eqInt(final Object name, final int compareValue) {
        return new Criterion.PrimitiveCriterion(name.toString(), Operator.GREATER_THAN, compareValue) {
            @Override
            public boolean resolve(final Map<String, FieldAccess> fields, final Object owner) {
                FieldAccess field = fields.get(name);
                int value = field.getInt(owner);
                return value == compareValue;
            }

        };
    }

    static Criterion notEqInt(final Object name, final int compareValue) {
        return new Criterion.PrimitiveCriterion(name.toString(), Operator.GREATER_THAN, compareValue) {
            @Override
            public boolean resolve(final Map<String, FieldAccess> fields, final Object owner) {
                FieldAccess field = fields.get(name);
                int value = field.getInt(owner);
                return value != compareValue;
            }
        };
    }

    static Criterion notInInts(final Object name, final int... compareValues) {
        return new Criterion.PrimitiveCriterion(name.toString(), Operator.GREATER_THAN, compareValues) {
            @Override
            public boolean resolve(final Map<String, FieldAccess> fields, final Object owner) {
                FieldAccess field = fields.get(name);
                int value = field.getInt(owner);

                for (int compareValue : compareValues) {
                    if (value == compareValue) {
                        return false;
                    }
                }
                return true;
            }
        };
    }

    static Criterion inInts(final Object name, final int... compareValues) {
        return new Criterion.PrimitiveCriterion(name.toString(), Operator.GREATER_THAN, compareValues) {
            @Override
            public boolean resolve(final Map<String, FieldAccess> fields, final Object owner) {
                FieldAccess field = fields.get(name);
                int value = field.getInt(owner);

                for (int compareValue : compareValues) {
                    if (value == compareValue) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    static Criterion ltInt(final Object name, final int compareValue) {
        return new Criterion.PrimitiveCriterion(name.toString(), Operator.GREATER_THAN, compareValue) {
            @Override
            public boolean resolve(final Map<String, FieldAccess> fields, final Object owner) {
                FieldAccess field = fields.get(name);
                int value = field.getInt(owner);
                return value < compareValue;
            }
        };
    }

    static Criterion lteInt(final Object name, final int compareValue) {
        return new Criterion.PrimitiveCriterion(name.toString(), Operator.GREATER_THAN, compareValue) {
            @Override
            public boolean resolve(final Map<String, FieldAccess> fields, final Object owner) {
                FieldAccess field = fields.get(name);
                int value = field.getInt(owner);
                return value <= compareValue;
            }
        };
    }

    static Criterion gtInt(final Object name, final int compareValue) {
        return new Criterion.PrimitiveCriterion(name.toString(), Operator.GREATER_THAN, compareValue) {
            @Override
            public boolean resolve(final Map<String, FieldAccess> fields, final Object owner) {
                FieldAccess field = fields.get(name);
                int value = field.getInt(owner);
                return value > compareValue;
            }
        };
    }

    static Criterion gteInt(final Object name, final int compareValue) {
        return new Criterion.PrimitiveCriterion(name.toString(), Operator.GREATER_THAN, compareValue) {
            @Override
            public boolean resolve(final Map<String, FieldAccess> fields, final Object owner) {
                FieldAccess field = fields.get(name);
                int value = field.getInt(owner);
                return value >= compareValue;
            }
        };
    }

    static Criterion betweenInt(final Object name, final int start, final int stop) {
        return new Criterion.PrimitiveCriterion(name.toString(), Operator.BETWEEN, start, stop) {
            @Override
            public boolean resolve(final Map<String, FieldAccess> fields, final Object owner) {
                FieldAccess field = fields.get(name);
                int value = field.getInt(owner);
                return value >= start && value < stop;
            }
        };
    }

    //Float
    //
    //
    //
    static Criterion eqFloat(final Object name, final float compareValue) {
        return new Criterion.PrimitiveCriterion(name.toString(), Operator.GREATER_THAN, compareValue) {
            @Override
            public boolean resolve(final Map<String, FieldAccess> fields, final Object owner) {
                FieldAccess field = fields.get(name);
                float value = field.getFloat(owner);
                return value == compareValue;
            }

        };
    }

    static Criterion notEqFloat(final Object name, final float compareValue) {
        return new Criterion.PrimitiveCriterion(name.toString(), Operator.GREATER_THAN, compareValue) {
            @Override
            public boolean resolve(final Map<String, FieldAccess> fields, final Object owner) {
                FieldAccess field = fields.get(name);
                float value = field.getFloat(owner);
                return value != compareValue;
            }
        };
    }

    static Criterion notInFloats(final Object name, final float... compareValues) {
        return new Criterion.PrimitiveCriterion(name.toString(), Operator.GREATER_THAN, compareValues) {
            @Override
            public boolean resolve(final Map<String, FieldAccess> fields, final Object owner) {
                FieldAccess field = fields.get(name);
                float value = field.getFloat(owner);

                for (float compareValue : compareValues) {
                    if (value == compareValue) {
                        return false;
                    }
                }
                return true;
            }
        };
    }

    static Criterion inFloats(final Object name, final float... compareValues) {
        return new Criterion.PrimitiveCriterion(name.toString(), Operator.GREATER_THAN, compareValues) {
            @Override
            public boolean resolve(final Map<String, FieldAccess> fields, final Object owner) {
                FieldAccess field = fields.get(name);
                float value = field.getFloat(owner);

                for (float compareValue : compareValues) {
                    if (value == compareValue) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    static Criterion ltFloat(final Object name, final float compareValue) {
        return new Criterion.PrimitiveCriterion(name.toString(), Operator.GREATER_THAN, compareValue) {
            @Override
            public boolean resolve(final Map<String, FieldAccess> fields, final Object owner) {
                FieldAccess field = fields.get(name);
                float value = field.getFloat(owner);
                return value < compareValue;
            }
        };
    }

    static Criterion lteFloat(final Object name, final float compareValue) {
        return new Criterion.PrimitiveCriterion(name.toString(), Operator.GREATER_THAN, compareValue) {
            @Override
            public boolean resolve(final Map<String, FieldAccess> fields, final Object owner) {
                FieldAccess field = fields.get(name);
                float value = field.getFloat(owner);
                return value <= compareValue;
            }
        };
    }

    static Criterion gtFloat(final Object name, final float compareValue) {
        return new Criterion.PrimitiveCriterion(name.toString(), Operator.GREATER_THAN, compareValue) {
            @Override
            public boolean resolve(final Map<String, FieldAccess> fields, final Object owner) {
                FieldAccess field = fields.get(name);
                float value = field.getFloat(owner);
                return value > compareValue;
            }
        };
    }

    static Criterion gteFloat(final Object name, final float compareValue) {
        return new Criterion.PrimitiveCriterion(name.toString(), Operator.GREATER_THAN, compareValue) {
            @Override
            public boolean resolve(final Map<String, FieldAccess> fields, final Object owner) {
                FieldAccess field = fields.get(name);
                float value = field.getFloat(owner);
                return value >= compareValue;
            }
        };
    }

    static Criterion betweenFloat(final Object name, final float start, final float stop) {
        return new Criterion.PrimitiveCriterion(name.toString(), Operator.BETWEEN, start, stop) {
            @Override
            public boolean resolve(final Map<String, FieldAccess> fields, final Object owner) {
                FieldAccess field = fields.get(name);
                float value = field.getFloat(owner);
                return value >= start && value < stop;
            }
        };
    }

    static Criterion isTrue(final Object name) {
        return eqBoolean(name, true);
    }

    static Criterion isFalse(final Object name) {
        return eqBoolean(name, false);
    }

    static Criterion eqBoolean(final Object name, final boolean compareValue) {
        return new Criterion.PrimitiveCriterion(name.toString(), Operator.GREATER_THAN, compareValue) {
            @Override
            public boolean resolve(final Map<String, FieldAccess> fields, final Object owner) {
                FieldAccess field = fields.get(name);
                boolean value = field.getBoolean(owner);
                return value == compareValue;
            }

        };
    }

    static Criterion notEqBoolean(final Object name, final boolean compareValue) {
        return new Criterion.PrimitiveCriterion(name.toString(), Operator.GREATER_THAN, compareValue) {
            @Override
            public boolean resolve(final Map<String, FieldAccess> fields, final Object owner) {
                FieldAccess field = fields.get(name);
                boolean value = field.getBoolean(owner);
                return value != compareValue;
            }
        };
    }

    //Double
    //
    //
    //
    static Criterion eqDouble(final Object name, final double compareValue) {
        return new Criterion.PrimitiveCriterion(name.toString(), Operator.GREATER_THAN, compareValue) {
            @Override
            public boolean resolve(final Map<String, FieldAccess> fields, final Object owner) {
                FieldAccess field = fields.get(name);
                double value = field.getDouble(owner);
                return value == compareValue;
            }

        };
    }

    static Criterion notEqDouble(final Object name, final double compareValue) {
        return new Criterion.PrimitiveCriterion(name.toString(), Operator.GREATER_THAN, compareValue) {
            @Override
            public boolean resolve(final Map<String, FieldAccess> fields, final Object owner) {
                FieldAccess field = fields.get(name);
                double value = field.getDouble(owner);
                return value != compareValue;
            }
        };
    }

    static Criterion notInDoubles(final Object name, final double... compareValues) {
        return new Criterion.PrimitiveCriterion(name.toString(), Operator.GREATER_THAN, compareValues) {
            @Override
            public boolean resolve(final Map<String, FieldAccess> fields, final Object owner) {
                FieldAccess field = fields.get(name);
                double value = field.getDouble(owner);

                for (double compareValue : compareValues) {
                    if (value == compareValue) {
                        return false;
                    }
                }
                return true;
            }
        };
    }

    static Criterion inDoubles(final Object name, final double... compareValues) {
        return new Criterion.PrimitiveCriterion(name.toString(), Operator.GREATER_THAN, compareValues) {
            @Override
            public boolean resolve(final Map<String, FieldAccess> fields, final Object owner) {
                FieldAccess field = fields.get(name);
                double value = field.getDouble(owner);

                for (double compareValue : compareValues) {
                    if (value == compareValue) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    static Criterion ltDouble(final Object name, final double compareValue) {
        return new Criterion.PrimitiveCriterion(name.toString(), Operator.GREATER_THAN, compareValue) {
            @Override
            public boolean resolve(final Map<String, FieldAccess> fields, final Object owner) {
                FieldAccess field = fields.get(name);
                double value = field.getDouble(owner);
                return value < compareValue;
            }
        };
    }

    static Criterion lteDouble(final Object name, final double compareValue) {
        return new Criterion.PrimitiveCriterion(name.toString(), Operator.GREATER_THAN, compareValue) {
            @Override
            public boolean resolve(final Map<String, FieldAccess> fields, final Object owner) {
                FieldAccess field = fields.get(name);
                double value = field.getDouble(owner);
                return value <= compareValue;
            }
        };
    }

    static Criterion gtDouble(final Object name, final double compareValue) {
        return new Criterion.PrimitiveCriterion(name.toString(), Operator.GREATER_THAN, compareValue) {
            @Override
            public boolean resolve(final Map<String, FieldAccess> fields, final Object owner) {
                FieldAccess field = fields.get(name);
                double value = field.getDouble(owner);
                return value > compareValue;
            }
        };
    }

    static Criterion gteDouble(final Object name, final double compareValue) {
        return new Criterion.PrimitiveCriterion(name.toString(), Operator.GREATER_THAN, compareValue) {
            @Override
            public boolean resolve(final Map<String, FieldAccess> fields, final Object owner) {
                FieldAccess field = fields.get(name);
                double value = field.getDouble(owner);
                return value >= compareValue;
            }
        };
    }

    static Criterion betweenDouble(final Object name, final double start, final double stop) {
        return new Criterion.PrimitiveCriterion(name.toString(), Operator.BETWEEN, start, stop) {
            @Override
            public boolean resolve(final Map<String, FieldAccess> fields, final Object owner) {
                FieldAccess field = fields.get(name);
                double value = field.getDouble(owner);
                return value >= start && value < stop;
            }
        };
    }

    //Short
    //
    //
    //
    static Criterion eqShort(final Object name, final short compareValue) {
        return new Criterion.PrimitiveCriterion(name.toString(), Operator.GREATER_THAN, compareValue) {
            @Override
            public boolean resolve(final Map<String, FieldAccess> fields, final Object owner) {
                FieldAccess field = fields.get(name);
                short value = field.getShort(owner);
                return value == compareValue;
            }

        };
    }

    static Criterion notEqShort(final Object name, final short compareValue) {
        return new Criterion.PrimitiveCriterion(name.toString(), Operator.GREATER_THAN, compareValue) {
            @Override
            public boolean resolve(final Map<String, FieldAccess> fields, final Object owner) {
                FieldAccess field = fields.get(name);
                short value = field.getShort(owner);
                return value != compareValue;
            }
        };
    }

    static Criterion notInShorts(final Object name, final short... compareValues) {
        return new Criterion.PrimitiveCriterion(name.toString(), Operator.GREATER_THAN, compareValues) {
            @Override
            public boolean resolve(final Map<String, FieldAccess> fields, final Object owner) {
                FieldAccess field = fields.get(name);
                short value = field.getShort(owner);

                for (short compareValue : compareValues) {
                    if (value == compareValue) {
                        return false;
                    }
                }
                return true;
            }
        };
    }

    static Criterion inShorts(final Object name, final short... compareValues) {
        return new Criterion.PrimitiveCriterion(name.toString(), Operator.GREATER_THAN, compareValues) {
            @Override
            public boolean resolve(final Map<String, FieldAccess> fields, final Object owner) {
                FieldAccess field = fields.get(name);
                short value = field.getShort(owner);

                for (short compareValue : compareValues) {
                    if (value == compareValue) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    static Criterion ltShort(final Object name, final short compareValue) {
        return new Criterion.PrimitiveCriterion(name.toString(), Operator.GREATER_THAN, compareValue) {
            @Override
            public boolean resolve(final Map<String, FieldAccess> fields, final Object owner) {
                FieldAccess field = fields.get(name);
                short value = field.getShort(owner);
                return value < compareValue;
            }
        };
    }

    static Criterion lteShort(final Object name, final short compareValue) {
        return new Criterion.PrimitiveCriterion(name.toString(), Operator.GREATER_THAN, compareValue) {
            @Override
            public boolean resolve(final Map<String, FieldAccess> fields, final Object owner) {
                FieldAccess field = fields.get(name);
                short value = field.getShort(owner);
                return value <= compareValue;
            }
        };
    }

    static Criterion gtShort(final Object name, final short compareValue) {
        return new Criterion.PrimitiveCriterion(name.toString(), Operator.GREATER_THAN, compareValue) {
            @Override
            public boolean resolve(final Map<String, FieldAccess> fields, final Object owner) {
                FieldAccess field = fields.get(name);
                short value = field.getShort(owner);
                return value > compareValue;
            }
        };
    }

    static Criterion gteShort(final Object name, final short compareValue) {
        return new Criterion.PrimitiveCriterion(name.toString(), Operator.GREATER_THAN, compareValue) {
            @Override
            public boolean resolve(final Map<String, FieldAccess> fields, final Object owner) {
                FieldAccess field = fields.get(name);
                short value = field.getShort(owner);
                return value >= compareValue;
            }
        };
    }

    static Criterion betweenShort(final Object name, final short start, final short stop) {
        return new Criterion.PrimitiveCriterion(name.toString(), Operator.BETWEEN, start, stop) {
            @Override
            public boolean resolve(final Map<String, FieldAccess> fields, final Object owner) {
                FieldAccess field = fields.get(name);
                short value = field.getShort(owner);
                return value >= start && value < stop;
            }
        };
    }

    //Byte
    //
    //
    //
    static Criterion eqByte(final Object name, final byte compareValue) {
        return new Criterion.PrimitiveCriterion(name.toString(), Operator.GREATER_THAN, compareValue) {
            @Override
            public boolean resolve(final Map<String, FieldAccess> fields, final Object owner) {
                FieldAccess field = fields.get(name);
                byte value = field.getByte(owner);
                return value == compareValue;
            }

        };
    }

    static Criterion notEqByte(final Object name, final byte compareValue) {
        return new Criterion.PrimitiveCriterion(name.toString(), Operator.GREATER_THAN, compareValue) {
            @Override
            public boolean resolve(final Map<String, FieldAccess> fields, final Object owner) {
                FieldAccess field = fields.get(name);
                byte value = field.getByte(owner);
                return value != compareValue;
            }
        };
    }

    static Criterion notInBytes(final Object name, final byte... compareValues) {
        return new Criterion.PrimitiveCriterion(name.toString(), Operator.GREATER_THAN, compareValues) {
            @Override
            public boolean resolve(final Map<String, FieldAccess> fields, final Object owner) {
                FieldAccess field = fields.get(name);
                byte value = field.getByte(owner);

                for (byte compareValue : compareValues) {
                    if (value == compareValue) {
                        return false;
                    }
                }
                return true;
            }
        };
    }

    static Criterion inBytes(final Object name, final byte... compareValues) {
        return new Criterion.PrimitiveCriterion(name.toString(), Operator.GREATER_THAN, compareValues) {
            @Override
            public boolean resolve(final Map<String, FieldAccess> fields, final Object owner) {
                FieldAccess field = fields.get(name);
                byte value = field.getByte(owner);

                for (byte compareValue : compareValues) {
                    if (value == compareValue) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    static Criterion ltByte(final Object name, final byte compareValue) {
        return new Criterion.PrimitiveCriterion(name.toString(), Operator.GREATER_THAN, compareValue) {
            @Override
            public boolean resolve(final Map<String, FieldAccess> fields, final Object owner) {
                FieldAccess field = fields.get(name);
                byte value = field.getByte(owner);
                return value < compareValue;
            }
        };
    }

    static Criterion lteByte(final Object name, final byte compareValue) {
        return new Criterion.PrimitiveCriterion(name.toString(), Operator.GREATER_THAN, compareValue) {
            @Override
            public boolean resolve(final Map<String, FieldAccess> fields, final Object owner) {
                FieldAccess field = fields.get(name);
                byte value = field.getByte(owner);
                return value <= compareValue;
            }
        };
    }

    static Criterion gtByte(final Object name, final byte compareValue) {
        return new Criterion.PrimitiveCriterion(name.toString(), Operator.GREATER_THAN, compareValue) {
            @Override
            public boolean resolve(final Map<String, FieldAccess> fields, final Object owner) {
                FieldAccess field = fields.get(name);
                byte value = field.getByte(owner);
                return value > compareValue;
            }
        };
    }

    static Criterion gteByte(final Object name, final byte compareValue) {
        return new Criterion.PrimitiveCriterion(name.toString(), Operator.GREATER_THAN, compareValue) {
            @Override
            public boolean resolve(final Map<String, FieldAccess> fields, final Object owner) {
                FieldAccess field = fields.get(name);
                byte value = field.getByte(owner);
                return value >= compareValue;
            }
        };
    }

    static Criterion betweenByte(final Object name, final byte start, final byte stop) {
        return new Criterion.PrimitiveCriterion(name.toString(), Operator.BETWEEN, start, stop) {
            @Override
            public boolean resolve(final Map<String, FieldAccess> fields, final Object owner) {
                FieldAccess field = fields.get(name);
                byte value = field.getByte(owner);
                return value >= start && value < stop;
            }
        };
    }

    //Long
    //
    //
    //
    static Criterion eqLong(final Object name, final long compareValue) {
        return new Criterion.PrimitiveCriterion(name.toString(), Operator.GREATER_THAN, compareValue) {
            @Override
            public boolean resolve(final Map<String, FieldAccess> fields, final Object owner) {
                FieldAccess field = fields.get(name);
                long value = field.getLong(owner);
                return value == compareValue;
            }

        };
    }

    static Criterion notEqLong(final Object name, final long compareValue) {
        return new Criterion.PrimitiveCriterion(name.toString(), Operator.GREATER_THAN, compareValue) {
            @Override
            public boolean resolve(final Map<String, FieldAccess> fields, final Object owner) {
                FieldAccess field = fields.get(name);
                long value = field.getLong(owner);
                return value != compareValue;
            }
        };
    }

    static Criterion notInLongs(final Object name, final long... compareValues) {
        return new Criterion.PrimitiveCriterion(name.toString(), Operator.GREATER_THAN, compareValues) {
            @Override
            public boolean resolve(final Map<String, FieldAccess> fields, final Object owner) {
                FieldAccess field = fields.get(name);
                long value = field.getLong(owner);

                for (long compareValue : compareValues) {
                    if (value == compareValue) {
                        return false;
                    }
                }
                return true;
            }
        };
    }

    static Criterion inLongs(final Object name, final long... compareValues) {
        return new Criterion.PrimitiveCriterion(name.toString(), Operator.GREATER_THAN, compareValues) {
            @Override
            public boolean resolve(final Map<String, FieldAccess> fields, final Object owner) {
                FieldAccess field = fields.get(name);
                long value = field.getLong(owner);

                for (long compareValue : compareValues) {
                    if (value == compareValue) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    static Criterion ltLong(final Object name, final long compareValue) {
        return new Criterion.PrimitiveCriterion(name.toString(), Operator.GREATER_THAN, compareValue) {
            @Override
            public boolean resolve(final Map<String, FieldAccess> fields, final Object owner) {
                FieldAccess field = fields.get(name);
                long value = field.getLong(owner);
                return value < compareValue;
            }
        };
    }

    static Criterion lteLong(final Object name, final long compareValue) {
        return new Criterion.PrimitiveCriterion(name.toString(), Operator.GREATER_THAN, compareValue) {
            @Override
            public boolean resolve(final Map<String, FieldAccess> fields, final Object owner) {
                FieldAccess field = fields.get(name);
                long value = field.getLong(owner);
                return value <= compareValue;
            }
        };
    }

    static Criterion gtLong(final Object name, final long compareValue) {
        return new Criterion.PrimitiveCriterion(name.toString(), Operator.GREATER_THAN, compareValue) {
            @Override
            public boolean resolve(final Map<String, FieldAccess> fields, final Object owner) {
                FieldAccess field = fields.get(name);
                long value = field.getLong(owner);
                return value > compareValue;
            }
        };
    }

    static Criterion gteLong(final Object name, final long compareValue) {
        return new Criterion.PrimitiveCriterion(name.toString(), Operator.GREATER_THAN, compareValue) {
            @Override
            public boolean resolve(final Map<String, FieldAccess> fields, final Object owner) {
                FieldAccess field = fields.get(name);
                long value = field.getLong(owner);
                return value >= compareValue;
            }
        };
    }

    static Criterion betweenLong(final Object name, final long start, final long stop) {
        return new Criterion.PrimitiveCriterion(name.toString(), Operator.BETWEEN, start, stop) {
            @Override
            public boolean resolve(final Map<String, FieldAccess> fields, final Object owner) {
                FieldAccess field = fields.get(name);
                long value = field.getLong(owner);
                return value >= start && value < stop;
            }
        };
    }

    //
    //
    //
    //
    static Criterion eqChar(final Object name, final char compareValue) {
        return new Criterion.PrimitiveCriterion(name.toString(), Operator.GREATER_THAN, compareValue) {
            @Override
            public boolean resolve(final Map<String, FieldAccess> fields, final Object owner) {
                FieldAccess field = fields.get(name);
                char value = field.getChar(owner);
                return value == compareValue;
            }

        };
    }

    static Criterion notEqChar(final Object name, final char compareValue) {
        return new Criterion.PrimitiveCriterion(name.toString(), Operator.GREATER_THAN, compareValue) {
            @Override
            public boolean resolve(final Map<String, FieldAccess> fields, final Object owner) {
                FieldAccess field = fields.get(name);
                char value = field.getChar(owner);
                return value != compareValue;
            }
        };
    }

    static Criterion notInChars(final Object name, final char... compareValues) {
        return new Criterion.PrimitiveCriterion(name.toString(), Operator.GREATER_THAN, compareValues) {
            @Override
            public boolean resolve(final Map<String, FieldAccess> fields, final Object owner) {
                FieldAccess field = fields.get(name);
                char value = field.getChar(owner);

                for (char compareValue : compareValues) {
                    if (value == compareValue) {
                        return false;
                    }
                }
                return true;
            }
        };
    }

    static Criterion inChars(final Object name, final char... compareValues) {
        return new Criterion.PrimitiveCriterion(name.toString(), Operator.GREATER_THAN, compareValues) {
            @Override
            public boolean resolve(final Map<String, FieldAccess> fields, final Object owner) {
                FieldAccess field = fields.get(name);
                char value = field.getChar(owner);

                for (char compareValue : compareValues) {
                    if (value == compareValue) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    static Criterion ltChar(final Object name, final char compareValue) {
        return new Criterion.PrimitiveCriterion(name.toString(), Operator.GREATER_THAN, compareValue) {
            @Override
            public boolean resolve(final Map<String, FieldAccess> fields, final Object owner) {
                FieldAccess field = fields.get(name);
                char value = field.getChar(owner);
                return value < compareValue;
            }
        };
    }

    static Criterion lteChar(final Object name, final char compareValue) {
        return new Criterion.PrimitiveCriterion(name.toString(), Operator.GREATER_THAN, compareValue) {
            @Override
            public boolean resolve(final Map<String, FieldAccess> fields, final Object owner) {
                FieldAccess field = fields.get(name);
                char value = field.getChar(owner);
                return value <= compareValue;
            }
        };
    }

    static Criterion gtChar(final Object name, final char compareValue) {
        return new Criterion.PrimitiveCriterion(name.toString(), Operator.GREATER_THAN, compareValue) {
            @Override
            public boolean resolve(final Map<String, FieldAccess> fields, final Object owner) {
                FieldAccess field = fields.get(name);
                char value = field.getChar(owner);
                return value > compareValue;
            }
        };
    }

    static Criterion gteChar(final Object name, final char compareValue) {
        return new Criterion.PrimitiveCriterion(name.toString(), Operator.GREATER_THAN, compareValue) {
            @Override
            public boolean resolve(final Map<String, FieldAccess> fields, final Object owner) {
                FieldAccess field = fields.get(name);
                char value = field.getChar(owner);
                return value >= compareValue;
            }
        };
    }

    static Criterion betweenChar(final Object name, final char start, final char stop) {
        return new Criterion.PrimitiveCriterion(name.toString(), Operator.BETWEEN, start, stop) {
            @Override
            public boolean resolve(final Map<String, FieldAccess> fields, final Object owner) {
                FieldAccess field = fields.get(name);
                char value = field.getChar(owner);
                return value >= start && value < stop;
            }
        };
    }


}
