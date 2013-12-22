package org.boon.json;

import org.boon.json.implementation.JsonUTF8Parser;

/**
 * Created by rick on 12/15/13.
 */
public class UTF8ByteParser extends JsonParserBaseTest {


    public JsonParserFactory factory() {
        return new JsonParserFactory() {
            public JsonParser create() {
                return new JsonUTF8Parser();
            }
        };

    }


}