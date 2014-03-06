package org.boon.core.reflection.fields;

import org.boon.core.reflection.Reflection;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FieldsAccessorsPropertyThenField implements FieldsAccessor {

    private final Map <Class<?>, Map<String, FieldAccess>> fieldMap = new ConcurrentHashMap<> ( );
    private final boolean useAlias;


    public FieldsAccessorsPropertyThenField (boolean useAlias) {
        this.useAlias = useAlias;
    }

    public final Map<String, FieldAccess> getFields ( Class<? extends Object> aClass ) {
        Map<String, FieldAccess> map = fieldMap.get( aClass );
        if (map == null) {
            map = doGetFields ( aClass );
            fieldMap.put ( aClass, map );
        }
        return map;
    }

    private final Map<String, FieldAccess> doGetFields ( Class<? extends Object> aClass ) {
        Map<String, FieldAccess> fieldAccessMap = Reflection.getPropertyFieldAccessMapPropertyFirst( aClass );

        if ( useAlias ) {
            Map<String, FieldAccess> fieldAccessMap2 = new LinkedHashMap<> ( fieldAccessMap.size () );

            for (FieldAccess fa : fieldAccessMap.values ()) {
                fieldAccessMap2.put ( fa.alias(), fa );
            }
            return fieldAccessMap2;
        } else {
            return fieldAccessMap;
        }

    }


}
