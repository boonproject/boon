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

package org.boon.core.reflection;

import org.boon.*;
import org.boon.core.Conversions;
import org.boon.core.Function;
import org.boon.core.Typ;
import org.boon.core.Value;
import org.boon.core.reflection.fields.FieldAccess;
import org.boon.core.reflection.fields.FieldAccessMode;
import org.boon.core.reflection.fields.FieldsAccessor;
import org.boon.core.value.ValueContainer;
import org.boon.core.value.ValueList;
import org.boon.core.value.ValueMap;
import org.boon.core.value.ValueMapImpl;
import org.boon.primitive.Arry;
import org.boon.primitive.CharBuf;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

import static org.boon.Boon.*;
import static org.boon.Exceptions.die;
import static org.boon.Exceptions.handle;
import static org.boon.core.Conversions.coerce;
import static org.boon.core.Type.*;


/**
 * Created by rick on 12/26/13.
 * @author Richard Hightower
 * <p>
 * This class creates Java objects from java.util.Lists and java.util.Maps.
 * It is used by the JSON parser lib.
 * There are map like objects that are index overlays of the parsed JSON.
 * This set of utilties makes Java a bit more dynamic.
 * This is the core of the serialization for JSON and works in conjunction with org.boon.core.Type.
 * </p>
 */
public class MapObjectConversion {


    /**
     * Create an object from a list using the fields of the class.
     * @param list list we are creating the object from
     * @param clazz the type of object that we are creating.
     * @param <T> generic type of the object that we are creating
     * @return new object we created from a list
     */
    public static <T> T fromListUsingFields( List<Object> list, Class<T> clazz ) {

        return fromListUsingFields( false, null, FieldAccessMode.FIELD_THEN_PROPERTY.create( false ), list,  clazz, null );

    }

    /**
     * Create an object from a list using the fields of the class.
     * This relies on the order of the fields as defined in the class.
     * @param respectIgnore ignore things marked with @JsonIgnore and transient values
     * @param view the view of the object which can ignore certain fields given certain views
     * @param fieldsAccessor how we are going to access the fields (by field, by property, combination)
     * @param list list we are creating the object from
     * @param clazz the type of object that we are creating.
     * @param ignoreSet a set of properties to ignore
     * @param <T> generic type we are returning
     * @return new object
     */
    public static <T> T fromListUsingFields( boolean respectIgnore, String view,
                                             FieldsAccessor fieldsAccessor, List<Object> list, Class<T> clazz ,
                                             Set<String> ignoreSet) {

        /* Get the fields from the object. */
        Map<String, FieldAccess> fieldMap = fieldsAccessor.getFields( clazz );
        List<Field> fields = Reflection.getFields( clazz );

        /* current item in the list. */
        Object item;
        /* Current  type of the field arg. */
        Class<?> fieldType;
        /* Current field. */
        Field field;

        /* Create a new instance of the object. */
        T toObject = Reflection.newInstance( clazz );

        /* Loop through the fields. */
        loop:
        for ( int index = 0; index < fields.size(); index++ ) {

            /* Get the field, param type, list item and field accessor. */
            field = fields.get( index );
            fieldType = field.getType();
            item = list.get( index );
            FieldAccess fieldAccess = fieldMap.get( field.getName() );


            /* if the field is a list and the item is a list then try to create the list and convert
            the value.
            REFACTOR: It seems like this same code to create a list from generic information is in
            several places. This belongs in Conversions or some other class that knows
            how to copy things into a field. Specifically this could be made more generic to work with
            not just list but all types of collections.
            REFACTOR:
             */
            if ( Typ.isList( fieldType ) && item instanceof List ) {
                List<Object> itemList = ( List<Object> ) item;
                if ( itemList.size() > 0 && itemList.get( 0 ) instanceof List ) {
                    Class<?> componentType = fieldAccess.getComponentClass();
                    List newList = new ArrayList( itemList.size() );

                    for ( Object o : itemList ) {
                        List fromList = ( List ) o;
                        newList.add( fromList(respectIgnore, view, fieldsAccessor,  fromList, componentType, ignoreSet ) );
                    }
                    fieldAccess.setValue( toObject, newList );

                }
            /* If the field type and item type match then just inject the item.*/
            } else if ( fieldType.isInstance( item ) ) {

                fieldAccess.setObject( toObject, item );
            }
            else if (item instanceof Map) {

                /* if the item was a map then convert it to the right object.
                * REFACTOR: Looks like we have a generic function to convert a field into a map
                * I wonder if this works with non instances too.
                * Again we do this in several places. It seems like there can be a lot of consolidation.
                * REFACTOR
                * */
                setFieldValueFromMap( respectIgnore, view, fieldsAccessor, ignoreSet, toObject, fieldAccess, item );

            } else {
                /* This is sort of chicken shit, at this point we are just letting the system
                decide how to convert it, if it can convert it.
                setValue will try to inject it based on basic type matching and then finally
                give up and call Conversion coerce.
                REFACTOR: Seems we need a more general way to inject items into a field and convert/coerce them.
                REFACTOR
                 */
                fieldAccess.setValue( toObject, item );
            }
        }

        return toObject;

    }


    /** Convert an item from a list into a class using the classes constructor.
     *
     * REFACTOR: Can't this just be from collection?
     * REFACTOR
     *
     * @param argList list if arguments
     * @param clazz  the type of the object we are creating
     * @param <T> generics
     * @return the new object that we just created.
     */
    public static <T> T fromList( List<?> argList, Class<T> clazz ) {
          return fromList( FieldAccessMode.FIELD_THEN_PROPERTY.create( false ), argList, clazz );
    }


    /** Convert an item from a list into a class using the classes constructor.
     *
     * REFACTOR: Can't this just be from collection?
     * REFACTOR
     *
     * @param respectIgnore  honor @JsonIgnore, transients, etc. of the field
     * @param view honor views for fields
     * @param fieldsAccessor how we are going to access the fields (by field, by property, combination)
     * @param argList list if arguments
     * @param clazz  the type of the object we are creating
     * @param ignoreSet a set of properties to ignore
     * @param <T> generics
     * @return the new object that we just created.
     */
    public static <T> T fromList( boolean respectIgnore, String view, FieldsAccessor fieldsAccessor,
                                  List<?> argList, Class<T> clazz, Set<String> ignoreSet ) {

        /* Size of the arguments. */
        int size = argList.size();

        /* List to hold items that we coerce into parameter types. */
        List<Object> convertedArguments = new ArrayList<>( argList );

        /* Meta data holder of the class. */
        ClassMeta<T> classMeta = ClassMeta.classMeta( clazz );

        /* The constructor to match. */
        ConstructorAccess<T> constructorToMatch = null;

        /* The final arguments. */
        Object[] finalArgs = null;


        boolean[] flag = new boolean[1];



        try {


            constructorToMatch = lookupConstructorMeta(respectIgnore,
                    view, fieldsAccessor, ignoreSet, size,
                    convertedArguments, classMeta, constructorToMatch, flag, false);

            if (constructorToMatch == null) {
                constructorToMatch = lookupConstructorMeta(respectIgnore,
                        view, fieldsAccessor, ignoreSet, size,
                        convertedArguments, classMeta, constructorToMatch, flag, true);
            }




            /* If we were not able to match then we bail. */
            if ( constructorToMatch != null ) {
                finalArgs = convertedArguments.toArray( new Object[convertedArguments.size()] );
                return constructorToMatch.create( finalArgs );
            } else {
                return (T) die(Object.class, "Unable to convert list", convertedArguments, "into", clazz);
            }

            /* Catch all of the exceptions and try to report why this failed.
            * Since we are doing reflection and a bit of "magic", we have to be clear as to why/how things failed.
            * */
        } catch ( Exception e ) {


            if (constructorToMatch != null)  {


                CharBuf buf = CharBuf.create(200);
                buf.addLine();
                buf.multiply('-', 10).add("FINAL ARGUMENTS").multiply('-', 10).addLine();
                if (finalArgs!=null) {
                    for (Object o : finalArgs) {
                        buf.puts("argument type    ", className(o));
                    }
                }


                buf.multiply('-', 10).add("CONSTRUCTOR").add(constructorToMatch).multiply('-', 10).addLine();
                buf.multiply('-', 10).add("CONSTRUCTOR PARAMS").multiply('-', 10).addLine();
                for (Class<?> c : constructorToMatch.parameterTypes()) {
                        buf.puts("constructor type ", c);
                }

                buf.multiply('-', 35).addLine();

                if (Boon.debugOn()) {
                    puts(buf);
                }



                buf.addLine("PARAMETER TYPES");
                buf.add(Lists.list(constructorToMatch.parameterTypes())).addLine();

                buf.addLine("ORIGINAL TYPES PASSED");
                buf.add(gatherTypes(convertedArguments)).addLine();

                buf.add(gatherActualTypes(convertedArguments)).addLine();

                buf.addLine("CONVERTED ARGUMENT TYPES");
                buf.add(gatherTypes(convertedArguments)).addLine();
                buf.add(gatherActualTypes(convertedArguments)).addLine();

                Boon.error( e, "unable to create object based on constructor", buf );


                return ( T ) handle(Object.class, e, buf.toString());
            } else {
                return ( T ) handle(Object.class, e,
                        "\nlist args after conversion", convertedArguments, "types",
                        gatherTypes(convertedArguments),
                        "\noriginal args", argList,
                        "original types", gatherTypes(argList));

            }
        }

    }

