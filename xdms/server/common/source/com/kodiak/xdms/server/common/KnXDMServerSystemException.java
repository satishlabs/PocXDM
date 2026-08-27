/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *********************************************************************
 * File name:   KnXDMServerSystemException.java
 * Subsystem:   Server Common
 * <p/>
 * Name                  Date         Release
 * -----------------    -----------   -------
 * Ravi Shanker .P       1/10/11   7.0
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
 * *************************************************************************
 */
package com.kodiak.xdms.server.common;

import com.kodiak.common.exception.KnSystemException;

public class KnXDMServerSystemException extends KnSystemException {

    public KnXDMServerSystemException(String errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }

    public KnXDMServerSystemException(String errorCode, String errorMessage, Exception root) {
        super(errorCode, errorMessage, root);
    }
}
