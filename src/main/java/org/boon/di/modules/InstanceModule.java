package org.boon.di.modules;

import org.boon.Exceptions;
import org.boon.Sets;
import org.boon.collections.MultiMap;
import org.boon.core.Supplier;
import org.boon.core.reflection.ClassMeta;
import org.boon.core.reflection.MethodAccess;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public class InstanceModule extends BaseModule {

    private Map<Class, Supplier<Object>> supplierMap = new ConcurrentHashMap<>();
    private MultiMap<String, Supplier<Object>> nameMap = new MultiMap<>();

    private Object module;

    public InstanceModule( Object object ) {
        module = object;

        ClassMeta classMeta = ClassMeta.classMeta(object.getClass());
        Iterable<MethodAccess> methods =  (Iterable<MethodAccess>)classMeta.methods();

        for ( MethodAccess method : methods) {

            if ( !method.isStatic() && method.name().startsWith( "provide" ) ) {
                addCreationMethod( method );
            }
        }


    }

    private static class InternalSupplier implements Supplier<Object> {

        private final Object module;

        private final MethodAccess method;

        InternalSupplier( MethodAccess method, Object module ) {
            this.method = method;
            this.module = module;
        }

        @Override
        public Object get() {
            try {
                return method.invoke( module );
            } catch ( Exception e ) {
                return Exceptions.handle( Object.class, e );
            }
        }
    }

    private Supplier<Object> createSupplier( final MethodAccess method ) {
        return new InternalSupplier( method, module );
    }

    @Override
    public <T> T get( Class<T> type ) {
        return ( T ) supplierMap.get( type ).get();
    }

    @Override
    public Object get( String name ) {
        return false;
    }


    @Override
    public <T> T get( Class<T> type, String name ) {

        return getSupplier( type, name ).get();
    }



    @Override
    public <T> Supplier<T> getSupplier( final Class<T> type, final String name ) {

        try {
            Set<Supplier<Object>> set = Sets.set( nameMap.getAll( name ) );
            for ( Supplier<Object> s : set ) {
                InternalSupplier supplier = ( InternalSupplier ) s;
                if ( type.isAssignableFrom( supplier.method.returnType() ) ) {
                    return (Supplier<T>)supplier;
                }
            }

            return  new Supplier<T>() {
                @Override
                public T get() {
                    return null;
                }
            };

        } catch ( Exception e ) {
            Exceptions.handle( e );
            return null;
        }

    }

    @Override
    public <T> Supplier<T> getSupplier( Class<T> type ) {
        Supplier<T> supplier = ( Supplier<T> ) supplierMap.get( type );
        if (supplier == null ) {
            supplier =  new Supplier<T>() {
                @Override
                public T get() {
                    return null;
                }
            };
        }

        return supplier;
    }

    @Override
    public Iterable<Object> values() {
        return (Iterable<Object>) (Object)supplierMap.values();
    }

    @Override
    public Iterable<String> names() {

        return nameMap.keySet();
    }

    @Override
    public Iterable types() {
        return supplierMap.keySet();
    }

    @Override
    public boolean has( Class type ) {
        return supplierMap.containsKey( type );
    }

    @Override
    public boolean has( String name ) {
        return nameMap.containsKey( name );
    }


    private void addCreationMethod( MethodAccess method ) {

        /** See if the name is in the method and that one takes precedence if found. */
        String named = NamedUtils.namedValueForMethod( method );
        boolean foundName = named != null;

        Class cls = method.returnType();


        /* Next see if named is in the class. */
        if ( !foundName ) {
            named = NamedUtils.namedValueForClass( cls );
            foundName = named != null;
        }

        Supplier<Object> supplier = createSupplier( method );
        this.supplierMap.put( cls, supplier );

        Class superClass = cls.getSuperclass();


        Class[] superTypes = cls.getInterfaces();

        for ( Class superType : superTypes ) {
            this.supplierMap.put( superType, supplier );
        }

        if ( superClass != null ) {
            while ( superClass != Object.class ) {
                this.supplierMap.put( superClass, supplier );

                  /* Next see if named is in the super if not found. */
                if ( !foundName ) {
                    named = NamedUtils.namedValueForClass( cls );
                    foundName = named != null;
                }
                superTypes = cls.getInterfaces();
                for ( Class superType : superTypes ) {
                    this.supplierMap.put( superType, supplier );
                }
                superClass = superClass.getSuperclass();
            }
        }


        if ( foundName ) {
            this.nameMap.put( named, supplier );
        }

    }
}
