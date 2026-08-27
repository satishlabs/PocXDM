/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/************************************************************************
 * File name:   KnProvConstants.java
 * Subsystem:
 * <p/>
 * Name                  Date         Release
 * -----------------    -----------   -------
 * Ravi Shanker .P       Dec 15, 2010   7.0
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
 ***************************************************************************/
package com.kodiak.xdms.server.subsmgmt.resources;

public class KnProvConstants {

    public static final String LIBRARY_NAME = "PROVLIB";
    public static final int SUCCESS = 0;
    public static final int FAILURE = 1;

    //hardcoded the list Name as this is as per the system Design.
    public static final String LIST_NAME = "oma_pocbuddylist";
    public static final String LIST_DISPLAY_NAME = "oma pocbuddylist";

    public static final String CREATE_SUBSCRIBER_SUCCESS = "CREATE SUBSCRIBER SUCCESS";
    public static final String CREATE_CORP_PROFILE_SUCCESS = "CREATE CORP PROFILE SUCCESS";
    public static final String UPDATE_AUTH_STATUS_SUCCESS = "UPDATE Subscriber Auth Status Success";
    public static final String UPDATE_SUBS_PROFILE_SUCCESS = "susbcriber profile updated successfully";
    public static final String FORCE_SYNC_SUCCESS = "FORCE_SYNC_SUCCESS";
    public static final String DELETE_SUBSCRIBER_SUCCESS = "Subscriber Profile deleted successfully";
    public static final String DELETE_BULK_SUBSCRIBER_SUCCESS = "Bulk Subscriber Profiles deleted successfully";
    public static final String CHANGE_MDN_SUCCESS = "Change MDN SUCCESS";
    public static final String REMOVE_SUBSCRIBER_SUCCESS = "Remove Subscriber Success";
    public static final String DELETE_CORP_PROFILE_SUCCESS = "Corporate Profile deleted successfully";
    public static final String CREATE_PAMACCOUNT_SUCCESS = "PAM account created successfully";
    public static final String UPDATE_PAMACCOUNT_SUCCESS = "PAM account updated successfully";
    public static final String DELETE_PAMACCOUNT_SUCCESS = "PAM account deleted successfully";
    public static final String GET_PAMACCOUNT_INFO_SUCCESS = "Get PAM account Info successfully";
    public static final String GET_PAMSUBS_INFO_SUCCESS = "Get PAM Subs Info successfully";
    public static final String CREATE_PAMSUBSPROF_SUCCESS = "PAM Subs Profile created successfully";
    public static final String UPDATE_PAMSUBSPROF_SUCCESS = "PAM  Subs Profile updated successfully";
    public static final String DELETE_PAMSUBSPROF_SUCCESS = "PAM  Subs Profile deleted successfully";
    public static final String UPDATE_DISP_SUBS_PROFILE_SUCCESS = "Dispatch for subscriber profile updated successfully";
    public static final String CREATE_EXT_SUBSCRIBER_SUCCESS = "CREATE EXTERNAL SUBSCRIBER SUCCESS";
    public static final String DELETE_EXT_SUBSCRIBER_SUCCESS = "DELETE EXTERNAL SUBSCRIBER SUCCESS";
    public static final String UPDATE_EXT_SUBSCRIBER_SUCCESS="UPDATE EXTERNAL SUBSCRIBER PROFILE SUCCESS";
    public static final String ADD_MODIFY_EXT_SUBSCRIBER_SUCCESS ="ADD OR MODIFY EXTERNAL SUBSCRIBER SUCCESS";
    public static final String CREATE_SUBSCR_ROAMING_PROFILE_SUCCESS ="CREATE SUBSCRIBER ROAMING PROFILE SUCCESS";
    public static final String DELETE_SUBSCR_ROAMING_PROFILE_SUCCESS ="DELETE SUBSCRIBER ROAMING PROFILE SUCCESS";
    public static final String UPDATE_CORP_PROFILELASTUPDATE_TIME_SUCCESS ="UPDATE CORP PROFILE LAST UPDATE TIME SUCCESS";
    public static final String CUSTOM_PROV_UPDATEBAN_OP="Updated Hierarchy successfully";
    public static final String UNSUPPORTED_DEVICE="UNSUPPORTED_DEVICE";
    public static final String UPDATE_ETAG_NNI_SUBSCR="UPDATE ETAG FOR NNI SUBSCRIBER";
    public static final String UPDATE_PRIVACY_OPT_STATUS_SUCCESS="UPDATE_PRIVACY_OPT_STATUS SUCCESS";
    public static final String CREATE_DEVICE_SUCCESS = "CREATE DEVICE SUCCESS";
    public static final String MODIFY_DEVICE_SUCCESS = "MODIFY DEVICE SUCCESS";
    public static final String SET_UPDATE_CLIENTSETTINGS_SUCCESS = "Client settings created/updated successfully";
    public static final String CREATE_CORP_SUCCESS = "CREATE CORP SUCCESS";
    public static final String UPDATE_CORP_SUCCESS = "UPDATE CORP SUCCESS";
    public static final String DELETE_CORP_ACCOUNT_SUCCESS = "Corporate Account deleted successfully";
	public static final int ZERO = 0;
	public static final int ONE = 1;
	public static final int TWO = 2;


