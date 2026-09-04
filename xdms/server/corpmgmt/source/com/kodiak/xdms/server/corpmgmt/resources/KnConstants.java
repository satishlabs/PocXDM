/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnConstants.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Jan 11, 2011      7.0
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
package com.kodiak.xdms.server.corpmgmt.resources;


import com.kodiak.dbmgr.KnDBConst;

import java.util.HashMap;
import java.util.Map;

public class KnConstants {

    public static final int SUCCESS = 0;
    public static final int FAILURE = 1;

    public static final int INITIAL_ETAG = 1;
    public static final int DEFAULT_GROUP_DIST_POLICY = 2; //NxN
    public static final int ABDG_GROUP_DIST_POLICY = 6; //NxN

    public static final String PRIVATE_GROUP_LIST_NAME = "PrivateGroupList";
    public static final String DEFAULT_CORP_PAIRING_LIST_NAME = "CorpPairingList";
    public static final int FILTER_TYPE_PAIRING_CONTACTS = 1;
    public static final int LIST_DIST_POLICY_NO_DIST = 0;
    public static final int LIST_TYPE_PRIVATE_GRPMEMLIST = 3;

    public static final String KEY_DATATYPE_GROUPNAME = "GROUPNAME";
    public static final String KEY_DATATYPE_MDN = "MDN";
    public static final String KEY_DATATYPE_MDNLIST = "MDNLIST";
    public static final String KEY_DATATYPE_ETAG = "ETAG";
    public static final String KEY_DATATYPE_SUBLIST_TYPE = "SUBLIST_TYPE";

    public static final int DIST_POLICY_PRIVATE_CONTACTLIST = 1;
    public static final int DIST_POLICY_SHARED_ANY_LIST = 3;
    public static final int DIST_POLICY_BROADCAST_GROUP = 4;
    public static final int DIST_POLICY_USER_PROFILE = 5;
    public static final int DIST_POLICY_COMMON_CONTACT_LIST_SUPPORT = 6;
    public static final int SUBLIST_TYPE_SHARED_LIST = 1;
    public static final int SUBLIST_TYPE_PRIVATE_CONTACTLIST = 2;

    public static final String CORP_LIST_TABLE = "DG.CORPLISTINFO";
    public static final String CORP_LIST_TABLE_PK = "CORPLISTID";
    public static final String CORP_GROUP_INFO_TABLE = "DG.CORPGROUPINFO";
    public static final String CORP_GROUP_INFO_TABLE_PK = "CORPGROUPID";
    public static final String CORP_OSM_INFO_TABLE="DG.CORPOSMINFO";
    public static final String CORP_OSM_INFO_TABLE_PK="OSMLISTID";
    public static final String CORP_GROUP_PROFILE_TABLE="DG.CORPGROUPPROFILE";
    public static final String CORP_GROUP_PROFILE_TABLE_PK="GRPPROFILEID";
    public static final String ADDED_MEMBERS = "ADDEDMEMBERS";
    public static final String DELTED_MEMBERS = "DELETEDMEMBERS";
    public static final int STANDARD_GROUP = 1;
    public static final int DISPATCH_GROUP = 2;
    public static final int BROADCAST_GROUP = 3;
    public static final int DISPATCH_CLIENT = 3;
    public static final int NORMAL_MEMBER = 0;
    public static final int SUPERVISOR = 1;
    public static final int DISPATCHER = 2;
    public static final int INTER_OP = 3;
    public static final int GROUP_MDN_CLIENT_TYPE= 8;
    public static final int BROADCAST_BROUP_BROADCASTER = 1;
    public static final int BROADCAST_BROUP_NON_BROADCASTER = 0;
    public static final int DEFAULT_OVERRIDE_DND = 1;
    public static final int OVERRIDE_DND_NOT_MODIFIED = -1;
    public static final boolean FALSE = Boolean.FALSE;
    public static final boolean TRUE = Boolean.TRUE;
    public static final int GROUP_DISPATCHER = 2;
    public static final int SUBSCRIPTION_TYPE_PUBLIC = 0;
    public static final int SUBSCRIPTION_TYPE_CORPORATE = 1;
    public static final int SUBSCRIPTION_TYPE_PUBLIC_CORP = 2;
    public static final int CLIENT_TYPE_UNKNOWN = 0;
    public static final int CLIENT_TYPE_HANDSET = 1;
    public static final int CLIENT_TYPE_DESKTOP = 2;
    public static final int CLIENT_TYPE_DISPATCH = 3;
    public static final int INTER_OP_CLIENT_TYPE = 4;
    public static final int WIFI_CLIENT_TYPE = 5;
    public static final int THIRD_PARTYPOC_CLIENT_TYPE = 6;
    public static final int CLIENT_TYPE_CROSSCARRIER = 10;
    public static final int PTTRADIOHANDSETCLIENT = 14;
    public static final int PTTRADIOCROSSCARRIERCLIENT = 15;
    public static final int PTTRADIOWIFIONLYCLIENT = 16;
    //public static final int MIN_GROUP_MEM = 2;
    public static final int NO_PRIVATE_LIST_ID = 0;
    public static final int AVATAR_NOT_MODIFIED = -1;
    public static final int EMERGENCY_INIT_NOT_MODIFIED = -1;

