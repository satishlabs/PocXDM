/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnBulkSGMdnCoExistsValidationRule.java
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

public class KnBulkSGMdnCoExistsValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnBulkSGMdnCoExistsValidationRule.class);
    private String CLASS = KnBulkSGMdnCoExistsValidationRule.class.getName();

    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY. Validating SGMdn and SGMdnPatch availability in same request");
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug(methodName, "persistDTO obtained from the request is - ", persistDTO);
        try {
            if (persistDTO instanceof KnCorpBulkGroupInfoPersistDTO) {
                KnCorpBulkGroupInfoPersistDTO bulkGroupPersistDTO = (KnCorpBulkGroupInfoPersistDTO) persistDTO;
                knLogger.debug(methodName, "DTO passed in the request is - ", bulkGroupPersistDTO);
                String clientType = bulkGroupPersistDTO.getAllowedClientTypes();
                if (KnConstants.SUBSCR_CLIENT_TYPE.GROUPMDN.value() == Integer.parseInt(clientType)) {
                    LinkedHashMap<Integer, LinkedList<String>> groupMdnPatchCount = bulkGroupPersistDTO.getGroupMdnPatchListCount();
                    Collection<Integer> grpIds = groupMdnPatchCount.entrySet().stream().filter(sgPatchMap -> sgPatchMap.getValue().size() > 0)
                            .map(Map.Entry::getKey).collect(Collectors.toList());
                    if (!grpIds.isEmpty()) {
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.SG_AND_SG_PATCH_MDN_CANNOT_BE_PART_OF_SAME_GROUP,
                                "SGMdn and SGMdnPatch cannot be part of same group", getEntityId(), getOperationType(), getRuleId(),
                                Arrays.asList(grpIds).toString(), "");
                    }
                } else if (KnConstants.SUBSCR_CLIENT_TYPE.SGMDNPATCH.value() == Integer.parseInt(clientType)) {
                    LinkedHashMap<Integer, LinkedList<String>> groupMdnCount = bulkGroupPersistDTO.getGroupMdnPatchListCount();
                    Collection<Integer> grpIds = groupMdnCount.entrySet().stream().filter(sgMap -> sgMap.getValue().size() > 0)
                            .map(Map.Entry::getKey).collect(Collectors.toList());
                    if (!grpIds.isEmpty()) {
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.SG_AND_SG_PATCH_MDN_CANNOT_BE_PART_OF_SAME_GROUP,
                                "SGMdn and SGMdnPatch cannot be part of same group", getEntityId(), getOperationType(), getRuleId(),
                                Arrays.asList(grpIds).toString(), "");
                    }
                }
            } else if (persistDTO instanceof KnCloningPersistDTO) {
                knLogger.debug("validate", "persistDTO is instanceof KnCorpBulkGroupInfoPersistDTO - ", persistDTO);
                KnCloningPersistDTO cloningPersistDTO = (KnCloningPersistDTO) persistDTO;
                if (cloningPersistDTO.getValidationMap().containsKey(com.kodiak.common.resources.KnConstants.CLONING_VALIDATION_BIT.GROUP.value()) && cloningPersistDTO.getValidationMap().get(com.kodiak.common.resources.KnConstants.CLONING_VALIDATION_BIT.GROUP.value())) {
                    KnCorpBulkGroupInfoPersistDTO bulkGroupPersistDTO = cloningPersistDTO.getGroupInfoPersistDTO();
                    knLogger.debug(methodName, "DTO passed in the request is - ", bulkGroupPersistDTO);
                    String clientType = bulkGroupPersistDTO.getAllowedClientTypes();
                    if (KnConstants.SUBSCR_CLIENT_TYPE.GROUPMDN.value() == Integer.parseInt(clientType)) {
                        LinkedHashMap<Integer, LinkedList<String>> groupMdnPatchCount = bulkGroupPersistDTO.getGroupMdnPatchListCount();
                        Collection<Integer> grpIds = groupMdnPatchCount.entrySet().stream().filter(sgPatchMap -> sgPatchMap.getValue().size() > 0)
                                .map(Map.Entry::getKey).collect(Collectors.toList());
                        if (!grpIds.isEmpty()) {
                            throw new KnCorpBOValidationException(KnErrorCodes.Validator.SG_AND_SG_PATCH_MDN_CANNOT_BE_PART_OF_SAME_GROUP,
                                    "SGMdn and SGMdnPatch cannot be part of same group", getEntityId(), getOperationType(), getRuleId(),
                                    Arrays.asList(grpIds).toString(), "");
                        }
                    } else if (KnConstants.SUBSCR_CLIENT_TYPE.SGMDNPATCH.value() == Integer.parseInt(clientType)) {
                        LinkedHashMap<Integer, LinkedList<String>> groupMdnCount = bulkGroupPersistDTO.getGroupMdnPatchListCount();
                        Collection<Integer> grpIds = groupMdnCount.entrySet().stream().filter(sgMap -> sgMap.getValue().size() > 0)
                                .map(Map.Entry::getKey).collect(Collectors.toList());
                        if (!grpIds.isEmpty()) {
                            throw new KnCorpBOValidationException(KnErrorCodes.Validator.SG_AND_SG_PATCH_MDN_CANNOT_BE_PART_OF_SAME_GROUP,
                                    "SGMdn and SGMdnPatch cannot be part of same group", getEntityId(), getOperationType(), getRuleId(),
                                    Arrays.asList(grpIds).toString(), "");
                        }
                    }
                }
            } else {
                knLogger.error("validate", "Unexpected DTO passed - ", persistDTO.getClass(),
                        ", Expected Dto - KnCorpGroupInfoPersistDTO ");
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                        "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                        getEntityId(), getOperationType(), getRuleId(), "DataType", "");
            }
        } finally {
            knLogger.debug(methodName, "Exit Point: Validation Successfully done for the SGMdn and SGMdnPatch availability");
        }
    }
}