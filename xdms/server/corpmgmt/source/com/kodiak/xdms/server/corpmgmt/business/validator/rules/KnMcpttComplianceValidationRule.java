/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/*
 *  ***********************************************************************
 *  File name:  KnMcpttComplianceValidationRule.java
 *  Subsystem:  PoCXDM
 *
 *    Name                 	    Date         	                  Release
 *    --------------------   -----------------------  -------------------------
 *    Chandrashekar HS          15/04/20, 2:08 PM                    10.0.1
 *
 *  Copyright (c) 2019 Kodiak, A Motorola Solutions Company
 *  9th floor, MFar, Manayata Tech Park,
 *  Greenheart Phase IV,Nagawara
 *  Bangalore - 560 045
 *  www.motorolasolutions.com
 *  All Rights Reserved.
 *
 * This software is the confidential and proprietary information of KodiakMotorola Solutions, Inc.
 * You shall not disclose such confidential information and shall use it only in accordance with the terms of the license agreement you entered into with Kodiak Motorola Solutions.
 *  ***********************************************************************
 */

package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPSubscriberInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnContactDetailsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpUserProfilePersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

public class KnMcpttComplianceValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnMcpttComplianceValidationRule.class);

    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY. Validating DGMDN.");
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug(methodName, "persistDTO obtained from the request is - ", persistDTO);
        if (persistDTO instanceof KnCorpUserProfilePersistDTO) {
            KnCorpUserProfilePersistDTO userProfilePersistDTO = (KnCorpUserProfilePersistDTO) persistDTO;
            if(!userProfilePersistDTO.isSkipValidation()) {
            boolean ismcPttCompliance = userProfilePersistDTO.isMcpttCompliance();
            if(!ismcPttCompliance && userProfilePersistDTO.getMcxGroupCount() > 0){
                knLogger.error(methodName, "validtion failed - ", "ismcPttCompliance -", ismcPttCompliance, "mcxgroups -", userProfilePersistDTO.getMcxGroupCount());
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.SUBSCRIBER_IS_NOT_MCPTT_COMLIANCE, "subscriber is non mcptt compliance hence subscriber" +
                            " can't be added to MCX group",
                        getEntityId(), getOperationType(), getRuleId(), String.valueOf(ismcPttCompliance), "");
            }
            }else {
                knLogger.debug(methodName, "User Profile is not assigned to any MDN hence skipping validation rule");
            }
        } else if(persistDTO instanceof KnContactDetailsPersistDTO){
            KnContactDetailsPersistDTO contactDetailsPersistDTO = (KnContactDetailsPersistDTO) persistDTO;
            KnIPSubscriberInfoDTO ipSubscriberInfoDTO = (KnIPSubscriberInfoDTO)persistDTO.getInputDTO();
            if(contactDetailsPersistDTO.getMcpttCompliance() != 1){
                knLogger.error(methodName, "validtion failed - ", "MCPttCompliance -", contactDetailsPersistDTO.getMcpttCompliance());
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.SUBSCRIBER_IS_NOT_MCPTT_COMLIANCE, "subscriber is non mcptt compliance hence subscriber" +
                        " can't be added to MCX group",
                        getEntityId(), getOperationType(), getRuleId(), String.valueOf(ipSubscriberInfoDTO.getMdn()), "");
            }

        } else{
            knLogger.error("validate", "Unexpected DTO passed - ", persistDTO.getClass(),
                    ", Expected Dto - KnCorpUserProfilePersistDTO");
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(), getEntityId(),
                    getOperationType(), getRuleId(), "DataType", "");
        }

    }
}
