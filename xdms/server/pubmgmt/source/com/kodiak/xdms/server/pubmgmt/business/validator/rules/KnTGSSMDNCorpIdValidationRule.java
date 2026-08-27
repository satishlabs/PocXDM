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

import java.util.Map;
import java.util.Objects;

import static com.kodiak.xdms.server.pubmgmt.resources.KnErrorCodes.Validator.MDNS_DOESNOT_BELONG_TO_SAME_CORP;

/**
 * Created by venkata sudhakar talluri on 10-01-2019
 */


public class KnTGSSMDNCorpIdValidationRule extends KnValidatorRule {

    //- validate corpid belong to MDN


    private static final KnLogger knLogger = KnLogger.getLogger(KnTGSSMDNCorpIdValidationRule.class);

    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName = "validate()";
        IPersistenceDTO persistDTO = getDTO();
        IInputDTO inputDTO = getInputDTO();
        KnTGSSListPersistDTO knTGSSListPersistDTO = null;
        KnIPTGSSListDTO knIPTGSSListDTO = null;


        knLogger.debug(methodName, "ENTRY : Validating mdn doesnot belong to same corp" + persistDTO);
        try {
            if (persistDTO instanceof KnTGSSListPersistDTO) {
                knTGSSListPersistDTO = (KnTGSSListPersistDTO)persistDTO;
            }else {
                throw new KnPubBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR, "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(), getEntityId(), getOperationType(), getRuleId(), "MDN", "");
            }
            if(inputDTO instanceof KnIPTGSSListDTO){
                knIPTGSSListDTO = (KnIPTGSSListDTO)inputDTO;
            }else{
                throw new KnPubBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR, "Unexpected instance of Input DTO passed - " + inputDTO.getClass(), getEntityId(), getOperationType(), getRuleId(), "MDN", "");
            }

            if (knIPTGSSListDTO.getCorpId() != knTGSSListPersistDTO.getCorpId()) {
                if (!(null != knTGSSListPersistDTO.getSharedCorpList() && knTGSSListPersistDTO.getSharedCorpList().contains(knTGSSListPersistDTO.getCorpId()))) {
                    //if the corpId is in the shared corpList then skip the error
                    knLogger.error(methodName, "Input mdn::", KnGDPRTemplate.mdn(knIPTGSSListDTO.getMdn()) , " doesn't belong to same corp as provided in input::" , knIPTGSSListDTO.getCorpId());
                    throw new KnPubBOValidationException(KnErrorCodes.Validator.MDNS_DOESNOT_BELONG_TO_SAME_CORP, "Input Mdn doesn't belongs to same corp- " + inputDTO.getClass(), getEntityId(), getOperationType(), getRuleId(), "MDN", "");
                }
            }

        } finally {
            knLogger.debug(methodName, "Exit Point");
        }
    }
}
