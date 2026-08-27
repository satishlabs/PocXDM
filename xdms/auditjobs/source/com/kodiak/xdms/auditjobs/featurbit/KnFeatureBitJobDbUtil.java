/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/*
 *  ***********************************************************************
 *  File name:  KnFeatureBitJobDbUtil.java
 *  Subsystem:  PoCXDM
 *
 *    Name                 	    Date         	                  Release
 *    --------------------   -----------------------  -------------------------
 *    Chandrashekar HS          23/01/20, 1:23 PM                    10.0
 *
 *  Copyright (c) 2019 Kodiak, A Motorola Solutions Company
 *  9th floor, MFar, Manayata Tech Park,
 *  Greenheart Phase IV,Nagawara
 *  Bangalore - 560 045
 *  www.motorolasolutions.com
 *  All Rights Reserved.
 *
 * This software is the confidential and proprietary information of KodiakMotorola Solutions, Inc.
 * You shall not disclose such confidential information and shall use it only in accordance with the terms of the license agreement you entered into with Kodiak Motorola Solutions.
 *   ***********************************************************************
 */

package com.kodiak.xdms.auditjobs.featurbit;

import com.kodiak.common.commdto.common.KnSubsFeatureBitInfoDTO;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnDbUtil;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.common.commdto.common.KnCorpFeatureBitInfoDTO;
import com.kodiak.utilities.generatealarmutil.KnAlarmConstants;
import com.kodiak.utilities.generatealarmutil.KnAlarmGeneratorUtil;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class KnFeatureBitJobDbUtil {
    private static final KnLogger knLogger = KnLogger.getLogger(KnFeatureBitJobDbUtil.class);
    private static String pttServerId;

    public KnFeatureBitJobDbUtil() {
        pttServerId = KnDbUtil.getDBConfigInfo().getLocalPttId();
    }

    HttpClient client = HttpClient.newHttpClient();
    public static final int ALARM_MOCLASSTYPE = 5;
   /* *//**
     * This method retgrieves the configurations from DG.RTXENVVARIABLEINFO for a given key
     *
     * @param paramName
     * @return the paramValue
     * @throws KnDAOException exception
     *//*
    protected String retrieveRTXEnvValue(String paramName) throws KnDAOException {
        String methodName = "retrieveRTXEnvValue(String)";
        knLogger.info(methodName, "retrieving rtx value  - ");
        String paramValue = null;
        KnPersisterTxn persisterTxn = KnPersisterTxn.getPersisterTxn();
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        try {
            persisterTxn.open();
            String query = "SELECT PARAMVALUE FROM DG.RTXENVVARIABLEINFO WHERE PARAMNAME ='" + paramName + "'";
            conn = persisterTxn.getDBConnection(pttServerId, false);
            pStmt = conn.prepareStatement(query);

            knLogger.info(methodName, "QUERY: Executing - ", query);
            rs = pStmt.executeQuery();
            knLogger.info(methodName, "QUERY: Executed - ");

            while (rs.next()) {
                paramValue = rs.getString("PARAMVALUE");
            }
            persisterTxn.save();

        } catch (SQLException e) {
            persisterTxn.rollback();
            knLogger.error(methodName, "SQLException occurred - ", e);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
        }
        knLogger.debug(methodName, "Returning value  - ", paramValue);
        return paramValue;
    }*/

   /* *//**
     * This method inserts  the configurations in DG.RTXENVVARIABLEINFO for a given key
     *
     * @param paramName
     * @return the paramValue
     * @throws KnDAOException exception
     *//*
    protected void insertRTXEnvValue(String paramName, String paramValue) throws KnDAOException {
        String methodName = "insertRTXEnvValue(String)";
        knLogger.info(methodName, "insertRTXEnvValue rtx value  - ", paramName, paramValue);
        KnPersisterTxn persisterTxn = KnPersisterTxn.getPersisterTxn();
        Connection conn;
        PreparedStatement pStmt = null;
        try {
            persisterTxn.open();
            String query = "INSERT INTO DG.RTXENVVARIABLEINFO(PTTSERVERID, PARAMNAME, PARAMVALUE) values(?,?,?)";
            conn = persisterTxn.getDBConnection(pttServerId, false);
            pStmt = conn.prepareStatement(query);

            knLogger.info(methodName, "QUERY: Executing - ", query);
            pStmt.setString(1, pttServerId);
            pStmt.setString(2, paramName);
            pStmt.setString(3, paramValue);
            pStmt.executeUpdate();
            knLogger.info(methodName, "QUERY: Executed - ");
            persisterTxn.save();
        } catch (SQLException e) {
            persisterTxn.rollback();
            knLogger.error(methodName, "SQLException occurred - ", e);

        } finally {
            KnDbUtil.closeStatement(pStmt);
        }
        knLogger.debug(methodName, "Returning value  - ", paramValue);
    }

    *//**
     * This method updates  the configurations in DG.RTXENVVARIABLEINFO for a given key
     *
     //* @param paramName
     * @return the paramValue
     * @throws KnDAOException exception
     *//*
    protected void updateRTXEnvValue(String paramName, String paramValue) throws KnDAOException {
        String methodName = "updateRTXEnvValue(String)";
        knLogger.info(methodName, "updateRTXEnvValue rtx value  - ", paramName, paramValue);
        KnPersisterTxn persisterTxn = KnPersisterTxn.getPersisterTxn();
        Connection conn;
        PreparedStatement pStmt = null;
        try {
            persisterTxn.open();
            String query = "UPDATE DG.RTXENVVARIABLEINFO set PARAMVALUE = ? where PTTSERVERID = ? AND PARAMNAME = ?";
            conn = persisterTxn.getDBConnection(pttServerId, false);
            pStmt = conn.prepareStatement(query);

            knLogger.info(methodName, "QUERY: Executing - ", query);
            pStmt.setString(1, paramValue);
            pStmt.setString(2, pttServerId);
            pStmt.setString(3, paramName);
            pStmt.executeUpdate();
            knLogger.info(methodName, "QUERY: Executed - ");
            persisterTxn.save();
        } catch (SQLException e) {
            persisterTxn.rollback();
            knLogger.error(methodName, "SQLException occurred - ", e);

        } finally {
            KnDbUtil.closeStatement(pStmt);
        }
        knLogger.debug(methodName, "Returning value  - ", paramValue);
    }*/

    protected List<KnSubsFeatureBitInfoDTO> getUnUpgradedMdns(String licenseVersion) throws KnDAOException {
        String methodName = "getUnUpgradedMdns(String)";
        knLogger.info(methodName, "retrieving getUnUpgradedMdns  for license ", licenseVersion);
        List<KnSubsFeatureBitInfoDTO> mdnDetailsList = new ArrayList<>();
        KnPersisterTxn persisterTxn = KnPersisterTxn.getPersisterTxn();
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        KnSubsFeatureBitInfoDTO mdnDetails;
        try {
            String batchSize = KnGeneralUtil.getUpgradeProps().getProperty("BATCHSIZE");
            if(batchSize==null){
                batchSize= "50";
            }
            persisterTxn.open();
            String query = "SELECT FIRST ? MDN, CLIENT_TYPE, PUBLICSUBSCRIPTIONTYPE, CORPSUBSCRIPTIONTYPE, PAMACCID, CORPID, QPPPACKID, " +
                    "ACTIVEFS1, CLIENTFS1, OPSFS1, CORPADMINFS1, SUBSCRIBERFS1,  XDMSFS1, ACTIVEFS2, CLIENTFS2, OPSFS2, CORPADMINFS2, SUBSCRIBERFS2, USERPROFILEFS2, XDMSFS2, FEATURE_REL_VERSION,USERPROFILEINDEX " +
                    "FROM DG.POCSUBSCRINFO WHERE FEATURE_REL_VERSION is NULL OR FEATURE_REL_VERSION != ?";
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);
            pStmt.setString(1, batchSize);
            pStmt.setString(2, licenseVersion);
            knLogger.info(methodName, "QUERY: Executing - ", query);
            rs = pStmt.executeQuery();
            knLogger.info(methodName, "QUERY: Executed - ", rs.getFetchSize());


            while (rs.next()) {
                mdnDetails = new KnSubsFeatureBitInfoDTO();
                mdnDetails.setMdn(rs.getString("MDN"));
                mdnDetails.setClientType(rs.getInt("CLIENT_TYPE"));
                mdnDetails.setPublicSubscriptionType(rs.getInt("PUBLICSUBSCRIPTIONTYPE"));
                mdnDetails.setCorporateSubscriptionType(rs.getInt("CORPSUBSCRIPTIONTYPE"));
                mdnDetails.setPamAccId(rs.getInt("PAMACCID"));
                mdnDetails.setCorpId(rs.getInt("CORPID"));
                mdnDetails.setQppPackId(rs.getInt("QPPPACKID"));
                mdnDetails.setActiveFS1(rs.getLong("ACTIVEFS1"));
                mdnDetails.setClientFS1(rs.getLong("CLIENTFS1"));
                mdnDetails.setOpsFS1(rs.getLong("OPSFS1"));
                mdnDetails.setCorpAdminFS1(rs.getLong("CORPADMINFS1"));
                mdnDetails.setSubsFS1(rs.getLong("SUBSCRIBERFS1"));
                mdnDetails.setXdmFS1(rs.getLong("XDMSFS1"));
                mdnDetails.setActiveFS2(rs.getString("ACTIVEFS2") != null ? rs.getString("ACTIVEFS2") : KnGeneralUtil.convertLongToHexString(rs.getLong("ACTIVEFS1")));
                mdnDetails.setClientFS2(rs.getString("CLIENTFS2") != null ? rs.getString("CLIENTFS2") : KnGeneralUtil.convertLongToHexString(rs.getLong("CLIENTFS1")));
                mdnDetails.setOpsFS2(rs.getString("OPSFS2") != null ? rs.getString("OPSFS2") : KnGeneralUtil.convertLongToHexString(rs.getLong("OPSFS1")));
                mdnDetails.setCorpAdminFS2(rs.getString("CORPADMINFS2") != null ? rs.getString("CORPADMINFS2") : KnGeneralUtil.convertLongToHexString(rs.getLong("CORPADMINFS1")));
                mdnDetails.setSubsFS2(rs.getString("SUBSCRIBERFS2") != null ? rs.getString("SUBSCRIBERFS2") : KnGeneralUtil.convertLongToHexString(rs.getLong("SUBSCRIBERFS1")));
                mdnDetails.setXdmFS2(rs.getString("XDMSFS2") != null ? rs.getString("XDMSFS2") : KnGeneralUtil.convertLongToHexString(rs.getLong("XDMSFS1")));
                mdnDetails.setUserProfileFS2(rs.getString("USERPROFILEFS2"));
                mdnDetails.setFeatureReleaseVersion(rs.getString("FEATURE_REL_VERSION") != null ? rs.getString("FEATURE_REL_VERSION") : "0");
                mdnDetails.setUserProfileIndex(rs.getInt("USERPROFILEINDEX"));
                mdnDetailsList.add(mdnDetails);
            }
            persisterTxn.save();

        } catch (Exception e){
            persisterTxn.rollback();
            knLogger.error(methodName, "SQLException occurred - ", e);
        }finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
        }
        knLogger.info(methodName, "Returning mdnlist size  - ", mdnDetailsList.size());
        return mdnDetailsList;
    }


    protected List<KnSubsFeatureBitInfoDTO> getUnUpgradedPamIds(String licenseVersion) throws KnDAOException {
        String methodName = "getUnUpgradedPamIds(String)";
        knLogger.info(methodName, "retrieving getUnUpgradedPamIds  for license ", licenseVersion);
        List<KnSubsFeatureBitInfoDTO> mdnDetailsList = new ArrayList<>();
        KnPersisterTxn persisterTxn = KnPersisterTxn.getPersisterTxn();
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        KnSubsFeatureBitInfoDTO mdnDetails;
        try {
            String batchSize = KnGeneralUtil.getUpgradeProps().getProperty("BATCHSIZE");
            if(batchSize==null){
                batchSize= "50";
            }
            persisterTxn.open();
            String query = "SELECT FIRST ? CLIENT_TYPE, PUBLICSUBSCRIPTIONTYPE, CORPSUBSCRIPTIONTYPE, PAMACCID, CORPID, SUBSCRIBERFS, SUBSCRIBERFS2, TIER_PKG_CODE, FEATURE_REL_VERSION FROM DG.PAMSUBSCRPROFILEINFO WHERE FEATURE_REL_VERSION is NULL OR FEATURE_REL_VERSION != ?";
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);
            pStmt.setString(1, batchSize);
            pStmt.setString(2, licenseVersion);
            knLogger.info(methodName, "QUERY: Executing - ", query);
            rs = pStmt.executeQuery();
            while (rs.next()) {
                mdnDetails = new KnSubsFeatureBitInfoDTO();
                mdnDetails.setClientType(rs.getInt("CLIENT_TYPE"));
                mdnDetails.setPublicSubscriptionType(rs.getInt("PUBLICSUBSCRIPTIONTYPE"));
                mdnDetails.setCorporateSubscriptionType(rs.getInt("CORPSUBSCRIPTIONTYPE"));
                mdnDetails.setPamAccId(rs.getInt("PAMACCID"));
                mdnDetails.setCorpId(rs.getInt("CORPID"));
                mdnDetails.setSubsFS1(rs.getLong("SUBSCRIBERFS"));
                mdnDetails.setSubsFS2(rs.getString("SUBSCRIBERFS2")!=null?rs.getString("SUBSCRIBERFS2"): KnGeneralUtil.convertLongToHexString(rs.getLong("SUBSCRIBERFS")));
                mdnDetails.setTierPkgCode(rs.getString("TIER_PKG_CODE"));
                mdnDetails.setFeatureReleaseVersion(rs.getString("FEATURE_REL_VERSION")!=null? rs.getString("FEATURE_REL_VERSION"):"0");
                mdnDetailsList.add(mdnDetails);
            }
            persisterTxn.save();

        } catch (SQLException e) {
            persisterTxn.rollback();
            knLogger.error(methodName, "SQLException occurred - ", e);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
        }
        knLogger.debug(methodName, "Returning pamIds size  - ", mdnDetailsList.size());
        return mdnDetailsList;
    }


    protected List<KnCorpFeatureBitInfoDTO> getUnUpgradedCorps(String featureBitVersion) throws KnDAOException {
        String methodName = "getUnUpgradedCorps(String)";
        knLogger.info(methodName, "retrieving getUnUpgradedCops for featureBitVersion ", featureBitVersion);
        List<KnCorpFeatureBitInfoDTO> corpDetailsList = new ArrayList<>();
        KnPersisterTxn persisterTxn = KnPersisterTxn.getPersisterTxn();
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        KnCorpFeatureBitInfoDTO corpDetails;
        try {
            String batchSize = KnGeneralUtil.getUpgradeProps().getProperty("BATCHSIZE");
            if(batchSize==null){
                batchSize= "50";
            }

            persisterTxn.open();
            String query = "SELECT FIRST ? CORPID, CORPFS1, CORPFS2, OPSCORPFS1, OPSCORPFS2, FEATURE_REL_VERSION, XDMCORPFS2_SET FROM DG.POCCORPINFO WHERE FEATURE_REL_VERSION is NULL OR FEATURE_REL_VERSION != ?";
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);
            pStmt.setString(1, batchSize);
            pStmt.setString(2, featureBitVersion);
            knLogger.info(methodName, "QUERY: Executing - ", query);
            rs = pStmt.executeQuery();
           while (rs.next()) {
               corpDetails = new KnCorpFeatureBitInfoDTO();
               corpDetails.setCorpId(rs.getInt("CORPID"));
               corpDetails.setCorpFs1(rs.getLong("CORPFS1"));
               corpDetails.setCorpFs2(rs.getString("CORPFS2")!=null?rs.getString("CORPFS2"): KnGeneralUtil.convertLongToHexString(rs.getLong("CORPFS1")));
               corpDetails.setOpsCorpFs1(rs.getLong("OPSCORPFS1"));
               corpDetails.setOpsCorpFs2(rs.getString("OPSCORPFS2")!=null?rs.getString("OPSCORPFS2"): KnGeneralUtil.convertLongToHexString(rs.getLong("OPSCORPFS1")));
               corpDetails.setFeatureReleaseVersion(rs.getString("FEATURE_REL_VERSION")!=null? rs.getString("FEATURE_REL_VERSION"):"0");
               String xdmCorpfs2Set = rs.getString("XDMCORPFS2_SET");
               if (xdmCorpfs2Set == null || xdmCorpfs2Set.equals("<NULL>")) {
                   xdmCorpfs2Set = KnGeneralUtil.getDefaultXDMCorpFS2Set(KnConstants.XDMCORPFS2_SET.EMERGENCY_CONF_TIMER_FEATURE.value(),true);
               }
               corpDetails.setXdmCorpfs2_set(xdmCorpfs2Set);
               corpDetailsList.add(corpDetails);
           }
            persisterTxn.save();

        } catch (SQLException e) {
            persisterTxn.rollback();
            knLogger.error(methodName, "SQLException occurred - ", e);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
        }
        knLogger.info(methodName, "Returning corp list size  - ", corpDetailsList.size());
        return corpDetailsList;
    }

    protected String getActiveCmsIpAddress() throws KnDAOException {
        String methodName = "getActiveCmsIpAddress()";
        knLogger.info(methodName, "retrieving getActiveCmsIpAddress");
        String cmsIpAddress = null;
        KnPersisterTxn persisterTxn = KnPersisterTxn.getPersisterTxn();
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        try {
            persisterTxn.open();
            String query = "SELECT IPADDRESS FROM DG.EMSINFO WHERE ISACTIVE='Y'";
            conn = persisterTxn.getDBConnection(pttServerId, false);
            pStmt = conn.prepareStatement(query);
            knLogger.info(methodName, "QUERY: Executing - ", query);
            rs = pStmt.executeQuery();
            while (rs.next()) {
                cmsIpAddress = rs.getString("IPADDRESS");
            }
            persisterTxn.save();
        } catch (SQLException e) {
            persisterTxn.rollback();
            knLogger.error(methodName, "SQLException occurred - ", e);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
        }
        knLogger.info(methodName, "Active CMS IP Address - ", cmsIpAddress);
        return cmsIpAddress;
    }

    protected void updateFeatureBitVersionInCms(String featureBitVersion) throws KnDAOException, IOException, InterruptedException {
        String methodName = "updateFeatureBitVersionInCms(String featureBitVersion)";
        String cmsIpAddress = getActiveCmsIpAddress();
        String url = "http://" + cmsIpAddress + ":8081/cms/script/config";
        knLogger.info(methodName, " url: ", url);
        String body = "{\"context\":\"MICROSVC_GLOBAL_ATTR_VALUE_CONFIG\",\"action\":\"MICROSVC_GLOBAL_ATTR_VALUE_CONFIG_UPDATE\"" +
                ",\"object\":{},\"actionObject\":[{\"PARAMNAME\":\"SRC_FEATURE_REL_VERSION\",\"CLUSTERID\":2," +
                "\"PARAMVALUE\":" + featureBitVersion + "}]}";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        knLogger.info(methodName, "Request sending to CMS: " + request);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int responseCode = response.statusCode();
        String responseBody = response.body();
        knLogger.info(methodName, "response: ", response);
        knLogger.info(methodName, "Response Code: ", responseCode);
        knLogger.info(methodName, "Response Body: ", responseBody);
        //response: , (POST http://10.7.0.10:8081/cms/script/config) 200
        //Response Body: , {"type":"MICROSVC_GLOBAL_ATTR_VALUE_CONFIG","ver":"1.0","code":"500","message":"Internal Server Error","data":null}
        if (responseCode == 200) {
            JSONParser parser = new JSONParser();
            try {
                // Parsing the response body to check the status code
                JSONObject jsonResponse = (JSONObject) parser.parse(responseBody);
                int statusCode = Integer.parseInt((String) jsonResponse.get("code"));
                knLogger.info(methodName, "Actual Response Code from body: ", statusCode);
                if (statusCode == 200) {
                    knLogger.info(methodName, "Feature bit version updated in CMS Success");
                    KnAlarmGeneratorUtil.generateAlarm(KnAlarmConstants.ALARM_UPDATE_SRC_FEATURE_REL_VERSION, KnAlarmConstants.SEVERITY_CLEAR,
                            ALARM_MOCLASSTYPE, "KnUpdateFeatureBitJob");
                } else {
                    knLogger.error(methodName, "Feature bit version updated in CMS Failed");
                    KnAlarmGeneratorUtil.generateAlarm(KnAlarmConstants.ALARM_UPDATE_SRC_FEATURE_REL_VERSION, KnAlarmConstants.SEVERITY_CRITICAL,
                            ALARM_MOCLASSTYPE, "KnUpdateFeatureBitJob");
                }
            } catch (ParseException e) {
                knLogger.error(methodName, "ParseException occurred - ", e);
            }
        }
    }

    protected void update(String mdn, String version) throws KnDAOException{
        String methodName = "update(String)";
        knLogger.info(methodName, "updateRTXEnvValue rtx value  - ", KnGDPRTemplate.mdn(mdn));
        KnPersisterTxn persisterTxn = KnPersisterTxn.getPersisterTxn();
        Connection conn;
        PreparedStatement pStmt = null;
        try {
            persisterTxn.open();
            String query = "UPDATE DG.POCSUBSCRINFO set FEATURE_REL_VERSION = ? where MDN = ?";
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA,false);
            pStmt = conn.prepareStatement(query);
            knLogger.info(methodName, "QUERY: Executing - ", query);
            pStmt.setString(1, version);
            pStmt.setString(2, mdn);
            pStmt.executeUpdate();
            persisterTxn.save();
        } catch (SQLException e) {
            persisterTxn.rollback();
            knLogger.error(methodName, "SQLException occurred - ", e);

        } finally {
            KnDbUtil.closeStatement(pStmt);
        }
        knLogger.debug(methodName, "Returning value  - ", KnGDPRTemplate.mdn(mdn));
    }

    /*protected String getCurrentXDMLicenseVersion() throws KnDAOException {
        String methodName = "getCurrentXDMLicenseVersion()";
        knLogger.info(methodName, "retrieving Current XDM License Version");
        if (licenseVersion != null) {
            return licenseVersion;
        }
        String xdmLicenseVersion = null;
        KnPersisterTxn persisterTxn = KnPersisterTxn.getPersisterTxn();
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        try {
            persisterTxn.open();
            String query = "SELECT VERSION FROM DG.LICENSEINFO where VERSION = (select DG_VERSION from dg.signalingcardinfo a,DG.SIGNALINGCARDADDLINFO b where a.signalingcardid=b.signalingcardid and a.ipaddress=?)";
            String localIPAddress = System.getenv(KnConstants.LOCAL_IP_ADDRESS).trim();
            conn = persisterTxn.getDBConnection(pttServerId, false);
            pStmt = conn.prepareStatement(query);
            pStmt.setString(1, localIPAddress);
            knLogger.info(methodName, "QUERY: Executing - ", query);
            rs = pStmt.executeQuery();
            knLogger.info(methodName, "QUERY: Executed - ");

            while (rs.next()) {
                licenseVersion = rs.getString("VERSION");
            }
            persisterTxn.save();

        } catch (SQLException e) {
            persisterTxn.rollback();
            knLogger.error(methodName, "SQLException occurred - ", e);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
        }
        knLogger.debug(methodName, "Returning license version size  - ", licenseVersion);
        return licenseVersion;
    }*/

    /*protected String getCurrentXDMFeatureBitVersion() throws KnDAOException {
        String methodName = "getCurrentXDMFeatureBitVersion()";
        knLogger.info(methodName, "retrieving Current XDM Feature bits  Version");
        String xdmFeatureBitVersion = null;
        try {
            JSONParser  parser = new JSONParser();
            Object object = parser.parse(new FileReader(System.getProperty("activeReleaseDir")+ "/upgrade-featureset.json"));
            JSONObject jsonObject = (JSONObject) object;
            xdmFeatureBitVersion = (String) jsonObject.get("fs-current-version");

        }catch (FileNotFoundException e){

        } catch (ParseException e) {
            e.printStackTrace();
        }
        knLogger.debug(methodName, "Returning fs-current-version version  - ", xdmFeatureBitVersion);
        return xdmFeatureBitVersion;
    }*/
}
