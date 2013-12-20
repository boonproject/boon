package org.boon.criteria;


import org.boon.core.reflection.Reflection;
import org.boon.core.reflection.fields.FieldAccess;
import org.boon.predicates.Predicate;

import java.util.Map;

public abstract class Criteria implements Predicate {
    private static ThreadLocal<Map<String, FieldAccess>> fieldsLocal = new ThreadLocal<> ();

    public static void fields ( Map<String, FieldAccess> fields ) {
        fieldsLocal.set ( fields );
    }

<<<<<<< HEAD
    public static void clearFields () {
=======
    public static void clearFields() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        fieldsLocal.set ( null );
    }

    public abstract void prepareForGroupTest ( Map<String, FieldAccess> fields, Object owner );


<<<<<<< HEAD
    public abstract void cleanAfterGroupTest ();
=======
    public abstract void cleanAfterGroupTest();
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc

    public abstract boolean resolve ( Map<String, FieldAccess> fields, Object owner );


    @Override
    public boolean test ( Object o ) {
        Map<String, FieldAccess> fields = getFieldsInternal ( o );
        return resolve ( fields, o );
    }

<<<<<<< HEAD
    protected Map<String, FieldAccess> getFieldsInternal ( Object o ) {
        return getFieldsInternal ( o.getClass () );
    }

    protected Map<String, FieldAccess> getFieldsInternal ( Class clazz ) {
=======
    protected Map<String, FieldAccess> getFieldsInternal( Object o ) {
        return getFieldsInternal ( o.getClass () );
    }

    protected Map<String, FieldAccess> getFieldsInternal( Class clazz ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        Map<String, FieldAccess> fields = fieldsLocal == null ? null : fieldsLocal.get ();
        if ( fields == null ) {
            fields = Reflection.getPropertyFieldAccessMap ( clazz );
        }
        return fields;
    }

}
