package org.boon;

import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.boon.Lists.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ListsTest {


    @Test
    public void iterAndFriends () {
        List<String> list =
                list ( "apple", "oranges", "pears", "grapes", "kiwi" );

        iterAndFriends ( list );

        list =
                safeList ( "apple", "oranges", "pears", "grapes", "kiwi" );

        iterAndFriends ( list );


        list =
                linkedList ( "apple", "oranges", "pears", "grapes", "kiwi" );

        iterAndFriends ( list );

        list =
                linkedList ( list );

        iterAndFriends ( list );

        list =
                safeList ( list );

        iterAndFriends ( list );


        list =
                copy ( safeList ( list ) );

        iterAndFriends ( list );


        list =
                copy ( linkedList ( list ) );

        iterAndFriends ( list );


        list =
                copy ( list ( list ) );

        iterAndFriends ( list );


    }


    public void iterAndFriends ( List<String> list ) {

        list = list ( list.iterator () );
        assertEquals ( 5, len ( list ) );
        assertTrue ( in ( "apple", list ) );
        assertEquals ( "oranges", idx ( list, 1 ) );

        list = new Vector<> ( list );
        list = list ( list );
        assertEquals ( 5, len ( list ) );
        assertTrue ( in ( "apple", list ) );
        assertEquals ( "oranges", idx ( list, 1 ) );


        list = new Vector<> ( list );
        list = list ( ( Iterable<String> ) list );
        assertEquals ( 5, len ( list ) );
        assertTrue ( in ( "apple", list ) );
        assertEquals ( "oranges", idx ( list, 1 ) );

        list = list ( enumeration ( list ) );
        assertEquals ( 5, len ( list ) );
        assertTrue ( in ( "apple", list ) );
        assertEquals ( "oranges", idx ( list, 1 ) );

        list = list ( String.class );
        list.add ( "hi" );
        assertEquals ( 1, len ( list ) );
        assertTrue ( in ( "hi", list ) );


    }

    @Test
    public void sliceTest () {
        List<String> list =
                list ( "apple", "oranges", "pears", "grapes", "kiwi" );
        List<String> list2 = slc ( list, 0, 2 );

        assertEquals ( list ( "apple", "oranges" ), list2 );
    }


    @Test
    public void sliceTest2 () {
        List<String> list =
                list ( "apple", "oranges", "pears", "grapes", "kiwi" );
        List<String> list2 = slc ( list, -3, -1 );

        assertEquals ( list ( "pears", "grapes" ), list2 );
    }


    @Test
    public void sliceStartNeg () {
        List<String> list =
                list ( "apple", "oranges", "pears", "grapes", "kiwi" );

        List<String> list2;

        //listStream[-2:]
        //['grapes', 'kiwi']
        list2 = slc ( list, -2 );

        assertEquals ( list ( "grapes", "kiwi" ), list2 );

    }


    @Test
    public void sliceStartPos () {
        List<String> list =
                list ( "apple", "oranges", "pears", "grapes", "kiwi" );

        List<String> list2;


        //listStream[2 : ]
        //['pears', 'grapes', 'kiwi']
        list2 = slc ( list, 2 );

        assertEquals ( list ( "pears", "grapes", "kiwi" ), list2 );


    }

    @Test
    public void sliceEnd () {
        List<String> list =
                list ( "apple", "oranges", "pears", "grapes", "kiwi" );

        // listStream[: -3]
        // ['apple', 'oranges']

        List<String> list2 = slcEnd ( list, -3 );

        assertEquals ( list ( "apple", "oranges" ), list2 );


        // listStream[: 2]
        // ['apple', 'oranges']
        list2 = slcEnd ( list, 2 );

        assertEquals ( list ( "apple", "oranges" ), list2 );

    }

    @Test
    public void tooBigIndex () {
        List<String> list =
                list ( "apple", "oranges", "pears" );
        idx ( list, 100 );

        slc ( list, -100 );

        slc ( list, 100 );

    }


    @Test
    public void tooSmallIndex () {
        List<String> list =
                list ( "apple", "oranges", "pears" );
        idx ( list, -100 );

        slcEnd ( list, -100 );

        slcEnd ( list, 100 );

    }

    @Test
    public void testMe () {
        List<String> list = list ( ( String[] ) null );
        System.out.println ( list );

        list =
                list ( "apple", "oranges", "pears" );
        assertEquals ( 3, len ( list ) );
        assertTrue ( in ( "apple", list ) );
        assertEquals ( "oranges", idx ( list, 1 ) );


        assertEquals ( "pears", idx ( list, -1 ) );
        assertEquals ( "oranges", idx ( list, -2 ) );
        assertEquals ( "apple", idx ( list, -3 ) );


        idx ( list, 1, "grapes" );
        assertEquals ( "grapes", idx ( list, 1 ) );
        assertEquals ( 3, len ( list ) );


        idx ( list, -2, "pear" );
        assertEquals ( "pear", idx ( list, 1 ) );
        assertEquals ( 3, len ( list ) );

        List<String> list2 = copy ( list );
        assertEquals ( 3, len ( list2 ) );
        assertTrue ( in ( "apple", list2 ) );
        assertEquals ( "pear", idx ( list2, 1 ) );

        list2 = copy ( ( ArrayList<String> ) list );
        assertEquals ( 3, len ( list2 ) );
        assertTrue ( in ( "apple", list2 ) );
        assertEquals ( "pear", idx ( list2, 1 ) );

        add ( list, "berry" );

        assertEquals ( "berry", idx ( list, 3 ) );


    }

    @Test
    public void testSafe () {
        CopyOnWriteArrayList<String> list = ( CopyOnWriteArrayList<String> ) safeList ( "apple", "oranges", "pears" );
        assertEquals ( 3, len ( list ) );
        assertTrue ( in ( "apple", list ) );
        assertEquals ( "oranges", idx ( list, 1 ) );
        idx ( list, 1, "pear" );
        assertEquals ( "pear", idx ( list, 1 ) );
        assertEquals ( 3, len ( list ) );

        List<String> list2 = copy ( list );
        assertEquals ( 3, len ( list2 ) );
        assertTrue ( in ( "apple", list2 ) );
        assertEquals ( "pear", idx ( list2, 1 ) );


    }


    @Test
    public void testLinked () {
        LinkedList<String> list = ( LinkedList<String> ) linkedList ( "apple", "oranges", "pears" );
        assertEquals ( 3, len ( list ) );
        assertTrue ( in ( "apple", list ) );
        assertEquals ( "oranges", idx ( list, 1 ) );
        idx ( list, 1, "pear" );
        assertEquals ( "pear", idx ( list, 1 ) );
        assertEquals ( 3, len ( list ) );

        List<String> list2 = copy ( list );
        assertEquals ( 3, len ( list2 ) );
        assertTrue ( in ( "apple", list2 ) );
        assertEquals ( "pear", idx ( list2, 1 ) );

        List<String> list3 = copy ( ( List<String> ) list );
        assertEquals ( 3, len ( list3 ) );
        assertTrue ( in ( "apple", list3 ) );
        assertEquals ( "pear", idx ( list3, 1 ) );


    }

}
