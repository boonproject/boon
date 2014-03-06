package org.boon.di.impl;

import org.boon.Boon;
import org.boon.Logger;
import org.boon.collections.ConcurrentLinkedHashSet;
import org.boon.core.Supplier;
import org.boon.core.Typ;
import org.boon.core.reflection.Fields;
import org.boon.core.reflection.Invoker;
import org.boon.core.reflection.MapObjectConversion;
import org.boon.core.reflection.Reflection;
import org.boon.core.reflection.fields.FieldAccess;
import org.boon.di.Context;
import org.boon.di.Module;
import org.boon.di.ProviderInfo;
import org.boon.logging.LogLevel;
import org.boon.logging.TerminalLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.boon.Boon.*;
import static org.boon.Exceptions.die;
import static org.boon.core.reflection.BeanUtils.idxBoolean;
import static org.boon.json.JsonFactory.fromJson;

public class ContextImpl implements Context, Module {

    protected ConcurrentLinkedHashSet<Module> modules = new ConcurrentLinkedHashSet<>();
    private String name;
    private AtomicReference <Context> parent = new AtomicReference<>(  );

    private Class<ContextImpl> contextImpl = ContextImpl.class;
    private Logger logger = configurableLogger(contextImpl);

    private boolean debug;

    public ContextImpl () {

        if ( Boon.debugOn() ) {
            logger.level(LogLevel.DEBUG);
            logger.tee(new TerminalLogger());
        }

        if ( logger.debugOn() ) {
            debug = true;
        }



    }

    @Override
    public void parent(Context context) {
        if ( debug ) logger.debug(contextImpl, "parent");
        this.parent.set( context );
    }

    @Override
    public Iterable<Object> values() {


        if ( debug ) logger.debug(contextImpl, "values()", "IN");

        List list = new ArrayList();
        for ( Module m : modules ) {

            for (Object o : m.values()) {
                list.add( o );
            }
        }


        if ( debug ) logger.debug(contextImpl, "values()", "OUT", list);
        return list;
    }

    @Override
    public Iterable<String> names() {
        if ( debug ) logger.debug(contextImpl, "names()", "IN");


        List list = new ArrayList();
        for ( Module m : modules ) {

            for (String n : m.names()) {
                list.add( n );
            }
        }

        if ( debug ) logger.debug(contextImpl, "names()", "OUT", list);
        return list;
    }

