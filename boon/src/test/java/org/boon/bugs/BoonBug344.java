package org.boon.bugs;

import org.boon.json.*;
import org.junit.Test;

import java.util.HashMap;

import static junit.framework.Assert.assertEquals;
/**
 * Created by rick on 12/14/15.
 */
public class BoonBug344 {


        @Test
        public void test() {
            ObjectMapper mapper = JsonFactory.create(new JsonParserFactory(),
                    new JsonSerializerFactory().setSerializeMapKeys(true).setEncodeStrings(true));
            HashMap<String, Object> data = new HashMap<>();
            data.put("\"hi\"", Double.parseDouble("1.2312312"));

            assertEquals("{\"\\\"hi\\\"\":1.2312312}", mapper.toJson(data));
        }


        @Test
        public void test2() {
            final JsonSerializerFactory jsonSerializerFactory = new JsonSerializerFactory();
            final JsonSerializer jsonSerializer = jsonSerializerFactory.setSerializeMapKeys(true).setEncodeStrings(true).create();

            HashMap<String, Object> data = new HashMap<>();
            data.put("\"hi\"", Double.parseDouble("1.2312312"));

            assertEquals("{\"\\\"hi\\\"\":1.2312312}", jsonSerializer.serialize(data).toString());
        }
}
