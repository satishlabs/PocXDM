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
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

public class KnLrgGrpCorpCountValidation extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnLrgGrpCorpCountValidation.class);

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
            if (groupInfoPersistDTO.isLargeGroup()) {
                boolean isLrgGrpExceeded = false;
                if ((groupInfoPersistDTO.getGroupCreatedBy() == KnConstants.CREATED_BY.ABDG.value()) &&
                        (groupInfoPersistDTO.getNumOfLrgAbdgGrp() >= groupInfoPersistDTO.getMaxLrgAbdgGrpPerCorp())) {
                    isLrgGrpExceeded = true;
                }
                if (groupInfoPersistDTO.getLargeGrpCountInCorp() >= groupInfoPersistDTO.getMaxLargeGrpCorp()) {
                    isLrgGrpExceeded = true;
                }
                if (isLrgGrpExceeded) {
                    knLogger.error(methodName, "Max allowed Large group in a corp reached",
                            groupInfoPersistDTO.getLargeGrpCountInCorp(), groupInfoPersistDTO.getMaxLargeGrpCorp());
                    throw new KnCorpBOValidationException(KnErrorCodes.BOEntity.MAX_LARGE_GROUP_PER_CORP_EXCEEDED,
                            "Max allowed Large group in a corp reached",
                            getEntityId(), getOperationType(), getRuleId(), String.valueOf(groupInfoPersistDTO.getMaxLargeGrpCorp()), "");
                }

            }
        } else if (persistDTO instanceof KnCorpBCGrpPersistDTO) {
            KnCorpBCGrpPersistDTO groupInfoPersistDTO = (KnCorpBCGrpPersistDTO) persistDTO;
            if ((groupInfoPersistDTO.isLargeGroup()) &&
                    (groupInfoPersistDTO.getLargeBCGrpCountInCorp() >= groupInfoPersistDTO.getMaxLargeBCGrpCorp())) {
                knLogger.error(methodName, "Max allowed Large group in a corp reached",
                        groupInfoPersistDTO.getLargeBCGrpCountInCorp(), groupInfoPersistDTO.getMaxLargeBCGrpCorp());
                throw new KnCorpBOValidationException(KnErrorCodes.BOEntity.MAX_LARGE_BCGROUP_PER_CORP_EXCEEDED,
                        "Max allowed Large group in a corp reached",
                        getEntityId(), getOperationType(), getRuleId(), String.valueOf(groupInfoPersistDTO.getMaxLargeBCGrpCorp()), "");
            }
        } else if (persistDTO instanceof KnCorpUserProfilePersistDTO) {
            KnCorpUserProfilePersistDTO upmPersistDTO = (KnCorpUserProfilePersistDTO) persistDTO;
            if (!upmPersistDTO.getLargeGroupIds().isEmpty()) {
                knLogger.error(methodName, "Max allowed Large group in a corp reached",
                        upmPersistDTO.getLargeGroupIds(), upmPersistDTO.getLargeGroupIds());
                throw new KnCorpBOValidationException(KnErrorCodes.BOEntity.MAX_LARGE_GROUP_PER_CORP_EXCEEDED,
                        "Max allowed Large group in a corp reached",
                        getEntityId(), getOperationType(), getRuleId(), String.valueOf(upmPersistDTO.getLargeGroupIds()), "");
            }
        }
    }
}
