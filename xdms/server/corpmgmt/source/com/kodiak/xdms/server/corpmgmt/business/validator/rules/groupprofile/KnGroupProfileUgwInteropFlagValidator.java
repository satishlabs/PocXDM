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
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupProfilePersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

public class KnGroupProfileUgwInteropFlagValidator extends KnValidatorRule {

    private static final KnLogger knLogger = KnLogger.getLogger(KnGroupProfileUgwInteropFlagValidator.class);

    /**
     * validate method. all the validators should implement this method
     *
     * @throws KnValidationException
     * @throws KnBOException
     */
    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName = "validate()";
        IPersistenceDTO persistDto = getDTO();
        if (persistDto instanceof KnCorpGroupProfilePersistDTO) {
            KnCorpGroupProfilePersistDTO groupPersistDto = (KnCorpGroupProfilePersistDTO) persistDto;

            String ugwInteropFlag = groupPersistDto.getUgwInteropSystemConfig();
            knLogger.debug(methodName, "ugwInteropFlag flag -", ugwInteropFlag);

            if (null != ugwInteropFlag && !ugwInteropFlag.equals("1")) {
                knLogger.error(methodName, "ugwInteropFlag flag is disabled", ugwInteropFlag);
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.GROUP_PROFILE_FEATURE_NOT_ALLOWED,
                        "ugwInteropFlag flag is disabled", getEntityId(), getOperationType(), getRuleId(), ugwInteropFlag, "");
            }
        }
        if (persistDto instanceof KnCorpGroupInfoPersistDTO) {
            KnCorpGroupInfoPersistDTO groupPersistDto = (KnCorpGroupInfoPersistDTO) persistDto;

            String ugwInteropFlag = groupPersistDto.getUgwInteropSystemConfig();
            knLogger.debug(methodName, "ugwInteropFlag flag -", ugwInteropFlag);

            if (null != ugwInteropFlag && !ugwInteropFlag.equals("1")) {
                knLogger.error(methodName, "ugwInteropFlag flag is disabled", ugwInteropFlag);
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.GROUP_PROFILE_FEATURE_NOT_ALLOWED,
                        "ugwInteropFlag flag is disabled", getEntityId(), getOperationType(), getRuleId(), ugwInteropFlag, "");
            }
        }
    }
}
