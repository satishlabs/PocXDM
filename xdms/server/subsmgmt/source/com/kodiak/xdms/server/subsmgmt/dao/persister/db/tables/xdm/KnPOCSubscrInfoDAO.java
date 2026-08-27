/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *****************************************************************************
 * File name:   KnPOCSubscrInfoDAO.java
 * Subsystem:   Provisioning Library
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ravi Shanker .P       Dec 15, 2010       7.0
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
 * *******************************************************************************
 */
package com.kodiak.xdms.server.subsmgmt.dao.persister.db.tables.xdm;

/*import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryResult;
import com.couchbase.client.java.query.N1qlQueryRow;
import com.couchbase.client.java.query.ParameterizedN1qlQuery;*/
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.query.QueryResult;
import com.kodiak.common.commdto.response.KnCorporateProfilepersistDTO1;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersistenceException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnConstants.HIERARCHY_TYPE;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.dbmgr.KnDBManager;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.cb.util.KnCBSRepository;
import com.kodiak.xdms.server.common.cb.util.KnCouchDbManager;
import com.kodiak.xdms.server.common.dao.persister.db.KnDBConnectionException;
import com.kodiak.xdms.server.common.dao.persister.db.KnDBPersistenceException;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.common.KnDocChangeListDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.resources.KnConstants;
import com.kodiak.xdms.server.common.resources.KnDAOSourceTypes;
import com.kodiak.xdms.server.subsmgmt.business.KnProvBOException;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnIPSubscriberInfoDTO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPSubsDispatcherDTO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPSubsProfileInfoDTO;
import com.kodiak.xdms.server.subsmgmt.dto.common.KnSubsProfileDTO;
import com.kodiak.xdms.server.subsmgmt.dto.persistdat.KnBulkSubsProfilePersistDTO;
import com.kodiak.xdms.server.subsmgmt.dto.persistdat.KnSubsAddlTGInfoDTO;
import com.kodiak.xdms.server.subsmgmt.dto.persistdat.KnSubsProfilePersistDTO;
import com.kodiak.xdms.server.subsmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvDAOSourceTypes;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;
import java.util.concurrent.RejectedExecutionException;
import java.util.stream.Collectors;

import static com.kodiak.xdms.server.common.dao.persister.db.tables.xdm.KnXDMSubscriberInfoDAO.ACTIVEFS2;
import static com.kodiak.xdms.server.common.dao.persister.db.tables.xdm.KnXDMSubscriberInfoDAO.EMERGCONFTIME;
import static com.kodiak.xdms.server.common.resources.KnConstants.*;
import static com.kodiak.xdms.server.common.resources.KnErrorCodes.BOEntity.CBS_NOT_REACHABLE;
import static com.kodiak.xdms.server.common.util.KnGeneralUtil.formCommaSeperatedIdList;
import static com.kodiak.xdms.server.subsmgmt.business.helper.KnProvInfoUtil.buildMCSDOC;

public class KnPOCSubscrInfoDAO implements ITableDAO {
    private static final KnLogger knLogger = KnLogger.getLogger(KnPOCSubscrInfoDAO.class);

    private static final String className = KnPOCSubscrInfoDAO.class.getName();
    private String pttServerId;
    public static final String TELURI = "tel:+";
    private static final String MDN = "MDN";
    private static final String POC_HOME = "POCHOME";
    private static final String PRESENCE_HOME = "PRESENCEHOME";
    private static final String XDMS_HOME = "XDMSHOME";
    private static final String SUBSCR_CREATION_TIME = "SUBSCRCREATIONTIME";
    private static final String LAST_PROFILE_UPDATE_TIME = "LASTPROFILEUPDATETIME";
    private static final String SUBSCR_NAME = "SUBSCRNAME";
    private static final String SERVICE_AUTH_STATUS = "SERVICEAUTHSTATUS";
    private static final String PUBLIC_SUBSCRIPTION_TYPE = "PUBLICSUBSCRIPTIONTYPE";
    private static final String CORP_SUBSCRIPTION_TYPE = "CORPSUBSCRIPTIONTYPE";
    private static final String CORP_ID = "CORPID";
    private static final String CORPCONTACTLISTID = "CORPCONTACTLISTID";
    private static final String CORP_CONTACT_PAIRING_IND = "CORPCONTACTPAIRINGIND";
    private static final String IMEI = "IMEI";
    private static final String CLIENT_PASSWORD = "CLIENT_PASSWORD";
    private static final String PAY_TYPE = "PAYTYPE";
    private static final String AFFILIATE_ID = "AFFLIATEID";
    private static final String USER_AGENT = "USERAGENT";
    private static final String EMAIL = "EMAIL";
    private static final String CLIENT_TYPE = "CLIENT_TYPE";
    private static final String DISPATCH_GRP_MEMBER = "DISPATCH_GRP_MEMBER";
    private static final String ACTIVE_FS1 = "ACTIVEFS1";
    private static final String CLIENT_FS1 = "CLIENTFS1";
    private static final String SUBS_FS1 = "SUBSCRIBERFS1";
    private static final String OPS_FS1 = "OPSFS1";
    private static final String CLIENTPV_MAJORVERSION = "CLIENTPV_MAJORVERSION";
    private static final String CLIENTPV_MINORVERSION = "CLIENTPV_MINORVERSION";
    private static final String DISPATCH_TYPE = "DISPATCH_TYPE";
    private static final String USER_ID = "USER_ID";
    private static final String LOWER_USER_ID = "LOWER(USER_ID)";
    private static final String DERIVED_KEY = "DERIVED_KEY";
    private static final String ACCOUNT_ID = "ACCOUNT_ID";
    private static final String PAMACC_ID = "PAMACCID";
    private static final String CORPADMIN_FS1 = "CORPADMINFS1";
    private static final String LAST_ACTIVATION_TIME = "LAST_ACTIVATION_TIME";
    private static final String CLIENT_SW_INF="CLIENT_SW_INFO";
    private static final String CLIENT_PLATFORM_TYPE="CLIENT_PLATFORM_TYPE";
    private static final String DYNAMIC_QOS_FLAG = "DYNAMICQOSFLAG";
    private static final String VOCODERID = "VOCODERID";
    private static final String ADDLINFO = "ADDLINFO";
    private static final String WEB_DISPATCH_ENABLED = "WEB_DISPATCH_ENABLED";
    private static final String SERVICE_STATUS_OP = "SERVICE_STATUS_OP";
    private static final String SERVICE_STATUS_AUTHUSER = "SERVICE_STATUS_AUTHUSER";

    private static final String UFMI = "UFMI";
    private static final String IDEN_USERNAME = "iDI_USERNAME";
    private static final String IDEN_PASSWORD = "iDI_E_PASSWORD";
    private static final String IDEN_BUSUNITID = "iDI_BUID";
    private static final String XDMS_FS1 = "XDMSFS1";
    private static final String DERIVEDKEY = "DERIVED_KEY";
    private static final String ALIAS_MDN = "ALIAS_MDN";
    private static final String LICENSE_TYPE = "LICENSE_TYPE";
    private static final String QPPPACKID = "QPPPACKID";
    private static final String SEGMENT_INDICATOR = "SEGMENT_INDICATOR";
    private static final String SQL_EXCEPTION_MSG = "SQL Exception occurred";
    private static final String MCPTT_COMPLIANCE = "MCPTT_COMPLIANCE";
    private static final String MC_PTTID = "MC_PTTID";
    private static final String TABLENAME = "DG.POCSUBSCRINFO";
    private static final String MDN_LIST = "MDNLIST";
    private static final String MCPTT_LIST = "MCPTTLIST";
    private static final String ACTIVE_FS2 = "ACTIVEFS2";
    private static final String CLIENT_FS2 = "CLIENTFS2";
    private static final String SUBS_FS2 = "SUBSCRIBERFS2";
    private static final String OPS_FS2 = "OPSFS2";
    private static final String CORPADMIN_FS2 = "CORPADMINFS2";
    private static final String USERPROFILEFS2 = "USERPROFILEFS2";
    private static final String XDMS_FS2 = "XDMSFS2";
    private static final String PRIVACY_OPT_STATUS = "PRIVACY_OPT_STATUS";
    private static final String  MC_VIDEOID = "MC_VIDEOID";
    private static final String  MC_DATAID = "MC_DATAID";
    private static final String  MC_ID = "MC_ID";
    private static final String  USERPROFILEINDEX = "USERPROFILEINDEX";
    private static final String  USERPROFILEID = "USERPROFILEID";
    private static final String  LASTPROFILEUPDATETIME = "LASTPROFILEUPDATETIME";
    private static final String  ISDEFAULTPROFILE = "ISDEFAULTPROFILE";
    private static final String FEATURE_REL_VERSION = "FEATURE_REL_VERSION";
    private static final String CAMERA_TYPE = "CAMERA_TYPE";
    private static final String EXT_GATEWAY_ID = "GW_ID";
    private static final String PREV_SERVICE_AUTH_STATUS = "PREVSERVICEAUTHSTATUS";
    private static final String CLUSTERID = "CLUSTERID";
    private static final String HIERARCHY_ID = "HIERARCHY_ID";
    private static final String HIERARCHY_ROOT = "HIERARCHY_ROOT";
    private static final String PREV_SERVICE_AUTH_STATUS_UPDATETIME = "PREVAUTHSTATUSUPDATETIME";

    private static final String UPDATE_QRY = "UPDATE " + TABLENAME + " SET ";
    private static final String DELETE_QRY = "DELETE FROM " + TABLENAME + " WHERE " + MDN + "= ?";
    private static final String CORP_SUBS_CNT_QRY = "SELECT COUNT(MDN) FROM " + TABLENAME + " WHERE " + CORP_ID + "= ?";
    private static final String CORP_SUBS_CNT_REAL_MDN_QRY = "SELECT COUNT(MDN) FROM " + TABLENAME + " WHERE " + CORP_ID + "= ?" + " AND nvl("+ USERPROFILEINDEX + ",0) = 0";
    private static final String SELECT_AUTH_STATUS_QRY = "SELECT " + SERVICE_AUTH_STATUS + " FROM " + TABLENAME + " WHERE " +
            USER_ID + "=?";
    //private static final String SUBS_CNT_QRY = "SELECT COUNT(MDN) FROM " + TABLENAME + " WHERE "+ USERPROFILEINDEX + "=0 OR "+USERPROFILEINDEX+" IS NULL";
    private static final String SUBS_CNT_QRY = "SELECT COUNT(MDN) FROM " + TABLENAME + " WHERE nvl("+ USERPROFILEINDEX + ",0) = 0";
    private static final String SUBS_CNT_PAM_QRY = "SELECT COUNT(MDN) FROM " + TABLENAME + " WHERE " + PAMACC_ID + "= ?"+" AND nvl("+ USERPROFILEINDEX + ",0) = 0";
    private static final String PAM_ACCOUNT_MDNS_QRY = "SELECT MDN FROM " + TABLENAME + " WHERE " + PAMACC_ID + "= ? ORDER BY MDN";
    private static final String PAM_ACCOUNT_MDNDETAILS_QRY = "SELECT MDN,SERVICEAUTHSTATUS FROM " + TABLENAME + " WHERE " + PAMACC_ID + "= ? ORDER BY MDN";
    private static final String NOTIFICATION_4_MDNS_QRY = "SELECT POCHOME, PRESENCEHOME, XDMSHOME, LASTPROFILEUPDATETIME,  CLIENTPV_MAJORVERSION," +
            " CLIENTPV_MINORVERSION FROM  " + TABLENAME + " WHERE MDN = ?";
    private static final String PAM_ACCOUNT_PROV_MDNS_QRY = "SELECT MDN FROM " + TABLENAME + " WHERE " + PAMACC_ID +
            "= ? AND SERVICEAUTHSTATUS=0 AND LAST_ACTIVATION_TIME  IS  NULL  ORDER BY MDN";
    private static final String PAM_ACCOUNT_LAST_SEQ_MDNS_QRY = "SELECT ROWS ? to ? MDN FROM " + TABLENAME + " WHERE " + PAMACC_ID + "= ? ORDER BY MDN DESC";
    private static final String GET_DISPATCH_QRY = "SELECT " + MDN + " , " + POC_HOME + " , " + PRESENCE_HOME + " , " + XDMS_HOME + " , " +
            CORP_ID + " , " + CORP_SUBSCRIPTION_TYPE + " , "+ CLIENT_TYPE + " , " + DISPATCH_GRP_MEMBER + " , " + SUBS_FS1 + " , " + CLIENT_FS1 + " , " +
            ACTIVE_FS1 + " , " + OPS_FS1 + " , " +XDMS_FS1+ " , " +QPPPACKID+ " , "+ CORPADMIN_FS1 +" , "+CLIENTPV_MAJORVERSION+ " , " +
            CLIENTPV_MINORVERSION + " , " + SEGMENT_INDICATOR + " , " + SUBS_FS2 + " , " + CLIENT_FS2 + " , " + ACTIVE_FS2 + " , " + OPS_FS2 + " , " +
            XDMS_FS2+ " , "+ CORPADMIN_FS2 + " , "+ LASTPROFILEUPDATETIME + " , "+ USERPROFILEFS2 + " FROM " + TABLENAME + " WHERE " + MDN + " IN ( " + MDN_LIST + " )";
    private static final String UPDATE_DISPATCH_QRY = "UPDATE " + TABLENAME + " SET " + LAST_PROFILE_UPDATE_TIME + " = ? ," + DISPATCH_GRP_MEMBER + " = ? ," +
            SUBS_FS1 + " = ? , " +XDMS_FS1+ " = ? , "+ ACTIVE_FS1 + " = ? , "+ QPPPACKID + " = ? , "+ SEGMENT_INDICATOR + " = ? , "+ CLIENT_TYPE+ " = ? ," +
            SUBS_FS2 + " = ? , " +XDMS_FS2+ " = ? , "+ ACTIVE_FS2 + " = ? WHERE " + MDN + " = ? ";

    private static final String QRY_UPDATE_ACCOUNTID = "UPDATE " + TABLENAME + " SET ACCOUNT_ID=? WHERE CORPID=?";
    private static final String UPDATE_SUBSCR_CORPID="UPDATE DG.POCSUBSCRINFO SET CORPID = ? , ACCOUNT_ID = ? WHERE MDN IN MDNLIST";
    private static final String UPDATE_BULK_SUBSCR_LASTPROFILETIME="UPDATE DG.POCSUBSCRINFO SET LASTPROFILEUPDATETIME WHERE MDN IN MDN_LIST";
    private static final String SELECT_MDN_QUERY="SELECT MDN FROM DG.POCSUBSCRINFO WHERE MDN=?";
    private static final String GET_PAM_ACCOUNT_MDNS_BY_INSERTION_TIME_QRY = "SELECT MDN FROM DG.POCSUBSCRINFO WHERE PAMACCID= ? AND SUBSCRCREATIONTIME >= ? ORDER BY MDN";
    public static final String UPDATE_CONTACT_LIST_ID_QUERY ="UPDATE DG.POCSUBSCRINFO SET CORPCONTACTLISTID=? WHERE MDN=?";
    public static final String SELECT_ADDRESS_BOOK_CONDITION =" AND UPPER(SUBSCRNAME) LIKE '%SEARCHSTRING%'";
    public static final String SELECT_ADDRESS_BOOK_CONDITION_WITH_ESCAPE =" AND UPPER(SUBSCRNAME) LIKE '%SEARCHSTRING%'  escape '"+com.kodiak.common.resources.KnConstants.ESCAPE_CHAR+"'";
    public static final String SELECT_ADDRESS_BOOK_QUERY ="select ROWS FIRST TO LAST MDN,SUBSCRNAME,ACTIVEFS2 from DG.POCSUBSCRINFO WHERE CORPID=? AND ( USERPROFILEINDEX = 0 OR USERPROFILEINDEX IS NULL ) "+SELECT_ADDRESS_BOOK_CONDITION + " ORDER BY "+SUBSCR_NAME;
    public static final String SELECT_ADDRESS_BOOK_QUERY_WITH_ESCAPE ="select ROWS FIRST TO LAST MDN,SUBSCRNAME,ACTIVEFS2 from DG.POCSUBSCRINFO WHERE CORPID=? AND ( USERPROFILEINDEX = 0 OR USERPROFILEINDEX IS NULL ) "+SELECT_ADDRESS_BOOK_CONDITION_WITH_ESCAPE + " ORDER BY "+SUBSCR_NAME;
    private static final String GET_SUBS_INFO_BY_MCPTTID ="SELECT "+CLIENT_PASSWORD+","+MDN+","+EMAIL+","+MC_PTTID+" FROM "+TABLENAME+" WHERE "+ MC_PTTID+" =? and "+ USERPROFILEINDEX +"=0;";
    public static final String SELECT_PROFILEIDMDN_QUERY ="select "+MC_PTTID+","+USERPROFILEINDEX+","+MDN+" from DG.POCSUBSCRINFO WHERE "+MC_PTTID+" IN ( "+ MCPTT_LIST+" )";
    private static final String GET_MDN_FOR_UNASSIGN = "Select B.MDN from DG.POCSUBSCRINFO A, DG.POCSUBSCRINFO B where A.MC_ID = B.MC_ID and A.MDN = ? and B.USERPROFILEID = ?";
    private static final String GET_MDN_FOR_UPM = "Select B.MDN,B.USERPROFILEFS2,B.XDMSFS2,B.ACTIVEFS2 from DG.POCSUBSCRINFO A, DG.POCSUBSCRINFO B where A.MC_ID = B.MC_ID and A.MDN = ?";
    private static final String GET_MDN_FOR_UPM_LIST = "Select B.MDN from DG.POCSUBSCRINFO A, DG.POCSUBSCRINFO B where A.MC_ID = B.MC_ID and A.MDN IN (?)";
    private static final String CB_BUCKET_NAME = "pocdata";
    private static final String USER_PROFILE_INDEX = "userProfileIndex";
    private static final String USER_PROFILE_NAME = "userProfileName";
    private static final String CORPORATE_ID = "corporateID";
    private static final String GET_USER_PROFILE_NAME = "select " + USER_PROFILE_NAME + " from `" + CB_BUCKET_NAME + "` where " + CORPORATE_ID + "=$corpId and " + USER_PROFILE_INDEX + "=$userProfileIndex";
    private static final String GET_SUBS_MCID_UPDTS_BY_MDNS="SELECT MDN,MC_ID,LASTPROFILEUPDATETIME,MCPTT_COMPLIANCE,USERPROFILEINDEX,CLIENTPV_MAJORVERSION,ACTIVEFS2 FROM DG.POCSUBSCRINFO WHERE ";
    private static final String UPDATE_SUBS_PROFILE_LAST_UPDATE_TIME="UPDATE DG.POCSUBSCRINFO SET LASTPROFILEUPDATETIME = ? WHERE MDN = ? ";
    private static final String GET_PROFLEMDNS_BY_BASEMDN = " SELECT MDN FROM DG.POCSUBSCRINFO WHERE MC_ID= (SELECT MC_ID FROM DG.POCSUBSCRINFO WHERE MDN=?) " +
            "AND ( USERPROFILEINDEX !=  0 AND USERPROFILEINDEX IS NOT NULL ) ";
    private static final String GET_MAX_USERPROFILEINDEX = "SELECT MAX(USERPROFILEINDEX) FROM DG.POCSUBSCRINFO WHERE MC_ID= (SELECT MC_ID FROM DG.POCSUBSCRINFO WHERE MDN=?)";
    private static final String GET_MC_ID_LIST_BY_REQUESTING_MDN_LIST = "SELECT MDN,MC_ID,USERPROFILEINDEX FROM DG.POCSUBSCRINFO WHERE MDN IN (MDNLIST)";
    private static final String GET_BASE_MDN_LIST_BY_MC_ID_LIST = "SELECT MDN,MC_ID FROM DG.POCSUBSCRINFO WHERE MC_ID IN (MC_ID_LIST) AND (USERPROFILEINDEX=0 OR USERPROFILEINDEX IS NULL)";

    private static final String CORP_SUBS_CNT_QRY_WITH_EXTCORPID = "SELECT COUNT(MDN) FROM " + TABLENAME + " WHERE " + ACCOUNT_ID + "= ?";
    private static final String MC_ID_LIST = "MC_ID_LIST";
//    private static final String SELECT_LIST_AUTH_STATUS_QRY = "SELECT " + SERVICE_AUTH_STATUS + " FROM " + TABLENAME;
    private static final String SUBS_ADDL_TABLENAME = "DG.SUBSCRIBER_ADDLINFO";
    private static final String DELETE_SUBS_ADDL_QUERY = "DELETE FROM " + SUBS_ADDL_TABLENAME + " WHERE " + MDN + "= ?";

    private KnCouchDbManager knCouchDbManager;


    public KnPOCSubscrInfoDAO(String pttServerId) {
        this.pttServerId = pttServerId;
        this.knCouchDbManager = KnCouchDbManager.getInstance();

    }

    public void insert(IPersistenceDTO persistenceDTO,Boolean userProfileCreate, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "insert(IPersistenceDTO, KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        boolean ownedTxn = false;
        try {
            if (persistTxn == null) {
                persistTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening Transaction");
                persistTxn.open();
                ownedTxn = true;
            }

            KnSubsProfilePersistDTO subsInfoPersistDto = (KnSubsProfilePersistDTO) persistenceDTO;
            knLogger.debug(methodName, "ENTRY: Create Subscriber Profile with DTO - ", subsInfoPersistDto, " Persist ", persistTxn);
            String mdn = subsInfoPersistDto.getMdn();
            String pocHome = subsInfoPersistDto.getPoCHome();
            String presenceHome = subsInfoPersistDto.getPresenceHome();
            String xdmsHome = subsInfoPersistDto.getXDMSHome();
            Long profileCreationTime = subsInfoPersistDto.getProfileCreationTime();
            Long lastProfileUpdateTime = subsInfoPersistDto.getLastProfileUpdateTime();
            String networkName = subsInfoPersistDto.getNetworkName();
            Boolean corpContactPairingInd = subsInfoPersistDto.getPairingInd();
            String aliasMdn=subsInfoPersistDto.getAliasMdn();
            knLogger.debug(methodName, "--->aliasMdn"+KnGDPRTemplate.mdn(aliasMdn));
            int pairingInd = -1;
            if (corpContactPairingInd != null) {
                if (corpContactPairingInd) {
                    pairingInd = 1;
                } else {
                    pairingInd = 0;
                }
            }
            int payType = subsInfoPersistDto.getPayType();
            int serviceAuthStatus = subsInfoPersistDto.getServiceAuthStatus();
            int serviceStatusOp = subsInfoPersistDto.getServiceStatusOp();
            int serviceStatusAuthUser = subsInfoPersistDto.getServiceStatusAuthUser();
            String affiliateId = subsInfoPersistDto.getAffiliateId();
            String imei = subsInfoPersistDto.getIMEI();

            int publicSubscriptionType = subsInfoPersistDto.getPublicSubscriptionType();
            int corpSubscriptionType = subsInfoPersistDto.getCorporateSubscriptionType();
            int corpId = -1;
            if (corpSubscriptionType == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
                corpId = subsInfoPersistDto.getCorpId();
            }
            String email = subsInfoPersistDto.getEmailAddress();
            int dispatchType = subsInfoPersistDto.getDispatchType();
            int clientSWType=subsInfoPersistDto.getSwType();
            int clientPlatformType=subsInfoPersistDto.getPlatformType();

            //subsInfoPersistDTO.
            int dynamicQosFlag=subsInfoPersistDto.getDynamicQosFlag();

            //vocoderId for PV=9
            int vocoderId = subsInfoPersistDto.getVocoderId();
            String userAgent = subsInfoPersistDto.getUserAgent();

            String derivedKey = subsInfoPersistDto.getDerivedKey();
            String clientPassword=subsInfoPersistDto.getClientPassword();
            String clusterId=subsInfoPersistDto.getClusterId();
            String hierarchyId=subsInfoPersistDto.getHierarchyId();
            String hierarchyRoot=subsInfoPersistDto.getHierarchyRoot();
            ArrayList<String> queryFields = new ArrayList<String>();
            queryFields.add(MDN);
            queryFields.add(POC_HOME);
            queryFields.add(PRESENCE_HOME);
            queryFields.add(XDMS_HOME);
            queryFields.add(SUBSCR_CREATION_TIME);
            queryFields.add(LAST_PROFILE_UPDATE_TIME);
            if (networkName != null && !networkName.equalsIgnoreCase("")) {
                queryFields.add(SUBSCR_NAME);
            }
            queryFields.add(SERVICE_AUTH_STATUS);
            queryFields.add(PUBLIC_SUBSCRIPTION_TYPE);
            queryFields.add(CORP_SUBSCRIPTION_TYPE);
            if (corpContactPairingInd != null) {
                queryFields.add(CORP_CONTACT_PAIRING_IND);
            }

            if (payType != -1) {
                queryFields.add(PAY_TYPE);
            }
            if (affiliateId != null) {
                queryFields.add(AFFILIATE_ID);
            }
            if (corpSubscriptionType == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
                queryFields.add(CORP_ID);
            }
            if (imei != null) {
                queryFields.add(IMEI);
            }
            // 7.2 parameters
            if (email != null) {
                queryFields.add(EMAIL);
            }
            queryFields.add(CLIENT_TYPE);
            queryFields.add(DISPATCH_GRP_MEMBER);
            queryFields.add(ACCOUNT_ID);
            queryFields.add(ADDLINFO);
            queryFields.add(DISPATCH_TYPE);
            queryFields.add(SERVICE_STATUS_OP);
            queryFields.add(SERVICE_STATUS_AUTHUSER);
            queryFields.add(UFMI);
            queryFields.add(IDEN_USERNAME);
            queryFields.add(IDEN_PASSWORD);
            queryFields.add(IDEN_BUSUNITID);
			queryFields.add(LICENSE_TYPE);
			queryFields.add(QPPPACKID);
			queryFields.add(SEGMENT_INDICATOR);
			queryFields.add(SUBS_FS1);
            queryFields.add(CLIENT_FS1);
            queryFields.add(ACTIVE_FS1);
            queryFields.add(OPS_FS1);
            queryFields.add(CORPADMIN_FS1);
            queryFields.add(XDMS_FS1);
            queryFields.add(SUBS_FS2);
            queryFields.add(CLIENT_FS2);
            queryFields.add(ACTIVE_FS2);
            queryFields.add(OPS_FS2);
            queryFields.add(CORPADMIN_FS2);
            queryFields.add(XDMS_FS2);
            queryFields.add(USER_ID);
			queryFields.add(MC_ID);
			queryFields.add(MC_PTTID);
			queryFields.add(MC_VIDEOID);
			queryFields.add(MC_DATAID);
			if (KnConstants.MCSCOMPLIANCE == subsInfoPersistDto.getMcsCompliance()||userProfileCreate) {
				queryFields.add(MCPTT_COMPLIANCE);
				if(subsInfoPersistDto.getLastActivationTime()!=null)
				{
				queryFields.add(LAST_ACTIVATION_TIME);
				}
				queryFields.add(CLIENTPV_MAJORVERSION);
				queryFields.add(CLIENTPV_MINORVERSION);
            }
            queryFields.add(ISDEFAULTPROFILE);
            queryFields.add(USERPROFILEINDEX);
            queryFields.add(USERPROFILEID);
            queryFields.add(ALIAS_MDN);
            queryFields.add(FEATURE_REL_VERSION);
            queryFields.add(USERPROFILEFS2);
            queryFields.add(USER_AGENT);
			if (userProfileCreate) {
				queryFields.add(CLIENT_SW_INF);
				queryFields.add(CLIENT_PLATFORM_TYPE);
				queryFields.add(DYNAMIC_QOS_FLAG);
				if (derivedKey != null) {
					queryFields.add(DERIVEDKEY);
				}
				if (vocoderId != 0) {
					queryFields.add(VOCODERID);
				}
				if (clientPassword != null) {
					queryFields.add(CLIENT_PASSWORD);
				}
			}
            if (subsInfoPersistDto.getCameraType() != null) {
                queryFields.add(CAMERA_TYPE);
            }
            queryFields.add(PREV_SERVICE_AUTH_STATUS);
            if(subsInfoPersistDto.getExtGatewayId()!=null){
                queryFields.add(EXT_GATEWAY_ID);
            }
            if(subsInfoPersistDto.getEmergConfigTimer() != null) {
                queryFields.add(EMERGCONFTIME);
            }
            if (clusterId != null) {
                queryFields.add(CLUSTERID);
            }
            if (hierarchyId != null) {
                queryFields.add(HIERARCHY_ID);
            }
            if (hierarchyRoot != null) {
                queryFields.add(HIERARCHY_ROOT);
            }
            query = KnDbUtil.getInsertQuery(TABLENAME, queryFields);
            knLogger.debug(methodName,"QUERY :",query);
            knLogger.debug(methodName, " Data Store before - ", KnDBConst.DataStores.XDM_SHARED_DATA, persistenceDTO);
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            knLogger.debug(methodName, " Connection .. ", conn);
            /*  KnDBConst.DataStores.
        conn = persistTxn.getDBConnection(pttServerId, , false);*/
            pStmt = conn.prepareStatement(query);

            int columnIndex = 0; // will dynamically update the column index value as per the received values.
            pStmt.setString(++columnIndex, mdn);
            pStmt.setString(++columnIndex, pocHome);
            pStmt.setString(++columnIndex, presenceHome);
            pStmt.setString(++columnIndex, xdmsHome);
            pStmt.setLong(++columnIndex, profileCreationTime);
            pStmt.setLong(++columnIndex, lastProfileUpdateTime);
            if (networkName != null && !networkName.equalsIgnoreCase("")) {
            	//multilingual revert changes
            	networkName = new String(networkName.getBytes("UTF-8"),"8859_1");
                pStmt.setString(++columnIndex, networkName);
            }
            pStmt.setInt(++columnIndex, serviceAuthStatus);
            pStmt.setInt(++columnIndex, publicSubscriptionType);
            pStmt.setInt(++columnIndex, corpSubscriptionType);
            if (corpContactPairingInd != null) {
                pStmt.setInt(++columnIndex, pairingInd);
            }
            if (payType != -1) {
                pStmt.setInt(++columnIndex, payType);
            }
            if (affiliateId != null) {
                pStmt.setString(++columnIndex, affiliateId);
            }
            if (corpSubscriptionType == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
                pStmt.setInt(++columnIndex, corpId);
            }
            if (imei != null) {
                pStmt.setString(++columnIndex, imei);
            }
            //7.2 values
            if (email != null) {
                pStmt.setString(++columnIndex, subsInfoPersistDto.getEmailAddress());
            }
            pStmt.setInt(++columnIndex, subsInfoPersistDto.getSubsClientType());
            pStmt.setInt(++columnIndex, subsInfoPersistDto.getDispatchGroupMember());
            pStmt.setString(++columnIndex, subsInfoPersistDto.getAccountId());
            pStmt.setInt(++columnIndex, subsInfoPersistDto.getHierarchyType().value());
            pStmt.setInt(++columnIndex, dispatchType);
            pStmt.setInt(++columnIndex, serviceStatusOp);
            pStmt.setInt(++columnIndex, serviceStatusAuthUser);
            pStmt.setString(++columnIndex, subsInfoPersistDto.getUfmi());
            pStmt.setString(++columnIndex, subsInfoPersistDto.getiDenUserName());
            pStmt.setString(++columnIndex, subsInfoPersistDto.getiDenPassword());
            pStmt.setString(++columnIndex, subsInfoPersistDto.getiDenBusUnitId());
			pStmt.setInt(++columnIndex, subsInfoPersistDto.getLicenseType());
			pStmt.setInt(++columnIndex, subsInfoPersistDto.getQppPkgId());
			pStmt.setString(++columnIndex, subsInfoPersistDto.getFirstNetIndicator());
			pStmt.setLong(++columnIndex, KnGeneralUtil.convertHexStringToLong(subsInfoPersistDto.getSubsFS2()));
            pStmt.setLong(++columnIndex, KnGeneralUtil.convertHexStringToLong(subsInfoPersistDto.getClientFS2()));
            pStmt.setLong(++columnIndex, KnGeneralUtil.convertHexStringToLong(subsInfoPersistDto.getActiveFS2()));
            pStmt.setLong(++columnIndex, KnGeneralUtil.convertHexStringToLong(subsInfoPersistDto.getOpsFS2()));
            pStmt.setLong(++columnIndex, KnGeneralUtil.convertHexStringToLong(subsInfoPersistDto.getCorpAdminFS2()));
            pStmt.setLong(++columnIndex, KnGeneralUtil.convertHexStringToLong(subsInfoPersistDto.getXdmsFS2()));
            pStmt.setString(++columnIndex, KnGeneralUtil.getFeatureSet(subsInfoPersistDto.getSubsFS2()));
            pStmt.setString(++columnIndex, KnGeneralUtil.getFeatureSet(subsInfoPersistDto.getClientFS2()));
            pStmt.setString(++columnIndex, KnGeneralUtil.getFeatureSet(subsInfoPersistDto.getActiveFS2()));
            pStmt.setString(++columnIndex, KnGeneralUtil.getFeatureSet(subsInfoPersistDto.getOpsFS2()));
            pStmt.setString(++columnIndex, KnGeneralUtil.getFeatureSet(subsInfoPersistDto.getCorpAdminFS2()));
            pStmt.setString(++columnIndex, KnGeneralUtil.getFeatureSet(subsInfoPersistDto.getXdmsFS2()));
            pStmt.setString(++columnIndex, subsInfoPersistDto.getUserId());
			pStmt.setBytes(++columnIndex, subsInfoPersistDto.getMcId().getBytes(StandardCharsets.UTF_8));
			pStmt.setBytes(++columnIndex, subsInfoPersistDto.getMcpttId().getBytes(StandardCharsets.UTF_8));
			pStmt.setBytes(++columnIndex, subsInfoPersistDto.getMcVideoId().getBytes(StandardCharsets.UTF_8));
			pStmt.setBytes(++columnIndex, subsInfoPersistDto.getMcDataId().getBytes(StandardCharsets.UTF_8));
			if (KnConstants.MCSCOMPLIANCE == subsInfoPersistDto.getMcsCompliance()||userProfileCreate) {

				pStmt.setInt(++columnIndex, subsInfoPersistDto.getMcsCompliance());
				if(subsInfoPersistDto.getLastActivationTime()!=null)
				{
				pStmt.setLong(++columnIndex, subsInfoPersistDto.getLastActivationTime());
				}
				pStmt.setInt(++columnIndex, subsInfoPersistDto.getClientPVmajorVer());
				pStmt.setInt(++columnIndex, subsInfoPersistDto.getClientPVminorVer());

            }
            pStmt.setInt(++columnIndex, subsInfoPersistDto.getIsDefaultProfile());
            pStmt.setInt(++columnIndex, subsInfoPersistDto.getUserProfileIndex());
            pStmt.setString(++columnIndex, subsInfoPersistDto.getUserProfileId());
            pStmt.setString(++columnIndex, aliasMdn);
            pStmt.setString(++columnIndex,String.valueOf(subsInfoPersistDto.getFeatureRelVersion()));
            pStmt.setString(++columnIndex, KnGeneralUtil.getFeatureSet(subsInfoPersistDto.getUserProfileFS2()));
            pStmt.setString(++columnIndex, userAgent);
            if (userProfileCreate) {
				pStmt.setInt(++columnIndex, clientSWType);
				pStmt.setInt(++columnIndex, clientPlatformType);

				pStmt.setInt(++columnIndex, dynamicQosFlag);
				if (derivedKey != null) {
					pStmt.setString(++columnIndex, derivedKey);
				}
				if (vocoderId != 0) {
					pStmt.setInt(++columnIndex, vocoderId);
				}
				if (clientPassword != null) {
					pStmt.setString(++columnIndex, clientPassword);
				}
			}
			if(subsInfoPersistDto.getCameraType() != null){
                pStmt.setInt(++columnIndex, subsInfoPersistDto.getCameraType());
            }
            pStmt.setInt(++columnIndex, subsInfoPersistDto.getPreviousServiceAuthStatusToStore());
            if(subsInfoPersistDto.getExtGatewayId() != null) {
                pStmt.setString(++columnIndex, subsInfoPersistDto.getExtGatewayId());
            }
            if(subsInfoPersistDto.getEmergConfigTimer() != null) {
                Float emergConfigTimer = Float.parseFloat(subsInfoPersistDto.getEmergConfigTimer());
                pStmt.setFloat(++columnIndex, emergConfigTimer);
            }
            if (null != clusterId) {
                pStmt.setString(++columnIndex, clusterId);
            }
            if (null != hierarchyId) {
                pStmt.setString(++columnIndex, hierarchyId);
            }
            if (null != hierarchyRoot) {
                pStmt.setString(++columnIndex, hierarchyRoot);
            }
            knLogger.debug(methodName, "Query: Executing -", query, ", persist DTO - ", persistenceDTO);
            pStmt.executeUpdate();
            knLogger.debug(methodName, "Query: Executed ");

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving transaction");
                persistTxn.save();
            }

        } catch (KnDAOException dbConne) {
            knLogger.error(methodName, "DAO Exception occurred");
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw dbConne;
        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQL Exception occurred");
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(sqlE, "Failed to create Subscriber Profile - " + sqlE.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            knLogger.error(methodName, e);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to create Subscriber Profile - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeStatement(pStmt);
            knLogger.debug(methodName, "EXIT : create Subscriber Profile");
        }

    }

