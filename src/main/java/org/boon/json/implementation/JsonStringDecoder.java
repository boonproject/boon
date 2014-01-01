package org.boon.json.implementation;

import org.boon.core.reflection.FastStringUtils;
import org.boon.json.JsonException;
import org.boon.primitive.CharBuf;
import org.boon.primitive.Chr;

public class JsonStringDecoder {



    protected static final int DOUBLE_QUOTE = '"';

    protected static final int ESCAPE = '\\';


    protected static final int LETTER_N = 'n';


    protected static final int LETTER_U = 'u';


    protected static final int LETTER_T = 't';

    protected static final int LETTER_R = 'r';

    protected static final int LETTER_B = 'b';

    protected static final int LETTER_F = 'f';

    protected static final int FORWARD_SLASH = '/';


    public static String decode( String string ) {
        if ( !string.contains( "\\" ) ) {
            return string;
        }
        char[] cs = FastStringUtils.toCharArray( string );
        return decode( cs, 0, cs.length );
    }

    public static String decode( char[] chars ) {

        return decode( chars, 0, chars.length );
    }


    public static String decode( char[] chars, int start, int to ) {

        if ( !Chr.contains( chars, '\\', start, to - start ) ) {
            return new String( chars, start, to - start );
        }
        return decodeForSure( chars, start, to );
    }


    public static String decodeForSure( CharSequence cs, int start, int to ) {
        if ( cs instanceof String ) {
            return decodeForSure( ( String ) cs, start, to );
        } else {
            return decode( cs.subSequence( start, to ).toString() );
        }

    }


    public static String decodeForSure( String string, int start, int to ) {
        char[] cs = FastStringUtils.toCharArray( string );
        return decodeForSure( cs, start, to );
    }

    public static String decodeForSure( char[] chars ) {
        return decodeForSure( chars, 0, chars.length );
    }

    public static String decodeForSure( char[] chars, int start, int to ) {

        CharBuf builder = CharBuf.create( to - start );
        for ( int index = start; index < to; index++ ) {
            char c = chars[ index ];
            if ( c == '\\' ) {
                if ( index < to ) {
                    index++;
                    c = chars[ index ];
                    switch ( c ) {

                        case 'n':
                            builder.add( '\n' );
                            break;

                        case '/':
                            builder.add( '/' );
                            break;

                        case '"':
                            builder.add( '"' );
                            break;

                        case 'f':
                            builder.add( '\f' );
                            break;

                        case 't':
                            builder.add( '\t' );
                            break;

                        case '\\':
                            builder.add( '\\' );
                            break;

                        case 'b':
                            builder.add( '\b' );
                            break;

                        case 'r':
                            builder.add( '\r' );
                            break;

                        case 'u':

                            if ( index + 4 < to ) {
                                String hex = new String( chars, index + 1, 4 );
                                char unicode = ( char ) Integer.parseInt( hex, 16 );
                                builder.add( unicode );
                                index += 4;
                            }
                            break;
                        default:
                            throw new JsonException( "Unable to decode string" );
                    }
                }
            } else {
                builder.add( c );
            }
        }
        return builder.toString();

    }

    public static String decodeForSure( byte[] bytes, int start, int to ) {


        if ( bytes[ start ] == '"' ) {
            start++;
        }

        CharBuf builder = CharBuf.create( to - start );
        for ( int index = start; index < to; index++ ) {
            int c = bytes[ index ];
            if ( c == '\\' ) {
                if ( index < bytes.length ) {
                    index++;
                    c = bytes[ index ];
                    switch ( c ) {

                        case LETTER_N:
                            builder.addChar( '\n' );
                            break;

                        case FORWARD_SLASH:
                            builder.addChar( '/' );
                            break;

                        case DOUBLE_QUOTE:
                            builder.addChar( '"' );
                            break;

                        case LETTER_F:
                            builder.addChar( '\f' );
                            break;

                        case LETTER_T:
                            builder.addChar( '\t' );
                            break;

                        case ESCAPE:
                            builder.addChar( '\\' );
                            break;

                        case LETTER_B:
                            builder.addChar( '\b' );
                            break;

                        case LETTER_R:
                            builder.addChar( '\r' );
                            break;

                        case LETTER_U:

                            if ( index + 4 < bytes.length ) {

                                CharBuf hex = CharBuf.create( 4 );
                                hex.addChar( bytes[ index + 1 ] );

                                hex.addChar( bytes[ index + 2 ] );

                                hex.addChar( bytes[ index + 3 ] );

                                hex.addChar( bytes[ index + 4 ] );
                                char unicode = ( char ) Integer.parseInt( hex.toString(), 16 );
                                builder.add( unicode );
                                index += 4;
                            }
                            break;
                        default:
                            throw new JsonException( "Unable to decode string" );
                    }
                }
            } else {
                builder.addChar( c );
            }
        }
        return builder.toString();

    }
}