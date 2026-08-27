/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

import com.kodiak.logger.KnLogger;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPEmergencyInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpMcpttFeaturePersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.Arrays;
import java.util.Collection;

/**
 * ************************************************************************
 * <p>
 * File name:  KnEmergDestTypeWithClientTypeValidationRule.java
 * Subsystem:  PoC
 * <p>
 * Name                         Date                     Release
 * --------------------     ----------------        ------------------
 * Saurabh Kumar             May 21, 2018                9.0
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

public class KnEmergDestTypeWithClientTypeValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnEmergDestTypeWithClientTypeValidationRule.class);
    private static final String ALLOWED_TYPES = "allowedTypes";

    @Override
    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY Point");
        IPersistenceDTO persistDTO = getDTO();
        String allowedType = getAttribute(ALLOWED_TYPES);
        knLogger.debug(methodName, "persistDTO obtained from the request is - ", persistDTO);
        if (persistDTO instanceof KnCorpMcpttFeaturePersistDTO) {
            IInputDTO inputDTO = persistDTO.getInputDTO();
            String[] subTypes = allowedType.split(DELIM);
            Collection<String> clientTypeList = Arrays.asList(subTypes);
            if (inputDTO instanceof KnIPEmergencyInfoDTO) {
                KnCorpMcpttFeaturePersistDTO mcpttFeaturePersistDTO = (KnCorpMcpttFeaturePersistDTO) persistDTO;
                KnIPEmergencyInfoDTO ipEmergencyInfoDTO = (KnIPEmergencyInfoDTO) inputDTO;
                knLogger.debug(methodName, "ipEmergencyInfoDTO - ", ipEmergencyInfoDTO);
                if (KnConstants.DESTINATION_TYPE.USER_SELECTED_DESTINATION.value() == ipEmergencyInfoDTO.getEmergDestType()) {
                    KnIPEmergencyInfoDTO emergencyInfoDTO = (KnIPEmergencyInfoDTO) inputDTO;
                    int clientType = mcpttFeaturePersistDTO.getSubsClientType();
                    if (!clientTypeList.contains(String.valueOf(clientType))) {
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.DEST_TYPE_NOT_ALLOWED,
                                "Destination type not allowed for Handset Wifi and CrossCarrier", getEntityId(),
                                getOperationType(), getRuleId(), Arrays.asList(emergencyInfoDTO.getMdn()).toString(), "");
                    }
                }
            }
        } else {
            knLogger.error("validate", "Unexpected DTO passed - ", persistDTO.getClass(),
                    ", Expected Dto - KnCorpGroupInfoPersistDTO ");
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR, "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), "DataType", "");
        }
        knLogger.debug(methodName, "EXIT: Validation Completed Successfully for emergency Attributes - DestTypeWithClientType");
    }
}
