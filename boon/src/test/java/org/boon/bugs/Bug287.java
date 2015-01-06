package org.boon.bugs;

import org.boon.json.JsonFactory;
import org.boon.json.ObjectMapper;
import org.junit.Test;

import static org.boon.Boon.puts;

/**
 * Created by rhightower on 1/5/15.
 */
public class Bug287 {

    boolean ok;


    public static class SomeClass {
        Class<?> clazz;

        SomeClass(Class<?> clazz) {
            this.clazz = clazz;
        }
    }

    @Test
    public void serializingClassFieldCausesSegFault() {

        SomeClass someClassInstance = new SomeClass(Bug287.class);

        ObjectMapper mapper = JsonFactory.create();

        final String json = mapper.toJson(someClassInstance);

        puts(json);

        SomeClass someClassInstance2 = mapper.readValue("{\"clazz\":\"org.boon.bugs.Bug287\"} ", SomeClass.class);

        ok = someClassInstance2.clazz.getName().equals("org.boon.bugs.Bug287");

    }

}
