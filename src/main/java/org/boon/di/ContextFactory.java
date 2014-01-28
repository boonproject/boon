package org.boon.di;

public class ContextFactory {

    public static Context context ( final Module... modules ) {
        return new ContextImpl ( modules );
    }

    public static Module classes(Class... classes) {

        return new ClassListModule ( classes );
    }


    public static Module module( Object module ) {

        return new InstanceModule ( module );
    }

}
