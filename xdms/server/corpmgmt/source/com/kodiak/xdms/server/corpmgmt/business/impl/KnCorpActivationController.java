/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnCorpActivationController.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Sanjiv Acharyya      Nov 28, 2011      7.2
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

package com.kodiak.xdms.server.corpmgmt.business.impl;

import com.kodiak.common.commdto.common.KnIdValue;
import com.kodiak.common.commdto.common.KnXDMCorpActivationDTO;
import com.kodiak.common.commdto.common.KnXDMDeviceProvDTO;
import com.kodiak.common.commdto.request.KnSMSDTO;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.exception.KnException;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnConstants.HIERARCHY_TYPE;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.processinvoker.impl.KnProcessInvokerImpl;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dao.KnFactorySelector;
import com.kodiak.xdms.server.common.dao.persister.IXDMServerDAO;
import com.kodiak.xdms.server.common.dto.common.KnActivationCodeConfigDTO;
import com.kodiak.xdms.server.common.dto.common.KnCorpProfileDTO;
import com.kodiak.xdms.server.common.dto.common.KnSubsProfileDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorFramework;
import com.kodiak.xdms.server.common.resources.KnProfileTypes;
import com.kodiak.xdms.server.common.util.KnSMSUtil;
import com.kodiak.xdms.server.corpmgmt.business.ICorpActivationInfoController;
import com.kodiak.xdms.server.corpmgmt.business.KnCorpBOException;
import com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpActivationInfoUtil;
import com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpCommonInfoUtil;
import com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpContactInfoUtil;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpActivationDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpMailInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpActivationRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.hook.KnCorpHookIPDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.hook.KnCorpHookRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpActivationPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGenActivationPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpMdnListPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnMdnDetailsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnActions;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.sql.Timestamp;
import java.util.*;

import static com.kodiak.xdms.server.common.resources.KnConstants.LIBRARY_NAME_CORP_MGMT;
import static com.kodiak.xdms.server.common.resources.KnProfileTypes.CORP_PROFILE;
import static com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpCommonInfoUtil.populate;
import static com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpCommonInfoUtil.populateXdmResponseFroomHook;

public class KnCorpActivationController implements ICorpActivationInfoController {
	private static final KnLogger knLogger = KnLogger.getLogger(KnCorpActivationController.class);

    private KnValidatorFramework validatorFW;
    private KnCorpCommonInfoUtil commonInfoUtil;
    private KnCorpContactInfoUtil contactInfoUtil;
    private KnCorpActivationInfoUtil activationInfoUtil;
    private KnGenInfoUtil genInfoUtil;


    public KnCorpActivationController() {
        commonInfoUtil = new KnCorpCommonInfoUtil();
        contactInfoUtil = new KnCorpContactInfoUtil();
        activationInfoUtil = new KnCorpActivationInfoUtil();
        validatorFW = KnValidatorFramework.getInstance(LIBRARY_NAME_CORP_MGMT);
        genInfoUtil = KnGenInfoUtil.getInstance();
    }

    public KnCorpActivationRespDTO getSubscribersEmailId(KnIPCorpActivationDTO activationDTO, KnPersisterTxn persisterTxn) {
        String methodName = "getSubscribersEmailId(KnIPCorpActivationDTO, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed activationDTO - ", activationDTO.getCorpId(), " , ", activationDTO.getMdn());
        KnCorpActivationRespDTO respDTO = new KnCorpActivationRespDTO();
        try {
            int corpId = activationDTO.getCorpId();
            String mdn = activationDTO.getMdn();
            // Step:
            // get the subscriber profile details.
            // get email from the subsc profile
            // get Masterlist etag
            // validate email and masterlist etag.
            knLogger.debug( methodName, "Fetch the subscriber profile ");
            KnSubsProfileDTO subscProfile = commonInfoUtil.getProfileDetails(mdn, KnProfileTypes.PUBLIC_PROFILE,
                    false, persisterTxn);
            knLogger.debug( methodName, "Subscriber profile - ", subscProfile);
            String email = subscProfile.getSubscriberEmail();
            int serviceAuthStatus = subscProfile.getServiceAuthStatus();
            if(serviceAuthStatus == com.kodiak.xdms.server.common.resources.KnConstants.SERVICE_AUTH_STATUS.ACTIVATED.value()){
              respDTO.setReActivationCount(1);
            }
            int clientType = subscProfile.getClientType();
            String xdmsHomePttId = subscProfile.getXdmsHome();
            int pamAccId = subscProfile.getPamAccId();
            knLogger.debug( methodName, "Fetch the corpProfile profile if cached or fetch from the DB the details");
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(corpId),
                    CORP_PROFILE, false, persisterTxn);
            knLogger.debug( methodName, "Retrieved Corp Profile details - ", corpProfile);

            //Invoking custom hook
            KnCorpHookRespDTO responseDTO = new KnCorpHookRespDTO();
            Map<String, Object> customParams = activationDTO.getCustomParamMap();
            if (activationDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY && customParams != null) {
                contactInfoUtil.checkValidHierarchySubs(Collections.singletonList(mdn), customParams, corpProfile.getXdmsHome(), persisterTxn);
                customParams.put(KnConstants.CURRENT_MASTER_LIST_ETAG, corpProfile.getCorpMasterListEtag());
                customParams.put(KnConstants.PERSISTER_TXN, persisterTxn);
                customParams.put(KnConstants.PTT_SERVER_ID, xdmsHomePttId);
                customParams.put(KnConstants.SUBSCRIBER_MDN, mdn);
                activationDTO.setCustomParamMap(customParams);
                KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
                hookIPDTO.setAction(KnActions.ACTIONS.GET_SUBS_EMAIL_ID);
                hookIPDTO.setData(activationDTO);
                KnProcessInvokerImpl processInvoker = KnProcessInvokerImpl.getInstance();
                Map<String, Object> customRespMap = null;
                knLogger.debug( methodName, "customParams Before hook invokation - ", customParams);
                Object hookResp = processInvoker.invokeHook(KnConstants.CUSTOM_CORP_USER_HOOK, hookIPDTO);
                if (hookResp != null && hookResp instanceof KnCorpHookRespDTO) {
                    responseDTO = (KnCorpHookRespDTO) hookResp;
                    customRespMap = responseDTO.getCustomParamMap();
                    if (com.kodiak.xdms.server.common.resources.KnConstants.RESPONSE_STATUS.FAILURE.value() == responseDTO.getStatus()) {
                        knLogger.error( methodName, "Returning Failure response");
                        populateXdmResponseFroomHook(responseDTO, respDTO);
                        return respDTO;
                    }
                }
                knLogger.debug( methodName, "customRespMap after hook invokation - ", customRespMap);
                respDTO.setCustomParamMap(customRespMap);
            }
            long corpEtag = corpProfile.getCorpMasterListEtag();
            // get the count of MDN and corpid for validation if mdn exist in the corporation
            int subscribersCount = contactInfoUtil.getSubscribersCount(mdn, corpId, true, xdmsHomePttId, persisterTxn);
            int clusterId = Integer.parseInt(System.getenv(com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME));
            Map<String, String> microServicesParamNameValueMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
            knLogger.debug(methodName, "param value of commonConfig", microServicesParamNameValueMap);
            String deviceSharingFlag = microServicesParamNameValueMap.get(com.kodiak.xdms.server.corpmgmt.resources.KnConstants.DEVICE_SHARING_FEATURE_FLAG);
            KnCorpActivationPersistDTO persistDTO = new KnCorpActivationPersistDTO();
            persistDTO.setServiceAuthStatus(serviceAuthStatus);
            persistDTO.setSubscriberCount(subscribersCount);
            persistDTO.setInputDTO(activationDTO);
            persistDTO.setClientType(clientType);
            persistDTO.setDispatchType(subscProfile.getDispatchType());
            persistDTO.setDispatchTypeEnabled(true);
            persistDTO.setLicenseType(subscProfile.getLicenseType());
            persistDTO.setDeviceSharingFlag(Integer.parseInt(deviceSharingFlag));
            persistDTO.setClientPVMajorVersion(subscProfile.getClientMajorVersion());
            knLogger.debug( methodName, "Invoking ValidationFW. DTO - ", persistDTO);
            validatorFW.validate(persistDTO);
            knLogger.debug( methodName, "Validation completed Successfully.");

            // introduced client interface also in activation path. for more info refer 8.1 TPMS SDD
            int clientIntf = this.getClientInterface(activationDTO);
            knLogger.info(methodName, "interface recieved - ", clientIntf);

            //Get the activation code configuration
            Map<Integer, KnActivationCodeConfigDTO> activationCodeConfigMap = new HashMap<>(1);
            activationCodeConfigMap.put(clientType, genInfoUtil.getActivationCodeConfig(clientType, clientIntf, persisterTxn));
            int actCodeLen = activationCodeConfigMap.get(clientType).getActCodeLength();

            if (actCodeLen < 7 || actCodeLen > 32) {
                throw new KnCorpBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Invalid activation key length");
            }

