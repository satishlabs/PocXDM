/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.request;

import com.kodiak.common.dto.IIdentifier;

public class KnXDMGroupPropertyInfoDTO implements IIdentifier {

    private static final long serialVersionUID = 7526471155622676132L;

    private String groupId;

    private String ugwInterop;

    private String recordingFS;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getUgwInterop() {
        return ugwInterop;
    }

    public void setUgwInterop(String ugwInterop) {
        this.ugwInterop = ugwInterop;
    }

    public String getRecordingFS() {
        return recordingFS;
    }

    public void setRecordingFS(String recordingFS) {
        this.recordingFS = recordingFS;
    }

    @Override
    public String toString() {
        return "KnXDMGroupProperyInfoDTO{" +
                "groupId='" + groupId + '\'' +
                ", ugwInterop='" + ugwInterop + '\'' +
                ", recordingFS='" + recordingFS + '\'' +
                '}';
    }

    @Override
    public String getObjectId() {
        return null;
    }
}
