package org.boon.handlebars;

import org.boon.core.Conversions;

/**
 * Created by Richard on 2/27/14.
 */
public enum Commands {
    EACH,
    IF,
    WITH,
    UNKNOWN;

    public static Commands command(String value) {
       return Conversions.toEnum(Commands.class, value.toUpperCase(), UNKNOWN);
    }
}
