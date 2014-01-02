package org.boon.json.serializers.impl;

import org.boon.core.reflection.Reflection;
import org.boon.core.reflection.fields.FieldAccess;
import org.boon.json.serializers.FieldsAccessor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by rick on 1/1/14.
 */
public class FieldAccessorsImplFieldThenProp implements FieldsAccessor {

    private final Map <Class<?>, Map<String, FieldAccess>> fieldMap = new ConcurrentHashMap<> ( );



    public final Map<String, FieldAccess> getFields ( Class<? extends Object> aClass ) {
        Map<String, FieldAccess> map = fieldMap.get( aClass );
        if (map == null) {
            map = doGetFields ( aClass );
            fieldMap.put ( aClass, map );
        }
        return map;
    }

    private final Map<String, FieldAccess> doGetFields ( Class<? extends Object> aClass ) {
        return Reflection.getPropertyFieldAccessors ( aClass );
    }




}
