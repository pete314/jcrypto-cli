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
 * @description IConfigHandler - Config handler interface for storage service
 * @package ie.peternagy.jcrypto.module
 */
package ie.peternagy.jcrypto.module.config;

import java.util.Map;

/**
 * IConfigHandler interface defines the contract for storage service configuration handlers.
 * Implementations should handle parsing user input, validating configuration, and initializing service clients.
 */
public interface IConfigHandler {
    
    /**
     * Parse configuration input from the user (e.g., via CLI).
     */
    public void parseConfigInput();

    /**
     * Validate the provided configuration map.
     * This typically involves checking if credentials are correct and if the service is accessible.
     *
     * @param service The configuration map containing service parameters.
     */
    public void validateConfig(Map service);

    /**
     * Get an initialized client for the service.
     *
     * @return An object representing the initialized service client (e.g., AmazonS3Client).
     */
    public Object getInitializedServiceClient();
}
