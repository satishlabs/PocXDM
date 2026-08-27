/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/****************************************************************************
 *
 * File name:   KnAuthorizationException.java
 * Subsystem:   Business Exceptions
 *
 * Name                 Date       Release
 * -------------------- ---------- ---------------------------------------
 * Rama Krishna         24/04/2006 5.7
 *
 * #401, 4th Floor, 'Prestige Sigma'
 * No.3, Vittal Mallya Road
 * Bangalore - 560 001
 * www.kodiaknetworks.com
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 ***************************************************************************/
package com.kodiak.xdms.server.common.framework.aas.authorization;

import com.kodiak.xdms.server.common.framework.aas.KnAASException;

/**
 * This Exception handles the authorization errors (or) exceptions in business layer. And it is
 * also extends from KnBOException class.
 */
public class KnAuthorizationException extends KnAASException {

    private String operation;
    /**
     * stores the authorization rule
     */
    private String authorizationRule;

    /**
     * @param errorCode    error code of error
     * @param errorMessage error message string
     * @param profile      the profile name
     * @param operation    operation id
     * @param rule         authorization rule
     */
    public KnAuthorizationException(String errorCode, String errorMessage, String profile,
                                    String operation, String rule) {
        super(errorCode, errorMessage, profile);
        this.authorizationRule = rule;
        this.operation = operation;
    }

    /**
     * @param errorCode    error code of error
     * @param errorMessage error message string
     * @param root         root of an another exception
     * @param profile      the profile name
     * @param operation    operation id
     * @param rule         authorization rule
     */
    public KnAuthorizationException(String errorCode, String errorMessage, Exception root,
                                    String profile, String operation, String rule) {
        super(errorCode, errorMessage, root, profile);
        this.operation = operation;
        this.authorizationRule = rule;
    }

    /**
     * returns the authorization rule
     *
     * @return String    returns authorization rule
     */
    public String getAuthorizationRule() {
        return authorizationRule;
    }

    /**
     * set the authorization rule
     *
     * @param rule authorization rule
     */
    public void setAuthorizationRule(String rule) {
        this.authorizationRule = rule;
    }

    /**
     * This method will return the message of the exception
     *
     * @return the message
     */
    public String getMessage() {
        return super.getMessage() + ", Operation : " + operation + ", Rule Id : " + authorizationRule;
    }
}
