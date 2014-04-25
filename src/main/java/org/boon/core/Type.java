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

import java.util.ArrayList;
import java.util.List;

public enum Type {

    INT, SHORT, BYTE, LONG, CHAR, BOOLEAN, COLLECTION, ARRAY, FLOAT, INSTANCE, BIG_INT, BIG_DECIMAL,
    DATE, NUMBER, LONG_WRAPPER, INTEGER_WRAPPER, SHORT_WRAPPER, CHAR_WRAPPER, BOOLEAN_WRAPPER,
    BYTE_WRAPPER, FLOAT_WRAPPER, DOUBLE_WRAPPER,
    INTEGER, STRING, DOUBLE, TRUE, FALSE, NULL, MAP, LIST, SET, CHAR_SEQUENCE,
    INTERFACE, ABSTRACT, OBJECT, SYSTEM, ENUM, CALENDAR, VALUE_MAP, VALUE, CLASS, URL, URI, VOID, FILE, PATH, UUID, LOCALE, TIME_ZONE;



    public  static Type getInstanceType ( Object object ) {


             if (object == null) {
                 return NULL;
             } else if (object instanceof Class) {
                 return CLASS;
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



        if ( clazz.isInterface() ) {
            type = INTERFACE;
        } else if (clazz.isEnum()) {
            type = ENUM;
        } else if (clazz.isArray()) {
            type = ARRAY;
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
                //case "org.boon.core.value.CharSequenceValue":
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
}
