package org.boon;


import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


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
     * The actual Java annotation.
     */
    private Annotation annotation;

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
        this ( annotation, new HashSet<String> () );
    }

    public AnnotationData( Annotation annotation, Set<String> allowedAnnotations ) {

        this.annotationSimpleName = annotation.annotationType ().getSimpleName ();
        this.annotationClassName = annotation.annotationType ().getName ();
        this.annotationPackageName = annotationClassName.substring ( 0, annotationClassName.length ()
                - annotationSimpleName.length () - 1 );
        this.annotation = annotation;
        this.allowedAnnotations = allowedAnnotations;
        this.name = unCapitalize ( annotationSimpleName );
        values = doGetValues ();
    }


    /* TODO this needs to be in Str or StringScanner, but it is here for now. */
    private static String unCapitalize( String string ) {
        StringBuilder rv = new StringBuilder ();
        if ( string.length () > 0 ) {
            rv.append ( Character.toLowerCase ( string.charAt ( 0 ) ) );
            if ( string.length () > 1 ) {
                rv.append ( string.substring ( 1 ) );
            }
        }
        return rv.toString ();
    }


    /**
     * Determines if this is an annotation we care about.
     * Checks to see if the package name is in the set.
     */
    public boolean isAllowed() {
        return allowedAnnotations.contains ( annotationPackageName );
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
    Map<String, Object> doGetValues() {
        /* Holds the value map. */
        Map<String, Object> values = new HashMap<String, Object> ();
        /* Get the declared methods from the actual annotation. */
        Method[] methods = annotation.annotationType ().getDeclaredMethods ();

        final Object[] noargs = ( Object[] ) null;

        /* Iterate through declared methods and extract values
         * by invoking decalared methods if they are no arg methods.
         */
        for ( Method method : methods ) {
            /* If it is a no arg method assume it is an annoation value. */
            if ( method.getParameterTypes ().length == 0 ) {
                try {
                    /* Get the value. */
                    Object value = method.invoke ( annotation, noargs );
                    values.put ( method.getName (), value );
                } catch ( Exception ex ) {
                    throw new RuntimeException ( ex );
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
}
