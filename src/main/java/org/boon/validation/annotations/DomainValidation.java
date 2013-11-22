package org.boon.validation.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.FIELD})
public @interface DomainValidation {
    String detailMessage( ) default "";

    String summaryMessage( ) default "";

    String parentProperty( ) default "";

    String method( ) default "";

    boolean global( ) default false;
}
