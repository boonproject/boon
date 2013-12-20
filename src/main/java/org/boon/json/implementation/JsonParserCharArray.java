package org.boon.json.implementation;

import org.boon.IO;
import org.boon.core.reflection.FastStringUtils;
import org.boon.core.reflection.Reflection;
import org.boon.json.JsonException;
import org.boon.json.JsonParser;
import org.boon.json.internal.JsonLazyLinkedMap;
import org.boon.primitive.CharBuf;
import org.boon.primitive.CharScanner;
import org.boon.primitive.Chr;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.boon.primitive.CharScanner.*;

/**
 * Converts an input JSON String into Java objects works with String or char array
 * as input. Produces an Object which can be any of the basic JSON types mapped
 * to Java.
 * <p/>
 * TODO char[] val = value;     avoid getfield opcode  for all
 */
public class JsonParserCharArray extends BaseJsonParser implements JsonParser {

    protected char[] charArray;
    protected int __index;
    protected char __currentChar;


    protected Object decodeFromChars( char[] cs ) {
        __index = 0;
        charArray = cs;
        init ();
        Object value = decodeValue ();
        return value;
    }


<<<<<<< HEAD
    protected final Object decodeFromString ( String cs ) {
=======
    protected final Object decodeFromString( String cs ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return decodeFromChars ( FastStringUtils.toCharArray ( cs ) );
    }


    protected final Object decodeFromBytes( byte[] bytes ) {
        final char[] chars = FastStringUtils.toCharArrayFromBytes ( bytes );
        return decodeFromChars ( chars );
    }


    protected final boolean hasMore() {
        return __index + 1 < charArray.length;
    }

    protected final char nextChar() {

        try {
            if ( hasMore () ) {
                __index++;
                return __currentChar = charArray[__index];
            } else {
                return '\u0000';
            }
        } catch ( Exception ex ) {
            throw new JsonException ( exceptionDetails ( "unable to advance character" ), ex );
        }
    }


    protected String exceptionDetails( String message ) {
        CharBuf buf = CharBuf.create ( 255 );

        buf.addLine ( message );


        buf.addLine ( "" );
        buf.addLine ( "The current character read is " + charDescription ( __currentChar ) );


        buf.addLine ( message );

        int line = 0;
        int lastLineIndex = 0;

        for ( int i = 0; i < __index && i < charArray.length; i++ ) {
<<<<<<< HEAD
            if ( charArray[ i ] == '\n' ) {
=======
            if ( charArray[i] == '\n' ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
                line++;
                lastLineIndex = i + 1;
            }
        }

        int count = 0;

        for ( int i = lastLineIndex; i < charArray.length; i++, count++ ) {
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
        for ( int i = 0; i < ( __index - lastLineIndex - 1 ); i++ ) {
            buf.add ( '.' );
        }
        buf.add ( '^' );

        return buf.toString ();
    }

    protected final void skipWhiteSpace() {


        label:
        for (; __index < this.charArray.length; __index++ ) {
            __currentChar = charArray[__index];
            switch ( __currentChar ) {
                case ' ':
                    continue label;

                case '\n':
                    continue label;

                case '\r':
                    continue label;

                case '\t':
                    continue label;

                default:
                    break label;

            }
        }

    }

    protected final Object decodeJsonObject() {


        if ( __currentChar == '{' )
            this.nextChar ();

        JsonLazyLinkedMap map = null;
        if ( heavyCache ) {
            map = createMap ();
        } else {
            map = new JsonLazyLinkedMap ();
        }

        for (; __index < this.charArray.length; __index++ ) {

            skipWhiteSpace ();


            if ( __currentChar == '"' ) {

                String key =
                        decodeString ();

                if ( internKeys ) {
                    String keyPrime = internedKeysCache.get ( key );
                    if ( keyPrime == null ) {
                        key = key.intern ();
                        internedKeysCache.put ( key, key );
                    } else {
                        key = keyPrime;
                    }
                }

                skipWhiteSpace ();

                if ( __currentChar != ':' ) {

                    complain ( "expecting current character to be " + charDescription ( __currentChar ) + "\n" );
                }
                this.nextChar (); // skip past ':'

                Object value = decodeValueInternal ();

                skipWhiteSpace ();
                map.put ( key, value );


            }
            if ( __currentChar == '}' ) {
                __index++;
                break;
            } else if ( __currentChar == ',' ) {
                continue;
            } else {
                complain (
                        "expecting '}' or ',' but got current char " + charDescription ( __currentChar ) );

            }
        }


        if ( heavyCache ) {
            return prepareMap ( map );
        } else {
            return map;
        }
    }


    protected final void complain( String complaint ) {
        throw new JsonException ( exceptionDetails ( complaint ) );
    }


<<<<<<< HEAD
    protected Object decodeValue () {
=======
    protected Object decodeValue() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return decodeValueInternal ();
    }

    private final Object decodeValueInternal() {
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


    protected final Object decodeNumber() {


        boolean doubleFloat = false;

        boolean minus = false;

        boolean simple = true;

        int sign = 1;

        int index;

        int digitsPastPoint = 0;


        __currentChar = charArray[__index];

        if ( __currentChar == '-' ) {
            minus = true;
            __index++;
            sign = -1;
        }

        int startIndex = __index;


        loop:
        for ( index = __index; index < charArray.length; index++ ) {
            __currentChar = charArray[index];

            if ( doubleFloat ) {
                digitsPastPoint++;
            }


            switch ( __currentChar ) {
                case ' ':
                    __index = index + 1;
                    break loop;

                case '\t':
                    __index = index + 1;
                    break loop;

                case '\n':
                    __index = index + 1;
                    break loop;

                case '\r':
                    __index = index + 1;
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

                    simple = false;
                    doubleFloat = true;
                    continue loop;

                case 'e':
                    simple = false;
                    doubleFloat = true;
                    continue loop;

                case 'E':

                    simple = false;
                    doubleFloat = true;
                    continue loop;

                case '.':
                    doubleFloat = true;
                    continue loop;

            }

            complain ( "expecting number char but got current char " + charDescription ( __currentChar ) );
        }

        __index = index;


        Object value;
        if ( doubleFloat ) {
            value = CharScanner.simpleDouble ( this.charArray, simple, minus, digitsPastPoint - 1, startIndex, __index );
        } else {

            if ( isInteger ( this.charArray, startIndex, __index - startIndex, minus ) ) {
                value = parseInt ( charArray, startIndex, __index - startIndex ) * sign;
            } else {
                value = parseLong ( charArray, startIndex, __index - startIndex ) * sign;
            }

        }

        skipWhiteSpace ();

        return value;

    }


    protected static final char[] NULL = Chr.chars ( "null" );

    protected final Object decodeNull() {

        if ( __index + NULL.length <= charArray.length ) {
            if ( charArray[__index] == 'n' &&
                    charArray[++__index] == 'u' &&
                    charArray[++__index] == 'l' &&
                    charArray[++__index] == 'l' ) {
                nextChar ();
                return null;
            }
        }
        throw new JsonException ( exceptionDetails ( "null not parse properly" ) );
    }


    protected static final char[] TRUE = Chr.chars ( "true" );

<<<<<<< HEAD
    protected final boolean decodeTrue () {
=======
    protected final boolean decodeTrue() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc

        if ( __index + TRUE.length <= charArray.length ) {
            if ( charArray[__index] == 't' &&
                    charArray[++__index] == 'r' &&
                    charArray[++__index] == 'u' &&
                    charArray[++__index] == 'e' ) {

                nextChar ();
                return true;

            }
        }

        throw new JsonException ( exceptionDetails ( "true not parsed properly" ) );
    }


    protected static char[] FALSE = Chr.chars ( "false" );

<<<<<<< HEAD
    protected final boolean decodeFalse () {
=======
    protected final boolean decodeFalse() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc

        if ( __index + FALSE.length <= charArray.length ) {
            if ( charArray[__index] == 'f' &&
                    charArray[++__index] == 'a' &&
                    charArray[++__index] == 'l' &&
                    charArray[++__index] == 's' &&
                    charArray[++__index] == 'e' ) {
                nextChar ();
                return false;
            }
        }
        throw new JsonException ( exceptionDetails ( "false not parsed properly" ) );
    }


<<<<<<< HEAD
    protected final String decodeString () {

        __currentChar = charArray[ __index ];
=======
    protected final String decodeString() {

        __currentChar = charArray[__index];
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc

        if ( __index < charArray.length && __currentChar == '"' ) {
            __index++;

        }


        final int startIndex = __index;


        boolean escape = false;
        boolean hasEscaped = false;

        done:
        for (; __index < this.charArray.length; __index++ ) {
            __currentChar = charArray[__index];
            switch ( __currentChar ) {

                case '"':
                    if ( !escape ) {
                        break done;
                    } else {
                        escape = false;
                        continue;
                    }


                case '\\':
                    hasEscaped = true;
                    escape = true;
                    continue;

            }
            escape = false;
        }

        String value = null;
        if ( hasEscaped ) {
            value = JsonStringDecoder.decodeForSure ( charArray, startIndex, __index );
        } else {
            value = new String ( charArray, startIndex, ( __index - startIndex ) );
        }

        if ( __index < charArray.length ) {
            __index++;
        }

        return value;
    }

<<<<<<< HEAD
    protected final List decodeJsonArray () {
=======
    protected final List decodeJsonArray() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        if ( __currentChar == '[' ) {
            this.nextChar ();
        }


        skipWhiteSpace ();


        /* the list might be empty  */
        if ( __currentChar == ']' ) {
            this.nextChar ();
            return Collections.EMPTY_LIST;
        }

        ArrayList<Object> list;
        if ( heavyCache ) {
            list = createList ();
        } else {
            list = new ArrayList ();
        }

        do {

            skipWhiteSpace ();

            Object arrayItem = decodeValueInternal ();

            list.add ( arrayItem );


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
                                " on array index of %s \n", charString, list.size () )
                );

            }
        } while ( this.hasMore () );


        if ( heavyCache ) {
            return prepareList ( list );
        } else {
            return list;
        }
    }


    @Override
    public final <T> T parse( Class<T> type, String str ) {
        return ( T ) this.decodeFromString ( str );
    }


    @Override
<<<<<<< HEAD
    public final <T> T parse ( Class<T> type, byte[] value ) {
=======
    public final <T> T parse( Class<T> type, byte[] value ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return ( T ) this.decodeFromBytes ( value );
    }


    @Override
<<<<<<< HEAD
    public final <T> T parseDirect ( Class<T> type, byte[] value ) {
=======
    public final <T> T parseDirect( Class<T> type, byte[] value ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        if ( value.length < 20_000 ) {
            CharBuf builder = CharBuf.createFromUTF8Bytes ( value );
            return parse ( type, builder.toCharArray () );
        } else {
            return this.parse ( type, new ByteArrayInputStream ( value ) );
        }
    }

    @Override
    public final <T> T parseAsStream ( Class<T> type, byte[] value ) {
        return this.parse ( type, new ByteArrayInputStream ( value ) );
    }


    @Override
    public final <T> T parse( Class<T> type, CharSequence charSequence ) {
        return parse ( type, charSequence.toString () );
    }

    @Override
<<<<<<< HEAD
    public final <T> T parse ( Class<T> type, char[] chars ) {
=======
    public final <T> T parse( Class<T> type, char[] chars ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        if ( type == Map.class || type == List.class ) {
            return ( T ) this.decodeFromChars ( chars );
        } else {
            Map<String, Object> objectMap = ( Map<String, Object> ) this.decodeFromChars ( chars );
            return Reflection.fromMap ( objectMap, type );
        }
    }


    private CharBuf fileInputBuf;

    @Override
    public final <T> T parse ( Class<T> type, Reader reader ) {

        fileInputBuf = IO.read ( reader, fileInputBuf, 256 );
        return parse ( type, fileInputBuf.readForRecycle () );

    }


    @Override
    public final <T> T parse( Class<T> type, InputStream input ) {
        fileInputBuf = IO.read ( input, fileInputBuf, StandardCharsets.UTF_8, 256 );
        return parse ( type, fileInputBuf.readForRecycle () );
    }


    @Override
    public final <T> T parse ( Class<T> type, InputStream input, Charset charset ) {
        fileInputBuf = IO.read ( input, fileInputBuf, charset, 256 );
        return parse ( type, fileInputBuf.readForRecycle () );
    }


}