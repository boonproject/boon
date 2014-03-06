package org.boon.di;

import org.boon.Lists;
import org.boon.di.impl.ContextImpl;
import org.boon.di.modules.InstanceModule;
import org.boon.di.modules.SupplierModule;

import java.util.List;
import java.util.Map;

public class DependencyInjection {


    public static Context context( final Module... modules ) {
        return new ContextImpl( modules );
    }

    public static Module classes( Class... classes ) {
        List<ProviderInfo> wrap = Lists.wrap(ProviderInfo.class, classes);
        return new SupplierModule(wrap);
    }



    public static Module objects( Object... objects ) {


        List<ProviderInfo> wrap = (List<ProviderInfo>) Lists.mapBy(objects, ProviderInfo.class, "objectProviderOf");

        return new SupplierModule( wrap );
    }


    public static Module prototypes( Object... objects ) {

        List<ProviderInfo> wrap = (List<ProviderInfo>) Lists.mapBy(objects, ProviderInfo.class, "prototypeProviderOf");

        return new SupplierModule( wrap );
    }

    public static Module module( Object module ) {

        return new InstanceModule( module );
    }

    public static Module suppliers( ProviderInfo... suppliers ) {

        return new SupplierModule( suppliers );
    }

    public static Context fromMap( Map<?, ?> map ) {
        return new ContextImpl(new SupplierModule( map ));
    }
}
