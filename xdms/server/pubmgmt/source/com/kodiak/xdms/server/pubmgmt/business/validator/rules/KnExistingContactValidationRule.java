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
import com.kodiak.xdms.server.pubmgmt.dto.common.KnMemberDTO;
import com.kodiak.xdms.server.pubmgmt.dto.persistdat.KnDynamicContactPersistDTO;
import com.kodiak.xdms.server.pubmgmt.resources.KnConstants;
import com.kodiak.xdms.server.pubmgmt.resources.KnErrorCodes;

import java.util.ArrayList;
import java.util.List;

public class KnExistingContactValidationRule extends KnValidatorRule {

    private static final KnLogger knLogger = KnLogger.getLogger(KnExistingContactValidationRule.class);

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
        KnDynamicContactPersistDTO dynamicContPerDto;
        if (persistDTO instanceof KnDynamicContactPersistDTO) {
            dynamicContPerDto = (KnDynamicContactPersistDTO) persistDTO;
            KnIPPubContactInfoDTO inputDto = (KnIPPubContactInfoDTO) dynamicContPerDto.getInputDTO();
            List<String> invalidMdnList = new ArrayList<>();
            if(inputDto.getModifiedContList() != null && dynamicContPerDto.getExistingContList() != null){
                for (KnMemberDTO member : inputDto.getModifiedContList()) {
                    if (!dynamicContPerDto.getExistingContList().contains(member.getMemberMdn())) {
                        invalidMdnList.add(member.getMemberMdn());
                    }
                }
            }
            if(dynamicContPerDto.getExistingContList() == null){
                if(inputDto.getModifiedContList() != null){
                    for (KnMemberDTO member : inputDto.getModifiedContList()) {
                        invalidMdnList.add(member.getMemberMdn());
                    }
                }
            }

            knLogger.debug(methodName, "invalidMdnList - ", KnGDPRTemplate.mdnList(invalidMdnList));
            if (!invalidMdnList.isEmpty()) {
                knLogger.error(methodName, "Modifying Contact not exist - ", KnGDPRTemplate.mdnList(invalidMdnList));
                throw new KnPubBOValidationException(KnErrorCodes.Validator.MODIFYING_CONTACT_NOT_EXIST,
                        "Modifying Contact not exist - " + persistDTO.getClass(),
                        getEntityId(), getOperationType(), getRuleId(), KnConstants.KEY_DATATYPE_MDN, invalidMdnList.toString());
            }
            List<String> alreadyExistingCont = new ArrayList<>();
            if(dynamicContPerDto.getAddedContactDetailList() != null && dynamicContPerDto.getExistingContList() != null){
                for (KnMemberDTO member : dynamicContPerDto.getAddedContactDetailList()) {
                    if (dynamicContPerDto.getExistingContList().contains(member.getMemberMdn())) {
                        alreadyExistingCont.add(member.getMemberMdn());
                    }
                }
            }

            knLogger.debug(methodName, "alreadyExistingCont - ", KnGDPRTemplate.mdnList(alreadyExistingCont));
            if (!alreadyExistingCont.isEmpty()) {
                knLogger.error(methodName, "Added Contact already exist - ", KnGDPRTemplate.mdnList(alreadyExistingCont));
                throw new KnPubBOValidationException(KnErrorCodes.Validator.ADDED_CONTACT_ALRREADY_EXIST,
                        "Added Contact already exist - " + persistDTO.getClass(),
                        getEntityId(), getOperationType(), getRuleId(), KnConstants.KEY_DATATYPE_MDN, alreadyExistingCont.toString());
            }

        } else {
            knLogger.error(methodName, "Unexpected DTO passed - " + persistDTO.getClass() +
                    ", Expected Dto - KnDynamicContactPersistDTO ");
            throw new KnPubBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), KnConstants.KEY_DATATYPE_MDN, "");
        }
    }
}
