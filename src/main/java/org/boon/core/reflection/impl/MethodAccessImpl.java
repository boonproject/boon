package org.boon.core.reflection.impl;

import org.boon.core.reflection.AnnotationData;
import org.boon.core.reflection.Annotations;
import org.boon.core.reflection.MethodAccess;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.boon.Exceptions.handle;

/**
 * Created by Richard on 2/17/14.
 */
public class MethodAccessImpl implements MethodAccess {

    final public Method method;
    final List<AnnotationData> annotationData;
    final Map<String, AnnotationData> annotationMap;

    public MethodAccessImpl() {
        method=null;
        annotationData=null;
        annotationMap=null;
    }

    public MethodAccessImpl( Method method ) {
        this.method = method;
        this.method.setAccessible( true );
        this.annotationData = Annotations.getAnnotationDataForMethod(method);

        annotationMap = new ConcurrentHashMap<>(  );
        for (AnnotationData data : annotationData) {
            annotationMap.put( data.getName(), data );
            annotationMap.put( data.getSimpleClassName(), data );
            annotationMap.put( data.getFullClassName(), data );
        }

    }

    public Object invoke(Object object, Object... args) {
        try {
            return method.invoke( object, args );
        } catch ( Exception ex ) {
            return handle( Object.class, ex,  "unable to invoke method", method,
                    " on object ", object, "with arguments", args );

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
        return method.getParameterTypes();
    }

    @Override
    public Type[] getGenericParameterTypes() {
        return method.getGenericParameterTypes();
    }

}