    public void insert(KnBulkSubsProfilePersistDTO subsInfoPersistDto, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "insert(KnBulkSubsProfilePersistDTO, KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        boolean ownedTxn = false;

        knLogger.debug(methodName, "ENTRY: Create Subscriber Profile with DTO - ", subsInfoPersistDto, " Persist ", persistTxn);

        try {
            if (persistTxn == null) {
                persistTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening Transaction");
                persistTxn.open();
                ownedTxn = true;
            }


            KnSubsProfileDTO subsProfileDTO = subsInfoPersistDto.getSubsProfile();
            knLogger.debug(methodName,"subsProfileDTO :",subsProfileDTO );
            //  String mdn = subsInfoPersistDtoBulk[0].getMdn();
            String pocHome = subsProfileDTO.getPoCHome();
            String presenceHome = subsProfileDTO.getPresenceHome();
            String xdmsHome = subsProfileDTO.getXDMSHome();
            Long profileCreationTime = subsInfoPersistDto.getProfileCreationTime();
            Long lastProfileUpdateTime = subsInfoPersistDto.getLastProfileUpdateTime();
            String networkName = subsProfileDTO.getNetworkName();
            //multilingual revert changes
            if(networkName != null){
            	networkName = new String(networkName.getBytes("UTF-8"),"8859_1");
            }
            Boolean corpContactPairingInd = subsProfileDTO.getPairingInd();
            int pairingInd = -1;
            if (corpContactPairingInd != null) {
                if (corpContactPairingInd) {
                    pairingInd = 1;
                } else {
                    pairingInd = 0;
                }
            }
            int payType = subsProfileDTO.getPayType();
            int serviceAuthStatus = subsProfileDTO.getServiceAuthStatus();
            String affiliateId = subsProfileDTO.getAffiliateId();
            String imei = subsProfileDTO.getIMEI();

            int publicSubscriptionType = subsProfileDTO.getPublicSubscriptionType();
            int corpSubscriptionType = subsProfileDTO.getCorporateSubscriptionType();
            int corpId = -1;
            if (corpSubscriptionType == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
                corpId = subsProfileDTO.getCorpId();
            }
            String email = subsProfileDTO.getEmailAddress();

            Integer client_Type = subsProfileDTO.getSubsClientType();
            Integer dispGrpMem = subsProfileDTO.getDispatchGroupMember();
            String accountId = subsProfileDTO.getAccountId();
            Integer pamAccId = subsProfileDTO.getPamAccId();

            ArrayList<String> queryFields = new ArrayList<String>();
            queryFields.add(MDN);
            queryFields.add(POC_HOME);
            queryFields.add(PRESENCE_HOME);
            queryFields.add(XDMS_HOME);
            queryFields.add(SUBSCR_CREATION_TIME);
            queryFields.add(LAST_PROFILE_UPDATE_TIME);
            queryFields.add(SUBSCR_NAME);
            queryFields.add(SERVICE_AUTH_STATUS);
            queryFields.add(PUBLIC_SUBSCRIPTION_TYPE);
            queryFields.add(CORP_SUBSCRIPTION_TYPE);
            if (corpContactPairingInd != null) {
                queryFields.add(CORP_CONTACT_PAIRING_IND);
            }

            if (payType != -1) {
                queryFields.add(PAY_TYPE);
            }
            if (affiliateId != null) {
                queryFields.add(AFFILIATE_ID);
            }
            if (corpSubscriptionType == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
                queryFields.add(CORP_ID);
            }
            if (imei != null) {
                queryFields.add(IMEI);
            }
            // 7.2 parameters
            if (email != null) {
                queryFields.add(EMAIL);
            }
            queryFields.add(CLIENT_TYPE);
            queryFields.add(DISPATCH_GRP_MEMBER);
            queryFields.add(ACCOUNT_ID);
            queryFields.add(PAMACC_ID);
            queryFields.add(ADDLINFO);
            queryFields.add(DISPATCH_TYPE);
			queryFields.add(LICENSE_TYPE);
			queryFields.add(QPPPACKID);
            queryFields.add(SERVICE_STATUS_OP);
            queryFields.add(SERVICE_STATUS_AUTHUSER);
            queryFields.add(SEGMENT_INDICATOR);
            queryFields.add(SUBS_FS1);
            queryFields.add(CLIENT_FS1);
            queryFields.add(ACTIVE_FS1);
            queryFields.add(OPS_FS1);
            queryFields.add(CORPADMIN_FS1);
            queryFields.add(XDMS_FS1);

            queryFields.add(SUBS_FS2);
            queryFields.add(CLIENT_FS2);
            queryFields.add(ACTIVE_FS2);
            queryFields.add(OPS_FS2);
            queryFields.add(CORPADMIN_FS2);
            queryFields.add(XDMS_FS2);
            queryFields.add(USERPROFILEFS2);
            queryFields.add(MC_ID);
            queryFields.add(MC_PTTID);
            queryFields.add(MC_VIDEOID);
            queryFields.add(MC_DATAID);
            queryFields.add(ISDEFAULTPROFILE);
            queryFields.add(USERPROFILEINDEX);
            queryFields.add(FEATURE_REL_VERSION);


            query = KnDbUtil.getInsertQuery(TABLENAME, queryFields);
            knLogger.debug(methodName, " Data Store before - ", KnDBConst.DataStores.XDM_SHARED_DATA, subsInfoPersistDto);
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            knLogger.debug(methodName, " Connection .. ", conn);
            /*  KnDBConst.DataStores.
        conn = persistTxn.getDBConnection(pttServerId, , false);*/
            pStmt = conn.prepareStatement(query);

            for (String mdn : subsInfoPersistDto.getMdns()) {
            	String defaultMCSID=TELURI+mdn;
                int columnIndex = 0; // will dynamically update the column index value as per the received values.
                pStmt.setString(++columnIndex, mdn);
                pStmt.setString(++columnIndex, pocHome);
                pStmt.setString(++columnIndex, presenceHome);
                pStmt.setString(++columnIndex, xdmsHome);
                pStmt.setLong(++columnIndex, profileCreationTime);
                pStmt.setLong(++columnIndex, lastProfileUpdateTime);
                //default networkName will be always mdn in case of bulk subscriber creation
                if(networkName != null){
                    pStmt.setString(++columnIndex, networkName);
                }
                else {
                    pStmt.setString(++columnIndex, mdn);
                }
                pStmt.setInt(++columnIndex, serviceAuthStatus);
                pStmt.setInt(++columnIndex, publicSubscriptionType);
                pStmt.setInt(++columnIndex, corpSubscriptionType);
                if (corpContactPairingInd != null) {
                    pStmt.setInt(++columnIndex, pairingInd);
                }
                if (payType != -1) {
                    pStmt.setInt(++columnIndex, payType);
                }
                if (affiliateId != null) {
                    pStmt.setString(++columnIndex, affiliateId);
                }
                if (corpSubscriptionType == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
                    pStmt.setInt(++columnIndex, corpId);
                }
                if (imei != null) {
                    pStmt.setString(++columnIndex, imei);
                }
                //7.2 values
                if (email != null) {
                    pStmt.setString(++columnIndex, email);
                }
                pStmt.setInt(++columnIndex, client_Type);
                pStmt.setInt(++columnIndex, dispGrpMem);
                pStmt.setString(++columnIndex, accountId);
                pStmt.setInt(++columnIndex, pamAccId);
                pStmt.setInt(++columnIndex, subsProfileDTO.getHierarchyType().value());
                pStmt.setInt(++columnIndex, subsProfileDTO.getDispatchType());
				pStmt.setInt(++columnIndex, subsProfileDTO.getLicenseType());
				pStmt.setInt(++columnIndex, subsProfileDTO.getQppPkgId());
                pStmt.setInt(++columnIndex, subsProfileDTO.getPoCStatusOP());
                pStmt.setInt(++columnIndex, subsProfileDTO.getPoCStatusAU());
                pStmt.setString(++columnIndex, subsProfileDTO.getFirstNetIndicator());
                pStmt.setLong(++columnIndex, KnGeneralUtil.convertHexStringToLong(subsProfileDTO.getSubsFS2()));
                pStmt.setLong(++columnIndex, KnGeneralUtil.convertHexStringToLong(subsProfileDTO.getClientFS2()));
                pStmt.setLong(++columnIndex, KnGeneralUtil.convertHexStringToLong(subsProfileDTO.getActiveFS2()));
                pStmt.setLong(++columnIndex, KnGeneralUtil.convertHexStringToLong(subsProfileDTO.getOpsFS2()));
                pStmt.setLong(++columnIndex, KnGeneralUtil.convertHexStringToLong(subsProfileDTO.getCorpAdminFS2()));
                pStmt.setLong(++columnIndex, KnGeneralUtil.convertHexStringToLong(subsProfileDTO.getXdmsFS2()));
                pStmt.setString(++columnIndex, KnGeneralUtil.getFeatureSet(subsProfileDTO.getSubsFS2()));
                pStmt.setString(++columnIndex, KnGeneralUtil.getFeatureSet(subsProfileDTO.getClientFS2()));
                pStmt.setString(++columnIndex, KnGeneralUtil.getFeatureSet(subsProfileDTO.getActiveFS2()));
                pStmt.setString(++columnIndex, KnGeneralUtil.getFeatureSet(subsProfileDTO.getOpsFS2()));
                pStmt.setString(++columnIndex, KnGeneralUtil.getFeatureSet(subsProfileDTO.getCorpAdminFS2()));
                pStmt.setString(++columnIndex, KnGeneralUtil.getFeatureSet(subsProfileDTO.getXdmsFS2()));
                pStmt.setString(++columnIndex, KnGeneralUtil.getFeatureSet(subsProfileDTO.getUserProfileFS2()));
                pStmt.setBytes(++columnIndex, defaultMCSID.getBytes(StandardCharsets.UTF_8));
                pStmt.setBytes(++columnIndex, defaultMCSID.getBytes(StandardCharsets.UTF_8));
                pStmt.setBytes(++columnIndex, defaultMCSID.getBytes(StandardCharsets.UTF_8));
                pStmt.setBytes(++columnIndex, defaultMCSID.getBytes(StandardCharsets.UTF_8));
                pStmt.setInt(++columnIndex, subsProfileDTO.getIsDefaultProfile());
                pStmt.setInt(++columnIndex, subsProfileDTO.getUserProfileIndex());
                pStmt.setString(++columnIndex, subsProfileDTO.getFeatureRelVersion());

                pStmt.addBatch();
            }
            knLogger.debug(methodName, "Query: Executing -", query, ", persist DTO - ", subsInfoPersistDto);
            int[] count = pStmt.executeBatch();
            knLogger.debug(methodName, "Query: Executed  mdns count:", count);

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving transaction");
                persistTxn.save();
            }

        } catch (KnDAOException dbConne) {
            knLogger.error(methodName, "DAO Exception occurred");
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw dbConne;
        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQL Exception occurred");
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(sqlE, "Failed to create Subscriber Profile - " + sqlE.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            knLogger.error(methodName, e);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to create Subscriber Profile - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeStatement(pStmt);
            knLogger.debug(methodName, "EXIT : create Subscriber Profile");
        }

    }

    /**
     * method to update the Subscriber Profile.
     *
     * @param persistenceDTO IPersistenceDTO
     * @param persisterTxn   KnPersisterTxn
     * @throws KnDAOException DB layer Exception
     */
    public void update(IPersistenceDTO persistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "update(IPersistenceDTO, KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        boolean ownedTxn = false;
        knLogger.debug(methodName, "ENTRY: Update Subscriber Profile with DTO - ", persistenceDTO);

        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }
            KnSubsProfilePersistDTO subsProfilePersistDTO = (KnSubsProfilePersistDTO) persistenceDTO;
            String mdn = subsProfilePersistDTO.getMdn();
            String imei = subsProfilePersistDTO.getIMEI();
            String networkName = subsProfilePersistDTO.getNetworkName();
            String affiliateId = subsProfilePersistDTO.getAffiliateId();
            int payType = subsProfilePersistDTO.getPayType();
            int corpSubscriptionType = subsProfilePersistDTO.getCorporateSubscriptionType();
            int publicSubscriptionType = subsProfilePersistDTO.getPublicSubscriptionType();
            long lastProfileUpdateTime = subsProfilePersistDTO.getLastProfileUpdateTime();
            int corpId = subsProfilePersistDTO.getCorpId();
            String userAgent = subsProfilePersistDTO.getUserAgent();
            String email = subsProfilePersistDTO.getEmailAddress();
            int subsClientType = subsProfilePersistDTO.getSubsClientType();
            int dispatchGrpMember = subsProfilePersistDTO.getDispatchGroupMember();
            Boolean corpContactPairingInd = subsProfilePersistDTO.getPairingInd();
            String clientPassword = subsProfilePersistDTO.getClientPassword();
            int pvMajorVersion = subsProfilePersistDTO.getClientPVmajorVer();
            int pvMinorVersion = subsProfilePersistDTO.getClientPVminorVer();
            String accountId = subsProfilePersistDTO.getAccountId();
            int vocoderId = subsProfilePersistDTO.getVocoderId();
            String derivedKey = subsProfilePersistDTO.getDerivedKey();
			String ufmi = subsProfilePersistDTO.getUfmi();
			String idenUserName = subsProfilePersistDTO.getiDenUserName();
			String idenPassword = subsProfilePersistDTO.getiDenPassword();
			String idenBusUnitId = subsProfilePersistDTO.getiDenBusUnitId();
			String firstNetIndicator = subsProfilePersistDTO.getFirstNetIndicator();
			int licenseType = subsProfilePersistDTO.getLicenseType();
			String userId = subsProfilePersistDTO.getUserId();
			String mcId = subsProfilePersistDTO.getMcId();
            String extGatewayId = subsProfilePersistDTO.getExtGatewayId();
			String mcpttId = subsProfilePersistDTO.getMcpttId();
			String mcVideoId = subsProfilePersistDTO.getMcVideoId();
			String mcDataId = subsProfilePersistDTO.getMcDataId();
            Integer cameraType = subsProfilePersistDTO.getCameraType();
            StringBuilder queryBuffer = new StringBuilder();
            queryBuffer.append(UPDATE_QRY);
            if (imei != null) {
                queryBuffer.append(IMEI).append("=?, ");
            }
            if (networkName != null) {
                queryBuffer.append(SUBSCR_NAME).append("=?, ");
            }
            if (affiliateId != null) {
                queryBuffer.append(AFFILIATE_ID).append("=?, ");
            }
            if (ufmi != null) {
                queryBuffer.append(UFMI).append("=?, ");
            }
            if (idenUserName != null) {
                queryBuffer.append(IDEN_USERNAME).append("=?, ");
            }
            if (idenPassword != null) {
                queryBuffer.append(IDEN_PASSWORD).append("=?, ");
            }
            if (idenBusUnitId != null) {
                queryBuffer.append(IDEN_BUSUNITID).append("=?, ");
            }
            if (payType != -1) {
                queryBuffer.append(PAY_TYPE).append("=?, ");
            }
            queryBuffer.append(CORP_SUBSCRIPTION_TYPE).append("=?, ");
            queryBuffer.append(PUBLIC_SUBSCRIPTION_TYPE).append("=?, ");
            queryBuffer.append(LAST_PROFILE_UPDATE_TIME).append("=?, ");
            queryBuffer.append(CORP_ID).append("=?, ");
            if (userAgent != null) {
                queryBuffer.append(USER_AGENT).append("=?, ");
            }

            if (email != null) {
                queryBuffer.append(EMAIL).append("=?, ");
            }
            queryBuffer.append(CLIENT_TYPE).append("=?, ");
            queryBuffer.append(DISPATCH_GRP_MEMBER).append("=?, ");
            queryBuffer.append(CORP_CONTACT_PAIRING_IND).append("=?, ");
            queryBuffer.append(CLIENT_PASSWORD).append("=?, ");
            if (pvMajorVersion != -1) {
                queryBuffer.append(CLIENTPV_MAJORVERSION).append("=?, ");
            }
            if (pvMinorVersion != -1) {
                queryBuffer.append(CLIENTPV_MINORVERSION).append("=?, ");
            }
            if (accountId != null) {
                queryBuffer.append(ACCOUNT_ID).append("=?, ");
            }
            if (vocoderId != 0) {
                queryBuffer.append(VOCODERID).append("=?, ");
            }
            queryBuffer.append(QPPPACKID).append("=?, ");
            if (derivedKey != null) {
                queryBuffer.append(DERIVEDKEY).append("=?, ");
            }

            if (firstNetIndicator != null) {
            queryBuffer.append(SEGMENT_INDICATOR).append("=?, ");
            }
            queryBuffer.append(SUBS_FS1).append("=?, ");
            queryBuffer.append(CLIENT_FS1).append("=?, ");
            queryBuffer.append(ACTIVE_FS1).append("=?, ");
            queryBuffer.append(OPS_FS1).append("=?, ");
            queryBuffer.append(CORPADMIN_FS1).append("=?, ");
            queryBuffer.append(XDMS_FS1).append("=?, ");

            queryBuffer.append(SUBS_FS2).append("=?, ");
            queryBuffer.append(CLIENT_FS2).append("=?, ");
            queryBuffer.append(ACTIVE_FS2).append("=?, ");
            queryBuffer.append(OPS_FS2).append("=?, ");
            queryBuffer.append(CORPADMIN_FS2).append("=?, ");
            queryBuffer.append(XDMS_FS2).append("=?");
            if (licenseType != 0) {
                queryBuffer.append(",").append(LICENSE_TYPE).append("=? ");
            }
            if (userId!=null)
			{
            queryBuffer.append(",").append(USER_ID).append("=? ");
			}

			if (null != mcId) {
				queryBuffer.append(",").append(MC_ID).append("=? ");

			}
			if (null != mcpttId) {
				queryBuffer.append(",").append(MC_PTTID).append("=? ");

			}

			if (null != mcVideoId) {
				queryBuffer.append(",").append(MC_VIDEOID).append("=? ");

			}
			if (null != mcDataId) {
				queryBuffer.append(",").append(MC_DATAID).append("=? ");

			}

            if (null != cameraType) {
                queryBuffer.append(",").append(CAMERA_TYPE).append("=? ");

            }
            if(null != extGatewayId) {
                queryBuffer.append(",").append(EXT_GATEWAY_ID).append("=? ");
            }
            queryBuffer.append(" WHERE ").append(MDN).append("=?");

