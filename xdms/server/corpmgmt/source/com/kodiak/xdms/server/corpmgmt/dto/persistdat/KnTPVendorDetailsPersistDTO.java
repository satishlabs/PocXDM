/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.persistdat;

import com.kodiak.common.resources.KnGDPRTemplate;

public class KnTPVendorDetailsPersistDTO {
	private Integer tpAccountId;
    private String tpAccount;
    private String password;
    private String emailId;
    private Long createTs;
    private Long lastUpdateTs;

    public Integer getTpAccountId() {
        return tpAccountId;
    }

    public void setTpAccountId(Integer tpAccountId) {
        this.tpAccountId = tpAccountId;
    }

    public String getTpAccount() {
        return tpAccount;
    }

    public void setTpAccount(String tpAccount) {
        this.tpAccount = tpAccount;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public void setCreateTs(Long createTs) {
        this.createTs = createTs;
    }

    public Long getLastUpdateTs() {
        return lastUpdateTs;
    }

    public void setLastUpdateTs(Long lastUpdateTs) {
        this.lastUpdateTs = lastUpdateTs;
    }

    public Long getCreateTs() {
        return createTs;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        super.toString();
        sb.append("tpAccountId -").append(tpAccountId).append(",tpAccount -")
                .append(tpAccount).append(",password -").append(password)
                .append(" ,emailId - ").append(KnGDPRTemplate.email(emailId))
                .append(" ,createTs - ").append(createTs)
                .append(" ,lastUpdateTs - ").append(lastUpdateTs);
        return sb.toString();
    }
}
