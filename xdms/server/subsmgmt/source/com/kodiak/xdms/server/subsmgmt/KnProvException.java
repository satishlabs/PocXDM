/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * Created by IntelliJ IDEA.
 * File name:   Kn.java
 * Subsystem:
 * <p/>
 * Name                  Date         Release
 * -----------------    -----------   -------
 * Ravi Shanker .P       Dec 15, 2010       7.0
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
 */
package com.kodiak.xdms.server.subsmgmt;

import com.kodiak.xdms.server.common.KnXDMServerException;

public class KnProvException extends KnXDMServerException {

    public KnProvException(String errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }

    public KnProvException(String errorCode, STATUS_CODE statusCode, String errorMessage) {
        super(errorCode, statusCode, errorMessage);
    }

    public KnProvException(String errorCode, String errorMessage, Exception root) {
        super(errorCode, errorMessage, root);
    }

    public KnProvException(String errorCode, STATUS_CODE statusCode, String errorMessage, Exception root) {
        super(errorCode, statusCode, errorMessage, root);
    }
}
