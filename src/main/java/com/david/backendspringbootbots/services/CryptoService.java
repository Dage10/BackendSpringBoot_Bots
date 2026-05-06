package com.david.backendspringbootbots.services;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public class CryptoService {
    private static final String ENCRYPTION_ALGORITHM = "AES/GCM/NoPadding";
    private static final byte[] ENCRYPTION_KEY_BYTES = "my_secret_key_very_long_32chars!".getBytes();
    private static final int AUTHENTICATION_TAG_LENGTH_BITS = 128;
    private static final int INITIALIZATION_VECTOR_LENGTH_BYTES = 12;

    public static String encrypt(String plainText) {
        if (plainText == null) return null;

        try {
            byte[] initializationVector = new byte[INITIALIZATION_VECTOR_LENGTH_BYTES];
            new SecureRandom().nextBytes(initializationVector);

            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            cipher.init(
                    Cipher.ENCRYPT_MODE,
                    new SecretKeySpec(ENCRYPTION_KEY_BYTES, "AES"),
                    new GCMParameterSpec(AUTHENTICATION_TAG_LENGTH_BITS, initializationVector)
            );

            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
            byte[] combinedOutput = new byte[initializationVector.length + encryptedBytes.length];

            System.arraycopy(initializationVector, 0, combinedOutput, 0, initializationVector.length);
            System.arraycopy(encryptedBytes, 0, combinedOutput, initializationVector.length, encryptedBytes.length);

            return Base64.getEncoder().encodeToString(combinedOutput);

        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    public static String decrypt(String encryptedText) {
        if (encryptedText == null) return null;

        try {
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);

            byte[] initializationVector = new byte[INITIALIZATION_VECTOR_LENGTH_BYTES];
            byte[] cipherBytes = new byte[decodedBytes.length - INITIALIZATION_VECTOR_LENGTH_BYTES];

            System.arraycopy(decodedBytes, 0, initializationVector, 0, INITIALIZATION_VECTOR_LENGTH_BYTES);
            System.arraycopy(decodedBytes, INITIALIZATION_VECTOR_LENGTH_BYTES, cipherBytes, 0, cipherBytes.length);

            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            cipher.init(
                    Cipher.DECRYPT_MODE,
                    new SecretKeySpec(ENCRYPTION_KEY_BYTES, "AES"),
                    new GCMParameterSpec(AUTHENTICATION_TAG_LENGTH_BITS, initializationVector)
            );

            return new String(cipher.doFinal(cipherBytes));

        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

}
