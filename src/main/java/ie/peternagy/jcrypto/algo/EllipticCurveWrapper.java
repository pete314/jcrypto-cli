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
 * @description EllipticCurveWrapper - Wrapper class for EllipticCurve
 * @package ie.peternagy.jcrypto.algo
 */
package ie.peternagy.jcrypto.algo;

import ie.peternagy.jcrypto.util.CryptoSecurityUtil;
import ie.peternagy.jcrypto.util.CryptoSignatureUtil;
import ie.peternagy.jcrypto.util.FileAcccessUtil;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class EllipticCurveWrapper {

    private final String PUBLIC_KEY_FILE_NAME = "id_ecdsa.pub";
    private final String PRIVATE_KEY_FILE_NAME = "id_ecdsa";
    private final String ALGORITHM_NAME = "ECDSA";
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private Cipher ecCipher;

    public EllipticCurveWrapper() {
        Security.addProvider(new BouncyCastleProvider());
        try {
            ecCipher = Cipher.getInstance("ECIES", "BC");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | NoSuchProviderException ex) {
            Logger.getLogger(EllipticCurveWrapper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Initialize the cipher for task
     *
     * @param isEncrypt
     */
    private void initCipher(boolean isEncrypt) {
        try {
            if (isEncrypt) {
                ecCipher.init(Cipher.ENCRYPT_MODE, publicKey);
            } else {
                ecCipher.init(Cipher.DECRYPT_MODE, privateKey);
            }
        } catch (InvalidKeyException ex) {
            Logger.getLogger(EllipticCurveWrapper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Crypt the data
     *
     * @param data - the bytes to work with
     * @param isEncrypt
     * @return the modified bytes
     */
    public byte[] doFinal(byte[] data, boolean isEncrypt) {
        try {
            initCipher(isEncrypt);
            return ecCipher.doFinal(data);
        } catch (IllegalBlockSizeException | BadPaddingException ex) {
            Logger.getLogger(EllipticCurveWrapper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Generate a set of Elliptic Curve keys
     */
    public void generateKeys() {
        try {
            ECGenParameterSpec ecGenSpec = new ECGenParameterSpec("secp384r1");
            KeyPairGenerator g = KeyPairGenerator.getInstance("ECIES");
            g.initialize(ecGenSpec, CryptoSecurityUtil.getSecureRandom());
            KeyPair pair = g.generateKeyPair();
            privateKey = pair.getPrivate();
            publicKey = pair.getPublic();

            writeKeys();
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException ex) {
            Logger.getLogger(EllipticCurveWrapper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Get the sha256 bytes of private key
     * @return 
     */
    public byte[] getKeyId(){
        if(privateKey != null)
            return CryptoSignatureUtil.calculateSHA256(privateKey.getEncoded());
        
        return null;
    }
    
    /**
     * Check the keys are available
     * 
     * @param checkPrivate
     * @return 
     */
    public boolean isInitialized(boolean checkPrivate){
        if((checkPrivate && privateKey != null)
                || (!checkPrivate && publicKey != null))
            return true;
        else
            return false;
    }
    
    /**
     * Try load the keys from disk
     */
    public void tryLoadKeys() {
        try {
            byte[] publicBytes = Hex.decodeHex(new String(FileAcccessUtil.readFromDisk(getKeyFilePath(false))).toCharArray());
            byte[] privateBytes = Hex.decodeHex(new String(FileAcccessUtil.readFromDisk(getKeyFilePath(true))).toCharArray());
            KeyFactory fact = KeyFactory.getInstance("ECDSA", "BC");
            publicKey = fact.generatePublic(new X509EncodedKeySpec(publicBytes));
            privateKey = fact.generatePrivate(new PKCS8EncodedKeySpec(privateBytes));
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException | DecoderException ex) {
            Logger.getLogger(EllipticCurveWrapper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Write the keys to disk
     */
    protected void writeKeys() {
        char[] publicBytes = Hex.encodeHex(publicKey.getEncoded());
        char[] privateBytes = Hex.encodeHex(privateKey.getEncoded());

        FileAcccessUtil.writeToDisk(getKeyFilePath(false), new String(publicBytes).getBytes());
        FileAcccessUtil.writeToDisk(getKeyFilePath(true), new String(privateBytes).getBytes());
    }

    /**
     * Generate the string path to key files
     *
     * @param isPrivate
     * @return the string path
     */
    protected String getKeyFilePath(boolean isPrivate) {
        return String.format("%s/%s", FileAcccessUtil.getUserHome(true), isPrivate ? PRIVATE_KEY_FILE_NAME : PUBLIC_KEY_FILE_NAME);
    }

}
