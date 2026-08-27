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

/**
 * This method validates OSM LIST MESSAGE LENGTH
 */
public class KnMaxOSMSListMsgLengthValidtionRule extends KnValidatorRule {

    private static final KnLogger knLogger = KnLogger.getLogger(KnMaxOSMSListMsgLengthValidtionRule.class);

    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName = "validate()";
        IPersistenceDTO persistDTO = getDTO();
        if (persistDTO instanceof KnCorpOSMPersistDTO) {
            KnCorpOSMPersistDTO dto=(KnCorpOSMPersistDTO)persistDTO;
            knLogger.debug( methodName,"added osm",dto.getAddedOSMMsgList());
            knLogger.debug( methodName,"modified osm",dto.getModifiedOSMMsgList());
            if(dto.getAddedOSMMsgList()!=null) {
                //OSM LIST MESSAGE LENGTH <= ALLOWED
                for (KnXDMOSMInfoRequestDTO osmList : dto.getAddedOSMMsgList()) {
                    if (osmList.getMsg().length() <= Integer.parseInt(dto.getMaxStatusMsgLength())) {
                        knLogger.debug(methodName, "OSM message length validation passed for added osm");
                    } else {
                        knLogger.error(methodName, "Allowed max osm msg length", dto.getMaxStatusMsgLength());
                        knLogger.error(methodName, "osm length received in added osm request", osmList.getMsg());
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.MAX_OSM_MSG_LENGTH_EXCEEDED,
                                "validation failed for length osm list message", getEntityId(), getOperationType(), getRuleId(), osmList.getMsg(), "");
                    }
                }
            }else if(dto.getModifiedOSMMsgList()!=null){
                for (KnXDMOSMInfoRequestDTO osmList : dto.getModifiedOSMMsgList()) {
                    if (osmList.getMsg().length() <= Integer.parseInt(dto.getMaxStatusMsgLength())) {
                        knLogger.debug(methodName, "OSM message length validation passed for modified osm");
                    } else {
                        knLogger.error(methodName, "Allowed max osm msg length", dto.getMaxStatusMsgLength());
                        knLogger.error(methodName, "osm length received in modified osm request", osmList.getMsg());
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.MAX_OSM_MSG_LENGTH_EXCEEDED,
                                "validation failed for length osm list message", getEntityId(), getOperationType(), getRuleId(), osmList.getMsg(), "");
                    }
                }
            }else{
                knLogger.debug( methodName,"validation passed, AddedOSMMsgList and ModifiedOSMMsgList is not present");
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
