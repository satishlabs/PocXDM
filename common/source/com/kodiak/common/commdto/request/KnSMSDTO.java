/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.request;

import com.kodiak.common.resources.KnGDPRTemplate;

/**
 * Created by nnamita on 15-06-2016.
 */
public class KnSMSDTO {

    private String mdn;
    private int recipientType;
    private int mesgValidity;
    private int msgNotificationId;
    private short dataEncodingScheme;
    private String countryCode;
    private String otp;
    private String tmpPwd;
    private String tmpPwdExpiry;

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    public int getRecipientType() {
        return recipientType;
    }

    public void setRecipientType(int recipientType) {
        this.recipientType = recipientType;
    }

    public int getMesgValidity() {
        return mesgValidity;
    }

    public void setMesgValidity(int mesgValidity) {
        this.mesgValidity = mesgValidity;
    }

    public int getMsgNotificationId() {
        return msgNotificationId;
    }

    public void setMsgNotificationId(int msgNotificationId) {
        this.msgNotificationId = msgNotificationId;
    }

    public short getDataEncodingScheme() {
        return dataEncodingScheme;
    }

    public void setDataEncodingScheme(short dataEncodingScheme) {
        this.dataEncodingScheme = dataEncodingScheme;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getTmpPwd() {
        return tmpPwd;
    }

    public void setTmpPwd(String tmpPwd) {
        this.tmpPwd = tmpPwd;
    }

    public String getTmpPwdExpiry() {
        return tmpPwdExpiry;
    }

    public void setTmpPwdExpiry(String tmpPwdExpiry) {
        this.tmpPwdExpiry = tmpPwdExpiry;
    }

    @Override
    public String toString() {
        return "KnSMSDTO{" +
                "mdn='" + KnGDPRTemplate.mdn(mdn) + '\'' +
                ", recipientType=" + recipientType +
                ", mesgValidity=" + mesgValidity +
                ", msgNotificationId=" + msgNotificationId +
                ", dataEncodingScheme=" + dataEncodingScheme +
                ", countryCode='" + countryCode + '\'' +
                ", otp='" + otp + '\'' +
                ", tmpPwd='" + tmpPwd + '\'' +
                ", tmpPwdExpiry='" + tmpPwdExpiry + '\'' +
                '}';
    }
}
