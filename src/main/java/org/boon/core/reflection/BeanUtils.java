package org.boon.core.reflection;

import org.boon.Exceptions;
import org.boon.StringScanner;
import org.boon.core.Conversions;
import org.boon.core.Typ;
import org.boon.core.Type;
import org.boon.core.reflection.fields.FieldAccess;
import org.boon.core.reflection.fields.MapField;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static org.boon.Boon.sputs;
import static org.boon.Exceptions.die;
import static org.boon.StringScanner.isDigits;

/**
 * Created by rick on 12/20/13.
 */
public class BeanUtils {


    /**
     * This returns getPropertyFieldFieldAccessMap(clazz, true, true);
     *
     * @param clazz gets the properties or fields of this class.
     * @return
     */
    public static Map<String, FieldAccess> getPropertyFieldAccessMap( Class<?> clazz ) {
        return Reflection.getPropertyFieldAccessMapFieldFirst( clazz );
    }


    /**
     * Get fields from object or Map.
     * Allows maps to act like they have fields.
     *
     * @param item
     * @return
     */
    public static Map<String, FieldAccess> getFieldsFromObject( Object item ) {
        Map<String, FieldAccess> fields = null;

        fields = getPropertyFieldAccessMap( item.getClass() );

        if ( item instanceof Map ) {
            fields = getFieldsFromMap( fields, ( Map<String, Object> ) item );
        }
        return fields;

    }


    /**
     * Get fields from map.
     *
     * @param fields
     * @param map
     * @return
     */
    private static Map<String, FieldAccess> getFieldsFromMap( Map<String, FieldAccess> fields, Map<String, Object> map ) {

        for ( Map.Entry<String, Object> entry : map.entrySet() ) {
            fields.put( entry.getKey(), new MapField( entry.getKey() ) );
        }
        return fields;

    }


    /**
     * Get property value, loads nested properties
     *
     * @param root
     * @param properties
     * @return
     */
    public static void setPropertyValue( final Object root, final Object newValue, final String... properties ) {
        Objects.requireNonNull( root );
        Objects.requireNonNull( properties );


        Object object = root;
        Object parent = root;

        int index = 0;
        for ( String property : properties ) {
            Map<String, FieldAccess> fields = getPropertyFieldAccessMap( object.getClass() );

            FieldAccess field = fields.get( property );


            if ( isDigits( property ) ) {
                /* We can index numbers and names. */
                object = Reflection.idx( object, Integer.parseInt( property ) );

            } else {

                if ( field == null ) {
                    die( sputs(
                            "We were unable to access property=", property,
                            "\nThe properties passed were=", properties,
                            "\nThe root object is =", root.getClass().getName(),
                            "\nThe current object is =", object.getClass().getName()
                    )
                    );
                }


                if ( index == properties.length - 1 ) {
                    field.setValue( object, newValue );
                } else {
                    object = field.getObject( object );
                }
            }

            index++;
        }

    }


    /**
     * Get property value, loads nested properties
     *
     * @param root
     * @param properties
     * @return
     */
    public static Object getPropertyValue( final Object root, final String... properties ) {
        Objects.requireNonNull( root );
        Objects.requireNonNull( properties );


        Object object = root;

        for ( String property : properties ) {

            Map<String, FieldAccess> fields = getFieldsFromObject( object );

            FieldAccess field = fields.get( property );

            if ( isDigits( property ) ) {
                /* We can index numbers and names. */
                object = Reflection.idx( object, Integer.parseInt( property ) );

            } else {

                if ( field == null ) {
                    die( sputs(
                            "We were unable to access property=", property,
                            "\nThe properties passed were=", properties,
                            "\nThe root object is =", root.getClass().getName(),
                            "\nThe current object is =", object.getClass().getName()
                    )
                    );
                }

                object = field.getObject( object );
            }
        }
        return object;
    }


    /**
     * Get property value, loads nested properties
     *
     * @param root
     * @param property
     * @return
     */
    public static Class<?> getPropertyType( final Object root, final String property ) {
        Objects.requireNonNull( root );
        Objects.requireNonNull( property );

        Map<String, FieldAccess> fields = getPropertyFieldAccessMap( root.getClass() );

        FieldAccess field = fields.get( property );
        return field.getType();
    }


