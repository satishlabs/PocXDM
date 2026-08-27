/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:
 * Subsystem:  POC
 * <p/>
 * Name                   Date         Release
 * -------------------- ------------ -------------------------------------
 * Sanjiv K Acharyya     29/10/13         7.7.0
 * <p/>
 * <p/>
 * #401, 4th Floor, 'Prestige Sigma'
 * No.3, Vittal Mallya Road
 * Bangalore - 560 001
 * www.kodiaknetworks.com
 * All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */
package com.kodiak.xdms.server.corpmgmt.dto.clientdat;

import com.kodiak.common.commdto.request.KnXDMTalkGroupInfoDTO;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.xdms.server.common.dto.intf.IAuthDTO;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpAddlTGInfoDTO;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class KnIPTalkGroupDTO implements IInputDTO {
    private static final long serialVersionUID = 987123457786798009L;

    private String performer;
    private IAuthDTO authDTO;
    private int clientType;
    private String operationType;
    private String entityId;
    private String profile;
    private int ifMatch;
    private int ifNoneMatch;
    private String mdn;
    private List<Integer> assigedGrpList;
    private List<Integer> deAssignedGrpList;
    private String corpId;
    private Map<String, Object> customParamMap;
    private String deleteType;
    private int camped_by;
    private List<KnXDMTalkGroupInfoDTO> addedCampGrpList;
    private List<KnXDMTalkGroupInfoDTO> modifiedCampGrpList;
    private List<KnXDMTalkGroupInfoDTO> removedCammpGrpList;
    private Integer mode;
    private String etag;
    private KnConstants.HIERARCHY_TYPE hierarchyType;
    private Collection<KnCorpAddlTGInfoDTO> addedAddlTgList;
    private Collection<KnCorpAddlTGInfoDTO> modifiedAddlTgList;
    private Collection<KnCorpAddlTGInfoDTO> removedAddlTgList;
    private boolean calledFromModifyUPM;
    private boolean upmCall;
    private String mcPttId;

    public String getMcPttId() {
		return mcPttId;
	}

	public void setMcPttId(String mcPttId) {
		this.mcPttId = mcPttId;
	}

	public KnConstants.HIERARCHY_TYPE getHierarchyType() {
	    return hierarchyType;
	}

	public void setHierarchyType(KnConstants.HIERARCHY_TYPE hierarchyType) {
	    this.hierarchyType = hierarchyType;
	}


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

    public String getObjectId() {
        return null;
    }

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    public List<Integer> getAssigedGrpList() {
        return assigedGrpList;
    }

    public void setAssigedGrpList(List<Integer> assigedGrpList) {
        this.assigedGrpList = assigedGrpList;
    }

    public List<Integer> getDeAssignedGrpList() {
        return deAssignedGrpList;
    }

    public void setDeAssignedGrpList(List<Integer> deAssignedGrpList) {
        this.deAssignedGrpList = deAssignedGrpList;
    }

    public String getCorpId() {
		return corpId;
	}

	public void setCorpId(String corpId) {
		this.corpId = corpId;
	}

	public Map<String, Object> getCustomParamMap() {
        return customParamMap;
    }

    public void setCustomParamMap(Map<String, Object> customParamMap) {
        this.customParamMap = customParamMap;
    }

    public String getDeleteType() {
        return deleteType;
    }

    public void setDeleteType(String deleteType) {
        this.deleteType = deleteType;
    }

    public int getCamped_by() {
        return camped_by;
    }

    public void setCamped_by(int camped_by) {
        this.camped_by = camped_by;
    }


    public List<KnXDMTalkGroupInfoDTO> getAddedCampGrpList() {
		return addedCampGrpList;
	}

	public void setAddedCampGrpList(List<KnXDMTalkGroupInfoDTO> addedCampGrpList) {
		this.addedCampGrpList = addedCampGrpList;
	}

	public List<KnXDMTalkGroupInfoDTO> getModifiedCampGrpList() {
		return modifiedCampGrpList;
	}

	public void setModifiedCampGrpList(List<KnXDMTalkGroupInfoDTO> modifiedCampGrpList) {
		this.modifiedCampGrpList = modifiedCampGrpList;
	}

	public List<KnXDMTalkGroupInfoDTO> getRemovedCammpGrpList() {
		return removedCammpGrpList;
	}

	public void setRemovedCammpGrpList(List<KnXDMTalkGroupInfoDTO> removedCammpGrpList) {
		this.removedCammpGrpList = removedCammpGrpList;
	}

    public Integer getMode() {
        return mode;
    }

    public void setMode(Integer mode) {
        this.mode = mode;
    }

	public String getEtag() {
		return etag;
	}

	public void setEtag(String etag) {
		this.etag = etag;
	}

    public Collection<KnCorpAddlTGInfoDTO> getAddedAddlTgList() {
        return addedAddlTgList;
    }

    public void setAddedAddlTgList(Collection<KnCorpAddlTGInfoDTO> addedAddlTgList) {
        this.addedAddlTgList = addedAddlTgList;
    }

    public Collection<KnCorpAddlTGInfoDTO> getModifiedAddlTgList() {
        return modifiedAddlTgList;
    }

    public void setModifiedAddlTgList(Collection<KnCorpAddlTGInfoDTO> modifiedAddlTgList) {
        this.modifiedAddlTgList = modifiedAddlTgList;
    }

    public Collection<KnCorpAddlTGInfoDTO> getRemovedAddlTgList() {
        return removedAddlTgList;
    }

    public void setRemovedAddlTgList(Collection<KnCorpAddlTGInfoDTO> removedAddlTgList) {
        this.removedAddlTgList = removedAddlTgList;
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

    public boolean isCalledFromModifyUPM() {
		return calledFromModifyUPM;
	}

	public void setCalledFromModifyUPM(boolean calledFromModifyUPM) {
		this.calledFromModifyUPM = calledFromModifyUPM;
	}

    public boolean isUpmCall() {
        return upmCall;
    }

    public void setUpmCall(boolean upmCall) {
        this.upmCall = upmCall;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(50);
        sb.append("corpId ").append(corpId);
        sb.append(",mdn ").append(KnGDPRTemplate.mdn(mdn));
        sb.append(",addedCampGrpList ").append(addedCampGrpList);
        sb.append(",modifiedCampGrpList ").append(modifiedCampGrpList);
        sb.append(",removedCampGrpList ").append(removedCammpGrpList);
        sb.append(",assigedGrpList ").append(assigedGrpList);
        sb.append(",deAssignedGrpList ").append(deAssignedGrpList);
        sb.append(",customParamMap ").append(customParamMap);
        sb.append(",deleteType ").append(deleteType);
        sb.append(",camped_by ").append(camped_by);
        sb.append(",addedAddlTgList ").append(addedAddlTgList);
        sb.append(",modifiedAddlTgList ").append(modifiedAddlTgList);
        sb.append(",removedAddlTgList ").append(removedAddlTgList);
        sb.append(",calledFromModifyUPM ").append(calledFromModifyUPM);
        sb.append(",upmCall ").append(upmCall);
        sb.append(",mcPttId ").append(KnGDPRTemplate.mcpttId(mcPttId));
        sb.append(",scanMode ").append(mode);
        return sb.toString();
    }
}
