package org.boon.core.primitive;

import org.junit.Test;


import static org.boon.core.primitive.Byt.*;
import static org.junit.Assert.*;

public class BytTest {


    @Test
    public void allocate() {

        byte[] letters =
                arrayOfByte(500);

        assertEquals(
                500,
                len(letters)
        );

    }


    @Test
    public void create() {

        byte[] letters =
                array((byte)0, (byte)1, (byte)2, (byte)3);

        assertEquals(
                4,
                len(letters)
        );

    }


    @Test
    public void index() {

        byte[] letters =
                array((byte)'a', (byte)'b', (byte)'c', (byte)'d');

        assertEquals(
                'a',
                idx(letters, 0)
        );


        assertEquals(
                'd',
                idx(letters, -1)
        );


        assertEquals(
                'd',
                idx(letters, letters.length - 1)
        );


        idx(letters, 1, (byte)'z');

        assertEquals(
                (byte)'z',
                idx(letters, 1)
        );
    }

    @Test
    public void isIn() {

        byte[] letters =
                array((byte)'a',(byte) 'b', (byte)'c', (byte)'d');


        assertTrue(
                in((byte)'a', letters)
        );

        assertFalse(
                in((byte)'z', letters)
        );

    }

    @Test
    public void slice() {

        byte[] letters =
                array((byte)'a', (byte)'b', (byte)'c', (byte)'d');


        assertArrayEquals(
                array((byte)'a', (byte)'b'),
                slc(letters, 0, 2)
        );

        assertArrayEquals(
                array((byte)'b', (byte)'c'),
                slc(letters, 1, -1)
        );

        //>>> letters[2:]
        //['c', 'd']
        //>>> letters[-2:]
        //['c', 'd']

        assertArrayEquals(
                array((byte)'c', (byte)'d'),
                slc(letters, -2)
        );


        assertArrayEquals(
                array((byte)'c', (byte)'d'),
                slc(letters, 2)
        );


        //>>> letters[:-2]
        //     ['a', 'b']
        assertArrayEquals(
                array((byte)'a', (byte)'b'),
                slcEnd(letters, -2)
        );


        //>>> letters[:-2]
        //     ['a', 'b']
        assertArrayEquals(
                array((byte)'a',(byte) 'b'),
                slcEnd(letters, 2)
        );

    }




    @Test
    public void outOfBounds() {

        byte[] fruit =
                array((byte)'a', (byte)'b', (byte)'c', (byte)'d', (byte)'e');

        slcEnd(fruit, 100);
        slcEnd(fruit, -100);

        slc(fruit, 100);
        slc(fruit, -100);
        idx(fruit, 100);
        idx(fruit, -100);


        idx(fruit, 100, (byte)'x');


        idx(fruit, -100, (byte)'z');


        assertEquals(
                (byte)'x',
                idx(fruit, -1)
        );


        assertEquals(
                (byte)'z',
                idx(fruit, 0)
        );

    }



    @Test
    public void growTest() {
        byte[] letters =
                array((byte)'a', (byte)'b', (byte)'c', (byte)'d', (byte)'e');

        letters = grow( letters,  21);


        assertEquals(
                'e',
                idx(letters, 4)
        );


        assertEquals(
                'a',
                idx(letters, 0)
        );




        assertEquals(
                len(letters),
                26
        );


        assertEquals(
                '\0',
                idx(letters, 20)
        );


        letters =  shrink ( letters, 23 );

        assertArrayEquals(
                array((byte)'a', (byte)'b', (byte)'c'),
                letters

        );

    }



    @Test
    public void growFast() {
        byte[] letters =
                array((byte)'a', (byte)'b', (byte)'c', (byte)'d', (byte)'e');

        letters = grow( letters );


        assertEquals(
                'e',
                idx(letters, 4)
        );


        assertEquals(
                'a',
                idx(letters, 0)
        );




        assertEquals(
                len(letters),
                10
        );


        assertEquals(
                '\0',
                idx(letters, 9)
        );


    }



    @Test
    public void compactTest() {
        byte[] letters =
                array((byte)'a', (byte)'b', (byte)'\0', (byte)'\0',
                        (byte)'\0', (byte)'\0', (byte)'\0', (byte)'c', (byte)'\0', (byte)'d', (byte)'e');

        letters = compact(letters);

        assertArrayEquals(
                array((byte)'a', (byte)'b', (byte)'c', (byte)'d', (byte)'e'),
                letters

        );



    }


