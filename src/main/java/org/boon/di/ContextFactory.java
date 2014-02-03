package org.boon.di;

import org.boon.di.impl.ContextImpl;
import org.boon.di.modules.ClassListModule;
import org.boon.di.modules.InstanceModule;
import org.boon.di.modules.ObjectListModule;
import org.boon.di.modules.SupplierModule;

public class ContextFactory {

    public static Context context ( final Module... modules ) {
        return new ContextImpl( modules );
    }

    public static Module classes(Class... classes) {

        return new ClassListModule( classes );
    }

    public static Module objects(Object... objects) {

        return new ObjectListModule( false, objects );
    }


    public static Module prototypes(Object... objects) {

        return new ObjectListModule( true, objects );
    }

    public static Module module( Object module ) {

        return new InstanceModule( module );
    }

    public static Module suppliers( SupplierInfo... suppliers ) {

        return new SupplierModule( suppliers );
    }

}
