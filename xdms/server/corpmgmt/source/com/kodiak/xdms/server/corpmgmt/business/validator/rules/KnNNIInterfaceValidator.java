/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnPoCLinkedGroupListPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

/**
 * Created by nnamita on 4/14/15.
 */
public class KnNNIInterfaceValidator extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnNNIInterfaceValidator.class);
    private static final long serialVersionUID = 7526471155622676325L;

    @Override
    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY Point");
        IPersistenceDTO persistDTO = getDTO();
        if (persistDTO instanceof KnPoCLinkedGroupListPersistDTO) {
            KnPoCLinkedGroupListPersistDTO pocLinkedOersistDTO = (KnPoCLinkedGroupListPersistDTO) persistDTO;
            if(pocLinkedOersistDTO.isPocIntfEnabled()){
                knLogger.error(methodName, "The operation  allowed as the nni gateway interface is enabled.");
            }else{
                knLogger.error(methodName, "The operation is not allowed as the nni gateway interface is disabled.");
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.
                        OPERATION_NOT_ALLOWED_FOR_NOT_LINKED_CORPORATE, "Operation not allowed when the nni gateway interface is disabled.",
                        getEntityId(), getOperationType(), getRuleId(), "Business validation","");
            }
        } else{
            knLogger.error(methodName, "Unexpected DTO passed - ", persistDTO.getClass(),
                    ", Expected Dto - KnPoCLinkedGroupListPersistDTO ");
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), "DataType", "");
        }
        knLogger.debug( methodName, "Validation Completed Successfully");

    }
}
