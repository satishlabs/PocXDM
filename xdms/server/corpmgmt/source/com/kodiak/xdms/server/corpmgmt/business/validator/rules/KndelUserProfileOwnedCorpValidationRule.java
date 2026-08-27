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


import java.util.List;

public class KndelUserProfileOwnedCorpValidationRule extends KnValidatorRule {

    private static final KnLogger knLogger = KnLogger.getLogger(KndelUserProfileOwnedCorpValidationRule.class);


    @Override
    public void validate() throws KnValidationException, KnBOException {

        final String methodName="validate()";
        IPersistenceDTO persistDTO = getDTO();
        if(persistDTO instanceof KnCorpUserProfilePersistDTO){
            KnCorpUserProfilePersistDTO upmDto=(KnCorpUserProfilePersistDTO)persistDTO;
            String reqCorpId = upmDto.getCorpId();
            boolean upmIsShared = upmDto.isSharedUpm();
            knLogger.debug(methodName, "upmIsShared: "+upmIsShared);
            List<Integer> sharedCorpListId = upmDto.getSharedCorpListId();
            knLogger.debug("sharedCorpListId: "+ sharedCorpListId);
            if(!upmIsShared){
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.USER_PROFILE_DELETE_NOT_ALLOWED_FOR_SHARED_CORP,
                        "UPM Cannot be deleted by sharedCorp", getEntityId()
                        , getOperationType(), getRuleId(),reqCorpId.toString(), "");
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
