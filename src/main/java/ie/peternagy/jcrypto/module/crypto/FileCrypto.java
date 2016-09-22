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
 * @description FileCrypto - File en/decryption module
 * @package ie.peternagy.jcrypto.module
 */
package ie.peternagy.jcrypto.module.crypto;

import ie.peternagy.jcrypto.algo.AesWrapper;
import ie.peternagy.jcrypto.algo.EllipticCurveWrapper;
import ie.peternagy.jcrypto.util.FileAccessUtil;
import java.io.File;
import org.apache.commons.io.FilenameUtils;

public class FileCrypto {
    private final AesWrapper aesWrapper;
    private final EllipticCurveWrapper curve;
    private final File inputFile;
    private final String inputFileString;
    private boolean isEncrypt;
    
    public FileCrypto(String filePath) {
        curve = new EllipticCurveWrapper();
        aesWrapper = new AesWrapper(curve);
        inputFile = FileAccessUtil.getFileByName(filePath);
        inputFileString = filePath;
        if("enc".equals(FilenameUtils.getExtension(filePath))){
            isEncrypt = false;
            aesWrapper.initCipher(false);
        }else{
            isEncrypt = true;
            aesWrapper.initCipher(true);
        }
    }
    
    public void cryptFile(){
        byte[] fileContent = FileAccessUtil.readFromDisk(inputFile);
        byte[] data;
        String outFileName;
        if(isEncrypt){
            data = aesWrapper.doFinalWithHeader(fileContent);
            outFileName = inputFileString + ".enc";
        }else{
            data = aesWrapper.doFinalWithHeader(fileContent);
            outFileName = inputFileString.substring(0, inputFileString.length() - 4);
        }
        
        FileAccessUtil.writeToDisk(outFileName, data);
    }
    
}
