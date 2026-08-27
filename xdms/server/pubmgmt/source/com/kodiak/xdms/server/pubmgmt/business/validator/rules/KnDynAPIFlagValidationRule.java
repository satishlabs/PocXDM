/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.business.validator.rules;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.pubmgmt.business.validator.KnPubBOValidationException;
import com.kodiak.xdms.server.pubmgmt.dto.persistdat.KnDynamicContactPersistDTO;
import com.kodiak.xdms.server.pubmgmt.dto.persistdat.KnDynamicGroupPersistDTO;
import com.kodiak.xdms.server.pubmgmt.resources.KnConstants;
import com.kodiak.xdms.server.pubmgmt.resources.KnErrorCodes;

public class KnDynAPIFlagValidationRule extends KnValidatorRule {

    private static final KnLogger knLogger = KnLogger.getLogger(KnDynAPIFlagValidationRule.class);

    /**
     * validate method. all the validators should implement this method
     *
     * @throws KnValidationException
     */
    @Override
    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug(methodName, "ENTRY : ");
        Boolean dynAPIFlag = false;
        if (persistDTO instanceof KnDynamicContactPersistDTO) {
            KnDynamicContactPersistDTO dynamicContPerDto = (KnDynamicContactPersistDTO) persistDTO;
            dynAPIFlag = dynamicContPerDto.getDynAPIServFlag();
        } else if (persistDTO instanceof KnDynamicGroupPersistDTO) {
            KnDynamicGroupPersistDTO dynamicGrpPerDto = (KnDynamicGroupPersistDTO) persistDTO;
            dynAPIFlag = dynamicGrpPerDto.getDynAPIServFlag();
        } else {
            knLogger.error(methodName, "Unexpected DTO passed - " + persistDTO.getClass() +
                    ", Expected Dto - KnDynamicContactPersistDTO ");
            throw new KnPubBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), KnConstants.KEY_DATATYPE_MDN, "");
        }

        if (!dynAPIFlag) {
            knLogger.error(methodName, "dynAPIFlag - ", dynAPIFlag);
            throw new KnPubBOValidationException(KnErrorCodes.Validator.DYNAMIC_API_SERVICE_FLAG_DISABLED,
                    "DYNAPI_SERVICE_FLAG is false - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), KnConstants.KEY_DATATYPE_MDN, "");
        }
    }
}