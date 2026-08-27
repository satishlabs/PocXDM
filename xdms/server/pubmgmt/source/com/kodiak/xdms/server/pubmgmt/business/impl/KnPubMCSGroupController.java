/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.business.impl;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnPubMCSGroupController.java
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

import com.kodiak.common.commdto.common.KnXDMMdnInfoDTO;
import com.kodiak.common.commdto.request.KnXDMOSMInfoRequestDTO;
import com.kodiak.common.ggcache.dto.KnCorpGrpLmrExtnDTO;
import com.kodiak.common.resources.KnGeneralCacheUtil;
import com.kodiak.xdms.server.pubmgmt.dto.common.KnMCSXCAPRespDTO;
import com.kodiak.common.commdto.response.KnXDMMCSGroupDocRespDTO;
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
import com.kodiak.xdms.server.pubmgmt.business.IPubMCSGroupController;
import com.kodiak.xdms.server.pubmgmt.business.KnPubBOException;
import com.kodiak.xdms.server.pubmgmt.business.helper.KnPubInfoUtil;
import com.kodiak.xdms.server.pubmgmt.dao.KnPubFactorySelector;
import com.kodiak.xdms.server.pubmgmt.dao.persister.IPubXdmDAO;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnIPMCSDTO;
import com.kodiak.xdms.server.pubmgmt.dto.common.KnEmgrDestinationInfoDTO;
import com.kodiak.xdms.server.pubmgmt.dto.persistdat.KnPubMCSXCAPPersistDTO;
import com.kodiak.xdms.server.pubmgmt.resources.KnErrorCodes;

import java.util.*;
import java.util.Map.Entry;

import static com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME;
import static com.kodiak.xdms.server.common.resources.KnConstants.CRI_DEFAULT_USER_AGENT;

public class KnPubMCSGroupController implements IPubMCSGroupController {
    private static final KnLogger knLogger = KnLogger.getLogger(KnPubMCSGroupController.class);
    private KnValidatorFramework validatorFwk = null;
    private KnAASFramework authorizationFwk = null;
    private KnPubInfoUtil pubInfoUtil = null;
    private KnGenInfoUtil genInfoUtil = null;
    private KnGeneralUtil generalUtil = null;


    public KnPubMCSGroupController() {
        validatorFwk = KnValidatorFramework.getInstance(KnConstants.LIBRARY_NAME_PUB_MGMT);
        authorizationFwk = KnAASFramework.getInstance(KnConstants.LIBRARY_NAME_PUB_MGMT);
        pubInfoUtil = new KnPubInfoUtil();
        genInfoUtil = KnGenInfoUtil.getInstance();
        generalUtil = new KnGeneralUtil();

    }

