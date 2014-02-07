package org.boon.json;

import org.boon.IO;
import org.boon.json.implementation.JsonParserCharArray;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.boon.Boon.puts;
import static org.boon.Exceptions.die;
import static org.boon.Str.lines;


/**
 * Created by rick on 12/12/13.
 */
public class JsonCharArrayTest extends JsonParserAndMapperBaseTest {


    public JsonParserAndMapper parser () {
        return new JsonParserFactory().createJsonCharArrayParser();
    }


    public JsonParserAndMapper objectParser () {
        return parser();
    }

    @Test
    public void testNest () {


        String nest = IO.read ( "files/nest.json" );

        this.jsonParserAndMapper.parse ( Map.class, nest );


    }

    @Test
    public void noNest () {


        String json = IO.read ( "files/nonest.json" );

        this.jsonParserAndMapper.parse ( Map.class, json );


    }


    @Test
    public void classic() {

            Map<String, Object> map = ( Map<String, Object> ) jsonParserAndMapper.parse ( Map.class,
                    lines (

                            "{ \"nums\": [12, 12345678, 999.999, 123456789.99],\n " +
                                    "    \"nums2\": [12, 12345678, 999.999, 123456789.99],\n" +
                                    "    \"nums3\": [12, 12345678, 999.999, 123456789.99]\n" +
                                    "}"
                    )
            );

        }



    @Test
    public void parseNegativeLong () {
        int i = jsonParserAndMapper.parseInt ( "123" );
        boolean ok = i == 123 || die ( "" + i );

        long l =  jsonParserAndMapper.parseLong ( "-123456789099" );
        ok = l == -123456789099L || die ( "" + l );

        puts ( ok );
    }


}
