package org.boon.json;

import org.boon.core.reflection.Reflection;
import org.boon.json.internal.Type;
import org.boon.json.internal.ValueInCharBuf;
import org.boon.primitive.CharBuf;
import org.boon.primitive.Chr;

import java.util.*;

import static org.boon.Boon.puts;
import static org.boon.json.ParserState.*;
import static org.boon.json.ParserState.END_LIST_ITEM;

public class JsonParserCharSequence {


    private CharSequence charSequence;
    private int __index;
    private char __currentChar;


    private final boolean decodeStrings;

    private JsonParserCharSequence ( boolean decodeStrings ) {
        this.decodeStrings = decodeStrings;

    }

    public static Object parse ( CharSequence cs ) {
        JsonParserCharSequence p = new JsonParserCharSequence ( false );
        return p.decode ( cs );

    }


    public static Object fullParse ( CharSequence cs ) {
        JsonParserCharSequence p = new JsonParserCharSequence ( true );
        return p.decode ( cs );

    }

    public static <T> T parseInto ( T object, CharSequence cs ) {
        Map<String, Object> objectMap = parseMap ( cs );
        return Reflection.fromMap ( objectMap, object );
    }

    public static <T> T parseInto ( Class<T> clz, CharSequence cs ) {
        Map<String, Object> objectMap = parseMap ( cs );
        return Reflection.fromMap ( objectMap, clz );
    }


    public static Object parseIntoJavaObject ( CharSequence cs ) {
        Map<String, Object> objectMap = parseMap ( cs );
        return Reflection.fromMap ( objectMap );
    }


    public static <T> T fullParseInto ( T object, CharSequence cs ) {
        Map<String, Object> objectMap = fullParseMap ( cs );
        return Reflection.fromMap ( objectMap, object );
    }

    public static <T> T fullParseInto ( Class<T> clz, CharSequence cs ) {
        Map<String, Object> objectMap = fullParseMap ( cs );
        return Reflection.fromMap ( objectMap, clz );
    }


    public static Object fullParseIntoJavaObject ( CharSequence cs ) {
        Map<String, Object> objectMap = fullParseMap ( cs );
        return Reflection.fromMap ( objectMap );
    }


    public static Map<String, Object> fullParseMap ( CharSequence cs ) {
        JsonParserCharSequence p = new JsonParserCharSequence ( true );
        return ( Map<String, Object> ) p.decode ( cs );
    }


    public static Map<String, Object> parseMap ( CharSequence cs ) {
        JsonParserCharSequence p = new JsonParserCharSequence ( false );
        return ( Map<String, Object> ) p.decode ( cs );
    }


    private Object decode ( CharSequence cs ) {
        charSequence = cs;
        return  decodeValue();
    }


    private final boolean hasMore () {
        return __index + 1 < charSequence.length ();
    }

