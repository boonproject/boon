package org.boon.bugs;

import org.boon.Maps;
import org.boon.json.JsonFactory;
import org.boon.json.JsonParserFactory;
import org.junit.Test;

import java.util.Map;

public class Bug273 {

        public static class A {
            public Map m;
        }

    @Test
        public  void test() {
            A a = new JsonParserFactory().createUTF8DirectByteParser().parse(A.class, "{\"m\":{\"a\":\"b\"}}");
            System.out.println(JsonFactory.toJson(a));
        }
}
