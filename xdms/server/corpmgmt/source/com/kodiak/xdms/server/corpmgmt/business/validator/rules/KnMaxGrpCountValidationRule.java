/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnMaxGrpCountValidationRule.java
 * Subsystem:  POC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Namita.P.Nair        16-03-2011      7.0
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

import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpGroupInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpBCGrpPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpBulkGroupPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.Arrays;

import com.kodiak.logger.KnLogger;

import static com.kodiak.common.resources.KnConstants.AREA_BASED_DYNAMIC_GROUP;


public class KnMaxGrpCountValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnMaxGrpCountValidationRule.class);
    private String CLASS = KnMaxGrpCountValidationRule.class.getName();

    /**
     * This method is used to validate the datafor unique sublist Name for the corporation
     *
     * @throws com.kodiak.xdms.server.common.framework.validator.KnValidationException
     */
    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY Point");
        IPersistenceDTO persistDTO = getDTO();
        if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
            knLogger.debug(methodName, "persistDTO is instanceof KnCorpGroupInfoPersistDTO - ", persistDTO);
            KnCorpGroupInfoPersistDTO corpGroupInfoPersistDTO = (KnCorpGroupInfoPersistDTO) persistDTO;
            KnIPCorpGroupInfoDTO groupInfoDTO = (KnIPCorpGroupInfoDTO) corpGroupInfoPersistDTO.getInputDTO();
            if (AREA_BASED_DYNAMIC_GROUP != groupInfoDTO.getClientType()) {
                if (groupInfoDTO.getGroupType() == KnConstants.STANDARD_GROUP) {
                    if (corpGroupInfoPersistDTO.getCorpGroupCount() >= corpGroupInfoPersistDTO.getMaxGroups()) {
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.MAX_GROUPS_REACHED,
                                "Groups limit for coporation has exceeded.", getEntityId(), getOperationType(), getRuleId(),
                                Arrays.asList(corpGroupInfoPersistDTO.getMaxGroups()).toString(), "");
                    }
                } else if (groupInfoDTO.getGroupType() == KnConstants.DISPATCH_GROUP) {
                    if (corpGroupInfoPersistDTO.getCorpGroupCount() >= corpGroupInfoPersistDTO.getMaxDispatchGroup()) {
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.MAX_DISPATCH_GROUP_LIMIT_EXCEEDED,
                                "Diapatch Groups limit for coporation has exceeded.", getEntityId(), getOperationType(), getRuleId(),
                                Arrays.asList(corpGroupInfoPersistDTO.getMaxDispatchGroup()).toString(), "");
                    }
                }
            }
        } else if (persistDTO instanceof KnCorpBCGrpPersistDTO) {
            KnCorpBCGrpPersistDTO bcGrpPersistDTO = (KnCorpBCGrpPersistDTO) persistDTO;
            if (bcGrpPersistDTO.getCorpGroupCount() >= bcGrpPersistDTO.getMaxGroups()) {
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.MAX_GROUPS_REACHED,
                        "Groups limit for coporation has exceeded.", getEntityId(), getOperationType(), getRuleId(),
                        Arrays.asList(bcGrpPersistDTO.getMaxGroups()).toString(), "");
            }
        }
        else if (persistDTO instanceof KnCorpBulkGroupPersistDTO) {
            KnCorpBulkGroupPersistDTO corpBulkGrpPersistDTO = (KnCorpBulkGroupPersistDTO) persistDTO;
            if (corpBulkGrpPersistDTO.getCorpGroupCount() >= corpBulkGrpPersistDTO.getMaxGroups()) {
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.MAX_GROUPS_REACHED,
                        "Groups limit for coporation has exceeded.", getEntityId(), getOperationType(), getRuleId(),
                        Arrays.asList(corpBulkGrpPersistDTO.getMaxGroups()).toString(), "");
            }
        }
        knLogger.debug(methodName, "Validation Completed Successfully for the Max group Count");
    }
}
