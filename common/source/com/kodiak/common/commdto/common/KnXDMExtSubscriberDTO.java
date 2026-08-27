/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.common;

import com.kodiak.common.dto.IIdentifier;
import com.kodiak.common.resources.KnGDPRTemplate;


/**
 * Created by Deepak on 8/4/14.
 */
public class KnXDMExtSubscriberDTO implements IIdentifier {

    private static final long serialVersionUID = 4945169275997033881L;
    private String mdn;
    private int profileId;
    private int setBy;

    public KnXDMExtSubscriberDTO() {
    }

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    public int getProfileId() {
        return profileId;
    }

    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }

    public int getSetBy() {
        return setBy;
    }

    public void setSetBy(int setBy) {
        this.setBy = setBy;
    }


    @Override
    public String getObjectId() {
        return mdn;
    }

    @Override
    public String toString() {
        return "KnXDMExtSubscriberDTO{" +
                "mdn='" + KnGDPRTemplate.mdn(mdn) + '\'' +
                ", profileId=" + profileId +
                ", setBy=" + setBy +
                '}';
    }
}
