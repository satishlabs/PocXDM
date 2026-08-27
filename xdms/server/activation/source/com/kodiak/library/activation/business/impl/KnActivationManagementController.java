/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**************************************************************************
 * <p/>
 * File name:  KnActivationManagementController.java
 * Subsystem:  Activation Library
 * <p/>
 * Name                   Date         Release
 * -------------------- ------------ -------------------------------------
 * Rashmi Kamat         29-Oct-2010       6.4
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
 * ************************************************************************/
package com.kodiak.library.activation.business.impl;

import com.kodiak.common.commdto.response.KnRadioDeviceActResponseDTO;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnGeneralCacheUtil;
import com.kodiak.common.resources.KnConstants.FEATURE_SET;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.library.activation.business.IClientManagementController;
import com.kodiak.library.activation.business.KnActBOException;
import com.kodiak.library.activation.business.helper.KnActClientInfoUtil;
import com.kodiak.library.activation.business.helper.KnConfigInfoUtil;
import com.kodiak.library.activation.dao.KnActivationFactorySelector;
import com.kodiak.library.activation.dao.persister.IActClientXDMServerDAO;
import com.kodiak.library.activation.dao.persister.db.tables.xdms.KnDeviceInfoDAO;
import com.kodiak.library.activation.dao.persister.db.tables.xdms.KnSubscrInfoDAO;
import com.kodiak.library.activation.dto.clientdat.KnIPClientRegistryDTO;
import com.kodiak.library.activation.dto.clientdat.KnIPRadioDeviceClientDTO;
import com.kodiak.library.activation.dto.common.KnDeviceInfoDTO;
import com.kodiak.library.activation.dto.common.KnSubscrInfoDTO;
import com.kodiak.library.activation.dto.persistdat.KnDeviceInfoPersistDTO;
import com.kodiak.library.activation.dto.persistdat.KnSubscriptionKeyInfoPersistDTO;
import com.kodiak.library.activation.resources.KnConstants;
import com.kodiak.library.activation.resources.KnErrorCodes;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.KnXDMServerSystemException;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dao.KnFactorySelector;
import com.kodiak.xdms.server.common.dao.persister.IXDMServerDAO;
import com.kodiak.xdms.server.common.dto.common.KnXDMSServiceConfigDTO;
import com.kodiak.xdms.server.common.dto.persistdat.KnDeviceImpiInfoPersistDTO;
import com.kodiak.xdms.server.common.framework.aas.KnAASException;
import com.kodiak.xdms.server.common.framework.aas.KnAASFramework;
import com.kodiak.xdms.server.common.framework.aas.authentication.KnAuthenticationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.resources.KnConstants.DEVICE_TYPE;
import com.kodiak.xdms.server.common.resources.KnConstants.SUBSCRIBERS_CLIENT_TYPE;
import com.kodiak.xdms.server.common.util.KnGeneralUtil;

import java.util.Map;

import static com.kodiak.common.resources.KnConstants.ENABLED;
import static com.kodiak.xdms.server.common.resources.KnConstants.DB_MGR_LOCAL_PTTID;
import static com.kodiak.xdms.server.common.resources.KnConstants.DELIM;

/**
 * KnActivationManagementController business entity - this class defines all the Client Provisioning / management<BR>
 * related methods. Almost all the methods in this entity accepts an input dto and do execute the <BR>
 * business logic accordingly. First, each method validates the input dto and on successfull validation<BR>
 * retrieves the necessary information from the input dto for further processing. All the methods defined<BR>
 * in this class throws KnActBOException.
 */
public class KnActivationManagementController implements IClientManagementController {
    private static final KnLogger knLogger = KnLogger.getLogger(KnActivationManagementController.class);

    private KnAASFramework authorizationFwk = null;
    private KnActClientInfoUtil clientInfoUtil = KnActClientInfoUtil.getInstance();
    KnConfigInfoUtil configInfoUtil = null;
    private KnGenInfoUtil genInfoUtil;
    private KnGeneralUtil generalUtil;
    private static KnGeneralCacheUtil generalCacheUtil;

    public KnActivationManagementController() {
        authorizationFwk = KnAASFramework.getInstance(KnConstants.LIBRARY_NAME);
        configInfoUtil = KnConfigInfoUtil.getInstance();
        genInfoUtil = KnGenInfoUtil.getInstance();
        generalUtil = new KnGeneralUtil();
        generalCacheUtil = new KnGeneralCacheUtil();
    }

