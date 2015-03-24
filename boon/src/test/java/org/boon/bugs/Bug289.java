package org.boon.bugs;

import org.boon.json.JsonParserAndMapper;
import org.boon.json.JsonParserFactory;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by rhightower on 3/23/15.
 */
public class Bug289 {


    public static class Entity {
        String name;
        Integer someNumber;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getSomeNumber() {
            return someNumber;
        }

        public void setSomeNumber(int someNumber) {
            this.someNumber = someNumber;
        }
    }

    @Test
    public void shouldParseNullInteger() {
        //Given
        JsonParserFactory jsonParserFactory = new JsonParserFactory();
        JsonParserAndMapper parserAndMapper = jsonParserFactory.create();


        //When
        Entity entity = parserAndMapper.parse(Entity.class, "{\n" +
                "  \"name\": \"A name\",\n" +
                "  \"someNumber\": null\n" +
                "}");


        //Then
        assertThat(entity.getName(), is("A name"));

        final Integer someNumber = entity.getSomeNumber();
        assertThat(someNumber, is(nullValue()));



        //When again
        entity = parserAndMapper.parse(Entity.class, "{\n" +
                "  \"name\": \"A name\",\n" +
                "  \"someNumber\": 1\n" +
                "}");


        //Then
        assertThat(entity.getName(), is("A name"));

        final int a = entity.getSomeNumber();
        assertThat(a, is(1));
    }
}
