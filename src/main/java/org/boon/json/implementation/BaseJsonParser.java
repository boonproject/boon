package org.boon.json.implementation;

import org.boon.json.internal.JsonLazyLinkedMap;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by rick on 12/15/13.
 */
public class BaseJsonParser {

    protected static final boolean heavyCache = Boolean.parseBoolean ( System.getProperty ( "org.boon.json.implementation.heavyCache", "false" ) );
    protected static final boolean internKeys = Boolean.parseBoolean ( System.getProperty ( "org.boon.json.implementation.internKeys", "false" ) );
    protected static ConcurrentHashMap<String, String> internedKeysCache;
    private JsonLazyLinkedMap[] levelMaps = new JsonLazyLinkedMap[ 5 ];
    private ArrayList[] levelLists = new ArrayList[ 5 ];

    private int objectLevel;
    private int listLevel;

    static {
        if ( internKeys ) {
            internedKeysCache = new ConcurrentHashMap<> ();
        }
    }


<<<<<<< HEAD
    protected void init () {
=======
    protected void init() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        objectLevel = 0;
        listLevel = 0;
    }


<<<<<<< HEAD
    protected JsonLazyLinkedMap createMap () {
=======
    protected JsonLazyLinkedMap createMap() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        if ( objectLevel == levelMaps.length ) {
            objectLevel++;
            return new JsonLazyLinkedMap ( 7 );
        }
<<<<<<< HEAD
        JsonLazyLinkedMap map = levelMaps[ objectLevel ];
        if ( map == null ) {
            map = new JsonLazyLinkedMap ( 10 );
            levelMaps[ objectLevel ] = map;
=======
        JsonLazyLinkedMap map = levelMaps[objectLevel];
        if ( map == null ) {
            map = new JsonLazyLinkedMap ( 10 );
            levelMaps[objectLevel] = map;
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        }
        objectLevel++;
        return map;
    }


<<<<<<< HEAD
    protected ArrayList createList () {
=======
    protected ArrayList createList() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc

        if ( listLevel == levelLists.length ) {
            listLevel++;
            return new ArrayList ( 5 );
        }
<<<<<<< HEAD
        ArrayList list = levelLists[ listLevel ];
        if ( list == null ) {
            list = new ArrayList ( 10 );
            levelLists[ listLevel ] = list;
=======
        ArrayList list = levelLists[listLevel];
        if ( list == null ) {
            list = new ArrayList ( 10 );
            levelLists[listLevel] = list;
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        }
        listLevel++;
        return list;

    }


<<<<<<< HEAD
    protected final ArrayList prepareList ( ArrayList old ) {
=======
    protected final ArrayList prepareList( ArrayList old ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc


        if ( listLevel < levelLists.length ) {
            ArrayList list = new ArrayList ( old );
            old.clear ();
            listLevel--;
            return list;
        } else {
            listLevel--;
            return old;
        }
    }


<<<<<<< HEAD
    protected Object prepareMap ( final JsonLazyLinkedMap map ) {
=======
    protected Object prepareMap( final JsonLazyLinkedMap map ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc

        if ( objectLevel < levelMaps.length ) {
            objectLevel--;
            return map.clearAndCopy ();
        } else {
            objectLevel--;
            return map;
        }
    }


<<<<<<< HEAD
    protected String charDescription ( char c ) {
=======
    protected String charDescription( char c ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
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


}
