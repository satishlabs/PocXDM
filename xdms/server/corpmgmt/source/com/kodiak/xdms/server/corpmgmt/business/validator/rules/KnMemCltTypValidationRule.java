/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name: KnMemCltTypValidationRule.java
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpSublistInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnContactDetailsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnSublistDetailsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import static com.kodiak.common.resources.KnConstants.SUBS_CLIENT_TYPE.DISPATCH_CLIENT;
import static com.kodiak.common.resources.KnConstants.SUBS_CLIENT_TYPE.THIRDPARTYDISPATCHERCLIENT;

/**
 * This validation rule is to validate the client type of the added members or contact list in request.
 * Throw "InterOP Subscribers not allowed to add as contacts" error if any of the MDN is of client type
 * not in allowed list.
 * Allowed client type = 0,1,2,3
 */

public class KnMemCltTypValidationRule extends KnValidatorRule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnMemCltTypValidationRule.class);
    private static final String ALLOWED_TYPES = "allowedTypes";

    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY. Validating Subscriber Client Type for added MDN.");
        IPersistenceDTO persistDTO = getDTO();
        int distPolicy = 0;
        knLogger.debug( methodName, "persistDTO obtained from the request is - " , persistDTO);
        String allowedSubscriptionTypes = getAttribute(ALLOWED_TYPES);
        knLogger.debug( methodName, "Allowed Client Types - " , allowedSubscriptionTypes);
        try {
            String[] subTypes = allowedSubscriptionTypes.split(DELIM);
            List<String> interOPMdnList = new ArrayList<String>();
            List<String> dispatchMdnList = new ArrayList<String>();
            Collection<String> clientTypeList = Arrays.asList(subTypes);
            if (persistDTO instanceof KnContactDetailsPersistDTO) {
                KnContactDetailsPersistDTO contactDetailsPersistDTO = (KnContactDetailsPersistDTO) persistDTO;
                knLogger.debug( methodName, "DTO passed in the request is - " , contactDetailsPersistDTO);
                knLogger.debug( methodName, "Validation the clientType of the subscriber.");
                //Allowed DGMDN to be added as private contact.
                List<String> clientTypeListWithDGMDN = new ArrayList<>(clientTypeList);
                clientTypeListWithDGMDN.add("18");
                knLogger.debug( methodName, "clientTypeList - " , clientTypeList);
                knLogger.debug( methodName, "clientTypeListWithDGMDN - " , clientTypeListWithDGMDN);
                Collection<KnCorpSubscriberDTO> mdnDto = contactDetailsPersistDTO.getAddedMdnDTO();
                //DGMDN clienttype=18 should not be allowed part of extcontact,should not be added to allowedTypes
                if (mdnDto != null) {
                    for (KnCorpSubscriberDTO subscriberDTO : mdnDto) {
                        if (!clientTypeListWithDGMDN.contains(String.valueOf(subscriberDTO.getClientType()))) {
                            knLogger.debug( methodName, "Subscribers clientType is - " , subscriberDTO.getClientType());
                            knLogger.debug( methodName, "InterOP Subscribers MDN is - " , subscriberDTO.getMdn());
                            interOPMdnList.add(subscriberDTO.getMdn());
                        }
                    }
                }

            } else if (persistDTO instanceof KnSublistDetailsPersistDTO) {
                KnSublistDetailsPersistDTO sublistDetailsPersistDTO = (KnSublistDetailsPersistDTO) persistDTO;
                KnIPCorpSublistInfoDTO corpSublistInfoDTO = (KnIPCorpSublistInfoDTO) persistDTO.getInputDTO();
                distPolicy = corpSublistInfoDTO.getDistributionPolicy();
                knLogger.debug(methodName, "distPolicy - ", distPolicy);
                knLogger.debug(methodName, "DTO passed in the request is - ", sublistDetailsPersistDTO);
                knLogger.debug(methodName, "clientTypeList - ", clientTypeList);
                Collection<KnCorpSubscriberDTO> mdnDto = sublistDetailsPersistDTO.getAddedMdnDTOList();
                //DGMDN clienttype=18 should not be allowed part of sublist,should not be added to allowedTypes
                if (mdnDto != null) {
                    for (KnCorpSubscriberDTO subscriberDTO : mdnDto) {
                        if (subscriberDTO.getClientType() == DISPATCH_CLIENT.value()
                                || subscriberDTO.getClientType() == THIRDPARTYDISPATCHERCLIENT.value()) {
                        	 dispatchMdnList.add(subscriberDTO.getMdn());
                        } else if (!clientTypeList.contains(String.valueOf(subscriberDTO.getClientType()))) {
                            knLogger.debug(methodName, "Subscribers clientType is - ", subscriberDTO.getClientType());
                            knLogger.debug(methodName, "Subscribers MDN is - ", subscriberDTO.getMdn());
                            interOPMdnList.add(subscriberDTO.getMdn());
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

            if (dispatchMdnList.size() > 0 && distPolicy != KnConstants.DEFAULT_GROUP_DIST_POLICY) {
                knLogger.debug( methodName, "DISPATCHER SUBSCRIBER CANNOT BE PART OF SUBLIST");
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.INVALID_CLIENT_TYPE,
                            "Dispatch/3rd Party Dispatcher subscriber can not be part of Sublist.", getEntityId(), getOperationType(), getRuleId(),
                            dispatchMdnList.toString(), "");
            }else if (interOPMdnList.size() > 0 && distPolicy != KnConstants.DEFAULT_GROUP_DIST_POLICY) {
                knLogger.debug( methodName, "Invalid Client Type Subscriber MDNs");
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.INVALID_CLIENT_TYPE,
                        "Invalid client type  Subscribers not allowed to add as contacts", getEntityId(),
                        getOperationType(), getRuleId(), interOPMdnList.toString(), "");
            }       
            } finally {
            knLogger.debug( methodName, "Exit Point: Validation Completed Successfully for the subscribers client type");
        }
    }
}
