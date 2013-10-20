package org.boon.core.primitive;


import org.junit.Test;

import static org.boon.core.primitive.Chr.*;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class ChrTest {


    @Test
    public void allocate() {

        char[] letters =
                arrayOfChar(500);

        assertEquals(
                500,
                len(letters)
        );

    }


    @Test
    public void create() {

        char[] letters =
                array('a', 'b', 'c', 'd');

        assertEquals(
                4,
                len(letters)
        );

    }



    @Test
    public void index() {

        char[] letters =
                array('a', 'b', 'c', 'd');

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
                idx(letters, letters.length - 1 )
        );



        idx(letters, 1, 'z');

        assertEquals(
                'z',
                idx(letters, 1 )
        );
    }



    @Test
    public void slice() {

        char[] letters =
                array('a', 'b', 'c', 'd');




        assertArrayEquals(
                array('a', 'b'),
                slc(letters, 0, 2)
        );

    }

    }
