package org.boon;

import java.util.Collection;
import java.util.Map;

import static org.boon.Exceptions.die;

/**
 * Created by Richard on 3/7/14.
 */
public class Ok {

    public static boolean ok(Object object) {
        return object!=null;
    }


    public static boolean ok(boolean value) {
        return value;
    }

    public static boolean ok(Number i) {
        return i!=null && i!=0;
    }

    public static boolean ok(int i) {
        return i!=0;
    }

    public static boolean ok(long i) {
        return i!=0;
    }

    public static boolean ok(Map map) {
        return map!=null && map.size() >0;
    }

    public static boolean ok(Collection c) {
        return c!=null && c.size() >0;
    }


    public static boolean ok(CharSequence cs) {
        return cs!=null && cs.length() >0;
    }




    public static boolean okOrDie(Object object) {
        return object!=null || die();
    }


    public static boolean okOrDie(boolean value) {
        return value || die();
    }

    public static boolean okOrDie(Number i) {
        return (i!=null && i!=0) || die();
    }


    public static boolean okOrDie(int i) {
        return i!=0 || die();
    }

    public static boolean okOrDie(long i) {
        return i!=0 || die();
    }

    public static boolean okOrDie(Map map) {
        return (map!=null && map.size() >0) || die();
    }

    public static boolean okOrDie(Collection c) {
        return (c!=null && c.size() >0) || die();
    }


    public static boolean okOrDie(CharSequence cs) {
        return (cs!=null && cs.length() >0) || die();
    }


    public static boolean okOrDie(String message, Object object) {
        return object!=null || die(message);
    }

    public static boolean okOrDie(String message, int i) {
        return i!=0 || die(message);
    }

    public static boolean okOrDie(String message, long i) {
        return i!=0 || die(message);
    }

    public static boolean okOrDie(String message, Map map) {
        return (map!=null && map.size() >0) || die(message);
    }

    public static boolean okOrDie(String message, Collection c) {
        return (c!=null && c.size() >0) || die(message);
    }


    public static boolean okOrDie(String message, CharSequence cs) {
        return (cs!=null && cs.length() >0) || die(message);
    }


    public static boolean okOrDie(String message, boolean value) {
        return value || die();
    }

    public static boolean okOrDie(String message, Number i) {
        return (i!=null && i!=0) || die();
    }


}
