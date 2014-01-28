package org.boon.di;

import org.boon.Exceptions;
import org.boon.core.Supplier;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class InstanceModule implements Module{

    private Map<Class, Supplier<Object>> supplierMap = new HashMap<> (  );
    private Object module;

    public InstanceModule(Object object) {
        module = object;
        Method[] methods = object.getClass ().getDeclaredMethods();
        for ( Method method : methods ) {
            if ( !Modifier.isStatic( method.getModifiers () ) && method.getName ().startsWith ( "provide" ) ) {
                 addCreationMethod(method);
            }
        }


    }

    private void addCreationMethod( Method method ) {
        Class cls = method.getReturnType ();

        Supplier<Object> supplier = createSupplier ( method );
        this.supplierMap.put ( cls, supplier );

        Class superClass = cls.getSuperclass ();


        Class[] superTypes = cls.getInterfaces();

        for (Class superType : superTypes) {
                this.supplierMap.put ( superType, supplier );
        }

        if (superClass != null)   {
        while ( superClass != Object.class) {
            this.supplierMap.put ( superClass, supplier );
            superTypes = cls.getInterfaces();
            for (Class superType : superTypes) {
                this.supplierMap.put ( superType, supplier );
            }
            superClass = cls.getSuperclass ();
        }
        }

    }

    private Supplier<Object> createSupplier( final Method method ) {
        method.setAccessible ( true );
        return new Supplier<Object> () {
            @Override
            public Object get() {
                try {
                    return method.invoke ( module  );
                } catch ( Exception e ) {
                    return Exceptions.handle (Object.class, e);
                }
            }
        };
    }

    @Override
    public <T> T get( Class<T> type ) {
        return (T) supplierMap.get ( type ).get ();
    }

    @Override
    public boolean has( Class type ) {
        return supplierMap.containsKey ( type );
    }
}
