/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.subsmgmt.business.validator.rules;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.subsmgmt.business.validator.KnProvBOValidationException;
import com.kodiak.xdms.server.subsmgmt.dto.persistdat.KnSubsProfilePersistDTO;
import com.kodiak.xdms.server.subsmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvConstants;

public class KnInvalidTokenValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnInvalidTokenValidationRule.class);

    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName=" validate()";

        KnSubsProfilePersistDTO subsProfilePersistDTO=null;
        IPersistenceDTO persistDTO = getDTO();
        if (persistDTO instanceof KnSubsProfilePersistDTO) {
            subsProfilePersistDTO = (KnSubsProfilePersistDTO) persistDTO;
            if(subsProfilePersistDTO!=null
                    &&subsProfilePersistDTO.getProfileMdnMcpttId()!=null
                    &&subsProfilePersistDTO.getBaseMdnMcpttId()!=null
                    &&subsProfilePersistDTO.getProfileMdnMcpttId().equals(subsProfilePersistDTO.getBaseMdnMcpttId())){
                knLogger.debug("validation passed mdn and profile mdn are same");
            }else{
                knLogger.error( methodName, "validation failed mdn and profile mdn are not same " , persistDTO.getClass());
                throw new KnProvBOValidationException(KnErrorCodes.Validator.INVALID_TOKEN_MCPTTID,
                        "Validation failed, request values - " + subsProfilePersistDTO, entityId, operationId, getRuleId(),
                        KnProvConstants.KEY_DATATYPE_MDN, ((KnSubsProfilePersistDTO) persistDTO).getMdn(), "" + subsProfilePersistDTO);
            }
        } else {
            knLogger.error( methodName, "Invalid DTO is passed for validation " , persistDTO.getClass());
            throw new KnProvBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Un-expected dao DTO is passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), KnProvConstants.KEY_DATATYPE_MDN);

        }
    }
}
