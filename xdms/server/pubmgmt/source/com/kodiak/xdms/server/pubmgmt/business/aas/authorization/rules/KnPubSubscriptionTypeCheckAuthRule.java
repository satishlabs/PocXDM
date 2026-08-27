/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.business.aas.authorization.rules;

import com.kodiak.xdms.server.common.framework.aas.authorization.KnAuthorizationRule;
import com.kodiak.xdms.server.common.framework.aas.authorization.KnAuthorizationException;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.dto.persistdat.KnSubscriberPersistDTO;
import com.kodiak.xdms.server.common.resources.KnErrorCodes;
import com.kodiak.xdms.server.common.resources.KnProfileTypes;
import com.kodiak.xdms.server.common.util.KnGeneralUtil;
import com.kodiak.logger.KnLogger;
import com.kodiak.common.exception.KnSystemException;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnPubSubscriptionTypeCheckAuthRule.java
 * Subsystem:  pubmgmt
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * SureshKumar G        Apr 11, 2011           7.0
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
public class KnPubSubscriptionTypeCheckAuthRule extends KnAuthorizationRule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnPubSubscriptionTypeCheckAuthRule.class);

    private static final String CLASS = KnPubSubscriptionTypeCheckAuthRule.class.getName();
    //name of the parameter configured in clg-aas.xml
    public static final String ALLOWED_SUBSCRIPTION_TYPES = "allowedSubscriptionTypes";

    /**
     * This method will do the atual authorization
     *
     * @param persistDTO the persist dto passed for authorization
     * @throws com.kodiak.xdms.server.common.framework.aas.authorization.KnAuthorizationException if there is any error
     */
    public void authorize(IPersistenceDTO persistDTO) throws KnAuthorizationException {
        //get the required datas
        String allowedSubscriptionTypes = getRuleParameter(ALLOWED_SUBSCRIPTION_TYPES);
        String profile = getProfile();
        String opId = getOperationId();
        String ruleId = getRuleId();

        IPersistenceDTO performerDTO = persistDTO.getPersistenceDTO();
        knLogger.debug( METHOD, "Performer DTO ->" + performerDTO +
                ", Allowed Subscription Types ->" + allowedSubscriptionTypes);

        if (allowedSubscriptionTypes == null || allowedSubscriptionTypes.trim().equals("")) {
            knLogger.error( METHOD, "required parameter(s) '" + ALLOWED_SUBSCRIPTION_TYPES +
                    "' is empty in configuration");
            throw new KnSystemException(KnErrorCodes.Authorizer.CONFIG_ERROR,
                    "required parameter(s) '" + ALLOWED_SUBSCRIPTION_TYPES + "' is empty in configuration");
        }

        int subscriptionType;
        //checking for profile type
        if (KnProfileTypes.PUBLIC_PROFILE.equals(profile)) {
            KnSubscriberPersistDTO dto = (KnSubscriberPersistDTO) performerDTO;
            subscriptionType = dto.getPubSubscriptionType();
        } else {
            knLogger.error( METHOD, "Invalid Profile - " + profile + ", Expected - " +
                    KnProfileTypes.PUBLIC_PROFILE);
            throw new KnSystemException(KnErrorCodes.Authorizer.INVALID_PROFILE, "Invalid Profile - " +
                    profile + ", Expected - " + KnProfileTypes.PUBLIC_PROFILE);
        }
        knLogger.debug( METHOD, "Profile -> " + profile + ", Operation -> " + opId
                + ", Rule -> " + ruleId + ", Params -> " + allowedSubscriptionTypes + ", DTO Param -> " +
                subscriptionType);
        //splitting the allowedServiceauthStatus' and putting into array
        int[] serviceStatusArr = KnGeneralUtil.convertStringToIntArray(allowedSubscriptionTypes, DELIM);
        boolean isValid = false;
        //checking if the service auth status of the performer matches with any one of the allowed
        //service auth status configured.
        for (int i = 0, len = serviceStatusArr.length; i < len; i++) {
            if (subscriptionType == serviceStatusArr[i]) {
                isValid = true;
                break;
            }
        }
        //if the service auth status of the performer matches none of the allowed services auth status
        //throw exception.
        if (!isValid) {
            knLogger.error( METHOD, "Failed because of invalid Subscription Type - " +
                    subscriptionType);
            throw new KnAuthorizationException(KnErrorCodes.Authorizer.INVALID_SUBSCRIPTION_TYPE,
                    "Invalid Subscription Type. Subscription Type : " + subscriptionType, profile, opId, ruleId);
        }
        knLogger.info( METHOD, "KnPubSubscriptionTypeCheckAuthRule - authorization done successfully.");
    }

}
