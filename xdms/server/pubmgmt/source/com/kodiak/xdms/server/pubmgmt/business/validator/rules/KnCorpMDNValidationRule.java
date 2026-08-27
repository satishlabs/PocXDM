/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.business.validator.rules;

import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.common.KnCorpProfileDTO;
import com.kodiak.xdms.server.common.dto.common.KnSubsProfileDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.pubmgmt.business.validator.KnPubBOValidationException;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnIPPubContactInfoDTO;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnIPPubGroupInfoDTO;
import com.kodiak.xdms.server.pubmgmt.dto.common.KnGroupMemberDTO;
import com.kodiak.xdms.server.pubmgmt.dto.common.KnMemberDTO;
import com.kodiak.xdms.server.pubmgmt.dto.persistdat.KnDynamicContactPersistDTO;
import com.kodiak.xdms.server.pubmgmt.dto.persistdat.KnDynamicGroupPersistDTO;
import com.kodiak.xdms.server.pubmgmt.resources.KnConstants;
import com.kodiak.xdms.server.pubmgmt.resources.KnErrorCodes;

import java.util.ArrayList;
import java.util.List;

public class KnCorpMDNValidationRule extends KnValidatorRule {

    private static final KnLogger knLogger = KnLogger.getLogger(KnCorpMDNValidationRule.class);

    /**
     * validate method. all the validators should implement this method
     *
     * @throws KnValidationException
     */
    @Override
    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug(methodName, "ENTRY : corporate MDN validation ");
        List<String> invalidMdnList = new ArrayList<>();
        KnSubsProfileDTO subsProfileDTO;

        if (persistDTO instanceof KnDynamicContactPersistDTO) {
            KnDynamicContactPersistDTO dynamicContPerDto = (KnDynamicContactPersistDTO) persistDTO;
            subsProfileDTO = dynamicContPerDto.getSubsProfileDTO();
            List<KnMemberDTO> addedContactList = dynamicContPerDto.getAddedContactDetailList();
            List<String> addList = dynamicContPerDto.getAddedContList();

            if (addedContactList != null) {
                for (KnMemberDTO member : addedContactList) {
                    if (member.getCorpId() != subsProfileDTO.getCorpId()) {
                        invalidMdnList.add(member.getMemberMdn());
                    }
                    addList.remove(member.getMemberMdn());
                }

            }
            knLogger.debug(methodName, "addList - ", KnGDPRTemplate.mdnList(addList));
            knLogger.debug(methodName, "invalidMdnList - ", KnGDPRTemplate.mdnList(invalidMdnList));
            if (dynamicContPerDto.getValidReqExtContactList() != null) {
                invalidMdnList.removeAll(dynamicContPerDto.getValidReqExtContactList());
            }
            if(addList != null){
                invalidMdnList.addAll(addList);
            }

        }  else if (persistDTO instanceof KnDynamicGroupPersistDTO) {
            KnDynamicGroupPersistDTO dynamicGrpPerDto = (KnDynamicGroupPersistDTO) persistDTO;
            subsProfileDTO = dynamicGrpPerDto.getSubsProfile();
            List<String> addList = dynamicGrpPerDto.getAddedMemList();
            List<KnMemberDTO> addedMemList = dynamicGrpPerDto.getAddedMemberDetailList();
            if (addedMemList != null) {
                for (KnMemberDTO member : addedMemList) {
                    if ((member.getCorpId() != subsProfileDTO.getCorpId())||(!addList.contains(member.getMemberMdn()))) {
                        invalidMdnList.add(member.getMemberMdn());
                    }
                    addList.remove(member.getMemberMdn());
                }
            }
            knLogger.debug(methodName, "addList - ", KnGDPRTemplate.mdnList(addList));
            knLogger.debug(methodName, "invalidMdnList - ", KnGDPRTemplate.mdnList(invalidMdnList));
            if (dynamicGrpPerDto.getValidReqExtContactList() != null) {
                invalidMdnList.removeAll(dynamicGrpPerDto.getValidReqExtContactList());
            }
            if(addList != null){
                invalidMdnList.addAll(addList);
            }

        }else {
            knLogger.error(methodName, "Unexpected DTO passed - " + persistDTO.getClass() +
                    ", Expected Dto - KnDynamicContactPersistDTO ");
            throw new KnPubBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), KnConstants.KEY_DATATYPE_MDN, "");
        }

        knLogger.debug(methodName, "invalidMdnList After removing external contact - ", KnGDPRTemplate.mdnList(invalidMdnList));

        if (!invalidMdnList.isEmpty()) {
            knLogger.error(methodName, "MDN not in corporation are - ", KnGDPRTemplate.mdnList(invalidMdnList));
            throw new KnPubBOValidationException(KnErrorCodes.Validator.MDN_NOT_OF_CORPORATION,
                    "MDN not in corporation - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), KnConstants.KEY_DATATYPE_MDN, invalidMdnList.toString());
        }


    }
}
