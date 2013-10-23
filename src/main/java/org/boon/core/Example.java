package org.boon.core;


import java.util.*;


import static org.boon.core.Exceptions.die;
import static org.boon.core.Maps.*;
import static org.boon.core.Sets.*;
import static org.boon.core.Lists.*;
import static org.boon.core.Arrays.*;
import static org.boon.core.Str.*;
import static org.boon.core.primitive.Byt.*;
import static org.boon.core.primitive.Chr.*;

/**
 * TODO exclude this from the jar file.
 */
public class Example {


    public static void main(String [] args) {
            collectionAndBasicTypes();
            strings();

    }

    private static void strings() {

        String letters = "abcd";

        boolean worked = true;

        worked &=

                idx(letters, 0)  == 'a'
                        || die("0 index is equal to a");



        worked &=

                idx(letters, -1)  == 'd'
                        || die("-1 index is equal to a");


        worked &=

                idx(letters, letters.length() - 1) == 'd'
                         || die("another way to express what the -1 means");


        //We can modify too
        letters = idx(letters, 1, 'z');

        worked &=

                idx(letters, 1) == 'z'
                        || die("Set the 1 index of letters to 'z'");


        worked &= (
                in('a', letters) &&
                in('z', letters)
        ) || die("'z' is in letters and 'a' is in letters");



        letters = "abcd";

        worked &=
                slc(letters, 0, 2).equals("ab")
                    || die("index 0 through index 2 is equal to \"ab\"");



        worked &=
                slc(letters, 1, -1).equals("bc")
                        || die("index 1 through index (length -1) is equal to \"bc\"");


        worked &=
                slcEnd(letters, -2).equals("ab")
                        || die("");


        worked &=
                slcEnd(letters, 2).equals("ab")
                        || die("");

    }

    private static void collectionAndBasicTypes() {
        //Works with lists, arrays, sets, maps, sorted maps, etc.
        List<String> fruitList;
        String [] fruitArray;
        Set<String> veggiesSet;
        char [] letters;
        byte [] bytes;
        NavigableMap<Integer, String> favoritesMap;
        Map<String, Integer> map;

        // These helper methods are used to create common Java types.
        // Sets and lists have concurrent and non concurrent variants
        // Set also has sorted and non sorted variants
        // This makes safeList, list, set, sortedSet, safeSet, safeSortedSet
        veggiesSet  =  sortedSet("salad", "broccoli", "spinach");
        fruitList   =  list( "apple", "oranges", "pineapple");
        fruitArray  =  array( "apple", "oranges", "pineapple");
        letters     =  array( 'a', 'b', 'c');
        bytes       =  array( new byte[]{0x1, 0x2, 0x3, 0x4});

        //You add up name / value pairs as a pseudo literal for map
        favoritesMap = sortedMap(
                            2, "pineapple",
                            1, "oranges",
                            3, "apple"
                        );


        // You add up name / value pairs as a pseudo literal for map
        // map, sortedMap, safeMap (thread safe concurrent), and sortedSafeMap are
        // supported.
        map =    map (
                "pineapple",  2,
                "oranges",    1,
                "apple",      3
        );


        // Getting the length
        assert len( veggiesSet )        == 3;
        assert len( fruitList )         == 3;
        assert len( fruitArray )        == 3;
        assert len( letters )           == 3;
        assert len( bytes )             == 4;
        assert len( favoritesMap )      == 3;
        assert len( map )               == 3;


        //Using idx to access a value.

        assert idx( veggiesSet, "b").equals("broccoli");

        assert idx( fruitList, 1 ).equals("oranges");

        assert idx( fruitArray, 1 ).equals("oranges");

        assert idx( letters, 1 ) == 'b';

        assert idx( bytes, 1 )      == 0x2;

        assert idx( favoritesMap, 2 ).equals("pineapple");

        assert idx( map, "pineapple" )  == 2;


        //Negative indexes
        assert idx( fruitList, -2 ).equals("oranges");

        assert idx( fruitArray, -2 ).equals("oranges");

        assert idx( letters, -2 ) == 'b';

        assert idx( bytes, -3 )      == 0x2;
    }
}
