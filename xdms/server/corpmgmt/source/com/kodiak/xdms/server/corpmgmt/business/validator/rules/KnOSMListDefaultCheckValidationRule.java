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
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpOSMPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.corpmgmt.resources.KnOperationTypes;

/**
 * This method validates if the OSM LIST contains default.
 */
public class KnOSMListDefaultCheckValidationRule extends KnValidatorRule {

    private static final KnLogger knLogger = KnLogger.getLogger(KnOSMListDefaultCheckValidationRule.class);
    final static int defaultOSM=1;

    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName = "validate()";
        IPersistenceDTO persistDTO = getDTO();
        if (persistDTO instanceof KnCorpOSMPersistDTO) {
            KnCorpOSMPersistDTO dto = (KnCorpOSMPersistDTO) persistDTO;
            knLogger.debug(methodName, "DB default", dto.getOsmListIdAndDefultMap().containsValue(1), "request default ", dto.getIsDefault());
            //DB conatins default && default in request then is invalid,only one default per corp.
            if (getOperationType().equals(KnOperationTypes.CREATE_OSM_LIST)&&
                    Integer.parseInt(dto.getIsDefault()) == defaultOSM&&
                    dto.getOsmListIdAndDefultMap().containsValue(defaultOSM)) {
                knLogger.error(methodName, "validation failed,Already  OSM default list exists");
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.OSM_LIST_DEFAULT_LIMIT,
                        "validation failed,Already  OSM default list exists  ", getEntityId(), getOperationType(), getRuleId(), dto.getIsDefault(), "");

            }else if(getOperationType().equals(KnOperationTypes.UPDATE_OSM_LIST)&&
                    Integer.parseInt(dto.getIsDefault()) == defaultOSM&&
                    dto.getOsmListIdAndDefultMap().containsValue(defaultOSM)&&
                    dto.getOsmListIdAndDefultMap().get(Integer.valueOf(dto.getOSMListId())).intValue()!=defaultOSM
                    ) {

                knLogger.error(methodName, "validation failed,Already  OSM default list exists");
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.OSM_LIST_DEFAULT_LIMIT,
                        "validation failed,Already  OSM default list exists  ", getEntityId(), getOperationType(), getRuleId(), dto.getIsDefault(), "");
            }else {
                knLogger.debug(methodName, "validation passed,As OSM default list is not present.");
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
