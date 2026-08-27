/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p>
 * File name:  KnDeleteDeviceUtility.java
 * Subsystem:  PoC
 * <p>
 * Name                         Date                     Release
 * --------------------     ----------------        ------------------
 * Sunil Biradar             10-Apr-2023                  12.3.2
 * <p>
 * <p>
 * The information disclosed herein is confidential and proprietary to Kodiak and/or Motorola Solutions, Inc.,
 * and is being furnished under a valid license agreement. This information may not be disclosed to third parties
 * without the prior written consent of Kodiak and/or Motorola Solutions, Inc. The recipient of this information
 * shall respect the security status of the information.
 * <p>
 * MOTOROLA, MOTO, MOTOROLA SOLUTIONS, and the Stylized M Logo are trademarks or registered trademarks of Motorola
 * Trademark Holdings, LLC and are used under license. All other trademarks are the property of their respective owners.
 * © 2022 Motorola Solutions, Inc. All rights reserved.
 * ************************************************************************
 */

package com.kodiak.common.resources;

import com.kodiak.vault.KnCommonVaultUtil;

import java.io.File;
import java.io.FileInputStream;
import java.security.spec.KeySpec;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import static com.kodiak.common.resources.KnConstants.T10_PASSWORD_NAME_PATH;
import static com.kodiak.common.resources.KnConstants.T10_USER_NAME_PATH;

/**
 * This Utility is specific for PR MINT-10538 (MINT-10889)
 *
 */
public class KnDeleteDeviceUtility {

    private static String ssl_flag = null;
    private static String wallet = null;
    private static String encryption = null;
    private static String cipherSuites = null;
    private static String clientAuthentication = null;
    private static String DB_URL = null;
    private static String localIPAddress = null;
    private static String pttServerId = null;
    private static String userID;
    private static String password;
    private static String key = "PBKDF2WithHmacSHA256";
    private static String algo = "AES";
    private static String padding = "AES/CBC/PKCS5Padding";
    public static final String SALT = "X@#5";
    public static final String SECRETKEY = "A1B8D95BA9EA5440F252C3";
    public static final String IVSTRING = "0000000000000000";
    public static final Integer KEYSIZE = 256;
    public static final Integer ITERATION = 1000;
    private static final String COMMONCONFIGFILE = "CommonConfig.properties";
    private static String commonConfigFile = "/DG/activeRelease/dat" + File.separator + COMMONCONFIGFILE;
    private static Properties lDbProps = new Properties();


