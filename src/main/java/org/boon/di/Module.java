package org.boon.di;

import org.boon.core.Supplier;

public interface Module {

    public <T> T get( Class<T> type );


    public Object get( String name );


    public <T> T get( Class<T> type, String name );

    public boolean has( Class type );

    public boolean has( String name );



    public <T> Supplier<T>  getSupplier( Class<T> type, String name );


    public <T> Supplier<T>  getSupplier( Class<T> type );

}