    private static <T> ConstructorAccess<T> lookupConstructorMeta(boolean respectIgnore,
                                                                  String view, FieldsAccessor fieldsAccessor,
                                                                  Set<String> ignoreSet, int size,
                                                                  List<Object> convertedArguments,
                                                                  ClassMeta<T> classMeta,
                                                                  ConstructorAccess<T> constructorToMatch,
                                                                  boolean[] flag, boolean loose) {
    /* Iterate through the constructors and see if one matches the arguments passed after coercion. */
        loop:
        for ( ConstructorAccess constructor : classMeta.constructors() ) {

            /* Get the parameters on the constructor and see if the size matches what was passed. */
            Class[] parameterTypes = constructor.parameterTypes();
            if ( parameterTypes.length == size ) {

                /* Iterate through each parameter and see if it can be converted. */
                for ( int index = 0; index < size; index++ ) {
                    /* The match and convert does the bulk of the work. */
                    if ( !matchAndConvertArgs( respectIgnore, view,
                            fieldsAccessor, convertedArguments, constructor,
                            parameterTypes, index, ignoreSet, flag, loose ) ) continue loop;
                }
                constructorToMatch = constructor;
            }
        }
        return constructorToMatch;
    }


    /** Convert an item from a list into a class using the classes constructor.
     *
     * REFACTOR: Can't this just be from collection?
     * REFACTOR
     *
     * @param fieldsAccessor how we are going to access the fields (by field, by property, combination)
     * @param argList list if arguments
     * @param clazz  the type of the object we are creating
     * @param <T> generics
     * @return the new object that we just created.
     */
    public static <T> T fromList( FieldsAccessor fieldsAccessor, List<?> argList, Class<T> clazz ) {
        return fromList( false, null, fieldsAccessor, argList, clazz, null );
    }

