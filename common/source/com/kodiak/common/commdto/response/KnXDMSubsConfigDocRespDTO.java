/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *****************************************************************************
 * File name:   KnXDMSubsConfigDocRespDTO.java
 * Subsystem:   COMMON DTO
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ravi Shanker .P       12/22/10       7.0
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

package com.kodiak.common.commdto.response;


import com.kodiak.common.resources.KnGDPRTemplate;

public class KnXDMSubsConfigDocRespDTO extends KnXDMRespDTO {

    private static final long serialVersionUID = 7526471155622676163L;
    //stores public subscription Type
    private int pubSubscriptionType;
    //Stores corporate subscription Type;
    private int corpSubscriptionType;
    //stores Service auth status
    private int subscriptionState;
    //stores Subscriber Name
    private String networkName;
    //stores subscriber mdn
    private String mdn;
    private String clientPVmajorVer;
    //stores the XUI
    private String xui;
    //Stores primary Registrar URI
    private String primaryRegistrarRoute;
    //Stores Geo Registrar URI
    private String geoRegistrarRoute;
    //Stores primary Session URI
    private String primarySessionRoute;
    //Stores Geo Session URI
    private String geoSessionRoute;
    //Stores primary POC Settings URI
    private String primaryPocSettingRoute;
    //Stores Geo POC Settings URI
    private String geoPocSettingRoute;
    //Stores primary IPA  URI
    private String primaryIpaRoute;
    //Stores Geo IPA URI
    private String geoIpaRoute;
    //Stores primary Presence URI
    private String primaryPresenceRoute;
    //Stores Geo Presence URI
    private String geoPresenceRoute;
    //Stores primary RLS  URI
    private String primaryRlsRoute;
    //Stores Geo RLS URI
    private String geoRlsRoute;
    //Stores XCAP Root  URI
    private String xcapRootUri;
    //Stores primary XDMS URI
    private String primaryXdmsRoute;
    //Stores Geo XDMS URI
    private String geoXdmsRoute;
    //Stores Max Public Contacts
    private Integer maxPublicContacts;
    //Stores Max Corporate Contacts
    private Integer maxCorporateContacts;
    //Stores Max Public Groups
    private Integer maxPublicGroups;
    //Stores Max Corporate Groups
    private Integer maxCorporateGroups;
    //Stores Max Member Per Public Group
    private Integer maxMembersPerPublicGroup;
    //Stores Max members per corp group
    private Integer maxMembersPerCorpGroup;
    //Stores Max adhoc group size
    private Integer maxAdhocGroupSize;
    //Stores dial plan info
    private String dialPlanInfo;
    //Stores conference factory URI
    private String conferenceFactoryUri;
    //Stores TBCP request Timer
    private Integer tbcpRequestTimer;
    //Stores TBCP release Timer
    private Integer tbcpReleaseTimer;
    //Stores media End Timer
    private Integer mediaEndTimer;
    //Stores media Idle Timer
    private Integer mediaIdleTimer;
    //Stores register timer
    private Integer registerTimer;
    //Stores publish POC Settings Timer
    private Integer publishPocSettingsTimer;
    //Stores publish presence timer
    private Integer publishPresenceTimer;
    //Stores invite timer
    private Integer inviteTimer;
    //Stores media port refersh timer
    private Integer mediaPortRefreshTimer;
    //Stores media Integerra brust Integererval
    private Integer mediaIntraburstInterval;
    //Stores number KA media packets
    private Integer numKaMediaPackets;
    //Stores media payload length
    private Integer mediaPayloadLength;
    //Stores location debouncing timer
    private Integer locationDebouncingTimer;
    //Stores RLS subscription timer
    private Integer rlsSubscriptionTimer;
    //Stores support pre establishment session
    private boolean supportPreEstablishmentSession;
    //Stores support simultaneous session
    private boolean supportSimultaneousSession;
    //Stores conference URI template
    private String conferenceUriTemplate;
    //Stores primary Subscription proxy URI
    private String primarySubscriptionProxyUri;
    //Stores Geo subscription proxy URI
    private String geoSubscriptionProxyUri;
    //Stores XDMS Subscription timer
    private Integer xdmsSubscriptionTimer;
    //Stores number of retries
    private Integer numOfRetries;
    //Stores number of TBCP retries
    private Integer numOfTbcpRetries;
    //Stores presence publish throttle timer
    private Integer presencePublishThrottleTimer;
    //Stores number of wakeup triggers
    private Integer numOfWakeupTriggers;
    //Stores octet size
    private Integer octetSize;
    //Stores wakeup time interval
    private Integer wakeupTimeInterval;
    //Stores instaPOC
    private boolean instaPoc;
    // Stores Last profile update time
    private long lastProfileUpdateTime;
    // Stores numOfBurstPerTrigger
    private Integer numOfBurstPerTrigger;

    //stores max talk burst duration
    private Integer maxTalkBurstDuration;
    //stores the active Feature Set
    private long activeFS1;
    //stores the supervisor-override
    private boolean supervisorOverride;
    //stores location publish interval
    private Integer locationPublishInterval;
    //stores the roaming allowed
    private boolean roamingAllowed;
    //stores session recovery timer 1
    private Integer sessionRecoveryTimer1;
    //stores session recovery timer 2
    private Integer sessionRecoveryTimer2;
    //stores session recovery timer 3
    private Integer sessionRecoveryTimer3;
    //stores session recovery timer 4
    private Integer sessionRecoveryTimer4;
    //stores session recovery timer 5
    private Integer sessionRecoveryTimer5;
    //stores session recovery timer 6
    private Integer sessionRecoveryTimer6;
    //stores ipDebouncerTimer
    private Integer ipDebouncerTimer;
    //stores tbcp-request-retry-timer
    private Integer tbcpRequestRetryTimer;
    //stores tbcp-release-retry-timer
    private Integer tbcpReleaseRetryTimer;
    //stores floor-recovery-timer
    private Integer floorRecoveryTimer;

    private Integer preCallNumKaMediaPackets;

    private Integer preCallKaPacketSize;

    private Integer kaPacketSize;

    private Integer preCallKaInterval;

    private Integer kaInterval;

    private Integer preCallKaDuration;

    private long clientFS1;

    private Integer roamingBit;

    private int clientType;

    private String swUpdateQryInterval;
    private String swUpdateInfoUrl;
    private String swUpdatePkgUrl;

    private String tuSmsAddress;
    private long tuDownTimer;
    private long tuUpTimerStartVal;
    private long tuUpTimerMaxVal;
    private long tuUpTimerRampDownPeriod;
    private long tuForceOnlineMaxWaitTimer;
    private Integer tuSmsAddressTon;

    private String sipProxyURI;
    private Integer clientConnRetryInterval;
    private Integer maxClientConnRtyAttempts;
    private Integer clientConnSecurityLevel;
    private Integer clientSipTxnTimeout;
    private Integer clientSipReferTxnTimeout;
    private Integer minTcpKaTimerOnWifi;
    private Integer wifiTcpKaTimerIncrVal;
    private Integer maxTcpKaTimerOnWifi;
    private Integer wifiSsidTimeoutMapSize;
    private Integer tcpKaTimerOnMacroCellular;
    private Integer detectWifiNatTcpTimeout;
    private Integer mediaSecureSessionRefreshIntvl;
    private Integer clientInCallSuspendTimer;

    ////version 5.x parameters
    private Integer mwpt;
    private Integer wpgt;
    private Integer srpi;
    private Integer stci;
    private String lgsrvuri;
    private Integer scgbm;
    private Integer spgag;
    private String ltekap;
    private String wfkap;
    private String umtskap;
    private Integer snv;
    private String cgpu;
    //7.7.1
    private String odlfreq;
    private String odldur;
    private Integer tfltconodl;
    private Integer tfltsnapodl;
    private Integer godlreq;
    //7.10
    private Integer mscl;
    private Integer uprio;
    private String escl;
    private Integer amrfpp;
    private String corpId;
    private String corpName;
    private Integer suirpi;
    private String ipvc;
    private String ipvprefc;
    private String ipvprefw;
    private String ipvmulti;
    private String ipvprefmulti;

    //8.0
    private String pwsuriw;
    private String gwsuriw;
    private String trice;

    //8.1
    private Integer distListRr;
    private String pWsUri;
    private String gWsUri;
    private Integer wsCaeT;

    //9
    private String negCI;
    private String clientCap;
    private String ipaATtl;
    private String ctl;
    private String ctlIntl;
    private String radScLS;
    private String radChLS;

    //version 6.0
    private Integer tpts;
    private Integer tptm;
    private String ipv;
    private Integer tcpktmcv6;
    private Integer mssrtv6;
    private Integer mprtv6;
    private String gppr;
    private String pprw;
    private String gpprw;
    private String lgsrvuriw;
    private Integer pcruv6;
    private Integer pcgt;
    private String xcaprooturiwifi;
    private Boolean asnchg;

    private String apnName ;

    //8.1.2 PV - 10, couch-sync-mobile
    private String maxTextMessageSize;
    private String maxMultimediaMsgSizeOverCellular;
    private String maxMultimediaMsgSizeOverWifi;
    private String push2MessageDeliveryReceiptEnabled;
    private String push2MessageReadReceiptEnabled;
    private String push2MessageFleetMemberGeoTag;
    private String maxUserPredefinedMessages;
    private String geoFenceDistanceMeasurementUnit;
    private String geoFencePeriodicLocationUpdateInterval;
    private String geoFencingPeriod;
    private String geoFenceDist4Client;
    private String geoFenceNotificationTh;
    private String geoFenceNotificationInt;
    private String onCallLocationUpdateEnabled;
    private String onCallLocationUpdateInterval;
    private String sgwRootUriCellular;
    private String sgwRootUriWifi;
    private String authUriCellular;
    private String authUriWifi;
    private String cbBukInfo;
    private String sgwAuthMec;
    private String sgwHbInterval;
    private String minVoiceMsgFallBackLen;
    private Integer mapProviderId;
    private String mapsUriC;
    private String mapsUriW;
    private String mapsGeoUriC;
    private String mapsGeoUriW;
    private String gApiKey;
    private String sgmLocUriCell;
    private String sgmLocUriWifi;
    private String mapStatsReportIntvl;
    private String locExpTimeIntrvl;

    //8.3 PV 11
    private Integer clientRecLen;
    private Integer pttRadioClientGrpht;
    private Integer pttradioClientNongrpht;
    private String activeGeoFencGrpSize;

    private String drxC;
    private String cqiP;
    private String cqiT;
    private Integer cqiMR;
    private Integer cqiPwT;
    private String cqiDisp;
    private Integer webDispMapStatReportIntvl;
    private Integer webDispUiStatReportIntvl;

    private String ufmi;
    private Integer idenIpteropFlag;

    //PV 13
    private Integer mabg;
    private Integer mlabg;
    private Integer mlabu;
    private Integer ntgch;
    private Integer nchzn;
    private String rpn;
    private String rpe;

    private String abdgUriC;
    private String abdgUriW;
    private Integer intCorpId;
    private String kuidPrefix;
    private Integer reaInd;
    private Integer etgsMode;
    private Integer msmcnt;
    private Integer msmlen;
    private Integer msmdst;

    //pv14
    private Integer mdyss;
    private Integer mddss;

    private String activeFS2;
    private String clientFS2;

    //pv16
    private String esriMapsClientId;
    private String esriMapsSecretKey;
    private String esriMapsCellularUri;
    private String esriMapsWifiUri;
    private String esriMapsGeoCellularUri;
    private String esriMapsGeoWifiUri;
    private Integer osmfml;
    private Integer vmflrIdleTime;
    private Integer vmflrHldTime;
    private Integer ispdse;
    private Integer pdsmcl;
    private Integer pdsmps;
    private String fdsUriC;
    private String fdsUriW;
    private Integer msdss;
    private Integer mfls;
    private Integer mafls;
    private Integer dflttl;
    private Integer mflttl;
    private Integer mmsgttl;
    private Integer osmlei;
    private Integer isOptInNeeded;
    //PV 17
    private Integer acrtepc;
    private Integer acrtepg;
    private Integer acrteag;

    //PV 19
    private Integer cskUploadInterval;
    private Integer gmkoli;

    //PV 20
    private Integer usrkeymatolp;
    private Integer cskskew;
    private Integer pckskew;
    private Integer gmkskew;
    private Integer strencrypt;
    private String mdsiUri;
    private String gmsUri;
    private String msgstoreuric;
    private String msgstoreuriw;
    private Integer msimulsdstxns;
    private Integer msimulfdtxns;
    private Integer msrchentries;

    //PV 21
    private Integer recordingStatus;

    //PV23
    private Integer maxVideoSessions;

    //PV24
    private Integer mMemUsrRegrp;
    private Integer mUsrRegrps;
    private Integer mBtfPerOwner;
    private String gmsServId;
    private String btfDuration;
    private String btfToneList;
    private String btfTonePauseIntervals;
    private Integer mGrpRegrps;

    //PV25
    private Integer altitudeFlag;
    private Integer verticalAccuracyFlag;
    private Integer mediaMissingTimer;;
    //PV29+ (MBMS)
    private Integer multicastKaInterval;
    // PTT Template
    private KnPttSettingsDocType pttSettingsDoc;
    private Integer kpiRepAudInterval;
    private Integer kpiRepUpRand;
    private Integer kpiRepMcs;
    public Integer getMaxVideoSessions() {
        return maxVideoSessions;
    }

