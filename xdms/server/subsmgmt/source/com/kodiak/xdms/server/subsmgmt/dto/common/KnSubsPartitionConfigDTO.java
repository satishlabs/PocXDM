/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ***********************************************************************
 * <p/>
 * File name:  KnTestCaseConstants.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Ravi Shanker P       2/4/13       7.5
 * <p/>
 * <p/>
 * #401, 4th Floor, 'Prestige Sigma'
 * No.3, Vittal Mallya Road
 * Bangalore - 560 001
 * www.kodiaknetworks.com
 * All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ***********************************************************************
 */
package com.kodiak.xdms.server.subsmgmt.dto.common;

public class KnSubsPartitionConfigDTO {

    private String pttServerId;
    private int mdnPartitionTypePOC;
    private int EnableCorpAccAnch;
    private Integer maxSubsPerXDMS;
    private Integer maxSubsPerPoC;

    /**
     * @return
     */
    public String getPttServerId() {
        return pttServerId;
    }

    /**
     * @param pttServerId
     */
    public void setPttServerId(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    /**
     * @return
     */
    public int getMdnPartitionTypePOC() {
        return mdnPartitionTypePOC;
    }

    /**
     * @param mdnPartitionTypePOC
     */
    public void setMdnPartitionTypePOC(int mdnPartitionTypePOC) {
        this.mdnPartitionTypePOC = mdnPartitionTypePOC;
    }

    /**
     * @return
     */
    public int getEnableCorpAccAnch() {
        return EnableCorpAccAnch;
    }

    /**
     * @param enableCorpAccAnch
     */
    public void setEnableCorpAccAnch(int enableCorpAccAnch) {
        EnableCorpAccAnch = enableCorpAccAnch;
    }

    /**
     * @return
     */
    public Integer getMaxSubsPerXDMS() {
        return maxSubsPerXDMS;
    }

    /**
     * @param maxSubsPerXDMS
     */
    public void setMaxSubsPerXDMS(Integer maxSubsPerXDMS) {
        this.maxSubsPerXDMS = maxSubsPerXDMS;
    }

    /**
     * @return
     */
    public Integer getMaxSubsPerPoC() {
        return maxSubsPerPoC;
    }

    /**
     * @param maxSubsPerPoC
     */
    public void setMaxSubsPerPoC(Integer maxSubsPerPoC) {
        this.maxSubsPerPoC = maxSubsPerPoC;
    }


    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("KnSubsPartitionConfigDTO [")
                .append("pttServerId -").append(pttServerId)
                .append(", mdnPartitionTypePOC = ").append(mdnPartitionTypePOC)
                .append(", EnableCorpAccAnch = ").append(EnableCorpAccAnch)
                .append(", maxSubsPerXDMS = ").append(maxSubsPerXDMS)
                .append(", maxSubsPerPoC = ").append(maxSubsPerPoC)
                .append("]");

        return strBuilder.toString();
    }


}
