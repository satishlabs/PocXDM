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
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpBCGrpPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

public class KnPreConfigGroupFlagUpdateValidator extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnPreConfigGroupFlagUpdateValidator.class);

    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName = "validates()";
        IPersistenceDTO persistDTO = getDTO();
        if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
            KnCorpGroupInfoPersistDTO grpPersistDTO = (KnCorpGroupInfoPersistDTO) persistDTO;
           Integer reqIsPreConfiguredGroup = grpPersistDTO.getIsPreConfiguredGroup();
           Integer dbIsPreConfiguredGroup = grpPersistDTO.getDbIsPreConfiguredGroup() == null ? 0 : grpPersistDTO.getDbIsPreConfiguredGroup();
           knLogger.debug(methodName,""," Requested reqIsPreConfiguredGroup ", reqIsPreConfiguredGroup
             , " DB dbIsPreConfiguredGroup " , dbIsPreConfiguredGroup);

            if(reqIsPreConfiguredGroup !=null&&!reqIsPreConfiguredGroup.equals(dbIsPreConfiguredGroup))
            {
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.UPDATE_PRECONFIG_GROUP_PARAM_IS_NOT_ALLOWED,
                        "Pre-Config param is modified.", getEntityId(), getOperationType(), getRuleId(), "", "");
            }
        } else if (persistDTO instanceof KnCorpBCGrpPersistDTO) {
            KnCorpBCGrpPersistDTO bcGrpPersistDTO = (KnCorpBCGrpPersistDTO) persistDTO;
            Integer reqIsPreConfiguredGroup = bcGrpPersistDTO.getIsPreConfiguredGroup();
            Integer dbIsPreConfiguredGroup = bcGrpPersistDTO.getDbIsPreConfiguredGroup() == null ? 0 : bcGrpPersistDTO.getDbIsPreConfiguredGroup();
            knLogger.debug(methodName,""," Requested reqIsPreConfiguredGroup ", reqIsPreConfiguredGroup
                    , " DB dbIsPreConfiguredGroup " , dbIsPreConfiguredGroup);

            if(reqIsPreConfiguredGroup !=null&&!reqIsPreConfiguredGroup.equals(dbIsPreConfiguredGroup))
            {
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.UPDATE_PRECONFIG_GROUP_PARAM_IS_NOT_ALLOWED,
                        "Pre-Config param is modified.", getEntityId(), getOperationType(), getRuleId(), "", "");
            }
        }
    }
}
