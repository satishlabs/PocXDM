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
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpGroupProfileDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGroupMemberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpBCGrpPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupProfilePersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.ArrayList;
import java.util.List;

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
        List<String> invalidMdnList = new ArrayList<>();
        if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
            KnCorpGroupInfoPersistDTO groupPersistDTO = (KnCorpGroupInfoPersistDTO) persistDTO;
            if (groupPersistDTO.getInputDTO() instanceof KnIPCorpGroupInfoDTO) {
                KnIPCorpGroupInfoDTO groupInfoDTO = (KnIPCorpGroupInfoDTO) groupPersistDTO.getInputDTO();
                if (groupPersistDTO.isGrpSharedModified() && groupInfoDTO.getGrpShared() == 0) {
                    knLogger.debug(methodName, "GrpShared modifying to disable");
                    for (KnCorpGroupMemberDTO member : groupPersistDTO.getExistingGrpMemDetail()) {
                        knLogger.debug(methodName, "Check1 member -", member.getMdn(), ", corpId -", member.getCorpId(),
                                ", isExternalContact -", member.isExternalContact());
                        boolean memberExternal = member.isExternalContact();
                        boolean mdnInExternalList = groupPersistDTO.getCorpContactExternalMdns() != null && groupPersistDTO.getCorpContactExternalMdns().contains(member.getMdn());
                        if (mdnInExternalList) {
                            memberExternal = true;
                            knLogger.debug(methodName, "member ", member.getMdn(), " marked external based on corpContactExternalMdns");
                        }
                        if (member.getCorpId() != 0 && member.getCorpId() != groupPersistDTO.getCorpId()
                                && !memberExternal) {
                            knLogger.debug(methodName, "Group having other corp member -", member.getMdn());
                            throw new KnCorpBOValidationException(KnErrorCodes.Validator.GROUP_SHARED_FLAG_MODIFY_NOT_ALLOWED,
                                    "Group having other corp member", getEntityId(),
                                    getOperationType(), getRuleId(), "", "");
                        }
                    }
                }
                if(groupInfoDTO.getCorpSharedCorpInfoList() != null && !groupInfoDTO.getCorpSharedCorpInfoList().isEmpty()){
                    knLogger.debug(methodName, "Shared Corp list size", groupInfoDTO.getCorpSharedCorpInfoList().size());
                    List<Integer> sharedCorpIds = new ArrayList<>();
                    groupInfoDTO.getCorpSharedCorpInfoList().forEach(item->sharedCorpIds.add(item.getCorpId()));
                    knLogger.debug(methodName, "sharedCorpIds", sharedCorpIds);
                    for (KnCorpGroupMemberDTO member : groupPersistDTO.getExistingGrpMemDetail()) {
                        if (member.getCorpId() != 0 && member.getCorpId() != groupPersistDTO.getCorpId() &&
                        (!sharedCorpIds.contains(member.getCorpId())) && !member.isExternalContact()) {
                                invalidMdnList.add(member.getMdn());
                        }
                    }
                }
                // TC-GRP-MOD-008: hierarchy-aware removal blocking is handled in KnCorpGroupInfoController P6-4 delta block
            }
        } else if (persistDTO instanceof KnCorpGroupProfilePersistDTO) {
            KnCorpGroupProfilePersistDTO groupProfilePersistDTO = (KnCorpGroupProfilePersistDTO) persistDTO;
            if (groupProfilePersistDTO.getInputDTO() instanceof KnIPCorpGorupProfileDTO) {
                KnIPCorpGorupProfileDTO groupProfileInfoDTO = (KnIPCorpGorupProfileDTO) groupProfilePersistDTO.getInputDTO();
                if (groupProfilePersistDTO.isGrpSharedChanged() && groupProfileInfoDTO.getGrpShared() == 0) {
                    knLogger.debug(methodName, "GrpShared modifying to disable");
                    for (KnCorpGroupMemberDTO member : groupProfilePersistDTO.getExsistingGroupMemberList()) {
                        if (member.getCorpId() != 0 && member.getCorpId() != groupProfileInfoDTO.getCorpId()
                                && !member.isExternalContact()) {
                            knLogger.debug(methodName, "Group having other corp member -", member.getMdn(), member.getGroupId());
                            throw new KnCorpBOValidationException(KnErrorCodes.Validator.GROUP_SHARED_FLAG_MODIFY_NOT_ALLOWED,
                                    "Group having other corp member", getEntityId(),
                                    getOperationType(), getRuleId(), "", "");
                        }
                    }
                }
                if(groupProfileInfoDTO.getSharedCorpList() != null && !groupProfileInfoDTO.getSharedCorpList().isEmpty()){
                    knLogger.debug(methodName, "Shared Corp list size", groupProfileInfoDTO.getSharedCorpList().size());
                    List<Integer> sharedCorpIds = new ArrayList<>();
                    groupProfileInfoDTO.getSharedCorpList().forEach(item->sharedCorpIds.add(item.getCorpId()));
                    knLogger.debug(methodName, "sharedCorpIds", sharedCorpIds);
                    for (KnCorpGroupMemberDTO member : groupProfilePersistDTO.getExsistingGroupMemberList()) {
                        if (member.getCorpId() != 0 && member.getCorpId() != groupProfileInfoDTO.getCorpId() &&
                                (!sharedCorpIds.contains(member.getCorpId())) && !member.isExternalContact()) {
                            invalidMdnList.add(member.getMdn());
                        }
                    }
                }
            }
        }else if (persistDTO instanceof KnCorpBCGrpPersistDTO) {
            knLogger.debug(methodName, "DTO - KnCorpBCGrpPersistDTO");
            KnCorpBCGrpPersistDTO bcGrpPersistDTO = (KnCorpBCGrpPersistDTO) persistDTO;
            if (bcGrpPersistDTO.getInputDTO() instanceof KnIPCorpGroupInfoDTO) {
                KnIPCorpGroupInfoDTO groupInfoDTO = (KnIPCorpGroupInfoDTO) bcGrpPersistDTO.getInputDTO();
                knLogger.debug(methodName, "Changes -", bcGrpPersistDTO.isGrpSharedModified(), groupInfoDTO.getGrpShared());
                if (bcGrpPersistDTO.isGrpSharedModified() && groupInfoDTO.getGrpShared() == 0) {
                    knLogger.debug(methodName, "GrpShared modifying to disable");
                    for (KnCorpGroupMemberDTO member : bcGrpPersistDTO.getExistingGrpMemDetail()) {
                        boolean memberExternal = member.isExternalContact();
                        boolean mdnInExternalList2 = bcGrpPersistDTO.getCorpContactExternalMdns() != null && bcGrpPersistDTO.getCorpContactExternalMdns().contains(member.getMdn());
                        if (mdnInExternalList2) {
                            memberExternal = true;
                            knLogger.debug(methodName, "member ", member.getMdn(), " marked external based on corpContactExternalMdns");
                        }
                        if (member.getCorpId() != 0 && member.getCorpId() != groupInfoDTO.getCorpId()
                                && !memberExternal) {
                            knLogger.debug(methodName, "Group having other corp member -", member.getMdn(), member.getGroupId());
                            throw new KnCorpBOValidationException(KnErrorCodes.Validator.GROUP_SHARED_FLAG_MODIFY_NOT_ALLOWED,
                                    "Group having other corp member", getEntityId(),
                                    getOperationType(), getRuleId(), "", "");
                        }
                    }
                }
                if((groupInfoDTO.getCorpSharedCorpInfoList() != null) && (!groupInfoDTO.getCorpSharedCorpInfoList().isEmpty())){
                    List<Integer> sharedCorpIds = new ArrayList<>();
                    groupInfoDTO.getCorpSharedCorpInfoList().forEach(item->sharedCorpIds.add(item.getCorpId()));
                    for (KnCorpGroupMemberDTO member : bcGrpPersistDTO.getExistingGrpMemDetail()) {
                        if (member.getCorpId() != 0 && member.getCorpId() != bcGrpPersistDTO.getCorpId() &&
                                (!sharedCorpIds.contains(member.getCorpId())) && !member.isExternalContact() ) {
                            invalidMdnList.add(member.getMdn());
                        }
                    }
                }
            }
        }
        knLogger.debug(methodName, "invalidMdnList - ", invalidMdnList);
        if(!invalidMdnList.isEmpty()){
            knLogger.debug(methodName, "Unsharing corp members exist, can not unshare the Members - ", invalidMdnList);
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.GROUP_SHARED_FLAG_MODIFY_NOT_ALLOWED,
                    "Group having other corp member", getEntityId(),
                    getOperationType(), getRuleId(), invalidMdnList.toString(), "");
        }

    }
}
