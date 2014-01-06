package org.boon.json.annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target ({ ElementType.METHOD, ElementType.FIELD })
@Retention ( RetentionPolicy.RUNTIME)
public @interface JsonInclude {

    public static enum Include {
        ALWAYS, NON_DEFAULT, NON_EMPTY, NON_NULL
    }

    Include value() default Include.ALWAYS;
}
