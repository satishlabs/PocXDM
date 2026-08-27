/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnOperationTypes.java
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


public class KnOperationTypes {

    public static final String AUTHENTICATE = "authenticate";
    public static final String GET_SUBS_DIR = "getSubsDirectory";
    public static final String UPDATE_SUBSCRIBER = "updateSubscriber";
    public static final String FORCE_SYNC = "forceSync";
    public static final String DELETE_SUBSCRIBER = "deleteSubscriber";
    public static final String CHANGE_MDN = "changeMdn";
    public static final String CREATE_SUBSCRIBER = "createSubscriber";

    public static final String PAIR_CORP_CONTACT = "pairCorpContact";
    public static final String GET_MASTER_LIST = "getMasterList";
    public static final String GET_CORP_RESOURCE_LIST = "getCorpResourceList";
    public static final String GET_SUBS_CONTACT_LIST = "getSubsContactList";
    public static final String MODIFY_SUBS_CONTACT_LIST = "modifySubsContactList";

    public static final String CLONE_SUBS_CONTACT_LIST = "cloneSubsContactList";

    public static final String UPM_MODIFY_SUBS_CONTACT_LIST = "upmModifySubsContactList";
    public static final String PUSH_SUBLISTS = "pushSublists";
    public static final String REMOVE_SUBLIST = "removeSublist";
    public static final String ADD_EXT_CONTACTS = "addExternalContacts";
    public static final String GET_EXT_CONTACTS = "getExternalContacts";
    public static final String REMOVE_EXT_CONTACTS = "removeExternalContacts";
    public static final String MODIFY_EXT_CONTACTS = "modifyExternalContacts";
    public static final String GET_SUBSCR_REVERSE_CONTACTS = "getSubscrReverseContacts";
    public static final String GET_CORP_EXT_CONTACTS = "getCorpExternalContacts";
    public static final String GET_CORP_PROFILE_BY_ENTITIES = "getCorpProfileByEntities";

    public static final String CREATE_SUBLIST = "createSublist";
    public static final String MODIFY_SUBLIST = "modifySublist";
    public static final String GET_SUBLIST_DETAILS = "getSublistDetails";
    public static final String GET_ALL_SUBLISTS = "getAllSublist";
    public static final String GET_DIST_LIST = "getDistList";
    public static final String DELETE_SUBLIST = "deleteSublist";
    public static final String GET_SUBSCR_SUBLISTS = "getSubscrSublists";
    public static final String ASSIGN_COMMON_CONTACT_LIST = "assignCommonContactList";
    public static final String UNASSIGN_COMMON_CONTACT_LIST = "unassignCommonContactList";
    public static final String GET_DEVICE_DETAILS = "getDeviceDetails";
    public static final String GET_GROUP_STATS = "getGroupStats";

