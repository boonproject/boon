package org.boon.core.reflection.impl;

import org.boon.core.reflection.AnnotationData;
import org.boon.core.reflection.Annotations;
import org.boon.core.reflection.ConstructorAccess;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.boon.Exceptions.handle;

/**
 * Created by Richard on 2/20/14.
 * @author Rick Hightower
 */
public class ConstructorAccessImpl <T> implements ConstructorAccess {


    final Constructor<T> constructor;
    final List<AnnotationData> annotationData;
    final Map<String, AnnotationData> annotationMap;

    ConstructorAccessImpl() {
        constructor =null;
        annotationData=null;
        annotationMap=null;
    }

    public ConstructorAccessImpl( Constructor<T> method ) {
        this.constructor = method;
        this.constructor.setAccessible(true);
        this.annotationData = Annotations.getAnnotationDataForMethod(method);

        annotationMap = new ConcurrentHashMap<>(  );
        for (AnnotationData data : annotationData) {
            annotationMap.put( data.getName(), data );
            annotationMap.put( data.getSimpleClassName(), data );
            annotationMap.put( data.getFullClassName(), data );
        }

    }


    @Override
    public Iterator<AnnotationData> annotationData() {
        return annotationData.iterator();
    }

    @Override
    public boolean hasAnnotation( String annotationName ) {
        return this.annotationMap.containsKey( annotationName );
    }

    @Override
    public AnnotationData getAnnotation( String annotationName ) {
        return this.annotationMap.get(annotationName);
    }

    @Override
    public Class<?>[] parameterTypes() {
        return constructor.getParameterTypes();
    }

    @Override
    public Type[] getGenericParameterTypes() {
        return constructor.getGenericParameterTypes();
    }

    @Override
    public T create(Object... args) {
        try {
            return constructor.newInstance( args );
        } catch ( Exception ex ) {
            return handle(constructor.getDeclaringClass(), ex, "unable to invoke constructor", constructor,
                    " on object ", constructor.getDeclaringClass(), "with arguments", args);

        }

    }
}
