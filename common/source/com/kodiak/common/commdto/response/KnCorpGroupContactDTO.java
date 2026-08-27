package com.kodiak.common.commdto.response;

import java.io.Serializable;

public class KnCorpGroupContactDTO implements Serializable {

    private Integer isSupervisor;
    private Integer isBroadcaster;
    private Integer isLocSupervisor;
    private Integer isOSMAuthorized;
    private Integer callInitiateAllowed;
    private Integer callTerminateAllowed;
    private Integer IncallAllowed;

    public KnCorpGroupContactDTO() {
    }

    public KnCorpGroupContactDTO(Integer isSupervisor, Integer isBroadcaster,
                                 Integer isLocSupervisor, Integer isOSMAuthorized,
                                 Integer callInitiateAllowed, Integer callTerminateAllowed,
                                 Integer incallAllowed) {
        this.isSupervisor = isSupervisor;
        this.isBroadcaster = isBroadcaster;
        this.isLocSupervisor = isLocSupervisor;
        this.isOSMAuthorized = isOSMAuthorized;
        this.callInitiateAllowed = callInitiateAllowed;
        this.callTerminateAllowed = callTerminateAllowed;
        IncallAllowed = incallAllowed;
    }

    public Integer getIsSupervisor() {
        return isSupervisor;
    }

    public void setIsSupervisor(Integer isSupervisor) {
        this.isSupervisor = isSupervisor;
    }

    public Integer getIsBroadcaster() {
        return isBroadcaster;
    }

    public void setIsBroadcaster(Integer isBroadcaster) {
        this.isBroadcaster = isBroadcaster;
    }

    public Integer getIsLocSupervisor() {
        return isLocSupervisor;
    }

    public void setIsLocSupervisor(Integer isLocSupervisor) {
        this.isLocSupervisor = isLocSupervisor;
    }

    public Integer getIsOSMAuthorized() {
        return isOSMAuthorized;
    }

    public void setIsOSMAuthorized(Integer isOSMAuthorized) {
        this.isOSMAuthorized = isOSMAuthorized;
    }

    public Integer getCallInitiateAllowed() {
        return callInitiateAllowed;
    }

    public void setCallInitiateAllowed(Integer callInitiateAllowed) {
        this.callInitiateAllowed = callInitiateAllowed;
    }

    public Integer getCallTerminateAllowed() {
        return callTerminateAllowed;
    }

    public void setCallTerminateAllowed(Integer callTerminateAllowed) {
        this.callTerminateAllowed = callTerminateAllowed;
    }

    public Integer getIncallAllowed() {
        return IncallAllowed;
    }

    public void setIncallAllowed(Integer incallAllowed) {
        IncallAllowed = incallAllowed;
    }

    @Override
    public String toString() {
        return "KnCorpGroupContactDTO{" +
                "isSupervisor=" + isSupervisor +
                ", isBroadcaster=" + isBroadcaster +
                ", isLocSupervisor=" + isLocSupervisor +
                ", isOSMAuthorized=" + isOSMAuthorized +
                ", callInitiateAllowed=" + callInitiateAllowed +
                ", callTerminateAllowed=" + callTerminateAllowed +
                ", IncallAllowed=" + IncallAllowed +
                '}';
    }
}
