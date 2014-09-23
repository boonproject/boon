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

package org.boon.core;

import java.util.*;

public enum Type {


    //PRIMITIVE
    BOOLEAN(false, true), BYTE(false, true), SHORT(false, true), CHAR(false, true),
    INT(false, true),  FLOAT(false, true), LONG(false, true), DOUBLE(false, true),

    //Wrappers
    LONG_WRAPPER(LONG), INTEGER_WRAPPER(INT), SHORT_WRAPPER(SHORT),
    CHAR_WRAPPER(CHAR), BOOLEAN_WRAPPER(BOOLEAN),
    BYTE_WRAPPER(BYTE), FLOAT_WRAPPER(FLOAT), DOUBLE_WRAPPER(DOUBLE),


    //Concepts
    TRUE(BOOLEAN), FALSE(BOOLEAN), INSTANCE, NULL,
    INTERFACE, ABSTRACT, SYSTEM, VOID, UNKNOWN, BASIC_TYPE,

    //BASE
    CHAR_SEQUENCE, NUMBER, OBJECT, CLASS, ENUM,


    //BASIC TYPES 1st Class
    STRING(CHAR_SEQUENCE), CALENDAR, DATE,


    //SECOND TIER BASIC TYPES
    URL(BASIC_TYPE), URI(BASIC_TYPE), LOCALE(BASIC_TYPE),
    TIME_ZONE(BASIC_TYPE), CURRENCY(BASIC_TYPE),
    FILE(BASIC_TYPE), PATH(BASIC_TYPE), UUID(BASIC_TYPE),



     //Numeric
     BIG_INT(NUMBER), BIG_DECIMAL(NUMBER),

    //COLLECTIONS
    COLLECTION, LIST(COLLECTION), SET(COLLECTION),
    MAP,
    MAP_STRING_OBJECT(MAP),

    ARRAY(true),
    ARRAY_INT(true, INT),
    ARRAY_BYTE(true, SHORT),
    ARRAY_SHORT(true, SHORT),
    ARRAY_FLOAT(true, FLOAT),
    ARRAY_DOUBLE(true, DOUBLE),
    ARRAY_LONG(true, LONG),
    ARRAY_STRING(true, STRING),
    ARRAY_OBJECT(true, OBJECT),



    //BOON
    VALUE_MAP, VALUE;


    final Type baseTypeOrWrapper;
    private final boolean array;
    private final boolean primitive;

    Type() {
        baseTypeOrWrapper =null;
        array=false;
        primitive=false;
    }


    Type(Type type) {
        baseTypeOrWrapper =type;
        array=false;
        primitive=false;

    }

    Type(boolean isarray) {
        this.array = isarray;
        baseTypeOrWrapper=null;
        primitive=false;

    }


    Type(boolean isarray, Type type) {
        this.array = isarray;
        baseTypeOrWrapper=type;
        primitive=false;

    }

    Type(boolean array, boolean primitive) {
        this.array = array;
        this.primitive = primitive;
        baseTypeOrWrapper = null;
    }

    public  static Type getInstanceType ( Object object ) {


             if (object == null) {
                 return NULL;
             } else {
                 return getType(object.getClass (), object);
             }
    }



    public static Type getType ( Class<?> clazz ) {
        return getType(clazz, null);
    }

    public static Type getType ( Class<?> clazz, Object object ) {

        final String className = clazz.getName();
        Type type =  getType( className );

        if (type != UNKNOWN) {
            return type;
        }

        if ( clazz.isInterface() ) {
            type = INTERFACE;
        } else if (clazz.isEnum()) {
            type = ENUM;
        } else if (clazz.isArray()) {
            type = getArrayType(clazz);
        } else if (Typ.isAbstract(clazz)) {
            type = ABSTRACT;
        } else if ( className.startsWith("java")) {
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
        } else if (className.startsWith("com.sun") || className.startsWith("sun.")) {
            type = SYSTEM;
        } else if (object !=null) {


            if (object instanceof Map) {
                type = MAP;
            } else if (object instanceof Collection) {

                type = COLLECTION;
                if (object instanceof List) {
                    type = LIST;
                } else if (object instanceof Set) {
                    type = SET;
                }
            } else {
                type = INSTANCE;
            }

        } else {
            type = INSTANCE;
        }

        return type;



    }

