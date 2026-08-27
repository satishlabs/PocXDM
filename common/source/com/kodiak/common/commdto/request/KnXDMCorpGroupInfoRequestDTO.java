/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMCorpGroupInfoRequestDTO.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Jan 17, 2011      7.0
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
package com.kodiak.common.commdto.request;

import com.kodiak.common.commdto.common.*;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnGDPRTemplate;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


public class KnXDMCorpGroupInfoRequestDTO extends KnXDMCorpGroupInfoDTO implements IXDMRequestDTO {

    private static final long serialVersionUID = 7526471155622776156L;

    private String operationType;
    private int clientType;
    private IAuthDTO authDTO;
    private String destPttServerId;
    private String destQueueName;
    private String transactionId;
    private Collection<String> addedMdnList;
    private Collection<KnXDMGroupMdnInfoDTO> addedMdnDTOList;
    //Added linked List to make sure the mdnList order is maintained
    private LinkedList<String> removedMdnList;
    private Collection<String> addedSublistIds;
    private Collection<String> removedSublistIds;
    private int groupType;
    private boolean contactPairing;
    private int overrideDND;
    private Integer avatar;
    private Integer hangTimeOut;
    private Integer emergOverrideDND;
    private Integer emergHangTimeAddOn;
    private Integer emergAutoFloorTimer;
    private KnConstants.HIERARCHY_TYPE hierarchyType;
    private String tpVendorID;
	private String tpRequestMdn;
	private int groupCreatedBy;
    private String version;
    private String OSMListId;
    private Integer mcxGrpInd;
    private String mcPttId;
    private Integer videoPermission;
    private String hierarchyId;

    // P7-2: explicit add/remove lists for group sharing (modifyGroup path)
    private List<KnXDMCorpGrpSharedCorpListDTO> addedGrpSharedCorpList;
    private List<KnXDMCorpGrpSharedCorpListDTO> removedGrpSharedCorpList;

    public List<KnXDMCorpGrpSharedCorpListDTO> getAddedGrpSharedCorpList() {
        return addedGrpSharedCorpList;
    }

    public void setAddedGrpSharedCorpList(List<KnXDMCorpGrpSharedCorpListDTO> addedGrpSharedCorpList) {
        this.addedGrpSharedCorpList = addedGrpSharedCorpList;
    }

    public List<KnXDMCorpGrpSharedCorpListDTO> getRemovedGrpSharedCorpList() {
        return removedGrpSharedCorpList;
    }

    public void setRemovedGrpSharedCorpList(List<KnXDMCorpGrpSharedCorpListDTO> removedGrpSharedCorpList) {
        this.removedGrpSharedCorpList = removedGrpSharedCorpList;
    }

    public void setHierarchyId(String hierarchyId) {
        this.hierarchyId=hierarchyId;
    }

    public String getHierarchyId() {
        return hierarchyId;
    }

    private List<KnXDMGroupPropertyInfoDTO> groupPropertyList;

    public List<KnXDMGroupPropertyInfoDTO> getGroupPropertyList() {
        return groupPropertyList;
    }

    public void setGroupPropertyList(List<KnXDMGroupPropertyInfoDTO> groupPropertyList) {
        this.groupPropertyList = groupPropertyList;
    }

    public Integer getVideoPermission() {
        return videoPermission;
    }

    public void setVideoPermission(Integer videoPermission) {
        this.videoPermission = videoPermission;
    }


    public String getMcPttId() {
		return mcPttId;
	}

	public void setMcPttId(String mcPttId) {
		this.mcPttId = mcPttId;
	}

	public Integer getMcxGrpInd() {
		return mcxGrpInd;
	}

	public void setMcxGrpInd(Integer mcxGrpInd) {
		this.mcxGrpInd = mcxGrpInd;
	}

    public String getTpRequestMdn() {
		return tpRequestMdn;
	}

	public String getTpVendorID() {
		return tpVendorID;
	}

	public void setTpVendorID(String tpVendorID) {
		this.tpVendorID = tpVendorID;
	}

	public void setTpRequestMdn(String tpRequestMdn) {
		this.tpRequestMdn = tpRequestMdn;
	}

	public int getGroupCreatedBy() {
		return groupCreatedBy;
	}

	public void setGroupCreatedBy(int groupCreatedBy) {
		this.groupCreatedBy = groupCreatedBy;
	}

	@Override
    public KnConstants.HIERARCHY_TYPE getHierarchyType() {
    	return hierarchyType;
    }

    @Override
    public void setHierarchyType(KnConstants.HIERARCHY_TYPE hierarchyType) {
    	this.hierarchyType = hierarchyType;
    }


    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public int getClientType() {
        return clientType;
    }

    public void setClientType(int clientType) {
        this.clientType = clientType;
    }

    public IAuthDTO getAuthDTO() {
        return authDTO;
    }

    public void setAuthDTO(IAuthDTO authDTO) {
        this.authDTO = authDTO;
    }

    public void setDestPttServerId(String destPttServerId) {
        this.destPttServerId = destPttServerId;
    }

    public String getDestPttServerId() {
        return destPttServerId;
    }

    public void setDestQueueName(String destQueueName) {
        this.destQueueName = destQueueName;
    }

