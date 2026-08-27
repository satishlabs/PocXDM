/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *********************************************************************
 * File name:   KnProvOperationTypes.java
 * Subsystem:   Provisioning Library
 * <p/>
 * Name                  Date         Release
 * -----------------    -----------   -------
 * Ravi Shanker .P       12/28/10   7.0
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
 * *************************************************************************
 */
package com.kodiak.xdms.server.subsmgmt.resources;

public class KnProvOperationTypes {
    public static final String CREATE_SUBSCRIBER = "createSubscriber";
    public static final String ACTIVATE = "activateSubscriber";
    public static final String GET_SUBSCRIBER_PROFILE = "getSubscriberDetails";
    public static final String UPDATE_SUBSCRIBER = "updateSubscriber";
    public static final String UPDATE_SUBSCRIBER_USER_PROFILE_FS = "updateSubscriberUserProfileFs";
    public static final String CHANGE_SERVICE_AUTH_STATUS = "changeServiceAuthStatus";
    public static final String FORCE_SYNC = "forceSync";
    public static final String DELETE_SUBSCRIBER = "deleteSubscriber";
    public static final String GET_SUBSCRIBER_CONFIG_DOC = "getSubscriberConfigDoc";
    public static final String CHANGE_MDN = "changeMDN";
    public static final String REMOVE_SUBS_INFO = "removeSubsInfo";
    public static final String GET_DEFAULT_SUBSCRIBER_PROFILE = "getDefaultSubscriberProfile";
    public static final String DELETE_CORP_PROFILE = "deleteCorpProfile";
    public static final String CREATE_CORP_PROFILE = "createCorpProfile";
    public static final String UPDATE_AUTO_PAIRING = "updateAutoParing";
    public static final String SET_SUBSCRIBERCLIENT_SETTINGS = "setSubscrClientSettings";
    public static final String GET_SUBSCRIBERCLINET_SETTINGS = "getSubscrClientSettings";

    public static final String CREATE_PAM_ACCOUNT = "createPAMAccount";
    public static final String UPDATE_PAM_ACCOUNT = "updatePAMAccount";
    public static final String DELETE_PAM_ACCOUNT = "deletePAMAccount";
    public static final String CREATE_PAM_SUBS_PROFILE = "createPAMSubsProfile";
    public static final String UPDATE_PAM_SUBS_PROFILE = "updatePAMSubsProfile";
    public static final String DELETE_PAM_SUBS_PROFILE = "deletePAMSubsProfile";
    public static final String GET_PAM_SUBS_PROFILE = "getPAMSubsProfile";
    
    public static final String MIGRATE_PAM_ACCOUNT = "migratePAMAccount";
    public static final String UPGRADE_PAM_ACCOUNT = "upgradePAMAccount";

    public static final String CREATE_BULK_SUBSCRIBER = "createBulkSubscriber";
    public static final String UPDATE_BULK_SUBSCRIBER = "updateBulkSubscriber";
    public static final String DELETE_BULK_SUBSCRIBER = "deleteBulkSubscriber";
    public static final String CHANGE_BULK_SERVICE_AUTH_STATUS = "changeBulkServiceAuthStatus";

    public static final String CREATE_EXT_SUBSCRIBER = "createExtSubscriber";
    public static final String DELETE_EXT_SUBSCRIBER= "deleteExtSubscriber";
    public static final String UPDATE_EXT_SUBSCRIBER="updateExtSubsProfileId";
    public static final String GET_EXT_SUBSCRIBER="getExtSubscriberInfo";
    public static final String GET_CORPID_EXT_SUBSCRIBER="getCorpIdsForExtSubscriber";
    public static final String ADD_MODIFY_EXT_SUBSCRIBER="addOrModifyExtSubscribers";
    public static final String GET_CORP_ID = "getCorporationID";
    public static final String GET_SUBSCRIBER_PROFILE_XDMDATA_INTF = "getSubsDetailsXDMDataIntf";
    public static final String SWITCH_CONVERGED_CLIENT = "switchConvergedClient";
    public static final String SEARCH_ADD_BOOK="searchAddressBook";
    public static final String PRIVACY_OPT_STATUS=" updatePrivacyOptStatus";

    public static final String GETSYSCONFIG = "getSysConfig";
    public static final String USER_LOGIN = "userLogin";
    public static final String SELECTPROFILEMDN = "selectProfileMdn";

    public static final String GETAUTHORIZEDUSERLIST = "getAuthorizedUserList";
    public static final String GET_USERPROFILEIDS_BY_PROFILEMDNS = "getUserprofileidsByProfileMdns";

    public static final String GET_SCUSCR_CLIENTSETTINGS = "getSubscrClientSettings";
    public static final String GET_CORPORATE_ACCOUNT_DETAILS= "getCorporateAccountDetails";

}
