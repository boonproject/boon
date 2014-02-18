package org.boon.core.reflection;

import org.boon.core.Value;
import org.boon.core.reflection.fields.FieldsAccessor;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Richard on 2/17/14.
 */
public class Mapper {

    final FieldsAccessor fieldsAccessor;
    final Set<String> ignoreSet;
    final String view;
    final boolean respectIgnore;

    public Mapper( FieldsAccessor fieldsAccessor, Set<String> ignoreSet, String view, boolean respectIgnore ) {
        this.fieldsAccessor = fieldsAccessor;
        this.ignoreSet = ignoreSet;
        this.view = view;
        this.respectIgnore = respectIgnore;
    }

    public  <T> T fromMap( Map<String, Object> map, Class<T> cls ) {
        return MapObjectConversion.fromMap(respectIgnore, view, this.fieldsAccessor, map, cls, ignoreSet);
    }

    public  <T> T fromList( List<?> list, Class<T> cls  ) {
        return MapObjectConversion.fromList( respectIgnore, view, this.fieldsAccessor, list, cls, ignoreSet );
    }



    public  <T> T fromValueMap( final Map<String, Value> map, Class<T> cls  ) {
        return MapObjectConversion.fromValueMap( respectIgnore, view, this.fieldsAccessor,  map, cls, ignoreSet) ;
    }

}