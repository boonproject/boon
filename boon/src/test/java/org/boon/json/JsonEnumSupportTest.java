package org.boon.json;

import org.junit.Test;

public class JsonEnumSupportTest {


    enum IceCream {
        CHOCOLATE,
        VANILLA;
    }

    public static class Human {
        IceCream likes = IceCream.CHOCOLATE;
    }

    @Test
    public void test() {



        final JsonSerializer serializer = new JsonSerializerFactory()
                .setSerializeAsSupport(false).useFieldsOnly().create();
        final JsonParserAndMapper parser = new JsonParserFactory().create();

        final String input = serializer.serialize(new Human()).toString();

        final Human parse = parser.parse(Human.class, input);

    }
}
