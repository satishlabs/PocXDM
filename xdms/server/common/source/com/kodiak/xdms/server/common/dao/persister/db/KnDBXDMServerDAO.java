/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/******************************************************************************
 * File name:   KnDBXDMServerDAO.java
 * Subsystem:   Server Common
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ravi Shanker .P       Dec 16, 2010       7.0
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
 * *******************************************************************************/

package com.kodiak.xdms.server.common.dao.persister.db;

import com.kodiak.common.commdto.common.KnPTTSettingDocInfoDTO;
import com.kodiak.common.commdto.common.KnXDMDeviceAddlInfoDTO;
import com.kodiak.common.commdto.common.KnXDMDeviceProvDTO;
import com.kodiak.common.commdto.request.KnXDMOSMInfoRequestDTO;
import com.kodiak.common.commdto.request.KnXDMTalkGroupInfoDTO;
import com.kodiak.common.commdto.response.KnXDMSubsProfileRespDTO;
import com.kodiak.common.dao.KnConnectionException;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersistenceException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.dto.KnPendingTxnInfoDTO;
import com.kodiak.common.resources.KnErrorCodes;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.dbmgr.KnDBConst;
//import com.kodiak.frameworks.messaging.common.dto.KnMqServiceConfig;
import com.kodiak.dbmgr.KnDBManager;
import com.kodiak.frameworks.messaging.common.dto.KnMqServiceConfig;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.idgenerator.dao.KnTableInfoBean;
import com.kodiak.utilities.statusmgr.IStatusMgrNotifyIntf;
import com.kodiak.utilities.statusmgr.KnStatusManagerClient;
import com.kodiak.utilities.statusmgr.utils.KnStatusMgrConstants;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dao.persister.IXDMServerDAO;
import com.kodiak.xdms.server.common.dao.persister.db.tables.KnTablesRegistry;
import com.kodiak.xdms.server.common.dao.persister.db.tables.xdm.*;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.common.*;
import com.kodiak.xdms.server.common.dto.persistdat.*;
import com.kodiak.xdms.server.common.resources.KnConstants;
import com.kodiak.xdms.server.common.resources.KnDAOSourceTypes;
import com.kodiak.xdms.server.common.dto.common.KnCorpUserProfileMCPTTConfig;
import com.kodiak.xdms.server.common.dto.common.KnAddlTGInfoDTO;

import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.kodiak.common.resources.KnConstants.CONTACT_NOTIFY_SERVICE_TYPE;
import static com.kodiak.common.resources.KnConstants.GROUP_NOTIFY_SERVICE_TYPE;
import static com.kodiak.common.resources.KnConstants.USER_NOTIFY_SERVICE_TYPE;
import static com.kodiak.xdms.server.common.util.KnGeneralUtil.formCommaSeperatedIdList;

public class KnDBXDMServerDAO implements IXDMServerDAO, IStatusMgrNotifyIntf {
    private static final KnLogger knLogger = KnLogger.getLogger(KnDBXDMServerDAO.class);

    private String xdmPttServerId;
    KnGenInfoUtil genInfoUtil = null;
    private static final String APN_PROFILE_INFO_TABLE = "DG.APNPROFILEINFO";
    private static final String APN_INFO_TABLE = "DG.APNINFO";
    private static final String SUBSCRIBER_APN_INFO_TABLE = "DG.SUBSCRAPNINFO";

    private static final String ALIAS_MDN = "ALIAS_MDN";
    private static final String QPPPACKID = "QPPPACKID";
    private static final String SEGMENT_INDICATOR = "SEGMENT_INDICATOR";
    private static final String MC_PTTID = "MC_PTTID";
    private static final String POCSUBSCRIBER_INFO_TABLE = "DG.POCSUBSCRINFO";
    private static final String MDN_LIST = "MDNLIST";
    private static final String ACTIVE_FS2 = "ACTIVEFS2";
    private static final String  MC_VIDEOID = "MC_VIDEOID";
    private static final String  MC_DATAID = "MC_DATAID";
    private static final String  MC_ID = "MC_ID";
    private static final String  POC_HOME = "POCHOME";
    private static final String  XDMS_HOME = "XDMSHOME";
    private static final String  CORP_ID = "CORPID";
    private static final String  CORP_SUBSCRIPTION_TYPE = "CORPSUBSCRIPTIONTYPE";
    private static final String  CLIENT_TYPE = "CLIENT_TYPE";
    private static final String  CLIENT_FS1 = "CLIENTFS1";
    private static final String  ACTIVE_FS1 = "ACTIVEFS1";
    private static final String  XDMS_FS1 = "XDMSFS1";
    private static final String  CORPADMIN_FS1 = "CORPADMINFS1";
    private static final String  CLIENTPV_MAJORVERSION = "CLIENTPV_MAJORVERSION";
    private static final String  CLIENTPV_MINORVERSION = "CLIENTPV_MINORVERSION";
    private static final String  SUBS_FS2 = "SUBSFS2";
    private static final String  CLIENT_FS2 = "CLIENTFS2";
    private static final String  SERVICE_AUTH_STATUS = "SERVICEAUTHSTATUS";
    private static final String  SUBSCRIBER_NAME = "SUBSCRNAME";
    private static final String  USER_AGENT = "USERAGENT";
    private static final String  LAST_PROFILE_UPDATETIME = "LASTPROFILEUPDATETIME";

    private static final String TABLENAME_SIPPROXYSVCCONFIG = "DG.SIPPROXYSVCCONFIG";

    private static final String SIP_PROXY_URI = "SIPPROXYURI";
    private static final String PTT_SERVER_ID = "PTTSERVERID";
    private static final String CLIENT_CONNECTION_RETRY_INTERVAL = "CLIENTCONNECTIONRETRYINTERVAL";
    private static final String MAX_CLIENT_CONN_RETRY_ATTEMPTS = "MAXCLIENTCONNRETRYATTEMPTS";
    private static final String CLIENT_CONNECTION_SECURITY_LEVEL = "CLIENTCONNECTIONSECURITYLEVEL";
    private static final String CLIENT_SIP_TXN_TIMEOUT = "CLIENTSIPTXNTIMEOUT";
    private static final String CLIENT_SIP_REFER_TXN_TIMEOUT = "CLIENTSIPREFERTXNTIMEOUT";
    private static final String MIN_TCP_KA_TIMER_ON_WIFI = "MIN_TCP_KA_TIMER_ON_WIFI";
    private static final String WIFI_TCP_KA_TIMER_INCR_VAL = "WIFI_TCP_KA_TIMER_INCR_VAL";
    private static final String MAX_TCP_KA_TIMER_ON_WIFI = "MAX_TCP_KA_TIMER_ON_WIFI";
    private static final String WIFI_SSID_TIMEOUT_MAP_SIZE = "WIFI_SSID_TIMEOUT_MAP_SIZE";
    private static final String TCP_KA_TIMER_ON_MACRO_CELLULAR = "TCP_KA_TIMER_ON_MACRO_CELLULAR";
    private static final String DETECT_WIFI_NAT_TCP_TIMEOUT = "DETECT_WIFI_NAT_TCP_TIMEOUT";
    private static final String SIP_PROXY_URI_FOR_BCS = "SIPPROXYURIFORBCS";
    private static final String CAMERA_TYPE = "CAMERA_TYPE";
    private static final String UPMID_LIST = "UPMIDS";

    private static final String QRY_SELECT_XCAP_ROOT_URI = "SELECT APNID, XCAPROOTURI from DG.APNPROFILEINFO WHERE PTTSERVERID = ?";
    private static final String SELECT_APN_INFO = "SELECT APNNAME, APNID  FROM DG.APNINFO";

    private static final String SELECT_APN_ID = "SELECT APNID FROM DG.SUBSCRAPNINFO WHERE MDN = ?";

    private static final String SELECT_APNIDS = "SELECT MDN, APNID FROM DG.SUBSCRAPNINFO WHERE  MDN IN ";

    private static final String SELECT_DEFAULT_APN_NAME = " SELECT APNNAME  FROM DG.APNINFO  WHERE ISDEFAULT = ? ";

    private static final String SELECT_ACTIVATIONCODE_CONFIG = "SELECT CLIENT_TYPE, ACTCODE_LENGTH, ACTCODE_TYPE,ACTCODE_VALIDITY ,ACTCODE_EXPIRY, INTF_TYPE FROM DG.ACTIVATIONCODE_CONFIG WHERE PTTSERVERID = ? ";

    private static final String UPDATE_USAGE_BY_MDN_QRY = "UPDATE DG.PAMACCOUNT_POOL_USAGE SET USAGE=?  WHERE MDN=?";
    
    private static final String SELECT_TP_USER_VENDOR_QRY ="select MDN, THIRD_PARTY_USER, THIRD_PARTY_ACCT_ID from DG.THIRD_PARTY_USER_MDN_MAP TPU "
    		+ "JOIN DG.THIRD_PARTY_ACCOUNT_INFO TPA ON TPU.THIRD_PARTY_ID = TPA.THIRD_PARTY_ID WHERE MDN in (MDNLIST)";

    private static final String QRY_SELECT_DYNAMIC_QOS_FLAG = "SELECT DYNAMICQOSFLAG from DG.APNPROFILEINFO WHERE PTTSERVERID = ? AND APNID= ?";

    private static final String QRY_APN_FIELD_ROPERTIES = "SELECT DYNAMICQOSFLAG,XCAPROOTURI,APNID, PTXBUCKETURICELL, LOCDATAURICELL ,MCSXCAPROOTURI from DG.APNPROFILEINFO WHERE PTTSERVERID = ?";


    final String QUERY_SERVICE_FQDN_INFO = "SELECT CLUSTERID, SERVICE_FQDN  from DG.SERVICE_FQDN_INFO  where SERVICETYPE='SYNCGW' and SERVICEVER='1.0'";

    private static final String SELECT_CLIENT_TYPE_CONFIG = "SELECT CLIENT_TYPE, IS_ENABLED, ENABLE_SUPP_DEVICE_CHK FROM DG.CLIENT_TYPE_CONFIGURATION";

    private static final String SELECT_SUPP_VOCODER_PROFILE = "SELECT ProfileId, VocoderName, VocoderId, NumOfFrames, PTime, MaxPTime, Bitrate, Mode, ClockRate, MaxULBandwidth, MAXDLBandwidth, OctetAlignedMode, Is_Default from DG.SupportedVocoderProfiles";

    private static final String QUERY_MICROSVCS_COMMONCONFIG = "SELECT COUNTRYCODE, CLUSTERID, PARAMNAME, PARAMSCOPE, PATH, PARAMVALUE, LASTUPDATETIME FROM DG.MICROSVCS_COMMONCONFIG";

    private static final String QUERY_MICROSVCS_CLUSTERINFO = "SELECT COUNTRYCODE, CLUSTERID, ISCONFIGURED, PTXBUCKETURIWIFI, DESC, KODIAK_MAPS_WIFIURI, KODIAK_MAPS_GEO_WIFIURI, LOCDATAURIWIFI FROM  DG.MICROSVCS_CLUSTERINFO";

    private static final String QUERY_MICROSVCS_SERVICECONFIG = "SELECT COUNTRYCODE, CLUSTERID, SERVICETYPE, SERVICEVER, PARAMNAME, PARAMSCOPE, PATH, PARAMVALUE, LASTUPDATETIME FROM DG.MICROSVCS_SERVICECONFIG";

    private static AtomicInteger sequenceNum;
    private static boolean registerStatus = false;
    
	private static final String QRY_EXECUTING_MSG  = "QUERY: Executing - ";
	private static final String QRY_EXE_MSG  = "QUERY: Executed - ";
	private static final String SQL_EXCEPTION_MSG = "SQL Exception occurred";

	public static final String MDNLIST = "MDNLIST";
	public static final String MDN = "MDN";
	public static final String THIRD_PARTY_USER = "THIRD_PARTY_USER";
	public static final String THIRD_PARTY_ACCT_ID = "THIRD_PARTY_ACCT_ID";
	private static final String SELECT_ADDL_PROFILE_BY_PKGTYPE = "SELECT PROFILE_ID,PKG_ID,PKG_ID_TYPE  FROM DG.ADDL_PROFILE_INFO  ";

    private static final String SELECT_QPP_PCRF_PROFILE = "SELECT QPPPCRFPROFILEID, APNID, MCPTTIDENTIFIER, RESERVATIONPRIORITY  FROM DG.QPP_PCRF_PROFILE";

    private static final String QRY_SELECT_ALL = "SELECT APNID, XCAPROOTURI,CLIENTLOGURI,SIPPROXYURI,GEO_SIPPROXYURI,GEOREG_PRIMF5URI,GEOREG_GEOF5URI,PRI_WSPROXYURI,GEO_WSPROXYURI,GEOREG_PRI_WSPROXYURI,GEOREG_GEO_WSPROXYURI,DYNAMICQOSFLAG,PTTBUCKETURICELL,PTXBUCKETURICELL,LOCDATAURICELL,KODIAK_MAPS_URI,KODIAK_MAPS_GEO_URI,OIDCXCAPROOTURI,RESOURCEPRIORITYEMERG,RESOURCEPRIORITYNORMAL,ESRIMAPS_URI,ESRIMAPS_GEO_URI,NONCALLSIPPROXYURI,GEO_NONCALLSIPPROXYURI,MCSXCAPROOTURI,KMSWSURI from DG.APNPROFILEINFO WHERE PTTSERVERID = ? AND APNID = ?";

    private static final String QRY_INSERT_DEVICE_INFO = "INSERT INTO DG.DEVICE_INFO ( DEVICEID, DEVICESTATUS, DEVICEACTTS, DEVICELASTUSED, DEVICEDIGESTPASSWD,DEVICE_CREATED_AS,DEVICE_CLIENTID,DEVICE_SHARED,DEVICE_IMPI,DEVICE_TYPE,DEVICE_NAME,REQ_DEVICEID,CORPID, DEVICE_SUBSCR_MDN) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?);";

    private static final String QRY_INSERT_DEVICE_ADDITIONAL_INFO = "INSERT INTO DG.DEVICE_ADDLINFO ( DEVICEID, DEVICE_SERIAL_NO, IMEI1, IMEI2, DEVICE_VERSION, DEVICEADDLINFO) VALUES (?,?,?,?,?,?);";

    private static final String QRY_UPDATE_DEVICE_ADDITIONAL_INFO = "UPDATE DG.DEVICE_ADDLINFO SET DEVICE_SERIAL_NO = ?, IMEI1 = ?, IMEI2 = ?, DEVICE_VERSION = ?, DEVICEADDLINFO = ? WHERE DEVICEID = ?;";

    private static final String QRY_UPDATE_DEVICE_INFO = "UPDATE DG.DEVICE_INFO SET REQ_DEVICEID = ?, DEVICELASTUSED = ?, DEVICE_TYPE = ?, DEVICE_SUBSCR_MDN = ? WHERE DEVICEID = ?;";

    private static final String QRY_INSERT_DEVICEIMPIINFO = "INSERT INTO DG.DEVICEIMPIINFO(DEVICE_IMPI,DEVICE_IMPU) values(?,?);";

    private static final String QRY_DELETE_DEVICE_INFO = "DELETE FROM DG.DEVICE_INFO WHERE DEVICEID=?;";

    private static final String QRY_DELETE_DEVICE_ADDLINFO = "DELETE FROM DG.DEVICE_ADDLINFO WHERE DEVICEID=?;";

    private static final String QRY_DELETE_DEVICEIMPIINFO = "DELETE FROM DG.DEVICEIMPIINFO WHERE DEVICE_IMPI= ( SELECT DEVICE_IMPI FROM DG.DEVICE_INFO WHERE DEVICEID = ? );";

    private static final String QRY_SEL_DEVICEIMPL = "SELECT DEVICE_IMPU FROM DG.DEVICEIMPIINFO WHERE DEVICE_IMPI = ?;";

    private static final String QRY_UPD_DIGESTPWD = "UPDATE DG.DEVICE_INFO SET DEVICEDIGESTPASSWD = ? WHERE DEVICEID = ?";

    private static final String QRY_SEL_DEVICEINFO = "SELECT DEVICEID FROM DG.DEVICE_INFO WHERE DEVICEID = ?";
    
    private static final String UPDATE_DEVICE_INFO_QRY = "UPDATE DG.DEVICE_INFO SET ";

    private static final String QRY_SEL_DEVICEINFO_REQDEVICEID = "SELECT DI.DEVICEID, DI.DEVICESTATUS, DI.DEVICEACTTS, DI.DEVICELASTUSED, DI.DEVICEDIGESTPASSWD, DI.DEVICE_CREATED_AS, DI.DEVICE_CLIENTID, DI.DEVICE_SHARED, DI.DEVICE_IMPI,DI.DEVICE_TYPE, DI.DEVICE_NAME, DI.REQ_DEVICEID, DI.CORPID,DI.DEVICE_SUBSCR_MDN, DA.DEVICE_SERIAL_NO, DA.IMEI1, DA.IMEI2 , DA.DEVICE_VERSION, DA.DEVICEADDLINFO FROM DG.DEVICE_INFO DI LEFT JOIN DG.DEVICE_ADDLINFO DA ON DI.DEVICEID = DA.DEVICEID WHERE REQ_DEVICEID = ?;";

    private static final String QRY_SEL_DEVICEINFO_REQDEVICEID_DEVICEID = "SELECT COUNT(DEVICEID) FROM DG.DEVICE_INFO WHERE REQ_DEVICEID != ? AND DEVICEID = ?";

    private static final String QRY_SEL_DEVICEINFO_SUBSCRIBER_MDN = "SELECT COUNT(DEVICEID) FROM DG.DEVICE_INFO WHERE DEVICE_SUBSCR_MDN = ?";

    private static final String QRY_SEL_DEVICEINFO_SUBS_MDN_INT_DEVICEID = "SELECT COUNT(DEVICEID) FROM DG.DEVICE_INFO WHERE DEVICE_SUBSCR_MDN = ? AND DEVICEID != ?";

    private static final String QRY_SEL_DEVICE_ID_BY_MDN = "SELECT REQ_DEVICEID FROM DG.DEVICE_INFO WHERE DEVICE_SUBSCR_MDN = ?";

    private static final String QRY_SEL_DEVICEINFO_DEVICEID = "SELECT DEVICEID,DEVICESTATUS,DEVICEACTTS,DEVICELASTUSED,DEVICEDIGESTPASSWD,DEVICE_CREATED_AS,DEVICE_CLIENTID,DEVICE_SHARED,DEVICE_IMPI,DEVICE_TYPE,DEVICE_NAME,REQ_DEVICEID,CORPID, DEVICE_SUBSCR_MDN FROM DG.DEVICE_INFO WHERE DEVICEID = ?";

    private static final String QRY_UPD_DEVICEINFO = "UPDATE DG.DEVICE_INFO SET DEVICE_SHARED = ?,REQ_DEVICEID = ?,DEVICE_TYPE = ?,CORPID = ? WHERE DEVICEID = ?";
    
    private static final String GET_BULK_PROFILE_DETAILS = "SELECT " + MDN + " , " + POC_HOME + " , " + XDMS_HOME + " , " +
            CORP_ID + " , " + CORP_SUBSCRIPTION_TYPE + " , "+ CLIENT_TYPE + " , "+ CLIENT_FS1 + " , " +
            ACTIVE_FS1 + " , " +XDMS_FS1+ " , " +QPPPACKID+ " , "+ CORPADMIN_FS1 +" , "+CLIENTPV_MAJORVERSION+ " , " + SUBSCRIBER_NAME +" ," +
            CLIENTPV_MINORVERSION + " , " + CLIENT_FS2 + " , " + ACTIVE_FS2 + " , "+
            MC_PTTID + " , " + MC_VIDEOID + " , " + MC_DATAID + " , " + MC_ID + " , " + SERVICE_AUTH_STATUS + " , "+ ALIAS_MDN
            + " , "+ USER_AGENT +" , "+ ACTIVE_FS2 + " , "+ CAMERA_TYPE +
            " FROM " + POCSUBSCRIBER_INFO_TABLE + " WHERE " + MDN + " IN ( " + MDN_LIST + " )";

    public static final String QRY_UPDATE_SUBSPROFILE_FOR_MDN = "UPDATE " + POCSUBSCRIBER_INFO_TABLE + " SET " + USER_AGENT + " = ?"+ " , "+
            CLIENTPV_MAJORVERSION +" =?" +" , "+ CLIENT_FS2 +" =?" +" , "+ ACTIVE_FS2 +" =?" +" , "+ LAST_PROFILE_UPDATETIME +" =? "  +" WHERE " +
            MDN + "=?";

    public static final String QRY_UPDATE_USERAGENT_FOR_MDN = "UPDATE " + POCSUBSCRIBER_INFO_TABLE + " SET " + USER_AGENT + " = ?"+ " , "+
            CLIENTPV_MAJORVERSION +" =?" +" , "+ LAST_PROFILE_UPDATETIME +" =?" +" WHERE " +
            MDN + "=?";

    public  static final String GET_UNIQUE_FIELDS_OSMINFO_LIST="SELECT C.OSMID , C.OSMESSAGE , C.OSMSHORTTEXT , C.OSMTYPE FROM DG.CORPGROUPINFO A , DG.CORPOSMINFO B , DG.OSMLISTINFO C WHERE A.CORPID = B.CORPID AND B.OSMLISTID = C.OSMLISTID AND C.OSMLISTID =  A.OSMLISTID AND A.CORPGROUPID = ? ";
    public  static final String GET_USER_PROFILE_BY_ID="SELECT GROUPID,GROUP_ZONE,GROUP_CHANNEL,GROUP_PRIORITY,IS_SUPERVISOR,IS_BROADCASTER,CALL_INITIATE_PERMISSION,CALL_RECEIVE_PERMISSION,INCALL_PERMISSION,IS_LOCWATCHER,IS_OSMAUTHORIZED FROM DG.PROFILE_GROUP_INFO WHERE USER_PROFILEID = ?";
    public  static final String GET_SHARED_GROUP_MEMBERS_BY_SHARED_AND_OWN_CORPIDS ="SELECT MEMBERMDN FROM DG.CORPGROUPMEMBERLIST WHERE MEMBERMDN_CORPID IN (SHAREDCORPIDS)" +
            " AND CORPGROUPID IN (SELECT CORPGROUPID FROM DG.CORPGRP_SHAREDLIST WHERE OWNEDCORPID=? AND SHAREDCORPID IN (SHAREDCORPIDS))";
    public  static final String SELECT_PRE_CONFIG_GROUPID_SHARED_VAL_BY_USER_PROFILE_ID ="SELECT CORPGROUPID, GROUP_SHARED FROM DG.CORPGROUPINFO WHERE CORPGROUPID IN (SELECT GROUPID FROM DG.PROFILE_GROUP_INFO WHERE USER_PROFILEID = ?) " +
            "AND IS_PRECONFIG_GRP = 1";
    private static final String SELECT_ALL_QRY = "SELECT " + PTT_SERVER_ID + ", " + SIP_PROXY_URI + ", "
            + CLIENT_CONNECTION_RETRY_INTERVAL + ", " + MAX_CLIENT_CONN_RETRY_ATTEMPTS + ", " + CLIENT_CONNECTION_SECURITY_LEVEL + ", " + CLIENT_SIP_TXN_TIMEOUT + ", "
            + CLIENT_SIP_REFER_TXN_TIMEOUT + ", " + MIN_TCP_KA_TIMER_ON_WIFI + ", " + WIFI_TCP_KA_TIMER_INCR_VAL + ", "
            + MAX_TCP_KA_TIMER_ON_WIFI + ", " + WIFI_SSID_TIMEOUT_MAP_SIZE + ", " + TCP_KA_TIMER_ON_MACRO_CELLULAR + ", " + DETECT_WIFI_NAT_TCP_TIMEOUT + " ," + SIP_PROXY_URI_FOR_BCS
            + " FROM  " + TABLENAME_SIPPROXYSVCCONFIG + " WHERE " + PTT_SERVER_ID + " =?";

    private static final String QRY_SELECT_TGSC_MODE =  "SELECT MDN, ETAG FROM DG.XDMS_TGSC WHERE MDN IN ";
    private static final String QRY_INSERT_TGSC_MODE =  "INSERT INTO DG.XDMS_TGSC(MDN,TGSC_MODE,ETAG) VALUES (?, ?, ?)";
    private static final String QRY_UPDATE_TGSC_MODE =  "UPDATE DG.XDMS_TGSC SET TGSC_MODE = ?, ETAG = ? WHERE MDN = ?";
    private static final String QRY_DEL_SUBSCR_CAMERA_INFO = "DELETE FROM DG.SUBSCR_CAMERA_INFO WHERE MDN= ? ;";
    private static final String GET_UPM_GROUP_BY_UPMID = "SELECT USER_PROFILEID,GROUPID from DG.PROFILE_GROUP_INFO WHERE USER_PROFILEID IN( " + UPMID_LIST + " )";
    private static final String SELECT_SHARED_CORPINFO_BY_OWNERCORPID_GROUPIDS = "SELECT CORPGROUPID ,SHAREDCORPID,OWNEDCORPID,MEM_FEATURES_ALLOWED FROM DG.CORPGRP_SHAREDLIST WHERE OWNEDCORPID = ? AND CORPGROUPID IN(GROUPIDS)";
    private static final String SELECT_PRECONFIG_GRP_BY_SHARED_CORPID = "SELECT CORPGROUPID ,OWNEDCORPID FROM DG.CORPGRP_SHAREDLIST WHERE SHAREDCORPID = ? AND  CORPGROUPID IN (SELECT CORPGROUPID FROM DG.CORPGROUPINFO WHERE IS_PRECONFIG_GRP =1)";
    private static final String GROUPIDS = "GROUPIDS";
    private static final String SELECT_USERPROFILE_GRP_ID_BY_SHARE_CORP_ID = "SELECT DISTINCT(CORPGROUPID) FROM DG.CORPGROUPINFO WHERE IS_PRECONFIG_GRP =1 AND CORPID IN (SELECT OWNEDCORPID FROM DG.USERPROFILE_SHAREDLIST WHERE SHAREDCORPID =?)";
    private static final String SELECT_DEVICE_IMPI_BY_DEVICEID = "SELECT DI.DEVICEID, DI.DEVICEDIGESTPASSWD, DIO.DEVICE_IMPI, DIO.DEVICE_IMPU FROM DG.DEVICE_INFO DI INNER JOIN DG.DEVICEIMPIINFO DIO ON DI.DEVICE_IMPI= DIO.DEVICE_IMPI WHERE DI.DEVICEID= ?";

    private static final String COUNT_DEVICES_QUERY = " SELECT COUNT(DEVICEID) FROM DG.DEVICE_INFO WHERE CORPID = ? ";

    private static final String COUNT_MDN_DEVICES_QUERY = " SELECT COUNT(DEVICEID) FROM DG.DEVICE_INFO WHERE CORPID = ? AND DEVICEID = ? ";
    private static final String GET_SUBSCRIBER_NAME_QUERY = " SELECT " + SUBSCRIBER_NAME + " FROM " + POCSUBSCRIBER_INFO_TABLE + " WHERE " + MDN + "=?";

    public KnDBXDMServerDAO() {
    }


    public KnDBXDMServerDAO(String xdmPttServerId) {
        this.xdmPttServerId = xdmPttServerId;
        genInfoUtil = KnGenInfoUtil.getInstance();
        if (!registerStatus) {
            knLogger.info("KnDBXDMServerDAO", "ENTRY", registerStatus);
            List<IStatusMgrNotifyIntf> list = new ArrayList<IStatusMgrNotifyIntf>();
            list.add(new KnDBXDMServerDAO());
            KnStatusManagerClient.registerObjects(list);
            registerStatus = true;
        }


    }

    /**
     * method to create an entry into the XDM Directory Table.
     *
     * @param mdn          String
     * @param persisterTxn KnPersisterTxn
     * @throws KnDAOException DB Layer Exception
     */
    public void addMdnToXDMDirectory(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "addMdnToXDMDirectory(String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: Adding MDN To XDM Directory");


        //populating the KnDocPersist DTO
        KnDocPersistDTO docPersistDTO = new KnDocPersistDTO();
        int docId = genInfoUtil.retrieveIdForTable(KnConstants.TABLE_XDM_DIRECTORY, xdmPttServerId,
                KnConstants.TABLE_XDM_DIRECTORY_COL, false, KnConstants.DUAL_DATA_STORE);
        docPersistDTO.setDocId(docId);
        docPersistDTO.setMdn(mdn);
        docPersistDTO.setEtag(KnConstants.INITIAL_ETAG);


        KnXDMDirectoryDAO directoryDAO = KnTablesRegistry.getXDMTablesRegistry().createXDMDirectoryDAO(xdmPttServerId);
        directoryDAO.insert(docPersistDTO, persisterTxn);

