/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ***************************************************************************
 * File name:   Kn.java
 * Subsystem:
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ravi Shanker .P       Dec 21, 2010        7.0
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
 * ******************************************************************************
 */
package com.kodiak.xdms.server.common.resources;

import com.kodiak.dbmgr.KnDBConst;

public class KnConstants {
    public static final String SUCCESS = "SUCCESS";

    //List of table names and column names or Id Generation
    public static final String EPC_MCX_CRILOC_FLAG = "EPC_MCX_CRILOC_FLAG";

    public static final String DG = "DG.";
    public static final String TABLE_XDM_RESOURCELISTINDEXDOC = DG + "XDM_RESOURCELISTINDEXDOC";
    public static final String TABLE_XDM_RESOURCELISTINDEXDOC_COL = "RESOURCELISTDOCID";
    public static final String TABLE_XDM_RESOURCELIST = DG + "XDM_RESOURCELIST";
    public static final String TABLE_XDM_RESOURCELIST_COL = "RESOURCELISTID";
    public static final String TABLE_XDM_CONTACTLIST_DOCMAP = DG + "XDM_CONTACTLIST_DOCMAP";
    public static final String TABLE_XDM_CONTACTLIST_DOCMAP_COL = "CONTACTLISTID";
    public static final String TABLE_XDM_DIRECTORY = DG + "XDM_DIRECTORY";
    public static final String TABLE_XDM_DIRECTORY_COL = "DIRDOCID";
    public static final String TABLE_XDM_RLSSERVICEDOC = DG + "XDM_RLSSERVICEDOC";
    public static final String TABLE_XDM_RLSSERVICEDOC_COL = "RLSDOCID";
    public static final String TABLE_XDM_CORPRESOURCELISTINDEXDOC = DG + "XDM_CORPRESOURCELISTINDEXDOC";
    public static final String TABLE_XDM_CORPRESOURCELISTINDEXDOC_COL = "RESOURCELISTDOCID";
    public static final String TABLE_TRANSPORT_SERVER_SOURCE_INFO = DG + "TRANSPORTSERVERSOURCEINFO";
    public static final String TABLE_TRANS_SER_SRC_COL =  "SEQUENCENUMBER";
    public static final String XDMS_ALLOW_SUMDN_AS_CONTACT = "XDMS_ALLOW_SUMDN_AS_CONTACT";
    public static final String AUTO_PAIR_SUBLIST_NAME = "CORP_AUTO_PAIR_SUBLIST";
    public static final String AUTO_PAIR_GROUP_NAME = "CORP_AUTO_PAIRING_GRP";
    public static final String ENABLE_CORP_AUTO_PAIRING = "ENABLE_CORP_AUTO_PAIRING";
    public static final String POC_VOCODER_PRIORITY_LIST = "POC_VOCODER_PRIORITY_LIST";
    public static final String BULK_PROV_BATCH_SIZE = "BULK_PROV_BATCH_SIZE";
    public static final String ENABLE_OIDC = "ENABLE_OIDC";
    public static final String IDS_BLOCKED_CAT_CORP_LIST = "IDS_BLOCKED_CAT_CORP_LIST";
    public static final String ALLOWED_SIPMSG_SIZE_ON_TCP = "ALLOWED_SIPMSG_SIZE_ON_TCP";
    public static final String ALLOWED_SIPMSG_SIZE_ON_UDP = "ALLOWED_SIPMSG_SIZE_ON_UDP";
    public static final String ALLOWED_SIPMSG_HEADERS_SIZE = "ALLOWED_SIPMSG_HEADERS_SIZE";
    public static final String CORP_AUTO_PAIRING_SIZE = "CORP_AUTO_PAIRING_SIZE";
    public static final String ROAM_NA_CLIENT_TYPES = "ROAM_NA_CLIENT_TYPES";
    public static final String IP_VER_CELLULAR = "IP_VER_CELLULAR";
    public static final String IP_PREF_ON_CELL_INTF = "IP_PREF_ON_CELL_INTF";
    public static final String IP_PREF_ON_WIFI_INTF = "IP_PREF_ON_WIFI_INTF";
    public static final String IP_VER_MULTICAST = "IP_VER_MULTICAST";
    public static final String IP_PREF_MULTICAST = "IP_PREF_MULTICAST";
    public static final String SERVER_PREFERRED_VOCODER_PROFILEID = "SERVER_PREFERRED_VOCODER_PROFILEID";
    public static final String SPECIFIC_CODEC_SUPPPORT_OLDER_BREW_CLIENTS = "SPECIFIC_CODEC_SUPPPORT_OLDER_BREW_CLIENTS";
    public static final String OLDER_CLIENTS_CODEC_SUPPORTED_BREW_UA ="OLDER_CLIENTS_CODEC_SUPPORTED_BREW_UA";
    public static final Integer AMR_HALF_RATE_PROFILEID = 4;
    public static final String CLUSTERID_ENV_NAME = "CLUSTERID";
    public static final String INET_APN = "INET";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String MAX_USER_PROFILES = "MAX_USER_PROFILES";
    public static final String MAX_STATUS_MSG_PER_OSM_LIST = "MAXSTATUSMSGPEROSMLIST";
    public static final String MAX_STATUS_SHORT_TEXT_LENGTH = "MAXSTATUSSHORTEXTLENGTH";
    public static final String MAX_STATUS_MSG_LENGTH = "MAXSTATUSMSGLENGTH";
    public static final String COMMON_CONTACTLIST_SIZE = "COMMON_CONTACTLIST_SIZE";
    public static final String COMMON_CONTACTLIST_PERSUB = "COMMON_CONTACTLIST_PERSUB";
    public static final String MAXMEM_NONAP_SUBLIST ="MAXMEM_NONAP_SUBLIST";
    public static final String BLK_EXT_CONT_LMT = "BLK_EXT_CONT_LMT";
    public static final String BLK_EXT_CONT_LMT_ALLOWED = "CORPADMINFSBULKAPILIMIT";
    public static final String MAX_USERPROFILES_PERSUB1 ="MAX_USERPROFILES_PERSUB";
    public static final String MAX_CORP_HIERARCHY_LEVELS ="MAX_CORP_HIERARCHY_LEVELS";
    public static final String TARGET_INFO_OPTIMIZATION = "TARGET_INFO_OPTIMIZATION";
    public static final String TARGET_INFO_OPTIMIZATION_ENABLED = "1";

