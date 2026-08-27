/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.resources;

import com.kodiak.common.commdto.common.KnXDMCorpInfo;
import com.kodiak.common.commdto.common.KnXDMSubsProvDTO;
import com.kodiak.common.commdto.request.KnXDMPAMAccInfoDTO;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnDbUtil;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * This is used to make a call to the database
 * Created by abhishek on 10/3/16.
 */
public class KnGeneralProfileUtil {

    private static final KnLogger knLogger = KnLogger.getLogger(KnGeneralProfileUtil.class);

    private static final String MDN = "MDN";
    private static final String SUBSCR_NAME = "SUBSCRNAME";
    private static final String PUBLIC_SUBSCRIPTION_TYPE = "PUBLICSUBSCRIPTIONTYPE";
    private static final String CORP_SUBSCRIPTION_TYPE = "CORPSUBSCRIPTIONTYPE";
    private static final String CORP_ID = "CORPID";
    private static final String CORP_CONTACT_PAIRING_IND = "CORPCONTACTPAIRINGIND";
    private static final String IMEI = "IMEI";
    private static final String PAY_TYPE = "PAYTYPE";
    private static final String AFFILIATE_ID = "AFFLIATEID";
    private static final String EMAIL = "EMAIL";
    private static final String USER_AGENT = "USERAGENT";
    private static final String CLIENT_TYPE = "CLIENT_TYPE";
    private static final String DISPATCH_GRP_MEMBER = "DISPATCH_GRP_MEMBER";
    private static final String ACTIVE_FS1 = "ACTIVEFS1";
    private static final String CLIENT_FS1 = "CLIENTFS1";
    private static final String SUBS_FS1 = "SUBSCRIBERFS1";
    private static final String ACCOUNT_ID = "ACCOUNT_ID";
    private static final String PAMACC_ID = "PAMACCID";
    private static final String CORPADMIN_FS1 = "CORPADMINFS1";
    private static final String LAST_ACTIVATION_TIME = "LAST_ACTIVATION_TIME";
    private static final String UFMI = "UFMI";
    private static final String ADDLINFO = "ADDLINFO";
    private static final String EXTCORPID = "EXTCORPID";
    private static final String CORP_HIERARCHY = "CORP_HIERARCHY";
    private static final String PAMACCSTATE = "PAMACCSTATE";
    private static final String EXTERNALPAMACCID = "EXTERNALPAMACCID";
    private static final String BILLINGMDN = "BILLINGMDN";
    private static final String MAXSUBSCRIBER = "MAXSUBSCRIBER";
    private static final String CREATIONTIME = "CREATIONTIME";
    private static final String LASTUPDATETIME = "CREATIONTIME";
    private static final String ACTIVE_FS2 = "ACTIVEFS2";
    private static final String CLIENT_FS2 = "CLIENTFS2";
    private static final String SUBS_FS2 = "SUBSCRIBERFS2";
    private static final String CORPADMIN_FS2 = "CORPADMINFS2";
    private static final String SELECT_SUBSCR_PROFILE_QRY = "SELECT MDN,SUBSCRNAME,PUBLICSUBSCRIPTIONTYPE,CORPSUBSCRIPTIONTYPE,CORPID,CORPCONTACTPAIRINGIND," +
            "IMEI,PAYTYPE,AFFLIATEID,EMAIL,USERAGENT,CLIENT_TYPE,DISPATCH_GRP_MEMBER,ACTIVEFS1,CLIENTFS1,SUBSCRIBERFS1,ACCOUNT_ID,PAMACCID,CORPADMINFS1," +
            "LAST_ACTIVATION_TIME,ADDLINFO,ACTIVEFS2,CLIENTFS2,SUBSCRIBERFS2,CORPADMINFS2 FROM DG.POCSUBSCRINFO WHERE MDN= ?";

    private static final String SELECT_CORP_PROFILE_QRY = "SELECT CORPID,EXTCORPID,CORP_HIERARCHY,CORPFS2,XDMCORPFS2_SET FROM DG.POCCORPINFO WHERE EXTCORPID = ?";

