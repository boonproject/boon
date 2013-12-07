package org.boon.core.reflection;

import org.boon.*;
import org.boon.core.Typ;
import org.boon.core.Value;
import org.boon.core.reflection.fields.*;
import org.boon.primitive.CharBuf;
import sun.misc.Unsafe;

import java.lang.ref.WeakReference;
import java.lang.reflect.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import static org.boon.Boon.sputs;
import static org.boon.Exceptions.die;
import static org.boon.Str.*;
import static org.boon.StringScanner.isDigits;


public class Reflection {

    private static final Logger log = Logger.getLogger ( Reflection.class.getName ( ) );
    private final static Set<String> fieldSortNames = Sets.set ( "name", "orderBy", "title", "key" );
    private final static Set<String> fieldSortNamesSuffixes = Sets.set ( "Name", "Title", "Key" );

    private static boolean _useUnsafe;
    private static boolean _inContainer;

    private final static Context _context;
    private static WeakReference<Context> weakContext = new WeakReference<> ( null );




    public static Unsafe getUnsafe( ) {
        if (context ().control==null) {
            try {
                Field f = Unsafe.class.getDeclaredField ( "theUnsafe" );
                f.setAccessible ( true );
                context().control = ( Unsafe ) f.get ( null );
                return context().control;
            } catch ( Exception e ) {
                return null;
            }
        } else {
            return context().control;
        }
    }


    static {
        try {
            Class.forName ( "sun.misc.Unsafe" );
            _useUnsafe = true;
        } catch ( ClassNotFoundException e ) {
            e.printStackTrace ( );
            _useUnsafe = false;
        }

        _useUnsafe = _useUnsafe && !Boolean.getBoolean ( "org.boon.noUnsafe" );
    }

    private static final boolean useUnsafe = _useUnsafe;


    static {
        try {
            Class.forName ( "javax.servlet.http.HttpServlet" );
            _inContainer = true;
        } catch ( ClassNotFoundException e ) {
            _inContainer = false;
        }
    }

    private final static boolean inContainer = _inContainer;

    static {

        boolean noStatics = Boolean.getBoolean ( "org.boon.noStatics" );
        if ( noStatics || inContainer ) {

            _context = null;
            weakContext = new WeakReference<> ( new Context ( ) );

        } else {
;
            _context = new Context ( );
        }
    }


    private static void setSortableField( Class<?> clazz, String fieldName ) {
        context ( )._sortableFields.put ( clazz.getName ( ), fieldName );
    }

    private static String getSortableField( Class<?> clazz ) {
        return context ( )._sortableFields.get ( clazz.getName ( ) );
    }


    /* Manages weak references. */
    private static Context context( ) {

        if ( _context != null ) {
            return _context;
        } else {
            Context context = weakContext.get ( );
            if ( context == null ) {
                context = new Context ( );
                weakContext = new WeakReference<> ( context );
            }
            return context;
        }
    }

    private static class Context {

        private Unsafe control;
        private Map<String, String> _sortableFields = new ConcurrentHashMap<> ( );

        private Map<Class<?>, Map<String, FieldAccess>> _allAccessorReflectionFieldsCache = new ConcurrentHashMap<> (200 );
        private Map<Class<?>, Map<String, FieldAccess>> _allAccessorPropertyFieldsCache = new ConcurrentHashMap<> (200 );
        private Map<Class<?>, Map<String, FieldAccess>> _allAccessorUnsafeFieldsCache = new ConcurrentHashMap<> (200 );


        private FieldAccess stringValueField;

    }


    static {
        try {
            if (_useUnsafe) {
                Field field = String.class.getDeclaredField ( "value" );
                context().stringValueField =   UnsafeField.createUnsafeField ( field );
            }
        } catch (Exception ex) {
            Exceptions.handle(ex);
        }
    }


    private static void setAccessorFieldInCache( Class<? extends Object> theClass, boolean useUnsafe, Map<String, FieldAccess> map ) {
        if (useUnsafe)  {
            context ( )._allAccessorUnsafeFieldsCache.put ( theClass, map );
        } else {
            context ( )._allAccessorReflectionFieldsCache.put ( theClass, map );

        }
    }

    private static void setPropertyAccessorFieldsInCache( Class<? extends Object> theClass,  Map<String, FieldAccess> map ) {
            context ( )._allAccessorPropertyFieldsCache.put ( theClass, map );
    }


    private static Map<String, FieldAccess> getPropertyAccessorFieldsFromCache( Class<? extends Object> theClass) {
        return context ( )._allAccessorPropertyFieldsCache.get ( theClass  );
    }

    private static Map<String, FieldAccess> getAccesorFieldFromCache( Class<? extends Object> theClass, boolean useUnsafe ) {

        if (useUnsafe)  {
            return context ( )._allAccessorUnsafeFieldsCache.get ( theClass   );
        } else {
            return context ( )._allAccessorReflectionFieldsCache.get ( theClass   );

        }
    }



    /**
     * This returns getPropertyFieldFieldAccessMap(clazz, true, true);
     *
     * @param clazz gets the properties or fields of this class.
     * @return
     * @see Reflection#getPropertyFieldAccessMap(Class, boolean, boolean)
     */
    public static Map<String, FieldAccess> getPropertyFieldAccessMap( Class<?> clazz ) {
        return getPropertyFieldAccessMap ( clazz, true, true );
    }

