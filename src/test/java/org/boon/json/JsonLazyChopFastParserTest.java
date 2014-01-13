package org.boon.json;

import org.boon.core.reflection.fields.FieldAccessMode;
import org.boon.json.implementation.JsonFastParser;

/**
 * Created by rick on 12/12/13.
 */
public class JsonLazyChopFastParserTest extends JsonParserBaseTest {

    public JsonParser parser () {
        return new JsonFastParser ( FieldAccessMode.create( FieldAccessMode.FIELD, true ), false, true, true );
    }

    public JsonParser objectParser () {
        return new JsonFastParser ( FieldAccessMode.create( FieldAccessMode.FIELD, true ), true, false, false );
    }

}
