/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *****************************************************************************
 * File name:   KnErrorCodes.java
 * Subsystem:   Common
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ravi Shanker .P       12/30/10       7.0
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
 * *******************************************************************************
 */
package com.kodiak.common.resources;

public class KnErrorCodes {
    public static final String MODULE_NAME = "ERRORCODES";

    public static final String SERVER_BUSY_REQUEST_DROPPED = "KNEC-CM1004";

    public static class Initializer {
        public static final String INTERNAL_ERROR = "KNEC-CM1001";
    }

    public static class DAO {
        public static final String SERVER_BUSY = "KNEC-CM1011";
        public static final String INTERNAL_ERROR = "KNEC-CM1001";
        public static final String PTT_SERVER_NOT_REACHABLE = "KNEC-CM1016";
        public static final String ROW_NOT_FOUND = "KNEC-CM1500";
        public static final String CONNECTION_FAILED = "KNEC-CM1501";
        public static final String ROW_ALREADY_EXISTS = "KNEC-CM1504";
        public static final String ROW_ALREADY_DELETED = "KNEC-CM1505";
        public static final String TXN_START_FAILED = "KNEC-CM1507";
        public static final String TXN_COMMIT_FAILED = "KNEC-CM1508";
        public static final String TXN_ROLLBACK_FAILED = "KNEC-CM1509";
        public static final String TXN_CONNECTION_NOT_AVAILABLE = "KNEC-CM1510";
        public static final String TXN_NOT_STARTED = "KNEC-CM1511";
        public static final String COLUMN_WIDTH_EXCEEDED = "KNEC-CM1512";
        public static final String SQL_EXCEPTION = "KNEC-CM1514";
        public static final String SUFFIX_LIMIT_EXCEEDED = "KNEC-CM1516";
        public static final String PREFIX_NOT_CONFIGURED = "KNEC-CM1517";
        public static final String INVALID_REQUEST_DATA = "Validator.KNEC-SP40506";
    }

}
