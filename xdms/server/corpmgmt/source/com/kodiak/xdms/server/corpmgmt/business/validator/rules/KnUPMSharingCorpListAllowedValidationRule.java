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
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpUserProfilePersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.List;

public class KnUPMSharingCorpListAllowedValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnUPMSharingCorpListAllowedValidationRule.class);

    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName = "validate()";
        IPersistenceDTO persistDTO = getDTO();
        if (persistDTO instanceof KnCorpUserProfilePersistDTO) {
            KnCorpUserProfilePersistDTO userProfilePersistDTO = (KnCorpUserProfilePersistDTO) persistDTO;
            List<String> reqUPMSharedCorpList = userProfilePersistDTO.getReqUserProfileSharedCorpList();
            String reqUpmSharingFlag = userProfilePersistDTO.getReqUPMSharingFlag();
            String dbUpmSharing = userProfilePersistDTO.getDbUPMSharingFlag();
            knLogger.debug(methodName, "ENTRY reqUPMSharedCorpList:",reqUPMSharedCorpList
                    ," reqUpmSharingFlag :",reqUpmSharingFlag," dbUpmSharing :",dbUpmSharing);
            if(reqUPMSharedCorpList!=null&&!reqUPMSharedCorpList.isEmpty()
                    &&(reqUpmSharingFlag.equals(String.valueOf(KnConstants.DISABLED))||dbUpmSharing.equals(String.valueOf(KnConstants.DISABLED)))){
                knLogger.error(methodName, "UPM sharing is disabled,not allowed for the corp list");
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.USER_PROFILE_SHARING_CORP_NOT_ALLOWED
                        ,"UPM sharing is disabled,not allowed for the corp list", getEntityId(), getOperationType(), getRuleId(), "", "");
            }

        }
    }
}
