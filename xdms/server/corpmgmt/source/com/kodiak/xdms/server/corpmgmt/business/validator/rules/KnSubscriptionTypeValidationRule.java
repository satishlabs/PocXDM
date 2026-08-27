/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnSubscriptionTypeValidationRule.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      8/11/11      7.1
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

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
//import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpContactListDTO;
//import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpSubscContactListDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnContactDetailsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;


/**
 * This Validation Rule class validates whether the Contacts (MDNs) are having valid Subscription types
 */
public class KnSubscriptionTypeValidationRule extends KnValidatorRule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnSubscriptionTypeValidationRule.class);
    private final String CLASS = KnSubscriptionTypeValidationRule.class.getName();
    private static final String ALLOWED_TYPES = "allowedTypes";

    public void validate() throws KnValidationException {

        final String methodName = "validate()";
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug(methodName, "ENTRY - Validating Subscription types of the MDNs.");
        String allowedSubscriptionTypes = getAttribute(ALLOWED_TYPES);
        Collection<KnCorpSubscriberDTO> contactList = new ArrayList<KnCorpSubscriberDTO>();

        try {
            if (persistDTO instanceof KnContactDetailsPersistDTO) {
                KnContactDetailsPersistDTO contactPersistDto = (KnContactDetailsPersistDTO) persistDTO;
                /*KnIPCorpContactListDTO inputDto = (KnIPCorpContactListDTO) persistDTO.getInputDTO();
                knLogger.debug( methodName, "InputDTO passed is - " , inputDto);*/
                Map<String, KnCorpSubscriberDTO> contactMap = contactPersistDto.getContactCorpDetails();
                if (contactMap != null) {
                    contactList = contactMap.values();
                }
            } else {
                knLogger.error( methodName, "Unexpected DTO passed - " , persistDTO.getClass() ,
                        ", Expected Dto - KnContactDetailsPersistDTO ");
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                        "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                        getEntityId(), getOperationType(), getRuleId(), "DataType", "");
            }

            knLogger.debug( methodName, "Allowed Subscription Types - " , allowedSubscriptionTypes);
            String[] subTypes = allowedSubscriptionTypes.split(DELIM);
            Collection<String> subscriptionTypeList = Arrays.asList(subTypes);
            List<String> failedMdnList=new ArrayList<String>();
            if (contactList != null) {
                for (KnCorpSubscriberDTO contact : contactList) {
                	String subscriptionType = String.valueOf(contact.getSubscriptionType());
                    if (!subscriptionTypeList.contains(subscriptionType)) {
                    	 failedMdnList.add(contact.getMdn());
                    }
                }
            }
            knLogger.debug( methodName, "Failed MDNs with invalid Subscription types - " , failedMdnList);
            if (failedMdnList.size() > 0) {
                knLogger.error( methodName, "mdns with invalid Subscription  are - " , failedMdnList);
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.INVALID_SUBSCRIPTION_TYPE,
                		 "Contacts with invalid Subscription type", getEntityId(), getOperationType(), getRuleId(), failedMdnList.toString(), "");
            }         
         }finally {
            knLogger.debug( methodName, "EXIT: Validation Completed Successfully for the Subscription types of the MDNs. ");
        }
    }
}
