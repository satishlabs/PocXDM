package com.kodiak.xdms.server.corpmgmt.dto.impl;

import com.kodiak.xdms.server.common.dto.common.KnCorpProfileDTO;
import com.kodiak.xdms.server.common.dto.common.KnSubsProfileDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpContactDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGroupMemberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpBulkGroupInfoPersistDTO;

import java.util.*;

public class KnBulkGroupCloningDTO {
    private Collection<KnCorpSubscriberDTO> groupPrivateList;
    private Collection<Integer> finalGroupToBeRemovedFromTargetMdn;
    private String toMdn;
    private String xdmsHome;
    private Map<Integer, KnCorpGroupDTO> groupDetailsMap;
    private Map<Integer, String> groupDistPopulationToAddToTarget;
    private Map<Integer, KnCorpContactDTO> groupMemberInsertList;
    private Map<Integer, List<String>> groupDistPopulationToRemoveFromTarget;
    private LinkedHashMap<Integer, LinkedList<String>> groupMemberDeleteList;
    private Map<Integer, Collection<KnCorpGroupMemberDTO>> supervisorMemberListMap;
    private KnCorpBulkGroupInfoPersistDTO persistDTO;
    private Map<Integer, KnCorpGroupDTO> groupDetailsMapOfFinalGroupToBeClonedToTargetMdn;
    private Map<Integer, KnCorpGroupDTO> groupDetailsMapOfFinalGroupToBeRemovedFromTargetMdn;
    private KnCorpProfileDTO corpProfile;
    private Set<String> mdnListForDirectory;
    private Set<String> mdnListForLrgGrp;
    private Collection<Integer> broadCastGrpList;
    private Map<Integer, Collection<String>> groupDistList;
    private com.kodiak.xdms.server.common.dto.common.KnSubsProfileDTO toMdnSubsProfile;

    public Collection<KnCorpSubscriberDTO> getGroupPrivateList() {
        return groupPrivateList;
    }

    public void setGroupPrivateList(Collection<KnCorpSubscriberDTO> groupPrivateList) {
        this.groupPrivateList = groupPrivateList;
    }

    public Collection<Integer> getFinalGroupToBeRemovedFromTargetMdn() {
        return finalGroupToBeRemovedFromTargetMdn;
    }

    public void setFinalGroupToBeRemovedFromTargetMdn(Collection<Integer> finalGroupToBeRemovedFromTargetMdn) {
        this.finalGroupToBeRemovedFromTargetMdn = finalGroupToBeRemovedFromTargetMdn;
    }

    public String getToMdn() {
        return toMdn;
    }

    public void setToMdn(String toMdn) {
        this.toMdn = toMdn;
    }

    public Map<Integer, KnCorpGroupDTO> getGroupDetailsMap() {
        return groupDetailsMap;
    }

    public void setGroupDetailsMap(Map<Integer, KnCorpGroupDTO> groupDetailsMap) {
        this.groupDetailsMap = groupDetailsMap;
    }

    public Map<Integer, String> getGroupDistPopulationToAddToTarget() {
        return groupDistPopulationToAddToTarget;
    }

    public void setGroupDistPopulationToAddToTarget(Map<Integer, String> groupDistPopulationToAddToTarget) {
        this.groupDistPopulationToAddToTarget = groupDistPopulationToAddToTarget;
    }

    public Map<Integer, KnCorpContactDTO> getGroupMemberInsertList() {
        return groupMemberInsertList;
    }

    public void setGroupMemberInsertList(Map<Integer, KnCorpContactDTO> groupMemberInsertList) {
        this.groupMemberInsertList = groupMemberInsertList;
    }

    public Map<Integer, List<String>> getGroupDistPopulationToRemoveFromTarget() {
        return groupDistPopulationToRemoveFromTarget;
    }

    public void setGroupDistPopulationToRemoveFromTarget(Map<Integer, List<String>> groupDistPopulationToRemoveFromTarget) {
        this.groupDistPopulationToRemoveFromTarget = groupDistPopulationToRemoveFromTarget;
    }

    public LinkedHashMap<Integer, LinkedList<String>> getGroupMemberDeleteList() {
        return groupMemberDeleteList;
    }

    public void setGroupMemberDeleteList(LinkedHashMap<Integer, LinkedList<String>> groupMemberDeleteList) {
        this.groupMemberDeleteList = groupMemberDeleteList;
    }

    public Map<Integer, Collection<KnCorpGroupMemberDTO>> getSupervisorMemberListMap() {
        return supervisorMemberListMap;
    }

    public void setSupervisorMemberListMap(Map<Integer, Collection<KnCorpGroupMemberDTO>> supervisorMemberListMap) {
        this.supervisorMemberListMap = supervisorMemberListMap;
    }

    public KnCorpBulkGroupInfoPersistDTO getPersistDTO() {
        return persistDTO;
    }

