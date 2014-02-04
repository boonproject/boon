package org.boon.di.impl;

import org.boon.collections.ConcurrentLinkedHashSet;
import org.boon.core.reflection.Reflection;
import org.boon.core.reflection.fields.FieldAccess;
import org.boon.di.Context;
import org.boon.di.Module;

import java.util.Map;
import java.util.Set;

public class ContextImpl implements Context {

    private Set<Module> modules = new ConcurrentLinkedHashSet<>();

    public ContextImpl( Module... modules ) {
        for ( Module module : modules ) {
            this.modules.add( module );
        }
    }

    @Override
    public <T> T get( Class<T> type ) {

        Object object = null;
        for ( Module module : modules ) {

            if ( module.has( type ) ) {
                object = module.get( type );
                break;
            }
        }

        resolveProperties( object );

        return ( T ) object;
    }

    @Override
    public <T> T get( Class<T> type, String name ) {

        T object = null;
        for ( Module module : modules ) {

            if ( module.has( name ) ) {
                object = module.get( type, name );
                break;
            }
        }

        resolveProperties( object );

        return object;
    }

    private void resolveProperties( Object object ) {
        if ( object != null ) {
            Map<String, FieldAccess> fields = Reflection.getAllAccessorFields( object.getClass(), true );
            for ( FieldAccess field : fields.values() ) {
                if ( field.hasAnnotation( "Inject" ) && field.hasAnnotation( "Named" ) ) {
                    field.setValue( object, get( field.getType(), named( field ) ) );
                } else if ( field.hasAnnotation( "Inject" ) ) {
                    field.setValue( object, get( field.getType() ) );
                }
            }
        }
    }

    private String named( FieldAccess field ) {
        return ( String ) field.getAnnotationData( "Named" ).get( "value" );
    }

    @Override
    public Object get( String name ) {

        Object object = null;
        for ( Module module : modules ) {

            if ( module.has( name ) ) {
                object = module.get( name );
                break;
            }
        }

        resolveProperties( object );

        return object;
    }
}
