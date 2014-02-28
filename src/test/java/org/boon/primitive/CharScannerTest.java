package org.boon.primitive;


import org.boon.Lists;
import org.junit.Test;

import java.util.List;

import static org.boon.Boon.puts;
import static org.boon.Exceptions.die;
import static org.boon.primitive.Chr.chars;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class CharScannerTest {


    @Test
    public void findString() {
        String findString = "456";
        String string = "0123456789101112";
        int index = CharScanner.findString(findString, string.toCharArray());

        boolean ok = index == 4 || die(index);
    }

    @Test
    public void findString2() {
        String findString = "{{{";
        String string = "0123{567{{0123{{{789";
        int index = CharScanner.findString(findString, string.toCharArray());

        boolean ok = index == 14 || die(index);
    }


    @Test
    public void findString3() {
        String findString = "{{{";
        String string = "0123{567{{0123{{6789{{{";
        int index = CharScanner.findString(findString, string.toCharArray());

        boolean ok = index == 20 || die(index);
    }

    @Test
    public void findString4() {
        String findString = "{{{";
        String string = "{{{0123{567{{0123{{6789{{{";
        int index = CharScanner.findString(findString, string.toCharArray());

        boolean ok = index == 0 || die(index);
    }


    @Test
    public void findString5() {
        String findString = "[[[";
        String string = "{{{012[3{5[67{{01[[23{{67[[8[9{{{";
        int index = CharScanner.findString(findString, string.toCharArray());

        boolean ok = index == -1 || die(index);
    }


    @Test
    public void parseInt() {

        int i =  CharScanner.parseInt( "-22".toCharArray() );
        boolean ok  = i  == -22 || die( "" + i);


        i =  CharScanner.parseInt( "22".toCharArray() );
        ok  = i  == 22 || die( "" + i);

    }

    @Test
    public void parseLongTest() {

        long value =  CharScanner.parseLong( "-22".toCharArray() );
        boolean ok  = value  == -22L || die( "" + value);


        value =  CharScanner.parseInt( "22".toCharArray() );
        ok  = value  == 22 || die( "" + value);

    }



    @Test
    public void parseLongTest2() {

        String test = "" + (Long.MAX_VALUE / 2L);
        long value =  CharScanner.parseLong( test.toCharArray() );
        boolean ok  = value  ==  Long.parseLong( test )|| die( value, Long.parseLong( test ));



    }



    @Test
    public void parseLongTest3() {

        String test = "" + (Long.MIN_VALUE / 2L);
        long value =  CharScanner.parseLong( test.toCharArray() );
        boolean ok  = value  ==  Long.parseLong( test )|| die( value, Long.parseLong( test ));



    }

    @Test
    public void parseLongTest4() {

        String test = "" + (Long.MAX_VALUE );
        long value =  CharScanner.parseLong( test.toCharArray() );
        boolean ok  = value  ==  Long.parseLong( test )|| die( value, Long.parseLong( test ));



    }



    @Test
    public void parseLongTest5() {

        String test = "" + (Long.MIN_VALUE );
        long value =  CharScanner.parseLong( test.toCharArray() );
        boolean ok  = value  ==  Long.parseLong( test )|| die( value, Long.parseLong( test ));



    }

    @Test
    public void parseIntMax() {

        boolean ok = true;
        int i =  0;
        i = CharScanner.parseInt( ("" + Integer.MAX_VALUE).toCharArray() );
        ok  &= i  == Integer.MAX_VALUE || die( "i", i, "MAX", Integer.MAX_VALUE);


    }


    @Test
    public void parseIntMin() {

        boolean ok = true;
        int i = 0;
        i =  CharScanner.parseInt( ("" + Integer.MIN_VALUE).toCharArray() );
        ok  &= i  == Integer.MIN_VALUE || die( "i", i, "MIN", Integer.MIN_VALUE);

    }



    @Test
    public void parseLongMax() {

        boolean ok = true;
        long l =  0;
        l = CharScanner.parseLong( ("" + Long.MAX_VALUE).toCharArray() );
        ok  &= l  == Long.MAX_VALUE || die( "l", l, "MAX", Long.MAX_VALUE);


    }


    @Test
    public void parseLongMin() {

        boolean ok = true;
        long l =  0;
        l = CharScanner.parseLong( ("" + Long.MIN_VALUE).toCharArray() );
        ok  &= l  == Long.MIN_VALUE || die( "l", l, "MIN", Long.MIN_VALUE);


    }



    @Test
    public void parseDouble() {

        String str = "123456789";
        double num =
                CharScanner.parseJsonNumber( str.toCharArray(), 0, str.length() ).doubleValue();
        boolean ok = num ==  123456789d || die("" + num);
    }


    @Test
    public void parseDoubleNegative() {

        String str = "-1.23456789E8";
        double num =
                (Double)CharScanner.parseJsonNumber( str.toCharArray(), 0, str.length() );
        boolean ok = num ==  -1.23456789E8 || die("" + num);
    }

    @Test
    public void parseDoubleNegativeNoE() {

        String str = "-123456789";
        double numTest = Double.parseDouble( str );
        testDouble( str );
    }



    @Test
    public void parseDoubleNegativeNoE2() {

        String str = "-1234567890";
        double numTest = Double.parseDouble( str );
        testDouble( str );
    }


    @Test
    public void parseDoubleMax() {

        String str = "" + Double.MAX_VALUE;
        double numTest = Double.parseDouble( str );
        testDouble( str );
    }


    @Test
    public void parseDoubleMin() {

        String str = "" + Double.MIN_VALUE;
        testDouble( str );
    }



    @Test
    public void manyDoubles() {

        List<String> doubles = Lists.list( "" + 1.01d, "" + 123456789.234D, "" + 55D,
                "" + Integer.MAX_VALUE + "." + Integer.MAX_VALUE,
                "66666666.666", "-6666666666.6666", "1E10" );


        for (String str : doubles) {
            testDouble( str );
        }
    }

    private void testDouble( String str ) {
        puts (str);
        double num =
                (Double)CharScanner.parseJsonNumber( str.toCharArray(), 0, str.length() ).doubleValue();
        double numTest = Double.parseDouble( str );

        boolean ok = num == numTest || die("num",  num, "numTest", numTest);
    }



    private void testDoubleInStringThreeOver( String str  ) {
        double numTest = Double.parseDouble( str );
        double num = CharScanner.parseJsonNumber( ( "   " + str ).toCharArray(), 3, str.length()+3 ).doubleValue();
        boolean ok = num == numTest || die("num",  num, "numTest", numTest);
    }

    @Test
    public void parseIntIgnore0 () {

        int i = CharScanner.parseIntFromToIgnoreDot( "1.1".toCharArray(), 0, "1.1".length() );
        boolean ok = i == 11 || die("i", i);

    }


    
    @Test
    public void simpleDoubleInString () {
        testDoubleInStringThreeOver( "1.1" );
    }



    @Test
    public void testLongMaxWithOffset () {
        testDoubleInStringThreeOver( "" + Long.MAX_VALUE );
    }



    @Test
    public void testLargeDecimal () {
        testDoubleInStringThreeOver( "" + Integer.MAX_VALUE + "."  + Integer.MAX_VALUE);
    }


    @Test
    public void testLargeDecimal2 () {
        testDoubleInStringThreeOver( "1000"  + "."  + "10001");
    }


    @Test
    public void testLargeDecimal3 () {
        testDoubleInStringThreeOver( "10000"  + "."  + "100001");
    }


    @Test
    public void testLargeDecimal4 () {
        testDoubleInStringThreeOver(  "" + 10_000_000  + "."  + 10_000_001);
    }


    @Test
    public void testLargeDecimal5 () {
        testDoubleInStringThreeOver(  "" + 100_000_000  + "."  + 100_000_001);
    }


    @Test
    public void testLargeDecimal6 () {
        testDoubleInStringThreeOver(  "" + 100_000_000  + "."  + 1_000_000_001);
    }


    @Test
    public void testLargeDecimal7 () {
        testDoubleInStringThreeOver(  "" + 100_000_000  + "."  + 1_000_000_001L);
    }


    @Test
    public void testLargeDecimal8 () {
        testDoubleInStringThreeOver(  "" + 1_000_000_000_000L  + "."  + 1_000_000_001L);
    }


    @Test
    public void testLargeDecimal9 () {
        testDoubleInStringThreeOver(  "" + 10_000_000_000_000L  + "."  + 1_000_000_001L);
    }


    @Test
    public void testLargeDecimal10 () {
        testDoubleInStringThreeOver(  "" + 100_000_000_000_000_000L  + "."  + 1_000_000_001L);
    }


    @Test
    public void testLargeDecimal11 () {
        testDoubleInStringThreeOver(  "" + 1_000_000_000_000_000_000L  + "."  + 1_000_000_001L);
    }

    @Test
    public void testLongMinWithOffset () {
        testDoubleInStringThreeOver( "" + Long.MIN_VALUE );
    }


    @Test
    public void testDoubleMaxWithOffset () {
        testDoubleInStringThreeOver( "" + Double.MAX_VALUE );
    }




    @Test
    public void testDoubleMinWithOffset () {
        testDoubleInStringThreeOver( "" + Double.MIN_VALUE );
    }

    @Test
    public void testDoubleMaxWithOffset2 () {
        testDoubleInStringThreeOver( "" + Double.MAX_VALUE/2 );
    }




    @Test
    public void testDoubleMinWithOffset2 () {
        testDoubleInStringThreeOver( "" + Double.MIN_VALUE/2 );
    }



    @Test
    public void testDoubleMaxWithOffset3 () {
        testDoubleInStringThreeOver( "" + (Double.MAX_VALUE/9)*8 );
    }




    @Test
    public void testDoubleMinWithOffset3 () {
        testDoubleInStringThreeOver( "" + (Double.MIN_VALUE/9)*8 );
    }

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