    /**
     * This method activate group connected client to use client for any group connected services.
     * Accepts clientregistry dto which contains a password to be set in registry.
     * <p/>
     * Operations:
     * Validates input dto (optional)
     * Fetches the pttserver id for client mdn.
     * Fetches the authorization data : performer details for client
     * and sets it in persist dto.
     * Authorize persist dto
     * Fetch business - validation data if anything needed and populate to persist dto
     * Validates persist dto
     * Perform actual operation by initating client registration request to SIP GW.
     *
     * @param ipClientRegistryDTO client registry dto object contains activationkey, password, clientcapabilityset,etc.
     * @throws com.kodiak.library.activation.business.KnActBOException
     *                               throws exception if activationKey is expired, client feature bit is disabled,etc.
     * @throws KnValidationException throws business validation exception
     * @throws KnAASException        throws authorization exception if GC client feature bit is disabled.
     */
    /*
     * public KnXDMActivateRespDTO registerClient(KnIPClientRegistryDTO
     * ipClientRegistryDTO) throws KnActBOException, KnAASException,
     * KnValidationException { String methodName =
     * "registerClient(KnIPClientRegistryDTO)"; knLogger.debug( methodName,
     * "ENTRY -> Input DTO Passed : " + ipClientRegistryDTO); knLogger.debug(
     * methodName, " ipClientRegistryDTO.getClientCapabilitySet() " +
     * ipClientRegistryDTO.getClientCapabilitySet()); KnPersisterTxn persisterTxn =
     * null; KnXDMActivateRespDTO result = null; try { persisterTxn =
     * KnPersisterTxn.getPersisterTxn(); persisterTxn.open();
     *
     * String xdmServerId = clientInfoUtil.getXdmServerId(); if (xdmServerId !=
     * null) { result = clientInfoUtil.requestProcess(ipClientRegistryDTO,
     * ipClientRegistryDTO.getMdn(), xdmServerId); // sent 60 sec for all client
     * (this will add to current time and set Expiry time )
     * KnSubscriptionKeyInfoPersistDTO subsInfo = new
     * KnSubscriptionKeyInfoPersistDTO();
     * subsInfo.setMdn(ipClientRegistryDTO.getMdn());
     * subsInfo.setServiceName(ipClientRegistryDTO.getServiceName());
     * subsInfo.setExpiryTime(result.getExpiryTime());
     *
     * // if MDN is TP client type and if it associated with TP account we are not
     * updating expiry time. if (result.getExpiryTime() !=
     * KnConstants.THIRD_PARTY_EXPIRY_TIME) { IActClientXDMServerDAO
     * locationServerDAO =
     * KnActivationFactorySelector.getDAOFactory(KnFactorySelector.DB).
     * createXDMServerDAO(); locationServerDAO.updateExpiryTime(subsInfo,
     * persisterTxn); } } // Save Transaction persisterTxn.save(); } catch
     * (KnActBOException ex) { knLogger.error( methodName, "BO Exception occured : "
     * + ex); rollback(persisterTxn); throw ex; } catch (KnDAOException ex) {
     * rollback(persisterTxn); if
     * (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) { knLogger.error(
     * methodName, "Row not found - Dao Exception : " + ex); throw new
     * KnAuthenticationException(KnErrorCodes.Authenticator.AUTHENTICATION_FAILED,
     * "Row not found exception", "WEBSERVICE", null); } knLogger.error( methodName,
     * "DAO Exception occured : " + ex); throw new
     * KnActBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
     * "Persistence exception occured : ", ex); } catch (Exception ex) {
     * knLogger.error( methodName,
     * "Exception occured while Subscriber Activation : " + ex.getMessage());
     * rollback(persisterTxn); throw new
     * KnXDMServerSystemException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
     * "Unexpected exception occured while " + "Subscriber Activation", ex); }
     * return result; }
     */

//    /**
//     * This is utility method provided for the client implementations to retrieve dial plan configuration.
//     *
//     * @return
//     * @throws com.kodiak.library.activation.business.KnActBOException
//     * @throws KnValidationException
//     */
//    public Map<String, KnDialPlanConfigDTO> getDialPlanConfig() throws KnActBOException, KnValidationException {
//        String methodName = "getDialPlanConfig()";
//
//        knLogger.debug( methodName, "ENTRY -> Getting dial plan info ...");
//
//        Map<String, KnDialPlanConfigDTO> dialPlanConfigMap = null;
//
//        try {
//            dialPlanConfigMap = clientInfoUtil.getDialPlanConfig();
//        } catch (KnActBOException e) {
//            knLogger.error( methodName, "Failed to get the dial plan info!");
//            throw new KnActBOException(KnErrorCodes.BOEntity.DIALPLANCONFIG_NOT_FOUND, e);
//        }
//
//        if (dialPlanConfigMap == null || dialPlanConfigMap.size() < 1) {
//            throw new KnActBOException(KnErrorCodes.BOEntity.DIALPLANCONFIG_NOT_FOUND, "Failed to retrieve dial plan config info!");
//        }
//
//        knLogger.debug( methodName, "Returning Dial plan config info : " + dialPlanConfigMap);
//        return dialPlanConfigMap;
//    }

