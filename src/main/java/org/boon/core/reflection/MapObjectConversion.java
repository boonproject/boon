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
import org.boon.primitive.Arry;
import org.boon.core.*;
import org.boon.core.reflection.fields.FieldAccess;
import org.boon.core.reflection.fields.FieldAccessMode;
import org.boon.core.reflection.fields.FieldsAccessor;
import org.boon.core.value.ValueContainer;
import org.boon.core.value.ValueList;
import org.boon.core.value.ValueMap;
import org.boon.core.value.ValueMapImpl;
import org.boon.primitive.CharBuf;

import java.lang.reflect.*;
import java.lang.reflect.Type;
import java.util.*;

import static org.boon.Boon.className;
import static org.boon.Boon.sputs;
import static org.boon.Exceptions.die;
import static org.boon.Exceptions.handle;
import static org.boon.core.Conversions.coerce;
import static org.boon.core.Conversions.toEnum;
import static org.boon.core.Type.gatherTypes;


/**
 * Created by rick on 12/26/13.
 */
public class MapObjectConversion {


    public static <T> T fromListUsingFields( List<Object> list, Class<T> clazz ) {

        return fromListUsingFields( false, null, FieldAccessMode.FIELD_THEN_PROPERTY.create( false ), list,  clazz, null );

    }

    public static <T> T fromListUsingFields( boolean respectIgnore, String view,
                                             FieldsAccessor fieldsAccessor, List<Object> list, Class<T> clazz ,
                                             Set<String> ignoreSet) {

        Map<String, FieldAccess> fieldMap = fieldsAccessor.getFields( clazz );
        List<Field> fields = Reflection.getFields( clazz );
        Object item;
        Class<?> paramType;
        Field field;

        T toObject = Reflection.newInstance( clazz );

        loop:
        for ( int index = 0; index < fields.size(); index++ ) {
            field = fields.get( index );
            paramType = field.getType();
            item = list.get( index );
            FieldAccess fieldAccess = fieldMap.get( field.getName() );

            if ( Typ.isList( paramType ) && item instanceof List ) {
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
            } else if ( paramType.isInstance( item ) ) {
                fieldAccess.setValue( toObject, item );
            }
            else if (item instanceof Map) {

                setFieldValueFromMap( respectIgnore, view, fieldsAccessor, ignoreSet, toObject, fieldAccess, item );

            } else {
                fieldAccess.setValue( toObject, item );
            }
        }

        return toObject;

    }



    public static <T> T fromList( List<Object> argList, Class<T> clazz ) {
          return fromList( FieldAccessMode.FIELD_THEN_PROPERTY.create( false ), argList, clazz );
    }




    public static <T> T fromList( boolean respectIgnore, String view, FieldsAccessor fieldsAccessor,
                                  List<?> argList, Class<T> clazz, Set<String> ignoreSet ) {

        int size = argList.size();

        List<Object> list = new ArrayList( argList );

        ClassMeta<T> classMeta = ClassMeta.classMeta( clazz );

        ConstructorAccess<T> match = null;

        Object[] finalArgs = null;




        try {


            loop:
            for ( ConstructorAccess constructor : classMeta.constructors() ) {
                Class[] parameterTypes = constructor.parameterTypes();
                if ( parameterTypes.length == size ) {

                    for ( int index = 0; index < size; index++ ) {
                        if ( !matchAndConvertArgs( respectIgnore, view,
                                fieldsAccessor, list, constructor, parameterTypes, index, ignoreSet ) ) continue loop;
                    }
                    match = constructor;
                }
            }

            if ( match != null ) {
                finalArgs = list.toArray( new Object[list.size()] );
                return ( T ) match.create( finalArgs );
            } else {
                return (T) die(Object.class, "Unable to convert list", list, "into", clazz);
            }

        } catch ( Exception e ) {


            if (match != null)  {


                CharBuf buf = CharBuf.create(200);
                buf.addLine();
                buf.multiply('-', 10).add("FINAL ARGUMENTS").multiply('-', 10).addLine();
                if (finalArgs!=null) {
                    for (Object o : finalArgs) {
                        buf.puts("argument type    ", className(o));
                    }
                }


                buf.multiply('-', 10).add("CONSTRUCTOR").add(match).multiply('-', 10).addLine();
                buf.multiply('-', 10).add("CONSTRUCTOR PARAMS").multiply('-', 10).addLine();
                for (Class<?> c : match.parameterTypes()) {
                        buf.puts("constructor type ", c);
                }

                buf.multiply('-', 35).addLine();


                return ( T ) handle(Object.class, e, buf.toString(),
                        "\nconstructor parameter types", match.parameterTypes(),
                        "\nlist args after conversion", list, "types",
                        gatherTypes(list),
                        "\noriginal args", argList,
                        "original types", gatherTypes(argList));
            } else {
                return ( T ) handle(Object.class, e,
                        "\nlist args after conversion", list, "types",
                        gatherTypes(list),
                        "\noriginal args", argList,
                        "original types", gatherTypes(argList));

            }
        }

    }


