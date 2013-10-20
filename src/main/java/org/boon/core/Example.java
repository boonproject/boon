package org.boon.core;


import java.util.*;


import static org.boon.core.Maps.*;
import static org.boon.core.Maps.sortedMap;
import static org.boon.core.Sets.*;
import static org.boon.core.Lists.*;
import static org.boon.core.Arrays.*;
import static org.boon.core.primitive.Byt.*;
import static org.boon.core.primitive.Chr.*;

/**
 * TODO exclude this from the jar file.
 */
public class Example {


        public static void main(String [] args) {

            //Works with lists, arrays, sets, maps, sorted maps, etc.
            List<String> fruitList;
            String [] fruitArray;
            Set<String> veggiesSet;
            char [] letters;
            byte [] bytes;
            NavigableMap <Integer, String> favoritesMap;
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