    /**
     * This converts/coerce a constructor argument to the given parameter type.
     *
     * REFACTOR:
     * This method was automatically refactored and its functionality gets duplicated in a few places.
     * Namely Invoker lib. It needs to be documented. Refactored to use org.boon.core.Type.
     * And code coverage. I have used it on several projects and have modified to work on
     * edge cases for certain customers and have not updated the unit test.
     * This method is beastly and important. It is currently 250 lines of code.
     * It started off small, and kept getting added to. It needs love, but it was a bitch to write.
     * REFACTOR
     *
     * @param view honor views for fields
     * @param fieldsAccessor how we are going to access the fields (by field, by property, combination)
     * @param ignoreSet a set of properties to ignore
     * @param respectIgnore  honor @JsonIgnore, transients, etc. of the field
     * @param convertedArgumentList   arguments being converted to match parameter types
     * @param methodAccess    constructor
     * @param parameterTypes   parameterTypes
     * @param index           index of argument
     * @return   true or false
     */
    public static boolean matchAndConvertArgs( boolean respectIgnore,
                                               String view,
                                               FieldsAccessor fieldsAccessor,
                                               List<Object> convertedArgumentList,
                                               BaseAccess methodAccess,
                                               Class[] parameterTypes,
                                               int index,
                                               Set<String> ignoreSet,
                                               boolean[] flag, boolean loose) {


        Object value;

        try {

            Class parameterClass;
            Object item;

            parameterClass = parameterTypes[index];
            item = convertedArgumentList.get( index );


            final org.boon.core.Type parameterType = org.boon.core.Type.getType(parameterClass);


            if ( item instanceof ValueContainer ) {
                item = ( ( ValueContainer ) item ).toValue();

                convertedArgumentList.set( index, item );
            }




            if (item == null) {
                return true;
            }

            switch (parameterType) {
                case INT:
                case SHORT:
                case BYTE:
                case BOOLEAN:
                case CHAR:
                case FLOAT:
                case DOUBLE:
                case LONG:
                    if (item == null) {
                        return false;
                    }


                case INTEGER_WRAPPER:
                case BYTE_WRAPPER:
                case SHORT_WRAPPER:
                case BOOLEAN_WRAPPER:
                case CHAR_WRAPPER:
                case FLOAT_WRAPPER:
                case DOUBLE_WRAPPER:
                case CHAR_SEQUENCE:
                case NUMBER:
                case LONG_WRAPPER:
                    if (!loose && item instanceof CharSequence) {
                        return false;
                    }


                    value = Conversions.coerceWithFlag(parameterType, parameterClass, flag, item );

                    if (flag[0] == false) {
                        return false;
                    }
                    convertedArgumentList.set( index, value );
                    return true;



                case CLASS:
                case ENUM:
                case STRING:
                    if (!loose && !(item instanceof CharSequence)) {
                        return false;
                    }


                    value = Conversions.coerceWithFlag(parameterType, parameterClass, flag, item );

                    if (flag[0] == false) {
                        return false;
                    }
                    convertedArgumentList.set( index, value );
                    return true;

                case MAP:
                case VALUE_MAP:

                    if (item instanceof Map) {
                        Map itemMap = (Map)item;

                    /* This code creates a map based on the parameterized types of the constructor arg.
                     *  This does ninja level generics manipulations and needs to be captured in some
                     *  reusable way.
                      * */
                    Type type = methodAccess.getGenericParameterTypes()[index];
                    if ( type instanceof ParameterizedType ) {
                        ParameterizedType pType = (ParameterizedType) type;
                        Class<?> keyType = (Class<?>) pType.getActualTypeArguments()[0];

                        Class<?> valueType = (Class<?>) pType.getActualTypeArguments()[1];


                        Map newMap = Conversions.createMap(parameterClass, itemMap.size());


                    /* Iterate through the map items and convert the keys/values to match
                    the parameterized constructor parameter args.
                     */

                        for (Object o : itemMap.entrySet()) {
                            Map.Entry entry = (Map.Entry) o;

                            Object key = entry.getKey();
                            value = entry.getValue();

                            key = ValueContainer.toObject(key);

                            value = ValueContainer.toObject(value);


                        /* Here is the actual conversion from a list or a map of some object.
                        This can be captured in helper method the duplication is obvious.
                         */
                            if (value instanceof List) {
                                value = fromList(respectIgnore, view, fieldsAccessor, (List) value, valueType, ignoreSet);

                            } else if (value instanceof Map) {
                                value = fromMap(respectIgnore, view, fieldsAccessor, (Map) value, valueType, ignoreSet);

                            } else {
                                value = coerce(valueType, value);
                            }


                            if (key instanceof List) {
                                key = fromList(respectIgnore, view, fieldsAccessor, (List) key, keyType, ignoreSet);

                            } else if (value instanceof Map) {
                                key = fromMap(respectIgnore, view, fieldsAccessor, (Map) key, keyType, ignoreSet);

                            } else {
                                key = coerce(keyType, key);
                            }

                            newMap.put(key, value);
                        }
                        convertedArgumentList.set(index, newMap);
                        return true;
                        }
                    }
                    break;
                case INSTANCE:
                    if ( parameterClass.isInstance( item ) ) {
                        return true;
                    }

                    if (item instanceof Map) {
                        item = fromMap( respectIgnore, view, fieldsAccessor, ( Map<String, Object> ) item, parameterClass, ignoreSet );
                        convertedArgumentList.set( index, item );
                        return true;
                    } else if ( item instanceof List ) {

                        List<Object> listItem = null;

                        listItem =      ( List<Object> ) item;

                        value = fromList(respectIgnore, view, fieldsAccessor, listItem, parameterClass, ignoreSet );

                        convertedArgumentList.set( index, value );
                        return true;

                    } else {
                        convertedArgumentList.set( index, coerce( parameterClass, item ) );
                        return true;
                    }
                    //break;
                case INTERFACE:
                case ABSTRACT:
                    if ( parameterClass.isInstance( item ) ) {
                        return true;
                    }

                    if (item instanceof Map) {

                        /** Handle conversion of user define interfaces. */
                        String className = (String) ((Map) item).get("class");
                        if (className != null) {
                            item = fromMap(respectIgnore, view, fieldsAccessor, (Map<String, Object>) item, Reflection.loadClass(className), ignoreSet);
                            convertedArgumentList.set(index, item);
                            return true;
                        } else {
                            return false;
                        }

                    }
                    break;


                case SET:
                case COLLECTION:
                case LIST:
                    if (item instanceof List ) {

                        List<Object> itemList = ( List<Object> ) item;

                        /* Items have stuff in it, the item is a list of lists.
                         * This is like we did earlier with the map.
                         * Here is some more ninja generics Java programming that needs to be captured in one place.
                         * */
                        if ( itemList.size() > 0 && (itemList.get( 0 ) instanceof List ||
                                itemList.get(0) instanceof ValueContainer)  ) {

                            /** Grab the generic type of the list. */
                            Type type = methodAccess.getGenericParameterTypes()[index];

                            /*  Try to pull the generic type information out so you can create
                               a strongly typed list to inject.
                             */
                            if ( type instanceof ParameterizedType ) {
                                ParameterizedType pType = ( ParameterizedType ) type;


                                Class<?> componentType;
                                if (! (pType.getActualTypeArguments()[0] instanceof Class)) {
                                    componentType = Object.class;
                                } else {
                                    componentType = (Class<?>) pType.getActualTypeArguments()[0];
                                }

                                Collection newList =  Conversions.createCollection( parameterClass, itemList.size() );

                                for ( Object o : itemList ) {
                                    if ( o instanceof ValueContainer ) {
                                        o = ( ( ValueContainer ) o ).toValue();
                                    }

                                    if (componentType==Object.class) {
                                        newList.add(o);
                                    } else {

                                        List fromList = ( List ) o;
                                        o = fromList( respectIgnore, view, fieldsAccessor, fromList, componentType, ignoreSet );
                                        newList.add( o );
                                    }
                                }
                                convertedArgumentList.set( index, newList );
                                return true;

                            }
                    } else {

                        /* Just a list not a list of lists so see if it has generics and pull out the
                        * type information and created a strong typed list. This looks a bit familiar.
                        * There is a big opportunity for some reuse here. */
                        Type type = methodAccess.getGenericParameterTypes()[index];
                        if ( type instanceof ParameterizedType ) {
                            ParameterizedType pType = ( ParameterizedType ) type;

                            Class<?> componentType = pType.getActualTypeArguments()[0] instanceof Class ? (Class<?>) pType.getActualTypeArguments()[0] : Object.class;

                            Collection newList =  Conversions.createCollection( parameterClass, itemList.size() );


                            for ( Object o : itemList ) {
                                if ( o instanceof ValueContainer ) {
                                    o = ( ( ValueContainer ) o ).toValue();
                                }
                                if (o instanceof List) {

                                    if (componentType != Object.class) {

                                        List fromList = ( List ) o;
                                        o = fromList(fieldsAccessor, fromList, componentType);
                                    }
                                    newList.add( o );
                                } else if (o instanceof Map) {
                                    Map fromMap = ( Map ) o;
                                    o = fromMap(respectIgnore, view, fieldsAccessor, fromMap, componentType, ignoreSet);
                                    newList.add( o );

                                } else {
                                    newList.add( Conversions.coerce(componentType, o));
                                }
                            }
                            convertedArgumentList.set( index, newList );
                            return true;

                        }

                    }
                }
                return false;


                default:
                    final org.boon.core.Type itemType = org.boon.core.Type.getInstanceType(item);

                    switch (itemType) {
                        case LIST:
                            convertedArgumentList.set(index, fromList(respectIgnore, view, fieldsAccessor, (List<Object>) item, parameterClass, ignoreSet));
                        case MAP:
                        case VALUE_MAP:
                            convertedArgumentList.set(index, fromMap(respectIgnore, view, fieldsAccessor, (Map<String, Object>) item, parameterClass, ignoreSet));

                        case NUMBER:
                        case BOOLEAN:
                        case INT:
                        case SHORT:
                        case BYTE:
                        case FLOAT:
                        case DOUBLE:
                        case LONG:
                        case DOUBLE_WRAPPER:
                        case FLOAT_WRAPPER:
                        case INTEGER_WRAPPER:
                        case SHORT_WRAPPER:
                        case BOOLEAN_WRAPPER:
                        case BYTE_WRAPPER:
                        case LONG_WRAPPER:
                        case CLASS:
                        case VALUE:
                            value = Conversions.coerceWithFlag( parameterClass, flag, item );

                            if (flag[0] == false) {
                                return false;
                            }
                            convertedArgumentList.set( index, value );
                            return true;



                        case CHAR_SEQUENCE:
                        case STRING:

                            value = Conversions.coerceWithFlag( parameterClass, flag, item );

                            if (flag[0] == false) {
                                return false;
                            }
                            convertedArgumentList.set( index, value );
                            return true;



                    }



            }


            if ( parameterClass.isInstance( item ) ) {
                return true;
            }


        } catch (Exception ex) {
            Boon.error(ex, "PROBLEM WITH oldMatchAndConvertArgs",
                    "respectIgnore", respectIgnore, "view", view,
                    "fieldsAccessor", fieldsAccessor, "list", convertedArgumentList,
                    "constructor", methodAccess, "parameters", parameterTypes,
                    "index", index, "ignoreSet", ignoreSet);
            return false;
        }

        return false;
    }



    /** Convert an object to a list.
     *
     * @param object the object we want to convert to a list
     * @return new list from an object
     */
    public static List<?> toList( Object object) {

        org.boon.core.Type instanceType = org.boon.core.Type.getInstanceType(object);

        switch (instanceType) {
            case NULL:
                return Lists.list((Object)null);
            case ARRAY:
                return Conversions.toList(object);
            case INSTANCE:
                if (Reflection.respondsTo(object, "toList")) {
                    return (List<?>) Reflection.invoke(object, "toList");
                }
                break;
        }
        return Lists.list(object);
    }


    /**
     * From map.
     * @param map map to create the object from.
     * @param clazz the new instance type
     * @param <T> generic type capture
     * @return new object
     */
    @SuppressWarnings( "unchecked" )
    public static <T> T fromMap( Map<String, Object> map, Class<T> clazz ) {
        return fromMap( false, null, FieldAccessMode.FIELD_THEN_PROPERTY.create( true ), map, clazz, null );

    }




    /**
     * fromMap converts a map into a java object.
     * @param map map to create the object from.
     * @param clazz  the new instance type
     * @param excludeProperties the properties to exclude
     * @param <T> generic type capture
     * @return the new object
     */
    @SuppressWarnings( "unchecked" )
    public static <T> T fromMap( Map<String, Object> map, Class<T> clazz, String... excludeProperties ) {
        Set<String> ignoreProps = excludeProperties.length > 0 ? Sets.set(excludeProperties) :  null;
        return fromMap( false, null, FieldAccessMode.FIELD_THEN_PROPERTY.create( true ), map,  clazz, ignoreProps );

    }


