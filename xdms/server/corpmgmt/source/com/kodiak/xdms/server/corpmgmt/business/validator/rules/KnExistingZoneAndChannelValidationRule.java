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
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpUserProfilePersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class KnExistingZoneAndChannelValidationRule extends KnValidatorRule {

    private static final KnLogger knLogger = KnLogger.getLogger(KnExistingZoneAndChannelValidationRule.class);


    @Override
    public void validate() throws KnValidationException, KnBOException {

        final String methodName = "validate()";
        IPersistenceDTO persistDTO = getDTO();
        if(persistDTO instanceof KnCorpUserProfilePersistDTO){
            KnCorpUserProfilePersistDTO dto=(KnCorpUserProfilePersistDTO)persistDTO;
            Set<KnCorpGroupListInfoDTO> dbUpmGroup = dto.getDbUserProfileDTO().getGroupList();
            Set<KnCorpGroupListInfoDTO> addedUpmGroup = dto.getModifyUserProfileDTO().getAddedGroupList();
            Set<KnCorpGroupListInfoDTO> modifyUpmGroup = dto.getModifyUserProfileDTO().getModifiedGroupList();
            Set<String> removedUpmGroup = dto.getModifyUserProfileDTO().getRemovedGroupIdsList();
            Set<KnCorpGroupListInfoDTO> requestUpmGroup =new HashSet<>();
            knLogger.debug(methodName," dbUpmGroup:",dbUpmGroup);
            knLogger.debug(methodName," addedUpmGroup:",addedUpmGroup );
            knLogger.debug(methodName," modifyUpmGroup:",modifyUpmGroup );
            knLogger.debug(methodName," removedUpmGroup:",removedUpmGroup );
            if((addedUpmGroup!=null&&!addedUpmGroup.isEmpty())||(modifyUpmGroup!=null&&!modifyUpmGroup.isEmpty())){
                if(addedUpmGroup!=null)
                requestUpmGroup.addAll(addedUpmGroup);
                if(modifyUpmGroup!=null){
                    requestUpmGroup.addAll(modifyUpmGroup);
                    Set<KnCorpGroupListInfoDTO> sameGroupWithSameZoneAndChannel=new HashSet<>();
                    //if same group with same zone and channel passed for modify multiple times,we are skipping that group.
                    dbUpmGroup.forEach(p ->
                            modifyUpmGroup.stream().filter(p1 ->(p.getGroupID()!=null&&p.getGroupID().equals(p1.getGroupID()))
                                    && (p.getGroupZone()!=null&&p.getGroupZone().equals(p1.getGroupZone()))
                                    && (p.getGroupChannel()!=null&&p.getGroupChannel().equals(p1.getGroupChannel())))
                                    .forEach(sameGroupWithSameZoneAndChannel::add));
                    knLogger.debug(methodName,"sameGroupWithSameZoneAndChannel :",sameGroupWithSameZoneAndChannel);
                    requestUpmGroup.removeAll(sameGroupWithSameZoneAndChannel);
                }
                knLogger.debug(methodName," requestUpmGroup :",requestUpmGroup);
                Set<KnCorpGroupListInfoDTO> existingZoneAndChannels=new HashSet<>();
                //MINT-19083
                Map<Integer, KnCorpGroupListInfoDTO> modifiedMap = new HashMap<>();
                if (modifyUpmGroup != null && !modifyUpmGroup.isEmpty()) {
                    for(KnCorpGroupListInfoDTO entry : modifyUpmGroup){
                        modifiedMap.put(entry.getGroupID(), entry);
                    }
                }
                knLogger.debug(methodName," modifiedMap :",modifiedMap);
                List<KnCorpGroupListInfoDTO> finalDbList = new ArrayList<>();
                if (dbUpmGroup != null && !dbUpmGroup.isEmpty()) {
                    for (KnCorpGroupListInfoDTO obj : dbUpmGroup) {
                        KnCorpGroupListInfoDTO modified = modifiedMap.get(obj.getGroupID());
                        if (null != removedUpmGroup &&
                                !removedUpmGroup.contains(String.valueOf(obj.getGroupID()))) {
                            finalDbList.add(Objects.requireNonNullElse(modified, obj));
                        }
                    }
                }
                knLogger.debug(methodName," finalDbList :",finalDbList);
                requestUpmGroup.forEach(p ->
                        finalDbList.stream().filter(p1 -> (!p.getGroupID().equals(p1.getGroupID()) &&
                                        p.getGroupZone()!=null&&p.getGroupZone().equals(p1.getGroupZone()))
                                && (p.getGroupChannel()!=null&&p.getGroupChannel().equals(p1.getGroupChannel())))
                                .forEach(existingZoneAndChannels::add));

                if(!existingZoneAndChannels.isEmpty()){
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.ZONE_CHANNLE_ALREADY_ASSIGNED_TO_MDN,
                            "Zone and channel,exists in db.", getEntityId(),
                            getOperationType(), getRuleId(), existingZoneAndChannels.toString(), "");
                }
            }

        }else {
            knLogger.error("validate()", "Unexpected DTO passed - ", persistDTO.getClass(),
                    ", Expected Dto - KnCorpUserProfilePersistDTO ");
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), "DataType", "");
        }
    }
}
