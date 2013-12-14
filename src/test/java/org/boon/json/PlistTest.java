package org.boon.json;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.Map;

import static org.boon.Exceptions.die;
import static org.boon.Maps.idx;

/**
 * Created by rick on 12/13/13.
 */
public class PlistTest {

    JsonParser jsonParser;


    public JsonParserFactory factory () {
        return new JsonParserFactory ();
    }

    @Before
    public void setup() {

        jsonParser = factory().plistStyle().create ();

    }

    @Test public void basic () {
        String testString = "{\n" +
                "                date=\"1994-11-05T08:15:30Z\";\n" +
                "        Applications = {\n" +
                "                isSymLink = 1;\n" +
                "                symLink   = \"/var/stash/Application/\";\n" +
                "                owner     = root;\n" +
//                "                permissions = {\n" +
//                "                        root      = (read, write, execute); \n" +
//                "                        \"<other>\" = (read, execute);\n" +
//                "                };\n" +
                "                numberOfFilesIncluded = 31;\n" +
                "                date=\"1994-11-05T08:15:30Z\";//lovebucket\n" +
                "        };\n" +
//                "        foo=bar;#comment \n"+
//                "        anotherComment=bar;//lovebucket \n" +
//                "        Library = {\n" +
//                "                isSymLink = 0;\n" +
//                "                owner     = root;\n" +
//                "                permissions = {\n" +
//                "                        root      = (read, write, execute); \n" +
//                "                        admin     = (read, write, execute); \n" +
//                "                        \"<other>\" = (read, execute);\n" +
//                "                };\n" +
//                "                numberOfFilesIncluded = 23;\n" +
//                "        };\n" +
//                "        /* etc. */\n" +
                "}";

        Map<String, Object> map = jsonParser.parse ( Map.class, testString );

        Map<String, Object> applications = (Map<String, Object>) map.get ( "Applications" );



        int symlink = (Integer)applications.get("isSymLink");
        boolean ok = symlink == 1 || die();
        ok &=  idx(map, "date").toString ().equals ( "Sat Nov 05 00:15:30 PST 1994" ) || die("I did not find:" + idx(map, "date") +"#");

    }


    @Test public void basic2 () {
//                "  b = { b1 = (read, write); \n b2 = (execute);\n };\n" +

        String testString = "{\n" +
                " a = {\n"   +
                "    b = { b1=foo;};"+
                "    c = 31;\n" +
                "    d =\"1994-11-05T08:15:30Z\";" +
                " };\n" +
                "}" ;


        Map<String, Object> map = jsonParser.parse ( Map.class, testString );
        boolean ok = map.size () == 1 || die();
        ok = map.containsKey ( "a" ) || die();
        ok = !map.containsKey ( "b" ) || die();
        ok = !map.containsKey ( "c" ) || die();
        ok = !map.containsKey ( "d" ) || die();

        Map<String, Object> a = ( Map<String, Object> ) map.get ( "a" );

       int c  = (int) a.get ( "c" );
        ok = c == 31 || die();

        Date d  = (Date ) a.get ( "d" );

        ok = d.toString ().equals ( "Sat Nov 05 00:15:30 PST 1994"  )|| die("" +d);


        Map<String, Object> b = ( Map<String, Object> ) a.get ( "b" );
        String b1  = ( String ) b.get ( "b1" );

        ok = b1.equals ( "foo"  )|| die("" +b1);


    }

}
