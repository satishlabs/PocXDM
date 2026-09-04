/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.business.impl;

import com.kodiak.common.commdto.common.KnUserAgentDTO;
import com.kodiak.common.commdto.common.KnXDMMdnInfoDTO;
import com.kodiak.common.commdto.request.KnXDMTalkGroupInfoDTO;
import com.kodiak.common.commdto.response.*;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.exception.KnSystemException;
import com.kodiak.common.ggcache.dto.KnDefaultMCSClientInfo;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.common.resources.KnGeneralCacheUtil;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.xmlmodifier.impl.KnXMLProcessor;
import com.kodiak.xdms.server.common.KnXDMServerException;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dao.KnFactorySelector;
import com.kodiak.xdms.server.common.dao.persister.IXDMServerDAO;
import com.kodiak.xdms.server.common.dto.common.*;
import com.kodiak.xdms.server.common.dto.common.KnCorpGroupListInfoDTO;
import com.kodiak.xdms.server.common.dto.persistdat.KnCorpUserProfileDTO;
import com.kodiak.xdms.server.common.dto.persistdat.KnSubsProfilePersistDTO;
import com.kodiak.xdms.server.common.dto.persistdat.KnSubscriberPersistDTO;
import com.kodiak.xdms.server.common.framework.aas.KnAASException;
import com.kodiak.xdms.server.common.framework.aas.KnAASFramework;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorFramework;
import com.kodiak.xdms.server.common.resources.KnConstants;
import com.kodiak.xdms.server.common.resources.KnProfileTypes;
import com.kodiak.xdms.server.common.util.KnGeneralUtil;
import com.kodiak.xdms.server.pubmgmt.business.IPubMCPTTController;
import com.kodiak.xdms.server.pubmgmt.business.KnPubBOException;
import com.kodiak.xdms.server.pubmgmt.business.helper.KnPubInfoUtil;
import com.kodiak.xdms.server.pubmgmt.dao.KnPubFactorySelector;
import com.kodiak.xdms.server.pubmgmt.dao.persister.IPubXdmDAO;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnIPMCSDTO;
import com.kodiak.xdms.server.pubmgmt.dto.common.KnEmgrDestinationInfoDTO;
import com.kodiak.xdms.server.pubmgmt.dto.common.KnMCPTTPermInfoDTO;
import com.kodiak.xdms.server.pubmgmt.dto.common.KnMCSXCAPRespDTO;
import com.kodiak.xdms.server.pubmgmt.dto.common.KnMemberDTO;
import com.kodiak.xdms.server.pubmgmt.dto.persistdat.KnPubMCSXCAPPersistDTO;
import com.kodiak.xdms.server.pubmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.pubmgmt.resources.KnOperationTypes;


import java.util.*;
import java.util.stream.Collectors;

import static com.kodiak.common.resources.KnConstants.*;
import static com.kodiak.common.resources.KnConstants.FEATURE_SET.HTTPSSUPPORT;
import static com.kodiak.common.resources.KnGeneralUtil.*;
import static com.kodiak.common.resources.KnGeneralUtil.convertLongToBitSet;
import static com.kodiak.xdms.server.common.resources.KnConstants.*;
import static com.kodiak.xdms.server.common.resources.KnConstants.PROTOCOL_VERSION_21;
import static com.kodiak.xdms.server.common.resources.KnConstants.PROTOCOL_VERSION_27;
import static com.kodiak.xdms.server.common.resources.KnConstants.PROTOCOL_VERSION_29;


/**
 * ************************************************************************
 * <p/>
 * File name:  KnPubMCPTTController.java
 * Subsystem:  pubmgmt
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * Puneet Singhania        June 25, 2019           9.1.1
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
 * ************************************************************************
 */
public class KnPubMCPTTController implements IPubMCPTTController {
    private static final KnLogger knLogger = KnLogger.getLogger(KnPubMCPTTController.class);


    private static final String CLASSNAME = KnPubMCPTTController.class.getName();
    private KnValidatorFramework validatorFwk = null;
    private KnAASFramework authorizationFwk = null;
    private KnPubInfoUtil pubInfoUtil = new KnPubInfoUtil();
    private static KnGenInfoUtil genInfoUtil = KnGenInfoUtil.getInstance();
    private static KnXMLProcessor xmlProcessor = KnXMLProcessor.getInstance();
    private KnGeneralUtil generalUtil = new KnGeneralUtil();
    int clusterId = 0;


    /**
     *
     */
    public KnPubMCPTTController() {//throws KnFWException {
        validatorFwk = KnValidatorFramework.getInstance(KnConstants.LIBRARY_NAME_PUB_MGMT);
        authorizationFwk = KnAASFramework.getInstance(KnConstants.LIBRARY_NAME_PUB_MGMT);
        clusterId = Integer.parseInt(System.getenv(KnConstants.CLUSTERID_ENV_NAME));
    }

    /**
     * Interface to retrieve the dynamic group details
     *
     * @param ipmcsdto
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    @Override
    public KnMCSXCAPRespDTO getMCPTTUEConfig(KnIPMCSDTO ipmcsdto, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getMCPTTUEConfig(KnIPMCSDTO, persisterTxn)";
        KnPubMCSXCAPPersistDTO pubMCSXCAPPersistDTO = null;
        KnMCSXCAPRespDTO respDTO = new KnMCSXCAPRespDTO();
        KnXDMMcPttUEConfigRespDTO result = new KnXDMMcPttUEConfigRespDTO();
        Integer maxSimDynSessionValue = null;
        Integer maxSimDedSessionValue = null;
        KnCorpProfileDTO subsProfileCorp = null;
        String corpFS2 = null;
        knLogger.debug(methodName, "--->ENTRY -> Input DTO Passed : " + ipmcsdto);

        try {
            String mcId = ipmcsdto.getMcId();
            knLogger.debug(methodName, "--->ipmcsdto.getMCId() : " + KnGDPRTemplate.mcId(mcId));

            /*KnSubsProfileDTO subsProfilePublic = pubInfoUtil.getProfileDetails(mcId,
                    KnProfileTypes.PUBLIC_PROFILE, true, persisterTxn);*/

            KnSubsProfileDTO subsProfilePublic = pubInfoUtil.getProfileDetailsByMcIDAndUpmIndex(mcId, "0", persisterTxn);

            knLogger.debug(methodName, "--->subsProfilePublic : " + subsProfilePublic);
            Map<String, String> microSvcCommonMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
            //conditions added for cri and non-cri client where user agent is not available in request
            if (KnConstants.MCSCOMPLIANCE == subsProfilePublic.getMcpttCompliance() &&
                    (null == ipmcsdto.getUserAgent() || ipmcsdto.getUserAgent().isEmpty())) {
                knLogger.debug(methodName, "Cri client(MCSCOMPLIANCE is 1) without user agent   ", ipmcsdto.getUserAgent());
                //check from db else retrive from default config
                if (null != subsProfilePublic.getUserAgent()) {
                    ipmcsdto.setUserAgent(subsProfilePublic.getUserAgent());
                } else {
                    ipmcsdto.setUserAgent(getDefaultUAByMicroserviceConfig(microSvcCommonMap));
                }
            } else if (KnConstants.MCSCOMPLIANCE != subsProfilePublic.getMcpttCompliance() &&
                    (null == ipmcsdto.getUserAgent() || ipmcsdto.getUserAgent().isEmpty())) {
                knLogger.error(methodName, "Non cri client(MCSCOMPLIANCE is 0) without user agent");
                throw new KnPubBOException(KnErrorCodes.Validator.USER_AGENT_NOT_AVAILABLE,
                        "User Agent not available for non cri client ");
            }
            knLogger.debug(methodName, "--->subsProfilePublic : " + subsProfilePublic);
            String pv = KnGeneralUtil.validateAndFetchDetailsFromUA(ipmcsdto.getUserAgent());
            int protocolVersion = pv != null ? Integer.parseInt(pv) : 0;
            pubMCSXCAPPersistDTO = new KnPubMCSXCAPPersistDTO();
            pubMCSXCAPPersistDTO.setInputDTO(ipmcsdto);
            pubMCSXCAPPersistDTO.setOperationType(KnOperationTypes.GET_MCPTT_UE_CONFIG);
            pubMCSXCAPPersistDTO.setMdn(subsProfilePublic.getMdn());
            pubMCSXCAPPersistDTO.setProtocolVersion(protocolVersion);
            pubMCSXCAPPersistDTO.setServiceAuthStatus(subsProfilePublic.getServiceAuthStatus());

            KnSubscriberPersistDTO originator = new KnSubscriberPersistDTO();
            originator.setMdn(subsProfilePublic.getMdn());
            originator.setMcpttID(subsProfilePublic.getMcpttId());
            originator.setNetworkName(subsProfilePublic.getNetworkName());
            originator.setMcpttCompliance(subsProfilePublic.getMcpttCompliance());
            originator.setPubSubscriptionType(subsProfilePublic.getPublicSubscriptionType());
            originator.setServiceAuthStatus(subsProfilePublic.getServiceAuthStatus());
            pubMCSXCAPPersistDTO.setPersistenceDTO(originator);

            knLogger.debug(methodName, "Invoking Authorization.");
            // 5. Authorizing the subscriber
            authorizationFwk.authorize(pubMCSXCAPPersistDTO);
            knLogger.debug(methodName, "Authorized successfully.");

            knLogger.debug(methodName, "--->Invoking validation - ", pubMCSXCAPPersistDTO);
            validatorFwk.validate(pubMCSXCAPPersistDTO);
            knLogger.debug(methodName, "--->Validation completed ");

