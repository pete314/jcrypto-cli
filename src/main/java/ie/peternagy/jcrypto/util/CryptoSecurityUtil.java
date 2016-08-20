/**
 * Copyright (C) 2016 Peter Nagy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * ======================================================================
 *
 * @author Peter Nagy - peternagy.ie
 * @since August 2016
 * @version 0.1
 * @description CryptoSecurityUtil - Collection of common methods for enhancing
 * crypto security
 * @package ie.peternagy.jcrypto.util
 */
package ie.peternagy.jcrypto.util;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;

public class CryptoSecurityUtil {

    private static final String RANDOM_SOURCE = "SHA1PRNG";
    private static final Map<Integer, Integer> EXCLUSIONS = new HashMap<Integer, Integer>() {
        {
            put(92, 92);//include in settings
        }
    };

    /**
     * Get an initialized SecureRandom instance
     *
     * @return SecureRandom
     */
    public static SecureRandom getSecureRandom() {
        try {
            return SecureRandom.getInstance(RANDOM_SOURCE);
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException("Invalid environment", ex);
        }
    }

    /**
     * Get secure random bytes
     *
     * @param length - number of bytes to generate
     * @return byte[] with secure bytes
     */
    public static byte[] getSecureBytes(int length) {
        SecureRandom secRandom = getSecureRandom();
        byte[] bytes = new byte[length];
        secRandom.setSeed(secRandom.nextLong());
        secRandom.nextBytes(bytes);

        return bytes;
    }

    /**
     * Generate a random int in range
     *
     * @param min - the lower bound
     * @param max - the upper bound
     * @return the generated random integer
     */
    public static int getRandomIntInRange(int min, int max) {
        SecureRandom secRandom = getSecureRandom();

        return secRandom.nextInt((max - min) + min);
    }

    /**
     * Generate a secure random big integer
     * @return 
     */
    public static BigInteger getRandomBigInt(){
        return new BigInteger(128, getSecureRandom());
    }

    /**
     * Generate secure IV based on algorithm spec
     *
     * @param cipherAlgorithmName - the name of the algorithm to use eg: "AES"
     * @return
     */
    public static IvParameterSpec generateSecureIV(String cipherAlgorithmName) {
        try {
            byte[] iv = getSecureBytes(Cipher.getInstance(cipherAlgorithmName).getBlockSize());

            return new IvParameterSpec(iv);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException ex) {
            throw new RuntimeException("Invalid environment, check max key size", ex);
        }
    }

    /**
     * Generate secure password based on the asci table
     *
     * @param length - minimum 15 maximum 256 char length
     * @return
     */
    public static char[] generateSecurePassword(final int length) {
        if (length < 15 || length > 256) {
            throw new RuntimeException("Password has to be between 15 and 256 chars long");
        }

        char[] password = new char[length];

        for (int i = length - 1; i >= 0; i--) {
            if (i == length - 1 || i == 0) {
                password[i] = (char) getAsciiValueWithExclusions(33);//dont use spaces at the end and beginning
            } else {
                password[i] = (char) getAsciiValueWithExclusions(32);
            }
        }

        return password;
    }

    /**
     * Generate a random integer, in range with exclusions
     *
     * Range from 33 to 127
     *
     * @return random integer
     */
    private static int getAsciiValueWithExclusions(int lowerBound) {

        SecureRandom secRandom = getSecureRandom();
        int num;
        do {
            num = (secRandom.nextInt((127 - lowerBound)) + lowerBound);//use acsii chars between 32 and 126
            if (!EXCLUSIONS.containsKey(num)) {
                return num;
            }
        } while (true);
    }
}
