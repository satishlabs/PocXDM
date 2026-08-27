/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnSubsCountValidationRule.java
 * Subsystem:  POC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Namita.P.Nair        21-02-2011      7.0
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

import java.util.Arrays;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpActivationDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCloningPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnContactDetailsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpActivationPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

public class KnSubsCountValidationRule extends KnValidatorRule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnSubsCountValidationRule.class);
    private final String CLASS = KnSubsContValidationRule.class.getName();

    public void validate() throws KnValidationException {

        final String methodName = "validate()";
        try {
            IPersistenceDTO persistDTO = getDTO();
            knLogger.debug(methodName, "ENTRY - Validating the subscriber count should be equal to one  ");
            if (persistDTO instanceof KnContactDetailsPersistDTO) {
            	KnContactDetailsPersistDTO contactDTO = (KnContactDetailsPersistDTO) persistDTO;
            	KnCorpSubscriberDTO knCorpSubscriberDTO=contactDTO.getSubscDto();
            	if (!(contactDTO.getSubscriberCount() == 1)) {
            		throw new KnCorpBOValidationException(KnErrorCodes.Validator.SUBSCRIBER_DOES_NOT_BELONG_TO_CORP,
            				"Subscriber is not part of the corporation", getEntityId(), getOperationType(), getRuleId(),Arrays.asList(knCorpSubscriberDTO.getMdn()).toString(), "");
            	}
            } else if (persistDTO instanceof KnCloningPersistDTO) {
				KnCloningPersistDTO cloningPersistDTO = (KnCloningPersistDTO) persistDTO;
				//KnContactDetailsPersistDTO contactDTO = cloningPersistDTO.getContactCloningPersistDTO();
				//KnCorpSubscriberDTO knCorpSubscriberDTO = contactDTO.getSubscDto();
				if (!(cloningPersistDTO.getSubscriberCount() == 1)) {
					throw new KnCorpBOValidationException(KnErrorCodes.Validator.SUBSCRIBER_DOES_NOT_BELONG_TO_CORP,
							"Subscriber is not part of the corporation", getEntityId(), getOperationType(), getRuleId(), Arrays.asList(cloningPersistDTO.getSubscDto().getMdn()).toString(), "");
				}
			} else if (persistDTO instanceof KnCorpActivationPersistDTO) {
            	KnCorpActivationPersistDTO contactDTO = (KnCorpActivationPersistDTO) persistDTO;
            	KnIPCorpActivationDTO inputDto=(KnIPCorpActivationDTO) contactDTO.getInputDTO();
            	if (!(contactDTO.getSubscriberCount() == 1)) {
            		if(inputDto.getMdn() != null){
            			knLogger.error("Subscriber is not part of the corporation ",inputDto.getMdn());
            			throw new KnCorpBOValidationException(KnErrorCodes.Validator.SUBSCRIBER_DOES_NOT_BELONG_TO_CORP,
            					"Subscriber is not part of the corporation",getEntityId(), getOperationType(), getRuleId(),Arrays.asList(inputDto.getMdn()).toString(), "");
            		} else if(inputDto.getMdnList()!= null){
            			knLogger.error("Subscriber is not part of the corporation ",inputDto.getMdnList());
            			throw new KnCorpBOValidationException(KnErrorCodes.Validator.SUBSCRIBER_DOES_NOT_BELONG_TO_CORP,
            					"Subscriber is not part of the corporation",getEntityId(), getOperationType(), getRuleId(),inputDto.getMdnList().toString(), "");
            		}
            	}
            } else {
            	knLogger.error( "validate()", "Unexpected DTO passed - " , persistDTO.getClass() ,
            			", Expected Dto - KnContactDetailsPersistDTO ");
            	throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
            			"Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
            			getEntityId(), getOperationType(), getRuleId(), "DataType", "");
            }        
        } 
        finally {
            knLogger.debug( methodName, "EXIT: Validation Completed Successfully");
        }
    }
}