    public static final String DELETED = "DELETED";
    public static final String MODIFIED = "MODIFIED";
    public static final String PRIVATE_LIST_NAME = "PrivateContactList";
    public static final String INTERNAL = "INTERNAL";
    public static final String EXTERNAL = "EXTERNAL";
    public static final int EQUAL_TO_LIMIT = 0;
    public static final int LESS_THAN_LIMIT = -1;
    public static final int SHARED_LIST_TYPE = 1;
    public static final int MAX_LIMIT_VALIDATION_NOT_REQUIRED = -1;
    public static final int MAX_SUBLIST_SIZE = 10;
    public static final int MAX_BROADCAST_GROUP_PER_SUBSCRIBER = 100;
    public static final String MAX_BCG_PER_DISP = "MAX_BCG_PER_DISP";
    public static final KnDBConst.DataStores DUAL_DATA_STORE= KnDBConst.DataStores.XDM_SHARED_DATA;
    public static final int LOCWATCHER = 1;
    public static final String GRPMEM_LOC_SVC_ENABLED = "GRPMEM_LOC_SVC_ENABLED";
    public static final String CAT_ACCESS_CONTROL_FEATURE = "CAT_ACCESS_CONTROL_FEATURE";
    public static final String MAX_LOC_WATCHERS_PER_GRP = "MAX_LOC_WATCHERS_PER_GRP";
    public static final String BLK_EXT_CONT_LMT = "BLK_EXT_CONT_LMT";
    public static final String MAX_ABDG_COUNT_SYSTEM = "MAX_ABDG_COUNT_SYSTEM";
    public static final String MAX_ABDG_PER_GRPOWNER = "MAX_ABDG_PER_GRPOWNER";
    public static final String MAX_ABDG_PER_GRPMEMBER = "MAX_ABDG_PER_GRPMEMBER";
    public static final String MAX_LRGABDG_TALKGRP_PER_CORP = "MAX_LRGABDG_TALKGRP_PER_CORP";
    public static final String MAX_USR_PER_LRGABDG_TALKGRP = "MAX_USR_PER_LRGABDG_TALKGRP";
    public static final String MAX_STATUS_MSG_PER_OSM_LIST = "MAXSTATUSMSGPEROSMLIST";
    public static final String MAX_STATUS_SHORT_TEXT_LENGTH = "MAXSTATUSSHORTEXTLENGTH";
    public static final String MAX_STATUS_MSG_LENGTH = "MAXSTATUSMSGLENGTH";
    public static final String MAX_FORM_MSG_APPEND_LENGTH = "MAXFORMMSGAPPENDLENGTH";
    public static final String LOC_WATCHER_ENABLED_CONFIG_VALUE = "1";
    public static final String DEVICE_SHARING_FEATURE_FLAG = "DEVICE_SHARING_FEATURE_FLAG";
    public static final String TEMP_PASSWORD_EXPIRY = "TEMP_PASSWORD_EXPIRY";
    public static final int DONT_SEND_ACCOUNT_MAIL = 2;
    public static final String CORPADMINFSBULKAPILIMIT = "CORPADMINFSBULKAPILIMIT";
    public static final String OSMFEATUREFLAG = "OSMFEATUREFLAG";
    public static final String USER_PROFILE_MGMT = "USER_PROFILE_MGMT";
    public static final String MAX_USER_PROFILES ="MAX_USER_PROFILES";
    public static final String MAXMEM_NONAP_SUBLIST ="MAXMEM_NONAP_SUBLIST";
    public static final String MAX_USERPROFILES_PERSUB ="MAX_USERPROFILES_PERSUB";
    public static final String ALLOW_GROUP_ACROSS_ZONES ="ALLOW_GROUP_ACROSS_ZONES";
    public static final String VLARGE_GROUP_SUPPORTED ="VLARGE_GROUP_SUPPORTED";
    public static final String GROUP_PROFILE_MGMT = "GROUP_PROFILE_MGMT";
    public static final String MAX_GROUP_PROFILES = "MAX_GROUP_PROFILES";
    public static final String MAX_GROUPS_PER_CRI_CLIENT = "MAX_GROUPS_PER_CRI_CLIENT";
    public static final String MAX_DISP_IN_MCXGRP = "MAX_DISP_IN_MCXGRP";
    public static final String CHANNELPERZONE_VALIDATION ="CHANNELPERZONE_VALIDATION";
    public static final String DEF_TGSC_MODE = "DEF_TGSC_MODE";
    public static final String GROUP_SHARING_FLAG = "GROUP_SHARING_FLAG";
    public static final String USER_PROFILE_SHARING_FLAG = "USER_PROFILE_SHARING_FLAG";
    public static final String COMMON_CONTACTLIST_SUPPORT = "COMMON_CONTACTLIST_SUPPORT";
    public static final String MCX_GROUP_REGROUP_FLAG = "MCX_GROUP_REGROUP_FLAG";
    public static final String COMMON_CONTACTLIST_PERSUB = "COMMON_CONTACTLIST_PERSUB";
    public static final Integer USER_PROFILE_SHARING_FEATURE_FLAG = 1;
    public static final String IDLIST ="IDLIST";
    public static final String IDTYPE ="IDTYPE";
    public static final String MULTISITE_DEPLOYMENT_FLAG = "MULTISITE_DEPLOYMENT_FLAG";
    public static final String POCHOME_AUTOASSIGN_FLAG = "POCHOME_AUTOASSIGN_FLAG";

