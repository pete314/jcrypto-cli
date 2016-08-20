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
 * @description CryptoSignatureUtil - Handles signing
 * @package ie.peternagy.jcrypto.util
 */
package ie.peternagy.jcrypto.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class CryptoSignatureUtil {
    
    /**
     * Calculate CRC32
     * 
     * @param content - the data to sign
     * @return the checksum
     */
    public static long calculateCrc32(final byte[] content) {
        Checksum checksum = new CRC32();
        checksum.update(content, 0, content.length);
        return checksum.getValue();
    }
    
    /**
     * Calculate SHA256 
     * @param content - the data to sign
     * @return the checksum
     */
    public static byte[] calculateSHA256(final byte[] content){
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(content);
            return md.digest();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(CryptoSignatureUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
