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
import com.kodiak.xdms.server.subsmgmt.business.validator.KnProvBOValidationException;
import com.kodiak.xdms.server.subsmgmt.dto.persistdat.KnPAMAccPersistDTO;
import com.kodiak.xdms.server.subsmgmt.dto.persistdat.KnSubsProfilePersistDTO;
import com.kodiak.xdms.server.subsmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvConstants;

public class KnCorpSubscriptionValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnCorpSubscriptionValidationRule.class);
    private static final String className = KnCorpSubscriptionValidationRule.class.getName();

    public void validate() throws KnValidationException {
        String methodName = "validate()";
        boolean subscriptionTypeValid = false;
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug(methodName, "validating if corp subscription type is value is valid", "DTO received - ", persistDTO);
        KnSubsProfilePersistDTO subsProfilePersistDTO;
        KnPAMAccPersistDTO pamAccPersistDTO;
        int corpSubscriptionType;

        if (persistDTO instanceof KnPAMAccPersistDTO) {
            pamAccPersistDTO = (KnPAMAccPersistDTO) persistDTO;
            corpSubscriptionType = pamAccPersistDTO.getProfileDetails().getCorpSubsType();
            if (!(corpSubscriptionType == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value())) {
                knLogger.debug(methodName, "Validation failed for corp subscription Type");
                throw new KnProvBOValidationException(KnErrorCodes.Validator.INVALID_CORP_SUBSCRIPTION_TYPE,
                        "Validation failed, Expected values - " + corpSubscriptionType, entityId, operationId, getRuleId(),
                        KnProvConstants.KEY_DATATYPE_MDN, ((KnPAMAccPersistDTO) persistDTO).getBillingMdn(), "" + corpSubscriptionType);
            }

        } else if (persistDTO instanceof KnSubsProfilePersistDTO) {
            subsProfilePersistDTO = (KnSubsProfilePersistDTO) persistDTO;
            corpSubscriptionType = subsProfilePersistDTO.getCorporateSubscriptionType();
            KnConstants.CORP_SUBSCRIPTION_TYPE[] corpSubscriptionTypes = KnConstants.CORP_SUBSCRIPTION_TYPE.values();

            for (KnConstants.CORP_SUBSCRIPTION_TYPE type : corpSubscriptionTypes) {
                if (corpSubscriptionType == type.value()) {
                    subscriptionTypeValid = true;
                }
            }
            if (!subscriptionTypeValid) {
                knLogger.debug(methodName, "Validation failed for corp subscription Type");
                throw new KnProvBOValidationException(KnErrorCodes.Validator.INVALID_CORP_SUBSCRIPTION_TYPE,
                        "Validation failed, Expected values - " + corpSubscriptionType, entityId, operationId, getRuleId(),
                        KnProvConstants.KEY_DATATYPE_MDN, ((KnSubsProfilePersistDTO) persistDTO).getMdn(), "" + corpSubscriptionType);
            }

        } else {
            knLogger.error(methodName, "Invalid DTO is passed for validation ", persistDTO.getClass());
            throw new KnProvBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Un-expected dao DTO is passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), KnProvConstants.KEY_DATATYPE_MDN);

        }

        knLogger.info(methodName, "Corp subscription Type validated successfully");

    }
}
