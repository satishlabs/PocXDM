/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.bulkfw.resources;
/**
 * ************************************************************************
 * <p/>
 * File name:  KnBulkProvConstants.java
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
public class KnBulkProvConstants {

    public static final int RETRY_COUNT = 3;
    public static final int RETRY_INTERVAL = 3000;

    public static enum BATCH_EXEC_TYPE {
        BATCH_EXEC_NORMAL(0), BATCH_EXEC_ROLLBACK(1);

        private int batchExceType;

        BATCH_EXEC_TYPE(int batchExceType) {
            this.batchExceType = batchExceType;
        }

        public int value() {
            return this.batchExceType;
        }
    }
}