    public void setMaxVideoSessions(Integer maxVideoSessions) {
        this.maxVideoSessions = maxVideoSessions;
    }

    public Integer getmMemUsrRegrp() {
        return mMemUsrRegrp;
    }

    public void setmMemUsrRegrp(Integer mMemUsrRegrp) {
        this.mMemUsrRegrp = mMemUsrRegrp;
    }

    public Integer getmUsrRegrps() {
        return mUsrRegrps;
    }

    public void setmUsrRegrps(Integer mUsrRegrps) {
        this.mUsrRegrps = mUsrRegrps;
    }

    public Integer getmBtfPerOwner() {
        return mBtfPerOwner;
    }

    public void setmBtfPerOwner(Integer mBtfPerOwner) {
        this.mBtfPerOwner = mBtfPerOwner;
    }

    public String getGmsServId() {
        return gmsServId;
    }

    public void setGmsServId(String gmsServId) {
        this.gmsServId = gmsServId;
    }

    public String getBtfDuration() {
        return btfDuration;
    }

    public void setBtfDuration(String btfDuration) {
        this.btfDuration = btfDuration;
    }

    public String getBtfToneList() {
        return btfToneList;
    }

    public void setBtfToneList(String btfToneList) {
        this.btfToneList = btfToneList;
    }

    public String getBtfTonePauseIntervals() {
        return btfTonePauseIntervals;
    }

    public void setBtfTonePauseIntervals(String btfTonePauseIntervals) {
        this.btfTonePauseIntervals = btfTonePauseIntervals;
    }

    public Integer getmGrpRegrps() {
        return mGrpRegrps;
    }

    public void setmGrpRegrps(Integer mGrpRegrps) {
        this.mGrpRegrps = mGrpRegrps;
    }

    public Integer getRecordingStatus() { return recordingStatus; }

    public void setRecordingStatus(Integer recordingStatus) { this.recordingStatus = recordingStatus; }

    public String getMsgstoreuric() {
        return msgstoreuric;
    }

    public void setMsgstoreuric(String msgstoreuric) {
        this.msgstoreuric = msgstoreuric;
    }

    public String getMsgstoreuriw() {
        return msgstoreuriw;
    }

    public void setMsgstoreuriw(String msgstoreuriw) {
        this.msgstoreuriw = msgstoreuriw;
    }

    public Integer getMsimulsdstxns() {
        return msimulsdstxns;
    }

    public void setMsimulsdstxns(Integer msimulsdstxns) {
        this.msimulsdstxns = msimulsdstxns;
    }

    public Integer getMsimulfdtxns() {
        return msimulfdtxns;
    }

    public void setMsimulfdtxns(Integer msimulfdtxns) {
        this.msimulfdtxns = msimulfdtxns;
    }

    public Integer getMsrchentries() {
        return msrchentries;
    }

    public void setMsrchentries(Integer msrchentries) {
        this.msrchentries = msrchentries;
    }

    public String getMdsiUri() {
        return mdsiUri;
    }

    public void setMdsiUri(String mdsiUri) {
        this.mdsiUri = mdsiUri;
    }

    public String getGmsUri() {
        return gmsUri;
    }

    public void setGmsUri(String gmsUri) {
        this.gmsUri = gmsUri;
    }

    public Integer getUsrkeymatolp() {
        return usrkeymatolp;
    }

    public void setUsrkeymatolp(Integer usrkeymatolp) {
        this.usrkeymatolp = usrkeymatolp;
    }

    public Integer getCskskew() {
        return cskskew;
    }

    public void setCskskew(Integer cskskew) {
        this.cskskew = cskskew;
    }

    public Integer getPckskew() {
        return pckskew;
    }

    public void setPckskew(Integer pckskew) {
        this.pckskew = pckskew;
    }

    public Integer getGmkskew() {
        return gmkskew;
    }

    public void setGmkskew(Integer gmkskew) {
        this.gmkskew = gmkskew;
    }

    public Integer getStrencrypt() {
        return strencrypt;
    }

    public void setStrencrypt(Integer strencrypt) {
        this.strencrypt = strencrypt;
    }

    public Integer getGmkoli() { return gmkoli; }

    public void setGmkoli(Integer gmkoli) { this.gmkoli = gmkoli; }

    public Integer getCskUploadInterval() {
        return cskUploadInterval;
    }

    public void setCskUploadInterval(Integer cskUploadInterval) {
        this.cskUploadInterval = cskUploadInterval;
    }

    public Integer getIsOptInNeeded() {
        return isOptInNeeded;
    }

    public void setIsOptInNeeded(Integer isOptInNeeded) {
        this.isOptInNeeded = isOptInNeeded;
    }

    public Integer getOsmlei() {
        return osmlei;
    }

    public void setOsmlei(Integer osmlei) {
        this.osmlei = osmlei;
    }

    public String getFdsUriC() {
        return fdsUriC;
    }

    public void setFdsUriC(String fdsUriC) {
        this.fdsUriC = fdsUriC;
    }

    public String getFdsUriW() {
        return fdsUriW;
    }

    public void setFdsUriW(String fdsUriW) {
        this.fdsUriW = fdsUriW;
    }

    public Integer getMsdss() {
        return msdss;
    }

    public void setMsdss(Integer msdss) {
        this.msdss = msdss;
    }

    public Integer getMfls() {
        return mfls;
    }

    public void setMfls(Integer mfls) {
        this.mfls = mfls;
    }

    public Integer getMafls() {
        return mafls;
    }

    public void setMafls(Integer mafls) {
        this.mafls = mafls;
    }

    public Integer getDflttl() {
        return dflttl;
    }

    public void setDflttl(Integer dflttl) {
        this.dflttl = dflttl;
    }

    public Integer getMflttl() {
        return mflttl;
    }

    public void setMflttl(Integer mflttl) {
        this.mflttl = mflttl;
    }

    public Integer getMmsgttl() {
        return mmsgttl;
    }

    public void setMmsgttl(Integer mmsgttl) {
        this.mmsgttl = mmsgttl;
    }

    public Integer getIspdse() {
        return ispdse;
    }

    public void setIspdse(Integer ispdse) {
        this.ispdse = ispdse;
    }

    public Integer getPdsmcl() {
        return pdsmcl;
    }

    public void setPdsmcl(Integer pdsmcl) {
        this.pdsmcl = pdsmcl;
    }

    public Integer getPdsmps() {
        return pdsmps;
    }

    public void setPdsmps(Integer pdsmps) {
        this.pdsmps = pdsmps;
    }

    public Integer getOsmfml() {
        return osmfml;
    }

    public void setOsmfml(Integer osmfml) {
        this.osmfml = osmfml;
    }

    public Integer getVmflrIdleTime() {
        return vmflrIdleTime;
    }

    public void setVmflrIdleTime(Integer vmflrIdleTime) {
        this.vmflrIdleTime = vmflrIdleTime;
    }

    public Integer getVmflrHldTime() {
        return vmflrHldTime;
    }

    public void setVmflrHldTime(Integer vmflrHldTime) {
        this.vmflrHldTime = vmflrHldTime;
    }

    public String getEsriMapsClientId() {
        return esriMapsClientId;
    }

    public void setEsriMapsClientId(String esriMapsClientId) {
        this.esriMapsClientId = esriMapsClientId;
    }

    public String getEsriMapsSecretKey() {
        return esriMapsSecretKey;
    }

    public void setEsriMapsSecretKey(String esriMapsSecretKey) {
        this.esriMapsSecretKey = esriMapsSecretKey;
    }

    public String getEsriMapsCellularUri() {
        return esriMapsCellularUri;
    }

    public void setEsriMapsCellularUri(String esriMapsCellularUri) {
        this.esriMapsCellularUri = esriMapsCellularUri;
    }

    public String getEsriMapsWifiUri() {
        return esriMapsWifiUri;
    }

    public void setEsriMapsWifiUri(String esriMapsWifiUri) {
        this.esriMapsWifiUri = esriMapsWifiUri;
    }

    public String getEsriMapsGeoCellularUri() {
        return esriMapsGeoCellularUri;
    }

    public void setEsriMapsGeoCellularUri(String esriMapsGeoCellularUri) {
        this.esriMapsGeoCellularUri = esriMapsGeoCellularUri;
    }

    public String getEsriMapsGeoWifiUri() {
        return esriMapsGeoWifiUri;
    }

    public void setEsriMapsGeoWifiUri(String esriMapsGeoWifiUri) {
        this.esriMapsGeoWifiUri = esriMapsGeoWifiUri;
    }

    public Integer getMdyss() { return mdyss; }

    public void setMdyss(Integer mdyss) { this.mdyss = mdyss; }

    public Integer getMddss() { return mddss; }

    public void setMddss(Integer mddss) { this.mddss = mddss; }

    public Integer getMsmcnt() {
        return msmcnt;
    }

    public void setMsmcnt(Integer msmcnt) {
        this.msmcnt = msmcnt;
    }

    public Integer getMsmlen() {
        return msmlen;
    }

    public void setMsmlen(Integer msmlen) {
        this.msmlen = msmlen;
    }

    public Integer getMsmdst() {
        return msmdst;
    }

    public void setMsmdst(Integer msmdst) {
        this.msmdst = msmdst;
    }

    public Integer getReaInd() {
        return reaInd;
    }

    public void setReaInd(Integer reaInd) {
        this.reaInd = reaInd;
    }

    public Integer getEtgsMode() {
        return etgsMode;
    }

    public void setEtgsMode(Integer etgsMode) {
        this.etgsMode = etgsMode;
    }


    public String getAbdgUriC() {
        return abdgUriC;
    }

    public void setAbdgUriC(String abdgUriC) {
        this.abdgUriC = abdgUriC;
    }

    public String getAbdgUriW() {
        return abdgUriW;
    }

    public void setAbdgUriW(String abdgUriW) {
        this.abdgUriW = abdgUriW;
    }

    public Integer getIntCorpId() {
        return intCorpId;
    }

    public void setIntCorpId(Integer intCorpId) {
        this.intCorpId = intCorpId;
    }

    public String getKuidPrefix() {
        return kuidPrefix;
    }

    public void setKuidPrefix(String kuidPrefix) {
        this.kuidPrefix = kuidPrefix;
    }

    public String getRpn() {
        return rpn;
    }

    public void setRpn(String rpn) {
        this.rpn = rpn;
    }

    public String getRpe() {
        return rpe;
    }

    public void setRpe(String rpe) {
        this.rpe = rpe;
    }

    public Integer getNtgch() {
        return ntgch;
    }

    public void setNtgch(Integer ntgch) {
        this.ntgch = ntgch;
    }

    public Integer getNchzn() {
        return nchzn;
    }

    public void setNchzn(Integer nchzn) {
        this.nchzn = nchzn;
    }


    public Integer getMabg() {
        return mabg;
    }
    public void setMabg(Integer mabg) {
        this.mabg = mabg;
    }

    public Integer getMlabg() {
        return mlabg;
    }
    public void setMlabg(Integer mlabg) {
        this.mlabg = mlabg;
    }

    public Integer getMlabu() {
        return mlabu;
    }
    public void setMlabu(Integer mlabu) {
        this.mlabu = mlabu;
    }

    public String getUfmi() {
        return ufmi;
    }

    public void setUfmi(String ufmi) {
        this.ufmi = ufmi;
    }

    public Integer getIdenIpteropFlag() {
        return idenIpteropFlag;
    }

    public void setIdenIpteropFlag(Integer idenIpteropFlag) {
        this.idenIpteropFlag = idenIpteropFlag;
    }

    public String getDrxC() {
        return drxC;
    }

    public void setDrxC(String drxC) {
        this.drxC = drxC;
    }

    public String getCqiP() {
        return cqiP;
    }

    public void setCqiP(String cqiP) {
        this.cqiP = cqiP;
    }

    public String getCqiT() {
        return cqiT;
    }

    public void setCqiT(String cqiT) {
        this.cqiT = cqiT;
    }

    public Integer getCqiMR() {
        return cqiMR;
    }

    public void setCqiMR(Integer cqiMR) {
        this.cqiMR = cqiMR;
    }

    public Integer getCqiPwT() {
        return cqiPwT;
    }

    public void setCqiPwT(Integer cqiPwT) {
        this.cqiPwT = cqiPwT;
    }

    public String getCqiDisp() {
        return cqiDisp;
    }

    public void setCqiDisp(String cqiDisp) {
        this.cqiDisp = cqiDisp;
    }

    public String getActiveGeoFencGrpSize() {
        return activeGeoFencGrpSize;
    }

    public void setActiveGeoFencGrpSize(String activeGeoFencGrpSize) {
        this.activeGeoFencGrpSize = activeGeoFencGrpSize;
    }

    /**
     * getter method for the public subscription type
     *
     * @return int
     */
    public int getPubSubscriptionType() {
        return pubSubscriptionType;
    }

    /**
     * setter method for the Public Subscription Types
     *
     * @param pubSubscriptionType int
     */
    public void setPubSubscriptionType(int pubSubscriptionType) {
        this.pubSubscriptionType = pubSubscriptionType;
    }

