/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnMaxGroupSizeValidationRule.java
 * Subsystem:  pubmgmt
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * Harsha A             Feb 2, 2011           7.0
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
package com.kodiak.xdms.server.pubmgmt.business.validator.rules;

import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.pubmgmt.business.validator.KnPubBOValidationException;
import com.kodiak.xdms.server.pubmgmt.dto.persistdat.KnDynamicGroupPersistDTO;
import com.kodiak.xdms.server.pubmgmt.dto.persistdat.KnPubGroupInfoPersistDTO;
import com.kodiak.xdms.server.pubmgmt.resources.KnErrorCodes;

import static com.kodiak.xdms.server.pubmgmt.resources.KnConstants.*;

import com.kodiak.logger.KnLogger;

public class KnMaxGroupSizeValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnMaxGroupSizeValidationRule.class);

    private final String CLASS = KnMaxGroupSizeValidationRule.class.getName();


    public void validate() throws KnValidationException {

        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug("validate", "ENTRY : Validating if total group members count exceeds max members " +
                "per group limit. dto passed ->" + persistDTO);
        int maxMembersAllowedPerGroup;
        int resultantMemCount = 0;
        int groupMembersCount = 0;

        if (persistDTO instanceof KnDynamicGroupPersistDTO) {
            KnDynamicGroupPersistDTO groupPersistDTO = (KnDynamicGroupPersistDTO) persistDTO;
            // current group members - 1 instead of current group members for validation.
            maxMembersAllowedPerGroup = groupPersistDTO.getMaxNumberOfMembers();

            //calculating the resultant group members count after addition/removal of group members
            int removedMemCount = 0;
            int addedMemCount = 0;
            int exisMemCount = 0;
            if( groupPersistDTO.getRemovedMembers() != null ){
                removedMemCount = groupPersistDTO.getRemovedMembers().size();
            }
            if(groupPersistDTO.getAddedMemberDetailList() != null){
                addedMemCount = groupPersistDTO.getAddedMemberDetailList().size();
            }
            if(groupPersistDTO.getExistingMembers() != null){
               exisMemCount =  groupPersistDTO.getExistingMembers().size();
            }
            if(addedMemCount != 0){
                resultantMemCount = exisMemCount + addedMemCount - removedMemCount;
            }

        } else if (persistDTO instanceof KnPubGroupInfoPersistDTO) {
            KnPubGroupInfoPersistDTO groupPersistDTO = (KnPubGroupInfoPersistDTO) persistDTO;
            // current group members - 1 instead of current group members for validation.
            maxMembersAllowedPerGroup = groupPersistDTO.getMaxNumberOfMembers();
            groupMembersCount = groupPersistDTO.getGroupMemberCount();
            int memberCount = groupPersistDTO.getGroupMembers().size();
            //calculating the resultant group members count after addition/removal of group members
            if(memberCount != 0){
                resultantMemCount = groupMembersCount + memberCount;
            }

        } else {
            knLogger.error("validate", "Unexpected DTO passed - " + persistDTO.getClass() +
                    ", Expected Dto - KnPubGroupInfoPersistDTO ");
            throw new KnPubBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), KEY_DATATYPE_MDN, "");
        }


        if (resultantMemCount > maxMembersAllowedPerGroup) {
            //checking for maximum group size reached after addtion/removal of Contact members
            knLogger.error("validate", "Validation rule failed. " + "Group members count : " + resultantMemCount +
                    " exceeds Max Members Per Group " + maxMembersAllowedPerGroup + " limit.");
            throw new KnPubBOValidationException(KnErrorCodes.Validator.MAX_GROUP_SIZE_REACHED, "Validation Rule Failed. " +
                    "Subscribers Count exceeds : " + maxMembersAllowedPerGroup, getEntityId(),
                    getOperationType(), getRuleId(), KEY_DATATYPE_GROUPNAME,
                    Integer.toString(maxMembersAllowedPerGroup), Integer.toString(groupMembersCount));
        }
        knLogger.info("validate", "KnMaxGroupSizeValidationRule Validated successfully.");

    }
}
