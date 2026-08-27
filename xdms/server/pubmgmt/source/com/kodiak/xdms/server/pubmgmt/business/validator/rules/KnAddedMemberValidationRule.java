/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.business.validator.rules;

import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.pubmgmt.business.validator.KnPubBOValidationException;
import com.kodiak.xdms.server.pubmgmt.dto.persistdat.KnDynamicGroupPersistDTO;
import com.kodiak.xdms.server.pubmgmt.resources.KnConstants;
import com.kodiak.xdms.server.pubmgmt.resources.KnErrorCodes;

import java.util.ArrayList;
import java.util.List;

public class KnAddedMemberValidationRule extends KnValidatorRule {

    private static final KnLogger knLogger = KnLogger.getLogger(KnAddedMemberValidationRule.class);
    /**
     * validate method. all the validators should implement this method
     *
     * @throws KnValidationException
     */
    @Override
    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug(methodName, "ENTRY :");
        List<String> invalidMdnList = new ArrayList<>();
        if (persistDTO instanceof KnDynamicGroupPersistDTO) {
            KnDynamicGroupPersistDTO dynamicGroupPersistDTO = (KnDynamicGroupPersistDTO) persistDTO;
            if(dynamicGroupPersistDTO.getExistingMembers() != null){
                List<String> existingList = new ArrayList<>();
                dynamicGroupPersistDTO.getExistingMembers().forEach((o)-> {
                    existingList.add(o.getMemberMdn());
                });
                if(dynamicGroupPersistDTO.getAddedMemberDetailList() != null){
                    dynamicGroupPersistDTO.getAddedMemberDetailList().forEach((o) ->{
                        if(existingList.contains(o.getMemberMdn())){
                            invalidMdnList.add(o.getMemberMdn());
                        }
                    });
                }
            }
        } else{
            knLogger.error(methodName, "Unexpected DTO passed - " + persistDTO.getClass() +
                    ", Expected Dto - KnDynamicGroupPersistDTO ");
            throw new KnPubBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), KnConstants.KEY_DATATYPE_MDN, "");
        }
        if (!invalidMdnList.isEmpty()) {
            knLogger.error(methodName, "Adding member already exist in the group - ", KnGDPRTemplate.mdnList(invalidMdnList));
            throw new KnPubBOValidationException(KnErrorCodes.Validator.ADDED_GROUP_MEMBER_ALREADY_EXIST,
                    "Adding member already exist in the group - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), KnConstants.KEY_DATATYPE_MDN, invalidMdnList.toString());
        }
    }
}