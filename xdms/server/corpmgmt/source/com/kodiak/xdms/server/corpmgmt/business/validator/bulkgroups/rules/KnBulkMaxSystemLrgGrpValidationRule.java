/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnBulkMaxSystemLrgGrpValidationRule.java
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

import static com.kodiak.common.resources.KnConstants.ENABLED;

public class KnBulkMaxSystemLrgGrpValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnBulkMaxSystemLrgGrpValidationRule.class);
    private final String CLASS = KnBulkMaxSystemLrgGrpValidationRule.class.getName();

    /**
     * @throws KnValidationException
     */
    public void validate() throws KnValidationException {

        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug("validate", "ENTRY : Validating if total large groups exceed max number ");
        KnCorpBulkGroupInfoPersistDTO groupBulkPersistDTO;
        if (persistDTO instanceof KnCorpBulkGroupInfoPersistDTO) {
            groupBulkPersistDTO = (KnCorpBulkGroupInfoPersistDTO) persistDTO;
            if (groupBulkPersistDTO.getLargeGroupSupported() == ENABLED) {
                int lrgBCGroupCount = groupBulkPersistDTO.getActualMemberForLargeGroupBC();
                int lrgGroupCount = groupBulkPersistDTO.getActualMemberForLargeGroup();
                int lrgGroupPerSystem = groupBulkPersistDTO.getMaxLargeGrpSystem();
                if ((lrgBCGroupCount + lrgGroupCount) > lrgGroupPerSystem) {
                    knLogger.error("validate", "Validation rule failed. ", "Large Group count : ", (lrgBCGroupCount + lrgGroupCount),
                            " exceeds Max Large Group ", lrgGroupPerSystem, " limit.");
                    throw new KnCorpBOValidationException(KnErrorCodes.BOEntity.MAX_LARGE_BCGROUP_PER_CORP_EXCEEDED, "Validation Rule Failed. " +
                            "Subscribers Count exceeds : " + lrgGroupPerSystem, getEntityId(), getOperationType(), getRuleId(),
                            Arrays.asList(lrgGroupPerSystem).toString(), "");
                }
            }
        } else if (persistDTO instanceof KnCloningPersistDTO) {
            knLogger.debug("validate", "persistDTO is instanceof KnCorpBulkGroupInfoPersistDTO - ", persistDTO);
            KnCloningPersistDTO cloningPersistDTO = (KnCloningPersistDTO) persistDTO;
            if (cloningPersistDTO.getValidationMap().containsKey(KnConstants.CLONING_VALIDATION_BIT.GROUP.value()) && cloningPersistDTO.getValidationMap().get(KnConstants.CLONING_VALIDATION_BIT.GROUP.value())) {
                groupBulkPersistDTO = cloningPersistDTO.getGroupInfoPersistDTO();
                if (groupBulkPersistDTO.getLargeGroupSupported() == ENABLED) {
                    int lrgBCGroupCount = groupBulkPersistDTO.getActualMemberForLargeGroupBC();
                    int lrgGroupCount = groupBulkPersistDTO.getActualMemberForLargeGroup();
                    int lrgGroupPerSystem = groupBulkPersistDTO.getMaxLargeGrpSystem();
                    if ((lrgBCGroupCount + lrgGroupCount) > lrgGroupPerSystem) {
                        knLogger.error("validate", "Validation rule failed. ", "Large Group count : ", (lrgBCGroupCount + lrgGroupCount),
                                " exceeds Max Large Group ", lrgGroupPerSystem, " limit.");
                        throw new KnCorpBOValidationException(KnErrorCodes.BOEntity.MAX_LARGE_BCGROUP_PER_CORP_EXCEEDED, "Validation Rule Failed. " +
                                "Subscribers Count exceeds : " + lrgGroupPerSystem, getEntityId(), getOperationType(), getRuleId(),
                                Arrays.asList(lrgGroupPerSystem).toString(), "");
                    }
                }
            }
        } else {
            knLogger.error("validate", "Unexpected DTO passed - ", persistDTO.getClass(),
                    ", Expected Dto - KnCorpBulkGroupInfoPersistDTO ");
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), "", "");
        }
        knLogger.debug("validate", "KnBulkMaxSystemLrgGrpValidationRule Validated successfully.");
    }
}

