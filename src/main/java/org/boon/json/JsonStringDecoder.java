package org.boon.json;

import org.boon.primitive.Byt;
import org.boon.primitive.ByteBuf;
import org.boon.primitive.CharBuf;
import org.boon.primitive.Chr;

public class JsonStringDecoder {

    public static String decode( String string ) {
        if ( !string.contains ( "\\" )) {
            return string;
        }
        char[] cs = string.toCharArray ( );
        return decode ( cs, 0, cs.length );
    }

    public static String decode( char[] chars ) {

        return decode ( chars, 0, chars.length );
    }

    public static String decode( char[] chars, int start, int to ) {



        if (!Chr.contains ( chars, '\\' , start, to - start) ) {
            return new String (chars, start, to - start);
        }

        final char[] cs = chars;

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

        if (!Byt.in ( '\\', start, to, chars ) ) {
            return new String (chars, start+1, to - start-1);
        }

        final byte[] cs = chars;

        if ( cs[start] == '"' ) {
            start++;
        }

        ByteBuf builder = ByteBuf.create ( cs.length );
        for ( int index = start; index < to; index++ ) {
            byte c = cs[index];
            if ( c == '\\' ) {
                if ( index < cs.length ) {
                    index++;
                    c = cs[index];
                    switch ( c ) {

                        case 'n':
                            builder.addByte ( '\n' );
                            break;

                        case '/':
                            builder.addByte ( '/' );
                            break;

                        case '"':
                            builder.addByte ( '"' );
                            break;

                        case 'f':
                            builder.addByte ( '\f' );
                            break;

                        case 't':
                            builder.addByte ( '\t' );
                            break;

                        case '\\':
                            builder.addByte ( '\\' );
                            break;

                        case 'b':
                            builder.addByte ( '\b' );
                            break;

                        case 'r':
                            builder.addByte ( '\r' );
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
                builder.addByte ( c );
            }
        }
        return builder.toString ( );

    }
}