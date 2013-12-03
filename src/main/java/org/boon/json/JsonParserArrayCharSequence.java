package org.boon.json;

import org.boon.core.reflection.Reflection;
import org.boon.primitive.CharBuf;
import org.boon.primitive.Chr;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.boon.json.ParserState.*;
import static org.boon.json.ParserState.END_LIST_ITEM;

public class JsonParserArrayCharSequence {


        private CharSequence charSequence;
        private int __index;
        private char __currentChar;
        private char __lastChar;

        private Map<String, Object> lastObject;
        private List<Object> lastList;
        private ParserState state = START;
        private ParserState lastState = START;

        private final boolean encodeStrings;

        private JsonParserArrayCharSequence ( boolean encodeStrings ) {
            this.encodeStrings = encodeStrings;

        }

        public static Object parse( CharSequence cs ) {
            JsonParserArrayCharSequence p = new JsonParserArrayCharSequence ( false );
            return p.decode ( cs );

        }


        public static <T> T parseInto( T object, CharSequence cs ) {
            Map<String, Object> objectMap = parseMap ( cs );
            return Reflection.fromMap ( objectMap, object );
        }

        public static <T> T parseInto( Class<T> clz, CharSequence cs ) {
            Map<String, Object> objectMap = parseMap ( cs );
            return Reflection.fromMap (objectMap, clz);
        }


        public static Object parseIntoJavaObject(  CharSequence cs ) {
            Map<String, Object> objectMap = parseMap ( cs );
            return Reflection.fromMap ( objectMap );
        }


        public static <T> T fullParseInto( T object, CharSequence cs ) {
            Map<String, Object> objectMap = fullParseMap ( cs );
            return Reflection.fromMap (objectMap, object);
        }

        public static <T> T fullParseInto( Class<T> clz, CharSequence cs ) {
            Map<String, Object> objectMap = fullParseMap ( cs );
            return Reflection.fromMap (objectMap, clz);
        }


        public static Object fullParseIntoJavaObject(  CharSequence cs ) {
            Map<String, Object> objectMap = fullParseMap ( cs );
            return Reflection.fromMap ( objectMap );
        }





        public static Map<String, Object> fullParseMap( CharSequence cs ) {
            JsonParserArrayCharSequence p = new JsonParserArrayCharSequence ( true );
            return ( Map<String, Object> ) p.decode ( cs );
        }




    public static Map<String, Object> parseMap( CharSequence cs ) {
        JsonParserArrayCharSequence p = new JsonParserArrayCharSequence ( false );
        return ( Map<String, Object> ) p.decode ( cs );
    }



    private Object decode( CharSequence cs ) {
            charSequence = cs;
            Object root = null;
            root = decodeValue ( );
            return root;
        }


        private final boolean hasMore( ) {
            return __index + 1 < charSequence.length ();
        }

