package org.boon;

import org.boon.primitive.CharBuf;
import org.boon.primitive.Chr;

import java.util.List;

public class Str {


    public final static String EMPTY_STRING = "";

<<<<<<< HEAD
    public static int len ( String str ) {
        return str.length ();
    }

    public static String slc ( String str, int start ) {
        return new String ( Chr.slc ( str.toCharArray (), start ) );
    }

    public static String slc ( String str, int start, int end ) {
        return new String ( Chr.slc ( str.toCharArray (), start, end ) );
    }

    public static String slcEnd ( String str, int end ) {
=======
    public static int len( String str ) {
        return str.length ();
    }

    public static String slc( String str, int start ) {
        return new String ( Chr.slc ( str.toCharArray (), start ) );
    }

    public static String slc( String str, int start, int end ) {
        return new String ( Chr.slc ( str.toCharArray (), start, end ) );
    }

    public static String slcEnd( String str, int end ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return new String ( Chr.slcEnd ( str.toCharArray (), end ) );
    }


<<<<<<< HEAD
    public static char idx ( String str, int index ) {
=======
    public static char idx( String str, int index ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        int i = calculateIndex ( str.length (), index );

        char c = str.charAt ( i );
        return c;
    }


    public static String idx ( String str, int index, char c ) {

        char[] chars = str.toCharArray ();
        Chr.idx ( chars, index, c );
        return new String ( chars );
    }


    public static boolean in ( char c, String str ) {

        for ( int index = 0; index < str.length (); index++ ) {
            char current = str.charAt ( index );
            if ( c == current ) {
                return true;
            }
        }

        return false;
    }


<<<<<<< HEAD
    public static boolean in ( char c, int offset, String str ) {
=======
    public static boolean in( char c, int offset, String str ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        final int length = str.length ();
        int off = calculateIndex ( length, offset );
        for ( int index = off; index < length; index++ ) {
            char current = str.charAt ( index );
            if ( c == current ) {
                return true;
            }
        }

        return false;
    }


<<<<<<< HEAD
    public static boolean in ( char c, int offset, int end, String str ) {
=======
    public static boolean in( char c, int offset, int end, String str ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        final int length = str.length ();

        int off = calculateIndex ( length, offset );
        int stop = calculateIndex ( length, end );

        for ( int index = off; index < stop; index++ ) {
            char current = str.charAt ( index );
            if ( c == current ) {
                return true;
            }
        }

        return false;
    }


<<<<<<< HEAD
    public static String add ( String str, char c ) {
=======
    public static String add( String str, char c ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return new String ( Chr.add ( str.toCharArray (), c ) );
    }


<<<<<<< HEAD
    public static String add ( String str, String str2 ) {
=======
    public static String add( String str, String str2 ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return new String ( Chr.add ( str.toCharArray (), str2.toCharArray () ) );
    }

    public static String add ( String... strings ) {
        int length = 0;
        for ( String str : strings ) {
            if ( str == null ) {
                continue;
            }
            length += str.length ();
        }
        CharBuf builder = CharBuf.createExact ( length );
        for ( String str : strings ) {
            if ( str == null ) {
                continue;
            }
            builder.add ( str );
        }
        return builder.toString ();
    }

<<<<<<< HEAD
    public static String compact ( String str ) {
=======
    public static String compact( String str ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return new String ( Chr.compact ( str.toCharArray () ) );
    }


    private static int calculateIndex ( final int length, int originalIndex ) {


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


<<<<<<< HEAD
    public static String[] split ( String str ) {
=======
    public static String[] split( String str ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        char[][] split = Chr.split ( str.toCharArray () );
        return fromCharArrayOfArrayToStringArray ( split );
    }

<<<<<<< HEAD
    public static String[] splitLines ( String str ) {
=======
    public static String[] splitLines( String str ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        char[][] split = Chr.splitLine ( str.toCharArray () );
        return fromCharArrayOfArrayToStringArray ( split );
    }

    public static String[] fromCharArrayOfArrayToStringArray ( char[][] split ) {
        String[] results = new String[ split.length ];

        char[] array;

        for ( int index = 0; index < split.length; index++ ) {
            array = split[ index ];

            results[ index ] = array.length == 0 ?
                    EMPTY_STRING : new String ( array );
        }
        return results;
    }


<<<<<<< HEAD
    public static String upper ( String str ) {
        return str.toUpperCase ();
    }

    public static String lower ( String str ) {
=======
    public static String upper( String str ) {
        return str.toUpperCase ();
    }

    public static String lower( String str ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return str.toLowerCase ();
    }


    public static String camelCaseUpper ( String in ) {
        return camelCase ( in, true );
    }


    public static String camelCaseLower ( String in ) {
        return camelCase ( in, false );
    }

    public static String camelCase ( String in ) {
        return camelCase ( in, false );
    }

<<<<<<< HEAD
    public static String camelCase ( String inStr, boolean upper ) {
=======
    public static String camelCase( String inStr, boolean upper ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        char[] in = inStr.toCharArray ();
        char[] out = Chr.camelCase ( in, upper );
        return new String ( out );
    }


<<<<<<< HEAD
    public static String underBarCase ( String inStr ) {
=======
    public static String underBarCase( String inStr ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        char[] in = inStr.toCharArray ();
        char[] out = Chr.underBarCase ( in );
        return new String ( out );
    }


<<<<<<< HEAD
    public static String lpad ( String inStr, int size, char fill ) {
        return new String ( Chr.lpad ( inStr.toCharArray (), size, fill ) );
    }

    public static String zfill ( int num, int size ) {
=======
    public static String lpad( String inStr, int size, char fill ) {
        return new String ( Chr.lpad ( inStr.toCharArray (), size, fill ) );
    }

    public static String zfill( int num, int size ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return new String ( Chr.lpad ( Integer.toString ( num ).toCharArray (),
                size, '0' ) );
    }


<<<<<<< HEAD
    public static String rpad ( String inStr, int size, char fill ) {
=======
    public static String rpad( String inStr, int size, char fill ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return new String ( Chr.rpad ( inStr.toCharArray (), size, fill ) );
    }


    public static String[] split ( final String input,
                                   final char split ) {
        return StringScanner.split ( input, split );

    }

    public static boolean in ( String value, String str ) {
        return str.contains ( value );
    }


    public static String lines ( String... lines ) {
        return join ( '\n', lines );
    }


    public static String join ( char delim, String... args ) {
        CharBuf builder = CharBuf.create ( 10 * args.length );

        int index = 0;
        for ( String arg : args ) {
            builder.add ( arg );
            if ( !( index == args.length - 1 ) ) {
                builder.add ( delim );
            }
            index++;
        }
        return builder.toString ();
    }

<<<<<<< HEAD
    public static String join ( String... args ) {
=======
    public static String join( String... args ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        CharBuf builder = CharBuf.create ( 10 * args.length );

        for ( String arg : args ) {
            builder.add ( arg );
        }
        return builder.toString ();
    }

<<<<<<< HEAD
    public static String joinCollection ( char delim, List<?> args ) {
=======
    public static String joinCollection( char delim, List<?> args ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        CharBuf builder = CharBuf.create ( 10 * args.size () );

        int index = 0;
        for ( Object arg : args ) {
            if ( arg == null ) {
                continue;
            }
            builder.add ( arg.toString () );
            if ( !( index == args.size () - 1 ) ) {
                builder.add ( delim );
            }
            index++;
        }
        return builder.toString ();

    }


    public static boolean isEmpty ( String str ) {
        if ( str == null ) {
            return true;
        } else {
            return str.isEmpty ();
        }

    }
}