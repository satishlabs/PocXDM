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

public class KnGroupModifyOsmChecker extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnGroupModifyOsmChecker.class);

    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName = "validate()";
        IPersistenceDTO persistDTO = getDTO();
        if(persistDTO instanceof KnCorpGroupInfoPersistDTO) {
            KnCorpGroupInfoPersistDTO groupPersistDTO = (KnCorpGroupInfoPersistDTO) persistDTO;
            knLogger.debug(methodName, "DTO passed in the request is - ", groupPersistDTO);
            if(groupPersistDTO.isOsmListChanged()){
                if(groupPersistDTO.getGroupCorpId() != groupPersistDTO.getCorpProfile().getCorpId()){
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.SHARED_CORPORATE_OSM_NOT_ALLOWED,
                            "Shared corp not allowed to modify osm", getEntityId(), getOperationType(), getRuleId(),
                            "", "");
                }
            }
        }
    }
}
