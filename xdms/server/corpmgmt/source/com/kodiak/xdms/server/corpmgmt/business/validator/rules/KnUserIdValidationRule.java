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
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPSubscriberInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnContactDetailsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.Arrays;

/**
 * ************************************************************************
 * <p/>
 * File name: KnUserIdValidationRule.java
 * Subsystem: PoC
 * <p/>
 * Name                     Date                    Release
 * -------------------- ------------ -------------------------------------
 * Saurabh Kumar         Feb 06, 2017            8.3
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

public class KnUserIdValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnUserIdValidationRule.class);

    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY. Validating userId.");
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug(methodName, "persistDTO obtained from the request is - ", persistDTO);
        int subsCorpId = 0;
        int reqCorpId = 0;
        try {
            if (persistDTO instanceof KnContactDetailsPersistDTO) {
                KnIPSubscriberInfoDTO subscriberInfoDTO = new KnIPSubscriberInfoDTO();
                IInputDTO inputDTO = persistDTO.getInputDTO();
                KnContactDetailsPersistDTO subsDetails = (KnContactDetailsPersistDTO) persistDTO;
                if (inputDTO instanceof KnIPSubscriberInfoDTO) {
                    subscriberInfoDTO = (KnIPSubscriberInfoDTO) inputDTO;
                    reqCorpId = Integer.parseInt(subscriberInfoDTO.getCorpId());
                    subsCorpId = subsDetails.getSubsCorpId();
                }
                if (subsCorpId != reqCorpId) {
                    knLogger.debug(methodName, "Request CorpID: ", reqCorpId);
                    knLogger.debug(methodName, "Database CorpID: ", subsCorpId);
                    knLogger.error("UserId not found in DB for the Corporate");
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.USER_ID_NOT_EXISTS,
                            "UserId not found in DB for the Corporate", getEntityId(),
                            getOperationType(), getRuleId(), Arrays.asList(subscriberInfoDTO.getUserId()).toString(), "");
                }
            } else {
                knLogger.error("validate", "Unexpected DTO passed - ", persistDTO.getClass(),
                        ", Expected Dto - KnContactDetailsPersistDTO ");
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                        "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                        getEntityId(), getOperationType(), getRuleId(), "DataType", "");
            }
        } finally {
            knLogger.debug(methodName, "Exit Point: Validation Successfully done for the WebDispatcher - Business Level");
        }
    }
}