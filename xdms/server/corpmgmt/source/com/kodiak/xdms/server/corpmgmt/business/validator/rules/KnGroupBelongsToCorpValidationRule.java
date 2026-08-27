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
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpGroupInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

public class KnGroupBelongsToCorpValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnGroupBelongsToCorpValidationRule.class);

    /**
     * validate method. all the validators should implement this method
     *
     * @throws KnValidationException
     * @throws KnBOException
     */

    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName = "validates()";
        knLogger.debug(methodName, "ENTRY.");
        IPersistenceDTO persistDTO = getDTO();
        if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
            knLogger.debug(methodName, "Bean is instance of KnCorpGroupInfoPersistDTO");
            KnCorpGroupInfoPersistDTO grpPersistDTO = (KnCorpGroupInfoPersistDTO) persistDTO;

            if(grpPersistDTO.getInputDTO() instanceof KnIPCorpGroupInfoDTO){
                KnIPCorpGroupInfoDTO inputDTO = (KnIPCorpGroupInfoDTO) grpPersistDTO.getInputDTO();
                int reqCorpId = grpPersistDTO.getCorpId();
                int groupBelongsCorpId = inputDTO.getCorpId();

                knLogger.debug(methodName, "", " Requested corpId ",reqCorpId, " groupBelongsCorpId ", groupBelongsCorpId);
                if(inputDTO.getCorpId() != grpPersistDTO.getCorpId())
                {
                    knLogger.error( methodName, "validation failed groupId in request is not present in corp" , reqCorpId);
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.CORP_GROUP_ID_NOT_EXISTS,
                            "validation failed groupId in request is not present in corp", getEntityId(), getOperationType(), getRuleId(), reqCorpId + "", "");
                }
            }

        }else{
            knLogger.error( "validate()", "Unexpected DTO passed - " , persistDTO.getClass() ,
                    ", Expected Dto - KnCorpGroupInfoPersistDTO ");
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), "DataType", "");
        }
    }
}