    @Override
    public KnMCSXCAPRespDTO getMCSGroupDoc(KnIPMCSDTO ipmcsdto, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        final String methodName = "getMCSGroupDoc(KnIPMCSDTO, persisterTxn)";
        knLogger.debug(methodName, "ENTRY -> Input DTO Passed : " + ipmcsdto);
        KnMCSXCAPRespDTO mcsxcapRespDTO = new KnMCSXCAPRespDTO();
        KnXDMMCSGroupDocRespDTO respDto = new KnXDMMCSGroupDocRespDTO();
        KnPubMCSXCAPPersistDTO pubMCSXCAPPersistDTO = null;
        Map<String, Boolean> mapMDNs = new HashMap<>();
        ArrayList<String> memMDNs = new ArrayList<>();
        Collection<KnXDMMdnInfoDTO> memberDTOS = new ArrayList<>();
        List<KnMicroSvcsServiceConfig> msServiceConfigList = new ArrayList<>();
        int index = 0;
        KnCorpGpInfoDTO knCorpGpInfoDTO = null;
        String mcpttEmCall = null;
        String corpId = "";
        String mcpttEmAl = null;
        String corpGroupId = "";
        Boolean autoCutIn = true;
        KnSubsProfileDTO subsProfile=null;
        KnCorpProfileDTO corpProfileDTO = null;
        String corpFS2 = null;
        int mcpttCompliance = 0;
        Map<String,Map<Integer,String>> profileMDNMap = null;
        List<String> mdnListForMCPTTID = null;
        try {
            String xdmPttId = genInfoUtil.retrieveLocalXDMPttServerId();
            IXDMServerDAO xdmDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttId);
            knLogger.info(methodName, "retrieving subscriber profile for MCPTTID ", KnGDPRTemplate.mcpttId(ipmcsdto.getMcpttID()));
            try {
                subsProfile = pubInfoUtil.getProfileDetails(ipmcsdto.getMcpttID(), KnProfileTypes.PUBLIC_PROFILE, persisterTxn);
            } catch (KnPubBOException ex) {
                knLogger.error(methodName, "PubBO Exception occurred while fetching profileDetailsByMDN so checking with profileDetailsByMCPTTID after exception: " + ex);
                subsProfile = pubInfoUtil.getProfileDetailsByMcpttID(ipmcsdto.getMcpttID(), persisterTxn);
                profileMDNMap = xdmDAO.selectProfileIdMDNsByMcpttIds(Arrays.asList(ipmcsdto.getMcpttID()),persisterTxn);
                if(profileMDNMap != null){
                    mdnListForMCPTTID = new ArrayList<>();
                    mdnListForMCPTTID.addAll(profileMDNMap.get(ipmcsdto.getMcpttID()).values());
                }
                mcpttCompliance = 1;
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
                knLogger.error(methodName, "Non cri client(MCSCOMPLIANCE is 0) without user agent ");
                throw new KnPubBOException(KnErrorCodes.Validator.USER_AGENT_NOT_AVAILABLE,
                        "User Agent not available for non cri client ");
            }
            String pv = KnGeneralUtil.validateAndFetchDetailsFromUA(ipmcsdto.getUserAgent());
            int protocolVersion = pv != null ? Integer.parseInt(pv) : 0;
            KnSubscriberPersistDTO originator = new KnSubscriberPersistDTO();
            corpGroupId = ipmcsdto.getGroupID();
            corpId = ipmcsdto.getCorpID();
            originator.setNetworkName(subsProfile.getNetworkName());
            originator.setPubSubscriptionType(subsProfile.getPublicSubscriptionType());
            originator.setServiceAuthStatus(subsProfile.getServiceAuthStatus());
            originator.setMcpttCompliance(mcpttCompliance);
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
            mapMDNs = xdmDAO.getGroupMemberList(Integer.parseInt(corpGroupId), persisterTxn);
            knCorpGpInfoDTO = xdmDAO.getCorpGroupInfoList(Integer.parseInt(corpGroupId), persisterTxn);
            String sharedCorpId = xdmDAO.getSharedCorpGroup(Integer.parseInt(corpGroupId), persisterTxn);
            String isPreConfiguredGroup = knCorpGpInfoDTO.getIsPreConfiguredGroup();
            if (String.valueOf(isPreConfiguredGroup) !=null ) {
               pubMCSXCAPPersistDTO.setIsPreConfiguredGroup(isPreConfiguredGroup);
            }
            boolean reGroupBit = false;
            if(subsProfile.getActiveFS2() != null && !subsProfile.getActiveFS2().isEmpty()) {
                reGroupBit = com.kodiak.common.resources.KnGeneralUtil.getFeatureBitValue(subsProfile.getActiveFS2(), com.kodiak.common.resources.KnConstants.FEATURE_SET.MCX_GROUP_REGROUP_FLAG_BIT.value());
            knLogger.debug(methodName,"reGroupBit: "+reGroupBit); }
            Set<String> keySet = mapMDNs.keySet();
            List<String> mdns = new ArrayList<>(keySet);
            pubMCSXCAPPersistDTO.setGroupMemberList(mdns);
            pubMCSXCAPPersistDTO.setMdns(mdnListForMCPTTID);
            //Retrive MCS Doc
            //- Validate if the User has a Authorized MDN document (has an authEntry in DG.AUTHORIZATION_DOC)
            knLogger.debug(methodName, "validating dto ", pubMCSXCAPPersistDTO);
            validatorFwk.validate(pubMCSXCAPPersistDTO);
            knLogger.debug(methodName, "Validated successfully!" + KnGDPRTemplate.mcpttId(subsProfile.getMcpttId()));
            if ( reGroupBit ) {
                if ( knCorpGpInfoDTO.getCorpId().equals(subsProfile.getCorpId()) || sharedCorpId != null && subsProfile.getCorpId() == Integer.parseInt(sharedCorpId));
                {
                    if (null == isPreConfiguredGroup || isPreConfiguredGroup.isEmpty()) {
                        respDto.setIsPreConfiguredGroup("false");
                    } else {
                        if (Integer.parseInt(isPreConfiguredGroup) == KnConstants.IS_PRE_CONFIG_GROUP_ENABLE) {
                            respDto.setIsPreConfiguredGroup("true");
                        } else {
                            respDto.setIsPreConfiguredGroup("false");
                        }
                    }
                    knLogger.debug(methodName, "PreConfiguredGroup: " + isPreConfiguredGroup);
                }
            } else {
                knLogger.debug(methodName, "Request CorpId : "+ Integer.parseInt(corpId) +" Fetched CorpId from Group : "+knCorpGpInfoDTO.getCorpId());
                knLogger.debug(methodName, "Subscriber CorpId : "+ subsProfile.getCorpId() +" Fetched CorpId from Group : "+knCorpGpInfoDTO.getCorpId());
                if (knCorpGpInfoDTO.getCorpId() != Integer.parseInt(corpId)) {
                    throw new KnPubBOException(KnErrorCodes.Validator.CORPID_NOT_MATCHED, " Either Request CorpId and Fetched CorpId from Group or Fetched CorpId from Group and Subscriber corpId does not match ");
                }
            }
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
            IPubXdmDAO xdmServerDAO = KnPubFactorySelector.getDAOFactory(KnFactorySelector.DB).createXdmServerDAO(xdmServerId);
            List<KnEmgrDestinationInfoDTO> emgrDestinationInfoDTOS = xdmServerDAO.getEmergencyDestinationInfo(subsProfile.getMdn(), true, persisterTxn);
            if (emgrDestinationInfoDTOS != null) {
                for (KnEmgrDestinationInfoDTO knEmgrDestinationInfoDTO : emgrDestinationInfoDTOS) {
                    if (knEmgrDestinationInfoDTO.getType() == 2) {
                        mcpttEmCall = knEmgrDestinationInfoDTO.getDestination();
                        mcpttEmAl = knEmgrDestinationInfoDTO.getDestination();
                    }
                }
            }
            knLogger.debug(methodName, "RespDTO Here: ", respDto);
            KnSIPProxySvcConfigDTO knProxyDTO=xdmDAO.selectSIPProxySvcConfig(subsProfile.getPocHome(),persisterTxn);
            String GMSFQDN=knProxyDTO.getSipProxyURI();
            msServiceConfigList = xdmDAO.retrieveMSSvcsServiceConfig(persisterTxn);
            if (subsProfile.getCorpSubscriptionType() == 1) {
                if (knCorpGpInfoDTO != null) {
                    String groupUri = "sip:" + knCorpGpInfoDTO.getCorpId() + "." + knCorpGpInfoDTO.getCorpgroupId() + KnConstants.AT + GMSFQDN;
                    respDto.setLsURI(groupUri);
                    String groupName = xdmDAO.getGroupName(corpGroupId, persisterTxn);
                    if (groupName != null) {
                        respDto.setLsName(groupName.trim());
                    }
                    knLogger.debug(methodName, " groupUri : " + groupUri + " groupName : " + groupName);
                }
                //added for story 1203
                knLogger.info("McxGrpInd value : ",knCorpGpInfoDTO.getMcxGrpInd());
                respDto.setMcxGrpInd(knCorpGpInfoDTO.getMcxGrpInd());
                //Find the Real Mdns for the profile Mdns
                List realMdns = pubInfoUtil.getRealMdns(mdns, true, persisterTxn);
				//Remove the profile Mdns from the map
				
				knLogger.debug(methodName, "realMdns - ", KnGDPRTemplate.mdnList(realMdns));
				Iterator<Entry<String, Boolean>> iterator = mapMDNs.entrySet().iterator();
				while(iterator.hasNext())
				{
					Entry<String, Boolean> entry = iterator.next();
					if(!realMdns.contains(entry.getKey()))
					{
						iterator.remove();
					}
				}
				knLogger.debug(methodName, "after removing profile mdns from the map - ", mapMDNs.size(),"--->", KnGDPRTemplate.mapKeyMdn(mapMDNs));
				//Add the Real mdn entry to the map if it does not exist in the map
				for (Object realmdn : realMdns) {
					Boolean isSupervisor = mapMDNs.get(realmdn);
					if(isSupervisor==null)
					{
						mapMDNs.put(realmdn.toString(), null);
					}
				}
				knLogger.debug(methodName, "after adding real mdns to the map - ", mapMDNs.size(),"--->", KnGDPRTemplate.mapKeyMdn(mapMDNs));
                if (mapMDNs != null && !mapMDNs.isEmpty()) {
                    for (Map.Entry<String, Boolean> entrySet : mapMDNs.entrySet()) {
                        memMDNs.add(entrySet.getKey());
                        if (subsProfile.getMdn().equals(entrySet.getKey())) {
                            autoCutIn = entrySet.getValue();
                        }
                    }
                    List<KnSubsProfileDTO> subsProfileDTOS = xdmDAO.retrieveBulkSubscribersInfo(memMDNs, persisterTxn);
                    if (subsProfileDTOS != null && !subsProfileDTOS.isEmpty()) {
                        for (KnSubsProfileDTO knSubsProfileDTO : subsProfileDTOS) {
                            KnXDMMdnInfoDTO knXDMMdnInfoDTO = new KnXDMMdnInfoDTO();
                            knXDMMdnInfoDTO.setUfmi(knSubsProfileDTO.getMcpttId());
                            knXDMMdnInfoDTO.setName(knSubsProfileDTO.getSubscriberName());
                            knXDMMdnInfoDTO.setMdn(knSubsProfileDTO.getMcVideoId());
                            knXDMMdnInfoDTO.setAliasMdn(knSubsProfileDTO.getMcDataId());
                            knLogger.debug(methodName, " KnXDMMdnInfoDTO ", knXDMMdnInfoDTO);
                            memberDTOS.add(knXDMMdnInfoDTO);
                        }
                    }
                }
            }
            knLogger.info("memberDTOS--> <",memberDTOS);
            knLogger.debug(methodName, " RespDTO Here: ", respDto);
            if(knCorpGpInfoDTO != null && knCorpGpInfoDTO.getMcxGrpInd() != 1){
            respDto.setUserdtoOMA(memberDTOS);
            respDto.setOnnwMaxParticipantCount(String.valueOf(memberDTOS.size()));
            }
            Integer hangTimer = knCorpGpInfoDTO.getHangTimeOut();
            if (hangTimer == null) {
                Integer hangTimeOut = xdmDAO.getPOCCallTable("EMSDSN", subsProfile.getPocHome(), persisterTxn);
                respDto.setOnnwHangTimer(hangTimeOut);
            } else {
                respDto.setOnnwHangTimer(hangTimer);
            }
            respDto.setOnnwAudoCutIn(autoCutIn);

            knLogger.debug(methodName, " RespDTO Here- ", respDto);
            String sdsFF = microServicesParamNameValueMap.get("SDSFEATUREFLAG");
            if (sdsFF == null || sdsFF.isEmpty()) {
                respDto.setAllowSDS("false");
            } else {
                if (Integer.parseInt(sdsFF) == 1) {
                    respDto.setAllowSDS("true");
                } else {
                    respDto.setAllowSDS("false");
                }
            }
            String fdFF = microServicesParamNameValueMap.get("FDFEATUREFLAG");
            if (fdFF == null || fdFF.isEmpty()) {
                respDto.setAllowFD("false");
            } else {
                if (Integer.parseInt(fdFF) == 1) {
                    respDto.setAllowFD("true");
                } else {
                    respDto.setAllowFD("false");
                }
            }
            String cmF = microServicesParamNameValueMap.get("CONVERSATIONMGTFLAG");
            if (cmF == null || cmF.isEmpty()) {
                respDto.setAllowCM("false");
            } else {
                if (Integer.parseInt(sdsFF) == 1) {
                    respDto.setAllowCM("true");
                } else {
                    respDto.setAllowCM("false");
                }
            }
            if (knCorpGpInfoDTO.getOsmLisId() == null || knCorpGpInfoDTO.getOsmLisId() == 0) {
                respDto.setAllowEnhancedStatus("false");
            } else {
                respDto.setAllowEnhancedStatus("true");
            }
            knLogger.debug(methodName, " RespDTO Here : ", respDto);
            for (KnMicroSvcsServiceConfig cfgDTO : msServiceConfigList) {
                if (cfgDTO.getServiceType().trim().equals("MCDATA") && cfgDTO.getParamName().equals("MAX_DATA_SIZE_SDS_BYTES")) {
                    respDto.setOnnwSDS(cfgDTO.getParamvalue());
                }
            }
            respDto.setOnnwFD(microServicesParamNameValueMap.get("MAX_DATA_SIZE_FD_BYTES"));
            respDto.setOnnwAR(microServicesParamNameValueMap.get("MAX_DATA_SIZE_AUTO_RECV_BYTES"));

            //if mdn's activefs 118 or 119 bit enabled, then fetch ugwConfig info from gg cache
            boolean astroFrequencySelect = com.kodiak.common.resources.KnGeneralUtil.getFeatureBitValue(subsProfile.getActiveFS2(), com.kodiak.common.resources.KnConstants.FEATURE_SET.ASTRO_FREQUENCT_SELECT_BIT.value());
            boolean astroCodedAndClear = com.kodiak.common.resources.KnGeneralUtil.getFeatureBitValue(subsProfile.getActiveFS2(), com.kodiak.common.resources.KnConstants.FEATURE_SET.ASTRO_CODED_AND_CLEAR_BIT.value());
            knLogger.debug(methodName, " astroFrequencySelect: ", astroFrequencySelect, "astroCodedAndClear: ",astroCodedAndClear);

            if(astroFrequencySelect || astroCodedAndClear) {
                KnGeneralCacheUtil generalCacheUtil = KnGeneralCacheUtil.getInstance();
                KnCorpGrpLmrExtnDTO corpGrpLmrExtnDTO = generalCacheUtil.getCorpGrpLmrExtn(Integer.parseInt(corpId), Integer.parseInt(corpGroupId));
                knLogger.debug(methodName, "corpGrpLmrExtnDTO obtained from the DB is-->", corpGrpLmrExtnDTO);

                if (corpGrpLmrExtnDTO != null && corpGrpLmrExtnDTO.getLmrExtn() != null && corpGrpLmrExtnDTO.getLmrExtn().length != 0){
                    respDto.setUgwConfig(new String(corpGrpLmrExtnDTO.getLmrExtn()));
                    knLogger.debug(methodName, "UGWConfig LmrExtn After Conversion-->", respDto.getUgwConfig());
                }
            }

            Map<String, String> rxtConfig = genInfoUtil.retrieveRTXConfigValues(persisterTxn);
            String priorityListValues = rxtConfig.get("POC_VOCODER_PRIORITY_LIST");
            respDto.setEncodingNames(priorityListValues);
            knLogger.debug(methodName, " RespDTO Here    : ", respDto);
            List<KnXDMOSMInfoRequestDTO> knXDMOSMInfoRequestDTOS = xdmDAO.getUniqueFieldsOSMInfoList(Integer.parseInt(corpGroupId), persisterTxn);
            knLogger.debug(methodName, " knXDMOSMInfoRequestDTOS   : ", knXDMOSMInfoRequestDTOS);
            respDto.setOperationalValues(knXDMOSMInfoRequestDTOS);
            Collection<String> actions = new ArrayList<>();
            actions.add(mcpttEmCall!=null?"true":"false");
            actions.add(mcpttEmAl!=null?"true":"false");
            respDto.setActions(actions);
            respDto.setMdn(subsProfile.getMdn());
            respDto.setEtag(System.currentTimeMillis());
            mcsxcapRespDTO.setGroupDocRespDTO(respDto);
        } catch (KnAASException aex) {
            knLogger.error(methodName, "Authorization Exception occured :" + aex);
            throw aex;
        } catch (KnPubBOException ex) {
            knLogger.error(methodName, "PubBO Exception occured : " + ex);
            throw ex;
        }
        catch (KnValidationException vex) {
            knLogger.error(methodName, "Validation Exception occurred :" + vex);
            throw new KnXDMServerException(vex.getErrorCode(), vex.getErrorMessage());
        }
        catch (Exception ex) {
            knLogger.error(methodName, "Exception occured while retrieving  info: " + ex);
            throw new KnSystemException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Unexpected exception occurred while retrieving getMCSGroupDoc info ", ex);
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
