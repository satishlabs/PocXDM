/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.impl.hook;

import com.kodiak.xdms.server.common.dto.clientdat.KnOPDirChgDTO;
import com.kodiak.xdms.server.corpmgmt.dto.ICorpHookRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpFailedData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12/7/11
 * Time: 12:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class KnCorpHookRespDTO implements ICorpHookRespDTO {
    private Object response;
    private Object data;
    private int status = 0;
    private String statusCode;
    private String message;
    private String extCorpId;
    private Collection<KnCorpFailedData> failedDataList;
    private Map<String, KnOPDirChgDTO> changeLogMap;
    Map<String, Object> customParamMap;
    List<String> addedOwnerIdList = new ArrayList<>();

    public void setResponse(Object response) {
        this.response = response;
    }

    public Object getResponse() {
        return response;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Collection<KnCorpFailedData> getFailedDataList() {
        return failedDataList;
    }

    public void setFailedDataList(Collection<KnCorpFailedData> failedDataList) {
        this.failedDataList = failedDataList;
    }

    public Map<String, KnOPDirChgDTO> getChangeLogMap() {
        return changeLogMap;
    }

    public void setChangeLogMap(Map<String, KnOPDirChgDTO> changeLogMap) {
        this.changeLogMap = changeLogMap;
    }

    public String getExtCorpId() {
        return extCorpId;
    }

    public void setExtCorpId(String extCorpId) {
        this.extCorpId = extCorpId;
    }

    public Map<String, Object> getCustomParamMap() {
        return customParamMap;
    }

    public void setCustomParamMap(Map<String, Object> customParamMap) {
        this.customParamMap = customParamMap;
    }

    public List<String> getAddedOwnerIdList() { return addedOwnerIdList; }

    public void setAddedOwnerIdList(List<String> addedOwnerIdList) { this.addedOwnerIdList = addedOwnerIdList; }
}
