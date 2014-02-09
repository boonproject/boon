package org.boon.json;

import org.boon.IO;
import org.boon.json.implementation.JsonParserCharArray;
import org.junit.Test;

import java.math.BigDecimal;
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



}
