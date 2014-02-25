package org.boon.json;

import org.boon.json.implementation.ObjectMapperImpl;

import java.io.Reader;
import java.util.List;

/**
 * Created by rick on 1/4/14.
 */
public class JsonFactory {


    private static ObjectMapper json = JsonFactory.create();

    public static ObjectMapper create () {
          return new ObjectMapperImpl();
    }

    public static String toJson(Object value) {
         return json.toJson( value );
    }

    public static void toJson(Object value, Appendable appendable) {
         json.toJson( value, appendable );
    }

    public static <T> T fromJson(String str, Class<T> clazz) {
        return json.fromJson(str, clazz);
    }


    public static <T> List<T> fromJsonArray(String str, Class<T> clazz) {
        return json.parser().parseList(clazz, str);
    }

    public static Object fromJson(String str) {
         return json.fromJson(str);
    }

    public static Object fromJson(Reader reader) {
        return json.fromJson(reader);
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
