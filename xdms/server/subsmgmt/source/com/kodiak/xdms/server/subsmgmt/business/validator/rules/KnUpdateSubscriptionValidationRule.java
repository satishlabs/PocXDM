/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *****************************************************************************
 * File name:   KnUpdateSubscriptionValidationRule.java
 * Subsystem:   Provisioning Library
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ravi Shanker .P       2/9/11       7.0
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
import com.kodiak.xdms.server.common.util.KnGeneralUtil;
import com.kodiak.xdms.server.subsmgmt.business.validator.KnProvBOValidationException;
import com.kodiak.xdms.server.subsmgmt.dto.persistdat.KnSubsProfilePersistDTO;
import com.kodiak.xdms.server.subsmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvConstants;

public class KnUpdateSubscriptionValidationRule extends KnValidatorRule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnUpdateSubscriptionValidationRule.class);

    private static final String className = KnUpdateSubscriptionValidationRule.class.getName();
    public static final String SUBSCRIPTION_TYPE = "subscriptionType";

    public static final String PUBLIC_SUBSCRIPTION_TYPE_RULE_ID = "publicSubscriptionChecker";
    public static final String CORP_SUBSCRIPTION_TYPE_RULE_ID = "corpSubscriptionChecker";
    public static final String DELIM = ",";

    public void validate() throws KnValidationException {
        String methodName = "validate()";

        String expectedSubType = getAttribute(SUBSCRIPTION_TYPE);
        boolean subscriptionTypeValid = false;

        int subscriptionType = -1;
        IPersistenceDTO persistDTO = getDTO();
        knLogger.info( methodName, "validating if public subscription type is value is valid");
        knLogger.debug( methodName, "DTO received - " , persistDTO);

        KnSubsProfilePersistDTO subsProfilePersistDTO;

        if (persistDTO instanceof KnSubsProfilePersistDTO) {
            subsProfilePersistDTO = (KnSubsProfilePersistDTO) persistDTO;

        } else {
            knLogger.error( methodName, "Invalid DTO is passed for validation " , persistDTO.getClass());
            throw new KnProvBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Un-expected dao DTO is passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), KnProvConstants.KEY_DATATYPE_MDN);

        }

        if (expectedSubType == null || expectedSubType.trim().equals("")) {
            knLogger.error( "validate", "Validation failed since parameter '" , SUBSCRIPTION_TYPE , "' is not configured.");
            throw new KnProvBOValidationException(KnErrorCodes.Validator.CONFIG_ERROR, SUBSCRIPTION_TYPE + " parameter is not configured.",
                    getEntityId(), getOperationType(), getRuleId(), KnProvConstants.KEY_DATATYPE_MDN,
                    ((KnSubsProfilePersistDTO) persistDTO).getMdn(), "" + expectedSubType);
        }

        if (getRuleId().equalsIgnoreCase(PUBLIC_SUBSCRIPTION_TYPE_RULE_ID)) {
            knLogger.debug( methodName, "Validating the Public Subscription type");
            subscriptionType = subsProfilePersistDTO.getPublicSubscriptionType();
        } else if (getRuleId().equalsIgnoreCase(CORP_SUBSCRIPTION_TYPE_RULE_ID)) {
            knLogger.debug( methodName, "Validating the Corp Subscription type");
            subscriptionType = subsProfilePersistDTO.getCorporateSubscriptionType();
        }


        int[] subTypes = KnGeneralUtil.convertStringToIntArray(expectedSubType, DELIM);

        for (int subType : subTypes) {
            if (subscriptionType != -1) {
                if (subscriptionType == subType) {
                    subscriptionTypeValid = true;
                    break;
                }
            } else {
                subscriptionTypeValid = true;
            }
        }

        if (!subscriptionTypeValid) {
            knLogger.debug( methodName, "Validation failed for Public subscriptionType");
            throw new KnProvBOValidationException(KnErrorCodes.Validator.INVALID_PUBLIC_SUBSCRIPTION_TYPE,
                    "Validation failed, Expected values - " + subscriptionType, entityId, operationId, getRuleId(),
                    KnProvConstants.KEY_DATATYPE_MDN, ((KnSubsProfilePersistDTO) persistDTO).getMdn(), "" + subscriptionType);
        }

        knLogger.debug( methodName, "Public subscription Type validated successfully");

    }
}
