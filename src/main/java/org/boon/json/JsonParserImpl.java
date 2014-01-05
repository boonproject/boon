package org.boon.json;

import org.boon.Exceptions;
import org.boon.IO;
import org.boon.core.Typ;
import org.boon.core.Value;
import org.boon.core.reflection.MapObjectConversion;
import org.boon.core.reflection.fields.FieldsAccessor;
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


    private int bufSize = 32;


    public JsonParserImpl( final FieldsAccessor fields,
                           Charset charset,
                           boolean lax,
                           boolean plistStyle, boolean chop, boolean lazyChop ) {


        super(fields);
        this.charset = charset;

        if ( lax ) {
           this.basicParser = new JsonParserLax ( fields, false, chop, lazyChop );
           this.objectParser = new JsonParserLax ( fields, true );
        } else if (plistStyle) {
            this.basicParser = new PlistParser ( fields, false, chop, lazyChop );
            this.objectParser = new PlistParser ( fields, true );
        } else {
            this.basicParser = new JsonFastParser( fields, false, chop, lazyChop );
            this.objectParser = new JsonFastParser( fields, true );
        }

        ( (BaseJsonParser) basicParser).setCharset ( charset );
        ( (BaseJsonParser) objectParser).setCharset ( charset );




    }



    @Override
    public final <T> T parse( Class<T> type, String value ) {

        if ( type == Object.class || type == Map.class || type == List.class || Typ.isBasicType ( type ) ) {
            Object obj = basicParser.parse( type, value );
            return (T) obj;
        } else {
           Map<String, Value> objectMap = ( Map<String, Value> ) objectParser.parse( Map.class, value );
           return MapObjectConversion.fromValueMap (fieldsAccessor, objectMap, type );
        }
    }


    @Override
    public final <T> T parse( Class<T> type, byte[] value ) {

        if ( type==Object.class || type == Map.class || type == List.class ) {
            if (value.length < 100_000) {
                return this.basicParser.parse( type, value );
            } else {
                return this.basicParser.parseAsStream ( type, value );
            }
        } else {
            Map<String, Value> objectMap = ( Map<String, Value> ) objectParser.parse( Map.class, value );
            return MapObjectConversion.fromValueMap (fieldsAccessor, objectMap, type );
        }
    }

    @Override
    public final <T> T parse( Class<T> type, byte[] value, Charset charset ) {

        if ( type==Object.class || type == Map.class || type == List.class ) {
            return this.basicParser.parse( type, value, charset );
        } else {
            Map<String, Value> objectMap = ( Map<String, Value> ) objectParser.parse( Map.class, value );
            return MapObjectConversion.fromValueMap (fieldsAccessor, objectMap, type );
        }
    }

    @Override
    public final <T> T parse( Class<T> type, CharSequence value ) {
        if ( type==Object.class ||  type == Map.class || type == List.class ) {
            return basicParser.parse( type, value );
        } else {
            Map<String, Value> objectMap = ( Map<String, Value> ) objectParser.parse( Map.class, value );
            return MapObjectConversion.fromValueMap ( fieldsAccessor, objectMap, type );
        }
    }

    @Override
    public final <T> T parse( Class<T> type, char[] value ) {
        if (  type==Object.class || type == Map.class || type == List.class ) {
            return basicParser.parse( type, value );
        } else {
            Map<String, Value> objectMap = ( Map<String, Value> ) objectParser.parse( Map.class, value );
            return MapObjectConversion.fromValueMap ( fieldsAccessor, objectMap, type );
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
    public Object parse ( byte[] bytes, Charset charset ) {
        return basicParser.parse(bytes, charset);
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
