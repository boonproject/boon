package org.boon.di.modules;

import org.boon.Maps;
import org.boon.Sets;
import org.boon.Str;
import org.boon.core.reflection.AnnotationData;
import org.boon.core.reflection.Annotations;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import static org.boon.Str.uncapitalize;

/**
 * Created by Richard on 2/3/14.
 */
public class NamedUtils {

    private static Set<String> annotationsThatHaveNamed = Sets.set( "jsonProperty", "serializedName", "named", "id", "in", "qualifier" );


    public static String namedValueForClass( Class<?> type ) {
        String named = null;
        Map<String, AnnotationData> annotationMap = Annotations.getAnnotationDataForClassAsMap( type );

        named = findNamed(  annotationMap, type );

        return named;
    }



    public static String namedValueForMethod( Method method ) {
        String named = null;

        Map<String, AnnotationData> annotationMap = Maps.toMap( "name", Annotations.getAnnotationDataForMethod( method ));
        named = findNamed(  annotationMap, method.getReturnType() );

        /** If named is null for this method, then check the name of the return class type class. */
        if (named == null) {
            named = namedValueForClass(method.getReturnType());
        }
        return named;
    }

    private static String findNamed(  Map<String, AnnotationData> annotationMap, Class<?> type ) {
        String named = null;

        for (String annotationName : annotationsThatHaveNamed) {
            named = getName(  annotationMap, annotationName, type );
            if (named != null) {
                break;
            }
        }


        return named;
    }

    private static String getName(  Map<String, AnnotationData> annotationMap, String annotationName, Class<?> type ) {
        String named = null;
        if ( annotationMap.containsKey( annotationName ) ) {
            named = ( String ) annotationMap.get( annotationName ).getValues().get( "value" );
            if ( Str.isEmpty( named )) {
                named = uncapitalize( type.getSimpleName() );
            }
        }
        return named;
    }

}