    /**
     * Rollback the transaction
     *
     * @param txn transaction object
     */
    private void rollback(KnPersisterTxn txn) {
        try {
            txn.rollback();
        } catch (Exception e) {
            knLogger.error("rollback(txn)", "Failed to rollback the transaction.");
        }
    }

    @Override
    public String retrieveMDN(KnIPClientRegistryDTO ipClientRegistryDTO) throws KnActBOException, KnAASException {
        final String methodName = "retrieveMDN(KnIPClientRegistryDTO)";
        knLogger.entry(methodName, ipClientRegistryDTO);

        String activationKey = ipClientRegistryDTO.getActivationKey();
        String mdn = null;
        KnPersisterTxn persisterTxn = null;

        try {
            persisterTxn = KnPersisterTxn.getPersisterTxn();
            persisterTxn.open();

            IActClientXDMServerDAO locationServerDAO = KnActivationFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO();
            KnSubscriptionKeyInfoPersistDTO subsKeyPersistDTO = locationServerDAO.retreieveSubscriptionKeyInfo(activationKey, persisterTxn);
            knLogger.debug(methodName, "DB ServiceName: - ", subsKeyPersistDTO.getServiceName());
            knLogger.debug(methodName, "Requested ServiceName: - ", ipClientRegistryDTO.getServiceName());
            if (subsKeyPersistDTO.getServiceName().equals(KnConstants.SERVICENAME_WIFIONLYCLIENT) && ipClientRegistryDTO.getServiceName().equals(KnConstants.SERVICENAME_HANDSET)) {
                knLogger.debug(methodName, "Setting input service name as PoCWIFIOnly for Tablet client");
                ipClientRegistryDTO.setServiceName(KnConstants.SERVICENAME_WIFIONLYCLIENT);
            }
            subsKeyPersistDTO.setInputDTO(ipClientRegistryDTO);
            mdn = subsKeyPersistDTO.getMdn();
            ipClientRegistryDTO.setMdn(mdn);
            // Authorize
            knLogger.debug(methodName, "Invoking Authorization.", KnGDPRTemplate.mdn(mdn));
            // 4. authorize the subscriber
            authorizationFwk.authorize(subsKeyPersistDTO);
            knLogger.debug(methodName, "Authorized successfully.");
            Integer subsClientType;
            //added null chk getting service name & type
            if (ipClientRegistryDTO.getUserAgentDTO() != null && ipClientRegistryDTO.getUserAgentDTO().getProtocolVersion() != null && ipClientRegistryDTO.getUserAgentDTO().getProtocolVersion().split("\\.")[0].matches(com.kodiak.common.resources.KnConstants.PROTOCOL_VERSION_REGEX)) {
                subsClientType = configInfoUtil.getSubsClientType(ipClientRegistryDTO.getServiceName());
            } else {
                subsClientType = 1;
            }
            knLogger.debug(methodName, "Subscriber client type fetched - " + subsClientType);
            ipClientRegistryDTO.setSubsClientType(subsClientType);

            // Save Transaction
            persisterTxn.save();

        } catch (KnDAOException e) {
            rollback(persisterTxn);
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(e.getErrorCode())) {
                knLogger.error(methodName, "Row not found - Dao Exception : " + e);
                throw new KnAuthenticationException(KnErrorCodes.Authorizer.INVALID_ACTIVATION_KEY, "Row not found exception", "WEBSERVICE", null);
            }
            knLogger.error(methodName, "DAO Exception occured : " + e);

        } catch (KnAASException e) {
            knLogger.error(methodName, "BO Exception occured : " + e);
            rollback(persisterTxn);
            throw e;

        } catch (Exception e) {
            knLogger.error(methodName, "Exception occured while Subscriber Activation : " + e.getMessage());
            rollback(persisterTxn);
            throw new KnXDMServerSystemException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Unexpected exception occured while " +
                    "Subscriber Activation", e);
        }

        knLogger.exit(methodName, KnGDPRTemplate.mdn(mdn));
        return mdn;
    }

    @Override
    public KnDeviceInfoDTO deviceActivation(KnIPClientRegistryDTO ipClientRegistryDTO) throws KnActBOException, KnAASException {
        final String methodName = "deviceActivation(KnIPClientRegistryDTO)";
        knLogger.entry(methodName, ipClientRegistryDTO);

        String activationKey = ipClientRegistryDTO.getActivationKey();
        String mdn = null;
        KnPersisterTxn persisterTxn = null;
        KnDeviceInfoDTO response = new KnDeviceInfoDTO();

        try {
            persisterTxn = KnPersisterTxn.getPersisterTxn();
            persisterTxn.open();

            IActClientXDMServerDAO locationServerDAO = KnActivationFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO();
            KnSubscriptionKeyInfoPersistDTO subsKeyPersistDTO = locationServerDAO.retreieveSubscriptionKeyInfo(activationKey, persisterTxn);
            knLogger.debug(methodName, "DB ServiceName: - ", subsKeyPersistDTO.getServiceName());

            subsKeyPersistDTO.setInputDTO(ipClientRegistryDTO);
            mdn = subsKeyPersistDTO.getMdn();
            ipClientRegistryDTO.setMdn(mdn);
            // Authorize
            knLogger.debug(methodName, "Invoking Authorization.", KnGDPRTemplate.mdn(mdn));
            // 4. authorize the subscriber
            authorizationFwk.authorize(subsKeyPersistDTO);
            knLogger.debug(methodName, "Authorized successfully.");


            //Get subscriber profile not found throw error
            KnSubscrInfoDAO subscrInfoDAO = KnActivationFactorySelector.getDAOFactory(KnFactorySelector.DB).createSubscrInfoDAO();
            KnDeviceInfoDAO deviceInfoDAO = KnActivationFactorySelector.getDAOFactory(KnFactorySelector.DB).createDeviceInfoDAO();
            KnSubscrInfoDTO subscrInfoDTO = subscrInfoDAO.getSubscrInfo(mdn, persisterTxn);
            int subsClientType = subscrInfoDTO.getClientType();
            int subsLicenseType = subscrInfoDTO.getLicenseType();
            String imei=ipClientRegistryDTO.getImeiNum();
            if(imei!=null)
            {
                knLogger.info(methodName,"imei:"+imei);
                subscrInfoDTO.setImei(imei);
                subscrInfoDAO.updateSubscriberProfile(subscrInfoDTO,persisterTxn);
            }


            Integer expirytime = genInfoUtil.getActivationCodeConfig(subsClientType, com.kodiak.common.resources.KnConstants.CLIENT_INTF_CAT, persisterTxn).getActCodeExpiry();
            subsKeyPersistDTO.setExpiryTime(expirytime);
            subsKeyPersistDTO.setActivationKey(activationKey);
            locationServerDAO.updateExpiryTime(subsKeyPersistDTO, persisterTxn);

            String pttServerId = System.getenv(DB_MGR_LOCAL_PTTID);
            KnXDMSServiceConfigDTO xdmsServiceConfigDTO = genInfoUtil.retrieveXDMSServiceConfig(pttServerId, persisterTxn);
            String realm = xdmsServiceConfigDTO.getAuthRealm();
            knLogger.debug(methodName, "realm ", realm);
            String password = generalUtil.generateMD5(ipClientRegistryDTO.getMdn().concat(DELIM).concat(realm).concat(DELIM).concat(ipClientRegistryDTO.getAuthKey()));
            knLogger.debug(methodName, "generated MD5 ", password);
            long millis = System.currentTimeMillis();
            knLogger.debug(methodName, "millis ", millis);
            boolean isExist = true;
            KnDeviceInfoPersistDTO deviceInfoPersistDTO = deviceInfoDAO.getDeviceDetails(mdn, persisterTxn);
            if (deviceInfoPersistDTO == null) {
                deviceInfoPersistDTO = new KnDeviceInfoPersistDTO();
                isExist = false;
            }
            String deviceImpi = deviceInfoPersistDTO.getDeviceIMPI();
            boolean bitEnabled = com.kodiak.common.resources.KnGeneralUtil.getFeatureBitValue(subscrInfoDTO.getSubsFS2(), FEATURE_SET.MCDEVICE.value());
            if(bitEnabled) {
            	deviceInfoPersistDTO.setDeviceType(DEVICE_TYPE.MC_DEVICE.Value());
            }else {
            	deviceInfoPersistDTO.setDeviceType(DEVICE_TYPE.RADIO_NEXT_DEVICE.Value());
            }
            deviceInfoPersistDTO.setDeviceId(mdn);
            deviceInfoPersistDTO.setDeviceStatus(KnConstants.DEVICE_ACTIVATION_ACTIVATED);
            deviceInfoPersistDTO.setDeviceActTimeStamp(millis);
            deviceInfoPersistDTO.setDeviceLastUsed(millis);
            deviceInfoPersistDTO.setDeviceDigestPassword(password);
            deviceInfoPersistDTO.setDeviceIMPI(com.kodiak.xdms.server.common.resources.KnConstants.TEL_URI_TEMPLATE + mdn);
            deviceInfoPersistDTO.setCorpId(subscrInfoDTO.getCorpId());
            IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB)
    				.createXDMServerDAO(pttServerId);
            if (isExist) {
                if (deviceImpi != null && !deviceImpi.isEmpty()) {
                    deviceInfoDAO.updateDeviceInfo(deviceInfoPersistDTO, persisterTxn);
                } else {
                    deviceInfoDAO.updateDeviceInfoWithImpi(deviceInfoPersistDTO, persisterTxn);
                }
                com.kodiak.xdms.server.common.dto.persistdat.KnDeviceInfoPersistDTO deletedeviceInfo=new com.kodiak.xdms.server.common.dto.persistdat.KnDeviceInfoPersistDTO();
                deletedeviceInfo.setDeviceId(mdn);
                xdmServerDAO.deleteDeviceImpiInfo(deletedeviceInfo, persisterTxn);
                KnDeviceImpiInfoPersistDTO deviceImpiInfo = new KnDeviceImpiInfoPersistDTO();
				deviceImpiInfo.setDeviceImpi(com.kodiak.xdms.server.common.resources.KnConstants.TEL_URI_TEMPLATE + mdn);
				deviceImpiInfo.setDeviceImpu(com.kodiak.xdms.server.common.resources.KnConstants.TEL_URI_TEMPLATE + mdn);
				xdmServerDAO.createDeviceImpiInfo(deviceImpiInfo, persisterTxn);
            } else {
                deviceInfoPersistDTO.setDeviceCreatedAs(com.kodiak.xdms.server.common.resources.KnConstants.IMPLICIT_DEVICE);
                deviceInfoDAO.addDeviceInfo(deviceInfoPersistDTO, persisterTxn);
                KnDeviceImpiInfoPersistDTO deviceImpiInfo = new KnDeviceImpiInfoPersistDTO();
				deviceImpiInfo.setDeviceImpi(com.kodiak.xdms.server.common.resources.KnConstants.TEL_URI_TEMPLATE + mdn);
				deviceImpiInfo.setDeviceImpu(com.kodiak.xdms.server.common.resources.KnConstants.TEL_URI_TEMPLATE + mdn);
				xdmServerDAO.createDeviceImpiInfo(deviceImpiInfo, persisterTxn);
            }

            response.setClientType(subsClientType);
            response.setLicenseType(subsLicenseType);
            response.setMdn(mdn);
            if (subscrInfoDTO.getLicenseType() != ENABLED) {
                response.setLoginType(KnConstants.IMPLICIT_LOGIN_TYPE);
                response.setUserId(mdn);
            } else {
                response.setLoginType(KnConstants.EXPLICIT_LOGIN_TYPE);
            }
            // Get CLIENT_ID and CLIENT_SECRET from the Micro Service common configuration
            if (subsClientType != SUBSCRIBERS_CLIENT_TYPE.DISPATCH.value()) {
                Map<String, String> oidcClientIdSecretMap = generalCacheUtil.getOidcClientIDSecret(KnConstants.HS_CLIENT_SECRET);
                String clientSecret = oidcClientIdSecretMap.get(KnConstants.HS_CLIENT_SECRET);
                response.setClientId(KnConstants.CLIENT_ID);
                response.setClientSecret(clientSecret);
            } else {
                Map<String, String> oidcClientIdSecretMap = generalCacheUtil.getOidcClientIDSecret(KnConstants.WDS_CLIENT_SECRET);
                String clientSecret = oidcClientIdSecretMap.get(KnConstants.WDS_CLIENT_SECRET);
                response.setClientId(KnConstants.CLIENT_ID_WSD);
                response.setClientSecret(clientSecret);
            }
            // Save Transaction
            persisterTxn.save();

        } catch (KnDAOException e) {
            rollback(persisterTxn);
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(e.getErrorCode())) {
                knLogger.error(methodName, "Row not found - Dao Exception : " + e);
                throw new KnActBOException(KnErrorCodes.BOEntity.SUBSCRIBER_INFO_NOT_FOUND, "Row not found exception");
            }
            knLogger.error(methodName, "DAO Exception occured : " + e);

        } catch (KnAASException e) {
            knLogger.error(methodName, "BO Exception occured : " + e);
            rollback(persisterTxn);
            throw e;

        } catch (Exception e) {
            knLogger.error(methodName, "Exception occured while Subscriber Activation : " + e.getMessage());
            rollback(persisterTxn);
            throw new KnXDMServerSystemException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Unexpected exception occured while " +
                    "Subscriber Activation", e);
        }

        knLogger.exit(methodName, KnGDPRTemplate.mdn(mdn));
        return response;
    }

    @Override
    public KnRadioDeviceActResponseDTO radioDeviceActivation(KnIPRadioDeviceClientDTO ipRadioDeviceClientDTO) throws KnActBOException, KnAASException {
        final String methodName = "radioDeviceActivation(KnIPRadioDeviceClientDTO)";
        knLogger.info(methodName, "Entry : ", ipRadioDeviceClientDTO);
        //  String mdn = null;
        KnPersisterTxn persisterTxn = null;
        KnRadioDeviceActResponseDTO response = new KnRadioDeviceActResponseDTO();
        try {
            persisterTxn = KnPersisterTxn.getPersisterTxn();
            persisterTxn.open();
            IActClientXDMServerDAO locationServerDAO = KnActivationFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO();
            // KnSubscriptionKeyInfoPersistDTO subsKeyPersistDTO = locationServerDAO.retreieveSubscriptionKeyInfo(activationKey, persisterTxn);
            KnSubscriptionKeyInfoPersistDTO subsKeyPersistDTO = new KnSubscriptionKeyInfoPersistDTO();
            knLogger.debug(methodName, "DB ServiceName: - ", subsKeyPersistDTO.getServiceName());

            subsKeyPersistDTO.setInputDTO(ipRadioDeviceClientDTO);
           /* mdn = subsKeyPersistDTO.getMdn();
            ipClientRegistryDTO.setMdn(mdn);*/
            // Authorize
            knLogger.debug(methodName, "Invoking Authorization.");
            // 4. authorize the subscriber
            authorizationFwk.authorize(subsKeyPersistDTO);
            knLogger.debug(methodName, "Authorized successfully.");

            //Get subscriber profile not found throw error
            //   KnSubscrInfoDAO subscrInfoDAO = KnActivationFactorySelector.getDAOFactory(KnFactorySelector.DB).createSubscrInfoDAO();
            KnDeviceInfoDAO deviceInfoDAO = KnActivationFactorySelector.getDAOFactory(KnFactorySelector.DB).createDeviceInfoDAO();
            KnDeviceInfoPersistDTO deviceInfoPersistDTO = deviceInfoDAO.getDeviceDetail(ipRadioDeviceClientDTO.getDeviceId(), persisterTxn);
            boolean isExist = true;
            if (deviceInfoPersistDTO == null) {
                deviceInfoPersistDTO = new KnDeviceInfoPersistDTO();
                isExist = false;
            }
            if (!isExist) {
                knLogger.error(methodName, "Radio Device Not Exist :");
                throw new KnActBOException(KnErrorCodes.BOEntity.POC_SUPPORTED_DEVICES_NOT_FOUND, "Row not found exception");
            }
            String pttServerId = System.getenv(DB_MGR_LOCAL_PTTID);
            KnXDMSServiceConfigDTO xdmsServiceConfigDTO = genInfoUtil.retrieveXDMSServiceConfig(pttServerId, persisterTxn);
            String realm = xdmsServiceConfigDTO.getAuthRealm();
            knLogger.debug(methodName, "realm ", realm);
            String password = generalUtil.generateMD5(deviceInfoPersistDTO.getDeviceId().concat(DELIM).concat(realm).concat(DELIM).concat(ipRadioDeviceClientDTO.getDevicePassword()));
            knLogger.debug(methodName, "generated MD5 ", password);
            long millis = System.currentTimeMillis();
            knLogger.debug(methodName, "millis ", millis);
            String actualDeviceId = deviceInfoPersistDTO.getDeviceId();
            deviceInfoPersistDTO.setDeviceId(ipRadioDeviceClientDTO.getDeviceId());
            deviceInfoPersistDTO.setDeviceStatus(KnConstants.DEVICE_ACTIVATION_ACTIVATED);
            deviceInfoPersistDTO.setDeviceActTimeStamp(millis);
            deviceInfoPersistDTO.setDeviceLastUsed(millis);
            deviceInfoPersistDTO.setDeviceDigestPassword(password);
            deviceInfoPersistDTO.setDeviceClientId(ipRadioDeviceClientDTO.getDeviceClientId());
            deviceInfoDAO.updateRadioDeviceInfo(deviceInfoPersistDTO, persisterTxn);
            response.setDeviceId(actualDeviceId);
            response.setDeviceDigestName(actualDeviceId);
            response.setDeviceIMPI(deviceInfoPersistDTO.getDeviceIMPI());
            response.setDeviceShared(String.valueOf(deviceInfoPersistDTO.getDeviceshared()));
            // Save Transaction
            persisterTxn.save();
        } catch (KnActBOException e) {
            knLogger.error(methodName, "RadioDevice Not Exist in Database: ", e);
            rollback(persisterTxn);
            throw new KnActBOException(KnErrorCodes.BOEntity.POC_SUPPORTED_DEVICES_NOT_FOUND, "Row not found exception");
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occured : " + e);
            rollback(persisterTxn);
            if (KnErrorCodes.DAO.INTERNAL_ERROR.equals(e.getErrorCode())) {
                knLogger.error(methodName, "Row not found - Dao Exception : " + e);
                throw new KnActBOException(KnErrorCodes.BOEntity.MESSAGE_PROCESSING_FAILED, "Row not found exception");
            }
        } catch (KnAASException e) {
            knLogger.error(methodName, "BO Exception occured : " + e);
            rollback(persisterTxn);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occured while Radio Device Activation : " + e.getMessage());
            rollback(persisterTxn);
            throw new KnXDMServerSystemException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Unexpected exception occured while " +
                    "Radio Device Activation", e);
        }
        knLogger.info(methodName, "Exit  : ", ipRadioDeviceClientDTO.getDeviceId());
        return response;
    }


    @Override
    public void updateDeviceInfo(String mdn, String clientPassword, KnPersisterTxn persisterTxn) throws KnActBOException {
        final String methodName = "updateDeviceInfo()";
        knLogger.entry(methodName, KnGDPRTemplate.mdn(mdn), clientPassword);
        try {
        	KnSubscrInfoDAO subscrInfoDAO = KnActivationFactorySelector.getDAOFactory(KnFactorySelector.DB).createSubscrInfoDAO();
            KnSubscrInfoDTO subscrInfoDTO = subscrInfoDAO.getSubscrInfo(mdn, persisterTxn);
            String pttServerId = System.getenv(DB_MGR_LOCAL_PTTID);
            long millis = System.currentTimeMillis();
            knLogger.debug(methodName, "millis ", millis);
            boolean isExist = true;
            KnDeviceInfoDAO deviceInfoDAO = KnActivationFactorySelector.getDAOFactory(KnFactorySelector.DB).createDeviceInfoDAO();
            KnDeviceInfoPersistDTO deviceInfoPersistDTO = deviceInfoDAO.getDeviceDetails(mdn, persisterTxn);
            knLogger.debug(methodName, "deviceInfoPersistDTO ", deviceInfoPersistDTO);
            if (deviceInfoPersistDTO == null) {
                deviceInfoPersistDTO = new KnDeviceInfoPersistDTO();
                isExist = false;
            }
            String deviceImpi = deviceInfoPersistDTO.getDeviceIMPI();
            boolean bitEnabled = com.kodiak.common.resources.KnGeneralUtil.getFeatureBitValue(subscrInfoDTO.getSubsFS2(), FEATURE_SET.MCDEVICE.value());
            if(bitEnabled) {
            	deviceInfoPersistDTO.setDeviceType(DEVICE_TYPE.MC_DEVICE.Value());
            }else {
            	deviceInfoPersistDTO.setDeviceType(DEVICE_TYPE.RADIO_NEXT_DEVICE.Value());
            }
            
            deviceInfoPersistDTO.setDeviceId(mdn);
            deviceInfoPersistDTO.setDeviceStatus(KnConstants.DEVICE_ACTIVATION_ACTIVATED);
            deviceInfoPersistDTO.setDeviceActTimeStamp(millis);
            deviceInfoPersistDTO.setDeviceLastUsed(millis);
            deviceInfoPersistDTO.setDeviceDigestPassword(clientPassword);
            deviceInfoPersistDTO.setDeviceIMPI(com.kodiak.xdms.server.common.resources.KnConstants.TEL_URI_TEMPLATE + mdn);
            deviceInfoPersistDTO.setCorpId(subscrInfoDTO.getCorpId());
            IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB)
    				.createXDMServerDAO(pttServerId);
            knLogger.info(methodName, "isExist ", isExist);
            if (isExist) {
                if (deviceImpi != null && !deviceImpi.isEmpty()) {
                    deviceInfoDAO.updateDeviceInfo(deviceInfoPersistDTO, persisterTxn);
                } else {
                    deviceInfoDAO.updateDeviceInfoWithImpi(deviceInfoPersistDTO, persisterTxn);
                }
                com.kodiak.xdms.server.common.dto.persistdat.KnDeviceInfoPersistDTO deletedeviceInfo=new com.kodiak.xdms.server.common.dto.persistdat.KnDeviceInfoPersistDTO();
                deletedeviceInfo.setDeviceId(mdn);
                xdmServerDAO.deleteDeviceImpiInfo(deletedeviceInfo, persisterTxn);
                KnDeviceImpiInfoPersistDTO deviceImpiInfo = new KnDeviceImpiInfoPersistDTO();
				deviceImpiInfo.setDeviceImpi(com.kodiak.xdms.server.common.resources.KnConstants.TEL_URI_TEMPLATE + mdn);
				deviceImpiInfo.setDeviceImpu(com.kodiak.xdms.server.common.resources.KnConstants.TEL_URI_TEMPLATE + mdn);
				xdmServerDAO.createDeviceImpiInfo(deviceImpiInfo, persisterTxn);
				knLogger.info(methodName, "Updated successfully ", deviceInfoPersistDTO);
            } else {
                deviceInfoPersistDTO.setDeviceCreatedAs(com.kodiak.xdms.server.common.resources.KnConstants.IMPLICIT_DEVICE);
                deviceInfoDAO.addDeviceInfo(deviceInfoPersistDTO, persisterTxn);
                KnDeviceImpiInfoPersistDTO deviceImpiInfo = new KnDeviceImpiInfoPersistDTO();
				deviceImpiInfo.setDeviceImpi(com.kodiak.xdms.server.common.resources.KnConstants.TEL_URI_TEMPLATE + mdn);
				deviceImpiInfo.setDeviceImpu(com.kodiak.xdms.server.common.resources.KnConstants.TEL_URI_TEMPLATE + mdn);
				xdmServerDAO.createDeviceImpiInfo(deviceImpiInfo, persisterTxn);
				knLogger.info(methodName, "Added successfully ", deviceInfoPersistDTO);
            }
            
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occured while Subscriber Activation : " + e.getMessage());
            rollback(persisterTxn);
            throw new KnXDMServerSystemException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Unexpected exception occured while " +
                    "Subscriber Activation", e);
        }
    }

    @Override
    public KnDeviceInfoPersistDTO getDeviceInfo(String mdn, KnPersisterTxn persisterTxn) throws KnActBOException {
        final String methodName = "getDeviceInfo()";
        knLogger.entry(methodName, KnGDPRTemplate.mdn(mdn));
        try {
            KnDeviceInfoDAO deviceInfoDAO = KnActivationFactorySelector.getDAOFactory(KnFactorySelector.DB).createDeviceInfoDAO();
            KnDeviceInfoPersistDTO deviceInfoPersistDTO = deviceInfoDAO.getDeviceDetails(mdn, persisterTxn);
            knLogger.debug(methodName, "deviceInfoPersistDTO ", deviceInfoPersistDTO);
            return deviceInfoPersistDTO;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while Subscriber Activation : " + e.getMessage());
            rollback(persisterTxn);
            throw new KnXDMServerSystemException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Unexpected exception occured while " +
                    "Subscriber Activation", e);
        }
    }
}