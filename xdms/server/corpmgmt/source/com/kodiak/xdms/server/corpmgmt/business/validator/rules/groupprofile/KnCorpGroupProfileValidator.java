/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.rules.groupprofile;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpBulkGroupDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpGorupProfileDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpGroupProfileDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPDeleteBulkCorpGrpDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpBulkGroupPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupProfilePersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.corpmgmt.resources.KnOperationTypes;

public class KnCorpGroupProfileValidator extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnCorpGroupProfileValidator.class);

    /**
     * validate method. all the validators should implement this method
     *
     * @throws KnValidationException
     * @throws KnBOException
     */
    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY.");
        IPersistenceDTO persistDTO = getDTO();
        if (persistDTO instanceof KnCorpBulkGroupPersistDTO) {
            KnCorpBulkGroupPersistDTO grpPersistDTO = (KnCorpBulkGroupPersistDTO) persistDTO;
            knLogger.debug(methodName, "Input DTO - ", grpPersistDTO.getInputDTO().getClass());
            String value="";
            if(grpPersistDTO.getInputDTO() instanceof KnIPCorpBulkGroupDTO){
                KnIPCorpBulkGroupDTO inputDto = (KnIPCorpBulkGroupDTO) grpPersistDTO.getInputDTO();
                if(inputDto.getProfileId() != null ){
                    value = String.valueOf(inputDto.getProfileId());
                }else if(inputDto.getProfileName() != null){
                    value = inputDto.getProfileName();
                }
            } else if(grpPersistDTO.getInputDTO() instanceof KnIPCorpGroupProfileDTO){
                KnIPCorpGroupProfileDTO inputDto = (KnIPCorpGroupProfileDTO) grpPersistDTO.getInputDTO();
                if(inputDto.getProfileId() != null ){
                    value = String.valueOf(inputDto.getProfileId());
                }else if(inputDto.getProfileName() != null){
                    value = inputDto.getProfileName();
                }
            }

            if (grpPersistDTO.getGroupProfile() == null ) {
                knLogger.error(methodName, "Group Profile not exist ");
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.GROUP_PROFILE_DOES_NOT_EXIST,
                        "Group profile not exist.", getEntityId(), getOperationType(), getRuleId(), value, "");
            }
        }

        if (persistDTO instanceof KnCorpGroupProfilePersistDTO) {
        	KnCorpGroupProfilePersistDTO grpPersistDTO = (KnCorpGroupProfilePersistDTO) persistDTO;
            IInputDTO input = grpPersistDTO.getInputDTO();
            String value = "";
        	if(input instanceof KnIPCorpGorupProfileDTO){
                KnIPCorpGorupProfileDTO inputDto = (KnIPCorpGorupProfileDTO) grpPersistDTO.getInputDTO();
                value = inputDto.getGrpProfileName();
                if(inputDto.getGrpProfileName() != null ){
                    value = inputDto.getGrpProfileName();
                }
                if(inputDto.getGrpProfileId() != null){
                    value = String.valueOf(inputDto.getGrpProfileId());
                }
            }else if(input instanceof KnIPDeleteBulkCorpGrpDTO){
                KnIPDeleteBulkCorpGrpDTO inputDto = (KnIPDeleteBulkCorpGrpDTO) grpPersistDTO.getInputDTO();
                value = inputDto.getGrpProfileName();
                if(inputDto.getGrpProfileName() != null ){
                    value = inputDto.getGrpProfileName();
                }
                if(inputDto.getGrpProfileId() != null){
                    value = String.valueOf(inputDto.getGrpProfileId());
                }
            }

            if (grpPersistDTO.isProfileExist() && KnOperationTypes.CREATE_CORP_GROUP_PROFILE.equals(grpPersistDTO.getOperationType()) ) {
                knLogger.error(methodName, "Group Profile not exist ");
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.GROUP_PROFILE_ALREADY_EXIST,
                        "Group profile alreay exist.", getEntityId(), getOperationType(), getRuleId(), value, "");
            }
            if (!(grpPersistDTO.isProfileExist()) && (KnOperationTypes.GET_GROUP_PROFILE_DETAILS.equals(grpPersistDTO.getOperationType()) ||
                    KnOperationTypes.MODIFY_GROUP_PROFILE.equals(grpPersistDTO.getOperationType()) ||
                    KnOperationTypes.DELETE_GROUP_PROFILE.equals(grpPersistDTO.getOperationType()) ||
                    KnOperationTypes.DELETE_GROUP_PROFILE_GROUP_LIST.equals(grpPersistDTO.getOperationType()) ) ) {
                knLogger.error(methodName, "Group Profile not exist ");
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.GROUP_PROFILE_DOES_NOT_EXIST,
                        "Group profile not exist.", getEntityId(), getOperationType(), getRuleId(), value, "");
            }

        }
    }
}
