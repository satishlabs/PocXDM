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
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;
import java.util.Arrays;
import com.kodiak.logger.KnLogger;

/**
 * Created by IntelliJ IDEA.
 * User: kodiak
 * Date: 11/6/12
 * Time: 4:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class KnGrpTypeChngValidationRule extends KnValidatorRule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnGrpTypeChngValidationRule.class);
    private String CLASS = KnGrpTypeChngValidationRule.class.getName();

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
                knLogger.debug( methodName, "persistDTO is instanceof KnCorpGroupInfoPersistDTO - ", persistDTO);
                KnCorpGroupInfoPersistDTO corpGroupInfoPersistDTO = (KnCorpGroupInfoPersistDTO) persistDTO;
                KnIPCorpGroupInfoDTO groupInfoDTO = (KnIPCorpGroupInfoDTO) corpGroupInfoPersistDTO.getInputDTO();
                if (groupInfoDTO.isGroupTypeChanged()) {
                    knLogger.debug( methodName, "persistDTO is instanceof KnCorpGroupInfoPersistDTO - ", persistDTO);
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.GROUP_TYPE_CHG_NOT_ALLOWED,
                            "Group Type change is not allowed.", getEntityId(), getOperationType(), getRuleId(),
                             Arrays.asList(groupInfoDTO.getGroupType()).toString(), "");
                }

            }         } finally {
            knLogger.debug( methodName, "Exit Point: Validation Completed Successfully");
        }
    }

}
