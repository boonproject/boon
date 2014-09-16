package org.boon.bugs;

import org.boon.Maps;
import org.boon.json.JsonSerializer;
import org.boon.json.JsonSerializerFactory;
import org.junit.Test;

import static org.boon.Boon.puts;

/**
 * Created by Richard on 9/16/14.
 */
public class Bug232 {

    @Test
    public void test() {
        JsonSerializerFactory factory = new JsonSerializerFactory();
        final JsonSerializer jsonSerializer = factory.includeNulls().create();

        final String json = jsonSerializer.serialize(

                Maps.map("job", "programmer",
                        "age", null,
                        "showSize", 12)
        ).toString();

        puts(json);


    }
}
