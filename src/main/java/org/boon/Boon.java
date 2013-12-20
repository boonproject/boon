package org.boon;


import org.boon.core.Sys;
import org.boon.primitive.CharBuf;

import static org.boon.Lists.toList;

public class Boon {

    public static void println ( String message ) {
        Sys.println ( message );
    }

<<<<<<< HEAD
    public static void println () {
=======
    public static void println() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        Sys.println ( "" );
    }

    public static void println ( Object message ) {

        print ( message );
        println ();
    }

    public static void print ( String message ) {
        Sys.print ( message );
    }

    public static void print ( Object message ) {

        if ( message == null ) {
            print ( "<NULL>" );
        } else if ( message.getClass ().isArray () ) {
            print ( toList ( message ).toString () );
        } else {
            print ( message.toString () );
        }
    }

    public static void puts ( Object... messages ) {

        for ( Object message : messages ) {
            print ( message );
            print ( ' ' );
        }
        println ();

    }

    public static void putl ( Object... messages ) {

        for ( Object message : messages ) {
            print ( message );
            println ();
        }
        println ();

    }

    public static String sputl ( Object... messages ) {
        CharBuf buf = CharBuf.create ( 100 );
        return sputl ( buf, messages );
    }

    public static String sputs ( Object... messages ) {
        CharBuf buf = CharBuf.create ( 100 );
        return sputs ( buf, messages );
    }


    public static String sputl ( CharBuf buf, Object... messages ) {

        for ( Object message : messages ) {
            if ( message == null ) {
                buf.add ( "<NULL>" );
            } else if ( message.getClass ().isArray () ) {
                buf.add ( toList ( message ).toString () );
            } else {
                buf.add ( message.toString () );
            }
            buf.add ( '\n' );
        }
        buf.add ( '\n' );

        return buf.toString ();


    }

    public static String sputs ( CharBuf buf, Object... messages ) {

        int index = 0;
        for ( Object message : messages ) {
            if ( index != 0 ) {
                buf.add ( ' ' );
            }
            index++;

            if ( message == null ) {
                buf.add ( "<NULL>" );
            } else if ( message.getClass ().isArray () ) {
                buf.add ( toList ( message ).toString () );
            } else {
                buf.add ( message.toString () );
            }
        }
        buf.add ( '\n' );

        return buf.toString ();

    }

}