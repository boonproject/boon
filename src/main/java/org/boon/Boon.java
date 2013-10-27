package org.boon;


import org.boon.core.Sys;

import static org.boon.Lists.list;
import static org.boon.Lists.toList;

public class Boon {

    public static void println(String message) {
        Sys.println(message);
    }

    public static void println() {
        Sys.println("");
    }

    public static void println(Object message) {

        print(message);
        println();
    }

    public static void print(String message) {
        Sys.print(message);
    }

    public static void print(Object message) {

        if (message == null ) {
            print("<NULL>");
        } else if (message.getClass().isArray()) {
            print( toList(message).toString() );
        } else {
            print( message.toString());
        }
    }

    public static void puts(Object... messages) {

        for (Object message : messages) {
            print(message);
        }
        println();

    }

}