package org.boon;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

import static org.boon.Boon.sputs;

public class Annotations {

    public static List<AnnotationData> getAnnotationDataForProperty( Class<?> clazz, String propertyName, boolean useReadMethod, Set<String> allowedPackages ) {
        return extractValidationAnnotationData( extractAllAnnotationsForProperty( clazz, propertyName, useReadMethod ), allowedPackages );
    }

    public static List<AnnotationData> getAnnotationDataForField( Class<?> clazz, String propertyName, Set<String> allowedPackages ) {
        return extractValidationAnnotationData( findFieldAnnotations( clazz, propertyName ), allowedPackages );
    }

    public static List<AnnotationData> getAnnotationDataForClass( Class<?> clazz, Set<String> allowedPackages ) {
        return extractValidationAnnotationData( findClassAnnotations( clazz ), allowedPackages );
    }

    private static Annotation[] findClassAnnotations( Class<?> clazz ) {
        return clazz.getAnnotations();
    }

    public static Collection<AnnotationData> getAnnotationDataForFieldAndProperty( Class<?> clazz, String propertyName, Set<String> allowedPackages ) {
        /* Extract the AnnotationData from the Java annotations. */
        List<AnnotationData> propertyAnnotationDataList =
                getAnnotationDataForProperty( clazz, propertyName, false, allowedPackages );

        /* Read the field annotations.  */
        List<AnnotationData> fieldAnnotationDataList =
                getAnnotationDataForField( clazz, propertyName, allowedPackages );

        /* Combine the annotations from field and properties. Field validations take precedence over property validations. */
        Map<String, AnnotationData> map = new HashMap<String, AnnotationData>( propertyAnnotationDataList.size() + fieldAnnotationDataList.size() );

        /* Add the property annotations to the map. */
        for ( AnnotationData annotationData : propertyAnnotationDataList ) {
            map.put( annotationData.getName(), annotationData );
        }

        /* Add the field annotations to the map allowing them to override the property annotations. */
        for ( AnnotationData annotationData : fieldAnnotationDataList ) {
            map.put( annotationData.getName(), annotationData );
        }
        return map.values();
    }


    /**
     * Create an annotation data list.
     *
     * @param annotations list of annotations.
     * @return
     */
    public static List<AnnotationData> extractValidationAnnotationData(
            Annotation[] annotations, Set<String> allowedPackages ) {
        List<AnnotationData> annotationsList = new ArrayList<>();
        for ( Annotation annotation : annotations ) {
            AnnotationData annotationData = new AnnotationData( annotation, allowedPackages );
            if ( annotationData.isAllowed() ) {
                annotationsList.add( annotationData );
            }
        }
        return annotationsList;
    }

    /**
     * Extract all annotations for a given property.
     * Searches current class and if none found searches
     * super class for annotations. We do this because the class
     * could be proxied with AOP.
     *
     * @param clazz        Class containing the property.
     * @param propertyName The name of the property.
     * @return
     */
    private static Annotation[] extractAllAnnotationsForProperty( Class<?> clazz, String propertyName, boolean useRead ) {
        try {

            Annotation[] annotations = findPropertyAnnotations( clazz, propertyName, useRead );

            /* In the land of dynamic proxied AOP classes,
             * this class could be a proxy. This seems like a bug
             * waiting to happen. So far it has worked... */
            if ( annotations.length == 0 ) {
                annotations = findPropertyAnnotations( clazz.getSuperclass(), propertyName, useRead );
            }
            return annotations;
        } catch ( Exception ex ) {
            return Exceptions.handle( Annotation[].class,
                    sputs(
                            "Unable to extract annotations for property",
                            propertyName, " of class ", clazz,
                            "  useRead ", useRead ), ex );
        }

    }

