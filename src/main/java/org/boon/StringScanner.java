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

package org.boon;

import org.boon.core.reflection.FastStringUtils;
import org.boon.primitive.CharScanner;
import org.boon.primitive.Chr;


public class StringScanner {


    public static boolean isDigits( String input ) {
        return CharScanner.isDigits( FastStringUtils.toCharArray( input ) );
    }



    public static String[]  split( final String string,
                                  final char split, final int limit ) {


        char[][] comps = CharScanner.split( FastStringUtils.toCharArray( string ), split, limit );

        return Str.fromCharArrayOfArrayToStringArray( comps );


    }
    public static String[] split( final String string,
                                  final char split ) {

        char[][] comps = CharScanner.split( FastStringUtils.toCharArray( string ), split );

        return Str.fromCharArrayOfArrayToStringArray( comps );

    }

    public static String[] splitByChars( final String string,
                                         final char... delimiters ) {

        char[][] comps = CharScanner.splitByChars( FastStringUtils.toCharArray( string ), delimiters );

        return Str.fromCharArrayOfArrayToStringArray( comps );

    }

    public static String[] splitByDelimiters( final String string,
                                              final String delimiters ) {

        char[][] comps = CharScanner.splitByChars( FastStringUtils.toCharArray( string ), delimiters.toCharArray() );

        return Str.fromCharArrayOfArrayToStringArray( comps );

    }


    public static String[] splitByCharsNoneEmpty( final String string, final char... delimiters ) {

        char[][] comps = CharScanner.splitByCharsNoneEmpty( FastStringUtils.toCharArray( string ), delimiters );
        return Str.fromCharArrayOfArrayToStringArray( comps );
    }

    public static String removeChars( final String string, final char... delimiters ) {
        char[][] comps = CharScanner.splitByCharsNoneEmpty( FastStringUtils.toCharArray( string ), delimiters );
        return new String(Chr.add ( comps ));
    }

    public static String[] splitByCharsNoneEmpty( final String string, int start, int end, final char... delimiters ) {
        Exceptions.requireNonNull( string );

        char[][] comps = CharScanner.splitByCharsNoneEmpty( FastStringUtils.toCharArray( string ), start, end, delimiters );
        return Str.fromCharArrayOfArrayToStringArray( comps );
    }

}
