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
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpUserProfileDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpUserProfilePersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

public class KnUserProfileExistsValidationRule extends KnValidatorRule {

    private static final KnLogger knLogger = KnLogger.getLogger(KnUserProfileExistsValidationRule.class);

    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName = "validate():userProfileExists";
        IPersistenceDTO persistDTO = getDTO();
        if(persistDTO instanceof KnCorpUserProfilePersistDTO){
            KnCorpUserProfilePersistDTO dto=(KnCorpUserProfilePersistDTO)persistDTO;
            KnCorpUserProfileDTO dbUPM = dto.getDbUserProfileDTO();
            knLogger.debug("Profile name in DB ",dbUPM);
            if(dbUPM==null){
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.UP_NOT_EXIST_DB,
                        "userProfile not exists in db", getEntityId()
                        , getOperationType(), getRuleId(), dto.getUserProfileId(), "");
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
