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
 * @description AesWrapperTest - Short description
 * @package ie.peternagy.jcrypto.algo
 */
package ie.peternagy.jcrypto.algo;

import ie.peternagy.jcrypto.util.CryptoSecurityUtil;
import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.util.Arrays;
import static org.junit.Assert.*;

/**
 *
 * @author pete
 */
public class AesWrapperTest extends AjUnitWrapper {

    protected static byte[] input;
    protected static byte[] output;
    protected static AesWrapper aesWrapper;
    protected static EllipticCurveWrapper curve;

    public AesWrapperTest() {
        testClassName = AesWrapper.class.getName();
        input = CryptoSecurityUtil.getSecureBytes(128);
        curve = new EllipticCurveWrapper();
        aesWrapper = new AesWrapper(curve);
    }

    /**
     * Test of initCipher method, of class AesWrapper.
     */
    @org.junit.Test
    public void testInitCipher() {
        boolean isEncrypt = false;
        Exception ex = null;
        AesWrapper instance = new AesWrapper(null);
        try {
            instance.initCipher(isEncrypt);
        } catch (Exception e) {
            ex = e;
        }

        assertTrue(null == ex);
    }


    /**
     * Test of doFinal method, of class AesWrapper.
     */
    @org.junit.Test
    public void testDoFinal_0args() {
        System.out.println("doFinal - encrypt");
        aesWrapper.initCipher(true);
        byte[] result = aesWrapper.doFinal();
        
        assertTrue(!Arrays.areEqual(result, input));
    }

    /**
     * Test of doFinal method, of class AesWrapper.
     */
    @org.junit.Test
    public void testDoFinal_byteArr() {
        System.out.println("doFinal");
        aesWrapper.initCipher(true);
        byte[] data = aesWrapper.doFinal(input);
        aesWrapper.initCipher(false);
        byte[] expResult = aesWrapper.doFinal(data);
        assertTrue(Arrays.areEqual(expResult, input));
    }

    /**
     * Test of update method, of class AesWrapper.
     */
    //@org.junit.Test
    public void testUpdate() {
        System.out.println("update");
        byte[] data = null;
        AesWrapper instance = null;
        byte[] expResult = null;
        byte[] result = instance.update(data);
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
    /**
     * Test doFinalWithHeaders
     */
    @org.junit.Test
    public void testDoFinalWithHeaders() {
        System.out.println("doFinalWithHeaders");
        aesWrapper.initCipher(true);
        
        byte[] data = aesWrapper.doFinalWithHeader(input);
        aesWrapper.initCipher(false);
        byte[] result = aesWrapper.doFinalWithHeader(data);
        
        assertTrue(Arrays.areEqual(result, input));
    }

    /**
     * Test doFinalWithHeaders - compact init
     */
    @org.junit.Test
    public void testDoFinalWithHeadersCompact() {
        System.out.println("doFinalWithHeaders - compact");
        input = CryptoSecurityUtil.getSecureBytes(128);
        aesWrapper = new AesWrapper(new EllipticCurveWrapper(), true);
        
        byte[] data = aesWrapper.doFinalWithHeader(input);
        aesWrapper = new AesWrapper(new EllipticCurveWrapper(), false);
        byte[] result = aesWrapper.doFinalWithHeader(data);
        
        assertTrue(Arrays.areEqual(result, input));
    }
}
