/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.business.validator.rules;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.dto.persistdat.KnSubscriberPersistDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.pubmgmt.business.validator.KnPubBOValidationException;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnIPMCSDTO;
import com.kodiak.xdms.server.pubmgmt.dto.persistdat.KnPubMCSXCAPPersistDTO;
import com.kodiak.xdms.server.pubmgmt.resources.KnErrorCodes;

import static com.kodiak.xdms.server.pubmgmt.resources.KnConstants.KEY_DATATYPE_MDN;

import com.kodiak.common.resources.KnGDPRTemplate;

public class KnServiceAuthStatusValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnServiceAuthStatusValidationRule.class);



    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName="validate()";
        final int DEACTIVATED=3;

        IPersistenceDTO persistDTO = getDTO();
        KnSubscriberPersistDTO knSubscriberPersistDTO = null;
        KnIPMCSDTO knIPMCSDTO = null;
        knLogger.debug(methodName," ENTRY persistDTO :", persistDTO);
        KnPubMCSXCAPPersistDTO pubMCSXCAPPersistDTO = null;
        if (persistDTO instanceof KnPubMCSXCAPPersistDTO) {
            pubMCSXCAPPersistDTO = (KnPubMCSXCAPPersistDTO) persistDTO;
            knSubscriberPersistDTO = (KnSubscriberPersistDTO) pubMCSXCAPPersistDTO.getPersistenceDTO();
            int serviceAuthStatus = knSubscriberPersistDTO.getServiceAuthStatus();
            knLogger.debug(methodName," serviceAuthStatus :",serviceAuthStatus);

            if(serviceAuthStatus==DEACTIVATED){
                knLogger.error(methodName," deactivated subscriber :",KnGDPRTemplate.mdn(pubMCSXCAPPersistDTO.getMdn()));
                throw new KnPubBOValidationException(KnErrorCodes.Validator.INVALID_AUTH_SATUS,
                        "Validation Rule Failed , subscriber is deactivated ",
                        getOperationType(), getRuleId(), KEY_DATATYPE_MDN, pubMCSXCAPPersistDTO.getMdn());

            }
        } else {
            knLogger.error("validate", "Unexpected DTO passed - " + persistDTO.getClass() +
                    ", Expected Dto - KnPubMCSXCAPPersistDTO ");
            throw new KnPubBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), KEY_DATATYPE_MDN, pubMCSXCAPPersistDTO.getMdn());
        }
    }
}