    //end of the list

    public static final String ALLOW_MALFORMED_UA_ACTIVATION = "ALLOW_MALFORMED_UA_ACTIVATION";
    public static final String CACHE_CLEANUP_TIMER = "XDM_DM_CONFIGCACHEREFRESHTIMER";
    public static final String ACTIVE_RELEASE_DIR = "activeRelDir";

    public static final String DB_MGR_FILE_NAME = "dbmgr.props";
    public static final String DB_MGR_DBDSN = "DBDSN";
    public static final String DB_MGR_LOCAL_PTTID = "PTTSERVERID";

    public static final String LIBRARY_NAME_PUB_MGMT = "PUBLIB";
    public static final String LIBRARY_NAME_CORP_MGMT = "CORPLIB";
    public static final String LIBRARY_NAME_NNICG_MGMT = "NNICGLIB";
    public static final String URI = "uri";

    public static final String UPDATE_CLIENT_FS = "updateClientFS";
    public static final String JUNK_PASSWORD = "JUNK";

    //status constants
    public static final int STATUS_SUCCESS = 0;
    public static final int STATUS_FAILURE = 1;

    //boolean flags
    public static final boolean TRUE = true;
    public static final boolean FALSE = false;
    public static final int SUBSYS_ID_APPROUTER = 25;
    public static final int SUBSYS_ID_XDMDATAMGR = 110;
    public static final int XDMDATAMGR_INTERFACEID = 0;
    public static final int VIDEO_PERMISSION_VALUE = 0;
    public static final int MSG_RESOLVED = 1;
    public static final int HEADER_SIZE = 6;
    public static final int FEATUREACCESSINDEX = 52;

    public static final int INITIAL_ETAG = 1;
    public static final KnDBConst.DataStores DUAL_DATA_STORE = KnDBConst.DataStores.XDM_SHARED_DATA;
    /*public static final KnDBConst.DataStores PRI_DATA_STORE = null;*/

    public static final int BIT_TRUE = 1;
    public static final int BIT_FALSE = 0;


    //for emergency mcsxcap
    public static final int CONTACT_MCSXCAP = 1;
    public static final int GROUP_MCSXCAP = 2;

    public static final int ACTIVATED_MCSXCAP = 2;
    public static final String AT= "@";
    public static final String SIP = "sip:";
    public static final String DOT = ".";
    public static final int EXTERNAL_SUBSCRIBER = 1;
    public static final int NOT_EXTERNAL_SUBSCRIBER = 0;

