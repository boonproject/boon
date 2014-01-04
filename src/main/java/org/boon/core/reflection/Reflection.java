package org.boon.core.reflection;

import org.boon.*;
import org.boon.core.*;
import org.boon.core.reflection.fields.*;
import org.boon.core.value.ValueMapImpl;
import org.boon.primitive.CharBuf;
import sun.misc.Unsafe;

import java.lang.ref.WeakReference;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import static org.boon.Exceptions.die;
import static org.boon.Str.*;


public class Reflection {

    private static final Logger log = Logger.getLogger( Reflection.class.getName() );
    private final static Set<String> fieldSortNames = Sets.safeSet( "name", "orderBy", "title", "key" );
    private final static Set<String> fieldSortNamesSuffixes = Sets.safeSet( "Name", "Title", "Key" );

    private static boolean _useUnsafe;

    static {
        try {
            Class.forName( "sun.misc.Unsafe" );
            _useUnsafe = true;
        } catch ( ClassNotFoundException e ) {
            e.printStackTrace();
            _useUnsafe = false;
        }

        _useUnsafe = _useUnsafe && !Boolean.getBoolean( "org.boon.noUnsafe" );
    }

    private static final boolean useUnsafe = _useUnsafe;



    private final static Context _context;
    private static WeakReference<Context> weakContext = new WeakReference<>( null );


    static {

        boolean noStatics = Boolean.getBoolean( "org.boon.noStatics" );
        if ( noStatics || Sys.inContainer() ) {

            _context = null;
            weakContext = new WeakReference<>( new Context() );

        } else {
            ;
            _context = new Context();
        }
    }




    public static Unsafe getUnsafe() {
        if ( context().control == null ) {
            try {
                Field f = Unsafe.class.getDeclaredField( "theUnsafe" );
                f.setAccessible( true );
                context().control = ( Unsafe ) f.get( null );
                return context().control;
            } catch ( Exception e ) {
                return null;
            }
        } else {
            return context().control;
        }
    }



    private static void setSortableField( Class<?> clazz, String fieldName ) {
        context()._sortableFields.put( clazz.getName(), fieldName );
    }

    private static String getSortableField( Class<?> clazz ) {
        return context()._sortableFields.get( clazz.getName() );
    }

    public static Object contextToHold() {
        return context();
    }

    /* Manages weak references. */
    private static Context context() {

        if ( _context != null ) {
            return _context;
        } else {
            Context context = weakContext.get();
            if ( context == null ) {
                context = new Context();
                weakContext = new WeakReference<>( context );
            }
            return context;
        }
    }

    public static Collection<Object> createCollection( Class<?> type, int size ) {

        if ( type == List.class ) {
            return new ArrayList<>( size );
        } else if ( type == SortedSet.class ) {
            return new TreeSet<>();
        } else if ( type == Set.class ) {
            return new LinkedHashSet<>( size );
        } else if ( Typ.isList( type ) ) {
            return new ArrayList<>();
        } else if ( Typ.isSortedSet( type ) ) {
            return new TreeSet<>();
        } else if ( Typ.isSet( type ) ) {
            return new LinkedHashSet<>( size );
        } else {
            return new ArrayList( size );
        }

    }


    private static class Context {

        private Unsafe control;
        private Map<String, String> _sortableFields = new ConcurrentHashMap<>();

        private Map<Class<?>, Map<String, FieldAccess>> _allAccessorReflectionFieldsCache = new ConcurrentHashMap<>( 200 );
        private Map<Class<?>, Map<String, FieldAccess>> _allAccessorPropertyFieldsCache = new ConcurrentHashMap<>( 200 );
        private Map<Class<?>, Map<String, FieldAccess>> _allAccessorUnsafeFieldsCache = new ConcurrentHashMap<>( 200 );


    }


    static {
        try {
            if ( _useUnsafe ) {
                Field field = String.class.getDeclaredField( "value" );
            }
        } catch ( Exception ex ) {
            Exceptions.handle( ex );
        }
    }


