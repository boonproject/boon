package org.boon.json;

import org.boon.core.reflection.Reflection;
import org.boon.primitive.Byt;
import org.boon.primitive.ByteScanner;
import org.boon.primitive.CharBuf;
import sun.nio.cs.Surrogate;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.boon.Exceptions.die;
import static org.boon.primitive.ByteScanner.isInteger;
import static org.boon.primitive.ByteScanner.parseInt;
import static org.boon.primitive.ByteScanner.parseLong;

public class JsonUTF8Parser {

    private byte[] charArray;
    private int __index;
    private int __currentChar;


    private static final int LETTER_B = 'b';
    private static final int NEW_LINE = '\n';

    private static final int RETURN = '\r';

    private static final int SPACE = ' ';
    private static final int ESCAPE = '\\';
    private static final int FORWARD_SLASH = '/';


    private static final int TAB = '\t';
    private static final int BELL = '\b';
    private static final int FORM_FEED = '\f';
    private static final int COMMA = ',';

    private static final int COLON = ':';

    private static final int OPEN_CURLY = '{';

    private static final int CLOSED_CURLY = '}';

    private static final int OPEN_BRACKET = '[';

    private static final int CLOSED_BRACKET = ']';


    private static final int DOUBLE_QUOTE = '"';

    private static final int LETTER_N = 'n';


    private static final int LETTER_U = 'u';

    private static final int LETTER_L = 'l';

    private static final int LETTER_T = 't';

    private static final int LETTER_R = 'r';

    private static final int LETTER_E = 'e';
    private static final int LETTER_BIG_E = 'E';

    private static final int LETTER_F = 'f';

    private static final int LETTER_A = 'a';


    private static final int LETTER_S = 's';

    private static final int ALPHA_0 = '0';
    private static final int ALPHA_1 = '1';
    private static final int ALPHA_2 = '2';
    private static final int ALPHA_3 = '3';
    private static final int ALPHA_4 = '4';
    private static final int ALPHA_5 = '5';
    private static final int ALPHA_6 = '6';
    private static final int ALPHA_7 = '7';
    private static final int ALPHA_8 = '8';
    private static final int ALPHA_9 = '9';
    private static final int MINUS = '-';
    private static final int PLUS = '+';

    private static final int DECIMAL_POINT = '.';

    private final CharBuf builder = CharBuf.create ( 20 );





    public JsonUTF8Parser () {

    }


    public static Object parse ( String cs ) {
        JsonUTF8Parser p = new JsonUTF8Parser ();
        return p.decode ( cs );

    }


    public static Map<String, Object> parseMap ( String cs ) {
        JsonUTF8Parser p = new JsonUTF8Parser ();
        return ( Map<String, Object> ) p.decode ( cs );
    }


    public static <T> T parseInto ( T object, String cs ) {
        Map<String, Object> objectMap = parseMap ( cs );
        return Reflection.fromMap ( objectMap, object );
    }

    public static <T> T parseInto ( Class<T> clz, String cs ) {
        Map<String, Object> objectMap = parseMap ( cs );
        return Reflection.fromMap ( objectMap, clz );
    }


    public static Object parseIntoJavaObject ( String cs ) {
        Map<String, Object> objectMap = parseMap ( cs );
        return Reflection.fromMap ( objectMap );
    }

    public static <T> T parseInto ( T object, byte[] cs ) {
        Map<String, Object> objectMap = parseMap ( cs );
        return Reflection.fromMap ( objectMap, object );
    }

    public static <T> T parseInto ( Class<T> clz, byte[] cs ) {
        Map<String, Object> objectMap = parseMap ( cs );
        return Reflection.fromMap ( objectMap, clz );
    }


    public static Object parseIntoJavaObject ( byte[] cs ) {
        Map<String, Object> objectMap = parseMap ( cs );
        return Reflection.fromMap ( objectMap );
    }


    public static Object parse ( byte[] cs ) {
        JsonUTF8Parser p = new JsonUTF8Parser ();
        return p.decode ( cs );

    }

    public static Map<String, Object> parseMap ( byte[] cs ) {
        JsonUTF8Parser p = new JsonUTF8Parser ();
        return ( Map<String, Object> ) p.decode ( cs );
    }


