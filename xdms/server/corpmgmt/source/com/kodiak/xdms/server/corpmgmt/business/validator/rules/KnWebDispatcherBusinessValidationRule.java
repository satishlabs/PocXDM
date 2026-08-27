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

import static com.kodiak.xdms.server.corpmgmt.resources.KnOperationTypes.UPDATE_CORP_SUBSCRIBER;

/**
 * ************************************************************************
 * <p/>
 * File name: KnWebDispatcherBusinessValidationRule.java
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

public class KnWebDispatcherBusinessValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnWebDispatcherSystemValidationRule.class);
    private static final String ALLOWED_CLIENT_TYPES = "allowedClientTypes";
    private static final String ALLOWED_CLIENT_TYPES_FOR_LICENSE = "allowedClientTypesForLicense";
    private static final String LICENSE_ENABLED = "licenseEnabled";

    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY. Validating webDispatcher.");
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug(methodName, "persistDTO obtained from the request is - ", persistDTO);
        String allowedClientType = getAttribute(ALLOWED_CLIENT_TYPES);
        String allowedClientTypeForLicense = getAttribute(ALLOWED_CLIENT_TYPES_FOR_LICENSE);
        String licenseEnabled = getAttribute(LICENSE_ENABLED);
        try {
            if (persistDTO instanceof KnContactDetailsPersistDTO) {
                KnIPSubscriberInfoDTO subscriberInfoDTO = (KnIPSubscriberInfoDTO) persistDTO.getInputDTO();
                KnContactDetailsPersistDTO subsDetails = (KnContactDetailsPersistDTO) persistDTO;
                int licenseType = subsDetails.getLicenseType();
                int inputDispatchType = 0;
                if (subscriberInfoDTO.getDispatchType() != null) {
                    inputDispatchType = Integer.parseInt(subscriberInfoDTO.getDispatchType());
                }
                if (subscriberInfoDTO.getOperationType().equals(UPDATE_CORP_SUBSCRIBER) && subscriberInfoDTO.getUserId() != null) {
                    if (!subsDetails.isUserIdExists()) {
                        if (allowedClientType.equals(subsDetails.getAllowedClientTypes())) {
                            if (subsDetails.getDbDispatchType() != 1 && inputDispatchType != 1) {
                                knLogger.debug(methodName, "Dispatch Type not set for requested MDN");
                                throw new KnCorpBOValidationException(KnErrorCodes.Validator.DISPATCH_TYPE_NOT_SET,
                                        "DispatchType not provided as of request",
                                        getEntityId(), getOperationType(), getRuleId(), Arrays.asList(subscriberInfoDTO.getMdn()).toString(), "");

                            }
                        } else if (!allowedClientType.equals(subsDetails.getAllowedClientTypes())
                                && (subsDetails.getDbDispatchType() != 0 || inputDispatchType != 0)) {
                            knLogger.debug(methodName, "Requested MDN does not belongs to dispatcher client community");
                            throw new KnCorpBOValidationException(KnErrorCodes.Validator.INVALID_CLIENT_TYPE,
                                    "Requested MDN is not a dispatcher client",
                                    getEntityId(), getOperationType(), getRuleId(), Arrays.asList(subscriberInfoDTO.getMdn()).toString(), "");
                        } else {
                            if (subsDetails.getDbDispatchType() != 1 && inputDispatchType != 1 && !licenseEnabled.equals(String.valueOf(licenseType))
                                    && !allowedClientTypeForLicense.contains(subsDetails.getAllowedClientTypes())) {
                                knLogger.debug(methodName, "Subscriber is not user license type");
                                throw new KnCorpBOValidationException(KnErrorCodes.Validator.SUBSCRIBER_IS_NOT_USER_LICENSE_TYPE,
                                        "Subscriber is not user license type", getEntityId(), getOperationType(), getRuleId(),
                                        Arrays.asList(subscriberInfoDTO.getMdn()).toString(), "");
                            }
                        }
                    } else {
                        knLogger.debug(methodName, "Requested User Id belongs to some other MDN");
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.USER_ID_EXISTS,
                                "Requested User Id belongs to some other MDN so provide some other user Id.",
                                getEntityId(), getOperationType(), getRuleId(), Arrays.asList(subscriberInfoDTO.getUserId()).toString(), "");
                    }
                }
            } else {
                knLogger.error("validate", "Unexpected DTO passed - ", persistDTO.getClass(),
                        ", Expected Dto - KnCorpGroupInfoPersistDTO ");
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                        "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                        getEntityId(), getOperationType(), getRuleId(), "DataType", "");
            }
        } finally {
            knLogger.debug(methodName, "Exit Point: Validation Successfully done for the WebDispatcher - System Level");
        }
    }
}