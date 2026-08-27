/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.response;
import com.kodiak.common.dto.IIdentifier;

public class KnXDMCorpAccountsList implements IIdentifier {

    private static final long serialVersionUID = -7989321401648884019L;
    private String accountId;
    private String corporateName;
    private Boolean locationEnabled;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getCorporateName() {
        return corporateName;
    }

    public void setCorporateName(String corporateName) {
        this.corporateName = corporateName;
    }

    public Boolean getLocationEnabled() {
        return locationEnabled;
    }

    public void setLocationEnabled(Boolean locationEnabled) {
        this.locationEnabled = locationEnabled;
    }

    @Override
    public String toString() {
        return "KnXDMCorpAccountsList{" +
                "accountId='" + accountId + '\'' +
                ", corporateName='" + corporateName + '\'' +
                ", locationEnabled=" + locationEnabled +
                '}';
    }

    @Override
    public String getObjectId() {
        return null;
    }
}
