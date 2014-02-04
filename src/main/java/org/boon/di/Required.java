package org.boon.di;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Rick Hightower
 */
@Target( {ElementType.METHOD, ElementType.FIELD, ElementType.TYPE, ElementType.PARAMETER} )
@Retention( RetentionPolicy.RUNTIME )
public @interface Required {
}