    @SuppressWarnings ( "unchecked" )
    public Object decode ( byte[] cs ) {
        charArray = cs;
        __index = 0;
        return decodeValue ();
    }


    private Object decode ( String cs ) {
        charArray = cs.getBytes ( StandardCharsets.UTF_8 );
        Object root = null;
        root = decodeValue ();
        return root;
    }


    private final boolean hasMore () {
        return __index + 1 < charArray.length;
    }

    private int nextChar () {

        try {
            if ( hasMore () ) {
                __index++;
                return __currentChar = charArray[ __index ];
            }
            return __currentChar;

        } catch ( Exception ex ) {
            throw new RuntimeException ( exceptionDetails ( "failure in next " +
                    ex.getLocalizedMessage () ), ex );

        }
    }


    private String exceptionDetails ( String message ) {
        CharBuf buf = CharBuf.create ( 255 );

        buf.addLine ( message );

        buf.addLine ( "" );
        buf.addLine ( "The current character read is " + charDescription ( __currentChar ) );


        buf.addLine ( message );

        int line = 0;
        int lastLineIndex = 0;

        for ( int i = 0; i < __index; i++ ) {
            if ( charArray[ i ] == NEW_LINE ) {
                line++;
                lastLineIndex = i + 1;
            }
        }

        int count = 0;

        for ( int i = lastLineIndex; i < charArray.length; i++, count++ ) {
            if ( charArray[ i ] == NEW_LINE ) {
                break;
            }
        }


        buf.addLine ( "line number " + line + 1 );
        buf.addLine ( "index number " + __index );


        try {
            buf.addLine ( new String ( charArray, lastLineIndex, count ) );
        } catch ( Exception ex ) {

            try {
                int index = ( __index - 10 < 0 ) ? 0 : __index - 10;

                buf.addLine ( new String ( charArray, index, __index ) );
            } catch ( Exception ex2 ) {
                buf.addLine ( new String ( charArray, 0, charArray.length ) );
            }
        }
        for ( int i = 0; i < ( __index - lastLineIndex - 1 ); i++ ) {
            buf.add ( '.' );
        }
        buf.add ( '^' );

        return buf.toString ();
    }

    private void skipWhiteSpace () {


        label:
        for (; __index < this.charArray.length; __index++ ) {
            __currentChar = charArray[ __index ];
            switch ( __currentChar ) {
                case NEW_LINE:
                    continue label;
                case RETURN:
                    continue label;

                case SPACE:
                    continue label;
                case TAB:
                    continue label;
                default:
                    break label;

            }
        }

    }

    private Object decodeJsonObject () {

        if ( __currentChar == OPEN_CURLY && this.hasMore () )
            this.nextChar ();

        Map<String, Object> map = new LinkedHashMap<> ();


        do {

            skipWhiteSpace ();


            if ( __currentChar == DOUBLE_QUOTE ) {
                String key = decodeKeyName ();
                skipWhiteSpace ();

                if ( __currentChar != COLON ) {

                    complain ( "expecting current character to be " + charDescription ( __currentChar ) + "\n" );
                }
                this.nextChar (); // skip past COLON
                skipWhiteSpace ();

                Object value = decodeValue ();

                skipWhiteSpace ();

                map.put ( key, value );


                if ( !( __currentChar == CLOSED_CURLY || __currentChar == COMMA ) ) {
                    complain ( "expecting '}' or ',' but got current char " + charDescription ( __currentChar ) );
                }
            }
            if ( __currentChar == CLOSED_CURLY ) {
                this.nextChar ();
                break;
            } else if ( __currentChar == COMMA ) {
                this.nextChar ();
                continue;
            } else {
                complain (
                        "expecting '}' or ',' but got current char " + charDescription ( __currentChar ) );

            }
        } while ( this.hasMore () );
        return map;
    }

    private void complain ( String complaint ) {
        throw new JsonException ( exceptionDetails ( complaint ) );
    }


