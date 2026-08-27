/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name: KnErrorCodes.java
 * Subsystem: PoC
 * <p/>
 * Name Date Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha Jan 11, 2011 7.0
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


public class KnErrorCodes {

    public static final String MODULE_NAME = "ERRORCODES";

    /**
     * Initializer class.
     */
    public static final class Initializer extends com.kodiak.xdms.server.common.resources.KnErrorCodes.ConfigInitializer {
        public static final String CORP_INIT_FAILED = "CORPLIB.Initializer.KNEC-CP16000";
    }

    /**
     * DAO class.
     */
    public static final class DAO extends com.kodiak.xdms.server.common.resources.KnErrorCodes.DAO {
        public static final String MAX_GRP_SIZE_REACHED = "DAO.KNEC-CP16012"; //Reached Maximum group size allowed limit
        public static final String MAX_DISPATCH_GROUP_LIMIT_EXCEEDED = "DAO.KNEC-CP16059";
        public static final String MAX_SUBSCRIBER_CONTACT_EXCEEDED = "DAO.KNEC-CP16025";
        public static final String MAX_BROADCAST_GRP_SIZE_REACHED = "DAO.KNEC-CP16104";
    }

    /**
     * BOEntity class.
     */
    public static final class BOEntity extends com.kodiak.xdms.server.common.resources.KnErrorCodes.BOEntity {
        //
        //
        public static final String PROFILE_DOES_NOT_EXIST = "CORPLIB.BOEntity.KNEC-CP16001";
        public static final String INVALID_POC_SUBSCRIBERS = "Validator.KNEC-CP16027";
        public static final String CONTACT_DOES_NOT_EXIST = "CORPLIB.BOEntity.KNEC-CP16002";
        public static final String SUBSCRIBER_DOES_NOT_BELONG_TO_CORP = "CORPLIB.BOEntity.KNEC-CP16034";
        public static final String ETXRENAL_CONTACT_NOT_FOUND_FOR_CORPORATE = "CORPLIB.BOEntity.KNEC-CP16043";

        public static final String SUBLIST_DOES_NOT_EXIST = "CORPLIB.BOEntity.KNEC-CP16004";
        public static final String SUBLISTS_LIST_DOES_NOT_EXIST = "CORPLIB.BOEntity.KNEC-CP16029";
        public static final String SUBLIST_DOES_NOT_BELONG_TO_CORP = "CORPLIB.BOEntity.KNEC-CP16038";
        public static final String NO_SUBLIST_DISTRIBUTION_FOUND = "CORPLIB.BOEntity.KNEC-CP16036";


        public static final String GROUP_DOES_NOT_EXIST = "CORPLIB.BOEntity.KNEC-CP16006";
        public static final String GROUP_MEMBER_ALREADY_EXISTS = "CORPLIB.BOEntity.KNEC-CP16008";
        public static final String GROUP_MEMBER_DOES_NOT_EXIST = "CORPLIB.BOEntity.KNEC-CP16009";
        public static final String GROUP_MEMBERS_LIST_DOES_NOT_EXIST = "CORPLIB.BOEntity.KNEC-CP16014";
        public static final String GROUPS_DOES_NOT_EXIST_FOR_CORP = "CORPLIB.BOEntity.KNEC-CP16014";
        public static final String NO_GROUP_EXISTS = "CORPLIB.BOEntity.KNEC-CP16010";
        public static final String NO_GROUP_EXISTS_FOR_SUBSCRIBER = "CORPLIB.BOEntity.KNEC-CP16015";

        public static final String NO_GROUPS_FOUND = "CORPLIB.BOEntity.KNEC-CP16010";
        public static final String INVALID_SUBSCRIPTION_TYPE = "BOEntity.KNEC-CP16044";

        public static final String NO_LICENSE_PROFILE_FOUND_FOR_BILLING_PROFILE = "BOEntity.KNEC-CP24001";
        public static final String NO_BILLING_PROFILE_FOUND_FOR_CORP_PROFILE = "BOEntity.KNEC-CP24002";
        public static final String INVALID_BILLING_NUMBER = "BOEntity.KNEC-CP24003";
        public static final String NO_LICENSE_SUBSCRIBER_FOUND_FOR_BILLING_PROFILE = "BOEntity.KNEC-CP24004";
        public static final String MARKLIST_REQUEST_SIZE_EXCEEDS_MAXIMUM_ALLOWED_PER_REQUEST = "BOEntity.KNEC-CP24005";
        public static final String UNMARKLIST_REQUEST_SIZE_EXCEEDS_MAXIMUM_ALLOWED_PER_REQUEST = "BOEntity.KNEC-CP24006";
        public static final String BILLING_PROFILE_NOT_FOUND = "BOEntity.KNEC-CP24007";
        public static final String MAX_LARGE_GROUP_PER_CORP_EXCEEDED = "CORPLIB.BOEntity.KNEC-CP16197";
        public static final String MAX_LARGE_BCGROUP_PER_CORP_EXCEEDED = "CORPLIB.BOEntity.KNEC-CP16198";
        public static final String MAX_LARGE_GROUP_PER_SYSTEM_EXCEEDED = "CORPLIB.BOEntity.KNEC-CP16199";
        public static final String DOC_NOT_MODIFIED = "CORPLIB.BOEntity.KNEC-CP16203";
        public static final String CAT_ACCESS_CONTROL_FEATURE_NOT_ENABLED = "CORPLIB.BOEntity.KNEC-CP16303";
        public static final String CAT_ACCESS_PER_VALUE_TS_IS_NOT_VALID = "CORPLIB.BOEntity.KNEC-CP16304";

    }