    //List of Table name for Registering to the ID generator
    public static final String DG = "DG.";
    public static final String TABLE_CORP_INFO = DG + "POCCORPINFO";
    public static final String TABLE_CORP_INFO_COLUMN = "CORPID";

    public static final String TABLE_PAM_ACC_INFO = DG + "PAMACCOUNTINFO";
    public static final String TABLE_PAM_ACC_INFO_COLUMN = "PAMACCID";
    
    public static final String TABLE_TP_USER_MDN_MAP = DG + "THIRD_PARTY_USER_MDN_MAP";
    public static final String TABLE_TP_USER_MDN_MAP_COLUMN = "THIRD_PARTY_USER_ID";
    
    public static final int DEFAULT_PROFILEID = 1;
    public static final long DEFAULT_SUBSFS = 1048375l;
    public static final int DEFAULT_PUBTYPE = 1;
    public static final int DEFAULT_CORPTYPE = 0;
    public static final int DEFAULT_CLIENTTYPE = 5;

    //end of list

    //for validation FW
    public static final String KEY_DATATYPE_MDN = "MDN";
    public static final String KEY_DATATYPE_EXTCORPID = "EXTCORPID";

    public static final String TEL_URI_TEMPLATE = "tel:+";
    public static final String SIP_URI_TEMPLATE = "sip:"; //sip protocol
    public static final String ROUTING_TYPE_TEMPLATE = "lr"; //loose Routing
    public static final String SEMI_COLON = ";";
    public static final String KNWDS = "knwds";

    public static final int DEFAULT_ROAMING_CLUSTER_ID = 1;

    //user agent parameter
    public static final String MANUFACTURER_NAME = "ManufacturerName";
    public static final String DEVICE_NAME = "DeviceName";
    public static final String OS_NAME = "OSName";
    public static final String OS_VERSION = "OSVersion";
    public static final String APPLICATION_NAME = "ApplicationName";
    public static final String PROTOCOL_VERSION = "ProtocolVersion";
    public static final String APPLICATION_VERSION = "ApplicationVersion";

    public static final int ENABLED_POC_WIFI = 1;
    public static final int WEB_DISPATCH_ENABLED = 1;
    public static final int LICENSEN_TYPE_STANDARD = 1;
    public static final String IMPLICIT_DEVICE = "0";

    //welcome sms parameters
    public static final int SRC_SUB_SYSTEM = 37;
    public static final int DELIVERY_REPORT = 0;
    public static final int VERSION = 1;
    public static final int WELCOME_SMS_ID = 5000;
    public static final int UPDATE_SMS_ID = 5001;
    public static final int NTLV_MESSAGE_TYPE = 1;
    //ptt client type -notification changes
    public static final int WELCOME_SMS_ID_PTTRADIO = 5002;
    public static final int CHANGEMDN_SMS_ID_PTTRADIO = 5003;

    public static final String DUMMY = "dummy.";

    public static final String POC_HOME_NOT_ASSIGNED = "0";

    public static enum PAY_TYPE {
        POSTPAID(0),
        PREPAID(1);

        int payType;

        PAY_TYPE(int type) {
            payType = type;
        }

        public int value() {
            return payType;
        }
    }

    public static enum SUBS_CLIENT_TYPE {
        UNKNOWN(0),
        HANDSET(1),
        DESKTOP(2),
        DISPATCH_CLIENT(3),
        POC_DONOR_RADIO(4),
        POC_WIFIONLY(5),
        THIRDPARTYPOCCLIENT(6),
        POC_NNI_Alias_MDN(7),
        POC_NNI_Group_MDN(8),
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

        int subsClientType;

        SUBS_CLIENT_TYPE(int type) {
            subsClientType = type;
        }

        public int value() {
            return subsClientType;
        }

    }

    public static enum DISPATCH_GROUP_MEMBER {
        NOT_MEMBER(0),
        MEMBER(1);

        int member;

        DISPATCH_GROUP_MEMBER(int grpMember) {
            member = grpMember;
        }

        public int value() {
            return member;
        }
    }

    public static enum POC_DONOR_RADIO {
        ENABLED(1),
        DISABLED(0);

        int status;

        POC_DONOR_RADIO(int type) {
            status = type;
        }

