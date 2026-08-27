/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.business.impl;

import com.kodiak.logger.KnLogger;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.exception.KnSystemException;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.xdms.server.common.KnXDMServerException;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dao.KnFactorySelector;
import com.kodiak.xdms.server.common.dao.persister.IXDMServerDAO;
import com.kodiak.xdms.server.common.dao.persister.db.KnDBConnectionException;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDirChgDTO;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDocChgDTO;
import com.kodiak.xdms.server.common.dto.common.KnCorpGpInfoDTO;
import com.kodiak.xdms.server.common.dto.common.KnCorpProfileDTO;
import com.kodiak.xdms.server.common.dto.common.KnDocChangeListDTO;
import com.kodiak.xdms.server.common.dto.common.KnSubsProfileDTO;
import com.kodiak.xdms.server.common.dto.persistdat.KnSubscriberPersistDTO;
import com.kodiak.xdms.server.common.framework.aas.KnAASException;
import com.kodiak.xdms.server.common.framework.aas.KnAASFramework;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorFramework;
import com.kodiak.xdms.server.common.resources.KnConstants;
import com.kodiak.xdms.server.common.resources.KnProfileTypes;
import com.kodiak.xdms.server.pubmgmt.business.IPubAuthInfoController;
import com.kodiak.xdms.server.pubmgmt.business.KnPubBOException;
import com.kodiak.xdms.server.pubmgmt.business.helper.KnPubInfoUtil;
import com.kodiak.xdms.server.pubmgmt.dao.KnPubFactorySelector;
import com.kodiak.xdms.server.pubmgmt.dao.persister.IPubXdmDAO;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnEmergencyConfigDocDTO;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnEmergencyMdnDTO;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnIPPubAuthListDTO;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnOpPubResponse;
import com.kodiak.xdms.server.pubmgmt.dto.common.*;
import com.kodiak.xdms.server.pubmgmt.dto.persistdat.KnPubAuthListPersistDTO;
import com.kodiak.xdms.server.pubmgmt.resources.KnErrorCodes;

import java.util.*;
import java.util.stream.Collectors;

import static com.kodiak.common.resources.KnConstants.*;
import static com.kodiak.xdms.server.pubmgmt.resources.KnConstants.EMERLOCREPORTINTVL;
import static com.kodiak.xdms.server.pubmgmt.resources.KnConstants.MS_CONFIG_PARAMSCOPE_100;
import static com.kodiak.xdms.server.pubmgmt.resources.KnErrorCodes.BOEntity.AUTH_DOC_NOT_EXISTS;
import static com.kodiak.xdms.server.pubmgmt.resources.KnErrorCodes.BOEntity.EMERGENCY_DOC_NOT_EXISTS;
import static com.kodiak.xdms.server.pubmgmt.resources.KnErrorCodes.Validator.*;

/**
 * Created by schandra on 19-12-2017.
 */
public class KnPubAuthInfoController implements IPubAuthInfoController {
    private static final KnLogger knLogger = KnLogger.getLogger(KnPubAuthInfoController.class);
    private KnValidatorFramework validatorFwk = null;
    private KnAASFramework authorizationFwk = null;
    private KnPubInfoUtil pubInfoUtil = null;
    private KnGenInfoUtil genInfoUtil = null;

    public KnPubAuthInfoController() {
        validatorFwk = KnValidatorFramework.getInstance(KnConstants.LIBRARY_NAME_PUB_MGMT);
        authorizationFwk = KnAASFramework.getInstance(KnConstants.LIBRARY_NAME_PUB_MGMT);
        pubInfoUtil = new KnPubInfoUtil();
        genInfoUtil = KnGenInfoUtil.getInstance();
    }

