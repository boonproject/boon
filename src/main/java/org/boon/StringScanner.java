package org.boon;

import org.boon.core.reflection.Reflection;
import org.boon.primitive.CharScanner;

import java.util.Objects;

public class StringScanner {


<<<<<<< HEAD
    public static boolean isDigits ( String input ) {
=======
    public static boolean isDigits( String input ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return CharScanner.isDigits ( Reflection.toCharArray ( input ) );
    }

    public static String[] split ( final String string,
                                   final char split ) {

        char[][] comps = CharScanner.split ( Reflection.toCharArray ( string ), split );

        return Str.fromCharArrayOfArrayToStringArray ( comps );

    }

    public static String[] splitByChars ( final String string,
                                          final char... delimiters ) {

        char[][] comps = CharScanner.splitByChars ( Reflection.toCharArray ( string ), delimiters );

        return Str.fromCharArrayOfArrayToStringArray ( comps );

    }

    public static String[] splitByDelimiters ( final String string,
                                               final String delimiters ) {

        char[][] comps = CharScanner.splitByChars ( Reflection.toCharArray ( string ), delimiters.toCharArray () );

        return Str.fromCharArrayOfArrayToStringArray ( comps );

    }


<<<<<<< HEAD
    public static String[] splitByCharsNoneEmpty ( final String string, final char... delimiters ) {
=======
    public static String[] splitByCharsNoneEmpty( final String string, final char... delimiters ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        Objects.requireNonNull ( string );

        char[][] comps = CharScanner.splitByCharsNoneEmpty ( Reflection.toCharArray ( string ), delimiters );
        return Str.fromCharArrayOfArrayToStringArray ( comps );
    }


<<<<<<< HEAD
    public static String[] splitByCharsNoneEmpty ( final String string, int start, int end, final char... delimiters ) {
=======
    public static String[] splitByCharsNoneEmpty( final String string, int start, int end, final char... delimiters ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        Objects.requireNonNull ( string );

        char[][] comps = CharScanner.splitByCharsNoneEmpty ( Reflection.toCharArray ( string ), start, end, delimiters );
        return Str.fromCharArrayOfArrayToStringArray ( comps );
    }

}
