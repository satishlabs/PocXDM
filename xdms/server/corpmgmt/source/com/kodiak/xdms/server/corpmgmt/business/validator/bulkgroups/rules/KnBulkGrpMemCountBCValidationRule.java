/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnBulkGrpMemCountBCValidationRule.java
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

import com.kodiak.common.resources.KnConstants;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCloningPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpBulkGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.kodiak.common.resources.KnConstants.ENABLED;

public class KnBulkGrpMemCountBCValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnBulkGrpMemCountBCValidationRule.class);
    private final String CLASS = KnBulkGrpMemCountBCValidationRule.class.getName();

    /**
     *
     * @throws KnValidationException
     */
    public void validate() throws KnValidationException {

        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug("validate", "ENTRY : Validating if total Broadcast group members count exceeds max members ");
        KnCorpBulkGroupInfoPersistDTO groupBulkPersistDTO;
        if (persistDTO instanceof KnCorpBulkGroupInfoPersistDTO) {
            groupBulkPersistDTO = (KnCorpBulkGroupInfoPersistDTO) persistDTO;
            Map<Integer, Integer> broadcastGroupsCount = groupBulkPersistDTO.getBroadcastGroupsCount();
            Map<Integer, Integer> broadcastGroupFailed = new HashMap<>();
            if (groupBulkPersistDTO.getLargeGroupSupported() == ENABLED) {
                broadcastGroupFailed.putAll(broadcastGroupsCount.entrySet().stream().filter(map -> map.getValue() >
                        groupBulkPersistDTO.getMaxMemPerLargeBCGroup()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
            } else {
                broadcastGroupFailed.putAll(broadcastGroupsCount.entrySet().stream().filter(map -> map.getValue() >=
                        groupBulkPersistDTO.getMaxAllowedMemPerBCG()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
            }
            if (!broadcastGroupFailed.isEmpty()) {
                //checking for maximum group size reached after addtion/removal of group members
                knLogger.error("validate", "Validation rule failed. ", "Group members count : ", broadcastGroupFailed.keySet(),
                        " exceeds Max Members Per Group ", broadcastGroupFailed, " limit.");
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.MAX_BROADCAST_GRP_SIZE_REACHED, "Validation Rule Failed. " +
                        "Subscribers Count exceeds : " + broadcastGroupFailed, getEntityId(), getOperationType(), getRuleId(),
                        Arrays.asList(broadcastGroupFailed.keySet()).toString(), "");
            }
        } else if (persistDTO instanceof KnCloningPersistDTO) {
            knLogger.debug("validate", "persistDTO is instanceof KnCorpBulkGroupInfoPersistDTO - ", persistDTO);
            KnCloningPersistDTO cloningPersistDTO = (KnCloningPersistDTO) persistDTO;
            if (cloningPersistDTO.getValidationMap().containsKey(KnConstants.CLONING_VALIDATION_BIT.GROUP.value()) && cloningPersistDTO.getValidationMap().get(KnConstants.CLONING_VALIDATION_BIT.GROUP.value())) {
                groupBulkPersistDTO = cloningPersistDTO.getGroupInfoPersistDTO();
                Map<Integer, Integer> broadcastGroupsCount = groupBulkPersistDTO.getBroadcastGroupsCount();
                Map<Integer, Integer> broadcastGroupFailed = new HashMap<>();
                if (groupBulkPersistDTO.getLargeGroupSupported() == ENABLED) {
                    broadcastGroupFailed.putAll(broadcastGroupsCount.entrySet().stream().filter(map -> map.getValue() >
                            groupBulkPersistDTO.getMaxMemPerLargeBCGroup()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
                } else {
                    broadcastGroupFailed.putAll(broadcastGroupsCount.entrySet().stream().filter(map -> map.getValue() >=
                            groupBulkPersistDTO.getMaxAllowedMemPerBCG()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
                }
                if (!broadcastGroupFailed.isEmpty()) {
                    //checking for maximum group size reached after addtion/removal of group members
                    knLogger.error("validate", "Validation rule failed. ", "Group members count : ", broadcastGroupFailed.keySet(),
                            " exceeds Max Members Per Group ", broadcastGroupFailed, " limit.");
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.MAX_BROADCAST_GRP_SIZE_REACHED, "Validation Rule Failed. " +
                            "Subscribers Count exceeds : " + broadcastGroupFailed, getEntityId(), getOperationType(), getRuleId(),
                            Arrays.asList(broadcastGroupFailed.keySet()).toString(), "");
                }
            }
        } else {
            knLogger.error("validate", "Unexpected DTO passed - ", persistDTO.getClass(),
                    ", Expected Dto - KnCLGroupInfoPersistDTO ");
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), "", "");
        }
        knLogger.debug("validate", "KnBulkGrpMemCountBCValidationRule Validated successfully.");
    }
}

