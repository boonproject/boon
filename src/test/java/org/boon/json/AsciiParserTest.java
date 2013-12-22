package org.boon.json;

import org.boon.json.implementation.JsonAsciiParser;

/**
 * Created by rick on 12/15/13.
 */
public class AsciiParserTest extends JsonParserBaseTest {


    public JsonParserFactory factory () {
        return new JsonParserFactory() {
            public JsonParser create () {
                return new JsonAsciiParser();
            }
        };

    }


}