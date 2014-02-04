package org.boon.di;

public interface Context {
    public <T> T get( Class<T> type );

    public <T> T get( Class<T> type, String name );

    public Object get( String name );

}