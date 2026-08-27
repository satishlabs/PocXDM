/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.business.impl;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnPubMCDATAController.java
 * Subsystem:  XDMS
 * <p/>
 * Name                  Date          Release
 * --------------------  ------------  -------------------------------------
 * Shashank Tewari      28/06/2019    9.1
 * <p/>
 * <p/>
 * www.kodiaknetworks.com
 * All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */

import com.kodiak.common.commdto.common.KnUserAgentDTO;
import com.kodiak.common.commdto.common.KnXDMGroupDTO;
import com.kodiak.common.commdto.common.KnXDMMCSGroupInfoDTO;
import com.kodiak.common.commdto.common.KnXDMMemberDTO;
import com.kodiak.common.commdto.request.KnXDMTalkGroupInfoDTO;
import com.kodiak.common.ggcache.dto.KnDefaultMCSClientInfo;
import com.kodiak.common.resources.KnConstants.FEATURE_SET;
import com.kodiak.common.resources.KnGeneralCacheUtil;
import com.kodiak.xdms.server.common.dto.persistdat.KnCorpUserProfileDTO;
import com.kodiak.xdms.server.pubmgmt.dto.common.KnMCSXCAPRespDTO;
import com.kodiak.common.commdto.response.KnXDMMCDATAServiceConfigRespDTO;
import com.kodiak.common.commdto.response.KnXDMMCDATAUserProfileRespDTO;
import com.kodiak.common.commdto.response.KnXDMMCDataUEConfigRespDTO;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.exception.KnSystemException;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.KnXDMServerException;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dao.KnFactorySelector;
import com.kodiak.xdms.server.common.dao.persister.IXDMServerDAO;
import com.kodiak.xdms.server.common.dto.common.*;
import com.kodiak.xdms.server.common.dto.persistdat.KnSubscriberPersistDTO;
import com.kodiak.xdms.server.common.framework.aas.KnAASException;
import com.kodiak.xdms.server.common.framework.aas.KnAASFramework;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorFramework;
import com.kodiak.xdms.server.common.resources.KnConstants;
import com.kodiak.xdms.server.common.resources.KnProfileTypes;
import com.kodiak.xdms.server.common.util.KnGeneralUtil;
import com.kodiak.xdms.server.pubmgmt.business.IPubMCDATAController;
import com.kodiak.xdms.server.pubmgmt.business.KnPubBOException;
import com.kodiak.xdms.server.pubmgmt.business.helper.KnPubInfoUtil;
import com.kodiak.xdms.server.pubmgmt.dao.KnPubFactorySelector;
import com.kodiak.xdms.server.pubmgmt.dao.persister.IPubXdmDAO;
import com.kodiak.xdms.server.pubmgmt.dao.persister.db.tables.KnPubDBTablesRegistry;
import com.kodiak.xdms.server.pubmgmt.dao.persister.db.tables.xdm.KnContactListDAO;
import com.kodiak.xdms.server.pubmgmt.dao.persister.db.tables.xdm.KnContactListMemberDAO;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnIPMCSDTO;
import com.kodiak.xdms.server.pubmgmt.dto.common.KnMemberDTO;
import com.kodiak.xdms.server.pubmgmt.dto.persistdat.KnPubMCSXCAPPersistDTO;
import com.kodiak.xdms.server.pubmgmt.resources.KnErrorCodes;

import java.util.*;
import java.util.stream.Collectors;

import static com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME;
import static com.kodiak.common.resources.KnConstants.PROTOCOL_VERSION_25;
import static com.kodiak.common.resources.KnGeneralUtil.getFeatureBitValue;
import static com.kodiak.xdms.server.common.resources.KnConstants.*;

public class KnPubMCDATAController implements IPubMCDATAController {
    private static final KnLogger knLogger = KnLogger.getLogger(KnPubMCDATAController.class);
    private KnValidatorFramework validatorFwk = null;
    private KnAASFramework authorizationFwk = null;
    private KnPubInfoUtil pubInfoUtil = null;
    private KnGenInfoUtil genInfoUtil = null;
    KnGeneralUtil generalUtil = null;

    public KnPubMCDATAController() {
        validatorFwk = KnValidatorFramework.getInstance(KnConstants.LIBRARY_NAME_PUB_MGMT);
        authorizationFwk = KnAASFramework.getInstance(KnConstants.LIBRARY_NAME_PUB_MGMT);
        pubInfoUtil = new KnPubInfoUtil();
        genInfoUtil = KnGenInfoUtil.getInstance();
        generalUtil = new KnGeneralUtil();
    }