    public static final String MAX_PRECONFIG_GROUPS ="MAX_PRECONFIGURED_GROUPS";
    //public static final String MAX_PRECONFIG_GROUPS ="MAX_PRECONFIG_GROUPS";
    public static final String IS_PRECONFIG_GRP="IS_PRECONFIG_GRP";
    public static final int IS_PRECONFIG_GRP_ENABLE=1;
    public static final int IS_PRECONFIG_GRP_DISABLE=0;
    public static final int GROUP_REGROUP=111;

    public static final String MCS_LOGIN_MAIL_LINK = "MCS_LOGIN_MAIL_LINK";

    public static final String TRK_EMAIL_FLAG = "TRK_EMAIL_FLAG";
    public static final String CORP_HIERARCHY = "CORP_HIERARCHY";
    public static final String CORP_HIERARCHY_ENABLED = "2";
    public static final String HIERARCHY_SUPPORT_HORIZONTAL = "HIERARCHY_SUPPORT_HORIZONTAL";
    public static final String HIERARCHY_SUPPORT_VERTICAL = "HIERARCHY_SUPPORT_VERTICAL";
    public static final String ROOT_HIERARCHY_NAME = "ROOT_HIERARCHY_NAME";
    public static final String ROOT_HIERARCHY_ID = "ROOT_HIERARCHY_ID";
    public static final String IS_ADDING_CHILD = "IS_ADDING_CHILD";
    public static final String IS_REMOVING_CHILD = "IS_REMOVING_CHILD";
    public static final int DEFAULT_MAX_HIERARCHY_HORIZONTAL_LENGTH = 15;
    public static final int DEFAULT_MAX_HIERARCHY_VERTICAL_LENGTH = 5;
    public static final int HIERARCHY_BATCH_INSERT_SIZE = 100;

