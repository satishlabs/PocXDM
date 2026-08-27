/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnConstants.java
 * Subsystem:  common
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * SureshKumar G        Jan 11, 2011           7.0
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
package com.kodiak.common.resources;

import java.io.File;
import java.util.*;

public class KnConstants {

    public static final String QUEUE_NAME_WEB_PROV = "WEBPROVQ";
    public static final String QUEUE_NAME_WEB_PROV_WRITE = "WEBPROVWRITEQ";
    public static final String QUEUE_NAME_WEB_PUB = "WEBPUBQ";
    public static final String QUEUE_NAME_WEB_CORP = "WEBCORPQ";
    public static final String QUEUE_NAME_WEB_CORP_WRITE = "WEBCORPWRITEQ";
    public static final String QUEUE_NAME_WEB_NNI = "WEBNNIQ";
    public static final String QUEUE_NAME_WEB_DATASYNC = "NNIGWDataSyncQ";
    public static final String QUEUE_NAME_WEB_DATA_INTF = "WEBDATAQ";

    //public static final Map<String,Object> dynamicQueueArgs =  new HashMap<>(){{put("x-queue-type", "classic");}};

    //public static final Map<String,Object> staticQueueArgs =  new HashMap<>(){{put("x-queue-type", "quorum");}};



    public static final String QUEUE_NAME_XDM_PROV = "XDMPROVQ";
    public static final String QUEUE_NAME_XDM_PROV_WRITE = "XDMPROVWRITEQ";
    public static final String QUEUE_NAME_XDM_PUB = "XDMPUBQ";
    public static final String QUEUE_NAME_XDM_CORP_WRITE = "XDMCORPWRITEQ";
    public static final String QUEUE_NAME_XDM_CORP = "XDMCORPQ";
    public static final String QUEUE_NAME_XDM_NNI = "XDMNNIQ";
    public static final String QUEUE_NAME_XDM_DATA_INTF = "XDMDATAQ";
    public static final String DROPPED_REQUEST = "DROPPEDREQUEST";
    public static final int IPC_SUBSYS_XDMS = 37;
    public static final int APP_ROUTER_PORT = 21751;
    public static final int DEFAULT_PAM_RESP_TIMER = 30;// in seconds

    public static final String CLUSTERID_ENV_NAME = "CLUSTERID";
    public static final String LOCAL_IP_ADDRESS = "LOCAL_IP_ADDRESS";
    public static final String FEATURE_ID_PROV = "3001";
    public static final String FEATURE_ID_PROV_POST = "3006";
    public static final String FEATURE_ID_PUB = "3002";
    public static final String FEATURE_ID_CORP = "3003";
    public static final String FEATURE_ID_CORP_POST = "3005";
    public static final String FEATURE_ID_NNI = "3101";
    public static final String FEATURE_ID_XDMDATA_INTF = "3004";

    public static final String PUBLISHER_ID_PROV = "PROV3001";
    public static final String PUBLISHER_ID_PUB = "PUB3002";
    public static final String PUBLISHER_ID_CORP = "CORP3003";
    public static final String PUBLISHER_ID_NNI="NNI3101";
    public static final String PUBLISHER_ID_HM = "HM0001";
    public static final String PUBLISHER_ID_NNI_SYNC = "NNI3102";
    public static final String PUBLISHER_ID_XDMDATA_INTF_REST = "RESTINTF3004";

    public static final String APP_UID_PUBLIC_CONTACT = "resource-lists";
    public static final String APP_UID_PUBLIC_GROUP = "org.openmobilealliance.groups";
    public static final String XCAP_ROOT = "kodiak-poc";
    public static final String OIDC_XCAP_ROOT = "oidcxcap";

    //war file name of the XCAP Server
    public static final String XCAP_ROOT_CONTEXT = "/kodiak-poc";
    public static final String OIDC_XCAP_ROOT_CONTEXT = "/oidcxcap";
    public static final String MCSXCAP_XCAP_ROOT_CONTEXT = "/mcsxcap";
    public static final String APP_UID_SUBSCRIBER_CONFIG = "kn-subscriber-config";
    public static final String APP_UID_RLS_SERVICES = "rls-services";
    public static final String APP_UID_CORP_RESOURCE_LIST = "kn-corp-resource-lists";
    public static final String APP_UID_CORP_GROUP = "kn-corp-groups";
    public static final String CORP_GROUP_URI = "kodiak-poc/kn-corp-groups/users/tel:+";
    public static final String APP_UID_PUB_TGSC = "kn-tgsc-list";
    public static final String TELURI = "tel:+";
    public static final String APP_UID_AUTH_LIST = "kn-authorization-list";
    public static final String APP_UID_EMERG_CONFIG = "kn-emergency-config";
    public static final String APP_UID_ADDL_TG_LIST = "org.openmobilealliance.group-usage-list";
    public static final String CLIENTSUPPORT_XCAP_ROOT_CONTEXT = "/clientsupport";
    public static final String CLIENTSUPPORT_PTTSETTING_DOC_CONTEXT = "/getPTTSettings";

    //Microservices activation Couchbase Auth service URL
    public static final String MS_CBAUTH_ROOT_CONTEXT = "/kodiakcbauth";
    public static final String PTTBUCKETURIWIFI = "PTTBUCKETURIWIFI";
    public static final String PTXBUCKETURIWIFI = "PTXBUCKETURIWIFI";
    public static final String XCAPMOBILESYNCFLAG = "XCAPMOBILESYNCFLAG";
    public static final String SYNCBUCKETINFO = "SYNCBUCKETINFO";
    public static final String SGWAUTHMETHOD = "SGWAUTHMETHOD";
    public static final String GRPMEM_LOC_SVC_ENABLED = "GRPMEM_LOC_SVC_ENABLED";
    public static final String IS_SYNCGW_INSTALLED = "IS_SYNCGW_INSTALLED";

    public static final String MCXDOMAINID = "MCXDOMAINID";
    public static final String GMSDOMAINID ="GMSDOMAINID";
    public static final String DEFAULT_FENCE_LOC_INTERVAL = "DEFAULT_FENCE_LOC_INTERVAL";
    public static final String MIN_LOC_INTERVAL = "MIN_LOC_INTERVAL";
    public static final String MAX_LOC_INTERVAL = "MAX_LOC_INTERVAL";

    public static final String DEFAULT_FENCING_DURATION = "DEFAULT_FENCING_DURATION";
    public static final String MIN_FENCING_DURATION = "MIN_FENCING_DURATION";
    public static final String MAX_FENCING_DURATION = "MAX_FENCING_DURATION";

    public static final String DEFAULT_GEOFENCE_DIST = "DEFAULT_GEOFENCE_DIST";
    public static final String MIN_GEOFENCE_DIST = "MIN_GEOFENCE_DIST";
    public static final String MAX_GEOFENCE_DIST = "MAX_GEOFENCE_DIST";

    public static final String JAVA_TRUST_STORE_PATH = "/lib/security/cacerts";
    public static final String JAVA_HOME = "java.home";
    public static final String JAVA_TRUST_STORE_PASSWORD = "changeit";

    public static final String DEFAULT_NOTIFY_THRSHOLD_COUNT = "DEFAULT_NOTIFY_THRSHOLD_COUNT";
    public static final String MIN_NOTIFY_THRSHOLD_COUNT = "MIN_NOTIFY_THRSHOLD_COUNT";
    public static final String MAX_NOTIFY_THRSHOLD_COUNT = "MAX_NOTIFY_THRSHOLD_COUNT";
    public static final String XDM_MAX_NOTIFICATION_COUNT = "XDM_MAX_NOTIFICATION_COUNT";

    public static final String NOTIFYJOB_AUDIT_INTRVAL = "NOTIFYJOB_AUDIT_INTRVAL";

    public static final String LARGE_GROUP_AUDIT_INTRVAL = "LARGE_GROUP_AUDIT_INTRVAL";

    public static final String XCAP_MAX_PENDING_NOTIFYQ_SIZE = "XCAP_MAX_PENDING_NOTIFYQ_SIZE";
    public static final String XCAP_NOTIFY_COUNT_PER_AUDIT_INTRVAL = "XCAP_NOTIFY_COUNT_PER_AUDIT_INTRVAL";

    public static final String XCAP_NOTIFY_SUPPRESS_MDN_BATCH = "XCAP_NOTIFY_SUPPRESS_MDN_BATCH";

    public static final String DEFAULT_XDM_MAX_NOTIFICATION_COUNT = "0";

    // Temporal workflow controls for watcher-level wait-and-bundle notifications.
    public static final String XCAP_NOTIFICATION_OPTIMIZED = "XCAP_NOTIFICATION_OPTIMIZED";
    public static final String XCAP_NOTIFICATION_PERIOD = "XCAP_NOTIFICATION_PERIOD";
    public static final String XCAP_DIFF_PAYLOAD_SIZE = "XCAP_DIFF_PAYLOAD_SIZE";

    public static final Map<String,Object> dynamicQueueArgs =  new HashMap<>(){{put("x-queue-type", "classic");}};

    public static final Map<String,Object> staticQueueArgs =  new HashMap<>(){{put("x-queue-type", "quorum");}};

    public static final String DEFAULT_FENCE_NOTIFY_INTERVAL = "DEFAULT_FENCE_NOTIFY_INTERVAL";
    public static final String MIN_FENCE_NOTIFY_INTERVAL = "MIN_FENCE_NOTIFY_INTERVAL";
    public static final String MAX_FENCE_NOTIFY_INTERVAL = "MAX_FENCE_NOTIFY_INTERVAL";

    public static final String FREQUENT_LOC_UPDATE_ONCALL_ENABLED = "FREQUENT_LOC_UPDATE_ONCALL_ENABLED";
    public static final String FREQUENT_LOC_UPDATE_INTERVAL = "FREQUENT_LOC_UPDATE_INTERVAL";

    public static final String XCAP_INDEX = "index";
    public static final String XCAP_DEFAULT_CONTACTLIST_NAME = "oma_pocbuddylist";
    public static final String XCAP_DEFAULT_CORP_CONTACTLIST_DISP_NAME = "Corporate Contacts";
    public static final String RLS_PACKAGE_NAME_PRESENCE = "presence";

    public static final String IDM_SERVICE_FLAG = "IDM_SERVICE_FLAG";
    public static final String MESSAGE_STORE_API = "/MessageStoreAPI";

    public static final String RECORDING_STATUS = "RECORDING_STATUS";
    public static final String COLLABORATION = "TIER1";
    public static final String COMMAND = "TIER2";
    public static final String MUTLISITE_DEPLOYMENT_FLAG = "MULTISITE_DEPLOYMENT_FLAG";
    public static final String CROSSSITE_TOPIC_EXCHANGE = "CrossSiteTopicExchange";

    public static final String K8_CLIENT_PUBLIC_CERT_PATH_WITH_FILE = "/mnt/dump/tls_certs/service.crt";
    public static final String K8_CLIENT_PRIVATE_CERT_PATH_WITH_FILE = "/mnt/dump/tls_certs/service.key";
    public static final String DC_CLIENT_PUBLIC_CERT_PATH_WITH_FILE = "/etc/vault.d/tls_certs/vault.kodiakptt.com.crt";
    public static final String DC_CLIENT_PRIVATE_CERT_PATH_WITH_FILE = "/etc/vault.d/tls_certs/vault.kodiakptt.com.key";

    // RabbitMQ mTLS certificate and key paths
    public static final String RMQ_MTLS_CA_CERT_PATH_DC = "/etc/pki/tls/certs/ca-bundle.crt";
    public static final String RMQ_MTLS_CA_CERT_PATH_K8 = "/mnt/dump/tls_certs/ca.crt";
    public static final String RMQ_MTLS_TLS_PROTOCOL = "TLS";
    public static final String RMQ_CLIENT_MTLS ="RMQ_CLIENT_MTLS";
    public static final String FLAG_ENABLED = "1";
    public static final String RMQ_SSL_PORT="RMQ_SSL_PORT";
    public static final String DELIM = ",";
    public static final String APPS_CBS_CLUSTER_STATUS_LOCAL  = "APPS_CBS_CLUSTER_STATUS_LOCAL";
    public static final String APPS_CBS_CLUSTER_STATUS_REMOTE  = "APPS_CBS_CLUSTER_STATUS_REMOTE";
    public static final int TTL = 30000; // 30 sec
    public static final int MESSAGE_TTL = 25000; // 25 sec

    //Interface types
    public static final int CLIENT_TYPE_XCAP = 1;
    public static final int CLIENT_TYPE_SOAP = 2;
    public static final int CLIENT_TYPE_WCSR = 3;
    public static final int CLIENT_TYPE_CAT_UI = 4;
    public static final int CLIENT_TYPE_PAM_SUBS_WCSR = 5;
    public static final int CLIENT_TYPE_REST = 6;
    public static final int PTX_XDMDATA_INTF = 7;
    public static final int DYNAMIC_CGMT_INTF = 8;
    public static final int AREA_BASED_DYNAMIC_GROUP = 9;
    public static final int MCX_GROUP_INDICATOR = 1;
    public static final int LARGE_GROUP_INDICATOR = 1;
    public static final int MEMBER_LIST_COUNT_FOR_LARGEGROUP = 250;

    public static final int CONTACT_LIST_TYPE = 1;
    public static final int GROUP_TYPE = 1;
    public static final String FAN_TYPE = "2";
    public static final String OWNER_FAN_TYPE = "2";
    public static final String OWNER_BAN_TYPE = "1";
    public static final String MASTER_LIST_VERSION_ID_1 = "1";

