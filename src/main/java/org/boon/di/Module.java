package org.boon.di;

import org.boon.core.Supplier;

public interface Module {

    public <T> T get( Class<T> type );


    public Object get( String name );


    public <T> T get( Class<T> type, String name );



    public ProviderInfo getProviderInfo( Class<?> type );


    public ProviderInfo getProviderInfo( String name );


    public ProviderInfo getProviderInfo( Class<?> type, String name );



    public boolean has( Class type );

    public boolean has( String name );



    public <T> Supplier<T>  getSupplier( Class<T> type, String name );


    public <T> Supplier<T>  getSupplier( Class<T> type );

    public void parent(Context context);



    public Iterable<Object> values();

    public Iterable<String> names();


    public Iterable<Class<?>> types();

}
