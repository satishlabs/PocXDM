/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.common;

public class KnCorpGroupPropertiesDTO {

    private String groupId;

    private String ugwInterop;

    private String recordingFS;

    private String pttRecording;

    private String dataRecording;

    private String videoRecording;

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

    public String getPttRecording() {
        return pttRecording;
    }

    public void setPttRecording(String pttRecording) {
        this.pttRecording = pttRecording;
    }

    public String getDataRecording() {
        return dataRecording;
    }

    public void setDataRecording(String dataRecording) {
        this.dataRecording = dataRecording;
    }

    public String getVideoRecording() {
        return videoRecording;
    }

    public void setVideoRecording(String videoRecording) {
        this.videoRecording = videoRecording;
    }
}
