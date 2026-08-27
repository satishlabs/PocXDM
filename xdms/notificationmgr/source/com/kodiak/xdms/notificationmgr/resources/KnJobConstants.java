/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnConstants.java
 * Subsystem:  POC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Sanjiv Acharyya        30-11-2011      7.2
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

public class KnJobConstants {

    public static final String JOB_NAME = "DISPATCH_GROUP_MEMEBER";
    public static final String JOB_GROUP_NAME = "DISPATCHR_GROUP_JOB";
    public static final String JOB_MICRO_SERVICE_NOTIFY = "MICRO_SERVICE_NOTIFY";
    public static final String BULK_GROUP_JOB_FOR_CONTACT = "BULK_GROUP_JOB_FOR_CONTACT";
    public static final String ERROR_CODE_INTERNAL_ERROR = "KNEC-MD30004";
    public static final String ETAG_MGMT = "ETAG_MGMT";
    public static final int THREAD_DELAY = 5;

    public static final String SERVICE_ID = "serviceId";
    public static final String RESOURCE_TYPE = "resourceType";
    public static final String RESOURCE_ID = "resourceId";
    public static final String RESOURCE_IDS = "resourceIds";

    public static enum SERVICE_ID_VALUE {
        AFFILIATION(1),
        LOCATION(2),
        PRESENCE(3),
        XDMDATAINTF(4),
        OSM(5),
        XDM(6);

        int resourceId;

        SERVICE_ID_VALUE(int type) {
            this.resourceId = type;
        }

        public int value() {
            return resourceId;
        }
    }

    public static enum RESOURCE_TYPE_VALUE {
        GROUP(1),
        MDN(2),
        CORP(3),
        BULK_GROUP_PROPERTIES(4);

        int resourcetype;

        RESOURCE_TYPE_VALUE(int type) {
            this.resourcetype = type;
        }

        public int value() {
            return resourcetype;
        }
    }

    public enum SERVICE_STATES
    {
        SERVICE_INIT_STATE,
        SERVICE_UP_STATE,
        SERVICE_DOWN_STATE,
        SERVICE_UNKNOW_STATE,
        SERVICE_PARTIALLY_UP_STATE
    }

    public enum EVENTS
    {
        UP_EVENT,
        DOWN_EVENT
    }

    public static final String UNSERSCORE = "_";
    public static final String SERVICE_AUTH_STATUS = "serviceAuthStatus";
}
