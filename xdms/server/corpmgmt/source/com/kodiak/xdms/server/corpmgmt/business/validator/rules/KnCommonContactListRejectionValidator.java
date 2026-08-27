/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpGroupInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.Collection;

public class KnCommonContactListRejectionValidator extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnCommonContactListRejectionValidator.class);
    /**
     * validate method. all the validators should implement this method
     *
     * @throws KnValidationException
     * @throws KnBOException
     */

    @Override
    public void validate() throws KnValidationException, KnBOException{
        final String methodName = "validates()";
        knLogger.debug(methodName, "ENTRY.");
        IPersistenceDTO persistDTO = getDTO();
        if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
            knLogger.debug(methodName, "Bean is instance of KnCorpGroupInfoPersistDTO");
            KnCorpGroupInfoPersistDTO corpPersistDTO = (KnCorpGroupInfoPersistDTO) persistDTO;

            if(corpPersistDTO.getInputDTO() instanceof KnIPCorpGroupInfoDTO){
                Collection<Integer> rejectCCList = corpPersistDTO.getCommonContactRejectList();

                knLogger.debug(methodName, "", " Common Contact list in createGroupPath ",rejectCCList);
                if(!corpPersistDTO.getCommonContactRejectList().isEmpty())
                {
                    knLogger.error( methodName, "validation failed ");
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.COMMON_CONTACT_LIST_REJECTION,
                            "validation failed as common contact list can't be assigned to create/modify group", getEntityId(), getOperationType(), getRuleId());
                }
            }

        }else{
            knLogger.error( "validate()", "Unexpected DTO passed - " , persistDTO.getClass() ,
                    ", Expected Dto - KnCorpGroupInfoPersistDTO");
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), "DataType", "");
        }
    }
}
