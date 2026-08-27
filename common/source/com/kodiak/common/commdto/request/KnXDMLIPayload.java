/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class KnXDMLIPayload {
    @JsonProperty("XID")
    private String xid;
    @JsonProperty("MDN")
    private String encryptedMDN;
    @JsonProperty("ADMFID")
    private String admfid;
    @JsonProperty("TaskDetails")
    private ArrayList<KnXDMLITaskDetail> taskDetails;

    public String getXid() {
        return xid;
    }

    public void setXid(String xid) {
        this.xid = xid;
    }

    public String getEncryptedMDN() {
        return encryptedMDN;
    }

    public void setEncryptedMDN(String encryptedMDN) {
        this.encryptedMDN = encryptedMDN;
    }

    public String getAdmfid() {
        return admfid;
    }

    public void setAdmfid(String admfid) {
        this.admfid = admfid;
    }

    public ArrayList<KnXDMLITaskDetail> getTaskDetails() {
        return taskDetails;
    }

    public void setTaskDetails(ArrayList<KnXDMLITaskDetail> taskDetails) {
        this.taskDetails = taskDetails;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(" KnXDMLIPayload[ ")
                .append(" xid - ").append(xid)
                .append(", TaskDetails - ").append(taskDetails)
                .append("]");

        return builder.toString();
    }
}
