package org.boon.utils;


import org.junit.Test;


import java.util.NavigableSet;
import java.util.Set;

import static org.boon.utils.Sets.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SetsTest {


    @Test
    public void simple() {
        Set<String> set =
                set("apple", "oranges", "pears", "grapes", "kiwi");

        assertEquals(5, len(set)) ;
        assertTrue(in("apple", set));

    }


    @Test
    public void sorted() {
        NavigableSet<String> set =
                sortedSet("apple", "kiwi", "oranges", "pears", "pineapple");

        assertEquals(
                5,
                len(set)
        );

        assertTrue(
                in("apple", set)
        );

        assertEquals(

                "oranges", idx(set, "ora")

        );

        assertEquals(

                "oranges", idx(set, "o")

        );

        assertEquals(

                "pears",
                idx(set, "p")

        );

        assertEquals(

                "pineapple",
                idx(set, "pi")

        );

        assertEquals(

                "pineapple",
                after(set, "pi")

        );

        assertEquals(

                "pears",
                before(set, "pi")

        );


        assertEquals(

                sortedSet("apple", "kiwi"),
                slc(set, "ap", "o")

        );

        assertEquals(

                sortedSet("apple", "kiwi"),
                slc(set, "o")

        );

        assertEquals(

                sortedSet("oranges", "pears", "pineapple"),
                slcEnd(set, "o")
        );

    }

}