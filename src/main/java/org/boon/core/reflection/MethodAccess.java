package org.boon.core.reflection;

import java.lang.reflect.Type;
import java.util.Iterator;

/**
 * Created by Richard on 2/17/14.
 */
public interface MethodAccess {
    public Object invoke(Object object, Object... args);
    public Iterator<AnnotationData> annotationData();

    boolean hasAnnotation(String annotationName) ;
    AnnotationData getAnnotation(String annotationName) ;

    Class<?>[] parameterTypes() ;

    Type[] getGenericParameterTypes();
}