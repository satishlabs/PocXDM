/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common.dao.persister.db.tables.xdm;

import com.kodiak.common.dao.KnConnectionException;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.dbmgr.KnDBManager;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dao.persister.db.KnDBConnectionException;
import com.kodiak.xdms.server.common.dao.persister.db.KnDBPersistenceException;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.common.KnDocChangeListDTO;
import com.kodiak.xdms.server.common.dto.common.KnEmergencyInfoDTO;
import com.kodiak.xdms.server.common.dto.common.KnPocSubsAddlInfoDTO;
import com.kodiak.xdms.server.common.dto.common.KnSubsProfileDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.dto.persistdat.KnSubsProfilePersistDTO;
import com.kodiak.xdms.server.common.resources.KnDAOSourceTypes;
import com.kodiak.xdms.server.common.resources.KnErrorCodes;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.common.commdto.common.KnXDMSubsProvDTO;
import com.kodiak.common.commdto.response.KnXDMSubsProfileRespDTO;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.util.KnGeneralUtil;
import com.kodiak.xdms.server.common.dto.common.KnCorpUserProfileMCPTTConfig;

import static com.kodiak.xdms.server.common.resources.KnConstants.PROTOCOL_VERSION_18_X;
import static com.kodiak.xdms.server.common.resources.KnConstants.USER_PROFILE_MGMT_BIT;
import static com.kodiak.xdms.server.common.util.KnGeneralUtil.formCommaSeperatedIdList;


import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMSubscriberInfoDAO.java
 * Subsystem:  common
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * SureshKumar G        Jan 12, 2011           7.0
 * <p/>
 * <p/>
 * #401, 4th Floor, 'Prestige Sigma'
 * No.3, Vittal Mallya Road
 * Bangalore - 560 001
 * www.kodiaknetworks.com
 * All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */
