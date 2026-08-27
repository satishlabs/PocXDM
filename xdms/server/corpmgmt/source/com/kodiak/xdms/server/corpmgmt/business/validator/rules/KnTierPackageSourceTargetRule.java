/*
 *  Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.
 *  All Rights Reserved
 *  Motorola Solutions Confidential Restricted
 *
 */

package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCloningPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.Map;

public class KnTierPackageSourceTargetRule extends KnValidatorRule {

    private static final KnLogger knLogger = KnLogger.getLogger(KnTierPackageSourceTargetRule.class);
    private final String CLASS = KnTierPackageSourceTargetRule.class.getName();
    @Override
    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        try {
            IPersistenceDTO persistDTO = getDTO();
            knLogger.debug(methodName, "ENTRY - Validating the subscriber count should be equal to one  ");
            if (persistDTO instanceof KnCloningPersistDTO) {
                KnCloningPersistDTO contactDTO = (KnCloningPersistDTO) persistDTO;
                Map<String, String> mdnTierPackageMap = contactDTO.getMdnTierPackageMap();
                if (mdnTierPackageMap.values().stream().distinct().count() == 2) {
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.SOURCE_TARGET_MDN_DOES_NOT_BELONG_TO_SAME_TIER_PACKAGE,
                            "Source and Target mdn is not part of the same tier package", getEntityId(), getOperationType(), getRuleId(), "", "");
                }
            }

        } finally {
            knLogger.debug( methodName, "EXIT: Validation Completed Successfully");
        }
    }
}
