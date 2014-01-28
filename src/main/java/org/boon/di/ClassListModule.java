package org.boon.di;

import org.boon.Exceptions;
import org.boon.core.reflection.Reflection;

import java.util.HashMap;
import java.util.Map;

public class ClassListModule implements Module {

    Map<Class, Class> classes = new HashMap<>(  );

    public ClassListModule(Class... classes) {

        for ( Class cls : classes ) {
            this.classes.put ( cls, cls );
            Class superClass = cls.getSuperclass ();


            Class[] superTypes = cls.getInterfaces();

            for (Class superType : superTypes) {
                this.classes.put ( superType, cls );
            }

            while ( superClass != Object.class) {
                this.classes.put ( superClass, cls );
                superTypes = cls.getInterfaces();
                for (Class superType : superTypes) {
                    this.classes.put ( superType, cls );
                }
                superClass = cls.getSuperclass ();
            }

        }
    }


    @Override
    public <T> T get( Class<T> type ) {
        try {
            return (T) Reflection.newInstance (classes.get(type));
        } catch ( Exception e ) {
            Exceptions.handle ( e );
            return null;
        }
    }

    @Override
    public boolean has( Class type ) {
        return classes.containsKey ( type );
    }
}
