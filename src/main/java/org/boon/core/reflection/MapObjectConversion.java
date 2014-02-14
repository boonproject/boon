package org.boon.core.reflection;

import org.boon.Exceptions;
import org.boon.Lists;
import org.boon.Maps;
import org.boon.Sets;
import org.boon.core.Conversions;
import org.boon.core.Typ;
import org.boon.core.Value;
import org.boon.core.reflection.fields.FieldAccess;
import org.boon.core.reflection.fields.FieldAccessMode;
import org.boon.core.reflection.fields.FieldsAccessor;
import org.boon.core.value.ValueContainer;
import org.boon.core.value.ValueList;
import org.boon.core.value.ValueMap;
import org.boon.core.value.ValueMapImpl;

import java.lang.reflect.*;
import java.util.*;

import static org.boon.Boon.sputs;
import static org.boon.core.Type.gatherTypes;


/**
 * Created by rick on 12/26/13.
 */
public class MapObjectConversion {


    public static <T> T fromListUsingFields( FieldsAccessor fieldsAccessor, List<Object> list, Class<T> clazz ) {
        return fromListUsingFields( fieldsAccessor, list, Reflection.newInstance( clazz ) );

    }

    public static <T> T fromListUsingFields( List<Object> list, Class<T> clazz ) {

        return fromListUsingFields( FieldAccessMode.FIELD.create( false ), list, Reflection.newInstance( clazz ) );

    }

