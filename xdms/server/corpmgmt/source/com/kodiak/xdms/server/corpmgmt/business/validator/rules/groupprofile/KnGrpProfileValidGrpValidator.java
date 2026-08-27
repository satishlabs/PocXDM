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
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupProfilePersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.ArrayList;
import java.util.List;

public class KnGrpProfileValidGrpValidator extends KnValidatorRule {

    private static final KnLogger knLogger = KnLogger.getLogger(KnGrpProfileValidGrpValidator.class);

    /**
     * validate method. all the validators should implement this method
     *
     * @throws KnValidationException
     * @throws KnBOException
     */
    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName = "validate()";
        IPersistenceDTO persistDTO = getDTO();
        if (persistDTO instanceof KnCorpGroupProfilePersistDTO) {
            KnCorpGroupProfilePersistDTO grpPersistDTO = (KnCorpGroupProfilePersistDTO) persistDTO;
            knLogger.debug(methodName, "New name profile -", grpPersistDTO.getNewGrpNmaeProfile());
            List<Integer> reqGroupList = grpPersistDTO.getRequestGroupList();
            List<Integer> dbGroupList = grpPersistDTO.getDbGroupList();
            if(reqGroupList !=null && dbGroupList != null){
                List<Integer> invalidGrps = new ArrayList<>(reqGroupList);
                invalidGrps.removeAll(dbGroupList);
                if (!invalidGrps.isEmpty()) {
                    knLogger.error(methodName, "Group passed not exist", invalidGrps);
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.GROUP_NOT_ASSOCIATED_PROFILE,
                            "Group passed not associated with profile", getEntityId(), getOperationType(), getRuleId(),
                            invalidGrps.toString(), "");
                }
            }
        }
        knLogger.debug(methodName, "Validation success.");
    }
}
