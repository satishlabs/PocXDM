/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

import com.kodiak.common.commdto.request.KnCorpOperationStatusMesssageInfoDTO;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpOSMPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.corpmgmt.resources.KnOperationTypes;

import java.util.Set;

public class KnUniqueOSMListNameValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnUniqueOSMListNameValidationRule.class);

    @Override
    public void validate() throws KnValidationException, KnBOException {

        final String methodName = "validate()";
        IPersistenceDTO persistDTO = getDTO();
        if (persistDTO instanceof KnCorpOSMPersistDTO) {
            KnCorpOSMPersistDTO dto = (KnCorpOSMPersistDTO) persistDTO;
            Set<KnCorpOperationStatusMesssageInfoDTO> corpOsmList = dto.getCorpOSMList();
            knLogger.debug(methodName, "DB value", corpOsmList, "request value ", dto.getOSMListName());
            knLogger.debug(methodName, "getOperationType() : ", getOperationType());
            knLogger.debug(methodName, "getOSMListDetails() : ", dto.getOSMListDetails());
            if (corpOsmList != null) {
                if (getOperationType().equals(KnOperationTypes.CREATE_OSM_LIST)) {
                    for (KnCorpOperationStatusMesssageInfoDTO corpOsm : corpOsmList) {
                        if (dto.getOSMListName() != null && corpOsm.getOSMListName() != null
                                && corpOsm.getOSMListName().equals(dto.getOSMListName())) {
                            knLogger.error(methodName, "validation failed,Already OSM List Name exists");
                            throw new KnCorpBOValidationException(KnErrorCodes.Validator.OSM_LIST_NAME_EXISTS,
                                    "validation failed,Already OSM List Name exists", getEntityId(), getOperationType(), getRuleId(), dto.getOSMListName(), "");
                        }
                    }
                } else if (getOperationType().equals(KnOperationTypes.UPDATE_OSM_LIST)) {
                    if (dto.getOSMListDetails() != null && dto.getOSMListDetails().getOSMListName().equals(dto.getOSMListName())) {
                        knLogger.debug(methodName, "validation passed,OSM List Name is same in DB and req.");
                    } else {
                        for (KnCorpOperationStatusMesssageInfoDTO corpOsm : corpOsmList) {
                            if (dto.getOSMListName() != null && corpOsm.getOSMListName() != null
                                    && corpOsm.getOSMListName().equals(dto.getOSMListName())) {
                                knLogger.error(methodName, "validation failed,Already OSM List Name exists");
                                throw new KnCorpBOValidationException(KnErrorCodes.Validator.OSM_LIST_NAME_EXISTS,
                                        "validation failed,Already OSM List Name exists", getEntityId(), getOperationType(), getRuleId(), dto.getOSMListName(), "");
                            }
                        }
                    }

                }
            }else {
                    knLogger.debug(methodName, "validation passed,OSM List Name doesn't exists in DB");
                }
            } else {
                knLogger.error("validate()", "Unexpected DTO passed - ", persistDTO.getClass(),
                        ", Expected Dto - KnCorpOSMPersistDTO ");
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                        "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                        getEntityId(), getOperationType(), getRuleId(), "DataType", "");
            }
        }
    }