    public static final int OPERATION_ID_ADD_CONTACTS = 200;
    public static final int OPERATION_ID_MODIFY_CONTACTS = 201;
    public static final int OPERATION_ID_DELETE_CONTACTS = 203;
    public static final int OPERATION_ID_GET_CONTACTS_LIST_DETAILS = 204;

    //Added for Dynamic Contact Mgmt
    public static final int OPERATION_ID_MODIFY_DYNAMIC_CONTACT = 210;
    public static final int OPERATION_ID_DELETE_DYNAMIC_CONTACT = 211;
    public static final int OPERATION_ID_GET_DYNAMIC_CONTACT = 212;

    public static final int OPERATION_ID_GET_DYNAMIC_NON_SHARED_GROUP_DETAILS = 235;
    public static final int OPERATION_ID_GET_DYNAMIC_NON_SHARED_GROUP_LIST = 234;
    public static final int OPERATION_ID_DELETE_DYNAMIC_NON_SHARED_GROUP = 233;
    public static final int OPERATION_ID_CREATE_DYNAMIC_NON_SHARED_GROUP = 231;
    public static final int OPERATION_ID_MODIFY_DYNAMIC_NON_SHARED_GROUP = 232;


    public static final int OPERATION_ID_CREATE_GROUP = 220;
    public static final int OPERATION_ID_MODIFY_GROUP = 221;
    public static final int OPERATION_ID_MODIFY_GROUP_NAME = 222;
    public static final int OPERATION_ID_MODIFY_GROUP_MEMBER = 223;
    public static final int OPERATION_ID_ADD_GROUP_MEMBERS = 224;
    public static final int OPERATION_ID_DELETE_GROUP_MEMBERS = 225;
    public static final int OPERATION_ID_DELETE_GROUP = 226;
    public static final int OPERATION_ID_GET_GROUP_DETAILS = 227;
    public static final int OPERATION_ID_GET_GROUP_LIST = 228;

    public static final int OPERATION_ID_GET_DIRECTORY = 250;
    public static final int OPERATION_ID_GET_RLS_DOC = 251;
    public static final int OPERATION_ID_SOAP_GET_GROUP_LIST = 261;
    public static final int OPERATION_ID_SOAP_GET_CONTACTLIST = 262;
    public static final int OPERATION_ID_SOAP_GET_GROUP_DETAILS = 263;

    public static final int OPERATION_ID_GET_SUBS_CONFIG = 264;
    public static final int OPERATION_ID_UPDATE_SUBSCRIBER_NAME = 265;
    public static final int OPERATION_ID_UPDATE_CAMPED_GROUP = 269;
    public static final int OPERATION_ID_UPDATE_CLIENT_FS1 = 266;

    public static final int OPERATION_ID_GET_CORP_SUBS_CONTACTLIST = 267;
    public static final int OPERATION_ID_GET_CORP_GROUPDETAILS = 268;

    public static final int OPERATION_ID_UPDATE_SCANLIST = 270;
    public static final int OPERATION_ID_GET_SCANLIST = 271;
    public static final int OPERATION_ID_DELETE_SCANLIST = 272;

    public static final int OPERATION_ID_UPDATE_AUTHORIZATIONLIST  = 273;
    public static final int OPERATION_ID_GET_AUTHORIZATIONLIST = 274;

    public static final int OPERATION_ID_GET_EMERGENCY_CONFIGDOC = 275;

    public static final int OPERATION_ID_GET_GROUP_USAGE_LIST_DOC = 276;

    public static final int OPERATION_ID_GET_MASTERLIST = 1;
    public static final int OPERATION_ID_MODIFY_SUBSCONTACTS = 2;
    public static final int OPERATION_ID_CREATE_CORPGROUP = 3;

    //MCS XCAP MCData

    public static final int OPERATION_ID_GET_MCDATA_UE_CONFIG= 504;
    public static final int OPERATION_ID_GET_MCDATA_USER_PROFILE = 505;
    public static final int OPERATION_ID_GET_MCDATA_SERVICE_CONFIG= 506;

    //MCS XCAP MCVideo
    public static final int OPERATION_ID_GET_MCVIDEO_UE_CONFIG= 507;
    public static final int OPERATION_ID_GET_MCVIDEO_USER_PROFILE = 508;
    public static final int OPERATION_ID_GET_MCVIDEO_SERVICE_CONFIG= 509;

    public static final int OPERATION_ID_GET_MCS_GROUP_DOC= 510;
    public static final int OPERATION_ID_GET_MCS_USER_DIR = 511;

    //Corporate Management Operation IDs//
    //Generic//
    public static final int OPERATION_ID_AUTHENTICATE = 300;
    public static final int OPERATION_ID_GETCORP_HIERARCHY = 323;
    public static final int OPERATION_ID_GET_CORP_SHARED_TRUST_MATRIX= 401;
    public static final int OPERATION_ID_UPDATE_CORP_TRUST_MATRIX    = 402;
    public static final int OPERATION_ID_DELETE_CORP_TRUST_MATRIX    = 403;


    //Contact Management//
    public static final int OPERATION_ID_GET_CORP_MASTERLIST = 301;
    public static final int OPERATION_ID_GET_SUBS_CONTACTLIST = 302;
    public static final int OPERATION_ID_MODIFYSUBSC_CONTACTS = 303;
    public static final int OPERATION_ID_PUSH_SUBLIST = 304;
    public static final int OPERATION_ID_REMOVE_SUBLIST = 305;
    public static final int OPERATION_ID_ADD_CORPCONTACT = 306;
    public static final int OPERATION_ID_MODIFY_CORPCONTACT = 307;
    public static final int OPERATION_ID_REMOVE_CORPCONTACT = 308;
    public static final int OPERATION_ID_GET_CORPCONTACTDETAILS = 309;
    public static final int OPERATION_ID_GET_SUBSCR_REVERSE_CONTACTS = 339;
    public static final int OPERATION_ID_GET_SUBSCR_SUBLISTS = 340;
    public static final int OPERATION_ID_REMOVE_SUBSCONTACTS = 342;


    //Sublist Management//
    public static final int OPERATION_ID_CREATE_SUBLIST = 310;
    public static final int OPERATION_ID_MODIFY_SUBLIST = 311;
    public static final int OPERATION_ID_DELETE_SUBLIST = 312;
    public static final int OPERATION_ID_GET_SUBLIST_DETAILS = 313;
    public static final int OPERATION_ID_GET_ALL_SUBLIST = 314;
    public static final int OPERATION_ID_GET_DISTRIBUTION_LIST = 315;
    public static final int OPERATION_ID_REMOVE_SUBSFROM_ALLSUBLIST = 343;

    //Group Management//
    public static final int OPERATION_ID_CREATE_CORP_GROUP = 316;
    public static final int OPERATION_ID_MODIFY_CORPGROUP = 317;
    public static final int OPERATION_ID_DELETE_CORPGROUP = 318;
    public static final int OPERATION_ID_GET_GROUPDETAILS = 319;
    public static final int OPERATION_ID_GET_GROUPLIST = 320;
    public static final int OPERATION_ID_GET_GROUP_DETAILS_LIST = 321;
    public static final int OPERATION_ID_GET_SUBSCRIBERS_GRPLIST = 322;
    public static final int OPERATION_ID_GET_POC_LINKED_GROUP_LIST = 341;
    public static final int OPERATION_ID_REMOVE_SUB_FROM_ALLGROUPS = 344;




    // Corporate Activation Mgmt//
    public static final int OPERATION_ID_GET_SUBS_EMAILID = 324;
    public static final int OPERATION_ID_SAVE_ACTIVATION_CODE = 325;

    // Corporate Custom API 7.2 //
    public static final int OPERATION_ID_CORP_CUSTOM = 326;

    public static final int OPERATION_ID_GENERATE_ACTIVATION_CODES = 327;
    public static final int OPERATION_ID_GET_MAIL_INFO = 328;
    public static final int OPERATION_ID_SEND_ACTIVATION_MAIL = 329;
    public static final int OPERATION_ID_ASSIGN_UNASSIGN_CAMPED_GROUP = 330;
    public static final int OPERATION_ID_MODIFY_SUBSC_FEATURE_BIT = 331;
    public static final int OPERATION_ID_MODIFY_SUBSCRIBER_SCAN_LIST = 332;
    public static final int OPERATION_ID_GET_SUBSCRIBER_SCAN_LIST = 333;
    public static final int OPERATION_ID_GET_SUBSCRIBER_FEATURE_SETS = 345;
    public static final int OPERATION_ID_UPDATE_SUBSCRIBER_CORPADMINFS = 346;
    //Corp Subs Management
    public static final int OPERATION_ID_GET_ACTIVATION_CODE = 347;
    public static final int OPERATION_ID_GET_CORP_SUBSCRIBER_DETAILS = 348;
    public static final int OPERATION_ID_UPDATE_CORP_SUBSCRIBER = 349;
    public static final int OPERATION_ID_SWITCH_SUBSCRIBER_CLIENT_PROFILE = 350;
    //CorpSoap: Operation ID 391 - 399: Reserved
    public static final int OPERATION_ID_GET_CORP_SUBS_USER_PROFILE = 391;
    public static final int OPERATION_ID_RESET_CORP_SUBS_USER_PASSOWRD = 392;
    public static final int OPERATION_ID_RESEND_CORP_SUBS_VERIFICATION_EMAIL = 393;
    public static final int OPERATION_ID_UPDATE_SUBS_ALIAS_ENTITIES = 394;
    public static final int OPERATION_ID_GENERATE_TEMP_PASSWORD = 395;
    public static final int OPERATION_ID_MODIFY_SUBSCRIBER_TG_LIST = 396;
    public static final int OPERATION_ID_GET_SUBSCRIBER_TG_LIST = 397;
    public static final int OPERATION_ID_DELETE_SUBSCRIBER_TG_LIST = 398;
    public static final int OPERATION_ID_SEND_TEMP_PASSWORD = 399;

    public static final int OPERATION_ID_ADD_BULK_GROUPS = 601;
    public static final int OPERATION_ID_CREATE_TALK_SCAN_LIST = 602;

    public static final int OPERATION_ID_CREATE_OSIM_LIST = 603;
    public static final int OPERATION_ID_UPDATE_OSM_LIST = 604;
    public static final int OPERATION_ID_DELETE_OSM_LIST = 605;
    public static final int OPERATION_ID_GET_OSM_LIST = 606;
    public static final int OPERATION_ID_GET_OSM_LIST_DETAILS = 607;
    public static final int OPERATION_ASSIGN_OSMLISTID_TO_GROUP = 608;
    public static final int OPERATION_ID_GET_OSM_GROUP_LIST = 609;

    public static final int OPERATION_ID_UPDATE_CORP_SUBSCRIBER_MCS_IDS = 610;

    //userProfile
    public static final int OPERATION_ID_CREATE_USER_PROFILE = 611;
    public static final int OPERATION_ID_UPDATE_USER_PROFILE = 612;
    public static final int OPERATION_ID_DELETE_USER_PROFILE = 613;
    public static final int OPERATION_ID_GET_USER_PROFILE_DETAILS = 614;
    public static final int OPERATION_ID_GET_USER_PROFILE_LIST = 615;
    public static final int OPERATION_ID_GET_USER_PROFILE_LIST_BY_NAME = 616;
    public static final int OPERATION_ID_GET_SUBSCRIBER_USER_PROFILE_LIST = 617;
    public static final int OPERATION_ID_ASSIGN_USER_PROFILE = 618;
    public static final int OPERATION_ID_UNASSIGN_USER_PROFILE = 619;
    public static final int OPERATION_ID_GET_USER_PROFILE_SUBSCRIBERLIST = 620;
    public static final int OPERATION_ID_UPDATE_DEFAULT_USERPROFILE = 621;
    public static final int OPERATION_ID_GET_ASYNC_OP_STATUS=633;

    //Group Profile Management
    public static final int OPERATION_ID_CREATE_GROUP_PROFILE = 622;
    public static final int OPERATION_ID_CREATE_BULK_GROUP_WITH_PROFILE = 623;
    public static final int OPERATION_ID_GET_GROUP_PROFILE_LIST = 624;
    public static final int OPERATION_ID_GET_GROUP_PROFILE_DETAILS = 625;
    public static final int OPERATION_ID_GET_GROUP_LIST_FOR_GROUP_PROFILE = 626;
    public static final int OPERATION_ID_SEARCH_GROUP_PROFILE = 627;
    public static final int OPERATION_ID_CREATE_CORP_GROUP_WITH_PROFILE = 628;
    public static final int OPERATION_ID_MODIFY_GROUP_PROFILE=629;
    public static final int OPERATION_ID_DELETE_BULK_GROUP_WITH_PROFILE = 630;
    public static final int OPERATION_ID_DELETE_GROUP_PROFILE = 631;

    // Trk mail
    public static final int OPERATION_ID_SEND_TRK_MATERIAL = 632;

    //Device management
    public static final int OPERATION_ID_GET_DEVICELIST= 634;
    public static final int OPERATION_ID_GET_DEVICESTATS= 636;
    public static final int OPERATION_ID_GET_CORPORATE_FS= 637;