    /**
     * getter method for the corporation subscription type
     *
     * @return String
     */
    public int getCorpSubscriptionType() {
        return corpSubscriptionType;
    }

    public void setCorpSubscriptionType(int corpSubscriptionType) {
        this.corpSubscriptionType = corpSubscriptionType;
    }

    //stores the Object Id

    private String objectId;

    /**
     * getter method for the Primary Session URI
     *
     * @return String
     */
    public String getPrimarySessionRoute() {
        return primarySessionRoute;
    }

    /**
     * setter method for the Primary Session URI
     *
     * @param primarySessionRoute String
     */
    public void setPrimarySessionRoute(String primarySessionRoute) {
        if (primarySessionRoute != null) {
            primarySessionRoute = primarySessionRoute.trim();
            if (primarySessionRoute.equals("")) {
                primarySessionRoute = null;
            }
        }
        this.primarySessionRoute = primarySessionRoute;
    }

    /**
     * getter method for the Geo Session URI
     *
     * @return String
     */
    public String getGeoSessionRoute() {
        return geoSessionRoute;
    }

    /**
     * setter method for the Geo Session URI
     *
     * @param geoSessionRoute String
     */
    public void setGeoSessionRoute(String geoSessionRoute) {
        if (geoSessionRoute != null) {
            geoSessionRoute = geoSessionRoute.trim();
            if (geoSessionRoute.equals("")) {
                geoSessionRoute = null;
            }
        }
        this.geoSessionRoute = geoSessionRoute;
    }

    /**
     * getter method for the Primary POC setting URI
     * *
     *
     * @return String
     */
    public String getPrimaryPocSettingRoute() {
        return primaryPocSettingRoute;
    }

    /**
     * setter method for the Primary POC setting URI
     * *
     *
     * @param primaryPocSettingRoute String
     */
    public void setPrimaryPocSettingRoute(String primaryPocSettingRoute) {
        if (primaryPocSettingRoute != null) {
            primaryPocSettingRoute = primaryPocSettingRoute.trim();
            if (primaryPocSettingRoute.equals("")) {
                primaryPocSettingRoute = null;
            }
        }
        this.primaryPocSettingRoute = primaryPocSettingRoute;
    }

    /**
     * getter method for the Geo POC Setting URI
     *
     * @return String
     */
    public String getGeoPocSettingRoute() {
        return geoPocSettingRoute;
    }

    /**
     * setter method for the Geo POC Setting URI
     *
     * @param geoPocSettingRoute String
     */

    public void setGeoPocSettingRoute(String geoPocSettingRoute) {
        if (geoPocSettingRoute != null) {
            geoPocSettingRoute = geoPocSettingRoute.trim();
            if (geoPocSettingRoute.equals("")) {
                geoPocSettingRoute = null;
            }
        }
        this.geoPocSettingRoute = geoPocSettingRoute;
    }

    /**
     * getter method for the Primary IPA URI
     *
     * @return String
     */
    public String getPrimaryIpaRoute() {
        return primaryIpaRoute;
    }

    /**
     * setter method for the Primary IPA URI
     *
     * @param primaryIpaRoute String
     */
    public void setPrimaryIpaRoute(String primaryIpaRoute) {
        if (primaryIpaRoute != null) {
            primaryIpaRoute = primaryIpaRoute.trim();
            if (primaryIpaRoute.equals("")) {
                primaryIpaRoute = null;
            }
        }
        this.primaryIpaRoute = primaryIpaRoute;
    }

    /**
     * getter method for the Geo IPA URI
     *
     * @return String
     */
    public String getGeoIpaRoute() {
        return geoIpaRoute;
    }

    /**
     * setter method for the Geo IPA URI
     *
     * @param geoIpaRoute String
     */
    public void setGeoIpaRoute(String geoIpaRoute) {
        if (geoIpaRoute != null) {
            geoIpaRoute = geoIpaRoute.trim();
            if (geoIpaRoute.equals("")) {
                geoIpaRoute = null;
            }
        }
        this.geoIpaRoute = geoIpaRoute;
    }

    /**
     * getter method for the Primary Presence URI
     *
     * @return String
     */
    public String getPrimaryPresenceRoute() {
        return primaryPresenceRoute;
    }

    /**
     * setter method for the Primary Presence URI
     *
     * @param primaryPresenceRoute String
     */
    public void setPrimaryPresenceRoute(String primaryPresenceRoute) {
        if (primaryPresenceRoute != null) {
            primaryPresenceRoute = primaryPresenceRoute.trim();
            if (primaryPresenceRoute.equals("")) {
                primaryPresenceRoute = null;
            }
        }
        this.primaryPresenceRoute = primaryPresenceRoute;
    }

    /**
     * getter method for the Geo presence URI
     *
     * @return String
     */
    public String getGeoPresenceRoute() {
        return geoPresenceRoute;
    }

    /**
     * setter method for the Geo presence URI
     *
     * @param geoPresenceRoute String
     */
    public void setGeoPresenceRoute(String geoPresenceRoute) {
        if (geoPresenceRoute != null) {
            geoPresenceRoute = geoPresenceRoute.trim();
            if (geoPresenceRoute.equals("")) {
                geoPresenceRoute = null;
            }
        }
        this.geoPresenceRoute = geoPresenceRoute;
    }

    /**
     * getter method for the Primary RLS URI
     *
     * @return String
     */
    public String getPrimaryRlsRoute() {
        return primaryRlsRoute;
    }

    /**
     * setter method for the Primary RLS URI
     *
     * @param primaryRlsRoute String
     */
    public void setPrimaryRlsRoute(String primaryRlsRoute) {
        if (primaryRlsRoute != null) {
            primaryRlsRoute = primaryRlsRoute.trim();
            if (primaryRlsRoute.equals("")) {
                primaryRlsRoute = null;
            }
        }
        this.primaryRlsRoute = primaryRlsRoute;
    }

    /**
     * getter method for the Geo RLS URI
     *
     * @return String
     */
    public String getGeoRlsRoute() {
        return geoRlsRoute;
    }

    /**
     * setter method for the Geo RLS URI
     *
     * @param geoRlsRoute String
     */
    public void setGeoRlsRoute(String geoRlsRoute) {
        if (geoRlsRoute != null) {
            geoRlsRoute = geoRlsRoute.trim();
            if (geoRlsRoute.equals("")) {
                geoRlsRoute = null;
            }
        }
        this.geoRlsRoute = geoRlsRoute;
    }


    /**
     * getter method for the XCAP Root URI
     *
     * @return String
     */
    public String getXcapRootUri() {
        return xcapRootUri;
    }

    /**
     * setter method for the XCAP Root URI
     *
     * @param xcapRootUri String
     */
    public void setXcapRootUri(String xcapRootUri) {
        if (xcapRootUri != null) {
            xcapRootUri = xcapRootUri.trim();
            if (xcapRootUri.equals("")) {
                xcapRootUri = null;
            }
        }
        this.xcapRootUri = xcapRootUri;
    }

    /**
     * getter method for the Primary XDMS URI
     *
     * @return String
     */
    public String getPrimaryXdmsRoute() {
        return primaryXdmsRoute;
    }

    /**
     * setter method for the Primary XDMS URI
     *
     * @param primaryXdmsRoute String
     */
    public void setPrimaryXdmsRoute(String primaryXdmsRoute) {
        if (primaryXdmsRoute != null) {
            primaryXdmsRoute = primaryXdmsRoute.trim();
            if (primaryXdmsRoute.equals("")) {
                primaryXdmsRoute = null;
            }
        }
        this.primaryXdmsRoute = primaryXdmsRoute;
    }

    /**
     * getter method for the GEO XDMS URI
     *
     * @return String
     */
    public String getGeoXdmsRoute() {
        return geoXdmsRoute;
    }

    /**
     * setter method for the GEO XDMS URI
     *
     * @param geoXdmsRoute String
     */
    public void setGeoXdmsRoute(String geoXdmsRoute) {
        if (geoXdmsRoute != null) {
            geoXdmsRoute = geoXdmsRoute.trim();
            if (geoXdmsRoute.equals("")) {
                geoXdmsRoute = null;
            }
        }
        this.geoXdmsRoute = geoXdmsRoute;
    }

    /**
     * getter method for the Max Public Groups
     *
     * @return int
     */
    public Integer getMaxPublicGroups() {
        return maxPublicGroups;
    }

    /**
     * getter method for the Max Public Groups
     *
     * @param maxPublicGroups int
     */
    public void setMaxPublicGroups(Integer maxPublicGroups) {
        this.maxPublicGroups = maxPublicGroups;
    }

    /**
     * getter method for the Max corporate Groups
     *
     * @return int
     */
    public Integer getMaxCorporateGroups() {
        return maxCorporateGroups;
    }

    /**
     * setter method for the Max corporate Groups
     *
     * @param maxCorporateGroups int
     */
    public void setMaxCorporateGroups(Integer maxCorporateGroups) {
        this.maxCorporateGroups = maxCorporateGroups;
    }

    /**
     * getter method for the max members per corp group
     *
     * @return int
     */
    public Integer getMaxMembersPerCorpGroup() {
        return maxMembersPerCorpGroup;
    }

    /**
     * setter method for the max members per corp group
     *
     * @param maxMembersPerCorpGroup int
     */
    public void setMaxMembersPerCorpGroup(Integer maxMembersPerCorpGroup) {
        this.maxMembersPerCorpGroup = maxMembersPerCorpGroup;
    }


    /**
     * getter method for the publish POC settings timer
     *
     * @return int
     */
    public Integer getPublishPocSettingsTimer() {
        return publishPocSettingsTimer;
    }

    /**
     * setter method for the publish POC settings timer
     *
     * @param publishPocSettingsTimer int
     */
    public void setPublishPocSettingsTimer(Integer publishPocSettingsTimer) {
        this.publishPocSettingsTimer = publishPocSettingsTimer;
    }

    /**
     * getter method for the publish presence timer
     *
     * @return int
     */
    public Integer getPublishPresenceTimer() {
        return publishPresenceTimer;
    }

    /**
     * setter method for the publish presence timer
     *
     * @param publishPresenceTimer int
     */
    public void setPublishPresenceTimer(Integer publishPresenceTimer) {
        this.publishPresenceTimer = publishPresenceTimer;
    }

    /**
     * getter method for the Media Intraburst interval
     *
     * @return int
     */
    public Integer getMediaIntraburstInterval() {
        return mediaIntraburstInterval;
    }

    /**
     * setter method for the Media Intraburst interval
     *
     * @param mediaIntraburstInterval int
     */
    public void setMediaIntraburstInterval(Integer mediaIntraburstInterval) {
        this.mediaIntraburstInterval = mediaIntraburstInterval;
    }

    /**
     * getter method for the Number KA media packets
     *
     * @return int
     */
    public Integer getNumKaMediaPackets() {
        return numKaMediaPackets;
    }

    /**
     * setter method for the Number KA media packets
     *
     * @param numKaMediaPackets int
     */
    public void setNumKaMediaPackets(Integer numKaMediaPackets) {
        this.numKaMediaPackets = numKaMediaPackets;
    }

    /**
     * getter method for the media Payload length
     *
     * @return int
     */
    public Integer getMediaPayloadLength() {
        return mediaPayloadLength;
    }

    /**
     * setter method for the media Payload length
     *
     * @param mediaPayloadLength int
     */
    public void setMediaPayloadLength(Integer mediaPayloadLength) {
        this.mediaPayloadLength = mediaPayloadLength;
    }

    /**
     * getter method for the RLS subscription Timer
     *
     * @return int
     */
    public Integer getRlsSubscriptionTimer() {
        return rlsSubscriptionTimer;
    }

    /**
     * setter method for the RLS subscription Timer
     *
     * @param rlsSubscriptionTimer int
     */
    public void setRlsSubscriptionTimer(Integer rlsSubscriptionTimer) {
        this.rlsSubscriptionTimer = rlsSubscriptionTimer;
    }


    public boolean isSupportPreEstablishmentSession() {
        return supportPreEstablishmentSession;
    }

    public void setSupportPreEstablishmentSession(boolean supportPreEstablishmentSession) {
        this.supportPreEstablishmentSession = supportPreEstablishmentSession;
    }

    /**
     * getter method for the Primary subscription Proxy URI
     *
     * @return String
     */
    public String getPrimarySubscriptionProxyUri() {
        return primarySubscriptionProxyUri;
    }

    /**
     * setter method for the Primary subscription Proxy URI
     *
     * @param primarySubscriptionProxyUri String
     */
    public void setPrimarySubscriptionProxyUri(String primarySubscriptionProxyUri) {
        if (primarySubscriptionProxyUri != null) {
            primarySubscriptionProxyUri = primarySubscriptionProxyUri.trim();
            if (primarySubscriptionProxyUri.equals("")) {
                primarySubscriptionProxyUri = null;
            }
        }
        this.primarySubscriptionProxyUri = primarySubscriptionProxyUri;
    }

    /**
     * getter method for the geo subscription proxy URI
     *
     * @return String
     */
    public String getGeoSubscriptionProxyUri() {
        return geoSubscriptionProxyUri;
    }

