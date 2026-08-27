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
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpSublistInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnSublistDetailsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;


/**
 * *****************************************************************************
 * File name:   KnSubsDistributionCheckerValidationRule.java
 * Subsystem:
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * ChandraShekar H S       10/4/14      7.8.1
 * <p/>
 * <p/>
 * <p>
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
 * *******************************************************************************
 */
public class KnSubsDistributionCheckerValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnSubsDistributionCheckerValidationRule.class);
    private String methodName = "validate(IPersistenceDTO persistDTO)";

    @Override
    public void validate() throws KnValidationException {
        try {
            IPersistenceDTO persistDTO = getDTO();

            knLogger.debug(methodName, "ENTRY : Validating SubList Distribution value Validation Rule");

            if (persistDTO instanceof KnSublistDetailsPersistDTO) {
                knLogger.debug(methodName, "ENTRY : KnSublistDetailsPersistDTO");
               // KnSublistDetailsPersistDTO contactDTO = (KnSublistDetailsPersistDTO) persistDTO;
                KnIPCorpSublistInfoDTO corpSublistInfoDTO = (KnIPCorpSublistInfoDTO) persistDTO.getInputDTO();

                if (((KnSublistDetailsPersistDTO) persistDTO).getDistributionPolicy() == KnConstants.DIST_POLICY_USER_PROFILE) {
                    if(corpSublistInfoDTO.isDistribution())
                    {
                        knLogger.error(methodName, "Validation failure", "Distribution ", corpSublistInfoDTO.isDistribution(), "Distribution cannot be enabled for User Profile");
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.DEST_TYPE_NOT_ALLOWED,
                                "Distribution cannot be enabled for User Profile", getEntityId(),
                                getOperationType(), getRuleId(), String.valueOf(KnConstants.DIST_POLICY_USER_PROFILE), "");
                    }
                }

            } else {
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR, "Invalid Persist DTO passed.",
                        getRuleId(), String.valueOf(KnConstants.DIST_POLICY_USER_PROFILE));
            }
        }
        finally {
            knLogger.debug( methodName, "Exit Point: Validating successfull");
        }
    }

}
