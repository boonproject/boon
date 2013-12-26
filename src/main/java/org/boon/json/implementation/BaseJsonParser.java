package org.boon.json.implementation;

import org.boon.core.Value;
import org.boon.core.reflection.Conversions;
import org.boon.core.reflection.Reflection;
import org.boon.core.LazyMap;
import org.boon.json.JsonParser;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class BaseJsonParser implements JsonParser {

    protected static final boolean internKeys = Boolean.parseBoolean( System.getProperty( "org.boon.json.implementation.internKeys", "false" ) );
    protected static ConcurrentHashMap<String, String> internedKeysCache;

    protected Charset charset  = StandardCharsets.UTF_8;


    static {
        if ( internKeys ) {
            internedKeysCache = new ConcurrentHashMap<>();
        }
    }



    protected String charDescription( char c ) {
        String charString;
        if ( c == ' ' ) {
            charString = "[SPACE]";
        } else if ( c == '\t' ) {
            charString = "[TAB]";

        } else if ( c == '\n' ) {
            charString = "[NEWLINE]";

        } else {
            charString = "'" + c + "'";
        }

        charString = charString + " with an int value of " + ( ( int ) c );
        return charString;
    }


    protected  <T> T convert( Class<T> type, Object object ) {
        if ( type == Map.class || type == List.class ) {
            return (T)object;
        } else {
            if ( object instanceof Map ) {
                return Reflection.fromMap( ( Map<String, Object> ) object, type );
            } else {
                return (T)object;
            }
        }
    }

    public void setCharset( Charset charset ) {
        this.charset = charset;
    }


    @Override
    public Map<String, Object> parseMap ( String jsonString ) {
        return parse ( Map.class, jsonString );
    }

    @Override
    public <T> List<T> parseList ( Class<T> componentType, String jsonString ) {
        List<Map<String, Object>> list =  parse ( List.class, jsonString );
        return Reflection.convertListOfMapsToObjects ( componentType, list );
    }


    @Override
    public <T> List<T> parseList ( Class<T> componentType, InputStream input ) {
        List<Map<String, Object>> list =  parse ( List.class, input );
        return Reflection.convertListOfMapsToObjects ( componentType, list );
    }

    @Override
    public <T> List<T> parseList ( Class<T> componentType, InputStream input, Charset charset ) {
        List<Map<String, Object>> list =  parse ( List.class, input, charset );
        return Reflection.convertListOfMapsToObjects ( componentType, list );
    }

    @Override
    public <T>  List<T> parseList ( Class<T> componentType, byte[] jsonBytes ) {
        List<Map<String, Object>> list =  parse ( List.class, jsonBytes );
        return Reflection.convertListOfMapsToObjects ( componentType, list );
    }

    @Override
    public <T>  List<T> parseList ( Class<T> componentType, byte[] jsonBytes, Charset charset ) {
        List<Map<String, Object>> list =  parse ( List.class, jsonBytes, charset );
        return Reflection.convertListOfMapsToObjects ( componentType, list );
    }

    @Override
    public <T>  List<T> parseList ( Class<T> componentType, char[] chars ) {
        List<Map<String, Object>> list =  parse ( List.class, chars );
        return Reflection.convertListOfMapsToObjects ( componentType, list );
    }

    @Override
    public <T>  List<T> parseList ( Class<T> componentType, CharSequence jsonSeq ) {
        List<Map<String, Object>> list =  parse ( List.class, jsonSeq );
        return Reflection.convertListOfMapsToObjects ( componentType, list );
    }

    @Override
    public <T>  List<T> parseListFromFile ( Class<T> componentType, String fileName ) {
        List<Map<String, Object>> list =  parseFile ( List.class, fileName );
        return Reflection.convertListOfMapsToObjects ( componentType, list );
    }




    @Override
    public int parseInt ( String jsonString ) {
        return Conversions.toInt ( parse ( int.class, jsonString ) );
    }

    @Override
    public int parseInt ( InputStream input ) {
        return Conversions.toInt ( parse ( int.class, input ) );
    }

    @Override
    public int parseInt ( InputStream input, Charset charset ) {
        return Conversions.toInt ( parse ( int.class, input, charset ) );
    }

    @Override
    public int parseInt ( byte[] jsonBytes ) {
        return Conversions.toInt ( parse ( int.class, jsonBytes ) );
    }

    @Override
    public int parseInt ( byte[] jsonBytes, Charset charset ) {
        return Conversions.toInt ( parse ( int.class, jsonBytes, charset ) );
    }

    @Override
    public int parseInt ( char[] chars ) {
        return Conversions.toInt ( parse ( int.class, chars ) );
    }

    @Override
    public int parseInt ( CharSequence jsonSeq ) {
        return Conversions.toInt ( parse ( int.class, jsonSeq ) );
    }

    @Override
    public int parseIntFromFile ( String fileName ) {
        return Conversions.toInt ( parseFile ( int.class, fileName ) );
    }

}
