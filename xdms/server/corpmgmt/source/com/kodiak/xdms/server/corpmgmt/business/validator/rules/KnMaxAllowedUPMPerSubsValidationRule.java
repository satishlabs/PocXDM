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

public class KnMaxAllowedUPMPerSubsValidationRule extends KnValidatorRule {

    private static final KnLogger knLogger = KnLogger.getLogger(KnMaxAllowedUPMPerSubsValidationRule.class);

    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName="validate()";
        IPersistenceDTO persistDTO = getDTO();
        if(persistDTO instanceof KnCorpUserProfilePersistDTO){
            KnCorpUserProfilePersistDTO upmDto=(KnCorpUserProfilePersistDTO)persistDTO;
            int maxUserrofilePerSub = Integer.parseInt(upmDto.getMaxUserProfilePerSub());
            int dbProfileMdnCount=0;
            if(upmDto.getDbProfileMdnList()!=null&&!upmDto.getDbProfileMdnList().isEmpty()){
                dbProfileMdnCount=upmDto.getDbProfileMdnList().size();
            }
            knLogger.debug(methodName,"maxUserrofilePerSub ",maxUserrofilePerSub,"dbProfileMdnCount ",dbProfileMdnCount);
            if(maxUserrofilePerSub<=dbProfileMdnCount){
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.MAX_UP_PER_SUBS_EXCEEDED,
                        "max allowed userProfile per subscriber exceeded", getEntityId()
                        , getOperationType(), getRuleId(),String.valueOf(maxUserrofilePerSub), "");
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
