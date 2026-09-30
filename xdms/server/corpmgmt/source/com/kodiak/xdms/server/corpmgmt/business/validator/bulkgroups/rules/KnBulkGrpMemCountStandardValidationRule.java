/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnBulkGrpMemCountStandardValidationRule.java
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

import java.util.*;
import java.util.stream.Collectors;

import static com.kodiak.common.resources.KnConstants.ENABLED;

public class KnBulkGrpMemCountStandardValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnBulkGrpMemCountStandardValidationRule.class);
    private final String CLASS = KnBulkGrpMemCountStandardValidationRule.class.getName();

    /**
     *
     * @throws KnValidationException
     */
    public void validate() throws KnValidationException {

        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug("validate", "ENTRY : Validating if total Standard group members count exceeds max members ");
        KnCorpBulkGroupInfoPersistDTO groupBulkPersistDTO;
        if (persistDTO instanceof KnCorpBulkGroupInfoPersistDTO) {
            groupBulkPersistDTO = (KnCorpBulkGroupInfoPersistDTO) persistDTO;
            Map<Integer, Integer> standardGroupsCount = groupBulkPersistDTO.getStandardGroupsCount();
            Map<Integer, Integer> standardGroupFailed = new HashMap<>();
            if (groupBulkPersistDTO.getLargeGroupSupported() == ENABLED) {
                standardGroupFailed.putAll(standardGroupsCount.entrySet().stream().filter(map -> map.getValue() >
                        groupBulkPersistDTO.getMaxMemPerLargeGroup()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
            } else {
                standardGroupFailed.putAll(standardGroupsCount.entrySet().stream().filter(map -> map.getValue() >=
                        groupBulkPersistDTO.getMaxNumberOfMembers()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
            }
            if (!standardGroupFailed.isEmpty()) {
                //checking for maximum group size reached after addtion/removal of group members
                knLogger.error("validate", "Validation rule failed. ", "Group members count : ", standardGroupFailed.keySet(),
                        " exceeds Max Members Per Group ", standardGroupFailed, " limit.");
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.MAX_GRP_SIZE_REACHED, "Validation Rule Failed. " +
                        "Subscribers Count exceeds : " + standardGroupFailed, getEntityId(), getOperationType(), getRuleId(),
                        Arrays.asList(standardGroupFailed.keySet()).toString(), "");
            }
        } else if (persistDTO instanceof KnCloningPersistDTO) {
            knLogger.debug("validate", "persistDTO is instanceof KnCorpBulkGroupInfoPersistDTO - ", persistDTO);
            KnCloningPersistDTO cloningPersistDTO = (KnCloningPersistDTO) persistDTO;
            if (cloningPersistDTO.getValidationMap().containsKey(KnConstants.CLONING_VALIDATION_BIT.GROUP.value()) && cloningPersistDTO.getValidationMap().get(KnConstants.CLONING_VALIDATION_BIT.GROUP.value())) {
                groupBulkPersistDTO = cloningPersistDTO.getGroupInfoPersistDTO();
                Map<Integer, Integer> standardGroupsCount = groupBulkPersistDTO.getStandardGroupsCount();
                Map<Integer, Integer> standardGroupFailed = new HashMap<>();
                if (groupBulkPersistDTO.getLargeGroupSupported() == ENABLED) {
                    standardGroupFailed.putAll(standardGroupsCount.entrySet().stream().filter(map -> map.getValue() >
                            groupBulkPersistDTO.getMaxMemPerLargeGroup()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
                } else {
                    standardGroupFailed.putAll(standardGroupsCount.entrySet().stream().filter(map -> map.getValue() >=
                            groupBulkPersistDTO.getMaxNumberOfMembers()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
                }
                if (!standardGroupFailed.isEmpty()) {
                    //checking for maximum group size reached after addtion/removal of group members
                    knLogger.error("validate", "Validation rule failed. ", "Group members count : ", standardGroupFailed.keySet(),
                            " exceeds Max Members Per Group ", standardGroupFailed, " limit.");
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.MAX_GRP_SIZE_REACHED, "Validation Rule Failed. " +
                            "Subscribers Count exceeds : " + standardGroupFailed, getEntityId(), getOperationType(), getRuleId(),
                            Arrays.asList(standardGroupFailed.keySet()).toString(), "");
                }
            }
        } else {
            knLogger.error("validate", "Unexpected DTO passed - ", persistDTO.getClass(),
                    ", Expected Dto - KnCLGroupInfoPersistDTO ");
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), "", "");
        }
        knLogger.debug("validate", "KnBulkGrpMemCountStandardValidationRule Validated successfully.");
    }
}

