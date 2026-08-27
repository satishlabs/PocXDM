/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.common;

public class KnCorpGroupListInfoDTO {

    private Integer groupID;
    private Integer groupZone;
    private Integer groupChannel;
    private Integer groupPriority;
    private KnCorpGroupContactDTO grpMemProps;
    private String groupName;
    private Integer groupType;

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

    public String getGroupName() { return groupName; }

    public void setGroupName(String groupName) { this.groupName = groupName; }

    public Integer getGroupType() { return groupType; }

    public void setGroupType(Integer groupType) { this.groupType = groupType; }

    @Override
    public String toString() {
        return "KnCorpGroupListInfoDTO{" +
                "groupID=" + groupID +
                ", groupZone=" + groupZone +
                ", groupChannel=" + groupChannel +
                ", groupPriority=" + groupPriority +
                ", grpMemProps=" + grpMemProps +
                ", groupName=" + groupName +
                ", groupType=" + groupType +
                '}';
    }
}
