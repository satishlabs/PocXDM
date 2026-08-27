/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnActions.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      11/25/11      7.2
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


public class KnActions {

    public static enum ACTIONS {
        CREATE_GROUP(1), MODIFY_GROUP(2), AUTHENTICATE(3), MODIFY_SUBSC_CONTACT(4), REMOVE_SUBLIST(5), CREATE_SUBLIST(6),
        GET_SUBLIST_DETAILS(7), GET_DISTRIBUTION_LIST(8),
        MODIFY_SUBLIST(9), DELET_SUBLIST(10), GET_SUBSC_GROUP_LIST(11), GET_GROUP_DETAILS(12),
        DELETE_GROUP(13), GET_SUBSC_SPEC_DETAILS(14), GET_SUBS_EMAIL_ID(15), SAVE_SUBS_ACTIVATION_CODE(16),
        GET_CORP_RESOURCE_LIST(17), GET_SUBSC_CONTACT_DETAILS(18), PUSH_SUBLIST(19), UPDATE_SUBSCRIBER(20),
        GENERATE_ACTIVATION_CODES(21), GET_MAIL_INFO(22), ASSIGN_UNASSIGN_TGS(23), MODIFY_SUBS_CORP_FEATURE(24),
        MODIFY_SCAN_LIST(25), GET_SCAN_LIST(26), GET_PSUEDOMDNS(27), MARK_UNMARK_PSUEDOMDNS(28), GET_BAN_FAN_MDN_LIST(29),
        UPDATE_BILLING_NAME(30), REMOVE_SUBSCRIBERS_CONTACTS(31), REMOVE_SUBSCRIBERS_ALL_SUBLIST(32), REMOVE_SUBSCRIBERS_ALL_GROUPS(33),
        GET_ALL_CORP_SUBSRIBER_FEATURE_SETS(34), MODIFY_SUBSCR_CORP_ADMINFS(35), GET_ACTIVATION_CODE(36),
        GET_SUBSCRIBER_DETAILS(37), SWITCH_CONVERGED_CLIENT(38), GET_CORP_DETAILS(39), UPDATE_CORP_SUBSCRIBER(40),
        SET_AUTH_USER_PERMISSIONS(41), GET_AUTH_USER(42), FORCE_SYNC(43), SUBS_EMERGENCY_ATTRIBUTES(44),
        CREATE_OSM_LIST(45), UPDATE_OSM_LIST(46), DELETE_OSM_LIST(47), GET_OSM_LIST(48), GET_OSM_LIST_DETAILS(49),
        GETEXTCORPID(50), ASSIGN_OSM_TO_GROUP(51), GET_OSM_GROUP_LIST(52),
        CREATE_USER_PROFILE(53), UPDATE_USER_PROFILE(54), DELETE_USER_PROFILE(55), GET_USER_PROFILE_DETAILS(56), GET_USER_PROFILE_LIST(57), GET_USER_PROFILE_LIST_BY_NAME(58),
        GET_SUBSCRIBER_USER_PROFILE_LIST(59), GET_USERPROFILE_SUBSCRIBER_LIST(60), UPDATE_DEFAULT_PROFILE(61), FILTER_USER_PROFILE_IDS(62), ASSIGN_USER_PROFILE(63),
        POP_ADDL_INFO(64), UNASSIGN_USER_PROFILE(65), MODIFY_MCX_GROUP(66), CREATE_BULK_GROUP(67), GET_CORP_HIERARCHY(68);

        private int action;

        ACTIONS(int action) {
            this.action = action;
        }

        public int getAction() {
            return this.action;
        }

    }
}
