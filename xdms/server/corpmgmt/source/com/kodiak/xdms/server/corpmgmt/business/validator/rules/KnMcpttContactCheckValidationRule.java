/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

import com.kodiak.common.resources.KnConstants;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCloningPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpMcpttFeaturePersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.Arrays;
import java.util.Collection;

/**
 * ************************************************************************
 * <p>
 * File name:  KnMcpttContactCheckValidationRule.java
 * Subsystem:  PoC
 * <p>
 * Name                         Date                     Release
 * --------------------     ----------------        ------------------
 * Saurabh Kumar             Dec 07, 2017                9.0
 * <p>
 * <p>
 * KODIAK, 9th Floor, 'MFar Manyata Tech Park'
 * Greenheart Phase IV, Nagawara
 * Bangalore - 560 045
 * www.kodiakptt.com
 * All Rights Reserved.
 * <p>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */

public class KnMcpttContactCheckValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnMcpttContactCheckValidationRule.class);

    @Override
    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY Point");
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug(methodName, "persistDTO obtained from the request is - ", persistDTO);
        if (persistDTO instanceof KnCorpMcpttFeaturePersistDTO) {
            KnCorpMcpttFeaturePersistDTO authUserListPersistDTO = (KnCorpMcpttFeaturePersistDTO) persistDTO;
            Collection<String> targetNotInContactListOfAuthMdn = authUserListPersistDTO.getTargetNotInContactListOfAuthMdn();
            knLogger.debug(methodName, "targetNotInContactListOfAuthMdn - ", targetNotInContactListOfAuthMdn);
            Collection<String> targetMdnDoNotHaveAuthMdnAsContact = authUserListPersistDTO.getTargetMdnDoNotHaveAuthMdnAsContact();
            knLogger.debug(methodName, "targetMdnDoNotHaveAuthMdnAsContact - ", targetMdnDoNotHaveAuthMdnAsContact);
            if (targetNotInContactListOfAuthMdn != null && !targetNotInContactListOfAuthMdn.isEmpty()) {
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.AUTH_MDN_DOES_NOT_HAVE_TARGET_MDN_AS_CONTACT,
                        "target MDN Not present In Contact List Of Auth Mdn --", getEntityId(),
                        getOperationType(), getRuleId(), Arrays.asList(targetNotInContactListOfAuthMdn).toString(), "");
            }
            if (targetMdnDoNotHaveAuthMdnAsContact != null && !targetMdnDoNotHaveAuthMdnAsContact.isEmpty()) {
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.TARGET_MDN_DOES_NOT_HAVE_AUTH_MDN_AS_CONTACT,
                        "target Mdn Do Not Have Auth Mdn As Contact --", getEntityId(),
                        getOperationType(), getRuleId(), Arrays.asList(targetMdnDoNotHaveAuthMdnAsContact).toString(), "");
            }
        } else if (persistDTO instanceof KnCloningPersistDTO) {
            KnCloningPersistDTO cloningPersistDTO = (KnCloningPersistDTO) persistDTO;
            if (cloningPersistDTO.getValidationMap().containsKey(KnConstants.CLONING_VALIDATION_BIT.PERMISSION.value()) && cloningPersistDTO.getValidationMap().get(KnConstants.CLONING_VALIDATION_BIT.PERMISSION.value())) {
                KnCorpMcpttFeaturePersistDTO authUserListPersistDTO = ((KnCloningPersistDTO) persistDTO).getAuthUserListPersistDTO();
                Collection<String> targetNotInContactListOfAuthMdn = authUserListPersistDTO.getTargetNotInContactListOfAuthMdn();
                knLogger.debug(methodName, "targetNotInContactListOfAuthMdn - ", targetNotInContactListOfAuthMdn);
                Collection<String> targetMdnDoNotHaveAuthMdnAsContact = authUserListPersistDTO.getTargetMdnDoNotHaveAuthMdnAsContact();
                knLogger.debug(methodName, "targetMdnDoNotHaveAuthMdnAsContact - ", targetMdnDoNotHaveAuthMdnAsContact);
                if (targetNotInContactListOfAuthMdn != null && !targetNotInContactListOfAuthMdn.isEmpty()) {
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.AUTH_MDN_DOES_NOT_HAVE_TARGET_MDN_AS_CONTACT,
                            "target MDN Not present In Contact List Of Auth Mdn --", getEntityId(),
                            getOperationType(), getRuleId(), Arrays.asList(targetNotInContactListOfAuthMdn).toString(), "");
                }
                if (targetMdnDoNotHaveAuthMdnAsContact != null && !targetMdnDoNotHaveAuthMdnAsContact.isEmpty()) {
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.TARGET_MDN_DOES_NOT_HAVE_AUTH_MDN_AS_CONTACT,
                            "target Mdn Do Not Have Auth Mdn As Contact --", getEntityId(),
                            getOperationType(), getRuleId(), Arrays.asList(targetMdnDoNotHaveAuthMdnAsContact).toString(), "");
                }
            }
        }else {
            knLogger.error("validate", "Unexpected DTO passed - ", persistDTO.getClass(),
                    ", Expected Dto - KnCorpGroupInfoPersistDTO ");
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR, "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), "DataType", "");
        }
        knLogger.debug(methodName, "EXIT: Validation Completed Successfully for MCPTT Contact existence");
    }
}
