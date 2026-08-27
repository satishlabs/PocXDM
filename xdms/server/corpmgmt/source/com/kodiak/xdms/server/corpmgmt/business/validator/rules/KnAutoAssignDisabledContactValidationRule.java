/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpUserProfilePersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import static com.kodiak.xdms.server.corpmgmt.resources.KnConstants.DIST_POLICY_USER_PROFILE;

public class KnAutoAssignDisabledContactValidationRule extends KnValidatorRule {

    private static final KnLogger knLogger = KnLogger.getLogger(KnAutoAssignDisabledContactValidationRule.class);

    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName = "validate()";
        IPersistenceDTO persistDTO = getDTO();
        if (persistDTO instanceof KnCorpUserProfilePersistDTO) {
            KnCorpUserProfilePersistDTO dto = (KnCorpUserProfilePersistDTO) persistDTO;

            knLogger.debug("auto assign :", dto.getAutoAssign());
            if (dto.getAutoAssign()!=null&&dto.getAutoAssign() != DIST_POLICY_USER_PROFILE) {
                knLogger.error(methodName,"Sublist is auto assign enabled");
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.UP_AUTO_ASSIGN_DISABLED,
                        "Sublist is not auto assign enabled", getEntityId()
                        , getOperationType(), getRuleId(), String.valueOf(dto.getAutoAssign()), "");
            }else{
                knLogger.debug(methodName,"Sublist is auto assign enabled");
            }

        } else {
            knLogger.error("validate()", "Unexpected DTO passed - ", persistDTO.getClass(),
                    ", Expected Dto - KnCorpUserProfilePersistDTO ");
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), "DataType", "");
        }
    }
}
