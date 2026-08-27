/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.rules.groupprofile;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpGroupDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpOSMPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.List;

public class KnGroupProfileExistsValidator extends KnValidatorRule {

    private static final KnLogger knLogger = KnLogger.getLogger(KnGroupProfileExistsValidator.class);

    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName = "KnGroupProfileExistsValidator.validate()";
        knLogger.info(methodName, "Validating group profile existence for operation: ", getOperationType());
        IPersistenceDTO persistDTO = getDTO();
        if (persistDTO instanceof KnCorpOSMPersistDTO dto) {
            List<KnCorpGroupDTO> corpGroupInfoList = dto.getCorpGroupInfoList();
            String osmListId = dto.getOSMListId();
            if (corpGroupInfoList == null || corpGroupInfoList.isEmpty()) {
                return;
            }
            knLogger.debug(methodName, "nonExistingGroupIdsInCorp :", corpGroupInfoList.toString());
            for (KnCorpGroupDTO groupInfo : corpGroupInfoList) {
                int groupId = groupInfo.getGroupId();
                String groupProfileId = groupInfo.getGroupProfileId();
                String groupDisplayName = groupInfo.getGroupDisplayName();
                if (null != groupProfileId) {
                    knLogger.error(methodName, "Validation failed: Group ", groupProfileId, " is linked to a profile and cannot be modified.");
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.GROUP_ASSOCIATED_WITH_PROFILE, "Group (" + groupDisplayName + ") is linked to a group profile and cannot be added or removed.",
                            getEntityId(), getOperationType(), getRuleId(), osmListId, "Group Id::" + groupId);
                }


            }
        }
        knLogger.info(methodName, "Validation passed: No group profiles are linked to the groups being modified.");
    }

}
