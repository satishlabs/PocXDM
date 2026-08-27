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
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnSublistDetailsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.Map;

public class KnLastMemberSublistUPMMapValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnLastMemberSublistUPMMapValidationRule.class);

    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName = "validate()";
        IPersistenceDTO persistDTO = getDTO();
        if (persistDTO instanceof KnSublistDetailsPersistDTO) {
            KnSublistDetailsPersistDTO corpSublistPersistDTO = (KnSublistDetailsPersistDTO) persistDTO;
            knLogger.debug(methodName, " corpSublistPersistDTO: ",corpSublistPersistDTO);
            Map<String, String> lastMemberSublistUPMMap = corpSublistPersistDTO.getLastMemberSublistUPMMap();
            knLogger.debug(methodName," lastMemberSublistUPMMap :",lastMemberSublistUPMMap);
            if(lastMemberSublistUPMMap!=null&&!lastMemberSublistUPMMap.isEmpty()){
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.LAST_MEMBER_SUBLIST_UPM_MAP_CHECK,
                        "last member sulist cannot be removed,until sublist unassigned from upm",
                        getEntityId(), getOperationType(), getRuleId(), lastMemberSublistUPMMap.toString(), "");

            }
        }

    }
}