    /**
     * setter method for geo Subscription Proxy Uri
     *
     * @param geoSubscriptionProxyUri String
     */
    public void setGeoSubscriptionProxyUri(String geoSubscriptionProxyUri) {
        if (geoSubscriptionProxyUri != null) {
            geoSubscriptionProxyUri = geoSubscriptionProxyUri.trim();
            if (geoSubscriptionProxyUri.equals("")) {
                geoSubscriptionProxyUri = null;
            }
        }
        this.geoSubscriptionProxyUri = geoSubscriptionProxyUri;
    }

    /**
     * getter method for the XDMS subs timer
     *
     * @return int
     */
    public Integer getXdmsSubscriptionTimer() {
        return xdmsSubscriptionTimer;
    }

    /**
     * setter method for xdms subscription Timer
     *
     * @param xdmsSubscriptionTimer int
     */
    public void setXdmsSubscriptionTimer(Integer xdmsSubscriptionTimer) {
        this.xdmsSubscriptionTimer = xdmsSubscriptionTimer;
    }

    /**
     * getter method for the number of TBCP retries
     *
     * @return int
     */
    public Integer getNumOfTbcpRetries() {
        return numOfTbcpRetries;
    }

    /**
     * setter method for number of TBCP retries
     *
     * @param numOfTbcpRetries int
     */
    public void setNumOfTbcpRetries(Integer numOfTbcpRetries) {
        this.numOfTbcpRetries = numOfTbcpRetries;
    }

    /**
     * getter method for the number of wakeup triggers
     *
     * @return int
     */
    public Integer getNumOfWakeupTriggers() {
        return numOfWakeupTriggers;
    }

    /**
     * setter method for number of wakeup triggers
     *
     * @param numOfWakeupTriggers int
     */
    public void setNumOfWakeupTriggers(Integer numOfWakeupTriggers) {
        this.numOfWakeupTriggers = numOfWakeupTriggers;
    }

    /**
     * getter method for the octet size
     *
     * @return int
     */
    public Integer getOctetSize() {
        return octetSize;
    }

    /**
     * setter method for octet size
     *
     * @param octetSize int
     */
    public void setOctetSize(Integer octetSize) {
        this.octetSize = octetSize;
    }

    /**
     * getter method Wakeup time interval
     *
     * @return int
     */
    public Integer getWakeupTimeInterval() {
        return wakeupTimeInterval;
    }

    /**
     * setter method for Wakeup Time interval
     *
     * @param wakeupTimeInterval int
     */
    public void setWakeupTimeInterval(Integer wakeupTimeInterval) {
        this.wakeupTimeInterval = wakeupTimeInterval;
    }

    /**
     * getter method for the Insta POC
     *
     * @return int
     */
    public boolean getInstaPoc() {
        return instaPoc;
    }

    /**
     * setter method for insta POC
     *
     * @param instaPoc boolean
     */
    public void setInstaPoc(boolean instaPoc) {
        this.instaPoc = instaPoc;
    }

    /**
     * getter method for Subscription State
     *
     * @return int
     */
    public int getSubscriptionState() {
        return subscriptionState;
    }

    /**
     * setter method for Subscription State
     *
     * @param subscriptionState int
     */
    public void setSubscriptionState(int subscriptionState) {
        this.subscriptionState = subscriptionState;
    }

    /**
     * getter method for Subscriber Name
     *
     * @return String
     */
    public String getNetworkName() {
        return networkName;
    }

    /**
     * setter method for Subscriber Name
     *
     * @param networkName String
     */
    public void setNetworkName(String networkName) {
        if (networkName != null) {
            networkName = networkName.trim();
            if (networkName.equals("")) {
                networkName = null;
            }
        }
        this.networkName = networkName;
    }

    /**
     * getter method for Max Contacts
     *
     * @return int
     */
    public Integer getMaxPublicContacts() {
        return maxPublicContacts;
    }

    /**
     * setter method for Max Contacts
     *
     * @param maxPublicContacts int
     */
    public void setMaxPublicContacts(Integer maxPublicContacts) {

        this.maxPublicContacts = maxPublicContacts;
    }

    /**
     * getter method for Max Members
     *
     * @return String
     */
    public Integer getMaxMembersPerPublicGroup() {
        return maxMembersPerPublicGroup;
    }

    /**
     * setter method for Max Members
     *
     * @param maxMembersPerPublicGroup int
     */
    public void setMaxMembersPerPublicGroup(Integer maxMembersPerPublicGroup) {

        this.maxMembersPerPublicGroup = maxMembersPerPublicGroup;
    }

    /**
     * getter method for max members per Adhoc Group
     *
     * @return String
     */
    public Integer getMaxAdhocGroupSize() {
        return maxAdhocGroupSize;
    }

    /**
     * setter method for max Members per Adhoc Group
     *
     * @param maxAdhocGroupSize int
     */
    public void setMaxAdhocGroupSize(Integer maxAdhocGroupSize) {

        this.maxAdhocGroupSize = maxAdhocGroupSize;
    }

    /**
     * getter method for Dial Plan Info
     *
     * @return String
     */
    public String getDialPlanInfo() {
        return dialPlanInfo;
    }

    /**
     * setter method for Dial Plan Info
     *
     * @param dialPlanInfo String
     */
    public void setDialPlanInfo(String dialPlanInfo) {
        if (dialPlanInfo != null) {
            dialPlanInfo = dialPlanInfo.trim();
            if (dialPlanInfo.equals("")) {
                dialPlanInfo = null;
            }
        }
        this.dialPlanInfo = dialPlanInfo;
    }

    /**
     * getter method for Conference Factory URI
     *
     * @return String
     */
    public String getConferenceFactoryUri() {
        return conferenceFactoryUri;
    }

    /**
     * setter method for Conference Factory URI
     *
     * @param conferenceFactoryUri String
     */
    public void setConferenceFactoryUri(String conferenceFactoryUri) {
        if (conferenceFactoryUri != null) {
            conferenceFactoryUri = conferenceFactoryUri.trim();
            if (conferenceFactoryUri.equals("")) {
                conferenceFactoryUri = null;
            }
        }
        this.conferenceFactoryUri = conferenceFactoryUri;
    }

    /**
     * getter method for TBCP request Timer
     *
     * @return int
     */
    public Integer getTbcpRequestTimer() {
        return tbcpRequestTimer;
    }

    /**
     * setter method for TBCP Release Timer
     *
     * @param tbcpRequestTimer String
     */
    public void setTbcpRequestTimer(Integer tbcpRequestTimer) {

        this.tbcpRequestTimer = tbcpRequestTimer;
    }

    /**
     * getter method for TBCP Release Timer
     *
     * @return String
     */
    public Integer getTbcpReleaseTimer() {
        return tbcpReleaseTimer;
    }

    /**
     * setter method for TBCP Release Timer
     *
     * @param tbcpReleaseTimer int
     */
    public void setTbcpReleaseTimer(Integer tbcpReleaseTimer) {

        this.tbcpReleaseTimer = tbcpReleaseTimer;
    }

    /**
     * getter method for Media End Timer
     *
     * @return int
     */
    public Integer getMediaEndTimer() {
        return mediaEndTimer;
    }

    /**
     * setter method for Media Ent Timer
     *
     * @param mediaEndTimer int
     */
    public void setMediaEndTimer(Integer mediaEndTimer) {

        this.mediaEndTimer = mediaEndTimer;
    }

    /**
     * getter method for Media Idle Timer
     *
     * @return int
     */
    public Integer getMediaIdleTimer() {
        return mediaIdleTimer;
    }

    /**
     * setter method for Media Idle Timer
     *
     * @param mediaIdleTimer int
     */
    public void setMediaIdleTimer(Integer mediaIdleTimer) {

        this.mediaIdleTimer = mediaIdleTimer;
    }

    /**
     * getter method for Registration Timer
     *
     * @return int
     */
    public Integer getRegisterTimer() {
        return registerTimer;
    }

    /**
     * setter method for Registration Timer
     *
     * @param registerTimer int
     */
    public void setRegisterTimer(Integer registerTimer) {

        this.registerTimer = registerTimer;
    }


    /**
     * getter method for PoC Setting Publish Timer
     *
     * @return int
     */
    public Integer getInviteTimer() {
        return inviteTimer;
    }

    /**
     * setter method for PoC Setting Publish Timer
     *
     * @param inviteTimer int
     */
    public void setInviteTimer(Integer inviteTimer) {
        this.inviteTimer = inviteTimer;
    }

    /**
     * getter method for NAT Timer
     *
     * @return int
     */
    public Integer getMediaPortRefreshTimer() {
        return mediaPortRefreshTimer;
    }

    /**
     * setter method for NAT Timer
     *
     * @param mediaPortRefreshTimer int
     */
    public void setMediaPortRefreshTimer(Integer mediaPortRefreshTimer) {
        this.mediaPortRefreshTimer = mediaPortRefreshTimer;
    }

    /**
     * getter method for Location Debouncing Timer
     *
     * @return int
     */
    public Integer getLocationDebouncingTimer() {
        return locationDebouncingTimer;
    }

    /**
     * setter method for Locaton Debouncing Timer
     *
     * @param locationDebouncingTimer int
     */
    public void setLocationDebouncingTimer(Integer locationDebouncingTimer) {
        this.locationDebouncingTimer = locationDebouncingTimer;
    }


    /**
     * check if Support Simultaneous Session is Enabled
     *
     * @return boolean
     */
    public boolean isSupportSimultaneousSession() {
        return supportSimultaneousSession;
    }

    /**
     * setter method for Support Simulataneous Session
     *
     * @param supportSimultaneousSession boolean
     */
    public void setSupportSimultaneousSession(boolean supportSimultaneousSession) {
        this.supportSimultaneousSession = supportSimultaneousSession;
    }

    /**
     * getter method for Conference URI Template
     *
     * @return String
     */
    public String getConferenceUriTemplate() {
        return conferenceUriTemplate;
    }

    /**
     * setter method for Conference URI Template
     *
     * @param conferenceUriTemplate String
     */
    public void setConferenceUriTemplate(String conferenceUriTemplate) {
        if (conferenceUriTemplate != null) {
            conferenceUriTemplate = conferenceUriTemplate.trim();
            if (conferenceUriTemplate.equals("")) {
                conferenceUriTemplate = null;
            }
        }
        this.conferenceUriTemplate = conferenceUriTemplate;
    }


    /**
     * getter method for Number of Retries
     *
     * @return int
     */
    public Integer getNumOfRetries() {
        return numOfRetries;
    }

    /**
     * setter method for Number of Retries
     *
     * @param numOfRetries int
     */
    public void setNumOfRetries(Integer numOfRetries) {
        this.numOfRetries = numOfRetries;
    }

    /**
     * getter method for Max Corporate Contacts
     *
     * @return int
     */
    public Integer getMaxCorporateContacts() {
        return maxCorporateContacts;
    }

    /**
     * setter method for Max Corporate Contacts
     *
     * @param maxCorporateContacts int
     */
    public void setMaxCorporateContacts(Integer maxCorporateContacts) {
        this.maxCorporateContacts = maxCorporateContacts;
    }

    /**
     * getter method for Presence Publish Throttle Timer
     *
     * @return int
     */
    public Integer getPresencePublishThrottleTimer() {
        return presencePublishThrottleTimer;
    }

    /**
     * setter method for Presence Publish Throttle Timer
     *
     * @param presencePublishThrottleTimer int
     */
    public void setPresencePublishThrottleTimer(Integer presencePublishThrottleTimer) {
        this.presencePublishThrottleTimer = presencePublishThrottleTimer;
    }

    /**
     * getter method for Primary Registrar URI
     *
     * @return String
     */
    public String getPrimaryRegistrarRoute() {
        return primaryRegistrarRoute;
    }

    /**
     * setter method for Primary Registrar URI
     *
     * @param primaryRegistrarRoute String
     */
    public void setPrimaryRegistrarRoute(String primaryRegistrarRoute) {
        this.primaryRegistrarRoute = primaryRegistrarRoute;
    }

    /**
     * getter method for geo Registrar URI
     *
     * @return String
     */
    public String getGeoRegistrarRoute() {
        return geoRegistrarRoute;
    }

    /**
     * setter method for Geo Registrar URI
     *
     * @param geoRegistrarRoute String
     */
    public void setGeoRegistrarRoute(String geoRegistrarRoute) {
        this.geoRegistrarRoute = geoRegistrarRoute;
    }

    /**
     * getter method for the XUI
     *
     * @return String
     */
    public String getXui() {
        return xui;
    }

    /**
     * setter method for the XUI
     *
     * @param xui String
     */
    public void setXui(String xui) {
        this.xui = xui;
    }

    /**
     * getter method for the LastProfileUpdateTime
     *
     * @return lastProfileUpdateTime long
     */

    public long getLastProfileUpdateTime() {
        return lastProfileUpdateTime;
    }

    /**
     * setter method for the LastProfileUpdateTime
     *
     * @param lastProfileUpdateTime long
     */
    public void setLastProfileUpdateTime(long lastProfileUpdateTime) {
        this.lastProfileUpdateTime = lastProfileUpdateTime;
    }

    /**
     * getter method for the NumWakeUpMsgsPerBurst
     *
     * @return numOfBurstPerTrigger int
     */
    public Integer getNumOfBurstPerTrigger() {
        return numOfBurstPerTrigger;
    }