            query = queryBuffer.toString();

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);

            pStmt = conn.prepareStatement(query);
            int columnIndex = 0;
            if (imei != null) {
                pStmt.setString(++columnIndex, imei);
            }
            if (networkName != null) {
            	//multilingual revert change
            	networkName = new String(subsProfilePersistDTO.getNetworkName().getBytes("UTF-8"),"8859_1");
                pStmt.setString(++columnIndex, networkName);
            }
            if (affiliateId != null) {
                pStmt.setString(++columnIndex, affiliateId);
            }
            if (ufmi != null) {
                if(ufmi.isEmpty()){
                    pStmt.setNull(++columnIndex, Types.VARCHAR);
                }else {
                pStmt.setString(++columnIndex, ufmi);
            }
            }
            if (idenUserName != null) {
                if(idenUserName.isEmpty()){
                pStmt.setNull(++columnIndex, Types.VARCHAR);
                }else {
                pStmt.setString(++columnIndex, idenUserName);
            }
            }

            if (idenPassword != null) {
                if(idenPassword.isEmpty()){
                    pStmt.setNull(++columnIndex, Types.VARCHAR);
                }else {
                pStmt.setString(++columnIndex, idenPassword);
            }
            }

            if (idenBusUnitId != null) {
                if(idenBusUnitId.isEmpty()){
                    pStmt.setNull(++columnIndex, Types.VARCHAR);
                }else {
                pStmt.setString(++columnIndex, idenBusUnitId);
            }
            }
            if (payType != -1) {
                pStmt.setInt(++columnIndex, payType);
            }
            pStmt.setInt(++columnIndex, corpSubscriptionType);
            pStmt.setInt(++columnIndex, publicSubscriptionType);
            pStmt.setLong(++columnIndex, lastProfileUpdateTime);
            if (corpId == 0) {
                pStmt.setNull(++columnIndex, java.sql.Types.INTEGER);
            } else {
                pStmt.setInt(++columnIndex, corpId);
            }

            if (userAgent != null) {
                pStmt.setString(++columnIndex, userAgent);
            }

            if (email != null) {
                pStmt.setString(++columnIndex, email);
            }
            pStmt.setInt(++columnIndex, subsClientType);
            pStmt.setInt(++columnIndex, dispatchGrpMember);
            if (corpContactPairingInd != null) {
                pStmt.setInt(++columnIndex, corpContactPairingInd ? 1 : 0);
            } else {
                pStmt.setNull(++columnIndex, java.sql.Types.TINYINT);
            }
            if (clientPassword == null) {
                pStmt.setNull(++columnIndex, java.sql.Types.CHAR);
            } else {
                pStmt.setString(++columnIndex, clientPassword);
            }
            if (pvMajorVersion != -1) {
                pStmt.setInt(++columnIndex, pvMajorVersion);
            }
            if (pvMinorVersion != -1) {
                pStmt.setInt(++columnIndex, pvMinorVersion);
            }

            if (accountId != null) {
                pStmt.setString(++columnIndex, accountId);
            }

            if (vocoderId != 0) {
                pStmt.setInt(++columnIndex, vocoderId);
            }
            pStmt.setInt(++columnIndex, subsProfilePersistDTO.getQppPkgId());
            if (derivedKey != null) {
                pStmt.setString(++columnIndex,derivedKey);
            }

            if (firstNetIndicator != null) {
            pStmt.setString(++columnIndex, firstNetIndicator);
            }
            pStmt.setLong(++columnIndex, KnGeneralUtil.convertHexStringToLong(subsProfilePersistDTO.getSubsFS2()));
            pStmt.setLong(++columnIndex, KnGeneralUtil.convertHexStringToLong(subsProfilePersistDTO.getClientFS2()));
            pStmt.setLong(++columnIndex, KnGeneralUtil.convertHexStringToLong(subsProfilePersistDTO.getActiveFS2()));
            pStmt.setLong(++columnIndex, KnGeneralUtil.convertHexStringToLong(subsProfilePersistDTO.getOpsFS2()));
            pStmt.setLong(++columnIndex, KnGeneralUtil.convertHexStringToLong(subsProfilePersistDTO.getCorpAdminFS2()));
            pStmt.setLong(++columnIndex, KnGeneralUtil.convertHexStringToLong(subsProfilePersistDTO.getXdmsFS2()));

            pStmt.setString(++columnIndex, KnGeneralUtil.getFeatureSet(subsProfilePersistDTO.getSubsFS2()));
            pStmt.setString(++columnIndex, KnGeneralUtil.getFeatureSet(subsProfilePersistDTO.getClientFS2()));
            pStmt.setString(++columnIndex, KnGeneralUtil.getFeatureSet(subsProfilePersistDTO.getActiveFS2()));
            pStmt.setString(++columnIndex, KnGeneralUtil.getFeatureSet(subsProfilePersistDTO.getOpsFS2()));
            pStmt.setString(++columnIndex, KnGeneralUtil.getFeatureSet(subsProfilePersistDTO.getCorpAdminFS2()));
            pStmt.setString(++columnIndex, KnGeneralUtil.getFeatureSet(subsProfilePersistDTO.getXdmsFS2()));

            if (licenseType != 0) {
                pStmt.setInt(++columnIndex, licenseType);
            }
            if (userId != null) {
				if (userId.isEmpty()) {
					pStmt.setNull(++columnIndex, Types.NVARCHAR);
				} else {
					pStmt.setString(++columnIndex, userId);
				}
			}
            if(mcId!=null)
            {
            pStmt.setBytes(++columnIndex, mcId.getBytes(StandardCharsets.UTF_8));
            }
            if(mcpttId!=null)
            {
            pStmt.setBytes(++columnIndex, mcpttId.getBytes(StandardCharsets.UTF_8));
            }
            if(mcVideoId!=null)
            {
            pStmt.setBytes(++columnIndex, mcVideoId.getBytes(StandardCharsets.UTF_8));
            }
            if(mcDataId!=null)
            {
            pStmt.setBytes(++columnIndex, mcDataId.getBytes(StandardCharsets.UTF_8));
            }
            if(cameraType!=null)
            {
                pStmt.setInt(++columnIndex, cameraType);
            }
            if (extGatewayId != null) {
                if (extGatewayId.isEmpty()) {
                    pStmt.setNull(++columnIndex, Types.CHAR);
                } else {
                    pStmt.setString(++columnIndex, extGatewayId);
                }
            }
            pStmt.setString(++columnIndex, mdn);

            knLogger.debug(methodName, "QUERY: Executing the Query - ", query, " DTO - ", persistenceDTO);

            pStmt.executeUpdate();
            knLogger.debug(methodName, "Query: Executed ");

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }

        } catch (KnDAOException dbConne) {
            knLogger.error(methodName, "DAO Exception occurred");
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw dbConne;
        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQL Exception occurred");
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(sqlE, "Failed to update Subscriber Profile - " + sqlE.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to update Subscriber Profile - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeStatement(pStmt);
            knLogger.debug(methodName, "EXIT: update Subscriber Profile");
        }
    }


    /**
     * method to update the Subscriber Profile.
     *
     * @param subsProfilePersistDTO IPersistenceDTO
     * @param persisterTxn          KnPersisterTxn
     * @throws KnDAOException DB layer Exception
     */
    public void update(KnBulkSubsProfilePersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "update(KnBulkSubsProfilePersistDTO, KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        boolean ownedTxn = false;
        knLogger.debug(methodName, "ENTRY: Update Subscriber Profile with DTO - ", subsProfilePersistDTO);

        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            KnSubsProfileDTO subsProfileDTO = subsProfilePersistDTO.getSubsProfile();
            String imei = subsProfileDTO.getIMEI();
            String networkName = subsProfileDTO.getNetworkName();
            String affiliateId = subsProfileDTO.getAffiliateId();
            int payType = subsProfileDTO.getPayType();
            int corpSubscriptionType = subsProfileDTO.getCorporateSubscriptionType();
            int publicSubscriptionType = subsProfileDTO.getPublicSubscriptionType();
            long lastProfileUpdateTime = subsProfilePersistDTO.getLastProfileUpdateTime();
            int corpId = subsProfileDTO.getCorpId();
            String userAgent = subsProfileDTO.getUserAgent();
            String email = subsProfileDTO.getEmailAddress();
            int subsClientType = subsProfileDTO.getSubsClientType();
            int dispatchGrpMember = subsProfileDTO.getDispatchGroupMember();
            Boolean corpContactPairingInd = subsProfileDTO.getPairingInd();
            String clientPassword = subsProfileDTO.getClientPassword();
            int pvMajorVersion = subsProfileDTO.getClientPVmajorVer();
            int pvMinorVersion = subsProfileDTO.getClientPVminorVer();
            String accountId = subsProfileDTO.getAccountId();
            String firstNetIndicator = subsProfileDTO.getFirstNetIndicator();

            StringBuilder queryBuffer = new StringBuilder();
            queryBuffer.append(UPDATE_QRY);
            if (imei != null) {
                queryBuffer.append(IMEI).append("=?, ");
            }
            if (networkName != null) {
                //multilingual revert  change
            	networkName = new String(networkName.getBytes("UTF-8"),"8859_1");
                queryBuffer.append(SUBSCR_NAME).append("=?, ");
            }
            if (affiliateId != null) {
                queryBuffer.append(AFFILIATE_ID).append("=?, ");
            }
            if (payType != -1) {
                queryBuffer.append(PAY_TYPE).append("=?, ");
            }
            queryBuffer.append(CORP_SUBSCRIPTION_TYPE).append("=?, ");
            queryBuffer.append(PUBLIC_SUBSCRIPTION_TYPE).append("=?, ");
            queryBuffer.append(LAST_PROFILE_UPDATE_TIME).append("=?, ");
            queryBuffer.append(CORP_ID).append("=?, ");
            if (userAgent != null) {
                queryBuffer.append(USER_AGENT).append("=?, ");
            }

            if (email != null) {
                queryBuffer.append(EMAIL).append("=?, ");
            }
            queryBuffer.append(CLIENT_TYPE).append("=?, ");
            queryBuffer.append(DISPATCH_GRP_MEMBER).append("=?, ");
            queryBuffer.append(CORP_CONTACT_PAIRING_IND).append("=?, ");
            queryBuffer.append(CLIENT_PASSWORD).append("=?, ");
            if (pvMajorVersion != -1) {
                queryBuffer.append(CLIENTPV_MAJORVERSION).append("=?, ");
            }
            if (pvMinorVersion != -1) {
                queryBuffer.append(CLIENTPV_MINORVERSION).append("=?, ");
            }
            if (accountId != null) {
                queryBuffer.append(ACCOUNT_ID).append("=?, ");
            }

            queryBuffer.append(QPPPACKID).append("=?, ");

            if (firstNetIndicator != null) {
            queryBuffer.append(SEGMENT_INDICATOR).append("=?, ");
            }

            queryBuffer.append(SUBS_FS1).append("=?, ");
            queryBuffer.append(CLIENT_FS1).append("=?, ");
            queryBuffer.append(ACTIVE_FS1).append("=?, ");
            queryBuffer.append(OPS_FS1).append("=?, ");
            queryBuffer.append(CORPADMIN_FS1).append("=?, ");
            queryBuffer.append(XDMS_FS1).append("=?, ");
            queryBuffer.append(SUBS_FS2).append("=?, ");
            queryBuffer.append(CLIENT_FS2).append("=?, ");
            queryBuffer.append(ACTIVE_FS2).append("=?, ");
            queryBuffer.append(OPS_FS2).append("=?, ");
            queryBuffer.append(CORPADMIN_FS2).append("=?, ");
            queryBuffer.append(XDMS_FS2).append("=? ");
            queryBuffer.append(" WHERE ").append(MDN).append("=?");

            query = queryBuffer.toString();

            //conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);

            pStmt = conn.prepareStatement(query);
            for (String mdn : subsProfilePersistDTO.getMdns()) {
                int columnIndex = 0;
                if (imei != null) {
                    pStmt.setString(++columnIndex, imei);
                }
                if (networkName != null) {
                    pStmt.setString(++columnIndex, networkName);
                }
                if (affiliateId != null) {
                    pStmt.setString(++columnIndex, affiliateId);
                }
                if (payType != -1) {
                    pStmt.setInt(++columnIndex, payType);
                }
                pStmt.setInt(++columnIndex, corpSubscriptionType);
                pStmt.setInt(++columnIndex, publicSubscriptionType);
                pStmt.setLong(++columnIndex, lastProfileUpdateTime);
                if (corpId == 0) {
                    pStmt.setNull(++columnIndex, java.sql.Types.INTEGER);
                } else {
                    pStmt.setInt(++columnIndex, corpId);
                }

                if (userAgent != null) {
                    pStmt.setString(++columnIndex, userAgent);
                }

                if (email != null) {
                    pStmt.setString(++columnIndex, email);
                }
                pStmt.setInt(++columnIndex, subsClientType);
                pStmt.setInt(++columnIndex, dispatchGrpMember);
                if (corpContactPairingInd != null) {
                    pStmt.setInt(++columnIndex, corpContactPairingInd ? 1 : 0);
                } else {
                    pStmt.setNull(++columnIndex, java.sql.Types.TINYINT);
                }
                if (clientPassword == null) {
                    pStmt.setNull(++columnIndex, java.sql.Types.CHAR);
                } else {
                    pStmt.setString(++columnIndex, clientPassword);
                }
                if (pvMajorVersion != -1) {
                    pStmt.setInt(++columnIndex, pvMajorVersion);
                }
                if (pvMinorVersion != -1) {
                    pStmt.setInt(++columnIndex, pvMinorVersion);
                }
                if (accountId != null) {
                    pStmt.setString(++columnIndex, accountId);
                }
                pStmt.setInt(++columnIndex, subsProfileDTO.getQppPkgId());
                if (firstNetIndicator != null) {
                pStmt.setString(++columnIndex, firstNetIndicator);
                }
                pStmt.setLong(++columnIndex, KnGeneralUtil.convertHexStringToLong(subsProfileDTO.getSubsFS2()));
                pStmt.setLong(++columnIndex, KnGeneralUtil.convertHexStringToLong(subsProfileDTO.getClientFS2()));
                pStmt.setLong(++columnIndex, KnGeneralUtil.convertHexStringToLong(subsProfileDTO.getActiveFS2()));
                pStmt.setLong(++columnIndex, KnGeneralUtil.convertHexStringToLong(subsProfileDTO.getOpsFS2()));
                pStmt.setLong(++columnIndex, KnGeneralUtil.convertHexStringToLong(subsProfileDTO.getCorpAdminFS2()));
                pStmt.setLong(++columnIndex, KnGeneralUtil.convertHexStringToLong(subsProfileDTO.getXdmsFS2()));
                pStmt.setString(++columnIndex, KnGeneralUtil.getFeatureSet(subsProfileDTO.getSubsFS2()));
                pStmt.setString(++columnIndex, KnGeneralUtil.getFeatureSet(subsProfileDTO.getClientFS2()));
                pStmt.setString(++columnIndex, KnGeneralUtil.getFeatureSet(subsProfileDTO.getActiveFS2()));
                pStmt.setString(++columnIndex, KnGeneralUtil.getFeatureSet(subsProfileDTO.getOpsFS2()));
                pStmt.setString(++columnIndex, KnGeneralUtil.getFeatureSet(subsProfileDTO.getCorpAdminFS2()));
                pStmt.setString(++columnIndex, KnGeneralUtil.getFeatureSet(subsProfileDTO.getXdmsFS2()));
                pStmt.setString(++columnIndex, mdn);
            }

            knLogger.debug(methodName, "QUERY: Executing the Query - ", query, " DTO - ", subsProfilePersistDTO);

            pStmt.executeBatch();
            knLogger.debug(methodName, "Query: Executed ");

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }

        } catch (KnDAOException dbConne) {
            knLogger.error(methodName, "DAO Exception occurred");
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw dbConne;
        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQL Exception occurred");
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(sqlE, "Failed to update Subscriber Profile - " + sqlE.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to update Subscriber Profile - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeStatement(pStmt);
            knLogger.debug(methodName, "EXIT: update Subscriber Profile");
        }
    }

    /**
     * method to update the Subscriber VocoderId.
     *
     * @param mdn
     * @param vocoderId
     * @param persisterTxn          KnPersisterTxn
     * @throws KnDAOException DB layer Exception
     */
    public void updateVocoderID(String mdn, Integer vocoderId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateVocoderID(MDN, VOCODERID, boolean, KnPersisterTxn)";
        String query = null;
        Connection conn = null;
        PreparedStatement pStmt = null;
        knLogger.info(methodName, "ENTRY: Update Subscriber Profile for mdn - ", KnGDPRTemplate.mdn(mdn), " VOCODERID : ", vocoderId);

        try {

            query = UPDATE_QRY + VOCODERID +" = "+vocoderId +" WHERE "+ MDN +" = '"+mdn+"'";

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);

            pStmt = conn.prepareStatement(query);

            knLogger.debug(methodName, "QUERY: Executing the Query - ", query);

            pStmt.executeUpdate();
            knLogger.debug(methodName, "Query: Executed ");


        }catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to update Subscriber Profile - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeStatement(pStmt);
            knLogger.debug(methodName, "EXIT: update Subscriber Profile");
        }
    }


        /**
         * method to delete the subscriber profile
         *
         * @param persistenceDTO IPersistenceDTO
         * @param persistTxn     KnPersisterTxn
         * @throws KnDAOException DB Layer exception
         */
    public void delete(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "delete(IPersistenceDTO, KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        boolean ownedTxn = false;

        knLogger.debug(methodName, "ENTRY: Delete Subscriber Profile");
        try {
            if (persistTxn == null) {
                persistTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening Transaction");
                persistTxn.open();
                ownedTxn = true;
            }

            KnSubsProfilePersistDTO subsInfoPersistDTO = (KnSubsProfilePersistDTO) persistenceDTO;
            String mdn = subsInfoPersistDTO.getMdn();

            query = DELETE_QRY;
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            //conn = persistTxn.getDBConnection(pttServerId, false);
            pStmt = conn.prepareStatement(query);
            pStmt.setString(1, mdn);

            knLogger.debug(methodName, "Query: Executing - ", query, ", mdn - ", KnGDPRTemplate.mdn(mdn));
            pStmt.executeUpdate();
            knLogger.debug(methodName, "Query: Executed ");

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the Transaction");
                persistTxn.save();
            }

        } catch (KnDAOException dbConne) {
            knLogger.error(methodName, "DAO Exception occurred");
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw dbConne;
        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQL Exception occurred");
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(sqlE, "Failed to delete Subscriber Profile - " + sqlE.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to delete Subscriber Profile - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeStatement(pStmt);
            knLogger.debug(methodName, "EXIT : delete Subscriber Profile");
        }
    }

    /**
     * method to delete the subscriber profile
     *
     * @param persistTxn KnPersisterTxn
     * @throws KnDAOException DB Layer exception
     */
    public void delete(List<String> mdns, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "delete(List<String>, KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        boolean ownedTxn = false;

        knLogger.debug(methodName, "ENTRY: Delete Subscriber Profile");
        try {
            if (persistTxn == null) {
                persistTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening Transaction");
                persistTxn.open();
                ownedTxn = true;
            }


            query = DELETE_QRY;
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            //conn = persistTxn.getDBConnection(pttServerId, false);
            pStmt = conn.prepareStatement(query);
            for (String mdn : mdns) {
                pStmt.setString(1, mdn);
                pStmt.addBatch();
            }
            knLogger.debug(methodName, "Query: Executing - ", query);
            int[] count = pStmt.executeBatch();
            knLogger.debug(methodName, "Query: Executed mdns count:", count);

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the Transaction");
                persistTxn.save();
            }

        } catch (KnDAOException dbConne) {
            knLogger.error(methodName, "DAO Exception occurred");
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw dbConne;
        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQL Exception occurred");
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(sqlE, "Failed to delete Subscriber Profile - " + sqlE.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to delete Subscriber Profile - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeStatement(pStmt);
            knLogger.debug(methodName, "EXIT : delete Subscriber Profile");
        }
    }

    public Collection select(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "select(IPersistenceDTO, KnPersisterTxn)";
        knLogger.debug(methodName, "Not Implemented");
        return null;
    }

    /**
     * retrieveing subscriber profile for Dispatcher changes
     *
     * @param mdns
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public List<KnOPSubsDispatcherDTO> retrieveBulkSubscribersInfo(List<String> mdns, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "retrieveBulkSubscribersInfo(List,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: Retrive BulkSubscriber Profile for Dispatch column Change ");
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        String query = null;
        ArrayList<KnOPSubsDispatcherDTO> listOfDTOs = new ArrayList<>();
        try {

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = GET_DISPATCH_QRY.replaceAll(MDN_LIST, KnGeneralUtil.getComSepList(mdns));
            stmt = conn.createStatement();
            knLogger.debug(methodName, "Executing query - ", query);
            rs = stmt.executeQuery(query);
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                KnOPSubsDispatcherDTO opSubsDispatcherDTO = new KnOPSubsDispatcherDTO();
                opSubsDispatcherDTO.setMdn(rs.getString(MDN).trim());
                opSubsDispatcherDTO.setPoCHome(rs.getString(POC_HOME));
                opSubsDispatcherDTO.setPresenceHome(rs.getString(PRESENCE_HOME));
                opSubsDispatcherDTO.setXDMSHome(rs.getString(XDMS_HOME));
                opSubsDispatcherDTO.setCorpId(rs.getInt(CORP_ID));
                opSubsDispatcherDTO.setCorporateSubscriptionType(rs.getInt(CORP_SUBSCRIPTION_TYPE));
                opSubsDispatcherDTO.setDispatchGroupMember(rs.getInt(DISPATCH_GRP_MEMBER));
                String subsFs2=rs.getString(SUBS_FS2)!=null?rs.getString(SUBS_FS2):KnGeneralUtil.convertLongToHexString(rs.getLong(SUBS_FS1));
                opSubsDispatcherDTO.setSubsFS2(subsFs2);
                String clientsFs2=rs.getString(CLIENT_FS2)!=null?rs.getString(CLIENT_FS2):KnGeneralUtil.convertLongToHexString(rs.getLong(CLIENT_FS1));
                opSubsDispatcherDTO.setClientFS2(clientsFs2);
                String activeFs2=rs.getString(ACTIVE_FS2)!=null?rs.getString(ACTIVE_FS2):KnGeneralUtil.convertLongToHexString(rs.getLong(ACTIVE_FS1));
                opSubsDispatcherDTO.setActiveFS2(activeFs2);
                String opsFs2=rs.getString(OPS_FS2)!=null?rs.getString(OPS_FS2):KnGeneralUtil.convertLongToHexString(rs.getLong(OPS_FS1));
                opSubsDispatcherDTO.setOpsFS2(opsFs2);
                String corpAdminFs2=rs.getString(CORPADMIN_FS2)!=null?rs.getString(CORPADMIN_FS2):KnGeneralUtil.convertLongToHexString(rs.getLong(CORPADMIN_FS1));
                opSubsDispatcherDTO.setCorpAdminFS2(corpAdminFs2);
                opSubsDispatcherDTO.setClientPVmajorVer(rs.getInt(CLIENTPV_MAJORVERSION));
                opSubsDispatcherDTO.setClientPVminorVer(rs.getInt(CLIENTPV_MINORVERSION));
                String xdmsFs2=rs.getString(XDMS_FS2)!=null?rs.getString(XDMS_FS2):KnGeneralUtil.convertLongToHexString(rs.getLong(XDMS_FS1));
                opSubsDispatcherDTO.setXdmsFS2(xdmsFs2);
                opSubsDispatcherDTO.setSubsClientType(rs.getInt(CLIENT_TYPE));
                opSubsDispatcherDTO.setQppPkgId(rs.getInt(QPPPACKID));
				opSubsDispatcherDTO.setFirstNetIndicator((rs.getString(SEGMENT_INDICATOR)));
				opSubsDispatcherDTO.setLastProfileUpdateTime(rs.getLong(LASTPROFILEUPDATETIME));
				if(rs.getString(USERPROFILEFS2)!=null)
				{
					opSubsDispatcherDTO.setUserProfileFS2(rs.getString(USERPROFILEFS2));
				}
                listOfDTOs.add(opSubsDispatcherDTO);

            }

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve Subscriber Profile for Dispatcher Changes - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            knLogger.debug(methodName, "EXIT :Failed to retrieve Subscriber Profile for Dispatcher Changes");
        }
        return listOfDTOs;
    }

    /**
     * method to update in DG.POCSUBSCRINFO table for dispatcher changes
     *
     * @param subsDisProfilePersistDTOs
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void updateBulkSubscrProfile(List<KnOPSubsDispatcherDTO> subsDisProfilePersistDTOs, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateBulkSubscrProfile(List,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: Update BulkSubscriber Profile for Dispatch Changes ",subsDisProfilePersistDTOs);
        Connection conn = null;
        PreparedStatement psmt = null;
        String query = null;
        try {
            query = UPDATE_DISPATCH_QRY;
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            psmt = conn.prepareStatement(query);
            for (KnOPSubsDispatcherDTO opSubsDispatcherDTO : subsDisProfilePersistDTOs) {
                psmt.setLong(1, opSubsDispatcherDTO.getLastProfileUpdateTime());
                psmt.setInt(2, opSubsDispatcherDTO.getDispatchGroupMember());
                psmt.setLong(3, KnGeneralUtil.convertHexStringToLong(opSubsDispatcherDTO.getSubsFS2()));
                psmt.setLong(4, KnGeneralUtil.convertHexStringToLong(opSubsDispatcherDTO.getXdmsFS2()));
                psmt.setLong(5, KnGeneralUtil.convertHexStringToLong(opSubsDispatcherDTO.getActiveFS2()));
                psmt.setInt(6, opSubsDispatcherDTO.getQppPkgId());
                psmt.setString(7, opSubsDispatcherDTO.getFirstNetIndicator());
                psmt.setLong(8, opSubsDispatcherDTO.getSubsClientType());
                psmt.setString(9, KnGeneralUtil.getFeatureSet(opSubsDispatcherDTO.getSubsFS2()));
                psmt.setString(10, KnGeneralUtil.getFeatureSet(opSubsDispatcherDTO.getXdmsFS2()));
                psmt.setString(11, KnGeneralUtil.getFeatureSet(opSubsDispatcherDTO.getActiveFS2()));
                psmt.setString(12, opSubsDispatcherDTO.getMdn());
                psmt.addBatch();
            }
            knLogger.debug(methodName, "QUERY: Executing the Query - ", query);
            psmt.executeBatch();
            knLogger.debug(methodName, "Query: Executed ");

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to updateBulkSubscriber Profile For Dispatcher- " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeStatement(psmt);
        }
    }

    /**
     * method to retrieve the Subscriber Profile
     *
     * @param mdn        String
     * @param persistTxn KnPersisterTxn
     * @return KnOPSubsProfileInfoDTO
     * @throws KnDAOException DB Layer Exception
     */
    public KnOPSubsProfileInfoDTO selectSubscriberProfile(String mdn, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "selectSubscriberProfile(IPersistenceDTO, KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        boolean ownedTxn = false;
        KnOPSubsProfileInfoDTO subsProfileInfoDto = new KnOPSubsProfileInfoDTO();
        knLogger.debug(methodName, "ENTRY: Select Subscriber Profile mdn",KnGDPRTemplate.mdn(mdn),"persistTxn ",persistTxn);
        try {
            if (persistTxn == null) {
                persistTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the transaction ");
                persistTxn.open();
                ownedTxn = true;
            }

            StringBuilder strBuffer = getSelectSubsProfileQuery();
            strBuffer.append(MDN).append("=?");

            query = strBuffer.toString();

            if (ownedTxn) {
                knLogger.debug(methodName,"ownedTxn ### pttServerId - ",pttServerId);
                conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
                knLogger.debug(methodName, "select...>>>SubscriberProfile ... ");
                //conn = persistTxn.getDBConnection(pttServerId, true);
            } else {
                knLogger.debug(methodName,"else ### pttServerId - ",pttServerId);
                conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
                //conn = persistTxn.getDBConnection(pttServerId, false);
                knLogger.debug(methodName, "selectSubscriberProfile ... ");
            }
            pStmt = conn.prepareStatement(query);
            pStmt.setString(1, mdn);

            knLogger.debug(methodName, "Query: Executing - ", query, ", mdn - ", KnGDPRTemplate.mdn(mdn));
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Query: Executed ");

            if (rs.next()) {
            	subsProfileInfoDto = new KnOPSubsProfileInfoDTO();
                getSelectSubsProfileResult(subsProfileInfoDto, rs);
            } else {
                knLogger.error(methodName, "Subscriber Profile doesn't exist");
                if (ownedTxn) {
                    persistTxn.rollback();
                }
                throw new KnDBPersistenceException(KnErrorCodes.DAO.ROW_NOT_FOUND, "Subscriber Profile does not exist",
                        pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
            }

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the Transaction");
                persistTxn.save();
            }
            knLogger.debug(methodName, "returning subscriber Profile Info ", subsProfileInfoDto,
                    "ActiveFs",subsProfileInfoDto.getActiveFS2(),"authstatus",subsProfileInfoDto.getServiceAuthStatus());
            return subsProfileInfoDto;

        } catch (KnDAOException dbConne) {
            knLogger.error(methodName, "DAO Exception occurred");
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw dbConne;
        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQL Exception occurred");
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(sqlE, "Failed to select Subscriber Profile - " + sqlE.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to select Subscriber Profile - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            knLogger.info(methodName, "EXIT : select Subscriber Profile");
        }
    }

    public Map<String, com.kodiak.xdms.server.common.dto.common.KnSubsProfileDTO> selectSubscriberProfile(List<String> mdnList, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "selectSubscriberProfile(IPersistenceDTO, KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        boolean ownedTxn = false;
        Map<String, com.kodiak.xdms.server.common.dto.common.KnSubsProfileDTO> subsProfileInfoDto =null;
        com.kodiak.xdms.server.common.dto.common.KnSubsProfileDTO subsProfileDTOCommon = new com.kodiak.xdms.server.common.dto.common.KnSubsProfileDTO();
        knLogger.debug(methodName, "ENTRY: Select Subscriber Profile mdn",KnGDPRTemplate.mdnList(mdnList),"persistTxn ",persistTxn);
        int index = 1;
        try {
            if (persistTxn == null) {
                persistTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the transaction ");
                persistTxn.open();
                ownedTxn = true;
            }

            StringBuilder strBuffer = getSelectBulkSubsProfileQuery();
            strBuffer.append(MDN).append("IN (?)");

            query = strBuffer.toString();
            query = query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(mdnList,query,"MDNLIST");
            if (ownedTxn) {
                knLogger.debug(methodName,"ownedTxn ### pttServerId - ",pttServerId);
                conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
                knLogger.debug(methodName, "select...>>>SubscriberProfile ... ");
                //conn = persistTxn.getDBConnection(pttServerId, true);
            } else {
                knLogger.debug(methodName,"else ### pttServerId - ",pttServerId);
                conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
                //conn = persistTxn.getDBConnection(pttServerId, false);
                knLogger.debug(methodName, "selectSubscriberProfile ... ");
            }
            pStmt = conn.prepareStatement(query);
            for(String mdn : mdnList){
                pStmt.setString(index++, mdn);
            }

            knLogger.debug(methodName, "Query: Executing - ", query, ", mdn - ", KnGDPRTemplate.mdnList(mdnList));
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Query: Executed ");

            if (rs.next()) {
                subsProfileInfoDto = new HashMap<>();
                getSelectSubsCommonProfileResult(subsProfileDTOCommon, rs);
                subsProfileInfoDto.put(rs.getString(MDN).trim(), subsProfileDTOCommon);
            } else {
                knLogger.error(methodName, "Subscriber Profile doesn't exist");
                if (ownedTxn) {
                    persistTxn.rollback();
                }
                throw new KnDBPersistenceException(KnErrorCodes.DAO.ROW_NOT_FOUND, "Subscriber Profile does not exist",
                        pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
            }

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the Transaction");
                persistTxn.save();
            }

            return subsProfileInfoDto;

        } catch (KnDAOException dbConne) {
            knLogger.error(methodName, "DAO Exception occurred");
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw dbConne;
        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQL Exception occurred");
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(sqlE, "Failed to select Subscriber Profile - " + sqlE.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to select Subscriber Profile - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            knLogger.info(methodName, "EXIT : select Subscriber Profile");
        }
    }


    public KnOPSubsProfileInfoDTO retrieveBaseMdnByMcpttId(String mcpttId, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "retrieveBaseMdnByMcpttId(String, KnPersisterTxn)";
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        KnOPSubsProfileInfoDTO subsProfileInfoDto = new KnOPSubsProfileInfoDTO();
        String query =null;
        knLogger.debug(methodName, "ENTRY: Select Subscriber Profile mcpttId-",KnGDPRTemplate.mcpttId(mcpttId),"persistTxn ",persistTxn);
        try {

            query = new StringBuilder(GET_SUBS_INFO_BY_MCPTTID).toString();

            knLogger.debug(methodName,"else ### pttServerId - ",pttServerId);
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            knLogger.debug(methodName, "selectSubscriberProfile ... ");

            pStmt = conn.prepareStatement(query);
            pStmt.setBytes(1, mcpttId.getBytes(StandardCharsets.UTF_8));

            knLogger.debug(methodName, "Query: Executing - ", query, ", mcpttId - ", KnGDPRTemplate.mcpttId(mcpttId));
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Query: Executed ");

            if (rs.next()) {
                subsProfileInfoDto = new KnOPSubsProfileInfoDTO();
                subsProfileInfoDto.setClientPassword(rs.getString(CLIENT_PASSWORD));
                subsProfileInfoDto.setMdn(rs.getString(MDN));
                subsProfileInfoDto.setEmailAddress(rs.getString(EMAIL));
                subsProfileInfoDto.setMcpttId(MC_PTTID);

            }


            knLogger.debug(methodName, "returning subscriber Profile Info ", subsProfileInfoDto);
            return subsProfileInfoDto;

        } catch (KnDAOException dbConne) {
            knLogger.error(methodName, "DAO Exception occurred");
            throw dbConne;
        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQL Exception occurred");
            throw KnDbUtil.processException(sqlE, "Failed to select Subscriber Profile - " + sqlE.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to select Subscriber Profile - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            knLogger.info(methodName, "EXIT : select Subscriber Profile");
        }
    }

    /**
     * method to retrieve the Subscriber count for the corporation
     *
     * @param corpId       String corporation Id
     * @param persisterTxn KnPersisterTxn
     * @return int corporation subscriber count
     * @throws KnDAOException DB Layer exception
     */
    public int getCorpSubscriberCnt(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getCorpSubscriberCnt(int, KnPersisterTxn)";
        int corpSubscriberCount = -1;
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String query = null;
        boolean ownedTxn = false;

        knLogger.debug(methodName, "ENTRY: Corp Subscriber count for Corp ID - ", corpId);
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            query = CORP_SUBS_CNT_REAL_MDN_QRY;
            //query = CORP_SUBS_CNT_QRY;
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            //conn = persisterTxn.getDBConnection(pttServerId, true);
            pStmt = conn.prepareStatement(query);
            pStmt.setInt(1, corpId);
            knLogger.debug(methodName, "Query: Executing - ", query);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Query: Executed ");

            if (rs != null) {
                while (rs.next()) {
                    corpSubscriberCount = rs.getInt(1);
                }
            }

            if (ownedTxn) {
                knLogger.debug(methodName, "saving the transaction");
                persisterTxn.save();
            }

        } catch (KnDAOException dbConne) {
            knLogger.error(methodName, "DAO Exception occurred");
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw dbConne;
        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQL Exception occurred");
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(sqlE, "Failed to select Subscriber Profile - " + sqlE.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to select Subscriber Profile - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            knLogger.debug(methodName, "EXIT : select Subscriber Profile");
        }
        return corpSubscriberCount;
    }

    /**
     * @param userId
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public int getServiceAuthStatusByUserId(String userId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getServiceAuthStatusByUserId(String, KnPersisterTxn)";
        int serviceAuthStatus = -1;
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String query = null;
        boolean ownedTxn = false;

        knLogger.debug(methodName, "ENTRY: Get Service Auth Status for userId - ", KnGDPRTemplate.userId(userId));
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            query = SELECT_AUTH_STATUS_QRY;
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            //conn = persisterTxn.getDBConnection(pttServerId, true);
            pStmt = conn.prepareStatement(query);
            pStmt.setString(1, userId);
            knLogger.debug(methodName, "Query: Executing - ", query);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Query: Executed ");

            if (rs != null) {
                if (rs.next()) {
                    serviceAuthStatus = rs.getInt(1);
                }
            }

            if (ownedTxn) {
                knLogger.debug(methodName, "saving the transaction");
                persisterTxn.save();
            }

        } catch (KnDAOException dbConne) {
            knLogger.error(methodName, "DAO Exception occurred");
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw dbConne;
        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQL Exception occurred");
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(sqlE, "Failed to select service auth status from subscriber Profile - "
                    + sqlE.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to select service auth status from subscriber Profile - " + e.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            knLogger.debug(methodName, "EXIT : select service auth status from subscriber Profile");
        }
        return serviceAuthStatus;
    }

    /**
     * @param persistenceDTO
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void activateSubscriber(IPersistenceDTO persistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "activateSubscriber(IPersistenceDTO, KnPersisterTxn)";
        boolean ownedTxn = false;
        Connection conn;
        PreparedStatement pStmt = null;
        String query = null;
        knLogger.entry(methodName, persistenceDTO, persisterTxn);
        try {

            KnSubsProfilePersistDTO subsInfoPersistDTO = (KnSubsProfilePersistDTO) persistenceDTO;
            String mdn = subsInfoPersistDTO.getMdn();
            int serviceAuthStatus = subsInfoPersistDTO.getServiceAuthStatus();
            String imei = subsInfoPersistDTO.getIMEI();
            String clientPassword = subsInfoPersistDTO.getClientPassword();
            long lastProfileUpdateTime = subsInfoPersistDTO.getLastProfileUpdateTime();
            String userAgent = subsInfoPersistDTO.getUserAgent();
            int pvMajorVersion = subsInfoPersistDTO.getClientPVmajorVer();
            int pvMinorVersion = subsInfoPersistDTO.getClientPVminorVer();
            int clientSWType=subsInfoPersistDTO.getSwType();
            int clientPlatformType=subsInfoPersistDTO.getPlatformType();

            //subsInfoPersistDTO.
            int swType=subsInfoPersistDTO.getSwType();
            int platformType=subsInfoPersistDTO.getPlatformType();
            int dynamicQosFlag=subsInfoPersistDTO.getDynamicQosFlag();

            //vocoderId for PV=9
            int vocoderId = subsInfoPersistDTO.getVocoderId();

            String derivedKey = subsInfoPersistDTO.getDerivedKey();

            int serviceAuthStatusOP = subsInfoPersistDTO.getServiceStatusOp();

            int licenseType = subsInfoPersistDTO.getLicenseType();

            StringBuilder strBuffer = new StringBuilder();
            strBuffer.append(UPDATE_QRY);
            strBuffer.append(SERVICE_AUTH_STATUS).append("=?");
            strBuffer.append(", ");
            strBuffer.append(LAST_PROFILE_UPDATE_TIME).append("=?");
            if (imei != null) {
                strBuffer.append(", ");
                strBuffer.append(IMEI).append("=?");
            }
            if (clientPassword != null) {
                strBuffer.append(", ");
                strBuffer.append(CLIENT_PASSWORD).append("=?");
            }
            if (userAgent != null) {
                strBuffer.append(", ");
                strBuffer.append(USER_AGENT).append("=?");
            }
            if (vocoderId != 0){
                strBuffer.append(", ");
                strBuffer.append(VOCODERID).append("=?");
            }
            strBuffer.append(", ");
            strBuffer.append(CLIENTPV_MAJORVERSION).append("=?");
            strBuffer.append(", ");
            strBuffer.append(CLIENTPV_MINORVERSION).append("=?");
            strBuffer.append(" , ");
            strBuffer.append(LAST_ACTIVATION_TIME).append("=?");

            strBuffer.append(", ");
            strBuffer.append(CLIENT_SW_INF).append("=?");
            strBuffer.append(" , ");
            strBuffer.append(CLIENT_PLATFORM_TYPE).append("=?");
            strBuffer.append(" , ");
            strBuffer.append(DYNAMIC_QOS_FLAG).append("=?");
            if (derivedKey != null) {
                strBuffer.append(", ");
                strBuffer.append(DERIVEDKEY).append("=?");
            }
			strBuffer.append(", ");
            strBuffer.append(SERVICE_STATUS_OP).append("=?");
            strBuffer.append(", ");
            strBuffer.append(CLIENT_TYPE).append("=?");
            strBuffer.append(", ");
            strBuffer.append(SUBS_FS1).append("=?");
            strBuffer.append(", ");
            strBuffer.append(CLIENT_FS1).append("=?");
            strBuffer.append(", ");
            strBuffer.append(ACTIVE_FS1).append("=?");
            strBuffer.append(", ");
            strBuffer.append(OPS_FS1).append("=?");
			strBuffer.append(", ");
            strBuffer.append(XDMS_FS1).append("=?");
            strBuffer.append(", ");
            strBuffer.append(SUBS_FS2).append("=?");
            strBuffer.append(", ");
            strBuffer.append(CLIENT_FS2).append("=?");
            strBuffer.append(", ");
            strBuffer.append(ACTIVE_FS2).append("=?");
            strBuffer.append(", ");
            strBuffer.append(OPS_FS2).append("=?");
			strBuffer.append(", ");
            strBuffer.append(XDMS_FS2).append("=?");

            if (licenseType != 0){
                strBuffer.append(", ").append(LICENSE_TYPE).append("=?");
            }
            //Privacy Opt Status changes start
            knLogger.info(methodName, "pvMajorVersion- ", pvMajorVersion);
            if(pvMajorVersion>=KnConstants.PROTOCOL_VERSION_16_X && subsInfoPersistDTO.getPrivacyExecutorBasedonFlag() == 1)
            {
                strBuffer.append(", ");
                strBuffer.append(PRIVACY_OPT_STATUS).append("=?");

                knLogger.info(methodName, "strBuffer- ", strBuffer);
            }
            //Privacy Opt Status changes end
            strBuffer.append(" WHERE ").append(MDN).append("=?");

            query = strBuffer.toString();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);
            int columnIndex = 0;
            pStmt.setInt(++columnIndex, serviceAuthStatus);
            pStmt.setLong(++columnIndex, lastProfileUpdateTime);
            if (imei != null) {
                pStmt.setString(++columnIndex, imei);
            }
            if (clientPassword != null) {
                pStmt.setString(++columnIndex, clientPassword);
            }
            if (userAgent != null) {
                pStmt.setString(++columnIndex, userAgent);
            }
            if (vocoderId != 0){
                pStmt.setInt(++columnIndex, vocoderId);
            }
            pStmt.setInt(++columnIndex, pvMajorVersion);
            pStmt.setInt(++columnIndex, pvMinorVersion);
            pStmt.setLong(++columnIndex, lastProfileUpdateTime);
            pStmt.setInt(++columnIndex, clientSWType);
            pStmt.setInt(++columnIndex, clientPlatformType);

            pStmt.setInt(++columnIndex, dynamicQosFlag);
            if (derivedKey != null) {
                pStmt.setString(++columnIndex, derivedKey);
            }
            pStmt.setInt(++columnIndex, serviceAuthStatusOP);
            pStmt.setInt(++columnIndex, subsInfoPersistDTO.getSubsClientType());
            pStmt.setLong(++columnIndex, KnGeneralUtil.convertHexStringToLong(subsInfoPersistDTO.getSubsFS2()));
            pStmt.setLong(++columnIndex, KnGeneralUtil.convertHexStringToLong(subsInfoPersistDTO.getClientFS2()));
            pStmt.setLong(++columnIndex, KnGeneralUtil.convertHexStringToLong(subsInfoPersistDTO.getActiveFS2()));
            pStmt.setLong(++columnIndex, KnGeneralUtil.convertHexStringToLong(subsInfoPersistDTO.getOpsFS2()));
			pStmt.setLong(++columnIndex, KnGeneralUtil.convertHexStringToLong(subsInfoPersistDTO.getXdmsFS2()));
			pStmt.setString(++columnIndex, KnGeneralUtil.getFeatureSet(subsInfoPersistDTO.getSubsFS2()));
            pStmt.setString(++columnIndex, KnGeneralUtil.getFeatureSet(subsInfoPersistDTO.getClientFS2()));
            pStmt.setString(++columnIndex, KnGeneralUtil.getFeatureSet(subsInfoPersistDTO.getActiveFS2()));
            pStmt.setString(++columnIndex, KnGeneralUtil.getFeatureSet(subsInfoPersistDTO.getOpsFS2()));
			pStmt.setString(++columnIndex, KnGeneralUtil.getFeatureSet(subsInfoPersistDTO.getXdmsFS2()));
            if (licenseType != 0){
                pStmt.setInt(++columnIndex, licenseType);
            }
            knLogger.info(methodName, "--->subsInfoPersistDTO.getPrivacyExecutorBasedonFlag()- ", subsInfoPersistDTO.getPrivacyExecutorBasedonFlag() );
            knLogger.info(methodName, "pvMajorVersion- ", pvMajorVersion);
            if(pvMajorVersion>=KnConstants.PROTOCOL_VERSION_16_X && subsInfoPersistDTO.getPrivacyExecutorBasedonFlag() == 1) {
                knLogger.info(methodName, "subsInfoPersistDTO.isFlagForPrivacy()- ", subsInfoPersistDTO.isFlagForPrivacy());
                if (subsInfoPersistDTO.isFlagForPrivacy() == true) {
                    pStmt.setInt(++columnIndex, 0);
                } else {
                    pStmt.setInt(++columnIndex, 1);
                }
                knLogger.info(methodName, "pStmt- ", pStmt);
            }
            pStmt.setString(++columnIndex, mdn);

            knLogger.debug(methodName, "QUERY: Executing query - ", query, " with DTO - ", subsInfoPersistDTO
            );
            knLogger.debug(methodName, "QUERY: Executed - ", pStmt.executeUpdate());

        } catch (SQLException sqlE) {
            throw KnDbUtil.processException(sqlE, "Failed to activate Subscriber - " + sqlE.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } catch (Exception e) {
            throw KnDbUtil.processException(e, "Failed to activate Subscriber - " + e.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeStatement(pStmt);
            knLogger.debug(methodName, "EXIT: activate Subscriber with DTO - ", persistenceDTO);
        }
        knLogger.exit(methodName);
    }

    /**
     * Update the service auth status of the Subscriber
     *
     * @param subsProfilePersistDTO KnSubsProfilePersistDTO
     * @param persisterTxn          KnPersisterTxn
     * @throws KnDAOException DB Layer exception
     */
    public void updateLastProfileUpdateTime(KnSubsProfilePersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateLastProfileUpdateTime(KnSubsProfilePersistDTO, KnPersisterTxn)";
        boolean ownedTxn = false;
        Connection conn;
        PreparedStatement pStmt = null;
        String query = null;
        knLogger.debug(methodName, "ENTRY: Update LastProfileUpdateTime with DTO -", subsProfilePersistDTO);
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            long lastProfileUpdateTime = subsProfilePersistDTO.getLastProfileUpdateTime();
            String mdn = subsProfilePersistDTO.getMdn();

            StringBuilder queryBuffer = new StringBuilder();
            queryBuffer.append(UPDATE_QRY);
            queryBuffer.append(LAST_PROFILE_UPDATE_TIME).append("=? ");
            queryBuffer.append("WHERE ").append(MDN).append("=?");

            query = queryBuffer.toString();

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);
            pStmt.setLong(1, lastProfileUpdateTime);
            pStmt.setString(2, mdn);

            knLogger.debug(methodName, "QUERY: Executing the query - ", query);
            pStmt.executeUpdate();
            knLogger.debug(methodName, "QUERY: Executed ");

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
        } catch (KnDAOException dbConne) {
            knLogger.error(methodName, "DAO Exception occurred");
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw dbConne;
        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQL Exception occurred");
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(sqlE, "Failed to update Last profile Update Time - " + sqlE.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to update Last profile Update Time - " + e.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeStatement(pStmt);
            knLogger.debug(methodName, "EXIT: update Last profile Update Time");
        }
    }

    /**
     * method to update the Subscriber Profile.
     *
     * @param persistenceDTO IPersistenceDTO
     * @param persisterTxn   KnPersisterTxn
     * @throws KnDAOException DB layer Exception
     */
    public void updateUserAgent(IPersistenceDTO persistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateUserAgent(IPersistenceDTO, KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        boolean ownedTxn = false;
        knLogger.debug(methodName, "ENTRY: Update User Agent of a Subscriber  with DTO - ", persistenceDTO);

        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }
            KnSubsProfilePersistDTO subsProfilePersistDTO = (KnSubsProfilePersistDTO) persistenceDTO;
            String mdn = subsProfilePersistDTO.getMdn();
            String userAgent = subsProfilePersistDTO.getUserAgent();
            int pvMajorVersion = subsProfilePersistDTO.getClientPVmajorVer();
            int pvMinorVersion = subsProfilePersistDTO.getClientPVminorVer();
            String accountId = subsProfilePersistDTO.getAccountId();
            long lastProfileUpdateTime = subsProfilePersistDTO.getLastProfileUpdateTime();

            StringBuilder queryBuffer = new StringBuilder();
            queryBuffer.append(UPDATE_QRY);

            queryBuffer.append(LAST_PROFILE_UPDATE_TIME).append("=?,");

            if (userAgent != null) {
                queryBuffer.append(USER_AGENT).append("=? , ");
            }
            if (pvMajorVersion != -1) {
                queryBuffer.append(CLIENTPV_MAJORVERSION).append("=?, ");
            }
            if (pvMinorVersion != -1) {
                queryBuffer.append(CLIENTPV_MINORVERSION).append("=? ");
            }

            queryBuffer.append(" WHERE ").append(MDN).append("=?");

            query = queryBuffer.toString();

            knLogger.debug(methodName, "QUERY::: Executing the Query - ", query);
            //conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);

            pStmt = conn.prepareStatement(query);
            int columnIndex = 0;

            pStmt.setLong(++columnIndex, lastProfileUpdateTime);

            if (userAgent != null) {
                pStmt.setString(++columnIndex, userAgent);
            }
            if (pvMajorVersion != -1) {
                pStmt.setInt(++columnIndex, pvMajorVersion);
            }
            if (pvMinorVersion != -1) {
                pStmt.setInt(++columnIndex, pvMinorVersion);
            }
            pStmt.setString(++columnIndex, mdn);

            knLogger.debug(methodName, "QUERY: Executing the Query - ", query, " DTO - ", persistenceDTO);

            pStmt.executeUpdate();
            knLogger.debug(methodName, "Query: Executed ");

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }

        } catch (KnDAOException dbConne) {
            knLogger.error(methodName, "DAO Exception occurred");
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw dbConne;
        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQL Exception occurred");
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(sqlE, "Failed to update User agent of a Subscriber Profile - " + sqlE.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to update Subscriber Profile - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeStatement(pStmt);
            knLogger.debug(methodName, "EXIT: update Subscriber Profile");
        }
    }


    /**
     * Update the service auth status of the Subscriber
     *
     * @param subsProfilePersistDTO KnSubsProfilePersistDTO
     * @param persisterTxn          KnPersisterTxn
     * @throws KnDAOException DB Layer exception
     */
    public KnSubsProfilePersistDTO updateServiceAuthStatus(KnSubsProfilePersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "upateServiceAuthStatus(KnSubsProfilePersistDTO, KnPersisterTxn)";
        boolean ownedTxn = false;
        Connection conn;
        PreparedStatement pStmt = null;
        String query = null;
        knLogger.debug(methodName, "ENTRY: update Service Auth Status with DTO -", subsProfilePersistDTO);
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            int serviceAuthStatus = subsProfilePersistDTO.getServiceAuthStatus();
            String clientPassword = subsProfilePersistDTO.getClientPassword();
            String userAgent = subsProfilePersistDTO.getUserAgent();
            long lastProfileUpdateTime = subsProfilePersistDTO.getLastProfileUpdateTime();
            String mdn = subsProfilePersistDTO.getMdn();
            int serviceStatusOp = subsProfilePersistDTO.getServiceStatusOp();

            StringBuilder queryBuffer = new StringBuilder();
            queryBuffer.append(UPDATE_QRY);
            queryBuffer.append(SERVICE_AUTH_STATUS).append("=?, ");
            queryBuffer.append(CLIENT_PASSWORD).append("=?, ");
            queryBuffer.append(USER_AGENT).append("=?, ");
            queryBuffer.append(LAST_PROFILE_UPDATE_TIME).append("=?, ");
            queryBuffer.append(SERVICE_STATUS_OP).append("=? ");
            queryBuffer.append("WHERE ").append(MDN).append("=?");
            query = queryBuffer.toString();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);
            pStmt.setInt(1, serviceAuthStatus);
            if (clientPassword == null) {
                pStmt.setNull(2, java.sql.Types.CHAR);
            } else {
                pStmt.setString(2, clientPassword);
            }
            if (userAgent == null) {
                pStmt.setNull(3, java.sql.Types.CHAR);
            } else {
                pStmt.setString(3, userAgent);
            }

            pStmt.setLong(4, lastProfileUpdateTime);
            pStmt.setInt(5,serviceStatusOp);
             pStmt.setString(6, mdn);

            knLogger.debug(methodName, "QUERY: Executing the query - ", query);
            pStmt.executeUpdate();
            knLogger.debug(methodName, "QUERY: Executed ");

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
        } catch (KnDAOException dbConne) {
            knLogger.error(methodName, "DAO Exception occurred");
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw dbConne;
        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQL Exception occurred");
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(sqlE, "Failed to update Service Auth Status - " + sqlE.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to update Service Auth Status - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeStatement(pStmt);
            knLogger.debug(methodName, "EXIT: update Service Auth Status");
        }
        return subsProfilePersistDTO;
    }


    public KnSubsProfilePersistDTO updateServiceAuthStatusForUPM(KnSubsProfilePersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateServiceAuthStatusForUPM(KnSubsProfilePersistDTO, KnPersisterTxn)";
        boolean ownedTxn = false;
        Connection conn;
        PreparedStatement pStmt = null;
        String query = null;
        knLogger.debug(methodName, "ENTRY: update Service Auth Status with DTO -", subsProfilePersistDTO);
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            int serviceAuthStatus = subsProfilePersistDTO.getServiceAuthStatus();
            String clientPassword = subsProfilePersistDTO.getClientPassword();
            String userAgent = subsProfilePersistDTO.getUserAgent();
            long lastProfileUpdateTime = subsProfilePersistDTO.getLastProfileUpdateTime();
            String mdn = subsProfilePersistDTO.getMdn();
            List<String> mdnList = subsProfilePersistDTO.getMdnList();
            int serviceStatusOp = subsProfilePersistDTO.getServiceStatusOp();

            StringBuffer keys = com.kodiak.common.dao.KnDbUtil.convertListToStringBuffer(mdnList);

            StringBuilder queryBuffer = new StringBuilder();
            queryBuffer.append(UPDATE_QRY);
            queryBuffer.append(SERVICE_AUTH_STATUS).append("=?, ");
            queryBuffer.append(CLIENT_PASSWORD).append("=?, ");
            queryBuffer.append(USER_AGENT).append("=?, ");
            queryBuffer.append(LAST_PROFILE_UPDATE_TIME).append("=?, ");
            queryBuffer.append(SERVICE_STATUS_OP).append("=?, ");
            queryBuffer.append(PREV_SERVICE_AUTH_STATUS).append("=?, ");
            queryBuffer.append(PREV_SERVICE_AUTH_STATUS_UPDATETIME).append("=? ");
            queryBuffer.append("WHERE MDN IN ").append(keys.toString());
            query = queryBuffer.toString();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);
            pStmt.setInt(1, serviceAuthStatus);
            if (clientPassword == null) {
                pStmt.setNull(2, java.sql.Types.CHAR);
            } else {
                pStmt.setString(2, clientPassword);
            }
            if (userAgent == null) {
                pStmt.setNull(3, java.sql.Types.CHAR);
            } else {
                pStmt.setString(3, userAgent);
            }

            pStmt.setLong(4, lastProfileUpdateTime);
            pStmt.setInt(5,serviceStatusOp);
            if (subsProfilePersistDTO.getPreviousServiceAuthStatusToStore() != null) {
                pStmt.setInt(6, subsProfilePersistDTO.getPreviousServiceAuthStatusToStore());
            } else {
                pStmt.setNull(6, java.sql.Types.INTEGER);
            }
            pStmt.setLong(7, System.currentTimeMillis());
            //pStmt.setString(6, mdn);

            knLogger.debug(methodName, "QUERY: Executing the query - ", query);
            pStmt.executeUpdate();
            knLogger.debug(methodName, "QUERY: Executed ");

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
        } catch (KnDAOException dbConne) {
            knLogger.error(methodName, "DAO Exception occurred");
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw dbConne;
        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQL Exception occurred");
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(sqlE, "Failed to update Service Auth Status - " + sqlE.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to update Service Auth Status - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeStatement(pStmt);
            knLogger.debug(methodName, "EXIT: update Service Auth Status");
        }
        return subsProfilePersistDTO;
    }
    /**
     * method to create subscriber profile.
     * This method has to be used only in case of Change MDN operation where complete subscriber profile
     * has to be populated with old MDN data
     *
     * @param persistenceDTO KnSubsProfilePersistDTO
     * @param persisterTxn   KnPersisterTxn
     * @throws KnDAOException DB layer Exception
     */
    public KnSubsProfilePersistDTO createSubsProfile(IPersistenceDTO persistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "createSubsProfile(IPersistenceDTO, KnPersisterTxn)";
        String query = null;
        KnSubsProfilePersistDTO knSubsProfilePersistDTO =null;
        Connection conn;
        PreparedStatement pStmt = null;
        boolean ownedTxn = false;

        knLogger.debug(methodName, "ENTRY: Create Subscriber Profile with DTO - ", persistenceDTO);

        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening Transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            KnSubsProfilePersistDTO subsInfoPersistDto = (KnSubsProfilePersistDTO) persistenceDTO;
            String mdn = subsInfoPersistDto.getMdn();
            String pocHome = subsInfoPersistDto.getPoCHome();
            String presenceHome = subsInfoPersistDto.getPresenceHome();
            String xdmsHome = subsInfoPersistDto.getXDMSHome();
            Long profileCreationTime = subsInfoPersistDto.getProfileCreationTime();
            Long lastProfileUpdateTime = subsInfoPersistDto.getLastProfileUpdateTime();
            Boolean corpContactPairingInd = subsInfoPersistDto.getPairingInd();
            String networkName = subsInfoPersistDto.getNetworkName();
            int pairingInd = -1;
            if (corpContactPairingInd != null) {
                if (corpContactPairingInd) {
                    pairingInd = 1;
                } else {
                    pairingInd = 0;
                }
            }
            int payType = subsInfoPersistDto.getPayType();
            int serviceAuthStatus = subsInfoPersistDto.getServiceAuthStatus();
            int svcStatusOp = subsInfoPersistDto.getServiceStatusOp();
            int svcStatusAuthUser = subsInfoPersistDto.getServiceStatusAuthUser();
            String affiliateId = subsInfoPersistDto.getAffiliateId();
            String imei = subsInfoPersistDto.getIMEI();
            String clientPassword = subsInfoPersistDto.getClientPassword();

            int publicSubscriptionType = subsInfoPersistDto.getPublicSubscriptionType();
            int corpSubscriptionType = subsInfoPersistDto.getCorporateSubscriptionType();
            int corpId = -1;
            int corpContactListId = -1;
            if (corpSubscriptionType == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
                corpId = subsInfoPersistDto.getCorpId();
                corpContactListId = subsInfoPersistDto.getCorpContactListId();
            }

            String email = subsInfoPersistDto.getEmailAddress();
            int subsClientType = subsInfoPersistDto.getSubsClientType();
            int dispatchGrpMember = subsInfoPersistDto.getDispatchGroupMember();
            String accountID = subsInfoPersistDto.getAccountId();
            String userAgent = subsInfoPersistDto.getUserAgent();
            String ufmi=subsInfoPersistDto.getUfmi();
            String iDenUserName=subsInfoPersistDto.getiDenUserName();
            String iDenPassword=subsInfoPersistDto.getiDenPassword();
            String iDenBusUnitId=subsInfoPersistDto.getiDenBusUnitId();
            String aliasMdn=subsInfoPersistDto.getAliasMdn();
            ArrayList<String> queryFields = new ArrayList<String>();
            queryFields.add(MDN);
            queryFields.add(POC_HOME);
            queryFields.add(PRESENCE_HOME);
            queryFields.add(XDMS_HOME);
            queryFields.add(SUBSCR_CREATION_TIME);
            queryFields.add(LAST_PROFILE_UPDATE_TIME);
            if (networkName != null && !networkName.equalsIgnoreCase("")) {
                queryFields.add(SUBSCR_NAME);
            }
            queryFields.add(SERVICE_AUTH_STATUS);
            queryFields.add(PUBLIC_SUBSCRIPTION_TYPE);
            queryFields.add(CORP_SUBSCRIPTION_TYPE);
            if (corpContactPairingInd != null) {
                queryFields.add(CORP_CONTACT_PAIRING_IND);
            }

            if (payType != -1) {
                queryFields.add(PAY_TYPE);
            }
            if (affiliateId != null) {
                queryFields.add(AFFILIATE_ID);
            }
            if (corpSubscriptionType == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
                queryFields.add(CORP_ID);
                queryFields.add(CORPCONTACTLISTID);
            }
            if (imei != null) {
                queryFields.add(IMEI);
            }
            if (clientPassword != null) {
                queryFields.add(CLIENT_PASSWORD);
            }
            queryFields.add(USER_AGENT);
            if (email != null) {
                queryFields.add(EMAIL);
            }
            queryFields.add(CLIENT_TYPE);
            queryFields.add(DISPATCH_GRP_MEMBER);
            queryFields.add(ACCOUNT_ID);
            if (subsInfoPersistDto.getLastActivationTime() != null) {
                queryFields.add(LAST_ACTIVATION_TIME);
            }
            queryFields.add(ADDLINFO);
            queryFields.add(DISPATCH_TYPE);
            queryFields.add(USER_ID);
            queryFields.add(SERVICE_STATUS_AUTHUSER);
            queryFields.add(SERVICE_STATUS_OP);
			queryFields.add(LICENSE_TYPE);
			queryFields.add(QPPPACKID);
			queryFields.add(UFMI);
			queryFields.add(IDEN_USERNAME);
			queryFields.add(IDEN_PASSWORD);
			queryFields.add(IDEN_BUSUNITID);
			queryFields.add(SEGMENT_INDICATOR);
			queryFields.add(ALIAS_MDN);
			queryFields.add(SUBS_FS1);
            queryFields.add(CLIENT_FS1);
            queryFields.add(ACTIVE_FS1);
            queryFields.add(OPS_FS1);
            queryFields.add(CORPADMIN_FS1);
			queryFields.add(XDMS_FS1);
			queryFields.add(SUBS_FS2);
            queryFields.add(CLIENT_FS2);
            queryFields.add(ACTIVE_FS2);
            queryFields.add(OPS_FS2);
            queryFields.add(CORPADMIN_FS2);
			queryFields.add(XDMS_FS2);
			queryFields.add(MC_ID);
			queryFields.add(MC_PTTID);
			queryFields.add(MC_VIDEOID);
			queryFields.add(MC_DATAID);
			queryFields.add(MCPTT_COMPLIANCE);
			if (KnConstants.MCSCOMPLIANCE == subsInfoPersistDto.getMcsCompliance()) {
				queryFields.add(CLIENTPV_MAJORVERSION);
				queryFields.add(CLIENTPV_MINORVERSION);
            }
			queryFields.add(ISDEFAULTPROFILE);
			queryFields.add(USERPROFILEINDEX);
            queryFields.add(USERPROFILEFS2);
			queryFields.add(FEATURE_REL_VERSION);
            queryFields.add(CLUSTERID);
            queryFields.add(HIERARCHY_ID);
            queryFields.add(HIERARCHY_ROOT);
            query = KnDbUtil.getInsertQuery(TABLENAME, queryFields);

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            knLogger.debug(methodName, "ccreateSubsProfile : ", conn);
            pStmt = conn.prepareStatement(query);
            int columnIndex = 0; // will dynamically update the column index value as per the received values.
            pStmt.setString(++columnIndex, mdn);
            pStmt.setString(++columnIndex, pocHome);
            pStmt.setString(++columnIndex, presenceHome);
            pStmt.setString(++columnIndex, xdmsHome);
            pStmt.setLong(++columnIndex, profileCreationTime);
            pStmt.setLong(++columnIndex, lastProfileUpdateTime);
            if (networkName != null && !networkName.equalsIgnoreCase("")) {
            	//multilingual revert changes
            	networkName = new String(networkName.getBytes("UTF-8"),"8859_1");
                pStmt.setString(++columnIndex, networkName);
            }
            pStmt.setInt(++columnIndex, serviceAuthStatus);
            pStmt.setInt(++columnIndex, publicSubscriptionType);
            pStmt.setInt(++columnIndex, corpSubscriptionType);
            if (corpContactPairingInd != null) {
                pStmt.setInt(++columnIndex, pairingInd);
            }
            if (payType != -1) {
                pStmt.setInt(++columnIndex, payType);
            }
            if (affiliateId != null) {
                pStmt.setString(++columnIndex, affiliateId);
            }
            if (corpSubscriptionType == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
                pStmt.setInt(++columnIndex, corpId);
                pStmt.setInt(++columnIndex, corpContactListId);
            }
            if (imei != null) {
                pStmt.setString(++columnIndex, imei);
            }
            if (clientPassword != null) {
                pStmt.setString(++columnIndex, clientPassword);
            }
            pStmt.setString(++columnIndex, userAgent);

            if (email != null) {
                pStmt.setString(++columnIndex, email);
            }
            pStmt.setInt(++columnIndex, subsClientType);
            pStmt.setInt(++columnIndex, dispatchGrpMember);
            pStmt.setString(++columnIndex, accountID);
            if (subsInfoPersistDto.getLastActivationTime() != null) {
                pStmt.setLong(++columnIndex, subsInfoPersistDto.getLastActivationTime());
            }
            pStmt.setInt(++columnIndex, subsInfoPersistDto.getHierarchyType().value());
            pStmt.setInt(++columnIndex,subsInfoPersistDto.getDispatchType());
            pStmt.setString(++columnIndex,subsInfoPersistDto.getUserId());
            pStmt.setInt(++columnIndex,svcStatusAuthUser);
            pStmt.setInt(++columnIndex,svcStatusOp);
			pStmt.setInt(++columnIndex,subsInfoPersistDto.getLicenseType());
			pStmt.setInt(++columnIndex,subsInfoPersistDto.getQppPkgId());
			pStmt.setString(++columnIndex, ufmi);
			pStmt.setString(++columnIndex, iDenUserName);
			pStmt.setString(++columnIndex, iDenPassword);
			pStmt.setString(++columnIndex, iDenBusUnitId);
			pStmt.setString(++columnIndex, subsInfoPersistDto.getFirstNetIndicator());
			pStmt.setString(++columnIndex, aliasMdn);
			pStmt.setLong(++columnIndex, KnGeneralUtil.convertHexStringToLong(subsInfoPersistDto.getSubsFS2()));
			pStmt.setLong(++columnIndex, KnGeneralUtil.convertHexStringToLong(subsInfoPersistDto.getClientFS2()));
			pStmt.setLong(++columnIndex, KnGeneralUtil.convertHexStringToLong(subsInfoPersistDto.getActiveFS2()));
			pStmt.setLong(++columnIndex, KnGeneralUtil.convertHexStringToLong(subsInfoPersistDto.getOpsFS2()));
			pStmt.setLong(++columnIndex, KnGeneralUtil.convertHexStringToLong(subsInfoPersistDto.getCorpAdminFS2()));
			pStmt.setLong(++columnIndex, KnGeneralUtil.convertHexStringToLong(subsInfoPersistDto.getXdmsFS2()));
			pStmt.setString(++columnIndex, KnGeneralUtil.getFeatureSet(subsInfoPersistDto.getSubsFS2()));
			pStmt.setString(++columnIndex, KnGeneralUtil.getFeatureSet(subsInfoPersistDto.getClientFS2()));
			pStmt.setString(++columnIndex, KnGeneralUtil.getFeatureSet(subsInfoPersistDto.getActiveFS2()));
			pStmt.setString(++columnIndex, KnGeneralUtil.getFeatureSet(subsInfoPersistDto.getOpsFS2()));
			pStmt.setString(++columnIndex, KnGeneralUtil.getFeatureSet(subsInfoPersistDto.getCorpAdminFS2()));
			pStmt.setString(++columnIndex, KnGeneralUtil.getFeatureSet(subsInfoPersistDto.getXdmsFS2()));
			pStmt.setBytes(++columnIndex, subsInfoPersistDto.getMcId().getBytes(StandardCharsets.UTF_8));
			pStmt.setBytes(++columnIndex, subsInfoPersistDto.getMcpttId().getBytes(StandardCharsets.UTF_8));
			pStmt.setBytes(++columnIndex, subsInfoPersistDto.getMcVideoId().getBytes(StandardCharsets.UTF_8));
			pStmt.setBytes(++columnIndex, subsInfoPersistDto.getMcDataId().getBytes(StandardCharsets.UTF_8));
			pStmt.setInt(++columnIndex,subsInfoPersistDto.getMcpttCompliance());
			if (KnConstants.MCSCOMPLIANCE == subsInfoPersistDto.getMcsCompliance()) {
				pStmt.setInt(++columnIndex, subsInfoPersistDto.getClientPVmajorVer());
				pStmt.setInt(++columnIndex, subsInfoPersistDto.getClientPVminorVer());

            }
			pStmt.setInt(++columnIndex, subsInfoPersistDto.getIsDefaultProfile());
			pStmt.setInt(++columnIndex, subsInfoPersistDto.getUserProfileIndex());
            pStmt.setString(++columnIndex, KnGeneralUtil.getFeatureSet(subsInfoPersistDto.getUserProfileFS2()));
			pStmt.setString(++columnIndex, String.valueOf(subsInfoPersistDto.getFeatureRelVersion()));
            pStmt.setString(++columnIndex, subsInfoPersistDto.getClusterId());
            pStmt.setString(++columnIndex, subsInfoPersistDto.getHierarchyId());
            pStmt.setString(++columnIndex, subsInfoPersistDto.getHierarchyRoot());
            knLogger.debug(methodName, "Query: Executing -", query, ", persist DTO - ", persistenceDTO);
            pStmt.executeUpdate();
            knLogger.debug(methodName, "Query: Executed ");

            if (ownedTxn) {
                knSubsProfilePersistDTO=subsInfoPersistDto;
                knLogger.debug(methodName, "Saving transaction");
                persisterTxn.save();
            }

        } catch (KnDAOException dbConne) {
            knLogger.error(methodName, "DAO Exception occurred");
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw dbConne;
        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQL Exception occurred");
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(sqlE, "Failed to create Subscriber Profile - " + sqlE.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to create Subscriber Profile - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeStatement(pStmt);
            knLogger.debug(methodName, "EXIT : create Subscriber Profile");
        }
        return knSubsProfilePersistDTO;
    }

    /**
     * method to retrieve the client password
     *
     * @param mdn          String
     * @param persisterTxn KnPersisterTxn
     * @return String
     * @throws KnDAOException DB Layer Exception
     */
    public String retrieveClientPassword(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "retrieveClientPassword(String, KnPersisterTxn)";
        String clientPassword = null;
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        knLogger.debug(methodName, "ENTRY: retrieve Client password - ", KnGDPRTemplate.mdn(mdn));
        boolean ownedTxn = false;

        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            query = "SELECT " + CLIENT_PASSWORD + " FROM " + TABLENAME + " WHERE " + MDN + "= ?";
            if (ownedTxn) {
                conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            } else {
                conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            }

            pStmt = conn.prepareStatement(query);
            pStmt.setString(1, mdn);

            knLogger.debug(methodName, "QUERY: Executing - ", query, " mdn - ", KnGDPRTemplate.mdn(mdn));
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "QUERY: Executed ");

            if (rs != null) {
                while (rs.next()) {
                    clientPassword = rs.getString(CLIENT_PASSWORD);
                }
            }

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
        } catch (KnDAOException dbConne) {
            knLogger.error(methodName, "DAO Exception occurred");
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw dbConne;
        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQL Exception occurred");
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(sqlE, "Failed to retrieve Client Password - " + sqlE.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to retrieve Client Password - " + e.getMessage(), pttServerId,
                    KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            knLogger.debug(methodName, "EXIT : retrieve client password - ", clientPassword);
        }
        return clientPassword;
    }

    /**
     * method to retrieve the Subscriber count
     *
     * @param persisterTxn KnPersisterTxn
     * @return int subscriber count
     * @throws KnDAOException DB Layer exception
     */
    public int getSubscriberCount(KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubscriberCount(KnPersisterTxn)";
        int subscriberCount = -1;
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String query = null;
        boolean ownedTxn = false;

        knLogger.debug(methodName, "ENTRY: Subscriber count ");
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            query = SUBS_CNT_QRY;

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pStmt = conn.prepareStatement(query);
            knLogger.debug(methodName, "Query: Executing - ", query);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Query: Executed ");

            if (rs != null) {
                while (rs.next()) {
                    subscriberCount = rs.getInt(1);
                }
            }

            if (ownedTxn) {
                knLogger.debug(methodName, "saving the transaction");
                persisterTxn.save();
            }

        } catch (KnDAOException dbConne) {
            knLogger.error(methodName, "DAO Exception occurred");
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw dbConne;
        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQL Exception occurred");
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(sqlE, "Failed to get Subscriber count - " + sqlE.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to get Subscriber count  - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            knLogger.debug(methodName, "EXIT : Subscriber count  ");
        }
        return subscriberCount;
    }
    public void updateSubsFS(IPersistenceDTO persistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateSubsFS(KnSubsProfilePersistDTO, KnPersisterTxn)";
        Connection conn;
        PreparedStatement pStmt = null;
        String query = null;

        try {
            KnSubsProfilePersistDTO subsProfilePersistDTO = (KnSubsProfilePersistDTO) persistenceDTO;
            knLogger.debug(methodName, "ENTRY: updatesubsFS with DTO - ", subsProfilePersistDTO);

            long lastProfileUpdateTime = subsProfilePersistDTO.getLastProfileUpdateTime();
            int clientType=subsProfilePersistDTO.getSubsClientType();
            String mdn = subsProfilePersistDTO.getMdn();

            StringBuilder queryBuffer = new StringBuilder();
            queryBuffer.append(UPDATE_QRY);
            queryBuffer.append(ACTIVE_FS1).append("=?, ");
            queryBuffer.append(SUBS_FS1).append("=?, ");
            queryBuffer.append(CLIENT_TYPE).append("=?, ");
            queryBuffer.append(ACTIVE_FS2).append("=?, ");
            queryBuffer.append(SUBS_FS2).append("=?, ");
            queryBuffer.append(LAST_PROFILE_UPDATE_TIME).append("=? ");
            queryBuffer.append("WHERE ").append(MDN).append("=?");

            query = queryBuffer.toString();

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);
            pStmt.setLong(1, KnGeneralUtil.convertHexStringToLong(subsProfilePersistDTO.getActiveFS2()));
            pStmt.setLong(2, KnGeneralUtil.convertHexStringToLong(subsProfilePersistDTO.getSubsFS2()));
            pStmt.setLong(3, clientType);
            pStmt.setString(4, KnGeneralUtil.getFeatureSet(subsProfilePersistDTO.getActiveFS2()));
            pStmt.setString(5, KnGeneralUtil.getFeatureSet(subsProfilePersistDTO.getSubsFS2()));
            pStmt.setLong(6, lastProfileUpdateTime);
            pStmt.setString(7, mdn);

            knLogger.debug(methodName, "QUERY: Executing the query - ", query,
                    " with DTO - ", subsProfilePersistDTO);
            pStmt.executeUpdate();
            knLogger.debug(methodName, "QUERY: Executed ");

        }catch (SQLException sqlE) {
            knLogger.error(methodName, "SQL Exception occurred");
            throw KnDbUtil.processException(sqlE, "Failed to update  subsFS - " + sqlE.getMessage(), pttServerId,
                    KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to update subsFS- " + e.getMessage(), pttServerId,
                    KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeStatement(pStmt);
        }
    }
    /**
     * method to update the active Feature Set
     *
     * @param subsProfilePersistDTO KnSubsProfilePersistDTO
     * @param persisterTxn          KnPersisterTxn
     * @throws KnDAOException DB Exception
     */
    public void updateActiveFS(KnSubsProfilePersistDTO subsProfilePersistDTO, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "updateActiveFS(KnSubsProfilePersistDTO, boolean, KnPersisterTxn)";
        boolean ownedTxn = false;
        Connection conn;
        PreparedStatement pStmt = null;
        String query = null;
        knLogger.debug(methodName, "ENTRY: update Active FS with DTO - ", subsProfilePersistDTO);
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            long lastProfileUpdateTime = subsProfilePersistDTO.getLastProfileUpdateTime();
            String mdn = subsProfilePersistDTO.getMdn();

            StringBuilder queryBuffer = new StringBuilder();
            queryBuffer.append(UPDATE_QRY);
            queryBuffer.append(ACTIVE_FS1).append("=?, ");
            queryBuffer.append(SUBS_FS1).append("=?, ");
            queryBuffer.append(ACTIVE_FS2).append("=?, ");
            queryBuffer.append(SUBS_FS2).append("=?, ");
            queryBuffer.append(LAST_PROFILE_UPDATE_TIME).append("=? ");
            queryBuffer.append("WHERE ").append(MDN).append("=?");

            query = queryBuffer.toString();

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            pStmt = conn.prepareStatement(query);
            pStmt.setLong(1, KnGeneralUtil.convertHexStringToLong(subsProfilePersistDTO.getActiveFS2()));
            pStmt.setLong(2, KnGeneralUtil.convertHexStringToLong(subsProfilePersistDTO.getSubsFS2()));
            pStmt.setString(3, KnGeneralUtil.getFeatureSet(subsProfilePersistDTO.getActiveFS2()));
            pStmt.setString(4, KnGeneralUtil.getFeatureSet(subsProfilePersistDTO.getSubsFS2()));
            pStmt.setLong(5, lastProfileUpdateTime);
            pStmt.setString(6, mdn);

            knLogger.debug(methodName, "QUERY: Executing the query - ", query,
                    " with DTO - ", subsProfilePersistDTO);
            pStmt.executeUpdate();
            knLogger.debug(methodName, "QUERY: Executed ");

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
        } catch (KnDAOException dbConne) {
            knLogger.error(methodName, "DAO Exception occurred");
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw dbConne;
        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQL Exception occurred");
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(sqlE, "Failed to update active FS - " + sqlE.getMessage(), pttServerId,
                    KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to update active FS - " + e.getMessage(), pttServerId,
                    KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeStatement(pStmt);
            knLogger.debug(methodName, "EXIT: update active FS");
        }

    }

    /**
     * method to retrieve the Subscriber count
     *
     * @param persisterTxn KnPersisterTxn
     * @return int subscriber count
     * @throws KnDAOException DB Layer exception
     */
    public int getSubsCountforPAM(int pamAccId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubsCountforPAM(int, KnPersisterTxn)";
        int subscriberCount = -1;
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String query = null;

        knLogger.debug(methodName, "ENTRY: get Subscriber count  for PAM account");
        try {
            query = SUBS_CNT_PAM_QRY;

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pStmt = conn.prepareStatement(query);
            pStmt.setInt(1, pamAccId);
            knLogger.debug(methodName, "Query: Executing - ", query);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Query: Executed ");

            while (rs.next()) {
                subscriberCount = rs.getInt(1);
            }


        } catch (KnDAOException dbConne) {
            knLogger.error(methodName, "DAO Exception occurred");

            throw dbConne;
        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQL Exception occurred");

            throw KnDbUtil.processException(sqlE, "Failed to get Subscriber count  for PAM account - " + sqlE.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);

            throw KnDbUtil.processException(e, "Failed to get Subscriber count  for PAM account - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            knLogger.debug(methodName, "EXIT : get Subscriber count for PAM account ");
        }
        return subscriberCount;
    }

    /**
     * Update the service auth status of the Subscriber
     *
     * @param subsProfilePersistDTO KnBulkSubsProfilePersistDTO
     * @param persisterTxn          KnPersisterTxn
     * @throws KnDAOException DB Layer exception
     */
    public void updateServiceAuthStatus(KnBulkSubsProfilePersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateServiceAuthStatus(KnBulkSubsProfilePersistDTO, KnPersisterTxn)";
        Connection conn;
        PreparedStatement pStmt = null;
        String query = null;
        knLogger.debug(methodName, "ENTRY: update Service Auth Status with DTO -", subsProfilePersistDTO);
        try {

            List<String> mdns = subsProfilePersistDTO.getMdns();
            KnSubsProfileDTO subsProfileDTO = subsProfilePersistDTO.getSubsProfile();

            int serviceAuthStatus = subsProfileDTO.getServiceAuthStatus();
            String clientPassword = subsProfileDTO.getClientPassword();
            String userAgent = subsProfileDTO.getUserAgent();
            long lastProfileUpdateTime = subsProfilePersistDTO.getLastProfileUpdateTime();
            int serviceStatusOp = subsProfileDTO.getServiceStatusOp();

            knLogger.debug(methodName, "", mdns, "serviceAuthStatus:", serviceAuthStatus, "lastProfileUpdateTime :", lastProfileUpdateTime);

            StringBuilder queryBuffer = new StringBuilder();
            queryBuffer.append(UPDATE_QRY);
            queryBuffer.append(SERVICE_AUTH_STATUS).append("=?, ");
            queryBuffer.append(CLIENT_PASSWORD).append("=?, ");
            queryBuffer.append(USER_AGENT).append("=?, ");
            queryBuffer.append(LAST_PROFILE_UPDATE_TIME).append("=?, ");
            queryBuffer.append(SERVICE_STATUS_OP).append("=? ");
            queryBuffer.append("WHERE ").append(MDN).append("=?");
            query = queryBuffer.toString();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);

            for (String mdn : mdns) {
                pStmt.setInt(1, serviceAuthStatus);
                if (clientPassword == null) {
                    pStmt.setNull(2, java.sql.Types.CHAR);
                } else {
                    pStmt.setString(2, clientPassword);
                }
                if (userAgent == null) {
                    pStmt.setNull(3, java.sql.Types.CHAR);
                } else {
                    pStmt.setString(3, userAgent);
                }

                pStmt.setLong(4, lastProfileUpdateTime);
                pStmt.setInt(5,serviceStatusOp);
                pStmt.setString(6, mdn);
                pStmt.addBatch();
            }

            knLogger.debug(methodName, "QUERY: Executing the query - ", query);
            int[] count = pStmt.executeBatch();
            knLogger.debug(methodName, "QUERY: Executed  count:", count.length);
            knLogger.debug(methodName, "EXIT: update Service Auth Status");

        } catch (KnDAOException dbConne) {
            knLogger.error(methodName, "DAO Exception occurred");

            throw dbConne;
        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQL Exception occurred");

            throw KnDbUtil.processException(sqlE, "Failed to update Service Auth Status - " + sqlE.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);

            throw KnDbUtil.processException(e, "Failed to update Service Auth Status - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeStatement(pStmt);
        }
    }

    /**
     * method to retrieve the Subscriber count
     *
     * @param persisterTxn KnPersisterTxn
     * @return List<String> subscriber mdns
     * @throws KnDAOException DB Layer exception
     */
    public List<String> getPAMAccountMdns(int pamAccId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getPAMAccountMdns(int, KnPersisterTxn)";
        List<String> mdns = new LinkedList<String>();
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String query = null;
        knLogger.debug(methodName, "ENTRY: get PAM Account MDNs");
        try {
            query = PAM_ACCOUNT_MDNS_QRY;

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pStmt = conn.prepareStatement(query);
            pStmt.setInt(1, pamAccId);
            knLogger.debug(methodName, "Query: Executing - ", query);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Query: Executed ");


            while (rs.next()) {
                mdns.add(rs.getString(1).trim());
            }

        } catch (KnDAOException dbConne) {
            knLogger.error(methodName, "DAO Exception occurred");

            throw dbConne;
        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQL Exception occurred");

            throw KnDbUtil.processException(sqlE, "Failed to get PAM Account MDNs- " + sqlE.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);

            throw KnDbUtil.processException(e, "Failed to get PAM Account MDNs " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            knLogger.debug(methodName, "EXIT : get PAM Account MDNs");
        }
        return mdns;
    }

    public List<String> getPAMAccountMdnsByInsertionTime(int pamAccId, long insertionTime, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getPAMAccountMdnsByInsertionTime(int, KnPersisterTxn)";
        List<String> mdns = new LinkedList<String>();
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        knLogger.info(methodName, "pamAccId-", pamAccId, "insertionTime", insertionTime);
        try {

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pStmt = conn.prepareStatement(GET_PAM_ACCOUNT_MDNS_BY_INSERTION_TIME_QRY);
            pStmt.setInt(1, pamAccId);
            pStmt.setLong(2, insertionTime);
            knLogger.debug(methodName, "Query: Executing - ", GET_PAM_ACCOUNT_MDNS_BY_INSERTION_TIME_QRY);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Query: Executed ");

            while (rs.next()) {
                mdns.add(rs.getString(1).trim());
            }

        }  catch (SQLException sqlE) {
            knLogger.error(methodName, "SQL Exception occurred", sqlE);
            throw KnDbUtil.processException(sqlE, "Failed to get PAM Account MDNs- " + sqlE.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO,
            		GET_PAM_ACCOUNT_MDNS_BY_INSERTION_TIME_QRY);

        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
        }

        knLogger.exit(methodName, "MDN size -", mdns.size());
        return mdns;
    }

    /**
     * method to retrieve PAM Account MDN Details(list of Pseudomdn & its service auth status)
     *
     * @param persisterTxn KnPersisterTxn
     * @return List<KnOPSubsProfileInfoDTO> (contains mdn and Service Auth status)
     * @throws KnDAOException DB Layer exception
     */
    public List<KnOPSubsProfileInfoDTO> getPAMAccountMdnsDetails(int pamAccId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getPAMAccountMdnsDetails(int, KnPersisterTxn)";
        List<KnOPSubsProfileInfoDTO> pamProfileDetails = new LinkedList<KnOPSubsProfileInfoDTO>();
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String query = null;
        KnOPSubsProfileInfoDTO subsProfileInfoDTO = null;
        knLogger.debug(methodName, "ENTRY: get PAM Account MDNs");
        try {
            query = PAM_ACCOUNT_MDNDETAILS_QRY;

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pStmt = conn.prepareStatement(query);
            pStmt.setInt(1, pamAccId);
            knLogger.debug(methodName, "Query: Executing - ", query);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Query: Executed ");

            while (rs.next()) {
                subsProfileInfoDTO = new KnOPSubsProfileInfoDTO();
                subsProfileInfoDTO.setMdn(rs.getString(1).trim());
                subsProfileInfoDTO.setServiceAuthStatus(rs.getInt(2));
                pamProfileDetails.add(subsProfileInfoDTO);
            }

        } catch (KnDAOException dbConne) {
            knLogger.error(methodName, "DAO Exception occurred");
            throw dbConne;
        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQL Exception occurred");

            throw KnDbUtil.processException(sqlE, "Failed to get PAM Account MDNs- " + sqlE.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);

            throw KnDbUtil.processException(e, "Failed to get PAM Account MDNs " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            knLogger.debug(methodName, "EXIT : get PAM Account PRofile Details");
        }
        return pamProfileDetails;
    }

    /**
     * method to retrieve the Subscriber count
     *
     * @param listMdn
     * @param persisterTxn KnPersisterTxn
     * @return List<String> subscriber mdns
     * @throws KnDAOException DB Layer exception
     */
    public List<String> getPAMAccountMdns(int pamAccId, int fetchSize, String listMdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getPAMAccountMdns(int, KnPersisterTxn)";
        List<String> mdns = new LinkedList<String>();
        Connection conn;
        Statement stmt = null;
        ResultSet rs = null;
        String query = null;

        knLogger.debug(methodName, "ENTRY: get PAM Account MDNs");
        try {
            StringBuilder strQuery = new StringBuilder(200);
            if (fetchSize == 0) {
                strQuery.append("SELECT ").append(" ").append(MDN).append(" FROM ").append(TABLENAME);
            } else {
                strQuery.append("SELECT FIRST ").append(fetchSize).append(" ").append(MDN).append(" FROM ").append(TABLENAME);
            }
            strQuery.append(" WHERE ").append(PAMACC_ID).append(" = ").append(pamAccId).append(" AND ").
                    append(MDN).append(" > '").append(listMdn).append("' ORDER BY ").append(MDN);

            query = strQuery.toString();

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);

            stmt = conn.createStatement();
            knLogger.debug(methodName, "Query: Executing - ", query);
            rs = stmt.executeQuery(query);
            knLogger.debug(methodName, "Query: Executed ");

            while (rs.next()) {
                mdns.add(rs.getString(1).trim());
            }


        } catch (KnDAOException dbConne) {
            knLogger.error(methodName, "DAO Exception occurred");
            throw dbConne;
        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQL Exception occurred");
            throw KnDbUtil.processException(sqlE, "Failed to get PAM Account MDNs- " + sqlE.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to get PAM Account MDNs " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            knLogger.debug(methodName, "EXIT : get PAM Account MDNs");
        }
        return mdns;
    }

    /**
     * @param pamAccId
     * @param startMdn
     * @param endMdn
     * @param fetchSize
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public List<String> getPAMAccountMdns(int pamAccId, String startMdn, String endMdn, int fetchSize, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getPAMAccountMdns(int, String, String, int, KnPersisterTxn)";
        List<String> mdns = new LinkedList<String>();
        Connection conn;
        Statement stmt = null;
        ResultSet rs = null;
        String query = null;

        knLogger.debug(methodName, "ENTRY: get PAM Account MDNs");
        try {

            StringBuilder strQuery = new StringBuilder(200);
            if (fetchSize == 0) {
                strQuery.append("SELECT ").append(MDN).append(" FROM ").append(TABLENAME);
            } else {
                strQuery.append("SELECT FIRST ").append(fetchSize).append(" ").append(MDN).append(" FROM ").append(TABLENAME);

            }
            strQuery.append(" WHERE ").append(PAMACC_ID).append(" = ").append(pamAccId).append(" AND ").
                    append(MDN).append(" BETWEEN '").append(startMdn).append("' AND '").append(endMdn).append("' ORDER BY ").append(MDN);
            query = strQuery.toString();

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);

            stmt = conn.createStatement();
            knLogger.debug(methodName, "Query: Executing - ", query);
            rs = stmt.executeQuery(query);
            knLogger.debug(methodName, "Query: Executed ");

            while (rs.next()) {
                mdns.add(rs.getString(1).trim());
            }


        } catch (KnDAOException dbConne) {
            knLogger.error(methodName, "DAO Exception occurred");

            throw dbConne;
        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQL Exception occurred");

            throw KnDbUtil.processException(sqlE, "Failed to get PAM Account MDNs- " + sqlE.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);

            throw KnDbUtil.processException(e, "Failed to get PAM Account MDNs " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            knLogger.debug(methodName, "EXIT : get PAM Account MDNs");
        }
        return mdns;
    }

    /**
     * Update the service auth status of the Subscriber
     *
     * @param persisterTxn KnPersisterTxn
     * @throws KnDAOException DB Layer exception
     */
    public void updateLastProfileUpdateTimeForPamAccId(int pamAccId, long lastProfileUpdatetime, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateLastProfileUpdateTimeForPamAccId(int, KnPersisterTxn)";
        boolean ownedTxn = false;
        Connection conn;
        PreparedStatement pStmt = null;
        String query = null;
        knLogger.debug(methodName, "ENTRY: Update LastProfileUpdateTime with pamAccId -", pamAccId);
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }


            StringBuilder queryBuffer = new StringBuilder();
            queryBuffer.append(UPDATE_QRY);
            queryBuffer.append(LAST_PROFILE_UPDATE_TIME).append("=? ");
            queryBuffer.append("WHERE ").append(PAMACC_ID).append("=?");

            query = queryBuffer.toString();

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);
            pStmt.setLong(1, lastProfileUpdatetime);
            pStmt.setInt(2, pamAccId);

            knLogger.debug(methodName, "QUERY: Executing the query - ", query);
            pStmt.executeUpdate();
            knLogger.debug(methodName, "QUERY: Executed ");

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
        } catch (KnDAOException dbConne) {
            knLogger.error(methodName, "DAO Exception occurred");
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw dbConne;
        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQL Exception occurred");
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(sqlE, "Failed to update Last profile Update Time - " + sqlE.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to update Last profile Update Time - " + e.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeStatement(pStmt);
            knLogger.debug(methodName, "EXIT: update Last profile Update Time");
        }
    }


    /**
     * method to retrieve the Subscriber Profile
     *
     * @param mdns       List
     * @param persistTxn KnPersisterTxn
     * @return KnOPSubsProfileInfoDTO
     * @throws KnDAOException DB Layer Exception
     */
    public Map<String, KnOPSubsProfileInfoDTO> retrieveNotificationDetails4mdns(List<String> mdns, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "retrieveNotificationDetails4mdns(IPersistenceDTO, KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        Map<String, KnOPSubsProfileInfoDTO> resultMap = new HashMap<String, KnOPSubsProfileInfoDTO>(50);
        boolean ownedTxn = false;
        knLogger.entry(methodName, mdns);
        try {
            if (persistTxn == null) {
                persistTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the transaction ");
                persistTxn.open();
                ownedTxn = true;
            }


            if (ownedTxn) {
                conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
                knLogger.debug(methodName, "select...>>>SubscriberProfile ... ");
                //conn = persistTxn.getDBConnection(pttServerId, true);
            } else {
                conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
                //conn = persistTxn.getDBConnection(pttServerId, false);
                knLogger.debug(methodName, "selectSubscriberProfile ... ");
            }
            for (String mdn : mdns) {
                KnOPSubsProfileInfoDTO subsProfileInfoDto = new KnOPSubsProfileInfoDTO();
                subsProfileInfoDto.setMdn(mdn);
                pStmt = conn.prepareStatement(NOTIFICATION_4_MDNS_QRY);
                pStmt.setString(1, mdn);

                knLogger.debug(methodName, "Query: Executing - ", NOTIFICATION_4_MDNS_QRY, ", mdn - ", KnGDPRTemplate.mdn(mdn));
                rs = pStmt.executeQuery();
                knLogger.debug(methodName, "Query: Executed ");


                if (rs.next()) {
                    subsProfileInfoDto.setPoCHome(rs.getString(1));
                    subsProfileInfoDto.setPresenceHome(rs.getString(2));
                    subsProfileInfoDto.setXDMSHome(rs.getString(3));
                    subsProfileInfoDto.setLastProfileUpdateTime(rs.getLong(4));
                    subsProfileInfoDto.setClientPVmajorVer(rs.getInt(5));
                    subsProfileInfoDto.setClientPVminorVer(rs.getInt(6));

                } else {
                    knLogger.error(methodName, "Subscriber Profile doesn't exist");
                    if (ownedTxn) {
                        persistTxn.rollback();
                    }
                    throw new KnDBPersistenceException(KnErrorCodes.DAO.ROW_NOT_FOUND, "Subscriber Profile does not exist",
                            pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
                }

                resultMap.put(mdn, subsProfileInfoDto);
            }

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the Transaction");
                persistTxn.save();
            }
            knLogger.exit(methodName, resultMap);
            return resultMap;

        } catch (KnDAOException dbConne) {
            knLogger.error(methodName, "DAO Exception occurred", dbConne);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw dbConne;
        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQL Exception occurred", sqlE);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(sqlE, "Failed to select Subscriber Profile - " + sqlE.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to select Subscriber Profile - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePrepareStmt(pStmt);
            knLogger.exit(methodName);
        }
    }

    /**
     * method to retrieve the Subscriber count
     *
     * @param persisterTxn KnPersisterTxn
     * @return List<String> subscriber mdns
     * @throws KnDAOException DB Layer exception
     */
    public List<String> getPAMAccountProvMdns(int pamAccId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getPAMAccountProvMdns(int, KnPersisterTxn)";
        List<String> mdns = new LinkedList<String>();
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String query = null;
        knLogger.entry(methodName, pamAccId, persisterTxn);
        try {
            query = PAM_ACCOUNT_PROV_MDNS_QRY;

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pStmt = conn.prepareStatement(query);
            pStmt.setInt(1, pamAccId);
            knLogger.debug(methodName, "Query: Executing - ", query);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Query: Executed ");


            while (rs.next()) {
                mdns.add(rs.getString(1).trim());
            }

        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQL Exception occurred");

            throw KnDbUtil.processException(sqlE, "Failed to get PAM Account MDNs- " + sqlE.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);

            throw KnDbUtil.processException(e, "Failed to get PAM Account MDNs " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePrepareStmt(pStmt);
        }
        knLogger.exit(methodName, KnGDPRTemplate.mdnList(mdns));
        return mdns;
    }

    /**
     * method to retrieve the Subscriber count
     *
     * @param persisterTxn KnPersisterTxn
     * @return List<String> subscriber mdns
     * @throws KnDAOException DB Layer exception
     */
    public List<String> getPamAccLastSequenceMdns(int pamAccId, int start, int end, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getPamAccLastSequenceMdns(int, int, int,  KnPersisterTxn)";
        List<String> mdns = new LinkedList<String>();
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String query = null;
        knLogger.entry(methodName, pamAccId, start, end, persisterTxn);
        try {
            query = PAM_ACCOUNT_LAST_SEQ_MDNS_QRY;

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pStmt = conn.prepareStatement(query);
            pStmt.setInt(1, start);
            pStmt.setInt(2, end);
            pStmt.setInt(3, pamAccId);
            knLogger.debug(methodName, "Query: Executing - ", query);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Query: Executed ");


            while (rs.next()) {
                mdns.add(rs.getString(1).trim());
            }

        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQL Exception occurred");

            throw KnDbUtil.processException(sqlE, "Failed to get PAM Account MDNs- " + sqlE.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);

            throw KnDbUtil.processException(e, "Failed to get PAM Account MDNs " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePrepareStmt(pStmt);
        }
        knLogger.exit(methodName, KnGDPRTemplate.mdnList(mdns));
        return mdns;
    }

    public void updateAccountIdForSubscr(String accountId, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateAccountIdForSubscr(String,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: Update AccountId for Subscribers with AccountId - ", accountId, " & corpId - ", corpId);
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        try {
            query = QRY_UPDATE_ACCOUNTID;
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);
            pStmt.setString(1, accountId);
            pStmt.setInt(2, corpId);
            knLogger.debug(methodName, "Query: Executing - ", query);
            int countRowUpdated = pStmt.executeUpdate();
            knLogger.debug(methodName, "Query: Executed with row updated - ", countRowUpdated);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to get PAM Account MDNs " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closePrepareStmt(pStmt);
        }
        knLogger.debug(methodName, "EXIT: AccountId for subscribers updated successfully - ");
    }

    public void updateBulkSubscrCorpId(KnBulkSubsProfilePersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException{
        final String methodName="updateBulkSubscrCorpId(KnBulkSubsProfilePersistDTO)";
        knLogger.debug(methodName,"ENTRY:subsProfilePersistDTO",subsProfilePersistDTO);

        Connection conn = null;
        PreparedStatement pStmt = null;
        String query = null;
        List<String> mdnList=subsProfilePersistDTO.getMdns();
        int corpId=subsProfilePersistDTO.getSubsProfile().getCorpId();
        String accountId=subsProfilePersistDTO.getSubsProfile().getAccountId();
        knLogger.debug(methodName,"mdnList",KnGDPRTemplate.mdnList(mdnList));
        knLogger.debug(methodName,"corpId",corpId);

        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = UPDATE_SUBSCR_CORPID.replaceAll(MDN_LIST, KnGeneralUtil.getComSepList(mdnList));
            knLogger.debug(methodName, "QUERY: - ",query);
            pStmt = conn.prepareStatement(query);
            pStmt.setInt(1,corpId);
            pStmt.setString(2,accountId);

            knLogger.debug(methodName, "QUERY: Executing the Query - ", query);
            int result=pStmt.executeUpdate();
            knLogger.debug(methodName, "Query: Executed ",result);

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to update BulkSubscribercorpid- " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closePrepareStmt(pStmt);
        }
        knLogger.debug(methodName, "EXIT: ");
    }

    public void updateLastProfileUpdateTime(KnBulkSubsProfilePersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName="updateLastProfileUpdateTime(KnBulkSubsProfilePersistDTO)";
        knLogger.debug(methodName,"ENTRY:subsProfilePersistDTO",subsProfilePersistDTO);
        Connection conn;
        PreparedStatement pStmt = null;
        String query = null;
        knLogger.debug(methodName, "ENTRY: Update LastProfileUpdateTime with DTO -", subsProfilePersistDTO);
        try {


            long lastProfileUpdateTime = subsProfilePersistDTO.getLastProfileUpdateTime();
            List<String> mdnList=subsProfilePersistDTO.getMdns();

            query = UPDATE_BULK_SUBSCR_LASTPROFILETIME.replaceAll(MDN_LIST, KnGeneralUtil.getComSepList(mdnList));

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);
            pStmt.setLong(1, lastProfileUpdateTime);


            knLogger.debug(methodName, "QUERY: Executing the query - ", query);
            pStmt.executeUpdate();
            knLogger.debug(methodName, "QUERY: Executed ");


        } catch (KnDAOException dbConne) {
            knLogger.error(methodName, "DAO Exception occurred");

            throw dbConne;
        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQL Exception occurred");

            throw KnDbUtil.processException(sqlE, "Failed to update Last profile Update Time - " + sqlE.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);

            throw KnDbUtil.processException(e, "Failed to update Last profile Update Time - " + e.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closePrepareStmt(pStmt);
        }
        knLogger.debug(methodName, "EXIT: update Last profile Update Time");
    }

    public boolean isMDNExist(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "isMDNExist(String, KnPersisterTxn)";
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        boolean isExist=false;

        knLogger.entry(methodName, "checking for MDN-", KnGDPRTemplate.mdn(mdn));
        try {

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pStmt = conn.prepareStatement(SELECT_MDN_QUERY);
            pStmt.setString(1, mdn);
            knLogger.debug(methodName, "Query: Executing - ", SELECT_MDN_QUERY);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Query: Executed ");

            while (rs.next()) {
               isExist = true;
            }

        }  catch (SQLException sqlE) {
            knLogger.error(methodName, SQL_EXCEPTION_MSG, sqlE);
            throw KnDbUtil.processException(sqlE, "Failed to retrieve Subscriber  - " + sqlE.getMessage(),
            		pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, SELECT_MDN_QUERY);

        }  finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePrepareStmt(pStmt);
        }
        knLogger.exit(methodName, isExist);
        return isExist;
    }

    /**
     * Update the CORPCONTACTLISTID for the the Subscriber
     *
     * @param persisterTxn          KnPersisterTxn
     * @throws KnDAOException DB Layer exception
     */
    public void updateSubsContactListID(int contactListID, String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateSubsContactListID(int, KnPersisterTxn)";
        Connection conn;
        PreparedStatement pStmt = null;
        knLogger.entry(methodName,  contactListID, KnGDPRTemplate.mdn(mdn));
        try {

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(UPDATE_CONTACT_LIST_ID_QUERY);

            if (contactListID == 0 ) {
            	pStmt.setNull(1, java.sql.Types.INTEGER);
            } else {
            	pStmt.setInt(1, contactListID);
            }

            pStmt.setString(2, mdn);

            knLogger.debug(methodName, "QUERY: Executing the query - ", UPDATE_CONTACT_LIST_ID_QUERY);
            pStmt.executeUpdate();
            knLogger.debug(methodName, "QUERY: Executed ");

        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQL Exception occurred");
            throw KnDbUtil.processException(sqlE, "Failed to update Service Auth Status - " + sqlE.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO,
            		UPDATE_CONTACT_LIST_ID_QUERY);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }
     }

    /**
     * method to retrieve the Subscriber Profile
     *
     * @param mdnList    LinkedList<String>
     * @param persistTxn KnPersisterTxn
     * @return KnOPSubsProfileInfoDTO
     * @throws KnDAOException DB Layer Exception
     */
    public KnOPSubsProfileInfoDTO selectSubsProfileInfo(List<String> mdnList, boolean readOnly, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "selectSubsProfileInfo(mdnList,boolean, KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        KnOPSubsProfileInfoDTO subsProfileInfoDto = new KnOPSubsProfileInfoDTO();
        int index = 1;
        knLogger.debug(methodName, "ENTRY: Select Subscriber Profile ", " readOnly :", readOnly);
        try {
            StringBuilder strBuffer = getSelectSubsProfileQuery();
            strBuffer.append(MDN).append(" IN ").append("(MDNLIST)");
            

            query = strBuffer.toString();
            query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(mdnList, query, "MDNLIST");
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            knLogger.debug(methodName, "selectSubscriberProfile ... ");
            pStmt = conn.prepareStatement(query);
            knLogger.debug(methodName, "Query: Executing - ", query);
            
            for(String mdn : mdnList)
            	pStmt.setString(index++, mdn);
            	
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Query: Executed ");
            Map<String, KnSubsProfileDTO> subsProfileDTOMap = new HashMap<>();
            Map<String, com.kodiak.xdms.server.common.dto.common.KnSubsProfileDTO> subsProfileCommonDTOMap = new HashMap<>();
            while (rs.next()) {
                KnSubsProfileDTO subsProfile = new KnSubsProfileDTO();
                com.kodiak.xdms.server.common.dto.common.KnSubsProfileDTO subsProfileDTOCommon = new com.kodiak.xdms.server.common.dto.common.KnSubsProfileDTO();
                getSelectSubsProfileResult(subsProfile, rs);
                getSelectSubsCommonProfileResult(subsProfileDTOCommon, rs);
                subsProfileDTOMap.put(rs.getString(MDN).trim(), subsProfile);
                subsProfileCommonDTOMap.put(rs.getString(MDN).trim(), subsProfileDTOCommon);
            }
            subsProfileInfoDto.setSubsRespMap(subsProfileDTOMap);
            subsProfileInfoDto.setSubsRespMapCommon(subsProfileCommonDTOMap);
            knLogger.debug(methodName, "returning subscriber Profile Info ", subsProfileInfoDto, subsProfileDTOMap.size());
            return subsProfileInfoDto;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to select Subscriber Profile - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePrepareStmt(pStmt);
            knLogger.debug(methodName, "EXIT : select Subscriber Profile");
        }
    }

    private void getSelectSubsCommonProfileResult(com.kodiak.xdms.server.common.dto.common.KnSubsProfileDTO subsProfileInfoDto, ResultSet rs) throws SQLException, UnsupportedEncodingException {
        subsProfileInfoDto.setMdn(rs.getString(MDN));
        subsProfileInfoDto.setPocHome(rs.getString(POC_HOME));
        subsProfileInfoDto.setPresenceHome(rs.getString(PRESENCE_HOME));
        subsProfileInfoDto.setXdmsHome(rs.getString(XDMS_HOME));
        subsProfileInfoDto.setServiceAuthStatus(rs.getInt(SERVICE_AUTH_STATUS));
        subsProfileInfoDto.setPublicSubscriptionType(rs.getInt(PUBLIC_SUBSCRIPTION_TYPE));
        subsProfileInfoDto.setCorporateSubscriptionType(rs.getInt(CORP_SUBSCRIPTION_TYPE));
        subsProfileInfoDto.setCorpId(rs.getInt(CORP_ID));
        int corpPairingInd = rs.getInt(CORP_CONTACT_PAIRING_IND);
        if (corpPairingInd == 1) {
            subsProfileInfoDto.setPairingIndicator(true);
        } else {
            subsProfileInfoDto.setPairingIndicator(false);
        }
        String imei = rs.getString(IMEI);
        if (imei != null) {
            imei = imei.trim();
        }
        subsProfileInfoDto.setIMEI(imei);
        subsProfileInfoDto.setAffiliateId(rs.getString(AFFILIATE_ID));
        subsProfileInfoDto.setPayType(rs.getInt(PAY_TYPE));
        subsProfileInfoDto.setSubsCreationTime(rs.getLong(SUBSCR_CREATION_TIME));
        subsProfileInfoDto.setLastProfileUpdateTime(rs.getLong(LAST_PROFILE_UPDATE_TIME));
        subsProfileInfoDto.setCorpContactListId(rs.getInt(CORPCONTACTLISTID));
        subsProfileInfoDto.setUserAgent(rs.getString(USER_AGENT));
        subsProfileInfoDto.setClientPassword(rs.getString(CLIENT_PASSWORD));
        subsProfileInfoDto.setEmailAddress(rs.getString(EMAIL));
        subsProfileInfoDto.setSubsClientType(rs.getInt(CLIENT_TYPE));
        subsProfileInfoDto.setDispatchGroupMember(rs.getInt(DISPATCH_GRP_MEMBER));
        subsProfileInfoDto.setClientPVmajorVer(rs.getInt(CLIENTPV_MAJORVERSION));
        subsProfileInfoDto.setClientPVminorVer(rs.getInt(CLIENTPV_MINORVERSION));
        subsProfileInfoDto.setAccountId(rs.getString(ACCOUNT_ID));
        subsProfileInfoDto.setPamAccId(rs.getInt(PAMACC_ID));
        subsProfileInfoDto.setVocoderId(rs.getInt(VOCODERID));
        subsProfileInfoDto.setHierarchyType(HIERARCHY_TYPE.validate(rs.getInt(ADDLINFO)));
        subsProfileInfoDto.setLastActivationTime(rs.getLong(LAST_ACTIVATION_TIME));
        subsProfileInfoDto.setDispatchType(rs.getInt(DISPATCH_TYPE));
        String derKey = rs.getString(DERIVED_KEY);
        if (derKey != null && !(derKey.isEmpty())){
            subsProfileInfoDto.setDerivedKey(KnGeneralUtil.convertAsciiToHex(derKey));
        }
        subsProfileInfoDto.setUserId(rs.getString(USER_ID));
        if (rs.getString(SUBSCR_NAME) != null)
            subsProfileInfoDto.setNetworkName(new String(rs.getString(SUBSCR_NAME).getBytes("8859_1"), "UTF-8"));
        subsProfileInfoDto.setUfmi(rs.getString(UFMI));
        subsProfileInfoDto.setiDenUserName(rs.getString(IDEN_USERNAME));
        subsProfileInfoDto.setiDenPassword(rs.getString(IDEN_PASSWORD));
        subsProfileInfoDto.setiDenBusUnitId(rs.getString(IDEN_BUSUNITID));
        subsProfileInfoDto.setPoCStatusOP(rs.getInt(SERVICE_STATUS_OP));
        subsProfileInfoDto.setPoCStatusAU(rs.getInt(SERVICE_STATUS_AUTHUSER));
        subsProfileInfoDto.setServiceStatusOp(rs.getInt(SERVICE_STATUS_OP));
        subsProfileInfoDto.setServiceStatusAuthUser(rs.getInt(SERVICE_STATUS_AUTHUSER));
        subsProfileInfoDto.setLicenseType(rs.getInt(LICENSE_TYPE));
        if (rs.getString(PREV_SERVICE_AUTH_STATUS) != null) {
            subsProfileInfoDto.setPreviousServiceAuthStatus(rs.getInt(PREV_SERVICE_AUTH_STATUS));
        }
        if(rs.getString(ALIAS_MDN) != null) subsProfileInfoDto.setAliasMdn(rs.getString(ALIAS_MDN).trim());
        subsProfileInfoDto.setQppPkgId(rs.getInt(QPPPACKID));
        subsProfileInfoDto.setFirstNetIndicator(rs.getString(SEGMENT_INDICATOR));
        subsProfileInfoDto.setMcpttCompliance(rs.getInt(MCPTT_COMPLIANCE));
        String subsFs2=rs.getString(SUBS_FS2)!=null?rs.getString(SUBS_FS2):KnGeneralUtil.convertLongToHexString(rs.getLong(SUBS_FS1));
        subsProfileInfoDto.setSubsFS2(subsFs2);
        String clientsFs2=rs.getString(CLIENT_FS2)!=null?rs.getString(CLIENT_FS2):KnGeneralUtil.convertLongToHexString(rs.getLong(CLIENT_FS1));
        subsProfileInfoDto.setClientFS2(clientsFs2);
        String activeFs2=rs.getString(ACTIVE_FS2)!=null?rs.getString(ACTIVE_FS2):KnGeneralUtil.convertLongToHexString(rs.getLong(ACTIVE_FS1));
        subsProfileInfoDto.setActiveFS2(activeFs2);
        String opsFs2=rs.getString(OPS_FS2)!=null?rs.getString(OPS_FS2):KnGeneralUtil.convertLongToHexString(rs.getLong(OPS_FS1));
        subsProfileInfoDto.setOpsFS2(opsFs2);
        String corpAdminFs2=rs.getString(CORPADMIN_FS2)!=null?rs.getString(CORPADMIN_FS2):KnGeneralUtil.convertLongToHexString(rs.getLong(CORPADMIN_FS1));
        subsProfileInfoDto.setCorpAdminFS2(corpAdminFs2);
        String xdmsFs1=rs.getString(XDMS_FS2)!=null?rs.getString(XDMS_FS2):KnGeneralUtil.convertLongToHexString(rs.getLong(XDMS_FS1));
        subsProfileInfoDto.setXdmsFS2(xdmsFs1);
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
        subsProfileInfoDto.setExtGatewayId(rs.getString(EXT_GATEWAY_ID));
        subsProfileInfoDto.setIsDefaultProfile(rs.getInt(ISDEFAULTPROFILE));
        subsProfileInfoDto.setFeatureRelVersion(rs.getString(FEATURE_REL_VERSION) != null ? rs.getString("FEATURE_REL_VERSION") : "0.0");
        subsProfileInfoDto.setSwType(rs.getInt(CLIENT_SW_INF));
        subsProfileInfoDto.setPlatformType(rs.getInt(CLIENT_PLATFORM_TYPE));
        subsProfileInfoDto.setDynamicQosFlag(rs.getInt(DYNAMIC_QOS_FLAG));
        if (rs.getString(USERPROFILEFS2) != null) {
            subsProfileInfoDto.setUserProfileFS2(rs.getString(USERPROFILEFS2));
        }
        if(rs.getString(USER_ID)!=null){
            subsProfileInfoDto.setUserId(rs.getString(USER_ID));
        }
        subsProfileInfoDto.setUserProfileId(rs.getString(USERPROFILEID));
        subsProfileInfoDto.setCameraType((Integer)rs.getObject(CAMERA_TYPE));
        subsProfileInfoDto.setPrivacyOptStatus(rs.getInt(PRIVACY_OPT_STATUS));
        knLogger.debug("subsProfileInfoDto::",subsProfileInfoDto);
    }

    public Collection<KnSubsProfileDTO> fetchSubsSpecificDetailsForBulkMdns(List<String> mdnList, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "fetchSubsSpecificDetailsForBulkMdns(List, boolean, KnPersisterTxn)";
        Collection<KnSubsProfileDTO> subsDto = new ArrayList<>();
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String query = null;
        KnSubsProfileDTO subsProfileDTO = null;
        knLogger.debug(methodName, "ENTRY:  MDNs in bulk ");
        try {
            StringBuffer keys = com.kodiak.common.dao.KnDbUtil.convertListToStringBuffer(mdnList);
            query = "SELECT MDN, CLIENT_TYPE, ACTIVEFS1, ACTIVEFS2 FROM " + TABLENAME + " WHERE MDN IN "+ keys.toString();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            pStmt = conn.prepareStatement(query);
            knLogger.debug(methodName, "Query: Executing - ", query);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Query: Executed ");
            while (rs.next()) {
                subsProfileDTO = new KnSubsProfileDTO();
                subsProfileDTO.setMdn(rs.getString(1).trim());
                subsProfileDTO.setSubsClientType(rs.getInt(2));
                String activeFS=rs.getString(4)!=null?rs.getString(4):KnGeneralUtil.convertLongToHexString(rs.getLong(3));
                subsProfileDTO.setActiveFS2(activeFS);
                subsDto.add(subsProfileDTO);
            }
            knLogger.debug(methodName, "subsDto :",subsDto);

        } catch (KnDAOException dbConne) {
            knLogger.error(methodName, "DAO Exception occurred");

            throw dbConne;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);

            throw KnDbUtil.processException(e, "Failed to get fetchSubsSpecificDetailsForBulkMdns for blk MDNs "
                    + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePrepareStmt(pStmt);
            knLogger.debug(methodName, "EXIT :fetchSubsSpecificDetailsForBulkMdns for Blk MDNs",subsDto);
        }
        return subsDto;
    }


    /**
     * method to update the Converged Client Profile.
     *
     * @param subsProfilePersistDTO KnSubsProfilePersistDTO
     * @param persisterTxn          KnPersisterTxn
     * @throws KnDAOException DB layer Exception
     */
    public void updateConvergedClientProfile(KnSubsProfilePersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateConvergedClientProfile(KnSubsProfilePersistDTO, KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        knLogger.debug(methodName, "ENTRY: Update Subscriber Profile with DTO - ", subsProfilePersistDTO);
        try {
            String mdn = subsProfilePersistDTO.getMdn();
            long lastProfileUpdateTime = subsProfilePersistDTO.getLastProfileUpdateTime();
            int subsClientType = subsProfilePersistDTO.getSubsClientType();
            String clientPassword = subsProfilePersistDTO.getClientPassword();

            StringBuilder queryBuffer = new StringBuilder();
            queryBuffer.append(UPDATE_QRY);
            queryBuffer.append(LAST_PROFILE_UPDATE_TIME).append("=?, ");
            queryBuffer.append(CLIENT_TYPE).append("=?, ");
            queryBuffer.append(CLIENT_PASSWORD).append("=?, ");
            queryBuffer.append(SUBS_FS1).append("=?, ");
            queryBuffer.append(CLIENT_FS1).append("=?, ");
            queryBuffer.append(ACTIVE_FS1).append("=?, ");
            queryBuffer.append(XDMS_FS1).append("=?, ");
            queryBuffer.append(SUBS_FS2).append("=?, ");
            queryBuffer.append(CLIENT_FS2).append("=?, ");
            queryBuffer.append(ACTIVE_FS2).append("=?, ");
            queryBuffer.append(XDMS_FS2).append("=? ");
            queryBuffer.append(" WHERE ").append(MDN).append("=?");

            query = queryBuffer.toString();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);
            int columnIndex = 0;

            pStmt.setLong(++columnIndex, lastProfileUpdateTime);
            pStmt.setInt(++columnIndex, subsClientType);
            if (clientPassword == null) {
                pStmt.setNull(++columnIndex, java.sql.Types.CHAR);
            } else {
                pStmt.setString(++columnIndex, clientPassword);
            }
            pStmt.setLong(++columnIndex, KnGeneralUtil.convertHexStringToLong(subsProfilePersistDTO.getSubsFS2()));
            pStmt.setLong(++columnIndex, KnGeneralUtil.convertHexStringToLong(subsProfilePersistDTO.getClientFS2()));
            pStmt.setLong(++columnIndex, KnGeneralUtil.convertHexStringToLong(subsProfilePersistDTO.getActiveFS2()));
            pStmt.setLong(++columnIndex, KnGeneralUtil.convertHexStringToLong(subsProfilePersistDTO.getXdmsFS2()));
            pStmt.setString(++columnIndex, KnGeneralUtil.getFeatureSet(subsProfilePersistDTO.getSubsFS2()));
            pStmt.setString(++columnIndex, KnGeneralUtil.getFeatureSet(subsProfilePersistDTO.getClientFS2()));
            pStmt.setString(++columnIndex, KnGeneralUtil.getFeatureSet(subsProfilePersistDTO.getActiveFS2()));
            pStmt.setString(++columnIndex, KnGeneralUtil.getFeatureSet(subsProfilePersistDTO.getXdmsFS2()));
            pStmt.setString(++columnIndex, mdn);

            knLogger.debug(methodName, "QUERY: Executing the Query - ", query, " DTO - ", subsProfilePersistDTO);
            pStmt.executeUpdate();
            knLogger.debug(methodName, "Query: Executed ");
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to update Subscriber Profile - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closePrepareStmt(pStmt);
            knLogger.debug(methodName, "EXIT: update Subscriber Profile");
        }
    }
    public void updateSubscriberUserID(KnSubsProfilePersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateSubscrUserID(KnSubsProfilePersistDTO, KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        knLogger.debug(methodName, "ENTRY: Update Subscriber User ID with DTO - ", subsProfilePersistDTO);
        try {
            String mdn = subsProfilePersistDTO.getOldMdn();
            StringBuilder queryBuffer = new StringBuilder();
            queryBuffer.append(UPDATE_QRY);
            queryBuffer.append(USER_ID).append("=? ,");
            queryBuffer.append(UFMI).append("=? ,");
            queryBuffer.append(ALIAS_MDN).append("=? ,");
            queryBuffer.append(MC_ID).append("=? ,");
            queryBuffer.append(MC_PTTID).append("=? ,");
            queryBuffer.append(MC_VIDEOID).append("=? ,");
            queryBuffer.append(MC_DATAID).append("=? ");
            queryBuffer.append(" WHERE ").append(MDN).append("=?");

            query = queryBuffer.toString();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);
            int columnIndex = 0;
            pStmt.setNull(++columnIndex, java.sql.Types.NVARCHAR);
            pStmt.setNull(++columnIndex, java.sql.Types.VARCHAR);
            pStmt.setNull(++columnIndex, java.sql.Types.VARCHAR);
            pStmt.setNull(++columnIndex, java.sql.Types.VARBINARY);
            pStmt.setNull(++columnIndex, java.sql.Types.VARBINARY);
            pStmt.setNull(++columnIndex, java.sql.Types.VARBINARY);
            pStmt.setNull(++columnIndex, java.sql.Types.VARBINARY);
            pStmt.setString(++columnIndex, mdn);

            knLogger.debug(methodName, "QUERY: Executing the Query - ", query, " DTO - ", subsProfilePersistDTO);
            pStmt.executeUpdate();
            knLogger.debug(methodName, "Query: Executed ");
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to update Subscriber Profile - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closePrepareStmt(pStmt);
            knLogger.debug(methodName, "EXIT: update Subscriber User ID");
        }
    }

    public KnOPSubsProfileInfoDTO selectSubscriberProfileForUFMI(String ufmi, KnPersisterTxn persistTxn) throws KnDAOException {
   	 String methodName = "selectSubscriberProfileForUFMI(String, KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        KnOPSubsProfileInfoDTO subsProfileInfoDto = null;
        knLogger.debug(methodName, "ENTRY: Select Subscriber Profile ");
        try {


            StringBuilder strBuffer = getSelectSubsProfileQuery();
            strBuffer.append(UFMI).append("=?");

            query = strBuffer.toString();


                conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
                //conn = persistTxn.getDBConnection(pttServerId, false);
                knLogger.debug(methodName, "selectSubscriberProfile ... ");
            pStmt = conn.prepareStatement(query);
            pStmt.setString(1, ufmi);

            knLogger.debug(methodName, "Query: Executing - ", query, ", ufmi - ", ufmi);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Query: Executed ");

            if (rs.next()) {
           	 subsProfileInfoDto=new KnOPSubsProfileInfoDTO();
           	getSelectSubsProfileResult(subsProfileInfoDto, rs);

            }


        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to select Subscriber Profile - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePrepareStmt(pStmt);
            knLogger.info(methodName, "EXIT : select Subscriber Profile");
        }

        return subsProfileInfoDto;

   }
public KnOPSubsProfileInfoDTO selectSubscriberProfileByUserId(String userId, KnPersisterTxn persistTxn)
			throws KnDAOException {
		String methodName = "selectSubscriberProfileByUserId(String, KnPersisterTxn)";
		String query = null;
		Connection conn;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		KnOPSubsProfileInfoDTO subsProfileInfoDto = null;
		knLogger.debug(methodName, "ENTRY: Select Subscriber Profile ");
		try {

			StringBuilder strBuffer = getSelectSubsProfileQuery();

			strBuffer.append(LOWER_USER_ID).append("=?");

			query = strBuffer.toString();

			conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
			// conn = persistTxn.getDBConnection(pttServerId, false);
			knLogger.debug(methodName, "selectSubscriberProfile ... ");
			pStmt = conn.prepareStatement(query);
			pStmt.setString(1, userId.toLowerCase());

			knLogger.debug(methodName, "Query: Executing - ", query, ", userId - ", userId.toLowerCase());
			rs = pStmt.executeQuery();
			knLogger.debug(methodName, "Query: Executed ");

			if (rs.next()) {
				subsProfileInfoDto = new KnOPSubsProfileInfoDTO();
				getSelectSubsProfileResult(subsProfileInfoDto, rs);

			} else {
                knLogger.error(methodName, "Subscriber Profile doesn't exist");
                throw new KnDBPersistenceException(KnErrorCodes.DAO.ROW_NOT_FOUND, "Subscriber Profile does not exist",
                        pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
            }

		}catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred");
            throw e;
        } catch (Exception e) {
			knLogger.error(methodName, "Unexpected Exception - ", e);
			throw KnDbUtil.processException(e, "Failed to select Subscriber Profile - " + e.getMessage(), pttServerId,
					KnProvDAOSourceTypes.POCSUBSCRINFO, query);
		} finally {
			KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePrepareStmt(pStmt);
			knLogger.info(methodName, "EXIT : select Subscriber Profile");
		}

		return subsProfileInfoDto;
	}

	public KnOPSubsProfileInfoDTO selectSubscriberProfileByAliasMdn(String aliasMdn, KnPersisterTxn persistTxn)
			throws KnDAOException {
		String methodName = "selectSubscriberProfileByAliasMdn(String, KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        KnOPSubsProfileInfoDTO subsProfileInfoDto = null;
        knLogger.debug(methodName, "ENTRY: Select Subscriber Profile ");
        try {

			StringBuilder strBuffer = getSelectSubsProfileQuery();

			strBuffer.append(ALIAS_MDN).append("=?");

			query = strBuffer.toString();

			conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
			// conn = persistTxn.getDBConnection(pttServerId, false);
			knLogger.debug(methodName, "selectSubscriberProfile ... ");
			pStmt = conn.prepareStatement(query);
			pStmt.setString(1, aliasMdn);

			knLogger.debug(methodName, "Query: Executing - ", query, ", aliasMdn - ", KnGDPRTemplate.mdn(aliasMdn));
			rs = pStmt.executeQuery();
			knLogger.debug(methodName, "Query: Executed ");

			if (rs.next()) {
				subsProfileInfoDto = new KnOPSubsProfileInfoDTO();
				getSelectSubsProfileResult(subsProfileInfoDto, rs);

			} else {
                knLogger.info(methodName, "Subscriber Profile doesn't exist");
                throw new KnDBPersistenceException(KnErrorCodes.DAO.ROW_NOT_FOUND, "Subscriber Profile does not exist",
                        pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
            }

		}catch (KnDAOException e) {
          //  knLogger.error(methodName, "DAO Exception occurred");
            throw e;
        } catch (Exception e) {
			knLogger.error(methodName, "Unexpected Exception - ", e);
			throw KnDbUtil.processException(e, "Failed to select Subscriber Profile - " + e.getMessage(), pttServerId,
					KnProvDAOSourceTypes.POCSUBSCRINFO, query);
		} finally {
			KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePrepareStmt(pStmt);
			knLogger.info(methodName, "EXIT : select Subscriber Profile");
		}

		return subsProfileInfoDto;
	}

    public KnOPSubsProfileInfoDTO selectSubscriberProfileByMcpttId(String mcpttId, KnPersisterTxn persistTxn)
			throws KnDAOException {

		String methodName = "selectSubscriberProfileByMcpttId(String, KnPersisterTxn)";
		String query = null;
		Connection conn;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		KnOPSubsProfileInfoDTO subsProfileInfoDto = null;
		knLogger.debug(methodName, "ENTRY: Select Subscriber Profile ");

		try {

			StringBuilder strBuffer = getSelectSubsProfileQuery();

			strBuffer.append(MC_PTTID).append("=?");

			query = strBuffer.toString();

			conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false); // conn =
			persistTxn.getDBConnection(pttServerId, false);
			knLogger.debug(methodName, "selectSubscriberProfile ... ");
			pStmt = conn.prepareStatement(query);
			pStmt.setBytes(1, mcpttId.getBytes(StandardCharsets.UTF_8));

			knLogger.debug(methodName, "Query: Executing - ", query, ", mcpttId - ", KnGDPRTemplate.mcpttId(mcpttId));
			rs = pStmt.executeQuery();
			knLogger.debug(methodName, "Query: Executed ");

			if (rs.next()) {
				subsProfileInfoDto = new KnOPSubsProfileInfoDTO();
				getSelectSubsProfileResult(subsProfileInfoDto, rs);

			} else {
				knLogger.error(methodName, "Subscriber Profile doesn't exist");
				throw new KnDBPersistenceException(KnErrorCodes.DAO.ROW_NOT_FOUND, "Subscriber Profile does not exist",
						pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
			}

		} catch (KnDAOException e) {
			knLogger.error(methodName, "DAO Exception occurred");
			throw e;
		} catch (Exception e) {
			knLogger.error(methodName, "Unexpected Exception - ", e);
			throw KnDbUtil.processException(e, "Failed to select Subscriber Profile - " + e.getMessage(), pttServerId,
					KnProvDAOSourceTypes.POCSUBSCRINFO, query);
		} finally {
			KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePrepareStmt(pStmt);
			knLogger.info(methodName, "EXIT : select Subscriber Profile");
		}

		return subsProfileInfoDto;

	}

	private StringBuilder getSelectSubsProfileQuery() {
		StringBuilder strBuffer = new StringBuilder();
		strBuffer.append("SELECT ");
		strBuffer.append(MDN).append(", ").append(POC_HOME).append(", ").append(PRESENCE_HOME).append(", ")
		.append(XDMS_HOME).append(", ").append(SERVICE_AUTH_STATUS).append(", ")
		.append(PUBLIC_SUBSCRIPTION_TYPE).append(", ").append(CORP_SUBSCRIPTION_TYPE).append(", ")
		.append(CORP_ID).append(", ").append(CORP_CONTACT_PAIRING_IND).append(", ").append(IMEI).append(", ")
		.append(AFFILIATE_ID).append(", ").append(PAY_TYPE).append(", ").append(SUBSCR_CREATION_TIME)
		.append(", ").append(LAST_PROFILE_UPDATE_TIME).append(", ").append(CORPCONTACTLISTID).append(", ")
		.append(USER_AGENT).append(", ").append(CLIENT_PASSWORD).append(", ").append(EMAIL).append(", ")
		.append(CLIENT_TYPE).append(", ").append(DISPATCH_GRP_MEMBER).append(", ").append(SUBS_FS1).append(", ")
		.append(CLIENT_FS1).append(", ").append(ACTIVE_FS1).append(", ").append(OPS_FS1).append(", ")
		.append(CLIENTPV_MAJORVERSION).append(", ").append(CLIENTPV_MINORVERSION).append(", ")
		.append(ACCOUNT_ID).append(", ").append(PAMACC_ID).append(",").append(CORPADMIN_FS1).append(" , ")
		.append(VOCODERID).append(" , ").append(ADDLINFO).append(" , ").append(LAST_ACTIVATION_TIME)
		.append(" , ").append(DISPATCH_TYPE).append(" , ").append(USER_ID).append(" , ").append(DERIVED_KEY).append(" , ").append(SUBSCR_NAME)
		.append(" , ").append(UFMI).append(" , ").append(IDEN_USERNAME).append(" , ").append(IDEN_PASSWORD)
		.append(" , ").append(IDEN_BUSUNITID).append(" , ").append(SERVICE_STATUS_OP).append(" , ")
		.append(SERVICE_STATUS_AUTHUSER).append(" , ").append(XDMS_FS1).append(" , ").append(LICENSE_TYPE)
		.append(" , ").append(ALIAS_MDN).append(" , ").append(QPPPACKID).append(" , ").append(SEGMENT_INDICATOR)
        .append(" , ").append(MCPTT_COMPLIANCE).append(" , ").append(SUBS_FS2).append(", ")
		.append(CLIENT_FS2).append(", ").append(ACTIVE_FS2).append(", ").append(OPS_FS2).append(",").append(CORPADMIN_FS2)
		.append(" , ").append(XDMS_FS2).append(" , ").append(MC_ID).append(" , ").append(MC_PTTID).append(" , ")
		.append(MC_VIDEOID).append(" , ").append(MC_DATAID).append(" , ").append(USERPROFILEINDEX).append(" , ").append(ISDEFAULTPROFILE).
         append(" , ").append(FEATURE_REL_VERSION).append(" , ").append(CLIENT_SW_INF).append(" , ").append(CLIENT_PLATFORM_TYPE).append(" , ").append(DYNAMIC_QOS_FLAG)
         .append(" , ").append(DERIVEDKEY).append(" , ").append(USERPROFILEFS2).append(" , ").append(USERPROFILEID).append(" , ").append(CAMERA_TYPE)
         .append(" , ").append(PRIVACY_OPT_STATUS).append(" , ").append(PREV_SERVICE_AUTH_STATUS).append(" , ").append(EXT_GATEWAY_ID).append(" , ").append(CLUSTERID).append(" , ").append(HIERARCHY_ID).append(",").append(HIERARCHY_ROOT);
		strBuffer.append(" FROM ").append(TABLENAME).append(" WHERE ");

		return strBuffer;

	}

    private StringBuilder getSelectBulkSubsProfileQuery() {
        StringBuilder strBuffer = new StringBuilder();
        strBuffer.append("SELECT ");
        strBuffer.append(MDN).append(", ").append(POC_HOME).append(", ").append(PRESENCE_HOME).append(", ")
                .append(XDMS_HOME).append(", ").append(SERVICE_AUTH_STATUS).append(", ")
                .append(PUBLIC_SUBSCRIPTION_TYPE).append(", ").append(CORP_SUBSCRIPTION_TYPE).append(", ")
                .append(CORP_ID).append(", ").append(CORP_CONTACT_PAIRING_IND).append(", ").append(IMEI).append(", ")
                .append(AFFILIATE_ID).append(", ").append(PAY_TYPE).append(", ").append(SUBSCR_CREATION_TIME)
                .append(", ").append(LAST_PROFILE_UPDATE_TIME).append(", ").append(CORPCONTACTLISTID).append(", ")
                .append(USER_AGENT).append(", ").append(CLIENT_PASSWORD).append(", ").append(EMAIL).append(", ")
                .append(CLIENT_TYPE).append(", ").append(DISPATCH_GRP_MEMBER).append(", ").append(SUBS_FS1).append(", ")
                .append(CLIENT_FS1).append(", ").append(ACTIVE_FS1).append(", ").append(OPS_FS1).append(", ")
                .append(CLIENTPV_MAJORVERSION).append(", ").append(CLIENTPV_MINORVERSION).append(", ")
                .append(ACCOUNT_ID).append(", ").append(PAMACC_ID).append(",").append(CORPADMIN_FS1).append(" , ")
                .append(VOCODERID).append(" , ").append(ADDLINFO).append(" , ").append(LAST_ACTIVATION_TIME)
                .append(" , ").append(DISPATCH_TYPE).append(" , ").append(USER_ID).append(" , ").append(DERIVED_KEY).append(" , ").append(SUBSCR_NAME)
                .append(" , ").append(UFMI).append(" , ").append(IDEN_USERNAME).append(" , ").append(IDEN_PASSWORD)
                .append(" , ").append(IDEN_BUSUNITID).append(" , ").append(SERVICE_STATUS_OP).append(" , ")
                .append(SERVICE_STATUS_AUTHUSER).append(" , ").append(XDMS_FS1).append(" , ").append(LICENSE_TYPE)
                .append(" , ").append(ALIAS_MDN).append(" , ").append(QPPPACKID).append(" , ").append(SEGMENT_INDICATOR)
                .append(" , ").append(MCPTT_COMPLIANCE).append(" , ").append(SUBS_FS2).append(", ")
                .append(CLIENT_FS2).append(", ").append(ACTIVE_FS2).append(", ").append(OPS_FS2).append(",").append(CORPADMIN_FS2)
                .append(" , ").append(XDMS_FS2).append(" , ").append(MC_ID).append(" , ").append(MC_PTTID).append(" , ")
                .append(MC_VIDEOID).append(" , ").append(MC_DATAID).append(" , ").append(USERPROFILEINDEX).append(" , ").append(ISDEFAULTPROFILE).
                append(" , ").append(FEATURE_REL_VERSION).append(" , ").append(CLIENT_SW_INF).append(" , ").append(CLIENT_PLATFORM_TYPE).append(" , ").append(DYNAMIC_QOS_FLAG)
                .append(" , ").append(DERIVEDKEY).append(" , ").append(USERPROFILEFS2).append(" , ").append(USERPROFILEID).append(" , ").append(CAMERA_TYPE)
                .append(" , ").append(PRIVACY_OPT_STATUS).append(" , ").append(PREV_SERVICE_AUTH_STATUS).append(" , ").append(EXT_GATEWAY_ID);;
        strBuffer.append(" FROM ").append(TABLENAME).append(" WHERE ");

        return strBuffer;

    }

	private void getSelectSubsProfileResult(KnSubsProfileDTO subsProfileInfoDto, ResultSet rs)
			throws SQLException, UnsupportedEncodingException {
		subsProfileInfoDto.setMdn(rs.getString(MDN));
		subsProfileInfoDto.setPoCHome(rs.getString(POC_HOME));
		subsProfileInfoDto.setPresenceHome(rs.getString(PRESENCE_HOME));
		subsProfileInfoDto.setXDMSHome(rs.getString(XDMS_HOME));
		subsProfileInfoDto.setServiceAuthStatus(rs.getInt(SERVICE_AUTH_STATUS));
		subsProfileInfoDto.setPublicSubscriptionType(rs.getInt(PUBLIC_SUBSCRIPTION_TYPE));
		subsProfileInfoDto.setCorporateSubscriptionType(rs.getInt(CORP_SUBSCRIPTION_TYPE));
		subsProfileInfoDto.setCorpId(rs.getInt(CORP_ID));
		int corpPairingInd = rs.getInt(CORP_CONTACT_PAIRING_IND);
		if (corpPairingInd == 1) {
			subsProfileInfoDto.setPairingInd(true);
		} else {
			subsProfileInfoDto.setPairingInd(false);
		}
		String imei = rs.getString(IMEI);
		if (imei != null) {
			imei = imei.trim();
		}
		subsProfileInfoDto.setIMEI(imei);
		subsProfileInfoDto.setAffiliateId(rs.getString(AFFILIATE_ID));
		subsProfileInfoDto.setPayType(rs.getInt(PAY_TYPE));
		subsProfileInfoDto.setSubsCreationTime(rs.getLong(SUBSCR_CREATION_TIME));
		subsProfileInfoDto.setLastProfileUpdateTime(rs.getLong(LAST_PROFILE_UPDATE_TIME));
		subsProfileInfoDto.setCorpContactListId(rs.getInt(CORPCONTACTLISTID));
		subsProfileInfoDto.setUserAgent(rs.getString(USER_AGENT));
		subsProfileInfoDto.setClientPassword(rs.getString(CLIENT_PASSWORD));
		subsProfileInfoDto.setEmailAddress(rs.getString(EMAIL));
		subsProfileInfoDto.setSubsClientType(rs.getInt(CLIENT_TYPE));
		subsProfileInfoDto.setDispatchGroupMember(rs.getInt(DISPATCH_GRP_MEMBER));
		subsProfileInfoDto.setClientPVmajorVer(rs.getInt(CLIENTPV_MAJORVERSION));
		subsProfileInfoDto.setClientPVminorVer(rs.getInt(CLIENTPV_MINORVERSION));
		subsProfileInfoDto.setAccountId(rs.getString(ACCOUNT_ID));
		subsProfileInfoDto.setPamAccId(rs.getInt(PAMACC_ID));
		subsProfileInfoDto.setVocoderId(rs.getInt(VOCODERID));
		subsProfileInfoDto.setHierarchyType(HIERARCHY_TYPE.validate(rs.getInt(ADDLINFO)));
		subsProfileInfoDto.setLastActivationTime(rs.getLong(LAST_ACTIVATION_TIME));
		subsProfileInfoDto.setDispatchType(rs.getInt(DISPATCH_TYPE));
		String derKey = rs.getString(DERIVED_KEY);
        if (derKey != null && !(derKey.isEmpty())){
        	subsProfileInfoDto.setDerivedKey(KnGeneralUtil.convertAsciiToHex(derKey));
        }
		subsProfileInfoDto.setUserId(rs.getString(USER_ID));
		if (rs.getString(SUBSCR_NAME) != null)
			subsProfileInfoDto.setNetworkName(new String(rs.getString(SUBSCR_NAME).getBytes("8859_1"), "UTF-8"));
		subsProfileInfoDto.setUfmi(rs.getString(UFMI));
		subsProfileInfoDto.setiDenUserName(rs.getString(IDEN_USERNAME));
		subsProfileInfoDto.setiDenPassword(rs.getString(IDEN_PASSWORD));
		subsProfileInfoDto.setiDenBusUnitId(rs.getString(IDEN_BUSUNITID));
		subsProfileInfoDto.setPoCStatusOP(rs.getInt(SERVICE_STATUS_OP));
		subsProfileInfoDto.setPoCStatusAU(rs.getInt(SERVICE_STATUS_AUTHUSER));
		subsProfileInfoDto.setServiceStatusOp(rs.getInt(SERVICE_STATUS_OP));
		subsProfileInfoDto.setServiceStatusAuthUser(rs.getInt(SERVICE_STATUS_AUTHUSER));
		subsProfileInfoDto.setLicenseType(rs.getInt(LICENSE_TYPE));
        if (rs.getString(PREV_SERVICE_AUTH_STATUS) != null) {
            subsProfileInfoDto.setPreviousServiceAuthStatus(rs.getInt(PREV_SERVICE_AUTH_STATUS));
        }
        if(rs.getString(ALIAS_MDN) != null) subsProfileInfoDto.setAliasMdn(rs.getString(ALIAS_MDN).trim());
        subsProfileInfoDto.setQppPkgId(rs.getInt(QPPPACKID));
        subsProfileInfoDto.setFirstNetIndicator(rs.getString(SEGMENT_INDICATOR));
        subsProfileInfoDto.setMcpttCompliance(rs.getInt(MCPTT_COMPLIANCE));
		String subsFs2=rs.getString(SUBS_FS2)!=null?rs.getString(SUBS_FS2):KnGeneralUtil.convertLongToHexString(rs.getLong(SUBS_FS1));
		subsProfileInfoDto.setSubsFS2(subsFs2);
        String clientsFs2=rs.getString(CLIENT_FS2)!=null?rs.getString(CLIENT_FS2):KnGeneralUtil.convertLongToHexString(rs.getLong(CLIENT_FS1));
        subsProfileInfoDto.setClientFS2(clientsFs2);
        String activeFs2=rs.getString(ACTIVE_FS2)!=null?rs.getString(ACTIVE_FS2):KnGeneralUtil.convertLongToHexString(rs.getLong(ACTIVE_FS1));
        subsProfileInfoDto.setActiveFS2(activeFs2);
        String opsFs2=rs.getString(OPS_FS2)!=null?rs.getString(OPS_FS2):KnGeneralUtil.convertLongToHexString(rs.getLong(OPS_FS1));
        subsProfileInfoDto.setOpsFS2(opsFs2);
        String corpAdminFs2=rs.getString(CORPADMIN_FS2)!=null?rs.getString(CORPADMIN_FS2):KnGeneralUtil.convertLongToHexString(rs.getLong(CORPADMIN_FS1));
        subsProfileInfoDto.setCorpAdminFS2(corpAdminFs2);
        String xdmsFs1=rs.getString(XDMS_FS2)!=null?rs.getString(XDMS_FS2):KnGeneralUtil.convertLongToHexString(rs.getLong(XDMS_FS1));
        subsProfileInfoDto.setXdmsFS2(xdmsFs1);
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
        subsProfileInfoDto.setExtGatewayId(rs.getString(EXT_GATEWAY_ID));
		subsProfileInfoDto.setIsDefaultProfile(rs.getInt(ISDEFAULTPROFILE));
        subsProfileInfoDto.setFeatureRelVersion(rs.getString(FEATURE_REL_VERSION) != null ? rs.getString("FEATURE_REL_VERSION") : "0.0");
        subsProfileInfoDto.setSwType(rs.getInt(CLIENT_SW_INF));
        subsProfileInfoDto.setPlatformType(rs.getInt(CLIENT_PLATFORM_TYPE));
        subsProfileInfoDto.setDynamicQosFlag(rs.getInt(DYNAMIC_QOS_FLAG));
		if (rs.getString(USERPROFILEFS2) != null) {
			subsProfileInfoDto.setUserProfileFS2(rs.getString(USERPROFILEFS2));
		}
        if(rs.getString(USER_ID)!=null){
            subsProfileInfoDto.setUserId(rs.getString(USER_ID));
        }
		subsProfileInfoDto.setUserProfileId(rs.getString(USERPROFILEID));
        subsProfileInfoDto.setCameraType((Integer)rs.getObject(CAMERA_TYPE));
        subsProfileInfoDto.setPrivacyOptStatus(rs.getInt(PRIVACY_OPT_STATUS));
        subsProfileInfoDto.setClusterId(rs.getString(CLUSTERID));
        subsProfileInfoDto.setHierarchyId(rs.getString(HIERARCHY_ID));
        subsProfileInfoDto.setHierarchyRoot(rs.getString(HIERARCHY_ROOT));
	}

	public KnOPSubsProfileInfoDTO searchCorpAddressBook(KnIPSubscriberInfoDTO subscriberDTO, KnPersisterTxn persistTxn)
			throws KnDAOException {
		String methodName = "searchCorpAddressBook(KnIPSubscriberInfoDTO, KnPersisterTxn)";
		String query = null;
		String query2 = null;
		Connection conn;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		KnOPSubsProfileInfoDTO subsProfileInfoDto = new KnOPSubsProfileInfoDTO();
		knLogger.debug(methodName, "ENTRY: searchCorpAddressBook " + subscriberDTO);
		int corpId = subscriberDTO.getCorpId();
		String searchString = subscriberDTO.getSearchString().toUpperCase();
        boolean specialCharecterFound = false;

		if(searchString.contains(com.kodiak.common.resources.KnConstants.UNDERSCORE)) {
			searchString=searchString.replaceAll(com.kodiak.common.resources.KnConstants.UNDERSCORE, com.kodiak.common.resources.KnConstants.ESCAPE_CHAR_REGEX+com.kodiak.common.resources.KnConstants.UNDERSCORE);
			specialCharecterFound = true;
		}

		if(searchString.contains(com.kodiak.common.resources.KnConstants.PERCENTAGE)) {
			searchString=searchString.replaceAll(com.kodiak.common.resources.KnConstants.PERCENTAGE, com.kodiak.common.resources.KnConstants.ESCAPE_CHAR_REGEX+com.kodiak.common.resources.KnConstants.PERCENTAGE);
            specialCharecterFound = true;
		}

        if (searchString.contains(com.kodiak.common.resources.KnConstants.APOSTROPHE)) {
            searchString = searchString.replaceAll(com.kodiak.common.resources.KnConstants.APOSTROPHE, com.kodiak.common.resources.KnConstants.ESCAPE_APOSTROPH_CHAR + com.kodiak.common.resources.KnConstants.PERCENTAGE);
            specialCharecterFound = true;
        }
        knLogger.debug(methodName, "formated " + searchString);
		int pageId = subscriberDTO.getPageId();
		int maxSize = subscriberDTO.getMaxPageSize();
		String first = String.valueOf(maxSize * (pageId - 1) + 1);
		String last = String.valueOf(maxSize * pageId);
		Integer total = 0;
		Integer pageSize = 0;
		Map<String, KnSubsProfileDTO> subsRespMap = null;
		try {
		    if(specialCharecterFound) {
                query = CORP_SUBS_CNT_REAL_MDN_QRY + SELECT_ADDRESS_BOOK_CONDITION_WITH_ESCAPE.replace("SEARCHSTRING", searchString);
            }else {
                query = CORP_SUBS_CNT_REAL_MDN_QRY + SELECT_ADDRESS_BOOK_CONDITION.replace("SEARCHSTRING", searchString);
            }
			conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            knLogger.debug(methodName, "Query: Executing - ", query, ", corpId - ", corpId);
			pStmt = conn.prepareStatement(query);
			pStmt.setInt(1, subscriberDTO.getCorpId());
			rs = pStmt.executeQuery();
			knLogger.debug(methodName, "Query: Executed ");

			if (rs.next()) {
				total = rs.getInt(1);
			}

			if (total % maxSize == 0) {
				subsProfileInfoDto.setTotalPages(total / maxSize);
			} else {
				subsProfileInfoDto.setTotalPages(total / maxSize + 1);
			}

			if (total >= Integer.valueOf(first)) {
				subsRespMap = new HashMap<String, KnSubsProfileDTO>();
                if (specialCharecterFound) {
                    query2 = SELECT_ADDRESS_BOOK_QUERY_WITH_ESCAPE.replace("FIRST", first).replace("LAST", last).replace("SEARCHSTRING",
                            searchString);
                } else {
                    query2 = SELECT_ADDRESS_BOOK_QUERY.replace("FIRST", first).replace("LAST", last).replace("SEARCHSTRING",
                            searchString);
                }
				pStmt = conn.prepareStatement(query2);
				pStmt.setInt(1, subscriberDTO.getCorpId());
				knLogger.debug(methodName, "Query: Executing - ", query2, ", corpId - ", corpId);
				rs2 = pStmt.executeQuery();
				knLogger.debug(methodName, "Query: Executed ");
				while (rs2.next()) {
					KnSubsProfileDTO profileDto = new KnSubsProfileDTO();
					profileDto.setMdn(rs2.getString(MDN).trim());
					if (rs2.getString(SUBSCR_NAME) != null)
						profileDto.setNetworkName(new String(rs2.getString(SUBSCR_NAME).getBytes("8859_1"), "UTF-8"));
					profileDto.setActiveFS2(rs2.getString(ACTIVE_FS2));
					subsRespMap.put(profileDto.getMdn(), profileDto);
				}
				subsProfileInfoDto.setSubsRespMap(subsRespMap);
				subsProfileInfoDto.setPageSize(subsRespMap.size());
			} else {
				subsProfileInfoDto.setPageSize(0);
			}
		}  catch (Exception e) {
			knLogger.error(methodName, "Unexpected Exception - ", e);
			throw KnDbUtil.processException(e, "Failed to select Subscriber Profiles - " + e.getMessage(), pttServerId,
					KnProvDAOSourceTypes.POCSUBSCRINFO, query);
		} finally {
			KnDbUtil.closeResultSet(rs);
			KnDbUtil.closeResultSet(rs2);
            KnDbUtil.closePrepareStmt(pStmt);
			knLogger.info(methodName, "EXIT : searchCorpAddressBook");
		}

		return subsProfileInfoDto;
    }

	public List<KnOPSubsProfileInfoDTO> selectSubscriberProfileByMCSIds(List<String> mcsIds, KnPersisterTxn persistTxn)
			throws KnDAOException {
		String methodName = "selectSubscriberProfileByMCSIds(List, KnPersisterTxn)";
		List<KnOPSubsProfileInfoDTO> subsProfileInfoList = new ArrayList<>();
		KnOPSubsProfileInfoDTO subsProfileInfoDto = null;
		Connection conn;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		String query = null;
		knLogger.debug(methodName, "ENTRY:  selectSubscriberProfileByMCSIds ");
		try {
			StringBuffer keys = com.kodiak.common.dao.KnDbUtil.convertListToStringBuffer(mcsIds);
			/*StringBuilder strBuffer = getSelectSubsProfileQuery();
			strBuffer.append(MC_ID).append(" IN ").append(keys.toString()).append(" OR ").append(MC_PTTID)
					.append(" IN ").append(keys.toString()).append(" OR ").append(MC_VIDEOID).append(" IN ")
					.append(keys.toString()).append(" OR ").append(MC_DATAID).append(" IN ").append(keys.toString());*/
            StringBuilder selectQryBuilder = getSelectSubsProfileQuery();
            String selectQry = selectQryBuilder.toString();

            StringBuilder strBuffer = selectQryBuilder.append(MC_ID).append(" IN ").append(keys.toString());
            strBuffer =strBuffer.append(" UNION ").append(selectQry).append(MC_PTTID).append(" IN ").append(keys.toString());
            strBuffer =strBuffer.append(" UNION ").append(selectQry).append(MC_VIDEOID).append(" IN ").append(keys.toString());
            strBuffer =strBuffer.append(" UNION ").append(selectQry).append(MC_DATAID).append(" IN ").append(keys.toString());
			query = strBuffer.toString();
			conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
			pStmt = conn.prepareStatement(query);
			knLogger.debug(methodName, "Query: Executing - ", query);
			rs = pStmt.executeQuery();
			knLogger.debug(methodName, "Query: Executed ");
			while (rs.next()) {
				subsProfileInfoDto = new KnOPSubsProfileInfoDTO();
				getSelectSubsProfileResult(subsProfileInfoDto, rs);
				subsProfileInfoList.add(subsProfileInfoDto);
			}
			knLogger.debug(methodName, "subsProfileInfoList :", subsProfileInfoList);

		} catch (Exception e) {
			knLogger.error(methodName, "Unexpected Exception - ", e);
			throw KnDbUtil.processException(e, "Failed to select Subscriber Profile By MCSIds" + e.getMessage(),
					pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
		} finally {
			KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePrepareStmt(pStmt);
			knLogger.debug(methodName, "EXIT :selectSubscriberProfileByMCSIds", subsProfileInfoList);
		}
		return subsProfileInfoList;
	}

	public List<KnOPSubsProfileInfoDTO> selectSubscriberProfileByMDNorAliasMdn(List<String> mdns,
			KnPersisterTxn persistTxn) throws KnDAOException {
		String methodName = "selectSubscriberProfileByMDNorAliasMdn(List, KnPersisterTxn)";
		List<KnOPSubsProfileInfoDTO> subsProfileInfoList = new ArrayList<>();
		KnOPSubsProfileInfoDTO subsProfileInfoDto = null;
		Connection conn;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		String query = null;
		knLogger.debug(methodName, "ENTRY:  selectSubscriberProfileByMDNorAliasMdn ");
		try {
			StringBuffer keys = com.kodiak.common.dao.KnDbUtil.convertListToStringBuffer(mdns);
			StringBuilder strBuffer = getSelectSubsProfileQuery();
			strBuffer.append(MDN).append(" IN ").append(keys.toString()).append(" OR ").append(ALIAS_MDN).append(" IN ")
					.append(keys.toString());
			query = strBuffer.toString();
			conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
			pStmt = conn.prepareStatement(query);
			knLogger.debug(methodName, "Query: Executing - ", query);
			rs = pStmt.executeQuery();
			knLogger.debug(methodName, "Query: Executed ");
			while (rs.next()) {
				subsProfileInfoDto = new KnOPSubsProfileInfoDTO();
				getSelectSubsProfileResult(subsProfileInfoDto, rs);
				subsProfileInfoList.add(subsProfileInfoDto);
			}
			knLogger.debug(methodName, "subsProfileInfoList :", subsProfileInfoList);

		} catch (Exception e) {
			knLogger.error(methodName, "Unexpected Exception - ", e);
			throw KnDbUtil.processException(e,
					"Failed to select Subscriber Profile By MDN or Alias Mdn" + e.getMessage(), pttServerId,
					KnProvDAOSourceTypes.POCSUBSCRINFO, query);
		} finally {
			KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePrepareStmt(pStmt);
			knLogger.debug(methodName, "EXIT :selectSubscriberProfileByMDNorAliasMdn", subsProfileInfoList);
		}
		return subsProfileInfoList;
	}

    public List<KnOPSubsProfileInfoDTO> selectSubscriberProfileByAliasMdn(List<String> mdns,
                                                                               KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "selectSubscriberProfileByAliasMdn(List, KnPersisterTxn)";
        List<KnOPSubsProfileInfoDTO> subsProfileInfoList = new ArrayList<>();
        KnOPSubsProfileInfoDTO subsProfileInfoDto = null;
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String query = null;
        knLogger.debug(methodName, "ENTRY:  selectSubscriberProfileByAliasMdn ");

        knLogger.debug(methodName, "mdns :", KnGDPRTemplate.mdnList(mdns));
        try {
            StringBuffer keys = com.kodiak.common.dao.KnDbUtil.convertListToStringBuffer(mdns);
            StringBuilder strBuffer = getSelectSubsProfileQuery();
            strBuffer.append(ALIAS_MDN).append(" IN ")
                    .append(keys.toString());
            query = strBuffer.toString();
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pStmt = conn.prepareStatement(query);
            knLogger.debug(methodName, "Query: Executing - ", query);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Query: Executed ");
            while (rs.next()) {
                subsProfileInfoDto = new KnOPSubsProfileInfoDTO();
                getSelectSubsProfileResult(subsProfileInfoDto, rs);
                subsProfileInfoList.add(subsProfileInfoDto);
            }
            knLogger.debug(methodName, "subsProfileInfoList :", subsProfileInfoList);

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e,
                    "Failed to select Subscriber Profile By  Alias Mdn" + e.getMessage(), pttServerId,
                    KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePrepareStmt(pStmt);
            knLogger.debug(methodName, "EXIT :selectSubscriberProfileByAliasMdn", subsProfileInfoList);
        }
        return subsProfileInfoList;
    }

    public List<KnOPSubsProfileInfoDTO> selectSubscriberProfileByMCDataIds(List<String> mcDataIds, KnPersisterTxn persistTxn)
            throws KnDAOException {
        String methodName = "selectSubscriberProfileByMCDataIds(List, KnPersisterTxn)";
        List<KnOPSubsProfileInfoDTO> subsProfileInfoList = new ArrayList<>();
        KnOPSubsProfileInfoDTO subsProfileInfoDto = null;
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String query = null;
        knLogger.debug(methodName, "ENTRY:  selectSubscriberProfileByMCDataIds ");
        try {
            StringBuffer keys = com.kodiak.common.dao.KnDbUtil.convertListToStringBuffer(mcDataIds);
            StringBuilder strBuffer = getSelectSubsProfileQuery();
            strBuffer.append(MC_DATAID).append(" IN ").append(keys.toString());
            query = strBuffer.toString();
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pStmt = conn.prepareStatement(query);
            knLogger.debug(methodName, "Query: Executing - ", query);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Query: Executed ");
            while (rs.next()) {
                subsProfileInfoDto = new KnOPSubsProfileInfoDTO();
                getSelectSubsProfileResult(subsProfileInfoDto, rs);
                subsProfileInfoList.add(subsProfileInfoDto);
            }
            knLogger.debug(methodName, "subsProfileInfoList :", subsProfileInfoList);

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to select Subscriber Profile By MCDataIds" + e.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePrepareStmt(pStmt);
            knLogger.debug(methodName, "EXIT :selectSubscriberProfileByMCDataIds", subsProfileInfoList);
        }
        return subsProfileInfoList;
    }

    public List<KnOPSubsProfileInfoDTO> selectSubscriberProfileByUserIds(List<String> userIds, KnPersisterTxn persistTxn)
            throws KnDAOException {
        String methodName = "selectSubscriberProfileByUserIds(List, KnPersisterTxn)";
        List<KnOPSubsProfileInfoDTO> subsProfileInfoList = new ArrayList<>();
        KnOPSubsProfileInfoDTO subsProfileInfoDto = null;
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String query = null;
        knLogger.debug(methodName, "ENTRY:  selectSubscriberProfileByUserIds ");
        try {
            StringBuffer keys = com.kodiak.common.dao.KnDbUtil.convertListToNvarcharStringBufferLowerCase(userIds);
            StringBuilder strBuffer = getSelectSubsProfileQuery();
            strBuffer.append(USER_ID).append(" IN ").append(keys.toString());
            query = strBuffer.toString();
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pStmt = conn.prepareStatement(query);
            knLogger.debug(methodName, "Query: Executing - ", query);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Query: Executed ");
            while (rs.next()) {
                subsProfileInfoDto = new KnOPSubsProfileInfoDTO();
                getSelectSubsProfileResult(subsProfileInfoDto, rs);
                subsProfileInfoList.add(subsProfileInfoDto);
            }
            knLogger.debug(methodName, "subsProfileInfoList :", subsProfileInfoList);

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to select Subscriber Profile By MCDataIds" + e.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePrepareStmt(pStmt);
            knLogger.debug(methodName, "EXIT :selectSubscriberProfileByMCDataIds", subsProfileInfoList);
        }
        return subsProfileInfoList;
    }

	public void updatePrivacyOptStatus(KnIPSubscriberInfoDTO subscriberDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updatePrivacyOptStatus(KnIPSubscriberInfoDTO, KnPersisterTxn)";
        Connection conn;
        PreparedStatement pStmt = null;
        String query = null;
        //KnOPSubsProfileInfoDTO subsProfileInfoDto = new KnOPSubsProfileInfoDTO();
        knLogger.debug(methodName, "ENTRY: Update updatePrivacyOptStatus with DTO -", subscriberDTO);
        try {

            String mdn = subscriberDTO.getMdn();
            String optStatusValue=subscriberDTO.getOptStatusValue();
            long lastProfileUpdateTime = Calendar.getInstance().getTimeInMillis();
            StringBuilder queryBuffer = new StringBuilder();
            queryBuffer.append(UPDATE_QRY);
            queryBuffer.append(PRIVACY_OPT_STATUS).append("=?,");
            queryBuffer.append(LAST_PROFILE_UPDATE_TIME).append("=? ");
            queryBuffer.append("WHERE ").append(MDN).append("=?");

            query = queryBuffer.toString();

            knLogger.debug( methodName, "mdn::"+KnGDPRTemplate.mdn(mdn)+" optStatusValue ::"+optStatusValue+" query::"+query);

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);
            pStmt.setString(1, optStatusValue);
            pStmt.setLong(2, lastProfileUpdateTime);
            pStmt.setString(3, mdn);

            knLogger.debug(methodName, "QUERY: Executing the query - ", query);
            pStmt.executeUpdate();
            knLogger.debug(methodName, "QUERY: Executed ");

              //  subsProfileInfoDto.setR
        }  catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to update Privacy Opt Status - " + e.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closePrepareStmt(pStmt);
            knLogger.debug(methodName, "EXIT: update Privacy Opt Status");
        }
        //return subsProfileInfoDto;
    }

	public void updateMCSIds(KnSubsProfilePersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn)
			throws KnDAOException {
		String methodName = "updateMCSIds(KnSubsProfilePersistDTO, KnPersisterTxn)";
		String query = null;
		Connection conn;
		PreparedStatement pStmt = null;
		knLogger.debug(methodName, "ENTRY: updateMCSIds with DTO - ", subsProfilePersistDTO);
		try {
			StringBuilder queryBuffer = new StringBuilder();
			queryBuffer.append(UPDATE_QRY);
			queryBuffer.append(MC_ID).append("=? ,");
			queryBuffer.append(MC_PTTID).append("=? ,");
			queryBuffer.append(MC_VIDEOID).append("=? ,");
			queryBuffer.append(MC_DATAID).append("=? ");
			queryBuffer.append(" WHERE ").append(MDN).append("=?");

			query = queryBuffer.toString();
			conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
			pStmt = conn.prepareStatement(query);
			int columnIndex = 0;
			pStmt.setBytes(++columnIndex, subsProfilePersistDTO.getMcId().getBytes(StandardCharsets.UTF_8));
			pStmt.setBytes(++columnIndex, subsProfilePersistDTO.getMcpttId().getBytes(StandardCharsets.UTF_8));
			pStmt.setBytes(++columnIndex, subsProfilePersistDTO.getMcVideoId().getBytes(StandardCharsets.UTF_8));
			pStmt.setBytes(++columnIndex, subsProfilePersistDTO.getMcDataId().getBytes(StandardCharsets.UTF_8));
			pStmt.setString(++columnIndex, subsProfilePersistDTO.getMdn());

			knLogger.debug(methodName, "QUERY: Executing the Query - ", query, " DTO - ", subsProfilePersistDTO);
			pStmt.executeUpdate();
			knLogger.debug(methodName, "Query: Executed ");
		} catch (Exception e) {
			knLogger.error(methodName, "Unexpected Exception - ", e);
			throw KnDbUtil.processException(e, "Failed to update Subscriber Profile - " + e.getMessage(), pttServerId,
					KnProvDAOSourceTypes.POCSUBSCRINFO, query);
		} finally {
            KnDbUtil.closePrepareStmt(pStmt);
			knLogger.debug(methodName, "EXIT: updateMCSIds");
		}
	}

    public Map<String, Map<Integer, String>> selectProfileIdMDNsByMcpttId(List<String> mcpttIds, boolean readOnly, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "selectProfileIdMDNsByMcpttIds(String,boolean, KnPersisterTxn)";
        String query = null;
        Connection conn;
        Statement stmt = null;
        ResultSet rs = null;
        Map<String,Map<Integer,String>> mcPttIdprofileIdMdns= new HashMap<>();
        Map<Integer,String> profileIdMdns= new HashMap<>();
        knLogger.debug(methodName, "ENTRY: selectProfileIdMDNsByMcpttIds ", " readOnly :", readOnly);
        try {
            query = SELECT_PROFILEIDMDN_QUERY.replaceAll(MCPTT_LIST, KnGeneralUtil.getComSepList(mcpttIds));
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            persistTxn.getDBConnection(pttServerId, readOnly);
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
                        pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
            }
        } catch (KnDAOException e) {
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to selectProfileIdMDNsByMcpttIds - " + e.getMessage(), pttServerId,
                    KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            knLogger.info(methodName, "EXIT : selectProfileIdMDNsByMcpttIds");
        }

        return mcPttIdprofileIdMdns;

    }

    public String getMdnForUnassign(String baseMdn, String userProfileId, KnPersisterTxn persistTxn)
            throws KnDAOException {
        String methodName = "getMdnForUnassign(int, KnPersisterTxn)";
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String query = null;
        String mdn=null;

        knLogger.debug(methodName, "ENTRY: getMdnForUnassign");
        try {
            query = GET_MDN_FOR_UNASSIGN;

            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pStmt = conn.prepareStatement(query);
            pStmt.setString(1, baseMdn);
            pStmt.setString(2, userProfileId);
            knLogger.debug(methodName, "Query: Executing - ", query);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Query: Executed ");

            while (rs.next()) {
                mdn = rs.getString(1);
            }


        } catch (KnDAOException dbConne) {
            knLogger.error(methodName, "DAO Exception occurred");

            throw dbConne;
        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQL Exception occurred");

            throw KnDbUtil.processException(sqlE, "Failed to getMdnForUnassign - " + sqlE.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);

            throw KnDbUtil.processException(e, "Failed to getMdnForUnassign - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            knLogger.debug(methodName, "EXIT : getMdnForUnassign ");
        }
        return mdn;
    }

    public Map<String, KnOPSubsProfileInfoDTO> getProfileMdnNupmfsByBaseMdn(String baseMdn, boolean readOnly, KnPersisterTxn persistTxn)
            throws KnDAOException {
        String methodName = "getProfileMdnNupmfsByBaseMdn(String, boolean, KnPersisterTxn)";
        Connection conn = null;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String query = null;
        Map<String,KnOPSubsProfileInfoDTO> mdnNupmfsMap=new HashMap<>();
        boolean ownedTxn = false;

        knLogger.debug(methodName, "ENTRY: getMdnForUPM");
        try {
            if (persistTxn != null) {
                knLogger.debug(methodName, " Data Store before - ", KnDBConst.DataStores.XDM_SHARED_DATA);
                conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
                knLogger.debug(methodName, "if block");
            } else {
                conn = KnDBManager.getDBManagerInstance().getConnection(KnGenInfoUtil.getSecDBPttServerId(pttServerId), true);
                ownedTxn = true;
                knLogger.debug(methodName, "else block");
                knLogger.debug(methodName, "conn", conn);
            }
            query = GET_MDN_FOR_UPM;

            //conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            pStmt = conn.prepareStatement(query);
            pStmt.setString(1, baseMdn);
            knLogger.debug(methodName, "Query: Executing - ", query);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Query: Executed ");
            while (rs.next()) {
            	KnOPSubsProfileInfoDTO knOPSubsProfileInfoDTO= new KnOPSubsProfileInfoDTO();
            	knOPSubsProfileInfoDTO.setUserProfileFS2(rs.getString(USERPROFILEFS2));
            	knOPSubsProfileInfoDTO.setXdmsFS2(rs.getString(XDMS_FS2));
                knOPSubsProfileInfoDTO.setOldActiveFS(rs.getString(ACTIVEFS2));
                mdnNupmfsMap.put(rs.getString(1).trim(),knOPSubsProfileInfoDTO);

            }
        } catch (KnDAOException dbConne) {
            knLogger.error(methodName, "DAO Exception occurred");

            throw dbConne;
        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQL Exception occurred");

            throw KnDbUtil.processException(sqlE, "Failed to getMdnForUnassign - " + sqlE.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);

            throw KnDbUtil.processException(e, "Failed to getMdnForUnassign - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            if (ownedTxn) {
                KnDbUtil.closeConnection(conn);
            }
            knLogger.debug(methodName, "EXIT : getMdnForUnassign mdnNupmfsMap:",KnGDPRTemplate.mapKeyMdn(mdnNupmfsMap));
        }
        return mdnNupmfsMap;
    }

    public List<String> getMdnForUPMList(List<String> baseMdn,KnPersisterTxn persistTxn)
            throws KnDAOException {
        String methodName = "getMdnForUPMList(List<String>, KnPersisterTxn)";
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String query = null;
        String mdn=null;
        List<String> mdnList=new ArrayList<>();
        String mdnCommaSeparated = String.join(",", baseMdn);

        knLogger.debug(methodName, "ENTRY: getMdnForUPM");
        try {
            query = GET_MDN_FOR_UPM_LIST;

            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pStmt = conn.prepareStatement(query);
            pStmt.setString(1, mdnCommaSeparated);
            knLogger.debug(methodName, "Query: Executing - ", query);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Query: Executed ");

            while (rs.next()) {
                mdn = rs.getString(1).trim();
                mdnList.add(mdn);
            }


        } catch (KnDAOException dbConne) {
            knLogger.error(methodName, "DAO Exception occurred");

            throw dbConne;
        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQL Exception occurred");

            throw KnDbUtil.processException(sqlE, "Failed to getMdnForUnassign - " + sqlE.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);

            throw KnDbUtil.processException(e, "Failed to getMdnForUnassign - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            knLogger.debug(methodName, "EXIT : getMdnForUnassign ");
        }
        return mdnList;
    }

    public void updateSubscrUserProfileFS(KnSubsProfilePersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateSubscrUserProfileFS(KnSubsProfilePersistDTO, KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        knLogger.debug(methodName, "ENTRY: - ", subsProfilePersistDTO);
        try {
            StringBuilder queryBuffer = new StringBuilder();
            queryBuffer.append(UPDATE_QRY);
            queryBuffer.append(ACTIVE_FS1).append("=? ,");
            queryBuffer.append(ACTIVE_FS2).append("=? ,");
            queryBuffer.append(LAST_PROFILE_UPDATE_TIME).append("=? ,");
            queryBuffer.append(USERPROFILEFS2).append("=? ");
            queryBuffer.append(" WHERE ").append(MDN).append("=?");

            query = queryBuffer.toString();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);
            pStmt.setLong(1, KnGeneralUtil.convertHexStringToLong(subsProfilePersistDTO.getActiveFS2()));
            pStmt.setString(2, KnGeneralUtil.getFeatureSet(subsProfilePersistDTO.getActiveFS2()));
            pStmt.setLong(3, subsProfilePersistDTO.getLastProfileUpdateTime());
            pStmt.setString(4, KnGeneralUtil.getFeatureSet(subsProfilePersistDTO.getUserProfileFS2()));
            pStmt.setString(5, subsProfilePersistDTO.getMdn());
            knLogger.debug(methodName, "QUERY: Executing the Query - ", query, " DTO - ", subsProfilePersistDTO);

            int numberOfRowsUpdated = pStmt.executeUpdate();
            knLogger.debug(methodName, "Query: Executed ");
            knLogger.debug(methodName, "QUERY: After query Execution - ", query, " numberOfRowsUpdated - ", numberOfRowsUpdated);

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to update Subscriber Profile - " + e.getMessage(), pttServerId,
                    KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closePrepareStmt(pStmt);
            knLogger.debug(methodName, "EXIT:");
        }
    }

    public void updateSubscrTS(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateSubscrTS(List<String>, KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        knLogger.debug(methodName, "ENTRY: - mdnList :", KnGDPRTemplate.mdnList(mdnList));
        try {
            StringBuilder queryBuffer = new StringBuilder();
            queryBuffer.append(UPDATE_QRY);
            queryBuffer.append(LAST_PROFILE_UPDATE_TIME).append("=? ");
            queryBuffer.append(" WHERE ").append(MDN).append("=?");

            query = queryBuffer.toString();
            knLogger.debug(methodName,"query :",query);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);
            for(String mdn :mdnList){
                long lastProfileUpdateTime = Calendar.getInstance().getTimeInMillis();
                pStmt.setLong(1, lastProfileUpdateTime);
                pStmt.setString(2, mdn);
                pStmt.addBatch();
            }
            pStmt.executeBatch();

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to update Subscriber Profile - " + e.getMessage(), pttServerId,
                    KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closePrepareStmt(pStmt);
            knLogger.debug(methodName, "EXIT:");
        }
    }

    public KnOPSubsProfileInfoDTO selectSubscriberProfileForUFMIForAssignUserProfile(String ufmi, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "selectSubscriberProfileForUFMIForAssignUserProfile(String, KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        KnOPSubsProfileInfoDTO subsProfileInfoDto = null;
        knLogger.debug(methodName, "ENTRY: Select Subscriber Profile ");
        try {


            StringBuilder strBuffer = getSelectSubsProfileQuery();
            strBuffer.append(UFMI).append("=?");
            strBuffer.append(" AND ");
            strBuffer.append(USERPROFILEINDEX).append("=?");
            query = strBuffer.toString();


            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            //conn = persistTxn.getDBConnection(pttServerId, false);
            knLogger.debug(methodName, "selectSubscriberProfile ... ");
            pStmt = conn.prepareStatement(query);
            pStmt.setString(1, ufmi);
            pStmt.setInt(2, 0);
            knLogger.debug(methodName, "Query: Executing - ", query, ", ufmi - ", ufmi);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Query: Executed ");

            if (rs.next()) {
                subsProfileInfoDto=new KnOPSubsProfileInfoDTO();
                getSelectSubsProfileResult(subsProfileInfoDto, rs);

            }


        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to select Subscriber Profile - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePrepareStmt(pStmt);
            knLogger.info(methodName, "EXIT : select Subscriber Profile");
        }

        return subsProfileInfoDto;

    }

    public KnOPSubsProfileInfoDTO selectSubscriberProfileByAliasMdnForAssignUserProfile(String aliasMdn, KnPersisterTxn persistTxn)
            throws KnDAOException {
        String methodName = "selectSubscriberProfileByAliasMdnForAssignUserProfile(String, KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        KnOPSubsProfileInfoDTO subsProfileInfoDto = null;
        knLogger.debug(methodName, "ENTRY: Select Subscriber Profile ");
        try {

            StringBuilder strBuffer = getSelectSubsProfileQuery();

            strBuffer.append(ALIAS_MDN).append("=?");
            strBuffer.append(" AND ");
            strBuffer.append(USERPROFILEINDEX).append("=?");
            query = strBuffer.toString();

            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            // conn = persistTxn.getDBConnection(pttServerId, false);
            knLogger.debug(methodName, "selectSubscriberProfile ... ");
            pStmt = conn.prepareStatement(query);
            pStmt.setString(1, aliasMdn);
            pStmt.setInt(2, 0);
            knLogger.debug(methodName, "Query: Executing - ", query, ", aliasMdn - ", KnGDPRTemplate.mdn(aliasMdn));
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Query: Executed ");

            if (rs.next()) {
                subsProfileInfoDto = new KnOPSubsProfileInfoDTO();
                getSelectSubsProfileResult(subsProfileInfoDto, rs);

            } else {
                knLogger.info(methodName, "Subscriber Profile doesn't exist");
                //throw new KnDBPersistenceException(KnErrorCodes.DAO.ROW_NOT_FOUND, "Subscriber Profile does not exist",
                //      pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
            }

        }catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred");
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to select Subscriber Profile - " + e.getMessage(), pttServerId,
                    KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePrepareStmt(pStmt);
            knLogger.info(methodName, "EXIT : select Subscriber Profile");
        }

        return subsProfileInfoDto;
    }

    public void updateMCSIdsForUserProfiles(KnSubsProfilePersistDTO subsProfilePersistDTO,String mcId,String mcDataId,String mcVideoId,String mcPttId, KnPersisterTxn persisterTxn)
			throws KnDAOException {
		String methodName = "updateMCSIdsForUserProfiles(KnSubsProfilePersistDTO,String,String,String,String KnPersisterTxn)";
		String query = null;
		Connection conn;
		PreparedStatement pStmt = null;
		knLogger.debug(methodName, "ENTRY: updateMCSIds with DTO - ", subsProfilePersistDTO);
		try {
			StringBuilder queryBuffer = new StringBuilder();
			queryBuffer.append(UPDATE_QRY);
			queryBuffer.append(MC_ID).append("=? ,");
			queryBuffer.append(MC_PTTID).append("=? ,");
			queryBuffer.append(MC_VIDEOID).append("=? ,");
			queryBuffer.append(MC_DATAID).append("=? ,");
			queryBuffer.append(SERVICE_AUTH_STATUS).append("=? ");
			queryBuffer.append(" WHERE ").append(MC_ID).append("=?").append(" AND ").append(MC_PTTID).append("=?")
					.append(" AND ").append(MC_VIDEOID).append("=?").append(" AND ").append(MC_DATAID).append("=?");

			query = queryBuffer.toString();
			conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
			pStmt = conn.prepareStatement(query);
			int columnIndex = 0;
			pStmt.setBytes(++columnIndex, subsProfilePersistDTO.getMcId().getBytes(StandardCharsets.UTF_8));
			pStmt.setBytes(++columnIndex, subsProfilePersistDTO.getMcpttId().getBytes(StandardCharsets.UTF_8));
			pStmt.setBytes(++columnIndex, subsProfilePersistDTO.getMcVideoId().getBytes(StandardCharsets.UTF_8));
			pStmt.setBytes(++columnIndex, subsProfilePersistDTO.getMcDataId().getBytes(StandardCharsets.UTF_8));
			pStmt.setInt(++columnIndex, subsProfilePersistDTO.getServiceAuthStatus());
			pStmt.setBytes(++columnIndex, mcId.getBytes(StandardCharsets.UTF_8));
			pStmt.setBytes(++columnIndex, mcPttId.getBytes(StandardCharsets.UTF_8));
			pStmt.setBytes(++columnIndex, mcVideoId.getBytes(StandardCharsets.UTF_8));
			pStmt.setBytes(++columnIndex, mcDataId.getBytes(StandardCharsets.UTF_8));

			knLogger.debug(methodName, "QUERY: Executing the Query - ", query, " DTO - ", subsProfilePersistDTO);
			pStmt.executeUpdate();
			knLogger.debug(methodName, "Query: Executed ");
		} catch (Exception e) {
			knLogger.error(methodName, "Unexpected Exception - ", e);
			throw KnDbUtil.processException(e, "Failed to update Subscriber Profile - " + e.getMessage(), pttServerId,
					KnProvDAOSourceTypes.POCSUBSCRINFO, query);
		} finally {
            KnDbUtil.closePrepareStmt(pStmt);
			knLogger.debug(methodName, "EXIT: updateMCSIds");
		}
    }
		/**
	     * @param corpId
	     * @return boolean
	     * @throws KnDAOException
	     */

	    public String getProfileName(int corpId, Integer userProfileIndex) throws KnDAOException {

	        final String methodName = "getProfileName(int,String)";
	        String result = null;
	        knLogger.debug(methodName, "corpId - ", corpId, " userProfileIndex - ", userProfileIndex);
	        try {
                JsonObject values = JsonObject.create().put("corpId", corpId).put("userProfileIndex", userProfileIndex);
                QueryResult queryResult= KnCBSRepository.getInstance().getQueryResult(GET_USER_PROFILE_NAME,values);
                result = queryResult.rowsAsObject().get(0).get(USER_PROFILE_NAME).toString();

	        } catch (NoSuchElementException e) {
	            result=null;
	            knLogger.error(methodName,"NoSuchElementException msg",e.getMessage());
            }catch (RejectedExecutionException e) {
	            throw new KnDAOException(CBS_NOT_REACHABLE,"CBS connection rejected");
	        } catch (Exception e) {
                KnCBSRepository.generateCBSTimeoutAlarm(e);
	            throw KnDbUtil.processException(e, "Failed to check the ProfileName for the given corpId and userProfileIndex", pttServerId, "pocdata", GET_USER_PROFILE_NAME);
	        }
	        return result;
	    }

    public void updateMdnFiledsNActiveFS(IPersistenceDTO persistenceDTO
            ,Map<String, String> mdnActivsFsMap, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "DAO.updateUserAgentForMcId()";
        boolean ownedTxn = false;
        Connection conn;
        PreparedStatement pStmt = null;
        String query = null;
        try {
            KnSubsProfilePersistDTO subsInfoPersistDTO = (KnSubsProfilePersistDTO) persistenceDTO;
            knLogger.debug(methodName," subsInfoPersistDTO :",subsInfoPersistDTO ," mdnActivsFsMap :",mdnActivsFsMap);
            String networkName = subsInfoPersistDTO.getNetworkName();
            int corpSubscriptionType = subsInfoPersistDTO.getCorporateSubscriptionType();
            int publicSubscriptionType = subsInfoPersistDTO.getPublicSubscriptionType();
            int serviceAuthStatus = subsInfoPersistDTO.getServiceAuthStatus();
            String imei = subsInfoPersistDTO.getIMEI();
            String clientPassword = subsInfoPersistDTO.getClientPassword();
            long lastProfileUpdateTime = subsInfoPersistDTO.getLastProfileUpdateTime();
            Long lastActivationTime = subsInfoPersistDTO.getLastActivationTime();
            String userAgent = subsInfoPersistDTO.getUserAgent();
            int pvMajorVersion = subsInfoPersistDTO.getClientPVmajorVer();
            int pvMinorVersion = subsInfoPersistDTO.getClientPVminorVer();
            int clientSWType=subsInfoPersistDTO.getSwType();
            int clientPlatformType=subsInfoPersistDTO.getPlatformType();
            int dynamicQosFlag=subsInfoPersistDTO.getDynamicQosFlag();

            //vocoderId for PV=9
            int vocoderId = subsInfoPersistDTO.getVocoderId();

            String derivedKey = subsInfoPersistDTO.getDerivedKey();

            int serviceAuthStatusOP = subsInfoPersistDTO.getServiceStatusOp();

            int licenseType = subsInfoPersistDTO.getLicenseType();

            StringBuilder strBuffer = new StringBuilder();
            strBuffer.append(UPDATE_QRY);
            if (null != networkName && !networkName.isEmpty()) {
            	strBuffer.append(SUBSCR_NAME).append("=?, ");
            }
            strBuffer.append(CORP_SUBSCRIPTION_TYPE).append("=?, ");
            strBuffer.append(PUBLIC_SUBSCRIPTION_TYPE).append("=?, ");
            strBuffer.append(SERVICE_AUTH_STATUS).append("=?");
            strBuffer.append(", ");
            strBuffer.append(LAST_PROFILE_UPDATE_TIME).append("=?");
            if (imei != null) {
                strBuffer.append(", ");
                strBuffer.append(IMEI).append("=?");
            }
            if (clientPassword != null) {
                strBuffer.append(", ");
                strBuffer.append(CLIENT_PASSWORD).append("=?");
            }
            if (userAgent != null) {
                strBuffer.append(", ");
                strBuffer.append(USER_AGENT).append("=?");
            }
            if (vocoderId != 0){
                strBuffer.append(", ");
                strBuffer.append(VOCODERID).append("=?");
            }
            strBuffer.append(", ");
            strBuffer.append(CLIENTPV_MAJORVERSION).append("=?");
            strBuffer.append(", ");
            strBuffer.append(CLIENTPV_MINORVERSION).append("=?");
            if (lastActivationTime != null) {
                strBuffer.append(" , ");
                strBuffer.append(LAST_ACTIVATION_TIME).append("=?");
            }

            strBuffer.append(", ");
            strBuffer.append(CLIENT_SW_INF).append("=?");
            strBuffer.append(" , ");
            strBuffer.append(CLIENT_PLATFORM_TYPE).append("=?");
            strBuffer.append(" , ");
            strBuffer.append(DYNAMIC_QOS_FLAG).append("=?");
            if (derivedKey != null) {
                strBuffer.append(", ");
                strBuffer.append(DERIVEDKEY).append("=?");
            }
            strBuffer.append(", ");
            strBuffer.append(SERVICE_STATUS_OP).append("=?");
            strBuffer.append(", ");
            strBuffer.append(CLIENT_TYPE).append("=?");
            strBuffer.append(", ");
            strBuffer.append(SUBS_FS1).append("=?");
            strBuffer.append(", ");
            strBuffer.append(CLIENT_FS1).append("=?");
            strBuffer.append(", ");
            strBuffer.append(ACTIVE_FS1).append("=?");
            strBuffer.append(", ");
            strBuffer.append(OPS_FS1).append("=?");
            strBuffer.append(", ");
            strBuffer.append(CORPADMIN_FS1).append("=?");
            strBuffer.append(", ");
            strBuffer.append(SUBS_FS2).append("=?");
            strBuffer.append(", ");
            strBuffer.append(CLIENT_FS2).append("=?");
            strBuffer.append(", ");
            strBuffer.append(ACTIVE_FS2).append("=?");
            strBuffer.append(", ");
            strBuffer.append(OPS_FS2).append("=?");
            strBuffer.append(", ");
            strBuffer.append(CORPADMIN_FS2).append("=?");

            if (licenseType != 0){
                strBuffer.append(", ").append(LICENSE_TYPE).append("=?");
            }
            //Privacy Opt Status changes start
            knLogger.info(methodName, "pvMajorVersion- ", pvMajorVersion);
			if (pvMajorVersion >= KnConstants.PROTOCOL_VERSION_16_X
					&& subsInfoPersistDTO.getPrivacyExecutorBasedonFlag() != null&& subsInfoPersistDTO.getPrivacyExecutorBasedonFlag() == 1)
            {
                strBuffer.append(", ");
                strBuffer.append(PRIVACY_OPT_STATUS).append("=?");

                knLogger.info(methodName, "strBuffer- ", strBuffer);
            }
            //Privacy Opt Status changes end
            strBuffer.append(" WHERE ").append(MDN).append("=?");

            query = strBuffer.toString();
            knLogger.debug(methodName," query :",query);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);
            int columnIndex = 0;
            for (Map.Entry mdnInfo : mdnActivsFsMap.entrySet()) {
                if (null != networkName && !networkName.isEmpty()) {
                    //multilingual revert change
                    networkName = new String(networkName.getBytes("UTF-8"), "8859_1");
                    pStmt.setString(++columnIndex, networkName);
                }
                pStmt.setInt(++columnIndex, corpSubscriptionType);
                pStmt.setInt(++columnIndex, publicSubscriptionType);
                pStmt.setInt(++columnIndex, serviceAuthStatus);
                pStmt.setLong(++columnIndex, lastProfileUpdateTime);
                if (imei != null) {
                    pStmt.setString(++columnIndex, imei);
                }
                if (clientPassword != null) {
                    pStmt.setString(++columnIndex, clientPassword);
                }
                if (userAgent != null) {
                    pStmt.setString(++columnIndex, userAgent);
                }
                if (vocoderId != 0){
                    pStmt.setInt(++columnIndex, vocoderId);
                }
                pStmt.setInt(++columnIndex, pvMajorVersion);
                pStmt.setInt(++columnIndex, pvMinorVersion);
                if (lastActivationTime != null) {
                    pStmt.setLong(++columnIndex, lastActivationTime);
                }
                pStmt.setInt(++columnIndex, clientSWType);
                pStmt.setInt(++columnIndex, clientPlatformType);

                pStmt.setInt(++columnIndex, dynamicQosFlag);
                if (derivedKey != null) {
                    pStmt.setString(++columnIndex, derivedKey);
                }
                pStmt.setInt(++columnIndex, serviceAuthStatusOP);
                pStmt.setInt(++columnIndex, subsInfoPersistDTO.getSubsClientType());
                pStmt.setLong(++columnIndex, KnGeneralUtil.convertHexStringToLong(subsInfoPersistDTO.getSubsFS2()));
                pStmt.setLong(++columnIndex, KnGeneralUtil.convertHexStringToLong(subsInfoPersistDTO.getClientFS2()));
                pStmt.setLong(++columnIndex, KnGeneralUtil.convertHexStringToLong(mdnInfo.getValue().toString()));
                pStmt.setLong(++columnIndex, KnGeneralUtil.convertHexStringToLong(subsInfoPersistDTO.getOpsFS2()));
                pStmt.setLong(++columnIndex, KnGeneralUtil.convertHexStringToLong(subsInfoPersistDTO.getCorpAdminFS2()));
                pStmt.setString(++columnIndex, KnGeneralUtil.getFeatureSet(subsInfoPersistDTO.getSubsFS2()));
                pStmt.setString(++columnIndex, KnGeneralUtil.getFeatureSet(subsInfoPersistDTO.getClientFS2()));
                pStmt.setString(++columnIndex, KnGeneralUtil.getFeatureSet(mdnInfo.getValue().toString()));
                pStmt.setString(++columnIndex, KnGeneralUtil.getFeatureSet(subsInfoPersistDTO.getOpsFS2()));
                pStmt.setString(++columnIndex, KnGeneralUtil.getFeatureSet(subsInfoPersistDTO.getCorpAdminFS2()));
                if (licenseType != 0){
                    pStmt.setInt(++columnIndex, licenseType);
                }
                knLogger.info(methodName, "--->subsInfoPersistDTO.getPrivacyExecutorBasedonFlag()- ", subsInfoPersistDTO.getPrivacyExecutorBasedonFlag() );
                knLogger.info(methodName, "pvMajorVersion- ", pvMajorVersion);
                if(pvMajorVersion>=KnConstants.PROTOCOL_VERSION_16_X && subsInfoPersistDTO.getPrivacyExecutorBasedonFlag() != null&&subsInfoPersistDTO.getPrivacyExecutorBasedonFlag() == 1) {
                    knLogger.info(methodName, "subsInfoPersistDTO.isFlagForPrivacy()- ", subsInfoPersistDTO.isFlagForPrivacy());
                    if (subsInfoPersistDTO.isFlagForPrivacy() == true) {
                        pStmt.setInt(++columnIndex, 0);
                    } else {
                        pStmt.setInt(++columnIndex, 1);
                    }
                    knLogger.info(methodName, "pStmt- ", pStmt);
                }
                pStmt.setString(++columnIndex, mdnInfo.getKey().toString());
                pStmt.addBatch();
                columnIndex = 0;
            }
            pStmt.executeBatch();
            knLogger.debug(methodName, "QUERY: Executed - ");

        } catch (SQLException sqlE) {
            throw KnDbUtil.processException(sqlE, "Failed to activate Subscriber - " + sqlE.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } catch (Exception e) {
            throw KnDbUtil.processException(e, "Failed to activate Subscriber - " + e.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closePrepareStmt(pStmt);
        }
        knLogger.exit(methodName);
    }

    /**
     * method to update the Subscriber Profile.
     *
     * @param persistenceDTO IPersistenceDTO
     * @param persisterTxn   KnPersisterTxn
     * @throws KnDAOException DB layer Exception
     */
    public void updateProfileMdnDetails(IPersistenceDTO persistenceDTO,List<String> UserProfileMdns, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateProfileMdnDetails(IPersistenceDTO, KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        boolean ownedTxn = false;
        knLogger.debug(methodName, "ENTRY: update Profile Mdn Details with DTO - ", persistenceDTO,"UserProfileMdns--->",UserProfileMdns);

        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }
            KnSubsProfilePersistDTO subsProfilePersistDTO = (KnSubsProfilePersistDTO) persistenceDTO;
            String networkName = subsProfilePersistDTO.getNetworkName();
            int corpSubscriptionType = subsProfilePersistDTO.getCorporateSubscriptionType();
            int publicSubscriptionType = subsProfilePersistDTO.getPublicSubscriptionType();

            StringBuilder queryBuffer = new StringBuilder();
            queryBuffer.append(UPDATE_QRY);
            if (networkName != null) {
                queryBuffer.append(SUBSCR_NAME).append("=?, ");
            }
            queryBuffer.append(CORP_SUBSCRIPTION_TYPE).append("=?, ");
            queryBuffer.append(PUBLIC_SUBSCRIPTION_TYPE).append("=?, ");
            queryBuffer.append(SUBS_FS1).append("=?, ");
            queryBuffer.append(SUBS_FS2).append("=? ");
            queryBuffer.append(" WHERE ").append(MDN).append("=?");
            query = queryBuffer.toString();

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);

            pStmt = conn.prepareStatement(query);
            int columnIndex = 0;
            for (String profileMdn : UserProfileMdns) {
            if (networkName != null) {
            	//multilingual revert change
            	networkName = new String(subsProfilePersistDTO.getNetworkName().getBytes("UTF-8"),"8859_1");
                pStmt.setString(++columnIndex, networkName);
            }
            pStmt.setInt(++columnIndex, corpSubscriptionType);
            pStmt.setInt(++columnIndex, publicSubscriptionType);
            pStmt.setLong(++columnIndex, KnGeneralUtil.convertHexStringToLong(subsProfilePersistDTO.getSubsFS2()));
            pStmt.setString(++columnIndex, KnGeneralUtil.getFeatureSet(subsProfilePersistDTO.getSubsFS2()));
            pStmt.setString(++columnIndex, profileMdn);

            knLogger.debug(methodName, "pStmt- ", pStmt);
            pStmt.addBatch();
            columnIndex = 0;
        }
            knLogger.debug(methodName, "QUERY: Executing the Query - ", query);
            pStmt.executeBatch();
            knLogger.debug(methodName, "Query: Executed ");

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }

        } catch (KnDAOException dbConne) {
            knLogger.error(methodName, "DAO Exception occurred");
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw dbConne;
        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQL Exception occurred");
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(sqlE, "Failed to  update Profile Mdn Details - " + sqlE.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to  update Profile Mdn Details - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closePrepareStmt(pStmt);
            knLogger.debug(methodName, "EXIT:  updateProfileMdnDetails");
        }
    }

	@Override
	public void insert(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
		// TODO Auto-generated method stub

	}

    public Map<String, Collection<KnDocChangeListDTO>> profileMdnEtagUpdate(List<String> profileMdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "DAO.profileMdnEtagUpdate()";
        boolean ownedTxn = false;
        Connection conn;
        PreparedStatement pstmt = null;
        StringBuilder selectQuery = new StringBuilder(GET_SUBS_MCID_UPDTS_BY_MDNS);
        String updateQuery = null;
        ResultSet rs = null;
        String query = null;
        int index = 1;
        knLogger.debug(methodName, "ENTRY: profileMdnList -", profileMdnList);
        Map<String, Collection<KnDocChangeListDTO>> mdnDocMap=new HashMap<>();
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            selectQuery.append(MDN).append(" IN ").append("(MDNLIST);");
            query = selectQuery.toString();
            query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(profileMdnList, query, "MDNLIST");
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            knLogger.debug(methodName, "Query: Executing - ", query);
             
            for(String profileMdn : profileMdnList)
            	pstmt.setString(index++, profileMdn);
            
            rs = pstmt.executeQuery();
            long lastProfileUpdateTime = Calendar.getInstance().getTimeInMillis();
            Collection<KnDocChangeListDTO> dbRecList=null;
            updateQuery = UPDATE_SUBS_PROFILE_LAST_UPDATE_TIME;
            pstmt = conn.prepareStatement(updateQuery);
                while (rs.next()) {
                    String mdn = rs.getString(1).trim();
                    String activeFs=rs.getString(7)!=null?rs.getString(7): KnGeneralUtil.convertLongToHexString(rs.getLong(5));
                    boolean upmBit = KnGeneralUtil.getFeatureBitValue(activeFs,USER_PROFILE_MGMT_BIT);
                    int majorPv = rs.getInt(6);
                    //mdns pv >18 or pv 19 onwards and umfit should be enabled
                    if(majorPv>PROTOCOL_VERSION_18_X||upmBit){
                        pstmt.setLong(1, lastProfileUpdateTime);
                        pstmt.setString(2, mdn);
                        pstmt.addBatch();
                        dbRecList=new ArrayList<>();
                        dbRecList.addAll(buildMCSDOC(new String(rs.getBytes(2), StandardCharsets.UTF_8)
                                ,mdn
                                ,String.valueOf(rs.getInt(5))
                                ,String.valueOf(lastProfileUpdateTime)
                                ,String.valueOf(rs.getLong(3))
                                ,rs.getInt(4)
                                ,null)
                        );
                        mdnDocMap.put(mdn,dbRecList);
                    }
                }
                pstmt.executeBatch();
            knLogger.debug(methodName, "Query executed successfully mdnDocMap:-",mdnDocMap);

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
        } catch (KnDAOException dbConne) {
            knLogger.error(methodName, "DAO Exception occurred");
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw dbConne;
        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQL Exception occurred");
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(sqlE, "Failed  - " + sqlE.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, selectQuery.toString());
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, selectQuery.toString());
        } finally {
            KnDbUtil.closeResultSet(rs);
            knLogger.debug(methodName, "EXIT: mdnDocMap",mdnDocMap);
        }
        return mdnDocMap;
    }

    public List<String> getBaseMdnByProfileMdn(List<String> profileMdnList,KnPersisterTxn persistTxn)
            throws KnDAOException {
        String methodName = "getBaseMdnByProfileMdn(String, KnPersisterTxn)";
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String GET_BASE_MDN_BY_PROFILE_MDN="SELECT MDN FROM DG.POCSUBSCRINFO WHERE " +
                "MC_ID IN (SELECT MC_ID FROM DG.POCSUBSCRINFO WHERE MDN IN ( ";
        StringBuilder query = new StringBuilder(GET_BASE_MDN_BY_PROFILE_MDN);
        List<String> baseMdnList=new ArrayList<>();

        knLogger.debug(methodName, "ENTRY: getGetBaseMdnByProfileMdn ,profileMdnList -",profileMdnList);
        try {
            query.append(formCommaSeperatedIdList(profileMdnList))
                    .append(" )  ) AND (USERPROFILEINDEX=0 OR USERPROFILEINDEX IS NULL)");
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query.toString());
            knLogger.debug(methodName, "Query: Executing - ", query.toString());
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Query: Executed ");
            while (rs.next()) {
                baseMdnList.add(rs.getString(1).trim());
            }
        } catch (KnDAOException dbConne) {
            knLogger.error(methodName, "DAO Exception occurred");
            throw dbConne;
        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQL Exception occurred");
            throw KnDbUtil.processException(sqlE, "Failed to getGetBaseMdnByProfileMdn - " + sqlE.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query.toString());
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to getGetBaseMdnByProfileMdn - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query.toString());
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePrepareStmt(pStmt);
            knLogger.debug(methodName, "EXIT : mdn-",KnGDPRTemplate.mdnList(baseMdnList));
        }
        return baseMdnList;
    }

    /**
     * Method to retrieve user profile Ids by profile mdns. This is read only method.
     * @param mdnList
     * @param persistTxn
     * @return
     * @throws KnDAOException
     */
    public KnOPSubsProfileInfoDTO selectUserProfileIdsByProfileMdns(List<String> mdnList, KnPersisterTxn persistTxn) throws KnDAOException{
        String methodName = "selectUserProfileIdsByProfileMdns(mdnList, KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        KnOPSubsProfileInfoDTO subsProfileInfoDto = new KnOPSubsProfileInfoDTO();
        knLogger.debug(methodName, "ENTRY: Select user profile id by profile mdn ",mdnList);
        try {
            StringBuilder sb = getUserProfileIdByProfileMdnQuery();
            sb.append(MDN).append(" IN ").append("(");
            sb.append(formCommaSeperatedIdList(mdnList)).append(");");

            query = sb.toString();
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pStmt = conn.prepareStatement(query);
            knLogger.debug(methodName, "Query: Executing - ", query);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Query: Executed ");
            Map<String, String> profileMdnUserProfileIdMap = new HashMap<>();
            while (rs.next()) {
                profileMdnUserProfileIdMap.put(rs.getString(MDN).trim(), rs.getString(USERPROFILEID));
            }
            subsProfileInfoDto.setProfileMdnUserProfileIdMap(profileMdnUserProfileIdMap);
            knLogger.debug(methodName, "returning user profile id by profile mdn ", subsProfileInfoDto, profileMdnUserProfileIdMap.size());
            return subsProfileInfoDto;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to select user profile id by profile mdn - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePrepareStmt(pStmt);
            knLogger.info(methodName, "EXIT :");
        }
    }

    private StringBuilder getUserProfileIdByProfileMdnQuery(){
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ")
                .append(MDN)
                .append(COMMA)
                .append(USERPROFILEID);
        sb.append(" FROM ").append(TABLENAME).append(" WHERE ");

        return sb;
    }

    public List<String> getProfileMdnListByBaseMdn(String baseMdn,KnPersisterTxn persistTxn)
            throws KnDAOException {
        String methodName = "getProfileMdnListByBaseMdn()";
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String query = null;
        String mdn=null;
        List<String> profileMdnList=new ArrayList<>();
        knLogger.debug(methodName, "ENTRY: baseMdn :",KnGDPRTemplate.mdn(baseMdn));
        try {
            query = GET_PROFLEMDNS_BY_BASEMDN;
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pStmt = conn.prepareStatement(query);
            pStmt.setString(1, baseMdn);
            knLogger.debug(methodName, "Query: Executing - ", query);
            rs = pStmt.executeQuery();
            while (rs.next()) {
                profileMdnList.add(rs.getString("MDN").trim());
            }
        } catch (KnDAOException dbConne) {
            knLogger.error(methodName, "DAO Exception occurred");
            throw dbConne;
        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQL Exception occurred");
            throw KnDbUtil.processException(sqlE, "Failed - " + sqlE.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePrepareStmt(pStmt);
            knLogger.debug(methodName, "EXIT profileMdnList :  ",KnGDPRTemplate.mdnList(profileMdnList));
        }
        return profileMdnList;
    }

    public void clearUserProfileAssignment(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "clearUserProfileAssignment(List<String>, KnPersisterTxn)";
        Connection conn;
        PreparedStatement pStmt = null;
        String query = null;
        knLogger.debug(methodName, "ENTRY: mdnList - ", KnGDPRTemplate.mdnList(mdnList));
        if (mdnList == null || mdnList.isEmpty()) {
            knLogger.debug(methodName, "No profile mdns found for cleanup");
            return;
        }
        try {
            StringBuilder queryBuffer = new StringBuilder();
            queryBuffer.append(UPDATE_QRY);
            queryBuffer.append(USERPROFILEID).append("=? ,");
            queryBuffer.append(ISDEFAULTPROFILE).append("=? ,");
            queryBuffer.append(LAST_PROFILE_UPDATE_TIME).append("=? ");
            queryBuffer.append(" WHERE ").append(MDN).append(" IN (").append(formCommaSeperatedIdList(mdnList)).append(")");
            query = queryBuffer.toString();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);
            pStmt.setNull(1, Types.VARCHAR);
            pStmt.setInt(2, 0);
            pStmt.setLong(3, Calendar.getInstance().getTimeInMillis());
            knLogger.debug(methodName, "QUERY: Executing the query - ", query);
            pStmt.executeUpdate();
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to clear user profile assignment - " + e.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closePrepareStmt(pStmt);
            knLogger.debug(methodName, "EXIT: clearUserProfileAssignment");
        }
    }

    public void updateToPrivacyOptStatus(Map<String,Integer> mapListForPrivacy, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateToPrivacyOptStatus()";
        Connection conn;
        PreparedStatement pstmt = null;
        knLogger.debug(methodName, "ENTRY: mapListForPrivacy -", KnGDPRTemplate.mapKeyMdn(mapListForPrivacy));
        final String query=" UPDATE DG.POCSUBSCRINFO SET PRIVACY_OPT_STATUS=?,LASTPROFILEUPDATETIME = ? WHERE MDN=? ";
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            knLogger.debug(methodName, "QUERY: Executing the query - ", query);
            for(String mdn:mapListForPrivacy.keySet()) {
                pstmt.setInt(1, mapListForPrivacy.get(mdn));
                pstmt.setLong(2, System.currentTimeMillis());
                pstmt.setString(3, mdn);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }  catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to update Privacy Opt Status - " + e.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closePrepareStmt(pstmt);
            knLogger.debug(methodName, "EXIT: update Privacy Opt Status");
        }
    }

    public void updateDefaultProfileFlag(String mcId,int corpId,int defaultProfile, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateDefaultProfileFlag(String,String,int)";
        Connection conn;
        PreparedStatement pStmt = null;
        String query = null;
        knLogger.info(methodName, "ENTRY:", KnGDPRTemplate.mcId(mcId),corpId,defaultProfile);
        try {
            StringBuilder queryBuffer = new StringBuilder();
            queryBuffer.append(UPDATE_QRY);
            queryBuffer.append(ISDEFAULTPROFILE).append("=?");
            queryBuffer.append("WHERE ")
                    .append(MC_ID).append("=? AND ").append(CORP_ID).append("=?");
            query = queryBuffer.toString();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);
            pStmt.setInt(1, defaultProfile);
            pStmt.setBytes(2, mcId.getBytes(StandardCharsets.UTF_8));
            pStmt.setInt(3, corpId);
            knLogger.debug(methodName, "QUERY: Executing the query - ", query);
            pStmt.executeUpdate();
        }  catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to update ISDEFAULTPROFILE - " + e.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closePrepareStmt(pStmt);
        }
    }
        public int getMaxUserProfileIndex(String baseMdn,KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "getMaxUserProfileIndex()";
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String query = null;
        String mdn=null;
        int maxUserProfileIndex = 0;
        knLogger.debug(methodName, "ENTRY: baseMdn :",KnGDPRTemplate.mdn(baseMdn));
        try {
            query = GET_MAX_USERPROFILEINDEX;
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pStmt = conn.prepareStatement(query);
            pStmt.setString(1, baseMdn);
            knLogger.debug(methodName, "Query: Executing - ", query);
            rs = pStmt.executeQuery();
            while (rs.next()) {
                maxUserProfileIndex = rs.getInt(1);
            }
        } catch (KnDAOException dbConn) {
            knLogger.error(methodName, "DAO Exception occurred");
            throw dbConn;
        } catch (SQLException sqlEx) {
            knLogger.error(methodName, "SQL Exception occurred");
            throw KnDbUtil.processException(sqlEx, "Failed - " + sqlEx.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePrepareStmt(pStmt);
            knLogger.debug(methodName, "EXIT getMaxUserProfileIndex :  ",maxUserProfileIndex);
        }
        return maxUserProfileIndex;
    }

    public Map<String, String> getBaseMdnListForRequestingMdnList(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getBaseMdnListForRequestingMdnList(mdnList, KnPersisterTxn)";
        String query = null;
        String queryProfileMdnList = null;
        Connection conn;
        Connection connec;
        PreparedStatement pStmt = null;
        PreparedStatement ptmt = null;
        ResultSet rs = null;
        ResultSet rset = null;
        knLogger.debug(methodName, "ENTRY: ", mdnList);
        try {
            query = GET_MC_ID_LIST_BY_REQUESTING_MDN_LIST.replaceAll(MDN_LIST, KnGeneralUtil.getComSepList(mdnList));
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pStmt = conn.prepareStatement(query);
            knLogger.debug(methodName, "Query: Executing - ", query);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Query: Executed ");
            Map<String, String> profileMdnListWithMcIds = new HashMap<>();
            Map<String, String> finalMdnListWithBaseAndProfile = new HashMap<>();
            while (rs.next()) {
                int userProfileIndex = rs.getInt(USERPROFILEINDEX);
                if (userProfileIndex == 0) {
                    finalMdnListWithBaseAndProfile.put(rs.getString(MDN).trim(), rs.getString(MDN).trim());
                } else {
                    profileMdnListWithMcIds.put(rs.getString(MDN).trim(), new String(rs.getBytes(MC_ID), StandardCharsets.UTF_8));
                }
            }
            knLogger.info(methodName, "Retrieved count of profileMdnListWithMcIds ", profileMdnListWithMcIds.size());

            if (0 < profileMdnListWithMcIds.size()) {
                queryProfileMdnList = GET_BASE_MDN_LIST_BY_MC_ID_LIST.replaceAll(MC_ID_LIST, KnGeneralUtil.getComSepList(profileMdnListWithMcIds.values()));
                connec = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
                ptmt = connec.prepareStatement(queryProfileMdnList);
                knLogger.debug(methodName, "Query: Executing - ", queryProfileMdnList);
                rset = ptmt.executeQuery();
                knLogger.debug(methodName, "Query: Executed ");
                Map<String, String> baseMdnListWithProfMcIds = new HashMap<>();
                while (rset.next()) {
                    baseMdnListWithProfMcIds.put(new String(rset.getBytes(MC_ID), StandardCharsets.UTF_8), rset.getString(MDN).trim());
                }

                knLogger.debug(methodName, "Retrieved  count of baseMdnListWithProfMcIds ", baseMdnListWithProfMcIds.size());
                if (baseMdnListWithProfMcIds.size() > 0) {
                    for (Map.Entry<String, String> entry : profileMdnListWithMcIds.entrySet()) {
                        if (baseMdnListWithProfMcIds.containsKey(entry.getValue())) {
                            String mdn = baseMdnListWithProfMcIds.get(entry.getValue());
                            finalMdnListWithBaseAndProfile.put(entry.getKey(), mdn);
                        }
                    }
                }

            }

            knLogger.info(methodName, "Returning  count of finalMdnListWithBaseAndProfile ", finalMdnListWithBaseAndProfile.size());
            return finalMdnListWithBaseAndProfile;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to getBaseMdnListForRequestingMdnList - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeResultSet(rset);
            KnDbUtil.closePrepareStmt(pStmt);
            KnDbUtil.closePrepareStmt(ptmt);
            knLogger.info(methodName, "EXIT :");
        }
    }

    /**
     * method to retrieve the Subscriber Profile if Exist
     *
     * @param mdn        String
     * @param persistTxn KnPersisterTxn
     * @return KnOPSubsProfileInfoDTO
     * @throws KnDAOException DB Layer Exception
     */
    public KnOPSubsProfileInfoDTO selectSubscriberProfileIfExist(String mdn, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "selectSubscriberProfileIfExist(IPersistenceDTO, KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        KnOPSubsProfileInfoDTO subsProfileInfoDto = new KnOPSubsProfileInfoDTO();
        knLogger.debug(methodName, "ENTRY: Select Subscriber Profile mdn", KnGDPRTemplate.mdn(mdn), "persistTxn ", persistTxn);
        try {
            StringBuilder strBuffer = getSelectSubsProfileQuery();
            strBuffer.append(MDN).append("=?");

            query = strBuffer.toString();

            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            knLogger.debug(methodName, "select...>>>selectSubscriberProfileIfExist ... ");
            pStmt = conn.prepareStatement(query);
            pStmt.setString(1, mdn);

            knLogger.debug(methodName, "Query: Executing - ", query, ", mdn - ", KnGDPRTemplate.mdn(mdn));
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Query: Executed ");

            if (rs.next()) {
                subsProfileInfoDto = new KnOPSubsProfileInfoDTO();
                getSelectSubsProfileResult(subsProfileInfoDto, rs);
            }
            return subsProfileInfoDto;
        } catch (KnDAOException dbConne) {
            knLogger.error(methodName, "DAO Exception occurred", dbConne);
            throw dbConne;
        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQL Exception occurred", sqlE);
            throw KnDbUtil.processException(sqlE, "Failed to select Subscriber Profile - " + sqlE.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to select Subscriber Profile - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePrepareStmt(pStmt);
            knLogger.info(methodName, "EXIT : select Subscriber Profile if exist");
        }
    }

    /**
     * Method to retrieve the SERVICE_AUTH_STATUS for a given subscriber.
     *
     * @param mdns        String - The mobile directory number (MDN) of the subscriber.
     * @param persisterTxn KnPersisterTxn - The persistence transaction object.
     * @return int - The SERVICE_AUTH_STATUS of the subscriber.
     * @throws KnDAOException If an error occurs while accessing the database.
     */
    public Map<String, Integer> getSubscriberServiceAuthStatus(List<String> mdns, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubscriberServiceAuthStatus(List<String>, KnPersisterTxn)";
        Map<String, Integer> serviceAuthStatusMap = new HashMap<>();
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        boolean ownedTxn = false;

        // Prepare the query with 'IN' clause for multiple MDNs
        final String query = "SELECT " + MDN + ", " + SERVICE_AUTH_STATUS + " FROM " + TABLENAME + " WHERE " + MDN + " IN ("
                + mdns.stream().map(mdn -> "?").collect(Collectors.joining(",")) + ")";
        knLogger.debug(methodName, "ENTRY: Get Service Auth Status for MDNs - ", KnGDPRTemplate.mdnList(mdns));
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pStmt = conn.prepareStatement(query);

            // Set the MDNs in the prepared statement
            for (int i = 0; i < mdns.size(); i++) {
                pStmt.setString(i + 1, mdns.get(i));
            }

            knLogger.debug(methodName, "Query: Executing - ", query);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Query: Executed ");

            // Process the results and store in the map
            while (rs != null && rs.next()) {
                String mdn = rs.getString(MDN);
                int authStatus = rs.getInt(SERVICE_AUTH_STATUS);
                serviceAuthStatusMap.put(mdn, authStatus); // Add MDN and its status to the map
            }

            knLogger.debug(methodName, "result from query",serviceAuthStatusMap);


            if (ownedTxn) {
                knLogger.debug(methodName, "saving the transaction");
                persisterTxn.save();
            }

        } catch (KnDAOException dbConne) {
            knLogger.error(methodName, "DAO Exception occurred");
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw dbConne;
        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQL Exception occurred");
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(sqlE, "Failed to select service auth status from subscriber Profile - "
                    + sqlE.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to select service auth status from subscriber Profile - " + e.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePrepareStmt(pStmt);
            knLogger.debug(methodName, "EXIT : select service auth status from subscriber Profile");
        }

        return serviceAuthStatusMap;
    }


    public int fetchCorpSubscriberCnt(String extCorpid, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "fetchCorpSubscriberCnt(String, KnPersisterTxn)";
        int corpSubscriberCount = -1;
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String query = null;
        boolean ownedTxn = false;

        knLogger.debug(methodName, "ENTRY: Corp Subscriber count for ExtCorp ID - ", extCorpid);
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            query = CORP_SUBS_CNT_QRY_WITH_EXTCORPID;
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pStmt = conn.prepareStatement(query);
            pStmt.setString(1, extCorpid);
            knLogger.debug(methodName, "Query: Executing - ", query);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Query: Executed ");

            if (rs != null) {
                while (rs.next()) {
                    corpSubscriberCount = rs.getInt(1);
                }
            }

            if (ownedTxn) {
                knLogger.debug(methodName, "saving the transaction");
                persisterTxn.save();
            }

        } catch (KnDAOException dbConne) {
            knLogger.error(methodName, "DAO Exception occurred");
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw dbConne;
        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQL Exception occurred");
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(sqlE, "Failed to select Subscriber Profile - " + sqlE.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to select Subscriber Profile - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePrepareStmt(pStmt);
            knLogger.debug(methodName, "EXIT : select Subscriber Profile");
        }
        return corpSubscriberCount;
    }

    public KnCorporateProfilepersistDTO1 getCorpID(String extCorpId, KnPersisterTxn persisterTxn) throws KnDAOException, KnProvBOException {
        String methodName = "getCorporateAccountDetails(extCorpId, KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        boolean ownedTxn = false;
        KnCorporateProfilepersistDTO1 profilepersistDTO = new KnCorporateProfilepersistDTO1();
        knLogger.debug(methodName, "ENTRY:  extCorpId ", extCorpId);
        try {
            query = "SELECT CORPID FROM DG.POCCORPINFO WHERE extCorpId=?";
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pStmt = conn.prepareStatement(query);
            pStmt.setString(1, (extCorpId));
            knLogger.debug(methodName, "Query: Executing - ", query);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Query: Executed ");

            if (rs.next()) {
                profilepersistDTO.setCorpId(rs.getInt(1));
                knLogger.debug(methodName, "profilepersistDTO.setCorpId - " + profilepersistDTO.getCorpId());
            } else {
                knLogger.error(methodName, "Corporate Profile not found for CorpId - " + extCorpId);
                throw new KnDAOException(com.kodiak.xdms.server.common.resources.KnErrorCodes.DAO.ROW_NOT_FOUND, "Corporate Profile not found");
            }
            knLogger.debug(methodName, "Corporate Profile found - " + profilepersistDTO);
            if (ownedTxn) persisterTxn.save();
            return profilepersistDTO;

        } catch (KnPersistenceException e) {
            if (ownedTxn) persisterTxn.rollback();
            knLogger.error(methodName, "KnPersistenceException occured while retrieving CorpProfile Details for corpId - " + extCorpId + ", " + e);
            throw KnDbUtil.processException(e, "KnPersistenceException occured - " + e,
                    pttServerId, KnDAOSourceTypes.POCCORPINFO, query);

        } catch (KnDAOException e) {
            if (ownedTxn) persisterTxn.rollback();
            knLogger.error(methodName, "KnDAOException occured while retrieving CorpProfile Details for corpId - " + extCorpId + ", " + e);
            if (com.kodiak.xdms.server.common.resources.KnErrorCodes.DAO.ROW_NOT_FOUND.equals(e.getErrorCode())) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.INVALID_CORP_PROFILE, "Corporate Profile not found");
            } else if (e instanceof KnDBConnectionException) {
                knLogger.error(methodName, "DAO DBConnection Exception occurred :" + e);
                throw new KnProvBOException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, e.getMessage(), e);
            }

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected exception occured while retrieving CorpProfile Details for corpId - " + extCorpId + ", " + e);
            if (ownedTxn) persisterTxn.rollback();
            throw KnDbUtil.processException(e, "Failed to retrieve CorpProfile Details - " + e,
                    pttServerId, KnDAOSourceTypes.POCCORPINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePrepareStmt(pStmt);
            knLogger.info(methodName, "inputCorpId - " + extCorpId, "EXIT : CorpProfile - " + profilepersistDTO);
        }
        return profilepersistDTO;

    }

    public KnCorporateProfilepersistDTO1 getCorporateAccountDetails(int CorpId, KnPersisterTxn persisterTxn) throws KnDAOException, KnProvBOException {
        String methodName = "getCorporateAccountDetails(corpID, KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        boolean ownedTxn = false;
        KnCorporateProfilepersistDTO1 profilepersistDTO = new KnCorporateProfilepersistDTO1();
        knLogger.debug(methodName, "ENTRY:  corpID ", CorpId);
        try {
            // Column Max_Corp_Hierarchy_Levels has removed
            query = " SELECT CORPNAME, MAXCORPGROUPS, MAXMEMBERSPERCORPGROUP, MaxDispatchGrps,";
            query += " CORP_HIERARCHY, MAX_MEMBERS_PER_BG, MAXRADIOCHANNELS, CORPID, MAXCORPLISTS,";
            query += " MAXMEMBERSPERCORPLIST, MAXSUBSCRS, MAX_NNI_SUBSCRS, EXTCORPID, CORPFS2,";
            query += " MAXMEMBERSPERDISPATCHGRP, MAXZONES, MAXCHANNELSPERZONE, MAX_LRGAB_TALKGRP,";
            query += " MAX_GRP_PROFILES, MAX_USER_PROFILES, MAX_USERPROFILES_PERSUB,";
            query += " WEB_DISPATCH_ENABLED, INTEROP_LICENSE_TYPE,XDMCORPFS2_SET";
            query += " FROM DG.POCCORPINFO WHERE CorpId=?";

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pStmt = conn.prepareStatement(query);
            pStmt.setInt(1, (CorpId));
            knLogger.debug(methodName, "Query: Executing - ", query);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Query: Executed ");
            if (rs.next()) {
                profilepersistDTO.setCorporateName(rs.getString(1));
                profilepersistDTO.setMaxCorpGroups(rs.getString(2));
                profilepersistDTO.setMaxMemPerCorpGroup(rs.getString(3));
                profilepersistDTO.setMaxDispatchGroups(rs.getString(4));
                profilepersistDTO.setIsHierarchyEnabled(rs.getString(5));
                profilepersistDTO.setMaxMemPerBCGrp(rs.getString(6));
                profilepersistDTO.setMaxChannelAllowed(rs.getString(7));
                profilepersistDTO.setCorpId(rs.getInt(8));
                profilepersistDTO.setMaxCorpLists(rs.getString(9));
                profilepersistDTO.setMaxMemPerCorpList(rs.getString(10));
                profilepersistDTO.setMaxSubscribers(rs.getString(11));
                profilepersistDTO.setMaxExtSubsPerCorp(rs.getString(12));
                profilepersistDTO.setAccountId(rs.getString(13).trim());
                profilepersistDTO.setCorpFS2(rs.getString(14));
                profilepersistDTO.setMaxMemPerDispatchGroup(rs.getString(15));
                profilepersistDTO.setMaxZoneAllowed(rs.getString(16));
                profilepersistDTO.setMaxChannelsPerZone(rs.getString(17));
                profilepersistDTO.setMaxLgrGrp(rs.getString(18));
                profilepersistDTO.setMaxGrpProfiles(rs.getString(19));
                profilepersistDTO.setMaxUserProfiles(rs.getString(20));
                profilepersistDTO.setMaxAssignProfiles(rs.getString(21));
                // Fetch WEB_DISPATCH_ENABLED
                Integer webDispatchEnabled = (Integer) rs.getObject("WEB_DISPATCH_ENABLED");
                if (null != webDispatchEnabled) {
                    profilepersistDTO.setDispatchEnabled(webDispatchEnabled > 0);
                }
                // Fetch INTEROP_LICENSE_TYPE
                Integer interopLicenseType = (Integer) rs.getObject("INTEROP_LICENSE_TYPE");
                if (null != interopLicenseType) {
                    profilepersistDTO.setIsInterOpEnabled(interopLicenseType > 0);
                }
                profilepersistDTO.setXdmCorpFS2Set(rs.getString("XDMCORPFS2_SET"));
            } else {
                knLogger.error(methodName, "Corporate Profile not found for CorpId - " + CorpId);
                throw new KnDAOException(com.kodiak.xdms.server.common.resources.KnErrorCodes.DAO.ROW_NOT_FOUND, "Corporate Profile not found");
            }
            knLogger.debug(methodName, "Corporate Profile found - " + profilepersistDTO);
            if (ownedTxn) persisterTxn.save();
            return profilepersistDTO;

        } catch (KnPersistenceException e) {
            if (ownedTxn) persisterTxn.rollback();
            knLogger.error(methodName, "KnPersistenceException occured while retrieving CorpProfile Details for corpId - " + CorpId + ", " + e);
            throw KnDbUtil.processException(e, "KnPersistenceException occured - " + e,
                    pttServerId, KnDAOSourceTypes.POCCORPINFO, query);

        } catch (KnDAOException e) {
            if (ownedTxn) persisterTxn.rollback();
            if (com.kodiak.xdms.server.common.resources.KnErrorCodes.DAO.ROW_NOT_FOUND.equals(e.getErrorCode())) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.INVALID_CORP_PROFILE, "Corporate Profile not found");
            } else if (e instanceof KnDBConnectionException) {
                knLogger.error(methodName, "DAO DBConnection Exception occurred :" + e);
                throw new KnProvBOException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, e.getMessage(), e);
            }

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected exception occured while retrieving CorpProfile Details for corpId - " + CorpId + ", " + e);
            if (ownedTxn) persisterTxn.rollback();
            throw KnDbUtil.processException(e, "Failed to retrieve CorpProfile Details - " + e,
                    pttServerId, KnDAOSourceTypes.POCCORPINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePrepareStmt(pStmt);
            knLogger.info(methodName, "inputCorpId - " + CorpId, "EXIT : CorpProfile - " + profilepersistDTO);
        }
        return profilepersistDTO;

    }
    /**
     * method to retrieve the Subscriber count for the corporation
     *
     * @param corpId       String corporation Id
     * @param persisterTxn KnPersisterTxn
     * @return int corporation subscriber count
     * @throws KnDAOException DB Layer exception
     */
    public int getCorpSubscriberWithProfileCnt(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getCorpSubscriberCnt(int, KnPersisterTxn)";
        int corpSubscriberCount = -1;
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String query = null;
        boolean ownedTxn = false;

        knLogger.debug(methodName, "ENTRY: Corp Subscriber count for Corp ID - ", corpId);
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            query =  CORP_SUBS_CNT_QRY;
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pStmt = conn.prepareStatement(query);
            pStmt.setInt(1, corpId);
            knLogger.debug(methodName, "Query: Executing - ", query);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Query: Executed ");

            if (rs != null) {
                while (rs.next()) {
                    corpSubscriberCount = rs.getInt(1);
                }
            }

            if (ownedTxn) {
                knLogger.debug(methodName, "saving the transaction");
                persisterTxn.save();
            }

        } catch (KnDAOException dbConne) {
            knLogger.error(methodName, "DAO Exception occurred");
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw dbConne;
        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQL Exception occurred");
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(sqlE, "Failed to select Subscriber Profile - " + sqlE.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to select Subscriber Profile - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePrepareStmt(pStmt);
            knLogger.debug(methodName, "EXIT : select Subscriber Profile");
        }
        return corpSubscriberCount;
    }

    /**
     * Retrieves a list of group IDs for a given MDN from the database.
     *
     * @param mdn The Mobile Directory Number (MDN) of the subscriber.
     * @return A list of group IDs (`CORPGROUPID`) associated with the MDN.
     * @throws KnDAOException If an error occurs while accessing the database.
     */
    public List<Integer> getGroupIdsList(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getGroupIdsList(mdn)";
        knLogger.debug(methodName, "Entry : MDN - ", KnGDPRTemplate.mdn(mdn));
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Connection conn = null;
        List<Integer> groupIdsList = new ArrayList<>();
        String query = "SELECT CORPGROUPID FROM DG.CORPGROUPMEMBERLIST WHERE GRP_MODIFY_PERM = 1 AND MEMBERMDN =?;";
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            knLogger.debug(methodName, "Acquired DB connection");
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, mdn);
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                groupIdsList.add(rs.getInt("CORPGROUPID"));
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while querying CORPGROUPMEMBERLIST - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occurred while querying CORPGROUPMEMBERLIST - ", e);
            throw KnDbUtil.processException(e, "Failed while querying - " + e,
                    pttServerId, KnDAOSourceTypes.CORPGROUPMEMBERLIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.debug(methodName, "EXIT. groupIdsList size: ", groupIdsList.size());
        }
        return groupIdsList;
    }

    /**
     * Retrieves a list of shared group IDs for a given MDN from the database.
     *
     * @param mdn The Mobile Directory Number (MDN) of the subscriber.
     * @return A list of shared group IDs (`CORPGROUPID`) associated with the MDN.
     * @throws KnDAOException If an error occurs while accessing the database.
     */
    public List<Integer> getSharedGroupList(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSharedGroupList(mdn)";
        knLogger.debug(methodName, "Entry : MDN - ", KnGDPRTemplate.mdn(mdn));
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Connection conn = null;
        List<Integer> groupIdsList = new ArrayList<>();
        String query = "SELECT CORPGROUPMEMBERLIST.CORPGROUPID FROM DG.CORPGROUPMEMBERLIST JOIN DG.CORPGROUPINFO ON " +
                "CORPGROUPMEMBERLIST.CORPGROUPID = CORPGROUPINFO.CORPGROUPID WHERE CORPGROUPINFO.GROUP_SHARED = 1 " +
                "AND CORPGROUPMEMBERLIST.MEMBERMDN = ?;";

        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            knLogger.debug(methodName, "Acquired DB connection");
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, mdn);
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                groupIdsList.add(rs.getInt("CORPGROUPID"));
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while querying CORPGROUPMEMBERLIST - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occurred while querying CORPGROUPMEMBERLIST - ", e);
            throw KnDbUtil.processException(e, "Failed while querying - " + e,
                    pttServerId, KnDAOSourceTypes.CORPGROUPMEMBERLIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.debug(methodName, "EXIT. groupIdsList size: ", groupIdsList.size());
        }
        return groupIdsList;
    }

    /**
     * Retrieves group information for a list of corporate group IDs.
     *
     * @param corpGroupIds A list of corporate group IDs to query.
     * @return A map where the key is the group type and the value is a list of maps containing group IDs and their display names.
     * @throws KnDAOException If an error occurs while accessing the database.
     */
    public Map<String, List<Map<Integer, String>>> getGroupInfo(List<Integer> corpGroupIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getGroupInfo";
        knLogger.debug(methodName, "Entry: corpGroupIds size - ", corpGroupIds.size());
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Connection conn = null;
        StringBuilder queryBuilder = new StringBuilder("SELECT DISTINCT CORPGROUPID, GROUPTYPE, GROUPDISPLAYNAME FROM " +
                "DG.CORPGROUPINFO WHERE CORPGROUPID IN (");
        for (int i = 0; i < corpGroupIds.size(); i++) {
            queryBuilder.append(i == 0 ? "?" : ", ?");
        }
        queryBuilder.append(")");
        String query = queryBuilder.toString();

        Map<String, List<Map<Integer, String>>> groupInfoMap = new HashMap<>();
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            knLogger.debug(methodName, "Acquired DB connection");
            pstmt = conn.prepareStatement(query);
            for (int i = 0; i < corpGroupIds.size(); i++) {
                pstmt.setInt(i + 1, corpGroupIds.get(i));
            }
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                int corpGroupId = rs.getInt("CORPGROUPID");
                String groupType = rs.getString("GROUPTYPE");
                String groupName = rs.getString("GROUPDISPLAYNAME");

                groupInfoMap.putIfAbsent(groupType, new ArrayList<>());
                Map<Integer, String> groupDetails = new HashMap<>();
                groupDetails.put(corpGroupId, groupName);
                groupInfoMap.get(groupType).add(groupDetails);
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while querying CORPGROUPINFO - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occurred while querying CORPGROUPINFO - ", e);
            throw KnDbUtil.processException(e, "Failed while querying CORPGROUPINFO - " + e,
                    pttServerId, KnDAOSourceTypes.CORPGROUPINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.debug(methodName, "EXIT. Group info size: ", groupInfoMap.size());
        }
        return groupInfoMap;
    }

    /**
     * Retrieves additional device information for a given MDN.
     *
     * @param mdn The Mobile Directory Number (MDN) of the subscriber.
     * @return The additional device information (`DEVICEADDLINFO`) as a UTF-8 string, or null if no device information is found.
     * @throws KnDAOException If an error occurs while accessing the database.
     */
    public String getAddDeviceInfo(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getAddDeviceInfo";
        Connection conn = null;
        PreparedStatement pstmt = null;
        PreparedStatement pStmtAddl = null;
        ResultSet rs = null;
        ResultSet rsAdd = null;
        String deviceId = null;
        String deviceAddInfo = null;
        String query1 = null;
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            query1 = "SELECT DEVICEID FROM DG.DEVICE_INFO WHERE DEVICE_SUBSCR_MDN = ?";
            pstmt = conn.prepareStatement(query1);
            pstmt.setString(1, mdn);
            knLogger.debug(methodName, "Executing query - ", query1);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                deviceId = rs.getString("DEVICEID");
                if (deviceId != null && !deviceId.isEmpty()) {
                    break;
                }
            }
            if (deviceId == null || deviceId.isEmpty()) {
                knLogger.debug(methodName, "No DeviceId found for MDN - ", KnGDPRTemplate.mdn(mdn));
                return null;
            }
            String query2 = "SELECT DEVICEADDLINFO FROM DG.DEVICE_ADDLINFO WHERE DEVICEID = ?";
            pStmtAddl = conn.prepareStatement(query2);
            pStmtAddl.setString(1, deviceId);
            rsAdd = pStmtAddl.executeQuery();
            if (rsAdd.next() && rsAdd.getString("DEVICEADDLINFO") != null) {
                deviceAddInfo = new String(rsAdd.getBytes("DEVICEADDLINFO"), StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred - ", e);
            throw KnDbUtil.processException(e, "Failed while retrieving device info", pttServerId,
                    KnDAOSourceTypes.CORPGROUPMEMBERLIST, query1);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            if (rsAdd != null) {
                KnDbUtil.closeResultSet(rsAdd);
            }
            if (pStmtAddl != null) {
                KnDbUtil.closePreparedStatement(pStmtAddl);
            }
            knLogger.debug(methodName, "EXIT - deviceAddInfo - ", deviceAddInfo);
        }
        return deviceAddInfo;
    }

    /**
     * Retrieves configuration values for zones and channels for a given external corporate ID.
     * Falls back to default configuration values if no valid configuration is found.
     *
     * @param extCorpId The external corporate ID.
     * @return A map containing configuration values for "MAXZONES" and "MAXCHANNELSPERZONE".
     * @throws KnDAOException If an error occurs while accessing the database.
     */
    public Map<String, Integer> getZoneAndChannerConfigValues(String extCorpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getZoneAndChannerConfigValues";
        Connection conn = null;
        Connection connAdd = null;
        PreparedStatement pstmt = null;
        PreparedStatement pStmtAddl = null;
        ResultSet rs = null;
        ResultSet rsAdd = null;
        String query = null;
        Map<String, Integer> configMap = new HashMap<>();
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            query = "SELECT MAXZONES, MAXCHANNELSPERZONE FROM DG.POCCORPINFO WHERE EXTCORPID = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, extCorpId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                knLogger.debug(methodName, "Retrieved MAXZONES and MAXCHANNELSPERZONE for EXTCORPID - ", extCorpId);
                configMap.put("MAXZONES", rs.getInt("MAXZONES"));
                configMap.put("MAXCHANNELSPERZONE", rs.getInt("MAXCHANNELSPERZONE"));
            }
            if (!configMap.containsKey("MAXZONES") || !configMap.containsKey("MAXCHANNELSPERZONE")
                    || configMap.getOrDefault("MAXZONES", 0) == 0 || configMap.getOrDefault("MAXCHANNELSPERZONE", 0) == 0) {
                knLogger.debug(methodName, "No config found for EXTCORPID - ", extCorpId, ", falling back to default config");
                KnDbUtil.closeResultSet(rs);
                KnDbUtil.closePreparedStatement(pstmt);

                String fallbackQuery = "SELECT MAXZONES, MAXCHANNELSPERZONE FROM DG.XDMS_SVC_CONFIG";
                connAdd = persisterTxn.getDBConnection(pttServerId, true);
                pStmtAddl = connAdd.prepareStatement(fallbackQuery);
                rsAdd = pStmtAddl.executeQuery();
                if (rsAdd.next()) {
                    configMap.put("MAXZONES", rsAdd.getInt("MAXZONES"));
                    configMap.put("MAXCHANNELSPERZONE", rsAdd.getInt("MAXCHANNELSPERZONE"));
                }
            }
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred - ", e);
            throw KnDbUtil.processException(e, "Failed while retrieving config", pttServerId,
                    KnDAOSourceTypes.XDMS_SVC_CONFIG, query);
        } finally {
            KnDbUtil.closeResultSet(rsAdd);
            KnDbUtil.closePreparedStatement(pStmtAddl);
            knLogger.debug(methodName, "EXIT-->");
        }
        return configMap;
    }

    /**
     * Retrieves a list of broadcaster group IDs for a given MDN.
     *
     * @param mdn The Mobile Directory Number (MDN) of the subscriber.
     * @return A list of broadcaster group IDs (`CORPGROUPID`).
     * @throws KnDAOException If an error occurs while accessing the database.
     */
    public List<Integer> getBroadcasterGroupIds(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getBroadcasterGroupIds";
        List<Integer> groupIds = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            String query = "SELECT CORPGROUPID FROM DG.CORPGROUPMEMBERLIST " +
                    "WHERE IS_BROADCASTER = 1 AND MEMBERMDN = ?;";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, mdn);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                groupIds.add(rs.getInt("CORPGROUPID"));
            }
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred - ", e);
            throw KnDbUtil.processException(e, "Failed while retrieving broadcaster group IDs", pttServerId,
                    KnDAOSourceTypes.CORPGROUPMEMBERLIST, " ");
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.debug(methodName, "EXIT - broadcasterGroupIds: ", groupIds.size());
        }
        return groupIds;
    }

    /**
     * Inserts a collection of subscriber additional talk group information into the database.
     *
     * @param subsAddlTGInfoDTOS The collection of `KnSubsAddlTGInfoDTO` objects containing the data to be inserted.
     * @param persisterTxn       The transaction object used for database operations.
     * @throws KnDAOException If an error occurs during the database operation.
     */
    public void insertSubsAddlTGList(Collection<KnSubsAddlTGInfoDTO> subsAddlTGInfoDTOS, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "insertSubsAddlTGList";
        knLogger.debug(methodName, "ENTRY - subsAddlTGInfoDTOS: ", subsAddlTGInfoDTOS.size());
        PreparedStatement pstmt = null;
        String query = "INSERT INTO DG.SUBSCRPTTRADIOTGLIST (MDN, GROUPID, ZONEID, CHANNELID) VALUES (?, ?, ?, ?)";
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            for (KnSubsAddlTGInfoDTO dto : subsAddlTGInfoDTOS) {
                pstmt.setString(1, dto.getMdn());
                pstmt.setInt(2, dto.getGroupId());
                pstmt.setInt(3, dto.getZoneId());
                pstmt.setInt(4, dto.getChannelId());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            knLogger.debug(methodName, "Batch insert executed successfully.");
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occurred inserting the data -- > ", e);
            throw KnDbUtil.processException(e, "Failed while inserting the data -- >" + e,
                    pttServerId, KnDAOSourceTypes.CORPGROUPMEMBERLIST, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.debug(methodName, "EXIT - ");
        }
    }
    public void deleteSubsAddlInfo(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "deleteSubsAddlInfo(mdn,KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        knLogger.debug(methodName, "ENTRY: Delete Mdn from SubsAddlInfo ", KnGDPRTemplate.mdn(mdn), " Persist ", persisterTxn);

        try {
            query = DELETE_SUBS_ADDL_QUERY;
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);
            pStmt.setString(1, mdn);
            pStmt.executeUpdate();
            knLogger.debug(methodName, "Query: Executed: ", query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to delete Subscriber AddlInfo  - " + e.getMessage(), pttServerId, SUBS_ADDL_TABLENAME, query);
        } finally {
            KnDbUtil.closePrepareStmt(pStmt);
        }
    }

}