    public static final int NO_PRIORITY = 100;
    public static final int FETCH_BATCH_SIZE = 100;
    public static final String LINE_SAPERATOR="_";
    public static final int ITERATIONS_10_000 = 10000;
    public static final int DERIVEDKEYLENGTH = 32;
    public static final int EIGHT = 8;
    public static final String WEBDISPATCHER = "WDS";
    public static final String DEVICE_SHARING_FEATURE_FLAG = "DEVICE_SHARING_FEATURE_FLAG";
    public static final String TEMP_PASSWORD_EXPIRY = "TEMP_PASSWORD_EXPIRY";
    public static final String MCX_URI_SCHEMES = "MCX_URI_SCHEMES";
    public static final int SG_MDN_MEMBER_TYPE = 1;
    public static final String BASE_MDN_INDEX = "0";
    public static final String DEFAULT_PROFILE = "0";

    public static final String AUTO_DEVICESHARE_WIFI_CC="AUTO_DEVICESHARE_WIFI_CC";
    public static final String ASYNC_OP_RETENTION_TIME_IN_MIN = "ASYNC_OP_RETENTION_TIME" ;
    public static final String TRK_EMAIL_FLAG = "TRK_EMAIL_FLAG";
    public static final String EMERG_DEST_ALL = "EMERG_DEST_ALL";

    public static final String IS_SG_SSL_ENABLED = "IS_SG_SSL_ENABLED";
    public static final String SSL_ENABLED = "1";
    public static final String MULTISITE_DEPLOYMENT_FLAG = "MULTISITE_DEPLOYMENT_FLAG";
    public static final int IS_PRE_CONFIG_GROUP_ENABLE = 1;
    public static final String CHECKTRUE = "true";
    public static final String CRI_DEFAULT_USER_AGENT = "CRI_DEFAULT_USER_AGENT";
    public static final int BATCH_SIZE = 100;
    public static final int MCX_GROUP_INDICATOR_VALUE = 1;
    public static final int MCX_GROUP_COUNT = 0;
    public static final String LOCK_REQUEST_DENIED_ERROR_MSG = "Lock request denied";
    public static final int TRANSACTION_COUNT = 20000;
    public static final int MAX_ATTEMPT_TO_DECODE = 10;

    public static enum SUBSCRIBERS_CLIENT_TYPE {
        UNKNOWN(0),
        HANDSET(1),
        DESKTOP(2),
        DISPATCH(3),
        POCDONORRADIO(4),
        WIFIONLY(5),
        THIRDPARTYPOCCLIENT(6),
        SU_CLIENT(7),
        SG_CLIENT(8),
       // RESERVED(9), reserved for future use
        CROSS_CARRIER_PTT_CLIENT(10),
        PDVCONNECT(11),
        THIRDPARTYDISPATCHERCLIENT(12),
        MOBILE_CLIENT(13),

    	//LMR client type changes
        PTTRADIOHANDSETCLIENT(14),
        PTTRADIOCROSSCARRIERCLIENT(15),
        PTTRADIOWIFIONLYCLIENT(16),
        SGMDNPATCH(17),
        DATAGROUPMDN(18),
        STANDALONECAMERA(19);
    	
        int clientType;

        SUBSCRIBERS_CLIENT_TYPE(int clientType) {
            this.clientType = clientType;
        }

        public int value() {
            return clientType;
        }
    }

    public static enum SERVICE_AUTH_STATUS {
        PROVISIONED(0),
        UNUSED(1),
        ACTIVATED(2),
        DEACTIVATED(3),
        DELETION_IN_PROGRESS(4),
        MARKED_FOR_ASYNC_DELETION(10),
        PRE_PROVISIONED(99);

        int serviceAuthStatus;

        SERVICE_AUTH_STATUS(int status) {
            this.serviceAuthStatus = status;
        }

        public int value() {
            return serviceAuthStatus;
        }
    }

    public static enum PUBLIC_SUBSCRIPTION_TYPE {
        NONE(0), PUBLIC(1);

        int publicSubscriptionType;

        PUBLIC_SUBSCRIPTION_TYPE(int type) {
            this.publicSubscriptionType = type;
        }

        public int value() {
            return publicSubscriptionType;
        }
    }

    public static enum CORP_SUBSCRIPTION_TYPE {
        NONE(0), CORPORATE(1);

        int corpSubscriptionType;

        CORP_SUBSCRIPTION_TYPE(int type) {
            this.corpSubscriptionType = type;
        }

        public int value() {
            return corpSubscriptionType;
        }
    }

    public static enum RESPONSE_STATUS {
        SUCCESS(0), FAILURE(1);
        int responseStatus;

        RESPONSE_STATUS(int responseStatus) {
            this.responseStatus = responseStatus;
        }

        public int value() {
            return responseStatus;
        }
    }

    public static enum SYNC_REQUEST_STATUS {
        SUCCESSFUL_DOC_CREATION(200), SUCCESSFUL_DOC_DELETION(201),FAILURE(0);