    /**
     * Validator class.
     */
    public static final class Validator extends com.kodiak.xdms.server.common.resources.KnErrorCodes.Validator {
        public static final String NON_FIRSTNET_FAN = "Validator.KNEC-CC16812";
        public static final String INCONTEXT_ID_NOT_IN_CORP = "Validator.KNEC-CC16802";
        public static final String GROUP_ALREADY_EXISTS = "Validator.KNEC-CP16016"; //Group name entered is duplicate
        public static final String MAX_GROUPS_REACHED = "Validator.KNEC-CP16011"; //Reached Maximum groups allowed limit
        public static final String MAX_GRP_SIZE_REACHED = "Validator.KNEC-CP16012"; //Reached Maximum group size allowed limit
        public static final String MAX_CONTACT_SIZE_REACHED = "Validator.KNEC-CP16013";
        public static final String MAX_GROUPS_PER_MEM_REACHED = "Validator.KNEC-CP16018";
        public static final String MIN_GROUP_MEMBER_LIMIT_ERROR = "Validator.KNEC-CP16017";
        public static final String MEMBERS_DOES_NOT_EXIST_FOR_GROUP = "Validator.KNEC-CP16019";
        public static final String SUBLIST_DOES_NOT_BELONG_TO_CORP = "Validator.KNEC-CP16003";
        public static final String SUBSCRIBER_NOT_PART_CORP_NOR_EXTERNAL = "Validator.KNEC-CP16020";
        public static final String EXTERNAL_CONTACT_CANNOT_BE_SUPERVISOR = "Validator.KNEC-CP16048";
        public static final String SUPERVISOR_DOES_NOT_EXIST_FOR_GROUP = "Validator.KNEC-CP16049";
        public static final String DISPATCHER_DOES_NOT_EXIST_FOR_GROUP = "Validator.KNEC-CP16050";
        public static final String MAXIMUM_DISPATCHER_EXCEEDED_FOR_DISPATCH_GROUP = "Validator.KNEC-CP16051";
        public static final String DISPATCHER_NOT_A_DISPATCH_CLIENT = "Validator.KNEC-CP16052";
        public static final String DISPATCHER_NOT_A_ALLOWED_FOR_STAND_GROUP = "Validator.KNEC-CP16053";
        public static final String EXTERNAL_CONTACT_CANNOT_BE_DISPATCHER = "Validator.KNEC-CP16054";
        public static final String DISPATCHER_SUBSCRIBER_CANNOT_BE_PART_OF_SUBLIST = "Validator.KNEC-CP16055";
        public static final String MAX_DISPATCH_GROUP_LIMIT_EXCEEDED = "Validator.KNEC-CP16059";
        public static final String DISPATCH_FEATURE_DISABLED = "Validator.KNEC-CP16061";
        public static final String MAX_DISPATCH_GROUP_MEM_LIMIT_EXCEEDED = "Validator.KNEC-CP16060";
        public static final String DISPATCH_MEMBER_PROPERTY_CANNOT_CHANGE = "Validator.KNEC-CP16062";
        public static final String DISPATCHER_SUBSCRIBER_CANNOT_BE_ADDED_AS_EXTERNAL_CONTACT = "Validator.KNEC-CP16064";
        public static final String INTEROP_SUBS_CANNOT_ADDED_IN_CONTACT = "Validator.KNEC-CP16065";
        public static final String INTEROP_SUBSC_NOT_A_INTEROP_CLIENT = "Validator.KNEC-CP16066";
        public static final String EXTERNAL_CONTACT_CANNOT_BE_INTEROP = "Validator.KNEC-CP16067";
        public static final String MULTIPLE_INTEROP_NOT_ALLOWED_FOR_GROUP = "Validator.KNEC-CP16068";
        public static final String INTEROP_SUBSC_CANNOT_BE_PART_OF_MULTIPLE_GRP = "Validator.KNEC-CP16069";
        public static final String INTEROP_MEMBER_PROPERTY_CANNOT_CHANGE = "Validator.KNEC-CP16070";
        public static final String GROUP_TYPE_CHG_NOT_ALLOWED = "Validator.KNEC-CP16071";
        public static final String REQUEST_THRESHOLD_LIMIT_REACHED = "Validator.KNEC-CP16074";
        public static final String ATLEAST_ONE_SHOULD_BE_PART_OF_CORP = "Validator.KNEC-CP16075";
        public static final String ACTIVATION_CODE_IS_INVALID = "Validator.KNEC-CP16076";
        public static final String SYSTEM_TGS_FEATURE_DISABLE = "Validator.KNEC-CP16077";
        public static final String SUBS_TGS_SERVER_BIT_DISABLE = "Validator.KNEC-CP16078";
        public static final String SUBS_TGSC_CLIENT_BIT_DISABLE = "Validator.KNEC-CP16100";
        public static final String OPERATION_NOT_ALLOWED_FOR_EXTERNAL_CONTACT = "Validator.KNEC-CP16079";
        public static final String MAX_GROUP_PER_SUBSCRIBER_LIMIT_REACHED = "Validator.KNEC-CP16080";
        public static final String GROUP_ALREADY_CAMPED = "Validator.KNEC-CP16081";
        public static final String GROUP_NOT_CAMPED_TO_SUBSCRIBER = "Validator.KNEC-CP16082";
        public static final String GROUP_DOES_NOT_EXIST = "Validator.KNEC-CP16006";
        public static final String INVALID_MODIFIED_CAMPED_GRP = "Validator.KNEC-CP16084";
        public static final String INVALID_REMOVED_CAMPED_GRP = "Validator.KNEC-CP16085";
        public static final String INVALID_PRIORITY_RANGE = "Validator.KNEC-CP16086";
        public static final String DUBLICATE_PRIORITY = "Validator.KNEC-CP16087";
        public static final String MAX_SCAN_LIST_SIZE_EXCEEDED = "Validator.KNEC-CP16089";
        public static final String TGS_CLIENT_SYSTEM_CONFIG_DISABLED  = "Validator.KNEC-CP16103";
        public static final String INVALID_SERVICE_STATUS = "Validator.KNEC-CP16100";
        public static final String TGS_SCAN_MODE_DISABLED  = "Validator.KNEC-CP16101";
        public static final String OPERATION_NOT_ALLOWED_FOR_NOT_LINKED_CORPORATE = "Validator.KNEC-CP16113";
        public static final String MEMBER_MDN_INVALID_CLIENT_TYPE = "Validator.KNEC-CP16083";
        public static final String GROUP_MDN_LIMIT_EXCEEDED ="Validator.KNEC-CP16111";
        public static final String GROUP_MDN_EXISTS_IN_GROUP ="Validator.KNEC-CP16112";
        public static final String INVALID_CHANNEL_RANGE = "Validator.KNEC-CP16118";
        public static final String DUPLICATE_CHANNEL = "Validator.KNEC-CP16119";
        public static final String MAX_CHANNEL_LIST_SIZE_EXCEEDED = "Validator.KNEC-CP16120";
        public static final String INVALID_CHANNEL = "Validator.KNEC-CP16121";
        public static final String INVALID_MODIFIED_CHANNEL_GRP = "Validator.KNEC-CP16122";
        public static final String INVALID_REMOVED_CHANNEL_GRP = "Validator.KNEC-CP16123";
        public static final String GROUP_ALREADY_CHANNELED = "Validator.KNEC-CP16124";
        public static final String LOCWATCHER_DISABLED_SYSTEM_LEVEL = "CORPLIB.BOEntity.KNEC-CP16127";
        public static final String INVALID_CLIENT_TYPE_FOR_LOCWATCHER = "CORPLIB.BOEntity.KNEC-CP16128";
        public static final String MAXIMUM_LOCWATCHER_EXCEEDED_FOR_GROUP = "Validator.KNEC-CP16130";
        public static final String SYSTEM_LEVEL_CONVERGED_CLIENT_FEATURE_DISABLED = "Validator.KNEC-CP16131";
        public static final String NO_CHANGE_IN_CLIENT_TYPE = "Validator.KNEC-CP16132";
        public static final String CLIENT_TYPE_DISABLED = "Validator.KNEC-CP16133";
        public static final String SUBSCRIBER_OTP_DOES_NOT_MATCH = "Validator.KNEC-CP16134";
        public static final String WEB_DISPATCHER_DISABLED_SYSTEM_LEVEL = "CORPLIB.BOEntity.KNEC-CP16135";
        public static final String USER_ID_EXISTS = "CORPLIB.BOEntity.KNEC-CP16136";
        public static final String DISPATCH_TYPE_NOT_SET = "CORPLIB.BOEntity.KNEC-CP16137";
        public static final String USER_ID_NOT_EXISTS = "CORPLIB.BOEntity.KNEC-CP16138";
        public static final String MAX_SG_PATCH_MDN_PER_GROUP_LIMIT_REACHED = "CORPLIB.BOEntity.KNEC-CP16139";
        public static final String SG_AND_SG_PATCH_MDN_CANNOT_BE_PART_OF_SAME_GROUP ="Validator.KNEC-CP16140";
        public static final String SUBSCRIBER_IS_NOT_USER_LICENSE_TYPE = "CORPLIB.BOEntity.KNEC-CP16161";
        public static final String ALIAS_MDN_EXISTS = "CORPLIB.BOEntity.KNEC-CP16162";
        public static final String SUBSCRIBER_IS_USER_LICENSE_TYPE = "CORPLIB.BOEntity.KNEC-CP16163";
        public static final String ALIAS_MDN_DOES_NOT_EXISTS = "CORPLIB.BOEntity.KNEC-CP16164";
        public static final String USER_ID_DOES_NOT_EXISTS = "CORPLIB.BOEntity.KNEC-CP16165";
        public static final String DB_PASSWORD_NULL_FOR_ACTIVATED_SUBSCRIBER = "BOEntity.KNEC-CP16166";
        public static final String ADDITIONAL_TALK_GROUP_NOT_ALLOWED_FOR_NON_BROADCASTER = "Validator.KNEC-CP16200";
        public static final String SUBSCRIBER_MCX_GROUP_NOT_VALID_MODIFY_REQUEST="Validator.KNEC-CP16238";
        public static final String SUBSCRIBER_MCX_GROUP_NOT_SG_MDN="Validator.KNEC-CP16239";
        public static final String ALIAS_MDN_DOES_NOT_BELONGS_TOSUBSMDN = "CORPLIB.BOEntity.KNEC-CP16262";
        public static final String SYSTEM_LEVEL_RECORDING_FLAG_DISABLED = "Validator.KNEC-CP16292";
        public static final String MAX_BROADCAST_GROUPS_PER_MEM_REACHED = "Validator.KNEC-CP16295";
		public static final String AUTHORIZED_LARGE_TG_FEATURE_DISABLED = "Validator.KNEC-CP16308";
        public static final String EMERGCONFIGTIMER_NOT_IN_RANGE = "Validator.KNEC-CP16309";
        public static final String EMERGCONFIGTIMER_FEATURE_DISABLED = "Validator.KNEC-CP16310";
        public static final String INVALID_GEOCODES = "Validator.KNEC-CP16311";
        public static final String CORPID_HIERARCHYID_MAP_NOT_EXISTS = "Validator.KNEC-CP16312";
        public static final String HIERARCHY_ID_MISMATCH = "Validator.KNEC-CP16313";