    /**
     * setter method for the NumWakeUpMsgsPerBurst
     *
     * @param numOfBurstPerTrigger int
     */
    public void setNumOfBurstPerTrigger(Integer numOfBurstPerTrigger) {
        this.numOfBurstPerTrigger = numOfBurstPerTrigger;
    }

    /**
     * getter method for the Max Talk Burst Duration
     *
     * @return max Talk Burst Duration int
     */
    public Integer getMaxTalkBurstDuration() {
        return maxTalkBurstDuration;
    }

    /**
     * setter method for the Max Talk Burst Duration
     *
     * @param maxTalkBurstDuration int
     */
    public void setMaxTalkBurstDuration(Integer maxTalkBurstDuration) {
        this.maxTalkBurstDuration = maxTalkBurstDuration;
    }

    /**
     * getter method for Active Feature Set
     *
     * @return long
     */
    public long getActiveFS1() {
        return activeFS1;
    }

    /**
     * setter method for Active Feature Set
     *
     * @param activeFS1 long
     */
    public void setActiveFS1(long activeFS1) {
        this.activeFS1 = activeFS1;
    }

    /**
     * getter method for the Object Id;
     *
     * @return String
     */
    public String getObjectId() {
        return this.getMdn();
    }

    public boolean isSupervisorOverride() {
        return supervisorOverride;
    }

    public void setSupervisorOverride(boolean supervisorOverride) {
        this.supervisorOverride = supervisorOverride;
    }

    public boolean isRoamingAllowed() {
        return roamingAllowed;
    }

    public void setRoamingAllowed(boolean roamingAllowed) {
        this.roamingAllowed = roamingAllowed;
    }

    public Integer getSessionRecoveryTimer1() {
        return sessionRecoveryTimer1;
    }

    public void setSessionRecoveryTimer1(Integer sessionRecoveryTimer1) {
        this.sessionRecoveryTimer1 = sessionRecoveryTimer1;
    }

    public Integer getSessionRecoveryTimer2() {
        return sessionRecoveryTimer2;
    }

    public void setSessionRecoveryTimer2(Integer sessionRecoveryTimer2) {
        this.sessionRecoveryTimer2 = sessionRecoveryTimer2;
    }

    public Integer getSessionRecoveryTimer3() {
        return sessionRecoveryTimer3;
    }

    public void setSessionRecoveryTimer3(Integer sessionRecoveryTimer3) {
        this.sessionRecoveryTimer3 = sessionRecoveryTimer3;
    }

    public Integer getSessionRecoveryTimer4() {
        return sessionRecoveryTimer4;
    }

    public void setSessionRecoveryTimer4(Integer sessionRecoveryTimer4) {
        this.sessionRecoveryTimer4 = sessionRecoveryTimer4;
    }

    public Integer getSessionRecoveryTimer5() {
        return sessionRecoveryTimer5;
    }

    public void setSessionRecoveryTimer5(Integer sessionRecoveryTimer5) {
        this.sessionRecoveryTimer5 = sessionRecoveryTimer5;
    }

    public Integer getSessionRecoveryTimer6() {
        return sessionRecoveryTimer6;
    }

    public void setSessionRecoveryTimer6(Integer sessionRecoveryTimer6) {
        this.sessionRecoveryTimer6 = sessionRecoveryTimer6;
    }

    public Integer getIpDebouncerTimer() {
        return ipDebouncerTimer;
    }

    public void setIpDebouncerTimer(Integer ipDebouncerTimer) {
        this.ipDebouncerTimer = ipDebouncerTimer;
    }

    public Integer getTbcpRequestRetryTimer() {
        return tbcpRequestRetryTimer;
    }

    public void setTbcpRequestRetryTimer(Integer tbcpRequestRetryTimer) {
        this.tbcpRequestRetryTimer = tbcpRequestRetryTimer;
    }

    public Integer getTbcpReleaseRetryTimer() {
        return tbcpReleaseRetryTimer;
    }

    public void setTbcpReleaseRetryTimer(Integer tbcpReleaseRetryTimer) {
        this.tbcpReleaseRetryTimer = tbcpReleaseRetryTimer;
    }

    public Integer getFloorRecoveryTimer() {
        return floorRecoveryTimer;
    }

    public void setFloorRecoveryTimer(Integer floorRecoveryTimer) {
        this.floorRecoveryTimer = floorRecoveryTimer;
    }

    public Integer getLocationPublishInterval() {
        return locationPublishInterval;
    }

    public void setLocationPublishInterval(Integer locationPublishInterval) {
        this.locationPublishInterval = locationPublishInterval;
    }

    public Integer getPreCallNumKaMediaPackets() {
        return preCallNumKaMediaPackets;
    }

    public void setPreCallNumKaMediaPackets(Integer preCallNumKaMediaPackets) {
        this.preCallNumKaMediaPackets = preCallNumKaMediaPackets;
    }

    public Integer getPreCallKaPacketSize() {
        return preCallKaPacketSize;
    }

    public void setPreCallKaPacketSize(Integer preCallKaPacketSize) {
        this.preCallKaPacketSize = preCallKaPacketSize;
    }

    public Integer getKaPacketSize() {
        return kaPacketSize;
    }

    public void setKaPacketSize(Integer kaPacketSize) {
        this.kaPacketSize = kaPacketSize;
    }

    public Integer getPreCallKaInterval() {
        return preCallKaInterval;
    }

    public void setPreCallKaInterval(Integer preCallKaInterval) {
        this.preCallKaInterval = preCallKaInterval;
    }

    public Integer getKaInterval() {
        return kaInterval;
    }

    public void setKaInterval(Integer kaInterval) {
        this.kaInterval = kaInterval;
    }

    public Integer getPreCallKaDuration() {
        return preCallKaDuration;
    }

    public void setPreCallKaDuration(Integer preCallKaDuration) {
        this.preCallKaDuration = preCallKaDuration;
    }

    public long getClientFS1() {
        return clientFS1;
    }

    public void setClientFS1(long clientFS1) {
        this.clientFS1 = clientFS1;
    }

    public Integer getRoamingBit() {
        return roamingBit;
    }

    public void setRoamingBit(Integer roamingBit) {
        this.roamingBit = roamingBit;
    }

    public Integer getClientType() {
        return clientType;
    }

    public void setClientType(Integer clientType) {
        this.clientType = clientType;
    }

    public String getSwUpdateQryInterval() {
        return swUpdateQryInterval;
    }

    public void setSwUpdateQryInterval(String swUpdateQryInterval) {
        this.swUpdateQryInterval = swUpdateQryInterval;
    }

    public String getSwUpdateInfoUrl() {
        return swUpdateInfoUrl;
    }

    public void setSwUpdateInfoUrl(String swUpdateInfoUrl) {
        this.swUpdateInfoUrl = swUpdateInfoUrl;
    }

    public String getSwUpdatePkgUrl() {
        return swUpdatePkgUrl;
    }

    public void setSwUpdatePkgUrl(String swUpdatePkgUrl) {
        this.swUpdatePkgUrl = swUpdatePkgUrl;
    }

    public String getTuSmsAddress() {
        return tuSmsAddress;
    }

    public void setTuSmsAddress(String tuSmsAddress) {
        this.tuSmsAddress = tuSmsAddress;
    }

    public long getTuDownTimer() {
        return tuDownTimer;
    }

    public void setTuDownTimer(long tuDownTimer) {
        this.tuDownTimer = tuDownTimer;
    }

    public long getTuUpTimerStartVal() {
        return tuUpTimerStartVal;
    }

    public void setTuUpTimerStartVal(long tuUpTimerStartVal) {
        this.tuUpTimerStartVal = tuUpTimerStartVal;
    }

    public long getTuUpTimerMaxVal() {
        return tuUpTimerMaxVal;
    }

    public void setTuUpTimerMaxVal(long tuUpTimerMaxVal) {
        this.tuUpTimerMaxVal = tuUpTimerMaxVal;
    }

    public long getTuUpTimerRampDownPeriod() {
        return tuUpTimerRampDownPeriod;
    }

    public void setTuUpTimerRampDownPeriod(long tuUpTimerRampDownPeriod) {
        this.tuUpTimerRampDownPeriod = tuUpTimerRampDownPeriod;
    }

    public long getTuForceOnlineMaxWaitTimer() {
        return tuForceOnlineMaxWaitTimer;
    }

    public void setTuForceOnlineMaxWaitTimer(long tuForceOnlineMaxWaitTimer) {
        this.tuForceOnlineMaxWaitTimer = tuForceOnlineMaxWaitTimer;
    }

    public String getSipProxyURI() {
        return sipProxyURI;
    }

    public void setSipProxyURI(String sipProxyURI) {
        this.sipProxyURI = sipProxyURI;
    }

    public Integer getClientConnRetryInterval() {
        return clientConnRetryInterval;
    }

    public void setClientConnRetryInterval(Integer clientConnRetryInterval) {
        this.clientConnRetryInterval = clientConnRetryInterval;
    }

    public Integer getMaxClientConnRtyAttempts() {
        return maxClientConnRtyAttempts;
    }

    public void setMaxClientConnRtyAttempts(Integer maxClientConnRtyAttempts) {
        this.maxClientConnRtyAttempts = maxClientConnRtyAttempts;
    }

    public Integer getClientConnSecurityLevel() {
        return clientConnSecurityLevel;
    }

    public void setClientConnSecurityLevel(Integer clientConnSecurityLevel) {
        this.clientConnSecurityLevel = clientConnSecurityLevel;
    }

    public Integer getClientSipTxnTimeout() {
        return clientSipTxnTimeout;
    }

    public void setClientSipTxnTimeout(Integer clientSipTxnTimeout) {
        this.clientSipTxnTimeout = clientSipTxnTimeout;
    }

    public Integer getClientSipReferTxnTimeout() {
        return clientSipReferTxnTimeout;
    }

    public void setClientSipReferTxnTimeout(Integer clientSipReferTxnTimeout) {
        this.clientSipReferTxnTimeout = clientSipReferTxnTimeout;
    }

    public Integer getMinTcpKaTimerOnWifi() {
        return minTcpKaTimerOnWifi;
    }

    public void setMinTcpKaTimerOnWifi(Integer minTcpKaTimerOnWifi) {
        this.minTcpKaTimerOnWifi = minTcpKaTimerOnWifi;
    }

    public Integer getWifiTcpKaTimerIncrVal() {
        return wifiTcpKaTimerIncrVal;
    }

    public void setWifiTcpKaTimerIncrVal(Integer wifiTcpKaTimerIncrVal) {
        this.wifiTcpKaTimerIncrVal = wifiTcpKaTimerIncrVal;
    }

    public Integer getMaxTcpKaTimerOnWifi() {
        return maxTcpKaTimerOnWifi;
    }

    public void setMaxTcpKaTimerOnWifi(Integer maxTcpKaTimerOnWifi) {
        this.maxTcpKaTimerOnWifi = maxTcpKaTimerOnWifi;
    }

    public Integer getWifiSsidTimeoutMapSize() {
        return wifiSsidTimeoutMapSize;
    }

    public void setWifiSsidTimeoutMapSize(Integer wifiSsidTimeoutMapSize) {
        this.wifiSsidTimeoutMapSize = wifiSsidTimeoutMapSize;
    }

    public Integer getTcpKaTimerOnMacroCellular() {
        return tcpKaTimerOnMacroCellular;
    }

    public void setTcpKaTimerOnMacroCellular(Integer tcpKaTimerOnMacroCellular) {
        this.tcpKaTimerOnMacroCellular = tcpKaTimerOnMacroCellular;
    }

    public Integer getDetectWifiNatTcpTimeout() {
        return detectWifiNatTcpTimeout;
    }

    public void setDetectWifiNatTcpTimeout(Integer detectWifiNatTcpTimeout) {
        this.detectWifiNatTcpTimeout = detectWifiNatTcpTimeout;
    }

    public Integer getMediaSecureSessionRefreshIntvl() {
        return mediaSecureSessionRefreshIntvl;
    }

    public void setMediaSecureSessionRefreshIntvl(Integer mediaSecureSessionRefreshIntvl) {
        this.mediaSecureSessionRefreshIntvl = mediaSecureSessionRefreshIntvl;
    }

    public Integer getClientInCallSuspendTimer() {
        return clientInCallSuspendTimer;
    }

    public void setClientInCallSuspendTimer(Integer clientInCallSuspendTimer) {
        this.clientInCallSuspendTimer = clientInCallSuspendTimer;
    }

    /**
     * getter method for MDN
     *
     * @return String
     */
    public String getMdn() {
        return mdn;
    }

    /**
     * setter method for MDN
     *
     * @param mdn String
     */
    public void setMdn(String mdn) {
        if (mdn != null) {
            mdn = mdn.trim();
            if (mdn.equals("")) {
                mdn = null;
            }
        }
        super.setMdn(mdn);
        this.mdn = mdn;
    }

    public int getTuSmsAddressTon() {
        return tuSmsAddressTon;
    }

    public void setTuSmsAddressTon(int tuSmsAddressTon) {
        this.tuSmsAddressTon = tuSmsAddressTon;
    }

    public Integer getMwpt() {
        return mwpt;
    }

    public void setMwpt(Integer mwpt) {
        this.mwpt = mwpt;
    }

    public Integer getWpgt() {
        return wpgt;
    }

