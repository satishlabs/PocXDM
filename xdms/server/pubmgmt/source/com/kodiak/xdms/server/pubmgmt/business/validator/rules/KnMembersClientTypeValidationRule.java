/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnMembersClientTypeValidationRule.java
 * Subsystem:  pubmgmt
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * Chandrashekar H S     June, 2015           8.0
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

import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.pubmgmt.business.validator.KnPubBOValidationException;
import com.kodiak.xdms.server.pubmgmt.dto.common.KnMemberDTO;
import com.kodiak.xdms.server.pubmgmt.dto.persistdat.KnDynamicContactPersistDTO;
import com.kodiak.xdms.server.pubmgmt.dto.persistdat.KnDynamicGroupPersistDTO;
import com.kodiak.xdms.server.pubmgmt.dto.persistdat.KnPubContactInfoPersistDTO;
import com.kodiak.xdms.server.pubmgmt.dto.persistdat.KnPubGroupInfoPersistDTO;
import com.kodiak.xdms.server.pubmgmt.resources.KnConstants;
import com.kodiak.xdms.server.pubmgmt.resources.KnErrorCodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.kodiak.xdms.server.pubmgmt.resources.KnConstants.KEY_DATATYPE_MDN;

public class KnMembersClientTypeValidationRule extends KnValidatorRule {

    private static final KnLogger knLogger = KnLogger.getLogger(KnMembersClientTypeValidationRule.class);
    private static final String NON_ALLOWED_TYPES = "nonAllowedTypes";

    public void validate() throws KnValidationException {

        final String methodName = "validate()";
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug( methodName, " Validating Members client type is not in the allowed type " , persistDTO);
        String nonAllowedClientTypes = getAttribute(NON_ALLOWED_TYPES);
        try {
        knLogger.debug(methodName, "nonAllowedClientTypes - ", nonAllowedClientTypes);
        String[] subTypes = nonAllowedClientTypes.split(DELIM);
        Collection<String> clntTypeList = Arrays.asList(subTypes);
        if(persistDTO instanceof KnDynamicContactPersistDTO){
            KnDynamicContactPersistDTO dynamicContactPersistDTO = (KnDynamicContactPersistDTO) persistDTO;
            List<KnMemberDTO> addedList = dynamicContactPersistDTO.getAddedContactDetailList();
            List<String> invalidMdnList = new ArrayList<>();
            for(KnMemberDTO member : addedList){
                if(clntTypeList.contains(member.getClientType())){
                    invalidMdnList.add(member.getMemberMdn());
                }
            }
            if(!invalidMdnList.isEmpty()){
                knLogger.error(methodName, "Contact MDN are of invalid client type - ", KnGDPRTemplate.mdnList(invalidMdnList));
                throw new KnPubBOValidationException(KnErrorCodes.Validator.CONTACT_MDN_CLIENT_TYPE_INVALID,
                        "Contact MDN are of invalid client type - " + persistDTO.getClass(),
                        getEntityId(), getOperationType(), getRuleId(), KnConstants.KEY_DATATYPE_MDN, invalidMdnList.toString());
            }
        } else if(persistDTO instanceof KnDynamicGroupPersistDTO){
                KnDynamicGroupPersistDTO dynamicGrpPersistDTO = (KnDynamicGroupPersistDTO) persistDTO;
                List<KnMemberDTO> addedList = dynamicGrpPersistDTO.getAddedMemberDetailList();
                List<String> invalidMdnList = new ArrayList<>();
                for(KnMemberDTO member : addedList){
                    if(clntTypeList.contains(member.getClientType())){
                        invalidMdnList.add(member.getMemberMdn());
                    }
                }
                if(!invalidMdnList.isEmpty()){
                    knLogger.error(methodName, "Contact MDN are of invalid client type - ", KnGDPRTemplate.mdnList(invalidMdnList));
                    throw new KnPubBOValidationException(KnErrorCodes.Validator.CONTACT_MDN_CLIENT_TYPE_INVALID,
                            "Contact MDN are of invalid client type - " + persistDTO.getClass(),
                            getEntityId(), getOperationType(), getRuleId(), KnConstants.KEY_DATATYPE_MDN, invalidMdnList.toString());
                }
            }else if (persistDTO instanceof KnPubContactInfoPersistDTO ){
            KnPubContactInfoPersistDTO pubContactInfoPersistDTO = (KnPubContactInfoPersistDTO) persistDTO;
            knLogger.debug(methodName, "DTO passed in the request is - ", pubContactInfoPersistDTO);
            knLogger.debug(methodName, "Validating the clientType of the contact members.");
            Collection<String> invalidMdns = new ArrayList<String>();
            for(KnMemberDTO memberDTO : pubContactInfoPersistDTO.getPoCMembers()){
                if(clntTypeList.contains(memberDTO.getClientType())){
                    knLogger.debug(methodName, "The Contact MDN cannot be SU MDN or SG MDN", KnGDPRTemplate.mdn(memberDTO.getMemberMdn()));
                    throw new KnPubBOValidationException(KnErrorCodes.Validator.MEMBER_CLIENT_TYPE_NOT_ALLOWED, "Member Client Type not allowed - " + persistDTO.getClass(), getEntityId(), getOperationType(), getRuleId(), KEY_DATATYPE_MDN , memberDTO.getMemberMdn());
                }
            }

        } else if( persistDTO instanceof KnPubGroupInfoPersistDTO){
            KnPubGroupInfoPersistDTO pubContactInfoPersistDTO = (KnPubGroupInfoPersistDTO) persistDTO;
            knLogger.debug(methodName, "DTO passed in the request is - ", pubContactInfoPersistDTO);
            knLogger.debug(methodName, "Validating the clientType of group members.");
            for(KnMemberDTO memberDTO : pubContactInfoPersistDTO.getPoCMembers()){
                if(clntTypeList.contains(memberDTO.getClientType())){
                    knLogger.debug(methodName, "The member MDN cannot be SU MDN or SG MDN", KnGDPRTemplate.mdn(memberDTO.getMemberMdn()));
                    throw new KnPubBOValidationException(KnErrorCodes.Validator.MEMBER_CLIENT_TYPE_NOT_ALLOWED, "Member Client Type not allowed - " + persistDTO.getClass(), getEntityId(), getOperationType(), getRuleId(), KEY_DATATYPE_MDN , memberDTO.getMemberMdn());
                }
            }
        }
        else {
            knLogger.error("validate", "Unexpected DTO passed - " , persistDTO.getClass(), ", Expected Dto - KnPubContactInfoPersistDTO || KnPubContactInfoPersistDTO ");
            throw new KnPubBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR, "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(), getEntityId(), getOperationType(), getRuleId(), KEY_DATATYPE_MDN, "");
        }

        }finally {
            knLogger.debug(methodName, "Exit Point");
        }

    }
}
