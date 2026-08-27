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

public class KnMaxLocWatchersPerGroupValidationRule extends KnValidatorRule {

    private static final KnLogger knLogger = KnLogger.getLogger(KnMaxLocWatchersPerGroupValidationRule.class);

    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName = "validate()";
        IPersistenceDTO persistDTO = getDTO();
        if (persistDTO instanceof KnCorpUserProfilePersistDTO) {
            KnCorpUserProfilePersistDTO dto = (KnCorpUserProfilePersistDTO) persistDTO;
            if (dto.getMaxLocWachersPerGroupReached()) {
                knLogger.error(methodName, "Mulitple LocWatcher not allowed for a group", KnErrorCodes.Validator.MAXIMUM_LOCWATCHER_EXCEEDED_FOR_GROUP);
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.MAXIMUM_LOCWATCHER_EXCEEDED_FOR_GROUP,
                        "Mulitple LocWatcher not allowed for a group.", getEntityId(), getOperationType(), getRuleId(),
                        "", "");
            }
        }
        knLogger.debug(methodName, "EXIT: Validation Completed Successfully");
    }
}