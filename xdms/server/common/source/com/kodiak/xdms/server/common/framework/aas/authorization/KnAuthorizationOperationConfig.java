/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/***************************************************************************
 *
 * File name:   KnAuthorizationOperationConfig.java
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

import com.kodiak.xdms.server.common.framework.aas.KnAASConstants;

import java.util.*;

/**
 * This class holds the configurations of a operation
 */
public class KnAuthorizationOperationConfig {

    // authorization profile
    private String profile;
    // id of the operatio
    private String id;
    // list of rules configured for the operation
    private Map rules;

    private boolean isAuthenticationRequired = false;

    /**
     * constructor
     */
    public KnAuthorizationOperationConfig() {
        rules = new LinkedHashMap();
    }

    /**
     * Returns authorization profile
     *
     * @return authorization profile
     */
    public String getProfile() {
        return profile;
    }

    /**
     * Sets authorization profile
     *
     * @param profile authorization profile
     */
    public void setProfile(String profile) {
        this.profile = profile;
    }

    /**
     * Returns id of the operatio
     *
     * @return id of the operatio
     */
    public String getId() {
        return id;
    }

    /**
     * Sets id of the operatio
     *
     * @param id id of the operatio
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns list of rules configured for the operation
     *
     * @return list of rules configured for the operation
     */
    public Collection getRules() {
        return rules.values();
    }

    /**
     * Sets list of rules configured for the operation
     *
     * @param rules list of rules configured for the operation
     */
    public void setRules(Collection rules) {
        if (rules != null) {
            for (Iterator it = rules.iterator(); it.hasNext();) {
                IAuthorizationRule ruleObj = (IAuthorizationRule) it.next();
                addRule(ruleObj);
            }
        }
    }

    /**
     * Add a rule to the rule list
     *
     * @param rule list of rules configured for the operation
     */
    public void addRule(IAuthorizationRule rule) {
        if (rule != null)
            this.rules.put(rule.getRuleId(), rule);
    }

    /**
     * Check whether the authentication is required for this operation
     * @return
     */
    public boolean isAuthenticationRequired() {
        return isAuthenticationRequired;
    }

    /**
     * set the authentication is required flag
     * @param authenticate
     */
    public void setAuthenticationRequired(boolean authenticate) {
        isAuthenticationRequired = authenticate;
    }

    /**
     * set the authentication is required flag
     * @param flag
     */
    public void setAuthenticationRequired(String flag) {
        if (flag != null && KnAASConstants.TRUE.equals(flag.trim())) {
            isAuthenticationRequired = true;
        } else {
            isAuthenticationRequired = false;
        }
    }
}
