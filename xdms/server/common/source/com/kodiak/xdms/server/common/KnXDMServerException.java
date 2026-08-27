/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common;

import com.kodiak.common.exception.KnException;
import com.kodiak.xdms.server.common.util.KnGeneralUtil;

import java.util.List;

/**
 * *********************************************************************
 * File name:   Kn.java
 * Subsystem:
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
public class KnXDMServerException extends KnException {

    private KnXDMError errorObject;

    private List errorParams;

    public KnXDMServerException(String errorCode, String errorMessage) {
        super(errorCode, errorMessage);
        this.errorObject = KnGeneralUtil.createErrorObject(errorCode);
    }

    public KnXDMServerException(String errorCode, STATUS_CODE statusCode, String errorMessage) {
        super(errorCode, statusCode, errorMessage);
        this.errorObject = KnGeneralUtil.createErrorObject(errorCode);
    }

    public KnXDMServerException(String errorCode, String errorMessage, Exception root) {
        super(errorCode, errorMessage, root);
        this.errorObject = KnGeneralUtil.createErrorObject(errorCode);
    }

    public KnXDMServerException(String errorCode, STATUS_CODE statusCode, String errorMessage, Exception root) {
        super(errorCode, statusCode, errorMessage, root);
        this.errorObject = KnGeneralUtil.createErrorObject(errorCode);
    }

    public KnXDMServerException(String errorCode,  Exception root) {
        super(errorCode, root);
        this.errorObject = KnGeneralUtil.createErrorObject(errorCode);
    }

    /**
     * This will return the list error parameters. it can be list of MDNs, grou pnames
     * etc.. all the elements in the list will be of type String.
     *
     * @return List  returns list of error parameters
     */
    public List getErrorParameters() {
        return this.errorParams;
    }

    /**
     * This method will set error parameter list to the exception
     *
     * @param params list of error parameters
     */
    public void setErrorParameters(List params) {
        this.errorParams = params;
    }

    public KnXDMError getErrorObject() {
        return errorObject;
    }

    public void setErrorObject(KnXDMError errorObject) {
        this.errorObject = errorObject;
    }

    public String toString() {
        StringBuffer strBuffer = new StringBuffer(100);
        strBuffer.append(super.toString());
        strBuffer.append(", Error_Object - ").append(errorObject)
                .append(", Error_Params - ").append(errorParams);

        return strBuffer.toString();
    }
}
