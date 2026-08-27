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
import com.kodiak.xdms.server.corpmgmt.resources.KnOperationTypes;

public class KnUniqueUserProfileNameValidationRule extends KnValidatorRule {

    private static final KnLogger knLogger = KnLogger.getLogger(KnUniqueUserProfileNameValidationRule.class);

    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName = "validate()";
        IPersistenceDTO persistDTO = getDTO();
        if(persistDTO instanceof KnCorpUserProfilePersistDTO){
            KnCorpUserProfilePersistDTO dto=(KnCorpUserProfilePersistDTO)persistDTO;
            String dbProfileName=dto.getDbProfileName();
            String reqProfileName=dto.getReqProfileName();
            String dbProfileNameById=dto.getDbUserProfileDTO()!=null?dto.getDbUserProfileDTO().getUserProfileName():null;
            knLogger.debug("Profile name in DB ",dbProfileName);
            knLogger.debug("Profile name in req ",reqProfileName);
            knLogger.debug("Profile name in DB based on id ",dbProfileNameById);
            if (getOperationType().equals(KnOperationTypes.CREATE_USER_PROFILE)){
                if(reqProfileName!=null&&dbProfileName!=null){
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.UNIQUE_UP_NAME,
                            "userProfileName exists in db", getEntityId()
                            , getOperationType(), getRuleId(), reqProfileName, "");
                }
            } else if (getOperationType().equals(KnOperationTypes.UPDATE_USER_PROFILE)){
                    if(reqProfileName!=null
                            &&dbProfileName!=null
                            &&!reqProfileName.equals(dbProfileNameById)){
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.UNIQUE_UP_NAME,
                                "userProfileName exists in db", getEntityId()
                                , getOperationType(), getRuleId(), reqProfileName, "");
                    }
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
