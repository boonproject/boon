package org.boon.core.reflection;

import java.util.Iterator;

/**
 * Created by Richard on 2/20/14.
 */
public interface Annotated {

    public Iterable<AnnotationData> annotationData();

    boolean hasAnnotation(String annotationName) ;
    AnnotationData annotation(String annotationName) ;

}
