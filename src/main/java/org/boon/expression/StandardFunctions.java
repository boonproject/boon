package org.boon.expression;

import org.boon.Boon;
import org.boon.Str;
import org.boon.StringScanner;
import org.boon.core.Conversions;

/**
 * Created by Richard on 9/17/14.
 */
public class StandardFunctions {

    public static boolean contains(Object string, Object searchString) {


        if (string == null || searchString == null) {
            return false;
        }

        return string.toString().contains(searchString.toString());
    }

    public static boolean containsIgnoreCase(Object string, Object searchString) {

        if (string == null || searchString == null) {
            return false;
        }

        return string.toString().toUpperCase().contains(searchString.toString().toUpperCase());
    }


    public static boolean endsWith(Object string, Object searchString) {


        if (string == null || searchString == null) {
            return false;
        }

        return string.toString().endsWith(searchString.toString());
    }

    public static boolean startsWith(Object string, Object searchString) {


        if (string == null || searchString == null) {
            return false;
        }

        return string.toString().startsWith(searchString.toString());
    }

    public static String escapeXml(Object string) {

        return string.toString().replace("<", "&lt;");
    }


    public static int indexOf(Object string, Object searchString) {


        if (string == null || searchString == null) {
            return -1;
        }
        return string.toString().indexOf(searchString.toString());
    }

    public static int length(Object item) {


        if (item == null ) {
            return 0;
        }
        return Conversions.len(item);
    }


    public static int len(Object item) {


        if (item == null ) {
            return 0;
        }
        return Conversions.len(item);
    }


    public static String replace(Object string, Object orgSubStr, Object newSubStr) {


        if (string == null ) {
            return "";
        }

        if (orgSubStr==null || newSubStr == null) {
            return string.toString();
        }

        return string.toString().replace(orgSubStr.toString(), newSubStr.toString());
    }

    public static String[] split(Object string, Object split) {


        if (string == null ) {
            return new String[0];
        }

        if (Str.isEmpty(split)) {
            return new String[]{string.toString()};
        }

        if (split.toString().length()==1) {
            return StringScanner.split(string.toString(), split.toString().charAt(0));
        } else {

            return string.toString().split(split.toString());
        }
    }

    public static String substring(Object string, int start, int stop) {


        if (string == null ) {
            return "";
        }


        return Str.slc(string.toString(), start, stop);
    }


    public static String slc(Object string, int start, int stop) {


        if (string == null ) {
            return "";
        }


        return Str.slc(string.toString(), start, stop);
    }


    public static String slc(Object string, int start) {


        if (string == null ) {
            return "";
        }


        return Str.slc(string.toString(), start);
    }


    public static String slcEnd(Object string, int end) {


        if (string == null ) {
            return "";
        }


        return Str.slcEnd(string.toString(), end);
    }



    public static String substringAfter(Object string, Object after) {


        if (string == null ) {
            return "";
        }

        if (Str.isEmpty(after)) {
            return string.toString();
        }

        return StringScanner.substringAfter(string.toString(), after.toString());
    }


    public static String substringBefore(Object string, Object before) {


        if (string == null ) {
            return "";
        }

        if (Str.isEmpty(before)) {
            return string.toString();
        }

        return StringScanner.substringBefore(string.toString(), before.toString());
    }


    public static String toLowerCase(Object string) {


        if (string == null ) {
            return "";
        }


        return Str.lower(string.toString());
    }


    public static String lower(Object string) {


        if (string == null ) {
            return "";
        }


        return Str.lower(string.toString());
    }

    public static String trim(Object string) {


        if (string == null ) {
            return "";
        }


        return string.toString().trim();
    }

    public static String join(Object objects, Object join) {
        return Str.joinCollection(join.toString(), Conversions.toList(objects));
    }




}