        int responseStatus;

        SYNC_REQUEST_STATUS(int responseStatus) {
            this.responseStatus = responseStatus;
        }

        public int value() {
            return responseStatus;
        }
    }

    public static enum MESSAGE_TYPE {
        NO_ACTION(0),
        XDM_DIFF_NOTIFY(3001),
        USER_DEACTIVATE(3002),
        USER_DELETE(3003),
        SUBSCR_PROFILE_CHANGE(3004),
        MDN_CHANGE(3005),
        TGSC_MODE_CHANGE(3007),
        CLIENT_TYPE_CHANGE(3),
        SUBSCR_TYPE_CHANGE(4),
        ACTV_FS_CHANGE(5),
        SUBSCR_NAME_CHANGE(6),
        USER_CREDENTIAL_CHANGE(7),
        DEVICE_DELETE(3008);

        int messageId;

        MESSAGE_TYPE(int type) {
            this.messageId = type;
        }

        public int value() {
            return messageId;
        }
    }

    public static enum REASON {
        USER_DEACTIVATE("deactivated"),
        USER_DELETE("deleted"),
        CLIENT_TYPE_CHANGE("client-type-change"),
        MDN_CHANGE("msisdn-change"),
        USER_CREDENTIAL_CHANGE("user-cred-change"),
        USER_DEACTIVATE_AUTH("admin-deactivated"),
        PRIVACY_OPT_IN("privacy-opt-in");

        String reasonCode;

        REASON(String reasonCode) {
            this.reasonCode = reasonCode;
        }

        public String value() {
            return reasonCode;
        }
    }

    public static enum RESOURCE_LIST_TYPE {
        UNKNOWN(0), OMA_BUDDY_LIST(1);
        int resourceListType;

        RESOURCE_LIST_TYPE(int resourceListType) {
            this.resourceListType = resourceListType;
        }

        public int value() {
            return resourceListType;
        }
    }

    public static enum DOC_CHANGE_TYPE {
        ADD(1), REPLACE(2), REMOVE(3);

        int docChangeType;

        DOC_CHANGE_TYPE(int docChangeType) {
            this.docChangeType = docChangeType;
        }

        public int value() {
            return docChangeType;
        }
    }

    public static enum TYPE_OF_DOC {
        DIR_DOC(0), SUBS_CONFIG_DOC(1);

        int typeOfUri;

        TYPE_OF_DOC(int typeOfUri) {
            this.typeOfUri = typeOfUri;
        }

        public int value() {
            return typeOfUri;
        }
    }

    public static final String TEL_URI_TEMPLATE = "tel:+";
    public static final String DIR_DOC_AUID = "org.openmobilealliance.xcap-directory";
    public static final String DIR_DOC_NAME = "directory.xml";
    public static final String DIR_DOC_TYPE = "users";


     public static enum ACCOUNT_TYPE {
        BUSINESS("B"), INDIVIDUAL("R");

        private String accountType;

        ACCOUNT_TYPE(String accountType) {
            this.accountType = accountType;
}

        public String value() {
            return accountType;
        }
	}

	public static final int SUBS_TGS_SERVER_BIT = 20;
    public static final int SUBS_TGSC_CLIENT_BIT = 28;
	public static final int SUBS_TGS_CLIENT_BIT = 31;
    public static final int SUBS_PTTRADIOCLIENT_SERVER_BIT = 44;
    public static final int IS_OSM_AUTHORIZE_BIT = 66;
    public static final int DISPATCHER_CLIENT = 3;
    public static final int LARGE_GROUP_DISABLED = 0;
    public static final int MCX_GRP_INDICATOR = 1;
    public static final int IS_LARGE_GROUP = 1;
    public static final String USER_LOGIN ="userLogin";
    public static final String GET_SUBSCRIBER_CONFIG_DOC ="getSubscriberConfigDoc";
    public static final String UPDATE_SUBSCRIBER ="updateSubscriber";
    public static final int THIRD_PARTY_DISPATCHERS_CLIENT = 12;
    public static final Integer UNKNOWN_CLIENT_TYPE = 0;

    public static final Integer PROTOCOL_VERSION_7_X = 7;
    public static final Integer PROTOCOL_VERSION_8_X = 8;

