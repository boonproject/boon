package org.boon.core;


public class Sys {

    public static void println ( String message ) {
        System.out.println ( message );
    }

    public static void print ( String message ) {
        System.out.print ( message );
    }

    private static boolean osFound = false;
    private static boolean isWindows = false;

<<<<<<< HEAD
    public static boolean isWindows () {
=======
    public static boolean isWindows() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        //return windows;
        if ( System.getProperty ( "os.name" ).contains ( "Windows" ) ) {
            isWindows = true;
        }
        return isWindows;
    }

<<<<<<< HEAD
    public static char windowsPathSeparator () {
=======
    public static char windowsPathSeparator() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return '\\';
    }


}
