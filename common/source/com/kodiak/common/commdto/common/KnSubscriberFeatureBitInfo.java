/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.common;

import com.kodiak.common.resources.KnGDPRTemplate;

import java.io.Serializable;

public class KnSubscriberFeatureBitInfo  implements Serializable {

    private static final long serialVersionUID = 7526471155622676128L;

    private String mdn;
    private long clientFS;
    private long corpAdminFS;

    private Integer ptxCorpAdminFS;
    private Integer ptmdCorpAdminFS;
    private Integer ptlocCorpAdminFS;
    private Integer tgsclntCorpAdminFS;
    private Integer brdcrmbCorpAdminFS;
    private Integer geofncCorpAdminFS;
    private Integer ambientListeningCorpAdminFS;
    private Integer discreteListeningCorpAdminFS;
    private Integer userCheckCorpAdminFS;
    private Integer userEnableCorpAdminFS;
    private Integer locPublishCorpAdminFS;
    private Integer mcVideoTxCorpAdminFS;
    private Integer mcVideoRxCorpAdminFS;
    private Integer mcVideoGroupRxCorpAdminFS;
    private Integer mcVideoConfirmedPullCorpAdminFS;
    private String clientFS2;
    private String corpAdminFS2;
    private Integer mcDeviceAdminFS;
    private Integer wdsPatchingAdminS;
    private Integer wdsRecordingAdminFS;
    private Integer pttRecordingAdminFS;
    private Integer dataRecordingAdminFS;
    private Integer videoRecordingAdminFS;
    private Integer selfDnDPrivilegeFS;
    private Integer largeAgencyDispatchFS;

    public Integer getWdsPatchingAdminS() {
        return wdsPatchingAdminS;
    }

    public void setWdsPatchingAdminS(Integer wdsPatchingAdminS) {
        this.wdsPatchingAdminS = wdsPatchingAdminS;
    }

    public Integer getWdsRecordingAdminFS() {
        return wdsRecordingAdminFS;
    }

    public void setWdsRecordingAdminFS(Integer wdsRecordingAdminFS) {
        this.wdsRecordingAdminFS = wdsRecordingAdminFS;
    }

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    public long getClientFS() {
        return clientFS;
    }

    public void setClientFS(long clientFS) {
        this.clientFS = clientFS;
    }

    public long getCorpAdminFS() {
        return corpAdminFS;
    }

    public void setCorpAdminFS(long corpAdminFS) {
        this.corpAdminFS = corpAdminFS;
    }

    public Integer getPtxCorpAdminFS() {
        return ptxCorpAdminFS;
    }

    public void setPtxCorpAdminFS(Integer ptxCorpAdminFS) {
        this.ptxCorpAdminFS = ptxCorpAdminFS;
    }

    public Integer getPtmdCorpAdminFS() {
        return ptmdCorpAdminFS;
    }

    public void setPtmdCorpAdminFS(Integer ptmdCorpAdminFS) {
        this.ptmdCorpAdminFS = ptmdCorpAdminFS;
    }

    public Integer getPtlocCorpAdminFS() {
        return ptlocCorpAdminFS;
    }

    public void setPtlocCorpAdminFS(Integer ptlocCorpAdminFS) {
        this.ptlocCorpAdminFS = ptlocCorpAdminFS;
    }

    public Integer getTgsclntCorpAdminFS() {
        return tgsclntCorpAdminFS;
    }

    public void setTgsclntCorpAdminFS(Integer tgsclntCorpAdminFS) {
        this.tgsclntCorpAdminFS = tgsclntCorpAdminFS;
    }

    public Integer getBrdcrmbCorpAdminFS() {
        return brdcrmbCorpAdminFS;
    }

    public void setBrdcrmbCorpAdminFS(Integer brdcrmbCorpAdminFS) {
        this.brdcrmbCorpAdminFS = brdcrmbCorpAdminFS;
    }

    public Integer getGeofncCorpAdminFS() {
        return geofncCorpAdminFS;
    }

    public void setGeofncCorpAdminFS(Integer geofncCorpAdminFS) {
        this.geofncCorpAdminFS = geofncCorpAdminFS;
    }

    public Integer getAmbientListeningCorpAdminFS() {
        return ambientListeningCorpAdminFS;
    }

    public void setAmbientListeningCorpAdminFS(Integer ambientListeningCorpAdminFS) {
        this.ambientListeningCorpAdminFS = ambientListeningCorpAdminFS;
    }

    public Integer getDiscreteListeningCorpAdminFS() {
        return discreteListeningCorpAdminFS;
    }

    public void setDiscreteListeningCorpAdminFS(Integer discreteListeningCorpAdminFS) {
        this.discreteListeningCorpAdminFS = discreteListeningCorpAdminFS;
    }

    public Integer getUserCheckCorpAdminFS() {
        return userCheckCorpAdminFS;
    }

    public void setUserCheckCorpAdminFS(Integer userCheckCorpAdminFS) {
        this.userCheckCorpAdminFS = userCheckCorpAdminFS;
    }

    public Integer getUserEnableCorpAdminFS() {
        return userEnableCorpAdminFS;
    }

    public void setUserEnableCorpAdminFS(Integer userEnableCorpAdminFS) {
        this.userEnableCorpAdminFS = userEnableCorpAdminFS;
    }

    public Integer getLocPublishCorpAdminFS() {
        return locPublishCorpAdminFS;
    }

    public void setLocPublishCorpAdminFS(Integer locPublishCorpAdminFS) {
        this.locPublishCorpAdminFS = locPublishCorpAdminFS;
    }

    public String getClientFS2() {
		return clientFS2;
	}

	public void setClientFS2(String clientFS2) {
		this.clientFS2 = clientFS2;
	}

