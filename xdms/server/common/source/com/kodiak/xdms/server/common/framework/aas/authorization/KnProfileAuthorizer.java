/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/***************************************************************************
 *
 * File name:   KnProfileAuthorizer.java
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


import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;

import java.util.Map;
import java.util.Iterator;
import java.util.Collection;


/**
 * This is an implementation of authorization profile. Each profile
 * has one instance of this class and each instance of this class
 * will hold a map where the key will be operation id and value
 * will be a Collection of rules. The collection of rules will have
 * only the enabled rules.
 */
public class KnProfileAuthorizer implements IProfileAuthorizer {
	private static final KnLogger knLogger = KnLogger.getLogger(KnProfileAuthorizer.class);

    private static final String CLASS = KnProfileAuthorizer.class.getName();

    private Map operationMap;

    /**
     * set the operations map
     *
     * @param operationMap
     */
    public void setOperations(Map operationMap) {
        this.operationMap = operationMap;
    }

    /**
     * get the operation config
     *
     * @param operation
     * @return the Opertaion configuration object
     */
    public KnAuthorizationOperationConfig getOperationConfig(String operation) {
        return (KnAuthorizationOperationConfig) operationMap.get(operation);
    }

    /**
     * the authorize method
     *
     * @param dto
     * @throws KnAuthorizationException
     */
    public final void authorize(IPersistenceDTO dto) throws KnAuthorizationException {
        String mName = "";
        String profile = dto.getProfile();
        String operation = dto.getOperationType();
        try {
            knLogger.debug( mName, "Starting Authorization for ", profile , " with operation - " , operation);
            KnAuthorizationOperationConfig operationConfig = getOperationConfig(operation);
            if (operationConfig == null) {
                knLogger.warn( mName, "Exiting authorization since no configuration found for operation - " , operation , ", profile - " , profile);
                return;
            }
            
            Collection rules = operationConfig.getRules();
            if (rules == null || rules.size() == 0) {
                knLogger.warn( mName, "Exiting authorization since no rules configured for operation - ", operation , ", profile - " , profile);
                return;
            }

            for (Iterator it = rules.iterator(); it.hasNext();) {
                IAuthorizationRule ruleObj = (IAuthorizationRule) it.next();
                String ruleId = ruleObj.getRuleId();
                knLogger.debug( mName, "Authorizing Profile: " , profile , ", Operation: " , operation ,", RuleId: " , ruleId);
                ruleObj.authorize(dto);
            }
            knLogger.debug( mName, "Authorization for " , profile , " with operation " , operation , " completed successfully.");
        } catch (KnAuthorizationException e) {
            knLogger.error( mName, "Authorization for " , profile , " with operation " , operation , " completed with error.");
            throw e;
        }
    }
}