    public void setPersistDTO(KnCorpBulkGroupInfoPersistDTO persistDTO) {
        this.persistDTO = persistDTO;
    }

    public Map<Integer, KnCorpGroupDTO> getGroupDetailsMapOfFinalGroupToBeClonedToTargetMdn() {
        return groupDetailsMapOfFinalGroupToBeClonedToTargetMdn;
    }

    public void setGroupDetailsMapOfFinalGroupToBeClonedToTargetMdn(Map<Integer, KnCorpGroupDTO> groupDetailsMapOfFinalGroupToBeClonedToTargetMdn) {
        this.groupDetailsMapOfFinalGroupToBeClonedToTargetMdn = groupDetailsMapOfFinalGroupToBeClonedToTargetMdn;
    }

    public Map<Integer, KnCorpGroupDTO> getGroupDetailsMapOfFinalGroupToBeRemovedFromTargetMdn() {
        return groupDetailsMapOfFinalGroupToBeRemovedFromTargetMdn;
    }

    public void setGroupDetailsMapOfFinalGroupToBeRemovedFromTargetMdn(Map<Integer, KnCorpGroupDTO> groupDetailsMapOfFinalGroupToBeRemovedFromTargetMdn) {
        this.groupDetailsMapOfFinalGroupToBeRemovedFromTargetMdn = groupDetailsMapOfFinalGroupToBeRemovedFromTargetMdn;
    }

    public KnCorpProfileDTO getCorpProfile() {
        return corpProfile;
    }

    public void setCorpProfile(KnCorpProfileDTO corpProfile) {
        this.corpProfile = corpProfile;
    }

    public Set<String> getMdnListForDirectory() {
        return mdnListForDirectory;
    }

    public void setMdnListForDirectory(Set<String> mdnListForDirectory) {
        this.mdnListForDirectory = mdnListForDirectory;
    }

    public Set<String> getMdnListForLrgGrp() {
        return mdnListForLrgGrp;
    }

    public void setMdnListForLrgGrp(Set<String> mdnListForLrgGrp) {
        this.mdnListForLrgGrp = mdnListForLrgGrp;
    }

    public Collection<Integer> getBroadCastGrpList() {
        return broadCastGrpList;
    }

    public void setBroadCastGrpList(Collection<Integer> broadCastGrpList) {
        this.broadCastGrpList = broadCastGrpList;
    }

    public Map<Integer, Collection<String>> getGroupDistList() {
        return groupDistList;
    }

    public void setGroupDistList(Map<Integer, Collection<String>> groupDistList) {
        this.groupDistList = groupDistList;
    }

    public KnSubsProfileDTO getToMdnSubsProfile() {
        return toMdnSubsProfile;
    }

    public void setToMdnSubsProfile(KnSubsProfileDTO toMdnSubsProfile) {
        this.toMdnSubsProfile = toMdnSubsProfile;
    }

    public String getXdmsHome() {
        return xdmsHome;
    }

    public void setXdmsHome(String xdmsHome) {
        this.xdmsHome = xdmsHome;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("KnBulkGroupCloningDTO{");
        sb.append("groupPrivateList=").append(groupPrivateList);
        sb.append(", finalGroupToBeRemovedFromTargetMdn=").append(finalGroupToBeRemovedFromTargetMdn);
        sb.append(", toMdn='").append(toMdn).append('\'');
        sb.append(", groupDetailsMap=").append(groupDetailsMap);
        sb.append(", groupDistPopulationToAddToTarget=").append(groupDistPopulationToAddToTarget);
        sb.append(", groupMemberInsertList=").append(groupMemberInsertList);
        sb.append(", groupDistPopulationToRemoveFromTarget=").append(groupDistPopulationToRemoveFromTarget);
        sb.append(", groupMemberDeleteList=").append(groupMemberDeleteList);
        sb.append(", supervisorMemberListMap=").append(supervisorMemberListMap);
        sb.append(", persistDTO=").append(persistDTO);
        sb.append(", groupDetailsMapOfFinalGroupToBeClonedToTargetMdn=").append(groupDetailsMapOfFinalGroupToBeClonedToTargetMdn);
        sb.append(", groupDetailsMapOfFinalGroupToBeRemovedFromTargetMdn=").append(groupDetailsMapOfFinalGroupToBeRemovedFromTargetMdn);
        sb.append(", corpProfile=").append(corpProfile);
        sb.append(", mdnListForDirectory=").append(mdnListForDirectory);
        sb.append(", mdnListForLrgGrp=").append(mdnListForLrgGrp);
        sb.append(", broadCastGrpList=").append(broadCastGrpList);
        sb.append(", groupDistList=").append(groupDistList);
        sb.append(", toMdnSubsProfile=").append(toMdnSubsProfile);
        sb.append('}');
        return sb.toString();
    }

}