    @Override
    public KnMCSXCAPRespDTO getMCDataUEConfig(KnIPMCSDTO ipmcsdto, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        final String methodName = "getMCDataUEConfig(KnIPMCSDTO, persisterTxn)";
        knLogger.debug(methodName, "ENTRY -> Input DTO Passed : " + ipmcsdto);
        KnMCSXCAPRespDTO mcsxcapRespDTO = new KnMCSXCAPRespDTO();
        KnXDMMCDataUEConfigRespDTO respDto = new KnXDMMCDataUEConfigRespDTO();
        KnPubMCSXCAPPersistDTO pubMCSXCAPPersistDTO;
        KnCorpProfileDTO subsProfileCorp = null;
        String corpFS2 =null;
        try {
            knLogger.info(methodName, "retrieving subscriber profile for MCID ", KnGDPRTemplate.mcId(ipmcsdto.getMcId()));
            // KnSubsProfileDTO subsProfile = pubInfoUtil.getProfileDetails(ipmcsdto.getMcId(), ipmcsdto.getProfile(), true, persisterTxn);
            int clusterId = Integer.parseInt(System.getenv(CLUSTERID_ENV_NAME));
            Map<String, String> microServicesParamNameValueMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
            KnSubsProfileDTO subsProfile = pubInfoUtil.getProfileDetailsByMcIDAndUpmIndex(ipmcsdto.getMcId(),"0", persisterTxn);
            knLogger.debug(methodName, "mcPttCompliance ", subsProfile.getMcpttCompliance(), "userAgent ", ipmcsdto.getUserAgent());
            //to add default UA for cri client
            if (KnConstants.MCSCOMPLIANCE == subsProfile.getMcpttCompliance() &&
                    (null == ipmcsdto.getUserAgent() || ipmcsdto.getUserAgent().isEmpty())) {
                knLogger.debug(methodName, "Cri client(MCSCOMPLIANCE is 1) without user agent   ", ipmcsdto.getUserAgent());
                //check from db else retrive from default config
                if (null != subsProfile.getUserAgent()) {
                    ipmcsdto.setUserAgent(subsProfile.getUserAgent());
                } else {
                    ipmcsdto.setUserAgent(getDefaultUAByMicroserviceConfig(microServicesParamNameValueMap));
                }
            } else if (KnConstants.MCSCOMPLIANCE != subsProfile.getMcpttCompliance() &&
                    (null == ipmcsdto.getUserAgent() || ipmcsdto.getUserAgent().isEmpty())) {
                knLogger.error(methodName, "Non cri client(MCSCOMPLIANCE is 0) without user agent ");
                throw new KnPubBOException(KnErrorCodes.Validator.USER_AGENT_NOT_AVAILABLE,
                        "User Agent not available for non cri client ");
            }

            String pv = KnGeneralUtil.validateAndFetchDetailsFromUA(ipmcsdto.getUserAgent());
            int protocolVersion = pv != null ? Integer.parseInt(pv) : 0;
            KnSubscriberPersistDTO originator = new KnSubscriberPersistDTO();
            originator.setMdn(subsProfile.getMdn());
            originator.setMcpttID(subsProfile.getMcpttId());
            originator.setMcpttCompliance(subsProfile.getMcpttCompliance());
            originator.setNetworkName(subsProfile.getNetworkName());
            originator.setPubSubscriptionType(subsProfile.getPublicSubscriptionType());
            originator.setServiceAuthStatus(subsProfile.getServiceAuthStatus());
            pubMCSXCAPPersistDTO = new KnPubMCSXCAPPersistDTO();
            pubMCSXCAPPersistDTO.setInputDTO(ipmcsdto);
            pubMCSXCAPPersistDTO.setPersistenceDTO(originator);
            pubMCSXCAPPersistDTO.setProtocolVersion(protocolVersion);

            knLogger.debug(methodName, "Invoking Authorization.");
            //  Authorizing the subscriber
            authorizationFwk.authorize(pubMCSXCAPPersistDTO);
            knLogger.debug(methodName, "Authorized successfully.");
            String xdmServerId = subsProfile.getXdmsHome();
            int indexDocEtag = ipmcsdto.getIfNoneMatch();
            //Retrive MCS Doc
            //- Validate if the User has a Authorized MDN document (has an authEntry in DG.AUTHORIZATION_DOC)
            //IPubXdmDAO xdmServerDAO = KnPubFactorySelector.getDAOFactory( KnFactorySelector.DB).createXdmServerDAO(xdmServerId);
            knLogger.debug(methodName, "validating dto ", pubMCSXCAPPersistDTO);
            validatorFwk.validate(pubMCSXCAPPersistDTO);
            knLogger.debug(methodName, "Validated successfully!");
            String xdmPttId = genInfoUtil.retrieveLocalXDMPttServerId();
            IXDMServerDAO xdmDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttId);
            if (subsProfile.getCorpSubscriptionType() == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
                subsProfileCorp = xdmDAO.getCorporateProfile(String.valueOf(subsProfile.getCorpId()), persisterTxn);
                if (subsProfileCorp != null) {
                    corpFS2 = subsProfileCorp.getCorpFS2();
                }
            }
            KnSubsProfileDTO subsProfileDTO = generalUtil.prepareUserProfileChanges(subsProfile, corpFS2, ipmcsdto.getUserAgent(), protocolVersion, ipmcsdto.getClientFS2());
            if(subsProfileDTO.isActiveFSUpdated()){
                xdmDAO.updateSubscriberProfile(subsProfileDTO, persisterTxn);
                mcsxcapRespDTO.setSubsProfileDTO(subsProfile);
            }
            Map<String, String> msSvcConfigDocMap = genInfoUtil.retrieveMSSvcsServiceConfig(clusterId, persisterTxn);
            //KnCorpProfileDTO corpProfileDTO=xdmDAO.getCorporateProfile(String.valueOf(subsProfile.getCorpId()),persisterTxn);

            String mcs_domain_name = microServicesParamNameValueMap.get("MCS_DOMAIN_NAME");
            String mcdata_ue_config_name = msSvcConfigDocMap.get("MCDATA_UE_CONFIG_NAME");
            String max_Simul_SDS_Txns_Nc4 = microServicesParamNameValueMap.get("MAXSIMULSDSTXN");
            String max_Simul_FD_Txns_Nc4 = microServicesParamNameValueMap.get("MAXSIMULFDTXN");
            String max_Simul_Data_Transmissions_Nc4 = microServicesParamNameValueMap.get("MAXSIMULDATATRANS");
            String max_Data_Transmissions_In_Group_Nc5 = microServicesParamNameValueMap.get("MAXSIMULDATATRANSGRP");
            String max_Simul_Data_Receptions_Nc4 = microServicesParamNameValueMap.get("MAXSIMULDATARECEP");
            String max_Data_Receptions_In_Group_Nc5 = microServicesParamNameValueMap.get("MAXSIMULDATARECEPGRP");
            Map<String, String> rtxConfig = genInfoUtil.retrieveRTXConfigValues(persisterTxn);
            String ip_pref_on_cell_intf = rtxConfig.get("IP_PREF_ON_CELL_INTF");
            knLogger.debug(methodName, " RespDTO Here: ", respDto);
            respDto.setMdn(subsProfile.getMdn());
            respDto.setEtag(System.currentTimeMillis());
            respDto.setMcs_domain_name(mcs_domain_name);
            respDto.setMcdata_ue_config_name(mcdata_ue_config_name);
            if (KnGeneralUtil.isInteger(max_Simul_SDS_Txns_Nc4)) {
                respDto.setMaxsimulsdstxn(Integer.parseInt(max_Simul_SDS_Txns_Nc4));
            }
            if (KnGeneralUtil.isInteger(max_Simul_FD_Txns_Nc4)) {
                respDto.setMaxsimulsdstxn(Integer.parseInt(max_Simul_FD_Txns_Nc4));
            }
            if (KnGeneralUtil.isInteger(max_Simul_Data_Transmissions_Nc4)) {
                respDto.setMaxsimulsdstxn(Integer.parseInt(max_Simul_Data_Transmissions_Nc4));
            }
            if (KnGeneralUtil.isInteger(max_Data_Transmissions_In_Group_Nc5)) {
                respDto.setMaxsimulsdstxn(Integer.parseInt(max_Data_Transmissions_In_Group_Nc5));
            }
            if (KnGeneralUtil.isInteger(max_Simul_Data_Receptions_Nc4)) {
                respDto.setMaxsimulsdstxn(Integer.parseInt(max_Simul_Data_Receptions_Nc4));
            }
            if (KnGeneralUtil.isInteger(max_Data_Receptions_In_Group_Nc5)) {
                respDto.setMaxsimulsdstxn(Integer.parseInt(max_Data_Receptions_In_Group_Nc5));
            }
            if (ip_pref_on_cell_intf != null) {
                respDto.setIp_pref_on_cell_intf(Boolean.valueOf(ip_pref_on_cell_intf));
            }
            String apnName = KnConstants.INET_APN;
            Integer apnId = genInfoUtil.getAPNId(apnName, persisterTxn);
            KnAPNProfileInfoDTO apnProfileInfoDTO = xdmDAO.retrieveAPNProfileInfo(subsProfile.getPocHome(), apnId, persisterTxn);
            knLogger.debug(methodName, "KnAPNProfileInfoDTO :", apnProfileInfoDTO, "apnName ", apnName, " apnId ", apnId);
            if(apnProfileInfoDTO.getSipproxyuri() == null || apnProfileInfoDTO.getGeosipproxyuri() == null){
                knLogger.error(methodName, "SIPProxyURL must be configured, Exception occured : ");
                throw new KnPubBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "SIPProxyURL not configured");
            }
            respDto.setSipProxyURI(apnProfileInfoDTO.getSipproxyuri());
            respDto.setGeoSipProxyURI(apnProfileInfoDTO.getGeosipproxyuri());
            mcsxcapRespDTO.setDataUEConfigRespDTO(respDto);
        } catch (KnAASException aex) {
            knLogger.error(methodName, "Authorization Exception occurred :" + aex);
            throw aex;
        } catch (KnPubBOException ex) {
            knLogger.error(methodName, "PubBO Exception occurred : " + ex);
            throw ex;
        } /*catch (KnDAOException ex) {
            knLogger.error( methodName, "DAO Exception occurred : " + ex);
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new KnXDMServerException(AUTH_DOC_NOT_EXISTS, "Auth doc doesnot exists");
            } else if (ex instanceof KnDBConnectionException) {
                knLogger.error( methodName, "DAO DBConnection Exception occurred :" + ex);
                throw new KnXDMServerException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);
            }
            throw new KnXDMServerException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Persistence exception occurred : ", ex);
        } */ catch (KnValidationException vex) {
            knLogger.error(methodName, "Validation Exception occurred :" + vex);
            throw new KnXDMServerException(vex.getErrorCode(), vex.getErrorMessage());
        } catch (Exception ex) {
            knLogger.error(methodName, "Exception occurred while retrieving  info: " + ex);
            throw new KnSystemException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Unexpected exception occurred while retrieving MCDATAUEConfig info ", ex);
        }
        knLogger.debug(methodName, "ResponseDTO = " + mcsxcapRespDTO);
        return mcsxcapRespDTO;
    }

    @Override
    public KnMCSXCAPRespDTO getMCDataUserProfile(KnIPMCSDTO ipmcsdto, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        final String methodName = "getMCDataUserProfile(KnIPMCSDTO, persisterTxn)";
        knLogger.debug(methodName, "ENTRY -> Input DTO Passed : " + ipmcsdto);
        KnMCSXCAPRespDTO mcsxcapRespDTO = new KnMCSXCAPRespDTO();
        KnXDMMCDATAUserProfileRespDTO respDto = new KnXDMMCDATAUserProfileRespDTO();
        KnPubMCSXCAPPersistDTO pubMCSXCAPPersistDTO;
        Collection<KnXDMMemberDTO> res_knXDMMemberDTO_Common_one2one_userdto = new ArrayList();
        Collection<KnXDMMemberDTO> memberDTOS = new ArrayList<>();
        List<KnXDMMCSGroupInfoDTO> knXDMMCSGroupInfoDTOS = new ArrayList<>();
        List<KnMicroSvcsServiceConfig> msServiceConfigList = new ArrayList<>();
        KnCorpProfileDTO corpProfileDTO =null;
        String corpFS2 = null;
        int index = 0;
        try {
            knLogger.info(methodName, "retreiving subscriber profile for MCID ", KnGDPRTemplate.mcId(ipmcsdto.getMcId()));

            //Doing validation for accesstoken on base MDN.
            KnSubsProfileDTO baseMdn = pubInfoUtil.getProfileDetailsByMcIDAndUpmIndex(ipmcsdto.getMcId(),"0",
                    persisterTxn);
            int mcpttCompliance = baseMdn.getMcpttCompliance();
            int clusterId = Integer.parseInt(System.getenv(CLUSTERID_ENV_NAME));
            Map<String, String> microServicesParamNameValueMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
            //conditions added for non-cri client where user agent is not available in request
            boolean isDefaultUASet = false;
            if (KnConstants.MCSCOMPLIANCE == baseMdn.getMcpttCompliance() &&
                    (null == ipmcsdto.getUserAgent() || ipmcsdto.getUserAgent().isEmpty())) {
                knLogger.debug(methodName, "Cri client(MCSCOMPLIANCE is 1) without user agent   ", ipmcsdto.getUserAgent());
                //check from db else retrive from default config
                if (null != baseMdn.getUserAgent()) {
                    ipmcsdto.setUserAgent(baseMdn.getUserAgent());
                    isDefaultUASet = true;
                } else {
                    ipmcsdto.setUserAgent(getDefaultUAByMicroserviceConfig(microServicesParamNameValueMap));
                    isDefaultUASet = true;
                }
            } else if (KnConstants.MCSCOMPLIANCE != baseMdn.getMcpttCompliance() &&
                    (null == ipmcsdto.getUserAgent() || ipmcsdto.getUserAgent().isEmpty())) {
                knLogger.error(methodName, "Non cri client(MCSCOMPLIANCE is 0) without user agent");
                throw new KnPubBOException(KnErrorCodes.Validator.USER_AGENT_NOT_AVAILABLE,
                        "User Agent not available for non cri client ");
            }
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
            KnSubscriberPersistDTO originator = new KnSubscriberPersistDTO();
            originator.setMdn(baseMdn.getMdn());
            originator.setMcpttID(baseMdn.getMcpttId());
            originator.setMcpttCompliance(mcpttCompliance);
            originator.setNetworkName(baseMdn.getNetworkName());
            originator.setPubSubscriptionType(baseMdn.getPublicSubscriptionType());
            originator.setServiceAuthStatus(baseMdn.getServiceAuthStatus());
            pubMCSXCAPPersistDTO = new KnPubMCSXCAPPersistDTO();
            pubMCSXCAPPersistDTO.setInputDTO(ipmcsdto);
            pubMCSXCAPPersistDTO.setPersistenceDTO(originator);
            pubMCSXCAPPersistDTO.setProtocolVersion(protocolVersion);
            knLogger.debug(methodName, "Invoking Authorization.");
            //  Authorizing the subscriber
            authorizationFwk.authorize(pubMCSXCAPPersistDTO);
            knLogger.debug(methodName, "Authorized successfully.");
            KnSubsProfileDTO subsProfile =null;
            boolean isKodiakClient = (mcpttCompliance == 0);
            String upmIndex ="0";
            if(isKodiakClient){
                //kodiak client fileName=mcdata-user-profile-0.xml
                String[] upmIdexArray = ipmcsdto.getFileName().split("[- .]+");
                upmIndex = upmIdexArray[3];
                subsProfile = pubInfoUtil.getProfileDetailsByMcIDAndUpmIndex(ipmcsdto.getMcId(),upmIndex, persisterTxn);
            }else{
                //3rd party client fileName=user-profile.xml
                subsProfile = pubInfoUtil.getProfileDetailsByMcIDAndUpmIndex(ipmcsdto.getMcId(),upmIndex, persisterTxn);
            }

            String xdmServerId = subsProfile.getXdmsHome();
            String reqMdn = subsProfile.getMdn();
            int indexDocEtag = ipmcsdto.getIfNoneMatch();
            //Retrive MCS Doc
            //- Validate if the User has a Authorized MDN document (has an authEntry in DG.AUTHORIZATION_DOC)
            IPubXdmDAO xdmServerDAO = KnPubFactorySelector.getDAOFactory(KnFactorySelector.DB).createXdmServerDAO(xdmServerId);

            knLogger.debug(methodName, "validating dto ", pubMCSXCAPPersistDTO);
            validatorFwk.validate(pubMCSXCAPPersistDTO);
            knLogger.debug(methodName, "Validated successfully!");

            String xdmPttId = genInfoUtil.retrieveLocalXDMPttServerId();
            int corpId = subsProfile.getCorpId();
            IXDMServerDAO xdmDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttId);
            if (subsProfile.getCorpSubscriptionType() == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
                corpProfileDTO = xdmDAO.getCorporateProfile(String.valueOf(subsProfile.getCorpId()), persisterTxn);
                if (corpProfileDTO != null) {
                    corpFS2 = corpProfileDTO.getCorpFS2();
                }
            }
            String oldActiveFS2 = subsProfile.getActiveFS2();
            KnSubsProfileDTO subsProfileDTOChanged = generalUtil.prepareUserProfileChanges(subsProfile, corpFS2, ipmcsdto.getUserAgent(), protocolVersion, ipmcsdto.getClientFS2());
            if(subsProfileDTOChanged.isActiveFSUpdated()){
                xdmDAO.updateSubscriberProfile(subsProfileDTOChanged, persisterTxn);
                mcsxcapRespDTO.setSubsProfileDTO(subsProfile);
                if(upmIndex.equals(KnConstants.BASE_MDN_INDEX)){
                    //for base mdn
                    com.kodiak.xdms.server.common.util.KnGeneralUtil.veryLargeGroupBitChanged(oldActiveFS2
                            ,subsProfileDTOChanged.getActiveFS2()
                            ,subsProfileDTOChanged.getMdn()
                            ,String.valueOf(subsProfile.getCorpId()),false);
                }
            }
            Map<String, String> msSvcConfigDocMap = genInfoUtil.retrieveMSSvcsServiceConfig(clusterId, persisterTxn);
            msServiceConfigList = xdmDAO.retrieveMSSvcsServiceConfig(persisterTxn);
            KnSIPProxySvcConfigDTO knProxyDTO = xdmDAO.selectSIPProxySvcConfig(subsProfile.getPocHome(), persisterTxn);
            String GMSFQDN = knProxyDTO.getSipProxyURI();
            int serviceAuthStatus = subsProfile.getServiceAuthStatus();
            boolean status = false;
            if (serviceAuthStatus == 2) {
                status = true;
            }
            respDto.setMdn(reqMdn);
            respDto.setEtag(System.currentTimeMillis());
            respDto.setXui_uri(subsProfile.getMcId());
            respDto.setName(msSvcConfigDocMap.get("MCDATA_USER_PROFILE_SETTINGS"));
            respDto.setStatus(status);
            respDto.setProfileName(msSvcConfigDocMap.get("MCDATA_USER_PROFILE_NAME"));
            respDto.setCommon_useralias_name(subsProfile.getUserId());
            if (protocolVersion >= PROTOCOL_VERSION_18_X&&isKodiakClient) {
                respDto.setActiveFS(subsProfile.getActiveFS2());
                respDto.setAliasMdn(reqMdn);
            }
            respDto.setOldActiveFS(oldActiveFS2);
            respDto.setCorpId(corpId);
            KnXDMMemberDTO common_mcdata_userdto = new KnXDMMemberDTO();
            common_mcdata_userdto.setUri(subsProfile.getMcDataId());
            common_mcdata_userdto.setDisplayName(subsProfile.getNetworkName());
            respDto.setCommon_mcdata_userdto(common_mcdata_userdto);
            if (corpProfileDTO != null && corpProfileDTO.getExtCorpId() != null) {
                respDto.setCommon_mco_name(corpProfileDTO.getExtCorpId().trim());
            }
            KnXDMMemberDTO common_fd_userdto = new KnXDMMemberDTO();
            respDto.setCommon_fd_userdto(common_fd_userdto);
            knLogger.debug(methodName, " RespDTO Here: ", respDto);
            if (msServiceConfigList != null && !msServiceConfigList.isEmpty()) {
                for (KnMicroSvcsServiceConfig cfgDTO : msServiceConfigList) {
                    if (clusterId == cfgDTO.getClusterId()) {
                        if (cfgDTO.getServiceType() != null && cfgDTO.getParamName() != null) {
                            if (cfgDTO.getServiceType().trim().equals("MCDATA") && cfgDTO.getParamName().equals("MAX_PAYLOAD_SIZE_SDS_CPLANE_BYTES")) {
                                knLogger.debug(methodName, " cfgDTO.getServiceType()- ", cfgDTO.getServiceType());
                                respDto.setCommon_trxcontrol_maxdata1to1(cfgDTO.getParamvalue());
                            }
                        }
                    }
                }
            }
            knLogger.debug(methodName, " MyUserProfile Here: ", respDto);
            KnXDMGroupDTO common_grpemergalert_userdto = new KnXDMGroupDTO();
            respDto.setCommon_grpemergalert_userdto(common_grpemergalert_userdto);

            //Pass the owner mdn to get the contactlistID
            // Pass the contactListIds to get the listof Member MDNs
            //For each member MDN  get the subsprofile and increment the index value of KnMember DTO
            knLogger.debug(methodName, " RespDTO Here  : ", respDto);
            index = 0;

            KnContactListDAO contactListDAO = KnPubDBTablesRegistry.getDBXdmTableRegistry().getContactListDAO(xdmPttId);
            Collection<Integer> contactListIds = contactListDAO.getContactListIdsForMdn(reqMdn, true, persisterTxn);
            if (contactListIds != null && !contactListIds.isEmpty()) {
                KnContactListMemberDAO contactMemDAO = KnPubDBTablesRegistry.getDBXdmTableRegistry().getContactListMemberDAO(xdmPttId);
                Map<Integer, Collection<KnMemberDTO>> map_for_membersContactIDAndBean = contactMemDAO.getMembersForContactIds(contactListIds, true, persisterTxn); ////Put if Condition
                if (map_for_membersContactIDAndBean != null && !map_for_membersContactIDAndBean.isEmpty()) {
                    for (Collection<KnMemberDTO> collection_knMemberDTO : map_for_membersContactIDAndBean.values()) {
                        for (KnMemberDTO knMemberDTO : collection_knMemberDTO) {
                            KnSubsProfileDTO subsProfileDTO = pubInfoUtil.getProfileDetails(knMemberDTO.getMemberMdn(), ipmcsdto.getProfile(), true, KnConstants.FALSE, persisterTxn);
                            KnXDMMemberDTO common_one2one_memberDTO = new KnXDMMemberDTO();
                            common_one2one_memberDTO.setUri(subsProfileDTO.getMcDataId());
                            common_one2one_memberDTO.setDisplayName(knMemberDTO.getMemberName());
                            common_one2one_memberDTO.setUfmi(String.valueOf(index++));
                            res_knXDMMemberDTO_Common_one2one_userdto.add(common_one2one_memberDTO);
                        }
                    }
                }
            }
            knLogger.debug(methodName, " RespDTO Here  res_knXDMMemberDTO_Common_one2one_userdto :   ", res_knXDMMemberDTO_Common_one2one_userdto);
            respDto.setCommon_one2one_userdto(res_knXDMMemberDTO_Common_one2one_userdto);
            knLogger.debug(methodName, " RespDTO Here   :   ", respDto);
            KnXDMMemberDTO common_one2one_kmsuri_userdto = new KnXDMMemberDTO();
            respDto.setCommon_one2one_kmsuri_userdto(common_one2one_kmsuri_userdto);

            if (subsProfile.getCorpSubscriptionType() == 1) {
                Integer corpListId = xdmServerDAO.getCorpListIdForMDN(reqMdn, persisterTxn);
                List<Integer> commonCorpListIds = new ArrayList<>();
                boolean isCommonContactEnabled = getFeatureBitValue(subsProfile.getActiveFS2(), FEATURE_SET.COMMON_CONTACT_LIST.value());
                if(isCommonContactEnabled) {
                    commonCorpListIds =  xdmServerDAO.getCommonCorpListIdForMDN(baseMdn.getMdn(), persisterTxn);
                }
                //SELECT CLI.CORPLISTID FROM DG.CORPLISTINFO CLI, DG.CORPLISTDISTINFO CDI WHERE CDI.RECIPIENTMDN = ? AND CLI.CORPLISTID=CDI.CORPLISTID AND CLI.LISTDISTRIBUTIONPOLICY=3;
                //todo:
                //Add  DAO Exception - , com.kodiak.common.dao.KnDAOException: Error Code : DAO.KNEC-CM10452, Error Message : No Contact Info found.
                //in errorcode xml . It is coming if corpListId is null and we pass null to getMemberMdnsForCorpListId . So added null check below
                index = 0;
                ArrayList<String> memberMdnsForCorpListId = null;

             if ((corpListId != null) || (commonCorpListIds != null  && !commonCorpListIds.isEmpty()) ) {
                 if(corpListId != null)
                    memberMdnsForCorpListId = (ArrayList<String>) xdmServerDAO.getMemberMdnsForCorpListId(List.of(corpListId), subsProfile.getCorpId(), persisterTxn);
                    //Error Code : DAO.KNEC-CM10452, Error Message : No Contact Info found. Query ->SELECT MEMBERMDN FROM DG.CORPLISTMEMBER WHERE CORPLISTID=? AND MEMBERCORPID=?;, Cause :
                    //corpid =9 but in DB there was value for 6. So updated from 6 to 9
                    if (commonCorpListIds != null  && !commonCorpListIds.isEmpty()) {
                        var memberMdnsForCommonCorpListId = xdmServerDAO.getMemberMdnsForCorpListId(commonCorpListIds,
                                null, persisterTxn);

                        if (memberMdnsForCommonCorpListId != null && !memberMdnsForCommonCorpListId.isEmpty()) {
                            if (memberMdnsForCorpListId == null) memberMdnsForCorpListId = new ArrayList<>();
                            memberMdnsForCorpListId.addAll(memberMdnsForCommonCorpListId);
                        }
                    }
                }

                if (memberMdnsForCorpListId != null) {
                    List<KnSubsProfileDTO> knSubsProfileDTOS = xdmDAO.retrieveBulkSubscribersInfo(memberMdnsForCorpListId, persisterTxn);
                    if (knSubsProfileDTOS != null) {
                        for (KnSubsProfileDTO knSubsProfileDTO : knSubsProfileDTOS) {
                            KnXDMMemberDTO corpMemDTO = new KnXDMMemberDTO();
                            corpMemDTO.setUri(knSubsProfileDTO.getMcDataId());
                            corpMemDTO.setDisplayName(knSubsProfileDTO.getSubscriberName());
                            corpMemDTO.setUfmi(String.valueOf(index++));
                            if (protocolVersion >= PROTOCOL_VERSION_18_X && isKodiakClient) {
                                String formattedUA = knSubsProfileDTO.getUserAgent();
                                if (knSubsProfileDTO.getUserAgent() != null) {
                                    KnUserAgentDTO userAgentInfo = KnGeneralUtil.getDetailsFromUserAgent(knSubsProfileDTO.getUserAgent());
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
                                corpMemDTO.setEntryType(String.valueOf(knSubsProfileDTO.getClientType()));
                                corpMemDTO.setContactType(knSubsProfileDTO.getMdn().trim());
                                corpMemDTO.setClientType(String.valueOf(knSubsProfileDTO.getClientType()));
                                corpMemDTO.setUserAgent(formattedUA);
                                corpMemDTO.setActiveFS(knSubsProfileDTO.getActiveFS2());
                            }
                            if (protocolVersion >= PROTOCOL_VERSION_22 && isKodiakClient){
                                corpMemDTO.setCameraType(knSubsProfileDTO.getCameraType());
                             }
                            memberDTOS.add(corpMemDTO);
                        }
                    }
                }
            }
            knLogger.debug(methodName, " RespDTO Here   memberDTOS   :  ", memberDTOS);
            knLogger.debug(methodName, " RespDTO Here      :  ", respDto);
            respDto.setCommon_one2one_anyext_userdto(memberDTOS);
            KnXDMMemberDTO common_one2one_anyext_kmsuri_userdto = new KnXDMMemberDTO();
            respDto.setCommon_one2one_anyext_kmsuri_userdto(common_one2one_anyext_kmsuri_userdto);

            // write Code for Onnetwork MCGroup


            knLogger.debug(methodName, "subsProfile.getCorpSubscriptionType() " + subsProfile.getCorpSubscriptionType() + "subsProfile.getCorpId() " + subsProfile.getCorpId());
            Map<String, List<Integer>> memberGroupIdsMap = null;
            Set<KnCorpGroupListInfoDTO> mcxGroupList = new HashSet<>();
            List<Integer> mcxGroups = new ArrayList<>();
            String userProfileId = subsProfile.getUserProfileId();
            List<Integer> preConfGrps = new ArrayList<>();
            if (subsProfile.getCorpSubscriptionType() == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
                memberGroupIdsMap = xdmDAO.selectCorpGroupId(Arrays.asList(reqMdn), persisterTxn);
                knLogger.debug(methodName, " CorpGroupIds " + memberGroupIdsMap.values());
                knLogger.debug(methodName, " userProfileId " + userProfileId);
                if(userProfileId!=null){
                	Set<KnCorpGroupListInfoDTO> mcxGrpList = pubInfoUtil.retriveGroupProfileInfoByProfileId(userProfileId,persisterTxn);
                    knLogger.debug(methodName, "mcxGroup ", mcxGrpList);
                    if (mcxGrpList != null && mcxGrpList.size() > 0) {
                         for(KnCorpGroupListInfoDTO mcxGroupdto:mcxGrpList) {
                        	 mcxGroups.add(mcxGroupdto.getGroupID());
                         }
                       
                        List<Integer> cpgroupIds = memberGroupIdsMap.get(reqMdn);
                        if(cpgroupIds==null) {
                            knLogger.debug(methodName, "No groups found for mdn - "+KnGDPRTemplate.mdn(reqMdn) +"hence initializing map with empty");
                            cpgroupIds = new ArrayList<Integer>();
                        }
                        knLogger.debug(methodName, " mcxGroups " , mcxGroups);
                        cpgroupIds.addAll(mcxGroups);
                        
                        mcxGroupList.addAll(mcxGrpList);
                        knLogger.debug(methodName, " mcxgroup info  " + mcxGroupList);
                        memberGroupIdsMap.put(reqMdn,cpgroupIds);
                        knLogger.debug(methodName, " CorpGroupIds " + memberGroupIdsMap.values());
                    }
                }

                boolean reGroupFeatureEnable = pubInfoUtil.isReGroupFeatureEnable(subsProfile.getActiveFS2());
                if (reGroupFeatureEnable) {
                    knLogger.debug(methodName, " flag enabled reGroupFeatureEnable " + reGroupFeatureEnable);
                    List<Integer> cpgroupIds = memberGroupIdsMap.get(reqMdn);
                    if(cpgroupIds==null) {
                        knLogger.debug(methodName, "No groups found for mdn - "+KnGDPRTemplate.mdn(reqMdn) +"hence initializing map with empty");
                        cpgroupIds = new ArrayList<>();
                    }
                    // preConfigCpgroupIds - groupId vs Shared ? for pre config group
                    List<Integer> preConfigCpgroupIds = xdmDAO.selectOwnerPreConfigCorpGroupId(corpId, persisterTxn);
                    if (preConfigCpgroupIds != null && !preConfigCpgroupIds.isEmpty()) {
                        preConfGrps.addAll(preConfigCpgroupIds);
                        cpgroupIds.addAll(preConfigCpgroupIds);
                    }
                    //For shared preconfigGroupId, GET pregroupId and belong - ownerCorpId where
                    Map<Integer,List<Integer>> ownerCorpIdAndPreConfigGrps = xdmDAO.selectOwnerCorpIdAndPreConfigGrps(corpId,persisterTxn);


                   // Map<Integer, List<Integer>> groupAndSharedCorpIds = xdmDAO.selectGroupSharedCorpId(corpId, sharedPreConfigGroupIds, persisterTxn);
                    for(Map.Entry<Integer,List<Integer>> entry : ownerCorpIdAndPreConfigGrps.entrySet())
                    {
                        boolean isBelongedToGroup = pubInfoUtil.subscrBelongsToSharedPreConfGroup(entry.getKey(),List.of(corpId), reqMdn,xdmDAO, persisterTxn);
                        if(isBelongedToGroup) {
                            preConfGrps.addAll(entry.getValue());
                            cpgroupIds.addAll(entry.getValue());
                        }
                    }

                   if (userProfileId != null) {
                       KnCorpUserProfileDTO userProfileDto = pubInfoUtil.getUserProfileById(userProfileId);
                       List<Integer> ownedCorpPreConfGrpForProfile = xdmDAO.selectOwnerPreConfigCorpGroupId(userProfileDto.getOwnerCorpId(), persisterTxn);
                       if(ownedCorpPreConfGrpForProfile != null && !ownedCorpPreConfGrpForProfile.isEmpty()){
                           preConfGrps.addAll(ownedCorpPreConfGrpForProfile);
                           cpgroupIds.addAll(ownedCorpPreConfGrpForProfile);
                       }

                       List<Integer> sharedCorpPreConfGrpForProfile = pubInfoUtil.getUserProfileGroupIdBySharedCorpId(userProfileDto.getOwnerCorpId(),xdmDAO,persisterTxn);
                       if(sharedCorpPreConfGrpForProfile != null && !sharedCorpPreConfGrpForProfile.isEmpty()) {
                           preConfGrps.addAll(sharedCorpPreConfGrpForProfile);
                           cpgroupIds.addAll(sharedCorpPreConfGrpForProfile);
                       }
                   }
                     memberGroupIdsMap.put(reqMdn,cpgroupIds);
                }
            }
            if (memberGroupIdsMap != null && memberGroupIdsMap.get(reqMdn) != null) {
                for (Integer corpGroupId : memberGroupIdsMap.get(reqMdn)) {
                    KnXDMMCSGroupInfoDTO knXDMMCSGroupInfoDTO = new KnXDMMCSGroupInfoDTO();
                    KnXDMMemberDTO knXDMMemberDTOGP = new KnXDMMemberDTO();
                    String groupUri = "sip:" + subsProfile.getCorpId() + "." + corpGroupId + KnConstants.AT + GMSFQDN;
                    knXDMMemberDTOGP.setUri(groupUri);
                    String groupName = xdmDAO.getGroupName(corpGroupId.toString(), persisterTxn);
                    if (groupName != null) {
                        knXDMMemberDTOGP.setDisplayName(groupName.trim());
                    }
                    knLogger.debug(methodName, " PreconfigResponseCheck" + preConfGrps);
                    if (preConfGrps.contains(corpGroupId)) {
                        knXDMMemberDTOGP.setPreConfiguredGroupUseOnly(true);
                    } else {
                        knXDMMemberDTOGP.setPreConfiguredGroupUseOnly(false);
                    }
                    knXDMMCSGroupInfoDTO.setGpID(knXDMMemberDTOGP);

                    //MCVideoGroupInfo/MCVideo-Group-ID/entry/anyExt/groupTypeExtM
                    KnXDMGroupDTO groupDTO = new KnXDMGroupDTO();
                    KnXDMTalkGroupInfoDTO talkGroupInfoDTO = new KnXDMTalkGroupInfoDTO();
                    Map<String, Boolean> groupMember = xdmDAO.getGroupMemberListWithBC(corpGroupId, persisterTxn);
                    if (protocolVersion >= PROTOCOL_VERSION_18_X&&isKodiakClient) {
                        KnCorpGpInfoDTO groupInfo = xdmDAO.getCorpGroupInfoList(corpGroupId, persisterTxn);
                        Map<Integer, Integer> zoneChannelMap = xdmDAO.getZoneChannelMap(corpGroupId, reqMdn, persisterTxn);
                        String csvZones=zoneChannelMap.keySet().stream().map(Object::toString).collect(Collectors.joining(","));
                        String csvChannels=zoneChannelMap.values().stream().map(Object::toString).collect(Collectors.joining(","));
                        KnXDMTalkGroupInfoDTO groupPriority = xdmDAO.getGroupPriority(corpGroupId, reqMdn, persisterTxn);
                        Boolean externalCorpGroup = xdmDAO.validateExtCorpGroup(subsProfile.getUserProfileId(), subsProfile.getCorpId(), groupInfo.getCorpId(), corpGroupId, persisterTxn);
                        if (protocolVersion >= PROTOCOL_VERSION_23 && isKodiakClient && externalCorpGroup) {
                            knXDMMCSGroupInfoDTO.setIsExtCorpGroupExtM(KnConstants.EXTERNAL_SUBSCRIBER);
                        }
                        if(mcxGroups.contains(corpGroupId)){
                            for (KnCorpGroupListInfoDTO value : mcxGroupList){
                                if(corpGroupId.equals(value.getGroupID())) {
                                    knLogger.debug(methodName, "---> mcx group zone info : " + corpGroupId);
                                    if(value.getGroupZone()!=null)
                                    csvZones = value.getGroupZone().toString();
                                    if(value.getGroupChannel()!=null)
                                    csvChannels = value.getGroupChannel().toString();
                                    if(groupPriority.getPriority()== null) {
                                        groupPriority.setPriority(value.getGroupPriority());
                                    }
                                }
                            }
                        }
                        groupDTO.setGroupType(Integer.parseInt(groupInfo.getGroupType()));
                        if (Integer.parseInt(groupInfo.getGroupType()) == 2) {
                            //Mikey-Sakke :: Pushing Broadcast Groups to non-broadcasters applicable above pv 20
                            //If add GroupType if PV>=20 for non-broadcastors
                            if (!groupMember.isEmpty() && (groupMember.get(reqMdn) != null)) {
                                //if mdn is is broadcastor or pv version>=20 then set flag as 1 else continue
                                if (groupMember.get(reqMdn) || protocolVersion >= PROTOCOL_VERSION_20_X) {
                                    groupDTO.setGroupType(1); //sending gptype 1(Broadcast)
                                } else {
                                    continue;
                                }
                            }

                        } else if (Integer.parseInt(groupInfo.getGroupType()) == 1) {
                            groupDTO.setGroupType(2);//sending gptype 2(Dispatch)
                        }
                        int count = 0;
                        if (groupMember != null)
                            count = count + groupMember.size();
                            groupDTO.setGroupmemberCount(count);
                        if (groupInfo.getAvatarId() != null)
                            groupDTO.setAvatarId(groupInfo.getAvatarId());
                        if (csvZones != null&&!csvZones.isEmpty())
                            talkGroupInfoDTO.setCsvZone(csvZones);
                        if (csvChannels != null&&!csvChannels.isEmpty())
                            talkGroupInfoDTO.setCsvChannel(csvChannels);
                        if (groupPriority.getPriority() != null)
                            talkGroupInfoDTO.setPriority(groupPriority.getPriority());

                        if (protocolVersion >= KnConstants.PROTOCOL_VERSION_27) {
                            if ((groupInfo.getGroupCreatedBy() != null) && (KnConstants.GROUP_CREATED_BY.ABDG.value() == groupInfo.getGroupCreatedBy())) {
                                groupDTO.setIsAbdgGroup(1);
                                if ((groupInfo.getGroupOwner() != null) && (!groupInfo.getGroupOwner().isEmpty())) {
                                    groupDTO.setAbdgGroupOwnerUri(KnConstants.TEL_URI_TEMPLATE + groupInfo.getGroupOwner());
                                    String ownerName = xdmDAO.getSubscriberName(groupInfo.getGroupOwner(), persisterTxn);
                                    if ((ownerName != null) && (!ownerName.isEmpty())) {
                                        groupDTO.setAbdgGroupOwnerDispName(ownerName);
                                    }
                                }
                            }
                        }
                    }else{
                        //if <pv18 then flag will not be set in the response
                        groupDTO.setGroupType(-1);
                    }

                    knXDMMCSGroupInfoDTO.setGroupDTO(groupDTO);
                    knXDMMCSGroupInfoDTO.setTalkGroupInfoDTO(talkGroupInfoDTO);

                    List<KnXDMMemberDTO> knXDMMemberDTOS = new ArrayList<>();
                    KnXDMMemberDTO knXDMMemberDTOGMS = new KnXDMMemberDTO();
                    String xcapRootURI = genInfoUtil.getXCAPRootURI(reqMdn, persisterTxn);
                    knXDMMemberDTOGMS.setUri(xcapRootURI);
                    knXDMMemberDTOS.add(knXDMMemberDTOGMS);
                    knXDMMCSGroupInfoDTO.setGmsAppServer(knXDMMemberDTOS);

                    List<KnXDMMemberDTO> knXDMMemberDTOS1 = new ArrayList<>();
                    KnXDMMemberDTO knXDMMemberDTOIDMS = new KnXDMMemberDTO();
                    String tokenEndpoint = microServicesParamNameValueMap.get("MCS_IDMS_TOKEN_ENDPOINT");
                    knXDMMemberDTOIDMS.setUri(tokenEndpoint);
                    knXDMMemberDTOS1.add(knXDMMemberDTOIDMS);
                    knXDMMCSGroupInfoDTO.setIdmsTokenEndPoints(knXDMMemberDTOS1);

                    KnXDMMemberDTO knXDMMemberDTOKMS = new KnXDMMemberDTO();
                    knXDMMCSGroupInfoDTO.setGroupKmsuri(knXDMMemberDTOKMS);
                    if (protocolVersion >= PROTOCOL_VERSION_20_X){
                        knXDMMCSGroupInfoDTO.setIsBroadcasterExtM(groupMember.containsKey(reqMdn)?1:0);
                    }
                    int subsClientType = xdmDAO.getSubsClientType(reqMdn, persisterTxn);
                    List<String> tgssGroupExtM = xdmDAO.getTgssGroupExtM(reqMdn, persisterTxn);
                    if (protocolVersion >= PROTOCOL_VERSION_27 && (subsClientType == 3 || subsClientType == 12)){
                        if(tgssGroupExtM != null && !tgssGroupExtM.isEmpty() && tgssGroupExtM.contains(String.valueOf(corpGroupId))) {
                            knXDMMCSGroupInfoDTO.setIsTgssGroupExtM(1);
                        }
                    }
                    knXDMMCSGroupInfoDTOS.add(knXDMMCSGroupInfoDTO);
                }
            }
            knLogger.debug(methodName, " RespDTOHere    : ", respDto);
            respDto.setMcsgrpInfo(knXDMMCSGroupInfoDTOS);
            KnXDMMemberDTO onnetwork_implicitaffiliations_usertdto = new KnXDMMemberDTO();
            respDto.setOnnetwork_implicitaffiliations_usertdto(onnetwork_implicitaffiliations_usertdto);

            KnXDMMemberDTO onnetwork_one2oneemergencyalert_dto = new KnXDMMemberDTO();
            respDto.setOnnetwork_one2oneemergencyalert_dto(onnetwork_one2oneemergencyalert_dto);
            if(protocolVersion >= PROTOCOL_VERSION_25){
                knLogger.debug("setting the userprofile index ",subsProfile.getUserProfileIndex());
                respDto.setUserProfileIndex(subsProfile.getUserProfileIndex());
            }

            mcsxcapRespDTO.setDataUserProfileRespDTO(respDto);
        } catch (KnAASException aex) {
            knLogger.error(methodName, "Authorization Exception occurred :" , aex);
            throw aex;
        } catch (KnPubBOException ex) {
            knLogger.error(methodName, "PubBO Exception occurred : " , ex);
            throw ex;
        } catch (KnValidationException vex) {
            knLogger.error(methodName, "Validation Exception occurred :", vex);
            throw new KnXDMServerException(vex.getErrorCode(), vex.getErrorMessage());
        }/*catch (KnDAOException ex) {
            knLogger.error( methodName, "DAO Exception occurred : " + ex);
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new KnXDMServerException(AUTH_DOC_NOT_EXISTS, "Auth doc doesnot exists");
            } else if (ex instanceof KnDBConnectionException) {
                knLogger.error( methodName, "DAO DBConnection Exception occurred :" + ex);
                throw new KnXDMServerException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);
            }
            throw new KnXDMServerException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Persistence exception occurred : ", ex); }*/ catch (Exception ex) {
            knLogger.error(methodName, "Exception occurred while retrieving  info: " + ex);
            throw new KnSystemException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Unexpected exception occurred while retrieving MCDATAUserProfile info ", ex);
        }
        knLogger.debug(methodName, "ResponseDTO = " + mcsxcapRespDTO);
        return mcsxcapRespDTO;
    }


    @Override
    public KnMCSXCAPRespDTO getMCDataServiceConfig(KnIPMCSDTO ipmcsdto, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        final String methodName = "getMCDataServiceConfig(KnIPMCSDTO, persisterTxn)";
        knLogger.debug(methodName, "ENTRY -> Input DTO Passed : " + ipmcsdto);
        KnPubMCSXCAPPersistDTO pubMCSXCAPPersistDTO;
        KnSubsProfileDTO subsProfile = null;
        KnMCSXCAPRespDTO mcsxcapRespDTO = new KnMCSXCAPRespDTO();
        KnXDMMCDATAServiceConfigRespDTO respDto = new KnXDMMCDATAServiceConfigRespDTO();
        List<KnMicroSvcsServiceConfig> msServiceConfigList = new ArrayList<>();
        KnCorpProfileDTO corpProfileDTO = null;
        String corpFS2 = null;
        try {
            try {
                subsProfile = pubInfoUtil.getProfileDetails(ipmcsdto.getMcpttID(), KnProfileTypes.PUBLIC_PROFILE, persisterTxn);
            } catch (KnPubBOException ex) {
                knLogger.error(methodName, "PubBO Exception occurred while fetching profileDetailsByMDN so checking with profileDetailsByMCPTTID after exception: " + ex);
                subsProfile = pubInfoUtil.getProfileDetailsByMcpttID(ipmcsdto.getMcpttID(), persisterTxn);
            }
            int clusterId = Integer.parseInt(System.getenv(CLUSTERID_ENV_NAME));
            knLogger.debug(methodName, " and clusterId : " + clusterId);
            Map<String, String> microServicesParamNameValueMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
            //conditions added for cri and non-cri client where user agent is not available in request
            if (KnConstants.MCSCOMPLIANCE == subsProfile.getMcpttCompliance() &&
                    (null == ipmcsdto.getUserAgent() || ipmcsdto.getUserAgent().isEmpty())) {
                knLogger.debug(methodName, "Cri client(MCSCOMPLIANCE is 1) without user agent   ", ipmcsdto.getUserAgent());
                //check from db else retrive from default config
                if (null != subsProfile.getUserAgent()) {
                    ipmcsdto.setUserAgent(subsProfile.getUserAgent());
                } else {
                    ipmcsdto.setUserAgent(getDefaultUAByMicroserviceConfig(microServicesParamNameValueMap));
                }
            } else if (KnConstants.MCSCOMPLIANCE != subsProfile.getMcpttCompliance() &&
                    (null == ipmcsdto.getUserAgent() || ipmcsdto.getUserAgent().isEmpty())) {
                knLogger.error(methodName, "Non cri client(MCSCOMPLIANCE is 0) without user agent");
                throw new KnPubBOException(KnErrorCodes.Validator.USER_AGENT_NOT_AVAILABLE,
                        "User Agent not available for non cri client ");
            }
            String pv = KnGeneralUtil.validateAndFetchDetailsFromUA(ipmcsdto.getUserAgent());
            int protocolVersion = pv != null ? Integer.parseInt(pv) : 0;
            KnSubscriberPersistDTO originator = new KnSubscriberPersistDTO();
            pubMCSXCAPPersistDTO = new KnPubMCSXCAPPersistDTO();
            pubMCSXCAPPersistDTO.setInputDTO(ipmcsdto);
            pubMCSXCAPPersistDTO.setPersistenceDTO(originator);
            pubMCSXCAPPersistDTO.setProtocolVersion(protocolVersion);
            knLogger.debug(methodName, "validating dto ", pubMCSXCAPPersistDTO);
            validatorFwk.validate(pubMCSXCAPPersistDTO);
            String xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
            IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
            String xdmPttId = genInfoUtil.retrieveLocalXDMPttServerId();
            IXDMServerDAO xdmDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttId);
            if (subsProfile.getCorpSubscriptionType() == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
                corpProfileDTO = xdmDAO.getCorporateProfile(String.valueOf(subsProfile.getCorpId()), persisterTxn);
                if (corpProfileDTO != null) {
                    corpFS2 = corpProfileDTO.getCorpFS2();
                }
            }
            KnSubsProfileDTO subsProfileDTO = generalUtil.prepareUserProfileChanges(subsProfile, corpFS2, ipmcsdto.getUserAgent(), protocolVersion, ipmcsdto.getClientFS2());
            if(subsProfileDTO.isActiveFSUpdated()){
                xdmDAO.updateSubscriberProfile(subsProfileDTO, persisterTxn);
                mcsxcapRespDTO.setSubsProfileDTO(subsProfile);
            }
            Map<String, String> msSvcConfigDocMap = genInfoUtil.retrieveMSSvcsServiceConfig(clusterId, persisterTxn);
            msServiceConfigList = xdmServerDAO.retrieveMSSvcsServiceConfig(persisterTxn);
            String mcs_domain_name = microServicesParamNameValueMap.get("MCS_DOMAIN_NAME");
            knLogger.debug(methodName, "Iterating msServiceConfigList " + msServiceConfigList);
            for (KnMicroSvcsServiceConfig serviceConfigDto : msServiceConfigList) {
                if (clusterId == serviceConfigDto.getClusterId()) {
                    if (serviceConfigDto.getServiceType().trim().equals("MCDATA") && serviceConfigDto.getParamName().equals("TIME_TEMP_DATA_WAITING")) {
                        String time_temp_data_waiting = serviceConfigDto.getParamvalue();
                        respDto.setCommon_tx_and_rx_control_time_temp_data_waiting(time_temp_data_waiting);
                    }
                    if (serviceConfigDto.getServiceType().trim().equals("MCDATA") && serviceConfigDto.getParamName().equals("TIME_PERIODIC_ANNOUNCEMENT")) {
                        String time_periodic_announcement = serviceConfigDto.getParamvalue();
                        respDto.setCommon_tx_and_rx_control_time_periodic_announcement(time_periodic_announcement);
                    }
                    if (serviceConfigDto.getServiceType().trim().equals("MCDATA") && serviceConfigDto.getParamName().equals("MAX_DATA_SIZE_SDS_BYTES")) {
                        String max_data_size_sds_bytes = serviceConfigDto.getParamvalue();
                        respDto.setOnnetwork_tx_and_rx_control_max_datasize_sds_bytes(max_data_size_sds_bytes);
                    }
                    if (serviceConfigDto.getServiceType().trim().equals("MCDATA") && serviceConfigDto.getParamName().equals("MAX_PAYLOAD_SIZE_SDS_CPLANE_BYTES")) {
                        String max_payload_size_sds_cplane_bytes = serviceConfigDto.getParamvalue();
                        respDto.setOnnetwork_tx_and_rx_control_max_payload_size_sds_cplane_bytes(max_payload_size_sds_cplane_bytes);
                    }
                }
            }

            knLogger.debug(methodName, " RespDTO Here: ", respDto);
            String maxFDBytes = microServicesParamNameValueMap.get("MAX_DATA_SIZE_FD_BYTES");
            String autoRecBytes = microServicesParamNameValueMap.get("MAX_DATA_SIZE_AUTO_RECV_BYTES");
            respDto.setOnnetwork_tx_and_rx_control_max_data_size_fd_bytes(maxFDBytes);
            respDto.setOnnetwork_tx_and_rx_control_max_data_size_auto_recv_bytes(autoRecBytes);
            String mcdata_confidentiality_protection = microServicesParamNameValueMap.get("MCDATA_CONFIDENTIALITY_PROTECTION");
            String mcdata_integrity_protection = microServicesParamNameValueMap.get("MCDATA_INTEGRITY_PROTECTION");
            String default_file_availability = microServicesParamNameValueMap.get("DEFAULT_FILE_AVAILABILITY");
            String max_file_availability = microServicesParamNameValueMap.get("MAX_FILE_AVAILABILITY");
            respDto.setOnnetwork_signalling_protection_confidentiality_protection(mcdata_confidentiality_protection);
            respDto.setOnnetwork_signalling_protection_integrity_protection(mcdata_integrity_protection);
            respDto.setOnnetwork_file_availability_default_file_availability(default_file_availability);
            respDto.setOnnetwork_file_max_file_availability(max_file_availability);
            respDto.setService_configuration_domain(mcs_domain_name);
            respDto.setEtag(System.currentTimeMillis());
            mcsxcapRespDTO.setDataServiceConfigRespDTO(respDto);
        } catch (KnPubBOException ex) {
            knLogger.error(methodName, "PubBO Exception occurred : " + ex);
            throw ex;
        } catch (KnValidationException vex) {
            knLogger.error(methodName, "Validation Exception occurred :" + vex);
            throw new KnXDMServerException(vex.getErrorCode(), vex.getErrorMessage());
        } catch (Exception ex) {
            knLogger.error(methodName, "Exception occurred while retrieving  info: " + ex);
            throw new KnSystemException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Unexpected exception occurred while retrieving MCDATAServiceConfig info ", ex);
        }
        knLogger.debug(methodName, "ResponseDTO = " + mcsxcapRespDTO);
        return mcsxcapRespDTO;
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
