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
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpUserProfilePersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.Arrays;

public class KnEmergencyAttributeCheckValidation extends KnValidatorRule {

    private static final KnLogger knLogger = KnLogger.getLogger(KnEmergencyAttributeCheckValidation.class);

    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName = "validate()";
        IPersistenceDTO persistDTO = getDTO();
        if (persistDTO instanceof KnCorpUserProfilePersistDTO) {
            KnCorpUserProfilePersistDTO dto = (KnCorpUserProfilePersistDTO) persistDTO;
            if (dto.isPrimaryDestinationIsEmpty()) {
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.PRIMARY_DESTINATION_IS_EMPTY,
                        "primary destination must be set for destType 2 --", getEntityId(),
                        getOperationType(), getRuleId(), Arrays.asList("").toString(), "");
            } else if (dto.isDestIsBroadCastGroup()) {
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.BROADCAST_GROUP_NOT_ALLOWED_AS_DESTINATIONS,
                        "Broadcast group not allowed for destination --", getEntityId(),
                        getOperationType(), getRuleId(), "", "");
            } else if (dto.isGroupDoesNotBelogsToUPM()) {
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.GROUP_DOES_NOT_EXIST,
                        "Group does not exists --", getEntityId(),
                        getOperationType(), getRuleId(), "", "");
            } else if (dto.isSublistDoesNotBelogsToUPM()) {
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.DESTINATION_NOT_BELONGS_TO_SUBS_CONTACT_LIST,
                        "Destination does not belongs to subs contact list --", getEntityId(),
                        getOperationType(), getRuleId(), "", "");
            }
        }else {
            knLogger.error("validate", "Unexpected DTO passed - ", persistDTO.getClass(),
                    ", Expected Dto - KnCorpGroupInfoPersistDTO ");
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR, "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), "DataType", "");
        }
        knLogger.debug(methodName, "EXIT: Validation Completed Successfully for emergency Attributes");
    }
}
