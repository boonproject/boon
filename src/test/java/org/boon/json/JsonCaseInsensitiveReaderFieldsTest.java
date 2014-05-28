package org.boon.json;

import org.boon.json.implementation.ObjectMapperImpl;
import org.junit.Before;
import org.junit.Test;

import static org.boon.Boon.puts;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by mstipanov on 28.05.2014..
 */
public class JsonCaseInsensitiveReaderFieldsTest {
    private ObjectMapper objectMapper;

    @Before
    public void setUp() throws Exception {
        objectMapper = new ObjectMapperImpl(new JsonParserFactory().useFieldsOnly().caseInsensitiveFields(), new JsonSerializerFactory().useFieldsOnly());

    }

    @Test
    public void test_caseInsensitiveProperty_lowercase() {
        String json = "{\"typename\":\"Processes\",\"fields\":[{\"name\":\"process\",\"type\":\"ConversionRateProcess[]\",\"properties\":[\"REQUIRED\"]}]} ";
        ApiDynamicType map = objectMapper.fromJson(json, ApiDynamicType.class);
        puts(json);
        puts(objectMapper.toJson(map));

        assertThat(objectMapper.fromJson(objectMapper.toJson(map)), is(objectMapper.fromJson("{\"typeName\":\"Processes\",\"fields\":[{\"name\":\"process\",\"type\":\"ConversionRateProcess[]\",\"properties\":[\"REQUIRED\"]}]}")));
    }

    @Test
    public void test_caseInsensitiveProperty_normal() {
        String json = "{\"typeName\":\"Processes\",\"fields\":[{\"name\":\"process\",\"type\":\"ConversionRateProcess[]\",\"properties\":[\"REQUIRED\"]}]} ";
        ApiDynamicType map = objectMapper.fromJson(json, ApiDynamicType.class);
        puts(json);
        puts(objectMapper.toJson(map));

        assertThat(objectMapper.fromJson(objectMapper.toJson(map)), is(objectMapper.fromJson("{\"typeName\":\"Processes\",\"fields\":[{\"name\":\"process\",\"type\":\"ConversionRateProcess[]\",\"properties\":[\"REQUIRED\"]}]}")));
    }

    @Test
    public void test_caseInsensitiveProperty_uppercase() {
        String json = "{\"TYPENAME\":\"Processes\",\"fields\":[{\"name\":\"process\",\"type\":\"ConversionRateProcess[]\",\"properties\":[\"REQUIRED\"]}]} ";
        ApiDynamicType map = objectMapper.fromJson(json, ApiDynamicType.class);
        puts(json);
        puts(objectMapper.toJson(map));


        assertThat(objectMapper.fromJson(objectMapper.toJson(map)), is(objectMapper.fromJson("{\"typeName\":\"Processes\",\"fields\":[{\"name\":\"process\",\"type\":\"ConversionRateProcess[]\",\"properties\":[\"REQUIRED\"]}]}")));
    }

    public enum ApiMethodParameterProperty {
        REQUIRED
    }

    public class ApiDynamicType {
        private String typeName;
        private ApiDynamicTypeField[] fields;

        public ApiDynamicType() {
        }
    }

    public class ApiDynamicTypeField {
        private String name;
        private String type;
        private ApiMethodParameterProperty[] properties;
        private String[] allowedValues;
    }
}
