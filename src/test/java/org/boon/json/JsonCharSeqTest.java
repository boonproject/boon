package org.boon.json;

import org.junit.Test;

/**
 * Created by rick on 12/15/13.
 */
public class JsonCharSeqTest extends JsonParserBaseTest {

    public JsonParserFactory factory () {
        return new JsonParserFactory ()
                .preferCharSequence ()
                .neverUseDirectBytes ()
                .neverUseOverlay ().setSizeToUseOverlay ( 0 );
    }


    //Fix this test
    @Test
    public void testLists() {

    }


    //Fix this test
    @Test
    public  void testFiles () {

    }

}