    public static final int OPERATION_ID_CLONE_CONTACT_GROUPS_AND_FEATURES = 640;
    public static final int OPERATION_ID_UPDATE_CORP_ADMIN_FS = 515;
    public static final int OPERATION_ID_MODIFY_CORPGROUP_PROPERTIES= 638;


    public static final int OPERATION_ID_GET_GROUPSTATS= 513;
    public static final int OPERATION_ID_SEND_MAIL = 514;

    //Statistics related to corp
    public static final int OPERATION_ID_GET_SUBSCRIBERS_STATS_OF_CORP = 635;

    // MCPTT:
    public static final int OPERATION_ID_SET_TARGET_USER_PERMISSIONS = 361;
    public static final int OPERATION_ID_GET_TARGET_USER_PERMISSIONS = 362;
    public static final int OPERATION_ID_GET_AUTHORIZED_MDN_LIST = 363;
    public static final int OPERATION_ID_SET_EMERGENCY_ATTRIBUTES = 364;
    public static final int OPERATION_ID_GET_EMERGENCY_ATTRIBUTES = 365;
    public static final int OPERATION_ID_GET_USER_EMERGENCY = 366;
    public static final int OPERATION_ID_GET_AUTHORIZED_USERLIST = 367;

    //PAMSUBSMGMT
    public static final int OPERATION_ID_GET_BILLING_NUMBERS = 334;
    public static final int OPERATION_ID_GET_LICENSE_SUBS = 335;
    public static final int OPERATION_ID_MARK_SUBS_FOR_DELETION = 336;
    public static final int OPERATION_ID_LICENSE_AUTHENTICATE = 337;
    public static final int OPERATION_ID_PAM_SUBS_CORP_CUSTOM = 338;
    public static final int OPERATION_ID_UPDATE_BILLING_NAME = 400;

    //Rest service IDM: Below operation ID for IDM is non-Changeable, if some is going to change the operation ID then need to intimate IDM Subsystem.
    public static final int OPERATION_ID_REST_GENERATE_SUBSCR_ACTIVATION_CODE = 351;
    public static final int OPERATION_ID_REST_GET_SUBSCR_ACTIVATION_CODE = 352;
    public static final int OPERATION_ID_REST_GENERATE_OTP = 353;
    public static final int OPERATION_ID_REST_VALIDATE_OTP = 354;
    public static final int OPERATION_ID_REST_GET_CORP_HIERARCHY = 355;
    public static final int OPERATION_ID_REST_GET_USER_DETAILS = 356;
    public static final int OPERATION_ID_REST_SEND_SMS = 357;

    //Rest Subs Management : Operation ID 371 - 390: Reserved
    public static final int OPERATION_ID_GET_CORP_EXTERNAL_CONTACT_DETAILS = 371;
    public static final int OPERATION_ID_GET_CORPORATE_PROFILE = 372;
    public static final int OPERATION_ID_GET_LI_TARGET_INFO = 373;
    public static final int OPERATION_ID_GET_CORPPROFILE_BY_ENTITIES = 374;
    public static final int OPERATION_ID_GET_POC_CONFIG = 375;
    public static final int OPERATION_ID_GET_MOBILE_SYNC_LOC_SUPERVISOR = 376;
    public static final int OPERATION_ID_GET_PROFILEID_MDN_MAP_FOR_MCPTTID = 377;
    public static final int OPERATION_ID_GET_SUBS_GROUP_MEMBERSHIP_DETAILS = 378;
    public static final int OPERATION_ID_GET_USERPROFILEIDS_BY_PROFILEMDNS = 379;

    public static final int OPERATION_ID_SET_SUBSCR_CLIENT_SETTINGS = 381;
    public static final int OPERATION_ID_GET_SUBSCR_CLIENT_SETTINGS = 382;
    public static final int OPERATION_ID_MODIFY_GROUP_UGW_CONFIG = 383;
    public static final int OPERATION_ID_GET_GROUPS_UGW_CONFIG = 384;
    public static final int OPERATION_ID_ASSIGN_COMMON_CONTACT_LIST_TO_SUBSCRIBERS = 385;
    public static final int OPERATION_ID_UNASSIGN_COMMON_CONTACT_LIST_TO_SUBSCRIBERS = 386;
    public static final int OPERATION_ID_GET_DEVICEINFO = 387;
    public static final int ACTION_GET_MASTERLIST = 1;
    public static final int ACTION_GET_CORP_HIERARCHY = 5;
    public static final int ACTION_PUSH_SUBLIST = 2;
    public static final int ACTION_GET_ALL_SUBLIST = 3;
    public static final int ACTION_GET_GROUP_LIST = 4;
    public static final int ACTION_GET_BILLINGNUMBERS = 6;
    public static final int ACTION_GET_USERPROFILE_SUBSCRIBERLIST = 10;
    public static final int SEND_ACCOUNT_MAIL = 1;

    public static final int OPERATION_ID_GET_MDN_AUTHORIZATION_FOR_GROUP = 512;

    public static final int OPERATION_ID_GET_GROUPS_DETAILS_WITHOUT_MEMBERS = 639;

    //Hierarchy
    public static final int OPERATION_ID_CREATE_HIRARCHY= 643;
    public static final int OPERATION_ID_DELETE_HIERARCHY= 644;
    public static final int OPERATION_ID_MODIFY_HIRARCHY= 645;
    public static final int OPERATION_ID_ALLOCATE_SUBSCRIBER = 646;
    public static final int OPERATION_ID_UNALLOCATE_SUBSCRIBER = 647;
    public static final int OPERATION_ID_GROUP_REHOME = 648;
    public static final int OPERATION_ID_GET_REGIONS = 649;

    //Corporate Management Operation IDs//
    //DATA SYNC operation Id
    public static final int OPERATION_ID_POC_DATASYNC = 990;
    public static final int OPERATION_ID_POC_CAPEXCHANGE = 991;

    // HealthMgr operation Id
    public static final int OPERATION_ID_HEALTH_PING = 999;

    public static final int OPERATION_ID_SEARCH_CORP_ADD_BOOK=280;

    // PTT Setting Template operation Id
    public static final int OPERATION_ID_CREATE_PTT_SETTING_TEMPLATE = 700;
    public static final int OPERATION_ID_DELETE_PTT_SETTING_TEMPLATE = 701;
    public static final int OPERATION_ID_GET_PTT_SETTING_TEMPLATE_LIST = 702;
    public static final int OPERATION_ID_GET_PTT_SETTING_DOC = 703;
    public static final int OPERATION_ID_ASSIGN_PTT_SETTING_TO_HIERARCHY = 704;
    public static final int OPERATION_ID_UNASSIGN_PTT_SETTING_TO_HIERARCHY = 705;
    public static final int OPERATION_ID_ASSIGN_PTT_SETTING_TO_MDN_LIST = 706;
    public static final int OPERATION_ID_GET_MDN_LIST_FOR_PTT_SETTINGID = 707;
    public static final int OPERATION_ID_SET_DEFAULT_PTT_SETTING_DOC = 708;
    public static final int OPERATION_ID_UNASSIGN_PTT_SETTING_TO_MDN_LIST = 709;
    public static final int OPERATION_ID_GET_MDN_COUNT_FOR_PTT_SETTING_DOC = 710;
    public static final int OPERATION_ID_ASSIGN_PTT_SETTING_TO_CORP = 711;
    public static final int OPERATION_ID_UNASSIGN_PTT_SETTING_TO_CORP = 712;
    public static final int OPERATION_ID_MODIFY_PTT_SETTING_TEMPLATE = 713;


    //Group member count in DG.CORPGROUPMEMBERLIST
    public static final int GROUP_MEMBER_COUNT = 0;
    public static final int USER_ID_COUNT = 0;
    public static final int USER_ID_COUNT_ONE = 1;

    // Constants for custom parameter keys
    public static final String INCONTEXTID = "INCONTEXTID";
    public static final String IDTYPE = "IDTYPE";
    public static final String IDLIST = "IDLIST";
    public static final String INCONTEXTIDINFO = "INCONTEXTIDINFO";
    public static final String MASTERLISTVERSIONID = "MASTERLISTVERSIONID";
    public static final String IDDETAILSLIST = "IDDETAILSLIST";
    public static final String INCONTEXTIDLIST = "INCONTEXTIDLIST";
    public static final String PERSISTER_TXN = "PERSISTER_TXN";
    public static final String LINKED_CORPORATE_ETAG = "LINKED_CORPORATE_ETAG";
    public static final String PTT_SERVER_ID = "PTT_SERVER_ID";
    public static final String PROFILE_MDN = "PROFILE_MDN";
    //Added some constants to pass data to the custom layer since query of fetching sublist members and the external was kind of repeated.
    public static final String SUBLIST_MEMBERS = "SUBLIST_MEMBERS";
    public static final String ETXERNAL_MEMBERS = "ETXERNAL_MEMBERS";
    public static final String SUBLIST_ID = "SUBLIST_ID";
    public static final String CURRENT_MASTER_LIST_ETAG = "CURRENTMASTERLISTETAG";
    public static final String ACTION = "ACTION";
    public static final String CUSTOM_CORP_USER_HOOK = "CustomCorpUserHook";
    public static final String CUSTOM_NNI_USER_HOOK = "CustomNniCapUserHook";
    public static final String MDN_LIST = "MDNLIST";
    public static final String GROUP_MEMBERS = "GROUPMEMBERS";
    public static final String ADD_GROUP_LIST = "ADD_GROUP_LIST";
    public static final String MODIFY_GROUP_LIST = "MODIFY_GROUP_LIST";
    public static final String REMOVE_GROUP_LIST = "REMOVE_GROUP_LIST";
    public static final String OWNER_FANLIST = "OWNER_FANLIST";
    public static final String SUBSCRIBER_MDN = "SUBSCRIBER_MDN";
    public static final String SUBSCR_FIRST_NAME = "SUBSCR_FIRST_NAME";
    public static final String SUBSCR_LAST_NAME = "SUBSCR_LAST_NAME";
    public static final String INSTALLCUSTOMFLAG = "INSTALLCUSTOMFLAG";
    public static final String CUSTOM_EMAIL__HOOK = "CustomCorpEMailHook";
    public static final String OLD_BAN_TYPE = "oldBanType";
    public static final String NEW_BAN_TYPE = "newBanType";
    public static final String OLD_EXT_BAN_ID = "oldExtBanId";
    public static final String NEW_EXT_BAN_ID = "newExtBanId";
    public static final String CORP_CLEAN_UP_REQ = "CORP_CLEAN_UP_REQ";
    public static final String MAX_SUBS_ALLOWED_GEN_ACTV_REQ = "MAX_SUBS_ALLOWED_GEN_ACTV_REQ";
    public static final String DELETE_TGS_FOR_SUBSCRIBER = "SUBSCRIBER";
    public static final String DELETE_TGS_FOR_ALL_CONFIG_CHANGE = "ALL";
    public static final String LINE_SAPERATOR="_";
    public static final String COMMA = ",";
    public static final String NULL = "null";
    public static final String COLON = ":";
    public static final String SEMICOLON = ";";
    public static final String EQUALS = "=";
    public static final String DATFORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
    public static final String PROVFS_BITS_POS = "PROVFS_BITS_POS";
    public static final String PROVFS_BITS_VAL = "PROVFS_BITS_VAL";

    public static final String PROV_FEATURE_ID = "FEATURE_ID";
    public static final String PROV_FEATUREBIT_ID = "featureId";

    public static final String FEATURE_ENABLED = "FEATURE_ENABLED";
    public static final String PROV_FS_ENABLE_ID = "enabled";

    //boolean flags
    public static final boolean TRUE = true;
    public static final boolean FALSE = false;

    public static enum ZONE_TYPE {
        SINGLE_ZONE(1);

        private final int value;

        ZONE_TYPE(int value){
            this.value = value;
        }
        public int value(){return value;}
    }
    public static enum TG_GROUP_TYPE {
        DISPATCHER_GROUP("1"),
        STD_GROUP("0"),
        BCG_GROUP("2");

        private final String value;

        TG_GROUP_TYPE(String value) {
            this.value = value;
        }
        public String value() {
            return value;
        }
    }

    public static enum RESPONSE_STATUS {
        SUCCESS(0), FAILURE(1), PARTIAL_SUCCESS(2);

        int responseStatus;

        RESPONSE_STATUS(int responseStatus) {
            this.responseStatus = responseStatus;
        }

        public int value() {
            return responseStatus;
        }

    }

