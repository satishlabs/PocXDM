/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ***************************************************************************
 * File name:   KnXDMProvMediator.java
 * Subsystem:   XDM Mediator
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ravi Shanker .P       Jan 11, 2011        7.0
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
 * ******************************************************************************
 */

package com.kodiak.xdms.mediator.helper;

import com.kodiak.common.commdto.common.*;
import com.kodiak.common.commdto.request.*;
import com.kodiak.common.commdto.response.*;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersistenceException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.exception.KnException;
import com.kodiak.logger.KnLogger;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.xdms.mediator.KnMediatorConstants;
import com.kodiak.xdms.server.common.KnXDMServerException;
import com.kodiak.xdms.server.common.dto.clientdat.KnIPChangeMDNInfoDTO;
import com.kodiak.xdms.server.subsmgmt.KnProvException;
import com.kodiak.xdms.server.subsmgmt.business.KnProvBOException;
import com.kodiak.xdms.server.subsmgmt.clientIntf.IProvClientIntf;
import com.kodiak.xdms.server.subsmgmt.clientIntf.impl.KnProvClientImpl;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.*;
import com.kodiak.xdms.server.subsmgmt.dto.common.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class KnXDMProvMediator {
    private static final KnLogger knLogger = KnLogger.getLogger(KnXDMProvMediator.class);

    private static final String className = KnXDMProvMediator.class.getName();
    IProvClientIntf provClientIntf;
    private static boolean isInitialized = false;
    private static KnXDMProvMediator instance = null;

    private KnXDMProvMediator() {
        provClientIntf = KnProvClientImpl.getInstance();
    }

    public static KnXDMProvMediator getInstance() {
        if (!isInitialized) {
            instance = new KnXDMProvMediator();
            isInitialized = true;
        }
        knLogger.info("getInstance()", "instance of KnXDMProvMediator");
        return instance;
    }

    /**
     * method to create the Subscriber.
     *
     * @param subsProvInputDTO KnXDMSubsProvDTO
     * @param persisterTxn     KnPersisterTxn
     * @return KnOPProvDTO response DTO received from the server.
     * @throws KnXDMServerException generic exception thrown by the Server
     */
    public KnOPCreateSubsInfoDTO createSubscriber(KnXDMSubsProvInfoDTO subsProvInputDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "createSubscriber(KnXDMSubsProvDTO, KnPersisterTxn)";
        KnOPCreateSubsInfoDTO respDTO = null;
        knLogger.info(methodName, "create Subscriber request with DTO - ", subsProvInputDTO
        );
        try {
            //populating the Library DTO before calling the API
            KnIPSubsProvInfoDTO subsProvInfoDTO = populateSubsProvInfoDTO(subsProvInputDTO);
            knLogger.debug(methodName, "KnIPSubsProvInfoDTO subsProvInfoDTO",subsProvInfoDTO);
            respDTO = provClientIntf.createSubscriber(subsProvInfoDTO, persisterTxn);
            knLogger.debug(methodName, "Successfully Create Subscriber Profile");

        } catch (KnXDMServerException e) {
            knLogger.error(methodName, "Failed to create Subscriber ");
            throw e;
        }
        return respDTO;
    }

    /**
     * method to populate the Library Input DTO
     *
     * @param subsProvInputDTO KnXDMSubsProvDTO
     * @return KnIPSubsProvInfoDTO Library input DTO
     */
    public KnIPSubsProvInfoDTO populateSubsProvInfoDTO(KnXDMSubsProvInfoDTO subsProvInputDTO) {
        String methodName = "populateSubsProvInfoDTO(KnXDMSubsProvDTO)";
        knLogger.debug(methodName, "XDM Subs prov Info DTO - ", subsProvInputDTO);
        KnIPSubsProvInfoDTO subsProvInfoDTO = new KnIPSubsProvInfoDTO();
        subsProvInfoDTO.setRecordingStatus(subsProvInputDTO.getRecordingStatus());
        subsProvInfoDTO.setMdn(subsProvInputDTO.getMdn());
        subsProvInfoDTO.setIMEI(subsProvInputDTO.getIMEI());
        subsProvInfoDTO.setNetworkName(subsProvInputDTO.getNetworkName());
        subsProvInfoDTO.setPublicSubscriptionType(subsProvInputDTO.getPublicSubscriptionType());
        subsProvInfoDTO.setCorporateSubscriptionType(subsProvInputDTO.getCorporateSubscriptionType());
        subsProvInfoDTO.setExtCorpId(subsProvInputDTO.getExtCorpId());
        subsProvInfoDTO.setPairingInd(subsProvInputDTO.getPairingInd());
        subsProvInfoDTO.setAffiliateId(subsProvInputDTO.getAffiliateId());
        subsProvInfoDTO.setPayType(subsProvInputDTO.getPayType());
        subsProvInfoDTO.setRoamingTypes((ArrayList<Integer>) subsProvInputDTO.getRoamingType());
        subsProvInfoDTO.setIfMatch(subsProvInputDTO.getIfMatch());
        subsProvInfoDTO.setIfNoneMatch(subsProvInputDTO.getIfNoneMatch());
        subsProvInfoDTO.setSubsFS2(subsProvInputDTO.getSubsFS2());
        subsProvInfoDTO.setEmailAddress(subsProvInputDTO.getEmailAddress());
        subsProvInfoDTO.setDispatchGroupMember(subsProvInputDTO.getDispatchGroupMember());
        subsProvInfoDTO.setSubsClientType(subsProvInputDTO.getSubscriberClientType());
        subsProvInfoDTO.setCorporateName(subsProvInputDTO.getCorporateName());
        subsProvInfoDTO.setAccountId(subsProvInputDTO.getAccountId());
        subsProvInfoDTO.setAutoPair(subsProvInputDTO.isAutoPair());
        subsProvInfoDTO.setHierarchyType(subsProvInputDTO.getHierarchyType());
        subsProvInfoDTO.setSubsDefPttRadio(subsProvInputDTO.getSubsDefPttRadio());
        subsProvInfoDTO.setProvFSMap(subsProvInputDTO.getProvFSMap());
        subsProvInfoDTO.setUfmi(subsProvInputDTO.getUfmi());
        subsProvInfoDTO.setiDenUserName(subsProvInputDTO.getiDenUserName());
        subsProvInfoDTO.setiDenPassword(subsProvInputDTO.getiDenPassword());
        subsProvInfoDTO.setiDenBusUnitId(subsProvInputDTO.getiDenBusUnitId());
        subsProvInfoDTO.setPkgIdMap(subsProvInputDTO.getPkgIdMap());
        subsProvInfoDTO.setLicenseType(subsProvInputDTO.getLicenseType());
        subsProvInfoDTO.setFirstNetIndicator(subsProvInputDTO.getFirstNetIndicator());
        subsProvInfoDTO.setUserId(subsProvInputDTO.getUserId());
        subsProvInfoDTO.setMcId(subsProvInputDTO.getMcId());
        subsProvInfoDTO.setMcpttId(subsProvInputDTO.getMcpttId());
        subsProvInfoDTO.setMcVideoId(subsProvInputDTO.getMcVideoId());
        subsProvInfoDTO.setMcDataId(subsProvInputDTO.getMcDataId());
        subsProvInfoDTO.setMcsCompliance(subsProvInputDTO.getMcsCompliance());
        subsProvInfoDTO.setOnBoardingEmailReqd(subsProvInputDTO.getOnBoardingMailReq());
        subsProvInfoDTO.setCameraType(subsProvInputDTO.getCameraType());
        subsProvInfoDTO.setExtGatewayId(subsProvInputDTO.getExtGatewayId());
        KnXDMSubsCameraInfo xdmSubsCameraInfo = subsProvInputDTO.getCameraInfo();
        if(xdmSubsCameraInfo!=null) {
            subsProvInfoDTO.setCameraInfo(new KnSubsCameraInfo(xdmSubsCameraInfo.getIpIdentifier(), xdmSubsCameraInfo.getCameraSerialId()));
        }
        if(subsProvInputDTO.getAliasInfoList() != null){
            List<KnSubsAliasInfoDTO> aliasInfoList=new ArrayList<>();
            for(KnXDMSubsAliasInfoDTO aliasInfo : subsProvInputDTO.getAliasInfoList()){
                KnSubsAliasInfoDTO aliasInfoDTO= new KnSubsAliasInfoDTO();
                aliasInfoDTO.setAliasId(aliasInfo.getAliasId());
                aliasInfoDTO.setAliasIdIssuer(aliasInfo.getAliasIdIssuer());
                aliasInfoDTO.setAliasIdType(aliasInfo.getAliasIdType());
                aliasInfoList.add(aliasInfoDTO);
            }
            subsProvInfoDTO.setAliasInfoList(aliasInfoList);
        }

        if(subsProvInputDTO.getAddAliasInfoList() != null && !subsProvInputDTO.getAddAliasInfoList().isEmpty()){
            List<KnSubsAliasInfoDTO> aliasInfoList=new ArrayList<>();
            for(KnXDMSubsAliasInfoDTO aliasInfo : subsProvInputDTO.getAddAliasInfoList()){
                KnSubsAliasInfoDTO aliasInfoDTO= new KnSubsAliasInfoDTO();
                aliasInfoDTO.setAliasId(aliasInfo.getAliasId());
                aliasInfoDTO.setAliasIdIssuer(aliasInfo.getAliasIdIssuer());
                aliasInfoDTO.setAliasIdType(aliasInfo.getAliasIdType());
                aliasInfoList.add(aliasInfoDTO);
            }
            subsProvInfoDTO.setAddAliasInfoList(aliasInfoList);
        }

        if(subsProvInputDTO.getRemoveAliasInfoList() != null && !subsProvInputDTO.getRemoveAliasInfoList().isEmpty()){
            List<KnSubsAliasInfoDTO> aliasInfoList=new ArrayList<>();
            for(KnXDMSubsAliasInfoDTO aliasInfo : subsProvInputDTO.getRemoveAliasInfoList()){
                KnSubsAliasInfoDTO aliasInfoDTO= new KnSubsAliasInfoDTO();
                aliasInfoDTO.setAliasId(aliasInfo.getAliasId());
                aliasInfoDTO.setAliasIdIssuer(aliasInfo.getAliasIdIssuer());
                aliasInfoDTO.setAliasIdType(aliasInfo.getAliasIdType());
                aliasInfoList.add(aliasInfoDTO);
            }
            subsProvInfoDTO.setRemoveAliasInfoList(aliasInfoList);
        }

        knLogger.debug(methodName, "Library Subs Prov DTO - ", subsProvInfoDTO);
        return subsProvInfoDTO;
    }

    /**
     * method to activate the Subscriber
     *
     * @param activateInfoDTO KnXDMActivationInfoDTO
     * @param persisterTxn    KnPersisterTxn
     * @return KnOPActivationInfoDTO
     * @throws KnXDMServerException generic exception thrown by server.
     */
    public KnOPActivationInfoDTO activateSubscriber(KnXDMActivateInfoDTO activateInfoDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "activateSubscriber(KnXDMActivateInfoDTO, KnPersisterTxn)";
        knLogger.info(methodName, "activate Subscriber request with DTO - ", activateInfoDTO);
        KnOPActivationInfoDTO responseDTO;
        try {
            //populating the library DTO before calling the API
            KnIPActivateMDNInfoDTO activateMdnInputInfo = new KnIPActivateMDNInfoDTO();
            activateMdnInputInfo.setMdn(activateInfoDTO.getMdn());
            activateMdnInputInfo.setIMEI(activateInfoDTO.getIMEI());
            activateMdnInputInfo.setRealm(activateInfoDTO.getRealm());

            responseDTO = provClientIntf.activateSubscriber(activateMdnInputInfo, persisterTxn);
        } catch (KnXDMServerException e) {
            knLogger.error(methodName, "Failed to activate Subscriber");
            throw e;
        }

        return responseDTO;
    }

    /**
     * Method which invokes the XDM Server Library for processing of retrieval of the Subscriber Profile
     *
     * @param subsInfoDTO  KnXDMSubsProvInfoDTO
     * @param persisterTxn KnPersisterTxn
     * @return KnOPSubsProfileInfoDTO
     * @throws KnXDMServerException Server Exception
     */
    public KnOPSubsProfileInfoDTO getSubscriberDetails(KnXDMSubsProvInfoDTO subsInfoDTO, KnPersisterTxn persisterTxn)
            throws KnXDMServerException {
        String methodName = "getSubscriberDetails(KnXDMSubsInfoDTO, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY: Get Subscriber Details - ", subsInfoDTO);
        KnOPSubsProfileInfoDTO responseDTO = null;
        boolean ownedTxn = false;

        try {

            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the Transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            //populating the library DTO before calling the API.
            KnIPSubscriberInfoDTO subsInfoInputDTO = new KnIPSubscriberInfoDTO();
            subsInfoInputDTO.setMdn(subsInfoDTO.getMdn());
            subsInfoInputDTO.setIfMatch(subsInfoDTO.getIfMatch());
            subsInfoInputDTO.setIfNoneMatch(subsInfoDTO.getIfNoneMatch());

            responseDTO = provClientIntf.getSubscriberDetails(subsInfoInputDTO, persisterTxn);
            knLogger.debug(methodName, "responseDTO :ActiveFs:",responseDTO.getActiveFS2());

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the Transaction ");
                persisterTxn.save();
            }

        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Failed to get the Transaction");
            if (ownedTxn) rollback(persisterTxn);
            throw new KnXDMServerException(e.getErrorCode(), e.getErrorMessage());
        } catch (KnXDMServerException e) {
            knLogger.error(methodName, "Failed to retrieve Subscriber profile");
            if (ownedTxn) rollback(persisterTxn);
            throw e;
        } finally {
            knLogger.info(methodName, "EXIT: Retrieved Default Subscriber Profile - ", responseDTO
            );
        }
        return responseDTO;
    }

    /**
     * method to retrieve the default Subscriber Profile
     *
     * @param requestDTO   KnXDMSubsInfoDTO
     * @param persisterTxn KnPersisterTxn
     * @return KnXDMSSubsProfileRespDTO response object
     * @throws KnXDMServerException   XDM Server Exception
     * @throws KnPersistenceException DB txn Exception
     */
    public KnXDMSubsProfileRespDTO getDefaultSubscriberProfile(KnXDMSubsInfoDTO requestDTO, KnPersisterTxn persisterTxn)
            throws KnXDMServerException, KnPersistenceException {
        String methodName = "getDefaultSubscriberProfile(KnXDMSubsInfoDTO, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY: Get Default Subscriber Profile - ", requestDTO);
        KnXDMSubsProfileRespDTO responseDTO = null;
        boolean ownedTxn = false;

        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the Transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            //populating the library DTO before calling the API.
            KnIPSubscriberInfoDTO subsInfoInputDTO = new KnIPSubscriberInfoDTO();
            subsInfoInputDTO.setMdn(requestDTO.getMdn());

            KnOPSubsProfileInfoDTO subsProfileInfoDTO = provClientIntf.getDefaultSubscriberProfile(subsInfoInputDTO, persisterTxn);
            responseDTO = new KnXDMSubsProfileRespDTO();
            responseDTO = populateRespSubscriberProfile(responseDTO, subsProfileInfoDTO);

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the Transaction ");
                persisterTxn.save();
            }

        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Failed to get the Transaction");
            if (ownedTxn) rollback(persisterTxn);
            throw e;
        } catch (KnXDMServerException e) {
            knLogger.error(methodName, "Failed to retrieve default Subscriber profile");
            if (ownedTxn) rollback(persisterTxn);
            throw e;
        } finally {
            knLogger.info(methodName, "EXIT: Retrieved Default Subscriber Profile - ", responseDTO
            );
        }
        return responseDTO;
    }

    /**
     * method to populate the Subscriber profile request DTO for XDM Server.
     *
     * @param serverSubsProvInfoDTO KnIPSubsProvInfoDTO
     * @param subsProvInputDTO      KnXDMSubsProvInfoDTO
     * @return KnIPSubsProvInfoDTO
     */
    public KnIPSubsProvInfoDTO populateReqSubsProfile(KnIPSubsProvInfoDTO serverSubsProvInfoDTO, KnXDMSubsProvInfoDTO subsProvInputDTO) {
        String methodName = "populateReqSusbProfile(KnIPSubsProvInfoDTO, KnXDMSubsProvInfoDTO)";
        knLogger.debug(methodName, "Populating the Server Request DTO ");
        if (subsProvInputDTO != null) {
            serverSubsProvInfoDTO.setMdn(subsProvInputDTO.getMdn());
            serverSubsProvInfoDTO.setNetworkName(subsProvInputDTO.getNetworkName());
            serverSubsProvInfoDTO.setPublicSubscriptionType(subsProvInputDTO.getPublicSubscriptionType());
            serverSubsProvInfoDTO.setCorporateSubscriptionType(subsProvInputDTO.getCorporateSubscriptionType());
            serverSubsProvInfoDTO.setExtCorpId(subsProvInputDTO.getExtCorpId());
            serverSubsProvInfoDTO.setPairingInd(subsProvInputDTO.getPairingInd());
            serverSubsProvInfoDTO.setAffiliateId(subsProvInputDTO.getAffiliateId());
            serverSubsProvInfoDTO.setPayType(subsProvInputDTO.getPayType());
            serverSubsProvInfoDTO.setRoamingTypes((ArrayList<Integer>) subsProvInputDTO.getRoamingType());
        }
        return serverSubsProvInfoDTO;
    }

    /**
     * method to populate the XDM response (From Server to Wrapper) DTO
     *
     * @param responseDTO        KnXDMSubsProfileRespDTO
     * @param subsProfileInfoDTO KnOPSubsProfileInfoDTO
     * @return KnXDMSubsProfileRespDTO
     */
    public KnXDMSubsProfileRespDTO populateRespSubscriberProfile(KnXDMSubsProfileRespDTO responseDTO, KnOPSubsProfileInfoDTO subsProfileInfoDTO) {
        String methodName = "populateRespSubscriberProfile(KnXDMSubsProfileRespDTO, KnOPSubsProfileInfoDTO)";
        knLogger.debug(methodName, "populating the XDM Response DTO ");
        if (subsProfileInfoDTO != null) {
            responseDTO.setMdn(subsProfileInfoDTO.getMdn());
            responseDTO.setIMEI(subsProfileInfoDTO.getIMEI());
            responseDTO.setPoCHome(subsProfileInfoDTO.getPoCHome());
            responseDTO.setPresenceHome(subsProfileInfoDTO.getPresenceHome());
            responseDTO.setXDMSHome(subsProfileInfoDTO.getXDMSHome());
            responseDTO.setNetworkName(subsProfileInfoDTO.getNetworkName());
            responseDTO.setServiceAuthStatus(subsProfileInfoDTO.getServiceAuthStatus());
            responseDTO.setPublicSubscriptionType(subsProfileInfoDTO.getPublicSubscriptionType());
            responseDTO.setCorporateSubscriptionType(subsProfileInfoDTO.getCorporateSubscriptionType());
            responseDTO.setPairingInd(subsProfileInfoDTO.getPairingInd());
            responseDTO.setExtCorpId(subsProfileInfoDTO.getExtCorpId());
            responseDTO.setPayType(subsProfileInfoDTO.getPayType());
            responseDTO.setAffiliateId(subsProfileInfoDTO.getAffiliateId());
            responseDTO.setRoamingType(subsProfileInfoDTO.getRoamingTypes());
            responseDTO.setEmailAddress(subsProfileInfoDTO.getEmailAddress());
            responseDTO.setSubscriberClientType(subsProfileInfoDTO.getSubsClientType());
            responseDTO.setDispatchGroupMember(subsProfileInfoDTO.getDispatchGroupMember());
            responseDTO.setInternalCorpId(subsProfileInfoDTO.getCorpId());
            responseDTO.setCorporateName(subsProfileInfoDTO.getCorporateName());
            responseDTO.setAccountId(subsProfileInfoDTO.getAccountId());
            responseDTO.setUserAgent(subsProfileInfoDTO.getUserAgent());
            responseDTO.setSubsCreationTime(subsProfileInfoDTO.getSubsCreationTime());
            responseDTO.setActiveFS(KnGeneralUtil.convertHexStringToLong(subsProfileInfoDTO.getActiveFS2()));
            responseDTO.setCorpAdminFS(KnGeneralUtil.convertHexStringToLong(subsProfileInfoDTO.getCorpAdminFS2()));
            responseDTO.setClientFS1(KnGeneralUtil.convertHexStringToLong(subsProfileInfoDTO.getClientFS2()));
            responseDTO.setActiveFS2(subsProfileInfoDTO.getActiveFS2());
            responseDTO.setCorpAdminFS2(subsProfileInfoDTO.getCorpAdminFS2());
            responseDTO.setClientFS2(subsProfileInfoDTO.getClientFS2());
            responseDTO.setLastActivationTime(subsProfileInfoDTO.getLastActivationTime());
            responseDTO.setBillingMDN(subsProfileInfoDTO.getBillingMDN());
            responseDTO.setRoamingAllowed(subsProfileInfoDTO.getRoamingAllowed());
            responseDTO.setTpUser(subsProfileInfoDTO.getTpUser());
            responseDTO.setTpAccount(subsProfileInfoDTO.getTpAccount());
            responseDTO.setDispatchType(subsProfileInfoDTO.getDispatchType());
            responseDTO.setUserId(subsProfileInfoDTO.getUserId());
            responseDTO.setUfmi(subsProfileInfoDTO.getUfmi());
            responseDTO.setiDenUserName(subsProfileInfoDTO.getiDenUserName());
            responseDTO.setiDenPassword(subsProfileInfoDTO.getiDenPassword());
            responseDTO.setiDenBusUnitId(subsProfileInfoDTO.getiDenBusUnitId());
            responseDTO.setPkgIdMap(subsProfileInfoDTO.getPkgIdMap());
            responseDTO.setLicenseType(subsProfileInfoDTO.getLicenseType());
            responseDTO.setAliasMdn(subsProfileInfoDTO.getAliasMdn());
            responseDTO.setFirstNetIndicator(subsProfileInfoDTO.getFirstNetIndicator());
            responseDTO.setMcId(subsProfileInfoDTO.getMcId());
            responseDTO.setMcpttId(subsProfileInfoDTO.getMcpttId());
            responseDTO.setMcVideoId(subsProfileInfoDTO.getMcVideoId());
            responseDTO.setMcDataId(subsProfileInfoDTO.getMcDataId());
            responseDTO.setMcsCompliance(subsProfileInfoDTO.getMcpttCompliance());
            responseDTO.setOnBoardingMailReq(subsProfileInfoDTO.getOnBoardingEmailReqd());
            responseDTO.setIsDefaultProfile(subsProfileInfoDTO.getIsDefaultProfile());
            responseDTO.setUserProfileIndex(subsProfileInfoDTO.getUserProfileIndex());
            responseDTO.setUserProfileName(subsProfileInfoDTO.getUserProfileName());
            responseDTO.setCameraType(subsProfileInfoDTO.getCameraType());
            responseDTO.setAliasInfoList(subsProfileInfoDTO.getAliasInfoDTOList());
            responseDTO.setCameraInfo(populateCameraInfo(subsProfileInfoDTO));
            responseDTO.setDeviceId(subsProfileInfoDTO.getDeviceId());
            responseDTO.setExtGatewayId(subsProfileInfoDTO.getExtGatewayId());
            responseDTO.setSubsFS2(subsProfileInfoDTO.getSubsFS2());
        }
        return responseDTO;
    }

    private KnXDMSubsCameraInfo populateCameraInfo(KnOPSubsProfileInfoDTO subsProfileInfoDTO) {
        if(subsProfileInfoDTO.getCameraInfo()!=null) {
            KnXDMSubsCameraInfo xdmcameraInfo = new KnXDMSubsCameraInfo();
            xdmcameraInfo.setIpIdentifier(subsProfileInfoDTO.getCameraInfo().getIpIdentifier());
            xdmcameraInfo.setCameraSerialId(subsProfileInfoDTO.getCameraInfo().getCameraSerialId());
            return xdmcameraInfo;
        }
        return null;
    }

    /**
     * method to populate the XDM response (From Server to Wrapper) DTO
     *
     * @param responseDTO        KnXDMSubsProfileRespDTO
     * @param subsProfileInfoDTO KnOPSubsProfileInfoDTO
     * @return KnXDMSubsProfileRespDTO
     */
    public KnXDMSubsProfileRespDTO populateRespSubsProfile(KnXDMSubsProfileRespDTO responseDTO, KnOPSubsProfileInfoDTO subsProfileInfoDTO) {
        String methodName = "populateRespSubsProfile(KnXDMSubsProfileRespDTO, KnOPSubsProfileInfoDTO)";
        knLogger.debug(methodName, "populating the XDM Response DTO ");
        if (subsProfileInfoDTO != null && subsProfileInfoDTO.getSubsRespMap() != null) {
            Collection<KnXDMSubsProvDTO> subsRespMap = new ArrayList<>();
            KnXDMSubsProvDTO subsProvDTO =  null;
            for(Map.Entry<String, KnSubsProfileDTO> Entry : subsProfileInfoDTO.getSubsRespMap().entrySet()){
                KnSubsProfileDTO subsProfileDTO = Entry.getValue();
                subsProvDTO = new KnXDMSubsProvDTO();
                subsProvDTO.setMdn(subsProfileDTO.getMdn());
                subsProvDTO.setPocHome(subsProfileDTO.getPoCHome());
                subsProvDTO.setIMEI(subsProfileDTO.getIMEI());
                subsProvDTO.setAccountId(subsProfileDTO.getAccountId());
                subsProvDTO.setNetworkName(subsProfileDTO.getNetworkName());
                subsProvDTO.setCorporateSubscriptionType(subsProfileDTO.getCorporateSubscriptionType());
                subsProvDTO.setPublicSubscriptionType(subsProfileDTO.getPublicSubscriptionType());
                subsProvDTO.setSubscriberClientType(subsProfileDTO.getSubsClientType());
                subsProvDTO.setEmailAddress(subsProfileDTO.getEmailAddress());
                subsProvDTO.setCorporateName(subsProfileDTO.getCorporateName());
                subsProvDTO.setCorpId(String.valueOf(subsProfileDTO.getCorpId()));
                subsProvDTO.setPocStatus(String.valueOf(subsProfileDTO.getServiceAuthStatus()));
                subsProvDTO.setActiveFS(KnGeneralUtil.convertHexStringToLong(subsProfileDTO.getActiveFS2()));
                subsProvDTO.setClientFS1(KnGeneralUtil.convertHexStringToLong(subsProfileDTO.getClientFS2()));
                subsProvDTO.setCorpAdminFS(KnGeneralUtil.convertHexStringToLong(subsProfileDTO.getCorpAdminFS2()));
                subsProvDTO.setActiveFS2(subsProfileDTO.getActiveFS2());
                subsProvDTO.setClientFS2(subsProfileDTO.getClientFS2());
                subsProvDTO.setCorpAdminFS2(subsProfileDTO.getCorpAdminFS2());
                subsProvDTO.setApnType(subsProfileDTO.getApnName());
                subsProvDTO.setClientPassword(subsProfileDTO.getClientPassword());
                subsProvDTO.setClientPvMajorVersion(subsProfileDTO.getClientPVmajorVer());
                subsProvDTO.setClientPvMinorVersion(subsProfileDTO.getClientPVminorVer());
                subsProvDTO.setDispatchType(subsProfileDTO.getDispatchType());
                subsProvDTO.setUserId(subsProfileDTO.getUserId());
                subsProvDTO.setDerivedKey(subsProfileDTO.getDerivedKey());
                subsProvDTO.setAliasMdn(subsProfileDTO.getAliasMdn());
                subsProvDTO.setMcDataId(subsProfileDTO.getMcDataId());
                subsProvDTO.setMcpttCompliance(String.valueOf(subsProfileDTO.getMcpttCompliance()));
                subsProvDTO.setMcpttId(subsProfileDTO.getMcpttId());
                subsProvDTO.setMcVideoId(subsProfileDTO.getMcVideoId());
                subsProvDTO.setMcId(subsProfileDTO.getMcId());
                subsProvDTO.setPkgIdMap(subsProfileDTO.getPkgIdMap());

                    /* subsProvDTO.setUfmi(subsProfileDTO.getUfmi());
                    subsProvDTO.setiDenUserName(subsProfileDTO.getiDenUserName());
                    subsProvDTO.setiDenPassword(subsProfileDTO.getiDenPassword());
                    subsProvDTO.setiDenBusUnitId(subsProfileDTO.getiDenBusUnitId());
                    subsProvDTO.setPkgIdMap(subsProfileDTO.getPkgIdMap());
                    subsProvDTO.setLicenseType(subsProfileDTO.getLicenseType());
                    subsProvDTO.setAliasMdn(subsProfileDTO.getAliasMdn());*/
                subsProvDTO.setLastUpdateProfileTime(subsProfileDTO.getLastProfileUpdateTime());
                subsProvDTO.setXcapRootUri(subsProfileDTO.getXcapRootUri());
                subsProvDTO.setIsDefaultProfile(subsProfileDTO.getIsDefaultProfile());
                subsProvDTO.setUserProfileIndex(subsProfileDTO.getUserProfileIndex());
                subsProvDTO.setUserProfileName(subsProfileDTO.getUserProfileName());
                subsProvDTO.setCameraType(subsProfileDTO.getCameraType());
                if(subsProfileDTO.getCameraInfo()!=null) {
                    KnSubsCameraInfo cameraInfo = subsProfileDTO.getCameraInfo();
                    subsProvDTO.setCameraInfo(new KnXDMSubsCameraInfo(cameraInfo.getIpIdentifier(),cameraInfo.getCameraSerialId()));
                }
                List<KnSubsAliasInfoDTO> aliasInfoList = subsProfileDTO.getAliasInfoList();
                if(aliasInfoList!=null && !aliasInfoList.isEmpty()){
                    subsProvDTO.setAliasInfoList(aliasInfoList.stream().
                            map(s -> new KnXDMSubsAliasInfoDTO(s.getAliasId(),s.getAliasIdIssuer(),s.getAliasIdType())).
                            collect(Collectors.toList()));
                }
                List<KnSubsAliasInfoDTO> addAliasInfoList = subsProfileDTO.getAliasInfoList();
                if(addAliasInfoList!=null && !addAliasInfoList.isEmpty()){
                    subsProvDTO.setAddAliasInfoList(addAliasInfoList.stream().
                            map(s -> new KnXDMSubsAliasInfoDTO(s.getAliasId(),s.getAliasIdIssuer(),s.getAliasIdType())).
                            collect(Collectors.toList()));
                }
                List<KnSubsAliasInfoDTO> removeAliasInfoList = subsProfileDTO.getAddAliasInfoList();
                if(removeAliasInfoList!=null && !removeAliasInfoList.isEmpty()){
                    subsProvDTO.setRemoveAliasInfoList(removeAliasInfoList.stream().
                            map(s -> new KnXDMSubsAliasInfoDTO(s.getAliasId(),s.getAliasIdIssuer(),s.getAliasIdType())).
                            collect(Collectors.toList()));
                }

                subsProvDTO.setCustMcpttId(subsProfileDTO.getCustMcpttId());
                subsProvDTO.setCustMcVideoId(subsProfileDTO.getCustMcVideoId());
                subsProvDTO.setCustMcDataId(subsProfileDTO.getCustMcDataId());
                subsProvDTO.setExtGatewayId(subsProfileDTO.getExtGatewayId());
                subsRespMap.add(subsProvDTO);
            }
            responseDTO.setSubsRespDTO(subsRespMap);
        }
        return responseDTO;
    }

    /**
     * method which invokes update subscriber on the Server Library for processing of the Updating of the
     * Subscriber  Profile
     *
     * @param subsProvInputDTO KnXDMSubsProvInfoDTO
     * @param persisterTxn     KnPersisterTxn
     * @return KnOPProvDTO
     * @throws KnXDMServerException XDM Server Exception
     */
    public KnOPUpdateSubsInfoDTO updateSubscriber(KnXDMSubsProvInfoDTO subsProvInputDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "updateSubscriber(KnXDMSubsProvInfoDTO, KnPersisterTxn)";
        KnOPUpdateSubsInfoDTO responseDTO = null;
        knLogger.info(methodName, "update Subscriber request with DTO - ", subsProvInputDTO
        );
        try {
            //populating the Library DTO before calling the API
            KnIPSubsProvInfoDTO serverSubsProvInfoDTO;
            serverSubsProvInfoDTO = populateSubsProvInfoDTO(subsProvInputDTO);

            responseDTO = provClientIntf.updateSubscriber(serverSubsProvInfoDTO, persisterTxn);
            knLogger.debug(methodName, "Successfully updated Subscriber Profile");
        } catch (KnXDMServerException e) {
            knLogger.error(methodName, "Failed to update the Subscriber profile");
            throw e;
        }

        return responseDTO;
    }
    /**
     * method which invokes update subscriberFS on the Server Library for processing of the Updating of the
     * Subscriber  Profile
     *
     * @param subsProvInputDTO KnXDMSubsProvInfoDTO
     * @param persisterTxn     KnPersisterTxn
     * @return KnOPProvDTO
     * @throws KnXDMServerException XDM Server Exception
     */
    public KnOPUpdateSubsInfoDTO updateSubscriberFSAndPkgCodes(KnXDMSubsProvInfoDTO subsProvInputDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "updateSbscriberFS(KnXDMSubsProvInfoDTO, KnPersisterTxn)";
        KnOPUpdateSubsInfoDTO responseDTO = null;
        knLogger.info(methodName, "updateSbscriberFS request with DTO - ", subsProvInputDTO);
        //populating the Library DTO before calling the API
        KnIPSubsProvInfoDTO serverSubsProvInfoDTO;
        serverSubsProvInfoDTO = populateSubsProvInfoDTO(subsProvInputDTO);

        responseDTO = provClientIntf.updateSubscriberFSAndPkgCodes(serverSubsProvInfoDTO, persisterTxn);
        knLogger.debug(methodName, "Successfully updated Subscriber Profile");
        return responseDTO;
    }
    /**
     * method which invokes update External corp ID
     *
     * @param corpSubsInfoReq KnXDMSubsProvInfoDTO
     * @param persisterTxn    KnPersisterTxn
     * @return KnOPProvDTO
     * @throws KnXDMServerException XDM Server Exception
     */
    public KnOPProvDTO updateExtCorpID(KnXDMCorpSubscInfoRequestDTO corpSubsInfoReq, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "updateExtCorpID(KnXDMCorpSubscInfoRequestDTO, KnPersisterTxn)";
        KnOPProvDTO responseDTO = null;
        knLogger.info(methodName, "update Ext Corp Id request with DTO - ", corpSubsInfoReq
        );
        try {

            KnIPCorpProfileInfoDTO corpProfileInfo = new KnIPCorpProfileInfoDTO();
            corpProfileInfo.setCorpId(Integer.parseInt(corpSubsInfoReq.getCorpId()));
            corpProfileInfo.setExtCorpId(corpSubsInfoReq.getExtCorpId());
            responseDTO = provClientIntf.updateExtCorporateID(corpProfileInfo, persisterTxn);
            knLogger.debug(methodName, "Successfully updated Ext Corp Id ");
        } catch (KnXDMServerException e) {
            knLogger.error(methodName, "Failed to update Ext Corp Id ");
            throw e;
        }

        return responseDTO;
    }

    /**
     * method to retrieve the Subscriber Config Document
     *
     * @param subsInfoDTO  KnXDMSubsInfoDTO
     * @param persisterTxn KnPersisterTxn
     * @return KnXDMSubsConfigDocRespDTO
     * @throws KnXDMServerException Server Exception
     */
    public KnXDMSubsConfigDocRespDTO getSubscriberConfigDocument(KnXDMSubsInfoDTO subsInfoDTO, KnPersisterTxn persisterTxn)
            throws KnXDMServerException {
        String methodName = "getSusbcriberConigDocument(KnXDMSubsInfoDTO, KnPersisterTxn)";
        KnXDMSubsConfigDocRespDTO responseDTO = null;
        knLogger.debug(methodName, "Get Subscriber Config document with DTO - ", subsInfoDTO);
        try {
            //populating the Library DTO before calling the API
            KnIPSubscriberInfoDTO subscriberInfoDTO = new KnIPSubscriberInfoDTO();
            subscriberInfoDTO.setMdn(subsInfoDTO.getMdn());
            KnOPSubsConfigDocInfoDTO serverRespDTO = provClientIntf.getSubscriberConfigDocument(subscriberInfoDTO, persisterTxn);
            knLogger.debug(methodName, "retrieve Subscriber config document - ", serverRespDTO);

            //populating the response DTO to wrapper
//            responseDTO.setMdn(serverRespDTO.getMdn());

        } catch (KnXDMServerException e) {
            knLogger.error(methodName, "Failed to retrieve Subscriber config document");
            throw e;
        }
        return null;  //To change body of created methods use File | Settings | File Templates.
    }

    /**
     * method invokes the change Service auth status API on prov library
     *
     * @param subsInfoDTO  KnXDMSubsStatusInfoDTO
     * @param persisterTxn KnPersisterTxn
     * @return KnOPProvDTO
     * @throws KnXDMServerException ServerException
     */
    public KnOPChgAuthStatusRespDTO changeServiceAuthStatus(KnXDMSubsStatusInfoDTO subsInfoDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "changeServiceAuthStatus(KnXDMSubsInfoDTO, KnPersisterTxn)";
        KnOPChgAuthStatusRespDTO responseDTO;
        knLogger.debug(methodName, "Change Service Auth Status with DTO - ", subsInfoDTO);

        try {
            //populating the Library DTO before calling the API
            KnIPSubsProvInfoDTO subscriberInfoDTO = new KnIPSubsProvInfoDTO();
            subscriberInfoDTO.setMdn(subsInfoDTO.getMdn());
            subscriberInfoDTO.setServiceAuthStatus(subsInfoDTO.getServiceAuthStatus());
            subscriberInfoDTO.setIfMatch(subsInfoDTO.getIfMatch());
            subscriberInfoDTO.setIfNoneMatch(subsInfoDTO.getIfNoneMatch());
            responseDTO = provClientIntf.changeServiceAuthStatus(subscriberInfoDTO, persisterTxn);
            knLogger.debug(methodName, "change service auth status response from server - ", responseDTO);
        } catch (KnXDMServerException e) {
            knLogger.error(methodName, "Failed to change service Auth status ");
            throw e;
        }
        return responseDTO;
    }

    /**
     * method which invokes the force sync api on prov library
     *
     * @param subsInfoDTO  KnXDMSubsInfoDTO
     * @param persisterTxn KnPersisterTxn
     * @return KnOPSubsProfileInfoDTO
     * @throws KnXDMServerException server Exception
     */
    public KnOPSubsProfileInfoDTO forceSync(KnXDMSubsInfoDTO subsInfoDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "forceSync(KnXDMSubsInfoDTO,KnPersisterTxn)";
        KnOPSubsProfileInfoDTO responseDTO;
        knLogger.debug(methodName, "forceSync operation with DTO - ", subsInfoDTO);
        try {
            //populating the library DTO
            KnIPSubscriberInfoDTO subscriberInfoDTO = new KnIPSubscriberInfoDTO();
            subscriberInfoDTO.setMdn(subsInfoDTO.getMdn());
            responseDTO = provClientIntf.forceSync(subscriberInfoDTO, persisterTxn);
            knLogger.debug(methodName, "Force Sync operation response from server - ", responseDTO);
        } catch (KnXDMServerException e) {
            knLogger.error(methodName, "Failed to change service Auth status ");
            throw e;
        }
        return responseDTO;
    }

    /**
     * method which invokes the delete subscriber API on prov library
     *
     * @param subsInfoDTO  KnXDMSubsInfoDTO
     * @param persisterTxn KnPersisterTxn
     * @return KnOPProvDTO
     * @throws KnXDMServerException Server Exception
     */
    public KnOPDeleteSubsRespDTO deleteSubscriber(KnXDMSubsInfoDTO subsInfoDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "deleteSubscriber(KnXDMSubsInfoDTO,KnPersisterTxn)";
        KnOPDeleteSubsRespDTO responseDTO = null;
        knLogger.debug(methodName, "ENTRY: Delete Subscriber with DTO -", subsInfoDTO);
        try {
            //populating the library DTO
            KnIPSubscriberInfoDTO subscriberInfoDTO = new KnIPSubscriberInfoDTO();
            subscriberInfoDTO.setMdn(subsInfoDTO.getMdn());
            responseDTO = provClientIntf.deleteSubscriber(subscriberInfoDTO, persisterTxn);
            knLogger.debug(methodName, "delete operation response from server - ", responseDTO);
        } catch (KnXDMServerException e) {
            knLogger.error(methodName, "Failed to delete subscriber ");
            throw e;
        } finally {
            knLogger.debug(methodName, "EXIT: Delete operation response - ", responseDTO);
        }
        return responseDTO;

    }

    /**
     * method which invokes the Prov library change MDN API
     *
     * @param changeMdnInfoDTO KnXDMChangeMDNInfoDTO
     * @param persisterTxn     KnPersisterTxn
     * @return KnOPSubsProfileInfoDTO
     * @throws KnXDMServerException Server Exception
     */
    public KnOPSubsProfileInfoDTO changeMDN(KnXDMChangeMDNInfoDTO changeMdnInfoDTO,boolean isAsyncCall, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methoName = "changeMDN(KnXDMChangeMDNInfoDTO, KnPersisterTxn)";
        KnOPSubsProfileInfoDTO responseDTO = null;
        knLogger.info(methoName, "ENTRY: Change MDN request - ", changeMdnInfoDTO);
        try {
            //populate the library DTO
            KnIPChangeMDNInfoDTO chgMdNInfoDTO = new KnIPChangeMDNInfoDTO();
            chgMdNInfoDTO.setOldMDN(changeMdnInfoDTO.getOldMDN());
            changeMdnInfoDTO.setOldIMEI(changeMdnInfoDTO.getOldIMEI());
            chgMdNInfoDTO.setNewMDN(changeMdnInfoDTO.getNewMdn());
            chgMdNInfoDTO.setNewIMEI(changeMdnInfoDTO.getNewIMEI());
            responseDTO = provClientIntf.changeMDN(chgMdNInfoDTO,false, persisterTxn);
            knLogger.debug(methoName, "Change MDN Response - ", responseDTO);
        } catch (KnXDMServerException e) {
            knLogger.error(methoName, "Failed to migrate MDN");
            throw e;
        } finally {
            knLogger.info(methoName, "EXIT: Change MDN Response - ", responseDTO);
        }
        return responseDTO;
    }

    /**
     * method which invokes the Prov library remove Subscriber Info from DG.POCSUBSCRINFO table
     * this method will be used while change MDN operation of the Mediator.
     *
     * @param changeMdnInfoDTO KnXDMChangeMDNInfoDTO
     * @param persisterTxn     KnPersisterTxn
     * @return KnOPSubsProfileInfoDTO
     * @throws KnXDMServerException Server Exception
     */
    public KnOPProvDTO removeSubsInfo(KnXDMChangeMDNInfoDTO changeMdnInfoDTO, KnPersisterTxn persisterTxn) throws
            KnXDMServerException {
        String methodName = "removeSubsInfo(KnXDMChangeMDNInfoDTO, KnPersisterTxn)";
        KnOPProvDTO responseDTO = null;
        knLogger.info(methodName, "ENTRY: Remove Subs Info Request - ", changeMdnInfoDTO);

        try {
            KnIPChangeMDNInfoDTO chgMdNInfoDTO = new KnIPChangeMDNInfoDTO();
            chgMdNInfoDTO.setOldMDN(changeMdnInfoDTO.getOldMDN());
            changeMdnInfoDTO.setOldIMEI(changeMdnInfoDTO.getOldIMEI());
            chgMdNInfoDTO.setNewMDN(changeMdnInfoDTO.getNewMdn());
            chgMdNInfoDTO.setNewIMEI(changeMdnInfoDTO.getNewIMEI());
            responseDTO = provClientIntf.removeSubsInfo(chgMdNInfoDTO, persisterTxn);
        } catch (KnXDMServerException e) {
            knLogger.error(methodName, "Failed to Remove MDN");
            throw e;
        } finally {
            knLogger.info(methodName, "ENTRY: Remove Subs Info response - ", responseDTO);
        }
        return responseDTO;
    }

    /**
     * Method to update the subscriber in bulk
     *
     * @param subsProvInputDTOList Collection<KnXDMSubsProvInfoDTO
     * @param persisterTxn         KnPersisterTxn
     * @return Collection<KnOPUpdateSubsInfoDTO>
     */
    public Collection<KnOPUpdateSubsInfoDTO> updateSubscriber(Collection<KnXDMSubsProvInfoDTO> subsProvInputDTOList,
                                                              KnPersisterTxn persisterTxn) {
        String methodName = "updateSubscriber(Collection<KnXDMSubsProvInfoDTO>, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY: Update Subscriber with List - ", subsProvInputDTOList
        );

        Collection<KnOPUpdateSubsInfoDTO> updateSubsInfoDTOList = new ArrayList<KnOPUpdateSubsInfoDTO>();
        for (KnXDMSubsProvInfoDTO subsProvInfoDTO : subsProvInputDTOList) {
            KnOPUpdateSubsInfoDTO updateSubsRespDTO = new KnOPUpdateSubsInfoDTO();
            try {
                updateSubsRespDTO = updateSubscriber(subsProvInfoDTO, persisterTxn);
            } catch (KnXDMServerException e) {
                knLogger.error(methodName, "Failed to update Subscriber - ", subsProvInfoDTO);
                knLogger.error(methodName, e);
                updateSubsRespDTO.setResponseStatus(1);
                updateSubsRespDTO.setResponseCode(e.getErrorCode());
                updateSubsRespDTO.setResponseMessage(e.getErrorMessage());
            }

            updateSubsInfoDTOList.add(updateSubsRespDTO);
        }
        knLogger.info(methodName, "EXIT: Update Subscriber with Resp List - ", updateSubsInfoDTOList
        );

        return updateSubsInfoDTOList;
    }

    public KnXDMSubsConfigDocRespDTO populateSubsConfigDocument(KnXDMSubsConfigDocRespDTO responseDTO, KnOPSubsConfigDocInfoDTO serverRespDTO) {
        String methodName = "populateSubsConfigDocument(KnXDMSubsConfigDocRespDTO, KnOPSubsConfigDocInfoDTO)";
        knLogger.debug(methodName, "populating the xdm response DTO - ");
        responseDTO.setPubSubscriptionType(serverRespDTO.getPubSubscriptionType());
        responseDTO.setCorpSubscriptionType(serverRespDTO.getCorpSubscriptionType());
        responseDTO.setSubscriptionState(serverRespDTO.getSubscriptionState());
        responseDTO.setNetworkName(serverRespDTO.getNetworkName());
        responseDTO.setMdn(serverRespDTO.getMdn());
        responseDTO.setClientPVmajorVer(serverRespDTO.getProtocolVersion());
        responseDTO.setXui(serverRespDTO.getXui());
        responseDTO.setPrimaryRegistrarRoute(serverRespDTO.getPrimaryRegistrarUri());

        responseDTO.setPrimarySessionRoute(serverRespDTO.getPrimarySessionUri());

        responseDTO.setPrimaryPocSettingRoute(serverRespDTO.getPrimaryPocSettingUri());
        responseDTO.setPrimaryIpaRoute(serverRespDTO.getPrimaryIpaUri());

        knLogger.info(methodName, "serverRespDTO.getProtocolVersion(): ", serverRespDTO.getProtocolVersion());

        if (serverRespDTO.getProtocolVersion() != null && !(serverRespDTO.getProtocolVersion().matches(KnConstants.PROTOCOL_VERSION_3_0)
                || serverRespDTO.getProtocolVersion().matches(KnConstants.PROTOCOL_VERSION_5_0))) {
            knLogger.info(methodName, "serverRespDTO.getProtocolVersion() 2: ", serverRespDTO.getProtocolVersion());

            responseDTO.setGeoSessionRoute(serverRespDTO.getGeoSessionUri());
            responseDTO.setGeoIpaRoute(serverRespDTO.getGeoIpaUri());
            responseDTO.setGeoPocSettingRoute(serverRespDTO.getGeoPocSettingUri());
            responseDTO.setGeoPresenceRoute(serverRespDTO.getGeoPresenceUri());
            responseDTO.setGeoRegistrarRoute(serverRespDTO.getGeoRegistrarUri());
            responseDTO.setGeoRlsRoute(serverRespDTO.getGeoRlsUri());
            responseDTO.setGeoSubscriptionProxyUri(serverRespDTO.getGeoSubscriptionProxyUri());
            responseDTO.setGeoXdmsRoute(serverRespDTO.getGeoXdmsUri());
            responseDTO.setPrimarySubscriptionProxyUri(serverRespDTO.getPrimarySubscriptionProxyUri());
            responseDTO.setMediaPortRefreshTimer(serverRespDTO.getMediaPortRefreshTimer());
        }

        responseDTO.setPrimaryPresenceRoute(serverRespDTO.getPrimaryPresenceUri());
        responseDTO.setPrimaryRlsRoute(serverRespDTO.getPrimaryRlsUri());
        responseDTO.setXcapRootUri(serverRespDTO.getXcapRootUri());
        responseDTO.setPrimaryXdmsRoute(serverRespDTO.getPrimaryXdmsUri());
        responseDTO.setMaxPublicContacts(serverRespDTO.getMaxPublicContacts());
        responseDTO.setMaxCorporateContacts(serverRespDTO.getMaxCorporateContacts());
        responseDTO.setMaxPublicGroups(serverRespDTO.getMaxPublicGroups());
        responseDTO.setMaxCorporateGroups(serverRespDTO.getMaxCorporateGroups());
        responseDTO.setMaxMembersPerPublicGroup(serverRespDTO.getMaxMembersPerPublicGroup());
        responseDTO.setMaxMembersPerCorpGroup(serverRespDTO.getMaxMembersPerCorpGroup());
        responseDTO.setMaxAdhocGroupSize(serverRespDTO.getMaxAdhocGroupSize());
        responseDTO.setDialPlanInfo(serverRespDTO.getDialPlanInfo());
        responseDTO.setConferenceFactoryUri(serverRespDTO.getConferenceFactoryUri());
        responseDTO.setTbcpRequestTimer(serverRespDTO.getTbcpRequestTimer());
        responseDTO.setTbcpReleaseTimer(serverRespDTO.getTbcpReleaseTimer());
        responseDTO.setMediaEndTimer(serverRespDTO.getMediaEndTimer());
        responseDTO.setMediaIdleTimer(serverRespDTO.getMediaIdleTimer());

        responseDTO.setMediaIntraburstInterval(serverRespDTO.getMediaIntraburstInterval());
        responseDTO.setNumKaMediaPackets(serverRespDTO.getNumKaMediaPackets());
        responseDTO.setMediaPayloadLength(serverRespDTO.getMediaPayloadLength());
        responseDTO.setLocationDebouncingTimer(serverRespDTO.getLocationDebouncingTimer());


        responseDTO.setNumOfRetries(serverRespDTO.getNumOfRetries());
        responseDTO.setNumOfTbcpRetries(serverRespDTO.getNumOfTbcpRetries());
        responseDTO.setPresencePublishThrottleTimer(serverRespDTO.getPresencePublishThrottleTimer());

        responseDTO.setOctetSize(serverRespDTO.getOctetSize());

        responseDTO.setInstaPoc(serverRespDTO.getInstaPoc());
        responseDTO.setLastProfileUpdateTime(serverRespDTO.getLastProfileUpdateTime());
        responseDTO.setNumOfBurstPerTrigger(serverRespDTO.getNumOfBurstPerTrigger());
        responseDTO.setMaxTalkBurstDuration(serverRespDTO.getMaxTalkBurstDuration());
        responseDTO.setActiveFS1(KnGeneralUtil.convertHexStringToLong(serverRespDTO.getActiveFS2()));
        responseDTO.setActiveFS2(serverRespDTO.getActiveFS2());
        responseDTO.setSupervisorOverride(serverRespDTO.isSupervisorOverride());
        responseDTO.setLocationPublishInterval(serverRespDTO.getLocationPublishInterval());
        responseDTO.setRoamingAllowed(serverRespDTO.isRoamingAllowed());
        responseDTO.setSessionRecoveryTimer1(serverRespDTO.getSessionRecoveryTimer1());
        responseDTO.setSessionRecoveryTimer2(serverRespDTO.getSessionRecoveryTimer2());
        responseDTO.setSessionRecoveryTimer3(serverRespDTO.getSessionRecoveryTimer3());
        responseDTO.setSessionRecoveryTimer4(serverRespDTO.getSessionRecoveryTimer4());
        responseDTO.setSessionRecoveryTimer5(serverRespDTO.getSessionRecoveryTimer5());
        responseDTO.setSessionRecoveryTimer6(serverRespDTO.getSessionRecoveryTimer6());
        responseDTO.setIpDebouncerTimer(serverRespDTO.getIpDebouncerTimer());
        responseDTO.setTbcpRequestRetryTimer(serverRespDTO.getTbcpRequestRetryTimer());
        responseDTO.setTbcpReleaseRetryTimer(serverRespDTO.getTbcpReleaseRetryTimer());
        responseDTO.setFloorRecoveryTimer(serverRespDTO.getFloorRecoveryTimer());
        responseDTO.setConferenceUriTemplate(serverRespDTO.getConferenceUriTemplate());

        if (serverRespDTO.getProtocolVersion() != null && !(serverRespDTO.getProtocolVersion().matches(KnConstants.PROTOCOL_VERSION_5_0))) {
            responseDTO.setPreCallNumKaMediaPackets(serverRespDTO.getPreCallNumKaMediaPackets());
            responseDTO.setPreCallKaPacketSize(serverRespDTO.getPreCallKaPacketSize());
            responseDTO.setKaPacketSize(serverRespDTO.getKaPacketSize());
            responseDTO.setPreCallKaInterval(serverRespDTO.getPreCallKaInterval());
            responseDTO.setKaInterval(serverRespDTO.getKaInterval());
            responseDTO.setPreCallKaDuration(serverRespDTO.getPreCallKaDuration());
            responseDTO.setRegisterTimer(serverRespDTO.getRegisterTimer());
            responseDTO.setPublishPocSettingsTimer(serverRespDTO.getPublishPocSettingsTimer());
            responseDTO.setPublishPresenceTimer(serverRespDTO.getPublishPresenceTimer());
            responseDTO.setInviteTimer(serverRespDTO.getInviteTimer());
            responseDTO.setRlsSubscriptionTimer(serverRespDTO.getRlsSubscriptionTimer());
            responseDTO.setSupportPreEstablishmentSession(serverRespDTO.isSupportPreEstablishmentSession());
            responseDTO.setSupportSimultaneousSession(serverRespDTO.isSupportSimultaneousSession());
            responseDTO.setXdmsSubscriptionTimer(serverRespDTO.getXdmsSubscriptionTimer());
            responseDTO.setNumOfWakeupTriggers(serverRespDTO.getNumOfWakeupTriggers());
            responseDTO.setWakeupTimeInterval(serverRespDTO.getWakeupTimeInterval());
        }
        if(serverRespDTO.getClientFS2()!=null) {
            responseDTO.setClientFS1(KnGeneralUtil.convertHexStringToLong(serverRespDTO.getClientFS2()));
        }
        responseDTO.setClientFS2(serverRespDTO.getClientFS2());
        responseDTO.setRoamingBit(serverRespDTO.getRoamingBit());
        responseDTO.setClientType(serverRespDTO.getClientType());
        responseDTO.setSwUpdateQryInterval(serverRespDTO.getSwUpdateQryInterval());
        responseDTO.setSwUpdateInfoUrl(serverRespDTO.getSwUpdateInfoUrl());
        responseDTO.setSwUpdatePkgUrl(serverRespDTO.getSwUpdatePkgUrl());

        responseDTO.setTuSmsAddress(serverRespDTO.getTuSmsAddress());
        responseDTO.setTuDownTimer(serverRespDTO.getTuDownTimer());
        responseDTO.setTuUpTimerStartVal(serverRespDTO.getTuUpTimerStartVal());
        responseDTO.setTuUpTimerMaxVal(serverRespDTO.getTuUpTimerMaxVal());
        responseDTO.setTuUpTimerRampDownPeriod(serverRespDTO.getTuUpTimerRampDownPeriod());
        responseDTO.setTuForceOnlineMaxWaitTimer(serverRespDTO.getTuForceOnlineMaxWaitTimer());
        responseDTO.setTuSmsAddressTon(serverRespDTO.getTuSmsAddressTon());


        if (serverRespDTO.getProtocolVersion() != null && (serverRespDTO.getProtocolVersion().matches(KnConstants.PROTOCOL_VERSION_3_0)
                || serverRespDTO.getProtocolVersion().matches(KnConstants.PROTOCOL_VERSION_5_0))) {
            responseDTO.setSipProxyURI(serverRespDTO.getSipProxyURI());
            responseDTO.setClientConnRetryInterval(serverRespDTO.getClientConnRetryInterval());
            responseDTO.setMaxClientConnRtyAttempts(serverRespDTO.getMaxClientConnRtyAttempts());
            responseDTO.setClientConnSecurityLevel(serverRespDTO.getClientConnSecurityLevel());
            responseDTO.setClientSipTxnTimeout(serverRespDTO.getClientSipTxnTimeout());
            responseDTO.setClientSipReferTxnTimeout(serverRespDTO.getClientSipReferTxnTimeout());
            responseDTO.setMinTcpKaTimerOnWifi(serverRespDTO.getMinTcpKaTimerOnWifi());
            responseDTO.setWifiTcpKaTimerIncrVal(serverRespDTO.getWifiTcpKaTimerIncrVal());
            responseDTO.setMaxTcpKaTimerOnWifi(serverRespDTO.getMaxTcpKaTimerOnWifi());
            responseDTO.setWifiSsidTimeoutMapSize(serverRespDTO.getWifiSsidTimeoutMapSize());
            responseDTO.setTcpKaTimerOnMacroCellular(serverRespDTO.getTcpKaTimerOnMacroCellular());
            responseDTO.setDetectWifiNatTcpTimeout(serverRespDTO.getDetectWifiNatTcpTimeout());
            responseDTO.setMediaSecureSessionRefreshIntvl(serverRespDTO.getMediaSecureSessionRefreshIntvl());
            responseDTO.setClientInCallSuspendTimer(serverRespDTO.getClientInCallSuspendTimer());
        }

        knLogger.debug(methodName, "serverRespDTO.getProtocolVersion() - - ",serverRespDTO.getProtocolVersion(),  "serverRespDTO.getProtocolVersion().matches(KnConstants.PROTOCOL_VERSION_5_0)", serverRespDTO.getProtocolVersion().matches(KnConstants.PROTOCOL_VERSION_5_0), "KnConstants.PROTOCOL_VERSION_5_0", KnConstants.PROTOCOL_VERSION_5_0);
        if (serverRespDTO.getProtocolVersion() != null && serverRespDTO.getProtocolVersion().matches(KnConstants.PROTOCOL_VERSION_5_0)) {
            responseDTO.setWpgt(serverRespDTO.getWpgt());
            responseDTO.setCgpu(serverRespDTO.getCgpu());
            responseDTO.setMwpt(serverRespDTO.getMwpt());
            if (serverRespDTO.getSrpi() == null) {
                responseDTO.setSrpi(0);
            } else {
                responseDTO.setSrpi(serverRespDTO.getSrpi());
            }
            if (serverRespDTO.getStci() == null) {
                responseDTO.setStci(60);
            } else {
                responseDTO.setStci(serverRespDTO.getStci());
            }
            responseDTO.setLgsrvuri(serverRespDTO.getLgsrvuri());
            if (serverRespDTO.getScgbm() == null) {
                responseDTO.setScgbm(0);
            } else {
                responseDTO.setScgbm(serverRespDTO.getScgbm());
            }
            if (serverRespDTO.getSpgag() == null) {
                responseDTO.setSpgag(720);
            } else {
                responseDTO.setSpgag(serverRespDTO.getSpgag());
            }
            responseDTO.setLtekap(serverRespDTO.getLtekap());
            responseDTO.setWfkap(serverRespDTO.getWfkap());
            responseDTO.setUmtskap(serverRespDTO.getUmtskap());
            responseDTO.setSnv(serverRespDTO.getSnv());
            if (serverRespDTO.getProtocolVersion() != null && serverRespDTO.getProtocolVersion().matches(KnConstants.PROTOCOL_VERSION_6_0)) {
                responseDTO.setMediaPortRefreshTimer(serverRespDTO.getMediaPortRefreshTimer());
                responseDTO.setTpts(serverRespDTO.getTpts());
                responseDTO.setTptm(serverRespDTO.getTptm());
                responseDTO.setIpv(serverRespDTO.getIpv());
                responseDTO.setTcpktmcv6(serverRespDTO.getTcpktmcv6());
                responseDTO.setMssrtv6(serverRespDTO.getMssrtv6());
                responseDTO.setMprtv6(serverRespDTO.getMprtv6());
                responseDTO.setGppr(serverRespDTO.getGppr());
                responseDTO.setPprw(serverRespDTO.getPprw());
                responseDTO.setGpprw(serverRespDTO.getGpprw());
                responseDTO.setLgsrvuriw(serverRespDTO.getLgsrvuriw());
                responseDTO.setPcruv6(serverRespDTO.getPcruv6());
                responseDTO.setPcgt(serverRespDTO.getPcgt());
                responseDTO.setXcaprooturiwifi(serverRespDTO.getXcaprooturiwifi());
                responseDTO.setAsnchg(serverRespDTO.isAsnchg());
                responseDTO.setApnName(serverRespDTO.getApnname());
            }
            //7.7.1
            responseDTO.setOdlfreq(serverRespDTO.getOdlfreq());
            responseDTO.setOdldur(serverRespDTO.getOdldur());
            responseDTO.setTfltconodl(serverRespDTO.getTfltconodl());
            responseDTO.setTfltsnapodl(serverRespDTO.getTfltsnapodl());
            responseDTO.setGodlreq(serverRespDTO.getGodlreq());
            responseDTO.setAsnchg(serverRespDTO.isAsnchg());
            //7.10
            if (serverRespDTO.getProtocolVersion() != null && serverRespDTO.getProtocolVersion().matches(KnConstants.PROTOCOL_VERSION_7_0)) {
                responseDTO.setMscl(serverRespDTO.getMscl());
                responseDTO.setUprio(serverRespDTO.getUprio());
                responseDTO.setEscl(serverRespDTO.getEscl());
                responseDTO.setAmrfpp(serverRespDTO.getAmrfpp());
                responseDTO.setCorpId(serverRespDTO.getCorpId());
                responseDTO.setCorpName(serverRespDTO.getCorpName());
                responseDTO.setSuirpi(serverRespDTO.getSuirpi());
                responseDTO.setIpvc(serverRespDTO.getIpvc());
                responseDTO.setIpvprefc(serverRespDTO.getIpvprefc());
                responseDTO.setIpvprefw(serverRespDTO.getIpvprefw());
            }
            if (serverRespDTO.getProtocolVersion() != null && serverRespDTO.getProtocolVersion().matches(KnConstants.PROTOCOL_VERSION_8_0)) {
                responseDTO.setPwsuriw(serverRespDTO.getPwsuriw());
                responseDTO.setGwsuriw(serverRespDTO.getGwsuriw());
                responseDTO.setTrice(serverRespDTO.getTrice());
                responseDTO.setDistListRr(serverRespDTO.getDispListRr());
                responseDTO.setpWsUri(serverRespDTO.getpWsUri());
                responseDTO.setgWsUri(serverRespDTO.getgWsUri());
                responseDTO.setWsCaeT(serverRespDTO.getWsCaeT());
            }
            if (serverRespDTO.getProtocolVersion() != null && serverRespDTO.getProtocolVersion().matches(KnConstants.PROTOCOL_VERSION_9_0)) {
                responseDTO.setNegCI(serverRespDTO.getNegCI());
                responseDTO.setClientCap(serverRespDTO.getClientCap());
                responseDTO.setCtl(serverRespDTO.getCtl());
                responseDTO.setCtlIntl(serverRespDTO.getCtlIntl());
                responseDTO.setIpaATtl(serverRespDTO.getIpaATtl());
                responseDTO.setRadChLS(serverRespDTO.getRadChLS());
                responseDTO.setRadScLS(serverRespDTO.getRadScLS());
            }

            if (serverRespDTO.getProtocolVersion() != null && serverRespDTO.getProtocolVersion().matches(KnConstants.PROTOCOL_VERSION_10_0)) {
                //8.1.2 PV - 10, couch-sync-mobile
                responseDTO.setMaxTextMessageSize(serverRespDTO.getMaxTextMessageSize());
                responseDTO.setMaxMultimediaMsgSizeOverCellular(serverRespDTO.getMaxMultimediaMsgSizeOverCellular());
                responseDTO.setMaxMultimediaMsgSizeOverWifi(serverRespDTO.getMaxMultimediaMsgSizeOverWifi());
                responseDTO.setPush2MessageDeliveryReceiptEnabled(serverRespDTO.getPush2MessageDeliveryReceiptEnabled());
                responseDTO.setPush2MessageReadReceiptEnabled(serverRespDTO.getPush2MessageReadReceiptEnabled());
                responseDTO.setPush2MessageFleetMemberGeoTag(serverRespDTO.getPush2MessageFleetMemberGeoTag());
                responseDTO.setMaxUserPredefinedMessages(serverRespDTO.getMaxUserPredefinedMessages());
                responseDTO.setGeoFenceDistanceMeasurementUnit(serverRespDTO.getGeoFenceDistanceMeasurementUnit());
                responseDTO.setGeoFencePeriodicLocationUpdateInterval(serverRespDTO.getGeoFencePeriodicLocationUpdateInterval());
                responseDTO.setGeoFencingPeriod(serverRespDTO.getGeoFencingPeriod());
                responseDTO.setGeoFenceDist4Client(serverRespDTO.getGeoFenceDist4Client());
                responseDTO.setGeoFenceNotificationTh(serverRespDTO.getGeoFenceNotificationTh());
                responseDTO.setGeoFenceNotificationInt(serverRespDTO.getGeoFenceNotificationInt());
                responseDTO.setOnCallLocationUpdateEnabled(serverRespDTO.getOnCallLocationUpdateEnabled());
                responseDTO.setOnCallLocationUpdateInterval(serverRespDTO.getOnCallLocationUpdateInterval());
                responseDTO.setSgwRootUriCellular(serverRespDTO.getSgwRootUriCellular());
                responseDTO.setSgwRootUriWifi(serverRespDTO.getSgwRootUriWifi());
                responseDTO.setAuthUriCellular(serverRespDTO.getAuthUriCellular());
                responseDTO.setAuthUriWifi(serverRespDTO.getAuthUriWifi());
                responseDTO.setCbBukInfo(serverRespDTO.getCbBukInfo());
                responseDTO.setSgwAuthMec(serverRespDTO.getSgwAuthMec());
                responseDTO.setSgwHbInterval(serverRespDTO.getSgwHbInterval());
                responseDTO.setMinVoiceMsgFallBackLen(serverRespDTO.getMinVoiceMsgFallBackLen());
                responseDTO.setMapProviderId(serverRespDTO.getMapProviderId());
                responseDTO.setMapsUriC(serverRespDTO.getMapsUriC());
                responseDTO.setMapsUriW(serverRespDTO.getMapsUriW());
                responseDTO.setMapsGeoUriC(serverRespDTO.getMapsGeoUriC());
                responseDTO.setMapsGeoUriW(serverRespDTO.getMapsGeoUriW());
                responseDTO.setgApiKey(serverRespDTO.getgApiKey());
                responseDTO.setSgmLocUriCell(serverRespDTO.getSgmLocUriCell());
                responseDTO.setSgmLocUriWifi(serverRespDTO.getSgmLocUriWifi());
                responseDTO.setMapStatsReportIntvl(serverRespDTO.getMapStatsReportIntvl());
                responseDTO.setLocExpTimeIntrvl(serverRespDTO.getLocExpTimeIntrvl());
            }

            if (serverRespDTO.getProtocolVersion() != null && serverRespDTO.getProtocolVersion().matches(KnConstants.PROTOCOL_VERSION_11_0)) {
                responseDTO.setClientRecLen(serverRespDTO.getClientRecLen());
                responseDTO.setPttRadioClientGrpht(serverRespDTO.getPttRadioClientGrpht());
                responseDTO.setPttradioClientNongrpht(serverRespDTO.getPttradioClientNongrpht());
                responseDTO.setActiveGeoFencGrpSize(serverRespDTO.getActiveGeoFencGrpSize());
                responseDTO.setDrxC(serverRespDTO.getDrxC());
                responseDTO.setCqiP(serverRespDTO.getCqiP());
                responseDTO.setCqiT(serverRespDTO.getCqiT());
                responseDTO.setCqiMR(serverRespDTO.getCqiMR());
                responseDTO.setCqiPwT(serverRespDTO.getCqiPwT());
                responseDTO.setCqiDisp(serverRespDTO.getCqiDisp());
                responseDTO.setWebDispMapStatReportIntvl(serverRespDTO.getWebDispMapStatReportIntvl());
                responseDTO.setWebDispUiStatReportIntvl(serverRespDTO.getWebDispUiStatReportIntvl());
            }

            if (serverRespDTO.getProtocolVersion() != null && serverRespDTO.getProtocolVersion().matches(KnConstants.PROTOCOL_VERSION_12_0)) {
                responseDTO.setUfmi(serverRespDTO.getUfmi());
                responseDTO.setIdenIpteropFlag(serverRespDTO.getIdenIpteropFlag());
            }

            if (serverRespDTO.getProtocolVersion() != null && serverRespDTO.getProtocolVersion().matches(KnConstants.PROTOCOL_VERSION_13_0)) {
                responseDTO.setMabg(serverRespDTO.getMabg());
                responseDTO.setMlabg(serverRespDTO.getMlabg());
                responseDTO.setMlabu(serverRespDTO.getMlabu());
                responseDTO.setNtgch(serverRespDTO.getNtgch());
                responseDTO.setNchzn(serverRespDTO.getNchzn());
                responseDTO.setRpn(serverRespDTO.getRpn());
                responseDTO.setRpe(serverRespDTO.getRpe());
                responseDTO.setAbdgUriC(serverRespDTO.getAbdgUriC());
                responseDTO.setAbdgUriW(serverRespDTO.getAbdgUriW());
                responseDTO.setIntCorpId(serverRespDTO.getIntCorpId());
                responseDTO.setKuidPrefix(serverRespDTO.getKuidPrefix());
                responseDTO.setReaInd(serverRespDTO.getReaInd());
                responseDTO.setEtgsMode(serverRespDTO.getEtgsMode());
                responseDTO.setMsmlen(serverRespDTO.getMsmlen());
                responseDTO.setMsmcnt(serverRespDTO.getMsmcnt());
                responseDTO.setMsmdst(serverRespDTO.getMsmdst());
            }
            if (serverRespDTO.getProtocolVersion() != null && serverRespDTO.getProtocolVersion().matches(KnConstants.PROTOCOL_VERSION_14_0)) {
                responseDTO.setMddss(serverRespDTO.getMddss());
                responseDTO.setMdyss(serverRespDTO.getMdyss());
            }
            if (serverRespDTO.getProtocolVersion() != null && serverRespDTO.getProtocolVersion().matches(KnConstants.PROTOCOL_VERSION_16_0)) {
                responseDTO.setEsriMapsClientId(serverRespDTO.getEsriMapsClientId());
                responseDTO.setEsriMapsSecretKey(serverRespDTO.getEsriMapsSecretKey());
                responseDTO.setEsriMapsCellularUri(serverRespDTO.getEsriMapsCellularUri());
                responseDTO.setEsriMapsGeoCellularUri(serverRespDTO.getEsriMapsGeoCellularUri());
                responseDTO.setEsriMapsWifiUri(serverRespDTO.getEsriMapsWifiUri());
                responseDTO.setEsriMapsGeoWifiUri(serverRespDTO.getEsriMapsWifiUri());
                responseDTO.setOsmfml(serverRespDTO.getOsmfml());
                responseDTO.setVmflrHldTime(serverRespDTO.getVmflrHldTime());
                responseDTO.setVmflrIdleTime(serverRespDTO.getVmflrIdleTime());
                responseDTO.setIspdse(serverRespDTO.getIspdse());
                responseDTO.setPdsmcl(serverRespDTO.getPdsmcl());
                responseDTO.setPdsmps(serverRespDTO.getPdsmps());
                responseDTO.setFdsUriC(serverRespDTO.getFdsUriC());
                responseDTO.setFdsUriW(serverRespDTO.getFdsUriW());
                responseDTO.setMsdss(serverRespDTO.getMsdss());
                responseDTO.setMfls(serverRespDTO.getMfls());
                responseDTO.setMafls(serverRespDTO.getMafls());
                responseDTO.setDflttl(serverRespDTO.getDflttl());
                responseDTO.setMflttl(serverRespDTO.getMflttl());
                responseDTO.setMmsgttl(serverRespDTO.getMmsgttl());
                responseDTO.setOsmlei(serverRespDTO.getOsmlei());
                responseDTO.setIsOptInNeeded(serverRespDTO.getIsOptInNeeded());
            }
            if (serverRespDTO.getProtocolVersion() != null && serverRespDTO.getProtocolVersion().matches(KnConstants.PROTOCOL_VERSION_17_0)) {
                responseDTO.setAcrtepc(serverRespDTO.getAcrtepc());
                responseDTO.setAcrtepg(serverRespDTO.getAcrtepg());
                responseDTO.setAcrteag(serverRespDTO.getAcrteag());
            }

            if (serverRespDTO.getProtocolVersion() != null && serverRespDTO.getProtocolVersion().matches(KnConstants.PROTOCOL_VERSION_19_0)) {
                responseDTO.setCskUploadInterval(serverRespDTO.getCskUploadInterval());
            }

            if (serverRespDTO.getProtocolVersion() != null && serverRespDTO.getProtocolVersion().matches(KnConstants.PROTOCOL_VERSION_19_0)) {
                responseDTO.setGmkoli(serverRespDTO.getGmkoli());
            }

            //PV 20
            if (serverRespDTO.getProtocolVersion() != null && serverRespDTO.getProtocolVersion().matches(KnConstants.PROTOCOL_VERSION_20_0)){
                knLogger.debug(methodName,"inside pv 20");
                responseDTO.setUsrkeymatolp(serverRespDTO.getUsrkeymatolp());
                responseDTO.setCskskew(serverRespDTO.getCskskew());
                responseDTO.setPckskew(serverRespDTO.getPckskew());
                responseDTO.setGmkskew(serverRespDTO.getGmkskew());
                responseDTO.setStrencrypt(serverRespDTO.getStrencrypt());
                responseDTO.setMdsiUri(serverRespDTO.getMdsiUri());
                responseDTO.setGmsUri(serverRespDTO.getGmsUri());
                responseDTO.setMsgstoreuric(serverRespDTO.getMsgstoreuric());
                responseDTO.setMsgstoreuriw(serverRespDTO.getMsgstoreuriw());
                responseDTO.setMsimulsdstxns(serverRespDTO.getMsimulsdstxns());
                responseDTO.setMsimulfdtxns(serverRespDTO.getMsimulfdtxns());
                responseDTO.setMsrchentries(serverRespDTO.getMsrchentries());
            }
            //PV 21
            if (serverRespDTO.getProtocolVersion() != null && serverRespDTO.getProtocolVersion().matches(KnConstants.PROTOCOL_VERSION_21_0)) {
                knLogger.debug(methodName, "inside pv 21");
                responseDTO.setRecordingStatus(serverRespDTO.getRecordingStatus());
            }

            //PV 23
            if (serverRespDTO.getProtocolVersion() != null && serverRespDTO.getProtocolVersion().matches(KnConstants.PROTOCOL_VERSION_23_0)) {
                knLogger.debug(methodName, "inside pv 23");
                responseDTO.setMaxVideoSessions(serverRespDTO.getMaxVideoSessions());
            }

            //PV 24
            if (serverRespDTO.getProtocolVersion() != null && serverRespDTO.getProtocolVersion().matches(KnConstants.PROTOCOL_VERSION_24_0)) {
                knLogger.debug(methodName, "inside pv 24");
                responseDTO.setMaxVideoSessions(serverRespDTO.getMaxVideoSessions());
                responseDTO.setmMemUsrRegrp(serverRespDTO.getmMemUsrRegrp());
                responseDTO.setmUsrRegrps(serverRespDTO.getmUsrRegrps());
                responseDTO.setBtfDuration(serverRespDTO.getBtfDuration());
                responseDTO.setBtfToneList(serverRespDTO.getBtfToneList());
                responseDTO.setBtfTonePauseIntervals(serverRespDTO.getBtfTonePauseIntervals());
                responseDTO.setmBtfPerOwner(serverRespDTO.getmBtfPerOwner());
                responseDTO.setGmsServId(serverRespDTO.getGmsServId());
                responseDTO.setmGrpRegrps(serverRespDTO.getmGrpRegrps());
            }
            //PV 25
            if (serverRespDTO.getProtocolVersion() != null && serverRespDTO.getProtocolVersion().matches(KnConstants.PROTOCOL_VERSION_25_0)) {
                knLogger.debug(methodName, "inside pv 25");
                responseDTO.setAltitudeFlag(serverRespDTO.getAltitudeFlag());
                responseDTO.setVerticalAccuracyFlag(serverRespDTO.getVerticalAccuracyFlag());
                responseDTO.setMediaMissingTimer(serverRespDTO.getMediaMissingTimer());
            }

            //PV 29+ (MBMS multicast-ka-interval)
            if (serverRespDTO.getMulticastKaInterval() != null) {
                responseDTO.setMulticastKaInterval(serverRespDTO.getMulticastKaInterval());
            }
            // PV 29
            if (serverRespDTO.getProtocolVersion() != null && serverRespDTO.getProtocolVersion().matches(KnConstants.PROTOCOL_VERSION_29_0)) {
                knLogger.debug(methodName, "inside pv 29");
                KnPttSettingsDocType pttSettingsDocType = new KnPttSettingsDocType();
                pttSettingsDocType.setUrl(serverRespDTO.getPttSettingUri());
                pttSettingsDocType.setEtag(serverRespDTO.getLastProfileUpdateTime());
                responseDTO.setPttSettingsDoc(pttSettingsDocType);
                responseDTO.setKpiRepAudInterval(serverRespDTO.getKpiRepAudInterval());
                responseDTO.setKpiRepUpRand(serverRespDTO.getKpiRepUpRand());
                responseDTO.setKpiRepMcs(serverRespDTO.getKpiRepMcs());
                responseDTO.setIpvmulti(serverRespDTO.getIpvmulti());
                responseDTO.setIpvprefmulti(serverRespDTO.getIpvprefmulti());
           }
        }
        knLogger.debug(methodName, "after populating the xdm response DTO - ", responseDTO);
        return responseDTO;
    }

    /*  public void sendMappedFailureResponse(IXDMResponseDTO responseDTO, KnMessage message, KnXDMServerException e) {
     String methodName = "sendMappedFailureResponse(IXDMResponseDTO, KnMessage, KnXDMServerException)";
     knLogger.debug( methodName, "sending mapped failure response");
    // String errorCode = e.getErrorCode();
     if (errorCode.contains("PUBLIB") && errorCode.contains("PM")) {
         //todo map the proper error code of prov library
     }
 }   */

    /**
     * method to delete the Corporate Profile.
     *
     * @param corpId       int
     * @param persisterTxn KnPersisterTxn
     * @return KnOPProvDTO
     * @throws com.kodiak.xdms.server.common.KnXDMServerException Exception
     */
    public KnOPProvDTO deleteCorpProfile(int corpId, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "deleteCorpProfile(KnXDMChangeMDNInfoDTO, KnPersisterTxn)";
        KnOPProvDTO responseDTO = null;
        knLogger.info(methodName, "ENTRY: Remove Corp profile Request - ", corpId);

        try {
            KnIPCorpProfileInfoDTO corpProfileInfoDTO = new KnIPCorpProfileInfoDTO();
            corpProfileInfoDTO.setCorpId(corpId);
            responseDTO = provClientIntf.deleteCorporateProfile(corpProfileInfoDTO, persisterTxn);
        } catch (KnXDMServerException e) {
            knLogger.error(methodName, "Failed to Remove Corp profile");
            throw e;
        } finally {
            knLogger.info(methodName, "ENTRY: Remove corp profile response - ", responseDTO);
        }
        return responseDTO;
    }

    /* method to populate the Library Input DTO
     *
     * @param pamAccInfoDTO KnXDMPAMAccInfoDTO
     * @return KnIPPAMAccInfoDTO Library input DTO
     */
    public KnIPPAMAccInfoDTO populatePAMAccountDTO(KnXDMPAMAccInfoDTO pamAccInfoDTO) {
        String methodName = "populatePAMAccountDTO(KnXDMPAMAccInfoDTO)";
        knLogger.debug(methodName, "XDM Subs PAM Account Info DTO - ", pamAccInfoDTO);

        KnIPPAMAccInfoDTO pamAccountInfoDTO = new KnIPPAMAccInfoDTO();
        pamAccountInfoDTO.setBillingName(pamAccInfoDTO.getBillingName());
        pamAccountInfoDTO.setBillingMdn(pamAccInfoDTO.getBillingNumber());
        pamAccountInfoDTO.setExtPamAccId(pamAccInfoDTO.getBillingNumber());
        pamAccInfoDTO.setOldBillingNumber(pamAccInfoDTO.getOldBillingNumber());
        pamAccountInfoDTO.setTotalNoOfLines(pamAccInfoDTO.getTotalNoOfLines());
        pamAccountInfoDTO.setSubscriberCount(pamAccInfoDTO.getSubsCount());
        pamAccountInfoDTO.setHierarchyType(pamAccInfoDTO.getHierarchyType());

        KnPAMSubsProfInfoDTO pamSubsProfInfoDTO = new KnPAMSubsProfInfoDTO();
        //populate PAM subs profile details
        if (pamAccInfoDTO.getProfileDetails() != null) {
            KnXDMPAMSubsProfInfoDTO xdmpamSubsProfInfoDTO = pamAccInfoDTO.getProfileDetails();
            pamSubsProfInfoDTO.setProfileName(xdmpamSubsProfInfoDTO.getProfileName());
            pamSubsProfInfoDTO.setSubscriberFS2(xdmpamSubsProfInfoDTO.getSubscriberFS2());
            pamSubsProfInfoDTO.setPubSubsType(xdmpamSubsProfInfoDTO.getPubSubsType());
            pamSubsProfInfoDTO.setCorpSubsType(xdmpamSubsProfInfoDTO.getCorpSubsType());
            pamSubsProfInfoDTO.setClient_Type(xdmpamSubsProfInfoDTO.getClient_Type());
            pamSubsProfInfoDTO.setExtCorpId(xdmpamSubsProfInfoDTO.getExtCorpId());
            pamSubsProfInfoDTO.setCorpName(xdmpamSubsProfInfoDTO.getCorpName());
            pamSubsProfInfoDTO.setServiceAuthStatus(xdmpamSubsProfInfoDTO.getServiceAuthStatus());
            pamSubsProfInfoDTO.setEmail(xdmpamSubsProfInfoDTO.getEmailAddress());
            pamSubsProfInfoDTO.setImei(xdmpamSubsProfInfoDTO.getIMEI());
            pamSubsProfInfoDTO.setHierarchyType(pamAccInfoDTO.getHierarchyType());

            //get custom details
            pamSubsProfInfoDTO.setCustomParamMap(xdmpamSubsProfInfoDTO.getCustomParamMap());
            //todo
            if(xdmpamSubsProfInfoDTO.getFeatureBitInfoMap()==null)
                pamSubsProfInfoDTO.setProvFSMap(pamAccInfoDTO.getFeatureBitInfoMap());
            else
                pamSubsProfInfoDTO.setProvFSMap(xdmpamSubsProfInfoDTO.getFeatureBitInfoMap());

            pamSubsProfInfoDTO.setPkgIdMap(xdmpamSubsProfInfoDTO.getPkgIdMap());
            pamSubsProfInfoDTO.setLicenseType(xdmpamSubsProfInfoDTO.getLicenseType());
            pamSubsProfInfoDTO.setFirstNetIndicator(xdmpamSubsProfInfoDTO.getFirstNetIndicator());
            pamAccountInfoDTO.setProfileDetails(pamSubsProfInfoDTO);
        }
        knLogger.debug(methodName, "KnIPPAMAccInfoDTO  - ", pamAccountInfoDTO);
        return pamAccountInfoDTO;
    }


    public KnXDMPAMAccInfoDTO populatePAMAccountDTO(KnXDMPAMAccInfoDTO responseDTO, KnOPPAMAccInfoDTO serverRespDTO) {
        String methodName = "populatePAMAccountDTO(KnXDMPAMAccInfoDTO, KnOPPAMAccInfoDTO)";
        knLogger.debug(methodName, "KnOPPAMAccInfo DTO - ", serverRespDTO);

        responseDTO.setBillingName(serverRespDTO.getBillingName());
        responseDTO.setBillingNumber(serverRespDTO.getBillingMdn());
        responseDTO.setTotalNoOfLines(serverRespDTO.getTotalNoOfLines());

        KnPAMSubsProfInfoDTO pamSubsProfInfoDTO;
        KnXDMPAMSubsProfInfoDTO xdmpamSubsProfInfoDTO = new KnXDMPAMSubsProfInfoDTO();
        //populate PAM subs profile details
        if (serverRespDTO.getProfileDetails() != null) {
            pamSubsProfInfoDTO = serverRespDTO.getProfileDetails();
            xdmpamSubsProfInfoDTO.setProfileName(pamSubsProfInfoDTO.getProfileName());
            xdmpamSubsProfInfoDTO.setSubscriberFS2(pamSubsProfInfoDTO.getSubscriberFS2());
            xdmpamSubsProfInfoDTO.setPubSubsType(pamSubsProfInfoDTO.getPubSubsType());
            xdmpamSubsProfInfoDTO.setCorpSubsType(pamSubsProfInfoDTO.getCorpSubsType());
            xdmpamSubsProfInfoDTO.setClient_Type(pamSubsProfInfoDTO.getClient_Type());
            xdmpamSubsProfInfoDTO.setExtCorpId(pamSubsProfInfoDTO.getExtCorpId());
            xdmpamSubsProfInfoDTO.setCorpName(pamSubsProfInfoDTO.getCorpName());
            xdmpamSubsProfInfoDTO.setServiceAuthStatus(pamSubsProfInfoDTO.getServiceAuthStatus());
            //get custom details
            xdmpamSubsProfInfoDTO.setCustomParamMap(pamSubsProfInfoDTO.getCustomParamMap());
            xdmpamSubsProfInfoDTO.setPkgIdMap(pamSubsProfInfoDTO.getPkgIdMap());
            xdmpamSubsProfInfoDTO.setFirstNetIndicator(pamSubsProfInfoDTO.getFirstNetIndicator());
            responseDTO.setProfileDetails(xdmpamSubsProfInfoDTO);
        }
        knLogger.debug(methodName, "KnXDMPAMAccInfoDTO - ", responseDTO);
        return responseDTO;
    }

    public KnIPBulkSubsProvInfoDTO populateBulkSubsProvInfoDTO(KnXDMPAMAccInfoDTO pamAccInfoDTO) {
        String methodName = "populateBulkSubsProvInfoDTO(KnXDMPAMAccInfoDTO)";
        knLogger.debug(methodName, "XDM PAM Account Info DTO - ", pamAccInfoDTO);

        KnIPBulkSubsProvInfoDTO subsProvInfoDTO = new KnIPBulkSubsProvInfoDTO();
        KnXDMPAMSubsProfInfoDTO pamSubsProfInfoDTO = pamAccInfoDTO.getProfileDetails();
        //populate PAM subs profile details
        subsProvInfoDTO.setPamAccId(pamSubsProfInfoDTO.getPamAccId());
        subsProvInfoDTO.setProfileId(pamSubsProfInfoDTO.getProfileId());
        subsProvInfoDTO.setProfileName(pamSubsProfInfoDTO.getProfileName());
        subsProvInfoDTO.setSubscriberFS2(pamSubsProfInfoDTO.getSubscriberFS2());
        subsProvInfoDTO.setPubSubsType(pamSubsProfInfoDTO.getPubSubsType());
        subsProvInfoDTO.setCorpSubsType(pamSubsProfInfoDTO.getCorpSubsType());
        subsProvInfoDTO.setClient_Type(pamSubsProfInfoDTO.getClient_Type());
        subsProvInfoDTO.setExtCorpId(pamSubsProfInfoDTO.getExtCorpId());
        subsProvInfoDTO.setCorpName(pamSubsProfInfoDTO.getCorpName());
        subsProvInfoDTO.setServiceAuthStatus(pamSubsProfInfoDTO.getServiceAuthStatus());

        subsProvInfoDTO.setMdns(pamAccInfoDTO.getProfileDetails().getMdns());
        //get custom details
        subsProvInfoDTO.setCustomParamMap(pamSubsProfInfoDTO.getCustomParamMap());
        subsProvInfoDTO.setProvFSMap(pamSubsProfInfoDTO.getFeatureBitInfoMap());
        subsProvInfoDTO.setPkgIdMap(pamSubsProfInfoDTO.getPkgIdMap());
        subsProvInfoDTO.setLicenseType(pamSubsProfInfoDTO.getLicenseType());
        subsProvInfoDTO.setFirstNetIndicator(pamSubsProfInfoDTO.getFirstNetIndicator());
        knLogger.debug(methodName, "Library PAM Acc DTO - ", subsProvInfoDTO);
        return subsProvInfoDTO;
    }

    /**
     * -->This method basically updates the External Subscriber data in the DB by calling createExtSubscriber method in Controller
     * -->Get extSubsRequestDTO (KnXDMExtSubsRequestDTO type) in the request parameter
     * -->Covert extSubsRequestDTO to extSubscriberInfoDTO (KnExtSubscriberInfoDTO type)
     * by calling method populateExtSubscriberInfoDTO
     * --> Pass the extSubscriberInfoDTO by calling the createExtSubscriber method
     * --> Store the response in opProvDTO (KnOPProvDTO type)
     * --> send  the response data
     *
     * @param extSubsRequestDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public KnXDMRespDTO createExtSubscriber(KnXDMExtSubsRequestDTO extSubsRequestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "createExtSubscriber(KnXDMExtSubsRequestDTO, KnPersisterTxn)";
        KnOPProvDTO opProvDTO = null;
        KnXDMRespDTO responseDTO = new KnXDMRespDTO();
        knLogger.debug(methodName, "Create External Subscriber Profile With Request DTO - ", extSubsRequestDTO);
        try {

            KnExtSubscriberInfoDTO extSubscriberInfoDTO = populateExtSubscriberInfoDTO(extSubsRequestDTO);
            opProvDTO = provClientIntf.createExtSubscriber(extSubscriberInfoDTO, persisterTxn);
            responseDTO.setResponseStatus(opProvDTO.getResponseStatus());
            responseDTO.setResponseMessage(opProvDTO.getResponseMessage());
            knLogger.debug(methodName, "Create External Subscriber Profile With Response DTO - ", responseDTO);

        } catch (KnProvException e) {
            knLogger.error(methodName, "BO Exception occurred :", e);
            throw new KnXDMServerException(e.getErrorCode(), e.getErrorMessage());
        }
        return responseDTO;
    }

    /**
     * -->This method basically updates the External Subscriber data in the DB by calling updateExtSubscriber method in Controller
     * -->Get extSubsRequestDTO (KnXDMExtSubsRequestDTO type) in the request parameter
     * -->Covert extSubsRequestDTO to extSubscriberInfoDTO (KnExtSubscriberInfoDTO type)
     * by calling method populateExtSubscriberInfoDTO
     * --> Pass the extSubscriberInfoDTO by calling the updateExtSubscriber method
     * --> Store the response in opProvDTO (KnOPProvDTO type)
     * --> send  the response data
     *
     * @param extSubsRequestDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public KnXDMRespDTO updateExtSubscriber(KnXDMExtSubsRequestDTO extSubsRequestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "updateExtSubscriber(KnXDMExtSubsRequestDTO, KnPersisterTxn)";
        KnOPProvDTO opProvDTO = null;
        KnXDMRespDTO responseDTO = new KnXDMRespDTO();
        knLogger.debug(methodName, "Update External Subscriber Profile Profile With Request DTO - ", extSubsRequestDTO);
        try {

            KnExtSubscriberInfoDTO extSubscriberInfoDTO = populateExtSubscriberInfoDTO(extSubsRequestDTO);
            opProvDTO = provClientIntf.updateExtSubscriber(extSubscriberInfoDTO, persisterTxn);
            responseDTO.setResponseStatus(opProvDTO.getResponseStatus());
            responseDTO.setResponseMessage(opProvDTO.getResponseMessage());
            knLogger.debug(methodName, "Update External Subscriber Profile Profile With Response DTO - ", responseDTO);
        } catch (KnProvException e) {
            knLogger.error(methodName, "BO Exception occurred :", e);
            throw new KnXDMServerException(e.getErrorCode(), e.getErrorMessage());
        }
        return responseDTO;
    }

    /**
     * -->This method basically deletes the External Subscriber from the DB by calling the method in Controller class
     * -->Get extSubsRequestDTO (KnXDMExtSubsRequestDTO type) in the request parameter
     * -->Covert extSubsRequestDTO to extSubscriberInfoDTO (KnExtSubscriberInfoDTO type)
     * by calling method populateExtSubscriberInfoDTO
     * --> Pass the extSubscriberInfoDTO by calling the deleteExtSubscriber method
     * --> Store the response in opProvDTO (KnOPProvDTO type)
     * --> send  the response data
     *
     * @param extSubsRequestDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public KnXDMRespDTO deleteExtSubscriber(KnXDMExtSubsRequestDTO extSubsRequestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "deleteExtSubscriber(KnXDMExtSubsRequestDTO, KnPersisterTxn)";
        KnOPProvDTO opProvDTO = null;
        KnXDMRespDTO responseDTO = new KnXDMRespDTO();
        knLogger.debug(methodName, "Delete External Subscriber Profile With Request DTO - ", extSubsRequestDTO);
        try {

            KnExtSubscriberInfoDTO extSubscriberInfoDTO = populateExtSubscriberInfoDTO(extSubsRequestDTO);
            opProvDTO = provClientIntf.deleteExtSubscriber(extSubscriberInfoDTO, persisterTxn);
            responseDTO.setResponseStatus(opProvDTO.getResponseStatus());
            responseDTO.setResponseMessage(opProvDTO.getResponseMessage());
            knLogger.debug(methodName, "Delete External Subscriber Profile With Response DTO - ", responseDTO);
        } catch (KnProvException e) {
            knLogger.error(methodName, "BO Exception occurred :", e);
            throw new KnXDMServerException(e.getErrorCode(), e.getErrorMessage());
        }
        return responseDTO;
    }

    /**
     * -->This method basically get the External Subscriber info data from the DB
     * -->Get extSubsRequestDTO (KnXDMExtSubsRequestDTO type) in the request parameter
     * -->Covert extSubsRequestDTO to extSubscriberInfoDTO (KnExtSubscriberInfoDTO type)
     * by calling method populateExtSubscriberInfoDTO
     * --> Pass the extSubscriberInfoDTO by calling the getExtSubscriberInfo method
     * -->Store the response data in extSubsInfoDTO (KnExtSubscriberInfoDTO type
     * -->Get the list of listOf KnExtSubscriberDTO from extSubsInfoDTO
     * -->Convert the  listOf KnExtSubscriberDTO data to list of KnXDMExtSubscriberDTO data to pass in the response
     *
     * @param extSubsRequestDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public KnXDMExtSubsResponseDTO getExtSubscriberInfo(KnXDMExtSubsRequestDTO extSubsRequestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getExtSubscriberInfo(KnXDMExtSubsRequestDTO, KnPersisterTxn)";
        KnXDMExtSubsResponseDTO responseDTO = new KnXDMExtSubsResponseDTO();
        knLogger.debug(methodName, "Get External Subscriber Profile With Request DTO - ", extSubsRequestDTO);
        KnExtSubscriberInfoDTO extSubsInfoDTO = null;
        try {
            KnExtSubscriberInfoDTO extSubscriberInfoDTO = populateExtSubscriberInfoDTO(extSubsRequestDTO);
            extSubsInfoDTO = provClientIntf.getExtSubscriberInfo(extSubscriberInfoDTO, persisterTxn);


            List<KnExtSubscriberDTO> listOfExtSubscriberDTOs = extSubsInfoDTO.getExtSubs();
            List<KnXDMExtSubscriberDTO> listOfXDMExtSubscriberDTOs = new ArrayList<>();

            for (KnExtSubscriberDTO extSubscriberDTO : listOfExtSubscriberDTOs) {
                KnXDMExtSubscriberDTO xdmExtSubsDTO = new KnXDMExtSubscriberDTO();
                xdmExtSubsDTO.setMdn(extSubscriberDTO.getMdn());
                xdmExtSubsDTO.setProfileId(extSubscriberDTO.getProfileId());
                xdmExtSubsDTO.setSetBy(extSubscriberDTO.getSetBy());
                listOfXDMExtSubscriberDTOs.add(xdmExtSubsDTO);
            }
            responseDTO.setExtSubs(listOfXDMExtSubscriberDTOs);
            responseDTO.setResponseStatus(extSubsInfoDTO.getResponseStatus());
            responseDTO.setResponseMessage(extSubsInfoDTO.getResponseMessage());
            knLogger.info(methodName, "Get External Subscriber Profile With Response DTO -", responseDTO);

        } catch (KnProvException e) {
            knLogger.error(methodName, "BO Exception occurred :", e);
            throw new KnXDMServerException(e.getErrorCode(), e.getErrorMessage());
        }
        return responseDTO;
    }

    /**
     * -->Get the mdn of the External subscriber for which corpid list is to be retrieved
     * -->call the getCorpIdsForExtSubscriber method by passing the mdn
     *
     * @param mdn
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public List<Integer> getCorpIdsForExtSubscriber(String mdn, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getCorpIdsForExtSubscriber(String, KnPersisterTxn)";
        List<Integer> listOfCorpIds = null;
        knLogger.debug(methodName, "Get CorpIds for External Subscriber Profile For MDN", KnGDPRTemplate.mdn(mdn));
        try {
            listOfCorpIds = provClientIntf.getCorpIdsForExtSubscriber(mdn, persisterTxn);
            knLogger.debug(methodName, "Get CorpIds for External Subscriber Profile With listOfCorpIds", listOfCorpIds);
        } catch (KnProvException e) {
            knLogger.error(methodName, "BO Exception occurred :", e);
            throw new KnXDMServerException(e.getErrorCode(), e.getErrorMessage());
        }
        return listOfCorpIds;
    }

    /**
     * -->Get extSubsRequestDTO (KnXDMExtSubsRequestDTO type) in the request
     * -->convert the extSubsRequestDTO to extSubscriberInfoDTO (KnExtSubscriberInfoDTO type) by calling
     * populateExtSubscriberInfoDTO  method
     * -->call addOrModifyExtSubscribers passing extSubscriberInfoDTO
     *
     * @param extSubsRequestDTO
     * @param persisterTxn
     * @return responseDTO
     * @throws KnXDMServerException
     */
    public KnXDMRespDTO addOrModifyExtSubscribers(KnXDMExtSubsRequestDTO extSubsRequestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "addOrModifyExtSubscribers(KnXDMExtSubsRequestDTO, KnPersisterTxn)";
        KnOPProvDTO opProvDTO = null;
        KnXDMRespDTO responseDTO = new KnXDMRespDTO();
        knLogger.debug(methodName, "AddOrModify External Subscriber Profile");
        try {

            KnExtSubscriberInfoDTO extSubscriberInfoDTO = populateExtSubscriberInfoDTO(extSubsRequestDTO);
            opProvDTO = provClientIntf.addOrModifyExtSubscribers(extSubscriberInfoDTO, persisterTxn);
            responseDTO.setResponseStatus(opProvDTO.getResponseStatus());
            responseDTO.setResponseMessage(opProvDTO.getResponseMessage());
            knLogger.debug(methodName, "AddOrModify External Subscriber Profile With Response DTO", responseDTO);
        } catch (KnProvException e) {
            knLogger.error(methodName, "BO Exception occurred :", e);
            throw new KnXDMServerException(e.getErrorCode(), e.getErrorMessage());
        }
        return responseDTO;
    }

    /**
     * -->Get extSubsRequestDTO in the request
     * -->convert the extSubsRequestDTO (KnXDMExtSubsRequestDTO type)to extSubscriberInfoDTO (KnExtSubscriberInfoDTO) type
     *
     * @param extSubsRequestDTO
     * @return
     */
    protected KnExtSubscriberInfoDTO populateExtSubscriberInfoDTO(KnXDMExtSubsRequestDTO extSubsRequestDTO) {
        String methodName = "populateExtSubscriberInfoDTO(KnXDMExtSubsRequestDTO)";
        KnExtSubscriberInfoDTO extSubscriberInfoDTO = new KnExtSubscriberInfoDTO();
        List<KnXDMExtSubscriberDTO> listOfXDMExtSubscriberDTO = extSubsRequestDTO.getExtSubs();
        List<KnExtSubscriberDTO> listOfExtSubscriberDTO = new ArrayList<>();

        for (KnXDMExtSubscriberDTO extSubscriberDTO : listOfXDMExtSubscriberDTO) {
            KnExtSubscriberDTO extDTO = new KnExtSubscriberDTO();
            extDTO.setMdn(extSubscriberDTO.getMdn());
            extDTO.setProfileId(extSubscriberDTO.getProfileId());
            extDTO.setSetBy(extSubscriberDTO.getSetBy());
            listOfExtSubscriberDTO.add(extDTO);
        }
        extSubscriberInfoDTO.setExtSubs(listOfExtSubscriberDTO);
        knLogger.debug(methodName, "Ext Subs InfoDTO With Response DTO", extSubscriberInfoDTO);
        return extSubscriberInfoDTO;
    }


    public List<String> retrievePAMAccountProvMDNs(int pamAccId, KnPersisterTxn persisterTxn)
            throws KnXDMServerException {
        final String methodName = "retrievePAMAccountProvMDNs(int, KnPersisterTxn)";
        knLogger.entry(methodName, pamAccId, persisterTxn);
        List<String> provMdnsList = null;

        try {

            provMdnsList = provClientIntf.retrievePAMAccountProvMDNs(pamAccId, persisterTxn);

        } catch (KnXDMServerException e) {
            knLogger.error(methodName, "Failed to retrieve Subscriber profile");
            throw e;
        }
        knLogger.exit(methodName, KnGDPRTemplate.mdnList(provMdnsList));
        return provMdnsList;
    }

    public List<String> getPamAccLastSequenceMdns(int pamAccId, int start, int end, KnPersisterTxn persisterTxn)
            throws KnXDMServerException {
        final String methodName = "getPamAccLastSequenceMdns(int, int, int,  KnPersisterTxn)";
        knLogger.entry(methodName, pamAccId, start, end, persisterTxn);
        List<String> lastSequenceList = null;

        try {

            lastSequenceList = provClientIntf.getPamAccLastSequenceMdns(pamAccId, start, end, persisterTxn);

        } catch (KnXDMServerException e) {
            knLogger.error(methodName, "Failed to retrieve Subscriber profile");
            throw e;
        }
        knLogger.exit(methodName, lastSequenceList);
        return lastSequenceList;
    }


    /**
     * Rollback the transaction
     *
     * @param txn transaction object
     */
    private void rollback(KnPersisterTxn txn) {
        try {
            knLogger.error("rollback()", "Rolling back transaction");
            if (txn != null) {
                txn.rollback();
            }
        } catch (Exception e) {
            knLogger.error("rollback(txn)", "Failed to rollback the transaction.");
        }
    }

    public KnOPProvDTO createSubscrRoamingProfiles(String mdn, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "createSubscrRoamingProfiles(String, KnPersisterTxn)";
        KnOPProvDTO opProvDTO = null;
        knLogger.info(methodName, "ENTRY:create Subscriber roaming profile for ", KnGDPRTemplate.mdn(mdn));
        try {
            opProvDTO = provClientIntf.createSubscrRoamingProfiles(mdn, persisterTxn);
            knLogger.debug(methodName, "EXIT:created Subscriber roaming profile with response ", opProvDTO);
        } catch (KnProvException e) {
            knLogger.error(methodName, "BO Exception occurred :", e);
            throw new KnXDMServerException(e.getErrorCode(), e.getErrorMessage());
        }
        return opProvDTO;
    }

    public KnOPProvDTO deleteSubscrRoamingProfiles(String mdn, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "deleteSubscrRoamingProfiles(String, KnPersisterTxn)";
        KnOPProvDTO opProvDTO = null;
        knLogger.debug(methodName, "ENTRY:Delete Subscriber roaming profile for", KnGDPRTemplate.mdn(mdn));
        try {
            opProvDTO = provClientIntf.deleteSubscrRoamingProfiles(mdn, persisterTxn);
            knLogger.debug(methodName, "EXIT:Deleted Subscriber roaming profile with response ", opProvDTO);
        } catch (KnProvException e) {
            knLogger.error(methodName, "BO Exception occurred :", e);
            throw new KnXDMServerException(e.getErrorCode(), e.getErrorMessage());
        }
        return opProvDTO;
    }

    public KnOPCorpProfileInfoDTO createCorpProfile(String extCorpId, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        final String methodName = "createCorpProfile(String)";
        knLogger.info(methodName, "ENTRY : extCorpId ", extCorpId);
        KnOPCorpProfileInfoDTO response = null;
        KnIPCorpProfileInfoDTO corpProfileInfoDTO = new KnIPCorpProfileInfoDTO();
        knLogger.info(methodName, " corpProfileInfoDTO ", corpProfileInfoDTO);
        response = provClientIntf.createCorpProfile(corpProfileInfoDTO, persisterTxn);
        knLogger.debug(methodName, "EXIT : ", response);
        return response;
    }

    /**
     * This method will accept the extCorpId
     *
     * @param extCorpId
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public int getCorpIdFromExtCorpId(String extCorpId, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        final String methodName = "getCorpIdFromExtCorpId(String,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: extCorpId-", extCorpId);
        int corpId = 0;
        KnOPCorpProfileInfoDTO corpProfileInfoDTO = provClientIntf.retrieveCorporateProfile(extCorpId, persisterTxn);
        knLogger.debug(methodName, "corpProfileInfoDTO:", corpProfileInfoDTO);
        corpId = corpProfileInfoDTO.getCorpId();
        knLogger.debug(methodName, "EXIT : corpId", corpId);
        return corpId;
    }

    /**
     * This method will call KnBulkSubsProvController to validate max corp subscriber per corp
     *
     * @param corpId
     * @param subsCount
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public boolean validateCorpSubsLimitAndPairLimit(int corpId, int subsCount, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        final String methodName = "validateCorpSubsLimit(int,int)";
        knLogger.debug(methodName, "ENTRY: corpId-", corpId, " ,subsCount", subsCount);
        boolean response = false;
        response = provClientIntf.validateCorpSubsLimitAndPairLimit(corpId, subsCount, persisterTxn);
        knLogger.debug(methodName, "EXIT : response", response);
        return response;
    }

    /**
     * this method will call the controller to update corporate profile last update time
     *
     * @param corpId
     * @param persisterTxn
     */
    public void updateCorpProfileLastUpdateTime(int corpId, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        final String methodName = "updateCorpProfileLastUpdateTime(int)";
        knLogger.debug(methodName, "ENTRY : corpId-", corpId);
        try {
            provClientIntf.updateCorpProfileLastUpdateTime(corpId, persisterTxn);

        } catch (KnProvException e) {
            knLogger.error(methodName, "BO Exception occurred :", e);
            throw new KnXDMServerException(e.getErrorCode(), e.getErrorMessage());
        }
        knLogger.debug(methodName, "EXIT : updated corporate profile last update time");
    }

    /*public List<KnOPProvDTO> sendBulkConfigDocNotification(List<String> mdns, KnPersisterTxn persisterTxn) {
        final String methodName = "sendBulkConfigDocNotification(List<String>)";
        knLogger.debug(methodName, " ENTRY : ", mdns);
        List<KnOPProvDTO> response = new ArrayList<>();
        KnOPProvDTO opProvDTO = null;
        try {
            response = provClientIntf.sendBulkConfigDocNotification(mdns, persisterTxn);
        } catch (KnProvException e) {
            e.printStackTrace();
        }
        knLogger.debug(methodName, " EXIT : ", response);
        return response;
    }*/
   /* public void updateEtagForNNISubscr(int corpId,KnPersisterTxn persisterTxn) throws KnXDMServerException {
        final String methodName = "updateEtagForNNISubscr(int,KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY : corpId =  ", corpId);
        try {
            provClientIntf.updateEtagForNNISubscr(corpId, persisterTxn);
        } catch (KnProvException e) {
            knLogger.error(methodName, "BO Exception occurred :", e);
            throw new KnXDMServerException(e.getErrorCode(), e.getErrorMessage());
        }
    }*/

    public void updateEtagForNNISubscr(String extCorpId, int clientType, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        final String methodName = "updateEtagForNNISubscr(String,KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY : extCorpId", extCorpId);
        try {
            provClientIntf.updateEtagForNNISubscr(extCorpId, clientType, persisterTxn);
            knLogger.debug(methodName, "Successfully updated Etag for NNI Subscriber");
        } catch (KnProvException e) {
            knLogger.error(methodName, "BO Exception occurred :", e);
            throw new KnXDMServerException(e.getErrorCode(), e.getErrorMessage());
        }
    }


    public boolean isMdnExistsInPseudoPoolNPamAccInfo(String mdn, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        final String methodName = "isMdnExistsInPseudoPoolNPamAccInfo(mdn)";
        knLogger.info(methodName, "ENTRY : Check Mdn is present in pseudoNoPool and PamAccInfo", KnGDPRTemplate.mdn(mdn));
        boolean response = false;
        try {
            response = provClientIntf.isMdnExistsInPseudoPoolNPamAccInfo(mdn, persisterTxn);
        } catch (KnProvException e) {
            knLogger.error(methodName, "BO Exception occurred :", e);
            throw new KnXDMServerException(e.getErrorCode(), e.getErrorMessage());
        }
        knLogger.debug(methodName, "EXIT : response", response);
        return response;

    }
    /**
     * This method will accept the extCorpId
     *
     * @param extCorpId
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public KnOPCorpProfileInfoDTO getCorpProfFromExtCorpId(String extCorpId, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        final String methodName = "getCorpProfFromExtCorpId(String,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: extCorpId-", extCorpId);
        KnOPCorpProfileInfoDTO corpProfileInfoDTO = provClientIntf.retrieveCorporateProfile(extCorpId, persisterTxn);
        knLogger.debug(methodName, "corpProfileInfoDTO: ", corpProfileInfoDTO);
        return corpProfileInfoDTO;
    }

    /**
     * This method will accept the extCorpId
     *
     * @param mdn
     * @param newActiveFS
     * @param oldActiveFS
     * @param persisterTxn
     * @return KnoOProvDTo
     * @throws KnXDMServerException
     */

    public KnOPProvDTO actionOnTGSSDoc(String mdn, String newActiveFS, String oldActiveFS,KnPersisterTxn persisterTxn) throws KnXDMServerException{
        final String methodName = "actionOnTGSSDoc(String,long,long,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: mdn-", KnGDPRTemplate.mdn(mdn) +" ,newActiveFS-"+newActiveFS+" ,oldActiveFS-"+oldActiveFS);
        KnOPProvDTO provDTO = provClientIntf.actionOnTGSSDoc(mdn,newActiveFS ,oldActiveFS ,persisterTxn);
        knLogger.debug(methodName, "provDTO: ", provDTO);
        return provDTO;
    }

    public KnIPSubsProvInfoDTO populateSubsProvInfoDTOForAssignUser(KnOPSubsProfileInfoDTO subsProvInputDTO) {
        String methodName = "populateSubsProvInfoDTO(KnXDMSubsProvDTO)";
        knLogger.debug(methodName, "XDM Subs prov Info DTO - ", subsProvInputDTO);

        KnIPSubsProvInfoDTO subsProvInfoDTO = new KnIPSubsProvInfoDTO();
        subsProvInfoDTO.setMdn(subsProvInputDTO.getMdn());
        subsProvInfoDTO.setPocPttServerId(subsProvInputDTO.getPocPttServerId());
        subsProvInfoDTO.setPresPttServerId(subsProvInputDTO.getPresPttServerId());
        subsProvInfoDTO.setIMEI(subsProvInputDTO.getIMEI());
        subsProvInfoDTO.setNetworkName(subsProvInputDTO.getNetworkName());
        subsProvInfoDTO.setPublicSubscriptionType(subsProvInputDTO.getPublicSubscriptionType());
        subsProvInfoDTO.setCorporateSubscriptionType(subsProvInputDTO.getCorporateSubscriptionType());
        subsProvInfoDTO.setExtCorpId(subsProvInputDTO.getExtCorpId());
        subsProvInfoDTO.setPairingInd(subsProvInputDTO.getPairingInd());
        subsProvInfoDTO.setAffiliateId(subsProvInputDTO.getAffiliateId());
        subsProvInfoDTO.setPayType(subsProvInputDTO.getPayType());
        subsProvInfoDTO.setRoamingTypes((ArrayList<Integer>) subsProvInputDTO.getRoamingTypes());
        subsProvInfoDTO.setIfMatch(subsProvInputDTO.getIfMatch());
        subsProvInfoDTO.setIfNoneMatch(subsProvInputDTO.getIfNoneMatch());
        subsProvInfoDTO.setEmailAddress(subsProvInputDTO.getEmailAddress());
        subsProvInfoDTO.setDispatchGroupMember(subsProvInputDTO.getDispatchGroupMember());
        subsProvInfoDTO.setSubsClientType(subsProvInputDTO.getSubsClientType());
        subsProvInfoDTO.setCorporateName(subsProvInputDTO.getCorporateName());
        subsProvInfoDTO.setAccountId(subsProvInputDTO.getAccountId());
        subsProvInfoDTO.setAutoPair(subsProvInputDTO.isAutoPair());
        subsProvInfoDTO.setHierarchyType(subsProvInputDTO.getHierarchyType());
        subsProvInfoDTO.setSubsDefPttRadio(subsProvInputDTO.getSubsDefPttRadio());
        subsProvInfoDTO.setProvFSMap(subsProvInputDTO.getProvFSMap());
        subsProvInfoDTO.setUfmi(subsProvInputDTO.getUfmi());
        subsProvInfoDTO.setiDenUserName(subsProvInputDTO.getiDenUserName());
        subsProvInfoDTO.setiDenPassword(subsProvInputDTO.getiDenPassword());
        subsProvInfoDTO.setiDenBusUnitId(subsProvInputDTO.getiDenBusUnitId());
        subsProvInfoDTO.setPkgIdMap(subsProvInputDTO.getPkgIdMap());
        subsProvInfoDTO.setLicenseType(subsProvInputDTO.getLicenseType());
        subsProvInfoDTO.setFirstNetIndicator(subsProvInputDTO.getFirstNetIndicator());
        //subsProvInfoDTO.setUserId(subsProvInputDTO.getUserId());
        subsProvInfoDTO.setMcId(subsProvInputDTO.getMcId());
        subsProvInfoDTO.setMcpttId(subsProvInputDTO.getMcpttId());
        subsProvInfoDTO.setMcVideoId(subsProvInputDTO.getMcVideoId());
        subsProvInfoDTO.setMcDataId(subsProvInputDTO.getMcDataId());
        subsProvInfoDTO.setMcsCompliance(subsProvInputDTO.getMcsCompliance());
        subsProvInfoDTO.setUserAgent(subsProvInputDTO.getUserAgent());
        subsProvInfoDTO.setClientPVmajorVer(subsProvInputDTO.getClientPVmajorVer());
        subsProvInfoDTO.setClientPVminorVer(subsProvInputDTO.getClientPVminorVer());
        subsProvInfoDTO.setLicenseType(subsProvInputDTO.getLicenseType());
        subsProvInfoDTO.setIMEI(subsProvInputDTO.getIMEI());
        subsProvInfoDTO.setServiceStatusAuthUser(subsProvInputDTO.getServiceStatusAuthUser());
        subsProvInfoDTO.setServiceStatusOp(subsProvInputDTO.getServiceStatusOp());
        subsProvInfoDTO.setServiceAuthStatus(subsProvInputDTO.getServiceAuthStatus());
        subsProvInfoDTO.setSwType(subsProvInputDTO.getSwType());
        subsProvInfoDTO.setPlatformType(subsProvInputDTO.getPlatformType());
        subsProvInfoDTO.setDynamicQosFlag(subsProvInputDTO.getDynamicQosFlag());
        subsProvInfoDTO.setDerivedKey(subsProvInputDTO.getDerivedKey());
        subsProvInfoDTO.setClientPassword(subsProvInputDTO.getClientPassword());
        subsProvInfoDTO.setLastActivationTime(subsProvInputDTO.getLastActivationTime());
        subsProvInfoDTO.setSubsFS2(subsProvInputDTO.getSubsFS2());
        subsProvInfoDTO.setClientFS2(subsProvInputDTO.getClientFS2());
        subsProvInfoDTO.setOpsFS2(subsProvInputDTO.getOpsFS2());
        subsProvInfoDTO.setCorpAdminFS2(subsProvInputDTO.getCorpAdminFS2());
        subsProvInfoDTO.setXdmsFS2(subsProvInputDTO.getXdmsFS2());
        subsProvInfoDTO.setFeatureRelVersion(subsProvInputDTO.getFeatureRelVersion());
        subsProvInfoDTO.setUserProfileName(subsProvInputDTO.getUserProfileName());
        if(null != subsProvInputDTO.getClusterId()) {
            subsProvInfoDTO.setClusterId(subsProvInputDTO.getClusterId());
        }
        if (null != subsProvInputDTO.getHierarchyId()) {
            subsProvInfoDTO.setHierarchyId(subsProvInputDTO.getHierarchyId());
        }
        knLogger.debug(methodName, "Library Subs Prov DTO - ", subsProvInfoDTO);
        return subsProvInfoDTO;
    }

    public KnXDMDeviceInfoRespDTO populateRespDeviceProfile(KnXDMDeviceInfoRespDTO responseDTO,
                                                            KnXDMDeviceProvDTO deviceProfileInfoDTO) {

        responseDTO.setDeviceId(deviceProfileInfoDTO.getDeviceId());
        responseDTO.setAccountId(deviceProfileInfoDTO.getAccountId());
        responseDTO.setDeviceType(deviceProfileInfoDTO.getDeviceType());
        responseDTO.setDevicePassword(deviceProfileInfoDTO.getDevicePassword());
        responseDTO.setDeviceIMPI(deviceProfileInfoDTO.getDeviceIMPI());
        responseDTO.setDeviceIMPU(deviceProfileInfoDTO.getDeviceIMPU());
        responseDTO.setDeviceShared(deviceProfileInfoDTO.getDeviceShared());
        responseDTO.setDeviceName(deviceProfileInfoDTO.getDeviceName());
        responseDTO.setDeviceClientId(deviceProfileInfoDTO.getDeviceClientId());
        responseDTO.setDeviceStatus(deviceProfileInfoDTO.getDeviceStatus());
        responseDTO.setDeviceActTimeStamp(deviceProfileInfoDTO.getDeviceActTimeStamp());
        responseDTO.setDeviceLastUsed(deviceProfileInfoDTO.getDeviceLastUsed());
        responseDTO.setDeviceCreatedAs(deviceProfileInfoDTO.getDeviceCreatedAs());
        responseDTO.setReqDeviceId(deviceProfileInfoDTO.getReqDeviceId());
        responseDTO.setDeviceSubscrMdn(deviceProfileInfoDTO.getDeviceSubscrMdn());
        responseDTO.setDeviceAddlInfo(deviceProfileInfoDTO.getDeviceAddlInfo());
        return responseDTO;
    }

    /**
     * This method will accept the baseMdn
     *
     * @param baseMdn String
     * @param persisterTxn KnPersisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public List<String> getMdnForUPM(String baseMdn, KnPersisterTxn persisterTxn) throws KnProvBOException,KnProvException{
        final String methodName = "getMdnForUPM(String,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: baseMdn-", KnGDPRTemplate.mdn(baseMdn));
        List<String> userProfileMdns = provClientIntf.getMdnForUPM(baseMdn, persisterTxn);
        knLogger.debug(methodName, "userProfileMdns: ", KnGDPRTemplate.mdnList(userProfileMdns));
        return userProfileMdns;
    }

    /**
     * method to login Notify Event.
     *
     * @param loginNotifyEventReq KnXDMLoginNotifyEventReqDTO
     * @param persisterTxn     KnPersisterTxn
     * @return KnOPProvDTO response DTO received from the server.
     * @throws KnXDMServerException generic exception thrown by the Server
     */
    public void loginNotifyEvent(KnXDMLoginNotifyEventReqDTO loginNotifyEventReq, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "loginNotifyEvent(KnXDMLoginNotifyEventReqDTO, KnPersisterTxn)";
        knLogger.info(methodName, "login Notify Event request with DTO - ", loginNotifyEventReq
        );
        try {
            knLogger.debug(methodName, "KnXDMLoginNotifyEventReqDTO ",loginNotifyEventReq);
            provClientIntf.loginNotifyEvent(loginNotifyEventReq, persisterTxn);
            knLogger.debug(methodName, "Login Notify Event Successfull");

        } catch (KnXDMServerException e) {
            knLogger.error(methodName, "Login Notify Event Failed ");
            throw e;
        }
    }


    /**
     * method to populate the XDM response (From Server to Wrapper) DTO
     *
     * @param responseDTO        KnXDMSubsProfileRespDTO
     * @param subsProfileInfoDTO KnOPSubsProfileInfoDTO
     * @return KnXDMSubsProfileRespDTO
     */
    public KnXDMSubsProfileRespDTO populateProfileMdnUserProfileResp(KnXDMSubsProfileRespDTO responseDTO, KnOPSubsProfileInfoDTO subsProfileInfoDTO) {
        String methodName = "populateProfileMdnUserProfileResp(KnXDMSubsProfileRespDTO, KnOPSubsProfileInfoDTO)";
        knLogger.debug(methodName, "populating the XDM Response DTO ");
        if (subsProfileInfoDTO != null) {
            responseDTO.setProfileMdnUserProfileIdMap(subsProfileInfoDTO.getProfileMdnUserProfileIdMap());
        }
        return responseDTO;
    }

    public List<KnSubsAliasInfoDTO> populateSubsAliasInfoList(List<KnXDMSubsAliasInfoDTO> xdmSubsAliasInfoList){
        String methodName = "populateSubsAliasInfoList()";
        knLogger.debug(methodName, "populating AliasInfo DTO ");
        List<KnSubsAliasInfoDTO> subsAliasInfoList = null;
        if(xdmSubsAliasInfoList!=null && !xdmSubsAliasInfoList.isEmpty()){
            subsAliasInfoList = new ArrayList<>();
            for(KnXDMSubsAliasInfoDTO xdmSubsAliasInfoDTO : xdmSubsAliasInfoList){
                KnSubsAliasInfoDTO aliasInfo = new KnSubsAliasInfoDTO();
                if(xdmSubsAliasInfoDTO.getAliasId()!=null)
                    aliasInfo.setAliasId(xdmSubsAliasInfoDTO.getAliasId());
                if(xdmSubsAliasInfoDTO.getAliasIdIssuer()!=null)
                    aliasInfo.setAliasIdIssuer(xdmSubsAliasInfoDTO.getAliasIdIssuer());
                subsAliasInfoList.add(aliasInfo);
            }
        }

        return subsAliasInfoList;
    }

    public KnCorporateProfilepersistDTO1 getCorporateAccountDetails(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnException, KnDAOException, KnProvBOException {
        String methodName = "getCorporateAccountDetails(String, KnPersisterTxn)";
        if (!(requestDTO instanceof KnXDMDeviceProvInfoDTO)) {
            knLogger.debug(methodName, "xdmRequestDTO not of type KnXDMCorpSubscInfoRequestDTO - xdmRequestDTO - ", requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMDeviceProvInfoDTO xdmRequestDTO = (KnXDMDeviceProvInfoDTO) requestDTO;
        knLogger.debug(methodName, "AccoutId in prov mediator - ", xdmRequestDTO);
        KnCorporateProfilepersistDTO1 profilepersistDTO = new KnCorporateProfilepersistDTO1();
        profilepersistDTO = provClientIntf.getCorporateAccountDetails(xdmRequestDTO, persisterTxn);
        knLogger.debug(methodName, "getCorporateAccountDetails", profilepersistDTO);
        KnXDMCorpProfile knXDMCorpProfile = new KnXDMCorpProfile();
        knLogger.debug(methodName, "corpname in response  :", knXDMCorpProfile.getCorporateName());
        return profilepersistDTO;
    }

    public KnXDMCorpAccountsListDTO getCorporateAccountsList(KnPersisterTxn persisterTxn,String txnId,String fetchSize, String nextToken) throws KnXDMServerException, KnDAOException {
        final String methodName = "getCorporateAccountsList(KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: getCorporateAccountsList- txnId::",txnId);
        KnXDMCorpAccountsListDTO respDto = new KnXDMCorpAccountsListDTO();
        try {
            respDto = provClientIntf.retrieveCorporationAccountsList(persisterTxn,fetchSize,nextToken);
            knLogger.debug(methodName, "getCorporateAccountsList", respDto);
        } catch (KnProvException e) {
            knLogger.error(methodName, "Exception occurred: ", e);
            throw new KnXDMServerException(e.getErrorCode(), e.getErrorMessage());
        }
        return respDto;
    }

    public KnXDMExtGWProfileListDTO getExtGWProfileList(String txnId) throws KnXDMServerException, KnDAOException {
        final String methodName = "getExtGWProfileList(txnId)";
        knLogger.debug(methodName, "ENTRY: getExtGWProfileList- txnId::",txnId);
        KnXDMExtGWProfileListDTO respDto = new KnXDMExtGWProfileListDTO();
        try {
            respDto = provClientIntf.retrieveExtGWProfileList();
            knLogger.debug(methodName, "getExtGWProfileList", respDto);
        } catch (KnProvException e) {
            knLogger.error(methodName, "Exception occurred: ", e);
            throw new KnXDMServerException(e.getErrorCode(), e.getErrorMessage());
        }
        return respDto;
    }
}
