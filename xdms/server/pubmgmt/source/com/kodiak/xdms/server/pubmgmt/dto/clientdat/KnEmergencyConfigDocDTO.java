/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.dto.clientdat;


import java.util.List;

public class KnEmergencyConfigDocDTO {
    protected int eStateInitPerm;
    protected int eStateCancelPerm;
    protected int callOrigMode;
    protected int origEmcAlrtInd;
    protected int eSelMode;
    protected int eLocPollTimer;
    protected String emergConfigTimer;
    protected List<KnEmergencyMdnDTO> mdnEntry;
    protected String docEtag;

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

    public List<KnEmergencyMdnDTO> getMdnEntry() {
        return mdnEntry;
    }

    public void setMdnEntry(List<KnEmergencyMdnDTO> mdnEntry) {
        this.mdnEntry = mdnEntry;
    }


    public String getDocEtag() {
        return docEtag;
    }

    public void setDocEtag(String docEtag) {
        this.docEtag = docEtag;
    }

    @Override
    public String toString() {
        return "KnEmergencyConfigDocDTO{" +
                "eStateInitPerm=" + eStateInitPerm +
                ", eStateCancelPerm=" + eStateCancelPerm +
                ", callOrigMode=" + callOrigMode +
                ", origEmcAlrtInd=" + origEmcAlrtInd +
                ", eSelMode=" + eSelMode +
                ", eLocPollTimer=" + eLocPollTimer +
                ", emergConfigTimer=" + emergConfigTimer +
                ", mdnEntry=" + mdnEntry +
                ", docEtag='" + docEtag + '\'' +
                '}';
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
