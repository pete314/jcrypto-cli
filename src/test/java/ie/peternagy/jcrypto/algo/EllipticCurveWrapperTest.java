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
 * @description EllipticCurveWrapperTest - Test class for EllipticCurveWrapper
 * @package ie.peternagy.jcrypto.algo
 */
package ie.peternagy.jcrypto.algo;

import ie.peternagy.jcrypto.util.CryptoSecurityUtil;
import org.bouncycastle.util.Arrays;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author pete
 */
public class EllipticCurveWrapperTest extends AjUnitWrapper {

    protected static byte[] input;
    protected static EllipticCurveWrapper curve;

    public EllipticCurveWrapperTest() {
        testClassName = EllipticCurveWrapper.class.getName();
        input = CryptoSecurityUtil.getSecureBytes(128);
        curve = new EllipticCurveWrapper();
    }

    /**
     * Test of doFinal method, of class EllipticCurveWrapper.
     */
    @Test
    public void testDoFinal() {
        System.out.println("doFinal - encrypt and decrypt");
        curve.tryLoadKeys();
        byte[] encData = curve.doFinal(input, true);
        byte[] decData = curve.doFinal(encData, false);

        assertTrue(Arrays.areEqual(decData, input));
    }

    /**
     * Test of doFinalWithHeader method, of class EllipticCurveWrapper.
     */
    @Test
    public void testDoFinalWithHeader() {
        System.out.println("doFinalWithHeader");
        curve.tryLoadKeys();
        byte[] encData = curve.doFinalWithHeader(input, true);
        byte[] decData = curve.doFinalWithHeader(encData, false);

        assertTrue(Arrays.areEqual(decData, input));
    }

    /**
     * Test of generateKeys method, of class EllipticCurveWrapper.
     */
    @Test
    public void testGenerateKeys() {
        System.out.println("generateKeys");
        EllipticCurveWrapper instance = new EllipticCurveWrapper();
        Exception ex = null;
        try {
            instance.generateKeys();
        } catch (Exception e) {
            ex = e;
        }
        
        assertTrue(ex == null);
    }

    /**
     * Test of getKeyId method, of class EllipticCurveWrapper.
     */
    @Test
    public void testGetKeyId() {
        System.out.println("getKeyId");
        curve.tryLoadKeys();
        byte[] expResult = curve.getKeyId();
        byte[] result = curve.getKeyId();
        
        assertTrue(Arrays.areEqual(expResult, result));
    }

    /**
     * Test of isInitialized method, of class EllipticCurveWrapper.
     */
    @Test
    public void testIsInitialized() {
        System.out.println("isInitialized");
        
        curve.tryLoadKeys();
        boolean privateLoaded = curve.isInitialized(true);
        boolean publicLoaded = curve.isInitialized(false);
        
        assertEquals(true, privateLoaded == publicLoaded);
    }

    /**
     * Test of tryLoadKeys method, of class EllipticCurveWrapper.
     */
    @Test
    public void testTryLoadKeys() {
        System.out.println("tryLoadKeys");
        EllipticCurveWrapper instance = new EllipticCurveWrapper();
        Exception ex = null;
        try {
            instance.tryLoadKeys();
        } catch (Exception e) {
            ex = e;
            System.out.println("Erro " + ex);
        }
        
        assertTrue(null == ex);
    }

    /**
     * Test of writeKeys method, of class EllipticCurveWrapper.
     */
    @Test
    public void testWriteKeys() {
        System.out.println("writeKeys");
        EllipticCurveWrapper instance = new EllipticCurveWrapper();
        instance.tryLoadKeys();
        Exception ex = null;
        try {
            instance.writeKeys();
        } catch (Exception e) {
            ex = e;
        }
        
        assertTrue(ex == null);
    }

}
