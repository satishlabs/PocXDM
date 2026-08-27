/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnMaxExtContCountValidationRule.java
 * Subsystem:  POC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Namita.P.Nair        17-03-2011      7.0
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
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpContactListDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnContactDetailsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

public class KnMaxExtContCountValidationRule extends KnValidatorRule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnMaxExtContCountValidationRule.class);

    private String CLASS = KnMaxExtContCountValidationRule.class.getName();

    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        try {
            IPersistenceDTO persistDTO = getDTO();
            knLogger.debug( methodName, "ENTRY - Validating the No of external contacts present for the corporation  " ,
                    persistDTO);
            if (persistDTO instanceof KnContactDetailsPersistDTO) {
                KnContactDetailsPersistDTO contactDTO = (KnContactDetailsPersistDTO) persistDTO;
                int allowedContactLimit = contactDTO.getMaxExtCorporateMembers();
                IInputDTO inputDTO = contactDTO.getInputDTO();
                KnIPCorpContactListDTO inputContactDTO = (KnIPCorpContactListDTO) inputDTO;
                int currentRequestCount = inputContactDTO.getContactList().size();
                int currentCount = contactDTO.getCurrentContactCount();
                if (currentRequestCount +currentCount >allowedContactLimit) {
                	knLogger.error("External contact count exceeded the limit..");
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.EXTERNAL_CONTACT_COUNT_EXCEEDED,
                            "External contact count exceeded the limit", getEntityId(), getOperationType(), getRuleId(), Arrays.asList(allowedContactLimit).toString(), null);
                }
            } else {
                knLogger.error( "validate()", "Unexpected DTO passed - " , persistDTO.getClass() ,
                        ", Expected Dto - KnContactDetailsPersistDTO ");
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                        "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                        getEntityId(), getOperationType(), getRuleId(), "DataType", "");
            }
            knLogger.debug( methodName, "Validation Completed Successfully");
        } finally {
            knLogger.debug( methodName, "EXIT");
        }
    }
}