    public static final Integer PROTOCOL_VERSION_8 = 8;
    public static final Integer PROTOCOL_VERSION_9_X = 9;
    public static final Integer PROTOCOL_VERSION_10_X = 10;
    public static final Integer PROTOCOL_VERSION_13_X = 13;
    public static final Integer PROTOCOL_VERSION_16_X = 16;
    public static final Integer PROTOCOL_VERSION_18 = 18;
    public static final Integer PROTOCOL_VERSION_18_X = 18;
    public static final Integer PROTOCOL_VERSION_19_X = 19;
    public static final Integer PROTOCOL_VERSION_20_X = 20;
    public static final Integer PROTOCOL_VERSION_20 = 20;
    public static final Integer PROTOCOL_VERSION_21 = 21;
    public static final Integer PROTOCOL_VERSION_22 = 22;
    public static final Integer PROTOCOL_VERSION_23 = 23;
    public static final Integer PROTOCOL_VERSION_24 = 24;
    public static final Integer PROTOCOL_VERSION_27 = 27;
    public static final Integer PROTOCOL_VERSION_29 = 29;

    public static final String UPDATE_AUTO_PAIR_CID = "99999";
    public static final String UPDATE_AUTO_PAIR_OPN = "updateCorpAutoPairing";
    public static final String ADD_TO_PAIRING_CID = "99998";
    public static final String ADD_TO_PAIRING_OPN = "addToPairingList";
    public static final String SUCCESS_MSG = "Operation is successfull";
    public static final String FAILURE_MSG = "Operation failed with error";
    public static final String UPDATE_AUTO_PAIR_BULK_CID = "88888";
    public static final String ADD_TO_PAIRING_BULK_CID = "88889";

    public static final String PTX_SYNC_GW_ADMIN_PORT = "PTXSYNCGWADMINPORT";
    public static final String PTX_BUCKET_NAME = "PTXBUCKETNAME";
    public static final String CONSUL_SERVICE_FLAG = "CONSUL_SERVICE_FLAG";
    public static final String HTTP_SCHEME = "http://";
    public static final String HTTP_SCHEME_TLS = "https://";
    public static final String COLON = ":";
    public static final String FORWARD_SLASH = "/";
    public static final String STAR = "*";
    public static final String COMMA = ",";
    public static final int ZERO = 0;
    
    //Added for NEXT GEN CAT
   // public static final String NXT_GEN_CAT_ENABLED = "NXT_GEN_CAT_ENABLED";
     public static final String XDM_NXT_GEN_CAT_ENABLED = "XDMS_NEXT_GEN_CAT_ACCESS";
    public static final String DEFAULT_WEBRTC_VOCODER_PROFILEID = "DEFAULT_WEBRTC_VOCODER_PROFILEID";
    public static final String MAX_LOC_WATCHERS_PER_GRP = "MAX_LOC_WATCHERS_PER_GRP";
    public static final String CONV_CLIENT_ENABLED = "CONV_CLIENT_ENABLED";
    public static final String IDM_INTF_INT_FQDN = "IDM_WS_FQDN_INT";
    public static final String IDM_WS_FQDN_INT_PORT = "IDM_WS_FQDN_INT_PORT";
    public static final String OIDC_INTF_INT_FQDN = "";
    public static final String OIDC_WS_FQDN_INT_PORT = "";
    public static final String UNEXPECTED_EXCEPTION_OCCURED = "Un-expected exception occurred - ";
    public static final String DAO_EXCEPTION_OCCURED = "DAO Exception occurred - ";
    //TP constants
    public static final String DELIM = ":";

    //Added for 8.1.2 release
    public static final String XDMS_LMR_SUB_DEFAULT_NAME = "XDMS_LMR_SUB_DEFAULT_NAME";
    
    public static enum PAMACCOUNT_POOL_USAGE {
    	MDN_NOT_EXIST(0),
        NOT_IN_USE(1),
        IN_USE(2);

        int usage;

        PAMACCOUNT_POOL_USAGE(int usage) {
        	this.usage = usage;
        }

        public int value() {
            return usage;
        }
    }

    public static enum GROUP_CREATED_BY {
    	CAT(0),
        TP(1),
        ABDG(2);

        int user;

        GROUP_CREATED_BY(int user) {
        	this.user = user;
        }

        public int value() {
            return user;
        }
    }

    public static int mappGroupTypeToXcap(int groupType) {
        int mappedGrpType = 0;
        switch (groupType){
            case 2:
                mappedGrpType = 2;
                break;
            case 3 :
                mappedGrpType = 1;
                break;
        }
        return mappedGrpType;
    }

    // PTX constants
    public static final String TEXTMSGFLAG = "TEXTMSGFLAG";
    public static final String MULTIMEDIAMSGFLAG = "MULTIMEDIAMSGFLAG";
    public static final String LOCATIONMSGFLAG = "LOCATIONMSGFLAG";
    public static final String URGENTMSGFLAG = "URGENTMSGFLAG";
    public static final String SDSFEATUREFLAG = "SDSFEATUREFLAG";
    public static final String FDFEATUREFLAG = "FDFEATUREFLAG";

