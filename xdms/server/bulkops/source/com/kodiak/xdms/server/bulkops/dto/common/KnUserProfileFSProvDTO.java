/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.bulkops.dto.common;

public class KnUserProfileFSProvDTO {

    private String ambientListening;
    private String brdcrmb;
    private String discreteListening;
    private String geofnc;
    private String locPublish;
    private String mcVideoConfirmedPull;
    private String mcVideoGroupRx;
    private String mcVideoRx;
    private String mcVideoTx;
    private String ptloc;
    private String ptmd;
    private String ptx;
    private String tgsclnt;
    private String userCheck;
    private String userEnable;
    private String osm;
    private String emergency;
    private String selfDnDPrivilege;

    public String getAmbientListening() {
        return ambientListening;
    }

    public void setAmbientListening(String ambientListening) {
        this.ambientListening = ambientListening;
    }

    public String getBrdcrmb() {
        return brdcrmb;
    }

    public void setBrdcrmb(String brdcrmb) {
        this.brdcrmb = brdcrmb;
    }

    public String getDiscreteListening() {
        return discreteListening;
    }

    public void setDiscreteListening(String discreteListening) {
        this.discreteListening = discreteListening;
    }

    public String getGeofnc() {
        return geofnc;
    }

    public void setGeofnc(String geofnc) {
        this.geofnc = geofnc;
    }

    public String getLocPublish() {
        return locPublish;
    }

    public void setLocPublish(String locPublish) {
        this.locPublish = locPublish;
    }

    public String getMcVideoConfirmedPull() {
        return mcVideoConfirmedPull;
    }

    public void setMcVideoConfirmedPull(String mcVideoConfirmedPull) {
        this.mcVideoConfirmedPull = mcVideoConfirmedPull;
    }

    public String getMcVideoGroupRx() {
        return mcVideoGroupRx;
    }

    public void setMcVideoGroupRx(String mcVideoGroupRx) {
        this.mcVideoGroupRx = mcVideoGroupRx;
    }

    public String getMcVideoRx() {
        return mcVideoRx;
    }

    public void setMcVideoRx(String mcVideoRx) {
        this.mcVideoRx = mcVideoRx;
    }

    public String getMcVideoTx() {
        return mcVideoTx;
    }

    public void setMcVideoTx(String mcVideoTx) {
        this.mcVideoTx = mcVideoTx;
    }

    public String getPtloc() {
        return ptloc;
    }

    public void setPtloc(String ptloc) {
        this.ptloc = ptloc;
    }

    public String getPtmd() {
        return ptmd;
    }

    public void setPtmd(String ptmd) {
        this.ptmd = ptmd;
    }

    public String getPtx() {
        return ptx;
    }

    public void setPtx(String ptx) {
        this.ptx = ptx;
    }

    public String getTgsclnt() {
        return tgsclnt;
    }

    public void setTgsclnt(String tgsclnt) {
        this.tgsclnt = tgsclnt;
    }

    public String getUserCheck() {
        return userCheck;
    }

    public void setUserCheck(String userCheck) {
        this.userCheck = userCheck;
    }

    public String getUserEnable() {
        return userEnable;
    }

    public void setUserEnable(String userEnable) {
        this.userEnable = userEnable;
    }

    public String getOsm() {
        return osm;
    }

    public void setOsm(String osm) {
        this.osm = osm;
    }

    public String getEmergency() {
        return emergency;
    }

    public void setEmergency(String emergency) {
        this.emergency = emergency;
    }

    public String getSelfDnDPrivilege() {
        return selfDnDPrivilege;
    }

    public void setSelfDnDPrivilege(String selfDnDPrivilege) {
        this.selfDnDPrivilege = selfDnDPrivilege;
    }

    @Override
    public String toString() {
        return "KnUserProfileFSProvDTO{" +
                "ambientListening='" + ambientListening + '\'' +
                ", brdcrmb='" + brdcrmb + '\'' +
                ", discreteListening='" + discreteListening + '\'' +
                ", geofnc='" + geofnc + '\'' +
                ", locPublish='" + locPublish + '\'' +
               ", mcVideoConfirmedPull='" + mcVideoConfirmedPull + '\'' +
                ", mcVideoGroupRx='" + mcVideoGroupRx + '\'' +
                ", mcVideoRx='" + mcVideoRx + '\'' +
                ", mcVideoTx='" + mcVideoTx + '\'' +
                ", ptloc='" + ptloc + '\'' +
                ", ptmd='" + ptmd + '\'' +
                ", ptx='" + ptx + '\'' +
                ", tgsclnt='" + tgsclnt + '\'' +
                ", userCheck='" + userCheck + '\'' +
                ", userEnable='" + userEnable + '\'' +
                ", osm='" + osm + '\'' +
                ", emergency='" + emergency + '\'' +
                ", selfDnDPrivilege='" + selfDnDPrivilege + '\'' +
                '}';
    }
}
