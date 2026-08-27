/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnResourceNotModifiedValidationRule.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Saurabh Kumar      August 13, 2015      8.1
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
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

/**
 * KnAuthorizeCorpRule validate the blocked or unblocked corporate.
 */
public class KnAuthorizeCorpRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnAuthorizeCorpRule.class);

    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY Point");
        IPersistenceDTO persistDTO = getDTO();
        try {
            if (persistDTO instanceof KnCorpInfoPersistDTO) {
                KnCorpInfoPersistDTO corpInfoPersistDTO = (KnCorpInfoPersistDTO) persistDTO;
                for (String id : corpInfoPersistDTO.getExtCorpIdList()) {
                    if (id.equals(corpInfoPersistDTO.getExtCorpId().trim())) {
                        knLogger.error(methodName, "unauthorized Corporate for Authenticate ");
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.UNAUTHORISED_CORPORATE, "CORPORATE_IS_BLOCKED", id);
                    }
                }
            }
            knLogger.debug(methodName, "Validation Completed Successfully");
        } finally {
            knLogger.debug(methodName, "Exit Point");
        }

    }
}

