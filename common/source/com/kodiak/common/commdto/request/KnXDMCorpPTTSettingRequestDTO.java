package com.kodiak.common.commdto.request;

import com.kodiak.common.commdto.common.IAuthDTO;
import com.kodiak.common.commdto.common.KnXDMCorpInfo;
import com.kodiak.common.commdto.common.KnXDMPTTSettingHierarchyListDTO;

import java.util.List;
import java.util.Map;

public class KnXDMCorpPTTSettingRequestDTO extends KnXDMCorpInfo implements IXDMRequestDTO{
    private static final long serialVersionUID = -7152562662234833838L;

    private String operationType;
    private int clientType;
    private IAuthDTO authDTO;
    private String destPttServerId;
    private String destQueueName;
    private String transactionId;
    private String corpId;
    private String hierarchyId;
    private String pttSettingId;
    private String pttSettingName;
    private String mdn;
    private Map<String, List<Integer>> pttSettingInfo;
    private List<KnXDMPTTSettingHierarchyListDTO> pttSettingDocList;
    private List<String> mdnList;
    private int isDefaultDoc;

    @Override
    public String getOperationType() {
        return operationType;
    }

    @Override
    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    @Override
    public int getClientType() {
        return clientType;
    }

    @Override
    public void setClientType(int clientType) {
        this.clientType = clientType;
    }

    @Override
    public IAuthDTO getAuthDTO() {
        return authDTO;
    }

    @Override
    public void setAuthDTO(IAuthDTO authDTO) {
        this.authDTO = authDTO;
    }

    @Override
    public String getDestPttServerId() {
        return destPttServerId;
    }

    @Override
    public void setDestPttServerId(String destPttServerId) {
        this.destPttServerId = destPttServerId;
    }

    @Override
    public String getDestQueueName() {
        return destQueueName;
    }

    @Override
    public void setDestQueueName(String destQueueName) {
        this.destQueueName = destQueueName;
    }

    @Override
    public String getTransactionId() {
        return transactionId;
    }

    @Override
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    @Override
    public String getCorpId() {return corpId;}

    @Override
    public void setCorpId(String corpId) {this.corpId = corpId;}

    public String getHierarchyId() { return hierarchyId;}

    public void setHierarchyId(String hierarchyId) { this.hierarchyId = hierarchyId;}

    public String getPttSettingId() { return pttSettingId; }

    public void setPttSettingId(String pttSettingId) { this.pttSettingId = pttSettingId; }

    public String getPttSettingName() {return pttSettingName;}

    public void setPttSettingName(String pttSettingName) { this.pttSettingName = pttSettingName;}

    public String getMdn() { return mdn; }

    public void setMdn(String mdn) { this.mdn = mdn; }

    public Map<String, List<Integer>> getPttSettingInfo() { return pttSettingInfo; }

    public void setPttSettingInfo(Map<String, List<Integer>> pttSettingInfo) { this.pttSettingInfo = pttSettingInfo; }

    public List<KnXDMPTTSettingHierarchyListDTO> getPttSettingDocList() {
        return pttSettingDocList;
    }

    public void setPttSettingDocList(List<KnXDMPTTSettingHierarchyListDTO> pttSettingDocList) {
        this.pttSettingDocList = pttSettingDocList;
    }

    public List<String> getMdnList() {
        return mdnList;
    }

    public void setMdnList(List<String> mdnList) {
        this.mdnList = mdnList;
    }

    public int getIsDefaultDoc() {
        return isDefaultDoc;
    }

    public void setIsDefaultDoc(int isDefaultDoc) {
        this.isDefaultDoc = isDefaultDoc;
    }

    @Override
    public String toString() {
        return "KnXDMCorpPTTSettingRequestDTO{" +
                "operationType='" + operationType + '\'' +
                ", clientType=" + clientType +
                ", authDTO=" + authDTO +
                ", destPttServerId='" + destPttServerId + '\'' +
                ", destQueueName='" + destQueueName + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", corpId='" + corpId + '\'' +
                ", hierarchyId='" + hierarchyId + '\'' +
                ", pttSettingId='" + pttSettingId + '\'' +
                ", pttSettingName='" + pttSettingName + '\'' +
                ", mdn='" + mdn + '\'' +
                ", pttSettingInfo=" + pttSettingInfo +
                ", pttSettingDocList=" + pttSettingDocList +
                ", mdnList=" + mdnList +
                ", isDefaultDoc=" + isDefaultDoc +
                '}';
    }
}
