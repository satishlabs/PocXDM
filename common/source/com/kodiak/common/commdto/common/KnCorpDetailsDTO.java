/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.common;


import java.util.List;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnCorpDetailsDTO.java
 * Subsystem:  PoCXDM
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Saurabh Kumar      June 23, 2016      8.1
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
 * ************************************************************************
 */
public class KnCorpDetailsDTO extends KnStatusInfo {
    List<KnIdDetails> idDetails;
    String corpId;
    String extCorpId;
    String corpName;

    public List<KnIdDetails> getIdDetails() {
        return idDetails;
    }

    public void setIdDetails(List<KnIdDetails> idDetails) {
        this.idDetails = idDetails;
    }

    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    public String getExtCorpId() {
        return extCorpId;
    }

    public void setExtCorpId(String extCorpId) {
        this.extCorpId = extCorpId;
    }

    public String getCorpName() {
        return corpName;
    }

    public void setCorpName(String corpName) {
        this.corpName = corpName;
    }

    public String toString() {
        StringBuffer strBuffer = new StringBuffer(100);
        strBuffer.append("[KnCorpDetailsDTO --> ");
        strBuffer.append(" corpId - ").append(corpId)
                .append(", extCorpId - ").append(extCorpId)
                .append(", corpName - ").append(corpName)
                .append(", idDetails - ").append(idDetails)
                .append("]");
        return strBuffer.toString();
    }
}
