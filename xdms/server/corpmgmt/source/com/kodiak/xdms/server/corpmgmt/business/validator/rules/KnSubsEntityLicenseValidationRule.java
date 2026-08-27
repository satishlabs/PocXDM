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
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.Arrays;
import java.util.Collection;

import static com.kodiak.xdms.server.corpmgmt.resources.KnOperationTypes.GENERATE_TEMP_PASSWORD;
import static com.kodiak.xdms.server.corpmgmt.resources.KnOperationTypes.GET_CORP_SUBS_USER_PROFILE;
import static com.kodiak.xdms.server.corpmgmt.resources.KnOperationTypes.UPDATE_CORP_SUBSCRIBER;

/**
 * ************************************************************************
 * <p/>
 * File name: KnSubsEntityLicenseValidationRule.java
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

public class KnSubsEntityLicenseValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnWebDispatcherSystemValidationRule.class);
    private static final String ALLOWED_CLIENT_TYPES = "allowedClientTypes";
    private static final String LICENSE_ENABLED = "licenseEnabled";

    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY. Validating.");
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug(methodName, "persistDTO obtained from the request is - ", persistDTO);
        String allowedClientType = getAttribute(ALLOWED_CLIENT_TYPES);
        String licenseEnabled = getAttribute(LICENSE_ENABLED);
        try {
            if (persistDTO instanceof KnContactDetailsPersistDTO) {
                String[] subTypes = allowedClientType.split(DELIM);
                Collection<String> clientTypeList = Arrays.asList(subTypes);
                KnIPSubscriberInfoDTO subscriberInfoDTO = (KnIPSubscriberInfoDTO) persistDTO.getInputDTO();
                KnContactDetailsPersistDTO subsDetails = (KnContactDetailsPersistDTO) persistDTO;
                int licenseType = subsDetails.getLicenseType();
                if (subscriberInfoDTO.getAliasMdn() != null || subscriberInfoDTO.getOperationType().equals(GENERATE_TEMP_PASSWORD)) {
                    if (!clientTypeList.contains(subsDetails.getAllowedClientTypes())) {
                        if (!licenseEnabled.equals(String.valueOf(licenseType))) {
                            knLogger.debug(methodName, "Subscriber is not user license type");
                            throw new KnCorpBOValidationException(KnErrorCodes.Validator.SUBSCRIBER_IS_NOT_USER_LICENSE_TYPE,
                                    "Subscriber is not user license type", getEntityId(), getOperationType(), getRuleId(),
                                    Arrays.asList(subscriberInfoDTO.getMdn()).toString(), "");

                        }

                    }
                    /*if (!licenseEnabled.equals(String.valueOf(licenseType))) {
                        if (!clientTypeList.contains(subsDetails.getAllowedClientTypes())) {
                            knLogger.debug(methodName, "Invalid Client type");
                            throw new KnCorpBOValidationException(KnErrorCodes.Validator.INVALID_CLIENT_TYPE,
                                    "Invalid Client type", getEntityId(), getOperationType(),
                                    getRuleId(), Arrays.asList(subscriberInfoDTO.getMdn()).toString(), "");
                        }
                    }*/
                }
            } else {
                knLogger.error("validate", "Unexpected DTO passed - ", persistDTO.getClass(),
                        ", Expected Dto - KnContactDetailsPersistDTO ");
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                        "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                        getEntityId(), getOperationType(), getRuleId(), "DataType", "");
            }
        } finally {
            knLogger.debug(methodName, "Exit Point: Validation Successfully done for the LicenseType and ClientType- ");
        }
    }
}