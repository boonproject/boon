package org.boon.bugs;

import org.boon.Boon;
import org.junit.Test;

import static org.boon.Boon.puts;
import static org.junit.Assert.assertTrue;

/**
 * Created by rhightower on 3/23/15.
 */
public class Bug285 {


    public static class Entity {
        String name;
        Integer someNumber;
        Boolean someBoolean = true;

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

        public Boolean getSomeBoolean() {
            return someBoolean;
        }

        public void setSomeBoolean(Boolean aBoolean) {
            this.someBoolean = aBoolean;
        }
    }

    @Test
    public void shouldSerializeBooleanWrapper() {


        assertTrue(Boon.toJson(new Entity()).contains("true}"));
        puts(Boon.toJson(new Entity()));
    }

}