    public static final String CREATE_GROUP = "createGroup";
    public static final String MODIFY_GROUP = "modifyGroup";
    public static final String UPM_MODIFY_GROUP = "upmModifyGroup";
    public static final String DELETE_GROUP = "deleteGroup";
    public static final String GET_GROUP_LIST = "getGroupList";
    public static final String GET_SUBSGROUP_LIST = "getSubsGroupList";
    public static final String GET_GROUP_DETAILS = "getGroupDetails";
    public static final String GET_SUBS_GROUP_DETAILS = "getSubsGroupDetails";
    public static final String ASSIGN_CONTACT = "assignContact";
    public static final String GET_SUBS_EMAIL = "getSubsEmailId";
    public static final String SAVE_ACT_CADE = "saveActCode";
    public static final String GENEARTE_ACTIVATION_CODES = "generateActivationCodes";
    public static final String GET_EMAIL_INFO = "getMailInfo";
    public static final String SEND_ACTIVATION_MAIL = "sendActivationMail";
    public static final String DELETE_TALK_GROUP_ENTRIES = "deleteTGEntries";
    public static final String MODIFY_TALK_GROUP_SELECT = "assignUnAssignCampGroups";
    public static final String ASSIGN_TALK_GROUP_SELECT = "assignCampGroup";
    public static final String GET_UNUSED_MDN_LIST = "getUnusedMdnList";
    public static final String CLEAN_CORP_DATA = "cleanCorpData";
    public static final String MODIFY_SUBS_CORP_FEATURE = "modifySubsCorpFeature";
    public static final String MODIFY_SUBSCRIBER_SCAN_LIST = "modifySubscriberScanList";
    public static final String UPM_MODIFY_SUBSCRIBER_SCAN_LIST = "upmModifySubscriberScanList";
    public static final String MODIFY_SUBSCRIBER_SCAN_AND_CHANNEL_LIST = "modifySubscriberScanAndChannelList";
    public static final String MODIFY_SUBSCRIBER_SCAN_LIST_XCAP = "modifySubscriberScanListXcap";
    public static final String GET_SUBSCRIBER_SCAN_LIST = "getSubscriberScanList";
    public static final String GET_SUBSCRIBER_SCAN_LIST_XCAP = "getSubscriberScanListXcap";
    public static final String CLEANUP_SUBS_CAMPED_GRPS = "cleanUpSubsCampedGrps";
    public static final String DELETE_SUBS_CAMPED_GRPS = "deleteScanList";
    public static final String MODIFY_SUBSCRIBER_SCAN_LIST_XCAP_CLIENTS = "modifySubscriberScanListXcapClients";
    public static final String CREATE_BROADCAST_GROUP = "createBCGroup";
    public static final String CREATE_VLG_BROADCAST_GROUP = "createVLGBCGroup";
    public static final String GET_BASIC_GROUP_INFO = "getBasicGrpInfo";
    public static final String MODIFY_BROADCAST_GROUP = "modifyBCGroup";
    public static final String UPM_MODIFY_BROADCAST_GROUP = "upmModifyBCGroup";
    public static final String GET_POC_LINKED_GROUP_LIST = "getPoCLinkedGroupList";
    public static final String REMOVE_SUBSCRIBERS_CONTACTS = "removeSubscribersContacts";
    public static final String REMOVE_SUBSCRIBERS_ALL_SUBLIST = "removeSubscribersAllSublist";
    public static final String REMOVE_SUBSCRIBERS_ALL_GROUPS = "removeSubscribersAllGroups";
    public static final String GET_SUBS_GROUP_DETAILS_XDMDATA_INTF = "getSubsGroupDetailsXDMDataIntf";
    public static final String GET_SUBSGROUP_LIST_XDMDATA_INTF = "getSubsGroupListXDMDataIntf";
    public static final String GET_CORPORATE_PROFILE_XDMDATA_INTF = "getCorporateProfileXDMDataIntf";
    public static final String GET_TARGET_PERMISSION_XDMDATA_INTF = "getTargetPermissionsXDMDataIntf";
    public static final String MODIFY_SUBSCRIBER_TG_LIST = "modifySubscriberTGList";
    public static final String UPM_MODIFY_SUBSCRIBER_TG_LIST = "upmModifySubscriberTGList";
    public static final String GET_SUBSCRIBER_TG_LIST = "getSubscriberTGList";
    public static final String GET_SUBS_TG_LIST_XCAP = "getSubsTGListXCAP";
    public static final String DELETE_TG_LIST = "deleteTGList";
    public static final String MODIFY_BULK_GROUP_PROPERTIES = "modifyBulkGroupProperties";

    public static final String GET_SUBSGROUP_LIST_DYANMIC_INTF = "getSubsGroupListDynamic";
    public static final String GET_SUBS_GROUP_DETAILS_DYNAMIC_INTF = "getSubsGroupDetailsDynamic";

    public static final String GET_BILLING_MDNS = "getAllBillingMdns";
    public static final String GET_LICENSE_SUBS = "getLicenseSubscribers";
    public static final String GET_LICENSE_SUBS_CSR = "getLicenseSubscribersForCSR";
    public static final String MARK_SUBS_FOR_DELETION = "markSubsForDeletion";
    public static final String UPDATE_BILLING_NAME = "updateBillingName";
    public static final String UPDATE_CORP_AUTO_PAITING = "updateCorpAutoPairing";
    public static final String ADD_TO_PAIRINF_LIST = "addToPairingList";

    public static final String GET_ALL_CORP_SUBSCR_FEATURESET = "getAllCorpSubscrFeatureSets";
    public static final String UPDATE_SUBSCR_CORPADMIN_FEATURESET = "updateCorpAdminFS";

