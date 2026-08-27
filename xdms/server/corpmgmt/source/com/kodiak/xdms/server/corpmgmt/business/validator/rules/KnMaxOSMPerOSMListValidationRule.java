/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

import com.kodiak.common.commdto.request.KnXDMOSMInfoRequestDTO;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpOSMPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.corpmgmt.resources.KnOperationTypes;

import java.util.List;

/**
 * This method validates max allowed OSMLIST to be stored in DB
 */
public class KnMaxOSMPerOSMListValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnMaxOSMPerOSMListValidationRule.class);
    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName = "validate()";
        IPersistenceDTO persistDTO = getDTO();
        if (persistDTO instanceof KnCorpOSMPersistDTO) {
            KnCorpOSMPersistDTO dto=(KnCorpOSMPersistDTO)persistDTO;
            knLogger.debug( methodName,"getOperationType() in request",getOperationType());
            List<KnXDMOSMInfoRequestDTO> osmInfoList = dto.getOsmInfoList();
            if(dto.getAddedOSMMsgList()!=null) {
                if(getOperationType().equals(KnOperationTypes.UPDATE_OSM_LIST)) {
                    int osmInDBCount=0;
                    if(osmInfoList!=null&&!osmInfoList.isEmpty()){
                        osmInDBCount=osmInfoList.size();
                    }
                    //DB COUNT+REQUEST <= ALLOWED
                    int totalOsmCount = osmInDBCount + dto.getAddedOSMMsgList().size();
                    validateAllowedOsmInfoPerOSMList(methodName, dto, totalOsmCount);
                }else if(getOperationType().equals(KnOperationTypes.CREATE_OSM_LIST)){
                    //REQUEST <= ALLOWED
                    int totalOsmCount = dto.getAddedOSMMsgList().size();
                    validateAllowedOsmInfoPerOSMList(methodName, dto, totalOsmCount);
                }

            }else{
                knLogger.debug(methodName, "validation passed,No added osm to validate in the requets.");
            }
        }else {
            knLogger.error( "validate()", "Unexpected DTO passed - " , persistDTO.getClass() ,
                    ", Expected Dto - KnCorpOSMPersistDTO ");
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), "DataType", "");
        }
    }

    private void validateAllowedOsmInfoPerOSMList(String methodName, KnCorpOSMPersistDTO dto, int totalOsmCount) throws KnCorpBOValidationException {
        knLogger.debug(methodName, "totalOsmCount :", totalOsmCount);
        if (totalOsmCount <= Integer.parseInt(dto.getMaxStatusMsgPerOsmList())) {
            knLogger.debug(methodName, "validation passed for max osm status list per corp to be stored in DB");
        } else {
            knLogger.error(methodName, "Allowed max osm list per corp", dto.getMaxStatusMsgPerOsmList());
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.MAX_STATUS_MSG_PER_OSM_LIST_DB_EXCEEDED,
                    "validation failed for max osm status list per corp to be stored in DB. ",
                    getEntityId(), getOperationType(), getRuleId(),"totalOsmCount="+totalOsmCount, "");
        }
    }
}
