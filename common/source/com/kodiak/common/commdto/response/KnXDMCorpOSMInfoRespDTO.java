/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.response;

import java.io.Serializable;
import java.util.Set;

public class KnXDMCorpOSMInfoRespDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 7612846385968531634L;
    private String OSMListName;
    private String OSMListId;
    private String defaultValue;
    private String count;
    private Set<KnXDMOSMInfoRespDTO> OSMMsgInfo;

    public KnXDMCorpOSMInfoRespDTO(){}

    public KnXDMCorpOSMInfoRespDTO(String OSMListName, String OSMListId, String defaultValue, String count) {
        this.OSMListName = OSMListName;
        this.OSMListId = OSMListId;
        this.defaultValue = defaultValue;
        this.count = count;
    }

    public String getOSMListName() {
        return OSMListName;
    }

    public void setOSMListName(String oSMListName) {
        OSMListName = oSMListName;
    }

    public String getOSMListId() {
        return OSMListId;
    }

    public void setOSMListId(String oSMListId) {
        OSMListId = oSMListId;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Set<KnXDMOSMInfoRespDTO> getOSMMsgInfo() {
        return OSMMsgInfo;
    }

    public void setOSMMsgInfo(Set<KnXDMOSMInfoRespDTO> oSMMsgInfo) {
        OSMMsgInfo = oSMMsgInfo;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "KnXDMCorpOSMInfoRespDTO{" +
                "OSMListName='" + OSMListName + '\'' +
                ", OSMListId='" + OSMListId + '\'' +
                ", defaultValue='" + defaultValue + '\'' +
                ", count='" + count + '\'' +
                ", OSMMsgInfo=" + OSMMsgInfo +
                '}';
    }
}
