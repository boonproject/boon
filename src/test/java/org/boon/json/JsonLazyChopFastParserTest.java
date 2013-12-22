package org.boon.json;

import org.boon.json.implementation.JsonFastParser;

/**
 * Created by rick on 12/12/13.
 */
public class JsonLazyChopFastParserTest extends JsonParserBaseTest {

   public JsonParser parser() {
       return new JsonFastParser ( false, true, true );
   }
}
