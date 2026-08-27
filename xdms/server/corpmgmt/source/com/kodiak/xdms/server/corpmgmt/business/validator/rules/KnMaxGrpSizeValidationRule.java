/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnMaxGrpSizeValidationRule.java
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
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpBCGrpPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpUserProfilePersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;
import java.util.Arrays;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.corpmgmt.resources.KnOperationTypes;


public class KnMaxGrpSizeValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnMaxGrpSizeValidationRule.class);

    /**
     * This method implements the actual logic for validation
     *
     * @throws KnValidationException
     */
    public void validate() throws KnValidationException {

        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug("validate", "ENTRY : Validating if total group members count exceeds max members ",
                "per group limit. ");
        KnCorpGroupInfoPersistDTO groupPersistDTO;
        int resultantMemCount = 0;
        int maxMembersAllowedPerGroup = 0;
        int groupType = 0;
        if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
            groupPersistDTO = (KnCorpGroupInfoPersistDTO) persistDTO;
            int groupMembersCount = groupPersistDTO.getGroupMemberCount();
            // In case of create group, anyways there won't be current members so we won't subtract 1.
            if (groupPersistDTO.getGroupMemberCount() == 0) {
                groupMembersCount += 1;
            }
            int newMemberCount = 0;// = groupPersistDTO.getNewMembers().size();
            int removeMembersCount = 0;// = groupPersistDTO.getRemovedMembers().size();
            //calculating the resultant group members count after addition/removal of group members
            resultantMemCount = groupMembersCount + newMemberCount - removeMembersCount;
            // -1 is to accomodate Owner MDN. Owner is also inserted in DG.CONNECTEDSESSIONMEMBERLIST thus we will take
            // current group members - 1 instead of current group members for validation.

            KnIPCorpGroupInfoDTO groupIpDTO = (KnIPCorpGroupInfoDTO) groupPersistDTO.getInputDTO();
            groupType = groupIpDTO.getGroupType();
            if (groupType == KnConstants.STANDARD_GROUP) {
                if(groupPersistDTO.getLargeGroupSupported() == 1){
                    maxMembersAllowedPerGroup = groupPersistDTO.getMaxMemPerLargeGroup();
                }else{
                    maxMembersAllowedPerGroup = groupPersistDTO.getMaxNumberOfMembers();
                }
            } else if (groupType == KnConstants.DISPATCH_GROUP) {
                if(groupPersistDTO.getLargeGroupSupported() == 1){
                    maxMembersAllowedPerGroup = groupPersistDTO.getMaxMemPerLargeGroup();
                    if(groupPersistDTO.getGroupCreatedBy() == KnConstants.CREATED_BY.ABDG.value()){
                        maxMembersAllowedPerGroup = groupPersistDTO.getMaxMemsPerLrgAbdgGrp();
                    }
                }else{
                    maxMembersAllowedPerGroup = groupPersistDTO.getMaxSubscriberPerDispatchGroup();
                }
            }
        } else if(persistDTO instanceof KnCorpBCGrpPersistDTO){
            KnCorpBCGrpPersistDTO bcGrpPersistDTO = (KnCorpBCGrpPersistDTO) persistDTO;
            KnIPCorpGroupInfoDTO groupIpDTO = (KnIPCorpGroupInfoDTO) bcGrpPersistDTO.getInputDTO();
            groupType = bcGrpPersistDTO.getGroupType();
            if(bcGrpPersistDTO.getLargeGroupSupported() == 1){
                maxMembersAllowedPerGroup = bcGrpPersistDTO.getMaxMemPerLargeBCGroup();
            }else{
                maxMembersAllowedPerGroup = bcGrpPersistDTO.getMaxAllowedMemPerBCG();
            }
            if (groupIpDTO.getOperationType().equals(KnOperationTypes.CREATE_BROADCAST_GROUP) ||
                    groupIpDTO.getOperationType().equals(KnOperationTypes.MODIFY_BROADCAST_GROUP)) {
                resultantMemCount = bcGrpPersistDTO.getGroupMemberCount();
            }
        } else if (persistDTO instanceof KnCorpUserProfilePersistDTO) {
            KnCorpUserProfilePersistDTO userProfilePersistDTO = (KnCorpUserProfilePersistDTO) persistDTO;
            knLogger.debug("userProfilePersistDTO.getGroupType()", userProfilePersistDTO.getGroupType(),
                    " userProfilePersistDTO.isMaxMembersAllowedPerGroupIsReached()",
                    userProfilePersistDTO.isMaxMembersAllowedPerGroupIsReached());
            if (userProfilePersistDTO.isMaxMembersAllowedPerGroupIsReached()) {
                //checking for maximum group size reached after addtion/removal of group members
                knLogger.error("validate", "Validation rule failed. ", "Group members count : ", resultantMemCount,
                        " exceeds Max Members Per Group ", "", " limit.");
                if (userProfilePersistDTO.getGroupType() == KnConstants.STANDARD_GROUP) {
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.MAX_GRP_SIZE_REACHED, "Validation Rule Failed. " +
                            "Subscribers Count exceeds : " + " ", getEntityId(), getOperationType(), getRuleId(), " ", "");
                } else if (userProfilePersistDTO.getGroupType() == KnConstants.DISPATCH_GROUP) {
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.MAX_DISPATCH_GROUP_MEM_LIMIT_EXCEEDED, "Validation Rule Failed. " +
                            "Subscribers Count exceeds : ", getEntityId(), getOperationType(), getRuleId(), "", "");
                } else if (userProfilePersistDTO.getGroupType() == KnConstants.BROADCAST_GROUP) {
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.MAX_BROADCAST_GRP_SIZE_REACHED, "Validation Rule Failed. " +
                            "Subscribers Count exceeds : ", getEntityId(), getOperationType(), getRuleId(), "", "");
                }
            }
        } else {
            knLogger.error("validate", "Unexpected DTO passed - ", persistDTO.getClass(),
                    ", Expected Dto - KnCLGroupInfoPersistDTO ");
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), "", "");//KnConstants.KEY_DATATYPE_MDN, "");
        }
        if (resultantMemCount > maxMembersAllowedPerGroup) {
            //checking for maximum group size reached after addtion/removal of group members
            knLogger.error("validate", "Validation rule failed. ", "Group members count : ", resultantMemCount,
                    " exceeds Max Members Per Group ", maxMembersAllowedPerGroup, " limit.");
            if (groupType == KnConstants.STANDARD_GROUP) {
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.MAX_GRP_SIZE_REACHED, "Validation Rule Failed. " +
                        "Subscribers Count exceeds : " + maxMembersAllowedPerGroup,getEntityId(), getOperationType(), getRuleId(), Arrays.asList(maxMembersAllowedPerGroup).toString(), "");
            } else if (groupType == KnConstants.DISPATCH_GROUP) {
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.MAX_DISPATCH_GROUP_MEM_LIMIT_EXCEEDED, "Validation Rule Failed. " +
                        "Subscribers Count exceeds : " + maxMembersAllowedPerGroup,getEntityId(), getOperationType(), getRuleId(), Arrays.asList(maxMembersAllowedPerGroup).toString(), "");
            } else if (KnConstants.BROADCAST_GROUP == groupType) {
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.MAX_BROADCAST_GRP_SIZE_REACHED, "Validation Rule Failed. " +
                        "Subscribers Count exceeds : " + maxMembersAllowedPerGroup,getEntityId(), getOperationType(), getRuleId(), Arrays.asList(maxMembersAllowedPerGroup).toString(), "");
            }
        }
        knLogger.debug("validate", "KnMaxGroupSizeValidationRule Validated successfully.");
    }
}