        //Contacts Management

        //Contact Mamnagement Contants
        public static final String SUBSCRIBER_DOES_NOT_BELONG_TO_CORP = "Validator.KNEC-CP16034";


        public static final String SOURCE_TARGET_MDN_DOES_NOT_BELONG_TO_SAME_CORPORATE_ID = "Validator.KNEC-CP16300";

        public static final String SOURCE_TARGET_MDN_DOES_NOT_BELONG_TO_SAME_TIER_PACKAGE = "Validator.KNEC-CP16301";

        public static final String SOURCE_TARGET_MDN_DOES_NOT_HAVE_COMPATIBLE_CLIENT_TYPE = "Validator.KNEC-CP16302";

        public static final String INVALID_SUBSCRIBER_CONTACTS = "Validator.KNEC-CP16021";//Not a valid contact for the subscriber
        public static final String SUBLIST_COUNT_EXCEEDED = "Validator.KNEC-CP16022";//sublist count has reached the max allowed limit
        public static final String INVALID_SUBSCRIBER_SUBLISTS = "Validator.KNEC-CP16023";//Invalid Sublist for the subscriber
        public static final String INVALID_EXTERNAL_SUBSCRIBERS = "Validator.KNEC-CP16024";
        public static final String MAX_SUBSCRIBER_CONTACT_EXCEEDED = "Validator.KNEC-CP16025";
        public static final String SAME_CORPORATE_MEMBERS_FOUND = "Validator.KNEC-CP16041";
        public static final String EXTERNAL_MEMBER_EXISTS_FOR_CORPORATION = "Validator.KNEC-CP16042";
        public static final String EXTERNAL_CONTACT_COUNT_EXCEEDED = "Validator.KNEC-CP16045";
        public static final String UNASSIGN_MDN_NOT_ASSGNED_TO_SUBLIST = "Validator.KNEC-CP16289";

