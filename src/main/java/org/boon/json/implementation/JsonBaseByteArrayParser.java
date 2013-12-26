package org.boon.json.implementation;

import org.boon.IO;
import org.boon.core.Typ;
import org.boon.core.reflection.Reflection;
import org.boon.json.JsonException;
import org.boon.core.LazyMap;
import org.boon.primitive.Byt;
import org.boon.primitive.ByteScanner;
import org.boon.primitive.CharBuf;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.boon.Exceptions.die;
import static org.boon.primitive.ByteScanner.isInteger;
import static org.boon.primitive.ByteScanner.parseInt;
import static org.boon.primitive.ByteScanner.parseLong;

/**
 * Created by rick on 12/15/13.
 */
public abstract class JsonBaseByteArrayParser extends BaseJsonParser {

    protected byte[] charArray;
    protected int __index;
    protected int __currentChar;


    protected static final int LETTER_B = 'b';
    protected static final int NEW_LINE = '\n';

    protected static final int RETURN = '\r';

    protected static final int SPACE = ' ';
    protected static final int ESCAPE = '\\';
    protected static final int FORWARD_SLASH = '/';


    protected static final int TAB = '\t';
    protected static final int BELL = '\b';
    protected static final int FORM_FEED = '\f';
    protected static final int COMMA = ',';

    protected static final int COLON = ':';

    protected static final int OPEN_CURLY = '{';

    protected static final int CLOSED_CURLY = '}';

    protected static final int OPEN_BRACKET = '[';

    protected static final int CLOSED_BRACKET = ']';


    protected static final int DOUBLE_QUOTE = '"';

    protected static final int LETTER_N = 'n';


    protected static final int LETTER_U = 'u';

    protected static final int LETTER_L = 'l';

    protected static final int LETTER_T = 't';

    protected static final int LETTER_R = 'r';

    protected static final int LETTER_E = 'e';
    protected static final int LETTER_BIG_E = 'E';

    protected static final int LETTER_F = 'f';

    protected static final int LETTER_A = 'a';


    protected static final int LETTER_S = 's';

    protected static final int ALPHA_0 = '0';
    protected static final int ALPHA_1 = '1';
    protected static final int ALPHA_2 = '2';
    protected static final int ALPHA_3 = '3';
    protected static final int ALPHA_4 = '4';
    protected static final int ALPHA_5 = '5';
    protected static final int ALPHA_6 = '6';
    protected static final int ALPHA_7 = '7';
    protected static final int ALPHA_8 = '8';
    protected static final int ALPHA_9 = '9';
    protected static final int MINUS = '-';
    protected static final int PLUS = '+';

    protected static final int DECIMAL_POINT = '.';

    protected final CharBuf builder = CharBuf.create( 20 );




    public JsonBaseByteArrayParser() {

    }


    protected final boolean hasMore() {
        return __index + 1 < charArray.length;
    }

    protected final int nextChar() {

        try {
            if ( hasMore() ) {
                __index++;
                return __currentChar = charArray[ __index ];
            }
            return __currentChar;

        } catch ( Exception ex ) {
            throw new RuntimeException( exceptionDetails( "failure in next " +
                    ex.getLocalizedMessage() ), ex );

        }
    }


    protected final String exceptionDetails( String message ) {
        CharBuf buf = CharBuf.create( 255 );

        buf.addLine( message );

        buf.addLine( "" );
        buf.addLine( "The current character read is " + charDescription( __currentChar ) );


        buf.addLine( message );

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


        buf.addLine( "line number " + line + 1 );
        buf.addLine( "index number " + __index );


        try {
            buf.addLine( new String( charArray, lastLineIndex, count, charset ) );
        } catch ( Exception ex ) {

            try {
                int index = ( __index - 10 < 0 ) ? 0 : __index - 10;

                buf.addLine( new String( charArray, index, __index, charset ) );
            } catch ( Exception ex2 ) {
                buf.addLine( new String( charArray, 0, charArray.length, charset ) );
            }
        }
        for ( int i = 0; i < ( __index - lastLineIndex - 1 ); i++ ) {
            buf.add( '.' );
        }
        buf.add( '^' );

        return buf.toString();
    }