    public static <T> T fromList( FieldsAccessor fieldsAccessor, List<Object> argList, Class<T> clazz ) {
        return fromList( false, null, fieldsAccessor, argList, clazz, null );
    }

    public static boolean matchAndConvertArgs( boolean respectIgnore, String view,
                                               FieldsAccessor fieldsAccessor, List<Object> list,
                                               ConstructorAccess constructor,
                                               Class[] parameterTypes, int index, Set<String> ignoreSet  ) {
        try {

            Class paramType;
            Object item;
            paramType = parameterTypes[index];
            item = list.get( index );
            if ( item instanceof ValueContainer ) {
                item = ( ( ValueContainer ) item ).toValue();
                list.set(index, item);
            }

            if (paramType.isPrimitive() && item == null) {
                return false;
            }

            if (item == null) {
                return true;
            }

            /** Handle primitive type conversion. */
            if ( Typ.isPrimitiveOrWrapper( paramType ) &&
                    ( item instanceof Number || item instanceof Boolean || item instanceof CharSequence ) ) {

                    Object o = Conversions.coerceOrDie( paramType, item );
                    list.set( index, o );
            }
            /** Handle map to user class instance conversion. */
            else if ( item instanceof Map && !Typ.isMap( paramType ) ) {

                /** Handle instance value conversion from map to user defined class instance. */
                if ( !paramType.isInterface() && !Typ.isAbstract( paramType ) ) {
                     item = fromMap( respectIgnore, view, fieldsAccessor, ( Map<String, Object> ) item, paramType, ignoreSet );
                     list.set(index, item);
                } else {

                    /** Handle conversion of user define interfaces. */
                    String  className = (String) ((Map) item).get( "class" );
                    if (className != null)  {
                        item = fromMap( respectIgnore, view, fieldsAccessor, ( Map<String, Object> ) item, Reflection.loadClass( className ), ignoreSet );
                        list.set(index, item);
                    } else {
                        return false;
                    }
                }
            }   /** Handle map to user class instance conversion. */
            else if ( item instanceof Map  ) {

                Map itemMap = (Map) item;

                Type type = constructor.getGenericParameterTypes()[index];
                if ( type instanceof ParameterizedType ) {
                    ParameterizedType pType = ( ParameterizedType ) type;
                    Class<?> keyType = ( Class<?> ) pType.getActualTypeArguments()[0];

                    Class<?> valueType = ( Class<?> ) pType.getActualTypeArguments()[1];



                    Map newMap =  Conversions.createMap(paramType, itemMap.size());

                    for ( Object o : itemMap.entrySet() ) {
                        Map.Entry entry = (Map.Entry)o;

                        Object key = entry.getKey();
                        Object value = entry.getValue();

                        key = ValueContainer.toObject(key);

                        value = ValueContainer.toObject(value);



                        if (value instanceof List) {
                            value = fromList( respectIgnore, view, fieldsAccessor, (List)value, valueType, ignoreSet );

                        } else if (value instanceof Map) {
                            value = fromMap( respectIgnore, view, fieldsAccessor, (Map) value, valueType, ignoreSet );

                        } else {
                            value = coerce(valueType, value);
                        }


                        if (key instanceof List) {
                            key = fromList( respectIgnore, view, fieldsAccessor, (List)key, keyType, ignoreSet );

                        } else if (value instanceof Map) {
                            key = fromMap( respectIgnore, view, fieldsAccessor, (Map) key, keyType, ignoreSet );

                        } else {
                            key = coerce(keyType, key);
                        }

                        newMap.put(key, value);
                    }
                    list.set( index, newMap );
                }
             }


            /* It is some sort of instance parameters (user defined instance of a class) and the item is a list. */
            else if ( item instanceof List && !Typ.isCollection(paramType) && !paramType.isEnum()) {

                List<Object> listItem = null;
                Object convertedItem = null;

                try {
                    listItem =      ( List<Object> ) item;

                    convertedItem = fromList(respectIgnore, view, fieldsAccessor, listItem, paramType, ignoreSet );

                    list.set( index, convertedItem);

                } catch (Exception ex) {


                    Boon.error(ex, "PROBLEM WITH matchAndConvertArgs converting a list item into an object",
                            "listItem", listItem,
                            "convertedItem", convertedItem,
                            "respectIgnore", respectIgnore, "view", view,
                            "fieldsAccessor", fieldsAccessor, "list", list,
                            "constructor", constructor, "parameters", parameterTypes,
                            "index", index, "ignoreSet", ignoreSet);
                    ex.printStackTrace();
                }

                /** The parameter is some sort of collection, and the item is a list. */
            } else if ( Typ.isCollection(paramType) && item instanceof List ) {
                 
                List<Object> itemList = ( List<Object> ) item;

                /** Items have stuff in it, the item is a list of lists */
                if ( itemList.size() > 0 && (itemList.get( 0 ) instanceof List ||
                        itemList.get(0) instanceof ValueContainer)  ) {


                    Type type = constructor.getGenericParameterTypes()[index];
                    if ( type instanceof ParameterizedType ) {
                        ParameterizedType pType = ( ParameterizedType ) type;
                        Class<?> componentType = ( Class<?> ) pType.getActualTypeArguments()[0];


                        Collection newList =  Conversions.createCollection( paramType, itemList.size() );

                        for ( Object o : itemList ) {
                            if ( o instanceof ValueContainer ) {
                                o = ( ( ValueContainer ) o ).toValue();
                            }

                            List fromList = ( List ) o;
                            o = fromList( respectIgnore, view, fieldsAccessor, fromList, componentType, ignoreSet );
                            newList.add( o );
                        }
                        list.set( index, newList );

                    }
                } else {

                    /* Just a list not a list of lists*/
                    Type type = constructor.getGenericParameterTypes()[index];
                    if ( type instanceof ParameterizedType ) {
                        ParameterizedType pType = ( ParameterizedType ) type;
                        Class<?> componentType = ( Class<?> ) pType.getActualTypeArguments()[0];


                        Collection newList =  Conversions.createCollection( paramType, itemList.size() );


                        for ( Object o : itemList ) {
                            if ( o instanceof ValueContainer ) {
                                o = ( ( ValueContainer ) o ).toValue();
                            }
                            if (o instanceof List) {
                                List fromList = ( List ) o;
                                o = fromList( respectIgnore, view, fieldsAccessor, fromList, componentType, ignoreSet );
                                newList.add( o );
                            } else if (o instanceof Map) {
                                Map fromMap = ( Map ) o;
                                o = fromMap(respectIgnore, view, fieldsAccessor, fromMap, componentType, ignoreSet);
                                newList.add( o );

                            } else {
                                newList.add( Conversions.coerce(componentType, o));
                            }
                        }
                        list.set( index, newList );

                    }

                }
            } else if ( paramType == Typ.string  && item instanceof CharSequence ) {
                list.set( index, item.toString() );
            } else if ( paramType.isEnum()  && (item instanceof CharSequence| item instanceof Number)  ) {
                list.set( index, toEnum(paramType, item));
            } else if ( paramType.isInstance( item ) ) {
                return true;
            } else {
                org.boon.core.Type type = org.boon.core.Type.getType(paramType);

                if ( type == org.boon.core.Type.INSTANCE ) {
                    list.set(index, coerce(paramType, item));
                } else {
                    return false;
                }
            }
        } catch (Exception ex) {
            Boon.error(ex, "PROBLEM WITH matchAndConvertArgs",
                    "respectIgnore", respectIgnore, "view", view,
                    "fieldsAccessor", fieldsAccessor, "list", list,
                    "constructor", constructor, "parameters", parameterTypes,
                    "index", index, "ignoreSet", ignoreSet);
            ex.printStackTrace();
            return false;
        }

        return true;
    }