        //Sublist
        public static final String INVALID_SUBLIST_TYPE = "Validator.KNEC-CP16038";
        public static final String SUBLIST_DOES_NOT_EXIST = "Validator.KNEC-CP16004";
        public static final String SUBLIST_NOT_MAPPED_TO_SUBSCRIBER = "Validator.KNEC-CP16026";
        public static final String INVALID_POC_SUBSCRIBERS = "Validator.KNEC-CP16027";
        public static final String SUBLIST_CONTACT_LIMIT_EXCEEDED = "Validator.KNEC-CP16028";
        public static final String SUBLIST_ALREADY_EXISTS = "Validator.KNEC-CP16030";
        public static final String SUBLIST_ALREADY_MAPPED = "Validator.KNEC-CP16031";
        public static final String INVALID_SUBLISTS_SUBSCRIBER_CONTACT = "Validator.KNEC-CP16037";
        public static final String MINIMUM_SUBLIST_MEM_LIMIT_NOT_STATISFIED = "Validator.KNEC-CP16047";
        public static final String COMMON_CONTACT_LIST_SUPPORT_FLAG_DISABLED = "Validator.KNEC-CP16284";
        public static final String COMMON_CONTACT_LIST_LIMIT_EXCEEDED = "Validator.KNEC-CP16285";
        public static final String AUTO_PAIRING_ENABLED = "Validator.KNEC-CP16286";
        public static final String COMMON_CONTACT_LIST_LIMIT_PER_SUB_EXCEEDED = "Validator.KNEC-CP16288";

        //common
        public static final String DOCUMENT_DOES_NOT_MATCH = "Validator.KNEC-CP16032";
        public static final String NO_CHANGE_IN_DOCUMENT_SINCE_LAST_FETCH = "Validator.KNEC-CP16033";
        public static final String INVALID_SUBSCRIPTION_TYPE = "Validator.KNEC-CP16044";
        public static final String MAX_CONTACT_PER_REQUEST_EXCEEDED = "Validator.KNEC-CP16058";
        public static final String UNAUTHORISED_CORPORATE = "Validator.KNEC-CP16046";
        public static final String INVALID_REQUEST = "Validator.KNEC-CP16238";
        public static final String VLG_SUBLIST_OPERATION_NOT_SUPPORTED = "Validator.KNEC-CP16240";
        public static final String VLG_GROUP_MEMBER_COUNT_EXCEED = "Validator.KNEC-CP16241";
        public static final String VLG_GROUP_MEMBER_MUST_BE_SG_OR_SGPATCH_MDN = "Validator.KNEC-CP16242";
        public static final String TOKEN_MCPTTID_NOT_MATCHED = "Validator.KNEC-CP16263";


        //Activation
        public static final String INVALID_SUBSCRIBERS_EMAIL = "Validator.KNEC-CP16056";
        public static final String SUBSCRIBER_IS_DEACTIVATED = "Validator.KNEC-CP16057";
        public static final String INVALID_CLIENT_TYPE = "Validator.KNEC-CP16063";
        public static final String ACTIVATION_CODE_MISSING = "Validator.KNEC-CP16114";
        public static final String SEND_MAIL_NOT_ALLOWED = "Validator.KNEC-CP16117";