    @SuppressWarnings ( "unchecked" )
    public static <T> T idxGeneric( Class<T> t, Object object, final String path ) {

        Objects.requireNonNull( object );
        Objects.requireNonNull( path );

        String[] properties = StringScanner.splitByDelimiters( path, ".[]" );

        return ( T ) getPropByPath( object, properties );


    }

    public static <T> List<T> idxList( Class<T> cls, Object items, String... path ) {
        return ( List<T> ) getPropByPath( items, path );
    }


    /**
     * This method handles walking lists of lists.
     *
     * @param item
     * @param path
     * @return
     */
    public static Object getPropByPath( Object item, String... path ) {
        Object o = item;
        for ( int index = 0; index < path.length; index++ ) {
            String propName = path[ index ];
            if ( o == null ) {
                return null;
            } else if ( Reflection.isArray( o ) || o instanceof Collection ) {
                o = getCollecitonProp( o, propName, index, path );
                break;
            } else {
                o = getProp( o, propName );
            }
        }
        return Conversions.unifyList ( o );
    }


    /**
     * Get property value
     *
     * @param object
     * @param path   in dotted notation
     * @return
     */
    public static Object idx( Object object, String path ) {

        Objects.requireNonNull( object );
        Objects.requireNonNull( path );

        String[] properties = StringScanner.splitByDelimiters( path, ".[]" );

        return getPropertyValue( object, properties );
    }

    /**
     * @param object
     * @param path
     * @return
     */
    public static Object idxRelax( Object object, final String path ) {
        Objects.requireNonNull( object );
        Objects.requireNonNull( path );

        String[] properties = StringScanner.splitByDelimiters( path, ".[]" );

        return getPropByPath( object, properties );
    }


    /**
     * This is an amazing little recursive method. It walks a fanout of
     * nested collection to pull out the leaf nodes
     */
    private static Object getCollecitonProp( Object o, String propName, int index, String[] path ) {
        o = getFieldValues ( o, propName );

        if ( index + 1 == path.length ) {
            return o;
        } else {
            index++;
            return getCollecitonProp( o, path[ index ], index, path );
        }
    }


    /**
     * This is one is forgiving of null paths.
     * This works with getters first, i.e., properties.
     *
     * @param object
     * @param property
     * @return
     */
    public static Object getProp( Object object, final String property ) {
        if ( object == null ) {
            return null;
        }

        if ( isDigits( property ) ) {
                /* We can index numbers and names. */
            object = Reflection.idx( object, Integer.parseInt( property ) );

        }

        Class<?> cls = object.getClass();

        /** Tries the getters first. */
        Map<String, FieldAccess> fields = Reflection.getPropertyFieldAccessors( cls );

        if ( !fields.containsKey( property ) ) {
            fields = Reflection.getAllAccessorFields( cls );
        }

        if ( !fields.containsKey( property ) ) {
            return null;
        } else {
            return fields.get( property ).getValue( object );
        }

    }


    /**
     * Get an int property.
     */
    public static int getPropertyInt( final Object root, final String... properties ) {

        Objects.requireNonNull( root );
        Objects.requireNonNull( properties );


        Object object = baseForGetProperty( root, properties );

        Map<String, FieldAccess> fields = getPropertyFieldAccessMap( object.getClass() );
        final String lastProperty = properties[ properties.length - 1 ];
        FieldAccess field = fields.get( lastProperty );

        if ( field.getType() == Typ.intgr ) {
            return field.getInt( object );
        } else {
            return Conversions.toInt( field.getValue( object ) );
        }

    }


    /**
     * @param root
     * @param properties
     * @return
     */
    private static Object baseForGetProperty( Object root, String[] properties ) {
        Object object = root;

        Map<String, FieldAccess> fields = null;

        for ( int index = 0; index < properties.length - 1; index++ ) {
            fields = getPropertyFieldAccessMap( object.getClass() );

            String property = properties[ index ];
            FieldAccess field = fields.get( property );

            if ( isDigits( property ) ) {
                /* We can index numbers and names. */
                object = Reflection.idx( object, Integer.parseInt( property ) );

            } else {

                if ( field == null ) {
                    die( sputs(
                            "We were unable to access property=", property,
                            "\nThe properties passed were=", properties,
                            "\nThe root object is =", root.getClass().getName(),
                            "\nThe current object is =", object.getClass().getName()
                    )
                    );
                }

                object = field.getObject( object );
            }
        }
        return object;
    }

