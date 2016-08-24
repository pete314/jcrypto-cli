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
 * @description JCryptoCli - The cli entry class
 * @package ie.peternagy.jcrypto.cli
 */
package ie.peternagy.jcrypto.cli;

import ie.peternagy.jcrypto.algo.AesWrapper;
import ie.peternagy.jcrypto.algo.EllipticCurveWrapper;
import ie.peternagy.jcrypto.module.FileCrypto;
import ie.peternagy.jcrypto.util.CryptoSecurityUtil;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

public class JCryptoCli {
    private static boolean isVerbose = false;
    
    public static void main(String[] args) throws NoSuchAlgorithmException {
        long startTime = System.currentTimeMillis();
        try {
            Options options = new Options();
            options.addOption("v", "verbose", false, "Show details of the process");
            options.addOption("b", "banchmark", false, "Run a banchmark test on implementation");
            options.addOption("f", "file", true, "The file to work with");

            CommandLineParser parser = new DefaultParser();
            CommandLine line = parser.parse(options, args);
            isVerbose = line.hasOption('v');
            
            if (line.hasOption('b')) {
                for (int i = 0; i < 50; ++i) {
                    System.out.println("Banchmark test " + i);
                    banchmark();
                }
            }else if(line.hasOption('f')){
                FileCrypto fileCrypto = new FileCrypto(line.getOptionValue('f'));
                fileCrypto.cryptFile();
            }
            
            if(isVerbose){
                System.out.printf("\n Process finished in %dms\n\n", System.currentTimeMillis() - startTime);
            }
            
        } catch (org.apache.commons.cli.ParseException ex) {
            Logger.getLogger(JCryptoCli.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static void banchmark() {
        int testSize = 3000000;
        byte[] mb3Arr = CryptoSecurityUtil.getSecureBytes(testSize);

        AesWrapper aesWrapper = new AesWrapper(new EllipticCurveWrapper());
        aesWrapper.initCipher(true);
        long curr = System.currentTimeMillis();
        aesWrapper.doFinal(mb3Arr);
        long execTime = System.currentTimeMillis() - curr;
        System.out.printf("Time %dms, throughput: %dMB/s", execTime, (1000 / execTime) * 3);
    }
}
