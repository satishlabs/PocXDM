/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/***************************************************************************
 *
 * File name:   KnAASConstants.java
 * Subsystem:   Framework
 *
 * Name                 Date       Release
 * -------------------- ---------- ---------------------------------------
 * Jiji Sasidharan      04-05-2006 5.7
 *
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
 **************************************************************************/
package com.kodiak.xdms.server.common.framework.aas;

/**
 * This interface holds all the constants used by the authorization framework
 */
public final class KnAASConstants {
    public static final String AAS_MODULE_NAME = "AAS";
    public static final String AAS_MODULE_ROOT = "aas";

    //authorization constants
    public static final String AUTHORIZATION = "authorization";
    public static final String AUTHENTICATION_REQUIRED = "authentication";

    //authentication constanst
    public static final String AUTHENTICATION = "authentication";

    // commmon constanst
    public static final String AUTH_PROFILES = "auth-profiles";
    public static final String RULES_CONFIG = "rules-config";
    public static final String ID = "id";
    public static final String CLASS_NAME = "class-name";
    public static final String AUTH_RULE_ID = "auth-rule-id";
    public static final String ENABLED = "enabled";
    public static final String PARAMETERS = "parameters";
    public static final String GLOBAL_RULES = "global-rules";
    public static final String TRUE = "true";
    public static final String FALSE = "false";
    public static final String LOGIN = "login";
    public static final String GENERAL = "general";
    public static final String LOGIN_AUTH = "login-auth";
    public static final String OPERATION_AUTH = "operation-auth";
    public static final String ALLOWED = "allowed";
}
