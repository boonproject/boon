package org.boon.json;

import org.boon.json.implementation.JsonParserLax;
import org.boon.json.implementation.PlistParser;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.boon.Boon.putl;
import static org.boon.Boon.puts;
import static org.boon.Exceptions.die;
import static org.boon.Maps.idx;

/**
 * Created by rick on 12/13/13.
 */
public class PlistTest {


    protected void inspectMap ( Map<String, Object> map ) {
        final Set<Map.Entry<String, Object>> entrySet = map.entrySet ();
        putl ( "map", map, "size", map.size (), "keys", map.keySet (), "values", map.values () );

        for ( String key : map.keySet () ) {
            puts ( "key", "#" + key + "#" );
        }

        for ( Object value : map.values () ) {
            puts ( "value", "#" + value + "#" );
        }

    }


    public JsonParser parser () {
        return new PlistParser ( false, true, true );

    }

    public JsonParser objectParser () {
        return new JsonParserLax ( true, false, false );

    }

    @Test
    public void basic () {
        String testString = "{\n" +
                "                date=\"1994-11-05T08:15:30Z\";\n" +
                "                \"foo\" = \"bar\";\n" +
                "        Applications = {\n" +
                "                isSymLink = 1;\n" +
                "                symLink   = \"/var/stash/Application/\";\n" +
                "                owner     = root;\n" +
                "                permissions = {\n" +
                "                        root      = (read, write, execute); \n" +
                "                        \"other\" = (read, execute);\n" +
                "                };\n" +
                "                numberOfFilesIncluded = 31;\n" +
                "                date=\"1994-11-05T08:15:30Z\";//lovebucket\n" +
                "        };\n" +
                "        anotherComment=bar;\n" +
                "        Library = {\n" +
                "                isSymLink = 0;\n" +
                "                owner     = root;\n" +
                "                permissions = {\n" +
                "                        root      = (read, //read this \n" +
                "                                     write, # write this\n" +
                "                                    /* hi */ execute); \n" +
                "                        admin     = (read, write, execute); \n" +
                "                        \"<other>\" = (read, execute);\n" +
                "                };\n" +
                "                numberOfFilesIncluded = 23;\n" +
                "        };\n" +
                "        /* etc. */\n" +
                "}";

        Map<String, Object> map = parser ().parse ( Map.class, testString );

        boolean ok = map.size () == 5 || die ( "" + map.size () );

        Map<String, Object> applications = ( Map<String, Object> ) map.get ( "Applications" );

        ok = idx ( map, "date" ).toString ().equals ( "Sat Nov 05 00:15:30 PST 1994" ) || die ( "I did not find:" + idx ( map, "date" ) + "#" );


        inspectMap ( map );
        int symlink = ( Integer ) applications.get ( "isSymLink" );
        ok = symlink == 1 || die ();

        Map<String, Object> library = ( Map<String, Object> ) map.get ( "Library" );
        symlink = ( Integer ) library.get ( "isSymLink" );
        ok = symlink == 0 || die ();

        int numberOfFilesIncluded = ( Integer ) library.get ( "numberOfFilesIncluded" );
        ok = numberOfFilesIncluded == 23 || die ();

        Map<String, Object> permissions2 = ( Map<String, Object> ) library.get ( "permissions" );
        ok = permissions2.get ( "root" ).toString ().equals ( "[read, write, execute]" ) || die ( "" + permissions2 );

    }


    @Test
    public void basic2 () {

        String testString = "{\n" +
                " a = {\n" +
                "    b = { b1=foo; b2=1; b3={}; b4=();};" +
                "    c = 31;\n" +
                "    d =\"1994-11-05T08:15:30Z\";" +
                " };" +
                " map2={" +


                "};\n" +
                "}";


        Map<String, Object> map = parser ().parse ( Map.class, testString );

        inspectMap ( map );
        inspectMap ( map );
        boolean ok = map.size () == 2 || die ();
        ok = map.containsKey ( "a" ) || die ();
        ok = !map.containsKey ( "b" ) || die ();
        ok = !map.containsKey ( "c" ) || die ();
        ok = !map.containsKey ( "d" ) || die ();

        Map<String, Object> a = ( Map<String, Object> ) map.get ( "a" );

        int c = ( int ) a.get ( "c" );
        ok = c == 31 || die ();

        Date d = ( Date ) a.get ( "d" );

        ok = d.toString ().equals ( "Sat Nov 05 00:15:30 PST 1994" ) || die ( "" + d );


        Map<String, Object> b = ( Map<String, Object> ) a.get ( "b" );
        String b1 = ( String ) b.get ( "b1" );
        int b2 = ( int ) b.get ( "b2" );

        ok = b1.equals ( "foo" ) || die ( "" + b1 );

        Map<String, Object> b3 = ( Map<String, Object> ) b.get ( "b3" );


        ok = b3.toString ().equals ( "{}" ) || die ( "" + b3 );


        List<Object> b4 = ( List<Object> ) b.get ( "b4" );

        ok = b4.toString ().equals ( "[]" ) || die ( "" + b4 );

        Map<String, Object> map2 = ( Map<String, Object> ) map.get ( "map2" );


    }


    @Test
    public void basic3 () {
//                "  b = { b1 = (read, write); \n b2 = (execute);\n };\n" +

        String testString = "{\n" +
                " a = {\n" +
                "    b = {      b1=foo; \n" +
                "               b2=1; \n" +
                "               b3={};\n " +
                "               b4=();\n" +
                "    };" +
                "    c = 31;\n" +
                "    d =\"1994-11-05T08:15:30Z\";" +
                " };\n" +
                "}";


        Map<String, Object> map = parser ().parse ( Map.class, testString );
        inspectMap ( map );
        boolean ok = map.size () == 1 || die ();
        ok = map.containsKey ( "a" ) || die ();
        ok = !map.containsKey ( "b" ) || die ();
        ok = !map.containsKey ( "c" ) || die ();
        ok = !map.containsKey ( "d" ) || die ();

        Map<String, Object> a = ( Map<String, Object> ) map.get ( "a" );

        int c = ( int ) a.get ( "c" );
        ok = c == 31 || die ();

        Date d = ( Date ) a.get ( "d" );

        ok = d.toString ().equals ( "Sat Nov 05 00:15:30 PST 1994" ) || die ( "" + d );


        Map<String, Object> b = ( Map<String, Object> ) a.get ( "b" );
        String b1 = ( String ) b.get ( "b1" );
        int b2 = ( int ) b.get ( "b2" );

        ok = b1.equals ( "foo" ) || die ( "" + b1 );

        Map<String, Object> b3 = ( Map<String, Object> ) b.get ( "b3" );


        ok = b3.toString ().equals ( "{}" ) || die ( "" + b3 );


        List<Object> b4 = ( List<Object> ) b.get ( "b4" );

        ok = b4.toString ().equals ( "[]" ) || die ( "" + b4 );

    }

}
