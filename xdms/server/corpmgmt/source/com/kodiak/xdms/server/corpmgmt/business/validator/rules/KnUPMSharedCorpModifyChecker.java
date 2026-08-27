/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

import com.kodiak.common.resources.KnConstants;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpModifyUserProfileDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpUserProfilePersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.BitSet;
import java.util.Map;

public class KnUPMSharedCorpModifyChecker extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnUPMexternalMemberPermChecker.class);

    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY.");
        IPersistenceDTO persistDTO = getDTO();
        if (persistDTO instanceof KnCorpUserProfilePersistDTO) {
            KnCorpUserProfilePersistDTO userProfilePersistDTO = (KnCorpUserProfilePersistDTO) persistDTO;
            KnCorpModifyUserProfileDTO modifyUPMObj = userProfilePersistDTO.getModifyUserProfileDTO();
            if(!userProfilePersistDTO.isOwnerCorpReq()){
                //shared corp cannot modify user profile
                if(modifyUPMObj != null){
                    if(modifyUPMObj.getUserProfileName() != null || (modifyUPMObj.getContactListID()!= null && modifyUPMObj.getContactListID() != -1)
                            || modifyUPMObj.getTgscMode() != null || modifyUPMObj.getEmergencyConfig() != null ||
                            modifyUPMObj.getAddedGroupList() != null || modifyUPMObj.getModifiedGroupList() != null
                            ||modifyUPMObj.getRemovedGroupIdsList()!=null || modifyUPMObj.getAddedMcpttPermissionsConfig() !=null ||
                            modifyUPMObj.getModifiedPermissionsConfig() != null || modifyUPMObj.getRemovedMcpttPermissionsConfig() != null){
                        //shared corp is not allowed to modify the profile
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.USER_PROFILE_MODIFY_NOT_ALLOWED_FOR_SHARED
                                , "shared profile is not allowed to be modified by the profile"
                                , getEntityId(), getOperationType(), getRuleId(), "", "");
                    }
                }
            }

        }
    }
}