	public String getCorpAdminFS2() {
		return corpAdminFS2;
	}

	public void setCorpAdminFS2(String corpAdminFS2) {
		this.corpAdminFS2 = corpAdminFS2;
	}

    public Integer getMcVideoTxCorpAdminFS() {
        return mcVideoTxCorpAdminFS;
    }

    public void setMcVideoTxCorpAdminFS(Integer mcVideoTxCorpAdminFS) {
        this.mcVideoTxCorpAdminFS = mcVideoTxCorpAdminFS;
    }

    public Integer getMcVideoRxCorpAdminFS() {
        return mcVideoRxCorpAdminFS;
    }

    public void setMcVideoRxCorpAdminFS(Integer mcVideoRxCorpAdminFS) {
        this.mcVideoRxCorpAdminFS = mcVideoRxCorpAdminFS;
    }

    public Integer getMcVideoGroupRxCorpAdminFS() {
        return mcVideoGroupRxCorpAdminFS;
    }

    public void setMcVideoGroupRxCorpAdminFS(Integer mcVideoGroupRxCorpAdminFS) {
        this.mcVideoGroupRxCorpAdminFS = mcVideoGroupRxCorpAdminFS;
    }

    public Integer getMcVideoConfirmedPullCorpAdminFS() {
        return mcVideoConfirmedPullCorpAdminFS;
    }

    public void setMcVideoConfirmedPullCorpAdminFS(Integer mcVideoConfirmedPullCorpAdminFS) {
        this.mcVideoConfirmedPullCorpAdminFS = mcVideoConfirmedPullCorpAdminFS;
    }

    public Integer getMcDeviceAdminFS() {
        return mcDeviceAdminFS;
    }

    public void setMcDeviceAdminFS(Integer mcDeviceAdminFS) {
        this.mcDeviceAdminFS = mcDeviceAdminFS;
    }

    public Integer getPttRecordingAdminFS() {
        return pttRecordingAdminFS;
    }

    public void setPttRecordingAdminFS(Integer pttRecordingAdminFS) {
        this.pttRecordingAdminFS = pttRecordingAdminFS;
    }

    public Integer getDataRecordingAdminFS() {
        return dataRecordingAdminFS;
    }

    public void setDataRecordingAdminFS(Integer dataRecordingAdminFS) {
        this.dataRecordingAdminFS = dataRecordingAdminFS;
    }

    public Integer getVideoRecordingAdminFS() {
        return videoRecordingAdminFS;
    }

    public void setVideoRecordingAdminFS(Integer videoRecordingAdminFS) {
        this.videoRecordingAdminFS = videoRecordingAdminFS;
    }

    public Integer getSelfDnDPrivilegeFS() { return selfDnDPrivilegeFS; }

    public void setSelfDnDPrivilegeFS(Integer selfDnDPrivilegeFS) { this.selfDnDPrivilegeFS = selfDnDPrivilegeFS; }

    public Integer getLargeAgencyDispatchFS() { return largeAgencyDispatchFS; }

    public void setLargeAgencyDispatchFS(Integer largeAgencyDispatchFS) {
        this.largeAgencyDispatchFS = largeAgencyDispatchFS;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(200);
        sb.append("MDN - ").append(KnGDPRTemplate.mdn(mdn))
                .append(", clientFS - ").append(clientFS)
                .append(", corpAdminFS - ").append(corpAdminFS)
                .append(", ptxCorpAdminFS - ").append(ptxCorpAdminFS)
                .append(", ptmdCorpAdminFS - ").append(ptmdCorpAdminFS)
                .append(", ptlocCorpAdminFS - ").append(ptlocCorpAdminFS)
                .append(", tgsclntCorpAdminFS - ").append(tgsclntCorpAdminFS)
                .append(", brdcrmbCorpAdminFS - ").append(brdcrmbCorpAdminFS)
                .append(", geofncCorpAdminFS - ").append(geofncCorpAdminFS)
                .append(", ambientListeningCorpAdminFS - ").append(ambientListeningCorpAdminFS)
                .append(", discreteListeningCorpAdminFS - ").append(discreteListeningCorpAdminFS)
                .append(", userCheckCorpAdminFS - ").append(userCheckCorpAdminFS)
                .append(", userEnableCorpAdminFS - ").append(userEnableCorpAdminFS)
                .append(", locPublishCorpAdminFS - ").append(locPublishCorpAdminFS)
                .append(", clientFS2 - ").append(clientFS2)
                .append(", corpAdminFS2 - ").append(corpAdminFS2)
                .append(", mcVideoTxCorpAdminFS - ").append(mcVideoTxCorpAdminFS)
                .append(", mcVideoRxCorpAdminFS - ").append(mcVideoRxCorpAdminFS)
                .append(", mcVideoGroupRxCorpAdminFS - ").append(mcVideoGroupRxCorpAdminFS)
                .append(", mcVideoConfirmedPullCorpAdminFS - ").append(mcVideoConfirmedPullCorpAdminFS)
                .append(", mcDeviceAdminFS - ").append(mcDeviceAdminFS)
                .append(", wdsPatchingAdminS - ").append(wdsPatchingAdminS)
                .append(", wdsRecordingAdminFS - ").append(wdsRecordingAdminFS)
                .append(", pttRecordingAdminFS - ").append(pttRecordingAdminFS)
                .append(", dataRecordingAdminFS - ").append(dataRecordingAdminFS)
                .append(", wdsRecordingAdminFS - ").append(videoRecordingAdminFS)
                .append(", selfDnDPrivilegeFS - ").append(selfDnDPrivilegeFS)
                .append(", largeAgencyDispatchFS - ").append(largeAgencyDispatchFS);
        return sb.toString();
    }
}