    private static final String SELECT_CORP_PROFILE_BY_CORPID_QRY = "SELECT CORPID,EXTCORPID,CORP_HIERARCHY  FROM DG.POCCORPINFO WHERE CORPID= ?";

    private static final String SELECT_PAMACC_PROFILE_QRY = "SELECT PAMACCID,PAMACCSTATE,EXTERNALPAMACCID,BILLINGMDN,MAXSUBSCRIBER,CREATIONTIME,CREATIONTIME," +
            "CORP_HIERARCHY FROM DG.PAMACCOUNTINFO WHERE EXTERNALPAMACCID= ? ";

    private static final String SELECT_CORP_PROFILE_BY_NAME_QRY = "SELECT CORPID,EXTCORPID,CORP_HIERARCHY,CORPNAME FROM DG.POCCORPINFO WHERE CORPNAME = ?";

    /**
     * common method to retrieve the pttId and the connection through it
     *
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
	public static Connection getDBConnection(KnPersisterTxn persisterTxn) throws KnDAOException {
		String methodName = " getDBConnection(KnPersisterTxn )";
		String pttId;
		Connection conn;

		pttId = KnDbUtil.getDBConfigInfo().getLocalPttId();
		conn = persisterTxn.getDBConnection(pttId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
		knLogger.debug(methodName, "local  pttId ", pttId);

		return conn;
	}

	/**
     * This will be common Method to validate  both corporate and Mdn Hierarchy to validate the  hierarchy type against the hierarchy type fetched for the Interface
     *
     * @param interfaceHierarchyType
     * @param dbResponseHierarchyType
     * @return
     * @throws KnDAOException
     */
    public static boolean validateHierarchy(KnConstants.HIERARCHY_TYPE interfaceHierarchyType, KnConstants.HIERARCHY_TYPE dbResponseHierarchyType) throws KnDAOException {
        String methodName = "validateHierarchy(KnConstants.HIERARCHY_TYPE,KnConstants.HIERARCHY_TYPE)";
        knLogger.debug(methodName, "interfaceHierarchyType :", interfaceHierarchyType, "dbResponseHierarchyType :", dbResponseHierarchyType);
        boolean isValidHierarchy = false;
        if (interfaceHierarchyType == dbResponseHierarchyType) {
            isValidHierarchy = true;
        }
        return isValidHierarchy;
    }

    public static boolean validateMDNCCAndHierarchy(String mdn, KnConstants.HIERARCHY_TYPE hierarchyType) throws KnDAOException {
        String methodName = "validateMDNHierarchy(String ,KnConstants.HIERARCHY_TYPE)";
        knLogger.debug(methodName, "Mdn :", KnGDPRTemplate.mdn(mdn),  " hierarchyType:", hierarchyType);
        boolean isValid = false;
        KnXDMSubsProvDTO xdmSubsProvDTO = getSubscriberDetails(mdn);
        if (null != xdmSubsProvDTO) {
        	KnConstants.HIERARCHY_TYPE corpHierarchyType = xdmSubsProvDTO.getHierarchyType();
        	knLogger.info(methodName, " corpHierarchyType:", corpHierarchyType);
            if (null == corpHierarchyType) {
                corpHierarchyType = KnConstants.HIERARCHY_TYPE.NON_HIERARCHY;
            }
        	if (hierarchyType.equals(corpHierarchyType)) {
        		isValid = true;
        	}
        } else {
        	knLogger.debug(methodName,"subscriber doesn't exists hence returning true");
        	isValid = true;
        }

        knLogger.debug(methodName,"isValid :", isValid);
        return isValid;
    }

    public static KnXDMSubsProvDTO getSubsDetails(String mdn) throws KnDAOException {
        return getSubscriberDetails(mdn);
    }


