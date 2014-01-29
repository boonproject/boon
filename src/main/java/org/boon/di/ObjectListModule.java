package org.boon.di;

import org.boon.Exceptions;

import java.util.HashMap;
import java.util.Map;

public class ObjectListModule implements Module {

    Map<Class, Object> objects = new HashMap<> (  );

    public ObjectListModule(Object... objects) {

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
            return (T) objects.get ( type );
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
