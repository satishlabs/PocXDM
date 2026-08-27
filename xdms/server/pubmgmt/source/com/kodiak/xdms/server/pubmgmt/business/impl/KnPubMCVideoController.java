/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.business.impl;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnPubMCVideoController.java
 * Subsystem:  XDMS
 * <p/>
 * Name                  Date          Release
 * --------------------  ------------  -------------------------------------
 * Shashank Tewari      29/07/2019    9.1
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
import com.kodiak.common.commdto.response.*;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.exception.KnSystemException;
import com.kodiak.common.ggcache.dto.KnDefaultMCSClientInfo;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.common.resources.KnGeneralCacheUtil;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.KnXDMServerException;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dao.KnFactorySelector;
import com.kodiak.xdms.server.common.dao.persister.IXDMServerDAO;
import com.kodiak.xdms.server.common.dto.common.*;
import com.kodiak.xdms.server.common.dto.common.KnCorpGroupListInfoDTO;
import com.kodiak.xdms.server.common.dto.persistdat.KnSubscriberPersistDTO;
import com.kodiak.xdms.server.common.framework.aas.KnAASException;
import com.kodiak.xdms.server.common.framework.aas.KnAASFramework;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorFramework;
import com.kodiak.xdms.server.common.resources.KnConstants;
import com.kodiak.xdms.server.common.resources.KnProfileTypes;
import com.kodiak.xdms.server.common.util.KnGeneralUtil;
import com.kodiak.xdms.server.pubmgmt.business.IPubMCVideoController;
import com.kodiak.xdms.server.pubmgmt.business.KnPubBOException;
import com.kodiak.xdms.server.pubmgmt.business.helper.KnPubInfoUtil;
import com.kodiak.xdms.server.pubmgmt.dao.KnPubFactorySelector;
import com.kodiak.xdms.server.pubmgmt.dao.persister.IPubXdmDAO;
import com.kodiak.xdms.server.pubmgmt.dao.persister.db.tables.KnPubDBTablesRegistry;
import com.kodiak.xdms.server.pubmgmt.dao.persister.db.tables.xdm.KnContactListDAO;
import com.kodiak.xdms.server.pubmgmt.dao.persister.db.tables.xdm.KnContactListMemberDAO;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnIPMCSDTO;
import com.kodiak.xdms.server.pubmgmt.dto.common.KnMCPTTPermInfoDTO;
import com.kodiak.xdms.server.pubmgmt.dto.common.KnMCSXCAPRespDTO;
import com.kodiak.xdms.server.pubmgmt.dto.common.KnMemberDTO;
import com.kodiak.xdms.server.pubmgmt.dto.persistdat.KnPubMCSXCAPPersistDTO;
import com.kodiak.xdms.server.pubmgmt.resources.KnErrorCodes;

import java.util.*;
import java.util.stream.Collectors;

import static com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME;
import static com.kodiak.common.resources.KnConstants.PROTOCOL_VERSION_25;
import static com.kodiak.common.resources.KnGeneralUtil.convertLongToBitSet;
import static com.kodiak.common.resources.KnGeneralUtil.getFeatureBitValue;
import static com.kodiak.xdms.server.common.resources.KnConstants.*;

public class KnPubMCVideoController implements IPubMCVideoController {
    private static final KnLogger knLogger = KnLogger.getLogger(KnPubMCVideoController.class);
    private KnValidatorFramework validatorFwk = null;
    private KnAASFramework authorizationFwk = null;
    private KnPubInfoUtil pubInfoUtil = null;
    private KnGenInfoUtil genInfoUtil = null;
    private KnGeneralUtil generalUtil = null;

    public KnPubMCVideoController() {
        validatorFwk = KnValidatorFramework.getInstance(KnConstants.LIBRARY_NAME_PUB_MGMT);
        authorizationFwk = KnAASFramework.getInstance(KnConstants.LIBRARY_NAME_PUB_MGMT);
        pubInfoUtil = new KnPubInfoUtil();
        genInfoUtil = KnGenInfoUtil.getInstance();
        generalUtil = new KnGeneralUtil();
    }

