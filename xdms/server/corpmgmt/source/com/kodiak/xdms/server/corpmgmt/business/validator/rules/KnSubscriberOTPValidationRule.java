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
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpActivationDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpActivationPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

/**
 * ************************************************************************
 * <p/>
 * File name: KnSubscriberOTPValidationRule.java
 * Subsystem: PoC
 * <p/>
 * Name                     Date                    Release
 * -------------------- ------------ -------------------------------------
 * Saurabh Kumar         June 06, 2016            8.2
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
public class KnSubscriberOTPValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnSubscriberOTPValidationRule.class);


    public KnSubscriberOTPValidationRule() {
    }

    /**
     * Validate the max allowed sublist for the corporate
     *
     * @throws com.kodiak.xdms.server.common.framework.validator.KnValidationException
     */
    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        String requesterMdn = null;
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug(methodName, "ENTRY Point : ");
        if (persistDTO instanceof KnCorpActivationPersistDTO) {
            KnCorpActivationPersistDTO activationPersistDTO = (KnCorpActivationPersistDTO) persistDTO;
            knLogger.debug(methodName, "activation Point : ", activationPersistDTO.getDbOTP());
            String dbOTP = ((KnCorpActivationPersistDTO) persistDTO).getDbOTP();
            IInputDTO inputDTO = activationPersistDTO.getInputDTO();
            if (inputDTO instanceof KnIPCorpActivationDTO) {
                KnIPCorpActivationDTO inputDto = (KnIPCorpActivationDTO) activationPersistDTO.getInputDTO();
                knLogger.debug(methodName, "InputDTO passed is - ", inputDto);
                requesterMdn = inputDto.getMdn();
                knLogger.debug(methodName, "input OTP -  ", inputDto.getActivationCode());
                String otp = ((KnIPCorpActivationDTO) inputDTO).getActivationCode();
                if (dbOTP == null || (otp != null && !dbOTP.equals(otp))) {
                    knLogger.error(methodName, "The OTP does not match");
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.SUBSCRIBER_OTP_DOES_NOT_MATCH,
                            "OTP does not match the OTP present in DB", getRuleId(),
                            KnConstants.KEY_DATATYPE_MDN, null, requesterMdn, null);
                }
            } else {
                knLogger.error("validate()", "Unexpected DTO passed - ", persistDTO.getClass(),
                        ", Expected Dto - KnMdnDetailsPersistDTO ");
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                        "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                        getEntityId(), getOperationType(), getRuleId(), "DataType", "");
            }
            knLogger.debug(methodName, "Validation Completed Successfully");
        }

    }
}
