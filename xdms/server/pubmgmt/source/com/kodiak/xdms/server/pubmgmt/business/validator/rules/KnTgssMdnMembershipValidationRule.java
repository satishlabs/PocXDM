/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.business.validator.rules;

import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.pubmgmt.business.validator.KnPubBOValidationException;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnIPTGSSListDTO;
import com.kodiak.xdms.server.pubmgmt.dto.persistdat.KnTGSSListPersistDTO;
import com.kodiak.xdms.server.pubmgmt.resources.KnErrorCodes;

public class KnTgssMdnMembershipValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnTGSSMDNCorpIdValidationRule.class);

    @Override
    public void validate() throws KnValidationException, KnBOException {

        final String methodName = "validate()";
        IPersistenceDTO persistDTO = getDTO();
        IInputDTO inputDTO = getInputDTO();
        KnTGSSListPersistDTO knTGSSListPersistDTO = null;
        KnIPTGSSListDTO knIPTGSSListDTO = null;


        knLogger.debug(methodName, "ENTRY : Validating mdn membership to the group ", persistDTO);
        try {
            if (persistDTO instanceof KnTGSSListPersistDTO) {
                knTGSSListPersistDTO = (KnTGSSListPersistDTO) persistDTO;
            } else {
                throw new KnPubBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR, "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(), getEntityId(), getOperationType(), getRuleId(), "MDN", "");
            }
            if (inputDTO instanceof KnIPTGSSListDTO) {
                knIPTGSSListDTO = (KnIPTGSSListDTO) inputDTO;
            } else {
                throw new KnPubBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR, "Unexpected instance of Input DTO passed - " + inputDTO.getClass(), getEntityId(), getOperationType(), getRuleId(), "MDN", "");
            }
            if (!(knTGSSListPersistDTO.getMemberList().contains(knIPTGSSListDTO.getMdn()))) {
                //if the corpId is in the shared corpList then skip the error
                knLogger.error(methodName, "Input mdn " + KnGDPRTemplate.mdn(knIPTGSSListDTO.getMdn()) + " is not present in the memberList table ", knTGSSListPersistDTO.getMemberList());
                throw new KnPubBOValidationException(KnErrorCodes.Validator.MDN_IS_NOT_PART_OF_THE_GROUP, " Input mdn is not present in the memberList table " + inputDTO.getClass(), getEntityId(), getOperationType(), getRuleId(), "MDN", "");
            }

        } finally {
            knLogger.debug(methodName, "Exit Point");
        }
    }
}
