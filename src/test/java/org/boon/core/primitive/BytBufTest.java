package org.boon.core.primitive;

import org.boon.core.Exceptions;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;
import static org.boon.core.primitive.Byt.bytes;
import static org.boon.core.primitive.Chr.chars;
import static org.junit.Assert.assertEquals;

public class BytBufTest {


    @Test
    public void testMe() {
        ByteBuf buf = new ByteBuf();
        buf.add(bytes("0123456789\n"));
        buf.add("0123456789\n");
        buf.add("0123456789\n");
        buf.add("0123456789\n");
        buf.add("0123456789\n");
        buf.add("0123456END\n");


        String out = new String(buf.readAndReset(), 0, buf.len());
        assertEquals(66, buf.len());
        assertTrue(out.endsWith("END\n"));

    }

    @Test
    public void testExact() {
        ByteBuf buf = ByteBuf.createExact(66);
        buf.add(bytes("0123456789\n"));
        buf.add("0123456789\n");
        buf.add("0123456789\n");
        buf.add("0123456789\n");
        buf.add("0123456789\n");
        buf.add("0123456END\n");


        String out = new String(buf.readAndReset());
        assertEquals(66, buf.len());
        assertTrue(out.endsWith("END\n"));

    }

    @Test(expected = Exceptions.Exception.class)
    public void testExact2TooSmall() {
        ByteBuf buf = ByteBuf.createExact(22);
        buf.add(bytes("0123456789\n"));
        buf.add("0123456789\n");
        buf.add("0123456789\n");
        buf.add("0123456789\n");
        buf.add("0123456789\n");
        buf.add("0123456END\n");



    }

}
