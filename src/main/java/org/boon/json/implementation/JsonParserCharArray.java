package org.boon.json.implementation;

import org.boon.core.reflection.FastStringUtils;
import org.boon.core.reflection.fields.FieldAccessMode;
import org.boon.core.reflection.fields.FieldsAccessor;
import org.boon.json.JsonException;
import org.boon.json.JsonParser;
import org.boon.core.LazyMap;
import org.boon.primitive.CharBuf;
import org.boon.primitive.CharScanner;
import org.boon.primitive.Chr;

import java.nio.charset.Charset;
import java.util.*;

import static org.boon.primitive.CharScanner.isInteger;

/**
 * Converts an input JSON String into Java objects works with String or char array
 * as input. Produces an Object which can be any of the basic JSON types mapped
 * to Java.
 * <p/>
 */
public class JsonParserCharArray extends BaseJsonParser implements JsonParser {

    protected char[] charArray;
    protected int __index;
    protected char __currentChar;



    public JsonParserCharArray(  ) {
        super( FieldAccessMode.create(FieldAccessMode.FIELD) );
    }

    public JsonParserCharArray( FieldAccessMode mode ) {
        super( FieldAccessMode.create(mode) );
    }

    public JsonParserCharArray( FieldsAccessor fieldsAccessor ) {
        super( fieldsAccessor );
    }


    protected Object decodeFromChars( char[] cs ) {
        __index = 0;
        charArray = cs;
        Object value = decodeValue();
        return value;
    }


    protected final Object decodeFromString( String cs ) {
        return decodeFromChars( FastStringUtils.toCharArray( cs ) );
    }


    protected final Object decodeFromBytes( byte[] bytes ) {
        final char[] chars = FastStringUtils.toCharArrayFromBytes( bytes, charset );
        return decodeFromChars( chars );
    }


    protected final Object decodeFromBytes( byte[] bytes, Charset charset ) {
        final char[] chars = FastStringUtils.toCharArrayFromBytes( bytes, charset );
        return decodeFromChars( chars );
    }

    protected final boolean hasMore() {
        return __index + 1 < charArray.length;
    }

    protected final char nextChar() {

        try {
            if ( hasMore() ) {
                __index++;
                return __currentChar = charArray[ __index ];
            } else {
                return '\u0000';
            }
        } catch ( Exception ex ) {
            throw new JsonException( exceptionDetails( "unable to advance character" ), ex );
        }
    }


    protected String exceptionDetails( String message ) {
        CharBuf buf = CharBuf.create( 255 );

        buf.addLine( message );


        buf.addLine( "" );
        buf.addLine( "The current character read is " + charDescription( __currentChar ) );


        buf.addLine( message );

        int line = 0;
        int lastLineIndex = 0;

        for ( int i = 0; i < __index && i < charArray.length; i++ ) {
            if ( charArray[ i ] == '\n' ) {
                line++;
                lastLineIndex = i + 1;
            }
        }

        int count = 0;

        for ( int i = lastLineIndex; i < charArray.length; i++, count++ ) {
            if ( charArray[ i ] == '\n' ) {
                break;
            }
        }


        buf.addLine( "line number " + line + 1 );
        buf.addLine( "index number " + __index );


        try {
            buf.addLine( new String( charArray, lastLineIndex, count ) );
        } catch ( Exception ex ) {

            try {
                int index = ( __index - 10 < 0 ) ? 0 : __index - 10;

                buf.addLine( new String( charArray, index, __index ) );
            } catch ( Exception ex2 ) {
                buf.addLine( new String( charArray, 0, charArray.length ) );
            }
        }
        for ( int i = 0; i < ( __index - lastLineIndex ); i++ ) {
            buf.add( '.' );
        }
        buf.add( '^' );

        return buf.toString();
    }



    private static int  skipWhiteSpaceFast( char [] array, int index ) {
        char c;
        for (; index< array.length; index++ ) {
            c = array [index];
            if ( c > 32 ) {

                return index;
            }
        }
        return index-1;
    }


