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

public class KnUpmShareCorpOnlyMdnValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnUpmShareCorpOnlyMdnValidationRule.class);

    @Override
    public void validate() throws KnValidationException, KnBOException {
        IPersistenceDTO persistDTO = getDTO();
        if (persistDTO instanceof KnCorpUserProfilePersistDTO) {
            KnCorpUserProfilePersistDTO userProfilePersistDTO = (KnCorpUserProfilePersistDTO) persistDTO;
            boolean corpOnlySubscriber = userProfilePersistDTO.isOnlyCorpSubs();
            boolean sharedUpm = userProfilePersistDTO.isSharedUpm();
            boolean upmShareEnabled = userProfilePersistDTO.isUpmShareEnabled();
            knLogger.debug("corpOnlySubscriber:",corpOnlySubscriber
                    ," sharedUpm:",sharedUpm," upmShareEnabled:",upmShareEnabled);
            if(corpOnlySubscriber&&sharedUpm&&upmShareEnabled){
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.USER_PROFILE_SHARING_CORP_ONLY_SUBSCRPTION_MDN
                        , "Assigment of shared UPM not allowed for corporate only subscription mdn"
                        , getEntityId(), getOperationType(), getRuleId(), "", "");
            }
        }

    }
}
