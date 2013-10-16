package org.boon.utils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.boon.utils.Lists.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ListsTest {


    @Test
    public void testMe() {
        List<String> list =   (List<String> ) list((String[])null);

        list =
                list("apple", "oranges", "pears");
        assertEquals(3, len(list)) ;
        assertTrue(in("apple", list));
        assertEquals("oranges", idx(list, 1));
        idx(list, 1, "pear");
        assertEquals("pear", idx(list, 1));
        assertEquals(3, len(list)) ;

        List<String> list2 = copy(list);
        assertEquals(3, len(list2)) ;
        assertTrue(in("apple", list2));
        assertEquals("pear", idx(list2, 1));

        list2 = copy((ArrayList)list);
        assertEquals(3, len(list2)) ;
        assertTrue(in("apple", list2));
        assertEquals("pear", idx(list2, 1));

        add(list, "berry");

        assertEquals("berry", idx(list, 3));


    }

    @Test
    public void testSafe() {
        CopyOnWriteArrayList<String> list = (CopyOnWriteArrayList) safeList("apple", "oranges", "pears");
        assertEquals(3, len(list)) ;
        assertTrue(in("apple", list));
        assertEquals("oranges", idx(list, 1));
        idx(list, 1, "pear");
        assertEquals("pear", idx(list, 1));
        assertEquals(3, len(list)) ;

        List<String> list2 = copy(list);
        assertEquals(3, len(list2)) ;
        assertTrue(in("apple", list2));
        assertEquals("pear", idx(list2, 1));



    }




    @Test
    public void testLinked() {
        LinkedList<String> list = new LinkedList(list("apple", "oranges", "pears"));
        assertEquals(3, len(list)) ;
        assertTrue(in("apple", list));
        assertEquals("oranges", idx(list, 1));
        idx(list, 1, "pear");
        assertEquals("pear", idx(list, 1));
        assertEquals(3, len(list)) ;

        List<String> list2 = copy(list);
        assertEquals(3, len(list2)) ;
        assertTrue(in("apple", list2));
        assertEquals("pear", idx(list2, 1));

        List<String> list3 = copy((List)list);
        assertEquals(3, len(list3)) ;
        assertTrue(in("apple", list3));
        assertEquals("pear", idx(list3, 1));


    }

}