        public static final String MDN_DOESNOT_BELONG_TO_GROUP = "Validator.KNEC-CP16039";
        public static final String MAX_FETCH_SIZE_EXCEED = "Validator.KNEC-CP16090";
        public static final String MAX_CORPORATE_SIZE_EXCEED = "Validator.KNEC-CP16091";
		public static final String INVALID_EXTERNAL_SUBS_TYPE = "Validator.KNEC-CP16092";
        public static final String CORP_EXT_CONTACT_BIT_DISABLED = "Validator.KNEC-CP16093";
        public static final String EXTERNAL_SUBS_COUNT_EXCEEDED = "Validator.KNEC-CP16094";
        public static final String CORP_CONTACT_TGS_BIT_DISABLED="Validator.KNEC-CP16095";
        public static final String CORP_EXT_CONTACT_SUPERVISOR_BIT_DISABLED="Validator.KNEC-CP16096";
        public static final String CORP_EXT_CONTACT_DISP_MEMBER_BIT_DISABLED="Validator.KNEC-CP16097";
        public static final String CORP_EXT_CONTACT_PRE_ARRANGD_GROUP_BIT_DISABLED="Validator.KNEC-CP16098";
        public static final String CORP_EXT_SUBLIST_ADD_BIT_DISABLED = "Validator.KNEC-CP16099";
        public static final String CORP_EXT_CONTACT_LOCWATCHER_BIT_DISABLED="Validator.KNEC-CP16129";
        //ERROR code added for broadcast group
        public static final String BROADCAST_GROUP_NOT_ALLOWED = "Validator.KNEC-CP16102";
        public static final String MAX_BROADCAST_GRP_SIZE_REACHED = "Validator.KNEC-CP16104";
        public static final String CORP_EXT_CONTACT_BROADCASTER_GROUP_BIT_DISABLED = "Validator.KNEC-CP16105";
        public static final String SUBSCRIBERS_BROADCASTER_FEATURE_DISABLED = "Validator.KNEC-CP16106";
        public static final String GROUP_BROADCASTER_NOT_EXIST = "Validator.KNEC-CP16107";
        public static final String MODIFIED_MEMBER_NOT_EXIST = "Validator.KNEC-CP16108";
        public static final String INVALID_BROADCASTER_COUNT = "Validator.KNEC-CP16109";
        public static final String BROADCAST_GROUP_SYSTEM_FEATURE_DISABLED = "Validator.KNEC-CP16110";
        public static final String INVALID_CLIENT_TYPE_FOR_ADD_EXTERNAL_CONTACT = "Validator.KNEC-CP16115";
        public static final String HANDSET_SUBSCRIBERS_MISSING = "Validator.KNEC-CP16116";
        public static final String BCGROUP_NOT_ALLOWED_IN_SCANLIST = "Validator.KNEC-CP16125";
        public static final String SUBS_NOT_A_BROADCASTER = "Validator.KNEC-CP16126";

        // MCPTT Error Code:
        public static final String AMBIENT_LISTENING_DISABLED_FOR_AUTHORIZED_MDN = "Validator.KNEC-CP16141";
        public static final String DISCRETE_LISTENING_DISABLED_FOR_AUTHORIZED_MDN = "Validator.KNEC-CP16142";
        public static final String USER_CHECK_DISABLED_FOR_AUTHORIZED_MDN = "Validator.KNEC-CP16143";
        public static final String USER_DISABLED_FOR_AUTHORIZED_MDN = "Validator.KNEC-CP16144";
        public static final String AMBIENT_LISTENING_FOR_TARGET_MDN = "Validator.KNEC-CP16145";
        public static final String USER_CHECK_DISABLED_FOR_TARGET_MDN = "Validator.KNEC-CP16146";
        public static final String AUTH_MDN_DOES_NOT_HAVE_TARGET_MDN_AS_CONTACT = "Validator.KNEC-CP16147";
        public static final String TARGET_MDN_DOES_NOT_HAVE_AUTH_MDN_AS_CONTACT = "Validator.KNEC-CP16148";
        public static final String SYSTEM_LEVEL_AMBIENT_LISTENING_FEATURE_DISABLED = "Validator.KNEC-CP16149";
        public static final String SYSTEM_LEVEL_DISCRETE_LISTENING_FEATURE_DISABLED = "Validator.KNEC-CP16150";
        public static final String SYSTEM_LEVEL_USER_CHECK_FEATURE_DISABLED = "Validator.KNEC-CP16151";
        public static final String SYSTEM_LEVEL_USER_SERVICE_FEATURE_DISABLED = "Validator.KNEC-CP16152";
        public static final String SYSTEM_LEVEL_EMERGENCY_FEATURE_DISABLED = "Validator.KNEC-CP16153";
        public static final String EMERGENCY_FEATURE_BIT_DISABLED = "Validator.KNEC-CP16154";
        public static final String DESTINATION_NOT_BELONGS_TO_SUBS_CONTACT_LIST = "Validator.KNEC-CP16155";
        public static final String BROADCAST_GROUP_NOT_ALLOWED_AS_DESTINATIONS = "Validator.KNEC-CP16156";
        public static final String SUBS_DOES_NOT_EXISTS_IN_PROVIDED_DESTINATION_GROUP = "Validator.KNEC-CP16157";
        public static final String PRIMARY_DESTINATION_IS_EMPTY = "Validator.KNEC-CP16158";
        public static final String REMOTE_EMERGENCY_DISABLED_FOR_AUTHORIZED_MDN = "Validator.KNEC-CP16159";
        public static final String REMOTE_EMERGENCY_DISABLED_FOR_TARGET_MDN = "Validator.KNEC-CP16160";
        public static final String EMERGENCY_DESTINATION_MAPPING_EXISTS_FOR_CONTACT ="Validator.KNEC-CP16187";
        public static final String EMERGENCY_DESTINATION_MAPPING_EXISTS_FOR_GROUP ="Validator.KNEC-CP16188";
        public static final String ADDITIONAL_TALK_GROUP_NOT_EXISTS ="Validator.KNEC-CP16189";
        public static final String ADDITIONAL_TALK_GROUP_EXITS ="Validator.KNEC-CP16190";
        public static final String MAX_ZONE_REACHED ="Validator.KNEC-CP16191";
        public static final String MAX_CHANNEL_REACHED ="Validator.KNEC-CP16192";
        public static final String SAME_ZONE_CHANNEL_CAN_NOT_BE_USED_TWICE ="Validator.KNEC-CP16193";
        public static final String DEST_TYPE_NOT_ALLOWED = "Validator.KNEC-CP16194";
        public static final String EMERGENCY_INIT_PERMISSION_ENABLED = "Validator.KNEC-CP16195";
        public static final String EMERGENCY_USER_IS_NOT_ACTIVATED = "Validator.KNEC-CP16202";
        public static final String MC_VIDEO_RX_DISABLED_FOR_AUTHORIZED_MDN = "Validator.KNEC-CP16216";
        public static final String MC_VIDEO_UNCONFIRMED_PULL_DISABLED_FOR_AUTHORIZED_MDN = "Validator.KNEC-CP16217";
        public static final String MC_VIDEO_UNCONFIRMED_PULL_RX_DISABLED_FOR_AUTHORIZED_MDN = "Validator.KNEC-CP16218";
        public static final String MC_VIDEO_TX_DISABLED_FOR_TARGET_MDN = "Validator.KNEC-CP16219";
        public static final String SYSTEM_LEVEL_MC_VIDEO_UNCONFIRMED_PULL_FEATURE_DISABLED = "Validator.KNEC-CP16220";
        public static final String MAX_REQUEST_LIMIT_EXCEEDS = "Validator.KNEC-CP16221";
        public static final String MC_DEVICE_DISABLED = "Validator.KNEC-CP16243";

