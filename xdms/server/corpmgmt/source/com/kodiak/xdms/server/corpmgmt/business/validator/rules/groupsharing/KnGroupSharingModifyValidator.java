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
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpGorupProfileDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpGroupInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGroupMemberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpBCGrpPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupProfilePersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.*;

public class KnGroupSharingModifyValidator extends KnValidatorRule {

    private static final KnLogger knLogger = KnLogger.getLogger(KnGroupSharingModifyValidator.class);

    /**
     * validate method. all the validators should implement this method
     *
     * @throws KnValidationException
     * @throws KnBOException
     */
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
                        groupPersistDTO.getRemovedSharedCorpIds(), ", corpContactExternalMdns - ",
                        groupPersistDTO.getCorpContactExternalMdns());
                if (groupPersistDTO.isGrpSharedModified() && groupInfoDTO.getGrpShared() == 0) {
                    List<String> corpContactExternalMdns = groupPersistDTO.getCorpContactExternalMdns();
                    knLogger.debug(methodName, "GrpShared disable path for KnCorpGroupInfoPersistDTO");
                    validateRemovedSharedCorpMembers(groupPersistDTO.getExistingGrpMemDetail(),
                            groupPersistDTO.getCorpId(), groupPersistDTO.getRemovedSharedCorpIds(), corpContactExternalMdns);
                }
                if(groupInfoDTO.getCorpSharedCorpInfoList() != null && !groupInfoDTO.getCorpSharedCorpInfoList().isEmpty()){
                    knLogger.debug(methodName, "Shared corp list update path for KnCorpGroupInfoPersistDTO");
                    validateRemovedSharedCorpMembers(groupPersistDTO.getExistingGrpMemDetail(),
                            groupPersistDTO.getCorpId(), groupPersistDTO.getRemovedSharedCorpIds(),null);
                }
            }
        } else if (persistDTO instanceof KnCorpGroupProfilePersistDTO) {
            KnCorpGroupProfilePersistDTO groupProfilePersistDTO = (KnCorpGroupProfilePersistDTO) persistDTO;
            if (groupProfilePersistDTO.getInputDTO() instanceof KnIPCorpGorupProfileDTO) {
                KnIPCorpGorupProfileDTO groupProfileInfoDTO = (KnIPCorpGorupProfileDTO) groupProfilePersistDTO.getInputDTO();
                knLogger.debug(methodName, "DTO - KnCorpGroupProfilePersistDTO, removedSharedCorpIds - ",
                        groupProfilePersistDTO.getRemovedSharedCorpIds(), ", sharedCorpFanMap - ",
                        groupProfilePersistDTO.getSharedCorpFanMap());
                if (groupProfilePersistDTO.isGrpSharedChanged() && groupProfileInfoDTO.getGrpShared() == 0) {
                    knLogger.debug(methodName, "GrpShared disable path for KnCorpGroupProfilePersistDTO");
                    validateRemovedSharedCorpMembers(groupProfilePersistDTO.getExsistingGroupMemberList(),
                            groupProfileInfoDTO.getCorpId(), groupProfilePersistDTO.getRemovedSharedCorpIds(),null);
                }
                if(groupProfileInfoDTO.getSharedCorpList() != null && !groupProfileInfoDTO.getSharedCorpList().isEmpty()){
                    knLogger.debug(methodName, "Shared corp list update path for KnCorpGroupProfilePersistDTO");
                    validateRemovedSharedCorpMembers(groupProfilePersistDTO.getExsistingGroupMemberList(),
                            groupProfileInfoDTO.getCorpId(), groupProfilePersistDTO.getRemovedSharedCorpIds(),null);
                }
            }
        }else if (persistDTO instanceof KnCorpBCGrpPersistDTO) {
            KnCorpBCGrpPersistDTO bcGrpPersistDTO = (KnCorpBCGrpPersistDTO) persistDTO;
            if (bcGrpPersistDTO.getInputDTO() instanceof KnIPCorpGroupInfoDTO) {
                KnIPCorpGroupInfoDTO groupInfoDTO = (KnIPCorpGroupInfoDTO) bcGrpPersistDTO.getInputDTO();
                knLogger.debug(methodName, "DTO - KnCorpBCGrpPersistDTO, removedSharedCorpIds - ",
                        bcGrpPersistDTO.getRemovedSharedCorpIds(), ", corpContactExternalMdns - ",
                        bcGrpPersistDTO.getCorpContactExternalMdns());
                if (bcGrpPersistDTO.isGrpSharedModified() && groupInfoDTO.getGrpShared() == 0) {
                    List<String> corpContactExternalMdns = bcGrpPersistDTO.getCorpContactExternalMdns();
                    knLogger.debug(methodName, "GrpShared disable path for KnCorpBCGrpPersistDTO");
                    validateRemovedSharedCorpMembers(bcGrpPersistDTO.getExistingGrpMemDetail(),
                            groupInfoDTO.getCorpId(), bcGrpPersistDTO.getRemovedSharedCorpIds(), corpContactExternalMdns);
                }
                if((groupInfoDTO.getCorpSharedCorpInfoList() != null) && (!groupInfoDTO.getCorpSharedCorpInfoList().isEmpty())){
                    knLogger.debug(methodName, "Shared corp list update path for KnCorpBCGrpPersistDTO");
                    validateRemovedSharedCorpMembers(bcGrpPersistDTO.getExistingGrpMemDetail(),
                            groupInfoDTO.getCorpId(), bcGrpPersistDTO.getRemovedSharedCorpIds(),null);
                }
            }
        }
        knLogger.debug(methodName, "EXIT");
    }

    private void validateRemovedSharedCorpMembers(Collection<KnCorpGroupMemberDTO> existingMembers, int ownerCorpId,
                                                  Collection<Integer> removedSharedCorpIds, List<String> corpContactExternalMdns) throws KnCorpBOValidationException {
        if (existingMembers == null || existingMembers.isEmpty()
                || removedSharedCorpIds == null || removedSharedCorpIds.isEmpty()) {
            return;
        }

        Set<Integer> removedCorpIds = new HashSet<>(removedSharedCorpIds);
        List<String> invalidMdnList = new ArrayList<>();
        for (KnCorpGroupMemberDTO member : existingMembers) {
            knLogger.debug("validateRemovedSharedCorpMembers", "member - ", member.getMdn(),
                    ", corpId - ", member.getCorpId(),
                    ", isExternalContact - ", member.isExternalContact(),
                    ", corpContactExternalMdns - ", corpContactExternalMdns);
            boolean memberExternal = member.isExternalContact();
            boolean mdnInExternalList = corpContactExternalMdns != null && corpContactExternalMdns.contains(member.getMdn());
            if (mdnInExternalList) {
                memberExternal = true;
                knLogger.debug("validateRemovedSharedCorpMembers", "member ", member.getMdn(), " marked external based on corpContactExternalMdns");
            }

            if (member.getCorpId() != 0 && member.getCorpId() != ownerCorpId
                    && removedCorpIds.contains(member.getCorpId()) && !memberExternal) {
                knLogger.debug("validateRemovedSharedCorpMembers", "Group still has member from removed shared corp -", member.getMdn());
                invalidMdnList.add(member.getMdn());
            } else {
                knLogger.debug("validateRemovedSharedCorpMembers", "member passed removed shared corp check -", member.getMdn());
            }
        }

        if (!invalidMdnList.isEmpty()) {
            knLogger.debug("validateRemovedSharedCorpMembers", "Unsharing corp members exist, can not unshare the Members - ", invalidMdnList);
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.GROUP_SHARED_FLAG_MODIFY_NOT_ALLOWED,
                    "Group having other corp member", getEntityId(),
                    getOperationType(), getRuleId(), invalidMdnList.toString(), "");
        }
    }
}
