/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.business.validator.rules;

import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnIPPubContactInfoDTO;
import com.kodiak.xdms.server.pubmgmt.dto.common.KnMemberDTO;
import com.kodiak.xdms.server.pubmgmt.dto.persistdat.KnDynamicContactPersistDTO;
import com.kodiak.xdms.server.pubmgmt.dto.persistdat.KnPubContactInfoPersistDTO;
import com.kodiak.xdms.server.pubmgmt.business.validator.KnPubBOValidationException;
import com.kodiak.xdms.server.pubmgmt.business.helper.KnPubInfoUtil;
import com.kodiak.xdms.server.pubmgmt.business.KnPubBOException;
import com.kodiak.xdms.server.pubmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.pubmgmt.resources.KnConstants;
import com.kodiak.logger.KnLogger;

import java.util.ArrayList;
import java.util.List;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnMaxContactSizeValidationRule.java
 * Subsystem:  pubmgmt
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * SureshKumar G        Jan 15, 2011           7.0
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
public class KnMaxContactSizeValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnMaxContactSizeValidationRule.class);

    private final String CLASS = KnMaxContactSizeValidationRule.class.getName();


    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug(methodName, "ENTRY : Validating if total ContactList members count exceeds max members " +
                "per group limit. dto passed ->" + persistDTO);
        KnPubInfoUtil pubInfoUtil = new KnPubInfoUtil();

        int maxMembersAllowed = 0;
        int contactMembersCount = 0;
        int resultantMemCount = 0;

        if (persistDTO instanceof KnDynamicContactPersistDTO) {
            int existContCount = 0;
            KnDynamicContactPersistDTO dynamicContactPersistDTO = (KnDynamicContactPersistDTO) persistDTO;
            maxMembersAllowed = dynamicContactPersistDTO.getMaxNumberOfMembers();
            List<String> existingConts = new ArrayList<>();
            if(dynamicContactPersistDTO.getExistingContList() != null){
                existingConts.addAll(dynamicContactPersistDTO.getExistingContList());
                existContCount = existingConts.size();
            }
            KnIPPubContactInfoDTO inputDto = (KnIPPubContactInfoDTO) dynamicContactPersistDTO.getInputDTO();
            List<String> removedList = inputDto.getRemovedContList();
            List<KnMemberDTO> addedList = dynamicContactPersistDTO.getAddedContactDetailList();
            int addedCount = 0;
            if(addedList != null){
                addedCount = addedList.size();
            }
            if(removedList != null){
                existingConts.removeAll(removedList);
                existContCount = existingConts.size();
            }

            resultantMemCount = existContCount + addedCount;
        } else if (persistDTO instanceof KnPubContactInfoPersistDTO) {
            KnPubContactInfoPersistDTO groupPersistDTO;
            groupPersistDTO = (KnPubContactInfoPersistDTO) persistDTO;
            // current group members - 1 instead of current Contact members for validation.
            maxMembersAllowed = groupPersistDTO.getMaxNumberOfMembers();
            try {
                contactMembersCount = pubInfoUtil.getContactMemberCount(groupPersistDTO.getOwner());
            } catch (KnPubBOException pboe) {
                knLogger.error(methodName, "Validation rule failed. " + "Contact members count : " +
                        contactMembersCount);
                throw new KnPubBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR, "Validation Rule Failed. ",
                        getEntityId(), getOperationType(), getRuleId(), KnConstants.KEY_DATATYPE_MDN,
                        Integer.toString(maxMembersAllowed), Integer.toString(contactMembersCount));
            }
            int memberCount = groupPersistDTO.getContactMembers().size();
            if(memberCount > 1 ){
                knLogger.info(methodName, "Bulk contact addition so only new member count will be used");
                contactMembersCount = 0;
            }
            resultantMemCount = contactMembersCount + memberCount;
        } else {
            knLogger.error(methodName, "Unexpected DTO passed - " + persistDTO.getClass() +
                    ", Expected Dto - KnPubContactInfoPersistDTO ");
            throw new KnPubBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), KnConstants.KEY_DATATYPE_MDN, "");
        }


        if (resultantMemCount > maxMembersAllowed) {
            //checking for maximum group size reached after addtion/removal of Contact members
            knLogger.error(methodName, "Validation rule failed. " + "Contact members count : " + resultantMemCount +
                    " exceeds Max Members Per Contact List " + maxMembersAllowed + " limit.");
            throw new KnPubBOValidationException(KnErrorCodes.Validator.MAX_CONTACT_SIZE_REACHED, "Validation Rule Failed. " +
                    "Subscribers Count exceeds : " + maxMembersAllowed, getEntityId(),
                    getOperationType(), getRuleId(), KnConstants.KEY_DATATYPE_MDN,
                    Integer.toString(maxMembersAllowed), Integer.toString(contactMembersCount));
        }
        knLogger.info(methodName, "KnMaxGroupSizeValidationRule Validated successfully.");

    }
}
