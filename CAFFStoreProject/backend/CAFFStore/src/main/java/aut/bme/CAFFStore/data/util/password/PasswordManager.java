package aut.bme.CAFFStore.data.util.password;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

public class PasswordManager {
    public static Boolean match(byte[] hashedPassword, String clearPassword, byte[] salt) {
        Boolean isMatching = false;

        byte[] newHashed = hashAndSalt(clearPassword, salt);
        isMatching = Arrays.equals(hashedPassword, newHashed);

        return isMatching;
    }

    public static byte[] hashAndSalt(String clearPassword, byte[] salt) {
        SecureRandom random = new SecureRandom();
        KeySpec spec = new PBEKeySpec(clearPassword.toCharArray(), salt, 65536, 128);

        SecretKeyFactory factory = null;
        byte[] hash = new byte[0];
        try {
            factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return hash;
        }
        try {
            hash = factory.generateSecret(spec).getEncoded();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

        return hash;
    }

    public static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        return salt;
    }
}