    /**
     * Find annotations given a particular property name and clazz. This figures
     * out the writeMethod for the property, and uses the write method
     * to look up the annotations.
     *
     * @param clazz        The class that holds the property.
     * @param propertyName The name of the property.
     * @return
     * @throws java.beans.IntrospectionException
     *
     */
    private static Annotation[] findPropertyAnnotations( Class<?> clazz, String propertyName, boolean useRead )
            throws IntrospectionException {

        PropertyDescriptor propertyDescriptor = getPropertyDescriptor( clazz, propertyName );
        if ( propertyDescriptor == null ) {
            return new Annotation[]{ };
        }
        Method accessMethod = null;

        if ( useRead ) {
            accessMethod = propertyDescriptor.getReadMethod();
        } else {
            accessMethod = propertyDescriptor.getWriteMethod();
        }

        if ( accessMethod != null ) {
            Annotation[] annotations = accessMethod.getAnnotations();
            return annotations;
        } else {
            return new Annotation[]{ };
        }
    }


    /**
     * This needs refactor and put into Refleciton.
     */
    private static PropertyDescriptor getPropertyDescriptor( final Class<?> type, final String propertyName ) {

        Objects.requireNonNull( type );
        Objects.requireNonNull( propertyName );

        if ( !propertyName.contains( "." ) ) {
            return doGetPropertyDescriptor( type, propertyName );
        } else {
            String[] propertyNames = propertyName.split( "[.]" );
            Class<?> clazz = type;
            PropertyDescriptor propertyDescriptor = null;
            for ( String pName : propertyNames ) {
                propertyDescriptor = doGetPropertyDescriptor( clazz, pName );
                if ( propertyDescriptor == null ) {
                    return null;
                }
                clazz = propertyDescriptor.getPropertyType();
            }
            return propertyDescriptor;
        }
    }


    private static Annotation[] findFieldAnnotations( Class<?> clazz, String propertyName ) {
        Field field = getField( clazz, propertyName );
        if ( field == null ) {
            return new Annotation[]{ };
        }
        Annotation[] annotations = field.getAnnotations();
        return annotations;
    }


    /**
     * This needs to be refactored and put into Reflection or something.
     */
    private static PropertyDescriptor doGetPropertyDescriptor( final Class<?> type, final String propertyName ) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo( type );
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for ( PropertyDescriptor pd : propertyDescriptors ) {
                if ( pd.getName().equals( propertyName ) ) {
                    return pd;
                }
            }
            Class<?> superclass = type.getSuperclass();
            if ( superclass != null ) {
                return doGetPropertyDescriptor( superclass, propertyName );
            }
            return null;

        } catch ( Exception ex ) {
            throw new RuntimeException( "Unable to get property " + propertyName + " for class " + type,
                    ex );
        }
    }


    private static Field getField( final Class<?> type, final String fieldName ) {
        if ( !fieldName.contains( "." ) ) {
            return doFindFieldInHeirarchy( type, fieldName );
        } else {
            String[] fieldNames = fieldName.split( "[.]" );
            Class<?> clazz = type;
            Field field = null;
            for ( String fName : fieldNames ) {
                field = doFindFieldInHeirarchy( clazz, fName );
                if ( field == null ) {
                    return null;
                }
                clazz = field.getType();
            }
            return field;
        }
    }


    private static Field doFindFieldInHeirarchy( Class<?> clazz, String propertyName ) {
        Field field = doGetField( clazz, propertyName );

        Class<?> sclazz = clazz.getSuperclass();
        if ( field == null ) {
            while ( true ) {
                if ( sclazz != null ) {
                    field = doGetField( sclazz, propertyName );
                    sclazz = sclazz.getSuperclass();
                }
                if ( field != null ) {
                    break;
                }
                if ( sclazz == null ) {
                    break;
                }
            }
        }
        return field;
    }

    private static Field doGetField( Class<?> clazz, String fieldName ) {
        Field field = null;
        try {
            field = clazz.getDeclaredField( fieldName );
        } catch ( SecurityException se ) {
            field = null;
        } catch ( NoSuchFieldException nsfe ) {
            field = null;
        }
        if ( field == null ) {
            Field[] fields = clazz.getDeclaredFields();
            for ( Field f : fields ) {
                if ( f.getName().equals( fieldName ) ) {
                    field = f;
                }
            }
        }
        if ( field != null ) {
            field.setAccessible( true );
        }
        return field;
    }


}
