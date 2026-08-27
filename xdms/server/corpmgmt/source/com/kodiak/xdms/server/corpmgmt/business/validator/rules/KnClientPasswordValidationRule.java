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
import com.kodiak.xdms.server.common.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPSubscriberInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnContactDetailsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.Arrays;

/**
 * ************************************************************************
 * <p/>
 * File name: KnClientPasswordValidationRule.java
 * Subsystem: PoC
 * <p/>
 * Name                     Date                    Release
 * -------------------- ------------ -------------------------------------
 * Saurabh Kumar         March 08, 2018            9.0
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

public class KnClientPasswordValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnClientPasswordValidationRule.class);

    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY. Validating userId.");
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug(methodName, "persistDTO obtained from the request is - ", persistDTO);
        if (persistDTO instanceof KnContactDetailsPersistDTO) {
            KnContactDetailsPersistDTO subsDetails = (KnContactDetailsPersistDTO) persistDTO;
            KnIPSubscriberInfoDTO subscriberInfoDTO = (KnIPSubscriberInfoDTO) subsDetails.getInputDTO();
            if (subsDetails.getServiceAuthStatus() == KnConstants.SERVICE_AUTH_STATUS.ACTIVATED.value() && subsDetails.getClientDBPassword() == null) {
                knLogger.error(methodName, "DB Password is null ", subsDetails.getClientDBPassword());
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.DB_PASSWORD_NULL_FOR_ACTIVATED_SUBSCRIBER,
                        "Client Password is null for Activated subscriber", getEntityId(), getOperationType(), getRuleId(),
                        Arrays.asList(subscriberInfoDTO.getMdn()).toString(), "");
            }
        } else {
            knLogger.error("validate", "Unexpected DTO passed - ", persistDTO.getClass(),
                    ", Expected Dto - KnCorpGroupInfoPersistDTO ");
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), "DataType", "");
        }
        knLogger.debug(methodName, "Exit Point: Validation Successfully done for userId existence");
    }
}