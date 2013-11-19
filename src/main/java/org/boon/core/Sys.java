package org.boon.core;


public class Sys {

    public static void println(String message) {
         System.out.println(message);
    }

    public static void print(String message) {
        System.out.print(message);
    }

    private static boolean osFound = false;
    private static boolean isWindows = false;

    public static boolean isWindows() {
        //return windows;
        if (System.getProperty("os.name").contains("Windows"))  {
            isWindows = true;
       }
        return isWindows;
    }

    public static char windowsPathSeparator() {
        return '\\';
    }




}
