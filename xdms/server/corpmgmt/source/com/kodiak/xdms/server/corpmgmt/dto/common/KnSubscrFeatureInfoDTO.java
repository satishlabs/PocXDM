/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.common;

import com.kodiak.common.resources.KnGDPRTemplate;

/**
 * Created by asanjiv on 11/3/2016.
 */
public class KnSubscrFeatureInfoDTO {

    private String mdn;
    private Integer ptxBit;
    private Integer ptmdBit;
    private Integer ptlocBit;
    private Integer tgscClientBit;
    private Integer brdcrmbBit;
    private Integer geofncBit;
    private boolean enabledPttRadio;
    private Integer ambientListeningBit;
    private Integer discreteListeningBit;
    private Integer userCheckCorpBit;
    private Integer userEnableCorpBit;
    private Integer locPublishCorpBit;
    private Integer mcVideoTx;
    private Integer mcVideoRx;
    private Integer mcVideoGroupRx;
    private Integer mcVideoConfirmedPull;
    private Integer mcDevice;
    private Integer wdsPatching;
    private Integer wdsRecording;
    private Integer pttRecording;
    private Integer dataRecording;
    private Integer videoRecording;
    private Integer selfDnDPrivilege;
    private Integer largeAgencyDispatch;

    public Integer getWdsPatching() {
        return wdsPatching;
    }

    public void setWdsPatching(Integer wdsPatching) {
        this.wdsPatching = wdsPatching;
    }

    public Integer getWdsRecording() {
        return wdsRecording;
    }

    public void setWdsRecording(Integer wdsRecording) {
        this.wdsRecording = wdsRecording;
    }

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    public Integer getPtxBit() {
        return ptxBit;
    }

    public void setPtxBit(Integer ptxBit) {
        this.ptxBit = ptxBit;
    }

    public Integer getPtmdBit() {
        return ptmdBit;
    }

    public void setPtmdBit(Integer ptmdBit) {
        this.ptmdBit = ptmdBit;
    }

    public Integer getPtlocBit() {
        return ptlocBit;
    }

    public void setPtlocBit(Integer ptlocBit) {
        this.ptlocBit = ptlocBit;
    }

    public Integer getTgscClientBit() {
        return tgscClientBit;
    }

    public void setTgscClientBit(Integer tgscClientBit) {
        this.tgscClientBit = tgscClientBit;
    }

    public boolean isEnabledPttRadio() {
        return enabledPttRadio;
    }

    public void setEnabledPttRadio(boolean enabledPttRadio) {
        this.enabledPttRadio = enabledPttRadio;
    }

    public Integer getBrdcrmbBit() {
        return brdcrmbBit;
    }

    public void setBrdcrmbBit(Integer brdcrmbBit) {
        this.brdcrmbBit = brdcrmbBit;
    }

    public Integer getGeofncBit() {
        return geofncBit;
    }

    public void setGeofncBit(Integer geofncBit) {
        this.geofncBit = geofncBit;
    }

    public Integer getAmbientListeningBit() {
        return ambientListeningBit;
    }

    public void setAmbientListeningBit(Integer ambientListeningBit) {
        this.ambientListeningBit = ambientListeningBit;
    }

    public Integer getDiscreteListeningBit() {
        return discreteListeningBit;
    }

    public void setDiscreteListeningBit(Integer discreteListeningBit) {
        this.discreteListeningBit = discreteListeningBit;
    }

    public Integer getUserCheckCorpBit() {
        return userCheckCorpBit;
    }

    public void setUserCheckCorpBit(Integer userCheckCorpBit) {
        this.userCheckCorpBit = userCheckCorpBit;
    }

    public Integer getUserEnableCorpBit() {
        return userEnableCorpBit;
    }

    public void setUserEnableCorpBit(Integer userEnableCorpBit) {
        this.userEnableCorpBit = userEnableCorpBit;
    }

    public Integer getLocPublishCorpBit() {
        return locPublishCorpBit;
    }

