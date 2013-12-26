package org.boon.json;

import org.boon.Exceptions;
import org.boon.IO;
import org.boon.core.Conversions;
import org.boon.core.Typ;
import org.boon.core.Value;
import org.boon.core.reflection.Reflection;
import org.boon.json.implementation.*;
import org.boon.primitive.CharBuf;


import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;


public class JsonParserImpl extends BaseJsonParser implements JsonParser  {



    private final JsonParser objectParser;
    private final JsonParser basicParser;
    private final JsonParser charSequenceParser;
    private final JsonParser byteParser;


    private int bufSize = 32;


    public JsonParserImpl( Charset charset,
                           boolean preferCharSequence, boolean lax,
                           boolean plistStyle, boolean chop, boolean lazyChop ) {



        this.charset = charset;

        if ( lax ) {
           this.basicParser = new JsonParserLax ( false, chop, lazyChop );
           this.objectParser = new JsonParserLax ( true );
        } else if (plistStyle) {
            this.basicParser = new PlistParser ( false, chop, lazyChop );
            this.objectParser = new PlistParser ( true );
        } else {
            this.basicParser = new JsonFastParser( false, chop, lazyChop );
            this.objectParser = new JsonFastParser( true );
        }

        ( (BaseJsonParser) basicParser).setCharset ( charset );
        ( (BaseJsonParser) objectParser).setCharset ( charset );

        if ( preferCharSequence ) {
                this.charSequenceParser = new JsonParserCharSequence();
                ( (BaseJsonParser) charSequenceParser).setCharset ( charset );
        } else {
                this.charSequenceParser = basicParser;
        }

        this.byteParser = new JsonUTF8Parser ();


    }



    @Override
    public final <T> T parse( Class<T> type, String value ) {

        if ( type == Map.class || type == List.class || Typ.isBasicType ( type ) ) {
            Object obj = charSequenceParser.parse( type, value );
            return (T) obj;
        } else {
           Map<String, Value> objectMap = ( Map<String, Value> ) objectParser.parse( Map.class, value );
           return Reflection.fromValueMap ( objectMap, type );
        }
    }


    @Override
    public final <T> T parse( Class<T> type, byte[] value ) {

        if ( type == Map.class || type == List.class ) {
            if (value.length < 1_000_000) {
                return this.basicParser.parse( type, value );
            } else {
                return this.byteParser.parse ( type, value );
            }
        } else {
            Map<String, Value> objectMap = ( Map<String, Value> ) objectParser.parse( Map.class, value );
            return Reflection.fromValueMap( objectMap, type );
        }
    }

    @Override
    public final <T> T parse( Class<T> type, byte[] value, Charset charset ) {

        if ( type == Map.class || type == List.class ) {
            return this.basicParser.parse( type, value, charset );
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


    private CharBuf charBuf;

    @Override
    public final <T> T parse( Class<T> type, Reader reader ) {

        charBuf = IO.read( reader, charBuf, bufSize );
        return parse( type, charBuf.readForRecycle() );

    }

    @Override
    public <T> T parseFile( Class<T> type, String fileName ) {
        int bufSize = this.bufSize;

        try {
            Path filePath = IO.path ( fileName );
            long size = Files.size ( filePath );
            size = size > 2_000_000_000 ? bufSize : size;
            this.bufSize = (int)size;
            if (size < 1_000_000)  {
                return parse ( type, Files.newInputStream ( filePath ) );
            } else {
                return parse ( type, Files.newBufferedReader ( filePath, charset ) );
            }
        } catch ( IOException ex ) {
            return Exceptions.handle (type, fileName, ex);
        } finally {
            this.bufSize = bufSize;
        }
    }


    @Override
    public Object parse ( char[] chars ) {
        return basicParser.parse ( chars );
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
        if ( value.length < 20_000 && charset == StandardCharsets.UTF_8 ) {
            CharBuf builder = CharBuf.createFromUTF8Bytes( value );
            return parse( type, builder.toCharArray() );
        } else {
            return this.parse( type, new ByteArrayInputStream( value ) );
        }
    }

    @Override
    public final <T> T parseAsStream( Class<T> type, byte[] value ) {
        charBuf = IO.read( new InputStreamReader ( new ByteArrayInputStream(value), charset ), charBuf, value.length );
        return this.basicParser.parse ( type, charBuf.readForRecycle () );
    }



}