    /**
     * fromMap converts a map into a Java object.
     * This version will see if there is a class parameter in the map, and dies if there is not.
     * @param map map to create the object from.
     * @return new object
     */
    public static Object fromMap( Map<String, Object> map ) {
        String clazz = (String) map.get( "class" );
        Class cls = Reflection.loadClass( clazz );
        return fromMap( false, null, FieldAccessMode.FIELD_THEN_PROPERTY.create( true ), map, cls  , null );
    }


    /**
     * fromMap converts a map into a java object
     * @param respectIgnore honor @JsonIgnore, transients, etc. of the field
     * @param view the view of the object which can ignore certain fields given certain views
     * @param fieldsAccessor how we are going to access the fields (by field, by property, combination)
     * @param map map to create the object from.
     * @param cls class type of new object
     * @param ignoreSet a set of properties to ignore
     * @param <T> map to create teh object from.
     * @return new object of type cls <T>
     */
    @SuppressWarnings("unchecked")
    public static <T> T fromMap( boolean respectIgnore, String view, FieldsAccessor fieldsAccessor, Map<String, Object> map, Class<T> cls, Set<String> ignoreSet ) {


        T toObject = Reflection.newInstance( cls );
        Map<String, FieldAccess> fields = fieldsAccessor.getFields( toObject.getClass() );
        Set<Map.Entry<String, Object>> mapKeyValuesEntrySet = map.entrySet();


        /* Iterate through the map keys/values. */
        for ( Map.Entry<String, Object> mapEntry : mapKeyValuesEntrySet ) {

            /* Get the field name. */
            String key = mapEntry.getKey();

            if ( ignoreSet != null ) {
                if ( ignoreSet.contains( key ) ) {
                    continue;
                }
            }

            /* Get the field and if it missing then ignore this map entry. */
            FieldAccess field = fields.get( key );


            if ( field == null ) {
                continue;
            }



            /* Check the view if it is active. */
            if ( view != null ) {
                if ( !field.isViewActive( view ) ) {
                    continue;
                }
            }


            /* Check respects ignore is active.
             * Then needs to be a chain of responsibilities.
             * */
            if ( respectIgnore ) {
                if ( field.ignore() ) {
                    continue;
                }
            }

            /* Get the value from the map. */
            Object value = mapEntry.getValue();


            /* If the value is a Value (a index overlay), then convert ensure it is not a container and inject
            it into the field, and we are done so continue.
             */
            if ( value instanceof Value ) {
                if ( ( ( Value ) value ).isContainer() ) {
                    value = ( ( Value ) value ).toValue();
                } else {
                    field.setFromValue( toObject, ( Value ) value );
                    continue;
                }
            }

            /* If the value is null, then inject an null value into the field.
            * Notice we do not check to see if the field is a primitive, if
            * it is we die which is the expected behavior.
            */
            if ( value == null ) {
                field.setObject( toObject, null );
                continue;
            }

            /* if the value's type and the field type are the same or
            the field just takes an object, then inject what we have as is.
             */
            if ( value.getClass() == field.type() || field.type() == Object.class) {
                field.setValue(toObject, value);
            } else if ( Typ.isBasicType( value ) ) {

                field.setValue(toObject, value);
            }


            /* See if it is a map<string, object>, and if it is then process it.
             *  REFACTOR:
             *  It looks like we are using some utility classes here that we could have used in
             *  oldMatchAndConvertArgs.
             *  REFACTOR
              * */
            else if ( value instanceof Map ) {
                setFieldValueFromMap(respectIgnore, view, fieldsAccessor, ignoreSet, toObject, field, value);
            } else if ( value instanceof Collection ) {
                /*It is a collection so process it that way. */
                processCollectionFromMapUsingFields( respectIgnore, view, fieldsAccessor, toObject, field, ( Collection ) value, ignoreSet );
            } else if ( value instanceof Map[] ) {
                /* It is an array of maps so, we need to process it as such. */
                processArrayOfMaps(respectIgnore, view, fieldsAccessor, toObject, field, value, ignoreSet );
            } else {
                /* If we could not determine how to convert it into some field
                object then we just go ahead an inject it using setValue which
                will call Conversion.coerce.
                 */
                field.setValue( toObject, value );
            }

        }

        return toObject;

    }


    /**
     * Inject a map into an object's field.
     * @param respectIgnore  honor @JsonIgnore, transients, etc. of the field
     * @param view the view of the object which can ignore certain fields given certain views
     * @param fieldsAccessor how we are going to access the fields (by field, by property, combination)
     * @param ignoreSet a set of properties to ignore
     * @param toObject object we are copying value into
     * @param field field we are injecting a value into
     * @param value value we are trying to inject which might need coercion
     * @param <T> generic type
     */
    private static <T> void setFieldValueFromMap( boolean respectIgnore, String view, FieldsAccessor fieldsAccessor,
                                                  Set<String> ignoreSet, T toObject, FieldAccess field, Object value ) {


        Class<?> fieldClassType = field.type();

        Map mapInner = (Map)value;

        /* Is the field not a map. */
        if ( !Typ.isMap( fieldClassType ) )  {

            if ( !fieldClassType.isInterface() && !Typ.isAbstract( fieldClassType ) ) {
                value = fromMap( respectIgnore, view, fieldsAccessor, mapInner, field.type(), ignoreSet );

            } else {
                String  className = (String) ((Map) value).get( "class" );
                if (className != null)  {
                    value = fromMap( respectIgnore, view, fieldsAccessor, mapInner, Reflection.loadClass( className ), ignoreSet );
                } else {
                    value = null;
                }
            }

           /*
           REFACTOR:
           This is at least the third time that I have seen this code in the class.
            It was either cut and pasted or I forgot I wrote it three times.
           REFACTOR:
             */
        }  else if (Typ.isMap( fieldClassType ))  {
            Class keyType = (Class)field.getParameterizedType().getActualTypeArguments()[0];
            Class valueType = (Class)field.getParameterizedType().getActualTypeArguments()[1];

            Set<Map.Entry> set = mapInner.entrySet();
            Map newMap = new LinkedHashMap(  );

            for (Map.Entry entry : set) {
                Object evalue = entry.getValue();

                Object key = entry.getKey();

                if (evalue instanceof ValueContainer) {
                    evalue = ((ValueContainer) evalue).toValue();
                }

                key  = Conversions.coerce( keyType, key );
                evalue = Conversions.coerce( valueType, evalue );
                newMap.put( key, evalue );
            }

            value  = newMap;

        }

        field.setValue(toObject, value);

    }



    /**
     * Creates an object from a value map.
     * @param respectIgnore  honor @JsonIgnore, transients, etc. of the field
     * @param view the view of the object which can ignore certain fields given certain views
     * @param fieldsAccessor how we are going to access the fields (by field, by property, combination)
     * @param ignoreSet a set of properties to ignore
     * @return new object from value map
     */
    @SuppressWarnings("unchecked")
    private static Object fromValueMap(boolean respectIgnore, String view,
            final FieldsAccessor fieldsAccessor,
            final Map<String, Value> valueMap, Set<String> ignoreSet  ) {

        try {
            String className = valueMap.get( "class" ).toString();
            Class<?> cls = Reflection.loadClass( className );
            return fromValueMap( respectIgnore, view, fieldsAccessor, valueMap, cls, ignoreSet );
        } catch ( Exception ex ) {
            return handle(Object.class, sputs("fromValueMap", "map", valueMap, "fieldAccessor", fieldsAccessor), ex);
        }
    }


