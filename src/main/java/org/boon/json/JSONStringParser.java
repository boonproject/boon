package org.boon.json;

import org.boon.primitive.CharBuf;

public class JSONStringParser {

    public static String decode( String string ) {

        char[] cs = string.toCharArray ( );
        return decode ( cs, 0, cs.length );
    }

    public static String decode( char[] chars ) {

        return decode ( chars, 0, chars.length );
    }

    public static String decode( char[] chars, int start, int to ) {

        final char[] cs = chars;
        final int length = cs.length;

        if ( cs[start] == '"' ) {
            start++;
        }

        CharBuf builder = CharBuf.create ( cs.length );
        for ( int index = start; index < to; index++ ) {
            char c = cs[index];
            if ( c == '\\' ) {
                if ( index < cs.length ) {
                    index++;
                    c = cs[index];
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

                            if ( index + 4 < cs.length ) {
                                String hex = new String ( cs, index + 1, index + 5 );
                                char unicode = ( char ) Integer.parseInt ( hex, 16 );
                                builder.add ( unicode );
                                index += 4;
                            }
                            break;
                        default:
                            new JSONException ( "Unable to decode string" );
                    }
                }
            } else {
                builder.add ( c );
            }
        }
        return builder.toString ( );

    }

}