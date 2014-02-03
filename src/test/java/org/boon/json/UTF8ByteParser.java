package org.boon.json;


import org.junit.Test;

/**
 * Created by rick on 12/15/13.
 */
public class UTF8ByteParser extends JsonParserAndMapperBaseTest {


    public JsonParserAndMapper parser () {
        return new JsonParserFactory().createUTF8DirectByteParser();
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