    private Object decodeValue () {
        Object value = null;

        done:
        for (; __index < this.charArray.length; __index++ ) {
            __currentChar = charArray[ __index ];


            switch ( __currentChar ) {
                case NEW_LINE:
                    break;

                case RETURN:
                case SPACE:
                case TAB:
                case BELL:
                case FORM_FEED:
                    break;

                case DOUBLE_QUOTE:
                    value = decodeString ();
                    break done;


                case LETTER_T:
                    value = decodeTrue ();
                    break done;

                case LETTER_F:
                    value = decodeFalse ();
                    break done;

                case LETTER_N:
                    value = decodeNull ();
                    break done;

                case OPEN_BRACKET:
                    value = decodeJsonArray ();
                    break done;

                case OPEN_CURLY:
                    value = decodeJsonObject ();
                    break done;

                case ALPHA_1:
                case ALPHA_2:
                case ALPHA_3:
                case ALPHA_4:
                case ALPHA_5:
                case ALPHA_6:
                case ALPHA_7:
                case ALPHA_8:
                case ALPHA_9:
                case ALPHA_0:
                case MINUS:
                    value = decodeNumber ();
                    break done;

                default:
                    throw new JsonException ( exceptionDetails ( "Unable to determine the " +
                            "current character, it is not a string, number, array, or object" ) );

            }
        }

        return value;
    }


    private Object decodeNumber () {

        int startIndex = __index;

        boolean doubleFloat = false;

        int index;
        int count = 0;
        int countDecimalPoint = 0;
        int eCount = 0;
        int plusCount = 0;


        boolean minus = false;

        boolean simple = true;

        int sign = 1;


        int digitsPastPoint = 0;

        __currentChar = charArray[ __index ];

        if ( __currentChar == MINUS ) {
            minus = true;
            __index++;
        }


        loop:
        for ( index = __index; index < charArray.length; index++, count++ ) {
            __currentChar = charArray[ index ];

            if ( doubleFloat ) {
                digitsPastPoint++;
            }


            switch ( __currentChar ) {
                case SPACE:
                case TAB:
                case NEW_LINE:
                case RETURN:
                case COMMA:
                case CLOSED_BRACKET:
                case CLOSED_CURLY:
                    break loop;

                case ALPHA_1:
                case ALPHA_2:
                case ALPHA_3:
                case ALPHA_4:
                case ALPHA_5:
                case ALPHA_6:
                case ALPHA_7:
                case ALPHA_8:
                case ALPHA_9:
                case ALPHA_0:
                    continue loop;


                case DECIMAL_POINT:
                    doubleFloat = true;
                    countDecimalPoint++;

                    if ( countDecimalPoint > 1 ) {
                        throw new JsonException ( exceptionDetails ( "number has more than one decimal point" ) );
                    }
                    continue loop;

                case LETTER_E:
                case LETTER_BIG_E:
                    simple = false;
                    doubleFloat = true;
                    eCount++;
                    if ( eCount > 1 ) {
                        throw new JsonException ( exceptionDetails ( "number has more than one exp definition" ) );
                    }
                    continue loop;

                case PLUS:
                    simple = false;
                    doubleFloat = true;
                    plusCount++;
                    if ( plusCount > 1 ) {
                        throw new JsonException ( exceptionDetails ( "number has more than one plus sign" ) );
                    }
                    if ( eCount == 0 ) {
                        throw new JsonException ( exceptionDetails ( "plus sign must come after exp" ) );

                    }
                    continue loop;

            }

            complain ( "expecting number char but got current char " + charDescription ( __currentChar ) );
        }

        __index = index;

        Object value;
        if ( doubleFloat ) {
            value = ByteScanner.simpleDouble ( this.charArray, simple, minus, digitsPastPoint - 1, startIndex, __index );
        } else {

            if ( isInteger ( this.charArray, startIndex, __index - startIndex, minus ) ) {
                value = parseInt ( charArray, startIndex, __index - startIndex ) * sign;
            } else {
                value = parseLong ( charArray, startIndex, __index - startIndex ) * sign;
            }

        }


        return value;

    }


    private static byte[] NULL = Byt.bytes ( "null" );

    private Object decodeNull () {

        if ( __index + NULL.length <= charArray.length ) {
            if ( charArray[ __index ] == LETTER_N &&
                    charArray[ ++__index ] == LETTER_U &&
                    charArray[ ++__index ] == LETTER_L &&
                    charArray[ ++__index ] == LETTER_L ) {
                nextChar ();
                return null;
            }
        }
        throw new JsonException ( exceptionDetails ( "null not parsed properly" ) );
    }

