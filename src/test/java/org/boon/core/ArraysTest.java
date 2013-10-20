package org.boon.core;


import org.junit.Test;

import static org.boon.core.Arrays.*;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ArraysTest {




    @Test
    public void allocate() {

        String[] fruit =
                array(String.class, 500);

        assertEquals(
                500,
                len(fruit)
        );

    }

    @Test
    public void addArrayTest() {

        String[] fruit =
                array( "apple", "oranges" );


        String[] veggies =
                array( "green beans", "broccoli" );

        String[] food = add( fruit, veggies );


        assertEquals(
                4,
                len(food)
        );


        assertEquals(
                "apple",
                idx(food, 0)
        );

        assertEquals(
                "oranges",
                idx(food, 1)
        );

        assertEquals(
                "green beans",
                idx(food, 2)
        );


        assertEquals(
                "broccoli",
                idx(food, 3)
        );

    }

    @Test
    public void shrinkTest() {

        String[] fruit =
                array("apple", "oranges", null, "grapes", "kiwi");

        fruit = shrink(fruit, 3 );

        assertEquals(
                2,
                len(fruit)
        );


        assertEquals(
                "apple",
                idx(fruit, 0)
        );

        assertEquals(
                "oranges",
                idx(fruit, 1)
        );
    }

    @Test
    public void growTest() {

        String[] fruit =
                array("apple", "oranges", null, "grapes", "kiwi");

        fruit = grow(fruit, 2 * fruit.length);

        assertEquals(
                15,
                len(fruit)
        );


        assertEquals(
                "apple",
                idx(fruit, 0)
        );

        assertEquals(
                "oranges",
                idx(fruit, 1)
        );


        assertEquals(
                "grapes",
                idx(fruit, 3)
        );


        assertEquals(
                "kiwi",
                idx(fruit, 4)
        );


        assertEquals(
                null,
                idx(fruit, 5)
        );


        assertEquals(
                null,
                idx(fruit, 14)
        );

    }


    @Test
    public void compactTest() {

        String[] fruit =
                array("apple", "oranges", null, "grapes", "kiwi");

        fruit = compact(fruit);

        assertEquals(
                4,
                len(fruit)
        );


        assertEquals(
                "apple",
                idx(fruit, 0)
        );

        assertEquals(
                "oranges",
                idx(fruit, 1)
        );


        assertEquals(
                "grapes",
                idx(fruit, 2)
        );


        assertEquals(
                "kiwi",
                idx(fruit, 3)
        );


    }

    @Test
    public void insertTest() {

        String[] fruit =
                array("apple", "oranges", "pears", "grapes", "kiwi");

        fruit = insert(fruit, 1, "bacon");


        assertEquals(
                6,
                len(fruit)
        );


        assertEquals(
                "bacon",
                idx(fruit, 1)
        );

        assertEquals(
                "apple",
                idx(fruit, 0)
        );


        assertEquals(
                "oranges",
                idx(fruit, 2)
        );


    }

    @Test
    public void insertTest2() {

        String[] fruit =
                array("apple", "oranges", "pears", "grapes", "kiwi");

        fruit = insert(fruit, 0, "bacon");


        assertEquals(
                6,
                len(fruit)
        );


        assertEquals(
                "bacon",
                idx(fruit, 0)
        );

        assertEquals(
                "apple",
                idx(fruit, 1)
        );


        assertEquals(
                "oranges",
                idx(fruit, 2)
        );


    }

    @Test
    public void insertTest3() {

        String[] fruit =
                array("apple", "oranges", "pears", "grapes", "kiwi");

        fruit = insert(fruit, fruit.length-1, "bacon");


        assertEquals(
                6,
                len(fruit)
        );


        assertEquals(
                "apple",
                idx(fruit, 0)
        );

        assertEquals(
                "oranges",
                idx(fruit, 1)
        );


        assertEquals(
                "pears",
                idx(fruit, 2)
        );

        assertEquals(
                "grapes",
                idx(fruit, 3)
        );

        assertEquals(
                "bacon",
                idx(fruit, 4)
        );


        assertEquals(
                "kiwi",
                idx(fruit, 5)
        );


    }

    @Test
    public void addTest() {

        String[] fruit =
                array("apple", "oranges", "pears", "grapes", "kiwi");

        fruit = add(fruit, "bacon");


        assertEquals(
                6,
                len(fruit)
        );


        assertEquals(
                "bacon",
                idx(fruit, 5)
        );

        assertEquals(
                "apple",
                idx(fruit, 0)
        );


        assertEquals(
                "pears",
                idx(fruit, 2)
        );


    }
    @Test
    public void basicTest() {

        String[] fruit =
                array("apple", "oranges", "pears", "grapes", "kiwi");



        assertArrayEquals(
                fruit,
                copy(fruit)
        );

        assertTrue(
                in("apple", fruit)
        );


        assertEquals(
                5,
                len(fruit)
        );


        assertEquals(
                "pears",
                idx(fruit, 2)
        );

        idx(fruit, 2, "bacon");

        assertEquals(
                "bacon",
                idx(fruit, 2)
        );


    }
    @Test
    public void sliceTest() {

        String[] fruit =
                array("apple", "oranges", "pears", "grapes", "kiwi");

        String[] array2  = slc(fruit, 0, 2);

        assertArrayEquals(
                array("apple", "oranges"),
                array2
        );

        String[] array3 = slc(fruit, -3, -2);

        assertArrayEquals(
                array("pears", "grapes"),
                array3
        );

        String[] array4 = slc(fruit, -3);

        assertArrayEquals(
                array("pears", "grapes", "kiwi"),
                array4
        );


        String[] array5 = slcEnd(fruit, -3);

        assertArrayEquals(
                array("apple", "oranges", "pears"),
                array5
        );

    }


}
