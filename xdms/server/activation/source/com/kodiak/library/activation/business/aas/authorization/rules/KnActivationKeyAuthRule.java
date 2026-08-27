/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnActivationKeyAuthRule.java
 * Subsystem:  Activation Library
 * <p/>
 * Name                   Date         Release
 * -------------------- ------------ -------------------------------------
 * Rashmi Kamat         29-Oct-2010       6.4
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
package com.kodiak.library.activation.business.aas.authorization.rules;

import com.kodiak.common.exception.KnSystemException;
import com.kodiak.library.activation.resources.KnConstants;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.aas.authorization.KnAuthorizationException;
import com.kodiak.xdms.server.common.framework.aas.authorization.KnAuthorizationRule;
import com.kodiak.logger.KnLogger;
import com.kodiak.library.activation.dto.persistdat.KnSubscriptionKeyInfoPersistDTO;
import com.kodiak.library.activation.dto.clientdat.KnIPClientRegistryDTO;
import com.kodiak.library.activation.resources.KnErrorCodes;
import com.kodiak.library.activation.resources.KnProfileTypes;

import java.util.Calendar;

public class KnActivationKeyAuthRule extends KnAuthorizationRule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnActivationKeyAuthRule.class);

    private static final String CLASS = KnActivationKeyAuthRule.class.getName();

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
        String serviceName = null;
        String clientServiceName = null;
        //getting the performer DTO
        IPersistenceDTO performerDTO = persistDTO.getPersistenceDTO();
        knLogger.info( METHOD, "Performer DTO ->" + performerDTO);

        KnSubscriptionKeyInfoPersistDTO subKeyDTO = null;
        //checking for profile type
        if (KnProfileTypes.WEBSERVICE_PROFILE.equals(profile)) {
            subKeyDTO = (KnSubscriptionKeyInfoPersistDTO) persistDTO;
            knLogger.info( METHOD, "Persist DTO ->" + subKeyDTO);
            expiryTime = subKeyDTO.getExpiryTime();
            serviceName = subKeyDTO.getServiceName();
            clientServiceName = ((KnIPClientRegistryDTO)subKeyDTO.getInputDTO()).getServiceName();
            knLogger.debug( METHOD, "Recieved - Expiry time: "+ expiryTime);
            knLogger.debug( METHOD, "Recieved - DB Service name: "+ serviceName + ", " +
                    "Client Service name: "+ clientServiceName);
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
        if (clientServiceName.equalsIgnoreCase(serviceName) && (currTime < expiryTime)) {
            knLogger.debug( METHOD, "Validited ActivationKey Successfully ");
            isValid = true;
        }
        if (serviceName.equals(KnConstants.SERVICENAME_PTT_RADIO_CLIENT) && clientServiceName.equals(KnConstants.SERVICENAME_HANDSET) && (currTime < expiryTime)){
            knLogger.debug( METHOD, "Validited ActivationKey Successfully ");
            isValid = true;
        }
        if (!isValid) {
            knLogger.error( METHOD, "ActivationKey is expired / Service name is not proper");
            throw new KnAuthorizationException(KnErrorCodes.Authorizer.INVALID_ACTIVATION_KEY,
                    "Invalid Activation Key. ActKey : " + subKeyDTO.getActivationKey(), profile, opId, ruleId);
        }
    }
}