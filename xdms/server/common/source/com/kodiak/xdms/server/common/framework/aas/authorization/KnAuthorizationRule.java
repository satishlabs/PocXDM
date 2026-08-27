/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/***************************************************************************
 *
 * File name:   KnAuthorizationRule.java
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
package com.kodiak.xdms.server.common.framework.aas.authorization;

import java.util.Map;
import java.util.HashMap;


/**
 *
 */
public abstract class KnAuthorizationRule implements IAuthorizationRule {

    protected static final String METHOD = "authorize";

    /**
     * store the rule parameters
     */
    private Map parameterMap;
    /**
     * store rule id
     */
    private String ruleId;
    /**
     * store operation id
     */
    private String operationId;
    /**
     * store the profile name
     */
    private String profile;

    /**
     * constructor
     */
    protected KnAuthorizationRule() {
        parameterMap = new HashMap();
    }

    /**
     * This method will set the rule parameters map
     *
     * @param parameters the rule parameters map
     */
    public void addRuleParameters(Map parameters) {
        parameterMap.putAll(parameters);
    }

    /**
     * This method will get the value of a parameter from
     * the parameter map
     *
     * @param parameterName
     * @return the parameter value
     */
    public String getRuleParameter(String parameterName) throws KnAuthorizationException {
        return (String) parameterMap.get(parameterName);
    }

    /**
     * This method will return the rule id
     *
     * @return the rule id
     */
    public String getRuleId() {
        return ruleId;
    }

    /**
     * this method will set the profile name
     *
     * @param profile the profile
     */
    public void setProfile(String profile) {
        this.profile = profile;
    }

    /**
     * this method will return the profile name.
     *
     * @return the profile name
     */
    public String getProfile() {
        return this.profile;
    }

    /**
     * This method will set the rule id
     *
     * @param ruleId the rule id
     */
    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    /**
     * this method will return the operation id
     *
     * @return the operation id
     */
    public String getOperationId() {
        return operationId;
    }

    /**
     * This method will set the operation id
     *
     * @param operationId the operation id
     */
    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }
}
