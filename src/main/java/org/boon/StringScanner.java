package org.boon;

import org.boon.core.reflection.Reflection;
import org.boon.primitive.CharScanner;

public class StringScanner {





    public static boolean isDigits( String input ) {
        return CharScanner.isDigits ( Reflection.toCharArray ( input ) );
    }

    public static String[] split( final String string,
                                  final char split ) {

        char[][] comps = CharScanner.split ( Reflection.toCharArray ( string ), split );

        return Str.fromCharArrayOfArrayToStringArray ( comps );

    }

    public static String[] splitByChars( final String string,
                                         final char... delimiters ) {

        char[][] comps = CharScanner.splitByChars ( Reflection.toCharArray ( string ), delimiters );

        return Str.fromCharArrayOfArrayToStringArray ( comps );

    }

    public static String[] splitByDelimiters( final String string,
                                              final String delimiters ) {

        char[][] comps = CharScanner.splitByChars ( Reflection.toCharArray ( string  ), delimiters.toCharArray () );

        return Str.fromCharArrayOfArrayToStringArray ( comps );

    }


    public static String[] splitByCharsNoneEmpty( final String string, final char... delimiters)  {
        char[][] comps = CharScanner.splitByCharsNoneEmpty ( Reflection.toCharArray ( string), delimiters );
        return Str.fromCharArrayOfArrayToStringArray ( comps );
    }

}
