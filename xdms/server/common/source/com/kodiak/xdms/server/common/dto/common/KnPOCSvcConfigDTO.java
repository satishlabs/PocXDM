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
 * Ajit kumar           Jab 15, 2011       7.0
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
public class KnPOCSvcConfigDTO implements IIdentifier {

    private static final long serialVersionUID = 7526471155622676176L;

    private String pttServerId;
    private String primaryPOCServerURI;
    private String geoPOCServerURI;
    private String pOC_ConfFactoryURI;
    private int preestSessionValidity;
    private int pOCPublishValidity;
    private int enableInstaPOC;
    private int maxLegsInAdhocGrpCall;
    private int maxTermLegsInPOCGrpCall;
    private int tBCPReqTimeout;
    private int numTBCPRetriesByClient;
    private int mediaPortRefreshTime;
    private int numMediaPortKAMsgs;
    private int mediaPortKAMsgSize;
    private int mediaIdleTimer;
    private int maxFloorHoldDuration;
    private int floorIdleDetectionTimer;
    private int maxTalkBurstGraceTime;
    private int enableSuperVisoryOverride;
    private int enableMissedCallAlert;
    private int enableCallEndedAlert;
    private int sipRetryTimer;
    private int sipRetryTimerBackOff;
    private int ipDebounceTimer;
    private int client_TBCPFloorReqRetryTimer;
    private int client_TBCPRelReqRetryTimer;
    private int preCallNumMediaPortKAMsgs;
    private int precallMediaPortKAMsgSize;
    private int preCallMediaPortKAInterval;
    private int mediaPortKAInterval;
    private int preCallMediaKADuration;
    private int mediaSecureSesRefIntvl;
    private int clientInCallSusTimer;
    private int enablePocWifi;
    private int dynamicQosFlag;
    private int maxSDDSession;
    private int maxSDYSession;
    private Integer mcvideofloorholdtimer;
    public String getPttServerId() {
        return pttServerId;
    }

