/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.resources;

import com.kodiak.common.commdto.common.KnKsekVaultInfo;
import com.kodiak.common.commdto.common.KnUserAgentDTO;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnDBConfigInfo;
import com.kodiak.common.dao.KnDbUtil;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.dto.KnPayloadIP;
import com.kodiak.common.exception.KnException;
import com.kodiak.common.ggcache.dto.KnAsyncJobTaskDTO;
import com.kodiak.logger.KnLogger;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kodiak.vault.KnCommonVaultUtil;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import static com.kodiak.common.resources.KnConstants.MCPTT_CONFIG_MGMT_SCOPE_OIDC;
import static com.kodiak.common.resources.KnConstants.MCPTT_DATA_CONFIG_MGMT_SCOPE_OIDC;
import static com.kodiak.common.resources.KnConstants.MCPTT_DATA_GROUP_MGMT_SCOPE_OIDC;
import static com.kodiak.common.resources.KnConstants.MCPTT_DATA_KEY_MGMT_SCOPE_OIDC;
import static com.kodiak.common.resources.KnConstants.MCPTT_DATA_SCOPE_OIDC;
import static com.kodiak.common.resources.KnConstants.MCPTT_GROUP_MGMT_SCOPE_OIDC;
import static com.kodiak.common.resources.KnConstants.MCPTT_KEY_MGMT_SCOPE_OIDC;
import static com.kodiak.common.resources.KnConstants.MCPTT_SERVICE_SCOPE_OIDC;
import static com.kodiak.common.resources.KnConstants.MCPTT_VIDEO_CONFIG_MGMT_SCOPE_OIDC;
import static com.kodiak.common.resources.KnConstants.MCPTT_VIDEO_GROUP_MGMT_SCOPE_OIDC;
import static com.kodiak.common.resources.KnConstants.MCPTT_VIDEO_KEY_MGMT_SCOPE_OIDC;
import static com.kodiak.common.resources.KnConstants.MCPTT_VIDEO_SCOPE_OIDC;
import static com.kodiak.common.resources.KnConstants.*;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.*;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class KnGeneralUtil {
    private static final KnLogger knLogger = KnLogger.getLogger(KnGeneralUtil.class);

    private static KnConstants.SIGCARD_TYPE installServiceName = null;
    private static Map<String, String> xdmServerPttServerIdMap = new HashMap<String, String>();
    private static int GENERATE_ACT_CODE_MAX_SUBS_LIMIT = 0;
    private static final String COMMA = ",";
    private static final String SEMI_COLON = ";";
    private static final String SINGLE_QUOTE = "'";
    private static final String EMPTY_STRING = "";
    private static final String CLUSTERID_ENV_NAME="CLUSTERID";
    private static Map<Integer, Integer> hirarchyInstallFlagMap = null;
    private static Map<Integer, String> countryCodeMap = null;
    private static Map<Integer, List<Integer>> subscriptionTypeMap = null;
    private static Map<Integer, Map<String,String>> swpkgConfigMap = null;
    private static Properties prop = null;
    private static final String  SELECT_CC_SWPKGCONFIG = "SELECT distinct SWPKGID, COUNTRYCODE FROM DG.SWPKGCONFIGPARAMVALUE WHERE   PTTSERVERID=?";
    private static final String  SELECT_SWPKGCONFIGPARAMVALUE = "SELECT SWPKGID, PARAMVALUE FROM DG.SWPKGCONFIGPARAMVALUE WHERE   PTTSERVERID=? AND PARAMNAME= ?";
    private static boolean isFeatureBitUpdateComplete = false;
    private static String vaultQueryFqdn = null;
    private static KnKsekVaultInfo ksekVaultInfo =null;
    private static String ENABLED = "1";
    public static final int ONLY_ETAG_UPDATE = 2;

    public static KnConstants.SIGCARD_TYPE getSoftwareInstalled() throws KnDAOException {
        String methodName = "getSoftwareInstalled()";
        Connection conn = null;
        if (installServiceName == null) {
            //fetch local pttserverid from dbmgr properties
            KnDBConfigInfo dbConfigInfo = KnDbUtil.getDBConfigInfo();
            String localPttId = dbConfigInfo.getLocalPttId();
            String localIpAdd = System.getenv(KnConstants.LOCAL_IP_ADDRESS);
            //execute db query to fetch singalingcardtype
            int cardType = 0;
            PreparedStatement pStmt = null;
            ResultSet resultSet = null;
            KnPersisterTxn persisterTxn = KnPersisterTxn.getPersisterTxn();
            knLogger.info(methodName, "ENTRY: Fetching Software Info.");
            try {

                String sqlQuery = "SELECT SIGNALINGCARDTYPE FROM DG.SIGNALINGCARDADDLINFO WHERE " +
                        "SIGNALINGCARDID = (SELECT SIGNALINGCARDID FROM DG.SIGNALINGCARDINFO WHERE IPADDRESS = ?)";


                knLogger.debug(methodName, "Opening the Transaction");
                persisterTxn.open();

                conn = persisterTxn.getDBConnection(localPttId, true);
                pStmt = conn.prepareStatement(sqlQuery);

                knLogger.debug(methodName, "Executing the query - ", sqlQuery);
                pStmt.setString(1, localIpAdd);
                resultSet = pStmt.executeQuery();
                knLogger.debug(methodName, "Executed the query - ");

                if (resultSet.next()) {
                    cardType = resultSet.getInt(1);
                }
                persisterTxn.save();
            } catch (KnDAOException e) {
                knLogger.error(methodName, "DAO exception - ", e);
                persisterTxn.rollback();
                throw e;
            } catch (Exception e) {
                knLogger.error(methodName, "Exception e ", e);
                persisterTxn.rollback();
                throw KnDbUtil.processException(e, "Exception occurred ", localPttId, "SIGNALINGCARDINFO", null);
            } finally {
                KnDbUtil.closeResultSet(resultSet);
                KnDbUtil.closeStatement(pStmt);
            }
            installServiceName = KnConstants.SIGCARD_TYPE.validate(cardType);
            knLogger.info(methodName, "EXIT: Software Installed - ", installServiceName);
        }

        return installServiceName;
    }

    /**
     *This will get the hierarchy  as enum which can be varifed against the package Id.
     * @param softwarePkg
     * @return
     * @throws KnDAOException
     */
    public static KnConstants.HIERARCHY_TYPE getHierarchyInterfaceType(KnConstants.SOFTWARE_PKG softwarePkg) throws KnDAOException {
        String methodName = "getHierarchyInterfaceType(KnConstants.SOFTWARE_PKG softwarePkg)";
        int hirarchy = 0;
        InputStream inputStream;
        KnConstants.HIERARCHY_TYPE enumHirarchyType = null;
        knLogger.info(methodName, "Hierarchy for software Package - ", softwarePkg.name());
        try {
            if (prop == null) {
                prop = new Properties();
                inputStream = new FileInputStream(System.getProperty(KnConstants.HIERARCHY_DIR_NAME) + File.separator + KnConstants.HIERARCHY_PROPS_FILE_NAME);
                prop.load(inputStream);
                knLogger.info(methodName, "design properties :", prop);
            }
        } catch (IOException e) {
            knLogger.warn(methodName, "ioException occurred - ", e);
        }

        if (hirarchyInstallFlagMap == null) {
            hirarchyInstallFlagMap = retrieveHierarchyInterface();
            knLogger.info(methodName, " retrieved from DB .... ", hirarchyInstallFlagMap);
        }
        if (hirarchyInstallFlagMap.get(softwarePkg.value()) == null) {
            knLogger.info(methodName, "Software pkg not configured..taking default as non-hierarchy");
            hirarchy = KnConstants.HIERARCHY_TYPE.NON_HIERARCHY.value();
            enumHirarchyType = KnConstants.HIERARCHY_TYPE.validate(hirarchy);
        } else {
            hirarchy = hirarchyInstallFlagMap.get(softwarePkg.value());
            knLogger.debug(methodName, " hierarchy fetched - ", hirarchy);
            enumHirarchyType = KnConstants.HIERARCHY_TYPE.validate(hirarchy);
        }
        if (prop != null && (Boolean.parseBoolean(prop.getProperty(KnConstants.HIERARCHY_PROPS)))) {
            hirarchyInstallFlagMap = null;
        }
        knLogger.info(methodName, "Exit: hierarchy flag  - ", enumHirarchyType);
        return enumHirarchyType;
    }

    /**
     * This method will return the value of the hirarchy flag  from the software package .
     * i.e say if SOAP  request handler package installed, then we are fetching the value of hirarchy flag
     * @return
     * @throws KnDAOException
     */
    private static Map<Integer, Integer> retrieveHierarchyInterface() throws KnDAOException {
        String methodName = "retrieveHierarchyInterface()";
        knLogger.info(methodName, "retrieving Webip, PARAM Value for PARAM Name ");
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String localPttId = KnDbUtil.getDBConfigInfo().getLocalPttId();
        KnPersisterTxn persisterTxn = null;
        try {
            persisterTxn = KnPersisterTxn.getPersisterTxn();
            persisterTxn.open();
            hirarchyInstallFlagMap = new HashMap<Integer,Integer>();
            conn = persisterTxn.getDBConnection(localPttId, false);
            pStmt = conn.prepareStatement(SELECT_SWPKGCONFIGPARAMVALUE);
            pStmt.setString(1, localPttId);
            pStmt.setString(2,"CORP_HIERARCHY");
            knLogger.info(methodName, "QUERY: Executing - ", SELECT_SWPKGCONFIGPARAMVALUE);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "QUERY: Executed - ");

            while (rs.next()) {
                //storing the values if we need to play around with the  paramvalue.
                int swPkgId = rs.getInt("SWPKGID");
                String webSvcParamValue=rs.getString("PARAMVALUE");
                int webSvcParamIValue= Integer.parseInt(webSvcParamValue);
                knLogger.debug(methodName," SWPKGID :",swPkgId, " webSvcParamIValue :",webSvcParamIValue);
                hirarchyInstallFlagMap.put(swPkgId,webSvcParamIValue);
             }
            persisterTxn.save();
        } catch (Exception e) {
            KnDbUtil.rollback(persisterTxn);
            knLogger.error(methodName, "Exception occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", localPttId, "DG.SWPKGCONFIGPARAMVALUE", null);

        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
        }
        knLogger.info(methodName, "Returning value - ", hirarchyInstallFlagMap);
        return hirarchyInstallFlagMap;
    }

    public static void insertIntoAsyncJobTaskUtil(String corpId) throws KnDAOException {
        KnAsyncJobTaskDTO jobTaskDTO = new KnAsyncJobTaskDTO();
        KnGeneralCacheUtil cacheUtil = new KnGeneralCacheUtil();
        jobTaskDTO.setOperationType(KnConstants.LOCATION_ENABLED_FEATURE.LOCATION_ENABLED_JOB.value());
        jobTaskDTO.setCorpId(Integer.parseInt(corpId));
        jobTaskDTO.setTaskId(String.valueOf(System.nanoTime()));
        jobTaskDTO.setStartTimeStamp(String.valueOf(Instant.now().toEpochMilli()));
        jobTaskDTO.setStatus(KnConstants.LOCATION_ENABLED_FEATURE.LOCATION_ENABLED_JOB.value());
        jobTaskDTO.setTaskType(KnConstants.LOCATION_ENABLED_TASK_TYPE.ADDLOCATIONENABLED.Value());
        cacheUtil.createAsyncJobTask(jobTaskDTO);
    }

    public static String getDefaultXDMCorpFS2Set(int bitPosition, boolean value) {
        BitSet bitSet = new BitSet();
        bitSet.set(bitPosition,value);
        String hexString = convertBitSetToHexString(bitSet);
        knLogger.debug("hexString::",hexString);
        return hexString;
    }

    /**
     * This method retgrieves the configurations from DG.RTXENVVARIABLEINFO for a given list of keys
     *
     * @param keyList the list of keys
     * @return the configMap
     * @throws KnDAOException exception
     */
    public Map<String, String> retrieveConfig(Collection<String> keyList) throws KnDAOException {
        String methodName = "retrieveCustomConfig(Collection<String>)";
        knLogger.debug(methodName, "retrieving Custom Config info - ");

        //retrieve the configuration for the given keyList
        KnPersisterTxn persisterTxn = KnPersisterTxn.getPersisterTxn();
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String localPttId = KnDbUtil.getDBConfigInfo().getLocalPttId();
        Map<String, String> configMap = new HashMap<String, String>();
        try {
            persisterTxn.open();
            StringBuffer keys = KnDbUtil.convertListToStringBuffer(keyList);
            String query = "SELECT PARAMNAME, PARAMVALUE FROM DG.RTXENVVARIABLEINFO WHERE PARAMNAME IN " + keys.toString();

            conn = persisterTxn.getDBConnection(localPttId, true);
            pStmt = conn.prepareStatement(query);

            knLogger.info(methodName, "QUERY: Executing - ", query);
            rs = pStmt.executeQuery();
            knLogger.info(methodName, "QUERY: Executed - ");

            while (rs.next()) {
                configMap.put(rs.getString("PARAMNAME"), rs.getString("PARAMVALUE"));
            }
            persisterTxn.save();

        } catch (KnDAOException e) {
            persisterTxn.rollback();
            knLogger.error(methodName, "KnDAOException occurred - ", e);

        } catch (SQLException e) {
            persisterTxn.rollback();
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", localPttId, "RTXENVVARIABLEINFO", null);

        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
        }
        knLogger.debug(methodName, "Returning configMap - ", configMap);
        return configMap;
    }

    public Map<String, String> retrieveMSCommonConfig(Collection<String> keyList) throws KnDAOException {
        String methodName = "retrieveMSCommonConfig(Collection<String> keyList)";
        knLogger.info(methodName, "retrieving MicroServices Common  Config info - ");

        //retrieve the configuration for the given keyList
        KnPersisterTxn persisterTxn = KnPersisterTxn.getPersisterTxn();
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String localPttId = KnDbUtil.getDBConfigInfo().getLocalPttId();
        Map<String, String> msConfigMap = new HashMap<String, String>();
        try {
            persisterTxn.open();
            StringBuffer keys = KnDbUtil.convertListToStringBuffer(keyList);
            String query = "SELECT PARAMNAME,PARAMVALUE from DG.MICROSVCS_COMMONCONFIG where CLUSTERID=? AND PARAMNAME IN " + keys.toString();
            int clusterId = Integer.parseInt(System.getenv(CLUSTERID_ENV_NAME));
            conn = persisterTxn.getDBConnection(localPttId, false);
            pStmt = conn.prepareStatement(query);
            pStmt.setInt(1,clusterId);
            knLogger.info(methodName, "QUERY: Executing - ", query);
            rs = pStmt.executeQuery();
            knLogger.info(methodName, "QUERY: Executed - ");

            while (rs.next()) {
                msConfigMap.put(rs.getString("PARAMNAME"), rs.getString("PARAMVALUE"));
            }
            persisterTxn.save();

        } catch (Exception e) {
            persisterTxn.rollback();
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", localPttId, "MICROSVCS_COMMONCONFIG", null);

        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
        }
        knLogger.debug(methodName, "Returning MsConfigMap - ", msConfigMap);
        return msConfigMap;
    }



    /**
     * @param obj Object
     * @return byte[]
     * @throws KnException exception
     */
    public static byte[] toByteArray(Object obj) throws KnException {
        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            oos.close();
            bos.close();
            bytes = bos.toByteArray();
        } catch (IOException ex) {
            throw new KnException("CM1001", "Unexpected Exception while converting object.");
        }
        return bytes;
    }

    /**
     * This method retrieves the value from DG.WEBSVCCONFIGINFO table for the given param name.
     *
     * @param key
     * @param pkgId
     * @return
     * @throws KnDAOException
     */
    public String retrievePkgConfigParam(String key, int pkgId) throws KnDAOException {
        String methodName = "retrievePkgConfigParam(String, int)";
        knLogger.info(methodName, "retrieving PARAM Value for PARAM Name ", key, "and pkgId " +
                "", pkgId);

        KnPersisterTxn persisterTxn = KnPersisterTxn.getPersisterTxn();
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String localPttId = KnDbUtil.getDBConfigInfo().getLocalPttId();
        String value = null;
        try {
            persisterTxn.open();
            String query = "SELECT PARAMVALUE FROM DG.SWPKGCONFIGPARAMVALUE WHERE PTTSERVERID=? AND SWPKGID=? AND PARAMNAME=?;";
            conn = persisterTxn.getDBConnection(localPttId, false);
            pStmt = conn.prepareStatement(query);
            pStmt.setString(1, localPttId);
            pStmt.setInt(2, pkgId);
            pStmt.setString(3, key);

            knLogger.info(methodName, "QUERY: Executing - ", query);
            rs = pStmt.executeQuery();
            knLogger.info(methodName, "QUERY: Executed - ");

            if (rs.next()) {
                value = rs.getString(1);
            }
            persisterTxn.save();

        } catch (KnDAOException e) {
            persisterTxn.rollback();
            knLogger.error(methodName, "KnDAOException occurred - ", e);

        } catch (SQLException e) {
            persisterTxn.rollback();
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", localPttId, "DG.SWPKGCONFIGPARAMVALUE", null);

        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
        }
        knLogger.debug(methodName, "Returning value - ", value);
        return value;
    }

    public static KnUserAgentDTO getDetailsFromUserAgent(String userAgent) {
        String methodName = "getDetailsFromUserAgent";
        knLogger.info(methodName, "ENTRY: getDetailsFromUserAgent ", userAgent);
        KnUserAgentDTO userAgentDTO = new KnUserAgentDTO();

/*
sample
User-Agent: MCPTT-client/MCPTT2.0 Win32_UA/kn/Kodiak/KodiakWin32Test Win32/1.0 knpoc-08_001_01_01_25V/9.0 Android_POC_07_007_01_43 I747UCDJL3
User-Agent: PoC-Client/OMA2.0 Motorola/Droid Android/2.0 knpoc-7.0/1.0
User-Agent: PoC-Client/OMA2.0 TEST/kn/Motorola/Droid Android/2.0 knpoc-7.0/2.0 yyy uuuu(with device UA)
mn  manufacturer name
dn  device name
osn  os/framework name
osv  os/framework version
an  app name  knpoc
av  app version
pv  protocol version
uv  ui version
*/
        try {
            String preFix1 = "PoC-client/OMA2.0";
            String preFix2 = "PoC-Client/OMA2.0";
            String preFix3 = "MCPTT-client/MCPTT2.0";
            if (userAgent.contains(preFix1))
                userAgent = userAgent.substring(userAgent.indexOf(preFix1) + 17);
            else if (userAgent.contains(preFix2)) {
                userAgent = userAgent.substring(userAgent.indexOf(preFix2) + 17);

            }else if (userAgent.contains(preFix3)){
                userAgent = userAgent.substring(userAgent.indexOf(preFix3) + 21);
            }
            userAgent = userAgent.trim();
            String deviceUA = null;
            String del = "/kn/";
            if (userAgent.contains(del)) {
                deviceUA = userAgent.substring(0, userAgent.indexOf(del));
                userAgent = userAgent.substring(userAgent.indexOf(del) + 4);
            }

            userAgent = userAgent.trim();
            String[] temp = userAgent.split("\\s+");
            String[] temp1;

            userAgentDTO.setDeviceUA(deviceUA);
            if (temp.length > 0) {
                temp1 = temp[0].split("/");
                userAgentDTO.setManufactName(temp1[0]);
                userAgentDTO.setDeviceName(temp1[1]);
            }
            if (temp.length > 1) {
                temp1 = temp[1].split("/");
                userAgentDTO.setOsName(temp1[0]);
                userAgentDTO.setOsVersion(temp1[1]);
            }

            if (temp.length > 2) {
                temp1 = temp[2].split("/");
                userAgentDTO.setProtocolVersion(temp1[1]);
                String []appNameVer = temp1[0].split("-");
                userAgentDTO.setAppName(appNameVer[0]);
                if (appNameVer.length > 1) {
                    userAgentDTO.setAppVersion(appNameVer[1]);
                }
            }

            if (temp.length > 3) {
                userAgentDTO.setUiVersion(temp[3]);
            }

            if (temp.length > 4) {
                userAgentDTO.setHsBaseband(temp[4]);
            }

        } catch (Exception e) {
            knLogger.warn(methodName, "invalid userAgent - ", userAgent);
            userAgentDTO = null;
        }
        knLogger.debug(methodName, "Exit: getDetailsFromUserAgent  userAgentDTO:", userAgentDTO);
        return userAgentDTO;
    }

    /**
     * method to retrieve XDM server Ptt Server Id
     *
     * @return String XDM PttServerId
     * @throws KnDAOException
     */
    public static String getXDMServerPttServerId() throws KnDAOException {
        String methodName = "getXDMServerPttServerId";
        String xdmPttServerId = null;
        knLogger.debug(methodName, "ENTRY: get XDM PttServer Id");
        String localPttId = KnDbUtil.getDBConfigInfo().getLocalPttId();

        try {
            if (!xdmServerPttServerIdMap.containsKey(localPttId)) {
                xdmPttServerId = retrieveXDMServerPTTID(localPttId);
                xdmServerPttServerIdMap.put(localPttId, xdmPttServerId);
                knLogger.debug(methodName, "XDM PttServer Id from DB");
            } else {
                xdmPttServerId = xdmServerPttServerIdMap.get(localPttId);
                knLogger.debug(methodName, "XDM PttServer Id from Cache");
            }
        } catch (KnDAOException e) {
            knLogger.debug(methodName, "DAO Exception occurred - ", e);
            throw e;
        }

        knLogger.debug(methodName, "XDM pttServer Id retrieved - ", xdmPttServerId);
        return xdmPttServerId;
    }

    private static String retrieveXDMServerPTTID(String localPttId) throws KnDAOException {
        String methodName = "retrieveXDMServerPTTID";

        Connection conn = null;
        Statement stmt = null;
        ResultSet resultSet = null;
        KnPersisterTxn persisterTxn = null;
        String xdmPttServerId = null;

        String query = "SELECT PTTSERVERID FROM DG.JOINTSIGNALINGCARDINFO WHERE SIGNALINGCARDID=(SELECT " +
                "MIN(SIGNALINGCARDID) FROM DG.SIGNALINGCARDADDLINFO WHERE SIGNALINGCARDTYPE IN (36, 37)) AND PTTSERVERID IS NOT NULL ";

        knLogger.info(methodName, "ENTRY: retrieve XDMServer PTT ID");

        try {
            persisterTxn = KnPersisterTxn.getPersisterTxn();
            knLogger.debug(methodName, "Opening the Transaction");
            persisterTxn.open();

            conn = persisterTxn.getDBConnection(localPttId, true);
            stmt = conn.createStatement();

            knLogger.debug(methodName, "Executing the query - ", query);
            resultSet = stmt.executeQuery(query);
            knLogger.debug(methodName, "Executed the query - ");

            if (resultSet.next()) {
                xdmPttServerId = resultSet.getString("PTTSERVERID");
            }

            persisterTxn.save();
            return xdmPttServerId;

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO exception - ", e);
            persisterTxn.rollback();
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception e ", e);
            persisterTxn.rollback();
            throw KnDbUtil.processException(e, "Exception occurred ", localPttId, "SIGNALINGCARDINFO", null);
        } finally {
            KnDbUtil.closeResultSet(resultSet);
            KnDbUtil.closeStatement(stmt);
            knLogger.info(methodName, "EXIT: retrieve XDMServer PTTID ");
        }
    }

    public int getGenActCodMaxSubsLmt() throws KnDAOException {
        try {
            if (GENERATE_ACT_CODE_MAX_SUBS_LIMIT <= 0) {
                ArrayList<String> keyList = new ArrayList<String>(1);
                keyList.add(KnConstants.MAX_SUBS_ALLOWED_GEN_ACTV_REQ);
                Map<String, String> configMap = this.retrieveConfig(keyList);
                if (configMap.get(KnConstants.MAX_SUBS_ALLOWED_GEN_ACTV_REQ) != null) {
                    GENERATE_ACT_CODE_MAX_SUBS_LIMIT = Integer.parseInt(configMap.get(KnConstants.MAX_SUBS_ALLOWED_GEN_ACTV_REQ));
                }
            }
            return GENERATE_ACT_CODE_MAX_SUBS_LIMIT;
        } catch (KnDAOException e) {
            throw e;
        }
    }

    /**
     * method to get feature bit value for specific bit
     *
     * @param featureSet
     * @param bitNumber
     * @return
     */
    public static boolean getFeatureBitValue(String featureSet, int bitNumber) {
        String methodName = "getFeatureBitValue(String,int )";
        knLogger.debug(methodName, "ENTRY: get Feature Bit data ","featureSet "+featureSet,"bitNumber "+bitNumber);
        BitSet bitSet = convertHexStringToBitSet(featureSet);
        boolean bitValue = bitSet.get(bitNumber);
        knLogger.debug(methodName, "EXIT: Feature Bit Value - ", bitValue);
        return bitValue;
    }

    /**
     * method to convert the Long value to Bit Set
     *
     * @param longValue long
     * @return BitSet
     */
    public static BitSet convertLongToBitSet(long longValue) {
        String methodName = "convertLongToBitSet(long)";
        knLogger.debug(methodName, "ENTRY: Received long value to convert bit set is - ", longValue);
        long value = longValue;
        BitSet bitSet = new BitSet(Long.SIZE);
        int index = 0;
        while (value != 0) {
            if (value % 2L != 0) {
                bitSet.set(index);
            }
            ++index;
            value = value >>> 1;
        }
        knLogger.debug(methodName, "EXIT: Generated BitSet - ", bitSet);
        return bitSet;
    }

    public static Properties getLocalDbMgrProps() {
        String methodName = "getLocalDbMgrProps()";
        knLogger.debug(methodName, "Entry :");
        String activeReleasePath = System.getProperty("activeRelDir");
        Properties dbMgrProps = new Properties();
        try {
            FileInputStream ipStream = new FileInputStream(activeReleasePath + File.separator + "dbmgr.props");
            dbMgrProps.load(ipStream);
            ipStream.close();
            knLogger.debug(methodName, "dbmgr properties loaded from file", dbMgrProps);
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred!!!", e);
        }
        return dbMgrProps;
    }

    public static String getComSepList(Collection<String> collectionStr) {
        StringBuffer buffer = new StringBuffer(1000);
        for (String mdn : collectionStr) {
            buffer = buffer.append(SINGLE_QUOTE).append(mdn).append(SINGLE_QUOTE).append(COMMA);
        }
        return buffer.substring(0, buffer.length() - 1);
    }

    public static List<List<String>> getCollectionList(List<String> list, int size) {
        List<List<String>> collecList = new ArrayList<List<String>>();
        if (list.size() <= size) {
            collecList.add(list);
            return collecList;
        }
        int length = list.size() / size;
        int endIndex = size;
        int startIndex = 0;
        for (int i = 0; i < length; i++) {
            List<String> subList = list.subList(startIndex, endIndex);
            collecList.add(subList);
            startIndex = endIndex;
            endIndex = endIndex + size;
        }
        endIndex = endIndex - size;
        if (endIndex < list.size()) {
            List<String> subList = list.subList(endIndex, list.size());
            collecList.add(subList);
        }
        return collecList;
    }

    /**
     * This method return the corresponding substype for a profile ID.
     * @param profileId
     * @return
     */
    public static int getSubsType(int profileId){
        int subType = 0;
        if(KnConstants.EXTERNAL_CONTACT_PROFILEID.KODIAK.value() == profileId){
            subType =  KnConstants.EXTERNAL_CONTACT_SUBS_TYPE.KODIAK.value();
        } else if(KnConstants.EXTERNAL_CONTACT_PROFILEID.MPTT.value() == profileId){
            subType =  KnConstants.EXTERNAL_CONTACT_SUBS_TYPE.MPTT.value();
        }
        return subType;
    }

    /**
     * This method returns the corresponding profileId for a subsType.
     * @param subsType
     * @return
     */
    public static int getExtContProfileId(int subsType){
        int profileId = 0;
        if(KnConstants.EXTERNAL_CONTACT_SUBS_TYPE.KODIAK.value() == subsType){
            profileId = KnConstants.EXTERNAL_CONTACT_PROFILEID.KODIAK.value();
        } else if(KnConstants.EXTERNAL_CONTACT_SUBS_TYPE.MPTT.value() == subsType){
            profileId = KnConstants.EXTERNAL_CONTACT_PROFILEID.MPTT.value();
        }
        return profileId;
    }

    public static String convertLongToHexString(long longValue){
        String hexaString=convertBitSetToHexString(convertLongToBitSet(longValue));
        knLogger.debug("convertLongToHexaString(long", "hexaString",hexaString);
        return hexaString;
    }

    /*
	 * public static String convertLongToString(long longValue){ String
	 * stringValue=Long.toString(longValue); knLogger.debug("convertLongToString",
	 * "stringValue",stringValue); return stringValue; }
	 */
    public static String convertBitSetToHexString(BitSet bitSet) {
        String methodName = "convertBitSetToHexString(BitSet)";
        knLogger.debug(methodName, "ENTRY: Received bit set to convert HexString value is - ", bitSet);
        String hexString = new BigInteger(convertBitSetToBinString(bitSet), 2).toString(16).toUpperCase();
        knLogger.debug(methodName, "EXIT: Generated hexString Value - ", hexString);
        return hexString;
    }
    public static String convertBitSetToBinString(BitSet bitSet) {
        String methodName = "convertBitSetToBinString(BitSet)";
        knLogger.debug(methodName, "ENTRY: Received bit set to convert BinString value is - ", bitSet);
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < bitSet.size(); i++) {
            if (bitSet.get(i)) {
                buffer.append("1");
            } else {
                buffer.append("0");
            }
        }
        String binString = buffer.reverse().toString();
        knLogger.debug(methodName, "EXIT: Generated binString Value - ", binString);
        return binString;
    }

    public static long convertHexStringToLong(String hexString) {
        String methodName = "convertHexStringToLong(String hexString)";
        knLogger.debug(methodName, "ENTRY: Received hexString value to convert Long is - ", hexString);
        String bin = (new BigInteger(hexString, 16).toString(2));
        if(bin.length()>63)
            bin=bin.substring(bin.length()-63);
        BitSet bitSet = new BitSet(bin.length());
        for (int i = 0; i < bin.length(); i++) {
            boolean value = bin.charAt(i) == '1' ? true : false;
            bitSet.set(bin.length() - 1 - i, value);
        }

        knLogger.debug(methodName, "EXIT: Generated BitSet - ", bitSet);
        return convertBitSetToLong(bitSet);

    }

    public static BitSet convertHexStringToBitSet(String hexString) {
        String methodName = "convertHexStringToBitSet(String hexString)";
        knLogger.debug(methodName, "ENTRY: Received hexString value to convert bit set is - ", hexString);
        String bin = new BigInteger(hexString, 16).toString(2);
        BitSet bitSet = new BitSet(bin.length());
        for (int i = 0; i < bin.length(); i++) {
            boolean value = bin.charAt(i) == '1' ? true : false;
            bitSet.set(bin.length() - 1 - i, value);
        }

        knLogger.debug(methodName, "EXIT: Generated BitSet - ", bitSet);
        return bitSet;

    }

    // this method will take the properties file name(directory and file name) and property key as a inputs and return property value.
    public static Object getPropertyValue(String fileName, String propertyName) {
        String methodName = "getPropertyValue(String, String)";
        knLogger.entry(methodName, "file-", fileName, ", property-", propertyName);

        InputStream input = null;
        Object propValue = null;
         try {
             Properties prop = new Properties();
             input = new FileInputStream(fileName);
             prop.load(input);
             propValue = prop.getProperty(propertyName);

         } catch (Exception e) {
             knLogger.warn(methodName, "issue in reading", fileName);

         } finally {
             if (input != null) {
                 try {
                     input.close();
                 } catch (IOException e) {
                     knLogger.warn(methodName, "expection while closing resource-", e);
                 }
             }
         }
        knLogger.exit(methodName, " value for", propertyName, "is -", propValue);
        return propValue;
    }

    /*
   This method is for parsing clientFS from client capabilities for PV 9
   Format is <val>:<val1,val2>;<val3>,<val4>
   Return String with column seperated value as per index
    */
    public static String getParameter(String str, int num)throws StringIndexOutOfBoundsException{
        String methodeName = "getParameter(clientCap)";
        String []clientFS = str.split(":");
        knLogger.debug(methodeName,"clientFS :",clientFS[num]);
        return clientFS[num];
    }

    /*
    This method is for parsing vocoderIdMap from client capabilities for PV 9
    Format is <val1,val2>;<val3>,<val4>
     */
    public static Map<Integer, Integer> getParameterMap(String str) throws StringIndexOutOfBoundsException,NumberFormatException{
        String methodName = "getParameterMap(VocoderID)";
        Map<Integer, Integer> vocoderIds = new TreeMap<>();
        for (String keyValue : str.split(";")) {
            String pairs[] = keyValue.split(",");
            vocoderIds.put(Integer.valueOf(pairs[0]), Integer.valueOf(pairs[1]));

        }
        knLogger.debug(methodName,"VocoderIdMap :",vocoderIds);
        return vocoderIds;

    }

    /**
     * method to get feature bit value for specific bit
     *
     * @param featureSetMap
     * @param bitNumber
     * @return
     */
    public static Map<String ,Boolean> getFeatureBitForBulk(Map<String,String> featureSetMap,int bitNumber) {
        String methodName = "getFeatureBitForBulk(Map,int )";
        Map<String ,Boolean> featureBitMap= new HashMap<String ,Boolean>();

        knLogger.debug(methodName, "Feature Bit Map ", featureSetMap);

            for (Map.Entry<String, String> activeFsEntrySet : featureSetMap.entrySet()) {
                String mdn = activeFsEntrySet.getKey();
                String activeFS = activeFsEntrySet.getValue();

                BitSet bitSet = convertHexStringToBitSet(activeFS);
                boolean bitValue = bitSet.get(bitNumber);
                featureBitMap.put(mdn, bitValue);
                knLogger.debug(methodName, "EXIT: Loop after calculating bit- ", bitValue);
            }

        knLogger.debug(methodName, "EXIT: featureBitMap - ", featureBitMap);
        return featureBitMap;
    }

    /**
     *  This to get service country code for a software package installed in server. This method
     * should cache all the values and return the countryCode for the requested software package
     * @param softwarePkg
     * @return
     * @throws KnDAOException
     */
    public static String getServiceCountryCode(KnConstants.SOFTWARE_PKG softwarePkg) throws KnDAOException {
        String methodName = "getServiceCountryCode(KnConstants.SOFTWARE_PKG softwarePkg)";
        knLogger.info(methodName, " SOFTWARE_PKG - ", softwarePkg.value());
        String countryCode= null;
        if (countryCodeMap == null) {
            countryCodeMap = fetchServiceCountryCode();
            knLogger.info(methodName, " retrieved from DB .... ",countryCodeMap);
        }

        if (countryCodeMap.get(softwarePkg.value()) == null) {
            throw new KnDAOException(KnErrorCodes.DAO.ROW_NOT_FOUND,"CountryCode not configured for package -"+softwarePkg.value());
        }else{
            countryCode  = countryCodeMap.get(softwarePkg.value()).trim();
        }
        knLogger.info(methodName, "countryCode fetched :", countryCode);

        return  countryCode;
    }

    /**
     * This to fetch service country code for a software package installed in server.
     * @return
     * @throws KnDAOException
     */
    private static Map<Integer, String>  fetchServiceCountryCode() throws KnDAOException {
        String methodName = "fetchServiceCountryCode()";
        knLogger.info(methodName, "retrieving Webip, PARAM Value for country code ");
        KnPersisterTxn persisterTxn =null;
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String localPttId = KnDbUtil.getDBConfigInfo().getLocalPttId();

        try {
            persisterTxn = KnPersisterTxn.getPersisterTxn();
            persisterTxn.open();
            countryCodeMap = new HashMap<Integer,String>();
            conn = persisterTxn.getDBConnection(localPttId, false);
            pStmt = conn.prepareStatement(SELECT_CC_SWPKGCONFIG);
            pStmt.setString(1, localPttId);
            knLogger.info(methodName, "QUERY: Executing - ", SELECT_CC_SWPKGCONFIG);
            rs = pStmt.executeQuery();
            knLogger.info(methodName, "QUERY: Executed - ");

            while (rs.next()) {
                //storing the values if we need to play around with the  paramvalue.
                int webServiceId=rs.getInt("SWPKGID");
                String svcParamCountrycode=rs.getString("COUNTRYCODE").trim();
                knLogger.debug(methodName," webServiceId :",webServiceId, " svcParamCountrycode :",svcParamCountrycode);
                countryCodeMap.put(webServiceId,svcParamCountrycode);
            }
            persisterTxn.save();

        } catch (Exception e) {
            KnDbUtil.rollback(persisterTxn);
            knLogger.error(methodName, "Exception occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", localPttId, "DG.SWPKGCONFIGPARAMVALUE", null);

        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
        }
        knLogger.info(methodName, "Returning country code value - ", countryCodeMap);
        return countryCodeMap;
    }

    /**
     *  This to get subscription type  for a software package installed in server. This method
     * should cache all the values and return the subscription type for the requested software package
     * @param softwarePkg
     * @return
     * @throws KnDAOException
     */
    public static List<Integer> retrieveSubscritpionList(KnConstants.SOFTWARE_PKG softwarePkg) throws KnDAOException {
        String methodName = "retrieveSubscritpionList(KnConstants.SOFTWARE_PKG softwarePkg)";
        knLogger.entry(methodName, " SOFTWARE_PKG - ", softwarePkg.value());
        List<Integer> subscriptionList = new ArrayList<>();
        if (subscriptionTypeMap == null) {
            subscriptionTypeMap = getAllowedSubscrpType();
            knLogger.info(methodName, " retrieved from DB .... ",subscriptionTypeMap);
        }

        if (subscriptionTypeMap.get(softwarePkg.value()) == null) {
            throw new KnDAOException(KnErrorCodes.DAO.ROW_NOT_FOUND,"subscritpion type  not configured for package -"+softwarePkg.value());
        }else{
            subscriptionList  = subscriptionTypeMap.get(softwarePkg.value());
        }
        knLogger.exit(methodName, "subscritpion type fetched :", subscriptionList);

        return  subscriptionList;
    }

    /**
     * This method will return the value of the subscription type  from the software package .
     * i.e say if SOAP  request handler package installed, then we are fetching the value of subscription type
     * @return
     * @throws KnDAOException
     */
    private static Map<Integer, List<Integer>> getAllowedSubscrpType() throws KnDAOException {
        String methodName = "getAllowedSubscrpType()";
        knLogger.info(methodName, "retrieving Webip, PARAM Value for PARAM Name ");
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String localPttId = KnDbUtil.getDBConfigInfo().getLocalPttId();
        KnPersisterTxn persisterTxn = null;
        try {
            persisterTxn = KnPersisterTxn.getPersisterTxn();
            persisterTxn.open();
            subscriptionTypeMap = new HashMap<Integer,List<Integer>>();
            conn = persisterTxn.getDBConnection(localPttId, false);
            pStmt = conn.prepareStatement(SELECT_SWPKGCONFIGPARAMVALUE);
            pStmt.setString(1, localPttId);
            pStmt.setString(2,"ALLOWED_SUBSCR_TYPES");
            knLogger.info(methodName, "QUERY: Executing - ", SELECT_SWPKGCONFIGPARAMVALUE);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "QUERY: Executed - ");
            List<Integer> subscriptionList = null;
            while (rs.next()) {
                //storing the values if we need to play around with the  paramvalue.
                int swPkgId=rs.getInt("SWPKGID");
                String webSvcParamValue=rs.getString("PARAMVALUE");
                knLogger.debug(methodName," SWPKGID :",swPkgId, " webSvcParamIValue :", webSvcParamValue);
                if (webSvcParamValue != null && !webSvcParamValue.isEmpty()) {
                    subscriptionList = new ArrayList<>();
                    String[] strArray = webSvcParamValue.split(KnConstants.DELIM);
                    for (String subsType : strArray) {
                        subscriptionList.add(Integer.valueOf(subsType.trim()));
                    }
                }
                subscriptionTypeMap.put(swPkgId,subscriptionList);
            }
            persisterTxn.save();
        } catch (Exception e) {
            KnDbUtil.rollback(persisterTxn);
            knLogger.error(methodName, "Exception occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", localPttId, "DG.SWPKGCONFIGPARAMVALUE", null);

        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
        }
        knLogger.info(methodName, "Returning value - ", subscriptionTypeMap);
        return subscriptionTypeMap;
    }

    public static long convertBitSetToLong(BitSet bitSet) {
        String methodName = "convertBitSetToLong(BitSet)";
        knLogger.debug(methodName, "ENTRY: Received bit set to convert long value is - ", bitSet);

        long longValue = 0L;
        for (int i = 0; i < bitSet.size(); ++i) {
            longValue += bitSet.get(i) ? (1L << i) : 0L;
        }

        knLogger.debug(methodName, "EXIT: Generated Long Value - ", longValue);
        return longValue;
    }
    public static String getUA(String UserAgent, int majorPvVersion){
        String methodName = "getUA(String, int)";
        StringBuilder builder = new StringBuilder(50);
        KnUserAgentDTO userAgentDTO=null;
        if(UserAgent!=null)
        {
         userAgentDTO = getDetailsFromUserAgent(UserAgent);
        }
        if(userAgentDTO == null && majorPvVersion == 0){
            return null;
        }
        builder = (userAgentDTO != null) ? builder.append(userAgentDTO.getOsName().trim()).append(SEMI_COLON) : builder.append(EMPTY_STRING).append(SEMI_COLON);
        builder = (majorPvVersion != 0) ? builder.append(majorPvVersion).append(SEMI_COLON) : builder.append(EMPTY_STRING).append(SEMI_COLON);
        builder = (userAgentDTO != null) ? builder.append(userAgentDTO.getManufactName().trim()).append(SEMI_COLON) : builder.append(EMPTY_STRING).append(SEMI_COLON);
        builder = (userAgentDTO != null) ? builder.append(userAgentDTO.getDeviceName().trim()): builder.append(EMPTY_STRING);
        knLogger.debug(methodName, "UserAgent Tag : ", builder);
        return builder.toString();
    }

    public String getSWPKGConfigType(int swpkgId, String paramName) throws KnDAOException {
        String methodName = "getSWPKGConfigType";
        String softwarePKGValue = "";
        knLogger.debug(methodName,"Software Package Name :: ", paramName, "Software Package ID :: ", swpkgId);
        if(swpkgConfigMap==null){
            swpkgConfigMap = retrieveSWPKGConfig();
            knLogger.info(methodName, " retrieved from DB .... ",swpkgConfigMap);
        }

        if(swpkgConfigMap.get(swpkgId)==null){
            knLogger.info(methodName, "Software pkg not configured..");
            throw new KnDAOException(KnErrorCodes.DAO.ROW_NOT_FOUND, "Package Id is notconfigured for package - "+swpkgId);
        }else{
            Map<String, String> param_value_Map = swpkgConfigMap.get(swpkgId);
            softwarePKGValue = param_value_Map.get(paramName);
            knLogger.debug(methodName, "Software Pkg value fetched from database :: ", softwarePKGValue);
        }
        return softwarePKGValue;
    }

    private Map<Integer, Map<String,String>> retrieveSWPKGConfig() throws KnDAOException{
        String methodName = "retrieveSWPKGConfig";
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String localPttId = KnDbUtil.getDBConfigInfo().getLocalPttId();
        KnPersisterTxn persisterTxn = null;
        try {
            persisterTxn = KnPersisterTxn.getPersisterTxn();
            persisterTxn.open();
            swpkgConfigMap = new HashMap<Integer,Map<String,String>>();
            conn = persisterTxn.getDBConnection(localPttId, false);
            String query = "SELECT SWPKGID, PARAMNAME, PARAMVALUE FROM DG.SWPKGCONFIGPARAMVALUE WHERE PTTSERVERID=? ";
            pStmt = conn.prepareStatement(query);
            pStmt.setString(1, localPttId);
            knLogger.info(methodName, "QUERY: Executing - ", query);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "QUERY: Executed - ");

            while (rs.next()) {
                //storing the values if we need to play around with the  paramvalue.
                int swPkgId = rs.getInt("SWPKGID");
                String paramValue=rs.getString("PARAMVALUE");
                String paramName=rs.getString("PARAMNAME");

                knLogger.debug(methodName," SWPKGID :",swPkgId,"webSvcParamIName",paramName, " webSvcParamIValue :",paramValue);
                if(swpkgConfigMap.containsKey(swPkgId)) {
                    Map<String, String> param_Name_value_Map = swpkgConfigMap.get(swPkgId);
                    param_Name_value_Map.put(paramName,paramValue);
                    swpkgConfigMap.put(swPkgId, param_Name_value_Map);
                }else{
                    Map<String, String> param_Name_value_Map = new HashMap<>();
                    param_Name_value_Map.put(paramName,paramValue);
                    swpkgConfigMap.put(swPkgId,param_Name_value_Map);
                }
            }
            persisterTxn.save();
        } catch (Exception e) {
            KnDbUtil.rollback(persisterTxn);
            knLogger.error(methodName, "Exception occurred - ", e);
            throw KnDbUtil.processException(e, "Exception occurred ", localPttId, "DG.SWPKGCONFIGPARAMVALUE", null);

        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
        }
        knLogger.info(methodName, "Returning value - ", swpkgConfigMap);
        return swpkgConfigMap;

    }

    public static byte[] getEncryptedPassword(String password, byte[] salt, int iterations, int derivedKeyLength) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, derivedKeyLength * 8);
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        return f.generateSecret(spec).getEncoded();
    }

    public static String convertHexToAscii(String hex) {
        String methodName = "convertHexToAscii(String hex)";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hex.length(); i++) {
            char c = hex.charAt(i);
            int ascii = (int) c;
            sb.append(Integer.toHexString(ascii));
        }
        knLogger.debug(methodName, " Hex to ASCII value : ", sb.toString());
        return sb.toString();
    }

    public static String convertAsciiToHex(String asciiValue) {
        String methodName = "convertAsciiToHex(String asciiValue)";
        StringBuffer hex = new StringBuffer();
        for (int i = 0; i < asciiValue.length(); i += 2) {
            String str = asciiValue.substring(i, i + 2);
            hex.append((char) Integer.parseInt(str, 16));
        }
        knLogger.debug(methodName, " ASCII to Hex Value : ", hex.toString());
        return hex.toString();
    }

    public static String convertToHex(byte[] data) {
        StringBuffer buf = new StringBuffer();
        for (byte aData : data) {
            int halfbyte = (aData >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9)) {
                    buf.append((char) ('0' + halfbyte));
                } else {
                    buf.append((char) ('a' + (halfbyte - 10)));
                }
                halfbyte = aData & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public static String getFeatureSet(String fs) {
         if(fs.length()%2!=0) {
             fs= "0"+fs;
        }

        return fs;
    }

    public static Boolean convertIntToBoolean(int fs) {
        return fs==1 ?true:false;
    }

    /**
     * Utility method for populating
     * @param activefs2
     * @return
     */
    public static List<String> populateOidcScopes(String activefs2){
        List<String> scopes = new ArrayList<>();

        scopes.add(MCPTT_SERVICE_SCOPE_OIDC);
        scopes.add(MCPTT_VIDEO_SCOPE_OIDC);
        scopes.add(MCPTT_DATA_SCOPE_OIDC);
        scopes.add(MCPTT_KEY_MGMT_SCOPE_OIDC);
        scopes.add(MCPTT_VIDEO_KEY_MGMT_SCOPE_OIDC);
        scopes.add(MCPTT_DATA_KEY_MGMT_SCOPE_OIDC);
        scopes.add(MCPTT_CONFIG_MGMT_SCOPE_OIDC);
        scopes.add(MCPTT_VIDEO_CONFIG_MGMT_SCOPE_OIDC);
        scopes.add(MCPTT_VIDEO_GROUP_MGMT_SCOPE_OIDC);
        scopes.add(MCPTT_DATA_GROUP_MGMT_SCOPE_OIDC);
        scopes.add(MCPTT_DATA_CONFIG_MGMT_SCOPE_OIDC);
        scopes.add(MCPTT_GROUP_MGMT_SCOPE_OIDC);
        return  scopes;
    }

    public static String generateMD5(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md;
        md = MessageDigest.getInstance("MD5");
        byte[] md5hash = new byte[32];
        md.update(text.getBytes("UTF-8"), 0, text.length());
        md5hash = md.digest();
        return convertToHex(md5hash);
    }

    public static Properties getUpgradeProps() {
        Properties logConfigProps = new Properties();
        try {
            FileInputStream fileInputStream = new FileInputStream("/DG/activeRelease/dat/xdm/upgrade.props");
            logConfigProps.load(fileInputStream);
            fileInputStream.close();
        } catch (Exception e) {
            knLogger.error("getUpgradeProps()", "failed to read file-", e);
        }
        return logConfigProps;
    }

    public static void setIsFeatureBitUpdateComplete(boolean isFeatureBitUpdateComplete) {
        KnGeneralUtil.isFeatureBitUpdateComplete = isFeatureBitUpdateComplete;
    }

    public static String replaceContactWithValue(String str, String constant, String value) {
        String finalStr = "";
        if (str == null || str.trim().equals("")) {
            return finalStr;
        }
        finalStr = str.replaceAll(constant, value);
        return finalStr;
    }

    public static String formCommaSeperatedIdList(Collection<String> collectionStr) {
        StringBuffer buffer = new StringBuffer(200);
        for (String mdn : collectionStr) {
            buffer = buffer.append("'").append(mdn).append("',");
        }
        int indx = buffer.lastIndexOf(",");
        if (indx > 0) {
            buffer.deleteCharAt(indx);
        }
        return buffer.toString();
    }

    public static List<Integer> getGroupIdFromGroupURI(List<String> str){
        String methodName = "getGroupIdFromGroupURI(String str)";
        List<Integer> values = new ArrayList<>();
        for(String s : str) {
            int start = s.indexOf(".");
            int stop = s.indexOf("@");
            values.add(Integer.parseInt(s.substring(start + 1, stop)));
            //Integer value = Integer.parseInt(str.substring(start + 1, stop));
        }
        knLogger.debug(methodName,"Value Returned: ",values);
        return values;
    }

    public static void setVaultQUERYFQDN(String vaultQUERYFQDN){
        vaultQueryFqdn=vaultQUERYFQDN;
    }

    public static String getVaultQUERYFQDN(){
        return vaultQueryFqdn;
    }
    public static void setksekVaultInfo(KnKsekVaultInfo ksekVaultInfo){
       KnGeneralUtil.ksekVaultInfo=ksekVaultInfo;
    }

    public static KnKsekVaultInfo getKsekVaultInfo() {
        return ksekVaultInfo;
    }

    private static int getDecimalRecordFromBit(String bitPosition) {
        return Integer.parseInt(bitPosition, 2);
    }

    /**
     * This method converts input number format to bitset string
     * @param groupRecordingFs
     * @return String
     */
    public static String getBitFromDecimalRecord(String groupRecordingFs) {
        return String.format("%3s", Integer.toBinaryString(Integer.parseInt(groupRecordingFs))).replace(" ", "0");
    }

    /**
     * This method returns decimal number
     * @param groupRecordingFs
     * @param mcsPttRecordingFlag
     * @param mcsDataRecordingFlag
     * @param mcsVideoRecordingFlag
     * @return
     */
    public static Integer getRecordDataForGroup(String groupRecordingFs, boolean mcsPttRecordingFlag,
                                                boolean mcsDataRecordingFlag, boolean mcsVideoRecordingFlag) {

        String methodName = "getRecordDataForGroup(String, boolean,boolean , boolean )";
        knLogger.debug(methodName, "groupRecordingFs", groupRecordingFs, "mcsPttRecordingFlag",mcsPttRecordingFlag,"mcsDataRecordingFlag", mcsDataRecordingFlag, "mcsVideoRecordingFlag", mcsVideoRecordingFlag);

        if (groupRecordingFs == null || groupRecordingFs.isEmpty()) {
            return null;
        }
        if (!mcsPttRecordingFlag && !mcsDataRecordingFlag && !mcsVideoRecordingFlag) {
            return 0;
        }

        String bitPositionString = getBitFromDecimalRecord(groupRecordingFs); //000 , 001, 010
        StringBuilder bitPosition = new StringBuilder(bitPositionString);

        if (!mcsPttRecordingFlag) {
            bitPosition.setCharAt(2, '0');
        }
        if (!mcsDataRecordingFlag) {
            bitPosition.setCharAt(1, '0');
        }
        if (!mcsVideoRecordingFlag) {
            bitPosition.setCharAt(0, '0');
        }
        knLogger.debug(methodName, "Returned value ", bitPosition);

        return getDecimalRecordFromBit(new String(bitPosition));
    }

    public static String getRecordingFs(String mcsPttRecordingFlag,
                                                String mcsDataRecordingFlag, String mcsVideoRecordingFlag) {

        String methodName = "getRecordingFs(String, boolean,boolean , boolean )";
        knLogger.debug(methodName, "mcsPttRecordingFlag",mcsPttRecordingFlag,"mcsDataRecordingFlag", mcsDataRecordingFlag, "mcsVideoRecordingFlag", mcsVideoRecordingFlag);

        String bitPositionString = getBitFromDecimalRecord("0");
        StringBuilder bitPosition = new StringBuilder(bitPositionString);

        if (Objects.equals(mcsPttRecordingFlag, ENABLED)) {
            bitPosition.setCharAt(2, '1');
        }
        if (Objects.equals(mcsDataRecordingFlag, ENABLED)) {
            bitPosition.setCharAt(1, '1');
        }
        if (Objects.equals(mcsVideoRecordingFlag, ENABLED)) {
            bitPosition.setCharAt(0, '1');
        }
        knLogger.debug(methodName, "Returned value ", bitPosition);
        return String.valueOf(bitPosition);
    }

    public static String getSplitString(String input) {
        String methodName = "getSplitString(String)";
        knLogger.info(methodName, "Entry input: ", input);
        String result = null;
        if (null != input) {
            String[] splits = input.split("_");
            if (null != splits[0]) {
                result = splits[0];
                result = result.replace("s", "");
            }
        }
        knLogger.info(methodName, "Returning result: ", result);
        return result;
    }
        /**
     * split the list into fixed number of size
     * @param alist
     * @param len
     * @param <T>
     * @return
     */
    public static <T> List<List<T>> splitList(List<T> alist, final int len) {
        return IntStream.range(0, alist.size()) // Iterate over the whole thing
                .filter(i -> i % len == 0) // Filter out every 'len' number
                .boxed() // Create a stream (instead of IntStream)
                .map(i -> alist.subList(i, Math.min(i + len, alist.size()))) // create sublists
                .collect(Collectors.toList()); // Collect the whole thing to a list of lists
    }

    public static String convertActiveFs2toHexActiveFs1(String hexString) {
        String methodName = "convertActiveFs2toHexActiveFs1(String hexString)";
        knLogger.debug(methodName, "ENTRY: Received activeFs2 value to convert to activefs1 is - ", hexString);
        String bin = (new BigInteger(hexString, 16).toString(2));
        if (bin.length() > 63)
            bin = bin.substring(bin.length() - 63);
        BitSet bitSet = new BitSet(bin.length());
        for (int i = 0; i < bin.length(); i++) {
            boolean value = bin.charAt(i) == '1' ? true : false;
            bitSet.set(bin.length() - 1 - i, value);
        }

        knLogger.debug(methodName, "EXIT: Generated BitSet - ", bitSet);
        return convertBitSetToHexString(bitSet);
    }

    public static Object byteArrayToObject(byte[] bytes) throws KnException {
        String methodName = "byteArrayToObject(byte[])";
        Object obj = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bis);
            obj = ois.readObject();
        } catch (ClassNotFoundException | IOException ex) {
            knLogger.info(methodName, "Unexpected Exception while ", ex);
            throw new KnException("CM1001", "Unexpected Exception while converting object.");
        }
        return obj;
    }

    public static String updateFeatureBit(String featureset, Map<Integer, Integer> values) {
        BitSet bit = convertHexStringToBitSet(featureset);
        for (Map.Entry<Integer, Integer> n1 : values.entrySet()) {
            bit.set(n1.getKey(), n1.getValue().equals(1) ? true : false);

        }
        return convertBitSetToHexString(bit);
    }

    public static String calculateActiveFeatureSetBasedOnPv(String hexString, int clientPv) {
        String methodName = "calculateActiveFeatureSetBasedOnPv";
        knLogger.debug(methodName, " Recevied hexString to convert ", hexString, " clientPv ", clientPv);
        if (clientPv < PROTOCOL_VERSION_16) {
            knLogger.debug(methodName, " Pv is less than 16 ", clientPv);
            return KnGeneralUtil.convertActiveFs2toHexActiveFs1(hexString);
        }
        if (!clientPvBitSupportMap.containsKey(clientPv)) {
            knLogger.debug(methodName, " Pv is not found in the map ", clientPv);
            return hexString;
        }
        BitSet activeFS_BitSet = new BitSet(clientPvBitSupportMap.get(clientPv) + 1);
        activeFS_BitSet.set(0, clientPvBitSupportMap.get(clientPv) + 1);
        BitSet activeFs2 = convertHexStringToBitSet(hexString);
        activeFS_BitSet.and(activeFs2);
        return convertBitSetToHexString(activeFS_BitSet);
    }

    public static String getProfileMdnCorpAdminFS(String corpAdminFS2) {
        String methodName = "getProfileMdnCorpAdminFS()";
        knLogger.debug(methodName, "Before corpAdminFS - ", corpAdminFS2);
        BitSet bitSet  = convertHexStringToBitSet(corpAdminFS2);
        for(int bit : KnConstants.DEFAULT_ENABLED_BITS_PROFILE_MDN){
            bitSet.set(bit, true);
        }
        String updatedFs = convertBitSetToHexString(bitSet);
        knLogger.debug(methodName, "After corpAdminFS - ", updatedFs);
        return updatedFs;
    }

    /**
     * Return Empty if Map is null, It can be used in for loop
     * @param map
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K,V> Map<K,V> emptyIfNull(Map<K,V> map) {
        if (map != null) {
            return map;
        } else {
            return Collections.<K,V>emptyMap();
        }
    }

    /**
     * Return Empty if Set is null, It can be used in for loop
     * @param set
     * @param <T>
     * @return
     */
    public static <T> Set<T> emptyIfNull(Set<T> set) {
        if (set != null) {
            return set;
        } else {
            return Collections.<T>emptySet();
        }
    }

    /**
     *   Return Empty if List is null, It can be used in for loop
     * @param list
     * @param <T>
     * @return
     */
    public static <T> List<T> emptyIfNull(List<T> list) {
        if (list != null) {
            return list;
        } else {
            return Collections.<T>emptyList();
        }
    }

    /**
     *   Return Empty if List is null, It can be used in for loop
     * @param list
     * @param <T>
     * @return
     */
    public static <T> Collection<T> emptyIfNull(Collection<T> list) {
        if (list != null) {
            return list;
        } else {
            return Collections.<T>emptyList();
        }
    }
    public static int getStartIndex(int fetchSize, int nextToken) {
        return (nextToken * fetchSize) + 1;
    }

    public static int getEndIndex(int fetchSize, int nextToken) {
        return (nextToken * fetchSize) + fetchSize;
    }

    public static String convertKnPaylaodIPToJsonString(KnPayloadIP knPayloadIP) {
        String methodName = "convertKnPaylaodIPToJsonString(KnPaylaodIP)";
        knLogger.debug(methodName, "ENTRY: Received KnPaylaodIP to convert to String is - ", knPayloadIP);
        ObjectMapper mapper = new ObjectMapper();
        try {
            String payload = mapper.writeValueAsString(knPayloadIP);
            knLogger.debug(methodName, "EXIT: Generated String - ", payload);
            return payload;
        } catch (IOException e) {
            knLogger.error(methodName, "Error while converting KnPaylaodIP to String");
            throw new RuntimeException("Error while converting KnPaylaodIP to String");
        }
    }

    public enum EntityName {
        MDN(1),
        GROUP(2);

        private int value;
        EntityName(int value) {
            this.value = value;
        }
        public int get() {
            return value;
        }
    }

    public enum ASYNC_OPS_ID {
        CREATE_SUBSCRIBER(1),
        UPDATE_SUBSCRIBER(2),
        DELETE_SUBSCRIBER(3),
        CHANGE_MDN(4),
        NOT_ASSINGED(5),
        UPDATE_USER_ID(6),
        UPDATE_MC_ID(7);

        private int value;
        ASYNC_OPS_ID(int value) {
            this.value = value;
        }
        public int get() {
            return value;
        }
    }

    public enum ASYNC_TASK_ID {
        notifyAllGroupMembers(1),
        isContactNotifyRequired(2),
        isEmergencyConfigNotifyRequired(3),
        isAuthorizationNotifyRequired(4),
        createOidcDeviceSharingProfile(5),
        updateCorpId(6),
        groupNotifyOnActiveFsChange(7),
        notifyCorpProfiles(8),
        updateGrpBCAndSubsChangeLogMap(9),
        notifyXcapMobile(10),
        updateContactName(11),
        updateClientType(12),
        updateSubscriptionType(13),
        deleteSubscriberProfile(14),
        initiateDispJobForDeletedMdn(15),
        isCorpLastProfileUpdateRequired(16),
        isCreateOidcDeviceSharingProfileWithMCSIds(17),

        isCorpCreateIDMProileRequired(18),
        dispatchClient(19),
        deleteCorporateProfile(20),
        subscriberNameChange(21);

        private int value;
        ASYNC_TASK_ID(int value) {
            this.value = value;
        }
        public int get() {
            return value;
        }
    }

    public enum SERVICE_TYPE {
        CORP(1),
        SOAP(2);

        private int value;
        SERVICE_TYPE(int value) {
            this.value = value;
        }
        public int get() {
            return value;
        }
    }



    public static String decodeHexDeviceInfo(String hexDeviceInfo, int maxAttempts) {
        int attempt = 1;
        String hextStringValue = hexDeviceInfo;
        knLogger.debug("Hex Device Info--->", hexDeviceInfo, "Max Attempts--->", maxAttempts);
        do {
            if (!hexDeviceInfo.contains("{")) {
                hexDeviceInfo = hexToString(hexDeviceInfo);
                attempt++;
            } else {
                break;
            }
        } while (attempt < maxAttempts);

        if (attempt == maxAttempts && !hexDeviceInfo.contains("{")) {
            hexDeviceInfo = hextStringValue;
            knLogger.warn("Max decode attempts reached after " + maxAttempts + " attempts. Hex Device Info: " + hexDeviceInfo);
        }
        return hexDeviceInfo;
    }

    public static String hexToString(String hex) {
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < hex.length(); i += 2) {
            bytes[i / 2] = (byte) Integer.parseInt(hex.substring(i, i + 2), 16);
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }
}