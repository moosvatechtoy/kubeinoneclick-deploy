package io.codeka.gaia.modules.util;

import io.codeka.gaia.modules.bo.TerraformModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Base64;

public class ModuleUtil {
    private static final Logger LOG = LoggerFactory.getLogger(ModuleUtil.class);
    private static  final String CREDENTIALS_PATH = "%s/%s";
    private static  final String CREDENTIALS_FILE = "/credential";

    public static void addSecretFile(TerraformModule  terraformModule, String credentialsLocation) {
        String credentialsFilePath = String.format(CREDENTIALS_PATH, credentialsLocation, terraformModule.getMainProvider().toLowerCase());
        FileOutputStream fos = null;
        File credentialsFile = null;
        try {
            File credentialDir = new File(credentialsFilePath);
            if(!credentialDir.exists()) {
                credentialDir.mkdirs();
            }
            credentialsFile = new File(credentialsFilePath + CREDENTIALS_FILE);
            fos = new FileOutputStream(credentialsFile);
            byte[] decodedCredentials = Base64.getDecoder().decode(terraformModule.getSecretKey());
            fos.write(decodedCredentials);
            fos.flush();
        } catch (Exception e) {
            LOG.error("Error credentials store", e);
        } finally {
            try {
                if (null != fos) {
                    fos.close();
                }
            } catch (Exception e) {
                LOG.error("Error closing resources", e);
            }
        }
    }
}