    public static boolean validateExtCorpCCAndHierarchy(String extCorpId, KnConstants.HIERARCHY_TYPE hierarchyType) throws KnDAOException {
        String methodName = "validateExtCorpCCAndHierarchy(String ,String, HIERARCHY_TYPE)";
        knLogger.debug(methodName, "extCorpId : ", extCorpId, "hierarchyType:", hierarchyType.value());
        boolean isValid = false;
        KnXDMCorpInfo xdmCorpInfo = getCorporateDetails(extCorpId);
        if (null != xdmCorpInfo) {
            KnConstants.HIERARCHY_TYPE corpHierarchyType = xdmCorpInfo.getHierarchyType();
            knLogger.info(methodName, "corpHierarchyType:", corpHierarchyType);
            if (null == corpHierarchyType) {
                corpHierarchyType = KnConstants.HIERARCHY_TYPE.NON_HIERARCHY;
            }
            if (hierarchyType.equals(corpHierarchyType)) {
                isValid = true;
            }
        } else {
            //if ext corpid doesn't exist in DB then return true(assuming valid corporate)
            isValid = true;
            knLogger.info(methodName,"corp doesn't exists hence returning true");

        }
        knLogger.info(methodName,"isValid :", isValid);
        return isValid;
    }

    public static boolean validateIntCorpCCAndHierarchy(int corpId, KnConstants.HIERARCHY_TYPE hierarchyType) throws KnDAOException {
        String methodName = "validateIntCorpCCAndHierarchy(int ,String, HIERARCHY_TYPE)";
        knLogger.debug(methodName, "CorpId : ", corpId, "hierarchyType:", hierarchyType.value());
        boolean isValid = false;
        KnXDMCorpInfo xdmCorpInfo = getCorporateDetails(corpId);
        if (null != xdmCorpInfo) {
            KnConstants.HIERARCHY_TYPE corpHierarchyType = xdmCorpInfo.getHierarchyType();
            knLogger.info(methodName, "corpHierarchyType:", corpHierarchyType);
            if (null == corpHierarchyType) {
                corpHierarchyType = KnConstants.HIERARCHY_TYPE.NON_HIERARCHY;
            }
            if (hierarchyType.equals(corpHierarchyType)) {
                isValid = true;
            }
        } else {
            //if ext corpid doesn't exist in DB then return true(assuming valid corporate)
            isValid = true;
            knLogger.info(methodName,"corp doesn't exists hence returning true");

        }
        knLogger.info(methodName,"isValid :", isValid);
        return isValid;
    }

    public static boolean validatePamAccCCAndHierarchy(String billingMDN, KnConstants.HIERARCHY_TYPE hierarchyType) throws KnDAOException {
        String methodName = "validateMDNHierarchy(String ,KnConstants.HIERARCHY_TYPE)";
        knLogger.debug(methodName, "billingMDN :", KnGDPRTemplate.mdn(billingMDN), " hierarchyType:", hierarchyType);
        boolean isValid = false;

        KnXDMPAMAccInfoDTO xdmpamAccInfoDTO = getPamAccountInfo(billingMDN);
        if (xdmpamAccInfoDTO != null) {
        	KnConstants.HIERARCHY_TYPE corpHierarchyType = xdmpamAccInfoDTO.getHierarchyType();
        	knLogger.info(methodName, " corpHierarchyType:", corpHierarchyType);
        	if (hierarchyType.equals(corpHierarchyType)) {
        		isValid = true;
        	}
        } else {
        	knLogger.info(methodName,"billing mdn doesn't exists hence returning true");
        	isValid = true;
        }

        knLogger.info(methodName,"isValid :", isValid);
        return isValid;
    }

