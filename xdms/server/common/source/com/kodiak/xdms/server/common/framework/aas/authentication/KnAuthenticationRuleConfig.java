/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/****************************************************************************
 *
 * File name:   KnAuthenticationRuleConfig.java
 * Subsystem:   WGP
 *
 * Name                 Release    Date
 * -------------------- ---------- ---------------------------------------
 * Jiji Sasidharan      6.0        Nov 27, 2006       
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
 * PLEASE NOTE: This is a replica of KnAuthorizationRuleConfig class.
 * This is replicated only to make authentication and authorization independant
 * and thus avoid naming collission and confusions. It does the same thing
 * the KnAuthorizationRuleConfig do.
 */
public class KnAuthenticationRuleConfig implements Cloneable {

    private String id;
    private String className;
    private Class ruleClass;

    private Map ruleParameterMap;

    /**
     * constructor
     */
    public KnAuthenticationRuleConfig() {
        this.ruleParameterMap = new HashMap();
    }

    /**
     * This method will return the id
     *
     * @return the id
     */
    public String getAuthRuleId() {
        return id;
    }

    /**
     * This method will set the id
     *
     * @param id the id
     */
    public void setAuthRuleId(String id) {
        this.id = id;
    }

    /**
     * This method will return the class name
     *
     * @return the class name
     */
    public String getClassName() {
        return className;
    }

    /**
     * this method will set the class name
     *
     * @param className
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * this method will return the class object
     *
     * @return the class object
     */
    public Class getRuleClass() {
        return ruleClass;
    }

    /**
     * This method will set the class object
     *
     * @param ruleClass the class object
     */
    public void setRuleClass(Class ruleClass) {
        this.ruleClass = ruleClass;
    }

    /**
     * return the rule parameters map
     *
     * @return the rule parameters map
     */
    public Map getRuleParameters() {
        return ruleParameterMap;
    }

    /**
     * set the deafult rule parameters map
     *
     * @param ruleParamterMap the rule parameters map
     */
    public void setRuleParameters(Map ruleParamterMap) {
        this.ruleParameterMap.putAll(ruleParamterMap);
    }

    /**
     * Create a new instance from the current instance
     * @return the copy of the instance
     * @throws CloneNotSupportedException
     */
    public Object clone() throws CloneNotSupportedException {
        KnAuthenticationRuleConfig config = new KnAuthenticationRuleConfig();
        config.id = id;
        config.ruleClass = ruleClass;
        config.ruleParameterMap.putAll(ruleParameterMap);
        return config;
    }
}
