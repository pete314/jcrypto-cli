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
 * @description JCryptoCli - Short description
 * @package ie.peternagy.jcrypto.cli
 */
package ie.peternagy.jcrypto.cli;

import ie.peternagy.jcrypto.algo.EllipticCurveWrapper;
import ie.peternagy.jcrypto.util.CryptoSecurityUtil;
import ie.peternagy.jcrypto.util.FileAcccessUtil;
import java.security.KeyPairGenerator;
import java.security.Provider;
import java.security.Security;
import java.util.Enumeration;
import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 *
 * @author pete
 */
public class JCryptoCli {

    public static void main(String[] args) {
        Security.addProvider(new BouncyCastleProvider());
        Security.getProvider("BC");
        System.out.println(FileAcccessUtil.getUserHome(true));
        EllipticCurveWrapper ec = new EllipticCurveWrapper();
        //ec.generateKeys();
        ec.tryLoadKeys();
        byte[] b = CryptoSecurityUtil.getSecureBytes(50);
        byte[] c = ec.doFinal(b, true);
        System.out.println("in " + Hex.encodeHexString(b));
        System.out.println("in " + Hex.encodeHexString(c));
    }
}
