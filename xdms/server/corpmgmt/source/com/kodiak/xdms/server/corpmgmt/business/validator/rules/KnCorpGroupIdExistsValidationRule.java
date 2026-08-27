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

import java.util.List;

public class KnCorpGroupIdExistsValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnCorpGroupIdExistsValidationRule.class);

    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName = "validate()";
        IPersistenceDTO persistDTO = getDTO();
        if (persistDTO instanceof KnCorpOSMPersistDTO) {
            KnCorpOSMPersistDTO dto=(KnCorpOSMPersistDTO)persistDTO;
            List<Integer> nonExistingGroupIdsInCorp = dto.getGroupIds();
            knLogger.debug( methodName,"nonExistingGroupIdsInCorp :",nonExistingGroupIdsInCorp);
            if(nonExistingGroupIdsInCorp!=null&&!nonExistingGroupIdsInCorp.isEmpty()){
                knLogger.error( methodName, "validation failed groupIds in request is not present in corp" , nonExistingGroupIdsInCorp);
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.CORP_GROUP_ID_NOT_EXISTS,
                        "validation failed groupIds in request is not present in corp", getEntityId(), getOperationType(), getRuleId(), nonExistingGroupIdsInCorp.toString(), "");
            }
        }else {
            knLogger.error( "validate()", "Unexpected DTO passed - " , persistDTO.getClass() ,
                    ", Expected Dto - KnCorpOSMPersistDTO ");
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), "DataType", "");
        }
    }
}
