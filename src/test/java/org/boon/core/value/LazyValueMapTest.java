package org.boon.core.value;

import org.boon.IO;
import org.boon.core.Value;
import org.boon.json.JsonParser;
import org.boon.json.implementation.JsonFastParser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.boon.Boon.puts;
import static org.boon.Boon.sputs;
import static org.boon.Exceptions.die;

public class LazyValueMapTest {

    int leafCount;
    int mapCount;
    int collectionCount;
    int integerCount;
    int longCount;
    int doubleCount;
    int stringCount;
    int dateCount;
    int nullCount;
    int listCount;

    @Before
    public void setUp() throws Exception {


        leafCount = 0;
        mapCount = 0;
        collectionCount = 0;
        integerCount = 0;
        longCount = 0;
        doubleCount = 0;
        stringCount = 0;
        dateCount = 0;
        nullCount = 0;
        listCount = 0;
    }

    @After
    public void tearDown() throws Exception {

    }


    @Test
    public void test() {

        Path bigJsonFile = IO.path ( "./files/citm2.json" );
        puts ( bigJsonFile, Files.exists ( bigJsonFile ) );

        JsonParser parser = new JsonFastParser ();

        Map<String, Object> map = parser.parseFile ( Map.class, bigJsonFile.toString () );

        walkMap ( map );

        puts ( "leaf", leafCount, "map", mapCount, "collection", collectionCount );
        puts ( "integer", integerCount, "long", longCount, "double", doubleCount );
        puts ( "string", stringCount, "date", dateCount, "null", nullCount );

    }


    @Test
    public void testGetWalk() {

        Path bigJsonFile = IO.path ( "./files/citm2.json" );
        puts ( bigJsonFile, Files.exists ( bigJsonFile ) );

        JsonParser parser = new JsonFastParser ();

        Map<String, Object> map = parser.parseFile ( Map.class, bigJsonFile.toString () );

        walkGetMap ( map );

        puts ( "leaf", leafCount, "map", mapCount, "list", listCount );
        puts ( "integer", integerCount, "long", longCount, "double", doubleCount );
        puts ( "string", stringCount, "date", dateCount, "null", nullCount );

    }

    private void walkMap( Map map ) {
        mapCount++;
        Set<Map.Entry<String, Object>> entries = map.entrySet ();

        for ( Map.Entry<String, Object> entry : entries ) {
            Object object = entry.getValue ();
            walkObject ( object );
        }

    }


    private void walkGetMap( Map map ) {
        mapCount++;
        Set<Map.Entry<String, Object>> entries = map.entrySet ();

        for ( Map.Entry<String, Object> entry : entries ) {
            walkGetObject ( map.get ( entry.getKey () ) );
        }

        map.size ();

    }
    private void walkObject( Object object ) {
        leafCount++;
        if ( object instanceof Value ) {
            die ( "Found a value" );
        } else if ( object instanceof Map ) {
            walkMap ( ( Map ) object );
        } else if ( object instanceof Collection ) {
            walkCollection ( ( Collection ) object );
        } else if ( object instanceof Long ) {
            longCount++;
        } else if ( object instanceof Integer ) {
            integerCount++;
        } else if ( object instanceof Double ) {
            doubleCount++;
        } else if ( object instanceof String ) {
            stringCount++;
        } else if ( object instanceof Date ) {
            dateCount++;
        } else if ( object == null ) {
            nullCount++;
        } else {
            die ( sputs ( object, object.getClass ().getName () ) );
        }
    }

    private void walkGetObject( Object object ) {
        leafCount++;
        if ( object instanceof Value ) {
            die ( "Found a value" );
        } else if ( object instanceof Map ) {
            walkGetMap ( ( Map ) object );
        } else if ( object instanceof List ) {
            walkGetList ( ( List ) object );
        } else if ( object instanceof Long ) {
            longCount++;
        } else if ( object instanceof Integer ) {
            integerCount++;
        } else if ( object instanceof Double ) {
            doubleCount++;
        } else if ( object instanceof String ) {
            stringCount++;
        } else if ( object instanceof Date ) {
            dateCount++;
        } else if ( object == null ) {
            nullCount++;
        } else {
            die ( sputs ( object, object.getClass ().getName () ) );
        }
    }



    private void walkGetList( List c ) {
        listCount++;
        for ( int index = 0; index < c.size (); index++ ) {
            walkGetObject ( c.get ( index ) );
        }

        c.size();
    }

    private void walkCollection( Collection c ) {
        collectionCount++;
        for ( Object o : c ) {
            walkObject ( o );
        }
    }
}
