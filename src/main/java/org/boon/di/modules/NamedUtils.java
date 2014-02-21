package org.boon.di.modules;

import org.boon.Maps;
import org.boon.Sets;
import org.boon.Str;
import org.boon.core.reflection.*;

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

        ClassMeta cls = ClassMeta.classMeta(type);


        String named = findNamed(  cls, type );

        return named;
    }



    public static String namedValueForMethod( MethodAccess method ) {

        String named = findNamed(  method, method.returnType() );

        /** If named is null for this method, then check the name of the return class type class. */
        if (named == null) {
            named = namedValueForClass(method.returnType());
        }
        return named;
    }

    private static String findNamed(  Annotated annotated, Class<?> type ) {
        String named = null;

        for (String annotationName : annotationsThatHaveNamed) {
            named = getName(  annotated, annotationName, type );
            if (named != null) {
                break;
            }
        }


        return named;
    }

    private static String getName(  Annotated annotated, String annotationName, Class<?> type ) {
        String named = null;
        if ( annotated.hasAnnotation(annotationName) ) {
            named = ( String ) annotated.annotation( annotationName ).getValues().get( "value" );
            if ( Str.isEmpty( named )) {
                named = uncapitalize( type.getSimpleName() );
            }
        }
        return named;
    }

}
