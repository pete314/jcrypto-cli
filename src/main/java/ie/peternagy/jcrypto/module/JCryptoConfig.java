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
 * @description JCryptoConfig - The class handles configuration
 * @package ie.peternagy.jcrypto.module
 */
package ie.peternagy.jcrypto.module;

import java.util.Scanner;

public class JCryptoConfig {
    private static final Scanner CLI_IN = new Scanner(System.in);

    /**
     * @todo: All config changes should include account lookup
     * @todo: Should create default empty config file and load
     */
    public static void showConfigOptions() {
        do {
            printMessage("\n\nConfiguration options:", "1) Configure S3", "2) Configure Google Drive", "3) Configure Dropbox", "4) Configure OneDrive", "0) Exit this menu");
            System.out.print("\nChoose one: ");
            int option = tryParseInt(CLI_IN.next(), 0, 3);

            switch (option) {
                case 0:
                    printMessage("Option 0");
                    printMessage("bye now");
                    return;
                case 1:
                    printMessage("Option 1");
                    S3ConfigHandler configHandler = new S3ConfigHandler();
                    configHandler.parseConfigInput();
                    break;
                case 2:
                    printMessage("Option 2");
                    break;
                case 3:
                    printMessage("Option 3");
                    break;
                default:
                    printMessage("Invalid option");
            }
        } while (true);
    }
    
    /**
     * Try parse string input to integer in range
     *
     * @param input - the input string
     * @param min - the lower bound of accepted number
     * @param max - the upper bound of accepted number
     * @return the valid number or -1 if invalid
     */
    protected static int tryParseInt(String input, int min, int max) {
        try {
            int iTmp = Integer.parseInt(input);

            if (iTmp >= min && iTmp <= max) {
                return iTmp;
            }
        } catch (Exception e) {
            //nothing to do
        }
        return -1;
    }

    /**
     * Print message(s)
     *
     * @param messages - the message(s) to print
     */
    public static void printMessage(String... messages) {
        for (String msg : messages) {
            System.out.println(msg);
        }
    }
}
