package org.boon.primitive;


import org.junit.Test;

import static org.boon.primitive.Byt.bytes;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class ByteScannerTest {

    @Test
    public void autoSplitThisEndsInSpace() {

        byte[] letters =
                bytes( "This is a string " );


        byte[][] splitted = ByteScanner.split( letters, ' ' );


        assertEquals(
                4,
                splitted.length
        );

        assertArrayEquals(
                bytes( "This" ),
                splitted[ 0 ]
        );


        assertArrayEquals(
                bytes( "is" ),
                splitted[ 1 ]
        );


        assertArrayEquals(
                bytes( "a" ),
                splitted[ 2 ]
        );


        assertArrayEquals(
                bytes( "string" ),
                splitted[ 3 ]
        );

        assertArrayEquals(
                new byte[][]{ bytes( "This" ), bytes( "is" ), bytes( "a" ), bytes( "string" ) },
                splitted
        );


    }

    @Test
    public void autoSplitThis() {

        byte[] letters =
                bytes( "This is a string" );


        byte[][] splitted = ByteScanner.split( letters, ' ' );


        assertEquals(
                4,
                splitted.length
        );

        assertArrayEquals(
                bytes( "This" ),
                splitted[ 0 ]
        );


        assertArrayEquals(
                bytes( "is" ),
                splitted[ 1 ]
        );


        assertArrayEquals(
                bytes( "a" ),
                splitted[ 2 ]
        );


        assertArrayEquals(
                bytes( "string" ),
                splitted[ 3 ]
        );

        assertArrayEquals(
                new byte[][]{ bytes( "This" ), bytes( "is" ), bytes( "a" ), bytes( "string" ) },
                splitted
        );


    }


    @Test
    public void autoSplitThisStartSpace() {

        byte[] letters =
                bytes( " This is a string" );


        byte[][] splitted = ByteScanner.split( letters, ' ' );


        assertEquals(
                5,
                splitted.length
        );


        assertEquals(
                0,
                splitted[ 0 ].length
        );

        assertArrayEquals(
                bytes( "This" ),
                splitted[ 1 ]
        );


        assertArrayEquals(
                bytes( "is" ),
                splitted[ 2 ]
        );


        assertArrayEquals(
                bytes( "a" ),
                splitted[ 3 ]
        );


        assertArrayEquals(
                bytes( "string" ),
                splitted[ 4 ]
        );

        assertArrayEquals(
                new byte[][]{ bytes( "" ), bytes( "This" ), bytes( "is" ), bytes( "a" ), bytes( "string" ) },
                splitted
        );


    }


    @Test
    public void autoSplitThisByTabOrSpace() {

        byte[] letters =
                bytes( "This\tis a string" );


        byte[][] splitted = ByteScanner.splitByChars( letters, '\t', ' ' );


        assertEquals(
                4,
                splitted.length
        );

        assertArrayEquals(
                bytes( "This" ),
                splitted[ 0 ]
        );


        assertArrayEquals(
                bytes( "is" ),
                splitted[ 1 ]
        );


        assertArrayEquals(
                bytes( "a" ),
                splitted[ 2 ]
        );


        assertArrayEquals(
                bytes( "string" ),
                splitted[ 3 ]
        );

        assertArrayEquals(
                new byte[][]{ bytes( "This" ), bytes( "is" ), bytes( "a" ), bytes( "string" ) },
                splitted
        );


    }


    @Test
    public void autoSplitThis3DoubleSpaceAfterA() {

        byte[] letters =
                bytes( "This is a  string" );


        byte[][] splitted = ByteScanner.split( letters, ' ' );


        assertEquals(
                5,
                splitted.length
        );

        assertArrayEquals(
                bytes( "This" ),
                splitted[ 0 ]
        );


        assertArrayEquals(
                bytes( "is" ),
                splitted[ 1 ]
        );


        assertArrayEquals(
                bytes( "a" ),
                splitted[ 2 ]
        );

        assertEquals(
                0,
                splitted[ 3 ].length
        );

        assertArrayEquals(
                bytes( "string" ),
                splitted[ 4 ]
        );

        assertArrayEquals(
                new byte[][]{ bytes( "This" ), bytes( "is" ), bytes( "a" ), bytes( "" ), bytes( "string" ) },
                splitted
        );


    }


    @Test
    public void splitThisEndsInSpace() {

        byte[] letters =
                bytes( "This is a string " );


        byte[][] splitted = ByteScanner.splitExact( letters, ' ', 10 );


        assertEquals(
                4,
                splitted.length
        );

        assertArrayEquals(
                bytes( "This" ),
                splitted[ 0 ]
        );


        assertArrayEquals(
                bytes( "is" ),
                splitted[ 1 ]
        );


        assertArrayEquals(
                bytes( "a" ),
                splitted[ 2 ]
        );


        assertArrayEquals(
                bytes( "string" ),
                splitted[ 3 ]
        );

        assertArrayEquals(
                new byte[][]{ bytes( "This" ), bytes( "is" ), bytes( "a" ), bytes( "string" ) },
                splitted
        );


    }

    @Test
    public void splitThis() {

        byte[] letters =
                bytes( "This is a string" );


        byte[][] splitted = ByteScanner.splitExact( letters, ' ', 10 );


        assertEquals(
                4,
                splitted.length
        );

        assertArrayEquals(
                bytes( "This" ),
                splitted[ 0 ]
        );


        assertArrayEquals(
                bytes( "is" ),
                splitted[ 1 ]
        );


        assertArrayEquals(
                bytes( "a" ),
                splitted[ 2 ]
        );


        assertArrayEquals(
                bytes( "string" ),
                splitted[ 3 ]
        );

        assertArrayEquals(
                new byte[][]{ bytes( "This" ), bytes( "is" ), bytes( "a" ), bytes( "string" ) },
                splitted
        );


    }


    @Test
    public void splitThisStartSpace() {

        byte[] letters =
                bytes( " This is a string" );


        byte[][] splitted = ByteScanner.splitExact( letters, ' ', 10 );


        assertEquals(
                5,
                splitted.length
        );


        assertEquals(
                0,
                splitted[ 0 ].length
        );

        assertArrayEquals(
                bytes( "This" ),
                splitted[ 1 ]
        );


        assertArrayEquals(
                bytes( "is" ),
                splitted[ 2 ]
        );


        assertArrayEquals(
                bytes( "a" ),
                splitted[ 3 ]
        );


        assertArrayEquals(
                bytes( "string" ),
                splitted[ 4 ]
        );

        assertArrayEquals(
                new byte[][]{ bytes( "" ), bytes( "This" ), bytes( "is" ), bytes( "a" ), bytes( "string" ) },
                splitted
        );


    }


    @Test
    public void splitThisByTabOrSpace() {

        byte[] letters =
                bytes( "This\tis a string" );


        byte[][] splitted = ByteScanner.splitExact( letters, 10, '\t', ' ' );


        assertEquals(
                4,
                splitted.length
        );

        assertArrayEquals(
                bytes( "This" ),
                splitted[ 0 ]
        );


        assertArrayEquals(
                bytes( "is" ),
                splitted[ 1 ]
        );


        assertArrayEquals(
                bytes( "a" ),
                splitted[ 2 ]
        );


        assertArrayEquals(
                bytes( "string" ),
                splitted[ 3 ]
        );

        assertArrayEquals(
                new byte[][]{ bytes( "This" ), bytes( "is" ), bytes( "a" ), bytes( "string" ) },
                splitted
        );


    }


    @Test
    public void splitThis3DoubleSpaceAfterA() {

        byte[] letters =
                bytes( "This is a  string" );


        byte[][] splitted = ByteScanner.splitExact( letters, ' ', 10 );


        assertEquals(
                5,
                splitted.length
        );

        assertArrayEquals(
                bytes( "This" ),
                splitted[ 0 ]
        );


        assertArrayEquals(
                bytes( "is" ),
                splitted[ 1 ]
        );


        assertArrayEquals(
                bytes( "a" ),
                splitted[ 2 ]
        );

        assertEquals(
                0,
                splitted[ 3 ].length
        );

        assertArrayEquals(
                bytes( "string" ),
                splitted[ 4 ]
        );

        assertArrayEquals(
                new byte[][]{ bytes( "This" ), bytes( "is" ), bytes( "a" ), bytes( "" ), bytes( "string" ) },
                splitted
        );


    }


}
