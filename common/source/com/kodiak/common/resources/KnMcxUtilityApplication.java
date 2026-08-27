/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.resources;

import com.kodiak.vault.KnCommonVaultUtil;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.spec.KeySpec;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import static com.kodiak.common.resources.KnConstants.T10_PASSWORD_NAME_PATH;
import static com.kodiak.common.resources.KnConstants.T10_USER_NAME_PATH;

/**
 * *****************************************************************************
 * File name:   KnMcxUtilityApplication
 * Subsystem:   Utility
 * Description: Mapping requested Groups to respective FANs given in the input file
 * <p/>
 * Name                                   Date           Release
 * -----------------                      -----------     -------
 *
 * @author kuppala sravan kumar           10/03/2021       -
 * <p/>
 * <p/>
 * *******************************************************************************
 */


public class KnMcxUtilityApplication {

    static Integer lineCount = 1;
    static Boolean isValidationFailed = Boolean.FALSE;
    static Boolean isProcessFailure = Boolean.FALSE;
    private static String ssl_flag=null;
    private static String wallet = null;
    private static String encryption = null;
    private static String cipherSuites = null;
    private static String clientAuthentication = null;
    private static String DB_URL = null;
    private static String localIPAddress = null;
    private static String pttServerId = null;

    private static String key = "PBKDF2WithHmacSHA256";
    private static String algo = "AES";
    private static String padding = "AES/CBC/PKCS5Padding";
    public static final String SALT = "X@#5";
    public static final String SECRETKEY = "A1B8D95BA9EA5440F252C3";
    public static final String IVSTRING = "0000000000000000";
    public static final Integer KEYSIZE = 256;
    public static final Integer ITERATION = 1000;
    private static String userID;
    private static String password;
    private static final String COMMONCONFIGFILE = "CommonConfig.properties";
    private static String commonConfigFile = "/DG/activeRelease/dat" + File.separator + COMMONCONFIGFILE;
    private static Properties commonConfigProps = new Properties();
    private static Properties lDbProps = new Properties();

