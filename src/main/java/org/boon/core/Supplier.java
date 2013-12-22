package org.boon.core;

public interface Supplier<T> {

    /**
     * Returns an object.
     *
     * @return an object
     */
    T get();
}
