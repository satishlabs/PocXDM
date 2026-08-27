/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/***************************************************************************
 *
 * File name:   IProfileAuthorizer.java
 * Subsystem:   Framework Configuration
 *
 * Name                 Date       Release
 * -------------------- ---------- ---------------------------------------
 * Jiji Sasidharan      03-05-2006 5.7
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
 * This is the base interface for profile authorizer. All the profile
 * Authorizers should implement this interface.
 */
public interface IProfileAuthorizer {

    /**
     * This method will authorize a request.
     *
     * @throws KnAuthorizationException if there is any of the
     *                                  authorization rule fails
     */
    void authorize(IPersistenceDTO dto) throws KnAuthorizationException;

    /**
     * This method will set the operations
     *
     * @param operationMap
     */
    void setOperations(Map operationMap);

    /**
     * Return the operation configuration of a given operation,
     *
     * @param operation the operation id
     * @return the operation configuration
     */
    KnAuthorizationOperationConfig getOperationConfig(String operation);

}
