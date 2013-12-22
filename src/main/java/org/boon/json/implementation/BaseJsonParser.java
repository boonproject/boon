package org.boon.json.implementation;

import org.boon.core.reflection.Reflection;
import org.boon.json.internal.JsonLazyLinkedMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by rick on 12/15/13.
 */
public class BaseJsonParser {

    protected static final boolean heavyCache = Boolean.parseBoolean( System.getProperty( "org.boon.json.implementation.heavyCache", "false" ) );
    protected static final boolean internKeys = Boolean.parseBoolean( System.getProperty( "org.boon.json.implementation.internKeys", "false" ) );
    protected static ConcurrentHashMap<String, String> internedKeysCache;
    private JsonLazyLinkedMap[] levelMaps = new JsonLazyLinkedMap[ 5 ];
    private ArrayList[] levelLists = new ArrayList[ 5 ];


    private int objectLevel;
    private int listLevel;

    static {
        if ( internKeys ) {
            internedKeysCache = new ConcurrentHashMap<>();
        }
    }


    protected void init () {
        objectLevel = 0;
        listLevel = 0;
    }


    protected JsonLazyLinkedMap createMap () {
        if ( objectLevel == levelMaps.length ) {
            objectLevel++;
            return new JsonLazyLinkedMap( 7 );
        }
        JsonLazyLinkedMap map = levelMaps[ objectLevel ];
        if ( map == null ) {
            map = new JsonLazyLinkedMap( 10 );
            levelMaps[ objectLevel ] = map;
        }
        objectLevel++;
        return map;
    }


    protected ArrayList createList () {

        if ( listLevel == levelLists.length ) {
            listLevel++;
            return new ArrayList( 5 );
        }
        ArrayList list = levelLists[ listLevel ];
        if ( list == null ) {
            list = new ArrayList( 10 );
            levelLists[ listLevel ] = list;
        }
        listLevel++;
        return list;

    }


    protected final ArrayList prepareList ( ArrayList old ) {


        if ( listLevel < levelLists.length ) {
            ArrayList list = new ArrayList( old );
            old.clear();
            listLevel--;
            return list;
        } else {
            listLevel--;
            return old;
        }
    }


    protected Object prepareMap ( final JsonLazyLinkedMap map ) {

        if ( objectLevel < levelMaps.length ) {
            objectLevel--;
            return map.clearAndCopy();
        } else {
            objectLevel--;
            return map;
        }
    }


    protected String charDescription ( char c ) {
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


    protected <T> T convert ( Class<T> type, T object ) {
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


}
