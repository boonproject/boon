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
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class JsonMappingParser implements JsonParserAndMapper {



    private final JsonParserAndMapper objectParser;
    private final JsonParserAndMapper basicParser;


    private final JsonParserAndMapper largeFileParser;
    private final FieldsAccessor fields;
    private final Charset charset;

    private CharBuf charBuf;


    private int bufSize = 32;


    public JsonMappingParser( final FieldsAccessor fields,
                                    Charset charset,
                                    boolean lax,
                                    boolean chop, boolean lazyChop ) {


        this.charset = charset;
        this.fields = fields;

        if ( lax ) {
           this.basicParser = new BaseJsonParserAndMapper( new JsonParserLax ( false, chop, lazyChop ), fields);
           this.objectParser = new BaseJsonParserAndMapper(new JsonParserLax ( true ), fields);
        } else {
            this.basicParser = new BaseJsonParserAndMapper( new JsonFastParser ( false, chop, lazyChop ), fields);
            this.objectParser = new BaseJsonParserAndMapper(new JsonFastParser ( true ), fields);
        }

        ( (BaseJsonParserAndMapper ) basicParser).setCharset ( charset );
        ( (BaseJsonParserAndMapper ) objectParser).setCharset ( charset );


        largeFileParser = new JsonParserFactory().createCharacterSourceParser();

    }


    @Override
    public Map<String, Object> parseMap( String value ) {
        return basicParser.parseMap( value );
    }

    @Override
    public Map<String, Object> parseMap( char[] value ) {
        return basicParser.parseMap( value );
    }

    @Override
    public Map<String, Object> parseMap( byte[] value ) {
        return basicParser.parseMap( value );
    }

    @Override
    public Map<String, Object> parseMap( byte[] value, Charset charset ) {
        return basicParser.parseMap( value, charset );
    }

    @Override
    public Map<String, Object> parseMap( InputStream value, Charset charset ) {
        return basicParser.parseMap( value, charset );
    }

    @Override
    public Map<String, Object> parseMap( CharSequence value ) {
        return basicParser.parseMap( value );
    }

    @Override
    public Map<String, Object> parseMap( InputStream value ) {
        return basicParser.parseMap( value );
    }

    @Override
    public Map<String, Object> parseMap( Reader value ) {
        return basicParser.parseMap( value );
    }

    @Override
    public Map<String, Object> parseMapFromFile( String file ) {

        return ( Map<String, Object> ) parseFile( file );
    }

    @Override
    public <T> List<T> parseList( Class<T> componentType, String jsonString ) {
        return objectParser.parseList( componentType, jsonString);
    }

    @Override
    public <T> List<T> parseList( Class<T> componentType, InputStream input ) {
        return objectParser.parseList( componentType, input);
    }

    @Override
    public <T> List<T> parseList( Class<T> componentType, Reader reader ) {
        return objectParser.parseList( componentType, reader);
    }

    @Override
    public <T> List<T> parseList( Class<T> componentType, InputStream input, Charset charset ) {
        return objectParser.parseList( componentType, input, charset);
    }

    @Override
    public <T> List<T> parseList( Class<T> componentType, byte[] jsonBytes ) {
        return objectParser.parseList( componentType, jsonBytes);
    }

    @Override
    public <T> List<T> parseList( Class<T> componentType, byte[] jsonBytes, Charset charset ) {
        return objectParser.parseList( componentType, jsonBytes, charset);
    }

    @Override
    public <T> List<T> parseList( Class<T> componentType, char[] chars ) {
        return objectParser.parseList( componentType, chars);
    }

    @Override
    public <T> List<T> parseList( Class<T> componentType, CharSequence jsonSeq ) {
        return objectParser.parseList( componentType, jsonSeq);
    }

    @Override
    public <T> List<T> parseListFromFile( Class<T> componentType, String fileName ) {
        return objectParser.parseListFromFile( componentType, fileName);
    }

    @Override
    public final <T> T parse( Class<T> type, String value ) {

        if ( Typ.isBasicTypeOrCollection( type ) ){
            Object obj = basicParser.parse( type, value );
            return (T) obj;
        } else {
            Object object = objectParser.parse( Map.class, value );
            return finalExtract( type, object );
        }
    }

    private <T> T finalExtract( Class<T> type, Object object ) {
        if (object instanceof Map ) {
            Map<String, Value> objectMap = ( Map<String, Value> ) object;
           return MapObjectConversion.fromValueMap( fields, objectMap, type );
        } else if (object instanceof List ) {
            List<Object> list = ( List<Object> ) object;
            return MapObjectConversion.fromList ( list, type );
        } else {
            return (T)object;
        }
    }


    @Override
    public final <T> T parse( Class<T> type, byte[] value ) {

        if ( type==Object.class || type == Map.class || type == List.class || Typ.isBasicType ( type ) ) {
            if (value.length < 100_000) {
                return this.basicParser.parse( type, value );
            } else {
                return this.basicParser.parseAsStream ( type, value );
            }
        } else {
            Object object = objectParser.parse( Map.class, value );
            return finalExtract( type, object );
        }
    }

    @Override
    public final <T> T parse( Class<T> type, byte[] value, Charset charset ) {

        if ( type==Object.class || type == Map.class || type == List.class || Typ.isBasicType ( type ) ) {
            return this.basicParser.parse( type, value, charset );
        } else {
            Object object = objectParser.parse( Map.class, value );
            return finalExtract( type, object );
        }
    }

    @Override
    public final <T> T parse( Class<T> type, CharSequence value ) {
        if ( type==Object.class ||  type == Map.class || type == List.class || Typ.isBasicType ( type ) ) {
            return basicParser.parse( type, value );
        } else {
            Object object = objectParser.parse( Map.class, value );
            return finalExtract( type, object );
        }
    }

    @Override
    public final <T> T parse( Class<T> type, char[] value ) {
        if (  type==Object.class || type == Map.class || type == List.class || Typ.isBasicType ( type ) ) {
            return basicParser.parse( type, value );
        } else {
            Object object = objectParser.parse( Map.class, value );
            return finalExtract( type, object );
        }

    }



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

            if (size > 10_000_000) {
                return this.largeFileParser.parseFile( type, fileName );
            } else {
                size = size > 2_000_000 ? bufSize : size;
                this.bufSize = (int)size;
            }

            if (size < 1_000_000)  {
                return parse ( type, Files.newInputStream ( filePath ), charset );
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
    public int parseInt( String jsonString ) {
        return basicParser.parseInt( jsonString );
    }

    @Override
    public int parseInt( InputStream input ) {
        return basicParser.parseInt( input );
    }

    @Override
    public int parseInt( InputStream input, Charset charset ) {
        return basicParser.parseInt( input, charset );
    }

    @Override
    public int parseInt( byte[] jsonBytes ) {
        return basicParser.parseInt( jsonBytes );
    }

    @Override
    public int parseInt( byte[] jsonBytes, Charset charset ) {
        return basicParser.parseInt( jsonBytes, charset );
    }

    @Override
    public int parseInt( char[] chars ) {
        return basicParser.parseInt( chars );
    }

    @Override
    public int parseInt( CharSequence jsonSeq ) {
        return basicParser.parseInt( jsonSeq );
    }

    @Override
    public int parseIntFromFile( String fileName ) {
        return basicParser.parseIntFromFile( fileName );
    }

    @Override
    public long parseLong( String jsonString ) {
        return basicParser.parseLong( jsonString );
    }

    @Override
    public long parseLong( InputStream input ) {
        return basicParser.parseLong( input );
    }

    @Override
    public long parseLong( InputStream input, Charset charset ) {
        return basicParser.parseLong( input, charset );
    }

    @Override
    public long parseLong( byte[] jsonBytes ) {
        return basicParser.parseLong( jsonBytes );
    }

    @Override
    public long parseLong( byte[] jsonBytes, Charset charset ) {
        return basicParser.parseLong( jsonBytes, charset );
    }

    @Override
    public long parseLong( char[] chars ) {
        return basicParser.parseLong( chars );
    }

    @Override
    public long parseLong( CharSequence jsonSeq ) {
        return basicParser.parseLong( jsonSeq );
    }

    @Override
    public long parseLongFromFile( String fileName ) {
        return basicParser.parseLongFromFile( fileName );
    }

    @Override
    public String parseString( String value ) {
        return basicParser.parseString( value );
    }

    @Override
    public String parseString( InputStream value ) {

        return basicParser.parseString( value );
    }

    @Override
    public String parseString( InputStream value, Charset charset ) {

        return basicParser.parseString( value, charset );
    }

    @Override
    public String parseString( byte[] value ) {

        return basicParser.parseString( value );
    }

    @Override
    public String parseString( byte[] value, Charset charset ) {

        return basicParser.parseString( value, charset );
    }

    @Override
    public String parseString( char[] value ) {


        return basicParser.parseString( value );
    }

    @Override
    public String parseString( CharSequence value ) {


        return basicParser.parseString( value );
    }

    @Override
    public String parseStringFromFile( String value ) {

        return basicParser.parseStringFromFile( value );
    }

    @Override
    public double parseDouble( String value ) {
        return basicParser.parseDouble( value );
    }

    @Override
    public double parseDouble( InputStream value ) {
        return basicParser.parseDouble( value );
    }

    @Override
    public double parseDouble( byte[] value ) {
        return basicParser.parseDouble( value );
    }

    @Override
    public double parseDouble( char[] value ) {
        return basicParser.parseDouble( value );
    }

    @Override
    public double parseDouble( CharSequence value ) {
        return basicParser.parseDouble( value );
    }

    @Override
    public double parseDouble( byte[] value, Charset charset ) {
        return basicParser.parseDouble( value, charset );
    }

    @Override
    public double parseDouble( InputStream value, Charset charset ) {
        return basicParser.parseDouble( value, charset );
    }

    @Override
    public double parseDoubleFromFile( String fileName ) {
        return basicParser.parseDoubleFromFile( fileName );
    }

    @Override
    public float parseFloat( String value ) {
        return basicParser.parseFloat( value );
    }

    @Override
    public float parseFloat( InputStream value ) {
        return basicParser.parseFloat( value );
    }

    @Override
    public float parseFloat( byte[] value ) {
        return basicParser.parseFloat( value );
    }

    @Override
    public float parseFloat( char[] value ) {
        return basicParser.parseFloat( value );
    }

    @Override
    public float parseFloat( CharSequence value ) {
        return basicParser.parseFloat( value );
    }

    @Override
    public float parseFloat( byte[] value, Charset charset ) {
        return basicParser.parseFloat( value, charset );
    }

    @Override
    public float parseFloat( InputStream value, Charset charset ) {
        return basicParser.parseFloat( value, charset );
    }

    @Override
    public float parseFloatFromFile( String fileName ) {
        return basicParser.parseFloatFromFile( fileName );
    }

    @Override
    public BigDecimal parseBigDecimal( String value ) {
        return basicParser.parseBigDecimal( value );
    }

    @Override
    public BigDecimal parseBigDecimal( InputStream value ) {
        return basicParser.parseBigDecimal( value );
    }

    @Override
    public BigDecimal parseBigDecimal( byte[] value ) {
        return basicParser.parseBigDecimal( value );
    }

    @Override
    public BigDecimal parseBigDecimal( char[] value ) {
        return basicParser.parseBigDecimal( value );
    }

    @Override
    public BigDecimal parseBigDecimal( CharSequence value ) {
        return basicParser.parseBigDecimal( value );
    }

    @Override
    public BigDecimal parseBigDecimal( byte[] value, Charset charset ) {
        return basicParser.parseBigDecimal( value, charset );
    }

    @Override
    public BigDecimal parseBigDecimal( InputStream value, Charset charset ) {
        return basicParser.parseBigDecimal( value, charset );
    }

    @Override
    public BigDecimal parseBigDecimalFromFile( String fileName ) {
        return basicParser.parseBigDecimalFromFile( fileName );
    }

    @Override
    public BigInteger parseBigInteger( String value ) {
        return basicParser.parseBigInteger( value );
    }

    @Override
    public BigInteger parseBigInteger( InputStream value ) {
        return basicParser.parseBigInteger( value );
    }

    @Override
    public BigInteger parseBigInteger( byte[] value ) {
        return basicParser.parseBigInteger( value );
    }

    @Override
    public BigInteger parseBigInteger( char[] value ) {
        return basicParser.parseBigInteger( value );
    }

    @Override
    public BigInteger parseBigInteger( CharSequence value ) {
        return basicParser.parseBigInteger( value );
    }

    @Override
    public BigInteger parseBigInteger( byte[] value, Charset charset ) {
        return basicParser.parseBigInteger( value, charset );
    }

    @Override
    public BigInteger parseBigInteger( InputStream value, Charset charset ) {
        return basicParser.parseBigInteger( value, charset );
    }

    @Override
    public BigInteger parseBigIntegerFile( String fileName ) {
        return basicParser.parseBigIntegerFile( fileName );
    }

    @Override
    public Date parseDate( String jsonString ) {
        return basicParser.parseDate( jsonString );
    }

    @Override
    public Date parseDate( InputStream input ) {
        return basicParser.parseDate( input );
    }

    @Override
    public Date parseDate( InputStream input, Charset charset ) {
        return basicParser.parseDate( input, charset );
    }

    @Override
    public Date parseDate( byte[] jsonBytes ) {
        return basicParser.parseDate( jsonBytes );
    }

    @Override
    public Date parseDate( byte[] jsonBytes, Charset charset ) {
        return basicParser.parseDate( jsonBytes, charset );
    }

    @Override
    public Date parseDate( char[] chars ) {
        return basicParser.parseDate( chars );
    }

    @Override
    public Date parseDate( CharSequence jsonSeq ) {
        return basicParser.parseDate( jsonSeq );
    }

    @Override
    public Date parseDateFromFile( String fileName ) {
        return basicParser.parseDateFromFile( fileName );
    }

    @Override
    public short parseShort( String jsonString ) {
        return basicParser.parseShort( jsonString );
    }

    @Override
    public byte parseByte( String jsonString ) {
        return basicParser.parseByte( jsonString );
    }

    @Override
    public char parseChar( String jsonString ) {
        return basicParser.parseChar( jsonString );
    }

    @Override
    public <T extends Enum> T parseEnum( Class<T> type, String jsonString ) {
        return basicParser.parseEnum( type, jsonString );
    }

    @Override
    public char[] parseCharArray( String jsonString ) {
        return basicParser.parseCharArray( jsonString );
    }

    @Override
    public byte[] parseByteArray( String jsonString ) {
        return basicParser.parseByteArray( jsonString );
    }

    @Override
    public short[] parseShortArray( String jsonString ) {
        return basicParser.parseShortArray( jsonString );
    }

    @Override
    public int[] parseIntArray( String jsonString ) {
        return basicParser.parseIntArray( jsonString );
    }

    @Override
    public float[] parseFloatArray( String jsonString ) {
        return basicParser.parseFloatArray( jsonString );
    }

    @Override
    public double[] parseDoubleArray( String jsonString ) {
        return basicParser.parseDoubleArray( jsonString );
    }

    @Override
    public long[] parseLongArray( String jsonString ) {
        return basicParser.parseLongArray( jsonString );
    }

    @Override
    public Object parse( String jsonString ) {
        return basicParser.parse( jsonString );
    }

    @Override
    public Object parse( byte[] bytes ) {
        return basicParser.parse( bytes );
    }

    @Override
    public Object parse ( byte[] bytes, Charset charset ) {
        return basicParser.parse(bytes, charset);
    }

    @Override
    public Object parse( CharSequence charSequence ) {
        return basicParser.parse( charSequence );
    }


    @Override
    public Object parse ( char[] chars ) {
        return basicParser.parse ( chars );
    }

    @Override
    public Object parse( Reader reader ) {
        return basicParser.parse ( reader );
    }

    @Override
    public Object parse( InputStream input ) {
        return basicParser.parse ( input );
    }

    @Override
    public Object parse( InputStream input, Charset charset ) {
        return basicParser.parse ( input, charset );
    }

    @Override
    public Object parseDirect( byte[] value ) {
        if ( value.length < 20_000 && charset == StandardCharsets.UTF_8 ) {
            CharBuf builder = CharBuf.createFromUTF8Bytes( value );
            return parse( builder.toCharArray() );
        } else {
            return this.parse( new ByteArrayInputStream( value ) );
        }
    }

    @Override
    public Object parseAsStream( byte[] value ) {
        return basicParser.parseAsStream ( value );
    }

    @Override
    public Object parseFile( String fileName ) {

        int bufSize = this.bufSize;
        try {


            Path filePath = IO.path ( fileName );
            long size = Files.size ( filePath );

            if (size > 10_000_000) {
                return this.largeFileParser.parseFile( fileName );
            } else {
                size = size > 2_000_000 ? bufSize : size;
                this.bufSize = (int)size;
            }

            if (size < 1_000_000)  {
                return parse ( Files.newInputStream ( filePath ), charset );
            } else {
                return parse (  Files.newBufferedReader ( filePath, charset ) );
            }
        } catch ( IOException ex ) {
            return Exceptions.handle (Typ.object, fileName, ex);
        } finally {
            this.bufSize = bufSize;
        }

    }

    @Override
    public void close() {

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