    /**
     * Gets a listStream of fields merges with properties if field is not found.
     *
     * @param clazz         get the properties or fields
     * @param useFieldFirst try to use the field first if this is set
     * @param useUnSafe     use unsafe if it is available for speed.
     * @return
     */
    public static Map<String, FieldAccess> getPropertyFieldAccessMap( Class<?> clazz, boolean useFieldFirst, boolean useUnSafe ) {
        /* Fallback map. */
        Map<String, FieldAccess> fieldsFallbacks = null;

        /* Primary merge into this one. */
        Map<String, FieldAccess> fieldsPrimary = null;


        /* Try to find the fields first if this is set. */
        if ( useFieldFirst ) {
            fieldsPrimary = Reflection.getAllAccessorFields ( clazz, useUnSafe );

            fieldsFallbacks = Reflection.getPropertyFieldAccessors ( clazz );

        } else {

             /* Try to find the properties first if this is set. */
            fieldsFallbacks = Reflection.getAllAccessorFields ( clazz, useUnSafe );
            fieldsPrimary = Reflection.getPropertyFieldAccessors ( clazz );

        }

        /* Add missing fields */
        for ( Map.Entry<String, FieldAccess> field : fieldsFallbacks.entrySet ( ) ) {
            if ( !fieldsPrimary.containsKey ( field.getKey ( ) ) ) {
                fieldsPrimary.put ( field.getKey ( ), field.getValue ( ) );
            }
        }

        return fieldsPrimary;
    }


    /**
     * Checks to see if we have a string field.
     *
     * @param value1
     * @param name
     * @return
     */
    public static boolean hasStringField( final Object value1, final String name ) {

        Class<?> clz = value1.getClass ( );
        return classHasStringField ( clz, name );
    }

    /**
     * Checks to see if this class has a string field.
     *
     * @param clz
     * @param name
     * @return
     */
    public static boolean classHasStringField( Class<?> clz, String name ) {

        List<Field> fields = getAllFields ( clz );
        for ( Field field : fields ) {
            if (
                    field.getType ( ).equals ( Typ.string ) &&
                            field.getName ( ).equals ( name ) &&
                            !Modifier.isStatic ( field.getModifiers ( ) ) &&
                            field.getDeclaringClass ( ) == clz
                    ) {
                return true;
            }
        }

        return false;
    }


    /**
     * Checks to if an instance has a field
     *
     * @param value1
     * @param name
     * @return
     */
    public static boolean hasField( Object value1, String name ) {
        return classHasField ( value1.getClass ( ), name );
    }

    /**
     * Checks to see if a class has a field.
     *
     * @param clz
     * @param name
     * @return
     */
    public static boolean classHasField( Class<?> clz, String name ) {
        List<Field> fields = getAllFields ( clz );
        for ( Field field : fields ) {
            if ( field.getName ( ).equals ( name )
                    && !Modifier.isStatic ( field.getModifiers ( ) )
                    && field.getDeclaringClass ( ) == clz ) {
                return true;
            }
        }

        return false;
    }

    /**
     * This can be used for default sort.
     *
     * @param value1 value we are analyzing
     * @return first field that is comparable or primitive.
     */
    public static String getFirstComparableOrPrimitive( Object value1 ) {
        return getFirstComparableOrPrimitiveFromClass ( value1.getClass ( ) );
    }

    /**
     * This can be used for default sort.
     *
     * @param clz class we are analyzing
     * @return first field that is comparable or primitive or null if not found.
     */
    public static String getFirstComparableOrPrimitiveFromClass( Class<?> clz ) {
        List<Field> fields = getAllFields ( clz );
        for ( Field field : fields ) {

            if ( ( field.getType ( ).isPrimitive ( ) || Typ.isComparable ( field.getType ( ) )
                    && !Modifier.isStatic ( field.getModifiers ( ) )
                    && field.getDeclaringClass ( ) == clz )
                    ) {
                return field.getName ( );
            }
        }

        return null;
    }

    /**
     * getFirstStringFieldNameEndsWith
     *
     * @param value object we are looking at
     * @param name  name
     * @return field name or null
     */
    public static String getFirstStringFieldNameEndsWith( Object value, String name ) {
        return getFirstStringFieldNameEndsWithFromClass ( value.getClass ( ), name );
    }

    /**
     * getFirstStringFieldNameEndsWithFromClass
     *
     * @param clz  class we are looking at
     * @param name name
     * @return field name or null
     */
    public static String getFirstStringFieldNameEndsWithFromClass( Class<?> clz, String name ) {
        List<Field> fields = getAllFields ( clz );
        for ( Field field : fields ) {
            if (
                    field.getName ( ).endsWith ( name )
                            && field.getType ( ).equals ( Typ.string )
                            && !Modifier.isStatic ( field.getModifiers ( ) )
                            && field.getDeclaringClass ( ) == clz ) {

                return field.getName ( );
            }
        }

        return null;
    }


    /**
     * Gets the first sortable fields found.
     *
     * @param value1
     * @return sortable field
     */
    public static String getSortableField( Object value1 ) {
        return getSortableFieldFromClass ( value1.getClass ( ) );
    }

    /**
     * Gets the first sortable field.
     *
     * @param clazz the class we are getting the sortable field from.
     * @return sortable field
     */
    public static String getSortableFieldFromClass( Class<?> clazz ) {

        /** See if the fieldName is in the field listStream already.
         * We keep a hashmap cache.
         * */
        String fieldName = getSortableField ( clazz );

        /**
         * Not found in cache.
         */
        if ( fieldName == null ) {

            /* See if we have this sortale field and look for string first. */
            for ( String name : fieldSortNames ) {
                if ( classHasStringField ( clazz, name ) ) {
                    fieldName = name;
                    break;
                }
            }

            /*
             Now see if we can find one of our predefined suffixes.
             */
            if ( fieldName == null ) {
                for ( String name : fieldSortNamesSuffixes ) {
                    fieldName = getFirstStringFieldNameEndsWithFromClass ( clazz, name );
                    if ( fieldName != null ) {
                        break;
                    }
                }
            }

            /**
             * Ok. We still did not find it so give us the first
             * primitive that we can find.
             */
            if ( fieldName == null ) {
                fieldName = getFirstComparableOrPrimitiveFromClass ( clazz );
            }

            /* We could not find a sortable field. */
            if ( fieldName == null ) {
                setSortableField ( clazz, "NOT FOUND" );
                die ( "Could not find a sortable field for type " + clazz );

            }

            /* We found a sortable field. */
            setSortableField ( clazz, fieldName );
        }
        return fieldName;

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

        fields = Reflection.getPropertyFieldAccessMap ( item.getClass ( ) );

        if ( item instanceof Map ) {
            fields = Reflection.getFieldsFromMap ( fields, ( Map<String, Object> ) item );
        }
        return fields;

    }


