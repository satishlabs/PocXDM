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
import com.kodiak.xdms.server.common.resources.KnConstants;
import com.kodiak.xdms.server.subsmgmt.business.validator.KnProvBOValidationException;
import com.kodiak.xdms.server.subsmgmt.dto.persistdat.KnSubsProfilePersistDTO;
import com.kodiak.xdms.server.subsmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvConstants;

public class KnPairingIndValidationRule extends KnValidatorRule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnPairingIndValidationRule.class);
    private static final String className = KnPairingIndValidationRule.class.getName();

    public void validate() throws KnValidationException {
        String methodName = "validate()";

        IPersistenceDTO persistDTO = getDTO();
        KnSubsProfilePersistDTO subsProfilePersistDTO;
        if (persistDTO instanceof KnSubsProfilePersistDTO) {
            subsProfilePersistDTO = (KnSubsProfilePersistDTO) persistDTO;
        } else {
            knLogger.error( methodName, "Invalid DTO is passed for validation " , persistDTO.getClass());
            throw new KnProvBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Un-expected dao DTO is passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), KnProvConstants.KEY_DATATYPE_MDN);
        }

        int publicSubscriptionType = subsProfilePersistDTO.getPublicSubscriptionType();
        int corpSubscriptionType = subsProfilePersistDTO.getCorporateSubscriptionType();
        Boolean pairingInd = subsProfilePersistDTO.getPairingInd();
        if (publicSubscriptionType == KnConstants.PUBLIC_SUBSCRIPTION_TYPE.PUBLIC.value()) {
            if (corpSubscriptionType == KnConstants.CORP_SUBSCRIPTION_TYPE.NONE.value()) {
                if (pairingInd != null)
                    if (pairingInd) {
                        knLogger.error( methodName, "Pairing Indicator cannot be true for public subscriber");
                        throw new KnProvBOValidationException(KnErrorCodes.Validator.PAIRING_IND_NOT_ALLOWED,
                                "Invalid Pairing Ind is passed - " + pairingInd,
                                entityId, operationId, getRuleId(), KnProvConstants.KEY_DATATYPE_MDN);
                    }
            }
        }
    }
}

