package org.boon.json.streaming.version1;

import org.boon.IO;
import org.boon.core.reflection.MapObjectConversion;
import org.boon.core.reflection.fields.FieldsAccessor;
import org.boon.core.reflection.fields.FieldsAccessorFieldThenProp;
import org.boon.json.JsonParser;
import org.boon.primitive.Byt;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Richard
 * Date: 1/13/14
 * Time: 4:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class StreamingJavaParserV1 implements JsonParser {

    JsonSlurper slurper = new JsonSlurper ();


    FieldsAccessor fieldsAccessor = new FieldsAccessorFieldThenProp ( false );

    @Override
    public Map<String, Object> parseMap( String value ) {
        return (Map<String, Object>) slurper.parseText(value);
    }

    @Override
    public Map<String, Object> parseMap( char[] value ) {
        return (Map<String, Object>) slurper.parseText(new String (value));
    }

    @Override
    public Map<String, Object> parseMap( byte[] value ) {
        return (Map<String, Object>) slurper.parseText(new String (value, StandardCharsets.UTF_8));
    }

    @Override
    public Map<String, Object> parseMap( byte[] value, Charset charset ) {
        return (Map<String, Object>) slurper.parseText(new String (value, charset));
    }

    @Override
    public Map<String, Object> parseMap( InputStream value, Charset charset ) {
        return (Map<String, Object>) slurper.parseText( IO.read ( value, charset ));
    }

    @Override
    public Map<String, Object> parseMap( CharSequence value ) {
        return (Map<String, Object>) slurper.parseText( IO.read ( value.toString() ));
    }

    @Override
    public Map<String, Object> parseMap( InputStream value ) {
        return (Map<String, Object>) slurper.parseText( IO.read ( value ));
    }

    @Override
    public Map<String, Object> parseMap( Reader value ) {
        return (Map<String, Object>) slurper.parseText( IO.read ( value ));
    }

    @Override
    public Map<String, Object> parseMapFromFile( String file ) {
        return (Map<String, Object>) slurper.parseText ( IO.read (  file  ) );
    }

    @Override
    public <T> List<T> parseList( Class<T> componentType, String jsonString ) {
        List<Object> list =  (List) slurper.parseText ( jsonString) ;
        return MapObjectConversion.convertListOfMapsToObjects ( fieldsAccessor, componentType, list );

    }

    @Override
    public <T> List<T> parseList( Class<T> componentType, InputStream input ) {
        List<Object> list =  (List) slurper.parseText ( IO.read ( input )) ;
        return MapObjectConversion.convertListOfMapsToObjects ( fieldsAccessor, componentType, list );
    }

    @Override
    public <T> List<T> parseList( Class<T> componentType, Reader reader ) {
        List<Object> list =  (List) slurper.parseText ( IO.read ( reader )) ;
        return MapObjectConversion.convertListOfMapsToObjects ( fieldsAccessor, componentType, list );
    }

    @Override
    public <T> List<T> parseList( Class<T> componentType, InputStream input, Charset charset ) {
        List<Object> list =  (List) slurper.parseText ( IO.read ( input, charset )) ;
        return MapObjectConversion.convertListOfMapsToObjects ( fieldsAccessor, componentType, list );
    }

    @Override
    public <T> List<T> parseList( Class<T> componentType, byte[] jsonBytes ) {
        List<Object> list =  (List) slurper.parseText ( new String ( jsonBytes, StandardCharsets.UTF_8 )) ;
        return MapObjectConversion.convertListOfMapsToObjects ( fieldsAccessor, componentType, list );
    }

    @Override
    public <T> List<T> parseList( Class<T> componentType, byte[] jsonBytes, Charset charset ) {
        List<Object> list =  (List) slurper.parseText ( new String ( jsonBytes, charset )) ;
        return MapObjectConversion.convertListOfMapsToObjects ( fieldsAccessor, componentType, list );
    }

    @Override
    public <T> List<T> parseList( Class<T> componentType, char[] chars ) {
        List<Object> list =  (List) slurper.parseText ( new String ( chars )) ;
        return MapObjectConversion.convertListOfMapsToObjects ( fieldsAccessor, componentType, list );
    }

    @Override
    public <T> List<T> parseList( Class<T> componentType, CharSequence jsonSeq ) {
        List<Object> list =  (List) slurper.parseText ( new String ( jsonSeq.toString() )) ;
        return MapObjectConversion.convertListOfMapsToObjects ( fieldsAccessor, componentType, list );
    }

    @Override
    public <T> List<T> parseListFromFile( Class<T> componentType, String fileName ) {
        List<Object> list =  (List) slurper.parseText ( IO.read ( fileName )) ;
        return MapObjectConversion.convertListOfMapsToObjects ( fieldsAccessor, componentType, list );
    }

    @Override
    public <T> T parse( Class<T> type, String jsonString ) {

        Object obj = slurper.parseText(jsonString);
        if (type == Map.class)  {
            return (T)obj;
        } else if (type==List.class) {
            return (T)obj;
        } else {
            return (T)MapObjectConversion.fromMap ( (Map)obj, type );
        }

    }

    @Override
    public <T> T parse( Class<T> type, byte[] bytes ) {
        return parse ( type, new String ( bytes, StandardCharsets.UTF_8 ) );
    }

    @Override
    public <T> T parse( Class<T> type, byte[] bytes, Charset charset ) {
        return parse (type, new String(bytes, charset));

    }

    @Override
    public <T> T parse( Class<T> type, CharSequence charSequence ) {
        return parse ( type, charSequence.toString () );
    }

    @Override
    public <T> T parse( Class<T> type, char[] chars ) {
        return parse (type, new String ( chars ));
    }

    @Override
    public <T> T parse( Class<T> type, Reader reader ) {
        return parse (type, IO.read(reader));
    }

    @Override
    public <T> T parse( Class<T> type, InputStream input ) {
        Map<String, Object> map = (Map<String, Object>) slurper.parseText( IO.read ( input ));
        return MapObjectConversion.fromMap ( map, type );
    }

    @Override
    public <T> T parse( Class<T> type, InputStream input, Charset charset ) {
        Map<String, Object> map = (Map<String, Object>) slurper.parseText( IO.read ( input, charset ));
        return MapObjectConversion.fromMap ( map, type );
    }

    @Override
    public <T> T parseDirect( Class<T> type, byte[] value ) {
        Map<String, Object> map = (Map<String, Object>) slurper.parseText( new String (value, StandardCharsets.UTF_8));
        return MapObjectConversion.fromMap ( map, type );
    }

    @Override
    public <T> T parseAsStream( Class<T> type, byte[] value ) {
        Map<String, Object> map = (Map<String, Object>) slurper.parseText( new String (value, StandardCharsets.UTF_8));
        return MapObjectConversion.fromMap ( map, type );
    }

    @Override
    public <T> T parseFile( Class<T> type, String fileName ) {
        Map<String, Object> map = (Map<String, Object>) slurper.parseText( IO.read ( fileName ));
        return MapObjectConversion.fromMap ( map, type );
    }

    @Override
    public int parseInt( String jsonString ) {
        return (int) slurper.parseText(jsonString);
    }

    @Override
    public int parseInt( InputStream input ) {
        return (int) slurper.parseText(IO.read(input));
    }

    @Override
    public int parseInt( InputStream input, Charset charset ) {
        return (int) slurper.parseText(IO.read(input, charset));
    }

    @Override
    public int parseInt( byte[] jsonBytes ) {
        return (int) slurper.parseText(new String(jsonBytes, StandardCharsets.UTF_8));
    }

    @Override
    public int parseInt( byte[] jsonBytes, Charset charset ) {
        return (int) slurper.parseText(new String(jsonBytes, charset));
    }

    @Override
    public int parseInt( char[] chars ) {
        return (int) slurper.parseText(new String(chars));
    }

    @Override
    public int parseInt( CharSequence jsonSeq ) {
        return (int) slurper.parseText(jsonSeq.toString ());
    }

    @Override
    public int parseIntFromFile( String fileName ) {

        return (int) slurper.parseText(IO.read ( fileName ));
    }

    @Override
    public long parseLong( String jsonString ) {
        return (long) slurper.parseText(jsonString);
    }

    @Override
    public long parseLong( InputStream input ) {
        return (long) slurper.parseText(IO.read(input));
    }

    @Override
    public long parseLong( InputStream input, Charset charset ) {
        return (long) slurper.parseText(IO.read(input, charset));
    }

    @Override
    public long parseLong( byte[] jsonBytes ) {
        return (long) slurper.parseText( Byt.utfString ( jsonBytes ));
    }

    @Override
    public long parseLong( byte[] jsonBytes, Charset charset ) {
        return (long) slurper.parseText( new String ( jsonBytes, charset ));
    }

    @Override
    public long parseLong( char[] chars ) {

        return (long) slurper.parseText(new String(chars));
    }

    @Override
    public long parseLong( CharSequence jsonSeq ) {
        return (long) slurper.parseText(jsonSeq.toString ());
    }

    @Override
    public long parseLongFromFile( String fileName ) {
        return (long) slurper.parseText(IO.read ( fileName ));
    }

    @Override
    public double parseDouble( String value ) {
        return (double) slurper.parseText(value);
    }

    @Override
    public double parseDouble( InputStream value ) {

        return (double) slurper.parseText(IO.read ( value ));
    }

    @Override
    public double parseDouble( byte[] value ) {
        return (double) slurper.parseText(Byt.utfString ( value ));
    }

    @Override
    public double parseDouble( char[] value ) {

        return (double) slurper.parseText(new String ( value ));
    }

    @Override
    public double parseDouble( CharSequence value ) {

        return (double) slurper.parseText(value.toString ());
    }

    @Override
    public double parseDouble( byte[] value, Charset charset ) {

        return (double) slurper.parseText(new String(value, charset));
    }

    @Override
    public double parseDouble( InputStream value, Charset charset ) {

        return (double) slurper.parseText(IO.read(value, charset));
    }

    @Override
    public double parseDoubleFromFile( String fileName ) {
        return (double) slurper.parseText(IO.read(fileName));
    }

    @Override
    public float parseFloat( String value ) {
        return (float) slurper.parseText(value);
    }

    @Override
    public float parseFloat( InputStream value ) {

        return (float) slurper.parseText(IO.read ( value ));
    }

    @Override
    public float parseFloat( byte[] value ) {

        return (float) slurper.parseText(Byt.utfString ( value ));
    }

    @Override
    public float parseFloat( char[] value ) {
        return (float) slurper.parseText(new String( value ));
    }

    @Override
    public float parseFloat( CharSequence value ) {
        return (float) slurper.parseText(value.toString());
    }

    @Override
    public float parseFloat( byte[] value, Charset charset ) {
        return (float) slurper.parseText(new String(value, charset));
    }

    @Override
    public float parseFloat( InputStream value, Charset charset ) {

        return (float) slurper.parseText(IO.read ( value, charset ));
    }

    @Override
    public float parseFloatFromFile( String fileName ) {

        return (float) slurper.parseText(IO.read ( fileName ));
    }

    @Override
    public BigDecimal parseBigDecimal( String value ) {
        return (BigDecimal) slurper.parseText(value);
    }

    @Override
    public BigDecimal parseBigDecimal( InputStream value ) {
        return (BigDecimal) slurper.parseText(IO.read (value));
    }

    @Override
    public BigDecimal parseBigDecimal( byte[] value ) {
        return (BigDecimal) slurper.parseText(Byt.utfString ( value ));
    }

    @Override
    public BigDecimal parseBigDecimal( char[] value ) {
        return (BigDecimal) slurper.parseText(new String ( value ));
    }

    @Override
    public BigDecimal parseBigDecimal( CharSequence value ) {

        return (BigDecimal) slurper.parseText(value.toString ());
    }

    @Override
    public BigDecimal parseBigDecimal( byte[] value, Charset charset ) {
        return (BigDecimal) slurper.parseText(new String(value, charset));
    }

    @Override
    public BigDecimal parseBigDecimal( InputStream value, Charset charset ) {

        return (BigDecimal) slurper.parseText(IO.read(value, charset));
    }

    @Override
    public BigDecimal parseBigDecimalFromFile( String fileName ) {
        return (BigDecimal) slurper.parseText(IO.read(fileName));
    }

    @Override
    public BigInteger parseBigInteger( String value ) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public BigInteger parseBigInteger( InputStream value ) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public BigInteger parseBigInteger( byte[] value ) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public BigInteger parseBigInteger( char[] value ) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public BigInteger parseBigInteger( CharSequence value ) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public BigInteger parseBigInteger( byte[] value, Charset charset ) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public BigInteger parseBigInteger( InputStream value, Charset charset ) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public BigInteger parseBigIntegerFile( String fileName ) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Date parseDate( String jsonString ) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Date parseDate( InputStream input ) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Date parseDate( InputStream input, Charset charset ) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Date parseDate( byte[] jsonBytes ) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Date parseDate( byte[] jsonBytes, Charset charset ) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Date parseDate( char[] chars ) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Date parseDate( CharSequence jsonSeq ) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Date parseDateFromFile( String fileName ) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public short parseShort( String jsonString ) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public byte parseByte( String jsonString ) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public char parseChar( String jsonString ) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends Enum> T parseEnum( Class<T> type, String jsonString ) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public char[] parseCharArray( String jsonString ) {
        return new char[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public byte[] parseByteArray( String jsonString ) {
        return new byte[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public short[] parseShortArray( String jsonString ) {
        return new short[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int[] parseIntArray( String jsonString ) {
        return new int[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public float[] parseFloatArray( String jsonString ) {
        return new float[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public double[] parseDoubleArray( String jsonString ) {
        return new double[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public long[] parseLongArray( String jsonString ) {
        return new long[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object parse( String jsonString ) {
        return slurper.parseText(jsonString);
    }

    @Override
    public Object parse( byte[] bytes ) {
        return slurper.parseText(Byt.utfString ( bytes ));
    }

    @Override
    public Object parse( byte[] bytes, Charset charset ) {

        return slurper.parseText(new String(bytes, charset));
    }

    @Override
    public Object parse( CharSequence charSequence ) {


        return slurper.parseText(charSequence.toString());

    }

    @Override
    public Object parse( char[] chars ) {

        return slurper.parseText(new String(chars));
    }

    @Override
    public Object parse( Reader reader ) {
        return slurper.parseText(IO.read ( reader ));
    }

    @Override
    public Object parse( InputStream input ) {

        return slurper.parseText(IO.read ( input ));
    }

    @Override
    public Object parse( InputStream input, Charset charset ) {
        return slurper.parseText(IO.read ( input, charset ));
    }

    @Override
    public Object parseDirect( byte[] value ) {
        return slurper.parseText(Byt.utfString ( value ));
    }

    @Override
    public Object parseAsStream( byte[] value ) {
        return slurper.parseText(Byt.utfString ( value ));
    }

    @Override
    public Object parseFile( String fileName ) {
        return slurper.parseText(IO.read ( fileName ));
    }

    @Override
    public void close() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
