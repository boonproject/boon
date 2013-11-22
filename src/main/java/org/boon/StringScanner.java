package org.boon;

import org.boon.primitive.CharScanner;

public class StringScanner {


//    char[][] split = Chr.splitLine(str.toCharArray());
//    return fromCharArrayOfArrayToStringArray(split);
//
//
//    public static char[][] split( final char[] inputArray,
//                                  final char split) {
//
//    }


    public static boolean isDigits( String input ) {
        return CharScanner.isDigits ( input.toCharArray ( ) );
    }

    public static String[] split( final String string,
                                  final char split ) {

        char[][] comps = CharScanner.split ( string.toCharArray ( ), split );

        return Str.fromCharArrayOfArrayToStringArray ( comps );

    }

    public static String[] splitByChars( final String string,
                                         final char... delimiters ) {

        char[][] comps = CharScanner.splitByChars ( string.toCharArray ( ), delimiters );

        return Str.fromCharArrayOfArrayToStringArray ( comps );

    }

    public static String[] splitByDelimiters( final String string,
                                              final String delimiters ) {

        char[][] comps = CharScanner.splitByChars ( string.toCharArray ( ), delimiters.toCharArray ( ) );

        return Str.fromCharArrayOfArrayToStringArray ( comps );

    }

}
