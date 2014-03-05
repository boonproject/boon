package org.boon.core.reflection.impl;

import org.boon.Lists;
import org.boon.core.reflection.AnnotationData;
import org.boon.core.reflection.Annotations;
import org.boon.core.reflection.MethodAccess;

import java.lang.invoke.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.boon.Exceptions.die;
import static org.boon.Exceptions.handle;

/**
 * Created by Richard on 2/17/14.
 */
public class MethodAccessImpl implements MethodAccess {

    final public Method method;
    final List<AnnotationData> annotationData;
    final Map<String, AnnotationData> annotationMap;


    //CallSite callSiteMethod;

    //final MethodHandles.Lookup lookup = MethodHandles.lookup();
    //final MethodHandle methodHandle;

    public MethodAccessImpl() {
        method=null;
        annotationData=null;
        annotationMap=null;
        //methodHandle = null;
    }

    public MethodAccessImpl( Method method ) {
        this.method = method;
        this.method.setAccessible( true );
        this.annotationData = Annotations.getAnnotationDataForMethod(method);


//        MethodHandle m;
//         try {
//
//                    m = lookup.unreflect(method);
//
//        } catch (Exception e) {
//            m = null;
//            handle(e);
//        }
//
//
//        methodHandle = m;

        annotationMap = new ConcurrentHashMap<>(  );
        for (AnnotationData data : annotationData) {
            annotationMap.put( data.getName(), data );
            annotationMap.put( data.getSimpleClassName(), data );
            annotationMap.put( data.getFullClassName(), data );
        }

    }


    public Object invoke(Object object, Object... args) {
        try {
            return method.invoke( object, args );
        } catch ( Throwable ex ) {

            return handle( Object.class, ex,  "unable to invoke method", method,
                    " on object ", object, "with arguments", args,
                    "\nparameter types", parameterTypes(), "\nargument types are" );

        }
    }


    public Object invokeBound(Object... args) {
        try {
            return method.invoke( instance, args );
        } catch ( Throwable ex ) {

            return handle( Object.class, ex,  "unable to invoke method", method,
                    " on object with arguments", args,
                    "\nparameter types", parameterTypes(), "\nargument types are" );

        }
    }

    @Override
    public Object invokeStatic(Object... args) {
        try {

            return method.invoke(null, args);
        } catch ( Throwable ex ) {
            return handle( Object.class, ex,  "unable to invoke method", method,
                    " with arguments", args );

        }
    }

    @Override
    public MethodAccess bind(Object instance) {
        die("Bind does not work for cached methodAccess make a copy with methodAccsess() first");
        return null;
    }

//    @Override
//    public MethodHandle methodHandle() {
//
//
//        MethodHandle m;
//        try {
//            m = lookup.unreflect(method);
//
//        } catch (Exception e) {
//            m = null;
//            handle(e);
//        }
//
//        return  m;
//    }

    Object instance;
    @Override
    public MethodAccess methodAccess() {
        return new MethodAccessImpl(this.method){


            @Override
            public MethodAccess bind(Object instance) {
                //methodHandle.bindTo(instance);
                this.instance = instance;
                return this;
            }


            @Override
            public Object bound() {
                return instance;
            }


        };
    }

    @Override
    public Object bound() {
        return null;
    }


    @Override
    public Iterable<AnnotationData> annotationData() {
        return new Iterable<AnnotationData>() {
            @Override
            public Iterator<AnnotationData> iterator() {
                return annotationData.iterator();
            }
        };
    }

    @Override
    public boolean hasAnnotation( String annotationName ) {
        return this.annotationMap.containsKey( annotationName );
    }

    @Override
    public AnnotationData annotation(String annotationName) {
        return this.annotationMap.get(annotationName);
    }

    @Override
    public boolean isStatic() {
        return Modifier.isStatic(method.getModifiers());
    }

    @Override
    public String name() {
        return method.getName();
    }

