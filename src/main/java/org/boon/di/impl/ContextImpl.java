package org.boon.di.impl;

import org.boon.collections.ConcurrentLinkedHashSet;
import org.boon.core.reflection.MapObjectConversion;
import org.boon.core.reflection.Reflection;
import org.boon.core.reflection.fields.FieldAccess;
import org.boon.di.Context;
import org.boon.di.Module;

import java.util.Map;

import static org.boon.Boon.puts;
import static org.boon.Boon.sputs;
import static org.boon.Exceptions.die;

public class ContextImpl implements Context, Module {

    protected ConcurrentLinkedHashSet<Module> modules = new ConcurrentLinkedHashSet<>();

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

    @Override
    public boolean has( Class type ) {

        for ( Module module : modules ) {

            if ( module.has( type ) ) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean has( String name ) {

        for ( Module module : modules ) {

            if ( module.has( name ) ) {
                return true;
            }
        }

        return false;
    }


    private void resolveProperties( Object object ) {
        if ( object != null ) {
            Map<String, FieldAccess> fields = Reflection.getAllAccessorFields( object.getClass(), true );
            for ( FieldAccess field : fields.values() ) {

                if ( ( field.injectable() ) ) {
                    handleInjectionOfField( object, field );
                }


            }
            Reflection.invokeMethodWithAnnotationNoReturn( object, "postConstruct" );
        }

    }

    private void handleInjectionOfField( Object object, FieldAccess field ) {

        Object value = null;


        boolean fieldNamed = field.isNamed();
        if (fieldNamed) {
            value =   get(field.type(), field.named());
        } else {
            value =   get(field.type() );
        }

        if (value == null && field.isNamed()) {
            value =   get( field.named() );
            if (value !=null ) {
                field.type().isAssignableFrom( value.getClass() );
            }
        }

        if (field.requiresInjection()) {
            if (value == null) {
                die(sputs(
                        "Unable to inject into", field.getName(), " of ", field.parent() , "with alias", field.named(), "was named",field.isNamed(), "field info",
                        field
                ));
            }
        }

        field.setValue( object, value  );
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

        if (object instanceof Map) {
            Map map = ( Map ) object;
            if (map.containsKey( "class" )) {
                object =  MapObjectConversion.fromMap( map );
            }
        }

        resolveProperties( object );

        return object;
    }

    @Override
    public Context add( Module module ) {
        this.modules.add( module );
        return this;
    }

    @Override
    public Context remove( Module module ) {
        this.modules.remove( module );
        return this;
    }

    @Override
    public Context addFirst( Module module ) {
        this.modules.addFirst( module );
        return this;
    }
}