    public void setPttServerId(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    public String getPrimaryPOCServerURI() {
        return primaryPOCServerURI;
    }

    public void setPrimaryPOCServerURI(String primaryPOCServerURI) {
        this.primaryPOCServerURI = primaryPOCServerURI;
    }

    public String getGeoPOCServerURI() {
        return geoPOCServerURI;
    }

    public void setGeoPOCServerURI(String geoPOCServerURI) {
        this.geoPOCServerURI = geoPOCServerURI;
    }

    public String getPOC_ConfFactoryURI() {
        return pOC_ConfFactoryURI;
    }

    public void setPOC_ConfFactoryURI(String pOC_ConfFactoryURI) {
        this.pOC_ConfFactoryURI = pOC_ConfFactoryURI;
    }

    public int getPreestSessionValidity() {
        return preestSessionValidity;
    }

    public void setPreestSessionValidity(int preestSessionValidity) {
        this.preestSessionValidity = preestSessionValidity;
    }

    public int getPOCPublishValidity() {
        return pOCPublishValidity;
    }

    public void setPOCPublishValidity(int pOCPublishValidity) {
        this.pOCPublishValidity = pOCPublishValidity;
    }

    public int getEnableInstaPOC() {
        return enableInstaPOC;
    }

    public void setEnableInstaPOC(int enableInstaPOC) {
        this.enableInstaPOC = enableInstaPOC;
    }

    public int getMaxLegsInAdhocGrpCall() {
        return maxLegsInAdhocGrpCall;
    }

    public void setMaxLegsInAdhocGrpCall(int maxLegsInAdhocGrpCall) {
        this.maxLegsInAdhocGrpCall = maxLegsInAdhocGrpCall;
    }

    public int getMaxTermLegsInPOCGrpCall() {
        return maxTermLegsInPOCGrpCall;
    }

    public void setMaxTermLegsInPOCGrpCall(int maxTermLegsInPOCGrpCall) {
        this.maxTermLegsInPOCGrpCall = maxTermLegsInPOCGrpCall;
    }

    public int getTBCPReqTimeout() {
        return tBCPReqTimeout;
    }

    public void setTBCPReqTimeout(int tBCPReqTimeout) {
        this.tBCPReqTimeout = tBCPReqTimeout;
    }

    public int getNumTBCPRetriesByClient() {
        return numTBCPRetriesByClient;
    }

    public void setNumTBCPRetriesByClient(int numTBCPRetriesByClient) {
        this.numTBCPRetriesByClient = numTBCPRetriesByClient;
    }

    public int getMediaPortRefreshTime() {
        return mediaPortRefreshTime;
    }

    public void setMediaPortRefreshTime(int mediaPortRefreshTime) {
        this.mediaPortRefreshTime = mediaPortRefreshTime;
    }

    public int getNumMediaPortKAMsgs() {
        return numMediaPortKAMsgs;
    }

    public void setNumMediaPortKAMsgs(int numMediaPortKAMsgs) {
        this.numMediaPortKAMsgs = numMediaPortKAMsgs;
    }

    public int getMediaPortKAMsgSize() {
        return mediaPortKAMsgSize;
    }

    public void setMediaPortKAMsgSize(int mediaPortKAMsgSize) {
        this.mediaPortKAMsgSize = mediaPortKAMsgSize;
    }

    public int getMediaIdleTimer() {
        return mediaIdleTimer;
    }

    public void setMediaIdleTimer(int mediaIdleTimer) {
        this.mediaIdleTimer = mediaIdleTimer;
    }

    public int getMaxFloorHoldDuration() {
        return maxFloorHoldDuration;
    }

    public void setMaxFloorHoldDuration(int maxFloorHoldDuration) {
        this.maxFloorHoldDuration = maxFloorHoldDuration;
    }

    public int getFloorIdleDetectionTimer() {
        return floorIdleDetectionTimer;
    }

    public void setFloorIdleDetectionTimer(int floorIdleDetectionTimer) {
        this.floorIdleDetectionTimer = floorIdleDetectionTimer;
    }

    public String getpOC_ConfFactoryURI() {
        return pOC_ConfFactoryURI;
    }

    public void setpOC_ConfFactoryURI(String pOC_ConfFactoryURI) {
        this.pOC_ConfFactoryURI = pOC_ConfFactoryURI;
    }

    public int getpOCPublishValidity() {
        return pOCPublishValidity;
    }

    public void setpOCPublishValidity(int pOCPublishValidity) {
        this.pOCPublishValidity = pOCPublishValidity;
    }

    public int gettBCPReqTimeout() {
        return tBCPReqTimeout;
    }

    public void settBCPReqTimeout(int tBCPReqTimeout) {
        this.tBCPReqTimeout = tBCPReqTimeout;
    }

    public int getMaxTalkBurstGraceTime() {
        return maxTalkBurstGraceTime;
    }

    public void setMaxTalkBurstGraceTime(int maxTalkBurstGraceTime) {
        this.maxTalkBurstGraceTime = maxTalkBurstGraceTime;
    }

    public int getEnableSuperVisoryOverride() {
        return enableSuperVisoryOverride;
    }

    public void setEnableSuperVisoryOverride(int enableSuperVisoryOverride) {
        this.enableSuperVisoryOverride = enableSuperVisoryOverride;
    }

    public int getEnableMissedCallAlert() {
        return enableMissedCallAlert;
    }

    public void setEnableMissedCallAlert(int enableMissedCallAlert) {
        this.enableMissedCallAlert = enableMissedCallAlert;
    }

    public int getEnableCallEndedAlert() {
        return enableCallEndedAlert;
    }

    public void setEnableCallEndedAlert(int enableCallEndedAlert) {
        this.enableCallEndedAlert = enableCallEndedAlert;
    }

    public int getSipRetryTimer() {
        return sipRetryTimer;
    }

    public void setSipRetryTimer(int sipRetryTimer) {
        this.sipRetryTimer = sipRetryTimer;
    }

    public int getSipRetryTimerBackOff() {
        return sipRetryTimerBackOff;
    }

    public void setSipRetryTimerBackOff(int sipRetryTimerBackOff) {
        this.sipRetryTimerBackOff = sipRetryTimerBackOff;
    }

    public int getIpDebounceTimer() {
        return ipDebounceTimer;
    }

    public void setIpDebounceTimer(int ipDebounceTimer) {
        this.ipDebounceTimer = ipDebounceTimer;
    }

    public int getClient_TBCPFloorReqRetryTimer() {
        return client_TBCPFloorReqRetryTimer;
    }

    public void setClient_TBCPFloorReqRetryTimer(int client_TBCPFloorReqRetryTimer) {
        this.client_TBCPFloorReqRetryTimer = client_TBCPFloorReqRetryTimer;
    }

    public int getClient_TBCPRelReqRetryTimer() {
        return client_TBCPRelReqRetryTimer;
    }

    public void setClient_TBCPRelReqRetryTimer(int client_TBCPRelReqRetryTimer) {
        this.client_TBCPRelReqRetryTimer = client_TBCPRelReqRetryTimer;
    }

    public int getPreCallNumMediaPortKAMsgs() {
        return preCallNumMediaPortKAMsgs;
    }

    public void setPreCallNumMediaPortKAMsgs(int preCallNumMediaPortKAMsgs) {
        this.preCallNumMediaPortKAMsgs = preCallNumMediaPortKAMsgs;
    }

    public int getPrecallMediaPortKAMsgSize() {
        return precallMediaPortKAMsgSize;
    }

    public void setPrecallMediaPortKAMsgSize(int precallMediaPortKAMsgSize) {
        this.precallMediaPortKAMsgSize = precallMediaPortKAMsgSize;
    }

    public int getPreCallMediaPortKAInterval() {
        return preCallMediaPortKAInterval;
    }

    public void setPreCallMediaPortKAInterval(int preCallMediaPortKAInterval) {
        this.preCallMediaPortKAInterval = preCallMediaPortKAInterval;
    }

    public int getMediaPortKAInterval() {
        return mediaPortKAInterval;
    }

    public void setMediaPortKAInterval(int mediaPortKAInterval) {
        this.mediaPortKAInterval = mediaPortKAInterval;
    }

    public int getPreCallMediaKADuration() {
        return preCallMediaKADuration;
    }

    public void setPreCallMediaKADuration(int preCallMediaKADuration) {
        this.preCallMediaKADuration = preCallMediaKADuration;
    }

    public int getMediaSecureSesRefIntvl() {
        return mediaSecureSesRefIntvl;
    }

    public void setMediaSecureSesRefIntvl(int mediaSecureSesRefIntvl) {
        this.mediaSecureSesRefIntvl = mediaSecureSesRefIntvl;
    }

    public int getClientInCallSusTimer() {
        return clientInCallSusTimer;
    }

    public void setClientInCallSusTimer(int clientInCallSusTimer) {
        this.clientInCallSusTimer = clientInCallSusTimer;
    }

    public int getEnablePocWifi() {
        return enablePocWifi;
    }

    public void setEnablePocWifi(int enablePocWifi) {
        this.enablePocWifi = enablePocWifi;
    }
    public int getDynamicQosFlag() {
        return dynamicQosFlag;
    }

    public void setDynamicQosFlag(int dynamicQosFlag) {
        this.dynamicQosFlag = dynamicQosFlag;
    }

    public int getMaxSDDSession() { return maxSDDSession; }

    public void setMaxSDDSession(int maxSDDSession) { this.maxSDDSession = maxSDDSession; }

    public int getMaxSDYSession() { return maxSDYSession; }

    public void setMaxSDYSession(int maxSDYSession) { this.maxSDYSession = maxSDYSession; }

    public Integer getMcvideofloorholdtimer() {return mcvideofloorholdtimer; }

    public void setMcvideofloorholdtimer(Integer mcvideofloorholdtimer) {this.mcvideofloorholdtimer = mcvideofloorholdtimer; }

    public String toString() {
        StringBuffer strBuffer = new StringBuffer(200);
        strBuffer.append(super.toString());
        strBuffer.append(", pttServerId - ").append(pttServerId);
        strBuffer.append(", primaryPOCServerURI - ").append(primaryPOCServerURI);
        strBuffer.append(", geoPOCServerURI - ").append(geoPOCServerURI);
        strBuffer.append(", pOC_ConfFactoryURI - ").append(pOC_ConfFactoryURI);
        strBuffer.append(", preestSessionValidity - ").append(preestSessionValidity);
        strBuffer.append(", pOCPublishValidity - ").append(pOCPublishValidity);

        strBuffer.append(", enableInstaPOC - ").append(enableInstaPOC);
        strBuffer.append(", maxLegsInAdhocGrpCall - ").append(maxLegsInAdhocGrpCall);
        strBuffer.append(", maxTermLegsInPOCGrpCall - ").append(maxTermLegsInPOCGrpCall);
        strBuffer.append(", tBCPReqTimeout - ").append(tBCPReqTimeout);
        strBuffer.append(", numTBCPRetriesByClient - ").append(numTBCPRetriesByClient);
        strBuffer.append(", mediaPortRefreshTime - ").append(mediaPortRefreshTime);

        strBuffer.append(", numMediaPortKAMsgs - ").append(numMediaPortKAMsgs);
        strBuffer.append(", mediaPortKAMsgSize - ").append(mediaPortKAMsgSize);
        strBuffer.append(", mediaIdleTimer - ").append(mediaIdleTimer);
        strBuffer.append(", maxFloorHoldDuration - ").append(maxFloorHoldDuration);
        strBuffer.append(", floorIdleDetectionTimer - ").append(floorIdleDetectionTimer);
        strBuffer.append(", maxTalkBurstGraceTime - ").append(maxTalkBurstGraceTime);
        strBuffer.append(", enableSuperVisoryOverride - ").append(enableSuperVisoryOverride);
        strBuffer.append(", enableMissedCallAlert - ").append(enableMissedCallAlert);
        strBuffer.append(", enableCallEndedAlert - ").append(enableCallEndedAlert);
        strBuffer.append(", sipRetryTimer - ").append(sipRetryTimer);
        strBuffer.append(", sipRetryTimerBackOff - ").append(sipRetryTimerBackOff);
        strBuffer.append(", ipDebounceTimer - ").append(ipDebounceTimer);
        strBuffer.append(", client_TBCPFloorReqRetryTimer - ").append(client_TBCPFloorReqRetryTimer);
        strBuffer.append(", client_TBCPRelReqRetryTimer - ").append(client_TBCPRelReqRetryTimer);
        strBuffer.append(", PreCallNumMediaPortKAMsgs - ").append(preCallNumMediaPortKAMsgs);
        strBuffer.append(", PrecallMediaPortKAMsgSize - ").append(precallMediaPortKAMsgSize);
        strBuffer.append(", PreCallMediaPortKAInterval  - ").append(preCallMediaPortKAInterval);
        strBuffer.append(", MediaPortKAInterval  - ").append(mediaPortKAInterval);
        strBuffer.append(", PreCallMediaKADuration  - ").append(preCallMediaKADuration);
        strBuffer.append(", mediaSecureSessionRefIntvl  - ").append(mediaSecureSesRefIntvl);
        strBuffer.append(", ClientInCallSuspendTimer  - ").append(clientInCallSusTimer);
        strBuffer.append(", Enable_POC_WIFI  - ").append(enablePocWifi);
        strBuffer.append(", dynamic_Qos_Flag  - ").append(dynamicQosFlag);
        strBuffer.append(", maxsimuldedeicatedsession - ").append(maxSDDSession);
        strBuffer.append(", maxsimuldynamicsession - ").append(maxSDYSession);
        strBuffer.append(", mcvideofloorholdtimer - ").append(mcvideofloorholdtimer);


        return strBuffer.toString();
    }

    public String getObjectId() {
        return pttServerId;
    }
}