        //Group Profile Management error codes
        public static final String GROUP_PROFILE_ALREADY_EXIST = "Validator.KNEC-CP16246";
        public static final String MAX_GROUP_PROFILE_SIZE_EXCEED = "Validator.KNEC-CP16247";
        public static final String MODIFY_GRPPROFILE_DATA_NOT_ALLOWED = "Validator.KNEC-CP16249";
        public static final String GROUPS_EXIST_FOR_PROFILE = "Validator.KNEC-CP16250";
        public static final String GROUP_NOT_ASSOCIATED_PROFILE = "Validator.KNEC-CP16251";
        public static final String GROUP_PROFILE_DOES_NOT_EXIST = "Validator.KNEC-CP16252";
        public static final String GROUP_PROFILE_FEATURE_NOT_ALLOWED = "Validator.KNEC-CP16253";

        public static final String MAX_GROUPS_PER_CRI_CLIENT_EXCEED = "Validator.KNEC-CP16248";

        public static final String EXTERNAL_GROUP_CONTACT_DESTINATION_NOT_ALLOWED = "Validator.KNEC-CP16299";

        // TP Dynamic group error codes
        public static final String TP_REQUEST_MDN_IS_NOT_THIRD_PARTY_SUBSCRIBER ="Validator.KNEC-CP16170";
        public static final String TP_GROUP_OWNER_IS_NOT_THIRD_PARTY_SUBSCRIBER ="Validator.KNEC-CP16171";
        public static final String TP_VEDNOR_ID_MISMATCH = "Validator.KNEC-CP16172";
        public static final String INVALID_TP_REQUEST_MDN = "Validator.KNEC-CP16173";
        public static final String TP_INVALID_MDN ="Validator.KNEC-CP16174";
        public static final String TP_INVALID_VENDOR ="Validator.KNEC-CP16175";
        public static final String TP_DELETE_GRP_PERMISSION_DENIED ="Validator.KNEC-CP16176";
        public static final String GROUP_MODIFY_PERMISSION_ENABLED_FOR_EXTERNAL_CONTACT = "Validator.KNEC-CP16201";

        // ABDG Group Error Code:
        public static final String MAX_ABDG_COUNT_EXCEEDED = "Validator.KNEC-CP16177";
        public static final String MAX_ABDG_COUNT_EXCEEDED_PER_OWNER = "Validator.KNEC-CP16178";
        public static final String MAX_ABDG_GROUPS_PER_MEM_REACHED = "Validator.KNEC-CP16179";
        public static final String GROUP_TYPE_NOT_ALLOWED = "Validator.KNEC-CP16180";
        public static final String GROUP_MEMBER_NOT_ALLOWED_TO_BE_PART_OF_ABDG_GROUP = "Validator.KNEC-CP16181";
        public static final String MDN_DO_NOT_HAVE_PERMISSIONS_TO_BECOME_GROUP_OWNER = "Validator.KNEC-CP16182";
        public static final String GROUP_OWNER_NOT_EXISTS_AS_ADDED_MEMBERS = "Validator.KNEC-CP16183";
        public static final String REQUESTED_GROUP_OWNER_NOT_MATCHED_WITH_EXISTING = "Validator.KNEC-CP16184";
        public static final String GROUP_OWNER_IS_NOT_A_DISPATCH_CLIENT ="Validator.KNEC-CP16185";
        public static final String GROUP_OWNER_CANNOT_BE_REMOVED ="Validator.KNEC-CP16186";
        public static final String COMMAND_PACKAGE_ASSIGNED = "Validator.KNEC-CP16204";
        public static final String SCAN_LIST_EXISTS = "Validator.KNEC-CP16205";
        public static final String GROUP_EXISTS = "Validator.KNEC-CP16206";
        public static final String INVALID_ZONE = "Validator.KNEC-CP16207";
        public static final String MAX_STATUS_MSG_PER_OSM_LIST_DB_EXCEEDED = "Validator.KNEC-CP16208";
        public static final String MAX_OSM_SHORT_TEXT_LENGTH_EXCEEDED = "Validator.KNEC-CP16209";
        public static final String MAX_OSM_MSG_LENGTH_EXCEEDED = "Validator.KNEC-CP16210";
        public static final String OSM_LIST_DEFAULT_LIMIT = "Validator.KNEC-CP16211";
        public static final String UNIQUE_OSM_INFO_LIST = "Validator.KNEC-CP16212";
        public static final String OSM_LIST_NAME_EXISTS = "Validator.KNEC-CP16213";
        public static final String OSM_LIST_ID_NOT_EXISTS = "Validator.KNEC-CP16214";
        public static final String OSM_AUTHORIZE_FB_DISABLED = "Validator.KNEC-CP16215";
        public static final String UNIQUE_OSM_INFO = "Validator.KNEC-CP16222";
        public static final String CORP_GROUP_ID_NOT_EXISTS = "Validator.KNEC-CP16261";
        public static final String SHARED_CORPORATE_OSM_NOT_ALLOWED = "Validator.KNEC-CP16267";
        public static final String COMMON_CONTACT_LIST_REJECTION = "Validator.KNEC-CP16269";
        public static final String GROUP_ASSOCIATED_WITH_PROFILE = "Validator.KNEC-CP16322";

