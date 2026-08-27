/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

import com.kodiak.common.resources.KnConstants;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpUserProfilePersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

public class KnUPMSharingFlagEnabledValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnUPMSharingFlagEnabledValidationRule.class);

    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName = "validate()";
        IPersistenceDTO persistDTO = getDTO();
        if (persistDTO instanceof KnCorpUserProfilePersistDTO) {
            KnCorpUserProfilePersistDTO userProfilePersistDTO = (KnCorpUserProfilePersistDTO) persistDTO;
            String dbUPMSharing = userProfilePersistDTO.getDbUPMSharingFlag();
            String reqUPMSharing = userProfilePersistDTO.getReqUPMSharingFlag();
            knLogger.debug(methodName, "ENTRY. dbUPMSharing",dbUPMSharing," reqUPMSharing :",reqUPMSharing);
            if(dbUPMSharing!=null&&reqUPMSharing!=null&&dbUPMSharing.equals(String.valueOf(KnConstants.DISABLED))
                    &&reqUPMSharing.equals(String.valueOf(KnConstants.ENABLED))){
                knLogger.error(methodName, "UPM sharing flag is disabled at system level");
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.USER_PROFILE_SHARING_DISABLED
                        , "UPM sharing flag is disabled at system level", getEntityId(), getOperationType(), getRuleId(), "", "");
            }
        }
    }
}
