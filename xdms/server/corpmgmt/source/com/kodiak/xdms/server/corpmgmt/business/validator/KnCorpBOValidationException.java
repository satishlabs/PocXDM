/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnCorpBOValidationException.java
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
package com.kodiak.xdms.server.corpmgmt.business.validator;

import com.kodiak.xdms.server.common.framework.validator.KnValidationException;


public class KnCorpBOValidationException extends KnValidationException {

    public KnCorpBOValidationException(String errorCode, String errorMessage, String rule) {
        super(errorCode, errorMessage, rule);
    }

    public KnCorpBOValidationException(String errorCode, String errorMessage, String rule, String errorType) {
        super(errorCode, errorMessage, rule, errorType);
    }

    public KnCorpBOValidationException(String errorCode, String errorMessage, String rule, String keyDataValue, String errorType) {
        super(errorCode, errorMessage, rule, keyDataValue, errorType);
    }

    public KnCorpBOValidationException(String errorCode, String errorMessage, String rule, String keyDataType, String keyDataValue, String failedRuleValue, String errorType) {
        super(errorCode, errorMessage, rule, keyDataType, keyDataValue, failedRuleValue, errorType);
    }

    public KnCorpBOValidationException(String errorCode, String errorMessage, Exception root, String rule, String data) {
        super(errorCode, errorMessage, root, rule, data);
    }
}