    /**
     * Get property value
     *
     * @param object
     * @param path   in dotted notation
     * @return
     */
    public static int idxInt( Object object, String path ) {

        Objects.requireNonNull( object );
        Objects.requireNonNull( path );

        String[] properties = StringScanner.splitByDelimiters( path, ".[]" );

        return getPropertyInt( object, properties );
    }


    /**
     * @param root
     * @param properties
     * @return
     */
    public static byte getPropertyByte( final Object root, final String... properties ) {
        Object object = baseForGetProperty( root, properties );

        Map<String, FieldAccess> fields = getPropertyFieldAccessMap( object.getClass() );
        final String lastProperty = properties[ properties.length - 1 ];
        FieldAccess field = fields.get( lastProperty );

        if ( field.getType() == Typ.bt ) {
            return field.getByte( object );
        } else {
            return Conversions.toByte( field.getValue( object ) );
        }
    }

    /**
     * @param object
     * @param path
     * @return
     */
    public static byte idxByte( Object object, String path ) {

        Objects.requireNonNull( object );
        Objects.requireNonNull( path );

        String[] properties = StringScanner.splitByDelimiters( path, ".[]" );

        return getPropertyByte( object, properties );
    }

    /**
     * @param root
     * @param properties
     * @return
     */
    public static float getPropertyFloat( final Object root, final String... properties ) {
        Object object = baseForGetProperty( root, properties );

        Map<String, FieldAccess> fields = getPropertyFieldAccessMap( object.getClass() );
        final String lastProperty = properties[ properties.length - 1 ];
        FieldAccess field = fields.get( lastProperty );

        if ( field.getType() == Typ.flt ) {
            return field.getFloat( object );
        } else {
            return Conversions.toFloat( field.getValue( object ) );
        }
    }


    /**
     * @param object
     * @param path
     * @return
     */
    public static float idxFloat( Object object, String path ) {

        Objects.requireNonNull( object );
        Objects.requireNonNull( path );

        String[] properties = StringScanner.splitByDelimiters( path, ".[]" );

        return getPropertyFloat( object, properties );
    }


    /**
     * @param root
     * @param properties
     * @return
     */
    public static short getPropertyShort( final Object root,
                                          final String... properties ) {


        Object object = baseForGetProperty( root, properties );

        Map<String, FieldAccess> fields = getPropertyFieldAccessMap( object.getClass() );
        final String lastProperty = properties[ properties.length - 1 ];
        FieldAccess field = fields.get( lastProperty );


        if ( field.getType() == Typ.shrt ) {
            return field.getShort( object );
        } else {
            return Conversions.toShort( field.getValue( object ) );
        }
    }


    /**
     * @param object
     * @param path
     * @return
     */
    public static short idxShort( Object object, String path ) {

        Objects.requireNonNull( object );
        Objects.requireNonNull( path );

        String[] properties = StringScanner.splitByDelimiters( path, ".[]" );

        return getPropertyShort( object, properties );
    }

    /**
     * @param root
     * @param properties
     * @return
     */
    public static char getPropertyChar( final Object root,
                                        final String... properties ) {

        Object object = baseForGetProperty( root, properties );

        Map<String, FieldAccess> fields = getPropertyFieldAccessMap( object.getClass() );
        final String lastProperty = properties[ properties.length - 1 ];
        FieldAccess field = fields.get( lastProperty );

        if ( field.getType() == Typ.chr ) {
            return field.getChar( object );
        } else {
            return Conversions.toChar( field.getValue( object ) );
        }
    }


    /**
     * @param object
     * @param path
     * @return
     */
    public static char idxChar( Object object, String path ) {

        Objects.requireNonNull( object );
        Objects.requireNonNull( path );

        String[] properties = StringScanner.splitByDelimiters( path, ".[]" );

        return getPropertyChar( object, properties );
    }


    /**
     * @param root
     * @param properties
     * @return
     */
    public static double getPropertyDouble( final Object root,
                                            final String... properties ) {


        Object object = baseForGetProperty( root, properties );

        Map<String, FieldAccess> fields = getPropertyFieldAccessMap( object.getClass() );
        final String lastProperty = properties[ properties.length - 1 ];
        FieldAccess field = fields.get( lastProperty );

        if ( field.getType() == Typ.dbl ) {
            return field.getDouble( object );
        } else {
            return Conversions.toDouble( field.getValue( object ) );
        }
    }