    @SuppressWarnings( "unchecked" )
    public static <T> T fromMap( Map<String, Object> map, Class<T> clazz ) {
        return fromMap( false, null, FieldAccessMode.FIELD_THEN_PROPERTY.create( true ), map, clazz, null );

    }


    public static List<Object> toList( Object object) {

        org.boon.core.Type instanceType = org.boon.core.Type.getInstanceType(object);

        switch (instanceType) {
            case NULL:
                return Lists.list(null);
            case ARRAY:
                return Conversions.toList(object);
            case INSTANCE:
                if (Reflection.respondsTo(object, "toList")) {
                    return (List<Object>) Reflection.invoke(object, "toList");
                }
                break;
        }
        return Lists.list(object);
    }



    @SuppressWarnings( "unchecked" )
    public static <T> T fromMap( Map<String, Object> map, Class<T> clazz, String... excludeProperties ) {
        Set<String> ignoreProps = excludeProperties.length > 0 ? Sets.set(excludeProperties) :  null;
        return fromMap( false, null, FieldAccessMode.FIELD_THEN_PROPERTY.create( true ), map,  clazz, ignoreProps );

    }



    public static Object fromMap( Map<String, Object> map ) {
        String clazz = (String) map.get( "class" );
        Class cls = Reflection.loadClass( clazz );
        return fromMap( false, null, FieldAccessMode.FIELD_THEN_PROPERTY.create( true ), map, cls  , null );
    }




