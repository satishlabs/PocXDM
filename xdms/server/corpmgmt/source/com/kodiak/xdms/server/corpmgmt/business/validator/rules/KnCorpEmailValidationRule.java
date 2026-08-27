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
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpActivationPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import com.kodiak.logger.KnLogger;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 11/29/11
 * Time: 2:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class KnCorpEmailValidationRule extends KnValidatorRule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnCorpEmailValidationRule.class);
    private static final long serialVersionUID = 7526471155622676279L;
    private String CLASS = KnCorpEmailValidationRule.class.getName();

    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY. Validating email Id of the subscriber.");
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug( methodName, "persistDTO obtained from the request is - " , persistDTO);
        try {
            if (persistDTO instanceof KnCorpActivationPersistDTO) {
                KnCorpActivationPersistDTO activationPersistDTO = (KnCorpActivationPersistDTO) persistDTO;
                knLogger.debug( methodName, "DTO passed in the request is - " , activationPersistDTO);
                knLogger.debug( methodName, "To validate if email is empty or null.");
//                IInputDTO inputDTO = activationPersistDTO.getInputDTO();
                String email = activationPersistDTO.getSubscriberEmail();
                knLogger.debug( methodName, "Email passed is - " , email);
                /*if (email == null || email.isEmpty()) {
                    knLogger.debug( methodName, "Email Passed is null or empty");
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.INVALID_SUBSCRIBERS_EMAIL,
                            "Email Passed is null or empty", getEntityId(),
                            getOperationType(), getRuleId(), "DataType", "");
                }*/
            } else {
                knLogger.error( "validate", "Unexpected DTO passed - " , persistDTO.getClass() ,
                        ", Expected Dto - KnCorpActivationPersistDTO ");
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                        "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                        getEntityId(), getOperationType(), getRuleId(), "DataType", "");
            }         } finally {
            knLogger.debug(methodName, "EXIT: Validation Completed Successfully means 'Validating email Id of the subscriber' ");
        }
    }

}

