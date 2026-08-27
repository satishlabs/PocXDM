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
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpBulkGroupPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

public class KnCorpBulkGrpNameValidator extends KnValidatorRule {

    private static final KnLogger knLogger = KnLogger.getLogger(KnCorpBulkGrpNameValidator.class);

    /**
     * validate method. all the validators should implement this method
     *
     * @throws KnValidationException
     * @throws KnBOException
     */
    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY.");
        IPersistenceDTO persistDTO = getDTO();
        if (persistDTO instanceof KnCorpBulkGroupPersistDTO) {
            KnCorpBulkGroupPersistDTO grpPersistDTO = (KnCorpBulkGroupPersistDTO) persistDTO;
            knLogger.debug(methodName, "Group name list.", grpPersistDTO.getExisingGroupNameList());
            if (grpPersistDTO.getExisingGroupNameList() != null && !grpPersistDTO.getExisingGroupNameList().isEmpty()) {
                knLogger.error(methodName, "Group name passed already exist", grpPersistDTO.getExisingGroupNameList());
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.GROUP_ALREADY_EXISTS,
                        "Group name already exist in the corporation.", getEntityId(), getOperationType(), getRuleId(),
                        grpPersistDTO.getExisingGroupNameList().toString(), "");
            }
        }
        knLogger.debug(methodName, "Validation success.");
    }
}
