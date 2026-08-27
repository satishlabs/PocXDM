/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *****************************************************************************
 * File name:   Kn.java
 * Subsystem:
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ravi Shanker .P       1/5/11       7.0
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
 * *******************************************************************************
 */

package com.kodiak.xdms.server.subsmgmt.business.validator.rules;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.common.resources.KnConstants;
import com.kodiak.xdms.server.subsmgmt.business.KnProvBOException;
import com.kodiak.xdms.server.subsmgmt.business.helper.KnProvInfoUtil;
import com.kodiak.xdms.server.subsmgmt.business.validator.KnProvBOValidationException;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPCorpProfileInfoDTO;
import com.kodiak.xdms.server.subsmgmt.dto.persistdat.KnSubsProfilePersistDTO;
import com.kodiak.xdms.server.subsmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvConstants;

public class KnCorpSubsCntValidationRule extends KnValidatorRule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnCorpSubsCntValidationRule.class);
    private static final String className = KnCorpSubsCntValidationRule.class.getName();
    private static KnProvInfoUtil provInfoUtil;

    static {
        provInfoUtil = new KnProvInfoUtil();
    }

    public void validate() throws KnValidationException {
        String methodName = "validate()";

        IPersistenceDTO persistDTO = getDTO();
        String extCorpId;
        int maxSubsPerCorp = 0;
        knLogger.info( methodName, "validating if Max Corp Subscriber count reached");
        knLogger.debug( methodName, "DTO received - " + persistDTO);

        KnSubsProfilePersistDTO subsProfilePersistDTO;
        if (persistDTO instanceof KnSubsProfilePersistDTO) {
            subsProfilePersistDTO = (KnSubsProfilePersistDTO) persistDTO;
            extCorpId = subsProfilePersistDTO.getExtCorpId();
        } else {
            knLogger.error( methodName, "Invalid DTO is passed for validation " + persistDTO.getClass());
            throw new KnProvBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Un-expected dao DTO is passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), KnProvConstants.KEY_DATATYPE_MDN);
        }

        try {
            int corpSubscriptionType = subsProfilePersistDTO.getCorporateSubscriptionType();
            if (corpSubscriptionType == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
                KnOPCorpProfileInfoDTO corpProfileRespDTO = provInfoUtil.retrieveCorpProfile(extCorpId, null);
                int corpId = corpProfileRespDTO.getCorpId();
                int maxSubsCorporateLimit = corpProfileRespDTO.getMaxSubscribers();
                int maxSubsPerCorpSystemLevel = provInfoUtil.retrieveXdmsSvcConfigInfo().getMaxSubscrPerCorp();
                if(maxSubsCorporateLimit > 0) {
                    maxSubsPerCorp = maxSubsPerCorpSystemLevel > maxSubsCorporateLimit ? maxSubsCorporateLimit : maxSubsPerCorpSystemLevel;
                }else{
                    maxSubsPerCorp = maxSubsPerCorpSystemLevel;
                }
                knLogger.debug(methodName, "maxSubsPerCorpSystemLevel: ", maxSubsPerCorpSystemLevel, " ,maxSubsCorporateLimit: ",
                        maxSubsCorporateLimit);
                if (corpId > 0) {
                    knLogger.debug( methodName, "Retrieving the corporation [" + corpId + "] current Subscriber count ");
                    int corpSubscriberCount = provInfoUtil.retrieveCorpSubsCount(corpId, null);
                    knLogger.debug( methodName, "Retrieved Corp subscriber count - " + corpSubscriberCount);
                    if (corpSubscriberCount + 1 > maxSubsPerCorp) {
                        throw new KnProvBOValidationException(KnErrorCodes.Validator.MAX_SUBS_LIMIT_REACHED_FOR_CORP,
                                "Validation failed, Max limit for subscriber is reached for corp Id" + corpId,
                                entityId, operationId, getRuleId(), KnProvConstants.KEY_DATATYPE_EXTCORPID);
                    }
                }
                knLogger.info( methodName, "Max subscriber per corp validation  is successful");

            } else {
                knLogger.info( methodName, "Max subscriber per corp validation is skipped as " +
                        "corporate subscription is not enabled");
            }

        } catch (KnProvBOException e) {
            knLogger.error( methodName, "Validation failed. Could not retrieve Corporation Info");
            throw new KnProvBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Validation failed. Failed to retrieve Corporation Info.",
                    entityId, operationId, getRuleId(), KnProvConstants.KEY_DATATYPE_MDN);
        }

    }
}
