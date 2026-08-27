/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnSubsContValidationRule.java
 * Subsystem:  POC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Namita.P.Nair        24-01-2011      7.0
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
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpSubscContactListDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnContactDetailsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.isNullOrEmpty;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.isObjectNull;
import static com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.Validator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This validation Rule...
 */
public class KnSubsContValidationRule extends KnValidatorRule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnSubsContValidationRule.class);
    private final String CLASS = KnSubsContValidationRule.class.getName();

    public void validate() throws KnValidationException {

        final String methodName = "validate()";
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug(methodName, "ENTRY - Validating whether removed members are part of Subscriber contact list ");
        Collection<String> contactMdnList;
        Collection<String> removedMemberList;
        try {
        	if (persistDTO instanceof KnContactDetailsPersistDTO) {
        		KnContactDetailsPersistDTO contactPersistDto = (KnContactDetailsPersistDTO) persistDTO;
        		contactMdnList = contactPersistDto.getContactMdnList();
        		KnIPCorpSubscContactListDTO inputDto = (KnIPCorpSubscContactListDTO) persistDTO.getInputDTO();
        		knLogger.debug( methodName, "InputDTO passed is - " , inputDto);
        		removedMemberList = inputDto.getRemovedMdnList();
        	} else {
        		knLogger.error( "validate()", "Unexpected DTO passed - " , persistDTO.getClass() ,
        				", Expected Dto - KnContactDetailsPersistDTO ");
        		throw new KnCorpBOValidationException(Validator.INTERNAL_ERROR,
        				"Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
        				getEntityId(), getOperationType(), getRuleId(), "DataType", "");
        	}


        	List<String> notFoundMdns=new ArrayList<String>();
        	//check if all the removed mdn List sent are found in DB
        	knLogger.debug( methodName, "Check if all the removedMdnList passed in request are present in DB.");
        	if (!isObjectNull(contactMdnList) && !contactMdnList.isEmpty()) {
        		knLogger.debug( methodName, "ContactPersistDTO/ContactMdnList Containing details from db about mdn " ,
        				"is not empty.");
        	} else {
        		contactMdnList = new ArrayList<String>();
        	}

        	if (!isObjectNull(removedMemberList) && !removedMemberList.isEmpty()) {
        		knLogger.debug( methodName, "subscContactlist/RemovedMdnList Containing details " ,
        				"from db about mdn is not empty.");
        		for (String mdn : removedMemberList) {
        			if (!contactMdnList.contains(mdn)) {
        				notFoundMdns.add(mdn);
        			}
        		}
        		knLogger.debug( methodName, "List of Mdn i.e RemovedMdnList not found in DB are - " , notFoundMdns);
        		if (notFoundMdns.size() > 0) {
        			knLogger.error( methodName, "mdns not in DB found for RemovedMdnList  are - " , notFoundMdns);
        			throw new KnCorpBOValidationException(Validator.INVALID_SUBSCRIBER_CONTACTS,
        					"Contacts to be deleted not found in the DB for subscriber", getEntityId(), getOperationType(), getRuleId(), notFoundMdns.toString(), "");
        		}
        	} 
        	} finally {
            knLogger.debug( methodName, "EXIT: Validation Completed Successfully");
        }
    }
}
