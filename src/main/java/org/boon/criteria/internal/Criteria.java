package org.boon.criteria.internal;


import org.boon.core.reflection.BeanUtils;
import org.boon.core.reflection.fields.FieldAccess;
import org.boon.predicates.Predicate;

import java.util.Map;

public abstract class Criteria implements Predicate {
    private static ThreadLocal<Map<String, FieldAccess>> fieldsLocal = new ThreadLocal<>();

    public static void fields( Map<String, FieldAccess> fields ) {
        fieldsLocal.set( fields );
    }

    public static void clearFields() {
        fieldsLocal.set( null );
    }

    public abstract void prepareForGroupTest( Map<String, FieldAccess> fields, Object owner );


    public abstract void cleanAfterGroupTest();

    public abstract boolean resolve( Map<String, FieldAccess> fields, Object owner );


    @Override
    public boolean test( Object o ) {
        Map<String, FieldAccess> fields = getFieldsInternal( o );
        return resolve( fields, o );
    }

    protected Map<String, FieldAccess> getFieldsInternal( Object o ) {
        return getFieldsInternal( o.getClass(), o );
    }


    protected Map<String, FieldAccess> getFieldsInternal( Class o ) {
        return getFieldsInternal( o.getClass(), null );
    }

    protected Map<String, FieldAccess> getFieldsInternal( Class clazz, Object o ) {
        Map<String, FieldAccess> fields = fieldsLocal == null ? null : fieldsLocal.get();
        if ( fields == null ) {
            if ( o != null ) {
                fields =  BeanUtils.getFieldsFromObject( o );
            } else {
                fields = BeanUtils.getPropertyFieldAccessMap( clazz );
            }
        }
        return fields;
    }

}