    protected final void skipWhiteSpace() {
        __index = skipWhiteSpaceFast ( this.charArray, __index );
        this.__currentChar = this.charArray[__index];
    }

    protected final Object decodeJsonObject() {


        if ( __currentChar == '{' )  {
            __index++;
        }

        LazyMap map = new LazyMap ();

        for (; __index < this.charArray.length; __index++ ) {

            skipWhiteSpace();


            if ( __currentChar == '"' ) {

                String key =
                        decodeString();

                if ( internKeys ) {
                    String keyPrime = internedKeysCache.get( key );
                    if ( keyPrime == null ) {
                        key = key.intern();
                        internedKeysCache.put( key, key );
                    } else {
                        key = keyPrime;
                    }
                }

                skipWhiteSpace();

                if ( __currentChar != ':' ) {

                    complain( "expecting current character to be " + charDescription( __currentChar ) + "\n" );
                }
                __index++;

                skipWhiteSpace();

                Object value = decodeValueInternal();

                skipWhiteSpace();
                map.put( key, value );


            }
            if ( __currentChar == '}' ) {
                __index++;
                break;
            } else if ( __currentChar == ',' ) {
                continue;
            } else {
                complain(
                        "expecting '}' or ',' but got current char " + charDescription( __currentChar ) );

            }
        }


        return map;
    }


    protected final void complain( String complaint ) {
        throw new JsonException( exceptionDetails( complaint ) );
    }


    protected Object decodeValue() {
        return decodeValueInternal();
    }

    private final Object decodeValueInternal() {
        Object value = null;
        skipWhiteSpace ();

        switch ( __currentChar ) {

            case '"':
                value = decodeString();
                break;


            case 't':
                value = decodeTrue();
                break;

            case 'f':
                value = decodeFalse();
                break;

            case 'n':
                value = decodeNull();
                break;

            case '[':
                value = decodeJsonArray();
                break;




            case '{':
                value = decodeJsonObject();
                break;

            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                value = decodeNumber(false);
                break;
            case '-':
                value = decodeNumber(true);
                break;

            default:
                throw new JsonException( exceptionDetails( "Unable to determine the " +
                        "current character, it is not a string, number, array, or object" ) );

        }

        return value;
    }




    private final Object decodeNumber(boolean minus) {

        char[] array = charArray;

        final int startIndex = __index;
        int index =  __index;
        char currentChar;
        boolean doubleFloat = false;
        boolean simple = true;
        int digitsPastPoint = 0;
        int sign = 1;




        if ( minus ) {
            minus = true;
            sign = -1;
            nextChar ();
        }


        while (true) {
            currentChar = array[index];

            if ( doubleFloat ) {
                digitsPastPoint++;
            }
            if ( isNumberDigit ( currentChar )) {
                //noop
            } else if ( currentChar <= 32 ) { //white
                break;
            } else if ( isDelimiter ( currentChar ) ) {
                break;
            } else if ( isDecimalChar (currentChar) ) {
                doubleFloat = true;
                if (currentChar != '.') {
                    simple = false;
                }
            }
            index++;
            if (index   >= array.length) break;
        }

        __index = index;
        __currentChar = currentChar;

        return getNumberFromSpan ( startIndex, doubleFloat, simple, digitsPastPoint, minus, sign );
    }

    private final Object getNumberFromSpan ( int startIndex, boolean doubleFloat, boolean simple, int digitsPastPoint, boolean minus, int sign ) {
        Object value;
        if ( doubleFloat ) {
            value = CharScanner.simpleDouble ( this.charArray, simple, minus, digitsPastPoint - 1, startIndex, __index );
        } else {

            if ( isInteger( this.charArray, startIndex, __index - startIndex, minus ) ) {
                value = CharScanner.parseInt( charArray, startIndex, __index - startIndex ) * sign;
            } else {
                value =  CharScanner.parseLong( charArray, startIndex, __index - startIndex ) * sign;
            }

        }

        return value;
    }



    protected static final char[] NULL = Chr.chars( "null" );

