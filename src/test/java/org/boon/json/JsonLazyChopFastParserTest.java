package org.boon.json;

import org.boon.core.reflection.fields.FieldAccessMode;
import org.boon.json.implementation.JsonFastParser;

/**
 * Created by rick on 12/12/13.
 */
public class JsonLazyChopFastParserTest extends JsonParserAndMapperBaseTest {
    public JsonParserAndMapper parser () {
        return new JsonParserFactory().setLazyChop( true ).createFastParser();
    }

    public JsonParserAndMapper objectParser () {
        return parser();
    }

}
