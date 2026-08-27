/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnMaxGroupSizeValidationRule.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Jan 11, 2011      7.0
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
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpGroupInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import com.kodiak.logger.KnLogger;


public class KnMaxGroupSizeValidationRule extends KnValidatorRule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnMaxGroupSizeValidationRule.class);
    private final String CLASS = KnMaxGroupSizeValidationRule.class.getName();

    /**
     * This method implements the actual logic for validation
     *
     * @throws KnValidationException
     */
    public void validate() throws KnValidationException {

        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug( "validate", "ENTRY : Validating if total group members count exceeds max members " ,
                "per group limit. ");
        KnCorpGroupInfoPersistDTO groupPersistDTO;
        if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
            groupPersistDTO = (KnCorpGroupInfoPersistDTO) persistDTO;
        } else {
            knLogger.error( "validate", "Unexpected DTO passed - " , persistDTO.getClass() ,
                    ", Expected Dto - KnCLGroupInfoPersistDTO ");
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), "", "");//KnConstants.KEY_DATATYPE_MDN, "");
        }
        int groupMembersCount = groupPersistDTO.getGroupMemberCount();
        // In case of create group, anyways there won't be current members so we won't subtract 1.
        if (groupPersistDTO.getGroupMemberCount() == 0) {
            groupMembersCount += 1;
        }
        int newMemberCount = 0;// = groupPersistDTO.getNewMembers().size();
        int removeMembersCount = 0;// = groupPersistDTO.getRemovedMembers().size();
        //calculating the resultant group members count after addition/removal of group members
        int resultantMemCount = groupMembersCount + newMemberCount - removeMembersCount;
        // -1 is to accomodate Owner MDN. Owner is also inserted in DG.CONNECTEDSESSIONMEMBERLIST thus we will take
        // current group members - 1 instead of current group members for validation.
        int maxMembersAllowedPerGroup = 0;
        KnIPCorpGroupInfoDTO groupIpDTO = (KnIPCorpGroupInfoDTO) groupPersistDTO.getInputDTO();
        int groupType = groupIpDTO.getGroupType();
        if (groupType == KnConstants.STANDARD_GROUP) {
            maxMembersAllowedPerGroup = groupPersistDTO.getMaxNumberOfMembers();
        } else if (groupType == KnConstants.DISPATCH_GROUP) {
            maxMembersAllowedPerGroup = groupPersistDTO.getMaxSubscriberPerDispatchGroup();
        }


        if (resultantMemCount > maxMembersAllowedPerGroup) {
            //checking for maximum group size reached after addtion/removal of group members
            knLogger.error( "validate", "Validation rule failed. " , "Group members count : " , resultantMemCount ,
                    " exceeds Max Members Per Group " , maxMembersAllowedPerGroup , " limit.");
            if (groupType == KnConstants.STANDARD_GROUP) {
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.MAX_GRP_SIZE_REACHED, "Validation Rule Failed. " +
                        "Subscribers Count exceeds : " + maxMembersAllowedPerGroup, getEntityId(),
                        getOperationType(), getRuleId(), KnConstants.KEY_DATATYPE_GROUPNAME,
                        Integer.toString(maxMembersAllowedPerGroup));
            } else if (groupType == KnConstants.DISPATCH_GROUP) {
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.MAX_DISPATCH_GROUP_MEM_LIMIT_EXCEEDED, "Validation Rule Failed. " +
                        "Subscribers Count exceeds : " + maxMembersAllowedPerGroup, getEntityId(),
                        getOperationType(), getRuleId(), KnConstants.KEY_DATATYPE_GROUPNAME,
                        Integer.toString(maxMembersAllowedPerGroup));
            }
        }
        knLogger.debug( "validate", "KnMaxGroupSizeValidationRule Validated successfully.");
    }
}

