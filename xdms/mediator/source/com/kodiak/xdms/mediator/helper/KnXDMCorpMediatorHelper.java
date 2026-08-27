/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/

package com.kodiak.xdms.mediator.helper;

import com.kodiak.common.commdto.common.KnXDMCorpGroupInfoDTO;
import com.kodiak.common.commdto.common.KnXDMGroupMdnInfoDTO;
import com.kodiak.common.commdto.request.KnXDMBulkCorpGroupInfoRequestDTO;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpGroupInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpContactDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGroupMemberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGrpBasicInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpGrpBasicInfoRespDto;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static com.kodiak.common.resources.KnConstants.CLIENT_TYPE_CAT_UI;
import static com.kodiak.common.resources.KnConstants.DISABLED;

public class KnXDMCorpMediatorHelper {
    KnLogger logger = KnLogger.getLogger(KnXDMCorpMediatorHelper.class);

    /**
     * This method is used to prepare a list of contacts from a collection of group members.
     * It creates a new contact for each member and sets the member's properties to the contact.
     * The method also checks the client type and sets the group modify permission accordingly.
     *
     * @param members    The collection of group members.
     * @param clientType The type of the client.
     * @return A set of contacts.
     */
    public Set<KnCorpGroupMemberDTO> prepareModifiedMemList(Collection<KnXDMGroupMdnInfoDTO> members, int clientType) {
        String methodName = "prepareModifiedMemList()";
        var modifiedMemList = new HashSet<KnCorpGroupMemberDTO>();

        for (KnXDMGroupMdnInfoDTO mem : KnGeneralUtil.emptyIfNull(members)) {
            KnCorpGroupMemberDTO contact = new KnCorpGroupMemberDTO();
            contact.setMdn(mem.getMdn());
            contact.setSupervisory(mem.getSupervisor());
            contact.setBroadcaster(mem.getBroadcaster());
            contact.setCallInitiatePermission(mem.getCallInitiatePermission());
            contact.setCallReceivePermission(mem.getCallReceivePermission());
            contact.setInCallPermission(mem.getInCallPermission());
            contact.setVideoCallInitiatePermission(mem.getVideoCallInitiatePermission());
            contact.setVideoCallReceivePermission(mem.getVideoCallReceivePermission());
            contact.setVideoInCallPermission(mem.getVideoInCallPermission());
            contact.setLocWatcher(mem.getLocWatcher());
            contact.setIsOSMAuthorize(mem.getIsOSMAuthorize());
            if (clientType != CLIENT_TYPE_CAT_UI) {
                contact.setGroupModifyPerm(mem.getGrpModifyPerm());
            } else {
                contact.setGroupModifyPerm(DISABLED);
            }
            modifiedMemList.add(contact);
        }
        logger.debug(methodName, "modifiedMemList :: -- ", modifiedMemList);
        return modifiedMemList;
    }

    public Set<KnCorpContactDTO> prepareContactList(Collection<KnXDMGroupMdnInfoDTO> members, int clientType) {
        String methodName = "prepareContactList()";
        var contactList = new HashSet<KnCorpContactDTO>();

        for (KnXDMGroupMdnInfoDTO mem : KnGeneralUtil.emptyIfNull(members)) {
            KnCorpContactDTO contact = new KnCorpContactDTO();
            contact.setMdn(mem.getMdn());
            contact.setSupervisory(mem.getSupervisor());
            contact.setBroadcaster(mem.getBroadcaster());
            contact.setCallInitiatePermission(mem.getCallInitiatePermission());
            contact.setCallReceivePermission(mem.getCallReceivePermission());
            contact.setInCallPermission(mem.getInCallPermission());
            contact.setVideoCallInitiatePermission(mem.getVideoCallInitiatePermission());
            contact.setVideoCallReceivePermission(mem.getVideoCallReceivePermission());
            contact.setVideoInCallPermission(mem.getVideoInCallPermission());
            contact.setLocWatcher(mem.getLocWatcher());
            contact.setIsOSMAuthorize(mem.getIsOSMAuthorize());
            if (clientType != CLIENT_TYPE_CAT_UI) {
                contact.setGroupModifyPerm(mem.getGrpModifyPerm());
            } else {
                contact.setGroupModifyPerm(DISABLED);
            }
            contactList.add(contact);
        }
        logger.debug(methodName, "contactList :: -- ", contactList);
        return contactList;
    }

