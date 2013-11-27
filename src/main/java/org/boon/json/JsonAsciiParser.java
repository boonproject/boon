package org.boon.json;

import org.boon.primitive.Byt;
import org.boon.primitive.CharBuf;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.boon.json.ParserState.*;
import static org.boon.json.ParserState.END_LIST_ITEM;

public class JsonAsciiParser {

    private byte[] charArray;
    private int __index;
    private int __currentChar;
    private int __lastChar;

    private Map<String, Object> lastObject;
    private List<Object> lastList;
    private ParserState state = START;
    private ParserState lastState = START;

    private static final int NEW_LINE = '\n';

    private static final int RETURN = '\r';

    private static final int SPACE = ' ';


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


    private final boolean encodeStrings;


    private JsonAsciiParser () {
        encodeStrings = false;

    }

    private JsonAsciiParser (boolean encodeStrings) {
        this.encodeStrings = encodeStrings;

    }

    public static Object parse ( String cs ) {
        JsonAsciiParser p = new JsonAsciiParser ();
        return p.decode ( cs );

    }


    public static Object fullParse ( String cs ) {
        JsonAsciiParser p = new JsonAsciiParser (true);
        return p.decode ( cs );

    }

    public static Map<String, Object> fullParseMap ( String cs ) {
        JsonAsciiParser p = new JsonAsciiParser ( true );
        return ( Map<String, Object> ) p.decode ( cs );
    }



    public static Map<String, Object> parseMap ( String cs ) {
        JsonAsciiParser p = new JsonAsciiParser (  );
        return ( Map<String, Object> ) p.decode ( cs );
    }

    public static Object parse ( byte[] cs ) {
        JsonAsciiParser p = new JsonAsciiParser ();
        return p.decode ( cs );

    }

    public static Map<String, Object> parseMap ( byte[] cs ) {
        JsonAsciiParser p = new JsonAsciiParser ();
        return ( Map<String, Object> ) p.decode ( cs );
    }


    @SuppressWarnings ( "unchecked" )
    private Object decode ( byte[] cs ) {
        charArray = cs;
        Object root = null;
        root = decodeValue ();
        return root;
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

        buf.add ( state.toString () ).addLine ( " is CURRENT STATE" );
        buf.add ( lastState.toString () ).addLine ( " is LAST STATE" );

        buf.addLine ( "" );
        buf.addLine ( "The last character read was " + charDescription ( __lastChar ) );
        buf.addLine ( "The current character read is " + charDescription ( __currentChar ) );


        if ( lastObject != null ) {
            buf.addLine ( "The last object read was" );
            buf.addLine ( "------------------------" );
            buf.addLine ( lastObject.toString () );
            buf.addLine ( "------------------------" );
        }

        if ( lastList != null ) {
            buf.addLine ( "The last array read was" );
            buf.addLine ( "------------------------" );
            buf.addLine ( lastList.toString () );
            buf.addLine ( "------------------------" );
        }


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
                case TAB:
                case BELL:
                case FORM_FEED:
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

        this.lastObject = map;

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

                setState ( START_OBJECT_ITEM );
                Object value = decodeValue ();

                skipWhiteSpace ();

                map.put ( key, value );


                setState ( END_OBJECT_ITEM );

                if ( !( __currentChar == CLOSED_CURLY || __currentChar == COMMA) ) {
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
                    setState ( START_STRING );
                    value = decodeString ();
                    setState ( END_STRING );
                    break done;


                case LETTER_T:
                    setState ( START_BOOLEAN );
                    value = decodeTrue ();
                    setState ( END_BOOLEAN );
                    break done;

                case LETTER_F:
                    setState ( START_BOOLEAN );
                    value = decodeFalse ();
                    setState ( END_BOOLEAN );
                    break done;

                case LETTER_N:
                    setState ( START_NULL );
                    value = decodeNull ();
                    setState ( END_NULL );
                    break done;

                case OPEN_BRACKET:
                    setState ( START_LIST );
                    value = decodeJsonArray ();
                    setState ( END_LIST );
                    break done;

                case OPEN_CURLY:
                    setState ( START_OBJECT );
                    value = decodeJsonObject ();
                    setState ( END_OBJECT );
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
                    setState ( START_NUMBER );
                    value = decodeNumber ();
                    setState ( END_NUMBER );
                    break done;

                default:
                    throw new JsonException ( exceptionDetails ( "Unable to determine the " +
                            "current character, it is not a string, number, array, or object" ) );

            }
        }