    private static void setAccessorFieldInCache( Class<? extends Object> theClass, boolean useUnsafe, Map<String, FieldAccess> map ) {
        if ( useUnsafe ) {
            context()._allAccessorUnsafeFieldsCache.put( theClass, map );
        } else {
            context()._allAccessorReflectionFieldsCache.put( theClass, map );

        }
    }

    private static void setPropertyAccessorFieldsInCache( Class<? extends Object> theClass, Map<String, FieldAccess> map ) {
        context()._allAccessorPropertyFieldsCache.put( theClass, map );
    }


    private static Map<String, FieldAccess> getPropertyAccessorFieldsFromCache( Class<? extends Object> theClass ) {
        return context()._allAccessorPropertyFieldsCache.get( theClass );
    }

    private static Map<String, FieldAccess> getAccesorFieldFromCache( Class<? extends Object> theClass, boolean useUnsafe ) {

        if ( useUnsafe ) {
            return context()._allAccessorUnsafeFieldsCache.get( theClass );
        } else {
            return context()._allAccessorReflectionFieldsCache.get( theClass );

        }
    }




    /**
     * Gets a listStream of fields merges with properties if field is not found.
     *
     * @param clazz get the properties or fields
     * @return
     */
    public static Map<String, FieldAccess> getPropertyFieldAccessMapFieldFirst( Class<?> clazz ) {
        /* Fallback map. */
        Map<String, FieldAccess> fieldsFallbacks = null;

        /* Primary merge into this one. */
        Map<String, FieldAccess> fieldsPrimary = null;


        /* Try to find the fields first if this is set. */
        fieldsPrimary = Reflection.getAllAccessorFields( clazz, true );
        fieldsFallbacks = Reflection.getPropertyFieldAccessors( clazz );
        combineFieldMaps( fieldsFallbacks, fieldsPrimary );


        return fieldsPrimary;
    }

    private static void combineFieldMaps( Map<String, FieldAccess> fieldsFallbacks, Map<String, FieldAccess> fieldsPrimary ) {
    /* Add missing fields */
        for ( Map.Entry<String, FieldAccess> field : fieldsFallbacks.entrySet() ) {
            if ( !fieldsPrimary.containsKey( field.getKey() ) ) {
                fieldsPrimary.put( field.getKey(), field.getValue() );
            }
        }
    }

    public static Map<String, FieldAccess> getPropertyFieldAccessMapPropertyFirst( Class<?> clazz ) {
        /* Fallback map. */
        Map<String, FieldAccess> fieldsFallbacks = null;

        /* Primary merge into this one. */
        Map<String, FieldAccess> fieldsPrimary = null;



             /* Try to find the properties first if this is set. */
        fieldsFallbacks = Reflection.getAllAccessorFields( clazz, true );
        fieldsPrimary = Reflection.getPropertyFieldAccessors( clazz );


        /* Add missing fields */
        combineFieldMaps( fieldsFallbacks, fieldsPrimary );

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

        Class<?> clz = value1.getClass();
        return classHasStringField( clz, name );
    }

