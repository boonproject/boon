package org.boon.predicates;

/**
 * Place holder or JDK 1.8 Function
 */
public interface Function<T, R> {

    /**
     * Compute the result of applying the function to the input argument
     *
     * @param t the input object
     * @return the function result
     */
    R apply(T t);

}
