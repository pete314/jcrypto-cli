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
 * @description JCryptoCli - The cli runner for jCrypto
 * @package ie.peternagy.jcrypto.cli
 */
package ie.peternagy.jcrypto.cli;

import ie.peternagy.jcrypto.algo.AesWrapper;
import ie.peternagy.jcrypto.algo.EllipticCurveWrapper;
import ie.peternagy.jcrypto.module.crypto.FileCrypto;
import ie.peternagy.jcrypto.module.config.JCryptoConfig;
import ie.peternagy.jcrypto.util.CryptoSecurityUtil;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

public class JCryptoCli {

    private static boolean isVerbose = false;
    private final static Options OPTIONS = buildCliOptions();

    public static void main(String[] args){
        long startTime = System.currentTimeMillis();
        try {
            CommandLineParser parser = new DefaultParser();
            CommandLine line = parser.parse(OPTIONS, args);
            isVerbose = line.hasOption('v');

            routeParams(line);
            
            if (isVerbose) {
                System.out.printf("\n Process finished in %dms\n\n", System.currentTimeMillis() - startTime);
            }
        } catch (org.apache.commons.cli.ParseException ex) {
            printCliHelp();
            //@todo: override the logger if not in debug mode
            Logger.getLogger(JCryptoCli.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Route the command line parameters to actions 
     * 
     * @param cli - Initialized CommandLine object with current parameters
     */
    private static void routeParams(CommandLine cli){
        if (cli.hasOption('b')) {
            runBenchmark();
        } else if (cli.hasOption('f')) {
            String storageProvider = cli.hasOption('u') ? cli.getOptionValue('u') : null;
            if (storageProvider != null && JCryptoConfig.AVAILABLE_STORAGE_PROVIDERS.containsKey(storageProvider)) {

            } else {
                FileCrypto fileCrypto = new FileCrypto(cli.getOptionValue('f'));
                fileCrypto.cryptFile();
            }
        } else if (cli.hasOption('c')) {
            JCryptoConfig.showConfigOptions();
        }else{
            printCliHelp();
        }
    }
    
    /**
     * Build the Cli options
     * 
     * @return initialized Options
     */
    private static Options buildCliOptions(){
        Options options = new Options();
        options.addOption("b", "banchmark", false, "Run a banchmark test on implementation");
        options.addOption("c", "configure", false, "Run the configuration to setup backup options");
        options.addOption("d", "directory", true, "The path to directory to work with");
        options.addOption("f", "file", true, "The file to work with");
        options.addOption("h", "help", false, "Show help & examples");
        options.addOption("i", "input", true, "Valid file path or stdin as data source");
        options.addOption("m", "mode", true, "Parameter to specify the cryptographic direction{enc or dec}");
        options.addOption("o", "output", true, "File path or stdout as data destination (the file will be created or over written)");
        options.addOption("u", "upload", true, "Upload encrypted content to storage provider");
        options.addOption("v", "verbose", false, "Show details of the process");
        
        return options;
    }
    
    /**
     * Print the cli help with options
     */
    private static void printCliHelp(){
        String helpHeader = "jCrypto cli options";
        String helpFooter = new StringBuilder("\nExamples:")
                                    .append("\nEcnrypt file: ").toString();
        
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp("jCrypto", helpHeader, OPTIONS, helpFooter, true);
    }

    /**
     * Executes a benchmark test
     */
    private static void runBenchmark(){
        int min = 0;
        int max = 0;
        int total = 0;
        int benchmarkMB = 3;
        
        for (int i = 0; i < 50; ++i) {
            int[] results = benchmark(benchmarkMB);
            
            if(isVerbose)
                System.out.printf("\nTest #%d: Time %dms, throughput: %dMB/s", i, results[0], results[1]);
            
            if(i == 0){
                min = max = total = results[1];
            }
            
            if(min > results[1])
                min = results[1];
            if(max < results[1])
                max = results[1];
            
            total += results[1];
        }
        
        System.out.printf("\nThroughput min: %dMB/s\nThroughput max: %dMB/s\nThroughput avg: %dMB/s\n\n", min, max, total / 50);
    }
    
    /**
     * Simple benchmark of the crypto algorithm
     * @param mbSize - the Megabytes to encrypt
     */
    private static int[] benchmark(int mbSize) {
        int testSize = mbSize * (1024 * 1024);//mb
        byte[] mb3Arr = CryptoSecurityUtil.getSecureBytes(testSize);

        AesWrapper aesWrapper = new AesWrapper(new EllipticCurveWrapper());
        aesWrapper.initCipher(true);
        
        long curr = System.currentTimeMillis();
        aesWrapper.doFinal(mb3Arr);
        long execTime = System.currentTimeMillis() - curr;
        int troughPut = (int)(1000 / execTime) * mbSize;
        
        
        return new int[]{(int) execTime, troughPut};
    }
}