            /*KnCorpProfileDTO subsProfileCorp = pubInfoUtil.getProfileDetails(String.valueOf(subsProfilePublic.getCorpId()),
                    KnProfileTypes.CORP_PROFILE, true, persisterTxn);*/
            IXDMServerDAO xdmDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).
                    createXDMServerDAO(genInfoUtil.retrieveLocalXDMPttServerId());
            if (subsProfilePublic.getCorpSubscriptionType() == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
                subsProfileCorp = xdmDAO.getCorporateProfile(String.valueOf(subsProfilePublic.getCorpId()), persisterTxn);
                if (subsProfileCorp != null) {
                    corpFS2 = subsProfileCorp.getCorpFS2();
                }
            }
            KnSubsProfileDTO subsProfileDTO = generalUtil.prepareUserProfileChanges(subsProfilePublic, corpFS2, ipmcsdto.getUserAgent(), protocolVersion,  ipmcsdto.getClientFS2());
            if(subsProfileDTO.isActiveFSUpdated()){
                xdmDAO.updateSubscriberProfile(subsProfileDTO, persisterTxn);
                respDTO.setSubsProfileDTO(subsProfilePublic);
            }
            String xdmServerId = subsProfilePublic.getXdmsHome();
            String mcsDomainName = microSvcCommonMap.get(MICROSERVICES_COMMON_CONFIG.MCS_DOMAIN_NAME.value());

            //knLogger.debug(methodName, "--->mcsDomainName : " + mcsDomainName);

            Map<String, String> msSvcConfigDocMap = genInfoUtil.retrieveMSSvcsServiceConfig(clusterId, persisterTxn);

            knLogger.debug(methodName, "--->msSvcConfigDocMap : " + msSvcConfigDocMap);
            String mcptt_ue_config_name = msSvcConfigDocMap.get(MICROSERVICES_SERVICE_CONFIG.MCPTT_UE_CONFIG_NAME.value());

            knLogger.debug(methodName, "--->mcptt_ue_config_name : " + mcptt_ue_config_name);
            //getting featurebit enable/disable from DG.XDMS_SVC_CONFIG:MULTI_SIM_SESSION starts
            KnXDMSServiceConfigDTO xdmsServiceConfigDTO = new KnXDMSServiceConfigDTO();
            xdmsServiceConfigDTO = genInfoUtil.retrieveXDMSServiceConfig(
                    genInfoUtil.retrieveLocalXDMPttServerId(), persisterTxn);

            knLogger.debug(methodName, "--->xdmsServiceConfigDTO : " + xdmsServiceConfigDTO);

            int multiSimSession = xdmsServiceConfigDTO.getMultiSimSession();
            if (multiSimSession == KnConstants.ZERO) {
                knLogger.debug(methodName, "--->if multiSimSession== : " + multiSimSession + " then setting maxsimdynsession=1 and maxsimdedsession=1");
                maxSimDynSessionValue = 1;
                maxSimDedSessionValue = 1;
                result.setMaxSimDynSession(1);
                result.setMaxSimDedSession(1);
            } else {
                knLogger.debug(methodName, "--->if multiSimSession== : " + multiSimSession + " then querying the values of maxsimdynsession and maxsimdedsession");
                //MAXSIMULDYNAMICSESSION value obtain
                Integer maxSimDynSession = subsProfilePublic.getMaxSDYSession();
                knLogger.debug(methodName, "--->maxSimDynSession from subsProfilePublic.getMaxSDYSession(): " + maxSimDynSession);
                maxSimDynSessionValue = maxSimDynSession;
                if (maxSimDynSession == 0 || maxSimDynSession == null) {
                    Integer corpMaxSimDynSession = null;
                    if (subsProfilePublic.getCorpSubscriptionType() == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
                        corpMaxSimDynSession = subsProfileCorp.getMaxSDYSession();
                    }
                    maxSimDynSessionValue = corpMaxSimDynSession;
                    knLogger.debug(methodName, "--->maxSimDynSession from subsProfileCorp.getMaxSDYSession(): " + corpMaxSimDynSession);
                    if (corpMaxSimDynSession == 0 || corpMaxSimDynSession == null) {
                        KnPOCSvcConfigDTO knPOCSvcConfigDTO = pubInfoUtil.retrievePOCSvcConfig(subsProfilePublic.getPocHome(), persisterTxn);
                        knLogger.debug(methodName, "--->knPOCSvcConfigDTO : " + knPOCSvcConfigDTO);
                        Integer pocMaxSimDynSession = knPOCSvcConfigDTO.getMaxSDYSession();
                        maxSimDynSessionValue = pocMaxSimDynSession;
                        knLogger.debug(methodName, "--->maxSimDynSession from knPOCSvcConfigDTO.getMaxSDYSession(): " + pocMaxSimDynSession);

                    }
                }
                //MAXSIMULDEDICATEDSESSION value obtain
                Integer maxSimDedSession = subsProfilePublic.getMaxSDDSession();
                maxSimDedSessionValue = maxSimDedSession;
                knLogger.debug(methodName, "--->maxSimDedSession from subsProfilePublic.getMaxSDDSession(): " + maxSimDedSession);
                if (maxSimDedSession == 0 || maxSimDedSession == null) {
                    Integer corpMaxSimDedSession = null;
                    if (subsProfilePublic.getCorpSubscriptionType() == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
                        corpMaxSimDedSession = subsProfileCorp.getMaxSDDSession();
                    }
                    maxSimDedSessionValue = corpMaxSimDedSession;
                    knLogger.debug(methodName, "--->maxSimDedSession from subsProfileCorp.getMaxSDDSession(): " + corpMaxSimDedSession);
                    if (corpMaxSimDedSession == 0 || corpMaxSimDedSession == null) {
                        KnPOCSvcConfigDTO knPOCSvcConfigDTO = pubInfoUtil.retrievePOCSvcConfig(subsProfilePublic.getPocHome(), persisterTxn);
                        knLogger.debug(methodName, "--->knPOCSvcConfigDTO : " + knPOCSvcConfigDTO);
                        Integer pocMaxSimDedSession = knPOCSvcConfigDTO.getMaxSDDSession();
                        maxSimDedSessionValue = pocMaxSimDedSession;
                        knLogger.debug(methodName, "--->maxSimDedSession from knPOCSvcConfigDTO.getMaxSDDSession(): " + pocMaxSimDedSession);

                    }
                }
            }

            //getting featurebit enable/disable from DG.XDMS_SVC_CONFIG:MULTI_SIM_SESSION ends

            Map<String, String> paramNameValueMap = genInfoUtil.retrieveRTXConfigValues(persisterTxn);

            knLogger.debug(methodName, "--->paramNameValueMap : " + paramNameValueMap);
            int ipPrefOnCellIntf = Integer.valueOf(paramNameValueMap.get(KnConstants.IP_PREF_ON_CELL_INTF));

            knLogger.debug(methodName, "--->ipPrefOnCellIntf : " + ipPrefOnCellIntf);

            String apnName = KnConstants.INET_APN;
            Integer apnId = genInfoUtil.getAPNId(apnName, persisterTxn);
            KnAPNProfileInfoDTO apnProfileInfoDTO = xdmDAO.retrieveAPNProfileInfo(subsProfilePublic.getPocHome(), apnId, persisterTxn);
            knLogger.debug(methodName, "KnAPNProfileInfoDTO :", apnProfileInfoDTO, "apnName ", apnName, " apnId ", apnId);
            if(apnProfileInfoDTO.getSipproxyuri() == null || apnProfileInfoDTO.getGeosipproxyuri() == null){
                knLogger.error(methodName, "SIPProxyURL must be configured, Exception occured : ");
                throw new KnPubBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "SIPProxyURL not configured");
            }
            result.setSipProxyURI(apnProfileInfoDTO.getSipproxyuri());
            result.setGeoSipProxyURI(apnProfileInfoDTO.getGeosipproxyuri());
            result.setIpPrefOnCellIntf(ipPrefOnCellIntf);
            result.setMcPttUEConfigname(mcptt_ue_config_name);
            result.setMcsDomainName(mcsDomainName);
            result.setMaxSimDynSession(maxSimDynSessionValue);
            result.setMaxSimDedSession(maxSimDedSessionValue);
            result.setEtag(subsProfilePublic.getProfileLastUpdated());
            respDTO.setPttUEConfigRespDTO(result);

            knLogger.debug(methodName, "--->responseDTO :" + respDTO);

        } catch (KnPubBOException ex) {
            knLogger.error(methodName, "PubBO Exception occured : " + ex);
            throw ex;
        } catch (KnAASException aex) {
            knLogger.error(methodName, "Authorization Exception occured :" + aex);
            throw aex;
        } catch (KnValidationException vex) {
            knLogger.error(methodName, "Validation Exception occured :" + vex);
            throw new KnXDMServerException(vex.getErrorCode(), vex.getErrorMessage());
        } catch (Exception ex) {
            knLogger.error(methodName, "Exception occured  :" + ex);
            throw new KnSystemException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Unexpected exception occured  :", ex);
        }
        return respDTO;
    }


    @Override
    public KnMCSXCAPRespDTO getMCPTTUserProfile(KnIPMCSDTO ipmcsdto, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getMCPTTUserProfile(KnIPMCSDTO, persisterTxn)";
        KnPubMCSXCAPPersistDTO pubMCSXCAPPersistDTO = null;
        KnMCSXCAPRespDTO respDTO = new KnMCSXCAPRespDTO();
        KnXDMMcPttUserProfileRespDTO result = new KnXDMMcPttUserProfileRespDTO();
        Boolean campModeCap = Boolean.FALSE;
        // Integer maxSimDynSessionValue=null;
        Integer maxSimultaneousCallsN6 = null;
        Integer maxSimDedSessionValue = null;
        KnCorpProfileDTO subsProfileCorp = null;
        String corpFS2 = null;
        knLogger.debug(methodName, "--->ENTRY -> Input DTO Passed : " + ipmcsdto);

        try {
            String mcId = ipmcsdto.getMcId();
            knLogger.debug(methodName, "--->ipmcsdto.getMCId() : " + KnGDPRTemplate.mcId(mcId));

            //Doing validation for accesstoken on base MDN.
            KnSubsProfileDTO baseMdn = pubInfoUtil.getProfileDetailsByMcIDAndUpmIndex(mcId, "0",
                    persisterTxn);
            Map<String, String> microSvcCommonMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
            boolean isDefaultUASet = false; //to add defaultClientfs
            if (KnConstants.MCSCOMPLIANCE == baseMdn.getMcpttCompliance() &&
                    (null == ipmcsdto.getUserAgent() || ipmcsdto.getUserAgent().isEmpty())) {
                knLogger.debug(methodName, "Cri client(MCSCOMPLIANCE is 1) without user agent   ", ipmcsdto.getUserAgent());
                //check from db else retrive from default config
                if (null != baseMdn.getUserAgent()) {
                    ipmcsdto.setUserAgent(baseMdn.getUserAgent());
                    isDefaultUASet = true;
                } else {
                    ipmcsdto.setUserAgent(getDefaultUAByMicroserviceConfig(microSvcCommonMap));
                    isDefaultUASet = true;
                }
            } else if (KnConstants.MCSCOMPLIANCE != baseMdn.getMcpttCompliance() &&
                    (null == ipmcsdto.getUserAgent() || ipmcsdto.getUserAgent().isEmpty())) {
                knLogger.error(methodName, "Non cri client(MCSCOMPLIANCE is 0) with out user agent");
                throw new KnPubBOException(KnErrorCodes.Validator.USER_AGENT_NOT_AVAILABLE,
                        "User Agent not available for non cri client ");
            }
            int mcpttCompliance = baseMdn.getMcpttCompliance();
            String pv = KnGeneralUtil.validateAndFetchDetailsFromUA(ipmcsdto.getUserAgent());
            int protocolVersion = pv != null ? Integer.parseInt(pv) : 0;
            //default ua is not available in subscriber provisioning hence fetching default clientfs from mcs client info
            if (isDefaultUASet) {
                Map<Integer, KnDefaultMCSClientInfo> criClientConfig = KnGeneralCacheUtil.getInstance().retrieveMCSClientInfo();
                knLogger.debug(methodName, "criClientConfig", criClientConfig);
                if (null != criClientConfig.get(protocolVersion)) {
                    ipmcsdto.setClientFS2(criClientConfig.get(protocolVersion).getClientFS2());
                    knLogger.debug(methodName, "isDefaultUASet", isDefaultUASet, "clientFs2", ipmcsdto.getClientFS2());
                }
            }
            pubMCSXCAPPersistDTO = new KnPubMCSXCAPPersistDTO();
            pubMCSXCAPPersistDTO.setInputDTO(ipmcsdto);
            pubMCSXCAPPersistDTO.setOperationType(KnOperationTypes.GET_MCPTT_USER_PROFILE);
            pubMCSXCAPPersistDTO.setMdn(baseMdn.getMdn());
            pubMCSXCAPPersistDTO.setProtocolVersion(protocolVersion);
            pubMCSXCAPPersistDTO.setServiceAuthStatus(baseMdn.getServiceAuthStatus());
            KnSubscriberPersistDTO originator = new KnSubscriberPersistDTO();
            originator.setMdn(baseMdn.getMdn());
            originator.setMcpttID(baseMdn.getMcpttId());
            originator.setMcpttCompliance(mcpttCompliance);
            originator.setNetworkName(baseMdn.getNetworkName());
            originator.setPubSubscriptionType(baseMdn.getPublicSubscriptionType());
            originator.setServiceAuthStatus(baseMdn.getServiceAuthStatus());
            pubMCSXCAPPersistDTO.setPersistenceDTO(originator);


            knLogger.debug(methodName, "Invoking Authorization.");
            // 5. Authorizing the subscriber
            authorizationFwk.authorize(pubMCSXCAPPersistDTO);
            knLogger.debug(methodName, "Authorized successfully.");

            knLogger.debug(methodName, "--->Invoking validation - ", pubMCSXCAPPersistDTO);
            validatorFwk.validate(pubMCSXCAPPersistDTO);
            knLogger.debug(methodName, "--->Validation completed ");

            KnSubsProfileDTO subsProfilePublic = null;
            String upmIndex = "0";
            boolean isKodiakClient = (mcpttCompliance == 0);
            if (isKodiakClient) {
                //kodiak client fileName=mcdata-user-profile-0.xml
                String[] upmIdexArray = ipmcsdto.getFileName().split("[- .]+");
                upmIndex = upmIdexArray[3];
                subsProfilePublic = pubInfoUtil.getProfileDetailsByMcIDAndUpmIndex(ipmcsdto.getMcId(), upmIndex, persisterTxn);
            } else {
                //3rd party client fileName=user-profile.xml
                subsProfilePublic = pubInfoUtil.getProfileDetailsByMcIDAndUpmIndex(ipmcsdto.getMcId(), "0", persisterTxn);
            }

            int upmIndexLimit = genInfoUtil.getUpmIndexLimit(String.valueOf(subsProfilePublic.getCorpId()), persisterTxn);
            boolean isAnyProfileMdnCrossingIndexLimit = genInfoUtil.isAnyProfileMdnCrossingIndexLimit(mcId, upmIndexLimit, persisterTxn);
            if (isAnyProfileMdnCrossingIndexLimit) {
                knLogger.info(methodName, "isAnyProfileMdnCrossingIndexLimit::", isAnyProfileMdnCrossingIndexLimit);
                genInfoUtil.recoverImpactedMdns(mcId, persisterTxn);
            }

            knLogger.debug(methodName, "--->subsProfilePublic : " + subsProfilePublic);
            String reqMdn = subsProfilePublic.getMdn();

            String xdmServerId = subsProfilePublic.getXdmsHome();

            IXDMServerDAO xdmDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).
                    createXDMServerDAO(genInfoUtil.retrieveLocalXDMPttServerId());
            if (subsProfilePublic.getCorpSubscriptionType() == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
                subsProfileCorp = xdmDAO.getCorporateProfile(String.valueOf(subsProfilePublic.getCorpId()), persisterTxn);
                if (subsProfileCorp != null) {
                    corpFS2 = subsProfileCorp.getCorpFS2();
                }
            }
            String oldActiveFS2 = subsProfilePublic.getActiveFS2();
            KnSubsProfileDTO subsProfileDTO = generalUtil.prepareUserProfileChanges(subsProfilePublic, corpFS2, ipmcsdto.getUserAgent(), protocolVersion, ipmcsdto.getClientFS2());
            if(subsProfileDTO.isActiveFSUpdated()){
                xdmDAO.updateSubscriberProfile(subsProfileDTO, persisterTxn);
                respDTO.setSubsProfileDTO(subsProfilePublic);
                if(upmIndex.equals(KnConstants.BASE_MDN_INDEX)){
                    //for base mdn
                    com.kodiak.xdms.server.common.util.KnGeneralUtil.veryLargeGroupBitChanged(oldActiveFS2
                            ,subsProfileDTO.getActiveFS2()
                            ,subsProfileDTO.getMdn()
                            ,String.valueOf(subsProfilePublic.getCorpId()),false);
                }
            }
            Map<String, List<Integer>> memberGroupIdsMap = null;
            Set<KnCorpGroupListInfoDTO> mcxGroupList = new HashSet<>();
            List<Integer> mcxGroups = new ArrayList<>();
            if (subsProfilePublic.getCorpSubscriptionType() == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
                memberGroupIdsMap = xdmDAO.selectCorpGroupId(Arrays.asList(reqMdn), persisterTxn);
                knLogger.debug(methodName, " CorpGroupIds " + memberGroupIdsMap.values());
                String userProfileId = subsProfilePublic.getUserProfileId();
                knLogger.debug(methodName, " userProfileId " + userProfileId);
                if (userProfileId != null) {
                    Set<KnCorpGroupListInfoDTO> mcxGrpList = pubInfoUtil.retriveGroupProfileInfoByProfileId(userProfileId,persisterTxn);
                    knLogger.debug(methodName, "McxGrp ", mcxGrpList);
                    if (mcxGrpList != null && mcxGrpList.size() > 0) {

                    	for(KnCorpGroupListInfoDTO corGrp:mcxGrpList) {
                    		mcxGroups.add(corGrp.getGroupID());
                    	}
                        List<Integer> cpgroupIds = memberGroupIdsMap.get(reqMdn);
                        if (cpgroupIds == null) {
                            knLogger.debug(methodName, "No groups found for mdn - " + KnGDPRTemplate.mdn(reqMdn) + "hence initializing map with empty");
                            cpgroupIds = new ArrayList<Integer>();
                        }
                        knLogger.debug(methodName, " mcxGroups ", mcxGroups);
                        cpgroupIds.addAll(mcxGroups);
//mcxGroupList.addAll(mcxGrpList);
                       for (KnCorpGroupListInfoDTO corpGroupListInfoDTO : mcxGrpList) {
                           if (mcxGroups.contains(corpGroupListInfoDTO.getGroupID())) {
                                mcxGroupList.add(corpGroupListInfoDTO);
                            }
                        }
                        knLogger.debug(methodName, " mcxgroup info  " + mcxGroupList);
                        memberGroupIdsMap.put(reqMdn, cpgroupIds);
                        knLogger.debug(methodName, " CorpGroupIds " + memberGroupIdsMap.values());
                    }
                }
                if (userProfileId != null) {
                    //Adding the abdg groups of profile mdns base mdn
                    knLogger.debug(methodName, "Base mdn ", baseMdn.getMdn());
                    List<Integer> groupList = xdmDAO.getSubsAbdgGroupList(baseMdn.getMdn(), persisterTxn);
                    knLogger.debug(methodName, "grouplist ", groupList);
                    if (!groupList.isEmpty()) {
                        if (memberGroupIdsMap.get(reqMdn) != null) {
                            memberGroupIdsMap.get(reqMdn).addAll(groupList);
                        } else {
                            memberGroupIdsMap.put(reqMdn, groupList);
                        }
                    }
                }
                boolean reGroupFeatureEnable = pubInfoUtil.isReGroupFeatureEnable(subsProfilePublic.getActiveFS2());
                if (reGroupFeatureEnable) {
                    knLogger.debug(methodName, " flag enabled reGroupFeatureEnable " + reGroupFeatureEnable);
                    List<Integer> cpgroupIds = memberGroupIdsMap.get(reqMdn);
                    if(cpgroupIds==null) {
                        knLogger.debug(methodName, "No groups found for mdn - "+KnGDPRTemplate.mdn(reqMdn) +"hence initializing map with empty");
                        cpgroupIds = new ArrayList<>();
                    }
                    int corpId = subsProfilePublic.getCorpId();
                    // preConfigCpgroupIds - groupId vs Shared ? for pre config group
                    List<Integer> preConfigCpgroupIds = xdmDAO.selectOwnerPreConfigCorpGroupId(corpId, persisterTxn);
                    cpgroupIds.addAll(preConfigCpgroupIds);
                    //For shared preconfigGroupId, GET pregroupId and belong - ownerCorpId where
                    Map<Integer,List<Integer>> ownerCorpIdAndPreConfigGrps = xdmDAO.selectOwnerCorpIdAndPreConfigGrps(corpId,persisterTxn);


                    // Map<Integer, List<Integer>> groupAndSharedCorpIds = xdmDAO.selectGroupSharedCorpId(corpId, sharedPreConfigGroupIds, persisterTxn);
                    for(Map.Entry<Integer,List<Integer>> entry : ownerCorpIdAndPreConfigGrps.entrySet())
                    {
                        boolean isBelongedToGroup = pubInfoUtil.subscrBelongsToSharedPreConfGroup(entry.getKey(),List.of(corpId), reqMdn,xdmDAO, persisterTxn);
                        if(isBelongedToGroup)
                            cpgroupIds.addAll(entry.getValue());
                    }
                    if (userProfileId != null) {
                        KnCorpUserProfileDTO userProfileDto = pubInfoUtil.getUserProfileById(userProfileId);
                        List<Integer> ownedCorpPreConfGrpForProfile = xdmDAO.selectOwnerPreConfigCorpGroupId(userProfileDto.getCorporateID(), persisterTxn);
                        if(ownedCorpPreConfGrpForProfile != null && !ownedCorpPreConfGrpForProfile.isEmpty())
                             cpgroupIds.addAll(ownedCorpPreConfGrpForProfile);

                        List<Integer> sharedCorpPreConfGrpForProfile = pubInfoUtil.getUserProfileGroupIdBySharedCorpId(corpId,xdmDAO,persisterTxn);
                        if(sharedCorpPreConfGrpForProfile != null && !sharedCorpPreConfGrpForProfile.isEmpty())
                            cpgroupIds.addAll(sharedCorpPreConfGrpForProfile);
                    }
                    memberGroupIdsMap.put(reqMdn,cpgroupIds);
                }
            }
            KnSIPProxySvcConfigDTO knProxyDTO = xdmDAO.selectSIPProxySvcConfig(subsProfilePublic.getPocHome(), persisterTxn);
            String GMSFQDN = knProxyDTO.getSipProxyURI();

            knLogger.debug(methodName, "--->GMSFQDN : " + GMSFQDN);

            //String groupUri="sip:"+subsProfilePublic.getCorpId()+"."+ corpGroupId+"@"+GMSFQDN;
            //knLogger.debug(methodName, "--->groupUri : " + groupUri);

            String contactUri = "tel:+" + reqMdn;
            knLogger.debug(methodName, "--->contactUri : " + KnGDPRTemplate.mdn(contactUri));

            String xuiUri = subsProfilePublic.getMcId();
            knLogger.debug(methodName, "--->xuiUri : " + KnGDPRTemplate.mcId(xuiUri));

            Map<String, String> msSvcConfigDocMap = genInfoUtil.retrieveMSSvcsServiceConfig(clusterId, persisterTxn);

            //knLogger.debug(methodName, "--->msSvcConfigDocMap : " + msSvcConfigDocMap);
            String name = msSvcConfigDocMap.get(MICROSERVICES_SERVICE_CONFIG.MCPTT_USER_PROFILE_SETTINGS.value());

            knLogger.debug(methodName, "--->name : " + name);

            Boolean status = false;
            if (subsProfilePublic.getServiceAuthStatus() == KnConstants.ACTIVATED_MCSXCAP) {
                status = true;
            }

            knLogger.debug(methodName, "--->status : " + status + "subsProfilePublic.getServiceAuthStatus()" + subsProfilePublic.getServiceAuthStatus());
            String profileName = msSvcConfigDocMap.get(MICROSERVICES_SERVICE_CONFIG.MCPTT_USER_PROFILE_NAME.value());
            if (!upmIndex.equals("0")) {
                profileName = xdmDAO.selectUserProfileName(reqMdn, com.kodiak.common.resources.KnConstants.TRUE, persisterTxn);
                if(profileName == null) {
                    KnCorpUserProfileDTO userProfile = pubInfoUtil.getUserProfileNameByindex(subsProfilePublic.getCorpId(), Integer.parseInt(upmIndex), persisterTxn);
                    profileName = userProfile.getUserProfileName();
                    xdmDAO.updateUserProfileName(profileName,reqMdn,persisterTxn);
                }
            }

            knLogger.debug(methodName, "--->profileName : " + KnGDPRTemplate.name(profileName));

            String userAlias = subsProfilePublic.getUserId();
            knLogger.debug(methodName, "--->userAlias : " + KnGDPRTemplate.userId(userAlias));

            String activeFs = subsProfilePublic.getActiveFS2();


            String mcPttUserId = subsProfilePublic.getMcpttId();
            knLogger.debug(methodName, "--->mcPttUserId : " + KnGDPRTemplate.mcpttId(mcPttUserId));

            String mcPttUserIdDispName = subsProfilePublic.getNetworkName();
            knLogger.debug(methodName, "--->mcPttUserIdDispName : " +KnGDPRTemplate.name(mcPttUserIdDispName));

          /*  IXDMServerDAO commonXdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmServerId);
            //deleting the XDM Contact List
           Integer contactListId= commonXdmServerDAO.selectContactListIdForMdn(ownerMdn, persisterTxn);
            knLogger.debug(methodName, "--->select the Contact List for mdn - ", ownerMdn + "contactListId " + contactListId);
           */

            IPubXdmDAO xdmServerDAO = KnPubFactorySelector.getDAOFactory(
                    KnFactorySelector.DB).createXdmServerDAO(xdmServerId);
            List<KnXDMMdnInfoDTO> listOfXdmMdnInfoDTOs = new ArrayList<KnXDMMdnInfoDTO>();
            List<String> contactMdns = new ArrayList<>();
            Collection<Integer> contactListIds = xdmServerDAO.getContactListIdForMDN(reqMdn, persisterTxn);//1050 coming
            Map<Integer, Collection<KnMemberDTO>> members = xdmServerDAO.getContactListMemberInfos(contactListIds, false, persisterTxn);

            Map<String, KnSubsProfilePersistDTO> subsMap = new HashMap<>();
            List<String> mdnList = new ArrayList<>();
            if (members != null) {
                for (Collection<KnMemberDTO> memberDTOs : members.values()) {
                    for (KnMemberDTO memberDTO : memberDTOs) {
                        mdnList.add(memberDTO.getMemberMdn());
                    }
                }
                //getting additional details for mdn.
                subsMap = pubInfoUtil.getSubscriberDetails(mdnList, true, persisterTxn);
                knLogger.debug(methodName, "subsMap : ", KnGDPRTemplate.mapKeyMdn(subsMap));
            } else {
                knLogger.debug(methodName, "NO contact member found for the subscriber");
            }

            if (members != null) {
                for (Collection<KnMemberDTO> memberDTOs : members.values()) {
                    for (KnMemberDTO memberDTO : memberDTOs) {
                        KnXDMMdnInfoDTO xdmMdnInfoDTO = new KnXDMMdnInfoDTO();
                        xdmMdnInfoDTO.setMdn(memberDTO.getMemberMdn().trim());
                        xdmMdnInfoDTO.setName(memberDTO.getMemberName().trim());
                        if (subsMap.containsKey(memberDTO.getMemberMdn().trim())) {
                            xdmMdnInfoDTO.setMcpttId((subsMap.get(memberDTO.getMemberMdn()).getMcpttId()));
                            xdmMdnInfoDTO.setPrivateCallUri((subsMap.get(memberDTO.getMemberMdn()).getMcpttId()));
                        }
                        xdmMdnInfoDTO.setPrivateCallDispName(memberDTO.getMemberName().trim());
                        listOfXdmMdnInfoDTOs.add(xdmMdnInfoDTO);
                        contactMdns.add(memberDTO.getMemberMdn().trim());
                    }
                }
            } else {
                knLogger.debug(methodName, "NO contact member found for the subscriber");
            }
            knLogger.debug(methodName, "listOfXdmMdnInfoDTOs after 1st iteration ,i.e.,PrivateCallUri/dispname : ", listOfXdmMdnInfoDTOs);


            Integer corpListId = xdmServerDAO.getCorpListIdForMDN(reqMdn, persisterTxn);
            List<Integer> commonCorpListIds = new ArrayList<>();
            boolean isCommonContactEnabled = getFeatureBitValue(activeFs, FEATURE_SET.COMMON_CONTACT_LIST.value());
            if(isCommonContactEnabled) {
               commonCorpListIds =  xdmServerDAO.getCommonCorpListIdForMDN(baseMdn.getMdn(), persisterTxn);
            }
            Collection<String> memberMdnsForCorpListId = new ArrayList<String>();
           if (subsProfilePublic.getCorpSubscriptionType() == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value() && (corpListId != null
                    || (commonCorpListIds != null  && !commonCorpListIds.isEmpty()))) {
                if(corpListId != null)
                     memberMdnsForCorpListId = xdmServerDAO.getMemberMdnsForCorpListId(List.of(corpListId),
                        subsProfilePublic.getCorpId(), persisterTxn);
                if (commonCorpListIds != null && !commonCorpListIds.isEmpty()) {
                    var memberMdnsForCommonCorpListId = xdmServerDAO.getMemberMdnsForCorpListId(commonCorpListIds,
                            null, persisterTxn);

                    if (memberMdnsForCommonCorpListId != null && !memberMdnsForCommonCorpListId.isEmpty()) {
                        if (memberMdnsForCorpListId == null) memberMdnsForCorpListId = new ArrayList<>();
                        memberMdnsForCorpListId.addAll(memberMdnsForCommonCorpListId);
                    }
                }
            }
            Collection<String> privateContactsForMDN = xdmServerDAO.getPrivateContactsForMDN(
                    reqMdn, subsProfilePublic.getCorpId(), persisterTxn);
            knLogger.debug(methodName, "privateContactsForMDN : " + KnGDPRTemplate.mdnList(privateContactsForMDN));

            if(!isCommonContactEnabled) {
                //fetching only the list of common contact mdns
                List<String> allCommonContactMdns = xdmServerDAO.getCommonContactListForMdns(reqMdn, xdmServerId, persisterTxn);
                knLogger.debug(methodName, "allCommonContactMdns :-", KnGDPRTemplate.mdnList(allCommonContactMdns));
                //fetching all the Non Common contacts of the subscriber
                List<String> listOfAllContactMdns = xdmServerDAO.getNonCommonContactListForMdns(reqMdn, xdmServerId, persisterTxn);
                knLogger.debug(methodName, "listOfAllContactMdns :-", KnGDPRTemplate.mdnList(listOfAllContactMdns));
                if(allCommonContactMdns != null && !allCommonContactMdns.isEmpty()) {
                    allCommonContactMdns.removeIf(mdn -> listOfAllContactMdns != null && listOfAllContactMdns.contains(mdn));
                    knLogger.debug(methodName, "After removing commonContacts from allCommonContactMdns :-", KnGDPRTemplate.mdnList(allCommonContactMdns));
                }
                    if (!privateContactsForMDN.isEmpty()) {
                        privateContactsForMDN.removeAll(allCommonContactMdns);
                    }
            }
            if (!privateContactsForMDN.isEmpty()) {
                HashSet<String> memberMdnsForCorpList = new HashSet<String>(privateContactsForMDN);
                if (memberMdnsForCorpListId != null && !memberMdnsForCorpListId.isEmpty()) {
                    memberMdnsForCorpList.addAll(memberMdnsForCorpListId);
                    memberMdnsForCorpListId.clear();
                }
                memberMdnsForCorpListId.addAll(memberMdnsForCorpList);

            }
            if (memberMdnsForCorpListId != null && !memberMdnsForCorpListId.isEmpty()) {
                String realMdnForProfileMdn = xdmServerDAO.getRealMdnForProfileMdn(reqMdn,
                        persisterTxn);
                if (realMdnForProfileMdn != null) {
                    memberMdnsForCorpListId.remove(realMdnForProfileMdn);
                }
                memberMdnsForCorpListId.remove(reqMdn.trim());
            }
            knLogger.debug(methodName, "memberMdnsForCorpListId : " + KnGDPRTemplate.mdnList(memberMdnsForCorpListId));

            //removing stale entries in contact(MDN non exisitng in pocsubscrinfo )
            Map<String, KnSubsProfilePersistDTO> dbMemebrMdn = pubInfoUtil.getSubscriberDetails(new ArrayList<>(memberMdnsForCorpListId), true, persisterTxn);
            Set<String> dbMdnList = dbMemebrMdn.keySet();
            memberMdnsForCorpListId.retainAll(dbMdnList);

            knLogger.debug(methodName, "memberMdnsForCorpListId after removing stale: " + KnGDPRTemplate.mdnList(memberMdnsForCorpListId));

            List<KnEmgrDestinationInfoDTO> destinationInfoDTOS = xdmServerDAO.getEmergencyDestinationInfo(reqMdn, true, persisterTxn);
            List<com.kodiak.common.commdto.response.KnEmergencyMdnDTOForMCSXCAP> knEmergencyMdnDTOsList = new ArrayList<>();
            String emergLocPollTimerExtM = microSvcCommonMap.get(MICROSERVICES_COMMON_CONFIG.EMERLOCREPORTINTVL.value());
            knLogger.debug(methodName, "emergLocPollTimerExtM : " + emergLocPollTimerExtM);

            KnPocSubsAddlInfoDTO subsAddlInfo = pubInfoUtil.getSubsAddlDetails(reqMdn, persisterTxn);
            knLogger.debug(methodName, "subsAddlInfo : " + subsAddlInfo);


            Map<String, KnEmergencyMdnDTOForMCSXCAP> defaultEmergengyDetails = new HashMap<String, KnEmergencyMdnDTOForMCSXCAP>();
            if (protocolVersion >= PROTOCOL_VERSION_18_X) {
                KnEmergencyMdnDTOForMCSXCAP mdnDTO1 = new KnEmergencyMdnDTOForMCSXCAP();
                mdnDTO1.setEmergencyCallEntryInfo("LocallyDetermined");
                mdnDTO1.setType(DEFAULT_EMERGENCY_TYPE);
                String emergencyFeatureFlag = microSvcCommonMap.get(EMERGENCY_CONF_TIMER_FEATURE);
                boolean emergencyBit = Optional.ofNullable(subsProfileCorp.getXdmCorpFS2Set())
                        .map(set -> com.kodiak.common.resources.KnGeneralUtil.getFeatureBitValue(set, XDMCORPFS2_SET.EMERGENCY_CONF_TIMER_FEATURE.value()))
                        .orElse(false);
                knLogger.debug(methodName, "emergencyFeatureFlag : " + emergencyFeatureFlag, "emergencyBit : " + emergencyBit);
                if (isKodiakClient) {
                    mdnDTO1.setEmergCallorigModeExtM(subsProfilePublic.getEmergCallType());
                    if (subsAddlInfo != null)
                        mdnDTO1.setEmergOrigAlertIndExtM(subsAddlInfo.getEmergOrigIndicatorBitSet());
                    mdnDTO1.setEmergLocPollTimerExtM(emergLocPollTimerExtM);
                    if (protocolVersion >= PROTOCOL_VERSION_28 && emergencyFeatureFlag.equals("1") && emergencyBit) {
                        String emergConfTimer = null != subsProfilePublic.getEmergConfigTimer() ? subsProfilePublic.getEmergConfigTimer()
                                : microSvcCommonMap.get(DEFAULT_EMERGENCY_TIMER);
                        mdnDTO1.setEmergConfigTimerExtM(emergConfTimer);
                    }
                }
                defaultEmergengyDetails.put(reqMdn, mdnDTO1);
            }

            if (destinationInfoDTOS != null) {
                if (destinationInfoDTOS.size() == 0) {
                    KnEmergencyMdnDTOForMCSXCAP mdnDTO = new KnEmergencyMdnDTOForMCSXCAP();
                    mdnDTO.setEmergencyCallEntryInfo("LocallyDetermined");
                    mdnDTO.setType(DEFAULT_EMERGENCY_TYPE);
                    KnEmergencyMdnDTOForMCSXCAP defaultDetails = defaultEmergengyDetails.get(reqMdn);
                    if (defaultDetails != null && isKodiakClient) {
                        mdnDTO.setEmergCallorigModeExtM(defaultDetails.getEmergCallorigModeExtM());
                        mdnDTO.setEmergOrigAlertIndExtM(defaultDetails.getEmergOrigAlertIndExtM());
                        mdnDTO.setEmergLocPollTimerExtM(defaultDetails.getEmergLocPollTimerExtM());
                        mdnDTO.setEmergConfigTimerExtM(defaultDetails.getEmergConfigTimerExtM());
                    }
                    knEmergencyMdnDTOsList.add(mdnDTO);
                } else {

                    for (KnEmgrDestinationInfoDTO destinationInfoDTO : destinationInfoDTOS) {
                        KnEmergencyMdnDTOForMCSXCAP mdnDTO = new KnEmergencyMdnDTOForMCSXCAP();
                        mdnDTO.setPriority(destinationInfoDTO.getPriority());
                        mdnDTO.setType(destinationInfoDTO.getType());
                        if (destinationInfoDTO.getType() == KnConstants.CONTACT_MCSXCAP) {
                            mdnDTO.setEmergencyCallUri(com.kodiak.xdms.server.pubmgmt.resources.KnConstants.TEL_COLON_PLUS + destinationInfoDTO.getDestination());

                            mdnDTO.setPrivateEmergencyAlertUri(com.kodiak.xdms.server.pubmgmt.resources.KnConstants.TEL_COLON_PLUS + destinationInfoDTO.getDestination());

                            //get SUBSCRNAME from  DG.POCSUBSCRINFO where MDN = EMERGDEST

                            KnSubsProfileDTO subsProfilePublicForEmerg = pubInfoUtil.getProfileDetails(destinationInfoDTO.getDestination(),
                                    KnProfileTypes.PUBLIC_PROFILE, true, KnConstants.FALSE, persisterTxn);

                            //KnEmergencyInfoDTO emergencyDest = pubInfoUtil.getEmergencySubsDestInfo(destinationInfoDTO.getDestination(), persisterTxn);
                            knLogger.debug(methodName, "--->subsProfilePublicForEmerg : " + subsProfilePublicForEmerg);
                            String subscrname = subsProfilePublicForEmerg.getNetworkName();
                            knLogger.debug(methodName, "--->subscrname : " + KnGDPRTemplate.name(subscrname));

                            mdnDTO.setEmergencyCallDisplayName(subscrname);
                            mdnDTO.setPrivateEmergencyAlertDispName(subscrname);
                            //MCPTTGROUPInitiation
                            mdnDTO.setEmergencyCallEntryInfo(destinationInfoDTOS.size() > 0 ? "UsePreConfigured" : "LocallyDetermined");
                            KnEmergencyMdnDTOForMCSXCAP defaultDetails = defaultEmergengyDetails.get(reqMdn);
                            if (isKodiakClient) {
                                mdnDTO.setEmergPriorityExtM(destinationInfoDTO.getPriority());
                                if (defaultDetails != null) {
                                    mdnDTO.setEmergCallorigModeExtM(defaultDetails.getEmergCallorigModeExtM());
                                    mdnDTO.setEmergOrigAlertIndExtM(defaultDetails.getEmergOrigAlertIndExtM());
                                    mdnDTO.setEmergLocPollTimerExtM(defaultDetails.getEmergLocPollTimerExtM());
                                    mdnDTO.setEmergConfigTimerExtM(defaultDetails.getEmergConfigTimerExtM());
                                }
                            }
                            knEmergencyMdnDTOsList.add(mdnDTO);
                            knLogger.debug(methodName, "--->After for destType=1 : knEmergencyMdnDTOsList : " + knEmergencyMdnDTOsList);

                        } else if (destinationInfoDTO.getType() == KnConstants.GROUP_MCSXCAP) {
                            String emergDest = destinationInfoDTO.getDestination();
                            //String groupId=emergDest.substring(); //to be decided
                            String groupId = emergDest;//suppose
                            KnCorpGpInfoDTO groupInfo = xdmDAO.getCorpGroupInfoList(Integer.parseInt(groupId), persisterTxn);
                            String emergencyAlertUri = "sip:" + groupInfo.getCorpId() + "." + emergDest + KnConstants.AT + GMSFQDN;
                            knLogger.debug(methodName, "--->emergencyAlertUri : " + emergencyAlertUri);
                            knLogger.debug(methodName, "--->emergencyAlertDisplayName : " + groupInfo.getGroupDisplayName());
                            mdnDTO.setEmergencyAlertUri(emergencyAlertUri);
                            mdnDTO.setEmergencyAlertDisplayName(groupInfo.getGroupDisplayName());

                            mdnDTO.setEmergencyCallEntryInfo(destinationInfoDTOS.size() > 0 ? "UsePreConfigured" : "LocallyDetermined");
                            KnEmergencyMdnDTOForMCSXCAP defaultDetails = defaultEmergengyDetails.get(reqMdn);
                            if (isKodiakClient) {
                                mdnDTO.setEmergPriorityExtM(destinationInfoDTO.getPriority());
                                if (defaultDetails != null) {
                                    mdnDTO.setEmergCallorigModeExtM(defaultDetails.getEmergCallorigModeExtM());
                                    mdnDTO.setEmergOrigAlertIndExtM(defaultDetails.getEmergOrigAlertIndExtM());
                                    mdnDTO.setEmergLocPollTimerExtM(defaultDetails.getEmergLocPollTimerExtM());
                                    mdnDTO.setEmergConfigTimerExtM(defaultDetails.getEmergConfigTimerExtM());
                                }
                            }
                            knEmergencyMdnDTOsList.add(mdnDTO);
                            knLogger.debug(methodName, "--->After for destType=2 : knEmergencyMdnDTOsList : " + knEmergencyMdnDTOsList);

                        }
                    }
                }


            } else {
                knEmergencyMdnDTOsList.add(defaultEmergengyDetails.get(reqMdn));
            }

            //respDto.setMdnEntry(knEmergencyMdnDTOsList);
            if (subsProfilePublic.getCorpSubscriptionType() == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value() && memberMdnsForCorpListId != null
                    && !memberMdnsForCorpListId.isEmpty()) {
                List<KnMCPTTPermInfoDTO> tuPermInfoList = new ArrayList<>();
                List<String> auPermInfoList = new ArrayList<>();
                try {
                    tuPermInfoList = xdmServerDAO.getMCPTTPermInfo(reqMdn, false, persisterTxn);
                } catch (KnDAOException ex) {
                    knLogger.error(methodName, ex.getMessage());
                }
                auPermInfoList = xdmServerDAO.getAllAuthMdsForTarget(reqMdn, persisterTxn);
                //getting base mdn from profile or base mdn.
                List<String> tuList = tuPermInfoList.stream()
                        .map(l -> l.getTargetMdn())
                        .collect(Collectors.toList());
                List<String> baseOfProfileMdn = pubInfoUtil.getRealMdns(tuList, true, persisterTxn);
                //getting map of profile mdn of base mdn.
                Map<String, List<String>> profileOFBaseMdnMap = pubInfoUtil.getProfileMdnListByBaseMdnsList(baseOfProfileMdn, true, persisterTxn);
                Integer finalDiscreteEnabled = null;
                Long mergedPermBits = null;

                // Getting the external contact details
                Map<String,KnMemberDTO> extContactMemMap = xdmServerDAO.getExtContactListByContactMDN((List<String>)memberMdnsForCorpListId, subsProfilePublic.getCorpId(), persisterTxn);
                Set<String> addedSharedcallUris = new HashSet<>();
                for (String memberMdnCorp : memberMdnsForCorpListId) {
                    //get MC_PTTID from  DG.POCSUBSCRINFO where MDN = MEMBERMDN
                    KnXDMMdnInfoDTO xdmMdnInfoDTO = new KnXDMMdnInfoDTO();
                    KnSubsProfileDTO subsProfilePublicForCorpListId = pubInfoUtil.getProfileDetails(memberMdnCorp,
                            KnProfileTypes.PUBLIC_PROFILE, true, KnConstants.FALSE, persisterTxn);

                    knLogger.debug(methodName, "--->subsProfilePublicForCorpListId : " + subsProfilePublicForCorpListId);
                    String sharedcallUri = subsProfilePublicForCorpListId.getMcpttId();
                    knLogger.debug(methodName, "--->sharedcallUri : " + KnGDPRTemplate.mcpttId(sharedcallUri));
                    if (subsProfilePublicForCorpListId.getUserProfileId() != null || sharedcallUri == null ||
                            addedSharedcallUris.contains(sharedcallUri)) {
                        knLogger.debug(methodName, "Skipping for profileMdns and duplicate entry for sharedcallUri");
                        continue;
                    }
                    addedSharedcallUris.add(sharedcallUri);
                    xdmMdnInfoDTO.setSharedCalluri(sharedcallUri);
                    String sharedcalldispName = subsProfilePublicForCorpListId.getNetworkName();
                    xdmMdnInfoDTO.setSharedcalldispName(sharedcalldispName);
                    //if external contact then fetch the name from dg.extcorpcontact
                    if(extContactMemMap.size() !=0){
                            KnMemberDTO memberDTO = extContactMemMap.get(memberMdnCorp);
                            if(memberDTO !=null && memberDTO.getCorpId() != subsProfilePublic.getCorpId()){
                                xdmMdnInfoDTO.setSharedcalldispName(memberDTO.getMemberName());
                            }
                    }
                    knLogger.debug(methodName, "--->xdmMdnInfoDTO getSharedcalldispName : " +KnGDPRTemplate.name(xdmMdnInfoDTO.getSharedcalldispName()));
                    if (protocolVersion >= PROTOCOL_VERSION_18_X && isKodiakClient) {
                        //SharedCallURIExtM
                        List<String> tuProfileMdnList = profileOFBaseMdnMap.get(memberMdnCorp.trim());
                        BitSet finalPermBits = new BitSet(Long.SIZE);
                        //basemdn
                        KnMCPTTPermInfoDTO tuBaseMdn = tuPermInfoList.stream().filter(e -> e.getTargetMdn().equals(memberMdnCorp.trim())).findAny().orElse(null);
                        if (tuBaseMdn != null) {
                            finalPermBits.or(pubInfoUtil.convertLongToBitSet(tuBaseMdn.getPermBitset()));
                            finalDiscreteEnabled = tuBaseMdn.getDiscreetEnabled();

                            //profile mdn
                            if (tuProfileMdnList != null) {
                                for (String profileMdn : tuProfileMdnList) {
                                    KnMCPTTPermInfoDTO tuPermObj = tuPermInfoList.stream().filter(e -> e.getTargetMdn().equals(profileMdn.trim())).findAny().orElse(null);
                                    if (tuPermObj != null) {
                                        finalPermBits.or(pubInfoUtil.convertLongToBitSet(tuPermObj.getPermBitset()));
                                        finalDiscreteEnabled = finalDiscreteEnabled | tuPermObj.getDiscreetEnabled();
                                    }
                                }
                            }
                        }

                        String formattedUA = subsProfilePublicForCorpListId.getUserAgent();
                        if (subsProfilePublicForCorpListId.getUserAgent() != null) {
                            KnUserAgentDTO userAgentInfo = KnGeneralUtil.getDetailsFromUserAgent(subsProfilePublicForCorpListId.getUserAgent());
                            if (null != userAgentInfo) {
                                String osName = userAgentInfo.getOsName();
                                String[] protocol_version = userAgentInfo.getProtocolVersion().split("\\.");
                                String newProtocolVersion = null;
                                if (protocol_version != null && protocol_version[0] != null) {
                                    int newMajorPV = Integer.parseInt(protocol_version[0]);
                                    newProtocolVersion = String.valueOf(newMajorPV);
                                }
                                String deviceMake = userAgentInfo.getManufactName();
                                String deviceModel = userAgentInfo.getDeviceName();
                                formattedUA = String.format("%s;%s;%s;%s", osName, newProtocolVersion, deviceMake, deviceModel);
                            }
                        }

                        mergedPermBits = pubInfoUtil.convertBitSetToLong(finalPermBits);
                        BitSet permBitSet = convertLongToBitSet(mergedPermBits);
                        boolean ambientListener = permBitSet.get(0);
                        boolean discreetListener = permBitSet.get(1);
                        boolean userCheckPerm = permBitSet.get(2);
                        boolean userServicePerm = permBitSet.get(3);
                        boolean remoteEmergPerm = permBitSet.get(4);
                        //boolean mcVideoUnconfirmedPull = permBitSet.get(5);
                        xdmMdnInfoDTO.setSharedcallentryTypeExtM(memberMdnCorp != null ? null : "2");
                        xdmMdnInfoDTO.setSharedcallContactTypeExtM(memberMdnCorp != null ? null : "2");
                        xdmMdnInfoDTO.setSharedcallClientTypeExM(String.valueOf(subsProfilePublicForCorpListId.getClientType()));
                        xdmMdnInfoDTO.setSharedcallUaExM(formattedUA);
                        xdmMdnInfoDTO.setSharedcallIsAuthUserExM(auPermInfoList.contains(memberMdnCorp) ? 1 : 0);
                        String activeFsBasedOnPv = calculateActiveFeatureSetBasedOnPv(subsProfilePublicForCorpListId.getActiveFS2(), protocolVersion);
                        xdmMdnInfoDTO.setSharedcallActiveFSExM(activeFsBasedOnPv);
                        xdmMdnInfoDTO.setSharedcallUserCheckPermExtM(userCheckPerm ? 1 : 0);
                        xdmMdnInfoDTO.setSharedcallUserSvcPermExtM(userServicePerm ? 1 : 0);
                        xdmMdnInfoDTO.setSharedcallAmbientListPermExtM(ambientListener ? 1 : 0);
                        xdmMdnInfoDTO.setSharedcallDiscreetListPermExtM(discreetListener ? 1 : 0);
                        xdmMdnInfoDTO.setSharedcallEmergInitCancelPermExtM(remoteEmergPerm ? 1 : 0);
                        xdmMdnInfoDTO.setSharedcallUserSvcStatusExtM(subsProfilePublicForCorpListId.getServiceAuthStatusAU());
                        if (finalDiscreteEnabled != null) {
                            xdmMdnInfoDTO.setSharedcallDiscreetListStatusExtM(finalDiscreteEnabled);
                        }
                        if (subsProfilePublicForCorpListId.getCameraType() != null && protocolVersion >= KnConstants.PROTOCOL_VERSION_22) {
                            xdmMdnInfoDTO.setCameraTypeExtM(subsProfilePublicForCorpListId.getCameraType());
                        }
                        boolean mcVideoUnconfirmedPull = false;
                        mergedPermBits = pubInfoUtil.convertBitSetToLong(finalPermBits);
                        permBitSet = convertLongToBitSet(mergedPermBits);
                        mcVideoUnconfirmedPull = permBitSet.get(5);
                        xdmMdnInfoDTO.setUnConfirmedPullExtM(mcVideoUnconfirmedPull ? 1 : 0);
                    }
                    listOfXdmMdnInfoDTOs.add(xdmMdnInfoDTO);

                }
            }
            //SharedCallListExtM

            knLogger.debug(methodName, "listOfXdmMdnInfoDTOs after 2nd iteration ,i.e.,SharedCalluri/dispname : ", listOfXdmMdnInfoDTOs);

            int subsClientType = subsProfilePublic.getClientType();
            boolean isDispatchClient = (subsClientType == SUBSCRIBERS_CLIENT_TYPE.DISPATCH.value());
            isDispatchClient = isDispatchClient || (subsClientType == SUBSCRIBERS_CLIENT_TYPE.THIRDPARTYDISPATCHERCLIENT.value());

            List<String> tgssGroupList = xdmDAO.getTgssGroupExtM(reqMdn, persisterTxn);
            List<Integer> tgssGroups = new ArrayList<>(tgssGroupList.stream().map(Integer::valueOf).toList());

            String missionCriticalOrganization = null;
            if (subsProfilePublic.getCorpSubscriptionType() == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
                knLogger.debug(methodName, "--->subsProfileCorp : " + subsProfileCorp);
                missionCriticalOrganization = subsProfileCorp.getExtCorpId();
                knLogger.debug(methodName, "--->missionCriticalOrganization : " + missionCriticalOrganization);
                if (null != memberGroupIdsMap && null != memberGroupIdsMap.get(reqMdn)
                        && !memberGroupIdsMap.get(reqMdn).isEmpty()) {
                    Map<Integer, Integer> groupMemCountMap = xdmServerDAO.getCorpGrpMemCount(memberGroupIdsMap.get(reqMdn), xdmServerId, persisterTxn);
                    List<KnXDMMdnInfoDTO> tgssMDNInfoDTOList = new ArrayList<>();
                    List<KnXDMMdnInfoDTO> abdgMDNInfoDTOList = new ArrayList<>();
                    List<KnXDMMdnInfoDTO> otherMDNInfoDTOList = new ArrayList<>();
                    for (Integer corpGroupId : memberGroupIdsMap.get(reqMdn)) {
                        KnXDMMdnInfoDTO xdmMdnInfoDTO2 = new KnXDMMdnInfoDTO();
                        KnCorpGpInfoDTO groupInfo = xdmDAO.getCorpGroupInfoList(corpGroupId, persisterTxn);
                        Integer sgMdnCount = xdmDAO.getMemberCountFromMemberList(corpGroupId,persisterTxn);
                        String onNetworkMCPTTGroupInfoUri = "sip:" + groupInfo.getCorpId() + "." + corpGroupId + KnConstants.AT + GMSFQDN;
                        knLogger.debug(methodName, "--->onNetworkMCPTTGroupInfoUri : " + onNetworkMCPTTGroupInfoUri);
                        Boolean externalCorpGroup = xdmDAO.validateExtCorpGroup(subsProfilePublic.getUserProfileId(), subsProfilePublic.getCorpId(), groupInfo.getCorpId(), corpGroupId, persisterTxn);
                        if (protocolVersion >= KnConstants.PROTOCOL_VERSION_23 && isKodiakClient && externalCorpGroup) {
                            xdmMdnInfoDTO2.setIsExtCorpGroupExtM(KnConstants.EXTERNAL_SUBSCRIBER);
                        }
                        if (protocolVersion >= PROTOCOL_VERSION_27 && isDispatchClient){
                            if(tgssGroups != null && !tgssGroups.isEmpty() && tgssGroups.contains(corpGroupId)) {
                                xdmMdnInfoDTO2.setIsTgssGroupExtM(1);
                            }
                        }
                        Map<String, Boolean> groupMember = xdmDAO.getGroupMemberListWithBC(corpGroupId, persisterTxn);
                        Map<Integer, Integer> zoneChannelMap = xdmDAO.getZoneChannelMap(corpGroupId, reqMdn, persisterTxn);
                        String csvZones = zoneChannelMap.keySet().stream().map(Object::toString).collect(Collectors.joining(","));
                        String csvChannels = zoneChannelMap.values().stream().map(Object::toString).collect(Collectors.joining(","));
                        KnXDMTalkGroupInfoDTO groupPriority = xdmDAO.getGroupPriority(corpGroupId, reqMdn, persisterTxn);
                        if (mcxGroups.contains(corpGroupId)) {
                            for (KnCorpGroupListInfoDTO value : mcxGroupList) {
                                if (corpGroupId.equals(value.getGroupID())) {
                                    knLogger.debug(methodName, "---> mcx group zone info : " + corpGroupId);
                                    if (value.getGroupZone() != null && value.getGroupZone()>0)
                                        csvZones = value.getGroupZone().toString();
                                    if (value.getGroupChannel() != null && value.getGroupChannel()>0)
                                        csvChannels = value.getGroupChannel().toString();
                                    if (groupPriority.getPriority() == null && value.getGroupPriority()>0) {
                                        groupPriority.setPriority(value.getGroupPriority());
                                    }
                                }
                            }
                        }


                        knLogger.debug(methodName, "--->groupName : " + groupInfo.getGroupDisplayName());
                        //MCPTTGroupInfo
                        String onNetworkMcPttGroupInfoDispName = groupInfo.getGroupDisplayName();
                        knLogger.debug(methodName, "--->onNetworkMcPttGroupInfoDispName : " + onNetworkMcPttGroupInfoDispName);
                        xdmMdnInfoDTO2.setOnNetworkMCPTTGroupInfoUri(onNetworkMCPTTGroupInfoUri);
                        xdmMdnInfoDTO2.setOnNetworkMcPttGroupInfoDispName(onNetworkMcPttGroupInfoDispName);
                        int gpType=0;
                        int count = 0;
                        if (protocolVersion >= PROTOCOL_VERSION_18_X && isKodiakClient) {
                            //changing the group type value according to MCSXCAP ICD
                            xdmMdnInfoDTO2.setOnNetworkMcPttGroupInfoGroupTypeExtM(pubInfoUtil.mappGroupTypeToDB(Integer.parseInt(groupInfo.getGroupType())));
                            gpType = Integer.parseInt(groupInfo.getGroupType());
                            if (gpType == 2) {
                                //Mikey-Sakke :: Pushing Broadcast Groups to non-broadcasters applicable above pv 20
                                //If add GroupType if PV>=20 for non-broadcastors
                                if (!groupMember.isEmpty() && (groupMember.get(reqMdn)!=null)) {
                                    //if mdn is is broadcastor or pv version>=20 then set flag as 1 else continue
                                    if(groupMember.get(reqMdn) || protocolVersion >= PROTOCOL_VERSION_20_X){
                                        xdmMdnInfoDTO2.setOnNetworkMcPttGroupInfoGroupTypeExtM(1); //sending gptype 1(Broadcast)
                                    }else {
                                            continue;
                                    }
                                }

                            } else if (gpType == 1) {
                                xdmMdnInfoDTO2.setOnNetworkMcPttGroupInfoGroupTypeExtM(2);//sending gptype 2(Dispatch)
                            }
                            if (groupMemCountMap.get(corpGroupId) != null) {
                                count = count + groupMemCountMap.get(corpGroupId);
                            }
                            xdmMdnInfoDTO2.setOnNetworkMcPttGroupInfoGroupmemberCountExtM(count);
                            if (groupInfo.getAvatarId() != null)
                                xdmMdnInfoDTO2.setOnNetworkMcPttGroupInfoAvatarId(groupInfo.getAvatarId());
                            if (csvZones != null && !csvZones.isEmpty())
                                xdmMdnInfoDTO2.setOnNetworkMcPttGroupInfoGroupZoneExtM(csvZones);
                            if (csvChannels != null && !csvChannels.isEmpty())
                                xdmMdnInfoDTO2.setOnNetworkMcPttGroupInfoGoupChannelExtM(csvChannels);
                            if (groupPriority.getPriority() != null)
                                xdmMdnInfoDTO2.setOnNetworkMcPttGroupInfoGrouppriorityExtM(groupPriority.getPriority());
                        }

                        if(protocolVersion >= PROTOCOL_VERSION_18_X && !isKodiakClient) {
                            xdmMdnInfoDTO2.setOnNetworkMcPttGroupInfoGroupTypeExtM(pubInfoUtil.mappGroupTypeToDB(Integer.parseInt(groupInfo.getGroupType())));
                            if (groupMember != null && groupMember.get(reqMdn) != null) {
                                xdmMdnInfoDTO2.setIsBroadcasterExtM(groupMember.get(reqMdn) ? 1 : 0);
                            }
                        }

                        if (protocolVersion >= PROTOCOL_VERSION_20_X) {
                            gpType = Integer.parseInt(groupInfo.getGroupType());
                            if (gpType == 2) {
                                if (groupMember != null && groupMember.get(reqMdn) != null) {
                                    xdmMdnInfoDTO2.setIsBroadcasterExtM(groupMember.get(reqMdn) ? 1 : 0);
                                } else {
                                    xdmMdnInfoDTO2.setIsBroadcasterExtM(0);
                                }
                            }
                        }
                        if ((protocolVersion >= KnConstants.PROTOCOL_VERSION_27) && isKodiakClient) {
                            xdmMdnInfoDTO2.setIsLargeGroupExtM(groupInfo.getIsLargeGroup());
                            xdmMdnInfoDTO2.setIsMcxGroupExtM(groupInfo.getMcxGrpInd());
                        }
                        if (protocolVersion >= KnConstants.PROTOCOL_VERSION_27) {
                            if (groupInfo.getIsLargeGroup() != null && groupInfo.getIsLargeGroup() == LARGE_GROUP_INDICATOR) {
                                xdmMdnInfoDTO2.setGroupmemberListCountExtM(MEMBER_LIST_COUNT_FOR_LARGEGROUP);
                            }else if(groupInfo.getMcxGrpInd() != null && groupInfo.getMcxGrpInd() == MCX_GROUP_INDICATOR){
                                    xdmMdnInfoDTO2.setGroupmemberListCountExtM(count);
                            } else {
                                xdmMdnInfoDTO2.setGroupmemberListCountExtM(count);
                            }
                        }

                        if ((protocolVersion >= KnConstants.PROTOCOL_VERSION_27) && isKodiakClient) {
                            if ((groupInfo.getGroupCreatedBy() != null) && (KnConstants.GROUP_CREATED_BY.ABDG.value() == groupInfo.getGroupCreatedBy())) {
                                xdmMdnInfoDTO2.setOnNetworkMcPttGroupInfoIsAbdgGroupExtM(1);
                                if ((groupInfo.getGroupOwner() != null) && (!groupInfo.getGroupOwner().isEmpty())) {
                                    xdmMdnInfoDTO2.setOnNetworkMcPttGroupInfoAbdgGroupOwnerExtMUri(KnConstants.TEL_URI_TEMPLATE + groupInfo.getGroupOwner());
                                    String ownerName = xdmDAO.getSubscriberName(groupInfo.getGroupOwner(), persisterTxn);
                                    if ((ownerName != null) && (!ownerName.isEmpty())) {
                                        xdmMdnInfoDTO2.setOnNetworkMcPttGroupInfoAbdgGroupOwnerExtMDispName(ownerName);
                                    }
                                }
                            }
                        }
                        if ((protocolVersion >= KnConstants.PROTOCOL_VERSION_29) && isKodiakClient) {
                            gpType = Integer.parseInt(groupInfo.getGroupType());
                            knLogger.debug(methodName, "Group Type is ", String.valueOf(gpType));
                            if (gpType != 2 && groupInfo.getVideoPermission() != null) {
                                xdmMdnInfoDTO2.setGroupLevelVideoCallPermExtM(groupInfo.getVideoPermission());
                            }
                        }

                        if (null != xdmMdnInfoDTO2.getOnNetworkMcPttGroupInfoIsAbdgGroupExtM() && (xdmMdnInfoDTO2.getOnNetworkMcPttGroupInfoIsAbdgGroupExtM() == 1)) {
                            abdgMDNInfoDTOList.add(xdmMdnInfoDTO2);
                        } else if((null != xdmMdnInfoDTO2.getIsTgssGroupExtM()) && (xdmMdnInfoDTO2.getIsTgssGroupExtM() == 1)) {
                            tgssMDNInfoDTOList.add(xdmMdnInfoDTO2);
                        } else {
                            otherMDNInfoDTOList.add(xdmMdnInfoDTO2);
                        }
                    }

                    int numberOfGroupsToReturn = 0;
                    if (memberGroupIdsMap != null && memberGroupIdsMap.get(reqMdn) != null) {
                        numberOfGroupsToReturn = memberGroupIdsMap.get(reqMdn).size();
                        boolean isLargeAgencyDispatch = getFeatureBitValue(activeFs, FEATURE_SET.LARGE_AGENCY_DISPATCH.value());
                        if (isDispatchClient && !isLargeAgencyDispatch) {
                            String xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
                            KnXDMSServiceConfigDTO serviceConfigDTO = genInfoUtil.retrieveXDMSServiceConfig(xdmPttServerId, persisterTxn);
                            int maxCorpGroupPerSubs = serviceConfigDTO.getMaxCorpGrpsPerSubs();
                            if (numberOfGroupsToReturn > maxCorpGroupPerSubs) {
                                numberOfGroupsToReturn = maxCorpGroupPerSubs;
                            }
                        }
                    }

                    if (numberOfGroupsToReturn > 0) {
                        otherMDNInfoDTOList.sort(Comparator.comparing(KnXDMMdnInfoDTO::getOnNetworkMcPttGroupInfoDispName));
                        otherMDNInfoDTOList.addAll(0, tgssMDNInfoDTOList);
                        if (numberOfGroupsToReturn < otherMDNInfoDTOList.size()) {
                            otherMDNInfoDTOList.subList(numberOfGroupsToReturn, otherMDNInfoDTOList.size()).clear();
                        }
                        List<KnXDMMdnInfoDTO> groupList = new ArrayList<>(otherMDNInfoDTOList);
                        groupList.addAll(abdgMDNInfoDTOList);
                        if (!groupList.isEmpty()) {
                            listOfXdmMdnInfoDTOs.addAll(groupList);
                        }
                    }
                }
            }
            knLogger.debug(methodName, "listOfXdmMdnInfoDTOs after 3rd iteration ,i.e.,OnNetworkMCPTTGroupInfoUri/dispname : ", listOfXdmMdnInfoDTOs);
            boolean flag = getFeatureBitValue(subsProfilePublic.getActiveFS2(), 48);

            if (flag == KnConstants.FALSE) {
                maxSimDedSessionValue = 1;
            } else {
                //MAXSIMULDEDICATEDSESSION value obtain
                Integer maxSimDedSession = subsProfilePublic.getMaxSDDSession();
                maxSimDedSessionValue = maxSimDedSession;
                knLogger.debug(methodName, "--->maxSimDedSession from subsProfilePublic.getMaxSDDSession(): " + maxSimDedSession);
                if (maxSimDedSession == 0 || maxSimDedSession == null) {
                    Integer corpMaxSimDedSession = null;
                    if (subsProfilePublic.getCorpSubscriptionType() == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value() && subsProfileCorp != null) {
                        corpMaxSimDedSession = subsProfileCorp.getMaxSDDSession();
                    }
                    maxSimDedSessionValue = corpMaxSimDedSession;
                    knLogger.debug(methodName, "--->maxSimDedSession from subsProfileCorp.getMaxSDDSession(): " + corpMaxSimDedSession);
                    if (corpMaxSimDedSession == 0 || corpMaxSimDedSession == null) {
                        KnPOCSvcConfigDTO knPOCSvcConfigDTO = pubInfoUtil.retrievePOCSvcConfig(subsProfilePublic.getPocHome(), persisterTxn);
                        knLogger.debug(methodName, "--->knPOCSvcConfigDTO : " + knPOCSvcConfigDTO);
                        Integer pocMaxSimDedSession = knPOCSvcConfigDTO.getMaxSDDSession();
                        maxSimDedSessionValue = pocMaxSimDedSession;
                        knLogger.debug(methodName, "--->maxSimDedSession from knPOCSvcConfigDTO.getMaxSDDSession(): " + pocMaxSimDedSession);
                    }
                }
            }
            knLogger.debug(methodName, "Returning maxSimDedSessionValue :", maxSimDedSessionValue);
            /*IXDMServerDAO xdmServerDao = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmServerId);
            KnPocSubsAddlInfoDTO addlDetails = xdmServerDao.getSubsAddlDetails(reqMdn, persisterTxn)

             */
            knLogger.debug(methodName, "listOfXdmMdnInfoDTOs in the end: ", listOfXdmMdnInfoDTOs);

            result.setContactUri(contactUri);
            // result.setGroupName(groupName);
            // result.setGroupUri(groupUri);
            result.setKnEmergencyMdnDTOsList(knEmergencyMdnDTOsList);
            result.setXuiUri(xuiUri);
            result.setUserAlias(userAlias);
            if (protocolVersion >= PROTOCOL_VERSION_18_X && isKodiakClient) {
                String activeFsBasedOnPv = calculateActiveFeatureSetBasedOnPv(activeFs, protocolVersion);
                result.setActiveFS(activeFsBasedOnPv);
                result.setAliasMdn(reqMdn);
            }
            //for pre config group notify
            result.setOldActiveFS(oldActiveFS2);
            result.setCorpId(subsProfilePublic.getCorpId());
            KnTalkGrpScanModeDTO grpScanMode = genInfoUtil.getSubsTalkGrpScanMode(reqMdn, true, persisterTxn);
            if (grpScanMode != null) {
                Integer scanMode = grpScanMode.getMode();
                campModeCap = scanMode == 1;
            }
            result.setCampModeCap(campModeCap);
            if(protocolVersion >= PROTOCOL_VERSION_21&&subsAddlInfo!=null){
                result.setEmergTermAlertIndExtM(subsAddlInfo.getEmergTermAlertIndExtM());
            }
            // result.setOnNetworkMcPttGroupInfoDispName(onNetworkMcPttGroupInfoDispName);
            result.setMcPttUserIdDispName(mcPttUserIdDispName);
            result.setStatus(status);
            result.setProfileName(profileName);
            // result.setOnNetworkMCPTTGroupInfoUri(onNetworkMCPTTGroupInfoUri);
            result.setName(name);
            result.setMissionCriticalOrganization(missionCriticalOrganization);
            result.setListOfXdmMdnInfoDTOs(listOfXdmMdnInfoDTOs);
            result.setMcPttUserId(mcPttUserId);
            result.setEtag(subsProfilePublic.getProfileLastUpdated());
            result.setMaxSimDedSession(maxSimDedSessionValue);
            if (protocolVersion >= PROTOCOL_VERSION_19_X) {
                result.setAllowEmergencyGroupCall(subsProfilePublic.getEmergInitiatePermission());
                result.setAllowEmergencyPrivateCall(subsProfilePublic.getEmergInitiatePermission());
                result.setAllowActivateEmergencyAlert(subsProfilePublic.getEmergInitiatePermission());
                result.setAllowCancelEmergencyAlert(subsProfilePublic.getEmergCancelPermission());
                result.setAllowCancelGroupEmergency(subsProfilePublic.getEmergCancelPermission());
                result.setAllowCancelPrivateEmergencyCall(subsProfilePublic.getEmergCancelPermission());
            }
            if(protocolVersion >= PROTOCOL_VERSION_25){
                knLogger.debug(" Setting the userprofileIndex ",upmIndex);
                result.setUserProfileIndex(Integer.parseInt(upmIndex));
            }
            boolean pttSettingFlag = getFeatureBitValue(subsProfilePublic.getActiveFS2(), 141);
            knLogger.debug(" pttSettingFlag ",pttSettingFlag);
            if ( protocolVersion >= PROTOCOL_VERSION_29 && pttSettingFlag) {
                String templateDocId = subsAddlInfo.getPttSettingDocId();
                if (templateDocId == null) {
                    ArrayList<String> newMdnList = new ArrayList<>();
                    newMdnList.add(reqMdn);
                    List<String> realMdnForProfileMdn = genInfoUtil.getBaseMdn(newMdnList, persisterTxn);
                    if (realMdnForProfileMdn != null && !realMdnForProfileMdn.isEmpty()) {
                        KnPocSubsAddlInfoDTO subsAddlInfo1 = pubInfoUtil.getSubsAddlDetails(realMdnForProfileMdn.getFirst(), persisterTxn);
                        if (subsAddlInfo1 != null && subsAddlInfo1.getPttSettingDocId() != null) {
                            templateDocId = subsAddlInfo1.getPttSettingDocId();
                        }
                    }
                }
                templateDocId = genInfoUtil.getDefaultPTTSettingDocValue(subsProfilePublic.getCorpId(),subsProfilePublic.getHierarchyId(),templateDocId,persisterTxn);
                knLogger.debug(methodName, "started PTT Setting config doc -",templateDocId);
                Integer apnid = xdmDAO.getSubsApnId(reqMdn, persisterTxn);
                String xcapRootUri = genInfoUtil.getXCAPRootURI(apnid, persisterTxn, false);
                StringBuffer resourceListUri = new StringBuffer(100);
                knLogger.debug(methodName, "xcaprootURI - ", xcapRootUri, " ,templateDocId - " + templateDocId);
                boolean httpsEnabled = com.kodiak.common.resources.KnGeneralUtil.getFeatureBitValue(baseMdn.getActiveFS2(), HTTPSSUPPORT.value());
                if (httpsEnabled) {
                    resourceListUri.append("https://");
                } else {
                    resourceListUri.append("http://");
                }
                if (null != xcapRootUri && !xcapRootUri.trim().equals("")) {
                    resourceListUri.append(xcapRootUri);
                } else {
                    resourceListUri.append(com.kodiak.common.resources.KnConstants.XCAP_ROOT);
                }
                resourceListUri.append(CLIENTSUPPORT_XCAP_ROOT_CONTEXT);
                resourceListUri.append(CLIENTSUPPORT_PTTSETTING_DOC_CONTEXT);
                resourceListUri.append("/").append(templateDocId);
                result.setPttSettingUri(resourceListUri.toString());
                result.setPttEtag(System.currentTimeMillis());
                knLogger.debug(methodName, "PttSetting URL - ", resourceListUri.toString(),", etag: ", subsProfilePublic.getProfileLastUpdated());

            }
            respDTO.setPttUserProfileRespDTO(result);
            knLogger.debug(methodName, "--->responseDTO :" + respDTO);

        } catch (KnPubBOException ex) {
            knLogger.error(methodName, "PubBO Exception occured : ", ex);
            throw ex;
        } catch (KnAASException aex) {
            knLogger.error(methodName, "Authorization Exception occured :", aex);
            throw aex;
        } catch (KnValidationException vex) {
            knLogger.error(methodName, "Validation Exception occured :", vex);
            throw new KnXDMServerException(vex.getErrorCode(), vex.getErrorMessage());
        } catch (Exception ex) {
            knLogger.error(methodName, "Exception occured : ", ex);
            throw new KnSystemException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Unexpected exception occured :", ex);
        }
        return respDTO;
    }

    @Override
    public KnMCSXCAPRespDTO getMCPTTServiceConfig(KnIPMCSDTO ipmcsdto, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getMCPTTServiceConfig(KnIPMCSDTO, persisterTxn)";
        KnPubMCSXCAPPersistDTO pubMCSXCAPPersistDTO = null;
        KnMCSXCAPRespDTO respDTO = new KnMCSXCAPRespDTO();
        KnXDMMcPttServiceConfigRespDTO result = new KnXDMMcPttServiceConfigRespDTO();
        knLogger.debug(methodName, "--->ENTRY -> Input DTO Passed : " + ipmcsdto);
        String qpp = null;
        KnSubsProfileDTO subsProfile = null;
        KnCorpProfileDTO subsProfileCorp = null;
        String corpFS2 = null;
        try {

            IXDMServerDAO xdmDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).
                    createXDMServerDAO(genInfoUtil.retrieveLocalXDMPttServerId());

            knLogger.info(methodName, "retrieving subscriber profile");
            try {
                subsProfile = pubInfoUtil.getProfileDetails(ipmcsdto.getMcpttID(), KnProfileTypes.PUBLIC_PROFILE, persisterTxn);
            } catch (KnPubBOException ex) {
                knLogger.error(methodName, "PubBO Exception occurred while fetching profileDetailsByMDN so checking with profileDetailsByMCPTTID after exception: " + ex);
                subsProfile = pubInfoUtil.getProfileDetailsByMcpttID(ipmcsdto.getMcpttID(), persisterTxn);
            }
            Map<String, String> microSvcCommonMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
            //conditions added for cri and non-cri client where user agent is not available in request
            knLogger.debug(methodName, "mcPttCompliance ", subsProfile.getMcpttCompliance(), "userAgent ", ipmcsdto.getUserAgent());
            if (KnConstants.MCSCOMPLIANCE == subsProfile.getMcpttCompliance() &&
                    (null == ipmcsdto.getUserAgent() || ipmcsdto.getUserAgent().isEmpty())) {
                knLogger.debug(methodName, "Cri client(MCSCOMPLIANCE is 1) without user agent   ", ipmcsdto.getUserAgent());
                //check from db else retrive from default config
                if (null != subsProfile.getUserAgent()) {
                    ipmcsdto.setUserAgent(subsProfile.getUserAgent());
                } else {
                    ipmcsdto.setUserAgent(getDefaultUAByMicroserviceConfig(microSvcCommonMap));
                }
            } else if (KnConstants.MCSCOMPLIANCE != subsProfile.getMcpttCompliance() &&
                    (null == ipmcsdto.getUserAgent() || ipmcsdto.getUserAgent().isEmpty())) {
                knLogger.error(methodName, "Non cri client(MCSCOMPLIANCE is 0) without user agent");
                throw new KnPubBOException(KnErrorCodes.Validator.USER_AGENT_NOT_AVAILABLE,
                        "User Agent not available for non cri client ");
            }
            String pv = KnGeneralUtil.validateAndFetchDetailsFromUA(ipmcsdto.getUserAgent());
            int protocolVersion = pv != null ? Integer.parseInt(pv) : 0;
            pubMCSXCAPPersistDTO = new KnPubMCSXCAPPersistDTO();
            pubMCSXCAPPersistDTO.setInputDTO(ipmcsdto);
            pubMCSXCAPPersistDTO.setProtocolVersion(protocolVersion);
            knLogger.debug(methodName, "--->Invoking validation - ", pubMCSXCAPPersistDTO);
            validatorFwk.validate(pubMCSXCAPPersistDTO);
            if (subsProfile.getCorpSubscriptionType() == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
                subsProfileCorp = xdmDAO.getCorporateProfile(String.valueOf(subsProfile.getCorpId()), persisterTxn);
                if (subsProfileCorp != null) {
                    corpFS2 = subsProfileCorp.getCorpFS2();
                }
            }
            KnSubsProfileDTO subsProfileDTO = generalUtil.prepareUserProfileChanges(subsProfile, corpFS2, ipmcsdto.getUserAgent(), protocolVersion, ipmcsdto.getClientFS2());
            if(subsProfileDTO.isActiveFSUpdated()){
                xdmDAO.updateSubscriberProfile(subsProfileDTO, persisterTxn);
                respDTO.setSubsProfileDTO(subsProfile);
            }

            String domain = microSvcCommonMap.get(MICROSERVICES_COMMON_CONFIG.MCS_DOMAIN_NAME.value());

            knLogger.debug(methodName, "--->domain : " + domain);

            KnPOCSvcConfigDTO knPOCSvcConfigDTO = pubInfoUtil.retrievePOCSvcConfig(subsProfile.getPocHome(), persisterTxn); //todo:assume this value
            knLogger.debug(methodName, "--->knPOCSvcConfigDTO : " + knPOCSvcConfigDTO);

            Integer t1EndOfRtpMedia = knPOCSvcConfigDTO.getMediaIdleTimer();
            knLogger.debug(methodName, "--->t1EndOfRtpMedia : " + t1EndOfRtpMedia);

            Integer t3StopTalkingGrace = knPOCSvcConfigDTO.getMaxFloorHoldDuration();
            knLogger.debug(methodName, "--->t3StopTalkingGrace : " + t3StopTalkingGrace);

            //QPP extraction
            String apnName = genInfoUtil.getDefaultAPNName(persisterTxn);
            int QPPPackId = subsProfile.getQpppackId();
            //  int QPPPackId=0; //todo:assume
            Integer apnId = genInfoUtil.getAPNId(apnName, persisterTxn);

            Integer QPPPCRFProfileId = 0;
            if (QPPPackId != 0) {
                //SELECT QPPPCRFProfileId FROM DG.QPP_Package WHERE QPPPackId = ? AND APNID = ? AND UserMode=2 AND CallTypeId=4
                //KnQPPProfileInfoDTO qppProfile = xdmDAO.getQPPDetails(apnId, persisterTxn);
                //Integer qppPackId=qppProfile.getQpppackId();

                QPPPCRFProfileId = xdmDAO.getQPPPCRFProfileId(apnId, QPPPackId, persisterTxn);

            }
            if (QPPPackId == 0) {
                //SELECT QPPPCRFProfileId FROM DG.QPP_Package WHERE APNID = ? AND IsDefault=1 AND UserMode=2 AND CallTypeId=4

                QPPPCRFProfileId = xdmDAO.getQPPPCRFProfileIdForApnId(apnId, persisterTxn);

            }
            if (QPPPCRFProfileId != null) {
                qpp = genInfoUtil.getEmergencyResourcePriority(QPPPCRFProfileId, apnId, true, persisterTxn);
            }
            //

            String emerResPriNamespace = null;
            String resourcePriorityEmerg = null;
            String emerResPriPriority = null;
            String immPerilResPriNamespace = null;
            String immPerilResPriPriority = null;
            String normalResPriNamespace = null;
            String normalResPriPriority = null;

            if (qpp == null) {
                KnAPNProfileInfoDTO knAPNProfileInfoDTO = xdmDAO.retrieveAPNProfileInfo(genInfoUtil.retrieveLocalXDMPttServerId(), apnId, persisterTxn);

                if (knAPNProfileInfoDTO != null) {
                    resourcePriorityEmerg = knAPNProfileInfoDTO.getResourcepriorityemerg();

                    if (resourcePriorityEmerg != null) {
                        emerResPriNamespace = resourcePriorityEmerg.split("\\.")[0];
                        emerResPriPriority = resourcePriorityEmerg.split("\\.")[1];
                    }

                    String resourcePriorityNormal = knAPNProfileInfoDTO.getResourceprioritynormal();

                    if (resourcePriorityNormal != null) {
                        immPerilResPriNamespace = resourcePriorityNormal.split("\\.")[0];
                        knLogger.debug(methodName, "--->immPerilResPriNamespace : " + immPerilResPriNamespace);

                        immPerilResPriPriority = resourcePriorityNormal.split("\\.")[1];
                        knLogger.debug(methodName, "--->immPerilResPriPriority : " + immPerilResPriPriority);

                        normalResPriNamespace = resourcePriorityNormal.split("\\.")[0];
                        knLogger.debug(methodName, "--->normalResPriNamespace : " + normalResPriNamespace);

                        normalResPriPriority = resourcePriorityNormal.split("\\.")[1];
                        knLogger.debug(methodName, "--->normalResPriPriority : " + normalResPriPriority);
                    }
                }
            } else {
                emerResPriNamespace = qpp.split("\\.")[0];
                emerResPriPriority = qpp.split("\\.")[1];
                immPerilResPriNamespace = qpp.split("\\.")[0];
                immPerilResPriPriority = qpp.split("\\.")[1];
                normalResPriNamespace = qpp.split("\\.")[0];
                normalResPriPriority = qpp.split("\\.")[1];
            }

////

            pubMCSXCAPPersistDTO.setOperationType(KnOperationTypes.GET_MCPTT_SERVICE_CONFIG);
            // pubMCSXCAPPersistDTO.setMdn(ownerMdn);
            // pubMCSXCAPPersistDTO.setProtocolVersion(subsProfilePublic.getClientMajorVersion());
            // pubMCSXCAPPersistDTO.setServiceAuthStatus(subsProfilePublic.getServiceAuthStatus());

            KnSubscriberPersistDTO originator = new KnSubscriberPersistDTO();
            //originator.setMdn(ownerMdn);
            // originator.setNetworkName(subsProfilePublic.getNetworkName());
            //  originator.setPubSubscriptionType(subsProfilePublic.getPublicSubscriptionType());
            //  originator.setServiceAuthStatus(subsProfilePublic.getServiceAuthStatus());
            pubMCSXCAPPersistDTO.setPersistenceDTO(originator);

            result.setDomain(domain);
            result.setImmPerilResPriNamespace(immPerilResPriNamespace);
            result.setImmPerilResPriPriority(immPerilResPriPriority);
            result.setNormalResPriNamespace(normalResPriNamespace);
            result.setNormalResPriPriority(normalResPriPriority);
            result.setT1EndOfRtpMedia(t1EndOfRtpMedia);
            result.setT3StopTalkingGrace(t3StopTalkingGrace);
            result.setEmerResPriPriority(emerResPriPriority);
            result.setEmerResPriNamespace(emerResPriNamespace);
            result.setProtocolVersion(String.valueOf(protocolVersion));

            result.setEtag(subsProfile.getProfileLastUpdated());
            respDTO.setPttServiceConfigRespDTO(result);
            knLogger.debug(methodName, "--->responseDTO :" + respDTO);

        } catch (KnPubBOException ex) {
            knLogger.error(methodName, "PubBO Exception occured : " + ex);
            throw ex;
        } catch (KnValidationException vex) {
            knLogger.error(methodName, "Validation Exception occurred :" + vex);
            throw new KnXDMServerException(vex.getErrorCode(), vex.getErrorMessage());
        } catch (Exception ex) {
            knLogger.error(methodName, "Exception occured: " +
                    ex.getMessage());
            throw new KnSystemException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Unexpected exception occured : ", ex);
        }
        return respDTO;
    }
    private String getDefaultUAByMicroserviceConfig(Map<String, String> microServicesParamNameValueMap) {
        String userAgent = microServicesParamNameValueMap.get(CRI_DEFAULT_USER_AGENT);
        //default user agent  is not configured in microservice common config db.So below have been designed for default ua
        if (null == userAgent) {
            userAgent = "3GPP_MCX 3GPP_Rel_14.0.0 19 CR";
        }
        return userAgent;
    }

}
