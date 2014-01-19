package org.boon.json;

import org.junit.After;
import org.junit.Test;

public class JsonParserUsingCharacterSource extends JsonParserBaseTest {

    JsonParser jsonCharSrcParser;

    public JsonParser parser () {
        jsonCharSrcParser =  new org.boon.json.implementation.JsonParserUsingCharacterSource (  );
        return  jsonCharSrcParser;
    }


    @After
    public void after () {
        //TODO either have it auto-close or delegate close to char source and then to reader
        jsonCharSrcParser.close ();
    }


}