/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnGroupMembershipAuthRule.java
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
import com.kodiak.xdms.server.common.framework.aas.authorization.KnAuthorizationException;
import com.kodiak.xdms.server.common.framework.aas.authorization.KnAuthorizationRule;
import com.kodiak.xdms.server.common.resources.KnProfileTypes;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupMemPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

public class KnGroupMembershipAuthRule extends KnAuthorizationRule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnGroupMembershipAuthRule.class);

    private static final String CLASS = KnGroupMembershipAuthRule.class.getName();

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
            KnCorpGroupInfoPersistDTO groupPersistDTO = (KnCorpGroupInfoPersistDTO) persistDTO;
            /*Collection<KnCorpGroupMemberDTO> groupMemberList = groupPersistDTO.getGroupMembers();
            knLogger.debug( methodName, "GroupMembers - " , groupMemberList);
            knLogger.debug( methodName, "Performer - " , dto);
            boolean exists = false;
            if (groupMemberList != null && !groupMemberList.isEmpty()) {
                for (KnCorpGroupMemberDTO member : groupMemberList) {
                    String memberMdn = member.getMdn();
                    String performerMdn = dto.getMdn();
                    knLogger.debug( methodName, "MemberMdn - " , memberMdn);
                    knLogger.debug( methodName, "PerformerMdn - " , performerMdn);
                    if (memberMdn != null && performerMdn != null) {
                        if (performerMdn.trim().equals(memberMdn.trim())) {
                            exists = true;
                            break;
                        }
                    }
                }
            }
            knLogger.debug( methodName, "exists - " , exists);*/
            if (!groupPersistDTO.isGroupMember()) {
                knLogger.error( methodName, "Invalid Member. Subscriber does not belong to group - " , dto);
                throw new KnAuthorizationException(KnErrorCodes.Authorizer.MDN_DOESNOT_BELONG_TO_GROUP,
                        "Invalid Member - " + dto, profile, opId, ruleId);
            }

        } else {
            knLogger.error( METHOD, "Invalid Profile - " , profile , ", Expected - " , KnProfileTypes.CORP_PROFILE);
            throw new KnSystemException(KnErrorCodes.Authorizer.INVALID_PROFILE, "Invalid Profile - " +
                    profile + ", Expected - " + KnProfileTypes.CORP_PROFILE);
        }
        knLogger.info( METHOD, "KnGroupMembershipAuthRule - authorization done successfully.");
    }
}