    /**
     * This method will be used to  retrieve the  integer hierarchy type.
     *
     * @param billingMDN
     * @return
     * @throws KnDAOException
     */
    public static KnXDMPAMAccInfoDTO getPamAccountInfo(String billingMDN) throws KnDAOException {
        String methodName = "getPamAccountInfo(String)";
        knLogger.debug(methodName, "retrieving country code using the billing MDN", KnGDPRTemplate.mdn(billingMDN));
        KnPersisterTxn persisterTxn = null;
        Connection conn = null;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        KnXDMPAMAccInfoDTO knXDMPAMAccInfoDTO = null;
        try {
            persisterTxn = KnPersisterTxn.getPersisterTxn();
            persisterTxn.open();
            conn = getDBConnection(persisterTxn);
            pStmt = conn.prepareStatement(SELECT_PAMACC_PROFILE_QRY);
            pStmt.setString(1, billingMDN);
            knLogger.debug(methodName, "QUERY: Executing - ", SELECT_PAMACC_PROFILE_QRY);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "QUERY: Executed - ");
            while (rs.next()) {
                knXDMPAMAccInfoDTO = new KnXDMPAMAccInfoDTO();
                knXDMPAMAccInfoDTO.setPamAccId(rs.getInt(PAMACC_ID));
                knXDMPAMAccInfoDTO.setBillingNumber(rs.getString(EXTERNALPAMACCID));
                knXDMPAMAccInfoDTO.setLastUpdateTime(rs.getLong(LASTUPDATETIME));
                knXDMPAMAccInfoDTO.setPamAccState(rs.getInt(PAMACCSTATE));
                knXDMPAMAccInfoDTO.setSubsCount(rs.getInt(MAXSUBSCRIBER));
                knXDMPAMAccInfoDTO.setCreationTime(rs.getLong(CREATIONTIME));
                knXDMPAMAccInfoDTO.setHierarchyType(KnConstants.HIERARCHY_TYPE.validate(rs.getInt(CORP_HIERARCHY)));
            }
            persisterTxn.save();
        } catch (Exception e) {
            KnDbUtil.rollback(persisterTxn);
            knLogger.error(methodName, "Exception occurred - ", e);
            throw KnDbUtil.processException(e, "Failed to select subscriber Profile - " + e.getMessage(), null, "DG.PAMACCOUNTINFO", SELECT_PAMACC_PROFILE_QRY);

        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
        }
        knLogger.debug(methodName, "Returning value - ", knXDMPAMAccInfoDTO);
        return knXDMPAMAccInfoDTO;
    }


    /**
     * This will be used to fetch the hierarchy based on internal corpId
     *
     * @param corpId
     * @return
     * @throws KnDAOException
     */
    public static KnXDMCorpInfo getCorporateDetails(int corpId) throws KnDAOException {
        String methodName = "getCorporateDetails(int corpId)";
        knLogger.info(methodName, "retrieving corp profile corp Id:", corpId);
        KnPersisterTxn persisterTxn = null;
        Connection conn = null;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        KnXDMCorpInfo knXDMCorpInfo = null;
        try {
            persisterTxn = KnPersisterTxn.getPersisterTxn();
            persisterTxn.open();
            conn = getDBConnection(persisterTxn);
            pStmt = conn.prepareStatement(SELECT_CORP_PROFILE_BY_CORPID_QRY);
            pStmt.setInt(1, corpId);
            knLogger.debug(methodName, " Executing query -", SELECT_CORP_PROFILE_BY_CORPID_QRY);
            rs = pStmt.executeQuery();
            while (rs.next()) {
                knXDMCorpInfo = new KnXDMCorpInfo();
                knXDMCorpInfo.setCorpId(String.valueOf(rs.getInt(CORP_ID)));
                knXDMCorpInfo.setExtCorpId(rs.getString(EXTCORPID));
                knXDMCorpInfo.setHierarchyType(KnConstants.HIERARCHY_TYPE.validate(rs.getInt(CORP_HIERARCHY)));

            }
            persisterTxn.save();
            knLogger.debug(methodName, " countryCode for corpId from DG.POCCORPINFO :", knXDMCorpInfo);
        } catch (Exception e) {
            KnDbUtil.rollback(persisterTxn);
            knLogger.error(methodName, "Exception occurred - ", e);
            throw KnDbUtil.processException(e, "Failed to select corporate Profile - " + e.getMessage(), null, "DG.POCCORPINFO", SELECT_CORP_PROFILE_BY_CORPID_QRY);

        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
        }
        knLogger.debug(methodName, "get corp profile for internal corpId : ", knXDMCorpInfo);
        return knXDMCorpInfo;
    }



    /**
     * This is to retrieve the hierarchy from the corporate level
     *
     * @param extCorpId
     * @return
     * @throws KnDAOException
     */
    public static KnXDMCorpInfo getCorporateDetails(String extCorpId) throws KnDAOException {
        String methodName = "getCorporateDetails(String)";
        knLogger.info(methodName, "Retrieve country from DG.POCCORPINFO where extCorpId:", extCorpId);
        KnPersisterTxn persisterTxn = null;
        Connection conn = null;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        KnXDMCorpInfo knXDMCorpInfo = null;//= new KnXDMCorpInfo();
        try {
            persisterTxn = KnPersisterTxn.getPersisterTxn();
            persisterTxn.open();
            conn = getDBConnection(persisterTxn);
            pStmt = conn.prepareStatement(SELECT_CORP_PROFILE_QRY);
            pStmt.setString(1, extCorpId);
            knLogger.debug(methodName, "Executing  query", SELECT_CORP_PROFILE_QRY);
            rs = pStmt.executeQuery();
            while (rs.next()) {
                knXDMCorpInfo = new KnXDMCorpInfo();
                knXDMCorpInfo.setCorpId(String.valueOf(rs.getInt("CORPID")));
                knXDMCorpInfo.setExtCorpId(rs.getString("EXTCORPID"));
                knXDMCorpInfo.setHierarchyType(KnConstants.HIERARCHY_TYPE.validate(rs.getInt("CORP_HIERARCHY")));
                knXDMCorpInfo.setCorpFS2(rs.getString("CORPFS2"));
                knXDMCorpInfo.setXdmCorpFS2Set(rs.getString("XDMCORPFS2_SET"));
            }
            persisterTxn.save();
        } catch (Exception e) {
            KnDbUtil.rollback(persisterTxn);
            knLogger.error(methodName, "Exception occurred - ", e);
            throw KnDbUtil.processException(e, "Failed to select corporate Profile - " + e.getMessage(), null, "DG.POCCORPINFO", SELECT_CORP_PROFILE_QRY);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
        }
        knLogger.debug(methodName, "Returning CorpInfo value for DG.POCCORPINFO  ", knXDMCorpInfo);
        return knXDMCorpInfo;
    }

    /**
     * This is to retrieve the MDN Hierarchy
     *
     * @param mdn
     * @return
     * @throws KnDAOException
     */
    public static KnXDMSubsProvDTO getSubscriberDetails(String mdn) throws KnDAOException {
        String methodName = "getSubscriberDetails(String)";
        knLogger.debug(methodName, "retrieving subscriber details -  ", KnGDPRTemplate.mdn(mdn));
        KnPersisterTxn persisterTxn = null;
        Connection conn = null;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        KnXDMSubsProvDTO knXDMSubsProvDTO = null;
        try {
            persisterTxn = KnPersisterTxn.getPersisterTxn();
            persisterTxn.open();
            conn = getDBConnection(persisterTxn);
            pStmt = conn.prepareStatement(SELECT_SUBSCR_PROFILE_QRY);
            pStmt.setString(1, mdn);
            knLogger.debug(methodName, "Executing query - ", SELECT_SUBSCR_PROFILE_QRY);
            rs = pStmt.executeQuery();
            while (rs.next()) {
                knXDMSubsProvDTO = new KnXDMSubsProvDTO();
                knXDMSubsProvDTO.setMdn(rs.getString(MDN));
                //multilingual revert changes
                if(rs.getString(SUBSCR_NAME) !=null){
                	knXDMSubsProvDTO.setNetworkName(new String(rs.getString(SUBSCR_NAME).getBytes("8859_1"),"UTF-8"));
                }
                knXDMSubsProvDTO.setPublicSubscriptionType(rs.getInt(PUBLIC_SUBSCRIPTION_TYPE));
                knXDMSubsProvDTO.setCorporateSubscriptionType(rs.getInt(CORP_SUBSCRIPTION_TYPE));
                knXDMSubsProvDTO.setCorpId(String.valueOf(rs.getInt(CORP_ID)));
                String imei = rs.getString(IMEI);
                if (imei != null) {
                    imei = imei.trim();
                }
                knXDMSubsProvDTO.setIMEI(imei);
                knXDMSubsProvDTO.setAffiliateId(rs.getString(AFFILIATE_ID));
                knXDMSubsProvDTO.setPayType(rs.getInt(PAY_TYPE));
                knXDMSubsProvDTO.setUserAgent(rs.getString(USER_AGENT));
                knXDMSubsProvDTO.setEmailAddress(rs.getString(EMAIL));
                knXDMSubsProvDTO.setSubscriberClientType(rs.getInt(CLIENT_TYPE));
                knXDMSubsProvDTO.setDispatchGroupMember(rs.getInt(DISPATCH_GRP_MEMBER));
                knXDMSubsProvDTO.setAccountId(rs.getString(ACCOUNT_ID));
                knXDMSubsProvDTO.setPamAccId(rs.getInt(PAMACC_ID));
                knXDMSubsProvDTO.setLastActivationTime(rs.getLong(LAST_ACTIVATION_TIME));
                knXDMSubsProvDTO.setHierarchyType(KnConstants.HIERARCHY_TYPE.validate(rs.getInt(ADDLINFO)));
                String subsFs2=rs.getString(SUBS_FS2)!=null?rs.getString(SUBS_FS2):KnGeneralUtil.convertLongToHexString(rs.getLong(SUBS_FS1));
                knXDMSubsProvDTO.setSubsFS2(subsFs2);
                String clientsFs2=rs.getString(CLIENT_FS2)!=null?rs.getString(CLIENT_FS2):KnGeneralUtil.convertLongToHexString(rs.getLong(CLIENT_FS1));
                knXDMSubsProvDTO.setClientFS2(clientsFs2);
                String activeFs2=rs.getString(ACTIVE_FS2)!=null?rs.getString(ACTIVE_FS2):KnGeneralUtil.convertLongToHexString(rs.getLong(ACTIVE_FS1));
                knXDMSubsProvDTO.setActiveFS2(activeFs2);
                String corpAdminFs2=rs.getString(CORPADMIN_FS2)!=null?rs.getString(CORPADMIN_FS2):KnGeneralUtil.convertLongToHexString(rs.getLong(CORPADMIN_FS1));
                knXDMSubsProvDTO.setCorpAdminFS2(corpAdminFs2);
            }
            persisterTxn.save();

        } catch (Exception e) {
            KnDbUtil.rollback(persisterTxn);
            knLogger.error(methodName, "Exception occurred - ", e);
            throw KnDbUtil.processException(e, "Failed to select subscriber Profile - " + e.getMessage(), null, "DG.POCSUBSCRINFO", SELECT_SUBSCR_PROFILE_QRY);

        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
        }
        knLogger.debug(methodName, "select subs profile- ", knXDMSubsProvDTO);
        return knXDMSubsProvDTO;
    }
    public static KnXDMCorpInfo getCorporateDetailsByCorpName(String corpName) throws KnDAOException {
        String methodName = "getCorporateDetailsByCorpName()";
        knLogger.info(methodName, "Entry:", corpName);
        KnPersisterTxn persisterTxn = null;
        Connection conn = null;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        KnXDMCorpInfo knXDMCorpInfo = null;//= new KnXDMCorpInfo();
        try {
            persisterTxn = KnPersisterTxn.getPersisterTxn();
            persisterTxn.open();
            conn = getDBConnection(persisterTxn);
            pStmt = conn.prepareStatement(SELECT_CORP_PROFILE_BY_NAME_QRY);
            pStmt.setString(1, corpName);
            knLogger.debug(methodName, "Executing  query", SELECT_CORP_PROFILE_BY_NAME_QRY);
            rs = pStmt.executeQuery();
            while (rs.next()) {
                knXDMCorpInfo = new KnXDMCorpInfo();
                knXDMCorpInfo.setCorpId(String.valueOf(rs.getInt("CORPID")));
                knXDMCorpInfo.setExtCorpId(rs.getString("EXTCORPID"));
                knXDMCorpInfo.setHierarchyType(KnConstants.HIERARCHY_TYPE.validate(rs.getInt("CORP_HIERARCHY")));
                knXDMCorpInfo.setCorpName(rs.getString("CORPNAME"));
            }
            persisterTxn.save();
        } catch (Exception e) {
            KnDbUtil.rollback(persisterTxn);
            knLogger.error(methodName, "Exception occurred - ", e);
            throw KnDbUtil.processException(e, "Failed to select corporate Profile by name - " + e.getMessage(), null, "DG.POCCORPINFO", SELECT_CORP_PROFILE_BY_NAME_QRY);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
        }
        knLogger.info(methodName, "EXIT : ", knXDMCorpInfo);
        return knXDMCorpInfo;
    }
}
