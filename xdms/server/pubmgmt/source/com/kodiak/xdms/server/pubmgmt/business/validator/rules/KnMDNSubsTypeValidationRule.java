/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.business.validator.rules;

import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.common.KnSubsProfileDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.pubmgmt.business.validator.KnPubBOValidationException;
import com.kodiak.xdms.server.pubmgmt.dto.common.KnMemberDTO;
import com.kodiak.xdms.server.pubmgmt.dto.persistdat.KnDynamicContactPersistDTO;
import com.kodiak.xdms.server.pubmgmt.dto.persistdat.KnDynamicGroupPersistDTO;
import com.kodiak.xdms.server.pubmgmt.resources.KnConstants;
import com.kodiak.xdms.server.pubmgmt.resources.KnErrorCodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class KnMDNSubsTypeValidationRule extends KnValidatorRule {

    private static final KnLogger knLogger = KnLogger.getLogger(KnMDNSubsTypeValidationRule.class);

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
        KnSubsProfileDTO subsProfile;
        List<String> pubSubsList = new ArrayList<>();

        if (persistDTO instanceof KnDynamicContactPersistDTO) {
            KnDynamicContactPersistDTO dynamicContPerDto = (KnDynamicContactPersistDTO) persistDTO;
            List<KnMemberDTO> addedList = dynamicContPerDto.getAddedContactDetailList();
            subsProfile = dynamicContPerDto.getSubsProfileDTO();
            if (addedList != null) {
                for (KnMemberDTO member : addedList) {
                    if (member.getSubscriptionType() == 0) {
                        pubSubsList.add(member.getMemberMdn());
                    }
                }
            }

        }else if (persistDTO instanceof KnDynamicGroupPersistDTO) {
            KnDynamicGroupPersistDTO dynamicGrpPerDto = (KnDynamicGroupPersistDTO) persistDTO;
            List<KnMemberDTO> addedList = dynamicGrpPerDto.getAddedMemberDetailList();
            subsProfile = dynamicGrpPerDto.getSubsProfile();
            if (addedList != null) {
                for (KnMemberDTO member : addedList) {
                    if (member.getSubscriptionType() == 0) {
                        pubSubsList.add(member.getMemberMdn());
                    }
                }
            }

        } else {
            knLogger.error(methodName, "Unexpected DTO passed - " + persistDTO.getClass() +
                    ", Expected Dto - KnDynamicContactPersistDTO ");
            throw new KnPubBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), KnConstants.KEY_DATATYPE_MDN, "");
        }

        if (subsProfile.getPublicSubscriptionType() != 1 || subsProfile.getCorpSubscriptionType() != 1) {
            knLogger.error(methodName, "Owner MDN is not C&P subs ");
            throw new KnPubBOValidationException(KnErrorCodes.Validator.OWNERMDN_NOT_CORP_PUB_SUBS,
                    "Owner MDN is not C&P subs " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), KnConstants.KEY_DATATYPE_MDN, subsProfile.getMdn());
        }

        if (!pubSubsList.isEmpty()) {
            knLogger.error(methodName, "Contact MDN are of public type - ", KnGDPRTemplate.mdnList(pubSubsList));
            throw new KnPubBOValidationException(KnErrorCodes.Validator.CONTACT_MDN_PUB_SUBS,
                    "Contact MDN are of public type - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), KnConstants.KEY_DATATYPE_MDN, pubSubsList.toString());
        }
    }
}
