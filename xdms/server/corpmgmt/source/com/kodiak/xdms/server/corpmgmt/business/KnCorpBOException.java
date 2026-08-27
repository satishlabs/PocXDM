/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnCorpBOException.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Jan 10, 2011      7.0
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
package com.kodiak.xdms.server.corpmgmt.business;

import com.kodiak.xdms.server.common.KnXDMServerException;
import com.kodiak.xdms.server.common.resources.KnConstants;
import com.kodiak.xdms.server.common.business.KnBOException;


public class KnCorpBOException extends KnBOException {

    private static final String ORGINATOR = KnConstants.LIBRARY_NAME_CORP_MGMT + "." + "ORGINATOR.";

    public KnCorpBOException(String errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }

//    public KnCorpBOException(String errorCode, STATUS_CODE statusCode, String errorMessage) {
//        super(errorCode, statusCode, errorMessage);
//    }

    public KnCorpBOException(String errorCode, String errorMessage, Exception root) {
        super(errorCode, errorMessage, root);
    }

//    public KnCorpBOException(String errorCode, STATUS_CODE statusCode, String errorMessage, Exception root) {
//        super(errorCode, statusCode, errorMessage, root);
//    }
}
