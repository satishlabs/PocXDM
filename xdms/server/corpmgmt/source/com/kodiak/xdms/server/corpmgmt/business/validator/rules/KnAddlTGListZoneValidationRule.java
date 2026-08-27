/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPTalkGroupDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpAddlTGInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpTGSPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.*;
import java.util.stream.Collectors;

import static com.kodiak.common.resources.KnConstants.PROTOCOL_VERSION_13;

/**
 * ************************************************************************
 * <p>
 * File name:  KnAddlTGListZoneValidationRule.java
 * Subsystem:  PoC
 * <p>
 * Name                         Date                     Release
 * --------------------     ----------------        ------------------
 * Saurabh Kumar             April 26, 2018                9.0
 * <p>
 * <p>
 * KODIAK, 9th Floor, 'MFar Manyata Tech Park'
 * Greenheart Phase IV, Nagawara
 * Bangalore - 560 045
 * www.kodiakptt.com
 * All Rights Reserved.
 * <p>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */

public class KnAddlTGListZoneValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnAddlTGListZoneValidationRule.class);

    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY. Validating Group Check");
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug(methodName, "persistDTO obtained from the request is - ", persistDTO);
        if (persistDTO instanceof KnCorpTGSPersistDTO) {
            KnCorpTGSPersistDTO tgsPersistDTO = (KnCorpTGSPersistDTO) persistDTO;
            KnIPTalkGroupDTO talkGroupDTO = (KnIPTalkGroupDTO) tgsPersistDTO.getInputDTO();
            Collection<KnCorpAddlTGInfoDTO> reqAddSubsAddlTGList = talkGroupDTO.getAddedAddlTgList();
            Collection<KnCorpAddlTGInfoDTO> reqModSubsAddlTGList = talkGroupDTO.getModifiedAddlTgList();
            Collection<KnCorpAddlTGInfoDTO> dbAddlTgList = tgsPersistDTO.getDbAddlTGList();
            int maxSystemZone = tgsPersistDTO.getMaxZone();
            Collection<KnCorpAddlTGInfoDTO> allTGList = new ArrayList<>();
            if(reqAddSubsAddlTGList != null) allTGList.addAll(reqAddSubsAddlTGList);
            if(tgsPersistDTO.getClientMajorVersion() > 0 && tgsPersistDTO.getClientMajorVersion() < PROTOCOL_VERSION_13){
                Collection<Integer> invalidZone = allTGList.stream().filter(ipList -> ipList.getZoneId() > 1)
                        .map(KnCorpAddlTGInfoDTO::getZoneId).collect(Collectors.toList());
                if(!invalidZone.isEmpty()){
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.INVALID_ZONE,
                            "Invalid Zone for PV > 0 and PV < 13", getEntityId(),
                            getOperationType(), getRuleId(), Arrays.asList(invalidZone).toString(), "");
                }
            } else {
                if(reqModSubsAddlTGList != null) allTGList.addAll(reqModSubsAddlTGList);
                int maxRequestedZone = allTGList.stream().map(KnCorpAddlTGInfoDTO::getZoneId).mapToInt(zone -> zone).max().orElse(0);
                if (maxRequestedZone > maxSystemZone) {
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.MAX_ZONE_REACHED,
                            "Max system level Zone reached", getEntityId(),
                            getOperationType(), getRuleId(), Arrays.asList(maxRequestedZone).toString(), "");
                }
            }
            Collection<KnCorpAddlTGInfoDTO> reqRemovedAddlTGList = talkGroupDTO.getRemovedAddlTgList();
            knLogger.debug("dbAddlTgList ::", dbAddlTgList, " reqModSubsAddlTGList ::", reqModSubsAddlTGList,
                    ", reqRemovedAddlTGList - ", reqRemovedAddlTGList);

            //MINT-16182
            Map<Integer, KnCorpAddlTGInfoDTO> removedMap = new HashMap<>();
            Map<Integer, List<KnCorpAddlTGInfoDTO>> modifiedMap = new HashMap<>();
            if (reqRemovedAddlTGList != null && !reqRemovedAddlTGList.isEmpty()) {
                removedMap = reqRemovedAddlTGList.stream().collect(Collectors.toMap
                        (KnCorpAddlTGInfoDTO::getGroupId, obj -> obj));
            }
            if (reqModSubsAddlTGList != null && !reqModSubsAddlTGList.isEmpty()) {
                reqModSubsAddlTGList.forEach(obj -> modifiedMap.computeIfAbsent(obj.getGroupId(), k -> new ArrayList<>()).add(obj));
            }
            Collection<KnCorpAddlTGInfoDTO> finalDbList = new ArrayList<>();
            if (dbAddlTgList != null && !dbAddlTgList.isEmpty()) {
                for (KnCorpAddlTGInfoDTO obj : dbAddlTgList) {
                    List<KnCorpAddlTGInfoDTO> modified = modifiedMap.get(obj.getGroupId());
                    if (!removedMap.containsKey(obj.getGroupId())) {
                        finalDbList.addAll(Optional.ofNullable(modified).orElse(Collections.singletonList(obj)));
                    }
                }
            }
            //addedAddlTGList
            knLogger.debug("reqAddSubsAddlTGList ::",reqAddSubsAddlTGList);
            knLogger.debug("finalDbList ::",finalDbList);
            if(!finalDbList.isEmpty()
                    &&reqAddSubsAddlTGList!=null&&!reqAddSubsAddlTGList.isEmpty()){
                Map<Integer,Integer> zoneChannelMap=new HashMap<>();
                for(KnCorpAddlTGInfoDTO dbRecord:finalDbList) {
                    for(KnCorpAddlTGInfoDTO addReq:reqAddSubsAddlTGList) {
                        if(dbRecord.getGroupId()!=addReq.getGroupId() &&
                                dbRecord.getZoneId()!=null&&dbRecord.getZoneId().equals(addReq.getZoneId())
                                && dbRecord.getChannelId()!=null
                                && dbRecord.getChannelId().equals(addReq.getChannelId())) {
                            zoneChannelMap.put(addReq.getZoneId(), addReq.getChannelId());
                        }
                    }
                }
                knLogger.debug("zoneChannelMap :",zoneChannelMap);
                if(zoneChannelMap!=null&&!zoneChannelMap.isEmpty()){
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.ZONE_CHANNLE_ALREADY_ASSIGNED_TO_MDN,
                            "Invalid Zone and channel,exists in db.", getEntityId(),
                            getOperationType(), getRuleId(), zoneChannelMap.toString(), "");
                }
            }
        } else {
            knLogger.error("validate", "Unexpected DTO passed - ", persistDTO.getClass(),
                    ", Expected Dto - KnCorpTGSPersistDTO ");
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), "DataType", "");
        }
        knLogger.debug(methodName, "Validation Completed Successfully for the GroupId Check");
    }
}
