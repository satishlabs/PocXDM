/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**************************************************************************
 * <p/>
 * File name:  KnBulkDocDiffConstants.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Ravi Shanker P       Oct 3, 2012   7.4
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
 * ************************************************************************/
package com.kodiak.xdms.bulkfw.resources;

/**
 * @author kodiak
 */
public class KnBulkDocDiffConstants extends KnBulkFwConstants {

    // Constants for TLV Generation
    public static final int MESSAGE_ID = 3001;
    public static final long SLEEP_TIMER = 1000l;

    public static enum DOC_CHANGE_TYPE {
        ADD(1), REPLACE(2), REMOVE(3);

        int docChangeType;

        DOC_CHANGE_TYPE(int docChangeType) {
            this.docChangeType = docChangeType;
        }

        public int value() {
            return docChangeType;
        }
    }

    public static enum TYPE_OF_DOC {
        DIR_DOC(0), SUBS_CONFIG_DOC(1);

        int typeOfUri;

        TYPE_OF_DOC(int typeOfUri) {
            this.typeOfUri = typeOfUri;
        }

        public int value() {
            return typeOfUri;
        }
    }

    public static enum STATUS {
        NOT_STARTED(0), IN_PROGRESS(1), COMPLETED(3), FAILED(4);

        int status;

        STATUS(int status) {
            this.status = status;
        }

        public int value() {
            return this.status;
        }
    }

}
