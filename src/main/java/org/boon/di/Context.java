package org.boon.di;

public interface Context {
    public <T> T get(Class<T> type);
}