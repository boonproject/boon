package org.boon.di;

import org.boon.core.Supplier;

/**
 * Created by Richard on 2/3/14.
 */
public class ProviderInfo<T> {

    private String name;
    private Class<T> type;
    private Supplier<T> supplier;
    private T object;


    public ProviderInfo( String name, Class<T> type, Supplier<T> supplier, T object ) {
        this.name = name;
        this.type = type;
        this.supplier = supplier;
        this.object = object;
    }

    public static <T> ProviderInfo<T> providerOf( Class<T> type, Supplier<T> supplier ) {
        return new ProviderInfo<>( null, type, supplier, null );
    }


    public static <T> ProviderInfo<T> providerOf( String name, Class<T> type, Supplier<T> supplier ) {
        return new ProviderInfo<>( name, type, supplier, null );
    }


    public static <T> ProviderInfo<T> providerOf( String name, Supplier<T> supplier ) {
        return new ProviderInfo<>( name, null, supplier, null );
    }


    public static <T> ProviderInfo<T> providerOf( String name, Class<T> type ) {
        return new ProviderInfo<>( name, type, null, null );
    }


    public static <T> ProviderInfo<T> providerOf( Class<T> type ) {
        return new ProviderInfo<>( null, type, null, null );
    }


    public static <T> ProviderInfo<T> providerOf( String name, T object ) {
        return new ProviderInfo<>( name, null, null, object );
    }


    public static <T> ProviderInfo<T> providerOf( T object ) {
        return new ProviderInfo<>( null, null, null, object );
    }


    public static <T> ProviderInfo<T> provider( Object name, Object value ) {

        ProviderInfo info;

        if (value instanceof ProviderInfo ) {
            ProviderInfo valueInfo = ( ProviderInfo ) value;
            info  = new ProviderInfo( name.toString(), valueInfo.type(), valueInfo.supplier(), valueInfo.value()  );
        } else if (value instanceof Class) {
            info = new ProviderInfo( name.toString(), (Class) value, null, null  );
        } else if (value instanceof Supplier) {
            info = new ProviderInfo( name.toString(), null, (Supplier) value, null  );
        } else  {
            if (value == null) {
                info = new ProviderInfo( name.toString(), null, null, value  );
            }  else {
                info = new ProviderInfo( name.toString(), value.getClass(), null, value  );
            }
        }
        return info;
    }

    public Class<T> type() {
        return type;
    }

    public Supplier<T> supplier() {
        return supplier;
    }

    public String name() {
        return name;
    }

    public Object value() {
        return object;
    }

}
