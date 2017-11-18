package com.theah64.mock_api.test;

import java.util.Random;

public class MyClass {


    public static void main(String[] args) {
        System.out.println(generateRandomWords(4));
    }

    public static String generateRandomWords(int numberOfWords) {
        final StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < numberOfWords; i++) {
            char[] word = new char[random.nextInt(8) + 3]; // words of length 3 through 10. (1 and 2 letter words are boring.)
            for (int j = 0; j < word.length; j++) {
                word[j] = (char) ('a' + random.nextInt(26));
            }
            stringBuilder.append(word).append(" ");
        }
        return stringBuilder.toString();
    }

}
