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
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpGroupInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpBCGrpPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

public class KnModifyGrpDataProfileValidator extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnModifyGrpDataProfileValidator.class);

    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY Point");
        IPersistenceDTO persistDTO = getDTO();
        try {
            if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
                knLogger.debug(methodName, "persistDTO is instanceof KnCorpGroupInfoPersistDTO - ", persistDTO);
                KnCorpGroupInfoPersistDTO corpGroupInfoPersistDTO = (KnCorpGroupInfoPersistDTO) persistDTO;
                KnIPCorpGroupInfoDTO groupInfoDTO = (KnIPCorpGroupInfoDTO) corpGroupInfoPersistDTO.getInputDTO();
                knLogger.debug(methodName, " grpProfileId - ",corpGroupInfoPersistDTO.getGroupProfileId()," avatar - ",
                        corpGroupInfoPersistDTO.getAvatar(),"osm - ",corpGroupInfoPersistDTO.getOSMListId(), "overridednd - ",
                        corpGroupInfoPersistDTO.getOverrideDnd(), "GrpSharedModified - ", corpGroupInfoPersistDTO.isGrpSharedModified());
                if (corpGroupInfoPersistDTO.getGroupProfileId() != null && groupInfoDTO.isOwnerCorpReq()) {
                    if ((groupInfoDTO.isGroupTypeChanged()) ||
                            (groupInfoDTO.getAvatar() != null && groupInfoDTO.getAvatar() != KnConstants.AVATAR_NOT_MODIFIED) ||
                            (corpGroupInfoPersistDTO.isOsmListChanged())) {

                        knLogger.debug(methodName, "GroupType,Avatar,osmListID" +
                                " which created via profile is not allowed to be modified ");
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.MODIFY_GRPPROFILE_DATA_NOT_ALLOWED,
                                "GroupType,Avatar,osmListID which created via profile is not allowed to be modified", getEntityId(), getOperationType(), getRuleId(),
                                "", "");
                    }
                }
                knLogger.debug(methodName, "isOwnerCorpReq - ", groupInfoDTO.isOwnerCorpReq(), " grpShared - ", groupInfoDTO.getGrpShared(), " corpGrpShared - ", corpGroupInfoPersistDTO.getGrpShared());
                if (!groupInfoDTO.isOwnerCorpReq()) {
                    if ((groupInfoDTO.getGrpShared() != null && groupInfoDTO.getGrpShared() == KnConstants.GROUP_SHARING_ENABLE_INDICATOR) || (corpGroupInfoPersistDTO.getGrpShared() != null && corpGroupInfoPersistDTO.getGrpShared() == KnConstants.GROUP_SHARING_ENABLE_INDICATOR)) {
                        knLogger.debug(methodName, "Request received from shared corp");
                        if ((groupInfoDTO.isGroupTypeChanged()) ||
                                (groupInfoDTO.getAvatar() != null && groupInfoDTO.getAvatar() != KnConstants.AVATAR_NOT_MODIFIED) ||
                                (corpGroupInfoPersistDTO.isOsmListChanged()) ||
                                (corpGroupInfoPersistDTO.getCorpSharedCorpInfoList() != null) ||
                                (corpGroupInfoPersistDTO.isGrpSharedModified()) ||
                                (groupInfoDTO.getGroupDisplayName() != null)) {

                        knLogger.debug(methodName, "Shared corporate not allowed to modify group properties");
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.MODIFY_GRPPROFILE_DATA_NOT_ALLOWED,
                                "Shared corporate not allowed to modify group properties", getEntityId(), getOperationType(), getRuleId(),
                                "", "");
                    }}
                }

            } else if (persistDTO instanceof KnCorpBCGrpPersistDTO) {
                knLogger.debug(methodName, "persistDTO is instanceof KnCorpBCGrpPersistDTO - ", persistDTO);
                KnCorpBCGrpPersistDTO corpBCGrpPersistDTO = (KnCorpBCGrpPersistDTO) persistDTO;
                KnIPCorpGroupInfoDTO groupInfoDTO = (KnIPCorpGroupInfoDTO) corpBCGrpPersistDTO.getInputDTO();

                knLogger.debug(methodName, " grpProfileId - ",corpBCGrpPersistDTO.getGroupProfileId()," avatar - ",groupInfoDTO.getAvatar(),"osm - ",groupInfoDTO.getOSMListId(), "overridednd - ",groupInfoDTO.getOverrideDnd());

                if (corpBCGrpPersistDTO.getGroupProfileId() != null && groupInfoDTO.isOwnerCorpReq()) {
                    int overrideDND = KnConstants.OVERRIDE_DND_NOT_MODIFIED;
                    if(groupInfoDTO.isUpmCall()) {
                        knLogger.debug(methodName,"request is from UPM hence re-setting overrideDND value as 0 ");
                        overrideDND = 0;
                    }
                    if ((groupInfoDTO.getAvatar() != null && groupInfoDTO.getAvatar() != KnConstants.AVATAR_NOT_MODIFIED) ||
                            (groupInfoDTO.getOSMListId() != null)
                            ||(groupInfoDTO.getOverrideDnd() != overrideDND)){

                        knLogger.debug(methodName, "GroupType,Avatar,osmListID,overrideDND which created via profile is not allowed " +
                                "to be modified via modifyGroup operation ");
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.MODIFY_GRPPROFILE_DATA_NOT_ALLOWED,
                                "GroupType,Avatar,osmListID,overrideDND which created via profile is not allowed to be modified via modifyGroup operation",
                                getEntityId(), getOperationType(), getRuleId(),
                                "", "");
                    }
                }
                if(!groupInfoDTO.isOwnerCorpReq()){
                    knLogger.debug(methodName, "Request received from shared corp");
                    int overrideDND = KnConstants.OVERRIDE_DND_NOT_MODIFIED;
                    if(groupInfoDTO.isUpmCall()) {
                        knLogger.debug(methodName,"request is from UPM hence re-setting overrideDND value as 0 ");
                        overrideDND = 0;
                    }
                    if ((groupInfoDTO.isGroupTypeChanged()) ||
                            (groupInfoDTO.getAvatar() != null && groupInfoDTO.getAvatar() != KnConstants.AVATAR_NOT_MODIFIED) ||
                            ((groupInfoDTO.getOSMListId() != null) && (!groupInfoDTO.getOSMListId().isEmpty()) ) ||
                            (corpBCGrpPersistDTO.getCorpSharedCorpInfoList() != null) ||
                            (corpBCGrpPersistDTO.isGrpSharedModified()) ||
                            (groupInfoDTO.getGroupDisplayName() != null) ||
                            (groupInfoDTO.getOverrideDnd() != overrideDND) ) {

                        knLogger.error(methodName, "Shared corporate not allowed to modify group properties");
                       /* throw new KnCorpBOValidationException(KnErrorCodes.Validator.MODIFY_GRPPROFILE_DATA_NOT_ALLOWED,
                                "Shared corporate not allowed to modify group properties", getEntityId(), getOperationType(), getRuleId(),
                                "", "");*/
                        knLogger.error(methodName,"--LOGS--", getEntityId(), getOperationType(), getRuleId());
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.MODIFY_GRPPROFILE_DATA_NOT_ALLOWED,
                                "Shared corporate not allowed to modify group properties", getRuleId(), "Group Property", " ", "", "");
                    }
                }
            }
        } finally {
            knLogger.debug(methodName, "Exit Point: Validation Completed Successfully");
        }
    }

}
