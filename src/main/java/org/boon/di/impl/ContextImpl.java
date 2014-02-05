package org.boon.di.impl;

import org.boon.collections.ConcurrentLinkedHashSet;
import org.boon.core.Supplier;
import org.boon.core.reflection.MapObjectConversion;
import org.boon.core.reflection.Reflection;
import org.boon.core.reflection.fields.FieldAccess;
import org.boon.di.Context;
import org.boon.di.Module;

import java.util.Map;

import static org.boon.Boon.sputs;
import static org.boon.Exceptions.die;
import static org.boon.core.reflection.BeanUtils.idxBoolean;

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

    @Override
    public <T> Supplier<T> getSupplier( Class<T> type, String name ) {


        Supplier<T> supplier = null;
        for ( Module module : modules ) {

            if ( module.has( name ) ) {
                supplier = module.getSupplier( type, name );
                break;
            }
        }
        final Supplier<T> s = supplier;

        return new Supplier<T>() {
            @Override
            public T get() {
                T o = s.get();
                resolveProperties( o );
                return o;
            }
        };
    }

    @Override
    public <T> Supplier<T> getSupplier( Class<T> type ) {

        Supplier<T> supplier = null;
        for ( Module module : modules ) {

            if ( module.has( type ) ) {
                supplier = module.getSupplier( type );
                break;
            }
        }

        final Supplier<T> s = supplier;

        return new Supplier<T>() {
            @Override
            public T get() {
                T o = s.get();
                resolveProperties( o );
                return o;
            }
        };
    }


    private void resolveProperties( Object object ) {


        if ( object != null ) {

            /* Since there is no concept of singleton or scope, you need some sort of flag to determine
            if injection has already happened for objects that are like singletons.
             */
            if (Reflection.hasField( object, "__init__" )) {
                if (idxBoolean( object, "__init__" )) {
                    return;
                }
            }

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
        if (fieldNamed && field.type() != Supplier.class) {
            value =   get( field.type(), field.named() );
        } else if (fieldNamed && field.type() == Supplier.class)  {
            value =  getSupplier( field.getComponentClass(), field.named() );
        }
        else {
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