    @SuppressWarnings("unchecked")
    public static <T> T fromMap( boolean respectIgnore, String view, FieldsAccessor fieldsAccessor, Map<String, Object> map, Class<T> cls, Set<String> ignoreSet ) {


        T toObject = Reflection.newInstance( cls );
        Map<String, FieldAccess> fields = fieldsAccessor.getFields( toObject.getClass() );
        Set<Map.Entry<String, Object>> entrySet = map.entrySet();


        /* Iterate through the fields. */
        //for ( FieldAccess field : fields ) {
        for ( Map.Entry<String, Object> entry : entrySet ) {
            String key = entry.getKey();

            if ( ignoreSet != null ) {
                if ( ignoreSet.contains( key ) ) {
                    continue;
                }
            }
            FieldAccess field = fields.get( key );


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

            Object value = entry.getValue();


            if ( value instanceof Value ) {
                if ( ( ( Value ) value ).isContainer() ) {
                    value = ( ( Value ) value ).toValue();
                } else {
                    field.setFromValue( toObject, ( Value ) value );
                    continue;
                }
            }

            if ( value == null ) {
                field.setObject( toObject, null );
                continue;
            }

            if ( value.getClass() == field.type() ) {
                field.setObject( toObject, value );
            } else if ( Typ.isBasicType( value ) ) {

                field.setValue( toObject, value );
            } else if ( value instanceof Value ) {
                field.setValue( toObject, value );
            }
            /* See if it is a map<string, object>, and if it is then process it. */
            //&& Typ.getKeyType ( ( Map<?, ?> ) value ) == Typ.string
            else if ( value instanceof Map ) {
                setFieldValueFromMap( respectIgnore, view, fieldsAccessor, ignoreSet, toObject, field, value );
            } else if ( value instanceof Collection ) {
                /*It is a collection so process it that way. */
                processCollectionFromMapUsingFields( respectIgnore, view, fieldsAccessor, toObject, field, ( Collection ) value, ignoreSet );
            } else if ( value instanceof Map[] ) {
                /* It is an array of maps so, we need to process it as such. */
                processArrayOfMaps(respectIgnore, view, fieldsAccessor, toObject, field, value, ignoreSet );
            } else {
                field.setValue( toObject, value );
            }

        }

        return toObject;

    }

