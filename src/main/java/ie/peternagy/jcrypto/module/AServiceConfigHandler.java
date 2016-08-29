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
 * @description AServiceConfigurator - Abstract configuration class for storage
 * service
 * @package ie.peternagy.jcrypto.module
 */
package ie.peternagy.jcrypto.module;

import ie.peternagy.jcrypto.algo.AesWrapper;
import ie.peternagy.jcrypto.algo.EllipticCurveWrapper;
import ie.peternagy.jcrypto.util.ConstantExchange;
import ie.peternagy.jcrypto.util.FileAccessUtil;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.yaml.snakeyaml.Yaml;

public abstract class AServiceConfigHandler {

    protected Map<String, Map> services;
    protected Map service;
    protected final Scanner CLI_IN = new Scanner(System.in);

    public AServiceConfigHandler() {
        readStorageConfig();
    }

    /**
     * Read encrypted storage configuration yaml
     *
     */
    public void readStorageConfig() {
        services = new HashMap<>();

        try {
            if (!ConstantExchange.STORAGE_CONF_FILE.exists()) {
                return;
            }
            AesWrapper aesWrapper = new AesWrapper(new EllipticCurveWrapper(), false);
            byte[] configEncBytes = FileAccessUtil.readFromDisk(ConstantExchange.STORAGE_CONF_FILE);
            byte[] configYamlBytes = aesWrapper.doFinalWithHeader(configEncBytes);
            
            Yaml yaml = new Yaml();
            for (Object entryBlock : yaml.loadAll(new String(configYamlBytes, "UTF-8"))) {
                service = (Map) entryBlock;
                String serviceName = (String) service.get("service");
                services.put(serviceName, service);
            }
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(JCryptoConfig.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(JCryptoConfig.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Create storage configuration yaml and write encrypted bytes
     */
    public void writeStorageConfig() {
        try {
            AesWrapper aesWrapper = new AesWrapper(new EllipticCurveWrapper(), true);
            StringWriter output = new StringWriter();
            Yaml yaml = new Yaml();
            for (Map.Entry<String, Map> tmpService : services.entrySet()) {
                yaml.dump(tmpService.getValue(), output);//this assumes that the "tmpService:" is containedin the 
            }
            byte[] encBytes = aesWrapper.doFinalWithHeader(output.toString().getBytes("UTF-8"));
            
            //@todo: should check for successful write
            FileAccessUtil.writeToDisk(ConstantExchange.STORAGE_CONF_FILE, encBytes);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(AServiceConfigHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
