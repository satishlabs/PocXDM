/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnSubListTypeValidationRule.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Mar 1, 2011      7.0
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
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;


public class KnSubListTypeValidationRule extends KnValidatorRule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnSubListTypeValidationRule.class);

    private static final String CLASS = KnSubListTypeValidationRule.class.getName();

    private static final String ALLOWED_TYPES = "allowedTypes";

    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug( methodName, "ENTRY Validating Sublist type ");
        String allowedListTypes = getAttribute(ALLOWED_TYPES);
        int sublistType = -1;
        int sublistId=-1;
        try {
            if (persistDTO instanceof KnSublistDetailsPersistDTO) {
                KnSublistDetailsPersistDTO corpSublistPersistDTO = (KnSublistDetailsPersistDTO) persistDTO;
                sublistType = corpSublistPersistDTO.getSublistType();
                sublistId=corpSublistPersistDTO.getSublistId();
            }
            int[] subTypes = KnCorpUtil.convertStringToIntArray(allowedListTypes, DELIM);

            boolean sublistTypePresent = false;
            for (int index = 0, len = subTypes.length; index < len; index++) {
                if (sublistType == subTypes[index]) {
                    knLogger.debug( methodName, "Sublist type present in expected list : Sublist type - " , sublistType);
                    sublistTypePresent = true;
                    break;
                }
            }
            if (!sublistTypePresent) {
            	 knLogger.error( methodName, "The Sublist type is not allowed - " , sublistType);
                 throw new KnCorpBOValidationException(KnErrorCodes.Validator.INVALID_SUBLIST_TYPE,
                         "The Sublist type is not allowed",  getEntityId(), getOperationType(), getRuleId(), Arrays.asList(sublistId).toString(), "");
            }
            knLogger.debug( methodName, "Validation Completed Successfully");
        } finally {
            knLogger.debug( methodName, "Exit Point");
        }
    }
}
