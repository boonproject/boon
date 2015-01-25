package org.boon.slumberdb.service.server;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Richard on 9/15/14.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface ServiceMethod {

    String alias() default "";
}
