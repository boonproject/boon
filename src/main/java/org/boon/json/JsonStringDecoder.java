package org.boon.json;

import org.boon.core.reflection.Reflection;
import org.boon.primitive.CharBuf;
import org.boon.primitive.Chr;

public class JsonStringDecoder {

    public static String decode( String string ) {
        if ( !string.contains ( "\\" )) {
            return string;
        }
        char[] cs = (char[]) Reflection.idx ( string, "value" );
        return decode ( cs, 0, cs.length );
    }

    public static String decode( char[] chars ) {

        return decode ( chars, 0, chars.length );
    }




    public static String decode( char[] chars, int start, int to ) {

        if (!Chr.contains ( chars, '\\' , start, to - start) ) {
            return new String (chars, start, to - start);
        }
        return decodeForSure(chars, start, to);
    }



    public static String decodeForSure( CharSequence cs, int start, int to ) {
             if (cs instanceof String) {
                 return decodeForSure ( (String)cs, start, to );
             } else {
                 return decode ( cs.subSequence ( start, to ).toString () );
             }

    }


    public static String decodeForSure( String string, int start, int to ) {
        char[] cs = (char[]) Reflection.idx ( string, "value" );
        return decodeForSure ( cs, start, to );
    }

    public static String decodeForSure( char[] chars, int start, int to ) {

        CharBuf builder = CharBuf.create ( to - start );
        for ( int index = start; index < to; index++ ) {
            char c = chars[index];
            if ( c == '\\' ) {
                if ( index < to ) {
                    index++;
                    c = chars[index];
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
                                String hex = new String ( chars, index + 1, index + 5 );
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
        return builder.toString ( );

    }



    public static String decode( final byte[] chars, int start, int to ) {

        final byte[] cs = chars;

        if ( cs[start] == '"' ) {
            start++;
        }

        CharBuf builder = CharBuf.create (  to - start );
        for ( int index = start; index < to; index++ ) {
            byte c = cs[index];
            if ( c == '\\' ) {
                if ( index < cs.length ) {
                    index++;
                    c = cs[index];
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
        return builder.toString ( );

    }
}