    public static final int TEXTMSGFLAGBIT = 33;
    public static final int MULTIMEDIAMSGFLAGBIT = 34;
    public static final int LOCATIONMSGFLAGBIT = 35;
    public static final int URGENTMSGFLAGBIT = 36;

    public static enum CB_PROFILE_TXN_TYPE {
        UNKNOWN(0),
        PROFILE_CREATION(1),
        PROFILE_UPDATE(2),
        PROFILE_ENABLE(3),
        PROFILE_DISABLE(4),
        PROFILE_DELETE(5);

        int txnType;

        CB_PROFILE_TXN_TYPE(int txnType) {
            this.txnType = txnType;
        }

        public int value() {
            return txnType;
        }
    }

    // IDM Operation Type:
    public static final String RESET_MAIL = "resetemail";
    public static final String UPDATE_USER_PASSWORD = "pwd";
    public static final String REGENERATE_USER_PASSWORD = "regenpwd";
    public static final String CHANGE_SERVICE_AUTH_STATUS = "state?accstate=";
    public static final String INVOKE_ACTION = "actions";

    public static final String BASE_PKGCODE = "BASE_PKGCODE";
    public static final String COMMAND_TIER_PKG = "TIER2";
    public static final Integer TIER_PKG_TYPE = 1;
    public static final Integer ADDON_PKG_TYPE = 2;
    public static final String ADD_ACTION = "Add";
    public static final String REMOVE_ACTION = "Remove";
    public static final String CANCEL_ACTION = "Cancel";
    public static final Integer DEFAULT_PROFILE_ID = 1;
    public static final Integer DEFAULT_DATAPKG_ID = 1;
    public static final Integer USER_LICENSE_TYPE = 1;
    public static final Integer USER_LICENSE_TYPE_DISABLED = 0;
    public static final Integer MCSCOMPLIANCE = 1;
    public static final Integer KODIAK_CLIENT = 0;
    public static final String DEFAULTAPNNAME = "INET";
    public static final int USER_PROFILE_MGMT_BIT=76;
    public static final int MCPTT_COMPLAIANCE_BIT=81;
    public static final Integer USER_PROFILE_INDEX=0;
    public static final int IS_DEFAULT_PROFILE = 1;
    public static final Integer MCDEVICE_LICENSE_TYPE = 0;
    public static final int REMOTEPUSHNOTIfICATION=43;
    public static final int HYBRID_IOS_BIT = 135;
    public static final Integer DISABLE = 0;
    public static final Integer ENABLE = 1;
    public static final String FEATURE_DISABLED = "0";
    public static final int UPM_BULK_UPDATE_SIZE = 5;
    public static final int BULK_UPDATE_SIZE = 1000;
    public static final int GG_BULK_UPDATE_SIZE = 200;
    public static final int VERY_LARGE_GROUP=88;
    public static final int VERY_LARGE_UPM=99;
    public static final String OIDCXCAP_ALERTTYPE_FOR_IOS16 = "OIDCXCAP_ALERTTYPE_FOR_IOS16";
    public static final Integer ALERT_TYPE_HIGH_PRIORITY = 7 ;
    public static final Integer ALERT_TYPE_LOW_PRIORITY = 6 ;

    public static enum SERVICE_STATUS_OP {

        PROVISIONED(0),
        ACTIVATED(2),
        DEACTIVATED(3),
        MARKED_FOR_ASYNC_DELETION(10);

        int serviceStatusOp;

        SERVICE_STATUS_OP(int status) {
            this.serviceStatusOp = status;
        }

        public int value() {
            return serviceStatusOp;
        }
    }

    public static enum SERVICE_STATUS_AUTHUSER {

        PROVISIONED(0),
        ACTIVATED(2),
        DEACTIVATED(3);

        int serviceStatusAuthUser;
        SERVICE_STATUS_AUTHUSER(int status){
            this.serviceStatusAuthUser = status;
        }
        public int value() {
            return serviceStatusAuthUser;
        }
    }

    public static final String DYNAPI_SERVICE_ENABLED = "DYNAPI_SERVICE_ENABLED";
    public static final String NUM_OF_LG_SUPPORTED ="NUM_OF_LG_SUPPORTED";
    public static final String LOCATION_FEATURE_FOR_LG_FLAG = "LOCATION_FEATURE_FOR_LG_FLAG";
    public static final int ENABLE_LOCATION_FEATURE_FOR_LG_FLAG = 1;
    public static final int DISABLE_LOCATION_FEATURE_FOR_LG_FLAG = 0;
    