public class
KnXDMSubscriberInfoDAO implements ITableDAO {
    private static final KnLogger knLogger = KnLogger.getLogger(KnXDMSubscriberInfoDAO.class);

    public static final String CLASSNAME = KnXDMSubscriberInfoDAO.class.getName();
    public static final String TABLENAME = "DG.POCSUBSCRINFO";

    public static final String MDN = "MDN";
    public static final String POC_HOME = "POCHOME";
    public static final String PRESENCE_HOME = "PRESENCEHOME";
    public static final String XDMS_HOME = "XDMSHOME";

    public static final String SUBSCR_CREATION_TIME = "SUBSCRCREATIONTIME";
    public static final String LAST_PROFILE_UPDATE_TIME = "LASTPROFILEUPDATETIME";
    public static final String CORP_CONTACT_PAIRING_IND = "CORPCONTACTPAIRINGIND";
    public static final String IMEI = "IMEI";

    public static final String SUBSCR_NAME = "SUBSCRNAME";
    public static final String SERVICE_AUTH_STATUS = "SERVICEAUTHSTATUS";
    public static final String PUBLIC_SUBSCRIPTION_TYPE = "PUBLICSUBSCRIPTIONTYPE";
    public static final String CORP_SUBSCRIPTION_TYPE = "CORPSUBSCRIPTIONTYPE";

    public static final String CORP_ID = "CORPID";
    public static final String CORP_CONTACTLIST_ID = "CORPCONTACTLISTID";
    public static final String PAY_TYPE = "PAYTYPE";
    public static final String USER_AGENT = "USERAGENT";
    public static final String DERIVED_KEY="DERIVED_KEY";

    public static final String CLIENT_PASSWORD = "CLIENT_PASSWORD";
    public static final String AFFLIATE_ID = "AFFLIATEID";
    public static final String EMAIL = "EMAIL";
    public static final String CLIENT_TYPE = "CLIENT_TYPE";
    public static final String CLIENT_PV_MAJOR_VERSION = "CLIENTPV_MAJORVERSION";
    public static final String CLIENT_PV_MINOR_VERSION = "CLIENTPV_MINORVERSION";
    public static final String PAMACCID = "PAMACCID";
    public static final String DISPATCH_TYPE = "DISPATCH_TYPE";
    public static final String USER_ID = "USER_ID";
    public static final String SERVICE_AUTH_STATUS_OP = "SERVICE_STATUS_OP";
    public static final String SERVICE_AUTH_STATUS_AU = "SERVICE_STATUS_AUTHUSER";
    public static final String EMERGCALLTYPE = "EMERGCALLTYPE";
    public static final String EMERGCANCELPERMISSION = "EMERGCANCELPERMISSION";
    public static final String EMERGINITIATEPERMISSION = "EMERGINITIATEPERMISSION";
    public static final String LICENSE_TYPE = "LICENSE_TYPE";
    public static final String ALIAS_MDN = "ALIAS_MDN";
    public static final String EMERGLMRBEHAVIOUR = "EMERGLMRBEHAVIOUR";
    public static final String MCPTT_COMPLIANCE = "MCPTT_COMPLIANCE";
    public static final String MC_PTTID = "MC_PTTID";
    public static final String MC_VIDEOID = "MC_VIDEOID";
    public static final String MC_DATAID = "MC_DATAID";
    public static final String MC_ID = "MC_ID";
    public static final String MAXSIMULDEDICATEDSESSION = "MAXSIMULDEDICATEDSESSION";
    public static final String MAXSIMULDYNAMICSESSION = "MAXSIMULDYNAMICSESSION";
    public static final String UFMI = "UFMI";
    public static final String ACTIVEFS1 = "ACTIVEFS1";
    public static final String SUBSCRIBERFS1 = "SUBSCRIBERFS1";
    public static final String ACTIVEFS2 = "ACTIVEFS2";
    public static final String SUBSCRIBERFS2 = "SUBSCRIBERFS2";
    public static final String OPSFS1 = "OPSFS1";
    public static final String CLIENTFS1 = "CLIENTFS1";
    public static final String XDMSFS1 = "XDMSFS1";
    public static final String CORPADMINFS1 = "CORPADMINFS1";
    public static final String USERPROFILEFS1 = "USERPROFILEFS1";
    public static final String OPSFS2 = "OPSFS2";
    public static final String CLIENTFS2 = "CLIENTFS2";
    public static final String XDMSFS2 = "XDMSFS2";
    public static final String CORPADMINFS2 = "CORPADMINFS2";
    public static final String USERPROFILEFS2 = "USERPROFILEFS2";
    public static final String CAMERA_TYPE = "CAMERA_TYPE";
    public static final String QPPPACKID = "QPPPACKID";
    public static final String DISCREET_ENABLED = "DISCREET_ENABLED";
    public static final String ACCOUNT_ID = "ACCOUNT_ID";

    public static final String LASTPROFILEUPDATETIME = "LASTPROFILEUPDATETIME";
    public static final String USERPROFILEINDEX = "USERPROFILEINDEX";
    public static final String ISDEFAULTPROFILE = "ISDEFAULTPROFILE";
    public static final String USERPROFILEID = "USERPROFILEID";
    public static final String EMERGCONFTIME   = "EMERGCONFTIME";
    public static final String AUTO_ASSIGN_TRACKER = "AUTO_ASSIGN_TRACKER";
    public static final String HIERARCHY_ID = "HIERARCHY_ID";

    public String pttServerId = null;

    public KnXDMSubscriberInfoDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }


    public static final String QRY_SELECT_SUBS_INFO = "SELECT " + SUBSCR_NAME  + ", " + SERVICE_AUTH_STATUS
            + ", " + PUBLIC_SUBSCRIPTION_TYPE + ", " + CORP_SUBSCRIPTION_TYPE + ", " + POC_HOME + ", " + PRESENCE_HOME + ", " +
            XDMS_HOME + ", " + LAST_PROFILE_UPDATE_TIME + ", " + CORP_CONTACTLIST_ID + ", " + CORP_ID + ", " + EMAIL + ", " +
            CLIENT_TYPE+ ", " + CLIENT_PV_MAJOR_VERSION+ ", " + CLIENT_PV_MINOR_VERSION + ", " + ACTIVEFS1 + ", " + PAMACCID
            + ", " + DISPATCH_TYPE + ", " + USER_ID + "," + CLIENT_PASSWORD  +  ", " + USER_AGENT + ", " + SERVICE_AUTH_STATUS_OP
            + ", " + SERVICE_AUTH_STATUS_AU + ", " + EMERGCALLTYPE + ", " + EMERGCANCELPERMISSION + ", " + EMERGINITIATEPERMISSION
            + ", "+ SUBSCRIBERFS1 + ", "+ LICENSE_TYPE + ", "+ ALIAS_MDN + "," + EMERGLMRBEHAVIOUR + "," + MCPTT_COMPLIANCE
            + "," + MC_PTTID + "," + MC_VIDEOID + "," + MC_DATAID + "," + MC_ID + "," + MAXSIMULDEDICATEDSESSION + ","
            + MAXSIMULDYNAMICSESSION +","+ UFMI+ ", " + ACTIVEFS2+ ", "+ SUBSCRIBERFS2
            + ", "+ QPPPACKID +  ", "+ DISCREET_ENABLED +", "+ USERPROFILEINDEX + ", " + USERPROFILEID + ","+ DERIVED_KEY+ ","
            + CLIENTFS1 +"," + CLIENTFS2 +"," +OPSFS1 +"," +OPSFS2 +"," +XDMSFS1 +"," +XDMSFS2 +"," +CORPADMINFS1 +"," + CORPADMINFS2
            + "," +USERPROFILEFS2+ "," +CAMERA_TYPE + "," +ACCOUNT_ID +"," +EMERGCONFTIME +" FROM " + TABLENAME + " WHERE " + MDN + " = ?";

    public static final String QRY_SELECT_SUBS_INFO_BY_MCPTTID = "SELECT " + SUBSCR_NAME  + ", " + SERVICE_AUTH_STATUS
            + ", " + PUBLIC_SUBSCRIPTION_TYPE + ", " + CORP_SUBSCRIPTION_TYPE + ", " + POC_HOME + ", " + PRESENCE_HOME + ", " +
            XDMS_HOME + ", " + LAST_PROFILE_UPDATE_TIME + ", " + CORP_CONTACTLIST_ID + ", " + CORP_ID + ", " + EMAIL + ", " +
            CLIENT_TYPE+ ", " + CLIENT_PV_MAJOR_VERSION+ ", " + CLIENT_PV_MINOR_VERSION + ", " + ACTIVEFS1 + ", " + PAMACCID
            + ", " + DISPATCH_TYPE + ", " + USER_ID + "," + CLIENT_PASSWORD  +  ", " + USER_AGENT + ", " + SERVICE_AUTH_STATUS_OP
            + ", " + SERVICE_AUTH_STATUS_AU + ", " + EMERGCALLTYPE + ", " + EMERGCANCELPERMISSION + ", " + EMERGINITIATEPERMISSION
            + ", "+ SUBSCRIBERFS1 + ", "+ LICENSE_TYPE + ", "+ ALIAS_MDN + "," + EMERGLMRBEHAVIOUR + "," + MCPTT_COMPLIANCE
            + "," + MC_PTTID + "," + MC_VIDEOID + "," + MC_DATAID + "," + MC_ID + "," + MAXSIMULDEDICATEDSESSION + "," + MAXSIMULDYNAMICSESSION +","+ UFMI+ ", " + ACTIVEFS2+ ", "+ SUBSCRIBERFS2  + ", "+ QPPPACKID + ", "+ MDN + ","
            + CLIENTFS1 +"," + CLIENTFS2 +"," +OPSFS1 +"," +OPSFS2 +"," +XDMSFS1 +"," +XDMSFS2 +"," +CORPADMINFS1 +"," + CORPADMINFS2 + "," +USERPROFILEFS2+ " FROM " + TABLENAME + " WHERE " + MC_PTTID + " = ?";

    public static final String QRY_SELECT_SUBS_INFO_LIST_BY_MCID = "SELECT " + MDN  + ", " + LASTPROFILEUPDATETIME
            + "," + USERPROFILEINDEX+ "," + MC_PTTID+ "," +SUBSCR_NAME+", " + PUBLIC_SUBSCRIPTION_TYPE+ ", "
            + SERVICE_AUTH_STATUS+ ", " +MC_ID+ ", " +ISDEFAULTPROFILE+ ", " +CORP_ID+ " FROM " + TABLENAME + " WHERE " + MC_ID + " = ?";

    public static final String QRY_SELECT_SUBS_INFO_BY_MCID = "SELECT " + SUBSCR_NAME  + ", " + SERVICE_AUTH_STATUS
            + ", " + PUBLIC_SUBSCRIPTION_TYPE + ", " + CORP_SUBSCRIPTION_TYPE + ", " + POC_HOME + ", " + PRESENCE_HOME + ", " +
            XDMS_HOME + ", " + LAST_PROFILE_UPDATE_TIME + ", " + CORP_CONTACTLIST_ID + ", " + CORP_ID + ", " + EMAIL + ", " +
            CLIENT_TYPE+ ", " + CLIENT_PV_MAJOR_VERSION+ ", " + CLIENT_PV_MINOR_VERSION + ", " + ACTIVEFS1 + ", " + PAMACCID
            + ", " + DISPATCH_TYPE + ", " + USER_ID + "," + CLIENT_PASSWORD  +  ", " + USER_AGENT + ", " + SERVICE_AUTH_STATUS_OP
            + ", " + SERVICE_AUTH_STATUS_AU + ", " + EMERGCALLTYPE + ", " + EMERGCANCELPERMISSION + ", " + EMERGINITIATEPERMISSION
            + ", "+ SUBSCRIBERFS1 + ", "+ LICENSE_TYPE + ", "+ ALIAS_MDN + "," + EMERGLMRBEHAVIOUR + "," + MCPTT_COMPLIANCE
            + "," + MC_PTTID + "," + MC_VIDEOID + "," + MC_DATAID + "," + MC_ID + "," + MAXSIMULDEDICATEDSESSION + "," + MAXSIMULDYNAMICSESSION +","+ UFMI+ ", " + ACTIVEFS2+ ", "+ SUBSCRIBERFS2  + ", "+ QPPPACKID + ", "+ MDN + ","
            + USERPROFILEID +","+ CLIENTFS1 +"," + CLIENTFS2 +"," +OPSFS1 +"," +OPSFS2 +"," +XDMSFS1 +"," +XDMSFS2 +"," +CORPADMINFS1 +"," + CORPADMINFS2 + "," +USERPROFILEFS2+ "," + CAMERA_TYPE + ","+ USERPROFILEINDEX + "," + EMERGCONFTIME +","+HIERARCHY_ID+ " FROM " + TABLENAME + " WHERE " + MC_ID + " = ?";


    public static final String QRY_SELECT_SUBS_PV = "SELECT " + CLIENT_PV_MAJOR_VERSION + " FROM " + TABLENAME + " WHERE " + MDN + " = ?";

    private static final String QRY_SELECT_SUBSCRIBERS_PV =  "SELECT "+ MDN + "," + CLIENT_PV_MAJOR_VERSION + " FROM " + TABLENAME + " WHERE " + MDN + " IN ";
    
    private static final String QRY_SELECT_REAL_MDNS_PROFILEMDN ="SELECT DISTINCT A.MDN, B.MDN FROM DG.POCSUBSCRINFO A ,DG.POCSUBSCRINFO B WHERE A.MC_ID = B.MC_ID AND (A.USERPROFILEINDEX=0 OR A.USERPROFILEINDEX IS NULL) AND B.MDN IN ";
    
    private static final String QRY_SELECT_MDN_WHERE_MCID_IS_NULL ="SELECT FIRST 1000(MDN) FROM DG.POCSUBSCRINFO WHERE MC_ID IS NULL";
    
    private static final String UPDATE_QRY = "UPDATE " + TABLENAME + " SET ";

    private static final String GET_SUBS_MCID_UPDTS_BY_MDNS = "SELECT MDN,MC_ID,LASTPROFILEUPDATETIME,MCPTT_COMPLIANCE,USERPROFILEINDEX,CLIENTPV_MAJORVERSION,ACTIVEFS2 FROM DG.POCSUBSCRINFO WHERE MDN IN ";

    private static final String UPDATE_SUBS_PROFILE_LAST_UPDATE_TIME = "UPDATE DG.POCSUBSCRINFO SET LASTPROFILEUPDATETIME = ? WHERE MDN = ?";

    private static final String QRY_SELECT_REAL_MDNS = "SELECT DISTINCT A.MDN FROM DG.POCSUBSCRINFO A, DG.POCSUBSCRINFO B WHERE A.MC_ID = B.MC_ID AND nvl(A.USERPROFILEINDEX, 0) = 0 AND B.MDN IN ";

    private static final String QRY_SELECT_BASE_MDNS_MAP = "SELECT B.MDN, A.MDN FROM DG.POCSUBSCRINFO A, DG.POCSUBSCRINFO B WHERE A.MC_ID = B.MC_ID AND nvl(A.USERPROFILEINDEX, 0) = 0 AND B.MDN IN ";

    private static final String MCPTT_LIST = "MCPTTLIST";

    public static final String SELECT_PROFILEIDMDN_QUERY ="select "+MC_PTTID+","+USERPROFILEINDEX+","+MDN+" from DG.POCSUBSCRINFO WHERE "+MC_PTTID+" IN ( "+ MCPTT_LIST+" )";

    private static final String QRY_SELECT_USER_PROFILE_NAME = "SELECT USER_PROFILE_NAME FROM  DG.POCSUBSCR_ADDLINFO WHERE MDN = ? ";

    private static final String USER_PROFILE_NAME = "USER_PROFILE_NAME";

    private static final String QRY_UPDATE_USER_PROFILE_NAME = "UPDATE DG.POCSUBSCR_ADDLINFO SET USER_PROFILE_NAME = ? WHERE MDN = ? ";

    private static final String FIRSTNET_SEGMENT_ENABLED_SUBSCRIBER = "SELECT MDN,SEGMENT_INDICATOR FROM DG.POCSUBSCRINFO WHERE SEGMENT_INDICATOR IS NOT NULL;";

    private static final String SELECT_FAN_ID = "SELECT FAN_ID,MDN FROM DG.SUBSCRIBER_ADDLINFO WHERE MDN IN ";

    private static final String UPDATE_SEGMENT_INDICATOR = "UPDATE DG.FAN_DETAILS SET SEGMENT_INDICATOR = ? WHERE FAN_ID = ?";

    private static final String SELECT_BASE_MDN = "SELECT MDN FROM DG.POCSUBSCRINFO WHERE (";

    private static final String QRY_SELECT_DISP_SUBSCRIBERS = "SELECT " + MDN + " FROM " + TABLENAME + " WHERE CLIENT_TYPE IN (3,12) AND " + MDN + " IN ";

    private static final String QRY_UPDATE_AUTO_ASSIGN_TRACKER  = "UPDATE DG.POCSUBSCR_ADDLINFO SET AUTO_ASSIGN_TRACKER = ? WHERE MDN = ? ";
    private static final String QRY_SELECT_AUTO_ASSIGN_TRACKER  = "SELECT MDN, AUTO_ASSIGN_TRACKER FROM DG.POCSUBSCR_ADDLINFO WHERE MDN IN ";



    /**
     * This method is used for insertion of data in SQL tables.
     *
     * @param persistenceDTO
     * @param persisterTxn
     */
    public void insert(IPersistenceDTO persistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        knLogger.warn( "insert", "Not Implemented");
    }

    /**
     * This method is used for updation of data from SQL tables.
     *
     * @param persistenceDTO
     * @param persisterTxn
     */
    public void update(IPersistenceDTO persistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        knLogger.warn( "update", "Not Implemented");
    }

    /**
     * This method is used for deletion of data from SQL tables.
     *
     * @param persistenceDTO
     * @param persisterTxn
     */
    public void delete(IPersistenceDTO persistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        knLogger.warn( "delete", "Not Implemented");
    }

    /**
     * This method is used for retrieving of data from SQL tables.
     *
     * @param persistenceDTO
     * @param persisterTxn
     * @return Collection
     */
    public Collection select(IPersistenceDTO persistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        knLogger.warn( "select", "Not Implemented");
        return null;
    }


    /**
     * @param mdn
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public KnSubsProfilePersistDTO selectSubscriberInfo(String mdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "selectSubscriberInfo(String, KnPersisterTxn)";
        knLogger.debug( methodName, "Entry : mdn",KnGDPRTemplate.mdn(mdn));

        boolean ownedTxn = false;
        Connection conn = null;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;

        KnSubsProfilePersistDTO subsPersistDTO = null;

        try {
            if (persisterTxn != null) {
                knLogger.debug(methodName, " Data Store before - ", KnDBConst.DataStores.XDM_SHARED_DATA);
                conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            } else {
                conn = KnDBManager.getDBManagerInstance().getConnection(KnGenInfoUtil.getSecDBPttServerId(pttServerId), true);
                ownedTxn = true;
            }
            query = QRY_SELECT_SUBS_INFO;
            //  conn = persisterTxn.getDBConnection(pttServerId, false);
            knLogger.debug(methodName, " Data Store before - ", KnDBConst.DataStores.XDM_SHARED_DATA);
              knLogger.debug( methodName, " Data Store After - ", KnDBConst.DataStores.XDM_SHARED_DATA);
            pStatement = conn.prepareStatement(query);
            pStatement.setString(1, mdn);
            knLogger.debug( methodName, "QUERY : Executing ", query, ", persisterTxn : ",
                    persisterTxn);
            rs = pStatement.executeQuery();
            knLogger.debug( methodName, "QUERY : Completed.");

            if (rs.next()) {
                subsPersistDTO = new KnSubsProfilePersistDTO();

                subsPersistDTO.setMdn(mdn);
                //multilingual revert changes
                if(rs.getString(1) != null)
                {
                    subsPersistDTO.setNetworkName(new String(rs.getString(1).getBytes("8859_1"),"UTF-8"));
                }
                subsPersistDTO.setServiceAuthStatus(rs.getInt(2));
                subsPersistDTO.setPublicSubscriptionType(rs.getInt(3));
                subsPersistDTO.setCorpSubscriptionType(rs.getInt(4));
                subsPersistDTO.setPocHome(rs.getString(5));
                subsPersistDTO.setPresenceHome(rs.getString(6));
                subsPersistDTO.setXdmsHome(rs.getString(7));
                subsPersistDTO.setProfileLastUpdated(rs.getLong(8));
                subsPersistDTO.setContactListId(rs.getInt(9));
                subsPersistDTO.setCorpId(rs.getInt(10));
                subsPersistDTO.setSubscriberEmail(rs.getString(11));
                subsPersistDTO.setClientType(rs.getInt(12));
                subsPersistDTO.setProtocolVersion(rs.getInt(13)+"."+rs.getInt(14));
                subsPersistDTO.setClientMajorVersion(rs.getInt(13));
                subsPersistDTO.setPamAccId(rs.getInt(16));
                subsPersistDTO.setDispatchType(rs.getInt(17));
                subsPersistDTO.setUserId(rs.getString(18));
                subsPersistDTO.setClientPassowrd(rs.getString(19));
                subsPersistDTO.setUserAgent(rs.getString(20));
                subsPersistDTO.setServiceAuthStatusOP(rs.getInt(21));
                subsPersistDTO.setServiceAuthStatusAU(rs.getInt(22));
                subsPersistDTO.setEmergCallType(rs.getInt(23));
                subsPersistDTO.setEmergCancelPermission(rs.getInt(24));
                subsPersistDTO.setEmergInitiatePermission(rs.getInt(25));
                subsPersistDTO.setLicenseType(rs.getInt(27));
                if(rs.getString(28) != null) subsPersistDTO.setAliasMdn(rs.getString(28).trim());
                subsPersistDTO.setEmergLmrBehaviour(rs.getInt(29));
                subsPersistDTO.setMcpttCompliance(rs.getInt(30));
                if (null != rs.getString(MC_PTTID)) {
                    subsPersistDTO.setMcpttId(new String(rs.getBytes(MC_PTTID), StandardCharsets.UTF_8));
                    }
                if (null != rs.getString(MC_VIDEOID)) {
                    subsPersistDTO.setMcVideoId(new String(rs.getBytes(MC_VIDEOID), StandardCharsets.UTF_8));
                }
                if (null != rs.getString(MC_DATAID)) {
                    subsPersistDTO.setMcDataId(new String(rs.getBytes(MC_DATAID), StandardCharsets.UTF_8));
                }
                if (null != rs.getString(MC_ID)) {
                    subsPersistDTO.setMcId(new String(rs.getBytes(MC_ID), StandardCharsets.UTF_8));
                }
                    subsPersistDTO.setMaxSDDSession(rs.getInt(35));
                    subsPersistDTO.setMaxSDYSession(rs.getInt(36));
                    subsPersistDTO.setUfmi(rs.getString(37));
                String activeFS=rs.getString(ACTIVEFS2)!=null?rs.getString(ACTIVEFS2):com.kodiak.common.resources.KnGeneralUtil.convertLongToHexString(rs.getLong(ACTIVEFS1));
                subsPersistDTO.setActiveFS2(activeFS);
                String subseFS=rs.getString(SUBSCRIBERFS2)!=null?rs.getString(SUBSCRIBERFS2):com.kodiak.common.resources.KnGeneralUtil.convertLongToHexString(rs.getLong(SUBSCRIBERFS1));
                subsPersistDTO.setSubscriberFS2(subseFS);
                subsPersistDTO.setQpppackId(rs.getInt(40));
                subsPersistDTO.setDiscreetEnabled(rs.getInt(41));
                subsPersistDTO.setUserProfileIndex(rs.getInt(USERPROFILEINDEX));
                subsPersistDTO.setUserProfileId(rs.getString(USERPROFILEID));
                subsPersistDTO.setDerivedKey(rs.getString(DERIVED_KEY));
                String clientFS=rs.getString(CLIENTFS2)!=null?rs.getString(CLIENTFS2):com.kodiak.common.resources.KnGeneralUtil.convertLongToHexString(rs.getLong(CLIENTFS1));
                subsPersistDTO.setClientFS2(clientFS);
                String opsFS=rs.getString(OPSFS2)!=null?rs.getString(OPSFS2):com.kodiak.common.resources.KnGeneralUtil.convertLongToHexString(rs.getLong(OPSFS1));
                subsPersistDTO.setOpsFS2(opsFS);
                String xdmsFS=rs.getString(XDMSFS2)!=null?rs.getString(XDMSFS2):com.kodiak.common.resources.KnGeneralUtil.convertLongToHexString(rs.getLong(XDMSFS1));
                subsPersistDTO.setXdmsFS2(xdmsFS);
                String corpAdminFS=rs.getString(CORPADMINFS2)!=null?rs.getString(CORPADMINFS2):com.kodiak.common.resources.KnGeneralUtil.convertLongToHexString(rs.getLong(CORPADMINFS1));
                subsPersistDTO.setCorpAdminFS2(corpAdminFS);
                if (rs.getString(USERPROFILEFS2) != null) {
                    subsPersistDTO.setUserProfileFS2(rs.getString(USERPROFILEFS2));
                }
                subsPersistDTO.setCameraType(rs.getInt(CAMERA_TYPE));
                if (rs.getString(ACCOUNT_ID) != null) {
                    subsPersistDTO.setAccountId(rs.getString(ACCOUNT_ID).trim());
                }
                subsPersistDTO.setEmergConfigTimer(rs.getString(EMERGCONFTIME));
            } else {
                // Throw exception
                knLogger.error( methodName, "No Subs Info found");
                throw new KnDAOException(KnErrorCodes.DAO.ROW_NOT_FOUND, "Subscriber info notfound. Query ->" + query);
            }

            knLogger.debug( methodName, "Returning subsPersistDTO - ", subsPersistDTO);
            return subsPersistDTO;
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve subsInfo for mdn - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.POCSUBSCRINFO, query);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve subsInfo for mdn - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStatement);
            if (ownedTxn) {
                KnDbUtil.closeConnection(conn);
            }
            knLogger.debug(methodName, "EXIT : subsInfo ->", subsPersistDTO);
        }
    }


    public KnSubsProfilePersistDTO selectSubscriberInfoByMcPttId(String mcPttId, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "selectSubscriberInfoByMcPttId(String, KnPersisterTxn)";
        knLogger.debug( methodName, "Entry : ");

        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        StringBuilder query = new StringBuilder();
        KnSubsProfilePersistDTO subsPersistDTO = null;

        try {

            query.append(QRY_SELECT_SUBS_INFO_BY_MCPTTID);
            query.append(" AND ");
            query.append(" ( ");
            query.append(USERPROFILEINDEX);
            query.append(" = 0 ");
            query.append(" or ");
            query.append(USERPROFILEINDEX);
            query.append(" is null ");
            query.append(" ) ");
            knLogger.debug( methodName, " Data Store before - ", KnDBConst.DataStores.XDM_SHARED_DATA);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            knLogger.debug( methodName, " Data Store After - ", KnDBConst.DataStores.XDM_SHARED_DATA);
            pStatement = conn.prepareStatement(query.toString());
            pStatement.setBytes(1, mcPttId.getBytes(StandardCharsets.UTF_8));
           // pStatement.setString(1,mcPttId);
            knLogger.debug( methodName, "QUERY : Executing ", query, ", persisterTxn : ",
                    persisterTxn);
            rs = pStatement.executeQuery();
            knLogger.debug( methodName, "QUERY : Completed.");

            if (rs.next()) {
                subsPersistDTO = new KnSubsProfilePersistDTO();
                //multilingual revert changes
                if(rs.getString(1) != null)
                {
                    subsPersistDTO.setNetworkName(new String(rs.getString(1).getBytes("8859_1"),"UTF-8"));
                }
                subsPersistDTO.setServiceAuthStatus(rs.getInt(2));
                subsPersistDTO.setPublicSubscriptionType(rs.getInt(3));
                subsPersistDTO.setCorpSubscriptionType(rs.getInt(4));
                subsPersistDTO.setPocHome(rs.getString(5));
                subsPersistDTO.setPresenceHome(rs.getString(6));
                subsPersistDTO.setXdmsHome(rs.getString(7));
                subsPersistDTO.setProfileLastUpdated(rs.getLong(8));
                subsPersistDTO.setContactListId(rs.getInt(9));
                subsPersistDTO.setCorpId(rs.getInt(10));
                subsPersistDTO.setSubscriberEmail(rs.getString(11));
                subsPersistDTO.setClientType(rs.getInt(12));
                subsPersistDTO.setProtocolVersion(rs.getInt(13)+"."+rs.getInt(14));
                subsPersistDTO.setClientMajorVersion(rs.getInt(13));
                subsPersistDTO.setPamAccId(rs.getInt(16));
                subsPersistDTO.setDispatchType(rs.getInt(17));
                subsPersistDTO.setUserId(rs.getString(18));
                subsPersistDTO.setClientPassowrd(rs.getString(19));
                subsPersistDTO.setUserAgent(rs.getString(20));
                subsPersistDTO.setServiceAuthStatusOP(rs.getInt(21));
                subsPersistDTO.setServiceAuthStatusAU(rs.getInt(22));
                subsPersistDTO.setEmergCallType(rs.getInt(23));
                subsPersistDTO.setEmergCancelPermission(rs.getInt(24));
                subsPersistDTO.setEmergInitiatePermission(rs.getInt(25));
                subsPersistDTO.setLicenseType(rs.getInt(27));
                if(rs.getString(28) != null) subsPersistDTO.setAliasMdn(rs.getString(28).trim());
                subsPersistDTO.setEmergLmrBehaviour(rs.getInt(29));
                subsPersistDTO.setMcpttCompliance(rs.getInt(30));
                if (null != rs.getString(MC_PTTID)) {
                    subsPersistDTO.setMcpttId(new String(rs.getBytes(MC_PTTID), StandardCharsets.UTF_8));
                }
                if (null != rs.getString(MC_VIDEOID)) {
                    subsPersistDTO.setMcVideoId(new String(rs.getBytes(MC_VIDEOID), StandardCharsets.UTF_8));
                }
                if (null != rs.getString(MC_DATAID)) {
                    subsPersistDTO.setMcDataId(new String(rs.getBytes(MC_DATAID), StandardCharsets.UTF_8));
                }
                if (null != rs.getString(MC_ID)) {
                    subsPersistDTO.setMcId(new String(rs.getBytes(MC_ID), StandardCharsets.UTF_8));
                }
                subsPersistDTO.setMaxSDDSession(rs.getInt(35));
                subsPersistDTO.setMaxSDYSession(rs.getInt(36));
                subsPersistDTO.setUfmi(rs.getString(37));
                String activeFS=rs.getString(ACTIVEFS2)!=null?rs.getString(ACTIVEFS2):com.kodiak.common.resources.KnGeneralUtil.convertLongToHexString(rs.getLong(ACTIVEFS1));
                subsPersistDTO.setActiveFS2(activeFS);
                String subseFS=rs.getString(SUBSCRIBERFS2)!=null?rs.getString(SUBSCRIBERFS2):com.kodiak.common.resources.KnGeneralUtil.convertLongToHexString(rs.getLong(SUBSCRIBERFS1));
                subsPersistDTO.setSubscriberFS2(subseFS);
                subsPersistDTO.setQpppackId(rs.getInt(40));
                subsPersistDTO.setMdn(rs.getString(41));
                String clientFS=rs.getString(CLIENTFS2)!=null?rs.getString(CLIENTFS2):com.kodiak.common.resources.KnGeneralUtil.convertLongToHexString(rs.getLong(CLIENTFS1));
                subsPersistDTO.setClientFS2(clientFS);
                String opsFS=rs.getString(OPSFS2)!=null?rs.getString(OPSFS2):com.kodiak.common.resources.KnGeneralUtil.convertLongToHexString(rs.getLong(OPSFS1));
                subsPersistDTO.setOpsFS2(opsFS);
                String xdmsFS=rs.getString(XDMSFS2)!=null?rs.getString(XDMSFS2):com.kodiak.common.resources.KnGeneralUtil.convertLongToHexString(rs.getLong(XDMSFS1));
                subsPersistDTO.setXdmsFS2(xdmsFS);
                String corpAdminFS=rs.getString(CORPADMINFS2)!=null?rs.getString(CORPADMINFS2):com.kodiak.common.resources.KnGeneralUtil.convertLongToHexString(rs.getLong(CORPADMINFS1));
                subsPersistDTO.setCorpAdminFS2(corpAdminFS);
                if(rs.getString(USERPROFILEFS2) !=null ){
                    subsPersistDTO.setUserProfileFS2(rs.getString(USERPROFILEFS2));
                }

            } else {
                // Throw exception
                knLogger.error( methodName, "No Subs Info found");
                throw new KnDAOException(KnErrorCodes.DAO.ROW_NOT_FOUND, "Subscriber info notfound. Query ->" + query);
            }

            knLogger.info( methodName, "Returning subsPersistDTO - ", subsPersistDTO);
            return subsPersistDTO;
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve subsInfo for mdn - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.POCSUBSCRINFO, query.toString());
        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve subsInfo for mdn - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.POCSUBSCRINFO, query.toString());
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug( methodName, "EXIT : subsInfo ->", subsPersistDTO);
        }
    }

    public KnPocSubsAddlInfoDTO getSubsAddlDetails(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubsAddlDetails(String, KnPersisterTxn)";
        knLogger.debug( methodName, "Entry mdn: ",KnGDPRTemplate.mdn(mdn));
        Connection conn = null;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;
        boolean ownedTxn = false;
        KnPocSubsAddlInfoDTO subsPersistDTO = null;
        try {
            query = "SELECT MDN, EMERGORIGINDICATORBITSET, ONBOARDINGMAILS_REQ, EMERGRECVINDICATORBITSET, TIER_PKG_CODE, PTT_SETTING_DOCID FROM DG.POCSUBSCR_ADDLINFO WHERE MDN=?";
            //conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            if (persisterTxn != null) {
                knLogger.debug(methodName, " Data Store before - ", KnDBConst.DataStores.XDM_SHARED_DATA);
                conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
                knLogger.debug(methodName, "if block");
            } else {
                conn = KnDBManager.getDBManagerInstance().getConnection(KnGenInfoUtil.getSecDBPttServerId(pttServerId), true);
                ownedTxn = true;
                knLogger.debug(methodName, "else block");
                knLogger.debug(methodName, "conn", conn);
            }
            pStatement = conn.prepareStatement(query);

            pStatement.setString(1,mdn);
            knLogger.debug( methodName, "QUERY : Executing ", query, ", persisterTxn : ",
                    persisterTxn);
            rs = pStatement.executeQuery();
            knLogger.debug( methodName, "QUERY : Completed.");

            if (rs.next()) {
                subsPersistDTO = new KnPocSubsAddlInfoDTO();
                subsPersistDTO.setMdn(rs.getString(1));
                subsPersistDTO.setEmergOrigIndicatorBitSet(rs.getInt(2));
                subsPersistDTO.setOnBoardingEmailReqd(rs.getInt(3));
                subsPersistDTO.setEmergTermAlertIndExtM((Integer)rs.getObject(4));
                subsPersistDTO.setTierPackage(rs.getString(5));
                subsPersistDTO.setPttSettingDocId(rs.getString(6));
            }

            knLogger.info( methodName, "Returning subsPersistDTO - ", subsPersistDTO);
            return subsPersistDTO;
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve subsInfo for mdn - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.POCSUBSCRINFO, query);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve subsInfo for mdn - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStatement);
            if (ownedTxn) {
                KnDbUtil.closeConnection(conn);
            }
            knLogger.debug( methodName, "EXIT : subsInfo ->", subsPersistDTO);
        }
    }

    public KnEmergencyInfoDTO getEmergencySubsDestInfo(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubsAddlDetails(String, KnPersisterTxn)";
        knLogger.debug( methodName, "Entry mdn: ",KnGDPRTemplate.mdn(mdn));
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;
        KnEmergencyInfoDTO emergencyInfoDTO = null;
        try {
            query = "SELECT EMERGDESTPRIORITY FROM DG.EMERGENCY_SUBSCR_DESTINFO  WHERE MDN=?;";
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStatement = conn.prepareStatement(query);

            pStatement.setString(1,mdn);
            knLogger.debug( methodName, "QUERY : Executing ", query, ", persisterTxn : ",
                    persisterTxn);
            rs = pStatement.executeQuery();
            knLogger.debug( methodName, "QUERY : Completed.");

            if (rs.next()) {
                emergencyInfoDTO = new KnEmergencyInfoDTO();
                emergencyInfoDTO.setDestPriority(rs.getInt(1));
            }

            knLogger.info( methodName, "Returning emergencyInfoDTO - ", emergencyInfoDTO);
            return emergencyInfoDTO;
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve subsInfo for mdn - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.POCSUBSCRINFO, query);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve subsInfo for mdn - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug( methodName, "EXIT : emergencyInfoDTO ->", emergencyInfoDTO);
        }
    }

    public Map<String, KnSubsProfilePersistDTO> getSubscriberDetails(List<String> mdnList, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "getSubscriberDetails()";
        knLogger.debug( methodName, "Entry : ");

        boolean ownedTxn = false;
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        StringBuilder strBuilder = new StringBuilder();

        KnSubsProfilePersistDTO subsPersistDTO = null;
        Map<String,KnSubsProfilePersistDTO> mapKnSubsProfilePersistDTO=new HashMap<>();

        try {


            strBuilder.append("SELECT MDN,USER_ID,UFMI,ALIAS_MDN,MC_PTTID from DG.POCSUBSCRINFO where MDN IN (")
            .append(KnGeneralUtil.formCommaSeperatedIdList(mdnList))
            .append(");");

            knLogger.debug( methodName, " Data Store before - ", KnDBConst.DataStores.XDM_SHARED_DATA);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            knLogger.debug( methodName, " Data Store After - ", KnDBConst.DataStores.XDM_SHARED_DATA);
            pStatement = conn.prepareStatement(strBuilder.toString());
            knLogger.debug( methodName, "QUERY : Executing ", strBuilder.toString(), ", persisterTxn : ",
                    persisterTxn);
            rs = pStatement.executeQuery();
            knLogger.debug( methodName, "QUERY : Completed.");

            while (rs.next()) {
                subsPersistDTO = new KnSubsProfilePersistDTO();
                subsPersistDTO.setMdn(rs.getString("MDN").trim());
                subsPersistDTO.setUserId(rs.getString("USER_ID"));
                subsPersistDTO.setUfmi(rs.getString("UFMI"));
                subsPersistDTO.setAliasMdn(rs.getString("ALIAS_MDN"));
                subsPersistDTO.setMcpttId(rs.getString("MC_PTTID"));
                mapKnSubsProfilePersistDTO.put(rs.getString("MDN").trim(),subsPersistDTO);
            }

            knLogger.debug( methodName, "Returning mapKnSubsProfilePersistDTO - ", mapKnSubsProfilePersistDTO);
            return mapKnSubsProfilePersistDTO;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to select Subscriber Profile - " + e.getMessage(), pttServerId, "POCSUBSCRINFO", strBuilder.toString());
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug(methodName, "EXIT : response listKnSubsProfilePersistDTO",mapKnSubsProfilePersistDTO);
        }
    }

    public int selectSubscriberPV(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "selectSubscriberPV(String, KnPersisterTxn)";
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;
        int pv=0;
        try {
            query = QRY_SELECT_SUBS_PV;
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStatement = conn.prepareStatement(query);
            pStatement.setString(1, mdn);
            knLogger.debug( methodName, "QUERY : Executing ", query);
            rs = pStatement.executeQuery();
            knLogger.debug( methodName, "QUERY : Completed.");

            if (rs.next()) {
                 pv= rs.getInt(1);
            } else {
                // Throw exception
                knLogger.info( methodName, "No Subs Info found");
                throw new KnDAOException(KnErrorCodes.DAO.ROW_NOT_FOUND, "Subscriber info not found. Query ->" + query);
            }

            return pv;
        } catch (SQLException e) {
            knLogger.error(methodName, "SQL Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve subsInfo for mdn - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.POCSUBSCRINFO, query);
          } finally {
                KnDbUtil.closeResultSet(rs);
                KnDbUtil.closePreparedStatement(pStatement);
                knLogger.debug( methodName, "EXIT : subsInfo pv ->", pv);
        }

    }

    public Map<String, Integer> selectSubscribersPV(List<String> mdns, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getSubscribersPV(List<String>, boolean, KnPersisterTxn)";
        Connection conn = null;
        boolean ownedTxn = false;
        Statement statement = null;
        ResultSet rs = null;
        Map<String, Integer> pvInfo = null;
        StringBuilder buffer = new StringBuilder(200);
        knLogger.entry(methodName, persisterTxn);
        try {
            if (persisterTxn != null) {
                conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            } else {
                conn = KnDBManager.getDBManagerInstance().getConnection(KnGenInfoUtil.getSecDBPttServerId(pttServerId), true);
                ownedTxn = true;
            }
            buffer.append(QRY_SELECT_SUBSCRIBERS_PV).append(KnDbUtil.convertListToStringBuffer(mdns));
            statement = conn.createStatement();
            knLogger.debug(methodName, "Query: Executing - ", buffer.toString());
            rs = statement.executeQuery(buffer.toString());
            knLogger.debug(methodName, "Query: Executed ");
            pvInfo = new HashMap<String, Integer>();
            while (rs.next()) {
                pvInfo.put(rs.getString(1).trim(), rs.getInt(2));
            }
            if (pvInfo.isEmpty()) {
                knLogger.info( methodName, "No Subs Info found");
                throw new KnDAOException(KnErrorCodes.DAO.ROW_NOT_FOUND, "Subscriber info not found");
            }

        } catch (SQLException e) {
            knLogger.error(methodName, "SQL Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve subsInfo " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.POCSUBSCRINFO, "PV");

        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(statement);
            knLogger.debug(methodName, "EXIT : subsInfo pv ->", KnGDPRTemplate.mapKeyMdn(pvInfo));
            if (ownedTxn) {
                KnDbUtil.closeConnection(conn);
            }
        }
        knLogger.exit(methodName);
        return pvInfo;
    }

    public KnSubsProfilePersistDTO selectSubscriberInfoByMcIdAndUPMIndex(String mcId,String upmIdex, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "selectSubscriberInfoByMcId(String,String,KnPersisterTxn)";
        knLogger.debug( methodName, "Entry : mcId:",KnGDPRTemplate.mcId(mcId)," upmIdex :",upmIdex);

        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        StringBuilder query = new StringBuilder();

        KnSubsProfilePersistDTO subsPersistDTO = null;

        try {

            query.append(QRY_SELECT_SUBS_INFO_BY_MCID);
            if(upmIdex!=null){
                if(upmIdex.equals("0")){
                    //for base mdn upmIndex can be null or 0
                    query.append(" AND ");
                    query.append(" ( ");
                    query.append(USERPROFILEINDEX);
                    query.append(" = ? ");
                    query.append(" or ");
                    query.append(USERPROFILEINDEX);
                    query.append(" is null ");
                    query.append(" ) ");

                }else{
                    //for profile mdn
                    query.append(" AND ");
                    query.append(USERPROFILEINDEX);
                    query.append(" = ?");
                }
            }

            knLogger.debug( methodName, "QUERY : Executing ", query.toString(), ", persisterTxn : ",
                    persisterTxn);
            knLogger.debug( methodName, " Data Store before - ", KnDBConst.DataStores.XDM_SHARED_DATA);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            knLogger.debug( methodName, " Data Store After - ", KnDBConst.DataStores.XDM_SHARED_DATA);
            pStatement = conn.prepareStatement(query.toString());
            pStatement.setBytes(1, mcId.getBytes(StandardCharsets.UTF_8));
            if(upmIdex!=null){
                pStatement.setInt(2,Integer.parseInt(upmIdex));
            }

            rs = pStatement.executeQuery();
            knLogger.debug( methodName, "QUERY : Completed.");

            if (rs.next()) {
                subsPersistDTO = new KnSubsProfilePersistDTO();
                //   subsPersistDTO.setMcpttId(mcPttId);
                //multilingual revert changes
                if(rs.getString(1) != null)
                {
                    subsPersistDTO.setNetworkName(new String(rs.getString(1).getBytes("8859_1"),"UTF-8"));
                }
                subsPersistDTO.setServiceAuthStatus(rs.getInt(2));
                subsPersistDTO.setPublicSubscriptionType(rs.getInt(3));
                subsPersistDTO.setCorpSubscriptionType(rs.getInt(4));
                subsPersistDTO.setPocHome(rs.getString(5));
                subsPersistDTO.setPresenceHome(rs.getString(6));
                subsPersistDTO.setXdmsHome(rs.getString(7));
                subsPersistDTO.setProfileLastUpdated(rs.getLong(8));
                subsPersistDTO.setContactListId(rs.getInt(9));
                subsPersistDTO.setCorpId(rs.getInt(10));
                subsPersistDTO.setSubscriberEmail(rs.getString(11));
                subsPersistDTO.setClientType(rs.getInt(12));
                subsPersistDTO.setProtocolVersion(rs.getInt(13)+"."+rs.getInt(14));
                subsPersistDTO.setClientMajorVersion(rs.getInt(13));
                subsPersistDTO.setPamAccId(rs.getInt(16));
                subsPersistDTO.setDispatchType(rs.getInt(17));
                subsPersistDTO.setUserId(rs.getString(18));
                subsPersistDTO.setClientPassowrd(rs.getString(19));
                subsPersistDTO.setUserAgent(rs.getString(20));
                subsPersistDTO.setServiceAuthStatusOP(rs.getInt(21));
                subsPersistDTO.setServiceAuthStatusAU(rs.getInt(22));
                subsPersistDTO.setEmergCallType(rs.getInt(23));
                subsPersistDTO.setEmergCancelPermission(rs.getInt(24));
                subsPersistDTO.setEmergInitiatePermission(rs.getInt(25));
                subsPersistDTO.setLicenseType(rs.getInt(27));
                if(rs.getString(28) != null) subsPersistDTO.setAliasMdn(rs.getString(28).trim());
                subsPersistDTO.setEmergLmrBehaviour(rs.getInt(29));
                subsPersistDTO.setMcpttCompliance(rs.getInt(30));
                if (null != rs.getString(MC_PTTID)) {
                    subsPersistDTO.setMcpttId(new String(rs.getBytes(MC_PTTID), StandardCharsets.UTF_8));
                }
                if (null != rs.getString(MC_VIDEOID)) {
                    subsPersistDTO.setMcVideoId(new String(rs.getBytes(MC_VIDEOID), StandardCharsets.UTF_8));
                }
                if (null != rs.getString(MC_DATAID)) {
                    subsPersistDTO.setMcDataId(new String(rs.getBytes(MC_DATAID), StandardCharsets.UTF_8));
                }
                if (null != rs.getString(MC_ID)) {
                    subsPersistDTO.setMcId(new String(rs.getBytes(MC_ID), StandardCharsets.UTF_8));
                }
                subsPersistDTO.setMaxSDDSession(rs.getInt(35));
                subsPersistDTO.setMaxSDYSession(rs.getInt(36));
                subsPersistDTO.setUfmi(rs.getString(37));
                String activeFS=rs.getString(ACTIVEFS2)!=null?rs.getString(ACTIVEFS2):com.kodiak.common.resources.KnGeneralUtil.convertLongToHexString(rs.getLong(ACTIVEFS1));
                subsPersistDTO.setActiveFS2(activeFS);
                String subseFS=rs.getString(SUBSCRIBERFS2)!=null?rs.getString(SUBSCRIBERFS2):com.kodiak.common.resources.KnGeneralUtil.convertLongToHexString(rs.getLong(SUBSCRIBERFS1));
                subsPersistDTO.setSubscriberFS2(subseFS);
                subsPersistDTO.setQpppackId(rs.getInt(40));
                subsPersistDTO.setMdn(rs.getString(41).trim());
                subsPersistDTO.setUserProfileId(rs.getString(42));
                String clientFS=rs.getString(CLIENTFS2)!=null?rs.getString(CLIENTFS2):com.kodiak.common.resources.KnGeneralUtil.convertLongToHexString(rs.getLong(CLIENTFS1));
                subsPersistDTO.setClientFS2(clientFS);
                String opsFS=rs.getString(OPSFS2)!=null?rs.getString(OPSFS2):com.kodiak.common.resources.KnGeneralUtil.convertLongToHexString(rs.getLong(OPSFS1));
                subsPersistDTO.setOpsFS2(opsFS);
                String xdmsFS=rs.getString(XDMSFS2)!=null?rs.getString(XDMSFS2):com.kodiak.common.resources.KnGeneralUtil.convertLongToHexString(rs.getLong(XDMSFS1));
                subsPersistDTO.setXdmsFS2(xdmsFS);
                String corpAdminFS=rs.getString(CORPADMINFS2)!=null?rs.getString(CORPADMINFS2):com.kodiak.common.resources.KnGeneralUtil.convertLongToHexString(rs.getLong(CORPADMINFS1));
                subsPersistDTO.setCorpAdminFS2(corpAdminFS);
                if (rs.getString(USERPROFILEFS2) != null) {
                    subsPersistDTO.setUserProfileFS2(rs.getString(USERPROFILEFS2));
                }
                subsPersistDTO.setCameraType(rs.getInt(CAMERA_TYPE));
                subsPersistDTO.setUserProfileIndex(rs.getInt(USERPROFILEINDEX));
                subsPersistDTO.setEmergConfigTimer(rs.getString(EMERGCONFTIME));
                if(rs.getString(HIERARCHY_ID) != null){
                    subsPersistDTO.setHierarchyId(rs.getString(HIERARCHY_ID));
                }
            } else {
                // Throw exception
                knLogger.error( methodName, "No Subs Info found");
                throw new KnDAOException(KnErrorCodes.DAO.ROW_NOT_FOUND, "Subscriber info not found");
            }

            knLogger.info( methodName, "Returning subsPersistDTO - ", subsPersistDTO);
            return subsPersistDTO;
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve subsInfo for mdn - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.POCSUBSCRINFO, query.toString());
        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve subsInfo for mdn - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.POCSUBSCRINFO, query.toString());
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug( methodName, "EXIT : subsInfo ->", subsPersistDTO);
        }
    }

    public List<KnSubsProfileDTO> getProfileDetailsListByMcID(String mcId, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "getProfileDetailsListByMcID(String, KnPersisterTxn)";
        knLogger.debug( methodName, "Entry : mcId ",KnGDPRTemplate.mcId(mcId));
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;
        List<KnSubsProfileDTO> subsProfileDTOList=new ArrayList<>();
        //KnSubsProfileDTO subsPersistDTO = null;
        try {
            query = QRY_SELECT_SUBS_INFO_LIST_BY_MCID;
            knLogger.debug( methodName, " Data Store before - ", KnDBConst.DataStores.XDM_SHARED_DATA);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            knLogger.debug( methodName, " Data Store After - ", KnDBConst.DataStores.XDM_SHARED_DATA);
            pStatement = conn.prepareStatement(query);
            pStatement.setBytes(1, mcId.getBytes(StandardCharsets.UTF_8));

            knLogger.debug( methodName, "QUERY : Executing ", query, ", persisterTxn : ",
                    persisterTxn);
            rs = pStatement.executeQuery();
            knLogger.debug( methodName, "QUERY : Completed.");

            if (rs.next() == false) {
                knLogger.error( methodName, "No Subs Info found");
                throw new KnDAOException(KnErrorCodes.DAO.ROW_NOT_FOUND, "Subscriber info notfound. Query ->" + query);
            }else{
                do {
                    KnSubsProfileDTO subsPersistDTO = new KnSubsProfileDTO();
                    subsPersistDTO.setMdn(rs.getString(MDN));
                    subsPersistDTO.setProfileLastUpdated(rs.getLong(LASTPROFILEUPDATETIME));
                    subsPersistDTO.setUserProfileIndex(rs.getInt(USERPROFILEINDEX));
                    if (null != rs.getString(MC_PTTID)) {
                        subsPersistDTO.setMcpttId(new String(rs.getBytes(MC_PTTID), StandardCharsets.UTF_8));
                    }
                    if(rs.getString(SUBSCR_NAME) != null)
                    {
                        subsPersistDTO.setNetworkName(new String(rs.getString(SUBSCR_NAME).getBytes("8859_1"),"UTF-8"));
                    }
                    subsPersistDTO.setPublicSubscriptionType(rs.getInt(PUBLIC_SUBSCRIPTION_TYPE));
                    subsPersistDTO.setServiceAuthStatus(rs.getInt(SERVICE_AUTH_STATUS));
                    subsPersistDTO.setMcId(new String(rs.getBytes(MC_ID), StandardCharsets.UTF_8));
                    subsPersistDTO.setDefaultProfile((rs.getInt(ISDEFAULTPROFILE))==1?true:false);
                    subsPersistDTO.setCorpId(rs.getInt(CORP_ID));
                    subsProfileDTOList.add(subsPersistDTO);
                }while(rs.next());
                }
            knLogger.info( methodName, "Returning subsProfileDTOList - ", subsProfileDTOList);
            return subsProfileDTOList;
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve subsInfo for mdn - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.POCSUBSCRINFO, query);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve subsInfo for mdn - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug( methodName, "EXIT : subsProfileDTOList ->", subsProfileDTOList);
        }
    }

    public List<String> getRealMdns(List<String> mdns, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "getRealMdns(List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : mdns ", KnGDPRTemplate.mdnList(mdns));
        Connection conn;
        Statement statement = null;
        ResultSet rs = null;
        List<String> realMdns = new ArrayList<String>();
        StringBuilder buffer = new StringBuilder(200);
        knLogger.entry(methodName, mdns != null ? mdns.size() : 0, persisterTxn);
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            buffer.append(QRY_SELECT_REAL_MDNS).append(KnDbUtil.convertListToStringBuffer(mdns));
            statement = conn.createStatement();
            knLogger.debug(methodName, "Query: Executing - ", buffer.toString());
            rs = statement.executeQuery(buffer.toString());
            knLogger.debug(methodName, "Query: Executed ");
            while (rs.next()) {
                String mdn = rs.getString(1).trim();
                realMdns.add(mdn);
            }

        } catch (SQLException e) {
            knLogger.error(methodName, "SQL Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve subsInfo " + e.getMessage(), pttServerId,
                    KnDAOSourceTypes.POCSUBSCRINFO, "PV");

        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(statement);
            knLogger.debug(methodName, "EXIT : realMdns ->", KnGDPRTemplate.mdnList(realMdns));
        }
        knLogger.exit(methodName, realMdns != null ? realMdns.size() : 0);
        return realMdns;
    }

    public Map<String,Set<String>> getBaseMdnsMap(List<String> mdns, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "getRealMdns(List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : mdns ", KnGDPRTemplate.mdnList(mdns));
        Connection conn = null;
        Statement statement = null;
        ResultSet rs = null;
        boolean ownedTxn = false;
        Map<String,Set<String>> baseMdnsMap = new HashMap<>();
        StringBuilder buffer = new StringBuilder(200);
        knLogger.entry(methodName, mdns, persisterTxn);
        try {
            if(persisterTxn == null){
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                ownedTxn = true;
            }
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            buffer.append(QRY_SELECT_BASE_MDNS_MAP).append(KnDbUtil.convertListToStringBuffer(mdns));
            statement = conn.createStatement();
            knLogger.debug(methodName, "Query: Executing - ", buffer.toString());
            rs = statement.executeQuery(buffer.toString());
            knLogger.debug(methodName, "Query: Executed ");
            while (rs.next()) {
                if(baseMdnsMap.containsKey(rs.getString(1).trim())) {
                    Set<String> mdnSet = baseMdnsMap.get(rs.getString(1).trim());
                    mdnSet.add(rs.getString(2).trim());
                    baseMdnsMap.put(rs.getString(1).trim(),mdnSet);
                }else{
                    Set<String> mdnSet = new HashSet<>();
                    mdnSet.add(rs.getString(2).trim());
                    baseMdnsMap.put(rs.getString(1).trim(),mdnSet);
                }
            }

            if (ownedTxn){
                persisterTxn.save();
            }

        } catch (SQLException e) {
            knLogger.error(methodName, "SQL Exception - ", e);
            if(ownedTxn){
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to retrieve subsInfo " + e.getMessage(), pttServerId,
                    KnDAOSourceTypes.POCSUBSCRINFO, "PV");

        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(statement);
            if (ownedTxn) {
                KnDbUtil.closeConnection(conn);
            }
            knLogger.debug(methodName, "EXIT : baseMdnsMap ->", baseMdnsMap);
        }
        knLogger.exit(methodName, baseMdnsMap);
        return baseMdnsMap;
    }
    
    /**
     * method to retrieve the Subscriber Profile
     *
     * @param mdnList    LinkedList<String>
     * @param persistTxn KnPersisterTxn
     * @return KnOPSubsProfileInfoDTO
     * @throws KnDAOException DB Layer Exception
     */
    public KnXDMSubsProfileRespDTO selectSubsProfileInfo(List<String> mdnList, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "selectSubsProfileInfo(mdnList, KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        KnXDMSubsProfileRespDTO subsProfileInfoDto = new KnXDMSubsProfileRespDTO();
        boolean ownedTxn = false;
        knLogger.info(methodName, "ENTRY: Select Subscriber Profile ");
        try {
            StringBuilder strBuffer = getSelectSubsProfileQuery();
            strBuffer.append(MDN).append(" IN ").append("(");
            strBuffer.append(formCommaSeperatedIdList(mdnList)).append(");");
            if (persistTxn == null) {
                persistTxn = KnPersisterTxn.getPersisterTxn();
                persistTxn.open();
                ownedTxn = true;
            }
            query = strBuffer.toString();
            boolean readOnly = ownedTxn ? true : false;
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            knLogger.debug(methodName, "selectSubscriberProfile ... ");
            pStmt = conn.prepareStatement(query);
            knLogger.debug(methodName, "Query: Executing - ", query);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Query: Executed ");
            List <KnXDMSubsProvDTO> subsProvList= new ArrayList<KnXDMSubsProvDTO>();
            while (rs.next()) {
                KnXDMSubsProvDTO subsProfile = new KnXDMSubsProvDTO();
                getSelectSubsProfileResult(subsProfile, rs);
                subsProvList.add(subsProfile);
            }
            subsProfileInfoDto.setSubsRespDTO(subsProvList);
            knLogger.debug(methodName, "returning subscriber Profile Info ", subsProfileInfoDto, subsProfileInfoDto.getSubsRespDTO().size());
            if (ownedTxn) {
                persistTxn.save();
            }
            return subsProfileInfoDto;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to select Subscriber Profile - " + e.getMessage(), pttServerId, KnDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            knLogger.info(methodName, "EXIT : select Subscriber Profile");
        }
    }

    public LinkedHashSet<String> selectSubDetails(List<String> mdnList, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "selectSubDetails(mdnList, KnPersisterTxn)";
        knLogger.info(methodName, "Entry -->" , mdnList.size(), persistTxn);
        Connection conn;
        Statement statement = null;
        ResultSet rs = null;
        StringBuilder buffer = new StringBuilder(200);
        LinkedHashSet<String> dispMdnList = new LinkedHashSet<>();
        try {
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            buffer.append(QRY_SELECT_DISP_SUBSCRIBERS).append(KnDbUtil.convertListToStringBuffer(mdnList));
            statement = conn.createStatement();
            rs = statement.executeQuery(buffer.toString());
            while (rs.next()) {
                dispMdnList.add(rs.getString(1));
            }
            knLogger.info(methodName, "returning subscriber Profile Info ", dispMdnList.size());
            return dispMdnList;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to select Subscriber Profile - " + e.getMessage(), pttServerId, KnDAOSourceTypes.POCSUBSCRINFO, buffer.toString());
        } finally {
            KnDbUtil.closeResultSet(rs);
        }
    }

    private StringBuilder getSelectSubsProfileQuery() {
        StringBuilder strBuffer = new StringBuilder();
        strBuffer.append("SELECT ");
        strBuffer.append(MDN).append(", ")
                .append(CORP_ID).append(", ").append(LAST_PROFILE_UPDATE_TIME).append(", ")
                .append("ACTIVEFS1").append(", ")
                .append("CLIENTPV_MAJORVERSION").append(", ").append("CLIENTPV_MINORVERSION").append(", ")
                .append("ACTIVEFS2").append(",").append(MC_ID).append(" , ").append(MC_PTTID).append(" , ")
                .append(MC_VIDEOID).append(" , ").append(MC_DATAID).append(" , ").append(USERPROFILEINDEX).append(" , ").append(MCPTT_COMPLIANCE).append(" , ").append(DERIVED_KEY).append(" , ").append(CLIENT_PASSWORD).append(" , ").append(USERPROFILEID);
        strBuffer.append(" FROM ").append(TABLENAME).append(" WHERE ");

        return strBuffer;

    }

    private void getSelectSubsProfileResult(KnXDMSubsProvDTO subsProfileInfoDto, ResultSet rs)
            throws SQLException, UnsupportedEncodingException {
        subsProfileInfoDto.setMdn(rs.getString(MDN));
        subsProfileInfoDto.setCorpId(String.valueOf(rs.getInt(CORP_ID)));
        subsProfileInfoDto.setLastUpdateProfileTime(rs.getLong(LAST_PROFILE_UPDATE_TIME));
        subsProfileInfoDto.setClientPvMajorVersion(rs.getInt("CLIENTPV_MAJORVERSION"));
        subsProfileInfoDto.setClientPvMinorVersion(rs.getInt("CLIENTPV_MINORVERSION"));
        String activeFs2=rs.getString("ACTIVEFS2")!=null?rs.getString("ACTIVEFS2"):com.kodiak.common.resources.KnGeneralUtil.convertLongToHexString(rs.getLong("ACTIVEFS1"));
        subsProfileInfoDto.setActiveFS2(activeFs2);
      
        if (null != rs.getString(MC_ID)) {
            subsProfileInfoDto.setMcId(new String(rs.getBytes(MC_ID), StandardCharsets.UTF_8));
        }
        if (null != rs.getString(MC_PTTID)) {
            subsProfileInfoDto.setMcpttId(new String(rs.getBytes(MC_PTTID), StandardCharsets.UTF_8));
        }
        if (null != rs.getString(MC_VIDEOID)) {
            subsProfileInfoDto.setMcVideoId(new String(rs.getBytes(MC_VIDEOID), StandardCharsets.UTF_8));
        }
        if (null != rs.getString(MC_DATAID)) {
            subsProfileInfoDto.setMcDataId(new String(rs.getBytes(MC_DATAID), StandardCharsets.UTF_8));
        }
        subsProfileInfoDto.setUserProfileIndex(rs.getInt(USERPROFILEINDEX));
        subsProfileInfoDto.setMcsCompliance(rs.getInt(MCPTT_COMPLIANCE));
        subsProfileInfoDto.setDerivedKey(rs.getString(DERIVED_KEY));
        subsProfileInfoDto.setClientPassword(rs.getString(CLIENT_PASSWORD));
        subsProfileInfoDto.setUserProfileId(rs.getString(USERPROFILEID));

    }

    public Map<String, String> getProfileMdnBaseMdnMap(List<String> mdns, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getProfileMdnBaseMdnMap(List<String>, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : mdns ", mdns);
        Connection conn;
        Statement statement = null;
        ResultSet rs = null;
        Map<String, String> profileMdnBaseMdnMap = new HashMap<>();
        StringBuilder buffer = new StringBuilder(200);
        knLogger.entry(methodName, mdns, persisterTxn);
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            buffer.append(QRY_SELECT_REAL_MDNS_PROFILEMDN).append(KnDbUtil.convertListToStringBuffer(mdns));
            statement = conn.createStatement();
            knLogger.debug(methodName, "Query: Executing - ", buffer.toString());
            rs = statement.executeQuery(buffer.toString());
            knLogger.debug(methodName, "Query: Executed ");
            while (rs.next()) {
                String mdn = rs.getString(1).trim();
                profileMdnBaseMdnMap.put(rs.getString(2).trim(), mdn);
            }

        } catch (SQLException e) {
            knLogger.error(methodName, "SQL Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve subsInfo " + e.getMessage(), pttServerId,
                    KnDAOSourceTypes.POCSUBSCRINFO, "PV");

        } finally {
            KnDbUtil.closeResultSet(rs);
            knLogger.debug(methodName, "EXIT : profileMdnBaseMdnMap ->", profileMdnBaseMdnMap);
        }
        knLogger.exit(methodName, profileMdnBaseMdnMap);
        return profileMdnBaseMdnMap;
    }

    public Map<String, List<String>> getProfileMdnListByBaseMdnsList(List<String> baseMdnList, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "DAO.getProfileMdnListByBaseMdnsList()";
        knLogger.debug(methodName, "Entry : baseMdnList ", KnGDPRTemplate.mdnList(baseMdnList));
        Connection conn;
        Statement statement = null;
        PreparedStatement pstmt;
        ResultSet rs = null;
        List<String> realMdns = new ArrayList<>();
        StringBuilder query=new StringBuilder();
        Map<String, List<String>> profileOfBaseMdnMap=new HashMap<>();
        try {

            query.append(" SELECT MDN FROM DG.POCSUBSCRINFO WHERE MC_ID= ");
            query.append(" (SELECT MC_ID FROM DG.POCSUBSCRINFO WHERE MDN=?)  ");
            query.append(" AND USERPROFILEINDEX!=0 ");
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            pstmt = conn.prepareStatement(query.toString());
            for(String baseMdn:baseMdnList){
                pstmt.setString(1, baseMdn);
                rs = pstmt.executeQuery();
                List<String> profileMdnList=new ArrayList<>();
                while (rs.next()) {
                    profileMdnList.add(rs.getString("MDN").trim());
                }
                profileOfBaseMdnMap.put(baseMdn,profileMdnList);
            }

        } catch (SQLException e) {
            knLogger.error(methodName, "SQL Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve subsInfo " + e.getMessage(), pttServerId,
                    KnDAOSourceTypes.POCSUBSCRINFO, "PV");

        } finally {
            KnDbUtil.closeResultSet(rs);
            knLogger.debug(methodName, "EXIT : profileOfBaseMdnMap :", profileOfBaseMdnMap);
        }
        return profileOfBaseMdnMap;
    }
    
    public List<String> getMcsIdNullMdnList(KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "getMcsIdNullMdnList(KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : persistTxn ", persistTxn);
        Connection conn;
        Statement statement = null;
        ResultSet rs = null;
        List<String> mdns = new ArrayList<String>();
        StringBuilder buffer = new StringBuilder(200);
        knLogger.entry(methodName, persistTxn);
        try {
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            buffer.append(QRY_SELECT_MDN_WHERE_MCID_IS_NULL);
            statement = conn.createStatement();
            knLogger.debug(methodName, "Query: Executing - ", buffer.toString());
            rs = statement.executeQuery(buffer.toString());
            knLogger.debug(methodName, "Query: Executed ");
            while (rs.next()) {
                String mdn = rs.getString(1).trim();
                mdns.add(mdn);
            }

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception -", e);
            throw KnDbUtil.processException(e, "Failed to getMcsIdNullMdnList " + e.getMessage(), pttServerId,
                    KnDAOSourceTypes.POCSUBSCRINFO, buffer.toString());

        } finally {
            KnDbUtil.closeResultSet(rs);
            knLogger.debug(methodName, "EXIT : getMcsIdNullMdnList->", KnGDPRTemplate.mdnList(mdns));
        }
        knLogger.exit(methodName, KnGDPRTemplate.mdnList(mdns));
        return mdns;

    }

    public void updateMcsIdForMdnList(List<String> mdnList, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "updateMcsIdForMdnList(mdnList, KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt;
        knLogger.debug(methodName, "ENTRY: updateMcsIdForMdnList - ", mdnList.size());
        try {
            StringBuilder queryBuffer = new StringBuilder();
            queryBuffer.append(UPDATE_QRY);
            queryBuffer.append(MC_ID).append("=? ,");
            queryBuffer.append(MC_PTTID).append("=? ,");
            queryBuffer.append(MC_VIDEOID).append("=? ,");
            queryBuffer.append(MC_DATAID).append("=? ");
            queryBuffer.append(" WHERE ").append(MDN).append("=?");

            query = queryBuffer.toString();
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);
            int columnIndex = 0;
            for (String mdn : mdnList) {
                String defaultMcsId = "tel:+" + mdn;
                pStmt.setBytes(++columnIndex, defaultMcsId.getBytes(StandardCharsets.UTF_8));
                pStmt.setBytes(++columnIndex, defaultMcsId.getBytes(StandardCharsets.UTF_8));
                pStmt.setBytes(++columnIndex, defaultMcsId.getBytes(StandardCharsets.UTF_8));
                pStmt.setBytes(++columnIndex, defaultMcsId.getBytes(StandardCharsets.UTF_8));
                pStmt.setString(++columnIndex, mdn);
                pStmt.addBatch();
                columnIndex = 0;
            }
            knLogger.debug(methodName, "Query: Executing - ", query);
            pStmt.executeBatch();
            knLogger.debug(methodName, "Query Executed ");
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to update McsId For Mdn List - " + e.getMessage(), pttServerId,
                    KnDAOSourceTypes.POCSUBSCRINFO, query);
        }
        knLogger.exit(methodName, "EXIT: updateMcsIdForMdnList");

    }

    public Map<String, Collection<KnDocChangeListDTO>> updateSubsTS(Set<String> mdnList
            ,String exists, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateSubsTS(List<String>,KnPersisterTxn)";
        knLogger.debug( methodName, "ENTRY Point : mdnList - " , KnGDPRTemplate.mdnList(mdnList)," exists ",exists);
        Connection conn;
        PreparedStatement pstmt;
        ResultSet rs = null;
        String selectQuery = null;
        String updateQuery = null;
        Map<String, Collection<KnDocChangeListDTO>> mdnDocMap=new HashMap<>();
        try {
            final AtomicInteger counter = new AtomicInteger(0);
            final int mdnBatchSize = 1000;
            final Collection<List<String>> mdnChunk = mdnList.stream()
                    .collect(Collectors.groupingBy(it -> counter.getAndIncrement() / mdnBatchSize))
                    .values();
                knLogger.debug( methodName, "mdnChunk--->",KnGDPRTemplate.mdnLists(mdnChunk));

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);

            for(List<String> chunk:mdnChunk){
                knLogger.debug( methodName, "chunk----->",KnGDPRTemplate.mdnList(chunk));
                StringBuilder strBuffer = new StringBuilder(200);
                strBuffer.append(GET_SUBS_MCID_UPDTS_BY_MDNS).append("(");
                strBuffer.append(formCommaSeperatedIdList(chunk)).append(");");
                selectQuery = strBuffer.toString();
                knLogger.debug( methodName, "Query selectQuery :",selectQuery);
                pstmt = conn.prepareStatement(selectQuery);
                rs = pstmt.executeQuery();
                knLogger.debug( methodName, "Query selectQuery :",selectQuery);
                long lastProfileUpdateTime = Calendar.getInstance().getTimeInMillis();
                Collection<KnDocChangeListDTO> dbRecList=null;
                while (rs.next()) {
                    String mdn = rs.getString(1).trim();
                    String activeFs=rs.getString(7);
                    boolean upmBit = com.kodiak.common.resources.KnGeneralUtil.getFeatureBitValue(activeFs,USER_PROFILE_MGMT_BIT);
                    int majorPv = rs.getInt(6);
                    //mdns pv >18 or pv 19 onwards and umfit should be enabled
                    if(majorPv>PROTOCOL_VERSION_18_X||upmBit){
                        updateQuery = UPDATE_SUBS_PROFILE_LAST_UPDATE_TIME;
                        pstmt = conn.prepareStatement(updateQuery);
                        pstmt.setLong(1, lastProfileUpdateTime);
                        pstmt.setString(2, mdn);
                        pstmt.addBatch();
                        dbRecList=new ArrayList<>();
                        dbRecList.addAll(KnGeneralUtil.buildMCSDOC(new String(rs.getBytes(2), StandardCharsets.UTF_8)
                                ,mdn
                                ,String.valueOf(rs.getInt(5))
                                ,String.valueOf(lastProfileUpdateTime)
                                ,String.valueOf(rs.getLong(3))
                                ,rs.getInt(4)
                                ,exists)
                        );
                        mdnDocMap.put(mdn,dbRecList);
                    }
                }
                pstmt.executeBatch();
            }
            knLogger.debug(methodName, "Query executed successfully mdnDocMap:-",mdnDocMap);
            return mdnDocMap;
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured " + e);
            throw KnDbUtil.processException(e, "Failed updating subs TS" + e,
                    pttServerId, KnDAOSourceTypes.POCSUBSCRINFO, selectQuery + " , " + updateQuery);
        } finally {
            KnDbUtil.closeResultSet(rs);
        }
    }

    public Map<String,Map<Integer,String>> selectProfileIdMDNsByMcpttId(List<String> mcpttIds, KnPersisterTxn persistTxn)  throws KnDAOException {
        String methodName = "selectProfileIdMDNsByMcpttIds(String, KnPersisterTxn)";
        String query = null;
        Connection conn;
        Statement stmt;
        ResultSet rs = null;
        Map<String,Map<Integer,String>> mcPttIdprofileIdMdns= new HashMap<>();
        Map<Integer,String> profileIdMdns= new HashMap<>();
        knLogger.debug(methodName, "ENTRY: selectProfileIdMDNsByMcpttIds ");
        try {
            query = SELECT_PROFILEIDMDN_QUERY.replaceAll(MCPTT_LIST, com.kodiak.common.resources.KnGeneralUtil.getComSepList(mcpttIds));
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            persistTxn.getDBConnection(pttServerId, false);
            stmt = conn.createStatement();
            knLogger.debug(methodName, "Query: Executing - ", query, ", mcpttIds - ", KnGDPRTemplate.mcPttIdList(mcpttIds));
            rs =stmt.executeQuery(query);
            knLogger.debug(methodName, "Query: Executed ");

            while (rs.next()) {
                if(null != rs.getString(MC_PTTID)) {
                    String mcPttId =  new String(rs.getBytes(MC_PTTID), StandardCharsets.UTF_8);
                    if (mcPttIdprofileIdMdns.get(mcPttId) == null) {
                        profileIdMdns = new HashMap<>();
                        profileIdMdns.put(rs.getInt(USERPROFILEINDEX), rs.getString(MDN).trim());
                        mcPttIdprofileIdMdns.put(mcPttId, profileIdMdns);
                    } else {
                        Map<Integer ,String> tempProfileIdMdns =  mcPttIdprofileIdMdns.get(mcPttId);
                        tempProfileIdMdns.put(rs.getInt(USERPROFILEINDEX), rs.getString(MDN).trim());
                    }
                }
            }
            knLogger.debug(methodName, "mcPttIdprofileIdMdns:"+KnGDPRTemplate.mcpttIdAndProfileMdnMap(mcPttIdprofileIdMdns));
            if(mcPttIdprofileIdMdns.isEmpty()){
                knLogger.info(methodName, "Subscriber Profile doesn't exist");
                throw new KnDBPersistenceException(KnErrorCodes.DAO.ROW_NOT_FOUND, "Subscriber Profile does not exist",
                        pttServerId, KnDAOSourceTypes.POCSUBSCRINFO, query);
            }
        } catch (KnDAOException e) {
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to selectProfileIdMDNsByMcpttIds - " + e.getMessage(), pttServerId,
                    KnDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
        }

        return mcPttIdprofileIdMdns;

    }

    public String selectUserProfileName(String mdn, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        final String methodName = "selectUserProfileName(String mdn,boolean readOnly, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "ENTRY:mdn", KnGDPRTemplate.mdn(mdn));
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String userProfileName = null;
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            pStmt = conn.prepareStatement(QRY_SELECT_USER_PROFILE_NAME);
            pStmt.setString(1, mdn);
            knLogger.debug(methodName, "QUERY: Executing the Query - ", QRY_SELECT_USER_PROFILE_NAME);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Query: Executed ");
            if (rs.next()) {
                userProfileName =rs.getString(USER_PROFILE_NAME) ;
            } else {
                knLogger.info(methodName, "Subscriber AddlInfo Profile doesn't exist");
            }
            knLogger.debug(methodName, "Query: Executed ");
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to updateSubscrPkgAddlInfo - " + e.getMessage(), pttServerId,
                    KnDAOSourceTypes.POCSUBSCRADDLINFO, QRY_SELECT_USER_PROFILE_NAME);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
        }
        return userProfileName;
    }

    public void updateUserProfileName(String userProfileName,String mdn, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        final String methodName = "updateUserProfileName(KnSubsAddlInfoPersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "ENTRY:subsProfilePersistDTO", KnGDPRTemplate.mdn(mdn));
        Connection conn;
        PreparedStatement pStmt = null;
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(QRY_UPDATE_USER_PROFILE_NAME);
            knLogger.debug("startin22g the job");
            pStmt.setString(1, userProfileName);
            pStmt.setString(2,mdn);
            knLogger.debug(methodName, "QUERY: Executing the Query - ", QRY_UPDATE_USER_PROFILE_NAME);
            pStmt.executeQuery();
            knLogger.debug(methodName, "Query: Executed ");
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to updateSubscrPkgAddlInfo - " + e.getMessage(), pttServerId,
                    KnDAOSourceTypes.POCSUBSCRADDLINFO, QRY_UPDATE_USER_PROFILE_NAME);
        } finally {
            KnDbUtil.closeStatement(pStmt);
        }
    }

    public void updateSegmentIndicator(KnPersisterTxn persistTxn, List<String> mdns,Map<String, String> mdnSegmentIndicator) throws KnDBPersistenceException, KnDBConnectionException, KnDAOException {
        // TODO Auto-generated method stub
        String methodName = "updateSegmentIndicator";
        knLogger.debug(methodName, "Entry : persistTxn ");
        Connection conn = null;
        PreparedStatement selectPstmt = null;
        PreparedStatement updatePstmt = null;
        ResultSet result = null;
        StringBuilder buffer = new StringBuilder(200);
        Map<Integer,String> fanIDMdn = new HashMap<>();
        try {
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            buffer.append(SELECT_FAN_ID).append("(");
            buffer.append(formCommaSeperatedIdList(mdns)).append(");");
            selectPstmt = conn.prepareStatement(buffer.toString());
            result = selectPstmt.executeQuery();
            while(result.next()) {
                int fanID = result.getInt(1);
                String mdn = result.getString(2);
                fanIDMdn.put(fanID, mdn);
            }
            KnDbUtil.closeResultSet(result); result = null;
            KnDbUtil.closeStatement(selectPstmt); selectPstmt = null;
            String updatequery = UPDATE_SEGMENT_INDICATOR;
            updatePstmt = conn.prepareStatement(updatequery);

            for(Map.Entry<Integer, String> entry: fanIDMdn.entrySet()) {
                int fai_id = entry.getKey();
                String segemtindicator = mdnSegmentIndicator.get(entry.getValue());
                updatePstmt.setString(1, segemtindicator);
                updatePstmt.setInt(2, fai_id);
                updatePstmt.addBatch();

            }
            updatePstmt.executeBatch();

            knLogger.debug("Query executed successfully");
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception -", e);
            throw KnDbUtil.processException(e, "Failed to segmentIndicator " + e.getMessage(), pttServerId,
                    KnDAOSourceTypes.POCSUBSCRINFO, buffer.toString());

        } finally {
            KnDbUtil.closeResultSet(result);
            KnDbUtil.closeStatement(selectPstmt);
            KnDbUtil.closeStatement(updatePstmt);
            knLogger.debug(methodName, "EXIT : updateSegmentIndicator");
        }


    }

    public Map<String, String> getSegIndEnableSubs(KnPersisterTxn persistTxn) throws KnDBPersistenceException, KnDBConnectionException, KnDAOException {
        // TODO Auto-generated method stub
        String methodName = "getSegIndEnableSubs";
        knLogger.debug(methodName, "Entry : persistTxn ");
        Connection conn;
        PreparedStatement pstmt;
        ResultSet rs = null;
        Map<String,String> mdnSegmentIndicator = new HashMap<>();
        StringBuilder buffer = new StringBuilder(200);
        try {
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            buffer.append(FIRSTNET_SEGMENT_ENABLED_SUBSCRIBER);
            pstmt = conn.prepareStatement(buffer.toString());
            knLogger.debug(methodName, "Query: Executing - ", buffer.toString());
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query: Executed ");

            while (rs.next()) {
                mdnSegmentIndicator.put(rs.getString(1),rs.getString(2));
            }

            knLogger.debug(methodName, "MDN segment indicator ",mdnSegmentIndicator);
        }catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception -", e);
            throw KnDbUtil.processException(e, "Failed to segmentIndicator " + e.getMessage(), pttServerId,
                    KnDAOSourceTypes.POCSUBSCRINFO, buffer.toString());

        } finally {
            KnDbUtil.closeResultSet(rs);
            knLogger.debug(methodName, "EXIT : getSegIndEnableSubs");
        }
        return mdnSegmentIndicator;
    }

    public String selectSubscriberMdnByMCSId(List<String> mcsIds, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "selectSubscriberMdnByMCSId(List<String>, KnPersisterTxn)";
        Connection conn;
        ResultSet rs = null;
        List<String> realMdns = new ArrayList<String>();
        StringBuilder buffer = new StringBuilder(200);
        String query = null;
        PreparedStatement pStmt;
        StringBuffer keys = com.kodiak.common.dao.KnDbUtil.convertListToStringBuffer(mcsIds);
        String mdn=null;
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            buffer.append(SELECT_BASE_MDN).append(MC_ID).append(" IN ").append(keys.toString()).append(" OR ").append(MC_PTTID)
                    .append(" IN ").append(keys.toString()).append(" OR ").append(MC_VIDEOID).append(" IN ")
                    .append(keys.toString()).append(" OR ").append(MC_DATAID).append(" IN ").append(keys.toString()).append(") AND (USERPROFILEINDEX=0 OR USERPROFILEINDEX IS NULL)");
            query = buffer.toString();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pStmt = conn.prepareStatement(query);
            knLogger.debug(methodName, "Query: Executing - ", query);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Query: Executed ");
            List <KnXDMSubsProvDTO> subsProvList= new ArrayList<KnXDMSubsProvDTO>();
            while (rs.next()) {
                mdn=(rs.getString(MDN));
            }

        } catch (SQLException e) {
            knLogger.error(methodName, "SQL Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve Base Mdn " + e.getMessage(), pttServerId,
                    KnDAOSourceTypes.POCSUBSCRINFO, "PV");

        } finally {
            KnDbUtil.closeResultSet(rs);
            knLogger.debug(methodName, "EXIT : Base Mdn ->", KnGDPRTemplate.mdn(mdn));
        }
        knLogger.exit(methodName, realMdns);
        return mdn;
    }

    public String getDeviceInfo(String deviceId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getDeviceInfo(deviceInfo, KnPersisterTxn)";
        knLogger.info(methodName, "Entry :  - DeviceID : ", deviceId);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = "SELECT DEVICE_CREATED_AS FROM DG.DEVICE_INFO WHERE DEVICEID=?;";
        String deviceCreatedAs = null;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, deviceId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                deviceCreatedAs = String.valueOf(rs.getInt(1));
            }
            knLogger.debug(methodName, "Query: Executed ", query);
        } catch (SQLException e) {
            knLogger.error(methodName, "SQL Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve deviceCreatedAs " + e.getMessage(), pttServerId,
                    KnDAOSourceTypes.DEVICE_INFO, "DEVICE_INFO");
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.info(methodName, "EXIT : deviceCreatedAs ->", deviceCreatedAs);
        }
        return deviceCreatedAs;
    }

    public List<String> getDeviceInfo(List<String> deviceId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getDeviceInfo(deviceInfo, KnPersisterTxn)";
        knLogger.info(methodName, "Entry :  - DeviceID : ", deviceId);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = "SELECT DEVICE_CREATED_AS FROM DG.DEVICE_INFO WHERE DEVICEID IN (DEVICEIDS);";
        List<String> deviceCreatedAs = new ArrayList<>();
        int index = 1;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(deviceId,query,"DEVICEIDS");
            pstmt = conn.prepareStatement(query);
            for(String devId : deviceId) {
                pstmt.setString(index++, devId);
            }
            rs = pstmt.executeQuery();
            while (rs.next()) {
                deviceCreatedAs.add(String.valueOf(rs.getInt(1)));
            }
            knLogger.debug(methodName, "Query: Executed ", query);
        } catch (SQLException e) {
            knLogger.error(methodName, "SQL Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve deviceCreatedAs " + e.getMessage(), pttServerId,
                    KnDAOSourceTypes.DEVICE_INFO, "DEVICE_INFO");
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.info(methodName, "EXIT : deviceCreatedAs ->", deviceCreatedAs);
        }
        return deviceCreatedAs;
    }

    public Map<String, String> getDeviceCreatedAsMap(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getDeviceCreatedAsMap(List<String>, KnPersisterTxn)";
        Map<String, String> deviceCreatedAsMap = new HashMap<>();
        if (mdnList == null || mdnList.isEmpty()) {
            return deviceCreatedAsMap;
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = "SELECT DEVICEID, DEVICE_CREATED_AS FROM DG.DEVICE_INFO WHERE DEVICEID IN (DEVICEIDS);";
        int index = 1;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(mdnList, query, "DEVICEIDS");
            pstmt = conn.prepareStatement(query);
            for (String devId : mdnList) {
                pstmt.setString(index++, devId);
            }
            rs = pstmt.executeQuery();
            while (rs.next()) {
                String mdn = rs.getString(1);
                if (mdn != null) {
                    deviceCreatedAsMap.put(mdn.trim(), String.valueOf(rs.getInt(2)));
                }
            }
            knLogger.debug(methodName, "Query: Executed ", query, ", resultSize=", deviceCreatedAsMap.size());
        } catch (SQLException e) {
            knLogger.error(methodName, "SQL Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve deviceCreatedAs map " + e.getMessage(), pttServerId,
                    KnDAOSourceTypes.DEVICE_INFO, "DEVICE_INFO");
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.info(methodName, "EXIT : deviceCreatedAsMap size ->", deviceCreatedAsMap.size());
        }
        return deviceCreatedAsMap;
    }

    public int getAllSubscribersCount(int corpId) throws KnDAOException, SQLException {
        String methodName = "getAllSubscribersCount";
        knLogger.info(methodName, "Entry :  - ");
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String query = "SELECT COUNT(MDN) FROM DG.POCSUBSCRINFO WHERE CORPID = ? ;";
        int subsCount = 0;
        try {
            Connection conn = KnDBManager.getDBManagerInstance().getConnection(KnGenInfoUtil.getSecDBPttServerId(pttServerId), false);
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, corpId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                subsCount = rs.getInt(1);
            }
        } catch (SQLException e) {
            knLogger.error("Exception Happened", e.getMessage());
            throw new SQLException(e);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(stmt);
        }
        knLogger.info(methodName, "EXIT : subsCount ->", subsCount);
        return subsCount;
    }

    public Map<String, String> getProfileMDNswithMCID(int corpId, int start, int end) throws SQLException {
        String methodName = "profileMdnswithMCID";
        knLogger.info(methodName, "Entry :  - ", start, end);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = "SELECT ROWS ? to ? MDN,MC_ID FROM DG.POCSUBSCRINFO WHERE nvl(USERPROFILEINDEX,0) !=0 AND CORPID=?;";
        Map<String, String> profileMdnswithMCID = new HashMap<>();
        try {
            Connection conn = KnDBManager.getDBManagerInstance().getConnection(KnGenInfoUtil.getSecDBPttServerId(pttServerId), false);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, start);
            pstmt.setInt(2, end);
            pstmt.setInt(3, corpId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                profileMdnswithMCID.put(rs.getString(1), rs.getString(2));
            }
            knLogger.debug("profileMdnswithMCID::", profileMdnswithMCID.size());
            return profileMdnswithMCID;
        } catch (Exception e) {
            knLogger.error("Exception Happened", e.getMessage());
            throw new RuntimeException(e);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public List<String> getProfileMDNswithNoBaseMDN(Map<String, String> profileMDNsWithMCID) {
        String methodName = "profileMDNsWithNoBaseMDN";
        knLogger.info(methodName, "Entry :  - ");
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = "SELECT MDN FROM DG.POCSUBSCRINFO WHERE MC_ID IN (?) AND USERPROFILEINDEX!=0";
        List<String> profileMDNsWithNoBaseMDN = new ArrayList<>();
        try {
            Connection conn = KnDBManager.getDBManagerInstance().getConnection(KnGenInfoUtil.getSecDBPttServerId(pttServerId), false);
            for (Map.Entry<String, String> entry : profileMDNsWithMCID.entrySet()) {
                pstmt = conn.prepareStatement(query);
                pstmt.setString(1, entry.getValue());
                rs = pstmt.executeQuery();
                while (rs.next()) {
                    profileMDNsWithNoBaseMDN.add(rs.getString(1));
                }
            }
            knLogger.debug("profileMDNsWithNoBaseMDN::", profileMDNsWithNoBaseMDN.size());
            return profileMDNsWithNoBaseMDN;
        } catch (Exception e) {
            knLogger.error("Exception Happened", e.getMessage());
            throw new RuntimeException(e);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public List<String> allProfileMDNs(KnPersisterTxn persisterTxn) {
        String methodName = "allProfileMDNs";
        knLogger.info(methodName, "Entry :  - ");
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = "SELECT MDN FROM DG.POCSUBSCRINFO WHERE nvl(USERPROFILEINDEX,0) !=0;";
        List<String> allProfileMDNs = new ArrayList<>();
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                allProfileMDNs.add(rs.getString(1));
            }
            knLogger.debug("allProfileMDNs::", allProfileMDNs.size());
            return allProfileMDNs;
        } catch (KnConnectionException e) {
            knLogger.error("Exception Happened", e.getMessage());
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public Map<String, List<String>> getInconsistenGroupId(String profileMdn, List<String> groupIds) {
        String methodName = "getInconsistenGroupId";
        knLogger.info(methodName, "Entry :  - ", profileMdn, groupIds);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = "SELECT CORPGROUPID from DG.CORPGROUPMEMBERLIST WHERE MEMBERMDN = ? AND CORPGROUPID IN (?)";
        Map<String, List<String>> mapOfProfileMdnWithGroupIds = new HashMap<>();
        List<String> groupIdList = new ArrayList<>();
        try {
            Connection conn = KnDBManager.getDBManagerInstance().getConnection(KnGenInfoUtil.getSecDBPttServerId(pttServerId), false);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, profileMdn);
            pstmt.setString(2, formCommaSeperatedIdList(groupIds));
            rs = pstmt.executeQuery();
            while (rs.next()) {
                groupIdList.add(rs.getString(1));
            }
            mapOfProfileMdnWithGroupIds.put(profileMdn, groupIdList);
            knLogger.debug("inconsistentGroupIds::", mapOfProfileMdnWithGroupIds.size());
        } catch (Exception e) {
            knLogger.error("Exception Happened", e.getMessage());
            throw new RuntimeException(e);
        }
        return mapOfProfileMdnWithGroupIds;
    }

    public Map<String, List<String>> getMapOfSublistIdWithContactMdn(String sublistId) {
        String methodName = "getMapOfSublistIdWithContactMdn";
        knLogger.info(methodName, "Entry :  - ");
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = "SELECT MEMBERMDN from DG.CORPLISTMEMBER where CORPLISTID= ?";
        Map<String, List<String>> mapOfSublistIdWithContactMdn = new HashMap<>();
        List<String> contactMdnList = new ArrayList<>();
        try {
            Connection conn = KnDBManager.getDBManagerInstance().getConnection(KnGenInfoUtil.getSecDBPttServerId(pttServerId), false);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, sublistId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                contactMdnList.add(rs.getString(1));
            }
            mapOfSublistIdWithContactMdn.put(sublistId, contactMdnList);
            knLogger.debug("mapOfSublistIdWithContactMdn::", mapOfSublistIdWithContactMdn.size());
            return mapOfSublistIdWithContactMdn;
        } catch (Exception e) {
            knLogger.error("Exception Happened", e.getMessage());
            throw new RuntimeException(e);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public List<String> ifSublistExistsinTT(List<String> sublistIds, KnPersisterTxn persisterTxn) {
        String methodName = "ifSublistExistsinTT";
        knLogger.info(methodName, "Entry :  - ");
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = "SELECT CORPLISTID from DG.CORPLISTINFO where CORPLISTID IN (?)";
        List<String> sublistIdList = new ArrayList<>();
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, formCommaSeperatedIdList(sublistIds));
            rs = pstmt.executeQuery();
            while (rs.next()) {
                sublistIdList.add(rs.getString(1));
            }
            knLogger.debug("sublistIdList::", sublistIdList.size());
            return sublistIdList;
        } catch (KnConnectionException e) {
            knLogger.error("Exception Happened", e.getMessage());
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public Map<String, List<String>> getProfileMdnsForUPMId(String upmId) {
        String methodName = "getProfileMdnsForUPMId";
        knLogger.info(methodName, "Entry :  - ", upmId);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = "SELECT MDN from DG.POCSUBSCRINFO where USERPROFILEID = ? AND USERPROFILEINDEX!=0";
        Map<String, List<String>> mapOfProfileMdnsForUPMId = new HashMap<>();
        List<String> profileMdnList = new ArrayList<>();
        try {
            Connection conn = KnDBManager.getDBManagerInstance().getConnection(KnGenInfoUtil.getSecDBPttServerId(pttServerId), false);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, upmId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                profileMdnList.add(rs.getString(1));
            }
            mapOfProfileMdnsForUPMId.put(upmId, profileMdnList);
            knLogger.debug("mapOfProfileMdnsForUPMId::", mapOfProfileMdnsForUPMId.size());
            return mapOfProfileMdnsForUPMId;
        } catch (Exception e) {
            knLogger.error("Exception Happened", e.getMessage());
            throw new RuntimeException(e);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public Map<String, List<String>> getContactMdnByProfileMdn(String profileMdn) {
        String methodName = "getContactMdnByProfileMdn";
        knLogger.info(methodName, "Entry :  - ", profileMdn);
        ResultSet rs = null;
        String query = "SELECT CONTACTMDN FROM DG.CORPCONTACTLIST WHERE MDN = ?";
        Map<String, List<String>> mapOfContactMdnByProfileMdn = new HashMap<>();
        PreparedStatement pstmt = null;
        List<String> contactMdnList = new ArrayList<>();
        try {
            Connection conn = KnDBManager.getDBManagerInstance().getConnection(KnGenInfoUtil.getSecDBPttServerId(pttServerId), false);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, profileMdn);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                contactMdnList.add(rs.getString(1));
            }
            mapOfContactMdnByProfileMdn.put(profileMdn, contactMdnList);
            knLogger.debug("mapOfContactMdnByProfileMdn::", mapOfContactMdnByProfileMdn.size());
            return mapOfContactMdnByProfileMdn;
        } catch (Exception e) {
            knLogger.error("Exception Happened", e.getMessage());
            throw new RuntimeException(e);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public String getFeatureBSForProfileMdnFromTT(String profileMdn) {
        String methodName = "getFeatureBSForProfileMdnFromTT";
        knLogger.info(methodName, "Entry :  - ", profileMdn);
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        String query = "SELECT USERPROFILEFS2 FROM DG.POCSUBSCRINFO WHERE MDN = ?";
        String featureBSForProfileMdn = null;
        try {
            Connection conn = KnDBManager.getDBManagerInstance().getConnection(KnGenInfoUtil.getSecDBPttServerId(pttServerId), false);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, profileMdn);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                featureBSForProfileMdn = rs.getString(1);
            }
            knLogger.debug(methodName, "featureBSForProfileMdn::", featureBSForProfileMdn);
        } catch (Exception e) {
            knLogger.error(methodName, "Exception Happened", e.getMessage());
            throw new RuntimeException(e);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        return featureBSForProfileMdn;
    }

    public Map<String, KnCorpUserProfileMCPTTConfig> getProfileMdnPerm(String profileMdn) {
        String methodName = "getProfileMdnPerm";
        knLogger.info(methodName, "Entry :  - ", profileMdn);
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        String query = "SELECT TARGET_MDN,PERM_BITSET FROM DG.MCPTT_PERM_INFO WHERE AUTHORIZED_MDN = ?";
        Map<String, KnCorpUserProfileMCPTTConfig> profileMdnPermMap = new HashMap<>();
        KnCorpUserProfileMCPTTConfig knCorpUserProfileMCPTTConfig = new KnCorpUserProfileMCPTTConfig();
        try {
            Connection conn = KnDBManager.getDBManagerInstance().getConnection(KnGenInfoUtil.getSecDBPttServerId(pttServerId), false);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, profileMdn);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                knCorpUserProfileMCPTTConfig.setMdn(rs.getString(1));
                knCorpUserProfileMCPTTConfig.setPermBitSet(rs.getLong(2));
                profileMdnPermMap.put(profileMdn, knCorpUserProfileMCPTTConfig);
            }
            knLogger.debug("featureBSForProfileMdn::", profileMdnPermMap.size());
            return profileMdnPermMap;
        } catch (Exception e) {
            knLogger.error("Exception Happened", e.getMessage());
            throw new RuntimeException(e);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public List<String> getGroupIdsForProfileMdn(String profileMdnItr) {
        String methodName = "getGroupIdsForProfileMdn";
        knLogger.info(methodName, "Entry :  - ", profileMdnItr);
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        String query = "SELECT CORPGROUPID FROM DG.CORPGROUPMEMBERLIST WHERE MEMBERMDN = ?";
        List<String> groupIdsForProfileMdn = new ArrayList<>();
        try {
            Connection conn = KnDBManager.getDBManagerInstance().getConnection(KnGenInfoUtil.getSecDBPttServerId(pttServerId), false);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, profileMdnItr);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                groupIdsForProfileMdn.add(rs.getString(1));
            }
            knLogger.debug("groupIdsForProfileMdn::", groupIdsForProfileMdn.size());
            return groupIdsForProfileMdn;
        } catch (Exception e) {
            knLogger.error("Exception Happened", e.getMessage());
            throw new RuntimeException(e);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public KnEmergencyInfoDTO getEmergencyInfoForProfileMdn(String profileMdnItr) {
        String methodName = "getEmergencyInfoForProfileMdn";
        knLogger.info(methodName, "Entry :  - ", profileMdnItr);
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        String query = "SELECT EMERGDESTPRIORITY,EMERGDESTTYPE,EMERGDEST FROM DG.EMERGENCY_SUBSCR_DESTINFO WHERE MDN = ?";
        KnEmergencyInfoDTO knEmergencyInfoDTO = new KnEmergencyInfoDTO();
        try {
            Connection conn = KnDBManager.getDBManagerInstance().getConnection(KnGenInfoUtil.getSecDBPttServerId(pttServerId), false);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, profileMdnItr);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                knEmergencyInfoDTO.setDestPriority(Integer.valueOf(rs.getString(1)));
                knEmergencyInfoDTO.setDestType(Integer.valueOf(rs.getString(2)));
                knEmergencyInfoDTO.setDestination(rs.getString(3));
            }
            knLogger.debug("knEmergencyInfoDTO::", knEmergencyInfoDTO);
            return knEmergencyInfoDTO;
        } catch (Exception e) {
            knLogger.error("Exception Happened", e.getMessage());
            throw new RuntimeException(e);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public Map<Integer, List<String>> getSublistMemberBySublistId(List<Integer> subLists, KnPersisterTxn persisterTxn) {
        String methodName = "getSublistMemberBySublistId";
        knLogger.info(methodName, "Entry :  - ", subLists);
        ResultSet rs = null;
        Connection conn = null;
        boolean ownedTxn = false;
        String query = "select CORPLISTID, MEMBERMDN from DG.CORPLISTMEMBER where CORPLISTID in (SUBLISTS);";
        query = KnGeneralUtil.replaceContactWithValue(query, "SUBLISTS", KnGeneralUtil.formIntegerCommaSeperatedIdList(subLists));
        knLogger.info("Final  query with data :- ", query);
        Map<Integer, List<String>> mapOfSublistIdsByProfileMdn = new HashMap<>();
        PreparedStatement pstmt = null;
        try {
            if (persisterTxn != null) {
                knLogger.debug(methodName, " Data Store before - ", KnDBConst.DataStores.XDM_SHARED_DATA);
                conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
                knLogger.debug(methodName, "if block");
            } else {
                conn = KnDBManager.getDBManagerInstance().getConnection(KnGenInfoUtil.getSecDBPttServerId(pttServerId), true);
                ownedTxn = true;
                knLogger.debug(methodName, "else block");
                knLogger.debug(methodName, "conn", conn);
            }
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                if (!mapOfSublistIdsByProfileMdn.containsKey(rs.getInt(1))) {
                    List<String> memberMdn = new ArrayList<>();
                    memberMdn.add(rs.getString(2).trim());
                    mapOfSublistIdsByProfileMdn.put(rs.getInt(1), memberMdn);
                } else {
                    List<String> memberMdn = mapOfSublistIdsByProfileMdn.get(rs.getInt(1));
                    memberMdn.add(rs.getString(2).trim());
                }
            }
            knLogger.debug("mapOfSublistIdsByProfileMdn::", mapOfSublistIdsByProfileMdn);
            return mapOfSublistIdsByProfileMdn;
        } catch (KnConnectionException e) {
            knLogger.error("Exception Happened", e.getMessage());
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            if (ownedTxn) {
                KnDbUtil.closeConnection(conn);
            }
        }
    }

    public Map<String, Map<Integer, Integer>> getSublistDetailByProfileMdns(List<String> profileMdn, KnPersisterTxn persisterTxn) {
        String methodName = "getSublistDetailByProfileMdns";
        knLogger.info(methodName, "Entry :  - ", profileMdn);
        ResultSet rs = null;
        Connection conn = null;
        boolean ownedTxn = false;
        String query = "SELECT CDI.RECIPIENTMDN,CLI.CORPLISTID,CLI.LISTDISTRIBUTIONPOLICY FROM DG.CORPLISTINFO CLI, DG.CORPLISTDISTINFO CDI WHERE CDI.RECIPIENTMDN in (PROFILEMDNLIST) AND CLI.CORPLISTID=CDI.CORPLISTID;";
        query = KnGeneralUtil.replaceContactWithValue(query, "PROFILEMDNLIST", KnGeneralUtil.formCommaSeperatedIdList(profileMdn));
        knLogger.info("Final  query with data :- ",query);
        Map<String, Map<Integer, Integer>> mapOfSublistIdsByProfileMdn = new HashMap<>();
        PreparedStatement pstmt = null;
        try {
            //Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            if (persisterTxn != null) {
                knLogger.debug(methodName, " Data Store before - ", KnDBConst.DataStores.XDM_SHARED_DATA);
                conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
                knLogger.debug(methodName, "if block");
            } else {
                conn = KnDBManager.getDBManagerInstance().getConnection(KnGenInfoUtil.getSecDBPttServerId(pttServerId), true);
                ownedTxn = true;
                knLogger.debug(methodName, "else block");
                knLogger.debug(methodName, "conn", conn);
            }
            pstmt = conn.prepareStatement(query);
            //pstmt.setString(1, formCommaSeperatedIdList(profileMdn));
            rs = pstmt.executeQuery();
            while (rs.next()) {
                knLogger.debug("Inside while loop process:- ",rs.toString());
                if (!mapOfSublistIdsByProfileMdn.containsKey(rs.getString(1).trim())) {
                    Map<Integer, Integer> sublistIdList = new HashMap<>();
                    sublistIdList.put(rs.getInt(2), rs.getInt(3));
                    mapOfSublistIdsByProfileMdn.put(rs.getString(1).trim(), sublistIdList);
                } else {
                    Map<Integer, Integer> sublistIdList = mapOfSublistIdsByProfileMdn.get(rs.getString(1).trim());
                    sublistIdList.put(rs.getInt(2), rs.getInt(3));
                }
            }
            knLogger.debug("mapOfSublistIdsByProfileMdn::", mapOfSublistIdsByProfileMdn);
            return mapOfSublistIdsByProfileMdn;
        } catch (KnConnectionException e) {
            knLogger.error("Exception Happened", e.getMessage());
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            if (ownedTxn) {
                KnDbUtil.closeConnection(conn);
            }
        }
    }

    /** Method to retrieve AUTO_ASSIGN_TRACKER flag for a list of MDNs
     *
     * @param mdnList
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */

    public Map<String, String> getAutoAssignTrackerForMdns(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getAutoAssignTrackerForMdns(List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry: mdnList size - ", mdnList.size());
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Map<String, String> mdnAutoAssignTrackerMap = new HashMap<>();
        StringBuilder buffer = new StringBuilder(200);

        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            buffer.append(QRY_SELECT_AUTO_ASSIGN_TRACKER);
            buffer.append(KnDbUtil.convertListToStringBuffer(mdnList));
            String query = buffer.toString();
            pstmt = conn.prepareStatement(query);
            knLogger.debug(methodName, "Query: Executing - ", query);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query: Executed");

            while (rs.next()) {
                String mdn = rs.getString(MDN);
                String autoAssignTracker = rs.getString(AUTO_ASSIGN_TRACKER);
                mdnAutoAssignTrackerMap.put(mdn.trim(), autoAssignTracker);
            }

            knLogger.debug(methodName, "Retrieved AUTO_ASSIGN_TRACKER for ", mdnAutoAssignTrackerMap.size(), " MDNs");
            return mdnAutoAssignTrackerMap;

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to get AUTO_ASSIGN_TRACKER for MDNs - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.POCSUBSCRADDLINFO, buffer.toString());
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.debug(methodName, "EXIT: getAutoAssignTrackerForMdns");
        }
    }


    /**
     * Method to update AUTO_ASSIGN_TRACKER flag for multiple subscribers in bulk
     *
     * @param mdns              List<String> - List of subscriber MDNs
     * @param trackerValue      Integer - Tracker value (0 = Manually assigned, 1 = Auto assigned, null = Default)
     * @param persisterTxn      KnPersisterTxn - Database transaction
     * @throws KnDAOException   DB Layer Exception
     */

    public void updateAutoAssignTrackerBulk(List<String> mdns, Integer trackerValue, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        final String methodName = "updateAutoAssignTrackerBulk(List<String> mdns, Integer trackerValue, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "ENTRY: mdns count=", mdns.size(), ", trackerValue=", trackerValue);
        Connection conn;
        PreparedStatement pStmt = null;
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(QRY_UPDATE_AUTO_ASSIGN_TRACKER);

            for (String mdn : mdns) {
                if (trackerValue != null) {
                    pStmt.setInt(1, trackerValue);
                } else {
                    pStmt.setNull(1, java.sql.Types.SMALLINT);
                }
                pStmt.setString(2, mdn);
                pStmt.addBatch();
            }

            knLogger.debug(methodName, "QUERY: Executing batch - ", QRY_UPDATE_AUTO_ASSIGN_TRACKER);
            int[] rowsUpdated = pStmt.executeBatch();
            knLogger.debug(methodName, "Query: Executed, total rows updated: ", rowsUpdated.length);

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to updateAutoAssignTrackerBulk - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.POCSUBSCRADDLINFO, QRY_UPDATE_AUTO_ASSIGN_TRACKER);
        } finally {
            KnDbUtil.closePrepareStmt(pStmt);
            knLogger.debug(methodName, "EXIT");
        }
    }
}
