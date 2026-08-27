/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.response;


/**
 * ************************************************************************
 * <p>
 * File name:  KnXDMMCVideoUEConfigRespDTO.java
 * Subsystem:  PoC
 * <p>
 * Name                         Date                     Release
 * --------------------     ----------------        ------------------
 * Shashank Tewari            July 29, 2019                9.1.1
 * <p>
 * <p>
 * KODIAK, 9th Floor, 'MFar Manyata Tech Park'
 * Greenheart Phase IV, Nagawara
 * Bangalore - 560 045
 * www.kodiakptt.com
 * All Rights Reserved.
 * <p>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */

public class KnXDMMCVideoUEConfigRespDTO extends KnXDMRespDTO {
    private static final long serialVersionUID = -2522574492063787041L;
    private String domain ;
    private String domainName;
    private boolean  ipv6Preferred;
    private String sipProxyURI;
    private String geoSipProxyURI;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public boolean isIpv6Preferred() {
        return ipv6Preferred;
    }

    public void setIpv6Preferred(boolean ipv6Preferred) {
        this.ipv6Preferred = ipv6Preferred;
    }

    public String getSipProxyURI() { return sipProxyURI; }

    public void setSipProxyURI(String sipProxyURI) { this.sipProxyURI = sipProxyURI; }

    public String getGeoSipProxyURI() {	return geoSipProxyURI; }

    public void setGeoSipProxyURI(String geoSipProxyURI) { this.geoSipProxyURI = geoSipProxyURI; }


    public String toString() {
        StringBuilder strBuffer = new StringBuilder(200);
        strBuffer.append(super.toString());
        strBuffer.append(" domain - ").append(domain)
                .append(", domainName - ").append(domainName)
                .append(", ipv6Preferred - ").append(ipv6Preferred)
                .append(", sipProxyURI - ").append(sipProxyURI)
                .append(", geoSipProxyURI - ").append(geoSipProxyURI);
        return strBuffer.toString();
    }
}
