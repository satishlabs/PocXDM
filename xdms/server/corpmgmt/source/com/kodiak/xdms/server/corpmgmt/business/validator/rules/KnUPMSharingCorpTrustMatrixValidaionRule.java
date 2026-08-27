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

import java.util.ArrayList;
import java.util.List;

public class KnUPMSharingCorpTrustMatrixValidaionRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnUPMSharingCorpTrustMatrixValidaionRule.class);

    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName = "validate()";

        IPersistenceDTO persistDTO = getDTO();
        if (persistDTO instanceof KnCorpUserProfilePersistDTO) {
            KnCorpUserProfilePersistDTO userProfilePersistDTO = (KnCorpUserProfilePersistDTO) persistDTO;

            List<String> reqUpmSharedCorpList=userProfilePersistDTO.getReqUserProfileSharedCorpList();
            List<String> dbUpmSharedCorpList = userProfilePersistDTO.getDbUserProfileSharedCorpList();
            knLogger.debug(methodName, "ENTRY reqUpmSharedCorpList:",reqUpmSharedCorpList," dbUpmSharedCorpList:",dbUpmSharedCorpList);

            if(reqUpmSharedCorpList!=null&&!reqUpmSharedCorpList.isEmpty()){
                List<String> notAllowedCorpInReq = new ArrayList<>(reqUpmSharedCorpList);
                notAllowedCorpInReq.removeAll(dbUpmSharedCorpList);
                if(!notAllowedCorpInReq.isEmpty() ){
                    knLogger.error(methodName, "UPM sharing for corpId not allowed");
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.USER_PROFILE_SHARING_VALID_TRUSTMATRIX
                            , "UPM sharing for corpId not allowed", getEntityId(), getOperationType(), getRuleId(), "", notAllowedCorpInReq.toString());
                }
            }

        }
    }
}
