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

import java.util.BitSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class KnUPMexternalMemberPermChecker extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnUPMexternalMemberPermChecker.class);

    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY.");
        IPersistenceDTO persistDTO = getDTO();
        if (persistDTO instanceof KnCorpUserProfilePersistDTO) {
            KnCorpUserProfilePersistDTO userProfilePersistDTO = (KnCorpUserProfilePersistDTO) persistDTO;
            if (userProfilePersistDTO.getExternalPemMap() != null) {
                Map<String, BitSet> externalMemPermMap = userProfilePersistDTO.getExternalPemMap();
                if (externalMemPermMap != null && !externalMemPermMap.isEmpty()) {
                    for (BitSet permSet : externalMemPermMap.values()) {
                        if (permSet != null) {
                            if ((permSet.get(KnConstants.MCPTT_PERMISSION_BIT.AMBIENTLISTENING.value()) == Boolean.TRUE ||
                                    permSet.get(KnConstants.MCPTT_PERMISSION_BIT.AMBIENTLISTENING.DISCRETELISTENING.value()) == Boolean.TRUE ||
                                    permSet.get(KnConstants.MCPTT_PERMISSION_BIT.AMBIENTLISTENING.USERCHECK.value()) == Boolean.TRUE ||
                                    permSet.get(KnConstants.MCPTT_PERMISSION_BIT.AMBIENTLISTENING.USERENABLE.value()) == Boolean.TRUE ||
                                    permSet.get(KnConstants.MCPTT_PERMISSION_BIT.AMBIENTLISTENING.REMOTEEMERGENCYPERMISSION.value()) == Boolean.TRUE ||
                                    permSet.get(KnConstants.MCPTT_PERMISSION_BIT.AMBIENTLISTENING.MCVIDEOUNCONFIRMEDPULL.value()) == Boolean.TRUE)) {
                                knLogger.error(methodName, "External member is not allowed to have target permissions ");
                                throw new KnCorpBOValidationException(KnErrorCodes.Validator.SUBSCRIBER_DOES_NOT_BELONG_TO_CORP,
                                        "subscriber does not belong to the group", getEntityId(), getOperationType(), getRuleId(), "", "");
                            }
                        }
                    }
                }
            }

        }

    }
}
