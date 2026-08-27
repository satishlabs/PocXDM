/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *****************************************************************************
 * File name:   KnProvBOException.java
 * Subsystem:   Provisioning Library
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ravi Shanker .P       12/25/10       7.0
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
package com.kodiak.xdms.server.subsmgmt.business;

import com.kodiak.xdms.server.subsmgmt.KnProvException;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvConstants;

public class KnProvBOException extends KnProvException {

    private static final String ORIGINATOR = KnProvConstants.LIBRARY_NAME + "." + "ORIGINATOR";

    public KnProvBOException(String errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }

    public KnProvBOException(String errorCode, STATUS_CODE statusCode, String errorMessage) {
        super(errorCode, statusCode, errorMessage);
    }

    public KnProvBOException(String errorCode, String errorMessage, Exception root) {
        super(errorCode, errorMessage, root);
    }

    public KnProvBOException(String errorMessage, KnProvException root) {
        super( ORIGINATOR + root.getErrorCode(), errorMessage, root);
    }

    public KnProvBOException(String errorCode, STATUS_CODE statusCode, String errorMessage, Exception root) {
        super(errorCode, statusCode, errorMessage, root);
    }
}