    public void setWpgt(Integer wpgt) {
        this.wpgt = wpgt;
    }

    public Integer getSrpi() {
        return srpi;
    }

    public void setSrpi(Integer srpi) {
        this.srpi = srpi;
    }

    public Integer getStci() {
        return stci;
    }

    public void setStci(Integer stci) {
        this.stci = stci;
    }

    public String getLgsrvuri() {
        return lgsrvuri;
    }

    public void setLgsrvuri(String lgsrvuri) {
        this.lgsrvuri = lgsrvuri;
    }

    public Integer getScgbm() {
        return scgbm;
    }

    public void setScgbm(Integer scgbm) {
        this.scgbm = scgbm;
    }

    public Integer getSpgag() {
        return spgag;
    }

    public void setSpgag(Integer spgag) {
        this.spgag = spgag;
    }

    public String getLtekap() {
        return ltekap;
    }

    public void setLtekap(String ltekap) {
        this.ltekap = ltekap;
    }

    public String getWfkap() {
        return wfkap;
    }

    public void setWfkap(String wfkap) {
        this.wfkap = wfkap;
    }

    public String getUmtskap() {
        return umtskap;
    }

    public void setUmtskap(String umtskap) {
        this.umtskap = umtskap;
    }

    public Integer getSnv() {
        return snv;
    }

    public void setSnv(Integer snv) {
        this.snv = snv;
    }

    public String getCgpu() {
        return cgpu;
    }

    public void setCgpu(String cgpu) {
        this.cgpu = cgpu;
    }

    public Integer getTpts() {
        return tpts;
    }

    public void setTpts(Integer tpts) {
        this.tpts = tpts;
    }

    public Integer getTptm() {
        return tptm;
    }

    public void setTptm(Integer tptm) {
        this.tptm = tptm;
    }

    public String getIpv() {
        return ipv;
    }

    public void setIpv(String ipv) {
        this.ipv = ipv;
    }

    public Integer getTcpktmcv6() {
        return tcpktmcv6;
    }

    public void setTcpktmcv6(Integer tcpktmcv6) {
        this.tcpktmcv6 = tcpktmcv6;
    }

    public Integer getMssrtv6() {
        return mssrtv6;
    }

    public void setMssrtv6(Integer mssrtv6) {
        this.mssrtv6 = mssrtv6;
    }

    public Integer getMprtv6() {
        return mprtv6;
    }

    public void setMprtv6(Integer mprtv6) {
        this.mprtv6 = mprtv6;
    }

    public String getGppr() {
        return gppr;
    }

    public void setGppr(String gppr) {
        this.gppr = gppr;
    }

    public String getPprw() {
        return pprw;
    }

    public void setPprw(String pprw) {
        this.pprw = pprw;
    }

    public String getGpprw() {
        return gpprw;
    }

    public void setGpprw(String gpprw) {
        this.gpprw = gpprw;
    }

    public String getLgsrvuriw() {
        return lgsrvuriw;
    }

    public void setLgsrvuriw(String lgsrvuriw) {
        this.lgsrvuriw = lgsrvuriw;
    }

    public Integer getPcruv6() {
        return pcruv6;
    }

    public void setPcruv6(Integer pcruv6) {
        this.pcruv6 = pcruv6;
    }

    public Integer getPcgt() {
        return pcgt;
    }

    public void setPcgt(Integer pcgt) {
        this.pcgt = pcgt;
    }

    public String getXcaprooturiwifi() {
        return xcaprooturiwifi;
    }

    public void setXcaprooturiwifi(String xcaprooturiwifi) {
        this.xcaprooturiwifi = xcaprooturiwifi;
    }



    public String getApnName() {
        return apnName;
    }

    public void setApnName(String apnName) {
        this.apnName = apnName;
    }

    public String getOdlfreq() {
        return odlfreq;
    }

    public void setOdlfreq(String odlfreq) {
        this.odlfreq = odlfreq;
    }

    public String getOdldur() {
        return odldur;
    }

    public void setOdldur(String odldur) {
        this.odldur = odldur;
    }

    public Integer getTfltconodl() {
        return tfltconodl;
    }

    public void setTfltconodl(Integer tfltconodl) {
        this.tfltconodl = tfltconodl;
    }

    public Integer getTfltsnapodl() {
        return tfltsnapodl;
    }

    public void setTfltsnapodl(Integer tfltsnapodl) {
        this.tfltsnapodl = tfltsnapodl;
    }

    public Integer getGodlreq() {
        return godlreq;
    }

    public void setGodlreq(Integer godlreq) {
        this.godlreq = godlreq;
    }

    public Boolean isAsnchg() {
        return asnchg;
    }

    public void setAsnchg(Boolean asnchg) {
        this.asnchg = asnchg;
    }

    public Integer getMscl() {
        return mscl;
    }

    public void setMscl(Integer mscl) {
        this.mscl = mscl;
    }

    public Integer getUprio() {
        return uprio;
    }

    public void setUprio(Integer uprio) {
        this.uprio = uprio;
    }

    public String getEscl() {
        return escl;
    }

    public void setEscl(String escl) {
        this.escl = escl;
    }

    public Integer getAmrfpp() {
        return amrfpp;
    }

    public void setAmrfpp(Integer amrfpp) {
        this.amrfpp = amrfpp;
    }

    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    public String getCorpName() {
        return corpName;
    }

    public void setCorpName(String corpName) {
        this.corpName = corpName;
    }

    public Integer getSuirpi() {
        return suirpi;
    }

    public void setSuirpi(Integer suirpi) {
        this.suirpi = suirpi;
    }

    public String getIpvc() {
        return ipvc;
    }

    public void setIpvc(String ipvc) {
        this.ipvc = ipvc;
    }

    public String getIpvprefc() {
        return ipvprefc;
    }

    public void setIpvprefc(String ipvprefc) {
        this.ipvprefc = ipvprefc;
    }

    public String getIpvprefw() {
        return ipvprefw;
    }

    public void setIpvprefw(String ipvprefw) {
        this.ipvprefw = ipvprefw;
    }

	public String getIpvmulti() {
		return ipvmulti;
	}

	public void setIpvmulti(String ipvmulti) {
		this.ipvmulti = ipvmulti;
	}

	public String getIpvprefmulti() {
		return ipvprefmulti;
	}

	public void setIpvprefmulti(String ipvprefmulti) {
		this.ipvprefmulti = ipvprefmulti;
	}

    public String getClientPVmajorVer() {
        return clientPVmajorVer;
    }

    public void setClientPVmajorVer(String clientPVmajorVer) {
        this.clientPVmajorVer = clientPVmajorVer;
    }
    public String getPwsuriw() {
        return pwsuriw;
    }

    public void setPwsuriw(String pwsuriw) {
        this.pwsuriw = pwsuriw;
    }

    public String getGwsuriw() {
        return gwsuriw;
    }

    public void setGwsuriw(String gwsuriw) {
        this.gwsuriw = gwsuriw;
    }

    public String getTrice() {
        return trice;
    }

    public void setTrice(String trice) {
        this.trice = trice;
    }

    public Integer getDistListRr() {
        return distListRr;
    }

    public void setDistListRr(Integer distListRr) {
        this.distListRr = distListRr;
    }

    public String getpWsUri() {
        return pWsUri;
    }

    public void setpWsUri(String pWsUri) {
        this.pWsUri = pWsUri;
    }

    public String getgWsUri() {
        return gWsUri;
    }

    public void setgWsUri(String gWsUri) {
        this.gWsUri = gWsUri;
    }

    public Integer getWsCaeT() {
        return wsCaeT;
    }

    public void setWsCaeT(Integer wsCaeT) {
        this.wsCaeT = wsCaeT;
    }

    public String getNegCI() {
        return negCI;
    }

    public void setNegCI(String negCI) {
        this.negCI = negCI;
    }

    public String getClientCap() {
        return clientCap;
    }

    public void setClientCap(String clientCap) {
        this.clientCap = clientCap;
    }


    public String getIpaATtl() {
        return ipaATtl;
    }

    public void setIpaATtl(String ipaATtl) {
        this.ipaATtl = ipaATtl;
    }

    public String getCtl() {
        return ctl;
    }

    public void setCtl(String ctl) {
        this.ctl = ctl;
    }

    public String getCtlIntl() {
        return ctlIntl;
    }

    public void setCtlIntl(String ctlIntl) {
        this.ctlIntl = ctlIntl;
    }

    public String getRadScLS() {
        return radScLS;
    }

    public void setRadScLS(String radScLS) {
        this.radScLS = radScLS;
    }

    public String getRadChLS() {
        return radChLS;
    }

    public void setRadChLS(String radChLS) {
        this.radChLS = radChLS;
    }


    public String getMaxTextMessageSize() {
        return maxTextMessageSize;
    }

    public void setMaxTextMessageSize(String maxTextMessageSize) {
        this.maxTextMessageSize = maxTextMessageSize;
    }

    public String getMaxMultimediaMsgSizeOverCellular() {
        return maxMultimediaMsgSizeOverCellular;
    }

    public void setMaxMultimediaMsgSizeOverCellular(String maxMultimediaMsgSizeOverCellular) {
        this.maxMultimediaMsgSizeOverCellular = maxMultimediaMsgSizeOverCellular;
    }

    public String getMaxMultimediaMsgSizeOverWifi() {
        return maxMultimediaMsgSizeOverWifi;
    }

    public void setMaxMultimediaMsgSizeOverWifi(String maxMultimediaMsgSizeOverWifi) {
        this.maxMultimediaMsgSizeOverWifi = maxMultimediaMsgSizeOverWifi;
    }

    public String getPush2MessageDeliveryReceiptEnabled() {
        return push2MessageDeliveryReceiptEnabled;
    }

    public void setPush2MessageDeliveryReceiptEnabled(String push2MessageDeliveryReceiptEnabled) {
        this.push2MessageDeliveryReceiptEnabled = push2MessageDeliveryReceiptEnabled;
    }

    public String getPush2MessageReadReceiptEnabled() {
        return push2MessageReadReceiptEnabled;
    }

    public void setPush2MessageReadReceiptEnabled(String push2MessageReadReceiptEnabled) {
        this.push2MessageReadReceiptEnabled = push2MessageReadReceiptEnabled;
    }

    public String getPush2MessageFleetMemberGeoTag() {
        return push2MessageFleetMemberGeoTag;
    }

    public void setPush2MessageFleetMemberGeoTag(String push2MessageFleetMemberGeoTag) {
        this.push2MessageFleetMemberGeoTag = push2MessageFleetMemberGeoTag;
    }

    public String getMaxUserPredefinedMessages() {
        return maxUserPredefinedMessages;
    }

    public void setMaxUserPredefinedMessages(String maxUserPredefinedMessages) {
        this.maxUserPredefinedMessages = maxUserPredefinedMessages;
    }

    public String getGeoFenceDistanceMeasurementUnit() {
        return geoFenceDistanceMeasurementUnit;
    }

    public void setGeoFenceDistanceMeasurementUnit(String geoFenceDistanceMeasurementUnit) {
        this.geoFenceDistanceMeasurementUnit = geoFenceDistanceMeasurementUnit;
    }

    public String getGeoFencePeriodicLocationUpdateInterval() {
        return geoFencePeriodicLocationUpdateInterval;
    }

    public void setGeoFencePeriodicLocationUpdateInterval(String geoFencePeriodicLocationUpdateInterval) {
        this.geoFencePeriodicLocationUpdateInterval = geoFencePeriodicLocationUpdateInterval;
    }

    public String getGeoFencingPeriod() {
        return geoFencingPeriod;
    }

    public void setGeoFencingPeriod(String geoFencingPeriod) {
        this.geoFencingPeriod = geoFencingPeriod;
    }

    public String getGeoFenceDist4Client() {
        return geoFenceDist4Client;
    }

    public void setGeoFenceDist4Client(String geoFenceDist4Client) {
        this.geoFenceDist4Client = geoFenceDist4Client;
    }

    public String getGeoFenceNotificationTh() {
        return geoFenceNotificationTh;
    }

    public void setGeoFenceNotificationTh(String geoFenceNotificationTh) {
        this.geoFenceNotificationTh = geoFenceNotificationTh;
    }

    public String getGeoFenceNotificationInt() {
        return geoFenceNotificationInt;
    }

    public void setGeoFenceNotificationInt(String geoFenceNotificationInt) {
        this.geoFenceNotificationInt = geoFenceNotificationInt;
    }

    public String getOnCallLocationUpdateEnabled() {
        return onCallLocationUpdateEnabled;
    }

    public void setOnCallLocationUpdateEnabled(String onCallLocationUpdateEnabled) {
        this.onCallLocationUpdateEnabled = onCallLocationUpdateEnabled;
    }

    public String getOnCallLocationUpdateInterval() {
        return onCallLocationUpdateInterval;
    }

    public void setOnCallLocationUpdateInterval(String onCallLocationUpdateInterval) {
        this.onCallLocationUpdateInterval = onCallLocationUpdateInterval;
    }

    public String getSgwRootUriCellular() {
        return sgwRootUriCellular;
    }

    public void setSgwRootUriCellular(String sgwRootUriCellular) {
        this.sgwRootUriCellular = sgwRootUriCellular;
    }

    public String getSgwRootUriWifi() {
        return sgwRootUriWifi;
    }

