/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.resources;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnOperationTypes.java
 * Subsystem:  pubmgmt
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * SureshKumar G        Jan 14, 2011           7.0
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
public class KnOperationTypes {

    public static final String CREATE_GROUP = "createGroup";
    public static final String MODIFY_GROUP = "modifyGroup";
    public static final String DELETE_GROUP = "deleteGroup";
    public static final String GET_GROUP_LIST = "getGroupList";
    public static final String GET_PUB_GROUP_LIST = "getPubGroupList";
    public static final String GET_GROUP_DOC_DETAILS = "getGroupDocDetails";
    public static final String GET_GROUP_DETAILS = "getGroupDetails";
    public static final String UPDATE_GROUP_NAME = "updateGroupName";
    public static final String DELETE_GROUP_MEMBER = "deleteGroupMember";
    public static final String UPDATE_GROUP_MEMBER = "updateGroupMember";
    public static final String ADD_GROUP_MEMBER = "addGroupMember";

    public static final String ADD_CONTACT = "addContact";
    public static final String UPDATE_CONTACT = "updateContact";
    public static final String DELETE_CONTACT = "deleteContact";
    public static final String GET_CONTACT_LIST = "getContactList";
    public static final String GET_XDMINTF_CONTACT_LIST = "getXDMintfContactList";
    public static final String GET_ALL_CONTACT_LISTS = "getAllContactLists";
    public static final String GET_DIRECTORY = "getDirectory";

    public static final String FORCE_SYNC = "forceSync";
    public static final String CHANGE_MDN = "changeMdn";
    public static final String DELETE_ALL_CONTACTS_AND_GROUPS = "deleteAllContactsAndGroups";
    public static final String GET_PUB_GROUP_DETAILS_XDMDATA_INTF = "getPubGroupDetailsXDMDataIntf";
    public static final String MODIFY_DYNAMIC_CONTACT = "modifyDynamicContact";
    public static final String DELETE_DYNAMIC_CONTACT = "deleteDynamicContacts";
    public static final String GET_DYNAMIC_CONTACT = "getDynamicContacts";
    public static final String GET_DYNAMIC_GROUP_DETAILS = "getDynamicGroupDetails";
    public static final String GET_DYNAMIC_GROUP_LIST = "getDynamicGroupList";
    public static final String DELETE_DYNAMIC_NONSHARED_GROUP = "deleteDynamicGroup";
    public static final String CREATE_DYNAMIC_NONSHARED_GROUP = "createNonSharedGroup";
    public static final String MODIFY_DYNAMIC_NONSHARED_GROUP = "modifyNonSharedGroup";

    public static final String UPDATE_AUTHLIST = "updateAuthList";
    public static final String GET_AUTHLIST = "getAuthList";
    public static final String GET_EMERGENCY_CONFIG_DOC = "getEmergencyConfig";

    public static final String UPDATE_TGSSLIST = "updateTGSSList";
    public static final String GET_TGSSLIST = "getTGSSList";
    public static final String DELETE_TGSSLIST = "deleteTGSSList";

    //MCS XCAP APIs
    public static final String GET_MCPTT_UE_CONFIG = "getMCPTTUEConfig";
    public static final String GET_MCPTT_USER_PROFILE = "getMCPTTUserProfile";
    public static final String GET_MCPTT_SERVICE_CONFIG = "getMCPTTServiceConfig";

    public static final String GET_MCDATA_UE_CONFIG = "getUEConfig";
    public static final String GET_MCDATA_USER_PROFILE = "getUserProfile";
    public static final String GET_MCDATA_SERVICE_CONFIG = "getServiceConfig";

    public static final String GET_MCVIDEO_UE_CONFIG = "getUEConfig";
    public static final String GET_MCVIDEO_USER_PROFILE = "getUserProfile";
    public static final String GET_MCVIDEO_SERVICE_CONFIG = "getServiceConfig";

    public static final String GET_MCS_GROUP_DOC = "getMCSGroupDoc";

    public static final String GET_MCS_USER_DIR = "getMCSUserDir";

}
