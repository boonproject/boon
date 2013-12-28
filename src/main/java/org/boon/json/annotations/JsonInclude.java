package org.boon.json.annotations;

public @interface JsonInclude {

    public static enum Include {
        ALWAYS, NON_DEFAULT, NON_EMPTY, NON_NULL
    }

    Include value() default Include.ALWAYS;
}
