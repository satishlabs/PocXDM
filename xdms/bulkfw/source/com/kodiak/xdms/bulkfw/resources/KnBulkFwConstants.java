/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.bulkfw.resources;
/**
 * ************************************************************************
 * <p/>
 * File name:  KnBulkFwConstants.java
 * Subsystem:  XDMS-BulkFrameWork
 * <p/>
 * Name                  Date          Release
 * --------------------  ------------  -------------------------------------
 * Chandrashekar H S     08/07/2015    8.0
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
public class KnBulkFwConstants {

    public static final String BULK_PROV_BATCH_SIZE = "BULK_PROV_BATCH_SIZE";
    public static final String UPDATE_BAN_BATCH_SIZE = "UPDATE_BAN_BATCH_SIZE";
    public static final String UPDATE_BAN_SIMUL_REQ_SIZE = "UPDATE_BAN_SIMUL_REQ_SIZE";
    public static final String PSEUDOMDN_HIGH_WATERMARK = "PSEUDOMDN_HIGH_WATERMARK";

    public static final String BO_INFO_TABLE_NAME = "DG.XDM_BULK_ORDER_INFO";
    public static final String BO_INFO_TABLE_NAME_DG = "XDM_BULK_ORDER_INFO";
    public static final String BULK_ORDER_ID = "BULKORDER_ID";

    public static final String SUBS_CONFIG_JOB_XML = "subsconfigBatchJob.xml";

    public static final String JOB_EXE_ID = "jobExecutionId";
    public static final int MAX_ALLOWED_BATCHES = 10;
    public static final int MAX_ALLOWED_DELETE_BATCHES = 1;

    //Pseudo MDN check interval in minutes
    public static final int PSEUDO_MDN_CHECK_INTEFVAL = 10;

    public static enum BULK_ORDER_TYPE {
        UNKNOWN(0), CONFIG_DOC_DIFF(1), SUBS_PAM_CREATE(2), SUBS_PAM_DELETE(3), SUBS_PAM_REACTIVATE(4), SUBS_PAM_SUSPEND(5), SUBS_PAM_DOWNGRADE(6),
        SUBS_NOTIFICATION(7), UPDATE_BAN(8) ,SUBS_PAM_ROLLBACK(9), SUBS_SUSPEND_ROLLBACK(10),SUBS_REACTIVATE_ROLLBACK(11);

        private int bulkOrderType;

        BULK_ORDER_TYPE(int bulkOrderType) {
            this.bulkOrderType = bulkOrderType;
        }

        public int value() {
            return this.bulkOrderType;
        }

    }

    public static enum CHANGE_LEVEL {
        UNKNOWN(0), GLOBAL(1), CORPORATE(2);

        private int changeLevel;

        CHANGE_LEVEL(int changeLevel) {
            this.changeLevel = changeLevel;
        }

        public int value() {
            return this.changeLevel;
        }
    }

    public static enum STATUS {
        NOT_STARTED(0), IN_PROGRESS(1), COMPLETED(2), FAILED(3), CRASHED(4), ROLLBACK(5);

        private int status;

        STATUS(int status) {
            this.status = status;
        }

        public int value() {
            return this.status;
        }
    }

    public static enum MEDIATOR_RESP_STATUS {
        SUCCESS(0), FAILURE(1);

        private int status;

        MEDIATOR_RESP_STATUS(int status) {
            this.status = status;
        }

        public int value() {
            return this.status;
        }
    }

    public static enum SERVICEAUTH_STATUS {
        REACTIVATE(2), SUSPEND(3);

        private int status;

        SERVICEAUTH_STATUS(int status) {
            this.status = status;
        }

        public int value() {
            return this.status;
        }
    }

    public static String getJobName(int bulkOrderType) {

        switch (bulkOrderType) {
            case 1:
                return "subsConfigDocDiffJob";
            case 2:
                return "createPAMJob";
            case 3:
                return "deletePAMJob";
            case 4:
            case 5:
                return "changeAuthStatus";
            case 6:
                return "deletePAMJob";
            case 8:
                return "updateBan";
            case 9:
                return "deletePAMJob";
            case 10:
            case 11:
                return "changeAuthStatus";
            default:
                return "genericJob";
        }

    }

    public static enum REQUEST_STATUS {
        SUCCESS(1), FAILURE(2);

        private int requestStatus;

        REQUEST_STATUS(int requestStatus) {
            this.requestStatus = requestStatus;
        }

        public int valueOf() {
            return this.requestStatus;
        }
    }

    public static enum CLIENT_TYPE {
        MOBILEMDN(1), WIFIONLY(2), CROSSCARRIERPTTCLIENT(10);

        private int clientType;

        CLIENT_TYPE(int clientType) {
            this.clientType = clientType;
        }

        public int valueOf() {
            return this.clientType;
        }
    }


    /*this method returns bulk operation type depends on the serviceAuthStatus got from the WEB.*/
    public static int getBulkOperationType(int serviceAuthStatus) {

        switch (serviceAuthStatus) {
            case 2:
                return 4;
            case 3:
                return 5;
            default:
                return 4;
        }
    }

    public static class ErrorCodes {
        public static final String INTERNAL_SERVER_ERROR = "KNEC-MD30004";
        public static final String INVALID_INPUT_DATA = "KNEC-BF9000";
        public static final String SUBSYSTEM_NOT_INITIALIZED = "KNEC-15000";
    }

    public static final String SUCCESS_CODE = "KNEC-MD00000";
    public static final int CORP_BROADCAST_GROUP_TYPE = 3;
}
