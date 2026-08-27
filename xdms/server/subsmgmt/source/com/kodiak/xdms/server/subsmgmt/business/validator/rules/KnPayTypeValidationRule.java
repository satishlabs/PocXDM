/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *****************************************************************************
 * File name:   Kn.java
 * Subsystem:
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
import com.kodiak.xdms.server.subsmgmt.business.validator.KnProvBOValidationException;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnIPSubsProvInfoDTO;
import com.kodiak.xdms.server.subsmgmt.dto.persistdat.KnSubsProfilePersistDTO;
import com.kodiak.xdms.server.subsmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvConstants;

public class KnPayTypeValidationRule extends KnValidatorRule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnPayTypeValidationRule.class);
    private static final String className = KnPayTypeValidationRule.class.getName();

    public void validate() throws KnValidationException {
        String methodName = "validate()";
        boolean payTypeValid = false;
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug( methodName, "validating if pay type value is valid", "DTO received - " + persistDTO);

        KnSubsProfilePersistDTO subsProfilePersistDTO;
        int payType;
        if (persistDTO instanceof KnSubsProfilePersistDTO) {
            subsProfilePersistDTO = (KnSubsProfilePersistDTO) persistDTO;
            KnIPSubsProvInfoDTO subsProvInputDTO = (KnIPSubsProvInfoDTO) subsProfilePersistDTO.getInputDTO();
            payType = subsProvInputDTO.getPayType();

        } else {
            knLogger.error( methodName, "Invalid DTO is passed for validation " + persistDTO.getClass());
            throw new KnProvBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Un-expected dao DTO is passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), KnProvConstants.KEY_DATATYPE_MDN);

        }

        KnProvConstants.PAY_TYPE[] payTypes = KnProvConstants.PAY_TYPE.values();

        if (payType != -1) {
            for (KnProvConstants.PAY_TYPE type : payTypes) {
                if (payType == type.value()) {
                    payTypeValid = true;
                }
            }
        } else {
            payTypeValid = true;
        }

        if (!payTypeValid) {
            knLogger.debug( methodName, "Validation failed for corp subscription Type");
            throw new KnProvBOValidationException(KnErrorCodes.Validator.INVALID_PAY_TYPE,
                    "Validation failed, Expected values - " , entityId, operationId, getRuleId(),
                    KnProvConstants.KEY_DATATYPE_MDN, ((KnSubsProfilePersistDTO) persistDTO).getMdn(), "" + payType);
        }

        knLogger.debug( methodName, "Exit: Pay Type validated successfully with inputDTO");

    }
}
