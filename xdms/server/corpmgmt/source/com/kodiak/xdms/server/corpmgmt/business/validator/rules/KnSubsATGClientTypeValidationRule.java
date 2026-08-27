/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

import com.kodiak.common.resources.KnConstants;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCloningPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpTGSPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * ************************************************************************
 * <p>
 * File name:  KnSubsATGClientTypeValidationRule.java
 * Subsystem:  PoC
 * <p>
 * Name                         Date                     Release
 * --------------------     ----------------        ------------------
 * Saurabh Kumar             April 26, 2018                9.0
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

public class KnSubsATGClientTypeValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnSubsATGClientTypeValidationRule.class);
    private static final String ALLOWED_TYPES = "allowedTypes";

    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY. Validating Subscriber ClientType Check");
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug(methodName, "persistDTO obtained from the request is - ", persistDTO);
        String allowedClientTypes = getAttribute(ALLOWED_TYPES);
        knLogger.debug(methodName, "allowedClientTypes - ", allowedClientTypes);
        String[] subTypes = allowedClientTypes.split(DELIM);
        Collection<String> clntTypeList = Arrays.asList(subTypes);
       if (persistDTO instanceof KnCorpTGSPersistDTO) {
           KnCorpTGSPersistDTO tgsPersistDTO = (KnCorpTGSPersistDTO) persistDTO;
            int clientType = tgsPersistDTO.getSubsClientType();
            if (!clntTypeList.contains(String.valueOf(clientType))) {
                knLogger.debug(methodName, "Invalid Subscribers Client type ", clientType);
                knLogger.debug(methodName, "Allowed list is ", clntTypeList);
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.INVALID_CLIENT_TYPE,
                        "Subscriber Client type is not valid", getEntityId(),
                        getOperationType(), getRuleId(),Arrays.asList(clientType).toString(), "");
            }
       } else if (persistDTO instanceof KnCloningPersistDTO cloningPersistDTO) {
           //KnCloningPersistDTO tgsPersistDTO = (KnCloningPersistDTO) persistDTO;
           //int clientType = tgsPersistDTO.getSubsClientType();
           if (cloningPersistDTO.getValidationMap().containsKey(KnConstants.CLONING_VALIDATION_BIT.SCAN_LIST.value()) && cloningPersistDTO.getValidationMap().get(KnConstants.CLONING_VALIDATION_BIT.SCAN_LIST.value())) {
               KnCorpSubscriberDTO knCorpSubscriberDTO = cloningPersistDTO.getSubscDto();
               Map<String, Integer> mdnClientTypeMap = cloningPersistDTO.getMdnClientTypeMap();
               int clientType = mdnClientTypeMap.get(knCorpSubscriberDTO.getMdn());
               if (!clntTypeList.contains(String.valueOf(clientType))) {
                   knLogger.debug(methodName, "Invalid Subscribers Client type ", clientType);
                   knLogger.debug(methodName, "Allowed list is ", clntTypeList);
                   throw new KnCorpBOValidationException(KnErrorCodes.Validator.INVALID_CLIENT_TYPE,
                           "Subscriber Client type is not valid", getEntityId(),
                           getOperationType(), getRuleId(), Arrays.asList(clientType).toString(), "");
               }
           }
       } else {
            knLogger.error("validate", "Unexpected DTO passed - ", persistDTO.getClass(),
                    ", Expected Dto - KnCorpTGSPersistDTO ");
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), "DataType", "");
        }
        knLogger.debug(methodName, "Validation Completed Successfully for the Subscriber ClientType Check");
    }
}