    public static final class ContactType {
    	public static final int EXTERNAL_SUBSCRIBER = 1;
    	public static final int NNI_SUBSCRIBER = 2;
    }
    //public static final KnDBConst.DataStores PRI_DATA_STORE =null;
    //Added for 7.8.1 NNI changes
    public static final int CONTACT_TYPE_EXTERNAL_CONTACT = 1;
    public static final int CONTACT_TYPE_EXTERNAL_SUBSCRIBER = 2;
    public static final int PROFILE_ID_KODIAK_EXTERNAL_CONTACT = 1;
	public static final String KEY_DATATYPE_MAX_EXT_SUBS_LIMIT="MAX_EXT_SUBS_LIMIT";
    public static final String KEY_DATATYPE_MAX_EXT_PROFILE_TYPE="EXT_PROFILE_TYPE";
    public static final String KEY_DATATYPE_MAX_EXT_PROFILE_FEATURESET="EXT_PROFILE_FEATURESET";
    public static final String KEY_DATATYPE_MAX_EXT_SUBS="MAX_EXT_SUBS";
    public static final String GROUP_NAME_MAP = "GROUPNAMEMAP";
    public static final int DB_ENABLED_VALUE = 1;
    public static final String EXTBROADCASTERFEATUREMAP = "EXTBROADCASTERFEATUREMAP";
    public static final String  CONFIGUREDPROFILEMAP = "CONFIGUREDPROFILEMAP";
    public static final String POC_NNI_INTERFACE_ENABLED ="POC_NNI_INTERFACE_ENABLED";
    public static final String GW_ENABLED_INDICATOR = "GW_ENABLED_INDICATOR";
    public static final String IDS_BLOCKED_CAT_CORP_LIST = "IDS_BLOCKED_CAT_CORP_LIST";
    public static final String XDMS_ALLOW_SUMDN_AS_CONTACT = "XDMS_ALLOW_SUMDN_AS_CONTACT";
    public static final String CONV_CLIENT_ENABLED = "CONV_CLIENT_ENABLED";
    public static final int INTERNAL_SUBSC_CLIENT_TYPE = 0;
    public static final int XCAP_DIFF_CONTACT_TYPE_EXTERNAL_CONTACT = 2;
    public static final int XCAP_DIFF_CONTACT_TYPE_EXTERNAL_SUBSCRIBER = 1;
    public static final int SG_MDN_MEMBER_TYPE = 1;
    public static final int SG_MDN_PATCH_MEMBER_TYPE = 2;
    public static final int LMR_INTEROP_NON_CAPABLE = 0;
    public static final int LMR_INTEROP_CAPABLE = 1;
    public static final int GROUP_MODIFY_PERMISSION = 1;
    public static final int DEFAULT_ZONE = 1;
    public static final String SIP = "sip:";
    public static final String DOT = ".";
    public static final String AT = "@";
	public static final Integer IS_ABDG_GROUP = 1;
	public static final int MCX_GROUP_INDICATOR = 1;
    public static final int EXTERNAL_SUBSCRIBER = 1;
    public static final int NOT_EXTERNAL_SUBSCRIBER = 0;
    public static final int PRECONFIG_GROUP = 1;

    public static final int DEFAULT_DISABLED_FLAG_VALUE = 0;
    public static final int CORP_GROUP_DEFAULT_SERVICE_TYPE = 7;
    public static final int CORP_GROUP_DEFAULT_ALLOWED_FEATURE = 7;
    public static final int GROUP_SHARING_ENABLE_INDICATOR = 1;
	public static final Integer USEPROFILEINDEX = 0;
	public static final String UPM_SHARING_FLAG_ENABLED="0";
    public static final String UGWINTEROP = "UGWINTEROP";
    public static final int UGWINTEROP_DBVALUE = 7;
    public static final int UGWINTEROP_DBVALUE_DISABLED = 0;
    public static final int LARGE_GROUP_DISABLED = 0;
    public static final int DEFAULT_AUTHORIZED_LARGE_TG_VALUE = 1;
    public static final int DEFAULT_VIDEO_PERMISSION_VALUE = 0;
    public static final int DEFAULT_VIDEO_PERMISSION_VALUE_FOR_BCG_GROUP = 1;
    //4RE get corp-resource-list details
    public static final String PROTOCOL_VERSION_22 = "22";
    public static final String INT_FAN_ID = "INT_FAN_ID";
    public static final String INT_BAN_ID = "INT_BAN_ID";

    //Added for 8.1.1 Changes
   // public static final String XDM_NXT_GEN_CAT_ENABLED = "XDMS_NEXT_GEN_CAT_ACCESS";

    public static final String NXT_GEN_CAT_ENABLED = "NXT_GEN_CAT_ENABLED";
    public static final String COMMON_CONTACTLIST_SIZE = "COMMON_CONTACTLIST_SIZE";
    public static final String PTT_RECORDING_FLAG = "PTT_RECORDING_FLAG";
    public static final String DATA_RECORDING_FLAG = "DATA_RECORDING_FLAG";
    public static final String VIDEO_RECORDING_FLAG = "VIDEO_RECORDING_FLAG";
    public static final String SELF_DND_FEATURE = "SELF_DND_FEATURE";
    public static final String LARGE_AGENCY_DISPATCH_FEATURE = "LARGE_AGENCY_DISPATCH_FEATURE";

    //Pam Constants
    public static final int MARKLIST_REQUEST_SIZE = 50;

