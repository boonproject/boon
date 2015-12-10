package org.boon.bugs;


import org.boon.Lists;
import org.boon.core.reflection.fields.FieldAccess;
import org.boon.json.JsonSerializer;
import org.boon.json.JsonSerializerFactory;
import org.boon.json.serializers.FieldFilter;
import org.boon.primitive.Arry;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

// https://github.com/advantageous/boon/issues/7
public class ABug7 {

    @Test
    public void testListComplex() {



        JsonSerializer jsonSerializer = new JsonSerializerFactory().setUseAnnotations(true)
                .addFilter(new FieldFilter() {
                    @Override
                    public boolean include(Object parent, FieldAccess fieldAccess) {
                        return !fieldAccess.name().equals("metaClass");
                    }
                }).create();

        final String json = jsonSerializer.serialize(Lists.list(1, 2, null, 3)).toString();

        assertNotNull(json);

        assertEquals("[1,2,null,3]", json);

    }


    @Test
    public void testArrayComplex() {



        JsonSerializer jsonSerializer = new JsonSerializerFactory().setUseAnnotations(true)
                .addFilter(new FieldFilter() {
                    @Override
                    public boolean include(Object parent, FieldAccess fieldAccess) {
                        return !fieldAccess.name().equals("metaClass");
                    }
                }).create();

        final String json = jsonSerializer.serialize(Arry.array(1, 2, null, 3)).toString();

        assertNotNull(json);

        assertEquals("[1,2,null,3]", json);

    }


    @Test
    public void testListSimple() {



        JsonSerializer jsonSerializer = new JsonSerializerFactory().create();

        final String json = jsonSerializer.serialize(Lists.list(1, 2, null, 3)).toString();

        assertNotNull(json);

        assertEquals("[1,2,null,3]", json);

    }


    @Test
    public void testArraySimple() {



        JsonSerializer jsonSerializer = new JsonSerializerFactory().create();

        final String json = jsonSerializer.serialize(Arry.array(1, 2, null, 3)).toString();

        assertNotNull(json);

        assertEquals("[1,2,null,3]", json);

    }
}