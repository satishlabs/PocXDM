/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnMaxSubscriberCountValidationRule.java
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

import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpSublistInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnContactDetailsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnSublistDetailsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.Collection;
import java.util.Map;

import com.kodiak.logger.KnLogger;

public class KnSubsMaxContCntWhilePushValidationRule extends KnValidatorRule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnSubsMaxContCntWhilePushValidationRule.class);
    private String CLASS = KnSubsMaxContCntWhilePushValidationRule.class.getName();

    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug( methodName, "ENTRY Point");
        IPersistenceDTO persistDTO = getDTO();
        try {
            if (persistDTO instanceof KnContactDetailsPersistDTO) {
                KnContactDetailsPersistDTO contactPersistDto = (KnContactDetailsPersistDTO) persistDTO;
                Map<String, String> subsContactCount = contactPersistDto.getSubscContactCnt();
                knLogger.debug( methodName, "subsContactCount details - " , subsContactCount);
                Map<String, Integer> subsAdditionalContCount = contactPersistDto.getSubscAdditonalContactCnt();
                knLogger.debug( methodName, "subsAdditionalContCount details - " , subsAdditionalContCount);
                int maxContactCount = contactPersistDto.getMaxSubscribersContactLimit();
                knLogger.debug( methodName, "contactPersistDto.getMaxSubscribersContactLimit() " , maxContactCount);
                for (Map.Entry<String, String> entry : subsContactCount.entrySet()) {
                    String mdn = entry.getKey();
                    String cotactCount = entry.getValue();
                    knLogger.debug( methodName, "cotactCount- " , cotactCount);
                    knLogger.debug( methodName, "subsAdditionalContCount.get(mdn)- " , subsAdditionalContCount.get(mdn));
                    if (cotactCount != null && subsAdditionalContCount.get(mdn) != null) {
                        if (Integer.parseInt(cotactCount) +
                                subsAdditionalContCount.get(mdn) > maxContactCount) {
                            throw new KnCorpBOValidationException(KnErrorCodes.Validator.MAX_SUBSCRIBER_CONTACT_EXCEEDED,
                                    "The contact count for the subscriber has exceeded", getEntityId(),
                                    getOperationType(), getRuleId(), "DataType", "");
                        }
                    }
                }
            } else if (persistDTO instanceof KnSublistDetailsPersistDTO) {
                KnSublistDetailsPersistDTO sublistPersistDTO = (KnSublistDetailsPersistDTO) persistDTO;
                KnIPCorpSublistInfoDTO inputDTO = (KnIPCorpSublistInfoDTO) sublistPersistDTO.getInputDTO();
                if (inputDTO.isDistribution()) {
                    Map<String, String> subsContactCount = sublistPersistDTO.getSubscContactCnt();
                    knLogger.debug( methodName, "subsContactCount details - " , subsContactCount);
                    Map<String, Integer> subsAdditionalContCount = sublistPersistDTO.getSubscAdditonalContactCnt();
                    knLogger.debug( methodName, "subsAdditionalContCount details - " , subsAdditionalContCount);
                    int maxContactCount = sublistPersistDTO.getMaxSubscribersContactLimit();
                    Collection<String> mdnList = subsContactCount.keySet();
                    if (mdnList != null && !mdnList.isEmpty()) {
                        for (String mdn : mdnList) {
                            if (subsContactCount.get(mdn) != null && subsAdditionalContCount.get(mdn) != null) {
                                if (Integer.parseInt(subsContactCount.get(mdn)) +
                                        subsAdditionalContCount.get(mdn) > maxContactCount) {
                                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.MAX_SUBSCRIBER_CONTACT_EXCEEDED,
                                            "The contact count for the subscriber has exceeded", getEntityId(),
                                            getOperationType(), getRuleId(), "DataType", "");
                                }
                            }
                        }
                    }
                }

            }
        } finally {
            knLogger.debug( methodName, "EXIT: Validation Completed Successfully");
        }
    }
}
