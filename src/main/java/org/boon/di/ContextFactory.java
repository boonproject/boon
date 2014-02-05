package org.boon.di;

import org.boon.di.impl.ContextImpl;
import org.boon.di.modules.ClassListModule;
import org.boon.di.modules.InstanceModule;
import org.boon.di.modules.ObjectListModule;
import org.boon.di.modules.SupplierModule;

import java.util.Map;

public class ContextFactory {


    public static Context context( final Module... modules ) {
        return new ContextImpl( modules );
    }

    public static Module classes( Class... classes ) {

        return new ClassListModule( classes );
    }


    public static Module classes( ProviderInfo... classes ) {

        return new ClassListModule( classes );
    }

    public static Module objects( Object... objects ) {

        return new ObjectListModule( false, objects );
    }


    public static Module prototypes( Object... objects ) {

        return new ObjectListModule( true, objects );
    }

    public static Module module( Object module ) {

        return new InstanceModule( module );
    }

    public static Module suppliers( ProviderInfo... suppliers ) {

        return new SupplierModule( suppliers );
    }

    public static Module objects( ProviderInfo... suppliers ) {

        return new ObjectListModule( false, suppliers );
    }


    public static Module prototypes( ProviderInfo... suppliers ) {

        return new ObjectListModule( true, suppliers );
    }

    public static Context fromMap( Map<?, ?> map ) {
        return new ContextImpl(new SupplierModule( map ));
    }
}
