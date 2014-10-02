/*
 * Copyright 2013-2014 Richard M. Hightower
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * __________                              _____          __   .__
 * \______   \ ____   ____   ____   /\    /     \ _____  |  | _|__| ____    ____
 *  |    |  _//  _ \ /  _ \ /    \  \/   /  \ /  \\__  \ |  |/ /  |/    \  / ___\
 *  |    |   (  <_> |  <_> )   |  \ /\  /    Y    \/ __ \|    <|  |   |  \/ /_/  >
 *  |______  /\____/ \____/|___|  / \/  \____|__  (____  /__|_ \__|___|  /\___  /
 *         \/                   \/              \/     \/     \/       \//_____/
 *      ____.                     ___________   _____    ______________.___.
 *     |    |____ ___  _______    \_   _____/  /  _  \  /   _____/\__  |   |
 *     |    \__  \\  \/ /\__  \    |    __)_  /  /_\  \ \_____  \  /   |   |
 * /\__|    |/ __ \\   /  / __ \_  |        \/    |    \/        \ \____   |
 * \________(____  /\_/  (____  / /_______  /\____|__  /_______  / / ______|
 *               \/           \/          \/         \/        \/  \/
 */

package org.boon.benchmark.datarepo.utils;

import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;

public class RandomWordUtil {

    static String consonants = "bcdfghjklmnpqrstvwxyz";
    static String vowels = "aeiou";

    public static void main( String[] args ) {

        SortedSet<String> set = generateWords( 10_000 );

        for ( String word : set ) {
            System.out.println( word );
        }

        System.out.println( set.size() );

    }

    public static SortedSet<String> generateWords( int wordCount ) {

        SortedSet<String> set = new TreeSet<String>();
        set.add( "Rick" );
        set.add( "Hightower" );
        set.add( "Pena" );
        set.add( "Carr" );
        set.add( "Vipin" );
        set.add( "Jeff" );
        set.add( "Diana" );
        set.add( "Smith" );
        set.add( "Patel" );
        set.add( "Whitney" );
        set.add( "Maya" );
        set.add( "Lucas" );
        set.add( "Noah" );
        set.add( "Ryan" );
        set.add( "Mary" );
        set.add( "Alex" );
        set.add( "Richard" );
        set.add( "Nick" );
        set.add( "Miguel" );
        set.add( "Martha" );
        set.add( "Melissa" );


        makeWords( set, wordCount );

        return set;
    }

    private static void makeWords( SortedSet<String> set, int wordCount ) {
        boolean flip = true;
        while ( set.size() < wordCount ) {
            if ( flip ) {
                makeWord( set, flip, 10, 3 );
            } else {
                makeWord( set, flip, 10, 3 );
            }
            flip = !flip;
        }
    }

    private static void makeWord( SortedSet<String> set, boolean consonantFirst, int maxLength, int minLength ) {
        Random random = new Random();
        int wordLength = Math.abs( random.nextInt() % maxLength );
        StringBuilder buffer = new StringBuilder();

        wordLength = wordLength > 3 ? wordLength : 3;
        for ( int wordIndex = 0; wordIndex < wordLength; wordIndex++ ) {
            char letter;
            if ( wordIndex == 0 ) {

            }
            if ( wordIndex % 2 == 0 ) {
                if ( consonantFirst ) {
                    letter = randomConsonant( random );
                } else {
                    letter = randomVowel( random );
                }
            } else {
                if ( consonantFirst ) {
                    letter = randomVowel( random );
                } else {
                    letter = randomConsonant( random );
                }
            }
            if ( wordIndex == 0 ) {
                buffer.append( Character.toUpperCase( letter ) );
            } else {
                buffer.append( letter );
            }

        }
        set.add( buffer.toString() );
    }

    private static char randomVowel( Random random ) {
        char letter;
        letter = vowels.charAt( Math.abs( random.nextInt() % vowels.length() ) );
        return letter;
    }

    private static char randomConsonant( Random random ) {
        char letter;
        letter = consonants.charAt( Math.abs( random.nextInt() % consonants.length() ) );
        return letter;
    }

}
