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
 * @description FileAcccessUtil - Handles data IO on disk
 * @package ie.peternagy.jcrypto.util
 */
package ie.peternagy.jcrypto.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileAcccessUtil {
    
    private static final String APP_FOLDER_NAME = ".jcrypto";
    
    /**
     * Check and create the application data folder if required
     * @return true if directory exist or created
     */
    protected static boolean checkCreateAppFolder(){
        File dir = new File(getUserHome(false) + "/" + APP_FOLDER_NAME);
        if(dir.exists() && dir.isDirectory())
            return true;
        else
            return dir.mkdir();
    }
    
    /**
     * Get the user home directory path
     * @param includeAppFolder - appends the .jcrypto
     * @return the path string | null
     */
    public static String getUserHome(boolean includeAppFolder){
        if(includeAppFolder){
            checkCreateAppFolder();
            return System.getProperty("user.home") + "/" + APP_FOLDER_NAME;
        }else
            return System.getProperty("user.home");
    }
    
    /**
     * Initialize and validate string file path
     * @param filePath - the string path
     * @return File with path
     */
    public static File getFileByName(String filePath){
        File file = new File(filePath);
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(FileAcccessUtil.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if(file.isFile() && file.canWrite())
            return file;
        else
            return null;
    }
    
    
    /**
     * Write bytes to disk 
     * 
     * @param outFilePath - the file path string
     * @param content - data to write
     * @return the result true|false
     */
    public static boolean writeToDisk(String outFilePath, byte[] content){
        return writeToDisk(getFileByName(outFilePath), content);
    }
    
    /**
     * Write bytes to disk 
     * 
     * @param outFile - file to write to
     * @param content - data to write
     * @return the result true|false
     */
    public static boolean writeToDisk(File outFile, byte[] content){
        try(final FileChannel writeFileChannel = new RandomAccessFile(outFile, "rw").getChannel()){
            MappedByteBuffer buffer = writeFileChannel.map(FileChannel.MapMode.READ_WRITE, 0, content.length);
            buffer.put(content);
            
            return true;
        } catch (IOException ex) {
            Logger.getLogger(FileAcccessUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return false;
    }
    
    public static byte[] readFromDisk(String inFilePath){
        return readFromDisk(getFileByName(inFilePath));
    }
    
    public static byte[] readFromDisk(File inFile){
        try (final FileChannel readFileChannel = new RandomAccessFile(inFile, "r").getChannel()){
            MappedByteBuffer buffer = readFileChannel.map(FileChannel.MapMode.READ_ONLY, 0, readFileChannel.size());
            byte[] allBytes = new byte[buffer.remaining()];
            buffer.get(allBytes);
            
            return allBytes;
        } catch (IOException ex) {
            Logger.getLogger(FileAcccessUtil.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException("Input file is not readable \n" + ex);
        }
    }
}
