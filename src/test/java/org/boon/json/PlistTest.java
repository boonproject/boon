package org.boon.json;

import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.boon.Exceptions.die;

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
                "        Applications = {\n" +
                "                isSymLink = 1;\n" +
                "                symLink   = \"/var/stash/Application/\";\n" +
                "                owner     = root;\n" +
                "                permissions = {\n" +
                "                        root      = (read, write, execute); \n" +
                "                        \"<other>\" = (read, execute);\n" +
                "                };\n" +
                "                numberOfFilesIncluded = 31;\n" +
                "        };\n" +
                "        foo=bar;#comment"+
                "        anotherComment=bar;//lovebucket" +
                "        Library = {\n" +
                "                isSymLink = 0;\n" +
                "                owner     = root;\n" +
                "                permissions = {\n" +
                "                        root      = (read, write, execute); \n" +
                "                        admin     = (read, write, execute); \n" +
                "                        \"<other>\" = (read, execute);\n" +
                "                };\n" +
                "                numberOfFilesIncluded = 23;\n" +
                "        };\n" +
                "        /* etc. */\n" +
                "}";

        Map<String, Object> map = jsonParser.parse ( Map.class, testString );

        Map<String, Object> applications = (Map<String, Object>) map.get ( "Applications" );



        int symlink = (Integer)applications.get("isSymLink");
        boolean ok = symlink == 1 || die();

    }

}