    private static byte[] TRUE = Byt.bytes ( "true" );

    private boolean decodeTrue () {

        if ( __index + TRUE.length <= charArray.length ) {
            if ( charArray[ __index ] == LETTER_T &&
                    charArray[ ++__index ] == LETTER_R &&
                    charArray[ ++__index ] == LETTER_U &&
                    charArray[ ++__index ] == LETTER_E ) {

                nextChar ();
                return true;

            }
        }

        throw new JsonException ( exceptionDetails ( "true not parsed properly" ) );
    }


    private static byte[] FALSE = Byt.bytes ( "false" );

    private boolean decodeFalse () {

        if ( __index + FALSE.length <= charArray.length ) {
            if ( charArray[ __index ] == LETTER_F &&
                    charArray[ ++__index ] == LETTER_A &&
                    charArray[ ++__index ] == LETTER_L &&
                    charArray[ ++__index ] == LETTER_S &&
                    charArray[ ++__index ] == LETTER_E ) {
                nextChar ();
                return false;
            }
        }
        throw new JsonException ( exceptionDetails ( "false not parsed properly" ) );
    }

    private final String decodeString () {




        if ( __index < charArray.length && __currentChar == DOUBLE_QUOTE ) {
            __index++;
        }


        loop:
        for (; __index < this.charArray.length; __index++ ) {
            __currentChar = charArray[ __index ];
            switch ( __currentChar ) {

                case DOUBLE_QUOTE:
                    break loop;

                case '\\':
                    if ( __index < charArray.length ) {
                        __index++;
                    }
                    __currentChar = charArray[ __index ];

                    switch ( __currentChar ) {

                        case LETTER_N:
                            builder.addChar ( '\n' );
                            continue loop;

                        case FORWARD_SLASH:
                            builder.addChar ( '/' );
                            continue loop;

                        case DOUBLE_QUOTE:
                            builder.addChar ( '"' );
                            continue loop;

                        case LETTER_F:
                            builder.addChar ( '\f' );
                            continue loop;

                        case LETTER_T:
                            builder.addChar ( '\t' );
                            continue loop;

                        case ESCAPE:
                            builder.addChar ( '\\' );
                            continue loop;

                        case LETTER_B:
                            builder.addChar ( '\b' );
                            continue loop;

                        case LETTER_R:
                            builder.addChar ( '\r' );
                            continue loop;

                        case LETTER_U:

                            if ( __index + 4 < charArray.length ) {

                                CharBuf hex = CharBuf.create ( 4 );
                                hex.addChar ( charArray[ __index + 1 ] );

                                hex.addChar ( charArray[ __index + 2 ] );

                                hex.addChar ( charArray[ __index + 3 ] );

                                hex.addChar ( charArray[ __index + 4 ] );
                                char unicode = ( char ) Integer.parseInt ( hex.toString (), 16 );
                                builder.add ( unicode );
                                __index += 4;
                            }
                            continue loop;
                    }
                default:


                    if ( __currentChar >= 0 ) {
                        builder.addChar ( __currentChar );
                    } else {
                        utf8MultiByte ( __currentChar, builder );
                    }

            }
        }


        if ( __index < charArray.length ) {
            __index++;
        }

        String str = builder.toString ();

        builder.readForRecycle ();
        return str;
    }

    private String decodeKeyName () {
        return decodeString ();

    }

    private List decodeJsonArray () {
        if ( __currentChar == '[' ) {
            this.nextChar ();
        }

        skipWhiteSpace ();

        List<Object> list = new ArrayList<> ();

        /* the list might be empty  */
        if ( __currentChar == ']' ) {
            this.nextChar ();
            return list;
        }


        int arrayIndex = 0;

        do {
            skipWhiteSpace ();

            Object arrayItem = decodeValue ();

            list.add ( arrayItem );

            arrayIndex++;


            skipWhiteSpace ();


            if ( __currentChar == COMMA ) {
                this.nextChar ();
                continue;
            } else if ( __currentChar == CLOSED_BRACKET ) {
                this.nextChar ();
                break;
            } else {
                String charString = charDescription ( __currentChar );

                complain (
                        String.format ( "expecting a ',' or a ']', " +
                                " but got \nthe current character of  %s " +
                                " on array index of %s \n", charString, arrayIndex )
                );

            }
        } while ( this.hasMore () );
        return list;
    }