    public static  char[] toCharArray( String str ) {

        if (_useUnsafe) {
            return (char[]) context().stringValueField.getObject ( str );
        } else {
            return str.toCharArray ();
        }
    }


    public static  char[] toCharArray( byte [] bytes ) {
        if (_useUnsafe) {
            return (char[]) context().stringValueField.getObject ( new String(bytes, StandardCharsets.UTF_8) );
        } else {
            return new String(bytes, StandardCharsets.UTF_8).toCharArray ();
        }
    }

    /**
     * Get fields from map.
     *
     * @param fields
     * @param map
     * @return
     */
    private static Map<String, FieldAccess> getFieldsFromMap( Map<String, FieldAccess> fields, Map<String, Object> map ) {

        for ( Map.Entry<String, Object> entry : map.entrySet ( ) ) {
            fields.put ( entry.getKey ( ), new MapField ( entry.getKey ( ) ) );
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
    public static Object getPropertyValue( final Object root, final String... properties ) {
        Objects.requireNonNull ( root );
        Objects.requireNonNull ( properties );


        Object object = root;

        for ( String property : properties ) {

            Map<String, FieldAccess> fields = Reflection.getFieldsFromObject ( object );

            FieldAccess field = fields.get ( property );

            if ( isDigits ( property ) ) {
                /* We can index numbers and names. */
                object = idx ( object, Integer.parseInt ( property ) );

            } else {

                if ( field == null ) {
                    die ( sputs (
                            "We were unable to access property=", property,
                            "\nThe properties passed were=", properties,
                            "\nThe root object is =", root.getClass ( ).getName ( ),
                            "\nThe current object is =", object.getClass ( ).getName ( )
                    )
                    );
                }

                object = field.getObject ( object );
            }
        }
        return object;
    }

    /**
     * Get property value, loads nested properties
     *
     * @param root
     * @param properties
     * @return
     */
    public static void setPropertyValue( final Object root, final Object newValue, final String... properties ) {
        Objects.requireNonNull ( root );
        Objects.requireNonNull ( properties );


        Object object = root;
        Object parent = root;

        int index = 0;
        for ( String property : properties ) {
            Map<String, FieldAccess> fields = Reflection.getPropertyFieldAccessMap ( object.getClass ( ) );

            FieldAccess field = fields.get ( property );


            if ( isDigits ( property ) ) {
                /* We can index numbers and names. */
                object = idx ( object, Integer.parseInt ( property ) );

            } else {

                if ( field == null ) {
                    die ( sputs (
                            "We were unable to access property=", property,
                            "\nThe properties passed were=", properties,
                            "\nThe root object is =", root.getClass ( ).getName ( ),
                            "\nThe current object is =", object.getClass ( ).getName ( )
                    )
                    );
                }


                if ( index == properties.length - 1 ) {
                    field.setValue ( object, newValue );
                } else {
                    object = field.getObject ( object );
                }
            }

            index++;
        }

    }

    /**
     * Get property value, loads nested properties
     *
     * @param root
     * @param property
     * @return
     */
    public static Class<?> getPropertyType( final Object root, final String property ) {
        Objects.requireNonNull ( root );
        Objects.requireNonNull ( property );

        Map<String, FieldAccess> fields = Reflection.getPropertyFieldAccessMap ( root.getClass ( ) );

        FieldAccess field = fields.get ( property );
        return field.getType ( );
    }


    @SuppressWarnings( "unchecked" )
    public static <T> T idxGeneric( Class<T> t, Object object, final String path ) {

        Objects.requireNonNull ( object );
        Objects.requireNonNull ( path );

        String[] properties = StringScanner.splitByDelimiters ( path, ".[]" );

        return ( T ) getPropByPath ( object, properties );


    }

    public static <T> List<T> idxList( Class<T> cls, Object items, String... path ) {
        return ( List<T> ) getPropByPath ( items, path );
    }


    /**
     * Get property value
     *
     * @param object
     * @param path   in dotted notation
     * @return
     */
    public static Object idx( Object object, String path ) {

        Objects.requireNonNull ( object );
        Objects.requireNonNull ( path );

        String[] properties = StringScanner.splitByDelimiters ( path, ".[]" );

        return getPropertyValue ( object, properties );
    }

    /**
     * @param object
     * @param path
     * @return
     */
    public static Object idxRelax( Object object, final String path ) {
        Objects.requireNonNull ( object );
        Objects.requireNonNull ( path );

        String[] properties = StringScanner.splitByDelimiters ( path, ".[]" );

        return getPropByPath ( object, properties );
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
            return getCollecitonProp ( o, path[index], index, path );
        }
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
            String propName = path[index];
            if ( o == null ) {
                return null;
            } else if ( isArray ( o ) || o instanceof Collection ) {
                o = getCollecitonProp ( o, propName, index, path );
                break;
            } else {
                o = getProp ( o, propName );
            }
        }
        return Conversions.unifyList ( o );
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

        if ( isDigits ( property ) ) {
                /* We can index numbers and names. */
            object = idx ( object, Integer.parseInt ( property ) );

        }

        Class<?> cls = object.getClass ( );

        /** Tries the getters first. */
        Map<String, FieldAccess> fields = getPropertyFieldAccessors ( cls );

        if ( !fields.containsKey ( property ) ) {
            fields = getAllAccessorFields ( cls );
        }

        if ( !fields.containsKey ( property ) ) {
            return null;
        } else {
            return fields.get ( property ).getValue ( object );
        }

    }


