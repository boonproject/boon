/*
 * Copyright 2013-2014 Richard M. Hightower
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * __________                              _____          __   .__
 * \______   \ ____   ____   ____   /\    /     \ _____  |  | _|__| ____    ____
 *  |    |  _//  _ \ /  _ \ /    \  \/   /  \ /  \\__  \ |  |/ /  |/    \  / ___\
 *  |    |   (  <_> |  <_> )   |  \ /\  /    Y    \/ __ \|    <|  |   |  \/ /_/  >
 *  |______  /\____/ \____/|___|  / \/  \____|__  (____  /__|_ \__|___|  /\___  /
 *         \/                   \/              \/     \/     \/       \//_____/
 *      ____.                     ___________   _____    ______________.___.
 *     |    |____ ___  _______    \_   _____/  /  _  \  /   _____/\__  |   |
 *     |    \__  \\  \/ /\__  \    |    __)_  /  /_\  \ \_____  \  /   |   |
 * /\__|    |/ __ \\   /  / __ \_  |        \/    |    \/        \ \____   |
 * \________(____  /\_/  (____  / /_______  /\____|__  /_______  / / ______|
 *               \/           \/          \/         \/        \/  \/
 */

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
