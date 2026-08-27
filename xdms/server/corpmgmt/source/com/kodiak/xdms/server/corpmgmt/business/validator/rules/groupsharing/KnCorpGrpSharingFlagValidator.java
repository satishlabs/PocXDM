/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.rules.groupsharing;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.business.validator.rules.groupprofile.KnCorpGrpProfileFlagValidator;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpGroupInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpBCGrpPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpBulkGroupPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupProfilePersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;


public class KnCorpGrpSharingFlagValidator extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnCorpGrpSharingFlagValidator.class);

    /**
     * validate method. all the validators should implement this method
     *
     * @throws KnValidationException
     * @throws KnBOException
     */
    private static final String GRPSHARINGNABLED = "1";
    private static final String MCXGRPNABLED = "1";
    private static final int MCXENABLEDINDICATOR = 1;

    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY.");
        IPersistenceDTO persistDTO = getDTO();
        if (persistDTO instanceof KnCorpGroupProfilePersistDTO) {
            KnCorpGroupProfilePersistDTO grpGersistDTO = (KnCorpGroupProfilePersistDTO) persistDTO;
            if (grpGersistDTO.getGrpShared() != null && grpGersistDTO.getGrpShared().intValue() == Integer.parseInt(GRPSHARINGNABLED)) {
                knLogger.debug(methodName,"input request for group sharing",grpGersistDTO.getGrpShared());
                knLogger.debug(methodName, "Flag - ", "corp level", grpGersistDTO.getCorpProfile().getGroupSharingFeature(),
                        "system level", grpGersistDTO.getParamNameValueMapCommon().get(KnConstants.GROUP_SHARING_FLAG));

                if (!GRPSHARINGNABLED.equals("" + grpGersistDTO.getCorpProfile().getGroupSharingFeature())) {
                    if(!GRPSHARINGNABLED.equals(grpGersistDTO.getParamNameValueMapCommon().get(KnConstants.GROUP_SHARING_FLAG))){
                        knLogger.error(methodName, "Group Sharing flag is  disabled");
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.GROUP_SHARING_FEATURE_NOT_ALLOWED,
                                "Group sharing feature disabled.", getEntityId(), getOperationType(), getRuleId(), "", "");
                    }

                }
            }else {
                knLogger.debug(methodName,"Input request is not for groupSharing hence skipping validation");
            }
        }else if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
            knLogger.debug(methodName,"Bean is instance of KnCorpGroupInfoPersistDTO");
            KnCorpGroupInfoPersistDTO grpGersistDTO = (KnCorpGroupInfoPersistDTO) persistDTO;
            grpGersistDTO.getInputDTO();
            Integer reqGrpShared = 0;
            if(grpGersistDTO.getInputDTO() instanceof KnIPCorpGroupInfoDTO) {
                KnIPCorpGroupInfoDTO inputDTO = (KnIPCorpGroupInfoDTO) grpGersistDTO.getInputDTO();
                reqGrpShared = inputDTO.getGrpShared();
            }
            knLogger.debug(methodName,"Input Request for GroupSharing",grpGersistDTO.getGrpShared());
            if (reqGrpShared!=null && reqGrpShared == Integer.parseInt(GRPSHARINGNABLED)) {
                knLogger.debug(methodName, "Flag - ", "corp level", grpGersistDTO.getCorpProfile().getGroupSharingFeature(),
                        "system level", grpGersistDTO.getParamNameValueMapCommon().get(KnConstants.GROUP_SHARING_FLAG));

                if (!GRPSHARINGNABLED.equals("" + grpGersistDTO.getCorpProfile().getGroupSharingFeature())) {
                    if(!GRPSHARINGNABLED.equals(grpGersistDTO.getParamNameValueMapCommon().get(KnConstants.GROUP_SHARING_FLAG))){
                        knLogger.error(methodName, "GrouSharing Flag is  disabled");
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.GROUP_SHARING_FEATURE_NOT_ALLOWED,
                                "GroupSharing feature disabled.", getEntityId(), getOperationType(), getRuleId(), "", "");
                    }
                }

            }else {
                knLogger.debug(methodName,"Input request is not for GroupSharing ,therefore skipping validation");
            }
        }
        else if (persistDTO instanceof KnCorpBCGrpPersistDTO) {
            knLogger.debug(methodName,"Bean is instance of KnCorpGroupInfoPersistDTO");
            KnCorpBCGrpPersistDTO grpGersistDTO = (KnCorpBCGrpPersistDTO) persistDTO;
            grpGersistDTO.getInputDTO();
            Integer reqGrpShared = 0;
            if(grpGersistDTO.getInputDTO() instanceof KnIPCorpGroupInfoDTO) {
                KnIPCorpGroupInfoDTO inputDTO = (KnIPCorpGroupInfoDTO) grpGersistDTO.getInputDTO();
                reqGrpShared = inputDTO.getGrpShared();
            }
            knLogger.debug(methodName,"Input Request for GroupSharing",grpGersistDTO.getGrpShared());
            if (reqGrpShared!=null && reqGrpShared == Integer.parseInt(GRPSHARINGNABLED)) {
                knLogger.debug(methodName, "Flag - ", "corp level", grpGersistDTO.getCorpProfile().getGroupSharingFeature(),
                        "system level", grpGersistDTO.getParamNameValueMapCommon().get(KnConstants.GROUP_SHARING_FLAG));

                if (!GRPSHARINGNABLED.equals("" + grpGersistDTO.getCorpProfile().getGroupSharingFeature())) {
                    if(!GRPSHARINGNABLED.equals(grpGersistDTO.getParamNameValueMapCommon().get(KnConstants.GROUP_SHARING_FLAG))){
                        knLogger.error(methodName, "GrouSharing Flag is  disabled");
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.GROUP_SHARING_FEATURE_NOT_ALLOWED,
                                "GroupSharing feature disabled.", getEntityId(), getOperationType(), getRuleId(), "", "");
                    }
                }

            }else {
                knLogger.debug(methodName,"Input request is not for GroupSharing ,therefore skipping validation");
            }

        }
    }

}
