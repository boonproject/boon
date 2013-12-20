package org.boon.json.implementation;

import org.boon.core.reflection.Reflection;
import org.boon.json.JsonException;
import org.boon.primitive.CharBuf;
import org.boon.primitive.Chr;

public class JsonStringDecoder {

<<<<<<< HEAD
    public static String decode ( String string ) {
=======
    public static String decode( String string ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        if ( !string.contains ( "\\" ) ) {
            return string;
        }
        char[] cs = ( char[] ) Reflection.idx ( string, "value" );
        return decode ( cs, 0, cs.length );
    }

    public static String decode ( char[] chars ) {

        return decode ( chars, 0, chars.length );
    }


<<<<<<< HEAD
    public static String decode ( char[] chars, int start, int to ) {
=======
    public static String decode( char[] chars, int start, int to ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc

        if ( !Chr.contains ( chars, '\\', start, to - start ) ) {
            return new String ( chars, start, to - start );
        }
        return decodeForSure ( chars, start, to );
    }


<<<<<<< HEAD
    public static String decodeForSure ( CharSequence cs, int start, int to ) {
=======
    public static String decodeForSure( CharSequence cs, int start, int to ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        if ( cs instanceof String ) {
            return decodeForSure ( ( String ) cs, start, to );
        } else {
            return decode ( cs.subSequence ( start, to ).toString () );
        }

    }


<<<<<<< HEAD
    public static String decodeForSure ( String string, int start, int to ) {
=======
    public static String decodeForSure( String string, int start, int to ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        char[] cs = ( char[] ) Reflection.idx ( string, "value" );
        return decodeForSure ( cs, start, to );
    }

    public static String decodeForSure ( char[] chars ) {
        return decodeForSure ( chars, 0, chars.length );
    }

<<<<<<< HEAD
    public static String decodeForSure ( char[] chars, int start, int to ) {
=======
    public static String decodeForSure( char[] chars, int start, int to ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc

        CharBuf builder = CharBuf.create ( to - start );
        for ( int index = start; index < to; index++ ) {
            char c = chars[ index ];
            if ( c == '\\' ) {
                if ( index < to ) {
                    index++;
                    c = chars[ index ];
                    switch ( c ) {

                        case 'n':
                            builder.add ( '\n' );
                            break;

                        case '/':
                            builder.add ( '/' );
                            break;

                        case '"':
                            builder.add ( '"' );
                            break;

                        case 'f':
                            builder.add ( '\f' );
                            break;

                        case 't':
                            builder.add ( '\t' );
                            break;

                        case '\\':
                            builder.add ( '\\' );
                            break;

                        case 'b':
                            builder.add ( '\b' );
                            break;

                        case 'r':
                            builder.add ( '\r' );
                            break;

                        case 'u':

                            if ( index + 4 < to ) {
                                String hex = new String ( chars, index + 1, 4 );
                                char unicode = ( char ) Integer.parseInt ( hex, 16 );
                                builder.add ( unicode );
                                index += 4;
                            }
                            break;
                        default:
                            throw new JsonException ( "Unable to decode string" );
                    }
                }
            } else {
                builder.add ( c );
            }
        }
        return builder.toString ();

    }


<<<<<<< HEAD
    public static String decode ( final byte[] chars, int start, int to ) {
=======
    public static String decode( final byte[] chars, int start, int to ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc

        final byte[] cs = chars;

        if ( cs[ start ] == '"' ) {
            start++;
        }

        CharBuf builder = CharBuf.create ( to - start );
        for ( int index = start; index < to; index++ ) {
            byte c = cs[ index ];
            if ( c == '\\' ) {
                if ( index < cs.length ) {
                    index++;
                    c = cs[ index ];
                    switch ( c ) {

                        case 'n':
                            builder.addChar ( '\n' );
                            break;

                        case '/':
                            builder.addChar ( '/' );
                            break;

                        case '"':
                            builder.addChar ( '"' );
                            break;

                        case 'f':
                            builder.addChar ( '\f' );
                            break;

                        case 't':
                            builder.addChar ( '\t' );
                            break;

                        case '\\':
                            builder.addChar ( '\\' );
                            break;

                        case 'b':
                            builder.addChar ( '\b' );
                            break;

                        case 'r':
                            builder.addChar ( '\r' );
                            break;

                        case 'u':

                            if ( index + 4 < cs.length ) {
                                String hex = new String ( cs, index + 1, index + 5 );
                                char unicode = ( char ) Integer.parseInt ( hex, 16 );
                                builder.add ( unicode );
                                index += 4;
                            }
                            break;
                        default:
                            throw new JsonException ( "Unable to decode string" );
                    }
                }
            } else {
                builder.addChar ( c );
            }
        }
        return builder.toString ();

    }
}