    // Backward notification compatibility check
    public static final String VERSION = "1.0|2.0";
    public static final String VERSION_2X = "2.0";
    
    
    public static final int ADDON_DATA_PKG_TYPE=1; 
    public static final int QPP_DATA_PKG_TYPE=2; 
    public static final int DEFAULT_QPP_ID=0;

    public static final String AWARE_CLIENTS = "6|12|13|18";

    public static final int DISPATCH_TYPE_WEB = 1;
    public static final String FIRSTFLAG_CONFIG = "FIRSTFLAG_CONFIG";
    public static final String MCPTT_CELLULAR_SIGNALING_TRANSPORT = "MCPTT_CELLULAR_SIGNALING_TRANSPORT";
    //default time slot
    public static final int TIME_SLOT_TYPE = 0;

    public static enum IDM_ACCOUNT_STATUS {

        DISABLE(0),
        ENABLE(1);

        int state;

        IDM_ACCOUNT_STATUS(int status) {
            this.state = status;
        }

        public int value() {
            return state;
        }
    }

    public static final int DEVICECREATEDAS = 1;
    public static final int DEVICESHARED = 2;
    public static final int IMPLICIT_DEVICE = 0;

    public static enum MCDEVICESHARED_TYPE {
		NONE(0), SHARED(1);

		int type;

		MCDEVICESHARED_TYPE(int type) {
			this.type = type;
		}

		public int Value() {
			return type;
		}

	}
    public static enum UPM_JOB_STATUS {

        NEW(0),
        INPROGRESS(1),
        COMPLETE(2),
        FAILURE(3),
        RESTART(4),
        CRASHED(5),
        STANDBY(6),
        STALE(7),
        VERY_LARGE_GRP_CLEAN_NEW(8),
        RETRY(9),
        WATCHER_NOTIFICATION_ASSIGN(10),
        WATCHER_NOTIFICATION_INPROGRESS(11),
        WATCHER_NOTIFICATION_SENT(12),
        WATCHER_NOTIFICATION_MODIFY_UPM_ADD_GROUP(13),
        WATCHER_NOTIFICATION_MODIFY_UPM_MODIFY_GROUP(14),
        WATCHER_NOTIFICATION_MODIFY_UPM_REMOVE_GROUP(15);


        int state;

        UPM_JOB_STATUS(int status) {
            this.state = status;
        }

        public int Value() {
            return state;
        }

    }


    public static enum UPM_OPERATION_TYPE {

        ASSIGN_USER_PROFILE(1),
        MODIFY_USER_PROFILE(2),
        DELETE_USER_PROFILE_MDN(3),
        DELETE_GROUP_FROM_PROFILE(4),
        USER_PROFILE_NOTIFICATION(5),
        DELETE_GROUP_SHARED_USER_PROFILE_NOTIFICATION(6),
        STALE_UPM_ELEMENTS(7),
        VERY_LARGE_GROUP_BIT_DISABLED(8),
        FEATURE_BIT_DOWNGRADE(9),
        INCONSISTENT_DATA_UPM(10),
        RETRY_MODIFY_USER_PROFILE(11),
        WATCHER_NOTIFICATION_ASSIGN(12),
        WATCHER_NOTIFICATION_MODIFY_UPM_ADD_GROUP(13),
        WATCHER_NOTIFICATION_MODIFY_UPM_MODIFY_GROUP(14),
        WATCHER_NOTIFICATION_MODIFY_UPM_REMOVE_GROUP(15);

        int opType;

        UPM_OPERATION_TYPE(int opType) {
            this.opType = opType;
        }

        public int Value() {
            return opType;
        }

    }

    public static enum UPM_RESOURCE_TYPE{

        MDN(1),
        GROUP(2),
        CONTACTLIST(3),
        PROFILEMDN(4);

        int type;

        UPM_RESOURCE_TYPE(int type) { this.type = type; }

        public int Value() { return type; }
    }

    public static enum UPM_TASK_TYPE {

        CREATESUBSCRIBER("CREATE_SUBSCRIBER"),
        ASSIGNCONTACTLIST("ASSIGN_CONTACT_LIST"),
        GROUPADDITION("GROUP_ADDITION"),
        TARGETPERMISSSION("TARGET_PERMISSION"),
        MODIFYUPM("MODIFY_UPM"),
        MODIFYUPMGROUP("MODIFY_UPM_GROUP"),
        MODIFYUPMSUBLIST("MODIFY_UPM_SUBLIST"),
        MODIFYUPMPERMISSION("MODIFY_UPM_PERMISSION"),
        MODIFYUPMFS("MODIFY_UPM_FS"),
        DELETESUBSCRIBER("DELETE_SUBSCRIBER"),
        TARGETPERMISSSION_TO_AU("TARGET_PERMISSION_TO_ALL_AU"),
        DELETEGROUP("DELETE_GROUP"),
        USER_PROFILE_NOTIFICATION("USER_PROFILE_NOTIFICATION");

