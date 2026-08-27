/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/****************************************************************************
 *
 * File name:   KnAuthenticationRule.java
 * Subsystem:   WGP
 *
 * Name                 Date       Release
 * -------------------- ---------- ---------------------------------------
 * Jiji Sasidharan      19/10/2006 5.7
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
package com.kodiak.xdms.server.common.framework.aas.authentication;

import java.util.Map;
import java.util.HashMap;

/**
 * This is a base implementation of the interface IAuthenticationRule.
 * It provides basic functionalities all the authentication rules must suppprt.
 */
public abstract class KnAuthenticationRule implements IAuthenticationRule {

    private String profile;
    private String ruleId;
    private Map parameterMap;
    protected static final String METHOD = "authenticate";
    /**
     * Return profile name.
     *
     * @return the profile name
     */
    public String getProfile() {
        return profile;
    }
                                              
    /**
     * Set the profile name
     *
     * @param profile the profile
     */
    public void setProfile(String profile) {
        this.profile = profile;
    }

    /**
     * Return rule id
     *
     * @return the rule id
     */
    public String getRuleId() {
        return ruleId;
    }

    /**
     * Set rule id
     *
     * @param ruleId the rule id
     */
    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    /**
     * Set the parameters configured in the xml for this rule
     *
     * @param parameters the parameters
     */
    public void addRuleParameters(Map parameters) {
        parameterMap.putAll(parameters);
    }

    /**
     * Return the value of a rule parameter.
     *
     * @param parameterName the name of the parameter
     * @return the value for the parameter
     * @throws KnAuthenticationException if there is any error
     */
    public String getRuleParameter(String parameterName) throws KnAuthenticationException {
        return (String) parameterMap.get(parameterName);
    }

    /**
     * constructor
     */
    protected KnAuthenticationRule() {
        parameterMap = new HashMap();
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer(500);
        buffer.append("KnAuthenticationRule@" + hashCode() + " - Rule Id -> ").append(ruleId).append(", Profile -> ").append(profile).append(", Parameters -> ").append(parameterMap);
        return buffer.toString();
    }
}
