/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnCorpContactRespDTO.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Feb 10, 2011      7.0
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
package com.kodiak.xdms.server.corpmgmt.dto.impl;


import com.kodiak.common.resources.KnGDPRTemplate;

public class KnCorpContactRespDTO extends KnCorpResponseDTO {

    private String corpId;
    private String contactCorpId;
    private String contactMdn;
    private String contactName;

    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    public String getContactCorpId() {
        return contactCorpId;
    }

    public void setContactCorpId(String contactCorpId) {
        this.contactCorpId = contactCorpId;
    }

    public String getContactMdn() {
        return contactMdn;
    }

    public void setContactMdn(String contactMdn) {
        this.contactMdn = contactMdn;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(100);
        sb.append(", CorpId - ").append(corpId)
                .append(", ContactCorpId - ").append(contactCorpId)
                .append(", ContactMdn - ").append(KnGDPRTemplate.mdn(contactMdn))
                .append(", ContactName - ").append(KnGDPRTemplate.name(contactName));
        return sb.toString();

    }
}
