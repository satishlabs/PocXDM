/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnBulkGrpMaxMemCountForClientValidationRule.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Saurabh Kumar      Jan 23, 2019      9.03
 * <p/>
 * <p/>
 * 9th Floor, MFar Manyata Tech Park
 * Greenheart Phase IV, Nagawara
 * Bangalore - 560 045
 * www.kodiaknetworks.com
 * All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */
package com.kodiak.xdms.server.corpmgmt.business.validator.bulkgroups.rules;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCloningPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpBulkGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.*;
import java.util.stream.Collectors;

public class KnBulkGrpMaxMemCountForClientValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnBulkGrpMaxMemCountForClientValidationRule.class);
    private String CLASS = KnBulkGrpMaxMemCountForClientValidationRule.class.getName();
    private String MULTIPLE_INTEROP_ALLOWED = "multipleInterOPAllowed";

    /**
     *
     * @throws com.kodiak.xdms.server.common.framework.validator.KnValidationException
     */
    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY Point");
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug(methodName, "persistDTO obtained from the request is - ", persistDTO);
        try {
            if (persistDTO instanceof KnCorpBulkGroupInfoPersistDTO) {
                knLogger.debug(methodName, "persistDTO is instanceof KnCorpGroupInfoPersistDTO - ", persistDTO);
                KnCorpBulkGroupInfoPersistDTO corpGroupInfoPersistDTO = (KnCorpBulkGroupInfoPersistDTO) persistDTO;
                String clientType = corpGroupInfoPersistDTO.getAllowedClientTypes();
                if (KnConstants.SUBSCR_CLIENT_TYPE.DISPATCH_CLIENT.value() == Integer.parseInt(clientType)) {
                    LinkedHashMap<Integer, LinkedList<String>> dispCount = corpGroupInfoPersistDTO.getDispatcherListCount();
                    boolean multipleDispatcherAllowed = corpGroupInfoPersistDTO.isMultipleDispatcherAllowed();
                    int maxDispatcherInDispatchGroup = corpGroupInfoPersistDTO.getMaxDispatchMembersPerDispatchGroup();
                    knLogger.debug(methodName, "maxDispatcherInDispatchGroup  ", maxDispatcherInDispatchGroup);
                    Collection<Integer> grpIds = dispCount.entrySet().stream().filter(disMap -> disMap.getValue().size() + 1 >=
                            maxDispatcherInDispatchGroup).map(Map.Entry::getKey).collect(Collectors.toList());
                    if(multipleDispatcherAllowed == Boolean.TRUE && !grpIds.isEmpty()){
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.MAXIMUM_DISPATCHER_EXCEEDED_FOR_DISPATCH_GROUP,
                                "Dispatcher count exceeds.", getEntityId(), getOperationType(), getRuleId(),
                                Arrays.asList(maxDispatcherInDispatchGroup).toString(), "");
                    }
                } else if(KnConstants.SUBSCR_CLIENT_TYPE.POC_DONOR_RADIO.value() == Integer.parseInt(clientType)){
                    LinkedHashMap<Integer, LinkedList<String>> interopCount = corpGroupInfoPersistDTO.getInteropMdnListCount();
                    boolean multipleInterOPAllowed = Boolean.valueOf(getAttribute(MULTIPLE_INTEROP_ALLOWED));
                    Collection<Integer> grpIds = interopCount.entrySet().stream().filter(sgMap -> sgMap.getValue().size() > 0)
                            .map(Map.Entry::getKey).collect(Collectors.toList());
                    if (multipleInterOPAllowed == Boolean.FALSE && !grpIds.isEmpty()) {
                            knLogger.error("Mulitple interOP not allowed for a group....", grpIds);
                            throw new KnCorpBOValidationException(KnErrorCodes.Validator.MULTIPLE_INTEROP_NOT_ALLOWED_FOR_GROUP,
                                    "Mulitple interOP not allowed for a group.", getEntityId(), getOperationType(), getRuleId(),
                                    grpIds.toString(), "");
                    }
                } else if(KnConstants.SUBSCR_CLIENT_TYPE.GROUPMDN.value() == Integer.parseInt(clientType)){
                    LinkedHashMap<Integer, LinkedList<String>> groupMdnCount = corpGroupInfoPersistDTO.getGroupMdnListCount();
                    int maxSGMdnPerGroup = corpGroupInfoPersistDTO.getMaxSGPerGrp();
                    knLogger.debug(methodName, "maxSGMdnPerGroup  ", maxSGMdnPerGroup);
                    Collection<Integer> grpIds = groupMdnCount.entrySet().stream().filter(sgMap -> sgMap.getValue().size() + 1 >=
                            maxSGMdnPerGroup).map(Map.Entry::getKey).collect(Collectors.toList());
                    if(!grpIds.isEmpty()){
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.GROUP_MDN_LIMIT_EXCEEDED,
                                "SG Mdn count exceeds.", getEntityId(), getOperationType(), getRuleId(),
                                Arrays.asList(maxSGMdnPerGroup).toString(), "");
                    }
                } else if(KnConstants.SUBSCR_CLIENT_TYPE.SGMDNPATCH.value() == Integer.parseInt(clientType)){
                    LinkedHashMap<Integer, LinkedList<String>> groupMdnPatchCount = corpGroupInfoPersistDTO.getGroupMdnListCount();
                    int maxSGMdnPatchPerGroup = corpGroupInfoPersistDTO.getMaxSGMdnPatchPerGroup();
                    knLogger.debug(methodName, "maxSGMdnPatchPerGroup  ", maxSGMdnPatchPerGroup);
                    Collection<Integer> grpIds = groupMdnPatchCount.entrySet().stream().filter(sgPatchMap -> sgPatchMap.getValue().size() + 1 >=
                            maxSGMdnPatchPerGroup).map(Map.Entry::getKey).collect(Collectors.toList());
                    if(!grpIds.isEmpty()){
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.MAX_SG_PATCH_MDN_PER_GROUP_LIMIT_REACHED,
                                "SG Mdn Patch count exceeds.", getEntityId(), getOperationType(), getRuleId(),
                                Arrays.asList(maxSGMdnPatchPerGroup).toString(), "");
                    }
                }
            } else if (persistDTO instanceof KnCloningPersistDTO) {
                knLogger.debug("validate", "persistDTO is instanceof KnCorpBulkGroupInfoPersistDTO - ", persistDTO);
                KnCloningPersistDTO cloningPersistDTO = (KnCloningPersistDTO) persistDTO;
                int cloningBitSet = Integer.parseInt(cloningPersistDTO.getCloningBitset());
                String binaryString = Integer.toBinaryString(cloningBitSet);
                if (binaryString.length() > 1 && binaryString.charAt(binaryString.length() - 2) == '1') {
                    KnCorpBulkGroupInfoPersistDTO corpGroupInfoPersistDTO = cloningPersistDTO.getGroupInfoPersistDTO();
                    String clientType = corpGroupInfoPersistDTO.getAllowedClientTypes();
                    if (KnConstants.SUBSCR_CLIENT_TYPE.DISPATCH_CLIENT.value() == Integer.parseInt(clientType)) {
                        LinkedHashMap<Integer, LinkedList<String>> dispCount = corpGroupInfoPersistDTO.getDispatcherListCount();
                        boolean multipleDispatcherAllowed = corpGroupInfoPersistDTO.isMultipleDispatcherAllowed();
                        int maxDispatcherInDispatchGroup = corpGroupInfoPersistDTO.getMaxDispatchMembersPerDispatchGroup();
                        knLogger.debug(methodName, "maxDispatcherInDispatchGroup  ", maxDispatcherInDispatchGroup);
                        Collection<Integer> grpIds = dispCount.entrySet().stream().filter(disMap -> disMap.getValue().size() + 1 >=
                                maxDispatcherInDispatchGroup).map(Map.Entry::getKey).collect(Collectors.toList());
                        if (multipleDispatcherAllowed == Boolean.TRUE && !grpIds.isEmpty()) {
                            throw new KnCorpBOValidationException(KnErrorCodes.Validator.MAXIMUM_DISPATCHER_EXCEEDED_FOR_DISPATCH_GROUP,
                                    "Dispatcher count exceeds.", getEntityId(), getOperationType(), getRuleId(),
                                    Arrays.asList(maxDispatcherInDispatchGroup).toString(), "");
                        }
                    } else if (KnConstants.SUBSCR_CLIENT_TYPE.POC_DONOR_RADIO.value() == Integer.parseInt(clientType)) {
                        LinkedHashMap<Integer, LinkedList<String>> interopCount = corpGroupInfoPersistDTO.getInteropMdnListCount();
                        boolean multipleInterOPAllowed = Boolean.valueOf(getAttribute(MULTIPLE_INTEROP_ALLOWED));
                        Collection<Integer> grpIds = interopCount.entrySet().stream().filter(sgMap -> sgMap.getValue().size() > 0)
                                .map(Map.Entry::getKey).collect(Collectors.toList());
                        if (multipleInterOPAllowed == Boolean.FALSE && !grpIds.isEmpty()) {
                            knLogger.error("Mulitple interOP not allowed for a group....", grpIds);
                            throw new KnCorpBOValidationException(KnErrorCodes.Validator.MULTIPLE_INTEROP_NOT_ALLOWED_FOR_GROUP,
                                    "Mulitple interOP not allowed for a group.", getEntityId(), getOperationType(), getRuleId(),
                                    grpIds.toString(), "");
                        }
                    } else if (KnConstants.SUBSCR_CLIENT_TYPE.GROUPMDN.value() == Integer.parseInt(clientType)) {
                        LinkedHashMap<Integer, LinkedList<String>> groupMdnCount = corpGroupInfoPersistDTO.getGroupMdnListCount();
                        int maxSGMdnPerGroup = corpGroupInfoPersistDTO.getMaxSGPerGrp();
                        knLogger.debug(methodName, "maxSGMdnPerGroup  ", maxSGMdnPerGroup);
                        Collection<Integer> grpIds = groupMdnCount.entrySet().stream().filter(sgMap -> sgMap.getValue().size() + 1 >=
                                maxSGMdnPerGroup).map(Map.Entry::getKey).collect(Collectors.toList());
                        if (!grpIds.isEmpty()) {
                            throw new KnCorpBOValidationException(KnErrorCodes.Validator.GROUP_MDN_LIMIT_EXCEEDED,
                                    "SG Mdn count exceeds.", getEntityId(), getOperationType(), getRuleId(),
                                    Arrays.asList(maxSGMdnPerGroup).toString(), "");
                        }
                    } else if (KnConstants.SUBSCR_CLIENT_TYPE.SGMDNPATCH.value() == Integer.parseInt(clientType)) {
                        LinkedHashMap<Integer, LinkedList<String>> groupMdnPatchCount = corpGroupInfoPersistDTO.getGroupMdnListCount();
                        int maxSGMdnPatchPerGroup = corpGroupInfoPersistDTO.getMaxSGMdnPatchPerGroup();
                        knLogger.debug(methodName, "maxSGMdnPatchPerGroup  ", maxSGMdnPatchPerGroup);
                        Collection<Integer> grpIds = groupMdnPatchCount.entrySet().stream().filter(sgPatchMap -> sgPatchMap.getValue().size() + 1 >=
                                maxSGMdnPatchPerGroup).map(Map.Entry::getKey).collect(Collectors.toList());
                        if (!grpIds.isEmpty()) {
                            throw new KnCorpBOValidationException(KnErrorCodes.Validator.MAX_SG_PATCH_MDN_PER_GROUP_LIMIT_REACHED,
                                    "SG Mdn Patch count exceeds.", getEntityId(), getOperationType(), getRuleId(),
                                    Arrays.asList(maxSGMdnPatchPerGroup).toString(), "");
                        }
                    }
                }
            }
        } finally {
            knLogger.debug(methodName, "Exit Point: Validation Completed Successfully");
        }
    }
}
