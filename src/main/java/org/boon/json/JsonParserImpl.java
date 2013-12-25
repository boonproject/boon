package org.boon.json;

import org.boon.IO;
import org.boon.core.Value;
import org.boon.core.reflection.Reflection;
import org.boon.json.implementation.*;
import org.boon.primitive.CharBuf;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;


public class JsonParserImpl implements JsonParser {


    private final Charset charset;


    private final JsonParser objectParser;
    private final JsonParser basicParser;
    private final JsonParser charSequenceParser;


    private int bufSize = 256;


    public JsonParserImpl( Charset charset,
                           boolean preferCharSequence, boolean lax, boolean plistStyle ) {



        this.charset = charset;

        if ( lax ) {
           this.basicParser = new JsonParserLax (  );
           this.objectParser = new JsonParserLax ( true );
        } else if (plistStyle) {
            this.basicParser = new PlistParser (  );
            this.objectParser = new PlistParser ( true );
        } else {
            this.basicParser = new JsonFastParser();
            this.objectParser = new JsonFastParser( true );
        }

        if ( preferCharSequence ) {
                this.charSequenceParser = new JsonParserCharSequence();
        } else {
                this.charSequenceParser = basicParser;
        }

    }

    @Override
    public final <T> T parse( Class<T> type, String value ) {

        if ( type == Map.class || type == List.class ) {
            Object obj = charSequenceParser.parse( type, value );
            if (obj instanceof  Map)  {
                return Reflection.fromMap ( ( Map<String,Object> ) obj, type );
            } else {
                return (T) obj;
            }
        } else {
           Map<String, Value> objectMap = ( Map<String, Value> ) objectParser.parse( Map.class, value );
           return Reflection.fromValueMap ( objectMap, type );
        }
    }


    @Override
    public final <T> T parse( Class<T> type, byte[] value ) {

        if ( type == Map.class || type == List.class ) {
                return this.basicParser.parse( type, value );
        } else {
            Map<String, Value> objectMap = ( Map<String, Value> ) objectParser.parse( Map.class, value );
            return Reflection.fromValueMap( objectMap, type );
        }
    }

    @Override
    public final <T> T parse( Class<T> type, CharSequence value ) {
        if ( type == Map.class || type == List.class ) {
            return charSequenceParser.parse( type, value );
        } else {
            Map<String, Value> objectMap = ( Map<String, Value> ) objectParser.parse( Map.class, value );
            return Reflection.fromValueMap( objectMap, type );
        }
    }

    @Override
    public final <T> T parse( Class<T> type, char[] value ) {
        if ( type == Map.class || type == List.class ) {
            return basicParser.parse( type, value );
        } else {
            Map<String, Value> objectMap = ( Map<String, Value> ) objectParser.parse( Map.class, value );
            return Reflection.fromValueMap( objectMap, type );
        }

    }


    CharBuf charBuf;

    @Override
    public final <T> T parse( Class<T> type, Reader reader ) {

        charBuf = IO.read( reader, charBuf, bufSize );
        return parse( type, charBuf.readForRecycle() );

    }

    @Override
    public final <T> T parse( Class<T> type, InputStream input ) {
        charBuf = IO.read( input, charBuf, this.charset, bufSize );
        return parse( type, charBuf.readForRecycle() );
    }

    @Override
    public final <T> T parse( Class<T> type, InputStream input, Charset charset ) {
        charBuf = IO.read( input, charBuf, charset, bufSize );
        return parse( type, charBuf.readForRecycle() );
    }


    @Override
    public final <T> T parseDirect( Class<T> type, byte[] value ) {
        if ( value.length < 20_000 ) {
            CharBuf builder = CharBuf.createFromUTF8Bytes( value );
            return parse( type, builder.toCharArray() );
        } else {
            return this.parse( type, new ByteArrayInputStream( value ) );
        }
    }

    @Override
    public final <T> T parseAsStream( Class<T> type, byte[] value ) {
        return this.parse( type, new ByteArrayInputStream( value ) );
    }


}