    private static Type getArrayType(Class<?> clazz) {
        Type type;
        final Type componentType = getType(clazz.getComponentType());
        switch(componentType) {


            case BYTE:
                type = ARRAY_BYTE;
                break;

            case SHORT:
                type = ARRAY_SHORT;
                break;

            case INT:
                type = ARRAY_INT;
                break;

            case FLOAT:
                type = ARRAY_FLOAT;
                break;

            case DOUBLE:
                type = ARRAY_DOUBLE;
                break;

            case LONG:
                type = ARRAY_LONG;
                break;

            case STRING:
                type = ARRAY_STRING;
                break;

            case OBJECT:
                type = ARRAY_OBJECT;
                break;

            default:
                type = ARRAY;
                break;

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
                case "java.lang.Number":
                    return Type.NUMBER;

                case "java.lang.Class":
                    return Type.CLASS;



                case "java.lang.Void":
                    return Type.VOID;





                case "java.lang.Long":
                    return Type.LONG_WRAPPER;


                case "java.util.Set":
                case "java.util.HashSet":
                case "java.util.TreeSet":
                    return Type.SET;

                case "java.util.List":
                case "java.util.ArrayList":
                case "java.util.LinkedList":
                case "org.boon.core.value.ValueList":
                    return Type.LIST;

                case "java.util.Map":
                case "org.boon.collections.LazyMap":
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

                case "java.lang.Object":
                    return Type.OBJECT;

                case "java.io.File":
                    return Type.FILE;

                case "java.net.URI":
                    return Type.URI;

                case "java.net.URL":
                    return Type.URL;

                case "java.nio.file.Path":
                    return Type.PATH;

                case "java.util.UUID":
                    return Type.UUID;


                case "java.util.Locale":
                    return Type.LOCALE;


                case "java.util.TimeZone":
                    return Type.TIME_ZONE;

                case "java.util.Currency":
                    return Type.CURRENCY;

            }
            return Type.UNKNOWN;

    }


    public boolean  hasLength (  ) {

        switch ( this ) {
            case LIST:
            case MAP:
            case STRING:
            case CHAR_SEQUENCE:
            case SET:
            case COLLECTION:
                return true;
            default:
                return this.isArray() || this.isCollection();
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



    public  static List<Object> gatherTypes ( List<?> list ) {

        List<Object> types = new ArrayList<>();

        for (Object o : list) {
            if (o instanceof List) {
                types.add(gatherTypes((List) o));
            }
            else {
                types.add(Type.getInstanceType(o));
            }
        }

        return types;
    }



    public  static List<Object> gatherActualTypes ( List<?> list ) {

        List<Object> types = new ArrayList<>();

        for (Object o : list) {
            if (o instanceof List) {
                types.add(gatherActualTypes((List) o));
            }
            else {
                types.add(Type.getActualType(o));
            }
        }

        return types;
    }

    private static Object getActualType(Object o) {
        if (o == null) {
            return NULL;
        } else {
            return o.getClass().getSimpleName();
        }
    }

    public  static List<Type> gatherTypes ( Object... list ) {

        List<Type> types = new ArrayList();

        for (Object o : list) {
            types.add(Type.getInstanceType( o )) ;
        }

        return types;
    }

    public Type wraps() {
        return baseTypeOrWrapper;
    }


    public Type componentType() {
        return baseTypeOrWrapper == null ? OBJECT : baseTypeOrWrapper;
    }


    public boolean isArray() {
        return array;
    }


    public boolean isPrimitive() {
        return primitive;
    }
}
