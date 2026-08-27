/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/****************************************************************************
 *
 * File name:   IAuthenticationRule.java
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

import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;

import java.util.Map;


/**
 * Base interface for all the autentication rules.
 * This interface defines a few methods which all the authentication
 * rules must implement
 *
 * @author <a href="mailto:sjiji@kodiaknetworks.com">Jiji Sasidharan</a>
 */
public interface IAuthenticationRule extends Cloneable {

    public static final String DELIM = ",";

    /**
     * Return profile name.
     *
     * @return the profile name
     */
    String getProfile();

    /**
     * Set the profile name
     *
     * @param profile the profile
     */
    void setProfile(String profile);

    /**
     * Return rule id
     *
     * @return the rule id
     */
    String getRuleId();

    /**
     * Set rule id
     *
     * @param ruleId the rule id
     */
    void setRuleId(String ruleId);

    /**
     * Set the parameters configured in the xml for this rule
     *
     * @param parameters the parameters
     */
    void addRuleParameters(Map parameters);

    /**
     * Authenticate the user
     *
     * @throws KnAuthenticationException if there is any error
     * @param persistDTO
     */
    void authenticate(IPersistenceDTO persistDTO) throws KnAuthenticationException;

    /**
     * Return the value of a rule parameter.
     *
     * @param parameterName the name of the parameter
     * @return the value for the parameter
     * @throws KnAuthenticationException if there is any error
     */
    String getRuleParameter(String parameterName) throws KnAuthenticationException;
    
}