            //updating TP clients as used if mdns activation code generating via CAT.  please refer 8.1 part4 SDD for more info "rqPOC_3rdParty_usecase_4.1".
            // in PAM pool usage only TP and Mobile clients are available. hence irrespective of mdns client type we are updating for all.
            if (clientIntf == KnConstants.CLIENT_INTF_CAT) {
            	List<String> mdnList = new ArrayList<>();
            	mdnList.add(mdn);
            	IXDMServerDAO commonXDMServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmsHomePttId);
            	int resCount = commonXDMServerDAO.updateUsageByMDNs(mdnList, com.kodiak.xdms.server.common.resources.KnConstants.PAMACCOUNT_POOL_USAGE.IN_USE.value(), persisterTxn);
            	knLogger.info(methodName, "updated pam TP pool mdns as used, count - ", resCount);
            }

            Collection<KnCorpSubscriberDTO> subscList = new ArrayList<>();
            KnCorpSubscriberDTO subs = new KnCorpSubscriberDTO();
            subs.setMdn(mdn);
            subs.setClientType(clientType);
            subscList.add(subs);
            //Generate the activation code and set into the subscriber DTO
            Map<String, String> activationCodeMap = activationInfoUtil.generateActivationCode(subscList, activationCodeConfigMap);
            if (activationDTO.getTempPwd() != null) {
                activationCodeMap.put(mdn, activationDTO.getTempPwd());
            }
            knLogger.debug( methodName, "activationCodeSet - ", activationCodeMap);
            subs.setActivationCode(activationCodeMap.get(mdn));
            //Generate the expiry and current timestamp and set into the subscriber dto.
            Map<Integer, Timestamp> expiryTimeMap = activationInfoUtil.generateExpiryTime(activationCodeConfigMap);
            Timestamp currentTimeInUTC = activationInfoUtil.getCurrentTimeInUTC();
            subs.setExpiryTime(expiryTimeMap.get(clientType));
            subs.setActivationTimestamp(currentTimeInUTC);


            // check if activation code exist in DB. If exist update the generated activation code.
            Set<String> existingActCodeList = activationInfoUtil.isActivationCodeExistInDB(new HashSet<>(activationCodeMap.keySet()), xdmsHomePttId, true, persisterTxn);
            knLogger.debug( methodName, "existingActCodeList - ", existingActCodeList);
            if (existingActCodeList != null && !existingActCodeList.isEmpty()) {
                activationCodeMap.putAll(activationInfoUtil.generateActivationCode(subscList, activationCodeConfigMap));
                if (activationDTO.getTempPwd() != null) {
                    activationCodeMap.put(mdn, activationDTO.getTempPwd());
                }
                subs.setActivationCode(activationCodeMap.get(mdn));
            }

            //MINT-17052
            boolean deviceExists = genInfoUtil.checkDeviceExsists(mdn, persisterTxn);
            if (deviceExists) {
                genInfoUtil.updateSipDigestPwd(mdn, null, persisterTxn);
            }

            int operationType;
            //Check if activation code exist for the MDN, if exist update into DG.TMPVASSUBSCRIPTIONKEYINFO table.
            Collection<KnCorpSubscriberDTO> mdnList = activationInfoUtil.isActivationCodeExistForMDN(subscList, xdmsHomePttId, true, persisterTxn);
            if (mdnList != null && !mdnList.isEmpty()) {
                knLogger.debug( methodName, "Updating the existing activation code - ");
                Collection<KnCorpSubscriberDTO> failedMdnList = activationInfoUtil.updateActivationCode(mdnList, xdmsHomePttId, persisterTxn);
                operationType = 1;
                updateUniqueActCode(failedMdnList, xdmsHomePttId, persisterTxn, operationType, activationCodeConfigMap, activationCodeMap);

            } else {
                // Insert activation code in to DG.TMPVASSUBSCRIPTIONKEYINFO table.
                knLogger.debug( methodName, "Inserting the activation code.. ");
                operationType = 2;
                Collection<KnCorpSubscriberDTO> failedMdnList = activationInfoUtil.insertActivationCode(subscList, null, xdmsHomePttId, persisterTxn);
                updateUniqueActCode(failedMdnList, xdmsHomePttId, persisterTxn, operationType, activationCodeConfigMap,activationCodeMap);
            }

            knLogger.debug(methodName,"updated activation code map::",activationCodeMap);
            respDTO.setActivationCode(activationCodeMap.get(mdn));

            Map<String, Object> custParamMap = activationDTO.getCustomParamMap();
            if (custParamMap != null) {
                custParamMap.put(KnConstants.CURRENT_MASTER_LIST_ETAG, corpEtag);
                activationDTO.setCustomParamMap(custParamMap);
            }
            KnCorpSubscriberDTO corpSubscriberDTO = activationInfoUtil.getActCodeExtTime(mdn, clientType, xdmsHomePttId, true, persisterTxn);
            if(null != corpSubscriberDTO){
                respDTO.setExpiryTimestamp(String.valueOf(corpSubscriberDTO.getExpiryTime().getTime()));
                respDTO.setActivationTimestamp(String.valueOf(corpSubscriberDTO.getActivationTimestamp().getTime()));
            } else{
                respDTO.setExpiryTimestamp(String.valueOf(expiryTimeMap.get(clientType).getTime()));
                respDTO.setActivationTimestamp(String.valueOf(currentTimeInUTC.getTime()));
            }

            KnCorpMailInfoDTO mailInfoDTO = new KnCorpMailInfoDTO();
            knLogger.debug( methodName, "Email - ", KnGDPRTemplate.email(email), " , clientType - ", subscProfile);
            mailInfoDTO.setTo(email);
            respDTO.setMailInfoDTO(mailInfoDTO);
            respDTO.setClientType(clientType);
            respDTO.setSubscrName(subscProfile.getNetworkName());
            respDTO.setPamAccId(pamAccId);
            populate(respDTO);
            if (responseDTO.getFailedDataList() != null && !responseDTO.getFailedDataList().isEmpty()) {
                respDTO.setFailedDataList(responseDTO.getFailedDataList());
            }

        } catch (KnValidationException e) {
            knLogger.error( methodName, "KnValidationException occured while generating activation code - ", e);
            populate(respDTO, e);
        } catch (KnCorpBOException e) {
            knLogger.error( methodName, "KnCorpBOException occured while generating activation code - ", e);
            populate(respDTO, e);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while generating activation code - ",
                    new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDTO, e);
        }
        knLogger.debug( methodName, "Returning Response - ", respDTO);
        return respDTO;
    }

    public KnCorpActivationRespDTO generateActivationCodes(KnIPCorpActivationDTO activationDTO, KnPersisterTxn persisterTxn) {
        String methodName = "generateActivationCodes(KnIPCorpActivationDTO, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed activationDTO - ", activationDTO.getCorpId(), " , " +
                "mdnList - ", KnGDPRTemplate.mdnList(activationDTO.getMdnList()));
        //g
        KnCorpActivationRespDTO respDTO = new KnCorpActivationRespDTO();
        try {
            int corpId = activationDTO.getCorpId();
            Collection<String> mdnList = activationDTO.getMdnList();
            // Step:
            // get the subscriber profile details.
            // get email from the subsc profile
            // get Masterlist etag
            // validate auth status, and masterlist etag.

            knLogger.debug( methodName, "Fetch the corpProfile profile if cached or fetch from the DB the details");
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(corpId),
                    CORP_PROFILE, false, persisterTxn);
            knLogger.debug( methodName, "Retrieved Corp Profile details - ", corpProfile);
            int maxMembersAllowed = corpProfile.getMaxSubsAllowedGenActvReq();
            String xdmsHomePttId = corpProfile.getXdmsHome();
            KnCorpMdnListPersistDTO corpMdnListPersistDto = new KnCorpMdnListPersistDTO();
            corpMdnListPersistDto.setCorpId(corpId);
            corpMdnListPersistDto.setAddedMdnList(mdnList);
            KnMdnDetailsPersistDTO pocMdnPersistDto = contactInfoUtil.getPoCSubscriberInfo(corpMdnListPersistDto,
                    xdmsHomePttId, persisterTxn);
            Collection<String> pocMdnList = pocMdnPersistDto.getMdnList();
            Collection<KnCorpSubscriberDTO> extMdnList = pocMdnPersistDto.getExternalMdnList();
            knLogger.debug( methodName, "pocMdnList- ", KnGDPRTemplate.mdnList(pocMdnList));
            //get the subscriber client_type, auth status.  filter out the external contacts
            Collection<KnCorpSubscriberDTO> subscProfile = contactInfoUtil.getSubscriberProfileInfo(mdnList, corpId, xdmsHomePttId, false, persisterTxn);

            //Invoking custom hook
            KnCorpHookRespDTO responseDTO = new KnCorpHookRespDTO();
            Map<String, Object> customParams = activationDTO.getCustomParamMap();
            if (activationDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
                contactInfoUtil.checkValidHierarchySubs((List<String>) mdnList, customParams, xdmsHomePttId, persisterTxn);
                customParams.put(KnConstants.CURRENT_MASTER_LIST_ETAG, corpProfile.getCorpMasterListEtag());
                customParams.put(KnConstants.PERSISTER_TXN, persisterTxn);
                customParams.put(KnConstants.PTT_SERVER_ID, xdmsHomePttId);
                activationDTO.setCustomParamMap(customParams);
                KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
                hookIPDTO.setAction(KnActions.ACTIONS.GENERATE_ACTIVATION_CODES);
                hookIPDTO.setData(activationDTO);
                KnProcessInvokerImpl processInvoker = KnProcessInvokerImpl.getInstance();
                Map<String, Object> customRespMap = null;
                knLogger.debug( methodName, "customParams Before hook invokation - ", customParams);
                Object hookResp = processInvoker.invokeHook(KnConstants.CUSTOM_CORP_USER_HOOK, hookIPDTO);
                if (hookResp != null && hookResp instanceof KnCorpHookRespDTO) {
                    responseDTO = (KnCorpHookRespDTO) hookResp;
                    customRespMap = responseDTO.getCustomParamMap();
                    if (com.kodiak.xdms.server.common.resources.KnConstants.RESPONSE_STATUS.FAILURE.value() == responseDTO.getStatus()) {
                        knLogger.error( methodName, "Returning Failure response");
                        populateXdmResponseFroomHook(responseDTO, respDTO);
                        return respDTO;
                    }
                }
                knLogger.debug( methodName, "customRespMap after hook invokation - ", customRespMap);
                respDTO.setCustomParamMap(customRespMap);
            }
            long corpEtag = corpProfile.getCorpMasterListEtag();
            int clusterId = Integer.parseInt(System.getenv(com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME));
            Map<String, String> microServicesParamNameValueMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
            //knLogger.debug(methodName, "param value of commonConfig", microServicesParamNameValueMap);
            String deviceSharingFlag = microServicesParamNameValueMap.get(com.kodiak.xdms.server.corpmgmt.resources.KnConstants.DEVICE_SHARING_FEATURE_FLAG);
            KnCorpGenActivationPersistDTO persistDTO = new KnCorpGenActivationPersistDTO();
            persistDTO.setSubscriberAuthStatusList(subscProfile);
            persistDTO.setPocMdnList(pocMdnList);
            persistDTO.setExtMdnList(extMdnList);
            persistDTO.setSubsCount(mdnList.size());
            persistDTO.setInputDTO(activationDTO);
            persistDTO.setMaxMembersAllowed(maxMembersAllowed);
            persistDTO.setDeviceSharingFlag(Integer.parseInt(deviceSharingFlag));
           //persistDTO.setSubscriberInfoList(pocMdnPersistDto.getAddedMdnDTO());
            // variable to hold number of reactivation count which is used for peg increment (16951)
            int reActCount = 0;
            //variable to hold the distinct client types.
            Set<Integer> clientTypes = new HashSet<>();
            for (KnCorpSubscriberDTO subscriberDTO : subscProfile) {
                clientTypes.add(subscriberDTO.getClientType());
                if (subscriberDTO.getServiceAuthStatus() == com.kodiak.xdms.server.common.resources.KnConstants.SERVICE_AUTH_STATUS.ACTIVATED.value()) {
                    reActCount++;
                }
            }
            respDTO.setReActivationCount(reActCount);
            knLogger.debug( methodName, "Invoking ValidationFW. DTO - ", persistDTO);
            validatorFW.validate(persistDTO);
            knLogger.debug( methodName, "Validation completed Successfully.");

            // introduced client interface also in activation path. for more info refer 8.1 TPMS SDD
            int clientIntf = this.getClientInterface(activationDTO);
            knLogger.info(methodName, "interface recieved - ", clientIntf);

            //Variable to hold the activation code config for each client types.
            Map<Integer, KnActivationCodeConfigDTO> activationCodeConfigMap = new HashMap<>();
            for(int clientType : clientTypes){
                activationCodeConfigMap.put(clientType, genInfoUtil.getActivationCodeConfig(clientType, clientIntf, persisterTxn));
            }

            for(KnActivationCodeConfigDTO actCodeConfig : activationCodeConfigMap.values()){
                if (actCodeConfig.getActCodeLength() < 7 || actCodeConfig.getActCodeLength() > 32) {
                    throw new KnCorpBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Invalid activation key length");
                }
            }

            //updating TP clients as used if mdns activation code generating via CAT.  please refer 8.1 part4 SDD for more info "rqPOC_3rdParty_usecase_4.1".
            // in PAM pool usage only TP and Mobile clients are available. hence irrespective of mdns client type we are updating for all.
            if (clientIntf == KnConstants.CLIENT_INTF_CAT) {
            	List<String> tmpMdnList = (List<String>) mdnList;
            	IXDMServerDAO commonXDMServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmsHomePttId);
            	int resCount = commonXDMServerDAO.updateUsageByMDNs(tmpMdnList, com.kodiak.xdms.server.common.resources.KnConstants.PAMACCOUNT_POOL_USAGE.IN_USE.value(), persisterTxn);
            	knLogger.info(methodName, "updated pam TP pool mdns as used, count - ", resCount);
            }

            Map<String, String> activationCodeMap = activationInfoUtil.generateActivationCode(subscProfile, activationCodeConfigMap);
            knLogger.debug( methodName, "activationCodeMap --- ", activationCodeMap);
            // get the current time in UTC format and use this while inserting into DB as insertion time.
            Timestamp currentTimeInUTC = activationInfoUtil.getCurrentTimeInUTC();
            Set<String> existingActCodeList = activationInfoUtil.isActivationCodeExistInDB(new HashSet<>(activationCodeMap.values()), xdmsHomePttId, false, persisterTxn);
            knLogger.debug( methodName, "existingActCodeList - ", existingActCodeList);
            Collection<KnCorpSubscriberDTO> duplicateCodeMdnList = activationInfoUtil.getDuplicateCodeMdnList(subscProfile, activationCodeMap, existingActCodeList);
            activationCodeMap.putAll(activationInfoUtil.generateActivationCode(duplicateCodeMdnList, activationCodeConfigMap));
            //generate the expiry time for each client type.
            Map<Integer, Timestamp> expiryTimeMap = activationInfoUtil.generateExpiryTime(activationCodeConfigMap);


            List<KnCorpSubscriberDTO> subsActivationCodeList = new ArrayList<>();
            for (KnCorpSubscriberDTO subscriberDTO : subscProfile) {
                String mdn = subscriberDTO.getMdn();
                KnCorpSubscriberDTO corpSubscriberDTO = new KnCorpSubscriberDTO();
                corpSubscriberDTO.setClientType(subscriberDTO.getClientType());
                corpSubscriberDTO.setServiceAuthStatus(subscriberDTO.getServiceAuthStatus());
                corpSubscriberDTO.setActivationCode(activationCodeMap.get(mdn));
                corpSubscriberDTO.setExpiryTime(expiryTimeMap.get(subscriberDTO.getClientType()));
                corpSubscriberDTO.setMdn(mdn);
                corpSubscriberDTO.setSubsActiveFS2(subscriberDTO.getSubsActiveFS2());
                corpSubscriberDTO.setActivationTimestamp(currentTimeInUTC);
                subsActivationCodeList.add(corpSubscriberDTO);
            }
            knLogger.debug( methodName, " subsActivationCodeList --- ", subsActivationCodeList);
            Collection<KnCorpSubscriberDTO> subscList = activationInfoUtil.isActivationCodeExistForMDN(subsActivationCodeList, xdmsHomePttId, false, persisterTxn);

            Iterator<KnCorpSubscriberDTO> itr = subsActivationCodeList.iterator();
            Map<String, String> mdnActivefsMap = new HashMap<>();
            while(itr.hasNext()){
                KnCorpSubscriberDTO dto = itr.next();
                mdnActivefsMap.put(dto.getMdn(),dto.getSubsActiveFS2());
                }

            //filter data to be updated
            subsActivationCodeList.removeAll(subscList);


            //MINT-17052
            for (KnCorpSubscriberDTO corpSubscriberDTO : subscList) {
                boolean deviceExists = genInfoUtil.checkDeviceExsists(corpSubscriberDTO.getMdn(), persisterTxn);
                if (deviceExists) {
                    genInfoUtil.updateSipDigestPwd(corpSubscriberDTO.getMdn(), null, persisterTxn);
                }
            }

            int operationType;
            if (subscList != null && !subscList.isEmpty()) {
                knLogger.debug( methodName, "Updating the activation code ---. ", subscList);
                Collection<KnCorpSubscriberDTO> failedMdnList = activationInfoUtil.updateActivationCode(subscList, xdmsHomePttId, persisterTxn);
                knLogger.debug( methodName, "Updating the activation code ---. ", subsActivationCodeList);
                operationType = 1;
                updateUniqueActCode(failedMdnList, xdmsHomePttId, persisterTxn, operationType, activationCodeConfigMap, activationCodeMap);
            }
            if (!subsActivationCodeList.isEmpty()) {
                knLogger.debug( methodName, "List before insertion - ", subsActivationCodeList);
                // Insert activation code in to DG.TMPVASSUBSCRIPTIONKEYINFO table.
                knLogger.info( methodName, "Inserting the activation code.. ");
                operationType = 2;
                Collection<KnCorpSubscriberDTO> failedMdnList = activationInfoUtil.insertActivationCode(subsActivationCodeList, null, xdmsHomePttId, persisterTxn);
                updateUniqueActCode(failedMdnList, xdmsHomePttId, persisterTxn, operationType, activationCodeConfigMap, activationCodeMap);
            }

            Collection<KnXDMCorpActivationDTO> activationCodeList = new ArrayList<KnXDMCorpActivationDTO>();
            Map<String, KnIPCorpActivationDTO> activationCodeDetailsMap = contactInfoUtil.getActivationCodeForCorpoateSubscriber(mdnList, xdmsHomePttId, false, persisterTxn);
            for (Map.Entry<String, KnIPCorpActivationDTO> entry : activationCodeDetailsMap.entrySet()) {
                String mdn = entry.getKey();
                KnIPCorpActivationDTO actDTO = entry.getValue();
                KnXDMCorpActivationDTO corpActivationDTO = new KnXDMCorpActivationDTO();
                corpActivationDTO.setActivationCode(actDTO.getActivationCode());
                corpActivationDTO.setMdn(mdn);
                Timestamp exTime = actDTO.getExpiryTimestamp();
                if (exTime != null) {
                    exTime.getTime();
                    corpActivationDTO.setExpiryTimestamp(String.valueOf(exTime.getTime()));
                }
                Timestamp activationTime = actDTO.getActivationTimestamp();
                if (activationTime != null) {
                    corpActivationDTO.setActivationTimestamp(String.valueOf(activationTime.getTime()));
                }
                corpActivationDTO.setActiveFs1(KnGeneralUtil.convertHexStringToLong(mdnActivefsMap.get(mdn)));
                corpActivationDTO.setActiveFs2(mdnActivefsMap.get(mdn));
                activationCodeList.add(corpActivationDTO);
            }
            respDTO.setActivationCodeList(activationCodeList);

            Map<String, Object> custParamMap = activationDTO.getCustomParamMap();
            if (custParamMap != null) {
                custParamMap.put(KnConstants.CURRENT_MASTER_LIST_ETAG, corpEtag);
                activationDTO.setCustomParamMap(custParamMap);
            }
            populate(respDTO);
            if (responseDTO.getFailedDataList() != null && !responseDTO.getFailedDataList().isEmpty()) {
                respDTO.setFailedDataList(responseDTO.getFailedDataList());
            }

        } catch (KnValidationException e) {
            knLogger.error( methodName, "KnValidationException occured while generating activation code - ", e);
            populate(respDTO, e);
        } catch (KnCorpBOException e) {
            knLogger.error( methodName, "KnCorpBOException occured while generating activation code - ", e);
            populate(respDTO, e);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while generating activation code - ",
                    new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDTO, e);
        } finally {
            knLogger.debug( methodName, "Returning Response - ", respDTO);
            knLogger.info( methodName, "EXIT:");
        }
        return respDTO;
    }

    public KnCorpActivationRespDTO saveClientActivationMail(KnIPCorpActivationDTO activationDTO, KnPersisterTxn persisterTxn) {
        String methodName = "saveClientActivationMail(KnIPCorpActivationDTO, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed activationDTO - ", activationDTO.getCorpId(), " , ", activationDTO.getMdn());
        KnCorpActivationRespDTO respDTO = new KnCorpActivationRespDTO();
        try {
//            KnCorpMailInfoDTO mailInfoDTO = activationDTO.getMailInfoDto();
            String mdn = activationDTO.getMdn();
            int corpId = activationDTO.getCorpId();
            String activationCode = activationDTO.getActivationCode();
            knLogger.debug( methodName, "Fetch the subscriber profile ");
            KnSubsProfileDTO subscProfile = commonInfoUtil.getProfileDetails(mdn, KnProfileTypes.PUBLIC_PROFILE,
                    false, persisterTxn);
            knLogger.debug( methodName, "Subscriber profile - ", subscProfile);
            int serviceAuthStatus = subscProfile.getServiceAuthStatus();
            String xdmsHomePttId = subscProfile.getXdmsHome();

            knLogger.debug( methodName, "Fetch the corpProfile profile if cached or fetch from the DB the details");
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(corpId),
                    CORP_PROFILE, false, persisterTxn);
            knLogger.debug( methodName, "Retrieved Corp Profile details - ", corpProfile);


            // Step:
            // get Masterlist etag
            // validate masterlist etag in custom hook.
            //Invoking custom hook
            KnCorpHookRespDTO responseDTO = new KnCorpHookRespDTO();
            Map<String, Object> customParams = activationDTO.getCustomParamMap();
            if (activationDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
                customParams.put(KnConstants.CURRENT_MASTER_LIST_ETAG, corpProfile.getCorpMasterListEtag());
                customParams.put(KnConstants.PERSISTER_TXN, persisterTxn);
                customParams.put(KnConstants.PTT_SERVER_ID, xdmsHomePttId);
                activationDTO.setCustomParamMap(customParams);
                KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
                hookIPDTO.setAction(KnActions.ACTIONS.SAVE_SUBS_ACTIVATION_CODE);
                hookIPDTO.setData(activationDTO);
                KnProcessInvokerImpl processInvoker = KnProcessInvokerImpl.getInstance();
                Object hookResp = processInvoker.invokeHook(KnConstants.CUSTOM_CORP_USER_HOOK, hookIPDTO);
                if (hookResp != null && hookResp instanceof KnCorpHookRespDTO) {
                    responseDTO = (KnCorpHookRespDTO) hookResp;
                    if (com.kodiak.xdms.server.common.resources.KnConstants.RESPONSE_STATUS.FAILURE.value() == responseDTO.getStatus()) {
                        knLogger.error( methodName, "Returning Failure response");
                        populateXdmResponseFroomHook(responseDTO, respDTO);
                        return respDTO;
                    }
                }
            }
            int clientType = subscProfile.getClientType();
            long corpEtag = contactInfoUtil.getCorporateEtag(corpId, xdmsHomePttId, persisterTxn);
            knLogger.debug( methodName, "corpEtag - ", corpEtag);
            int clusterId = Integer.parseInt(System.getenv(com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME));
            Map<String, String> microServicesParamNameValueMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
            //knLogger.debug(methodName, "param value of commonConfig", microServicesParamNameValueMap);
            String deviceSharingFlag = microServicesParamNameValueMap.get(com.kodiak.xdms.server.corpmgmt.resources.KnConstants.DEVICE_SHARING_FEATURE_FLAG);
            KnCorpActivationPersistDTO persistDTO = new KnCorpActivationPersistDTO();
            int subscribersCount = contactInfoUtil.getSubscribersCount(mdn, corpId, xdmsHomePttId, persisterTxn);
            persistDTO.setServiceAuthStatus(serviceAuthStatus);
            persistDTO.setClientType(clientType);
            persistDTO.setSubscriberCount(subscribersCount);
            persistDTO.setInputDTO(activationDTO);
            persistDTO.setLicenseType(subscProfile.getLicenseType());
            persistDTO.setDeviceSharingFlag(Integer.parseInt(deviceSharingFlag));
            persistDTO.setClientPVMajorVersion(subscProfile.getClientMajorVersion());
            knLogger.debug( methodName, "Invoking ValidationFW. DTO - ", persistDTO);
            validatorFW.validate(persistDTO);
            knLogger.debug( methodName, "Validation completed Successfully.");
            // get the current time in UTC format and use this while inserting into DB as insertion time.
            Timestamp currentTimeInUTC = activationInfoUtil.getCurrentTimeInUTC();
            ///knLogger.debug( methodName, "currentTimeInUTC :- " , currentTimeInUTC);

            // introduced client interface also in activation path. for more info refer 8.1 TPMS SDD
            int clientIntf = this.getClientInterface(activationDTO);
            knLogger.info(methodName, "interface recieved - ", clientIntf);

            //Variable to hold the activation code config for each client types.
            Map<Integer, KnActivationCodeConfigDTO> activationCodeConfigMap = new HashMap<>(1);
            activationCodeConfigMap.put(clientType, genInfoUtil.getActivationCodeConfig(clientType, clientIntf, persisterTxn));

            //updating TP clients as used if mdns activation code generating via CAT.  please refer 8.1 part4 SDD for more info "rqPOC_3rdParty_usecase_4.1".
            // in PAM pool usage only TP and Mobile clients are available. hence irrespective of mdns client type we are updating for all.
            if (clientIntf == KnConstants.CLIENT_INTF_CAT) {
            	List<String> mdnList = new ArrayList<>();
            	mdnList.add(mdn);
            	IXDMServerDAO commonXDMServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmsHomePttId);
            	int resCount = commonXDMServerDAO.updateUsageByMDNs(mdnList, com.kodiak.xdms.server.common.resources.KnConstants.PAMACCOUNT_POOL_USAGE.IN_USE.value(), persisterTxn);
            	knLogger.info(methodName, "updated pam TP pool mdns as used, count - ", resCount);
            }

            Map<Integer, Timestamp> expiryTimeMap = activationInfoUtil.generateExpiryTime(activationCodeConfigMap);

            // check if activation code exist for the MDN. If exist update the existing details
            boolean actCodeExist = activationInfoUtil.isActivationCodeExist(mdn, xdmsHomePttId, persisterTxn, clientType);
            knLogger.debug( methodName, "actCodeExist - ", actCodeExist);
            if (actCodeExist) {
                // Update the details in DG.TMPVASSUBSCRIPTIONKEYINFO table;
                knLogger.debug( methodName, "Updating the existing activation code - ");
                activationInfoUtil.updateActivationCode(mdn, activationCode, expiryTimeMap.get(clientType), xdmsHomePttId, persisterTxn,
                        clientType, currentTimeInUTC);
            } else {
                // Insert activation code in to DG.TMPVASSUBSCRIPTIONKEYINFO table.
                knLogger.debug( methodName, "Inserting the activation code.. ");
                activationInfoUtil.insetActivationCode(mdn, activationCode, expiryTimeMap.get(clientType), xdmsHomePttId, persisterTxn,
                        clientType, currentTimeInUTC);
            }
            knLogger.debug( methodName, "Save act code successfull.. ");

            respDTO.setPamAccId(subscProfile.getPamAccId());
            populate(respDTO);
            if (responseDTO.getFailedDataList() != null && !responseDTO.getFailedDataList().isEmpty()) {
                respDTO.setFailedDataList(responseDTO.getFailedDataList());
            }
        } catch (KnCorpBOException e) {
            knLogger.error( methodName, "KnCorpBOException occured while sending activation code - ", e);
            populate(respDTO, e);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while sending activation code - ",
                    new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDTO, e);
        }
        knLogger.debug( methodName, "Returning Response - ", respDTO);
        return respDTO;
    }

    public KnCorpActivationRespDTO getMailInfo(KnIPCorpActivationDTO activationDTO, KnPersisterTxn persisterTxn) {
        String methodName = "getMailInfo(KnIPCorpActivationDTO, KnPersisterTxn)";
        knLogger.info( methodName, "ENTRY : Input DTO Passed activationDTO - ", activationDTO.getCorpId());
        //g
        KnCorpActivationRespDTO respDTO = new KnCorpActivationRespDTO();
        try {
            int corpId = activationDTO.getCorpId();
            Collection<String> mdnList = activationDTO.getMdnList();
            knLogger.info( methodName, "ENTRY : mdnList- ", KnGDPRTemplate.mdnList(mdnList));
            String mdn = null;
            for (String subs : mdnList) {
                mdn = subs;
            }
            // Step:
            // get the subscriber profile details.
            // get email from the subsc profile
            // get Masterlist etag
            // validate email and masterlist etag.
            //get the activation code for MDN
            //return the activation code & email of the subscriber
            int clusterId = Integer.parseInt(System.getenv(com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME));
            Map<String, String> microServicesParamNameValueMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
            //knLogger.debug(methodName, "param value of commonConfig", microServicesParamNameValueMap);
            String deviceSharingFlag = microServicesParamNameValueMap.get(com.kodiak.xdms.server.corpmgmt.resources.KnConstants.DEVICE_SHARING_FEATURE_FLAG);
            KnCorpActivationPersistDTO persistDTO = new KnCorpActivationPersistDTO();
            knLogger.debug( methodName, "Fetch the corpProfile profile if cached or fetch from the DB the details");
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(corpId),
                    CORP_PROFILE, false, persisterTxn);
            knLogger.debug( methodName, "Retrieved Corp Profile details - ", corpProfile);
            String xdmsHomePttId = corpProfile.getXdmsHome();
            KnCorpMdnListPersistDTO corpMdnListPersistDto = new KnCorpMdnListPersistDTO();
            corpMdnListPersistDto.setCorpId(corpId);
            corpMdnListPersistDto.setAddedMdnList(mdnList);
            KnMdnDetailsPersistDTO pocMdnPersistDto = contactInfoUtil.getPoCSubscriberInfo(corpMdnListPersistDto,
                    xdmsHomePttId, true, persisterTxn);
            Collection<String> pocMdnList = pocMdnPersistDto.getMdnList();
            knLogger.debug( methodName, "pocMdnList- ", KnGDPRTemplate.mdnList(pocMdnList));

            Collection<KnCorpSubscriberDTO> subsProfileDetails = contactInfoUtil.getSubscriberProfileDetails(mdnList, corpId, xdmsHomePttId, true, persisterTxn);
            knLogger.debug( methodName, "subsProfileDetails - ", subsProfileDetails);
            String email = null;
            int clientType = 0;
            String subscrName = null;
            int pamAccId = 0 ;
            for (KnCorpSubscriberDTO corpSubscriberDTO : subsProfileDetails) {
                email = corpSubscriberDTO.getSubscEmail();
                clientType = corpSubscriberDTO.getClientType();
                subscrName = corpSubscriberDTO.getName();
                pamAccId = corpSubscriberDTO.getPamAccId();
                persistDTO.setServiceAuthStatus(corpSubscriberDTO.getServiceAuthStatus());
            }

            //Invoking custom hook
            KnCorpHookRespDTO responseDTO = new KnCorpHookRespDTO();
            Map<String, Object> customParams = activationDTO.getCustomParamMap();
            if (activationDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
                contactInfoUtil.checkValidHierarchySubs(Collections.singletonList(mdn), customParams, xdmsHomePttId, persisterTxn);
                customParams.put(KnConstants.CURRENT_MASTER_LIST_ETAG, corpProfile.getCorpMasterListEtag());
                customParams.put(KnConstants.PERSISTER_TXN, persisterTxn);
                customParams.put(KnConstants.PTT_SERVER_ID, xdmsHomePttId);
                customParams.put(KnConstants.SUBSCRIBER_MDN, mdn);
                activationDTO.setCustomParamMap(customParams);
                KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
                hookIPDTO.setAction(KnActions.ACTIONS.GET_MAIL_INFO);
                hookIPDTO.setData(activationDTO);
                KnProcessInvokerImpl processInvoker = KnProcessInvokerImpl.getInstance();
                Map<String, Object> customRespMap = null;
                knLogger.debug( methodName, "customParams Before hook invokation - ", customParams);
                Object hookResp = processInvoker.invokeHook(KnConstants.CUSTOM_CORP_USER_HOOK, hookIPDTO);
                if (hookResp != null && hookResp instanceof KnCorpHookRespDTO) {
                    responseDTO = (KnCorpHookRespDTO) hookResp;
                    customRespMap = responseDTO.getCustomParamMap();
                    if (com.kodiak.xdms.server.common.resources.KnConstants.RESPONSE_STATUS.FAILURE.value() == responseDTO.getStatus()) {
                        knLogger.error( methodName, "Returning Failure response");
                        populateXdmResponseFroomHook(responseDTO, respDTO);
                        return respDTO;
                    }
                }
                knLogger.debug( methodName, "customRespMap after hook invokation - ", customRespMap);
                respDTO.setCustomParamMap(customRespMap);
            }
            long corpEtag = contactInfoUtil.getCorporateEtag(corpId, xdmsHomePttId, true, persisterTxn);
            // get the count of MDN and corpid for validation if mdn exist in the corporation
            int subscribersCount = contactInfoUtil.getSubscribersCount(mdn, corpId, true, xdmsHomePttId, persisterTxn);


            KnCorpSubscriberDTO corpSubscriberDTO = activationInfoUtil.getActCodeExtTime(mdn, clientType, xdmsHomePttId, true, persisterTxn);
            String activationCode = corpSubscriberDTO.getActivationCode();
            Timestamp expiryTime = corpSubscriberDTO.getExpiryTime();
            persistDTO.setActivationCode(activationCode);
            knLogger.debug( methodName, " activation code - ", activationCode);
            knLogger.debug( methodName, " expiryTime - ", expiryTime);
            persistDTO.setSubscriberCount(subscribersCount);

            persistDTO.setInputDTO(activationDTO);
            persistDTO.setPocMdnList(pocMdnList);
            persistDTO.setExpTimeStamp(expiryTime);
            persistDTO.setSubscriberAuthStatusList(pocMdnPersistDto.getAddedMdnDTO());
            persistDTO.setPocMdnList(pocMdnList);
            persistDTO.setDeviceSharingFlag(Integer.parseInt(deviceSharingFlag));
            knLogger.debug( methodName, "Invoking ValidationFW. DTO - ", persistDTO);
            validatorFW.validate(persistDTO);
            knLogger.debug( methodName, "Validation completed Successfully.");
            respDTO.setActivationCode(activationCode);
            if(null != expiryTime){
                respDTO.setExpiryTimestamp(String.valueOf(expiryTime.getTime()));
            }

            KnCorpMailInfoDTO mailInfoDTO = new KnCorpMailInfoDTO();
            knLogger.debug( methodName, "Email - ", KnGDPRTemplate.email(email), " activationCode", activationCode);
            mailInfoDTO.setTo(email);
            respDTO.setMailInfoDTO(mailInfoDTO);
            respDTO.setClientType(clientType);
            respDTO.setSubscrName(subscrName);
            respDTO.setPamAccId(pamAccId);
            Map<String, Object> custParamMap = activationDTO.getCustomParamMap();
            if (custParamMap != null) {
                custParamMap.put(KnConstants.CURRENT_MASTER_LIST_ETAG, corpEtag);
                activationDTO.setCustomParamMap(custParamMap);
            }

            populate(respDTO);
            if (responseDTO.getFailedDataList() != null && !responseDTO.getFailedDataList().isEmpty()) {
                respDTO.setFailedDataList(responseDTO.getFailedDataList());
            }

        } catch (KnValidationException e) {
            knLogger.error( methodName, "KnValidationException occured while generating activation code - ", e);
            populate(respDTO, e);
        } catch (KnCorpBOException e) {
            knLogger.error( methodName, "KnCorpBOException occured while generating activation code - ", e);
            populate(respDTO, e);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while generating activation code - ",
                    new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDTO, e);
        } finally {
            knLogger.debug( methodName, "Returning Response - ", respDTO);
            knLogger.info( methodName, "EXIT:");
        }
        return respDTO;

    }


    public KnCorpActivationRespDTO sendActivationMail(KnIPCorpActivationDTO activationDTO, KnPersisterTxn persisterTxn) {
        String methodName = "sendActivationMail(KnIPCorpActivationDTO, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed activationDTO - ", activationDTO.getMdn());
        KnCorpActivationRespDTO respDTO = new KnCorpActivationRespDTO();
        try {
            String mdn = activationDTO.getMdn();
            knLogger.debug( methodName, "Fetch the subscriber profile ");
            KnSubsProfileDTO subscProfile = commonInfoUtil.getProfileDetails(mdn, KnProfileTypes.PUBLIC_PROFILE,
                    false, persisterTxn);
            knLogger.debug( methodName, "Subscriber profile - ", subscProfile);
            if (activationDTO.getCorpId() > 0 && subscProfile.getCorpId() != activationDTO.getCorpId()) {
                throw new KnCorpBOException(KnErrorCodes.BOEntity.SUBSCRIBER_DOES_NOT_BELONG_TO_CORP,
                        "Subscribers Does not belong to the corporation.");
            }
            if (activationDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY && null != activationDTO.getCustomParamMap()) {
                contactInfoUtil.checkValidHierarchySubs(Collections.singletonList(mdn), activationDTO.getCustomParamMap(), subscProfile.getXdmsHome(), persisterTxn);
            }
            respDTO.setPamAccId(subscProfile.getPamAccId());
            populate(respDTO);
        } catch (KnCorpBOException e) {
            knLogger.error( methodName, "KnCorpBOException occured while sending activation code - ", e);
            populate(respDTO, e);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while sending activation code - ",
                    new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDTO, e);
        } finally {
            knLogger.debug( methodName, "Returning Response - ", respDTO);
            knLogger.info( methodName, "EXIT:");
        }
        return respDTO;
    }

    public KnCorpActivationRespDTO sendMail(KnIPCorpActivationDTO activationDTO, KnPersisterTxn persisterTxn) {
        String methodName = "sendMail(KnIPCorpActivationDTO, KnPersisterTxn)";
        knLogger.info( methodName, "ENTRY : Input DTO Passed activationDTO - ", activationDTO.getMdn());
        KnCorpActivationRespDTO respDTO = new KnCorpActivationRespDTO();
        try {
            String subscriberMdn = activationDTO.getMdn();
            knLogger.debug( methodName, "Fetch the subscriber profile ");
            KnSubsProfileDTO subscProfile = commonInfoUtil.getProfileDetails(subscriberMdn, KnProfileTypes.PUBLIC_PROFILE,
                    false, persisterTxn);
            knLogger.debug( methodName, "Subscriber profile - ", subscProfile);
            if (activationDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY && null != activationDTO.getCustomParamMap()) {
                contactInfoUtil.checkValidHierarchySubs(Collections.singletonList(subscriberMdn), activationDTO.getCustomParamMap(), subscProfile.getXdmsHome(), persisterTxn);
            }
            if (null != subscProfile && subscProfile.getCorpId() != activationDTO.getCorpId()) {
                throw new KnCorpBOException(KnErrorCodes.Validator.SEND_MAIL_NOT_ALLOWED, "Sending mail not allowed");
            }
            respDTO.setPamAccId(subscProfile.getPamAccId());
            populate(respDTO);
        } catch (KnCorpBOException e) {
            knLogger.error( methodName, "KnCorpBOException occured while sending Mail - ", e);
            populate(respDTO, e);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while sending Mail - ",
                    new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDTO, e);
        } finally {
            knLogger.debug( methodName, "Returning Response - ", respDTO);
            knLogger.info( methodName, "EXIT:");
        }
        return respDTO;
    }

    public void updateUniqueActCode(Collection<KnCorpSubscriberDTO> failedMdnList, String xdmsHomePttId, KnPersisterTxn
            persisterTxn, int operationType, Map<Integer, KnActivationCodeConfigDTO> activationCodeConfigMap, Map<String, String> activationCodeMap) throws KnCorpBOException {
        String methodName = "updateUniqueActCode()";
        int retryCount = 3;
        do {
            if (failedMdnList != null && !failedMdnList.isEmpty()) {
                Map<String, String> newActivCodeMap = activationInfoUtil.generateActivationCode(failedMdnList, activationCodeConfigMap);
                knLogger.debug( methodName, "retryCount - ", retryCount);
                for (KnCorpSubscriberDTO subscriber : failedMdnList) {
                    String mdn = subscriber.getMdn();
                    subscriber.setActivationCode(newActivCodeMap.get(mdn));
                    //update activation code map activation code with new activation code
                    if(null != activationCodeMap.get(mdn)){
                        activationCodeMap.put(mdn, newActivCodeMap.get(mdn));
                    }
                    knLogger.debug(methodName,"activationCodeMap::",activationCodeMap);
                }
                if (operationType == 1) {
                    knLogger.debug( methodName, "updating ActivationCode - ");
                    failedMdnList = activationInfoUtil.updateActivationCode(failedMdnList, xdmsHomePttId, persisterTxn);
                } else if (operationType == 2) {
                    knLogger.debug( methodName, "inserting ActivationCode - ");
                    failedMdnList = activationInfoUtil.insertActivationCode(failedMdnList, null, xdmsHomePttId, persisterTxn);
                }
            }
            retryCount--;
        } while (retryCount > 0);
        if (failedMdnList != null && !failedMdnList.isEmpty()) {
            throw new KnCorpBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Unexpected Exception occured while generating activation code");
        }
        knLogger.debug( methodName, "Exit -  ");
    }

    private int getClientInterface(KnIPCorpActivationDTO activationDTO) {
    	int clientIntf =  activationDTO.getClientType();
    	if (clientIntf == KnConstants.CLIENT_TYPE_REST) {
    		clientIntf = KnConstants.CLIENT_INTF_REST;
    	} else  {
    		clientIntf = KnConstants.CLIENT_INTF_CAT;
    	}
    	return clientIntf;
    }


    @Override
    public KnCorpActivationRespDTO getSubscrActivationCode(KnIPCorpActivationDTO activationDTO, KnPersisterTxn persisterTxn) {
        String methodName = "getSubscrActivationCode(KnIPCorpActivationDTO, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed activationDTO - ", activationDTO.getMdn());
        KnCorpActivationRespDTO respDTO = new KnCorpActivationRespDTO();
        Map<String, Object> responseMap = new HashMap<>();
        try {
            String mdn = activationDTO.getMdn();
            // Step:
            // get the subscriber profile details.
            // get email from the subsc profile
            // get Masterlist etag
            // validate email and masterlist etag.
            knLogger.debug(methodName, "Fetch the subscriber profile ");
            //mdn = "91222004870";
            KnSubsProfileDTO subscProfile = commonInfoUtil.getProfileDetails(mdn, KnProfileTypes.PUBLIC_PROFILE,
                    false, persisterTxn);
            knLogger.debug(methodName, "Subscriber profile - ", subscProfile);
            String xdmsHomePttId = subscProfile.getXdmsHome();
            ArrayList<String> mdnList = new ArrayList<>();
            mdnList.add(mdn);
            Map<String, KnIPCorpActivationDTO> activationCodeDetailsMap = contactInfoUtil.getActivationCodeForCorpoateSubscriber(mdnList, xdmsHomePttId, true, persisterTxn);
            int clusterId = Integer.parseInt(System.getenv(com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME));
            Map<String, String> microServicesParamNameValueMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
            //knLogger.debug(methodName, "param value of commonConfig", microServicesParamNameValueMap);
            String deviceSharingFlag = microServicesParamNameValueMap.get(com.kodiak.xdms.server.corpmgmt.resources.KnConstants.DEVICE_SHARING_FEATURE_FLAG);
            KnIPCorpActivationDTO actDTO = activationCodeDetailsMap.get(mdn);
            if (actDTO != null) {
                String activationCode = actDTO.getActivationCode();
                KnCorpActivationPersistDTO persistDTO = new KnCorpActivationPersistDTO();
                persistDTO.setActivationCode(activationCode);
                knLogger.debug(methodName, " activation code - ", activationCode);
                persistDTO.setInputDTO(activationDTO);
                persistDTO.setPocMdnList(mdnList);
                persistDTO.setClientType(subscProfile.getClientType());
                persistDTO.setExpTimeStamp(actDTO.getExpiryTimestamp());
                persistDTO.setServiceAuthStatus(subscProfile.getServiceAuthStatus());
                persistDTO.setLicenseType(subscProfile.getLicenseType());
                persistDTO.setDeviceSharingFlag(Integer.parseInt(deviceSharingFlag));
                persistDTO.setClientPVMajorVersion(subscProfile.getClientMajorVersion());
                knLogger.debug(methodName, "Invoking ValidationFW. DTO - ", persistDTO);
                validatorFW.validate(persistDTO);
                knLogger.debug(methodName, "Validation completed Successfully.");
                populate(respDTO);
                respDTO.setActivationCode(activationCode);
                responseMap.put("activationCode", respDTO.getActivationCode());
                Timestamp exTime = actDTO.getExpiryTimestamp();
                if (exTime != null) {
                    exTime.getTime();
                    respDTO.setExpiryTimestamp(String.valueOf(exTime.getTime()));
                    responseMap.put("expiryTimestamp", respDTO.getExpiryTimestamp());
                }
                Timestamp activationTime = actDTO.getActivationTimestamp();
                if (activationTime != null) {
                    respDTO.setActivationTimestamp(String.valueOf(activationTime.getTime()));
                    responseMap.put("activationTimestamp", respDTO.getActivationTimestamp());
                }
                responseMap.put("status", String.valueOf(respDTO.getStatus()));
                responseMap.put("statusCode", respDTO.getStatusCode());
                responseMap.put("message", respDTO.getMessage());
            } else {
                knLogger.debug(methodName, " activation code does not exists for the subscriber.");
                throw new KnCorpBOException(KnErrorCodes.Validator.ACTIVATION_CODE_MISSING, "ActivationCode Missing");
            }
        } catch (KnValidationException e) {
            knLogger.error(methodName, "KnValidationException occured while generating activation code - ", e);
            populate(respDTO, e);
            responseMap.put("status", String.valueOf(respDTO.getStatus()));
            responseMap.put("statusCode", respDTO.getStatusCode());
            responseMap.put("message", respDTO.getMessage());
        } catch (KnCorpBOException e) {
            knLogger.error(methodName, "KnCorpBOException occured while retrieving the activation code - ", e);
            populate(respDTO, e);
            responseMap.put("status", String.valueOf(respDTO.getStatus()));
            responseMap.put("statusCode", respDTO.getStatusCode());
            responseMap.put("message", respDTO.getMessage());
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while retrieving the activation code - ",
                    new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDTO, e);
            responseMap.put("status", String.valueOf(respDTO.getStatus()));
            responseMap.put("statusCode", respDTO.getStatusCode());
            responseMap.put("message", respDTO.getMessage());
        }
        respDTO.setResponseMap(responseMap);
        knLogger.debug(methodName, "Returning Response - ", respDTO);
        return respDTO;
    }

    @Override
    public KnCorpActivationRespDTO getTempPwdForLegacy(KnIPCorpActivationDTO activationDTO, KnPersisterTxn persisterTxn) {
        String methodName = "getTempPwdForLegacy(KnIPCorpActivationDTO, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed activationDTO - ", activationDTO.getMdn());
        KnCorpActivationRespDTO respDTO = new KnCorpActivationRespDTO();
        Map<String, Object> responseMap = new HashMap<>();
        try {
            String mdn = activationDTO.getMdn();
            KnSubsProfileDTO subscProfile = commonInfoUtil.getProfileDetails(mdn, KnProfileTypes.PUBLIC_PROFILE,
                    false, persisterTxn);
            knLogger.debug(methodName, "Subscriber profile - ", subscProfile);
            String xdmsHomePttId = subscProfile.getXdmsHome();
            ArrayList<String> mdnList = new ArrayList<>();
            mdnList.add(mdn);
            Map<String, KnIPCorpActivationDTO> activationCodeDetailsMap = contactInfoUtil.getActivationCodeForCorpoateSubscriber(mdnList, xdmsHomePttId, true, persisterTxn);
            KnIPCorpActivationDTO actDTO = activationCodeDetailsMap.get(mdn);
            if (actDTO != null) {
                String activationCode = actDTO.getActivationCode();
                populate(respDTO);
                respDTO.setActivationCode(activationCode);
                responseMap.put("activationCode", respDTO.getActivationCode());
                Timestamp exTime = actDTO.getExpiryTimestamp();
                if (exTime != null) {
                    exTime.getTime();
                    respDTO.setExpiryTimestamp(String.valueOf(exTime.getTime()));
                    responseMap.put("expiryTimestamp", respDTO.getExpiryTimestamp());
                }
                Timestamp activationTime = actDTO.getActivationTimestamp();
                if (activationTime != null) {
                    respDTO.setActivationTimestamp(String.valueOf(activationTime.getTime()));
                }
            }
        } catch (KnCorpBOException e) {
            knLogger.error(methodName, "KnCorpBOException occured while retrieving the activation code - ", e);
            populate(respDTO, e);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while retrieving the activation code - ",
                    new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDTO, e);
        }
        respDTO.setResponseMap(responseMap);
        knLogger.debug(methodName, "Returning Response - ", respDTO);
        return respDTO;
    }

    @Override
    public KnCorpActivationRespDTO generateOTP(KnIPCorpActivationDTO activationDTO, KnPersisterTxn persisterTxn) {
        String methodName = "generateOTP(KnIPCorpActivationDTO, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed activationDTO - ", activationDTO);
        KnCorpActivationRespDTO respDTO = new KnCorpActivationRespDTO();
        Map<String, Object> responseMap = new HashMap<>();
        try {
            String mdn = activationDTO.getMdn();
            knLogger.debug(methodName, "Fetch the subscriber profile ");
            // Step:
            // get the subscriber profile details.
            // get email from the subsc profile
            // get Masterlist etag
            // validate email and masterlist etag.
            knLogger.debug(methodName, "Fetch the subscriber profile ");
            KnSubsProfileDTO subscProfile = commonInfoUtil.getProfileDetails(mdn, KnProfileTypes.PUBLIC_PROFILE,
                    false, persisterTxn);
            knLogger.debug(methodName, "Subscriber profile - ", subscProfile);
            String extCorpId = activationDTO.getExtCorpId();
            knLogger.debug(methodName, "Fetch the corpProfile profile if cached or fetch from the DB the details");
            int corpId = commonInfoUtil.getCorpId(extCorpId, persisterTxn);
            activationDTO.setCorpId(subscProfile.getCorpId());
            String xdmsHomePttId = subscProfile.getXdmsHome();
            ArrayList<String> mdnList = new ArrayList<>();
            mdnList.add(mdn);
            KnCorpActivationPersistDTO persistDTO = new KnCorpActivationPersistDTO();
            persistDTO.setInputDTO(activationDTO);
            persistDTO.setCorpId(corpId);
            persistDTO.setClientType(subscProfile.getClientType());
            knLogger.debug(methodName, "Invoking ValidationFW. DTO - ", persistDTO);
            validatorFW.validate(persistDTO);
            knLogger.debug(methodName, "Validation completed Successfully.");
            //Get the activation code configuration
            Map<Integer, KnActivationCodeConfigDTO> activationCodeConfigMap = new HashMap<>(1);
            activationCodeConfigMap.put(subscProfile.getClientType(), genInfoUtil.getActivationCodeConfig(subscProfile.getClientType(), KnConstants.REST_IDM_INTF_TYPE, persisterTxn));
            int actCodeLen = activationCodeConfigMap.get(subscProfile.getClientType()).getActCodeLength();
            int clientType = subscProfile.getClientType();
            String serviceName = KnConstants.IDM_REST_SERVICE_NAME;
            Collection<String> mdns = activationInfoUtil.activationCodeExistForMDNs(mdnList, serviceName, xdmsHomePttId, persisterTxn);
            if (mdns != null && !mdns.isEmpty()) {
                activationInfoUtil.deleteActivationCode(mdns, serviceName, xdmsHomePttId, persisterTxn);
            }
            Collection<KnCorpSubscriberDTO> subscList = new ArrayList<>();
            KnCorpSubscriberDTO subsc = new KnCorpSubscriberDTO();
            subsc.setMdn(mdn);
            subsc.setClientType(clientType);
            subscList.add(subsc);
            Map<String, String> activationCodeMap = activationInfoUtil.generateActivationCode(subscList, activationCodeConfigMap);
            knLogger.debug(methodName, "activationCodeMap --- ", activationCodeMap);
            // get the current time in UTC format and use this while inserting into DB as insertion time.
            Timestamp currentTimeInUTC = activationInfoUtil.getCurrentTimeInUTC();
            Map<Integer, Timestamp> expiryTimeMap = activationInfoUtil.generateExpiryTime(activationCodeConfigMap);

            List<KnCorpSubscriberDTO> subsActivationCodeList = new ArrayList<>();
            for (KnCorpSubscriberDTO subscriberDTO : subscList) {
                String mdnVal = subscriberDTO.getMdn();
                KnCorpSubscriberDTO corpSubscriberDTO = new KnCorpSubscriberDTO();
                corpSubscriberDTO.setClientType(subscriberDTO.getClientType());
                corpSubscriberDTO.setActivationCode(activationCodeMap.get(mdnVal));
                corpSubscriberDTO.setExpiryTime(expiryTimeMap.get(subscriberDTO.getClientType()));
                corpSubscriberDTO.setMdn(mdnVal);
                corpSubscriberDTO.setActivationTimestamp(currentTimeInUTC);
                subsActivationCodeList.add(corpSubscriberDTO);
            }
            knLogger.debug(methodName, " subsActivationCodeList --- ", subsActivationCodeList);
            //insert intp tmp
            Collection<KnCorpSubscriberDTO> failedMdnList = activationInfoUtil.insertActivationCode(subsActivationCodeList, serviceName, xdmsHomePttId, persisterTxn);
            // if insertActivationCode failed(because of timesTen Error the we will take one more change to insert again)
            // then we will regenerate the OTP code in the next call and update the OTP via call by reference.
            int operationType = 2;
            updateUniqueActCode(failedMdnList, xdmsHomePttId, persisterTxn, operationType, activationCodeConfigMap, activationCodeMap);

            //Insert to the dg.pendingsmsnotification
            KnSMSDTO corpSMSDTO = new KnSMSDTO();
            corpSMSDTO.setOtp(activationCodeMap.get(mdn));
            corpSMSDTO.setMsgNotificationId(KnConstants.IDM_OTP_SMS_VERIFICATION_MSG_ID);
            corpSMSDTO.setMdn(mdn);
            KnSMSUtil smsUtil =new KnSMSUtil();
            smsUtil.sendSMS(corpSMSDTO,persisterTxn);
            populate(respDTO);
            responseMap.put("status", String.valueOf(respDTO.getStatus()));
            responseMap.put("statusCode", respDTO.getStatusCode());
            responseMap.put("message", respDTO.getMessage());
        } catch (KnValidationException e) {
            knLogger.error(methodName, "KnValidationException occured while generating activation code - ", e);
            populate(respDTO, e);
            responseMap.put("status", String.valueOf(respDTO.getStatus()));
            responseMap.put("statusCode", respDTO.getStatusCode());
            responseMap.put("message", respDTO.getMessage());
        } catch (KnCorpBOException e) {
            knLogger.error(methodName, "KnCorpBOException occured while retrieving the activation code - ", e);
            populate(respDTO, e);
            responseMap.put("status", String.valueOf(respDTO.getStatus()));
            responseMap.put("statusCode", respDTO.getStatusCode());
            responseMap.put("message", respDTO.getMessage());
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while retrieving the activation code - ",
                    new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDTO, e);
            responseMap.put("status", String.valueOf(respDTO.getStatus()));
            responseMap.put("statusCode", respDTO.getStatusCode());
            responseMap.put("message", respDTO.getMessage());
        }
        respDTO.setResponseMap(responseMap);
        knLogger.debug(methodName, "Returning Response - ", respDTO);
        return respDTO;
    }

    @Override
    public KnCorpActivationRespDTO validateOTP(KnIPCorpActivationDTO activationDTO, KnPersisterTxn persisterTxn) {
        String methodName = "validateOTP(KnIPCorpActivationDTO, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed activationDTO - ", activationDTO.getMdn());
        KnCorpActivationRespDTO respDTO = new KnCorpActivationRespDTO();
        Map<String, Object> responseMap = new HashMap<>();
        try {
            String mdn = activationDTO.getMdn();
            knLogger.debug(methodName, "Fetch the corpProfile profile if cached or fetch from the DB the details");
            knLogger.debug(methodName, "Fetch the subscriber profile ");
            KnSubsProfileDTO subscProfile = commonInfoUtil.getProfileDetails(mdn, KnProfileTypes.PUBLIC_PROFILE,
                    false, persisterTxn);
            knLogger.debug(methodName, "Subscribers profile - ", subscProfile);
            String xdmsHomePttId = subscProfile.getXdmsHome();
            KnCorpActivationPersistDTO persistDTO = new KnCorpActivationPersistDTO();
            persistDTO.setInputDTO(activationDTO);
            persistDTO.setClientType(subscProfile.getClientType());
            String dbOTP = activationInfoUtil.getSubscribersOTP(mdn, KnConstants.IDM_REST_SERVICE_NAME, xdmsHomePttId, persisterTxn);
            persistDTO.setDbOTP(dbOTP);
            knLogger.debug(methodName, "Invoking ValidationFW. DTO - ", persistDTO);
            validatorFW.validate(persistDTO);
            knLogger.debug(methodName, "Validation completed Successfully.");
            populate(respDTO);
            responseMap.put("status", String.valueOf(respDTO.getStatus()));
            responseMap.put("statusCode", respDTO.getStatusCode());
            responseMap.put("message", respDTO.getMessage());
        } catch (KnCorpBOException e) {
            knLogger.error(methodName, "KnCorpBOException occured while retrieving the activation code - ", e);
            populate(respDTO, e);
            responseMap.put("status", String.valueOf(respDTO.getStatus()));
            responseMap.put("statusCode", respDTO.getStatusCode());
            responseMap.put("message", respDTO.getMessage());
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while retrieving the activation code - ",
                    new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDTO, e);
            responseMap.put("status", String.valueOf(respDTO.getStatus()));
            responseMap.put("statusCode", respDTO.getStatusCode());
            responseMap.put("message", respDTO.getMessage());
        }
        respDTO.setResponseMap(responseMap);
        knLogger.debug(methodName, "Returning Response - ", respDTO);
        return respDTO;
    }

    @Override
    public KnCorpActivationRespDTO getCorpBanFanDetails(KnIPCorpActivationDTO activationDTO, KnPersisterTxn persisterTxn) {
        String methodName = "getCorpBanFanDetails(KnIPCorpActivationDTO, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed activationDTO - ", activationDTO.getExtCorpId());
        KnCorpActivationRespDTO respDTO = new KnCorpActivationRespDTO();
        Map<String, Object> responseMap = new HashMap<>();
        try {
            String extCorpId = activationDTO.getExtCorpId();
            knLogger.debug(methodName, "Fetch the corpProfile profile if cached or fetch from the DB the details");
            int corpId = commonInfoUtil.getCorpId(extCorpId, true, persisterTxn);
            knLogger.debug(methodName, "Fetch the subscriber profile ");
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(corpId),KnProfileTypes.CORP_PROFILE,
                    false, persisterTxn);
            knLogger.debug(methodName, "Corporate profile - ", corpProfile);
            String xdmsHomePttId = corpProfile.getXdmsHome();
            activationDTO.setCorpId(corpId);
            //Invoking custom hook
            KnCorpHookRespDTO responseDTO = new KnCorpHookRespDTO();
            Map<String, Object> customParams = new HashMap<>();
            if (extCorpId != null && !extCorpId.isEmpty()) {
                customParams.put(KnConstants.PERSISTER_TXN, persisterTxn);
                customParams.put(KnConstants.PTT_SERVER_ID, xdmsHomePttId);
                customParams.put(KnConstants.CORP_ID, corpId);
                activationDTO.setCustomParamMap(customParams);
                KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
                hookIPDTO.setAction(KnActions.ACTIONS.GET_CORP_DETAILS);
                hookIPDTO.setData(activationDTO);
                KnProcessInvokerImpl processInvoker = KnProcessInvokerImpl.getInstance();
                Map<String, Object> customRespMap = null;
                knLogger.debug(methodName, "customParams Before hook invokation - ", customParams);
                Object hookResp = processInvoker.invokeHook(KnConstants.CUSTOM_CORP_USER_HOOK, hookIPDTO);
                if (hookResp != null && hookResp instanceof KnCorpHookRespDTO) {
                    responseDTO = (KnCorpHookRespDTO) hookResp;
                    customRespMap = responseDTO.getCustomParamMap();
                    if (com.kodiak.xdms.server.common.resources.KnConstants.RESPONSE_STATUS.FAILURE.value() == responseDTO.getStatus()) {
                        knLogger.error(methodName, "Returning Failure response");
                        populateXdmResponseFroomHook(responseDTO, respDTO);
                        return respDTO;
                    }
                }
                knLogger.debug(methodName, "customRespMap after hook invokation - ", customRespMap);
                respDTO.setCustomParamMap(customRespMap);
            }
            Map<String, Object> respCutParams = respDTO.getCustomParamMap();
            //Get all FAN details in the corp by corpid.
            Map<Integer,String> fanDetails = (Map<Integer,String>)respCutParams.get(KnConstants.FAN_DETAILS);
            Map<Integer,String> banDetails = (Map<Integer,String>)respCutParams.get(KnConstants.BAN_DETAILS);
            //Get all BAN details for the corporate
            knLogger.debug(methodName, "fanDetails - ", fanDetails);
            knLogger.debug(methodName, "banDetails - ", banDetails);
            //Get the activation code configuration
            List<KnIdValue> fanIdValue = new ArrayList<>();
            List<KnIdValue> banIdValue = new ArrayList<>();

            if(fanDetails != null && !fanDetails.isEmpty()) {
                for (Map.Entry<Integer, String> entry : fanDetails.entrySet()) {
                    KnIdValue idVal = new KnIdValue();
                    idVal.setAttrKey(String.valueOf(entry.getKey()));
                    idVal.setAttrVal(entry.getValue());
                    fanIdValue.add(idVal);
                }
            }
            if(banDetails != null && !banDetails.isEmpty()) {
                for (Map.Entry<Integer, String> entry : banDetails.entrySet()) {
                    KnIdValue idVal = new KnIdValue();
                    idVal.setAttrKey(String.valueOf(entry.getKey()));
                    idVal.setAttrVal(entry.getValue());
                    banIdValue.add(idVal);
                }
            }

            populate(respDTO);
            responseMap.put(KnConstants.CORP_ID, String.valueOf(corpId));
            responseMap.put(KnConstants.EXT_CORP_ID, extCorpId);
            responseMap.put(KnConstants.CORPNAME, corpProfile.getNetworkName());
            responseMap.put(KnConstants.STATUS, String.valueOf(respDTO.getStatus()));
            responseMap.put(KnConstants.STATUS_CODE, respDTO.getStatusCode());
            responseMap.put(KnConstants.MESSAGE, respDTO.getMessage());
            responseMap.put(KnConstants.FAN_DETAILS, fanIdValue);
            responseMap.put(KnConstants.BAN_DETAILS, banIdValue);
        } catch (KnCorpBOException e) {
            knLogger.error(methodName, "KnCorpBOException occured while retrieving the activation code - ", e);
            populate(respDTO, e);
            responseMap.put("status", String.valueOf(respDTO.getStatus()));
            responseMap.put("statusCode", respDTO.getStatusCode());
            responseMap.put("message", respDTO.getMessage());
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while retrieving the activation code - ",
                    new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDTO, e);
            responseMap.put("status", String.valueOf(respDTO.getStatus()));
            responseMap.put("statusCode", respDTO.getStatusCode());
            responseMap.put("message", respDTO.getMessage());
        }
        respDTO.setResponseMap(responseMap);
        knLogger.debug(methodName, "Returning Response - ", respDTO);
        return respDTO;
    }

    public KnCorpActivationRespDTO generateActivationCodeIDMIntf(KnIPCorpActivationDTO activationDTO, KnPersisterTxn persisterTxn) {
        String methodName = "generateActivationCodeIDMIntf(KnIPCorpActivationDTO, KnPersisterTxn)";
        knLogger.info( methodName, "ENTRY : Input DTO Passed activationDTO - ", activationDTO);
        KnCorpActivationRespDTO respDTO = new KnCorpActivationRespDTO();
        Map<String, Object> responseMap = new HashMap<>();
        try {
            String mdn = activationDTO.getMdn();
            knLogger.debug( methodName, "Fetch the subscriber profile ");
            KnSubsProfileDTO subscProfile = commonInfoUtil.getProfileDetails(mdn, KnProfileTypes.PUBLIC_PROFILE,
                    false, persisterTxn);
            knLogger.debug( methodName, "Subscriber profile - ", subscProfile);
            String xdmsHomePttId = subscProfile.getXdmsHome();
            int clientType = subscProfile.getClientType();
            int clusterId = Integer.parseInt(System.getenv(com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME));
            Map<String, String> microServicesParamNameValueMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
            //knLogger.debug(methodName, "param value of commonConfig", microServicesParamNameValueMap);
            String deviceSharingFlag = microServicesParamNameValueMap.get(com.kodiak.xdms.server.corpmgmt.resources.KnConstants.DEVICE_SHARING_FEATURE_FLAG);
            KnCorpActivationPersistDTO persistDTO = new KnCorpActivationPersistDTO();
            persistDTO.setServiceAuthStatus(subscProfile.getServiceAuthStatus());
            persistDTO.setInputDTO(activationDTO);
            persistDTO.setClientType(clientType);
            persistDTO.setLicenseType(subscProfile.getLicenseType());
            persistDTO.setDeviceSharingFlag(Integer.parseInt(deviceSharingFlag));
            persistDTO.setClientPVMajorVersion(subscProfile.getClientMajorVersion());
            knLogger.debug( methodName, "Invoking ValidationFW. DTO - ", persistDTO);
            validatorFW.validate(persistDTO);
            knLogger.debug( methodName, "Validation completed Successfully.");

            // introduced client interface also in activation path. for more info refer 8.1 TPMS SDD
            int clientIntf = this.getClientInterface(activationDTO);
            knLogger.info(methodName, "interface recieved - ", clientIntf);

            //Get the activation code configuration
            Map<Integer, KnActivationCodeConfigDTO> activationCodeConfigMap = new HashMap<>(1);
            activationCodeConfigMap.put(clientType, genInfoUtil.getActivationCodeConfig(clientType, clientIntf, persisterTxn));
            int actCodeLen = activationCodeConfigMap.get(clientType).getActCodeLength();

            if (actCodeLen < 7 || actCodeLen > 32) {
                throw new KnCorpBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Invalid activation key length");
            }

            //updating TP clients as used if mdns activation code generating via CAT.  please refer 8.1 part4 SDD for more info "rqPOC_3rdParty_usecase_4.1".
            // in PAM pool usage only TP and Mobile clients are available. hence irrespective of mdns client type we are updating for all.
            if (clientIntf == KnConstants.CLIENT_INTF_CAT) {
                List<String> mdnList = new ArrayList<>();
                mdnList.add(mdn);
                IXDMServerDAO commonXDMServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmsHomePttId);
                int resCount = commonXDMServerDAO.updateUsageByMDNs(mdnList, com.kodiak.xdms.server.common.resources.KnConstants.PAMACCOUNT_POOL_USAGE.IN_USE.value(), persisterTxn);
                knLogger.info(methodName, "updated pam TP pool mdns as used, count - ", resCount);
            }

            Collection<KnCorpSubscriberDTO> subscList = new ArrayList<>();
            KnCorpSubscriberDTO subs = new KnCorpSubscriberDTO();
            subs.setMdn(mdn);
            subs.setClientType(clientType);
            subscList.add(subs);
            //Generate the activation code and set into the subscriber DTO
            Map<String, String> activationCodeMap = activationInfoUtil.generateActivationCode(subscList, activationCodeConfigMap);
            knLogger.debug( methodName, "activationCodeSet - ", activationCodeMap);
            subs.setActivationCode(activationCodeMap.get(mdn));
            //Generate the expiry and current timestamp and set into the subscriber dto.
            Map<Integer, Timestamp> expiryTimeMap = activationInfoUtil.generateExpiryTime(activationCodeConfigMap);
            Timestamp currentTimeInUTC = activationInfoUtil.getCurrentTimeInUTC();
            subs.setExpiryTime(expiryTimeMap.get(clientType));
            subs.setActivationTimestamp(currentTimeInUTC);

            // check if activation code exist in DB. If exist update the generated activation code.
            Set<String> existingActCodeList = activationInfoUtil.isActivationCodeExistInDB(new HashSet<>(activationCodeMap.keySet()), xdmsHomePttId, false, persisterTxn);
            knLogger.debug( methodName, "existingActCodeList - ", existingActCodeList);
            if (existingActCodeList != null && !existingActCodeList.isEmpty()) {
                activationCodeMap.putAll(activationInfoUtil.generateActivationCode(subscList, activationCodeConfigMap));
                subs.setActivationCode(activationCodeMap.get(mdn));            }

            int operationType;
            //Check if activation code exist for the MDN, if exist update into DG.TMPVASSUBSCRIPTIONKEYINFO table.
            Collection<KnCorpSubscriberDTO> mdnList = activationInfoUtil.isActivationCodeExistForMDN(subscList, xdmsHomePttId, false, persisterTxn);
            if (mdnList != null && !mdnList.isEmpty()) {
                knLogger.debug( methodName, "Updating the existing activation code - ");
                Collection<KnCorpSubscriberDTO> failedMdnList = activationInfoUtil.updateActivationCode(mdnList, xdmsHomePttId, persisterTxn);
                operationType = 1;
                updateUniqueActCode(failedMdnList, xdmsHomePttId, persisterTxn, operationType, activationCodeConfigMap, activationCodeMap);

            } else {
                // Insert activation code in to DG.TMPVASSUBSCRIPTIONKEYINFO table.
                knLogger.debug( methodName, "Inserting the activation code.. ");
                operationType = 2;
                Collection<KnCorpSubscriberDTO> failedMdnList = activationInfoUtil.insertActivationCode(subscList, null, xdmsHomePttId, persisterTxn);
                updateUniqueActCode(failedMdnList, xdmsHomePttId, persisterTxn, operationType, activationCodeConfigMap, activationCodeMap);
            }
            KnCorpSubscriberDTO corpSubscriberDTO = activationInfoUtil.getActCodeExtTime(mdn, clientType, xdmsHomePttId, false, persisterTxn);
            if(null != corpSubscriberDTO){
                responseMap.put(KnConstants.EXPIRY_TIME, String.valueOf(timesStampToDate(corpSubscriberDTO.getExpiryTime().getTime())));
                responseMap.put(KnConstants.ACTIVATION_TIME, String.valueOf(timesStampToDate(corpSubscriberDTO.getActivationTimestamp().getTime())));
            } else{
                responseMap.put(KnConstants.EXPIRY_TIME, String.valueOf(timesStampToDate(expiryTimeMap.get(clientType).getTime())));
                responseMap.put(KnConstants.ACTIVATION_TIME, String.valueOf(timesStampToDate(currentTimeInUTC.getTime())));
            }

            populate(respDTO);
            responseMap.put(KnConstants.STATUS, String.valueOf(respDTO.getStatus()));
            responseMap.put(KnConstants.STATUS_CODE, respDTO.getStatusCode());
            responseMap.put(KnConstants.MESSAGE, respDTO.getMessage());
            responseMap.put(KnConstants.MDN, mdn);
            responseMap.put(KnConstants.ACTIVATION_CODE, activationCodeMap.get(mdn));
            if (respDTO.getFailedDataList() != null && !respDTO.getFailedDataList().isEmpty()) {
                respDTO.setFailedDataList(respDTO.getFailedDataList());
            }
        } catch (KnCorpBOException e) {
            knLogger.error( methodName, "KnCorpBOException occured while generating activation code - ", e);
            populate(respDTO, e);
            responseMap.put("status", String.valueOf(respDTO.getStatus()));
            responseMap.put("statusCode", respDTO.getStatusCode());
            responseMap.put("message", respDTO.getMessage());
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while generating activation code - ",
                    new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDTO, e);
            responseMap.put("status", String.valueOf(respDTO.getStatus()));
            responseMap.put("statusCode", respDTO.getStatusCode());
            responseMap.put("message", respDTO.getMessage());
        }
        respDTO.setResponseMap(responseMap);
        knLogger.debug( methodName, "Returning Response - ", respDTO);
        return respDTO;
    }

    private Date timesStampToDate(long timestamp){
        Timestamp stamp = new Timestamp(timestamp);
        return new Date(stamp.getTime());
    }
}