        //UPM
        public static final String MAX_UP_EXCEEDED = "Validator.KNEC-CP16223";
        public static final String UP_AUTO_ASSIGN_DISABLED = "Validator.KNEC-CP16224";
        public static final String UNIQUE_UP_NAME = "Validator.KNEC-CP16225";
        public static final String GROUP_NOT_EXIST_CORP = "Validator.KNEC-CP16226";
        public static final String UP_NOT_EXIST_DB = "Validator.KNEC-CP16227";
        public static final String MAX_UP_PER_SUBS_EXCEEDED = "Validator.KNEC-CP16228";
        public static final String UPM_BIT_DISABLED = "Validator.KNEC-CP16229";
        public static final String UPM_NOT_EXISTS_IN_CORP = "Validator.KNEC-CP16230";
        public static final String UP_ASYNC_JOB_EXISTS = "Validator.KNEC-CP16231";
        public static final String UPM_ASSIGNED_TO_SUBS = "Validator.KNEC-CP16232";
        public static final String USER_PROFILE_IS_ALREADY_ASSIGNED = "Validator.KNEC-CP16236";

        //UPM SHARING
        public static final String USER_PROFILE_SHARING_DISABLED = "Validator.KNEC-CP16270";
        public static final String USER_PROFILE_SHARING_VALID_TRUSTMATRIX = "Validator.KNEC-CP16271";
        public static final String USER_PROFILE_SHARING_CORP_NOT_ALLOWED = "Validator.KNEC-CP16272";
        public static final String USER_PROFILE_SHARING_CORP_ID_NOT_FOUND = "Validator.KNEC-CP16273";
        public static final String USER_PROFILE_SHARING_CORP_ONLY_SUBSCRPTION_MDN = "Validator.KNEC-CP16274";
        public static final String USER_PROFILE_DELETE_NOT_ALLOWED_FOR_SHARED_CORP = "Validator.KNEC-CP16282";
        public static final String USER_PROFILE_SHARING_UNASSIGN_NOT_ALLOWED = "Validator.KNEC-CP16275";
        public static final String USER_PROFILE_SHARING_ASSIGN_NOT_ALLOWED = "Validator.KNEC-CP16283";
        public static final String USER_PROFILE_MODIFY_NOT_ALLOWED_FOR_SHARED = "Validator.KNEC-CP16287";

        public static final String ON_BOARDING_MAIL_NOT_REQUIRED = "Validator.KNEC-CP16233";

        //FAIRFAX
        public static final String GROUP_ACROSS_ZONES_NOT_ALLOWED = "Validator.KNEC-CP16234";
        public static final String ZONE_CHANNLE_ALREADY_ASSIGNED_TO_MDN = "Validator.KNEC-CP16235";
        public static final String SUBSCRIBER_IS_NOT_MCPTT_COMLIANCE = "Validator.KNEC-CP16237";
        public static final String SUBSCRIBER_MCX_GROUP_NOT_ALLOW_DISPATCHER_GROUP="Validator.KNEC-CP16240";
        public static final String SUBSCRIBER_MCX_CRI_MCPTT_FLAG_DISABLED="Validator.KNEC-CP16244";
        public static final String SUBSCRIBER_MCX_CRI_LOCFLAG_DISABLED="Validator.KNEC-CP16245";