    @Test
    public void copyTest() {

        assertArrayEquals(
                array((byte)'a', (byte)'b', (byte)'c', (byte)'d', (byte)'e'),
                copy(array((byte)'a', (byte)'b', (byte)'c', (byte)'d', (byte)'e'))

        );



    }


    @Test
    public void addTest() {

        assertArrayEquals(
                array((byte)'a', (byte)'b', (byte)'c', (byte)'d', (byte)'e', (byte)'f'),
                add(array((byte)'a', (byte)'b', (byte)'c', (byte)'d', (byte)'e'), (byte)'f') );




    }

    @Test
    public void addTestArray() {

        assertArrayEquals(
                array(     (byte)'a', (byte)'b', (byte)'c', (byte)'d', (byte)'e', (byte)'f'),
                add( array((byte)'a', (byte)'b', (byte)'c', (byte)'d'), array((byte)'e', (byte)'f') )

        );



    }

    void foo(byte a) {

    }

    @Test
    public void addInsertSingle() {

        byte f = 0;

        foo( f = 0xa );

        foo( f = 'a' );

        foo( f = 1 );


        assertArrayEquals(
                array((byte)'a', (byte)'b', (byte)'c', (byte)'d', (byte)'e', (byte)'f', (byte)'g'),
                insert( array((byte)'a', (byte)'b', (byte)'d', (byte)'e', (byte)'f', (byte)'g'), 2, (byte)'c' )

        );

        assertArrayEquals(
                array((byte)'a', (byte)'b', (byte)'c', (byte)'d', (byte)'e', (byte)'f', (byte)'g'),
                insert( array((byte)'b', (byte)'c', (byte)'d', (byte)'e', (byte)'f', (byte)'g'), 0, (byte)'a' )

        );

        assertArrayEquals(
                array((byte)'a', (byte)'b', (byte)'c', (byte)'d', (byte)'e', (byte)'f', (byte)'g'),
                insert( array((byte)'a', (byte)'b', (byte)'c', (byte)'d', (byte)'e', (byte)'g'), 5, (byte)'f' )

        );


    }


    @Test
    public void addInsertEdge() {
        assertArrayEquals(
                array(new byte[]{'a', 'b', 'c', 'd', 'e', 'f', 'g'}),
                insert( array(new byte[]{'a', 'b', 'c', 'd', 'e', 'f'}), 6, (byte)'g' )

        );

        assertArrayEquals(
                array(new byte[]{'a', 'b', 'c', 'd', 'e', 'f', 'g'}),
                insert( array(new byte[]{'a', 'b', 'c', 'd', 'e', 'f'}), 7, (byte)'g' )

        );

    }

    @Test
    public void addInsertArray() {

        assertArrayEquals(
                array(new byte[]{'a', 'b', 'c', 'd', 'e', 'f', 'g'}),
                insert( array(new byte[]{'a', 'b', 'e', 'f', 'g'}), 2, array(new byte[]{'c', 'd'}) )

        );

        assertArrayEquals(
                array(new byte[]{'a', 'b', 'c', 'd', 'e', 'f', 'g'}),
                insert( array(new byte[]{'c', 'd', 'e', 'f', 'g'}), 0, array(new byte[]{'a', 'b'}) )

        );



    }



    @Test
    public void addInsertArrayEnd() {

        assertArrayEquals(
                array(new byte[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i'}),
                insert( array(new byte[]{'a', 'b', 'c', 'd', 'e', 'h', 'i'}), 5, array(new byte[]{'f', 'g'}) )

        );


    }



    @Test
    public void addInsertArrayEnd2() {



        assertArrayEquals(
                array(new byte[]{'a', 'b', 'c', 'd', 'e', 'h', 'f', 'g', 'i'}),
                insert( array(new byte[]{'a', 'b', 'c', 'd', 'e', 'h', 'i'}), 6, array(new byte[]{'f', 'g'}) )

        );

    }



    @Test
    public void addInsertArrayEnd3() {



        assertArrayEquals(
                array(new byte[]{'a', 'b', 'c', 'd', 'e', 'h', 'i', 'f', 'g'} ),
                insert( array(new byte[]{'a', 'b', 'c', 'd', 'e', 'h', 'i'}), 7, array(new byte[]{'f', 'g'}) )

        );

    }


}
