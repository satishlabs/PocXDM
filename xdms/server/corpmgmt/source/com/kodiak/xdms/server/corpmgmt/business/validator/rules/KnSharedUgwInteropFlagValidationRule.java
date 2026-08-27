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

public class KnSharedUgwInteropFlagValidationRule extends KnValidatorRule {

    private static final KnLogger knLogger = KnLogger.getLogger(KnSharedUgwInteropFlagValidationRule.class);

    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName = "validate()";
        IPersistenceDTO persistDto = getDTO();
        if (persistDto instanceof KnCorpGroupInfoPersistDTO) {
            KnCorpGroupInfoPersistDTO groupPersistDto = (KnCorpGroupInfoPersistDTO) persistDto;

            boolean ugwSharedInteropFlag = groupPersistDto.isUgwInteropSharedCorpChange();
            knLogger.debug(methodName, "ugwSharedInteropFlag flag -", ugwSharedInteropFlag);

            if (ugwSharedInteropFlag) {
                knLogger.error(methodName, "ugwInterop flag not allowed to be changed from shared corp", ugwSharedInteropFlag);
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.UGWINTEROP_CHANGE_SHARED_CORP_NOT_ALLOWED,
                        "ugwInterop flag not allowed to be changed from shared corp", getEntityId(), getOperationType(), getRuleId(), String.valueOf(ugwSharedInteropFlag), "");
            }
        }
    }
}
