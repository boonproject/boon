package org.boon.di;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Rick Hightower
 *
 * Same effect at @Inject @Required if no argument passed.
 * Same effect as @Inject if @In(required=false) is passed.
 *
 * Same effect as @Inject @Named("bar") @Required if @In(value="bar" )
 */
@Target( {ElementType.METHOD, ElementType.FIELD, ElementType.TYPE, ElementType.PARAMETER} )
@Retention( RetentionPolicy.RUNTIME )
public @interface In {
    boolean required() default true;

    String value() default "";


}