    private final char nextChar() {

        try {
            if ( __index + 1 < charSequence.length() ) {
                __index++;
                return __currentChar = charSequence.charAt(__index);
            } else {
                return '\u0000';
            }
        } catch ( Exception ex ) {
            throw new JsonException (  exceptionDetails ( "unable to advance character"), ex);
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
            if ( charSequence.charAt ( i ) == '\n' ) {
                line++;
                lastLineIndex = i + 1;
            }
        }

        int count = 0;

        for ( int i = lastLineIndex; i < charSequence.length (); i++, count++ ) {
            if ( charSequence.charAt ( i ) == '\n' ) {
                break;
            }
        }


        buf.addLine ( "line number " + line + 1 );
        buf.addLine ( "index number " + __index );


        try {
            buf.addLine ( charSequence.subSequence ( lastLineIndex, count ) );

        } catch ( Exception ex ) {

            try {
                int index = ( __index - 10 < 0 ) ? 0 : __index - 10;

                buf.addLine ( charSequence.subSequence ( index, __index ) );
            } catch ( Exception ex2 ) {
                buf.addLine ( charSequence.subSequence ( 0, charSequence.length () ) );
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
        for (; __index < this.charSequence.length (); __index++ ) {
            __currentChar = charSequence.charAt ( __index );
            switch ( __currentChar ) {
                case '\n':
                    continue label;

                case '\r':
                    continue label;

                case ' ':
                    continue label;

                case '\t':
                    continue label;

                default:
                    break label;

            }
        }

    }

    private Object decodeJsonObject () {

        if ( __currentChar == '{' )
            this.nextChar ();

        Map<String, Object> map = new LinkedHashMap<> ();


        for (; __index < this.charSequence.length(); __index++ ) {

            skipWhiteSpace ();


            if ( __currentChar == '"' ) {
                String key = decodeString ();
                skipWhiteSpace ();

                if ( __currentChar != ':' ) {

                    complain ( "expecting current character to be " + charDescription ( __currentChar ) + "\n" );
                }
                this.nextChar (); // skip past ':'

                Object value = decodeValue ();

                skipWhiteSpace ();

                map.put ( key, value );



            }
            if ( __currentChar == '}' ) {
                this.nextChar ();
                break;
            } else if ( __currentChar == ',' ) {
                this.nextChar ();
                continue;
            } else {
                complain (
                        "expecting '}' or ',' but got current char " + charDescription ( __currentChar ) );

            }
        }
        return map;
    }

    private void complain ( String complaint ) {
        throw new JsonException ( exceptionDetails ( complaint ) );
    }


    private Object decodeValue () {
        Object value = null;

        skipWhiteSpace ();

        switch ( __currentChar ) {
            case '\n':
                break;

            case '\r':
                break;

            case ' ':
                break;

            case '\t':
                break;

            case '\b':
                break;

            case '\f':
                break;

            case '"':
                value = decodeString ();
                break;


            case 't':
                value = decodeTrue ();
                break;

            case 'f':
                value = decodeFalse ();
                break;

            case 'n':
                value = decodeNull ();
                break;

            case '[':
                value = decodeJsonArray ();
                break;

            case '{':
                value = decodeJsonObject ();
                break;

            case '1':
                value = decodeNumber ();
                break;

            case '2':
                value = decodeNumber ();
                break;

            case '3':
                value = decodeNumber ();
                break;

            case '4':
                value = decodeNumber ();
                break;

            case '5':
                value = decodeNumber ();
                break;

            case '6':
                value = decodeNumber ();
                break;

            case '7':
                value = decodeNumber ();
                break;

            case '8':
                value = decodeNumber ();
                break;

            case '9':
                value = decodeNumber ();
                break;

            case '0':
                value = decodeNumber ();
                break;

            case '-':
                value = decodeNumber ();
                break;

            default:
                throw new JsonException ( exceptionDetails ( "Unable to determine the " +
                        "current character, it is not a string, number, array, or object" ) );

        }

        return value;
    }



    private Object decodeNumber () {
        int startIndex = __index;

        boolean doubleFloat = false;


        loop:
        for (  ; __index < charSequence.length(); __index++ ) {
            __currentChar = charSequence.charAt ( __index );

            switch ( __currentChar ) {
                case ' ':
                    break loop;

                case '\t':
                    break loop;

                case '\n':
                    break loop;

                case '\r':
                    break loop;

                case ',':
                    break loop;

                case ']':
                    break loop;

                case '}':
                    break loop;

                case '1':
                    continue loop;

                case '2':
                    continue loop;

                case '3':
                    continue loop;

                case '4':
                    continue loop;

                case '5':
                    continue loop;

                case '6':
                    continue loop;

                case '7':
                    continue loop;

                case '8':
                    continue loop;

                case '9':
                    continue loop;

                case '0':
                    continue loop;

                case '-':
                    continue loop;


                case '+':
                    doubleFloat = true;
                    continue loop;

                case 'e':
                    doubleFloat = true;
                    continue loop;

                case 'E':
                    doubleFloat = true;
                    continue loop;

                case '.':
                    doubleFloat = true;
                    continue loop;

            }

            complain ( "expecting number char but got current char " + charDescription ( __currentChar ) );
        }

 ;

        Number number;
        if ( doubleFloat ) {
            number = Double.parseDouble (
                    charSequence.subSequence ( startIndex, __index ).toString () );
        } else {

            try {
                number = Integer.parseInt (
                        charSequence.subSequence ( startIndex, __index ).toString () );

            } catch (Exception ex) {
                number = Long.parseLong (
                        charSequence.subSequence ( startIndex, __index ).toString () );

            }
       }

        skipWhiteSpace ();

        return number;


    }


    private static char[] NULL = Chr.chars ( "null" );

    private Object decodeNull () {

        if ( __index + NULL.length <= charSequence.length () ) {
            if ( charSequence.charAt ( __index ) == 'n' &&
                    charSequence.charAt ( ++__index ) == 'u' &&
                    charSequence.charAt ( ++__index ) == 'l' &&
                    charSequence.charAt ( ++__index ) == 'l' ) {
                nextChar ();
                return null;
            }
        }
        throw new JsonException ( exceptionDetails ( "null not parse properly" ) );
    }

    private static char[] TRUE = Chr.chars ( "true" );

    private boolean decodeTrue () {

        if ( __index + TRUE.length <= charSequence.length () ) {
            if ( charSequence.charAt ( __index ) == 't' &&
                    charSequence.charAt ( ++__index ) == 'r' &&
                    charSequence.charAt ( ++__index ) == 'u' &&
                    charSequence.charAt ( ++__index ) == 'e' ) {

                nextChar ();
                return true;

            }
        }

        throw new JsonException ( exceptionDetails ( "true not parsed properly" ) );
    }


    private static char[] FALSE = Chr.chars ( "false" );

    private boolean decodeFalse () {

        if ( __index + FALSE.length <= charSequence.length () ) {
            if ( charSequence.charAt ( __index ) == 'f' &&
                    charSequence.charAt ( ++__index ) == 'a' &&
                    charSequence.charAt ( ++__index ) == 'l' &&
                    charSequence.charAt ( ++__index ) == 's' &&
                    charSequence.charAt ( ++__index ) == 'e' ) {
                nextChar ();
                return false;
            }
        }
        throw new JsonException ( exceptionDetails ( "false not parsed properly" ) );
    }

    private String decodeString () {
        ValueInCharBuf value = new ValueInCharBuf ( Type.STRING );

        __currentChar = charSequence.charAt ( __index);

        if ( __index < charSequence.length () && __currentChar == '"' ) {
            __index++;
        }

        final int startIndex = __index;

        boolean escape = false;

        done:
        for (; __index < this.charSequence.length (); __index++ ) {
            __currentChar = charSequence.charAt ( __index );
            switch ( __currentChar ) {

                case '"':
                    if ( !escape )  {
                        break done;
                    } else {
                        escape = false;
                        continue;
                    }


                case '\\':
                    escape = true;
                    continue;

            }
            escape = false;
        }




        value.startIndex = 0;
        value.endIndex = __index - startIndex;
        value.buffer = charSequence.subSequence ( startIndex, __index ).toString ().toCharArray ();
        value.decodeStrings = decodeStrings;

        if ( __index < charSequence.length () ) {
            __index++;
        }


        return value.stringValue ();
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

            if ( arrayItem == null) {
                list.add ( null ); //JSON null detected
            } else {
                list.add ( arrayItem );
            }

            arrayIndex++;


            skipWhiteSpace ();

            char c = __currentChar;

            if ( c == ',' ) {
                this.nextChar ();
                continue;
            } else if ( c == ']' ) {
                this.nextChar ();
                break;
            } else {
                String charString = charDescription ( c );

                complain (
                        String.format ( "expecting a ',' or a ']', " +
                                " but got \nthe current character of  %s " +
                                " on array index of %s \n", charString, arrayIndex )
                );

            }
        } while ( this.hasMore () );
        return list;
    }

    private String charDescription ( char c ) {
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