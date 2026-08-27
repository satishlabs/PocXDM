/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.rules;


import java.util.Arrays;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpTGSPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import static com.kodiak.xdms.server.corpmgmt.resources.KnOperationTypes.CREATE_SUBS_ATG_SCAN_LIST;

public class KnSysTGSBitValidation extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnSysTGSBitValidation.class);
    private static final long serialVersionUID = 7526471155622234233L;

    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY Point");
        IPersistenceDTO persistDTO = getDTO();
        if (persistDTO instanceof KnCorpTGSPersistDTO) {
            KnCorpTGSPersistDTO tgsPersistDTO = (KnCorpTGSPersistDTO) persistDTO;
            knLogger.debug(methodName, "tgsPersistDTO.getOperationType()", tgsPersistDTO.getOperationType(), tgsPersistDTO.isPriorityExists());
            if(tgsPersistDTO.isPriorityExists() || !CREATE_SUBS_ATG_SCAN_LIST.equals(tgsPersistDTO.getOperationType())){
                boolean isSysTGSEnable = tgsPersistDTO.isSysTGSBitEanble();
                if(!isSysTGSEnable){
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.SYSTEM_TGS_FEATURE_DISABLE,
                            "System wide TGS feature bit disabled", getEntityId(), getOperationType(), getRuleId(),
                            Arrays.asList(0).toString(),"");
                }
            }
        } else{
            knLogger.error("validate", "Unexpected DTO passed - ", persistDTO.getClass(),
                    ", Expected Dto - KnCorpTGSPersistDTO ");
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), "DataType", "");
        }
        knLogger.debug( methodName, "Validation Completed Successfully");
    }
}
