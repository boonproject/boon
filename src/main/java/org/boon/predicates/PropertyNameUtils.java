package org.boon.predicates;

import org.boon.Str;

public class PropertyNameUtils {


    public static Function<String, String> underBarCase = new Function<String, String> () {
        @Override
        public String apply ( String in ) {
            return Str.underBarCase ( in );
        }
    };
    public static Function<String, String> camelCase = new Function<String, String> () {
        @Override
        public String apply ( String in ) {
            return Str.camelCase ( in );
        }
    };


    public static Function<String, String> camelCaseUpper = new Function<String, String> () {
        @Override
        public String apply ( String in ) {
            return Str.camelCaseUpper ( in );
        }
    };


    public static Function<String, String> camelCaseLower = new Function<String, String> () {
        @Override
        public String apply ( String in ) {
            return Str.camelCaseLower ( in );
        }
    };

    public static Function<String, String> upperCase = new Function<String, String> () {
        @Override
<<<<<<< HEAD
        public String apply ( String in ) {
=======
        public String apply( String in ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
            return in.toUpperCase ();
        }
    };

    public static Function<String, String> lowerCase = new Function<String, String> () {
        @Override
<<<<<<< HEAD
        public String apply ( String in ) {
=======
        public String apply( String in ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
            return in.toLowerCase ();
        }
    };

}