    //start of Provisioning Operation ID's
    public static final int OP_ID_CREATE_SUBS = 100;
    public static final int OP_ID_UPDATE_SUBS = 101;
    public static final int OP_ID_DELETE_SUBS = 102;
    public static final int OP_ID_GET_SUBS_PROFILE = 103;
    public static final int OP_ID_CHANGE_AUTH_STATUS = 104;
    public static final int OP_ID_CHANGE_MDN = 105;
    public static final int OP_ID_FORCE_SYNC = 106;
    public static final int OP_ID_ACTIVATE_SUBS = 107;
    public static final int OP_ID_GET_SUBS_CONFIG = 108;
    public static final int OP_ID_UPDATE_SUBS_NAME = 109;
    public static final int OP_ID_GET_DEFAULT_SUBS_PROFILE = 110;
    public static final int OP_ID_UPDATE_CLIENT_FS1 = 111;
    public static final int OP_ID_CREATE_PAM_ACCOUNT = 112;
    public static final int OP_ID_UPDATE_PAM_ACCOUNT = 113;
    public static final int OP_ID_DELETE_PAM_ACCOUNT = 114;
    public static final int OP_ID_CHANGE_AUTH_STATUS_PAM_ACC = 115;
    public static final int OP_ID_UPDATE_USERAGENT = 116;
    public static final int OP_ID_UPGRADE_PAM_ACCOUNT= 117;
    public static final int OP_ID_UPDATE_PAM_ACCOUNT_PROFILE = 118;
    public static final int OP_ID_GET_PAM_ACCOUNT_DETAILS = 119;
    public static final int OP_ID_DOWNGRADE_RATE_PLAN_PAM_ACCOUNT = 120;
    public static final int OP_ID_CHANGE_BILLING_NUM_PAM_ACCOUNT = 122;
    public static final int OP_ID_GET_LICENSE_PK_PROFILE = 123;
    public static final int OP_ID_DELETE_EXT_SUBS = 121;
    public static final int OP_ID_CUSTOM_PROV = 180;
    public static final int OP_ID_CUSTOM_UPDATE_BAN =125;
    public static final int OP_ID_UPDATE_AUTO_PAIRING = 126;
    public static final int OP_ID_GETSYSCONFIG = 128;
    public static final int OP_ID_DEVICE_ACTIVATION = 129;
    public static final int OP_ID_USER_LOGIN = 130;
    public static final int OP_ID_UPGRADE_OR_DOWNGRADE_LICENSE_PK = 131;
    public static final int OP_ID_GET_EMERGENCY_DETAILS = 132;
    public static final int OP_ID_UPDATE_MCSIDS = 133;
    public static final int OP_ID_UPDATE_USERID = 134;
    public static final int OP_ID_UPDATE_LICENSE_PK_MCSIDS = 135;
    public static final int OP_ID_UPDATE_LICENSE_PK_USERID = 136;
    public static final int SELECT_PROFILE_MDN_OPID = 137;
	
	public static final int OP_ID_CREATE_DEVICE = 138;
    public static final int OP_ID_GETDEVICE = 139;
    public static final int OP_ID_DELETEDEVICE = 140;
    public static final int OP_ID_RADIO_DEVICE_ACTIVATION = 141;
    public static final int OP_ID_MODIFY_DEVICE = 142;

    public static final int OP_ID_CREATE_CORP_ACCOUNT = 144;
    public static final int OP_ID_GET_CORPORATE_ACCOUNT_LIST = 145;
    public static final int OP_ID_GET_CORPORATE_ACC_DETAILS = 146;
    public static final int OP_ID_DELETE_CORP_ACCOUNT = 147;
    public static final int OP_ID_UPDATE_CORP_ACCOUNT =148;
    public static final int OP_ID_GET_EXTGW_PROFILE_LIST =149;
    public static final int OP_ID_CREATE_BULK_SUBSCRIBER =150;
    public static final int OP_ID_UPDATE_BULK_SUBSCRIBER =151;
    public static final int OP_ID_DELETE_BULK_SUBSCRIBER =152;
    public static final int OP_ID_UPDATE_BULK_AUTH_STATUS =153;

    //TP user operations IDs
    public static final int OP_ID_CREATE_TP_USER = 501;
    public static final int OP_ID_UPDATE_TP_USER = 502;
    public static final int OP_ID_DELETE_TP_USER = 503;
    public static final int OP_ID_GEN_ACTIVATION_CODE = 504;
    public static final int OP_ID_UPDATE_CAT_PER_SET = 642;

    public static final int CLIENT_INTF_CAT = 1;
    public static final int CLIENT_INTF_REST = 2;

    //end of Provisioning Operation ID's

    public static final String PROTOCOL_VERSION_TILL_8 = "2|3|5|6|7|8";
    public static final String PROTOCOL_VERSION_REGEX = "2|3|5|6|7|8|9|10|11|12|13|14|15|16|17|18|19|20|21|22|23|24|25|26|27|28|29";
    public static final String PROTOCOL_VERSION_2_0 = "2";
    public static final String PROTOCOL_VERSION_3_0 = "3";
    public static final String PROTOCOL_VERSION_5_0 = "5|6|7|8|9|10|11|12|13|14|15|16|17|18|19|20|21|22|23|24|25|26|27|28|29";
    public static final String PROTOCOL_VERSION_6_0 = "6|7|8|9|10|11|12|13|14|15|16|17|18|19|20|21|22|23|24|25|26|27|28|29";
    public static final String PROTOCOL_VERSION_7_0 = "7|8|9|10|11|12|13|14|15|16|17|18|19|20|21|22|23|24|25|26|27|28|29";
    public static final String PROTOCOL_VERSION_8_0 = "8|9|10|11|12|13|14|15|16|17|18|19|20|21|22|23|24|25|26|27|28|29";
    public static final String PROTOCOL_VERSION_REGEX1 = "2|3|5";
    public static final String PROTOCOL_VERSION_9_0 = "9|10|11|12|13|14|15|16|17|18|19|20|21|22|23|24|25|26|27|28|29";
    public static final String PROTOCOL_VERSION_10_0 = "10|11|12|13|14|15|16|17|18|19|20|21|22|23|24|25|26|27|28|29";
    public static final String PROTOCOL_VERSION_11_0 = "11|12|13|14|15|16|17|18|19|20|21|22|23|24|25|26|27|28|29";
    public static final String PROTOCOL_VERSION_12_0 = "12|13|14|15|16|17|18|19|20|21|22|23|24|25|26|27|28|29";
    public static final String PROTOCOL_VERSION_13_0 = "13|14|15|16|17|18|19|20|21|22|23|24|25|26|27|28|29";
    public static final String PROTOCOL_VERSION_14_0 = "14|15|16|17|18|19|20|21|22|23|24|25|26|27|28|29";
    public static final String PROTOCOL_VERSION_15_0 = "15|16|17|18|19|20|21|22|23|24|25|26|27|28|29";
    public static final String PROTOCOL_VERSION_16_0 = "16|17|18|19|20|21|22|23|24|25|26|27|28|29";
    public static final String PROTOCOL_VERSION_17_0 = "17|18|19|20|21|22|23|24|25|26|27|28|29";
    public static final String PROTOCOL_VERSION_18_0 = "18|19|20|21|22|23|24|25|26|27|28|29";
    public static final String PROTOCOL_VERSION_19_0 = "19|20|21|22|23|24|25|26|27|28|29";
    public static final String PROTOCOL_VERSION_20_0 = "20|21|22|23|24|25|26|27|28|29";
    public static final String PROTOCOL_VERSION_21_0 = "21|22|23|24|25|26|27|28|29";
    public static final String PROTOCOL_VERSION_22_0 = "22|23|24|25|26|27|28|29";
    public static final String PROTOCOL_VERSION_23_0 = "23|24|25|26|27|28|29";
    public static final String PROTOCOL_VERSION_24_0 = "24|25|26|27|28|29";
    public static final String PROTOCOL_VERSION_25_0 = "25|26|27|28|29";
    public static final String PROTOCOL_VERSION_26_0 = "26|27|28|29";
    public static final String PROTOCOL_VERSION_27_0 = "27|28|29";
    public static final String PROTOCOL_VERSION_28_0 = "28|29";
    public static final String PROTOCOL_VERSION_29_0 = "29";

    public static final int PROTOCOL_VERSION_0 = 0;
    public static final int PROTOCOL_VERSION_7 = 7;
    public static final int PROTOCOL_VERSION_9 = 9;
    public static final int PROTOCOL_VERSION_10 = 10;
    public static final int PROTOCOL_VERSION_13 = 13;
    public static final int PROTOCOL_VERSION_14 = 14;
	public static final int PROTOCOL_VERSION_15 = 15;
    public static final int PROTOCOL_VERSION_16 = 16;
    public static final int PROTOCOL_VERSION_17 = 17;
    public static final int PROTOCOL_VERSION_18 = 18;
    public static final int PROTOCOL_VERSION_19 = 19;
    public static final int PROTOCOL_VERSION_20 = 20;
    public static final int PROTOCOL_VERSION_21 = 21;
    public static final int PROTOCOL_VERSION_22 = 22;
    public static final int PROTOCOL_VERSION_23 = 23;
    public static final int PROTOCOL_VERSION_24 = 24;
    public static final int PROTOCOL_VERSION_25 = 25;
    public static final int PROTOCOL_VERSION_26 = 26;
    public static final int PROTOCOL_VERSION_27 = 27;
    public static final int PROTOCOL_VERSION_28 = 28;
    public static final int PROTOCOL_VERSION_29 = 29;

    //Bits supported by each pv
    static HashMap<Integer,Integer>  clientPvBitSupportMap=new HashMap<Integer, Integer>() {{
        put(PROTOCOL_VERSION_16, 71);
        put(PROTOCOL_VERSION_17, 75);
        put(PROTOCOL_VERSION_18, 82);
        put(PROTOCOL_VERSION_19, 90);
        put(PROTOCOL_VERSION_20, 97);
        put(PROTOCOL_VERSION_21, 100);
        put(PROTOCOL_VERSION_22, 105);
        put(PROTOCOL_VERSION_23, 108);
        put(PROTOCOL_VERSION_24, 120);
        put(PROTOCOL_VERSION_25, 130);
        put(PROTOCOL_VERSION_26, 136);
        put(PROTOCOL_VERSION_27, 139);
    }};



    //Camped By values
    public static final int CAMPED_BY_ALL = -1;
    public static final int CAMPED_BY_CORPORATE_ADMIN = 1;
    public static final int CAMPED_BY_XCAP = 2;

    //updateBan
    public static final String UPDATEBAN_MDN="00000000000";
    public static final String MDN="MDN";


    //DT constants
    public static final String DT_CONFIG_FILE = "designconfig.properties";
    public static final String DT_MQ_KEY = "mock_mq";
    public static final String UNDERSCORE = "_";
    public static final String PERCENTAGE = "%";
    public static final String APOSTROPHE = "'";
    public static final String DELIMITER = "/";
    public static final String TOMCAT_HOME = "catalina.base";
    public static final String DT_REMOTE_KEY = "remote";
    public static final String EQUAL_TO = "=";
    public static final String USER_EVENT_FEDERATED_ROUTING_KEY ="prod.USERNTFTYEVENTQ1_V3.kodiakptt.com";
    public static final String GROUP_EVENT_FEDERATED_ROUTING_KEY ="prod.GRPNTFTYEVENTQ1_V3.kodiakptt.com";
    public static final String ETAG_EVENT_FEDERATED_ROUTING_KEY ="prod.MCSXCAPEtagQ1_V3.kodiakptt.com";
    public static final String ETAG_EVENT_ROUTING_KEY ="prod.MCSXCAPEtagQ1_V1.kodiakptt.com";

    public static final String POC_GW_SYNC_ENABLE = "POC_GW_SYNC";

    public static enum SUBS_CLIENT_TYPE {
        UNKNOWN(0),
        HANDSET(1),
        DESKTOP(2),
        DISPATCH_CLIENT(3),
        POC_DONOR_RADIO(4),
        POC_WIFIONLY(5),
        THIRDPARTYPOCCLIENT(6),
        ALIASMDN(7),
        GROUPMDN(8),
        MOBILEAPI(13),
        CROSSCARRIER(10),
        PDVCONNECT(11),
        NOTFOUND(-1),
        THIRDPARTYDISPATCHERCLIENT(12),
    	//LMR client type changes
        PTTRADIOHANDSETCLIENT(14),
        PTTRADIOCROSSCARRIERCLIENT(15),
        PTTRADIOWIFIONLYCLIENT(16),
        SGMDNPATCH(17),
        DATAGROUPMDN(18);



        int subsClientType;

        SUBS_CLIENT_TYPE(int type) {
            subsClientType = type;
        }

        public int value() {
            return subsClientType;
        }

        private static final Map<Integer, SUBS_CLIENT_TYPE> map = new HashMap<Integer, SUBS_CLIENT_TYPE>();

        static {
        	for (SUBS_CLIENT_TYPE element : SUBS_CLIENT_TYPE.values()) {
				map.put(element.value(), element);
			}
        }

        public static SUBS_CLIENT_TYPE getValueOf(int value) {
        	SUBS_CLIENT_TYPE client_TYPE =  map.get(value);
        	if (client_TYPE == null) {
        		client_TYPE = NOTFOUND;
        	}
        	return client_TYPE;
        }

    }


    public static enum SERVICE_AUTH_STATUS {
        PROVISIONED(0),
        UNUSED(1),
        ACTIVATED(2),
        DEACTIVATED(3),
        DELETION_IN_PROGRESS(4),
        PRE_PROVISIONED(99);

        int serviceAuthStatus;

        SERVICE_AUTH_STATUS(int status) {
            this.serviceAuthStatus = status;
        }

        public int value() {
            return serviceAuthStatus;
        }

