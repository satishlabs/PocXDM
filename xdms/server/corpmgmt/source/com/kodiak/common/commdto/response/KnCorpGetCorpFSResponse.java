/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.response;

import com.kodiak.common.dto.IIdentifier;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpResponseDTO;


public class KnCorpGetCorpFSResponse  extends KnCorpResponseDTO implements IIdentifier{

    private static final long serialVersionUID = -3388047771821246619L;

    private String pttRecording;

    private String dataRecording;

    private String videoRecording;

    private String selfDnDPrivilege;

    private String largeAgencyDispatch;

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

    public String getSelfDnDPrivilege() { return selfDnDPrivilege; }

    public void setSelfDnDPrivilege(String selfDnDPrivilege) { this.selfDnDPrivilege = selfDnDPrivilege; }

    public String getLargeAgencyDispatch() { return largeAgencyDispatch; }

    public void setLargeAgencyDispatch(String largeAgencyDispatch) { this.largeAgencyDispatch = largeAgencyDispatch; }

    @Override
    public String toString() {
        return "KnCorpGetCorpFSResponse{" +
                "pttRecording='" + pttRecording + '\'' +
                ", dataRecording='" + dataRecording + '\'' +
                ", videoRecording='" + videoRecording + '\'' +
                ", selfDnDPrivilege='" + selfDnDPrivilege + '\'' +
                ", largeAgencyDispatch='" + largeAgencyDispatch + '\'' +
                '}';
    }

        @Override
        public String getObjectId() {
            return null;
        }

}
