package org.boon.core.reflection;

import org.boon.Lists;
import org.boon.collections.MultiMap;
import org.boon.core.reflection.fields.FieldAccess;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.boon.Exceptions.die;


/**
 * Created by Richard on 2/17/14.
 */
public class ClassMeta <T> {

    final Class<T> cls;

    final Map<String, MethodAccess> methodMap;
    final MultiMap<String, MethodAccess> methodsMulti;
    final List <MethodAccess> methods;

    final Map<String, FieldAccess> fieldMap;
    final Map<String, FieldAccess> propertyMap;


    final List<FieldAccess> fields;
    final List<FieldAccess> properties;

    final static MethodAccess MANY_METHODS = new MethodAccess(){
        @Override
        public Object invoke( Object object, Object... args ) {
            return die(Object.class, "Unable to invoke method as there are more than one with that same name", object, args);
        }

        @Override
        public Iterator<AnnotationData> annotationData() {
            return die(Iterator.class, "Unable to use method as there are more than one with that same name");
        }

        @Override
        public boolean hasAnnotation( String annotationName ) {
            return die(Boolean.class, "Unable to invoke method as there are more than one with that same name");
        }

        @Override
        public AnnotationData getAnnotation( String annotationName ) {
            return die(AnnotationData.class, "Unable to invoke method as there are more than one with that same name");
        }

        @Override
        public Class<?>[] parameterTypes() {
            return die(Class[].class, "Unable to invoke method as there are more than one with that same name");
        }

        @Override
        public Type[] getGenericParameterTypes() {
            return die(Type[].class, "Unable to invoke method as there are more than one with that same name");
        }
    };


    public ClassMeta( Class<T> cls ) {
        this.cls = cls;
        fieldMap = Reflection.getAllAccessorFields( this.cls );
        fields = Lists.list(fieldMap.values());
        propertyMap = Reflection.getPropertyFieldAccessors( this.cls );
        properties = Lists.list(propertyMap.values());
        List<Class<?>> classes = getBaseClassesSuperFirst();
        methodMap = new ConcurrentHashMap<>(  );
        methodsMulti = new MultiMap<>(  );



        for (Class clasz : classes) {
            Method[] methods_ = clasz.getDeclaredMethods();

            for (Method m : methods_) {
                if ( methodMap.containsKey( m.getName() )) {
                    /** Checking for duplicates */
                    MethodAccessImpl invoker = ( MethodAccessImpl ) methodMap.get( m.getName() );
                    if (invoker.method.getParameterTypes().length != m.getParameterTypes().length) {
                        methodMap.put( m.getName(), MANY_METHODS );
                    } else {
                        boolean match = true;
                        for (int index =0; index < m.getParameterTypes().length; index++) {
                            if (m.getParameterTypes()[index] != invoker.method.getParameterTypes()[index]) {
                                match = false;
                            }
                        }
                        /* A match means a subclass overrode a base class. */
                        if ( match ) {
                            methodMap.put( m.getName(), new MethodAccessImpl( m ) );
                        } else {
                            /* Don't allow overloads. */
                            methodMap.put( m.getName(), MANY_METHODS );
                        }
                    }

                } else {
                    methodMap.put( m.getName(), new MethodAccessImpl( m ));
                }
                methodsMulti.put( m.getName(), new MethodAccessImpl( m ) );
            }
        }

        methods = Lists.list( methodsMulti.values() );


    }

    public static ClassMeta classMeta( Class<?> aClass ) {
        ClassMeta meta = Reflection.context()._classMetaMap.get( aClass );
        if (meta == null) {
            meta = new ClassMeta( aClass );
            Reflection.context()._classMetaMap.put( aClass, meta );
        }
        return meta;
    }

    public MethodAccess method(String name) {
        return methodMap.get( name );
    }


    public Iterable<MethodAccess> methods(String name) {
        return methodsMulti.getAll( name );
    }

    private List<Class<?>> getBaseClassesSuperFirst() {
        List<Class<?>> classes = new ArrayList( 10 );
        Class<?> currentClass = cls;
        while (currentClass != Object.class) {
            classes.add( currentClass );
            currentClass = currentClass.getSuperclass();
        }
        java.util.Collections.reverse( classes );
        return classes;

    }



    public Map<String, FieldAccess> fieldMap() {
        return fieldMap;
    }

    public Map<String, FieldAccess> propertyMap() {
        return propertyMap;
    }

    public Iterator<FieldAccess> fields() {
        return fields.iterator();
    }


    public Iterator<MethodAccess> methods() {
        return methods.iterator();
    }

    public Iterator<FieldAccess> properties() {
        return properties.iterator();
    }
}