    public static void getPropertyValue() throws Exception {
        FileInputStream lFStream = null;
        FileInputStream fileInputStream = null;
        {
            try {
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String args[]) throws ClassNotFoundException, SQLException {
        try {
            getPropertyValue();
            BufferedReader readFile = new BufferedReader(new FileReader("input.txt"));
            String line = readFile.readLine();
            while (null != line && !line.isEmpty()) {
                List<String> lineData = Arrays.stream(line.split(",")).collect(Collectors.toList());
                String groupId = lineData.get(0);
                System.out.println("Provided group Id : " + groupId);
                String action = lineData.get(1);
                System.out.println("Provided action : " + action);
                if (action.equals("1") || action.equals("2")) {
                    List<String> fannames = lineData.subList(2, lineData.size());
                    System.out.println("Provided fan names : " + fannames);
                    validateInput(groupId, action, fannames);
                } else {
                    System.out.println(" Wrong action parameter requested");
                    System.out.println("Validation failed, Exiting...");
                    System.exit(1);
                }
                line = readFile.readLine();

                if (isValidationFailed) {
                    isValidationFailed = Boolean.FALSE;
                    isProcessFailure = Boolean.TRUE;

                }

            }

            if (isProcessFailure) {
                System.out.println("Validation Failed Hence Exiting");
                System.exit(1);
            }


            BufferedReader readFile2 = new BufferedReader(new FileReader("input.txt"));
            String line2 = readFile2.readLine();
            while (null != line2 && !line2.isEmpty()) {
                List<String> lineData = Arrays.stream(line2.split(",")).collect(Collectors.toList());
                String groupId = lineData.get(0);
                System.out.println("Provided group Id : " + groupId);
                String action = lineData.get(1);
                System.out.println("Provided action : " + action);
                if (action.equals("1") || action.equals("2")) {
                    List<String> fannames = lineData.subList(2, lineData.size());
                    System.out.println("Provided fan names : " + fannames);
                    updateOrRemove(groupId, action, fannames);
                } else {
                    System.out.println(" Wrong action parameter requested");
                    System.out.println("Validation failed, Exiting...");
                    System.exit(1);
                }
                line2 = readFile2.readLine();

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (isProcessFailure) {
            System.exit(1);
        }

    }

    private static void updateOrRemove(String groupId, String action, List<String> fannames)
            throws ClassNotFoundException, SQLException {
        System.out.println("Entering into Update/Remove Operations");
        String localDBUid = "";
        String localDBPwd = "";
        Connection conn = null;
        PreparedStatement stmt2 = null;

        String QRY_INSERT_GROUP_HIERARCHY = "INSERT INTO DG.GROUP_HIERARCHY_MAP (ID_TYPE,CORPGROUPID,ID_VALUE) VALUES(3, ";


        String QRY_DELETE_GROUP_HIERARCHY = "DELETE DG.GROUP_HIERARCHY_MAP where ";
        //Changes to allow all group types XDM-8035
        String QRY_SELECT_GROUP_INFO = "SELECT CORPGROUPID,GROUPNAME,CORPID,IS_LARGEGROUP FROM DG.CORPGROUPINFO where CORPGROUPID";

        String QRY_SELECT_FAN_DETAILSByFANNAME = "SELECT FAN_ID,EXTERNAL_FAN_ID,SEGMENT_INDICATOR,CORPID,FAN_NAME FROM DG.FAN_DETAILS  where EXTERNAL_FAN_ID";

        String QRY_SELECT_GROUP_HIERARCHY = "SELECT CORPGROUPID,ID_TYPE,ID_VALUE FROM DG.GROUP_HIERARCHY_MAP where ID_TYPE=2";

        String QRY_SELECT_GROUP_HIERARCHY_FOR_DELETECHECK = "SELECT CORPGROUPID,ID_TYPE,ID_VALUE FROM DG.GROUP_HIERARCHY_MAP where CORPGROUPID='";

        String QRY_SELECT_GROUP_PROFILE = "SELECT USER_PROFILEID FROM DG.PROFILE_GROUP_INFO where GROUPID=' ";
        String QRY_SELECT_GROUP_PROFILE_HIERARCHY = "SELECT USERPROFILEID,ID_VALUE FROM  DG.USERPROFILE_HIERARCHY_MAP where USERPROFILEID='";
        String QRY_SELECT_CORPGROUPMEMBERLIST = "SELECT CORPGROUPID FROM DG.CORPGROUPMEMBERLIST where CORPGROUPID='";

        localIPAddress = System.getenv("LOCAL_IP_ADDRESS");
        pttServerId = System.getenv("PTTSERVERID");
        //System.out.println("localIPAddress:" + localIPAddress + " PTTserverId:" + pttServerId);

        Class.forName("com.timesten.jdbc.TimesTenDriver");

		/*final String DB_URL = "jdbc:timesten:client:TTC_Server=" + localIPAddress + ";TTC_Server_DSN=DG_" + pttServerId
				+ "_6;TCP_PORT=53389";
		conn = DriverManager.getConnection(DB_URL, localDBUid, localDBPwd);*/
        //final String DB_URL = "jdbc:timesten:client:TTC_Server=" + localIPAddress + ";TTC_Server_DSN=DG_" + pttServerId + "_6;TCP_PORT=53389";
        DB_URL= getConnectionURL();
        try {
            conn = DriverManager.getConnection(DB_URL, userID ,password);
        } catch (Exception e) {
            System.out.println("Connection failed..Please check the connection details");
            e.getMessage();
            e.getLocalizedMessage();
            System.out.println("e.getMessage():" + e.getMessage()+" e.getLocalizedMessage():"+e.getLocalizedMessage());
        }
        conn = DriverManager.getConnection(DB_URL, userID ,password);
        stmt2 = conn.prepareStatement(QRY_SELECT_GROUP_INFO + " IN" + "('" + groupId + "')");

        ResultSet rs2 = stmt2.executeQuery();
        Map<String, String> dbGroupNames = new HashMap<>();
        String corpGroupId = null;
        while (rs2.next()) {
            dbGroupNames.put(rs2.getString("CORPID"), rs2.getString("CORPGROUPID"));
            corpGroupId = rs2.getString("CORPGROUPID");

        }

        String query = QRY_SELECT_FAN_DETAILSByFANNAME + " IN " + convertListToStringBuffer(fannames).toString();

        PreparedStatement stmt4 = conn.prepareStatement(query);
        ResultSet rs4 = stmt4.executeQuery();
        List<String> fnFans = new ArrayList<>();
        Map<String, String> fanMap = new HashMap<>();
        while (rs4.next()) {

            fnFans.add(rs4.getString("FAN_ID"));
            fanMap.put(rs4.getString("FAN_ID"), rs4.getString("EXTERNAL_FAN_ID"));
        }

        String checkProfile = QRY_SELECT_GROUP_PROFILE + corpGroupId + "'";
        PreparedStatement stmtUserPrCheck = conn.prepareStatement(checkProfile);
        ResultSet checkUpro = stmtUserPrCheck.executeQuery();
        List<String> userProfileIdList = new ArrayList<>();
        while (checkUpro.next()) {
            userProfileIdList.add(checkUpro.getString("USER_PROFILEID"));
        }
        /*if (action.equals("2")){
        validateUserProfileIds(corpGroupId,fanMap);
        }*/
        //with the list of user profile ids


        if (action.equals("1")) {
            System.out.println("Entering into Update Operation as action is 1");
            for (String fan : fnFans) {
                String querySelect = QRY_SELECT_GROUP_HIERARCHY + " AND CORPGROUPID='" + corpGroupId
                        + "'" + "AND ID_VALUE='" + fan + "'";
                PreparedStatement stmt5 = conn.prepareStatement(querySelect);
                ResultSet rs = stmt5.executeQuery();
                if (!rs.next()) {
                    String queryinsert = QRY_INSERT_GROUP_HIERARCHY + "'" + corpGroupId + "','" + fan
                            + "')";
                    PreparedStatement stmt6 = conn.prepareStatement(queryinsert);
                    stmt6.executeQuery();

                }
            }
        } else if (action.equals("2")) {
            System.out.println("Entering into remove Operations as action is 2");


            String deleteCheck = QRY_SELECT_GROUP_HIERARCHY_FOR_DELETECHECK + corpGroupId + "'";
            String memeberListCheck = QRY_SELECT_CORPGROUPMEMBERLIST + corpGroupId + "'";

            PreparedStatement stmtmemeberListCheck = conn.prepareStatement(memeberListCheck);
            ResultSet checkstmtmemeberListCheck = stmtmemeberListCheck.executeQuery();
            Set<String> stmtmemeberListCheckSet = new HashSet<>();
            while (checkstmtmemeberListCheck.next()) {
                stmtmemeberListCheckSet.add(checkstmtmemeberListCheck.getString("CORPGROUPID"));
            }
            for (String fan : fnFans) {

                //String querySelctGPH = QRY_SELECT_GROUP_PROFILE_HIERARCHY + userProfileId + "'" + "AND ID_VALUE='" + fan + "'";

                //PreparedStatement stmtGPHCheck = conn.prepareStatement(querySelctGPH);
                // ResultSet checkGPH = stmtGPHCheck.executeQuery();
                   /* Set<String> gphValuesMapped = new HashSet<>();
                    while (checkGPH.next()) {
                        gphValuesMapped.add(checkGPH.getString("ID_VALUE"));
                    }*/
                    /*isHetheOwnerOfTheGroup(corpGroupId,fan);
                    isMemberExistInGroup(corpGroupId,fan);*/
                PreparedStatement stmtDeleteCheck = conn.prepareStatement(deleteCheck);
                ResultSet check = stmtDeleteCheck.executeQuery();
                Set<String> idValuesMapped = new HashSet<>();
                while (check.next()) {
                    idValuesMapped.add(check.getString("ID_VALUE"));
                }
                if (!idValuesMapped.isEmpty()) {
                    if (idValuesMapped.size() > 1) {
                        String querydelete = QRY_DELETE_GROUP_HIERARCHY + "CORPGROUPID ='" + corpGroupId
                                + "' AND ID_VALUE='" + fan + "'";
                        PreparedStatement stmt5 = conn.prepareStatement(querydelete);
                        stmt5.executeQuery();
                    } else {
                        System.out.println("Deletion Failed..Minimum one FAN ID : " + fanMap.get(fan) + " should be mapped to group");

                    }
                } else {
                    System.out.println("Deletion Failed..No FanIds are mapped to group");
                }
            }
        }
        conn.close();

    }


    private static void validateInput(String groupId, String action, List<String> fannames)
            throws SQLException, ClassNotFoundException {

        String localDBUid = "";
        String localDBPwd = "";
        Connection conn = null;
        PreparedStatement stmt = null;
        PreparedStatement stmt2 = null;
        ResultSet rs = null;
        String QRY_SELECT_GROUP_HIERARCHYFORFANID = "SELECT CORPGROUPID,ID_TYPE,ID_VALUE FROM DG.GROUP_HIERARCHY_MAP where ID_TYPE=2 AND CORPGROUPID";
        //Changes to allow all group types XDM-8035
        String QRY_SELECT_GROUP_INFO = "SELECT CORPGROUPID,CORPID,GROUPNAME,IS_LARGEGROUP FROM DG.CORPGROUPINFO where CORPGROUPID";
        String QRY_SELECT_FAN_DETAILSByFANID = "SELECT FAN_ID,EXTERNAL_FAN_ID,SEGMENT_INDICATOR,CORPID,FAN_NAME FROM DG.FAN_DETAILS  where SEGMENT_INDICATOR IS NOT NULL AND EXTERNAL_FAN_ID";
        String localIPAddress = System.getenv("LOCAL_IP_ADDRESS");
        String pttServerId = System.getenv("PTTSERVERID");
        // System.out.println("localIPAddress:" + localIPAddress + " PTTserverId:" + pttServerId);
        try {
            Class.forName("com.timesten.jdbc.TimesTenDriver");
        } catch (Exception e) {
            System.out.println();
            System.out.println();
            System.out.println("   ###### Please Run ./etc/kodiakDG.conf Before Running The Script ########");
            System.out.println();
            System.out.println();
        }

        //final String DB_URL = "jdbc:timesten:client:TTC_Server=" + localIPAddress + ";TTC_Server_DSN=DG_" + pttServerId
        //		+ "_6;TCP_PORT=53389";

        //final String DB_URL = "jdbc:timesten:client:TTC_Server=" + localIPAddress + ";TTC_Server_DSN=DG_" + pttServerId + "_6;TCP_PORT=53389";
        DB_URL= getConnectionURL();
        conn = DriverManager.getConnection(DB_URL,userID ,password);
        // Validation 2 started

        stmt2 = conn.prepareStatement(QRY_SELECT_GROUP_INFO + " IN" + "('" + groupId + "')");

        ResultSet rs2 = stmt2.executeQuery();
        Map<String, String> coprgroupMap = new HashMap<>();
        String corpGroupId = null;
        int isLargeGroup = 0;
        while (rs2.next()) {
            String dbGroupCORPid = rs2.getString("CORPID");
            corpGroupId = rs2.getString("CORPGROUPID");
            isLargeGroup = rs2.getInt("IS_LARGEGROUP");
            coprgroupMap.put(dbGroupCORPid, corpGroupId);

        }
        if (coprgroupMap.isEmpty()) {
            System.out.println("Group does not exist in the DB :" + groupId);
            System.out.println("Validation failed, Exiting...");
            System.exit(1);
        }
        System.out.println("corpGroupId:" + corpGroupId + " " + "isLargeGroup from the db:" + isLargeGroup);
        stmt = conn.prepareStatement(QRY_SELECT_FAN_DETAILSByFANID + " IN" + convertListToStringBuffer(fannames));

        rs = stmt.executeQuery();
        List<String> dbFanNames = new ArrayList<>();
        List<Integer> fanIds = new ArrayList<>();
        Map<Integer, String> fanIdExtid = new HashMap<>();
        while (rs.next()) {
            dbFanNames.add(rs.getString("EXTERNAL_FAN_ID"));
            fanIds.add(rs.getInt("FAN_ID"));
            fanIdExtid.put(rs.getInt("FAN_ID"), rs.getString("EXTERNAL_FAN_ID"));

        }

        // Validation1
        //XDM-8448 changes
        if (isLargeGroup == 2) {
            if (dbFanNames.containsAll(fannames)) {
                System.out.println("FanNames are validated..all are firstNetFans");
            } else {
                for (String name : fannames) {
                    if (!dbFanNames.contains(name)) {
                        System.out.println(" fan Id: " + name + " is not the firstNetFAN ");
                    }
                }
                System.out.println("Validation failed, Exiting...");
                System.exit(1);
            }
        }
        System.out.println("Data validations are done.. Entering to Buisiness validations");

        List<Integer> fanId = validateUserProfileIds(corpGroupId, fanIds);
        if (fanId != null && !fanId.isEmpty()) {
            System.out.println("Business validation got failed");
            System.out.println(groupId + " is assigned to a user profile(s) in " + getExternalList(fanIdExtid, fanId));
            isValidationFailed = Boolean.TRUE;
        }
        List<Integer> isMemberExistfanId = isMemberExistInGroup(corpGroupId, fanIds);
        if (isMemberExistfanId != null && !isMemberExistfanId.isEmpty()) {
            System.out.println("Business validation got failed");
            System.out.println("In " + getExternalList(fanIdExtid, isMemberExistfanId) + " members are assigned to " + groupId);
            isValidationFailed = Boolean.TRUE;
        }
       /* Integer isTheOwner = isHetheOwnerOfTheGroup(corpGroupId,fanIds);
        if(isTheOwner != null){
            System.out.println("Business validation got failed");
            System.out.println(groupName+" is created by "+fanIdExtid.get(isTheOwner)+" hence cannot be removed");
            isValidationFailed=Boolean.TRUE;

        }*/
        System.out.println("Buisiness validations for Line Number:" + lineCount + " is done");
        lineCount++;
        System.out.println();


        conn.close();
    }

    public static StringBuffer convertListToStringBuffer(Collection arrayList) {
        StringBuffer buffer = new StringBuffer(200);
        buffer.setLength(0);
        if (!(arrayList == null || arrayList.isEmpty())) {
            buffer.append(" ('");
            for (Object anArrayList : arrayList) {
                buffer.append((String) anArrayList).append("', '");
            }
            int len = buffer.length();
            buffer.delete(len - 4, len);
            buffer.append("') ");
        } else {
            buffer.append("( )");

        }

        return buffer;
    }

    private static List<Integer> validateUserProfileIds(String groupId, List<Integer> fanList) throws SQLException, ClassNotFoundException {
        String localIPAddress = System.getenv("LOCAL_IP_ADDRESS");
        String pttServerId = System.getenv("PTTSERVERID");
        // System.out.println("localIPAddress:" + localIPAddress + " PTTserverId:" + pttServerId);

        Class.forName("com.timesten.jdbc.TimesTenDriver");

        //final String DB_URL = "jdbc:timesten:client:TTC_Server=" + localIPAddress + ";TTC_Server_DSN=DG_" + pttServerId
        //		+ "_6;TCP_PORT=53389";
        //final String DB_URL = "jdbc:timesten:client:TTC_Server=" + localIPAddress + ";TTC_Server_DSN=DG_" + pttServerId + "_6;TCP_PORT=53389";
        DB_URL = getConnectionURL();
        Connection conn = DriverManager.getConnection(DB_URL, userID ,password);
        Boolean isValid = Boolean.TRUE;
        ResultSet rs;
        List<Integer> idValuesList = new ArrayList<>();

        String idQuery = "select distinct ID_Value from DG.USERPROFILE_HIERARCHY_MAP where USERPROFILEID IN(SELECT USER_PROFILEID FROM DG.PROFILE_GROUP_INFO where GROUPID=" + groupId + ")";

        PreparedStatement query = conn.prepareStatement(idQuery);
        rs = query.executeQuery();
        while (rs.next()) {
            idValuesList.add(rs.getInt("ID_VALUE"));
        }
        List<Integer> invalidFanids = new ArrayList<>();
        // we got the list of idvalues with idvalues check wheather the
        for (Integer fanId : fanList) {
            if (idValuesList.contains(fanId)) {
                invalidFanids.add(fanId);
            }

        }
        return invalidFanids;

    }

    private static List<Integer> isMemberExistInGroup(String corpGroupId, List<Integer> fanids) throws SQLException, ClassNotFoundException {
        String localIPAddress = System.getenv("LOCAL_IP_ADDRESS");
        String pttServerId = System.getenv("PTTSERVERID");
        // System.out.println("localIPAddress:" + localIPAddress + " PTTserverId:" + pttServerId);

        Class.forName("com.timesten.jdbc.TimesTenDriver");

        //final String DB_URL = "jdbc:timesten:client:TTC_Server=" + localIPAddress + ";TTC_Server_DSN=DG_" + pttServerId
        //		+ "_6;TCP_PORT=53389";
        //final String DB_URL = "jdbc:timesten:client:TTC_Server=" + localIPAddress + ";TTC_Server_DSN=DG_" + pttServerId + "_6;TCP_PORT=53389";
        DB_URL = getConnectionURL();
        Connection conn = DriverManager.getConnection(DB_URL, userID ,password);
        Boolean isValid = Boolean.TRUE;
        List<String> mdnList = new ArrayList<>();
        Integer groupid = Integer.parseInt(corpGroupId);
        String idQuery = "SELECT MEMBERMDN FROM DG.CORPGROUPMEMBERLIST WHERE CORPGROUPID= ?";
        ResultSet rs;
        PreparedStatement pStmt = conn.prepareStatement(idQuery);
        pStmt.setInt(1, groupid);
        rs = pStmt.executeQuery();
        while (rs.next()) {
            mdnList.add(rs.getString("MEMBERMDN"));
        }
        List<Integer> invalidFanids = new ArrayList<>();
        //select the fanIds based on the mdns
        if (!mdnList.isEmpty()) {
            String listOfMdns = mdnList.stream()
                    .collect(Collectors.joining(","));
            String fanQuery = "SELECT FAN_ID FROM DG.SUBSCRIBER_ADDLINFO WHERE MDN IN " + "(" + listOfMdns + ")";
            PreparedStatement pStmt2 = conn.prepareStatement(fanQuery);
            rs = pStmt2.executeQuery();
            while (rs.next()) {
                Integer fan = rs.getInt("FAN_ID");
                if (fanids.contains(fan)) {
                    invalidFanids.add(fan);
                }
            }
        }
        return invalidFanids;

    }

    private static Integer isHetheOwnerOfTheGroup(String corpGroupId, List<Integer> fanids) throws SQLException, ClassNotFoundException {
        String localIPAddress = System.getenv("LOCAL_IP_ADDRESS");
        String pttServerId = System.getenv("PTTSERVERID");
        // System.out.println("localIPAddress:" + localIPAddress + " PTTserverId:" + pttServerId);

        Class.forName("com.timesten.jdbc.TimesTenDriver");

        //final String DB_URL = "jdbc:timesten:client:TTC_Server=" + localIPAddress + ";TTC_Server_DSN=DG_" + pttServerId
        //		+ "_6;TCP_PORT=53389";
        //final String DB_URL = "jdbc:timesten:client:TTC_Server=" + localIPAddress + ";TTC_Server_DSN=DG_" + pttServerId + "_6;TCP_PORT=53389";
        DB_URL = getConnectionURL();
        Connection conn = DriverManager.getConnection(DB_URL, userID ,password);
        Boolean isValid = Boolean.TRUE;
        List<String> mdnList = new ArrayList<>();
        Integer groupid = Integer.parseInt(corpGroupId);
        Integer groupsCorpid = null;
        Map<Integer, Integer> fansCorpids = new HashMap<>();
        String groupCorpidQuery = "SELECT CORPID FROM DG.CORPGROUPINFO WHERE CORPGROUPID= ?";
        ResultSet rs;
        PreparedStatement pStmt = conn.prepareStatement(groupCorpidQuery);
        pStmt.setInt(1, groupid);
        rs = pStmt.executeQuery();
        while (rs.next()) {
            groupsCorpid = (rs.getInt("CORPID"));
        }
        String listOfFanIds = fanids.stream().map(String::valueOf).collect(Collectors.joining(","));
        String fansCorpidQuery = "SELECT DISTINCT FAN_ID,CORPID FROM DG.FAN_DETAILS WHERE FAN_ID IN " + "(" + listOfFanIds + ")";
        pStmt = conn.prepareStatement(fansCorpidQuery);
        rs = pStmt.executeQuery();
        while (rs.next()) {
            /*System.out.println(rs.getInt("CORPID"));
            System.out.println(rs.getInt("FAN_ID"));*/
            fansCorpids.put(rs.getInt("CORPID"), rs.getInt("FAN_ID"));
        }
        //checking wheather the fan is creater of the group or not
        if (fansCorpids.containsKey(groupsCorpid)) {
            return fansCorpids.get(groupsCorpid);

        }
        return null;
    }

    private static List<String> getExternalList(Map<Integer, String> extMap, List<Integer> fanids) {

        List<String> result = new ArrayList<>();
        for (Integer fanid : fanids) {
            result.add(extMap.get(fanid));
        }
        return result;
    }

    private static String getConnectionURL() {
        System.out.println("Values from common file " + ssl_flag + wallet +
                encryption + cipherSuites + clientAuthentication);

        if (ssl_flag.equals("1")) {
            System.out.println("SSL flag is enabled");
            DB_URL = "jdbc:timesten:client:TTC_Server=" + localIPAddress + ";TTC_Server_DSN=DG_"
                    + pttServerId + "_6;TCP_PORT=53389"
                    + ";Wallet=" + wallet
                    + ";Encryption=" + encryption
                    + ";CipherSuites=" + cipherSuites
                    + ";SSLClientAuthentication=" + clientAuthentication;
        } else {
            System.out.println("SSL flag is disabled ");
            DB_URL = "jdbc:timesten:client:TTC_Server=" + localIPAddress +
                    ";TTC_Server_DSN=DG_" + pttServerId + "_6;TCP_PORT=53389";
        }
        return DB_URL;
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