    private String charDescription ( int c ) {
        String charString;
        if ( c == SPACE ) {
            charString = "[SPACE]";
        } else if ( c == TAB ) {
            charString = "[TAB]";

        } else if ( c == NEW_LINE ) {
            charString = "[NEWLINE]";

        } else {
            charString = "'" + ( char ) c + "'";
        }

        charString = charString + " with an int value of " + ( ( int ) c );
        return charString;
    }


    //  [C2..DF] [80..BF]
    private static boolean isMalformed2 ( int b1, int b2 ) {
        return ( b1 & 0x1e ) == 0x0 || ( b2 & 0xc0 ) != 0x80;
    }

    //  [E0]     [A0..BF] [80..BF]
    //  [E1..EF] [80..BF] [80..BF]
    private static boolean isMalformed3 ( int b1, int b2, int b3 ) {
        return ( b1 == ( byte ) 0xe0 && ( b2 & 0xe0 ) == 0x80 ) ||
                ( b2 & 0xc0 ) != 0x80 || ( b3 & 0xc0 ) != 0x80;
    }

    //  [F0]     [90..BF] [80..BF] [80..BF]
    //  [F1..F3] [80..BF] [80..BF] [80..BF]
    //  [F4]     [80..8F] [80..BF] [80..BF]
    //  only check 80-be range here, the [0xf0,0x80...] and [0xf4,0x90-...]
    //  will be checked by Surrogate.neededFor(uc)
    private static boolean isMalformed4 ( int b2, int b3, int b4 ) {
        return ( b2 & 0xc0 ) != 0x80 || ( b3 & 0xc0 ) != 0x80 ||
                ( b4 & 0xc0 ) != 0x80;
    }



    private final void utf8MultiByte (final int b1, final CharBuf builder) {

        boolean ok = true;

        if ( ( b1 >> 5 ) == -2 ) {
            int b2;

            ok = hasMore () || die ( "unable to parse 2 byte utf 8 - b2" );
            __index++;
            b2 = this.charArray[ __index ];

            if ( isMalformed2 ( b1, b2 ) ) {
                builder.addChar ( '#' );
            } else {
                builder.addChar ( ( ( b1 << 6 ) ^ b2 ) ^ 0x0f80 );
            }
        } else if ( ( b1 >> 4 ) == -2 ) {
            int b2;
            int b3;

            ok = hasMore () || die ( "unable to parse 3 byte utf 8 - b2" );
            __index++;
            b2 = this.charArray[ __index ];
            ok = hasMore () || die ( "unable to parse 3 byte utf 8 - b3" );
            __index++;
            b3 = this.charArray[ __index ];

            if ( isMalformed3 ( b1, b2, b3 ) ) {
                builder.addChar ( '#' );
            } else {
                builder.addChar ( ( ( b1 << 12 ) ^ ( b2 << 6 ) ^ b3 ) ^ 0x1f80 );
            }
        } else if ( ( b1 >> 3 ) == -2 ) {
            int b2;
            int b3;
            int b4;

            ok = hasMore () || die ( "unable to parse 4 byte utf 8 - b2" );
            __index++;
            b2 = this.charArray[ __index ];
            ok = hasMore () || die ( "unable to parse 4 byte utf 8 - b3" );
            __index++;
            b3 = this.charArray[ __index ];
            ok = hasMore () || die ( "unable to parse 4 byte utf 8 - b4" );
            __index++;
            b4 = this.charArray[ __index ];

            int uc = ( ( b1 & 0x07 ) << 18 ) |
                    ( ( b2 & 0x3f ) << 12 ) |
                    ( ( b3 & 0x3f ) << 6 ) |
                    ( b4 & 0x3f );

            if ( isMalformed4 ( b2, b3, b4 ) && !Surrogate.neededFor ( uc ) ) {
                builder.addChar ( '#' );
            } else {

                final char high = Surrogate.high ( uc );
                final char low = Surrogate.low ( uc );

                builder.addChar ( high );
                builder.addChar ( low );

            }
        }

    }

}

