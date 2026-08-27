/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMCorpContactListRespDTO.java
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
package com.kodiak.common.commdto.response;

import com.kodiak.common.commdto.common.KnCorpAsyncJobStatusInfo;
import com.kodiak.common.commdto.common.KnXDMCorpUserProfileDTO;
import com.kodiak.common.commdto.common.KnXDMMdnInfoDTO;

import java.util.Collection;
import java.util.List;
import java.util.Set;


public class KnXDMCorpUserProfileRespDTO extends KnXDMCorpRespDTO implements IXDMResponseDTO {

    private static final long serialVersionUID = 7526471155622776147L;

    private String corpId;

    private KnXDMCorpUserProfileDTO userProfileInfo;

    private Collection<KnXDMCorpUserProfileDTO> userProfilelistLists;

    private Integer maxTotalCount;

    private List<KnXDMMdnInfoDTO> mdnList;

    private List<KnCorpAsyncJobStatusInfo> jobStatus;

    private List<String> ownerIDList;

    private String sharingEnabled;

    private List<String> userProfileSharedCorpList;

    private String ownerCorpId;

    private String ownerExtCorpId;

    private String profileMdn;
    private Collection<Integer> addedSublistIds;
    private Collection<Integer> removedSublistIds;
    private Set<KnCorpGroupListInfoDTO> upmGroupList;
    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    public KnXDMCorpUserProfileDTO getUserProfileInfo() {
        return userProfileInfo;
    }

    public void setUserProfileInfo(KnXDMCorpUserProfileDTO userProfileInfo) {
        this.userProfileInfo = userProfileInfo;
    }

    public Collection<KnXDMCorpUserProfileDTO> getUserProfilelistLists() {
        return userProfilelistLists;
    }

    public void setUserProfilelistLists(Collection<KnXDMCorpUserProfileDTO> userProfilelistLists) {
        this.userProfilelistLists = userProfilelistLists;
    }

    public Integer getMaxTotalCount() {
        return maxTotalCount;
    }

    public void setMaxTotalCount(Integer maxTotalCount) {
        this.maxTotalCount = maxTotalCount;
    }

    public List<KnXDMMdnInfoDTO> getMdnList() { return mdnList; }

    public void setMdnList(List<KnXDMMdnInfoDTO> mdnList) { this.mdnList = mdnList; }

    public List<KnCorpAsyncJobStatusInfo> getJobStatus() {   return jobStatus;  }

    public void setJobStatus(List<KnCorpAsyncJobStatusInfo> jobStatus) {   this.jobStatus = jobStatus;  }

    public List<String> getOwnerIDList() { return ownerIDList; }

    public void setOwnerIDList(List<String> ownerIDList) { this.ownerIDList = ownerIDList; }

    public String getSharingEnabled() {
        return sharingEnabled;
    }

    public void setSharingEnabled(String sharingEnabled) {
        this.sharingEnabled = sharingEnabled;
    }

    public List<String> getUserProfileSharedCorpList() {
        return userProfileSharedCorpList;
    }

    public void setUserProfileSharedCorpList(List<String> userProfileSharedCorpList) {
        this.userProfileSharedCorpList = userProfileSharedCorpList;
    }

    public String getOwnerCorpId() {
        return ownerCorpId;
    }

    public void setOwnerCorpId(String ownerCorpId) {
        this.ownerCorpId = ownerCorpId;
    }

    public String getOwnerExtCorpId() {
        return ownerExtCorpId;
    }

    public void setOwnerExtCorpId(String ownerExtCorpId) {
        this.ownerExtCorpId = ownerExtCorpId;
    }

    public String getProfileMdn() {
        return profileMdn;
    }

    public void setProfileMdn(String profileMdn) {
        this.profileMdn = profileMdn;
    }
    public Collection<Integer> getAddedSublistIds() {
        return addedSublistIds;
    }

    public void setAddedSublistIds(Collection<Integer> addedSublistIds) {
        this.addedSublistIds = addedSublistIds;
    }

    public Collection<Integer> getRemovedSublistIds() {
        return removedSublistIds;
    }

    public void setRemovedSublistIds(Collection<Integer> removedSublistIds) {
        this.removedSublistIds = removedSublistIds;
    }

    public Set<KnCorpGroupListInfoDTO> getUpmGroupList() {
        return upmGroupList;
    }

    public void setUpmGroupList(Set<KnCorpGroupListInfoDTO> upmGroupList) {
        this.upmGroupList = upmGroupList;
    }

    @Override
    public String toString() {
        return "KnXDMCorpUserProfileRespDTO{" +
                "corpId='" + corpId + '\'' +
                ", userProfileInfo=" + userProfileInfo +
                ", userProfilelistLists=" + userProfilelistLists +
                ", maxTotalCount=" + maxTotalCount +
                ", mdnList=" + mdnList +
                ", jobStatus=" + jobStatus +
                ", ownerIDList=" + ownerIDList +
                ", sharingEnabled=" + sharingEnabled +
                ", userProfileSharedCorpList=" + userProfileSharedCorpList +
                ", ownerCorpId=" + ownerCorpId +
                ", ownerExtCorpId=" + ownerExtCorpId +
                ", profileMdn=" + profileMdn +
                ", addedSublistIds=" + addedSublistIds +
                ", removedSublistIds=" + removedSublistIds +
                '}';
    }
}
