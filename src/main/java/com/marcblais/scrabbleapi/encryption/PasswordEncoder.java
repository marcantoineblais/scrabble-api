package com.marcblais.scrabbleapi.encryption;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;

public class PasswordEncoder {
    public static String encode(String raw) {
        MessageDigest messageDigest;
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        byte[] hashedPassword;
        StringBuilder saltBuilder = new StringBuilder();
        StringBuilder hashedPasswordBuilder = new StringBuilder();

        try {
            messageDigest = MessageDigest.getInstance("SHA-512");
        } catch (Exception ex) {
            return null;
        }

        random.nextBytes(salt);
        messageDigest.update(salt);
        hashedPassword = messageDigest.digest(raw.getBytes(StandardCharsets.UTF_8));

        for (byte b : salt) {
            saltBuilder.append(String.format("%02x", b));
        }

        for (byte b : hashedPassword) {
            hashedPasswordBuilder.append(String.format("%02x", b));
        }

        return saltBuilder + ":" + hashedPasswordBuilder;
    }

    public static boolean isEqual(String raw, String other) {
        MessageDigest messageDigest;
        byte[] otherSalt = PasswordEncoder.hexStringToByteArray(other.split(":")[0]);
        byte[] otherHashed = PasswordEncoder.hexStringToByteArray(other.split(":")[1]);
        byte[] hashedPassword;

        try {
            messageDigest = MessageDigest.getInstance("SHA-512");
        } catch (Exception ex) {
            return false;
        }

        messageDigest.update(otherSalt);
        hashedPassword = messageDigest.digest(raw.getBytes(StandardCharsets.UTF_8));

        return MessageDigest.isEqual(hashedPassword, otherHashed);
    }

    public static byte[] hexStringToByteArray(String string) {
        char[] characters = string.toCharArray();
        byte[] bytesArray = new byte[characters.length / 2];

        for (int i = 0; i < bytesArray.length; i ++) {
            int b1 = Character.digit(characters[2 * i], 16) << 4;
            int b2 = Character.digit(characters[2 * i + 1], 16);
            bytesArray[i] = (byte) (b1 + b2);
        }

        return bytesArray;
    }
}