    /**
     * @param object
     * @param path
     * @return
     */
    public static double idxDouble( Object object, String path ) {

        Objects.requireNonNull( object );
        Objects.requireNonNull( path );

        String[] properties = StringScanner.splitByDelimiters( path, ".[]" );

        return getPropertyDouble( object, properties );
    }


    /**
     * @param root
     * @param properties
     * @return
     */
    public static long getPropertyLong( final Object root,
                                        final String... properties ) {


        Object object = baseForGetProperty( root, properties );

        Map<String, FieldAccess> fields = getPropertyFieldAccessMap( object.getClass() );
        final String lastProperty = properties[ properties.length - 1 ];
        FieldAccess field = fields.get( lastProperty );

        if ( field.getType() == Typ.lng ) {
            return field.getLong( object );
        } else {
            return Conversions.toLong( field.getValue( object ) );
        }
    }


    /**
     * @param object
     * @param path
     * @return
     */
    public static long idxLong( Object object, String path ) {

        Objects.requireNonNull( object );
        Objects.requireNonNull( path );

        String[] properties = StringScanner.splitByDelimiters( path, ".[]" );

        return getPropertyLong( object, properties );
    }


    /**
     * @param root
     * @param properties
     * @return
     */
    public static boolean getPropertyBoolean( final Object root,
                                              final String... properties ) {


        Object object = baseForGetProperty( root, properties );

        Map<String, FieldAccess> fields = getPropertyFieldAccessMap( object.getClass() );
        final String lastProperty = properties[ properties.length - 1 ];
        FieldAccess field = fields.get( lastProperty );

        if ( field.getType() == Typ.bln ) {
            return field.getBoolean( object );
        } else {
            return Conversions.toBoolean( field.getValue( object ) );
        }
    }


    public static boolean idxBoolean( Object object, String path ) {

        Objects.requireNonNull( object );
        Objects.requireNonNull( path );

        String[] properties = StringScanner.splitByDelimiters( path, ".[]" );

        return getPropertyBoolean( object, properties );
    }


    public static <V> Map<String, V> collectionToMap( String propertyKey, Collection<V> values ) {
        LinkedHashMap<String, V> map = new LinkedHashMap<String, V>( values.size() );
        Iterator<V> iterator = values.iterator();
        for ( V v : values ) {
            String key = idxGeneric( Typ.string, v, propertyKey );
            map.put( key, v );
        }
        return map;
    }


    public static void copyProperties( Object object, Map<String, Object> properties ) {

        Set<Map.Entry<String, Object>> props = properties.entrySet();
        for ( Map.Entry<String, Object> entry : props ) {
            setPropertyValue( object, entry.getValue(), entry.getKey() );
        }
    }


    public static Object getFieldValues( Object object, final String key ) {
        if ( object == null ) {
            return null;
        }
        if ( Reflection.isArray( object ) || object instanceof Collection ) {
            Iterator iter = Conversions.iterator( object );
            List list = new ArrayList( Reflection.len( object ) );
            while ( iter.hasNext() ) {
                list.add( getFieldValues( iter.next(), key ) );
            }
            return list;
        } else {
            return getFieldValue ( object, key );
        }
    }



    private static Object getFieldValue( Object object, final String key ) {
        if ( object == null ) {
            return null;
        }

        Class<?> cls = object.getClass();

        Map<String, FieldAccess> fields = Reflection.getPropertyFieldAccessMapFieldFirst( cls );

        if ( !fields.containsKey( key ) ) {
            return null;
        } else {
            return fields.get( key ).getValue( object );
        }
    }


    public static <T> T copy( T item ) {
        if ( item instanceof Cloneable ) {
            try {
                Method method = item.getClass().getMethod( "clone", ( Class[] ) null );
                method.setAccessible( true );
                return ( T ) method.invoke( item, ( Object[] ) null );
            } catch ( NoSuchMethodException | InvocationTargetException | IllegalAccessException ex ) {
                return fieldByFieldCopy( item );
            }
        } else {
            return fieldByFieldCopy( item );
        }
    }


