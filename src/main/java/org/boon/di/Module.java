package org.boon.di;

public interface Module {

    public <T> T get(Class<T> type);

    public boolean has(Class type);

}
