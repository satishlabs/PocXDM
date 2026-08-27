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

/**
 * This method validates OSM code length allowed.
 */
public class KnMaxOSMShortTextLengthValidationRule extends KnValidatorRule {

    private static final KnLogger knLogger = KnLogger.getLogger(KnMaxOSMShortTextLengthValidationRule.class);

    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName = "validate()";
        IPersistenceDTO persistDTO = getDTO();
        if (persistDTO instanceof KnCorpOSMPersistDTO) {
            KnCorpOSMPersistDTO dto=(KnCorpOSMPersistDTO)persistDTO;
            knLogger.debug(methodName,"MAXSTATUSSHORTEXTLENGTH: ",dto.getMaxStatusShortTextLength());
            knLogger.debug(methodName,"getOperationType(): ",getOperationType());
            knLogger.debug(methodName,"modify osm : ",dto.getModifiedOSMMsgList());
            knLogger.debug(methodName,"add osm : ",dto.getAddedOSMMsgList());
            if(getOperationType().equals(KnOperationTypes.CREATE_OSM_LIST)&&dto.getAddedOSMMsgList()!=null) {
                //OSM SHORT TEXT REQUEST <= ALLOWED for CREATE_OSM_LIST
                validateShortTextAddedOSM(methodName, dto);
            }
            if(getOperationType().equals(KnOperationTypes.UPDATE_OSM_LIST)) {
                if(dto.getModifiedOSMMsgList()!=null){
                    //OSM SHORT TEXT REQUEST <= ALLOWED for UPDATE_OSM_LIST
                    for (KnXDMOSMInfoRequestDTO osmList : dto.getModifiedOSMMsgList()) {
                        if (osmList.getMsgShortText().length() <= Integer.parseInt(dto.getMaxStatusShortTextLength())) {
                            knLogger.debug(methodName, "validation passed for length osm list message short text in modifiedOSMMsgList");
                        } else {
                            knLogger.error(methodName, "Vladidation failed for allowed short text msg length:", dto.getMaxStatusShortTextLength());
                            knLogger.error(methodName, "short text msg in request:", osmList.getMsgShortText());
                            throw new KnCorpBOValidationException(KnErrorCodes.Validator.MAX_OSM_SHORT_TEXT_LENGTH_EXCEEDED,
                                    "validation failed for length osm list message short text in modifiedOSMMsgList", getEntityId()
                                    , getOperationType(), getRuleId(), dto.getModifiedOSMMsgList().toString(), "");
                        }
                    }
                } else if(dto.getAddedOSMMsgList()!=null){
                    validateShortTextAddedOSM(methodName, dto);
                }else{
                    knLogger.debug(methodName, "validation passed for MAXSTATUSSHORTEXTLENGTH.");
                }
            }

        }else {
            knLogger.error( "validate()", "Unexpected DTO passed - " , persistDTO.getClass() ,
                    ", Expected Dto - KnCorpOSMPersistDTO ");
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), "DataType", "");
        }
    }

    private void validateShortTextAddedOSM(String methodName, KnCorpOSMPersistDTO dto) throws KnCorpBOValidationException {
        for (KnXDMOSMInfoRequestDTO osmList : dto.getAddedOSMMsgList()) {
            if (osmList.getMsgShortText().length() <= Integer.parseInt(dto.getMaxStatusShortTextLength())) {
                knLogger.debug(methodName, "validation passed for length osm list message short text.");
            } else {
                knLogger.error(methodName, "Vladidation failed for allowed short text msg length:", dto.getMaxStatusShortTextLength());
                knLogger.error(methodName, "short text msg in request:", osmList.getMsgShortText());
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.MAX_OSM_SHORT_TEXT_LENGTH_EXCEEDED,
                        "validation failed for length osm list message short text in addedOSMMsgList", getEntityId()
                        , getOperationType(), getRuleId(),dto.getAddedOSMMsgList().toString(), "");
            }
        }
    }
}
