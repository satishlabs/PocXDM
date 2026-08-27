/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.dto.clientdat;

import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.xdms.server.common.dto.intf.IAuthDTO;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;

import java.util.List;
import java.util.Map;

/**
 * Created by venkata sudhakar talluri on 28-12-2018
 */

public class KnIPTGSSListDTO implements IInputDTO {

    private String performer;
    private IAuthDTO authDTO;
    private int clientType;
    private String operationType;
    private String entityId;
    private String profile;
    private String docEtag;
    protected List<Integer> groupIds;
    protected int corpId;
    protected String mdn;
    private int ifMatch;
    private int ifNoneMatch;
    protected String mcpttId;
    private Map<Integer,Integer> groupIdCorpInfo;

    public String getDocEtag() {
        return docEtag;
    }

    public void setDocEtag(String docEtag) {
        this.docEtag = docEtag;
    }

    public List<Integer> getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(List<Integer> groupIds) {
        this.groupIds = groupIds;
    }

    public int getCorpId() {
        return corpId;
    }

    public void setCorpId(int corpId) {
        this.corpId = corpId;
    }

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    @Override
    public String getPerformer() { return performer; }

    @Override
    public void setPerformer(String performer) { this.performer = performer; }

    @Override
    public IAuthDTO getAuthDTO() { return authDTO; }

    @Override
    public void setAuthDTO(IAuthDTO authDTO) { this.authDTO = authDTO; }

    @Override
    public int getClientType() { return clientType; }

    @Override
    public void setClientType(int clientType) { this.clientType = clientType; }

    @Override
    public String getOperationType() { return operationType; }

    @Override
    public void setOperationType(String operationType) {  this.operationType = operationType; }

    @Override
    public String getEntityId() { return entityId; }

    @Override
    public void setEntityId(String entityId) { this.entityId = entityId; }

    @Override
    public String getProfile() { return profile; }

    @Override
    public void setProfile(String profile) { this.profile = profile; }

    @Override
    public String getObjectId() {

        return mdn;
    }

    public int getIfMatch() {
        return ifMatch;
    }

    public void setIfMatch(int ifMatch) {
        this.ifMatch = ifMatch;
    }

    public int getIfNoneMatch() {
        return ifNoneMatch;
    }

    public void setIfNoneMatch(int ifNoneMatch) {
        this.ifNoneMatch = ifNoneMatch;
    }

    public String getMcpttId() {
		return mcpttId;
	}

	public void setMcpttId(String mcpttId) {
		this.mcpttId = mcpttId;
	}

    public Map<Integer, Integer> getGroupIdCorpInfo() { return groupIdCorpInfo; }

    public void setGroupIdCorpInfo(Map<Integer, Integer> groupIdCorpInfo) { this.groupIdCorpInfo = groupIdCorpInfo; }

    @Override
    public String toString() {

        StringBuffer strBuffer = new StringBuffer(100);
        strBuffer.append(super.toString());

        strBuffer.append(", Performer - ").append(performer);
        strBuffer.append(", AuthDTO - ").append(authDTO);
        strBuffer.append(", ClientType - ").append(clientType);
        strBuffer.append(", OperationType - ").append(operationType);
        strBuffer.append(", EntityId - ").append(entityId);
        strBuffer.append(", Profile - ").append(profile);

        strBuffer.append(", Mdn - ").append(KnGDPRTemplate.mdn(mdn));
        strBuffer.append(", corpId - ").append(corpId);
        strBuffer.append(", groupIds - ").append(groupIds);
        strBuffer.append(", mcpttId - ").append(KnGDPRTemplate.mcpttId(mcpttId));
        strBuffer.append(", groupIdCorpInfo - ").append(groupIdCorpInfo);

        return strBuffer.toString();
    }
}
