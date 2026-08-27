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
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPUserProfileDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpUserProfilePersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.Collection;

public class KnUPMCommonContactListRejectionValidator extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnUPMCommonContactListRejectionValidator.class);

    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName = "validates()";
        knLogger.debug(methodName, "ENTRY.");
        IPersistenceDTO persistDTO = getDTO();
        if (persistDTO instanceof KnCorpUserProfilePersistDTO) {
            KnCorpUserProfilePersistDTO corpUPMPersistDTO=(KnCorpUserProfilePersistDTO)persistDTO;
                int rejectCCLId = corpUPMPersistDTO.getCommonContactListRejectionId();
                knLogger.debug(methodName, " Common Contact list in UPM Path ",rejectCCLId);
                if(corpUPMPersistDTO.getCommonContactListRejectionId() != 0)
                {
                    knLogger.error( methodName, "validation failed ");
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.COMMON_CONTACT_LIST_REJECTION,
                            "validation failed as common contact list can't be assigned to create/modify UPM", getEntityId(), getOperationType(), getRuleId());
                }
        }else{
            knLogger.error( "validate()", "Unexpected DTO passed - " , persistDTO.getClass() ,
                    ", Expected Dto - KnCorpUserProfilePersistDTO");
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), "DataType", "");
        }
    }
}