    /**
     * Creates an object from a value map.
     *
     * This does some special handling to take advantage of us using the value map so it avoids creating
     * a bunch of array objects and collections. Things you have to worry about when writing a
     * high-speed JSON serializer.
     * @param respectIgnore  honor @JsonIgnore, transients, etc. of the field
     * @param view the view of the object which can ignore certain fields given certain views
     * @param fieldsAccessor how we are going to access the fields (by field, by property, combination)
     * @param cls the new type
     * @param ignoreSet a set of properties to ignore
     * @return new object from value map
     */
    @SuppressWarnings("unchecked")
    public static <T> T fromValueMap( boolean respectIgnore, String view, final FieldsAccessor fieldsAccessor,
                                      final Map<String, Value> valueMap,
                                      final Class<T> cls, Set<String> ignoreSet ) {

        T newInstance = Reflection.newInstance( cls );
        ValueMap map = ( ValueMap ) ( Map ) valueMap;


        Map<String, FieldAccess> fields = fieldsAccessor.getFields( cls);
        Map.Entry<String, Object>[] entries;

        FieldAccess field = null;
        String fieldName = null;
        Map.Entry<String, Object> entry;


        int size;


        /* if the map is not hydrated get its entries right form the array to avoid collection creations. */
        if ( !map.hydrated() ) {
            size = map.len();
            entries = map.items();
        } else {
            size = map.size();
            entries = ( Map.Entry<String, Object>[] ) map.entrySet().toArray( new Map.Entry[size] );
        }

        /* guard. We should check if this is still needed.
        * I might have added it for debugging and forgot to remove it.*/
        if ( size == 0 || entries == null ) {
            return newInstance;
        }


        /* Iterate through the entries. */
        for ( int index = 0; index < size; index++ ) {
            Object value = null;
            try {

                entry    = entries[index];

                fieldName = entry.getKey();


                if ( ignoreSet != null ) {
                    if ( ignoreSet.contains( fieldName ) ) {
                        continue;
                    }
                }

                field = fields.get( fieldName );


                if ( field == null ) {
                    continue;
                }

                if ( view != null ) {
                    if ( !field.isViewActive( view ) ) {
                        continue;
                    }
                }


                if ( respectIgnore ) {
                    if ( field.ignore() ) {
                        continue;
                    }
                }


                value = entry.getValue();


                if ( value instanceof Value ) {
                    fromValueMapHandleValueCase( respectIgnore, view, fieldsAccessor, newInstance, field, ( Value ) value, ignoreSet );
                } else {
                    fromMapHandleNonValueCase( respectIgnore, view, fieldsAccessor, newInstance, field, value, ignoreSet );
                }
            }catch (Exception ex) {
                return (T) handle(Object.class, ex, "fieldName", fieldName, "of class", cls, "had issues for value", value, "for field", field);
            }

        }

        return newInstance;
    }

    /**
     *
     * Gets called by  fromValueMap
     * This does some special handling to take advantage of us using the value map so it avoids creating
     * a bunch of array objects and collections. Things you have to worry about when writing a
     * high-speed JSON serializer.
     * @param respectIgnore  honor @JsonIgnore, transients, etc. of the field
     * @param view the view of the object which can ignore certain fields given certain views
     * @param fieldsAccessor how we are going to access the fields (by field, by property, combination)
     * @param ignoreSet a set of properties to ignore
     * @param field  field we want to inject something into
     * @param newInstance the thing we want to inject a field value into
     * @param objectValue object value we want to inject into the field.
     * @return new object from value map
     */
    private static <T> void fromMapHandleNonValueCase( boolean respectIgnore, String view,
                                                       FieldsAccessor fieldsAccessor,
                                                       T newInstance, FieldAccess field,
                                                       Object objectValue, Set<String> ignoreSet ) {
        try {
            if ( objectValue instanceof Map ) {
                Class<?> clazz = field.type();
                if ( !clazz.isInterface() && !Typ.isAbstract( clazz ) ) {
                    objectValue = fromValueMap( respectIgnore, view, fieldsAccessor, ( Map<String, Value> ) objectValue, field.type(), ignoreSet );
                } else {
                    objectValue = fromValueMap( respectIgnore, view, fieldsAccessor, ( Map<String, Value> ) objectValue, ignoreSet );
                }
                field.setValue(newInstance, objectValue);
            } else if ( objectValue instanceof Collection ) {
                handleCollectionOfValues( respectIgnore, view, fieldsAccessor, newInstance, field,
                        ( Collection<Value> ) objectValue, ignoreSet );
            } else {
                field.setValue( newInstance, objectValue );
            }
        } catch ( Exception ex ) {
            handle(sputs("Problem handling non value case of fromValueMap", "field", field.name(),
                    "fieldType", field.type().getName(), "object from map", objectValue), ex);
        }
    }



    /**
     *
     * Gets called by  fromValueMap
     * This does some special handling to take advantage of us using the value map so it avoids creating
     * a bunch of array objects and collections. Things you have to worry about when writing a
     * high-speed JSON serializer.
     * @param respectIgnore  honor @JsonIgnore, transients, etc. of the field
     * @param view the view of the object which can ignore certain fields given certain views
     * @param fieldsAccessor how we are going to access the fields (by field, by property, combination)
     * @param ignoreSet a set of properties to ignore
     * @param field  field we want to inject something into
     * @param newInstance the thing we want to inject a field value into
     * @param value object value of type Value we want to inject into the field.
     * @return new object from value map
     */
    private static <T> void fromValueMapHandleValueCaseOLD(
            boolean respectIgnore, String view,
            FieldsAccessor fieldsAccessor, T newInstance, FieldAccess field, Value value, Set<String> ignoreSet  ) {
        Object objValue = null;

        try {
            if ( value.isContainer() ) {
                objValue = value.toValue();
                if ( objValue instanceof Map ) {
                    Map<String, Value> mapObjectValue = ( Map<String, Value> ) objValue;
                    Class<?> clazz = field.type();

                    if (clazz == Object.class) {

                        final Value aClass = mapObjectValue.get("class");
                        if (aClass!=null) {
                            String strClass = aClass.stringValue();
                            clazz = Class.forName(strClass);
                        }
                    }
                    if ( !clazz.isInterface() && !Typ.isAbstract( clazz ) ) {
                        objValue = fromValueMap( respectIgnore, view, fieldsAccessor,
                                mapObjectValue, clazz, ignoreSet );
                    } else {

                        /*
                        REFACTOR:
                        here is this same generics code again. This is the fourth time in this class
                        that I have seen this very similar code base.
                        REFACTOR
                        Time to break out simean.
                         */
                        if (Typ.isMap( field.type() ))  {
                            Class keyType = (Class)field.getParameterizedType().getActualTypeArguments()[0];
                            Class valueType = (Class)field.getParameterizedType().getActualTypeArguments()[1];

                            Map mapInner = (Map)objValue;
                            Set<Map.Entry> set = mapInner.entrySet();
                            Map newMap = new LinkedHashMap(  );

                            for (Map.Entry entry : set) {
                                Object evalue = entry.getValue();

                                Object key = entry.getKey();

                                if (evalue instanceof ValueContainer) {
                                    evalue = ((ValueContainer) evalue).toValue();
                                }

                                key  = Conversions.coerce( keyType, key );
                                evalue = Conversions.coerce( valueType, evalue );
                                newMap.put( key, evalue );
                            }

                            objValue = newMap;

                        } else {
                            objValue = fromValueMap( respectIgnore, view, fieldsAccessor, ( Map<String, Value> ) objValue, ignoreSet );
                        }
                    }
                    field.setValue(newInstance, objValue);
                } else if ( objValue instanceof Collection ) {
                    handleCollectionOfValues( respectIgnore, view, fieldsAccessor, newInstance, field,
                            ( Collection<Value> ) objValue, ignoreSet );
                }
            } else {
                field.setFromValue( newInstance, value );
            }
        } catch ( Exception ex ) {
            handle(sputs("Problem handling non value case of fromValueMap", "field", field.name(),
                    "fieldType", field.type().getName(), "object from map", "objValue", objValue, "value", value), ex);

        }
    }



