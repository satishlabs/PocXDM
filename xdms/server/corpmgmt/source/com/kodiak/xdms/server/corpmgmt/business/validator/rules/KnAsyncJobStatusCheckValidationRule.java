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

public class KnAsyncJobStatusCheckValidationRule extends KnValidatorRule {

    private static final KnLogger knLogger = KnLogger.getLogger(KnAsyncJobStatusCheckValidationRule.class);


    @Override
    public void validate() throws KnValidationException, KnBOException {

        final String methodName="validate()";
        IPersistenceDTO persistDTO = getDTO();
        if(persistDTO instanceof KnCorpUserProfilePersistDTO){
            KnCorpUserProfilePersistDTO upmDto=(KnCorpUserProfilePersistDTO)persistDTO;
            List<Integer> jobStatusList = upmDto.getJobStatusList();
            String userProfileId = upmDto.getUserProfileId();

            knLogger.debug(methodName,"jobStatusList  ",jobStatusList,"userProfileId :",userProfileId);
            if(jobStatusList!=null&&!jobStatusList.isEmpty()){
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.UP_ASYNC_JOB_EXISTS,
                        "Async job exists for the upm", getEntityId()
                        , getOperationType(), getRuleId(),userProfileId, "");
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
