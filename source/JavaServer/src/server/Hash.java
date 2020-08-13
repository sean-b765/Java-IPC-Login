package server;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;

public class Hash {
    private final static Random random = new SecureRandom();

    // returns a hash
    // takes: 	password 	as charArray,
    //			_salt		as byte array
    public static byte[] genHash(char[] password, byte[] _salt) {
        PBEKeySpec spec = new PBEKeySpec(password, _salt, 10000, 256);

        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            return skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } finally {
            spec.clearPassword();
        }

        return null;
    }

    // will return a salt
    public static byte[] genSalt() {
        byte[] _salt = new byte[16];
        random.nextBytes(_salt);
        return _salt;
    }

    // will return true if the hash matches, false if otherwise
    // takes:	password -> the password to compare with hash/salt
    //			salt of the existing record
    //			hash of the existing record
    public static boolean match(char[] password, byte[] salt, byte[] expectedHash) {
        // generate the hash of the given password, using the existing salt
        byte[] generatedHash = genHash(password, salt);

        // if the lengths don't match, we can already return false
        if (generatedHash.length != expectedHash.length)
            return false;

        // if any characters in the hash do not match, we can return false
        for (int i = 0; i < generatedHash.length; i++) {
            if (generatedHash[i] != expectedHash[i])
                return false;
        }
        return true; // password hashes match!
    }

    // simple byte[] array to String method
    public static String bytesToString(byte[] array) {
        return new String(array, StandardCharsets.UTF_8);
    }

    // simple String to byte[] array method
    public static byte[] stringToBytes(String str) {
        return str.getBytes(StandardCharsets.UTF_8);
    }
}