    /**
     *
     * Gets called by  fromValueMap
     * This does some special handling to take advantage of us using the value map so it avoids creating
     * a bunch of array objects and collections. Things you have to worry about when writing a
     * high-speed JSON serializer.
     * @param respectIgnore  honor @JsonIgnore, transients, etc. of the field
     * @param view the view of the object which can ignore certain fields given certain views
     * @param fieldsAccessor how we are going to access the fields (by field, by property, combination)
     * @param ignoreSet a set of properties to ignore
     * @param field  field we want to inject something into
     * @param newInstance the thing we want to inject a field value into
     * @param value object value of type Value we want to inject into the field.
     * @return new object from value map
     */
    private static <T> void fromValueMapHandleValueCase(
            boolean respectIgnore, String view,
            FieldsAccessor fieldsAccessor, T newInstance, FieldAccess field, Value value, Set<String> ignoreSet  ) {


           Object objValue =
                ValueContainer.toObject(value);

            Class<?> clazz = field.type();
//
//            if (field.typeEnum()==INSTANCE && value.type() == MAP) {
//                field.setObject(newInstance, fromValueMap(respectIgnore, view, fieldsAccessor, ( Map<String, Value> ) objValue, clazz, ignoreSet ));
//
//                return;
//            } else if (field.typeEnum()==INSTANCE && value.type() == LIST) {
//                field.setObject(newInstance, fromList(respectIgnore, view, fieldsAccessor, ( List<Object> ) objValue, clazz, ignoreSet ));
//                return;
//            }



            switch (field.typeEnum()) {

                case OBJECT:
                case ABSTRACT:
                case INTERFACE:
                    if (objValue instanceof  Map) {
                        final Map<String, Value> valueMap = (Map<String, Value>) objValue;

                        final Value aClass = valueMap.get("class");
                        clazz = Reflection.loadClass(aClass.stringValue());

                    }
                case INSTANCE:
                    switch (value.type()) {
                        case MAP:
                            objValue = fromValueMap(respectIgnore, view, fieldsAccessor, ( Map<String, Value> ) objValue, clazz, ignoreSet );
                            break;
                        case LIST:
                            objValue = fromList(respectIgnore, view, fieldsAccessor, (List<Object>) objValue, clazz, ignoreSet);
                            break;


                    }
                    field.setValue(newInstance, objValue);

                    break;

                case MAP:
                case VALUE_MAP:


                    Class keyType = (Class)field.getParameterizedType().getActualTypeArguments()[0];
                    Class valueType = (Class)field.getParameterizedType().getActualTypeArguments()[1];

                    Map mapInner = (Map)objValue;
                    Set<Map.Entry> set = mapInner.entrySet();
                    Map newMap = new LinkedHashMap(  );

                    for (Map.Entry entry : set) {
                        Object evalue = entry.getValue();

                        Object key = entry.getKey();

                        if (evalue instanceof ValueContainer) {
                            evalue = ((ValueContainer) evalue).toValue();
                        }

                        key  = Conversions.coerce( keyType, key );
                        evalue = Conversions.coerce( valueType, evalue );
                        newMap.put( key, evalue );
                    }

                    objValue = newMap;

                    field.setValue(newInstance, objValue);

                    break;

                case LIST:
                case COLLECTION:
                case SET:
                case ARRAY:


                    handleCollectionOfValues( respectIgnore, view, fieldsAccessor, newInstance, field,
                            ( Collection<Value> ) objValue, ignoreSet );

                    break;

                default:
                        field.setFromValue(newInstance, value);

            }
    }


    /**
     *
     * Gets called by  fromValueMap
     * This does some special handling to take advantage of us using the value map so it avoids creating
     * a bunch of array objects and collections. Things you have to worry about when writing a
     * high-speed JSON serializer.
     * @param respectIgnore  honor @JsonIgnore, transients, etc. of the field
     * @param view the view of the object which can ignore certain fields given certain views
     * @param fieldsAccessor how we are going to access the fields (by field, by property, combination)
     * @param ignoreSet a set of properties to ignore
     * @param field  field we want to inject something into
     * @param newInstance the thing we want to inject a field value into
     * @param value object value of type Value we want to inject into the field.
     * @return new object from value map
     */
    private static <T> void fromValueMapHandleValueCase2(
            boolean respectIgnore, String view,
            FieldsAccessor fieldsAccessor, T newInstance, FieldAccess field, Value value, Set<String> ignoreSet  ) {

        Class<?> clazz = field.type();

        switch (value.type()) {

            case MAP:

                Map<String, Value> valueMap = (Map<String, Value>)  ValueContainer.toObject(value);


                switch (field.typeEnum()) {
                    case OBJECT:
                    case ABSTRACT:
                    case INTERFACE:
                        Value aClass = valueMap.get("class");
                        clazz = Reflection.loadClass(aClass.stringValue());
                        //fall through
                    case INSTANCE:
                        field.setObject(newInstance, fromValueMap(respectIgnore, view, fieldsAccessor, valueMap, clazz, ignoreSet));
                        break;
                    case MAP:
                    case VALUE_MAP:

                        Class keyType = (Class)field.getParameterizedType().getActualTypeArguments()[0];
                        Class valueType = (Class)field.getParameterizedType().getActualTypeArguments()[1];

                        Set<Map.Entry<String, Value>> set = valueMap.entrySet();
                        Map newMap = new LinkedHashMap(  );

                        for (Map.Entry entry : set) {
                            Object evalue = entry.getValue();
                            evalue = ValueContainer.toObject(evalue);

                            Object key = entry.getKey();


                            key  = Conversions.coerce( keyType, key );
                            evalue = Conversions.coerce( valueType, evalue );
                            newMap.put( key, evalue );
                        }


                        field.setValue(newInstance, newMap);

                        break;



                }
                break;
            case LIST:
                handleCollectionOfValues(respectIgnore, view, fieldsAccessor, newInstance, field,
                        (Collection<Value>) ValueContainer.toObject(value), ignoreSet);
                break;
            case TRUE:
            case FALSE:
            case CHAR_SEQUENCE:
            case NUMBER:
                field.setFromValue(newInstance, value);

        }

     }

    /**
     * Helper method to extract collection of values into some field collection.
     * REFACTOR:
     * This could be refactored to use the org.boon.core.Type system which should be faster.
     * REFACTOR
     * @param respectIgnore  honor @JsonIgnore, transients, etc. of the field
     * @param view the view of the object which can ignore certain fields given certain views
     * @param fieldsAccessor how we are going to access the fields (by field, by property, combination)
     * @param newInstance new instance we are injecting field into
     * @param field field we are injecting a value into
     * @param collection the collection we are coercing into a field value
     * @param ignoreSet set of properties that we want to ignore.
     */
    private static void processCollectionFromMapUsingFields(boolean respectIgnore, String view,
            final FieldsAccessor fieldsAccessor, final Object newInstance,
            final FieldAccess field,
            final Collection<?> collection, final Set<String> ignoreSet ) {
        final Class<?> fieldComponentClass = field.getComponentClass();

        final Class<?> valueComponentClass = Reflection.getComponentType(collection);


        /** See if we have a collection of maps because if we do, then we have some
         * recursive processing to do.
         */
        if ( Typ.isMap( valueComponentClass ) ) {
                handleCollectionOfMaps( respectIgnore, view, fieldsAccessor, newInstance, field,
                        ( Collection<Map<String, Object>> ) collection, ignoreSet );
            return;

        }

        /** See if this is a value object of some sort. */
        if ( Typ.isValue( valueComponentClass ) ) {
                 handleCollectionOfValues( respectIgnore, view,  fieldsAccessor, newInstance, field,
                        ( Collection<Value> ) collection, ignoreSet );
            return;
        }


        /**
         * See if the collection implements the same type as the field.
         * I saw a few places that could have used this helper method earlier in the file but were not.
         */
        if (Typ.implementsInterface( collection.getClass(), field.type() )) {

            if (fieldComponentClass!=null && fieldComponentClass.isAssignableFrom(valueComponentClass)) {
                    field.setValue(newInstance, collection);

                return;
            }

        }

        /** See if this is some sort of collection.
         * TODO we need a coerce that needs a respectIgnore
         *
         * REFACTOR:
         * Note we are assuming it is a collection of instances.
         * We don't handle enums here.
         *
         * We do in other places.
         *
         * We handle all sorts of generics but not here.
         *
         * REFACTOR
         *
         **/
        if (!field.typeEnum().isCollection()) {
            if (collection instanceof List) {
                try {
                    Object value = fromList(respectIgnore, view, fieldsAccessor, (List) collection, field.getComponentClass(), ignoreSet);
                    field.setValue(newInstance, value);
                } catch  (Exception ex) {
                    //There is an edge case that needs this. We need a coerce that takes respectIngore, etc.
                    field.setValue(newInstance, collection);
                }
            } else {
                field.setValue(newInstance, collection);
            }
            return;
        }


        /**
         * Create a new collection. if the types already match then just copy them over.
         * Note that this is currently untyped in the null case.
         * We are relying on the fact that the field.setValue calls the Conversion.coerce.
         */
        Collection<Object> newCollection = Conversions.createCollection( field.type(), collection.size() );

        if ( fieldComponentClass == null || fieldComponentClass.isAssignableFrom(valueComponentClass)) {

            newCollection.addAll(collection);
            field.setValue( newInstance, newCollection );
            return;
        }



        /* Here we try to do the coercion for each individual collection item. */
        for (Object itemValue : collection) {
            newCollection.add(Conversions.coerce(fieldComponentClass, itemValue));
            field.setValue(newInstance, newCollection);
        }

    }

