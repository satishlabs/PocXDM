/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *****************************************************************************
 * File name:   KnSubsClientTypeValidationRule.java
 * Subsystem:
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ravi Shanker .P       11/12/11       7.0
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
import com.kodiak.xdms.server.subsmgmt.dto.persistdat.KnPAMAccPersistDTO;
import com.kodiak.xdms.server.subsmgmt.dto.persistdat.KnSubsProfilePersistDTO;
import com.kodiak.xdms.server.subsmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvConstants;

public class KnSubsClientTypeValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnSubsClientTypeValidationRule.class);
    private static final String CLASS_NAME = KnSubsClientTypeValidationRule.class.getName();

    public void validate() throws KnValidationException {
        String methodName = "validate()";
        boolean subsClientTypeValid = false;
        IPersistenceDTO persistDTO = getDTO();
        knLogger.info( methodName, "validating if subs client type is value is valid");
        knLogger.debug( methodName, "DTO received - " , persistDTO);

        KnSubsProfilePersistDTO subsProfilePersistDTO;
        KnPAMAccPersistDTO pamAccPersistDTO;
        Integer subsClientType;
        String emailId;

        if (persistDTO instanceof KnPAMAccPersistDTO) {
            pamAccPersistDTO = (KnPAMAccPersistDTO) persistDTO;
            subsClientType = pamAccPersistDTO.getProfileDetails().getClient_Type();
            if (!(subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.CROSS_CARRIER_PTT_CLIENT.value()
                    || subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.DESKTOP.value()
                    || subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.DISPATCH_CLIENT.value()
                    || subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.POC_WIFIONLY.value()
                    || subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.THIRDPARTYPOCCLIENT.value())) {
                knLogger.debug(methodName, "Validation failed for corp subscription Type");
                throw new KnProvBOValidationException(KnErrorCodes.Validator.INVALID_CORP_SUBSCRIPTION_TYPE,
                        "Validation failed, Expected values - " + subsClientType, entityId, operationId, getRuleId(),
                        KnProvConstants.KEY_DATATYPE_MDN, ((KnPAMAccPersistDTO) persistDTO).getBillingMdn(), "" + subsClientType);
            }
        } else if (persistDTO instanceof KnSubsProfilePersistDTO) {
            subsProfilePersistDTO = (KnSubsProfilePersistDTO) persistDTO;
            KnIPSubsProvInfoDTO subsProvInputDTO = (KnIPSubsProvInfoDTO) subsProfilePersistDTO.getInputDTO();
            subsClientType = subsProvInputDTO.getSubsClientType();
            KnProvConstants.SUBS_CLIENT_TYPE[] subsClientTypes = KnProvConstants.SUBS_CLIENT_TYPE.values();
            if (subsClientType != null) {
                for (KnProvConstants.SUBS_CLIENT_TYPE type : subsClientTypes) {
                    if (subsClientType == type.value()) {
                        subsClientTypeValid = true;
                    }
                }
            } else {
                subsClientTypeValid = true;
            }
            /*if(subsClientType != null && subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.CROSS_CARRIER_PTT_CLIENT.value()){
                subsClientTypeValid = false;
            }*/
            if (!subsClientTypeValid) {
                knLogger.debug( methodName, "Validation failed for subs client type Type");
                throw new KnProvBOValidationException(KnErrorCodes.Validator.INVALID_SUBS_CLIENT_TYPE,
                        "Validation failed, Expected values - " , entityId, operationId, getRuleId(),
                        KnProvConstants.KEY_DATATYPE_MDN, ((KnSubsProfilePersistDTO) persistDTO).getMdn(), "" + subsClientType);
            }

        } else {
            knLogger.error( methodName, "Invalid DTO is passed for validation " , persistDTO.getClass());
            throw new KnProvBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Un-expected dao DTO is passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), KnProvConstants.KEY_DATATYPE_MDN);

        }
        knLogger.debug( methodName, "subs client type validated successfully");
    }
}
