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
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpBCGrpPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

public class KnCorpBCOsmListIdExistsValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnCorpBCOsmListIdExistsValidationRule.class);

    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName = "validate()";
        IPersistenceDTO persistDTO = getDTO();
        if (persistDTO instanceof KnCorpBCGrpPersistDTO) {
            KnCorpBCGrpPersistDTO dto=(KnCorpBCGrpPersistDTO)persistDTO;
            knLogger.debug( methodName,"OSMLISTID for validation",dto.getOSMListId());
            knLogger.debug( methodName,"OSMLISTID form DB. ",dto.getOsmListIdAndDefultMap());
            if(dto.getOSMListId()!=null&&!dto.getOSMListId().isEmpty()&&dto.getOsmListIdAndDefultMap()!=null
                    &&!dto.getOsmListIdAndDefultMap().isEmpty()
                    &&!dto.getOsmListIdAndDefultMap().containsKey(Integer.parseInt(dto.getOSMListId()))){
                knLogger.error( methodName, "validation failed osm listId in request is not present in DB." , dto.getOSMListId());
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.OSM_LIST_ID_NOT_EXISTS,
                        "validation failed osm listId in request is not present in DB. ", getEntityId(), getOperationType(), getRuleId(),dto.getOSMListId(), "");
            }else{
                knLogger.debug( methodName,"validation passed osm listId in request is present in DB.", dto.getOSMListId());
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