    /**
     * This method is used to get an IP Corp Group Info DTO from an XDM Corp Group Info DTO and an XDM Bulk Corp Group Info Request DTO.
     * It sets the properties of the IP Corp Group Info DTO based on the properties of the XDM DTOs.
     *
     * @param corpGroupInfoDTO The XDM Corp Group Info DTO.
     * @param xdmRequestDto    The XDM Bulk Corp Group Info Request DTO.
     * @param groupId          The group ID.
     * @return An IP Corp Group Info DTO.
     */
    public KnIPCorpGroupInfoDTO getIpCorpGroupInfoDTO(KnXDMCorpGroupInfoDTO corpGroupInfoDTO, KnXDMBulkCorpGroupInfoRequestDTO xdmRequestDto, Integer groupId) {
        KnIPCorpGroupInfoDTO ipCorpGroupInfoDTO = new KnIPCorpGroupInfoDTO();
        if (corpGroupInfoDTO.getCorpId() != null)
            ipCorpGroupInfoDTO.setCorpId(Integer.valueOf(corpGroupInfoDTO.getCorpId()));
        ipCorpGroupInfoDTO.setClientType(xdmRequestDto.getClientType());
        ipCorpGroupInfoDTO.setUseProfileId(corpGroupInfoDTO.getUserProfileId());
        ipCorpGroupInfoDTO.setGroupId(groupId);
        ipCorpGroupInfoDTO.setETag(Integer.parseInt(corpGroupInfoDTO.getETag()));
        ipCorpGroupInfoDTO.setHierarchyType(xdmRequestDto.getHierarchyType());
        ipCorpGroupInfoDTO.setUpmCall(corpGroupInfoDTO.isUpmCall());
        return ipCorpGroupInfoDTO;
    }

    public KnCorpGrpBasicInfoDTO getCopGrpBasicInfoDTO(KnCorpGrpBasicInfoRespDto grpBasicInfoDto) {
        var grpBasicInfo = new KnCorpGrpBasicInfoDTO();
        grpBasicInfo.setGroupId(grpBasicInfoDto.getGroupId());
        grpBasicInfo.setGrpDisplayName(grpBasicInfoDto.getGrpDisplayName());
        grpBasicInfo.setGrpType(grpBasicInfoDto.getGrpType());
        grpBasicInfo.setGroupListId(grpBasicInfoDto.getGroupListId());
        grpBasicInfo.setGrpEtag(grpBasicInfoDto.getGrpEtag());
        grpBasicInfo.setCorpId(grpBasicInfoDto.getCorpId());
        grpBasicInfo.setLmrInteropCapable(grpBasicInfoDto.getLmrInteropCapable());
        grpBasicInfo.setGrpOwner(grpBasicInfoDto.getGrpOwner());
        grpBasicInfo.setGroupCreatedBy(grpBasicInfoDto.getGroupCreateBy());
        grpBasicInfo.setLargeGroup(grpBasicInfoDto.isLargeGroup());
        grpBasicInfo.setGroupProfileId(grpBasicInfoDto.getGroupProfileId());
        grpBasicInfo.setGrpShared(grpBasicInfoDto.getGrpShared());
        grpBasicInfo.setGroupCorpId(grpBasicInfoDto.getGroupCorpId());
        grpBasicInfo.setOverrideDnd(grpBasicInfoDto.getOverrideDnd());
        grpBasicInfo.setIsPreConfiguredGroup(grpBasicInfoDto.getIsPreConfiguredGroup());
        grpBasicInfo.setUgwInterop(grpBasicInfoDto.getUgwInterop());
        grpBasicInfo.setOldLmrInteropFlag(grpBasicInfoDto.getOldLmrInteropFlag());
        return grpBasicInfo;
    }
}