    /**
     * Processes an array of maps.
     * @param respectIgnore  honor @JsonIgnore, transients, etc. of the field
     * @param view  the view of the object which can ignore certain fields given certain views
     * @param fieldsAccessor   how we are going to access the fields (by field, by property, combination)
     * @param newInstance  new instance we are injecting field into
     * @param field    field we are injecting a value into
     * @param value     value we are trying to inject which might need coercion
     * @param ignoreSet    set of properties that we want to ignore.
     */
    private static void processArrayOfMaps( boolean respectIgnore, String view, final FieldsAccessor fieldsAccessor,
                                            Object newInstance, FieldAccess field, Object value, Set<String> ignoreSet) {
        Map<String, Object>[] maps = ( Map<String, Object>[] ) value;
        List<Map<String, Object>> list = Lists.list( maps );
        handleCollectionOfMaps( respectIgnore, view, fieldsAccessor, newInstance, field,
                list, ignoreSet );

    }


    /**
     * Processes an collection of maps.
     * @param respectIgnore  honor @JsonIgnore, transients, etc. of the field
     * @param view  the view of the object which can ignore certain fields given certain views
     * @param fieldsAccessor   how we are going to access the fields (by field, by property, combination)
     * @param newInstance  new instance we are injecting field into
     * @param field    field we are injecting a value into
     * @param ignoreSet    set of properties that we want to ignore.
     */
    @SuppressWarnings("unchecked")
    private static void handleCollectionOfMaps( boolean respectIgnore, String view, final FieldsAccessor fieldsAccessor, Object newInstance,
                                                FieldAccess field, Collection<Map<String, Object>> collectionOfMaps,
                                                final Set<String> ignoreSet ) {

        Collection<Object> newCollection = Conversions.createCollection( field.type(), collectionOfMaps.size() );


        Class<?> componentClass = field.getComponentClass();

        if ( componentClass != null ) {


            for ( Map<String, Object> mapComponent : collectionOfMaps ) {

                newCollection.add( fromMap( respectIgnore, view, fieldsAccessor, mapComponent, componentClass, ignoreSet ) );

            }
            field.setObject( newInstance, newCollection );

        }

    }

    /**
     * Processes an collection of maps.
     * This can inject into an array and appears to be using some of the Type lib.
     * @param respectIgnore  honor @JsonIgnore, transients, etc. of the field
     * @param view  the view of the object which can ignore certain fields given certain views
     * @param fieldsAccessor   how we are going to access the fields (by field, by property, combination)
     * @param newInstance  new instance we are injecting field into
     * @param field    field we are injecting a value into
     * @param ignoreSet    set of properties that we want to ignore.
     */
    @SuppressWarnings("unchecked")
    private static void handleCollectionOfValues(
            boolean respectIgnore, String view,
            FieldsAccessor fieldsAccessor, Object newInstance,
            FieldAccess field, Collection<Value> acollectionOfValues, Set<String> ignoreSet ) {

        Collection collectionOfValues = acollectionOfValues;

        if(field.typeEnum() == INSTANCE) {

            field.setObject(newInstance, fromList(fieldsAccessor, (List) acollectionOfValues, field.type()));
            return;

        }

        if ( collectionOfValues instanceof ValueList ) {
            collectionOfValues = ( ( ValueList ) collectionOfValues ).list();
        }

        Collection<Object> newCollection = Conversions.createCollection( field.type(), collectionOfValues.size() );

        Class<?> componentClass = field.getComponentClass();



        /** If the field is a collection than try to convert the items in the collection to
         * the field type.
         */
        switch (field.typeEnum() ) {


            case LIST:
            case SET:
            case COLLECTION:


                for ( Value value : ( List<Value> ) collectionOfValues ) {

                    if ( value.isContainer() ) {
                        Object oValue = value.toValue();
                        if ( oValue instanceof Map ) {
                            newCollection.add( fromValueMap( respectIgnore, view, fieldsAccessor, ( Map ) oValue, componentClass, ignoreSet ) );
                        }
                    } else {
                        newCollection.add( Conversions.coerce( componentClass, value.toValue() ) );
                    }


                }
                field.setObject( newInstance, newCollection );
                break;

            case ARRAY:

                org.boon.core.Type componentType =  org.boon.core.Type.getType(componentClass);
                int index = 0;

                switch (componentType) {
                    case INT:
                        int [] iarray = new int[collectionOfValues.size()];
                        for ( Value value : ( List<Value> ) collectionOfValues ) {
                              iarray[index] = value.intValue();
                            index++;

                        }
                        field.setObject( newInstance, iarray);
                        return;
                    case SHORT:
                        short [] sarray = new short[collectionOfValues.size()];
                        for ( Value value : ( List<Value> ) collectionOfValues ) {
                            sarray[index] = value.shortValue();
                            index++;

                        }
                        field.setObject( newInstance, sarray);
                        return;
                    case DOUBLE:
                        double [] darray = new double[collectionOfValues.size()];
                        for ( Value value : ( List<Value> ) collectionOfValues ) {
                            darray[index] = value.doubleValue();
                            index++;

                        }
                        field.setObject( newInstance, darray);
                        return;
                    case FLOAT:
                        float [] farray = new float[collectionOfValues.size()];
                        for ( Value value : ( List<Value> ) collectionOfValues ) {
                            farray[index] = value.floatValue();
                            index++;

                        }
                        field.setObject( newInstance, farray);
                        return;

                    case LONG:
                        long [] larray = new long[collectionOfValues.size()];
                        for ( Value value : ( List<Value> ) collectionOfValues ) {
                            larray[index] = value.longValue();
                            index++;

                        }
                        field.setObject( newInstance, larray);
                        return;


                    case BYTE:
                        byte [] barray = new byte[collectionOfValues.size()];
                        for ( Value value : ( List<Value> ) collectionOfValues ) {
                            barray[index] = value.byteValue();
                            index++;

                        }
                        field.setObject( newInstance, barray);
                        return;


                    case CHAR:
                        char [] chars = new char[collectionOfValues.size()];
                        for ( Value value : ( List<Value> ) collectionOfValues ) {
                            chars[index] = value.charValue();
                            index++;
                        }
                        field.setObject( newInstance, chars);
                        return;


                    default:
                        Object array = Array.newInstance( componentClass, collectionOfValues.size() );
                        Object o;

                        for ( Value value : ( List<Value> ) collectionOfValues ) {
                            if (value instanceof ValueContainer) {
                                o = value.toValue();
                                if (o instanceof List) {
                                    o = fromList(fieldsAccessor, (List)o, componentClass);
                                    if (componentClass.isInstance( o )) {
                                       Array.set(array, index, o);
                                    } else {
                                        break;
                                    }
                                }
                            } else {
                                o = value.toValue();
                                if (componentClass.isInstance( o )) {
                                    Array.set(array, index, o);
                                } else {
                                    Array.set(array, index, Conversions.coerce( componentClass, o ));
                                }
                            }
                            index++;
                        }
                        field.setValue( newInstance, array);


                }
                break;
        }

    }

