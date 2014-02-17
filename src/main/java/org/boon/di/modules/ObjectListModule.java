package org.boon.di.modules;

import org.boon.Exceptions;
import org.boon.Sets;
import org.boon.collections.MultiMap;
import org.boon.core.Supplier;
import org.boon.core.reflection.BeanUtils;
import org.boon.di.ProviderInfo;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ObjectListModule extends BaseModule {

    private final boolean prototypes;
    Map<Class, Object> objects = new ConcurrentHashMap<>();
    MultiMap<String, Object> nameMap = new MultiMap<>();



    @Override
    public  Iterable<Object> values() {
        return objects.values();
    }

    @Override
    public Iterable<String> names() {
        return nameMap.keySet();
    }

    @Override
    public Iterable types() {
        return objects.keySet();
    }


    public ObjectListModule( boolean prototypes, Object... objects ) {

        this.prototypes = prototypes;
        for ( Object object : objects ) {
            extractTypeInfo( object, false );

        }
    }


    public ObjectListModule( boolean prototypes, ProviderInfo... objects ) {
        this.prototypes = prototypes;
        for ( ProviderInfo info : objects ) {
            if ( info.name() != null ) {
                nameMap.put( info.name(), info.value() );
                extractTypeInfo( info.value(), true );
            } else {
                extractTypeInfo( info.value(), false );
            }
        }
    }


    @Override
    public <T> T get( Class<T> type ) {
        try {
            if ( !prototypes ) {
                return ( T ) objects.get( type );
            } else {
                return BeanUtils.copy( ( T ) objects.get( type ) );
            }
        } catch ( Exception e ) {
            Exceptions.handle( e );
            return null;
        }
    }

    @Override
    public Object get( String name ) {
        try {
            if ( !prototypes ) {
                return nameMap.get( name );
            } else {
                return BeanUtils.copy( nameMap.get( name ) );
            }
        } catch ( Exception e ) {
            Exceptions.handle( e );
            return null;
        }
    }

    @Override
    public <T> T get( Class<T> type, String name ) {
        try {

            Set<Object> set = Sets.set( nameMap.getAll( name ) );
            for ( Object object : set ) {
                if ( type.isAssignableFrom( object.getClass() ) ) {


                    if ( !prototypes ) {
                        return ( T ) object;
                    } else {
                        return ( T ) BeanUtils.copy( object );
                    }
                }
            }
            return null;
        } catch ( Exception e ) {
            Exceptions.handle( e );
            return null;
        }

    }

    @Override
    public boolean has( Class type ) {
        return objects.containsKey( type );
    }

    @Override
    public boolean has( String name ) {
        return nameMap.containsKey( name );
    }

    @Override
    public <T> Supplier<T> getSupplier( final Class<T> type, final String name ) {
        return new  Supplier<T>() {

            @Override
            public T get() {
                return ObjectListModule.this.get( type, name );
            }
        };
    }

    @Override
    public <T> Supplier<T> getSupplier( final Class<T> type ) {
        return new  Supplier<T>() {

            @Override
            public T get() {
                return ObjectListModule.this.get( type );
            }
        };
    }



    private void extractTypeInfo( Object object, boolean foundName ) {
        Class cls = object.getClass();
        this.objects.put( cls, object );


        String named = null;
        if ( !foundName ) {
            named = NamedUtils.namedValueForClass( cls );

            if ( named != null ) {
                nameMap.put( named, object );
                foundName = true;
            }
        }


        Class superClass = cls.getSuperclass();


        Class[] superTypes = cls.getInterfaces();

        for ( Class superType : superTypes ) {
            this.objects.put( superType, object );
        }

        while ( superClass != Object.class ) {
            this.objects.put( superClass, object );


            if ( !foundName ) {
                named = NamedUtils.namedValueForClass( superClass );
                if ( named != null ) {
                    nameMap.put( named, object );
                    foundName = true;
                }
            }

            superTypes = cls.getInterfaces();
            for ( Class superType : superTypes ) {
                this.objects.put( superType, object );
            }
            superClass = superClass.getSuperclass();
        }
    }

}
