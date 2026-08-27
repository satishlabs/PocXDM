/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnPairingIndicatorCorporateAuthRule.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Mar 15, 2011      7.0
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
package com.kodiak.xdms.server.corpmgmt.business.aas.authorization.rules;

import com.kodiak.common.exception.KnSystemException;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.dto.persistdat.KnCorpProfilePersistDTO;
import com.kodiak.xdms.server.common.framework.aas.authorization.KnAuthorizationException;
import com.kodiak.xdms.server.common.framework.aas.authorization.KnAuthorizationRule;
import com.kodiak.xdms.server.common.resources.KnProfileTypes;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

public class KnPairingIndicatorCorporateAuthRule extends KnAuthorizationRule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnPairingIndicatorCorporateAuthRule.class);

    private static final String CLASS = KnPairingIndicatorCorporateAuthRule.class.getName();

    /**
     * This method will do the actual authorization
     *
     * @param persistDTO the persist dto passed for authorization
     * @throws com.kodiak.xdms.server.common.framework.aas.authorization.KnAuthorizationException
     *          if there is any error
     */
    public void authorize(IPersistenceDTO persistDTO) throws KnAuthorizationException {
        String methodName = "authorize(persistDTO)";
        //get the required datas
        String profile = getProfile();
        String opId = getOperationId();
        String ruleId = getRuleId();
        
        //checking for profile type
        if (KnProfileTypes.CORP_PROFILE.equals(profile)) {
            KnCorpProfilePersistDTO corpProfilePersistDTO = (KnCorpProfilePersistDTO) persistDTO;
            if (corpProfilePersistDTO.getPairedContactListId() > 0) {
                knLogger.error( methodName, "Invalid Corporate For CAT. This is small corporate with" ,
                        " pairing indicator as true.So reject the request - " ,corpProfilePersistDTO);
                throw new KnAuthorizationException(KnErrorCodes.Authorizer.UNAUTHORISED_CORPORATE,
                        "Unauthorised Corporate For CAT - " + corpProfilePersistDTO, profile, opId, ruleId);
            }
        } else {
            knLogger.error( METHOD, "Invalid Profile - " , profile , ", Expected - " , KnProfileTypes.CORP_PROFILE);
            throw new KnSystemException(KnErrorCodes.Authorizer.INVALID_PROFILE, "Invalid Profile - " +
                    profile + ", Expected - " + KnProfileTypes.CORP_PROFILE);
        }
        knLogger.info( METHOD, "KnPairingIndicatorCorporateAuthRule - authorization done successfully.");
    }
}
