/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.clientdat;

import com.kodiak.common.commdto.common.KnXDMCorpContactDTO;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.xdms.server.common.dto.intf.IAuthDTO;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpGroupDTO;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12/5/11
 * Time: 7:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class KnIPCorpDispatchGrpMemInfoDto extends KnCorpGroupDTO implements IInputDTO {

    private static final long serialVersionUID = 7526471155622676188L;

    private Collection<KnXDMCorpContactDTO> groupMemberListToChk;
    private Collection<KnXDMCorpContactDTO> addedGroupMembers;
    private Collection<KnXDMCorpContactDTO> deletedGroupMembers;
    private String performer;
    private IAuthDTO authDTO;
    private int clientType;
    private String operationType;
    private String entityId;
    private String profile;
    private String updatedMdn;
    private String toMdn;
    private Collection<Integer> grpIds;

    public String getPerformer() {
        return performer;
    }

    public void setPerformer(String performer) {
        this.performer = performer;
    }

    public IAuthDTO getAuthDTO() {
        return authDTO;
    }

    public void setAuthDTO(IAuthDTO authDTO) {
        this.authDTO = authDTO;
    }

    public int getClientType() {
        return clientType;
    }

    public void setClientType(int clientType) {
        this.clientType = clientType;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public Collection<KnXDMCorpContactDTO> getGroupMemberListToChk() {
        return groupMemberListToChk;
    }

    public void setGroupMemberListToChk(Collection<KnXDMCorpContactDTO> groupMemberListToChk) {
        this.groupMemberListToChk = groupMemberListToChk;
    }

    public Collection<KnXDMCorpContactDTO> getAddedGroupMembers() {
        return addedGroupMembers;
    }

    public void setAddedGroupMembers(Collection<KnXDMCorpContactDTO> addedGroupMembers) {
        this.addedGroupMembers = addedGroupMembers;
    }

    public Collection<KnXDMCorpContactDTO> getDeletedGroupMembers() {
        return deletedGroupMembers;
    }

    public void setDeletedGroupMembers(Collection<KnXDMCorpContactDTO> deletedGroupMembers) {
        this.deletedGroupMembers = deletedGroupMembers;
    }

    public String getUpdatedMdn() {
        return updatedMdn;
    }

    public void setUpdatedMdn(String updatedMdn) {
        this.updatedMdn = updatedMdn;
    }

    public String getToMdn() {
        return toMdn;
    }

    public void setToMdn(String toMdn) {
        this.toMdn = toMdn;
    }

    public Collection<Integer> getGrpIds() {
        return grpIds;
    }

    public void setGrpIds(Collection<Integer> grpIds) {
        this.grpIds = grpIds;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(400);
        sb.append(super.toString())
                .append(", toMdn - ").append(KnGDPRTemplate.mdn(toMdn))
                .append(", groupMemberListToChk - ").append(groupMemberListToChk)
                .append(", addedGroupMembers - ").append(addedGroupMembers)
                .append(", deletedGroupMembers - ").append(deletedGroupMembers)
                .append(", updatedMdn - ").append(KnGDPRTemplate.mdn(updatedMdn))
                .append(", grpIds - ").append(grpIds);
        return sb.toString();
    }
}
