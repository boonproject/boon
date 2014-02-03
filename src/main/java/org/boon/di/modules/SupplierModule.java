package org.boon.di.modules;

import org.boon.core.Supplier;
import org.boon.di.Module;
import org.boon.di.SupplierInfo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Richard on 2/3/14.
 */
public class SupplierModule implements Module {

    private Map<Class, Supplier<Object>> supplierMap = new ConcurrentHashMap<>(  );

    public SupplierModule(SupplierInfo...  suppliers) {

            for (SupplierInfo supplierInfo : suppliers) {
                supplierMap.put( supplierInfo.type(), supplierInfo.supplier() );
            }
    }


    @Override
    public <T> T get( Class<T> type ) {
        return (T) supplierMap.get ( type ).get ();
    }

    @Override
    public boolean has( Class type ) {
        return supplierMap.containsKey ( type );
    }
}