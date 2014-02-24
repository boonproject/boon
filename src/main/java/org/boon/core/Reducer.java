package org.boon.core;


/**
 * Place holder or JDK 1.8 Function
 */
public interface Reducer<IN, SUM> {

    /**
     * Compute the result of applying the function to the input argument
     *
     * @param in the input object
     * @return the function result
     */
    SUM apply( SUM sum, IN in );

}
