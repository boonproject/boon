package org.boon.di.modules;

import org.boon.Maps;
import org.boon.core.reflection.AnnotationData;
import org.boon.core.reflection.Annotations;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * Created by Richard on 2/3/14.
 */
public class NamedUtils {

    public static String namedValueForClass( Class<?> type ) {
        String named = null;
        Map<String, AnnotationData> annotationMap = Annotations.getAnnotationDataForClassAsMap( type );
        if ( annotationMap.containsKey( "named" ) ) {
            named = ( String ) annotationMap.get( "named" ).getValues().get( "value" );
        }
        return named;
    }


    public static String namedValueForMethod( Method method ) {
        String named = null;
        List<AnnotationData> annotations = Annotations.getAnnotationDataForMethod( method );
        Map<String, AnnotationData> annotationMap = Maps.toMap( "name", annotations );
        if ( annotationMap.containsKey( "named" ) ) {
            named = ( String ) annotationMap.get( "named" ).getValues().get( "value" );
        }
        return named;
    }

}
