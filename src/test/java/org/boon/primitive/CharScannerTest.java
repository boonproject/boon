package org.boon.primitive;


import org.junit.Test;

import static org.boon.Exceptions.die;
import static org.boon.primitive.Chr.chars;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class CharScannerTest {


    @Test
    public void parseLong() {

        String str = "12345678910";
        long l1 =  CharScanner.parseLongFromTo( str.toCharArray(), 0, str.length() );
        boolean ok = l1 ==  12345678910L || die("" + l1);



        str = "abc12345678910";
        l1 =  CharScanner.parseLongFromTo( str.toCharArray(), 3, str.length() );
        ok = l1 ==  12345678910L || die("" + l1);




        str = "abcdefghijklmnopqrstuvwxyz12345678910";
        l1 =  CharScanner.parseLongFromTo( str.toCharArray(), 26, str.length() );
        ok = l1 ==  12345678910L || die("" + l1);




        String str2 = "abcdefghijklmnopqrstuvwxyz12345678910mymilkshakemakestheboysintheyard";
        l1 =  CharScanner.parseLongFromTo( str2.toCharArray(), 26, str.length() );
        ok = l1 ==  12345678910L || die("" + l1);
    }

        @Test
    public void autoSplitThisEndsInSpace() {

        char[] letters =
                chars( "This is a string " );


        char[][] splitted = CharScanner.split( letters, ' ' );


        assertEquals(
                4,
                splitted.length
        );

        assertArrayEquals(
                chars( "This" ),
                splitted[ 0 ]
        );


        assertArrayEquals(
                chars( "is" ),
                splitted[ 1 ]
        );


        assertArrayEquals(
                chars( "a" ),
                splitted[ 2 ]
        );


        assertArrayEquals(
                chars( "string" ),
                splitted[ 3 ]
        );

        assertArrayEquals(
                new char[][]{ chars( "This" ), chars( "is" ), chars( "a" ), chars( "string" ) },
                splitted
        );


    }

    @Test
    public void autoSplitThis() {

        char[] letters =
                chars( "This is a string" );


        char[][] splitted = CharScanner.split( letters, ' ' );


        assertEquals(
                4,
                splitted.length
        );

        assertArrayEquals(
                chars( "This" ),
                splitted[ 0 ]
        );


        assertArrayEquals(
                chars( "is" ),
                splitted[ 1 ]
        );


        assertArrayEquals(
                chars( "a" ),
                splitted[ 2 ]
        );


        assertArrayEquals(
                chars( "string" ),
                splitted[ 3 ]
        );

        assertArrayEquals(
                new char[][]{ chars( "This" ), chars( "is" ), chars( "a" ), chars( "string" ) },
                splitted
        );


    }


    @Test
    public void autoSplitThisStartSpace() {

        char[] letters =
                chars( " This is a string" );


        char[][] splitted = CharScanner.split( letters, ' ' );


        assertEquals(
                5,
                splitted.length
        );


        assertEquals(
                0,
                splitted[ 0 ].length
        );

        assertArrayEquals(
                chars( "This" ),
                splitted[ 1 ]
        );


        assertArrayEquals(
                chars( "is" ),
                splitted[ 2 ]
        );


        assertArrayEquals(
                chars( "a" ),
                splitted[ 3 ]
        );


        assertArrayEquals(
                chars( "string" ),
                splitted[ 4 ]
        );

        assertArrayEquals(
                new char[][]{ chars( "" ), chars( "This" ), chars( "is" ), chars( "a" ), chars( "string" ) },
                splitted
        );


    }


    @Test
    public void autoSplitThisByTabOrSpace() {

        char[] letters =
                chars( "This\tis a string" );


        char[][] splitted = CharScanner.splitByChars( letters, '\t', ' ' );


        assertEquals(
                4,
                splitted.length
        );

        assertArrayEquals(
                chars( "This" ),
                splitted[ 0 ]
        );


        assertArrayEquals(
                chars( "is" ),
                splitted[ 1 ]
        );


        assertArrayEquals(
                chars( "a" ),
                splitted[ 2 ]
        );


        assertArrayEquals(
                chars( "string" ),
                splitted[ 3 ]
        );

        assertArrayEquals(
                new char[][]{ chars( "This" ), chars( "is" ), chars( "a" ), chars( "string" ) },
                splitted
        );


    }


    @Test
    public void autoSplitThis3DoubleSpaceAfterA() {

        char[] letters =
                chars( "This is a  string" );


        char[][] splitted = CharScanner.split( letters, ' ' );


        assertEquals(
                5,
                splitted.length
        );

        assertArrayEquals(
                chars( "This" ),
                splitted[ 0 ]
        );


        assertArrayEquals(
                chars( "is" ),
                splitted[ 1 ]
        );


        assertArrayEquals(
                chars( "a" ),
                splitted[ 2 ]
        );

        assertEquals(
                0,
                splitted[ 3 ].length
        );

        assertArrayEquals(
                chars( "string" ),
                splitted[ 4 ]
        );

        assertArrayEquals(
                new char[][]{ chars( "This" ), chars( "is" ), chars( "a" ), chars( "" ), chars( "string" ) },
                splitted
        );


    }


    @Test
    public void splitThisEndsInSpace() {

        char[] letters =
                chars( "This is a string " );


        char[][] splitted = CharScanner.splitExact( letters, ' ', 10 );


        assertEquals(
                4,
                splitted.length
        );

        assertArrayEquals(
                chars( "This" ),
                splitted[ 0 ]
        );


        assertArrayEquals(
                chars( "is" ),
                splitted[ 1 ]
        );


        assertArrayEquals(
                chars( "a" ),
                splitted[ 2 ]
        );


        assertArrayEquals(
                chars( "string" ),
                splitted[ 3 ]
        );

        assertArrayEquals(
                new char[][]{ chars( "This" ), chars( "is" ), chars( "a" ), chars( "string" ) },
                splitted
        );


    }

    @Test
    public void splitThis() {

        char[] letters =
                chars( "This is a string" );


        char[][] splitted = CharScanner.splitExact( letters, ' ', 10 );


        assertEquals(
                4,
                splitted.length
        );

        assertArrayEquals(
                chars( "This" ),
                splitted[ 0 ]
        );


        assertArrayEquals(
                chars( "is" ),
                splitted[ 1 ]
        );


        assertArrayEquals(
                chars( "a" ),
                splitted[ 2 ]
        );


        assertArrayEquals(
                chars( "string" ),
                splitted[ 3 ]
        );

        assertArrayEquals(
                new char[][]{ chars( "This" ), chars( "is" ), chars( "a" ), chars( "string" ) },
                splitted
        );


    }


    @Test
    public void splitThisStartSpace() {

        char[] letters =
                chars( " This is a string" );


        char[][] splitted = CharScanner.splitExact( letters, ' ', 10 );


        assertEquals(
                5,
                splitted.length
        );


        assertEquals(
                0,
                splitted[ 0 ].length
        );

        assertArrayEquals(
                chars( "This" ),
                splitted[ 1 ]
        );


        assertArrayEquals(
                chars( "is" ),
                splitted[ 2 ]
        );


        assertArrayEquals(
                chars( "a" ),
                splitted[ 3 ]
        );


        assertArrayEquals(
                chars( "string" ),
                splitted[ 4 ]
        );

        assertArrayEquals(
                new char[][]{ chars( "" ), chars( "This" ), chars( "is" ), chars( "a" ), chars( "string" ) },
                splitted
        );


    }


    @Test
    public void splitThisByTabOrSpace() {

        char[] letters =
                chars( "This\tis a string" );


        char[][] splitted = CharScanner.splitExact( letters, 10, '\t', ' ' );


        assertEquals(
                4,
                splitted.length
        );

        assertArrayEquals(
                chars( "This" ),
                splitted[ 0 ]
        );


        assertArrayEquals(
                chars( "is" ),
                splitted[ 1 ]
        );


        assertArrayEquals(
                chars( "a" ),
                splitted[ 2 ]
        );


        assertArrayEquals(
                chars( "string" ),
                splitted[ 3 ]
        );

        assertArrayEquals(
                new char[][]{ chars( "This" ), chars( "is" ), chars( "a" ), chars( "string" ) },
                splitted
        );


    }


    @Test
    public void splitThis3DoubleSpaceAfterA() {

        char[] letters =
                chars( "This is a  string" );


        char[][] splitted = CharScanner.splitExact( letters, ' ', 10 );


        assertEquals(
                5,
                splitted.length
        );

        assertArrayEquals(
                chars( "This" ),
                splitted[ 0 ]
        );


        assertArrayEquals(
                chars( "is" ),
                splitted[ 1 ]
        );


        assertArrayEquals(
                chars( "a" ),
                splitted[ 2 ]
        );

        assertEquals(
                0,
                splitted[ 3 ].length
        );

        assertArrayEquals(
                chars( "string" ),
                splitted[ 4 ]
        );

        assertArrayEquals(
                new char[][]{ chars( "This" ), chars( "is" ), chars( "a" ), chars( "" ), chars( "string" ) },
                splitted
        );


    }


}
