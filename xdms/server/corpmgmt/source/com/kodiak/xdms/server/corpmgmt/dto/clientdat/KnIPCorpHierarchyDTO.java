package com.kodiak.xdms.server.corpmgmt.dto.clientdat;

import com.kodiak.common.commdto.common.KnIdDetailsListDTO;
import com.kodiak.common.commdto.common.KnModifiedIdDetailsListDTO;

import java.util.List;
import java.util.Map;

public class KnIPCorpHierarchyDTO extends KnIPCorpInfoDTO{
    private KnIdDetailsListDTO idDetailsListDTO;

    private List<KnModifiedIdDetailsListDTO> modifiedIdDetails;

    private String transactionId;

    private Map<String,Integer> maxHierarchyHorizonatlMap;
    private Map<String,Integer> maxHierarchyVerticalMap;
    private Map<String, Integer> childHierarchyDepthMap;
    private Map<String, Map<Integer, Integer>> clusterIdSubscriberMap;
    private Map<String, String> hierarchyIdNameMap;

    public KnIdDetailsListDTO getIdDetailsListDTO() {
        return idDetailsListDTO;
    }

    public void setIdDetailsListDTO(KnIdDetailsListDTO idDetailsListDTO) {
        this.idDetailsListDTO = idDetailsListDTO;
    }

    public List<KnModifiedIdDetailsListDTO> getModifiedIdDetails() {
        return modifiedIdDetails;
    }

    public void setModifiedIdDetails(List<KnModifiedIdDetailsListDTO> modifiedIdDetails) {
        this.modifiedIdDetails = modifiedIdDetails;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Map<String, Integer> getMaxHierarchyHorizonatlMap() {
        return maxHierarchyHorizonatlMap;
    }

    public void setMaxHierarchyHorizonatlMap(Map<String, Integer> maxHierarchyHorizonatlMap) {
        this.maxHierarchyHorizonatlMap = maxHierarchyHorizonatlMap;
    }

    public Map<String, Integer> getMaxHierarchyVerticalMap() {
        return maxHierarchyVerticalMap;
    }

    public void setMaxHierarchyVerticalMap(Map<String, Integer> maxHierarchyVerticalMap) {
        this.maxHierarchyVerticalMap = maxHierarchyVerticalMap;
    }

    public Map<String, Integer> getChildHierarchyDepthMap() {
        return childHierarchyDepthMap;
    }

    public void setChildHierarchyDepthMap(Map<String, Integer> childHierarchyDepthMap) {
        this.childHierarchyDepthMap = childHierarchyDepthMap;
    }

    public Map<String, Map<Integer, Integer>> getClusterIdSubscriberMap() {
        return clusterIdSubscriberMap;
    }

    public void setClusterIdSubscriberMap(Map<String, Map<Integer, Integer>> clusterIdSubscriberMap) {
        this.clusterIdSubscriberMap = clusterIdSubscriberMap;
    }

    public Map<String, String> getHierarchyIdNameMap() {
        return hierarchyIdNameMap;
    }

    public void setHierarchyIdNameMap(Map<String, String> hierarchyIdNameMap) {
        this.hierarchyIdNameMap = hierarchyIdNameMap;
    }

    @Override
    public String toString() {
        return "KnIPCorpHierarchyDTO{" +
                "idDetailsListDTO=" + idDetailsListDTO +
                ", modifiedIdDetails=" + modifiedIdDetails +
                ", transactionId='" + transactionId + '\'' +
                ", maxHierarchyHorizonatlMap=" + maxHierarchyHorizonatlMap +
                ", maxHierarchyVerticalMap=" + maxHierarchyVerticalMap +
                ", childHierarchyDepthMap=" + childHierarchyDepthMap +
                ", clusterIdSubscribermap=" + clusterIdSubscriberMap +
                ", hierarchyIdNameMap=" + hierarchyIdNameMap +
                '}';
    }
}
