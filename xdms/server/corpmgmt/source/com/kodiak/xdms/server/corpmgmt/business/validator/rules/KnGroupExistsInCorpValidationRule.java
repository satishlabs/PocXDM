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
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGroupListInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpUserProfilePersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.corpmgmt.resources.KnOperationTypes;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

public class KnGroupExistsInCorpValidationRule extends KnValidatorRule {

    private static final KnLogger knLogger = KnLogger.getLogger(KnGroupExistsInCorpValidationRule.class);

    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName = "validate()";
        IPersistenceDTO persistDTO = getDTO();
        if (persistDTO instanceof KnCorpUserProfilePersistDTO) {
            KnCorpUserProfilePersistDTO dto = (KnCorpUserProfilePersistDTO) persistDTO;
            //create UPM
            if(getOperationType().equals(KnOperationTypes.CREATE_USER_PROFILE)&&dto.getCorpGroupList()!=null&&
                    dto.getUserProfileDTO().getGroupList()!=null&&
                    !dto.getUserProfileDTO().getGroupList().isEmpty()){
                final AtomicLong counter = new AtomicLong();
                Collection<KnCorpGroupInfoPersistDTO> groupList = dto.getCorpGroupList();
                Set<KnCorpGroupListInfoDTO> reqGroupList = dto.getUserProfileDTO().getGroupList();
                knLogger.debug("DB groupList -",groupList);
                knLogger.debug("reqGroupList -",reqGroupList);
                for (KnCorpGroupInfoPersistDTO group : groupList) {
                    for (KnCorpGroupListInfoDTO reqGroup : reqGroupList) {
                        if (reqGroup.getGroupID().intValue()==group.getGroupId()) {
                            counter.incrementAndGet();
                        }
                    }
                }
                if(counter.intValue()<1){
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.GROUP_NOT_EXIST_CORP,
                            "group doesn't exists in corp", getEntityId()
                            , getOperationType(), getRuleId(), String.valueOf(dto.getAutoAssign()), "");
                }
                //modify UPM
            }else if(getOperationType().equals(KnOperationTypes.UPDATE_USER_PROFILE)&& (dto.getCorpGroupList()!=null && !dto.getCorpGroupList().isEmpty())){
                knLogger.debug(methodName,"in modify user profile block",dto.getCorpGroupList().size());
                final AtomicLong groupCounter = new AtomicLong();
                Collection<KnCorpGroupInfoPersistDTO> groupList = dto.getCorpGroupList();
                Set<KnCorpGroupListInfoDTO> addedGroupList = dto.getModifyUserProfileDTO().getAddedGroupList();
                Set<KnCorpGroupListInfoDTO> modifyGroupList = dto.getModifyUserProfileDTO().getModifiedGroupList();
                Set<String> removeGroupIdList = dto.getModifyUserProfileDTO().getRemovedGroupIdsList();
                knLogger.debug("addedGroupList :",addedGroupList);
                knLogger.debug("modifyGroupList :",modifyGroupList);
                knLogger.debug("removeGroupIdList :",removeGroupIdList);
                Set<Integer> reqGroupIdsSet=new HashSet<>();
                if(addedGroupList!=null&&!addedGroupList.isEmpty()){
                    for(KnCorpGroupListInfoDTO groupId:addedGroupList){
                        reqGroupIdsSet.add(groupId.getGroupID());
                    }
                }
                if(modifyGroupList!=null&&!modifyGroupList.isEmpty()){
                    for(KnCorpGroupListInfoDTO groupId:modifyGroupList){
                        reqGroupIdsSet.add(groupId.getGroupID());
                    }
                }
                if(removeGroupIdList!=null&&!removeGroupIdList.isEmpty()){
                    for(String groupId:removeGroupIdList){
                        reqGroupIdsSet.add(Integer.valueOf(groupId));
                    }
                }

                knLogger.debug("DB groupList -",groupList);
                knLogger.debug("reqGroupList -",reqGroupIdsSet);
                if(reqGroupIdsSet!=null&&!reqGroupIdsSet.isEmpty()){
                    for (KnCorpGroupInfoPersistDTO group : groupList) {
                        for (Integer reqGroupIds : reqGroupIdsSet) {
                            if (reqGroupIds.intValue()==group.getGroupId()) {
                                groupCounter.incrementAndGet();
                            }
                        }
                    }
                    if(groupCounter.intValue()<1){
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.GROUP_NOT_EXIST_CORP,
                                "group doesn't exists in corp", getEntityId()
                                , getOperationType(), getRuleId(), String.valueOf(dto.getAutoAssign()), "");
                    }
                }


            }
        } else {
            knLogger.error("validate()", "Unexpected DTO passed - ", persistDTO.getClass(),
                    ", Expected Dto - KnCorpUserProfilePersistDTO ");
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), "DataType", "");
        }
    }
}
