/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnCorpSublistValidationRule.java
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

import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnSublistDetailsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpSublistInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;
import com.kodiak.logger.KnLogger;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

public class KnSublistSubscMemshipValidationRule extends KnValidatorRule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnSublistSubscMemshipValidationRule.class);

    private String CLASS = KnSublistSubscMemshipValidationRule.class.getName();

    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY Validating if the removed members exist in sublist.");
        IPersistenceDTO persistDTO = getDTO();
        Collection<String> dbSubscList = null;
        Collection<String> inputSubscrList = null;
        try {
            if (persistDTO instanceof KnSublistDetailsPersistDTO) {
                KnSublistDetailsPersistDTO corpSublistPersistDTO = (KnSublistDetailsPersistDTO) persistDTO;
                dbSubscList = new ArrayList<String>();
                if (corpSublistPersistDTO.getDbSublistMembers() != null &&
                        !corpSublistPersistDTO.getDbSublistMembers().isEmpty()) {
                    for (KnCorpSubscriberDTO subsc : corpSublistPersistDTO.getDbSublistMembers()) {
                        dbSubscList.add(subsc.getMdn());
                    }
                }
                IInputDTO inputDTO = persistDTO.getInputDTO();
                if (inputDTO instanceof KnIPCorpSublistInfoDTO) {
                    KnIPCorpSublistInfoDTO sublistInfo = (KnIPCorpSublistInfoDTO) inputDTO;
                    inputSubscrList = sublistInfo.getRemovedMdnList();
                }
            }
            List<String> notFoundMdnList=new ArrayList<String>();
            if (dbSubscList == null || dbSubscList.isEmpty()) {
                if (inputSubscrList != null && !inputSubscrList.isEmpty()) {
                    for (String mdn : inputSubscrList) {
                    	notFoundMdnList.add(mdn);
                    }
                }
            } else {
                if (inputSubscrList != null && !inputSubscrList.isEmpty()) {
                    for (String mdn : inputSubscrList) {
                        if (!dbSubscList.contains(mdn)) {
                        	notFoundMdnList.add(mdn);
                        }
                    }
                }
            }
            if (notFoundMdnList.size() > 0) {
                knLogger.error( methodName, "The removed sublist members are not found in sublist - " , notFoundMdnList);
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.INVALID_SUBLISTS_SUBSCRIBER_CONTACT,
                        "The removed sublist members are not found in sublist", getEntityId(), getOperationType(),
                        getRuleId(), notFoundMdnList.toString(), "");
            }     
            }finally {
            knLogger.debug( methodName, "Exit Point: Validation Completed Successfully for the removed MDN");
        }
    }
}