        private final char nextChar( ) {

            try {
                if ( hasMore ( ) ) {
                    __lastChar = __currentChar;
                    __index++;
                    return __currentChar = charSequence.charAt ( __index);
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
                if ( charSequence.charAt ( i ) == '\n' ) {
                    line ++;
                    lastLineIndex = i + 1;
                }
            }

            int count = 0;

            for ( int i = lastLineIndex; i < charSequence.length(); i++, count++) {
                if ( charSequence.charAt ( i ) == '\n' ) {
                    break;
                }
            }



            buf.addLine ( "line number " + line + 1 );
            buf.addLine ( "index number " + __index );


            try {
                buf.addLine (  charSequence.subSequence ( lastLineIndex, count ));

            } catch ( Exception ex ) {

                try {
                    int index = ( __index - 10 < 0 ) ? 0 : __index - 10;

                    buf.addLine ( charSequence.subSequence (  index, __index ) );
                } catch ( Exception ex2 ) {
                    buf.addLine ( charSequence.subSequence ( 0, charSequence.length() ) );
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
            for (; __index < this.charSequence.length(); __index++ ) {
                __currentChar = charSequence.charAt ( __index);
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

        private Object decodeJsonObject( ) {

            if ( __currentChar == '{' && this.hasMore ( ) )
                this.nextChar ( );

            Map<String, Object> map = new LinkedHashMap<> ( );

            this.lastObject = map;

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
                    Object value = decodeValue ( );

                    skipWhiteSpace ( );

                    map.put ( key, value );


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
            return map;
        }

        private void complain( String complaint ) {
            throw new JsonException ( exceptionDetails ( complaint ) );
        }


        private Object decodeValue( ) {
            Object value = null;

            done:
            for (; __index < this.charSequence.length(); __index++ ) {
                __currentChar = charSequence.charAt ( __index);


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

        private Object decodeNumber( ) {

            int startIndex = __index;

            boolean doubleFloat = false;

            int index;
            int count = 0;
            int countDecimalPoint = 0;
            int eCount = 0;
            int plusCount = 0;

            loop:
            for ( index = __index; index < charSequence.length(); index++, count++ ) {
                __currentChar = charSequence.charAt ( index);
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

            String svalue = charSequence.subSequence (  startIndex, __index ).toString ();


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

            skipWhiteSpace ( );

            return value;

        }


        private static char[] NULL = Chr.chars ( "null" );

        private Object decodeNull( ) {

            if ( __index + NULL.length <= charSequence.length() ) {
                if ( charSequence.charAt (  __index ) == 'n' &&
                        charSequence.charAt (  ++__index ) == 'u' &&
                        charSequence.charAt (  ++__index ) == 'l' &&
                        charSequence.charAt (  ++__index ) == 'l' ) {
                    nextChar ();
                    return null;
                }
            }
            throw new JsonException ( exceptionDetails( "null not parse properly" ) );
        }

        private static char[] TRUE = Chr.chars ( "true" );

        private boolean decodeTrue( ) {

            if ( __index + TRUE.length <= charSequence.length() ) {
                if ( charSequence.charAt (  __index ) == 't' &&
                        charSequence.charAt (  ++__index ) == 'r' &&
                        charSequence.charAt (  ++__index ) == 'u' &&
                        charSequence.charAt (  ++__index ) == 'e' ) {

                    nextChar ( );
                    return true;

                }
            }

            throw new JsonException ( exceptionDetails ( "true not parsed properly" ) );
        }


        private static char[] FALSE = Chr.chars ( "false" );

        private boolean decodeFalse( ) {

            if ( __index + FALSE.length <= charSequence.length() ) {
                if ( charSequence.charAt (  __index ) == 'f' &&
                        charSequence.charAt (  ++__index ) == 'a' &&
                        charSequence.charAt (  ++__index ) == 'l' &&
                        charSequence.charAt (  ++__index ) == 's' &&
                        charSequence.charAt (  ++__index )  == 'e' ) {
                    nextChar ( );
                    return false;
                }
            }
            throw new JsonException (exceptionDetails( "false not parsed properly") );
        }

        private String decodeString( ) {
            String value = null;

            if ( __index < charSequence.length() && __currentChar == '"' ) {
                __index++;
            }

            final int startIndex = __index;

            done:
            for (; __index < this.charSequence.length(); __index++ ) {
                __currentChar = charSequence.charAt ( __index);
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
                        if ( __index < charSequence.length() ) {
                            __index++;
                        }
                        continue;

                }
            }

            value = encodeString ( startIndex, __index );

            if ( __index < charSequence.length() ) {
                __index++;
            }

            return value;
        }

        private String encodeString( int start, int to ) {
            return  encodeStrings  ?
                    JsonStringDecoder.decode ( charSequence.subSequence ( start, to).toString () )
                    : charSequence.subSequence (  start, to ).toString ();

        }

        private String decodeKeyName( ) {
            return decodeString ( );

        }

        private List decodeJsonArray( ) {
            if ( __currentChar == '[' ) {
                this.nextChar ( );
            }

            skipWhiteSpace ( );

            List<Object> list = new ArrayList<> ( );
            this.lastList = list;

        /* the list might be empty  */
            if ( __currentChar == ']' ) {
                this.nextChar ( );
                return list;
            }


            int arrayIndex = 0;

            do {
                skipWhiteSpace ( );

                setState ( START_LIST_ITEM );
                Object arrayItem = decodeValue ( );

                if ( arrayItem == null && state == END_NULL ) {
                    list.add ( null ); //JSON null detected
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
            return list;
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