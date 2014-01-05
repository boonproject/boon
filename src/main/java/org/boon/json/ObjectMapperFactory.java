package org.boon.json;

import org.boon.json.implementation.ObjectMapperImpl;

/**
 * Created by rick on 1/4/14.
 */
public class ObjectMapperFactory {

    public static ObjectMapper create () {
          return new ObjectMapperImpl();
    }

    public static ObjectMapper create (JsonParserFactory parserFactory, JsonSerializerFactory serializerFactory) {
        return new ObjectMapperImpl(parserFactory, serializerFactory);
    }

}