        public int value() {
            return status;
        }
    }

    public static enum THIRD_PARTY_POC_CLIENT {
        ENABLED(1),
        DISABLED(0);

        int status;

        THIRD_PARTY_POC_CLIENT(int type) {
            status = type;
        }

        public int value() {
            return status;
        }
    }

    public static enum BLK_UNSUPPORTED_DEVICES {
        ENABLED(1),
        DISABLED(0);

        int status;

        BLK_UNSUPPORTED_DEVICES(int type) {
            status = type;
        }

        public int value() {
            return status;
        }
    }

    public static enum CLIENT_TYPE_CONFIGURATION {
        ENABLED(1),
        DISABLED(0);

        int status;

        CLIENT_TYPE_CONFIGURATION(int type){status = type;}

        public int value(){return status;}

    }

    public static final boolean TRUE = true;
    public static final boolean FALSE = false;
    public static final int DEFAULT_MEDIA_INTRABURST_INTERVAL = 5;
    public static final int DEFAULT_NUM_OF_RETRIES = 3;


    public static final int DESKTOP_SWPKG_ID = 6;
    public static final int DISPATCH_SWPKG_ID = 7;
    public static final String WIN_DSKTP_UPGRADE_CHK_INTVL = "WIN_DSKTP_UPGRADE_CHK_INTVL";
    public static final String WIN_DISPATCH_UPGRADE_CHK_INTVL = "WIN_DISPATCH_UPGRADE_CHK_INTVL";
    public static final String WIN_DSKTP_UPGRADE_INFO_URL = "WIN_DSKTP_UPGRADE_INFO_URL";
    public static final String WIN_DISPATCH_UPGRADE_INFO_URL = "WIN_DISPATCH_UPGRADE_INFO_URL";
    public static final String WIN_DSKTP_UPGRADE_PKG_URL = "WIN_DSKTP_UPGRADE_PKG_URL";
    public static final String WIN_DISPATCH_UPGRADE_PKG_URL = "WIN_DISPATCH_UPGRADE_PKG_URL";
    public static final int HOUR_IN_SECONDS = 3600;

    public static final String POC_PTT_ID_KEY = "POC_PTT_ID";
    public static final String PR_PTT_ID_KEY = "PR_PTT_ID";
    public static final int POC_CARD_TYPE = 32;
    public static final int GEO_POC_CARD_TYPE = 33;
    public static final int PRESENCE_CARD_TYPE = 34;
    public static final int GEO_PRESENCE_CARD_TYPE = 35;
    //global pr in poc flag
    public static final String PR_IN_POC_ENABLED = "1";
    //poc level pr in poc flag
    public static final int ENABLE_PR_IN_POC = 1;

    public static final int DEFAULT_NOTIFY_SIZE = 4;
    public static final int XDMS_POC_CAPACITY_REACHED_ALARMCODE = 17602;
    public static final int XDMMANAGEDOBJECT_CLASSTYPE = 5;
    public static final String PROVISIONING = "PROVISIONING";
    public static final String PAM = "PAM";


    public static enum PAM_ACCOUNT_STATE {
        PROVISION(0),
        ACTIVE(2),
        SUSPEND(3),
        DELETE_IN_PROGRESS(4);

        int accountState;

        PAM_ACCOUNT_STATE(int type) {
            accountState = type;
        }

        public int value() {
            return accountState;
        }
    }
    
    public static final String UPDATE_CORP_HOME = "UPDATE_CORP_HOME";

    public static enum PARTITION_TYPE {
        MDN_BASED(1),
        LOAD_BASED(2);

        int partitionType;

        PARTITION_TYPE(int type) {
            partitionType = type;
        }

        public int value() {
            return partitionType;
        }
    }

    public static enum CORP_ACC_ANCHORING {
        DISABLED(0),
        ENABLED(1);

        int corpAccAnchoring;

        CORP_ACC_ANCHORING(int type) {
            corpAccAnchoring = type;
        }

        public int value() {
            return corpAccAnchoring;
        }
    }

    public static enum ALLOW_POC_PROV {
        DONT_ALLOW(0),
        ALLOW_PROV(1),
        ALLOW_ONLY_ANCH(2);

        int allowProv;

        ALLOW_POC_PROV(int type) {
            allowProv = type;
        }

        public int value() {
            return allowProv;
        }
    }

