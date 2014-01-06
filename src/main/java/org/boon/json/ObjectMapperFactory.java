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

    public static ObjectMapper createUseProperties (boolean useJsonDates) {
        JsonParserFactory jpf = new JsonParserFactory();
        jpf.usePropertiesFirst();
        JsonSerializerFactory jsf = new JsonSerializerFactory();

        jsf.usePropertiesFirst();

        if (useJsonDates) {
            jsf.useJsonFormatForDates();
        }
        return new ObjectMapperImpl(jpf, jsf);
    }

    public static ObjectMapper createUseAnnotations (boolean useJsonDates) {
        JsonParserFactory jpf = new JsonParserFactory();
        JsonSerializerFactory jsf = new JsonSerializerFactory();

        jsf.useAnnotations();

        if (useJsonDates) {
            jsf.useJsonFormatForDates();
        }
        return new ObjectMapperImpl(jpf, jsf);
    }


    public static ObjectMapper createUseJSONDates () {
        JsonParserFactory jpf = new JsonParserFactory();
        JsonSerializerFactory jsf = new JsonSerializerFactory();
        jsf.useJsonFormatForDates();
        return new ObjectMapperImpl(jpf, jsf);
    }

}
