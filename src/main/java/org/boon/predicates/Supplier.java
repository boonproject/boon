package org.boon.predicates;

public interface Supplier<T> {

    /**
     * Returns an object.
     *
     * @return an object
     */
    T get();
}