    public static final String CUSTOM_PROV_INVOKER = "CustomProvInvoker";
    public static final String CUSTOM_PROV_ACTION = "ACTION";
    public static final String CUSTOM_PROV_ADD_PAMSUBS_OP = "AddPAMSubs";
    public static final String CUSTOM_PROV_MODIFY_PAMSUBS_OP = "ModifyPAMSubs";
    public static final String CUSTOM_PROV_CANCEL_PAMSUBS_OP = "CancelPAMSubs";
    public static final String CUSTOM_PROV_VALIDATE_SUBS_OP = "ValidateSubscriber";
    public static final String CUSTOM_PROV_UPDATE_PAM_DETAILS_OP = "UpdatePAMDetails";
    public static final String CUSTOM_PROV_UPDATE_PAM_RATEPLAN_OP = "UpdatePAMRatePlan";
    public static final String CUSTOM_PROV_VALIDATE_UPDATE_SUBS_OP = "ValidateUpdateSubscriber";
    public static final String CUSTOM_PROV_CANCEL_PAMACCOUNT_OP = "CancelPAMAcc";
    public static final String CUSTOM_PROV_ADD_PAMACCOUNT_OP = "AddPAMAcc";
    public static final String CUSTOM_PROV_CREATE_FAN="CreateFan";
    public static final String CUSTOM_PROV_CREATE_BAN="CreateBan";
    public static final String CUSTOM_UPDATE_BULK_SUBSCRBAN="updateBulkSubscrBAN";
    public static final String CUSTOM_UPDATE_BAN_STATUS="UpdateBANStatus";
    public static final String CUSTOM_GET_BANMDNLIST="GetBanMdnList";
    public static final String CUSTOM_UPDATE_PAMACCFAN="UpdatePamAccFanId";
    public static final String CUSTOM_BAN_STATUS="banStatus";
    public static final String INT_FAN_ID = "INT_FAN_ID";
    public static final String INT_BAN_ID = "INT_BAN_ID";
    public static final String BLOCKED_BAN_STATUS="BlockedBan";
    public static final String UNBLOCKED_BAN_STATUS="UnBlockedBan";
    public static final String BAN_STATUS = "BAN_STATUS";


    public static final String CORP_ID = "CORP_ID";
    public static final String ACCOUNT_TYPE_INDICATOR = "ACCOUNT_TYPE_INDICATOR";
    public static final String RATE_PLAN = "RATE_PLAN";
    public static final String EXT_BAN_ID = "EXT_BAN_ID";
    public static final String EXT_FAN_ID = "EXT_FAN_ID";
    public static final String BAN_MDNLIST = "BAN_MDNLIST";
    public static final String OLD_CORP_ID = "OLD_CORP_ID";
    public static final String FAN_NAME = "FAN_NAME";
    public static final String BAN_NAME = "BAN_NAME";
    public static final String CORP_NAME = "CORPNAME";

    public static final String ERROR_CODE_INVALID_DTO_PASSED = "KNEC-MD30003";
    public static final String SUCCESS_CODE = "KNEC-MD00000";
    public static final String ERROR_CODE_INTERNAL_ERROR = "KNEC-MD30004";

    public static enum COUNT {
        INCREMENT("+"),
        DECREMENT("-");

        String delimiter;

        COUNT(String type) {
            delimiter = type;
        }

        public String value() {
            return delimiter;
        }
    }

    //PAM constants
    public static final String CUSTOM_PROV_VIEW_PAMACCOUNT_OP = "ViewPAMAcc";
    public static final String CUSTOM_UPDATE_BULKOPINFO_OP = "UpdateBulkOp";
    public static final String CUSTOM_GET_BULKOPINFO_OP = "GetBulkOp";
    public static final String CUSTOM_DELETE_BULKOPINFO_OP = "DeleteBulkOp";

    //Auto pair constants
    public static final int AUTO_PAIR_ENABLED = 1;

	public static final String CHANGE_MDN_EVENT_TYPE = "101";

    
    //TPMS constants
    public static final String DELETE_TP_ACCOUNT_SUCCESS = "Delete TP Account Success";
   
    public static enum IDEN_INTEROP {
        ENABLED(1),
        DISABLED(0);
        int status;
        IDEN_INTEROP(int type) {
            status = type;
        }

        public int value() {
            return status;
        }
    }

    public static enum SUBSCR_AUTH_STATUS {
        PROVISION(0),
        ACTIVE(2),
        SUSPEND(3);

        int status;

        SUBSCR_AUTH_STATUS(int type) {
            status = type;
        }

        public int value() {
            return status;
        }
    }

    public static enum NOTIFY_ON_ANY_MDN {
        BASE_MDN(0),
        PSEUDO_MDN(1);

        int notifyOnAnyMdn;

        NOTIFY_ON_ANY_MDN(int notifyOnAnyMdn) {
            this.notifyOnAnyMdn = notifyOnAnyMdn;
        }

        public int value() {
            return notifyOnAnyMdn;
        }
    }

    // DG.MICROSVCS_COMMONCONFIG PARAMNAME
    public static final String MAX_GROUP_PROFILES = "MAX_GROUP_PROFILES";
}