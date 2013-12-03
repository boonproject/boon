package org.boon.json;


import org.boon.core.reflection.Reflection;
import org.boon.primitive.Byt;
import org.boon.primitive.CharBuf;

import java.util.*;


public class JsonParserCharArray {

    private char[] charArray;
    private int __index;
    private char __currentChar;
    private char __lastChar;


    private static final char LETTER_B = 'b';
    private static final char NEW_LINE = '\n';

    private static final char RETURN = '\r';

    private static final char SPACE = ' ';
    private static final char ESCAPE = '\\';
    private static final char FORWARD_SLASH = '/';


    private static final char TAB = '\t';
    private static final char BELL = '\b';
    private static final char FORM_FEED = '\f';
    private static final char COMMA = ',';

    private static final char COLON = ':';

    private static final char OPEN_CURLY = '{';

    private static final char CLOSED_CURLY = '}';

    private static final char OPEN_BRACKET = '[';

    private static final char CLOSED_BRACKET = ']';


    private static final char DOUBLE_QUOTE = '"';

    private static final char LETTER_N = 'n';


    private static final char LETTER_U = 'u';

    private static final char LETTER_L = 'l';

    private static final char LETTER_T = 't';

    private static final char LETTER_R = 'r';

    private static final char LETTER_E = 'e';
    private static final char LETTER_BIG_E = 'E';

    private static final char LETTER_F = 'f';

    private static final char LETTER_A = 'a';


    private static final char LETTER_S = 's';

    private static final char ALPHA_0 = '0';
    private static final char ALPHA_1 = '1';
    private static final char ALPHA_2 = '2';
    private static final char ALPHA_3 = '3';
    private static final char ALPHA_4 = '4';
    private static final char ALPHA_5 = '5';
    private static final char ALPHA_6 = '6';
    private static final char ALPHA_7 = '7';
    private static final char ALPHA_8 = '8';
    private static final char ALPHA_9 = '9';
    private static final char MINUS = '-';
    private static final char PLUS = '+';

    private static final char DECIMAL_POINT = '.';


    public static Object parse ( char[] cs ) {
        JsonParserCharArray p = new JsonParserCharArray ();
        return p.decode ( cs );

    }


    public static Map<String, Object> parseMap ( char[] cs ) {
        JsonParserCharArray p = new JsonParserCharArray ();
        return ( Map<String, Object> ) p.decode ( cs );
    }


    public static <T> T parseInto ( T object, char[] cs ) {
        Map<String, Object> objectMap = parseMap ( cs );
        return Reflection.fromMap ( objectMap, object );
    }


    @SuppressWarnings ( "unchecked" )
    private Object decode ( char[] cs ) {
        charArray = cs;
        Object root = null;
        root = decodeValue ();
        return root;
    }


    private final boolean hasMore () {
        return __index + 1 < charArray.length;
    }

    private char nextChar () {

        try {
            if ( hasMore () ) {
                __lastChar = __currentChar;
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
        buf.addLine ( "The last character read was " + charDescription ( __lastChar ) );
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

        CharBuf buf = CharBuf.create ( 20 );

        loop:
        for ( index = __index; index < charArray.length; index++, count++ ) {
            __currentChar = charArray[ index ];

            switch ( __currentChar ) {
                case SPACE:
                case TAB:
                case NEW_LINE:
                case RETURN:
                    __index = index + 1;
                    break loop;

                case COMMA:
                    break loop;

                case CLOSED_BRACKET:
                    break loop;

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
                case MINUS:
                    buf.addChar ( __currentChar );
                    continue loop;

                case DECIMAL_POINT:
                    doubleFloat = true;
                    countDecimalPoint++;

                    if ( countDecimalPoint > 1 ) {
                        throw new JsonException ( exceptionDetails ( "number has more than one decimal point" ) );
                    }

                    buf.addChar ( __currentChar );
                    continue loop;

                case LETTER_E:
                case LETTER_BIG_E:
                    doubleFloat = true;
                    eCount++;
                    if ( eCount > 1 ) {
                        throw new JsonException ( exceptionDetails ( "number has more than one exp definition" ) );
                    }

                    buf.addChar ( __currentChar );
                    continue loop;

                case PLUS:
                    doubleFloat = true;
                    plusCount++;
                    if ( plusCount > 1 ) {
                        throw new JsonException ( exceptionDetails ( "number has more than one plus sign" ) );
                    }
                    if ( eCount == 0 ) {
                        throw new JsonException ( exceptionDetails ( "plus sign must come after exp" ) );

                    }

                    buf.addChar ( __currentChar );
                    continue loop;

            }

            complain ( "expecting number char but got current char " + charDescription ( __currentChar ) );
        }

        __index = index;


        Object value = null;
        if ( doubleFloat ) {
            value = buf.doubleValue ();
        } else {
            value = buf.toIntegerWrapper ();
        }

        skipWhiteSpace ();

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
        throw new JsonException ( exceptionDetails ( "null not parse properly" ) );
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

        CharBuf builder = CharBuf.create ( 20 );

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
                    builder.addChar ( __currentChar );

            }
        }


        if ( __index < charArray.length ) {
            __index++;
        }

        return builder.toString ();
    }

    private String decodeKeyName () {
        return decodeString ();

    }

    private List decodeJsonArray () {
        if ( __currentChar == '[' ) {
            this.nextChar ();
        }

        skipWhiteSpace ();

        /* the list might be empty  */
        if ( __currentChar == ']' ) {
            this.nextChar ();
            return Collections.emptyList ();
        }


        List<Object> list = new ArrayList<> ();


        int arrayIndex = 0;

        do {
            skipWhiteSpace ();

            Object arrayItem = decodeValue ();

            if ( arrayItem == null ) {
                list.add ( null ); //JSON null detected
            } else {
                list.add ( arrayItem );
            }

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


}

