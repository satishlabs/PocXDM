/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.business.impl;

import com.kodiak.common.commdto.common.knXDMUserDirInfoDTO;
import com.kodiak.xdms.server.pubmgmt.dto.common.KnMCSXCAPRespDTO;
import com.kodiak.common.commdto.response.KnXDMMCUserDirRespDTO;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.exception.KnSystemException;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.KnXDMServerException;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dao.KnFactorySelector;
import com.kodiak.xdms.server.common.dao.persister.IXDMServerDAO;
import com.kodiak.xdms.server.common.dto.common.KnAPNProfileInfoDTO;
import com.kodiak.xdms.server.common.dto.common.KnSubsProfileDTO;
import com.kodiak.xdms.server.common.dto.persistdat.KnCorpUserProfileDTO;
import com.kodiak.xdms.server.common.dto.persistdat.KnSubscriberPersistDTO;
import com.kodiak.xdms.server.common.framework.aas.KnAASException;
import com.kodiak.xdms.server.common.framework.aas.KnAASFramework;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorFramework;
import com.kodiak.xdms.server.common.resources.KnConstants;
import com.kodiak.xdms.server.common.util.KnGeneralUtil;
import com.kodiak.xdms.server.pubmgmt.business.IPubMCSUserDirController;
import com.kodiak.xdms.server.pubmgmt.business.KnPubBOException;
import com.kodiak.xdms.server.pubmgmt.business.helper.KnPubInfoUtil;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnIPMCSDTO;
import com.kodiak.xdms.server.pubmgmt.dto.persistdat.KnPubMCSXCAPPersistDTO;
import com.kodiak.xdms.server.pubmgmt.resources.KnErrorCodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME;
import static com.kodiak.common.resources.KnConstants.TRUE;
import static com.kodiak.xdms.server.common.resources.KnConstants.CRI_DEFAULT_USER_AGENT;

public class KnPubMCUserDirController implements IPubMCSUserDirController {
    private static final KnLogger knLogger = KnLogger.getLogger(KnPubMCUserDirController.class);
    private KnValidatorFramework validatorFwk = null;
    private KnAASFramework authorizationFwk = null;
    private KnPubInfoUtil pubInfoUtil = null;
    private KnGenInfoUtil genInfoUtil = null;

    public KnPubMCUserDirController() {
        validatorFwk = KnValidatorFramework.getInstance(KnConstants.LIBRARY_NAME_PUB_MGMT);
        authorizationFwk = KnAASFramework.getInstance(KnConstants.LIBRARY_NAME_PUB_MGMT);
        pubInfoUtil = new KnPubInfoUtil();
        genInfoUtil = KnGenInfoUtil.getInstance();
    }

