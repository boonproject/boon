package org.boon.json;

/**
 * Created by rick on 12/12/13.
 */
public class JsonLazyChopFastParserTest extends JsonParserBaseTest{


    public JsonParserFactory factory () {
        return new JsonParserFactory ().neverPreferCharSequence ().neverUseDirectBytes ().useLazyFinalParse ().setSizeToForceLazyFinalParse ( Integer.MAX_VALUE );
    }

}
