/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnSubLstAlrdyPushValidationRule.java
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
 */
package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnContactDetailsPersistDTO;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.isObjectNull;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.Collection;

public class KnSubLstAlrdyPushValidationRule extends KnValidatorRule {
	//private static final KnLogger knLogger = KnLogger.getLogger(KnSubLstAlrdyPushValidationRule.class);
    //final String CLASS = KnSubLstAlrdyPushValidationRule.class.getName();

    public void validate() throws KnValidationException {
        /*final String methodName = "validate()";
        ( methodName, "ENTRY Point");
        IPersistenceDTO persistDTO = getDTO();
        try {
            if (persistDTO instanceof KnContactDetailsPersistDTO) {
                //KnContactDetailsPersistDTO contactPersistDto = (KnContactDetailsPersistDTO) persistDTO;
                //Collection<Integer> sublists = contactPersistDto.getSubscSublistIds();
                *//*if (!isObjectNull(sublists) && !sublists.isEmpty()) {
                    knLogger.debug( methodName, "Some of the sublist passed in the request is already pushed " , sublists);
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.SUBLIST_ALREADY_MAPPED,
                            "Some of the sublist passed in the request is already pushed", getEntityId(),
                            getOperationType(), getRuleId(), "DataType", "");
                }*//*
            }
            ( methodName, "Validation Completed Successfully");

        } finally {
            knLogger.debug( methodName, "EXIT");
        }*/
    }
}
