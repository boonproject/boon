package org.boon;


import org.boon.Str;
import org.junit.Test;

import static org.boon.Exceptions.die;
import static org.boon.Str.*;
import static org.junit.Assert.*;

public class StrTest {

    @Test
    public void index () {

        String letters = "abcd";

        assertEquals (
                'a',
                idx ( letters, 0 )
        );


        assertEquals (
                'd',
                idx ( letters, -1 )
        );


        assertEquals (
                'd',
                idx ( letters, letters.length () - 1 )
        );


        letters = idx ( letters, 1, 'z' );

        assertEquals (
                'z',
                idx ( letters, 1 )
        );
    }


    @Test
    public void isIn () {

        String letters = "abcd";


        assertTrue (
                in ( 'a', letters )
        );

        assertFalse (
                in ( 'z', letters )
        );

    }


    @Test
    public void isInAtOffset () {

        String letters = "abcd";


        assertFalse (
                in ( 'a', 1, letters )
        );

        assertTrue (
                in ( 'c', 1, letters )
        );

    }

    @Test
    public void isInAtRange () {

        String letters = "abcd";


        assertFalse (
                in ( 'a', 1, 2, letters )
        );

        assertTrue (
                in ( 'c', 1, 3, letters )
        );

    }

    @Test
    public void slice () {

        String letters = "abcd";


        assertEquals (
                "ab",
                slc ( letters, 0, 2 )
        );

        assertEquals (
                "bc",
                slc ( letters, 1, -1 )
        );

        //>>> letters[2:]
        //['c', 'd']
        //>>> letters[-2:]
        //['c', 'd']

        assertEquals (
                "cd",
                slc ( letters, -2 )
        );


        assertEquals (
                "cd",
                slc ( letters, 2 )
        );


        //>>> letters[:-2]
        //     ['a', 'b']
        assertEquals (
                "ab",
                slcEnd ( letters, -2 )
        );


        //>>> letters[:-2]
        //     ['a', 'b']
        assertEquals (
                "ab",
                slcEnd ( letters, 2 )
        );

    }


    @Test
    public void outOfBounds () {

        String letters = "abcde";

        slcEnd ( letters, 100 );
        slcEnd ( letters, -100 );

        slc ( letters, 100 );
        slc ( letters, -100 );
        idx ( letters, 100 );
        idx ( letters, -100 );


        letters = idx ( letters, 100, 'x' );


        letters = idx ( letters, -100, 'z' );


        assertEquals (
                'x',
                idx ( letters, -1 )
        );


        assertEquals (
                'z',
                idx ( letters, 0 )
        );

    }


    @Test
    public void compactTest () {
        String letters =
                "ab\0\0\0\0\0\0c\0d\0\0e";

        letters = compact ( letters );

        assertEquals (
                "abcde",
                letters

        );


    }


    @Test
    public void joinByTest () {
        String foo = Str.join ( ',', "foo", "bar", "baz" );
        boolean ok = true;

        ok |= foo.equals ( "foo,bar,baz" ) || die ( "Foo was not equal to foo,bar,baz" );


    }


    @Test
    public void lines () {
        String foo = Str.lines (
                "Line 1",
                "Line 2",
                "Line 3" );

        boolean ok = true;


        ok |= foo.startsWith ( "Line 1" ) || die ( "foo string started with Line 1" );

        ok |= foo.endsWith ( "Line 3" ) || die ( "foo string ends with Line 3 \n" + foo );

    }


}
