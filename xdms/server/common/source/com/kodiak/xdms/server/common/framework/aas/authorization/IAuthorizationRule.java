/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/***************************************************************************
 *
 * File name:   IAuthorizationRule.java
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

import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;

import java.util.Map;

/**
 * This is the base interface for all the authroizer rules.
 * All the authorization rules should implement this interface.
 */
public interface IAuthorizationRule extends Cloneable {

    public static final String DELIM = ",";

    /**
     * this method will return the profile name.
     *
     * @return the profile name
     */
    String getProfile();

    /**
     * this method will set the profile name
     *
     * @param profile the profile
     */
    void setProfile(String profile);

    /**
     * This method will return the rule id
     *
     * @return the rule id
     */
    String getRuleId();

    /**
     * this method will set the rule id to rule object
     *
     * @param ruleId the rule id
     */
    void setRuleId(String ruleId);

    /**
     * this method will return the operation id
     *
     * @return the operation id
     */
    String getOperationId();

    /**
     * this method will set the operation id to rule object
     *
     * @param operationId the operation id
     */
    void setOperationId(String operationId);

    /**
     * this method will set the rule parameters defined in the xml file
     * to the rule object.
     *
     * @param parameters the parameters
     */
    void addRuleParameters(Map parameters);

    /**
     * this method will do the atual authorization
     *
     * @throws KnAuthorizationException if there is any error
     * @param persistDTO
     */
    void authorize(IPersistenceDTO persistDTO) throws KnAuthorizationException;

    /**
     * this method will return the value
     *
     * @param parameterName the name of the parameter
     * @return the value for the parameter
     * @throws KnAuthorizationException if there is any error
     */
    String getRuleParameter(String parameterName) throws KnAuthorizationException;

}
