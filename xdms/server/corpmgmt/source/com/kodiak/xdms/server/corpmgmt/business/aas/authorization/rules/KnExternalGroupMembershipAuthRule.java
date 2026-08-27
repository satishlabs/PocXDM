/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnCorpBOException.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Namita P Nair      March 15, 2011      7.0
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
import com.kodiak.xdms.server.common.framework.aas.authorization.KnAuthorizationException;
import com.kodiak.xdms.server.common.framework.aas.authorization.KnAuthorizationRule;
import com.kodiak.xdms.server.common.resources.KnProfileTypes;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupMemPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.Collection;


public class KnExternalGroupMembershipAuthRule extends KnAuthorizationRule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnExternalGroupMembershipAuthRule.class);

    private static final String CLASS = KnExternalGroupMembershipAuthRule.class.getName();

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

        IPersistenceDTO performerDTO = persistDTO.getPersistenceDTO();
        //checking for profile type
        if (KnProfileTypes.CORP_PROFILE.equals(profile)) {
            KnCorpGroupMemPersistDTO dto = (KnCorpGroupMemPersistDTO) performerDTO;
//            KnIPCorpGroupDTO groupDTO = (KnIPCorpGroupDTO) dto.getInputDTO();
            KnCorpGroupInfoPersistDTO groupPersistDTO = (KnCorpGroupInfoPersistDTO) persistDTO;
            Collection<KnCorpSubscriberDTO> externalContacts = groupPersistDTO.getExternalContacts();
            boolean isExternal = false;
            KnCorpSubscriberDTO subsc = new KnCorpSubscriberDTO();
            subsc.setMdn(dto.getMdn());
            if (externalContacts.contains(subsc)) {
                isExternal = true;
            }

            if (isExternal) {
                knLogger.error( methodName, "External member of Group. External Subscriber cannot access group details - " , dto);
                throw new KnAuthorizationException(KnErrorCodes.Authorizer.MDN_DOESNOT_BELONG_TO_CORP,
                        "External member of Group cannot acces Group Details", profile, opId, ruleId);
            }

        } else {
            knLogger.error( METHOD, "Invalid Profile - " , profile , ", Expected - " , KnProfileTypes.CORP_PROFILE);
            throw new KnSystemException(KnErrorCodes.Authorizer.INVALID_PROFILE, "Invalid Profile - " +
                    profile + ", Expected - " + KnProfileTypes.CORP_PROFILE);
        }
        knLogger.info( METHOD, "KnExternalGroupMembershipAuthRule - authorization done successfully.");
    }
}
