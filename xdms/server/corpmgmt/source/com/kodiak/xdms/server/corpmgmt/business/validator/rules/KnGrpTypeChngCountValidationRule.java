/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpGroupInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import com.kodiak.logger.KnLogger;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12/8/11
 * Time: 12:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class KnGrpTypeChngCountValidationRule extends KnValidatorRule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnGrpTypeChngCountValidationRule.class);
    private String CLASS = KnGrpTypeChngCountValidationRule.class.getName();

    /**
     * This method is used to validate the datafor unique sublist Name for the corporation
     *
     * @throws com.kodiak.xdms.server.common.framework.validator.KnValidationException
     *
     */
    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY Point");
        IPersistenceDTO persistDTO = getDTO();
        try {
            if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
                knLogger.debug( methodName, "persistDTO is instanceof KnCorpGroupInfoPersistDTO - " , persistDTO);
                KnCorpGroupInfoPersistDTO corpGroupInfoPersistDTO = (KnCorpGroupInfoPersistDTO) persistDTO;
                KnIPCorpGroupInfoDTO groupInfoDTO = (KnIPCorpGroupInfoDTO) corpGroupInfoPersistDTO.getInputDTO();
                if (groupInfoDTO.isGroupTypeChanged()) {
                    knLogger.debug( methodName, "persistDTO is instanceof KnCorpGroupInfoPersistDTO - " , persistDTO);
                    if (groupInfoDTO.getGroupType() == KnConstants.STANDARD_GROUP) {
                        if ((corpGroupInfoPersistDTO.getCorpGroupCount() + 1) > corpGroupInfoPersistDTO.getMaxGroups()) {
                            throw new KnCorpBOValidationException(KnErrorCodes.Validator.MAX_GROUPS_REACHED,
                                    "Groups limit for coporation has exceeded.", getEntityId(), getOperationType(), getRuleId(),
                                    "DataType", "");
                        }
                    } else if (groupInfoDTO.getGroupType() == KnConstants.DISPATCH_GROUP) {
                        if ((corpGroupInfoPersistDTO.getCorpGroupCount() +1) > corpGroupInfoPersistDTO.getMaxDispatchGroup()) {
                            throw new KnCorpBOValidationException(KnErrorCodes.Validator.MAX_DISPATCH_GROUP_LIMIT_EXCEEDED,
                                    "Diapatch Groups limit for coporation has exceeded.", getEntityId(), getOperationType(), getRuleId(),
                                    "DataType", "");
                        }
                    }
                }

            }         } finally {
            knLogger.debug( methodName, "Exit Point: Validation Completed Successfully");
        }
    }

}
