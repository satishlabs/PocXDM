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

public class KnUpmSharedWithMdnCorpValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnUpmSharedWithMdnCorpValidationRule.class);

    @Override
    public void validate() throws KnValidationException, KnBOException {

        IPersistenceDTO persistDTO = getDTO();
        if (persistDTO instanceof KnCorpUserProfilePersistDTO) {
            KnCorpUserProfilePersistDTO userProfilePersistDTO = (KnCorpUserProfilePersistDTO) persistDTO;
            //upm sharing enabled at system level
            boolean upmShareEnabled = userProfilePersistDTO.isUpmShareEnabled();
            //upm is shared with the corp of the mdn
            boolean sharedUpmForMdnCorp = userProfilePersistDTO.isSharedUpmForMdnCorp();
            String requestCorpId = userProfilePersistDTO.getCorpId();
            boolean upmIsShared = userProfilePersistDTO.isSharedUpm();
            knLogger.debug("upmShareEnabled :",upmShareEnabled," sharedUpmForMdnCorp:",sharedUpmForMdnCorp);
            if(sharedUpmForMdnCorp&&!upmShareEnabled){
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.USER_PROFILE_SHARING_DISABLED
                        , "UPM sharing flag is disabled at system level", getEntityId(), getOperationType(), getRuleId(), "", "");
            }
            if(upmIsShared&&!sharedUpmForMdnCorp){
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.USER_PROFILE_SHARING_VALID_TRUSTMATRIX
                        , "UPM is not shared with assigned mdn corp", getEntityId(), getOperationType(), getRuleId(), "", requestCorpId);
            }
        }
    }
}
