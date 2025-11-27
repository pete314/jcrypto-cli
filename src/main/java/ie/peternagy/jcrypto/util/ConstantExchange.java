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
 * @description ConstantExchange
 * @package ie.peternagy.jcrypto.util
 */
package ie.peternagy.jcrypto.util;

import java.io.File;

/**
 * ConstantExchange holds application-wide constants.
 * This includes version information, file extensions, and configuration file paths.
 */
public class ConstantExchange {
    public static final byte CURRENT_VERSION = (byte)100;
    public static final String ENCRYPTED_EXTENSION = "enc";
    public static final String STORAGE_CONFIG_FILE_NAME = "storage_conf.enc";
    public static final File STORAGE_CONF_FILE = new File(FileAccessUtil.getUserHome(true) + File.separator + ConstantExchange.STORAGE_CONFIG_FILE_NAME);
    
}
