package org.boon.core.reflection;

import org.boon.Sets;
import org.boon.core.Typ;
import org.boon.core.reflection.fields.FieldAccess;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.boon.Exceptions.die;

/**
 * Created by Richard on 2/17/14.
 */
public class Fields {
    private final static Set<String> fieldSortNames = Sets.safeSet( "name", "orderBy", "title", "key" );
    private final static Set<String> fieldSortNamesSuffixes = Sets.safeSet( "Name", "Title", "Key" );

    private static void setSortableField( Class<?> clazz, String fieldName ) {
        Reflection.context()._sortableFields.put( clazz.getName(), fieldName );
    }

    private static String getSortableField( Class<?> clazz ) {
        return Reflection.context()._sortableFields.get( clazz.getName() );
    }

    /**
     * Checks to see if we have a string field.
     *
     * @param value1
     * @param name
     * @return
     */
    public static boolean hasStringField( final Object value1, final String name ) {

        Class<?> clz = value1.getClass();
        return classHasStringField( clz, name );
    }

    /**
     * Checks to see if this class has a string field.
     *
     * @param clz
     * @param name
     * @return
     */
    public static boolean classHasStringField( Class<?> clz, String name ) {

        List<Field> fields = Reflection.getAllFields( clz );
        for ( Field field : fields ) {
            if (
                    field.getType().equals( Typ.string ) &&
                            field.getName().equals( name ) &&
                            !Modifier.isStatic( field.getModifiers() ) &&
                            field.getDeclaringClass() == clz
                    ) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks to if an instance has a field
     *
     * @param value1
     * @param name
     * @return
     */
    public static boolean hasField( Object value1, String name ) {
        return classHasField( value1.getClass(), name );
    }

    /**
     * Checks to see if a class has a field.
     *
     * @param clz
     * @param name
     * @return
     */
    public static boolean classHasField( Class<?> clz, String name ) {
        List<Field> fields = Reflection.getAllFields( clz );
        for ( Field field : fields ) {
            if ( field.getName().equals( name )
                    && !Modifier.isStatic( field.getModifiers() )
                    && field.getDeclaringClass() == clz ) {
                return true;
            }
        }

        return false;
    }

    /**
     * This can be used for default sort.
     *
     * @param value1 value we are analyzing
     * @return first field that is comparable or primitive.
     */
    public static String getFirstComparableOrPrimitive( Object value1 ) {
        return getFirstComparableOrPrimitiveFromClass( value1.getClass() );
    }

    /**
     * This can be used for default sort.
     *
     * @param clz class we are analyzing
     * @return first field that is comparable or primitive or null if not found.
     */
    public static String getFirstComparableOrPrimitiveFromClass( Class<?> clz ) {
        List<Field> fields = Reflection.getAllFields( clz );
        for ( Field field : fields ) {

            if ( ( field.getType().isPrimitive() || Typ.isComparable( field.getType() )
                    && !Modifier.isStatic( field.getModifiers() )
                    && field.getDeclaringClass() == clz )
                    ) {
                return field.getName();
            }
        }

        return null;
    }

    /**
     * getFirstStringFieldNameEndsWith
     *
     * @param value object we are looking at
     * @param name  name
     * @return field name or null
     */
    public static String getFirstStringFieldNameEndsWith( Object value, String name ) {
        return getFirstStringFieldNameEndsWithFromClass( value.getClass(), name );
    }

    /**
     * getFirstStringFieldNameEndsWithFromClass
     *
     * @param clz  class we are looking at
     * @param name name
     * @return field name or null
     */
    public static String getFirstStringFieldNameEndsWithFromClass( Class<?> clz, String name ) {
        List<Field> fields = Reflection.getAllFields( clz );
        for ( Field field : fields ) {
            if (
                    field.getName().endsWith( name )
                            && field.getType().equals( Typ.string )
                            && !Modifier.isStatic( field.getModifiers() )
                            && field.getDeclaringClass() == clz ) {

                return field.getName();
            }
        }

        return null;
    }

    /**
     * Gets the first sortable fields found.
     *
     * @param value1
     * @return sortable field
     */
    public static String getSortableField( Object value1 ) {
        return getSortableFieldFromClass( value1.getClass() );
    }

    /**
     * Gets the first sortable field.
     *
     * @param clazz the class we are getting the sortable field from.
     * @return sortable field
     */
    public static String getSortableFieldFromClass( Class<?> clazz ) {

        /** See if the fieldName is in the field listStream already.
         * We keep a hashmap cache.
         * */
        String fieldName = getSortableField( clazz );

        /**
         * Not found in cache.
         */
        if ( fieldName == null ) {

            /* See if we have this sortale field and look for string first. */
            for ( String name : fieldSortNames ) {
                if ( classHasStringField( clazz, name ) ) {
                    fieldName = name;
                    break;
                }
            }

            /*
             Now see if we can find one of our predefined suffixes.
             */
            if ( fieldName == null ) {
                for ( String name : fieldSortNamesSuffixes ) {
                    fieldName = getFirstStringFieldNameEndsWithFromClass( clazz, name );
                    if ( fieldName != null ) {
                        break;
                    }
                }
            }

            /**
             * Ok. We still did not find it so give us the first
             * primitive that we can find.
             */
            if ( fieldName == null ) {
                fieldName = getFirstComparableOrPrimitiveFromClass( clazz );
            }

            /* We could not find a sortable field. */
            if ( fieldName == null ) {
                setSortableField( clazz, "NOT FOUND" );
                die( "Could not find a sortable field for type " + clazz );

            }

            /* We found a sortable field. */
            setSortableField( clazz, fieldName );
        }
        return fieldName;

    }

    public static boolean hasField( Class<?> aClass, String name ) {
        Map<String, FieldAccess> fields = Reflection.getAllAccessorFields( aClass );
        return fields.containsKey( name );
    }
}
