/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.request;

import com.kodiak.common.commdto.common.IAuthDTO;
import com.kodiak.common.commdto.common.KnXDMContactListDTO;
import com.kodiak.common.commdto.common.KnXDMMdnInfoDTO;
import com.kodiak.common.resources.KnConstants;

import java.util.List;

public class KnXDMDynContactInfoDTO extends KnXDMContactListDTO implements IXDMRequestDTO {


    private static final long serialVersionUID = 7526471155628765656L;

    private String operationType;
    private int clientType;
    private IAuthDTO authDTO;
    private String objectId;
    private String destPttServerId;
    private String destQueueName;
    private String transactionId;
    private KnConstants.HIERARCHY_TYPE hierarchyType;
    //Variable added for dynamic contact - modified contact list
    private List<KnXDMMdnInfoDTO> modifiedContList;
    //Variable added for dynamic contact - removed contacts
    private List<String> removedContList;

    private String vendorId;
    private String version;

    public List<KnXDMMdnInfoDTO> getModifiedContList() {
        return modifiedContList;
    }

    public void setModifiedContList(List<KnXDMMdnInfoDTO> modifiedContList) {
        this.modifiedContList = modifiedContList;
    }

    public List<String> getRemovedContList() {
        return removedContList;
    }

    public void setRemovedContList(List<String> removedContList) {
        this.removedContList = removedContList;
    }



    @Override
    public String getOperationType() {
        return this.operationType;
    }

    @Override
    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    @Override
    public int getClientType() {
        return this.clientType;
    }

    @Override
    public void setClientType(int clientType) {
        this.clientType = clientType;
    }

    @Override
    public IAuthDTO getAuthDTO() {
        return this.authDTO;
    }

    @Override
    public void setAuthDTO(IAuthDTO authDTO) {
        this.authDTO = authDTO;
    }

    @Override
    public void setDestPttServerId(String destPttServerId) {
        this.destPttServerId = destPttServerId;
    }

    @Override
    public String getDestPttServerId() {
        return this.destPttServerId;
    }

    @Override
    public void setDestQueueName(String destQueueName) {
        this.destQueueName = destQueueName;
    }

    @Override
    public String getDestQueueName() {
        return this.destQueueName;
    }

    @Override
    public String getTransactionId() {
        return this.transactionId;
    }

    @Override
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    @Override
    public void setHierarchyType(KnConstants.HIERARCHY_TYPE hierarchyType) {
        this.hierarchyType = hierarchyType;
    }

    @Override
    public KnConstants.HIERARCHY_TYPE getHierarchyType() {
        return this.hierarchyType;
    }

    @Override
    public String getObjectId() {
        return this.objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("KnXDMDynContactInfoDTO{");
        sb.append("modifiedContList ='").append(modifiedContList).append('\'');
        sb.append(", removedContList='").append(removedContList).append('\'');
        sb.append(", vendorId='").append(vendorId).append('\'');
        sb.append(", version='").append(version).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
