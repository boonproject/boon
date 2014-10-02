package org.boon.bugs;

import org.junit.Test;
import groovy.lang.GString;
import org.boon.json.JsonSerializer;
import org.boon.json.JsonSerializerFactory;
import org.boon.json.serializers.JsonSerializerInternal;
import org.boon.json.serializers.impl.AbstractCustomObjectSerializer;
import org.boon.primitive.CharBuf;
import org.codehaus.groovy.runtime.GStringImpl;

import static org.boon.Boon.puts;


/**
 * Created by Richard on 9/14/14.
 */
public class Bug197 {


    public static final class JsonEncoder {

        private JsonEncoder() {}

        public static String toJson(Object obj) {
            JsonSerializerFactory jsonSerializerFactory = new JsonSerializerFactory()
                    .addTypeSerializer(GStringImpl.class, new AbstractCustomObjectSerializer(GStringImpl.class) {

                        @Override
                        public void serializeObject(JsonSerializerInternal serializer, Object instance, CharBuf builder) {
                            builder.addString(((GString) instance).toString());
                        }
                    });
            JsonSerializer serializer = jsonSerializerFactory.create();
            return serializer.serialize(obj).toString();
        }
    }

    @Test
    public void test() {


       String value = "hello";
       String actualJson = JsonEncoder.toJson(new String("Not AGString $value"));

       puts(actualJson);



    }
}
