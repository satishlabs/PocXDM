/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kodiak.common.dto.IIdentifier;

public class KnOICDAttributes implements IIdentifier {

    private static final long serialVersionUID = 7526571155622699666L;

    @JsonProperty(value = "mcpttid")
    private String mcpttid;
    @JsonProperty(value = "clienttype")
    private String clienttype;
    @JsonProperty(value = "usertype")
    private String usertype;
    @JsonProperty(value = "accstate")
    private String accstate;
    @JsonProperty(value = "actcode")
    private String actcode;
    @JsonProperty(value = "creationdate")
    private String creationdate;

    public String getMcpttid() {
        return mcpttid;
    }

    public void setMcpttid(String mcpttid) {
        this.mcpttid = mcpttid;
    }

    public String getClienttype() {
        return clienttype;
    }

    public void setClienttype(String clienttype) {
        this.clienttype = clienttype;
    }

    public String getUsertype() {
        return usertype;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

    public String getAccstate() {
        return accstate;
    }

    public void setAccstate(String accstate) {
        this.accstate = accstate;
    }

    public String getActcode() {
        return actcode;
    }

    public void setActcode(String actcode) {
        this.actcode = actcode;
    }

    public String getCreationdate() {
        return creationdate;
    }

    public void setCreationdate(String creationdate) {
        this.creationdate = creationdate;
    }

    @Override
    @JsonIgnore
    public String getObjectId() {
        return null;
    }
}
