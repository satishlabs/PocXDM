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
import com.kodiak.xdms.server.corpmgmt.business.validator.rules.groupsharing.KnCorpGrpSharingFlagValidator;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpGroupInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpBCGrpPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupProfilePersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

public class KnCorpPreConfigGroupValidator extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnCorpPreConfigGroupValidator.class);

    /**
     * validate method. all the validators should implement this method
     *
     * @throws KnValidationException
     * @throws KnBOException
     */
    private static final String FEATURENABLED = "1";
    private static final String MCXGRPNABLED = "1";
    private static final int MCXENABLEDINDICATOR = 1;
    private static final String IS_PRECONFIG_GRP="IS_PRECONFIG_GRP";
    Integer preConfigFeature = null;

    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName = "validates()";
        knLogger.debug(methodName, "ENTRY.");
        IPersistenceDTO persistDTO = getDTO();
       if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
            knLogger.debug(methodName,"Bean is instance of KnCorpGroupInfoPersistDTO");
            KnCorpGroupInfoPersistDTO grpGersistDTO = (KnCorpGroupInfoPersistDTO) persistDTO;
            grpGersistDTO.getInputDTO();
            if(grpGersistDTO.getInputDTO() instanceof KnIPCorpGroupInfoDTO) {
                KnIPCorpGroupInfoDTO inputDTO = (KnIPCorpGroupInfoDTO) grpGersistDTO.getInputDTO();
                preConfigFeature = inputDTO.getIsPreConfiguredGroup();
            }
            knLogger.debug(methodName,"Input Request for isPreConfigGroup",preConfigFeature);
            if (preConfigFeature !=null && preConfigFeature == Integer.parseInt(FEATURENABLED)) {
                knLogger.debug(methodName, "", "corp level", "",
                        "system level", grpGersistDTO.getParamNameValueMapCommon().get(KnConstants.MCX_GROUP_REGROUP_FLAG));


                    if(!FEATURENABLED.equals(grpGersistDTO.getParamNameValueMapCommon().get(KnConstants.MCX_GROUP_REGROUP_FLAG))){
                        knLogger.error(methodName, "preConfig feature is  disabled");
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.PRECONFIG_GROUP_FEATURE_IS_NOT_ALLOWED,
                                "preConfig feature is  disabled", getEntityId(), getOperationType(), getRuleId(), "", "");
                    }


            }else {
                knLogger.debug(methodName,"Input request is not for preconfig ,therefore skipping validation");
            }
        }
        else if (persistDTO instanceof KnCorpBCGrpPersistDTO) {
            knLogger.debug(methodName,"Bean is instance of KnCorpBCGrpPersistDTO");
            KnCorpBCGrpPersistDTO grpGersistDTO = (KnCorpBCGrpPersistDTO) persistDTO;
            grpGersistDTO.getInputDTO();
            if(grpGersistDTO.getInputDTO() instanceof KnIPCorpGroupInfoDTO) {
                KnIPCorpGroupInfoDTO inputDTO = (KnIPCorpGroupInfoDTO) grpGersistDTO.getInputDTO();
                preConfigFeature = inputDTO.getIsPreConfiguredGroup();
            }
           knLogger.debug(methodName,"Input Request for preconfig",preConfigFeature);
            if (preConfigFeature !=null && preConfigFeature == Integer.parseInt(FEATURENABLED)) {
                knLogger.debug(methodName, "Flag - ", "","",
                        "system level", grpGersistDTO.getParamNameValueMapCommon().get(KnConstants.MCX_GROUP_REGROUP_FLAG));


                    if(!FEATURENABLED.equals(grpGersistDTO.getParamNameValueMapCommon().get(KnConstants.MCX_GROUP_REGROUP_FLAG))){
                        knLogger.error(methodName, "preConfig feature is  disabled");
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.PRECONFIG_GROUP_FEATURE_IS_NOT_ALLOWED,
                                "preConfig feature is  disabled", getEntityId(), getOperationType(), getRuleId(), "", "");
                    }

            }else {
                knLogger.debug(methodName,"Input request is not for GroupSharing ,therefore skipping validation");
            }

        }
    }

}
