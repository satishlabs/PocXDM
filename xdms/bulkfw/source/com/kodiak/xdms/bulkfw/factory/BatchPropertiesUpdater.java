package com.kodiak.xdms.bulkfw.factory;

import com.kodiak.common.resources.KnConstants;
import com.kodiak.vault.KnCommonVaultUtil;
import com.kodiak.logger.KnLogger;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Properties;

import static com.kodiak.common.resources.KnConstants.T10_PASSWORD_NAME_PATH;
import static com.kodiak.common.resources.KnConstants.T10_USER_NAME_PATH;

public class BatchPropertiesUpdater {
    private static final KnLogger knLogger = KnLogger.getLogger(BatchPropertiesUpdater.class);
    private static String ssl_flag = null;
    private static String wallet = null;
    private static String encryption = null;
    private static String cipherSuites = null;
    private static String clientAuthentication = null;
    private static String localIPAddress = null;
    private static String pttServerId = null;
    private static String DB_URL = null;
    private static String key = "PBKDF2WithHmacSHA256";
    private static String algo = "AES";
    private static String padding = "AES/CBC/PKCS5Padding";
    public static final String SALT = "X@#5";
    public static final String SECRETKEY = "A1B8D95BA9EA5440F252C3";
    public static final String IVSTRING = "0000000000000000";
    public static final Integer KEYSIZE = 256;
    public static final Integer ITERATION = 1000;
    private static String userID;
    private static final String COMMONCONFIGFILE = "CommonConfig.properties";
    private static String commonConfigFile = "/DG/activeRelease/dat" + File.separator + COMMONCONFIGFILE;
    private static Properties commonConfigProps = new Properties();
    private static Properties lDbProps = new Properties();
    private static final String directConnectionUrl = "jdbc:timesten:direct:dsn=DG_";

    public static void updateBatchJdbcCredentials(String propertiesFilePath, String user, String password) {
        String methodName = "updateBatchJdbcCredentials(String,String, String, String)";
        knLogger.info(methodName, "propertiesFilePath: ", propertiesFilePath);
        Properties properties = new Properties();
        try (FileInputStream in = new FileInputStream(propertiesFilePath)) {
            properties.load(in);
        } catch (IOException e) {
            knLogger.error(methodName, "Error loading properties file", e);
            e.printStackTrace();
            return;
        }
        properties.setProperty("batch.jdbc.user", user);
        properties.setProperty("batch.jdbc.password", password);
        try (FileOutputStream out = new FileOutputStream(propertiesFilePath)) {
            properties.store(out, null);
            knLogger.debug(methodName, "Done storing the properties file");
        } catch (IOException e) {
            knLogger.error(methodName, "Error storing properties file", e);
            e.printStackTrace();
        }
    }

    public static void updateBatchJdbcUrl(String propertiesFilePath) {
        String methodName = "updateBatchJdbcUrl(String)";
        Properties properties = new Properties();
        try (FileInputStream in = new FileInputStream(propertiesFilePath)) {
            properties.load(in);
        } catch (IOException e) {
            knLogger.error(methodName, "Exception occured while reading and loading into properties object- ", e);
            e.printStackTrace();
            return;
        }

        String url = properties.getProperty("batch.jdbc.url");
        if (url != null) {
            String newUrl = url.replace(":", ":").replace("=", "=");
            properties.setProperty("batch.jdbc.url", newUrl);
        }

        try (FileOutputStream out = new FileOutputStream(propertiesFilePath)) {
            properties.store(out, null);
        } catch (IOException e) {
            knLogger.error(methodName, "Exception occured while saving the properties- ", e);
            e.printStackTrace();
        }
    }

    public static void getProperty() {

        getPropertyValue();

    }

    private static String getConnectionURL() {
        String methodName = "getConnectionURL()";
        knLogger.info(methodName, "Getting the connection URL: ", "Values from common file " + ssl_flag + wallet +
                encryption + cipherSuites + clientAuthentication);
        if (ssl_flag.equals("1")) {
            knLogger.info(methodName, "SSL flag is enabled");
            DB_URL = "jdbc:timesten:client:TTC_Server=" + localIPAddress + ";TTC_Server_DSN=DG_"
                    + pttServerId + "_6;TCP_PORT=53389"
                    + ";Wallet=" + wallet
                    + ";Encryption=" + encryption
                    + ";CipherSuites=" + cipherSuites
                    + ";SSLClientAuthentication=" + clientAuthentication;
        } else {
            knLogger.info(methodName, "SSL flag is disabled");
            DB_URL = "jdbc:timesten:client:TTC_Server=" + localIPAddress +
                    ";TTC_Server_DSN=DG_" + pttServerId + "_6;TCP_PORT=53389";
        }
        return DB_URL;
    }

    public static void getPropertyValue() {
        String methodName = "getPropertyValue()";
        FileInputStream lFStream = null;
        FileInputStream fileInputStream = null;
        {
            try {
                String propertiesFilePath = "/DG/activeRelease/dat/xdm/batch-timesten.properties";
                String password = null;
                lFStream = new FileInputStream(commonConfigFile);
                lDbProps.load(lFStream);
                userID = KnCommonVaultUtil.getKeyFromVault(T10_USER_NAME_PATH, KnConstants.DBMGR_DBUSERID);
                password = KnCommonVaultUtil.getKeyFromVault(T10_PASSWORD_NAME_PATH, KnConstants.DBMGR_DBPASSWORD);
                fileInputStream = new FileInputStream(commonConfigFile);
                commonConfigProps.load(fileInputStream);
                ssl_flag = commonConfigProps.getProperty("IS_TIMESTEN_SSL_ENABLED");
                wallet = commonConfigProps.getProperty("TIMESTEN_SSL_CLIENTWALLET");
                encryption = commonConfigProps.getProperty("TIMESTEN_SSL_ENCRYPTIONTYPE");
                cipherSuites = commonConfigProps.getProperty("TIMESTEN_SSL_CIPHERSUITES");
                clientAuthentication = commonConfigProps.getProperty("TIMESTEN_SSL_CLIENTAUTH");
                localIPAddress = System.getenv("LOCAL_IP_ADDRESS");
                pttServerId = System.getenv("PTTSERVERID");
                //DB_URL = getConnectionURL();
                updateBatchJdbcCredentials(propertiesFilePath, userID, password);
                updateBatchJdbcUrl(propertiesFilePath);
                knLogger.info(methodName, "Successfully updated the properties file");
            } catch (Exception e) {
                knLogger.error(methodName, "Exception occured while updating the properties file- ", e, "Exiting");
                System.exit(1);
            }
        }
    }

    public static String decrypt(String salt, String encryptedText, String secretKey, String ivString, int keySize, int iterations) throws Exception {
        byte[] iv = ivString.getBytes();
        IvParameterSpec ivspec = new IvParameterSpec(iv);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(key);
        KeySpec spec = new PBEKeySpec(secretKey.toCharArray(), salt.getBytes(), iterations, keySize);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKeySpec secKey = new SecretKeySpec(tmp.getEncoded(), algo);
        Cipher cipher = Cipher.getInstance(padding);
        cipher.init(Cipher.DECRYPT_MODE, secKey, ivspec);
        return new String(cipher.doFinal(Base64.getDecoder().decode(encryptedText)));
    }


}
