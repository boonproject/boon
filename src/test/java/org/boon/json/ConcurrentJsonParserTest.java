package org.boon.json;


import org.boon.json.implementation.JsonParserConcurrent;
import org.junit.After;

public class ConcurrentJsonParserTest extends JsonParserBaseTest {

    JsonParserConcurrent jsonParserConcurrent;
    public JsonParser parser () {
        jsonParserConcurrent =  new JsonParserConcurrent();
        return  jsonParserConcurrent;
    }


    @After
    public void after () {
      jsonParserConcurrent.close ();
    }
}
