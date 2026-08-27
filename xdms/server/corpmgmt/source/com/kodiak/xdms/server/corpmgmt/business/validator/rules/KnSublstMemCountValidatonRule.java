/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnSublistSizeValidatonRule.java
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

public class KnSublstMemCountValidatonRule extends KnValidatorRule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnSublstMemCountValidatonRule.class);

    private String CLASS = KnSublstMemCountValidatonRule.class.getName();


    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug( methodName, "ENTRY Validating if the sublist contact count would exceed the allowed limit.");
        IPersistenceDTO persistDTO = getDTO();
        try {
            if (persistDTO instanceof KnSublistDetailsPersistDTO) {
                KnSublistDetailsPersistDTO corpSublistPersistDTO = (KnSublistDetailsPersistDTO) persistDTO;
                knLogger.debug( methodName, "Corporate DTO passed in the request is - " , corpSublistPersistDTO);
                knLogger.debug( methodName, "To validate If the max size of the corplist sublist count has exceeded.");
                int count = corpSublistPersistDTO.getTotalSublistsMembersCount();
                boolean isCommonSublist = corpSublistPersistDTO.isCommonContactList();
                knLogger.debug( methodName, "The sublist contact count as of now is - " , count," isCommonSublist:",isCommonSublist);
                if (!isCommonSublist&&(count > corpSublistPersistDTO.getMaxCorpListMemberCount())) {
                    knLogger.debug( methodName, "Sublist allowed sublist count exceeded");
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.SUBLIST_CONTACT_LIMIT_EXCEEDED,
                            "Sublist contact Limit exceeded", getEntityId(), getOperationType(), getRuleId(),  Arrays.asList(corpSublistPersistDTO.getMaxCorpListMemberCount()).toString(), "");
                }
            }
            knLogger.debug( methodName, "Validation Completed Successfully");
        } finally {
            knLogger.debug( methodName, "Exit Point");
        }
    }
}
