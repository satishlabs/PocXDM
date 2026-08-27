package com.kodiak.xdms.mediator.resources;

import com.kodiak.xdms.server.corpmgmt.dto.common.KnSubsDestEmergencyAttributes;

import java.util.List;

public class KnInconsistenUPMDataDTO {
    private static List<String> profileMdnswithNoBaseMDN;

    private static List<String> addGroupIds;

    private static List<String> removeGroupIds;

    private static List<String> addContacts;

    private static List<String> removeContacts;

    private KnSubsDestEmergencyAttributes AddDestEmergencyAttributes;

    private KnSubsDestEmergencyAttributes removeDestEmergencyAttributes;
    public String updateUPMFS;

    public List<String> getProfileMdnswithNoBaseMDN() {
        return profileMdnswithNoBaseMDN;
    }

    public void setProfileMdnswithNoBaseMDN(List<String> profileMdnswithNoBaseMDN) {
        KnInconsistenUPMDataDTO.profileMdnswithNoBaseMDN = profileMdnswithNoBaseMDN;
    }

    public List<String> getAddGroupIds() {
        return addGroupIds;
    }

    public void setAddGroupIds(List<String> addGroupIds) {
        KnInconsistenUPMDataDTO.addGroupIds = addGroupIds;
    }

    public List<String> getRemoveGroupIds() {
        return removeGroupIds;
    }

    public void setRemoveGroupIds(List<String> removeGroupIds) {
        KnInconsistenUPMDataDTO.removeGroupIds = removeGroupIds;
    }

    public List<String> getAddContacts() {
        return addContacts;
    }

    public void setAddContacts(List<String> addContacts) {
        KnInconsistenUPMDataDTO.addContacts = addContacts;
    }

    public List<String> getRemoveContacts() {
        return removeContacts;
    }

    public void setRemoveContacts(List<String> removeContacts) {
        KnInconsistenUPMDataDTO.removeContacts = removeContacts;
    }

    public KnSubsDestEmergencyAttributes getAddDestEmergencyAttributes() {
        return AddDestEmergencyAttributes;
    }

    public void setAddDestEmergencyAttributes(KnSubsDestEmergencyAttributes addDestEmergencyAttributes) {
        this.AddDestEmergencyAttributes = addDestEmergencyAttributes;
    }

    public KnSubsDestEmergencyAttributes getRemoveDestEmergencyAttributes() {
        return removeDestEmergencyAttributes;
    }

    public void setRemoveDestEmergencyAttributes(KnSubsDestEmergencyAttributes removeDestEmergencyAttributes) {
        this.removeDestEmergencyAttributes = removeDestEmergencyAttributes;
    }

    public String getUpdateUPMFS() {
        return updateUPMFS;
    }

    public void setUpdateUPMFS(String updateUPMFS) {
        this.updateUPMFS = updateUPMFS;
    }

    @Override
    public String toString() {
        return "KnInconsistenUPMDataDTO{" +
                "profileMdnswithNoBaseMDN=" + profileMdnswithNoBaseMDN +
                ", addGroupIds=" + addGroupIds +
                ", removeGroupIds=" + removeGroupIds +
                ", addContacts=" + addContacts +
                ", removeContacts=" + removeContacts +
                ", AddDestEmergencyAttributes=" + AddDestEmergencyAttributes +
                ", removeDestEmergencyAttributes=" + removeDestEmergencyAttributes +
                ", updateUPMFS='" + updateUPMFS + '\'' +
                '}';
    }
}
