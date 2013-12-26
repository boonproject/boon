package org.boon.json;

import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

public interface JsonParser {


    Map<String, Object> parseMap( String jsonString );
    <T> List<T>  parseList(  Class<T> componentType, String jsonString );
    <T> List<T>  parseList(  Class<T> componentType, InputStream input );
    <T> List<T>  parseList(  Class<T> componentType, InputStream input, Charset charset );
    <T> List<T>  parseList(  Class<T> componentType, byte[] jsonBytes );
    <T> List<T>  parseList(  Class<T> componentType, byte[] jsonBytes, Charset charset );
    <T> List<T>  parseList(  Class<T> componentType, char[] chars );
    <T> List<T>  parseList(  Class<T> componentType, CharSequence jsonSeq );
    <T> List<T>  parseListFromFile(  Class<T> componentType, String fileName );

    <T> T parse( Class<T> type, String jsonString );
    <T> T parse( Class<T> type, byte[] bytes );
    <T> T parse( Class<T> type, byte[] bytes, Charset charset );
    <T> T parse( Class<T> type, CharSequence charSequence );
    <T> T parse( Class<T> type, char[] chars );
    <T> T parse( Class<T> type, Reader reader );
    <T> T parse( Class<T> type, InputStream input );
    <T> T parse( Class<T> type, InputStream input, Charset charset );
    <T> T parseDirect( Class<T> type, byte[] value );
    <T> T parseAsStream( Class<T> type, byte[] value );
    <T> T parseFile( Class<T> type,  String fileName);


    int  parseInt(  String jsonString );
    int  parseInt(  InputStream input );
    int  parseInt(  InputStream input, Charset charset );
    int  parseInt(  byte[] jsonBytes );
    int  parseInt(  byte[] jsonBytes, Charset charset );
    int  parseInt(  char[] chars );
    int  parseInt(  CharSequence jsonSeq );
    int  parseIntFromFile(  String fileName );

}
