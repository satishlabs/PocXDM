/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnUniqueSublistNameValidationrule.java
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

public class KnUnqSublistNameValidationRule extends KnValidatorRule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnUnqSublistNameValidationRule.class);

    /**
     * This method is used to validate the datafor unique sublist Name for the corporation
     *
     * @throws KnValidationException
     */
    public void validate() throws KnValidationException {

        final String methodName = "validate()";
        knLogger.debug( methodName, "ENTRY Point");
        IPersistenceDTO persistDTO = getDTO();
        try {
            if (persistDTO instanceof KnSublistDetailsPersistDTO) {
                KnSublistDetailsPersistDTO sublistPersistDto = (KnSublistDetailsPersistDTO) persistDTO;
                if (sublistPersistDto.getSublistCountByName() > 0) {
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.SUBLIST_ALREADY_EXISTS,
                            "Sublist Name already exists cannot modify or create sublist", getEntityId(), getOperationType(), getRuleId(), Arrays.asList(sublistPersistDto.getSublistName()).toString(), "");
                }

            }
            knLogger.debug( methodName, "Validation Completed Successfully");
        } finally {
            knLogger.debug ( methodName, "Exit Point");
        }
    }
}