    protected final void skipWhiteSpace() {


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


    protected String charDescription( int c ) {
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


    private Object decode( byte[] cs ) {
        charArray = cs;
        __index = 0;
        return decodeValue();
    }


    public <T> T parse( Class<T> type, String str ) {
        return this.parse( type, str.getBytes( charset ) );
    }

    public <T> T parse( Class<T> type, byte[] bytes ) {

        if ( type == Map.class || type == List.class || Typ.isBasicType (type) ) {
            return ( T ) this.decode( bytes );
        } else {
            Map<String, Object> objectMap = ( Map<String, Object> ) this.decode( bytes );
            return Reflection.fromMap( objectMap, type );
        }

    }


    public <T> T parse( Class<T> type, InputStream input ) {
        return parse( type, IO.input( input ) );
    }

    public <T> T parse( Class<T> type, CharSequence charSequence ) {
        return parse( type, charSequence.toString() );
    }

    public <T> T parse( Class<T> type, char[] chars ) {

        return parse( type, new String( chars ) );
    }


    public <T> T parse( Class<T> type, Reader reader ) {

        die( "you are using the wrong class" );
        return null;
    }


    public <T> T parse( Class<T> type, InputStream input, Charset charset ) {
        die( "you are using the wrong class" );
        return null;
    }


    protected void complain( String complaint ) {
        throw new JsonException( exceptionDetails( complaint ) );
    }


    protected final Object decodeValue() {
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
                    value = decodeString();
                    break done;


                case LETTER_T:
                    value = decodeTrue();
                    break done;

                case LETTER_F:
                    value = decodeFalse();
                    break done;

                case LETTER_N:
                    value = decodeNull();
                    break done;

                case OPEN_BRACKET:
                    value = decodeJsonArray();
                    break done;

                case OPEN_CURLY:
                    value = decodeJsonObject();
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
                    value = decodeNumber();
                    break done;

                default:
                    throw new JsonException( exceptionDetails( "Unable to determine the " +
                            "current character, it is not a string, number, array, or object" ) );

            }
        }

        return value;
    }


    protected final Object decodeJsonObject() {

        if ( __currentChar == OPEN_CURLY && this.hasMore() ) {
            this.nextChar();
        }

        skipWhiteSpace();

        if ( __currentChar == CLOSED_CURLY && this.hasMore() ) {
            this.nextChar();
            return Collections.EMPTY_MAP;

        }


        LazyMap map =  new LazyMap ();

        do {

            skipWhiteSpace();


            if ( __currentChar == DOUBLE_QUOTE ) {
                String key = decodeKeyName();

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

                if ( __currentChar != COLON ) {

                    complain( "expecting current character to be " + charDescription( __currentChar ) + "\n" );
                }
                this.nextChar(); // skip past COLON
                skipWhiteSpace();

                Object value = decodeValue();

                skipWhiteSpace();

                map.put( key, value );


                if ( !( __currentChar == CLOSED_CURLY || __currentChar == COMMA ) ) {
                    complain( "expecting '}' or ',' but got current char " + charDescription( __currentChar ) );
                }
            }
            if ( __currentChar == CLOSED_CURLY ) {
                this.nextChar();
                break;
            } else if ( __currentChar == COMMA ) {
                this.nextChar();
                continue;
            } else {
                complain(
                        "expecting '}' or ',' but got current char " + charDescription( __currentChar ) );

            }
        } while ( this.hasMore() );

        return map;
    }


    protected final Object decodeNumber() {

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
                case MINUS:
                    continue loop;


                case DECIMAL_POINT:
                    doubleFloat = true;
                    countDecimalPoint++;

                    if ( countDecimalPoint > 1 ) {
                        throw new JsonException( exceptionDetails( "number has more than one decimal point" ) );
                    }
                    continue loop;

                case LETTER_E:
                case LETTER_BIG_E:
                    simple = false;
                    doubleFloat = true;
                    eCount++;
                    if ( eCount > 1 ) {
                        throw new JsonException( exceptionDetails( "number has more than one exp definition" ) );
                    }
                    continue loop;

                case PLUS:
                    simple = false;
                    doubleFloat = true;
                    plusCount++;
                    if ( plusCount > 1 ) {
                        throw new JsonException( exceptionDetails( "number has more than one plus sign" ) );
                    }
                    if ( eCount == 0 ) {
                        throw new JsonException( exceptionDetails( "plus sign must come after exp" ) );

                    }
                    continue loop;

            }

