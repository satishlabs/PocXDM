/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name: KnSubsClntTypeValidationRule.java
 * Subsystem: PoC
 * <p/>
 * Name Date Release
 * -------------------- ------------ -------------------------------------
 * Sanjiv Kr. Acharyya 12/15/11 7.2
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

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpActivationPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpTGSPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.Arrays;
import java.util.Collection;

public class KnSubsClntTypeValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnSubsClntTypeValidationRule.class);
    private static final String ALLOWED_TYPES = "allowedTypes";

    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY. Validating Subscriber auth status.");
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug(methodName, "persistDTO obtained from the request is - ", persistDTO);
        String allowedClientTypes = getAttribute(ALLOWED_TYPES);
        knLogger.debug(methodName, "allowedClientTypes - ", allowedClientTypes);
        String[] subTypes = allowedClientTypes.split(DELIM);
        Collection<String> clntTypeList = Arrays.asList(subTypes);
        if (persistDTO instanceof KnCorpActivationPersistDTO) {
            KnCorpActivationPersistDTO activationPersistDTO = (KnCorpActivationPersistDTO) persistDTO;
            knLogger.debug(methodName, "DTO passed in the request is - ", activationPersistDTO);
            knLogger.debug(methodName, "Validation the clientType of the subscriber.");
//                IInputDTO inputDTO = activationPersistDTO.getInputDTO();
            int clientType = activationPersistDTO.getClientType();
            knLogger.debug(methodName, "Subscribers clientType is - ", clientType);

            if (!clntTypeList.contains(String.valueOf(clientType))) {
                knLogger.debug(methodName, "Subscribers Client type is not valid");
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.INVALID_CLIENT_TYPE,
                        "Subscriber Client type is not valid", getEntityId(),
                        getOperationType(), getRuleId(),Arrays.asList(clientType).toString(), "");
            }
        } else if (persistDTO instanceof KnCorpTGSPersistDTO) {
            KnCorpTGSPersistDTO tgsPersistDTO = (KnCorpTGSPersistDTO) persistDTO;
            int clientType = tgsPersistDTO.getSubsClientType();
            if (!clntTypeList.contains(String.valueOf(clientType))) {
                knLogger.debug(methodName, "Invalid Subscribers Client type ", clientType);
                knLogger.debug(methodName, "Allowed list is ", clntTypeList);
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.INVALID_CLIENT_TYPE,
                        "Subscriber Client type is not valid", getEntityId(),
                        getOperationType(), getRuleId(),Arrays.asList(clientType).toString(), "");
            }

        } else {
            knLogger.error("validate", "Unexpected DTO passed - ", persistDTO.getClass(),
                    ", Expected Dto - KnCorpActivationPersistDTO ");
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), "DataType", "");
        }
        knLogger.debug(methodName, "Validation Completed Successfully for the Subscriber auth status. ");
    }
}
