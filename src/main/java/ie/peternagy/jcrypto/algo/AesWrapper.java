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
 * @description AesWrapper - AES algorithm wrapper class
 * @package ie.peternagy.jcrypto.algo
 */
package ie.peternagy.jcrypto.algo;

import ie.peternagy.jcrypto.util.CryptoSecurityUtil;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.lang3.ArrayUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

public class AesWrapper {

    private final String KEYGEN_ALGORITHM = "PBKDF2WithHmacSHA512";
    private final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
    private final EllipticCurveWrapper curve;
    private Cipher cipher;
    private SecretKey secretKey;
    private byte[] iv;
    private byte[] salt;
    private byte[] baseKey;
    private final String ALGORITHM_NAME = "AES";
    private boolean state;//true>>encrypt

    public AesWrapper(EllipticCurveWrapper curve) {
        Security.addProvider(new BouncyCastleProvider());
        Security.getProvider("BC");
        this.curve = curve;
        iv = CryptoSecurityUtil.getSecureBytes(16);
        salt = CryptoSecurityUtil.getSecureBytes(16);
        baseKey = CryptoSecurityUtil.getSecureBytes(64);
        generateSecretKey();
    }

    public AesWrapper(EllipticCurveWrapper curve, boolean state) {
        this(curve);
        this.state = state;
        initCipher(state);
    }
    
    

    /**
     * Initialize the cipher
     *
     * @param isEncrypt - true >> encryption
     */
    public void initCipher(boolean isEncrypt) {
        try {
            state = isEncrypt;
            cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(isEncrypt ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
        } catch (GeneralSecurityException e) {
            System.err.println(e);
            throw new RuntimeException("Invalid environment, check max key size xx", e);
        }
    }

    /**
     * Generate secret key from iv, salt, baseKey
     *
     */
    protected final void generateSecretKey() {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(KEYGEN_ALGORITHM);
            KeySpec keySpec = new PBEKeySpec(new String(baseKey).toCharArray(), salt, 4096, 256);
            SecretKey generalSecret = factory.generateSecret(keySpec);

            secretKey = new SecretKeySpec(generalSecret.getEncoded(), ALGORITHM_NAME);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            Logger.getLogger(AesWrapper.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException("Invalid environment, check max key size", ex);
        }
    }

    /**
     * Close final block of multi part encryption
     *
     * @return the final block
     */
    public byte[] doFinal() {
        try {
            return cipher.doFinal();
        } catch (IllegalBlockSizeException | BadPaddingException ex) {
            Logger.getLogger(AesWrapper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Crypt the data
     *
     * @param data - the bytes to work with
     * @return the modified bytes
     */
    public byte[] doFinal(byte[] data) {
        try {
            return cipher.doFinal(data);
        } catch (IllegalBlockSizeException | BadPaddingException ex) {
            Logger.getLogger(AesWrapper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Encrypt a chunk of data (used in large data sets with doFinal())
     *
     * @param data - chunk to encrypt
     * @return
     */
    public byte[] update(byte[] data) {
        return cipher.update(data);
    }

    public byte[] doFinalWithHeader(byte[] data) {
        if (state) {
            byte[] finalBlock = doFinal(data);
            byte[] header = createHeader();
            return ArrayUtils.addAll(header, finalBlock);
        }else{
            return doFinal(extractHeader(data));
        }
    }

    protected byte[] createHeader() {
        try {
            byte[] garbageByte = CryptoSecurityUtil.getSecureBytes(CryptoSecurityUtil.getRandomIntInRange(0, 768));
            byte[] baseKeyEnc = curve.doFinalWithHeader(baseKey, true);
            ByteArrayOutputStream header = new ByteArrayOutputStream();

            header.write((byte) 100);
            header.write(ByteBuffer.allocate(Integer.SIZE / Byte.SIZE).putInt(iv.length).array());
            header.write(ByteBuffer.allocate(Integer.SIZE / Byte.SIZE).putInt(salt.length).array());
            header.write(ByteBuffer.allocate(Integer.SIZE / Byte.SIZE).putInt(garbageByte.length).array());
            header.write(ByteBuffer.allocate(Integer.SIZE / Byte.SIZE).putInt(baseKeyEnc.length).array());
            header.write(iv);
            header.write(salt);
            header.write(garbageByte);
            header.write(baseKeyEnc);//encrypt with EC
            //include: long and crc32 for data

            return header.toByteArray();
        } catch (IOException ex) {
            Logger.getLogger(AesWrapper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    protected byte[] extractHeader(byte[] data){
        int version = data[0];
        int currentPosition = 1;
        int ivLength = ByteBuffer.wrap(ArrayUtils.subarray(data, currentPosition, currentPosition +Integer.BYTES)).getInt();
        currentPosition += Integer.BYTES;
        int saltLength = ByteBuffer.wrap(ArrayUtils.subarray(data, currentPosition, currentPosition +Integer.BYTES)).getInt();
        currentPosition += Integer.BYTES;
        int garbageLength = ByteBuffer.wrap(ArrayUtils.subarray(data, currentPosition, currentPosition +Integer.BYTES)).getInt();
        currentPosition += Integer.BYTES;
        int baseKeyLength = ByteBuffer.wrap(ArrayUtils.subarray(data, currentPosition, currentPosition +Integer.BYTES)).getInt();
        currentPosition += Integer.BYTES;
        iv = ArrayUtils.subarray(data, currentPosition, currentPosition + ivLength);
        currentPosition += ivLength;
        salt = ArrayUtils.subarray(data, currentPosition, currentPosition + saltLength);
        currentPosition += saltLength;
        currentPosition += garbageLength;//skip garbage
        byte[] encKeyBase = ArrayUtils.subarray(data, currentPosition, currentPosition + baseKeyLength);
        currentPosition += baseKeyLength;
        baseKey = curve.doFinalWithHeader(encKeyBase, false);
        
        byte[] content = ArrayUtils.subarray(data, currentPosition, data.length);
        
        generateSecretKey();
        initCipher(state);
        
        return content;
    }
}
