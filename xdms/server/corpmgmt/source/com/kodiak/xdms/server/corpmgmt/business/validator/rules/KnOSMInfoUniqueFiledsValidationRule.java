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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class KnOSMInfoUniqueFiledsValidationRule extends KnValidatorRule {

    private static final KnLogger knLogger = KnLogger.getLogger(KnOSMInfoUniqueFiledsValidationRule.class);

    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName = "validate()";
        IPersistenceDTO persistDTO = getDTO();

        if (persistDTO instanceof KnCorpOSMPersistDTO) {
            KnCorpOSMPersistDTO dto=(KnCorpOSMPersistDTO)persistDTO;

            List<KnXDMOSMInfoRequestDTO> osmInfoList = dto.getOsmInfoList();
            Set<KnXDMOSMInfoRequestDTO> modifiedOsmList = dto.getModifiedOSMMsgList();
            List<KnXDMOSMInfoRequestDTO> addedOsmInfoList = new ArrayList<>();
            if(dto.getAddedOSMMsgList()!=null){
                addedOsmInfoList.addAll(dto.getAddedOSMMsgList());
            }
            int osmInfoCount=dto.getUniqueOSMInfoCount();
            knLogger.debug( methodName,"OSMINFOLIST in DB.",osmInfoList);
            knLogger.debug( methodName,"OSMINFOLIST in request ,modifiedOsmList:",modifiedOsmList);
            knLogger.debug( methodName,"OSMINFOLIST in request ,addedOsmInfoList:",addedOsmInfoList);
            knLogger.debug( methodName,"getOperationType() in request",getOperationType());
            knLogger.debug( methodName,"osmInfoCount ",osmInfoCount);
            if(getOperationType().equals(KnOperationTypes.UPDATE_OSM_LIST)) {
                if (modifiedOsmList != null) {
                    if (osmInfoCount > modifiedOsmList.size()) {
                        knLogger.error(methodName, "Validation failed modifiedOsmList : osm info is present in DB", dto.getOSMListId());
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.UNIQUE_OSM_INFO_LIST,
                                "Validation failed modifiedOsmList : osm info is present in DB", getEntityId(), getOperationType(), getRuleId(), "", "");
                    }
                }
                if (addedOsmInfoList != null) {
                    //Validating duplicate osm info in request
                    uniqueOSMInfoInReqValidation(methodName, dto, addedOsmInfoList);
                    if(osmInfoList!=null&&!osmInfoList.isEmpty()){
                        for (KnXDMOSMInfoRequestDTO addedOSMListReq : addedOsmInfoList) {
                            //Validating duplicate osm info req againt db.
                            validateUniqueOSMFields(methodName, dto, osmInfoList, addedOSMListReq);
                        }
                    }

                }
            }else if(getOperationType().equals(KnOperationTypes.CREATE_OSM_LIST) && addedOsmInfoList != null &&!addedOsmInfoList.isEmpty()) {
                uniqueOSMInfoInReqValidation(methodName, dto, addedOsmInfoList);
            }else{
                knLogger.info( methodName,"OSM uniQue validation passed as OSMINFOLIST in DB is null or empty.",osmInfoList);
            }

        }else {
            knLogger.error( "validate()", "Unexpected DTO passed - " , persistDTO.getClass() ,
                    ", Expected Dto - KnCorpOSMPersistDTO ");
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), "DataType", "");
        }
    }

    private void uniqueOSMInfoInReqValidation(String methodName, KnCorpOSMPersistDTO dto, List<KnXDMOSMInfoRequestDTO> addedOsmInfoList) throws KnCorpBOValidationException {
        AtomicInteger duplicateCount=new AtomicInteger();
        for (int i = 0; i < addedOsmInfoList.size(); i++)
        {
            for (int j = i+1; j < addedOsmInfoList.size(); j++)
            {
                KnXDMOSMInfoRequestDTO osm=addedOsmInfoList.get(i);
                KnXDMOSMInfoRequestDTO osm1=addedOsmInfoList.get(j);
                if(osm.getMsgId().equals(osm1.getMsgId())||
                        osm.getMsgOrderId().equals(osm1.getMsgOrderId())||
                        osm.getMsgShortText().equals(osm1.getMsgShortText())||
                        osm.getMsg().equals(osm1.getMsg())) {
                    duplicateCount.incrementAndGet();
                }

            }
        }
        knLogger.debug(methodName,"Duplicate osm info count",duplicateCount.intValue());
        if(duplicateCount.intValue()>=1){
            knLogger.error(methodName, "Validation failed,OSM info request contains duplicate", dto.getOSMListId());
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.UNIQUE_OSM_INFO,
                    "Validation failed,OSM info request contains duplicate",
                    getEntityId(), getOperationType(), getRuleId(),"OSM info duplicate count "+duplicateCount.intValue(), "");
        }
    }

    private void validateUniqueOSMFields(String methodName, KnCorpOSMPersistDTO dto, List<KnXDMOSMInfoRequestDTO> osmInfoList, KnXDMOSMInfoRequestDTO osmListReq) throws KnCorpBOValidationException {
        for(KnXDMOSMInfoRequestDTO dbOsm:osmInfoList) {
            if (dbOsm.getMsgId().equals(osmListReq.getMsgId())
                    ||dbOsm.getMsgOrderId().equals(osmListReq.getMsgOrderId())
                    ||dbOsm.getMsgShortText().equals(osmListReq.getMsgShortText())
                    ||dbOsm.getMsg().equals(osmListReq.getMsg())) {

                knLogger.error(methodName, "Validation failed modifiedOsmList : Request OSM info list is present in DB", dto.getOSMListId());
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.UNIQUE_OSM_INFO_LIST,
                        "Validation failed modifiedOsmList: Request OSM info list is present in DB",
                        getEntityId(), getOperationType(), getRuleId(), osmListReq.getMsgId()+"OSM already exists", "");
            }
        }
    }
}
