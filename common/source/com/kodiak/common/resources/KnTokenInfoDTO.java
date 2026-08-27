/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.resources;

import java.util.Date;

/**
 * *****************************************************************************
 * File name:   KnTokenInfoDTO.java
 * Subsystem: PoC
 * <p>
 * Name                    Date            Release
 * -----------------    -----------       -------
 * supananda              10/17/2016            1.0
 * <p>
 * <p>
 * 9th Floor, 'MFar', Manyata Tech Park
 * Nagawara
 * Bangalore - 560 045
 * www.kodiakptt.com
 * All Rights Reserved.
 * <p>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * *******************************************************************************
 */
public class KnTokenInfoDTO {

    private String subject;
    private String userName;
    private String issuer;
    private Date exp;
    private String audiance;
    private String scope;
    private String serviceType;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public Date getExp() {
        return exp;
    }

    public void setExp(Date exp) {
        this.exp = exp;
    }

    public String getAudiance() {
        return audiance;
    }

    public void setAudiance(String audiance) {
        this.audiance = audiance;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(200);
        sb.append("subject - ").append(subject).append(", userName - ").append(userName)
                .append(", issuer - ").append(issuer).append(", exp - ").append(exp)
                .append(", secret - Not printable").append(", audiance - ").append(audiance)
                .append(", scope - ").append(scope).append(", Service type - ").append(serviceType);
        return sb.toString();
    }
}
