package org.boon.primitive;

import org.boon.Exceptions;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;
import static org.boon.Exceptions.die;
import static org.boon.primitive.Byt.*;
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


    @Test
    public void doubleTest() {
        ByteBuf buf = ByteBuf.createExact(8);

        //add the double
        buf.add(10.0000000000001);

        byte[] bytes = buf.readAndReset();
        boolean worked = true;

        worked |= idxDouble(bytes, 0) == 10.0000000000001 || die("Double worked");

    }


    @Test
    public void floatTest() {
        ByteBuf buf = ByteBuf.createExact(8);

        //add the float
        buf.add(10.001f);

        byte[] bytes = buf.readAndReset();
        boolean worked = true;

        worked |= buf.len() == 4 || die("Float worked");


        //read the float
        float flt = idxFloat(bytes, 0);

        worked |= flt == 10.001f || die("Float worked");

    }

    @Test
    public void intTest() {
        ByteBuf buf = ByteBuf.createExact(8);

        //Add the int to the array
        buf.add(99);

        byte[] bytes = buf.readAndReset();
        boolean worked = true;


        //Read the int back
        int value = idxInt(bytes, 0);

        worked |= buf.len() == 4 || die("Int worked length = 4");
        worked |= value == 99 || die("Int worked value was 99");

    }

    @Test
    public void charTest() {
        ByteBuf buf = ByteBuf.createExact(8);

        //Add the char to the array
        buf.add('c');

        byte[] bytes = buf.readAndReset();
        boolean worked = true;


        //Read the char back
        int value = idxChar(bytes, 0);

        worked |= buf.len() == 2 || die("char worked length = 4");
        worked |= value == 'c' || die("char worked value was 'c'");

    }


    @Test
    public void shortTest() {
        ByteBuf buf = ByteBuf.createExact(8);

        //Add the short to the array
        buf.add((short)77);

        byte[] bytes = buf.readAndReset();
        boolean worked = true;


        //Read the short back
        int value = idxShort(bytes, 0);

        worked |= buf.len() == 2 || die("short worked length = 2");
        worked |= value == 77 || die("short worked value was 77");

    }


    @Test
    public void byteTest() {
        ByteBuf buf = ByteBuf.createExact(8);

        //Add the byte to the array
        buf.add( (byte)33 );

        byte[] bytes = buf.readAndReset();
        boolean worked = true;


        //Read the byte back
        int value = idx(bytes, 0);

        worked |= buf.len() == 1 || die("byte worked length = 1");
        worked |= value == 33 || die("byte worked value was 33");

    }


    @Test
    public void addRockSockEmRobotEm() {
        boolean worked = true;
        ByteBuf buf = ByteBuf.create(1);

        //Add the various to the array
        buf.add( (byte)  1 );
        buf.add( (short) 2 );
        buf.add( (char)  3 );
        buf.add(         4 );
        buf.add( (float) 5 );
        buf.add( (long)  6 );
        buf.add( (double)7 );

        worked |= buf.len() == 29 || die("length = 29");


        byte[] bytes = buf.readAndReset();

        byte myByte;
        short myShort;
        char myChar;
        int myInt;
        float myFloat;
        long myLong;
        double myDouble;

        myByte    =   idx       ( bytes, 0 );
        myShort   =   idxShort  ( bytes, 1 );
        myChar    =   idxChar   ( bytes, 3 );
        myInt     =   idxInt    ( bytes, 5 );
        myFloat   =   idxFloat  ( bytes, 9 );
        myLong   =    idxLong   ( bytes, 13 );
        myDouble  =   idxDouble ( bytes, 21 );

        worked |= myByte   == 1 || die("value was 1");
        worked |= myShort  == 2 || die("value was 2");
        worked |= myChar   == 3 || die("value was 3");
        worked |= myInt    == 4 || die("value was 4");
        worked |= myFloat  == 5 || die("value was 5");
        worked |= myLong   == 6 || die("value was 6");
        worked |= myDouble == 7 || die("value was 7");

        ByteBuf buf2 = ByteBuf.create( bytes );

        bytes = buf2.readAndReset();

        myDouble  =   idxDouble ( bytes, 21 );
        worked |= myDouble == 7 || die("value was 7");


    }

}
