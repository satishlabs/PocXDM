/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.bulkops.dto.common;

import java.util.List;

/**
 * DTO for NNI Subscriber Profile Information
 * Contains all NNI-related profile data for bulk operations
 * Thread-safe - immutable after construction
 */
public class KnBulkOpsNNISubsDTO {

    private List<String> mdns;
    private Integer profileId;
    private Long nniActiveFs;

    public KnBulkOpsNNISubsDTO() {
    }

    public KnBulkOpsNNISubsDTO(List<String> mdns, Integer profileId, Long nniActiveFs) {
        this.mdns = mdns;
        this.profileId = profileId;
        this.nniActiveFs = nniActiveFs;
    }

    public List<String> getMdns() {
        return mdns;
    }

    public void setMdns(List<String> mdns) {
        this.mdns = mdns;
    }

    public Integer getProfileId() {
        return profileId;
    }

    public void setProfileId(Integer profileId) {
        this.profileId = profileId;
    }

    public Long getNniActiveFs() {
        return nniActiveFs;
    }

    public void setNniActiveFs(Long nniActiveFs) {
        this.nniActiveFs = nniActiveFs;
    }

    @Override
    public String toString() {
        return "KnBulkOpsNNISubsDTO{" +
                "mdnCount=" + (mdns != null ? mdns.size() : 0) +
                ", profileId=" + profileId +
                ", nniActiveFs=" + nniActiveFs +
                '}';
    }
}