    private static <T> T fieldByFieldCopy( T item ) {

        final Class<T> aClass = (Class<T>) item.getClass();
        Map<String, FieldAccess> fields = Reflection.getAllAccessorFields( aClass );

        T clone = Reflection.newInstance( aClass );

        for ( FieldAccess field : fields.values() ) {
            try {
            if ( field.isStatic() ) {
                continue;
            }
            if (!field.isPrimitive() && !Typ.isBasicType( field.getType() ))  {



                Object value = field.getObject( item );
                if (value == null) {
                    field.setObject(clone, null);
                } else {
                    field.setObject( clone, copy( value ) );
                }
            } else if (field.isPrimitive()) {
                field.setValue( clone, field.getValue( item ) );
            } else {
                Object value = field.getObject( item );

                if (value == null) {
                    field.setObject(clone, null);
                } else {
                    field.setObject( clone,  value  );
                }

            }
            }catch (Exception ex) {
                return (T) Exceptions.handle(Object.class, ""+field,  ex );
            }
        }
        return clone;
    }




    public static void copyProperties( Object src, Object dest ) {
         fieldByFieldCopy( src, dest );
    }

    private static void fieldByFieldCopy( Object src, Object dst ) {

        final Class<?> srcClass = src.getClass();
        Map<String, FieldAccess> srcFields = Reflection.getAllAccessorFields( srcClass );


        final Class<?> dstClass =  dst.getClass();
        Map<String, FieldAccess> dstFields = Reflection.getAllAccessorFields ( dstClass );

        for ( FieldAccess srcField : srcFields.values() ) {

            FieldAccess dstField = dstFields.get ( srcField.getName() );
            try {

                copySrcFieldToDestField ( src, dst, dstField, srcField );

            }catch (Exception ex) {
                 Exceptions.handle( sputs("copying field", srcField.getName (), srcClass, " to ", dstField.getName(), dstClass), ex );
            }
        }
    }

    private static void copySrcFieldToDestField( Object src, Object dst, FieldAccess dstField, FieldAccess srcField ) {
        if ( srcField.isStatic() ) {
            return ;
        }

        if (dstField == null ) {
            return ;
        }

                /* If its primitive handle it. */
        if ( srcField.isPrimitive() ) {
            dstField.setValue( dst, srcField.getValue( src ) );
            return ;
        }

        Object srcValue = srcField.getObject( src );

                /* if value is null then handle it unless it is primitive.*/
        if (srcValue == null) {
            if ( !dstField.isPrimitive () ) {
                dstField.setObject(dst, null);
            }
            return ;
        }





                /* Basic type. */
        if ( Typ.isBasicType( srcField.getType() ) ) {
            /* Handle non primitive copy. */
            Object value = srcField.getObject( src );
            dstField.setValue( dst,  value  );
            return ;
        }

                /* Types match and not a collection so just copy. */
        if (    !(srcValue instanceof Collection ) && dstField.getType() == srcValue.getClass() ||
                Typ.isSuperType ( dstField.getType (), srcValue.getClass () ) ) {

            dstField.setObject(dst, copy( srcField.getObject ( src ) ));
            return ;
        }



                /* Collection field copy. */
        if ( srcValue instanceof Collection && dstField.getComponentClass() != null
                            && Typ.isCollection ( dstField.getType () )
                            ) {

            handleCollectionFieldCopy ( dst, dstField, ( Collection ) srcValue );
            return ;

        }


                      /* Non identical object copy. */
        if (dstField.typeEnum () == Type.ABSTRACT || dstField.typeEnum () == Type.INTERFACE) {
                            //no op
        } else {
                Object newInstance = Reflection.newInstance ( dstField.getType () );
                fieldByFieldCopy( srcField.getObject( src ), newInstance );
                dstField.setObject ( dst, newInstance );
        }
    }

    private static void handleCollectionFieldCopy( Object dst, FieldAccess dstField, Collection srcValue ) {
        if ( dstField.getComponentClass () != Typ.string )  {

            Collection dstCollection = Reflection.createCollection ( dstField.getType (), srcValue.size () );
            for ( Object srcComponentValue : srcValue ) {

                Object newInstance = Reflection.newInstance( dstField.getComponentClass() );
                fieldByFieldCopy( srcComponentValue, newInstance );
                dstCollection.add ( newInstance );
            }

            dstField.setObject ( dst, dstCollection );
        } else {

            Collection dstCollection = Reflection.createCollection( dstField.getType(), srcValue.size() );
            for ( Object srcComponentValue : srcValue ) {

                if (srcComponentValue!=null) {
                    dstCollection.add ( srcComponentValue.toString () );
                }
            }

            dstField.setObject ( dst, dstCollection );

        }
    }

}
