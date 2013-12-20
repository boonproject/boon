package org.boon.json;

import org.boon.IO;
import org.junit.Test;

import java.util.Map;


/**
 * Created by rick on 12/12/13.
 */
public class JsonCharArray extends JsonParserBaseTest {


    public JsonParserFactory factory () {
        return new JsonParserFactory ()
                .neverPreferCharSequence ()
                .neverUseDirectBytes ()
                .neverUseLazyFinalParse ().setSizeToForceLazyFinalParse ( 0 );
    }


    @Test
    public void testNest () {


        String nest = IO.read ( "files/nest.json" );

        this.jsonParser.parse ( Map.class, nest );


    }

    @Test
    public void noNest () {


        String json = IO.read ( "files/nonest.json" );

        this.jsonParser.parse ( Map.class, json );


    }

}
