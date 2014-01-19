package org.boon.json.implementation;

import org.boon.IO;
import org.boon.core.LazyMap;
import org.boon.core.reflection.FastStringUtils;
import org.boon.core.reflection.fields.FieldAccessMode;
import org.boon.core.reflection.fields.FieldsAccessor;
import org.boon.json.JsonException;
import org.boon.primitive.*;

import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.boon.primitive.CharScanner.doubleValue;
import static org.boon.primitive.CharScanner.isInteger;


/**
 * Converts an input JSON String into Java objects works with String or char array
 * as input. Produces an Object which can be any of the basic JSON types mapped
 * to Java.
 * <p/>
 */
public class JsonParserUsingCharacterSource extends BaseJsonParser {

    private  CharacterSource characterSource;

    public JsonParserUsingCharacterSource(  ) {
        super( FieldAccessMode.create ( FieldAccessMode.FIELD, true ) );
    }

    public JsonParserUsingCharacterSource( FieldAccessMode mode, boolean useAnnotations ) {
        super( FieldAccessMode.create(mode, useAnnotations) );
    }

    public JsonParserUsingCharacterSource( FieldsAccessor fieldsAccessor ) {
        super( fieldsAccessor );
    }






    //TODO you have to improve this.
    protected String exceptionDetails( String message ) {
        CharBuf buf = CharBuf.create( 255 );

        buf.addLine( message );


        buf.addLine( "" );
        buf.addLine( "The current character read is " + charDescription( characterSource.currentChar() ) );


        buf.addLine( message );



        return buf.toString();
    }


    protected final Object decodeJsonObject() {

        CharacterSource characterSource = this.characterSource;

        if ( characterSource.currentChar() == '{' )  {
            characterSource.nextChar();
        }

        LazyMap map = new LazyMap ();

        while (characterSource.hasChar()) {

            characterSource.skipWhiteSpace ();


            if ( characterSource.currentChar () == DOUBLE_QUOTE ) {

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

                characterSource.skipWhiteSpace ();
                if ( characterSource.currentChar() != COLON ) {

                    complain( "expecting current character to be : but was " + charDescription( characterSource.currentChar() ) + "\n" );
                }

                characterSource.nextChar ();

                characterSource.skipWhiteSpace();

                Object value = decodeValue();

                characterSource.skipWhiteSpace();

                map.put( key, value );


            }

            int ch = characterSource.currentChar();
            if ( ch == '}' ) {
                characterSource.nextChar();
                break;
            } else if ( ch == ',' ) {
                characterSource.nextChar();
                continue;
            } else {
                complain(
                        "expecting '}' or ',' but got current char " + charDescription( ch ) );

            }
        }


        return map;
    }


    protected final void complain( String complaint ) {
        throw new JsonException( exceptionDetails( complaint ) );
    }



    private final Object decodeValue() {
        CharacterSource characterSource = this.characterSource;
        Object value = null;
        characterSource.skipWhiteSpace ();

        switch ( characterSource.currentChar () ) {

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
            case '-':
                value = decodeNumber();
                break;

            default:
                throw new JsonException( exceptionDetails( "Unable to determine the " +
                        "current character, it is not a string, number, array, or object" ) );

        }

        return value;
    }




    private final Object decodeNumber( ) {

        char[] chars = characterSource.readNumber ();
        Object value = null;

        if ( CharScanner.hasDecimalChar ( chars ) )  {
            value = CharScanner.doubleValue ( chars );
        } else if (CharScanner.isInteger ( chars )) {
            value = CharScanner.parseInt ( chars );
        } else if (CharScanner.isLong( chars )) {
            value = CharScanner.parseLong ( chars );
        }

        return value;
    }



    protected static final char[] NULL = Chr.chars ( "null" );

    protected final Object decodeNull() {
        if ( !characterSource.consumeIfMatch ( NULL ) ) {
            throw new JsonException( exceptionDetails( "null not parse properly" ) );
        }
        return null;
    }


    protected static final char[] TRUE = Chr.chars( "true" );

    protected final boolean decodeTrue() {

        if ( characterSource.consumeIfMatch ( TRUE ) ) {
            return true;
        } else {
            throw new JsonException( exceptionDetails( "true not parsed properly" ) );
        }
    }


    protected static char[] FALSE = Chr.chars( "false" );

    protected final boolean decodeFalse() {

        if ( characterSource.consumeIfMatch ( FALSE ) ) {
            return false;
        } else {
            throw new JsonException( exceptionDetails( "false not parsed properly" ) );
        }

    }



    private CharBuf builder = CharBuf.create( 20 );

    private String decodeString() {

        CharacterSource characterSource = this.characterSource;

        characterSource.nextChar();


        char [] chars = characterSource.findNextChar ( '"', '\\' );



        String value = null;
        if ( characterSource.hadEscape() ) {
            value = builder.decodeJsonString ( chars ).toString ();
            builder.recycle ();
        } else {
            value = new String( chars  );
        }

        return value;
    }


    protected final List decodeJsonArray() {

        CharacterSource characterSource = this.characterSource;

        if ( this.characterSource.currentChar() == '[' ) {
            characterSource.nextChar();
        }


        characterSource.skipWhiteSpace ();


        /* the list might be empty  */
        if ( this.characterSource.currentChar() == ']' ) {
            characterSource.nextChar();
            return Collections.EMPTY_LIST;
        }

        ArrayList<Object> list = new ArrayList();

        do {

            characterSource.skipWhiteSpace ();

            Object arrayItem = decodeValue ();

            list.add( arrayItem );


            characterSource.skipWhiteSpace ();

            int c = characterSource.currentChar();

            if ( c == COMMA ) {
                characterSource.nextChar();
                continue;
            } else if ( c == CLOSED_BRACKET ) {
                characterSource.nextChar();
                break;
            } else {

                String charString = charDescription( c );

                complain(
                        String.format( "expecting a ',' or a ']', " +
                                " but got \nthe current character of  %s " +
                                " on array index of %s \n", charString, list.size() )
                );

            }
        } while ( characterSource.hasChar () );

        return list;
    }


    @Override
    public final <T> T parse( Class<T> type, String str ) {

        characterSource = new CharArrayCharacterSource ( str  );
        T object = ( T )  this.decodeValue ();
        return convert( type, object );
    }


    @Override
    public final <T> T parse( Class<T> type, byte[] value ) {
        characterSource = new CharArrayCharacterSource ( new String ( value, charset )  );
        T object = ( T )  this.decodeValue ();
        return convert( type, object );
    }

    @Override
    public <T> T parse( Class<T> type, byte[] value, Charset charset ) {
        characterSource = new CharArrayCharacterSource ( new String ( value, charset )  );
        T object = ( T )  this.decodeValue ();
        return convert( type, object );
    }




    @Override
    public Object parse ( char[] chars ) {
        characterSource = new CharArrayCharacterSource ( chars  );
        return decodeValue ();
    }

    @Override
    public final <T> T parse( Class<T> type, char[] chars ) {
        characterSource = new CharArrayCharacterSource ( chars  );
        T object = ( T )  this.decodeValue ();
        return convert ( type, object );

    }

    @Override
    public Object parse ( byte[] value, Charset charset ) {
        characterSource = new CharArrayCharacterSource ( new String ( value, charset )  );
        return  this.decodeValue ();
    }


    @Override
    public  Object parse(  Reader reader ) {

        characterSource = new ReaderCharacterSource ( reader );
        return  this.decodeValue ();

    }



}