    @Override
    public KnMCSXCAPRespDTO getMCVideoUEConfig(KnIPMCSDTO ipmcsdto, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        final String methodName = "getMCVideoUEConfig(KnIPMCSDTO, persisterTxn)";
        knLogger.debug(methodName, "ENTRY -> Input DTO Passed : " + ipmcsdto);
        KnMCSXCAPRespDTO mcsxcapRespDTO = new KnMCSXCAPRespDTO();
        KnXDMMCVideoUEConfigRespDTO respDto = new KnXDMMCVideoUEConfigRespDTO();
        KnPubMCSXCAPPersistDTO pubMCSXCAPPersistDTO;
        KnCorpProfileDTO corpProfileDTO;
        String corpFS2 = null;
        try {
            knLogger.info(methodName, "retrieving subscriber profile for MCID ", KnGDPRTemplate.mcId(ipmcsdto.getMcId()));
            // KnSubsProfileDTO subsProfile = pubInfoUtil.getProfileDetails(ipmcsdto.getMcId(), ipmcsdto.getProfile(), true, persisterTxn);
            KnSubsProfileDTO subsProfile = pubInfoUtil.getProfileDetailsByMcIDAndUpmIndex(ipmcsdto.getMcId(),"0", persisterTxn);
            int clusterId = Integer.parseInt(System.getenv(CLUSTERID_ENV_NAME));
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
            //KnCorpProfileDTO corpProfileDTO=xdmDAO.getCorporateProfile(String.valueOf(subsProfile.getCorpId()),persisterTxn);
            String mcs_domain_name = microServicesParamNameValueMap.get("MCS_DOMAIN_NAME");
            Map<String, String> msSvcConfigDocMap = genInfoUtil.retrieveMSSvcsServiceConfig(clusterId, persisterTxn);
            String ueConfigName = msSvcConfigDocMap.get("MCVIDEO_UE_CONFIG_NAME");
            Map<String, String> rtxConfig = genInfoUtil.retrieveRTXConfigValues(persisterTxn);
            String ip_pref_on_cell_intf = rtxConfig.get("IP_PREF_ON_CELL_INTF");
            knLogger.debug(methodName, " RespDTO Here: ", respDto);
            respDto.setMdn(subsProfile.getMdn());
            respDto.setEtag(System.currentTimeMillis());
            respDto.setDomain(mcs_domain_name);
            respDto.setDomainName(ueConfigName);
            respDto.setIpv6Preferred(Boolean.valueOf(ip_pref_on_cell_intf));
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
            mcsxcapRespDTO.setVideoUEConfigRespDTO(respDto);
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
            knLogger.error(methodName, "Exception occurred  while retrieving  info: " + ex);
            throw new KnSystemException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Unexpected exception occurred while retrieving MCVideoUEConfig info ", ex);
        }
        knLogger.debug(methodName, "ResponseDTO = " + mcsxcapRespDTO);
        return mcsxcapRespDTO;
    }

