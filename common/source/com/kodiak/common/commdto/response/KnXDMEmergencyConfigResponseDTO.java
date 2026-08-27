/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.response;

import com.kodiak.common.commdto.common.KnEmergencyEntryDTO;

import java.util.List;

public class KnXDMEmergencyConfigResponseDTO extends KnXDMRespDTO {
    private static final long serialVersionUID = 7526471166622676154L;

    protected int eStateInitPerm;
    protected int eStateCancelPerm;
    protected int callOrigMode;
    protected int origEmcAlrtInd;
    protected int eSelMode;
    protected int eLocPollTimer;
    protected List<KnEmergencyEntryDTO> eList;
    protected String docEtag;
    protected String emergConfigTimer;
    @Override
    public String toString() {
        return "KnXDMEmergencyConfigResponseDTO{" +
                "eStateInitPerm=" + eStateInitPerm +
                ", eStateCancelPerm=" + eStateCancelPerm +
                ", callOrigMode=" + callOrigMode +
                ", origEmcAlrtInd=" + origEmcAlrtInd +
                ", eSelMode=" + eSelMode +
                ", eLocPollTimer=" + eLocPollTimer +
                ", emergConfigTimer=" + emergConfigTimer +
                ", eList=" + eList +
                ", docEtag='" + docEtag + '\'' +
                '}';
    }

    public String getDocEtag() {
        return docEtag;
    }

    public void setDocEtag(String docEtag) {
        this.docEtag = docEtag;
    }

    public int geteStateInitPerm() {
        return eStateInitPerm;
    }

    public void seteStateInitPerm(int eStateInitPerm) {
        this.eStateInitPerm = eStateInitPerm;
    }

    public int geteStateCancelPerm() {
        return eStateCancelPerm;
    }

    public void seteStateCancelPerm(int eStateCancelPerm) {
        this.eStateCancelPerm = eStateCancelPerm;
    }

    public int getCallOrigMode() {
        return callOrigMode;
    }

    public void setCallOrigMode(int callOrigMode) {
        this.callOrigMode = callOrigMode;
    }

    public int getOrigEmcAlrtInd() {
        return origEmcAlrtInd;
    }

    public void setOrigEmcAlrtInd(int origEmcAlrtInd) {
        this.origEmcAlrtInd = origEmcAlrtInd;
    }

    public int geteSelMode() {
        return eSelMode;
    }

    public void seteSelMode(int eSelMode) {
        this.eSelMode = eSelMode;
    }

    public List<KnEmergencyEntryDTO> geteList() {
        return eList;
    }

    public void seteList(List<KnEmergencyEntryDTO> eList) {
        this.eList = eList;
    }

    public int geteLocPollTimer() {
        return eLocPollTimer;
    }

    public void seteLocPollTimer(int eLocPollTimer) {
        this.eLocPollTimer = eLocPollTimer;
    }

    public String getEmergConfigTimer() {return emergConfigTimer;}

    public void setEmergConfigTimer(String emergConfigTimer) {this.emergConfigTimer = emergConfigTimer;}
}