    /**
     * Get an int property.
     */
    public static int getPropertyInt( final Object root, final String... properties ) {

        Objects.requireNonNull ( root );
        Objects.requireNonNull ( properties );


        Object object = baseForGetProperty ( root, properties );

        Map<String, FieldAccess> fields = Reflection.getPropertyFieldAccessMap ( object.getClass ( ) );
        final String lastProperty = properties[properties.length - 1];
        FieldAccess field = fields.get ( lastProperty );

        if ( field.getType ( ) == Typ.intgr ) {
            return field.getInt ( object );
        } else {
            return Conversions.toInt ( field.getValue ( object ) );
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
            fields = Reflection.getPropertyFieldAccessMap ( object.getClass ( ) );

            String property = properties[index];
            FieldAccess field = fields.get ( property );

            if ( isDigits ( property ) ) {
                /* We can index numbers and names. */
                object = idx ( object, Integer.parseInt ( property ) );

            } else {

                if ( field == null ) {
                    die ( sputs (
                            "We were unable to access property=", property,
                            "\nThe properties passed were=", properties,
                            "\nThe root object is =", root.getClass ( ).getName ( ),
                            "\nThe current object is =", object.getClass ( ).getName ( )
                    )
                    );
                }

                object = field.getObject ( object );
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

        Objects.requireNonNull ( object );
        Objects.requireNonNull ( path );

        String[] properties = StringScanner.splitByDelimiters ( path, ".[]" );

        return getPropertyInt ( object, properties );
    }


    /**
     * @param root
     * @param properties
     * @return
     */
    public static byte getPropertyByte( final Object root, final String... properties ) {
        Object object = baseForGetProperty ( root, properties );

        Map<String, FieldAccess> fields = getPropertyFieldAccessMap ( object.getClass ( ) );
        final String lastProperty = properties[properties.length - 1];
        FieldAccess field = fields.get ( lastProperty );

        if ( field.getType ( ) == Typ.bt ) {
            return field.getByte ( object );
        } else {
            return Conversions.toByte ( field.getValue ( object ) );
        }
    }

    /**
     * @param object
     * @param path
     * @return
     */
    public static byte idxByte( Object object, String path ) {

        Objects.requireNonNull ( object );
        Objects.requireNonNull ( path );

        String[] properties = StringScanner.splitByDelimiters ( path, ".[]" );

        return getPropertyByte ( object, properties );
    }

    /**
     * @param root
     * @param properties
     * @return
     */
    public static float getPropertyFloat( final Object root, final String... properties ) {
        Object object = baseForGetProperty ( root, properties );

        Map<String, FieldAccess> fields = getPropertyFieldAccessMap ( object.getClass ( ) );
        final String lastProperty = properties[properties.length - 1];
        FieldAccess field = fields.get ( lastProperty );

        if ( field.getType ( ) == Typ.flt ) {
            return field.getFloat ( object );
        } else {
            return Conversions.toFloat ( field.getValue ( object ) );
        }
    }


    /**
     * @param object
     * @param path
     * @return
     */
    public static float idxFloat( Object object, String path ) {

        Objects.requireNonNull ( object );
        Objects.requireNonNull ( path );

        String[] properties = StringScanner.splitByDelimiters ( path, ".[]" );

        return getPropertyFloat ( object, properties );
    }


    /**
     * @param root
     * @param properties
     * @return
     */
    public static short getPropertyShort( final Object root,
                                          final String... properties ) {


        Object object = baseForGetProperty ( root, properties );

        Map<String, FieldAccess> fields = getPropertyFieldAccessMap ( object.getClass ( ) );
        final String lastProperty = properties[properties.length - 1];
        FieldAccess field = fields.get ( lastProperty );


        if ( field.getType ( ) == Typ.shrt ) {
            return field.getShort ( object );
        } else {
            return Conversions.toShort ( field.getValue ( object ) );
        }
    }


    /**
     * @param object
     * @param path
     * @return
     */
    public static short idxShort( Object object, String path ) {

        Objects.requireNonNull ( object );
        Objects.requireNonNull ( path );

        String[] properties = StringScanner.splitByDelimiters ( path, ".[]" );

        return getPropertyShort ( object, properties );
    }

    /**
     * @param root
     * @param properties
     * @return
     */
    public static char getPropertyChar( final Object root,
                                        final String... properties ) {

        Object object = baseForGetProperty ( root, properties );

        Map<String, FieldAccess> fields = getPropertyFieldAccessMap ( object.getClass ( ) );
        final String lastProperty = properties[properties.length - 1];
        FieldAccess field = fields.get ( lastProperty );

        if ( field.getType ( ) == Typ.chr ) {
            return field.getChar ( object );
        } else {
            return Conversions.toChar ( field.getValue ( object ) );
        }
    }


    /**
     * @param object
     * @param path
     * @return
     */
    public static char idxChar( Object object, String path ) {

        Objects.requireNonNull ( object );
        Objects.requireNonNull ( path );

        String[] properties = StringScanner.splitByDelimiters ( path, ".[]" );

        return getPropertyChar ( object, properties );
    }


    /**
     * @param root
     * @param properties
     * @return
     */
    public static double getPropertyDouble( final Object root,
                                            final String... properties ) {


        Object object = baseForGetProperty ( root, properties );

        Map<String, FieldAccess> fields = getPropertyFieldAccessMap ( object.getClass ( ) );
        final String lastProperty = properties[properties.length - 1];
        FieldAccess field = fields.get ( lastProperty );

        if ( field.getType ( ) == Typ.dbl ) {
            return field.getDouble ( object );
        } else {
            return Conversions.toDouble ( field.getValue ( object ) );
        }
    }


    /**
     * @param object
     * @param path
     * @return
     */
    public static double idxDouble( Object object, String path ) {

        Objects.requireNonNull ( object );
        Objects.requireNonNull ( path );

        String[] properties = StringScanner.splitByDelimiters ( path, ".[]" );

        return getPropertyDouble ( object, properties );
    }


    /**
     * @param root
     * @param properties
     * @return
     */
    public static long getPropertyLong( final Object root,
                                        final String... properties ) {


        Object object = baseForGetProperty ( root, properties );

        Map<String, FieldAccess> fields = getPropertyFieldAccessMap ( object.getClass ( ) );
        final String lastProperty = properties[properties.length - 1];
        FieldAccess field = fields.get ( lastProperty );

        if ( field.getType ( ) == Typ.lng ) {
            return field.getLong ( object );
        } else {
            return Conversions.toLong ( field.getValue ( object ) );
        }
    }


    /**
     * @param object
     * @param path
     * @return
     */
    public static long idxLong( Object object, String path ) {

        Objects.requireNonNull ( object );
        Objects.requireNonNull ( path );

        String[] properties = StringScanner.splitByDelimiters ( path, ".[]" );

        return getPropertyLong ( object, properties );
    }


    /**
     * @param root
     * @param properties
     * @return
     */
    public static boolean getPropertyBoolean( final Object root,
                                              final String... properties ) {


        Object object = baseForGetProperty ( root, properties );

        Map<String, FieldAccess> fields = getPropertyFieldAccessMap ( object.getClass ( ) );
        final String lastProperty = properties[properties.length - 1];
        FieldAccess field = fields.get ( lastProperty );

        if ( field.getType ( ) == Typ.bln ) {
            return field.getBoolean ( object );
        } else {
            return Conversions.toBoolean ( field.getValue ( object ) );
        }
    }


    public static boolean idxBoolean( Object object, String path ) {

        Objects.requireNonNull ( object );
        Objects.requireNonNull ( path );

        String[] properties = StringScanner.splitByDelimiters ( path, ".[]" );

        return getPropertyBoolean ( object, properties );
    }


    public static boolean hasField( Class<?> aClass, String name ) {
        Map<String, FieldAccess> fields = getAllAccessorFields ( aClass );
        return fields.containsKey ( name );
    }

    @SuppressWarnings( "serial" )
    public static class ReflectionException extends RuntimeException {

        public ReflectionException( ) {
            super ( );
        }

        public ReflectionException( String message, Throwable cause ) {
            super ( message, cause );
        }

        public ReflectionException( String message ) {
            super ( message );
        }

        public ReflectionException( Throwable cause ) {
            super ( cause );
        }
    }

    public static boolean isArray( Object obj ) {
        if ( obj == null ) return false;
        return obj.getClass ( ).isArray ( );
    }

    public static int len( Object obj ) {
        if ( isArray ( obj ) ) {
            return arrayLength ( obj );
        } else if ( obj instanceof CharSequence ) {
            return ( ( CharSequence ) obj ).length ( );
        } else if ( obj instanceof Collection ) {
            return ( ( Collection<?> ) obj ).size ( );
        } else if ( obj instanceof Map ) {
            return ( ( Map<?, ?> ) obj ).size ( );
        } else {
            die ( "Not an array like object" );
            return 0; //will never get here.
        }
    }


    public static int arrayLength( Object obj ) {
        return Array.getLength ( obj );
    }


    private static void handle( Exception ex ) {
        throw new ReflectionException ( ex );
    }


    public static Object idx( Object object, int index ) {
        if ( isArray ( object ) ) {
            object = Array.get ( object, index );
        } else if ( object instanceof List ) {
            object = Lists.idx ( ( List ) object, index );
        }
        return object;
    }

    public static void idx( Object object, int index, Object value ) {
        try {
            if ( isArray ( object ) ) {
                Array.set ( object, index, value );
            } else if ( object instanceof List ) {
                Lists.idx ( ( List ) object, index, value );
            }
        } catch ( Exception notExpected ) {
            String msg = lines ( "An unexpected error has occurred",
                    "This is likely a programming error!",
                    String.format ( "Object is %s, index is %s, and set is %s", object, index, value ),
                    String.format ( "The object is an array? %s", object == null ? "null" : object.getClass ( ).isArray ( ) ),
                    String.format ( "The object is of type %s", object == null ? "null" : object.getClass ( ).getName ( ) ),
                    String.format ( "The set is of type %s", value == null ? "null" : value.getClass ( ).getName ( ) ),

                    ""

            );
            Exceptions.handle ( msg, notExpected );
        }
    }


    private static Object getFieldValues( Object object, final String key ) {
        if ( object == null ) {
            return null;
        }
        if ( isArray ( object ) || object instanceof Collection ) {
            Iterator iter = Conversions.iterator ( object );
            List list = new ArrayList ( len ( object ) );
            while ( iter.hasNext ( ) ) {
                list.add ( getFieldValues ( iter.next ( ), key ) );
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

        Class<?> cls = object.getClass ( );

        Map<String, FieldAccess> fields = getPropertyFieldAccessMap ( cls );

        if ( !fields.containsKey ( key ) ) {
            return null;
        } else {
            return fields.get ( key ).getValue ( object );
        }
    }


    public static Object newInstance( String className ) {
        Class<?> clazz = null;

        try {
            clazz = Class.forName ( className );


            return newInstance ( clazz );


        } catch ( Exception ex ) {
            log.info ( String.format ( "Unable to create this class %s", className ) );
            return null;
        }
    }

    public static <T> T newInstance( Class<T> clazz ) {
        T newInstance = null;

        try {
            if (_useUnsafe) {
                newInstance = (T) getUnsafe ().allocateInstance(clazz);
            }else {
                newInstance = clazz.newInstance ( );
            }
        } catch ( Exception ex ) {
            handle ( ex );
        }

        return newInstance;

    }


    @SuppressWarnings( "unchecked" )
    public static <T> T fromMap( Map<String, Object> map, Class<T> clazz ) {

        return fromMap ( map, newInstance ( clazz ) );
    }

    @SuppressWarnings( "unchecked" )
    public static Object fromMap( Map<String, Object> map  ) {
        String className = ( String ) map.get ( "class" );
        Object newInstance = newInstance ( className );
        return fromMap (map, newInstance);
    }

    @SuppressWarnings( "unchecked" )
    public static <T> T fromMap( Map<String, Object> map, T newInstance  ) {


        Objects.requireNonNull ( newInstance );


        Map<String, FieldAccess> fields = getAllAccessorFields ( newInstance.getClass () );
        Set<Map.Entry<String, Object>> entrySet = map.entrySet ();

        /* Iterate through the fields. */
        //for ( FieldAccess field : fields ) {
        for (Map.Entry <String, Object> entry :  entrySet) {


            FieldAccess field  = fields.get ( entry.getKey () );
            Object value = entry.getValue ();

            if ( field == null ) {
                continue;
            }

            if ( value instanceof Value ) {
                if (( ( Value ) value ).isContainer ())  {
                    value =  ((Value)value).toValue ();
                } else {
                    field.setFromValue ( newInstance, (Value) value );
                    continue;
                }
            }


            if (Typ.isBasicType ( value )) {

                 field.setValue ( newInstance, value );
            } else if (value instanceof  Value) {
                field.setValue ( newInstance, value );
            }
            /* See if it is a map<string, object>, and if it is then process it. */
            //&& Typ.getKeyType ( ( Map<?, ?> ) value ) == Typ.string
            else if ( value instanceof Map  ) {
                value = fromMap ( ( Map<String, Object> ) value, field.getType () );
                field.setObject ( newInstance, value );
            } else if ( value instanceof Collection ) {
                /*It is a collection so process it that way. */
                processCollectionFromMap ( newInstance, field, ( Collection ) value );
            } else if ( value instanceof Map[] ) {
                /* It is an array of maps so, we need to process it as such. */
                processArrayOfMaps ( newInstance, field, value );
            } else {
                field.setValue ( newInstance, value );
            }

        }

        return newInstance;
    }

    @SuppressWarnings( "unchecked" )
    public static <T> T fromValueMap( Map<String, Value> map, Class<T> clazz ) {
        return fromValueMap ( map, newInstance ( clazz ) );
    }

    @SuppressWarnings( "unchecked" )
    public static <T> T fromValueMap( Map<String, Value> map, T newInstance  ) {



        Map<String, FieldAccess> fields = getAllAccessorFields ( newInstance.getClass (), true );
        Map.Entry<String, Value>[] entries  = map.entrySet ().toArray (null);
        int size = map.size ();


        for (int index = 0; index < size; index++) {
            Map.Entry<String, Value> entry = entries[index];

            String key = entry.getKey ();
            FieldAccess field  = fields.get ( key );
            Value value = entry.getValue ();


            if ( field == null ) {
                continue;
            }

           if (value .isContainer ())  {
               Object objValue;

               objValue =  value.toValue ();
               if ( objValue instanceof Map  ) {
                   objValue = fromValueMap ( ( Map<String, Value> ) objValue, field.getType () );
                   field.setObject ( newInstance, objValue );
               } else if ( objValue instanceof Collection ) {
                   handleCollectionOfValues ( newInstance, field,
                           ( Collection<Value> ) objValue );
               }

           } else {
                    field.setFromValue ( newInstance,  value );
           }

        }

        return newInstance;
    }

    private static void processCollectionFromMap( final Object newInstance,
                                                  final FieldAccess field,
                                                  final Collection<?> collection ) {

        final Class<?> componentType = getComponentType ( collection );
        /** See if we have a collection of maps because if we do, then we have some
         * recursive processing to do.
         */
        if ( Typ.isMap ( componentType ) ) {
            handleCollectionOfMaps ( newInstance, field,
                    ( Collection<Map<String, Object>> ) collection );
        } else if (Typ.isValue ( componentType )) {
            handleCollectionOfValues ( newInstance, field,
                    ( Collection<Value> ) collection );

        }
        else {

            /* It might be a collection of regular types. */

            /*If it is a compatiable type just inject it. */
            if ( field.getType ( ).isInterface ( ) &&
                    Typ.implementsInterface ( collection.getClass ( ), field.getType ( ) ) ) {

                field.setValue ( newInstance, collection );

            } else {
                /* The type was not compatible so create a new collection that is. */
                Collection<Object> newCollection =
                        createCollection ( field.getType ( ), collection.size ( ) );

                newCollection.addAll ( collection );
                field.setValue ( newInstance, newCollection );

            }

        }

    }


    private static void processArrayOfMaps( Object newInstance, FieldAccess field, Object value ) {
        Map<String, Object>[] maps = ( Map<String, Object>[] ) value;
        List<Map<String, Object>> list = Lists.list ( maps );
        handleCollectionOfMaps ( newInstance, field,
                list );

    }

    @SuppressWarnings( "unchecked" )
    private static void handleCollectionOfMaps( Object newInstance,
                                                FieldAccess field, Collection<Map<String, Object>> collectionOfMaps ) {

        Collection<Object> newCollection = createCollection ( field.getType ( ), collectionOfMaps.size ( ) );


        Class<?> componentClass = field.getComponentClass ( );

        if ( componentClass != null ) {


            for ( Map<String, Object> mapComponent : collectionOfMaps ) {

                newCollection.add ( fromMap ( mapComponent, componentClass ) );

            }
            field.setObject ( newInstance, newCollection );

        }

    }


    @SuppressWarnings( "unchecked" )
    private static void handleCollectionOfValues( Object newInstance,
                                                FieldAccess field, Collection<Value> collectionOfValues ) {

        Collection<Object> newCollection = createCollection ( field.getType ( ), collectionOfValues.size ( ) );


        Class<?> componentClass = field.getComponentClass ( );

        if ( componentClass != null ) {


            for ( Value value : collectionOfValues ) {

                if (value.isContainer ()) {
                    Object oValue = value.toValue ();
                    if (oValue instanceof  Map) {
                        newCollection.add ( fromValueMap ( (Map) oValue, componentClass ) );
                    }
                } else {
                    newCollection.add ( Conversions.coerce( componentClass, value.toValue() ));
                }


            }
            field.setObject ( newInstance, newCollection );

        }

    }

    public static Collection<Object> createCollection( Class<?> type, int size ) {

            if ( type == List.class ) {
                return new ArrayList<> ( size );
            } else if ( type == SortedSet.class ) {
                return new TreeSet<> ( );
            } else if ( type == Set.class ) {
                return new LinkedHashSet<> ( size );
            } else if ( Typ.isList ( type ) ) {
                return new ArrayList<> ( );
            } else if ( Typ.isSortedSet ( type ) ) {
                return new TreeSet<> ( );
            } else if ( Typ.isSet ( type ) ) {
                return new LinkedHashSet<> ( size );
            } else {
                return new ArrayList ( size );
            }

    }


    public static Map<String, Object> toMap( final Object object ) {

        if ( object == null ) {
            return null;
        }

        Map<String, Object> map = new LinkedHashMap<> ( );


        class FieldToEntryConverter implements
                Conversions.Converter<Maps.Entry<String, Object>, FieldAccess> {
            @Override
            public Maps.Entry<String, Object> convert( FieldAccess from ) {
                if ( from.isReadOnly ( ) ) {
                    return null;
                }
                Maps.Entry<String, Object> entry = new Maps.EntryImpl<> ( from.getName ( ),
                        from.getValue ( object ) );
                return entry;
            }
        }

        final Map<String, FieldAccess> fieldMap = getAllAccessorFields ( object.getClass ( ) );
        List<FieldAccess> fields = new ArrayList ( fieldMap.values ( ) );


        Collections.reverse ( fields ); // make super classes fields first that
        // their update get overriden by
        // subclass fields with the same name

        List<Maps.Entry<String, Object>> entries = Conversions.mapFilterNulls (
                new FieldToEntryConverter ( ), new ArrayList ( fields ) );

        map.put ( "class", object.getClass ( ).getName ( ) );

        for ( Maps.Entry<String, Object> entry : entries ) {
            Object value = entry.value ( );
            if ( value == null ) {
                continue;
            }
            if ( Typ.isBasicType ( value ) ) {
                map.put ( entry.key ( ), entry.value ( ) );
            } else if ( isArray ( value )
                    && Typ.isBasicType ( value.getClass ( ).getComponentType ( ) ) ) {
                map.put ( entry.key ( ), entry.value ( ) );
            } else if ( isArray ( value ) ) {
                int length = arrayLength ( value );
                List<Map<String, Object>> list = new ArrayList<> ( length );
                for ( int index = 0; index < length; index++ ) {
                    Object item = idx ( value, index );
                    list.add ( toMap ( item ) );
                }
                map.put ( entry.key ( ), list );
            } else if ( value instanceof Collection ) {
                Collection<?> collection = ( Collection<?> ) value;
                Class<?> componentType = getComponentType ( collection, fieldMap.get ( entry.key ( ) ) );
                if ( Typ.isBasicType ( componentType ) ) {
                    map.put ( entry.key ( ), value );
                } else {
                    List<Map<String, Object>> list = new ArrayList<> (
                            collection.size ( ) );
                    for ( Object item : collection ) {
                        if ( item != null ) {
                            list.add ( toMap ( item ) );
                        } else {

                        }
                    }
                    map.put ( entry.key ( ), list );
                }
            } else if ( value instanceof Map ) {

            } else {
                map.put ( entry.key ( ), toMap ( value ) );
            }
        }
        return map;
    }

    public static Class<?> getComponentType( Collection<?> collection, FieldAccess fieldAccess ) {
        Class<?> clz = fieldAccess.getComponentClass ( );
        if ( clz == null ) {
            clz = getComponentType ( collection );
        }
        return clz;

    }

    public static Class<?> getComponentType( Collection<?> value ) {
        if ( value.size ( ) > 0 ) {
            Object next = value.iterator ( ).next ( );
            return next.getClass ( );
        } else {
            return Typ.object;
        }
    }

    private static class FieldConverter implements Conversions.Converter<FieldAccess, Field> {

        boolean thisUseUnsafe;

        FieldConverter( boolean useUnsafe ) {
            this.thisUseUnsafe = useUnsafe;
        }

        @Override
        public FieldAccess convert( Field from ) {
            if ( useUnsafe && thisUseUnsafe ) {
                return UnsafeField.createUnsafeField ( from );
            } else {
                return new ReflectField ( from );
            }
        }
    }

    public static Map<String, FieldAccess> getAllAccessorFields(
            Class<? extends Object> theClass ) {
        return getAllAccessorFields ( theClass, true );
    }

    public static Map<String, FieldAccess> getAllAccessorFields(
            Class<? extends Object> theClass, boolean useUnsafe ) {
        Map<String, FieldAccess> map = getAccesorFieldFromCache ( theClass, useUnsafe );
        if ( map == null ) {
            List<FieldAccess> list = Conversions.map ( new FieldConverter ( useUnsafe ), getAllFields ( theClass ) );
            map = new LinkedHashMap<> ( list.size ()  );
            for (FieldAccess fieldAccess : list) {
                map.put ( fieldAccess.getName (), fieldAccess );
            }

            setAccessorFieldInCache ( theClass, useUnsafe, map );

        }
        return map;
    }


    public static <V> Map<String, V> collectionToMap( String propertyKey, Collection<V> values ) {
        LinkedHashMap<String, V> map = new LinkedHashMap<String, V> ( values.size ( ) );
        Iterator<V> iterator = values.iterator ( );
        for ( V v : values ) {
            String key = Reflection.idxGeneric ( Typ.string, v, propertyKey );
            map.put ( key, v );
        }
        return map;
    }

    public static List<Field> getAllFields( Class<? extends Object> theClass ) {
        List<Field> list = getFields ( theClass );
        while ( theClass != Typ.object ) {

            theClass = theClass.getSuperclass ( );
            getFields ( theClass, list );
        }
        return list;
    }


    public static Map<String, FieldAccess> getPropertyFieldAccessors(
            Class<? extends Object> theClass ) {


        Map<String, FieldAccess> fields = getPropertyAccessorFieldsFromCache ( theClass );
        if ( fields == null ) {
            Map<String, Pair<Method>> methods = getPropertySetterGetterMethods ( theClass );

            fields = new LinkedHashMap<> ( );

            for ( Map.Entry<String, Pair<Method>> entry :
                    methods.entrySet ( ) ) {

                final Pair<Method> methodPair = entry.getValue ( );
                final String key = entry.getKey ( );

                PropertyField pf = new PropertyField ( key, methodPair.getFirst ( ), methodPair.getSecond ( ) );

                fields.put ( key, pf );

            }

            setPropertyAccessorFieldsInCache ( theClass, fields );
        }


        return fields;
    }

    public static Map<String, Pair<Method>> getPropertySetterGetterMethods(
            Class<? extends Object> theClass ) {

        Method[] methods = theClass.getMethods ( );

        Map<String, Pair<Method>> methodMap = new LinkedHashMap<> ( methods.length );
        List<Method> getterMethodList = new ArrayList<> ( methods.length );

        for ( int index = 0; index < methods.length; index++ ) {
            Method method = methods[index];
            String name = method.getName ( );

            if ( method.getParameterTypes ( ).length == 1
                    && method.getReturnType ( ) == void.class
                    && name.startsWith ( "set" ) ) {
                Pair<Method> pair = new Pair<Method> ( );
                pair.setFirst ( method );
                String propertyName = slc ( name, 3 );

                propertyName = lower ( slc ( propertyName, 0, 1 ) ) + slc ( propertyName, 1 );
                methodMap.put ( propertyName, pair );
            }

            if ( method.getParameterTypes ( ).length > 0
                    || method.getReturnType ( ) == void.class
                    || !( name.startsWith ( "get" ) || name.startsWith ( "is" ) )
                    || name.equals ( "getClass" ) ) {
                continue;
            }
            getterMethodList.add ( method );
        }

        for ( Method method : getterMethodList ) {
            String name = method.getName ( );
            String propertyName = null;
            if ( name.startsWith ( "is" ) ) {
                propertyName = name.substring ( 2 );
            } else if ( name.startsWith ( "get" ) ) {
                propertyName = name.substring ( 3 );
            }

            propertyName = lower ( propertyName.substring (  0, 1 ))  +  propertyName.substring (  1 ) ;

            Pair<Method> pair = methodMap.get ( propertyName );
            if ( pair == null ) {
                pair = new Pair<> ( );
                methodMap.put ( propertyName, pair );
            }
            pair.setSecond ( method );

        }
        return methodMap;
    }

    public static void getFields( Class<? extends Object> theClass,
                                  List<Field> list ) {
        List<Field> more = getFields ( theClass );
        list.addAll ( more );
    }

    public static List<Field> getFields( Class<? extends Object> theClass ) {
        List<Field> list = Lists.list ( theClass.getDeclaredFields ( ) );
        for ( Field field : list ) {
            field.setAccessible ( true );
        }
        return list;
    }

    public static <T> T copy( T item ) {
        if ( item instanceof Cloneable ) {
            try {
                Method method = item.getClass ( ).getMethod ( "clone", ( Class[] ) null );
                return ( T ) method.invoke ( item, ( Object[] ) null );
            } catch ( NoSuchMethodException | InvocationTargetException | IllegalAccessException ex ) {
                return fieldByFieldCopy ( item );
            }
        } else {
            return fieldByFieldCopy ( item );
        }
    }


    private static <T> T fieldByFieldCopy( T item ) {
        Map<String, FieldAccess> fields = getAllAccessorFields ( item.getClass ( ) );
        T clone = null;
        try {
            clone = ( T ) item.getClass ( ).newInstance ( );
        } catch ( Exception e ) {
            handle ( e );
        }
        for ( FieldAccess field : fields.values ( ) ) {
            if ( field.isStatic ( ) || field.isFinal ( ) || field.isReadOnly ( ) ) {
                continue;
            }
            field.setValue ( clone, field.getValue ( item ) );
        }
        return clone;
    }


    public static Iterator iterator( final Object o ) {
        if ( o instanceof Collection ) {
            return ( ( Collection ) o ).iterator ( );
        } else if ( isArray ( o ) ) {
            return new Iterator ( ) {
                int index = 0;
                int length = len ( o );

                @Override
                public boolean hasNext( ) {
                    return index < length;
                }

                @Override
                public Object next( ) {
                    Object value = Reflection.idx ( o, index );
                    index++;
                    return value;
                }

                @Override
                public void remove( ) {
                }
            };
        }
        return null;
    }


    public static String joinBy( char delim, Object... args ) {
        CharBuf builder = CharBuf.create ( 256 );
        int index = 0;
        for ( Object arg : args ) {
            builder.add ( arg.toString ( ) );
            if ( !( index == args.length - 1 ) ) {
                builder.add ( delim );
            }
            index++;
        }
        return builder.toString ( );
    }


    public static List<Map<String, Object>> toListOfMaps( Collection<?> collection ) {
        List<Map<String, Object>> list = new ArrayList<> ( );
        for ( Object o : collection ) {
            list.add ( toMap ( o ) );
        }
        return list;
    }


    public static void copyProperties( Object object, Map<String, Object> properties ) {

        Set<Map.Entry<String, Object>> props = properties.entrySet ( );
        for ( Map.Entry<String, Object> entry : props ) {
            setPropertyValue ( object, entry.getValue ( ), entry.getKey ( ) );
        }
    }
}
