package org.boon.criteria;

import org.boon.core.Typ;
import org.boon.core.reflection.fields.FieldAccess;
import org.boon.core.reflection.Conversions;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Objects;



public abstract class Criterion<VALUE> extends Criteria {

    private String name;
    private Operator operator;
    protected VALUE value;
    protected VALUE[] values;
    private final int hashCode;
    private final String toString;

    private boolean initialized;
    private Criterion nativeDelegate;
    private boolean useDelegate;

    public Criterion(String name, Operator operator, VALUE... values) {
        Objects.requireNonNull( name,       "name cannot be null");
        Objects.requireNonNull( operator,   "operator cannot be null");
        Objects.requireNonNull( values,     "values cannot be null");

        this.name = name;
        this.operator = operator;
        this.setValues(values);
        hashCode = doHashCode();
        toString = doToString();
    }


    public String getName() {
        return name;
    }

    public Operator getOperator() {
        return operator;
    }


    public VALUE getValue() {
        return value;
    }


    public VALUE[] getValues() {
        return values;
    }

    public void setValues(VALUE[] values) {
        if (values.length > 0) {
            this.value = values[0];
        }
        this.values = values;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Criterion)) return false;

        Criterion criterion = (Criterion) o;

        if (name != null ? !name.equals(criterion.name) : criterion.name != null) return false;
        if (operator != criterion.operator) return false;
        if (value != null ? !value.equals(criterion.value) : criterion.value != null) return false;
        if (!Arrays.equals(values, criterion.values)) return false;

        return true;
    }


    @Override
    public int hashCode() {
        return hashCode;
    }

    public int doHashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (operator != null ? operator.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (values != null ? Arrays.hashCode(values) : 0);
        return result;
    }

    @Override
    public String toString() {
        return toString;
    }


    public String doToString() {
        StringBuilder builder = new StringBuilder(80);
        builder.append("c{");
        builder.append("\"name\":'");
        builder.append(name);
        builder.append(", \"operator\":");
        builder.append(operator);
        builder.append(", \"set\":");
        builder.append(value);
        builder.append(", \"update\":");
        builder.append(Arrays.toString(values));
        builder.append("}");
        return builder.toString();
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void init(Object o) {
        init(o.getClass());
    }

    public void init(Class clazz) {
        Map<String, FieldAccess> fields = getFieldsInternal(clazz);
        initIfNeeded(this, fields);
    }

    public void init(Map<String, FieldAccess> fields) {
        initIfNeeded(this, fields);
    }


    private static void initIfNeeded(Criterion criterion, Map<String, FieldAccess> fields) {
        if (!criterion.initialized) {
            criterion.initialized = true;
            FieldAccess field = fields.get(criterion.name);

            if (field == null) {
                return;
            }

            Class type = field.getType();


            if (!type.isPrimitive() && type != Typ.date) {
                return;
            }


            if (type == Typ.date && !(criterion.value instanceof Date)) {
                criterion.value = Conversions.toDate(criterion.value);
                if (criterion.operator == Operator.BETWEEN) {
                    criterion.values[0] = Conversions.toDate(criterion.values[0]);

                    criterion.values[1] = Conversions.toDate(criterion.values[1]);

                }
                return;
            }

            criterion.useDelegate = true;

            if (type == Typ.intgr) {
                switch (criterion.operator) {
                    case EQUAL:
                        criterion.nativeDelegate = CriteriaFactory.eqInt(criterion.name, Conversions.toInt(criterion.value));
                        break;

                    case NOT_EQUAL:
                        criterion.nativeDelegate = CriteriaFactory.notEqInt(criterion.name, Conversions.toInt(criterion.value));
                        break;

                    case LESS_THAN:
                        criterion.nativeDelegate = CriteriaFactory.ltInt(criterion.name, Conversions.toInt(criterion.value));
                        break;

                    case LESS_THAN_EQUAL:
                        criterion.nativeDelegate = CriteriaFactory.lteInt(criterion.name, Conversions.toInt(criterion.value));
                        break;

                    case GREATER_THAN:
                        criterion.nativeDelegate = CriteriaFactory.gtInt(criterion.name, Conversions.toInt(criterion.value));
                        break;

                    case GREATER_THAN_EQUAL:
                        criterion.nativeDelegate = CriteriaFactory.gteInt(criterion.name, Conversions.toInt(criterion.value));
                        break;

                    case BETWEEN:
                        criterion.nativeDelegate = CriteriaFactory.betweenInt(criterion.name, Conversions.toInt(criterion.value),
                                Conversions.toInt(criterion.values[1]));
                        break;

                    case IN:
                        criterion.nativeDelegate = CriteriaFactory.inInts(criterion.name, Conversions.iarray(criterion.values));
                        break;


                    case NOT_IN:
                        criterion.nativeDelegate = CriteriaFactory.notInInts(criterion.name, Conversions.iarray(criterion.values));

                        break;

                    default:
                        criterion.useDelegate = false;
                }
            } else if (type == Typ.bt) {
                switch (criterion.operator) {
                    case EQUAL:
                        criterion.nativeDelegate = CriteriaFactory.eqByte(criterion.name, Conversions.toByte(criterion.value));
                        break;

                    case NOT_EQUAL:
                        criterion.nativeDelegate = CriteriaFactory.notEqByte(criterion.name, Conversions.toByte(criterion.value));
                        break;

                    case LESS_THAN:
                        criterion.nativeDelegate = CriteriaFactory.ltByte(criterion.name, Conversions.toByte(criterion.value));
                        break;

                    case LESS_THAN_EQUAL:
                        criterion.nativeDelegate = CriteriaFactory.lteByte(criterion.name, Conversions.toByte(criterion.value));
                        break;

                    case GREATER_THAN:
                        criterion.nativeDelegate = CriteriaFactory.gtByte(criterion.name, Conversions.toByte(criterion.value));
                        break;

                    case GREATER_THAN_EQUAL:
                        criterion.nativeDelegate = CriteriaFactory.gteByte(criterion.name, Conversions.toByte(criterion.value));
                        break;

                    case IN:
                        criterion.nativeDelegate = CriteriaFactory.inBytes(criterion.name, Conversions.barray(criterion.values));
                        break;


                    case NOT_IN:
                        criterion.nativeDelegate = CriteriaFactory.notInBytes(criterion.name, Conversions.barray(criterion.values));

                        break;

                    default:
                        criterion.useDelegate = false;
                }

            } else if (type == Typ.shrt) {
                switch (criterion.operator) {
                    case EQUAL:
                        criterion.nativeDelegate = CriteriaFactory.eqShort(criterion.name, Conversions.toShort(criterion.value));
                        break;

                    case NOT_EQUAL:
                        criterion.nativeDelegate = CriteriaFactory.notEqShort(criterion.name, Conversions.toShort(criterion.value));
                        break;

                    case LESS_THAN:
                        criterion.nativeDelegate = CriteriaFactory.ltShort(criterion.name, Conversions.toShort(criterion.value));
                        break;

                    case LESS_THAN_EQUAL:
                        criterion.nativeDelegate = CriteriaFactory.lteShort(criterion.name, Conversions.toShort(criterion.value));
                        break;

                    case GREATER_THAN:
                        criterion.nativeDelegate = CriteriaFactory.gtShort(criterion.name, Conversions.toShort(criterion.value));
                        break;

                    case GREATER_THAN_EQUAL:
                        criterion.nativeDelegate = CriteriaFactory.gteShort(criterion.name, Conversions.toShort(criterion.value));
                        break;

                    case IN:
                        criterion.nativeDelegate = CriteriaFactory.inShorts(criterion.name, Conversions.sarray(criterion.values));
                        break;


                    case NOT_IN:
                        criterion.nativeDelegate = CriteriaFactory.notInShorts(criterion.name, Conversions.sarray(criterion.values));

                        break;

                    default:
                        criterion.useDelegate = false;
                }

            } else if (type == Typ.flt) {
                switch (criterion.operator) {
                    case EQUAL:
                        criterion.nativeDelegate = CriteriaFactory.eqLong(criterion.name, Conversions.toLong(criterion.value));
                        break;

                    case NOT_EQUAL:
                        criterion.nativeDelegate = CriteriaFactory.notEqLong(criterion.name, Conversions.toLong(criterion.value));
                        break;

                    case LESS_THAN:
                        criterion.nativeDelegate = CriteriaFactory.ltLong(criterion.name, Conversions.toLong(criterion.value));
                        break;

                    case LESS_THAN_EQUAL:
                        criterion.nativeDelegate = CriteriaFactory.lteLong(criterion.name, Conversions.toLong(criterion.value));
                        break;

                    case GREATER_THAN:
                        criterion.nativeDelegate = CriteriaFactory.gtLong(criterion.name, Conversions.toLong(criterion.value));
                        break;

                    case GREATER_THAN_EQUAL:
                        criterion.nativeDelegate = CriteriaFactory.gteLong(criterion.name, Conversions.toLong(criterion.value));
                        break;

                    case IN:
                        criterion.nativeDelegate = CriteriaFactory.inLongs(criterion.name, Conversions.larray(criterion.values));
                        break;


                    case NOT_IN:
                        criterion.nativeDelegate = CriteriaFactory.notInLongs(criterion.name, Conversions.larray(criterion.values));

                        break;

                    default:
                        criterion.useDelegate = false;
                }


            } else if (type == Typ.flt) {


                switch (criterion.operator) {
                    case EQUAL:
                        criterion.nativeDelegate = CriteriaFactory.eqFloat(criterion.name, Conversions.toFloat(criterion.value));
                        break;

                    case NOT_EQUAL:
                        criterion.nativeDelegate = CriteriaFactory.notEqFloat(criterion.name, Conversions.toFloat(criterion.value));
                        break;

                    case LESS_THAN:
                        criterion.nativeDelegate = CriteriaFactory.ltFloat(criterion.name, Conversions.toFloat(criterion.value));
                        break;

                    case LESS_THAN_EQUAL:
                        criterion.nativeDelegate = CriteriaFactory.lteFloat(criterion.name, Conversions.toFloat(criterion.value));
                        break;

                    case GREATER_THAN:
                        criterion.nativeDelegate = CriteriaFactory.gtFloat(criterion.name, Conversions.toFloat(criterion.value));
                        break;

                    case GREATER_THAN_EQUAL:
                        criterion.nativeDelegate = CriteriaFactory.gteFloat(criterion.name, Conversions.toFloat(criterion.value));
                        break;

                    case IN:
                        criterion.nativeDelegate = CriteriaFactory.inFloats(criterion.name, Conversions.farray(criterion.values));
                        break;


                    case NOT_IN:
                        criterion.nativeDelegate = CriteriaFactory.notInFloats(criterion.name, Conversions.farray(criterion.values));

                        break;

                    default:
                        criterion.useDelegate = false;
                }

            } else if (type == Typ.dbl) {

                switch (criterion.operator) {
                    case EQUAL:
                        criterion.nativeDelegate = CriteriaFactory.eqDouble(criterion.name, Conversions.toDouble(criterion.value));
                        break;

                    case NOT_EQUAL:
                        criterion.nativeDelegate = CriteriaFactory.notEqDouble(criterion.name, Conversions.toDouble(criterion.value));
                        break;

                    case LESS_THAN:
                        criterion.nativeDelegate = CriteriaFactory.ltDouble(criterion.name, Conversions.toDouble(criterion.value));
                        break;

                    case LESS_THAN_EQUAL:
                        criterion.nativeDelegate = CriteriaFactory.lteDouble(criterion.name, Conversions.toDouble(criterion.value));
                        break;

                    case GREATER_THAN:
                        criterion.nativeDelegate = CriteriaFactory.gtDouble(criterion.name, Conversions.toDouble(criterion.value));
                        break;

                    case GREATER_THAN_EQUAL:
                        criterion.nativeDelegate = CriteriaFactory.gteDouble(criterion.name, Conversions.toDouble(criterion.value));
                        break;

                    case BETWEEN:
                        criterion.nativeDelegate = CriteriaFactory.betweenDouble(criterion.name, Conversions.toDouble(criterion.value),
                                Conversions.toDouble(criterion.values[1]));
                        break;

                    case IN:
                        criterion.nativeDelegate = CriteriaFactory.inDoubles(criterion.name, Conversions.toDouble(criterion.value),
                                Conversions.toDouble(criterion.values[1]));
                        break;


                    case NOT_IN:
                        criterion.nativeDelegate = CriteriaFactory.notInDoubles(criterion.name, Conversions.toDouble(criterion.value),
                                Conversions.toDouble(criterion.values[1]));
                        break;

                    default:
                        criterion.useDelegate = false;
                }


            } else if (type == Typ.bln) {


                switch (criterion.operator) {
                    case EQUAL:
                        criterion.nativeDelegate = CriteriaFactory.eqBoolean(criterion.name, Conversions.toBoolean(criterion.value));
                        break;

                    case NOT_EQUAL:
                        criterion.nativeDelegate = CriteriaFactory.notEqBoolean(criterion.name, Conversions.toBoolean(criterion.value));
                        break;


                    default:
                        criterion.useDelegate = false;
                }

            } else if (type == Typ.chr) {
                switch (criterion.operator) {


                    case EQUAL:
                        criterion.nativeDelegate = CriteriaFactory.eqChar(criterion.name, Conversions.toChar(criterion.value));
                        break;

                    case NOT_EQUAL:
                        criterion.nativeDelegate = CriteriaFactory.notEqChar(criterion.name, Conversions.toChar(criterion.value));
                        break;

                    case LESS_THAN:
                        criterion.nativeDelegate = CriteriaFactory.ltChar(criterion.name, Conversions.toChar(criterion.value));
                        break;

                    case LESS_THAN_EQUAL:
                        criterion.nativeDelegate = CriteriaFactory.lteChar(criterion.name, Conversions.toChar(criterion.value));
                        break;

                    case GREATER_THAN:
                        criterion.nativeDelegate = CriteriaFactory.gtChar(criterion.name, Conversions.toChar(criterion.value));
                        break;

                    case GREATER_THAN_EQUAL:
                        criterion.nativeDelegate = CriteriaFactory.gteChar(criterion.name, Conversions.toChar(criterion.value));
                        break;

                    case BETWEEN:
                        criterion.nativeDelegate = CriteriaFactory.betweenChar(criterion.name, Conversions.toChar(criterion.value),
                                Conversions.toChar(criterion.values[1]));
                        break;

                    case IN:
                        criterion.nativeDelegate = CriteriaFactory.inChars(criterion.name, Conversions.toChar(criterion.value),
                                Conversions.toChar(criterion.values[1]));
                        break;


                    case NOT_IN:
                        criterion.nativeDelegate = CriteriaFactory.notInChars(criterion.name, Conversions.toChar(criterion.value),
                                Conversions.toChar(criterion.values[1]));
                        break;

                    default:
                        criterion.useDelegate = false;
                }

            }
        }

    }

    @Override
    public boolean test(Object o) {

        Map<String, FieldAccess> fields = getFieldsInternal(o);

        initIfNeeded(this, fields);
        if (this.useDelegate) {
            return this.nativeDelegate.resolve(fields, o);
        }
        return resolve(fields, o);
    }

    public static abstract class PrimitiveCriterion extends Criterion {

        public PrimitiveCriterion(String name, Operator operator, Object... objects) {
            super(name, operator, objects);
        }

        @Override
        public boolean test(Object o) {

            Map<String, FieldAccess> fields = getFieldsInternal(o);
            return resolve(fields, o);
        }

    }

}