    public static final String CLONE_SUBSCR_CORPADMIN_FEATURESET = "cloneCorpAdminFS";
    public static final String GET_ACTIVATION_CODES = "getActivationCode";
    public static final String GET_CORP_SUBS_DETAILS = "getSubscrDetails";
    public static final String UPDATE_CORP_SUBSCRIBER = "updateCorpSubscriber";
    public static final String GET_CORP_SUBS_USER_PROFILE = "getCorpSubsUserProfile";
    public static final String RESET_CORP_SUBS_USER_PASSOWRD = "resetCorpSubsUserPassword";
    public static final String RESEND_CORP_SUBS_VERIFICATION_EMAIL = "resendCorpSubsVerificationEmail";
    public static final String UPDATE_SUBS_ALIAS_ENTITIES = "updateSubsAliasEntities";
    public static final String GENERATE_TEMP_PASSWORD = "generateTempPassword";
    public static final String SEND_TEMP_PASSWORD = "sendTempPassword";
    public static final String VALIDATE_SUBS_CLIENT_SETTINGS = "setSubscrClientSettings";
    public static final String VALIDATE_GET_SUBS_CLIENT_SETTINGS = "getSubscrClientSettings";
    public static final String GET_SUBSCR_ACTIVATION_CODE = "getSubscrActivationCode";
    public static final String GENERATE_OTP = "generateOTP";
    public static final String VALIDATE_OTP = "validateOTP";
    public static final String GET_CORP_BAN_FAN_DETAILS = "getCorpBanFanDetails";
    public static final String SWITCH_CONVERGED_CLIENT = "switchConvergedClient";
    public static final String GET_REST_SUBSCR_ACTIVATION_CODE = "generateActivationCodeIDMIntf";
    public static final String GET_TMP_PWD_FOR_LEGACY = "getTmpPwdForLegacy";
    public static final String GET_CORPORATE_FEATURESET = "getCorporateFS";
    public static final String MODIFY_CORPORATE_FEATURESET = "updateCorporateFS";
    public static final String MODIFY_CORPORATE_PER_SET = "setCATAccessPermission";

    public static final String SET_TARGET_PERMISSION = "setTargetPermissions";
    public static final String UPM_SET_TARGET_PERMISSION = "upmSetTargetPermissions";
    public static final String GET_TARGET_PERMISSION = "getTargetPermissions";
    public static final String GET_AUTH_USER_LIST = "getAuthorizedMdnList";
    public static final String SET_SUBS_EMERGENCY_ATTRIBUTES = "setSubsEmergencyAttributes";
    public static final String UPM_SET_SUBS_EMERGENCY_ATTRIBUTES = "upmSetSubsEmergencyAttributes";
    public static final String GET_SUBS_EMERGENCY_ATTRIBUTES = "getSubsEmergencyAttributes";
    public static final String GET_USER_EMERGENCY_DESTINATION = "getUserEmergDest";
    public static final String GET_SUBS_EMERGENCY_DETAILS = "getSubsEmergencyDetails";
    public static final String GET_ALL_AUTH_USER_LIST = "getAllAuthorizedMdnList";

    public static final String ADD_BULK_GROUPS_TO_SUBSCRIBER = "addBulkGroupsToSubscriber";
    public static final String CREATE_SUBS_ATG_SCAN_LIST = "createSubsATGScanList";
    public static final String CLONE_CONTACTS_GROUP_FEATURES_VALIDATION = "cloneContactsGroupsAndFeatures";
    public static final String CREATE_OSM_LIST = "createOSMList";
    public static final String UPDATE_OSM_LIST = "updateOSMList";
    public static final String DELETE_OSM_LIST = "deleteOSMList";
    public static final String GET_OSM_LIST = "getOSMList";
    public static final String GET_OSM_LIST_DETAILS = "getOSMListDetails";
    public static final String ASSIGN_OSM_TO_GROUP = "assignOSMToGroup";
    public static final String GET_OSM_GROUP_LIST = "getOSMGroupList";

    public static final String GET_MOBILE_SYNC_LOC_SUPERVISORS = "getMobileSyncLocSupervisors";

    public static final String CREATE_USER_PROFILE= "createUserProfile";
    public static final String UPDATE_USER_PROFILE= "updateUserProfile";
    public static final String UPDATE_CB_USER_PROFILE= "updateCBUserProfile";
    public static final String DELETE_USER_PROFILE= "deleteUserProfile";
    public static final String GET_USER_PROFILE_DETAILS= "getUserProfileDetails";
    public static final String GET_USER_PROFILE_LIST= "getUserProfileList";
    public static final String GET_USER_PROFILE_LIST_BY_NAME= "getUserProfileListByName";
    public static final String GET_SUBSCRIBER_USER_PROFILE_LIST= "getSubscriberUserProfileList";
	public static final String ASSIGN_USER_PROFILE= "assignUserProfile";

    public static final String GET_USER_PROFILE_SUBSCRIBERLIST= "getUserProfileSubscriberList";
    public static final String UPDATE_DEFAULT_USER_PROFILE= "updateDefaultUserProfile";
    public static final String GET_PROFILE_MDN_BY_UPM_ID= "getProfileMdnByUPId";
	public static final String UNASSIGN_USER_PROFILE = "unAssignUserProfile";
    public static final String UPDATE_IMPACTED_TU_PERMS = "updateImpactedTuPerms";
    public static final String USER_PROFILE_NOTIFICATION = "userProfileNotification";

