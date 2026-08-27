/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.persistdat;

import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;

public class KnCorpRecordingFsPersistDTO implements IPersistenceDTO {

    private IInputDTO inputDTO;
    private IPersistenceDTO persistenceDTO;
    private String entityId;
    private String operationType;
    private String profile;
    private Boolean sysPttRecordingFlag;
    private Boolean sysDataRecordingFlag;
    private Boolean sysVideoRecordingFlag;
    private Boolean sysSelfDnDPrivilege;
    private String reqSelfDnDPrivilege;
    private Boolean sysLargeAgencyDispatch;
    private String  reqLargeAgencyDispatch;

    private String reqRecordingFs;


    @Override
    public String getObjectId() {
        return null;
    }

    @Override
    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    @Override
    public String getOperationType() {
        return operationType;
    }

    @Override
    public String getEntityId() {
        return entityId;
    }

    @Override
    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    @Override
    public void setInputDTO(IInputDTO inputDTO) {
        setProfile(inputDTO.getProfile());
        setEntityId(inputDTO.getEntityId());
        setOperationType(inputDTO.getOperationType());
        this.inputDTO = inputDTO;
    }

    @Override
    public IInputDTO getInputDTO() {
        return inputDTO;
    }

    @Override
    public void setPersistenceDTO(IPersistenceDTO persistenceDTO) {
        this.persistenceDTO = persistenceDTO;
    }

    @Override
    public IPersistenceDTO getPersistenceDTO() {
        return persistenceDTO;
    }

    @Override
    public String getProfile() {
        return profile;
    }

    @Override
    public void setProfile(String profile) {
        this.profile = profile;
    }

    public Boolean getSysPttRecordingFlag() { return sysPttRecordingFlag; }

    public void setSysPttRecordingFlag(Boolean sysPttRecordingFlag) { this.sysPttRecordingFlag = sysPttRecordingFlag; }

    public Boolean getSysDataRecordingFlag() { return sysDataRecordingFlag; }

    public void setSysDataRecordingFlag(Boolean sysDataRecordingFlag) { this.sysDataRecordingFlag = sysDataRecordingFlag; }

    public Boolean getSysVideoRecordingFlag() { return sysVideoRecordingFlag; }

    public void setSysVideoRecordingFlag(Boolean sysVideoRecordingFlag) { this.sysVideoRecordingFlag = sysVideoRecordingFlag; }

    public String getReqRecordingFs() { return reqRecordingFs; }

    public void setReqRecordingFs(String reqRecordingFs) { this.reqRecordingFs = reqRecordingFs; }

    public Boolean getSysSelfDnDPrivilege() {
        return sysSelfDnDPrivilege;
    }

    public void setSysSelfDnDPrivilege(Boolean sysSelfDnDPrivilege) {
        this.sysSelfDnDPrivilege = sysSelfDnDPrivilege;
    }

    public String getReqSelfDnDPrivilege() {
        return reqSelfDnDPrivilege;
    }
    public void setReqSelfDnDPrivilege(String reqSelfDnDPrivilege) {
        this.reqSelfDnDPrivilege = reqSelfDnDPrivilege;
    }

    public Boolean getSysLargeAgencyDispatch() {
        return sysLargeAgencyDispatch;
    }

    public void setSysLargeAgencyDispatch(Boolean sysLargeAgencyDispatch) {
        this.sysLargeAgencyDispatch = sysLargeAgencyDispatch;
    }

    public String getReqLargeAgencyDispatch() {
        return reqLargeAgencyDispatch;
    }
    public void setReqLargeAgencyDispatch(String reqLargeAgencyDispatch) {
        this.reqLargeAgencyDispatch = reqLargeAgencyDispatch;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(300);
        sb.append(super.toString())
                .append(", sysPttRecordingFlag - ").append(sysPttRecordingFlag)
                .append(", sysDataRecordingFlag - ").append(sysDataRecordingFlag)
                .append(", sysVideoRecordingFlag - ").append(sysVideoRecordingFlag)
                .append(", reqRecordingFs - ").append(reqRecordingFs)
                .append(", sysSelfDndPrivilege - ").append(sysSelfDnDPrivilege)
                .append(", reqSelfDnDPrivilege - ").append(reqSelfDnDPrivilege)
                .append(", sysLargeAgencyDispatch -").append(sysLargeAgencyDispatch)
                .append(", reqLargeAgencyDispatch -").append(reqLargeAgencyDispatch);
        return sb.toString();
    }
}
