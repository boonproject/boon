package org.boon.json;

import org.boon.json.streaming.version1.StreamingJavaParserV1;
import org.junit.Test;

public class Streaming1Test extends JsonParserBaseTest{


    public JsonParser parser () {
        return new StreamingJavaParserV1();
    }



    @Test //Groovy streaming parser does not support numbers as top level root object.
    public void testNumber () {
    }

    @Test//Top level booleans not supported
    public void testBoolean () {

    }



    @Test ///Groovy streaming parser does not support numbers as top level root object.
    public void testString () {

    }


    @Test ///Groovy streaming parser does not support numbers as top level root object.
    public void parseArray () {

    }

    @Test ///Groovy streaming parser does not support numbers as top level root object.
    public void parseNumber () {
    }




}