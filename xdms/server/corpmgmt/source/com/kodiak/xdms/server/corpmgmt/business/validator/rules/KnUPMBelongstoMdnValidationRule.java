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

public class KnUPMBelongstoMdnValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnUPMBelongstoMdnValidationRule.class);


    @Override
    public void validate() throws KnValidationException, KnBOException {

        final String methodName="validate()";
        IPersistenceDTO persistDTO = getDTO();
        if(persistDTO instanceof KnCorpUserProfilePersistDTO){
            KnCorpUserProfilePersistDTO upmDto=(KnCorpUserProfilePersistDTO)persistDTO;
            KnCorpUserProfileDTO userProfile = upmDto.getUserProfileDTO();
            String profileId=upmDto.getUserProfileId();
            Boolean userProfiledoesntbelongstomdn = userProfile.isUserProfilebelongstomdn();

            knLogger.debug(methodName,"userProfile ",userProfile);
            if(!userProfiledoesntbelongstomdn){
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.USER_PROFILE_DOESNT_BELONG_TO_MDN,
                        "Userprofile doesn't belong to MDN", getEntityId()
                        , getOperationType(), getRuleId(),profileId, "");
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
