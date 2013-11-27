package org.boon.json;

import org.boon.json.internal.*;
import org.boon.primitive.CharBuf;
import org.boon.primitive.Chr;

import java.util.List;
import java.util.Map;

import static org.boon.json.ParserState.*;

/**
 * Converts an input JSON String into Java objects works with String or char array
 * as input. Produces an Object which can be any of the basic JSON types mapped
 * to Java.
 */
public class JsonLazyEncodeParser {

    private char[] charArray;
    private int __index;
    private char __currentChar;
    private char __lastChar;

    private Map<String, Object> lastObject;
    private List<Object> lastList;
    private ParserState state = START;
    private ParserState lastState = START;

    private JsonLazyEncodeParser () {

    }

    public static Object parse( String cs ) {
        JsonLazyEncodeParser p = new JsonLazyEncodeParser ( );
        return p.decode ( cs );

    }


    public static Map<String, Object> parseMap( String cs ) {
        JsonLazyEncodeParser p = new JsonLazyEncodeParser ( );
        return ( Map<String, Object> ) p.decode ( cs );
    }

    public static <T> List<T> parseList( Class<T> type, String cs ) {
        JsonLazyEncodeParser p = new JsonLazyEncodeParser ( );
        return ( List<T> ) p.decode ( cs );
    }






    public static Object parse( char[] cs ) {
        JsonLazyEncodeParser p = new JsonLazyEncodeParser ( );
        return p.decode ( cs );

    }

    public static Map<String, Object> parseMap( char [] cs ) {
        JsonLazyEncodeParser p = new JsonLazyEncodeParser ( );
        return ( Map<String, Object> ) p.decode ( cs );
    }

    public static <T> List<T> parseList( Class<T> type, char [] cs ) {
        JsonLazyEncodeParser p = new JsonLazyEncodeParser ( );
        return ( List<T> ) p.decode ( cs );
    }


    public static Number parseNumber( char[] cs ) {
        JsonLazyEncodeParser p = new JsonLazyEncodeParser ( );
        return ( Number ) p.decode ( cs );
    }


    @SuppressWarnings("unchecked")
    private Object decode( char[] cs ) {
        charArray = cs;
        Object root = null;
        root = decodeValue ( ).toValue ();
        return root;
    }


    private Object decode( String cs ) {
        charArray = cs.toCharArray ( );
        Object root = null;
        root = decodeValue ( ).toValue ();
        return root;
    }


    private final boolean hasMore( ) {
        return __index + 1 < charArray.length;
    }

    private final char nextChar( ) {

        try {
            if ( hasMore ( ) ) {
                __lastChar = __currentChar;
                __index++;
                return __currentChar = charArray[__index];
            }
            return __currentChar;

        } catch ( Exception ex ) {
            throw new RuntimeException ( exceptionDetails ( "failure in next " +
                    ex.getLocalizedMessage ( ) ), ex );

        }
    }


