package org.boon.core;

/**
 * Created by Richard on 2/20/14.
 */
public interface AsyncFunction <IN, OUT> {

    /**
     * Compute the result of applying the function to the input argument
     *
     * @param in the input object
     * @return the function result
     */
    void apply( IN in, Handler<OUT> handler);

}