    public static final String MSI_PRIMARY_WAVE_RECORDING_SERVER_IP = "MSI_PRIMARY_WAVE_RECORDING_SERVER_IP";
    public static final String MSI_PRIMARY_WAVE_RECORDING_SERVER_PORT = "MSI_PRIMARY_WAVE_RECORDING_SERVER_PORT";
    public static final String MSI_GEO_WAVE_RECORDING_SERVER_IP = "MSI_GEO_WAVE_RECORDING_SERVER_IP";
    public static final String MSI_GEO_WAVE_RECORDING_SERVER_PORT = "MSI_GEO_WAVE_RECORDING_SERVER_PORT";

    //subscriber contact
    public static final String NORMAL_CONTACT_COUNT = "NORMAL_CONTACT_COUNT";
    public static final String COMMON_CONTACT_COUNT = "COMMON_CONTACT_COUNT";
    public static final int NTFY_ON_ANY_MDN = 1;
    public static final String PREV_DIR_ETAG = "0";
    public static final String NEW_DIR_ETAG = "1";
    public static final String HIERARCHY_NAME_MAP = "HIERARCHY_NAME_MAP";
    public static final String DELETE_HIERARCHY_LIST = "DELETE_HIERARCHY_LIST";


    public static enum SUBSCR_CLIENT_TYPE {
        HANDSET(1),
        DESKTOP(2),
        DISPATCH_CLIENT(3),
        POC_DONOR_RADIO(4),
        POC_WIFIONLY(5),
        THIRDPARTYPOCCLIENT(6),
        ALIASMDN(7),
        GROUPMDN(8),
        CROSSCARRIER(10),
        PDVCONNECT(11),
        THIRDPARTYDISPATCHERCLIENT(12),
        MOBILEAPI(13),   
    	//LMR client type changes
    	PTTRADIOHANDSETCLIENT(14),
    	PTTRADIOCROSSCARRIERCLIENT(15),
    	PTTRADIOWIFIONLYCLIENT(16),
        SGMDNPATCH(17),
        DGMDN(18);

        int subsClientType;

        SUBSCR_CLIENT_TYPE(int type) {
            subsClientType = type;
        }

        public int value() {
            return subsClientType;
        }

        private static final Map<Integer, SUBSCR_CLIENT_TYPE> map = new HashMap<Integer, SUBSCR_CLIENT_TYPE>();

        static {
            for (SUBSCR_CLIENT_TYPE element : SUBSCR_CLIENT_TYPE.values()) {
                map.put(element.value(), element);
            }
        }

        public static SUBSCR_CLIENT_TYPE getValueOf(int value) {
            SUBSCR_CLIENT_TYPE client_TYPE =  map.get(value);
            return client_TYPE;
        }

    }


    public static enum CREATED_BY {
        CAT_ADMIN(0),
        DYNAMIC_CGMT_INTF(1),
        ABDG(2);

        int createdBy;

        CREATED_BY(int createdBy) {
            this.createdBy = createdBy;
        }

        public int value() {
            return createdBy;
        }
    }

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

    public static enum CB_MAPPING {
        NEED_TO_REMOVE(0),
        ETAG_CHANGE(1);

        int cbMapping;

        CB_MAPPING(int cbMapping) {
            this.cbMapping = cbMapping;
        }

        public int value() {
            return cbMapping;
        }
    }

    public static enum GRP_PROFILE_STATUS {
        ACTIVE(1),
        DELETE_IN_PROGRESS(2);

        int status;

        GRP_PROFILE_STATUS(int status) {
            this.status = status;
        }

        public int value() {
            return status;
        }
    }

	public static final Integer DEFAULT_GRP_TYPE = 1;
    public static final int MCPTT_COMPLIANCE = 1;

    public static enum GRP_MEMBER_PROPS_BITSET {
        SUPERVISOR(0),
        IS_BROADCASTER(1),
        CALL_INITIATE_PERMISSION(2),
        CALL_RECEIVE_PERMISSION(3),
        INCALL_PERMISSION(4),
        IS_LOCWATCHER(5),
        IS_OSMAUTHORIZED(6),
        VIDEO_CALL_INITIATE_PERMISSION(7),
        VIDEO_CALL_RECEIVE_PERMISSION(8),
        VIDEO_INCALL_PERMISSION(9);

        int propsBitSet;

        GRP_MEMBER_PROPS_BITSET(int type) {
            this.propsBitSet = type;
        }

        public int value() {
            return propsBitSet;
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

    public enum SORT_TYPE_BY_COLUMN {
        NONE(0),
        MDN(1),
        SUBSCRIBER_NAME(2);

        int sortType;

        SORT_TYPE_BY_COLUMN(int sortType) {
            this.sortType = sortType;
        }

        public int value() {
            return this.sortType;
        }
    }

}
