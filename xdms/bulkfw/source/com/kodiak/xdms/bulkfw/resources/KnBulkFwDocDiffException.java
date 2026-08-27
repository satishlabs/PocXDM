/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**************************************************************************
 * <p/>
 * File name:  KnBulkFwDocDiffException.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Ravi Shanker P       Oct 4, 2012   7.4
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

import com.kodiak.common.exception.KnException;

/**
 * @author kodiak
 */
public class KnBulkFwDocDiffException extends KnException {

    private static final String BULK_FW_DOC_DIFF = "BULK_FW_DOC_DIFF.";

    /**
     * Constructs new KnBulkFwDocDiffException object with error code, error message and BULK_FW_DOC_DIFF
     * type of the error.
     *
     * @param errorCode    error code of error
     * @param errorMessage error message string
     */
    public KnBulkFwDocDiffException(String errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }

    /**
     * Constructs new KnBulkFwDocDiffException object with error code, error message, BULK_FW_DOC_DIFF
     * type and instance of another exception object.
     *
     * @param errorCode    error code of error
     * @param errorMessage error message string
     * @param root         root of an another exception instance
     */
    public KnBulkFwDocDiffException(String errorCode, String errorMessage, Exception root) {
        super(errorCode, errorMessage, root);
    }

    /**
     * Constructs the new KnBulkFwDocDiffException object with error message and with
     * another exception object.
     * This construtor is used to wrap the exception with new KnBulkFwDocDiffException.
     * It will be used the same error code but changing orignator to BULK_FW_DOC_DIFF.
     * This will be used whenever we expect multiple exceptions from the calling method.
     *
     * @param errorMessage error message string
     * @param root         root of an another exception object
     */
    public KnBulkFwDocDiffException(String errorMessage, KnException root) {
        super(BULK_FW_DOC_DIFF + root.getErrorCode(), errorMessage, root);
    }


    /**
     * This method will return the message of the exception
     *
     * @return the message
     */
    public String getMessage() {
        return super.getMessage();
    }

}
