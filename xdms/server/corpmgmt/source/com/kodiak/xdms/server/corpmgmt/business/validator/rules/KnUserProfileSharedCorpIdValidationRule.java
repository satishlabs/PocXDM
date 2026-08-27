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
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPUserProfileDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpUserProfileDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpUserProfilePersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.List;

public class KnUserProfileSharedCorpIdValidationRule extends KnValidatorRule {

    private static final KnLogger knLogger = KnLogger.getLogger(KnUserProfileSharedCorpIdValidationRule.class);

    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName = "validate():userProfileIsShared";
        IPersistenceDTO persistDTO = getDTO();
        if (persistDTO instanceof KnCorpUserProfilePersistDTO) {

            KnCorpUserProfilePersistDTO userProfilePersistDTO = (KnCorpUserProfilePersistDTO) persistDTO;
            KnCorpUserProfileDTO userProfileDTO = userProfilePersistDTO.getUserProfileDTO();
            if (userProfileDTO == null) {
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.UP_NOT_EXIST_DB,
                        "userProfile not exists in db", getEntityId()
                        , getOperationType(), getRuleId(), userProfilePersistDTO.getUserProfileId(), "");
            }
            Integer ownerCorpId = userProfileDTO.getCorporateID();
            KnIPUserProfileDTO inUserProfile = (KnIPUserProfileDTO) userProfilePersistDTO.getInputDTO();
            knLogger.debug(methodName, " inUserProfile :- " + inUserProfile);
            if (ownerCorpId != null && inUserProfile.getCorpId().equalsIgnoreCase(String.valueOf(ownerCorpId))) {
                knLogger.debug(methodName, " Shared Corp Id validation is success");
                return;

            }
            List<String> dbUpmSharedCorpList = userProfilePersistDTO.getDbUserProfileSharedCorpList();
            knLogger.debug(methodName, "ENTRY dbUpmSharedCorpList:", dbUpmSharedCorpList);

            if (dbUpmSharedCorpList == null || !dbUpmSharedCorpList.contains(inUserProfile.getCorpId())) {
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.USER_PROFILE_SHARING_CORP_ID_NOT_FOUND,
                        "No owner or shared corpId found for this user profile. ", getEntityId()
                        , getOperationType(), getRuleId(), userProfilePersistDTO.getUserProfileId(), "");
            }
            knLogger.debug(methodName, "Exit : KnUserProfileSharedCorpIdValidationRule - success");
        } else {
            knLogger.error(methodName, "Unexpected DTO passed - ", persistDTO.getClass(),
                    ", Expected Dto - KnCorpUserProfilePersistDTO ");
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), "DataType", "");
        }
    }
}
