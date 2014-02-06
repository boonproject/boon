package org.boon.primitive;

import org.junit.Test;


public class LngTest {


    @Test
    public void test() {
        main();
    }

    public static void main( String... args ) {
        outputs( "data", Lng.str( 1_000_000_000L ) );
    }

    private static void outputs( String data, String str ) {
    }
}
