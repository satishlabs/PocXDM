/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnMaxGrpSizeValidationRule.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Namita P Nair      March 19, 2011      7.0
 * <p/>
 * <p/>
 * #401, 4th Floor, 'Prestige Sigma'
 * No.3, Vittal Mallya Road
 * Bangalore - 560 001
 * www.kodiaknetworks.com
 * All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */
package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.common.KnXDMSServiceConfigDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.KnCorpBOException;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpGroupInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPSubscriberInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGroupMemberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpGroupDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpBCGrpPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpBulkGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpUserProfilePersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.kodiak.common.resources.KnConstants.*;
import static com.kodiak.common.resources.KnConstants.SUBS_CLIENT_TYPE.DISPATCH_CLIENT;
import static com.kodiak.common.resources.KnConstants.SUBS_CLIENT_TYPE.THIRDPARTYDISPATCHERCLIENT;
import static com.kodiak.xdms.server.corpmgmt.resources.KnConstants.LARGE_AGENCY_DISPATCH_FEATURE;

public class KnMaxGrpPerMemCountValidationRule extends KnValidatorRule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnMaxGrpPerMemCountValidationRule.class);
    private final String CLASS = KnMaxGrpPerMemCountValidationRule.class.getName();

    /**
     * This method implements the actual logic for validation
     *
     * @throws KnValidationException
     *
     */
    public void validate() throws KnValidationException {
        IPersistenceDTO persistDTO = getDTO();
        String methodName = "validate()";
        knLogger.debug( methodName, "ENTRY : Validating if total members group count exceeds max groups " ,
                "per member limit.");
        int maxGroupsPerMemberCount = 0;
        int maxGroupsPerCriClient = 0;
        List<String> criClientList = new ArrayList<>();
        int clientIntf = 0;
        boolean maxDispacherAllowedPerMcxGroupReached=false;
        Map<String, Integer> groupMemberMap = null;
        int maxAllowedDispatcherPerMcxGroup=0;
        int groupId =0;
        boolean validated = false;
        int maxCorpGroupPerLargeDispatch=0;
        boolean isSysLargeAgencyDispatchEnabled = false;
        boolean isCorpLargeAgencyDispatchEnabled = false;
        Collection<KnCorpSubscriberDTO> addedMdn = null;
        Map<String, Integer> subsGroupCount = null;
        Map<String, Integer> subscriberBroadcastGroupCount = null;
        int maxSubscriberBroadcastGroupCount = 0;

        if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
            KnCorpGroupInfoPersistDTO groupPersistDTO = (KnCorpGroupInfoPersistDTO) persistDTO;
            KnIPCorpGroupInfoDTO groupIpDTO = (KnIPCorpGroupInfoDTO) groupPersistDTO.getInputDTO();
            clientIntf = groupIpDTO.getClientType();
            maxGroupsPerMemberCount = groupPersistDTO.getMaxGroupsPerMemberCount();
            groupMemberMap = groupPersistDTO.getSubsGroupsCount();
            maxGroupsPerCriClient = groupPersistDTO.getMaxGroupsPerCRIClient();
            criClientList = groupPersistDTO.getCriClientList();
            maxDispacherAllowedPerMcxGroupReached=groupPersistDTO.isReachedMaxDispacherAllowedPerMcxGroup();
            maxAllowedDispatcherPerMcxGroup=groupPersistDTO.getMaxAllowedDispatcherPerMcxGroup();
            groupId = groupPersistDTO.getGroupId();
            addedMdn = groupPersistDTO.getAddedMDNs();
            subsGroupCount = groupPersistDTO.getSubsGroupCount();
            maxCorpGroupPerLargeDispatch = groupPersistDTO.getMaxGroupsPerLargeDispatchMemberCount();
            isSysLargeAgencyDispatchEnabled = groupPersistDTO.getIsSysLargeAgencyDispatchEnabled();
            isCorpLargeAgencyDispatchEnabled = groupPersistDTO.getIsCorpLargeAgencyDispatchEnabled();
            knLogger.debug(" maxDispacherAllowedPerMcxGroupReached :",maxDispacherAllowedPerMcxGroupReached
                    ," maxAllowedDispatcherPerMcxGroup :",maxAllowedDispatcherPerMcxGroup," groupId :",groupId);

        } else if (persistDTO instanceof KnCorpBCGrpPersistDTO) {
            KnCorpBCGrpPersistDTO groupPersistDTO = (KnCorpBCGrpPersistDTO) persistDTO;
            maxGroupsPerMemberCount = groupPersistDTO.getMaxGroupsPerMemberCount();
            groupMemberMap = groupPersistDTO.getSubsGroupCountMap();
            maxGroupsPerCriClient = groupPersistDTO.getMaxGroupsPerCRIClient();
            criClientList = groupPersistDTO.getCriClientList();
            addedMdn = groupPersistDTO.getAddedMDNs();
            subsGroupCount = groupPersistDTO.getSubsGroupCount();
            maxCorpGroupPerLargeDispatch = groupPersistDTO.getMaxGroupsPerLargeDispatchMemberCount();
            isSysLargeAgencyDispatchEnabled = groupPersistDTO.getIsSysLargeAgencyDispatchEnabled();
            isCorpLargeAgencyDispatchEnabled = groupPersistDTO.getIsCorpLargeAgencyDispatchEnabled();
            subscriberBroadcastGroupCount = groupPersistDTO.getSubscriberBroadcastGroupCount();
            maxSubscriberBroadcastGroupCount = groupPersistDTO.getMaxBroadcastGroupCountPerSubscriber();
        } else if (persistDTO instanceof KnCorpBulkGroupInfoPersistDTO) {
            KnCorpBulkGroupInfoPersistDTO groupPersistDTO = (KnCorpBulkGroupInfoPersistDTO) persistDTO;
            KnIPSubscriberInfoDTO subsResquestDTO = (KnIPSubscriberInfoDTO) groupPersistDTO.getInputDTO();
            maxGroupsPerMemberCount = groupPersistDTO.getMaxGroupsPerMemberCount();
            maxCorpGroupPerLargeDispatch = groupPersistDTO.getMaxGroupsPerLargeDispatchMemberCount();
            subsGroupCount = groupPersistDTO.getSubsGroupCount();
            isSysLargeAgencyDispatchEnabled = groupPersistDTO.getIsSysLargeAgencyDispatchEnabled();
            isCorpLargeAgencyDispatchEnabled = groupPersistDTO.getIsCorpLargeAgencyDispatchEnabled();
            if (groupPersistDTO.getToMdnMcpttCompliance() == KnConstants.MCPTT_COMPLIANCE) {
                knLogger.debug(methodName, "To MDN is CRI Client");
                maxGroupsPerMemberCount = groupPersistDTO.getMaxGrpsPerCriClient();
            }
            Map<Integer, KnCorpGroupDTO> grpDetailsMap = groupPersistDTO.getGroupDetailsMap();
            if (grpDetailsMap.size() > maxGroupsPerMemberCount) {
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.MAX_GROUPS_PER_MEM_REACHED, "Validation Rule Failed. " +
                        "Subscribers Count exceeds : " + maxGroupsPerMemberCount, getEntityId(),
                        getOperationType(), getRuleId(), subsResquestDTO.getToMdn(),
                        Integer.toString(maxGroupsPerMemberCount));
            }
        } else if (persistDTO instanceof KnCorpUserProfilePersistDTO) {
            KnCorpUserProfilePersistDTO userProfilePersistDTO = (KnCorpUserProfilePersistDTO) persistDTO;
            maxGroupsPerMemberCount = userProfilePersistDTO.getMaxGroupsPerMemberCount();
            int grpListSize = userProfilePersistDTO.getGroupListSize();
            if (grpListSize > maxGroupsPerMemberCount) {
                knLogger.error(methodName, "Max groups per subcriber limit reached", grpListSize , maxGroupsPerMemberCount);
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.MAX_GROUPS_PER_MEM_REACHED, "Validation Rule Failed. " +
                        "User Profile Group Count exceeds : " + maxGroupsPerMemberCount, getEntityId(),
                        getOperationType(), getRuleId(),"", Integer.toString(maxGroupsPerMemberCount));
            }
        } else {
            knLogger.error(methodName, "Unexpected DTO passed - ", persistDTO.getClass(),
                    ", Expected Dto - KnCorpGroupInfoPersistDTO ");
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), "", "");
        }
        knLogger.debug( methodName, "Max groups per Members allowed - " , maxGroupsPerMemberCount);
        knLogger.debug( methodName, "Max groups per Large Dispatch Members allowed - " , maxCorpGroupPerLargeDispatch);

        if(maxDispacherAllowedPerMcxGroupReached){
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.MAXIMUM_DISPATCHER_EXCEEDED_FOR_MCX_GROUP,
                    "dispacher member Count exceeds in mcx group = " + maxAllowedDispatcherPerMcxGroup, getEntityId(),
                    getOperationType(), getRuleId(), String.valueOf(groupId),
                    Integer.toString(maxAllowedDispatcherPerMcxGroup));
        }
        if (AREA_BASED_DYNAMIC_GROUP != clientIntf && (groupMemberMap != null && !groupMemberMap.isEmpty())) {
            if (((addedMdn != null) && !addedMdn.isEmpty()) && (subsGroupCount != null && !subsGroupCount.isEmpty())) {
                Map<String, Integer> finalSubsGroupCount = subsGroupCount;
                int finalMaxGroupsPerMemberCount = maxGroupsPerMemberCount;
                List<String> limitExceededMdns = addedMdn.stream()
                        .filter(sub -> (!(KnGeneralUtil.getFeatureBitValue(sub.getCorpAdminFS2(), FEATURE_SET.LARGE_AGENCY_DISPATCH.value()))))
                        .filter(sub -> finalSubsGroupCount.containsKey(sub.getMdn()) &&
                                (finalSubsGroupCount.get(sub.getMdn()) >= finalMaxGroupsPerMemberCount))
                        .map(KnCorpSubscriberDTO::getMdn)
                        .collect(Collectors.toList());
                if (!limitExceededMdns.isEmpty()) {
                    knLogger.error( methodName, "Validation rule failed. " , "Members Group Limit exceeded: " , limitExceededMdns
                            , ".Actual allowed limit is - " , maxGroupsPerMemberCount);
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.MAX_GROUPS_PER_MEM_REACHED, "Validation Rule Failed. " +
                            "Max groups per subscriber limit exceeded : " + maxGroupsPerMemberCount, getEntityId(),
                            getOperationType(), getRuleId(), limitExceededMdns.toString(), Integer.toString(maxGroupsPerMemberCount));
                }

                if ((isSysLargeAgencyDispatchEnabled) && (isCorpLargeAgencyDispatchEnabled)) {
                    int finalMaxCorpGroupPerLargeDispatch = maxCorpGroupPerLargeDispatch;
                    List<String> limitExceededLDMdns = addedMdn.stream()
                            .filter(sub -> (sub.getClientType() == DISPATCH_CLIENT.value()) || (sub.getClientType() == THIRDPARTYDISPATCHERCLIENT.value()))
                            .filter(sub -> (KnGeneralUtil.getFeatureBitValue(sub.getCorpAdminFS2(), FEATURE_SET.LARGE_AGENCY_DISPATCH.value())))
                            .filter(sub -> finalSubsGroupCount.containsKey(sub.getMdn()) &&
                                    (finalSubsGroupCount.get(sub.getMdn()) >= finalMaxCorpGroupPerLargeDispatch))
                            .map(KnCorpSubscriberDTO::getMdn)
                            .collect(Collectors.toList());
                    validated = true;
                    if (!limitExceededLDMdns.isEmpty()) {
                        knLogger.error( methodName, "Validation rule failed. " , "Members Group Limit exceeded: " , limitExceededLDMdns
                                , ".Actual allowed limit is - " , maxCorpGroupPerLargeDispatch);
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.MAX_GROUPS_PER_MEM_REACHED, "Validation Rule Failed. " +
                                "Max groups per subscriber limit exceeded : " + maxCorpGroupPerLargeDispatch, getEntityId(),
                                getOperationType(), getRuleId(), limitExceededLDMdns.toString(), Integer.toString(maxCorpGroupPerLargeDispatch));
                    }

                    // Check if the subscriber has exceeded the max broadcast group count
                    if (subscriberBroadcastGroupCount != null && !subscriberBroadcastGroupCount.isEmpty() && (maxSubscriberBroadcastGroupCount > 0)) {
                        int finalMaxSubscriberBroadcastGroupCount = maxSubscriberBroadcastGroupCount;
                        Map<String, Integer> finalSubscriberBroadcastGroupCount = subscriberBroadcastGroupCount;
                        List<String> limitExceededBroadcastMdns = addedMdn.stream()
                                .filter(sub -> (sub.getClientType() == DISPATCH_CLIENT.value()) || (sub.getClientType() == THIRDPARTYDISPATCHERCLIENT.value()))
                                .filter(sub -> (KnGeneralUtil.getFeatureBitValue(sub.getCorpAdminFS2(), FEATURE_SET.LARGE_AGENCY_DISPATCH.value())))
                                .filter(sub -> finalSubscriberBroadcastGroupCount.containsKey(sub.getMdn()) &&
                                        (finalSubscriberBroadcastGroupCount.get(sub.getMdn()) >= finalMaxSubscriberBroadcastGroupCount))
                                .map(KnCorpSubscriberDTO::getMdn)
                                .collect(Collectors.toList());
                        if (!limitExceededBroadcastMdns.isEmpty()) {
                            knLogger.error(methodName, "Validation rule failed. ", "Members Broadcast Group Limit exceeded: ", limitExceededBroadcastMdns
                                    , ".Actual allowed limit is - ", maxSubscriberBroadcastGroupCount);
                            throw new KnCorpBOValidationException(KnErrorCodes.Validator.MAX_BROADCAST_GROUPS_PER_MEM_REACHED, "Validation Rule Failed. " +
                                    "Max broadcast groups per subscriber limit exceeded : " + maxSubscriberBroadcastGroupCount, getEntityId(),
                                    getOperationType(), getRuleId(), limitExceededBroadcastMdns.toString(), Integer.toString(maxSubscriberBroadcastGroupCount));
                        }
                    }
                }
            }
            Collection<String> memberMdnList = groupMemberMap.keySet();
            StringBuffer mdnBuffer = new StringBuffer(50);
            boolean groupLimitExceeded = false;
            if (memberMdnList != null) {
                for (String mdn : memberMdnList) {
                    int mdnGroupLimitCount = groupMemberMap.get(mdn);
                    knLogger.debug( methodName, "mdnGroupLimitCount - " , mdnGroupLimitCount);
                    if(criClientList.contains(mdn)){
                        if(mdnGroupLimitCount >= maxGroupsPerCriClient){
                            groupLimitExceeded = true;
                            mdnBuffer.append(mdn).append(",");
                        }

                    }else if (mdnGroupLimitCount >= maxGroupsPerMemberCount) {
                        groupLimitExceeded = true;
                        mdnBuffer.append(mdn).append(",");
                    }
                }
            }
            String mdnStr = null;
            if (!mdnBuffer.toString().equals("")) {
                mdnStr = mdnBuffer.deleteCharAt(mdnBuffer.lastIndexOf(",")).toString();
            }
            if ((!validated) && (groupLimitExceeded)) {
                knLogger.error( methodName, "Validation rule failed. " , "Members Group Limit exceeded" , mdnStr
                        , ".Actual allowed limit is - " , maxGroupsPerMemberCount , " limit.");
                List<String> failedData = new ArrayList<>();
                if (mdnStr != null) {
                	failedData = Arrays.asList(mdnStr.split(","));
                }
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.MAX_GROUPS_PER_MEM_REACHED, "Validation Rule Failed. " +
                        "Subscribers Count exceeds : " + maxGroupsPerMemberCount, getEntityId(),
                        getOperationType(), getRuleId(), failedData.toString(),
                        Integer.toString(maxGroupsPerMemberCount));

            }
        }
        knLogger.debug( methodName, "KnMaxGroupsPerMemberCountValidationRule Validated successfully.");

    }
}