        return value;
    }


    private void setState ( ParserState state ) {
        this.lastState = this.state;
        this.state = state;
    }

    private Object decodeNumber () {

        int startIndex = __index;

        boolean doubleFloat = false;

        int index;
        int count = 0;
        int countDecimalPoint = 0;
        int eCount = 0;
        int plusCount = 0;

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
                    if ( lastState == START_LIST_ITEM || lastState == START_OBJECT_ITEM ) {
                        break loop;
                    } else {
                        throw new JsonException ( exceptionDetails ( "Unexpected comma token in parse number" ) );
                    }

                case CLOSED_BRACKET:
                    if ( lastState == START_LIST_ITEM ) {
                        break loop;
                    } else {
                        throw new JsonException ( exceptionDetails ( "Unexpected close bracket token in parse number" ) );
                    }

                case CLOSED_CURLY:
                    if ( lastState == START_OBJECT_ITEM ) {
                        break loop;
                    } else {
                        throw new JsonException ( exceptionDetails ( "Unexpected close curly brace token in parse number" ) );
                    }

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
                    doubleFloat = true;
                    eCount++;
                    if ( eCount > 1 ) {
                        throw new JsonException ( exceptionDetails ( "number has more than one exp definition" ) );
                    }
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
                    continue loop;

            }

            complain ( "expecting number char but got current char " + charDescription ( __currentChar ) );
        }

        __index = index;

        String svalue = new String ( this.charArray, startIndex, count, StandardCharsets.UTF_8 );


        Object value = null;
        try {
            if ( doubleFloat ) {
                value = Double.parseDouble ( svalue );
            } else {
                value = Integer.parseInt ( svalue );
            }
        } catch ( Exception ex ) {
            try {
                value = Long.parseLong ( svalue );
            } catch ( Exception ex2 ) {
                complain ( "expecting to decode a number but got value of " + svalue );
            }

        }

        skipWhiteSpace ();

        return value;

    }


    private static byte[] NULL = Byt.bytes ( "null" );

    private Object decodeNull () {

        if ( __index + NULL.length <= charArray.length ) {
            if ( charArray[ __index ] == LETTER_N &&
                    charArray[ ++__index ] ==  LETTER_U &&
                    charArray[ ++__index ] ==  LETTER_L &&
                    charArray[ ++__index ] ==  LETTER_L ) {
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

    private String decodeString () {
        String value = null;

        final int startIndex = __index;
        if ( __index < charArray.length && __currentChar == '"' ) {
            __index++;
        }


        done:
        for (; __index < this.charArray.length; __index++ ) {
            __currentChar = charArray[ __index ];
            switch ( __currentChar ) {

                case '"':
                    break done;

                case '\\':
                    if ( __index < charArray.length ) {
                        __index++;
                    }
                    continue;

            }
        }

        value = encodeString ( startIndex, __index );

        if ( __index < charArray.length ) {
            __index++;
        }

        return value;
    }

    private String encodeString ( int start, int to ) {
        return (encodeStrings) ? JsonStringDecoder.decode ( charArray, start, to ) :
                 new String( charArray, start + 1, ( to - start )-1 );
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
        this.lastList = list;

        /* the list might be empty  */
        if ( __currentChar == ']' ) {
            this.nextChar ();
            return list;
        }


        int arrayIndex = 0;

        do {
            skipWhiteSpace ();

            setState ( START_LIST_ITEM );
            Object arrayItem = decodeValue ();

            if ( arrayItem == null && state == END_NULL ) {
                list.add ( null ); //JSON null detected
            } else if ( arrayItem == null ) {
                throw new JsonException ( exceptionDetails ( "array item was null" ) );
            } else {
                list.add ( arrayItem );
            }

            arrayIndex++;

            setState ( END_LIST_ITEM );

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
            charString = "'" + (char)c + "'";
        }

        charString = charString + " with an int value of " + ( ( int ) c );
        return charString;
    }


}

