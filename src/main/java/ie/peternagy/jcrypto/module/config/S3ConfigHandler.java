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
 * @description S3ConfigHandler - Configuration handler for AWS simple storage
 * @package ie.peternagy.jcrypto.module
 */
package ie.peternagy.jcrypto.module.config;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import java.util.Map;

public class S3ConfigHandler extends AServiceConfigHandler implements IConfigHandler {

    protected final String SERVICE_NAME = "aws-s3";
    private final String[] SERVICE_REQUIRED_PARAMS = {"access-key", "secret-key", "bucket-name"};

    public S3ConfigHandler() {
        super();
        service = getService(SERVICE_NAME);
    }

    @Override
    public void parseConfigInput() {
        System.out.println("\n\nAmazon simple storage configuration\n\n");
        getServiceParams(SERVICE_NAME, SERVICE_REQUIRED_PARAMS);
        validateConfig(service);

        String input;
        do {
            System.out.print("\nWould you like to save it? (y/n)");
            input = CLI_IN.next();
            if (input.equalsIgnoreCase("y")) {
                services.put(SERVICE_NAME, service);
                writeStorageConfig();
                break;
            }else if(input.equalsIgnoreCase("n")){
                break;
            }
        } while (true);
    }

    @Override
    public void validateConfig(Map config) {
        AWSCredentials credentials = new BasicAWSCredentials((String) config.get("access-key"), (String) config.get("secret-key"));
        AmazonS3 s3client = new AmazonS3Client(credentials);
        try {
            System.out.println("S3 config validation...\n");
            s3client.doesBucketExist((String) config.get("bucket-name"));

            System.out.println("Valid configuration!\n");
        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which "
                    + "means your request made it "
                    + "to Amazon S3, but was rejected with an error response"
                    + " for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which "
                    + "means the client encountered "
                    + "an internal error while trying to "
                    + "communicate with S3, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
    }

    @Override
    public Object getInitializedServiceClient() {
        if (service != null) {
            AWSCredentials credentials = new BasicAWSCredentials((String) service.get("access-key"), (String) service.get("secret-key"));
            return new AmazonS3Client(credentials);
        }

        return null;
    }

    public String getBucketName() {
        return (String) service.get("bucket-name");
    }
}