        public static KnConstants.SERVICE_AUTH_STATUS fromValue(int value) {
            for (KnConstants.SERVICE_AUTH_STATUS status : KnConstants.SERVICE_AUTH_STATUS.values()) {
                if (status.value() == value) {
                    return status;
                }
            }
            return PROVISIONED;
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

    public static enum SUBSCRIPTION_TYPES {
        PUBLIC(0), CORPORATE(1), CORPORATE_AND_PUBLIC(2);

        int subscription_Type;

        SUBSCRIPTION_TYPES(int type) {
            this.subscription_Type = type;
        }

        public int value() {
            return subscription_Type;
        }
    }

    public static final String ACTIVE_RELEASE_DIR = System.getProperty("activeRelDir");

    public static final String AUTHSERVER_JWT_EXPIRY_TIMEOUT = "AUTHSERVER_JWT_EXPIRY_TIMEOUT";
    public static final String JWT_ISSUER = "Kodiak Auth Service";
    public static final int MILLI_SECONDS = 1000;
    public static final String JWT_SERVICE_TYPE = "Kodiak PTT Service";

    public static final String AUTH_KEY_PROPS_FILE_NAME = ACTIVE_RELEASE_DIR + File.separator + "auth-key.props";

    public static final String DB_FILE_NAME = "dbmgr.props";
    public static final String DB_FILE_PATH = ACTIVE_RELEASE_DIR + File.separator + DB_FILE_NAME;

    public static final String DBMGR_DBUSERID = "U";
    public static final String DBMGR_DBPASSWORD = "P";
    public static final String ENTT_PASSWORD = "P";
    public static final String DBMGR_IPADDRESS = "IPADDRESS";
    public static final String DBMGR_TCP_PORT = "TCP_PORT";
    public static final String DBMGR_DBDSN = "DBDSN";
    public static final String DBMGR_AUDIT_INTERVAL = "ACTIVE_IP_AUDIT_INTERVAL";
    public static final String DBMGR_DBPORT = "PORT";
    public static final String DBMGR_EMSPORT = "EMSDBPORT";
    public static final String DBMGR_PTTDBPORT = "PTTDBPORT";
    public static final int XDMS_POC_SUFFIX_LIMIT_REACHED_ALARMCODE = 17601;
    public static final int XDMMANAGEDOBJECT_CLASSTYPE = 5;
    public static final int RMQ_HEART_BEAT = 5;
    public static final int COMMON_CONTACT = 1;
    public static final String SELF_DND_FEATURE = "SELF_DND_FEATURE";
    public static final String LARGE_AGENCY_DISPATCH = "LARGE_AGENCY_DISPATCH";
    public static final String MAX_EMERGENCY_TIME = "MAX_EMERGENCY_TIME";
    public static final String MIN_EMERGENCY_TIME = "MIN_EMERGENCY_TIME";
    public static final String DEFAULT_EMERGENCY_TIMER = "DEFAULT_EMERGENCY_TIMER";
    public static final String EMERGENCY_CONF_TIMER_FEATURE = "EMERGENCY_CONF_TIMER_FEATURE";

    //external Corp Group
    public static final int EXTERNAL_CORP_GROUP = 1;
    public static final int NOT_EXTERNAL_CORP_GROUP = 0;

    //any change is SIGCARD_TYPE , pls update OM_SUBSYSIDS  in com.kodiak.frameworks.statisticalmgr.utils.KnConstants
    public static enum SIGCARD_TYPE {
        WEBCARD(20),
        XDM_DATAMGR(36),
        XDM_DATAMGR_GEO(37);

        int cardType;

        SIGCARD_TYPE(int cardType) {
            this.cardType = cardType;
        }

        public int value() {
            return this.cardType;
        }

        public static SIGCARD_TYPE validate(int serviceType) {
            for (SIGCARD_TYPE sigCardType : values()) {
                if (sigCardType.value() == serviceType) {
                    return sigCardType;
                }
            }
            return null;
        }
    }

    public static enum FEATURE_SET {
        INSTAPOC(0),
        MISSEDCALLALERT(1),
        CALLENDEDALERT(2),
        LOCATIONSUBSCRIPTION(3),
        SUPERVISORYOVERRIDE(4),
        BROADCASTGROUPSERVICE(5),
        RESTRICTAVAILABILITY(6),
        SECONDARYPRESENCE(7),
        SESSPARTTOORIG(8),
        SESSPARTTOTERM(9),
        MIDCALLADDPARTBYORIG(10),
        MIDCALLEXPELPARTBYORIG(11),
        MIDCALLADDPARTBYTERM(12),
        MIDCALLEXPELPARTBYTERM(13),
        TUSMS(14),
        XCAPDOCDIFF(15),
        POCOVERWIFI(16),
        HTTPSSUPPORT(17),
        PAGMSGSUPPORT(18),
        RELOGINMSGSUPPORT(19),
        TLKGRPSELSERVER(20),
        PRUPDATEREDUCTION(21),
        JITTERSTATS(22),
        HISTBASEDPRES(23),
        DECOUPPRESCALL(24),
        PERICLIENTLOGS(25),
        ONDEMCLIENTLOGS(26),
        ONDEMLOCATION(27),
        TLKGRPSCANCLIENT(28),
        BATTERYUSAGEOPTIMIZATION(29),
        CLIENTUISTATS(30),
        TLKGRPSELCLIENT(31),
        PUSHTOTEXT(33),
        PUSHTOMULTIMEDIA(34),
        PUSHTOLOCATION(35),
        URGENTMSGORIG(36),
        GROUPMEMLOC(38),
        GEOFENCEFEATURE(39),
        GCMPUSHNOTIFICATION(40),
        REMOTEPUSHNOTIFICATION(43),
        PTTRADIOCLIENT(44),
        XCAPCOUCHCLIENT(45),
        VOICEMSGFALLBACK(46),
        PTTRECORDINONCLIENT(47),
        MULTISIMULTANEOUSSESSION(48),
        BREADCUMB(49),
        INSTADRX(50),
        USERENABLE(51),
        INTEROPFEATURE(53),
        AMBIENTLISTENING(54),
        DISCRETELISTENING(55),
        USERCHECK(56),
        USERENABLEDISABLE(57),
        EMERGENCY(58),
        ABDG_GROUP_MEMBER(59),
        ABDG_GROUP_OWNER(60),
        MCDATA_SDS_FEATURE(64),
        MCDATA_FD_FEATURE(65),
        OPERATIONALSTATUSMESSAGING(66),
        MCVIDEOTX(67),
        MCVIDEORX(68),
        MCVIDEOGROUPRX(69),
        MCVIDEOCONFIRMEDPULL(70),
        MCVIDEOUNCONFIRMEDPULL(71),
        AFFILIATIONFEATURE(78),
        MCPTT_COMPLAIANCE_BIT(81),
        MCPTT_VIA_GW(82),
        MCDEVICE(84),
        USER_PROFILE_MGMT_BIT(76),
        WDSPATCHING(89),
        WDSRECORDING(90),
        DATA_GROUP_OP(93),
        DATA_INTER_OP(106),
        MCX_GROUP_REGROUP_FLAG_BIT(111),
        ASTRO_FREQUENCT_SELECT_BIT(118),
        ASTRO_CODED_AND_CLEAR_BIT(119),
        COMMON_CONTACT_LIST(121),
        PTT_RECORDING_FLAG(126),
        DATA_RECORDING_FLAG(127),
        VIDEO_RECORDING_FLAG(128),
        SELF_DND_PRIVILEGE(136),
        LARGE_AGENCY_DISPATCH(139),
        PTT_SETTINGS_CONTROL(141);

        int featureSet;

        FEATURE_SET(int type) {
            this.featureSet = type;
        }

        public int value() {
            return featureSet;
        }
    }

    public static enum XDMCORPFS2_SET {
        LOCATION_ENABLED(1),
        EMERGENCY_CONF_TIMER_FEATURE(2);

        int xdmCorpFS2_Bit;

        XDMCORPFS2_SET(int type) {
            this.xdmCorpFS2_Bit = type;
        }

        public int value() {
            return xdmCorpFS2_Bit;
        }
    }

    public static List<Integer> DEFAULT_ENABLED_BITS_PROFILE_MDN = Arrays.asList(FEATURE_SET.ONDEMLOCATION.value(),
            FEATURE_SET.TLKGRPSCANCLIENT.value(),
            FEATURE_SET.PUSHTOTEXT.value(),
            FEATURE_SET.PUSHTOMULTIMEDIA.value(),
            FEATURE_SET.PUSHTOLOCATION.value(),
            FEATURE_SET.GEOFENCEFEATURE.value(),
            FEATURE_SET.BREADCUMB.value(),
            FEATURE_SET.AMBIENTLISTENING.value(),
            FEATURE_SET.DISCRETELISTENING.value(),
            FEATURE_SET.USERCHECK.value(),
            FEATURE_SET.MCVIDEOTX.value(),
            FEATURE_SET.MCVIDEORX.value(),
            FEATURE_SET.MCVIDEOGROUPRX.value(),
            FEATURE_SET.MCVIDEOCONFIRMEDPULL.value(),
            FEATURE_SET.SELF_DND_PRIVILEGE.value(),
            FEATURE_SET.LARGE_AGENCY_DISPATCH.value(),
            FEATURE_SET.MCDATA_SDS_FEATURE.value(),
            FEATURE_SET.MCDATA_FD_FEATURE.value(),
            FEATURE_SET.EMERGENCY.value(),
            FEATURE_SET.OPERATIONALSTATUSMESSAGING.value());

    public static enum CACHE_TABLE_LIST {
        DIALPLANINFO("DG.DIALPLANINFO"),
        LICENSEINFO("DG.LICENSEINFO"),
        SYSTEMCONFIG_FEATURESET("DG.SYSTEMCONFIG_FEATURESET"),
        SUBSCRIBERPROFILEDEFN("DG.SUBSCRIBERPROFILEDEFN"),
        DEFAULTSUBPROFILE("DG.DEFAULTSUBPROFILE"),
        SUBS_POCSERVERMAP("DG.SUBS_POCSERVERMAP"),
        SUBS_PRSERVERMAP("DG.SUBS_PRSERVERMAP"),
        ROAMINGCLUSTERINFO("DG.ROAMINGCLUSTERINFO"),
        PRESENCESERVICECONFIG("DG.PRESENCESERVICECONFIG"),
        POC_SVC_CONFIG("DG.POC_SVC_CONFIG"),
        POCREGISTRARSRVCCONFIG("DG.POCREGISTRARSRVCCONFIG"),
        XDMS_DOCSUBPRX_CONFIG("DG.XDMS_DOCSUBPRX_CONFIG"),
        INSTAPOC_SRVC_CFG("DG.INSTAPOC_SRVC_CFG"),
        SIGNALINGCARDINFO("DG.SIGNALINGCARDINFO"),
        LOCATIONSERVICECONFIG("DG.LOCATIONSERVICECONFIG"),
        SWPKGCONFIGPARAMVALUE("DG.SWPKGCONFIGPARAMVALUE"),
        FEATUREACCESSINFO("DG.FEATUREACCESSINFO"),
        FEATUREACCESSNUMBERINFO("DG.FEATUREACCESSNUMBERINFO"),
        SIPPROXYSVCCONFIG("DG.SIPPROXYSVCCONFIG"),
        PAM_SVC_CONFIG("DG.PAM_SVC_CONFIG"),
        SUBSCRPARTITIONINGCONFIG("DG.SUBSCRPARTITIONINGCONFIG"),
        PTTSERVERIPINFO("DG.PTTSERVERIPINFO"),
        POC_SUPPORTED_DEVICES("DG.POC_SUPPORTED_DEVICES"),
        XDMS_SVC_CONFIG("DG.XDMS_SVC_CONFIG"),
        XDM_WEB_SUBSYSTEM_CONFIG("DG.XDM_WEB_SUBSYSTEM_CONFIG"),
        SOFTWAREPKGINSTALLINFO("DG.SOFTWAREPKGINSTALLINFO"),
        SIGNALINGCARDADDLINFO("DG.SIGNALINGCARDADDLINFO"),
        SC_SWPKGINSTALLINFO("DG.SC_SWPKGINSTALLINFO"),
        JOINTSIGNALINGCARDINFO("DG.JOINTSIGNALINGCARDINFO"),
        RTXENVVARIABLEINFO("DG.RTXENVVARIABLEINFO"),
        APNINFO("DG.APNINFO"),
        APNPROFILEINFO("DG.APNPROFILEINFO"),
        POC_BLACKLIST_DEVICES("DG.POC_BLACKLIST_DEVICES"),
        ACTIVATIONCODECONFIG("DG.ACTIVATIONCODE_CONFIG"),
        ROAMINGMCCMNCINFO ("DG.ROAMINGMCCMNCINFO"),
        RATEPLANINFO("DG.RATE_PLAN_INFO"),
        GENERIC_NNI_PROFILE("DG.GENERIC_NNI_PROFILE"),
        POC_NNI_SYSTEM_CAPS("DG.POC_SYSTEM_NNI_CAPS"),
        GW_SYSTEM_NNI_CAPS("DG.GATEWAY_SYSTEM_NNI_CAPS"),
        GENERIC_POC_NNI_CONFIG("DG.GENERIC_POC_NNI_CONFIG"),
        CLIENTCAPABILITYMASK_CONFIG("DG.CLIENTCAPABILITYMASK_CONFIG"),
        CLIENT_TYPE_CONFIG("DG.CLIENT_TYPE_CONFIGURATION"),
        SUPP_VOCODER_PROFILES("DG.SUPPORTEDVOCODERPROFILES"),
        MICROSVCS_COMMONCONFIG("DG.MICROSVCS_COMMONCONFIG"),
        MICROSVCS_CLUSTERINFO("DG.MICROSVCS_CLUSTERINFO"),
        MICROSVCS_SERVICECONFIG("DG.MICROSVCS_SERVICECONFIG"),
        SERVICE_FQDN_INFO("DG.SERVICE_FQDN_INFO"),
        MQ_QUEUE_INFO("DG.MQ_QUEUE_INFO"),
        INSTA_DRX_CONFIG("DG.InstaDRX_Config"),
        KQI_CONFIG("DG.KQI_CONFIG"),
        KQI_PROFILE_INFO("DG.KQI_PROFILE_INFO"),
        FEATURE_APPID_MAPINFO("DG.FEATURE_APPID_MAPINFO"),
    	PKG_APPID_MAPINFO("DG.PKG_APPID_MAPINFO"),
    	DATA_PACKAGE_INFO("DG.DATA_PACKAGE_INFO"),
    	ADDL_PROFILE_INFO("DG.ADDL_PROFILE_INFO"),
        QPP_PCRF_PROFILE("DG.QPP_PCRF_PROFILE");
        String tableName;

        CACHE_TABLE_LIST(String type) {
            this.tableName = type;
        }

        public String value() {
            return tableName;
        }
    }

    public static enum WEB_CACHE_TABLE_LIST {
        DIALPLANINFO("DG.DIALPLANINFO"),
        SIGNALINGCARDINFO("DG.SIGNALINGCARDINFO"),
        SWPKGCONFIGPARAMVALUE("DG.SWPKGCONFIGPARAMVALUE"),
        PTTSERVERIPINFO("DG.PTTSERVERIPINFO"),
        SOFTWAREPKGINSTALLINFO("DG.SOFTWAREPKGINSTALLINFO"),
        SIGNALINGCARDADDLINFO("DG.SIGNALINGCARDADDLINFO"),
        SC_SWPKGINSTALLINFO("DG.SC_SWPKGINSTALLINFO"),
        JOINTSIGNALINGCARDINFO("DG.JOINTSIGNALINGCARDINFO"),
        RTXENVVARIABLEINFO("DG.RTXENVVARIABLEINFO"),
        ATTELIGIBLECODES("DG.ATT_ELIGIBLE_CODES");

        String tableName;

        WEB_CACHE_TABLE_LIST(String type) {
            this.tableName = type;
        }

        public String value() {
            return tableName;
        }
    }

    public static final String wgpClientID = null;
    public static String activeReleasePath = File.separator + "DG" + File.separator + "activeRelease";
    public static final String[] LICENCE_FEATURES = new String[]{"UnKnown", "PAIRING_CONTACT_STRONG", "PAIRING_CONTACT_LOOSE", "CALL_LEG_INFO",
            "UPGRADE_CONFERENCE", "SILENT_MODE", "EXTENDED_GROUP_CREATION", "NETWORK_WIDENAME", "ROAMING_GATEWAY", "NAME_SYNC", "BUDDY_ALERT", "CDF",
            "PREPAID", "P2T", "P2C", "P2M", "GROUP_BASED_COMM", "REC_CALLS_ROAMING", "INIT_CALLS_ROAMING", "WIRELESS_PRIORITY_SERVICE", "ORIG_CONTROLS_FLOOR",
            "PRESENCE_ROAMING", "VOICE_BUFFER", "SUPPRESS_HOWLING_UPGRADE_P2C", "NETWORK_NAMES_IU", "P2C_TERMINATE", "SIM_CLIENT", "POC", "P2C_AUTO_ANSWER",
            "LI_COMPLIANCE", "GROUP_OPERATION", "EMLPP", "PREPAID_SUBSCRIBER_TYPE", "PRESENCE_OVER_IP", "INTERNATIONAL_GROUP_SMS", "NATIONAL_CALLING_CONTROL",
            "FLASH_SMS", "REAL_TIME_PRESENCE", "LOCAL_CHIRP_TONE", "PRESENCE_AVAILABILITY", "KX440_HANDSET_WORKAROUNDS", "SIM_LITE", "BREW_LITE", "VB_LITE",
            "GSMS_LITE", "VSMS", "VSMS_LITE", "JAVA_LITE", "OPEN_OS_LITE", "SERVER_CORPORATE_MGNT", "SMS_FALLBACK", "VB_MCAD", "INTERNATIONAL_VSMS",
            "CONFERENCE_SCHEDULER"};

    public static void setActiveReleasePath(String activeReleasePath) {
        KnConstants.activeReleasePath = activeReleasePath;
    }

    public static String getActiveReleasePath() {
        return activeReleasePath;
    }

    public static final String REG_BOOLEAN = "true|false";


    public static enum EXTERNAL_CONTACT_PROFILEID {
        KODIAK(0),
        MPTT(1);
        int profileId;

        EXTERNAL_CONTACT_PROFILEID(int id) {
            this.profileId = id;
        }

        public int value() {
            return profileId;
        }
    }

    public static enum EXTERNAL_CONTACT_SUBS_TYPE {
        KODIAK(1),
        MPTT(2);
        int subsType;

        EXTERNAL_CONTACT_SUBS_TYPE(int type) {
            this.subsType = type;
        }

        public int value() {
            return subsType;
        }
    }

    public static enum ROAMING_TYPE {
        HOME(1),
        NATIONAL(2),
        INTERNATIONAL(3);

        int roamingType;

        ROAMING_TYPE(int type) {
            roamingType = type;
        }

        public int value() {
            return roamingType;
        }
    }

    public static enum RESOURCE_ID_LIFECYCLE_MGR{
        NNICAPSERVICE,
        NNIACCMGMTSERVICE;
    }

    public static enum AUDIT {
    	PROV("4000"),
    	TPUSER("4014"),
        XDMDATA_INTF("4019"),
        DYN_CONTACT_GROUP("4056");

    	private String auditValue;

    	private AUDIT(String auditValue) {
    		this.auditValue = auditValue;
    	}

    	public String value() {
    		return auditValue;
    	}
    }

    /**
     * This is used for the  HIRARCHY FLAG checking.
     */
    public static enum HIERARCHY_TYPE {
        HIERARCHY(2),
        NON_HIERARCHY(1);
        int hierarchyType;

        HIERARCHY_TYPE(int hierarchyType) {
            this.hierarchyType = hierarchyType;
        }

        public int value() {
            return this.hierarchyType;
        }

        public static HIERARCHY_TYPE validate(int hierarchy) {
            for (HIERARCHY_TYPE hierarchyType : values()) {
                if (hierarchyType.value() == hierarchy) {
                    return hierarchyType;
                }
            }
            return null;
        }
    }

    public static enum SOFTWARE_PKG {
        POC_SOAP_CSR(1),
        POC_CORPORATE_SOAP(2),
        POC_PUBLIC_XCAP(3),
        CAT_UI(4),
        POC_WCSR(5),
        POC_CLI_AUTO_UPGRADE(6),
        POC_DISP_AUTO_UPGRADE(7),
        AUTO_DISCOVERY_POC_CONS_SERVICE(8),
        HANDSET_ONBOARD(9),
        //DISPATCHER_APP_WEB_SERVICE(10),
        PAM_SERVICE(11),
        WEB_RESOURCE_ACTIVE_SERVICE(12),
        OPR_MGMT_WEB_SERVICE(13),
        POC_KPI_WEB_SERVICE(14),
        PAM_SUBS_MGMT_WEB_SERVICE(15),
        // POC_NNI_CAPABILITY_WEB_SERVICE(16),
        POC_NNI_ACC_MGMT_WEB_SERVICE(17),
        // POC_NNI_ACC_GROUP_MGMT(18),
        PAM_UI(19),
        NNI_GW_SYNC(20),
        THRD_PARTY_ACC_MGMT_SER(21),
        CROSS_CARRIER(22),
        // ATT_IDS_PROV(1000),
        // ATT_CUST_CORP_PACKAGEID(1001),
        //  ATT_CAT_UI_CUST(1002),
        //  ATT_WCSR_CUST(1003),
        //  ATT_XDMS_CUST(1004),
        //  ATT_SOAP_CSR(1005),
        //  ATT_PAM_SUBS_MGMT_SOAP(1006),
        //  ATT_PAMT_UI_CUST(1007),
        //  CUST_ATT_ACTIVATION_PACKAGEID(1008),
        //  ATT_THRD_PARTY_MGMT_CUST(1009),
        // USAGE_DATA_REPORT_GEN_REPORT(4000),
        // CONTACT_GRP_DATA_SNAP_REPORT(4001),
        //  PAM_CSR_REST(5000),
        // PAM_SM_REST(5001),
        // ATT_PAM_CUSTTOMIZE(6000),
        // PAM_CSR_CUSTTOMIZE(6001),
        // PAM_SM_CUSTTOMIZE(6002),
        GW_ACC_LINK_MGMT(7000),
        GW_CONTACT_GRP_MGMT(7001),
        GW_ADMIN_TOOL(7002),
        GW_ADMIN_CUST_TOOL(8000),
        RMQ_MSG_BROKER(9000);


        int softwarePkg;

        SOFTWARE_PKG(int softwarePkg) {
            this.softwarePkg = softwarePkg;
        }

        public int value() {
            return this.softwarePkg;
        }

        public static SOFTWARE_PKG validate(int softwarePkg) {
            for (SOFTWARE_PKG softwarePkgs : values()) {
                if (softwarePkgs.value() == softwarePkg) {
                    return softwarePkgs;
                }
            }
            return null;
        }

    }

    public static final String HTTPS_PREFIX = "https://";

    public static enum MICROSERVICES_EVENT_TYPE {
        CLIENT_ACTIVATION("subscriberActivation"), DELETE_SUBSCR("deleteSubscriber"),
        CREATE_SUBSCR("createSubscriber"),
        CHANGE_SERVICE_AUTH_STATUS("serviceAuthStatus"),CHANGE_MDN("changeMDN"),
        CREATE_CORP_GROUP("createCorpGroup"),MODIFY_CORP_GROUP("modifyCorpGroup"),
        DELETE_CORP_GROUP("deleteCorpGroup"), MODIFY_SUBS_CONTACT("modifyCorpContact"),
        CREATE_PUB_GROUP("createPubGroup"), MODIFY_PUB_GROUP("modifyPubGroup"),
        DELETE_PUB_GROUP("deletePubGroup"),FEATURE_BIT_CHANGE("featureBitChange"),
        CLIENT_PV_UPGRADE("clientPVUpgrade"),ASSIGN_USER_PROFILE("assignUserProfile"),
        UNASSIGN_USER_PROFILE("unassignUserProfile"), CREATE_CORP("createCorp"),
        MODIFY_CORP("modifyCorp"), DELETE_CORP("deleteCorp"), MODIFY_SUBSCRIBER("modifySubscriber"),
        CREATE_DEVICE("CreateDevice"),MODIFY_DEVICE("ModifyDevice"),DELETE_DEVICE("DeleteDevice")
        ,MODIFY_BULK_GROUP_PROPERTIES("modifyBulkGroupProperties"), ETAG_UPDATE("etagUpdate");
        private String eventType;

        MICROSERVICES_EVENT_TYPE(String eventType) {
            this.eventType = eventType;
        }

        public String value() {
            return eventType;
        }
    }

    public static enum MICROSERVICES_NOTIFY_EVENT_TYPE {
        USER_NOTIFY_EVENTS(1), GROUP_NOTIFY_EVENTS(2)
        , CONTACT_NOTIFY_EVENTS(3), CLIENT_PV_UPGARDE(4)
        ,CORPORATE_EVENT(5),BULK_GROUP_PROPERTIES_NOTIFY_EVENTS(6)
        ,ETAG_UPDATE(7);

        private int type;

        MICROSERVICES_NOTIFY_EVENT_TYPE(int type) {
            this.type = type;
        }

        public int value() {
            return type;
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

    /**
     * This could be used to configure more feature bit which we
     * want to enable via PROVISIONING
     */
  public static enum PROV_FS_BIT {
        LMR_INTEROP(53),
        THIRD_PARTY_DATA_RECODING(134),
        HYBRID_IOS_CLIENT(135),
        SELF_DND_PRIVILEGE(136),
        LARGE_AGENCY_DISPATCH(139);
        int featureBitPosValue;

        PROV_FS_BIT(int bitValue) {
            this.featureBitPosValue = bitValue;
        }

        public int value() {
            return featureBitPosValue;
        }

    }

    /**
     * This will be the possible
     * value for  the PROSIONING bit values
     * which can be either enabled or disabled.
     */
    public static enum PROVFS_ENABLE {
        ENABLED(1),
        DISABLED(0);
        int featureBitValue;

        PROVFS_ENABLE(int status) {
            this.featureBitValue = status;
        }

        public int value() {
            return featureBitValue;
        }
    }
    public static enum FEATURE_SET_ALLOWED {
        INSTAPOC(0),
        MISSEDCALLALERT(1),
        CALLENDEDALERT(2),
        LOCATIONSUBSCRIPTION(3),
        SUPERVISORYOVERRIDE(4),
        BROADCASTGROUPSERVICE(5),
        RESTRICTAVAILABILITY(6),
        SECONDARYPRESENCE(7),

        TUSMS(14),
        XCAPDOCDIFF(15),
        POCOVERWIFI(16),
        HTTPSSUPPORT(17),
        PAGMSGSUPPORT(18),
        RELOGINMSGSUPPORT(19),
        TLKGRPSELSERVER(20),
        PRUPDATEREDUCTION(21),
        JITTERSTATS(22),
        HISTBASEDPRES(23),
        DECOUPPRESCALL(24),
        PERICLIENTLOGS(25),
        ONDEMCLIENTLOGS(26),
        ONDEMLOCATION(27),
        TLKGRPSCANCLIENT(28),
        BATTERYUSAGEOPTIMIZATION(29),
        CLIENTUISTATS(30),
        TLKGRPSELCLIENT(31),
        PUSHTOTEXT(33),
        PUSHTOMULTIMEDIA(34),
        PUSHTOLOCATION(35),
        URGENTMSGORIG(36),
        GROUPMEMLOC(38),
        GEOFENCEFEATURE(39),
        GCMPUSHNOTIFICATION(40),
        REMOTEPUSHNOTIFICATION(43),
        PTTRADIOCLIENT(44),
        XCAPCOUCHCLIENT(45),
        VOICEMSGFALLBACK(46),
        BREADCUMB(49),
        INSTADRX(50),
        BLOCK_CALL_WIFI(51),
        BLOCK_CALL_CELLULAR(52),
        LMR_INTEROP_BIT(53),
        AMBIENTLISTENING(54),
        DISCRETELISTENING(55),
        USERCHECK(56),
        USERENABLE(57),
        REMOTEEMERGENCYPERMISSION(58),
        ABDG_GROUP_OWNER(59),
        ABDG_GROUP_MEMBER(60),
        MCVIDEOTX(67),
        MCVIDEORX(68),
        MCVIDEOGROUPRX(69),
        MCVIDEOCONFIRMEDPULL(70);

        int featureSet;

        FEATURE_SET_ALLOWED(int type) {
            this.featureSet = type;
        }

        public int value() {
            return featureSet;
        }
    }

    public static enum MCPTT_PERMISSION_BIT {
        AMBIENTLISTENING(0),
        DISCRETELISTENING(1),
        USERCHECK(2),
        USERENABLE(3),
        REMOTEEMERGENCYPERMISSION(4),
        MCVIDEOUNCONFIRMEDPULL(5);

        int mcpttPermissionBit;

        MCPTT_PERMISSION_BIT(int type) {
            this.mcpttPermissionBit = type;
        }

        public int value() {
            return mcpttPermissionBit;
        }
    }

    public static enum DESTINATION_TYPE {
        USER_SELECTED_DESTINATION(1),
        CAT_CONFIGURED_DESTINATION(2);

        int destType;

        DESTINATION_TYPE(int destType) {
            this.destType = destType;
        }

        public int value() {
            return destType;
        }
    }

    public static enum DESTINATION_TYPE_MGMT {
        CONTACT(1),
        GROUP(2);

        int destTypeMgmt;

        DESTINATION_TYPE_MGMT(int destTypeMgmt) {
            this.destTypeMgmt = destTypeMgmt;
        }

        public int value() {
            return destTypeMgmt;
        }
    }

    public static enum DESTINATION_PRIORITY {
        FIRST_PRIORITY(1),
        SECOND_PRIORITY(2);

        int priority;

        DESTINATION_PRIORITY(int priority) {
            this.priority = priority;
        }

        public int value() {
            return priority;
        }
    }

    public static final String MICROSERVICE_NOTIFY_DOC_VER = "1.0";

    public static final String GROUP_NOTIFY_SERVICE_TYPE = "GrpNtfEvents";
    public static final String USER_NOTIFY_SERVICE_TYPE = "UserNtfEvents";
    public static final String CONTACT_NOTIFY_SERVICE_TYPE = "ContactNotify";

    public static final String GET_SUBSCR_FEATURE_CUSTOM_HOOK_CALL_DATA = "CUSTOM_DATA";
    public static final int retryThreshold = 3;
    public static final String LOCAL_COUNTRY_CODE_KEY = "COUNTRYCODE";

    public static List<String> customKeys = Arrays.asList("FIRST_NAME", "LAST_NAME", "SUBSCRIBER_UNIQ_ID", "RATE_PLAN", "EXT_FAN_ID", "FAN_NAME",
            "EXT_BAN_ID", "BAN_NAME", "INT_FAN_ID", "INT_BAN_ID", "ACCOUNT_TYPE_INDICATOR", "BAN_TYPE", "BILLING_MDN");


    public static final String IDM_REST_SERVICE_NAME = "IDM-SERVICE";
    public static final int REST_IDM_INTF_TYPE = 3;
    public static final int IDM_OTP_SMS_VERIFICATION_MSG_ID = 5301;
    public static final int OIDC_TMP_PASSWORD_GENERATION = 5302;
    public static final int OIDC_ACCOUNT_VERIFICATION = 5303;
    public static final String CORP_ID = "corpId";
    public static final String CORP_ID_EXT = "corpid";
    public static final String EXT_CORP_ID = "extCorpId";
    public static final String BAN_DETAILS = "BANDETAILS";
    public static final String FAN_DETAILS = "FANDETAILS";
    public static final String BAN_TYPE = "1";
    public static final String HIER_CTX_ID_TYPE = "3";
    public static final String STATUS = "status";
    public static final String STATUS_CODE = "statusCode";
    public static final String MESSAGE = "message";
    public static final String CORPNAME = "corpName";
    public static final String ACTIVATION_CODE = "activationCode";
    public static final String EXPIRY_TIME = "expiryTime";
    public static final String ACTIVATION_TIME = "insertionTime";
    public static final int PROVISIONED = 0;
    public static final String USER_ID = "userId";
    public static final String USER_TYPE = "userType";
    public static final String MDN_IDM = "mdn";
    public static final String MDN_AS_USER_ID = "mdnUser";
    public static final String SUPER_ADMIN = "1";
    public static final String OTHER = "2";
    public static final String OLD_USER_ID = "oldUserId";
    public static final String OLD_ALIAS_MDN = "oldAliasMdn";
    public static final int DISPATCH_TYPE = 1;
    public static final int DISPATCH_APP_ID = 2;
    public static final String OIDC = "/oidc";
    public static final String HTTP_SCHEME = "http://";
    public static final String FORWARD_SLASH = "/";
    public static final String PORT = "8080";
    public static final String RESOURCE_IDENTIFIER = "/kidm/int/v1/";
    public static final String IDM_USER = "/users/";
    public static final String WELCOME_EMAIL = "welcome_mail";
    public static final String VERIFICATION_EMAIL = "verification_mail";
    public static final String VERIFICATION_EMAIL_API = "verificationmail";
    public static final String UPDATE_EMAIL = "pwd_update_mail";
    public static final String PROFILE_1 = "1";
    public static final String ACCOUNT_STATUS = "accState";
    public static final String CHANGE_SERVICE_AUTH_STATUS = "state?accstate=";
    public static final String ALIAS_MDN = "aliasMdn";

    // consolidation error codes constants
    public static final String CONSOL_VAL_ERROR_CODE = "Validator.KNEC-CM10301";
    
    public static int SUBSCR_DEF_PTTRADIO_ENABLED=1;
    public static final int ENABLED = 1;

    public static final String BIT_ENABLED = "1";

    public static final int DISABLED = 0;
    public static final String DISABLED_STRING = "0";
    public static final String ENABLED_STRING = "1";
    public static final int DEFAULT_AU = 2;
    public static final String PWD_SUFFIX = "#1A";
    public static final String ENABLE_OIDC = "ENABLE_OIDC";
    public static final String ENABLEOIDC = "enable_oidc";
    public static final String LI_SERVER_3GPP_COMPLIACE_FLAG ="LI_SERVER_3GPP_COMPLIACE_FLAG";
    public static final String LI_SERVER_3GPP_ENCR_TARGETID_FLAG ="LI_SERVER_3GPP_ENCR_TARGETID_FLAG";
    public static final String CLIENT_TYPE = "clientType";
    public static final String SW_VERSION = "swVersion";
    public static final int NOT_MODIFIED = -1;

    public static final String HIERARCHY_PROPS = "hierarchyCleanUp";
    public static final String HIERARCHY_PROPS_FILE_NAME = "hierarchyConfig.properties";
    public static final String HIERARCHY_DIR_NAME = "activeReleaseDir";
    
    // dynamic TP group 
    public static final int GROUP_CREATED_BY_DYNAMIC_INTF = 1;
    public static final int GROUP_CREATED_BY_ABDG_INTF = 2;

	public static enum MICROSERVICES_COMMON_CONFIG {
		ABDGFEATUREFLAG("ABDGFEATUREFLAG"), DYNAPI_SERVICE_ENABLED("DYNAPI_SERVICE_ENABLED"),
		ALLOW_PUBLIC_CNT_MGMT("ALLOW_PUBLIC_CNT_MGMT"), ALLOW_PUBLIC_GRP_MGMT("ALLOW_PUBLIC_GRP_MGMT"),
		MCS_DOMAIN_NAME("MCS_DOMAIN_NAME"),GMSFQDN("GMSFQDN"),EMERLOCREPORTINTVL("EMERLOCREPORTINTVL"), VLARGE_GROUP_SUPPORTED("VLARGE_GROUP_SUPPORTED")
        ,MULTISITE_DEPLOYMENT_FLAG ("MULTISITE_DEPLOYMENT_FLAG");

		private String serviceKey;

		MICROSERVICES_COMMON_CONFIG(String serviceKey) {
			this.serviceKey = serviceKey;
		}

		public String value() {
			return serviceKey;
		}
	}
	
	public static enum MICROSERVICES_SERVICE_CONFIG {

        MCPTT_UE_CONFIG_NAME("MCPTT_UE_CONFIG_NAME"), MCPTT_USER_PROFILE_SETTINGS("MCPTT_USER_PROFILE_SETTINGS"),MCPTT_USER_PROFILE_NAME("MCPTT_USER_PROFILE_NAME");

        private String serviceKey;

        MICROSERVICES_SERVICE_CONFIG(String serviceKey) {
            this.serviceKey = serviceKey;
        }

        public String value() {
            return serviceKey;
        }
    }

    public static enum OIDC_USER_TYPE {
        DISPATCHER(1),
        OTHERS(2);

        int userType;

        OIDC_USER_TYPE(int userType) {
            this.userType = userType;
        }

        public int value() {
            return userType;
        }
    }

    public static enum APP_ID {
        CAT("1"),
        DISPATCHER("2"),
        WCSR("3"),
        HANDSET_STANDARD("4"),
        CSD("5"),
        APDConsole("6"),
        USERMCSCLIENTS("10");

        String appId;

        APP_ID(String appId) {
            this.appId = appId;
        }

        public String value() {
            return appId;
        }
    }

    public static enum TMP_PWD_MODE {
        VERIFICATION_MAIL("1"),
        PASSWORD_INFO_MAIL("2"),
        PASSWORD_INFO_SMS("3"),
        WELCOME_MAIL("4"),
        WELCOME_SMS("5");

        String tmpPwdMode;

        TMP_PWD_MODE(String tmpPwdMode) {
            this.tmpPwdMode = tmpPwdMode;
        }

        public String value() {
            return tmpPwdMode;
        }
    }

    public static enum SEND_PWD_MODE {
        NONE(0),
        MAIL(1),
        SMS(2),
        BOTH(3);

        int sendPwdMode;

        SEND_PWD_MODE(int sendPwdMode) {
            this.sendPwdMode = sendPwdMode;
        }

        public int value() {
            return sendPwdMode;
        }
    }

    public static final long CACHE_EXPIRY_TIME = 86400000L;
    public static final int CONTROLLED_UPGRADE_VERSION = 2;

    public static final String MC_ID_OIDC = "mcid";
    public static final String MCPTT_ID_OIDC = "mcpttid";
    public static final String MCVIDEO_ID_OIDC = "mcvideoid";
    public static final String MCDATA_ID_OIDC = "mcdataid";
    public static final String NETWORK_NAME="networkName";
    public static final String DIGEST_PWD_OIDC = "digestPassword";
    public static final String DEVICEIMPU_OIDC = "deviceImpu";
    public static final String DEVICEIMPL_OIDC = "deviceImpi";
    public static final String MCS_SCOPES_OIDC = "mcsscope";
    public static final String ACTIONS = "actions";
    public static final String CLIENT_TYPE_OIDC = "clienttype";
    public static final String USER_TYPE_OIDC = "usertype";
    public static final String ACC_STATE_OIDC = "accstate";
    public static final String ACT_CODE_OIDC = "actcode";
    public static final String CREATION_DATE_OIDC = "creationdate";
    public static final String BACKWARD_COMPATIBILTY_REQ = "BACKWARD_COMPATIBILTY_REQ";

    public static final String CLIENT_TYPE_HS_WIFI_CROSS_NORMAL_RADIO = "1|5|10|14|15|16";
    public static final String CLIENT_TYPE_WIFI_CROSS_NORMAL_RADIO = "5|10|15|16";

    public static final String APP_UID_TGSS_LIST = "kn-tgss-list";
    public static final int OPERATION_ID_UPDATE_TGSSLIST = 277;
    public static final int OPERATION_ID_GET_TGSSLIST = 278;
    public static final int OPERATION_ID_DELETE_TGSSLIST = 279;
    public static final int OPERATION_ID_PRIVACYOPTIN = 281;
    //operation ids for mcsxcap service
    public static final int OPERATION_ID_GET_MCPTT_UE_CONFIG = 500;
    public static final int OPERATION_ID_GET_MCPTT_USER_PROFILE= 501;
    public static final int OPERATION_ID_GET_MCPTT_SERVICE_CONFIG = 502;
  
    public static final String REMOTE_SMPP_SERVER_PTTSERVERID="REMOTE_SMPP_SERVER_PTTSERVERID";
    public static final String USE_REMOTE_SMPP="USE_REMOTE_SMPP";
    public static final String CLUSTER_STATUS = "CLUSTER_STATUS";
	public static final String FQDN_INFO="RMQ_FQDN_INFO";
	public static final String RMQ_XDMPROV_QUEUE_INFO="RMQ_XDMPROV_QUEUE_INFO";
    public static final String RMQ_XDMPROV_WRITE_QUEUE_INFO="RMQ_XDMPROV_WRITE_QUEUE_INFO";
	public static final String RMQ_XDMPUB_QUEUE_INFO="RMQ_XDMPUB_QUEUE_INFO";
	public static final String RMQ_XDMCORP_QUEUE_INFO="RMQ_XDMCORP_QUEUE_INFO";
    public static final String RMQ_XDMCORP_WRITE_QUEUE_INFO="RMQ_XDMCORP_WRITE_QUEUE_INFO";
	public static final String RMQ_XDMDATAINTF_QUEUE_INFO="RMQ_XDMDATAINTF_QUEUE_INFO";
    public static final String RMQ_XDMLINTFYEVT_QUEUE_INFO="RMQ_XDMLINTFYEVT_QUEUE_INFO";
	public static final String RMQ_CONTACTNOTIFY_BINDING_KEY="RMQ_CONTACTNOTIFY_BINDING_KEY";
    public static final String RMQ_GRPNTFTYEVENT_BINDING_KEY="RMQ_GRPNTFTYEVENT_BINDING_KEY";
	public static final String RMQ_DISTSCHEDULER_BINDING_KEY="RMQ_DISTSCHEDULER_BINDING_KEY";
    public static final String RMQ_MCSXCAP_QUEUE_INFO="RMQ_MCSXCAP_QUEUE_INFO";
    public static final String RMQ_ETAGNTFTYEVENT_BINDING_KEY="RMQ_ETAGNTFTYEVENT_BINDING_KEY";
    public static final String RMQ_IDSPROVService_BINDING_KEY="RMQ_IDSPROVService_BINDING_KEY";
	public static final String RMQ_SOAPPROVService_BINDING_KEY="RMQ_SOAPPROVService_BINDING_KEY";
	public static final String RMQ_USERNTFTYEVENT_BINDING_KEY="RMQ_USERNTFTYEVENT_BINDING_KEY";
    public static final String RMQ_CLIENTPVUPGRADE_BINDING_KEY="RMQ_CLIENTPVUPGRADE_BINDING_KEY";
    public static final String RMQ_MCSNOTIFICATION_BINDING_KEY="RMQ_XCAPSN_BINDING_KEY";
	public static final String RMQ_LOGINNOTIFY_BINDING_KEY="RMQ_LOGINNOTIFY_BINDING_KEY";
    public static final String RMQ_LINOTIFY_BINDING_KEY="RMQ_LINOTIFY_BINDING_KEY";
    public static final String VAULT_QUERY_FQDN_INFO="VAULT_QUERY_FQDN";
	public static final String RMQ_USER_NAME = "RMQ_USER_NAME";
	public static final String RMQ_PASSWORD = "RMQ_PASSWORD";
	public static final String RMQ_PORT = "RMQ_PORT";
	public static final String RMQ_V_HOST = "RMQ_V_HOST";
	public static final String RMQ_EXCHANGE_NAME = "RMQ_EXCHANGE_NAME";
    public static final String SECRET_DATA_PATH ="secret/data/";
    public static final String RMQ_USER_NAME_PATH = "U/CMN/RMQU";
    public static final String RMQ_PASSWORD_PATH = "P/CMN/RMQP";
    public static final String RMQ_USER_KEY = "RMQU";
    public static final String RMQ_PASSWORD_KEY	= "RMQP";
    public static final String JWT_PRESHARED_NAME_PATH = "P/MAPS/JWT_PRESHARED_KEY";
    public static final String JWT_PRESHARED_KEY = "JWT_PRESHARED_KEY";
    public static final String OIDCSECRETKEY = "OIDCSECRETKEY";
    public static final String T10_USER_NAME_PATH = "U/CMN/TTAP/U";
    public static final String T10_PASSWORD_NAME_PATH = "P/CMN/TTAP/P";
    public static final String OIDC_SECRET_KEY_PATH = "P/MAPS/OIDCSECRETKEY";
    public static final String ENTT_PASSWORD_NAME_PATH = "P/CMN/TTAP/P";
    public static final String CBS_BUCKET_POCDATA_USER_NAME_PATH = "U/CBS/POCDATA_BKT/U";
    public static final String CBS_BUCKET_POCDATA_PW_PATH = "P/CBS/POCDATA_BKT/P";
    public static final String GRIDGAIN_AUTHID_NAME_PATH = "U/CMN/GGAP/U";
    public static final String GRIDGAIN_AUTHPWD_NAME_PATH = "P/CMN/GGAP/P";
    public static final String GOOGLE_MAPS_API_KEY = "GMAPK";
    public static final String GOOGLE_MAPS_API_PATH = "/P/MAPS/GMAPK";
    public static final String SALT = "X@#5";
	public static final String SECRETKEY = "A1B8D95BA9EA5440F252C3";
	public static final String IVSTRING = "0000000000000000";
	public static final Integer KEYSIZE=256;
	public static final Integer ITERATION=1000;
    public static final String INVALID_POC_HOME ="0";


	public  static  final String MCPTT_SERVICE_SCOPE_OIDC = "3gpp:mc:ptt_service";
    public  static  final String MCPTT_VIDEO_SCOPE_OIDC = "3gpp:mc:video_service";
    public  static  final String MCPTT_DATA_SCOPE_OIDC =  "3gpp:mc:data_service";
    public  static  final String MCPTT_KEY_MGMT_SCOPE_OIDC = "3gpp:mc:ptt_key_management_service";
    public  static  final String MCPTT_VIDEO_KEY_MGMT_SCOPE_OIDC = "3gpp:mc:video_key_management_service";
    public  static  final String MCPTT_DATA_KEY_MGMT_SCOPE_OIDC =  "3gpp:mc:data_key_management_service";
    public  static  final String MCPTT_CONFIG_MGMT_SCOPE_OIDC = "3gpp:mc:ptt_config_management_service";
    public  static  final String MCPTT_VIDEO_CONFIG_MGMT_SCOPE_OIDC =  "3gpp:mc:video_config_management_service";
    public  static  final String MCPTT_DATA_CONFIG_MGMT_SCOPE_OIDC =  "3gpp:mc:data_config_management_service";
    public  static  final String MCPTT_GROUP_MGMT_SCOPE_OIDC =  "3gpp:mc:ptt_group_management_service";
    public  static  final String MCPTT_VIDEO_GROUP_MGMT_SCOPE_OIDC = "3gpp:mc:video_group_management_service";
    public  static  final String MCPTT_DATA_GROUP_MGMT_SCOPE_OIDC = "3gpp:mc:data_group_management_service";
    public  static  final int MCPTT_COMPLIANCE_ENABLED = 1;
    public  static  final int MAX_OIDC_PASSWORD_LENGTH = 12;
    public  static  final int DEFAULT_DEVICE_PASSWORD_LENGTH=32;
    public static final String MCS_TEMP_PASSWORD_EXPIRY = "MCS_TEMP_PASSWORD_EXPIRY";
    //Couchbase connection details for UPM
    public static final String RETRY_COUNTER = "RETRY_COUNTER";
    public static final String POCBUCKETNAME = "POCBUCKETNAME";
    public static final String CBS_BUCKET_PASSWORD = "CBS_BUCKET_PASSWORD";
    public static final String CONNECTION_TIMEOUT_CBS = "CONNECTION_TIMEOUT_IN_MILLIS";
    public static final int RETRY_TIME_LAPSE = 1000;
    public static final String CBS_FQDN_POCDATA = "CBS_FQDN_POCDATA";
    public static final String CBS_FQDN = "CBS_FQDN";
    public static final Integer IS_ABDG_GROUP = 1;
    public static final Integer GROUP_SHARED_ENABLED = 1;
    public static final String CBS_BUCKET_POCDATA_PW = "P";
    public static final String CBS_BUCKET_POCDATA_USER = "U";
    public static final String FAN_ID = "FAN_ID";
    public static final String BAN_ID = "BAN_ID";

    public static enum ON_BOARDING_MAIL {
        REQUIRED(0),
        NOT_REQUIRED(1);

        int onBoardingMailRedq;

        ON_BOARDING_MAIL(int onBoardingMailRedq) {
            this.onBoardingMailRedq = onBoardingMailRedq;
        }

        public int value() {
            return onBoardingMailRedq;
        }
    }
    
     public static enum GROUP_SIZE_TYPE {
        LARGE_GROUP(0),
        VLARGE_GROUP(1);

        int isVLargeGroup;

        GROUP_SIZE_TYPE(int isVLargeGroup) {
            this.isVLargeGroup = isVLargeGroup;
        }

        public int value() {
            return this.isVLargeGroup;
        }
    }

    public static enum CAMERA_TYPE {
        RE4(1);
        int cameraType;

        CAMERA_TYPE(int cameraType) {
            this.cameraType = cameraType;
        }

        public int value() {
            return this.cameraType;
        }
    }
    
    public static final String ESCAPE_CHAR = "\\";

    public static final String ESCAPE_CHAR_REGEX = "\\\\";

    public static final String ESCAPE_APOSTROPH_CHAR = "''";

    public static final Integer DEFAULT_EMERGENCY_TYPE = 1;

    public static final String ASYNCFW_MAX_JOB_RUNTIME = "MAX_JOB_RUN_TIME";

    public static final int UPM_JOB_CRASHED_STATUS = 5;

    public static final int UPM_JOB_FAILED_STATUS = 3;

    public static final int UPM_JOB_NEW_STATUS = 0;

    public static final int UPM_JOB_INPROGRESS_STATUS = 1;

    public static final String GROUP_RECORDING_DISABLED = "0";
    public static final String GROUP_RECORDING_MCPTT = "1";
    public static final String GROUP_RECORDING_MCDATA = "2";
    public static final String GROUP_RECORDING_MCPTT_MCDATA = "3";
    public static final String GROUP_RECORDING_MCVIDEO = "4";
    public static final String GROUP_RECORDING_MCPTT_MCVIDEO = "5";
    public static final String GROUP_RECORDING_MCPDATA_MCVIDEO = "6";
    public static final String GROUP_RECORDING_MCPTT_MCDATA_MCVIDEO = "7";

    public static final String POCHOME = "POCHOME";
    public static final String CLUSTERID = "CLUSTERID";

    public static enum BUSINESS_CLIENT_TYPE {
        ATT(1);

        int businessClientType;

        BUSINESS_CLIENT_TYPE(int businessClientType) {
            this.businessClientType = businessClientType;
        }

        public int value() {
            return businessClientType;
        }
    }

    public enum REC_TYPE {
        AUDIO(1),
        VIDEO(2),
        BOTH(3);

        int recType;

        REC_TYPE(int recType) {
            this.recType = recType;
        }

        public int value() {
            return this.recType;
        }
    }

    public enum SIP_RECORDING_FLAG {
        ANY(2),
        NONE(null);

        Integer sipRecordingFlag;

        SIP_RECORDING_FLAG(Integer sipRecordingFlag) {
            this.sipRecordingFlag = sipRecordingFlag;
        }

        public Integer value() {
            return this.sipRecordingFlag;
        }
    }

    public enum CLONING_BIT_POSITION {
        CONTACT(0),
        GROUP(1),
        GROUP_MEMBER_PROP(2),
        SCAN_LIST(3),
        FEATURE(4),
        EMERGENCY_ARRBT(5),
        PERMISSION(6);

        Integer cloningBitPosition;

        CLONING_BIT_POSITION(Integer cloningBitPosition) {
            this.cloningBitPosition = cloningBitPosition;
        }

        public Integer value() {
            return this.cloningBitPosition;
        }
    }

    public enum CLONING_VALIDATION_BIT {
        CONTACT(0),
        GROUP(1),
        GROUP_MEMBER_PROP(2),
        SCAN_LIST(3),
        FEATURE(4),
        EMERGENCY_ARRBT(5),
        PERMISSION(6);

        Integer cloningValidationBit;

        CLONING_VALIDATION_BIT(Integer cloningValidationBit) {
            this.cloningValidationBit = cloningValidationBit;
        }

        public Integer value() {
            return this.cloningValidationBit;
        }
    }

    public enum LOCATION_ENABLED_FEATURE {
        LOCATION_ENABLED_JOB(16);

        int state;

        LOCATION_ENABLED_FEATURE(int status) {
            this.state = status;
        }

        public int value() {
            return state;
        }
    }

    public enum LOCATION_ENABLED_TASK_TYPE {
        ADDLOCATIONENABLED("ADDLOCATIONENABLED");

        String value;

        LOCATION_ENABLED_TASK_TYPE(String value) {
            this.value = value;
        }

        public String Value() {
            return value;
        }

    }
    public enum ZONE_ASSIGNMENT_STATUS {
        DISABLED("disabled"),
        ASSIGNED("assigned"),
        FAILED("failed"),
        PARTIAL("partial");

        private final String value;

        ZONE_ASSIGNMENT_STATUS(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public enum AUTO_ASSIGN_TRACKER {
        UNASSIGNED(0),
        AUTO_ASSIGNED(1),
        MANUALLY_ASSIGNED(2);

        private final int value;

        AUTO_ASSIGN_TRACKER(int value) {
            this.value = value;
        }

        public int value() {
            return value;
        }
}

}