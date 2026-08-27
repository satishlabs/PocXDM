/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name: KnIntOPClientValidationRule.java
 * Subsystem: PoC
 * <p/>
 * Name Date Release
 * -------------------- ------------ -------------------------------------
 * Sanjiv Kr. Acharyya 2/8/12 7.2
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

import com.kodiak.common.resources.KnConstants;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCloningPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnContactDetailsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.corpmgmt.resources.KnOperationTypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * This validation rule is to validate the client type of the mdn list in request.
 * Throw Invalid Client Type Type error if any of the MDN is of client type not in allowed list.
 * Allowed client type = 0,1,2,3
 */
public class KnIntOPClientValidationRule extends KnValidatorRule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnIntOPClientValidationRule.class);
    private String CLASS = KnIntOPClientValidationRule.class.getName();
    private static final String ALLOWED_TYPES = "allowedTypes";

    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug( methodName, "ENTRY. Validating Subscriber Client Type.");
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug( methodName, "persistDTO obtained from the request is - " , persistDTO);
        String allowedSubscriptionTypes = getAttribute(ALLOWED_TYPES);
        try {
            knLogger.debug( methodName, "getOperationType() " , getOperationType());
            if(getOperationType().equals(KnOperationTypes.GET_SUBS_CONTACT_LIST)){
                //getSubsContactList is allowed for DGMDN.
                allowedSubscriptionTypes=allowedSubscriptionTypes+",18";
            }
            knLogger.debug( methodName, "Allowed Client Types 123 - " , allowedSubscriptionTypes);
            String[] subTypes = allowedSubscriptionTypes.split(DELIM);
            Collection<String> clientTypeList = Arrays.asList(subTypes);
            Collection<String> interOpMdn = new ArrayList<String>();
            if (persistDTO instanceof KnContactDetailsPersistDTO) {
                KnContactDetailsPersistDTO contactDetailsPersistDTO = (KnContactDetailsPersistDTO) persistDTO;
                knLogger.debug( methodName, "DTO passed in the request is - " , contactDetailsPersistDTO);

                /*
                inputDTO =  persistDTO.getInputDTO();
                knLogger.debug( methodName, "InputDTO passed is - " , inputDTO);
                */
                knLogger.debug( methodName, "Validation the clientType of the subscriber.");
                knLogger.debug( methodName, "XX clientTypeList - " , clientTypeList);
                Map<String, Integer> mdnClientTypeMap = contactDetailsPersistDTO.getMdnClientTypeMap();
                if (mdnClientTypeMap != null) {
                    for (Map.Entry<String, Integer> entry : mdnClientTypeMap.entrySet()) {
                        String mdn = entry.getKey();
                        String clientType = String.valueOf(entry.getValue());
                        if (!clientTypeList.contains(clientType)) {
                            //DGMDN(18) should not be allowed to have contacts
                            knLogger.debug( methodName, "InterOP/Alias/Group MDN/DGMDN - " , mdn);
                            interOpMdn.add(mdn);
                        }
                    }
                }
            } else if (persistDTO instanceof KnCloningPersistDTO) {
                KnCloningPersistDTO cloningPersistDTO = (KnCloningPersistDTO) persistDTO;
                knLogger.debug(methodName, "DTO passed in the request is - ", cloningPersistDTO);
                if (cloningPersistDTO.getValidationMap().containsKey(KnConstants.CLONING_VALIDATION_BIT.CONTACT.value()) && cloningPersistDTO.getValidationMap().get(KnConstants.CLONING_VALIDATION_BIT.CONTACT.value())) {
                    knLogger.debug(methodName, "Validation the clientType of the subscriber.");
                    knLogger.debug(methodName, "XX clientTypeList - ", clientTypeList);
                    Map<String, Integer> mdnClientTypeMap = cloningPersistDTO.getContactCloningPersistDTO().getMdnClientTypeMap();
                    if (mdnClientTypeMap != null) {
                        for (Map.Entry<String, Integer> entry : mdnClientTypeMap.entrySet()) {
                            String mdn = entry.getKey();
                            String clientType = String.valueOf(entry.getValue());
                            if (!clientTypeList.contains(clientType)) {
                                //DGMDN(18) should not be allowed to have contacts
                                knLogger.debug(methodName, "InterOP/Alias/Group MDN/DGMDN - ", mdn);
                                interOpMdn.add(mdn);
                            }
                        }
                    }
                }
            } else {
                knLogger.error( "validate", "Unexpected DTO passed - " , persistDTO.getClass() ,
                        ", Expected Dto - KnContactDetailsPersistDTO ");
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                        "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                        getEntityId(), getOperationType(), getRuleId(), "DataType", "");
            }
            if (!interOpMdn.isEmpty()) {
                knLogger.debug( methodName, "InterOp Subscribers :- " , interOpMdn);
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.INVALID_CLIENT_TYPE,
                        "Subscriber Client type is not valid", getEntityId(),
                        getOperationType(), getRuleId(), interOpMdn.toString(), "");
            }
        } finally {
            knLogger.debug( methodName, "Exit Point: Validation Successfully done for the not allowed Client");
        }

    }
}
