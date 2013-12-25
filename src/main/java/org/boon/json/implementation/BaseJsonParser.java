package org.boon.json.implementation;

import org.boon.core.reflection.Reflection;
import org.boon.core.LazyMap;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by rick on 12/15/13.
 */
public class BaseJsonParser {

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


    protected <T> T convert( Class<T> type, T object ) {
        if ( type == Map.class || type == List.class ) {
            return object;
        } else {
            if ( object instanceof Map ) {
                return Reflection.fromMap( ( Map<String, Object> ) object, type );
            } else {
                return object;
            }
        }
    }

    public void setCharset( Charset charset ) {
        this.charset = charset;
    }
}
