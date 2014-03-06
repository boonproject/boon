package org.boon.core.reflection;

import org.boon.*;
import org.boon.core.*;
import org.boon.core.reflection.fields.*;
import sun.misc.Unsafe;

import java.lang.ref.WeakReference;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import static org.boon.Boon.sputs;
import static org.boon.Exceptions.die;
import static org.boon.Str.*;


public class Reflection {

    private static final Logger log = Logger.getLogger( Reflection.class.getName() );

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


    public static Object contextToHold() {
        return context();
    }

    /* Manages weak references. */
    static Context context() {

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

    static class Context {

        Unsafe control;
        Map<String, String> _sortableFields = new ConcurrentHashMap<>();

        Map<Class<?>, ClassMeta<?>> _classMetaMap = new ConcurrentHashMap<>( 200 );

        Map<Class<?>, Map<String, FieldAccess>> _allAccessorReflectionFieldsCache = new ConcurrentHashMap<>( 200 );
        Map<Class<?>, Map<String, FieldAccess>> _allAccessorPropertyFieldsCache = new ConcurrentHashMap<>( 200 );
        Map<Class<?>, Map<String, FieldAccess>> _allAccessorUnsafeFieldsCache = new ConcurrentHashMap<>( 200 );


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


    private static void handle( Exception ex ) {
        throw new ReflectionException( ex );
    }





    public static Class<?> loadClass( String className ) {

        try {
            Class<?> clazz = Class.forName( className );


            return clazz;


        } catch ( Exception ex ) {
            log.info( String.format( "Unable to create load class %s", className ) );
            return null;
        }
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
        ClassMeta <T> cls = ClassMeta.classMeta(clazz);

        try {
            /* See if there is a no arg constructor. */
            ConstructorAccess<T> declaredConstructor = cls.noArgConstructor();
            if (declaredConstructor !=null ) {
                /* If there was a no argument constructor, then use it. */
                newInstance = declaredConstructor.create();
            } else {
                if ( _useUnsafe ) {
                    newInstance = ( T ) getUnsafe().allocateInstance( clazz );
                } else {
                    die ( sputs( clazz.getName (), "does not have a no arg constructor and unsafe is not turned on" ) );
                }

            }
        } catch ( Exception ex ) {
            try {
                if ( _useUnsafe ) {
                    newInstance = ( T ) getUnsafe().allocateInstance( clazz );
                    return newInstance; //we handled it.
                }
            } catch ( Exception ex2 ) {
                handle( ex2 );
            }

            handle( ex );
        }

        return newInstance;

    }

    public static <T> T newInstance( Class<T> clazz, Object arg ) {
        T newInstance = null;


        ClassMeta <T> cls = ClassMeta.classMeta(clazz);
         try {
            /* See if there is a no arg constructor. */
            ConstructorAccess<T> declaredConstructor = cls.declaredConstructor(arg.getClass());
            if (declaredConstructor !=null ) {
                /* If there was a no argument constructor, then use it. */
                newInstance = declaredConstructor.create(arg);
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

    private static class FieldConverter implements Function<Field, FieldAccess> {

        boolean thisUseUnsafe;

        FieldConverter( boolean useUnsafe ) {
            this.thisUseUnsafe = useUnsafe;
        }

        @Override
        public FieldAccess apply( Field from ) {
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
            List<FieldAccess> list = Lists.mapBy( getAllFields( theClass ), new FieldConverter( useUnsafe ) );
            map = new LinkedHashMap<>( list.size() );
            for ( FieldAccess fieldAccess : list ) {
                map.put( fieldAccess.name(), fieldAccess );
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

                fields.put( pf.alias(), pf );

            }

            setPropertyAccessorFieldsInCache( theClass, fields );
        }


        return fields;
    }

    public static Map<String, Pair<Method>> getPropertySetterGetterMethods(
            Class<? extends Object> theClass ) {

        try {
            Method[] methods = theClass.getMethods();

            Map<String, Pair<Method>> methodMap = new LinkedHashMap<>( methods.length );
            List<Method> getterMethodList = new ArrayList<>( methods.length );

            for ( int index = 0; index < methods.length; index++ ) {
                Method method = methods[ index ];
                if (extractPropertyInfoFromMethodPair(methodMap, getterMethodList, method)) continue;
            }

            for ( Method method : getterMethodList ) {
                extractProperty(methodMap, method);

            }
            return methodMap;
        } catch (Exception ex) {
            ex.printStackTrace();
            return Exceptions.handle(Map.class, ex, theClass);
        }
    }

    private static boolean extractPropertyInfoFromMethodPair(Map<String, Pair<Method>> methodMap,
                                                             List<Method> getterMethodList,
                                                             Method method) {
        String name = method.getName();

        try {

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
                    || name.equals( "getClass" ) || name.equals("get") || name.equals("is") ) {
                return true;
            }
            getterMethodList.add( method );
            return false;

        } catch (Exception ex) {
            return Exceptions.handle(Boolean.class, ex, name, method);
        }
    }

    private static void extractProperty(Map<String, Pair<Method>> methodMap, Method method) {
        try {
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
        } catch (Exception ex) {
            Exceptions.handle(ex, "extractProperty property extract of getPropertySetterGetterMethods", method);
        }
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


    public static boolean respondsTo( Class<?> type, String methodName) {
        return ClassMeta.classMeta(type).respondsTo(methodName);
    }

    public static boolean respondsTo( Class<?> type, String methodName, Class<?>... params) {
        return ClassMeta.classMeta(type).respondsTo(methodName, params);
    }


    public static boolean respondsTo( Class<?> type, String methodName, Object... params) {
        return ClassMeta.classMeta(type).respondsTo(methodName, params);
    }


    public static boolean respondsTo( Class<?> type, String methodName, List<?> params) {
        return ClassMeta.classMeta(type).respondsTo(methodName, params);
    }


    public static boolean respondsTo( Object object, String methodName) {
        if (object == null || methodName == null) {
            return false;
        }
        return ClassMeta.classMeta(object.getClass()).respondsTo(methodName);
    }

    public static boolean respondsTo( Object object, String methodName, Class<?>... params) {
        return ClassMeta.classMeta(object.getClass()).respondsTo(methodName, params);
    }


    public static boolean respondsTo( Object object, String methodName, Object... params) {
        return ClassMeta.classMeta(object.getClass()).respondsTo(methodName, params);
    }


    public static boolean respondsTo( Object object, String methodName, List<?> params) {
        return ClassMeta.classMeta(object.getClass()).respondsTo(methodName, params);
    }


    public static boolean handles( Object object, Class<?> interfaceCls) {
        return ClassMeta.classMeta(object.getClass()).handles(interfaceCls);
    }


    public static boolean handles( Class cls, Class<?> interfaceCls) {
        return ClassMeta.classMeta(cls).handles(interfaceCls);
    }


    public static Object invoke (Object object, String name, Object... args){
        return ClassMeta.classMeta( object.getClass() ).invoke(object, name, args );
    }


    public static Object invoke (Object object, String name, List<?> args){
        return ClassMeta.classMeta( object.getClass() ).invoke(object, name, args );
    }
}