            complain( "expecting number char but got current char " + charDescription( __currentChar ) );
        }

        __index = index;

        Object value;
        if ( doubleFloat ) {
            value = ByteScanner.simpleDouble( this.charArray, simple, minus, digitsPastPoint - 1, startIndex, __index );
        } else {

            if ( isInteger( this.charArray, startIndex, __index - startIndex, minus ) ) {
                value = ByteScanner.parseInt( charArray, startIndex, __index - startIndex ) * sign;
            } else {
                value =  ByteScanner.parseLong( charArray, startIndex, __index - startIndex ) * sign;
            }

        }


        return value;

    }


    protected final static byte[] NULL = Byt.bytes( "null" );

    protected final Object decodeNull() {

        if ( __index + NULL.length <= charArray.length ) {
            if ( charArray[ __index ] == LETTER_N &&
                    charArray[ ++__index ] == LETTER_U &&
                    charArray[ ++__index ] == LETTER_L &&
                    charArray[ ++__index ] == LETTER_L ) {
                nextChar();
                return null;
            }
        }
        throw new JsonException( exceptionDetails( "null not parsed properly" ) );
    }

    protected final static byte[] TRUE = Byt.bytes( "true" );

    protected final boolean decodeTrue() {

        if ( __index + TRUE.length <= charArray.length ) {
            if ( charArray[ __index ] == LETTER_T &&
                    charArray[ ++__index ] == LETTER_R &&
                    charArray[ ++__index ] == LETTER_U &&
                    charArray[ ++__index ] == LETTER_E ) {

                nextChar();
                return true;

            }
        }

        throw new JsonException( exceptionDetails( "true not parsed properly" ) );
    }


    protected final static byte[] FALSE = Byt.bytes( "false" );

    protected final boolean decodeFalse() {

        if ( __index + FALSE.length <= charArray.length ) {
            if ( charArray[ __index ] == LETTER_F &&
                    charArray[ ++__index ] == LETTER_A &&
                    charArray[ ++__index ] == LETTER_L &&
                    charArray[ ++__index ] == LETTER_S &&
                    charArray[ ++__index ] == LETTER_E ) {
                nextChar();
                return false;
            }
        }
        throw new JsonException( exceptionDetails( "false not parsed properly" ) );
    }


    protected final String decodeString() {


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
                            builder.addChar( '\n' );
                            continue loop;

                        case FORWARD_SLASH:
                            builder.addChar( '/' );
                            continue loop;

                        case DOUBLE_QUOTE:
                            builder.addChar( '"' );
                            continue loop;

                        case LETTER_F:
                            builder.addChar( '\f' );
                            continue loop;

                        case LETTER_T:
                            builder.addChar( '\t' );
                            continue loop;

                        case ESCAPE:
                            builder.addChar( '\\' );
                            continue loop;

                        case LETTER_B:
                            builder.addChar( '\b' );
                            continue loop;

                        case LETTER_R:
                            builder.addChar( '\r' );
                            continue loop;

                        case LETTER_U:

                            if ( __index + 4 < charArray.length ) {

                                CharBuf hex = CharBuf.create( 4 );
                                hex.addChar( charArray[ __index + 1 ] );

                                hex.addChar( charArray[ __index + 2 ] );

                                hex.addChar( charArray[ __index + 3 ] );

                                hex.addChar( charArray[ __index + 4 ] );
                                char unicode = ( char ) Integer.parseInt( hex.toString(), 16 );
                                builder.add( unicode );
                                __index += 4;
                            }
                            continue loop;
                    }
                default:
                    addChar();


            }
        }


        if ( __index < charArray.length ) {
            __index++;
        }

        String str = builder.toString();

        builder.readForRecycle();
        return str;
    }


    abstract protected void addChar();

    protected final String decodeKeyName() {
        return decodeString();

    }

    protected final List decodeJsonArray() {
        if ( __currentChar == OPEN_BRACKET ) {
            this.nextChar();
        }

        skipWhiteSpace();

                /* the list might be empty  */
        if ( __currentChar == CLOSED_BRACKET ) {
            this.nextChar();
            return Collections.EMPTY_LIST;
        }


        ArrayList<Object> list = new ArrayList();



        int arrayIndex = 0;

        do {
            skipWhiteSpace();

            Object arrayItem = decodeValue();

            list.add( arrayItem );

            arrayIndex++;


            skipWhiteSpace();


            if ( __currentChar == COMMA ) {
                this.nextChar();
                continue;
            } else if ( __currentChar == CLOSED_BRACKET ) {
                this.nextChar();
                break;
            } else {
                String charString = charDescription( __currentChar );

                complain(
                        String.format( "expecting a ',' or a ']', " +
                                " but got \nthe current character of  %s " +
                                " on array index of %s \n", charString, arrayIndex )
                );

            }
        } while ( this.hasMore() );


        return list;
    }


    public <T> T parseDirect( Class<T> type, byte[] value ) {
        return this.parse( type, value );
    }

    public <T> T parseAsStream( Class<T> type, byte[] value ) {
        return this.parse( type, new ByteArrayInputStream( value ) );
    }



    public <T> T parse( Class<T> type, byte[] bytes, Charset charset ) {
        return parse ( type, bytes );
    }


    public <T> T parseFile( Class<T> type, String fileName ) {
        return parse(type, IO.input ( fileName ));
    }
}
