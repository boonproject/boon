package org.boon.primitive;

import org.junit.Test;

import static org.boon.Boon.puts;

public class LngTest {


    @Test
    public void test () {
        main ();
    }

    public static void main ( String... args ) {
        puts ( "data", Lng.str ( 1_000_000_000L ) );
    }
}
