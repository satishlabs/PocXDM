/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnSublistPresentForSubsc.java
 * Subsystem:  POC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Namita.P.Nair        28-01-2011      7.0
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
 *//*

package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpSublistSubscDistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnContactDetailsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;

import java.util.Map;

public class KnSubsSublistMemshipValidationRule extends KnValidatorRule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnSubsSublistMemshipValidationRule.class);

    private String CLASS = KnSubsSublistMemshipValidationRule.class.getName();

    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        ( methodName, "ENTRY Point");
        IPersistenceDTO persistDTO = getDTO();
        try {
            if (persistDTO instanceof KnContactDetailsPersistDTO) {
                KnContactDetailsPersistDTO contactPersistDto = (KnContactDetailsPersistDTO) persistDTO;
                Map<String, String> subscSublistCnt = contactPersistDto.getSubscSublistCnt();

                IInputDTO inputDTO = contactPersistDto.getInputDTO();
                if (inputDTO instanceof KnIPCorpSublistSubscDistDTO) {
                    KnIPCorpSublistSubscDistDTO sublistSubscDistInfo = (KnIPCorpSublistSubscDistDTO) inputDTO;
                    int sublistSize = sublistSubscDistInfo.getSublistIds().size();
                    knLogger.debug( methodName, "subscSublistCnt - ",subscSublistCnt);
                    for (String mdn : sublistSubscDistInfo.getMdnList()) {
                        if (subscSublistCnt == null || subscSublistCnt.isEmpty() 
                                || (subscSublistCnt.get(mdn) != null &&
                                (Integer.parseInt(subscSublistCnt.get(mdn)) < sublistSize))){
                            throw new KnCorpBOValidationException(KnErrorCodes.Validator.SUBLIST_NOT_MAPPED_TO_SUBSCRIBER,
                                    "Sublist not mapped to subscriber", getEntityId(), getOperationType(), getRuleId(), "DataType", "");
                        }
                    }
                }
            }
            ( methodName, "Validation Completed Successfully");
        } finally{
            ( methodName, "Exit Point");
        }
    }
}
*/