    public String getDestQueueName() {
        return destQueueName;
    }

	public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Collection<String> getAddedMdnList() {
        return addedMdnList;
    }

    public void setAddedMdnList(Collection<String> addedMdnList) {
        this.addedMdnList = addedMdnList;
    }

    public Collection<KnXDMGroupMdnInfoDTO> getAddedMdnDTOList() {
        return addedMdnDTOList;
    }

    public void setAddedMdnDTOList(Collection<KnXDMGroupMdnInfoDTO> addedMdnDTOList) {
        this.addedMdnDTOList = addedMdnDTOList;
    }

    public Collection<String> getAddedSublistIds() {
        return addedSublistIds;
    }

    public void setAddedSublistIds(Collection<String> addedSublistIds) {
        this.addedSublistIds = addedSublistIds;
    }

    public LinkedList<String> getRemovedMdnList() {
        return removedMdnList;
    }

    public void setRemovedMdnList(LinkedList<String> removedMdnList) {
        this.removedMdnList = removedMdnList;
    }

    public Collection<String> getRemovedSublistIds() {
        return removedSublistIds;
    }

    public void setRemovedSublistIds(Collection<String> removedSublistIds) {
        this.removedSublistIds = removedSublistIds;
    }

    public int getGroupType() {
        return groupType;
    }

    public void setGroupType(int groupType) {
        this.groupType = groupType;
    }

    public boolean isContactPairing() {
        return contactPairing;
    }

    public void setContactPairing(boolean contactPairing) {
        this.contactPairing = contactPairing;
    }

    @Override
    public Integer getAvatar() {
        return avatar;
    }

    @Override
    public void setAvatar(Integer avatar) {
        this.avatar = avatar;
    }

    public Integer getHangTimeOut() {
        return hangTimeOut;
    }

    public void setHangTimeOut(Integer hangTimeOut) {
        this.hangTimeOut = hangTimeOut;
    }

    public Integer getEmergOverrideDND() {
        return emergOverrideDND;
    }

    public void setEmergOverrideDND(Integer emergOverrideDND) {
        this.emergOverrideDND = emergOverrideDND;
    }

    public Integer getEmergHangTimeAddOn() {
        return emergHangTimeAddOn;
    }

    public void setEmergHangTimeAddOn(Integer emergHangTimeAddOn) {
        this.emergHangTimeAddOn = emergHangTimeAddOn;
    }

    public Integer getEmergAutoFloorTimer() {
        return emergAutoFloorTimer;
    }

    public void setEmergAutoFloorTimer(Integer emergAutoFloorTimer) {
        this.emergAutoFloorTimer = emergAutoFloorTimer;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getOverrideDND() {
        return overrideDND;
    }

    public void setOverrideDND(int overrideDND) {
        this.overrideDND = overrideDND;
    }

    public String getOSMListId() {
        return OSMListId;
    }

    public void setOSMListId(String OSMListId) {
        this.OSMListId = OSMListId;
    }

    @Override
    public String toString() {
    	StringBuilder builder = new StringBuilder();
    	builder.append("KnXDMCorpGroupInfoRequestDTO[")
    			.append("operationType=").append(operationType)
    			.append(", clientType=").append(clientType)
    			.append(", authDTO=").append(authDTO)
                .append(", destPttServerId='").append(destPttServerId)
                .append(", destQueueName='").append(destQueueName)
                .append(", transactionId='").append(transactionId)
                .append(", addedMdnList=").append(KnGDPRTemplate.mdnList(addedMdnList))
                .append(", addedMdnDTOList=").append(addedMdnDTOList)
                .append(", removedMdnList=").append(KnGDPRTemplate.mdnList(removedMdnList))
                .append(", addedSublistIds=").append(addedSublistIds)
                .append(", removedSublistIds=").append(removedSublistIds)
                .append(", groupType=").append(groupType)
                .append(", contactPairing=").append(contactPairing)
                .append(", avatar=").append(avatar)
                .append(", hangTimeOut=").append(hangTimeOut)
                .append(", emergOverrideDND=").append(emergOverrideDND)
                .append(", emergHangTimeAddOn=").append(emergHangTimeAddOn)
                .append(", emergAutoFloorTimer=").append(emergAutoFloorTimer)
                .append(", hierarchyType=").append(hierarchyType)
                .append(", tpVendorID=").append(tpVendorID)
                .append(", tpRequestMdn=").append(KnGDPRTemplate.mdn(tpRequestMdn))
                .append(", groupCreatedBy=").append(groupCreatedBy)
                .append(", version=").append(version)
                .append(", overrideDND=").append(overrideDND)
                .append(", OSMListId=").append(OSMListId)
                .append(", mcxGrpInd=").append(mcxGrpInd)
                .append(", groupPropertyList=").append(groupPropertyList)
                .append(" videoPermission =").append(videoPermission)
                .append(", mcPttId=").append(KnGDPRTemplate.mcpttId(mcPttId))
                .append(", addedGrpSharedCorpList=").append(addedGrpSharedCorpList)
                .append(", removedGrpSharedCorpList=").append(removedGrpSharedCorpList)
                .append(" KnXDMCorpGroupInfoDTO =").append(super.toString())
                .append("]");
    	return builder.toString();
    }
}