    public static void main(String args[]) {
        try {
            getPropertyValue();
            deleteDevice();
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("#######################::ERROR::###########################");
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void deleteDevice() throws SQLException, ClassNotFoundException {
        System.out.println("Entering into deleteDevice Operations");
        String localDBUid = "";
        String localDBPwd = "";
        Connection conn = null;
        PreparedStatement stmt2 = null;

        String QRY_SELECT_DEVICE_ID = "SELECT DEVICEID from DG.DEVICE_INFO where DEVICEID IN (SELECT DEVICEID FROM DG.DEVICE_ADDLINFO WHERE DEVICEADDLINFO=0x7B2249734D756C74695A6F6E65223A66616C73652C224E6F4F664368616E6E656C735065725A6F6E65223A302C224465766963654653223A6E756C6C7D) AND DEVICE_TYPE=0";
        String QRY_DELETE_DEVICE_IMPI_INFO = "DELETE FROM DG.DEVICEIMPIINFO WHERE DEVICE_IMPI IN (SELECT DEVICE_IMPI FROM DG.DEVICE_INFO WHERE DEVICEID IN (DEVICEIDS))";
        String QRY_DELETE_DEVICE_INFO = "DELETE FROM DG.DEVICE_INFO WHERE DEVICEID IN (DEVICEIDS)";
        String QRY_DELETE_DEVICE_ADDLINFO = "DELETE FROM DG.DEVICE_ADDLINFO WHERE DEVICEID IN (DEVICEIDS)";

        //System.out.println("localIPAddress:" + localIPAddress + " PTTserverId:" + pttServerId);

        Class.forName("com.timesten.jdbc.TimesTenDriver");

		/*final String DB_URL = "jdbc:timesten:client:TTC_Server=" + localIPAddress + ";TTC_Server_DSN=DG_" + pttServerId
				+ "_6;TCP_PORT=53389";
		conn = DriverManager.getConnection(DB_URL, localDBUid, localDBPwd);*/

        //final String DB_URL = "jdbc:timesten:client:TTC_Server=" + localIPAddress + ";TTC_Server_DSN=DG_" + pttServerId + "_6;TCP_PORT=53389";
        DB_URL = getConnectionURL();
        conn = DriverManager.getConnection(DB_URL, userID, password);
        stmt2 = conn.prepareStatement(QRY_SELECT_DEVICE_ID);
        System.out.println("Executing select query : " + QRY_SELECT_DEVICE_ID);
        ResultSet rs2 = stmt2.executeQuery();
        List<Long> deviceIds = new ArrayList<>();
        while (rs2.next()) {
            System.out.println(rs2.getString("DEVICEID"));
            Long in = Long.valueOf(rs2.getString("DEVICEID"));
            deviceIds.add(in);
        }
        System.out.println("All deviceIds : " + deviceIds);
        var deviceIdLists = splitList(deviceIds, 1000);
        for (var deviceIdList : deviceIdLists) {
            System.out.println("deviceIds from DEVICEIMPIINFO : " + deviceIdList);
            String query = replaceContactWithValue(QRY_DELETE_DEVICE_IMPI_INFO, "DEVICEIDS", formIntegerCommaSeperatedIdList(deviceIdList));
            PreparedStatement stmt4 = conn.prepareStatement(query);
            System.out.println("Executing delete query : " + query);
            int i = stmt4.executeUpdate();
            System.out.println("Query no of rows successfully deleted from DEVICEIMPIINFO : " + i);
        }
        for (var deviceIdList : deviceIdLists) {
            System.out.println("deviceIds from DEVICE_ADDLINFO: " + deviceIdList);
            String query = replaceContactWithValue(QRY_DELETE_DEVICE_ADDLINFO, "DEVICEIDS", formIntegerCommaSeperatedIdList(deviceIdList));
            PreparedStatement stmt6 = conn.prepareStatement(query);
            System.out.println("Executing delete query : " + query);
            int i = stmt6.executeUpdate();
            System.out.println("Query no of rows successfully deleted from DEVICE_ADDLINFO : " + i);
        }
        for (var deviceIdList : deviceIdLists) {
            System.out.println("deviceIds from DEVICE_INFO: " + deviceIdList);
            String query = replaceContactWithValue(QRY_DELETE_DEVICE_INFO, "DEVICEIDS", formIntegerCommaSeperatedIdList(deviceIdList));
            PreparedStatement stmt5 = conn.prepareStatement(query);
            System.out.println("Executing delete query : " + query);
            int i = stmt5.executeUpdate();
            System.out.println("Query no of rows successfully deleted from DEVICE_INFO : " + i);
        }
        conn.close();

    }

    public static String formIntegerCommaSeperatedIdList(Collection<Long> collectionStr) {
        StringBuffer buffer = new StringBuffer(200);
        if (collectionStr == null || collectionStr.isEmpty()) {
            return "";
        }
        for (Long str : collectionStr) {
            buffer = buffer.append(str).append(",");
        }
        int indx = buffer.lastIndexOf(",");
        if (indx > 0) {
            buffer.deleteCharAt(indx);
        }
        return buffer.toString();
    }

    public static String replaceContactWithValue(String str, String constant, String value) {
        String finalStr = "";
        if (str == null || str.trim().equals("")) {
            return finalStr;
        }
        finalStr = str.replaceAll(constant, value);
        return finalStr;
    }

    public static <T> List<List<T>> splitList(List<T> alist, final int len) {
        return IntStream.range(0, alist.size()) // Iterate over the whole thing
                .filter(i -> i % len == 0) // Filter out every 'len' number
                .boxed() // Create a stream (instead of IntStream)
                .map(i -> alist.subList(i, Math.min(i + len, alist.size()))) // create sublists
                .collect(Collectors.toList()); // Collect the whole thing to a list of lists
    }

    private static String getConnectionURL() {
        System.out.println("Values from common file " + ssl_flag + " " + wallet + " " + encryption + " " + cipherSuites + " " + clientAuthentication + " " +
                localIPAddress + " " + pttServerId);
        if (ssl_flag.equals("1")) {
            System.out.println("SSL flag is enabled");
            DB_URL = "jdbc:timesten:client:TTC_Server=" + localIPAddress + ";TTC_Server_DSN=DG_"
                    + pttServerId + "_6;TCP_PORT=53389"
                    + ";Wallet=" + wallet
                    + ";Encryption=" + encryption
                    + ";CipherSuites=" + cipherSuites
                    + ";SSLClientAuthentication=" + clientAuthentication;
        } else {
            System.out.println("SSL flag is disabled");
            DB_URL = "jdbc:timesten:client:TTC_Server=" + localIPAddress +
                    ";TTC_Server_DSN=DG_" + pttServerId + "_6;TCP_PORT=53389";
        }
        System.out.println("DB_URL : " + DB_URL);
        return DB_URL;
    }

    public static void getPropertyValue() throws Exception {
        FileInputStream lFStream = null;
        try {
            lFStream = new FileInputStream(commonConfigFile);
            lDbProps.load(lFStream);
            userID = KnCommonVaultUtil.getKeyFromVault(T10_USER_NAME_PATH, KnConstants.DBMGR_DBUSERID);
            password = KnCommonVaultUtil.getKeyFromVault(T10_PASSWORD_NAME_PATH, KnConstants.DBMGR_DBPASSWORD);
            ssl_flag = lDbProps.getProperty("IS_TIMESTEN_SSL_ENABLED");
            wallet = lDbProps.getProperty("TIMESTEN_SSL_CLIENTWALLET");
            encryption = lDbProps.getProperty("TIMESTEN_SSL_ENCRYPTIONTYPE");
            cipherSuites = lDbProps.getProperty("TIMESTEN_SSL_CIPHERSUITES");
            clientAuthentication = lDbProps.getProperty("TIMESTEN_SSL_CLIENTAUTH");
            localIPAddress = System.getenv("LOCAL_IP_ADDRESS");
            pttServerId = System.getenv("PTTSERVERID");
        } catch (Exception e) {
            e.printStackTrace();
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