    /**
     * Basic toMap to create an object into a map.
     * @param object the object we want to convert to a list
     * @param ignore do we honor ignore properties
     * @return new map
     */
    public static Map<String, Object> toMap( final Object object, final String... ignore ) {
        return toMap( object, Sets.set( ignore ) );
    }


    /**
     * Converts a field access set into a collection of map entries.
     */
    public static class FieldToEntryConverter implements
            Function<FieldAccess, Maps.Entry<String, Object>> {

        final Object object;

        public FieldToEntryConverter(Object object) {
            this.object = object;
        }

        @Override
        public Maps.Entry<String, Object> apply( FieldAccess from ) {
            if ( from.isReadOnly() ) {
                return null;
            }
            Maps.Entry<String, Object> entry = new Maps.EntryImpl<>( from.name(),
                    from.getValue( object ) );
            return entry;
        }
    }


    /**
     * This could be refactored to use core.Type class and it would run faster.
     * Converts an object into a map
     * @param object the object that we want to convert
     * @param ignore the map
     * @return map map representation of the object
     */
    public static Map<String, Object> toMap( final Object object, Set<String> ignore ) {

        if ( object == null ) {
            return null;
        }

        Map<String, Object> map = new LinkedHashMap<>();



        final Map<String, FieldAccess> fieldMap = Reflection.getAllAccessorFields( object.getClass() );
        List<FieldAccess> fields = new ArrayList( fieldMap.values() );


        Collections.reverse( fields ); // make super classes fields first that
        // their update get overridden by
        // subclass fields with the same name

        List<Maps.Entry<String, Object>> entries = Conversions.mapFilterNulls(
                new FieldToEntryConverter(object), new ArrayList( fields ) );

        map.put( "class", object.getClass().getName() );

        for ( Maps.Entry<String, Object> entry : entries ) {

            String key = entry.key();

            if ( ignore.contains( key ) ) {
                continue;
            }

            Object value = entry.value();
            if ( value == null ) {
                continue;
            }
            if ( Typ.isBasicType( value ) ) {
                map.put( key, entry.value() );
            } else if ( Boon.isArray( value )
                    && Typ.isBasicType( value.getClass().getComponentType() ) ) {
                map.put( key, entry.value() );
            } else if ( Boon.isArray( value ) ) {
                int length = Arry.len(value);
                List<Map<String, Object>> list = new ArrayList<>( length );
                for ( int index = 0; index < length; index++ ) {
                    Object item = BeanUtils.idx( value, index );
                    list.add( toMap( item, ignore ) );
                }
                map.put( key, list );
            } else if ( value instanceof Collection ) {
                Collection<?> collection = ( Collection<?> ) value;
                Class<?> componentType = Reflection.getComponentType( collection, fieldMap.get( entry.key() ) );
                if ( Typ.isBasicType( componentType ) ) {
                    map.put( key, value );
                } else {
                    List<Map<String, Object>> list = new ArrayList<>(
                            collection.size() );
                    for ( Object item : collection ) {
                        if ( item != null ) {
                            list.add( toMap( item, ignore ) );
                        } else {

                        }
                    }
                    map.put( entry.key(), list );
                }
            } else if ( value instanceof Map ) {

            } else {
                map.put( entry.key(), toMap( value, ignore ) );
            }
        }
        return map;


    }




    /**
     * This could be refactored to use core.Type class and it would run faster.
     *
     * REFACTOR:
     * This is nearly a duplicate of the last method.
     * I think the rationality was speed, but we need to rethink that and come up with a
     * Mapper class.
     * REFACTOR
     * Converts an object into a map
     * @param object the object that we want to convert
     * @return map map representation of the object
     */
    public static Map<String, Object> toMap( final Object object ) {

        if ( object == null ) {
            return null;
        }

        if ( object instanceof Map ) {
            return ( Map<String, Object> ) object;
        }

        Map<String, Object> map = new LinkedHashMap<>();



        final Map<String, FieldAccess> fieldMap = Reflection.getAllAccessorFields( object.getClass() );
        List<FieldAccess> fields = new ArrayList( fieldMap.values() );


        Collections.reverse( fields ); // make super classes fields first that
        // their update get overridden by
        // subclass fields with the same name

        List<Maps.Entry<String, Object>> entries = Conversions.mapFilterNulls(
                new FieldToEntryConverter(object), new ArrayList( fields ) );

        map.put( "class", object.getClass().getName() );

        for ( Maps.Entry<String, Object> entry : entries ) {
            Object value = entry.value();
            if ( value == null ) {
                continue;
            }
            if ( Typ.isBasicType( value ) ) {
                map.put( entry.key(), entry.value() );
            } else if ( Boon.isArray( value )
                    && Typ.isBasicType( value.getClass().getComponentType() ) ) {
                map.put( entry.key(), entry.value() );
            } else if ( Boon.isArray( value ) ) {
                int length = Arry.len(value);
                List<Map<String, Object>> list = new ArrayList<>( length );
                for ( int index = 0; index < length; index++ ) {
                    Object item = BeanUtils.idx( value, index );
                    list.add( toMap( item ) );
                }
                map.put( entry.key(), list );
            } else if ( value instanceof Collection ) {
                Collection<?> collection = ( Collection<?> ) value;
                Class<?> componentType = Reflection.getComponentType( collection, fieldMap.get( entry.key() ) );
                if ( Typ.isBasicType( componentType ) ) {
                    map.put( entry.key(), value );
                } else {
                    List<Map<String, Object>> list = new ArrayList<>(
                            collection.size() );
                    for ( Object item : collection ) {
                        if ( item != null ) {
                            list.add( toMap( item ) );
                        } else {

                        }
                    }
                    map.put( entry.key(), list );
                }
            } else if ( value instanceof Map ) {

            } else {
                map.put( entry.key(), toMap( value ) );
            }
        }
        return map;
    }

    /**
     * This converts a list of maps to objects.
     * I always forget that this exists. I need to remember.
     *
     * @param respectIgnore   honor @JsonIgnore, transients, etc. of the field
     * @param view the view of the object which can ignore certain fields given certain views
     * @param fieldsAccessor how we are going to access the fields (by field, by property, combination)
     * @param componentType The component type of the created list
     * @param list the input list
     * @param ignoreProperties properties to ignore
     * @param <T> generics
     * @return a new list
     */
    public static <T> List<T> convertListOfMapsToObjects(   boolean respectIgnore, String view,
                                                            FieldsAccessor fieldsAccessor,
                                                            Class<T> componentType, List<?> list, Set<String> ignoreProperties) {
        List<Object> newList = new ArrayList<>( list.size() );
        for ( Object obj : list ) {

            if ( obj instanceof Value ) {
                obj = ( ( Value ) obj ).toValue();
            }

            if ( obj instanceof Map ) {

                Map map = ( Map ) obj;
                if ( map instanceof ValueMapImpl ) {
                    newList.add( fromValueMap( respectIgnore, view, fieldsAccessor, ( Map<String, Value> ) map, componentType, ignoreProperties ) );
                } else {
                    newList.add( fromMap( respectIgnore, view, fieldsAccessor, map, componentType, ignoreProperties ) );
                }
            } else {
                newList.add( Conversions.coerce( componentType, obj ) );
            }
        }
        return ( List<T> ) newList;
    }

    /**
     * Creates a list of maps from a list of class instances.
     * @param collection  the collection we are coercing into a field value
     * @return the return value.
     */
    public static List<Map<String, Object>> toListOfMaps( Collection<?> collection ) {
        List<Map<String, Object>> list = new ArrayList<>();
        for ( Object o : collection ) {
            list.add( toMap( o ) );
        }
        return list;
    }

}
