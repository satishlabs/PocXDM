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
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPSubscriberInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnContactDetailsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.Arrays;

import static com.kodiak.xdms.server.corpmgmt.resources.KnOperationTypes.UPDATE_CORP_SUBSCRIBER;

/**
 * ************************************************************************
 * <p/>
 * File name: KnOnBoardingMailReqdRuleValidationRule.java
 * Subsystem: PoC
 * <p/>
 * Name                     Date                    Release
 * -------------------- ------------ -------------------------------------
 * Saurabh Kumar         January 08, 2020            10.0
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

public class KnOnBoardingMailReqdRuleValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnOnBoardingMailReqdRuleValidationRule.class);

    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY. Validating.");
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug(methodName, "persistDTO obtained from the request is - ", persistDTO);
        try {
            if (persistDTO instanceof KnContactDetailsPersistDTO) {
                KnIPSubscriberInfoDTO subscriberInfoDTO = (KnIPSubscriberInfoDTO) persistDTO.getInputDTO();
                KnContactDetailsPersistDTO subsDetails = (KnContactDetailsPersistDTO) persistDTO;
                if (subsDetails.getOnBoardingEmailReqd() == KnConstants.ON_BOARDING_MAIL.NOT_REQUIRED.value()) {
                    knLogger.debug(methodName, "On Boarding Mail Not required");
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.ON_BOARDING_MAIL_NOT_REQUIRED,
                            "Can not perform resendCorpSubsVerificationEmail or resetCorpSubsUserPassword because " +
                                    "onBoardingMail is not needed for this subscriber",
                            getEntityId(), getOperationType(), getRuleId(), Arrays.asList(subscriberInfoDTO.getMdn()).toString(), "");
                }
            } else {
                knLogger.error("validate", "Unexpected DTO passed - ", persistDTO.getClass(),
                        ", Expected Dto - KnContactDetailsPersistDTO ");
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                        "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                        getEntityId(), getOperationType(), getRuleId(), "DataType", "");
            }
        } finally {
            knLogger.debug(methodName, "Exit Point: Validation Successfully done for OnBoardingMailReqd ");
        }
    }
}