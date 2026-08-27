/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.subsmgmt.dto.common;

import com.kodiak.common.dto.IIdentifier;


/**
 * Created by Deepak on 8/4/14.
 */
public class KnExtSubscriberDTO implements IIdentifier {
    private static final long serialVersionUID = -4240452710404503562L;
    private String mdn;
    private int profileId;
    private int setBy;

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
        return "KnExtSubscriberDTO{" +
                "mdn='" + mdn + '\'' +
                ", profileId=" + profileId +
                ", setBy=" + setBy +
                '}';
    }

    /*@Override
    public boolean equals(Object o) {
          KnExtSubscriberDTO that = (KnExtSubscriberDTO) o;
       if(this.mdn.equals(that.mdn)){
           return true;
       }
        return false;
    }*/

    @Override
    public boolean equals(Object o) {
        boolean result;
        if (o == null || o.getClass() != getClass()) {
            result = false;
        } else {
            KnExtSubscriberDTO subscriberDTO = (KnExtSubscriberDTO) o;
            result = this.mdn.equals(subscriberDTO.mdn);
        }
        return result;
    }



    @Override
    public int hashCode() {
        return mdn != null ? mdn.hashCode() : 0;
    }
}
