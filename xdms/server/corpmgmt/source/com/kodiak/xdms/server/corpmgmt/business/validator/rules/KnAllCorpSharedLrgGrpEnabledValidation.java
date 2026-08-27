/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p>
 * File name:  KnAllCorpSharedLrgGrpEnabledValidation.java
 * Subsystem:  XDM
 * <p>
 * Name                         Date                     Release
 * --------------------     ----------------        ------------------
 * Sudhendu Nayak             16-June-2023                  12.3.1.2
 * <p>
 * <p>
 * The information disclosed herein is confidential and proprietary to Kodiak and/or Motorola Solutions, Inc.,
 * and is being furnished under a valid license agreement. This information may not be disclosed to third parties
 * without the prior written consent of Kodiak and/or Motorola Solutions, Inc. The recipient of this information
 * shall respect the security status of the information.
 * <p>
 * MOTOROLA, MOTO, MOTOROLA SOLUTIONS, and the Stylized M Logo are trademarks or registered trademarks of Motorola
 * Trademark Holdings, LLC and are used under license. All other trademarks are the property of their respective owners.
 * © 2022 Motorola Solutions, Inc. All rights reserved.
 * ************************************************************************
 */

package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpBCGrpPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.Map;

import static com.kodiak.xdms.server.corpmgmt.resources.KnConstants.LARGE_GROUP_DISABLED;

public class KnAllCorpSharedLrgGrpEnabledValidation extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnAllCorpSharedLrgGrpEnabledValidation.class);


    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName = "validate()";
        IPersistenceDTO persistDTO = getDTO();
        if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
            KnCorpGroupInfoPersistDTO groupInfoPersistDTO = (KnCorpGroupInfoPersistDTO) persistDTO;
            Map<Integer, Integer> oneCorplargeGroupsDisabledMap = groupInfoPersistDTO.getCorpIdAndLargeGroupFlagMap();
            knLogger.info(methodName," oneCorplargeGroupsDisabledMap:",oneCorplargeGroupsDisabledMap);
            if (oneCorplargeGroupsDisabledMap != null
                    && !oneCorplargeGroupsDisabledMap.isEmpty()
                    && oneCorplargeGroupsDisabledMap.containsValue(LARGE_GROUP_DISABLED)) {
                knLogger.error(methodName, "Group cannot be converted to larege group" +
                        ",As Shared or Owned corp doesn't have large group flag enabled", oneCorplargeGroupsDisabledMap);
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.ALL_CORP_NOT_LARGE_GROUP_ENABLED,
                        "Shared or Owned corp doesn't have large group flag enabled",
                        getEntityId(), getOperationType(), getRuleId(), String.valueOf(groupInfoPersistDTO.getMaxLargeGrpSystem()), "");
            }
        } else if (persistDTO instanceof KnCorpBCGrpPersistDTO) {
            KnCorpBCGrpPersistDTO groupInfoPersistDTO = (KnCorpBCGrpPersistDTO) persistDTO;
            Map<Integer, Integer> oneCorplargeGroupsDisabledMap = groupInfoPersistDTO.getCorpIdAndLargeGroupFlagMap();
            if (oneCorplargeGroupsDisabledMap != null
                    && !oneCorplargeGroupsDisabledMap.isEmpty()
                    && oneCorplargeGroupsDisabledMap.containsValue(LARGE_GROUP_DISABLED)) {
                knLogger.error(methodName, "Group cannot be converted to larege group" +
                        ",As Shared or Owned corp doesn't have large group flag enabled", oneCorplargeGroupsDisabledMap);
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.ALL_CORP_NOT_LARGE_GROUP_ENABLED,
                        "Shared or Owned corp doesn't have large group flag enabled",
                        getEntityId(), getOperationType(), getRuleId(), String.valueOf(groupInfoPersistDTO.getMaxLargeGrpSystem()), "");
            }
        }
    }
}