    @Override
    public Class<?> declaringType() {
        return method.getDeclaringClass();
    }

    @Override
    public Class<?> returnType() {
        return method.getReturnType();
    }

    @Override
    public boolean respondsTo(Class<?>[] parametersToMatch) {
        boolean match = true;

        Class<?>[] parameterTypes = method.getParameterTypes();


        if ( parameterTypes.length != parametersToMatch.length ) {
            return false;
        }


        for (int index = 0; index < parameterTypes.length; index++) {
            Class<?> type = parameterTypes[index];
            Class<?> matchToType = parametersToMatch[index];
            if (type.isPrimitive()) {

                if (!(type == int.class &&  ( matchToType == Integer.class || matchToType == int.class) ||
                        type == boolean.class &&  ( matchToType == Boolean.class || matchToType == boolean.class) ||
                        type == long.class &&  ( matchToType == Long.class  || matchToType == long.class) ||
                        type == float.class &&  ( matchToType == Float.class   || matchToType == float.class) ||
                        type == double.class &&  ( matchToType == Double.class   || matchToType == double.class) ||
                        type == short.class &&  ( matchToType == Short.class   || matchToType == short.class) ||
                        type == byte.class &&  ( matchToType == Byte.class   || matchToType == byte.class) ||
                        type == char.class &&  ( matchToType == Character.class || matchToType == char.class) )
                )
                {
                    match = false;
                    break;
                }


            } else if (!type.isAssignableFrom( matchToType )) {
                match = false;
                break;
            }
        }

        return match;
    }

    @Override
    public boolean respondsTo(Object... args) {



        boolean match = true;
        Class<?>[] parameterTypes = method.getParameterTypes();



        if ( parameterTypes.length != args.length ) {
            return false;
        }

        for (int index = 0; index < parameterTypes.length; index++) {
            Object arg = args[index];
            Class<?> type = parameterTypes[index];
            Class<?> matchToType = arg != null ? arg.getClass() : null;

            if (type.isPrimitive()) {

                if (arg == null) {
                    match = false;
                    break;
                }
                if (!(type == int.class &&  matchToType == Integer.class ||
                        type == boolean.class &&  matchToType == Boolean.class ||
                        type == long.class &&  matchToType == Long.class   ||
                        type == float.class &&  matchToType == Float.class   ||
                        type == double.class &&  matchToType == Double.class   ||
                        type == short.class &&  matchToType == Short.class   ||
                        type == byte.class &&  matchToType == Byte.class   ||
                        type == char.class &&  matchToType == Character.class
                ))
                {
                    match = false;
                    break;
                }


            } else if (arg == null) {

            } else if (!type.isInstance( arg )) {
                match = false;
                break;
            }
        }

        return match;
    }


    @Override
    public Class<?>[] parameterTypes() {
        return method.getParameterTypes();
    }

    @Override
    public Type[] getGenericParameterTypes() {
        return method.getGenericParameterTypes();
    }


    @Override
    public String toString() {
        return "MethodAccessImpl{" +
                "method=" + method +
                ", annotationData=" + annotationData +
                ", instance=" + instance +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MethodAccessImpl that = (MethodAccessImpl) o;

        if (annotationData != null ? !annotationData.equals(that.annotationData) : that.annotationData != null)
            return false;
        if (annotationMap != null ? !annotationMap.equals(that.annotationMap) : that.annotationMap != null)
            return false;
        if (instance != null ? !instance.equals(that.instance) : that.instance != null) return false;
        if (method != null ? !method.equals(that.method) : that.method != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = method != null ? method.hashCode() : 0;
        result = 31 * result + (annotationData != null ? annotationData.hashCode() : 0);
        result = 31 * result + (annotationMap != null ? annotationMap.hashCode() : 0);
        result = 31 * result + (instance != null ? instance.hashCode() : 0);
        return result;
    }
}