    private String exceptionDetails( String message ) {
        CharBuf buf = CharBuf.create ( 255 );

        buf.addLine ( message );

        buf.add ( state.toString ( ) ).addLine ( " is CURRENT STATE" );
        buf.add ( lastState.toString ( ) ).addLine ( " is LAST STATE" );

        buf.addLine ( "" );
        buf.addLine ( "The last character read was " + charDescription ( __lastChar ) );
        buf.addLine ( "The current character read is " + charDescription ( __currentChar ) );


        if ( lastObject != null ) {
            buf.addLine ( "The last object read was" );
            buf.addLine ( "------------------------" );
            buf.addLine ( lastObject.toString ( ) );
            buf.addLine ( "------------------------" );
        }

        if ( lastList != null ) {
            buf.addLine ( "The last array read was" );
            buf.addLine ( "------------------------" );
            buf.addLine ( lastList.toString ( ) );
            buf.addLine ( "------------------------" );
        }


        buf.addLine ( message );

        int line = 0;
        int lastLineIndex = 0;

        for ( int i = 0; i < __index; i++) {
            if ( charArray[i] == '\n' ) {
                line ++;
                lastLineIndex = i + 1;
            }
        }

        int count = 0;

        for ( int i = lastLineIndex; i < charArray.length; i++, count++) {
            if ( charArray[i] == '\n' ) {
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
        for ( int i = 0; i < (__index - lastLineIndex -1); i++ ) {
            buf.add ( '.' );
        }
        buf.add ( '^' );

        return buf.toString ( );
    }

    private void skipWhiteSpace( ) {


        label:
        for (; __index < this.charArray.length; __index++ ) {
            __currentChar = charArray[__index];
            switch ( __currentChar ) {
                case '\n':
                    continue label;
                case '\r':
                    continue label;

                case ' ':
                case '\t':
                case '\b':
                case '\f':
                    continue label;
                default:
                    break label;

            }
        }

    }

    private Value decodeJsonObject( ) {

        if ( __currentChar == '{' && this.hasMore ( ) )
            this.nextChar ( );

        JsonMap map = new JsonMap ( );

        this.lastObject = map;

        Value value = new Value(map);


        do {

            skipWhiteSpace ( );


            if ( __currentChar == '"' ) {
                String key = decodeKeyName ( );
                skipWhiteSpace ( );

                if ( __currentChar != ':' ) {

                    complain ( "expecting current character to be " + charDescription ( __currentChar ) + "\n" );
                }
                this.nextChar ( ); // skip past ':'
                skipWhiteSpace ( );

                setState ( START_OBJECT_ITEM );
                Value item = decodeValue ( );

                skipWhiteSpace ( );


                MapItemValue miv = new MapItemValue ();
                miv.name = key;
                miv.value = item;
                map.items.add (miv);

                setState ( END_OBJECT_ITEM );

                if ( !( __currentChar == '}' || __currentChar == ',' ) ) {
                    complain ( "expecting '}' or ',' but got current char " + charDescription ( __currentChar ) );
                }
            }
            if ( __currentChar == '}' ) {
                this.nextChar ( );
                break;
            } else if ( __currentChar == ',' ) {
                this.nextChar ( );
                continue;
            } else {
                complain (
                        "expecting '}' or ',' but got current char " + charDescription ( __currentChar ) );

            }
        } while ( this.hasMore ( ) );
        return value;
    }

    private void complain( String complaint ) {
        throw new JsonException ( exceptionDetails ( complaint ) );
    }


    private Value decodeValue( ) {
        Value value = null;

        done:
        for (; __index < this.charArray.length; __index++ ) {
            __currentChar = charArray[__index];


            switch ( __currentChar ) {
                case '\n':
                    break;

                case '\r':
                case ' ':
                case '\t':
                case '\b':
                case '\f':
                    break;

                case '"':
                    setState ( START_STRING );
                    value = decodeString ( );
                    setState ( END_STRING );
                    break done;


                case 't':
                    setState ( START_BOOLEAN );
                    value = decodeTrue ( );
                    setState ( END_BOOLEAN );
                    break done;

                case 'f':
                    setState ( START_BOOLEAN );
                    value = decodeFalse ( );
                    setState ( END_BOOLEAN );
                    break done;

                case 'n':
                    setState ( START_NULL );
                    value = decodeNull ( );
                    setState ( END_NULL );
                    break done;

                case '[':
                    setState ( START_LIST );
                    value = decodeJsonArray ( );
                    setState ( END_LIST );
                    break done;

                case '{':
                    setState ( START_OBJECT );
                    value = decodeJsonObject ( );
                    setState ( END_OBJECT );
                    break done;

                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                case '0':
                case '-':
                    setState ( START_NUMBER );
                    value = decodeNumber ( );
                    setState ( END_NUMBER );
                    break done;

                default:
                    throw new JsonException ( exceptionDetails ( "Unable to determine the " +
                            "current character, it is not a string, number, array, or object" ) );

            }
        }

        return value;
    }


    private void setState( ParserState state ) {
        this.lastState = this.state;
        this.state = state;
    }

    private Value decodeNumber( ) {

        int startIndex = __index;

        boolean doubleFloat = false;

        int index;
        int count = 0;
        int countDecimalPoint = 0;
        int eCount = 0;
        int plusCount = 0;

        loop:
        for ( index = __index; index < charArray.length; index++, count++ ) {
            __currentChar = charArray[index];
            char c = __currentChar;

            switch ( __currentChar ) {
                case ' ':
                case '\t':
                case '\n':
                case '\r':
                    __index = index + 1;
                    break loop;

                case ',':
                    if ( lastState == START_LIST_ITEM || lastState == START_OBJECT_ITEM ) {
                        break loop;
                    } else {
                        throw new JsonException ( exceptionDetails("Unexpected comma token in parse number") );
                    }

                case ']':
                    if ( lastState == START_LIST_ITEM ) {
                        break loop;
                    } else {
                        throw new JsonException ( exceptionDetails("Unexpected close bracket token in parse number") );
                    }

                case '}':
                    if ( lastState == START_OBJECT_ITEM ) {
                        break loop;
                    } else {
                        throw new JsonException ( exceptionDetails("Unexpected close curly brace token in parse number") );
                    }

                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                case '0':
                case '-':
                    continue loop;

                case '.':
                    doubleFloat = true;
                    countDecimalPoint++;
                    if ( countDecimalPoint > 1 ) {
                        throw new JsonException (exceptionDetails( "number has more than one decimal point") );
                    }
                    continue loop;

                case 'e':
                case 'E':
                    doubleFloat = true;
                    eCount++;
                    if ( eCount > 1 ) {
                        throw new JsonException ( exceptionDetails("number has more than one exp definition"));
                    }
                    continue loop;

                case '+':
                    doubleFloat = true;
                    plusCount++;
                    if ( plusCount > 1 ) {
                        throw new JsonException (exceptionDetails( "number has more than one plus sign") );
                    }
                    if ( eCount == 0 ) {
                        throw new JsonException (exceptionDetails ( "plus sign must come after exp") );

                    }
                    continue loop;

            }

            complain ( "expecting number char but got current char " + charDescription ( c ) );
        }

        __index = index;

        Value value = new Value();
        value.buffer = this.charArray;
        value.startIndex = startIndex;
        value.endIndex = __index;

        if ( doubleFloat ) {
                value.type = Type.DOUBLE;
        } else {
                value.type = Type.INTEGER;
        }

        skipWhiteSpace ( );

        return value;

    }


    private static char[] NULL = Chr.chars ( "null" );

    private Value decodeNull( ) {

        if ( __index + NULL.length <= charArray.length ) {
            if ( charArray[__index] == 'n' &&
                    charArray[++__index] == 'u' &&
                    charArray[++__index] == 'l' &&
                    charArray[++__index] == 'l' ) {
                nextChar ();
                return Value.NULL;
            }
        }
        throw new JsonException ( exceptionDetails( "null not parse properly" ) );
    }

    private static char[] TRUE = Chr.chars ( "true" );

    private Value decodeTrue( ) {

        if ( __index + TRUE.length <= charArray.length ) {
            if ( charArray[__index] == 't' &&
                    charArray[++__index] == 'r' &&
                    charArray[++__index] == 'u' &&
                    charArray[++__index] == 'e' ) {

                nextChar ( );
                return Value.TRUE;

            }
        }

        throw new JsonException ( exceptionDetails ( "true not parsed properly" ) );
    }


    private static char[] FALSE = Chr.chars ( "false" );

    private Value decodeFalse( ) {

        if ( __index + FALSE.length <= charArray.length ) {
            if ( charArray[__index] == 'f' &&
                    charArray[++__index] == 'a' &&
                    charArray[++__index] == 'l' &&
                    charArray[++__index] == 's' &&
                    charArray[++__index] == 'e' ) {
                nextChar ( );
                return Value.FALSE;
            }
        }
        throw new JsonException (exceptionDetails( "false not parsed properly") );
    }

    private Value decodeString( ) {
        Value value = new Value(Type.STRING);

        final int startIndex = __index;
        if ( __index < charArray.length && __currentChar == '"' ) {
            __index++;
        }


        done:
        for (; __index < this.charArray.length; __index++ ) {
            __currentChar = charArray[__index];
            switch ( __currentChar ) {

                case '"':
                    break done;

                case '\t':
                case '\n':
                case '\r':
                case '\f':
                case '\b':
                    throw new JsonException ( exceptionDetails("illegal control character found " + __currentChar ));


                case '\\':
                    if ( __index < charArray.length ) {
                        __index++;
                    }
                    continue;

            }
        }

        value.startIndex = startIndex;
        value.endIndex = __index;
        value.buffer = charArray;

        if ( __index < charArray.length ) {
            __index++;
        }

        return value;
    }


    private String decodeKeyName( ) {
        return  decodeString ( ).toKey ();

    }

    private Value decodeJsonArray( ) {
        if ( __currentChar == '[' ) {
            this.nextChar ( );
        }

        skipWhiteSpace ( );

        List<Object> list = new JsonList ( );
        Value value = new Value(list);

        this.lastList = list;

        /* the list might be empty  */
        if ( __currentChar == ']' ) {
            this.nextChar ( );
            return value;
        }


        int arrayIndex = 0;

        do {
            skipWhiteSpace ( );

            setState ( START_LIST_ITEM );
            Value arrayItem = decodeValue ( );

            if ( arrayItem == null && state == END_NULL ) {
                list.add ( Value.NULL ); //JSON null detected
            } else if ( arrayItem == null ) {
                throw new JsonException ( exceptionDetails ( "array item was null") );
            } else {
                list.add ( arrayItem );
            }

            arrayIndex++;

            setState ( END_LIST_ITEM );

            skipWhiteSpace ( );

            char c = __currentChar;

            if ( c == ',' ) {
                this.nextChar ( );
                continue;
            } else if ( c == ']' ) {
                this.nextChar ( );
                break;
            } else {
                String charString = charDescription ( c );

                complain (
                        String.format ( "expecting a ',' or a ']', " +
                                " but got \nthe current character of  %s " +
                                " on array index of %s \n", charString, arrayIndex )
                );

            }
        } while ( this.hasMore ( ) );
        return value;
    }

    private String charDescription( char c ) {
        String charString;
        if ( c == ' ' ) {
            charString = "[SPACE]";
        } else if ( c == '\t' ) {
            charString = "[TAB]";

        } else if ( c == '\n' ) {
            charString = "[NEWLINE]";

        } else {
            charString = "'" + c + "'";
        }

        charString = charString + " with an int value of " + ( ( int ) c );
        return charString;
    }


}