        //Group sharing
        public static final String GROUP_SHARING_FEATURE_NOT_ALLOWED = "Validator.KNEC-CP16254";
        public static final String GROUP_SHARING_NONMCX_NOT_ALLOWED = "Validator.KNEC-CP16255";
        public static final String SHARED_CORPORATE_NOT_EXSITS = "Validator.KNEC-CP16256";
        public static final String GROUPS_NOT_ALLOWED_TOBE_SHARED = "Validator.KNEC-CP16257";
        public static final String GROUPS_NOT_ALLOWED_TOBE_SHARED_WITH_HIERARCHY = "Validator.KNEC-CP16320";
        public static final String GROUP_MEMBER_PROPERTY_NOT_ALLOWED = "Validator.KNEC-CP16258";
        public static final String GROUP_SHARED_FLAG_MODIFY_NOT_ALLOWED = "Validator.KNEC-CP16259";
        public static final String GROUP_SHARED_SUBLIST_ADD_SHARED_CORP_NOT_ALLOWED = "Validator.KNEC-CP16260";
        public static final String EXTERNAL_CONTACT_NOT_ALLOWED_FOR_SHARED_CORP = "CORPLIB.BOEntity.KNEC-CP16263";
        public static final String TRANSACTION_ID_DOES_NOT_BELONGS_TO_CORP = "CORPLIB.BOEntity.KNEC-CP16264";
        public static final String MAXIMUM_DISPATCHER_EXCEEDED_FOR_MCX_GROUP = "Validator.KNEC-CP16265";
        public static final String LAST_MEMBER_SUBLIST_UPM_MAP_CHECK = "Validator.KNEC-CP16268";
        public static final String USER_PROFILE_DOESNT_BELONG_TO_MDN = "Validator.KNEC-CP16266";

        //Group Regroup
        public static final String PRECONFIG_GROUP_FEATURE_IS_NOT_ALLOWED = "Validator.KNEC-CP16276";
        public static final String MAX_PRE_CONFIG_GROUPS_REACHED = "Validator.KNEC-CP16277";
        public static final String UPDATE_PRECONFIG_GROUP_PARAM_IS_NOT_ALLOWED = "Validator.KNEC-CP16278";
        public static final String UPDATE_PRECONFIG_GROUP_MEMBER_IS_NOT_ALLOWED = "Validator.KNEC-CP16279";
        public static final String PRECONFIG_GROUP_IS_NOT_ALLOWED = "Validator.KNEC-CP16280";

        //UGW Config
        public static final String UGW_CONFIG_NOT_EXISTS = "Validator.KNEC-CP16281";

        public static final String UGWINTEROP_CHANGE_SHARED_CORP_NOT_ALLOWED = "Validator.KNEC-CP16290";

        public static final String DONT_SEND_MAIL_NOT_ALLOWED = "Validator.KNEC-CP16291";

        public static final String MAILTYPE_MISS_IN_REQ_FOR_CRI = "Validator.KNEC-CP16293";

        public static final String CONFIGURATIONS_NOT_VALID_FOR_RECORDING = "Validator.KNEC-CP16296";

        public static final String ALL_CORP_NOT_LARGE_GROUP_ENABLED = "Validator.KNEC-CP16297";

        public static final String SYSTEM_LEVEL_SELF_DND_PRIVILEGE_FLAG_DISABLED = "Validator.KNEC-CP16298";

        public static final String ERROR_CODE_UPDATE_USER_PROFILE_IN_PROGRESS = "Validator.KNEC-CP16300";

        public static final String SYSTEM_LEVEL_LARGE_AGENCY_DISPATCH_FLAG_DISABLED = "Validator.KNEC-CP16305";

        public static final String MORE_THAN_MAX_ALLOWED_CORP_GROUP = "Validator.KNEC-CP16306";

        public static final String SUBSCRIBER_NOT_A_DISPATCH_CLIENT = "Validator.KNEC-CP16307";


        //Hierarchy
        public static final String MULTIPLE_PARENTS_NODES_NOT_ALLOWED = "Validator.KNEC-CP16311";
        public static final String HIERARCHY_DEPTH_EXCEEDED = "Validator.KNEC-CP16312";
        public static final String HIERARCHY_LENGTH_EXCEEDED = "Validator.KNEC-CP16313";
        public static final String IDNAME_ALREADY_EXISTS = "Validator.KNEC-CP16315";
        public static final String GEOCODE_NOT_CONFIGURED_IN_THE_SYSTEM = "Validator.KNEC-CP16318";
        public static final String PARENT_CHILD_HIERARCHY_NOT_SAME = "Validator.KNEC-CP16319";
        public static final String ROOT_HIERARCHY_MODIFICATION_NOT_ALLOWED = "Validator.KNEC-CP16321";
        public static final String SUBSCRIBER_ASSOCIATE_WITH_GEOCODE = "Validator.KNEC-CP16323";


        // PTT Setting
        public static final String UNIQUE_PTT_SETTING_NAME = "Validator.KNEC-CP16311";
        public static final String PTT_SETTING_DOC_NOT_VALID = "Validator.KNEC-CP16312";
        public static final String DELETE_PTT_TEMPLATE_NOT_ALLOWED = "Validator.KNEC-CP16313";
        public static final String PTT_TEMPLATE_ALREADY_ASSIGNED_TO_CORP = "Validator.KNEC-CP16314";
        public static final String PTT_TEMPLATE_ALREADY_SET_AS_DEFAULT = "Validator.KNEC-CP16315";
        public static final String PTT_TEMPLATE_ASSIGNED_TO_SUBSCRIBERS = "Validator.KNEC-CP16316";
        public static final String INVALID_INPUT = "Validator.KNEC-CP16317";
    }

    /**
     * Authenticator class
     */
    public static final class Authenticator extends com.kodiak.xdms.server.common.resources.KnErrorCodes.Authenticator {
    }

    /**
     * Authorizer class.
     */
    public static final class Authorizer extends com.kodiak.xdms.server.common.resources.KnErrorCodes.Authorizer {
        public static final String MDN_DOESNOT_BELONG_TO_GROUP = "Authorizer.KNEC-CP16039";
        public static final String MDN_DOESNOT_BELONG_TO_CORP = "Authorizer.KNEC-CP16040";
        public static final String INVALID_SUBSCRIPTION_TYPE = "BOEntity.KNEC-CP16044";
        public static final String UNAUTHORISED_CORPORATE = "Authorizer.KNEC-CP16046";
    }
}
