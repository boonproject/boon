package org.boon;

import org.junit.Test;

import static org.boon.primitive.Arry.idx;
import static org.boon.primitive.Arry.len;
import static org.boon.Exceptions.die;
import static org.junit.Assert.assertEquals;

/**
 * Created by rick on 12/8/13.
 */
public class StringScannerTest {


    private static final String TEST_STRING = "[199984,1384795052823,\"/127.0.0.1:51706\",[\"abc123\",\"rickHigh\"," +
            "\"217.0.0.1\",\"start\",1234567,12345678,\"abcsesson123\",\"asdfasdf\"]]";


    @Test
    public void testRemoveChars() {

        String testString = "1_2 345 6    _____\t\t7\t890";

        String after = StringScanner.removeChars ( testString, '_', ' ', '\t' );
        boolean ok =  "1234567890".equals (after ) || die( "$"+ after + "$");
    }

    @Test
    public void testCreateFromString() {

        String[] split = StringScanner.splitByCharsNoneEmpty( TEST_STRING, '[', ',', '"', '\\', ':', ']', '/' );

        String first = idx( split, 0 );
        String second = idx( split, 1 );
        String third = idx( split, 2 );
        String fourth = idx( split, 3 );
        String fifth = idx( split, 4 );
        String sixth = idx( split, 5 );
        String seventh = idx( split, 6 );

        String last = idx( split, -1 );


        assertEquals( "199984", first );


        assertEquals( "1384795052823", second );


        assertEquals( "127.0.0.1", third );


        assertEquals( "51706", fourth );


        assertEquals( "abc123", fifth );

        assertEquals( "rickHigh", sixth );
        assertEquals( "217.0.0.1", seventh );
        assertEquals( "asdfasdf", last );

        assertEquals( 12, len( split ) );


    }


    @Test
    public void testSimple() {

        String[] split = StringScanner.splitByCharsNoneEmpty( "1,2,3", ',' );

        String firstArg = idx( split, 0 );

        assertEquals( "1", firstArg );
    }

    @Test
    public void testSimple2() {

        String[] split = StringScanner.splitByCharsNoneEmpty( "1,2,,4", ',' );

        String firstArg = idx( split, 0 );
        String second = idx( split, 1 );
        String third = idx( split, 2 );

        assertEquals( "1", firstArg );

        assertEquals( "2", second );


        assertEquals( "4", third );
    }

}
