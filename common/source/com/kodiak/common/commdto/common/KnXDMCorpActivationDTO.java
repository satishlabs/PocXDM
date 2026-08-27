/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.common;

/**
 * Created by IntelliJ IDEA.
 * User: kodiak
 * Date: 23/5/13
 * Time: 5:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class KnXDMCorpActivationDTO extends KnXDMMdnInfoDTO {

    private static final long serialVersionUID = 7526471155622676326L;

    private String activationCode;
    private String activationTimestamp;
    private String expiryTimestamp;

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public String getActivationTimestamp() {
        return activationTimestamp;
    }

    public void setActivationTimestamp(String activationTimestamp) {
        this.activationTimestamp = activationTimestamp;
    }

    public String getExpiryTimestamp() {
        return expiryTimestamp;
    }

    public void setExpiryTimestamp(String expiryTimestamp) {
        this.expiryTimestamp = expiryTimestamp;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(200);
        sb.append(super.toString())
                .append(", activationCode - ").append(activationCode)
                .append(", activationTimestamp - ").append(activationTimestamp);
        return sb.toString();
    }
}
