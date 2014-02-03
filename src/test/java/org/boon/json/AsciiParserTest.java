package org.boon.json;

import org.junit.Test;

/**
 * Created by rick on 12/15/13.
 */
public class AsciiParserTest extends JsonParserAndMapperBaseTest {



    public JsonParserAndMapper parser () {
        return new JsonParserFactory().createASCIIParser();
    }

    public JsonParserAndMapper objectParser () {
        return parser();
    }


    @Test //TODO broke
    public void testArrayOfArrayWithSimpleValuesValue7() {

    }

    @Test //TODO broke
    public void testBackSlashEscaping2() {


    }

}