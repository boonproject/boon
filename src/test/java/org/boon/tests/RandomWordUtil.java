package org.boon.tests;

import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;

public class RandomWordUtil {

    static String consonants = "bcdfghjklmnpqrstvwxyz";
    static String vowels = "aeiou";

    public static void main(String[] args) {

        SortedSet<String> set = generateWords(10_000);

        for (String word : set) {
            System.out.println(word);
        }

        System.out.println(set.size());

    }

    public static SortedSet<String> generateWords(int wordCount) {

        SortedSet<String> set = new TreeSet<String>();
        set.add("Rick");
        set.add("Hightower");
        set.add("Pena");
        set.add("Carr");
        set.add("Vipin");
        set.add("Jeff");
        set.add("Diana");
        set.add("Smith");
        set.add("Patel");
        set.add("Whitney");
        set.add("Maya");
        set.add("Lucas");
        set.add("Noah");
        set.add("Ryan");
        set.add("Mary");
        set.add("Alex");
        set.add("Richard");
        set.add("Nick");
        set.add("Miguel");
        set.add("Martha");
        set.add("Melissa");


        makeWords(set, wordCount);

        return set;
    }

    private static void makeWords(SortedSet<String> set, int wordCount) {
        boolean flip = true;
        while (set.size() < wordCount) {
            if (flip) {
                makeWord(set, flip, 10, 3);
            } else {
                makeWord(set, flip, 10, 3);
            }
            flip = !flip;
        }
    }

    private static void makeWord(SortedSet<String> set, boolean consonantFirst, int maxLength, int minLength) {
        Random random = new Random();
        int wordLength = Math.abs(random.nextInt() % maxLength);
        StringBuilder buffer = new StringBuilder();

        wordLength = wordLength > 3 ? wordLength : 3;
        for (int wordIndex = 0; wordIndex < wordLength; wordIndex++) {
            char letter;
            if (wordIndex == 0) {

            }
            if (wordIndex % 2 == 0) {
                if (consonantFirst) {
                    letter = randomConsonant(random);
                } else {
                    letter = randomVowel(random);
                }
            } else {
                if (consonantFirst) {
                    letter = randomVowel(random);
                } else {
                    letter = randomConsonant(random);
                }
            }
            if (wordIndex == 0) {
                buffer.append(Character.toUpperCase(letter));
            } else {
                buffer.append(letter);
            }

        }
        set.add(buffer.toString());
    }

    private static char randomVowel(Random random) {
        char letter;
        letter = vowels.charAt(Math.abs(random.nextInt() % vowels.length()));
        return letter;
    }

    private static char randomConsonant(Random random) {
        char letter;
        letter = consonants.charAt(Math.abs(random.nextInt() % consonants.length()));
        return letter;
    }

}