    @Override
    public KnIPPubAuthListDTO getAuthorizationList(KnIPPubAuthListDTO authListDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        KnIPPubAuthListDTO respDto = new KnIPPubAuthListDTO();
        final String methodName = "getAuthorizationList(KnIPPubAuthListDTO, persisterTxn)";
        KnOpPubResponse result = new KnOpPubResponse();
        knLogger.debug( methodName, "ENTRY -> Input DTO Passed : " + authListDTO);
        KnPubAuthListPersistDTO pubAuthListPersistDTO = null;
        try{

            knLogger.info(methodName, "retriving subscriber prifile");
            KnSubsProfileDTO subsProfile = pubInfoUtil.getProfileDetails(authListDTO.getAuthMDN(), authListDTO.getProfile(), true, KnConstants.FALSE, persisterTxn);
            KnSubscriberPersistDTO originator = new KnSubscriberPersistDTO();
            originator.setMdn(authListDTO.getAuthMDN());
            originator.setInputDTO(authListDTO);
            originator.setNetworkName(subsProfile.getNetworkName());
            originator.setPubSubscriptionType(subsProfile.getPublicSubscriptionType());
            originator.setServiceAuthStatus(subsProfile.getServiceAuthStatus());
            originator.setMcpttCompliance(subsProfile.getMcpttCompliance());
            originator.setMcpttID(subsProfile.getMcpttId());
            pubAuthListPersistDTO = new KnPubAuthListPersistDTO();
            pubAuthListPersistDTO.setInputDTO(authListDTO);
            pubAuthListPersistDTO.setPersistenceDTO(originator);
           
            knLogger.debug(methodName, "Invoking Authorization.");
            // 5. Authorizing the subscriber
            authorizationFwk.authorize(pubAuthListPersistDTO);
            knLogger.debug( methodName, "Authorized successfully.");
            String xdmServerId = subsProfile.getXdmsHome();
            int indexDocEtag = authListDTO.getIfNoneMatch();
            //Retrive Authorization Doc
            //- Validate if the User has a Authorized MDN document (has an authEntry in DG.AUTHORIZATION_DOC)
            IPubXdmDAO xdmServerDAO = KnPubFactorySelector.getDAOFactory( KnFactorySelector.DB).createXdmServerDAO(xdmServerId);
            KnAuthDocDTO authDocDTO = xdmServerDAO.getAuthorizationDocDetails(authListDTO.getAuthMDN(), true, persisterTxn);

            if (indexDocEtag > 0 && indexDocEtag == authDocDTO.getEtag()) {
                knLogger.info(methodName, "Auth Document not modifed :", authDocDTO.getEtag());
                throw new KnPubBOException(KnErrorCodes.BOEntity.DOC_NOT_MODIFIED, "Auth Document not modifed");
            }

            List<KnMCPTTPermInfoDTO> finalTuPermList=new ArrayList<>();
            //- Validate if the MDN has any MCPTT permissions set i.e, MDN exists as DG.MCPTT_PERM_INFO:AUTHORIZED_MDN
            List<KnMCPTTPermInfoDTO> tuPermInfoList = xdmServerDAO.getMCPTTPermInfo(authListDTO.getAuthMDN(), true, persisterTxn);

            // Retrieve the document data from DG.AUTHORIZATION_DOC, DG.MCPTT_PERM_INFO, DG.POCSUBSCRINFO:SERVICE_STATUS_AUTHUSER
            knLogger.debug( methodName, "validationg dto ", pubAuthListPersistDTO);
            validatorFwk.validate(pubAuthListPersistDTO);
            knLogger.debug( methodName, "Validated successfully!");
            //getting base mdn from profile or base mdn.
            List<String> tuList = tuPermInfoList.stream()
                    .map(l -> l.getTargetMdn())
                    .collect(Collectors.toList());
            List<String> baseMdn = pubInfoUtil.getRealMdns(tuList, true, persisterTxn);
            //getting map of profile mdn of base mdn.
            Map<String, List<String>> profileOFBaseMdnMap = pubInfoUtil.getProfileMdnListByBaseMdnsList(baseMdn, true, persisterTxn);

            for(Map.Entry<String, List<String>> baseMap:profileOFBaseMdnMap.entrySet()){
                BitSet finalPermBits = new BitSet(Long.SIZE);
                //basemdn
                Integer finalDiscreteEnabled=0;
                KnMCPTTPermInfoDTO tuBaseMdn = tuPermInfoList.stream().filter(e -> e.getTargetMdn().equals(baseMap.getKey().trim())).findAny().orElse(null);
                if(tuBaseMdn!=null){
                    finalPermBits.or(pubInfoUtil.convertLongToBitSet(tuBaseMdn.getPermBitset()));
                    finalDiscreteEnabled=tuBaseMdn.getDiscreetEnabled();
                }
                //profile mdn
                for(String profileMdn:baseMap.getValue()){
                    KnMCPTTPermInfoDTO tuPermObj = tuPermInfoList.stream().filter(e -> e.getTargetMdn().equals(profileMdn.trim())).findAny().orElse(null);
                    if(tuPermObj!=null){
                        finalPermBits.or(pubInfoUtil.convertLongToBitSet(tuPermObj.getPermBitset()));
                        finalDiscreteEnabled=finalDiscreteEnabled|tuPermObj.getDiscreetEnabled();
                    }
                }
                finalTuPermList.add(new KnMCPTTPermInfoDTO(
                        (long)pubInfoUtil.convertBitSetToLong(finalPermBits),
                        baseMap.getKey(),
                        finalDiscreteEnabled
                ));
            }

            // get target mdn list
            List<String> targetMdns = null;
            if(finalTuPermList.size() != 0){
                targetMdns = new ArrayList<>();
                for(KnMCPTTPermInfoDTO permInfoDTO : finalTuPermList){
                    targetMdns.add(permInfoDTO.getTargetMdn());
                }
            }

            //Fetch target user service status from pocsubscrinfo
            List<KnTargetMDNInfoDTO> targetMDNInfoDTOS = new ArrayList<>();
            Map<String, Integer> mdnVsServiceAuthMap = xdmServerDAO.getUserServiceStatus(targetMdns, true, persisterTxn);
            if (mdnVsServiceAuthMap != null) {
            for(KnMCPTTPermInfoDTO permInfoDTO : finalTuPermList){
                BitSet bitSet = KnGeneralUtil.convertLongToBitSet(permInfoDTO.getPermBitset());
                KnTargetMDNInfoDTO mdnInfoDTO = new KnTargetMDNInfoDTO();
                mdnInfoDTO.setTargetMdn(permInfoDTO.getTargetMdn());
                mdnInfoDTO.setFeaturePermissions( (bitSet.get(2)?1:0) + "," + (bitSet.get(3)?1:0) + "," + (bitSet.get(0)?1:0) + "," + (bitSet.get(1)?1:0)+ "," + (bitSet.get(4)?1:0) +","+ (bitSet.get(5)?1:0));
                int authstatus = 0;
                if(mdnVsServiceAuthMap.get(permInfoDTO.getTargetMdn()).equals(KnConstants.SERVICE_AUTH_STATUS.ACTIVATED.value())){
                    authstatus = 1;
                }
                mdnInfoDTO.setFeatureStatus( authstatus+ "," + permInfoDTO.getDiscreetEnabled());
                targetMDNInfoDTOS.add(mdnInfoDTO);
                }
            }
            respDto.setAuthMDN(authListDTO.getAuthMDN());
            respDto.setTargetMDNInfoDTOS(targetMDNInfoDTOS);
            respDto.setDocEtag(String.valueOf(authDocDTO.getEtag()));
            knLogger.debug(methodName, "Returning response :", respDto);

        } catch (KnAASException aex) {
            knLogger.error( methodName, "Authorization Exception occured :" + aex);
            throw aex;
        } catch (KnPubBOException ex) {
            knLogger.error( methodName, "PubBO Exception occured : " + ex);
            throw ex;
        } catch (KnDAOException ex) {
            knLogger.error( methodName, "DAO Exception occured : " + ex);
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new KnXDMServerException(AUTH_DOC_NOT_EXISTS, "Auth doc doesnot exists");
            } else if (ex instanceof KnDBConnectionException) {
                knLogger.error( methodName, "DAO DBConnection Exception occured :" + ex);
                throw new KnXDMServerException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);
            }
            throw new KnXDMServerException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Persistence exception occured : ", ex);
        } catch (KnValidationException vex) {
            knLogger.error( methodName, "Validation Exception occured :" + vex);

            throw new KnXDMServerException(vex.getErrorCode(), vex.getErrorMessage());
        } catch (Exception ex) {
            knLogger.error( methodName, "Exception occured while while retriving authorization doc info: " +  ex.getMessage());

            throw new KnSystemException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Unexpected exception occured while retriving mcptt info ", ex);
        }

        return respDto;
    }

    @Override
    public KnOpPubResponse updateAuthorizationList(KnIPPubAuthListDTO authListDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        final String methodName = "updateAuthorizationList(KnIPPubAuthListDTO, persisterTxn)";
        KnOpPubResponse result = new KnOpPubResponse();
        knLogger.debug( methodName, "ENTRY -> Input DTO Passed : " + authListDTO);
        KnPubAuthListPersistDTO pubAuthListPersistDTO = null;
        try{
            knLogger.info(methodName, "retriving subscriber prifile");
            KnSubsProfileDTO subsProfile = pubInfoUtil.getProfileDetails(authListDTO.getAuthMDN(), authListDTO.getProfile(), true, KnConstants.FALSE, persisterTxn);
            KnSubscriberPersistDTO originator = new KnSubscriberPersistDTO();
            originator.setMdn(authListDTO.getAuthMDN());
            originator.setInputDTO(authListDTO);
            originator.setNetworkName(subsProfile.getNetworkName());
            originator.setPubSubscriptionType(subsProfile.getPublicSubscriptionType());
            originator.setServiceAuthStatus(subsProfile.getServiceAuthStatus());
            originator.setMcpttCompliance(subsProfile.getMcpttCompliance());
            originator.setMcpttID(subsProfile.getMcpttId());
            String xdmServerId = subsProfile.getXdmsHome();
            int indexDocEtag = authListDTO.getIfNoneMatch();
            pubAuthListPersistDTO = new KnPubAuthListPersistDTO();
            pubAuthListPersistDTO.setPersistenceDTO(originator);
            pubAuthListPersistDTO.setInputDTO(authListDTO);
            knLogger.debug(methodName, "Invoking Authorization.");
            // 5. Authorizing the subscriber
            authorizationFwk.authorize(pubAuthListPersistDTO);
            knLogger.debug( methodName, "Authorized successfully.");
            pubAuthListPersistDTO.setMdn(subsProfile.getMdn());
            pubAuthListPersistDTO.setCorpId(subsProfile.getCorpId());

            IPubXdmDAO xdmServerDAO = KnPubFactorySelector.getDAOFactory( KnFactorySelector.DB).createXdmServerDAO(xdmServerId);
            String targetMdn = authListDTO.getTargetMDNInfoDTOS().iterator().next().getTargetMdn();
            String featureStatus = authListDTO.getTargetMDNInfoDTOS().iterator().next().getFeatureStatus();
            String[] fstatus = featureStatus.split(",");
            int userServStatusflag = Integer.parseInt(fstatus[0]);
            int userServStatus = (userServStatusflag == 1)? KnConstants.SERVICE_AUTH_STATUS.ACTIVATED.value():KnConstants.SERVICE_AUTH_STATUS.DEACTIVATED.value();
            int discreetListenerStatus = Integer.parseInt(fstatus[1]);
            knLogger.info(methodName, "userServStatus - ", userServStatus, "discreetListenerStatus", discreetListenerStatus);
            List<String> targetMdnList = new ArrayList<String>() {{add(targetMdn);}};
            Map<String, Integer> mdnCorpIdMap = xdmServerDAO.getTargetMdnsCorpid(targetMdnList, persisterTxn);
            pubAuthListPersistDTO.setMdnCorpIdMap(mdnCorpIdMap);

            List<KnMemberDTO> memberDTOs = xdmServerDAO.getMembersClientType(targetMdnList ,persisterTxn);
            pubAuthListPersistDTO.setPocMembers(memberDTOs);

            KnAuthDocDTO authDocDTO = xdmServerDAO.getAuthorizationDocDetails(authListDTO.getAuthMDN(), false, persisterTxn);
            if (indexDocEtag > 0 && authDocDTO.getEtag() != indexDocEtag) {
                knLogger.error(methodName, "Etag mismatch while updating auth doc  : ", authDocDTO.getEtag());
                throw new KnPubBOException(KnErrorCodes.BOEntity.DOC_MODIFIED, "Etag mismatch while updating auth doc");

            }
            BitSet bitSet = KnGeneralUtil.convertHexStringToBitSet(subsProfile.getActiveFS2());
            knLogger.info(methodName, "Active bit set ", bitSet);

            if(!(bitSet.get(com.kodiak.common.resources.KnConstants.FEATURE_SET.DISCRETELISTENING.value())) && !(bitSet.get(com.kodiak.common.resources.KnConstants.FEATURE_SET.USERENABLEDISABLE.value()))){
                throw new KnValidationException(MDN_DOESNOT_HAVE_PERMISSIONS, "Discreet Listening Permission AND User Enable/Disable Features are disable for auth user ",null);
            }

            KnMCPTTPermInfoDTO permInfoDTO = xdmServerDAO.getMCPTTPermInfoOnTarget(authListDTO.getAuthMDN(), targetMdn, persisterTxn);
            BitSet perBitset = KnGeneralUtil.convertLongToBitSet(permInfoDTO.getPermBitset());
            int discreteEnabledDBValue = permInfoDTO.getDiscreetEnabled();
            knLogger.info(methodName, "Permission  bit set ", perBitset);
            if(discreteEnabledDBValue != discreetListenerStatus) {
                if (!(bitSet.get(com.kodiak.common.resources.KnConstants.FEATURE_SET.DISCRETELISTENING.value()) && perBitset.get(com.kodiak.common.resources.KnConstants.MCPTT_PERMISSION_BIT.DISCRETELISTENING.value()))) {
                    throw new KnValidationException(MDN_DOESNOT_HAVE_DISCREETE_PERM, "Discreet Listening Permission are disable for auth user ",null);
                }
            }

            KnSubsProfileDTO targetMdnProfile = pubInfoUtil.getProfileDetails(targetMdn, authListDTO.getProfile(), true, KnConstants.FALSE, persisterTxn);
            if(targetMdnProfile.getServiceAuthStatusAU() != userServStatus){
                if( !( bitSet.get(com.kodiak.common.resources.KnConstants.FEATURE_SET.USERENABLEDISABLE.value()) && perBitset.get(com.kodiak.common.resources.KnConstants.MCPTT_PERMISSION_BIT.USERENABLE.value()))){
                    throw new KnValidationException(MDN_DOESNOT_HAVE_USERENABLE_PERM, "Authorized User doesnot have permission to perform", null);
                }
            }

            if(discreetListenerStatus == 1 ){
                if(pubInfoUtil.getCorpProfileDetails(String.valueOf(subsProfile.getCorpId()), KnProfileTypes.CORP_PROFILE, persisterTxn).getPrivacyAmbDiscListenFlag().equals(1)){
                    if(targetMdnProfile.getClientMajorVersion()<PROTOCOL_VERSION_16){
                            knLogger.error(methodName, "privacy opt feature enabled & target is PV <16, So AU can't do Discreet Listening on TU");
                            throw new KnValidationException(MDN_DOESNOT_HAVE_DISCREETE_PERM, "Discreet Listening Permission are disable for auth user",null);
                    }
                }
            }
            knLogger.debug(methodName," ServiceAuth status of "+targetMdn+" is "+targetMdnProfile.getServiceAuthStatus()+" and the discrete value is "+discreetListenerStatus + " and discreteEnabledDBValue" + discreteEnabledDBValue);
            if (discreteEnabledDBValue == 1 && discreetListenerStatus == 1) {
                //MINT-17225
                knLogger.debug(methodName, "skipping ", MDN_IS_IN_NON_ACTIVATED_STATE, " validation as discreteEnabledDBValue and discreetListenerStatus both are 1");
            } else if (discreetListenerStatus == 1 && !(targetMdnProfile.getServiceAuthStatus() == KnConstants.SERVICE_AUTH_STATUS.ACTIVATED.value())) {
                knLogger.error(methodName, "Target mdn is not in activated state");
                throw new KnValidationException(MDN_IS_IN_NON_ACTIVATED_STATE, "Discreet Listening Permission are disable for auth user", null);
            }

            knLogger.debug( methodName, "validationg dto ", pubAuthListPersistDTO);
            validatorFwk.validate(pubAuthListPersistDTO);
            knLogger.debug( methodName, "Validated successfully!");
            //Update Auth doc table
            long lastProfileUpdateTime = Calendar.getInstance().getTimeInMillis();

            xdmServerDAO.updateAuthorizationDocDetails(authListDTO.getAuthMDN(), lastProfileUpdateTime, persisterTxn);
            
            List<String> baseMdnList = new ArrayList<>();
            baseMdnList.add(targetMdn);
            Map<String, List<String>> profileOFBaseMdnMap = pubInfoUtil.getProfileMdnListByBaseMdnsList(baseMdnList, false, persisterTxn);
            List<String> profileMdns = profileOFBaseMdnMap.get(targetMdn);
            List<String> targetMdns = new ArrayList<>();
            targetMdns.add(targetMdn.trim());
            if(profileMdns!=null&&!profileMdns.isEmpty())
            {
            targetMdns.addAll(profileMdns);
            }

            knLogger.debug(methodName, "targetMdns--->", KnGDPRTemplate.mdnList(targetMdns), "targetMdn:", KnGDPRTemplate.mdn(targetMdn));
            //MINT-17227
            List<KnMCPTTPermInfoDTO> permInfoDTOList = xdmServerDAO.getMCPTTPermInfo(authListDTO.getAuthMDN(), false, persisterTxn);
            if (discreetListenerStatus == 1) {
                int count = 0;
                for(KnMCPTTPermInfoDTO permissionInfoDTO : permInfoDTOList){
                    if(permissionInfoDTO.getDiscreetEnabled()==1){
                        if (!(targetMdns.contains(targetMdn)))
                            count++;
                    }
                }
                if(count > 0){
                    throw new KnValidationException(MDN_DOESNOT_HAVE_DISCREETE_PERM, "Authorized User is already having Discreet enabled on one target", null);
                }
            }

            if(discreteEnabledDBValue != discreetListenerStatus) {
                //update DG.MCPTT_PERM_INFO:DISCREET_ENABLED
                xdmServerDAO.updateMCPTTDiscreetEnabled(authListDTO.getAuthMDN(), targetMdns, discreetListenerStatus, persisterTxn);
                if(discreetListenerStatus == 1 ){
                    xdmServerDAO.updateDiscreetEnabledForTarget(targetMdns,discreetListenerStatus,lastProfileUpdateTime,persisterTxn);
                }
                result.setDiscreetChanged(true);
            }
            List<String> authMdsList = new ArrayList<>();
            //if(targetMdnProfile.getServiceAuthStatusAU() != userServStatus){
            //Update service auth status

            int finalServiceAuthStatus = KnGenInfoUtil.calculateServiceAuthStatus(targetMdnProfile.getServiceAuthStatusOP(), userServStatus);
            xdmServerDAO.updateServiceAuthStatusForTarget(targetMdns, userServStatus, finalServiceAuthStatus, lastProfileUpdateTime, persisterTxn);
            if (finalServiceAuthStatus != targetMdnProfile.getServiceAuthStatus()) {
                result.setServiceAuthChange(true);
            }
            result.setServiceAuthStatus(finalServiceAuthStatus);
            result.setServiceStatusAuthOP(targetMdnProfile.getServiceAuthStatusOP());
            result.setServiceStatusAuthUser(userServStatus);
            result.setDispatchType(targetMdnProfile.getDispatchType());
            //fetch and update all auth_users , authorization doc for target mdn
            authMdsList = xdmServerDAO.getAllAuthMdsForTarget(targetMdn, persisterTxn);

            xdmServerDAO.updateMdnsEtagForAuthDoc(authMdsList, lastProfileUpdateTime, persisterTxn);
            //}

            //profile notification
            knLogger.debug(methodName, "Profile Mdns - ", KnGDPRTemplate.mdnList(profileMdns));
			if (profileMdns != null && !profileMdns.isEmpty())
           {
            Map<String, Collection<KnDocChangeListDTO>> profileMdnEtagMap = pubInfoUtil.profileMdnEtagUpdate(
                    null
                    , String.valueOf(subsProfile.getCorpId()),profileMdns
                    ,null, subsProfile.getXdmsHome(), persisterTxn);
            result.setProfileMdnEtagMap(profileMdnEtagMap);
            if(profileMdnEtagMap != null && !profileMdnEtagMap.isEmpty()){
            	result.setMcsXcapRootUriMap(genInfoUtil.getXCAPRootURI(new ArrayList<>(profileMdnEtagMap.keySet()), persisterTxn, true));
            }
           }
            
            if(!authMdsList.contains(authListDTO.getAuthMDN())){
                knLogger.debug(methodName, "owner MDN is already part of target auth list");
                authMdsList.add(authListDTO.getAuthMDN());
                knLogger.debug(methodName, "owner MDN not added - ", KnGDPRTemplate.mdnList(authMdsList));
            }
            if (result.isServiceAuthChange() && userServStatus == KnConstants.SERVICE_AUTH_STATUS.DEACTIVATED.value()) {
                authMdsList.addAll(targetMdns);
            }
            List<String> authDistinctMdnList = authMdsList.stream().distinct().collect(Collectors.toList());
            knLogger.debug(methodName, "authDistinctMdsList--",KnGDPRTemplate.mdnList(authDistinctMdnList));

            result.setMdnList(authDistinctMdnList);
            
            if(profileMdns!=null&&!profileMdns.isEmpty())
            {
            result.setProfileMdnList(profileMdns);
            }
            IXDMServerDAO commonXDMServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmServerId);
            List<Integer> previousEtags = commonXDMServerDAO.getCurrentEtagsForDirDoc(authDistinctMdnList, persisterTxn);

            //update the dir doc etag
            commonXDMServerDAO.updateEtagForDirDoc(authDistinctMdnList, persisterTxn);
            knLogger.debug(methodName, "Successfully updated the xdm directory");
            //populating the Subscriber config doc DTO

            List<KnOPDirChgDTO> dirChgDTOs = new ArrayList<KnOPDirChgDTO>();
            KnOPDocChgDTO docChgDTO;
            KnOPDirChgDTO dirChgDTO;
            int i = 0;

            for (String mdn : authDistinctMdnList) {
                KnSubsProfileDTO subsProfileInfoDTO = pubInfoUtil.getProfileDetails(mdn, authListDTO.getProfile(), true, KnConstants.FALSE, persisterTxn);
                dirChgDTO = new KnOPDirChgDTO();
                docChgDTO = new KnOPDocChgDTO();
                Collection<KnOPDocChgDTO> chgDocList = new ArrayList<KnOPDocChgDTO>();
                if(!(mdn.equals(targetMdn))){
                    docChgDTO.setDocumentChgType(KnConstants.DOC_CHANGE_TYPE.REPLACE.value());
                    String authListDocUri = pubInfoUtil.generateAuthListSelUri(mdn);
                    docChgDTO.setDocUri(authListDocUri);
                    docChgDTO.setNewEtag(String.valueOf(lastProfileUpdateTime));
                    chgDocList.add(docChgDTO);
                }else{
                    docChgDTO.setDocumentChgType(KnConstants.DOC_CHANGE_TYPE.REPLACE.value());
                    String authListDocUri = pubInfoUtil.generateSubsConfigSelUri(mdn);
                    docChgDTO.setDocUri(authListDocUri);
                    docChgDTO.setNewEtag(String.valueOf(lastProfileUpdateTime));
                    chgDocList.add(docChgDTO);
                }
                //populating the XDM Directory DTO
                dirChgDTO.setDocChgDTO(chgDocList);
                dirChgDTO.setXcapRootURI(genInfoUtil.getXCAPRootURI(mdn, persisterTxn));
                dirChgDTO.setPocHome(subsProfileInfoDTO.getPocHome());
                dirChgDTO.setPresenceHome(subsProfileInfoDTO.getPresenceHome());
                String dirDocUri = genInfoUtil.generateDirDocUri(mdn);
                dirChgDTO.setDirUri(dirDocUri);
                dirChgDTO.setDirPrevEtag(String.valueOf(previousEtags.get(i)));
                int newEtag = previousEtags.get(i) + 1;
                dirChgDTO.setDirNewEtag(String.valueOf(newEtag));
                String protocolVersion = subsProfileInfoDTO.getProtocolVersion();
                dirChgDTO.setProtoVersion((protocolVersion==null)?"1":protocolVersion.substring(0, protocolVersion.indexOf('.')));
                dirChgDTO.setClientType(subsProfileInfoDTO.getClientType());
                //populating the Dir chg DTO to response
                dirChgDTOs.add(dirChgDTO);
                i++;
            }
            result.setActiveFS2(targetMdnProfile.getActiveFS2());
            result.setDirChgDTOs(dirChgDTOs);
            result.setDocEtag(String.valueOf(lastProfileUpdateTime));
        }catch (KnAASException aex) {
            knLogger.error( methodName, "Authorization Exception occured :" + aex);
            throw aex;
        } catch (KnPubBOException ex) {
            knLogger.error( methodName, "PubBO Exception occured : " , ex);
            throw ex;
        } catch (KnDAOException ex) {
            knLogger.error( methodName, "DAO Exception occured : " , ex);
            if (ex instanceof KnDBConnectionException) {
                knLogger.error( methodName, "DAO DBConnection Exception occured :" + ex);
                throw new KnXDMServerException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);
            }
            throw new KnXDMServerException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Persistence exception occured : ", ex);
        } catch (KnValidationException vex) {
            knLogger.error( methodName, "Validation Exception occured :" , vex);

            throw new KnXDMServerException(vex.getErrorCode(), vex.getErrorMessage());
        } catch (Exception ex) {
            knLogger.error( methodName, "Exception occured while updating Authorization info: " , ex.getMessage());

            throw new KnSystemException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Unexpected exception occured while updating Authorization doc ", ex);
        }
        return result;
    }


    public KnEmergencyConfigDocDTO getEmergencyConfigDoc(KnIPPubAuthListDTO authListDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException{
        KnEmergencyConfigDocDTO respDto = new KnEmergencyConfigDocDTO();
        final String methodName = "getEmergencyConfigDoc(KnIPPubAuthListDTO, persisterTxn)";
        KnOpPubResponse result = new KnOpPubResponse();
        knLogger.debug( methodName, "ENTRY -> Input DTO Passed : " + authListDTO);
        KnPubAuthListPersistDTO pubAuthListPersistDTO = null;
        try{

            knLogger.info(methodName, "retriving subscriber prifile");
            KnSubsProfileDTO subsProfile = pubInfoUtil.getProfileDetails(authListDTO.getAuthMDN(), authListDTO.getProfile(), true, KnConstants.FALSE, persisterTxn);
            KnSubscriberPersistDTO originator = new KnSubscriberPersistDTO();
            originator.setMdn(authListDTO.getAuthMDN());
            originator.setInputDTO(authListDTO);
            originator.setMcpttCompliance(subsProfile.getMcpttCompliance());
            originator.setMcpttID(subsProfile.getMcpttId());
            originator.setNetworkName(subsProfile.getNetworkName());
            originator.setPubSubscriptionType(subsProfile.getPublicSubscriptionType());
            originator.setServiceAuthStatus(subsProfile.getServiceAuthStatus());
            pubAuthListPersistDTO = new KnPubAuthListPersistDTO();
            pubAuthListPersistDTO.setInputDTO(authListDTO);
            pubAuthListPersistDTO.setPersistenceDTO(originator);
            knLogger.debug(methodName, "Invoking Authorization.");
            // 5. Authorizing the subscriber
            authorizationFwk.authorize(pubAuthListPersistDTO);
            knLogger.debug( methodName, "Authorized successfully.");
            String xdmServerId = subsProfile.getXdmsHome();
            int indexDocEtag = authListDTO.getIfNoneMatch();
            //Retrive Authorization Doc
            //- Validate if the User has a Authorized MDN document (has an authEntry in DG.AUTHORIZATION_DOC)
            IPubXdmDAO xdmServerDAO = KnPubFactorySelector.getDAOFactory( KnFactorySelector.DB).createXdmServerDAO(xdmServerId);
            KnEmergencyDocDTO  authDocDTO = xdmServerDAO.getEmergencyDocDetails(authListDTO.getAuthMDN(), true, persisterTxn);

            if (indexDocEtag > 0 && indexDocEtag == authDocDTO.getEtag()) {
                knLogger.info(methodName, "emergency Document not modifed :", authDocDTO.getEtag());
                throw new KnPubBOException(KnErrorCodes.BOEntity.DOC_NOT_MODIFIED, "Auth Document not modifed");
            }

            //- Validate if the MDN has Emergency Feature Enabled i.e, ActiveFS1 for Emergency Bit is  enabled.
            boolean emergencyBit = KnGeneralUtil.getFeatureBitValue( subsProfile.getActiveFS2(),
                            com.kodiak.common.resources.KnConstants.FEATURE_SET.EMERGENCY.value());
            if(!emergencyBit){
                throw new KnValidationException(MDN_DOESNOT_HAVE_EMERGENCY_PERM, "Owner mdn doesn't have emergency permissions", null);
            }
            knLogger.debug( methodName, "Validated successfully!");
            IXDMServerDAO xdmDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(genInfoUtil.retrieveLocalXDMPttServerId());
            KnCorpProfileDTO subsProfileCorp = xdmDAO.getCorporateProfile(String.valueOf(subsProfile.getCorpId()), persisterTxn);
            int protocolVersion = subsProfile.getClientMajorVersion();
            int clusterId = Integer.parseInt(System.getenv(CLUSTERID_ENV_NAME));
            Map<String, String> paramNameValueMapCommon = genInfoUtil.retrieveMSSvcsCommonConfigWithParmScope(clusterId, MS_CONFIG_PARAMSCOPE_100, true, persisterTxn);
            String emConfigTimerFeatureFlag = paramNameValueMapCommon.get(EMERGENCY_CONF_TIMER_FEATURE);
            boolean emConfigTimerBit = Optional.ofNullable(subsProfileCorp.getXdmCorpFS2Set())
                    .map(set -> com.kodiak.common.resources.KnGeneralUtil.getFeatureBitValue(set, XDMCORPFS2_SET.EMERGENCY_CONF_TIMER_FEATURE.value()))
                    .orElse(false);
            knLogger.debug(methodName, "emConfigTimerFeatureFlag : " + emConfigTimerFeatureFlag, "emConfigTimerBit : " + emConfigTimerBit, "protocolVersion : " + protocolVersion);

            respDto.setDocEtag(String.valueOf(authDocDTO.getEtag()));
            //Emergency configuration from poc sub scrinfo
            respDto.setCallOrigMode(subsProfile.getEmergCallType());
            respDto.seteStateCancelPerm(subsProfile.getEmergCancelPermission());
            respDto.seteStateInitPerm(subsProfile.getEmergInitiatePermission());
            if (protocolVersion >= PROTOCOL_VERSION_28 && emConfigTimerFeatureFlag.equals("1") && emConfigTimerBit) {
                String emergConfTimer = null != subsProfile.getEmergConfigTimer() ? subsProfile.getEmergConfigTimer()
                        : paramNameValueMapCommon.get(DEFAULT_EMERGENCY_TIMER);
                respDto.setEmergConfigTimer(emergConfTimer);
                knLogger.debug(methodName, "emergency Config Timer value:", emergConfTimer);
            }

            String eLocPollInterval = paramNameValueMapCommon.get(EMERLOCREPORTINTVL);
            knLogger.debug(methodName, "param value of emergency location poll interval ", eLocPollInterval);
            //Emergency configuration from poc sub scr add info
            KnSubsAddEmgrConfigDTO addEmgrConfig = xdmServerDAO.getSubAddEmergencyConfig(authListDTO.getAuthMDN(), true, persisterTxn);
            respDto.seteSelMode(addEmgrConfig.getEmergencyDestType());
            respDto.setOrigEmcAlrtInd(addEmgrConfig.getEmergOriginBitset());
            if(eLocPollInterval != null) {
                respDto.seteLocPollTimer(Integer.parseInt(eLocPollInterval));
            }

            //EmergencyList Destination info
            List<KnEmgrDestinationInfoDTO> destinationInfoDTOS = xdmServerDAO.getEmergencyDestinationInfo(authListDTO.getAuthMDN(), true, persisterTxn);
            List<KnEmergencyMdnDTO> knEmergencyMdnDTOsList = new ArrayList<>();
            for (KnEmgrDestinationInfoDTO destinationInfoDTO : destinationInfoDTOS) {
                KnEmergencyMdnDTO mdnDTO = new KnEmergencyMdnDTO();
                mdnDTO.setPriority(destinationInfoDTO.getPriority());
                mdnDTO.setType(destinationInfoDTO.getType());
                if (destinationInfoDTO.getType() == 1) {
                    mdnDTO.setEntryMdn(com.kodiak.xdms.server.pubmgmt.resources.KnConstants.TEL_COLON_PLUS + destinationInfoDTO.getDestination());
                } else {
                    String emergDest = destinationInfoDTO.getDestination();
                    if (emergDest != null && !emergDest.isEmpty()) {
                        KnCorpGpInfoDTO corpGroupInfo = xdmDAO.getCorpGroupInfoList(Integer.parseInt(emergDest), persisterTxn);
                        mdnDTO.setEntryMdn(
                                com.kodiak.xdms.server.pubmgmt.resources.KnConstants.TEL_COLON_PLUS +
                                        authListDTO.getAuthMDN() +
                                        com.kodiak.xdms.server.pubmgmt.resources.KnConstants.CORP_GROUP_URI +
                                        corpGroupInfo.getCorpId() + "_" + emergDest
                        );
                    }
                }
                knEmergencyMdnDTOsList.add(mdnDTO);
            }
            respDto.setMdnEntry(knEmergencyMdnDTOsList);
            knLogger.debug(methodName, "Returning response :", respDto);
        } catch (KnAASException aex) {
            knLogger.error( methodName, "Authorization Exception occured :" + aex);
            throw aex;
        } catch (KnPubBOException ex) {
            knLogger.error( methodName, "PubBO Exception occured : " + ex);
            throw ex;
        } catch (KnDAOException ex) {
            knLogger.error( methodName, "DAO Exception occured : " + ex);
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new KnXDMServerException(EMERGENCY_DOC_NOT_EXISTS , "Emergency doc doesnot exists");
            } else if (ex instanceof KnDBConnectionException) {
                knLogger.error( methodName, "DAO DBConnection Exception occured :" + ex);
                throw new KnXDMServerException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);
            }
            throw new KnXDMServerException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Persistence exception occured : ", ex);
        } catch (KnValidationException vex) {
            knLogger.error( methodName, "Validation Exception occured :" + vex);

            throw new KnXDMServerException(vex.getErrorCode(), vex.getErrorMessage());
        } catch (Exception ex) {
            knLogger.error( methodName, "Exception occured while while retriving authorization doc info: " +  ex.getMessage());

            throw new KnSystemException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Unexpected exception occured while retriving mcptt info ", ex);
        }

        return respDto;
    }
}
