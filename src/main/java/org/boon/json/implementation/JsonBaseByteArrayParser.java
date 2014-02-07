package org.boon.json.implementation;

import org.boon.IO;
import org.boon.core.Typ;
import org.boon.core.reflection.MapObjectConversion;
import org.boon.core.reflection.fields.FieldsAccessor;
import org.boon.json.JsonException;
import org.boon.core.LazyMap;
import org.boon.primitive.Byt;
import org.boon.primitive.ByteScanner;
import org.boon.primitive.CharBuf;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.*;

import static org.boon.Exceptions.die;

/**
 * Created by rick on 12/15/13.
 */
public abstract class JsonBaseByteArrayParser extends BaseJsonParser {

    protected byte[] charArray;
    protected int __index;
    protected int __currentChar;


    protected static final int NEW_LINE = '\n';

    protected static final int RETURN = '\r';

    protected static final int SPACE = ' ';


    protected static final int TAB = '\t';
    protected static final int BELL = '\b';
    protected static final int FORM_FEED = '\f';

    protected static final int COLON = ':';

    protected static final int OPEN_CURLY = '{';


    protected static final int OPEN_BRACKET = '[';




    protected static final int LETTER_N = 'n';


    protected static final int LETTER_U = 'u';

    protected static final int LETTER_L = 'l';

    protected static final int LETTER_T = 't';

    protected static final int LETTER_R = 'r';


    protected static final int LETTER_F = 'f';

    protected static final int LETTER_A = 'a';


    protected static final int LETTER_S = 's';


    protected final CharBuf builder = CharBuf.create( 20 );


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




    private static int  skipWhiteSpaceFastBytes( byte [] array, int index ) {
        int c;
        for (; index< array.length; index++ ) {
            c = array [index];
            if ( c > 32 ) {

                return index;
            }
        }
        return index-1;
    }


    protected final void skipWhiteSpace() {
        __index = skipWhiteSpaceFastBytes ( this.charArray, __index );
        this.__currentChar = this.charArray[__index];
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
            return MapObjectConversion.fromMap ( objectMap, type );
        }

    }

    @Override
    public Object parse ( byte[] bytes ) {
        return this.decode ( bytes );
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
                    value = decodeString ();
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
                    value = decodeNumber(false);
                    break done;

                case MINUS:
                    value = decodeNumber(true);
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
                String key = decodeString();

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



    private final Object decodeNumber(boolean minus) {

        byte[] array = charArray;

        final int startIndex = __index;
        int index =  __index;
        int currentChar;
        boolean doubleFloat = false;
        boolean simple = true;
        int digitsPastPoint = 0;

        if (minus) {
            index++;
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

        return getNumberFromSpan ( startIndex, doubleFloat, simple, digitsPastPoint );
    }



    private final Object getNumberFromSpan ( int startIndex, boolean doubleFloat, boolean simple, int digitsPastPoint) {
        Object value;
        if ( doubleFloat ) {
            value = ByteScanner.simpleDouble ( this.charArray, simple,  digitsPastPoint - 1, startIndex, __index );
        } else {

            if ( ByteScanner.isInteger ( this.charArray, startIndex, __index - startIndex ) ) {
                value = ByteScanner.parseInt( charArray, startIndex, __index - startIndex );
            } else {
                value =  ByteScanner.parseLong( charArray, startIndex, __index - startIndex );
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



    protected abstract String decodeString();


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

        boolean foundEnd = false;



        int arrayIndex = 0;


        try {
            while ( this.hasMore() ) {
                skipWhiteSpace();

                Object arrayItem = decodeValue();

                list.add( arrayItem );

                arrayIndex++;

                __currentChar = this.charArray[__index];


                if ( __currentChar == COMMA ) {
                    this.nextChar();
                    continue;
                } else if ( __currentChar == CLOSED_BRACKET ) {
                    this.nextChar();
                    foundEnd = true;
                    break;
                }

                skipWhiteSpace();


                if ( __currentChar == COMMA ) {
                    this.nextChar();
                    continue;
                } else if ( __currentChar == CLOSED_BRACKET ) {
                    this.nextChar();
                    foundEnd = true;
                    break;
                } else {
                    String charString = charDescription( __currentChar );

                    complain(
                            String.format( "expecting a ',' or a ']', " +
                                    " but got \nthe current character of  %s " +
                                    " on array index of %s \n", charString, arrayIndex )
                    );

                }
            }
        }catch (Exception ex) {
            throw new JsonException( exceptionDetails( ex.getMessage() ), ex );
        }

        if (!foundEnd) {
            complain( "No end bracket found for JSON Array" );
        }
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



    @Override
    public Object parse ( char[] chars ) {
        return parse ( new String (chars) );
    }

    @Override
    public Object parse ( String string ) {
        return parse ( string.getBytes ( charset ) );
    }


    @Override
    public Object parse ( byte[] bytes, Charset charset ) {

        return parse (  bytes );
    }




}