    @Override
    public Iterable<Class<?>> types() {

        if ( debug ) logger.debug(contextImpl, "types()", "IN");
        List list = new ArrayList();
        for ( Module m : modules ) {

            for (Class<?> c : m.types()) {
                list.add( c );
            }
        }

        if ( debug ) logger.debug(contextImpl, "types()", "OUT", list);
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
            module.parent(this);
            this.modules.add( module );
        }
    }

    @Override
    public <T> T get( Class<T> type ) {


        if ( debug ) logger.debug(contextImpl, "get(type)", "IN", type);

        try {

            Object object = null;
            for ( Module module : modules ) {

                if ( module.has( type ) ) {
                    object = module.get( type );
                    break;
                }
            }

            resolveProperties( true, object, getProviderInfo(type) );


            if ( debug ) logger.debug(contextImpl, "get(type)", "OUT", object);
            return ( T ) object;
        } finally {
        }
    }




    @Override
    public <T> T get( Class<T> type, String name ) {

        if ( debug ) logger.debug(contextImpl, "get(type, name)", "IN", type, name);

        try {


            T object = null;
            for ( Module module : modules ) {

                if ( module.has( name ) ) {
                    object = module.get( type, name );
                    break;
                }
            }

            resolveProperties( true, object, getProviderInfo(type, name) );


            if ( debug ) logger.debug(contextImpl, "get(type, name)", "IN", type, name, "OUT", object);

            return object;

        } finally {
        }
    }

    @Override
    public ProviderInfo getProviderInfo(Class<?> type) {

        if ( debug ) logger.debug(contextImpl, "getProviderInfo(type)", "IN", type);

        ProviderInfo pi = null;
        for ( Module module : modules ) {

            if ( module.has( type ) ) {
                pi = module.getProviderInfo(type);
                break;
            }
        }


        if ( debug ) logger.debug(contextImpl, "getProviderInfo(type)", "IN", type, "OUT", pi);
        return pi;

    }

    @Override
    public ProviderInfo getProviderInfo(String name) {


        if ( debug ) logger.debug(contextImpl, "getProviderInfo(name)", "IN", name);

        ProviderInfo pi = null;
        for ( Module module : modules ) {

            if ( module.has( name ) ) {
                pi = module.getProviderInfo( name );
                break;
            }
        }


        if ( debug ) logger.debug(contextImpl, "getProviderInfo(name)", "IN", name, "OUT", pi);
        return pi;

    }

    @Override
    public ProviderInfo getProviderInfo(Class<?> type, String name) {


        if ( debug ) logger.debug(contextImpl, "getProviderInfo( type, name )", "IN", type, name);

        ProviderInfo pi = null;
        for ( Module module : modules ) {

            if ( module.has( name ) ) {
                pi = module.getProviderInfo( type, name );
                break;
            }
        }


        if ( debug ) logger.debug(contextImpl, "getProviderInfo( type, name )", "IN", type, name, "OUT", pi);
        return pi;

    }

    @Override
    public boolean has( Class type ) {


        if ( debug ) logger.debug(contextImpl, "has( type )", "IN", type );



        if ( debug ) logger.debug(contextImpl, "has( type )", "IN", type );

        for ( Module module : modules ) {

            if ( module.has( type ) ) {

                if ( debug ) logger.debug(contextImpl, "has( type )", "IN", type, "OUT", true );
                return true;
            }
        }


        if ( debug ) logger.debug(contextImpl, "has( type )", "IN", type, "OUT", false );
        return false;
    }

    @Override
    public boolean has( String name ) {

        for ( Module module : modules ) {

            if ( module.has( name ) ) {

                if ( debug ) logger.debug(contextImpl, "has( name )", "IN", name, "OUT", true );
                return true;
            }
        }


        if ( debug ) logger.debug(contextImpl, "has( name )", "IN", name, "OUT", false );
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





    private void resolveProperties( boolean enforce, Object object, ProviderInfo info ) {

        if ( debug ) logger.debug(contextImpl, "resolveProperties(enforce, object, info )", "IN",
                enforce, object, info );


        if ( object != null ) {

            /* Since there is no concept of singleton or scope, you need some sort of flag to determine
            if injection has already happened for objects that are like singletons.
             */
            if ( Fields.hasField( object, "__init__" ) ) {
                if ( idxBoolean( object, "__init__" ) ) {


                    if ( debug ) logger.debug(contextImpl, "Object was initialized already" );

                    return;
                }
            }

            if (info != null && info.isPostConstructCalled() && info.value()!=null && !info.prototype()) {
                return;
            }

            Map<String, FieldAccess> fields = Reflection.getAllAccessorFields( object.getClass(), true );
            for ( FieldAccess field : fields.values() ) {

                if ( ( field.injectable() ) ) {

                    handleInjectionOfField(enforce, object, field );
                }


            }


            if ( debug ) logger.debug(contextImpl, "Invoking post construct start...", object );
            Invoker.invokeMethodWithAnnotationNoReturn( object, "postConstruct" );
            if ( debug ) logger.debug(contextImpl, "Invoking post construct done...", object );

            if (info!=null && info.value() != null && !info.prototype()) {

                if ( debug ) logger.debug(contextImpl, "Setting post construct property on provider info...",
                        object );
                info.setPostConstructCalled(true);
            }
        }

        if ( debug ) logger.debug(contextImpl, "resolveProperties(enforce, object, info )", "OUT",
                enforce, object, info );


    }

     public void resolveProperties( Object object ) {

         resolveProperties(true, object, null);

    }


    public void resolvePropertiesIgnoreRequired( Object object ) {

        resolveProperties(false, object, null);

    }

    private void handleInjectionOfField( boolean enforce, Object object, FieldAccess field ) {

        if ( debug ) logger.debug(contextImpl, "handleInjectionOfField(enforce, object, field )", "IN",
                enforce, object, field );


        Object value = null;

        if (field.type().isPrimitive() || Typ.isBasicType(field.type())) {
           handleInjectionOfBasicField(enforce, object, field);
           return;
        }


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

        if ( enforce && field.requiresInjection()  ) {
            if ( value == null ) {

                debug();
                die( sputs(
                        "Unable to inject into", field.name(), " of ", field.parent(), "with alias\n",
                        field.named(), "was named", field.isNamed(), "field info",
                        field, "\n"
                ) );
            }
        }

        if ( debug ) logger.debug(contextImpl, "handleInjectionOfField(enforce, object, field )", "IN",
                enforce, object, field, "\n", "FIELD INJECTION", "into", object, field.name(), "with value", value,
                "VALUE TYPE", className(value), field.type());

        field.setValue( object, value );
    }

    private void handleInjectionOfBasicField(boolean enforce, Object object, FieldAccess field) {

        if ( debug ) logger.debug(contextImpl, "handleInjectionOfBasicField(enforce, object, field )", "IN",
                enforce, object, field );

        String name = null;
        if (field.isNamed()) {
            if ( debug ) logger.debug(contextImpl, "handleInjectionOfBasicField", "FIELD IS NAMED");

            name = field.alias();

            if ( debug ) logger.debug(contextImpl, "handleInjectionOfBasicField", "FIELD IS NAMED", "name", name);
        }

        if (name == null) {

            name = field.name();
            if ( debug ) logger.debug(contextImpl, "handleInjectionOfBasicField", "USING FIELD NAME AS NAME", "name", name);

        }

        Object value = this.get(name);

        if (value == null) {
            if ( debug ) logger.debug(contextImpl, "handleInjectionOfBasicField", "NAME NOT FOUND IN CONTEXT", "name", name);

            name = Boon.add(field.declaringParent().getName(), ".", field.alias());
        }

        value = this.get(name);

        if (value == null) {
            if ( debug ) logger.debug(contextImpl, "handleInjectionOfBasicField", "NAME NOT FOUND IN CONTEXT", "name", name);

            name = Boon.add(field.declaringParent().getPackage().getName(), ".", field.alias());
        }


        value = this.get(name);

        if (debug && value == null) {
            logger.debug(contextImpl, "handleInjectionOfBasicField", "NAME NOT FOUND IN CONTEXT", "name", name);
        }

        if (enforce && value == null && field.requiresInjection()) {
            die ("Basic field", field.name(), "needs injection for class", field.declaringParent());
        }


        if ( debug ) logger.debug(contextImpl, "handleInjectionOfBasicField(enforce, object, field )", "IN",
                enforce, object, field, "\n", "FIELD INJECTION", "into", object, field.name(), "with value", value,
                "VALUE TYPE", className(value), field.type());



       field.setValue(object, value);


        if ( debug ) logger.debug(contextImpl, "handleInjectionOfBasicField(enforce, object, field )", "OUT",
                enforce, object, field );


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

        resolveProperties( true, object, getProviderInfo(name) );

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
        module.parent(this);
        this.modules.add( module );
        return this;
    }

    @Override
    public Context remove( Module module ) {
        module.parent(null);
        this.modules.remove( module );
        return this;
    }

    @Override
    public Context addFirst( Module module ) {
        module.parent(this);
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
