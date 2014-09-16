package org.boon.bugs;

import com.google.common.hash.HashCode;

import com.google.common.hash.Hasher;
import org.boon.json.JsonSerializer;
import org.boon.json.JsonSerializerFactory;
import org.boon.json.serializers.CustomObjectSerializer;
import org.boon.json.serializers.JsonSerializerInternal;
import org.boon.primitive.CharBuf;
import org.junit.Test;

import java.nio.charset.Charset;

import static com.google.common.hash.Hashing.md5;
import static org.boon.Boon.puts;

/**
 * Created by Richard on 9/16/14.
 */
public class Bugs231 {


    public static String toJson(Object obj) {
        return createSerializer().serialize(obj).toString();
    }

    private static JsonSerializer createSerializer() {
        JsonSerializerFactory factory = new JsonSerializerFactory()
                .addTypeSerializer(HashCode.class, new HashCodeSerializer());
        return factory.create();
    }


    private static class HashCodeSerializer implements CustomObjectSerializer<HashCode> {
        @Override
        public Class<HashCode> type() {
            return HashCode.class;
        }

        @Override
        public void serializeObject(JsonSerializerInternal serializer, HashCode instance, CharBuf builder) {
            serializer.serializeString(instance.toString(), builder);
        }
    }


    @Test
    public void test() {
        Hasher hasher = md5().newHasher();
        hasher.putString("heisann", Charset.defaultCharset());
        HashCode hash = hasher.hash();
        String actualJson = toJson(hash);
        puts(actualJson);
    }
}
