package org.boon.core.reflection;

/**
 * Created by Richard on 2/20/14.
 */
public interface ConstructorAccess<T> extends BaseAccess {



    public T create(Object... args);
}
