/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.mcsnotifymgr.resources;
/**
 * ************************************************************************
 * <p>
 * File name:  KnMCSNotifyConstants.java
 * Subsystem:  XDMS
 * <p>
 * Name                         Date                     Release
 * --------------------     ----------------        ------------------
 * Venkata Sudhakar            Dec 23, 2019                10.0+
 * <p>
 * <p>
 * KODIAK, 9th Floor, 'MFar Manyata Tech Park'
 * Greenheart Phase IV, Nagawara
 * Bangalore - 560 045
 * www.kodiakptt.com
 * All Rights Reserved.
 * <p>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */

public class KnMCSNotifyConstants {

    public static final String MCS_GRPNAME = "MCSDOCCHANGE";

    public static enum NOTIFYTYPE {
        DOCUMENT_CHANGE(1),
        DOCUMENT_DIFF(2);

        int notifyType;

        NOTIFYTYPE(int notifyType) {
            this.notifyType = notifyType;
        }

        public int value() {
            return notifyType;
        }
    }

    public static enum DOCTYPE {
        MDN(1),
        GROUP(2);

        int docType;

        DOCTYPE(int docType) {
            this.docType = docType;
        }

        public int value() {
            return docType;
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

}