    public static <T> T fromListUsingFields( FieldsAccessor fieldsAccessor, List<Object> list, T toObject ) {

        Map<String, FieldAccess> fieldMap = fieldsAccessor.getFields( toObject.getClass() );
        List<Field> fields = Reflection.getFields( toObject.getClass() );
        Object item;
        Class<?> paramType;
        Field field;

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
                        newList.add( fromList( fromList, componentType ) );
                    }
                    fieldAccess.setValue( toObject, newList );

                }
            } else if ( paramType.isInstance( item ) ) {
                fieldAccess.setValue( toObject, item );
            }
            else{
                fieldAccess.setValue( toObject, item );
            }
        }

        return toObject;

    }

    public static <T> T fromList( List<Object> argList, Class<T> clazz ) {
          return fromList( FieldAccessMode.FIELD_THEN_PROPERTY.create( false ), argList, clazz );
    }

    public static <T> T fromList( FieldsAccessor fieldsAccessor, List<Object> argList, Class<T> clazz ) {
        int size = argList.size();

        List<Object> list = new ArrayList( argList );
        Constructor<?>[] constructors = clazz.getConstructors();
        Constructor match = null;


        try {


            loop:
            for ( Constructor constructor : constructors ) {
                constructor.setAccessible( true );
                Class[] parameterTypes = constructor.getParameterTypes();
                if ( parameterTypes.length == size ) {

                    for ( int index = 0; index < size; index++ ) {
                        if ( !matchAndConvertConstructorArg(fieldsAccessor, list, constructor, parameterTypes, index ) ) continue loop;
                    }
                    match = constructor;
                }
            }

            if ( match != null ) {
                return ( T ) match.newInstance( list.toArray( new Object[list.size()] ) );
            } else {
                return fromListUsingFields( list, clazz );
            }

        } catch ( Exception e ) {

            if (match != null)  {
                return ( T ) Exceptions.handle( Object.class, e,
                        "\nconstructor parameter types", match.getParameterTypes(),
                        "\nlist args after conversion", list, "types",
                        gatherTypes( list ),
                        "\noriginal args", argList,
                        "original types", gatherTypes( argList ) );
            } else {
                return ( T ) Exceptions.handle( Object.class, e,
                        "\nlist args after conversion", list, "types",
                        gatherTypes( list ),
                        "\noriginal args", argList,
                        "original types", gatherTypes( argList ) );

            }
        }

    }

    private static boolean matchAndConvertConstructorArg( FieldsAccessor fieldsAccessor, List<Object> list, Constructor constructor, Class[] parameterTypes, int index ) {
        try {

            Class paramType;
            Object item;
            paramType = parameterTypes[index];
            item = list.get( index );
            if ( item instanceof ValueContainer ) {
                item = ( ( ValueContainer ) item ).toValue();
            }

            if ( Typ.isPrimitiveOrWrapper( paramType ) &&
                    ( item instanceof Number || item instanceof Boolean || item instanceof CharSequence ) ) {

                    Object o = Conversions.coerceOrDie( paramType, item );
                    list.set( index, o );
            }
            else if ( item instanceof Map && !Typ.isMap( paramType ) ) {
                list.set( index, fromMap(fieldsAccessor,  ( Map<String, Object> ) item, paramType ) );
            } else if ( item instanceof List && !Typ.isList( paramType ) ) {
                list.set( index, fromList(fieldsAccessor,  ( List<Object> ) item, paramType ) );
            } else if ( Typ.isList( paramType ) && item instanceof List ) {
                List<Object> itemList = ( List<Object> ) item;
                if ( itemList.size() > 0 && (itemList.get( 0 ) instanceof List || itemList.get(0) instanceof ValueContainer)  ) {
                    Type type = constructor.getGenericParameterTypes()[index];
                    if ( type instanceof ParameterizedType ) {
                        ParameterizedType pType = ( ParameterizedType ) type;
                        Class<?> componentType = ( Class<?> ) pType.getActualTypeArguments()[0];
                        List newList = new ArrayList( itemList.size() );

                        for ( Object o : itemList ) {
                            if ( o instanceof ValueContainer ) {
                                o = ( ( ValueContainer ) o ).toValue();
                            }

                            List fromList = ( List ) o;
                            newList.add( fromList( fieldsAccessor, fromList, componentType ) );
                        }
                        list.set( index, newList );

                    }
                }
            } else if ( paramType == Typ.string && item instanceof CharSequence ) {
                list.set( index, item.toString() );
            } else if ( !paramType.isInstance( item ) ) {
                return false;
            }
        } catch (Exception ex) {
            return false;
        }

        return true;
    }


    @SuppressWarnings( "unchecked" )
    public static <T> T fromMap( Map<String, Object> map, Class<T> clazz ) {
        return fromMap( map, Reflection.newInstance( clazz ) );

    }

    public static <T> T fromMap( Map<String, Object> map, Class<T> clazz, String... ignore ) {
        return fromMap( map, Reflection.newInstance( clazz ), ignore );

    }

    public static <T> T fromMap( FieldsAccessor fieldFieldsAccessor, Map<String, Object> map, Class<T> clazz, Set<String> ignore ) {
        return fromMap( fieldFieldsAccessor, map, Reflection.newInstance( clazz ), ignore );

    }

    public static <T> T fromMap( FieldsAccessor fieldFieldsAccessor, Map<String, Object> map, Class<T> clazz ) {

        return fromMap( fieldFieldsAccessor, map, Reflection.newInstance( clazz ) );

    }

    public static Object fromMap( Map<String, Object> map ) {
        return fromMap( FieldAccessMode.FIELD.create( false ), map );
    }

    @SuppressWarnings("unchecked")
    public static Object fromMap( FieldsAccessor fieldFieldsAccessor, Map<String, Object> map ) {
        String className = ( String ) map.get( "class" );
        Object newInstance = Reflection.newInstance( className );
        return fromMap( fieldFieldsAccessor, map, newInstance );
    }


    @SuppressWarnings("unchecked")
    public static Object fromMap( FieldsAccessor fieldFieldsAccessor, Map<String, Object> map, Set<String> ignoreSet ) {
        String className = ( String ) map.get( "class" );
        Object newInstance = Reflection.newInstance( className );
        return fromMap( fieldFieldsAccessor, map, newInstance, ignoreSet );
    }

    public static <T> T fromMap( Map<String, Object> map, T newInstance ) {
        return fromMap( FieldAccessMode.FIELD.create( false ), map, newInstance );
    }


    public static <T> T fromMap( Map<String, Object> map, T object, String... ignore ) {
        return fromMap( FieldAccessMode.FIELD.create( false ), map, object, ignore );
    }

    public static <T> T fromMap( Map<String, Object> map, T newInstance, Set<String> ignore ) {
        return fromMap( FieldAccessMode.FIELD.create( false ), map, newInstance, ignore );
    }

    @SuppressWarnings("unchecked")
    public static <T> T fromMap( FieldsAccessor fieldsAccessor, Map<String, Object> map, T object, String... ignore ) {
        Set<String> ignoreSet = Sets.set( ignore );
        return fromMap( fieldsAccessor, map, object, ignoreSet );
    }

    @SuppressWarnings("unchecked")
    public static <T> T fromMap( FieldsAccessor fieldsAccessor, Map<String, Object> map, T toObject, Set<String> ignoreSet ) {

        Map<String, FieldAccess> fields = fieldsAccessor.getFields( toObject.getClass() );
        Set<Map.Entry<String, Object>> entrySet = map.entrySet();


        /* Iterate through the fields. */
        //for ( FieldAccess field : fields ) {
        for ( Map.Entry<String, Object> entry : entrySet ) {
            String key = entry.getKey();

            if ( ignoreSet.contains( key ) ) {
                continue;
            }

            FieldAccess field = fields.get( key );

            if ( field == null ) {
                continue;
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
                Class<?> clazz = field.type();
                if ( !clazz.isInterface() && !Typ.isAbstract( clazz ) ) {
                    value = fromMap( fieldsAccessor, ( Map<String, Object> ) value, field.type(), ignoreSet );

                } else {
                    value = fromMap( fieldsAccessor, ( Map<String, Object> ) value, ignoreSet );
                }
                field.setObject( toObject, value );
            } else if ( value instanceof Collection ) {
                /*It is a collection so process it that way. */
                processCollectionFromMapUsingFields( fieldsAccessor, toObject, field, ( Collection ) value, ignoreSet );
            } else if ( value instanceof Map[] ) {
                /* It is an array of maps so, we need to process it as such. */
                processArrayOfMaps( fieldsAccessor, toObject, field, value );
            } else {
                field.setValue( toObject, value );
            }

        }

        return toObject;

    }

    @SuppressWarnings("unchecked")
    public static <T> T fromMap( FieldsAccessor fieldsAccessor, Map<String, Object> map, T newInstance ) {

        Map<String, FieldAccess> fields = fieldsAccessor.getFields( newInstance.getClass() );
        Set<Map.Entry<String, Object>> entrySet = map.entrySet();

        /* Iterate through the fields. */
        //for ( FieldAccess field : fields ) {
        for ( Map.Entry<String, Object> entry : entrySet ) {


            FieldAccess field = fields.get( entry.getKey() );

            if ( field == null ) {
                continue;
            }
            Object value = entry.getValue();


            if ( value instanceof Value ) {
                if ( ( ( Value ) value ).isContainer() ) {
                    value = ( ( Value ) value ).toValue();
                } else {
                    field.setFromValue( newInstance, ( Value ) value );
                    continue;
                }
            }

            if ( value == null ) {
                field.setObject( newInstance, null );
                continue;
            }

            if ( value.getClass() == field.type() ) {
                field.setObject( newInstance, value );
            } else if ( Typ.isBasicType( value ) ) {

                field.setValue( newInstance, value );
            } else if ( value instanceof Value ) {
                field.setValue( newInstance, value );
            }
            /* See if it is a map<string, object>, and if it is then process it. */
            //&& Typ.getKeyType ( ( Map<?, ?> ) value ) == Typ.string
            else if ( value instanceof Map ) {
                Class<?> clazz = field.type();
                if ( !clazz.isInterface() && !Typ.isAbstract( clazz ) ) {
                    value = fromMap( fieldsAccessor, ( Map<String, Object> ) value, field.type() );

                } else {
                    value = fromMap( fieldsAccessor, ( Map<String, Object> ) value );
                }
                field.setObject( newInstance, value );
            } else if ( value instanceof Collection ) {
                /*It is a collection so process it that way. */
                processCollectionFromMapUsingFields( fieldsAccessor, newInstance, field, ( Collection ) value );
            } else if ( value instanceof Map[] ) {
                /* It is an array of maps so, we need to process it as such. */
                processArrayOfMaps( fieldsAccessor, newInstance, field, value );
            } else {
                field.setValue( newInstance, value );
            }

        }

        return newInstance;
    }


    @SuppressWarnings("unchecked")
    public static <T> T fromValueMap(
            final FieldsAccessor fieldsAccessor,
            final Map<String, Value> map,
            final Class<T> clazz ) {

        return fromValueMap( fieldsAccessor, map, Reflection.newInstance( clazz ) );
    }


    @SuppressWarnings("unchecked")
    public static <T> T fromValueMap(
            final FieldsAccessor fieldsAccessor,
            final Map<String, Value> map ) {

        try {
            String className = map.get( "class" ).toString();
            Object newInstance = Reflection.newInstance( className );
            return fromValueMap( fieldsAccessor, map, ( T ) newInstance );
        } catch ( Exception ex ) {
            return ( T ) Exceptions.handle( Object.class, sputs( "fromValueMap", "map", map, "fieldAccessor", fieldsAccessor ), ex );
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T fromValueMap( final FieldsAccessor fieldsAccessor,
                                      final Map<String, Value> amap,
                                      final T newInstance ) {


        ValueMap map = ( ValueMap ) ( Map ) amap;


        Map<String, FieldAccess> fields = fieldsAccessor.getFields( newInstance.getClass() );
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


        for ( int index = 0; index < size; index++ ) {
            Map.Entry<String, Object> entry = entries[index];

            String key = entry.getKey();
            FieldAccess field = fields.get( key );
            Object ovalue = entry.getValue();

            if ( field == null ) {
                continue;
            }


            if ( ovalue instanceof Value ) {
                fromValueMapHandleValueCase( fieldsAccessor, newInstance, field, ( Value ) ovalue );
            } else {
                fromMapHandleNonValueCase( fieldsAccessor, newInstance, field, ovalue );
            }

        }

        return newInstance;
    }

    private static <T> void fromMapHandleNonValueCase( FieldsAccessor fieldsAccessor, T newInstance, FieldAccess field, Object ovalue ) {
        try {
            if ( ovalue instanceof Map ) {
                Class<?> clazz = field.type();
                if ( !clazz.isInterface() && !Typ.isAbstract( clazz ) ) {
                    ovalue = fromValueMap( fieldsAccessor, ( Map<String, Value> ) ovalue, field.type() );
                } else {
                    ovalue = fromValueMap( fieldsAccessor, ( Map<String, Value> ) ovalue );
                }
                field.setObject( newInstance, ovalue );
            } else if ( ovalue instanceof Collection ) {
                handleCollectionOfValues( fieldsAccessor, newInstance, field,
                        ( Collection<Value> ) ovalue );
            } else {
                field.setValue( newInstance, ovalue );
            }
        } catch ( Exception ex ) {
            Exceptions.handle( sputs( "Problem handling non value case of fromValueMap", "field", field.getName(),
                    "fieldType", field.type().getName(), "object from map", ovalue ), ex );
        }
    }

    private static <T> void fromValueMapHandleValueCase( FieldsAccessor fieldsAccessor, T newInstance, FieldAccess field, Value value ) {
        Object objValue = null;

        try {
            if ( value.isContainer() ) {
                objValue = value.toValue();
                if ( objValue instanceof Map ) {
                    Class<?> clazz = field.type();
                    if ( !clazz.isInterface() && !Typ.isAbstract( clazz ) ) {
                        objValue = fromValueMap( fieldsAccessor, ( Map<String, Value> ) objValue, field.type() );
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
                            objValue = fromValueMap( fieldsAccessor, ( Map<String, Value> ) objValue );
                        }
                    }
                    field.setObject( newInstance, objValue );
                } else if ( objValue instanceof Collection ) {
                    handleCollectionOfValues( fieldsAccessor, newInstance, field,
                            ( Collection<Value> ) objValue );
                }
            } else {
                field.setFromValue( newInstance, value );
            }
        } catch ( Exception ex ) {
            Exceptions.handle( sputs( "Problem handling non value case of fromValueMap", "field", field.getName(),
                    "fieldType", field.type().getName(), "object from map", "objValue", objValue, "value", value ), ex );

        }
    }


    @SuppressWarnings("unchecked")
    public static <T> T fromValueMap( final FieldsAccessor fieldsAccessor,
                                      final ValueMapImpl map,
                                      final T newInstance ) {


        Map<String, FieldAccess> fields = fieldsAccessor.getFields( newInstance.getClass() );
        Map.Entry<String, Value>[] entries;
        int size;

        if ( !map.hydrated() ) {
            size = map.len();
            entries = map.items();
        } else {
            size = map.size();
            entries = ( Map.Entry<String, Value>[] ) map.entrySet().toArray( new Map.Entry[size] );
        }

        /* guard. */
        if ( size == 0 || entries == null ) {
            return newInstance;
        }


        for ( int index = 0; index < size; index++ ) {
            Map.Entry<String, Value> entry = entries[index];

            String key = entry.getKey();
            FieldAccess field = fields.get( key );
            Value value = entry.getValue();


            if ( value.isContainer() ) {
                Object objValue;

                objValue = value.toValue();
                if ( objValue instanceof Map ) {

                    Class<?> clazz = field.type();
                    if ( !clazz.isInterface() && !Typ.isAbstract( clazz ) ) {
                        objValue = fromValueMap( fieldsAccessor, ( Map<String, Value> ) objValue, field.type() );

                    } else {

                        objValue = fromValueMap( fieldsAccessor, ( Map<String, Value> ) objValue );
                    }
                    field.setObject( newInstance, objValue );
                } else if ( objValue instanceof Collection ) {
                    handleCollectionOfValues( fieldsAccessor, newInstance, field,
                            ( Collection<Value> ) objValue );
                }

            } else {
                field.setFromValue( newInstance, value );
            }
        }

        return newInstance;
    }

    private static void processCollectionFromMapUsingFields(
            final FieldsAccessor fieldsAccessor, final Object newInstance,
            final FieldAccess field,
            final Collection<?> collection, final Set<String> ignoreSet ) {
        final Class<?> componentType = Reflection.getComponentType( collection );
        /** See if we have a collection of maps because if we do, then we have some
         * recursive processing to do.
         */
        if ( Typ.isMap( componentType ) ) {
            if ( ignoreSet != null ) {
                handleCollectionOfMaps( fieldsAccessor, newInstance, field,
                        ( Collection<Map<String, Object>> ) collection, ignoreSet );
            } else {
                handleCollectionOfMaps( fieldsAccessor, newInstance, field,
                        ( Collection<Map<String, Object>> ) collection );
            }
        } else if ( Typ.isValue( componentType ) ) {
            if ( ignoreSet != null ) {
                handleCollectionOfValues( fieldsAccessor, newInstance, field,
                        ( Collection<Value> ) collection, ignoreSet );
            } else {
                handleCollectionOfValues( fieldsAccessor, newInstance, field,
                        ( Collection<Value> ) collection );
            }


        } else {

            /* It might be a collection of regular types. */

            /*If it is a compatible type just inject it. */
            if ( field.type().isInterface() &&
                    Typ.implementsInterface( collection.getClass(), field.type() ) ) {

                field.setValue( newInstance, collection );

            } else {
                /* The type was not compatible so create a new collection that is. */
                Collection<Object> newCollection =
                        Reflection.createCollection( field.type(), collection.size() );

                newCollection.addAll( collection );
                field.setValue( newInstance, newCollection );

            }

        }


    }

    private static void processCollectionFromMapUsingFields(
            final FieldsAccessor fieldsAccessor, final Object newInstance,
            final FieldAccess field,
            final Collection<?> collection ) {
        processCollectionFromMapUsingFields( fieldsAccessor, newInstance, field, collection, null );
    }

    private static void processArrayOfMaps( final FieldsAccessor fieldsAccessor, Object newInstance, FieldAccess field, Object value ) {
        Map<String, Object>[] maps = ( Map<String, Object>[] ) value;
        List<Map<String, Object>> list = Lists.list( maps );
        handleCollectionOfMaps( fieldsAccessor, newInstance, field,
                list );

    }

    @SuppressWarnings("unchecked")
    private static void handleCollectionOfMaps( final FieldsAccessor fieldsAccessor, Object newInstance,
                                                FieldAccess field, Collection<Map<String, Object>> collectionOfMaps,
                                                final Set<String> ignoreSet ) {

        Collection<Object> newCollection = Reflection.createCollection( field.type(), collectionOfMaps.size() );


        Class<?> componentClass = field.getComponentClass();

        if ( componentClass != null ) {


            for ( Map<String, Object> mapComponent : collectionOfMaps ) {

                newCollection.add( fromMap( fieldsAccessor, mapComponent, componentClass, ignoreSet ) );

            }
            field.setObject( newInstance, newCollection );

        }

    }

    @SuppressWarnings("unchecked")
    private static void handleCollectionOfMaps( final FieldsAccessor fieldsAccessor, Object newInstance,
                                                FieldAccess field, Collection<Map<String, Object>> collectionOfMaps
    ) {

        Collection<Object> newCollection = Reflection.createCollection( field.type(), collectionOfMaps.size() );


        Class<?> componentClass = field.getComponentClass();

        if ( componentClass != null ) {


            for ( Map<String, Object> mapComponent : collectionOfMaps ) {

                newCollection.add( fromMap( fieldsAccessor, mapComponent, componentClass ) );

            }
            field.setObject( newInstance, newCollection );

        }

    }

    @SuppressWarnings("unchecked")
    private static void handleCollectionOfValues( FieldsAccessor fieldsAccessor, Object newInstance,
                                                  FieldAccess field, Collection<Value> acollectionOfValues ) {

        Collection collectionOfValues = acollectionOfValues;

        if ( collectionOfValues instanceof ValueList ) {
            collectionOfValues = ( ( ValueList ) collectionOfValues ).list();
        }

        Collection<Object> newCollection = Reflection.createCollection( field.type(), collectionOfValues.size() );



        if ( field.typeEnum().isCollection() ) {

            Class<?> componentClass = field.getComponentClass();

            for ( Value value : ( List<Value> ) collectionOfValues ) {

                if ( value.isContainer() ) {
                    Object oValue = value.toValue();
                    if ( oValue instanceof Map ) {
                        newCollection.add( fromValueMap( fieldsAccessor, ( Map ) oValue, componentClass ) );
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

    private static void handleCollectionOfValues( FieldsAccessor fieldsAccessor, Object newInstance,
                                                  FieldAccess field, Collection<Value> acollectionOfValues, Set<String> ignoreSet ) {

        Collection collectionOfValues = acollectionOfValues;

        if ( collectionOfValues instanceof ValueList ) {
            collectionOfValues = ( ( ValueList ) collectionOfValues ).list();
        }

        Collection<Object> newCollection = Reflection.createCollection( field.type(), collectionOfValues.size() );


        Class<?> componentClass = field.getComponentClass();

        if ( componentClass != null ) {


            for ( Value value : ( List<Value> ) collectionOfValues ) {

                if ( value.isContainer() ) {
                    Object oValue = value.toValue();
                    if ( oValue instanceof Map ) {
                        newCollection.add( fromValueMap( fieldsAccessor, ( Map ) oValue, componentClass ) );
                    }
                } else {
                    newCollection.add( Conversions.coerce( componentClass, value.toValue() ) );
                }


            }
            field.setObject( newInstance, newCollection );

        }

    }


    public static Map<String, Object> toMap( final Object object, final String... ignore ) {
        return toMap( object, Sets.set( ignore ) );
    }

    public static Map<String, Object> toMap( final Object object, Set<String> ignore ) {

        if ( object == null ) {
            return null;
        }

        Map<String, Object> map = new LinkedHashMap<>();


        class FieldToEntryConverter implements
                Conversions.Converter<Maps.Entry<String, Object>, FieldAccess> {
            @Override
            public Maps.Entry<String, Object> convert( FieldAccess from ) {
                if ( from.isReadOnly() ) {
                    return null;
                }
                Maps.Entry<String, Object> entry = new Maps.EntryImpl<>( from.getName(),
                        from.getValue( object ) );
                return entry;
            }
        }

        final Map<String, FieldAccess> fieldMap = Reflection.getAllAccessorFields( object.getClass() );
        List<FieldAccess> fields = new ArrayList( fieldMap.values() );


        Collections.reverse( fields ); // make super classes fields first that
        // their update get overridden by
        // subclass fields with the same name

        List<Maps.Entry<String, Object>> entries = Conversions.mapFilterNulls(
                new FieldToEntryConverter(), new ArrayList( fields ) );

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
            } else if ( Reflection.isArray( value )
                    && Typ.isBasicType( value.getClass().getComponentType() ) ) {
                map.put( key, entry.value() );
            } else if ( Reflection.isArray( value ) ) {
                int length = Reflection.arrayLength( value );
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


        class FieldToEntryConverter implements
                Conversions.Converter<Maps.Entry<String, Object>, FieldAccess> {
            @Override
            public Maps.Entry<String, Object> convert( FieldAccess from ) {
                if ( from.isReadOnly() ) {
                    return null;
                }
                Maps.Entry<String, Object> entry = new Maps.EntryImpl<>( from.getName(),
                        from.getValue( object ) );
                return entry;
            }
        }

        final Map<String, FieldAccess> fieldMap = Reflection.getAllAccessorFields( object.getClass() );
        List<FieldAccess> fields = new ArrayList( fieldMap.values() );


        Collections.reverse( fields ); // make super classes fields first that
        // their update get overridden by
        // subclass fields with the same name

        List<Maps.Entry<String, Object>> entries = Conversions.mapFilterNulls(
                new FieldToEntryConverter(), new ArrayList( fields ) );

        map.put( "class", object.getClass().getName() );

        for ( Maps.Entry<String, Object> entry : entries ) {
            Object value = entry.value();
            if ( value == null ) {
                continue;
            }
            if ( Typ.isBasicType( value ) ) {
                map.put( entry.key(), entry.value() );
            } else if ( Reflection.isArray( value )
                    && Typ.isBasicType( value.getClass().getComponentType() ) ) {
                map.put( entry.key(), entry.value() );
            } else if ( Reflection.isArray( value ) ) {
                int length = Reflection.arrayLength( value );
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

    public static <T> List<T> convertListOfMapsToObjects( FieldsAccessor fieldsAccessor, Class<T> componentType, List<Object> list ) {
        List<Object> newList = new ArrayList<>( list.size() );
        for ( Object obj : list ) {

            if ( obj instanceof Value ) {
                obj = ( ( Value ) obj ).toValue();
            }

            if ( obj instanceof Map ) {

                Map map = ( Map ) obj;
                if ( map instanceof ValueMapImpl ) {
                    newList.add( fromValueMap( fieldsAccessor, ( Map<String, Value> ) map, componentType ) );
                } else {
                    newList.add( fromMap( fieldsAccessor, map, componentType ) );
                }
            } else {
                newList.add( Conversions.coerce( componentType, obj ) );
            }
        }
        return ( List<T> ) newList;
    }
}