    public void setSgwRootUriWifi(String sgwRootUriWifi) {
        this.sgwRootUriWifi = sgwRootUriWifi;
    }

    public String getAuthUriCellular() {
        return authUriCellular;
    }

    public void setAuthUriCellular(String authUriCellular) {
        this.authUriCellular = authUriCellular;
    }

    public String getAuthUriWifi() {
        return authUriWifi;
    }

    public void setAuthUriWifi(String authUriWifi) {
        this.authUriWifi = authUriWifi;
    }

    public String getCbBukInfo() {
        return cbBukInfo;
    }

    public void setCbBukInfo(String cbBukInfo) {
        this.cbBukInfo = cbBukInfo;
    }

    public String getSgwAuthMec() {
        return sgwAuthMec;
    }

    public void setSgwAuthMec(String sgwAuthMec) {
        this.sgwAuthMec = sgwAuthMec;
    }

    public String getSgwHbInterval() {
        return sgwHbInterval;
    }

    public void setSgwHbInterval(String sgwHbInterval) {
        this.sgwHbInterval = sgwHbInterval;
    }

    public String getMinVoiceMsgFallBackLen() {
        return minVoiceMsgFallBackLen;
    }

    public void setMinVoiceMsgFallBackLen(String minVoiceMsgFallBackLen) {
        this.minVoiceMsgFallBackLen = minVoiceMsgFallBackLen;
    }

    public Integer getMapProviderId() {
        return mapProviderId;
    }

    public void setMapProviderId(Integer mapProviderId) {
        this.mapProviderId = mapProviderId;
    }

    public String getMapsUriC() {
        return mapsUriC;
    }

    public void setMapsUriC(String mapsUriC) {
        this.mapsUriC = mapsUriC;
    }

    public String getMapsUriW() {
        return mapsUriW;
    }

    public void setMapsUriW(String mapsUriW) {
        this.mapsUriW = mapsUriW;
    }

    public String getMapsGeoUriC() {
        return mapsGeoUriC;
    }

    public void setMapsGeoUriC(String mapsGeoUriC) {
        this.mapsGeoUriC = mapsGeoUriC;
    }

    public String getMapsGeoUriW() {
        return mapsGeoUriW;
    }

    public void setMapsGeoUriW(String mapsGeoUriW) {
        this.mapsGeoUriW = mapsGeoUriW;
    }

    public String getgApiKey() {
        return gApiKey;
    }

    public void setgApiKey(String gApiKey) {
        this.gApiKey = gApiKey;
    }

    public String getSgmLocUriCell() {
        return sgmLocUriCell;
    }

    public void setSgmLocUriCell(String sgmLocUriCell) {
        this.sgmLocUriCell = sgmLocUriCell;
    }

    public String getSgmLocUriWifi() {
        return sgmLocUriWifi;
    }

    public void setSgmLocUriWifi(String sgmLocUriWifi) {
        this.sgmLocUriWifi = sgmLocUriWifi;
    }

    public String getMapStatsReportIntvl() {
        return mapStatsReportIntvl;
    }

    public void setMapStatsReportIntvl(String mapStatsReportIntvl) {
        this.mapStatsReportIntvl = mapStatsReportIntvl;
    }

    public String getLocExpTimeIntrvl() {
        return locExpTimeIntrvl;
    }

    public void setLocExpTimeIntrvl(String locExpTimeIntrvl) {
        this.locExpTimeIntrvl = locExpTimeIntrvl;
    }

    public Integer getClientRecLen() {
        return clientRecLen;
    }

    public void setClientRecLen(Integer clientRecLen) {
        this.clientRecLen = clientRecLen;
    }

    public Integer getPttRadioClientGrpht() {
        return pttRadioClientGrpht;
    }

    public void setPttRadioClientGrpht(Integer pttRadioClientGrpht) {
        this.pttRadioClientGrpht = pttRadioClientGrpht;
    }

    public Integer getPttradioClientNongrpht() {
        return pttradioClientNongrpht;
    }

    public void setPttradioClientNongrpht(Integer pttradioClientNongrpht) {
        this.pttradioClientNongrpht = pttradioClientNongrpht;
    }

    public Integer getWebDispMapStatReportIntvl() {
        return webDispMapStatReportIntvl;
    }

    public void setWebDispMapStatReportIntvl(Integer webDispMapStatReportIntvl) {
        this.webDispMapStatReportIntvl = webDispMapStatReportIntvl;
    }

    public Integer getWebDispUiStatReportIntvl() {
        return webDispUiStatReportIntvl;
    }

    public void setWebDispUiStatReportIntvl(Integer webDispUiStatReportIntvl) {
        this.webDispUiStatReportIntvl = webDispUiStatReportIntvl;
    }

    public String getActiveFS2() {
        return activeFS2;
    }

    public void setActiveFS2(String activeFS2) {
        this.activeFS2 = activeFS2;
    }

    public String getClientFS2() {
        return clientFS2;
    }

    public void setClientFS2(String clientFS2) {
        this.clientFS2 = clientFS2;
    }

    public Integer getAcrtepc() {
        return acrtepc;
    }

    public void setAcrtepc(Integer acrtepc) {
        this.acrtepc = acrtepc;
    }

    public Integer getAcrtepg() {
        return acrtepg;
    }

    public void setAcrtepg(Integer acrtepg) {
        this.acrtepg = acrtepg;
    }

    public Integer getAcrteag() {
        return acrteag;
    }

    public void setAcrteag(Integer acrteag) {
        this.acrteag = acrteag;
    }

    public Integer getAltitudeFlag() {
        return altitudeFlag;
    }

    public void setAltitudeFlag(Integer altitudeFlag) {
        this.altitudeFlag = altitudeFlag;
    }

    public Integer getVerticalAccuracyFlag() {
        return verticalAccuracyFlag;
    }

    public void setVerticalAccuracyFlag(Integer verticalAccuracyFlag) {
        this.verticalAccuracyFlag = verticalAccuracyFlag;
    }

    public Integer getMediaMissingTimer() {
        return mediaMissingTimer;
    }

    public void setMediaMissingTimer(Integer mediaMissingTimer) {
        this.mediaMissingTimer = mediaMissingTimer;
    }

    public Integer getMulticastKaInterval() {
        return multicastKaInterval;
    }

    public void setMulticastKaInterval(Integer multicastKaInterval) {
        this.multicastKaInterval = multicastKaInterval;
    }

    public KnPttSettingsDocType getPttSettingsDoc() {
        return pttSettingsDoc;
    }

    public void setPttSettingsDoc(KnPttSettingsDocType pttSettingsDoc) {
        this.pttSettingsDoc = pttSettingsDoc;
    }

    public Integer getKpiRepAudInterval() {
        return kpiRepAudInterval;
    }

    public void setKpiRepAudInterval(Integer kpiRepAudInterval) {
        this.kpiRepAudInterval = kpiRepAudInterval;
    }

    public Integer getKpiRepUpRand() {
        return kpiRepUpRand;
    }

    public void setKpiRepUpRand(Integer kpiRepUpRand) {
        this.kpiRepUpRand = kpiRepUpRand;
    }

    public Integer getKpiRepMcs() {
        return kpiRepMcs;
    }

    public void setKpiRepMcs(Integer kpiRepMcs) {
        this.kpiRepMcs = kpiRepMcs;
    }

