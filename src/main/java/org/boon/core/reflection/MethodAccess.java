package org.boon.core.reflection;

import java.lang.invoke.MethodHandle;

/**
 * Created by Richard on 2/17/14.
 */
public interface MethodAccess extends BaseAccess{
    public Object invoke(Object object, Object... args);
    boolean isStatic();

    String name();
    Class<?> declaringType() ;


    Class<?> returnType() ;

    boolean respondsTo(Class<?>... types);

    boolean respondsTo(Object... args);


    Object invokeStatic(Object... args);

    MethodAccess bind(Object instance);

    MethodHandle methodHandle();

    MethodAccess methodAccess();

    Object bound();
}