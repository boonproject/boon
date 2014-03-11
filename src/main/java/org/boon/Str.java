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
import org.boon.primitive.CharBuf;
import org.boon.primitive.CharScanner;
import org.boon.primitive.Chr;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class Str {


    public final static String EMPTY_STRING = "";



    @Universal
    public static int lengthOf( String str ) {
        return len(str);
    }


    @Universal
    public static String sliceOf( String str, int start ) {
        return slc(str, start);
    }


    @Universal
    public static String sliceOf( String str, int start, int end ) {
        return slc(str, start, end);
    }


    @Universal
    public static String endSliceOf( String str, int end ) {
        return slcEnd(str, end);
    }


    @Universal
    public static char atIndex( String str, int index ) {
        return idx(str, index);
    }

    @Universal
    public static String atIndex( String str, int index, char c ) {
            return idx (str, index, c);
    }


    @Universal
    public static int len( String str ) {
        return str.length();
    }



    @Universal
    public static String slc( String str, int start ) {

        return FastStringUtils.noCopyStringFromChars( Chr.slc( FastStringUtils.toCharArray(str), start ) );
    }


    @Universal
    public static String slc( String str, int start, int end ) {
        return FastStringUtils.noCopyStringFromChars(Chr.slc(FastStringUtils.toCharArray(str), start, end));
    }


    @Universal
    public static String slcEnd( String str, int end ) {
        return FastStringUtils.noCopyStringFromChars( Chr.slcEnd( FastStringUtils.toCharArray(str), end ) );
    }



    @Universal
    public static char idx( String str, int index ) {
        int i = calculateIndex( str.length(), index );

        char c = str.charAt( i );
        return c;
    }


    @Universal
    public static String idx( String str, int index, char c ) {

        char[] chars = str.toCharArray();
        Chr.idx( chars, index, c );
        return new String( chars );
    }

    @Universal
    public static boolean in( char[] chars, String str ) {
        return Chr.in ( chars, FastStringUtils.toCharArray(str) );
    }


    @Universal
    public static boolean in( char c, String str ) {
        return Chr.in ( c, FastStringUtils.toCharArray(str) );
    }


    @Universal
    public static boolean in( char c, int offset, String str ) {
        return Chr.in ( c, offset, FastStringUtils.toCharArray(str) );
    }


    @Universal
    public static boolean in( char c, int offset, int end, String str ) {
        return Chr.in ( c, offset, end, FastStringUtils.toCharArray(str) );
    }


    @Universal
    public static String add( String str, char c ) {
        return FastStringUtils.noCopyStringFromChars( Chr.add( FastStringUtils.toCharArray(str), c ) );
    }


    @Universal
    public static String add( String str, String str2 ) {
        return FastStringUtils.noCopyStringFromChars(
                Chr.add(
                 FastStringUtils.toCharArray(str),
                 FastStringUtils.toCharArray(str2) )
        );
    }

    @Universal
    public static String add( String... strings ) {
        int length = 0;
        for ( String str : strings ) {
            if ( str == null ) {
                continue;
            }
            length += str.length();
        }
        CharBuf builder = CharBuf.createExact( length );
        for ( String str : strings ) {
            if ( str == null ) {
                continue;
            }
            builder.add( str );
        }
        return builder.toString();
    }

    public static String compact( String str ) {
        return FastStringUtils.noCopyStringFromChars( Chr.compact( FastStringUtils.toCharArray(str) ) );
    }


    private static int calculateIndex( final int length, int originalIndex ) {


        int index = originalIndex;

        /* Adjust for reading from the right as in
        -1 reads the 4th element if the length is 5
         */
        if ( index < 0 ) {
            index = length + index;
        }

        /* Bounds check
            if it is still less than 0, then they
            have an negative index that is greater than length
         */
         /* Bounds check
            if it is still less than 0, then they
            have an negative index that is greater than length
         */
        if ( index < 0 ) {
            index = 0;
        }
        if ( index >= length ) {
            index = length - 1;
        }
        return index;
    }


    public static String[] split( String str ) {
        char[][] split = Chr.split( FastStringUtils.toCharArray(str) );
        return fromCharArrayOfArrayToStringArray( split );
    }

    public static String[] splitLines( String str ) {
        char[][] split = Chr.splitLines( FastStringUtils.toCharArray(str) );
        return fromCharArrayOfArrayToStringArray( split );
    }


    public static String[] splitComma( String str ) {
        char[][] split = Chr.splitComma( FastStringUtils.toCharArray(str) );
        return fromCharArrayOfArrayToStringArray( split );
    }


    public static String[] splitBySpace( String str ) {
        char[][] split = CharScanner.splitBySpace( FastStringUtils.toCharArray(str) );
        return fromCharArrayOfArrayToStringArray( split );
    }

    public static String[] splitByPipe( String str ) {
        char[][] split = CharScanner.splitByPipe( FastStringUtils.toCharArray(str) );
        return fromCharArrayOfArrayToStringArray( split );
    }

    public static String[] fromCharArrayOfArrayToStringArray( char[][] split ) {
        String[] results = new String[ split.length ];

        char[] array;

        for ( int index = 0; index < split.length; index++ ) {
            array = split[ index ];

            results[ index ] = array.length == 0 ?
                    EMPTY_STRING : FastStringUtils.noCopyStringFromChars( array );
        }
        return results;
    }


    public static String upper( String str ) {
        return str.toUpperCase();
    }

    public static String lower( String str ) {
        return str.toLowerCase();
    }


    public static String camelCaseUpper( String in ) {
        return camelCase( in, true );
    }


    public static String camelCaseLower( String in ) {
        return camelCase( in, false );
    }

    public static String camelCase( String in ) {
        return camelCase( in, false );
    }

    public static String camelCase( String inStr, boolean upper ) {
        char[] in = FastStringUtils.toCharArray(inStr);
        char[] out = Chr.camelCase( in, upper );
        return FastStringUtils.noCopyStringFromChars( out );
    }


    public static boolean insideOf(String start, String inStr, String end) {
        return Chr.insideOf(FastStringUtils.toCharArray(start), FastStringUtils.toCharArray(inStr), FastStringUtils.toCharArray(end));
    }

    public static String underBarCase( String inStr ) {
        char[] in = FastStringUtils.toCharArray(inStr);
        char[] out = Chr.underBarCase( in );
        return FastStringUtils.noCopyStringFromChars( out );
    }


    public static void equalsOrDie(CharSequence a, CharSequence b) {
        char[] ac = FastStringUtils.toCharArray(a);
        char[] bc = FastStringUtils.toCharArray(b);
        Chr.equalsOrDie(ac, bc);
    }

    public static void equalsOrDie(String a, String b) {
        char[] ac = FastStringUtils.toCharArray(a);
        char[] bc = FastStringUtils.toCharArray(b);
        Chr.equalsOrDie(ac, bc);
    }


    /* Left off here. */


    public static String lpad( String inStr, int size, char fill ) {
        return new String( Chr.lpad( inStr.toCharArray(), size, fill ) );
    }


    public static String lpad( String inStr, int size ) {
        return new String( Chr.lpad( inStr.toCharArray(), size, ' ' ) );
    }

    public static String zfill( int num, int size ) {
        return new String( Chr.lpad( Integer.toString( num ).toCharArray(),
                size, '0' ) );
    }


    public static String rpad( String inStr, int size, char fill ) {
        return new String( Chr.rpad( inStr.toCharArray(), size, fill ) );
    }


    public static String rpad( String inStr, int size) {
        return new String( Chr.rpad( inStr.toCharArray(), size, ' ' ) );
    }


    public static String rpad( Object obj, int size) {
        if (obj != null) {
            return new String( Chr.rpad( obj.toString().toCharArray(), size, ' ' ) );
        } else {
            return new String( Chr.rpad( "<NULL>".toCharArray(), size, ' ' ) );
        }
    }


    public static String[] split( final String input,
                                  final char split ) {
        return StringScanner.split( input, split );

    }

    public static boolean in( String value, String str ) {
        return str.contains( value );
    }


    public static String lines( String... lines ) {
        return join( '\n', lines );
    }



    public static String linesConvertQuotes( String... lines ) {

        for (int index=0; index < lines.length; index++) {
            lines[index]=lines[index].replace( '\'', '"' );
        }
        return join( '\n', lines );
    }


    public static String join( char delim, String... args ) {
        CharBuf builder = CharBuf.create( 10 * args.length );

        int index = 0;
        for ( String arg : args ) {
            builder.add( arg );
            if ( !( index == args.length - 1 ) ) {
                builder.add( delim );
            }
            index++;
        }
        return builder.toString();
    }

    public static String join( String... args ) {
        CharBuf builder = CharBuf.create( 10 * args.length );

        for ( String arg : args ) {
            builder.add( arg );
        }
        return builder.toString();
    }

    public static String joinCollection( char delim, List<?> args ) {
        CharBuf builder = CharBuf.create( 10 * args.size() );

        int index = 0;
        for ( Object arg : args ) {
            if ( arg == null ) {
                continue;
            }
            builder.add( arg.toString() );
            if ( !( index == args.size() - 1 ) ) {
                builder.add( delim );
            }
            index++;
        }
        return builder.toString();

    }


    public static boolean isEmpty( String str ) {
        if ( str == null ) {
            return true;
        } else {
            return str.isEmpty();
        }

    }


    public static String uncapitalize( String string ) {
        StringBuilder rv = new StringBuilder();
        if ( string.length() > 0 ) {
            rv.append( Character.toLowerCase( string.charAt( 0 ) ) );
            if ( string.length() > 1 ) {
                rv.append( string.substring( 1 ) );
            }
        }
        return rv.toString();
    }

    public static String toString(Object object, String defaultString) {
        if (object == null) {
            return defaultString;
        } else {
            return object.toString();
        }
    }


    public static String toString(Object object) {
        if (object == null) {
            return "";
        } else {
            return object.toString();
        }
    }


    public static String str(Object str) {
        return str == null ? "<NULL>" : str.toString();
    }

    public static boolean startsWithItemInCollection(String name, Collection<String> startsWithList) {
        for (String startsWith : startsWithList) {
            if (name.startsWith(startsWith)) {
                return true;
            }
        }
        return false;
    }

    public static String readable(String s) {
        return s.replace("\\n", "\n");
    }
}