    public void setLocPublishCorpBit(Integer locPublishCorpBit) {
        this.locPublishCorpBit = locPublishCorpBit;
    }

    public Integer getMcVideoTx() {
        return mcVideoTx;
    }

    public void setMcVideoTx(Integer mcVideoTx) {
        this.mcVideoTx = mcVideoTx;
    }

    public Integer getMcVideoRx() {
        return mcVideoRx;
    }

    public void setMcVideoRx(Integer mcVideoRx) {
        this.mcVideoRx = mcVideoRx;
    }

    public Integer getMcVideoGroupRx() {
        return mcVideoGroupRx;
    }

    public void setMcVideoGroupRx(Integer mcVideoGroupRx) {
        this.mcVideoGroupRx = mcVideoGroupRx;
    }

    public Integer getMcVideoConfirmedPull() {
        return mcVideoConfirmedPull;
    }

    public void setMcVideoConfirmedPull(Integer mcVideoConfirmedPull) {
        this.mcVideoConfirmedPull = mcVideoConfirmedPull;
    }

    public Integer getMcDevice() {
        return mcDevice;
    }

    public void setMcDevice(Integer mcDevice) {
        this.mcDevice = mcDevice;
    }

    public Integer getPttRecording() {
        return pttRecording;
    }

    public void setPttRecording(Integer pttRecording) {
        this.pttRecording = pttRecording;
    }

    public Integer getDataRecording() {
        return dataRecording;
    }

    public void setDataRecording(Integer dataRecording) {
        this.dataRecording = dataRecording;
    }

    public Integer getVideoRecording() {
        return videoRecording;
    }

    public void setVideoRecording(Integer videoRecording) {
        this.videoRecording = videoRecording;
    }

    public Integer getSelfDnDPrivilege() { return selfDnDPrivilege; }

    public void setSelfDnDPrivilege(Integer selfDnDPrivilege) { this.selfDnDPrivilege = selfDnDPrivilege; }

    public Integer getLargeAgencyDispatch() { return largeAgencyDispatch; }

    public void setLargeAgencyDispatch(Integer largeAgencyDispatch) {
        this.largeAgencyDispatch = largeAgencyDispatch;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(100);
        sb.append(super.toString())
                .append(", mdn - ").append(KnGDPRTemplate.mdn(mdn))
                .append(", ptxBit - ").append(ptxBit)
                .append(", ptmdBit - ").append(ptmdBit)
                .append(", ptlocBit - ").append(ptlocBit)
                .append(", tgscClientBit - ").append(tgscClientBit)
                .append(", brdcrmbBit - ").append(brdcrmbBit)
                .append(", geofncBit - ").append(geofncBit)
                .append(", enabledPttRadio - ").append(enabledPttRadio)
                .append(", ambientListeningBit - ").append(ambientListeningBit)
                .append(", discreteListeningBit - ").append(discreteListeningBit)
                .append(", userCheckCorpBit - ").append(userCheckCorpBit)
                .append(", userEnableCorpBit - ").append(userEnableCorpBit)
                .append(", locPublishCorpBit - ").append(locPublishCorpBit)
                .append(", mcVideoTx - ").append(mcVideoTx)
                .append(", mcVideoRx - ").append(mcVideoRx)
                .append(", mcVideoGroupRx - ").append(mcVideoGroupRx)
                .append(", mcVideoConfirmedPull - ").append(mcVideoConfirmedPull)
                .append(", mcDevice - ").append(mcDevice)
                .append(", wdsPatching - ").append(wdsPatching)
                .append(", wdsRecording - ").append(wdsRecording)
                .append(", pttRecording - ").append(pttRecording)
                .append(", dataRecording - ").append(dataRecording)
                .append(", videoRecording - ").append(videoRecording)
                .append(", selfDnDPrivilege - ").append(selfDnDPrivilege)
                .append(", largeAgencyDispatch - ").append(largeAgencyDispatch);
        return sb.toString();
    }
}
