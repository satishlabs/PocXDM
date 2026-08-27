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
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupProfilePersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

public class KnUniqueGrpProfileNameValidator extends KnValidatorRule {

    private static final KnLogger knLogger = KnLogger.getLogger(KnUniqueGrpProfileNameValidator.class);

    /**
     * validate method. all the validators should implement this method
     *
     * @throws KnValidationException
     * @throws KnBOException
     */
    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName = "validate()";
        IPersistenceDTO persistDTO = getDTO();
        if (persistDTO instanceof KnCorpGroupProfilePersistDTO) {
            KnCorpGroupProfilePersistDTO grpPersistDTO = (KnCorpGroupProfilePersistDTO) persistDTO;
            knLogger.debug(methodName, "New name profile -", grpPersistDTO.getNewGrpNmaeProfile());
            if (grpPersistDTO.getNewGrpNmaeProfile() != null) {
                knLogger.error(methodName, "Group profilename passed already exist", grpPersistDTO.getNewGrpNmaeProfile().getProfileName());
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.GROUP_PROFILE_ALREADY_EXIST,
                        "Group profile name already exist in the corporation.", getEntityId(), getOperationType(), getRuleId(),
                        grpPersistDTO.getNewGrpNmaeProfile().getProfileName(), "");
            }
        }
        knLogger.debug(methodName, "Validation success.");
    }
}