        knLogger.debug(methodName, "EXIT: Adding MDN To XDM Directory");

    }


    private static final int MAX_RESOURCELISTDOCID_RETRIES = 3;

    public int addMdnToXDMContactListDocMap(KnContactListPersistDTO contactListPersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "addMdnToXDMContactListDocMap(KnContactListPersistDTO, KnPersisterTxn)";
        boolean ownedTxn = false;
        int contactListDocId = -1;

        knLogger.debug(methodName, "ENTRY: Adding MDN To XDM Contact List Doc Map ", contactListPersistDTO);


        contactListDocId = genInfoUtil.retrieveIdForTable(KnConstants.TABLE_XDM_CONTACTLIST_DOCMAP, xdmPttServerId,
                KnConstants.TABLE_XDM_CONTACTLIST_DOCMAP_COL, false, KnConstants.DUAL_DATA_STORE);

        //int resListId = genInfoUtil.retrieveIdForTable(KnConstants.TABLE_XDM_RESOURCELISTINDEXDOC, xdmPttServerId,
        //         KnConstants.TABLE_XDM_RESOURCELISTINDEXDOC_COL, false, KnConstants.DUAL_DATA_STORE);
        KnXDMContactListDocMapDAO rlsServiceDocDAO = KnTablesRegistry.getXDMTablesRegistry().createXDMContactListDoCMapDAO(xdmPttServerId);

        int retries = 0;
        while (true) {
            int resListId = generateSequenceNumber(persisterTxn);
            knLogger.debug(methodName, "Got max resourcelist id ", resListId);
            contactListPersistDTO.setResourceListId(resListId);
            contactListPersistDTO.setContactListId(contactListDocId);
            contactListPersistDTO.setEtag(KnConstants.INITIAL_ETAG);
            try {
                rlsServiceDocDAO.insert(contactListPersistDTO, persisterTxn);
                break;
            } catch (KnDAOException e) {
                if (retries < MAX_RESOURCELISTDOCID_RETRIES && isResourceListDocIdCollision(e)) {
                    retries++;
                    knLogger.warn(methodName, "RESOURCELISTDOCID collision detected (resListId=", resListId,
                            "), resetting sequence and retrying (attempt ", retries, "/", MAX_RESOURCELISTDOCID_RETRIES, ")");
                    resetSequenceNum();
                } else {
                    throw e;
                }
            }
        }
        knLogger.debug(methodName, "EXIT: Adding MDN To XDM Contact List Doc Map");

        return contactListDocId;
    }

    public void addMdnToXDMContactList(String mdn, int contactListId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "addMdnToXDMContactList(String, int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: Adding MDN To XDM Contact List ", KnGDPRTemplate.mdn(mdn));


        KnContactListPersistDTO contactListPersistDTO = new KnContactListPersistDTO();
        contactListPersistDTO.setContactListId(contactListId);
        contactListPersistDTO.setMdn(mdn);
        KnXDMContactListDAO contactListDAO = KnTablesRegistry.getXDMTablesRegistry().createXDMContactListDAO(xdmPttServerId);
        contactListDAO.insert(contactListPersistDTO, persisterTxn);
        knLogger.debug(methodName, "EXIT: Adding MDN To XDM Contact List ");
    }


    public void addMdnToCorpResourceListIndexDoc(String mdn, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "addMdnToXDMResourceListIndexDoc(KnResourceListPersistDTO, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: Adding MDN To XDM Resource List Index Doc ", KnGDPRTemplate.mdn(mdn));

        KnDocPersistDTO docPersistDTO = new KnDocPersistDTO();
        KnTableInfoBean tableInfoBean = new KnTableInfoBean();

        int resourceListDocId = genInfoUtil.retrieveIdForTable(KnConstants.TABLE_XDM_CORPRESOURCELISTINDEXDOC, xdmPttServerId,
                KnConstants.TABLE_XDM_CORPRESOURCELISTINDEXDOC_COL, false);
        docPersistDTO.setDocId(resourceListDocId);
        docPersistDTO.setMdn(mdn);
        docPersistDTO.setEtag(KnConstants.INITIAL_ETAG);
        KnXDMCorpResourceListIndexDocDAO rlsServiceDocDAO = KnTablesRegistry.getXDMTablesRegistry().createXDMCorpResourceListIndexDocDAO(xdmPttServerId);
        rlsServiceDocDAO.insert(docPersistDTO, persisterTxn);

        knLogger.debug(methodName, "EXIT: Adding MDN To XDM Resource List Index Doc");

    }

    /**
     * method to retrieve the XDM Service Configuration
     *
     * @param persisterTxn KnPersisterTxn
     * @return KnXDMSServiceConfigDTO XDM Svc configuration DTO
     * @throws KnDAOException DB Layer Exception
     */
    public KnXDMSServiceConfigDTO retrieveXDMSServiceConfig(KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "retrieveXDMSServiceConfig(KnPersisterTxn)";
        boolean ownedTxn = false;
        KnXDMSServiceConfigDTO xdmsServiceConfigDTO = null;
        Connection conn = null;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String query = null;
        String tableName = "DG.XDMS_SVC_CONFIG";

        knLogger.debug(methodName, "ENTRY: retrieve XDMS Service Config Txn - ", persisterTxn);
        try {
            if (persisterTxn != null) {
                knLogger.debug(methodName, " Data Store before - ", KnDBConst.DataStores.XDM_SHARED_DATA);
                conn = persisterTxn.getDBConnection(xdmPttServerId, false);
            } else {
                conn = KnDBManager.getDBManagerInstance().getConnection(xdmPttServerId , true);
                ownedTxn = true;
            }
            query = "SELECT * FROM " + tableName;
            //unused string query obj
            /*StringBuffer queryBuffer = new StringBuffer(100);
            queryBuffer.append("SELECT ")
                    .append("PTTServerId")
                            //  .append(", PrimaryXDMSURI")
                            // .append(", GeoXDMSURI")
                    .append(", XCAPRootURI")
                    .append(", XCAPRootURI_WiFi")
                    .append(", auth_realm")
                    .append(", MaxPublicContactsPerSubscr")
                    .append(", MaxPublicPOCGrpsPerSubscr")
                    .append(", MaxMembersPerPublicPOCGrp")
                    .append(", PublicPocGrp_ConfURITemplate")
                    .append(", MaxCorpContactsPerSubscr")
                    .append(", MaxCorpGrpsPerSubscr")
                    .append(", CorpPocGrp_ConfURITemplate")
                    .append(", Max_Corp_Subscrs")
                    .append(", MaxSublistsPerCorp")
                    .append(", MaxMembersPerCorpSublist")
                    .append(", MaxExtContactsPerCorp")
                    .append(", MaxPOCGrpsPerCorp")
                    .append(", MaxMembersPerCorpPOCGrp")
                    .append(", MAXCONTACTSPERREQUEST")
                    .append(", ENABLE_SUPERVISORY_OVERRIDE")
                    .append(", DISPATCH_GRP_ENABLED")
                    .append(", MAXDISPATCHGRPSPERCORP")
                    .append(", MAXMEMBERSPERDISPATCHGRP")
                    .append(", MAXNUMOFDISPATCHERINGROUP")
                    .append(", BLOCK_UNSUPPORTED_DEVICES")
                    .append(", ENABLE_POC_DONOR_RADIO_SUPPORT")
                    .append(", ENABLE_TALKGROUP_SELECT")
                    .append(", ENABLE_TGSCANNING_CLIENT")
                    .append(", MAXCAMPEDGROUPS")
                    .append(", UPTO_PRIORITY")
                    .append(", MAX_NNI_SUBSCRS_PER_CORP")
                    .append(", MAX_MEMBERS_PER_BG")
                    .append(", BROADCAST_GRP_ENABLED")
                    .append(", BLOCK_BLACKLIST_DEVICES")
                    .append(", ENABLE_THIRD_PARTY_CLIENT")
                    .append(", MAXSGMDNSPERGROUP")
                    .append(", PTTRADIO_SCANLIST_SIZE")
                    .append(", PTTRADIO_CHANNELLIST_SIZE")
                    .append(", PTTRADIO_DEF_SCANMODE")
                    .append(", WEB_DISPATCH_ENABLED")
                    .append(", SUBSCR_DEF_PTTRADIO")
                    .append(", INTEROP_LICENSE_TYPE")
                    .append(", MAX_SGMDNPATCH_PERGROUP")
                    .append(", iDEN_INTEROP")
                    .append(", EMERGFEATACTIVATED")
                    .append(", AMBIENT_ENABLED")
                    .append(", DISCREET_ENABLED")
                    .append(", USERCHECK_ENABLED")
                    .append(", USERSVCCTRLENABLED")
                    .append(", MAXCHANNELSPERZONE")
                    .append(", MAXRADIOCHANNELS")
                    .append(", MAXZONES")
                    .append(", LARGE_GROUP_SUPPORTED")
                    .append(", NUMOF_LARGE_GROUP_PERCORP")
                    .append(", NUMOF_MBR_PER_LARGEGROUP")
                    .append(", NUMOF_LARGE_BGROUP_PERCORP")
                    .append(", NUMOF_MBR_PER_LARGE_BGROUP")
                    .append(", MULTI_SIM_SESSION")
                    .append(", MCVIDEOENABLED")
                    .append(", MCVIDEOUNCFRMPULLENABLED")
                    .append(", PRIVACY_AMB_DISC_LISTEN")
                    .append(", MCSXCAPROOTURI_WIFI")
                    .append(", KMSWSURI_WIFI");
            queryBuffer.append("FROM ").append(tableName);*/


           /* if (ownedTxn) {
                conn = persisterTxn.getDBConnection(xdmPttServerId, true);
            } else {
                conn = persisterTxn.getDBConnection(xdmPttServerId, false);
            }*/

            pStmt = conn.prepareStatement(query);
            knLogger.debug(methodName, "Query: Executing Query - ", query, ", Txn - ", persisterTxn);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Executed the query and Result set - ", rs);
            xdmsServiceConfigDTO = new KnXDMSServiceConfigDTO();
            if (rs.next()) {
                xdmsServiceConfigDTO.setPttServerId(rs.getString("PTTSERVERID"));
                //  xdmsServiceConfigDTO.setPrimaryXDMSUri(rs.getString("PRIMARYXDMSURI"));
                //  xdmsServiceConfigDTO.setGeoXDMSUri(rs.getString("GEOXDMSURI"));
                xdmsServiceConfigDTO.setXcapRootUri(rs.getString("XCAPROOTURI"));
                xdmsServiceConfigDTO.setXcapRootUri_Wifi(rs.getString("XCAPRootURI_WiFi"));
                xdmsServiceConfigDTO.setAuthRealm(rs.getString("AUTH_REALM"));
                xdmsServiceConfigDTO.setMaxPublicContactsPerSubs(rs.getInt("MaxPublicContactsPerSubscr"));
                xdmsServiceConfigDTO.setMaxPublicPOCGrpsPerSubs(rs.getInt("MaxPublicPOCGrpsPerSubscr"));
                xdmsServiceConfigDTO.setMaxMembersPerPublicPOCGrp(rs.getInt("MaxMembersPerPublicPOCGrp"));
                xdmsServiceConfigDTO.setPublicPocGrpConfURITemplate(rs.getString("PublicPocGrp_ConfURITemplate"));
                xdmsServiceConfigDTO.setMaxCorpContactsPerSubs(rs.getInt("MaxCorpContactsPerSubscr"));
                xdmsServiceConfigDTO.setMaxCorpGrpsPerSubs(rs.getInt("MaxCorpGrpsPerSubscr"));
                xdmsServiceConfigDTO.setCorpPocGrpConfURITemplate(rs.getString("CorpPocGrp_ConfURITemplate"));
                xdmsServiceConfigDTO.setMaxSubscrPerCorp(rs.getInt("Max_Corp_Subscrs"));
                xdmsServiceConfigDTO.setMaxSublistsPerCorp(rs.getInt("MaxSublistsPerCorp"));
                xdmsServiceConfigDTO.setMaxMembersPerCorpSublist(rs.getInt("MaxMembersPerCorpSublist"));
                xdmsServiceConfigDTO.setMaxExtContactsPerCorp(rs.getInt("MaxExtContactsPerCorp"));
                // MaxPOCGrpsPerCorp - now fetched from MICROSVCS_COMMONCONFIG, will be set later
                // xdmsServiceConfigDTO.setMaxPOCGrpsPerCorp(rs.getInt("MaxPOCGrpsPerCorp"));
                xdmsServiceConfigDTO.setMaxMembersPerCorpPOCGrp(rs.getInt("MaxMembersPerCorpPOCGrp"));
                xdmsServiceConfigDTO.setMaxContactPerRequest(rs.getInt("MAXCONTACTSPERREQUEST"));
                xdmsServiceConfigDTO.setSupervisoryEnabled(rs.getInt("ENABLE_SUPERVISORY_OVERRIDE"));
                xdmsServiceConfigDTO.setDispatchEnabled(rs.getInt("DISPATCH_GRP_ENABLED"));
                // MAXDISPATCHGRPSPERCORP - now fetched from MICROSVCS_COMMONCONFIG, will be set later
                // xdmsServiceConfigDTO.setMaxDispatchGroup(rs.getInt("MAXDISPATCHGRPSPERCORP"));
                xdmsServiceConfigDTO.setMaxMembersPerDispatchGroup(rs.getInt("MAXMEMBERSPERDISPATCHGRP"));
                xdmsServiceConfigDTO.setMaxDispatchMembersPerDispatchGroup(rs.getInt("MAXNUMOFDISPATCHERINGROUP"));
                xdmsServiceConfigDTO.setBlockUnsupportedDevices(rs.getInt("BLOCK_UNSUPPORTED_DEVICES"));
                xdmsServiceConfigDTO.setEnablePocDonorRadioSupport(rs.getInt("ENABLE_POC_DONOR_RADIO_SUPPORT"));
                xdmsServiceConfigDTO.setEnableTGS(rs.getInt("ENABLE_TALKGROUP_SELECT"));
                xdmsServiceConfigDTO.setMaxCampedGroups(rs.getInt("MAXCAMPEDGROUPS"));
                xdmsServiceConfigDTO.setMaxPriority(rs.getInt("UPTO_PRIORITY"));
                xdmsServiceConfigDTO.setTgscanningClient(rs.getInt("ENABLE_TGSCANNING_CLIENT"));
                xdmsServiceConfigDTO.setMaxExtSubsPerCorp(rs.getInt("MAX_NNI_SUBSCRS_PER_CORP"));
                xdmsServiceConfigDTO.setEnableBCGrpFeature(rs.getInt("BROADCAST_GRP_ENABLED"));
                xdmsServiceConfigDTO.setMaxMemPerBCGrp(rs.getInt("MAX_MEMBERS_PER_BG"));
                xdmsServiceConfigDTO.setBlockBlackListDevices(rs.getInt("BLOCK_BLACKLIST_DEVICES"));
                xdmsServiceConfigDTO.setEnable3rdPartyPocClientSupport(rs.getInt("ENABLE_THIRD_PARTY_CLIENT"));
                xdmsServiceConfigDTO.setMaxSGMdnsPerGroup(rs.getInt("MAXSGMDNSPERGROUP"));
                xdmsServiceConfigDTO.setPttRadioScanListSize(rs.getInt("PTTRADIO_SCANLIST_SIZE"));
                xdmsServiceConfigDTO.setPttRadioChannelListSize(rs.getInt("PTTRADIO_CHANNELLIST_SIZE"));
                xdmsServiceConfigDTO.setPttRadioDefScanMode(rs.getInt("PTTRADIO_DEF_SCANMODE"));
                xdmsServiceConfigDTO.setWebDispatchEnabled(rs.getInt("WEB_DISPATCH_ENABLED"));
                xdmsServiceConfigDTO.setSubsDefPttRadio(rs.getInt("SUBSCR_DEF_PTTRADIO"));
                xdmsServiceConfigDTO.setInteropLicenceType(rs.getInt("INTEROP_LICENSE_TYPE"));
                xdmsServiceConfigDTO.setMaxSGPatchPerGrp(rs.getInt("MAX_SGMDNPATCH_PERGROUP"));
                xdmsServiceConfigDTO.setiDenInterOp(rs.getInt("iDEN_INTEROP"));
                xdmsServiceConfigDTO.setEmergFeature(rs.getInt("EMERGFEATACTIVATED"));
                xdmsServiceConfigDTO.setAmbientListening(rs.getInt("AMBIENT_ENABLED"));
                xdmsServiceConfigDTO.setDiscreteListening(rs.getInt("DISCREET_ENABLED"));
                xdmsServiceConfigDTO.setUserCheck(rs.getInt("USERCHECK_ENABLED"));
                xdmsServiceConfigDTO.setUserSvcCtrl(rs.getInt("USERSVCCTRLENABLED"));
                xdmsServiceConfigDTO.setMaxChannelsPerZone(rs.getInt("MAXCHANNELSPERZONE"));
                xdmsServiceConfigDTO.setMaxRadioChannels(rs.getInt("MAXRADIOCHANNELS"));
                xdmsServiceConfigDTO.setMaxZones(rs.getInt("MAXZONES"));
                xdmsServiceConfigDTO.setLargeGrpSupport(rs.getInt("LARGE_GROUP_SUPPORTED"));
                xdmsServiceConfigDTO.setMaxLrgGrpPerCorp(rs.getInt("NUMOF_LARGE_GROUP_PERCORP"));
                xdmsServiceConfigDTO.setMaxMemPerLrgGrp(rs.getInt("NUMOF_MBR_PER_LARGEGROUP"));
                xdmsServiceConfigDTO.setMaxLrgBGrpPerCorp(rs.getInt("NUMOF_LARGE_BGROUP_PERCORP"));
                xdmsServiceConfigDTO.setMaxMemPerLrgBGrp(rs.getInt("NUMOF_MBR_PER_LARGE_BGROUP"));
                xdmsServiceConfigDTO.setMcVideoEnabled(rs.getInt("MCVIDEOENABLED"));
                xdmsServiceConfigDTO.setMcVideoUnCfrmPullEnabled(rs.getInt("MCVIDEOUNCFRMPULLENABLED"));
                xdmsServiceConfigDTO.setMaxCorpGrpsPerLargeDispatch(rs.getInt("MAXCORPGRPSPERLARGEDISPATCH"));
                if(rs.getString("PRIVACY_AMB_DISC_LISTEN")!=null)
                {
                xdmsServiceConfigDTO.setPrivacyAmbDiscListen(Integer.parseInt(rs.getString("PRIVACY_AMB_DISC_LISTEN")));
                }
                else
                {
                    xdmsServiceConfigDTO.setPrivacyAmbDiscListen(null);
                }
                xdmsServiceConfigDTO.setMultiSimSession(rs.getInt("MULTI_SIM_SESSION"));
                xdmsServiceConfigDTO.setMcsXcapRootUriWifi(rs.getString("MCSXCAPROOTURI_WIFI"));
                xdmsServiceConfigDTO.setKmsUriWifi(rs.getString("KMSWSURI_WIFI"));
                
                // Fetch MAXPOCGRPSPERCORP from MICROSVCS_COMMONCONFIG
                PreparedStatement pStmtMaxPOC = null;
                ResultSet rsMaxPOC = null;
                try {
                    String queryMaxPOC = "SELECT PARAMVALUE FROM DG.MICROSVCS_COMMONCONFIG WHERE PARAMNAME = 'MAXPOCGRPSPERCORP'";
                    pStmtMaxPOC = conn.prepareStatement(queryMaxPOC);
                    knLogger.debug(methodName, "Query: Executing Query - ", queryMaxPOC);
                    rsMaxPOC = pStmtMaxPOC.executeQuery();
                    if (rsMaxPOC.next()) {
                        xdmsServiceConfigDTO.setMaxPOCGrpsPerCorp(rsMaxPOC.getInt("PARAMVALUE"));
                        knLogger.debug(methodName, "Retrieved MAXPOCGRPSPERCORP from MICROSVCS_COMMONCONFIG: ", rsMaxPOC.getInt("PARAMVALUE"));
                    } else {
                        knLogger.warn(methodName, "MAXPOCGRPSPERCORP not found in MICROSVCS_COMMONCONFIG");
                    }
                } catch (SQLException sqlE) {
                    knLogger.error(methodName, "Error fetching MAXPOCGRPSPERCORP from MICROSVCS_COMMONCONFIG: ", sqlE);
                } finally {
                    KnDbUtil.closeResultSet(rsMaxPOC);
                    KnDbUtil.closeStatement(pStmtMaxPOC);
                }
                
                // Fetch MAXDISPATCHGRPSPERCORP from MICROSVCS_COMMONCONFIG
                PreparedStatement pStmtMaxDispatch = null;
                ResultSet rsMaxDispatch = null;
                try {
                    String queryMaxDispatch = "SELECT PARAMVALUE FROM DG.MICROSVCS_COMMONCONFIG WHERE PARAMNAME = 'MAXDISPATCHGRPSPERCORP'";
                    pStmtMaxDispatch = conn.prepareStatement(queryMaxDispatch);
                    knLogger.debug(methodName, "Query: Executing Query - ", queryMaxDispatch);
                    rsMaxDispatch = pStmtMaxDispatch.executeQuery();
                    if (rsMaxDispatch.next()) {
                        xdmsServiceConfigDTO.setMaxDispatchGroup(rsMaxDispatch.getInt("PARAMVALUE"));
                        knLogger.debug(methodName, "Retrieved MAXDISPATCHGRPSPERCORP from MICROSVCS_COMMONCONFIG: ", rsMaxDispatch.getInt("PARAMVALUE"));
                    } else {
                        knLogger.warn(methodName, "MAXDISPATCHGRPSPERCORP not found in MICROSVCS_COMMONCONFIG");
                    }
                } catch (SQLException sqlE) {
                    knLogger.error(methodName, "Error fetching MAXDISPATCHGRPSPERCORP from MICROSVCS_COMMONCONFIG: ", sqlE);
                } finally {
                    KnDbUtil.closeResultSet(rsMaxDispatch);
                    KnDbUtil.closeStatement(pStmtMaxDispatch);
                }
                
            } else {
                knLogger.warn(methodName, "XDMS Svc Config is not available ");
                throw new KnDBPersistenceException(KnErrorCodes.DAO.ROW_NOT_FOUND, "XDMS Svc Config is not available",
                        xdmPttServerId, KnDAOSourceTypes.XDMS_SVC_CONFIG, query);
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception - " + e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected exception - ", e);
            throw KnDbUtil.processException(e, "failed to retrieve XDMS Service Config ", xdmPttServerId, KnDAOSourceTypes.XDMS_SVC_CONFIG, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            if (ownedTxn) {
                KnDbUtil.closeConnection(conn);
            }
            knLogger.info(methodName, "EXIT : XDMS Service Config -> ", xdmsServiceConfigDTO);
        }
        return xdmsServiceConfigDTO;
    }

    public Map<String, String> retrieveXDMSSubsSysConfig(KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "retrieveXDMSSubsSysConfig(KnPersisterTxn)";
        boolean ownedTxn = false;
        Map<String, String> subSysConfig = null;
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String query = null;
        String tableName = "DG.XDM_WEB_SUBSYSTEM_CONFIG";
        try {

            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the Transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            query = "SELECT * FROM " + tableName;

            conn = persisterTxn.getDBConnection(xdmPttServerId, true);
            pStmt = conn.prepareStatement(query);
            knLogger.debug(methodName, "Executing the Query - " + query);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Executed the query and Result set - " + rs);
            subSysConfig = new HashMap<String, String>();
            while (rs.next()) {
                subSysConfig.put(rs.getString(2), rs.getString(3));
            }

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }

        } catch (KnDAOException e) {
            // rollback the txn only if its owned by the current method.
            knLogger.error(methodName, "DAO Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            // rollback the txn only if its owned by the current method.
            knLogger.error(methodName, "Unexpected exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "failed to retrieve XDMS Service Config ", xdmPttServerId, KnDAOSourceTypes.XDM_WEB_SUBSYSTEM_CONFIG, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            knLogger.info(methodName, "EXIT : XDMS Service Config -> " + subSysConfig);
        }
        return subSysConfig;
    }

    public KnCorpProfilePersistDTO getCorporateProfile(String corpId, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "getCorporateProfile(corpId, persisterTxn)";
        boolean ownedTxn = false;
        KnCorpProfilePersistDTO corpProfile = null;

        knLogger.debug(methodName, "ENTRY: Retrieving Corporate profile for CorpId - " + corpId);

        try {
            KnXDMCorpInfoDAO corpInfoDAO = KnTablesRegistry.getXDMTablesRegistry().
                    createXDMCorpInfoDAO(xdmPttServerId);
            corpProfile = corpInfoDAO.selectCorpInfo(corpId, persisterTxn);
            return corpProfile;

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception has been occurred " + e.getMessage());
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected exception has occurred " + e.getMessage());
            throw KnDbUtil.processException(e, "Failed to fetch Subs Info ...", corpId, KnDAOSourceTypes.POCSUBSCRINFO, null);
        } finally {
            knLogger.debug(methodName, "EXIT: ");
        }
    }

    public KnQPPProfileInfoDTO getQPPDetails(Integer apnId, KnPersisterTxn persisterTxn) throws KnDAOException
    {

        String methodName = "getQPPDetails(apnId, persisterTxn)";
        KnQPPProfileInfoDTO qppProfile = null;

        knLogger.debug(methodName, "ENTRY: Retrieving Qpp profile for apnId - " + apnId);

        try {

            KnQPPProfileInfoDAO qppProfileInfoDAO = KnTablesRegistry.getXDMTablesRegistry().
                    createQppProfileInfoDAO(xdmPttServerId);
            qppProfile = qppProfileInfoDAO.getQPPDetails(apnId, persisterTxn);
            return qppProfile;

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception has been occurred " + e.getMessage());
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected exception has occurred " + e.getMessage());
            throw KnDbUtil.processException(e, "Failed to fetch Subs Info ...", apnId.toString(), KnDAOSourceTypes.QPP_PACKAGE, null);
        } finally {
            knLogger.debug(methodName, "EXIT: ");
        }
    }

    public Integer getQPPPCRFProfileId(Integer apnId, Integer QPPPackId,KnPersisterTxn persisterTxn) throws KnDAOException
    {

        String methodName = "getQPPPCRFProfileId(apnId,QpppackId, persisterTxn)";
        Integer QPPPCRFProfileId = 0;

        knLogger.debug(methodName, "ENTRY: Retrieving QPPPCRFProfileId for apnId - " + apnId);

        try {

            KnQPPProfileInfoDAO qppProfileInfoDAO = KnTablesRegistry.getXDMTablesRegistry().
                    createQppProfileInfoDAO(xdmPttServerId);
            QPPPCRFProfileId = qppProfileInfoDAO.getQPPPCRFProfileId(apnId,QPPPackId, persisterTxn);
            return QPPPCRFProfileId;

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception has been occurred " + e.getMessage());
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected exception has occurred " + e.getMessage());
            throw KnDbUtil.processException(e, "Failed to fetch Subs Info ...", apnId.toString(), KnDAOSourceTypes.QPP_PACKAGE, null);
        } finally {
            knLogger.debug(methodName, "EXIT: ");
        }
    }

    public Integer getQPPPCRFProfileIdForApnId(Integer apnId,KnPersisterTxn persisterTxn) throws KnDAOException
    {

        String methodName = "getQPPPCRFProfileIdForApnId(apnId, persisterTxn)";
        Integer QPPPCRFProfileId = 0;

        knLogger.debug(methodName, "ENTRY: Retrieving QPPPCRFProfileId for apnId - " + apnId);

        try {

            KnQPPProfileInfoDAO qppProfileInfoDAO = KnTablesRegistry.getXDMTablesRegistry().
                    createQppProfileInfoDAO(xdmPttServerId);
            QPPPCRFProfileId = qppProfileInfoDAO.getQPPPCRFProfileIdForApnId(apnId, persisterTxn);
            return QPPPCRFProfileId;

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception has been occurred " + e.getMessage());
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected exception has occurred " + e.getMessage());
            throw KnDbUtil.processException(e, "Failed to fetch Subs Info ...", apnId.toString(), KnDAOSourceTypes.QPP_PACKAGE, null);
        } finally {
            knLogger.debug(methodName, "EXIT: ");
        }
    }


    public List<Integer> getCorpGroupId(String corpId, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "getCorpGroupId(corpId, persisterTxn)";
        Integer corpGroupId = 0;
        List<Integer> corpGroupIds;

        knLogger.debug(methodName, "ENTRY: Retrieving corpGroupId for CorpId - " + corpId);

        try {
            KnXDMCorpInfoDAO corpInfoDAO = KnTablesRegistry.getXDMTablesRegistry().
                    createXDMCorpInfoDAO(xdmPttServerId);
            corpGroupIds = corpInfoDAO.selectCorpGroupId(corpId, persisterTxn);
            return corpGroupIds;

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception has been occurred " + e.getMessage());
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected exception has occurred " + e.getMessage());
            throw KnDbUtil.processException(e, "Failed to fetch Subs Info ...", corpId, KnDAOSourceTypes.CORPGROUPINFO, null);
        } finally {
            knLogger.debug(methodName, "EXIT: ");
        }
    }


    public String getGroupName(String groupId, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "getGroupName(groupId, persisterTxn)";
        String groupName="";

        knLogger.debug(methodName, "ENTRY: Retrieving groupName for groupId - " + groupId);

        try {

            KnXDMCorpInfoDAO corpInfoDAO = KnTablesRegistry.getXDMTablesRegistry().
                    createXDMCorpInfoDAO(xdmPttServerId);
            groupName = corpInfoDAO.getGroupName(groupId, persisterTxn);
            return groupName;

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception has been occurred " + e.getMessage());
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected exception has occurred " + e.getMessage());
            throw KnDbUtil.processException(e, "Failed to fetch Subs Info ...", groupId, KnDAOSourceTypes.CORPGROUPINFO, null);
        } finally {
            knLogger.debug(methodName, "EXIT: ");
        }
    }

    /**
     * @param mdn
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public KnSubsProfilePersistDTO getSubscriberProfile(String mdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubscriberProfile(String, KnPersisterTxn)";
        boolean ownedTxn = false;
        KnSubsProfilePersistDTO subsProfile = null;

        knLogger.debug(methodName, "ENTRY: getSubscriberProfile ", KnGDPRTemplate.mdn(mdn));

        try {
            KnXDMSubscriberInfoDAO subsInfoDAO = KnTablesRegistry.getXDMTablesRegistry().
                    createXDMSubscribreInfoDAO(xdmPttServerId);
            subsProfile = subsInfoDAO.selectSubscriberInfo(mdn, readOnly, persisterTxn);

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception has been occurred ", e.getMessage());
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected exception has occurred ", e.getMessage());
            throw KnDbUtil.processException(e, "Failed to fetch Subs Info ...", mdn, KnDAOSourceTypes.POCSUBSCRINFO, null);
        } finally {
            knLogger.info(methodName, "Exit: getSubscriberProfile mdn:", KnGDPRTemplate.mdn(mdn));
        }
        return subsProfile;
    }

    public Map<String, KnSubsProfilePersistDTO> getSubscriberProfileDetails(List<String> mdnList, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubscriberProfileDetails()";
        boolean ownedTxn = false;
        Map<String,KnSubsProfilePersistDTO> subsProfile = null;

        knLogger.debug(methodName, "ENTRY: ", KnGDPRTemplate.mdnList(mdnList));

        try {
            KnXDMSubscriberInfoDAO subsInfoDAO = KnTablesRegistry.getXDMTablesRegistry().
                    createXDMSubscribreInfoDAO(xdmPttServerId);
            subsProfile = subsInfoDAO.getSubscriberDetails(mdnList, readOnly, persisterTxn);

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception has been occurred ", e.getMessage());
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected exception has occurred ", e.getMessage());
            throw KnDbUtil.processException(e, "Failed to fetch Subs Info ...", null, KnDAOSourceTypes.POCSUBSCRINFO, null);
        } finally {
            knLogger.info(methodName, "inputGetSubscriberProfile mdnList size:", mdnList.size());
            knLogger.debug(methodName, "inputGetSubscriberProfile mdnList :", mdnList);
            knLogger.exit(methodName, "inputGetSubscriberProfile: SubsProfile: ", subsProfile);
        }
        return subsProfile;
    }

    /**
     * @param mdn
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void updateEtagForDirDoc(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "updateEtagForDirDoc(String, KnPersisterTxn)";
        KnXDMDirectoryDAO directoryDAO = KnTablesRegistry.
                getXDMTablesRegistry().createXDMDirectoryDAO(xdmPttServerId);
        directoryDAO.updateEtagForDirDoc(mdn, persisterTxn);
        knLogger.debug(methodName, "EXIT : Mdn:", KnGDPRTemplate.mdn(mdn));
    }
    
    public void updateAffForCorpGrpMemList(String mdn, int isAffiliationEnabled, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "updateAffForCorpGrpMemList(int, KnPersisterTxn)";
        KnXDMCorpGroupMemberListDAO directoryDAO = KnTablesRegistry.
                getXDMTablesRegistry().createCorpGroupMemberListDAO(xdmPttServerId);
        directoryDAO.updateAffForCorpGrpMemList(mdn, isAffiliationEnabled, persisterTxn);
        knLogger.debug(methodName, "EXIT : Mdn:", KnGDPRTemplate.mdn(mdn));
    }

    public void updateAffForCorpGrpMemList(Map<String, String> mdnIsAffiliationEnabledMap, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "updateAffForCorpGrpMemList(Map<String, String>, KnPersisterTxn)";
        KnXDMCorpGroupMemberListDAO directoryDAO = KnTablesRegistry.
                getXDMTablesRegistry().createCorpGroupMemberListDAO(xdmPttServerId);
        directoryDAO.updateAffForCorpGrpMemList(mdnIsAffiliationEnabledMap, persisterTxn);
        knLogger.debug(methodName, "EXIT : mdnIsAffiliationEnabledMap:", KnGDPRTemplate.mdnMap(mdnIsAffiliationEnabledMap));
    }

    public Map<String,List<Integer>> selectCorpGroupId(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "selectCorpGroupId(List, KnPersisterTxn)";
        KnXDMCorpGroupMemberListDAO directoryDAO = KnTablesRegistry.
                getXDMTablesRegistry().createCorpGroupMemberListDAO(xdmPttServerId);
        Map<String,List<Integer>> memberGroupIdsMap=directoryDAO.selectCorpGroupId(mdnList, persisterTxn);
        knLogger.debug(methodName, "EXIT :");
        return memberGroupIdsMap;
    }

    public List<Integer> selectOwnerPreConfigCorpGroupId(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "selectOwnerPreConfigCorpGroupId(List, KnPersisterTxn)";
        KnXDMCorpGroupMemberListDAO directoryDAO = KnTablesRegistry.
                getXDMTablesRegistry().createCorpGroupMemberListDAO(xdmPttServerId);
        List<Integer> memberGroupIdsMap=directoryDAO.selectOwnerPreConfigCorpGroupId(corpId, persisterTxn);
        knLogger.debug(methodName, "EXIT :");
        return memberGroupIdsMap;
    }

    /**
     * method to delete the contact List for the MDN
     *
     * @param mdn          String
     * @param persisterTxn KnPersisterTxn
     * @throws KnDAOException
     */
    public void deleteContactListForMdn(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deletContactListForMdn(String, KnPersisterTxn)";

        KnContactListPersistDTO contactListPersistDTO = new KnContactListPersistDTO();
        contactListPersistDTO.setMdn(mdn);

        KnXDMContactListDAO contactListDAO = KnTablesRegistry.getXDMTablesRegistry().createXDMContactListDAO(xdmPttServerId);
        contactListDAO.delete(contactListPersistDTO, persisterTxn);


        knLogger.debug(methodName, "EXIT: delete contact list for Mdn - ", KnGDPRTemplate.mdn(mdn));

    }

    public Integer selectContactListIdForMdn(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "selectContactListForMdn(String, KnPersisterTxn)";

        KnContactListPersistDTO contactListPersistDTO = new KnContactListPersistDTO();
        contactListPersistDTO.setMdn(mdn);

        KnXDMContactListDAO contactListDAO = KnTablesRegistry.getXDMTablesRegistry().createXDMContactListDAO(xdmPttServerId);
        Integer contactListId = contactListDAO.selectAll(contactListPersistDTO, persisterTxn);


        knLogger.debug(methodName, "EXIT: delete contact list for Mdn - ", KnGDPRTemplate.mdn(mdn));
        return contactListId;
    }


    public void deleteContactListDocMapForMdn(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteContactListDocMapForMdn(String, KnPersisterTxn)";
        KnContactListPersistDTO contactListPersistDTO = new KnContactListPersistDTO();
        contactListPersistDTO.setMdn(mdn);
        KnXDMContactListDocMapDAO contactListDocMapDAO = KnTablesRegistry.getXDMTablesRegistry().createXDMContactListDoCMapDAO(xdmPttServerId);
        contactListDocMapDAO.delete(contactListPersistDTO, persisterTxn);

        knLogger.debug(methodName, "EXIT: delete contact list Doc Map for Mdn - ", KnGDPRTemplate.mdn(mdn));

    }


    /**
     * method which deletes teh Corp Resource List Index Doc for Mdn
     *
     * @param mdn          String
     * @param persisterTxn KnPersisterTxn
     * @throws KnDAOException DB Layer Exception
     */
    public void deleteCorpResourceListIndexDoc(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteCorpResouceListIndexDoc(String, KnPersisterTxn)";
        KnResourceListPersistDTO resourceListPersistDTO = new KnResourceListPersistDTO();
        resourceListPersistDTO.setMdn(mdn);

        KnXDMCorpResourceListIndexDocDAO corpResourceListIndexDocDAO = KnTablesRegistry.getXDMTablesRegistry().createXDMCorpResourceListIndexDocDAO(xdmPttServerId);
        corpResourceListIndexDocDAO.delete(resourceListPersistDTO, persisterTxn);


        knLogger.debug(methodName, "EXIT: delete corp resource list for Mdn - ", KnGDPRTemplate.mdn(mdn));


    }


    /**
     * @param mdn
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public int getCurrentEtagForDirDoc(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "getCurrentEtagForDirDoc(String, KnPersisterTxn)";
        boolean ownedTxn = false;
        int etag = 0;

        knLogger.entry(methodName, "ENTRY: Getting Etag for Dir doc ", KnGDPRTemplate.mdn(mdn));

        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the Transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            KnXDMDirectoryDAO directoryDAO = KnTablesRegistry.
                    getXDMTablesRegistry().createXDMDirectoryDAO(xdmPttServerId);
            etag = directoryDAO.getCurrentEtagForDirDoc(mdn, persisterTxn);

            if (ownedTxn) {
                persisterTxn.save();
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception has been occurred " + e.getMessage());
            if (ownedTxn) persisterTxn.rollback();
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected exception has occurred " + e.getMessage());
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to fetch Dir Etag Info ..." + etag, mdn, KnDAOSourceTypes.XDM_DIRECTORY, null);
        } finally {
            if (ownedTxn) {
                persisterTxn = null;
            }
            knLogger.exit(methodName, "Mdn:", KnGDPRTemplate.mdn(mdn), " Etag:", etag);
        }
        return etag;

    }

    public Map<String, Integer> getCurrentEtagForDirDoc(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getCurrentEtagForDirDoc(List<String>, KnPersisterTxn)";
        boolean ownedTxn = false;
        Map<String, Integer> etagMap = new HashMap<>();

        knLogger.entry(methodName, "ENTRY: Getting Etag for Dir doc ");

        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the Transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            KnXDMDirectoryDAO directoryDAO = KnTablesRegistry.
                    getXDMTablesRegistry().createXDMDirectoryDAO(xdmPttServerId);
            etagMap = directoryDAO.getCurrentEtagForDirDoc(mdnList, persisterTxn);

            if (ownedTxn) {
                persisterTxn.save();
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception has been occurred " + e.getMessage());
            if (ownedTxn) persisterTxn.rollback();
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected exception has occurred " + e.getMessage());
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to fetch Dir Etag Info ..." + etagMap, xdmPttServerId, KnDAOSourceTypes.XDM_DIRECTORY, null);
        } finally {
            if (ownedTxn) {
                persisterTxn = null;
            }
        }
        return etagMap;
    }

    /**
     * @param mdn
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public int getCurrentDirEtagForUpdate(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "getCurrentDirEtagForUpdate(String, KnPersisterTxn)";
        int etag = 0;

        KnXDMDirectoryDAO directoryDAO = KnTablesRegistry.
                getXDMTablesRegistry().createXDMDirectoryDAO(xdmPttServerId);
        etag = directoryDAO.getCurrentDirEtagForUpdate(mdn, persisterTxn);

        knLogger.exit(methodName, "Etag:", etag);
        return etag;
    }


    /**
     * method to delete the XDM Directory for the MDN
     *
     * @param mdn          String
     * @param persisterTxn KnPersisterTxn
     * @throws KnDAOException DB Layer Exception
     */
    public void deleteXDMDirectoryForMdn(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteXDMDirectoryForMdn(String, KnPersisterTxn)";
        KnDocPersistDTO docPersistDTO = new KnDocPersistDTO();
        docPersistDTO.setMdn(mdn);

        KnXDMDirectoryDAO directoryDAO = KnTablesRegistry.getXDMTablesRegistry().createXDMDirectoryDAO(xdmPttServerId);
        directoryDAO.delete(docPersistDTO, persisterTxn);

        knLogger.debug(methodName, "EXIT: Delete XDM Directory for Mdn - ", KnGDPRTemplate.mdn(mdn));

    }


    /**
     * method to update the old mdn of the Directory doc to the new mdn
     *
     * @param oldMdn       String
     * @param newMdn       String
     * @param persisterTxn KnPersisterTxn
     * @throws KnDAOException DB layer Exception
     */
    public void updateDirDocMdn(String oldMdn, String newMdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateDirDocMdn(String, String, KnPersisterTxn)";

        KnXDMDirectoryDAO directoryDAO = KnTablesRegistry.getXDMTablesRegistry().createXDMDirectoryDAO(xdmPttServerId);
        directoryDAO.updateMdn(oldMdn, newMdn, persisterTxn);

        knLogger.debug(methodName, "EXIT: update the old mdn of dir to new mdn ");

    }


    /**
     * method to update the old mdn of the contact list of the new mdn
     *
     * @param oldMdn       String
     * @param newMdn       String
     * @param persisterTxn KnPersisterTxn
     * @throws KnDAOException DB layer Exception
     */
    public void updateContactListMdn(String oldMdn, String newMdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateContactListMdn(Stirng, String, KnPersisterTxn)";

        KnXDMContactListDAO contactListDAO = KnTablesRegistry.getXDMTablesRegistry().createXDMContactListDAO(xdmPttServerId);
        contactListDAO.updateMdn(oldMdn, newMdn, persisterTxn);

        knLogger.debug(methodName, "EXIT: update the old mdn of contact list to new mdn ");
    }

    /**
     * method to update the old mdn of the contact list index doc of the new Mdn
     *
     * @param oldMdn       String
     * @param newMdn       String
     * @param persisterTxn KnPersisterTxn
     * @throws KnDAOException DB layer Exception
     */
    public void updateContactListDocMapMdn(String oldMdn, String newMdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateContactListDocMapMdn(String, String, KnPersisterTxn)";

        KnXDMContactListDocMapDAO contactListDocMapDAO = KnTablesRegistry.getXDMTablesRegistry().createXDMContactListDoCMapDAO(xdmPttServerId);
        contactListDocMapDAO.updateMdn(oldMdn, newMdn, persisterTxn);

        knLogger.debug(methodName, "EXIT: update the old mdn of contact list doc map to new mdn ");
    }

    /**
     * method to update the mdn of Corp resource list index doc
     *
     * @param oldMdn       String
     * @param newMdn       String
     * @param persisterTxn KnPersisterTxn
     * @throws KnDAOException
     */
    public void updateCorpResourceListIndexDocMdn(String oldMdn, String newMdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateCorpResourceListIndexDocMdn(String, String, KnPersisterTxn)";

        KnXDMCorpResourceListIndexDocDAO corpResourceListIndexDocDAO = KnTablesRegistry.getXDMTablesRegistry().createXDMCorpResourceListIndexDocDAO(xdmPttServerId);
        corpResourceListIndexDocDAO.updateMdn(oldMdn, newMdn, persisterTxn);
        knLogger.debug(methodName, "EXIT: update the old mdn of corp resource list doc to new mdn ");
    }


    /**
     * method to retrieve the POC Supported Devices
     *
     * @param persisterTxn KnPersisterTxn
     * @return Map<String, KnPOCSuppDevicesDTO> POC Supported Devices DTO
     * @throws KnDAOException DB Layer Exception
     */

    public Map<String, KnPOCSuppDevicesDTO> retrievePOCSuppDevices(KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "retrievePOCSuppDevices(int ,KnPersisterTxn)";
        boolean ownedTxn = false;
        KnPOCSuppDevicesDTO pocSuppDevicesDTO = null;
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String query = null;
        String tableName = "DG.POC_SUPPORTED_DEVICES";
        Map<String, KnPOCSuppDevicesDTO> suppDevices = new HashMap<String, KnPOCSuppDevicesDTO>();

        knLogger.debug(methodName, "ENTRY: retrieve POC Supported Devices Txn - " + persisterTxn);
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the Transaction");
                persisterTxn.open();
                ownedTxn = true;
            }
            query = "SELECT * FROM " + tableName;
            StringBuffer queryBuffer = new StringBuffer(100);
            queryBuffer.append("SELECT ")
                    .append("PTTSERVERID")
                    .append(", RECID")
                    .append(", DEVICEVENDOR")
                    .append(", DEVICEMODEL")
                    .append(", DEVICEOS")
                    .append(", DEVICEOSVERSION")
                    .append(", INSERTTIME");

            queryBuffer.append(" FROM ").append(tableName);


            if (ownedTxn) {
                conn = persisterTxn.getDBConnection(xdmPttServerId, true);
            } else {
                conn = persisterTxn.getDBConnection(xdmPttServerId, false);
            }

            pStmt = conn.prepareStatement(query);
            knLogger.debug(methodName, "Query: Executing Query - " + query + ", Txn - " + persisterTxn);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Executed the query and Result set - " + rs);

            while (rs.next()) {
                pocSuppDevicesDTO = new KnPOCSuppDevicesDTO();
                pocSuppDevicesDTO.setPttServerId(rs.getString("PTTSERVERID"));
                pocSuppDevicesDTO.setRecId(rs.getInt("RECID"));
                pocSuppDevicesDTO.setDeviceVendor(rs.getString("DEVICEVENDOR"));
                pocSuppDevicesDTO.setDeviceModel(rs.getString("DEVICEMODEL"));
                pocSuppDevicesDTO.setDeviceOS(rs.getString("DEVICEOS"));
                pocSuppDevicesDTO.setDeviceOSVersion(rs.getString("DEVICEOSVERSION"));
                pocSuppDevicesDTO.setInsertTime(rs.getLong("INSERTTIME"));
                suppDevices.put(pocSuppDevicesDTO.getPttServerId() + ":" + pocSuppDevicesDTO.getRecId(), pocSuppDevicesDTO);
                // suppDevices.put(pocSuppDevicesDTO.getPttServerId(), pocSuppDevicesDTO);

            }

            if (suppDevices == null || suppDevices.isEmpty()) {
                knLogger.warn(methodName, "POC Supported Device is not available ");
                throw new KnDBPersistenceException(KnErrorCodes.DAO.ROW_NOT_FOUND, "POC Supported Device is not available",
                        xdmPttServerId, KnDAOSourceTypes.POC_SUPP_DEVICES, query);
            }

        } catch (KnDAOException e) {
            // rollback the txn only if its owned by the current method.
            knLogger.error(methodName, "DAO Exception - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            // rollback the txn only if its owned by the current method.
            knLogger.error(methodName, "Unexpected exception - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "failed to retrieve POC Supported Device ", xdmPttServerId, KnDAOSourceTypes.POC_SUPP_DEVICES, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            knLogger.debug(methodName, "EXIT : POC Supported Device -> " + suppDevices);
        }
        return suppDevices;
    }

    public void addMdnToCorpResourceListIndexDoc(List<String> mdns, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "addMdnToXDMResourceListIndexDoc(List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: Adding MDNs To XDM Resource List Index Doc  count:", mdns.size());
        List<Integer> resourceListDocIds;

        List<KnDocPersistDTO> docPersistDTOs = new ArrayList<KnDocPersistDTO>(mdns.size());
        resourceListDocIds = genInfoUtil.retrieveIdForTable(KnConstants.TABLE_XDM_CORPRESOURCELISTINDEXDOC, xdmPttServerId,
                KnConstants.TABLE_XDM_CORPRESOURCELISTINDEXDOC_COL, false, mdns.size());

        knLogger.debug(methodName, "resourceListDocIds size:", resourceListDocIds.size());
        KnDocPersistDTO docPersistDTO;
        for (int i = 0; i < mdns.size(); i++) {
            docPersistDTO = new KnDocPersistDTO();
            docPersistDTO.setDocId(resourceListDocIds.get(i));
            docPersistDTO.setMdn(mdns.get(i));
            docPersistDTO.setEtag(KnConstants.INITIAL_ETAG);
            docPersistDTOs.add(docPersistDTO);
        }
        KnXDMCorpResourceListIndexDocDAO corpResourceListIndexDocDAO = KnTablesRegistry.getXDMTablesRegistry().createXDMCorpResourceListIndexDocDAO(xdmPttServerId);
        corpResourceListIndexDocDAO.insert(docPersistDTOs, persisterTxn);

        knLogger.debug(methodName, "EXIT: Adding MDN To XDM Resource Lists Index Doc");


    }


    /**
     * method to create an entry into the XDM Directory Table.
     *
     * @param mdns         List<String>
     * @param persisterTxn KnPersisterTxn
     * @throws KnDAOException DB Layer Exception
     */
    public void addMdnToXDMDirectory(List<String> mdns, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "addMdnToXDMDirectory(List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: Adding MDNs To XDM Directory");
        List<Integer> docIds;
        docIds = genInfoUtil.retrieveIdForTable(KnConstants.TABLE_XDM_DIRECTORY, xdmPttServerId,
                KnConstants.TABLE_XDM_DIRECTORY_COL, false, KnConstants.DUAL_DATA_STORE, mdns.size());

        //populating the KnDocPersist DTO
        List<KnDocPersistDTO> docPersistDTOs = new ArrayList<KnDocPersistDTO>(mdns.size());

        KnDocPersistDTO docPersistDTO;
        for (int i = 0; i < mdns.size(); i++) {
            docPersistDTO = new KnDocPersistDTO();
            docPersistDTO.setDocId(docIds.get(i));
            docPersistDTO.setMdn(mdns.get(i));
            docPersistDTO.setEtag(KnConstants.INITIAL_ETAG);
            docPersistDTOs.add(docPersistDTO);
        }

        KnXDMDirectoryDAO directoryDAO = KnTablesRegistry.getXDMTablesRegistry().createXDMDirectoryDAO(xdmPttServerId);
        directoryDAO.insert(docPersistDTOs, persisterTxn);
        knLogger.debug(methodName, "EXIT: Adding MDNs To XDM Directory");


    }


    public List<Integer> addMdnToXDMContactListDocMap(List<KnContactListPersistDTO> contactListPersistDTOs, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "addMdnToXDMContactListDocMap(List<KnContactListPersistDTO>, KnPersisterTxn)";
        List<Integer> contactListDocId;

        knLogger.info(methodName, "ENTRY: Adding MDNs To XDM Contact List Doc Map ", contactListPersistDTOs.size());

        contactListDocId = genInfoUtil.retrieveIdForTable(KnConstants.TABLE_XDM_CONTACTLIST_DOCMAP, xdmPttServerId,
                KnConstants.TABLE_XDM_CONTACTLIST_DOCMAP_COL, false, KnConstants.DUAL_DATA_STORE, contactListPersistDTOs.size());

        KnXDMContactListDocMapDAO rlsServiceDocDAO = KnTablesRegistry.getXDMTablesRegistry().createXDMContactListDoCMapDAO(xdmPttServerId);

        int retries = 0;
        while (true) {
            for (int i = 0; i < contactListPersistDTOs.size(); i++) {
                int resListIds = generateSequenceNumber(persisterTxn);
                contactListPersistDTOs.get(i).setResourceListId(resListIds);
                contactListPersistDTOs.get(i).setContactListId(contactListDocId.get(i));
                contactListPersistDTOs.get(i).setEtag(KnConstants.INITIAL_ETAG);
            }
            try {
                rlsServiceDocDAO.insert(contactListPersistDTOs, persisterTxn);
                break;
            } catch (KnDAOException e) {
                if (retries < MAX_RESOURCELISTDOCID_RETRIES && isResourceListDocIdCollision(e)) {
                    retries++;
                    knLogger.warn(methodName, "RESOURCELISTDOCID collision detected during bulk insert",
                            ", resetting sequence and retrying (attempt ", retries, "/", MAX_RESOURCELISTDOCID_RETRIES, ")");
                    resetSequenceNum();
                } else {
                    throw e;
                }
            }
        }

        knLogger.debug(methodName, "EXIT: Adding MDNs To XDM Contact List Doc Map");
        return contactListDocId;
    }

    public void addMdnToXDMContactList(List<String> mdns, List<Integer> contactListIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "addMdnToXDMContactList(List<String>, List<Integer>, KnPersisterTxn)";

        knLogger.debug(methodName, "ENTRY: Adding MDNs To XDM Contact List ", mdns.size());

        List<KnContactListPersistDTO> contactListPersistDTOs = new ArrayList<KnContactListPersistDTO>(mdns.size());
        KnContactListPersistDTO contactListPersistDTO;

        for (int i = 0; i < mdns.size(); i++) {
            contactListPersistDTO = new KnContactListPersistDTO();
            contactListPersistDTO.setContactListId(contactListIds.get(i));
            contactListPersistDTO.setMdn(mdns.get(i));
            contactListPersistDTOs.add(contactListPersistDTO);
        }
        KnXDMContactListDAO contactListDAO = KnTablesRegistry.getXDMTablesRegistry().createXDMContactListDAO(xdmPttServerId);
        contactListDAO.insert(contactListPersistDTOs, persisterTxn);

        knLogger.debug(methodName, "EXIT: Adding MDNs To XDM Contact List ");

    }

    /**
     * @param mdns
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void updateEtagForDirDoc(List<String> mdns, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "updateEtagForDirDoc(List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");

        KnXDMDirectoryDAO directoryDAO = KnTablesRegistry.
                getXDMTablesRegistry().createXDMDirectoryDAO(xdmPttServerId);
        directoryDAO.updateEtagForDirDoc(mdns, persisterTxn);
        knLogger.debug(methodName, "EXIT : Mdn:", mdns.size());

    }

    /**
     * method to delete the contact List for the MDN
     *
     * @param mdns         List<String>
     * @param persisterTxn KnPersisterTxn
     * @throws KnDAOException
     */
    public void deleteContactListForMdn(List<String> mdns, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deletContactListForMdn(List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: Delete ContactList for MDN - ", mdns.size(), " with Txn - ", persisterTxn);

        KnXDMContactListDAO contactListDAO = KnTablesRegistry.getXDMTablesRegistry().createXDMContactListDAO(xdmPttServerId);
        contactListDAO.delete(mdns, persisterTxn);

        knLogger.debug(methodName, "EXIT: delete contact list for Mdn - ", mdns.size());

    }

    public void deleteContactListDocMapForMdn(List<String> mdns, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteContactListDocMapForMdn(List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: Delete contact List Doc Map for MDN - ", mdns.size(), " with Txn - ", persisterTxn);

        KnXDMContactListDocMapDAO contactListDocMapDAO = KnTablesRegistry.getXDMTablesRegistry().createXDMContactListDoCMapDAO(xdmPttServerId);
        contactListDocMapDAO.delete(mdns, persisterTxn);
        knLogger.debug(methodName, "EXIT: delete contact list Doc Map for Mdn - ", mdns.size());

    }


    /**
     * method which deletes teh Corp Resource List Index Doc for Mdn
     *
     * @param mdns         List<String>
     * @param persisterTxn KnPersisterTxn
     * @throws KnDAOException DB Layer Exception
     */
    public void deleteCorpResourceListIndexDoc(List<String> mdns, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteCorpResouceListIndexDoc(List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: Delete Corp resource List Index Doc for MDN - ", mdns.size());

        KnXDMCorpResourceListIndexDocDAO corpResourceListIndexDocDAO = KnTablesRegistry.getXDMTablesRegistry().createXDMCorpResourceListIndexDocDAO(xdmPttServerId);
        corpResourceListIndexDocDAO.delete(mdns, persisterTxn);
        knLogger.debug(methodName, "EXIT: delete corp resource list for Mdn - ", mdns.size());
    }
    /**
     * method to delete the XDM Directory for the MDN
     *
     * @param mdns         List of String
     * @param persisterTxn KnPersisterTxn
     * @throws KnDAOException DB Layer Exception
     */
    public void deleteXDMDirectoryForMdn(List<String> mdns, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteXDMDirectoryForMdn(List<String>, KnPersisterTxn)";

        KnXDMDirectoryDAO directoryDAO = KnTablesRegistry.getXDMTablesRegistry().createXDMDirectoryDAO(xdmPttServerId);
        directoryDAO.delete(mdns, persisterTxn);
        knLogger.debug(methodName, "EXIT: Delete XDM Directory for Mdn - ", mdns.size());
    }


    /**
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public List<KnBulkOrderInfoDTO> getCompletedBulkOrdersInfo(long expiredtime, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getCompletedBulkOrdersDetails(KnPersisterTxn)";
        List<KnBulkOrderInfoDTO> bulkOrderInfoDTOs = null;

        KnXDMBulkOrderInfoDAO bulkOrderInfoDAO = KnTablesRegistry.getXDMTablesRegistry().
                createXDMBulkOrderInfoDAO(xdmPttServerId);
        bulkOrderInfoDTOs = bulkOrderInfoDAO.getCompletedBulkOrdersInfo(expiredtime, persisterTxn);

        knLogger.debug(methodName, "EXIT: get Bulk orders Info: size ", bulkOrderInfoDTOs.size());
        return bulkOrderInfoDTOs;
    }

    @Override
    public int getBulkOrderCount(int bulkOrderId,KnPersisterTxn persisterTxn) throws KnDAOException{
        String methodName = "getCompletedBulkOrdersDetails(KnPersisterTxn)";
        knLogger.debug(methodName,"ENTRY : bulkOrderId",bulkOrderId);
        KnXDMBulkOrderInfoDAO bulkOrderInfoDAO = KnTablesRegistry.getXDMTablesRegistry().
                createXDMBulkOrderInfoDAO(xdmPttServerId);
        return bulkOrderInfoDAO.getBulkOrderCount(bulkOrderId,persisterTxn);

    }

    /**
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public KnBulkOrderInfoDTO getCompletedBulkOrderDetail(int bulkOrderID, KnPersisterTxn persisterTxn) throws KnDAOException {

        KnXDMBulkOrderInfoDAO bulkOrderInfoDAO = KnTablesRegistry.getXDMTablesRegistry().createXDMBulkOrderInfoDAO(xdmPttServerId);
        return bulkOrderInfoDAO.getCompletedBulkOrderDetail(bulkOrderID, persisterTxn);
    }

    /**
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public void deleteBulkOrders(List<Integer> bulkOrderList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteBulkOrders( List<Integer>,KnPersisterTxn)";

        KnXDMBulkOrderInfoDAO bulkOrderInfoDAO = KnTablesRegistry.getXDMTablesRegistry().
                createXDMBulkOrderInfoDAO(xdmPttServerId);
        bulkOrderInfoDAO.deleteBulkOrders(bulkOrderList, persisterTxn);
        knLogger.debug(methodName, "EXIT: delete Bulk orders Info: size ", bulkOrderList.size());

    }

    /**
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public void updateBulkOrders(List<Integer> bulkOrderList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateBulkOrders( List<Integer>,KnPersisterTxn)";
        KnXDMBulkOrderInfoDAO bulkOrderInfoDAO = KnTablesRegistry.getXDMTablesRegistry().
                createXDMBulkOrderInfoDAO(xdmPttServerId);
        bulkOrderInfoDAO.updateBulkOrders(bulkOrderList, persisterTxn);
        knLogger.debug(methodName, "EXIT: update Bulk orders Info: size ", bulkOrderList.size());

    }

    /**
     * method to retrieve the PAM Service Configuration
     *
     * @param persisterTxn KnPersisterTxn
     * @return KnXDMSServiceConfigDTO XDM Svc configuration DTO
     * @throws KnDAOException DB Layer Exception
     */
    public KnPAMServiceConfigDTO retrievePAMServiceConfig(KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "retrievePAMServiceConfig(KnPersisterTxn)";
        KnPAMServiceConfigDTO pamServiceConfigDTO = null;
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String query = null;
        knLogger.debug(methodName, "ENTRY: retrieve PAM Service Config Txn - ", persisterTxn);
        try {
            query = "SELECT PTTSERVERID, MAXSUBSCRPERACCOUNT, MAXTXNPERBATCH FROM DG.PAM_SVC_CONFIG";
            conn = persisterTxn.getDBConnection(xdmPttServerId, true);
            pStmt = conn.prepareStatement(query);
            knLogger.debug(methodName, "Query: Executing Query - ", query, ", Txn - ", persisterTxn);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Executed the query and Result set - ", rs);
            pamServiceConfigDTO = new KnPAMServiceConfigDTO();
            if (rs.next()) {
                pamServiceConfigDTO.setPttServerId(rs.getString(1));
                pamServiceConfigDTO.setMaxSubscrPerAcc(rs.getInt(2));
                pamServiceConfigDTO.setMaxTxnPerBatch(rs.getInt(3));
            } else {
                knLogger.warn(methodName, "XDMS Svc Config is not available ");
                throw new KnDBPersistenceException(KnErrorCodes.DAO.ROW_NOT_FOUND, "XDMS Svc Config is not available",
                        xdmPttServerId, KnDAOSourceTypes.XDMS_SVC_CONFIG, query);
            }

        } catch (KnDAOException e) {
            // rollback the txn only if its owned by the current method.
            knLogger.error(methodName, "DAO Exception - ", e);
            throw e;
        } catch (Exception e) {
            // rollback the txn only if its owned by the current method.
            knLogger.error(methodName, "Unexpected exception - ", e);
            throw KnDbUtil.processException(e, "failed to retrieve XDMS Service Config ", xdmPttServerId, KnDAOSourceTypes.XDMS_SVC_CONFIG, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            knLogger.debug(methodName, "EXIT : PAM Service Config -> ", pamServiceConfigDTO);
        }
        return pamServiceConfigDTO;
    }

    /**
     * @param mdns
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public List<Integer> getCurrentEtagsForDirDoc(List<String> mdns, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "getCurrentEtagsForDirDoc(List<String>, KnPersisterTxn)";
        List<Integer> etags = null;

        KnXDMDirectoryDAO directoryDAO = KnTablesRegistry.
                getXDMTablesRegistry().createXDMDirectoryDAO(xdmPttServerId);
        etags = directoryDAO.getCurrentEtagsForDirDoc(mdns, persisterTxn);


        knLogger.debug(methodName, "EXIT : Etags:", etags.size());

        return etags;

    }

    /**
     * This method is to retrieve the external subscriber profiles map
     *
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    //@Override
    public Map<Integer, KnExtProfileDetails> getExtProfileDetailsMap(KnPersisterTxn persisterTxn) throws KnDAOException {
        KnExtSubscrProfileInfoDAO extSubscrProfileInfoDAO = KnTablesRegistry.getXDMTablesRegistry().
                createExtSubscrProfileInfoDAO(xdmPttServerId);
        return extSubscrProfileInfoDAO.getExtProfileDetailsMap(persisterTxn);
    }


    /**
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<Integer, String> retrieveXCAPRootURIs(KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "retrieveXCAPRootURIs(KnPersisterTxn)";
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        Map<Integer, String> xcapRootUriMap = null;
        knLogger.entry(methodName, persisterTxn);
        try {
            conn = persisterTxn.getDBConnection(xdmPttServerId, true);
            pStmt = conn.prepareStatement(QRY_SELECT_XCAP_ROOT_URI);
            pStmt.setString(1, xdmPttServerId);
            knLogger.debug(methodName, "Executing the Query - ", QRY_SELECT_XCAP_ROOT_URI);
            rs = pStmt.executeQuery();
            xcapRootUriMap = new HashMap<Integer, String>();
            while (rs.next()) {
                xcapRootUriMap.put(rs.getInt(1), rs.getString(2));
            }

            if (xcapRootUriMap.isEmpty()) {
                knLogger.error(methodName, " xcap root uri is not configured");
                throw new KnDBPersistenceException(KnErrorCodes.DAO.ROW_NOT_FOUND, "No record found", xdmPttServerId, APN_PROFILE_INFO_TABLE, QRY_SELECT_XCAP_ROOT_URI);
            }

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected exception - ", e);
            throw KnDbUtil.processException(e, "failed to retrieve xcap root uri ", xdmPttServerId, APN_PROFILE_INFO_TABLE, QRY_SELECT_XCAP_ROOT_URI);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePrepareStmt(pStmt);
        }
        knLogger.exit(methodName, xcapRootUriMap);
        return xcapRootUriMap;
    }


    public KnAPNProfileInfoDTO retrieveAPNProfileInfo(String pochome , Integer apnId ,KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "retrieveAPNProfileInfo(xdmPttServerId,apnId,KnPersisterTxn)";
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        KnAPNProfileInfoDTO knAPNProfileInfoDTO=null;
        knLogger.entry(methodName, persisterTxn);
        try {
            conn = persisterTxn.getDBConnection(xdmPttServerId, true);
            pStmt = conn.prepareStatement(QRY_SELECT_ALL);
            pStmt.setString(1, pochome);
            pStmt.setInt(2, apnId);
            knLogger.debug(methodName, "Executing the Query - ", QRY_SELECT_ALL);
            rs = pStmt.executeQuery();
            knAPNProfileInfoDTO = new KnAPNProfileInfoDTO();
            while (rs.next()) {
                knAPNProfileInfoDTO.setApnId(apnId);
                knAPNProfileInfoDTO.setXcaprooturi(rs.getString(2));
                knAPNProfileInfoDTO.setClientloguri(rs.getString(3));
                knAPNProfileInfoDTO.setSipproxyuri(rs.getString(4));
                knAPNProfileInfoDTO.setGeosipproxyuri(rs.getString(5));
                knAPNProfileInfoDTO.setGeoreg_primf5uri(rs.getString(6));
                knAPNProfileInfoDTO.setGeoreg_geof5uri(rs.getString(7));
                knAPNProfileInfoDTO.setPri_wsproxyuri(rs.getString(8));
                knAPNProfileInfoDTO.setGeo_wsproxyuri(rs.getString(9));
                knAPNProfileInfoDTO.setGeoreg_pri_wsproxyuri(rs.getString(10));
                knAPNProfileInfoDTO.setGeoreg_geo_wsproxyuri(rs.getString(11));
                knAPNProfileInfoDTO.setDynamicQosFlag(rs.getInt(12));
                knAPNProfileInfoDTO.setPttbucketuricell(rs.getString(13));
                knAPNProfileInfoDTO.setPtxbucketuricell(rs.getString(14));
                knAPNProfileInfoDTO.setLocdatauricell(rs.getString(15));
                knAPNProfileInfoDTO.setKodiak_maps_uri(rs.getString(16));
                knAPNProfileInfoDTO.setKodiak_maps_geo_uri(rs.getString(17));
                knAPNProfileInfoDTO.setOidcxcaprooturi(rs.getString(18));
                knAPNProfileInfoDTO.setResourcepriorityemerg(rs.getString(19));
                knAPNProfileInfoDTO.setResourceprioritynormal(rs.getString(20));
                knAPNProfileInfoDTO.setEsrimaps_uri(rs.getString(21));
                knAPNProfileInfoDTO.setEsrimaps_geo_uri(rs.getString(22));
                knAPNProfileInfoDTO.setNoncallsipproxyuri(rs.getString(23));
                knAPNProfileInfoDTO.setGeo_noncallsipproxyuri(rs.getString(24));
                knAPNProfileInfoDTO.setMcsXcapRootUri(rs.getString("MCSXCAPROOTURI"));
                knAPNProfileInfoDTO.setKmsUri(rs.getString("KMSWSURI"));

            }

            if (knAPNProfileInfoDTO == null) {
                knLogger.error(methodName, " knAPNProfileInfo is not configured");
                throw new KnDBPersistenceException(KnErrorCodes.DAO.ROW_NOT_FOUND, "No record found", xdmPttServerId, APN_PROFILE_INFO_TABLE, QRY_SELECT_ALL);
            }

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected exception - ", e);
            throw KnDbUtil.processException(e, "failed to retrieve knAPNProfileInfo ", xdmPttServerId, APN_PROFILE_INFO_TABLE, QRY_SELECT_ALL);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePrepareStmt(pStmt);
        }
        knLogger.exit(methodName, knAPNProfileInfoDTO);
        return knAPNProfileInfoDTO;
    }


    public Map<String, Integer> retrieveAPNInfo(boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "retrieveAPNInfo(boolean, KnPersisterTxn)";
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Map<String, Integer> apnInfo = null;
        boolean ownedTxn = false;
        knLogger.info(methodName, persisterTxn,readOnly);
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug( methodName, "Opening the Transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            conn = persisterTxn.getDBConnection(xdmPttServerId, readOnly);
            pstmt = conn.prepareStatement(SELECT_APN_INFO);
            knLogger.debug(methodName, "Query: Executing - ", SELECT_APN_INFO);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query: Executed ");
            apnInfo = new HashMap<String, Integer>();
            while (rs.next()) {
                apnInfo.put(rs.getString(1), rs.getInt(2));
            }
            if (apnInfo.isEmpty()) {
                knLogger.error(methodName, "apn info doesn't exist");
                throw new KnDBPersistenceException(KnErrorCodes.DAO.ROW_NOT_FOUND, "apn profile  Doesnt exist", xdmPttServerId,
                        APN_INFO_TABLE, SELECT_APN_INFO);
            }

            if (ownedTxn) {
                knLogger.debug( methodName, "Saving the transaction");
                persisterTxn.save();
            }
        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQL Exception occurred");
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(sqlE, "Failed to select apn profileinfo - " + sqlE.getMessage(), xdmPttServerId,
                    APN_INFO_TABLE, SELECT_APN_INFO);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to select apn  info - " + e.getMessage(),
                    xdmPttServerId, APN_INFO_TABLE, SELECT_APN_INFO);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePrepareStmt(pstmt);
        }
        knLogger.exit(methodName, apnInfo);
        return apnInfo;

    }


    /**
     * @param mdns
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<String, Integer> getSubsApnId(List<String> mdns, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getSubsApnId(List<String>, boolean, KnPersisterTxn)";
        Connection conn = null;
        boolean ownedTxn = false;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        Map<String, Integer> apnInfo = null;
        int index=1;
        StringBuilder buffer = new StringBuilder(200);
        knLogger.debug(methodName, persisterTxn);
        try {
            if (persisterTxn != null) {
                knLogger.debug(methodName, " Data Store before - ", KnDBConst.DataStores.XDM_SHARED_DATA);
                conn = persisterTxn.getDBConnection(xdmPttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            } else {
                conn = KnDBManager.getDBManagerInstance().getConnection(KnGenInfoUtil.getSecDBPttServerId(xdmPttServerId), true);
                ownedTxn = true;
            }
            String txnIdStr = "";
            if (mdns != null && !mdns.isEmpty()) {
                for (String txnId : mdns) {
                    txnIdStr = txnIdStr + "?" + ",";
                }
                txnIdStr = txnIdStr.substring(0, txnIdStr.length() - 1);
            }
                buffer.append(SELECT_APNIDS).append("(").append(txnIdStr).append(")");
            knLogger.debug(methodName, "Query: Executing - ", buffer.toString());
                pStmt = conn.prepareStatement(buffer.toString());
                for(String mdn:mdns){
                    pStmt.setString(index++, mdn);
                }
                rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Query: Executed ");
            apnInfo = new HashMap<String, Integer>();
            while (rs.next()) {
                apnInfo.put(rs.getString(1).trim(), rs.getInt(2));
            }
            if (apnInfo.isEmpty()) {
                knLogger.error(methodName, "apn info doesn't exist");
                throw new KnDBPersistenceException(KnErrorCodes.DAO.ROW_NOT_FOUND, "Subs Apn  profile  Doesnt exist", xdmPttServerId,
                        SUBSCRIBER_APN_INFO_TABLE, buffer.toString());
            }

        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQL Exception occurred", sqlE);
            throw KnDbUtil.processException(sqlE, "Failed to select apn info - " + sqlE.getMessage(), xdmPttServerId,
                    SUBSCRIBER_APN_INFO_TABLE, buffer.toString());
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to select apn  info - " + e.getMessage(),
                    xdmPttServerId, SUBSCRIBER_APN_INFO_TABLE, buffer.toString());
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            if (ownedTxn) {
                KnDbUtil.closeConnection(conn);
            }
        }
        knLogger.debug(methodName);
        return apnInfo;
    }

    /**
     * @param mdn
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Integer getSubsApnId(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getSubsApnId(KnPersisterTxn)";
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        Integer apnid;
        knLogger.debug(methodName, KnGDPRTemplate.mdn(mdn), persisterTxn);
        try {
            conn = persisterTxn.getDBConnection(xdmPttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pStmt = conn.prepareStatement(SELECT_APN_ID);
            pStmt.setString(1, mdn);
            knLogger.debug(methodName, "Query: Executing - ", SELECT_APN_ID);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Query: Executed ");
            if (rs.next()) {
                apnid = rs.getInt(1);
            } else {
                knLogger.error(methodName, "apn  profile info doesn't exist");
                throw new KnDBPersistenceException(KnErrorCodes.DAO.ROW_NOT_FOUND, "subs apn info  Doesnt exist", xdmPttServerId,
                        SUBSCRIBER_APN_INFO_TABLE, SELECT_APN_ID);
            }

        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQL Exception occurred", sqlE);
            throw KnDbUtil.processException(sqlE, "Failed to select Subs APN Info  - " + sqlE.getMessage(), xdmPttServerId,
                    SUBSCRIBER_APN_INFO_TABLE, SELECT_APN_ID);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to select apn  info - " + e.getMessage(),
                    xdmPttServerId, SUBSCRIBER_APN_INFO_TABLE, SELECT_APN_ID);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePrepareStmt(pStmt);
        }
        knLogger.exit(methodName, apnid);
        return apnid;

    }


    public String selectDefaultAPNName(boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "selectDefaultAPNName(boolean, KnPersisterTxn)";
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String apnName = null;
        boolean ownedTxn = false;
        knLogger.debug(methodName, "ENTRY: Select APN Profile info ");
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug( methodName, "Opening the Transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            conn = persisterTxn.getDBConnection(xdmPttServerId, readOnly);
            pStmt = conn.prepareStatement(SELECT_DEFAULT_APN_NAME);
            pStmt.setInt(1, 1);
            knLogger.debug(methodName, "Query: Executing - ", SELECT_DEFAULT_APN_NAME);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Query: Executed ");
            if (rs.next()) {
                apnName = rs.getString(1);
            } else {
                knLogger.error(methodName, "apn  profile info doesn't exist");
                throw new KnDBPersistenceException(KnErrorCodes.DAO.ROW_NOT_FOUND, "apn profile  Doesnt exist", xdmPttServerId,
                        APN_INFO_TABLE, SELECT_DEFAULT_APN_NAME);
            }

            if (ownedTxn) {
                knLogger.debug( methodName, "Saving the transaction");
                persisterTxn.save();
            }

        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQL Exception occurred");
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(sqlE, "Failed to select apn profileinfo - " + sqlE.getMessage(), xdmPttServerId,
                    APN_INFO_TABLE, SELECT_DEFAULT_APN_NAME);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to select apn  info - " + e.getMessage(),
                    xdmPttServerId, APN_INFO_TABLE, SELECT_DEFAULT_APN_NAME);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePrepareStmt(pStmt);
        }
        knLogger.exit(methodName, apnName);
        return apnName;

    }

    @Override
    public Map<Integer, KnPOCBlackListDevicesDTO> retrievePOCBlackListDevices(KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "retrievePOCBlackListDevices(KnPersisterTxn)";
        boolean ownedTxn = false;
        KnPOCBlackListDevicesDTO pocBlackListDevicesDTO = null;
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String query = null;
        String tableName = "DG.POC_BLACKLIST_DEVICES";
        Map<Integer, KnPOCBlackListDevicesDTO> blacklistDevices = new HashMap<Integer, KnPOCBlackListDevicesDTO>();

        knLogger.debug(methodName, "ENTRY: retrieve POC Blacklist Devices Txn - ", persisterTxn);
        try {


            StringBuffer queryBuffer = new StringBuffer(100);
            queryBuffer.append("SELECT ")
                    .append("RECID")
                    .append(", DEVICEVENDOR")
                    .append(", DEVICEMODEL")
                    .append(", DEVICEOS")
                    .append(", DEVICEOSVERSION")
                    .append(", PROTOCOL_VERSION")
                    .append(", UI_VERSION")
                    .append(", HS_BASEBAND")
                    .append(", INSERTIONTIME");

            queryBuffer.append(" FROM ").append(tableName);
            conn = persisterTxn.getDBConnection(xdmPttServerId, true);


            pStmt = conn.prepareStatement(queryBuffer.toString());
            knLogger.debug(methodName, "Query: Executing Query - ", query, ", Txn - ", persisterTxn);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Executed the query and Result set - ", rs);

            while (rs.next()) {
                pocBlackListDevicesDTO = new KnPOCBlackListDevicesDTO();
                pocBlackListDevicesDTO.setRecId(rs.getInt("RECID"));
                pocBlackListDevicesDTO.setDeviceVendor(rs.getString("DEVICEVENDOR").trim());
                pocBlackListDevicesDTO.setDeviceModel(rs.getString("DEVICEMODEL").trim());
                pocBlackListDevicesDTO.setDeviceOS(rs.getString("DEVICEOS").trim());
                pocBlackListDevicesDTO.setDeviceOSVersion(rs.getString("DEVICEOSVERSION").trim());
                pocBlackListDevicesDTO.setProtocolVersion(rs.getString("PROTOCOL_VERSION").trim());
                pocBlackListDevicesDTO.setUiVersion(rs.getString("UI_VERSION").trim());
                pocBlackListDevicesDTO.setHsBaseband(rs.getString("HS_BASEBAND").trim());
                pocBlackListDevicesDTO.setInsertTime(rs.getLong("INSERTIONTIME"));
                blacklistDevices.put(rs.getInt("RECID"), pocBlackListDevicesDTO);
                // suppDevices.put(pocSuppDevicesDTO.getPttServerId(), pocSuppDevicesDTO);

            }


        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected exception - ", e);
            throw KnDbUtil.processException(e, "failed to retrieve POC Blacklisted Device ", xdmPttServerId, KnDAOSourceTypes.POC_BLACKLIST_DEVICES, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            knLogger.info(methodName, "EXIT : POC Blacklisted Device -> ", blacklistDevices);
        }
        return blacklistDevices;
    }

    @Override
    public Map<String, KnActivationCodeConfigDTO> retrieveActivationCodeConfig(KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "retrieveActivationCodeConfig(KnPersisterTxn)";
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String query = null;
        boolean ownedTxn = true;
        knLogger.debug(methodName, "ENTRY: retrieve POC Blacklist Devices Txn - ", persisterTxn);
        Map<String, KnActivationCodeConfigDTO> activationCodeConfigDTOMap = new HashMap<>();
        try {
            query = SELECT_ACTIVATIONCODE_CONFIG;
            //conn = persisterTxn.getDBConnection(xdmPttServerId, true);
            if (persisterTxn != null) {
                knLogger.debug(methodName, " Data Store before - ", KnDBConst.DataStores.XDM_SHARED_DATA);
                conn = persisterTxn.getDBConnection(xdmPttServerId, false);
                knLogger.debug(methodName, "if block");
            } else {
                conn = KnDBManager.getDBManagerInstance().getConnection(xdmPttServerId, true);
                knLogger.debug(methodName, "else block");
                knLogger.debug(methodName, "conn", conn);
            }

            pStmt = conn.prepareStatement(query);
            pStmt.setString(1, xdmPttServerId);
            knLogger.debug(methodName, "Query: Executing Query - ", query);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Executed the query and Result set - ", rs);

            while (rs.next()) {
                KnActivationCodeConfigDTO activationCodeConfigDTO = new KnActivationCodeConfigDTO();
                activationCodeConfigDTO.setClientType(rs.getInt("CLIENT_TYPE"));
                activationCodeConfigDTO.setActCodeLength(rs.getInt("ACTCODE_LENGTH"));
                activationCodeConfigDTO.setActCodeType(rs.getInt("ACTCODE_TYPE"));
                activationCodeConfigDTO.setActCodeValidity(rs.getInt("ACTCODE_VALIDITY"));
                activationCodeConfigDTO.setActCodeExpiry(rs.getInt("ACTCODE_EXPIRY"));
                activationCodeConfigDTO.setIntf(rs.getInt("INTF_TYPE"));
                String key = activationCodeConfigDTO.getClientType() + KnConstants.DELIM + activationCodeConfigDTO.getIntf();
                activationCodeConfigDTOMap.put(key, activationCodeConfigDTO);
            }

            if (activationCodeConfigDTOMap.isEmpty()) {
                knLogger.warn(methodName, "Activation Config params not configured in DB");
                throw new KnDBPersistenceException(KnErrorCodes.DAO.ROW_NOT_FOUND, "Activation Config params not configured in DB",
                        xdmPttServerId, KnDAOSourceTypes.ACTIVATIONCODE_CONFIG, query);
            }


        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected exception - ", e);
            throw KnDbUtil.processException(e, "failed to retrieve Activation configuration ", xdmPttServerId, KnDAOSourceTypes.ACTIVATIONCODE_CONFIG, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStmt);
        }
        return activationCodeConfigDTOMap;
    }

    @Override
    /**
     * This method retgrieves the configurations from DG.RTXENVVARIABLEINFO for a given list of keys
     *
     * @param keyList the list of keys
     * @return the configMap
     * @throws KnDAOException exception
     */
    public Map<String, String> retrieveRTXConfig(List<String> keyList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "retrieveRTXConfig(Collection<String>)";
        knLogger.info(methodName, "retrieving Custom Config info - ");

        //retrieve the configuration for the given keyList
        //KnPersisterTxn persisterTxn = KnPersisterTxn.getPersisterTxn();
        Connection conn = null;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        boolean ownedTxn = false;
        //String localPttId = KnDbUtil.getDBConfigInfo().getLocalPttId();
        Map<String, String> configMap = new HashMap<String, String>();
        try {
            if (persisterTxn != null) {
                knLogger.debug(methodName, " Data Store before - ", KnDBConst.DataStores.XDM_SHARED_DATA);
                conn = persisterTxn.getDBConnection(xdmPttServerId, false);
            } else {
                conn = KnDBManager.getDBManagerInstance().getConnection(xdmPttServerId, true);
                ownedTxn = true;
            }
            StringBuffer keys = KnDbUtil.convertListToStringBuffer(keyList);
            String query = "SELECT PARAMNAME, PARAMVALUE FROM DG.RTXENVVARIABLEINFO WHERE PARAMNAME IN " + keys.toString();
            pStmt = conn.prepareStatement(query);
            knLogger.info(methodName, "QUERY: Executing - ", query);
            rs = pStmt.executeQuery();
            knLogger.info(methodName, "QUERY: Executed - ");
            while (rs.next()) {
                configMap.put(rs.getString("PARAMNAME"), rs.getString("PARAMVALUE"));
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred - ", e);
        } catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
            throw com.kodiak.common.dao.KnDbUtil.processException(e, "Exception occurred ", xdmPttServerId, "RTXENVVARIABLEINFO", null);
        } finally {
            com.kodiak.common.dao.KnDbUtil.closeResultSet(rs);
            com.kodiak.common.dao.KnDbUtil.closeStatement(pStmt);
            if (ownedTxn) {
                KnDbUtil.closeConnection(conn);
            }
        }
        knLogger.debug(methodName, "Returning configMap - ", configMap);
        return configMap;
    }


    private int generateSequenceNumber(KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "generateSequenceNumber";

        if (sequenceNum == null) {
            synchronized (KnDBXDMServerDAO.class) {
                if (sequenceNum == null) {
                    Integer start = fetchMaxSeqNumber(persisterTxn);
                    if (start != null) {
                        sequenceNum = new AtomicInteger(start);
                    }
                }
            }
        }
        Integer generatedId = sequenceNum.incrementAndGet();

        knLogger.info(methodName, " generateId ", generatedId);
        return generatedId;
    }


    /**
     * method to retrieve the max sequence Number
     *
     * @param persisterTxn KnPersisterTxn
     * @return int maxSeqNumber
     */

    private Integer fetchMaxSeqNumber(KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "fetchMaxSeqNumber(KnPersisterTxn)";
        Integer maxSeqNum = null;
        KnXDMContactListDocMapDAO rlsServiceDocDAO = KnTablesRegistry.getXDMTablesRegistry().createXDMContactListDoCMapDAO(xdmPttServerId);
        maxSeqNum = rlsServiceDocDAO.getMaxResourceListId(persisterTxn);
        knLogger.debug(methodName, "EXIT: fetch Max sequence Number ", maxSeqNum);
        return maxSeqNum;
    }

    public void notify(KnStatusMgrConstants.CARD_STATES previousState, KnStatusMgrConstants.CARD_STATES currentState) {
        String methodName = "notify() ";
        knLogger.info(methodName, "Redundancy Status Notify -> Prev Status : ", previousState, ", Curr Status : ",
                currentState);
        if (KnStatusMgrConstants.CARD_STATES.ACTIVE.equals(currentState)) {
            sequenceNum = null;
            knLogger.info(methodName, "sequenceNum set to null ");
        }

    }

    /**
     * Resets the in-memory RESOURCELISTDOCID sequence counter to force re-initialization
     * from the database on the next call to generateSequenceNumber.
     * This is called when a unique constraint violation on RESOURCELISTDOCID is detected,
     * which can occur during active-standby switchover due to replication lag.
     */
    public static synchronized void resetSequenceNum() {
        String methodName = "resetSequenceNum";
        knLogger.info(methodName, "Resetting sequenceNum to null due to RESOURCELISTDOCID collision");
        sequenceNum = null;
    }

    /**
     * Checks if the given exception is caused by a unique constraint violation
     * on the RESOURCELISTDOCID column of XDM_CONTACTLIST_DOCMAP table.
     */
    private boolean isResourceListDocIdCollision(KnDAOException e) {
        // Check the exception message and cause chain for TT0907 unique constraint on XDM_CONTACTLIST_DOCMAP
        String message = e.getMessage();
        if (message != null && message.contains("TT0907") && message.contains("XDM_CONTACTLIST_DOCMAP")) {
            return true;
        }
        // Also check the root cause in case the wrapping doesn't include full text
        Throwable cause = e.getCause();
        while (cause != null) {
            String causeMsg = cause.getMessage();
            if (causeMsg != null && causeMsg.contains("TT0907") && causeMsg.contains("XDM_CONTACTLIST_DOCMAP")) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }
    
    public int updateUsageByMDNs(List<String> mdns, int usage, KnPersisterTxn persisterTxn) throws KnDAOException {
    	final String methodName = "updateUsageByMDNs(List, int, KnPersisterTxn)";
    	knLogger.entry(methodName, KnGDPRTemplate.mdnList(mdns), usage);
    	Connection conn;
    	PreparedStatement pStmt = null;
    	int[] result;
    	
    	try {
    		conn = persisterTxn.getDBConnection(xdmPttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
    		pStmt = conn.prepareStatement(UPDATE_USAGE_BY_MDN_QRY);
    		
    		for (String mdn : mdns) {
    			pStmt.setInt(1, usage);
    			pStmt.setString(2, mdn);
    			pStmt.addBatch();
			}
    		
    		knLogger.debug(methodName, QRY_EXECUTING_MSG, UPDATE_USAGE_BY_MDN_QRY);
			result = pStmt.executeBatch();
			knLogger.debug(methodName, QRY_EXE_MSG);
			
		} catch (SQLException e) {
			knLogger.error(methodName, SQL_EXCEPTION_MSG, e);
			throw KnDbUtil.processException(e, "failed to update mdns state ", xdmPttServerId, KnDAOSourceTypes.PAMACCOUNT_POOL_USAGE, UPDATE_USAGE_BY_MDN_QRY);
		} finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }
    	
    	knLogger.exit(methodName, result.length);
    	return result.length;
    }

    public Map<String, KnTPUserAccountDTO> retrieveTPUserAccountForMDNs(List<String> mdns, KnPersisterTxn persistTxn) throws KnDAOException {
        return retrieveTPUserAccountForMDNs(mdns, false, persistTxn);
    }

    public Map<String, KnTPUserAccountDTO> retrieveTPUserAccountForMDNs(List<String> mdns, boolean readOnly, KnPersisterTxn persistTxn) throws KnDAOException {
        final String methodName = "retrieveTPUserAccountForMDNs(List,boolean,KnPersisterTxn)";
    	knLogger.entry(methodName, KnGDPRTemplate.mdnList(mdns));
    	Connection conn;
    	PreparedStatement stmt = null;
    	ResultSet rs = null;
    	int index = 1;
    	Map<String, KnTPUserAccountDTO> tpMdnMap = new HashMap<>();
    	String query; 
    	try {
    		query = SELECT_TP_USER_VENDOR_QRY;
    		conn = persistTxn.getDBConnection(xdmPttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
    		//String mdnListStr = KnGeneralUtil.formCommaSeperatedIdList(mdns);
    		//query = KnGeneralUtil.replaceContactWithValue(query, MDNLIST, mdnListStr);
            query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(mdns,query,"MDNLIST");
    		stmt = conn.prepareStatement(query);
    		for(String mdn : mdns){
    		    stmt.setString(index++,mdn);
            }
    		knLogger.debug(methodName, QRY_EXECUTING_MSG, SELECT_TP_USER_VENDOR_QRY);
    		rs = stmt.executeQuery();
			knLogger.debug(methodName, QRY_EXE_MSG);
			
			while (rs.next()) {
				KnTPUserAccountDTO tpUserAccountDTO = new KnTPUserAccountDTO();
				tpUserAccountDTO.setMdn(rs.getString(MDN).trim());
				tpUserAccountDTO.setTpUser(rs.getString(THIRD_PARTY_USER));
				tpUserAccountDTO.setTpAccount(rs.getString(THIRD_PARTY_ACCT_ID));
				tpMdnMap.put(rs.getString(MDN).trim(), tpUserAccountDTO);
			}
			
		} catch (SQLException e) {
			knLogger.error(methodName, SQL_EXCEPTION_MSG, e);
			throw KnDbUtil.processException(e, "failed to update mdns state ", xdmPttServerId, KnDAOSourceTypes.PAMACCOUNT_POOL_USAGE, SELECT_TP_USER_VENDOR_QRY);
			
		} finally {
            com.kodiak.common.dao.KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
        }
    	
    	knLogger.exit(methodName, "map size-", tpMdnMap.size());
    	return tpMdnMap;
    }
    
    public String retrieveActivationCode(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
    	KnTmpVASSubscrKeyInfoDAO tmpVASSubscrKeyInfoDAO = KnTablesRegistry.getXDMTablesRegistry().createTmpVASSubscriptionKeyInfoDAO(xdmPttServerId);
    	return tmpVASSubscrKeyInfoDAO.retrieveActivationCode(mdn, persisterTxn);
    }


	@Override
	public void removeActivationCode(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
		KnTmpVASSubscrKeyInfoDAO tmpVASSubscrKeyInfoDAO = KnTablesRegistry.getXDMTablesRegistry().createTmpVASSubscriptionKeyInfoDAO(xdmPttServerId);
    	tmpVASSubscrKeyInfoDAO.removeActivationCode(mdn, persisterTxn);
	}

    @Override
    /*
    This method is used to retieve client type config from the table DG.CLIENT_TYPE_CONFIGURATION
     */
    public Map<Integer, KnClientTypeConfigDTO> retrieveClientTypeConfig(KnPersisterTxn persisterTxn) throws KnDAOException{
        String methodName = "retrieveClientTypeConfig(KnPersisterTxn persisterTxn)";
        knLogger.info(methodName, "retrieving client type config");
        Connection conn;
        Statement stmt = null;
        ResultSet rs = null;
        Map<Integer , KnClientTypeConfigDTO> clientTypeConfigDTOMap = new HashMap<>();
        boolean ownedTxn = false;

        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            String query = SELECT_CLIENT_TYPE_CONFIG;
            conn = persisterTxn.getDBConnection(xdmPttServerId, false);
            stmt = conn.createStatement();

            knLogger.debug(methodName, QRY_EXECUTING_MSG, SELECT_CLIENT_TYPE_CONFIG);
            rs = stmt.executeQuery(query);
            knLogger.debug(methodName, QRY_EXE_MSG);

            while (rs.next()) {
               KnClientTypeConfigDTO configDTO = new KnClientTypeConfigDTO();
                configDTO.setClientType(Integer.parseInt(rs.getString("CLIENT_TYPE")));
                configDTO.setIsEnable(Integer.parseInt(rs.getString("IS_ENABLED")));
                configDTO.setEnableSuppDevChk(Integer.parseInt(rs.getString("ENABLE_SUPP_DEVICE_CHK")));
                clientTypeConfigDTOMap.put(rs.getInt("CLIENT_TYPE"),configDTO);
            }

            if(ownedTxn){
                knLogger.debug(methodName," Saving the transaction");
                persisterTxn.save();
            }

        }catch (SQLException e){
            knLogger.error(methodName, SQL_EXCEPTION_MSG, e);
            if (ownedTxn) {
                knLogger.debug(methodName, "rolling back the transaction ");
                persisterTxn.rollback();
            }

            throw KnDbUtil.processException(e, "failed to retieve client_type details ", xdmPttServerId, KnDAOSourceTypes.CLIENT_TYPE_CONFIGURATION, SELECT_CLIENT_TYPE_CONFIG);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
        }

        knLogger.exit(methodName, "map size-", clientTypeConfigDTOMap.size());
        return clientTypeConfigDTOMap;

    }

    /**
     * This  method is for retrieving the dynamicQosFlag from APNProfile table.
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    @Override
    public Map<Integer, KnAPNConfigDTO> retrieveAPNInfoConfig(KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "retrieveAPNInfoConfig(KnPersisterTxn persistTxn)";
        knLogger.info(methodName , "retrieve APN Properties ");
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        boolean ownedTxn = false;
        Map<Integer , KnAPNConfigDTO> knAPNConfDTO = new HashMap<>();
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }
            conn = persisterTxn.getDBConnection(xdmPttServerId, true);
            pStmt = conn.prepareStatement(QRY_APN_FIELD_ROPERTIES);
            pStmt.setString(1, xdmPttServerId);

            knLogger.debug(methodName, "Executing the Query - ", QRY_APN_FIELD_ROPERTIES+"-xdmPttServerId-"+xdmPttServerId);
            rs = pStmt.executeQuery();

            while (rs.next()) {
                KnAPNConfigDTO knAPNConfigDTO = new KnAPNConfigDTO();
                knAPNConfigDTO.setDynamicQosFlag(rs.getInt(1));
                knAPNConfigDTO.setApnXCAPUri(rs.getString(2));
                knAPNConfigDTO.setApnId(rs.getInt(3));
                knAPNConfigDTO.setPtxBucketUri(rs.getString(4));
                knAPNConfigDTO.setLocDataUriCellular(rs.getString(5));
                knAPNConfigDTO.setMcsXCAPUri(rs.getString(6));
                knLogger.debug(methodName , "retrieve APN Properties :"+ knAPNConfigDTO.getDynamicQosFlag()+" :"+knAPNConfigDTO.getApnXCAPUri());
                // confusion what will be key over here
                knAPNConfDTO.put( knAPNConfigDTO.getApnId(),knAPNConfigDTO);
            }
            if(ownedTxn){
                knLogger.debug(methodName," Saving the transaction");
                persisterTxn.save();
            }
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected exception - ", e);
            if (ownedTxn) {
                knLogger.debug(methodName, "rolling back the transaction ");
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "failed to retrieve xcap root uri ", xdmPttServerId, QRY_APN_FIELD_ROPERTIES, QRY_APN_FIELD_ROPERTIES);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStmt);
        }
        knLogger.exit(methodName, "Exit-->");
        return knAPNConfDTO;
    }

    @Override
    public Map<Integer, KnSuppVocoderProfileDTO> retrieveSuppVocoderProfile(KnPersisterTxn persisterTxn) throws KnDAOException{
        String methodName = "retrieveSuppVocoderProfile(KnPersisterTxn persistTxn)";
        knLogger.info(methodName , "ENTRY : retrieving supported vocoder Profiles ");
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        Map<Integer , KnSuppVocoderProfileDTO> suppVocoderProfileDTO = new HashMap<>();
        try {
            conn = persisterTxn.getDBConnection(xdmPttServerId, true);
            pStmt = conn.prepareStatement(SELECT_SUPP_VOCODER_PROFILE);

            knLogger.debug(methodName, "Executing the Query - ", SELECT_SUPP_VOCODER_PROFILE+"-xdmPttServerId-"+xdmPttServerId);
            rs = pStmt.executeQuery();
            while (rs.next()) {
               KnSuppVocoderProfileDTO profileDTO = new KnSuppVocoderProfileDTO();
                int profileId = rs.getInt("ProfileId");
                profileDTO.setVocoderId(rs.getInt("VocoderId"));
                profileDTO.setVocoderName(rs.getString("VocoderName"));
                profileDTO.setProfileId(profileId);
                profileDTO.setNumOfFrames(rs.getInt("NumOfFrames"));
                profileDTO.setpTime(rs.getInt("PTime"));
                profileDTO.setMaxPTime(rs.getInt("MaxPTime"));
                profileDTO.setMaxDLBandwidth(rs.getInt("MAXDLBandwidth"));
                profileDTO.setMaxULBandwidth(rs.getInt("MaxULBandwidth"));
                profileDTO.setMode(rs.getInt("Mode"));
                profileDTO.setOctetAlignedMode(rs.getInt("OctetAlignedMode"));
                profileDTO.setClockRate(rs.getInt("ClockRate"));
                profileDTO.setBitrate(rs.getInt("Bitrate"));
                profileDTO.setIsDefault(rs.getInt("Is_Default"));
                suppVocoderProfileDTO.put(profileId,profileDTO);
            }

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected exception - ", e);
            throw KnDbUtil.processException(e, "failed to retrieve xcap root uri ", xdmPttServerId, SELECT_SUPP_VOCODER_PROFILE, SELECT_SUPP_VOCODER_PROFILE);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStmt);
        }
        knLogger.exit(methodName, suppVocoderProfileDTO);
        return suppVocoderProfileDTO;
    }

@Override
    public Map<Integer, KnMqServiceConfig> retrieveMqConfigDetails(KnPersisterTxn persisterTxn) throws KnDAOException {

    final String methodName = "retrieveMqConfigDetails(KnPersisterTxn)";
    Connection conn;
    PreparedStatement pStmt = null;
    ResultSet rs = null;
    String query = null;
    Map<Integer,String>  fqdnMap=new HashMap<Integer, String>();
    String tableName = "DG.MQ_SERVICE_CONFIG";
    Map<Integer, KnMqServiceConfig> knMqServiceConfigMap = new HashMap<Integer, KnMqServiceConfig>();
    knLogger.debug(methodName, "ENTRY: retrieve MQ properties - ", persisterTxn);
    fqdnMap=retrieveFqdnDetail(persisterTxn);
    try {

        StringBuffer queryBuffer = new StringBuffer(100);
        queryBuffer.append("SELECT ")
                .append("VHOST_NAME")
                .append(", EXCHANGE_NAME")
                .append(", EXCHANGE_TYPE ")
                .append(", IS_DURABLE")
                .append(", IS_AUTO_DELETE")
                .append(", IS_INTERNAL")
                .append(", USERNAME")
                .append(", PORT")
                .append(", PASSWORD");

        queryBuffer.append(" FROM ").append(tableName);
        conn = persisterTxn.getDBConnection(xdmPttServerId, true);

        pStmt = conn.prepareStatement(queryBuffer.toString());
        knLogger.debug(methodName, "Query: Executing Query - ", query, ", Txn - ", persisterTxn);
        rs = pStmt.executeQuery();
        knLogger.debug(methodName, "Executed the query and Result set - ", rs);
        KnMqServiceConfig knMqServiceConfig =  new KnMqServiceConfig();
          while (rs.next()) {

            knMqServiceConfig.setvHostName(rs.getString("VHOST_NAME").trim());
            knMqServiceConfig.setExchangeName(rs.getString("EXCHANGE_NAME").trim());
            knMqServiceConfig.setExchangeType(rs.getString("EXCHANGE_TYPE").trim());
            knMqServiceConfig.setIsDurable(rs.getInt("IS_DURABLE"));
            knMqServiceConfig.setIsAutoDelete(rs.getInt("IS_AUTO_DELETE"));
            knMqServiceConfig.setIsInternal(rs.getInt("IS_INTERNAL"));
            knMqServiceConfig.setUserName(rs.getString("USERNAME").trim());
            knMqServiceConfig.setUsrPassword(rs.getString("PASSWORD"));
            knMqServiceConfig.setPort(rs.getInt("PORT"));
        }
        for (Map.Entry<Integer, String> fqdnEntry : fqdnMap.entrySet()) {
            knLogger.info(methodName, "FQDN Entry :key : ", fqdnEntry.getKey());
            KnMqServiceConfig mqServiceConfig =  new KnMqServiceConfig();
            mqServiceConfig.setvHostName(knMqServiceConfig.getvHostName());
            mqServiceConfig.setExchangeName(knMqServiceConfig.getExchangeName());
            mqServiceConfig.setExchangeType(knMqServiceConfig.getExchangeType());
            mqServiceConfig.setIsDurable(knMqServiceConfig.getIsDurable());
            mqServiceConfig.setIsAutoDelete(knMqServiceConfig.getIsAutoDelete());
            mqServiceConfig.setIsInternal(knMqServiceConfig.getIsInternal());
            mqServiceConfig.setUserName(knMqServiceConfig.getUserName());
            mqServiceConfig.setUsrPassword(knMqServiceConfig.getUsrPassword());
           mqServiceConfig.setPort(knMqServiceConfig.getPort());

            mqServiceConfig.setServiceFqdn(fqdnEntry.getValue());
            knMqServiceConfigMap.put(fqdnEntry.getKey(),mqServiceConfig);
        }

    } catch (Exception e) {
        knLogger.error(methodName, "Unexpected exception - ", e);
        throw KnDbUtil.processException(e, "failed to retrieve Service config info ", xdmPttServerId, "KnDAOSourceTypes.MQ_SERVICE_CONFIG", query);
    } finally {
        KnDbUtil.closeResultSet(rs);
        KnDbUtil.closeStatement(pStmt);
        knLogger.info(methodName, "EXIT :Retrieved Service config info  -> ", knMqServiceConfigMap);
    }
    return knMqServiceConfigMap;
    }

    private  Map<Integer,String>retrieveFqdnDetail(KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "retrieveFqdnDetails(KnPersisterTxn)";
        boolean ownedTxn = false;
        Connection conn;
        PreparedStatement pStmt = null;

       Map<Integer,String> serviceFqdnMap= new HashMap<Integer, String>();
        ResultSet rs = null;
        String query = null;
        String fqdn= "";
        String tableName = "DG.SERVICE_FQDN_INFO";
        knLogger.debug(methodName, "ENTRY: retrieve FQDN properties - ", persisterTxn);
        try {
            StringBuffer queryBuffer = new StringBuffer(100);
            queryBuffer.append("SELECT ")
                    .append("CLUSTERID")
                    .append(",SERVICE_FQDN")
                    .append(" FROM ")
                    .append(tableName)
                    .append(" WHERE SERVICETYPE =")
                    .append("'RMQ'");
            conn = persisterTxn.getDBConnection(xdmPttServerId, true);

            pStmt = conn.prepareStatement(queryBuffer.toString());
            knLogger.debug(methodName, "Query: Executing Query - ", query, ", Txn - ", persisterTxn);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Executed the query and Result set - ", rs);

            while (rs.next()) {

                serviceFqdnMap.put(rs.getInt(1), rs.getString(2).trim());
            }

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected exception - ", e);
            throw KnDbUtil.processException(e, "failed to retrieve service FQDN ", xdmPttServerId, "KnDAOSourceTypes.MQ_SERVICE_FQDN_INFO", query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            knLogger.info(methodName, "EXIT :retrieve service FQDN -> ", fqdn);
        }
        return serviceFqdnMap;
    }
    /**
     * This method is to fetch the ptx url bucet
     * url format ;
     * http://<SYNCGW-FQDN>:<SYNCGW-ADMIN-PORT>/<PTX-BUCKET-NAME
     * SERVICE_FQDN column
     * SERVICETYPE=’SYNCGW’ and SERVICEVER=’1.0’ and CLUSTERID=1 and 2,
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<Integer, String> retrieveSericeFqdnInfo(KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "retrieveSericeFqdnInfo(IPersistenceDTO, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : Input DTO passed persistenceDTO - ", persisterTxn);
        Map<Integer,String> fqdnMap= new HashMap<Integer, String>();


        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;

        try {
            conn = persisterTxn.getDBConnection(xdmPttServerId, true);
            pStmt = conn.prepareStatement(QUERY_SERVICE_FQDN_INFO);

            knLogger.debug(methodName, "Executing the Query - ", QUERY_SERVICE_FQDN_INFO+"-xdmPttServerId-"+xdmPttServerId);
            rs = pStmt.executeQuery();

            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                fqdnMap.put(rs.getInt(1),rs.getString(2));
            }
            knLogger.debug(methodName,"fqdnMap :",fqdnMap);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected exception - ", e);
            throw KnDbUtil.processException(e, "failed to retrieve Microsrvs common config  ", xdmPttServerId, QUERY_SERVICE_FQDN_INFO, QUERY_SERVICE_FQDN_INFO);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStmt);
        }
            return fqdnMap;
        }

    /**
     * Method to retrieve the routing keys from DB DG.MQ_QUEUE_INFO table.
     * @param clusterId
     * @param sigCardType
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    @Override
    public Map<String, String> retrieveNotifyRoutingKeys(int clusterId, int sigCardType, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "retrieveNotifyRoutingKeys()";
        knLogger.info(methodName, "retrieving routing keys - ");
        Connection conn;
        PreparedStatement pStmt = null;
        PreparedStatement pStmt1 = null;
        PreparedStatement pStmt2 = null;
        ResultSet rs = null;
        ResultSet rs1 = null;
        ResultSet rs2 = null;
        Map<String, String> routingKeyMap = new HashMap<String, String>();
        String query = null;
        try {
            query = "select ROUTING_KEY from DG.MQ_QUEUE_INFO where CLUSTERID = ? and SIGCARDTYPE = ? and QUEUENAME in " +
                    "(select QUEUENAME from DG.MQ_QUEUE_TYPE_INFO where SERVICETYPE = ?);";

            conn = persisterTxn.getDBConnection(xdmPttServerId, false);
            pStmt = conn.prepareStatement(query);
            pStmt.setInt(1, clusterId);
            pStmt.setInt(2, sigCardType);
            pStmt.setString(3, USER_NOTIFY_SERVICE_TYPE);

            knLogger.info(methodName, "QUERY: Executing - ", query);
            rs = pStmt.executeQuery();
            knLogger.info(methodName, "QUERY: Executed - ");

            if (rs.next()) {
                routingKeyMap.put(USER_NOTIFY_SERVICE_TYPE, rs.getString("ROUTING_KEY"));
            }

            knLogger.info(methodName, "User service key queried - ", routingKeyMap);
            pStmt1 = conn.prepareStatement(query);
            pStmt1.setInt(1, clusterId);
            pStmt1.setInt(2, sigCardType);
            pStmt1.setString(3, GROUP_NOTIFY_SERVICE_TYPE);

            knLogger.info(methodName, "QUERY: Executing - ", query);
            rs1 = pStmt1.executeQuery();
            knLogger.info(methodName, "QUERY: Executed - ");

            if (rs1.next()) {
                routingKeyMap.put(GROUP_NOTIFY_SERVICE_TYPE, rs1.getString("ROUTING_KEY"));
            }

            knLogger.info(methodName, "User service key queried - ", routingKeyMap);
            pStmt2 = conn.prepareStatement(query);
            pStmt2.setInt(1, clusterId);
            pStmt2.setInt(2, sigCardType);
            pStmt2.setString(3, CONTACT_NOTIFY_SERVICE_TYPE);

            knLogger.info(methodName, "QUERY: Executing - ", query);
            rs2 = pStmt2.executeQuery();
            knLogger.info(methodName, "QUERY: Executed - ");

            if (rs2.next()) {
                routingKeyMap.put(CONTACT_NOTIFY_SERVICE_TYPE, rs2.getString("ROUTING_KEY"));
            }

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected exception - ", e);
            throw com.kodiak.common.dao.KnDbUtil.processException(e, "Exception occurred ", xdmPttServerId, methodName, query);

        } finally {
            com.kodiak.common.dao.KnDbUtil.closeResultSet(rs);
            com.kodiak.common.dao.KnDbUtil.closeStatement(pStmt);
            com.kodiak.common.dao.KnDbUtil.closeResultSet(rs1);
            com.kodiak.common.dao.KnDbUtil.closeStatement(pStmt1);
            com.kodiak.common.dao.KnDbUtil.closeResultSet(rs2);
            com.kodiak.common.dao.KnDbUtil.closeStatement(pStmt2);
        }
        knLogger.debug(methodName, "Returning routingKeyMap - ", routingKeyMap);
        return routingKeyMap;
    }

    public List<KnMicroSvcsCommonConfig> retrieveMSSvcsCommonConfig(KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "retrieveMSSvcsCommonConfig(KnPersisterTxn persistTxn)";
        knLogger.info(methodName , "retrieve MicroService Common Properties ");
        Connection conn = null;
        boolean ownedTxn = false;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        List<KnMicroSvcsCommonConfig> microSvcsCommonConf = new ArrayList<>();
        try {
            if (persisterTxn != null) {
                knLogger.debug(methodName, " Data Store before - ", KnDBConst.DataStores.XDM_SHARED_DATA);
                conn = persisterTxn.getDBConnection(xdmPttServerId, true);
            } else {
                conn = KnDBManager.getDBManagerInstance().getConnection(xdmPttServerId, true);
                ownedTxn = true;
            }
           // conn = persisterTxn.getDBConnection(xdmPttServerId, true);
            pStmt = conn.prepareStatement(QUERY_MICROSVCS_COMMONCONFIG);

            knLogger.debug(methodName, "Executing the Query - ", QUERY_MICROSVCS_COMMONCONFIG+"-xdmPttServerId-"+xdmPttServerId);
            rs = pStmt.executeQuery();

            while (rs.next()) {
                KnMicroSvcsCommonConfig microSvcsCommonConfDto = new KnMicroSvcsCommonConfig();
                microSvcsCommonConfDto.setCountryCode(rs.getString(1));
                microSvcsCommonConfDto.setClusterId(rs.getInt(2));
                microSvcsCommonConfDto.setParamName(rs.getString(3));
                microSvcsCommonConfDto.setParamScope(rs.getInt(4));
                microSvcsCommonConfDto.setPath(rs.getString(5));
                microSvcsCommonConfDto.setParamValue(rs.getString(6));
                microSvcsCommonConfDto.setLastUpdateTime(rs.getLong(7));
                microSvcsCommonConf.add(microSvcsCommonConfDto);
            }


        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected exception - ", e);
            throw KnDbUtil.processException(e, "failed to retrieve Microsrvs common config  ", xdmPttServerId, QUERY_MICROSVCS_COMMONCONFIG, QUERY_MICROSVCS_COMMONCONFIG);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            if (ownedTxn) {
                KnDbUtil.closeConnection(conn);
            }
        }
        knLogger.info(methodName, "Configurations fetched - ", microSvcsCommonConf);
        return microSvcsCommonConf;
    }

    public KnMicroSvcsClusterInfo retrieveMSSvcsClusterConfig(KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "retrieveMSSvcsClusterConfig(KnPersisterTxn persistTxn)";
        knLogger.info(methodName , "retrieve MicroService ClusterConfig ");
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        KnMicroSvcsClusterInfo microSvcsClusterDto = new KnMicroSvcsClusterInfo();
        Map<Integer , KnMicroSvcsClusterInfo> microSvcsClusterInfo= new HashMap<>();
        try {
            conn = persisterTxn.getDBConnection(xdmPttServerId, true);
            pStmt = conn.prepareStatement(QUERY_MICROSVCS_CLUSTERINFO);

            knLogger.debug(methodName, "Executing the Query - ", QUERY_MICROSVCS_CLUSTERINFO+"-xdmPttServerId-"+xdmPttServerId);
            rs = pStmt.executeQuery();

            while (rs.next()) {

                microSvcsClusterDto.setCountryCode(rs.getString(1));
                microSvcsClusterDto.setClusterId(rs.getInt(2));
                microSvcsClusterDto.setIsConfigured(rs.getInt(3));
                microSvcsClusterDto.setPtxBucketUriwifi(rs.getString(4));
                microSvcsClusterDto.setDesc(rs.getString(5));
                microSvcsClusterDto.setKodiakMapsWifiUri(rs.getString(6));
                microSvcsClusterDto.setKodiakMapsGeoWifiuri(rs.getString(7));
                microSvcsClusterDto.setLocDataUriWifi(rs.getString(8));
                //microSvcsClusterInfo.put( microSvcsClusterDto.getClusterId(),microSvcsClusterDto);
            }


        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected exception - ", e);
            throw KnDbUtil.processException(e, "failed to retrieve Microsrvs common config  ", xdmPttServerId, QUERY_MICROSVCS_CLUSTERINFO, QUERY_MICROSVCS_CLUSTERINFO);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStmt);
        }
        knLogger.info(methodName, "Micro service cluster info - ", microSvcsClusterInfo);
        return microSvcsClusterDto;
    }

    public List<KnMicroSvcsServiceConfig> retrieveMSSvcsServiceConfig(KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "retrieveMSSvcsServiceConfig(KnPersisterTxn persistTxn)";
        knLogger.info(methodName , "retrieve MicroService Service config ");
        Connection conn = null;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        boolean ownedTxn = false;
        List<KnMicroSvcsServiceConfig> microSvcsServiceConfigInfo = new ArrayList<>();
        try {
            if (persisterTxn != null) {
                knLogger.debug(methodName, " Data Store before - ", KnDBConst.DataStores.XDM_SHARED_DATA);
                conn = persisterTxn.getDBConnection(xdmPttServerId, true);
            } else {
                conn = KnDBManager.getDBManagerInstance().getConnection(xdmPttServerId, true);
                ownedTxn = true;
            }
           //conn = persisterTxn.getDBConnection(xdmPttServerId, true);
            pStmt = conn.prepareStatement(QUERY_MICROSVCS_SERVICECONFIG);

            knLogger.debug(methodName, "Executing the Query - ", QUERY_MICROSVCS_SERVICECONFIG +"-xdmPttServerId-"+xdmPttServerId);
            rs = pStmt.executeQuery();
            while (rs.next()) {
                KnMicroSvcsServiceConfig microSvcsServiceConfigDto = new KnMicroSvcsServiceConfig();
                microSvcsServiceConfigDto.setCountryCode(rs.getString(1));
                microSvcsServiceConfigDto.setClusterId(rs.getInt(2));
                microSvcsServiceConfigDto.setServiceType(rs.getString(3));
                microSvcsServiceConfigDto.setServiceVer(rs.getString(4));
                microSvcsServiceConfigDto.setParamName(rs.getString(5));
                microSvcsServiceConfigDto.setParamScope(rs.getInt(6));
                microSvcsServiceConfigDto.setPath(rs.getString(7));
                microSvcsServiceConfigDto.setParamvalue(rs.getString(8));
                microSvcsServiceConfigDto.setLastUpdateTime(rs.getLong(9));
                microSvcsServiceConfigInfo.add(microSvcsServiceConfigDto);
            }

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected exception - ", e);
            throw KnDbUtil.processException(e, "failed to retrieve Microsrvs common config  ", xdmPttServerId, QUERY_MICROSVCS_SERVICECONFIG, QUERY_MICROSVCS_SERVICECONFIG);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            if (ownedTxn) {
                KnDbUtil.closeConnection(conn);
            }
        }
        knLogger.info(methodName, "Micro service cluster info ");
        return microSvcsServiceConfigInfo;
    }

    public void insertCBTxnFailLog(KnFailedCBTxnLogDTO cbTxnLogDTO, KnPersisterTxn persisterTxn) throws KnDAOException{
        String methodName = "insert(IPersistenceDTO, KnPersisterTxn)";
        Connection conn = null;
        PreparedStatement prepStmt = null;
        String query = null;
        try {
            knLogger.debug( methodName, "failedCBTxnLog DTO - " , cbTxnLogDTO);
            String mdn = cbTxnLogDTO.getMdn();
            int impactedClusterId = cbTxnLogDTO.getImpactedClusterId();
            int txnType = cbTxnLogDTO.getTxnType();
            int loggerBy = cbTxnLogDTO.getLoggedBy();
            long time = System.currentTimeMillis() + impactedClusterId;
            knLogger.debug( methodName, "current time is - " , time);
            query = "INSERT INTO DG.FAILEDCBTXNLOG(MDN,LASTUPDATETIME, IMPACTED_CLUSTERID, TXNTYPE, LOGGEDBY ) VALUES (?, ?, ?, ?, ?)";
            //conn = persistTxn.getDBConnection(pttServerId, false);
            conn = persisterTxn.getDBConnection(xdmPttServerId, false);
            prepStmt = conn.prepareStatement(query);
            prepStmt.setString(1, mdn);
            prepStmt.setLong(2, time);
            prepStmt.setInt(3, impactedClusterId);
            prepStmt.setInt(4, txnType);
            prepStmt.setInt(5, loggerBy);

            knLogger.debug( methodName, "QUERY : Executing " , query , ", mdn : " , KnGDPRTemplate.mdn(mdn) , ", persistTxn : " , persisterTxn);
            prepStmt.executeUpdate();
            knLogger.debug( methodName, "QUERY : Completed.");

        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " , e);
            throw KnDbUtil.processException(e, "Failed to add mdn to failed  list- " + e.getMessage(), xdmPttServerId, KnDAOSourceTypes.FAILEDCBTXNLOG, query);
        } finally {
            KnDbUtil.closePreparedStatement(prepStmt);
            knLogger.debug( methodName, "EXIT : Added Mdn to failed txn  List");
        }
    }


    public void deleteCBTxnFailLog(KnFailedCBTxnLogDTO cbTxnLogDTO, KnPersisterTxn persisterTxn) throws KnDAOException{
        String methodName = "deleteCBTxnFailLog(IPersistenceDTO, KnPersisterTxn)";
        Connection conn = null;
        PreparedStatement prepStmt = null;
        String query = null;
        try {
            knLogger.debug( methodName, "failedCBTxnLog DTO - " , cbTxnLogDTO);
            String mdn = cbTxnLogDTO.getMdn();

            query = "DELETE FROM DG.FAILEDCBTXNLOG WHERE MDN = ?";
            conn = persisterTxn.getDBConnection(xdmPttServerId, false);
            prepStmt = conn.prepareStatement(query);
            prepStmt.setString(1, mdn);

            knLogger.debug( methodName, "QUERY : Executing " , query , ", mdn : " , KnGDPRTemplate.mdn(mdn) , ", persistTxn : " , persisterTxn);
            prepStmt.executeUpdate();
            knLogger.debug( methodName, "QUERY : Completed.");

        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " , e);
            throw KnDbUtil.processException(e, "Failed to delete mdn from  failed txn list- " + e.getMessage(), xdmPttServerId, KnDAOSourceTypes.FAILEDCBTXNLOG, query);
        } finally {
            KnDbUtil.closePreparedStatement(prepStmt);
            knLogger.debug( methodName, "EXIT : Deleted mdn from  failed txn list");
        }
    }
    
    public Map<Integer, KnDataPkgInfoDTO> retrieveDataPkgInfo(KnPersisterTxn persisterTxn)throws KnDAOException{
    	String methodName = "retrieveDataPkgInfo(KnPersisterTxn)";
        Connection conn = null;
        PreparedStatement prepStmt = null;
        ResultSet rs = null;
        Map<Integer, KnDataPkgInfoDTO> dataPkgInfoDTOMap=new HashMap<>();
        String query = null;
        try {
            
            query = "select DATA_PKG_ID,DATA_PKG_NAME,LOC_HISTORY,RECORDING,MESSAGING_DATA from DG.DATA_PACKAGE_INFO";
            conn = persisterTxn.getDBConnection(xdmPttServerId, false);
            prepStmt = conn.prepareStatement(query);
            knLogger.debug( methodName, "QUERY : Executing " , query , "  persistTxn : " , persisterTxn);
            rs = prepStmt.executeQuery();
            while (rs.next()) {
                KnDataPkgInfoDTO knDataPkgInfoDTO = new KnDataPkgInfoDTO();
                knDataPkgInfoDTO.setDataPkgId(rs.getInt("DATA_PKG_ID"));
                knDataPkgInfoDTO.setDataPkgName(rs.getString("DATA_PKG_NAME"));
                knDataPkgInfoDTO.setLocHistory(rs.getInt("LOC_HISTORY"));
                knDataPkgInfoDTO.setRecording(rs.getInt("RECORDING"));
                knDataPkgInfoDTO.setMsgData(rs.getInt("MESSAGING_DATA"));
                dataPkgInfoDTOMap.put(knDataPkgInfoDTO.getDataPkgId(), knDataPkgInfoDTO);
            }

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to retrieveDataPkgInfo - " + e.getMessage(), xdmPttServerId, KnDAOSourceTypes.DATAPACKAGEINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(prepStmt);
        }
		return dataPkgInfoDTOMap;
    	
    }
	
    @Override
	public Map<Integer, Map<Integer, Integer>> retrieveAddlProfileInfoByPkgType(KnPersisterTxn persisterTxn)
			throws KnDAOException {
		String methodName = "retrieveAddlProfileInfoByPkgType(KnPersisterTxn)";
        Connection conn = null;
        PreparedStatement prepStmt = null;
        ResultSet rs = null;
        Map<Integer,Map<Integer, Integer>> addlProfileInfoMap=new HashMap<>();
        boolean ownedTxn = false;
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the Transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            conn = persisterTxn.getDBConnection(xdmPttServerId, false);
            prepStmt = conn.prepareStatement(SELECT_ADDL_PROFILE_BY_PKGTYPE);
            knLogger.debug( methodName, "QUERY : Executing " , SELECT_ADDL_PROFILE_BY_PKGTYPE , "  persistTxn : " , persisterTxn);
            rs = prepStmt.executeQuery();
			while (rs.next()) {
				int pkgIdType = rs.getInt("PKG_ID_TYPE");
				if (addlProfileInfoMap.get(pkgIdType) != null) {
					addlProfileInfoMap.get(pkgIdType).put(rs.getInt("PROFILE_ID"), rs.getInt("PKG_ID"));
				} else {
					Map<Integer, Integer> profileMap = new HashMap<>();
					profileMap.put(rs.getInt("PROFILE_ID"), rs.getInt("PKG_ID"));
					addlProfileInfoMap.put(pkgIdType, profileMap);
				}

			}
            if (ownedTxn) {
                knLogger.debug( methodName, "Saving the transaction");
                persisterTxn.save();
            }
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " , e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to retrieveAddlProfileInfo- " + e.getMessage(), xdmPttServerId, KnDAOSourceTypes.ADDLPROFILEINFO, SELECT_ADDL_PROFILE_BY_PKGTYPE);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(prepStmt);
        }
		return addlProfileInfoMap;
	}

    public int getSubscriberPV(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubscriberPV(String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: getSubscriberProfile ", KnGDPRTemplate.mdn(mdn));
        KnXDMSubscriberInfoDAO subsInfoDAO = KnTablesRegistry.getXDMTablesRegistry().createXDMSubscribreInfoDAO(xdmPttServerId);
        return subsInfoDAO.selectSubscriberPV(mdn, persisterTxn);
    }

    public Map<String, Integer> getSubscribersPV(List<String> mdns, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getSubscribersPV(List<String>, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: getSubscriberProfile ", KnGDPRTemplate.mdnList(mdns));
        KnXDMSubscriberInfoDAO subsInfoDAO = KnTablesRegistry.getXDMTablesRegistry().createXDMSubscribreInfoDAO(xdmPttServerId);
        return subsInfoDAO.selectSubscribersPV(mdns, readOnly, persisterTxn);
    }


    public List<KnQPPpcrfProfileDTO>  retriveQPPpcrfProfile(boolean readOnly, KnPersisterTxn persisterTxn)throws KnDAOException {
        String methodName = "retriveQPPpcrfProfile(boolean, KnPersisterTxn)";
        Connection conn = null;
        PreparedStatement prepStmt = null;
        ResultSet rs = null;
        List<KnQPPpcrfProfileDTO> qpPpcrfProfileDTOS = new ArrayList<>();
        try {

            conn = persisterTxn.getDBConnection(xdmPttServerId, readOnly);
            prepStmt = conn.prepareStatement(SELECT_QPP_PCRF_PROFILE);
            knLogger.debug( methodName, "QUERY : Executing " , SELECT_QPP_PCRF_PROFILE , "  persistTxn : " , persisterTxn);
            rs = prepStmt.executeQuery();
            while (rs.next()) {
                KnQPPpcrfProfileDTO  qpPpcrfProfileDTO = new KnQPPpcrfProfileDTO();
                qpPpcrfProfileDTO.setQppPcrfProfileId(rs.getInt(1));
                qpPpcrfProfileDTO.setApnId(rs.getInt(2));
                qpPpcrfProfileDTO.setMcpttIdentifier(rs.getString(3));
                qpPpcrfProfileDTO.setReservationPriority(rs.getInt(4));
                qpPpcrfProfileDTOS.add(qpPpcrfProfileDTO);

            }

        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " , e);
            throw KnDbUtil.processException(e, "Failed to retriveQPPpcrfProfile- " + e.getMessage(), xdmPttServerId, KnDAOSourceTypes.QPP_PCRF_PROFILE, SELECT_QPP_PCRF_PROFILE);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(prepStmt);
        }
        knLogger.debug( methodName, "resturning dto  - " , qpPpcrfProfileDTOS);
        return qpPpcrfProfileDTOS;
    }
    @Override
    public void createDeviceInfo(KnDeviceInfoPersistDTO deviceInfo, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "createDeviceInfo()";
        Connection conn;
        PreparedStatement pStatement = null;
        String query = null;
        knLogger.debug(methodName, "ENTRY : deviceInfo -> " + deviceInfo);
        try {
            //open a txn if its not already opened
            conn = persisterTxn.getDBConnection(xdmPttServerId, KnDBConst.DataStores.XDM_SHARED_DATA,true);
            pStatement = conn.prepareStatement(QRY_INSERT_DEVICE_INFO);
            pStatement.setString(1, deviceInfo.getDeviceId());
            pStatement.setInt(2, deviceInfo.getDeviceStatus());
            pStatement.setLong(3, deviceInfo.getDeviceActTimeStamp());
            pStatement.setLong(4, deviceInfo.getDeviceLastUsed());
            pStatement.setString(5, deviceInfo.getDeviceDigestPassword());
            pStatement.setInt(6, deviceInfo.getDeviceCreatedAs());
            pStatement.setString(7, deviceInfo.getDeviceClientId());
            if (null != deviceInfo.getDeviceshared()) {
                pStatement.setInt(8, deviceInfo.getDeviceshared());
            } else {
                pStatement.setNull(8, Types.INTEGER);
            }
            pStatement.setBytes(9, deviceInfo.getDeviceIMPI().getBytes(StandardCharsets.UTF_8));
            pStatement.setInt(10, deviceInfo.getDeviceType());
            if (deviceInfo.getDeviceName() != null) {
                pStatement.setBytes(11, deviceInfo.getDeviceName().getBytes(StandardCharsets.UTF_8));
            } else {
                pStatement.setBytes(11, null);
            }
            pStatement.setString(12, deviceInfo.getReqDeviceId());

            if (deviceInfo.getCorpId() != null && deviceInfo.getCorpId() != 0) {
                pStatement.setInt(13, deviceInfo.getCorpId());
            } else {
                pStatement.setNull(13, Types.INTEGER);
            }
            if (null != deviceInfo.getDeviceSubscriberMdn()) {
                pStatement.setString(14, deviceInfo.getDeviceSubscriberMdn().trim());
            } else {
                pStatement.setNull(14, Types.CHAR);
            }
            knLogger.debug(methodName, "QUERY : Executing " + QRY_INSERT_DEVICE_INFO);
            int count = pStatement.executeUpdate();
            knLogger.debug(methodName, "QUERY : Completed." + count);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception - " + e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - " + e);
            throw KnDbUtil.processException(e, "Failed to  - " + e.getMessage(), xdmPttServerId,
                    KnDAOSourceTypes.DEVICE_INFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStatement);
        }
    }

    @Override
    public void insertAdditionalDeviceInfo(KnDeviceAddlInfoPersistDTO deviceAddlInfo, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "insertAdditionalDeviceInfo()";
        Connection conn;
        PreparedStatement pStatement = null;
        String query = null;
        knLogger.debug(methodName, "ENTRY : deviceAddlInfo -> " + deviceAddlInfo);
        try {
            //open a txn if its not already opened
            conn = persisterTxn.getDBConnection(xdmPttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pStatement = conn.prepareStatement(QRY_INSERT_DEVICE_ADDITIONAL_INFO);
            if (null != deviceAddlInfo.getDeviceId()) {
                pStatement.setString(1, deviceAddlInfo.getDeviceId());
            } else {
                pStatement.setNull(1, Types.VARCHAR);
            }
            if (null != deviceAddlInfo.getDeviceSerialNo()) {
                pStatement.setString(2, deviceAddlInfo.getDeviceSerialNo());
            } else {
                pStatement.setNull(2, Types.VARCHAR);
            }
            if (null != deviceAddlInfo.getDeviceIMEI1()) {
                pStatement.setString(3, deviceAddlInfo.getDeviceIMEI1());
            } else {
                pStatement.setNull(3, Types.VARCHAR);
            }
            if (null != deviceAddlInfo.getDeviceIMEI2()) {
                pStatement.setString(4, deviceAddlInfo.getDeviceIMEI2());
            } else {
                pStatement.setNull(4, Types.VARCHAR);
            }
            if (null != deviceAddlInfo.getDeviceVersion()) {
                pStatement.setString(5, deviceAddlInfo.getDeviceVersion());
            } else {
                pStatement.setNull(5, Types.VARCHAR);
            }
            if (null != deviceAddlInfo.getDeviceInfo()) {
                pStatement.setBytes(6, deviceAddlInfo.getDeviceInfo().getBytes(StandardCharsets.UTF_8));
            } else {
                pStatement.setNull(6, Types.VARBINARY);
            }
            knLogger.debug(methodName, "QUERY : Executing " + QRY_INSERT_DEVICE_ADDITIONAL_INFO);
            int count = pStatement.executeUpdate();
            knLogger.debug(methodName, "QUERY : Completed." + count);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception - " + e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - " + e);
            throw KnDbUtil.processException(e, "Failed to  - " + e.getMessage(), xdmPttServerId,
                    KnDAOSourceTypes.DEVICE_INFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStatement);
        }
    }

    @Override
    public void updateAdditionalDeviceInfo(KnDeviceAddlInfoPersistDTO deviceAddlInfo, KnXDMDeviceProvDTO deviceProfileInfo, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateAdditionalDeviceInfo()";
        Connection conn;
        PreparedStatement pStatement = null;
        String query = null;
        knLogger.debug(methodName, "ENTRY : deviceAddlInfo -> " + deviceAddlInfo);

        try {
            //open a txn if its not already opened
            conn = persisterTxn.getDBConnection(xdmPttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pStatement = conn.prepareStatement(QRY_UPDATE_DEVICE_ADDITIONAL_INFO);
            if (null != deviceAddlInfo.getDeviceSerialNo() && deviceAddlInfo.getDeviceSerialNo().equals("")) {
                pStatement.setNull(1, Types.VARCHAR);
            } else {
                pStatement.setString(1, deviceAddlInfo.getDeviceSerialNo() == null ? deviceProfileInfo.getDeviceAddlInfo().getDeviceSerialNo() : deviceAddlInfo.getDeviceSerialNo());
            }
            if (null != deviceAddlInfo.getDeviceIMEI1() && deviceAddlInfo.getDeviceIMEI1().equals("")) {
                pStatement.setNull(2, Types.VARCHAR);
            } else {
                pStatement.setString(2, deviceAddlInfo.getDeviceIMEI1() == null ? deviceProfileInfo.getDeviceAddlInfo().getDeviceIMEI1() : deviceAddlInfo.getDeviceIMEI1());
            }
            if (null != deviceAddlInfo.getDeviceIMEI2() && deviceAddlInfo.getDeviceIMEI2().equals("")) {
                pStatement.setNull(3, Types.VARCHAR);
            } else {
                pStatement.setString(3, deviceAddlInfo.getDeviceIMEI2() == null ? deviceProfileInfo.getDeviceAddlInfo().getDeviceIMEI2() : deviceAddlInfo.getDeviceIMEI2());
            }
            if (null != deviceAddlInfo.getDeviceVersion() && deviceAddlInfo.getDeviceVersion().equals("")) {
                pStatement.setNull(4, Types.VARCHAR);
            } else {
                pStatement.setString(4, deviceAddlInfo.getDeviceVersion() == null ? deviceProfileInfo.getDeviceAddlInfo().getDeviceVersion() : deviceAddlInfo.getDeviceVersion());
            }
            if (null != deviceAddlInfo.getDeviceInfo() && deviceAddlInfo.getDeviceInfo().equals("")) {
                pStatement.setNull(5, Types.VARBINARY);
            } else {
                pStatement.setBytes(5, deviceAddlInfo.getDeviceInfo() == null ? deviceProfileInfo.getDeviceAddlInfo().getDeviceInfo().getBytes(StandardCharsets.UTF_8) : deviceAddlInfo.getDeviceInfo().getBytes(StandardCharsets.UTF_8));
            }

            pStatement.setString(6, deviceAddlInfo.getDeviceId());
            knLogger.debug(methodName, "QUERY : Executing " + QRY_UPDATE_DEVICE_ADDITIONAL_INFO);
            int count = pStatement.executeUpdate();
            knLogger.debug(methodName, "QUERY : Completed." + count);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception - " + e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - " + e);
            throw KnDbUtil.processException(e, "Failed to  - " + e.getMessage(), xdmPttServerId,
                    KnDAOSourceTypes.DEVICE_INFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStatement);
        }
    }

    @Override
    public void modifyDeviceInfo(KnDeviceInfoPersistDTO deviceInfo, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "modifyDeviceInfo()";
        Connection conn;
        PreparedStatement pStatement = null;
        String query = null;
        knLogger.debug(methodName, "ENTRY : deviceInfo -> " + deviceInfo);
        try {
            //open a txn if its not already opened
            conn = persisterTxn.getDBConnection(xdmPttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pStatement = conn.prepareStatement(QRY_UPDATE_DEVICE_INFO);
            pStatement.setString(1, deviceInfo.getReqDeviceId());
            pStatement.setLong(2, deviceInfo.getDeviceLastUsed());
            pStatement.setInt(3, deviceInfo.getDeviceType());
            pStatement.setString(4, deviceInfo.getDeviceSubscriberMdn());
            pStatement.setString(5, deviceInfo.getDeviceId());
            knLogger.debug(methodName, "QUERY : Executing " + QRY_UPDATE_DEVICE_INFO);
            int count = pStatement.executeUpdate();
            knLogger.debug(methodName, "QUERY : Completed." + count);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception - " + e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - " + e);
            throw KnDbUtil.processException(e, "Failed to  - " + e.getMessage(), xdmPttServerId,
                    KnDAOSourceTypes.DEVICE_INFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStatement);
        }
    }

    @Override
	public void updateDeviceInfoStatusAndPassword(KnDeviceInfoPersistDTO deviceInfo, KnPersisterTxn persisterTxn) throws KnDAOException {
		String methodName = "updateDeviceInfoStatusAndPassword()";
		Connection conn;
		PreparedStatement pStatement = null;
		String query = null;
		knLogger.debug(methodName, "ENTRY : deviceInfo -> " + deviceInfo);
		try {
			// open a txn if its not already opened
			StringBuilder queryBuffer = new StringBuilder();
			queryBuffer.append(UPDATE_DEVICE_INFO_QRY);
			queryBuffer.append("DEVICESTATUS").append("=? ");
			/*if (deviceInfo.getDeviceStatus() == 2) {
				queryBuffer.append(",").append("DEVICEDIGESTPASSWD").append("=?");
			}*/
			queryBuffer.append(" WHERE ").append("DEVICEID").append("=?");
			query = queryBuffer.toString();
			conn = persisterTxn.getDBConnection(xdmPttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
			pStatement = conn.prepareStatement(query);
			int columnIndex = 0;
			pStatement.setInt(++columnIndex, deviceInfo.getDeviceStatus());
		/*	if (deviceInfo.getDeviceStatus() == 2) {
				pStatement.setNull(++columnIndex, Types.VARCHAR);
			}*/
			pStatement.setString(++columnIndex, deviceInfo.getDeviceId());

			knLogger.debug(methodName, "QUERY : Executing " + query);
			int count = pStatement.executeUpdate();
			knLogger.debug(methodName, "QUERY : Completed." + count);
		} catch (KnDAOException e) {
			knLogger.error(methodName, "DAO Exception - " + e);
			throw e;
		} catch (Exception e) {
			knLogger.error(methodName, "Unexpected Exception - " + e);
			throw KnDbUtil.processException(e, "Failed to  - " + e.getMessage(), xdmPttServerId,
					KnDAOSourceTypes.DEVICE_INFO, query);
		} finally {
            KnDbUtil.closePreparedStatement(pStatement);
        }
	}
    @Override
    public KnPocSubsAddlInfoDTO getSubsAddlDetails(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubsAddlDetails(String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: mdn :", KnGDPRTemplate.mdn(mdn));
        KnXDMSubscriberInfoDAO subsInfoDAO = KnTablesRegistry.getXDMTablesRegistry().createXDMSubscribreInfoDAO(xdmPttServerId);
        return subsInfoDAO.getSubsAddlDetails(mdn, persisterTxn);
    }

    @Override
    public KnEmergencyInfoDTO getEmergencySubsDestInfo(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getEmergencySubsDestInfo(String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: mdn :", KnGDPRTemplate.mdn(mdn));
        KnXDMSubscriberInfoDAO subsInfoDAO = KnTablesRegistry.getXDMTablesRegistry().createXDMSubscribreInfoDAO(xdmPttServerId);
        return subsInfoDAO.getEmergencySubsDestInfo(mdn, persisterTxn);
    }

    @Override
    public KnSubsProfilePersistDTO getProfileDetailsByMcpttID(String mcpttId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getProfileDetailsByMcpttID(String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: getProfileDetailsByMcpttID ", KnGDPRTemplate.mcpttId(mcpttId));
        KnXDMSubscriberInfoDAO subsInfoDAO = KnTablesRegistry.getXDMTablesRegistry().createXDMSubscribreInfoDAO(xdmPttServerId);
        return subsInfoDAO.selectSubscriberInfoByMcPttId(mcpttId, persisterTxn);
    }

    @Override
    public KnSubsProfilePersistDTO getProfileDetailsByMcIDAndUpmIndex(String mcId,String upmIndex, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getProfileDetailsByMcIDAndUpmIndex(String,String,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: ", KnGDPRTemplate.mcId(mcId)," upmIndex ",upmIndex);
        KnXDMSubscriberInfoDAO subsInfoDAO = KnTablesRegistry.getXDMTablesRegistry().createXDMSubscribreInfoDAO(xdmPttServerId);
        return subsInfoDAO.selectSubscriberInfoByMcIdAndUPMIndex(mcId,upmIndex, persisterTxn);
    }

    @Override
    public List<KnSubsProfileDTO> getProfileDetailsListByMcID(String mcId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getProfileDetailsListByMcID(String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: ", KnGDPRTemplate.mcId(mcId));
        KnXDMSubscriberInfoDAO subsInfoDAO = KnTablesRegistry.getXDMTablesRegistry().createXDMSubscribreInfoDAO(xdmPttServerId);
        return subsInfoDAO.getProfileDetailsListByMcID(mcId, persisterTxn);
    }

    @Override
    public KnCorpUserProfileDTO getUserProfileNameByindex(int corpId, int upmIndex, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getProfileDetailsListByMcID(String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: corpId ", corpId," upmIndex ",upmIndex);
        KnXDMCBUserProfileMgmtDAO upmDAO = new KnXDMCBUserProfileMgmtDAO(xdmPttServerId);
        return upmDAO.getUserProfileNameByIndex(corpId, upmIndex);
    }

    @Override
    public KnCorpUserProfileDTO getUserProfileById( String userProfileId) throws KnDAOException {
        String methodName = "getProfileDetailsListByMcID(String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: corpId ", userProfileId," upmIndex ",userProfileId);
        KnXDMCBUserProfileMgmtDAO upmDAO = new KnXDMCBUserProfileMgmtDAO(xdmPttServerId);
        return upmDAO.getUserProfileById( userProfileId);
    }

    @Override
    public void createDeviceImpiInfo(KnDeviceImpiInfoPersistDTO deviceImpiInfo, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "createDeviceImpiInfo()";
        Connection conn;
        PreparedStatement pStatement = null;
        String query = null;
        knLogger.debug(methodName, "ENTRY : deviceInfo -> " + deviceImpiInfo);
        try {
            //open a txn if its not already opened
            conn = persisterTxn.getDBConnection(xdmPttServerId, KnDBConst.DataStores.XDM_SHARED_DATA,true);
            pStatement = conn.prepareStatement(QRY_INSERT_DEVICEIMPIINFO);
            pStatement.setBytes(1, deviceImpiInfo.getDeviceImpi().getBytes(StandardCharsets.UTF_8));
            pStatement.setBytes(2, deviceImpiInfo.getDeviceImpu().getBytes(StandardCharsets.UTF_8));
            knLogger.debug(methodName, "QUERY : Executing " + QRY_INSERT_DEVICEIMPIINFO);
            int count = pStatement.executeUpdate();
            knLogger.debug(methodName, "QUERY : Completed." + count);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception - " + e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - " + e);
            throw KnDbUtil.processException(e, "Failed to  - " + e.getMessage(), xdmPttServerId,
                    KnDAOSourceTypes.DEVICEIMPIINFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStatement);
        }
    }


    @Override
    public void deleteDeviceInfo(KnDeviceInfoPersistDTO deviceInfo, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteDeviceInfo()";
        Connection conn;
        PreparedStatement pStatement = null;
        String query = null;
        int index = 1;
        knLogger.debug(methodName, "ENTRY : deviceInfo -> " + deviceInfo);
        try {
            //open a txn if its not already opened
            conn = persisterTxn.getDBConnection(xdmPttServerId, KnDBConst.DataStores.XDM_SHARED_DATA,true);
            if(null != deviceInfo.getDeviceIdList() && !deviceInfo.getDeviceIdList().isEmpty()){
                query = "DELETE FROM DG.DEVICE_INFO WHERE DEVICEID IN (DEVICEIDLIST)";
                query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(deviceInfo.getDeviceIdList(),query,"DEVICEIDLIST");
                pStatement = conn.prepareStatement(query);
                for(String deviceId : deviceInfo.getDeviceIdList()){
                    pStatement.setString(index++, deviceId);
                }
                int count = pStatement.executeUpdate();
                knLogger.debug(methodName, "QUERY : Completed." + count);
            } else {
                pStatement = conn.prepareStatement(QRY_DELETE_DEVICE_INFO);
                pStatement.setString(1, deviceInfo.getDeviceId());
                knLogger.debug(methodName, "QUERY : Executing " + QRY_DELETE_DEVICE_INFO);
                int count = pStatement.executeUpdate();
                knLogger.debug(methodName, "QUERY : Completed." + count);
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception - " + e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - " + e);
            throw KnDbUtil.processException(e, "Failed to  - " + e.getMessage(), xdmPttServerId,
                    KnDAOSourceTypes.DEVICE_INFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStatement);
        }

    }

    @Override
    public void deleteDeviceAddlInfo(KnDeviceInfoPersistDTO deviceInfo, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteDeviceAddlInfo()";
        Connection conn;
        PreparedStatement pStatement = null;
        String query = null;
        knLogger.debug(methodName, "ENTRY : deviceInfo -> " + deviceInfo);
        try {
            //open a txn if its not already opened
            conn = persisterTxn.getDBConnection(xdmPttServerId, KnDBConst.DataStores.XDM_SHARED_DATA,true);
            pStatement = conn.prepareStatement(QRY_DELETE_DEVICE_ADDLINFO);
            pStatement.setString(1, deviceInfo.getDeviceId());
            knLogger.debug(methodName, "QUERY : Executing " + QRY_DELETE_DEVICE_ADDLINFO);
            int count = pStatement.executeUpdate();
            knLogger.debug(methodName, "QUERY : Completed." + count);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception - " + e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - " + e);
            throw KnDbUtil.processException(e, "Failed to  - " + e.getMessage(), xdmPttServerId,
                    KnDAOSourceTypes.DEVICE_INFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStatement);
        }

    }

    @Override
    public void deleteDeviceImpiInfo(KnDeviceInfoPersistDTO deviceInfo, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "deleteDeviceImpiInfo()";
        Connection conn;
        PreparedStatement pStatement = null;
        String query = null;
        int index = 1;
        knLogger.debug(methodName, "ENTRY : deviceInfo -> " + deviceInfo);
        try {
            //open a txn if its not already opened
            conn = persisterTxn.getDBConnection(xdmPttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            if(null != deviceInfo.getDeviceIdList() && !deviceInfo.getDeviceIdList().isEmpty()){
                query = "DELETE FROM DG.DEVICEIMPIINFO WHERE DEVICE_IMPI IN ( SELECT DEVICE_IMPI FROM DG.DEVICE_INFO WHERE DEVICEID IN (DEVICEIDLIST))";
                query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(deviceInfo.getDeviceIdList(),query,"DEVICEIDLIST");
                pStatement = conn.prepareStatement(query);
                for(String deviceId : deviceInfo.getDeviceIdList()){
                    pStatement.setString(index++, deviceId);
                }
                int count = pStatement.executeUpdate();
                knLogger.debug(methodName, "QUERY : Completed." + count);
            } else {
                pStatement = conn.prepareStatement(QRY_DELETE_DEVICEIMPIINFO);
                pStatement.setString(1, deviceInfo.getDeviceId());
                knLogger.debug(methodName, "QUERY : Executing " + QRY_DELETE_DEVICEIMPIINFO);
                int count = pStatement.executeUpdate();
                knLogger.debug(methodName, "QUERY : Completed." + count);
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception - " + e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - " + e);
            throw KnDbUtil.processException(e, "Failed to  - " + e.getMessage(), xdmPttServerId,
                    KnDAOSourceTypes.DEVICEIMPIINFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStatement);
        }

    }

    @Override
    public void updateDigestPwd(String deviceId, String pwd, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "updateDigestPwd(String,String, KnPersisterTxn)";
        Connection conn = null;
        PreparedStatement pStatement = null;
        int cnt = 0;
        knLogger.entry(methodName, "deviceId-", deviceId);
        try {

            conn = persisterTxn.getDBConnection(xdmPttServerId, KnDBConst.DataStores.XDM_SHARED_DATA,true);
            pStatement = conn.prepareStatement(QRY_UPD_DIGESTPWD);
            pStatement.setString(1, pwd);
            pStatement.setString(2, deviceId);

            knLogger.debug(methodName, "QUERY : Executing ",QRY_UPD_DIGESTPWD);
            cnt = pStatement.executeUpdate();
            knLogger.debug(methodName, "QUERY : Completed. - ", cnt);

        } catch (SQLException e) {
            knLogger.error(methodName, "SQL Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to - " + e.getMessage(), xdmPttServerId, KnDAOSourceTypes.DEVICE_INFO, QRY_UPD_DIGESTPWD);

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - " + e);
            throw KnDbUtil.processException(e, "Failed to  - " + e.getMessage(), xdmPttServerId,
                    KnDAOSourceTypes.DEVICE_INFO, QRY_UPD_DIGESTPWD);
        } finally {
            KnDbUtil.closePreparedStatement(pStatement);
        }

        knLogger.exit(methodName, "no of rows updated -", cnt);
    }

    public int getDeviceCountForCorpId(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getDeviceCountForCorpId()";
        String query = COUNT_DEVICES_QUERY;
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        int count = 0;
        knLogger.debug(methodName, "ENTRY : Getting device count for corpId: " + corpId);
        try {
            conn = persisterTxn.getDBConnection(xdmPttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pStmt = conn.prepareStatement(query);
            pStmt.setInt(1, corpId);
            rs = pStmt.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
            knLogger.info(methodName, "QUERY: Executed the Query : count - ", count);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - " + e);
            throw KnDbUtil.processException(e, "Failed to  - " + e.getMessage(), xdmPttServerId,
                    KnDAOSourceTypes.DEVICE_INFO, COUNT_DEVICES_QUERY);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStmt);
        }

        return count;
    }

    public int getDeviceCountByMdnAndCorpId(String mdn, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getDeviceCountForCorpId()";
        String query = COUNT_MDN_DEVICES_QUERY;
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        int count = 0;
        knLogger.debug(methodName, "ENTRY : Getting device count for corpId: " + corpId);
        try {
            conn = persisterTxn.getDBConnection(xdmPttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pStmt = conn.prepareStatement(query);
            pStmt.setInt(1, corpId);
            pStmt.setString(2, mdn);
            rs = pStmt.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
            knLogger.debug(methodName, "QUERY: Executed the Query : count - ", count);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - " + e);
            throw KnDbUtil.processException(e, "Failed to  - " + e.getMessage(), xdmPttServerId,
                    KnDAOSourceTypes.DEVICE_INFO, COUNT_MDN_DEVICES_QUERY);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStmt);
        }

        return count;
    }

    @Override
    public String getDeviceImpuInfo(String deviceImpl, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getDeviceImpuInfo(String, KnPersisterTxn)";
        Connection conn = null;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        int cnt = 0;
        String deviceImpu = null;

        knLogger.entry(methodName, "deviceImpl-", deviceImpl);
        try {

            conn = persisterTxn.getDBConnection(xdmPttServerId,KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pStatement = conn.prepareStatement(QRY_SEL_DEVICEIMPL);
            pStatement.setBytes(1, deviceImpl.getBytes(StandardCharsets.UTF_8));

            knLogger.debug(methodName, "QUERY : Executing "+QRY_SEL_DEVICEIMPL);
            rs = pStatement.executeQuery();
            while (rs.next()) {
                deviceImpu = new String(rs.getBytes(1),StandardCharsets.UTF_8);
            }
            knLogger.debug(methodName, "QUERY : Completed. - ", deviceImpu);

        } catch (SQLException e) {
            knLogger.error(methodName, "SQL Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to - " + e.getMessage(), xdmPttServerId, KnDAOSourceTypes.DEVICEIMPIINFO, QRY_SEL_DEVICEIMPL);

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - " + e);
            throw KnDbUtil.processException(e, "Failed to  - " + e.getMessage(), xdmPttServerId,
                    KnDAOSourceTypes.DEVICEIMPIINFO, QRY_SEL_DEVICEIMPL);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStatement);
        }

        knLogger.exit(methodName, "deviceImpu", deviceImpu);
        return deviceImpu;
    }

    @Override
    public boolean checkDeviceExsists(String deviceId, KnPersisterTxn persisterTxn) throws KnDAOException {

        final String methodName = "checkDeviceExsists(String, KnPersisterTxn)";
        Connection conn = null;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String device_Id = null;
        boolean deviceExsists = false;
        knLogger.entry(methodName, "deviceId-", deviceId);
        try {
            conn = persisterTxn.getDBConnection(xdmPttServerId,KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pStatement = conn.prepareStatement(QRY_SEL_DEVICEINFO);
            pStatement.setString(1, deviceId);

            knLogger.debug(methodName, "QUERY : Executing "+QRY_SEL_DEVICEINFO);
            rs = pStatement.executeQuery();
            while (rs.next()) {
                device_Id = rs.getString(1);
            }
            deviceExsists = (null != device_Id);

            knLogger.debug(methodName, "QUERY : Completed. - ", deviceExsists);

        } catch (SQLException e) {
            knLogger.error(methodName, "SQL Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to - " + e.getMessage(), xdmPttServerId, KnDAOSourceTypes.DEVICE_INFO, QRY_SEL_DEVICEINFO);

        }  catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - " + e);
            throw KnDbUtil.processException(e, "Failed to  - " + e.getMessage(), xdmPttServerId,
                    KnDAOSourceTypes.DEVICE_INFO, QRY_SEL_DEVICEINFO);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
        }

        knLogger.exit(methodName, "deviceId exsists-", deviceExsists);
        return deviceExsists;
    }

    @Override
    public Map<String,Boolean> getGroupMemberList(int groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getGroupMemberList(groupId, persisterTxn)";
        knLogger.debug(methodName, "ENTRY: Retrieving Member MDNs for groupId - " + groupId);
        Map<String,Boolean> mapMDNs=new HashMap<>();
        try {
            KnXDMCorpInfoDAO corpInfoDAO = KnTablesRegistry.getXDMTablesRegistry().
                    createXDMCorpInfoDAO(xdmPttServerId);
            mapMDNs=corpInfoDAO.getGroupMemberList(groupId,persisterTxn);
            return mapMDNs;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception has been occurred " + e.getMessage());
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected exception has occurred " + e.getMessage());
            throw KnDbUtil.processException(e, "Failed to fetch GroupMember MDNs ...", String.valueOf(groupId), KnDAOSourceTypes.CORPGROUPMEMBERLIST, null);
        } finally {
            knLogger.debug(methodName, "EXIT: ");
        }
    }

    @Override
    public Map<String,Boolean> getGroupMemberListWithBC(int groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getGroupMemberListWithBC(groupId, persisterTxn)";
        knLogger.debug(methodName, "ENTRY: Retrieving Member MDNs for groupId - " + groupId);
        Map<String,Boolean> mapMDNs=new HashMap<>();
        try {
            KnXDMCorpInfoDAO corpInfoDAO = KnTablesRegistry.getXDMTablesRegistry().
                    createXDMCorpInfoDAO(xdmPttServerId);
            mapMDNs=corpInfoDAO.getGroupMemberListWithBC(groupId,persisterTxn);
            return mapMDNs;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception has been occurred " + e.getMessage());
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected exception has occurred " + e.getMessage());
            throw KnDbUtil.processException(e, "Failed to fetch GroupMember MDNs ...", String.valueOf(groupId), KnDAOSourceTypes.CORPGROUPMEMBERLIST, null);
        } finally {
            knLogger.debug(methodName, "EXIT: ");
        }
    }
    @Override
    public Integer getMemberCountFromMemberList(int groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getMemberCountFromMemberList(groupId, persisterTxn)";
        knLogger.debug(methodName, "ENTRY: Retrieving memberCount for groupId - " + groupId);
        Integer memberCount;
        try {
            KnXDMCorpInfoDAO corpInfoDAO = KnTablesRegistry.getXDMTablesRegistry().
                    createXDMCorpInfoDAO(xdmPttServerId);
            memberCount = corpInfoDAO.getMemberCountFromMemberList(groupId, persisterTxn);
            return memberCount;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception has been occurred " + e.getMessage());
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected exception has occurred " + e.getMessage());
            throw KnDbUtil.processException(e, "Failed to fetch osmListId  ...", String.valueOf(groupId), KnDAOSourceTypes.CORP_GROUP_MEMBER_COUNT, null);
        } finally {
            knLogger.debug(methodName, "EXIT: ");
        }
    }

    @Override
    public KnCorpGpInfoDTO getCorpGroupInfoList(int groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getCorpGroupInfoList(groupId, persisterTxn)";
        knLogger.debug(methodName, "ENTRY: Retrieving osmListId for groupId - " + groupId);
        KnCorpGpInfoDTO knCorpGpInfoDTO=null;
        try {
            KnXDMCorpInfoDAO corpInfoDAO = KnTablesRegistry.getXDMTablesRegistry().
                    createXDMCorpInfoDAO(xdmPttServerId);
            knCorpGpInfoDTO=corpInfoDAO.getCorpGroupInfoList(groupId,persisterTxn);
            return knCorpGpInfoDTO;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception has been occurred " + e.getMessage());
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected exception has occurred " + e.getMessage());
            throw KnDbUtil.processException(e, "Failed to fetch osmListId  ...", String.valueOf(groupId), KnDAOSourceTypes.CORPGROUPINFO, null);
        } finally {
            knLogger.debug(methodName, "EXIT: ");
        }
    }

    @Override
    public String getSharedCorpGroup(int groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSharedCorpGroup";
        knLogger.debug(methodName, "ENTRY: Retrieving sharedCorpid for groupId - " + groupId);
        String sharedCorpid=null;
        try {
            KnXDMCorpInfoDAO corpInfoDAO = KnTablesRegistry.getXDMTablesRegistry().
                    createXDMCorpInfoDAO(xdmPttServerId);
            sharedCorpid=corpInfoDAO.getSharedCorpGroup(groupId,persisterTxn);
            return sharedCorpid;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception has been occurred " + e.getMessage());
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected exception has occurred " + e.getMessage());
            throw KnDbUtil.processException(e, "Failed to fetch sharedCorpid ...", String.valueOf(groupId), KnDAOSourceTypes.CORPGROUPINFO, null);
        } finally {
            knLogger.debug(methodName, "EXIT: ");
        }
    }

    @Override
    public List<KnCorpGpInfoDTO> getCorpGroupInfoDetails(List<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getCorpGroupInfoList(groupId, persisterTxn)";
        knLogger.debug(methodName, "ENTRY: Retrieving osmListId for groupIds - " + groupIds);
        List<KnCorpGpInfoDTO> knCorpGpInfoDTOs=null;
        try {
            KnXDMCorpInfoDAO corpInfoDAO = KnTablesRegistry.getXDMTablesRegistry().
                    createXDMCorpInfoDAO(xdmPttServerId);
            knCorpGpInfoDTOs=corpInfoDAO.getCorpGroupInfoDetails(groupIds,persisterTxn);
            return knCorpGpInfoDTOs;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception has been occurred " + e.getMessage());
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected exception has occurred " + e.getMessage());
            throw KnDbUtil.processException(e, "Failed to fetch osmListId  ...", String.valueOf(groupIds), KnDAOSourceTypes.CORPGROUPINFO, null);
        } finally {
            knLogger.debug(methodName, "EXIT: ");
        }
    }



    @Override
    public List<KnSubsProfileDTO> retrieveBulkSubscribersInfo(List<String> mdns, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "retrieveBulkSubscribersInfo(List,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: Retrieve BulkSubscriber Profile for MDNS ");
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String query = null;
        int index = 1;
        ArrayList<KnSubsProfileDTO> listOfDTOs = new ArrayList<>();
        try {
            conn = persisterTxn.getDBConnection(xdmPttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = GET_BULK_PROFILE_DETAILS; /* .replaceAll(MDN_LIST,KnGeneralUtil.formCommaSeperatedIdList(mdns)); */
            query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(mdns,query,"MDNLIST");
            stmt = conn.prepareStatement(query);
            for(String mdn : mdns){
                stmt.setString(index++,mdn);
            }
            knLogger.debug(methodName, "Executing query - ", query);
            rs = stmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                KnSubsProfileDTO knSubsProfileDTO = new KnSubsProfileDTO();
                knSubsProfileDTO.setMdn(rs.getString(MDN).trim());
                knSubsProfileDTO.setPocHome(rs.getString(POC_HOME));
                knSubsProfileDTO.setCorpId(rs.getInt(CORP_ID));
                knSubsProfileDTO.setCorpSubscriptionType(rs.getInt(CORP_SUBSCRIPTION_TYPE));
             /*   String subsFs2=rs.getString(SUBS_FS2)!=null?rs.getString(SUBS_FS2):KnGeneralUtil.convertLongToHexString(rs.getLong(SUBS_FS1));
                knSubsProfileDTO.setSubscriberFS2(subsFs2);
                String clientsFs2=rs.getString(CLIENT_FS2)!=null?rs.getString(CLIENT_FS2):KnGeneralUtil.convertLongToHexString(rs.getLong(CLIENT_FS1));
                knSubsProfileDTO.setClientFS2(clientsFs2);
                String activeFs2=rs.getString(ACTIVE_FS2)!=null?rs.getString(ACTIVE_FS2):KnGeneralUtil.convertLongToHexString(rs.getLong(ACTIVE_FS1));
                knSubsProfileDTO.setActiveFS2(activeFs2);*/
                knSubsProfileDTO.setClientMajorVersion(rs.getInt(CLIENTPV_MAJORVERSION));
                knSubsProfileDTO.setSubscriberName(rs.getString(SUBSCRIBER_NAME));
                knSubsProfileDTO.setClientMinorVersion(rs.getInt(CLIENTPV_MINORVERSION));
                knSubsProfileDTO.setClientType(rs.getInt(CLIENT_TYPE));
                knSubsProfileDTO.setQpppackId(rs.getInt(QPPPACKID));
                knSubsProfileDTO.setServiceAuthStatus(rs.getInt(SERVICE_AUTH_STATUS));
                knSubsProfileDTO.setAliasMdn(rs.getString(ALIAS_MDN));
                knSubsProfileDTO.setUserAgent(rs.getString(USER_AGENT));
                knSubsProfileDTO.setActiveFS2(rs.getString(ACTIVE_FS2));
                if (null != rs.getString(MC_PTTID)) {
                    knSubsProfileDTO.setMcpttId(new String(rs.getBytes(MC_PTTID), StandardCharsets.UTF_8));
                }
                if (null != rs.getString(MC_VIDEOID)) {
                    knSubsProfileDTO.setMcVideoId(new String(rs.getBytes(MC_VIDEOID), StandardCharsets.UTF_8));
                }
                if (null != rs.getString(MC_DATAID)) {
                    knSubsProfileDTO.setMcDataId(new String(rs.getBytes(MC_DATAID), StandardCharsets.UTF_8));
                }
                if (null != rs.getString(MC_ID)) {
                    knSubsProfileDTO.setMcId(new String(rs.getBytes(MC_ID), StandardCharsets.UTF_8));
                }
                knSubsProfileDTO.setCameraType(rs.getInt(CAMERA_TYPE));
                listOfDTOs.add(knSubsProfileDTO);
            }
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve Bulk Subscriber Profile - " + e.getMessage(), xdmPttServerId, KnDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            knLogger.debug(methodName, "EXIT ");
        }
        return listOfDTOs;
    }

    @Override
    public void updateSubscriberProfile(KnSubsProfileDTO subsProfileDTO, KnPersisterTxn persisterTxn)throws KnDAOException {
            String methodName = "updateSubscriberProfile(KnSubsProfileDTO,KnPersisterTxn)";
            knLogger.debug( methodName, "Entry with subsProfileDTO members MDN- ",subsProfileDTO.getMdn()," UserAgent-" , subsProfileDTO.getUserAgent(), "ClientMajorVersion-",subsProfileDTO.getClientMajorVersion(),
                                                "ClientFS2-",subsProfileDTO.getClientFS2(),"ActiveFS2-",subsProfileDTO.getActiveFS2());
            Connection conn;
            PreparedStatement pStatement = null;
            ResultSet rs = null;
            String query = null;
            try {
               // long lastProfileUpdateTime = Calendar.getInstance().getTimeInMillis();
                query = QRY_UPDATE_SUBSPROFILE_FOR_MDN;
                conn = persisterTxn.getDBConnection(xdmPttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
                pStatement = conn.prepareStatement(query);
                pStatement.setString(1, subsProfileDTO.getUserAgent());
                pStatement.setInt(2, subsProfileDTO.getClientMajorVersion());
                pStatement.setString(3, com.kodiak.common.resources.KnGeneralUtil.getFeatureSet(subsProfileDTO.getClientFS2()));
                pStatement.setString(4, com.kodiak.common.resources.KnGeneralUtil.getFeatureSet(subsProfileDTO.getActiveFS2()));
                pStatement.setLong(5, subsProfileDTO.getLastProfileUpdateTime());
                pStatement.setString(6, subsProfileDTO.getMdn());
                knLogger.debug( methodName, "QUERY : Executing " + query + ", persisterTxn : " + persisterTxn);
                int count = pStatement.executeUpdate();
                knLogger.debug( methodName, "QUERY : Completed." + count);
                knLogger.info( methodName, "Updated SubsProfile for MDN:  " + KnGDPRTemplate.mdn(subsProfileDTO.getMdn()));
            } catch (Exception e) {
                knLogger.error( methodName, "Unexpected Exception - " + e);
                throw KnDbUtil.processException(e, "Failed to Update SubsProfile - " + e.getMessage(), xdmPttServerId, KnDAOSourceTypes.POCSUBSCRINFO, query);
            } finally {
                KnDbUtil.closeResultSet(rs);
                KnDbUtil.closePreparedStatement(pStatement);
                knLogger.debug( methodName, "EXIT : MDN ->" +  KnGDPRTemplate.mdn(subsProfileDTO.getMdn()));
            }
        }

    @Override
    public List<KnXDMOSMInfoRequestDTO> getUniqueFieldsOSMInfoList(int corpGroupId, KnPersisterTxn persisterTxn) throws KnDAOException{
        String methodName = "getUniqueFieldsOSMInfoList(int,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: Retrieve UniqueFields ");
        Connection conn = null;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;
        ArrayList<KnXDMOSMInfoRequestDTO> listOfDTOs = new ArrayList<>();
        try {
            conn = persisterTxn.getDBConnection(xdmPttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = GET_UNIQUE_FIELDS_OSMINFO_LIST;
            pStatement = conn.prepareStatement(query);
            pStatement.setInt(1,corpGroupId);
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pStatement.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                KnXDMOSMInfoRequestDTO knXDMOSMInfoRequestDTO=new KnXDMOSMInfoRequestDTO();
                knXDMOSMInfoRequestDTO.setMsgId(rs.getString("OSMID"));
                knXDMOSMInfoRequestDTO.setMsg(rs.getString("OSMESSAGE"));
                knXDMOSMInfoRequestDTO.setMsgShortText(rs.getString("OSMSHORTTEXT"));
                knXDMOSMInfoRequestDTO.setMsgType(rs.getString("OSMTYPE"));
                listOfDTOs.add(knXDMOSMInfoRequestDTO);
            }
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve OSMINFOFIELDS - " + e.getMessage(), xdmPttServerId, KnDAOSourceTypes.CORPGROUPINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug(methodName, "EXIT ");
        }
        return listOfDTOs;
    }

    @Override
    public Integer getPOCCallTable(String db, String pocHome,KnPersisterTxn persisterTxn) throws KnDAOException{
        String methodName = "getPOCCallTable(String,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: Retrieve Connection ");
        Connection conn = null;
        PreparedStatement pStatement = null;
        Integer hangTimeOut=null;
        ResultSet rs = null;
        String query = null;
        try {
            conn = persisterTxn.getDBConnection(db,false);
            query = "SELECT HANGTIMEOUT FROM DG.SC_POC_CALLP_"+pocHome;
            pStatement = conn.prepareStatement(query);
            knLogger.debug(methodName, "Connection  - ", conn);
            rs = pStatement.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                hangTimeOut= rs.getInt(1);
                knLogger.debug(methodName, "hangTimeOut  - ",hangTimeOut);
            }
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve ESMDSN POCCALLTable - " + e.getMessage(), xdmPttServerId, KnDAOSourceTypes.EMSDSN, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug(methodName, "EXIT");
        }
        return hangTimeOut;
    }

    /**
     * method to select the SIP Proxy Service Config
     *
     * @param persistTxn KnPersisterTxn
     * @return KnSipProxySvcConfigDTO
     * @throws KnDAOException
     */
    @Override
    public KnSIPProxySvcConfigDTO selectSIPProxySvcConfig(String pocPttServerId, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "selectSIPProxySvcConfig(String, KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt=null;
        ResultSet rs = null;
        boolean ownedTxn = false;
        KnSIPProxySvcConfigDTO sipProxySvcConfigDTO = new KnSIPProxySvcConfigDTO();
        knLogger.info( methodName, "ENTRY: Select SIP Proxy Srvc Config ",persistTxn);
        try {
            if (persistTxn == null) {
                persistTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug( methodName, "Opening the transaction ");
                ownedTxn = true;
            }

            query = SELECT_ALL_QRY;

            conn = persistTxn.getDBConnection(xdmPttServerId, true);
            pStmt = conn.prepareStatement(query);
            pStmt.setString(1, pocPttServerId);

            knLogger.debug( methodName, "Query: Executing - ", query);
            rs = pStmt.executeQuery();
            knLogger.debug( methodName, "Query: Executed ");

            if (rs.next()) {
                sipProxySvcConfigDTO.setPttServerId(rs.getString(PTT_SERVER_ID));
                sipProxySvcConfigDTO.setSipProxyURI(rs.getString(SIP_PROXY_URI));
                sipProxySvcConfigDTO.setClientConnRetryInterval(rs.getInt(CLIENT_CONNECTION_RETRY_INTERVAL));
                sipProxySvcConfigDTO.setClientConnSecurityLevel(rs.getInt(CLIENT_CONNECTION_SECURITY_LEVEL));
                sipProxySvcConfigDTO.setClientSipReferTxnTimeout(rs.getInt(CLIENT_SIP_REFER_TXN_TIMEOUT));
                sipProxySvcConfigDTO.setClientSipTxnTimeout(rs.getInt(CLIENT_SIP_TXN_TIMEOUT));
                sipProxySvcConfigDTO.setDetectWifiNatTcpTimeout(rs.getInt(DETECT_WIFI_NAT_TCP_TIMEOUT));
                sipProxySvcConfigDTO.setMaxClientConnRtyAttempts(rs.getInt(MAX_CLIENT_CONN_RETRY_ATTEMPTS));
                sipProxySvcConfigDTO.setMaxTcpKaTimerOnWifi(rs.getInt(MAX_TCP_KA_TIMER_ON_WIFI));
                sipProxySvcConfigDTO.setTcpKaTimerOnMacroCellular(rs.getInt(TCP_KA_TIMER_ON_MACRO_CELLULAR));
                sipProxySvcConfigDTO.setMinTcpKaTimerOnWifi(rs.getInt(MIN_TCP_KA_TIMER_ON_WIFI));
                sipProxySvcConfigDTO.setWifiSsidTimeoutMapSize(rs.getInt(WIFI_SSID_TIMEOUT_MAP_SIZE));
                sipProxySvcConfigDTO.setWifiTcpKaTimerIncrVal(rs.getInt(WIFI_TCP_KA_TIMER_INCR_VAL));
                sipProxySvcConfigDTO.setSipProxyURIForBCS(rs.getString(SIP_PROXY_URI_FOR_BCS));

            } else {
                knLogger.error( methodName, "SIP Proxy  service Config doesn't exist");
                if (ownedTxn) {
                    persistTxn.rollback();
                }
                throw new KnDBPersistenceException(KnErrorCodes.DAO.ROW_NOT_FOUND, "SIP Proxy  service Config  Doesnt exist", xdmPttServerId,
                        KnDAOSourceTypes.SIPPROXYSVCCONFIG, query);
            }

            if (ownedTxn) {
                knLogger.debug( methodName, "Saving the Transaction");
                persistTxn.save();
            }
            knLogger.debug( methodName, "returning SIP Proxy  Srvc Config ", sipProxySvcConfigDTO);
            return sipProxySvcConfigDTO;

        } catch (KnDAOException dbConne) {
            knLogger.error( methodName, "DAO Exception occurred");
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw dbConne;
        } catch (SQLException sqlE) {
            knLogger.error( methodName, "SQL Exception occurred");
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(sqlE, "Failed to select SIP Proxy  service Config  - " + sqlE.getMessage(),
                    xdmPttServerId, KnDAOSourceTypes.SIPPROXYSVCCONFIG, query);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - ", e);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to select SIP Proxy  service Config  - " + e.getMessage(),
                    xdmPttServerId, KnDAOSourceTypes.SIPPROXYSVCCONFIG, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStmt);
            knLogger.info( methodName, "EXIT : select SIP Proxy  service Config ");
        }
    }

    @Override
    public Map<Integer,Integer> getZoneChannelMap(int groupId,String mdn ,KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "getSubsPttTgList(groupId, persisterTxn)";
        knLogger.debug(methodName, "ENTRY: Retrieving for groupId - ",groupId,"mdn ",KnGDPRTemplate.mdn(mdn));
        try {
            KnXDMCorpInfoDAO corpInfoDAO = KnTablesRegistry.getXDMTablesRegistry().
                    createXDMCorpInfoDAO(xdmPttServerId);
            return corpInfoDAO.getZoneChannelMap(groupId,mdn,persistTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception has been occurred " + e.getMessage());
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected exception has occurred " + e.getMessage());
            throw KnDbUtil.processException(e, "Failed to fetch corpAddlTGInfoDTO  ...", String.valueOf(groupId), KnDAOSourceTypes.SUBSCRPTTRADIOTGLIST, null);
        } finally {
            knLogger.debug(methodName, "EXIT: ");
        }
    }

    @Override
    public KnXDMTalkGroupInfoDTO getGroupPriority(int groupId,String mdn, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "getGroupPriority(groupId, persisterTxn)";
        knLogger.debug(methodName, "ENTRY: Retrieving for groupId - " + groupId);
        KnXDMTalkGroupInfoDTO corpAddlTGInfoDTO=null;
        try {
            KnXDMCorpInfoDAO corpInfoDAO = KnTablesRegistry.getXDMTablesRegistry().
                    createXDMCorpInfoDAO(xdmPttServerId);
            corpAddlTGInfoDTO=corpInfoDAO.getGroupPriority(groupId,mdn,persistTxn);
            return corpAddlTGInfoDTO;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception has been occurred " + e.getMessage());
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected exception has occurred " + e.getMessage());
            throw KnDbUtil.processException(e, "Failed to fetch corpAddlTGInfoDTO  ...", String.valueOf(groupId), KnDAOSourceTypes.SUBSCRPTTRADIOTGLIST, null);
        } finally {
            knLogger.debug(methodName, "EXIT: ");
        }
    }
    
    
    @Override
    public List<String> getRealMdns(List<String> mdns, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getRealMdns(List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: ", KnGDPRTemplate.mdnList(mdns));
        KnXDMSubscriberInfoDAO subsInfoDAO = KnTablesRegistry.getXDMTablesRegistry().createXDMSubscribreInfoDAO(xdmPttServerId);
        return subsInfoDAO.getRealMdns(mdns, readOnly, persisterTxn);
    }

    public Map<String,Set<String>> getBaseMdnsMap(List<String> mdns, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getRealMdns(List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: ", KnGDPRTemplate.mdnList(mdns));
        KnXDMSubscriberInfoDAO subsInfoDAO = KnTablesRegistry.getXDMTablesRegistry().createXDMSubscribreInfoDAO(xdmPttServerId);
        return subsInfoDAO.getBaseMdnsMap(mdns, readOnly, persisterTxn);
    }

    @Override
    public KnXDMDeviceProvDTO selectDeviceProfile(String deviceId, KnPersisterTxn persisterTxn) throws KnDAOException {
        return selectDeviceProfile(deviceId, false, persisterTxn);
    }

    @Override
    public KnXDMDeviceProvDTO selectDeviceProfile(String deviceId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "selectDeviceProfile(String, boolean, KnPersisterTxn)";
        Connection conn = null;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
	        KnXDMDeviceProvDTO deviceProfile=null;
	        knLogger.info(methodName, "deviceId- ", deviceId);
	        try {
                conn = persisterTxn.getDBConnection(xdmPttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
                pStatement = conn.prepareStatement(QRY_SEL_DEVICEINFO_REQDEVICEID);
	            pStatement.setString(1, deviceId);

	            knLogger.debug(methodName, "QUERY : Executing " + QRY_SEL_DEVICEINFO_REQDEVICEID);
	            rs = pStatement.executeQuery();
	            while (rs.next()) {
	            	deviceProfile= new KnXDMDeviceProvDTO();
	            	deviceProfile.setDeviceId(rs.getString("DEVICEID"));
	            	deviceProfile.setDeviceStatus(rs.getInt("DEVICESTATUS"));
	            	deviceProfile.setDeviceActTimeStamp(rs.getLong("DEVICEACTTS"));
	            	deviceProfile.setDeviceLastUsed(rs.getLong("DEVICELASTUSED"));
	            	deviceProfile.setDevicePassword(rs.getString("DEVICEDIGESTPASSWD"));
	            	deviceProfile.setDeviceCreatedAs(rs.getInt("DEVICE_CREATED_AS"));
	            	deviceProfile.setDeviceClientId(rs.getString("DEVICE_CLIENTID"));
	            	deviceProfile.setDeviceShared(rs.getInt("DEVICE_SHARED"));
	            	deviceProfile.setDeviceIMPI(rs.getString("DEVICE_IMPI"));
	            	deviceProfile.setDeviceType(rs.getInt("DEVICE_TYPE"));
	            	deviceProfile.setDeviceName(rs.getString("DEVICE_NAME"));
	            	deviceProfile.setReqDeviceId(rs.getString("REQ_DEVICEID"));
	            	deviceProfile.setCorpId(rs.getInt("CORPID"));
                    deviceProfile.setDeviceSubscrMdn(rs.getString("DEVICE_SUBSCR_MDN"));

                    KnXDMDeviceAddlInfoDTO knXDMDeviceAddlInfoDTO = new KnXDMDeviceAddlInfoDTO();
                    knXDMDeviceAddlInfoDTO.setDeviceSerialNo(rs.getString("DEVICE_SERIAL_NO"));
                    knXDMDeviceAddlInfoDTO.setDeviceIMEI1(rs.getString("IMEI1"));
                    knXDMDeviceAddlInfoDTO.setDeviceIMEI2(rs.getString("IMEI2"));
                    if (null != rs.getBytes("DEVICEADDLINFO")) {
                        knXDMDeviceAddlInfoDTO.setDeviceInfo(new String(rs.getBytes("DEVICEADDLINFO"), StandardCharsets.UTF_8));
                    }
                    knXDMDeviceAddlInfoDTO.setDeviceVersion(rs.getString("DEVICE_VERSION"));
                    deviceProfile.setDeviceAddlInfo(knXDMDeviceAddlInfoDTO);
	            }

	            knLogger.debug(methodName, "QUERY : Completed. - ", deviceProfile);

	        }   catch (Exception e) {
	            knLogger.error(methodName, "Unexpected Exception - " + e);
	            throw KnDbUtil.processException(e, "Failed to  - " + e.getMessage(), xdmPttServerId,
	                    KnDAOSourceTypes.DEVICE_INFO, QRY_SEL_DEVICEINFO_REQDEVICEID);
	        } finally {
                KnDbUtil.closeResultSet(rs);
                KnDbUtil.closePreparedStatement(pStatement);
            }

	        return deviceProfile;
	}

    @Override
    public Integer selectOtherDeviceProfile(String deviceId, String reqDeviceId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "selectOtherDeviceProfile(String, String, boolean, KnPersisterTxn)";
        Connection conn = null;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        Integer deviceCount = null;
        knLogger.info(methodName, "deviceId- ", deviceId, " reqDeviceId - ", reqDeviceId);
        try {
            conn = persisterTxn.getDBConnection(xdmPttServerId,KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            pStatement = conn.prepareStatement(QRY_SEL_DEVICEINFO_REQDEVICEID_DEVICEID);
            pStatement.setString(1, reqDeviceId);
            pStatement.setString(2, deviceId);

            knLogger.debug(methodName, "QUERY : Executing " + QRY_SEL_DEVICEINFO_REQDEVICEID_DEVICEID);
            rs = pStatement.executeQuery();
            while (rs.next()) {
                deviceCount = rs.getInt(1);
            }

            knLogger.debug(methodName, "QUERY : Completed. - ", deviceCount);

        }   catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - " + e);
            throw KnDbUtil.processException(e, "Failed to  - " + e.getMessage(), xdmPttServerId,
                    KnDAOSourceTypes.DEVICE_INFO, QRY_SEL_DEVICEINFO_REQDEVICEID_DEVICEID);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
        }

        return deviceCount;
    }

    @Override
    public Integer getDeviceProfile(String deviceSubscriberMdn, String intDeviceId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getDeviceProfile(String, String, KnPersisterTxn)";
        Connection conn = null;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        Integer deviceCount = null;
        knLogger.info(methodName, "deviceSubscriberMdn- ", deviceSubscriberMdn, " intDeviceId: ", intDeviceId);
        try {
            conn = persisterTxn.getDBConnection(xdmPttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            if (null == intDeviceId) {
                pStatement = conn.prepareStatement(QRY_SEL_DEVICEINFO_SUBSCRIBER_MDN);
                pStatement.setString(1, deviceSubscriberMdn);
                knLogger.debug(methodName, "QUERY : Executing " + QRY_SEL_DEVICEINFO_SUBSCRIBER_MDN);
            }else {
                pStatement = conn.prepareStatement(QRY_SEL_DEVICEINFO_SUBS_MDN_INT_DEVICEID);
                pStatement.setString(1, deviceSubscriberMdn);
                pStatement.setString(2, intDeviceId);
                knLogger.debug(methodName, "QUERY : Executing " + QRY_SEL_DEVICEINFO_SUBS_MDN_INT_DEVICEID);
            }

            rs = pStatement.executeQuery();
            while (rs.next()) {
                deviceCount = rs.getInt(1);
            }

            knLogger.debug(methodName, "QUERY : Completed. - ", deviceCount);

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - " + e);
            if (null == intDeviceId) {
                throw KnDbUtil.processException(e, "Failed to  - " + e.getMessage(), xdmPttServerId,
                        KnDAOSourceTypes.DEVICE_INFO, QRY_SEL_DEVICEINFO_SUBSCRIBER_MDN);
            } else {
                throw KnDbUtil.processException(e, "Failed to  - " + e.getMessage(), xdmPttServerId,
                        KnDAOSourceTypes.DEVICE_INFO, QRY_SEL_DEVICEINFO_SUBS_MDN_INT_DEVICEID);
            }
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
        }

        return deviceCount;
    }

	@Override
    public String getDeviceId(String deviceSubscriberMdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getDeviceId(String, KnPersisterTxn)";
        Connection conn = null;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String deviceId = null;
        knLogger.info(methodName, "deviceSubscriberMdn- ", deviceSubscriberMdn);
        try {
            conn = persisterTxn.getDBConnection(xdmPttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pStatement = conn.prepareStatement(QRY_SEL_DEVICE_ID_BY_MDN);
            pStatement.setString(1, deviceSubscriberMdn);

            knLogger.debug(methodName, "QUERY : Executing " + QRY_SEL_DEVICE_ID_BY_MDN);
            rs = pStatement.executeQuery();
            while (rs.next()) {
                deviceId = rs.getString(1);
            }
            knLogger.debug(methodName, "QUERY : Completed. - ", deviceId);

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - " + e);
            throw KnDbUtil.processException(e, "Failed to  - " + e.getMessage(), xdmPttServerId,
                    KnDAOSourceTypes.DEVICE_ID, QRY_SEL_DEVICE_ID_BY_MDN);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
        }
        return deviceId;
    }

    @Override
    public KnXDMDeviceProvDTO getDeviceImuiInfo(String deviceId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getDeviceImuiInfo(String, KnPersisterTxn)";
        Connection conn = null;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        knLogger.info(methodName, "deviceId- ", deviceId);
        KnXDMDeviceProvDTO deviceProvDTO = new KnXDMDeviceProvDTO();
        try {
            conn = persisterTxn.getDBConnection(xdmPttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pStatement = conn.prepareStatement(SELECT_DEVICE_IMPI_BY_DEVICEID);
            pStatement.setString(1, deviceId);

            knLogger.debug(methodName, "QUERY : Executing " + SELECT_DEVICE_IMPI_BY_DEVICEID);
            rs = pStatement.executeQuery();
            while (rs.next()) {
                deviceProvDTO.setDeviceId(rs.getString("DEVICEID"));
                deviceProvDTO.setDevicePassword(rs.getString("DEVICEDIGESTPASSWD"));
                deviceProvDTO.setDeviceIMPU(new String(rs.getBytes("DEVICE_IMPU"),StandardCharsets.UTF_8));
                deviceProvDTO.setDeviceIMPI(new String(rs.getBytes("DEVICE_IMPI"),StandardCharsets.UTF_8));

            }
            knLogger.debug(methodName, "QUERY : Completed deviceProvDTO. - ", deviceProvDTO);

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - " + e);
            throw KnDbUtil.processException(e, "Failed to  - " + e.getMessage(), xdmPttServerId,
                    KnDAOSourceTypes.DEVICE_ID, QRY_SEL_DEVICE_ID_BY_MDN);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
        }
        return deviceProvDTO;
    }

    @Override
	public KnXDMSubsProfileRespDTO selectSubsProfileInfo(List<String> mdnList, KnPersisterTxn persistTxn)
			throws KnDAOException {
		 String methodName = "selectSubsProfileInfo(List<String>, KnPersisterTxn)";
	        knLogger.debug(methodName, "ENTRY: ", KnGDPRTemplate.mdnList(mdnList));
	        KnXDMSubscriberInfoDAO subsInfoDAO = KnTablesRegistry.getXDMTablesRegistry().createXDMSubscribreInfoDAO(xdmPttServerId);
	        return subsInfoDAO.selectSubsProfileInfo(mdnList, persistTxn);
	}

    public LinkedHashSet<String> selectSubDetails(List<String> mdnList, KnPersisterTxn persistTxn)
            throws KnDAOException {
        KnXDMSubscriberInfoDAO subsInfoDAO = KnTablesRegistry.getXDMTablesRegistry().createXDMSubscribreInfoDAO(xdmPttServerId);
        return subsInfoDAO.selectSubDetails(mdnList, persistTxn);
    }

    @Override
    public Map<String, String> getProfileMdnBaseMdnMap(List<String> mdns, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getProfileMdnBaseMdnMap(List<String>, boolean, KnPersisterTxn)";
        KnXDMSubscriberInfoDAO subsInfoDAO = KnTablesRegistry.getXDMTablesRegistry()
                .createXDMSubscribreInfoDAO(xdmPttServerId);
        return subsInfoDAO.getProfileMdnBaseMdnMap(mdns, readOnly, persisterTxn);
    }

    @Override
    public Map<String, List<String>> getProfileMdnListByBaseMdnsList(List<String> baseMdnList, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMSubscriberInfoDAO subsInfoDAO = KnTablesRegistry.getXDMTablesRegistry().createXDMSubscribreInfoDAO(xdmPttServerId);
        return subsInfoDAO.getProfileMdnListByBaseMdnsList(baseMdnList, readOnly, persisterTxn);
    }

    @Override
    public List<String> getMcsIdNullMdnList(KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "getMcsIdNullMdnList(KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: ", persistTxn);
        KnXDMSubscriberInfoDAO subsInfoDAO = KnTablesRegistry.getXDMTablesRegistry()
                .createXDMSubscribreInfoDAO(xdmPttServerId);
        return subsInfoDAO.getMcsIdNullMdnList(persistTxn);
    }
    @Override
    public void updateSegmentIndicator(KnPersisterTxn persistTxn,List<String> mdns,Map<String, String> mdnSegmentIndicator) throws KnDAOException {
        String methodName = "updateSegmentIndicator";
        knLogger.info(methodName, "ENTRY: ", persistTxn);
        KnXDMSubscriberInfoDAO subsInfoDAO = KnTablesRegistry.getXDMTablesRegistry()
                .createXDMSubscribreInfoDAO(xdmPttServerId);
        subsInfoDAO.updateSegmentIndicator(persistTxn,mdns,mdnSegmentIndicator);

    }

    @Override
    public void updateMcsIdForMdnList(List<String> mdnList, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "updateMcsIdForMdnList(KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: ", persistTxn);
        KnXDMSubscriberInfoDAO subsInfoDAO = KnTablesRegistry.getXDMTablesRegistry()
                .createXDMSubscribreInfoDAO(xdmPttServerId);
        subsInfoDAO.updateMcsIdForMdnList(mdnList,persistTxn);
    }

    @Override
	public void updateDeviceInfo(KnDeviceInfoPersistDTO deviceInfo, KnPersisterTxn persisterTxn) throws KnDAOException {
		final String methodName = "updateDeviceInfo(KnDeviceInfoPersistDTO , KnPersisterTxn)";
        Connection conn = null;
        PreparedStatement pStatement = null;
        int cnt = 0;
        knLogger.entry(methodName, "deviceInfo-", deviceInfo);
        try {

            conn = persisterTxn.getDBConnection(xdmPttServerId, KnDBConst.DataStores.XDM_SHARED_DATA,true);
            pStatement = conn.prepareStatement(QRY_UPD_DEVICEINFO);
            pStatement.setInt(1, deviceInfo.getDeviceshared());
            pStatement.setString(2, deviceInfo.getReqDeviceId());
            pStatement.setInt(3, deviceInfo.getDeviceType());
            if (deviceInfo.getCorpId() != null && deviceInfo.getCorpId() != 0) {
                pStatement.setInt(4, deviceInfo.getCorpId());
            } else {
                pStatement.setNull(4, Types.INTEGER);
            }
            pStatement.setString(5, deviceInfo.getDeviceId());

            knLogger.debug(methodName, "QUERY : Executing ",QRY_UPD_DEVICEINFO);
            cnt = pStatement.executeUpdate();
            knLogger.debug(methodName, "QUERY : Completed. - ", cnt);

        } catch (SQLException e) {
            knLogger.error(methodName, "SQL Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to - " + e.getMessage(), xdmPttServerId, KnDAOSourceTypes.DEVICE_INFO, QRY_UPD_DEVICEINFO);

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - " + e);
            throw KnDbUtil.processException(e, "Failed to  - " + e.getMessage(), xdmPttServerId,
                    KnDAOSourceTypes.DEVICE_INFO, QRY_UPD_DEVICEINFO);
        } finally {
            KnDbUtil.closePreparedStatement(pStatement);
        }

        knLogger.exit(methodName, "no of rows updated -", cnt);
		
	}

    @Override
	public void updateDeviceType(KnDeviceInfoPersistDTO deviceInfo, KnPersisterTxn persisterTxn) throws KnDAOException {
		final String methodName = "updateDeviceType(KnDeviceInfoPersistDTO , KnPersisterTxn)";
        Connection conn = null;
        PreparedStatement pStatement = null;
        int cnt = 0;
        knLogger.entry(methodName, "deviceInfo-", deviceInfo);
        String query = UPDATE_DEVICE_INFO_QRY +" DEVICE_TYPE = ? WHERE DEVICEID = ?";
        try {
             
            conn = persisterTxn.getDBConnection(xdmPttServerId, KnDBConst.DataStores.XDM_SHARED_DATA,true);
            pStatement = conn.prepareStatement(query);
            pStatement.setInt(1, deviceInfo.getDeviceType());
            pStatement.setString(2, deviceInfo.getDeviceId());

            knLogger.debug(methodName, "QUERY : Executing ",query);
            cnt = pStatement.executeUpdate();
            knLogger.debug(methodName, "QUERY : Completed. - ", cnt);

        } catch (SQLException e) {
            knLogger.error(methodName, "SQL Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to - " + e.getMessage(), xdmPttServerId, KnDAOSourceTypes.DEVICE_INFO, query);

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - " + e);
            throw KnDbUtil.processException(e, "Failed to  - " + e.getMessage(), xdmPttServerId,
                    KnDAOSourceTypes.DEVICE_INFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStatement);
        }

        knLogger.exit(methodName, "no of rows updated -", cnt);
		
	}
    

	@Override
	public KnXDMDeviceProvDTO selectDeviceProfileByDeviceId(String deviceId, KnPersisterTxn persisterTxn)
			throws KnDAOException {
		 final String methodName = "selectDeviceprofile(String, KnPersisterTxn)";
	        Connection conn = null;
	        PreparedStatement pStatement = null;
	        ResultSet rs = null;
	        KnXDMDeviceProvDTO deviceProfile=null;
	        knLogger.info(methodName, "deviceId- ", deviceId);
	        try {
	            conn = persisterTxn.getDBConnection(xdmPttServerId,KnDBConst.DataStores.XDM_SHARED_DATA, true);
	            pStatement = conn.prepareStatement(QRY_SEL_DEVICEINFO_DEVICEID);
	            pStatement.setString(1, deviceId);

	            knLogger.debug(methodName, "QUERY : Executing "+QRY_SEL_DEVICEINFO_DEVICEID);
	            rs = pStatement.executeQuery();
	            while (rs.next()) {
	            	deviceProfile= new KnXDMDeviceProvDTO();
	            	deviceProfile.setDeviceId(rs.getString("DEVICEID"));
	            	deviceProfile.setDeviceStatus(rs.getInt("DEVICESTATUS"));
	            	deviceProfile.setDeviceActTimeStamp(rs.getLong("DEVICEACTTS"));
	            	deviceProfile.setDeviceLastUsed(rs.getLong("DEVICELASTUSED"));
	            	deviceProfile.setDevicePassword(rs.getString("DEVICEDIGESTPASSWD"));
	            	deviceProfile.setDeviceCreatedAs(rs.getInt("DEVICE_CREATED_AS"));
	            	deviceProfile.setDeviceClientId(rs.getString("DEVICE_CLIENTID"));
	            	deviceProfile.setDeviceShared(rs.getInt("DEVICE_SHARED"));
	            	deviceProfile.setDeviceIMPI(rs.getString("DEVICE_IMPI"));
	            	deviceProfile.setDeviceType(rs.getInt("DEVICE_TYPE"));
	            	deviceProfile.setDeviceName(rs.getString("DEVICE_NAME"));
	            	deviceProfile.setReqDeviceId(rs.getString("REQ_DEVICEID"));
	            	deviceProfile.setCorpId(rs.getInt("CORPID"));
                    deviceProfile.setDeviceSubscrMdn(rs.getString("DEVICE_SUBSCR_MDN"));
	            }

	            knLogger.debug(methodName, "QUERY : Completed. - ", deviceProfile);

	        }   catch (Exception e) {
	            knLogger.error(methodName, "Unexpected Exception - " + e);
	            throw KnDbUtil.processException(e, "Failed to  - " + e.getMessage(), xdmPttServerId,
	                    KnDAOSourceTypes.DEVICE_INFO, QRY_SEL_DEVICEINFO_DEVICEID);
	        } finally {
                KnDbUtil.closeResultSet(rs);
                KnDbUtil.closePreparedStatement(pStatement);
            }

	        return deviceProfile;
	}
	
	 @Override
	public Map<String, Collection<KnDocChangeListDTO>> updateSubsTS(Set<String> baseMdnList, String exists,
			KnPersisterTxn persisterTxn) throws KnDAOException {
		KnXDMSubscriberInfoDAO subsInfoDAO = KnTablesRegistry.getXDMTablesRegistry()
				.createXDMSubscribreInfoDAO(xdmPttServerId);
		return subsInfoDAO.updateSubsTS(baseMdnList, exists, persisterTxn);
	}
	 
	 @Override
		public Map<String, Integer> getAndUpdateDirectoryEtag(Set<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
			KnXDMDirectoryDAO xdmirectoryDAO = KnTablesRegistry.getXDMTablesRegistry()
					.createXDMDirectoryDAO(xdmPttServerId);
			return xdmirectoryDAO.getAndUpdateDirectoryEtag(mdnList,persisterTxn);
		}

    @Override
    public Map<String, Integer> getDirectoryEtag(Set<String> mdnList) throws KnDAOException {
        KnXDMDirectoryDAO xdmirectoryDAO = KnTablesRegistry.getXDMTablesRegistry()
                .createXDMDirectoryDAO(xdmPttServerId);
        return xdmirectoryDAO.getDirectoryEtag(mdnList);
    }

	@Override
    public void updateEtagForDirDocOfMdnList(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMDirectoryDAO directoryDAO = KnTablesRegistry.
                getXDMTablesRegistry().createXDMDirectoryDAO(xdmPttServerId);
        directoryDAO.updateEtagForDirDocOfMdnList(mdnList, persisterTxn);
    }

    @Override
    public Set<KnCorpGroupListInfoDTO> retriveGroupProfileInfoByProfileId( String userProfileId,KnPersisterTxn persisterTxn) throws KnDAOException {
	        String methodName = "getMcxGroupbyUserProfileId(String, KnPersisterTxn)";
	        knLogger.debug(methodName, "ENTRY: corpId ", userProfileId," upmIndex ",userProfileId);
	        KnXDMCBUserProfileMgmtDAO upmDAO = new KnXDMCBUserProfileMgmtDAO(xdmPttServerId);
	        Connection conn = null;
	        PreparedStatement pstmt = null;
	        ResultSet rs = null;
	        String query = null;
            Set<KnCorpGroupListInfoDTO> groupList=new HashSet<KnCorpGroupListInfoDTO>();

        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            conn = persisterTxn.getDBConnection(xdmPttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(GET_USER_PROFILE_BY_ID);
            pstmt.setString(1, userProfileId);
            knLogger.debug(methodName, "ENTRY: query ", GET_USER_PROFILE_BY_ID, " userProfileId ", userProfileId);

            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "queryExceuted Sucessfully ", rs);


            while (rs.next()) {
                KnCorpGroupListInfoDTO knCorpGroupListInfoDTO = new KnCorpGroupListInfoDTO();
                knCorpGroupListInfoDTO.setGroupID(rs.getInt(1));
                knCorpGroupListInfoDTO.setGroupZone(rs.getInt(2));
                knCorpGroupListInfoDTO.setGroupChannel(rs.getInt(3));
                knCorpGroupListInfoDTO.setGroupPriority(rs.getInt(4));
                KnCorpGroupContactDTO knCorpGroupContactDTO = new KnCorpGroupContactDTO();
                knCorpGroupContactDTO.setIsSupervisor(rs.getInt(5));
                knCorpGroupContactDTO.setIsBroadcaster(rs.getInt(6));
                knCorpGroupContactDTO.setCallInitiateAllowed(rs.getInt(7));
                knCorpGroupContactDTO.setCallTerminateAllowed(rs.getInt(8));
                knCorpGroupContactDTO.setIncallAllowed(rs.getInt(9));
                knCorpGroupContactDTO.setIsLocSupervisor(rs.getInt(10));
                knCorpGroupContactDTO.setIsOSMAuthorized(rs.getInt(11));
                knCorpGroupListInfoDTO.setGrpMemProps(knCorpGroupContactDTO);
                groupList.add(knCorpGroupListInfoDTO);
            }
        } catch (SQLException e) {
            knLogger.error(e);
            throw KnDbUtil.processException(e, "Failed to select GroupProfile " + e, xdmPttServerId,
                    KnDAOSourceTypes.XDM_CORP_USERPROFILE, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        return groupList;

	    }

    @Override
    public Map<Integer, Integer> retrivePreConfigGroupProfileInfoByProfileId( String userProfileId,KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "retrivePreConfigGroupProfileInfoByProfileId(String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: corpId ", userProfileId, " upmIndex ", userProfileId);
        KnXDMCBUserProfileMgmtDAO upmDAO = new KnXDMCBUserProfileMgmtDAO(xdmPttServerId);
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        int corpGroupId = 0;
        int groupShared = 0;
        Map<Integer, Integer> groupIdAndGroupShared = new HashMap<>();

        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            conn = persisterTxn.getDBConnection(xdmPttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pstmt = conn.prepareStatement(SELECT_PRE_CONFIG_GROUPID_SHARED_VAL_BY_USER_PROFILE_ID);
            pstmt.setString(1, userProfileId);
            knLogger.debug(methodName, "ENTRY: query ", SELECT_PRE_CONFIG_GROUPID_SHARED_VAL_BY_USER_PROFILE_ID, " userProfileId ", userProfileId);

            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "queryExceuted Sucessfully ", rs);
            while (rs.next()) {
                corpGroupId = rs.getInt(1);
                groupShared = rs.getInt(2);
                groupIdAndGroupShared.put(corpGroupId, groupShared);
            }
        } catch (SQLException e) {
            knLogger.error(e);
            throw KnDbUtil.processException(e, "Failed to select GroupProfile " + e, xdmPttServerId,
                    KnDAOSourceTypes.XDM_CORP_USERPROFILE, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        return groupIdAndGroupShared;

    }

    @Override
    public List<String>  getSharedGroupMemberBySharedAndOwnCorpids(Integer ownedCorpId, List<Integer> sharedCorpids,KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSharedGroupMemberBySharedAndOwnCorpids(Integer, List<Integer>,KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY: ownedCorpId ", ownedCorpId," sharedCorpids ", sharedCorpids);

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = GET_SHARED_GROUP_MEMBERS_BY_SHARED_AND_OWN_CORPIDS;
        int index = 1;
        List<String> groupMemberList = new ArrayList<>();
        try {
            conn = persisterTxn.getDBConnection(xdmPttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarksAnyType(sharedCorpids,query,"SHAREDCORPIDS");
            pstmt = conn.prepareStatement(query);
            for(Integer corpId : sharedCorpids){
                pstmt.setInt(index++,corpId);
            }
            pstmt.setInt(index++,ownedCorpId);

            for(Integer corpId : sharedCorpids){
                pstmt.setInt(index++,corpId);
            }
            knLogger.debug(methodName, "ENTRY: query ", query, " ownedCorpId ", ownedCorpId," sharedCorpids ", sharedCorpids
             , " pstmt ", pstmt);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "queryExceuted Sucessfully ", rs);
            while (rs.next()) {
                groupMemberList.add(rs.getString(1));
            }
            knLogger.debug(methodName, "groupMemberList ", groupMemberList);
        } catch (SQLException e) {
            knLogger.error(e);
            throw KnDbUtil.processException(e, "Failed to select GroupProfile " + e, xdmPttServerId,
                    KnDAOSourceTypes.XDM_CORP_USERPROFILE, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        return groupMemberList;

    }
    public KnTalkGrpScanModeDTO getSubsTalkGrpScanMode(String mdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubsTalkGrpScanMode(String,boolean,KnPersisterTxn)";
        knLogger.debug(methodName, "mdn", KnGDPRTemplate.mdn(mdn));
        KnTalkGrpScanModeDTO grpScanMode = null;
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = persisterTxn.getDBConnection(xdmPttServerId, readOnly);
            pstmt = conn.prepareStatement("SELECT TGSC_MODE, ETAG FROM DG.XDMS_TGSC WHERE MDN=?");
            pstmt.setString(1, mdn);
            knLogger.debug(methodName, "Executing Query- SELECT TGSC_MODE, ETAG FROM DG.XDMS_TGSC WHERE MDN=? ");
            rs = pstmt.executeQuery();
            if (rs.next()) {
                grpScanMode = new KnTalkGrpScanModeDTO();
                grpScanMode.setMode(rs.getInt(1));
                grpScanMode.setEtag(rs.getInt(2));
            }
            knLogger.debug(methodName, "Exit: Query executed successfully . result = ",grpScanMode);
            return grpScanMode;
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while createSubsTalkGrpScanMode", xdmPttServerId, "CAMPEDGROUPINFO","SELECT TGSC_MODE, ETAG FROM DG.XDMS_TGSC WHERE MDN=?");
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public void insertSubsTalkGrpScanMode(KnTalkGrpScanModeDTO talkGrpScanModeDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "insertSubsTalkGrpScanMode(KnTalkGrpScanModeDTO , KnPersisterTxn)";
        knLogger.debug(methodName, "talkGrpScanModeDTO=", talkGrpScanModeDTO);
        Connection conn;
        PreparedStatement pstmt=null;
        try {
            conn = persisterTxn.getDBConnection(xdmPttServerId, false);
            pstmt = conn.prepareStatement(QRY_INSERT_TGSC_MODE);
            knLogger.debug(methodName, "Executing Query- INSERT INTO DG.XDMS_TGSC(MDN,TGSC_MODE,ETAG) VALUES (?, ?, ?) ");
            pstmt.setString(1,talkGrpScanModeDTO.getMdn());
            pstmt.setInt(2,talkGrpScanModeDTO.getMode());
            pstmt.setInt(3,talkGrpScanModeDTO.getEtag());
            boolean flag = pstmt.execute();
            knLogger.debug(methodName, "Exit: Query executed successfully . result = ",flag);
           // return grpScanMode;
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while createSubsTalkGrpScanMode", xdmPttServerId, "DG.XDMS_TGSC","INSERT INTO DG.XDMS_TGSC(MDN,TGSC_MODE,ETAG) VALUES (?, ?, ?)");
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public void updateSubsTalkGrpScanMode(List<String> mdnList,int tgscMode, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateSubsTalkGrpScanMode(List ,String, KnPersisterTxn)";
        knLogger.info(methodName, "Updating TalkGrpScanMode ");
        knLogger.debug(methodName, "tgscMode", tgscMode," AND mdnList ",KnGDPRTemplate.mdnList(mdnList));
        Connection conn;
        PreparedStatement pstmt=null;
        String selectQuery;
        ResultSet rs = null;
        HashMap<String,Integer> tgscEtagMap = new HashMap<>();
        try {
            StringBuilder strBuffer = new StringBuilder(500);
            strBuffer.append(QRY_SELECT_TGSC_MODE).append("(");
            strBuffer.append(formCommaSeperatedIdList(mdnList)).append(");");
            selectQuery = strBuffer.toString();
            knLogger.debug( methodName, "executing select query- " , selectQuery );
            conn = persisterTxn.getDBConnection(xdmPttServerId, false);
            pstmt = conn.prepareStatement(selectQuery);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                String mdn = rs.getString(1).trim();
                tgscEtagMap.put(mdn.trim(), rs.getInt(2));
            }
            knLogger.debug( methodName, "select query executed successfully ");
            if (tgscMode == 1) {
                //create a list of mdn which is part of mdnList but not the part of tgscEtagMap keyset
                List<String> insertMdnList = mdnList.stream()
                        .map(String::trim)
                        .filter(mdn -> !tgscEtagMap.containsKey(mdn))
                        .collect(Collectors.toList());
                knLogger.debug(methodName, "mdnList to be inserted into tgsc table ", KnGDPRTemplate.mdnList(insertMdnList));
                //insert the mdn which is not part of tgscEtagMap
                for (String mdn : insertMdnList) {
                    KnTalkGrpScanModeDTO talkGrpScanModeDTO = new KnTalkGrpScanModeDTO();
                    talkGrpScanModeDTO.setMdn(mdn.trim());
                    talkGrpScanModeDTO.setMode(tgscMode);
                    talkGrpScanModeDTO.setEtag(0);
                    insertSubsTalkGrpScanMode(talkGrpScanModeDTO, persisterTxn);
                }
            }
            knLogger.debug( methodName, "Now executing update query- " , QRY_UPDATE_TGSC_MODE);
            pstmt = conn.prepareStatement(QRY_UPDATE_TGSC_MODE);
            for (String mdn : tgscEtagMap.keySet()) {
                mdn = mdn.trim();
                int etag = tgscEtagMap.get(mdn);
                pstmt.setInt(1, tgscMode);
                pstmt.setInt(2, ++etag);
                pstmt.setString(3, mdn);
                pstmt.addBatch();
            }
            int[] flag = pstmt.executeBatch();
            knLogger.debug(methodName, "Exit: Update Query executed flag length = ",flag.length," result executed successfully= ", Arrays.stream(flag).filter(v->v==1).count());
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while updateSubsTalkGrpScanMode", xdmPttServerId, "DG.XDMS_TGSC",QRY_UPDATE_TGSC_MODE);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public Map<String,Map<Integer,String>> selectProfileIdMDNsByMcpttIds(List<String> mcpttIds, KnPersisterTxn persisterTxn) throws KnDAOException{
        String methodName = "selectProfileIdMDNsByMcpttIds(String, KnPersisterTxn)";
        Map<String,Map<Integer,String>> profileIdMdns = new HashMap<>();
        knLogger.debug(methodName, "ENTRY: Retrieve Subscriber Info");
        KnXDMSubscriberInfoDAO subsInfoDAO = KnTablesRegistry.getXDMTablesRegistry().createXDMSubscribreInfoDAO(xdmPttServerId);
        profileIdMdns = subsInfoDAO.selectProfileIdMDNsByMcpttId(mcpttIds, persisterTxn);
        knLogger.debug(methodName, "EXIT ");
        return profileIdMdns;
    }

    @Override
    public String selectUserProfileName(String mdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "selectUserProfileName(String mdn,boolean readOnly, KnPersisterTxn persisterTxn)";
        knLogger.info(methodName, "ENTRY: select user profile name ", "msn - ", KnGDPRTemplate.mdn(mdn));
        KnXDMSubscriberInfoDAO subsInfoDAO = KnTablesRegistry.getXDMTablesRegistry().createXDMSubscribreInfoDAO(xdmPttServerId);
        String userProfileName = subsInfoDAO.selectUserProfileName(mdn, readOnly, persisterTxn);
        knLogger.info(methodName, "EXIT: selct User Profile Name ");
        return userProfileName;
    }

    @Override
    public void updateUserProfileName(String userProfileName,String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateUserProfileName(KnSubsAddlInfoPersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn)";
        knLogger.info(methodName, "ENTRY: update user profile name "+KnGDPRTemplate.name(userProfileName));
        KnXDMSubscriberInfoDAO subsInfoDAO = KnTablesRegistry.getXDMTablesRegistry().createXDMSubscribreInfoDAO(xdmPttServerId);
        subsInfoDAO.updateUserProfileName(userProfileName,mdn, persisterTxn);
        knLogger.info(methodName, "EXIT: update User Profile Name ");
    }
    @Override
    public Map<String, String> getSegIndEnableSubs(KnPersisterTxn persistTxn) throws KnDAOException {
        // TODO Auto-generated method stub
        String methodName = "updateSegmentIndicator";
        knLogger.info(methodName, "ENTRY: ", persistTxn);
        KnXDMSubscriberInfoDAO subsInfoDAO = KnTablesRegistry.getXDMTablesRegistry()
                .createXDMSubscribreInfoDAO(xdmPttServerId);
        return subsInfoDAO.getSegIndEnableSubs(persistTxn);
    }

    @Override
    public void deleteSubscriberCameraInfo(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteSubscriberCameraInfo()";
        Connection conn;
        PreparedStatement pStatement = null;
        String query = null;
        knLogger.debug(methodName, "ENTRY : deleteSubscriberCameraInfo mdn--> " + KnGDPRTemplate.mdn(mdn));
        try {
            //open a txn if its not already opened
            conn = persisterTxn.getDBConnection(xdmPttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pStatement = conn.prepareStatement(QRY_DEL_SUBSCR_CAMERA_INFO);
            pStatement.setString(1, mdn);
            knLogger.debug(methodName, "QUERY : Executing " + QRY_DEL_SUBSCR_CAMERA_INFO);
            int count = pStatement.executeUpdate();
            knLogger.debug(methodName, "QUERY : Completed." + count);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception - " + e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - " + e);
            throw KnDbUtil.processException(e, "Failed to  - " + e.getMessage(), xdmPttServerId,
                    KnDAOSourceTypes.SUBSCR_CAMERA_INFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStatement);
        }
    }

    @Override
    public void deleteSubscriberCameraInfo(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteSubscriberCameraInfo()";
        Connection conn;
        PreparedStatement pStatement = null;
        String query = null;
        int index = 1;
        knLogger.debug(methodName, "ENTRY : deleteSubscriberCameraInfo mdn--> " + KnGDPRTemplate.mdnList(mdnList));
        try {
            //open a txn if its not already opened
            conn = persisterTxn.getDBConnection(xdmPttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            query = "DELETE FROM DG.SUBSCR_CAMERA_INFO WHERE MDN IN (MDNLIST)";
            query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(mdnList,query,"MDNLIST");
            pStatement = conn.prepareStatement(query);
            for(String mdn : mdnList){
                pStatement.setString(index++, mdn);
            }
            knLogger.debug(methodName, "QUERY : Executing " + query);
            int count = pStatement.executeUpdate();
            knLogger.debug(methodName, "QUERY : Completed." + count);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception - " + e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - " + e);
            throw KnDbUtil.processException(e, "Failed to  - " + e.getMessage(), xdmPttServerId,
                    KnDAOSourceTypes.SUBSCR_CAMERA_INFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStatement);
        }
    }

    @Override
    public Map<String,List<Integer>> retriveUpmIdGroupMapByUserProfileId( List<String> userProfileIdList, boolean readOnly,KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "retriveUpmIdGroupMapByUserProfileId()";
        knLogger.debug(methodName, "ENTRY: userProfileIdList ", userProfileIdList);
        KnXDMCBUserProfileMgmtDAO upmDAO = new KnXDMCBUserProfileMgmtDAO(xdmPttServerId);
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        Map<String,List<Integer>> upmGroupMap=new HashMap<>();
        int index = 1;

        try {
            conn = persisterTxn.getDBConnection(xdmPttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            query = GET_UPM_GROUP_BY_UPMID;
            query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(userProfileIdList,query,UPMID_LIST);
            pstmt = conn.prepareStatement(query);
            for(String mdn : userProfileIdList){
                pstmt.setString(index++,mdn);
            }
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                String upmId=rs.getString("USER_PROFILEID");
                int groupId=rs.getInt("GROUPID");
                List<Integer> mcxGroupList = upmGroupMap.get(upmId);
                if(mcxGroupList!=null&&!mcxGroupList.isEmpty()){
                    upmGroupMap.get(upmId).add(groupId);
                }else{
                    List<Integer> newMcxGroupList=new ArrayList<>();
                    newMcxGroupList.add(groupId);
                    upmGroupMap.put(upmId,newMcxGroupList);
                }
            }
        }catch (SQLException e) {
            knLogger.error(e);
            throw KnDbUtil.processException(e, "Failed to select GroupProfile " + e, xdmPttServerId,
                    KnDAOSourceTypes.XDM_CORP_USERPROFILE , query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        knLogger.debug(methodName, " EXIT upmGroupMap - ", upmGroupMap);
        return upmGroupMap;
    }

    /**
     *
     * @param sharedCorpId
     * @param persisterTxn
     * @return Map <GroupId <List of SharedCorp>>
     * @throws KnDAOException
     */
    @Override
    public Map<Integer,List<Integer>> selectOwnerCorpIdAndPreConfigGrps(int sharedCorpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = " selectOwnerCorpIdAndPreConfigGrps(int , KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : sharedCorpId - ", sharedCorpId);
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        Map<Integer,List<Integer>> ownerCorpIdAndPreConfigGrps = new HashMap<>();
        try {
            conn = persisterTxn.getDBConnection(xdmPttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = SELECT_PRECONFIG_GRP_BY_SHARED_CORPID;

            knLogger.debug( methodName, "query -", query);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1,sharedCorpId);

            knLogger.debug(methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                int grpId = rs.getInt("CORPGROUPID");
                int ownedCorpId = rs.getInt("OWNEDCORPID");
                if(ownerCorpIdAndPreConfigGrps.get(ownedCorpId) == null) {
                    List<Integer> preConfigGroupIdList = new ArrayList<>();
                    preConfigGroupIdList.add(grpId);
                    ownerCorpIdAndPreConfigGrps.put(ownedCorpId,preConfigGroupIdList);
                }else {
                    ownerCorpIdAndPreConfigGrps.get(ownedCorpId).add(grpId);
                }
            }
        }catch (SQLException e) {
            knLogger.error(e);
            throw KnDbUtil.processException(e, "Failed to select GroupProfile " + e, xdmPttServerId,
                    KnDAOSourceTypes.CORPGRP_SHAREDLIST , query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        knLogger.debug(methodName, " EXIT ownerCorpIdAndPreConfigGrps - ", ownerCorpIdAndPreConfigGrps);
        return ownerCorpIdAndPreConfigGrps;
    }
    /**
     *
     * @param ownedCorpId
     * @param groupIds
     * @param persisterTxn
     * @return Map <GroupId <List of SharedCorp>>
     * @throws KnDAOException
     */
    @Override
    public Map<Integer,List<Integer>> selectGroupSharedCorpId(int ownedCorpId, Collection<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "selectGroupSharedCorpId(int, Collection<Integer>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : groupIds - ", groupIds,"ownedCorpId - ",ownedCorpId);
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        Map<Integer,List<Integer>> sharedCorpInfoMap = new HashMap<>();
        int index = 1;
        try {
            conn = persisterTxn.getDBConnection(xdmPttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = SELECT_SHARED_CORPINFO_BY_OWNERCORPID_GROUPIDS;
            // query = KnDbUtil.replaceValInQry(query ,ownedCorpId);
            query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarksAnyType(groupIds,query,GROUPIDS);
            knLogger.debug( methodName, "query -", query);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(index++,ownedCorpId);
            for(Integer groupId : groupIds)
            {
                pstmt.setInt(index++,groupId);
            }
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                int grpId = rs.getInt("CORPGROUPID");
                int sharedCorpId = rs.getInt("SHAREDCORPID");
                if(sharedCorpInfoMap.get(grpId) == null) {
                    List<Integer> sharedCorpIIdList = new ArrayList<>();
                    sharedCorpIIdList.add(sharedCorpId);
                    sharedCorpInfoMap.put(grpId,sharedCorpIIdList);
                }else {
                    sharedCorpInfoMap.get(grpId).add(sharedCorpId);
                }
            }
        }catch (SQLException e) {
            knLogger.error(e);
            throw KnDbUtil.processException(e, "Failed to select GroupProfile " + e, xdmPttServerId,
                    KnDAOSourceTypes.CORPGRP_SHAREDLIST , query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        knLogger.debug(methodName, " EXIT sharedCorpInfoMap - ", sharedCorpInfoMap);
        return sharedCorpInfoMap;
    }

    /**
     *
     * @param sharedCorpId
     * @param persisterTxn
     * @return Map <GroupId <List of SharedCorp>>
     * @throws KnDAOException
     */
    @Override
    public List<Integer> getUserProfileGroupIdBySharedCorpId(int sharedCorpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getUserProfileGroupIdBySharedCorpId(int,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :sharedCorpId- ", sharedCorpId);
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        int index = 1;
        List<Integer> groupIdList=new ArrayList<>();
        try {
            conn = persisterTxn.getDBConnection(xdmPttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = SELECT_USERPROFILE_GRP_ID_BY_SHARE_CORP_ID;
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(index,sharedCorpId);

            knLogger.debug(methodName, "Executing query - ", query);
            knLogger.debug(methodName, "ENTRY: query ", SELECT_USERPROFILE_GRP_ID_BY_SHARE_CORP_ID, "sharedCorpId" + sharedCorpId);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "queryExceuted Sucessfully ", rs);
            while (rs.next()) {
                groupIdList.add(rs.getInt(1));
            }
        }catch (SQLException e) {
            knLogger.error(e);
            throw KnDbUtil.processException(e, "Failed to select GroupProfile " + e, xdmPttServerId,
                    KnDAOSourceTypes.CORPGRP_SHAREDLIST , query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        knLogger.debug(methodName, " EXIT groupIdList - ", groupIdList);
        return groupIdList;
    }

    @Override
    public String selectSubscriberMdnByMCSId(List<String> mcsIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "selectSubscriberMdnByMCSId(String,String,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: ");
        KnXDMSubscriberInfoDAO subsInfoDAO = KnTablesRegistry.getXDMTablesRegistry().createXDMSubscribreInfoDAO(xdmPttServerId);
        return subsInfoDAO.selectSubscriberMdnByMCSId(mcsIds, persisterTxn);
    }

    public KnCorpConfigInfoDto selectCorpConfigureInfo(int corpId, String paramName, KnPersisterTxn persisterTxn) throws KnDAOException {
        return selectCorpConfigureInfo(corpId, paramName, false, persisterTxn);
    }

    /**
     * Method to retrieve corp configuration info. This is read only method.
     */
    @Override
    public KnCorpConfigInfoDto selectCorpConfigureInfo(int corpId, String paramName, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "selectCorpConfigureInfo(corpId, paramName,readOnly, persisterTxn)";
        KnCorpConfigInfoDto corpConfigInfo = null;
        try {
            KnCorpConfigInfoDAO corpInfoDAO = KnTablesRegistry.getXDMTablesRegistry().
                    createXDMCorpConfigInfoDAO(xdmPttServerId);
            corpConfigInfo = corpInfoDAO.selectCorpConfigInfo(corpId, paramName, readOnly, persisterTxn);
            return corpConfigInfo;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception has been occurred " + e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected exception has occurred " + e);
            throw KnDbUtil.processException(e, "Failed to fetch corp config info Info ...", "", KnDAOSourceTypes.POC_CORP_CONFIG_INFO, null);
        }
    }

    @Override
    public void insertCorpConfigureInfo(KnCorpConfigInfoDto corpConfigInfoDto,  KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "insertCorpConfigureInfo(corpId, paramName, persisterTxn)";
        KnCorpConfigInfoDto corpConfigInfo = null;
        try {
            KnCorpConfigInfoDAO corpInfoDAO = KnTablesRegistry.getXDMTablesRegistry().
                    createXDMCorpConfigInfoDAO(xdmPttServerId);
            corpInfoDAO.insertCorpConfigInfo(corpConfigInfoDto, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception has been occurred " + e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected exception has occurred " + e);
            throw KnDbUtil.processException(e, "Failed to fetch corp config info Info ...", "", KnDAOSourceTypes.POC_CORP_CONFIG_INFO, null);
        }
    }

    @Override
    public void updateCorpConfigureInfo(KnCorpConfigInfoDto corpConfigInfoDto,  KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateCorpConfigureInfo(corpId, paramName, persisterTxn)";
        try {
            KnCorpConfigInfoDAO corpInfoDAO = KnTablesRegistry.getXDMTablesRegistry().
                    createXDMCorpConfigInfoDAO(xdmPttServerId);
            corpInfoDAO.updateCorpConfigInfo(corpConfigInfoDto, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception has been occurred " + e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected exception has occurred " + e);
            throw KnDbUtil.processException(e, "Failed to fetch corp config info Info ...", "", KnDAOSourceTypes.POC_CORP_CONFIG_INFO, null);
        }
    }


    public void deleteCorpConfigureInfo(KnCorpConfigInfoDto corpConfigInfoDto,  KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteCorpConfigureInfo(corpId, paramName, persisterTxn)";
        knLogger.debug(methodName, "ENTRY: Update Corporate profile for CorpId - " + corpConfigInfoDto.getCorpId());
        try {
            KnCorpConfigInfoDAO corpInfoDAO = KnTablesRegistry.getXDMTablesRegistry().
                    createXDMCorpConfigInfoDAO(xdmPttServerId);
            corpInfoDAO.deleteCorpConfigInfo(corpConfigInfoDto, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception has been occurred " + e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected exception has occurred " + e);
            throw KnDbUtil.processException(e, "Failed to fetch corp config info Info ...", "", KnDAOSourceTypes.POC_CORP_CONFIG_INFO, null);
        } finally {
            knLogger.debug(methodName, "EXIT: ");
        }
    }

    @Override
    public List<Integer> getSubsAbdgGroupList(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getCorpGroupInfoList(groupId, persisterTxn)";
        List<Integer> abdgGroupList = null;
        try {
            KnXDMCorpInfoDAO corpInfoDAO = KnTablesRegistry.getXDMTablesRegistry().
                    createXDMCorpInfoDAO(xdmPttServerId);
            abdgGroupList = corpInfoDAO.getSubsAbdgGroupList(mdn, persisterTxn);
            return abdgGroupList;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception has been occurred " + e.getMessage());
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected exception has occurred " + e.getMessage());
            throw KnDbUtil.processException(e, "Failed to fetch osmListId  ...", "", KnDAOSourceTypes.CORPGROUPINFO, null);
        } finally {
            knLogger.debug(methodName, "EXIT: ");
        }
    }

    @Override
    public Collection<KnCorpGpInfoDTO> getSubscriberGroupList(String mdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubscriberGroupList(mdn, readOnly, persisterTxn)";
        Collection<KnCorpGpInfoDTO> groupList = null;
        try {
            knLogger.info(methodName, "Entry : ", KnGDPRTemplate.mdn(mdn));
            KnXDMCorpInfoDAO corpInfoDAO = KnTablesRegistry.getXDMTablesRegistry().
                    createXDMCorpInfoDAO(xdmPttServerId);
            groupList = corpInfoDAO.getSubscriberGroupList(mdn, readOnly, persisterTxn);
            return groupList;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception has been occurred " + e.getMessage());
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected exception has occurred " + e.getMessage());
            throw KnDbUtil.processException(e, "Failed to fetch osmListId  ...", "", KnDAOSourceTypes.CORPGROUPINFO, null);
        } finally {
            knLogger.debug(methodName, "EXIT: ");
        }
    }

    public Boolean validateExtCorpGroup(String userProfileId, int subscCorpID, int groupOwnerCorpId, int groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "validateExtCorpGroup(int,int)";
        knLogger.info(methodName, " subscCorpID:", subscCorpID, " groupId:", groupId, " groupOwnerCorpId:",
                groupOwnerCorpId, " userProfileId:", userProfileId);
        boolean isExtCorpGroup;
        KnXDMCorpInfoDAO corpInfoDAO = KnTablesRegistry.getXDMTablesRegistry().
                createXDMCorpInfoDAO(xdmPttServerId);
        if (groupOwnerCorpId == subscCorpID) {
            return false;
        }
        List<Integer> sharedCorpIds = corpInfoDAO.getSharedCorpIds(groupId, persisterTxn);
        knLogger.debug(methodName, "Shared Corp IDs: ", sharedCorpIds, " for groupId - ", groupId);
        isExtCorpGroup = (sharedCorpIds != null && !sharedCorpIds.contains(subscCorpID));
        List<Integer> sharedCorpIdsUserProfile = null;
        if (userProfileId != null) {
            sharedCorpIdsUserProfile = corpInfoDAO.getUpmSharedCorpIds(userProfileId, persisterTxn);
            knLogger.debug(methodName, "Shared Corp IDs from UserProfile: ", sharedCorpIdsUserProfile,
                    " for userProfileId - ", userProfileId);
            isExtCorpGroup = (sharedCorpIds != null && !sharedCorpIds.contains(subscCorpID)) &&
                    (sharedCorpIdsUserProfile != null && !sharedCorpIdsUserProfile.contains(subscCorpID));
        }
        knLogger.info(methodName, "isExtCorpGroup: ", isExtCorpGroup);
        return isExtCorpGroup;
    }

    @Override
    public void updateSipRecordingFlag(Integer sipRecordingFlag, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpInfoDAO corpInfoDAO = KnTablesRegistry.getXDMTablesRegistry().
                createXDMCorpInfoDAO(xdmPttServerId);
        corpInfoDAO.updateSipRecordingFlag(sipRecordingFlag,corpId,persisterTxn);
    }

    @Override
    public String getDeviceInfo(String deviceId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMSubscriberInfoDAO subsInfoDAO = KnTablesRegistry.getXDMTablesRegistry().createXDMSubscribreInfoDAO(xdmPttServerId);
        String methodName = "getDeviceInfo(deviceId, persisterTxn)";
        KnXDMDeviceProvDTO knXDMDeviceProvDTO = new KnXDMDeviceProvDTO();
        try {
            return subsInfoDAO.getDeviceInfo(deviceId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception has been occurred " + e.getMessage());
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected exception has occurred " + e.getMessage());
            throw KnDbUtil.processException(e, "Failed to fetch deviceCreatedAs ...", "", KnDAOSourceTypes.DEVICE_INFO, null);
        }
    }

    @Override
    public List<String> getDeviceInfo(List<String> deviceId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMSubscriberInfoDAO subsInfoDAO = KnTablesRegistry.getXDMTablesRegistry().createXDMSubscribreInfoDAO(xdmPttServerId);
        String methodName = "getDeviceInfo(deviceId, persisterTxn)";
        KnXDMDeviceProvDTO knXDMDeviceProvDTO = new KnXDMDeviceProvDTO();
        try {
            return subsInfoDAO.getDeviceInfo(deviceId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception has been occurred " + e.getMessage());
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected exception has occurred " + e.getMessage());
            throw KnDbUtil.processException(e, "Failed to fetch deviceCreatedAs ...", "", KnDAOSourceTypes.DEVICE_INFO, null);
        }
    }

    @Override
    public Map<String, String> getDeviceCreatedAsMap(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getDeviceCreatedAsMap(mdnList, persisterTxn)";
        KnXDMSubscriberInfoDAO subsInfoDAO = KnTablesRegistry.getXDMTablesRegistry().createXDMSubscribreInfoDAO(xdmPttServerId);
        try {
            return subsInfoDAO.getDeviceCreatedAsMap(mdnList, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception has been occurred " + e.getMessage());
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected exception has occurred " + e.getMessage());
            throw KnDbUtil.processException(e, "Failed to fetch deviceCreatedAs map ...", "", KnDAOSourceTypes.DEVICE_INFO, null);
        }
    }

    @Override
    public void createRecordingInfoForTarget(Collection<KnRecordingTargetInfoDTO> recordingTargetInfoDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnRecordingInfoDAO recordingDao = KnTablesRegistry.getXDMTablesRegistry().createRecordingInfoDAO(xdmPttServerId);
        recordingDao.createRecordingInfoForTarget(recordingTargetInfoDTO, persisterTxn);
    }

    @Override
    public void updateRecordingInfoTargetByTarget(Map<String, Integer> targetRecTypeMap, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnRecordingInfoDAO recordingDao = KnTablesRegistry.getXDMTablesRegistry().createRecordingInfoDAO(xdmPttServerId);
        recordingDao.updateRecordingInfoTargetByTarget(targetRecTypeMap, persisterTxn);
    }

    @Override
    public void deleteRecordingInfoTargetByTarget(Collection<String> target, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnRecordingInfoDAO recordingDao = KnTablesRegistry.getXDMTablesRegistry().createRecordingInfoDAO(xdmPttServerId);
        recordingDao.deleteRecordingInfoTargetByTarget(target, persisterTxn);
    }

    @Override
    public Collection<KnRecordingTargetInfoDTO> getRecordingInfoTargetByTarget(Collection<String> targetList, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnRecordingInfoDAO recordingDao = KnTablesRegistry.getXDMTablesRegistry().createRecordingInfoDAO(xdmPttServerId);
        return recordingDao.getRecordingInfoTargetByTarget(targetList, persisterTxn);
    }

    @Override
    public void deleteRecordingInfoByCorpId(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteRecordingInfoByCorpId(int, persisterTxn)";
        knLogger.debug(methodName," Entry :",corpId);
        try {
            KnRecordingInfoDAO corpInfoDAO = KnTablesRegistry.getXDMTablesRegistry().
                    creteRecordingInfoDAO(xdmPttServerId);
            corpInfoDAO.deleteRecordingInfoByCorpId(corpId, persisterTxn);
        }catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception has been occurred " + e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected exception has occurred " + e);
            throw KnDbUtil.processException(e, "Failed to delete Recording Target info ...", "", KnDAOSourceTypes.POC_CORP_CONFIG_INFO, null);
        } finally {
            knLogger.debug(methodName, "EXIT: ");
        }
    }

    public int getAllSubscribersCount(int corpId) throws KnDAOException {
        int allSubsCount = 0;
        String methodName = "getAllSubscribersCount()";
        try {
            KnXDMSubscriberInfoDAO subsInfoDAO = KnTablesRegistry.getXDMTablesRegistry().createXDMSubscribreInfoDAO(xdmPttServerId);
            allSubsCount = subsInfoDAO.getAllSubscribersCount(corpId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return allSubsCount;
    }

    public Map<String, String> getProfileMDNswithMCID(int corpId, int start, int end) {
        String methodName = "profileMdnswithMCID(KnPersisterTxn, int, int)";
        Map<String, String> profileMdnListwithMCID = new HashMap<>();
        try {
            KnXDMSubscriberInfoDAO subsInfoDAO = KnTablesRegistry.getXDMTablesRegistry().createXDMSubscribreInfoDAO(xdmPttServerId);
            profileMdnListwithMCID = subsInfoDAO.getProfileMDNswithMCID(corpId, start, end);
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected exception has occurred " + e.getMessage());
            throw new RuntimeException(e);
        }
        return profileMdnListwithMCID;
    }

    public List<String> getProfileMDNswithNoBaseMDN(Map<String, String> profileMDNsWithMCID) {
        String methodName = "getProfileMDNswithNoBaseMDN(KnPersisterTxn, Map<String, String>)";
        List<String> profileMdnListwithNoBaseMDN = new ArrayList<>();
        try {
            KnXDMSubscriberInfoDAO subsInfoDAO = KnTablesRegistry.getXDMTablesRegistry().createXDMSubscribreInfoDAO(xdmPttServerId);
            profileMdnListwithNoBaseMDN = subsInfoDAO.getProfileMDNswithNoBaseMDN(profileMDNsWithMCID);
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected exception has occurred " + e.getMessage());
            throw new RuntimeException(e);
        }
        return profileMdnListwithNoBaseMDN;
    }

    public List<String> getAllProfileMDNs(KnPersisterTxn persisterTxn) {
        String methodName = "getAllProfileMDNs";
        List<String> allProfileMDNs = new ArrayList<>();
        try {
            KnXDMSubscriberInfoDAO subsInfoDAO = KnTablesRegistry.getXDMTablesRegistry().createXDMSubscribreInfoDAO(xdmPttServerId);
            allProfileMDNs = subsInfoDAO.allProfileMDNs(persisterTxn);
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected exception has occurred " + e.getMessage());
            throw new RuntimeException(e);
        }
        return allProfileMDNs;
    }

    public Map<String, List<String>> getInconsistenGroupId(String profileMdn, List<String> groupIds) {
        String methodName = "getInconsistenGroupId(String, List<String>, KnPersisterTxn)";
        Map<String, List<String>> mapOfProfileMdnWithGroupIds = new HashMap<>();
        try {
            KnXDMSubscriberInfoDAO subsInfoDAO = KnTablesRegistry.getXDMTablesRegistry().createXDMSubscribreInfoDAO(xdmPttServerId);
            mapOfProfileMdnWithGroupIds = subsInfoDAO.getInconsistenGroupId(profileMdn, groupIds);
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected exception has occurred " + e.getMessage());
            throw new RuntimeException(e);
        }
        return mapOfProfileMdnWithGroupIds;
    }

    public Map<String, List<String>> getSublistIdContactMdnMap(String sublistId) {
        String methodName = "getSublistIdContactMdnMap(String, KnPersisterTxn)";
        Map<String, List<String>> mapOfSublistIdWithContactMdn = new HashMap<>();
        try {
            KnXDMSubscriberInfoDAO subsInfoDAO = KnTablesRegistry.getXDMTablesRegistry().createXDMSubscribreInfoDAO(xdmPttServerId);
            mapOfSublistIdWithContactMdn = subsInfoDAO.getMapOfSublistIdWithContactMdn(sublistId);
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected exception has occurred " + e.getMessage());
            throw new RuntimeException(e);
        }
        return mapOfSublistIdWithContactMdn;
    }

    public List<String> ifSublistExistsinTT(List<String> sublistIds, KnPersisterTxn persisterTxn){
        String methodName = "ifSublistExistsinTT(List<String>, KnPersisterTxn)";
        List<String> ifSublistExistsinTTList = new ArrayList<>();
        try {
            KnXDMSubscriberInfoDAO subsInfoDAO = KnTablesRegistry.getXDMTablesRegistry().createXDMSubscribreInfoDAO(xdmPttServerId);
            ifSublistExistsinTTList = subsInfoDAO.ifSublistExistsinTT(sublistIds, persisterTxn);
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected exception has occurred " + e.getMessage());
            throw new RuntimeException(e);
        }
        return ifSublistExistsinTTList;

    }

    public Map<String, List<String>> getProfileMdnsForUPMId(String upmId) {
        String methodName = "getProfileMdnsForUPMId(List<String>, KnPersisterTxn)";
        Map<String, List<String>> mapOfProfileMdnsForUPMId = new HashMap<>();
        try {
            KnXDMSubscriberInfoDAO subsInfoDAO = KnTablesRegistry.getXDMTablesRegistry().createXDMSubscribreInfoDAO(xdmPttServerId);
            mapOfProfileMdnsForUPMId = subsInfoDAO.getProfileMdnsForUPMId(upmId);
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected exception has occurred " + e.getMessage());
            throw new RuntimeException(e);
        }
        return mapOfProfileMdnsForUPMId;
    }

    public Map<String, List<String>> getContactMdnByProfileMdn(String profileMdn) {
        String methodName = "getContactMdnByProfileMdn(String, KnPersisterTxn)";
        Map<String, List<String>> mapOfContactMdnByProfileMdn = new HashMap<>();
        try {
            KnXDMSubscriberInfoDAO subsInfoDAO = KnTablesRegistry.getXDMTablesRegistry().createXDMSubscribreInfoDAO(xdmPttServerId);
            mapOfContactMdnByProfileMdn = subsInfoDAO.getContactMdnByProfileMdn(profileMdn);
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected exception has occurred " + e.getMessage());
            throw new RuntimeException(e);
        }
        return mapOfContactMdnByProfileMdn;
    }

    public String getFeatureBSForProfileMdnFromTT(String profileMdn) {
        String methodName = "getFeatureBSForProfileMdnFromTT(String, KnPersisterTxn)";
        String featureBSForProfileMdn = null;
        try {
            KnXDMSubscriberInfoDAO subsInfoDAO = KnTablesRegistry.getXDMTablesRegistry().createXDMSubscribreInfoDAO(xdmPttServerId);
            featureBSForProfileMdn = subsInfoDAO.getFeatureBSForProfileMdnFromTT(profileMdn);
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected exception has occurred " + e.getMessage());
            throw new RuntimeException(e);
        }
        return featureBSForProfileMdn;
    }

    public Map<String, KnCorpUserProfileMCPTTConfig> getProfileMdnPerm(String profileMdn) {
        String methodName = "getProfileMdnPerm(String, KnPersisterTxn)";
        Map<String, KnCorpUserProfileMCPTTConfig> profileMdnPermMap = new HashMap<>();
        try {
            KnXDMSubscriberInfoDAO subsInfoDAO = KnTablesRegistry.getXDMTablesRegistry().createXDMSubscribreInfoDAO(xdmPttServerId);
            profileMdnPermMap = subsInfoDAO.getProfileMdnPerm(profileMdn);
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected exception has occurred " + e.getMessage());
            throw new RuntimeException(e);
        }
        return profileMdnPermMap;
    }

    public List<String> getAllCorpIds(){
        String methodName = "getAllCorpIds()";
        List<String> distinctCorpIds = new ArrayList<>();
        try {
            KnXDMCorpInfoDAO corpInfoDAO = KnTablesRegistry.getXDMTablesRegistry().createXDMCorpInfoDAO(xdmPttServerId);
            distinctCorpIds = corpInfoDAO.getAllCorpIds();
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected exception has occurred " + e.getMessage());
            throw new RuntimeException(e);
        }
        return distinctCorpIds;
    }

    public List<String> getGroupIdsForProfileMdn(String profileMdnItr) {
        String methodName = "getGroupIdsForProfileMdn(String, KnPersisterTxn)";
        List<String> groupIdsForProfileMdn = new ArrayList<>();
        try {
            KnXDMSubscriberInfoDAO subsInfoDAO = KnTablesRegistry.getXDMTablesRegistry().createXDMSubscribreInfoDAO(xdmPttServerId);
            groupIdsForProfileMdn = subsInfoDAO.getGroupIdsForProfileMdn(profileMdnItr);
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected exception has occurred " + e.getMessage());
            throw new RuntimeException(e);
        }
        return groupIdsForProfileMdn;
    }

    public KnEmergencyInfoDTO getEmergencyInfoForProfileMdn(String profileMdnItr) {
        String methodName = "getEmergencyInfoForProfileMdn(String, KnPersisterTxn)";
        KnEmergencyInfoDTO emergencyInfoForProfileMdn = new KnEmergencyInfoDTO();
        try {
            KnXDMSubscriberInfoDAO subsInfoDAO = KnTablesRegistry.getXDMTablesRegistry().createXDMSubscribreInfoDAO(xdmPttServerId);
            emergencyInfoForProfileMdn = subsInfoDAO.getEmergencyInfoForProfileMdn(profileMdnItr);
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected exception has occurred " + e.getMessage());
            throw new RuntimeException(e);
        }
        return emergencyInfoForProfileMdn;
    }

    public Map<String, Map<Integer,Integer>> getSublistDetailByProfileMdns(List<String> profileMdn, KnPersisterTxn persisterTxn){
        String methodName = "getContactMdnByProfileMdn(String, KnPersisterTxn)";
        Map<String, Map<Integer,Integer>> mapOfContactMdnByProfileMdn = new HashMap<>();
        try {
            KnXDMSubscriberInfoDAO subsInfoDAO = KnTablesRegistry.getXDMTablesRegistry().createXDMSubscribreInfoDAO(xdmPttServerId);
            mapOfContactMdnByProfileMdn = subsInfoDAO.getSublistDetailByProfileMdns(profileMdn, persisterTxn);
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected exception has occurred " + e.getMessage());
            throw new RuntimeException(e);
        }
        return mapOfContactMdnByProfileMdn;
    }

    public Map<Integer, List<String>> getSublistMemberBySublistId(List<Integer> subLists, KnPersisterTxn persisterTxn){
        String methodName = "getContactMdnByProfileMdn(String, KnPersisterTxn)";
        Map<Integer, List<String>> mapOfContactMdnByProfileMdn = new HashMap<>();
        try {
            KnXDMSubscriberInfoDAO subsInfoDAO = KnTablesRegistry.getXDMTablesRegistry().createXDMSubscribreInfoDAO(xdmPttServerId);
            mapOfContactMdnByProfileMdn = subsInfoDAO.getSublistMemberBySublistId(subLists, persisterTxn);
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected exception has occurred " + e.getMessage());
            throw new RuntimeException(e);
        }
        return mapOfContactMdnByProfileMdn;
    }

    public List<String> getTgssGroupExtM(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpInfoDAO corpInfoDAO = KnTablesRegistry.getXDMTablesRegistry().createXDMCorpInfoDAO(xdmPttServerId);
        return corpInfoDAO.getTgssGroupExtM(mdn,persisterTxn);
    }

    public int getSubsClientType(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpInfoDAO corpInfoDAO = KnTablesRegistry.getXDMTablesRegistry().createXDMCorpInfoDAO(xdmPttServerId);
        return corpInfoDAO.getSubsClientType(mdn,persisterTxn);
    }

    @Override
    public String getSubscriberName(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubscriberName(mdn,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: Retrieve subscriber name for mdn ", mdn);
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String query = null;
        String subscriberName = null;
        try {
            conn = persisterTxn.getDBConnection(xdmPttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = GET_SUBSCRIBER_NAME_QUERY;
            stmt = conn.prepareStatement(query);
            stmt.setString(1, mdn);
            knLogger.debug(methodName, "Executing query - ", query);
            rs = stmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                subscriberName = rs.getString(1);
            }
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Retrieve subscriber name for mdn - " + mdn + e.getMessage(), xdmPttServerId, KnDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            knLogger.debug(methodName, "EXIT ");
        }
        return subscriberName;
    }

    @Override
    public boolean isAnyProfileMdnCrossingIndexLimit(String mcId, int upmIndexLimit, KnPersisterTxn persisterTxn) {
        String methodName = "isAnyProfileMdnCrossingIndexLimit()";
        knLogger.debug(methodName, "ENTRY:: ", mcId, upmIndexLimit);
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String query = null;
        int upmIndex = 0;
        boolean isLimitCrossed = false;
        try {
            conn = persisterTxn.getDBConnection(xdmPttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            query = "select MDN,USERPROFILEINDEX from DG.POCSUBSCRINFO where MC_ID = ? AND USERPROFILEINDEX !=0;";
            stmt = conn.prepareStatement(query);
            byte[] mcIdBytes = mcId.getBytes(StandardCharsets.UTF_8);
            stmt.setBytes(1, mcIdBytes);
            knLogger.debug(methodName, "Executing query - ", query);
            rs = stmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                upmIndex = rs.getInt(2);
                if (upmIndex > upmIndexLimit)
                    isLimitCrossed = true;
            }
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw new RuntimeException(e);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            knLogger.debug(methodName, "EXIT:: ", upmIndex);
        }
        return isLimitCrossed;
    }

    public void recoverImpactedMdns(String mcId, KnPersisterTxn persisterTxn) {
        List<String> profileMDNs = getProfileMdnsBasedOnCreationTime(mcId, persisterTxn);
        DBUpdateForImpactedMdns(profileMDNs, persisterTxn);
    }

    private List<String> getProfileMdnsBasedOnCreationTime(String mcId, KnPersisterTxn persisterTxn) {
        String methodName = "getProfileMdnsBasedOnCreationTime(String)";
        knLogger.debug(methodName, "ENTRY:: ", mcId);
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String query = "select MDN from DG.POCSUBSCRINFO where MC_ID = ? AND USERPROFILEINDEX !=0 order by SUBSCRCREATIONTIME;";
        List<String> profileMdns = new ArrayList<>();
        try {
            conn = persisterTxn.getDBConnection(xdmPttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            stmt = conn.prepareStatement(query);
            byte[] mcIdBytes = mcId.getBytes(StandardCharsets.UTF_8);
            stmt.setBytes(1, mcIdBytes);
            knLogger.debug(methodName, "Executing query - ", query);
            rs = stmt.executeQuery();
            while (rs.next()) {
                String profileMdn = rs.getString("MDN");
                profileMdns.add(profileMdn);
            }
        } catch (SQLException e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw new RuntimeException(e);
        } catch (KnConnectionException e) {
            knLogger.error(methodName, "Unexpected Exception at connection - ", e);
            throw new RuntimeException(e);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
        }
        knLogger.debug(methodName, "Profile MDNs::", profileMdns);
        return profileMdns;
    }

    private void DBUpdateForImpactedMdns(List<String> impactedMdn, KnPersisterTxn persisterTxn) {
        String methodName = "getProfileMdnsBasedOnCreationTime(String)";
        Connection conn = null;
        PreparedStatement updateStmt = null;
        ResultSet rs = null;
        try {
            conn = persisterTxn.getDBConnection(xdmPttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            //recovering the impacted mdns
            if (!impactedMdn.isEmpty()) {
                int index = 0;
                String updateQuery = "UPDATE DG.POCSUBSCRINFO SET USERPROFILEINDEX = ? WHERE MDN = ?;";
                updateStmt = conn.prepareStatement(updateQuery);
                for (String profileMdn : impactedMdn) {
                    knLogger.debug(methodName, "Recovering impacted mdn::", profileMdn);
                    updateStmt.setInt(1, ++index);
                    updateStmt.setString(2, profileMdn);
                    updateStmt.addBatch();
                }
                updateStmt.executeBatch();
            }
        } catch (SQLException e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw new RuntimeException(e);
        } catch (KnConnectionException e) {
            knLogger.error(methodName, "Unexpected Exception at connection- ", e);
            throw new RuntimeException(e);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(updateStmt);
        }
    }

    @Override
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
            conn = persisterTxn.getDBConnection(xdmPttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
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
            throw KnDbUtil.processException(e, "Failed while retrieving device info", xdmPttServerId,
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
     * Retrieves group information for a list of corporate group IDs.
     *
     * @param corpGroupIds A list of corporate group IDs to query.
     * @return A map where the key is the group type and the value is a list of maps containing group IDs and their display names.
     * @throws KnDAOException If an error occurs while accessing the database.
     */
    @Override
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
            conn = persisterTxn.getDBConnection(xdmPttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
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
                    xdmPttServerId, KnDAOSourceTypes.CORPGROUPINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.debug(methodName, "EXIT. Group info size: ", groupInfoMap.size());
        }
        return groupInfoMap;
    }


    /**
     * Retrieves configuration values for zones and channels for a given external corporate ID.
     * Falls back to default configuration values if no valid configuration is found.
     *
     * @param extCorpId The external corporate ID.
     * @return A map containing configuration values for "MAXZONES" and "MAXCHANNELSPERZONE".
     * @throws KnDAOException If an error occurs while accessing the database.
     */
    @Override
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
            conn = persisterTxn.getDBConnection(xdmPttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
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
                connAdd = persisterTxn.getDBConnection(xdmPttServerId, true);
                pStmtAddl = connAdd.prepareStatement(fallbackQuery);
                rsAdd = pStmtAddl.executeQuery();
                if (rsAdd.next()) {
                    configMap.put("MAXZONES", rsAdd.getInt("MAXZONES"));
                    configMap.put("MAXCHANNELSPERZONE", rsAdd.getInt("MAXCHANNELSPERZONE"));
                }
            }
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred - ", e);
            throw KnDbUtil.processException(e, "Failed while retrieving config", xdmPttServerId,
                    KnDAOSourceTypes.XDMS_SVC_CONFIG, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            if (rsAdd != null) {
                KnDbUtil.closeResultSet(rsAdd);
            }
            if (pStmtAddl != null) {
                KnDbUtil.closePreparedStatement(pStmtAddl);
            }
            knLogger.debug(methodName, "EXIT - configMap - ", configMap);
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
    @Override
    public List<Integer> getBroadcasterGroupIds(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getBroadcasterGroupIds";
        List<Integer> groupIds = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = persisterTxn.getDBConnection(xdmPttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
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
            throw KnDbUtil.processException(e, "Failed while retrieving broadcaster group IDs", xdmPttServerId,
                    KnDAOSourceTypes.CORPGROUPMEMBERLIST, " ");
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.debug(methodName, "EXIT - broadcasterGroupIds: ", groupIds.size());
        }
        return groupIds;
    }

    @Override
    public List<KnAddlTGInfoDTO> getSubsAddlTGList(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getSubsAddlTGList(String, KnPersisterTxn)";
        List<KnAddlTGInfoDTO> addlTGInfoList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = "SELECT MDN, GROUPID, ZONEID, CHANNELID, ZONENAME FROM DG.SUBSCRPTTRADIOTGLIST WHERE MDN = ?";
        try {
            conn = persisterTxn.getDBConnection(xdmPttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, mdn);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                KnAddlTGInfoDTO dto = new KnAddlTGInfoDTO();
                dto.setMdn(rs.getString("MDN"));
                dto.setGroupId(rs.getInt("GROUPID"));
                dto.setZoneId(rs.getInt("ZONEID"));
                dto.setChannelId(rs.getInt("CHANNELID"));
                dto.setZoneName(rs.getString("ZONENAME"));
                addlTGInfoList.add(dto);
            }
            knLogger.debug(methodName, "Fetched addlTGInfoList for mdn: ", mdn, " -> ", addlTGInfoList.size());
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occurred while retrieving the data -- > ", e);
            throw KnDbUtil.processException(e, "Failed while retrieving the data -- >" + e,
                    xdmPttServerId, KnDAOSourceTypes.CORPGROUPMEMBERLIST, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.debug(methodName, "EXIT - addlTGInfoList: ", addlTGInfoList.size());
        }
        return addlTGInfoList;
    }


    /**
     * Inserts a collection of subscriber additional talk group information into the database.
     *
     * @param corpAddlTGInfoDTOS The collection of `KnCorpAddlTGInfoDTO` objects containing the data to be inserted.
     * @param persisterTxn       The transaction object used for database operations.
     * @throws KnDAOException If an error occurs during the database operation.
     */
    @Override
    public void insertSubsAddlTGList(Collection<KnAddlTGInfoDTO> corpAddlTGInfoDTOS, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "insertSubsAddlTGList";
        knLogger.debug(methodName, "ENTRY - corpAddlTGInfoDTOS: ", corpAddlTGInfoDTOS.size());
        PreparedStatement pstmt = null;
        String query = "INSERT INTO DG.SUBSCRPTTRADIOTGLIST (MDN, GROUPID, ZONEID, CHANNELID) VALUES (?, ?, ?, ?)";
        try {
            Connection conn = persisterTxn.getDBConnection(xdmPttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            for (KnAddlTGInfoDTO dto : corpAddlTGInfoDTOS) {
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
                    xdmPttServerId, KnDAOSourceTypes.CORPGROUPMEMBERLIST, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.debug(methodName, "EXIT - ");
        }
    }

    public String selectXDMCorpFS(int corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpInfoDAO corpInfoDAO = KnTablesRegistry.getXDMTablesRegistry().createXDMCorpInfoDAO(xdmPttServerId);
        return corpInfoDAO.selectXDMCorpFS(corpId, readOnly, persisterTxn);
    }


    @Override
    public boolean IsMdnPresentAsGroupMember(String groupMember, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpInfoDAO corpInfoDAO = KnTablesRegistry.getXDMTablesRegistry().createXDMCorpInfoDAO(xdmPttServerId);
        return corpInfoDAO.IsMdnPresentAsGroupMember(groupMember,persisterTxn);
    }

    @Override
    public boolean IsMdnPresentAsContact(String contactMdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpInfoDAO corpInfoDAO = KnTablesRegistry.getXDMTablesRegistry().createXDMCorpInfoDAO(xdmPttServerId);
        return corpInfoDAO.IsMdnPresentAsContact(contactMdn,persisterTxn);
    }

    @Override
    public boolean IsMdnPresentAsTarget(String targetMdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpInfoDAO corpInfoDAO = KnTablesRegistry.getXDMTablesRegistry().createXDMCorpInfoDAO(xdmPttServerId);
        return corpInfoDAO.IsMdnPresentAsTarget(targetMdn,persisterTxn);
    }

    @Override
    public boolean IsMdnPresentAsDestination(String destinationMdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpInfoDAO corpInfoDAO = KnTablesRegistry.getXDMTablesRegistry().createXDMCorpInfoDAO(xdmPttServerId);
        return corpInfoDAO.IsMdnPresentAsDestination(destinationMdn,persisterTxn);
    }

    @Override
    public void insertIntoAsyncTable(KnPendingTxnInfoDTO notifyDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpInfoDAO corpInfoDAO = KnTablesRegistry.getXDMTablesRegistry().createXDMCorpInfoDAO(xdmPttServerId);
        corpInfoDAO.insertIntoAsyncTable(notifyDTO,persisterTxn);
    }

    @Override
    public int getMdnCount(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpResourceListIndexDocDAO serviceDocDAO = KnTablesRegistry.getXDMTablesRegistry().createXDMCorpResourceListIndexDocDAO(xdmPttServerId);
        return serviceDocDAO.getMdnCount(mdn, persisterTxn);
    }

    public Set<KnPTTSettingDocInfoDTO> getAllPTTSettingDocList(int corpId, String hierarchyId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getAllPTTSettingDocList(int, String, KnPersisterTxn)";
        knLogger.debug(methodName, "corpId: ", corpId, ", hierarchyId: ", hierarchyId);
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        Set<KnPTTSettingDocInfoDTO> pttDocList = new HashSet<>();
        try {
            if (hierarchyId == null || hierarchyId.isEmpty()) {
                hierarchyId = String.valueOf(corpId);
            }
            query = "SELECT DISTINCT PTT_SETTING_DOCID,IS_DEFAULTDOC  FROM DG.CORP_PTTSETTING_MAP WHERE CORPID =? AND HIERARCHY_ID =?";
            conn = persisterTxn.getDBConnection(xdmPttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, corpId);
            pstmt.setString(2, hierarchyId);
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully.");
            while (rs.next()) {
                KnPTTSettingDocInfoDTO respDTO = new KnPTTSettingDocInfoDTO();
                respDTO.setDocId(rs.getString("PTT_SETTING_DOCID"));
                respDTO.setIsDefault(rs.getInt("IS_DEFAULTDOC"));
                pttDocList.add(respDTO);
            }
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occurred while retrieving the getPTTSettingDocIdsForHierarchy - ", e);
            throw KnDbUtil.processException(e, "Failed while retrieving the getPTTSettingDocIdsForHierarchy -  " + e,
                    xdmPttServerId, "CORP_PTTSETTING_MAP", query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        knLogger.debug(methodName, "pttDocList: ", pttDocList);
        return pttDocList;
    }

    /**
     * Retrieves the cluster ID associated with a given PTT server ID.
     * <p>
     * This method executes a SQL query to fetch the cluster ID from the database
     * by joining the `SIGNALING_CARD_INFO` and `SIGNALINGCARD_SITE_MAP` tables.
     *
     * @param knPersisterTxn The transaction object used to manage database connections.
     * @return The cluster ID as an `String` if found, otherwise `null`.
     */
    @Override
    public Map<String,String> getClusterId(KnPersisterTxn knPersisterTxn) throws KnDAOException {
        final String methodName = "getClusterId()";
        knLogger.info(methodName, "Entry : Fetching cluster ID for PTT server ID: ", xdmPttServerId, " from database");
        String selectQuery = " SELECT DISTINCT ssm.CLUSTERID, sci.PTTSERVERID FROM  DG.SIGNALINGCARDINFO sci INNER JOIN DG.SIGNALINGCARD_SITE_MAP ssm ON (sci.SIGNALINGCARDID = ssm.SIGNALINGCARDID) ";
        Map<String, String> clusterIdMap = new HashMap<>();
        Connection connection=null;
        PreparedStatement ps=null;
        ResultSet rs= null;
        try {
            xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
            knPersisterTxn.open();
            connection = knPersisterTxn.getDBConnection(xdmPttServerId,true);
            ps = connection.prepareStatement(selectQuery);
            rs= ps.executeQuery();
            while (rs.next()) {
                String pttserverid = rs.getString("PTTSERVERID");
                String clusterid = rs.getString("CLUSTERID");
                clusterIdMap.putIfAbsent(pttserverid, clusterid);
            }
            knPersisterTxn.save();
        } catch (Exception e) {
            knLogger.error("getClusterId", "Exception in getting clusterId", e);
            throw new KnDAOException(KnErrorCodes.DAO.INTERNAL_ERROR,e.getMessage(),e);
        }finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(ps);
            KnDbUtil.closeConnection(connection);
        }
        knLogger.info(methodName, "Exit : Retrieved cluster ID map: ", clusterIdMap);
        return clusterIdMap;
    }

}
