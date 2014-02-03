package org.boon.di.modules;

import org.boon.Exceptions;
import org.boon.core.reflection.BeanUtils;
import org.boon.di.Module;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ObjectListModule implements Module {

    private final boolean prototypes;
    Map<Class, Object> objects = new ConcurrentHashMap<>(  );

    public ObjectListModule(boolean prototypes, Object... objects) {

        this.prototypes = prototypes;
        for ( Object object : objects ) {
            Class cls = object.getClass ();
            this.objects.put ( cls, object );
            Class superClass = cls.getSuperclass ();


            Class[] superTypes = cls.getInterfaces();

            for (Class superType : superTypes) {
                this.objects.put ( superType, object );
            }

            while ( superClass != Object.class) {
                this.objects.put ( superClass, object );
                superTypes = cls.getInterfaces();
                for (Class superType : superTypes) {
                    this.objects.put ( superType, object );
                }
                superClass = cls.getSuperclass ();
            }

        }
    }


    @Override
    public <T> T get( Class<T> type ) {
        try {
            if (!prototypes) {
                return (T) objects.get ( type );
            } else {
                return BeanUtils.copy((T)objects.get(type));
            }
        } catch ( Exception e ) {
            Exceptions.handle ( e );
            return null;
        }
    }

    @Override
    public boolean has( Class type ) {
        return objects.containsKey ( type );
    }
}
