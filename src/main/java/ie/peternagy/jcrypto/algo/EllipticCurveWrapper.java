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
import ie.peternagy.jcrypto.util.FileAccessUtil;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
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
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.ArrayUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * EllipticCurveWrapper class provides a wrapper around Elliptic Curve encryption and decryption operations.
 * It handles key pair generation, key loading/saving, and ECIES cipher operations.
 */
public class EllipticCurveWrapper {

    private final String PUBLIC_KEY_FILE_NAME = "id_ecdsa.pub";
    private final String PRIVATE_KEY_FILE_NAME = "id_ecdsa";
    private final String ALGORITHM_NAME = "ECIES";
    private final String EC_CURVE = "secp521r1";
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private Cipher ecCipher;

    /**
     * Constructor for EllipticCurveWrapper.
     * Initializes the Bouncy Castle provider, loads keys if available, and creates the ECIES cipher.
     */
    public EllipticCurveWrapper() {
        Security.addProvider(new BouncyCastleProvider());
        try {
            generateKeys();
            ecCipher = Cipher.getInstance(ALGORITHM_NAME, "BC");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | NoSuchProviderException ex) {
            Logger.getLogger(EllipticCurveWrapper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Initialize the cipher for the specified task (encryption or decryption).
     *
     * @param isEncrypt boolean flag indicating the mode: true for encryption, false for decryption.
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
     * Crypt the data using ECIES.
     *
     * @param data The byte array of data to encrypt or decrypt.
     * @param isEncrypt boolean flag indicating the mode: true for encryption, false for decryption.
     * @return The modified bytes after encryption or decryption, or null if an exception occurs.
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
     * Crypt data with signature (header) included.
     * If encrypting, adds a header with CRC32.
     * If decrypting, extracts and verifies the header.
     *
     * @param data The bytes to work with.
     * @param isEncrypt boolean flag indicating the mode: true for encryption, false for decryption.
     * @return The modified bytes with header (if encrypting) or without header (if decrypting).
     */
    public byte[] doFinalWithHeader(byte[] data, boolean isEncrypt) {
        if (isEncrypt) {
            byte[] outData = doFinal(data, true);
            byte[] rawHeader = createRawHeader(CryptoSignatureUtil.calculateCrc32(outData));
            
            return ArrayUtils.addAll(rawHeader, outData);
        }else{
            byte[] content = extractRawHeader(data);
            return doFinal(content, false);
        }
    }

    /**
     * Create raw header - includes version, keyId, crc.
     * 
     * @param dataCrc The CRC32 checksum of the data.
     * @return The constructed header as a byte array, or null if an IOException occurs.
     */
    public byte[] createRawHeader(long dataCrc) {
        try {
            byte[] keyId = getKeyId();
            ByteArrayOutputStream header = new ByteArrayOutputStream();

            header.write((byte) 100);//version
            header.write(ByteBuffer.allocate(Integer.SIZE / Byte.SIZE).putInt(keyId.length).array());//key id length
            header.write(ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(dataCrc).array());
            header.write(keyId);
            
            return header.toByteArray();
        } catch (IOException ex) {
            Logger.getLogger(EllipticCurveWrapper.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }
    
    /**
     * Extract header parameters from data and verify version, key ID, and checksum.
     *
     * @param data The content with header signature.
     * @return The content bytes without the header.
     * @throws RuntimeException if version, key ID, or checksum verification fails.
     */
    public byte[] extractRawHeader(byte[] data){
        int version = data[0];
        int currentPosition = 1;
        int keyIdSize = ByteBuffer.wrap(ArrayUtils.subarray(data, currentPosition, currentPosition +Integer.BYTES)).getInt();
        currentPosition += Integer.BYTES;
        long crcSum = ByteBuffer.wrap(ArrayUtils.subarray(data, currentPosition, currentPosition + Long.BYTES)).getLong();
        currentPosition += Long.BYTES;
        byte[] keyId = ArrayUtils.subarray(data, currentPosition, currentPosition + keyIdSize);
        currentPosition += keyIdSize;
        byte[] content = ArrayUtils.subarray(data, currentPosition, data.length);
        
        if(version != 100 || !Arrays.equals(keyId, getKeyId()) || CryptoSignatureUtil.calculateCrc32(content) != crcSum){
            String reason = version != 100 ? "Invalid version " : !Arrays.equals(keyId, getKeyId()) ? " Invalid key id" : " Invalid data checksum";
            throw new RuntimeException("EC headers do not match - decrypt " + reason);
        }
        
        return content;
    }

    /**
     * Generate a set of Elliptic Curve keys (secp521r1).
     * Keys are saved to disk after generation.
     */
    public void generateKeys() {
        tryLoadKeys();
        if(isInitialized(true) && isInitialized(false)){
            return;
        }
        
        try {
            ECGenParameterSpec ecGenSpec = new ECGenParameterSpec(EC_CURVE);
            KeyPairGenerator g = KeyPairGenerator.getInstance(ALGORITHM_NAME);
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
     * Get the sha256 bytes of private key.
     *
     * @return The SHA-256 hash of the private key encoded bytes, or null if private key is not initialized.
     */
    public byte[] getKeyId() {
        if (privateKey != null) {
            return CryptoSignatureUtil.calculateSHA256(privateKey.getEncoded());
        }

        return null;
    }

    /**
     * Check if the keys are available (initialized).
     *
     * @param checkPrivate boolean flag: true to check private key, false to check public key.
     * @return true if the specified key is initialized, false otherwise.
     */
    public boolean isInitialized(boolean checkPrivate) {
        if ((checkPrivate && privateKey != null)
                || (!checkPrivate && publicKey != null)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Try load the keys from disk.
     * Attempts to read the public and private keys from their default file locations.
     */
    public void tryLoadKeys() {
        try {
            byte[] publicFileContent = FileAccessUtil.readFromDisk(getKeyFilePath(false));
            byte[] privateFileContent = FileAccessUtil.readFromDisk(getKeyFilePath(true));

            if (publicFileContent.length == 0 || privateFileContent.length == 0) {
                return;
            }

            byte[] publicBytes = Hex.decodeHex(new String(publicFileContent).toCharArray());
            byte[] privateBytes = Hex.decodeHex(new String(privateFileContent).toCharArray());
            KeyFactory fact = KeyFactory.getInstance("ECDSA", "BC");
            publicKey = fact.generatePublic(new X509EncodedKeySpec(publicBytes));
            privateKey = fact.generatePrivate(new PKCS8EncodedKeySpec(privateBytes));
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException | DecoderException ex) {
            Logger.getLogger(EllipticCurveWrapper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Write the keys to disk encoded in Hex.
     */
    protected void writeKeys() {
        char[] publicBytes = Hex.encodeHex(publicKey.getEncoded());
        char[] privateBytes = Hex.encodeHex(privateKey.getEncoded());

        FileAccessUtil.writeToDisk(getKeyFilePath(false), new String(publicBytes).getBytes());
        FileAccessUtil.writeToDisk(getKeyFilePath(true), new String(privateBytes).getBytes());
    }

    /**
     * Generate the string path to key files.
     *
     * @param isPrivate boolean flag: true for private key file path, false for public key file path.
     * @return the string path to the key file.
     */
    protected String getKeyFilePath(boolean isPrivate) {
        return String.format("%s%s%s", FileAccessUtil.getUserHome(true), File.separator, isPrivate ? PRIVATE_KEY_FILE_NAME : PUBLIC_KEY_FILE_NAME);
    }
}
