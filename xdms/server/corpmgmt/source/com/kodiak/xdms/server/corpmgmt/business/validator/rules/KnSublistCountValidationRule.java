/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnSublistCountValidationRule.java
 * Subsystem:  POC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Namita.P.Nair        21-01-2011      7.0
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
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnSublistDetailsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;


public class KnSublistCountValidationRule extends KnValidatorRule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnSublistCountValidationRule.class);

    private String CLASS = KnSublistCountValidationRule.class.getName();

    /**
     * Validate the max allowed sublist for the corporate
     *
     * @throws KnValidationException
     */
    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY. Validating if the sublist count will exceed the allowed limit.");
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug( methodName, "persistDTO obtained from the request is - " , persistDTO);
        try {
            if (persistDTO instanceof KnSublistDetailsPersistDTO) {
                KnSublistDetailsPersistDTO corpSublistPersistDTO = (KnSublistDetailsPersistDTO) persistDTO;
                knLogger.debug( methodName, "Corporate DTO passed in the request is - " , corpSublistPersistDTO);
                knLogger.debug( methodName, "To validate If the max size of the corplist sublist count has exceeded.");
               // IInputDTO inputDTO = corpSublistPersistDTO.getInputDTO();
                int count = corpSublistPersistDTO.getCorpSublistCount();
                knLogger.debug( methodName, "The sublist count as of now is - " , count);
                if (count >= corpSublistPersistDTO.getMaxCorpList()) {
                    knLogger.debug( methodName, "Corporate allowed sublist count exceeded");
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.SUBLIST_COUNT_EXCEEDED,
                            "Sublist Count exceeded the allowed Limit", getEntityId(),
                            getOperationType(), getRuleId(),  Arrays.asList(corpSublistPersistDTO.getMaxCorpList()).toString(), "");
                }
            } else {
                knLogger.error( "validate", "Unexpected DTO passed - " , persistDTO.getClass() ,
                        ", Expected Dto - KnContactDetailsPersistDTO ");
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                        "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                        getEntityId(), getOperationType(), getRuleId(), "DataType", "");
         }  
         } finally {
            knLogger.debug( methodName, "EXIT: Validation Completed Successfully for the sublist count.");
        }
    }
}
