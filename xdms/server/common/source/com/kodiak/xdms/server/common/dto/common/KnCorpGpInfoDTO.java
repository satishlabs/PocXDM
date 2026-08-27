/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common.dto.common;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Aug 14, 2019
 * Time: 11:27:16 AM
 * To change this template use File | Settings | File Templates.
 */
import com.kodiak.common.dto.IIdentifier;

public class KnCorpGpInfoDTO implements IIdentifier {

    private static final long serialVersionUID = 7526471155622676175L;

    private Integer corpgroupId;
    private Integer corpId;
    private String groupDisplayName;
    private Integer groupMemListId;
    private Integer groupDistPolicy;
    private String groupType;
    private String groupName;
    private String pocHome;
    private Integer hangTimeOut;
    private Integer osmLisId;
    private Integer avatarId;
    private Integer mcxGrpInd;
    private String isPreConfiguredGroup;
    private Integer groupEtag;
    private Integer groupCreatedBy;
    private String groupOwner;
    private Integer isLargeGroup;

    public String getIsPreConfiguredGroup() {
        return isPreConfiguredGroup;
    }

    public void setIsPreConfiguredGroup(String isPreConfiguredGroup) {
        this.isPreConfiguredGroup = isPreConfiguredGroup;
    }

    public Integer getMcxGrpInd() {
        return mcxGrpInd;
    }

    public void setMcxGrpInd(Integer mcxGrpInd) {
        this.mcxGrpInd = mcxGrpInd;
    }

    public Integer getCorpgroupId() {
        return corpgroupId;
    }

    public void setCorpgroupId(Integer corpgroupId) {
        this.corpgroupId = corpgroupId;
    }

    public Integer getCorpId() {
        return corpId;
    }

    public void setCorpId(Integer corpId) {
        this.corpId = corpId;
    }

    public String getGroupDisplayName() {
        return groupDisplayName;
    }

    public void setGroupDisplayName(String groupDisplayName) {
        this.groupDisplayName = groupDisplayName;
    }

    public Integer getGroupMemListId() {
        return groupMemListId;
    }

    public void setGroupMemListId(Integer groupMemListId) {
        this.groupMemListId = groupMemListId;
    }

    public Integer getGroupDistPolicy() {
        return groupDistPolicy;
    }

    public void setGroupDistPolicy(Integer groupDistPolicy) {
        this.groupDistPolicy = groupDistPolicy;
    }

    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getPocHome() {
        return pocHome;
    }

    public void setPocHome(String pocHome) {
        this.pocHome = pocHome;
    }

    public Integer getHangTimeOut() {
        return hangTimeOut;
    }

    public void setHangTimeOut(Integer hangTimeOut) {
        this.hangTimeOut = hangTimeOut;
    }

    public Integer getOsmLisId() {
        return osmLisId;
    }

    public void setOsmLisId(Integer osmLisId) {
        this.osmLisId = osmLisId;
    }

    public String getObjectId() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Integer getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(Integer avatarId) {
        this.avatarId = avatarId;
    }

    public Integer getGroupEtag() { return groupEtag; }

    public void setGroupEtag(Integer groupEtag) { this.groupEtag = groupEtag; }

    public Integer getGroupCreatedBy() { return groupCreatedBy; }

    public void setGroupCreatedBy(Integer groupCreatedBy) { this.groupCreatedBy = groupCreatedBy; }

    public String getGroupOwner() {
        return groupOwner;
    }

    public void setGroupOwner(String groupOwner) {
        this.groupOwner = groupOwner;
    }

    public Integer getIsLargeGroup() {return isLargeGroup;}

    public void setIsLargeGroup(Integer isLargeGroup) {this.isLargeGroup = isLargeGroup;}

    @Override
    public String toString() {
        return "KnCorpGpInfoDTO{" +
                "corpgroupId=" + corpgroupId +
                ", corpId=" + corpId +
                ", groupDisplayName='" + groupDisplayName + '\'' +
                ", groupMemListId=" + groupMemListId +
                ", groupDistPolicy=" + groupDistPolicy +
                ", groupType='" + groupType + '\'' +
                ", groupName='" + groupName + '\'' +
                ", pocHome='" + pocHome + '\'' +
                ", hangTimeOut=" + hangTimeOut +
                ", osmLisId=" + osmLisId +
                ", avatarId=" + avatarId +
                ", mcxGrpInd=" + mcxGrpInd +
                ", isPreConfiguredGroup=" + isPreConfiguredGroup +
                ", groupEtag=" + groupEtag +
                ", groupCreatedBy=" + groupCreatedBy +
                ", groupOwner=" + groupOwner +
                ", isLargeGroup=" + isLargeGroup +
                '}';
    }
}

