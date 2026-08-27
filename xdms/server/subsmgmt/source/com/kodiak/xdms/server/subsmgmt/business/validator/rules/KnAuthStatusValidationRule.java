/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *****************************************************************************
 * File name:   KnAuthStatusValidationRule.java
 * Subsystem:   Provisioning Library
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ravi Shanker .P       1/14/11       7.0
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
import com.kodiak.xdms.server.subsmgmt.business.validator.KnProvBOValidationException;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnIPSubsProvInfoDTO;
import com.kodiak.xdms.server.subsmgmt.dto.persistdat.KnSubsProfilePersistDTO;
import com.kodiak.xdms.server.subsmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvConstants;

public class KnAuthStatusValidationRule extends KnValidatorRule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnAuthStatusValidationRule.class);
    private static final String className = KnAuthStatusValidationRule.class.getName();

    public void validate() throws KnValidationException {
        String methodName = "validate()";
        IPersistenceDTO persistDTO = getDTO();

        knLogger.debug( methodName, "Validating Mdn Status with DTO -" , persistDTO);

        KnSubsProfilePersistDTO subsProfilePersistDTO = null;
        if (persistDTO instanceof KnSubsProfilePersistDTO) {
            subsProfilePersistDTO = (KnSubsProfilePersistDTO) persistDTO;
        } else {
            knLogger.error( methodName, "Invalid DTO is passed for validation " , persistDTO.getClass());
            throw new KnProvBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Un-expected dao DTO is passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), KnProvConstants.KEY_DATATYPE_MDN);
        }

        KnIPSubsProvInfoDTO subsProvInfoDTO = null;
        try {
            subsProvInfoDTO = (KnIPSubsProvInfoDTO) subsProfilePersistDTO.getInputDTO();
            knLogger.debug( methodName, " input dto for validation - " , subsProvInfoDTO);
        } catch (Exception e) {
            throw new KnProvBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Un-expected input DTO is passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), KnProvConstants.KEY_DATATYPE_MDN);
        }

        Integer serviceAuthStatus = subsProvInfoDTO.getServiceAuthStatus();

        if (serviceAuthStatus == KnConstants.SERVICE_AUTH_STATUS.ACTIVATED.value() ||
                serviceAuthStatus == KnConstants.SERVICE_AUTH_STATUS.DEACTIVATED.value() ||
                serviceAuthStatus == KnConstants.SERVICE_AUTH_STATUS.PROVISIONED.value()) {
            knLogger.debug( methodName, "Valid service auth status is passed " , serviceAuthStatus
            );
        } else {
            knLogger.debug( methodName, "Service auth status is not activated or deactivated or Provisioned - " , serviceAuthStatus
            );
            throw new KnProvBOValidationException(KnErrorCodes.Validator.INVALID_SERVICE_AUTH_STATUS,
                    "Invalid Service Auth Status is passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), KnProvConstants.KEY_DATATYPE_MDN);
        }

        knLogger.debug( methodName, "Exit: Successfully validated the Mdn Status means 'Validating Mdn Status with input DTO ' ");
    }

}

