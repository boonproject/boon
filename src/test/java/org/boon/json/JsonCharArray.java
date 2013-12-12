package org.boon.json;

/**
 * Created by rick on 12/12/13.
 */
public class JsonCharArray extends JsonParserBaseTest {


    public JsonParserFactory factory () {
        return new JsonParserFactory ()
                .neverPreferCharSequence ()
                .neverUseDirectBytes ()
                .neverUseOverlay ().setSizeToUseOverlay ( 0 );
    }


}
