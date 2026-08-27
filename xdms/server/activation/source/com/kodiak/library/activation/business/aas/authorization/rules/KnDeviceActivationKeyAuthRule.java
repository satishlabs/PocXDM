/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.library.activation.business.aas.authorization.rules;

import com.kodiak.library.activation.dto.persistdat.KnSubscriptionKeyInfoPersistDTO;
import com.kodiak.library.activation.resources.KnErrorCodes;
import com.kodiak.library.activation.resources.KnProfileTypes;
import com.kodiak.logger.KnLogger;
import com.kodiak.common.exception.KnSystemException;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.aas.authorization.KnAuthorizationException;
import com.kodiak.xdms.server.common.framework.aas.authorization.KnAuthorizationRule;

import java.util.Calendar;

public class KnDeviceActivationKeyAuthRule extends KnAuthorizationRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnDeviceActivationKeyAuthRule.class);


    /**
     * This method will do the actual authorization
     *
     * @param persistDTO the persist dto passed for authorization
     * @throws KnAuthorizationException if there is any error
     */
    public void authorize(IPersistenceDTO persistDTO) throws KnAuthorizationException {
        //get the required datas
        knLogger.debug( METHOD, "Persist DTO ->" + persistDTO);
        //stores the profile type of the user
        String profile = getProfile();
        //stores the operation Id
        String opId = getOperationId();
        //stores the Authorization rule Id
        String ruleId = getRuleId();
        long expiryTime;
        //getting the performer DTO
        IPersistenceDTO performerDTO = persistDTO.getPersistenceDTO();
        knLogger.info( METHOD, "Performer DTO ->" + performerDTO);

        KnSubscriptionKeyInfoPersistDTO subKeyDTO = null;
        //checking for profile type
        if (KnProfileTypes.WEBSERVICE_PROFILE.equals(profile)) {
            subKeyDTO = (KnSubscriptionKeyInfoPersistDTO) persistDTO;
            knLogger.info( METHOD, "Persist DTO ->" + subKeyDTO);
            expiryTime = subKeyDTO.getExpiryTime();
        } else {
            knLogger.error( METHOD, "Invalid Profile - " + profile + ", Expected ->" +
                    KnProfileTypes.WEBSERVICE_PROFILE);
            throw new KnSystemException(KnErrorCodes.Authorizer.INVALID_PROFILE, "Invalid Profile ->" +
                    profile + ", Expected ->" + KnProfileTypes.WEBSERVICE_PROFILE);
        }

        knLogger.debug( METHOD, "Profile -> " + profile + ", Operation -> " + opId
                + ", Rule -> " + ruleId);

        // Date currDate = new Date();
        // long currTime1 = currDate.getTime();
        long currTime = Calendar.getInstance().getTimeInMillis();
        knLogger.debug( METHOD, "Current time calculated: "+currTime);

        boolean isValid = false;
        if (currTime < expiryTime) {
            knLogger.debug( METHOD, "Validited ActivationKey Successfully ");
            isValid = true;
        } else {
            knLogger.error( METHOD, "ActivationKey is expired ");
            throw new KnAuthorizationException(KnErrorCodes.Authorizer.INVALID_ACTIVATION_KEY,
                    "Invalid Activation Key. ActKey : " + subKeyDTO.getActivationKey(), profile, opId, ruleId);
        }
    }
}
