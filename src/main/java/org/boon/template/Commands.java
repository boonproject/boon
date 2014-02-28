package org.boon.template;

import org.boon.core.Conversions;

/**
 * Created by Richard on 2/27/14.
 */
public enum Commands {
    EACH, //implement index and key // and this might already work @Index and @Key
    IF, //ADD ELSE? IF, WITH and EACH can call functions
    WITH, //implement this
    UNKNOWN,
    UNLESS, //MISSING
    LOG, //MISSING
    EMPTY, //like if but checks if a list is empty
    LENGTH, //like if but checks to see if a list has a certain length
    JSON, //converts java object into json for pages that have JSON content
    FUNC //calls a function if return type is not void, outputs to screen, hmmmm.... 
    ;

    public static Commands command(String value) {
       return Conversions.toEnum(Commands.class, value.toUpperCase(), UNKNOWN);
    }
}