    /**
     * Checks to see if this class has a string field.
     *
     * @param clz
     * @param name
     * @return
     */
    public static boolean classHasStringField( Class<?> clz, String name ) {

        List<Field> fields = getAllFields( clz );
        for ( Field field : fields ) {
            if (
                    field.getType().equals( Typ.string ) &&
                            field.getName().equals( name ) &&
                            !Modifier.isStatic( field.getModifiers() ) &&
                            field.getDeclaringClass() == clz
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
        return classHasField( value1.getClass(), name );
    }

    /**
     * Checks to see if a class has a field.
     *
     * @param clz
     * @param name
     * @return
     */
    public static boolean classHasField( Class<?> clz, String name ) {
        List<Field> fields = getAllFields( clz );
        for ( Field field : fields ) {
            if ( field.getName().equals( name )
                    && !Modifier.isStatic( field.getModifiers() )
                    && field.getDeclaringClass() == clz ) {
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
        return getFirstComparableOrPrimitiveFromClass( value1.getClass() );
    }

    /**
     * This can be used for default sort.
     *
     * @param clz class we are analyzing
     * @return first field that is comparable or primitive or null if not found.
     */
    public static String getFirstComparableOrPrimitiveFromClass( Class<?> clz ) {
        List<Field> fields = getAllFields( clz );
        for ( Field field : fields ) {

            if ( ( field.getType().isPrimitive() || Typ.isComparable( field.getType() )
                    && !Modifier.isStatic( field.getModifiers() )
                    && field.getDeclaringClass() == clz )
                    ) {
                return field.getName();
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
        return getFirstStringFieldNameEndsWithFromClass( value.getClass(), name );
    }

    /**
     * getFirstStringFieldNameEndsWithFromClass
     *
     * @param clz  class we are looking at
     * @param name name
     * @return field name or null
     */
    public static String getFirstStringFieldNameEndsWithFromClass( Class<?> clz, String name ) {
        List<Field> fields = getAllFields( clz );
        for ( Field field : fields ) {
            if (
                    field.getName().endsWith( name )
                            && field.getType().equals( Typ.string )
                            && !Modifier.isStatic( field.getModifiers() )
                            && field.getDeclaringClass() == clz ) {

                return field.getName();
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
        return getSortableFieldFromClass( value1.getClass() );
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
        String fieldName = getSortableField( clazz );

        /**
         * Not found in cache.
         */
        if ( fieldName == null ) {

            /* See if we have this sortale field and look for string first. */
            for ( String name : fieldSortNames ) {
                if ( classHasStringField( clazz, name ) ) {
                    fieldName = name;
                    break;
                }
            }

            /*
             Now see if we can find one of our predefined suffixes.
             */
            if ( fieldName == null ) {
                for ( String name : fieldSortNamesSuffixes ) {
                    fieldName = getFirstStringFieldNameEndsWithFromClass( clazz, name );
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
                fieldName = getFirstComparableOrPrimitiveFromClass( clazz );
            }

            /* We could not find a sortable field. */
            if ( fieldName == null ) {
                setSortableField( clazz, "NOT FOUND" );
                die( "Could not find a sortable field for type " + clazz );

            }

            /* We found a sortable field. */
            setSortableField( clazz, fieldName );
        }
        return fieldName;

    }


    public static boolean hasField( Class<?> aClass, String name ) {
        Map<String, FieldAccess> fields = getAllAccessorFields( aClass );
        return fields.containsKey( name );
    }

    @SuppressWarnings ( "serial" )
    public static class ReflectionException extends RuntimeException {

        public ReflectionException() {
            super();
        }

        public ReflectionException( String message, Throwable cause ) {
            super( message, cause );
        }

        public ReflectionException( String message ) {
            super( message );
        }

        public ReflectionException( Throwable cause ) {
            super( cause );
        }
    }

    public static boolean isArray( Object obj ) {
        if ( obj == null ) return false;
        return obj.getClass().isArray();
    }

    public static int len( Object obj ) {
        if ( isArray( obj ) ) {
            return arrayLength( obj );
        } else if ( obj instanceof CharSequence ) {
            return ( ( CharSequence ) obj ).length();
        } else if ( obj instanceof Collection ) {
            return ( ( Collection<?> ) obj ).size();
        } else if ( obj instanceof Map ) {
            return ( ( Map<?, ?> ) obj ).size();
        } else {
            die( "Not an array like object" );
            return 0; //will never get here.
        }
    }


    public static int arrayLength( Object obj ) {
        return Array.getLength( obj );
    }


    private static void handle( Exception ex ) {
        throw new ReflectionException( ex );
    }



    public static Object newInstance( String className ) {

        try {
            Class<?> clazz = Class.forName( className );


            return newInstance( clazz );


        } catch ( Exception ex ) {
            log.info( String.format( "Unable to create this class %s", className ) );
            return null;
        }
    }

    public static <T> T newInstance( Class<T> clazz ) {
        T newInstance = null;

        try {
            if ( _useUnsafe ) {
                newInstance = ( T ) getUnsafe().allocateInstance( clazz );
            } else {
                newInstance = clazz.newInstance();
            }
        } catch ( Exception ex ) {
            handle( ex );
        }

        return newInstance;

    }


    public static Class<?> getComponentType( Collection<?> collection, FieldAccess fieldAccess ) {
        Class<?> clz = fieldAccess.getComponentClass();
        if ( clz == null ) {
            clz = getComponentType( collection );
        }
        return clz;

    }

    public static Class<?> getComponentType( Collection<?> value ) {
        if ( value.size() > 0 ) {
            Object next = value.iterator().next();
            return next.getClass();
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
                return UnsafeField.createUnsafeField( from );
            } else {
                return new ReflectField( from );
            }
        }
    }

    public static Map<String, FieldAccess> getAllAccessorFields(
            Class<? extends Object> theClass ) {
        return getAllAccessorFields( theClass, true );
    }

    public static Map<String, FieldAccess> getAllAccessorFields(
            Class<? extends Object> theClass, boolean useUnsafe ) {
        Map<String, FieldAccess> map = getAccesorFieldFromCache( theClass, useUnsafe );
        if ( map == null ) {
            List<FieldAccess> list = Conversions.map( new FieldConverter( useUnsafe ), getAllFields( theClass ) );
            map = new LinkedHashMap<>( list.size() );
            for ( FieldAccess fieldAccess : list ) {
                map.put( fieldAccess.getName(), fieldAccess );
            }

            setAccessorFieldInCache( theClass, useUnsafe, map );

        }
        return map;
    }


    public static List<Field> getAllFields( Class<? extends Object> theClass ) {
        List<Field> list = getFields( theClass );
        while ( theClass != Typ.object ) {

            theClass = theClass.getSuperclass();
            getFields( theClass, list );
        }
        return list;
    }


    public static Map<String, FieldAccess> getPropertyFieldAccessors(
            Class<? extends Object> theClass ) {


        Map<String, FieldAccess> fields = getPropertyAccessorFieldsFromCache( theClass );
        if ( fields == null ) {
            Map<String, Pair<Method>> methods = getPropertySetterGetterMethods( theClass );

            fields = new LinkedHashMap<>();

            for ( Map.Entry<String, Pair<Method>> entry :
                    methods.entrySet() ) {

                final Pair<Method> methodPair = entry.getValue();
                final String key = entry.getKey();

                PropertyField pf = new PropertyField( key, methodPair.getFirst(), methodPair.getSecond() );

                fields.put( key, pf );

            }

            setPropertyAccessorFieldsInCache( theClass, fields );
        }


        return fields;
    }

    public static Map<String, Pair<Method>> getPropertySetterGetterMethods(
            Class<? extends Object> theClass ) {

        Method[] methods = theClass.getMethods();

        Map<String, Pair<Method>> methodMap = new LinkedHashMap<>( methods.length );
        List<Method> getterMethodList = new ArrayList<>( methods.length );

        for ( int index = 0; index < methods.length; index++ ) {
            Method method = methods[ index ];
            String name = method.getName();

            if ( method.getParameterTypes().length == 1
                    && method.getReturnType() == void.class
                    && name.startsWith( "set" ) ) {
                Pair<Method> pair = new Pair<Method>();
                pair.setFirst( method );
                String propertyName = slc( name, 3 );

                propertyName = lower( slc( propertyName, 0, 1 ) ) + slc( propertyName, 1 );
                methodMap.put( propertyName, pair );
            }

            if ( method.getParameterTypes().length > 0
                    || method.getReturnType() == void.class
                    || !( name.startsWith( "get" ) || name.startsWith( "is" ) )
                    || name.equals( "getClass" ) ) {
                continue;
            }
            getterMethodList.add( method );
        }

        for ( Method method : getterMethodList ) {
            String name = method.getName();
            String propertyName = null;
            if ( name.startsWith( "is" ) ) {
                propertyName = name.substring( 2 );
            } else if ( name.startsWith( "get" ) ) {
                propertyName = name.substring( 3 );
            }

            propertyName = lower( propertyName.substring( 0, 1 ) ) + propertyName.substring( 1 );

            Pair<Method> pair = methodMap.get( propertyName );
            if ( pair == null ) {
                pair = new Pair<>();
                methodMap.put( propertyName, pair );
            }
            pair.setSecond( method );

        }
        return methodMap;
    }

    public static void getFields( Class<? extends Object> theClass,
                                  List<Field> list ) {
        List<Field> more = getFields( theClass );
        list.addAll( more );
    }

    public static List<Field> getFields( Class<? extends Object> theClass ) {
        List<Field> list = Lists.list( theClass.getDeclaredFields() );
        for ( Field field : list ) {
            field.setAccessible( true );
        }
        return list;
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
        Map<String, FieldAccess> fields = getAllAccessorFields( item.getClass() );
        T clone = null;
        try {
            clone = ( T ) item.getClass().newInstance();
        } catch ( Exception e ) {
            handle( e );
        }
        for ( FieldAccess field : fields.values() ) {
            if ( field.isStatic() || field.isFinal() || field.isReadOnly() ) {
                continue;
            }
            field.setValue( clone, field.getValue( item ) );
        }
        return clone;
    }


    public static Iterator iterator( final Object o ) {
        if ( o instanceof Collection ) {
            return ( ( Collection ) o ).iterator();
        } else if ( isArray( o ) ) {
            return new Iterator() {
                int index = 0;
                int length = len( o );

                @Override
                public boolean hasNext() {
                    return index < length;
                }

                @Override
                public Object next() {
                    Object value = Reflection.idx( o, index );
                    index++;
                    return value;
                }

                @Override
                public void remove() {
                }
            };
        }
        return null;
    }


    public static String joinBy( char delim, Object... args ) {
        CharBuf builder = CharBuf.create( 256 );
        int index = 0;
        for ( Object arg : args ) {
            builder.add( arg.toString() );
            if ( !( index == args.length - 1 ) ) {
                builder.add( delim );
            }
            index++;
        }
        return builder.toString();
    }


    public static List<Map<String, Object>> toListOfMaps( Collection<?> collection ) {
        List<Map<String, Object>> list = new ArrayList<>();
        for ( Object o : collection ) {
            list.add( MapObjectConversion.toMap ( o ) );
        }
        return list;
    }


    public static Object idx( Object object, int index ) {
        if ( isArray( object ) ) {
            object = Array.get( object, index );
        } else if ( object instanceof List ) {
            object = Lists.idx( ( List ) object, index );
        }
        return object;
    }

    public static void idx( Object object, int index, Object value ) {
        try {
            if ( isArray( object ) ) {
                Array.set( object, index, value );
            } else if ( object instanceof List ) {
                Lists.idx( ( List ) object, index, value );
            }
        } catch ( Exception notExpected ) {
            String msg = lines( "An unexpected error has occurred",
                    "This is likely a programming error!",
                    String.format( "Object is %s, index is %s, and set is %s", object, index, value ),
                    String.format( "The object is an array? %s", object == null ? "null" : object.getClass().isArray() ),
                    String.format( "The object is of type %s", object == null ? "null" : object.getClass().getName() ),
                    String.format( "The set is of type %s", value == null ? "null" : value.getClass().getName() ),

                    ""

            );
            Exceptions.handle( msg, notExpected );
        }
    }




}
