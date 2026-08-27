/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common.dto.common;

/**
 * Created by hanwar on 10-09-2016.
 */
public class KnSuppVocoderProfileDTO {

    private int vocoderId;
    private int profileId;
    private String vocoderName;
    private int numOfFrames;
    private int pTime;
    private int maxPTime;
    private int bitrate;
    private int clockRate;
    private int mode;
    private int maxULBandwidth;
    private int maxDLBandwidth;
    private int octetAlignedMode;
    private int isDefault;

    public int getProfileId() {
        return profileId;
    }

    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }

    public String getVocoderName() {
        return vocoderName;
    }

    public void setVocoderName(String vocoderName) {
        this.vocoderName = vocoderName;
    }

    public int getVocoderId() {
        return vocoderId;
    }

    public void setVocoderId(int vocoderId) {
        this.vocoderId = vocoderId;
    }

    public int getNumOfFrames() {
        return numOfFrames;
    }

    public void setNumOfFrames(int numOfFrames) {
        this.numOfFrames = numOfFrames;
    }

    public int getpTime() {
        return pTime;
    }

    public void setpTime(int pTime) {
        this.pTime = pTime;
    }

    public int getMaxPTime() {
        return maxPTime;
    }

    public void setMaxPTime(int maxPTime) {
        this.maxPTime = maxPTime;
    }

    public int getBitrate() {
        return bitrate;
    }

    public void setBitrate(int bitrate) {
        this.bitrate = bitrate;
    }

    public int getClockRate() {
        return clockRate;
    }

    public void setClockRate(int clockRate) {
        this.clockRate = clockRate;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getMaxULBandwidth() {
        return maxULBandwidth;
    }

    public void setMaxULBandwidth(int maxULBandwidth) {
        this.maxULBandwidth = maxULBandwidth;
    }

    public int getMaxDLBandwidth() {
        return maxDLBandwidth;
    }

    public void setMaxDLBandwidth(int maxDLBandwidth) {
        this.maxDLBandwidth = maxDLBandwidth;
    }

    public int getOctetAlignedMode() {
        return octetAlignedMode;
    }

    public void setOctetAlignedMode(int octetAlignedMode) {
        this.octetAlignedMode = octetAlignedMode;
    }

    public int getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(int isDefault) {
        this.isDefault = isDefault;
    }
}
