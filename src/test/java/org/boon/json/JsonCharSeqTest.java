package org.boon.json;


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


}