package aut.bme.caffstore.data.util.password;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

public class PasswordManager {

    private PasswordManager() {
    }

    public static boolean match(byte[] hashedPassword, String clearPassword, byte[] salt) {
        boolean isMatching;

        byte[] newHashed = hashAndSalt(clearPassword, salt);
        isMatching = Arrays.equals(hashedPassword, newHashed);

        return isMatching;
    }

    public static byte[] hashAndSalt(String clearPassword, byte[] salt) {
        KeySpec spec = new PBEKeySpec(clearPassword.toCharArray(), salt, 65536, 128);

        SecretKeyFactory factory;
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
