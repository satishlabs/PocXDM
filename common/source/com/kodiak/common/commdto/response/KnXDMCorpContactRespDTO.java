/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMCorpContactRespDTO.java
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
package com.kodiak.common.commdto.response;

import com.kodiak.common.resources.KnGDPRTemplate;

public class KnXDMCorpContactRespDTO extends KnXDMCorpRespDTO {

    private static final long serialVersionUID = 7526471155622776148L;

    private String corpId;
    private String contactCorpId;
    private String mdn;
    private String name;

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

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(200);
        sb.append(super.toString())
                .append(", CorpId - ").append(corpId)
                .append(", ContactCorpId - ").append(contactCorpId)
                .append(", mdn - ").append(KnGDPRTemplate.mdn(mdn))
                .append(", name - ").append(KnGDPRTemplate.name(name));
        return sb.toString();
    }
}
