package org.boon.core;

public enum Type {

    INT, SHORT, BYTE, LONG, CHAR, BOOLEAN, COLLECTION, ARRAY, FLOAT, INSTANCE, BIG_INT, BIG_DECIMAL,
    DATE, NUMBER, LONG_WRAPPER, INTEGER_WRAPPER, SHORT_WRAPPER, CHAR_WRAPPER, BOOLEAN_WRAPPER,
    BYTE_WRAPPER, FLOAT_WRAPPER, DOUBLE_WRAPPER,
    INTEGER, STRING, DOUBLE, TRUE, FALSE, NULL, MAP, LIST, SET, CHAR_SEQUENCE,
    INTERFACE, ABSTRACT, OBJECT, SYSTEM, ENUM, CALENDAR, VALUE_MAP, VALUE;



    public  static Type getInstanceType ( Object object ) {
             if (object == null) {
                 return NULL;
             } else {
                 return getType(object.getClass ());
             }
    }

    public static Type getType ( Class<?> clazz ) {

        final String className = clazz.getName();
        Type type =  getType( className );

        if (type != INSTANCE) {
            return type;
        }

        if ( className.startsWith ( "java" )) {
              if ( Typ.isCharSequence ( clazz ) ) {
                      type = CHAR_SEQUENCE;
               } else if (Typ.isCollection ( clazz )) {
                     if (Typ.isList ( clazz )) {
                         type = LIST;
                     } else if (Typ.isSet ( clazz )) {
                         type = SET;
                     } else {
                         type = COLLECTION;
                     }
               } else if (Typ.isMap ( clazz )) {
                      type = MAP;
               }
               else {
                    type = SYSTEM;
               }
        } else if ( clazz.isInterface () ) {
            type = INTERFACE;
        } else if (clazz.isEnum()) {
            type = ENUM;
        } else if (clazz.isArray ()) {
            type = ARRAY;
        } else if (Typ.isAbstract ( clazz )) {
            type = ABSTRACT;
        }
        return type;

    }

    public static Type getType ( String typeName ) {

            switch ( typeName ) {
                case "int":
                    return Type.INT;
                case "short":
                    return Type.SHORT;
                case "byte":
                    return Type.BYTE;
                case "float":
                    return Type.FLOAT;
                case "double":
                    return Type.DOUBLE;
                case "boolean":
                    return Type.BOOLEAN;
                case "char":
                    return Type.CHAR;
                case "long":
                    return Type.LONG;

                case "java.lang.String":
                    return Type.STRING;
                case "java.lang.Boolean":
                    return Type.BOOLEAN_WRAPPER;
                case "java.lang.Byte":
                    return Type.BYTE_WRAPPER;
                case "java.lang.Short":
                    return Type.SHORT_WRAPPER;
                case "java.lang.Integer":
                    return Type.INTEGER_WRAPPER;
                case "java.lang.Double":
                    return Type.DOUBLE_WRAPPER;
                case "java.lang.Float":
                    return Type.FLOAT_WRAPPER;
                case "java.lang.Character":
                    return Type.CHAR_WRAPPER;

                case "java.lang.Long":
                    return Type.LONG_WRAPPER;


                case "java.util.Set":
                case "java.util.HashSet":
                case "java.util.TreeSet":
                    return Type.SET;

                case "java.util.List":
                case "java.util.ArrayList":
                case "java.util.LinkedList":
                    return Type.LIST;

                case "java.util.Map":
                case "org.boon.core.LazyMap":
                case "java.util.HashMap":
                case "java.util.LinkedHashMap":
                case "java.util.TreeMap":
                case "org.boon.core.value.LazyValueMap":
                    return Type.MAP;

                case "java.lang.CharSequence":
                    return Type.CHAR_SEQUENCE;

                case "java.math.BigDecimal":
                    return Type.BIG_DECIMAL;
                case "java.math.BigInteger":
                    return Type.BIG_INT;

                case "java.util.Date":
                case "java.sql.Date":
                case "java.sql.Time":
                case "java.sql.Timestamp":
                    return Type.DATE;



                case "java.util.Calendar":
                    return Type.CALENDAR;

                case "org.boon.core.value.ValueMapImpl":
                    return Type.VALUE_MAP;

                case "org.boon.core.value.NumberValue":
                case "org.boon.core.value.CharSequenceValue":
                    return Type.VALUE;


            }
            return Type.INSTANCE;

    }


    public boolean  hasLength (  ) {

        switch ( this ) {
            case LIST:
            case MAP:
            case STRING:
            case CHAR_SEQUENCE:
            case SET:
            case COLLECTION:
            case ARRAY:
                return true;
            default:
                return false;
        }
    }

    public  boolean isCollection (  ) {

        switch ( this ) {
            case LIST:
            case SET:
            case COLLECTION:
                return true;
            default:
                return false;
        }
    }

}
