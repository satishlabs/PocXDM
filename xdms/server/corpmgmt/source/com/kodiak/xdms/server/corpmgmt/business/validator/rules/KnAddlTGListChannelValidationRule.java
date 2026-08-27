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
import static com.kodiak.common.resources.KnConstants.PROTOCOL_VERSION_16;

/**
 * ************************************************************************
 * <p>
 * File name:  KnAddlTGListChannelValidationRule.java
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

public class KnAddlTGListChannelValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnAddlTGListChannelValidationRule.class);
    private int ONE=1;
    private int ZERO=0;

    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY. Validating Group Check");
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug(methodName, "persistDTO obtained from the request is - ", persistDTO);
        final int CHANNELPER_ZONE_VALIDATION_DISABLED=0;
        if (persistDTO instanceof KnCorpTGSPersistDTO) {
            KnCorpTGSPersistDTO tgsPersistDTO = (KnCorpTGSPersistDTO) persistDTO;
            KnIPTalkGroupDTO talkGroupDTO = (KnIPTalkGroupDTO) tgsPersistDTO.getInputDTO();
            Collection<KnCorpAddlTGInfoDTO> reqAddSubsAddlTGList = talkGroupDTO.getAddedAddlTgList();
            Collection<KnCorpAddlTGInfoDTO> reqModSubsAddlTGList = talkGroupDTO.getModifiedAddlTgList();
            int maxSystemChannelPerZone = tgsPersistDTO.getMaxChannelPerZone();
            int clientMajorVersion = tgsPersistDTO.getClientMajorVersion();
            int allowGroupAcorssZones=tgsPersistDTO.getAllowGroupAcrossZones();
            Integer skipChannelPerZoneValidation = tgsPersistDTO.getSkipChannelPerZoneValidation();
            //1 = skipChannelPerZoneValidation enabled
            //0 = skipChannelPerZoneValidation disabled
            if(skipChannelPerZoneValidation==null){
                //setting default vaue to 1 = skipChannelPerZoneValidation enabled
                skipChannelPerZoneValidation=1;
            }
            knLogger.debug("clientMajorVersion :",clientMajorVersion," allowGroupAcorssZones :",allowGroupAcorssZones);
            knLogger.debug("reqAddSubsAddlTGList :",reqAddSubsAddlTGList," reqModSubsAddlTGList :",reqModSubsAddlTGList);

            Collection<KnCorpAddlTGInfoDTO> reqGroupIdsList = new ArrayList<>();
            if(reqAddSubsAddlTGList != null) reqGroupIdsList.addAll(reqAddSubsAddlTGList);
            if(reqModSubsAddlTGList != null) reqGroupIdsList.addAll(reqModSubsAddlTGList);
            int maxRequestedChannel = reqGroupIdsList.stream().map(KnCorpAddlTGInfoDTO::getChannelId).mapToInt(channel -> channel).max().orElse(0);

            knLogger.debug("reqGroupIdsList :",reqGroupIdsList);
            Map<Integer,Integer> groupIdCountMap=new HashMap<>();
            if(reqGroupIdsList!=null&&reqGroupIdsList.size()>ONE){
                List<Integer> addGroupIdList = reqGroupIdsList.stream().map(req->req.getGroupId()).collect(Collectors.toList());
                for(Integer i:addGroupIdList) {
                    if(Collections.frequency(addGroupIdList, i)>ONE) {
                        groupIdCountMap.put(i,Collections.frequency(addGroupIdList, i));
                    }
                }
            }
            knLogger.debug("groupIdCountMap ",groupIdCountMap);

            knLogger.debug(methodName,"maxRequestedChannel",maxRequestedChannel,"maxSystemChannelPerZone:",maxSystemChannelPerZone);
            knLogger.debug(methodName," skipChannelPerZoneValidation:",skipChannelPerZoneValidation);
            if (clientMajorVersion >= PROTOCOL_VERSION_13 && maxRequestedChannel > maxSystemChannelPerZone
                    &&skipChannelPerZoneValidation!=CHANNELPER_ZONE_VALIDATION_DISABLED) {
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.MAX_CHANNEL_REACHED,
                        "Max System Level Channel Reached", getEntityId(),
                        getOperationType(), getRuleId(), Arrays.asList(maxRequestedChannel).toString(), "");
            }else if(groupIdCountMap!=null&&!groupIdCountMap.isEmpty()
                    &&((clientMajorVersion>ZERO&&clientMajorVersion <PROTOCOL_VERSION_16)||allowGroupAcorssZones==ZERO)){
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.GROUP_ACROSS_ZONES_NOT_ALLOWED,
                        "Group across zone not allowed", getEntityId(),
                        getOperationType(), getRuleId(), groupIdCountMap.toString(), "");
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
