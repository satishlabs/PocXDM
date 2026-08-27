/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common.dto.common;


import com.couchbase.client.core.deps.com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.couchbase.client.core.deps.com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(value= JsonInclude.Include.NON_NULL)
public class KnCorpGroupListInfoDTO {

    private Integer groupID;
    private Integer groupZone;
    private Integer groupChannel;
    private Integer groupPriority;
    private KnCorpGroupContactDTO grpMemProps;

    public KnCorpGroupListInfoDTO(){}

    public KnCorpGroupListInfoDTO(Integer groupID, Integer groupZone,
                                  Integer groupChannel, Integer groupPriority,
                                  KnCorpGroupContactDTO grpMemProps) {
        this.groupID = groupID;
        this.groupZone = groupZone;
        this.groupChannel = groupChannel;
        this.groupPriority = groupPriority;
        this.grpMemProps = grpMemProps;
    }

    public Integer getGroupID() {
        return groupID;
    }

    public void setGroupID(Integer groupID) {
        this.groupID = groupID;
    }

    public Integer getGroupZone() {
        return groupZone;
    }

    public void setGroupZone(Integer groupZone) {
        this.groupZone = groupZone;
    }

    public Integer getGroupChannel() {
        return groupChannel;
    }

    public void setGroupChannel(Integer groupChannel) {
        this.groupChannel = groupChannel;
    }

    public Integer getGroupPriority() {
        return groupPriority;
    }

    public void setGroupPriority(Integer groupPriority) {
        this.groupPriority = groupPriority;
    }

    public KnCorpGroupContactDTO getGrpMemProps() {
        return grpMemProps;
    }

    public void setGrpMemProps(KnCorpGroupContactDTO grpMemProps) {
        this.grpMemProps = grpMemProps;
    }

    @Override
    public String toString() {
        return "KnCorpGroupListInfoDTO{" +
                "groupID=" + groupID +
                ", groupZone=" + groupZone +
                ", groupChannel=" + groupChannel +
                ", groupPriority=" + groupPriority +
                ", grpMemProps=" + grpMemProps +
                '}';
    }
}
