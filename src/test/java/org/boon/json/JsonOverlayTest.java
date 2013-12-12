package org.boon.json;

/**
 * Created by rick on 12/12/13.
 */
public class JsonOverlayTest extends JsonParserBaseTest{


    public JsonParserFactory factory () {
        return new JsonParserFactory ().neverPreferCharSequence ().neverUseDirectBytes ().useOverlay ().setSizeToUseOverlay ( Integer.MAX_VALUE );
    }

}
