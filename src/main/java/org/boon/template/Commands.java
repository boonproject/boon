package org.boon.template;

import org.boon.core.Conversions;

/**
 * Created by Richard on 2/27/14.
 */
public enum Commands {
    EACH,
    IF,
    WITH,
    UNKNOWN,
    UNLESS,
    LOG, //MISSING
    LENGTH, //like if but checks to see if a list has a certain length
    FUNCTION, //calls a function if return type is not void, outputs to screen, hmmmm....
    INCLUDE //INCLUDE ANOTHER TEMPLATE has a name gets mapped in.. used like function if found in namespace
    ;

    public static Commands command(String value) {
       return Conversions.toEnum(Commands.class, value.toUpperCase(), UNKNOWN);
    }
}
