package com.kodiak.xdms.server.corpmgmt.business.validator.rules.groupsharing;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpGorupProfileDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpGroupInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSharedCorpInfo;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpBCGrpPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupProfilePersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class KnCorpGroupSharedFanValidation extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnCorpGroupSharedFanValidation.class);

    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName = "validate()";
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug(methodName, "ENTRY : dtoType - ", persistDTO != null ? persistDTO.getClass().getName() : null);

        if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
            KnCorpGroupInfoPersistDTO groupPersistDTO = (KnCorpGroupInfoPersistDTO) persistDTO;
            if (groupPersistDTO.getInputDTO() instanceof KnIPCorpGroupInfoDTO) {
                KnIPCorpGroupInfoDTO groupInfoDTO = (KnIPCorpGroupInfoDTO) groupPersistDTO.getInputDTO();
                knLogger.debug(methodName, "DTO - KnCorpGroupInfoPersistDTO, removedSharedCorpIds - ",
                        groupPersistDTO.getRemovedSharedCorpIds(),
                        ", sharedCorpFanMap - ", groupPersistDTO.getSharedCorpFanMap());
                validateRemovedSharedCorpFans(groupPersistDTO.getRemovedSharedCorpIds(),
                        groupPersistDTO.getSharedCorpFanMap());
                if (groupPersistDTO.isGrpSharedModified() && groupInfoDTO.getGrpShared() == 0) {
                    knLogger.debug(methodName, "Grp shared disable path for KnCorpGroupInfoPersistDTO");
                    validateRemovedSharedCorpFans(groupPersistDTO.getRemovedSharedCorpIds(),
                            groupPersistDTO.getSharedCorpFanMap());
                }
                if (groupInfoDTO.getCorpSharedCorpInfoList() != null && !groupInfoDTO.getCorpSharedCorpInfoList().isEmpty()) {
                    knLogger.debug(methodName, "Grp shared list update path for KnCorpGroupInfoPersistDTO");
                    validateRemovedSharedCorpFans(groupPersistDTO.getRemovedSharedCorpIds(),
                            groupPersistDTO.getSharedCorpFanMap());
                }
            }
        } else if (persistDTO instanceof KnCorpGroupProfilePersistDTO){
            KnCorpGroupProfilePersistDTO groupProfilePersistDTO = (KnCorpGroupProfilePersistDTO) persistDTO;
            if (groupProfilePersistDTO.getInputDTO() instanceof KnIPCorpGorupProfileDTO) {
                KnIPCorpGorupProfileDTO groupProfileInfoDTO = (KnIPCorpGorupProfileDTO) groupProfilePersistDTO.getInputDTO();
                if (groupProfilePersistDTO.isGrpSharedChanged() && groupProfileInfoDTO.getGrpShared() == 0) {
                    knLogger.debug(methodName, "Grp shared disable path for KnCorpGroupInfoPersistDTO");
                    validateRemovedSharedCorpFans(groupProfilePersistDTO.getRemovedSharedCorpIds(),
                            groupProfilePersistDTO.getSharedCorpFanMap());
                }
                if(groupProfileInfoDTO.getSharedCorpList() != null && !groupProfileInfoDTO.getSharedCorpList().isEmpty()){
                    knLogger.debug(methodName, "Grp shared list update path for KnCorpGroupInfoPersistDTO");
                    validateRemovedSharedCorpFans(groupProfilePersistDTO.getRemovedSharedCorpIds(),
                            groupProfilePersistDTO.getSharedCorpFanMap());
                }
            }
        }
        else if (persistDTO instanceof KnCorpBCGrpPersistDTO) {
            knLogger.debug(methodName, "DTO - KnCorpBCGrpPersistDTO");
            KnCorpBCGrpPersistDTO bcGrpPersistDTO = (KnCorpBCGrpPersistDTO) persistDTO;
            if (bcGrpPersistDTO.getInputDTO() instanceof KnIPCorpGroupInfoDTO) {
                KnIPCorpGroupInfoDTO groupInfoDTO = (KnIPCorpGroupInfoDTO) bcGrpPersistDTO.getInputDTO();
                knLogger.debug(methodName, "removedSharedCorpIds - ", bcGrpPersistDTO.getRemovedSharedCorpIds(),
                        ", sharedCorpFanMap - ", bcGrpPersistDTO.getSharedCorpFanMap());
                validateRemovedSharedCorpFans(bcGrpPersistDTO.getRemovedSharedCorpIds(),
                        bcGrpPersistDTO.getSharedCorpFanMap());
                if (bcGrpPersistDTO.isGrpSharedModified() && groupInfoDTO.getGrpShared() == 0) {
                    knLogger.debug(methodName, "Grp shared disable path for KnCorpBCGrpPersistDTO");
                    validateRemovedSharedCorpFans(bcGrpPersistDTO.getRemovedSharedCorpIds(),
                            bcGrpPersistDTO.getSharedCorpFanMap());
                }
                if (groupInfoDTO.getCorpSharedCorpInfoList() != null && !groupInfoDTO.getCorpSharedCorpInfoList().isEmpty()) {
                    knLogger.debug(methodName, "Grp shared list update path for KnCorpBCGrpPersistDTO");
                    validateRemovedSharedCorpFans(bcGrpPersistDTO.getRemovedSharedCorpIds(),
                            bcGrpPersistDTO.getSharedCorpFanMap());
                }
            }
        }
        knLogger.debug(methodName, "EXIT");
    }

    private void validateRemovedSharedCorpFans(Collection<Integer> removedSharedCorpIds,
                                               Map<Integer, List<Integer>> sharedCorpFanMap)
            throws KnCorpBOValidationException {
        knLogger.debug("validateRemovedSharedCorpFans", "removedSharedCorpIds - ", removedSharedCorpIds,
                ", sharedCorpFanMap - ", sharedCorpFanMap);
        if (removedSharedCorpIds == null || removedSharedCorpIds.isEmpty()
                || sharedCorpFanMap == null || sharedCorpFanMap.isEmpty()) {
            knLogger.debug("validateRemovedSharedCorpFans", "Nothing to validate.");
            return;
        }

        List<Integer> invalidCorpIds = new ArrayList<>();
        for (Integer removedCorpId : removedSharedCorpIds) {
            List<Integer> fanIds = sharedCorpFanMap.get(removedCorpId);
            knLogger.debug("validateRemovedSharedCorpFans", "Checking removedCorpId - ", removedCorpId,
                    ", fanIds - ", fanIds);
            if (fanIds != null && !fanIds.isEmpty()) {
                invalidCorpIds.add(removedCorpId);
            }
        }

        if (!invalidCorpIds.isEmpty()) {
            knLogger.debug("validateRemovedSharedCorpFans", "Removed shared corp still has mapped fans - ", invalidCorpIds);
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.SHARED_FAN_EXIST_CANNOT_UNSHARE,
                    "Removed shared corp still has mapped fans -", getEntityId(),
                    getOperationType(), getRuleId(), invalidCorpIds.toString(), "");
        }
        knLogger.debug("validateRemovedSharedCorpFans", "Validation passed.");
    }
}
