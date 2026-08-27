/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.request;

import com.kodiak.common.dto.IIdentifier;

import java.util.Set;

public class KnCorpOperationStatusMesssageInfoDTO implements IIdentifier {

    private static final long serialVersionUID = 1L;

    private String OSMListName;
    private String OSMListId;
    private String isDefault;
    private String count;
    private Set<KnXDMOSMInfoRequestDTO> OSMMsgInfo;

    public KnCorpOperationStatusMesssageInfoDTO(){

    }

    public KnCorpOperationStatusMesssageInfoDTO(String OSMListId, String OSMListName, String isDefault,String count) {
        this.OSMListId = OSMListId;
        this.OSMListName = OSMListName;
        this.isDefault = isDefault;
        this.count=count;
    }

    public String getOSMListName() {
        return OSMListName;
    }

    public void setOSMListName(String OSMListName) {
        this.OSMListName = OSMListName;
    }

    public String getOSMListId() {
        return OSMListId;
    }

    public void setOSMListId(String OSMListId) {
        this.OSMListId = OSMListId;
    }

    public String getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(String isDefault) {
        this.isDefault = isDefault;
    }

    public Set<KnXDMOSMInfoRequestDTO> getOSMMsgInfo() {
        return OSMMsgInfo;
    }

    public void setOSMMsgInfo(Set<KnXDMOSMInfoRequestDTO> OSMMsgInfo) {
        this.OSMMsgInfo = OSMMsgInfo;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "KnCorpOperationStatusMesssageInfoDTO{" +
                "OSMListName='" + OSMListName + '\'' +
                ", OSMListId='" + OSMListId + '\'' +
                ", isDefault='" + isDefault + '\'' +
                ", OSMMsgInfo=" + OSMMsgInfo +
                ", count=" + count +
                '}';
    }

    @Override
    public String getObjectId() {
        return OSMListId;
    }
}

