package org.boon.core.reflection;


import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.boon.Str.uncapitalize;


/**
 * This is a helper class that helps us extract annotation data
 * from the Annotations.
 * <p/>
 * This was pulled over from crank. I have not given in a thorough review yet.
 *
 * @author Rick Hightower
 */
public class AnnotationData {

    /**
     * The name of the classname of the annotation.
     */
    private String annotationClassName;

    /**
     * The simple name of the annotation.
     */
    private String annotationSimpleName;

    /**
     * The package of the annotation.
     */
    private String annotationPackageName;

    private Set<String> allowedAnnotations;

    private String name;

    private Map<String, Object> values;

    public AnnotationData( Annotation annotation ) {
        this( annotation, new HashSet<String>() );
    }

    public AnnotationData( Annotation annotation, Set<String> allowedAnnotations ) {

        this.annotationSimpleName = annotation.annotationType().getSimpleName ();
        this.annotationClassName = annotation.annotationType().getName ();
        this.annotationPackageName = annotationClassName.substring ( 0, annotationClassName.length ()
                - annotationSimpleName.length () - 1 );
        this.allowedAnnotations = allowedAnnotations;
        this.name = uncapitalize( annotationSimpleName );
        this.values = doGetValues(annotation);
    }




    /**
     * Determines if this is an annotation we care about.
     * Checks to see if the package name is in the set.
     */
    public boolean isAllowed() {
        if (allowedAnnotations ==null || allowedAnnotations.size ()==0) return true;
        return allowedAnnotations.contains( annotationPackageName );
    }

    /**
     * Get the name of the annotation by lowercasing the first letter
     * of the simple name, e.g., short name Required becomes required.
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Get the values from the annotation.
     * We use reflection to turn the annotation into a simple HashMap
     * of values.
     *
     * @return
     */
    Map<String, Object> doGetValues(Annotation annotation) {
        /* Holds the value map. */
        Map<String, Object> values = new HashMap<String, Object>();
        /* Get the declared methodMap from the actual annotation. */
        Method[] methods = annotation.annotationType().getDeclaredMethods();

        final Object[] noargs = ( Object[] ) null;

        /* Iterate through declared methodMap and extract values
         * by invoking decalared methodMap if they are no arg methodMap.
         */
        for ( Method method : methods ) {
            /* If it is a no arg method assume it is an annoation value. */
            if ( method.getParameterTypes().length == 0 ) {
                try {
                    /* Get the value. */
                    Object value = method.invoke( annotation, noargs ); 
                    if (value instanceof Enum) {
                        Enum enumVal = (Enum)value;
                        value = enumVal.name ();
                    }
                    values.put( method.getName(), value );
                } catch ( Exception ex ) {
                    throw new RuntimeException( ex );
                }
            }
        }
        return values;
    }

    public Map<String, Object> getValues() {
        return values;
    }

    public String toString() {
        return name;
    }


    public String getFullClassName () {
        return annotationClassName;
    }


    public String getSimpleClassName () {
        return annotationSimpleName;
    }

}