    public static final String GET_GRP_MEMBERSHIP_DETAILS = "getGroupMemberShipDetails";
    public static final String MODIFY_MCX_GROUP = "modifyMcXGroup";
    public static final String CREATE_BULK_CORP_GROUP = "createBulkCorpGroup";
    public static final String GET_PROFILE_CORP_GROUP_LIST = "getProfileGroupList";
	public static final String CREATE_CORP_GROUP_PROFILE = "createCorpGroupProfile";
    public static final String GET_GROUP_PROFILE_LIST = "getGroupProfileList";
    public static final String GET_GROUP_PROFILE_DETAILS = "getGroupProfileDetails";
    public static final String SEARCH_GROUP_PROFILE = "searchGroupProfile";
    public static final String MODIFY_GROUP_PROFILE = "modifyGroupProfile";
    public static final String DELETE_GROUP_PROFILE_GROUP_LIST = "deleteGrpProfileGroupList";
    public static final String DELETE_GROUP_PROFILE = "deleteGroupProfile";
    public static final String GET_CORP_SHARED_TRUST_MATRIX = "getCorpSharedTrustMatrix";
    public static final String UPDATE_CORP_TRUST_MATRIX = "updateCorpTrustMatrix";
    public static final String DELETE_CORP_TRUST_MATRIX = "deleteCorpTrustMatrix";
    public static final String GET_CORP_GRP_LMR_EXTN = "getCorpGrpLmrExtn";
    public static final String MODIFY_GROUPS_UGW_CONFIG = "modifyGroupsUGWConfig";

    public static final String SEND_TRK_MATERIAL = "sendTrkMaterial";

    public static final String GET_ASYNC_OP_STATUS="getAsyncOpStatus";

    public static final String GET_MDN_AUTHORIZATION_FOR_GROUPID="getMdnAuthorizationForGroupId";

    public static final String GET_DEVICE_LIST = "getDeviceList";

    public static final String GET_DEVICE_STATS = "getDeviceStats";

    public static final String GET_SUBSCRIBER_STATS = "getSubscriberStats";

    public static final String GET_GROUP_DETAILS_WITHOUT_MEMBERS = "getGroupsDetailsWithoutMembers";

    public static final String ASSIGN_UPM_MODIFY_SUBS_CONTACT_LIST = "assignUserProfileModifySubsContactList";

    public static final String SET_TARGET_PERMISSION_ASSIGNUPM = "SetTargetPermissionsAssignUPM";
    public static final String GET_BULK_BASIC_GROUP_INFO = "getBulkBasicGrpInfo";


    public static final String CREATE_HIERARCHY = "createHierarchy";

    public static final String MODIFY_HIERARCHY = "modifyHierarchy";

    public static final String GET_REGIONS = "getRegions";

    public static final String ALLOCATE_SUBSCRIBER = "allocateSubs";

    public static final String UNALLOCATE_SUBSCRIBER = "unAllocateSubs";

    public static final String GROUP_REHOME = "groupRehome";


    public static final String CREATE_CORP_PTTSETTING_DOC= "createPTTSettingDoc";
    public static final String DELETE_CORP_PTTSETTING_DOC= "deletePTTSettingDoc";
    public static final String GET_ALL_CORP_PTTSETTING_DOC_LIST= "getPTTSettingDocList";
    public static final String GET_CORP_PTTSETTING_DOC= "getPTTSettingDoc";
    public static final String SET_DEFAULT_PTTSETTING_DOC= "setDefaultPttSettingDoc";
    public static final String ASSIGN_PTTSETTING_DOC_TO_HIERARCHY= "assignPttSettingToHierarchy";
    public static final String UNASSIGN_PTTSETTING_DOC_TO_HIERARCHY= "unassignPttSettingToHierarchy";
    public static final String ASSIGN_PTTSETTING_DOC_TO_MDNLIST= "assignPttSettingDocToMdns";
    public static final String UNASSIGN_PTTSETTING_DOC_TO_MDNLIST= "UnassignPttSettingDocToMdns";
    public static final String GET_MDN_LIST_FOR_PTT_SETTING_DOC= "getPttSettingDocMdnList";
    public static final String GET_MDN_COUNT_FOR_PTT_SETTINGID= "getMDNCountForPttSettingDocID";
    public static final String ASSIGN_PTTSETTING_DOC_TO_CORP= "assignPttSettingToCorp";
    public static final String UNASSIGN_PTTSETTING_DOC_TO_CORP= "unassignPttSettingToCorp";


}
