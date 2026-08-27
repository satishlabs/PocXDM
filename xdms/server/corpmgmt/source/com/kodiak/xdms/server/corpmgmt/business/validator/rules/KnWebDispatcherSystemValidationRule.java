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

import static com.kodiak.xdms.server.corpmgmt.resources.KnOperationTypes.RESEND_CORP_SUBS_VERIFICATION_EMAIL;
import static com.kodiak.xdms.server.corpmgmt.resources.KnOperationTypes.UPDATE_CORP_SUBSCRIBER;

/**
 * ************************************************************************
 * <p/>
 * File name: KnWebDispatcherSystemValidationRule.java
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

public class KnWebDispatcherSystemValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnWebDispatcherSystemValidationRule.class);
    private static final String ALLOWED_CLIENT_TYPES = "allowedClientTypes";
    private static final String WEB_DISPATCHER_ENABLED = "webDispatcherEnabled";
    private static final String ALLOWED_TYPES = "allowedTypes";
    private static final int MCPTTCOMPLIANCEENABLED = 1;

    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY. Validating WebDispatcher.");
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug(methodName, "persistDTO obtained from the request is - ", persistDTO);
        String allowedClientType = getAttribute(ALLOWED_CLIENT_TYPES);
        String webDispatcherEnabled = getAttribute(WEB_DISPATCHER_ENABLED);
        String allowedClientTypeForServiceAuthStatusChange = getAttribute(ALLOWED_TYPES);
        knLogger.debug(methodName, "Allowed Client Types - ", allowedClientType, "webDispatcherEnabled - ", webDispatcherEnabled,
                "allowedClientTypeForServiceAuthStatusChange", allowedClientTypeForServiceAuthStatusChange);
        try {
            if (persistDTO instanceof KnContactDetailsPersistDTO) {
                KnIPSubscriberInfoDTO subscriberInfoDTO = (KnIPSubscriberInfoDTO) persistDTO.getInputDTO();
                KnContactDetailsPersistDTO subsDetails = (KnContactDetailsPersistDTO) persistDTO;

                if((subsDetails.getMcpttCompliance() == MCPTTCOMPLIANCEENABLED) && subscriberInfoDTO.getOperationType().equals(RESEND_CORP_SUBS_VERIFICATION_EMAIL)){
                  /*  if (!allowedClientType.equals(subsDetails.getAllowedClientTypes()) && subscriberInfoDTO.getOperationType().equals(RESEND_CORP_SUBS_VERIFICATION_EMAIL)) {
                        knLogger.debug(methodName, "Requested MDN is not a valid client type for mcptt compliance");
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.INVALID_CLIENT_TYPE,
                                "Requested MDN is not a dispatcher client",
                                getEntityId(), getOperationType(), getRuleId(), Arrays.asList(subscriberInfoDTO.getMdn()).toString(), "");
                    }*/
                  knLogger.debug(methodName,"mcptt compliance flow skipping wds validation conditions no need to client types");
                } else if(subsDetails.isWebDispatcherFlag()) {
                    if (webDispatcherEnabled.equals(String.valueOf(subsDetails.getWebDispatcherEnabled()))) {
                        if (!allowedClientType.equals(subsDetails.getAllowedClientTypes()) && subscriberInfoDTO.getOperationType().equals(UPDATE_CORP_SUBSCRIBER)) {
                            knLogger.debug(methodName, "Requested MDN does not belongs to dispatcher client community");
                            throw new KnCorpBOValidationException(KnErrorCodes.Validator.INVALID_CLIENT_TYPE,
                                    "Requested MDN is not a dispatcher client",
                                    getEntityId(), getOperationType(), getRuleId(), Arrays.asList(subscriberInfoDTO.getMdn()).toString(), "");

                        }
                    } else {
                        knLogger.debug(methodName, "System and Corp Level flag is disabled for WebDispatcher Conversion");
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.WEB_DISPATCHER_DISABLED_SYSTEM_LEVEL,
                                "PC Dispather is not allowed to convert into WEB Dispatcher because System and Corp level flag is disabled",
                                getEntityId(), getOperationType(), getRuleId(), Arrays.asList(subsDetails.getWebDispatcherEnabled()).toString(), "");
                    }
                } else if(!subsDetails.isWebDispatcherFlag() && subscriberInfoDTO.getOperationType().equals(UPDATE_CORP_SUBSCRIBER)){
                    String[] subTypes = allowedClientTypeForServiceAuthStatusChange.split(DELIM);
                    Collection<String> clientTypeList = Arrays.asList(subTypes);
                    if(subsDetails.getServiceAuthStatusAU() != 0 && !clientTypeList.contains(subsDetails.getAllowedClientTypes())){
                        knLogger.debug(methodName, "Invalid client type Subscribers :- ", subscriberInfoDTO.getMdn());
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.MEMBER_MDN_INVALID_CLIENT_TYPE,
                                "Subscriber Client type is not valid", getEntityId(), getOperationType(), getRuleId(),
                                Arrays.asList(subscriberInfoDTO.getMdn()).toString(), "");
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