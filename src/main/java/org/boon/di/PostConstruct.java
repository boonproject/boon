package org.boon.di;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Rick Hightower
 */
@Target( {ElementType.METHOD } )
@Retention( RetentionPolicy.RUNTIME )
public @interface PostConstruct {
}
