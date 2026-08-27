/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.rules.groupprofile;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpBulkGroupPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupProfilePersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

public class KnCorpGrpProfileFlagValidator extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnCorpGrpProfileFlagValidator.class);

    /**
     * validate method. all the validators should implement this method
     *
     * @throws KnValidationException
     * @throws KnBOException
     */
    private static final String GRPROMGMTENABLED="1";
    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY.");
        IPersistenceDTO persistDTO = getDTO();
        if (persistDTO instanceof KnCorpBulkGroupPersistDTO) {
            KnCorpBulkGroupPersistDTO grpGersistDTO = (KnCorpBulkGroupPersistDTO) persistDTO;
            knLogger.debug(methodName, "Flag - ", grpGersistDTO.getCorpProfile().getGroupProfileMgmt());
            if (!GRPROMGMTENABLED.equals( ""+grpGersistDTO.getCorpProfile().getGroupProfileMgmt()) &&
            		!GRPROMGMTENABLED.equals( grpGersistDTO.getParamNameValueMapCommon().get(KnConstants.GROUP_PROFILE_MGMT))) {
                knLogger.error(methodName, "Group Profile Management flag diabled ");
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.GROUP_PROFILE_FEATURE_NOT_ALLOWED,
                        "Group profile feature disabled.", getEntityId(), getOperationType(), getRuleId(), "", "");

            }
        }
        
        if (persistDTO instanceof KnCorpGroupProfilePersistDTO) {
        	KnCorpGroupProfilePersistDTO grpGersistDTO = (KnCorpGroupProfilePersistDTO) persistDTO;
            knLogger.debug(methodName, "Flag - ","corp level", grpGersistDTO.getCorpProfile().getGroupProfileMgmt(),"system level",grpGersistDTO.getParamNameValueMapCommon().get(KnConstants.GROUP_PROFILE_MGMT));
            if (!GRPROMGMTENABLED.equals( ""+grpGersistDTO.getCorpProfile().getGroupProfileMgmt()) &&
            		!GRPROMGMTENABLED.equals( grpGersistDTO.getParamNameValueMapCommon().get(KnConstants.GROUP_PROFILE_MGMT))) {
                knLogger.error(methodName, "Group Profile Management flag diabled ");
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.GROUP_PROFILE_FEATURE_NOT_ALLOWED,
                        "Group profile feature disabled.", getEntityId(), getOperationType(), getRuleId(), "", "");

            }
        }
    }
    
}