    @Override
    public KnMCSXCAPRespDTO getMCSUserDir(KnIPMCSDTO ipmcsdto, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        final String methodName = "getMCSUserDir(KnIPMCSDTO, persisterTxn)";
        knLogger.debug(methodName, "ENTRY -> Input DTO Passed : " + ipmcsdto);
        KnMCSXCAPRespDTO mcsxcapRespDTO = new KnMCSXCAPRespDTO();
        KnXDMMCUserDirRespDTO respDTO = new KnXDMMCUserDirRespDTO();
        List<knXDMUserDirInfoDTO> userDirInfoDTOS = new ArrayList<>();
        try {
            IXDMServerDAO xdmDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).
                    createXDMServerDAO(genInfoUtil.retrieveLocalXDMPttServerId());
            int clusterId = Integer.parseInt(System.getenv(CLUSTERID_ENV_NAME));
            KnSubsProfileDTO subsProfile = pubInfoUtil.getProfileDetailsByMcIDAndUpmIndex(ipmcsdto.getMcId(),"0", persisterTxn);
            Map<String, String> microSvcCommonMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
            //conditions added for cri and non-cri client where user agent is not available in request
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
            //  Authorizing the subscriber
            String pv = KnGeneralUtil.validateAndFetchDetailsFromUA(ipmcsdto.getUserAgent());
            int protocolVersion = pv != null ? Integer.parseInt(pv) : 0;
            KnSubscriberPersistDTO originator = new KnSubscriberPersistDTO();
            originator.setMdn(subsProfile.getMdn());
            originator.setMcpttID(subsProfile.getMcpttId());
            originator.setMcpttCompliance(subsProfile.getMcpttCompliance());
            originator.setNetworkName(subsProfile.getNetworkName());
            originator.setPubSubscriptionType(subsProfile.getPublicSubscriptionType());
            originator.setServiceAuthStatus(subsProfile.getServiceAuthStatus());
            KnPubMCSXCAPPersistDTO pubMCSXCAPPersistDTO = new KnPubMCSXCAPPersistDTO();
            pubMCSXCAPPersistDTO.setInputDTO(ipmcsdto);
            pubMCSXCAPPersistDTO.setPersistenceDTO(originator);
            pubMCSXCAPPersistDTO.setProtocolVersion(protocolVersion);
            knLogger.debug(methodName, "Invoking Authorization.");
            //  Authorizing the subscriber
            authorizationFwk.authorize(pubMCSXCAPPersistDTO);
            knLogger.debug(methodName, "Authorized successfully.");
            //business validation
            knLogger.debug(methodName, "validating dto ", pubMCSXCAPPersistDTO);
            validatorFwk.validate(pubMCSXCAPPersistDTO);
            knLogger.debug(methodName, "Validated successfully!");

            List<KnSubsProfileDTO> subsProfileList = pubInfoUtil.getProfileDetailsListByMcID(ipmcsdto.getMcId(), persisterTxn);
            //getting fetched value
            for (KnSubsProfileDTO subsProfiles : subsProfileList) {
                knXDMUserDirInfoDTO respUserDir = new knXDMUserDirInfoDTO();
                if(subsProfiles.getUserProfileIndex()!=0){
                    /*KnCorpUserProfileDTO userProfile = pubInfoUtil.getUserProfileNameByindex(subsProfiles.getCorpId(), subsProfiles.getUserProfileIndex(), persisterTxn);
                    if(userProfile!=null)
                        respUserDir.setUserProfileName(userProfile.getUserProfileName());*/
                    String profileName = xdmDAO.selectUserProfileName(subsProfiles.getMdn(), TRUE, persisterTxn);
                    if(profileName != null)
                        respUserDir.setUserProfileName(profileName);

                }else{
                    //default profile name
                    Map<String, String> msSvcConfigDocMap = genInfoUtil.retrieveMSSvcsServiceConfig(clusterId, persisterTxn);
                    String defaultUserProfileName = msSvcConfigDocMap.get("MCPTT_USER_PROFILE_NAME");
                    respUserDir.setUserProfileName(defaultUserProfileName);
                }
                respUserDir.setUserProfileEtag(toString().valueOf(subsProfiles.getProfileLastUpdated()));
                respUserDir.setUserProfileIndex(subsProfiles.getUserProfileIndex());
                respUserDir.setMcId(subsProfiles.getMcId());
                respUserDir.setDefaultExtM(subsProfiles.getDefaultProfile());
                userDirInfoDTOS.add(respUserDir);
            }
            //xcap root uri
            String apnName = genInfoUtil.getDefaultAPNName(persisterTxn);
            Integer apnId = genInfoUtil.getAPNId(apnName, persisterTxn);
            KnAPNProfileInfoDTO knAPNProfileInfoDTO = xdmDAO.retrieveAPNProfileInfo(genInfoUtil.retrieveLocalXDMPttServerId(), apnId, persisterTxn);
            knLogger.debug(methodName, "knAPNProfileInfoDTO :", knAPNProfileInfoDTO, "apnName ", apnName, " apnId ", apnId);
            //setting response
            respDTO.setXcapRootUri(knAPNProfileInfoDTO.getXcaprooturi());
            respDTO.setUserDirInfoDTOS(userDirInfoDTOS);
            mcsxcapRespDTO.setUserDirRespDTO(respDTO);
        } catch (KnAASException aex) {
            knLogger.error(methodName, "Authorization Exception occurred :" + aex);
            throw aex;
        } catch (KnPubBOException ex) {
            knLogger.error(methodName, "PubBO Exception occurred : " + ex);
            throw ex;
        } catch (KnValidationException vex) {
            knLogger.error(methodName, "Validation Exception occurred :" + vex);
            throw new KnXDMServerException(vex.getErrorCode(), vex.getErrorMessage());
        } catch (Exception ex) {
            knLogger.error(methodName, "Exception occurred while retrieving  info: " + ex);
            throw new KnSystemException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Unexpected exception occurred while retrieving MCVideoUserProfile info ", ex);
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
