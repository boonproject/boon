package org.boon.predicates;

import org.boon.Str;

public class PropertyNameUtils {


    public static Function<String, String> underBarCase = new Function<String, String>() {
        @Override
        public String apply(String in) {
            return Str.underBarCase(in);
        }
    };
    public static Function<String, String> camelCase = new Function<String, String>() {
        @Override
        public String apply(String in) {
            return Str.camelCase(in);
        }
    };


    public static Function<String, String> camelCaseUpper = new Function<String, String>() {
        @Override
        public String apply(String in) {
            return Str.camelCaseUpper(in);
        }
    };


    public static Function<String, String> camelCaseLower = new Function<String, String>() {
        @Override
        public String apply(String in) {
            return Str.camelCaseLower(in);
        }
    };

    public static Function<String, String> upperCase = new Function<String, String>() {
        @Override
        public String apply(String in) {
            return in.toUpperCase();
        }
    };

    public static Function<String, String> lowerCase = new Function<String, String>() {
        @Override
        public String apply(String in) {
            return in.toLowerCase();
        }
    };

}
