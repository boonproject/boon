package org.boon.bugs;

import org.boon.json.JsonFactory;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by rhightower on 4/18/15.
 */
public class Bug311 {



    public static class SimpleObject {
        float f1 = Float.NEGATIVE_INFINITY;

        float f2 = Float.NaN;

    }

    @Test
    public  void test () {



        final String json = JsonFactory.toJson(new SimpleObject());

        final SimpleObject simpleObject = JsonFactory.fromJson(json, SimpleObject.class);

        assertEquals(Float.NEGATIVE_INFINITY, simpleObject.f1, 0.1);

        assertEquals(Float.NaN, simpleObject.f2, 0.1);

    }
}
