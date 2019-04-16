package com.agrostar.wallet.utils;

import java.security.SecureRandom;
import java.util.Random;

public class RandomStringGenerator {

    private final Random random;
    private final int length;
    private char[] buffer;

    private static String SOURCE_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static Integer RANDOM_STRING_DEFAULT_LENGTH = 10;

    private static RandomStringGenerator randomStringGenerator;


    public static RandomStringGenerator getInstance() {
        if (randomStringGenerator == null) {
            synchronized (RandomStringGenerator.class) {
                if(randomStringGenerator == null) {
                    randomStringGenerator = new RandomStringGenerator(new SecureRandom(), RANDOM_STRING_DEFAULT_LENGTH);
                }
            }
        }
        return randomStringGenerator;
    }

    private RandomStringGenerator(Random random, int length) {
        this.random = random;
        this.length = length;
        buffer = new char[length];
    }

    public String nextString() {
        for (int idx = 0; idx < buffer.length; ++idx)
            buffer[idx] = SOURCE_STRING.charAt(random.nextInt(SOURCE_STRING.length()));
        return new String(buffer);
    }
}