    private static <T> void setFieldValueFromMap( boolean respectIgnore, String view, FieldsAccessor fieldsAccessor,
                                                  Set<String> ignoreSet, T toObject, FieldAccess field, Object value ) {
        Class<?> clazz = field.type();

        Map mapInner = (Map)value;

        if ( !Typ.isMap( clazz ) )  {

            if ( !clazz.isInterface() && !Typ.isAbstract( clazz ) ) {
                value = fromMap( respectIgnore, view, fieldsAccessor, mapInner, field.type(), ignoreSet );

            } else {
                String  className = (String) ((Map) value).get( "class" );
                if (className != null)  {
                    value = fromMap( respectIgnore, view, fieldsAccessor, mapInner, Reflection.loadClass( className ), ignoreSet );
                } else {
                    value = null;
                }
            }
        }  else if (Typ.isMap( clazz ))  {
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

        field.setObject( toObject, value );

    }


    @SuppressWarnings("unchecked")
    private static Object fromValueMap(boolean respectIgnore, String view,
            final FieldsAccessor fieldsAccessor,
            final Map<String, Value> map, Set<String> ignoreSet  ) {

        try {
            String className = map.get( "class" ).toString();
            Class<?> cls = Reflection.loadClass( className );
            return fromValueMap( respectIgnore, view, fieldsAccessor, map, cls, ignoreSet );
        } catch ( Exception ex ) {
            return handle(Object.class, sputs("fromValueMap", "map", map, "fieldAccessor", fieldsAccessor), ex);
        }
    }


    @SuppressWarnings("unchecked")
    public static <T> T fromValueMap( boolean respectIgnore, String view, final FieldsAccessor fieldsAccessor,
                                      final Map<String, Value> amap,
                                      final Class<T> cls, Set<String> ignoreSet ) {

        T newInstance = Reflection.newInstance( cls );
        ValueMap map = ( ValueMap ) ( Map ) amap;


        Map<String, FieldAccess> fields = fieldsAccessor.getFields( cls);
        Map.Entry<String, Object>[] entries;
        int size;

        if ( !map.hydrated() ) {
            size = map.len();
            entries = map.items();
        } else {
            size = map.size();
            entries = ( Map.Entry<String, Object>[] ) map.entrySet().toArray( new Map.Entry[size] );
        }

        /* guard. */
        if ( size == 0 || entries == null ) {
            return newInstance;
        }


        FieldAccess field = null;
        String fieldName = null;
        Map.Entry<String, Object> entry;

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

    private static <T> void fromMapHandleNonValueCase( boolean respectIgnore, String view,
                                                       FieldsAccessor fieldsAccessor,
                                                       T newInstance, FieldAccess field, Object ovalue, Set<String> ignoreSet ) {
        try {
            if ( ovalue instanceof Map ) {
                Class<?> clazz = field.type();
                if ( !clazz.isInterface() && !Typ.isAbstract( clazz ) ) {
                    ovalue = fromValueMap( respectIgnore, view, fieldsAccessor, ( Map<String, Value> ) ovalue, field.type(), ignoreSet );
                } else {
                    ovalue = fromValueMap( respectIgnore, view, fieldsAccessor, ( Map<String, Value> ) ovalue, ignoreSet );
                }
                field.setObject( newInstance, ovalue );
            } else if ( ovalue instanceof Collection ) {
                handleCollectionOfValues( respectIgnore, view, fieldsAccessor, newInstance, field,
                        ( Collection<Value> ) ovalue, ignoreSet );
            } else {
                field.setValue( newInstance, ovalue );
            }
        } catch ( Exception ex ) {
            handle(sputs("Problem handling non value case of fromValueMap", "field", field.name(),
                    "fieldType", field.type().getName(), "object from map", ovalue), ex);
        }
    }

    private static <T> void fromValueMapHandleValueCase(
            boolean respectIgnore, String view,
            FieldsAccessor fieldsAccessor, T newInstance, FieldAccess field, Value value, Set<String> ignoreSet  ) {
        Object objValue = null;

        try {
            if ( value.isContainer() ) {
                objValue = value.toValue();
                if ( objValue instanceof Map ) {
                    Class<?> clazz = field.type();
                    if ( !clazz.isInterface() && !Typ.isAbstract( clazz ) ) {
                        objValue = fromValueMap( respectIgnore, view, fieldsAccessor,
                                ( Map<String, Value> ) objValue, field.type(), ignoreSet );
                    } else {
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
                    field.setObject( newInstance, objValue );
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

        if ( Typ.isValue( valueComponentClass ) ) {
                 handleCollectionOfValues( respectIgnore, view,  fieldsAccessor, newInstance, field,
                        ( Collection<Value> ) collection, ignoreSet );
            return;
        }





        if (Typ.implementsInterface( collection.getClass(), field.type() )) {

            if (fieldComponentClass!=null && fieldComponentClass.isAssignableFrom(valueComponentClass)) {
                    field.setValue(newInstance, collection);

                return;
            }

        }

        if (!field.typeEnum().isCollection()) {
            if (collection instanceof List) {
                try {
                    Object value = fromList(respectIgnore, view, fieldsAccessor, (List) collection, field.getComponentClass(), ignoreSet);
                    field.setObject(newInstance, value);
                } catch  (Exception ex) {
                    //There is an edge case that needs this. We need a coerce that takes respectIngore, etc.
                    field.setObject(newInstance, coerce(field.typeEnum(), field.type(), collection));
                }
            } else {
                field.setObject(newInstance, coerce(field.typeEnum(), field.type(), collection));
            }
            return;
        }



        Collection<Object> newCollection = Conversions.createCollection( field.type(), collection.size() );

        if ( fieldComponentClass == null || fieldComponentClass.isAssignableFrom(valueComponentClass)) {

            newCollection.addAll(collection);
            field.setValue( newInstance, newCollection );
            return;
        }


        for (Object itemValue : collection) {
            newCollection.add(Conversions.coerce(fieldComponentClass, itemValue));
            field.setValue(newInstance, newCollection);
        }
    }

    private static void processArrayOfMaps( boolean respectIgnore, String view, final FieldsAccessor fieldsAccessor,
                                            Object newInstance, FieldAccess field, Object value, Set<String> ignoreSet) {
        Map<String, Object>[] maps = ( Map<String, Object>[] ) value;
        List<Map<String, Object>> list = Lists.list( maps );
        handleCollectionOfMaps( respectIgnore, view, fieldsAccessor, newInstance, field,
                list, ignoreSet );

    }

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


    @SuppressWarnings("unchecked")
    private static void handleCollectionOfValues(
            boolean respectIgnore, String view,
            FieldsAccessor fieldsAccessor, Object newInstance,
            FieldAccess field, Collection<Value> acollectionOfValues, Set<String> ignoreSet ) {

        Collection collectionOfValues = acollectionOfValues;

        if ( collectionOfValues instanceof ValueList ) {
            collectionOfValues = ( ( ValueList ) collectionOfValues ).list();
        }

        Collection<Object> newCollection = Conversions.createCollection( field.type(), collectionOfValues.size() );



        if ( field.typeEnum().isCollection() ) {

            Class<?> componentClass = field.getComponentClass();

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

        } else if (field.typeEnum() == org.boon.core.Type.ARRAY) {

            Class<?> componentClass = field.getComponentClass();
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
                    field.setObject( newInstance, array);
                    return;

            }
        }
        else  {
            field.setObject( newInstance, fromList( fieldsAccessor,  (List) acollectionOfValues, field.type()));
        }

    }

    public static Map<String, Object> toMap( final Object object, final String... ignore ) {
        return toMap( object, Sets.set( ignore ) );
    }

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

    public static List<Map<String, Object>> toListOfMaps( Collection<?> collection ) {
        List<Map<String, Object>> list = new ArrayList<>();
        for ( Object o : collection ) {
            list.add( toMap( o ) );
        }
        return list;
    }

}