        String value;

        UPM_TASK_TYPE(String value) {
            this.value = value;
        }

        public String Value() {
            return value;
        }

    }



    public static enum USERPROFILESTATUS{

        ACTIVE(1),
        DELETE_IN_PROGRESS(2);

        int type;

        USERPROFILESTATUS(int type) { this.type = type; }

        public int Value() { return type; }
    }

    public static enum MSGREADSTATUS {

        UNREAD(0),
        READ(1);

        int state;

        MSGREADSTATUS(int status) {
            this.state = status;
        }

        public int Value() {
            return state;
        }

    }
    
    public static enum DEVICE_TYPE {

        DEFAULT_DEVICE(0),
        RADIO_NEXT_DEVICE(1),
        MC_DEVICE(2),
        TABLET(3),
        DISPATCHER(4),
        MOTOTRBO(5),
        RG170(6),
        RG275(7),
        TLK_100(8),
        TLK_150(9),
        SLN_1000(10),
        EVOLVE(11),
        ION(12);

        int type;

        DEVICE_TYPE(int status) {
            this.type = status;
        }

        public int Value() {
            return type;
        }

    }
    public static final String MASS_LOCATION_ENABLED= "MASS_LOCATION_ENABLED";
    public static final String ONE_MESSAGE_SERVICE_ENABLED = "ONE_MESSAGE_SERVICE_ENABLED";
    public static final String AUTO_ASSIGN_ZONE= "AUTO_ASSIGN_ZONE";
    public static final String AUTO_ASSIGN_ZONE_POSITION= "AUTO_ASSIGN_ZONE_POSITION";

    public static enum ONE_MSG_STATUS {

        ENABLED("1"),
        DISABLED("0");

        String state;

        ONE_MSG_STATUS(String state){this.state = state;}

        public String value(){
            return state;
        }
    }
    public static enum LMR_BIT_STATUS {

        NO_CHANGE(0),
        MANUALLY_DISABLED(1),
        MANUALLY_ENABLED(2);

        int type;

        LMR_BIT_STATUS(int status) {
            this.type = status;
        }

        public int Value() {
            return type;
        }

    }
    
    public final static int LMR_BIT=53;
    
    public static enum SYNCGWPROFILECREATED {

        ENABLED(1),
        DISABLED(0);

        Integer state;

        SYNCGWPROFILECREATED(Integer state)
        {this.state = state;}

        public Integer value(){
            return state;
        }
    }

    public static enum DEVICE_STATUS_OP {

        ACTIVATED(1),
        DEACTIVATED(2);

        int deviceStatusOp;

        DEVICE_STATUS_OP(int status) {
            this.deviceStatusOp = status;
        }

        public int value() {
            return deviceStatusOp;
        }
    }

    public static enum MCPTT_COMPLIANCE {

        KODIAK_CLIENT(0),
        CRI_CLIENT(1);

        int type;

        MCPTT_COMPLIANCE(int type) {
            this.type = type;
        }

        public int Value() {
            return type;
        }

    }

    public enum FILTER_TYPE {
        ALL(0),
        INTERNAL(1),
        EXTERNAL(2),
        NNI(3),
        INTEROP(4);

        int filterType;

        FILTER_TYPE(int filterType) {
            this.filterType = filterType;
        }

        public int value() {
            return this.filterType;
        }
    }

    public enum OPS_CODE {
        DEFAULT(0),
        UPDATE_CORP_SUBSCRIBER(1);
        Integer opsCode;

        OPS_CODE(Integer opsCode) {
            this.opsCode = opsCode;
        }

        public Integer value() {
            return this.opsCode;
        }
    }

    public enum NOTIFICATION_PRIORITY {
        HIGH(0),
        DEFAULT(5),
        LOW(10);
        final Integer notificationPriority;

        NOTIFICATION_PRIORITY(Integer notificationPriority) {
            this.notificationPriority = notificationPriority;
        }

        public Integer value() {
            return this.notificationPriority;
        }
    }
    public static final String SYSTEM_DEFAULT_PTTSETTINGDOCID = "SYSTEM_DEFAULT_PTTSETTINGDOCID";
    public static final String PTT_TEMPLATE_DOC_TYPE = "PTT_SETTING";
    public static final String PTT_TEMPLATE_DOC_VERSION = "1.0";

}
