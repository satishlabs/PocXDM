/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPEmergencyInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpMcpttFeaturePersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.Arrays;

/**
 * ************************************************************************
 * <p>
 * File name:  KnEmergInitPermValidationRule.java
 * Subsystem:  PoC
 * <p>
 * Name                         Date                     Release
 * --------------------     ----------------        ------------------
 * Saurabh Kumar             Jan 23, 2018                9.0
 * <p>
 * <p>
 * KODIAK, 9th Floor, 'MFar Manyata Tech Park'
 * Greenheart Phase IV, Nagawara
 * Bangalore - 560 045
 * www.kodiakptt.com
 * All Rights Reserved.
 * <p>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */

public class KnEmergInitPermValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnEmergInitPermValidationRule.class);
    private static final int DISPATCHER = 3;

    @Override
    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY Point");
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug(methodName, "persistDTO obtained from the request is - ", persistDTO);
        if (persistDTO instanceof KnCorpMcpttFeaturePersistDTO) {
            IInputDTO inputDTO = persistDTO.getInputDTO();
            if (inputDTO instanceof KnIPEmergencyInfoDTO) {
                KnCorpMcpttFeaturePersistDTO mcpttFeaturePersistDTO = (KnCorpMcpttFeaturePersistDTO) persistDTO;
                KnIPEmergencyInfoDTO ipEmergencyInfoDTO = (KnIPEmergencyInfoDTO) inputDTO;
                knLogger.debug(methodName, "ipEmergencyInfoDTO - ", ipEmergencyInfoDTO);
                KnIPEmergencyInfoDTO emergencyInfoDTO = (KnIPEmergencyInfoDTO) inputDTO;
                int clientType = mcpttFeaturePersistDTO.getSubsClientType();
                if (DISPATCHER == clientType && emergencyInfoDTO.getEmergInitPermission() == 1) {
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.EMERGENCY_INIT_PERMISSION_ENABLED,
                            "Dispatcher Should Not Have Emerg Init Perm Enabled", getEntityId(),
                            getOperationType(), getRuleId(), Arrays.asList(emergencyInfoDTO.getMdn()).toString(), "");
                }

            }
        } else {
            knLogger.error("validate", "Unexpected DTO passed - ", persistDTO.getClass(),
                    ", Expected Dto - KnCorpGroupInfoPersistDTO ");
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR, "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), "DataType", "");
        }
        knLogger.debug(methodName, "EXIT: Validation Completed Successfully for emergency Attributes - " +
                "Dispatcher Should Not Have Emerg Init Perm Enabled");
    }
}
