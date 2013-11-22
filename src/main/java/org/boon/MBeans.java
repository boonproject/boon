package org.boon;

import javax.management.*;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Array;
import java.util.*;


/**
 * Utility methods to convert MBeans to a Map.
 */
public class MBeans {

    public static Map<String, Object> map( final MBeanServer server,
                                           final ObjectName name ) {


        Objects.requireNonNull ( server, "server cannot be null" );
        Objects.requireNonNull ( name, "name cannot be null" );


            /* Return the bean attributes converted to a map. */
        Map<String, Object> result;
        MBeanInfo info = null;

        try {


            info = server.getMBeanInfo ( name );

            final String[] attributeNames = getAttributeNames ( info );
            result = new HashMap<> ( attributeNames.length );


            final AttributeList attributeList = server.getAttributes ( name, attributeNames );


            for ( Object obj : attributeList ) {
                final Attribute attribute = ( Attribute ) obj;
                result.put ( attribute.getName ( ), convertValue ( attribute.getValue ( ) ) );
            }

            return result;

        } catch ( Exception ex ) {

            return Exceptions.handle ( Map.class, String.format (
                    "Unable to turn mbean into map %s ", name.getCanonicalName ( )
            ), ex );
        }

    }

    public static String[] getAttributeNames( MBeanInfo info ) {
        final MBeanAttributeInfo[] attributes = info.getAttributes ( );
        final String[] attributeNames = new String[attributes.length];

        for ( int index = 0; index < attributes.length; index++ ) {

            attributeNames[index] = attributes[index].getName ( );
        }
        return attributeNames;
    }

    private static Object convertValue( Object value ) {


            /* convert nulls */
        if ( value == null ) {
            value = "null";
        }

            /* convert an array to a List and convert the component objects of the array.
            */
        if ( value.getClass ( ).isArray ( ) ) {

            value = convertFromArrayToList ( value );

        } else if ( value instanceof CompositeData ) {

            value = convertFromCompositeDataToToMap ( value );

        } else if ( value instanceof TabularData ) {
            value = convertFromTabularDataToMap ( value );
        }

        return value;
    }

    private static Object convertFromTabularDataToMap( Object value ) {
        final TabularData data = ( TabularData ) value;

        final Set<List<?>> keys = ( Set<List<?>> ) data.keySet ( );

        final Map<String, Object> map = new HashMap<> ( );
        for ( final List<?> key : keys ) {
            final Object subValue = convertValue ( data.get ( key.toArray ( ) ) );

            if ( key.size ( ) == 1 ) {
                map.put ( convertValue ( key.get ( 0 ) ).toString ( ), subValue );
            } else {
                map.put ( convertValue ( key ).toString ( ), subValue );
            }
        }

        value = map;
        return value;
    }

    private static Object convertFromCompositeDataToToMap( Object value ) {
        final CompositeData data = ( CompositeData ) value;
        final Map<String, Object> map = new HashMap<String, Object> ( );
        final Set<String> keySet = data.getCompositeType ( ).keySet ( );

        for ( final String key : keySet ) {
            map.put ( key, convertValue ( data.get ( key ) ) );
        }

        value = map;
        return value;
    }

    private static Object convertFromArrayToList( Object value ) {
        final List<Object> list = new ArrayList<Object> ( );

        final int length = Array.getLength ( value );

        for ( int index = 0; index < length; index++ ) {
            list.add ( convertValue ( Array.get ( value, index ) ) );
        }

        value = list;
        return value;
    }


    public static DynamicMBean createMBean( final Object instance, final Class<?> managedInterface ) {

        Objects.requireNonNull ( instance, "instance cannot be null" );
        Objects.requireNonNull ( managedInterface, "managedInterface cannot be null" );


        try {

                /* Create the bean. */
            return new StandardMBean ( instance, ( Class ) managedInterface );

        } catch ( final NotCompliantMBeanException ex ) {
            return Exceptions.handle ( DynamicMBean.class, String.format (
                    "createMBean unable to register %s under interface %s",
                    instance.getClass ( ).getName ( ), managedInterface.getClass ( ).getName ( )
            ), ex );

        }
    }

    public static void registerMBean( final String prefix, final String name, final Object mbean ) {

        Objects.requireNonNull ( prefix, "prefix can't be null" );
        Objects.requireNonNull ( name, "name can't be null" );
        Objects.requireNonNull ( mbean, "mbean can't be null" );

        String nameOfBean = nameOfBean = String.format ( "%s.%s:type=%s",
                prefix, mbean.getClass ( ).getSimpleName ( ),
                name );

        try {


            final ObjectName objectName = new ObjectName ( nameOfBean );

            final MBeanServer beanServer = ManagementFactory.getPlatformMBeanServer ( );

            beanServer.registerMBean ( mbean, objectName );

        } catch ( final Exception ex ) {
            Exceptions.handle ( String.format (
                    "registerMBean %s %s %s %s", prefix, name, mbean, nameOfBean
            ), ex );

        }
    }

}
