/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *****************************************************************************
 * File name:   KnPublicSubscriptionValidationRule.java
 * Subsystem:   Provisioning Library
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ravi Shanker .P       1/5/11       7.0
 * <p/>
 * <p/>
 * #401, 4th Floor, 'Prestige Sigma'
 * No.3, Vittal Mallya Road
 * Bangalore - 560 001
 * www.kodiaknetworks.com
 * All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * *******************************************************************************
 */

package com.kodiak.xdms.server.subsmgmt.business.validator.rules;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.common.resources.KnConstants;
import com.kodiak.xdms.server.subsmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.subsmgmt.business.validator.KnProvBOValidationException;
import com.kodiak.xdms.server.subsmgmt.dto.persistdat.KnSubsProfilePersistDTO;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvConstants;

public class KnPublicSubscriptionValidationRule extends KnValidatorRule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnPublicSubscriptionValidationRule.class);
    private static final String className = KnPublicSubscriptionValidationRule.class.getName();

    public void validate() throws KnValidationException {
        String methodName = "validate()";
        boolean subscriptionTypeValid = false;
        IPersistenceDTO persistDTO = getDTO();
        knLogger.info( methodName, "validating if public subscription type is value is valid");
        knLogger.debug( methodName, "DTO received - " , persistDTO);

        KnSubsProfilePersistDTO subsProfilePersistDTO;
        int publicSubscriptionType;
        if (persistDTO instanceof KnSubsProfilePersistDTO) {
            subsProfilePersistDTO = (KnSubsProfilePersistDTO) persistDTO;
            publicSubscriptionType = subsProfilePersistDTO.getPublicSubscriptionType();

        } else {
            knLogger.error( methodName, "Invalid DTO is passed for validation " , persistDTO.getClass());
            throw new KnProvBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Un-expected dao DTO is passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), KnProvConstants.KEY_DATATYPE_MDN);

        }

        KnConstants.PUBLIC_SUBSCRIPTION_TYPE[] publicSubscriptionTypes = KnConstants.PUBLIC_SUBSCRIPTION_TYPE.values();

        for (KnConstants.PUBLIC_SUBSCRIPTION_TYPE type : publicSubscriptionTypes) {
            if (publicSubscriptionType == type.value()) {
                subscriptionTypeValid = true;
            }
        }

        if (!subscriptionTypeValid) {
            knLogger.debug( methodName, "Validation failed for Public subscriptionType");
            throw new KnProvBOValidationException(KnErrorCodes.Validator.INVALID_PUBLIC_SUBSCRIPTION_TYPE,
                    "Validation failed, Expected values - " , entityId, operationId, getRuleId(),
                    KnProvConstants.KEY_DATATYPE_MDN, ((KnSubsProfilePersistDTO) persistDTO).getMdn(), "" + publicSubscriptionType);
        }

        knLogger.debug( methodName, "Public subscription Type validated successfully");

    }
}
