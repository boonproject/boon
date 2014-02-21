package org.boon.di.impl;

import org.boon.collections.ConcurrentLinkedHashSet;
import org.boon.core.Supplier;
import org.boon.core.reflection.Fields;
import org.boon.core.reflection.Invoker;
import org.boon.core.reflection.MapObjectConversion;
import org.boon.core.reflection.Reflection;
import org.boon.core.reflection.fields.FieldAccess;
import org.boon.di.Context;
import org.boon.di.Module;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.boon.Boon.puts;
import static org.boon.Boon.sputs;
import static org.boon.Exceptions.die;
import static org.boon.core.reflection.BeanUtils.idxBoolean;
import static org.boon.json.JsonFactory.fromJson;

public class ContextImpl implements Context, Module {

    protected ConcurrentLinkedHashSet<Module> modules = new ConcurrentLinkedHashSet<>();
    private String name;
    private AtomicReference <Context> parent = new AtomicReference<>(  );

    @Override
    public void setParent( Context context ) {
        this.parent.set( context );
    }

    @Override
    public Iterable<Object> values() {

        List list = new ArrayList();
        for ( Module m : modules ) {

            for (Object o : m.values()) {
                list.add( o );
            }
        }
        return list;
    }

    @Override
    public Iterable<String> names() {
        List list = new ArrayList();
        for ( Module m : modules ) {

            for (String n : m.names()) {
                list.add( n );
            }
        }
        return list;
    }

    @Override
    public Iterable<Class<?>> types() {
        List list = new ArrayList();
        for ( Module m : modules ) {

            for (Class<?> c : m.types()) {
                list.add( c );
            }
        }
        return list;
    }


    @Override
    public Iterable<Module> children() {
        return modules;
    }

    public void setName( String name ) {
        this.name = name;
    }


    public ContextImpl( Module... modules ) {
        for ( Module module : modules ) {
            module.setParent( this );
            this.modules.add( module );
        }
    }

    @Override
    public <T> T get( Class<T> type ) {

        try {

            Object object = null;
            for ( Module module : modules ) {

                if ( module.has( type ) ) {
                    object = module.get( type );
                    break;
                }
            }

            resolveProperties( object );

            return ( T ) object;
        } finally {
        }
    }




    @Override
    public <T> T get( Class<T> type, String name ) {

        try {


            T object = null;
            for ( Module module : modules ) {

                if ( module.has( name ) ) {
                    object = module.get( type, name );
                    break;
                }
            }

            return object;

        } finally {
        }
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
    public <T> Supplier<T> getSupplier( final Class<T> type, final String name ) {


        try {


            Supplier<T> supplier = null;
            for ( Module module : modules ) {

                if ( module.has( name ) ) {
                    supplier = module.getSupplier( type, name );
                    break;
                }
            }

            final Supplier<T> s = supplier;
            final Context resolver = this;


            return new Supplier<T>() {
                String supplierName = name;
                Class<T> supplierType = type;
                @Override
                public T get() {
                    T o = s.get();
                    resolver.resolveProperties( o );
                    return o;
                }
            };


        } finally {
        }
    }

    @Override
    public <T> Supplier<T> getSupplier( Class<T> type ) {

        try {


            Supplier<T> supplier = null;
            for ( Module module : modules ) {

                if ( module.has( type ) ) {
                    supplier = module.getSupplier( type );
                    break;
                }
            }

            final Supplier<T> s = supplier;


            final Context resolver = ( Context ) this;

            return new Supplier<T>() {
                @Override
                public T get() {
                    T o = s.get();
                    resolveProperties( o );
                    return o;
                }
            };


        } finally {
        }
    }



    public void resolveProperties( Object object ) {


        if ( object != null ) {

            /* Since there is no concept of singleton or scope, you need some sort of flag to determine
            if injection has already happened for objects that are like singletons.
             */
            if ( Fields.hasField( object, "__init__" ) ) {
                if ( idxBoolean( object, "__init__" ) ) {
                    return;
                }
            }

            Map<String, FieldAccess> fields = Reflection.getAllAccessorFields( object.getClass(), true );
            for ( FieldAccess field : fields.values() ) {

                if ( ( field.injectable() ) ) {
                    handleInjectionOfField( object, field );
                }


            }
            Invoker.invokeMethodWithAnnotationNoReturn( object, "postConstruct" );
        }

    }

    private void handleInjectionOfField( Object object, FieldAccess field ) {

        Object value = null;


        boolean fieldNamed = field.isNamed();
        if ( fieldNamed && field.type() != Supplier.class ) {
            value = this.get( field.type(), field.named() );
        } else if ( fieldNamed && field.type() == Supplier.class ) {
            value = this.getSupplier( field.getComponentClass(), field.named() );
        } else {
            value = this.get( field.type() );
        }

        if ( value == null && field.isNamed() ) {
            value = get( field.named() );
            if ( value != null ) {
                field.type().isAssignableFrom( value.getClass() );
            }
        }

        if ( field.requiresInjection() ) {
            if ( value == null ) {

                debug();
                die( sputs(
                        "Unable to inject into", field.getName(), " of ", field.parent(), "with alias\n",
                        field.named(), "was named", field.isNamed(), "field info",
                        field, "\n"
                ) );
            }
        }

        field.setValue( object, value );
    }

    public void debug() {

        puts (this, "----debug----");

        if (this.parent.get()!=null) {

            puts (this, "delegating to parent----");
            this.parent.get().debug();

        } else {

            displayModuleInfo();
        }
    }

    private void displayModuleInfo() {

        int index = 0;

        for (Module module : modules) {

            if (module instanceof ContextImpl) {
                ContextImpl context = ( ContextImpl ) module;
                context.displayModuleInfo();
            } else {
                puts (index, module);

                puts ("Names:---------------------------");
                for ( String name : module.names() ) {
                    puts ("              ", name);
                }


                puts ("Type--:---------------------------");
                for ( Class<?> cls : module.types() ) {
                    puts ("              ", name);
                }


                puts ("Object--:---------------------------");
                for ( Object value : module.values() ) {
                    puts ("              ", name);
                }
            }

            index++;
        }
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

        if ( object instanceof Map ) {
            Map map = ( Map ) object;
            if ( map.containsKey( "class" ) ) {
                object = MapObjectConversion.fromMap( map );
            }
        }

        resolveProperties( object );

        return object;
    }

    @Override
    public Object invoke( String objectName, String methodName, Object args ) {
        Object object = this.get( objectName );
        return Invoker.invokeFromObject( object, methodName, args );
    }

    @Override
    public Object invokeOverload( String objectName, String methodName, Object args ) {
        Object object = this.get( objectName );
        return Invoker.invokeOverloadedFromObject( object, methodName, args );
    }

    @Override
    public Object invokeFromJson( String objectName, String methodName, String args ) {
        return invoke( objectName, methodName, fromJson(args) );
    }

    @Override
    public Object invokeOverloadFromJson( String objectName, String methodName, String args ) {
        return invokeOverload( objectName, methodName, fromJson(args) );
    }

    @Override
    public Context add( Module module ) {
        module.setParent( this );
        this.modules.add( module );
        return this;
    }

    @Override
    public Context remove( Module module ) {
        module.setParent( null );
        this.modules.remove( module );
        return this;
    }

    @Override
    public Context addFirst( Module module ) {
        module.setParent( this );
        this.modules.addFirst( module );
        return this;
    }


    @Override
    public String toString() {
        return "ContextImpl{" +
                ", name='" + name + '\'' +
                '}';
    }
}
