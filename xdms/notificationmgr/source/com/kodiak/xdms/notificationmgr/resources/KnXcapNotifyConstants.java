/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnXcapNotifyConstants.java
 * Subsystem:  PoC XDMS
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Harsha             08-Jan-2011     7.0
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
package com.kodiak.xdms.notificationmgr.resources;

public class KnXcapNotifyConstants {
    public static final int DIRECTORY_CHANGE_TYPE_ADD = 1;
    public static final int DIRECTORY_CHANGE_TYPE_REPLACE = 2;
    public static final int DIRECTORY_CHANGE_TYPE_REMOVE = 3;

    // public static final int XCAP_NOTIFICATION_FEATURE_ID = 5000;
    public static final int XCAP_NOTIFICATION_MESSAGE_ID = 3001;
    public static final int XCAP_DEACTIVATION_MESSAGE_ID = 3002;
    public static final int PROXY_DELETE_SUBS_MESSAGE_ID = 3003;

    public static final int DOC_CHANGE_EVENT_FEATURE_ID = 601;
    public static final int SUBS_PROFILE_CHANGE_FEATURE_ID = 701;
    public static final String ALLOWED_SIPMSG_HEADERS_SIZE = "ALLOWED_SIPMSG_HEADERS_SIZE";
    public static final String ALLOWED_SIPMSG_SIZE_ON_TCP = "ALLOWED_SIPMSG_SIZE_ON_TCP";
    public static final String ALLOWED_SIPMSG_SIZE_ON_UDP = "ALLOWED_SIPMSG_SIZE_ON_UDP";
    public static final String TELURI = "tel:+";

    public static final String DEFAULT_GROUPNAME = "XcapNotifications";
    public static final String DEFAULT_JOBGROUPNAME = "XcapDiffNotifications";
    public static final String DEFAULT_JOBSEHNAME = "SEH_Notification";

    //INT82147 Identify type of Notify listed below, Directory change Notify  - 1, Doc-Diff  Notify - 2
    public static final int DIR_CHANGE_NOTIFY_TYPE = 1;
    public static final int DOC_DIFF_NOTIFY_TYPE = 2;
    //public static final int SUBSCRIBE_NOTIFY_TYPE = 3;


    public static final int RTX_VERSION = 70;


    public static enum DIRECTORY_CHG_LOG_TYPE {
        ADD(1), REPLACE(2), REMOVE(3);

        int dirChgLogType;

        DIRECTORY_CHG_LOG_TYPE(int dirChgLogType) {
            this.dirChgLogType = dirChgLogType;
        }

        public int value() {
            return dirChgLogType;
        }
    }

    public static enum DESTTYPE {
        MDN(1),
        GROUP(2),
        SUPPRESSMDN(3);

        final int destType;

        DESTTYPE(int destType) {
            this.destType = destType;
        }

        public int value() {
            return destType;
        }
    }

    public static enum NOTIFYSTATUS {
        PENDING(1),
        NOTIFY_INITIATED(2);

        final int notifyStatus;

        NOTIFYSTATUS(int notifyStatus) {
            this.notifyStatus = notifyStatus;
        }

        public int value() {
            return notifyStatus;
        }
    }

    public static enum PAYLOADVERSION {
        ONE(1),
        TWO(2);

        final int payloadVersion;

        PAYLOADVERSION(int payloadVersion) {
            this.payloadVersion = payloadVersion;
        }

        public int value() {
            return payloadVersion;
        }
    }

    public static enum DOC_TYPE {
        CORP_CONTACT_DOC("Corp_Contact_doc"),
        SUBS_CONFIG_DOC("Subs_Config_Doc"),
        EMERGENCY_DOC("Emergency_Doc"),
        AU_PERMISSION_DOC("AU_Permission_Doc"),
        TGSC_LIST_DOC("TGSC_List_Doc"),
        XCAP_DIRECTORY_DOCUMENT("XCAP_Directory_document"),
        CORP_GROUP_DOC("Corp_Group_Doc"),
        SUBS_DEREGISTER_NOTIFY_DOC("Subs_Deregister_notify_Doc"),
        SUBS_CHNAGE_MDN_DOC("Subs_Chnage_MDN_Doc"),
        SUBS_PROFILE_CHANGE_NOTIFY("Subs_Profile_Change_notify");
        String docType;

        DOC_TYPE(String docType) {
            this.docType = docType;
        }

        public String value() {
            return docType;
        }
    }
}