    public String toString() {
        StringBuilder strBuffer = new StringBuilder(500);
        strBuffer.append(super.toString())
                .append(", MDN - ").append(KnGDPRTemplate.mdn(mdn))
                .append(",public Subscription_Type - ").append(pubSubscriptionType)
                .append(", corp Subscription_Type - ").append(corpSubscriptionType)
                .append(", Subscription_State - ").append(subscriptionState)
                .append(", Subscriber_Name - ").append(KnGDPRTemplate.name(networkName))
                .append(", XUI - ").append(xui)
                .append(", Primary_Registrar_URI  - ").append(primaryRegistrarRoute)
                .append(", Geo_Registrar_URI  - ").append(geoRegistrarRoute)
                .append(", Primary_Session_URI  - ").append(primarySessionRoute)
                .append(", Geo_Session_URI  - ").append(geoSessionRoute)
                .append(", Primary_POC_Setting_URI  - ").append(primaryPocSettingRoute)
                .append(", Geo_POC_Setting_URI  - ").append(geoPocSettingRoute)
                .append(", Primary_IPA_URI  - ").append(primaryIpaRoute)
                .append(", Geo_IPA_URI  - ").append(geoIpaRoute)
                .append(", Primary_Presence_URI  - ").append(primaryPresenceRoute)
                .append(", Geo_Presence_URI  - ").append(geoPresenceRoute)
                .append(", Primary_RLS_URI  - ").append(primaryRlsRoute)
                .append(", Geo_RLS_URI  - ").append(geoRlsRoute)
                .append(", Xcap_Root_URI  - ").append(xcapRootUri)
                .append(", Primary_Xdms_URI  - ").append(primaryXdmsRoute)
                .append(", Geo_Xdms_URI  - ").append(geoXdmsRoute)
                .append(", Max_Public_Contacts  - ").append(maxPublicContacts)
                .append(", Max_Corporate_Contacts  - ").append(maxCorporateContacts)
                .append(", Max_Public_Groups  - ").append(maxPublicGroups)
                .append(", Max_Corporate_Groups  - ").append(maxCorporateGroups)
                .append(", Max_Members_Per_Public_Group  - ").append(maxMembersPerPublicGroup)
                .append(", Max_Members_per_Corp_Group  - ").append(maxMembersPerCorpGroup)
                .append(", Max_Adhos_Group_Size  - ").append(maxAdhocGroupSize)
                .append(", Dial_Plan_Info  - ").append(dialPlanInfo)
                .append(", Conference_Factory_URI  - ").append(conferenceFactoryUri)
                .append(", TBCP_Request_Timer  - ").append(tbcpRequestTimer)
                .append(", TBCP_Release_Timer  - ").append(tbcpReleaseTimer)
                .append(", Media_End_Timer  - ").append(mediaEndTimer)
                .append(", Media_Idle_Timer  - ").append(mediaIdleTimer)
                .append(", Register_Timer  - ").append(registerTimer)
                .append(", Publish_POC_Settings_Timer  - ").append(publishPocSettingsTimer)
                .append(", Publish_Presence_Timer  - ").append(publishPresenceTimer)
                .append(", Invite_Timer  - ").append(inviteTimer)
                .append(", Media_Port_Refresh_Timer  - ").append(mediaPortRefreshTimer)
                .append(", Media_Intrabrust_Interval  - ").append(mediaIntraburstInterval)
                .append(", Num_KA_Media_Packets  - ").append(numKaMediaPackets)
                .append(", Media_Payload_Length  - ").append(mediaPayloadLength)
                .append(", Location_Debouncing_Timer  - ").append(locationDebouncingTimer)
                .append(", RLS_Subscription_Timer  - ").append(rlsSubscriptionTimer)
                .append(", Support_PreEstablishment_Session  - ").append(supportPreEstablishmentSession)
                .append(", Support_Simultaneous_Session  - ").append(supportSimultaneousSession)
                .append(", Conference_URI_Template  - ").append(conferenceUriTemplate)
                .append(", Primary_Subscription_Proxy_URI  - ").append(primarySubscriptionProxyUri)
                .append(", Geo_Subscription_Timer  - ").append(geoSubscriptionProxyUri)
                .append(", Xdms_Subscription_Timer  - ").append(xdmsSubscriptionTimer)
                .append(", Number_Of_Retries  - ").append(numOfRetries)
                .append(", Number_Of_TBCP_Retries  - ").append(numOfTbcpRetries)
                .append(", Presence_Publish_Throttle_Timer  - ").append(presencePublishThrottleTimer)
                .append(", Number_of_WakeUp_Triggers  - ").append(numOfWakeupTriggers)
                .append(", Octet_Size  - ").append(octetSize)
                .append(", WakeUp_Time_Interval  - ").append(wakeupTimeInterval)
                .append(", Num_WakeUp_Msgs_Per_Burst  - ").append(numOfBurstPerTrigger)
                .append(", Insta_POC  - ").append(instaPoc)
                .append(", Max Talk Burst Duration - ").append(maxTalkBurstDuration)
                .append(", Last_Profile_Update_Timer - ").append(lastProfileUpdateTime)
                .append(", active_Feature_Set_1 - ").append(activeFS1)
                .append(", supervisorOverride - ").append(supervisorOverride)
                .append(", roamingAllowed - ").append(roamingAllowed)
                .append(", sessionRecoveryTimer1 - ").append(sessionRecoveryTimer1)
                .append(", sessionRecoveryTimer2 - ").append(sessionRecoveryTimer2)
                .append(", sessionRecoveryTimer3 - ").append(sessionRecoveryTimer3)
                .append(", sessionRecoveryTimer4 - ").append(sessionRecoveryTimer4)
                .append(", sessionRecoveryTimer5 - ").append(sessionRecoveryTimer5)
                .append(", sessionRecoveryTimer6 - ").append(sessionRecoveryTimer6)
                .append(", ipDebouncerTimer - ").append(ipDebouncerTimer)
                .append(", tbcpRequestRetryTimer - ").append(tbcpRequestRetryTimer)
                .append(", tbcpReleaseRetryTimer - ").append(tbcpReleaseRetryTimer)
                .append(", floorRecoveryTimer - ").append(floorRecoveryTimer)
                .append(", LocationPublishInterval - ").append(locationPublishInterval)
                .append(", ClientType - ").append(clientType)
                .append(", software_Update_Qry_Interval - ").append(swUpdateQryInterval)
                .append(", software_Update_Info_Url - ").append(swUpdateInfoUrl)
                .append(", software_Update_Pkg_Url - ").append(swUpdatePkgUrl)

                .append(", tu_Sms_Address - ").append(tuSmsAddress)
                .append(", tu_Down_Timer - ").append(tuDownTimer)
                .append(", tu_Up_Timer_Start_Val - ").append(tuUpTimerStartVal)
                .append(", tu_Up_Timer_Max_Val - ").append(tuUpTimerMaxVal)
                .append(", tu_Up_Timer_Ramp_Down_Period - ").append(tuUpTimerRampDownPeriod)
                .append(", tu_Force_Online_Max_Wait_Timer - ").append(tuForceOnlineMaxWaitTimer)
                .append(", tu_Sms_AddressTON - ").append(tuSmsAddressTon)
                .append(", sip_proxy_uri - ").append(sipProxyURI)
                .append(", client_Conn_Retry_Interval - ").append(clientConnRetryInterval)
                .append(", maxClient_Conn_Rty_Attempts - ").append(maxClientConnRtyAttempts)
                .append(", client_Conn_Security_Level - ").append(clientConnSecurityLevel)
                .append(", client_SipTxn_Timeout - ").append(clientSipTxnTimeout)
                .append(", client_SipRefer_Txn_Timeout - ").append(clientSipReferTxnTimeout)
                .append(", min_Tcp_Ka_Timer_On_Wifi - ").append(minTcpKaTimerOnWifi)
                .append(", wifi_Tcp_Ka_Timer_IncrVal - ").append(wifiTcpKaTimerIncrVal)
                .append(", maxTcp_Ka_Timer_On_Wifi - ").append(maxTcpKaTimerOnWifi)
                .append(", wifi_Ssid_Timeout_Map_Size - ").append(wifiSsidTimeoutMapSize)
                .append(", tcp_Ka_Timer_On_Macro_Cellular - ").append(tcpKaTimerOnMacroCellular)
                .append(", detect_Wifi_Nat_Tcp_Timeout - ").append(detectWifiNatTcpTimeout)
                .append(", media_Secure_Session_Refresh_Intvl - ").append(mediaSecureSessionRefreshIntvl)
                .append(", client_In_Call_Suspend_Timer - ").append(clientInCallSuspendTimer)
                .append(",mwpt - ").append(mwpt)
                .append(",wpgt -").append(wpgt)
                .append(",srpi -").append(srpi)
                .append(",stci -").append(stci)
                .append(",lgsrvuri -").append(lgsrvuri)
                .append(",scgbm -").append(scgbm)
                .append(",spgag -").append(spgag)
                .append(",ltekap -").append(ltekap)
                .append(",wfkap -").append(wfkap)
                .append(",umtskap -").append(umtskap)
                .append(",snv -").append(snv)
                .append(",cgpu -").append(cgpu)
                   //version 6.0
                .append(", tpts").append(tpts)
                .append(", tptm").append(tptm)
                .append(", ipv").append(ipv)
                .append(", tcpktmcv6 ").append(tcpktmcv6)
                .append(", mssrtv6").append(mssrtv6)
                .append(", mprtv6").append(mprtv6)
                .append(", gppr").append(gppr)
                .append(", pprw").append(pprw)
                .append(", gpprw").append(gpprw)
                .append(", lgsrvuriw").append(lgsrvuriw)
                .append(", pcruv6").append(pcruv6)
                .append(", pcgt").append(pcgt)
                .append(", xcaprooturiwifi").append(xcaprooturiwifi)
                .append(", apnName ").append(apnName)
                .append(", corpId ").append(corpId)
                .append(", corpName ").append(corpName)
                .append(", suirpi ").append(suirpi)
                .append(", ipvc ").append(ipvc)
                .append(", ipvprefc ").append(ipvprefc)
                .append(", ipvprefw ").append(ipvprefw)
                .append(", ipvmulti ").append(ipvmulti)
                .append(", ipvprefmulti ").append(ipvprefmulti)
                .append(" ,clientPVmajorVer ").append(clientPVmajorVer)
                .append(", pwsuriw ").append(pwsuriw)
                .append(", gwsuriw ").append(gwsuriw)
                .append(", trice ").append(trice)
                .append(", dispListRr ").append(distListRr)
                .append(", p-ws-uri ").append(pWsUri)
                .append(", g-ws-uri ").append(gWsUri)
                .append(", ws-cae-t ").append(wsCaeT)
                .append(", neg-c-i ").append(negCI)
                .append(", client-capabilities ").append(clientCap)
                .append(", ipaATtl ").append(ipaATtl)
                .append(", ctl ").append(ctl)
                .append(", ctlIntl ").append(ctlIntl)
                .append(", radChLS ").append(radChLS)
                .append(", radScLS ").append(radScLS)
                .append(", maxTextMessageSize ").append(maxTextMessageSize)
                .append(", maxMultimediaMsgSizeOverCellular ").append(maxMultimediaMsgSizeOverCellular)
                .append(", maxMultimediaMsgSizeOverWifi ").append(maxMultimediaMsgSizeOverWifi)
                .append(", push2MessageDeliveryReceiptEnabled ").append(push2MessageDeliveryReceiptEnabled)
                .append(", push2MessageReadReceiptEnabled ").append(push2MessageReadReceiptEnabled)
                .append(", push2MessageFleetMemberGeoTag ").append(push2MessageFleetMemberGeoTag)
                .append(", maxUserPredefinedMessages ").append(maxUserPredefinedMessages)
                .append(", geoFenceDistanceMeasurementUnit ").append(geoFenceDistanceMeasurementUnit)
                .append(", geoFencePeriodicLocationUpdateInterval ").append(geoFencePeriodicLocationUpdateInterval)
                .append(", geoFencingPeriod ").append(geoFencingPeriod)
                .append(", geoFenceDist4Client ").append(geoFenceDist4Client)
                .append(", geoFenceNotificationTh ").append(geoFenceNotificationTh)
                .append(", geoFenceNotificationInt ").append(geoFenceNotificationInt)
                .append(", onCallLocationUpdateEnabled ").append(onCallLocationUpdateEnabled)
                .append(", onCallLocationUpdateInterval ").append(onCallLocationUpdateInterval)
                .append(" sgwRootUriCellular - ").append(sgwRootUriCellular)
                .append(" sgwRootUriWifi - ").append(sgwRootUriWifi)
                .append(" authUriCellular - ").append(authUriCellular)
                .append(" authUriWifi - ").append(authUriWifi)
                .append(" cbBukInfo - ").append(cbBukInfo)
                .append(" sgwAuthMec - ").append(sgwAuthMec)
                .append(" sgwHbInterval - ").append(sgwHbInterval)
                .append(" minVoiceMsgFallBackLen - ").append(minVoiceMsgFallBackLen)
                .append(" mapProviderId - ").append(mapProviderId)
                .append(" mapsUriC - ").append(mapsUriC)
                .append(" mapsUriW - ").append(mapsUriW)
                .append(" mapsGeoUriC - ").append(mapsGeoUriC)
                .append(" mapsGeoUriW - ").append(mapsGeoUriW)
                .append(" gApiKey - ").append(gApiKey)
                .append(" sgmLocUriCell - ").append(sgmLocUriCell)
                .append(" sgmLocUriWifi - ").append(sgmLocUriWifi)
                .append(" mapStatsReportIntvl - ").append(mapStatsReportIntvl)
                .append("locExpTimeIntrvl - ").append(locExpTimeIntrvl)
                .append(" clientRecLen - ").append(clientRecLen)
                .append(" pttRadioClientGrpht - ").append(pttRadioClientGrpht)
                .append(" pttradioClientNongrpht - ").append(pttradioClientNongrpht)
                .append(" activeGeoFencGrpSize - ").append(activeGeoFencGrpSize)
                .append(" drxC - ").append(drxC)
                .append(" cqiP - ").append(cqiP)
                .append(" cqiT - ").append(cqiT)
                .append(" cqiMR - ").append(cqiMR)
                .append(" cqiPwT - ").append(cqiPwT)
                .append(" cqiDisp - ").append(cqiDisp)
                .append(" webDispMapStatReportIntvl - ").append(webDispMapStatReportIntvl)
                .append(" webDispUiStatReportIntvl - ").append(webDispUiStatReportIntvl)
                .append(" ufmi - ").append(ufmi)
                .append(" idenIpteropFlag - ").append(idenIpteropFlag)
                .append(" mabg - ").append(mabg)
                .append(" mlabg - ").append(mlabg)
                .append(" mlabu - ").append(mlabu)
                .append(" nchzn - ").append(nchzn)
                .append(" ntgch - ").append(ntgch)
                .append(" rpn - ").append(rpn)
                .append(" rpe - ").append(rpe)
                .append(" abdgUriC - ").append(abdgUriC)
                .append(" abdgUriW - ").append(abdgUriW)
                .append(" intCorpId - ").append(intCorpId)
                .append(" kuidPrefix - ").append(kuidPrefix)
                .append(" reaInd - ").append(reaInd)
                .append(" etgsMode - ").append(etgsMode)
                .append(" msmdst - ").append(msmdst)
                .append(" msmcnt - ").append(msmcnt)
                .append(" msmlen - ").append(msmlen)
                .append(" mdyss - ").append(mdyss)
                .append(" mddss - ").append(mddss)
                .append(" activeFS2 - ").append(activeFS2)
                .append(" clientFS2 - ").append(clientFS2)
                .append(" esriMapsClientId - ").append(esriMapsClientId)
                .append(" esriMapsSecretKey - ").append(esriMapsSecretKey)
                .append(" esriMapsCellularUri - ").append(esriMapsCellularUri)
                .append(" esriMapsWifiUri - ").append(esriMapsWifiUri)
                .append(" esriMapsGeoCellularUri - ").append(esriMapsGeoCellularUri)
                .append(" esriMapsGeoWifiUri - ").append(esriMapsGeoWifiUri)
                .append(" osmfml - ").append(osmfml)
                .append(" vmflrIdleTime - ").append(vmflrIdleTime)
                .append(" vmflrHldTime - ").append(vmflrHldTime)
                .append(" ispdse - ").append(ispdse)
                .append(" pdsmcl - ").append(pdsmcl)
                .append(" pdsmps - ").append(pdsmps)
                .append(" fdsUriC - ").append(fdsUriC)
                .append(" fdsUriW - ").append(fdsUriW)
                .append(" msdss - ").append(msdss)
                .append(" mfls - ").append(mfls)
                .append(" mafls - ").append(mafls)
                .append(" dflttl - ").append(dflttl)
                .append(" mflttl - ").append(mflttl)
                .append(" mmsgttl - ").append(mmsgttl)
                .append(" osmlei - ").append(osmlei)
                .append(" isOptInNeeded - ").append(isOptInNeeded)
                .append(" acrtepc - ").append(acrtepc)
                .append(" acrtepg - ").append(acrtepg)
                .append(" acrteag - ").append(acrteag)
                .append(" usrkeymatolp - ").append(usrkeymatolp)
                .append(" cskskew - ").append(cskskew)
                .append(" pckskew - ").append(pckskew)
                .append(" gmkskew - ").append(gmkskew)
                .append(" strencrypt - ").append(strencrypt)
                .append(" mdsiUri - ").append(mdsiUri)
                .append(" gmsUri - ").append(gmsUri)
                .append(" msgstoreuric - ").append(msgstoreuric)
                .append(" msgstoreuriw - ").append(msgstoreuriw)
                .append(" msimulsdstxns - ").append(msimulsdstxns)
                .append(" msimulfdtxns - ").append(msimulfdtxns)
                .append(" msrchentries - ").append(msrchentries)
                .append("recordingStatus - ").append(recordingStatus)
                .append("maxVideoSessions - ").append(maxVideoSessions)
                .append("mMemUsrRegrp - ").append(mMemUsrRegrp)
                .append("mUsrRegrps - ").append(mUsrRegrps)
                .append("mBtfPerOwner - ").append(mBtfPerOwner)
                .append("gmsServId - ").append(gmsServId)
                .append("btfDuration - ").append(btfDuration)
                .append("btfToneList - ").append(btfToneList)
                .append("btfTonePauseIntervals - ").append(btfTonePauseIntervals)
                .append("mGrpRegrps - ").append(mGrpRegrps)
                .append("altitudeFlag - ").append(altitudeFlag)
                .append("verticalAccuracyFlag - ").append(verticalAccuracyFlag)
                .append(" mediaMissingTimer - ").append(mediaMissingTimer)
                .append(" multicastKaInterval - ").append(multicastKaInterval)
                .append(" pttSettingsDoc - ").append(pttSettingsDoc)
                .append(" kpiRepAudInterval - ").append(kpiRepAudInterval)
                .append(" kpiRepUpRand - ").append(kpiRepUpRand)
                .append(" kpiRepMcs - ").append(kpiRepMcs)
                .append("]");

        return strBuffer.toString();
    }

}