    protected final Object decodeNull() {

        if ( __index + NULL.length <= charArray.length ) {
            if ( charArray[ __index ] == 'n' &&
                    charArray[ ++__index ] == 'u' &&
                    charArray[ ++__index ] == 'l' &&
                    charArray[ ++__index ] == 'l' ) {
                __index++;
                return null;
            }
        }
        throw new JsonException( exceptionDetails( "null not parse properly" ) );
    }


    protected static final char[] TRUE = Chr.chars( "true" );

    protected final boolean decodeTrue() {

        if ( __index + TRUE.length <= charArray.length ) {
            if ( charArray[ __index ] == 't' &&
                    charArray[ ++__index ] == 'r' &&
                    charArray[ ++__index ] == 'u' &&
                    charArray[ ++__index ] == 'e' ) {

                __index++;
                return true;

            }
        }

        throw new JsonException( exceptionDetails( "true not parsed properly" ) );
    }


    protected static char[] FALSE = Chr.chars( "false" );

    protected final boolean decodeFalse() {

        if ( __index + FALSE.length <= charArray.length ) {
            if ( charArray[ __index ] == 'f' &&
                    charArray[ ++__index ] == 'a' &&
                    charArray[ ++__index ] == 'l' &&
                    charArray[ ++__index ] == 's' &&
                    charArray[ ++__index ] == 'e' ) {
                __index++;
                return false;
            }
        }
        throw new JsonException( exceptionDetails( "false not parsed properly" ) );
    }



    private CharBuf builder = CharBuf.create( 20 );

    private String decodeString() {

        char[] array = charArray;
        int index = __index;
        char currentChar = charArray[index];

        if ( index < array.length && currentChar == '"' ) {
            index++;
        }

        final int startIndex = index;


        boolean encoded = hasEscapeChar ( array, index, indexHolder );
        index = indexHolder[0];



        String value = null;
        if ( encoded ) {
            index = findEndQuote ( array,  index);
            value = builder.decodeJsonString ( array, startIndex, index ).toString ();
            builder.recycle ();
        } else {
            value = new String( array, startIndex, ( index - startIndex ) );
        }

        if ( index < charArray.length ) {
            index++;
        }
        __index = index;
        return value;
    }


    protected final List decodeJsonArray() {
        if ( __currentChar == '[' ) {
            __index++;
        }


        skipWhiteSpace();


        /* the list might be empty  */
        if ( __currentChar == ']' ) {
            __index++;
            return Collections.EMPTY_LIST;
        }

        ArrayList<Object> list = new ArrayList();

        do {

            skipWhiteSpace();

            Object arrayItem = decodeValueInternal();

            list.add( arrayItem );


            skipWhiteSpace();

            char c = __currentChar;

            if ( c == ',' ) {
                __index++;
                continue;
            } else if ( c == ']' ) {
                __index++;
                break;
            } else {

                String charString = charDescription( c );

                complain(
                        String.format( "expecting a ',' or a ']', " +
                                " but got \nthe current character of  %s " +
                                " on array index of %s \n", charString, list.size() )
                );

            }
        } while ( this.hasMore() );

        return list;
    }


    @Override
    public final <T> T parse( Class<T> type, String str ) {

        T object = ( T ) this.decodeFromString( str );
        return convert( type, object );
    }


    @Override
    public final <T> T parse( Class<T> type, byte[] value ) {
        T object = ( T ) this.decodeFromBytes( value );
        return convert( type, object );
    }

    @Override
    public <T> T parse( Class<T> type, byte[] bytes, Charset charset ) {
        T object = ( T ) this.decodeFromBytes( bytes, charset );
        return convert( type, object );
    }




    @Override
    public Object parse ( char[] chars ) {
        return this.decodeFromChars( chars );
    }

    @Override
    public final <T> T parse( Class<T> type, char[] chars ) {
        T object = ( T ) this.decodeFromChars( chars );
        return convert( type, object );

    }

    @Override
    public Object parse ( byte[] bytes, Charset charset ) {
        return this.decodeFromBytes ( bytes );
    }


}