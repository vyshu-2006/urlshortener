package com.urlshortener.util;

import org.springframework.stereotype.Component;

@Component
public class Base62Encoder {

    private static final String ALLOWED_STRING = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final char[] ALLOWED_CHARACTERS = ALLOWED_STRING.toCharArray();
    private static final int BASE = ALLOWED_CHARACTERS.length;

    public String encode(long input) {
        StringBuilder encodedString = new StringBuilder();

        if(input == 0) {
            return String.valueOf(ALLOWED_CHARACTERS[0]);
        }

        while (input > 0) {
            encodedString.append(ALLOWED_CHARACTERS[(int) (input % BASE)]);
            input = input / BASE;
        }

        return encodedString.reverse().toString();
    }

    public long decode(String input) {
        char[] characters = input.toCharArray();
        int length = characters.length;
        long decoded = 0;

        for (int i = 0; i < length; i++) {
            decoded += (long) (ALLOWED_STRING.indexOf(characters[i]) * Math.pow(BASE, length - 1 - i));
        }

        return decoded;
    }
}
