package org.boon.core.reflection;

import java.lang.reflect.Type;
import java.util.Iterator;

/**
 * Created by Richard on 2/20/14.
 */
public interface BaseAccess {


    public Iterator<AnnotationData> annotationData();

    boolean hasAnnotation(String annotationName) ;
    AnnotationData getAnnotation(String annotationName) ;

    Class<?>[] parameterTypes() ;

    Type[] getGenericParameterTypes();
}
