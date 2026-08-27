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
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

public class KnPreConfigGroupMemberValidator extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnPreConfigGroupMemberValidator.class);

    /**
     * validate method. all the validators should implement this method
     *
     * @throws KnValidationException
     * @throws KnBOException
     */
    private static final int IS_PRE_CONFIG_GROUP_ENABLE = 1;
    private static final int IS_PRE_CONFIG_GROUP_DISABLE = 0;

    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName = "validates()";
        knLogger.debug(methodName, "ENTRY.");
        IPersistenceDTO persistDTO = getDTO();
        if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
            knLogger.debug(methodName, "Bean is instance of KnCorpGroupInfoPersistDTO");
            KnCorpGroupInfoPersistDTO grpPersistDTO = (KnCorpGroupInfoPersistDTO) persistDTO;
            grpPersistDTO.getInputDTO();
            knLogger.debug(methodName, "", " Requested isPreConfigGroup ", grpPersistDTO.getIsPreConfiguredGroup()
                    , " grpPersistDTO.getAddedMdnList() ", grpPersistDTO.getAddedMdnList());
            boolean isPreConfiguredGroup = true;
            if (grpPersistDTO.getIsPreConfiguredGroup() == null || grpPersistDTO.getIsPreConfiguredGroup() == IS_PRE_CONFIG_GROUP_DISABLE) {
                knLogger.debug(methodName, "Not a preConfigGroup. so skipping.");
                isPreConfiguredGroup = false;
            }
            if (isPreConfiguredGroup && (grpPersistDTO.getAddedMdnList() != null && !grpPersistDTO.getAddedMdnList().isEmpty()))
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.UPDATE_PRECONFIG_GROUP_MEMBER_IS_NOT_ALLOWED,
                        "Pre-Config member is not allowed for update.", getEntityId(), getOperationType(), getRuleId(), "", "");

        }
    }
}
