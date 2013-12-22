package org.boon.core;


import org.boon.core.reflection.Reflection;
import org.boon.core.timer.TimeKeeper;
import org.boon.core.timer.TimeKeeperBasic;

import java.util.concurrent.atomic.AtomicReference;

public class Sys {

    private final static boolean isWindows;
    private final static boolean inContainer;


    public static void println ( String message ) {
        System.out.println( message );
    }

    public static void print ( String message ) {
        System.out.print( message );
    }


    public static boolean isWindows () {
        return isWindows;
    }


    static {
        if ( System.getProperty( "os.name" ).contains( "Windows" ) ) {
            isWindows = true;
        } else {
            isWindows = false;
        }

    }

    public static char windowsPathSeparator () {
        return '\\';
    }


    final static AtomicReference<TimeKeeper> timer = new AtomicReference<TimeKeeper>( new TimeKeeperBasic() );

    public static TimeKeeper timer () {
        return timer.get();
    }

    public static long time () {
        return timer.get().time();
    }


    static {
        boolean _inContainer;
        boolean forceInContainer = Boolean.parseBoolean( System.getProperty( "org.boon.forceInContainer", "false" ) );
        boolean forceNoContainer = Boolean.parseBoolean( System.getProperty( "org.boon.forceNoContainer", "false" ) );

        if ( forceNoContainer ) {
            _inContainer = false;
        } else if ( forceInContainer ) {
            _inContainer = true;
        } else {
            _inContainer = detectContainer();
        }

        inContainer = _inContainer;
    }

    private static boolean detectContainer () {

        boolean _inContainer;

        try {
            Class.forName( "javax.servlet.http.HttpServlet" );

            _inContainer = true;
        } catch ( ClassNotFoundException e ) {
            _inContainer = false;
        }
        if ( !_inContainer ) {
            try {
                Class.forName( "javax.ejb.EJBContext" );

                _inContainer = true;
            } catch ( ClassNotFoundException e ) {
                _inContainer = false;
            }

        }

        return _inContainer;

    }


    public static boolean inContainer () {
        return inContainer;
    }


    static {
        if ( !inContainer ) {
            SystemTimeKeeper.start();
            timer.set( new SystemTimeKeeper() );
        }
    }


    /* Everything that has a cache you need to hold on to, should use this so they can
     * all be stuffed into application context. */
    public static Object contextToHold () {

        return Reflection.contextToHold();
    }


}
