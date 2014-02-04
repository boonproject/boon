package org.boon.di;

public interface Module {

    public <T> T get( Class<T> type );


    public Object get( String name );


    public <T> T get( Class<T> type, String name );

    public boolean has( Class type );

    public boolean has( String name );

}
