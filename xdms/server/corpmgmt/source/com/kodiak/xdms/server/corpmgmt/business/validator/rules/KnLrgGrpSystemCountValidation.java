/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpBCGrpPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpUserProfilePersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

public class KnLrgGrpSystemCountValidation extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnLrgGrpSystemCountValidation.class);
    /**
     * validate method. all the validators should implement this method
     *
     * @throws KnValidationException
     */
    @Override
    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        IPersistenceDTO persistDTO = getDTO();
        if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
            KnCorpGroupInfoPersistDTO groupInfoPersistDTO = (KnCorpGroupInfoPersistDTO) persistDTO;
            if((groupInfoPersistDTO.isLargeGroup()) &&
                    (groupInfoPersistDTO.getLargeGrpCountInSystem() >= groupInfoPersistDTO.getMaxLargeGrpSystem())){
                knLogger.error(methodName, "Max allowed Large group in the system reached",
                        groupInfoPersistDTO.getLargeGrpCountInSystem(), groupInfoPersistDTO.getMaxLargeGrpSystem());
                throw new KnCorpBOValidationException(KnErrorCodes.BOEntity.MAX_LARGE_GROUP_PER_SYSTEM_EXCEEDED,
                        "Max allowed Large group in the system reached",
                        getEntityId(), getOperationType(), getRuleId(), String.valueOf(groupInfoPersistDTO.getMaxLargeGrpSystem()), "");
            }
        }else if (persistDTO instanceof KnCorpBCGrpPersistDTO){
            KnCorpBCGrpPersistDTO groupInfoPersistDTO = (KnCorpBCGrpPersistDTO) persistDTO;
            if((groupInfoPersistDTO.isLargeGroup()) &&
                    (groupInfoPersistDTO.getLargeGrpCountInSystem() >= groupInfoPersistDTO.getMaxLargeGrpSystem())){
                knLogger.error(methodName, "Max allowed Large group in the system reached",
                        groupInfoPersistDTO.getLargeGrpCountInSystem(), groupInfoPersistDTO.getMaxLargeGrpSystem());
                throw new KnCorpBOValidationException(KnErrorCodes.BOEntity.MAX_LARGE_GROUP_PER_SYSTEM_EXCEEDED,
                        "Max allowed Large group in the system reached",
                        getEntityId(), getOperationType(), getRuleId(), String.valueOf(groupInfoPersistDTO.getMaxLargeGrpSystem()), "");
            }
        } else if (persistDTO instanceof KnCorpUserProfilePersistDTO) {
            KnCorpUserProfilePersistDTO upmPersistDTO = (KnCorpUserProfilePersistDTO) persistDTO;
            if(upmPersistDTO.isSystemLevelLargeGroupLimitReached()){
                knLogger.error(methodName, "Max allowed Large group in the system reached",
                        "", "");
                throw new KnCorpBOValidationException(KnErrorCodes.BOEntity.MAX_LARGE_GROUP_PER_SYSTEM_EXCEEDED,
                        "Max allowed Large group in the system reached",
                        getEntityId(), getOperationType(), getRuleId(), "", "");
            }
        }
    }
}
