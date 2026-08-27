/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpBCGrpPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KnGroupBroadcasterCoutValidator extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnGroupBroadcasterCoutValidator.class);
    private static final String ALLOWED_TYPES = "minBroadcasterCount";

    @Override
    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY Point");
        IPersistenceDTO persistDTO = getDTO();
        int minBrdstrCount = Integer.parseInt(getAttribute(ALLOWED_TYPES));
        knLogger.debug(methodName, "persistDTO obtained from the request is - ", persistDTO);
        if (persistDTO instanceof KnCorpBCGrpPersistDTO) {
            List<String> finalBroadcaster;
            KnCorpBCGrpPersistDTO bcGrpPersistDTO = (KnCorpBCGrpPersistDTO) persistDTO;
            finalBroadcaster = new ArrayList<>(bcGrpPersistDTO.getFinalBrdstrList());
            if (finalBroadcaster.size() < minBrdstrCount) {
                knLogger.error(methodName, "Alteat ", minBrdstrCount, " broadcaster required");
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.INVALID_BROADCASTER_COUNT,
                        "Broadcaster count is less than the required", getEntityId(), getOperationType(), getRuleId(),
                        Arrays.asList(minBrdstrCount).toString(), "");
            }

        } else {
            knLogger.error("validate", "Unexpected DTO passed - ", persistDTO.getClass());
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), "DataType", "");
        }
    }
}
