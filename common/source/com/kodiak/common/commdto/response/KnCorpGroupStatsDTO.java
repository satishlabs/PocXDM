/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.response;

import java.io.Serializable;

public class KnCorpGroupStatsDTO implements Serializable {

    private String groupCount;

    private String stdGroups;

    private String interopGroups;

    private String largeGroups;

    private String mcxGroups;

    public String getGroupCount() {
        return groupCount;
    }

    public void setGroupCount(String groupCount) {
        this.groupCount = groupCount;
    }

    public String getStdGroups() {
        return stdGroups;
    }

    public void setStdGroups(String stdGroups) {
        this.stdGroups = stdGroups;
    }

    public String getInteropGroups() {
        return interopGroups;
    }

    public void setInteropGroups(String interopGroups) {
        this.interopGroups = interopGroups;
    }

    public String getLargeGroups() {
        return largeGroups;
    }

    public void setLargeGroups(String largeGroups) {
        this.largeGroups = largeGroups;
    }

    public String getMcxGroups() {
        return mcxGroups;
    }

    public void setMcxGroups(String mcxGroups) {
        this.mcxGroups = mcxGroups;
    }

    @Override
    public String toString() {
        return "{" +
                "groupCount='" + groupCount + '\'' +
                ", stdGroups='" + stdGroups + '\'' +
                ", interopGroups='" + interopGroups + '\'' +
                ", largeGroups='" + largeGroups + '\'' +
                ", mcxGroups='" + mcxGroups + '\'' +
                '}';
    }
}
