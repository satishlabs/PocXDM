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

public class KnAssignUnassignUpmAllowedValiddationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnAssignUnassignUpmAllowedValiddationRule.class);

    @Override
    public void validate() throws KnValidationException, KnBOException {
        IPersistenceDTO persistDTO = getDTO();
        if (persistDTO instanceof KnCorpUserProfilePersistDTO) {
            KnCorpUserProfilePersistDTO userProfilePersistDTO = (KnCorpUserProfilePersistDTO) persistDTO;
            boolean isAssignUPMAllowed = userProfilePersistDTO.isAssignUPMAllowed();
            boolean isUnassignUPMAllowed = userProfilePersistDTO.isUnassignUPMAllowed();

            knLogger.debug("isUnassignUPMAllowed:",isUnassignUPMAllowed," isAssignUPMAllowed:",isAssignUPMAllowed);
            if(isUnassignUPMAllowed){
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.USER_PROFILE_SHARING_UNASSIGN_NOT_ALLOWED
                        , "unassign upm is not allowed for the mdn/corp"
                        , getEntityId(), getOperationType(), getRuleId(), "", "");
            }
            if(isAssignUPMAllowed){
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.USER_PROFILE_SHARING_ASSIGN_NOT_ALLOWED
                        , "assign upm is not allowed for the mdn/corp"
                        , getEntityId(), getOperationType(), getRuleId(), "", "");
            }
        }
    }
}
