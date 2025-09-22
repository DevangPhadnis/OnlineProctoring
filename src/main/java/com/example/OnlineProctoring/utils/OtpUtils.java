package com.example.OnlineProctoring.utils;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class OtpUtils {

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final int SALT_LEN = 16;
    private static final int ITERATIONS = 120_000;
    private static final int KEY_LEN = 256;

    public static String generateNumericOtp(int digits) {
        int max = (int) Math.pow(10, digits);
        int n = secureRandom.nextInt(max);
        return String.format("%0" + digits + "d", n);
    }

    public static String generateSalt() {
        byte[] saltBytes = new byte[SALT_LEN];
        secureRandom.nextBytes(saltBytes);
        return Base64.getEncoder().encodeToString(saltBytes);
    }

    public static String hashOtp(String otp, String base64Salt) throws Exception {
        byte[] salt = Base64.getDecoder().decode(base64Salt);
        PBEKeySpec pbeKeySpec = new PBEKeySpec(otp.toCharArray(), salt, ITERATIONS, KEY_LEN);
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] key = secretKeyFactory.generateSecret(pbeKeySpec).getEncoded();
        return Base64.getEncoder().encodeToString(key);
    }

    public static boolean verifyOtp(String otp, String base64Salt, String expectedHash) throws Exception {
        String computed = hashOtp(otp, base64Salt);
        return MessageDigest.isEqual(computed.getBytes(), expectedHash.getBytes());
    }
}
