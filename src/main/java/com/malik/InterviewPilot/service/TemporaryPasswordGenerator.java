package com.malik.InterviewPilot.service;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

/** Generates one-time temporary passwords for the forgot-password flow — never persisted in plaintext. */
@Component
public class TemporaryPasswordGenerator {

    // Excludes visually-confusable characters (0/O, 1/l/I) — this is read off an email and typed back in.
    private static final String ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";
    private static final int LENGTH = 12;

    private final SecureRandom random = new SecureRandom();

    public String generate() {
        StringBuilder builder = new StringBuilder(LENGTH);
        for (int i = 0; i < LENGTH; i++) {
            builder.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }
        return builder.toString();
    }
}