    @Override
    public KnMCSXCAPRespDTO getMCVideoUserProfile(KnIPMCSDTO ipmcsdto, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        final String methodName = "getMCVideoUserProfile(KnIPMCSDTO, persisterTxn)";
        knLogger.debug(methodName, "ENTRY -> Input DTO Passed : " + ipmcsdto);
        KnMCSXCAPRespDTO mcsxcapRespDTO = new KnMCSXCAPRespDTO();
        KnXDMMCVideoUserProfileRespDTO respDto = new KnXDMMCVideoUserProfileRespDTO();
        KnPubMCSXCAPPersistDTO pubMCSXCAPPersistDTO;
        Collection<KnXDMMemberDTO> notifyList = new ArrayList();
        List<KnXDMMCSGroupInfoDTO> knXDMMCSGroupInfoDTOS = new ArrayList<>();
        Collection<KnXDMMemberDTO> memberDTOS = new ArrayList<>();
        KnCorpProfileDTO corpProfileDTO;
        String corpFS2 = null;
        int index = 0;
        try {
            knLogger.info(methodName, "retreiving subscriber profile for MCID ", KnGDPRTemplate.mcId(ipmcsdto.getMcId()));

            //Doing validation for accesstoken on base MDN.
            KnSubsProfileDTO baseMdn = pubInfoUtil.getProfileDetailsByMcIDAndUpmIndex(ipmcsdto.getMcId(),"0",
                    persisterTxn);
            int clusterId = Integer.parseInt(System.getenv(CLUSTERID_ENV_NAME));
            Map<String, String> microServicesParamNameValueMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
            boolean isDefaultUASet = false;////to add defaultClientfs
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
                knLogger.error(methodName, "Non cri client(MCSCOMPLIANCE is 0) without user agent ");
                throw new KnPubBOException(KnErrorCodes.Validator.USER_AGENT_NOT_AVAILABLE,
                        "User Agent not available for non cri client ");
            }
            int mcpttCompliance = baseMdn.getMcpttCompliance();
            int protocolVersion = Integer.parseInt(KnGeneralUtil.validateAndFetchDetailsFromUA(ipmcsdto.getUserAgent()));
            //default ua is not available in subscriber provisioning hence fetching default clientfs from mcs client info
            if (isDefaultUASet) {
                Map<Integer, KnDefaultMCSClientInfo> criClientConfig = KnGeneralCacheUtil.getInstance().retrieveMCSClientInfo();
                if (null != criClientConfig.get(protocolVersion)) {
                    ipmcsdto.setClientFS2(criClientConfig.get(protocolVersion).getClientFS2());
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
                //kodiak client fileName=mcvideo-user-profile-0.xml
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
            corpProfileDTO = xdmDAO.getCorporateProfile(String.valueOf(subsProfile.getCorpId()), persisterTxn);
            if (corpProfileDTO != null) {
                corpFS2 = corpProfileDTO.getCorpFS2();
            }
            String oldActiveFS2 = subsProfile.getActiveFS2();
            KnSubsProfileDTO subsProfileDTOChanged = generalUtil.prepareUserProfileChanges(subsProfile, corpFS2, ipmcsdto.getUserAgent(), protocolVersion, ipmcsdto.getClientFS2());
            if(subsProfileDTOChanged.isActiveFSUpdated()){
                xdmDAO.updateSubscriberProfile(subsProfileDTOChanged, persisterTxn);
                mcsxcapRespDTO.setSubsProfileDTO(subsProfile);
                if(upmIndex.equals(KnConstants.BASE_MDN_INDEX)){
                    com.kodiak.xdms.server.common.util.KnGeneralUtil.veryLargeGroupBitChanged(oldActiveFS2
                            ,subsProfileDTOChanged.getActiveFS2()
                            ,subsProfileDTOChanged.getMdn()
                            ,String.valueOf(subsProfile.getCorpId()),false);
                }
            }
            Map<String, String> msSvcConfigDocMap = genInfoUtil.retrieveMSSvcsServiceConfig(clusterId, persisterTxn);
            KnSIPProxySvcConfigDTO knProxyDTO = xdmDAO.selectSIPProxySvcConfig(subsProfile.getPocHome(), persisterTxn);
            String GMSFQDN = knProxyDTO.getSipProxyURI();
            int serviceAuthStatus = subsProfile.getServiceAuthStatus();
            boolean status = false;
            if (serviceAuthStatus == 2) {
                status = true;
            }
            respDto.setMdn(reqMdn);
            respDto.setEtag(System.currentTimeMillis());
            respDto.setXuiURI(subsProfile.getMcId());
            respDto.setName(msSvcConfigDocMap.get("MCVIDEO_USER_PROFILE_SETTINGS"));
            respDto.setStatus(String.valueOf(status));
            respDto.setProfileName(msSvcConfigDocMap.get("MCVIDEO_USER_PROFILE_NAME"));
            respDto.setActiveFS(subsProfile.getActiveFS2());
            knLogger.debug(methodName, " RespDTO Here : ", respDto);
            KnXDMMemberDTO cmUserAlias = new KnXDMMemberDTO();
            cmUserAlias.setDisplayName(subsProfile.getUserId());
            respDto.setCmUserAlias(cmUserAlias);
            respDto.setOldActiveFS(oldActiveFS2);
            respDto.setCorpId(corpId);

            KnXDMMemberDTO cmVideoUserID = new KnXDMMemberDTO();
            cmVideoUserID.setUri(subsProfile.getMcVideoId());
            cmVideoUserID.setDisplayName(subsProfile.getNetworkName());
            respDto.setCmVideoUserID(cmVideoUserID);
            if (corpProfileDTO != null && corpProfileDTO.getExtCorpId() != null) {
                respDto.setMcoName(corpProfileDTO.getExtCorpId().trim());
            }
            //Pass the owner mdn to get the contactlistID
            // Pass the contactListIds to get the listof Member MDNs
            //For each member MDN  get the subsprofile and increment the index value of KnMember DTO
            knLogger.debug(methodName, " RespDTO Here   : ", respDto);
            index = 0;
            KnContactListDAO contactListDAO = KnPubDBTablesRegistry.getDBXdmTableRegistry().getContactListDAO(xdmPttId);
            Collection<Integer> contactListIds = contactListDAO.getContactListIdsForMdn(reqMdn, true, persisterTxn);
            if (contactListIds != null && !contactListIds.isEmpty()) {
                KnContactListMemberDAO contactMemDAO = KnPubDBTablesRegistry.getDBXdmTableRegistry().getContactListMemberDAO(xdmPttId);
                Map<Integer, Collection<KnMemberDTO>> map_for_membersContactIDAndBean = contactMemDAO.getMembersForContactIds(contactListIds, true, persisterTxn); ////Put if Condition
                if (map_for_membersContactIDAndBean != null && !map_for_membersContactIDAndBean.isEmpty()) {
                    for (Collection<KnMemberDTO> collection_knMemberDTO : map_for_membersContactIDAndBean.values()) {
                        if (collection_knMemberDTO != null) {
                            for (KnMemberDTO knMemberDTO : collection_knMemberDTO) {
                                KnSubsProfileDTO subsProfileDTO = pubInfoUtil.getProfileDetails(knMemberDTO.getMemberMdn(), ipmcsdto.getProfile(), true, KnConstants.FALSE, persisterTxn);
                                KnXDMMemberDTO common_one2one_memberDTO = new KnXDMMemberDTO();
                                common_one2one_memberDTO.setUri(subsProfileDTO.getMcVideoId());
                                common_one2one_memberDTO.setDisplayName(knMemberDTO.getMemberName());
                                common_one2one_memberDTO.setUfmi(String.valueOf(index));
                                index++;
                                notifyList.add(common_one2one_memberDTO);
                            }
                        }
                    }
                }
            }

            respDto.setCmNotList(notifyList);
            knLogger.debug(methodName, " RespDTOHere: ", respDto);
            index = 0;
            knLogger.debug(methodName, "subsProfile.getCorpSubscriptionType() " + subsProfile.getCorpSubscriptionType() + " subsProfile.getCorpId() " + subsProfile.getCorpId());
            if (subsProfile.getCorpSubscriptionType() == 1) {
                Integer corpListId = xdmServerDAO.getCorpListIdForMDN(reqMdn, persisterTxn);
                List<Integer> commonCorpListIds = new ArrayList<>();
                boolean isCommonContactEnabled = getFeatureBitValue(subsProfile.getActiveFS2(), com.kodiak.common.resources.KnConstants.FEATURE_SET.COMMON_CONTACT_LIST.value());
                if(isCommonContactEnabled) {
                    commonCorpListIds =  xdmServerDAO.getCommonCorpListIdForMDN(baseMdn.getMdn(), persisterTxn);
                }
                Collection<String> memberMdnsForCorpListId = null;
                if ((corpListId != null) || (commonCorpListIds != null  && !commonCorpListIds.isEmpty()) ) {
                    if(corpListId != null)
                        memberMdnsForCorpListId = xdmServerDAO.getMemberMdnsForCorpListId(List.of(corpListId), subsProfile.getCorpId(), persisterTxn);
                    if (commonCorpListIds != null  && !commonCorpListIds.isEmpty()) {
                        var memberMdnsForCommonCorpListId = xdmServerDAO.getMemberMdnsForCorpListId(commonCorpListIds,
                                null, persisterTxn);

                        if (memberMdnsForCommonCorpListId != null && !memberMdnsForCommonCorpListId.isEmpty()) {
                            if (memberMdnsForCorpListId == null) memberMdnsForCorpListId = new ArrayList<>();
                            memberMdnsForCorpListId.addAll(memberMdnsForCommonCorpListId);
                        }
                    }
                    if (memberMdnsForCorpListId != null) {
                        List<KnMCPTTPermInfoDTO> tuPermInfoList =new ArrayList<>();
                        try{
                            tuPermInfoList = xdmServerDAO.getMCPTTPermInfoOnTargetMDN(reqMdn, persisterTxn);
                        }catch (KnDAOException ex){
                            knLogger.error(methodName,ex.getMessage());
                        }
                        //getting base mdn from profile or base mdn.
                        List<String> tuList = tuPermInfoList.stream()
                                .map(l -> l.getAuthorizedMdn())
                                .collect(Collectors.toList());
                        List<String> baseOfProfileMdn = pubInfoUtil.getRealMdns(tuList, true, persisterTxn);
                        //getting map of profile mdn of base mdn.
                        Map<String, List<String>> profileOFBaseMdnMap = pubInfoUtil.getProfileMdnListByBaseMdnsList(baseOfProfileMdn, true, persisterTxn);
                        Long mergedPermBits = null;
                        for (String corpMDN : memberMdnsForCorpListId) {
                            KnSubsProfileDTO subsProfileDTO = pubInfoUtil.getProfileDetails(corpMDN, ipmcsdto.getProfile(), true, KnConstants.FALSE, persisterTxn);

                            boolean mcVideoUnconfirmedPull = false;

                            KnXDMMemberDTO corpMemDTO = new KnXDMMemberDTO();
                            corpMemDTO.setUri(subsProfileDTO.getMcDataId());
                            corpMemDTO.setDisplayName(subsProfileDTO.getNetworkName());
                            corpMemDTO.setUfmi(String.valueOf(index++));
                            if (protocolVersion >= PROTOCOL_VERSION_18_X&&isKodiakClient) {
                                String formattedUA = subsProfileDTO.getUserAgent();
                                if (subsProfileDTO.getUserAgent() != null) {
                                    KnUserAgentDTO userAgentInfo = KnGeneralUtil.getDetailsFromUserAgent(subsProfileDTO.getUserAgent());
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
                                corpMemDTO.setEntryType(String.valueOf(subsProfileDTO.getClientType()));
                                corpMemDTO.setContactType(subsProfileDTO.getMdn().trim());
                                corpMemDTO.setClientType(String.valueOf(subsProfileDTO.getClientType()));
                                corpMemDTO.setUserAgent(formattedUA);
                                corpMemDTO.setActiveFS(subsProfileDTO.getActiveFS2());
                                //KnMCPTTPermInfoDTO permInfoDTO = xdmServerDAO.getMCPTTPermInfoByMdns(subsProfile.getMdn(), corpMDN, persisterTxn);
                                List<String> tuProfileMdnList = profileOFBaseMdnMap.get(corpMDN.trim());
                                BitSet finalPermBits = new BitSet(Long.SIZE);
                                //basemdn
                                KnMCPTTPermInfoDTO tuBaseMdn = tuPermInfoList.stream().filter(e -> e.getAuthorizedMdn().equals(corpMDN.trim()))
                                        .findAny().orElse(null);
                                if(tuBaseMdn!=null){
                                    finalPermBits.or(pubInfoUtil.convertLongToBitSet(tuBaseMdn.getPermBitset()));
                                    //profile mdn
                                    if(tuProfileMdnList!=null){
                                        for(String profileMdn:tuProfileMdnList){
                                            KnMCPTTPermInfoDTO tuPermObj = tuPermInfoList.stream().filter(e -> e.getAuthorizedMdn().equals(profileMdn.trim()))
                                                    .findAny().orElse(null);
                                            if(tuPermObj!=null){
                                                finalPermBits.or(pubInfoUtil.convertLongToBitSet(tuPermObj.getPermBitset()));
                                            }
                                        }
                                    }
                                }

                                mergedPermBits=pubInfoUtil.convertBitSetToLong(finalPermBits);
                                BitSet permBitSet = convertLongToBitSet(mergedPermBits);
                                mcVideoUnconfirmedPull = permBitSet.get(5);
                                corpMemDTO.setUnConfirmedPull(mcVideoUnconfirmedPull ? 1 : 0);
                            }
                            if(protocolVersion >= PROTOCOL_VERSION_22 && isKodiakClient){
                                corpMemDTO.setCameraType(subsProfileDTO.getCameraType());
                            }
                            memberDTOS.add(corpMemDTO);
                        }
                    }
                }
            }
            knLogger.debug(methodName, "memberDTOS : ", memberDTOS);
            respDto.setCmSharedNotList(memberDTOS);
            //  Code for Onnetwork MCGroup

            knLogger.debug(methodName, "subsProfile.getCorpSubscriptionType() " + subsProfile.getCorpSubscriptionType() + "subsProfile.getCorpId() " + subsProfile.getCorpId());
            Map<String, List<Integer>> memberGroupIdsMap = null;
            Set<KnCorpGroupListInfoDTO> mcxGroupList = new HashSet<>();
            List<Integer> mcxGroups = new ArrayList<>();
            if (subsProfile.getCorpSubscriptionType() == 1) {
                memberGroupIdsMap = xdmDAO.selectCorpGroupId(Arrays.asList(reqMdn), persisterTxn);
                knLogger.debug(methodName, " CorpGroupIds " + memberGroupIdsMap.values());
                String userProfileId = subsProfile.getUserProfileId();
                knLogger.debug(methodName, " userProfileId " + userProfileId);
                if(userProfileId!=null){
                    Set<KnCorpGroupListInfoDTO> mcxGrpList = pubInfoUtil.retriveGroupProfileInfoByProfileId(userProfileId, persisterTxn);
                    knLogger.debug(methodName, "mcxGroup ", mcxGrpList);
                    if (mcxGrpList != null && mcxGrpList.size() > 0) {

                    	for(KnCorpGroupListInfoDTO corGrp:mcxGrpList) {
                    		mcxGroups.add(corGrp.getGroupID());
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
                knLogger.debug(methodName, " CorpGroupIds " + memberGroupIdsMap.values());
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
                                        if(groupPriority.getPriority() == null) {
                                            groupPriority.setPriority(value.getGroupPriority());
                                        }
                                    }
                                }
                            }
                            groupDTO.setGroupType(Integer.parseInt(groupInfo.getGroupType()));
                            if (Integer.parseInt(groupInfo.getGroupType()) == 2) {
                                //Mikey-Sakke :: Pushing Broadcast Groups to non-broadcasters applicable above pv 20
                                //If add GroupType if PV>=20 for non-broadcastors
                                if (!(groupMember.isEmpty()) && (groupMember.get(reqMdn) != null)) {
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
                        if (protocolVersion >= PROTOCOL_VERSION_20_X){
                            knXDMMCSGroupInfoDTO.setIsBroadcasterExtM(groupMember.get(reqMdn)?1:0);
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
                Integer mcVideoFlrTimer = corpProfileDTO.getMcvideoFloorHoldTimer();
                knLogger.debug(methodName, " mcVideoFlrTimer : ", mcVideoFlrTimer);
                if (mcVideoFlrTimer == null) {
                    KnPOCSvcConfigDTO pocSvcConfigDTO = pubInfoUtil.retrievePOCSvcConfig(subsProfile.getPocHome(), persisterTxn);
                    mcVideoFlrTimer = pocSvcConfigDTO.getMcvideofloorholdtimer();
                    if (mcVideoFlrTimer == 0) {
                        respDto.setMaxTimeSingleTransmit(7200);
                    } else {
                        respDto.setMaxTimeSingleTransmit(mcVideoFlrTimer);
                    }
                } else {
                    respDto.setMaxTimeSingleTransmit(mcVideoFlrTimer);
                }
                knLogger.debug(methodName, " RespDTO Here    : ", respDto);
            }
            respDto.setMcsgrpInfo(knXDMMCSGroupInfoDTOS);
            if(protocolVersion >= PROTOCOL_VERSION_25){
                knLogger.debug("Setting the index ",subsProfile.getUserProfileIndex());
                respDto.setUserProfileIndex(subsProfile.getUserProfileIndex());
            }
            mcsxcapRespDTO.setVideoUserProfileRespDTO(respDto);
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
        }*/ catch (KnValidationException vex) {
            knLogger.error(methodName, "Validation Exception occurred :" + vex);
            throw new KnXDMServerException(vex.getErrorCode(), vex.getErrorMessage());
        } catch (Exception ex) {
            knLogger.error(methodName, "Exception occurred while retrieving  info: " + ex);
            throw new KnSystemException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Unexpected exception occurred while retrieving MCVideoUserProfile info ", ex);
        }
        knLogger.debug(methodName, "ResponseDTO = " + mcsxcapRespDTO);
        return mcsxcapRespDTO;
    }

    @Override
    public KnMCSXCAPRespDTO getMCVideoServiceConfig(KnIPMCSDTO ipmcsdto, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        final String methodName = "getMCVideoServiceConfig(KnIPMCSDTO, persisterTxn)";
        knLogger.debug(methodName, "ENTRY -> Input DTO Passed : " + ipmcsdto);
        KnPubMCSXCAPPersistDTO pubMCSXCAPPersistDTO;
        KnMCSXCAPRespDTO mcsxcapRespDTO = new KnMCSXCAPRespDTO();
        KnXDMMCVideoServiceConfigRespDTO respDto = new KnXDMMCVideoServiceConfigRespDTO();
        KnSubsProfileDTO subsProfile = null;
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
            String xdmPttId = genInfoUtil.retrieveLocalXDMPttServerId();
            knLogger.debug(methodName, " XDM PTTServerId : ", xdmPttId);
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
            String mcs_domain_name = microServicesParamNameValueMap.get("MCS_DOMAIN_NAME");
            respDto.setDomain(mcs_domain_name);
            respDto.setEtag(System.currentTimeMillis());
            mcsxcapRespDTO.setVideoServiceConfigRespDTO(respDto);
        } catch (KnPubBOException ex) {
            knLogger.error(methodName, "PubBO Exception occurred : " + ex);
            throw ex;
        } catch (KnValidationException vex) {
            knLogger.error(methodName, "Validation Exception occurred :", vex);
            throw new KnXDMServerException(vex.getErrorCode(), vex.getErrorMessage());
        } catch (Exception ex) {
            knLogger.error(methodName, "Exception occurred while retrieving  info: " + ex);
            throw new KnSystemException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Unexpected exception occurred while retrieving MCVideoServiceConfig info ", ex);
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
