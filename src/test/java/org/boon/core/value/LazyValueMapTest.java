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
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

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

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }


    @Test
    public void test() {

        Path bigJsonFile = IO.path ( "./files/citm2.json" );
        puts ( bigJsonFile, Files.exists ( bigJsonFile ) );

        JsonParser parser = new JsonFastParser (  );

        Map<String, Object> map = parser.parseFile( Map.class, bigJsonFile.toString () );

        walkMap(map);

        puts("leaf", leafCount, "map", mapCount, "collection", collectionCount);
        puts("integer", integerCount, "long", longCount, "double", doubleCount );
        puts("string", stringCount, "date", dateCount, "null", nullCount);

    }

    private void walkMap( Map map ) {
        mapCount++;
        Set<Map.Entry<String,Object>> entries = map.entrySet ();

        for ( Map.Entry<String, Object> entry :  entries ) {
            Object object = entry.getValue ();
            walkObject( object );
        }

    }

    private void walkObject( Object object ) {
        leafCount++;
        if (object instanceof Value ) {
            die ("Found a value");
        } else if (object instanceof  Map ) {
            walkMap ( (Map) object );
        } else if ( object instanceof Collection ) {
            walkCollection ( (Collection) object );
        } else if (object instanceof Long) {
            longCount++;
        } else if (object instanceof Integer) {
            integerCount++;
        } else if (object instanceof Double ){
            doubleCount++;
        } else if (object instanceof String ) {
            stringCount++;
        } else if (object instanceof Date ) {
            dateCount++;
        } else if ( object == null ) {
            nullCount++;
        } else {
            die ( sputs ( object, object.getClass().getName ()) );
        }
    }

    private void walkCollection( Collection c ) {
        collectionCount++;
        for (Object o : c) {
            walkObject ( o );
        }
    }
}
