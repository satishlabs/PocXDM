/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/***************************************************************************************************
 *  * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 *  * All Rights Reserved                                                                             *
 *  * Motorola Solutions Confidential Restricted                                                      *
 *  **************************************************************************************************
 * <p>>
 * File name:   KnTTDBProps.java
 * Subsystem:
 * <p/>
 * Name                 Date          Release
 * -----------------    -----------   -------
 * Sanjiv A             22/11/23      13.0
 *
 * ************************************************************************
 */
package com.kodiak.tool.memorymonitor.util;



import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Properties;

public class KnTTDBProps {

    private static String key = "PBKDF2WithHmacSHA256";
    private static String algo = "AES";
    private static String padding = "AES/CBC/PKCS5Padding";
    public static final String SALT = "X@#5";
    public static final String SECRETKEY = "A1B8D95BA9EA5440F252C3";
    public static final String IVSTRING = "0000000000000000";
    public static final Integer KEYSIZE = 256;
    public static final Integer ITERATION = 1000;
    private String userID;
    private String password;
    private String ssl_flag;
    private String wallet;
    private String encryption;
    private String cipherSuites;
    private String clientAuthentication;
    public static final String ACTIVE_REL_DIR_ENV_KEY = "activeRelDir";
    Properties lDbProps = new Properties();
    String lActiveRelPath = System.getProperty(ACTIVE_REL_DIR_ENV_KEY);
    public static final String DB_PROPS_FILE_NAME = "dbmgr.props";
    private static KnTTDBProps dBProps;
    public static final String COMMONCONFIGFILE = "CommonConfig.properties";
    public static String commonConfigFile = "/DG/activeRelease/dat" + File.separator + COMMONCONFIGFILE;
    Properties commonConfigProps = new Properties();

    public void init() throws Exception {
        FileInputStream lFStream = null;
        FileInputStream fileInputStream = null;
        {
            try {
                lFStream = new FileInputStream(lActiveRelPath + File.separator + DB_PROPS_FILE_NAME);
                lDbProps.load(lFStream);
                userID = "";
                password = "";


                fileInputStream = new FileInputStream(commonConfigFile);
                commonConfigProps.load(fileInputStream);

                ssl_flag = commonConfigProps.getProperty("IS_TIMESTEN_SSL_ENABLED");
                wallet = commonConfigProps.getProperty("TIMESTEN_SSL_CLIENTWALLET");
                encryption = commonConfigProps.getProperty("TIMESTEN_SSL_ENCRYPTIONTYPE");
                cipherSuites = commonConfigProps.getProperty("TIMESTEN_SSL_CIPHERSUITES");
                clientAuthentication = commonConfigProps.getProperty("TIMESTEN_SSL_CLIENTAUTH");

            } catch (Exception e) {
                e.printStackTrace();
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

    public synchronized static KnTTDBProps getInstance() {
        if (dBProps == null) {
            String lMethName = "getInstance()";
            dBProps = new KnTTDBProps();
        }
        return dBProps;
    }

    public String getUserID() {
        return userID;
    }

    public String getPassword() {
        return password;
    }

    public String getSsl_flag() {
        return ssl_flag;
    }

    public String getWallet() {
        return wallet;
    }

    public String getEncryption() {
        return encryption;
    }

    public String getCipherSuites() {
        return cipherSuites;
    }

    public String getClientAuthentication() {
        return clientAuthentication;
    }


}
