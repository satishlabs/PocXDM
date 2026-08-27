/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common.dto.common;

import com.kodiak.common.dto.IIdentifier;

/**
 * *****************************************************************************
 * <p/>
 * Subsystem:   POC
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ajit kumar           Aug 31, 2012       7.4
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
 * *******************************************************************************
 */
public class KnSIPProxySvcConfigDTO implements IIdentifier {

    private static final long serialVersionUID = 7526471155622676300L;

    private String pttServerId;
    private String sipProxyURI;
    private int clientConnRetryInterval;
    private int maxClientConnRtyAttempts;
    private int clientConnSecurityLevel;
    private int clientSipTxnTimeout;
    private int clientSipReferTxnTimeout;
    private int minTcpKaTimerOnWifi;
    private int wifiTcpKaTimerIncrVal;
    private int maxTcpKaTimerOnWifi;
    private int wifiSsidTimeoutMapSize;
    private int tcpKaTimerOnMacroCellular;
    private int detectWifiNatTcpTimeout;
    private String sipProxyURIForBCS;
    private String geoSipProxyUri;
    private String sipProxyUriWifi;
    private String geoSipProxyUriWifi;

    public String getPttServerId() {
        return pttServerId;
    }

    public void setPttServerId(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    public String getSipProxyURI() {
        return sipProxyURI;
    }

    public void setSipProxyURI(String sipProxyURI) {
        this.sipProxyURI = sipProxyURI;
    }

    public int getClientConnRetryInterval() {
        return clientConnRetryInterval;
    }

    public void setClientConnRetryInterval(int clientConnRetryInterval) {
        this.clientConnRetryInterval = clientConnRetryInterval;
    }

    public int getMaxClientConnRtyAttempts() {
        return maxClientConnRtyAttempts;
    }

    public void setMaxClientConnRtyAttempts(int maxClientConnRtyAttempts) {
        this.maxClientConnRtyAttempts = maxClientConnRtyAttempts;
    }

    public int getClientConnSecurityLevel() {
        return clientConnSecurityLevel;
    }

    public void setClientConnSecurityLevel(int clientConnSecurityLevel) {
        this.clientConnSecurityLevel = clientConnSecurityLevel;
    }

    public int getClientSipTxnTimeout() {
        return clientSipTxnTimeout;
    }

    public void setClientSipTxnTimeout(int clientSipTxnTimeout) {
        this.clientSipTxnTimeout = clientSipTxnTimeout;
    }

    public int getClientSipReferTxnTimeout() {
        return clientSipReferTxnTimeout;
    }

    public void setClientSipReferTxnTimeout(int clientSipReferTxnTimeout) {
        this.clientSipReferTxnTimeout = clientSipReferTxnTimeout;
    }

    public int getMinTcpKaTimerOnWifi() {
        return minTcpKaTimerOnWifi;
    }

    public void setMinTcpKaTimerOnWifi(int minTcpKaTimerOnWifi) {
        this.minTcpKaTimerOnWifi = minTcpKaTimerOnWifi;
    }

    public int getWifiTcpKaTimerIncrVal() {
        return wifiTcpKaTimerIncrVal;
    }

    public void setWifiTcpKaTimerIncrVal(int wifiTcpKaTimerIncrVal) {
        this.wifiTcpKaTimerIncrVal = wifiTcpKaTimerIncrVal;
    }

    public int getMaxTcpKaTimerOnWifi() {
        return maxTcpKaTimerOnWifi;
    }

    public void setMaxTcpKaTimerOnWifi(int maxTcpKaTimerOnWifi) {
        this.maxTcpKaTimerOnWifi = maxTcpKaTimerOnWifi;
    }

    public int getWifiSsidTimeoutMapSize() {
        return wifiSsidTimeoutMapSize;
    }

    public void setWifiSsidTimeoutMapSize(int wifiSsidTimeoutMapSize) {
        this.wifiSsidTimeoutMapSize = wifiSsidTimeoutMapSize;
    }

    public int getTcpKaTimerOnMacroCellular() {
        return tcpKaTimerOnMacroCellular;
    }

    public void setTcpKaTimerOnMacroCellular(int tcpKaTimerOnMacroCellular) {
        this.tcpKaTimerOnMacroCellular = tcpKaTimerOnMacroCellular;
    }

    public int getDetectWifiNatTcpTimeout() {
        return detectWifiNatTcpTimeout;
    }

    public void setDetectWifiNatTcpTimeout(int detectWifiNatTcpTimeout) {
        this.detectWifiNatTcpTimeout = detectWifiNatTcpTimeout;
    }

    public String getSipProxyURIForBCS() {
        return sipProxyURIForBCS;
    }

    public void setSipProxyURIForBCS(String sipProxyURIForBCS) {
        this.sipProxyURIForBCS = sipProxyURIForBCS;
    }

    public String getGeoSipProxyUri() {
        return geoSipProxyUri;
    }

    public void setGeoSipProxyUri(String geoSipProxyUri) {
        this.geoSipProxyUri = geoSipProxyUri;
    }

    public String getSipProxyUriWifi() {
        return sipProxyUriWifi;
    }

    public void setSipProxyUriWifi(String sipProxyUriWifi) {
        this.sipProxyUriWifi = sipProxyUriWifi;
    }

    public String getGeoSipProxyUriWifi() {
        return geoSipProxyUriWifi;
    }

    public void setGeoSipProxyUriWifi(String geoSipProxyUriWifi) {
        this.geoSipProxyUriWifi = geoSipProxyUriWifi;
    }

    public String toString() {
        StringBuffer strBuffer = new StringBuffer(200);
        strBuffer.append(super.toString());
        strBuffer.append(", pttServerId - ").append(pttServerId);
        strBuffer.append(", sipProxyURI - ").append(sipProxyURI);
        strBuffer.append(", clientConnRetryInterval - ").append(clientConnRetryInterval);
        strBuffer.append(", maxClientConnRtyAttempts - ").append(maxClientConnRtyAttempts);
        strBuffer.append(", clientConnSecurityLevel - ").append(clientConnSecurityLevel);
        strBuffer.append(", clientSipTxnTimeout - ").append(clientSipTxnTimeout);
        strBuffer.append(", clientSipReferTxnTimeout - ").append(clientSipReferTxnTimeout);
        strBuffer.append(", minTcpKaTimerOnWifi - ").append(minTcpKaTimerOnWifi);
        strBuffer.append(", wifiTcpKaTimerIncrVal - ").append(wifiTcpKaTimerIncrVal);
        strBuffer.append(", maxTcpKaTimerOnWifi - ").append(maxTcpKaTimerOnWifi);
        strBuffer.append(", wifiSsidTimeoutMapSize - ").append(wifiSsidTimeoutMapSize);
        strBuffer.append(", tcpKaTimerOnMacroCellular - ").append(tcpKaTimerOnMacroCellular);
        strBuffer.append(", detectWifiNatTcpTimeout - ").append(detectWifiNatTcpTimeout);
        strBuffer.append(", sipProxyURIForBCS - ").append(sipProxyURIForBCS);

        return strBuffer.toString();
    }

    public String getObjectId() {
        return pttServerId;  //To change body of implemented methods use File | Settings | File Templates.
    }
}

