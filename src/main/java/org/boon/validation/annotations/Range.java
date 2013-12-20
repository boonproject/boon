package org.boon.validation.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention ( RetentionPolicy.RUNTIME )
@Target ( { ElementType.METHOD, ElementType.TYPE, ElementType.FIELD } )
public @interface Range {
<<<<<<< HEAD
    String min ();

    String max ();

    String detailMessage () default "";

    String summaryMessage () default "";
=======
    String min();

    String max();

    String detailMessage() default "";

    String summaryMessage() default "";
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc

}
