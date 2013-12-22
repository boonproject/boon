package org.boon.primitive;


import org.junit.Test;

import static junit.framework.Assert.assertTrue;
import static org.boon.primitive.Chr.chars;
import static org.junit.Assert.assertEquals;

public class CharBufTest {


    @Test
    public void testMe() {
        CharBuf buf = new CharBuf();
        buf.add( chars( "0123456789\n" ) );
        buf.add( "0123456789\n" );
        buf.add( "0123456789\n" );
        buf.add( "0123456789\n" );
        buf.add( "0123456789\n" );
        buf.add( "0123456END\n" );


        String out = buf.toString();
        assertEquals( 66, out.length() );
        assertTrue( out.endsWith( "END\n" ) );

    }

    @Test
    public void testExact() {
        CharBuf buf = CharBuf.createExact( 66 );
        buf.add( chars( "0123456789\n" ) );
        buf.add( "0123456789\n" );
        buf.add( "0123456789\n" );
        buf.add( "0123456789\n" );
        buf.add( "0123456789\n" );
        buf.add( "0123456END\n" );


        String out = buf.toString();
        assertEquals( 66, out.length() );
        assertTrue( out.endsWith( "END\n" ) );

    }

}
