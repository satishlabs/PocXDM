/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *****************************************************************************
 * File name:   KnSubsProvController.java
 * Subsystem:   Provisioning Library
 * <p>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ravi Shanker .P       12/25/10       7.0
 * <p>
 * <p>
 * #401, 4th Floor, 'Prestige Sigma'
 * No.3, Vittal Mallya Road
 * Bangalore - 560 001
 * www.kodiaknetworks.com
 * All Rights Reserved.
 * <p>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * *******************************************************************************
 */

package com.kodiak.xdms.server.subsmgmt.business.impl;

import com.kodiak.common.commdto.common.*;
import com.kodiak.common.commdto.request.*;
import com.kodiak.common.commdto.response.*;

import com.kodiak.common.ggcache.dto.KnAsyncJobTaskDTO;
import com.kodiak.frameworks.statisticalmgr.KnStatisticsManagerImpl;
import com.kodiak.vault.KnCommonVaultUtil;
import com.kodiak.common.dao.KnPersistenceException;

import com.kodiak.common.dto.KnPayloadIP;

import com.kodiak.utilities.featuresetupgrade.util.KnUpgardeFS;
import com.kodiak.utilities.featuresetupgrade.util.KnUpgardeFSConfig;
import com.kodiak.utilities.kuidgenerator.KnKUIDConstants;
import com.kodiak.utilities.kuidgenerator.KnKUIDGenerator;
import com.kodiak.xdms.server.common.dto.common.KnRecordingTargetInfoDTO;
import com.kodiak.xdms.server.common.dto.persistdat.KnDeviceAddlInfoPersistDTO;
import com.kodiak.xdms.server.common.resources.KnConstants.SUBSCRIBERS_CLIENT_TYPE;
import com.kodiak.common.commdto.response.KnXDMProfileIdMdnMapRespDTO;
import com.kodiak.common.commdto.response.KnXDMSubsAliasDetailsRespDTO;
import com.kodiak.common.commdto.response.KnSysConfigRespDTO;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnDbUtil;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.ggcache.dto.KnDefaultMCSClientInfo;
import com.kodiak.common.resources.*;
import com.kodiak.common.resources.KnConstants.APP_ID;
import com.kodiak.common.resources.KnConstants.TMP_PWD_MODE;
import com.kodiak.ems.base.itf.KnEMSConst;
import com.kodiak.ems.base.utils.KnLicenseInfo;
import com.kodiak.frameworks.config.KnConfigurationLoader;
import com.kodiak.frameworks.confignotifier.watcher.KnConfigWatcherUtil;
import com.kodiak.frameworks.normalizationfw.clientIntf.INormalizeIntf;
import com.kodiak.frameworks.normalizationfw.clientIntf.impl.KnNormalizeImpl;
import com.kodiak.frameworks.normalizationfw.dto.KnDialPlanConfigDTO;
import com.kodiak.frameworks.statisticalmgr.KnOMConstants;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.featureset.KnFeatureSetException;
import com.kodiak.utilities.featureset.KnFeatureSetUtil;
import com.kodiak.utilities.generatealarmutil.KnAlarmConstants;
import com.kodiak.utilities.generatealarmutil.KnAlarmGeneratorUtil;
import com.kodiak.utilities.syncgateway.KnManageSyncUserProfileUtil;
import com.kodiak.xdms.server.common.KnXDMServerException;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dao.KnFactorySelector;
import com.kodiak.xdms.server.common.dao.persister.IXDMServerDAO;
import com.kodiak.xdms.server.common.dao.persister.db.KnDBConnectionException;
import com.kodiak.xdms.server.common.dto.clientdat.KnIPChangeMDNInfoDTO;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDirChgDTO;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDocChgDTO;
import com.kodiak.xdms.server.common.dto.common.*;
import com.kodiak.xdms.server.common.dto.persistdat.KnContactListPersistDTO;
import com.kodiak.xdms.server.common.dto.persistdat.KnDeviceImpiInfoPersistDTO;
import com.kodiak.xdms.server.common.dto.persistdat.KnDeviceInfoPersistDTO;
import com.kodiak.xdms.server.common.framework.KnFWException;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorFramework;
import com.kodiak.xdms.server.common.resources.KnConstants;
import com.kodiak.xdms.server.common.resources.KnConstants.IDM_ACCOUNT_STATUS;
import com.kodiak.xdms.server.common.dto.common.KnDocChangeListDTO;
import com.kodiak.xdms.server.subsmgmt.KnProvException;
import com.kodiak.xdms.server.subsmgmt.business.ISubsProvController;
import com.kodiak.xdms.server.subsmgmt.business.KnProvBOException;
import com.kodiak.xdms.server.subsmgmt.business.helper.KnProvInfoUtil;
import com.kodiak.xdms.server.subsmgmt.business.helper.KnProvSMSUtil;
import com.kodiak.xdms.server.subsmgmt.dao.KnProvFactorySelector;
import com.kodiak.xdms.server.subsmgmt.dao.persister.IProvXDMServerDAO;
import com.kodiak.xdms.server.subsmgmt.dao.persister.db.tables.KnProvTablesRegistry;
import com.kodiak.xdms.server.subsmgmt.dao.persister.db.tables.xdm.KnDeviceInfoDAO;
import com.kodiak.xdms.server.subsmgmt.dao.persister.db.tables.xdm.KnSubsCameraInfoDAO;
import com.kodiak.xdms.server.subsmgmt.dao.persister.db.tables.xdm.KnSubscrPTTRadioGrpListDocDAO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.*;
import com.kodiak.xdms.server.subsmgmt.dto.common.*;
import com.kodiak.xdms.server.subsmgmt.dto.common.KnSubsProfileDTO;
import com.kodiak.xdms.server.subsmgmt.dto.persistdat.*;
import com.kodiak.xdms.server.subsmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvConstants;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvUtil;
import com.kodiak.xdms.server.subsmgmt.resources.KnSubConfigConstants;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.io.FileInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static com.kodiak.common.resources.KnConstants.*;
import static com.kodiak.common.resources.KnConstants.CHANGE_SERVICE_AUTH_STATUS;
import static com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME;
import static com.kodiak.common.resources.KnConstants.DEFAULT_EMERGENCY_TIMER;
import static com.kodiak.common.resources.KnConstants.FEATURE_SET.HTTPSSUPPORT;
import static com.kodiak.common.resources.KnConstants.MCSXCAP_XCAP_ROOT_CONTEXT;
import static com.kodiak.common.resources.KnConstants.PROTOCOL_VERSION_27;
import static com.kodiak.common.resources.KnGeneralUtil.calculateActiveFeatureSetBasedOnPv;
import static com.kodiak.xdms.server.common.resources.KnConstants.*;
import static com.kodiak.xdms.server.subsmgmt.resources.KnProvConstants.ZERO;
import static com.kodiak.xdms.server.subsmgmt.resources.KnProvConstants.ONE;
import static com.kodiak.xdms.server.subsmgmt.resources.KnProvConstants.TWO;
import static com.kodiak.xdms.server.subsmgmt.resources.KnProvConstants.NOTIFY_ON_ANY_MDN;
import static com.kodiak.xdms.server.subsmgmt.resources.KnSubConfigConstants.POCHOME_AUTOASSIGN_FLAG;

public class KnSubsProvController implements ISubsProvController {
    private static final KnLogger knLogger = KnLogger.getLogger(KnSubsProvController.class);
    private KnConfigWatcherUtil configWatcherUtil = KnConfigWatcherUtil.getInstance();

    private String xdmPttServerId;
    private KnGenInfoUtil genInfoUtil;
    private KnProvInfoUtil provInfoUtil;
    private KnValidatorFramework validatorFwk;
    private KnLicenseInfo licenseInfo;
    private KnFeatureSetUtil featureSetUtil;
    private KnProvSMSUtil provSMSUtil;
    private int changeMdnSeq = 1;
    private KnJWTUtil jwtUtil;
    private int jwtexpiryTime;
    private KnGeneralCacheUtil generalCacheUtil;
    private KnGeneralPasswordUtil pwdUtil;
    private KnEncryptionDecryptionUtil encryptionDecryptionUtil;
    private KnUpgardeFS knUpgardeFS;
    private String currentFsVersion;
    private KnProvUtil provUtil;

    public KnSubsProvController() {
        final String methodName = "Constructor";
        knLogger.info(methodName, "in subs prov controller1");
        provInfoUtil = new KnProvInfoUtil();
        provUtil=new KnProvUtil();
        genInfoUtil = KnGenInfoUtil.getInstance();
        validatorFwk = KnValidatorFramework.getInstance(KnProvConstants.LIBRARY_NAME);
        licenseInfo = KnEMSConst.getLicenseInfo();
        featureSetUtil = KnFeatureSetUtil.getInstance();
        provSMSUtil = new KnProvSMSUtil();
        //register KnProvInfoUtil calss object to config notifier
        List<Observer> observers = new ArrayList<>();
        observers.add(provInfoUtil);
        //register KnGenInfoUtil class object
        observers.add(genInfoUtil );
        configWatcherUtil.addObservers(observers);
        generalCacheUtil = KnGeneralCacheUtil.getInstance();
        pwdUtil = KnGeneralPasswordUtil.getInstance();
        encryptionDecryptionUtil = KnEncryptionDecryptionUtil.getInstance();
        knUpgardeFS = KnUpgardeFS.getInstance();
        try {
            Properties prop = new Properties();
            InputStream input = new FileInputStream(AUTH_KEY_PROPS_FILE_NAME);
            prop.load(input);
            String secretKey = KnCommonVaultUtil.getKeyFromVault(JWT_PRESHARED_NAME_PATH, JWT_PRESHARED_KEY);
            jwtexpiryTime = Integer.parseInt(prop.getProperty(AUTHSERVER_JWT_EXPIRY_TIMEOUT));
            knLogger.info(methodName, "Secret key read is ", secretKey);
            jwtUtil = new KnJWTUtil(secretKey);
            knLogger.info(methodName, "Initialized KnJWTUtil- ", jwtUtil);
            currentFsVersion= KnUpgardeFSConfig.getFsCurrentVersion();
        } catch (Exception e) {
            knLogger.error(methodName, "Failed to initialize KnJWTUtil", e);
        }
    }

    /**
     * This method will create the Subscriber Profile for the received Input Parameters
     * This method will perform the following activities
     * -> Authenticate the User.
     * -> Validate the received input DTO data.
     * -> populate the subscriber info persistdat DTO's
     * -> Invoke the DAO Layer for DB Population
     *
     * @param subsProvInputDTO KnIPSubsProfileInputDTO
     * @return KnProvRespDTO Object
     */
    public KnOPCreateSubsInfoDTO createSubscriber(KnIPSubsProvInfoDTO subsProvInputDTO, KnPersisterTxn persisterTxn) throws KnProvBOException, KnValidationException {
        String methodName = "createSubscriber(KnSubsProfilePersistDTO, KnPersisterTxn)";
        KnSubsProfilePersistDTO subsProvPersistDTO;
        boolean ownedTxn = false;
        KnOPCreateSubsInfoDTO respDTO = new KnOPCreateSubsInfoDTO();
        knLogger.info(methodName, "ENTRY: , Txn - ", persisterTxn);
        String xdmPttServerId;
        Collection<Integer> successPegs = new ArrayList<Integer>();
        List<Integer> clientTypeList = new ArrayList<Integer>();
        KnExtSubsPersistDTO extSubsPersistResponseDTO;
        BitSet bitset = new BitSet();
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening transaction ");
                persisterTxn.open();
                ownedTxn = true;
            }

            try {
                xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
            } catch (KnBOException e) {
                knLogger.error(methodName, "failed to retrieve xdm Ptt Sever Id");
                knLogger.error(methodName, e);
                if (ownedTxn) {
                    rollback(persisterTxn);

                }
                throw new KnProvBOException(KnErrorCodes.BOEntity.XDMS_PTT_ID_NOT_FOUND, "Failed to retrieve XDM PTT Server ID", e);
            }
            if(null!=subsProvInputDTO.getExtGatewayId()) {
                if (!provInfoUtil.validateExtGatewayId(subsProvInputDTO.getExtGatewayId(),persisterTxn)) {
                    knLogger.error(methodName, "External GatewayId does not exist : ", subsProvInputDTO.getExtGatewayId());
                    throw new KnProvBOException(KnErrorCodes.BOEntity.EXTERNAL_GATEWAY_ID_NOT_EXIST, "Invalid External Gateway Id passed");
                }
            }
            String mdn = subsProvInputDTO.getMdn();
            String ufmi=subsProvInputDTO.getUfmi();
            Integer subsClientType = subsProvInputDTO.getSubsClientType();
            String userId = subsProvInputDTO.getUserId();
            
            if(ufmi !=null) {
                KnOPSubsProfileInfoDTO subsProfileInfoDTO = provInfoUtil.retrieveSubscriberInfoForUFMI(ufmi, persisterTxn);
                if(subsProfileInfoDTO!=null){
                    throw new KnProvBOException(KnErrorCodes.BOEntity.DUPLICATE_UFMI, "UFMI already Exists");
                }
            }
			String tierPackageId = null;
			Map<String, Integer> addonPackageIds = new HashMap<>();
			Map<String, Integer> addPkgIds = new HashMap<String, Integer>();
			if (subsProvInputDTO.getPkgIdMap() != null
					&& subsProvInputDTO.getPkgIdMap().get(KnConstants.ADD_ACTION) != null) {
				addPkgIds = subsProvInputDTO.getPkgIdMap().get(KnConstants.ADD_ACTION);
				for (Entry<String, Integer> entry : addPkgIds.entrySet()) {
					if (entry.getValue().intValue() == KnConstants.TIER_PKG_TYPE.intValue()) {
						tierPackageId = entry.getKey();
					} else if (entry.getValue().intValue() == KnConstants.ADDON_PKG_TYPE.intValue()) {
						addonPackageIds.put(entry.getKey(), entry.getValue());
					}
				}
			}
			knLogger.info(methodName, "addPkgIds map " ,addPkgIds);
            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();

            //check if the mdn is present in pseudoNoPool and PamAccountInfo table
            boolean isMdnPresentInPamAccInfoTable = provXDMServerDAO.isMdnPresentInPamAccInfo(mdn, persisterTxn);
            if (isMdnPresentInPamAccInfoTable) {
                knLogger.error(methodName, "Mdn is present in PamAccountInfo");
                throw new KnProvBOException(KnErrorCodes.BOEntity.MDN_PRESENT_IN_PAMACCOUNTINFO,
                        "Mdn is present in PamAccountInfo");
            }

            KnOPSubsProfileInfoDTO subsProfile = null;
			try {
				subsProfile = provXDMServerDAO.selectSubscriberProfileByAliasMdn(mdn, persisterTxn);
			} catch (KnDAOException e) {
				knLogger.error(methodName, e);
			}
			if (subsProfile != null) {
				knLogger.error(methodName, "Mdn is present as aliasMdn");
				throw new KnProvBOException(KnErrorCodes.BOEntity.MDN_PRESENT_AS_ALIASMDN,
						"Mdn is present as aliasMdn");
			}

            if(subsProvInputDTO.getAliasInfoList() != null){
                List<KnSubsAliasInfoDTO> alisasInfoList=subsProvInputDTO.getAliasInfoList();
                Map<String, String> aliasIdIssuerMap= new HashMap<>();
                for(KnSubsAliasInfoDTO aliasInfoDTO: alisasInfoList){
                    aliasIdIssuerMap.put(aliasInfoDTO.getAliasId(),aliasInfoDTO.getAliasIdIssuer());
                }
                Map<String, List<KnSubsAliasInfoDTO>> subsAliasIdInfoMap = provXDMServerDAO.selectSubsAliasIdInfo(aliasIdIssuerMap,false, persisterTxn);
                if(subsAliasIdInfoMap!=null && !subsAliasIdInfoMap.isEmpty()){
                    throw new KnProvBOException(KnErrorCodes.BOEntity.DUPLICATE_ALIAS_INFO_NOT_ALLOWED, "Duplicate info present");
                }
            }

            //Authenticate the request
            knLogger.debug(methodName, "No Authorisation is being performed");

            //populate the Subscriber Prov Persist DTO.
            subsProvPersistDTO = new KnSubsProfilePersistDTO();
            subsProvPersistDTO.setInputDTO(subsProvInputDTO);
            subsProvPersistDTO.setHierarchyType(subsProvInputDTO.getHierarchyType());
            subsProvPersistDTO.setMdn(mdn);
            subsProvPersistDTO.setXDMSHome(xdmPttServerId);
            subsProvPersistDTO.setIMEI(subsProvInputDTO.getIMEI());
            subsProvPersistDTO.setProvFSMap(subsProvInputDTO.getProvFSMap());
            subsProvPersistDTO.setFirstNetIndicator(subsProvInputDTO.getFirstNetIndicator());
            subsProvPersistDTO.setExtGatewayId(subsProvInputDTO.getExtGatewayId());
            String networkName = subsProvInputDTO.getNetworkName();
            knLogger.debug(methodName,"subsProvInputDTO :",subsProvInputDTO);

            Map<String, String> paramNameValueMap = genInfoUtil.retrieveRTXConfigValues(persisterTxn);
            if (null == networkName || networkName.isEmpty()) {
                // as per FRS
                if (KnProvConstants.SUBS_CLIENT_TYPE.POC_NNI_Group_MDN.value() == subsClientType)
                    networkName=paramNameValueMap.get(KnConstants.XDMS_LMR_SUB_DEFAULT_NAME);
                else
                    networkName = mdn;
            }

            knLogger.debug(methodName,"network name :: ", KnGDPRTemplate.name(networkName));

            subsProvPersistDTO.setNetworkName(networkName);

            knLogger.debug(methodName, "subsProvPersistDTO :: ", subsProvPersistDTO );

            knLogger.debug(methodName,"subsClientType :",subsClientType);
            knLogger.debug(methodName,"subsProvInputDTO :",subsProvInputDTO);

            //9.0 onwards as per User check, discreet and ambient listening feature new params serviced_status_op/authUser are introduced
            //these new param need to set there default values
            //Default value for service status op - provisioned (0)
            // service auth user - activated(2)

            int serviceStatusOp = 0;
            int serviceStatusAuthUser = 2;
            KnDefaultMCSClientInfo defaultMCSClientInfo=null;

            int clusterId = Integer.parseInt(System.getenv(CLUSTERID_ENV_NAME));
            Map<String, String> microServicesParamNameValueMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
            String pochomeAutoAssignValue = microServicesParamNameValueMap.get(POCHOME_AUTOASSIGN_FLAG);
            boolean pochomeAutoAssignFlag = true;
            if (pochomeAutoAssignValue != null) {
                try {
                    pochomeAutoAssignFlag = Integer.parseInt(pochomeAutoAssignValue) == ENABLED;
                } catch (NumberFormatException e) {
                    knLogger.warn(methodName, "Invalid pochomeAutoAssignFlag: ", pochomeAutoAssignValue);
                }
            }
            knLogger.debug(methodName, "pochomeAutoAssignFlag value: ", pochomeAutoAssignFlag);
            //serviceauth status set to ACTIVATED for subscriber 7,8,17 & 18
            if (KnProvConstants.SUBS_CLIENT_TYPE.POC_NNI_Alias_MDN.value() == subsClientType ||
                    KnProvConstants.SUBS_CLIENT_TYPE.POC_NNI_Group_MDN.value() == subsClientType
                    || subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.SGMDNPATCH.value() || subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.DATAGROUPMDN.value() ||
                    subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.STANDALONECAMERA.value()) {
                // subsProvPersistDTO.setServiceAuthStatus(KnConstants.SERVICE_AUTH_STATUS.ACTIVATED.value());
                //SERVICE_STATUS_OP for client type 7, 8 , 17 , 18 & 19 is set to ACTIVATED
                serviceStatusOp = KnConstants.SERVICE_STATUS_OP.ACTIVATED.value();
                subsProvPersistDTO.setServiceStatusOp(serviceStatusOp);
            }else {
                //while subscriber is created the service auth status is always set to Provisioned - 0 for subscriber other than 7 & 8
                // 9.0 onwards service auth status is set based on service_status_op and service_status_authuser value
                //  subsProvPersistDTO.setServiceAuthStatus(KnConstants.SERVICE_AUTH_STATUS.PROVISIONED.value());
            	if (KnConstants.MCSCOMPLIANCE == subsProvInputDTO.getMcsCompliance()
						&& (subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.POC_WIFIONLY.value()
								|| subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.CROSS_CARRIER_PTT_CLIENT.value())) {
					serviceStatusOp = KnConstants.SERVICE_STATUS_OP.ACTIVATED.value();
	                subsProvPersistDTO.setServiceStatusOp(serviceStatusOp);
	                defaultMCSClientInfo = KnGeneralCacheUtil.getInstance().retrieveDefaultMCSClientInfo();
				} else {
                serviceStatusOp = KnConstants.SERVICE_STATUS_OP.PROVISIONED.value();
                subsProvPersistDTO.setServiceStatusOp(serviceStatusOp);
            }
            }

            serviceStatusAuthUser = KnConstants.SERVICE_STATUS_AUTHUSER.ACTIVATED.value();
            subsProvPersistDTO.setServiceStatusAuthUser(serviceStatusAuthUser);
            //calculating servicece auuth status as per new rule User check ambient feature
            //setting service auth status

            if (pochomeAutoAssignFlag){
                subsProvPersistDTO.setServiceAuthStatus(genInfoUtil.calculateServiceAuthStatus(serviceStatusOp, serviceStatusAuthUser));
            }
            else {
                subsProvPersistDTO.setServiceAuthStatus(KnConstants.SERVICE_AUTH_STATUS.PRE_PROVISIONED.value());
            }
            subsProvPersistDTO.setPreviousServiceAuthStatusToStore(subsProvPersistDTO.getServiceAuthStatus());
            int publicSubscriptionType = subsProvInputDTO.getPublicSubscriptionType();
            subsProvPersistDTO.setPublicSubscriptionType(publicSubscriptionType);
            int corporateSubscriptionType = subsProvInputDTO.getCorporateSubscriptionType();
            subsProvPersistDTO.setCorporateSubscriptionType(corporateSubscriptionType);
            //Deprecate of Existing pairing indicator logic because of new logic
            /*boolean pairingIndicator = false;
            if (subsProvInputDTO.getPairingInd() != null) {
                pairingIndicator = subsProvInputDTO.getPairingInd();
            }*/

            if (corporateSubscriptionType == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
                String extCorpId = subsProvInputDTO.getExtCorpId();
                if (extCorpId == null) {
                    throw new KnProvBOException(KnErrorCodes.BOEntity.EXTCORPID_CANNOT_BE_NULL_FOR_CORP_SUBS,
                            "Ext Corp ID cannot be null for a corporate subscriber");
                }
                subsProvPersistDTO.setExtCorpId(extCorpId);
            }

            subsProvPersistDTO.setAccountId(subsProvInputDTO.getAccountId());
            subsProvPersistDTO.setAffiliateId(subsProvInputDTO.getAffiliateId());
            subsProvPersistDTO.setPayType(subsProvInputDTO.getPayType());
            subsProvPersistDTO.setRoamingTypes(subsProvInputDTO.getRoamingTypes());




            //Deprecation of Existing pairing indicator logic because of new logic
            /*if (subsClientType != KnProvConstants.SUBS_CLIENT_TYPE.DISPATCH_CLIENT.value()) {
                subsProvPersistDTO.setPairingInd(pairingIndicator);
            }*/

            int pocDonorRadioSupport = genInfoUtil.retrieveXDMSServiceConfig(xdmPttServerId, persisterTxn).getEnablePocDonorRadioSupport();
            if (pocDonorRadioSupport == KnProvConstants.POC_DONOR_RADIO.DISABLED.value() &&
                    subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.POC_DONOR_RADIO.value()) {
                knLogger.error(methodName, "POC_DONOR_RADIO is disabled");
                throw new KnProvBOException(KnErrorCodes.BOEntity.POC_DONOR_RADIO_DISABLED,
                        "Poc Donor Radio is disabled");
            }
            int thirdPartyPocClientSupport = genInfoUtil.retrieveXDMSServiceConfig(xdmPttServerId, persisterTxn).getEnable3rdPartyPocClientSupport();
            if (thirdPartyPocClientSupport == KnProvConstants.THIRD_PARTY_POC_CLIENT.DISABLED.value() &&
                    subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.THIRDPARTYPOCCLIENT.value()) {
                knLogger.error(methodName, "3rd Party Poc Client is disabled");
                throw new KnProvBOException(KnErrorCodes.BOEntity.THIRD_PARTY_POC_CLIENT_DISABLED,
                        "3rd Party Poc Client is disabled");
            }

            int iDenInterOp = genInfoUtil.retrieveXDMSServiceConfig(xdmPttServerId, persisterTxn).getiDenInterOp();
            if (iDenInterOp == KnProvConstants.IDEN_INTEROP.DISABLED.value() && ufmi!=null) {
                knLogger.error(methodName, "IDenInterOp is disabled");
                throw new KnProvBOException(KnErrorCodes.BOEntity.IDEN_INTEROP_DISABLED,"iDen InterOp is disabled");
            }
            //Deprecation of Existing pairing indicator logic because of new logic
            /*if (pairingIndicator && (subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.POC_DONOR_RADIO.value())) {
                knLogger.error(methodName, "Pairing Indicator should not enabled for POC Donor radio client type");
                throw new KnProvBOException(KnErrorCodes.BOEntity.PAIRING_IND_NOT_ALLOWED_POC_DONOR_RADIO,
                        "Pairing Indicator should not enabled for POC Donor radio client type");
            }*/
            subsProvPersistDTO.setUfmi(subsProvInputDTO.getUfmi());
            subsProvPersistDTO.setiDenUserName(subsProvInputDTO.getiDenUserName());
            subsProvPersistDTO.setiDenPassword(subsProvInputDTO.getiDenPassword());
            subsProvPersistDTO.setiDenBusUnitId(subsProvInputDTO.getiDenBusUnitId());
            subsProvPersistDTO.setSubsClientType(subsClientType);
            subsProvPersistDTO.setEmailAddress(subsProvInputDTO.getEmailAddress());
            subsProvPersistDTO.setSubsDefPttRadio(subsProvInputDTO.getSubsDefPttRadio());
            //Converged Client  changes starts
            int corpId = -1;
            KnOPCorpProfileInfoDTO corpProfileInfoDTO = null;
            String extCorpId = subsProvInputDTO.getExtCorpId();
            String poCPttServerId = null;
            boolean updateCorpHome = false;
            String presencePttServerId = null;
            if (corporateSubscriptionType == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
                knLogger.debug(methodName, "verifying if corporate profile already exists");
                corpProfileInfoDTO = provXDMServerDAO.retrieveCorporateProfile(extCorpId, false, persisterTxn);
                corpId = corpProfileInfoDTO.getCorpId();
                knLogger.debug(methodName, "Corporate Id Retrieved - ", corpId);
                corpProfileInfoDTO.setExtCorpId(extCorpId);
                if (pochomeAutoAssignFlag){
                    Map<String, String> corpAnchorMap = provInfoUtil.validateAndgetCorpAnchorPocHome(mdn, corpProfileInfoDTO, persisterTxn);
                    poCPttServerId = corpAnchorMap.get(KnProvConstants.POC_PTT_ID_KEY);
                    updateCorpHome = Boolean.parseBoolean(corpAnchorMap.get(KnProvConstants.UPDATE_CORP_HOME));
                    knLogger.debug(methodName, "poCPttServerId - ", poCPttServerId, " updateCorpHome: ", updateCorpHome);
                }

                if(subsProvPersistDTO.getSubsDefPttRadio() != com.kodiak.common.resources.KnConstants.SUBSCR_DEF_PTTRADIO_ENABLED ){
                 knLogger.debug(methodName, "SUBSCR_DEF_PTTRADIO is disabled proceeding to check system & corp level ");
                 int subsDef=genInfoUtil.retrieveXDMSServiceConfig(xdmPttServerId, persisterTxn).getSubsDefPttRadio();
                 //check system level SUBSCR_DEF_PTTRADIO value
                 if(subsDef == com.kodiak.common.resources.KnConstants.SUBSCR_DEF_PTTRADIO_ENABLED){
                	 knLogger.debug(methodName, "system level SUBSCR_DEF_PTTRADIO is enabled skipping corp level check");
                	 subsProvPersistDTO.setSubsDefPttRadio(subsDef);
                 }
                 else if(corpProfileInfoDTO.getCorpId() > 0 && corpProfileInfoDTO.getSubsDefPttRadio() == com.kodiak.common.resources.KnConstants.SUBSCR_DEF_PTTRADIO_ENABLED){
                	 knLogger.debug(methodName, "corp level SUBSCR_DEF_PTTRADIO is enabled");
                	 subsProvPersistDTO.setSubsDefPttRadio(com.kodiak.common.resources.KnConstants.SUBSCR_DEF_PTTRADIO_ENABLED);
                 }
                }
                if(subsProvPersistDTO.getSubsDefPttRadio() == com.kodiak.common.resources.KnConstants.SUBSCR_DEF_PTTRADIO_ENABLED ){

                	knLogger.debug(methodName, "Converting regular client type ",subsProvPersistDTO.getSubsClientType()," to  PTTRADIO Client type");
                	switch(subsProvPersistDTO.getSubsClientType()){
                	case 1:
                		subsClientType=KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value();
                		break;
                	case 5:
                		subsClientType=KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value();
                		break;
                	case 10:
                		subsClientType=KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value();
                		break;
                	default:
                		break;
                	}
                }
            }
            //Converged Client  changes ends
            subsProvPersistDTO.setDispatchGroupMember(KnProvConstants.DISPATCH_GROUP_MEMBER.NOT_MEMBER.value());
            subsProvPersistDTO.setSubsClientType(subsClientType);
            subsProvPersistDTO.setLicenseType(subsProvInputDTO.getLicenseType());
            //Validate the Persist DTO
            validatorFwk.validate(subsProvPersistDTO);
            knLogger.debug(methodName, "Successfully validated the data");
            String deviceSharingFlag = microServicesParamNameValueMap.get(DEVICE_SHARING_FEATURE_FLAG);
            knLogger.info(methodName, "deviceSharingFlag",  deviceSharingFlag);
            int deviceSharewifiFlag=Integer.parseInt(microServicesParamNameValueMap.get(AUTO_DEVICESHARE_WIFI_CC));
            int licenseType = 0;
            knLogger.info(methodName, "deviceSharewifiFlag-->",  deviceSharewifiFlag);
			if ((subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.POC_WIFIONLY.value()
					|| subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.CROSS_CARRIER_PTT_CLIENT.value()
					|| subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value()
					|| subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value()) && Integer.parseInt(deviceSharingFlag) == ENABLED
                    && deviceSharewifiFlag == ENABLED){
				knLogger.debug(methodName, "license type for wifi , crosscarrier and its ptt ");
				subsProvPersistDTO.setLicenseType(KnConstants.USER_LICENSE_TYPE);
			}else{
                knLogger.debug("setting the else val"+subsProvInputDTO.getLicenseType());
                subsProvPersistDTO.setLicenseType(subsProvInputDTO.getLicenseType());
        }

            licenseType = subsProvPersistDTO.getLicenseType();
			knLogger.debug(methodName,"licenseType -",licenseType);
            //validate the subscription Types.
            if (publicSubscriptionType == KnConstants.PUBLIC_SUBSCRIPTION_TYPE.NONE.value() &&
                    corporateSubscriptionType == KnConstants.CORP_SUBSCRIPTION_TYPE.NONE.value()) {
                knLogger.error(methodName, "Either Public or Corporate Subscription Types are required to be enabled");
                throw new KnProvBOException(KnErrorCodes.BOEntity.INVALID_SUBSCRIPTION_TYPE, "Invalid public or Corp Subscription types passed");
            }

            if (publicSubscriptionType == -1 && corporateSubscriptionType == -1) {
                knLogger.error(methodName, "Either Public or Corporate Subscription Types are required to be enabled");
                throw new KnProvBOException(KnErrorCodes.BOEntity.INVALID_SUBSCRIPTION_TYPE, "Invalid public and Corp Subscription types passed");
            }

            //validate if dispatch client is set for Public Subscription Type
            if (corporateSubscriptionType == KnConstants.CORP_SUBSCRIPTION_TYPE.NONE.value()) {
                if (subsClientType != KnProvConstants.SUBS_CLIENT_TYPE.HANDSET.value()) {
                    knLogger.error(methodName, "Invalid client type for  public subscriber");
                    throw new KnProvBOException(KnErrorCodes.BOEntity.INVALID_CLIENT_TYPE_FOR_PUBLIC_SUBS,
                            "Invalid client type set for public subscriber");
                }

            }

            IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);


            ArrayList<String> mdnList = new ArrayList<>();
            mdnList.add(mdn);

            knLogger.debug(methodName, "Getting  External subscriber Profile");
            extSubsPersistResponseDTO = provXDMServerDAO.getExtSubscriberInfo(mdnList, persisterTxn);
            if (!(extSubsPersistResponseDTO.getExtSubs().isEmpty())) {
                knLogger.error(methodName, "Subscriber Already Present As External Subscriber");
                throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_ALREADY_EXISTS_AS_EXT_SUBS, "Subscriber Already exist as External Subscriber");
            }


            String licenseNoOfSubsCount = licenseInfo.getNoOfSubs();
            int totalSubsLimit = Integer.parseInt(licenseNoOfSubsCount);
            knLogger.debug(methodName, "Max subs count for license - ", totalSubsLimit);
            int subscriberCount = provXDMServerDAO.retrieveSubscriberCount(persisterTxn);
            if ((subscriberCount + 1) > totalSubsLimit) {
                knLogger.error(methodName, "Max subscriber limit reached for license ");
                throw new KnProvBOException(KnErrorCodes.BOEntity.MAX_SUBS_LIMIT_REACHED_FOR_LICENSE, "Max Subscriber limit reached for license");
            }

            // changes for Corp Account Anchoring
            if (!pochomeAutoAssignFlag) {
                knLogger.debug(methodName, "pochomeAutoAssignFlag is disabled");
                poCPttServerId = String.valueOf(0);
                presencePttServerId = String.valueOf(0);
            } else {
                knLogger.debug(methodName, "pochomeAutoAssignFlag is enabled,BAU");
                if (poCPttServerId == null) {
                    poCPttServerId = provInfoUtil.getSubsPoCHome(mdn, persisterTxn);
                }
                presencePttServerId = provInfoUtil.getSubsPresenceHome(mdn, poCPttServerId, persisterTxn);
            }

            knLogger.info(methodName, "presencePttServerId :", presencePttServerId);
            subsProvPersistDTO.setPoCHome(poCPttServerId);
            subsProvPersistDTO.setPresenceHome(presencePttServerId);

            //validate the received Feature set with the License.
            //check if the received subsFeatureSet1 is null or 0 then apply the default subsFeatureSet1.

            String corpFS2 = null;
            String opsCorpFS2 = null;
            int existingPairInd = -1;
            if (corporateSubscriptionType == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
                // -->. creating Corporate Profile
                knLogger.debug(methodName, "verifying if corporate profile already exists");
                if (corpProfileInfoDTO == null) {
                    corpProfileInfoDTO = provXDMServerDAO.retrieveCorporateProfile(extCorpId, false, persisterTxn);
                }
                corpId = corpProfileInfoDTO.getCorpId();
                knLogger.debug(methodName, "Corporate Id Retrieved - ", corpId);

                int svcDispatchType = genInfoUtil.retrieveXDMSServiceConfig(xdmPttServerId, persisterTxn).getWebDispatchEnabled();
                int corpDispatchType = corpProfileInfoDTO.getWebDispatchEnabled();
                knLogger.debug(methodName, "svcDispatchType & corpDispatchType - ", svcDispatchType, corpDispatchType);

                if (corpId > 0) {
                    knLogger.debug(methodName, "corporate profile already exists with corp id [", corpId, "] ", "for extCorpId ", extCorpId);
                    existingPairInd = corpProfileInfoDTO.getPairedContactListId();

                    //Deprecation of Existing pairing indicator logic because of new logic
                    /*knLogger.debug(methodName, "pairingIndicator:", pairingIndicator, " existingPairInd:", existingPairInd, " corpId:", corpId);
                    if ((pairingIndicator ^ (existingPairInd != 0))) {
                    	knLogger.error(methodName, "Mismatch in pairing indicator for the provided corporation ");
                        throw new KnProvBOException(KnErrorCodes.BOEntity.MISMATCH_IN_PAIRING_IND,
                                "Mismatch in pairing indicator for the provided corporation");
                    }*/

                    if (existingPairInd > 0) {
                        paramNameValueMap = genInfoUtil.retrieveRTXConfigValues(persisterTxn);
                        knLogger.debug(methodName, "param value map", paramNameValueMap);
                        int corpAutoPairingCnt = Integer.valueOf(paramNameValueMap.get(KnConstants.CORP_AUTO_PAIRING_SIZE));
                        int corpSubsCount = provInfoUtil.retrieveCorpSubsCount(corpProfileInfoDTO.getCorpId(), persisterTxn);
                        knLogger.info(methodName, "existing subs count - ", corpSubsCount, "autopair limit - ", corpAutoPairingCnt);
                        if (corpSubsCount >= corpAutoPairingCnt) {
                            // to disable auto pairing
                            respDTO.setCorpAutoPairing(false);
                            respDTO.setIsOldCorp(true);
                        } else {
                            // to enable auto pairing
                            if (KnProvConstants.SUBS_CLIENT_TYPE.HANDSET.value() == subsClientType) {
                                respDTO.setCorpAutoPairing(true);
                                respDTO.setIsOldCorp(true);
                            }
                        }
                    }

                    Collections.addAll(clientTypeList, com.kodiak.common.resources.KnConstants.SUBS_CLIENT_TYPE.ALIASMDN.value(),
                            com.kodiak.common.resources.KnConstants.SUBS_CLIENT_TYPE.GROUPMDN.value(), com.kodiak.common.resources.KnConstants.SUBS_CLIENT_TYPE.SGMDNPATCH.value());
                    long corpProfileUpdateTime = Calendar.getInstance().getTimeInMillis();
                    KnCorpProfilePersistDTO corpProfilePersistDTO = populateCorpProfileInfoDTO(xdmPttServerId, subsProvPersistDTO, persisterTxn);
                    if (subsProvInputDTO.getCorporateName() == null) {
                        corpProfilePersistDTO.setCorporateName(corpProfileInfoDTO.getCorporateName());
                    } else {
                        corpProfilePersistDTO.setCorporateName(subsProvInputDTO.getCorporateName());
                    }
                    // update the Corp home if poc is full and subscriber moving to new poc (for load based partioning)
                    if (updateCorpHome) {
                        // checks if count for su/sg client is greater than 0 then we will go for alarm generation.
                        if (provXDMServerDAO.getSubsCountOfClientTypeForCorp(corpId, clientTypeList, persisterTxn) > 0) {
                            String serviceName = extCorpId + ":" + corpProfileInfoDTO.getPocHome() + ":" + poCPttServerId;
                            knLogger.info(methodName, "serviceName", serviceName, "Alarm Id 17608");
                            KnAlarmGeneratorUtil.generateAlarm2(KnAlarmConstants.ALARM_CORPORATE_POC_HOME_RESET, KnAlarmConstants.SEVERITY_MAJOR, serviceName);
                        }
                        knLogger.info(methodName, "update new poc home for corp ");
                        corpProfilePersistDTO.setPocHome(poCPttServerId);
                    } else {
                        //dont update poc home
                        corpProfilePersistDTO.setPocHome(null);
                    }
                    corpProfilePersistDTO.setLastProfileUpdateTime(corpProfileUpdateTime);
                    corpProfilePersistDTO.setCorpId(corpId);
                    provXDMServerDAO.updateCorporateProfile(corpProfilePersistDTO, persisterTxn);
                    subsProvPersistDTO.setCorpId(corpId);
                    opsCorpFS2 = corpProfileInfoDTO.getOpsCorpFS2();
                    corpFS2 = corpProfileInfoDTO.getCorpFS2();

                    if((KnProvConstants.SUBS_CLIENT_TYPE.DISPATCH_CLIENT.value() == subsClientType) &&
                            (KnProvConstants.WEB_DISPATCH_ENABLED == svcDispatchType || KnProvConstants.WEB_DISPATCH_ENABLED == corpDispatchType)){
                        subsProvPersistDTO.setDispatchType(KnProvConstants.WEB_DISPATCH_ENABLED);
                    }

                } else {
                    knLogger.debug(methodName, "creating the corporate Profile");

                    paramNameValueMap = genInfoUtil.retrieveRTXConfigValues(persisterTxn);
                    knLogger.debug(methodName, "param value map", paramNameValueMap);
                    int enbaleAutoPair = Integer.valueOf(paramNameValueMap.get(KnConstants.ENABLE_CORP_AUTO_PAIRING));
                    if (enbaleAutoPair == KnProvConstants.AUTO_PAIR_ENABLED && subsProvInputDTO.isAutoPair()) {
                        knLogger.info(methodName, "System flag corp auto pairing is enabled and CBE is also configured.");
                        if (KnProvConstants.SUBS_CLIENT_TYPE.HANDSET.value() == subsClientType) {
                            // to enable auto pairing
                            respDTO.setCorpAutoPairing(true);
                            respDTO.setIsOldCorp(false);
                        }
                    }

                    //get final CorpFeatureSet
                    corpFS2 = featureSetUtil.getDefFinalCorpFS();
                    knLogger.debug(methodName, "final corpFS :", corpFS2);

                    //get final OpsCorpFeatureSet
                    opsCorpFS2 = featureSetUtil.getDefFinalOpsCorpFS();
                    knLogger.debug(methodName, "final opsCorpFS :", opsCorpFS2);

                    KnCorpProfilePersistDTO corpProfilePersistDTO = populateCorpProfileInfoDTO(xdmPttServerId, subsProvPersistDTO, persisterTxn);
                    corpProfilePersistDTO.setCorporateName(subsProvInputDTO.getCorporateName());
                    corpProfilePersistDTO.setCorpFS2(corpFS2);
                    corpProfilePersistDTO.setOpsCorpFS2(opsCorpFS2);

                    // getting doc config info from Registrar Service Config
                    if(poCPttServerId != KnProvConstants.POC_HOME_NOT_ASSIGNED){
                        KnPOCSvcConfigDTO knPOCSvcConfigDTO = provInfoUtil.retrievePOCSvcConfig(poCPttServerId);
                        corpProfilePersistDTO.setDynamicQosFlag(knPOCSvcConfigDTO.getDynamicQosFlag());
                    }else{
                        // if POC Home is not assigned set Dynamic Qos flag to 0
                        corpProfilePersistDTO.setDynamicQosFlag(0);
                    }

                    //added new  parameter for dynamic Qos
                    corpProfilePersistDTO.setFeatureRelVersion(currentFsVersion);
                    String xdmCorpFS2Set = KnGeneralUtil.getDefaultXDMCorpFS2Set(XDMCORPFS2_SET.EMERGENCY_CONF_TIMER_FEATURE.value(), com.kodiak.common.resources.KnConstants.TRUE);
                    xdmCorpFS2Set = featureSetUtil.calculateXDMCorpFS2(xdmCorpFS2Set, XDMCORPFS2_SET.EMERGENCY_CONF_TIMER_FEATURE.value(), com.kodiak.common.resources.KnConstants.TRUE);
                    corpProfilePersistDTO.setXdmCorpFS2Set(xdmCorpFS2Set);
                    provXDMServerDAO.createCorporateProfile(corpProfilePersistDTO, persisterTxn);
                    successPegs.add(KnOMConstants.XDM_NUM_CORP_PROFILE_CREATED);
                    knLogger.debug(methodName, "created the corporate profile for extCorpId - ", extCorpId);
                    //corpid=getCorporateProfile by passing corpProfilePersistDTO.getExtCorpId()
                    corpProfileInfoDTO = provXDMServerDAO.retrieveCorporateProfile(corpProfilePersistDTO.getExtCorpId(), false, persisterTxn);
                    corpId = corpProfileInfoDTO.getCorpId();
                    subsProvPersistDTO.setCorpId(corpId);
                    if((KnProvConstants.SUBS_CLIENT_TYPE.DISPATCH_CLIENT.value() == subsClientType) &&
                            (KnProvConstants.WEB_DISPATCH_ENABLED == svcDispatchType)){
                        subsProvPersistDTO.setDispatchType(KnProvConstants.WEB_DISPATCH_ENABLED);
                    }
                }
            }


            //check if the received subsFeatureSet1 is null or 0 then apply the default subsFeatureSet1.
            String subsFS2 = subsProvInputDTO.getSubsFS2();
            if (subsFS2 == null) {
				BitSet finalFSBitSet = new BitSet(Long.SIZE);
				String basePkgCode = paramNameValueMap.get(KnConstants.BASE_PKGCODE);
				String basePkgDefFS = featureSetUtil.getDefSubsFeatureSetForBasePkg(publicSubscriptionType,
						corporateSubscriptionType, subsClientType, basePkgCode, xdmPttServerId);
				knLogger.debug(methodName, "base pkg is applied - ", basePkgDefFS);
				BitSet basePkgDefFSBitSet = featureSetUtil.convertHexStringToBitSet(basePkgDefFS);
				finalFSBitSet.or(basePkgDefFSBitSet);
				if (!addPkgIds.isEmpty()) {
					String pkgCodesFS = featureSetUtil.getDefSubsFeatureSetForPkgCodes(publicSubscriptionType,
							corporateSubscriptionType, subsClientType, addPkgIds, xdmPttServerId);
					knLogger.debug(methodName, "addPkgIds is present with subscriberFS - ", pkgCodesFS);
					BitSet pkgCodes_BiSet = featureSetUtil.convertHexStringToBitSet(pkgCodesFS);
					finalFSBitSet.or(pkgCodes_BiSet);
				}
				subsFS2 = featureSetUtil.convertBitSetToHexString(finalFSBitSet);
            }
            //Here we need to calculate the subscriberFS based on value i got
            Map<Integer, Integer> provFSMap = subsProvPersistDTO.getProvFSMap();
            knLogger.debug(methodName," subsProvPersistDTO's   ProvFS :", provFSMap);
            //same values of provFS1 and provFS1BitMask is used for calculation final subsFS after oring with bitmask
            String provFS2 = subsFS2;
            String provFS2BitMask = subsFS2;

             knLogger.debug(methodName," createSubscriber's   ProvFS :", provFSMap);
            if(provFSMap!= null  && !provFSMap.isEmpty()){
                if(isProvFSBitAllowed(provFSMap)){
                    bitset= featureSetUtil.convertHexStringToBitSet(subsFS2);

                    for(Map.Entry<Integer,Integer> provEntry: provFSMap.entrySet()){
                        if(provEntry.getValue()== com.kodiak.common.resources.KnConstants.PROVFS_ENABLE.ENABLED.value()){
                            bitset.set(provEntry.getKey());
                        }else{
                            bitset.clear(provEntry.getKey());
                        }
                    }
                    subsFS2=featureSetUtil.convertBitSetToHexString(bitset);

                    provFS2= subsFS2;
                    knLogger.debug(methodName,"  provFS2 ",provFS2,"bitset :",bitset);
                    provFS2BitMask= provFS2;
                    knLogger.debug(methodName,"provFS2BitMask :",provFS2BitMask);
                }
                else {
                    knLogger.error(methodName, "Operation not allowed for feature bit(s)");
                    throw new KnProvBOException(KnErrorCodes.Validator.INVALID_PROV_FS, "Requested feature bit(s) is/are not allowed to enable/disable via create/update.");
                }
            }
            knLogger.debug(methodName,"subsFS2 :",subsFS2,"provFS2 :",provFS2,"provFS2BitMask :",provFS2BitMask);
            //Generate the SubsFeatureSet by performing the BitMask with License SubsFSBITMASK
            subsFS2 = featureSetUtil.generateSubsFeatureSet(subsFS2, provFS2, provFS2BitMask);
            knLogger.debug(methodName, "final subsFS : ", subsFS2);

            //get final ClientFeatureSet
            int defaultClientPVMajorVersion = 1;

            if (KnConstants.MCSCOMPLIANCE == subsProvInputDTO.getMcsCompliance()) {
            	defaultClientPVMajorVersion=defaultMCSClientInfo.getPv();
            }

            String clientCapOverrideBitMask = featureSetUtil.getClientCapabilityBitMask(xdmPttServerId, defaultClientPVMajorVersion);
            knLogger.debug(methodName, "defaultClientPVMajorVersion ", defaultClientPVMajorVersion, " ,clientCapOverrideBitMask=", clientCapOverrideBitMask);
            String clientFS2 = featureSetUtil.getDefFinalClientFS(clientCapOverrideBitMask);

            if (KnConstants.MCSCOMPLIANCE == subsProvInputDTO.getMcsCompliance()) {
            	clientFS2 = defaultMCSClientInfo.getClientFS2();
            }
            String opsFS2=null;
            String corpAdminFS2 = featureSetUtil.getDefFinalCorpAdminFS();
            //Generate the ActiveFeatureSet
            String activeFS2=null;
            String xdmsFs2=featureSetUtil.getDefFinalXdmsFS();
            String userProfileFS2=featureSetUtil.getDefFinalUserProfileFS();
            if (corporateSubscriptionType == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
                opsFS2 = opsCorpFS2;
                if(poCPttServerId.equals(String.valueOf(0)) && presencePttServerId.equals(String.valueOf(0))){
                    poCPttServerId = xdmPttServerId;
                    presencePttServerId = xdmPttServerId;
                }
                activeFS2 = featureSetUtil.generateActiveFeatBitSet(poCPttServerId, presencePttServerId, xdmPttServerId,
                        clientFS2, subsFS2, opsFS2, opsCorpFS2, corpAdminFS2, clientCapOverrideBitMask,xdmsFs2,userProfileFS2);
            } else {
                //get final OpsFeatureSet
                opsFS2 = featureSetUtil.getDefFinalOpsFS();
                knLogger.debug(methodName, "final opsFS : ", opsFS2);
                if(poCPttServerId.equals(String.valueOf(0)) && presencePttServerId.equals(String.valueOf(0))){
                    poCPttServerId = xdmPttServerId;
                    presencePttServerId = xdmPttServerId;
                }
                activeFS2 = featureSetUtil.generateActiveFeatBitSet(poCPttServerId, presencePttServerId, xdmPttServerId,
                        clientFS2, subsFS2, opsFS2, clientCapOverrideBitMask,xdmsFs2,userProfileFS2);

            }
            //if MCPTT_COMPLAIANCE_BIT=81 is disabled then add to ASYNC_JOB_NOTIFY,if change is there
			if (KnConstants.MCSCOMPLIANCE != subsProvInputDTO.getMcsCompliance() && (subsProvInputDTO.getMcId() != null
					|| subsProvInputDTO.getMcDataId() != null || subsProvInputDTO.getMcpttId() != null
					|| subsProvInputDTO.getMcVideoId() != null)) {
				throw new KnProvBOException(KnErrorCodes.BOEntity.MCXIDS_NOT_ALLOWED_FOR_KODIAK_CLIENTS,
						"MCX Id(s) are not allowed for kodiak clients.");
			}
            if(subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.STANDALONECAMERA.value() && subsProvInputDTO.getCameraType() == CAMERA_TYPE.RE4.value()) {
                subsProvPersistDTO.setCameraType(subsProvInputDTO.getCameraType());
                String activatedClientFS2 = generalCacheUtil.getActivatedClientFS2(subsProvInputDTO.getCameraType(),subsProvInputDTO.getSubsClientType());
                if(activatedClientFS2 == null){
                    activatedClientFS2 = featureSetUtil.getDefFinalClientFS();
                }
                BitSet clientFS2BitSet = featureSetUtil.convertHexStringToBitSet(activatedClientFS2);
                String clientFS2BITMASK = featureSetUtil.getClientFS2BitMask();
                BitSet clientFS2MaskBitSet = featureSetUtil.convertHexStringToBitSet(clientFS2BITMASK);
                BitSet finalClientFSBitSet = new BitSet(Long.SIZE);
                finalClientFSBitSet.or(clientFS2BitSet);
                finalClientFSBitSet.or(clientFS2MaskBitSet);
                clientFS2 = featureSetUtil.convertBitSetToHexString(finalClientFSBitSet);
                if(poCPttServerId.equals(String.valueOf(0)) && presencePttServerId.equals(String.valueOf(0))){
                    poCPttServerId = xdmPttServerId;
                    presencePttServerId = xdmPttServerId;
                }
                activeFS2 = featureSetUtil.generateActiveFeatBitSet(poCPttServerId, presencePttServerId, xdmPttServerId,
                        clientFS2, subsFS2, opsFS2, clientCapOverrideBitMask,xdmsFs2,userProfileFS2);

            }
			validateMCXIdsUriFormat(subsProvInputDTO, persisterTxn);
			validateIfUserIdAlredyExistsInSystem(subsProvInputDTO.getUserId(), persisterTxn);
			Set<String> mdnSet = new HashSet<>();
			if (subsProvInputDTO.getMcId() != null && subsProvInputDTO.getMcId().startsWith(TELURI)) {
				mdnSet.add(subsProvInputDTO.getMcId().substring(TELURI.length()));
			}
			if (subsProvInputDTO.getMcpttId() != null && subsProvInputDTO.getMcpttId().startsWith(TELURI)) {
				mdnSet.add(subsProvInputDTO.getMcpttId().substring(TELURI.length()));
			}
			if (subsProvInputDTO.getMcVideoId() != null && subsProvInputDTO.getMcVideoId().startsWith(TELURI)) {
				mdnSet.add(subsProvInputDTO.getMcVideoId().substring(TELURI.length()));
			}
			if (subsProvInputDTO.getMcDataId() != null && subsProvInputDTO.getMcDataId().startsWith(TELURI)) {
				mdnSet.add(subsProvInputDTO.getMcDataId().substring(TELURI.length()));
			}
			List<String> mcsIdList = new ArrayList<String>();
			mcsIdList.addAll(mdnSet);
			valiadteIfMCXIdsExistsAsMdnOrAliasMdn(mcsIdList, persisterTxn);

			// Adding input MDN along with mcsId list to check if it exist in KUIDPOOL
			mcsIdList.add(subsProvInputDTO.getMdn());
			validateIfMdnOrMcidsPresentInKUIDPOOL(mcsIdList);

			Set<String> mcsIdSet = new HashSet<>();
			mcsIdSet.add(TELURI + subsProvInputDTO.getMdn());
			if (subsProvInputDTO.getMcId() != null) {
				mcsIdSet.add(subsProvInputDTO.getMcId());
			}
			if (subsProvInputDTO.getMcpttId() != null) {
				mcsIdSet.add(subsProvInputDTO.getMcpttId());
			}
			if (subsProvInputDTO.getMcVideoId() != null) {
				mcsIdSet.add(subsProvInputDTO.getMcVideoId());
			}
			if (subsProvInputDTO.getMcDataId() != null) {
				mcsIdSet.add(subsProvInputDTO.getMcDataId());
			}
			List<String> mcsIds = new ArrayList<String>();
			mcsIds.addAll(mcsIdSet);
			valiadteIfMCXIdsExistsInSystem(mcsIds, subsProvInputDTO.getMdn(), persisterTxn);
			populateDefaultMCSIds(subsProvInputDTO);

			subsProvPersistDTO.setUserId(subsProvInputDTO.getUserId());
			subsProvPersistDTO.setMcId(subsProvInputDTO.getMcId());
			subsProvPersistDTO.setMcpttId(subsProvInputDTO.getMcpttId());
			subsProvPersistDTO.setMcVideoId(subsProvInputDTO.getMcVideoId());
			subsProvPersistDTO.setMcDataId(subsProvInputDTO.getMcDataId());
            subsProvPersistDTO.setSubsFS2(subsFS2);;
            subsProvPersistDTO.setClientFS2(clientFS2);
            subsProvPersistDTO.setOpsFS2(opsFS2);
            subsProvPersistDTO.setActiveFS2(activeFS2);
            subsProvPersistDTO.setUserProfileFS2(userProfileFS2);
            subsProvPersistDTO.setCorpAdminFS2(corpAdminFS2);
            String updatedXdmsFs2 = xdmsFs2;
            String xdmCorpFS2Set = genInfoUtil.selectXDMCorpFS(corpId, true, persisterTxn);
            knLogger.info(methodName, "xdmCorpFS2Set: ", xdmCorpFS2Set);
            boolean corpLevelLocationFlag = false;
            if (xdmCorpFS2Set != null) {
                corpLevelLocationFlag = KnGeneralUtil.getFeatureBitValue(xdmCorpFS2Set, 1);
                knLogger.info(methodName, "corpLevelLocationFlag: ", corpLevelLocationFlag);
            } else {
                knLogger.warn(methodName, "xdmCorpFS2Set is null");
            }
            if (corpLevelLocationFlag) {
                //TO-DO --> update XDMSFS2
                BitSet xdmsFs2check = KnGeneralUtil.convertHexStringToBitSet(updatedXdmsFs2);
                knLogger.info(methodName, "xdmsFs2check: ", xdmsFs2check);
                xdmsFs2check.set(27, true);
                updatedXdmsFs2 = KnGeneralUtil.convertBitSetToHexString(xdmsFs2check);
                knLogger.info(methodName, "updatedXdmsFs2: ", updatedXdmsFs2);
                subsProvPersistDTO.setXdmsFS2(updatedXdmsFs2);
            }
            subsProvPersistDTO.setXdmsFS2(updatedXdmsFs2);
          //During create subscriber will not belong to any profile hence default profile flag is set to 1 and profile index to 0
            subsProvPersistDTO.setIsDefaultProfile(KnConstants.IS_DEFAULT_PROFILE);
            subsProvPersistDTO.setUserProfileIndex(KnConstants.USER_PROFILE_INDEX);
            knLogger.debug(methodName, "Subscriber client type ", subsClientType);
            long profileCreationTime = Calendar.getInstance().getTimeInMillis();
            subsProvPersistDTO.setProfileCreationTime(profileCreationTime);
            //since creation time is same as last profile time while create
            subsProvPersistDTO.setLastProfileUpdateTime(profileCreationTime);
            if (KnConstants.MCSCOMPLIANCE == subsProvInputDTO.getMcsCompliance()) {
            	subsProvPersistDTO.setMcsCompliance(subsProvInputDTO.getMcsCompliance());
            	subsProvPersistDTO.setLastActivationTime(profileCreationTime);
            	subsProvPersistDTO.setClientPVmajorVer(defaultMCSClientInfo.getPv());
            	subsProvPersistDTO.setClientPVminorVer(0);
                //populate userAgent from common config file for CRI client
                String userAgent = microServicesParamNameValueMap.get(CRI_DEFAULT_USER_AGENT);
                subsProvPersistDTO.setUserAgent(userAgent);
            }

			Integer qppPkgId = null;
			Integer profileId = null;
			Integer dataPkgId = null;
			if (!addonPackageIds.isEmpty()) {
				for (String addonPkgCode : addonPackageIds.keySet()) {
					profileId = featureSetUtil.getAddProfIdForPkg(addonPkgCode, xdmPttServerId);
					if (profileId != null) {
						qppPkgId = genInfoUtil.getDataPkgId(KnConstants.QPP_DATA_PKG_TYPE, persisterTxn).get(profileId);
						if (qppPkgId != null) {
							dataPkgId = qppPkgId;
							break;
						} else if (dataPkgId == null)
							dataPkgId = genInfoUtil.getDataPkgId(KnConstants.ADDON_DATA_PKG_TYPE, persisterTxn)
									.get(profileId);

					}
				}

			}
			if (qppPkgId == null)
				qppPkgId = KnConstants.DEFAULT_QPP_ID;
			if (dataPkgId == null)
				dataPkgId = KnConstants.DEFAULT_DATAPKG_ID;
			if (profileId == null)
				profileId = KnConstants.DEFAULT_PROFILE_ID;

			subsProvPersistDTO.setQppPkgId(qppPkgId);
            knLogger.debug(methodName, "Subscriber Prov Persist DTO - ", subsProvPersistDTO);

            //updating server capacity util table for pocHome,presenceHome and xdmHome
            /*knLogger.debug(methodName, "updating count to Server capacity Util Info");
            List<String> pttServerIds = new ArrayList<String>();
            pttServerIds.add(poCPttServerId);
            pttServerIds.add(presencePttServerId);
            pttServerIds.add(xdmPttServerId);
            provXDMServerDAO.updateServerCapacityUtil(pttServerIds, KnProvConstants.COUNT.INCREMENT,1,  persisterTxn);*/
            boolean awareClient = false;
            if((String.valueOf(subsClientType)).matches(AWARE_CLIENTS)){
                knLogger.debug(methodName, "Aware client, So not creating OIDC profile");
                awareClient = true;
            }
            subsProvPersistDTO.setFeatureRelVersion(currentFsVersion);

            knLogger.debug(methodName, "adding an entry into Subscriber Info");
            provXDMServerDAO.createSubscrProfile(subsProvPersistDTO,Boolean.FALSE, persisterTxn);
            if(subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.STANDALONECAMERA.value() && subsProvInputDTO.getCameraType()== CAMERA_TYPE.RE4.value() && subsProvInputDTO.getCameraInfo() != null){
                subsProvPersistDTO.setCameraInfo(subsProvInputDTO.getCameraInfo());
                knLogger.debug(methodName, "adding an entry into subscriber Camera Info");
                provXDMServerDAO.createSubscriberCameraInfo(subsProvPersistDTO,persisterTxn);
            }
            if(subsProvInputDTO.getAliasInfoList() != null){
                List<KnSubsAliasInfoDTO> aliasInfoList=subsProvInputDTO.getAliasInfoList();
                provXDMServerDAO.insertIntoSubsAliasId(aliasInfoList,mdn, persisterTxn);
            }
            String idmFqdn = genInfoUtil.getIDMInternalFqdn(persisterTxn);
            boolean bitEnabled = KnGeneralUtil.getFeatureBitValue(subsProvPersistDTO.getSubsFS2(), FEATURE_SET.MCDEVICE.value());
            knLogger.debug(methodName,"awareClient :",awareClient
                    ," MCSCOMP: ",subsProvInputDTO.getMcsCompliance()," licenseType:",licenseType," bitEnabled: ",bitEnabled);
            //check for CRI and kodiak clients
            if ((KnConstants.MCSCOMPLIANCE == subsProvInputDTO.getMcsCompliance() && !awareClient)
                    || (KnConstants.KODIAK_CLIENT == subsProvInputDTO.getMcsCompliance()&&KnConstants.USER_LICENSE_TYPE_DISABLED==licenseType&&bitEnabled)) {

                KnXDMDeviceProvDTO deviceProfile = xdmServerDAO.selectDeviceProfileByDeviceId(mdn, persisterTxn);
				KnDeviceInfoPersistDTO deviceDTO=new KnDeviceInfoPersistDTO();
				deviceDTO.setDeviceId(mdn);
				xdmServerDAO.deleteDeviceImpiInfo(deviceDTO, persisterTxn);
				xdmServerDAO.deleteDeviceInfo(deviceDTO, persisterTxn);
                String appId = APP_ID.USERMCSCLIENTS.value();
                if((bitEnabled || KnConstants.USER_LICENSE_TYPE == licenseType) && KnConstants.MCSCOMPLIANCE != subsProvInputDTO.getMcsCompliance()){
                    appId = APP_ID.HANDSET_STANDARD.value();
                }
                KnXDMSServiceConfigDTO xdmsServiceConfigDTO = genInfoUtil.retrieveXDMSServiceConfig(xdmPttServerId,
                        persisterTxn);
                String realm = xdmsServiceConfigDTO.getAuthRealm();
                String password = pwdUtil.generatePassword(com.kodiak.common.resources.KnConstants.MAX_OIDC_PASSWORD_LENGTH);
                KnDeviceInfoPersistDTO deviceInfoPersistDTO = new KnDeviceInfoPersistDTO();
                deviceInfoPersistDTO.setDeviceId(mdn);
                deviceInfoPersistDTO.setDeviceStatus(KnConstants.DEVICE_STATUS_OP.ACTIVATED.value());
                deviceInfoPersistDTO.setDeviceActTimeStamp(profileCreationTime);
                deviceInfoPersistDTO.setDeviceLastUsed(profileCreationTime);
                deviceInfoPersistDTO.setDeviceCreatedAs(KnConstants.IMPLICIT_DEVICE);
                if (corporateSubscriptionType == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
                deviceInfoPersistDTO.setCorpId(corpId); }
                //XMSSTP-873 - CRI client to support SIP URI format.
                if(KnConstants.MCSCOMPLIANCE == subsProvInputDTO.getMcsCompliance()){
                    deviceInfoPersistDTO.setDeviceIMPI(subsProvInputDTO.getMcId());
                }else{
                    deviceInfoPersistDTO.setDeviceIMPI(KnConstants.TEL_URI_TEMPLATE + mdn);
                }
                if(bitEnabled && !(KnConstants.MCSCOMPLIANCE == subsProvInputDTO.getMcsCompliance())) {
                	if(deviceProfile!=null) {
                    deviceInfoPersistDTO.setDeviceDigestPassword(deviceProfile.getDevicePassword());
                	} else {
                		deviceInfoPersistDTO.setDeviceDigestPassword(null);
                	}

                    deviceInfoPersistDTO.setDeviceType(DEVICE_TYPE.MC_DEVICE.Value());
                    deviceInfoPersistDTO.setDeviceName(null);
                    deviceInfoPersistDTO.setDeviceClientId(null);
                    deviceInfoPersistDTO.setDeviceshared(KnConstants.MCDEVICESHARED_TYPE.SHARED.Value());
                    deviceInfoPersistDTO.setReqDeviceId(mdn);
                }
                else{
                	if(deviceProfile!=null) {
                        deviceInfoPersistDTO.setDeviceDigestPassword(deviceProfile.getDevicePassword());
                    	} else {
                    		String clientPassword = provInfoUtil.generateHA1(KnConstants.TEL_URI_TEMPLATE + mdn, realm, password);
                            deviceInfoPersistDTO.setDeviceDigestPassword(clientPassword);
                    	}

                    deviceInfoPersistDTO.setDeviceClientId(mdn);
                    deviceInfoPersistDTO.setDeviceshared(KnConstants.DEVICESHARED);
                }
                xdmServerDAO.createDeviceInfo(deviceInfoPersistDTO, persisterTxn);
                KnDeviceImpiInfoPersistDTO deviceImpiInfo = new KnDeviceImpiInfoPersistDTO();
                //XMSSTP-873 - CRI client to support SIP URI format.
                if(KnConstants.MCSCOMPLIANCE == subsProvInputDTO.getMcsCompliance()){
                    deviceImpiInfo.setDeviceImpi(subsProvInputDTO.getMcId());
                    deviceImpiInfo.setDeviceImpu(subsProvInputDTO.getMcId());
                }else{
                    deviceImpiInfo.setDeviceImpi(KnConstants.TEL_URI_TEMPLATE + mdn);
                    deviceImpiInfo.setDeviceImpu(KnConstants.TEL_URI_TEMPLATE + mdn);
                }
                xdmServerDAO.createDeviceImpiInfo(deviceImpiInfo, persisterTxn);

				// create OIDC profile
				KnXDMSubsAliasDetailsRespDTO oidcResponseDTO = null;
				KnOPSubsProfileInfoDTO subsProfileInfoDTO = provInfoUtil.retrieveSubscriberInfo(mdn, persisterTxn);
				String pwdExpiry = microServicesParamNameValueMap.get(MCS_TEMP_PASSWORD_EXPIRY);
				knLogger.info(methodName, "deviceSharingFlag", deviceSharingFlag);
				String oidcUserId = null;
				if (subsProfileInfoDTO.getAliasMdn() != null) {
					oidcUserId = subsProfileInfoDTO.getAliasMdn();
				} else {
					oidcUserId = subsProfileInfoDTO.getMdn();
				}

				int deviceSharing = 0;
				int passwordExpiry = 0;
				if (deviceSharingFlag != null)
					deviceSharing = Integer.parseInt(deviceSharingFlag);
				if (pwdExpiry != null)
					passwordExpiry = Integer.parseInt(pwdExpiry);
				long currentTimeInMilliSecond = System.currentTimeMillis();
				long pwdExpiryInMilli = currentTimeInMilliSecond + (1000 * 60 * passwordExpiry);
				KnXDMSubsAliasDetailsReqDTO subsAliasDetailsReqDTO = new KnXDMSubsAliasDetailsReqDTO();
				subsAliasDetailsReqDTO.setUserid(oidcUserId);
				subsAliasDetailsReqDTO.setTemppwd(Boolean.FALSE);
                /**
                 * Device sharing is allowed only for user license clients hence adding explict conditions to check lincetype
                 */
                knLogger.info(methodName,"appId - ",appId," licenseType - ",licenseType);
				if (appId.equals(APP_ID.HANDSET_STANDARD.value()) ) {
                    String randomPwd = pwdUtil.generatePassword(password, appId);
                    subsAliasDetailsReqDTO.setPwd(randomPwd);
                    subsAliasDetailsReqDTO.setTemppwd(Boolean.FALSE);
                    subsAliasDetailsReqDTO.setGeneratepwd(randomPwd == null);
                    Map<String, Object> oidcAttributes = new HashMap<>();
				    if((ENABLED == deviceSharing) &&  (KnConstants.USER_LICENSE_TYPE == licenseType)) {
                        subsAliasDetailsReqDTO.setTemppwd(Boolean.TRUE);
                        subsAliasDetailsReqDTO.setPwdexpiry(pwdExpiryInMilli);
                        oidcAttributes.put(MCPTT_ID_OIDC, mdn);
                        //send email
                        List<String> actions = new ArrayList<>();
                        actions.add(TMP_PWD_MODE.PASSWORD_INFO_MAIL.value());
                        oidcAttributes.put(ACTIONS, actions);
                    }else{
                        oidcAttributes.put(MCPTT_ID_OIDC, subsProfileInfoDTO.getMcpttId());
                    }
                    xdmPttServerId = KnDbUtil.getDBConfigInfo().getLocalPttId();
                    knLogger.debug(methodName, "realm ", realm);
                    oidcAttributes.put(MCVIDEO_ID_OIDC, subsProfileInfoDTO.getMcVideoId());
                    oidcAttributes.put(MCDATA_ID_OIDC, subsProfileInfoDTO.getMcDataId());
                    subsAliasDetailsReqDTO.setAttributes(oidcAttributes);
				}else if(appId == APP_ID.USERMCSCLIENTS.value()){
                    xdmPttServerId = KnDbUtil.getDBConfigInfo().getLocalPttId();
                    knLogger.debug(methodName, "realm ", realm);
                    Map<String, Object> oidcAttributes = new HashMap<>();
                    List<String> actions = new ArrayList<>();
                    actions.add(TMP_PWD_MODE.PASSWORD_INFO_MAIL.value());
                    oidcAttributes.put(ACTIONS, actions);
                    oidcAttributes.put(MCPTT_ID_OIDC, subsProfileInfoDTO.getMcpttId());
                    oidcAttributes.put(MCVIDEO_ID_OIDC, subsProfileInfoDTO.getMcVideoId());
                    oidcAttributes.put(MCDATA_ID_OIDC, subsProfileInfoDTO.getMcDataId());
                    oidcAttributes.put(MC_ID_OIDC, subsProfileInfoDTO.getMcId());
                    oidcAttributes.put(NETWORK_NAME, subsProfileInfoDTO.getNetworkName());
                    oidcAttributes.put(DIGEST_PWD_OIDC, password);
                    oidcAttributes.put(DEVICEIMPL_OIDC, KnConstants.TEL_URI_TEMPLATE + mdn);
                    oidcAttributes.put(DEVICEIMPU_OIDC, KnConstants.TEL_URI_TEMPLATE + mdn);
                    oidcAttributes.put(MCS_SCOPES_OIDC, KnGeneralUtil.populateOidcScopes(subsProfileInfoDTO.getActiveFS2()));
                    subsAliasDetailsReqDTO.setAttributes(oidcAttributes);
                    String randomPwd = pwdUtil.generatePassword(password, appId);
                    subsAliasDetailsReqDTO.setTemppwd(Boolean.TRUE);
                    subsAliasDetailsReqDTO.setPwd(randomPwd);
                    subsAliasDetailsReqDTO.setGeneratepwd(randomPwd == null);
                    subsAliasDetailsReqDTO.setPwdexpiry(pwdExpiryInMilli);
                }

				subsAliasDetailsReqDTO.setEmail(subsProfileInfoDTO.getUserId());
				oidcResponseDTO = KnManageSyncUserProfileUtil.getInstance().createIDMUserForOIDCMgmt(subsAliasDetailsReqDTO, appId, idmFqdn, false, null);

				knLogger.debug(methodName, "oidcResponseDTO ", oidcResponseDTO);
            }else if(KnProvConstants.SUBS_CLIENT_TYPE.DISPATCH_CLIENT.value() == subsClientType
                    &&userId!=null){
                String appId = APP_ID.DISPATCHER.value();
                String password = pwdUtil.generatePassword(com.kodiak.common.resources.KnConstants.MAX_OIDC_PASSWORD_LENGTH);
                Map<String, Object> oidcAttributes = new HashMap<>();
                int passwordExpiry = 0;
                String pwdExpiry = microServicesParamNameValueMap.get(MCS_TEMP_PASSWORD_EXPIRY);
                if (pwdExpiry != null)
                    passwordExpiry = Integer.parseInt(pwdExpiry);
                long currentTimeInMilliSecond = System.currentTimeMillis();
                long pwdExpiryInMilli = currentTimeInMilliSecond + (1000 * 60 * passwordExpiry);
                KnXDMSubsAliasDetailsReqDTO subsAliasDetailsReqDTO = new KnXDMSubsAliasDetailsReqDTO();
                subsAliasDetailsReqDTO.setUserid(userId);
                subsAliasDetailsReqDTO.setTemppwd(Boolean.FALSE);
                oidcAttributes.put(MCPTT_ID_OIDC, subsProvPersistDTO.getMcpttId());
                oidcAttributes.put(MCVIDEO_ID_OIDC, subsProvPersistDTO.getMcVideoId());
                oidcAttributes.put(MCDATA_ID_OIDC, subsProvPersistDTO.getMcDataId());
                subsAliasDetailsReqDTO.setAttributes(oidcAttributes);
                subsAliasDetailsReqDTO.setPwd(password);
                subsAliasDetailsReqDTO.setGeneratepwd(password == null);
                subsAliasDetailsReqDTO.setPwdexpiry(pwdExpiryInMilli);
                subsAliasDetailsReqDTO.setEmail(userId);
                KnXDMSubsAliasDetailsRespDTO oidcResponseDTO = KnManageSyncUserProfileUtil.getInstance()
                        .createIDMUserForOIDCMgmt(subsAliasDetailsReqDTO, appId, idmFqdn, false, null);
                knLogger.debug(methodName, "oidcResponseDTO for wds client:", oidcResponseDTO);
            }

            List<String> mdns = new ArrayList<>();
            mdns.add(mdn);
            if (KnProvConstants.SUBS_CLIENT_TYPE.POC_NNI_Alias_MDN.value() == subsClientType ||
                    KnProvConstants.SUBS_CLIENT_TYPE.POC_NNI_Group_MDN.value() == subsClientType) {
                provInfoUtil.createNNISubscriberProfile(mdns,subsClientType,persisterTxn);
            }

            if (extCorpId != null) {
                knLogger.debug(methodName, "Calling Update Etag for Corp = ", extCorpId);
                updateEtagForNNISubscr(extCorpId, persisterTxn);
            }


            // Interne APN changes ....
            String apnName = genInfoUtil.getDefaultAPNName(persisterTxn);
            if (KnConstants.MCSCOMPLIANCE == subsProvInputDTO.getMcsCompliance()){
            	apnName=KnConstants.DEFAULTAPNNAME;
            }
            // verify apn exists or not ..
            Integer apnId = genInfoUtil.getAPNId(apnName, persisterTxn);
            knLogger.info(methodName, " APNID ", apnId, "APN NAME  :", apnName);
            if (apnId == null) {
                knLogger.error(methodName, " Default APN not exists , APN_INFO_NOT_FOUND ");
                throw new KnProvBOException(KnErrorCodes.BOEntity.APN_INFO_NOT_FOUND, " APN_INFO_NOT_FOUND ");
            }
            provXDMServerDAO.addSubApn(mdn, apnId, persisterTxn);
            // --> Creating an entry into the Subscriber Roaming Profile
            // subs roaming profile is mandatory if feature roaming is enabled
            // current release no license chk is performed has to add a check in later phases

//            if (subsClientType == null) {
//                subsClientType = KnProvConstants.SUBS_CLIENT_TYPE.HANDSET.value();
//            }
            knLogger.debug(methodName, "Subscriber client type ", subsClientType);

            //Checking whether subscriber client type is applicable for roaming or not
            if (null == paramNameValueMap.get(KnConstants.ROAM_NA_CLIENT_TYPES)) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.SERVER_CONFIGURATION_FAILURE, "ROAM_NA_CLIENT_TYPE parameter not found");
            }
            String paramValue = (String) paramNameValueMap.get(KnConstants.ROAM_NA_CLIENT_TYPES);
            List<String> roamNAClientTypes = new ArrayList<>();
            if (null != paramValue) {
                roamNAClientTypes = Arrays.asList(paramValue.split(","));
                knLogger.debug(methodName, "Roaming not applicable client type ", roamNAClientTypes);
            }
            ArrayList<Integer> roamingClusterIdList = new ArrayList<Integer>();
            //Integration Fix: retrieving the roaming clusterId for the clusterName received as request
            if (roamNAClientTypes.contains(String.valueOf(subsClientType))) {
                roamingClusterIdList.add(KnProvConstants.DEFAULT_ROAMING_CLUSTER_ID);
            } else {
                if (subsProvInputDTO.getRoamingTypes() != null && !subsProvInputDTO.getRoamingTypes().isEmpty()) {
                    HashMap<Integer, String> supportedRoamingClusterInfo = (HashMap<Integer, String>) provInfoUtil.retrieveSupportedRoamingList();
                    for (int clusterName : subsProvInputDTO.getRoamingTypes()) {
                        for (int roamingID : supportedRoamingClusterInfo.keySet()) {
                            //  if (supportedRoamingClusterInfo.get(roamingID).equalsIgnoreCase(String.valueOf(clusterName))) {
                            if (String.valueOf(clusterName).equalsIgnoreCase(supportedRoamingClusterInfo.get(roamingID)) && !roamingClusterIdList.contains(roamingID)) {
                                roamingClusterIdList.add(roamingID);
                                break;
                            }
                        }
                    }
                } else {
                    roamingClusterIdList.add(KnProvConstants.DEFAULT_ROAMING_CLUSTER_ID);
                }
            }
            knLogger.debug(methodName, "adding an entry into Subscriber Roaming Profile");
            provXDMServerDAO.createSubscrRoamingProfile(mdn, roamingClusterIdList, persisterTxn);

            if (corporateSubscriptionType == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
                // -->. creating entry into the XDM_CorpResourceListIndex Doc
                knLogger.debug(methodName, "Adding an entry into the Corp resource List Index Doc");
                xdmServerDAO.addMdnToCorpResourceListIndexDoc(mdn, persisterTxn);

            }
            // -->. creating entry into the XDM_ContactListDocMap
            int contactListId = -1;
            knLogger.debug(methodName, "adding an entry into XDM Contact List Doc Map ");
            KnContactListPersistDTO contactListDocPersistDTO = new KnContactListPersistDTO();
            contactListDocPersistDTO.setMdn(mdn);
            contactListId = xdmServerDAO.addMdnToXDMContactListDocMap(contactListDocPersistDTO, persisterTxn);

            // -->. creating entry into the XDM_ContactList
            knLogger.debug(methodName, "adding an entry into XDM Contact List ");
            xdmServerDAO.addMdnToXDMContactList(mdn, contactListId, persisterTxn);

            // -->. Creating entry into the XDM_Directory
            knLogger.debug(methodName, "adding an entry into XDM Directory");
            xdmServerDAO.addMdnToXDMDirectory(mdn, persisterTxn);

            // -->. Creating entry into the addon package
			if (!addonPackageIds.isEmpty()) {
				provXDMServerDAO.createSubAddOnPkgs(mdn, new ArrayList<>(addonPackageIds.keySet()), persisterTxn);
			}

			// -->. Creating entry into the pocsubsaddlinfo
			KnSubsAddlInfoPersistDTO subsProfilePersistDTO = new KnSubsAddlInfoPersistDTO();
			subsProfilePersistDTO.setMdn(mdn);
			subsProfilePersistDTO.setTimeSlotType(KnConstants.TIME_SLOT_TYPE);
			subsProfilePersistDTO.setTierPkgCode(tierPackageId);
			subsProfilePersistDTO.setDataPkgId(dataPkgId);
			subsProfilePersistDTO.setOnBoardingMailReqd(subsProvInputDTO.getOnBoardingEmailReqd());
			provXDMServerDAO.createSubscrPkgAddlInfo(subsProfilePersistDTO, persisterTxn);
            String oneMessageEnabled = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn).get(KnConstants.ONE_MESSAGE_SERVICE_ENABLED);
            knLogger.debug(methodName, "oneMessageEnabled", oneMessageEnabled);
            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction ");
                persisterTxn.save();
            }

            if (subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.DESKTOP.value()) {
                successPegs.add(KnOMConstants.XDM_NUM_DESKTOP_CLIENTS_CREATED);
            } else if (subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.DISPATCH.value()) {
                successPegs.add(KnOMConstants.XDM_NUM_DISPATCH_CLIENTS_CREATED);
            } else if (subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.POCDONORRADIO.value()) {
                successPegs.add(KnOMConstants.XDM_NUM_LMR_INTEROP_CLIENTS_CREATED);
            } else if (subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.THIRDPARTYPOCCLIENT.value()) {
                successPegs.add(KnOMConstants.XDM_NUM_3RD_PARTY_POC_CLIENTS_CREATED);
            } else if (subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.MOBILE_CLIENT.value()) {
                successPegs.add(KnOMConstants.XDM_NUM_MOBILE_API_CLIENTS_CREATED);
            } else if (subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.THIRDPARTYDISPATCHERCLIENT.value()) {
                successPegs.add(KnOMConstants.XDM_NUM_3RDPARTY_DISPATCHER_CLIENTS_CREATED);
            } else if (subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.POC_NNI_Alias_MDN.value()){
                successPegs.add(KnOMConstants.XDM_NUM_NNI_ALIAS_SUBSCRIBERS_CREATED);
            } else if (subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.POC_NNI_Group_MDN.value()){
                successPegs.add(KnOMConstants.XDM_NUM_NNI_GROUP_SUBSCRIBERS_CREATED);
            } else if (subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.CROSS_CARRIER_PTT_CLIENT.value()) {
                successPegs.add(KnOMConstants.XDM_NUM_CROSS_CARRIER_PTT_CLIENTS_CREATED);
            } else if (subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value()){
                successPegs.add(KnOMConstants.XDM_NUM_PTTRADIO_CROSSCARRIER_CLIENTS_CREATED);
            } else if (subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value()){
                successPegs.add(KnOMConstants.XDM_NUM_PTTRADIO_HANDSET_CLIENTS_CREATED);
            } else if (subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value()) {
                successPegs.add(KnOMConstants.XDM_NUM_PTTRADIO_WIFIONLY_CLIENTS_CREATED);
            } else if (subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.SGMDNPATCH.value()) {
                successPegs.add(KnOMConstants.XDM_NUM_SG_MDN_PATCH_CLIENTS_CREATED);
            }else if (subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.DATAGROUPMDN.value()) {
                successPegs.add(KnOMConstants.XDM_NUM_SG_MDN_PATCH_CLIENTS_CREATED);
            }
            respDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.SUCCESS.value());
            respDTO.setResponseMessage(KnProvConstants.CREATE_SUBSCRIBER_SUCCESS);
            if (corporateSubscriptionType == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
                respDTO.setCorpId(corpId);
            }
            respDTO.setSuccessPegs(successPegs);
            if(activeFS2 != null) {
                respDTO.setActiveFS2(activeFS2);
                respDTO.setSubsFS2(subsFS2);
            }
            respDTO.setSubsFS2(subsProvPersistDTO.getSubsFS2());
            respDTO.setMcId(subsProvPersistDTO.getMcId());
            respDTO.setMcDataId(subsProvPersistDTO.getMcDataId());
            respDTO.setMcPttId(subsProvPersistDTO.getMcpttId());
            respDTO.setMcVideoId(subsProvPersistDTO.getMcVideoId());
            respDTO.setNetworkName(subsProvPersistDTO.getNetworkName());
            respDTO.setCorpName(subsProvPersistDTO.getCorporateName());
            respDTO.setServiceAuthStatus(subsProvPersistDTO.getServiceAuthStatus());
            //welcome sms notification..
            //ptt client type -notification changes
            if (null != oneMessageEnabled && oneMessageEnabled.equals(ONE_MSG_STATUS.ENABLED.value())) {
                knLogger.debug(methodName, "One Message Is Disabled storing SMS into table");
            if(subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value() || subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value()||
            		subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value()){

            	sendSMSNotification(subsClientType, mdn, KnProvConstants.WELCOME_SMS_ID_PTTRADIO);
            }
            else if (subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.HANDSET.value() ||
                        subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.POCDONORRADIO.value()){

            	sendSMSNotification(subsClientType, mdn, KnProvConstants.WELCOME_SMS_ID);
            }
            }
            knLogger.exit(methodName, "Create Subscriber operation-", respDTO);

            return respDTO;
        } catch (KnFeatureSetException ex) {
            knLogger.error(methodName, "Feature Set Exception occured :", ex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw new KnProvBOException(ex.getErrorCode(), ex.getMessage(), ex);
        } catch (KnDBConnectionException ex) {
            knLogger.error(methodName, "DAO DBConnection Exception occured :", ex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);

        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occurred : ", ex);
            knLogger.error(methodName, ex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            if (com.kodiak.xdms.server.common.resources.KnErrorCodes.DAO.ROW_ALREADY_EXISTS.equals(ex.getErrorCode())) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_ALREADY_EXISTS, "Subscriber already exists");
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception occurred :", e);
            knLogger.error(methodName, e);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            if (KnErrorCodes.BOEntity.MAX_SUBS_LIMIT_REACHED_FOR_POC.equals(e.getErrorCode())) {
                knLogger.info("raise alarm", "KnProvConstants.XDMS_POC_CAPACITY_REACHED_ALARMCODE :", KnProvConstants.XDMS_POC_CAPACITY_REACHED_ALARMCODE
                        , "KnAlarmConstants.CRITICAL :", KnAlarmConstants.SEVERITY_CRITICAL
                        , "KnProvConstants.MANAGEDOBJECT_CLASSTYPE :", KnProvConstants.XDMMANAGEDOBJECT_CLASSTYPE);
                KnAlarmGeneratorUtil.generateAlarm(KnProvConstants.XDMS_POC_CAPACITY_REACHED_ALARMCODE, KnAlarmConstants.SEVERITY_CRITICAL, KnProvConstants.XDMMANAGEDOBJECT_CLASSTYPE, KnProvConstants.PROVISIONING);
            }

            throw e;
        } catch (KnValidationException vex) {
            knLogger.error(methodName, "Validation Exception occured :", vex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw vex;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while Create Subscriber");
            knLogger.error(methodName, e);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred while create Subscriber", e);
        }
    }

    /**
     * method to populate the Corporate Profile Info
     *
     * @param xdmPttServerId        String
     * @param subsProfilePersistDTO KnSubsProfilePersistDTO
     * @param persisterTxn          KnPersisterTxn
     * @return KnCorpProfilePersistDTO
     * @throws KnProvBOException BO Entity Exception
     */
    private KnCorpProfilePersistDTO populateCorpProfileInfoDTO(String xdmPttServerId, KnSubsProfilePersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnProvBOException {
        String methodName = "populateCorpProfileInfoDTO(KnSubsProfilePersistDTO, KnPersisterTxn)";

        knLogger.debug(methodName, "populating the corpProfilePersistDTO");
        KnCorpProfilePersistDTO corpProfilePersistDTO = new KnCorpProfilePersistDTO();
        corpProfilePersistDTO.setExtCorpId(subsProfilePersistDTO.getExtCorpId());
        corpProfilePersistDTO.setXDMSHome(xdmPttServerId);
        corpProfilePersistDTO.setPocHome(subsProfilePersistDTO.getPoCHome());
        corpProfilePersistDTO.setHierarchyType(subsProfilePersistDTO.getHierarchyType());
        return corpProfilePersistDTO;
    }
    public KnOPUpdateSubsInfoDTO updateSubscriberFSAndPkgCodes(KnIPSubsProvInfoDTO subsProvInputDTO, KnPersisterTxn persisterTxn) throws KnProvBOException, KnValidationException {
    	String methodName = "updateSubscriberFSAndPkgCodes(KnIPSubsProvInfoDTO, KnPersisterTxn)";
        KnOPUpdateSubsInfoDTO responseDTO = new KnOPUpdateSubsInfoDTO();
        KnSubsProfilePersistDTO subsProfilePersistDTO= null;
        KnOPCorpProfileInfoDTO corpProfileInfoDTO = null;
        Map<Integer,Integer> provFSMap= null;
        long lastProfileUpdateTime = 0;
        boolean isProfileUpdated = false;
        boolean isPkgCodeUpdated = false;
        Integer isprovFSEnabled= PROVFS_ENABLE.DISABLED.value();
        knLogger.info(methodName, "ENTRY: Txn - ", persisterTxn);
        try {
        	Map<String, String> paramNameValueMap = genInfoUtil.retrieveRTXConfigValues(persisterTxn);
        	IProvXDMServerDAO xdmServerDAO = KnProvFactorySelector.getProvDAOFactory(KnFactorySelector.DB).createProvXDMServerDAO();

            try {
                xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
            } catch (KnBOException e) {
                knLogger.error(methodName, "failed to retrieve xdm Ptt Sever Id",e );
                throw new KnProvBOException(KnErrorCodes.BOEntity.XDMS_PTT_ID_NOT_FOUND, "Failed to retrieve XDM PTT Server ID", e);
            }

            KnSubsProfilePersistDTO validatePersistDTO = new KnSubsProfilePersistDTO();
            String mdn = subsProvInputDTO.getMdn();
            provFSMap=subsProvInputDTO.getProvFSMap();
            //retrieve the Subscriber Profile.
            //retrieved Profile will used further for validation input data of the against to the input DTO
            KnOPSubsProfileInfoDTO existingSubsProfileDTO = provInfoUtil.retrieveSubscriberInfo(mdn, persisterTxn);
            knLogger.info(methodName, "Existing subs details :",existingSubsProfileDTO );
            //retrieve tier pkg & addon pkg
			Map<String, Integer> exsitingPkgIds = new HashMap<String, Integer>();
			Map<String, Integer> exisitngSubsPkgs = new HashMap<String, Integer>();
			String existingTierPkg = null;
			KnOPSubsAddlInfoProfileDTO allProfileDto = xdmServerDAO.retrieveSubscrAddlInfo(subsProvInputDTO.getMdn(),
					persisterTxn);
			if (allProfileDto != null)
				existingTierPkg = allProfileDto.getTierPkgCode();
			if (existingTierPkg != null)
				exsitingPkgIds.put(existingTierPkg, KnConstants.TIER_PKG_TYPE);
			List<String> existingAddonPkgs = xdmServerDAO.selectSubAddOnPkgs(subsProvInputDTO.getMdn(), persisterTxn);
			if (existingAddonPkgs != null) {
				for (String pkgCode : existingAddonPkgs) {
					exsitingPkgIds.put(pkgCode, KnConstants.ADDON_PKG_TYPE);
				}
			}
			knLogger.info(methodName, "PkgIdsMap ",
					subsProvInputDTO.getPkgIdMap() + " exsitingPkgIds " + exsitingPkgIds);
			Map<String, Integer> finalPkgIdsMap = new HashMap<String, Integer>();
			exisitngSubsPkgs=new HashMap<>(exsitingPkgIds);
			knLogger.info(methodName, "exisitngSubsPkgs " +exisitngSubsPkgs);
			List<String> qppPkgCodes=featureSetUtil.getQPPPkgCodes(xdmPttServerId);
			if (subsProvInputDTO.getPkgIdMap() != null) {
				if (subsProvInputDTO.getPkgIdMap().get(KnConstants.ADD_ACTION) != null
						&& !subsProvInputDTO.getPkgIdMap().get(KnConstants.ADD_ACTION).isEmpty()) {
					if (subsProvInputDTO.getPkgIdMap().get(KnConstants.REMOVE_ACTION) != null
							&& !subsProvInputDTO.getPkgIdMap().get(KnConstants.REMOVE_ACTION).isEmpty()) {
						finalPkgIdsMap = exsitingPkgIds;
						boolean status = false;
						Map<String, Integer> removePkgIds = subsProvInputDTO.getPkgIdMap()
								.get(KnConstants.REMOVE_ACTION);
						for (String pkgCode : removePkgIds.keySet()) {
							if (!exsitingPkgIds.containsKey(pkgCode.trim())
									|| (exsitingPkgIds.containsKey(pkgCode.trim()) && exsitingPkgIds.get(pkgCode.trim())
											.intValue() != removePkgIds.get(pkgCode).intValue())) {
								throw new KnProvBOException(KnErrorCodes.BOEntity.PKG_NOT_FOUND,
										"package id not configured as tier or Addon!!");
							}

							finalPkgIdsMap.remove(pkgCode.trim(), removePkgIds.get(pkgCode));
							knLogger.info(methodName, "Pkg code removed  ",pkgCode.trim() + " finalPkgIdsMap " + finalPkgIdsMap);
							status = true;
						}

						Map<String, Integer> addPkgIds = subsProvInputDTO.getPkgIdMap().get(KnConstants.ADD_ACTION);

						for (String pkgCode : addPkgIds.keySet()) {
							if (!exsitingPkgIds.containsKey(pkgCode.trim())) {
								List<String> qppPkgs = qppPkgCodes.stream().filter(x -> exsitingPkgIds.keySet().contains(x))
										.collect(Collectors.toList());
								if(qppPkgs.size() > 0 && qppPkgCodes.contains(pkgCode) )
								{
									throw new KnProvBOException(KnErrorCodes.BOEntity.QPPPKG_ASSIGNED,
											"qpp pkg already assign!!");
								}
								if (exsitingPkgIds.containsValue(KnConstants.TIER_PKG_TYPE)
										&& addPkgIds.get(pkgCode).intValue() == KnConstants.TIER_PKG_TYPE.intValue()) {
									throw new KnProvBOException(KnErrorCodes.BOEntity.TIERPKG_ASSIGNED,
											"tier pkg already assign!!");
								}

								finalPkgIdsMap.put(pkgCode.trim(), addPkgIds.get(pkgCode));
								knLogger.info(methodName, "Pkg code added  ",pkgCode.trim() + " finalPkgIdsMap " + finalPkgIdsMap);
								status = true;
							}

							if (exsitingPkgIds.containsKey(pkgCode.trim()) && exsitingPkgIds.get(pkgCode.trim())
									.intValue() != addPkgIds.get(pkgCode).intValue()) {
								throw new KnProvBOException(KnErrorCodes.BOEntity.PKG_NOT_FOUND,
										"package id not configured as tier or Addon!!");
							}
						}

						if (status)
							isPkgCodeUpdated = true;
					} else {
						finalPkgIdsMap = exsitingPkgIds;
						Map<String, Integer> newPkgIds = subsProvInputDTO.getPkgIdMap().get(KnConstants.ADD_ACTION);
						boolean status = false;
						for (String pkgCode : newPkgIds.keySet()) {
							if (!exsitingPkgIds.containsKey(pkgCode.trim())) {
								List<String> qppPkgs = qppPkgCodes.stream().filter(x -> exsitingPkgIds.keySet().contains(x))
										.collect(Collectors.toList());
								if(qppPkgs.size() > 0 && qppPkgCodes.contains(pkgCode) )
								{
									throw new KnProvBOException(KnErrorCodes.BOEntity.QPPPKG_ASSIGNED,
											"qpp pkg already assign!!");
								}
								if (exsitingPkgIds.containsValue(KnConstants.TIER_PKG_TYPE)
										&& newPkgIds.get(pkgCode).intValue() == KnConstants.TIER_PKG_TYPE.intValue()) {
									throw new KnProvBOException(KnErrorCodes.BOEntity.TIERPKG_ASSIGNED,
											"tier pkg already assign!!");
								}

								finalPkgIdsMap.put(pkgCode.trim(), newPkgIds.get(pkgCode));
								knLogger.info(methodName, "Pkg code added  ",pkgCode.trim() + " finalPkgIdsMap " + finalPkgIdsMap);
								status = true;
							}

							if (exsitingPkgIds.containsKey(pkgCode.trim()) && exsitingPkgIds.get(pkgCode.trim())
									.intValue() != newPkgIds.get(pkgCode).intValue()) {
								throw new KnProvBOException(KnErrorCodes.BOEntity.PKG_NOT_FOUND,
										"package id not configured as tier or Addon!!");
							}
						}

						if (status)
							isPkgCodeUpdated = true;
					}
				} else {
					if (subsProvInputDTO.getPkgIdMap().get(KnConstants.REMOVE_ACTION) != null
							&& !subsProvInputDTO.getPkgIdMap().get(KnConstants.REMOVE_ACTION).isEmpty()) {
						finalPkgIdsMap = exsitingPkgIds;
						Map<String, Integer> newPkgIds = subsProvInputDTO.getPkgIdMap()
								.get(KnConstants.REMOVE_ACTION);
						for (String pkgCode : newPkgIds.keySet()) {
							if (!exsitingPkgIds.containsKey(pkgCode.trim())
									|| (exsitingPkgIds.containsKey(pkgCode.trim()) && exsitingPkgIds.get(pkgCode.trim())
											.intValue() != newPkgIds.get(pkgCode).intValue())) {
								throw new KnProvBOException(KnErrorCodes.BOEntity.PKG_NOT_FOUND,
										"package id not configured as tier or Addon!!");
							}

							finalPkgIdsMap.remove(pkgCode.trim(), newPkgIds.get(pkgCode));
							knLogger.info(methodName, "Pkg code removed  ",pkgCode.trim() + " finalPkgIdsMap " + finalPkgIdsMap);
							isPkgCodeUpdated = true;
						}

					}
				}
			}
			String existingSubsFs2=existingSubsProfileDTO.getSubsFS2();
			int newSubsClientType = existingSubsProfileDTO.getSubsClientType();
			String provFS2=null;
			String provFS2BitMask=null;
			if (isPkgCodeUpdated) {
				knLogger.info(methodName, "change in pkgCode");
				BitSet finalFSBitSet = new BitSet(Long.SIZE);
				String basePkgCode = paramNameValueMap.get(KnConstants.BASE_PKGCODE);
				String basePkgCodeFS = featureSetUtil.getDefSubsFeatureSetForBasePkg(
						existingSubsProfileDTO.getPublicSubscriptionType(),
						existingSubsProfileDTO.getCorporateSubscriptionType(), newSubsClientType, basePkgCode,
						xdmPttServerId);
				knLogger.debug(methodName, "base pkg is applied - ", basePkgCodeFS);
				BitSet basePkgCodeBiSet = featureSetUtil.convertHexStringToBitSet(basePkgCodeFS);
				finalFSBitSet.or(basePkgCodeBiSet);
				String pkgCodeFS = featureSetUtil.getDefSubsFeatureSetForPkgCodes(
						existingSubsProfileDTO.getPublicSubscriptionType(),
						existingSubsProfileDTO.getCorporateSubscriptionType(), newSubsClientType, finalPkgIdsMap,
						xdmPttServerId);
				knLogger.debug(methodName, "PkgIds is present with subscriberFS - ", pkgCodeFS);
				BitSet pkgCodeBiSet = featureSetUtil.convertHexStringToBitSet(pkgCodeFS);
				finalFSBitSet.or(pkgCodeBiSet);
				int oldIntropBitStatus=findInterOPBitStatus(existingSubsProfileDTO, exisitngSubsPkgs, persisterTxn);

				if (finalFSBitSet.get(LMR_BIT) && LMR_BIT_STATUS.MANUALLY_DISABLED.Value()==oldIntropBitStatus) {
					finalFSBitSet.clear(LMR_BIT);
				} else if (!finalFSBitSet.get(LMR_BIT) && LMR_BIT_STATUS.MANUALLY_ENABLED.Value()==oldIntropBitStatus) {
					finalFSBitSet.set(LMR_BIT);
				}
				String newSubsFS2 = featureSetUtil.convertBitSetToHexString(finalFSBitSet);
				provFS2 = newSubsFS2;
				provFS2BitMask = newSubsFS2;
				knLogger.debug(methodName, "clientType or subsType or pkg code changed.. got default SubsFS :",
						newSubsFS2);
				existingSubsFs2 = featureSetUtil.generateSubsFeatureSet(newSubsFS2, provFS2, provFS2BitMask);
				isProfileUpdated = true;

			}
            //verifying requested bit and existing db bit are not same
            if(provFSMap!= null  && !provFSMap.isEmpty()){
            	List<com.kodiak.common.resources.KnConstants.PROV_FS_BIT> availableProvFSList = Arrays.asList(com.kodiak.common.resources.KnConstants.PROV_FS_BIT.values());
            	if(isProvFSBitAllowed(provFSMap)){
            		for(com.kodiak.common.resources.KnConstants.PROV_FS_BIT availableProvFS : availableProvFSList){
            			boolean featureIsEnabled=KnGeneralUtil.getFeatureBitValue(existingSubsFs2,availableProvFS.value());
            			 if (featureIsEnabled)
                             isprovFSEnabled= com.kodiak.common.resources.KnConstants.PROVFS_ENABLE.ENABLED.value();
                             for (Integer provFsKey: provFSMap.keySet()) {
                                 // if not equals to 1 that is enabled then
                                 if (! provFSMap.get(provFsKey).equals(isprovFSEnabled)) {
                                     isProfileUpdated = true;
                                     break;
                                 }
                             }
            		}
            	}
            }
            if(!isProfileUpdated){
            	knLogger.error(methodName, "No change in profile observed");
                throw new KnProvBOException(KnErrorCodes.BOEntity.NO_CHG_IN_PROFILE, "No change in profile observed");
            }
            BitSet bitset = new BitSet();
            if(provFSMap != null) {
                bitset= featureSetUtil.convertHexStringToBitSet(existingSubsFs2);
                for(Map.Entry<Integer,Integer> provEntry: provFSMap.entrySet()){
            	if(provEntry.getValue()== com.kodiak.common.resources.KnConstants.PROVFS_ENABLE.ENABLED.value()){
                    bitset.set(provEntry.getKey());
                }else{
                    bitset.clear(provEntry.getKey());
                }

            }
                existingSubsFs2=featureSetUtil.convertBitSetToHexString(bitset);

            }
            provFS2= featureSetUtil.convertBitSetToHexString(bitset);
            provFS2BitMask= provFS2;

            String subsFS2 = featureSetUtil.generateSubsFeatureSet(existingSubsFs2, provFS2, provFS2BitMask);
            String pocPttId = existingSubsProfileDTO.getPoCHome();
            String presencePttId = existingSubsProfileDTO.getPresenceHome();
            String xdmsPttId = existingSubsProfileDTO.getXDMSHome();
            int newCorpType = existingSubsProfileDTO.getCorporateSubscriptionType();
            String opsFS2 = existingSubsProfileDTO.getOpsFS2();
            String corpAdminFS2 = existingSubsProfileDTO.getCorpAdminFS2();
            String clientFS2 = existingSubsProfileDTO.getClientFS2();
            int corpId = existingSubsProfileDTO.getCorpId();


            String corpFS2 = null;
            if (corpId != 0) {
                String extCorpId = xdmServerDAO.retrieveExtCorporationId(existingSubsProfileDTO.getCorpId(), persisterTxn);
                knLogger.debug(methodName, "extcorpid", extCorpId);
                corpProfileInfoDTO = xdmServerDAO.retrieveCorporateProfile(extCorpId, false, persisterTxn);
                corpFS2 = corpProfileInfoDTO.getCorpFS2();
            }
            //fetch all pocpttserverid, presencepttid, etc from subs profile
            int clientPVMajorVersion=existingSubsProfileDTO.getClientPVmajorVer();
            String clientCapOverrideBitMask=featureSetUtil.getClientCapabilityBitMask(xdmPttServerId,clientPVMajorVersion);
            String activeFS2;
            String xdmsFs2=existingSubsProfileDTO.getXdmsFS2();
            String userProfileFS2 = existingSubsProfileDTO.getUserProfileFS2();
            if(userProfileFS2==null)
            {
            	userProfileFS2=featureSetUtil.getDefFinalUserProfileFS();
            }
            if (newCorpType == KnConstants.CORP_SUBSCRIPTION_TYPE.NONE.value()) {
                activeFS2 = featureSetUtil.generateActiveFeatBitSet(pocPttId, presencePttId, xdmsPttId, clientFS2, subsFS2, opsFS2,clientCapOverrideBitMask,xdmsFs2,userProfileFS2);
            } else {
                activeFS2 = featureSetUtil.generateActiveFeatBitSet(pocPttId, presencePttId, xdmsPttId, clientFS2, subsFS2, corpFS2, opsFS2, corpAdminFS2,clientCapOverrideBitMask,xdmsFs2,userProfileFS2);
            }
            lastProfileUpdateTime = Calendar.getInstance().getTimeInMillis();
            subsProfilePersistDTO = new KnSubsProfilePersistDTO();
            subsProfilePersistDTO.setMdn(mdn);
            subsProfilePersistDTO.setLastProfileUpdateTime(lastProfileUpdateTime);
            subsProfilePersistDTO.setSubsClientType(newSubsClientType);
            subsProfilePersistDTO.setSubsFS2(subsFS2);
            subsProfilePersistDTO.setActiveFS2(activeFS2);
            subsProfilePersistDTO.setXdmsFS2(xdmsFs2);
            subsProfilePersistDTO.setCorpId(corpId);
            subsProfilePersistDTO.setClientPVmajorVer(clientPVMajorVersion);

            String tierPackageId =allProfileDto!=null?allProfileDto.getTierPkgCode():null;
			Integer qppPkgId = existingSubsProfileDTO.getQppPkgId();
			Integer profileId = null;
			Integer dataPkgId = allProfileDto!=null?allProfileDto.getDataPkgId():null;
			Map<String, Integer> addonPackageIds = new HashMap<>();
			if (isPkgCodeUpdated) {
				String updatedTierPkg=null;
				knLogger.debug(methodName, "Updated pkg map in  pocsubsaddlinfo", finalPkgIdsMap);
				for (Entry<String, Integer> entry : finalPkgIdsMap.entrySet()) {
					if (entry.getValue().intValue() == KnConstants.TIER_PKG_TYPE.intValue()) {
						updatedTierPkg = entry.getKey();
					} else if (entry.getValue() == KnConstants.ADDON_PKG_TYPE.intValue()) {
						addonPackageIds.put(entry.getKey(), entry.getValue());

					}
				}
				tierPackageId=updatedTierPkg;
				if (!addonPackageIds.isEmpty()) {
					for (String addonPkgCode : addonPackageIds.keySet()) {
						profileId = featureSetUtil.getAddProfIdForPkg(addonPkgCode, xdmPttServerId);
						if (profileId != null) {
							qppPkgId = genInfoUtil.getDataPkgId(KnConstants.QPP_DATA_PKG_TYPE, persisterTxn).get(profileId);
							if (qppPkgId != null) {
								dataPkgId = qppPkgId;
								break;
							} else if (dataPkgId == null)
								dataPkgId = genInfoUtil.getDataPkgId(KnConstants.ADDON_DATA_PKG_TYPE, persisterTxn).get(profileId);
						}
					}

				}else {
					qppPkgId=null;
					dataPkgId=null;
				}
			}
			if (qppPkgId == null)
				qppPkgId = KnConstants.DEFAULT_QPP_ID;
			if (dataPkgId == null)
				dataPkgId = KnConstants.DEFAULT_DATAPKG_ID;
			if (profileId == null)
				profileId = KnConstants.DEFAULT_PROFILE_ID;

            IXDMServerDAO commonXDMServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
            int isAffiliationEnabled;
            boolean bitEnabled = KnGeneralUtil.getFeatureBitValue(subsProfilePersistDTO.getActiveFS2(), com.kodiak.common.resources.KnConstants.FEATURE_SET.AFFILIATIONFEATURE.value());
            if (subsProfilePersistDTO.getClientPVmajorVer() >= com.kodiak.xdms.server.common.resources.KnConstants.PROTOCOL_VERSION_18
                    && bitEnabled && checkClientType(subsProfilePersistDTO.getSubsClientType(), existingSubsProfileDTO.getMcpttCompliance())) {
                isAffiliationEnabled = ENABLED;
            } else {
                isAffiliationEnabled = DISABLED;
            }
            commonXDMServerDAO.updateAffForCorpGrpMemList(subsProfilePersistDTO.getMdn(), isAffiliationEnabled, persisterTxn);

			subsProfilePersistDTO.setQppPkgId(qppPkgId);
            xdmServerDAO.updateSubsFS(subsProfilePersistDTO, persisterTxn);

			if (isProfileUpdated && isPkgCodeUpdated) {
				xdmServerDAO.deleteSubAddlOnPkgs(mdn, persisterTxn);

				if (!addonPackageIds.isEmpty()) {
					xdmServerDAO.createSubAddOnPkgs(mdn, new ArrayList<>(addonPackageIds.keySet()), persisterTxn);
				}

				// -->. updating entry into the pocsubsaddlinfo
				KnSubsAddlInfoPersistDTO subsAddlProfilePersistDTO = new KnSubsAddlInfoPersistDTO();
				subsAddlProfilePersistDTO.setMdn(mdn);
				subsAddlProfilePersistDTO.setTierPkgCode(tierPackageId);
				subsAddlProfilePersistDTO.setDataPkgId(dataPkgId);
				Integer onBoardingMail = subsProvInputDTO.getOnBoardingEmailReqd();
                if (allProfileDto != null && allProfileDto.getOnBoardingMailReqd() != null
                        && allProfileDto.getOnBoardingMailReqd().equals(subsProvInputDTO.getOnBoardingEmailReqd())) {
                    onBoardingMail = allProfileDto.getOnBoardingMailReqd();
                }
                subsAddlProfilePersistDTO.setOnBoardingMailReqd(onBoardingMail);
                // check subscriber addlInfo exists
                if (allProfileDto != null) {
                    xdmServerDAO.updateSubscrPkgAddlInfo(subsAddlProfilePersistDTO, persisterTxn);
                } else {
                    subsAddlProfilePersistDTO.setTimeSlotType(KnConstants.TIME_SLOT_TYPE);
                    xdmServerDAO.createSubscrPkgAddlInfo(subsAddlProfilePersistDTO, persisterTxn);
                }
				responseDTO.setActiveFSChanged(true);
			}
            responseDTO.setCorpId(existingSubsProfileDTO.getCorpId());

            responseDTO.setActiveFS2(activeFS2);
            responseDTO.setPocPttId(pocPttId);
            responseDTO.setPresPttId(presencePttId);
            responseDTO.setResponseMessage(KnProvConstants.UPDATE_SUBS_PROFILE_SUCCESS);
            responseDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.SUCCESS.value());

            subsProfilePersistDTO.setLicenseType(existingSubsProfileDTO.getLicenseType());
            subsProfilePersistDTO.setMcpttCompliance(existingSubsProfileDTO.getMcpttCompliance());
            updateMCDevice(subsProfilePersistDTO, lastProfileUpdateTime,responseDTO, persisterTxn);

            int previousEtag = commonXDMServerDAO.getCurrentEtagForDirDoc(mdn, persisterTxn);
            commonXDMServerDAO.updateEtagForDirDoc(mdn, persisterTxn);
            String xcapRooturi = genInfoUtil.getXCAPRootURI(mdn, persisterTxn);
            KnOPDocChgDTO docChgDTO = new KnOPDocChgDTO();
            docChgDTO.setDocumentChgType(KnConstants.DOC_CHANGE_TYPE.REPLACE.value());
            String subsConfigDocUri = provInfoUtil.generateSubsConfigSelUri(mdn);
            docChgDTO.setDocUri(subsConfigDocUri);
            docChgDTO.setNewEtag(String.valueOf(lastProfileUpdateTime));

            Collection<KnOPDocChgDTO> docChgList = new ArrayList<KnOPDocChgDTO>();
            docChgList.add(docChgDTO);

            //populating Dir Document change DTO
            KnOPDirChgDTO dirChgDTO = new KnOPDirChgDTO();
            /*  KnXDMSServiceConfigDTO xdmsServiceConfigDTO = genInfoUtil.retrieveXDMSServiceConfig(xdmPttServerId, persisterTxn);
              //dirChgDTO.setXcapRootURI(xdmsServiceConfigDTO.getXcapRootUri());
            */
            dirChgDTO.setXcapRootURI(xcapRooturi);
            dirChgDTO.setPocHome(pocPttId);
            dirChgDTO.setPresenceHome(presencePttId);
            dirChgDTO.setDocChgDTO(docChgList);
            String dirDocUri = genInfoUtil.generateDirDocUri(mdn);
            dirChgDTO.setDirUri(dirDocUri);
            dirChgDTO.setDirPrevEtag(String.valueOf(previousEtag));
            int newEtag = previousEtag + 1;
            dirChgDTO.setDirNewEtag(String.valueOf(newEtag));

            //setting the Document Change DTO the response
            responseDTO.setDirChgDTO(dirChgDTO);


            knLogger.debug(methodName,"-[ responseDTO ]-",responseDTO ,"[ subsProvInputDTO  ]",subsProvInputDTO);

            knLogger.info(methodName, "EXIT: Update SubscriberFS operation ");
            return responseDTO;
        }
        catch (KnFeatureSetException ex) {
            knLogger.error(methodName, "Feature Set Exception occured :", ex);
            throw new KnProvBOException(ex.getErrorCode(), ex.getMessage(), ex);

        } catch (KnDBConnectionException ex) {
            knLogger.error(methodName, "DAO DBConnection Exception occured :", ex);
            throw new KnProvBOException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);

        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occurred : ", ex);
            knLogger.error(methodName, ex);
            if (com.kodiak.xdms.server.common.resources.KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_INFO_NOT_FOUND, "Subscriber does not exists");
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception occurred :", e);
            knLogger.error(methodName, e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while Update Subscriber");
            knLogger.error(methodName, e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred while update Subscriber", e);
        }
    }
    /**
     * method to update the Subscriber Profile.
     * --> populate the Subs profile persist DTO
     * --> Validate the input data
     * --> send the data to the DAO layer for updation of profile
     * --> on success populate the document DTO (for notification)
     * --> populate the response DTO.
     *
     * @param subsProvInputDTO KnIPSubsProvInfoDTO
     * @param persisterTxn     KnPersisterTxn
     * @return KnOPProvDTO response DTO
     * @throws KnProvBOException     BO Entity Exception
     * @throws KnValidationException Validation level exception
     */
    public KnOPUpdateSubsInfoDTO updateSubscriber(KnIPSubsProvInfoDTO subsProvInputDTO, KnPersisterTxn persisterTxn) throws KnProvBOException, KnValidationException {
        String methodName = "updateSubscriber(KnIPSubsProvInfoDTO, KnPersisterTxn)";
        boolean ownedTxn = false;
        KnOPUpdateSubsInfoDTO responseDTO = new KnOPUpdateSubsInfoDTO();
        KnSubsProfilePersistDTO subsProfilePersistDTO = new KnSubsProfilePersistDTO();
        Integer isprovFSEnabled= PROVFS_ENABLE.DISABLED.value();
        //same values of provFS1 and provFS1BitMask is used for calculation final subsFS after oring with bitmask
        knLogger.info(methodName, "ENTRY: Txn - ", persisterTxn);
        Collection<Integer> successPegs = new ArrayList<Integer>();
        boolean isUpgradePkg = false;

        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            try {
                xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
            } catch (KnBOException e) {
                knLogger.error(methodName, "failed to retrieve xdm Ptt Sever Id");
                knLogger.error(methodName, e);
                if(isUpgradePkg) {
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_UPGRADE_PACKAGE_WITH_CLIENT_CHANGE_FAILURE);
                }
                if (ownedTxn) {
                    rollback(persisterTxn);
                }
                throw new KnProvBOException(KnErrorCodes.BOEntity.XDMS_PTT_ID_NOT_FOUND, "Failed to retrieve XDM PTT Server ID", e);
            }

            KnSubsProfilePersistDTO validatePersistDTO = new KnSubsProfilePersistDTO();
            String mdn = subsProvInputDTO.getMdn();
            Integer reqSubsClientType = subsProvInputDTO.getSubsClientType();
            String reqUserId = subsProvInputDTO.getUserId();
            String extCorpId = subsProvInputDTO.getExtCorpId();
            //retrieve the Subscriber Profile.
            //retrieved Profile will used further for validation input data of the against to the input DTO
            KnOPSubsProfileInfoDTO existingSubsProfileDTO = provInfoUtil.retrieveSubscriberInfo(mdn, persisterTxn);
            Integer dbSubsClienType = existingSubsProfileDTO.getSubsClientType();
            String dbUserId = existingSubsProfileDTO.getUserId();
            //Validating extGatewayId
            if(null!=subsProvInputDTO.getExtGatewayId() && !subsProvInputDTO.getExtGatewayId().isEmpty()) {
                if (!provInfoUtil.validateExtGatewayId(subsProvInputDTO.getExtGatewayId(),persisterTxn)) {
                    knLogger.error(methodName, "External GatewayId does not exist : ", subsProvInputDTO.getExtGatewayId());
                    throw new KnProvBOException(KnErrorCodes.BOEntity.EXTERNAL_GATEWAY_ID_NOT_EXIST, "Invalid External Gateway Id passed");
                }
            }
            if((reqSubsClientType!=null&&KnProvConstants.SUBS_CLIENT_TYPE.DISPATCH_CLIENT.value()==reqSubsClientType&&reqUserId!=null)
                    ||(dbSubsClienType!=null&&KnProvConstants.SUBS_CLIENT_TYPE.DISPATCH_CLIENT.value()==dbSubsClienType&&(dbUserId==null||dbUserId.isEmpty()))){
                subsProvInputDTO.setUserId(reqUserId);
                knLogger.debug(methodName,"setting userid null for dispatcher client :",subsProvInputDTO.getUserId());
            }

            String mcpttId_DB = existingSubsProfileDTO.getMcpttId();
			  String mcpttId_Input = subsProvInputDTO.getMcpttIdFromToken();
	            knLogger.debug( methodName, "mcpttId_DB::"+KnGDPRTemplate.mcpttId(mcpttId_DB));
	            knLogger.debug( methodName, "mcpttId_Input::"+KnGDPRTemplate.mcpttId(mcpttId_Input));

			  
				if (mcpttId_Input != null && mcpttId_DB != null)
			{
				if (existingSubsProfileDTO.getMcpttCompliance() == 0) {
					if (!mcpttId_Input.isEmpty() && !mcpttId_DB.isEmpty()
							&& !(mcpttId_Input.trim().equals(mcpttId_DB.substring(5).trim()) ||mcpttId_Input.trim().equals(mcpttId_DB.trim()))) {
						 knLogger.debug(methodName, "MCPTTD is not matched with the token");
						throw new KnProvBOException(KnErrorCodes.Validator.INVALID_TOKEN_MCPTTID,
								"MCPTTD is not matched with the token");
					}
				} else if (existingSubsProfileDTO.getMcpttCompliance() == 1) {
					if (!mcpttId_Input.isEmpty() && !mcpttId_DB.isEmpty() && !mcpttId_Input.trim().equals(mcpttId_DB.trim())) {
						 knLogger.debug(methodName, "MCPTTD is not matched with the token");
						throw new KnProvBOException(KnErrorCodes.Validator.INVALID_TOKEN_MCPTTID,
								"MCPTTD is not matched with the token");
					}
				}
			}

            //check if the mdn is a pseudo mdn then restrict the operation
           /* if(existingSubsProfileDTO.getPamAccId() != null && existingSubsProfileDTO.getPamAccId()!= 0){
                knLogger.error(methodName, "Mdn is present as a pseudo number,operation not allowed");
                throw new KnProvBOException(KnErrorCodes.BOEntity.MDN_PRESENT_AS_PSEUDOMDN_IN_POCSUBSCRINFO,
                        "Mdn is present as a pseudo mdn in POCSUBSCRINFO");
            }*/
            //  String existingExtCorpId = provInfoUtil.retrieveExtCorpId(existingSubsProfileDTO.getCorpId());
            if (existingSubsProfileDTO.getSubsClientType() == 11) {
                knLogger.error(methodName, "CLient Type of PDV will not support");
                throw new KnProvBOException(KnErrorCodes.Validator.INVALID_SUBS_CLIENT_TYPE, "subsClientType," + existingSubsProfileDTO.getSubsClientType());
            }


            KnSubsCameraInfo cameraInfo = subsProvInputDTO.getCameraInfo();
            if (existingSubsProfileDTO.getSubsClientType() != KnProvConstants.SUBS_CLIENT_TYPE.STANDALONECAMERA.value()) {
                if(subsProvInputDTO.getCameraType() != null||cameraInfo!=null){
                    knLogger.error(methodName, " Not a STANDALONECAMERA client ");
                    throw new KnProvBOException(KnErrorCodes.BOEntity.NOT_STANDALONECAMERA_CLIENT, "subsClientType," + existingSubsProfileDTO.getSubsClientType());
                }
            }

            validatePersistDTO.setInputDTO(subsProvInputDTO);
            validatePersistDTO.setMdn(existingSubsProfileDTO.getMdn());
            validatePersistDTO.setIMEI(existingSubsProfileDTO.getIMEI());
            validatePersistDTO.setPublicSubscriptionType(subsProvInputDTO.getPublicSubscriptionType());
            validatePersistDTO.setCorporateSubscriptionType(subsProvInputDTO.getCorporateSubscriptionType());
            validatePersistDTO.setPayType(subsProvInputDTO.getPayType());
            validatePersistDTO.setExtCorpId(subsProvInputDTO.getExtCorpId());
            validatePersistDTO.setPairingInd(existingSubsProfileDTO.getPairingInd());
            validatePersistDTO.setServiceAuthStatus(existingSubsProfileDTO.getServiceAuthStatus());
            validatePersistDTO.setRoamingTypes(subsProvInputDTO.getRoamingTypes());
            validatePersistDTO.setSubsClientType(subsProvInputDTO.getSubsClientType());
            validatePersistDTO.setFirstNetIndicator(subsProvInputDTO.getFirstNetIndicator());

            //invoking the validation FW
            validatorFwk.validate(validatePersistDTO);

            Map<String, String> paramNameValueMap = genInfoUtil.retrieveRTXConfigValues(persisterTxn);

            //validate the proper values while updating the subscription Type
            int oldCorpType = existingSubsProfileDTO.getCorporateSubscriptionType();
            int newCorpType = subsProvInputDTO.getCorporateSubscriptionType();

            int oldPublicType = existingSubsProfileDTO.getPublicSubscriptionType();
            int newPublicType = subsProvInputDTO.getPublicSubscriptionType();

            //variables to identify the profile level changes
            boolean isProfileUpdated = false;
            boolean isActiveFSUpdated = false;
            boolean isNameUpdated = false;
            boolean isSubsTypeUpdated = false;
            boolean isExtCorpIdUpdated = false;
            boolean isCorpNameUpdated = false;
            boolean isClientTypeUpdated = false;
            boolean isDispGrpMemUpdated = false;
            boolean isUFMIChange = false;
            boolean isPkgCodeUpdated = false;
            int upgradePkgClientType = 0;
            IProvXDMServerDAO xdmServerDAO = KnProvFactorySelector.getProvDAOFactory(KnFactorySelector.DB).createProvXDMServerDAO();
			// retrieve tier pkg & addon pkg
			Map<String, Integer> exsitingPkgIds = new HashMap<String, Integer>();
			Map<String, Integer> exsitingSubsPkgs = new HashMap<String, Integer>();
			String existingTierPkg = null;
			KnOPSubsAddlInfoProfileDTO allProfileDto = xdmServerDAO.retrieveSubscrAddlInfo(subsProvInputDTO.getMdn(),
					persisterTxn);
			if (allProfileDto != null)
				existingTierPkg = allProfileDto.getTierPkgCode();
			if (existingTierPkg != null)
				exsitingPkgIds.put(existingTierPkg, KnConstants.TIER_PKG_TYPE);
			List<String> existingAddonPkgs = xdmServerDAO.selectSubAddOnPkgs(subsProvInputDTO.getMdn(), persisterTxn);
			if (existingAddonPkgs != null) {
				for (String pkgCode : existingAddonPkgs) {
					exsitingPkgIds.put(pkgCode, KnConstants.ADDON_PKG_TYPE);
				}
			}
			knLogger.info(methodName, "PkgIdsMap ",
					subsProvInputDTO.getPkgIdMap() + " exsitingPkgIds " + exsitingPkgIds);
			exsitingSubsPkgs=new HashMap<>(exsitingPkgIds);
			knLogger.info(methodName, " exsitingSubsPkgs " + exsitingSubsPkgs);
			Map<String, Integer> finalPkgIdsMap = new HashMap<String, Integer>();
			List<String> qppPkgCodes=featureSetUtil.getQPPPkgCodes(xdmPttServerId);
			if (subsProvInputDTO.getPkgIdMap() != null) {
				if (subsProvInputDTO.getPkgIdMap().get(KnConstants.ADD_ACTION) != null
						&& !subsProvInputDTO.getPkgIdMap().get(KnConstants.ADD_ACTION).isEmpty()) {
					if (subsProvInputDTO.getPkgIdMap().get(KnConstants.REMOVE_ACTION) != null
							&& !subsProvInputDTO.getPkgIdMap().get(KnConstants.REMOVE_ACTION).isEmpty()) {
						finalPkgIdsMap = exsitingPkgIds;
						boolean status = false;
						Map<String, Integer> removePkgIds = subsProvInputDTO.getPkgIdMap()
								.get(KnConstants.REMOVE_ACTION);
						for (String pkgCode : removePkgIds.keySet()) {
							if (!exsitingPkgIds.containsKey(pkgCode.trim())
									|| (exsitingPkgIds.containsKey(pkgCode.trim()) && exsitingPkgIds.get(pkgCode.trim())
											.intValue() != removePkgIds.get(pkgCode).intValue())) {
								throw new KnProvBOException(KnErrorCodes.BOEntity.PKG_NOT_FOUND,
										"package id not configured as tier or Addon!!");
							}

							finalPkgIdsMap.remove(pkgCode.trim(), removePkgIds.get(pkgCode));
							knLogger.info(methodName, "Pkg code removed  ",pkgCode.trim() + " finalPkgIdsMap " + finalPkgIdsMap);
							status = true;
						}

						Map<String, Integer> addPkgIds = subsProvInputDTO.getPkgIdMap().get(KnConstants.ADD_ACTION);

						for (String pkgCode : addPkgIds.keySet()) {
							if (!exsitingPkgIds.containsKey(pkgCode.trim())) {
								List<String> qppPkgs = qppPkgCodes.stream().filter(x -> exsitingPkgIds.keySet().contains(x))
										.collect(Collectors.toList());
								if(qppPkgs.size() > 0 && qppPkgCodes.contains(pkgCode) )
								{
									throw new KnProvBOException(KnErrorCodes.BOEntity.QPPPKG_ASSIGNED,
											"qpp pkg already assign!!");
								}
								if (exsitingPkgIds.containsValue(KnConstants.TIER_PKG_TYPE)
										&& addPkgIds.get(pkgCode).intValue() == KnConstants.TIER_PKG_TYPE) {
									throw new KnProvBOException(KnErrorCodes.BOEntity.TIERPKG_ASSIGNED,
											"tier pkg already assign!!");
								}

								finalPkgIdsMap.put(pkgCode.trim(), addPkgIds.get(pkgCode));
								knLogger.info(methodName, "Pkg code added  ",pkgCode.trim() + " finalPkgIdsMap " + finalPkgIdsMap);
								status = true;
							}

							if (exsitingPkgIds.containsKey(pkgCode.trim()) && exsitingPkgIds.get(pkgCode.trim())
									.intValue() != addPkgIds.get(pkgCode).intValue()) {
								throw new KnProvBOException(KnErrorCodes.BOEntity.PKG_NOT_FOUND,
										"package id not configured as tier or Addon!!");
							}
						}
                        int clientType = existingSubsProfileDTO.getSubsClientType();
                        if (subsProvInputDTO.getHierarchyType() == HIERARCHY_TYPE.NON_HIERARCHY && exsitingSubsPkgs.containsKey(COLLABORATION) || exsitingSubsPkgs.containsKey(COMMAND)) {
                            if (clientType == SUBS_CLIENT_TYPE.HANDSET.value()
                                    || clientType == SUBS_CLIENT_TYPE.POC_WIFIONLY.value()
                                    || clientType == SUBS_CLIENT_TYPE.CROSSCARRIER.value()) {
                                boolean clientUpdated = false;
                                String pkgCode = finalPkgIdsMap.keySet().iterator().next();
                                knLogger.debug(methodName, "pkgCode: ", pkgCode);
                                String defSubsFS2 = featureSetUtil.retrieveDefSubsFS2(pkgCode, persisterTxn);
                                boolean userProfileBitEnabled = KnGeneralUtil.getFeatureBitValue(defSubsFS2, FEATURE_SET.USER_PROFILE_MGMT_BIT.value());
                                knLogger.debug(methodName, "upgradePkgCode: ", userProfileBitEnabled);
                                if (userProfileBitEnabled) {
                                    if (clientType == SUBS_CLIENT_TYPE.HANDSET.value()) {
                                        upgradePkgClientType = SUBS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value();
                                        clientUpdated = true;
                                    } else if (clientType == SUBS_CLIENT_TYPE.POC_WIFIONLY.value()) {
                                        upgradePkgClientType = SUBS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value();
                                        clientUpdated = true;
                                    } else if (clientType == SUBS_CLIENT_TYPE.CROSSCARRIER.value()) {
                                        upgradePkgClientType = SUBS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value();
                                        clientUpdated = true;
                                    }
                                }
                                knLogger.debug(methodName, "clientUpdated: ", clientUpdated);
                                if (clientUpdated) {
                                    isUpgradePkg = true;
                                    responseDTO.setUpgradePkg(isUpgradePkg);
                                }
                            }
                        }
                        if (status)
                            isPkgCodeUpdated = true;
					} else {
						finalPkgIdsMap = exsitingPkgIds;
						Map<String, Integer> newPkgIds = subsProvInputDTO.getPkgIdMap().get(KnConstants.ADD_ACTION);
						boolean status = false;
						for (String pkgCode : newPkgIds.keySet()) {
							if (!exsitingPkgIds.containsKey(pkgCode.trim())) {
								List<String> qppPkgs = qppPkgCodes.stream().filter(x -> exsitingPkgIds.keySet().contains(x))
										.collect(Collectors.toList());
								if(qppPkgs.size() > 0 && qppPkgCodes.contains(pkgCode) )
								{
									throw new KnProvBOException(KnErrorCodes.BOEntity.QPPPKG_ASSIGNED,
											"qpp pkg already assign!!");
								}
								if (exsitingPkgIds.containsValue(KnConstants.TIER_PKG_TYPE)
										&& newPkgIds.get(pkgCode).intValue() == KnConstants.TIER_PKG_TYPE) {
									throw new KnProvBOException(KnErrorCodes.BOEntity.TIERPKG_ASSIGNED,
											"tier pkg already assign!!");
								}

								finalPkgIdsMap.put(pkgCode.trim(), newPkgIds.get(pkgCode));
								knLogger.info(methodName, "Pkg code added  ",pkgCode.trim() + " finalPkgIdsMap " + finalPkgIdsMap);
								status = true;
							}

							if (exsitingPkgIds.containsKey(pkgCode.trim()) && exsitingPkgIds.get(pkgCode.trim())
									.intValue() != newPkgIds.get(pkgCode).intValue()) {
								throw new KnProvBOException(KnErrorCodes.BOEntity.PKG_NOT_FOUND,
										"package id not configured as tier or Addon!!");
							}
						}

						if (status)
							isPkgCodeUpdated = true;
					}
				} else {
					if (subsProvInputDTO.getPkgIdMap().get(KnConstants.REMOVE_ACTION) != null
							&& !subsProvInputDTO.getPkgIdMap().get(KnConstants.REMOVE_ACTION).isEmpty()) {
						finalPkgIdsMap = exsitingPkgIds;
						Map<String, Integer> newPkgIds = subsProvInputDTO.getPkgIdMap()
								.get(KnConstants.REMOVE_ACTION);
						for (String pkgCode : newPkgIds.keySet()) {
							if (!exsitingPkgIds.containsKey(pkgCode.trim())
									|| (exsitingPkgIds.containsKey(pkgCode.trim()) && exsitingPkgIds.get(pkgCode.trim())
											.intValue() != newPkgIds.get(pkgCode).intValue())) {
								throw new KnProvBOException(KnErrorCodes.BOEntity.PKG_NOT_FOUND,
										"package id not configured as tier or Addon!!");
							}

							finalPkgIdsMap.remove(pkgCode.trim(), newPkgIds.get(pkgCode));
							knLogger.info(methodName, "Pkg code removed  ",pkgCode.trim() + " finalPkgIdsMap " + finalPkgIdsMap);
							isPkgCodeUpdated = true;
						}

					}
				}

			}
			if (isPkgCodeUpdated) {
				knLogger.info(methodName, "change in pkgCode");
				isProfileUpdated = true;
			}
            //verify if there is a change in the Subscriber Profile if not return an error
            // parameters to be verified are as follows
            // 1. NetworkName, 2. PayType 3. RoamingType 4. SubscriptionType 5. Affiliate Id
            //6. EXT CORP ID, 7. SUBS CLIENT TYPE, 8. EMAIL  9.IMEI

            //verifying if IMEI  is changed
            String newImei = null;
            if (subsProvInputDTO.getIMEI() != null) {
                newImei = subsProvInputDTO.getIMEI().trim();
            }
            String oldImei = null;
            if (existingSubsProfileDTO.getIMEI() != null) {
                oldImei = existingSubsProfileDTO.getIMEI().trim();
            }

            if (newImei != null && (!newImei.equalsIgnoreCase(oldImei))) {
                knLogger.debug(methodName, "change in IMEI");
                isProfileUpdated = true;
            }

          //verifying if firstNetIndicator  is changed
            String newFirstNetIndicator = null;
            if (subsProvInputDTO.getFirstNetIndicator() != null) {
            	newFirstNetIndicator = subsProvInputDTO.getFirstNetIndicator().trim();
            }
            String oldFirstNetIndicator = null;
            if (existingSubsProfileDTO.getFirstNetIndicator() != null) {
            	oldFirstNetIndicator = existingSubsProfileDTO.getFirstNetIndicator().trim();
            }

            if (newFirstNetIndicator != null && (!newFirstNetIndicator.equalsIgnoreCase(oldFirstNetIndicator))) {
                knLogger.debug(methodName, "change in oldFirstNetIndicator");
                isProfileUpdated = true;
            }

            //verifying if network name is changed
            String oldNetworkName = existingSubsProfileDTO.getNetworkName().trim();
            String newNetworkName = subsProvInputDTO.getNetworkName();
//            if (newNetworkName != null) {
//                newNetworkName = new String(newNetworkName.getBytes(ENCODING_UTF8), ENCODING_8859);
//            }
            if (oldNetworkName != null && newNetworkName != null) {
                if (!oldNetworkName.equals(newNetworkName)) {
                    knLogger.debug(methodName, "change in network name");
                    successPegs.add(KnOMConstants.XDM_NUM_SUBSCR_NAME_CHANGE);
                    // isNameOrStateUpdated = true;
                    isNameUpdated = true;
                    isProfileUpdated = true;
                }
            }

            //verify if UFMI has got changed

			String oldUFMI = existingSubsProfileDTO.getUfmi();
			String newUFMI = subsProvInputDTO.getUfmi();

			if (newUFMI != null) {
				if (!newUFMI.equals(oldUFMI)) {
					knLogger.debug(methodName, "Change in UFMI");
					isUFMIChange = true;
				}
			}

			String oldIdenUserName = existingSubsProfileDTO.getiDenUserName();
			String newIdenUserName = subsProvInputDTO.getiDenUserName();

			if (newIdenUserName != null) {
				if (!newIdenUserName.equals(oldIdenUserName)) {
					knLogger.debug(methodName, "Change in idenUserName");
					isUFMIChange = true;
				}
			}

			String oldIdenPassword = existingSubsProfileDTO.getiDenPassword();
			String newIdenPassword = subsProvInputDTO.getiDenPassword();

			if (newIdenPassword != null) {
				if (!newIdenPassword.equals(oldIdenPassword)) {
					knLogger.debug(methodName, "Change in idenPassword");
					isUFMIChange = true;
				}
			}

			String oldIdenBusUnitId = existingSubsProfileDTO.getiDenBusUnitId();
			String newIdenBusUnitId = subsProvInputDTO.getiDenBusUnitId();

			if (newIdenBusUnitId != null) {
				if (!newIdenBusUnitId.equals(oldIdenBusUnitId)) {
					knLogger.debug(methodName, "Change in idenBusUnitId");
					isUFMIChange = true;
				}
			}
            //verify if pay Type has got changed
            int oldPayType = existingSubsProfileDTO.getPayType();
            int newPayType = subsProvInputDTO.getPayType();
            if (newPayType != -1) {
                if (oldPayType != newPayType) {
                    knLogger.debug(methodName, "Change in Pay Type");
                    isProfileUpdated = true;
                }
            }

            //verify if roaming Type has got changed
            ArrayList<Integer> newRoamingTypeList = subsProvInputDTO.getRoamingTypes();
            List<Integer> oldRoamingTypes = provInfoUtil.retrieveSubscrRoamingProfile(mdn, persisterTxn);

            Map<Integer, String> roamingTypeInfo = provInfoUtil.retrieveSupportedRoamingList();
            ArrayList<Integer> oldRoamingTypeList = new ArrayList<Integer>();
            for (int oldRoamingId : oldRoamingTypes) {
                if (roamingTypeInfo.get(oldRoamingId) != null) {
                    oldRoamingTypeList.add(Integer.valueOf(roamingTypeInfo.get(oldRoamingId)));
                }
            }


            if (newRoamingTypeList != null) {
                if (!newRoamingTypeList.contains(-1)) {
                    for (int newRoamingType : newRoamingTypeList) {
                        if (!oldRoamingTypeList.contains(newRoamingType) || (oldRoamingTypeList.contains(newRoamingType) && oldRoamingTypeList.size() != newRoamingTypeList.size())) {
                            knLogger.debug(methodName, "Roaming Type change is observed");
                            isProfileUpdated = true;
                        }
                    }
                } else {
                    knLogger.debug(methodName, "Roaming Type change is observed");
                    isProfileUpdated = true;
                }
            }

            //verify if there is a change in the subscription Type

            if ((newPublicType == 0 && newCorpType == 0) || (newPublicType == -1 && newCorpType == -1)) {
                newPublicType = oldPublicType;
                newCorpType = oldCorpType;
            }
            String newExtCorpId = subsProvInputDTO.getExtCorpId();
            String oldAccountId = null;
            String newAccountId = null;
            if (subsProvInputDTO.getAccountId() != null) {
                newAccountId = subsProvInputDTO.getAccountId().trim();
            }
            if (existingSubsProfileDTO.getAccountId() != null) {
                oldAccountId = existingSubsProfileDTO.getAccountId().trim();
            }

            if (newAccountId != null && (!newAccountId.equalsIgnoreCase(oldAccountId))) {
                knLogger.error(methodName, "Account Id cannot be changed ");
                throw new KnProvBOException(KnErrorCodes.BOEntity.ACCOUNT_ID_CHANGE_NOT_ALLOWED,
                        "Account Id Change not Allowed");
            }
            if (newPublicType != -1) {
                if (oldPublicType != newPublicType) {
                    knLogger.debug(methodName, "Public Subscription type has got changed");
                    isSubsTypeUpdated = true;
                    isProfileUpdated = true;
                }
            }

            if (newCorpType != -1) {
                if (oldCorpType != newCorpType) {
                    knLogger.debug(methodName, "Corp Subscription type has got changed");
                    if (newExtCorpId == null && oldAccountId == null) {
                        knLogger.error(methodName, "Ext Corp ID cannot be null for a corporate subscriber");
                        throw new KnProvBOException(KnErrorCodes.BOEntity.EXTCORPID_CANNOT_BE_NULL_FOR_CORP_SUBS,
                                "Ext Corp ID cannot be null for a corporate subscriber");
                    }
                    isSubsTypeUpdated = true;
                    isProfileUpdated = true;
                }
            }
            if ((oldCorpType == newCorpType && oldPublicType != newPublicType)){
                isSubsTypeUpdated = true;
                knLogger.debug(methodName, "Subscription type has got changed");
            }
               else if(!(oldCorpType == newCorpType && oldPublicType == newPublicType))
             {
                isSubsTypeUpdated = false;
                knLogger.error(methodName, "Subscription type change not allowed");
                throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIPTION_TYPE_CHANGE_NOT_ALLOWED,
                    "SubscriptionType change is not allowed");
              }



            //verify if there is a change in the Affiliate Id
            String newAffiliateId = null;
            if (subsProvInputDTO.getAffiliateId() != null) {
                newAffiliateId = subsProvInputDTO.getAffiliateId().trim();
            }
            String oldAffiliateId = null;
            if (existingSubsProfileDTO.getAffiliateId() != null) {
                oldAffiliateId = existingSubsProfileDTO.getAffiliateId().trim();
            }

            if (newAffiliateId != null && (!newAffiliateId.equalsIgnoreCase(oldAffiliateId))) {
                knLogger.debug(methodName, "change in affiliate id");
                isProfileUpdated = true;
            }

            String newEMail = subsProvInputDTO.getEmailAddress();
            String oldEMail = existingSubsProfileDTO.getEmailAddress();
            if (newEMail != null && (!newEMail.equalsIgnoreCase(oldEMail))) {
                knLogger.debug(methodName, "change in eMail");
                isProfileUpdated = true;
            }
            String oldGatwayId = existingSubsProfileDTO.getExtGatewayId();
            String newGatewayId = subsProvInputDTO.getExtGatewayId();
            if (newGatewayId != null && (!newGatewayId.equalsIgnoreCase(oldGatwayId))) {
                knLogger.debug(methodName, "change in gatewayId");
                isProfileUpdated = true;
            }
            int newSubsClientType = existingSubsProfileDTO.getSubsClientType();
            int oldSubsClientType = existingSubsProfileDTO.getSubsClientType();

            if (subsProvInputDTO.getSubsClientType() != null) {
                newSubsClientType = subsProvInputDTO.getSubsClientType();
            }

            int newDispatchGrpMember = existingSubsProfileDTO.getDispatchGroupMember();
            //value will be there whenever there is change
            if (subsProvInputDTO.getDispatchGroupMember() != null) {
                isDispGrpMemUpdated = true;
                newDispatchGrpMember = subsProvInputDTO.getDispatchGroupMember();
                isProfileUpdated = true;
            }


            String newClientPassword = existingSubsProfileDTO.getClientPassword();
			// verify that if the client Type has been changed form HANDSET to PTTRADIOHANDSETCLIENT and vice versa
			boolean isPTTRadioClientTypeChange = true;
			if (oldSubsClientType != newSubsClientType)
			{
				String str = ""+SUBS_CLIENT_TYPE.HANDSET.value()+"|"+SUBS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value()+","+SUBS_CLIENT_TYPE.CROSSCARRIER.value()+"|"+SUBS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value()+","+SUBS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value()+"|"+SUBS_CLIENT_TYPE.POC_WIFIONLY.value()+"";
				String[] regEx1 = str.split(",");
				for(int i=0;i<regEx1.length;i++) {
				    if(String.valueOf(oldSubsClientType).matches(regEx1[i]) && String.valueOf(newSubsClientType).matches(regEx1[i])){
				    	isPTTRadioClientTypeChange = false;
						knLogger.info(methodName, "Client type change skipped");
				    }
				}

			}

            // verify if the client Type has been changed.
            if (oldSubsClientType != newSubsClientType && isPTTRadioClientTypeChange) {
                knLogger.error(methodName, "Client type cannot be changed ");
                throw new KnProvBOException(KnErrorCodes.Validator.INVALID_SUBS_CLIENT_TYPE,"Client type cannot be changed ");
            }

            String corpFS2 = null;
            String pocPttId = existingSubsProfileDTO.getPoCHome();
            String presencePttId = existingSubsProfileDTO.getPresenceHome();
            String xdmsPttId = existingSubsProfileDTO.getXDMSHome();

            String pocPTTIdActiveFS2 = pocPttId;
            if (pocPttId == null || pocPttId.isEmpty() || "0".equals(pocPttId)) {
                knLogger.debug(methodName, "Invalid pocPttId [", pocPttId, "] for MDN ", mdn, ", using fallback [", xdmsPttId, "]");
                pocPTTIdActiveFS2 = xdmsPttId;
            }
            String presencePTTIdActiveFS2 = presencePttId;
            if (presencePttId == null || presencePttId.isEmpty() || "0".equals(presencePttId)) {
                knLogger.debug(methodName, "Invalid presencePttId [", presencePttId, "] for MDN ", mdn, ", using fallback [", xdmsPttId, "]");
                presencePTTIdActiveFS2 = xdmsPttId;
            }

            String clientFS2=null;
            if (subsProvInputDTO.getClientFS2() != null) {
            	clientFS2 = subsProvInputDTO.getClientFS2();
                knLogger.debug(methodName, "change in ClientFS1. generateClientFeatureSet1 :", clientFS2);
                isProfileUpdated = true;
            }
            else {
            	clientFS2 = existingSubsProfileDTO.getClientFS2();
            }
            Map<Integer, Integer> provFSMap = subsProvInputDTO.getProvFSMap();
            String existingSubsFs=existingSubsProfileDTO.getSubsFS2();
            knLogger.debug("update :provFSMap ",provFSMap);

            if(provFSMap!= null  && !provFSMap.isEmpty()){
                if(isProvFSBitAllowed(provFSMap)){
                    List<com.kodiak.common.resources.KnConstants.PROV_FS_BIT> availableProvFSList = Arrays.asList(com.kodiak.common.resources.KnConstants.PROV_FS_BIT.values());
                    for(com.kodiak.common.resources.KnConstants.PROV_FS_BIT availableProvFS : availableProvFSList)
                    {
                        boolean featureIsEnabled=KnGeneralUtil.getFeatureBitValue(existingSubsFs,availableProvFS.value());
                        if (featureIsEnabled )
                            isprovFSEnabled= com.kodiak.common.resources.KnConstants.PROVFS_ENABLE.ENABLED.value();

                        if(provFSMap !=null ){
                            for (Integer provFsKey: provFSMap.keySet()) {
                                // if not equals to 1 that is enabled then
                                if (! provFSMap.get(provFsKey).equals(isprovFSEnabled)) {
                                    isProfileUpdated = true;
                                    break;
                                }
                            }
                        }
                    }
                }else {
                    knLogger.error(methodName, "Operation not allowed for feature bit(s)!");
                    throw new KnProvBOException(KnErrorCodes.Validator.INVALID_PROV_FS, "Requested feature bit(s) is/are not allowed to enable/disable via create/update.");
                }
            }




                    String oldExtCorpId = null;
            try {
                oldExtCorpId = xdmServerDAO.retrieveExtCorporationId(existingSubsProfileDTO.getCorpId(), persisterTxn);
                knLogger.debug(methodName, "Old Ext Corp ID - ", oldExtCorpId);
            } catch (KnDAOException e) {
                if(isUpgradePkg) {
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_UPGRADE_PACKAGE_WITH_CLIENT_CHANGE_FAILURE);
                }
                if (com.kodiak.xdms.server.common.resources.KnErrorCodes.DAO.SERVER_ERROR.equals(e.getErrorCode())) {
                    knLogger.debug(methodName, "No Old Corporate Profile exists");
                }
            }

            if (newCorpType == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value() ||
                    oldCorpType == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
                //--> check if the corp subscription Type has been updated.
                if (oldCorpType != newCorpType) {
                    isSubsTypeUpdated = true;

                    // isNameOrStateUpdated = true;
                    isProfileUpdated = true;
                }
                // check if the new Ext Corp ID is passed when subscription type changed from non-corp to corp
                if (oldCorpType == KnConstants.CORP_SUBSCRIPTION_TYPE.NONE.value() &&
                        newCorpType == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
                    knLogger.debug(methodName, "Corp subscription type of subs changed from NONE to CORPORATE"
                    );
                    if (newExtCorpId == null && oldAccountId == null) {
                        knLogger.error(methodName, "Ext Corp ID cannot be null for a corporate subscriber"
                        );
                        throw new KnProvBOException(KnErrorCodes.BOEntity.EXTCORPID_CANNOT_BE_NULL_FOR_CORP_SUBS,
                                "Ext Corp ID cannot be null for a corporate subscriber");
                    } else if (newExtCorpId == null) {
                        newExtCorpId = oldAccountId.trim();
                        knLogger.debug(methodName, "assiging public account id to ext corpid");
                        isExtCorpIdUpdated = true;
                    } else {
                        knLogger.debug(methodName, "Corp subs type change from none to corp ",
                                "hence ext corp id has been updated");
                        isExtCorpIdUpdated = true;
                    }
                }

                if (oldCorpType == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value() &&
                        newCorpType == KnConstants.CORP_SUBSCRIPTION_TYPE.NONE.value()) {
                    isExtCorpIdUpdated = true;
                    newExtCorpId = null;      //PR INT52567 Fix:
                    if (newSubsClientType != KnProvConstants.SUBS_CLIENT_TYPE.HANDSET.value()) {
                        knLogger.debug(methodName, "Invalid client type passed for public subscriber");
                        throw new KnProvBOException(KnErrorCodes.BOEntity.INVALID_CLIENT_TYPE_FOR_PUBLIC_SUBS,
                                "Invalid client type passed for public subscriber");

                    }
                } else {
                    if (newExtCorpId == null) {
                        newExtCorpId = oldExtCorpId;
                    }
                }

                knLogger.debug(methodName, "oldExtCorpId :", oldExtCorpId, "  newExtCorpId:", newExtCorpId);

                if (oldExtCorpId != null) {
                    if (!oldExtCorpId.equals(newExtCorpId)) {
                        knLogger.debug(methodName, "EXT CORP ID change detected");
                        isProfileUpdated = true;
                        isExtCorpIdUpdated = true;
                    }
                } else if (newExtCorpId != null) {
                    if (!newExtCorpId.equals(oldExtCorpId)) {
                        knLogger.debug(methodName, "EXT CORP ID change detected");
                        isProfileUpdated = true;
                        isExtCorpIdUpdated = true;
                    }
                }
            }

            // etag check for update in state or subscriber name
            if (isNameUpdated || isSubsTypeUpdated) {
                long etag = subsProvInputDTO.getIfMatch();
                // Get current etag
                long currentEtag = existingSubsProfileDTO.getLastProfileUpdateTime();
                if (etag > 0 && currentEtag != etag) {
                    knLogger.error(methodName, "Etag mismatches  ", etag);
                    throw new KnProvBOException(KnErrorCodes.BOEntity.MISMATCH_IN_SUBS_DOC_ETAG, "Etag mismatch");
                }
            }

            //
            if (oldSubsClientType == KnProvConstants.SUBS_CLIENT_TYPE.POC_NNI_Alias_MDN.value() ||
                    oldSubsClientType == KnProvConstants.SUBS_CLIENT_TYPE.POC_NNI_Group_MDN.value()) {
                if (isNameUpdated) {
                    knLogger.debug(methodName, "isNameUpdated is true for alias Mdn and group Mdn");
                    updateEtagForNNISubscr(oldExtCorpId, persisterTxn);
                }
            }


            //populate the subscriber profile persist DTO
            //subsProfilePersistDTO = new KnSubsProfilePersistDTO();
            subsProfilePersistDTO.setMdn(subsProvInputDTO.getMdn());
            subsProfilePersistDTO.setHierarchyType(subsProvInputDTO.getHierarchyType());
            if (subsProvInputDTO.getFirstNetIndicator() != null) {
            subsProfilePersistDTO.setFirstNetIndicator(subsProvInputDTO.getFirstNetIndicator());
            }
            if (subsProvInputDTO.getIMEI() != null) {
                subsProfilePersistDTO.setIMEI(subsProvInputDTO.getIMEI());
            }
            if (subsProvInputDTO.getAffiliateId() != null) {
                subsProfilePersistDTO.setAffiliateId(subsProvInputDTO.getAffiliateId());
            }
            if (null != newNetworkName && !newNetworkName.isEmpty()) {
                subsProfilePersistDTO.setNetworkName(newNetworkName);
            }

            if (isUFMIChange) {
				int iDenInterOp = genInfoUtil.retrieveXDMSServiceConfig(xdmPttServerId, persisterTxn).getiDenInterOp();
				if (iDenInterOp == KnProvConstants.IDEN_INTEROP.DISABLED.value()) {
					knLogger.error(methodName, "IDenInterOp is disabled");
					throw new KnProvBOException(KnErrorCodes.BOEntity.IDEN_INTEROP_DISABLED,
							"iDen InterOp is disabled");
				}

				if(newUFMI !=null && !newUFMI.isEmpty()  && !newUFMI.equals(oldUFMI) ) {
	                KnOPSubsProfileInfoDTO subsProfileInfoDTO = provInfoUtil.retrieveSubscriberInfoForUFMI(newUFMI, persisterTxn);
	                if(subsProfileInfoDTO!=null){
	                    throw new KnProvBOException(KnErrorCodes.BOEntity.DUPLICATE_UFMI, "UFMI already Exists");
	                }
	            }
				subsProfilePersistDTO.setUfmi(newUFMI);
				subsProfilePersistDTO.setiDenUserName(newIdenUserName);
				subsProfilePersistDTO.setiDenPassword(newIdenPassword);
				subsProfilePersistDTO.setiDenBusUnitId(newIdenBusUnitId);
				isProfileUpdated=true;
			}

            subsProfilePersistDTO.setPayType(subsProvInputDTO.getPayType());
            if (newCorpType != -1) {
                if (oldCorpType != newCorpType) {
                    subsProfilePersistDTO.setCorporateSubscriptionType(newCorpType);
                } else {
                    subsProfilePersistDTO.setCorporateSubscriptionType(oldCorpType);
                }
            } else {
                subsProfilePersistDTO.setCorporateSubscriptionType(oldCorpType);
            }

            if (newPublicType != -1) {
                if (oldPublicType != newPublicType) {
                    subsProfilePersistDTO.setPublicSubscriptionType(newPublicType);
                } else {
                    subsProfilePersistDTO.setPublicSubscriptionType(oldPublicType);
                }
            } else {
                subsProfilePersistDTO.setPublicSubscriptionType(oldPublicType);
            }

            // check if the subscriber client type is only Public subscriber and client type is not handset
            // if it true then send error.

            if (subsProfilePersistDTO.getPublicSubscriptionType() == KnConstants.PUBLIC_SUBSCRIPTION_TYPE.PUBLIC.value() &&
                    subsProfilePersistDTO.getCorporateSubscriptionType() == KnConstants.CORP_SUBSCRIPTION_TYPE.NONE.value()) {
                if (newSubsClientType != KnConstants.SUBSCRIBERS_CLIENT_TYPE.HANDSET.value() || oldSubsClientType==KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value()) {
                    knLogger.error(methodName, "Invalid Client Type for public subscriber");
                    throw new KnProvBOException(KnErrorCodes.BOEntity.INVALID_CLIENT_TYPE_FOR_PUBLIC_SUBS,
                            "Invalid client type passed for public subscriber");
                }
            }


            subsProfilePersistDTO.setRoamingTypes(subsProvInputDTO.getRoamingTypes());

            long lastProfileUpdateTime = Calendar.getInstance().getTimeInMillis();
            subsProfilePersistDTO.setLastProfileUpdateTime(lastProfileUpdateTime);

            //Deprecate of Existing pairing indicator logic because of new logic
           /* boolean pairingIndicator = false;
            if (subsProvInputDTO.getPairingInd() == null) {
                pairingIndicator = existingSubsProfileDTO.getPairingInd();
            } else {
                pairingIndicator = subsProvInputDTO.getPairingInd();
            }*/

            // updating the corporate level data
            KnOPCorpProfileInfoDTO corpProfileInfoDTO = null;
            if (newExtCorpId != null && (newCorpType == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value() ||
                    oldCorpType == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value())) {
                corpProfileInfoDTO = xdmServerDAO.retrieveCorporateProfile(newExtCorpId, false, persisterTxn);
                corpFS2 = corpProfileInfoDTO.getCorpFS2();
                String oldCorpName = corpProfileInfoDTO.getCorporateName();
                String newCorpName = subsProvInputDTO.getCorporateName();
                if (newCorpName != null) {
                    if (!newCorpName.equals(oldCorpName)) {
                        // isProfileUpdated = true;
                        isCorpNameUpdated = true;
                        knLogger.debug(methodName, "Change in Corporate name observed - ");
                    }
                }
            }
            subsProfilePersistDTO.setClientPVmajorVer(existingSubsProfileDTO.getClientPVmajorVer());
            subsProfilePersistDTO.setClientPVminorVer(existingSubsProfileDTO.getClientPVminorVer());
            subsProfilePersistDTO.setUserAgent(existingSubsProfileDTO.getUserAgent());
            String method = subsProvInputDTO.getMethod();
            if (KnConstants.UPDATE_CLIENT_FS.equals(method)) {
                knLogger.debug(methodName, "Call from updateClientFS user Agent :", method);
                KnUserAgentDTO userAgentDTO = subsProvInputDTO.getUserAgentDTO();
                String newUserAgent = subsProvInputDTO.getUserAgent();

                Integer subsClientType = existingSubsProfileDTO.getSubsClientType();
                boolean pttRadioClient = featureSetUtil.getFeatureBitValue(clientFS2, com.kodiak.common.resources.KnConstants.FEATURE_SET.PTTRADIOCLIENT.value());
                if (subsClientType.equals(KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value()) ||
                        subsClientType.equals(KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value()) ||
                        subsClientType.equals(KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value())){
                    if (!pttRadioClient){
                        knLogger.error(methodName,"PTT Radio client feature bit #44 is disable");
                        throw new KnProvBOException(KnErrorCodes.BOEntity.PTTRADIOCLIENTBIT_DISABLED,"PTT Radio client feature bit is disable");
                    }
                }

                //Reject request for client if app name is not knwds
                if (existingSubsProfileDTO.getDispatchType() == com.kodiak.common.resources.KnConstants.DISPATCH_TYPE){
                    if (!(userAgentDTO.getAppName().equals(KnProvConstants.KNWDS))){
                        throw new KnProvBOException(KnErrorCodes.BOEntity.INVALID_CLIENT, "Invalid client for dispatch subscriber app name not matching");
                    }
                }

                if (newUserAgent != null) {
                    if (existingSubsProfileDTO.getUserAgent() == null || (!existingSubsProfileDTO.getUserAgent().equals(newUserAgent))) {
                        knLogger.debug(methodName, "new user Agent :", newUserAgent);
                        knLogger.debug(methodName, "change in User Agent ");
                        subsProfilePersistDTO.setUserAgent(newUserAgent);
                        isProfileUpdated = true;
                    }
                }

                if (userAgentDTO != null) {
                    //Device Onboarding validation
                    boolean supportedDevice = provInfoUtil.isSupportedDevice(userAgentDTO, xdmPttServerId, persisterTxn, existingSubsProfileDTO);
                    if (!supportedDevice) {
                        knLogger.error(methodName, "BLOCK_UNSUPPORTED_DEVICES is enabled and device not supported");
                        throw new KnProvBOException(KnErrorCodes.BOEntity.UNSUPPORTED_DEVICE, "UNSUPPORTED DEVICE");
                    }
                    String protocolVersion = userAgentDTO.getProtocolVersion();
                    if (protocolVersion != null) {
                        try {
                            String[] pv = protocolVersion.split("\\.");
                            if (pv != null && pv[0] != null) {
                                int majorPV = Integer.parseInt(pv[0]);
                                if (existingSubsProfileDTO.getClientPVmajorVer() != majorPV) {
                                    knLogger.debug(methodName, "majorPV version :", majorPV);
                                    knLogger.debug(methodName, "change in major PV version ");
                                    subsProfilePersistDTO.setClientPVmajorVer(majorPV);
                                    isProfileUpdated = true;
                                }
                            }

                            if (pv != null && pv[1] != null) {
                                int minorPV = Integer.parseInt(pv[1]);
                                if (existingSubsProfileDTO.getClientPVminorVer() != minorPV) {
                                    knLogger.debug(methodName, "minorPV version :", minorPV);
                                    knLogger.debug(methodName, "change in minor PV version");
                                    subsProfilePersistDTO.setClientPVminorVer(minorPV);
                                    isProfileUpdated = true;
                                }
                            }


                            Map<Integer,Integer> vocoderIdMap1 = subsProvInputDTO.getVocoderIdMap();
                            if(vocoderIdMap1 != null){
                                if ((protocolVersion.split("\\.")[0]).matches(com.kodiak.common.resources.KnConstants.PROTOCOL_VERSION_9_0)){
                                    knLogger.debug(methodName, "majorPV version : 9 and above");
                                    List<KnClientVocoderProfilePersistDTO> clientVocoderProfilePersistDTOs = new ArrayList<>();

                                    for (Integer priority : vocoderIdMap1.keySet()){
                                        KnClientVocoderProfilePersistDTO clientVocoderProfilePersistDTO = new KnClientVocoderProfilePersistDTO();
                                        clientVocoderProfilePersistDTO.setVocoderId(vocoderIdMap1.get(priority));
                                        clientVocoderProfilePersistDTO.setPriority(priority);
                                        clientVocoderProfilePersistDTO.setMdn(subsProvInputDTO.getMdn());
                                        clientVocoderProfilePersistDTOs.add(clientVocoderProfilePersistDTO);
                                    }
                                    int newVocoderId = createClientSuppVocoder(clientVocoderProfilePersistDTOs,persisterTxn);
                                    knLogger.debug(methodName,"Change is vocoder ID old ID :",existingSubsProfileDTO.getVocoderId()," New vocoder Id :",newVocoderId);
                                    subsProfilePersistDTO.setVocoderId(newVocoderId);
                                    isProfileUpdated = true;
                                }else if(Integer.parseInt(protocolVersion.split("\\.")[0]) < KnConstants.PROTOCOL_VERSION_9_X && "1".equals(paramNameValueMap.get(KnConstants.SPECIFIC_CODEC_SUPPPORT_OLDER_BREW_CLIENTS))
                                        && newUserAgent.contains(paramNameValueMap.get(KnConstants.OLDER_CLIENTS_CODEC_SUPPORTED_BREW_UA))){
                                    knLogger.debug(methodName, "Older brew client");
                                    subsProfilePersistDTO.setVocoderId(KnConstants.AMR_HALF_RATE_PROFILEID);
                                }
                            }


                        } catch (NumberFormatException nfe) {
                            if(isUpgradePkg) {
                                KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_UPGRADE_PACKAGE_WITH_CLIENT_CHANGE_FAILURE);
                            }
                            knLogger.debug(methodName, "Invalid PV version :", nfe);
                            knLogger.debug(methodName, "setting PV as 1.0:");
                            subsProfilePersistDTO.setClientPVmajorVer(1);
                            subsProfilePersistDTO.setClientPVminorVer(0);
                        }
                    } else {
                        knLogger.debug(methodName, "setting pv as 1.0 as PV is null or improper");
                        subsProfilePersistDTO.setClientPVmajorVer(1);
                        subsProfilePersistDTO.setClientPVminorVer(0);
                    }
                } else {
                    knLogger.debug(methodName, "setting Pv as 1.0 as PV is null or not proper");
                    subsProfilePersistDTO.setClientPVmajorVer(1);
                    subsProfilePersistDTO.setClientPVminorVer(0);
                }

                int clusterId = Integer.parseInt(System.getenv(com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME));
                String deviceSharingFlag = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn).get(KnConstants.DEVICE_SHARING_FEATURE_FLAG);

                int deviceSharing = 0;
                if(deviceSharingFlag != null) deviceSharing = Integer.parseInt(deviceSharingFlag);
                int deviceSharewifiFlag=Integer.parseInt(genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn).get(KnConstants.AUTO_DEVICESHARE_WIFI_CC));
                knLogger.debug("deviceSharewifiFlag-->"+deviceSharewifiFlag);
                knLogger.debug("getClientPVmajorVer"+subsProfilePersistDTO.getClientPVmajorVer()+"getClientPVmajorVer"+existingSubsProfileDTO.getClientPVmajorVer()
                +"getLicenseType"+existingSubsProfileDTO.getLicenseType()+"deviceSharing"+deviceSharing+"existingSubsProfileDTO.getSubsClientType()"+existingSubsProfileDTO.getSubsClientType()
                +"");
                if(subsProfilePersistDTO.getClientPVmajorVer() != existingSubsProfileDTO.getClientPVmajorVer() && subsProfilePersistDTO.getClientPVmajorVer() >= PROTOCOL_VERSION_14
                        && existingSubsProfileDTO.getLicenseType() != ENABLED && deviceSharing == ENABLED && (String.valueOf(existingSubsProfileDTO.getSubsClientType()).matches(CLIENT_TYPE_WIFI_CROSS_NORMAL_RADIO))
                        && deviceSharewifiFlag== ENABLED){
                    subsProfilePersistDTO.setLicenseType(ENABLED);
                }else{
                    knLogger.debug("setting the else val-->"+existingSubsProfileDTO.getLicenseType());
                    subsProfilePersistDTO.setLicenseType(existingSubsProfileDTO.getLicenseType());
                }

            }
            if (KnConstants.MCSCOMPLIANCE != existingSubsProfileDTO.getMcsCompliance() && (subsProvInputDTO.getMcId() != null
					|| subsProvInputDTO.getMcDataId() != null || subsProvInputDTO.getMcpttId() != null
					|| subsProvInputDTO.getMcVideoId() != null)) {
				throw new KnProvBOException(KnErrorCodes.BOEntity.MCXIDS_NOT_ALLOWED_FOR_KODIAK_CLIENTS,
						"MCX Id(s) are not allowed for kodiak clients.");
			}
            validateMCXIdsUriFormat(subsProvInputDTO, persisterTxn);
			validateIfUserIdAlredyExistsInSystem(subsProvInputDTO.getUserId(), persisterTxn);
			Set<String> mdnSet= new HashSet<>();
			if (checkIfMcxIdIsNotEqualToRequestMdn(subsProvInputDTO.getMcId(), subsProvInputDTO.getMdn())) {
				mdnSet.add(subsProvInputDTO.getMcId().substring(TELURI.length()));
			}
			if (checkIfMcxIdIsNotEqualToRequestMdn(subsProvInputDTO.getMcpttId(), subsProvInputDTO.getMdn())) {
				mdnSet.add(subsProvInputDTO.getMcpttId().substring(TELURI.length()));
			}
			if (checkIfMcxIdIsNotEqualToRequestMdn(subsProvInputDTO.getMcVideoId(), subsProvInputDTO.getMdn())) {
				mdnSet.add(subsProvInputDTO.getMcVideoId().substring(TELURI.length()));
			}
			if (checkIfMcxIdIsNotEqualToRequestMdn(subsProvInputDTO.getMcDataId(), subsProvInputDTO.getMdn())) {
				mdnSet.add(subsProvInputDTO.getMcDataId().substring(TELURI.length()));
			}
			List<String> mdns = new ArrayList<String>();
			mdns.addAll(mdnSet);
			valiadteIfMCXIdsExistsAsMdnOrAliasMdn(mdns, persisterTxn);
			validateIfMdnOrMcidsPresentInKUIDPOOL(mdns);
			valiadteIfMCXIdsExistsInSystemForUpdate(subsProvInputDTO, persisterTxn);
			populateDefaultMCXIdsForUpdate(subsProvInputDTO);

			if (subsProvInputDTO.getUserId() != null) {
				if (!(subsProvInputDTO.getUserId().isEmpty() && null == existingSubsProfileDTO.getUserId())
						&& !subsProvInputDTO.getUserId().equalsIgnoreCase(existingSubsProfileDTO.getUserId())) {
					subsProfilePersistDTO.setUserId(subsProvInputDTO.getUserId());
					isProfileUpdated = true;
				}

			}
			if (subsProvInputDTO.getMcId() != null) {
				if (!subsProvInputDTO.getMcId().equals(existingSubsProfileDTO.getMcId())) {
					subsProfilePersistDTO.setMcId(subsProvInputDTO.getMcId());
					isProfileUpdated = true;
				}
			}
			if (subsProvInputDTO.getMcpttId() != null) {
				if (!subsProvInputDTO.getMcpttId().equals(existingSubsProfileDTO.getMcpttId())) {
					subsProfilePersistDTO.setMcpttId(subsProvInputDTO.getMcpttId());
					isProfileUpdated = true;
				}
			}
			if (subsProvInputDTO.getMcVideoId() != null) {
				if (!subsProvInputDTO.getMcVideoId().equals(existingSubsProfileDTO.getMcVideoId())) {
					subsProfilePersistDTO.setMcVideoId(subsProvInputDTO.getMcVideoId());
					isProfileUpdated = true;
				}
			}
			if (subsProvInputDTO.getMcDataId() != null) {
				if (!subsProvInputDTO.getMcDataId().equals(existingSubsProfileDTO.getMcDataId())) {
					subsProfilePersistDTO.setMcDataId(subsProvInputDTO.getMcDataId());
					isProfileUpdated = true;
				}
			}
            if(subsProvInputDTO.getOnBoardingEmailReqd()!=null) {
                if (allProfileDto != null && allProfileDto.getOnBoardingMailReqd() != null && !allProfileDto.getOnBoardingMailReqd().equals(subsProvInputDTO.getOnBoardingEmailReqd())) {
                    isProfileUpdated = true;
                }
            }

            if (subsProvInputDTO.getCameraType() != null) {
                if (!subsProvInputDTO.getCameraType().equals(existingSubsProfileDTO.getCameraType())) {
                    subsProfilePersistDTO.setCameraType(subsProvInputDTO.getCameraType());
                    isProfileUpdated = true;
                }
            }

            if(cameraInfo!=null){
                isProfileUpdated = true;
            }
            boolean  aliasInfoAlreadyExist = true;
            if(subsProvInputDTO.getAddAliasInfoList()!=null) {
                knLogger.debug("check for input AliaInfoList");
                List<KnSubsAliasInfoDTO> alisasInfoList=subsProvInputDTO.getAddAliasInfoList();
                Map<String, String> aliasIdIssuerMap= new HashMap<>();
                for(KnSubsAliasInfoDTO aliasInfoDTO: alisasInfoList){
                    aliasIdIssuerMap.put(aliasInfoDTO.getAliasId(),aliasInfoDTO.getAliasIdIssuer());
                }
                Map<String, List<KnSubsAliasInfoDTO>> subsAliasIdInfoMap = xdmServerDAO.selectSubsAliasIdInfoByAliasId(aliasIdIssuerMap, persisterTxn);
              aliasInfoAlreadyExist =provInfoUtil.checkAliasInfoCombinationExist(alisasInfoList,subsAliasIdInfoMap);
              knLogger.debug("new AliasInfoList already exist :: ",aliasInfoAlreadyExist);
                if(aliasInfoAlreadyExist){
                    knLogger.debug("AliasInfoList cannot be modified");
                    throw new KnProvBOException(KnErrorCodes.BOEntity.DUPLICATE_ALIAS_INFO_NOT_ALLOWED,
                            "Input ALIAS INFO combination (AliasId,AliasIssuer,AliasType) already exist");
                }else
                {
                    isProfileUpdated=true;
                    knLogger.debug("AliasInfoList for modify validated");

                }

            }
            boolean removeFromList=false;
            if(subsProvInputDTO.getRemoveAliasInfoList()!=null) {
                knLogger.debug("check for input AliaInfoList");
                List<KnSubsAliasInfoDTO> alisasInfoList=subsProvInputDTO.getRemoveAliasInfoList();
                Map<String, String> aliasIdIssuerMap= new HashMap<>();
                for(KnSubsAliasInfoDTO aliasInfoDTO: alisasInfoList){
                    aliasIdIssuerMap.put(aliasInfoDTO.getAliasId(),aliasInfoDTO.getAliasIdIssuer());
                }
                Map<String, List<KnSubsAliasInfoDTO>> subsAliasIdInfoMap = xdmServerDAO.selectSubsAliasIdInfoByAliasId(aliasIdIssuerMap, persisterTxn);
                removeFromList =provInfoUtil.checkAliasInfoCombinationExist(subsProvInputDTO.getMdn(),alisasInfoList,subsAliasIdInfoMap);
                knLogger.debug("new AliasInfoList already exist :: ",removeFromList);
                if(!removeFromList){
                    knLogger.debug("AliasInfoList to be removed doesn't exist");
                    throw new KnProvBOException(KnErrorCodes.BOEntity.REMOVE_ALIAS_INFO_NOT_EXIST,
                            "Input ALIAS INFO combination (AliasId,AliasIssuer,AliasType) to remove doesn't exist");
                }else
                {
                    isProfileUpdated=true;
                    knLogger.debug("AliasInfoList for modify validated");

                }

            }

            //send a failure response if profile is not updated.
            if (!isProfileUpdated && !isCorpNameUpdated) {
                knLogger.error(methodName, "No change in profile observed");
                throw new KnProvBOException(KnErrorCodes.BOEntity.NO_CHG_IN_PROFILE, "No change in profile observed");
            }
            if (isExtCorpIdUpdated && (newCorpType == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value() ||
                    oldCorpType == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value())) {
                if (newExtCorpId != null) {
                    // check if the new Ext Corp ID exists in DB.
                    // if exists assign the INT CORP ID of the new Ext Corp ID to the Subscriber profile
                    // and update the corporate profile etag
                    // else create the NEW EXT CORP ID profile and assign newly created ID to subscriber profile
                    knLogger.debug(methodName, "verifying if corporate profile already exists");
                    int corpId = corpProfileInfoDTO.getCorpId();
                    knLogger.debug(methodName, "Corporate Id Retrieved - ", corpId);
                    int maxSubsCountPerCorp = 0;
                    if (corpId > 0) {
                        knLogger.debug(methodName, "corporate profile already exists with corp id ", "[", corpId, "] for extCorpId ", newExtCorpId);
                        int pairedContactListID = corpProfileInfoDTO.getPairedContactListId();
                        boolean existingPairInd = false;
                        if (pairedContactListID > 0) {
                            existingPairInd = true;
                        }

                        //Deprecate of Existing pairing indicator logic because of new
                        // logic
                       /* knLogger.debug(methodName, "pairingIndicator:", pairingIndicator, " existingPairInd:" , existingPairInd, " corpId:", corpId);
                        if ((pairingIndicator ^ existingPairInd)) {
                            knLogger.error(methodName, "Mismatch in pairing indicator for the provided corporation " );
                            throw new KnProvBOException(KnErrorCodes.BOEntity.MISMATCH_IN_PAIRING_IND,
                                    "Mismatch in pairing indicator for the provided corporation");
                        }*/

                        int maxSubsCorporateLimit = corpProfileInfoDTO.getMaxSubscribers();
                        int corpSubsCount = provInfoUtil.retrieveCorpSubsCount(corpId, persisterTxn);
                        int maxSubsPerCorpSystemLevel = genInfoUtil.retrieveXDMSServiceConfig(xdmPttServerId, persisterTxn).getMaxSubscrPerCorp();
                        if(maxSubsCorporateLimit > 0) {
                            maxSubsCountPerCorp = maxSubsPerCorpSystemLevel > maxSubsCorporateLimit ? maxSubsCorporateLimit : maxSubsPerCorpSystemLevel;
                        }else{
                            maxSubsCountPerCorp = maxSubsPerCorpSystemLevel;
                        }
                        knLogger.debug(methodName, "maxSubsPerCorpSystemLevel: ", maxSubsPerCorpSystemLevel, " ,maxSubsCorporateLimit: ",
                                maxSubsCorporateLimit, "corpSubsCount: ", corpSubsCount);
                        if (corpSubsCount + 1 > maxSubsCountPerCorp) {
                            knLogger.error(methodName, "Max Subscriber limit has been reached for corp - ", corpId);
                            throw new KnProvBOException(KnErrorCodes.Validator.MAX_SUBS_LIMIT_REACHED_FOR_CORP,
                                    "Max Subscriber limit for corp has been reached");
                        }

                        subsProfilePersistDTO.setPairingInd(existingPairInd);

                        //New autopair implementation
                        if (pairedContactListID > 0) {
                            knLogger.debug(methodName, "param value map", paramNameValueMap);
                            int corpAutoPairingCnt = Integer.valueOf(paramNameValueMap.get(KnConstants.CORP_AUTO_PAIRING_SIZE));
                            knLogger.info(methodName, "existing subs count - ", corpSubsCount, "autopair limit - ", corpAutoPairingCnt);
                            if (corpSubsCount >= corpAutoPairingCnt) {
                                // to disable auto pairing
                                responseDTO.setCorpAutoPairing(false);
                                responseDTO.setIsOldCorp(true);
                            } else {
                                // to enable auto pairing
                                if (KnProvConstants.SUBS_CLIENT_TYPE.HANDSET.value() == newSubsClientType) {
                                    responseDTO.setCorpAutoPairing(true);
                                    responseDTO.setIsOldCorp(true);
                                }
                            }
                        }

                        KnCorpProfilePersistDTO corpProfilePersistDTO = new KnCorpProfilePersistDTO();
                        corpProfilePersistDTO.setCorpId(corpId);
                        if (subsProvInputDTO.getCorporateName() != null) {
                            corpProfilePersistDTO.setCorporateName(subsProvInputDTO.getCorporateName());
                        } else {
                            corpProfilePersistDTO.setCorporateName(corpProfileInfoDTO.getCorporateName());
                        }
                        xdmServerDAO.updateCorporateProfile(corpProfilePersistDTO, persisterTxn);
                        subsProfilePersistDTO.setCorpId(corpId);

                    } else {
                        knLogger.debug(methodName, "creating the corporate Profile");

                        //NEW AUtoPair Logic Implementation
                        knLogger.debug(methodName, "param value map", paramNameValueMap);
                        int enbaleAutoPair = Integer.valueOf(paramNameValueMap.get(KnConstants.ENABLE_CORP_AUTO_PAIRING));
                        if (enbaleAutoPair == KnProvConstants.AUTO_PAIR_ENABLED && (subsProvInputDTO.isAutoPair() != null && subsProvInputDTO.isAutoPair())) {
                            knLogger.info(methodName, "System flag corp auto pairing is enabled and CBE is also configured.");
                            if (KnProvConstants.SUBS_CLIENT_TYPE.HANDSET.value() == newSubsClientType) {
                                // to enable auto pairing
                                responseDTO.setCorpAutoPairing(true);
                                responseDTO.setIsOldCorp(false);
                            }
                        }

                        //get final CorpFeatureSet
                        corpFS2 = featureSetUtil.getDefFinalCorpFS();
                        knLogger.debug(methodName, "final corpFS :", corpFS2);

                        //get final OpsCorpFeatureSet
                        String opsCorpFS2 = featureSetUtil.getDefFinalOpsCorpFS();
                        knLogger.debug(methodName, "final opsCorpFS :", opsCorpFS2);
                        subsProfilePersistDTO.setExtCorpId(newExtCorpId);
                        subsProfilePersistDTO.setPoCHome(existingSubsProfileDTO.getPoCHome());
                        KnCorpProfilePersistDTO corpProfilePersistDTO = populateCorpProfileInfoDTO(xdmPttServerId,
                                subsProfilePersistDTO, persisterTxn);
                        if (subsProvInputDTO.getCorporateName() != null) {
                            corpProfilePersistDTO.setCorporateName(subsProvInputDTO.getCorporateName());
                        }
                        corpProfilePersistDTO.setCorpFS2(corpFS2);
                        corpProfilePersistDTO.setOpsCorpFS2(opsCorpFS2);
                        String pocPttServerId = corpProfilePersistDTO.getPocHome();
                        KnPOCSvcConfigDTO knPOCSvcConfigDTO = provInfoUtil.retrievePOCSvcConfig(pocPttServerId);
                        //added new  parameter for dynamic Qos
                        corpProfilePersistDTO.setDynamicQosFlag(knPOCSvcConfigDTO.getDynamicQosFlag());
                        String xdmCorpFS2Set = KnGeneralUtil.getDefaultXDMCorpFS2Set(XDMCORPFS2_SET.EMERGENCY_CONF_TIMER_FEATURE.value(), com.kodiak.common.resources.KnConstants.TRUE);
                        xdmCorpFS2Set = featureSetUtil.calculateXDMCorpFS2(xdmCorpFS2Set, XDMCORPFS2_SET.EMERGENCY_CONF_TIMER_FEATURE.value(), com.kodiak.common.resources.KnConstants.TRUE);
                        corpProfilePersistDTO.setXdmCorpFS2Set(xdmCorpFS2Set);
                        corpId = xdmServerDAO.createCorporateProfile(corpProfilePersistDTO, persisterTxn);
                        successPegs.add(KnOMConstants.XDM_NUM_CORP_PROFILE_CREATED);
                        knLogger.debug(methodName, "created the corporate profile for extCorpId - ",
                                newExtCorpId);
                        subsProfilePersistDTO.setCorpId(corpId);
                        subsProfilePersistDTO.setPairingInd(subsProvInputDTO.getPairingInd());
                    }

                }

                if (oldExtCorpId != null) {
                    // if Corporate subscription type is moved Corp to non-corp
                    if (newExtCorpId == null) {
                        subsProfilePersistDTO.setCorpId(0);
                    }
                }
            } else if (newCorpType == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value() ||
                    oldCorpType == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
                int corpId = existingSubsProfileDTO.getCorpId();
                subsProfilePersistDTO.setCorpId(corpId);
                if (corpId > 0) {
                    String oldCorpName = corpProfileInfoDTO.getCorporateName();
                    String newCorpName = subsProvInputDTO.getCorporateName();
                    KnCorpProfilePersistDTO corpProfilePersistDTO = new KnCorpProfilePersistDTO();
                    corpProfilePersistDTO.setCorpId(corpId);
                    if (newCorpName != null) {
                        if (!newCorpName.equals(oldCorpName)) {
                            knLogger.debug(methodName, "Change in Corporate name observed - ");

                            corpProfilePersistDTO.setCorporateName(newCorpName);
                        } else {
                            corpProfilePersistDTO.setCorporateName(oldCorpName);

                        }
                    }
                    xdmServerDAO.updateCorporateProfile(corpProfilePersistDTO, persisterTxn);
                }

                //Deprecate of Existing pairing indicator logic because of new logic
               /* //PR INT52567: setting the existing the Pairing Indicator
                subsProfilePersistDTO.setPairingInd(pairingIndicator);*/
            }
            if (newEMail != null) {
                subsProfilePersistDTO.setEmailAddress(newEMail);
            }
            if (newCorpType == KnConstants.CORP_SUBSCRIPTION_TYPE.NONE.value()) {
                subsProfilePersistDTO.setCorpId(0);
            }

            String newSubsFS2 = existingSubsProfileDTO.getSubsFS2();
            //same values of provFS1 and provFS1BitMask is used for calculation final subsFS after oring with bitmask
            String provFS2 = newSubsFS2;
            String provFS2BitMask = newSubsFS2;
            Boolean isSubsFS2Updated=false;

            if (isClientTypeUpdated || isSubsTypeUpdated ||isPkgCodeUpdated) {

				BitSet finalFSBitSet = new BitSet(Long.SIZE);
				String basePkgCode = paramNameValueMap.get(KnConstants.BASE_PKGCODE);
				String basePkgCodeFS = featureSetUtil.getDefSubsFeatureSetForBasePkg(newPublicType, newCorpType,
						oldSubsClientType, basePkgCode, xdmPttServerId);
				knLogger.debug(methodName, "base pkg is applied - ", basePkgCodeFS);
				BitSet basePkgCodeBiSet = featureSetUtil.convertHexStringToBitSet(basePkgCodeFS);
				finalFSBitSet.or(basePkgCodeBiSet);
				Map<String, Integer> pkgIdsMap = null;
				if (isPkgCodeUpdated) {
					pkgIdsMap = finalPkgIdsMap;
					knLogger.debug(methodName, "updated pkg is applied - ", pkgIdsMap);
				} else {
					pkgIdsMap = exsitingSubsPkgs;
					knLogger.debug(methodName, "existing pkg is applied - ", pkgIdsMap);
				}

				String pkgCodeFS = featureSetUtil.getDefSubsFeatureSetForPkgCodes(newPublicType, newCorpType,
						oldSubsClientType, pkgIdsMap, xdmPttServerId);
				knLogger.debug(methodName, "addPkgIds is present with subscriberFS - ", pkgCodeFS);
				BitSet pkgCodeBiSet = featureSetUtil.convertHexStringToBitSet(pkgCodeFS);
				finalFSBitSet.or(pkgCodeBiSet);
				int oldIntropBitStatus=findInterOPBitStatus(existingSubsProfileDTO, exsitingSubsPkgs, persisterTxn);

				if (finalFSBitSet.get(LMR_BIT) && LMR_BIT_STATUS.MANUALLY_DISABLED.Value()==oldIntropBitStatus) {
					finalFSBitSet.clear(LMR_BIT);
				} else if (!finalFSBitSet.get(LMR_BIT) && LMR_BIT_STATUS.MANUALLY_ENABLED.Value()==oldIntropBitStatus) {
					finalFSBitSet.set(LMR_BIT);
				}
				newSubsFS2 = featureSetUtil.convertBitSetToHexString(finalFSBitSet);
                provFS2 = newSubsFS2;
                provFS2BitMask = newSubsFS2;
                knLogger.debug(methodName, "clientType or subsType or pkg code changed.. got default SubsFS :", newSubsFS2);
                isSubsFS2Updated=true;
            }

            String subsFS2 = featureSetUtil.generateSubsFeatureSet(newSubsFS2, provFS2, provFS2BitMask);
            //if dispatch group member flag changed then set/clear on demand location bit in subsFS

            //setting on demand location bit in subsFS
            String xdmsFs2=existingSubsProfileDTO.getXdmsFS2();
            if (newDispatchGrpMember == KnConstants.BIT_TRUE) {
            	xdmsFs2 = featureSetUtil.getSetFeatureSetBits(xdmsFs2, new int[]{com.kodiak.common.resources.KnConstants.FEATURE_SET.ONDEMLOCATION.value()});
            	knLogger.debug(methodName, "on demand location bit set in xdmsFs:", xdmsFs2);
            } else {
            	xdmsFs2 = featureSetUtil.getClearFeatureSetBits(xdmsFs2, new int[]{com.kodiak.common.resources.KnConstants.FEATURE_SET.ONDEMLOCATION.value()});
            	knLogger.debug(methodName, "on demand location bit clear in xdmsFs:", xdmsFs2);
            }

            int clientPVMajorVersion = subsProfilePersistDTO.getClientPVmajorVer();
            String clientCapOverrideBitMask = featureSetUtil.getClientCapabilityBitMask(xdmPttServerId, clientPVMajorVersion);
            if (subsProvInputDTO.getClientFS2() != null) {
                clientFS2 = featureSetUtil.generateClientFeatureSet(subsProvInputDTO.getClientFS2(), clientCapOverrideBitMask);
            }
            boolean talkGrpSelClient = featureSetUtil.getFeatureBitValue(clientFS2, com.kodiak.common.resources.KnConstants.FEATURE_SET.TLKGRPSELCLIENT.value());
            boolean talkGrpScanClient = featureSetUtil.getFeatureBitValue(clientFS2, com.kodiak.common.resources.KnConstants.FEATURE_SET.TLKGRPSCANCLIENT.value());

            if (!((newSubsClientType == KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value() || newSubsClientType == KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value() || newSubsClientType == KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value()))) {
            //When talkGrpSelClient bit is disable in clientFS then enabling talkgrpserver bit of clientFS for
            // corporate subs and which are not of clienttype pocdonor or dispatch
            if ((!(talkGrpSelClient || talkGrpScanClient)) && newCorpType == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value() && !(newSubsClientType == KnProvConstants.SUBS_CLIENT_TYPE.POC_DONOR_RADIO.value() || newSubsClientType == KnProvConstants.SUBS_CLIENT_TYPE.DISPATCH_CLIENT.value())) {
                knLogger.debug(methodName, "corp type setting talk grp server bit since client bit is disable in clientFS ");
                clientFS2 = featureSetUtil.getSetFeatureSetBits(clientFS2, new int[]{com.kodiak.common.resources.KnConstants.FEATURE_SET.TLKGRPSELSERVER.value()});
            } else {
                //When talkGrpSelClient bit is enabled in clientFS then disabling talkgrpserver bit of clientFS for
                // public subs or which are of clienttype pocdonor or dispatch
                knLogger.debug(methodName, "public to corp/candp or talkGrpSelClient is enabled. clearing talk grp server bit since client bit enable in clientFS");                 // subsFS1 = featureSetUtil.getClearFeatureSetBits(subsFS1, new int[]{KnConstants.FEATURE_SET.TLKGRPSELSERVER.value()});
                clientFS2 = featureSetUtil.getClearFeatureSetBits(clientFS2, new int[]{com.kodiak.common.resources.KnConstants.FEATURE_SET.TLKGRPSELSERVER.value()});
            }
            }else{
            	clientFS2 = featureSetUtil.getSetFeatureSetBits(clientFS2, new int[]{com.kodiak.common.resources.KnConstants.FEATURE_SET.TLKGRPSELSERVER.value()});
            	clientFS2 = featureSetUtil.getClearFeatureSetBits(clientFS2, new int[]{com.kodiak.common.resources.KnConstants.FEATURE_SET.TLKGRPSELCLIENT.value()});
            	clientFS2 = featureSetUtil.getClearFeatureSetBits(clientFS2, new int[]{com.kodiak.common.resources.KnConstants.FEATURE_SET.TLKGRPSCANCLIENT.value()});
                knLogger.debug(methodName, "setting talk grp server bit since client bit is disable in clientFS & clearing talk grp server bit in clientFS fpr PTT Radio clients :",clientFS2);
            }
            String opsFS2 = existingSubsProfileDTO.getOpsFS2();
            String activeFS2;
            String corpAdminFS2 = existingSubsProfileDTO.getCorpAdminFS2();
            knLogger.debug(methodName, "clientPVMajorVersion ", clientPVMajorVersion, " ,clientCapOverrideBitMask=", clientCapOverrideBitMask);

            boolean cleanUpTGSData = false;

            knLogger.debug(methodName," updated subsProvPersistDTO's   ProvFS :", provFSMap);

            BitSet bitset= new BitSet();
            if(provFSMap!=null && ! provFSMap.isEmpty()){
                    bitset= featureSetUtil.convertHexStringToBitSet(subsFS2);
                    for(Map.Entry<Integer,Integer> provEntry: provFSMap.entrySet()){
                        if(provEntry.getValue()== com.kodiak.common.resources.KnConstants.PROVFS_ENABLE.ENABLED.value()){
                            bitset.set(provEntry.getKey());
                        }else{
                            bitset.clear(provEntry.getKey());
                        }
                    }
                    subsFS2= featureSetUtil.convertBitSetToHexString(bitset);
                    provFS2 = subsFS2;
                    provFS2BitMask= provFS2;
                    knLogger.debug(methodName,"  subsFS2 ",subsFS2,"provFS2" ,provFS2);
                    subsFS2 = featureSetUtil.generateSubsFeatureSet(subsFS2, provFS2, provFS2BitMask);


            }
            String userProfileFS2 = existingSubsProfileDTO.getUserProfileFS2();
            if(userProfileFS2==null)
            {
            	userProfileFS2=featureSetUtil.getDefFinalUserProfileFS();
            }
            if (newCorpType == KnConstants.CORP_SUBSCRIPTION_TYPE.NONE.value()) {
                activeFS2 = featureSetUtil.generateActiveFeatBitSet(pocPTTIdActiveFS2, presencePTTIdActiveFS2, xdmsPttId,
                        clientFS2, subsFS2, opsFS2, clientCapOverrideBitMask,xdmsFs2,userProfileFS2);
            } else {
                activeFS2 = featureSetUtil.generateActiveFeatBitSet(pocPTTIdActiveFS2, presencePTTIdActiveFS2, xdmsPttId,
                        clientFS2, subsFS2, corpFS2, opsFS2, corpAdminFS2, clientCapOverrideBitMask,xdmsFs2,userProfileFS2);
            }

            if (KnConstants.UPDATE_CLIENT_FS.equals(method)) {
                knLogger.debug(methodName, "Call from updateClientFS for derivedKey :", method);
                boolean remotePushNotification = featureSetUtil.getFeatureBitValue(activeFS2, FEATURE_SET.REMOTEPUSHNOTIFICATION.value());
                boolean gcmPushNotify = featureSetUtil.getFeatureBitValue(activeFS2, FEATURE_SET.GCMPUSHNOTIFICATION.value());
               if (remotePushNotification || gcmPushNotify){
                byte[] salt = mdn.getBytes();
                   byte[] encryptedPwd = KnGeneralUtil.getEncryptedPassword(existingSubsProfileDTO.getClientPassword(),salt,KnConstants.ITERATIONS_10_000,KnConstants.DERIVEDKEYLENGTH);
                   String derivedKey = provInfoUtil.convertToHex(encryptedPwd);
                    knLogger.debug(methodName,"Derived key after generation and convertion: ",derivedKey);
                    subsProfilePersistDTO.setDerivedKey(KnGeneralUtil.convertHexToAscii(derivedKey));
               }
            }

            if (!activeFS2.equals(existingSubsProfileDTO.getActiveFS2())) {
                isActiveFSUpdated = true;
                cleanUpTGSData = cleanUpTGSData(activeFS2, existingSubsProfileDTO.getActiveFS2());
                //Checking MCPTT feature permission
                setMCPTTFeatureDisable(mdn, activeFS2, persisterTxn, xdmServerDAO);
                //adding to mcxGroup clean up job on 81 bit disable
                com.kodiak.xdms.server.common.util.KnGeneralUtil.veryLargeGroupBitChanged(existingSubsProfileDTO.getActiveFS2()
                        ,activeFS2
                        ,subsProfilePersistDTO.getMdn()
                        ,String.valueOf(subsProfilePersistDTO.getCorpId()),false);
            }
            knLogger.debug(methodName, "cleanUp TGS ", cleanUpTGSData);
            subsProfilePersistDTO.setDispatchGroupMember(newDispatchGrpMember);
            subsProfilePersistDTO.setClientPassword(newClientPassword);
            if (isPTTRadioClientTypeChange && !isUpgradePkg) {
                // Existing case : Setting the new client type if isPTTRadioClientTypeChange is true  and it's not an upgrade package
                subsProfilePersistDTO.setSubsClientType(newSubsClientType);
            } else if (isUpgradePkg) {
                // if Package is upgraded to mcptt setting the upgraded package client type
                knLogger.debug(methodName, "subsclientype: ", upgradePkgClientType);
                subsProfilePersistDTO.setSubsClientType(upgradePkgClientType);
            } else {
                //Existing Case : Retaining the old client type if no changes are detected
                subsProfilePersistDTO.setSubsClientType(oldSubsClientType);
            }
            subsProfilePersistDTO.setSubsFS2(subsFS2);
            subsProfilePersistDTO.setActiveFS2(activeFS2);
            subsProfilePersistDTO.setClientFS2(clientFS2);
            subsProfilePersistDTO.setOpsFS2(opsFS2);
            subsProfilePersistDTO.setXdmsFS2(xdmsFs2);
            subsProfilePersistDTO.setCorpAdminFS2(corpAdminFS2);

            if (newAccountId != null) {
                subsProfilePersistDTO.setAccountId(newAccountId.trim());
            }


            //
            // of Old Corporation Details
            if (isExtCorpIdUpdated) {
                if (oldExtCorpId != null) {
                    // check if the current subs is the last subs for the corporation
                    // if true then remove the corporate profile else update the corporate profile etag.
                    int corpId = existingSubsProfileDTO.getCorpId();
                    int corpSubsCount = xdmServerDAO.retrieveCorpSubscriberCount(corpId, persisterTxn);
                    knLogger.debug(methodName, "corp Subscriber count - ", corpSubsCount);
                    if (corpSubsCount == 0) {
                        knLogger.debug(methodName, "Require to Delete Corp profile since the last subs of ",
                                "corp profile is deleted - ");
                    } else {
                        knLogger.debug(methodName, "Updating the corp profile etag since mdn for corp is deleted"
                        );
                        long profileUpdateTime = Calendar.getInstance().getTimeInMillis();
                        xdmServerDAO.updateCorpProfileLastUpdateTime(corpId, profileUpdateTime, persisterTxn);
                        knLogger.debug(methodName, "updated the corp profile etag for corp id - ", corpId
                        );
                    }
                }
            }



            Integer qppPkgId = existingSubsProfileDTO.getQppPkgId();
			Integer profileId = null;
			String tierPackageId = allProfileDto!=null?allProfileDto.getTierPkgCode():null;
			Integer dataPkgId=allProfileDto!=null?allProfileDto.getDataPkgId():null;

			Map<String, Integer> addonPackageIds=null;

			if (isPkgCodeUpdated) {
				String updatedTierPkg=null;
				addonPackageIds = new HashMap<>();
				knLogger.debug(methodName, "Updated pkg map in  pocsubsaddlinfo", finalPkgIdsMap);
				for (Entry<String, Integer> entry : finalPkgIdsMap.entrySet()) {
					if (entry.getValue().intValue() == KnConstants.TIER_PKG_TYPE.intValue()) {
						updatedTierPkg = entry.getKey();
						knLogger.debug(methodName, "tier pkg is present ", updatedTierPkg);
					} else if (entry.getValue() == KnConstants.ADDON_PKG_TYPE.intValue()) {
						addonPackageIds.put(entry.getKey(), entry.getValue());

					}
				}
				tierPackageId=updatedTierPkg;
				if (!addonPackageIds.isEmpty()) {
					for (String addonPkgCode : addonPackageIds.keySet()) {
						profileId = featureSetUtil.getAddProfIdForPkg(addonPkgCode, xdmPttServerId);
						if (profileId != null) {
							qppPkgId = genInfoUtil.getDataPkgId(KnConstants.QPP_DATA_PKG_TYPE, persisterTxn)
									.get(profileId);
							if (qppPkgId != null) {
								dataPkgId = qppPkgId;
								break;
							} else if (dataPkgId == null)
								dataPkgId = genInfoUtil.getDataPkgId(KnConstants.ADDON_DATA_PKG_TYPE, persisterTxn)
										.get(profileId);

						}
					}

				} else {
					qppPkgId=null;
					dataPkgId=null;
				}
			}

			if (qppPkgId == null)
				qppPkgId = KnConstants.DEFAULT_QPP_ID;
			if (dataPkgId == null)
				dataPkgId = KnConstants.DEFAULT_DATAPKG_ID;
			if (profileId == null)
				profileId = KnConstants.DEFAULT_PROFILE_ID;
			subsProfilePersistDTO.setQppPkgId(qppPkgId);
            subsProfilePersistDTO.setExtGatewayId(subsProvInputDTO.getExtGatewayId());
            //Update the Subscriber DB
            // --> subscriber info table
            if (isProfileUpdated || !isCorpNameUpdated || isUpgradePkg) {
                knLogger.info(methodName, "Updated Subscriber Profile");
                xdmServerDAO.updateSubscrProfile(subsProfilePersistDTO, persisterTxn);

            }
            //Handles the upgrade package scenario by assigning zones and channels to the subscriber.
            boolean isAutoAssignSkip = false;
            if (isUpgradePkg) {
                int clusterId = Integer.parseInt(System.getenv(com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME));
                String autoAssignZoneChannelFalg = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn).get(AUTO_ASSIGN_ZONE);
                isAutoAssignSkip = provInfoUtil.assignZonesAndChannels(mdn, dbSubsClienType, autoAssignZoneChannelFalg, extCorpId, persisterTxn);
            }
//            responseDTO.setUpgradePkg(isUpgradePkg);
            responseDTO.setAutoAssignSkip(isAutoAssignSkip);
            if(existingSubsProfileDTO.getSubsClientType() == KnProvConstants.SUBS_CLIENT_TYPE.STANDALONECAMERA.value()
                    && cameraInfo != null){
                Collection<String> reqMdns=new ArrayList<>();
                reqMdns.add(mdn);
                Map<String, KnSubsCameraInfo> dbCameraInfo = xdmServerDAO.getSubscriberCameraInfo(reqMdns, false, persisterTxn);
                if(cameraInfo!=null
                        &&cameraInfo.getCameraSerialId()==null&&cameraInfo.getIpIdentifier()==null){
                    xdmServerDAO.deleteSubscriberCameraInfo(mdn,persisterTxn);
                }else if(!dbCameraInfo.isEmpty()&&cameraInfo!=null&&cameraInfo.getIpIdentifier()!=null){
                    xdmServerDAO.updateSubscriberCameraInfo(mdn,cameraInfo,persisterTxn);
                }else{
                    subsProfilePersistDTO.setCameraInfo(cameraInfo);
                    xdmServerDAO.createSubscriberCameraInfo(subsProfilePersistDTO,persisterTxn);
                }
            }

            if(removeFromList)
            {
                knLogger.debug("removing AliasIdInfoList change ");
                xdmServerDAO.removeSubsAliasId(subsProvInputDTO.getMdn(),subsProvInputDTO.getRemoveAliasInfoList(),persisterTxn);
                knLogger.debug("Deleted existing AliasIdInfo");
            }
            if(!aliasInfoAlreadyExist)
            {
                knLogger.debug("Adding AliasIdInfoList change ");
                 xdmServerDAO.insertIntoSubsAliasId(subsProvInputDTO.getAddAliasInfoList(),subsProvInputDTO.getMdn(),persisterTxn);
                knLogger.debug("Inserted New AliasIdInfo");
            }

            subsProfilePersistDTO.setLicenseType(existingSubsProfileDTO.getLicenseType());
            subsProfilePersistDTO.setMcpttCompliance(existingSubsProfileDTO.getMcpttCompliance());
            updateMCDevice(subsProfilePersistDTO, lastProfileUpdateTime,responseDTO, persisterTxn);

            String subsFs2 = subsProfilePersistDTO.getSubsFS2();
			boolean upmBit = KnGeneralUtil.getFeatureBitValue(subsFs2, USER_PROFILE_MGMT_BIT);
			knLogger.debug("upmBit ", upmBit, "subsFs2 ", subsFs2);
			List<String> UserProfileMdns = new ArrayList<String>();
				// check if it is a Real mdn
				if (existingSubsProfileDTO.getUserProfileIndex() == null
						|| existingSubsProfileDTO.getUserProfileIndex().equals(USER_PROFILE_INDEX)) {
					// Get All profile MDNs for given Real MDN
					UserProfileMdns = getMdnForUPM(mdn, persisterTxn);
					if (!UserProfileMdns.isEmpty()) {
						UserProfileMdns.remove(mdn.toString().trim());
					}
				}

            // --> update XDM directory.
            IXDMServerDAO commonXDMServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);

			//check if UPM bit is enabled and if SubsFS2/SubscriberName/PublicOrCoprSubsriptionType is updated then update the
		    //fields in profile MDNs as well if UPM bit is disabled no need to update since the profile MDN's will be deleted asynchronously
				 Map<String, KnOPSubsProfileInfoDTO> mdnUpmFsMap=null;
				 Map<String, String> profileMdnActivsFsMap=null;
			if(upmBit&&existingSubsProfileDTO.getUserProfileIndex() == null
					|| existingSubsProfileDTO.getUserProfileIndex().equals(USER_PROFILE_INDEX))
			{
				knLogger.debug("updateProfileMdnDetails ", "isNameUpdated--", isNameUpdated, "isSubsTypeUpdated-- ",
						isSubsTypeUpdated, "isSubsFS2Updated--", isSubsFS2Updated);

				//xdmServerDAO.updateProfileMdnDetails(subsProfilePersistDTO,UserProfileMdns,persisterTxn);
				
				//updating fields for profileMdn.
                mdnUpmFsMap = xdmServerDAO.getProfileMdnNupmfsByBaseMdn(mdn, false, persisterTxn);
                mdnUpmFsMap.remove(mdn);

				if (mdnUpmFsMap.size() > 0) {

				profileMdnActivsFsMap = provInfoUtil.calculateActiveFS2WithUPMFS(
						subsProfilePersistDTO.getCorporateSubscriptionType(), pocPTTIdActiveFS2, presencePTTIdActiveFS2, xdmsPttId,
						clientFS2, subsFS2, corpFS2, opsFS2, corpAdminFS2, clientCapOverrideBitMask, 
						mdnUpmFsMap);

				Collection<KnSubsProfileDTO> subsSpecificDetails = xdmServerDAO
                        .fetchSubsSpecificDetailsForBulkMdns(new ArrayList<>(profileMdnActivsFsMap.keySet()), false, persisterTxn);
                Map<String, String> mdnUpmIdMap =new HashMap<>();
                Map<String, String> mdnActiveFsMap =new HashMap<>();
                for(KnSubsProfileDTO subsInfo:subsSpecificDetails){
                        mdnUpmIdMap.put(subsInfo.getMdn(),subsInfo.getUserProfileId());
                        mdnActiveFsMap.put(subsInfo.getMdn(),subsInfo.getActiveFS2());
                }
				subsProfilePersistDTO.setServiceAuthStatus(existingSubsProfileDTO.getServiceAuthStatus());
				subsProfilePersistDTO.setLastActivationTime(existingSubsProfileDTO.getLastActivationTime());
				subsProfilePersistDTO.setSwType(existingSubsProfileDTO.getSwType());
				subsProfilePersistDTO.setPlatformType(existingSubsProfileDTO.getPlatformType());
				subsProfilePersistDTO.setDynamicQosFlag(existingSubsProfileDTO.getDynamicQosFlag());
				subsProfilePersistDTO.setServiceStatusOp(existingSubsProfileDTO.getServiceStatusOp());
				xdmServerDAO.updateMdnFiledsNActiveFS(subsProfilePersistDTO, profileMdnActivsFsMap, persisterTxn);

				Map<String,List<Integer>> upmIdGroupMap=provInfoUtil.retriveUpmIdGroupMapByUserProfileId(new ArrayList<>(mdnUpmIdMap.values()),persisterTxn);
				knLogger.debug(methodName," mdnUpmIdMap :",mdnUpmIdMap," existingActiveFS2 :",mdnActiveFsMap);
				knLogger.debug(methodName," profileMdnActivsFsMap :",profileMdnActivsFsMap);

                    //Updating isAffiliationEnabled flag for Profile Mdns
                    Map<String, String> mdnIsAffiliationEnabledMap = new HashMap<>();
                    for (Map.Entry<String, String> mdnFsMap : profileMdnActivsFsMap.entrySet()) {
                        Integer isAffiliationEnabledForProfileMdns = DISABLED;
                        boolean bitEnabledForProfileMdns = KnGeneralUtil.getFeatureBitValue(
                                mdnFsMap.getValue(),
                                com.kodiak.common.resources.KnConstants.FEATURE_SET.AFFILIATIONFEATURE.value());
                        if (subsProfilePersistDTO
                                .getClientPVmajorVer() >= com.kodiak.xdms.server.common.resources.KnConstants.PROTOCOL_VERSION_18
                                && bitEnabledForProfileMdns
                                && checkClientType(subsProfilePersistDTO.getSubsClientType(),
                                existingSubsProfileDTO.getMcpttCompliance())) {
                            isAffiliationEnabledForProfileMdns = ENABLED;
                        }
                        mdnIsAffiliationEnabledMap.put(mdnFsMap.getKey(),
                                isAffiliationEnabledForProfileMdns.toString());
                        //mcxGroup clean job


                        //for (Map.Entry<String, String> profieMdnActiveFS : profileMdnActivsFsMap.entrySet()) {
                        String profileMdnActiveFs = mdnFsMap.getValue();
                        boolean veryLargeGroupBit = KnGeneralUtil.getFeatureBitValue(profileMdnActiveFs, VERY_LARGE_GROUP);
                        String profileMdn = mdnFsMap.getKey();
                        String upmId = mdnUpmIdMap.get(profileMdn);
                        String existingActiveFS2 = mdnActiveFsMap.get(profileMdn);
                        List<Integer> mcxGroups = upmIdGroupMap.get(upmId);
                        if (!veryLargeGroupBit && mcxGroups != null && !mcxGroups.isEmpty()) {
                            //adding to mcxGroup clean up job on profile mdn 81 bit disable
                            com.kodiak.xdms.server.common.util.KnGeneralUtil.veryLargeGroupBitChanged(existingActiveFS2
                                    , profileMdnActiveFs
                                    , profileMdn
                                    , String.valueOf(subsProfilePersistDTO.getCorpId()), true);
                        }
                    }
                    commonXDMServerDAO.updateAffForCorpGrpMemList(mdnIsAffiliationEnabledMap, persisterTxn);
                }
            }

			if (isProfileUpdated && isPkgCodeUpdated) {
				xdmServerDAO.deleteSubAddlOnPkgs(mdn, persisterTxn);

				if (!addonPackageIds.isEmpty()) {
					knLogger.debug(methodName, "addon pkg is present ", addonPackageIds);
					xdmServerDAO.createSubAddOnPkgs(mdn, new ArrayList<>(addonPackageIds.keySet()), persisterTxn);
				}

                List<String> profileMdn = new ArrayList<>();
                if (null != profileMdnActivsFsMap && null != profileMdnActivsFsMap.entrySet()) {
                    for (Map.Entry<String, String> profieMdnActiveFS : profileMdnActivsFsMap.entrySet()) {
                        profileMdn.add(profieMdnActiveFS.getKey());
                    }
                }
				// -->. updating entry into the pocsubsaddlinfo

				KnSubsAddlInfoPersistDTO subsAddlProfilePersistDTO = new KnSubsAddlInfoPersistDTO();
				subsAddlProfilePersistDTO.setMdn(mdn);
                subsAddlProfilePersistDTO.setProfilemdn(profileMdn);
				subsAddlProfilePersistDTO.setTierPkgCode(tierPackageId);
				subsAddlProfilePersistDTO.setDataPkgId(dataPkgId);
                Integer onBoardingMail = subsProvInputDTO.getOnBoardingEmailReqd();
                if (allProfileDto != null && allProfileDto.getOnBoardingMailReqd() != null
                        && allProfileDto.getOnBoardingMailReqd().equals(subsProvInputDTO.getOnBoardingEmailReqd())) {
                    onBoardingMail = allProfileDto.getOnBoardingMailReqd();
                }
                subsAddlProfilePersistDTO.setOnBoardingMailReqd(onBoardingMail);
				// check subscriber addlInfo exists
				if (allProfileDto != null) {
					xdmServerDAO.updateSubscrPkgAddlInfo(subsAddlProfilePersistDTO, persisterTxn);
				} else {
					subsAddlProfilePersistDTO.setTimeSlotType(KnConstants.TIME_SLOT_TYPE);
					xdmServerDAO.createSubscrPkgAddlInfo(subsAddlProfilePersistDTO, persisterTxn);
				}
                //updating the pkg info for profilemdn
                knLogger.debug(methodName, "profilemdns", profileMdn);
                if (null != profileMdn) {
                    xdmServerDAO.updateSubscrPkgAddlInfoforProfileMdn(subsAddlProfilePersistDTO, persisterTxn);
                }
			} else if((allProfileDto != null && allProfileDto.getOnBoardingMailReqd() != null
                    && !allProfileDto.getOnBoardingMailReqd().equals(subsProvInputDTO.getOnBoardingEmailReqd()))){
                KnSubsAddlInfoPersistDTO subsAddlProfilePersistDTO = new KnSubsAddlInfoPersistDTO();
                subsAddlProfilePersistDTO.setMdn(mdn);
                subsAddlProfilePersistDTO.setOnBoardingMailReqd(subsProvInputDTO.getOnBoardingEmailReqd());
                xdmServerDAO.updateSubscrOnBoardingMail(subsAddlProfilePersistDTO, persisterTxn);
            }
            //newExtCorpId will be coming from the request
            //oldExtCorpId will be from the DB
            //oldSub
            knLogger.debug(methodName, "Before Calling Update Etag for Corp:-oldExtCorpId=", oldExtCorpId, " ,newExtCorpId=", newExtCorpId);
            if (oldExtCorpId != null && !oldExtCorpId.equals(newExtCorpId)) {
                if (oldExtCorpId != null) {

                    updateEtagForNNISubscr(oldExtCorpId, persisterTxn);
                }
                if (newExtCorpId != null && subsProvInputDTO.getCorporateSubscriptionType() != 0) {
                    updateEtagForNNISubscr(newExtCorpId, persisterTxn);
                }

            } else {
                if (newExtCorpId != null && subsProvInputDTO.getCorporateSubscriptionType() != 0) {
                    updateEtagForNNISubscr(newExtCorpId, persisterTxn);
                }
            }
            /*if(oldExtCorpId != newExtCorpId){
                if(oldExtCorpId != null){
                    knLogger.debug(methodName,"oldExtCorpId != null, calling updateEtagForNNISubscr method");
                    updateEtagForNNISubscr(oldExtCorpId,persisterTxn);
                }
                if(newExtCorpId !=null && subsProvInputDTO.getCorporateSubscriptionType()!=0){
                    updateEtagForNNISubscr(newExtCorpId,persisterTxn);
                }

            }else{
                //if
                if(newExtCorpId!=null  && subsProvInputDTO.getCorporateSubscriptionType()!=0){
                    updateEtagForNNISubscr(newExtCorpId,persisterTxn);
                }
            }*/
            // --> subscriber roaming profile table
            ArrayList<Integer> receivedRoamingTypeList = subsProfilePersistDTO.getRoamingTypes();

            //Update roaming profile as per below
            // 1. if passed null no change in the existing profile
            // 2. if passed with -1 as input clean up the roaming profile and create roaming profile with default roaming Id
            // 3. if any values are passed clean up the roaming profile and create roaming profile with new data
            if (receivedRoamingTypeList != null && !receivedRoamingTypeList.isEmpty()) {
                ArrayList<Integer> roamingTypeList = new ArrayList<Integer>();

                if (receivedRoamingTypeList.contains(-1)) {
                    knLogger.debug(methodName, "creating the roaming profile with default value");
                    roamingTypeList.add(KnProvConstants.DEFAULT_ROAMING_CLUSTER_ID);
                } else {
                    HashMap<Integer, String> supportedRoamingClusterInfo = (HashMap<Integer, String>) provInfoUtil.retrieveSupportedRoamingList();
                    for (int clusterName : receivedRoamingTypeList) {
                        for (int roamingID : supportedRoamingClusterInfo.keySet()) {
                            if (String.valueOf(clusterName).equalsIgnoreCase(supportedRoamingClusterInfo.get(roamingID)) && !roamingTypeList.contains(roamingID)) {
                                roamingTypeList.add(roamingID);
                                break;
                            }
                        }
                    }
                }

                knLogger.debug(methodName, "Removing the subscriber Roaming profile");
                xdmServerDAO.deleteSubscrRoamingProfile(mdn, persisterTxn);

                //Client_type check for roam_na list
                if (null == paramNameValueMap.get(KnConstants.ROAM_NA_CLIENT_TYPES)) {
                    throw new KnProvBOException(KnErrorCodes.BOEntity.SERVER_CONFIGURATION_FAILURE, "ROAM_NA_CLIENT_TYPE parameter not found");
                }
                String paramValue = (String) paramNameValueMap.get(KnConstants.ROAM_NA_CLIENT_TYPES);
                List<String> roamNAClientTypes = new ArrayList<>();
                if (null != paramValue) {
                    roamNAClientTypes = Arrays.asList(paramValue.split(","));
                    knLogger.debug(methodName, "Roaming not applicable client type ", roamNAClientTypes);
                }
                if (roamNAClientTypes.contains(String.valueOf(newSubsClientType))) {
                    knLogger.debug(methodName, "MDN new client type present in ROAMING NOT APPLICABLE CLIENT TYPE ", newSubsClientType);
                    ArrayList<Integer> roamingAllowed = new ArrayList<>();
                    roamingAllowed.add(KnProvConstants.DEFAULT_ROAMING_CLUSTER_ID);
                    knLogger.debug(methodName, "adding the subscriber roaming profile with default values");
                    xdmServerDAO.createSubscrRoamingProfile(mdn, roamingAllowed, persisterTxn);

                } else {
                    knLogger.debug(methodName, "adding the subscriber roaming profile");
                    xdmServerDAO.createSubscrRoamingProfile(mdn, roamingTypeList, persisterTxn);
                }

            }


            if (oldCorpType == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value() &&
                    newCorpType == KnConstants.CORP_SUBSCRIPTION_TYPE.NONE.value()) {
                //Remove the entry from DG.XDM_CorpResourceListIndexDoc
                knLogger.debug(methodName, "change in corp subs type to none");
                knLogger.debug(methodName, "deleting an entry into corp resource list index doc");
                commonXDMServerDAO.deleteCorpResourceListIndexDoc(mdn, persisterTxn);
                knLogger.debug(methodName, "deleted entry -");

            } else if (oldCorpType == KnConstants.CORP_SUBSCRIPTION_TYPE.NONE.value() &&
                    newCorpType == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
                //Create an entry to DG.XDM_CorpResourceListIndexDoc
                knLogger.debug(methodName, "change in corp subs type to corporate from none");
                knLogger.debug(methodName, "adding an entry into corp resource list index doc");
                commonXDMServerDAO.addMdnToCorpResourceListIndexDoc(mdn, persisterTxn);
                knLogger.debug(methodName, "added entry -");

            }
            int isAffiliationEnabled;
            boolean bitEnabled = KnGeneralUtil.getFeatureBitValue(subsProfilePersistDTO.getActiveFS2(), com.kodiak.common.resources.KnConstants.FEATURE_SET.AFFILIATIONFEATURE.value());
			if (subsProfilePersistDTO.getClientPVmajorVer() >= com.kodiak.xdms.server.common.resources.KnConstants.PROTOCOL_VERSION_18
				&& bitEnabled && checkClientType(subsProfilePersistDTO.getSubsClientType(),existingSubsProfileDTO.getMcpttCompliance())) 
			{
                isAffiliationEnabled = ENABLED;
            } else {
                isAffiliationEnabled = DISABLED;
            }

			commonXDMServerDAO.updateAffForCorpGrpMemList(subsProfilePersistDTO.getMdn(),isAffiliationEnabled,persisterTxn);

            int previousEtag = commonXDMServerDAO.getCurrentEtagForDirDoc(mdn, persisterTxn);
            commonXDMServerDAO.updateEtagForDirDoc(mdn, persisterTxn);

            //IAPN changes
            String xcapRooturi = genInfoUtil.getXCAPRootURI(mdn, persisterTxn);

            int clusterId = Integer.parseInt(System.getenv(CLUSTERID_ENV_NAME));
            String oneMessageEnabled = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn).get(KnConstants.ONE_MESSAGE_SERVICE_ENABLED);
            knLogger.debug(methodName, "oneMessageEnabled", oneMessageEnabled);

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }

            //populate the response DTO
            //profile Mdns are set if upmBit is disabled to delete them asynchronously in the Mediator
            if(!upmBit) {
            responseDTO.setUserProfileMdns(UserProfileMdns);
            }
            responseDTO.setCorpId(subsProfilePersistDTO.getCorpId());
            responseDTO.setActiveFSChanged(isActiveFSUpdated);
            responseDTO.setSubsNameChanged(isNameUpdated);
            responseDTO.setSubsTypeChanged(isSubsTypeUpdated);
            responseDTO.setActiveFS2(activeFS2);
            responseDTO.setSubsFS2(subsFS2);
            responseDTO.setCleanUpTGSData(cleanUpTGSData);
            responseDTO.setPocPttId(pocPttId);
            responseDTO.setPresPttId(presencePttId);
            responseDTO.setMdnUpmFsMap(mdnUpmFsMap);
			responseDTO.setLastProfileUpdateTime(lastProfileUpdateTime);
			responseDTO.setProfileMdnActivsFsMap(profileMdnActivsFsMap);
			responseDTO.setMcId(existingSubsProfileDTO.getMcId());
			responseDTO.setMcDataId(existingSubsProfileDTO.getMcDataId());
			responseDTO.setMcPttId(existingSubsProfileDTO.getMcpttId());
			responseDTO.setMcVideoId(existingSubsProfileDTO.getMcVideoId());
			responseDTO.setServiceAuthStatus(existingSubsProfileDTO.getServiceAuthStatus());
			responseDTO.setNetworkName(existingSubsProfileDTO.getNetworkName());
            responseDTO.setSubsClientType(existingSubsProfileDTO.getSubsClientType());
            if (oldPublicType == KnConstants.PUBLIC_SUBSCRIPTION_TYPE.PUBLIC.value() &&
                    newPublicType == KnConstants.PUBLIC_SUBSCRIPTION_TYPE.NONE.value()) {
                responseDTO.setResponseMessage(KnProvConstants.UPDATE_SUBS_PROFILE_SUCCESS);
                responseDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.SUCCESS.value());
                KnOPDirChgDTO dirChgDTO = new KnOPDirChgDTO();
                // KnXDMSServiceConfigDTO xdmsServiceConfigDTO = genInfoUtil.retrieveXDMSServiceConfig(xdmPttServerId, persisterTxn);
                dirChgDTO.setXcapRootURI(xcapRooturi);
                dirChgDTO.setPocHome(pocPttId);
                dirChgDTO.setPresenceHome(presencePttId);
                KnOPDocChgDTO docChgDTO = new KnOPDocChgDTO();
                docChgDTO.setDocumentChgType(KnConstants.DOC_CHANGE_TYPE.REPLACE.value());
                String dirDocUri = genInfoUtil.generateDirDocUri(mdn);
                dirChgDTO.setDirUri(dirDocUri);
                int newEtag = previousEtag + 1;
                dirChgDTO.setDirNewEtag(String.valueOf(newEtag));
                responseDTO.setDirChgDTO(dirChgDTO);
            } else {
                responseDTO.setResponseMessage(KnProvConstants.UPDATE_SUBS_PROFILE_SUCCESS);
                responseDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.SUCCESS.value());
                responseDTO.setEtag(lastProfileUpdateTime);
                //populating the Subs Config document change DTO
                KnOPDocChgDTO docChgDTO = new KnOPDocChgDTO();
                docChgDTO.setDocumentChgType(KnConstants.DOC_CHANGE_TYPE.REPLACE.value());
                String subsConfigDocUri = provInfoUtil.generateSubsConfigSelUri(mdn);
                docChgDTO.setDocUri(subsConfigDocUri);
                docChgDTO.setNewEtag(String.valueOf(lastProfileUpdateTime));

                Collection<KnOPDocChgDTO> docChgList = new ArrayList<KnOPDocChgDTO>();
                docChgList.add(docChgDTO);

                //populating Dir Document change DTO
                KnOPDirChgDTO dirChgDTO = new KnOPDirChgDTO();
                /*  KnXDMSServiceConfigDTO xdmsServiceConfigDTO = genInfoUtil.retrieveXDMSServiceConfig(xdmPttServerId, persisterTxn);
                  //dirChgDTO.setXcapRootURI(xdmsServiceConfigDTO.getXcapRootUri());
                */
                dirChgDTO.setXcapRootURI(xcapRooturi);
                dirChgDTO.setPocHome(pocPttId);
                dirChgDTO.setPresenceHome(presencePttId);
                dirChgDTO.setDocChgDTO(docChgList);
                String dirDocUri = genInfoUtil.generateDirDocUri(mdn);
                dirChgDTO.setDirUri(dirDocUri);
                dirChgDTO.setDirPrevEtag(String.valueOf(previousEtag));
                int newEtag = previousEtag + 1;
                dirChgDTO.setDirNewEtag(String.valueOf(newEtag));

                //setting the Document Change DTO the response
                responseDTO.setDirChgDTO(dirChgDTO);
                if(!UserProfileMdns.isEmpty())
                {
                	List<KnOPDirChgDTO> dirChgDTOs= new ArrayList<KnOPDirChgDTO>();
                	for (String profileMdn : UserProfileMdns) {
                		KnOPDocChgDTO profileMdnDocChgDTO = new KnOPDocChgDTO();
                		profileMdnDocChgDTO.setDocumentChgType(KnConstants.DOC_CHANGE_TYPE.REPLACE.value());
                		String profileMdnSubsConfigDocUri = provInfoUtil.generateSubsConfigSelUri(profileMdn);
                		profileMdnDocChgDTO.setDocUri(profileMdnSubsConfigDocUri);
                		profileMdnDocChgDTO.setNewEtag(String.valueOf(lastProfileUpdateTime));
                		
                		Collection<KnOPDocChgDTO> profileMdnDocChgList = new ArrayList<KnOPDocChgDTO>();
                		profileMdnDocChgList.add(profileMdnDocChgDTO);
                		//populating Dir Document change DTO
                		KnOPDirChgDTO profileMdnDirChgDTO = new KnOPDirChgDTO();
                		profileMdnDirChgDTO.setXcapRootURI(xcapRooturi);
                		profileMdnDirChgDTO.setPocHome(pocPttId);
                		profileMdnDirChgDTO.setPresenceHome(presencePttId);
                		profileMdnDirChgDTO.setDocChgDTO(profileMdnDocChgList);
                		String profileMdnDirDocUri = genInfoUtil.generateDirDocUri(profileMdn);
                		profileMdnDirChgDTO.setDirUri(profileMdnDirDocUri);
                		profileMdnDirChgDTO.setDirPrevEtag(String.valueOf(previousEtag));
                		int profileMdnNewEtag = previousEtag + 1;
                		profileMdnDirChgDTO.setDirNewEtag(String.valueOf(profileMdnNewEtag));
                		
                		dirChgDTOs.add(profileMdnDirChgDTO);
					}
                	responseDTO.setDirChgDTOs(dirChgDTOs);
                }
            }
            
          //updating LASTPROFILEUPDATETIME in POCSUBSCRINFO profile mdn notiy
           if(mdnUpmFsMap!=null&&mdnUpmFsMap.size()>0)
           {
			IProvXDMServerDAO xdmServerDAONew = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB)
					.createProvXDMServerDAO();
			Map<String, Collection<KnDocChangeListDTO>> profileMdnEtagMap = xdmServerDAONew
					.profileMdnEtagUpdate(UserProfileMdns, persisterTxn);
			responseDTO.setProfileMdnEtagMap(profileMdnEtagMap);
			if (profileMdnEtagMap != null && !profileMdnEtagMap.isEmpty()) {
				responseDTO.setMcsXcapRootUriMap(
						genInfoUtil.getXCAPRootURI(new ArrayList<>(profileMdnEtagMap.keySet()), persisterTxn, true));
			}
           }
            knLogger.debug(methodName, "responseDTO from Controller :",responseDTO.getServiceAuthStatus());
            //updating the success pegs
            responseDTO.setSuccessPegs(successPegs);

            //welcome sms notification..
            if (oldSubsClientType != newSubsClientType) {
                //if one message is disabled then go in old flow
                if (null != oneMessageEnabled && oneMessageEnabled.equals(ONE_MSG_STATUS.ENABLED.value())) {
               if (newSubsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.HANDSET.value() ||
            		newSubsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.POCDONORRADIO.value()){
            	 sendSMSNotification(newSubsClientType, mdn, KnProvConstants.WELCOME_SMS_ID);
               }
            }
            }

            knLogger.debug(methodName,"responseDTO: ",responseDTO ,"[ subsProvInputDTO  ]",subsProvInputDTO);

            knLogger.info(methodName, "EXIT: Update Subscriber operation ");

            return responseDTO;
        } catch (KnFeatureSetException ex) {
            knLogger.error(methodName, "Feature Set Exception occured :", ex);
            if(isUpgradePkg) {
                KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_UPGRADE_PACKAGE_WITH_CLIENT_CHANGE_FAILURE);
            }
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw new KnProvBOException(ex.getErrorCode(), ex.getMessage(), ex);

        } catch (KnDBConnectionException ex) {
            knLogger.error(methodName, "DAO DBConnection Exception occured :", ex);
            if(isUpgradePkg) {
                KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_UPGRADE_PACKAGE_WITH_CLIENT_CHANGE_FAILURE);
            }
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);

        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occurred : ", ex);
            knLogger.error(methodName, ex);
            if(isUpgradePkg) {
                KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_UPGRADE_PACKAGE_WITH_CLIENT_CHANGE_FAILURE);
            }
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            if (com.kodiak.xdms.server.common.resources.KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_INFO_NOT_FOUND, "Subscriber does not exists");
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception occurred :", e);
            knLogger.error(methodName, e);
            if(isUpgradePkg) {
                KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_UPGRADE_PACKAGE_WITH_CLIENT_CHANGE_FAILURE);
            }
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw e;
        } catch (KnValidationException vex) {
            knLogger.error(methodName, "Validation Exception occurred :", vex);
            if(isUpgradePkg) {
                KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_UPGRADE_PACKAGE_WITH_CLIENT_CHANGE_FAILURE);
            }
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw vex;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while Update Subscriber");
            knLogger.error(methodName, e);
            if(isUpgradePkg) {
                KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_UPGRADE_PACKAGE_WITH_CLIENT_CHANGE_FAILURE);
            }
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred while update Subscriber", e);
        }

    }

    private void updateMCDevice(KnSubsProfilePersistDTO persistDTO, Long lastProfileUpdateTime, KnOPProvDTO responseDTO,
			KnPersisterTxn persisterTxn) throws KnDAOException, KnProvBOException {

		KnDeviceInfoPersistDTO deviceInfoPersistDTO = new KnDeviceInfoPersistDTO();
		deviceInfoPersistDTO.setDeviceId(persistDTO.getMdn());
		IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB)
				.createXDMServerDAO(xdmPttServerId);
		KnXDMDeviceProvDTO deviceProfile = xdmServerDAO.selectDeviceProfileByDeviceId(persistDTO.getMdn(), persisterTxn);
		if (KnGeneralUtil.getFeatureBitValue(persistDTO.getSubsFS2(), FEATURE_SET.MCDEVICE.value())
        && !(KnConstants.MCSCOMPLIANCE == persistDTO.getMcpttCompliance())) {
			if (deviceProfile != null) {
				deviceInfoPersistDTO.setDeviceshared(KnConstants.MCDEVICESHARED_TYPE.SHARED.Value());
				deviceInfoPersistDTO.setReqDeviceId(persistDTO.getMdn());
				deviceInfoPersistDTO.setDeviceType(DEVICE_TYPE.MC_DEVICE.Value());
				deviceInfoPersistDTO.setCorpId(persistDTO.getCorpId());
				xdmServerDAO.updateDeviceInfo(deviceInfoPersistDTO, persisterTxn);
			} else {

				if (lastProfileUpdateTime == null) {
					lastProfileUpdateTime = Calendar.getInstance().getTimeInMillis();
				}
				deviceInfoPersistDTO.setDeviceStatus(KnConstants.DEVICE_STATUS_OP.ACTIVATED.value());
				deviceInfoPersistDTO.setDeviceActTimeStamp(lastProfileUpdateTime);
				deviceInfoPersistDTO.setDeviceLastUsed(lastProfileUpdateTime);
				deviceInfoPersistDTO.setDeviceType(DEVICE_TYPE.MC_DEVICE.Value());
				deviceInfoPersistDTO.setDeviceIMPI(KnConstants.TEL_URI_TEMPLATE + persistDTO.getMdn());
				deviceInfoPersistDTO.setDeviceshared(KnConstants.MCDEVICESHARED_TYPE.SHARED.Value());
				deviceInfoPersistDTO.setReqDeviceId(persistDTO.getMdn());
				deviceInfoPersistDTO.setCorpId(persistDTO.getCorpId());
				deviceInfoPersistDTO.setDeviceCreatedAs(KnConstants.DEVICECREATEDAS);

                xdmServerDAO.createDeviceInfo(deviceInfoPersistDTO, persisterTxn);

                KnDeviceImpiInfoPersistDTO deviceImpiInfo = new KnDeviceImpiInfoPersistDTO();
				deviceImpiInfo.setDeviceImpi(KnConstants.TEL_URI_TEMPLATE + persistDTO.getMdn());
				deviceImpiInfo.setDeviceImpu(KnConstants.TEL_URI_TEMPLATE + persistDTO.getMdn());
				xdmServerDAO.createDeviceImpiInfo(deviceImpiInfo, persisterTxn);
			}

		} else if (!KnGeneralUtil.getFeatureBitValue(persistDTO.getSubsFS2(), FEATURE_SET.MCDEVICE.value())
				&& deviceProfile != null && deviceProfile.getDeviceType()==DEVICE_TYPE.MC_DEVICE.Value()
                && !(KnConstants.MCSCOMPLIANCE == persistDTO.getMcpttCompliance())) {
            deviceInfoPersistDTO.setDeviceType(DEVICE_TYPE.RADIO_NEXT_DEVICE.Value());
            deviceInfoPersistDTO.setDeviceId(deviceProfile.getDeviceId());
            xdmServerDAO.updateDeviceType(deviceInfoPersistDTO, persisterTxn);
		}
    }
    private void valiadteIfMCXIdsExistsInSystemForUpdate(KnIPSubsProvInfoDTO subsProvInputDTO,
			KnPersisterTxn persisterTxn)
			throws KnDAOException, KnProvBOException {
		String methodName = "valiadteIfMCXIdsExistsInSystemForUpdate(KnIPSubsProvInfoDTO, KnPersisterTxn)";
		Set<String> mcsIdSet= new HashSet<String>();
		if (checkIfMcxIdIsNotEqualToRequestMdn(subsProvInputDTO.getMcId(), subsProvInputDTO.getMdn())
				|| checkIfMcxIdIsNotTELURI(subsProvInputDTO.getMcId())) {
			mcsIdSet.add(subsProvInputDTO.getMcId());
		}
		if (checkIfMcxIdIsNotEqualToRequestMdn(subsProvInputDTO.getMcpttId(), subsProvInputDTO.getMdn())
				|| checkIfMcxIdIsNotTELURI(subsProvInputDTO.getMcpttId())) {
			mcsIdSet.add(subsProvInputDTO.getMcpttId());
		}
		if (checkIfMcxIdIsNotEqualToRequestMdn(subsProvInputDTO.getMcVideoId(), subsProvInputDTO.getMdn())
				|| checkIfMcxIdIsNotTELURI(subsProvInputDTO.getMcVideoId())) {
			mcsIdSet.add(subsProvInputDTO.getMcVideoId());
		}
		if (checkIfMcxIdIsNotEqualToRequestMdn(subsProvInputDTO.getMcDataId(), subsProvInputDTO.getMdn())
				|| checkIfMcxIdIsNotTELURI(subsProvInputDTO.getMcDataId())) {
			mcsIdSet.add(subsProvInputDTO.getMcDataId());
		}
		List<String> mcsIds = new ArrayList<String>();
		mcsIds.addAll(mcsIdSet);
		if (!mcsIds.isEmpty()) {
			IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB)
					.createProvXDMServerDAO();
			List<KnOPSubsProfileInfoDTO> subsProfileInfoDTOList = provXDMServerDAO
					.selectSubscriberProfileByMCSIds(mcsIds, persisterTxn);
			if (!subsProfileInfoDTOList.isEmpty()) {
				for (KnOPSubsProfileInfoDTO knOPSubsProfileInfoDTO : subsProfileInfoDTOList) {
					if (!knOPSubsProfileInfoDTO.getMdn().equals(subsProvInputDTO.getMdn())) {
						knLogger.error(methodName, "MCS Id(s) already exists.");
						throw new KnProvBOException(KnErrorCodes.BOEntity.MCSIDS_ALREADY_EXISTS,
								"MCS Id(s) already exists.");
					}
				}
			}
		}
	}

	private void populateDefaultMCXIdsForUpdate(KnIPSubsProvInfoDTO subsProvInputDTO) {
		if (subsProvInputDTO.getMcId() != null && subsProvInputDTO.getMcId().isEmpty()) {
			subsProvInputDTO.setMcId(TELURI + subsProvInputDTO.getMdn());
		}
		if (subsProvInputDTO.getMcpttId() != null && subsProvInputDTO.getMcpttId().isEmpty()) {
			subsProvInputDTO.setMcpttId(TELURI + subsProvInputDTO.getMdn());
		}
		if (subsProvInputDTO.getMcVideoId() != null && subsProvInputDTO.getMcVideoId().isEmpty()) {
			subsProvInputDTO.setMcVideoId(TELURI + subsProvInputDTO.getMdn());
		}

		if (subsProvInputDTO.getMcDataId() != null && subsProvInputDTO.getMcDataId().isEmpty()) {
			subsProvInputDTO.setMcDataId(TELURI + subsProvInputDTO.getMdn());
		}
	}

	private void validateMCXIdsUriFormat(KnIPSubsProvInfoDTO subsProvInputDTO, KnPersisterTxn persisterTxn)
			throws KnBOException, KnProvBOException {
		// checking if the MCX IDs starts with the Configured URI Schemes
		String methodName = "validateMCXIdsUriFormat(KnIPSubsProvInfoDTO, KnPersisterTxn)";
		int clusterId = Integer.parseInt(System.getenv(CLUSTERID_ENV_NAME));
		String mcxUriScheme = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn)
				.get(KnConstants.MCX_URI_SCHEMES);
		knLogger.debug(methodName, "Configured URI Schemes :", mcxUriScheme);
		List<String> mcxUriScemeList = new ArrayList<String>();
		if (mcxUriScheme != null) {
			String[] mcxUriSchemes = mcxUriScheme.split(KnConstants.COMMA);
			mcxUriScemeList = Arrays.asList(mcxUriSchemes);
		}
		if (!mcxUriScemeList.contains(STAR)) {
			validateMcsId(subsProvInputDTO.getMcId(), mcxUriScemeList);
			validateMcsId(subsProvInputDTO.getMcpttId(), mcxUriScemeList);
			validateMcsId(subsProvInputDTO.getMcVideoId(), mcxUriScemeList);
			validateMcsId(subsProvInputDTO.getMcDataId(), mcxUriScemeList);
		}
	}

	private boolean checkIfMcxIdIsNotTELURI(String mcxId) {
		return null != mcxId && !mcxId.isEmpty() && !mcxId.startsWith(TELURI);
	}

	private boolean checkIfMcxIdIsNotEqualToRequestMdn(String mcxId, String mdn) {
		return null != mcxId && mcxId.startsWith(TELURI)
				&& !mcxId.substring(TELURI.length()).equals(mdn);
	}

	private void validateMcsId(String mcsID, List<String> mcxUriScemeList) throws KnProvBOException {
		Boolean flag = false;
		if (mcsID!=null && !mcsID.isEmpty()) {
			for (String mcxUriSceme : mcxUriScemeList) {
				if (mcsID.startsWith(mcxUriSceme)) {
					flag = true;
					break;
				}
			}
			if (!flag) {
				throw new KnProvBOException(KnErrorCodes.BOEntity.INVALID_MCSIDS, "Invalid MCS Id(s).");
			}
		}
	}

	private void populateDefaultMCSIds(KnIPSubsProvInfoDTO subsProvInputDTO) {
		String defaultMcsId = null;
		if (subsProvInputDTO.getMcId() != null) {
			defaultMcsId = subsProvInputDTO.getMcId();
		} else if (subsProvInputDTO.getMcpttId() != null) {
			defaultMcsId = subsProvInputDTO.getMcpttId();
		} else if (subsProvInputDTO.getMcVideoId() != null) {
			defaultMcsId = subsProvInputDTO.getMcVideoId();
		} else if (subsProvInputDTO.getMcDataId() != null) {
			defaultMcsId = subsProvInputDTO.getMcDataId();
		}
		if (defaultMcsId == null) {
			subsProvInputDTO.setMcId(TELURI + subsProvInputDTO.getMdn());
			subsProvInputDTO.setMcpttId(TELURI + subsProvInputDTO.getMdn());
			subsProvInputDTO.setMcVideoId(TELURI + subsProvInputDTO.getMdn());
			subsProvInputDTO.setMcDataId(TELURI + subsProvInputDTO.getMdn());
		} else if (defaultMcsId != null) {
			if (subsProvInputDTO.getMcId() == null) {
				subsProvInputDTO.setMcId(defaultMcsId);
			}
			if (subsProvInputDTO.getMcpttId() == null) {
				subsProvInputDTO.setMcpttId(defaultMcsId);
			}
			if (subsProvInputDTO.getMcVideoId() == null) {
				subsProvInputDTO.setMcVideoId(defaultMcsId);
			}
			if (subsProvInputDTO.getMcDataId() == null) {
				subsProvInputDTO.setMcDataId(defaultMcsId);
			}

		}
	}

	private void validateIfUserIdAlredyExistsInSystem(String userId, KnPersisterTxn persisterTxn)
			throws KnDAOException, KnProvBOException {
		String methodName = "validateIfUserIdAlredyExistsInSystem(KnIPSubsProvInfoDTO, KnPersisterTxn)";
		if (userId != null && !userId.isEmpty()) {
			KnOPSubsProfileInfoDTO subscriberProfileByUserId = null;
			try {
				IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB)
						.createProvXDMServerDAO();
				subscriberProfileByUserId = provXDMServerDAO.selectSubscriberProfileByUserId(userId, persisterTxn);
			} catch (KnDAOException e) {
				if (com.kodiak.xdms.server.common.resources.KnErrorCodes.DAO.ROW_NOT_FOUND.equals(e.getErrorCode()))
					knLogger.error(methodName, e);
				else {
					throw e;
				}
			}
			if (subscriberProfileByUserId != null) {
				knLogger.error(methodName, "User Id already exists.");
				throw new KnProvBOException(KnErrorCodes.BOEntity.USER_ID_ALREADY_EXISTS, "User Id already exists.");
			}
		}
	}

	private void valiadteIfMCXIdsExistsInSystem(List<String> mcsIds, String mdn, KnPersisterTxn persisterTxn)
			throws KnDAOException, KnProvBOException {
		String methodName = "valiadteIfMCXIdsExistsInSystem(List<String>, String,KnPersisterTxn)";
		if (!mcsIds.isEmpty()) {
			IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB)
					.createProvXDMServerDAO();
			List<KnOPSubsProfileInfoDTO> subsProfileInfoDTOList = provXDMServerDAO
					.selectSubscriberProfileByMCSIds(mcsIds, persisterTxn);
			if (!subsProfileInfoDTOList.isEmpty()) {
				for (KnOPSubsProfileInfoDTO knOPSubsProfileInfoDTO : subsProfileInfoDTOList) {
					if ((TELURI + mdn).equals(knOPSubsProfileInfoDTO.getMcId())
							|| (TELURI + mdn).equals(knOPSubsProfileInfoDTO.getMcpttId())
							|| (TELURI + mdn).equals(knOPSubsProfileInfoDTO.getMcVideoId())
							|| (TELURI + mdn).equals(knOPSubsProfileInfoDTO.getMcDataId())) {
						knLogger.error(methodName, "MDN exist as MCS Id(s).");
						throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_ALREADY_EXISTS,
								"MDN exist as MCS Id(s).");
					}
				}
				knLogger.error(methodName, "MCS Id(s) already exists.");
				throw new KnProvBOException(KnErrorCodes.BOEntity.MCSIDS_ALREADY_EXISTS, "MCS Id(s) already exists.");
			}
		}

	}

	private void valiadteIfMCXIdsExistsAsMdnOrAliasMdn(List<String> mcsIds,
			KnPersisterTxn persisterTxn) throws KnProvBOException, KnDAOException {
		String methodName = "valiadteIfMCXIdsExistsAsMdnOrAliasMdn(List<String>, KnPersisterTxn)";
		if (!mcsIds.isEmpty()) {
			IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB)
					.createProvXDMServerDAO();
			List<KnOPSubsProfileInfoDTO> subsProfileInfoDTOList = provXDMServerDAO
					.selectSubscriberProfileByMDNorAliasMdn(mcsIds, persisterTxn);
			if (!subsProfileInfoDTOList.isEmpty()) {
				knLogger.error(methodName, "MCS Id(s) already exists as MDN or ALIAS MDN.");
				throw new KnProvBOException(KnErrorCodes.BOEntity.MCSIDS_ALREADY_EXISTS, "MCS Id(s) already exists.");
			}

		}
	}

	private void validateIfMdnOrMcidsPresentInKUIDPOOL(List<String> mcsIds) throws KnDAOException, KnProvBOException {
		String methodName = "validateIfMdnOrMcidsPresentInKUIDPOOL(List<String>)";
		String kuidPrefix = generalCacheUtil.retrieveKUIDPrefix();
		if (kuidPrefix != null) {
			for (String telMdn : mcsIds) {
				if (telMdn.startsWith(kuidPrefix)) {
					knLogger.error(methodName, "Operation not allowed for billing/pseudo number");
					throw new KnProvBOException(KnErrorCodes.BOEntity.MDN_PRESENT_IN_KUIDPPOOL,
							"Operation not allowed for billing/pseudo number");
				}
			}
		}
	}


    public KnOPUpdateSubsInfoDTO updateSubscriberUserAgent(KnIPSubsProvInfoDTO subsProvInputDTO, KnPersisterTxn persisterTxn) throws KnProvBOException, KnValidationException {
        String methodName = "updateSubscriberUserAgent(KnIPSubsProvInfoDTO, KnPersisterTxn)";

        boolean ownedTxn = false;
        boolean isProfileUpdated = false;
        KnOPUpdateSubsInfoDTO responseDTO = new KnOPUpdateSubsInfoDTO();

        try {

            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            try {
                xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
            } catch (KnBOException e) {
                knLogger.error(methodName, "failed to retrieve xdm Ptt Sever Id");
                knLogger.error(methodName, e);
                if (ownedTxn) {
                    rollback(persisterTxn);
                }
                throw new KnProvBOException(KnErrorCodes.BOEntity.XDMS_PTT_ID_NOT_FOUND, "Failed to retrieve XDM PTT Server ID", e);
            }


            knLogger.debug(methodName, "Call from update  user Agent :", subsProvInputDTO);
            KnUserAgentDTO userAgentDTO = subsProvInputDTO.getUserAgentDTO();
            String newUserAgent = subsProvInputDTO.getUserAgent();
            String mdn = subsProvInputDTO.getMdn();
            KnOPSubsProfileInfoDTO existingSubsProfileDTO = provInfoUtil.retrieveSubscriberInfo(mdn, persisterTxn);
            KnSubsProfilePersistDTO subsProfilePersistDTO = new KnSubsProfilePersistDTO();
            subsProfilePersistDTO.setMdn(mdn);
            IProvXDMServerDAO xdmServerDAO = KnProvFactorySelector.getProvDAOFactory(KnFactorySelector.DB).createProvXDMServerDAO();
            IXDMServerDAO commonXDMServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);

            // if(existingSubsProfileDTO.getSubsClientType() == KnProvConstants.SUBS_CLIENT_TYPE.HANDSET.value())
            boolean supportedDevice = provInfoUtil.isSupportedDevice(userAgentDTO, xdmPttServerId, persisterTxn, existingSubsProfileDTO);

            knLogger.info(methodName, " isSupportedDevice ", supportedDevice);

            if (!supportedDevice) {
                responseDTO.setResponseMessage(KnProvConstants.UNSUPPORTED_DEVICE);
                responseDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.FAILURE.value());
                return responseDTO;
            }

            /*//Reject request for client if app name is not knwds
            if (existingSubsProfileDTO.getDispatchType() == KnConstants.DISPATCH_TYPE){
                if (!(userAgentDTO.getAppName().equals(KnProvConstants.KNWDS))){
                    throw new KnProvBOException(KnErrorCodes.BOEntity.INVALID_CLIENT, "Invalid client for dispatch subscriber app name not matching");
                }
            }*/
            if (newUserAgent != null) {
                if (existingSubsProfileDTO.getUserAgent() == null || (!existingSubsProfileDTO.getUserAgent().equals(newUserAgent))) {
                    knLogger.debug(methodName, "new user Agent :", newUserAgent);
                    knLogger.debug(methodName, "change in User Agent ");
                    subsProfilePersistDTO.setUserAgent(newUserAgent);
                    isProfileUpdated = true;
                }
            }
            subsProfilePersistDTO.setClientPVmajorVer(existingSubsProfileDTO.getClientPVmajorVer());
            subsProfilePersistDTO.setClientPVminorVer(existingSubsProfileDTO.getClientPVminorVer());
            if (userAgentDTO != null) {
                String protocolVersion = userAgentDTO.getProtocolVersion();
                knLogger.debug(methodName, "protocol version :", protocolVersion);
                if (protocolVersion != null) {
                    try {
                        String[] pv = protocolVersion.split("\\.");
                        knLogger.debug(methodName, " pv ", pv);
                        if (pv != null && pv[0] != null) {
                            int majorPV = Integer.parseInt(pv[0]);
                            knLogger.debug(methodName, " majorPV ", majorPV);
                            if (existingSubsProfileDTO.getClientPVmajorVer() != majorPV) {
                                knLogger.debug(methodName, "majorPV version :", majorPV);
                                knLogger.debug(methodName, "change in major PV version ");
                                subsProfilePersistDTO.setClientPVmajorVer(majorPV);
                                isProfileUpdated = true;
                            }
                        }

                        if (pv != null && pv[1] != null) {
                            int minorPV = Integer.parseInt(pv[1]);
                            knLogger.debug(methodName, " minorPV ", minorPV);
                            if (existingSubsProfileDTO.getClientPVminorVer() != minorPV) {
                                knLogger.debug(methodName, "minorPV version :", minorPV);
                                knLogger.debug(methodName, "change in minor PV version");
                                subsProfilePersistDTO.setClientPVminorVer(minorPV);
                                isProfileUpdated = true;
                            }
                        }

                    } catch (NumberFormatException nfe) {
                        knLogger.error(methodName, "Invalid PV version :", nfe);
                        knLogger.error(methodName, "setting PV as 1.0:");
                        subsProfilePersistDTO.setClientPVmajorVer(1);
                        subsProfilePersistDTO.setClientPVminorVer(0);
                    }
                } else {
                    knLogger.debug(methodName, "setting pv as 1.0 as PV is null or improper");
                    subsProfilePersistDTO.setClientPVmajorVer(1);
                    subsProfilePersistDTO.setClientPVminorVer(0);
                }
            } else {
                knLogger.debug(methodName, "setting Pv as 1.0 as PV is null or not proper");
                subsProfilePersistDTO.setClientPVmajorVer(1);
                subsProfilePersistDTO.setClientPVminorVer(0);
            }
            if (!isProfileUpdated) {
                knLogger.error(methodName, "No change in profile observed");
                throw new KnProvBOException(KnErrorCodes.BOEntity.NO_CHG_IN_PROFILE, "No change in profile observed");
            }

            String pocPttId = existingSubsProfileDTO.getPoCHome();
            String presencePttId = existingSubsProfileDTO.getPresenceHome();

            knLogger.debug(methodName, "Updated Subscriber Profile ", subsProfilePersistDTO);
            long lastProfileUpdateTime = Calendar.getInstance().getTimeInMillis();
            subsProfilePersistDTO.setLastProfileUpdateTime(lastProfileUpdateTime);
            xdmServerDAO.updateUserAgnet(subsProfilePersistDTO, persisterTxn);

            int corpId = existingSubsProfileDTO.getCorpId();
            long profileUpdateTime = Calendar.getInstance().getTimeInMillis();
            xdmServerDAO.updateCorpProfileLastUpdateTime(corpId, profileUpdateTime, persisterTxn);
            responseDTO.setResponseMessage(KnProvConstants.UPDATE_SUBS_PROFILE_SUCCESS);
            responseDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.SUCCESS.value());
            responseDTO.setEtag(lastProfileUpdateTime);
            //populating the Subs Config document change DTO
            KnOPDocChgDTO docChgDTO = new KnOPDocChgDTO();
            docChgDTO.setDocumentChgType(KnConstants.DOC_CHANGE_TYPE.REPLACE.value());
            String subsConfigDocUri = provInfoUtil.generateSubsConfigSelUri(mdn);
            docChgDTO.setDocUri(subsConfigDocUri);
            docChgDTO.setNewEtag(String.valueOf(lastProfileUpdateTime));

            Collection<KnOPDocChgDTO> docChgList = new ArrayList<KnOPDocChgDTO>();
            docChgList.add(docChgDTO);
            int previousEtag = commonXDMServerDAO.getCurrentEtagForDirDoc(mdn, persisterTxn);
            //populating Dir Document change DTO
            KnOPDirChgDTO dirChgDTO = new KnOPDirChgDTO();
            //  KnXDMSServiceConfigDTO xdmsServiceConfigDTO = genInfoUtil.retrieveXDMSServiceConfig(xdmPttServerId, persisterTxn);
            //dirChgDTO.setXcapRootURI(xdmsServiceConfigDTO.getXcapRootUri());
            dirChgDTO.setXcapRootURI(genInfoUtil.getXCAPRootURI(mdn, persisterTxn));
            dirChgDTO.setPocHome(pocPttId);
            dirChgDTO.setPresenceHome(presencePttId);
            dirChgDTO.setDocChgDTO(docChgList);
            String dirDocUri = genInfoUtil.generateDirDocUri(mdn);
            dirChgDTO.setDirUri(dirDocUri);
            dirChgDTO.setDirPrevEtag(String.valueOf(previousEtag));
            int newEtag = previousEtag + 1;
            dirChgDTO.setDirNewEtag(String.valueOf(newEtag));

            //setting the Document Change DTO the response
            responseDTO.setDirChgDTO(dirChgDTO);

        } catch (KnDBConnectionException ex) {
            knLogger.error(methodName, "DAO DBConnection Exception occured :", ex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);

        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occurred : ", ex);
            knLogger.error(methodName, ex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            if (com.kodiak.xdms.server.common.resources.KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_INFO_NOT_FOUND, "Subscriber does not exists");
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception occurred :", e);
            knLogger.error(methodName, e);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while Update Subscriber");
            knLogger.error(methodName, e);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred while update Subscriber", e);
        }
        return responseDTO;
    }


    /**
     * method to delete the Subscriber Profile
     *
     * @param subscriberDTO KnIPSubscriberInfoDTO
     * @param persisterTxn  KnPersisterTxn
     * @return KnOPProvDTO
     * @throws KnProvBOException BO Entity Exception
     */
    public KnOPDeleteSubsRespDTO deleteSubscriber(KnIPSubscriberInfoDTO subscriberDTO, KnPersisterTxn persisterTxn) throws KnProvBOException {
        String methodName = "deleteSubscriber(KnIPSubscriberInfoDTO, KnPersisterTxn)";
        boolean ownedTxn = false;
        KnSubsProfilePersistDTO subsProfilePersistDTO = new KnSubsProfilePersistDTO();
        KnOPDeleteSubsRespDTO responseDTO= new KnOPDeleteSubsRespDTO();
        knLogger.info(methodName, "ENTRY: delete Subscriber with DTO mdn and corpId- ", subscriberDTO.getMdn(), " ", subscriberDTO.getCorpId());
        Collection<Integer> successPegs = new ArrayList<Integer>();
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }
            try {
                xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
            } catch (KnBOException e) {
                knLogger.error(methodName, "failed to retrieve xdm Ptt Sever Id");
                knLogger.error(methodName, e);
                if (ownedTxn) {
                    rollback(persisterTxn);
                }
                throw new KnProvBOException(KnErrorCodes.BOEntity.XDMS_PTT_ID_NOT_FOUND, "Failed to retrieve XDM PTT Server ID", e);
            }

            String mdn = subscriberDTO.getMdn();
            KnOPSubsProfileInfoDTO existingProfileInfoDTO = provInfoUtil.retrieveSubscriberInfo(mdn, persisterTxn);
            knLogger.debug(methodName, "existingProfileInfoDTO :", existingProfileInfoDTO);

            int subsClientType = existingProfileInfoDTO.getSubsClientType();
            if (subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.DESKTOP.value()) {
                successPegs.add(KnOMConstants.XDM_NUM_DESKTOP_CLIENTS_DELETED);
            } else if (subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.DISPATCH.value()) {
                successPegs.add(KnOMConstants.XDM_NUM_DISPATCH_CLIENTS_DELETED);
            } else if (subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.POCDONORRADIO.value()) {
                successPegs.add(KnOMConstants.XDM_NUM_LMR_INTEROP_CLIENTS_DELETED);
            } else if (subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.THIRDPARTYPOCCLIENT.value()) {
                successPegs.add(KnOMConstants.XDM_NUM_3RD_PARTY_POC_CLIENTS_DELETED);
            } else if (subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.MOBILE_CLIENT.value()) {
                successPegs.add(KnOMConstants.XDM_NUM_MOBILE_API_CLIENTS_DELETED);
            } else if (subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.THIRDPARTYDISPATCHERCLIENT.value()) {
                successPegs.add(KnOMConstants.XDM_NUM_3RDPARTY_DISPATCHER_CLIENTS_DELETED);
            } else if (subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.POC_NNI_Alias_MDN.value()) {
                successPegs.add(KnOMConstants.XDM_NUM_NNI_ALIAS_SUBSCRIBERS_DELETED);
            } else if (subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.POC_NNI_Group_MDN.value()) {
                successPegs.add(KnOMConstants.XDM_NUM_NNI_GROUP_SUBSCRIBERS_DELETED);
            } else if (subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.CROSS_CARRIER_PTT_CLIENT.value()) {
                successPegs.add(KnOMConstants.XDM_NUM_CROSS_CARRIER_PTT_CLIENTS_DELETED);
            } else if (subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value()) {
                successPegs.add(KnOMConstants.XDM_NUM_PTTRADIO_CROSSCARRIER_CLIENTS_DELETED);
            } else if (subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value()) {
                successPegs.add(KnOMConstants.XDM_NUM_PTTRADIO_HANDSET_CLIENTS_DELETED);
            } else if (subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value()) {
                successPegs.add(KnOMConstants.XDM_NUM_PTTRADIO_WIFIONLY_CLIENTS_DELETED);
            } else if (subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.SGMDNPATCH.value()) {
                successPegs.add(KnOMConstants.XDM_NUM_SG_MDN_PATCH_CLIENTS_DELETED);
            } else if (subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.DATAGROUPMDN.value()) {
                successPegs.add(KnOMConstants.XDM_NUM_SG_MDN_PATCH_CLIENTS_DELETED);
            }
            subsProfilePersistDTO.setMdn(mdn);

            IXDMServerDAO commonXdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
            //getting the prev etag of directory
            int previousEtag = commonXdmServerDAO.getCurrentEtagForDirDoc(mdn, persisterTxn);

            commonXdmServerDAO.deleteXDMDirectoryForMdn(mdn, persisterTxn);
            knLogger.debug(methodName, "deleted the directory for Mdn - ", KnGDPRTemplate.mdn(mdn));

            //deleting the XDM Contact List
            commonXdmServerDAO.deleteContactListForMdn(mdn, persisterTxn);
            knLogger.debug(methodName, "deleted the Contact List for mdn - ", KnGDPRTemplate.mdn(mdn));

            //deleting the XDM Contact List Doc Map
            commonXdmServerDAO.deleteContactListDocMapForMdn(mdn, persisterTxn);
            knLogger.debug(methodName, "deleted the contact list doc map for mdn - ", KnGDPRTemplate.mdn(mdn));

            //deleting Corp resource List Index Doc
            int corpSubscriptionType = existingProfileInfoDTO.getCorporateSubscriptionType();
            if (corpSubscriptionType == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
                commonXdmServerDAO.deleteCorpResourceListIndexDoc(mdn, persisterTxn);
                knLogger.debug(methodName, "deleted the Corp resource List Index Doc");
            }

            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();

            //delete client vocoder ID from DG.ClientSuppVocoders
            provXDMServerDAO.deleteClientSuppVocoder(mdn, persisterTxn);
            String xcapRooturi = genInfoUtil.getXCAPRootURI(mdn, persisterTxn);
            //delete the subscriber roaming profile
            provXDMServerDAO.deleteSubscrRoamingProfile(mdn, persisterTxn);
            knLogger.debug(methodName, "Delete the Subscriber Roaming Profile");
            provXDMServerDAO.deleteSubApn(mdn, persisterTxn);
            //delete nni subscriber profile

            if (subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.POC_NNI_Alias_MDN.value() || subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.POC_NNI_Group_MDN.value()) {
                knLogger.debug(methodName, "Deleting NNI Subscriber profile");
                List<String> mdns = new ArrayList<>();
                mdns.add(mdn);
                provXDMServerDAO.deleteNNISubscrProfile(mdns, persisterTxn);
            }
            boolean bitEnabled = KnGeneralUtil.getFeatureBitValue(existingProfileInfoDTO.getSubsFS2(), FEATURE_SET.MCDEVICE.value());
            String deviceCreatedAs = commonXdmServerDAO.getDeviceInfo(mdn, persisterTxn);
            boolean impliciteDevice = KnProvConstants.IMPLICIT_DEVICE.equals(deviceCreatedAs);
            knLogger.debug(methodName, "values of deviceCreatedAs", deviceCreatedAs, " impliciteDevice:", impliciteDevice);
            if (MCSCOMPLIANCE == existingProfileInfoDTO.getMcpttCompliance()
                    || bitEnabled
                    || existingProfileInfoDTO.getLicenseType() == KnProvConstants.LICENSEN_TYPE_STANDARD
                    || impliciteDevice) {
                IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
                KnDeviceInfoPersistDTO deviceInfoPersistDTO = new KnDeviceInfoPersistDTO();
                deviceInfoPersistDTO.setDeviceId(mdn);
                xdmServerDAO.deleteDeviceImpiInfo(deviceInfoPersistDTO, persisterTxn);
                xdmServerDAO.deleteDeviceInfo(deviceInfoPersistDTO, persisterTxn);
                if (!impliciteDevice) {
                    responseDTO.setDeleteDeviceNotify(true);
                }
            }
            if (subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.STANDALONECAMERA.value()) {
                knLogger.debug(methodName, "Deleting subscriber camera info");
                IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
                xdmServerDAO.deleteSubscriberCameraInfo(mdn, persisterTxn);
            }

            if (subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.THIRDPARTYPOCCLIENT.value() || subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.MOBILE_CLIENT.value()
                    || subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.THIRDPARTYDISPATCHERCLIENT.value()) {
                knLogger.debug(methodName, "Deleting TP or Mobile Subscriber profile");
                List<String> mdns = new ArrayList<>();
                mdns.add(mdn);
                // deleting pal pool usage table entry
                provXDMServerDAO.deletePAMAccPoolUsage(mdns, persisterTxn);

                //delete TP user mdn map for this mdn
                KnTPUserPersistDTO tpUserPersistDTO = new KnTPUserPersistDTO();
                tpUserPersistDTO.setMdn(mdn);
                provXDMServerDAO.deleteTPUserMDNMap(tpUserPersistDTO, persisterTxn);
            }

            String extCorpId = null;
            //if the subscriber is a corp or corp-public type then we will retirve the extcorpid of the corp
            int corpIdFromSubscr = existingProfileInfoDTO.getCorpId();
            if (corpIdFromSubscr != 0) {
                knLogger.debug(methodName, "Subscriber is corp or corp-public type,Retrive the extCorpId for the corp=", corpIdFromSubscr);
                extCorpId = provXDMServerDAO.retrieveExtCorporationId(corpIdFromSubscr, persisterTxn);
            }
            knLogger.debug(methodName, "Ext Corp ID - ", extCorpId);

            //delete Subscriber MCPTT profile
            provXDMServerDAO.deleteMCPTTProfile(mdn, persisterTxn);
            knLogger.debug(methodName, "Delete Subscriber MCPTT Profile");

            //Delete subscriber Authorization Doc profile
            provXDMServerDAO.deleteAuthorizationDocProfile(mdn, persisterTxn);
            knLogger.debug(methodName, "Delete Subscriber Authorization Doc Profile");

            //Delete addon Packages
            knLogger.debug(methodName, "Deleted the subscriber addon pkgs");
            provXDMServerDAO.deleteSubAddlOnPkgs(mdn, persisterTxn);

            //Delete subscriber TGSS Doc  & SSgroupinfo profile

            knLogger.debug(methodName, "Delete TGSS SSChannel Info Doc Profile");

            provXDMServerDAO.deleteSSChannelGrpInfo(mdn, persisterTxn);

            knLogger.debug(methodName, "Delete Subscriber TGSS Doc Profile");
            provXDMServerDAO.deleteTGSSDoc(mdn, persisterTxn);

            knLogger.debug(methodName, "Delete Subscriber AliasId Info");
            provXDMServerDAO.deleteSubsAliasId(mdn, persisterTxn);
            //UCSPROVCONFIG-1083: if UPM async request and isHierarchy deleting from SubscrAddlInfo
            knLogger.info(methodName, "isUPM: ", subscriberDTO.isUpmCall(), " Hierarchy Type: ", subscriberDTO.getHierarchyType());
            if (subscriberDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY && subscriberDTO.isUpmCall()) {
                provXDMServerDAO.deleteSubsAddlInfo(mdn, persisterTxn);
            }
            //Get All profile MDNs for given Real MDN
            List<String> UserProfileMdns = new ArrayList<String>();
            //check if it is a Real mdn
            if (existingProfileInfoDTO.getUserProfileIndex() == null || existingProfileInfoDTO.getUserProfileIndex().equals(USER_PROFILE_INDEX)) {
                UserProfileMdns = getMdnForUPM(mdn, persisterTxn);
                UserProfileMdns.remove(mdn);
                if (!UserProfileMdns.isEmpty()) {
                    IProvXDMServerDAO xdmServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB)
                            .createProvXDMServerDAO();
                    subsProfilePersistDTO.setMdn(mdn);
                    subsProfilePersistDTO.setServiceAuthStatus(KnConstants.SERVICE_AUTH_STATUS.DEACTIVATED.value());
                    subsProfilePersistDTO.setServiceStatusOp(SERVICE_STATUS_OP.DEACTIVATED.value());
                    long lastProfileUpdateTime = Calendar.getInstance().getTimeInMillis();
                    subsProfilePersistDTO.setLastProfileUpdateTime(lastProfileUpdateTime);
                    subsProfilePersistDTO.setMdnList(UserProfileMdns);
                    xdmServerDAO.updateServiceAuthStatusForUPM(subsProfilePersistDTO, persisterTxn);
                    knLogger.debug(methodName, "updated service auth status successfully for ProfileMdns");
                }
            }
            //delete the subscriber profile
            provXDMServerDAO.deleteSubscriberProfile(mdn, persisterTxn);
            knLogger.debug(methodName, "Delete Subscriber Profile");
            if (corpIdFromSubscr != 0) {
                knLogger.debug(methodName, "Calling Update Etag for Corp", extCorpId);
                updateEtagForNNISubscr(extCorpId, persisterTxn);
            }

            //deleting Corp profile Info

            if (corpSubscriptionType == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
                int corpId = existingProfileInfoDTO.getCorpId();
                //deleting the corp Profile.
                // --> verify if the current MDN is the last member of the Corporation
                // --> if true then delete corp profile else continue.
                int corpSubsCount = provXDMServerDAO.retrieveCorpSubscriberWithProfileCnt(corpId, persisterTxn);
                int corpProfileCleanUp = provXDMServerDAO.retrieveCorpProfileCleanUp(corpId, persisterTxn);
                int deviceCount = genInfoUtil.getDeviceCountForCorpId(corpId, persisterTxn);
                knLogger.debug(methodName, "corp Subscriber count - ", corpSubsCount, " corpProfileCleanUp :", corpProfileCleanUp, "deviceCount", deviceCount);
                if (corpSubsCount == 0 && corpProfileCleanUp != 1 && deviceCount == 0) {
                    knLogger.info(methodName, "Deleting Corporate profile as corpProfileCleanUp = ", corpProfileCleanUp,
                            " and corpSubsCount = ", corpSubsCount);
                    //delete corp profile
                    provXDMServerDAO.deleteCorporateProfile(corpId, persisterTxn);
                    successPegs.add(KnOMConstants.XDM_NUM_CORP_PROFILE_DELETED);
                    knLogger.debug(methodName, "Deleted the corporate profile");
                } else {
                    knLogger.debug(methodName, "Updating the corp profile etag since mdn for corp is deleted"
                    );
                    long profileUpdateTime = Calendar.getInstance().getTimeInMillis();
                    provXDMServerDAO.updateCorpProfileLastUpdateTime(corpId, profileUpdateTime, persisterTxn);
                    knLogger.debug(methodName, "updated the corp profile etag for corp id - ", corpId
                    );
                }
            }
            //IDM user profile delete
            String idmFqdn = genInfoUtil.getIDMInternalFqdn(persisterTxn);
            boolean isOIDCApplicable = generalCacheUtil.isOIDCApplicable(existingProfileInfoDTO.getCorpId(), WEBDISPATCHER);
            String appId = existingProfileInfoDTO.getDispatchType() == DISPATCH_TYPE_WEB ? APP_ID.DISPATCHER.value() : APP_ID.HANDSET_STANDARD.value();
            if (KnConstants.MCSCOMPLIANCE == existingProfileInfoDTO.getMcpttCompliance()) {
                appId = APP_ID.USERMCSCLIENTS.value();
                KnManageSyncUserProfileUtil.getInstance().deleteIDMUserForOIDCMgmt(idmFqdn, appId, existingProfileInfoDTO.getAliasMdn() != null
                        ? existingProfileInfoDTO.getAliasMdn() : existingProfileInfoDTO.getMdn());
            } else if ((existingProfileInfoDTO.getSubsClientType() == DISPATCHER_CLIENT) && existingProfileInfoDTO.getUserId() != null) {
                KnManageSyncUserProfileUtil.getInstance().deleteIDMUserForOIDCMgmt(idmFqdn, appId, existingProfileInfoDTO.getUserId());
            } else if ((existingProfileInfoDTO.getClientPVmajorVer() >= PROTOCOL_VERSION_13 || (existingProfileInfoDTO.getClientPVmajorVer() == 0 && isOIDCApplicable))) {
                KnManageSyncUserProfileUtil.getInstance().deleteIDMUserForOIDCMgmt(idmFqdn, appId, existingProfileInfoDTO.getAliasMdn() != null
                        ? existingProfileInfoDTO.getAliasMdn() : existingProfileInfoDTO.getMdn());
            } else if ((existingProfileInfoDTO.getClientPVmajorVer() != 0 && existingProfileInfoDTO.getClientPVmajorVer() < PROTOCOL_VERSION_13)
                    || (existingProfileInfoDTO.getClientPVmajorVer() == 0 && !isOIDCApplicable)) {
                KnManageSyncUserProfileUtil.getInstance().deleteIDMUser(idmFqdn, existingProfileInfoDTO.getUserId());
            }

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }

            responseDTO.setMdn(existingProfileInfoDTO.getMdn());
            responseDTO.setPocServerHome(existingProfileInfoDTO.getPoCHome());
            responseDTO.setPresenceServerHome(existingProfileInfoDTO.getPresenceHome());

            responseDTO.setResponseMessage(KnProvConstants.DELETE_SUBSCRIBER_SUCCESS);
            responseDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.SUCCESS.value());
            KnOPDirChgDTO dirChgDTO = new KnOPDirChgDTO();
            /*KnXDMSServiceConfigDTO xdmsServiceConfigDTO = genInfoUtil.retrieveXDMSServiceConfig(xdmPttServerId, persisterTxn);
            dirChgDTO.setXcapRootURI(xdmsServiceConfigDTO.getXcapRootUri());
            */
            dirChgDTO.setXcapRootURI(xcapRooturi);
            dirChgDTO.setPocHome(existingProfileInfoDTO.getPoCHome());
            dirChgDTO.setPresenceHome(existingProfileInfoDTO.getPresenceHome());
            String dirDocUri = genInfoUtil.generateDirDocUri(mdn);
            dirChgDTO.setDirUri(dirDocUri);
            dirChgDTO.setDirPrevEtag(String.valueOf(previousEtag));
            dirChgDTO.setProtoVersion(String.valueOf(existingProfileInfoDTO.getClientPVmajorVer()));
            dirChgDTO.setClientType(existingProfileInfoDTO.getSubsClientType());
            responseDTO.setDirChgDTO(dirChgDTO);
            //setting the activeFS for the request.
            responseDTO.setActiveFS2(existingProfileInfoDTO.getActiveFS2());
            responseDTO.setCorpId(existingProfileInfoDTO.getCorpId());
            responseDTO.setPassword(existingProfileInfoDTO.getClientPassword());
            responseDTO.setUserProfileMdns(UserProfileMdns);
            responseDTO.setSuccessPegs(successPegs);
            knLogger.debug(methodName, "Response DTO - ", responseDTO);
            knLogger.info(methodName, "EXIT: delete Subscriber operation ");

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
            return responseDTO;
        } catch (KnDBConnectionException ex) {
            knLogger.error(methodName, "DAO DBConnection Exception occured :", ex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);

        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occurred : ", ex);
            knLogger.error(methodName, ex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            if (com.kodiak.xdms.server.common.resources.KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_INFO_NOT_FOUND, "Subscriber does not exists");
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception occurred :", e);
            knLogger.error(methodName, e);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while delete Subscriber");
            knLogger.error(methodName, e);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred while delete Subscriber", e);
        }
    }

    /**
     * method to retrieve the Subscriber Details
     *
     * @param subscriberDTO KnIPSubscriberInfoDTO
     * @param persisterTxn  KnPersisterTxn
     * @return KnOPSubsProfileInfoDTO Object
     * @throws KnProvBOException
     */
    public KnOPSubsProfileInfoDTO getSubscriberDetails(KnIPSubscriberInfoDTO subscriberDTO, KnPersisterTxn persisterTxn) throws KnProvBOException {
        String methodName = "getSubscriberDetails(KnIPSubscriberInfoDTO, KnPersisterTxn)";
        boolean ownedTxn = false;
        //KnSubsProfilePersistDTO subsProfilePersistDTO = new KnSubsProfilePersistDTO();
        knLogger.info(methodName, "ENTRY: getSubscriberDetails with DTO ", subscriberDTO);
        KnOPSubsProfileInfoDTO subsProfileRespDTO = new KnOPSubsProfileInfoDTO();
        Map<String,List<KnSubsAliasInfoDTO>> subsAliasIdInfoMap = null;
        boolean performPrivacyOperation = false;
        Integer privacyFlagToExecute = 0;
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            try {
                xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
            } catch (KnBOException e) {
                knLogger.error(methodName, "failed to retrieve xdm Ptt Sever Id");
                knLogger.error(methodName, e);
                if (ownedTxn) {
                    rollback(persisterTxn);
                }
                throw new KnProvBOException(KnErrorCodes.BOEntity.XDMS_PTT_ID_NOT_FOUND, "Failed to retrieve XDM PTT Server ID", e);
            }

            String mdn = null;
            // subsProfilePersistDTO.setMdn(mdn);

            //retrieving the Subscriber Info
            IProvXDMServerDAO provXdmServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            if(subscriberDTO!=null && subscriberDTO.getAliasInfoDTOList()!= null && subscriberDTO.getAliasInfoDTOList().get(0).getAliasId() != null && subscriberDTO.getAliasInfoDTOList().get(0).getAliasIdIssuer() != null){
            	String userId = null;
            	String aliasMdn = null;
            	knLogger.info(methodName, "Inside AliasID and issuer");
                IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
                Map<String,String> aliasIdIPMap = subscriberDTO.getAliasInfoDTOList().stream().collect(Collectors.toMap(KnXDMSubsAliasInfoDTO::getAliasId,KnXDMSubsAliasInfoDTO::getAliasIdIssuer));
                subsAliasIdInfoMap = provXDMServerDAO.selectSubsAliasIdInfo(aliasIdIPMap, true, persisterTxn);
                if(subsAliasIdInfoMap!=null && !subsAliasIdInfoMap.isEmpty()) {
                	knLogger.info(methodName, "Alais id values are there ", subsAliasIdInfoMap);
                	mdn = new ArrayList<>(subsAliasIdInfoMap.keySet()).get(0);
                	subscriberDTO.setMdn(mdn);
                }
                else  {
                knLogger.info(methodName, "Alais id values are not there ", subsAliasIdInfoMap);
                subsAliasIdInfoMap = null;
                KnXDMSubsAliasInfoDTO aliasInfo = subscriberDTO.getAliasInfoDTOList().get(0);
                try {
                      aliasMdn = provInfoUtil.retrieveSubscriberInfoByAliasMdn(aliasInfo.getAliasId(),persisterTxn).getMdn();
                }
                catch(Exception e) {
                	knLogger.info(methodName, "No record found for requested aliasid", aliasInfo.getAliasId());
                	 try {
                		 userId   = provInfoUtil.retrieveSubscriberInfoByUserId(aliasInfo.getAliasId(),persisterTxn).getMdn();
                	 }
                	 catch (Exception ex) {
                		 knLogger.info(methodName, "No record found for requested userId", aliasInfo.getAliasId());
					}
                }


                 if(aliasMdn!=null) {
                	 knLogger.info(methodName, "The aliasMdn is ", mdn);
                	 mdn = aliasMdn;
                	 subscriberDTO.setMdn(mdn);
                 }
                 else if(userId != null) {
                	 knLogger.info(methodName, "The userID is ", mdn);
                	 mdn = userId;
                	 subscriberDTO.setMdn(mdn);
                 }
                 else {
                	 throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_INFO_NOT_FOUND,
                             "Subscriber Profile info not found");
                 }
            }

            }
            if (subscriberDTO.getMdn() != null) {
                mdn = subscriberDTO.getMdn();
                if (!mdn.startsWith(TELURI)) {
                    subsProfileRespDTO = provInfoUtil.retrieveSubscriberInfo(mdn, persisterTxn);
                } else {
                    subsProfileRespDTO = provInfoUtil.retrieveSubscriberInfoByMcpttId(mdn, persisterTxn);
                    mdn = subsProfileRespDTO.getMdn();
                }
            }

            if (subscriberDTO.getAliasMdn() != null) {
                subsProfileRespDTO = provInfoUtil.retrieveSubscriberInfoByAliasMdn(subscriberDTO.getAliasMdn(),
                        persisterTxn);
                mdn = subsProfileRespDTO.getMdn();
            }

            if (subscriberDTO.getUserId() != null) {
                subsProfileRespDTO = provInfoUtil.retrieveSubscriberInfoByUserId(subscriberDTO.getUserId(),
                        persisterTxn);
                mdn = subsProfileRespDTO.getMdn();
            }

            List<String> mcsId = new ArrayList<String>();
            if (subscriberDTO.getMcId() != null) {
                mcsId.add(subscriberDTO.getMcId());
            } else if (subscriberDTO.getMcPttId() != null) {
                mcsId.add(subscriberDTO.getMcPttId());
            } else if (subscriberDTO.getMcVideoId() != null) {
                mcsId.add(subscriberDTO.getMcVideoId());
            } else if (subscriberDTO.getMcDataId() != null) {
                mcsId.add(subscriberDTO.getMcDataId());
            }
            if (!mcsId.isEmpty()) {
                List<KnOPSubsProfileInfoDTO> selectSubscriberProfileByMCSIds = provInfoUtil
                        .selectSubscriberProfileByMCSIds(mcsId, persisterTxn);
                if (selectSubscriberProfileByMCSIds.isEmpty()) {
                    knLogger.error(methodName, "Subscriber Profile info not found");
                    throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_INFO_NOT_FOUND,
                            "Subscriber Profile info not found");
                } else {
                    subsProfileRespDTO = selectSubscriberProfileByMCSIds.get(0);
                    mdn = subsProfileRespDTO.getMdn();
                }
            }
            String pocPttServerId = subsProfileRespDTO.getPoCHome();
            subsProfileRespDTO.setPocPttServerId(pocPttServerId);
            String pocServerName = provInfoUtil.getSignalingCardInfo(pocPttServerId, persisterTxn);
            subsProfileRespDTO.setPoCHome(pocServerName);

            String presencePttServerId = subsProfileRespDTO.getPresenceHome();
            subsProfileRespDTO.setPresPttServerId(presencePttServerId);
            String presenceServerName = provInfoUtil.getSignalingCardInfo(presencePttServerId, persisterTxn);
            subsProfileRespDTO.setPresenceHome(presenceServerName);

            String xdmServerName = provInfoUtil.getSignalingCardInfo(xdmPttServerId, persisterTxn);
            subsProfileRespDTO.setXDMSHome(xdmServerName);


            long etag = subscriberDTO.getIfNoneMatch();
            // Get current etag
            long currentEtag = subsProfileRespDTO.getLastProfileUpdateTime();
            if (etag > 0 && currentEtag == etag) {
                knLogger.error(methodName, "Etag matches while get subscriber : ", etag);
                throw new KnProvBOException(KnErrorCodes.BOEntity.NO_CHG_IN_ETAG, "Etag matches");
            }

            ArrayList<Integer> roamingTypes = new ArrayList<Integer>();

            //retrieve subscriber client type
            String subClientType = String.valueOf(subsProfileRespDTO.getSubsClientType());
            int clientype = subsProfileRespDTO.getSubsClientType();
            knLogger.debug(methodName, "Client type - ", clientype);

            //retrieve TP userMdnMap
            IXDMServerDAO commonXDMServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
            if ((KnProvConstants.SUBS_CLIENT_TYPE.THIRDPARTYPOCCLIENT.value() == clientype) ||
                    (KnProvConstants.SUBS_CLIENT_TYPE.MOBILE_CLIENT.value() == clientype) ||
                    (KnProvConstants.SUBS_CLIENT_TYPE.THIRDPARTYDISPATCHERCLIENT.value() == clientype)) {


                List<String> tpMdnList = new ArrayList<>();
                tpMdnList.add(mdn);
                Map<String, KnTPUserAccountDTO> tpMdnMap = commonXDMServerDAO.retrieveTPUserAccountForMDNs(tpMdnList, persisterTxn);
                knLogger.debug(methodName, "MDN details-", KnGDPRTemplate.mdnList(tpMdnMap.keySet()));

                KnTPUserAccountDTO tpUserAccountDTO = tpMdnMap.get(mdn);
                if (tpUserAccountDTO != null) {
                    subsProfileRespDTO.setTpUser(tpUserAccountDTO.getTpUser());
                    subsProfileRespDTO.setTpAccount(tpUserAccountDTO.getTpAccount());
                }
            }

            KnSubsCameraInfoDAO cameraInfoDAO=KnProvTablesRegistry.getProvXDMTablesRegistry().createCameraInfoDAO(xdmPttServerId);
            if(KnProvConstants.SUBS_CLIENT_TYPE.STANDALONECAMERA.value()==clientype)
            {
                knLogger.debug(methodName, " ClientType - ", clientype ," CameraInfo - ",subsProfileRespDTO.getCameraType());
                Integer camerType = subsProfileRespDTO.getCameraType();

                KnOPSubsProfileInfoDTO   subsProfileInfDTO=  cameraInfoDAO.getSubscriberCameraInfo(subsProfileRespDTO.getMdn(),persisterTxn);
                if(subsProfileInfDTO!=null && subsProfileInfDTO.getCameraInfo()!=null)
                {
                    KnSubsCameraInfo cameraInfo=new KnSubsCameraInfo();
                    cameraInfo.setCameraSerialId(subsProfileInfDTO.getCameraInfo().getCameraSerialId());
                    cameraInfo.setIpIdentifier(subsProfileInfDTO.getCameraInfo().getIpIdentifier());
                    knLogger.debug(methodName," CameraSerialId - ",cameraInfo.getCameraSerialId()," IpIdentifier - ",cameraInfo.getIpIdentifier());
                    subsProfileRespDTO.setCameraInfo(cameraInfo);
                }
            }

            //retrieve raoming NA client type from cache/DB
            Map<String, String> paramNameValueMap = genInfoUtil.retrieveRTXConfigValues(persisterTxn);
            if (null == paramNameValueMap.get(KnConstants.ROAM_NA_CLIENT_TYPES)) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.SERVER_CONFIGURATION_FAILURE, "ROAM_NA_CLIENT_TYPE parameter not found");
            }
            String paramValue = (String) paramNameValueMap.get(KnConstants.ROAM_NA_CLIENT_TYPES);
            List<String> roamNAClientTypes = new ArrayList<>();
            if (null != paramValue) {
                roamNAClientTypes = Arrays.asList(paramValue.split(","));
            }

            Integer roamingAllowed = null;
            if (roamNAClientTypes.contains(subClientType)) {
                knLogger.debug(methodName, "MDN client type present in ROAMING NOT APPLICABLE CLIENT TYPE ", subClientType);
                roamingAllowed = -1;
                roamingTypes.add(roamingAllowed);
            } else {
                //retrieving the Subscriber Roaming Profile
                List<Integer> roamingProfileIds = provInfoUtil.retrieveSubscrRoamingProfile(mdn, persisterTxn);
                Map<Integer, String> supportedRoamingInfo = provInfoUtil.retrieveSupportedRoamingList();

                for (int roamingId : roamingProfileIds) {
                    if (supportedRoamingInfo.get(roamingId) != null) {
                        roamingTypes.add(Integer.parseInt(supportedRoamingInfo.get(roamingId)));
                    }
                }
                roamingAllowed = provInfoUtil.getRoamingType(roamingProfileIds, persisterTxn);
            }

            subsProfileRespDTO.setRoamingTypes(roamingTypes);
            subsProfileRespDTO.setRoamingAllowed(roamingAllowed);

            //retrieving the Ext CorpId.
            String extCorpId = null;
            if (subsProfileRespDTO.getCorporateSubscriptionType() == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
                extCorpId = provXdmServerDAO.retrieveExtCorporationId(subsProfileRespDTO.getCorpId(), persisterTxn);
                KnOPCorpProfileInfoDTO corpProfileInfoDTO = provXdmServerDAO.retrieveCorporateProfile(extCorpId, true, persisterTxn);
                subsProfileRespDTO.setCorporateName(corpProfileInfoDTO.getCorporateName());
                subsProfileRespDTO.setExtCorpId(extCorpId);
                subsProfileRespDTO.setAccountId(extCorpId);
                if (corpProfileInfoDTO.getPairedContactListId() == 0) {
                    subsProfileRespDTO.setPairingInd(false);
                } else {
                    subsProfileRespDTO.setPairingInd(true);
                }

            }

            //retrive Tier pkg and addon pkg details
            Map<String, Map<String, Integer>> pkgIdMap = new HashMap<>();
            Map<String, Integer> pkgId = new HashMap<>();
            KnOPSubsAddlInfoProfileDTO subsAddlInfoProfile = provXdmServerDAO.retrieveSubscrAddlInfo(mdn, persisterTxn);
            if (subsAddlInfoProfile != null && subsAddlInfoProfile.getTierPkgCode() != null) {
                pkgId.put(subsAddlInfoProfile.getTierPkgCode(), KnConstants.TIER_PKG_TYPE);
            }

            List<String> subsAddonPkgs = provXdmServerDAO.selectSubAddOnPkgs(mdn, persisterTxn);
            if (subsAddonPkgs != null) {
                for (String pkgCode : subsAddonPkgs) {
                    pkgId.put(pkgCode, KnConstants.ADDON_PKG_TYPE);
                }
            }
            pkgIdMap.put(KnConstants.ADD_ACTION, pkgId);
            subsProfileRespDTO.setPkgIdMap(pkgIdMap);

            //Privacy Opt Status starts
////
            KnOPSubsProfileInfoDTO existingProfileInfoDTO = null;
            Integer privacyAmbDiscListenFromXDMS_SVC_CONFIG = null;

            xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
            knLogger.debug(methodName, "xdmPttServerId::" + xdmPttServerId);
            // retrieving the Subscriber Info
            provXdmServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB)
                    .createProvXDMServerDAO();

            // we have mdn here already from request
            // existingProfileInfoDTO = provInfoUtil.retrieveSubscriberInfoUU(mdn, persisterTxn);
            mdn = subsProfileRespDTO.getMdn();
            knLogger.debug(methodName, "--->mdn::" + KnGDPRTemplate.mdn(mdn));

            existingProfileInfoDTO = provInfoUtil.retrieveSubscriberInfo(mdn, persisterTxn);
            knLogger.info(methodName, "--->existingProfileInfoDTO :", existingProfileInfoDTO);

            knLogger.debug(methodName, "ENTERS VALIDATION_1: ");

            // boolean privacyAmbDiscListenFlag = false;
            Integer privacyAmbDiscListen;
            //if the subscriber is a corp or corp-public type then we will retirve the extcorpid of the corp
            int corpIdFromSubscr = existingProfileInfoDTO.getCorpId();
            knLogger.debug(methodName, "--->corpIdFromSubscr::" + corpIdFromSubscr);
            if (corpIdFromSubscr != 0) {
                knLogger.debug(methodName, "--->Subscriber is corp or corp-public type,Retrive the extCorpId for the corp=", corpIdFromSubscr);
                //try {
                privacyAmbDiscListen = provXdmServerDAO.retrievePrivacyAmbDiscListenFlag(corpIdFromSubscr, persisterTxn);
                knLogger.debug(methodName, "--->privacyAmbDiscListen::" + privacyAmbDiscListen);
                if (privacyAmbDiscListen == null) {
                    //try {
                    KnXDMSServiceConfigDTO xdmsServiceConfigsDTO = genInfoUtil.retrieveXDMSServiceConfig(xdmPttServerId, persisterTxn);
                    knLogger.debug(methodName, "xdmsServiceConfigsDTO::" + xdmsServiceConfigsDTO + " xdmPttServerId::" + xdmPttServerId);

                    privacyAmbDiscListenFromXDMS_SVC_CONFIG = xdmsServiceConfigsDTO.getPrivacyAmbDiscListen();
                    knLogger.debug(methodName, "privacyAmbDiscListenFromXDMS_SVC_CONFIG::" + privacyAmbDiscListenFromXDMS_SVC_CONFIG);
                    privacyFlagToExecute = privacyAmbDiscListenFromXDMS_SVC_CONFIG;

                   /* if(privacyAmbDiscListenFromXDMS_SVC_CONFIG!=null && privacyAmbDiscListenFromXDMS_SVC_CONFIG==1)
                    {
                        /////////////////PERFORM THE REQUIRED OPERATION HERE
                        performPrivacyOperation=true;
                    } */

                } else {
                    //PERFORM THE REQUIRED OPERATION
                    //performPrivacyOperation=true;
                    privacyFlagToExecute = privacyAmbDiscListen;
                    knLogger.debug(methodName, "---> privacyFlagToExecute - ", privacyFlagToExecute + "--->performPrivacyOperation:" + performPrivacyOperation);

                }


            }
            knLogger.debug(methodName, "--->performPrivacyOperation:" + performPrivacyOperation);
            ////

            subsProfileRespDTO.setPerformPrivacyOperation(performPrivacyOperation);
            subsProfileRespDTO.setPrivacyValueToExecute(privacyFlagToExecute);
            subsProfileRespDTO.setOnBoardingEmailReqd(subsAddlInfoProfile != null ? subsAddlInfoProfile.getOnBoardingMailReqd() : 0);

            if (subsProfileRespDTO.getUserProfileIndex() != 0) {
                String profileName = null;
                profileName = commonXDMServerDAO.selectUserProfileName(mdn, com.kodiak.common.resources.KnConstants.FALSE, persisterTxn);
                if (profileName == null){
                    try {
                        profileName = provInfoUtil.getUserProfileName(subsProfileRespDTO.getCorpId(),
                                subsProfileRespDTO.getUserProfileIndex());
                        commonXDMServerDAO.updateUserProfileName(profileName,mdn,persisterTxn);
                    } catch (KnProvBOException ex) {
                        knLogger.error(methodName, "CBS connection error" + ex);
                        if (ownedTxn) {
                            rollback(persisterTxn);
                        }
                        throw new KnProvBOException(ex.getErrorCode(), ex.getMessage());
                    } }
                subsProfileRespDTO.setUserProfileName(profileName);
			}

            String deviceId = commonXDMServerDAO.getDeviceId(mdn, persisterTxn);
            if(deviceId != null)
            subsProfileRespDTO.setDeviceId(deviceId);

            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            if(subsAliasIdInfoMap==null){
                subsAliasIdInfoMap = provXDMServerDAO.selectSubsAliasIdInfoByMdn(Arrays.asList(mdn), persisterTxn);
            }

			/* If we are doing getSubscriberDetails using aliasId and alaisInfo the in response also we need to add */

            if (subsAliasIdInfoMap.get(mdn) != null) {
            	List<KnSubsAliasInfoDTO> alaisInfo = subsAliasIdInfoMap.get(mdn);
            	List<KnXDMSubsAliasInfoDTO> listaliasInfoDTO = new ArrayList<KnXDMSubsAliasInfoDTO>();
            	for(KnSubsAliasInfoDTO alias : alaisInfo) {
            		KnXDMSubsAliasInfoDTO aliasInfoDTO = new KnXDMSubsAliasInfoDTO(alias.getAliasId(),alias.getAliasIdIssuer(),alias.getAliasIdType());
            		listaliasInfoDTO.add(aliasInfoDTO);
            	}
            	subsProfileRespDTO.setAliasInfoDTOList(listaliasInfoDTO);
            }

            if (subsAddlInfoProfile.getPttSettingDocId() != null)
            {
                subsProfileRespDTO.setPttSettingDocId(subsAddlInfoProfile.getPttSettingDocId());
            }
            //Privacy Opt Status ends
            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
        } catch (KnDBConnectionException ex) {
            knLogger.error(methodName, "DAO DBConnection Exception occured :" + ex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);

        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occurred : " + ex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            if (com.kodiak.xdms.server.common.resources.KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_INFO_NOT_FOUND, "Subscriber doesn't exists");
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception occurred :" + e.getErrorCode());
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while get Subscriber details" + e);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred while get Subscriber details", e);
        }
        knLogger.info(methodName, "EXIT:  getSubscriberDetails - ", subsProfileRespDTO);

        return subsProfileRespDTO;
    }

    /**
     * method to retrieve the Subscriber Details
     *
     * @param subscriberDTO KnIPSubscriberInfoDTO
     * @param persisterTxn  KnPersisterTxn
     * @return KnOPSubsProfileInfoDTO Object
     * @throws KnProvBOException
     */
    public KnOPSubsProfileInfoDTO getSubscriberDetailsBasic(KnIPSubscriberInfoDTO subscriberDTO, KnPersisterTxn persisterTxn) throws KnProvBOException {
        String methodName = "getSubscriberDetailsBasic(KnIPSubscriberInfoDTO, KnPersisterTxn)";
        boolean ownedTxn = false;
        knLogger.info(methodName, "ENTRY: get basic SubscriberDetails with DTO ", subscriberDTO);
        KnOPSubsProfileInfoDTO subsProfileRespDTO = new KnOPSubsProfileInfoDTO();
        Map<String,List<KnSubsAliasInfoDTO>> subsAliasIdInfoMap = null;
        boolean performPrivacyOperation = false;
        Integer privacyFlagToExecute = 0;

        try {

            try {
                xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId(); // DEL - GET SUBSD - GET1 - PTT ID - No DB calls.
            } catch (KnBOException e) {
                knLogger.error(methodName, "failed to retrieve xdm Ptt Sever Id");
                knLogger.error(methodName, e);

                throw new KnProvBOException(KnErrorCodes.BOEntity.XDMS_PTT_ID_NOT_FOUND, "Failed to retrieve XDM PTT Server ID", e);
            }

            String mdn = null;

            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }
            //retrieving the Subscriber Info
            if (subscriberDTO.getMdn() != null) {
                mdn = subscriberDTO.getMdn();
                if (!mdn.startsWith(TELURI)) {
                    subsProfileRespDTO = provInfoUtil.retrieveSubscriberInfo(mdn, persisterTxn); // DEL - GET SUBSD - GET3.1 - persisterTxn null check is performed
                } else {
                    subsProfileRespDTO = provInfoUtil.retrieveSubscriberInfoByMcpttId(mdn, persisterTxn); // DEL - GET SUBSD - GET3.2 - persisterTxn null check is NOT performed
                    mdn = subsProfileRespDTO.getMdn();
                }
            }

            if(subscriberDTO.getMdnList() != null){
                List<String> mdnList = subscriberDTO.getMdnList();
                subsProfileRespDTO = provInfoUtil.selectSubsProfileInfo(mdnList,true,persisterTxn);
            }

            List<String> mcsId = new ArrayList<String>();
            if (subscriberDTO.getMcId() != null) {
                mcsId.add(subscriberDTO.getMcId());
            } else if (subscriberDTO.getMcPttId() != null) {
                mcsId.add(subscriberDTO.getMcPttId());
            } else if (subscriberDTO.getMcVideoId() != null) {
                mcsId.add(subscriberDTO.getMcVideoId());
            } else if (subscriberDTO.getMcDataId() != null) {
                mcsId.add(subscriberDTO.getMcDataId());
            }

            if (!mcsId.isEmpty()) {
                List<KnOPSubsProfileInfoDTO> selectSubscriberProfileByMCSIds = provInfoUtil
                        .selectSubscriberProfileByMCSIds(mcsId, persisterTxn); // DEL - GET SUBSD - GET4 - persisterTxn null check is not performed.
                if (selectSubscriberProfileByMCSIds.isEmpty()) {
                    knLogger.error(methodName, "Subscriber Profile info not found");
                    throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_INFO_NOT_FOUND,
                            "Subscriber Profile info not found");
                } else {
                    subsProfileRespDTO = selectSubscriberProfileByMCSIds.get(0);
                    mdn = subsProfileRespDTO.getMdn();
                }
            }

            // DEL - GET SUBSD - TODO Need a check the below calls.
            String pocPttServerId = subsProfileRespDTO.getPoCHome();
            subsProfileRespDTO.setPocPttServerId(pocPttServerId);
            String pocServerName = provInfoUtil.getSignalingCardInfo(pocPttServerId, persisterTxn); // DEL - GET SUBSD - GET5 - null check is performed
            subsProfileRespDTO.setPoCHome(pocServerName);

            String presencePttServerId = subsProfileRespDTO.getPresenceHome();
            subsProfileRespDTO.setPresPttServerId(presencePttServerId);
            String presenceServerName = provInfoUtil.getSignalingCardInfo(presencePttServerId, persisterTxn); // DEL - GET SUBSD - GET6 call goed to GET5 // Check the call!
            subsProfileRespDTO.setPresenceHome(presenceServerName);

            String xdmServerName = provInfoUtil.getSignalingCardInfo(xdmPttServerId, persisterTxn); // DEL - GET SUBSD - GET7
            subsProfileRespDTO.setXDMSHome(xdmServerName);

            long etag = subscriberDTO.getIfNoneMatch();
            // Get current etag
            long currentEtag = subsProfileRespDTO.getLastProfileUpdateTime(); // DEL - GET SUBSD - GET8 TODO Need a check here..
            if (etag > 0 && currentEtag == etag) {
                knLogger.error(methodName, "Etag matches while get subscriber : ", etag);
                throw new KnProvBOException(KnErrorCodes.BOEntity.NO_CHG_IN_ETAG, "Etag matches");
            }

            ArrayList<Integer> roamingTypes = new ArrayList<Integer>();

            //retrieve subscriber client type
            String subClientType = String.valueOf(subsProfileRespDTO.getSubsClientType());
            int clientype = subsProfileRespDTO.getSubsClientType();
            knLogger.debug(methodName, "Client type - ", clientype);

            //retrieve TP userMdnMap
            IXDMServerDAO commonXDMServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
            if ((KnProvConstants.SUBS_CLIENT_TYPE.THIRDPARTYPOCCLIENT.value() == clientype) ||
                    (KnProvConstants.SUBS_CLIENT_TYPE.MOBILE_CLIENT.value() == clientype) ||
                    (KnProvConstants.SUBS_CLIENT_TYPE.THIRDPARTYDISPATCHERCLIENT.value() == clientype)) {


                List<String> tpMdnList = new ArrayList<>();
                tpMdnList.add(mdn);
                Map<String, KnTPUserAccountDTO> tpMdnMap = commonXDMServerDAO.retrieveTPUserAccountForMDNs(tpMdnList, persisterTxn); // DEL - GET SUBSD - GET9 - No null check for persisterTxn
                knLogger.debug(methodName, "MDN details-", KnGDPRTemplate.mdnList(tpMdnMap.keySet()));

                KnTPUserAccountDTO tpUserAccountDTO = tpMdnMap.get(mdn);
                if (tpUserAccountDTO != null) {
                    subsProfileRespDTO.setTpUser(tpUserAccountDTO.getTpUser());
                    subsProfileRespDTO.setTpAccount(tpUserAccountDTO.getTpAccount());
                }
            }

            KnSubsCameraInfoDAO cameraInfoDAO=KnProvTablesRegistry.getProvXDMTablesRegistry().createCameraInfoDAO(xdmPttServerId);
            if(KnProvConstants.SUBS_CLIENT_TYPE.STANDALONECAMERA.value()==clientype)
            {
                knLogger.debug(methodName, " ClientType - ", clientype ," CameraInfo - ",subsProfileRespDTO.getCameraType());
                Integer camerType = subsProfileRespDTO.getCameraType();

                KnOPSubsProfileInfoDTO   subsProfileInfDTO=  cameraInfoDAO.getSubscriberCameraInfo(subsProfileRespDTO.getMdn(),persisterTxn);
                if(subsProfileInfDTO!=null && subsProfileInfDTO.getCameraInfo()!=null)
                {
                    KnSubsCameraInfo cameraInfo=new KnSubsCameraInfo();
                    cameraInfo.setCameraSerialId(subsProfileInfDTO.getCameraInfo().getCameraSerialId());
                    cameraInfo.setIpIdentifier(subsProfileInfDTO.getCameraInfo().getIpIdentifier());
                    knLogger.debug(methodName," CameraSerialId - ",cameraInfo.getCameraSerialId()," IpIdentifier - ",cameraInfo.getIpIdentifier());
                    subsProfileRespDTO.setCameraInfo(cameraInfo);
                }
            }

            IProvXDMServerDAO provXdmServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();

            //retrieve raoming NA client type from cache/DB
            /*Map<String, String> paramNameValueMap = genInfoUtil.retrieveRTXConfigValues(persisterTxn);
            if (null == paramNameValueMap.get(KnConstants.ROAM_NA_CLIENT_TYPES)) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.SERVER_CONFIGURATION_FAILURE, "ROAM_NA_CLIENT_TYPE parameter not found");
            }
            String paramValue = (String) paramNameValueMap.get(KnConstants.ROAM_NA_CLIENT_TYPES);
            List<String> roamNAClientTypes = new ArrayList<>();
            if (null != paramValue) {
                roamNAClientTypes = Arrays.asList(paramValue.split(","));
            }*/

            //Integer roamingAllowed = null;
            /*if (roamNAClientTypes.contains(subClientType)) {
                knLogger.debug(methodName, "MDN client type present in ROAMING NOT APPLICABLE CLIENT TYPE ", subClientType);
                roamingAllowed = -1;
                roamingTypes.add(roamingAllowed);
            } else {
                //retrieving the Subscriber Roaming Profile
                List<Integer> roamingProfileIds = provInfoUtil.retrieveSubscrRoamingProfile(mdn, persisterTxn); // DEL - GET SUBSD - GET10 - Null check is performed. Roaming Profile
                Map<Integer, String> supportedRoamingInfo = provInfoUtil.retrieveSupportedRoamingList();

                for (int roamingId : roamingProfileIds) {
                    if (supportedRoamingInfo.get(roamingId) != null) {
                        roamingTypes.add(Integer.parseInt(supportedRoamingInfo.get(roamingId)));
                    }
                }
                roamingAllowed = provInfoUtil.getRoamingType(roamingProfileIds, persisterTxn); // DEL - GET SUBSD - GET11 - Null check is not performed.. Roaming Type
            }*/

            //subsProfileRespDTO.setRoamingTypes(roamingTypes);
            //subsProfileRespDTO.setRoamingAllowed(roamingAllowed);

            //retrieving the Ext CorpId.
            //String extCorpId = null;
            /*if (subsProfileRespDTO.getCorporateSubscriptionType() == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
                extCorpId = provXdmServerDAO.retrieveExtCorporationId(subsProfileRespDTO.getCorpId(), persisterTxn); // DEL - GET SUBSD - GET12.1 - Null check is performed
                KnOPCorpProfileInfoDTO corpProfileInfoDTO = provXdmServerDAO.retrieveCorporateProfile(extCorpId, true, persisterTxn); // DEL - GET SUBSD - GET12.2 - Null check is performed
                subsProfileRespDTO.setCorporateName(corpProfileInfoDTO.getCorporateName());
                subsProfileRespDTO.setExtCorpId(extCorpId);
                subsProfileRespDTO.setAccountId(extCorpId);
                if (corpProfileInfoDTO.getPairedContactListId() == 0) {
                    subsProfileRespDTO.setPairingInd(false);
                } else {
                    subsProfileRespDTO.setPairingInd(true);
                }

            }*/

            //retrive Tier pkg and addon pkg details
            Map<String, Map<String, Integer>> pkgIdMap = new HashMap<>();
            Map<String, Integer> pkgId = new HashMap<>();
            KnOPSubsAddlInfoProfileDTO subsAddlInfoProfile = provXdmServerDAO.retrieveSubscrAddlInfo(mdn, persisterTxn); // DEL - GET SUBSD - GET13 - Additional Info TODO Need to check!
            if (subsAddlInfoProfile != null && subsAddlInfoProfile.getTierPkgCode() != null) {
                pkgId.put(subsAddlInfoProfile.getTierPkgCode(), KnConstants.TIER_PKG_TYPE);
            }

            /*List<String> subsAddonPkgs = provXdmServerDAO.selectSubAddOnPkgs(mdn, persisterTxn); // DEL - GET SUBSD - GET14
            if (subsAddonPkgs != null) {
                for (String pkgCode : subsAddonPkgs) {
                    pkgId.put(pkgCode, KnConstants.ADDON_PKG_TYPE);
                }
            }
            pkgIdMap.put(KnConstants.ADD_ACTION, pkgId);
            subsProfileRespDTO.setPkgIdMap(pkgIdMap);*/

            //Privacy Opt Status starts
            //KnOPSubsProfileInfoDTO existingProfileInfoDTO = null;
            //Integer privacyAmbDiscListenFromXDMS_SVC_CONFIG = null;

            //xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
            //knLogger.debug(methodName, "xdmPttServerId::" + xdmPttServerId);
            // retrieving the Subscriber Info
            //provXdmServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();

            // we have mdn here already from request
            // existingProfileInfoDTO = provInfoUtil.retrieveSubscriberInfoUU(mdn, persisterTxn);
            //mdn = subsProfileRespDTO.getMdn();
            //knLogger.debug(methodName, "--->mdn::" + KnGDPRTemplate.mdn(mdn));

            //existingProfileInfoDTO = provInfoUtil.retrieveSubscriberInfo(mdn, persisterTxn); // DEL - GET SUBSD - GET15 - TODO Looks like a duplicate cal, Chk and delete
            //knLogger.info(methodName, "--->existingProfileInfoDTO :", existingProfileInfoDTO);

            //knLogger.debug(methodName, "ENTERS VALIDATION_1: ");

            // boolean privacyAmbDiscListenFlag = false;
            //Integer privacyAmbDiscListen;
            //if the subscriber is a corp or corp-public type then we will retirve the extcorpid of the corp
            //int corpIdFromSubscr = existingProfileInfoDTO.getCorpId();
            //knLogger.debug(methodName, "--->corpIdFromSubscr::" + corpIdFromSubscr);

            /*if (corpIdFromSubscr != 0) {
                knLogger.debug(methodName, "--->Subscriber is corp or corp-public type,Retrive the extCorpId for the corp=", corpIdFromSubscr);
                //try {
                privacyAmbDiscListen = provXdmServerDAO.retrievePrivacyAmbDiscListenFlag(corpIdFromSubscr, persisterTxn); // DEL - GET SUBSD - GET16
                knLogger.debug(methodName, "--->privacyAmbDiscListen::" + privacyAmbDiscListen);
                *//*if (privacyAmbDiscListen == null) {
                    //try {
                    KnXDMSServiceConfigDTO xdmsServiceConfigsDTO = genInfoUtil.retrieveXDMSServiceConfig(xdmPttServerId, persisterTxn); // DEL - GET SUBSD - GET17
                    knLogger.debug(methodName, "xdmsServiceConfigsDTO::" + xdmsServiceConfigsDTO + " xdmPttServerId::" + xdmPttServerId);

                    privacyAmbDiscListenFromXDMS_SVC_CONFIG = xdmsServiceConfigsDTO.getPrivacyAmbDiscListen();
                    knLogger.debug(methodName, "privacyAmbDiscListenFromXDMS_SVC_CONFIG::" + privacyAmbDiscListenFromXDMS_SVC_CONFIG);
                    privacyFlagToExecute = privacyAmbDiscListenFromXDMS_SVC_CONFIG;

                   *//**//* if(privacyAmbDiscListenFromXDMS_SVC_CONFIG!=null && privacyAmbDiscListenFromXDMS_SVC_CONFIG==1)
                    {
                        /////////////////PERFORM THE REQUIRED OPERATION HERE
                        performPrivacyOperation=true;
                    } *//**//*

                } else {
                    //PERFORM THE REQUIRED OPERATION
                    //performPrivacyOperation=true;
                    privacyFlagToExecute = privacyAmbDiscListen;
                    knLogger.debug(methodName, "---> privacyFlagToExecute - ", privacyFlagToExecute + "--->performPrivacyOperation:" + performPrivacyOperation);

                }*//*
            }*/
            // knLogger.debug(methodName, "--->performPrivacyOperation:" + performPrivacyOperation);

            subsProfileRespDTO.setPerformPrivacyOperation(performPrivacyOperation);
            subsProfileRespDTO.setPrivacyValueToExecute(privacyFlagToExecute);
            subsProfileRespDTO.setOnBoardingEmailReqd(subsAddlInfoProfile != null ? subsAddlInfoProfile.getOnBoardingMailReqd() : 0);

            /*if (subsProfileRespDTO.getUserProfileIndex() != 0) {
                String profileName = null;
                profileName = commonXDMServerDAO.selectUserProfileName(mdn, com.kodiak.common.resources.KnConstants.FALSE, persisterTxn); // DEL - GET SUBSD - GET18 - No null check performed. Check why Readonly is false
                if (profileName == null){
                    try {
                        profileName = provInfoUtil.getUserProfileName(subsProfileRespDTO.getCorpId(),
                                subsProfileRespDTO.getUserProfileIndex());
                        commonXDMServerDAO.updateUserProfileName(profileName,mdn,persisterTxn); // DEL - SET SUBSD - SET GET19 - Can be ignored in the Basic method. Only executed if ProfName is null
                    } catch (KnProvBOException ex) {
                        knLogger.error(methodName, "CBS connection error" + ex);
                        if (ownedTxn) {
                            rollback(persisterTxn);
                        }
                        throw new KnProvBOException(ex.getErrorCode(), ex.getMessage());
                    } }
                subsProfileRespDTO.setUserProfileName(profileName);
            }*/

            /*String deviceId = commonXDMServerDAO.getDeviceId(mdn, persisterTxn); // DEL - GET SUBSD - GET20 - No Null check, TODO -Check usage
            if(deviceId != null)
                subsProfileRespDTO.setDeviceId(deviceId);*/

            /*IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            if(subsAliasIdInfoMap==null){
                subsAliasIdInfoMap = provXDMServerDAO.selectSubsAliasIdInfoByMdn(Arrays.asList(mdn), persisterTxn); // DEL - GET SUBSD - GET21 - No null check.
            }*/

            /* If we are doing getSubscriberDetails using aliasId and alaisInfo the in response also we need to add */

            /*if (subsAliasIdInfoMap.get(mdn) != null) {
                List<KnSubsAliasInfoDTO> alaisInfo = subsAliasIdInfoMap.get(mdn);
                List<KnXDMSubsAliasInfoDTO> listaliasInfoDTO = new ArrayList<KnXDMSubsAliasInfoDTO>();
                for(KnSubsAliasInfoDTO alias : alaisInfo) {
                    KnXDMSubsAliasInfoDTO aliasInfoDTO = new KnXDMSubsAliasInfoDTO(alias.getAliasId(),alias.getAliasIdIssuer(),alias.getAliasIdType());
                    listaliasInfoDTO.add(aliasInfoDTO);
                }
                subsProfileRespDTO.setAliasInfoDTOList(listaliasInfoDTO);
            }*/

            //Privacy Opt Status ends
            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
        } catch (KnDBConnectionException ex) {
            knLogger.error(methodName, "DAO DBConnection Exception occured :" + ex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);

        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occurred : " + ex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            if (com.kodiak.xdms.server.common.resources.KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_INFO_NOT_FOUND, "Subscriber doesn't exists");
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception occurred :" + e.getErrorCode());
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while get Subscriber details" + e);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred while get Subscriber details", e);
        }
        knLogger.info(methodName, "EXIT:  getSubscriberDetails - ", subsProfileRespDTO);

        return subsProfileRespDTO;
    }

    /**
     * method to perform the Migration of one MDN to another MDN
     *
     * @param changeMDNInfoDTO KnIPChangeMDNInfoDTO
     * @param persisterTxn     KnPersisterTxn
     * @return KnOPProvDTO
     * @throws KnProvBOException BO Entity Exception
     * @throws KnFWException     Validation/AAS Exception
     */
    public KnOPSubsProfileInfoDTO changeMdn(KnIPChangeMDNInfoDTO changeMDNInfoDTO,boolean isAsyncCall, KnPersisterTxn persisterTxn) throws KnProvBOException, KnFWException {
        String methodName = "changeMdn(KnIPChangeMDNInfo, KnPersisterTxn)";
        boolean ownedTxn = false;
        knLogger.info(methodName, "ENTRY: change MDN with DTO - ", changeMDNInfoDTO, " with Txn - ", persisterTxn, " isAsyncCall - ", isAsyncCall);
        KnOPSubsProfileInfoDTO responseDTO;
        KnExtSubsPersistDTO extSubsPersistResponseDTO;
        try {
            // opening the transaction object.
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }
            //retrieving the local XDM PttServer Id
            try {
                xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
            } catch (KnBOException e) {
                knLogger.error(methodName, "failed to retrieve xdm Ptt Sever Id");
                knLogger.error(methodName, e);
                if (ownedTxn) {
                    rollback(persisterTxn);
                }
                throw new KnProvBOException(KnErrorCodes.BOEntity.XDMS_PTT_ID_NOT_FOUND, "Failed to retrieve XDM PTT Server ID", e);
            }
            //need to perform the validations on the Old MDN.

            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            // retrieve the old subscriber profile
            String oldMdn = changeMDNInfoDTO.getOldMDN();
            KnIPSubscriberInfoDTO subscriberInfoDTO = new KnIPSubscriberInfoDTO();
            subscriberInfoDTO.setMdn(changeMDNInfoDTO.getOldMDN());
            KnOPSubsProfileInfoDTO oldSubsProfileInfoDTO = provInfoUtil.retrieveSubscriberInfo(subscriberInfoDTO.getMdn(), persisterTxn);
            knLogger.debug(methodName, "retrieved old Subs Profile - ", oldSubsProfileInfoDTO);

            //check if the mdn is a pseudo mdn then restrict the operation
            if (oldSubsProfileInfoDTO.getPamAccId() != null && oldSubsProfileInfoDTO.getPamAccId() != 0) {
                knLogger.error(methodName, "Mdn is present as a pseudo number,operation not allowed");
                throw new KnProvBOException(KnErrorCodes.BOEntity.MDN_PRESENT_AS_PSEUDOMDN_IN_POCSUBSCRINFO,
                        "Mdn is present as a pseudo mdn in POCSUBSCRINFO");
            }

            // Create Subscriber with old Subscriber profile
            // new MDN auth status is set to active
            String newMdn = changeMDNInfoDTO.getNewMDN();

            //check if the mdn is present in pseudoNoPool and PamAccountInfo table
            boolean isMdnPresentInPamAccInfoTable = provXDMServerDAO.isMdnPresentInPamAccInfo(newMdn, persisterTxn);
            if (isMdnPresentInPamAccInfoTable) {
                knLogger.error(methodName, "New Mdn is present in PamAccountInfo");
                throw new KnProvBOException(KnErrorCodes.BOEntity.MDN_PRESENT_IN_PAMACCOUNTINFO,
                        "Mdn is present in PamAccountInfo");
            }

			KnOPSubsProfileInfoDTO subsProfile = null;
			try {
				subsProfile = provXDMServerDAO.selectSubscriberProfileByAliasMdn(newMdn, persisterTxn);
			} catch (KnDAOException e) {
				knLogger.error(methodName, e);
			}
			if (subsProfile != null) {
				knLogger.error(methodName, "Mdn is present as aliasMdn");
				throw new KnProvBOException(KnErrorCodes.BOEntity.MDN_PRESENT_AS_ALIASMDN,
						"Mdn is present as aliasMdn");
			}

            ArrayList<String> mdnList = new ArrayList<>();
            mdnList.add(newMdn);

            knLogger.debug(methodName, "Getting  External subscriber Profile");
            extSubsPersistResponseDTO = provXDMServerDAO.getExtSubscriberInfo(mdnList, persisterTxn);
            if (!(extSubsPersistResponseDTO.getExtSubs().isEmpty())) {
                knLogger.error(methodName, "Subscriber Already Present As External Subscriber");
                throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_ALREADY_EXISTS_AS_EXT_SUBS, "Subscriber Already exist as External Subscriber");
            }

            //Rejecting changeMDN request for subsClientType 7 & 8
            if (KnProvConstants.SUBS_CLIENT_TYPE.POC_NNI_Alias_MDN.value() == oldSubsProfileInfoDTO.getSubsClientType() ||
                    KnProvConstants.SUBS_CLIENT_TYPE.POC_NNI_Group_MDN.value() == oldSubsProfileInfoDTO.getSubsClientType()){
                throw new KnProvBOException(KnErrorCodes.Validator.CHANGE_MDN_NOT_ALLOWED_FOR_LMR,"MDN change not allowed for LMR subscriber/group");
            }

            //Rejecting changeMDN request for clientType 19 (4RE Camera)
            if (KnProvConstants.SUBS_CLIENT_TYPE.STANDALONECAMERA.value() == oldSubsProfileInfoDTO.getSubsClientType()){
                knLogger.debug("ClientType received for camera : ", oldSubsProfileInfoDTO.getSubsClientType());
                throw new KnProvBOException(KnErrorCodes.Validator.CHANGE_MDN_NOT_ALLOWED_FOR_4RE_CAMERA,"MDN change is not allowed for 4RE Camera");
            }

            List<String> mdns = new ArrayList<String>();
			mdns.add(TELURI + changeMDNInfoDTO.getNewMDN());
			List<KnOPSubsProfileInfoDTO> subsProfileInfoDTOList = provXDMServerDAO.selectSubscriberProfileByMCSIds(mdns,
					persisterTxn);
			if (!subsProfileInfoDTOList.isEmpty()) {
				for (KnOPSubsProfileInfoDTO knOPSubsProfileInfoDTO : subsProfileInfoDTOList) {
					if (!knOPSubsProfileInfoDTO.getMdn().equals(changeMDNInfoDTO.getOldMDN())) {
						knLogger.error(methodName, "MDN exist as MCS Id(s).");
						throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_ALREADY_EXISTS,
								"MDN exist as MCS Id(s).");
					}
				}
			}

            KnSubsProfilePersistDTO subsProfilePersistDTO = new KnSubsProfilePersistDTO();
            subsProfilePersistDTO.setMdn(changeMDNInfoDTO.getNewMDN());
            if (changeMDNInfoDTO.getNewIMEI() == null) {
                subsProfilePersistDTO.setIMEI(oldSubsProfileInfoDTO.getIMEI());
            } else {
                subsProfilePersistDTO.setIMEI(changeMDNInfoDTO.getNewIMEI());
            }
            // changes as part of the Load based partitioning logic
            String poCPttServerId = oldSubsProfileInfoDTO.getPoCHome();
            String presencePttServerId = oldSubsProfileInfoDTO.getPresenceHome();
//           String poCPttServerId = provInfoUtil.getSubscriberPoCHome(newMdn, persisterTxn);
            subsProfilePersistDTO.setPoCHome(poCPttServerId);
//            String presencePttServerId = provInfoUtil.getSubscriberPresenceHome(newMdn, persisterTxn);
            subsProfilePersistDTO.setPresenceHome(presencePttServerId);
            subsProfilePersistDTO.setXDMSHome(xdmPttServerId);
            long profileTime = Calendar.getInstance().getTimeInMillis();
            subsProfilePersistDTO.setProfileCreationTime(oldSubsProfileInfoDTO.getSubsCreationTime());
            subsProfilePersistDTO.setLastProfileUpdateTime(profileTime);
            if (null != oldSubsProfileInfoDTO.getNetworkName() && !oldSubsProfileInfoDTO.getNetworkName().isEmpty()) {
                subsProfilePersistDTO.setNetworkName(oldSubsProfileInfoDTO.getNetworkName());
            } else {
                subsProfilePersistDTO.setNetworkName(changeMDNInfoDTO.getNewMDN());
            }
            subsProfilePersistDTO.setServiceAuthStatus(oldSubsProfileInfoDTO.getServiceAuthStatus());
            subsProfilePersistDTO.setPublicSubscriptionType(oldSubsProfileInfoDTO.getPublicSubscriptionType());
            subsProfilePersistDTO.setCorporateSubscriptionType(oldSubsProfileInfoDTO.getCorporateSubscriptionType());
            subsProfilePersistDTO.setCorpId(oldSubsProfileInfoDTO.getCorpId());
            subsProfilePersistDTO.setCorpContactListId(oldSubsProfileInfoDTO.getCorpContactListId());
            String clientPassword = provXDMServerDAO.retrieveClientPassword(changeMDNInfoDTO.getOldMDN(), persisterTxn);
            subsProfilePersistDTO.setClientPassword(clientPassword);
            subsProfilePersistDTO.setPayType(oldSubsProfileInfoDTO.getPayType());
            subsProfilePersistDTO.setAffiliateId(oldSubsProfileInfoDTO.getAffiliateId());
            subsProfilePersistDTO.setPairingInd(oldSubsProfileInfoDTO.getPairingInd());
            subsProfilePersistDTO.setEmailAddress(oldSubsProfileInfoDTO.getEmailAddress());
            subsProfilePersistDTO.setSubsClientType(oldSubsProfileInfoDTO.getSubsClientType());
            subsProfilePersistDTO.setDispatchGroupMember(oldSubsProfileInfoDTO.getDispatchGroupMember());
            subsProfilePersistDTO.setClientPVmajorVer(oldSubsProfileInfoDTO.getClientPVmajorVer());
            subsProfilePersistDTO.setClientPVminorVer(oldSubsProfileInfoDTO.getClientPVminorVer());
            subsProfilePersistDTO.setAccountId(oldSubsProfileInfoDTO.getAccountId());
            subsProfilePersistDTO.setUserAgent(oldSubsProfileInfoDTO.getUserAgent());
            subsProfilePersistDTO.setLastActivationTime(oldSubsProfileInfoDTO.getLastActivationTime());
            subsProfilePersistDTO.setOldMdn(changeMDNInfoDTO.getOldMDN());
            subsProfilePersistDTO.setUfmi(oldSubsProfileInfoDTO.getUfmi());
            subsProfilePersistDTO.setiDenUserName(oldSubsProfileInfoDTO.getiDenUserName());
            subsProfilePersistDTO.setiDenPassword(oldSubsProfileInfoDTO.getiDenPassword());
            subsProfilePersistDTO.setiDenBusUnitId(oldSubsProfileInfoDTO.getiDenBusUnitId());
            subsProfilePersistDTO.setFirstNetIndicator(oldSubsProfileInfoDTO.getFirstNetIndicator());
            subsProfilePersistDTO.setMcpttCompliance(oldSubsProfileInfoDTO.getMcpttCompliance());
            subsProfilePersistDTO.setMcpttId(oldSubsProfileInfoDTO.getMcpttId());
            subsProfilePersistDTO.setSubsFS2(oldSubsProfileInfoDTO.getSubsFS2());
            subsProfilePersistDTO.setClientFS2(oldSubsProfileInfoDTO.getClientFS2());
            subsProfilePersistDTO.setActiveFS2(oldSubsProfileInfoDTO.getActiveFS2());
            subsProfilePersistDTO.setOpsFS2(oldSubsProfileInfoDTO.getOpsFS2());
            subsProfilePersistDTO.setCorpAdminFS2(oldSubsProfileInfoDTO.getCorpAdminFS2());
            subsProfilePersistDTO.setXdmsFS2(oldSubsProfileInfoDTO.getXdmsFS2());
            subsProfilePersistDTO.setUserProfileIndex(oldSubsProfileInfoDTO.getUserProfileIndex());
            subsProfilePersistDTO.setIsDefaultProfile(oldSubsProfileInfoDTO.getIsDefaultProfile());
            Boolean mcxIdIsMdn=false;
			if (oldSubsProfileInfoDTO.getMcId() == null
					|| oldSubsProfileInfoDTO.getMcId().equals(TELURI + oldSubsProfileInfoDTO.getMdn())) {
				subsProfilePersistDTO.setMcId(TELURI + changeMDNInfoDTO.getNewMDN());
				mcxIdIsMdn=true;
			} else {
				subsProfilePersistDTO.setMcId(oldSubsProfileInfoDTO.getMcId());
			}
			if (oldSubsProfileInfoDTO.getMcpttId() == null
					|| oldSubsProfileInfoDTO.getMcpttId().equals(TELURI + oldSubsProfileInfoDTO.getMdn())) {
				subsProfilePersistDTO.setMcpttId(TELURI + changeMDNInfoDTO.getNewMDN());
				mcxIdIsMdn=true;
			} else {
				subsProfilePersistDTO.setMcpttId(oldSubsProfileInfoDTO.getMcpttId());
			}
			if (oldSubsProfileInfoDTO.getMcVideoId() == null
					|| oldSubsProfileInfoDTO.getMcVideoId().equals(TELURI + oldSubsProfileInfoDTO.getMdn())) {
				subsProfilePersistDTO.setMcVideoId(TELURI + changeMDNInfoDTO.getNewMDN());
				mcxIdIsMdn=true;
			} else {
				subsProfilePersistDTO.setMcVideoId(oldSubsProfileInfoDTO.getMcVideoId());
			}
			if (oldSubsProfileInfoDTO.getMcDataId() == null
					|| oldSubsProfileInfoDTO.getMcDataId().equals(TELURI + oldSubsProfileInfoDTO.getMdn())) {
				subsProfilePersistDTO.setMcDataId(TELURI + changeMDNInfoDTO.getNewMDN());
				mcxIdIsMdn=true;
			} else {
				subsProfilePersistDTO.setMcDataId(oldSubsProfileInfoDTO.getMcDataId());
			}
            //verify if the subscriber is not in deactivated state
            if (oldSubsProfileInfoDTO.getServiceAuthStatus() != KnConstants.SERVICE_AUTH_STATUS.DEACTIVATED.value()) {
                knLogger.warn(methodName, "Old Subscriber is not in deactivated");
                subsProfilePersistDTO.setServiceAuthStatus(KnConstants.SERVICE_AUTH_STATUS.PROVISIONED.value());
                subsProfilePersistDTO.setClientPassword(null);
                subsProfilePersistDTO.setUserAgent(null);
            }
            subsProfilePersistDTO.setServiceStatusOp(KnConstants.SERVICE_STATUS_OP.PROVISIONED.value());
            subsProfilePersistDTO.setServiceStatusAuthUser(KnConstants.SERVICE_STATUS_AUTHUSER.ACTIVATED.value());
            long profileCreationTime = Calendar.getInstance().getTimeInMillis();

            if (KnConstants.MCSCOMPLIANCE == oldSubsProfileInfoDTO.getMcpttCompliance()) {
            	subsProfilePersistDTO.setServiceAuthStatus(oldSubsProfileInfoDTO.getPoCStatusAU());
                subsProfilePersistDTO.setClientPassword(oldSubsProfileInfoDTO.getClientPassword());
                subsProfilePersistDTO.setUserAgent(oldSubsProfileInfoDTO.getUserAgent());
                subsProfilePersistDTO.setServiceStatusOp(oldSubsProfileInfoDTO.getServiceStatusOp());
                subsProfilePersistDTO.setServiceStatusAuthUser(oldSubsProfileInfoDTO.getServiceStatusAuthUser());

                subsProfilePersistDTO.setProfileCreationTime(oldSubsProfileInfoDTO.getSubsCreationTime());
                subsProfilePersistDTO.setLastProfileUpdateTime(profileCreationTime);
                subsProfilePersistDTO.setLastActivationTime(oldSubsProfileInfoDTO.getLastActivationTime());
                subsProfilePersistDTO.setClientPVmajorVer(oldSubsProfileInfoDTO.getClientPVmajorVer());
                subsProfilePersistDTO.setClientPVminorVer(oldSubsProfileInfoDTO.getClientPVminorVer());

            }
            subsProfilePersistDTO.setHierarchyType(oldSubsProfileInfoDTO.getHierarchyType());

            subsProfilePersistDTO.setDispatchType(oldSubsProfileInfoDTO.getDispatchType());
            subsProfilePersistDTO.setUserId(oldSubsProfileInfoDTO.getUserId());
            subsProfilePersistDTO.setAliasMdn(oldSubsProfileInfoDTO.getAliasMdn());
            subsProfilePersistDTO.setLicenseType(oldSubsProfileInfoDTO.getLicenseType());
            subsProfilePersistDTO.setQppPkgId(oldSubsProfileInfoDTO.getQppPkgId());
            subsProfilePersistDTO.setFeatureRelVersion(oldSubsProfileInfoDTO.getFeatureRelVersion());
            subsProfilePersistDTO.setUserProfileFS2(oldSubsProfileInfoDTO.getUserProfileFS2());
            subsProfilePersistDTO.setClusterId(oldSubsProfileInfoDTO.getClusterId());
            subsProfilePersistDTO.setHierarchyId(oldSubsProfileInfoDTO.getHierarchyId());
            subsProfilePersistDTO.setHierarchyRoot(oldSubsProfileInfoDTO.getHierarchyRoot());
            knLogger.debug(methodName, "Proceeding to update User ID and MCX Ids to null");
            
          //Get All profile MDN' for given Real MDN
            List<String> allMdnList= getMdnForUPM(changeMDNInfoDTO.getOldMDN(),persisterTxn);
            //profileMdns
            List<String> profileMdnList=new ArrayList<>(allMdnList);
            profileMdnList.remove(changeMDNInfoDTO.getOldMDN());
            
            provXDMServerDAO.updateSubscriberUserID(subsProfilePersistDTO, persisterTxn);

            //invoke the DAO layer for creation of Profile
            KnSubsProfilePersistDTO respSubsProfilePersistDTO = provXDMServerDAO.createNewSubscrProfile(subsProfilePersistDTO, persisterTxn);
            knLogger.debug(methodName,"respSubsProfilePersistDTO :",respSubsProfilePersistDTO);

            //Identify the list of user profiles which are associated with the Subscriber i.e.,
            //Identify all the list of DG.POCSUBSCRINFO:MDNs whose OLD MC_ID, MCPTT_ID, MCDATA_ID, MCVIDEO_ID is same across all the subscriber profiles.
            //Along with the requested MCX IDs changes for the Subscriber MDN, Modify the MCX IDs for all those subscribers that are identified as user profiles associated with the Subscriber
			//if (mcxIdIsMdn) {
				provXDMServerDAO.updateMCSIdsForUserProfiles(subsProfilePersistDTO, oldSubsProfileInfoDTO.getMcId(),
						oldSubsProfileInfoDTO.getMcDataId(), oldSubsProfileInfoDTO.getMcVideoId(),
						oldSubsProfileInfoDTO.getMcpttId(), persisterTxn);
			//}

            KnOPSubsAddlInfoProfileDTO oldSubsAddlInfoDTO = provInfoUtil.retrieveSubscrAddlInfo(subscriberInfoDTO.getMdn(), persisterTxn);
            knLogger.debug(methodName, "retrieved old Subs AddlInfo Profile - ", oldSubsAddlInfoDTO);
            if (oldSubsAddlInfoDTO != null){
                KnSubsAddlInfoPersistDTO subsAddlInfoPersistDTO = new KnSubsAddlInfoPersistDTO();
                subsAddlInfoPersistDTO.setMdn(changeMDNInfoDTO.getNewMDN());
                subsAddlInfoPersistDTO.setDrxValue(oldSubsAddlInfoDTO.getDrxValue());
                subsAddlInfoPersistDTO.setTimeSlotType(oldSubsAddlInfoDTO.getTimeSlotType());
                subsAddlInfoPersistDTO.setCallHistoryDuration(oldSubsAddlInfoDTO.getCallHistoryDuration());
                subsAddlInfoPersistDTO.setDataPurgeDuration(oldSubsAddlInfoDTO.getDataPurgeDuration());
                subsAddlInfoPersistDTO.setDrxMO(oldSubsAddlInfoDTO.getDrxMO());
                subsAddlInfoPersistDTO.setDrxMT(oldSubsAddlInfoDTO.getDrxMT());
                subsAddlInfoPersistDTO.setDrxThreshCount(oldSubsAddlInfoDTO.getDrxThreshCount());
                subsAddlInfoPersistDTO.setfixedtsStartTime(oldSubsAddlInfoDTO.getfixedtsStartTime());
                subsAddlInfoPersistDTO.setFixedtsEndTime(oldSubsAddlInfoDTO.getFixedtsEndTime());
                subsAddlInfoPersistDTO.setDataPkgId(oldSubsAddlInfoDTO.getDataPkgId());
                subsAddlInfoPersistDTO.setTierPkgCode(oldSubsAddlInfoDTO.getTierPkgCode());
                 provXDMServerDAO.createNewSubscrAddlProfile(subsAddlInfoPersistDTO, persisterTxn);
                knLogger.debug(methodName,"Subscriber Addlinfo profile created");
            }
            List<String> existAddOnPkgids = provXDMServerDAO.selectSubAddOnPkgs(oldMdn,persisterTxn);
			knLogger.debug(methodName, "retrieved old Subs Addon Packages - ", existAddOnPkgids);
			if (existAddOnPkgids != null && !existAddOnPkgids.isEmpty()) {
				provXDMServerDAO.deleteSubAddlOnPkgs(oldMdn, persisterTxn);
				provXDMServerDAO.createSubAddOnPkgs(newMdn, existAddOnPkgids, persisterTxn);
			}
            boolean awareClient = false;
            if((String.valueOf(oldSubsProfileInfoDTO.getSubsClientType())).matches(AWARE_CLIENTS)){
                knLogger.debug(methodName, "Aware client, So not creating OIDC profile");
                awareClient = true;
            }
			String sipRandomPwdPlain = pwdUtil.generatePassword(com.kodiak.common.resources.KnConstants.MAX_OIDC_PASSWORD_LENGTH);
            IXDMServerDAO commonXdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
            KnXDMDeviceProvDTO knXDMDeviceProvDTO = commonXdmServerDAO.selectDeviceProfileByDeviceId(oldMdn, persisterTxn);
            if (KnConstants.MCSCOMPLIANCE == oldSubsProfileInfoDTO.getMcpttCompliance() && !awareClient) {
				IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
				KnDeviceInfoPersistDTO deviceInfoPersistDTO=new KnDeviceInfoPersistDTO();
                deviceInfoPersistDTO.setDeviceId(oldMdn);
                xdmServerDAO.deleteDeviceImpiInfo(deviceInfoPersistDTO, persisterTxn);
                xdmServerDAO.deleteDeviceInfo(deviceInfoPersistDTO, persisterTxn);
                deviceInfoPersistDTO=new KnDeviceInfoPersistDTO();
                deviceInfoPersistDTO.setDeviceId(newMdn);
                deviceInfoPersistDTO.setDeviceStatus(KnConstants.DEVICE_STATUS_OP.ACTIVATED.value());
                deviceInfoPersistDTO.setDeviceActTimeStamp(profileCreationTime);
                deviceInfoPersistDTO.setDeviceLastUsed(profileCreationTime);
                KnXDMSServiceConfigDTO xdmsServiceConfigDTO = genInfoUtil.retrieveXDMSServiceConfig(xdmPttServerId, persisterTxn);
                String realm = xdmsServiceConfigDTO.getAuthRealm();
                String deviceDigestPassword = provInfoUtil.generateHA1(KnConstants.TEL_URI_TEMPLATE+newMdn, realm, sipRandomPwdPlain);
                deviceInfoPersistDTO.setDeviceDigestPassword(deviceDigestPassword);
                deviceInfoPersistDTO.setDeviceCreatedAs(KnConstants.DEVICECREATEDAS);
                deviceInfoPersistDTO.setDeviceClientId(newMdn);
                deviceInfoPersistDTO.setDeviceshared(KnConstants.DEVICESHARED);
                deviceInfoPersistDTO.setDeviceIMPI(KnConstants.TEL_URI_TEMPLATE+newMdn);
                deviceInfoPersistDTO.setCorpId(oldSubsProfileInfoDTO.getCorpId());
                xdmServerDAO.createDeviceInfo(deviceInfoPersistDTO, persisterTxn);

                KnDeviceImpiInfoPersistDTO deviceImpiInfo= new KnDeviceImpiInfoPersistDTO();
                deviceImpiInfo.setDeviceImpi(KnConstants.TEL_URI_TEMPLATE+newMdn);
                deviceImpiInfo.setDeviceImpu(KnConstants.TEL_URI_TEMPLATE+newMdn);
                xdmServerDAO.createDeviceImpiInfo(deviceImpiInfo, persisterTxn);

            } else if (KnConstants.MCSCOMPLIANCE != oldSubsProfileInfoDTO.getMcpttCompliance() && null != knXDMDeviceProvDTO &&
                    knXDMDeviceProvDTO.getDeviceCreatedAs() == IMPLICIT_DEVICE) {
                //Below changes are added for XDM-10077
                KnDeviceInfoPersistDTO deviceInfoPersistDTO=new KnDeviceInfoPersistDTO();
                deviceInfoPersistDTO.setDeviceId(oldMdn);
                commonXdmServerDAO.deleteDeviceImpiInfo(deviceInfoPersistDTO, persisterTxn);
                commonXdmServerDAO.deleteDeviceInfo(deviceInfoPersistDTO, persisterTxn);
                deviceInfoPersistDTO=new KnDeviceInfoPersistDTO();
                deviceInfoPersistDTO.setDeviceId(newMdn);
                deviceInfoPersistDTO.setDeviceStatus(knXDMDeviceProvDTO.getDeviceStatus());
                deviceInfoPersistDTO.setDeviceActTimeStamp(knXDMDeviceProvDTO.getDeviceActTimeStamp());
                deviceInfoPersistDTO.setDeviceLastUsed(knXDMDeviceProvDTO.getDeviceLastUsed());
                deviceInfoPersistDTO.setDeviceDigestPassword(knXDMDeviceProvDTO.getDevicePassword());
                deviceInfoPersistDTO.setDeviceCreatedAs(knXDMDeviceProvDTO.getDeviceCreatedAs());
                deviceInfoPersistDTO.setDeviceClientId(knXDMDeviceProvDTO.getDeviceClientId());
                deviceInfoPersistDTO.setDeviceshared(knXDMDeviceProvDTO.getDeviceShared());
                deviceInfoPersistDTO.setDeviceIMPI(KnConstants.TEL_URI_TEMPLATE+newMdn);
                deviceInfoPersistDTO.setCorpId(knXDMDeviceProvDTO.getCorpId());
                deviceInfoPersistDTO.setReqDeviceId(newMdn);
                deviceInfoPersistDTO.setDeviceName(knXDMDeviceProvDTO.getDeviceName());
                deviceInfoPersistDTO.setDeviceSubscriberMdn(knXDMDeviceProvDTO.getDeviceSubscrMdn());
                deviceInfoPersistDTO.setDeviceType(knXDMDeviceProvDTO.getDeviceType());
                commonXdmServerDAO.createDeviceInfo(deviceInfoPersistDTO, persisterTxn);
                KnDeviceImpiInfoPersistDTO deviceImpiInfo= new KnDeviceImpiInfoPersistDTO();
                deviceImpiInfo.setDeviceImpi(KnConstants.TEL_URI_TEMPLATE+newMdn);
                deviceImpiInfo.setDeviceImpu(KnConstants.TEL_URI_TEMPLATE+newMdn);
                commonXdmServerDAO.createDeviceImpiInfo(deviceImpiInfo, persisterTxn);
            }

            // IDM user profile change
            KnPayloadIP payloadIP = new KnPayloadIP();
            int clusterId = Integer.parseInt(System.getenv(CLUSTERID_ENV_NAME));
            if(!isAsyncCall){
                provInfoUtil.updateIdmAndOidcProfilesForChangeMdnAPI(oldMdn, newMdn, oldSubsProfileInfoDTO, subsProfilePersistDTO, sipRandomPwdPlain, persisterTxn);
            }else{
                payloadIP.setSipRandomPwdPlain(sipRandomPwdPlain);
                payloadIP.setOldMdn(oldMdn);
            }


            String oldMdnXcaprootUri = genInfoUtil.getXCAPRootURI(oldMdn, persisterTxn);
            IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
            provXDMServerDAO.deleteSubApn(oldMdn, persisterTxn);
            provXDMServerDAO.deleteClientSuppVocoder(oldMdn,persisterTxn);
            knLogger.debug(methodName,"Client supp vocoder ID for old mdn deleted");

            if (oldSubsProfileInfoDTO.getCorpId() != 0) {
                knLogger.debug(methodName, "Subscriber is corp or corp-public type,Retrive the extCorpId for the corp=", oldSubsProfileInfoDTO.getCorpId());
                String extCorpId = provXDMServerDAO.retrieveExtCorporationId(oldSubsProfileInfoDTO.getCorpId(), persisterTxn);
                List<Integer> clientTypeList = new ArrayList<>();
                Collections.addAll(clientTypeList, com.kodiak.common.resources.KnConstants.SUBS_CLIENT_TYPE.ALIASMDN.value(),
                        com.kodiak.common.resources.KnConstants.SUBS_CLIENT_TYPE.GROUPMDN.value(), com.kodiak.common.resources.KnConstants.SUBS_CLIENT_TYPE.SGMDNPATCH.value());
                int aliasNGroupMdnCount = provXDMServerDAO.getSubsCountOfClientTypeForCorp(oldSubsProfileInfoDTO.getCorpId(), clientTypeList, persisterTxn);
                if (aliasNGroupMdnCount == 0) {
                    knLogger.debug(methodName, "Alias/Group mdn are not present in the corp with extCorp =", extCorpId, " ,Nullify LinkedGwKey ");
                    KnCorpProfilePersistDTO corpProfilePersistDTO = new KnCorpProfilePersistDTO();
                    corpProfilePersistDTO.setExtCorpId(extCorpId);
                    provXDMServerDAO.updateLinkedGwKeyOfCorp(corpProfilePersistDTO, persisterTxn);
                    knLogger.debug(methodName, "LinkedGwKey is nullified in corpInfo table");
                }
                //knLogger.info(methodName, " Calling Update Etag for Corp", extCorpId);
                //updateEtagForNNISubscr(extCorpId, persisterTxn);
            }


// Internet APN changes ....
            String apnName = genInfoUtil.getDefaultAPNName(persisterTxn);
            if (KnConstants.MCSCOMPLIANCE == oldSubsProfileInfoDTO.getMcsCompliance()){
            	apnName=KnConstants.DEFAULTAPNNAME;
            }
            // verify apn exists or not ..
            Integer apnId = genInfoUtil.getAPNId(apnName, persisterTxn);
            knLogger.info(methodName, " APNID ", apnId, "APN NAME  :", apnName);
            if (apnId == null) {
                knLogger.error(methodName, " Default APN not exists , APN_INFO_NOT_FOUND ");
                throw new KnProvBOException(KnErrorCodes.BOEntity.APN_INFO_NOT_FOUND, " APN_INFO_NOT_FOUND ");
            }
            provXDMServerDAO.addSubApn(newMdn, apnId, persisterTxn);
//            // deactivate the old MDN
//            KnSubsProfilePersistDTO oldSubsProfilePersistDTO = new KnSubsProfilePersistDTO();
//            oldSubsProfilePersistDTO.setMdn(oldMdn);
//            oldSubsProfilePersistDTO.setServiceAuthStatus(KnConstants.SERVICE_AUTH_STATUS.DEACTIVATED.value());
//            provXDMServerDAO.updateServiceAuthStatus(oldSubsProfilePersistDTO, persisterTxn);
//            knLogger.debug( methodName, "De-activated the Old Mdn - " , oldMdn);

            provXDMServerDAO.updateSubscrRoamingProfileMdn(oldMdn, newMdn, persisterTxn);
            knLogger.debug(methodName, "Updated the old mdn [", KnGDPRTemplate.mdn(oldMdn), "] of subscr roaming profile",
                    " with new mdn [", KnGDPRTemplate.mdn(newMdn), "]");

            // Update the MDN in the below tables
            // DG.XDM_ResourceList, DG.XDM_ResourceListIndexDoc
            // DG.XDM_CorpResourceListIndexDoc
            // DG.XDM_Directory, DG.XDM_ContactList,
            // DG.XDM_ContactList_DocMap

            IXDMServerDAO xdmServerDA0 = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
            //retrieve the dir etag of the old mdn required for notification
            int previousEtag = xdmServerDA0.getCurrentEtagForDirDoc(oldMdn, persisterTxn);
            
           

            if (oldSubsProfileInfoDTO.getCorporateSubscriptionType() == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
                xdmServerDA0.updateCorpResourceListIndexDocMdn(oldMdn, newMdn, persisterTxn);
                knLogger.debug(methodName, "Updated the old mdn [",KnGDPRTemplate.mdn(oldMdn), "] of  XDM corp resource List index doc ",
                        "with new mdn [", KnGDPRTemplate.mdn(newMdn), "]");
            }

            xdmServerDA0.updateContactListMdn(oldMdn, newMdn, persisterTxn);
            knLogger.debug(methodName, "Updated the old mdn [", KnGDPRTemplate.mdn(oldMdn), "] of contact list doc ",
                    "with new mdn [", KnGDPRTemplate.mdn(newMdn), "]");

            xdmServerDA0.updateContactListDocMapMdn(oldMdn, newMdn, persisterTxn);
            knLogger.debug(methodName, "Updated the old mdn [", KnGDPRTemplate.mdn(oldMdn), "] of contact list doc map ",
                    "with new mdn [", KnGDPRTemplate.mdn(newMdn), "]");

//            deleting the old Subscriber profile
//            provXDMServerDAO.deleteSubscriberProfile(oldMdn, persisterTxn);
//            knLogger.debug( methodName, "Deleted Old Subscriber Profile ");
            xdmServerDA0.updateDirDocMdn(oldMdn, newMdn, persisterTxn);
            knLogger.debug(methodName, "Updated the old mdn [", KnGDPRTemplate.mdn(oldMdn), "] of dir doc with new mdn [", KnGDPRTemplate.mdn(newMdn), "]"
            );

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }

            //todo... event put...
            //create event
            //knLogger.info(methodName, "No. of events before adding the event ",eventStore.getEventCount());

            //sending the response
            // responseDTO = new KnOPProvDTO();
            responseDTO = oldSubsProfileInfoDTO;
            responseDTO.setResponseMessage(KnProvConstants.CHANGE_MDN_SUCCESS);
            responseDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.SUCCESS.value());
            responseDTO.setMdn(oldMdn);
            responseDTO.setCorpId(oldSubsProfileInfoDTO.getCorpId());
            responseDTO.setActiveFS2(oldSubsProfileInfoDTO.getActiveFS2());
            responseDTO.setXDMSHome(oldSubsProfileInfoDTO.getXDMSHome());
            
            //updating LASTPROFILEUPDATETIME in POCSUBSCRINFO profile mdn notiy
			IProvXDMServerDAO xdmServerDAONew = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB)
					.createProvXDMServerDAO();
			Map<String, Collection<KnDocChangeListDTO>> profileMdnEtagMap = xdmServerDAONew
					.profileMdnEtagUpdate(profileMdnList, persisterTxn);
			responseDTO.setProfileMdnEtagMap(profileMdnEtagMap);
			if (profileMdnEtagMap != null && !profileMdnEtagMap.isEmpty()) {
				responseDTO.setMcsXcapRootUriMap(
						genInfoUtil.getXCAPRootURI(new ArrayList<>(profileMdnEtagMap.keySet()), persisterTxn, true));
			}
            
            KnOPDirChgDTO dirChgDTO = new KnOPDirChgDTO();
            /*KnXDMSServiceConfigDTO xdmsServiceConfigDTO = genInfoUtil.retrieveXDMSServiceConfig(xdmPttServerId, persisterTxn);
            dirChgDTO.setXcapRootURI(xdmsServiceConfigDTO.getXcapRootUri());
            */
            dirChgDTO.setXcapRootURI(oldMdnXcaprootUri);
            String dirDocUri = genInfoUtil.generateDirDocUri(oldMdn);
            dirChgDTO.setDirUri(dirDocUri);
            dirChgDTO.setDirPrevEtag(String.valueOf(previousEtag));
            dirChgDTO.setProtoVersion(String.valueOf(oldSubsProfileInfoDTO.getClientPVmajorVer()));
            dirChgDTO.setClientType(oldSubsProfileInfoDTO.getSubsClientType());
            boolean subsUpmBit = com.kodiak.common.resources.KnGeneralUtil.getFeatureBitValue(oldSubsProfileInfoDTO.getActiveFS2(), USER_PROFILE_MGMT_BIT);
            knLogger.debug(methodName,"subsUpmBit :",subsUpmBit);
            if(subsUpmBit){
                //owner mdn is  0 - Base Mdn,1 - Profile Mdn
                dirChgDTO.setNtfyOnAnyMDN(0);
            }
            
            List<KnOPDirChgDTO> profilrMdnDirChgDTOs= new ArrayList<KnOPDirChgDTO>();
            
            for (String profileMdn : profileMdnList) {
            	 KnOPDirChgDTO profilMdnDirChgDTO = new KnOPDirChgDTO();
            	 profilMdnDirChgDTO.setXcapRootURI( genInfoUtil.getXCAPRootURI(profileMdn, persisterTxn));
                 String profilMdnDirDocUri = genInfoUtil.generateDirDocUri(profileMdn);
                 profilMdnDirChgDTO.setDirUri(profilMdnDirDocUri);
                 profilMdnDirChgDTO.setDirPrevEtag(String.valueOf(xdmServerDA0.getCurrentEtagForDirDoc(profileMdn, persisterTxn)));
                 profilMdnDirChgDTO.setProtoVersion(String.valueOf(oldSubsProfileInfoDTO.getClientPVmajorVer()));
                 profilMdnDirChgDTO.setClientType(oldSubsProfileInfoDTO.getSubsClientType());
                 knLogger.debug(methodName,"subsUpmBit :",subsUpmBit);
                 if(subsUpmBit){
                     //owner mdn is  0 - Base Mdn,1 - Profile Mdn
                	 profilMdnDirChgDTO.setNtfyOnAnyMDN(1);
                 }
                 profilrMdnDirChgDTOs.add(profilMdnDirChgDTO);
			}
            responseDTO.setDirChgtDTO(dirChgDTO);
            responseDTO.setDirChgDTOs(profilrMdnDirChgDTOs);
            responseDTO.setLastProfileUpdateTime(profileTime);

            knLogger.debug(methodName, "Response DTO - ", responseDTO);
            
            //update the dir doc etag for profile MDNs
            xdmServerDA0.updateEtagForDirDocOfMdnList(profileMdnList, persisterTxn);
            knLogger.debug(methodName, "Successfully updated the xdm directory");

            String oneMessageEnabled = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn).get(KnConstants.ONE_MESSAGE_SERVICE_ENABLED);
            knLogger.debug(methodName, "oneMessageEnabled", oneMessageEnabled);

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
            //welcome sms notification ..
            if (null != oneMessageEnabled && oneMessageEnabled.equals(ONE_MSG_STATUS.ENABLED.value())) {
                int subsClientType = oldSubsProfileInfoDTO.getSubsClientType();
                if (subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value() || subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value() ||
                        subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value()) {

                    sendSMSNotification(subsClientType, newMdn, KnProvConstants.CHANGEMDN_SMS_ID_PTTRADIO);
                } else if (subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.HANDSET.value() ||
                        subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.POCDONORRADIO.value()) {

                    sendSMSNotification(subsClientType, newMdn, KnProvConstants.UPDATE_SMS_ID);
                }
            }


            long etag = provXDMServerDAO.getTGSSDocEtag(oldMdn, persisterTxn);
            if(etag>=0) {
                provXDMServerDAO.deleteTGSSDoc(oldMdn, persisterTxn);
                provXDMServerDAO.createTGSSDoc(newMdn, etag, persisterTxn);
            }

            List<Integer> groupIds = provXDMServerDAO.selectSSChannelGrpInfo(oldMdn,persisterTxn);
            knLogger.debug(methodName, "retrieved old Subs TGSS groupid - ", groupIds);
            if (groupIds != null && !groupIds.isEmpty()) {
                provXDMServerDAO.deleteSSChannelGrpInfo(oldMdn, persisterTxn);
                provXDMServerDAO.createSSChannelGrpInfo(newMdn, groupIds, persisterTxn);
            }
            //RECORDING_TARGET_INFO

            Map<String, String> microServicesParamNameValueMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
            String targetInfoOptimization = microServicesParamNameValueMap.get(com.kodiak.xdms.server.common.resources.KnConstants.TARGET_INFO_OPTIMIZATION);
            if (!com.kodiak.xdms.server.common.util.KnGeneralUtil.isTargetInfoOptimizationEnabled(targetInfoOptimization)) {
                knLogger.debug(methodName, "Target Info Optimization is enabled");
                Set<String> targetMdnSet = new HashSet<>();
                targetMdnSet.add(oldMdn);
                IXDMServerDAO commonXDMServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
                Collection<KnRecordingTargetInfoDTO> recordingTargetList = commonXDMServerDAO.getRecordingInfoTargetByTarget(targetMdnSet, persisterTxn);
                if (null != recordingTargetList && !recordingTargetList.isEmpty()) {
                    knLogger.debug(methodName, "recordingTargetList is not empty, hence updating the record");
                    commonXDMServerDAO.deleteRecordingInfoTargetByTarget(targetMdnSet, persisterTxn);
                    recordingTargetList.iterator().next().setTarget(newMdn);
                    commonXDMServerDAO.createRecordingInfoForTarget(recordingTargetList, persisterTxn);
                }
            }
            knLogger.info(methodName, "EXIT: change MDN of Subscriber operation ");
            responseDTO.setKnPayloadIP(payloadIP);
            knLogger.debug(" set the data ",payloadIP);
            return responseDTO;
        } catch (KnDBConnectionException ex) {
            knLogger.error(methodName, "DAO DBConnection Exception occured :", ex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);

        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occurred : ", ex);
            knLogger.error(methodName, ex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                knLogger.error(methodName, "Old Mdn [", KnGDPRTemplate.mdn(changeMDNInfoDTO.getOldMDN()), "] Info Not found "
                );
                throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_INFO_NOT_FOUND,
                        "Old Mdn does not exists - " + changeMDNInfoDTO.getOldMDN());

            } else if (KnErrorCodes.DAO.ROW_ALREADY_EXISTS.equals(ex.getErrorCode())) {
                knLogger.error(methodName, "New Mdn [", KnGDPRTemplate.mdn(changeMDNInfoDTO.getNewMDN()), "] already exists "
                );
                throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_ALREADY_EXISTS,
                        "New Mdn already exists - " + changeMDNInfoDTO.getNewMDN());

            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception occurred :", e);
            knLogger.error(methodName, e);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while change MDN of Subscriber");
            knLogger.error(methodName, e);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred while change MDN of Subscriber", e);
        }

    }

    /**
     * method to delete the data of the old Subscriber Info
     * will be used while the change mdn operation of the xdm mediator
     *
     * @param changeMDNInfoDTO KnIPChangeMDNInfoDTO
     * @param persisterTxn     KnPersisterTxn
     * @return KnOPProvDTO
     */
    public KnOPProvDTO removeSubsInfo(KnIPChangeMDNInfoDTO changeMDNInfoDTO, KnPersisterTxn persisterTxn)
            throws KnProvBOException {
        String methodName = "removeSubsInfo(KnIPChangeMDNInfoDTO, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY: Remove old Subscriber Info");
        boolean ownedTxn = false;
        KnOPProvDTO responseDTO;
        try {

            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            // retrieve the old subscriber profile
            String oldMdn = changeMDNInfoDTO.getOldMDN();
//            provXDMServerDAO.retrieveMdnServiceAuthStatus(oldMdn, persisterTxn);

            //deleting the old Subscriber profile
            provXDMServerDAO.deleteSubscriberProfile(oldMdn, persisterTxn);
            knLogger.debug(methodName, "Deleted Old Subscriber Profile ");

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }

            //sending the response
            responseDTO = new KnOPProvDTO();
            responseDTO.setResponseMessage(KnProvConstants.REMOVE_SUBSCRIBER_SUCCESS);
            responseDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.SUCCESS.value());

            knLogger.debug(methodName, "Response DTO - ", responseDTO);
            knLogger.info(methodName, "EXIT: change removal of Subscriber operation ");

            return responseDTO;
        } catch (KnDBConnectionException ex) {
            knLogger.error(methodName, "DAO DBConnection Exception occured :", ex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);

        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occurred : ", ex);
            knLogger.error(methodName, ex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                knLogger.error(methodName, "Old Mdn [", KnGDPRTemplate.mdn(changeMDNInfoDTO.getOldMDN()), "] Info Not found "
                );
                throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_INFO_NOT_FOUND,
                        "Old Mdn does not exists - " + changeMDNInfoDTO.getOldMDN());

            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception occurred :", e);
            knLogger.error(methodName, e);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while removal of Subscriber");
            knLogger.error(methodName, e);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred while removal of " +
                    " old Subscriber Info", e);
        }

    }

    /**
     * method to perform the force sync operation
     *
     * @param subscriberDTO KnIPSubscriberInfoDTO
     * @param persisterTxn  KnPersisterTxn
     * @return KnOPSubsProfileInfoDTO
     * @throws KnProvBOException BO Entity Exception
     * @throws KnFWException     Frame Work Exception
     */
    public KnOPSubsProfileInfoDTO forceSync(KnIPSubscriberInfoDTO subscriberDTO, KnPersisterTxn persisterTxn) throws KnProvBOException, KnFWException {
        String methodName = "forceSync(KnIPSubscriberInfoDTO, KnPersisterTxn)";
        boolean ownedTxn = false;
        knLogger.info(methodName, "ENTRY: force sync request with DTO - ", subscriberDTO, " with Txn - ", persisterTxn);
        KnOPSubsProfileInfoDTO responseDTO;
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }
            try {
                xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
            } catch (KnBOException e) {
                knLogger.error(methodName, "failed to retrieve xdm Ptt Sever Id");
                knLogger.error(methodName, e);
                if (ownedTxn) {
                    rollback(persisterTxn);
                }
                throw new KnProvBOException(KnErrorCodes.BOEntity.XDMS_PTT_ID_NOT_FOUND, "Failed to retrieve XDM PTT Server ID", e);
            }

            String mdn = subscriberDTO.getMdn();
            //retrieve the Service Auth Status of the MDN or Subscriber
            responseDTO = provInfoUtil.retrieveSubscriberInfo(mdn, persisterTxn);
            int existingSvcAuthStatus = responseDTO.getServiceAuthStatus();

            KnSubsProfilePersistDTO validateProfilePersistDTO = new KnSubsProfilePersistDTO();
            validateProfilePersistDTO.setInputDTO(subscriberDTO);
            validateProfilePersistDTO.setServiceAuthStatus(existingSvcAuthStatus);

            //validate the service auth Status
            validatorFwk.validate(validateProfilePersistDTO);
            knLogger.debug(methodName, "Validations are successful");

            //populate the persist DTO for the DB update.

            KnSubsProfilePersistDTO subsProfilePersistDTO = new KnSubsProfilePersistDTO();
            subsProfilePersistDTO.setMdn(mdn);
            long lastProfileUpdateTime = Calendar.getInstance().getTimeInMillis();
            subsProfilePersistDTO.setLastProfileUpdateTime(lastProfileUpdateTime);

            IProvXDMServerDAO xdmServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            xdmServerDAO.updateLastProfileUpdateTime(subsProfilePersistDTO, persisterTxn);
            knLogger.debug(methodName, "updated subscriber profile successfully");

            IXDMServerDAO commonXDMServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
            int previousEtag = commonXDMServerDAO.getCurrentEtagForDirDoc(mdn, persisterTxn);
            commonXDMServerDAO.updateEtagForDirDoc(mdn, persisterTxn);
            knLogger.debug(methodName, "Successfully updated the xdm directory");

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }

            //populate the response DTO
            responseDTO.setResponseMessage(KnProvConstants.FORCE_SYNC_SUCCESS);
            responseDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.SUCCESS.value());
            KnOPDirChgDTO dirChgDTO = new KnOPDirChgDTO();
            KnXDMSServiceConfigDTO xdmsServiceConfigDTO = genInfoUtil.retrieveXDMSServiceConfig(xdmPttServerId, persisterTxn);
            //dirChgDTO.setXcapRootURI(xdmsServiceConfigDTO.getXcapRootUri());
            dirChgDTO.setXcapRootURI(genInfoUtil.getXCAPRootURI(mdn, persisterTxn));
            KnOPDocChgDTO docChgDTO = new KnOPDocChgDTO();
            docChgDTO.setDocumentChgType(KnConstants.DOC_CHANGE_TYPE.REPLACE.value());
            String dirDocUri = genInfoUtil.generateDirDocUri(mdn);
            dirChgDTO.setDirUri(dirDocUri);
            dirChgDTO.setPocHome(responseDTO.getPoCHome());
            dirChgDTO.setPresenceHome(responseDTO.getPresenceHome());
//            dirChgDTO.setDirPrevEtag(String.valueOf(previousEtag));
            int newEtag = previousEtag + 1;
            dirChgDTO.setDirNewEtag(String.valueOf(newEtag));
            responseDTO.setDirChgtDTO(dirChgDTO);
            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
            knLogger.debug(methodName, "Response DTO - ", responseDTO);
            knLogger.info(methodName, "EXIT: force sync of Subscriber operation ");

            return responseDTO;
        } catch (KnDBConnectionException ex) {
            knLogger.error(methodName, "DAO DBConnection Exception occured :", ex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);
        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occurred : ", ex);
            knLogger.error(methodName, ex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_INFO_NOT_FOUND, "Subscriber doesnt exists");
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception occurred :", e);
            knLogger.error(methodName, e);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw e;
        } catch (KnValidationException vex) {
            knLogger.error(methodName, "Validation Exception occurred :", vex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw vex;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while force sync of Subscriber");
            knLogger.error(methodName, e);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred while force sync of Subscriber", e);
        }
    }

    /**
     * method to activate the Subscriber
     * this method performs the initial activation of the Subscriber
     *
     * @param activateMdnInfoDTO KnIPActivateMDNInfoDTO
     * @param persisterTxn       KnPersisterTxn
     * @return KnOPActivationInfoDTO
     * @throws KnProvBOException     BO entity Exceptions
     * @throws KnValidationException Validation Exception
     */
    public KnOPActivationInfoDTO activateSubscriber(KnIPActivateMDNInfoDTO activateMdnInfoDTO, KnPersisterTxn persisterTxn) throws KnProvBOException, KnValidationException {
        String methodName = "activateSubscriber(KnIPActivateMDNInfoDTO)";
        boolean ownedTxn = false;
        KnSubsProfilePersistDTO subsProfilePersistDTO = new KnSubsProfilePersistDTO();
        knLogger.info(methodName, "ENTRY: ActivateSubscriber ", activateMdnInfoDTO, ", ",
                " persisterTxn - ", persisterTxn);
        KnOPActivationInfoDTO responseDTO = null;
        String protocolVersion ="";
        boolean performPrivacyOperation=false;
        Integer privacyFlagToExecute=0;
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            try {
                xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
            } catch (KnBOException e) {
                knLogger.error(methodName, "failed to retrieve xdm Ptt Sever Id");
                knLogger.error(methodName, e);
                if (ownedTxn) {
                    rollback(persisterTxn);
                }
                throw new KnProvBOException(KnErrorCodes.BOEntity.XDMS_PTT_ID_NOT_FOUND, "Failed to retrieve XDM PTT Server ID", e);
            }

            IXDMServerDAO xdmDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
            String mdn = activateMdnInfoDTO.getMdn();
            //retrieve the Service Auth Status of the MDN or Subscriber
            KnOPSubsProfileInfoDTO subsProfileInfoDTO = provInfoUtil.retrieveSubscriberInfo(mdn, persisterTxn);
            int serviceAuthStatus = subsProfileInfoDTO.getServiceAuthStatus();
            int serviceAuthStatusOp = subsProfileInfoDTO.getServiceStatusOp();
            int serviceAuthStatusAuth = subsProfileInfoDTO.getServiceStatusAuthUser();
            Integer dbClientType = subsProfileInfoDTO.getSubsClientType();
            //Added for device login flow
            if(serviceAuthStatusOp == KnProvConstants.SUBSCR_AUTH_STATUS.PROVISION.value()){
                knLogger.debug(methodName, "Service auth status OP is provisioned ");
                subsProfilePersistDTO.setServiceStatusOp(KnProvConstants.SUBSCR_AUTH_STATUS.ACTIVE.value());
                subsProfilePersistDTO.setServiceAuthStatus(KnGenInfoUtil.calculateServiceAuthStatus(KnProvConstants.SUBSCR_AUTH_STATUS.ACTIVE.value(), serviceAuthStatusAuth));
            }else{
                subsProfilePersistDTO.setServiceAuthStatus(subsProfileInfoDTO.getServiceAuthStatus());
                subsProfilePersistDTO.setServiceStatusOp(subsProfileInfoDTO.getServiceStatusOp());
            }

            subsProfilePersistDTO.setMdn(mdn);
            subsProfilePersistDTO.setIMEI(activateMdnInfoDTO.getIMEI());
            subsProfilePersistDTO.setSwType(activateMdnInfoDTO.getSwType());
            subsProfilePersistDTO.setPlatformType(activateMdnInfoDTO.getPlatformType());
            subsProfilePersistDTO.setServiceStatusAuthUser(serviceAuthStatusAuth);

            // updating MCS Ids
			if (subsProfileInfoDTO.getMcId() == null) {
				subsProfilePersistDTO.setMcId(TELURI + mdn);
				subsProfilePersistDTO.setMcpttId(TELURI + mdn);
				subsProfilePersistDTO.setMcVideoId(TELURI + mdn);
				subsProfilePersistDTO.setMcDataId(TELURI + mdn);
			    updateMCSIds(persisterTxn, subsProfilePersistDTO);
			}

            KnUserAgentDTO userAgentDTO = activateMdnInfoDTO.getUserAgentDTO();
            knLogger.debug(methodName,"userAgentDTO :",userAgentDTO);
            int clientPVMajorVersion = 0;
            if (userAgentDTO != null) {
                protocolVersion = userAgentDTO.getProtocolVersion();
                if (protocolVersion != null) {
                    String[] pv = protocolVersion.split("\\.");
                    if (pv != null && pv[0] != null) {
                        subsProfilePersistDTO.setClientPVmajorVer(Integer.parseInt(pv[0]));
                        clientPVMajorVersion = Integer.parseInt(pv[0]);
                    }

                    if (pv != null && pv[1] != null) {
                        subsProfilePersistDTO.setClientPVminorVer(Integer.parseInt(pv[1]));
                    }
                }
            }

            if (clientPVMajorVersion < KnConstants.PROTOCOL_VERSION_13_X) {
                //Need to check if the received client type is same as the client type provided while create of subs profile
                if (activateMdnInfoDTO.getSubsClientType() != null && !(activateMdnInfoDTO.getSubsClientType().equals(subsProfileInfoDTO.getSubsClientType())) &&
                        !((activateMdnInfoDTO.getSubsClientType().equals(KnProvConstants.SUBS_CLIENT_TYPE.HANDSET.value()) &&
                                ((subsProfileInfoDTO.getSubsClientType().equals(KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value())) ||
                                        (subsProfileInfoDTO.getSubsClientType().equals(KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value())) ||
                                        (subsProfileInfoDTO.getSubsClientType().equals(KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value())) ||
                                        (subsProfileInfoDTO.getSubsClientType().equals(KnProvConstants.SUBS_CLIENT_TYPE.POC_DONOR_RADIO.value())) ||
                                        (subsProfileInfoDTO.getSubsClientType().equals(KnProvConstants.SUBS_CLIENT_TYPE.CROSS_CARRIER_PTT_CLIENT.value())) ||
                                        (subsProfileInfoDTO.getSubsClientType().equals(KnProvConstants.SUBS_CLIENT_TYPE.PDVCONNECT.value()))))
                                ||
                                (activateMdnInfoDTO.getSubsClientType().equals(KnProvConstants.SUBS_CLIENT_TYPE.THIRDPARTYPOCCLIENT.value()) &&
                                        (subsProfileInfoDTO.getSubsClientType().equals(KnProvConstants.SUBS_CLIENT_TYPE.MOBILE_CLIENT.value())))
                                ||
                                (activateMdnInfoDTO.getSubsClientType().equals(KnProvConstants.SUBS_CLIENT_TYPE.DISPATCH_CLIENT.value()) &&
                                        (subsProfileInfoDTO.getSubsClientType().equals(KnProvConstants.SUBS_CLIENT_TYPE.THIRDPARTYDISPATCHERCLIENT.value())))
                                ||
                                (activateMdnInfoDTO.getSubsClientType().equals(KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value()) &&
                                        (subsProfileInfoDTO.getSubsClientType().equals(KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value())) ||
                                        (subsProfileInfoDTO.getSubsClientType().equals(KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value()))))) {
                    knLogger.error(methodName, "unauthorized Subscriber client used for activate ");
                    throw new KnProvBOException(KnErrorCodes.BOEntity.UNAUTHORIZED_SUBS_CLIENT_TYPE, "Un-authorized client type");
                }
            }


                // added to retrieve KnPOCSvcConfigDTO to fetch dynamicQosFlag from it.
                String pocPttServerId = subsProfileInfoDTO.getPoCHome();
                KnPOCSvcConfigDTO knPOCSvcConfigDTO = provInfoUtil.retrievePOCSvcConfig(pocPttServerId);

            //generate the HA1 password
//            String realm = activateMdnInfoDTO.getRealm();
            // retrieving the realm value from the configured DB value
            KnXDMSServiceConfigDTO xdmsServiceConfigDTO = genInfoUtil.retrieveXDMSServiceConfig(xdmPttServerId, persisterTxn);
            String realm = xdmsServiceConfigDTO.getAuthRealm();
            String password = activateMdnInfoDTO.getAuthDTO().getPassword();
            if (realm != null) {
                String clientPassword = provInfoUtil.generateHA1(mdn, realm, password);
                subsProfilePersistDTO.setClientPassword(clientPassword);
            }

            String userAgent = activateMdnInfoDTO.getUserAgent();
            subsProfilePersistDTO.setUserAgent(userAgent);

            String clientStr = ""+SUBS_CLIENT_TYPE.HANDSET.value()+"|"+SUBS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value()+","+SUBS_CLIENT_TYPE.CROSSCARRIER.value()+"|"+SUBS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value()+","+SUBS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value()+"|"+SUBS_CLIENT_TYPE.POC_WIFIONLY.value()+"";
            knLogger.debug(methodName,"LicenseType= ",subsProfileInfoDTO.getLicenseType());
            if(subsProfileInfoDTO.getLicenseType() == ENABLED){
                if(subsProfileInfoDTO.getSubsClientType() != activateMdnInfoDTO.getSubsClientType()){
                    String[] regExArray = clientStr.split(",");
                    String[] clientArray;
                    List<Integer> standardClients = Arrays.asList(SUBS_CLIENT_TYPE.HANDSET.value(), SUBS_CLIENT_TYPE.POC_WIFIONLY.value(), SUBS_CLIENT_TYPE.CROSSCARRIER.value());
                    List<Integer> pttClients = Arrays.asList(SUBS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value(), SUBS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value(), SUBS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value());
                    if(standardClients.contains(subsProfileInfoDTO.getSubsClientType()) && standardClients.contains(activateMdnInfoDTO.getSubsClientType())){
                        subsProfilePersistDTO.setSubsClientType(activateMdnInfoDTO.getSubsClientType());
                        subsProfileInfoDTO.setSubsClientType(activateMdnInfoDTO.getSubsClientType());
                    }
                    else if(standardClients.contains(subsProfileInfoDTO.getSubsClientType()) && pttClients.contains(activateMdnInfoDTO.getSubsClientType())){
                        subsProfilePersistDTO.setSubsClientType(activateMdnInfoDTO.getSubsClientType());
                        subsProfileInfoDTO.setSubsClientType(activateMdnInfoDTO.getSubsClientType());
                    }
                    else if(pttClients.contains(subsProfileInfoDTO.getSubsClientType()) && pttClients.contains(activateMdnInfoDTO.getSubsClientType())){
                        subsProfilePersistDTO.setSubsClientType(activateMdnInfoDTO.getSubsClientType());
                        subsProfileInfoDTO.setSubsClientType(activateMdnInfoDTO.getSubsClientType());
                    }
                    else if(pttClients.contains(subsProfileInfoDTO.getSubsClientType()) && standardClients.contains(activateMdnInfoDTO.getSubsClientType())){
                        if(subsProfileInfoDTO.getSubsClientType() == SUBS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value()){
                            if(activateMdnInfoDTO.getSubsClientType() == SUBS_CLIENT_TYPE.POC_WIFIONLY.value()){
                                subsProfilePersistDTO.setSubsClientType(SUBS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value());
                                subsProfileInfoDTO.setSubsClientType(SUBS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value());
                            }
                            else if(activateMdnInfoDTO.getSubsClientType() == SUBS_CLIENT_TYPE.CROSSCARRIER.value()){
                                subsProfilePersistDTO.setSubsClientType(SUBS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value());
                                subsProfileInfoDTO.setSubsClientType(SUBS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value());
                            }
                        }
                        else if(subsProfileInfoDTO.getSubsClientType() == SUBS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value()){
                            if(activateMdnInfoDTO.getSubsClientType() == SUBS_CLIENT_TYPE.HANDSET.value()){
                                subsProfilePersistDTO.setSubsClientType(SUBS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value());
                                subsProfileInfoDTO.setSubsClientType(SUBS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value());
                            }
                            else if(activateMdnInfoDTO.getSubsClientType() == SUBS_CLIENT_TYPE.CROSSCARRIER.value()){
                                subsProfilePersistDTO.setSubsClientType(SUBS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value());
                                subsProfileInfoDTO.setSubsClientType(SUBS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value());
                            }
                        }

                        else if(subsProfileInfoDTO.getSubsClientType() == SUBS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value()){
                            if(activateMdnInfoDTO.getSubsClientType() == SUBS_CLIENT_TYPE.HANDSET.value()){
                                subsProfilePersistDTO.setSubsClientType(SUBS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value());
                                subsProfileInfoDTO.setSubsClientType(SUBS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value());
                            }
                            else if(activateMdnInfoDTO.getSubsClientType() == SUBS_CLIENT_TYPE.POC_WIFIONLY.value()){
                                subsProfilePersistDTO.setSubsClientType(SUBS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value());
                                subsProfileInfoDTO.setSubsClientType(SUBS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value());
                            }
                        }
                        for(int i =0 ; i<regExArray.length ; i+=2){
                            clientArray = Arrays.stream(regExArray).flatMap(s -> Arrays.stream(s.split("\\|"))).toArray(String[] :: new);
                            if(activateMdnInfoDTO.getSubsClientType() == Integer.parseInt(clientArray[0])) {
                                subsProfilePersistDTO.setSubsClientType(Integer.parseInt(clientArray[1]));
                                break;
                            }
                        }
                    }
                    else{
                        knLogger.debug(methodName,"INVALID CLIENT TYPE");
                        throw new KnProvBOException(KnErrorCodes.BOEntity.UNAUTHORIZED_SUBS_CLIENT_TYPE,"Un-authorized client type");
                    }
                }
            }
            //added for backward compatibility
            if (userAgentDTO != null && !(userAgentDTO.getProtocolVersion().split("\\.")[0].matches(com.kodiak.common.resources.KnConstants.PROTOCOL_VERSION_REGEX))) {
                if (activateMdnInfoDTO.getSubsClientType() != null && !(activateMdnInfoDTO.getSubsClientType().equals(subsProfileInfoDTO.getSubsClientType()) &&
                        (activateMdnInfoDTO.getSubsClientType().equals(KnProvConstants.SUBS_CLIENT_TYPE.HANDSET.value())))) {
                    knLogger.error(methodName, "Unauthorized Subscriber client used for activate...(backward compatibility)");
                    throw new KnProvBOException(KnErrorCodes.BOEntity.UNAUTHORIZED_SUBS_CLIENT_TYPE, "Un-authorized client type");
                }
            }

            //Reject request for client if app name is not knwds
            if (subsProfileInfoDTO.getDispatchType() == com.kodiak.common.resources.KnConstants.DISPATCH_TYPE){
                if (!(userAgentDTO.getAppName().equals(KnProvConstants.KNWDS))){
                    throw new KnProvBOException(KnErrorCodes.BOEntity.INVALID_CLIENT, "Invalid client for dispatch subscriber app name not matching");
                }
            }

            if(clientPVMajorVersion >= KnConstants.PROTOCOL_VERSION_13_X){
                knLogger.info(methodName, "PV 13 + clients resetting the client type");
                int clientType = provInfoUtil.computeClientType(subsProfileInfoDTO.getSubsClientType(), activateMdnInfoDTO.getClientType());
                subsProfilePersistDTO.setSubsClientType(clientType);
                activateMdnInfoDTO.setClientType(clientType);
            }else{
                subsProfilePersistDTO.setSubsClientType(subsProfileInfoDTO.getSubsClientType());
            }

            boolean supportedDevice = provInfoUtil.isSupportedDevice(userAgentDTO, xdmPttServerId, persisterTxn, subsProfileInfoDTO);

            if (!supportedDevice) {
                knLogger.error(methodName, "BLOCK_UNSUPPORTED_DEVICES is enabled and device not supported");
                throw new KnProvBOException(KnErrorCodes.BOEntity.UNSUPPORTED_DEVICE, "UNSUPPORTED DEVICE");
            }
            boolean blackListDevice = provInfoUtil.isBlackListDevice(userAgentDTO, xdmPttServerId, persisterTxn);
            if (blackListDevice) {
                knLogger.error(methodName, "Device is Black listed");
                throw new KnProvBOException(KnErrorCodes.BOEntity.BLACKLISTED_DEVICE, "BLACKLISTED DEVICE");
            }



            String subsFS2 = generateSubsFS2(subsProfileInfoDTO, persisterTxn);
            String xdmsFs2=subsProfileInfoDTO.getXdmsFS2();
            int newDispatchGrpMember = subsProfileInfoDTO.getDispatchGroupMember();
            String xdmCorpFS2Set = genInfoUtil.selectXDMCorpFS(subsProfileInfoDTO.getCorpId(), true, persisterTxn);
            boolean corpLevelLocationFlag = false;
            if(null != xdmCorpFS2Set) {
                corpLevelLocationFlag = KnGeneralUtil.getFeatureBitValue(xdmCorpFS2Set, XDMCORPFS2_SET.LOCATION_ENABLED.value());
            }
            if (newDispatchGrpMember == KnConstants.BIT_TRUE || corpLevelLocationFlag) {
            	xdmsFs2 = featureSetUtil.getSetFeatureSetBits(xdmsFs2, new int[]{com.kodiak.common.resources.KnConstants.FEATURE_SET.ONDEMLOCATION.value()});
            	knLogger.debug(methodName, "on demand location bit set in xdmsFs:", xdmsFs2);
            } else {
            	xdmsFs2 = featureSetUtil.getClearFeatureSetBits(xdmsFs2, new int[]{com.kodiak.common.resources.KnConstants.FEATURE_SET.ONDEMLOCATION.value()});
            	knLogger.debug(methodName, "on demand location bit clear in xdmsFs:", xdmsFs2);
            }

            //SDD-POC-Enhancement For Supervisor Location Subscription: 9.1.1: RQPOC_SUPERVISOR_LOC_SUBSCRPTN_1
            if(subsProfileInfoDTO.getSubsClientType() == SUBSCRIBERS_CLIENT_TYPE.DISPATCH.value()
                    || subsProfileInfoDTO.getSubsClientType() == SUBSCRIBERS_CLIENT_TYPE.THIRDPARTYDISPATCHERCLIENT.value()){
                xdmsFs2 = featureSetUtil.getSetFeatureSetBits(xdmsFs2, new int[]{com.kodiak.common.resources.KnConstants.FEATURE_SET.LOCATIONSUBSCRIPTION.value()});
                knLogger.debug(methodName, "Location Subscription bit set in xdmsFs:", xdmsFs2);
            }
            // Internet APN changes ...
            String apnName = activateMdnInfoDTO.getApnName();
            knLogger.info(methodName, "APN NAME as of request   ", apnName);
            if (apnName == null || apnName.equalsIgnoreCase("UNKNOWN")) {
                apnName = genInfoUtil.getDefaultAPNName(persisterTxn);
            }
            // verify apn exists or not ..
            Integer apnId = genInfoUtil.getAPNId(apnName, persisterTxn);
            knLogger.info(methodName, " APNID ", apnId, "APNNAME:", apnName);
            if (apnId == null) {
                knLogger.error(methodName, " APN not exists , APN_INFO_NOT_FOUND ");
                throw new KnProvBOException(KnErrorCodes.BOEntity.APN_INFO_NOT_FOUND, " APN_INFO_NOT_FOUND ");
            }

            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            //Get All profile MDN' for given Real MDN
            List<String> allMdnList = getMdnForUPM(mdn, persisterTxn);
            boolean isDirecEtagUpdateRequired = false;
            if (!allMdnList.isEmpty()) {
                ArrayList<String> mdnList = new ArrayList();
                mdnList.add(mdn);
                Map<String, Integer> apnIdMap = provXDMServerDAO.selectSubApn(mdnList, true, persisterTxn);
                //Before updating checking DB APNID and Request APN ID if not same update
                if (null != apnIdMap.get(mdn) && !Objects.equals(apnIdMap.get(mdn), apnId)) {
                    isDirecEtagUpdateRequired = true;
                    provXDMServerDAO.updateSubApnIdForMdnList(allMdnList, apnId, false, persisterTxn);
                }
            }

            KnAPNProfileInfoDTO apnProfileInfoDTO = xdmDAO.retrieveAPNProfileInfo(xdmPttServerId, apnId, persisterTxn);
            knLogger.debug(methodName,"apnProfileInfoDTO ",apnProfileInfoDTO);
            KnXDMSServiceConfigDTO xdmServiceConfig=xdmDAO.retrieveXDMSServiceConfig(persisterTxn);
            knLogger.debug(methodName,"xdmServiceConfig ",xdmServiceConfig);

            String clientFeatureSet="0"; //assign  0 to reset to 7.1
            //added for backward compatibility ( will be null from request)
            if (activateMdnInfoDTO.getClientFS2() != null) {
                clientFeatureSet = activateMdnInfoDTO.getClientFS2();
            }
            int subReqclientType = activateMdnInfoDTO.getSubsClientType();
            //Request subscriber client type will always be 14 for PTT clients
            if (subReqclientType == KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value()) {
                //enabling 44th bit for PTT client
                clientFeatureSet = featureSetUtil.getSetFeatureSetBits(clientFeatureSet, new int[]{com.kodiak.common.resources.KnConstants.FEATURE_SET.PTTRADIOCLIENT.value()});
            }
            String clientCapOverrideBitMask = featureSetUtil.getClientCapabilityBitMask(xdmPttServerId, clientPVMajorVersion);
            String clientFS2 = featureSetUtil.generateClientFeatureSet(clientFeatureSet,clientCapOverrideBitMask);
            boolean pvUpmDisabled = (clientPVMajorVersion == PROTOCOL_VERSION_18_X
                    || clientPVMajorVersion == PROTOCOL_VERSION_19_X
                    || clientPVMajorVersion == PROTOCOL_VERSION_20_X);
            boolean clientUpmDisabled=(dbClientType.equals(KnProvConstants.SUBS_CLIENT_TYPE.HANDSET.value())
                    ||dbClientType.equals(KnProvConstants.SUBS_CLIENT_TYPE.POC_WIFIONLY.value())
                    ||dbClientType.equals(KnProvConstants.SUBS_CLIENT_TYPE.CROSS_CARRIER_PTT_CLIENT.value()));
            knLogger.debug(methodName," pvUpmDisabled :",pvUpmDisabled," clientUpmDisabled :",clientUpmDisabled," dbClientType:",dbClientType);
            if(pvUpmDisabled&&clientUpmDisabled){
                //disabling 76th bit for 1,5,10 client
                knLogger.debug(methodName," clientFS2 before disabling 76 bit for handset clients :",clientFS2);
                clientFS2 = featureSetUtil.getClearFeatureSetBits(clientFS2, new int[]{com.kodiak.common.resources.KnConstants.FEATURE_SET.USER_PROFILE_MGMT_BIT.value()});
                knLogger.debug(methodName," clientFS2 after disabling 76 bit for handset clients :",clientFS2);
            }
            boolean talkGrpSelClient = featureSetUtil.getFeatureBitValue(clientFS2, com.kodiak.common.resources.KnConstants.FEATURE_SET.TLKGRPSELCLIENT.value());
            boolean talkGrpScanClient = featureSetUtil.getFeatureBitValue(clientFS2, com.kodiak.common.resources.KnConstants.FEATURE_SET.TLKGRPSCANCLIENT.value());
            boolean pttRadioClient = featureSetUtil.getFeatureBitValue(clientFS2, com.kodiak.common.resources.KnConstants.FEATURE_SET.PTTRADIOCLIENT.value());
            int corpSubscriptionType = subsProfileInfoDTO.getCorporateSubscriptionType();
            int clientType = subsProfileInfoDTO.getSubsClientType();

            //checking pttradioclient feature bit for ptt radio clients with service name as PoCService
            if (activateMdnInfoDTO.getSubsClientType().equals(KnProvConstants.SUBS_CLIENT_TYPE.HANDSET.value())){
                if ((subsProfileInfoDTO.getSubsClientType().equals(KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value()) ||
                        (subsProfileInfoDTO.getSubsClientType().equals(KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value())) ||
                        (subsProfileInfoDTO.getSubsClientType().equals(KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value())))){
                    if (!pttRadioClient){
                        knLogger.error(methodName,"PTT Radio Client bit 44 is disabled hence invalidating request ");
                        throw new KnProvBOException(KnErrorCodes.BOEntity.PTTRADIOCLIENTBIT_DISABLED, "PTT radio client feature bit disabled");                    }
                }
            }

            if (!(clientType == KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value() || clientType == KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value() || clientType == KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value())) {
            //When talkGrpSelClient bit is disable in clientFS then enabling talkgrpserver bit of subsFS for
            // corporate subs and which are not of clienttype pocdonor or dispatch
            if ((!(talkGrpSelClient || talkGrpScanClient)) && corpSubscriptionType == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value() && !(clientType == KnProvConstants.SUBS_CLIENT_TYPE.DISPATCH_CLIENT.value() || clientType == KnProvConstants.SUBS_CLIENT_TYPE.POC_DONOR_RADIO.value())) {
                knLogger.debug(methodName, "setting talk grp server bit since client bit is disable in clientFS ");
                clientFS2 = featureSetUtil.getSetFeatureSetBits(clientFS2, new int[]{com.kodiak.common.resources.KnConstants.FEATURE_SET.TLKGRPSELSERVER.value()});
            } else {
                knLogger.debug(methodName, "talkGrpSelClient is enabled or dispatch/interop clienttype. clearing talk grp server bit in clientFS");
                clientFS2 = featureSetUtil.getClearFeatureSetBits(clientFS2, new int[]{com.kodiak.common.resources.KnConstants.FEATURE_SET.TLKGRPSELSERVER.value()});
            }
            } else {
                // talkGrpScanClient bit is disabled and talkGrpScanSrv bit should be enabled.
            	clientFS2 = featureSetUtil.getSetFeatureSetBits(clientFS2, new int[]{com.kodiak.common.resources.KnConstants.FEATURE_SET.TLKGRPSELSERVER.value()});
            	clientFS2 = featureSetUtil.getClearFeatureSetBits(clientFS2, new int[]{com.kodiak.common.resources.KnConstants.FEATURE_SET.TLKGRPSELCLIENT.value()});
            	clientFS2 = featureSetUtil.getClearFeatureSetBits(clientFS2, new int[]{com.kodiak.common.resources.KnConstants.FEATURE_SET.TLKGRPSCANCLIENT.value()});
                knLogger.debug(methodName, "setting talk grp server bit since client bit is disable in clientFS & clearing talk grp server bit in clientFS fpr PTT Radio clients :",clientFS2);
            }

            Map<String, String> paramNameValueMap = genInfoUtil.retrieveRTXConfigValues(persisterTxn);


            //setting vocoderId for PV 9 and less then PV 13
            if (clientPVMajorVersion < KnConstants.PROTOCOL_VERSION_13_X) {
                if (clientPVMajorVersion >= KnConstants.PROTOCOL_VERSION_9_X) {
                    List<KnClientVocoderProfilePersistDTO> clientVocoderProfilePersistDTOs = new ArrayList<>();
                    Map<Integer, Integer> vocoderIdMap1 = activateMdnInfoDTO.getVocoderIdMap();
                    for (Integer priority : vocoderIdMap1.keySet()) {
                        KnClientVocoderProfilePersistDTO clientVocoderProfilePersistDTO = new KnClientVocoderProfilePersistDTO();
                        clientVocoderProfilePersistDTO.setVocoderId(vocoderIdMap1.get(priority));
                        clientVocoderProfilePersistDTO.setPriority(priority);
                        clientVocoderProfilePersistDTO.setMdn(activateMdnInfoDTO.getMdn());
                        clientVocoderProfilePersistDTOs.add(clientVocoderProfilePersistDTO);
                    }
                    int vocoderId = createClientSuppVocoder(clientVocoderProfilePersistDTOs, persisterTxn);
                    subsProfilePersistDTO.setVocoderId(vocoderId);
                } else if (activateMdnInfoDTO.getPlatformType() == 2) {
                    if (null == paramNameValueMap.get(KnConstants.DEFAULT_WEBRTC_VOCODER_PROFILEID)) {
                        throw new KnProvBOException(KnErrorCodes.BOEntity.SERVER_CONFIGURATION_FAILURE, "DEFAULT_WEBRTC_VOCODER_PROFILEID parameter not found");
                    }
                    String paramValue = (String) paramNameValueMap.get(KnConstants.DEFAULT_WEBRTC_VOCODER_PROFILEID);
                    subsProfilePersistDTO.setVocoderId(Integer.parseInt(paramValue));
                    knLogger.debug(methodName, "setting vocoder from rtx for webRTC client :", paramValue);

                } else if (clientPVMajorVersion < KnConstants.PROTOCOL_VERSION_9_X && "1".equals(paramNameValueMap.get(KnConstants.SPECIFIC_CODEC_SUPPPORT_OLDER_BREW_CLIENTS))
                        && userAgent.contains(paramNameValueMap.get(KnConstants.OLDER_CLIENTS_CODEC_SUPPORTED_BREW_UA))) {
                    knLogger.debug(methodName, "Older brew client");
                    subsProfilePersistDTO.setVocoderId(KnConstants.AMR_HALF_RATE_PROFILEID);
                } else {
                    Map<Integer, KnSuppVocoderProfileDTO> vocoderIdMap = genInfoUtil.retrieveSuppVocoderProfile(persisterTxn);
                    //vocoder id is changed to profile id variable name kept same.
                    for (Integer vocoder : vocoderIdMap.keySet()) {
                        if (vocoderIdMap.get(vocoder).getIsDefault() == 1) {
                            subsProfilePersistDTO.setVocoderId(vocoder);
                            break;
                        }
                    }
                }
            }

            String opsFS2 = subsProfileInfoDTO.getOpsFS2();
            String pocPttId = subsProfileInfoDTO.getPoCHome();
            String presencePttId = subsProfileInfoDTO.getPresenceHome();
            String xdmsPttId = subsProfileInfoDTO.getXDMSHome();
            String corpFS2=null;
            String activeFS2=null;
            String corpAdminFS2 = subsProfileInfoDTO.getCorpAdminFS2();
            String existingActiveFS2 = subsProfileInfoDTO.getActiveFS2();
            String userProfileFs=subsProfileInfoDTO.getUserProfileFS2();
            boolean cleanUpTGSData = false;
           // retriev the APN COntiguration paramters
             KnAPNConfigDTO knAPNConfigDTO = genInfoUtil.retrieveAPNInfoConfig(persisterTxn, apnId);
            int apnDynamicQoflag= knAPNConfigDTO.getDynamicQosFlag().intValue();
            int subsProInfoDynamicQosFlag;
            int svcDynamicQosFlag= knPOCSvcConfigDTO.getDynamicQosFlag();
            int corpDynamicQosFlag;
            //
            knLogger.debug(methodName, "clientPVMajorVersion ", clientPVMajorVersion, " ,clientCapOverrideBitMask=", clientCapOverrideBitMask);
            knLogger.debug(methodName, "userProfileFs ", userProfileFs);


            IProvXDMServerDAO provXdmServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            //generating the Active FS
            if (subsProfileInfoDTO.getCorporateSubscriptionType() == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
                String extCorpId = provXdmServerDAO.retrieveExtCorporationId(subsProfileInfoDTO.getCorpId(), persisterTxn);
                KnOPCorpProfileInfoDTO corpProfileInfoDTO = provInfoUtil.retrieveCorpProfile(extCorpId, persisterTxn);
                corpFS2 = corpProfileInfoDTO.getCorpFS2();
                corpDynamicQosFlag = corpProfileInfoDTO.getDynamicQosFlag();
                //ANDing the SVC,APN,Corp DynamicQosFlag and setting it to subsProfileInfoDTO
                subsProInfoDynamicQosFlag=(svcDynamicQosFlag & corpDynamicQosFlag & apnDynamicQoflag);
                activeFS2 = featureSetUtil.generateActiveFeatBitSet(pocPttId, presencePttId, xdmsPttId, clientFS2, subsFS2, corpFS2, opsFS2, corpAdminFS2, clientCapOverrideBitMask,xdmsFs2,userProfileFs);
            } else {
                //If the non-corporate ANDing only  SVC and APN
                subsProInfoDynamicQosFlag=(svcDynamicQosFlag &  apnDynamicQoflag);
                activeFS2 = featureSetUtil.generateActiveFeatBitSet(pocPttId, presencePttId, xdmsPttId, clientFS2, subsFS2, opsFS2, clientCapOverrideBitMask,xdmsFs2,userProfileFs);
            }

            if (!activeFS2.equals(existingActiveFS2)) {
                cleanUpTGSData = cleanUpTGSData(activeFS2, existingActiveFS2);
                setMCPTTFeatureDisable(mdn, activeFS2, persisterTxn, provXDMServerDAO);
                //adding to mcxGroup clean up job on 81 bit disable
                com.kodiak.xdms.server.common.util.KnGeneralUtil.veryLargeGroupBitChanged(existingActiveFS2
                        ,activeFS2
                        ,subsProfilePersistDTO.getMdn()
                        ,String.valueOf(subsProfilePersistDTO.getCorpId()),false);
            }
            boolean remotePushNotification = featureSetUtil.getFeatureBitValue(activeFS2, FEATURE_SET.REMOTEPUSHNOTIFICATION.value());
            boolean gcmPushNotify = featureSetUtil.getFeatureBitValue(activeFS2, FEATURE_SET.GCMPUSHNOTIFICATION.value());
            if (remotePushNotification || gcmPushNotify){
                byte[] salt = mdn.getBytes();
                byte[] encryptedPwd = provInfoUtil.getEncryptedPassword(subsProfilePersistDTO.getClientPassword(),salt,KnConstants.ITERATIONS_10_000,KnConstants.DERIVEDKEYLENGTH);
                String derivedKey = provInfoUtil.convertToHex(encryptedPwd);
                knLogger.debug(methodName,"Derived key after generation and convertion: ",derivedKey);
                subsProfilePersistDTO.setDerivedKey(KnGeneralUtil.convertHexToAscii(derivedKey));
            }
            knLogger.debug(methodName, "cleanUp TGS ", cleanUpTGSData);
            subsProfilePersistDTO.setSubsFS2(subsFS2);
            subsProfilePersistDTO.setClientFS2(clientFS2);
            subsProfilePersistDTO.setActiveFS2(activeFS2);
            subsProfilePersistDTO.setOpsFS2(opsFS2);
            subsProfilePersistDTO.setCorpAdminFS2(corpAdminFS2);
            subsProfilePersistDTO.setXdmsFS2(xdmsFs2);
            subsProfilePersistDTO.setDynamicQosFlag(subsProInfoDynamicQosFlag);

            //TODO: Supported Devices Check is required.

            long lastProfileUpdateTime = Calendar.getInstance().getTimeInMillis();
            subsProfilePersistDTO.setLastProfileUpdateTime(lastProfileUpdateTime);
            subsProfilePersistDTO.setInputDTO(activateMdnInfoDTO);

            //Invoking validation FW
            validatorFwk.validate(subsProfilePersistDTO);
            knLogger.debug(methodName, "Successfully Validated the data ");

            //setting the service auth status to activate
            subsProfilePersistDTO.setServiceAuthStatus(KnConstants.SERVICE_AUTH_STATUS.ACTIVATED.value());
            subsProfilePersistDTO.setServiceStatusOp(KnConstants.SERVICE_AUTH_STATUS.ACTIVATED.value());

            int clusterId = Integer.parseInt(System.getenv(com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME));
            String deviceSharingFlag = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn).get(KnConstants.DEVICE_SHARING_FEATURE_FLAG);

            int deviceSharing = 0;
            if(deviceSharingFlag != null) deviceSharing = Integer.parseInt(deviceSharingFlag);
            int deviceSharewifiFlag=Integer.parseInt(genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn).get(KnConstants.AUTO_DEVICESHARE_WIFI_CC));
            knLogger.debug("deviceSharewifiFlag-->"+deviceSharewifiFlag);
            if(subsProfilePersistDTO.getClientPVmajorVer() != subsProfileInfoDTO.getClientPVmajorVer() && subsProfilePersistDTO.getClientPVmajorVer() >= PROTOCOL_VERSION_14
                    && subsProfileInfoDTO.getLicenseType() != ENABLED && deviceSharing == ENABLED && (String.valueOf(subsProfileInfoDTO.getSubsClientType()).matches(CLIENT_TYPE_WIFI_CROSS_NORMAL_RADIO))
                    && deviceSharewifiFlag== ENABLED){
                subsProfilePersistDTO.setLicenseType(ENABLED);
            }else{
                knLogger.debug("setting the else val-->"+subsProfileInfoDTO.getLicenseType());
                subsProfilePersistDTO.setLicenseType(subsProfileInfoDTO.getLicenseType());
            }
            //Privacy Opt Status starts
            ////
            KnOPSubsProfileInfoDTO existingProfileInfoDTO=null;
            Integer privacyAmbDiscListenFromXDMS_SVC_CONFIG=null;

                xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
                knLogger.debug( methodName, "xdmPttServerId::"+xdmPttServerId);
                // retrieving the Subscriber Info
                provXdmServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB)
                        .createProvXDMServerDAO();

                // we have mdn here already from request
                // existingProfileInfoDTO = provInfoUtil.retrieveSubscriberInfoUU(mdn, persisterTxn);
                knLogger.debug( methodName, "--->mdn::"+KnGDPRTemplate.mdn(mdn));

                existingProfileInfoDTO = provInfoUtil.retrieveSubscriberInfo(mdn, persisterTxn);
                knLogger.info(methodName, "--->existingProfileInfoDTO :",existingProfileInfoDTO);

                knLogger.debug( methodName, "ENTERS VALIDATION_1: ");

                //boolean privacyAmbDiscListenFlag = false;

                Integer privacyAmbDiscListen;
                //if the subscriber is a corp or corp-public type then we will retirve the extcorpid of the corp
                int corpIdFromSubscr = existingProfileInfoDTO.getCorpId();
                knLogger.debug( methodName, "--->corpIdFromSubscr::"+corpIdFromSubscr);
                if (corpIdFromSubscr != 0) {
                    knLogger.debug(methodName, "--->Subscriber is corp or corp-public type,Retrive the extCorpId for the corp=", corpIdFromSubscr);
                    //try {
                    privacyAmbDiscListen = provXdmServerDAO.retrievePrivacyAmbDiscListenFlag(corpIdFromSubscr, persisterTxn);
                    knLogger.debug( methodName, "--->privacyAmbDiscListen::"+privacyAmbDiscListen);
                    if(privacyAmbDiscListen==null)
                    {
                        //try {
                        KnXDMSServiceConfigDTO xdmsServiceConfigsDTO = genInfoUtil.retrieveXDMSServiceConfig(xdmPttServerId, persisterTxn);
                        knLogger.debug( methodName, "xdmsServiceConfigsDTO::"+xdmsServiceConfigsDTO+" xdmPttServerId::"+xdmPttServerId);

                        privacyAmbDiscListenFromXDMS_SVC_CONFIG= xdmsServiceConfigsDTO.getPrivacyAmbDiscListen();
                        knLogger.debug( methodName, "privacyAmbDiscListenFromXDMS_SVC_CONFIG::"+privacyAmbDiscListenFromXDMS_SVC_CONFIG);
                        privacyFlagToExecute=privacyAmbDiscListenFromXDMS_SVC_CONFIG;

                      /*  if(privacyAmbDiscListenFromXDMS_SVC_CONFIG!=null && privacyAmbDiscListenFromXDMS_SVC_CONFIG==1)
                        {
                         /////////////////PERFORM THE REQUIRED OPERATION HERE
                            performPrivacyOperation=true;
                        } */

                    }
                    else
                    {
                        //PERFORM THE REQUIRED OPERATION
                       // performPrivacyOperation=true;
                        privacyFlagToExecute=privacyAmbDiscListen;
                        knLogger.debug(methodName, "---> privacyFlagToExecute - ", privacyFlagToExecute + "--->performPrivacyOperation:"+performPrivacyOperation);
                    }



                }
                knLogger.debug(methodName, "--->performPrivacyOperation:"+performPrivacyOperation);
            ////
            knLogger.info(methodName, "--->subsProfilePersistDTO.getClientPVmajorVer():", subsProfilePersistDTO.getClientPVmajorVer());
            if(privacyFlagToExecute!=null && privacyFlagToExecute==1 && subsProfilePersistDTO.getClientPVmajorVer()>= PROTOCOL_VERSION_16) {
                boolean flagForAmbDis = false;
                // KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails("" + corpId, CORP_PROFILE, false, persisterTxn);
                //String xdmsHome = corpProfile.getXdmsHome();
                //  String xdmsHome=subsProfileInfoDTO.getXDMSHome();
                List<KnMCPTTPermInfoDTO> mcpttPermInfoDTOS = provXdmServerDAO.getMCPTTPermInfoForTargetMdn(mdn, persisterTxn);
                for (KnMCPTTPermInfoDTO permInfoDTO : mcpttPermInfoDTOS) {
                    knLogger.info(methodName, "permInfoDTO:", permInfoDTO);
                    Long permBitPrivacy = permInfoDTO.getPermBitset();
                    knLogger.info(methodName, "permBitPrivacy:", permBitPrivacy);
                    BitSet targetDBPermsBitForPrivacy = featureSetUtil.convertLongToBitSet(permBitPrivacy);
                    knLogger.info(methodName, "targetDBPermsBitForPrivacy:", targetDBPermsBitForPrivacy);
                    boolean ambientListenerBit = targetDBPermsBitForPrivacy.get(com.kodiak.common.resources.KnConstants.MCPTT_PERMISSION_BIT.AMBIENTLISTENING.value());
                    knLogger.info(methodName, "ambientListenerBit:", ambientListenerBit);

                    boolean discreetListenerBit = targetDBPermsBitForPrivacy.get(com.kodiak.common.resources.KnConstants.MCPTT_PERMISSION_BIT.DISCRETELISTENING.value());
                    knLogger.info(methodName, "discreetListenerBit:", discreetListenerBit);

                    if (ambientListenerBit == true || discreetListenerBit == true) {
                        flagForAmbDis = true;
                        knLogger.info(methodName, "--->if ambientListenerBit equals true or discreetListenerBit equals true");

                    }

                }


                knLogger.info(methodName, "--->Before: subsProfilePersistDTO:", subsProfilePersistDTO, "flagForAmbDis:", flagForAmbDis);
                subsProfilePersistDTO.setFlagForPrivacy(flagForAmbDis);
                knLogger.info(methodName, "--->After: subsProfilePersistDTO:", subsProfilePersistDTO, "flagForAmbDis:", flagForAmbDis);


            }
            //Privacy Opt Status ends
			subsProfilePersistDTO.setPrivacyExecutorBasedonFlag(privacyFlagToExecute);

			 int isAffiliationEnabled;
            boolean bitEnabled = KnGeneralUtil.getFeatureBitValue(subsProfilePersistDTO.getActiveFS2(), com.kodiak.common.resources.KnConstants.FEATURE_SET.AFFILIATIONFEATURE.value());
			if (subsProfilePersistDTO.getClientPVmajorVer() >= com.kodiak.xdms.server.common.resources.KnConstants.PROTOCOL_VERSION_18
					&& bitEnabled && checkClientType(subsProfilePersistDTO.getSubsClientType(),subsProfileInfoDTO.getMcpttCompliance())) 
			{
                isAffiliationEnabled = ENABLED;
            } else {
                isAffiliationEnabled = DISABLED;
            }

            IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
            if (isAffiliationEnabled != KnProvUtil.getBitValueInInt(subsProfileInfoDTO.getActiveFS2(), com.kodiak.common.resources.KnConstants.FEATURE_SET.AFFILIATIONFEATURE.value())) {
                xdmServerDAO.updateAffForCorpGrpMemList(subsProfilePersistDTO.getMdn(), isAffiliationEnabled, persisterTxn);
            }

            boolean isSubsrUpdateRequired = KnProvUtil.isDBUpdateRequired(subsProfilePersistDTO, subsProfileInfoDTO);
            if (isSubsrUpdateRequired) {
                isDirecEtagUpdateRequired = true;
                provXdmServerDAO.activateSubscriber(subsProfilePersistDTO, persisterTxn);
            }
            knLogger.debug(methodName, "Successfully activated the subscriber");

            //updating fileds for profileMdn.
            Map<String, KnOPSubsProfileInfoDTO> mdnUpmFsMap = provXdmServerDAO.getProfileMdnNupmfsByBaseMdn(mdn, false, persisterTxn);
            mdnUpmFsMap.remove(mdn);
            Map<String, String> profileMdnActivsFsMap=null;
			if (mdnUpmFsMap.size() > 0) {

                //MINT-24505:
                String corpAdminFS2ForProfile = KnGeneralUtil.getProfileMdnCorpAdminFS(corpAdminFS2);
			    profileMdnActivsFsMap = provInfoUtil.calculateActiveFS2WithUPMFS(
						subsProfileInfoDTO.getCorporateSubscriptionType(), pocPttId, presencePttId, xdmsPttId,
						clientFS2, subsFS2, corpFS2, opsFS2, corpAdminFS2ForProfile, clientCapOverrideBitMask,
						mdnUpmFsMap);
                Collection<KnSubsProfileDTO> subsSpecificDetails = provXdmServerDAO
                        .fetchSubsSpecificDetailsForBulkMdns(new ArrayList<>(profileMdnActivsFsMap.keySet()), false, persisterTxn);
                Map<String, String> mdnUpmIdMap =new HashMap<>();
                Map<String, String> mdnActiveFsMap =new HashMap<>();
                for(KnSubsProfileDTO subsInfo:subsSpecificDetails){
                    mdnUpmIdMap.put(subsInfo.getMdn(),subsInfo.getUserProfileId());
                    mdnActiveFsMap.put(subsInfo.getMdn(),subsInfo.getActiveFS2());
                }
                Map<String,List<Integer>> upmIdGroupMap=provInfoUtil.retriveUpmIdGroupMapByUserProfileId(new ArrayList<>(mdnUpmIdMap.values()),persisterTxn);
                knLogger.debug(methodName," mdnUpmIdMap :",mdnUpmIdMap," existingActiveFS2 :",mdnActiveFsMap);
                knLogger.debug(methodName," profileMdnActivsFsMap :",profileMdnActivsFsMap);

			    subsProfilePersistDTO.setCorporateSubscriptionType(subsProfileInfoDTO.getCorporateSubscriptionType());
				subsProfilePersistDTO.setPublicSubscriptionType(subsProfileInfoDTO.getPublicSubscriptionType());
                //MINT-24505: set and reset corpAdminFS2
                subsProfilePersistDTO.setCorpAdminFS2(corpAdminFS2ForProfile);
				provXdmServerDAO.updateMdnFiledsNActiveFS(subsProfilePersistDTO, profileMdnActivsFsMap, persisterTxn);
                subsProfilePersistDTO.setCorpAdminFS2(corpAdminFS2);

				//Updating isAffiliationEnabled flag for Profile Mdns
				if (profileMdnActivsFsMap.size() > 0) {
					Map<String, String> mdnIsAffiliationEnabledMap = new HashMap<String, String>();
					for (Entry<String, String> mdnFsMap : profileMdnActivsFsMap.entrySet()) {

						Integer isAffiliationEnabledForProfileMdns;
						boolean bitEnabledForProfileMdns = KnGeneralUtil.getFeatureBitValue(
								mdnFsMap.getValue().toString(),
								com.kodiak.common.resources.KnConstants.FEATURE_SET.AFFILIATIONFEATURE.value());
						if (subsProfilePersistDTO
								.getClientPVmajorVer() >= com.kodiak.xdms.server.common.resources.KnConstants.PROTOCOL_VERSION_18
								&& bitEnabledForProfileMdns
								&& checkClientType(subsProfilePersistDTO.getSubsClientType(),
										subsProfileInfoDTO.getMcpttCompliance())) {
							isAffiliationEnabledForProfileMdns = ENABLED;
						} else {
							isAffiliationEnabledForProfileMdns = DISABLED;
						}
						mdnIsAffiliationEnabledMap.put(mdnFsMap.getKey().toString(),
								isAffiliationEnabledForProfileMdns.toString());
                        //mcxGroup clean job
                        String profileMdnActiveFs = mdnFsMap.getValue();
                        boolean veryLargeGroupBit = KnGeneralUtil.getFeatureBitValue(profileMdnActiveFs, VERY_LARGE_GROUP);
                        String profileMdn = mdnFsMap.getKey();
                        String upmId=mdnUpmIdMap.get(profileMdn);
                        String existingProfileMdnActiveFS2=mdnActiveFsMap.get(profileMdn);
                        List<Integer> mcxGroups = upmIdGroupMap.get(upmId);
                        knLogger.debug(methodName," profilemdn: ",profileMdn," mcxgroup veryLargeGroupBit:",veryLargeGroupBit," mcxGroups :",mcxGroups);
                        if(!veryLargeGroupBit&&mcxGroups!=null&&!mcxGroups.isEmpty()){
                            //adding to mcxGroup clean up job on profile mdn 81 bit disable
                            com.kodiak.xdms.server.common.util.KnGeneralUtil.veryLargeGroupBitChanged(existingProfileMdnActiveFS2
                                    ,profileMdnActiveFs
                                    ,profileMdn
                                    ,String.valueOf(subsProfilePersistDTO.getCorpId()),true);
                        }
					}
					xdmServerDAO.updateAffForCorpGrpMemList(mdnIsAffiliationEnabledMap, persisterTxn);
				}

			}
            
			

            //updating the XDM Dir doc
            xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
            if (isDirecEtagUpdateRequired) {
                xdmServerDAO.updateEtagForDirDoc(mdn, persisterTxn);
            }
            knLogger.debug(methodName, "Successfully updated the xdm directory");
            //updating the Corp Profile in case of corp subscriber
            if (corpSubscriptionType == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
                int corpId = subsProfileInfoDTO.getCorpId();
                provXdmServerDAO.updateCorpProfileLastUpdateTime(corpId, lastProfileUpdateTime, persisterTxn);
            }


            String cbPtxUri =null;
            String authServUri =null;
            String authServWifiUri = null;

            // fetch XDM service config for deriving the XCAPURI
            KnXDMSServiceConfigDTO xdmSvcConfigDTO = genInfoUtil.retrieveXDMSServiceConfig(xdmPttServerId, persisterTxn);

            // fetch the microservices cluster config for deriving the PTT and PTX bucket URIs
            KnMicroSvcsClusterInfo clusterInfo  = genInfoUtil.retrieveMSSvcsClusterConfig(clusterId, persisterTxn);

            // Fetch the microservices common config for deriving the xcap mobile sync flag
            Map<String, String> msCommonConfigMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);

            if (clusterInfo != null) {
                cbPtxUri = clusterInfo.getPtxBucketUriwifi();
                String xcapRootUri = genInfoUtil.getXCAPRootURI(mdn, persisterTxn);
                authServUri = xcapRootUri.replace(
                		com.kodiak.common.resources.KnConstants.XCAP_ROOT_CONTEXT,
                		com.kodiak.common.resources.KnConstants.MS_CBAUTH_ROOT_CONTEXT);
                if(clientPVMajorVersion >=  KnConstants.PROTOCOL_VERSION_13_X){
                    authServUri = xcapRootUri.replace(
                            com.kodiak.common.resources.KnConstants.OIDC_XCAP_ROOT_CONTEXT,
                            com.kodiak.common.resources.KnConstants.MS_CBAUTH_ROOT_CONTEXT);
                }
                authServWifiUri = xdmSvcConfigDTO.getXcapRootUri_Wifi().replace(
                        com.kodiak.common.resources.KnConstants.XCAP_ROOT_CONTEXT,
                        com.kodiak.common.resources.KnConstants.MS_CBAUTH_ROOT_CONTEXT);
            }

            //populating the activateSubscriber response DTO
            responseDTO = new KnOPActivationInfoDTO();

            //MC DEVICE
            boolean mcDeviceBit = KnGeneralUtil.getFeatureBitValue(activeFS2, FEATURE_SET.MCDEVICE.value());
            KnXDMDeviceProvDTO deviceProfile = xdmServerDAO.selectDeviceProfile(mdn, persisterTxn);
            knLogger.debug(methodName,"mcDeviceBit :",mcDeviceBit
                    ," deviceProfile :",deviceProfile);
            if(!mcDeviceBit
                    && deviceProfile!=null&&deviceProfile.getDeviceType()==DEVICE_TYPE.MC_DEVICE.Value()){
                KnDeviceInfoPersistDTO deviceInfoPersistDTO=new KnDeviceInfoPersistDTO();
                deviceInfoPersistDTO.setDeviceId(mdn);
                deviceInfoPersistDTO.setDeviceType(DEVICE_TYPE.RADIO_NEXT_DEVICE.Value());
                xdmServerDAO.updateDeviceType(deviceInfoPersistDTO, persisterTxn);
                //responseDTO.setDeleteDeviceNotify(true);
            }

            // if TP Account is not present for this mdn then set expery time otherwise set expery time as 1. more info refer 8.1 SDD part4 rqPOC_3rdParty_usecase_6
            KnTPUserAccountDTO tpUserAccountDTO = provXDMServerDAO.retrieveTPUserAccountForMDN(mdn, persisterTxn);
            if (tpUserAccountDTO == null) {
                responseDTO.setExpiryTime(genInfoUtil.getActivationCodeConfig(clientType,
                        com.kodiak.common.resources.KnConstants.CLIENT_INTF_CAT, persisterTxn).getActCodeExpiry());
            } else {
                responseDTO.setExpiryTime(genInfoUtil.getActivationCodeConfig(clientType,
                        com.kodiak.common.resources.KnConstants.CLIENT_INTF_REST, persisterTxn).getActCodeExpiry());
            }

            String jwtToken = null;
            knLogger.debug(methodName, "jwtUtil instance ", jwtUtil, "if not null generate the token");
            if(jwtUtil != null) {
                //rqPOC_MapServer_Deployment_19 : XDMS token generation
                KnTokenInfoDTO tokenInfoDTO = new KnTokenInfoDTO();
                tokenInfoDTO.setSubject(mdn);
                tokenInfoDTO.setUserName(mdn);
                tokenInfoDTO.setIssuer(JWT_ISSUER);
                tokenInfoDTO.setServiceType(JWT_SERVICE_TYPE);
                tokenInfoDTO.setExp(new Date(System.currentTimeMillis() + (jwtexpiryTime * MILLI_SECONDS)));
                jwtToken = jwtUtil.generateJWEToken(tokenInfoDTO);
                knLogger.debug(methodName, "token generated -", jwtToken);
            }

            responseDTO.setMdn(mdn);
            KnSubscrPTTRadioGrpListDocDAO pttRadioGrpListDocDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createPTTRadioGrpListDAO(xdmPttServerId);
            if (clientPVMajorVersion >= KnConstants.PROTOCOL_VERSION_13_X) {
                responseDTO.setXcapRootUri(genInfoUtil.getXCAPRootURI(mdn, persisterTxn));
                responseDTO.setXcapRootUri_Wifi(xdmSvcConfigDTO.getXcapRootUri_Wifi().replace(
                        com.kodiak.common.resources.KnConstants.XCAP_ROOT_CONTEXT,
                        com.kodiak.common.resources.KnConstants.OIDC_XCAP_ROOT_CONTEXT));
                KnDeviceInfoDAO deviceInfoDAO = KnProvTablesRegistry.getProvXDMTablesRegistry().createDeviceInfoDAO(xdmPttServerId);
                deviceInfoDAO.updateLastUsed(mdn, subsProfilePersistDTO.getLastProfileUpdateTime(), persisterTxn);
                //Insert into DG.SUBSCRPTTRADIOGROUPLISTDOC if not exist.
                if (!pttRadioGrpListDocDAO.isDocExist(persisterTxn, mdn)) {
                    knLogger.debug(methodName, "PTTRadioGroupListDoc not exist, creating the doc");
                    pttRadioGrpListDocDAO.insertTGLDoc(persisterTxn, mdn, (long) 1);
                }
            } else {
                responseDTO.setXcapRootUri(genInfoUtil.getXCAPRootURI(mdn, persisterTxn));
                responseDTO.setXcapRootUri_Wifi(xdmSvcConfigDTO.getXcapRootUri_Wifi());
                // Cleaning records for scanList and TGList:
                Collection<Integer> groupIds = pttRadioGrpListDocDAO.getSubsAddlTGList(mdn, persisterTxn);
                pttRadioGrpListDocDAO.deleteSubsAddlTalkGroupZone(mdn, persisterTxn);
                pttRadioGrpListDocDAO.deleteSubsAddlTalkGroupChannel(mdn, persisterTxn);
                pttRadioGrpListDocDAO.deleteScanList(mdn, groupIds, persisterTxn);
            }
            if( subsProfileInfoDTO.getLastActivationTime()==null ||  subsProfileInfoDTO.getLastActivationTime()==0L)
            {
            	knLogger.info(methodName, "last activation time : ",subsProfileInfoDTO.getLastActivationTime()," delete mdn ", KnGDPRTemplate.mdn(mdn)," from SUBSCRIBERADDLINFO" );
            	List<String> mdns= new ArrayList<>();
            	mdns.add(mdn);
            	KnGeneralCacheUtil.getInstance().deleteExpPasswordMDNs(mdns);
            	knLogger.info(methodName, "mdn : ",KnGDPRTemplate.mdnList(mdns)," deleted from SUBSCRIBERADDLINFO" );
            }
            responseDTO.setXui(provInfoUtil.generateXUI(mdn));

            if (subsProfilePersistDTO.getClientPVmajorVer() < PROTOCOL_VERSION_16) {
                String activefs1 = KnGeneralUtil.convertActiveFs2toHexActiveFs1(activeFS2);
                responseDTO.setActiveFS2(activefs1);
            } else {
                String activeFs2BasedOnPv = calculateActiveFeatureSetBasedOnPv(activeFS2, subsProfilePersistDTO.getClientPVmajorVer());
                responseDTO.setActiveFS2(activeFs2BasedOnPv);
            }

            responseDTO.setSubscriberFS2(subsFS2);
            responseDTO.setCleanUpTGSData(cleanUpTGSData);
            responseDTO.setPocHome(pocPttId);
            responseDTO.setPresHome(presencePttId);
            responseDTO.setOldActiveFS2(existingActiveFS2);
            responseDTO.setPvVersion(protocolVersion);

            // Internet APN changes ...

            //responseDTO.setXcapRootUri(xdmSvcConfigDTO.getXcapRootUri());
            responseDTO.setXui(provInfoUtil.generateXUI(mdn));
            responseDTO.setApnName(apnName);
            if (clientPVMajorVersion >= KnConstants.PROTOCOL_VERSION_18_X) {
                responseDTO.setMcsXcapRootUri(apnProfileInfoDTO.getMcsXcapRootUri()+MCSXCAP_XCAP_ROOT_CONTEXT);
                responseDTO.setMcsXcapRootUriWifi(xdmServiceConfig.getMcsXcapRootUriWifi()+MCSXCAP_XCAP_ROOT_CONTEXT);
                responseDTO.setKmsUri(apnProfileInfoDTO.getKmsUri());
                responseDTO.setKmsUriWifi(xdmServiceConfig.getKmsUriWifi());
                responseDTO.setMcsXui(subsProfileInfoDTO.getMcId());
            }

           // setting the required paramter for REST call
            responseDTO.setEmail(subsProfileInfoDTO.getEmailAddress());
            responseDTO.setPassword(subsProfilePersistDTO.getClientPassword());
            responseDTO.setSubscriberName(subsProfileInfoDTO.getNetworkName());
            responseDTO.setCorpId(subsProfileInfoDTO.getCorpId());

            //For PV 10 subscriber activation
            responseDTO.setSgwRootUriCellular(knAPNConfigDTO.getPtxBucketUri());
            responseDTO.setSgwRootUriWifi(cbPtxUri);
            responseDTO.setAuthUriCellular(authServUri);
            responseDTO.setAuthUriWifi(authServWifiUri);
            responseDTO.setCbBukInfo(msCommonConfigMap.get(com.kodiak.common.resources.KnConstants.SYNCBUCKETINFO));
            responseDTO.setSgwAuthMec(msCommonConfigMap.get(com.kodiak.common.resources.KnConstants.SGWAUTHMETHOD));
            responseDTO.setIpvc(paramNameValueMap.get(KnConstants.IP_VER_CELLULAR));
            responseDTO.setIpvcPrefC(paramNameValueMap.get(KnConstants.IP_PREF_ON_CELL_INTF));
            responseDTO.setIpvcPrefW(paramNameValueMap.get(KnConstants.IP_PREF_ON_WIFI_INTF));
            if (clientPVMajorVersion >= com.kodiak.common.resources.KnConstants.PROTOCOL_VERSION_29) {
                responseDTO.setIpvcMulti(paramNameValueMap.get(KnConstants.IP_VER_MULTICAST));
                responseDTO.setIpvcPrefMulti(paramNameValueMap.get(KnConstants.IP_PREF_MULTICAST));
            }
            responseDTO.setSgwLocUriCellular(knAPNConfigDTO.getLocDataUriCellular());
            responseDTO.setSgwLocUriWifi(clusterInfo.getLocDataUriWifi());
            responseDTO.setToken(jwtToken);
            responseDTO.setClientType(subsProfilePersistDTO.getSubsClientType());
            responseDTO.setPerformPrivacyOperation(performPrivacyOperation);
            responseDTO.setPrivacyValueToExecute(privacyFlagToExecute);
            responseDTO.setProfileMdnActivsFsMap(profileMdnActivsFsMap);
            
            //updating LASTPROFILEUPDATETIME in POCSUBSCRINFO profile mdn notiy
            if (mdnUpmFsMap.size() > 0) {
                List<String> profileMdnList = new ArrayList<String>(mdnUpmFsMap.keySet());
                IProvXDMServerDAO xdmServerDAONew = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB)
                        .createProvXDMServerDAO();
                Map<String, Collection<KnDocChangeListDTO>> profileMdnEtagMap = xdmServerDAONew
                        .profileMdnEtagUpdate(profileMdnList, persisterTxn);
                responseDTO.setProfileMdnEtagMap(profileMdnEtagMap);
                responseDTO.setMdnUpmFsMap(mdnUpmFsMap);
                if (profileMdnEtagMap != null && !profileMdnEtagMap.isEmpty()) {
                    responseDTO.setMcsXcapRootUriMap(
                            genInfoUtil.getXCAPRootURI(new ArrayList<>(profileMdnEtagMap.keySet()), persisterTxn, true));
                }
            }

            //UCSPROVCONFIG-897 Upgrading from 12.3 ==> 14.0 KN_GetConfigInfo is not reflecting with configured value.
            if (subsProfilePersistDTO.getClientPVmajorVer() >= PROTOCOL_VERSION_28 && subsProfileInfoDTO.getClientPVmajorVer() <= PROTOCOL_VERSION_27) {
                IProvXDMServerDAO xdmServerDAONew = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
                xdmServerDAONew.updateEmergencyDocEtag(mdn, persisterTxn);
            }

            knLogger.debug(methodName,"responseDTO :",responseDTO,"subsProfileInfoDTO :",subsProfileInfoDTO);
            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
            }
 			if (clientPVMajorVersion >= KnConstants.PROTOCOL_VERSION_20) {
            	knLogger.debug(methodName, "get MCXID value", msCommonConfigMap.get(com.kodiak.common.resources.KnConstants.MCXDOMAINID));	
            	knLogger.debug(methodName, "get GMSID value", msCommonConfigMap.get(com.kodiak.common.resources.KnConstants.GMSDOMAINID));
            	responseDTO.setMCXDOMAINID(msCommonConfigMap.get(com.kodiak.common.resources.KnConstants.MCXDOMAINID));
            	responseDTO.setGMSDOMAINID(msCommonConfigMap.get(com.kodiak.common.resources.KnConstants.GMSDOMAINID));
            	
            }
        } catch (KnFeatureSetException ex) {
            knLogger.error(methodName, "Feature Set Exception occured :", ex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw new KnProvBOException(ex.getErrorCode(), ex.getMessage(), ex);
        } catch (KnDBConnectionException ex) {
            knLogger.error(methodName, "DAO DBConnection Exception occured :", ex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);

        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occurred : ", ex);
            knLogger.error(methodName, ex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            if (com.kodiak.xdms.server.common.resources.KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_INFO_NOT_FOUND, "Subscriber already exists");
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception occurred :", e);
            knLogger.error(methodName, e);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw e;
        } catch (KnValidationException vex) {
            knLogger.error(methodName, "Validation Exception occurred :", vex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw vex;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while activate Subscriber");
            knLogger.error(methodName, e);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred while activate Subscriber", e);
        }
        knLogger.info(methodName, "EXIT: Activate Subscriber operation - ");
        return responseDTO;
    }
    
    private boolean checkClientType(Integer clientType,int mcsCompliance)
	{
		return clientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value()
				|| clientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value()
				|| clientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value()
				|| clientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.DISPATCH.value()
				|| clientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.THIRDPARTYDISPATCHERCLIENT.value()
				|| mcsCompliance == KnConstants.MCSCOMPLIANCE;
	}
    

	public void updateMCSIds(KnPersisterTxn persisterTxn, KnSubsProfilePersistDTO subsProfilePersistDTO) throws KnProvBOException, KnDAOException
			{
		IProvXDMServerDAO iProvXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB)
				.createProvXDMServerDAO();
		iProvXDMServerDAO.updateMCSIds(subsProfilePersistDTO, persisterTxn);
	}

    /**
     * method to retrieve the Subscriber Config Document
     *
     * @param subscriberDTO ISubscriberDTO
     * @param persisterTxn  KnPersisterTxn
     * @return KnOPSubsConfigDocInfoDTO
     * @throws KnProvBOException BO entity Exception
     */
    public KnOPSubsConfigDocInfoDTO getSubscriberConfigDoc(KnIPSubscriberInfoDTO subscriberDTO, KnPersisterTxn persisterTxn) throws KnProvBOException {
        String methodName = "getSubscriberConfigDoc(ISubscriberDTO, KnPersisterTxn )";
        boolean ownedTxn = false;
        boolean isActiveFS1updated = false;
        knLogger.debug(methodName, "ENTRY: getSubscriberConfigDoc with DTO ", subscriberDTO);
        KnOPSubsConfigDocInfoDTO subsConfigDTO = null;
        KnOPCorpProfileInfoDTO corpProfileInfoDTO = null;
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            try {
                xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
            } catch (KnBOException e) {
                knLogger.error(methodName, "failed to retrieve xdm Ptt Sever Id");
                knLogger.error(methodName, e);
                if (ownedTxn) {
                    rollback(persisterTxn);
                }
                throw new KnProvBOException(KnErrorCodes.BOEntity.XDMS_PTT_ID_NOT_FOUND, "Failed to retrieve XDM PTT Server ID", e);
            }
            String mdn = subscriberDTO.getMdn();

            //retrieving the Subscriber Info
            IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
            IProvXDMServerDAO provXdmServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            KnOPSubsProfileInfoDTO subsProfileRespDTO = provXdmServerDAO.retrieveSubscriberInfo(mdn, persisterTxn);
            knLogger.debug(methodName, "subsProfileRespDTO :", subsProfileRespDTO.getServiceAuthStatus());
            String mcpttId_DB = subsProfileRespDTO.getMcpttId();
            String mcpttId_Input = subscriberDTO.getMcPttIdFromToken();
            knLogger.debug(methodName, "mcpttId_DB::" + KnGDPRTemplate.mcpttId(mcpttId_DB));
            knLogger.debug(methodName, "mcpttId_Input::" + KnGDPRTemplate.mcpttId(mcpttId_Input));


            if (mcpttId_Input != null && mcpttId_DB != null) {
                if (subsProfileRespDTO.getMcpttCompliance() == 0) {
                    if (!mcpttId_Input.isEmpty() && !mcpttId_DB.isEmpty()
                            && !(mcpttId_Input.trim().equals(mcpttId_DB.substring(5).trim()) || mcpttId_Input.trim().equals(mcpttId_DB.trim()))) {
                        knLogger.debug(methodName, "MDN is not matched with the token");
                        throw new KnProvBOException(KnErrorCodes.Validator.INVALID_TOKEN_MCPTTID,
                                "MDN is not matched with the token");
                    }
                } else if (subsProfileRespDTO.getMcpttCompliance() == 1) {
                    if (!mcpttId_Input.isEmpty() && !mcpttId_DB.isEmpty() && !mcpttId_Input.trim().equals(mcpttId_DB.trim())) {
                        knLogger.debug(methodName, "MCPTTD is not matched with the token");
                        throw new KnProvBOException(KnErrorCodes.Validator.INVALID_TOKEN_MCPTTID,
                                "MCPTTD is not matched with the token");
                    }
                }
            }
            // updating MCS Ids
            if (subsProfileRespDTO.getMcId() == null) {
                KnSubsProfilePersistDTO subsProfilePersistDTO = new KnSubsProfilePersistDTO();
                subsProfilePersistDTO.setMdn(mdn);
                subsProfilePersistDTO.setMcId(TELURI + mdn);
                subsProfilePersistDTO.setMcpttId(TELURI + mdn);
                subsProfilePersistDTO.setMcVideoId(TELURI + mdn);
                subsProfilePersistDTO.setMcDataId(TELURI + mdn);
                updateMCSIds(persisterTxn, subsProfilePersistDTO);
            }

            subsConfigDTO = new KnOPSubsConfigDocInfoDTO();
            subsConfigDTO.setPubSubscriptionType(subsProfileRespDTO.getPublicSubscriptionType());
            subsConfigDTO.setCorpSubscriptionType(subsProfileRespDTO.getCorporateSubscriptionType());
            subsConfigDTO.setSubscriptionState(subsProfileRespDTO.getServiceAuthStatus());
            subsConfigDTO.setNetworkName(subsProfileRespDTO.getNetworkName());
            subsConfigDTO.setMdn(subsProfileRespDTO.getMdn());
            subsConfigDTO.setLastProfileUpdateTime(subsProfileRespDTO.getLastProfileUpdateTime());
            subsConfigDTO.setXui(KnProvConstants.TEL_URI_TEMPLATE + subsProfileRespDTO.getMdn());
            subsConfigDTO.setClientType(subsProfileRespDTO.getSubsClientType());
            subsConfigDTO.setProtocolVersion(String.valueOf(subsProfileRespDTO.getClientPVmajorVer()));
            if (subsConfigDTO.getProtocolVersion().equals(KnSubConfigConstants.PROTOCOL_VERSION_0_X)) {
                subsConfigDTO.setProtocolVersion(KnSubConfigConstants.PROTOCOL_VERSION_1_X);
                subsProfileRespDTO.setClientPVmajorVer(1);
                //   subsProfileRespDTO.setClientPVminorVer(0);
            }
            if (subsProfileRespDTO.getClientPVmajorVer() < PROTOCOL_VERSION_16) {
                String activefs1 = KnGeneralUtil.convertActiveFs2toHexActiveFs1(subsProfileRespDTO.getActiveFS2());
                subsConfigDTO.setActiveFS2(activefs1);
            } else {
                subsConfigDTO.setActiveFS2(subsProfileRespDTO.getActiveFS2());
            }

            //resticting clientFS for PV 9
            if (!(subsConfigDTO.getProtocolVersion().matches(KnSubConfigConstants.PROTOCOL_VERSION_9_X))) {
                subsConfigDTO.setClientFS2(subsProfileRespDTO.getClientFS2());
            }
            //setting the value as 3. As per SDD V7.2.
            subsConfigDTO.setRoamingBit(3);

            int clusterId = Integer.parseInt(System.getenv(CLUSTERID_ENV_NAME));
            Map<String, String> msSvcConfigDocMap = genInfoUtil.retrieveMSSvcsServiceConfig(clusterId, persisterTxn);

            // Internet APN changes ...
            String apnName = subscriberDTO.getApnName();
            knLogger.info(methodName, "APN NAME as of request   ", apnName);
            if (apnName == null || apnName.equalsIgnoreCase("UNKNOWN")) {
                apnName = genInfoUtil.getDefaultAPNName(true, persisterTxn);
            }
            knLogger.info(methodName, "APN NAME  ", apnName);
            // verify apn exists or not ..
            Integer apnId = genInfoUtil.getAPNId(apnName, true, persisterTxn);
            if (apnId == null) {
                knLogger.error(methodName, " APN not exists , APN_INFO_NOT_FOUND ");
                throw new KnProvBOException(KnErrorCodes.BOEntity.APN_INFO_NOT_FOUND, " APN_INFO_NOT_FOUND ");
            }
            // verify apn exists or not ..
            knLogger.info(methodName, " APNID ", apnId);
            if (apnId == null) {
                knLogger.error(methodName, " APN not exists , APN_INFO_NOT_FOUND ");
                throw new KnProvBOException(KnErrorCodes.BOEntity.APN_INFO_NOT_FOUND, " APN_INFO_NOT_FOUND ");
            }

            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            if (KnConstants.MCSCOMPLIANCE != subsProfileRespDTO.getMcpttCompliance()) {
                List<String> allMdnList = getMdnForUPM(mdn, true, persisterTxn);
                if (!allMdnList.isEmpty()) {
                    provXDMServerDAO.updateSubApnIdForMdnList(allMdnList, apnId, false, persisterTxn);
                }
            }

            // getting doc config info from Corporate Info
            //  KnOPCorpProfileInfoDTO corpProfileInfoDTO = provXdmServerDAO.retrieveCorporateProfile(extCorpId, persisterTxn);

            //   subsConfigDTO.setMaxCorporateContacts(corpProfileInfoDTO.getMaxSubscribers());
            //  subsConfigDTO.setMaxMembersPerCorpGroup(corpProfileInfoDTO.getMaxMembersPerCorpGroup());

            //setting activeFS1
            String pocPttId = subsProfileRespDTO.getPoCHome();
            String presencePttId = subsProfileRespDTO.getPresenceHome();
            String xdmsPttId = subsProfileRespDTO.getXDMSHome();
            String clientFS2 = subsProfileRespDTO.getClientFS2();
            // recalculate subcriberFS
            String subsFS2 = generateSubsFS2(subsProfileRespDTO, persisterTxn);
            String opsFS2 = subsProfileRespDTO.getOpsFS2();
            String corpAdminFS2 = subsProfileRespDTO.getCorpAdminFS2();
            int clientPVMajorVersion = subsProfileRespDTO.getClientPVmajorVer();
            String clientCapOverrideBitMask = featureSetUtil.getClientCapabilityBitMask(xdmPttServerId, clientPVMajorVersion);
            int vocoderId = subsProfileRespDTO.getVocoderId();
            knLogger.debug(methodName, "clientPVMajorVersion ", clientPVMajorVersion, " ,clientCapOverrideBitMask=", clientCapOverrideBitMask);
            Map<String, String> paramNameValueMap = genInfoUtil.retrieveRTXConfigValues(persisterTxn);
            if (subsConfigDTO.getProtocolVersion().matches(KnSubConfigConstants.PROTOCOL_VERSION_9_12)) {

                Map<Integer, Integer> clientVocoderProfile = provXDMServerDAO.retrieveClientSuppVocoder(mdn, true, persisterTxn);
                knLogger.debug(methodName, " retrieve client Vocoder file :", clientVocoderProfile);


                Integer serverPreferVocoder = null;
                try {
                    serverPreferVocoder = Integer.valueOf(paramNameValueMap.get(KnConstants.SERVER_PREFERRED_VOCODER_PROFILEID));
                } catch (NumberFormatException nfe) {
                    knLogger.error(methodName, "NumberFormatException occured while retrieving server preffered vocoderid");
                }
                if (!serverPreferVocoder.equals(vocoderId)) {
                    if (clientVocoderProfile.containsValue(serverPreferVocoder)) {
                        knLogger.debug(methodName, "Updating vocoder with server preferrred");
                        provXDMServerDAO.updateSubscVocoderId(mdn, serverPreferVocoder, false, persisterTxn);
                        vocoderId = serverPreferVocoder;
                    } else {
                        Integer vocoder = clientVocoderProfile.values().iterator().next();
                        knLogger.debug(methodName, "Updating vocoder with clientSupp vocoder with highest priority :", vocoder);
                        provXDMServerDAO.updateSubscVocoderId(mdn, vocoder, false, persisterTxn);
                        vocoderId = vocoder;
                    }
                }
            }

            int QPPPackId = subsProfileRespDTO.getQppPkgId();


            Map<String, String> mapIds = new HashMap<String, String>();
            Map<String, String> loaderResponse = new HashMap<String, String>();
            mapIds.put("POC_PTTSERVERID", pocPttId);
            mapIds.put("PRES_PTTSERVERID", presencePttId);
            mapIds.put("XDMS_PTTSERVERID", xdmsPttId);
            mapIds.put("MDN", mdn);
            mapIds.put("CORPID", String.valueOf(subsProfileRespDTO.getCorpId()));
            mapIds.put("FEATUREACCESSINDEX", String.valueOf(52));
            mapIds.put("APNID", Integer.toString(apnId));
            mapIds.put("VOCODERID", Integer.toString(vocoderId));
            mapIds.put("CLUSTERID", Integer.toString(clusterId));
            mapIds.put("QPPPackId", Integer.toString(QPPPackId));
            knLogger.debug("Map ids ", mapIds);
            // calling ConfigurationLoader  frame work ..
            KnConfigurationLoader loader = KnConfigurationLoader.getInstance();
            KnSubsConfigParams subsConfigParams = new KnSubsConfigParams();
            //Map<String, Map<String, String>> Ver1Keys = subsConfigParams.getVersion1Ids(mapIds);
            Map<String, Map<String, String>> Ver1Keys = subsConfigParams.getParamKeys(String.valueOf(subsProfileRespDTO.getClientPVmajorVer()), mapIds);
            String googleMapsApiKeyValue = KnCommonVaultUtil.getKeyFromVault(GOOGLE_MAPS_API_PATH, GOOGLE_MAPS_API_KEY);
            loaderResponse = loader.getCongiurationParamValues(Ver1Keys);
            if (Ver1Keys.containsKey(KnSubConfigConstants.G_API_KEY)) {
                loaderResponse.put(KnSubConfigConstants.G_API_KEY, googleMapsApiKeyValue);
            }
            loaderResponse.put(KnSubConfigConstants.G_API_KEY, googleMapsApiKeyValue);
            knLogger.info("loaderResponse ---- ", loaderResponse);
            String corpFS2 = null;
            String activeFS2 = null;
            //retrieving the Ext CorpId and calcluating activeFS.
            String extCorpId = null;
            String xdmsFs2 = subsProfileRespDTO.getXdmsFS2();
            String userProfileFS2 = subsProfileRespDTO.getUserProfileFS2();
            if (userProfileFS2 == null) {
                userProfileFS2 = featureSetUtil.getDefFinalUserProfileFS();
            }
            if (subsProfileRespDTO.getCorporateSubscriptionType() == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
                extCorpId = provXdmServerDAO.retrieveExtCorporationId(subsProfileRespDTO.getCorpId(), persisterTxn);
                // getting doc config info from Corporate Info
                corpProfileInfoDTO = provXdmServerDAO.retrieveCorporateProfile(extCorpId, true, persisterTxn);
                corpFS2 = corpProfileInfoDTO.getCorpFS2();
                activeFS2 = featureSetUtil.generateActiveFeatBitSet(pocPttId, presencePttId, xdmsPttId, clientFS2, subsFS2, corpFS2, opsFS2, corpAdminFS2, clientCapOverrideBitMask, xdmsFs2, userProfileFS2);

            } else {
                activeFS2 = featureSetUtil.generateActiveFeatBitSet(pocPttId, presencePttId, xdmsPttId, clientFS2, subsFS2, opsFS2, clientCapOverrideBitMask, xdmsFs2, userProfileFS2);
            }

            String newActiveFS2 = KnGeneralUtil.calculateActiveFeatureSetBasedOnPv(activeFS2,clientPVMajorVersion);
            subsConfigDTO.setActiveFS2(newActiveFS2);
            subsConfigDTO.setOldActiveFS2(subsProfileRespDTO.getActiveFS2());
            subsConfigDTO.setSubscriberFS2(subsFS2);
            boolean cleanUpTGSData = false;

            Map<String, KnOPSubsProfileInfoDTO> mdnUpmFsMap = null;

            //update the activeFS1 in DB
            knLogger.debug(methodName, "Checking ActiveFS old and calculated are same or not ", "old ActiveFS2 : ", subsProfileRespDTO.getActiveFS2(), " Calculated Active FS2 : ", activeFS2);
            if (!activeFS2.equals(subsProfileRespDTO.getActiveFS2())) {
                //adding to mcxGroup clean up job on 81 bit disable
                com.kodiak.xdms.server.common.util.KnGeneralUtil.veryLargeGroupBitChanged(subsProfileRespDTO.getActiveFS2()
                        ,activeFS2
                        ,subsProfileRespDTO.getMdn()
                        ,String.valueOf(subsProfileRespDTO.getCorpId()),false);

                long lastProfileUpdateTime = Calendar.getInstance().getTimeInMillis();
                KnSubsProfilePersistDTO subsProfilePersistDTO = new KnSubsProfilePersistDTO();
                subsProfilePersistDTO.setMdn(mdn);
                subsProfilePersistDTO.setActiveFS2(activeFS2);
                subsProfilePersistDTO.setSubsFS2(subsFS2);
                ;
                subsProfilePersistDTO.setLastProfileUpdateTime(lastProfileUpdateTime);
                subsProfilePersistDTO.setClientPVmajorVer(Integer.parseInt(subsConfigDTO.getProtocolVersion()));
                subsProfilePersistDTO.setSubsClientType(subsConfigDTO.getClientType());

                provXdmServerDAO.updateActiveFS(subsProfilePersistDTO, false, persisterTxn);
                isActiveFS1updated = true;
                cleanUpTGSData = cleanUpTGSData(activeFS2, subsProfileRespDTO.getActiveFS2());
                //check and update MCPTT feature permission
                setMCPTTFeatureDisable(mdn, activeFS2, persisterTxn, provXDMServerDAO);
                int isAffiliationEnabled;
                IXDMServerDAO commonXDMServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
                boolean bitEnabled = KnGeneralUtil.getFeatureBitValue(subsProfilePersistDTO.getActiveFS2(), com.kodiak.common.resources.KnConstants.FEATURE_SET.AFFILIATIONFEATURE.value());
                knLogger.debug(methodName, "Checking the PV , Client Type and bitEnabled ", bitEnabled, subsProfilePersistDTO.getClientPVmajorVer(), subsProfilePersistDTO.getSubsClientType());
                if (subsProfilePersistDTO.getClientPVmajorVer() >= com.kodiak.xdms.server.common.resources.KnConstants.PROTOCOL_VERSION_18
                        && bitEnabled && checkClientType(subsProfilePersistDTO.getSubsClientType(), subsProfileRespDTO.getMcpttCompliance())) {
                    isAffiliationEnabled = ENABLED;
                } else {
                    isAffiliationEnabled = DISABLED;
                }
                commonXDMServerDAO.updateAffForCorpGrpMemList(subsProfilePersistDTO.getMdn(), isAffiliationEnabled, persisterTxn);

                //updating fileds for profileMdn.
                mdnUpmFsMap = provXdmServerDAO.getProfileMdnNupmfsByBaseMdn(mdn, true, persisterTxn);
                mdnUpmFsMap.remove(mdn);
                if (mdnUpmFsMap.size() > 0) {
                    Map<String, String> profileMdnActivsFsMap = provInfoUtil.calculateActiveFS2WithUPMFS(
                            subsProfileRespDTO.getCorporateSubscriptionType(), pocPttId, presencePttId, xdmsPttId,
                            clientFS2, subsFS2, corpFS2, opsFS2, corpAdminFS2, clientCapOverrideBitMask,
                            mdnUpmFsMap);
                    Collection<KnSubsProfileDTO> subsSpecificDetails = provXDMServerDAO
                            .fetchSubsSpecificDetailsForBulkMdns(new ArrayList<>(profileMdnActivsFsMap.keySet()), true, persisterTxn);
                    Map<String, String> mdnUpmIdMap =new HashMap<>();
                    Map<String, String> mdnActiveFsMap =new HashMap<>();
                    for(KnSubsProfileDTO subsInfo:subsSpecificDetails){
                        mdnUpmIdMap.put(subsInfo.getMdn(),subsInfo.getUserProfileId());
                        mdnActiveFsMap.put(subsInfo.getMdn(),subsInfo.getActiveFS2());
                    }
                    Map<String,List<Integer>> upmIdGroupMap=provInfoUtil.retriveUpmIdGroupMapByUserProfileId(new ArrayList<>(mdnUpmIdMap.values()), true, persisterTxn);
                    knLogger.debug(methodName," mdnUpmIdMap :",mdnUpmIdMap," existingActiveFS2 :",mdnActiveFsMap);
                    knLogger.debug(methodName," profileMdnActivsFsMap :",profileMdnActivsFsMap);

                    subsProfilePersistDTO.setNetworkName(subsProfileRespDTO.getNetworkName());
                    subsProfilePersistDTO.setCorporateSubscriptionType(subsProfileRespDTO.getCorporateSubscriptionType());
                    subsProfilePersistDTO.setPublicSubscriptionType(subsProfileRespDTO.getPublicSubscriptionType());
                    subsProfilePersistDTO.setServiceAuthStatus(subsProfileRespDTO.getServiceAuthStatus());
                    subsProfilePersistDTO.setIMEI(subsProfileRespDTO.getIMEI());
                    subsProfilePersistDTO.setClientPassword(subsProfileRespDTO.getClientPassword());
                    subsProfilePersistDTO.setUserAgent(subsProfileRespDTO.getUserAgent());
                    subsProfilePersistDTO.setVocoderId(subsProfileRespDTO.getVocoderId());
                    subsProfilePersistDTO.setClientPVmajorVer(subsProfileRespDTO.getClientPVmajorVer());
                    subsProfilePersistDTO.setClientPVminorVer(subsProfileRespDTO.getClientPVminorVer());
                    subsProfilePersistDTO.setLastActivationTime(subsProfileRespDTO.getLastActivationTime());
                    subsProfilePersistDTO.setSwType(subsProfileRespDTO.getSwType());
                    subsProfilePersistDTO.setPlatformType(subsProfileRespDTO.getPlatformType());
                    subsProfilePersistDTO.setDynamicQosFlag(subsProfileRespDTO.getDynamicQosFlag());
                    if (subsProfileRespDTO.getDerivedKey() != null)
                        subsProfilePersistDTO.setDerivedKey(KnGeneralUtil.convertHexToAscii(subsProfileRespDTO.getDerivedKey()));
                    subsProfilePersistDTO.setServiceStatusOp(subsProfileRespDTO.getServiceStatusOp());
                    subsProfilePersistDTO.setLicenseType(subsProfileRespDTO.getLicenseType());
                    subsProfilePersistDTO.setClientFS2(clientFS2);
                    subsProfilePersistDTO.setOpsFS2(opsFS2);
                    subsProfilePersistDTO.setXdmsFS2(xdmsFs2);
                    subsProfilePersistDTO.setCorpAdminFS2(corpAdminFS2);
                    provXdmServerDAO.updateMdnFiledsNActiveFS(subsProfilePersistDTO, profileMdnActivsFsMap, persisterTxn);
                    subsConfigDTO.setProfileMdnActivsFsMap(profileMdnActivsFsMap);
                    //Add to mcx cleanup job
                    for(Map.Entry<String,String> profieMdnActiveFS:profileMdnActivsFsMap.entrySet()){
                        String profileMdnActiveFs = profieMdnActiveFS.getValue();
                        boolean veryLargeGroupBit = KnGeneralUtil.getFeatureBitValue(profileMdnActiveFs, VERY_LARGE_GROUP);
                        String profileMdn = profieMdnActiveFS.getKey();
                        String upmId=mdnUpmIdMap.get(profileMdn);
                        String existingActiveFS2=mdnActiveFsMap.get(profileMdn);
                        List<Integer> mcxGroups = upmIdGroupMap.get(upmId);
                        knLogger.debug(methodName," profilemdn: ",profileMdn," mcxgroup veryLargeGroupBit:",veryLargeGroupBit," mcxGroups :",mcxGroups);
                        if(!veryLargeGroupBit&&mcxGroups!=null&&!mcxGroups.isEmpty()){
                            //adding to mcxGroup clean up job on profile mdn 81 bit disable
                            com.kodiak.xdms.server.common.util.KnGeneralUtil.veryLargeGroupBitChanged(existingActiveFS2
                                    ,profileMdnActiveFs
                                    ,profileMdn
                                    ,String.valueOf(subsProfilePersistDTO.getCorpId()),true);
                        }
                    }

                }
            }
            subsConfigDTO.setCleanUpTGSData(cleanUpTGSData);
            knLogger.debug(methodName, "cleanUp TGS ", cleanUpTGSData);
            // getting doc config info from POC Service Config
            String pocPttServerId = subsProfileRespDTO.getPoCHome();

            // getting doc config info from Registrar Service Config
            KnPOCRegistrarSrvcConfigDTO pocRegistrarSrvcConfigDTO = provInfoUtil.retrievePOCregistrarSrvcConfig(pocPttServerId);

            // getting doc config info from presence Service Config
            String presencePttServerId = subsProfileRespDTO.getPresenceHome();
            KnPresenceServiceConfigDTO preServConfigDTO = provInfoUtil.retrievePresenceServiceConfig(presencePttServerId);
            subsConfigDTO.setPocPttId(pocPttId);
            subsConfigDTO.setPresPttId(presencePttId);

// Version 1.x & 2.x ...
            knLogger.info(methodName, "  Version ..", subsConfigDTO.getProtocolVersion());
            if (subsConfigDTO.getProtocolVersion() != null && subsConfigDTO.getProtocolVersion().matches(KnSubConfigConstants.PROTOCOL_VERSION_1_X)
                    || subsConfigDTO.getProtocolVersion().matches(KnSubConfigConstants.PROTOCOL_VERSION_2_X)) {
                knLogger.info(methodName, "  1 & 2 Version ..", subsConfigDTO.getProtocolVersion());
                subsConfigDTO.setPrimaryPresenceUri(provInfoUtil.updateConfigRouteUri(loaderResponse.get(KnSubConfigConstants.PRIMARY_RLS_ROUTE)));
                subsConfigDTO.setGeoPresenceUri(provInfoUtil.updateConfigRouteUri(loaderResponse.get(KnSubConfigConstants.GEO_RLS_ROUTE)));
                //subsConfigDTO.setGeoSessionUri(provInfoUtil.updateConfigRouteUri(pocRegistrarSrvcConfigDTO.getGeoRegistrarURI()));
                subsConfigDTO.setGeoSessionUri(provInfoUtil.updateConfigRouteUri(loaderResponse.get(KnSubConfigConstants.GEO_REGISTRAR_ROUTE)));
                //subsConfigDTO.setGeoPocSettingUri(provInfoUtil.updateConfigRouteUri(pocRegistrarSrvcConfigDTO.getGeoRegistrarURI()));
                subsConfigDTO.setGeoPocSettingUri(provInfoUtil.updateConfigRouteUri(loaderResponse.get(KnSubConfigConstants.GEO_SESSION_ROUTE)));
                //subsConfigDTO.setGeoIpaUri(provInfoUtil.updateConfigRouteUri(pocRegistrarSrvcConfigDTO.getGeoRegistrarURI()));
                subsConfigDTO.setGeoIpaUri(provInfoUtil.updateConfigRouteUri(loaderResponse.get(KnSubConfigConstants.GEO_SESSION_ROUTE)));
                // subsConfigDTO.setGeoRegistrarUri(provInfoUtil.updateConfigRouteUri(pocRegistrarSrvcConfigDTO.getGeoRegistrarURI()));
                subsConfigDTO.setGeoRegistrarUri(provInfoUtil.updateConfigRouteUri(loaderResponse.get(KnSubConfigConstants.GEO_SESSION_ROUTE)));
                //subsConfigDTO.setGeoSubscriptionProxyUri(pocRegistrarSrvcConfigDTO.getGeoRegistrarURI());
                subsConfigDTO.setGeoSubscriptionProxyUri(loaderResponse.get(KnSubConfigConstants.GEO_SESSION_ROUTE));
                //subsConfigDTO.setGeoXdmsUri(provInfoUtil.updateConfigRouteUri(pocRegistrarSrvcConfigDTO.getGeoRegistrarURI()));
                subsConfigDTO.setGeoXdmsUri(provInfoUtil.updateConfigRouteUri(loaderResponse.get(KnSubConfigConstants.GEO_SESSION_ROUTE)));
                //subsConfigDTO.setPrimarySessionUri(provInfoUtil.updateConfigRouteUri(pocSvcConfigDTO.getPrimaryPOCServerURI()));
                subsConfigDTO.setPrimarySessionUri(provInfoUtil.updateConfigRouteUri(loaderResponse.get(KnSubConfigConstants.PRIMARY_POC_SETTINGS_ROUTE))); // deprecated in 5
                subsConfigDTO.setGeoRlsUri(provInfoUtil.updateConfigRouteUri(loaderResponse.get(KnSubConfigConstants.GEO_RLS_ROUTE)));
                //subsConfigDTO.setGeoRlsUri(KnProvConstants.DUMMY + provInfoUtil.updateConfigRouteUri(preServConfigDTO.getGeoPresenceServerURI()));
                //subsConfigDTO.setPrimaryXdmsUri(provInfoUtil.updateConfigRouteUri(xdmsDocSubPrxConfigDTO.getPrimaryDocSubPrxURI()));
                subsConfigDTO.setPrimaryXdmsUri(provInfoUtil.updateConfigRouteUri(loaderResponse.get(KnSubConfigConstants.PRIMARY_XDMS_ROUTE)));
                //subsConfigDTO.setPublishPocSettingsTimer(pocSvcConfigDTO.getPOCPublishValidity());
                subsConfigDTO.setPublishPocSettingsTimer(convertInteger(loaderResponse.get(KnSubConfigConstants.PUBLISH_POC_SETTINGS_TIMER)));

                subsConfigDTO.setPublishPresenceTimer(convertInteger(loaderResponse.get(KnSubConfigConstants.PUBLISH_PRESENCE_TIMER)));
                subsConfigDTO.setInviteTimer(convertInteger(loaderResponse.get(KnSubConfigConstants.INVITE_TIMER)));
                //subsConfigDTO.setMediaPortRefreshTimer(pocSvcConfigDTO.getMediaPortRefreshTime());
                subsConfigDTO.setMediaPortRefreshTimer(convertInteger(loaderResponse.get(KnSubConfigConstants.MEDIA_PORT_REFRESH_TIMER)));
                //subsConfigDTO.setRlsSubscriptionTimer(preServConfigDTO.getRLS_SubscriptionValidity());
                subsConfigDTO.setRlsSubscriptionTimer(convertInteger(loaderResponse.get(KnSubConfigConstants.RLS_SUBSCRIPTION_TIMER)));
                //setting default values
                subsConfigDTO.setSupportPreEstablishmentSession(KnProvConstants.TRUE);
                subsConfigDTO.setSupportSimultaneousSession(KnProvConstants.FALSE);
                subsConfigDTO.setNumOfWakeupTriggers(convertInteger(loaderResponse.get(KnSubConfigConstants.NUM_OF_WAKEUP_TRIGGERS)));
                //subsConfigDTO.setXdmsSubscriptionTimer(xdmsDocSubPrxConfigDTO.getDocSubscriptionValidity());
                subsConfigDTO.setXdmsSubscriptionTimer(convertInteger(loaderResponse.get(KnSubConfigConstants.XDMS_SUBSCRIPTION_TIMER)));
                //subsConfigDTO.setWakeupTimeInterval(instaPOCSrvcCfgDTO.getWakeupTriggerInterval());
                subsConfigDTO.setWakeupTimeInterval(convertInteger(loaderResponse.get(KnSubConfigConstants.WAKEUP_TIME_INTERVAL)));
                subsConfigDTO.setPrimaryXdmsUri(provInfoUtil.updateConfigRouteUri(loaderResponse.get(KnSubConfigConstants.PRIMARY_XDMS_ROUTE)));
                subsConfigDTO.setPublishPresenceTimer(convertInteger(loaderResponse.get(KnSubConfigConstants.PUBLISH_PRESENCE_TIMER)));
                subsConfigDTO.setRoamingAllowed(KnProvConstants.TRUE);
            }
            knLogger.debug(methodName, " after 1.2 ", subsConfigDTO);
            //Version 3.X
            if (subsConfigDTO.getProtocolVersion() != null && subsConfigDTO.getProtocolVersion().matches(KnSubConfigConstants.PROTOCOL_VERSION_3_X)) {
                knLogger.info(methodName, "  only 3 Version  ..", subsConfigDTO.getProtocolVersion());
                subsConfigDTO.setPrimarySessionUri(provInfoUtil.updateConfigRouteUri(loaderResponse.get(KnSubConfigConstants.PRIMARY_POC_SETTINGS_ROUTE)));
                subsConfigDTO.setPrimaryPresenceUri(provInfoUtil.updateConfigRouteUri(loaderResponse.get(KnSubConfigConstants.PRIMARY_RLS_ROUTE)));
                //subsConfigDTO.setPrimaryXdmsUri(provInfoUtil.updateConfigRouteUri(xdmsDocSubPrxConfigDTO.getPrimaryDocSubPrxURI()));
                subsConfigDTO.setPrimaryXdmsUri(provInfoUtil.updateConfigRouteUri(loaderResponse.get(KnSubConfigConstants.PRIMARY_XDMS_ROUTE)));
                //subsConfigDTO.setPublishPocSettingsTimer(pocSvcConfigDTO.getPOCPublishValidity());
                subsConfigDTO.setPublishPocSettingsTimer(convertInteger(loaderResponse.get(KnSubConfigConstants.PUBLISH_POC_SETTINGS_TIMER)));
                //subsConfigDTO.setInviteTimer(pocSvcConfigDTO.getPreestSessionValidity());
                subsConfigDTO.setInviteTimer(convertInteger(loaderResponse.get(KnSubConfigConstants.INVITE_TIMER)));
                subsConfigDTO.setPublishPresenceTimer(convertInteger(loaderResponse.get(KnSubConfigConstants.PUBLISH_PRESENCE_TIMER)));
                //subsConfigDTO.setRlsSubscriptionTimer(preServConfigDTO.getRLS_SubscriptionValidity());
                subsConfigDTO.setRlsSubscriptionTimer(convertInteger(loaderResponse.get(KnSubConfigConstants.RLS_SUBSCRIPTION_TIMER)));
                //setting default values
                subsConfigDTO.setSupportPreEstablishmentSession(KnProvConstants.TRUE);
                subsConfigDTO.setSupportSimultaneousSession(KnProvConstants.FALSE);
                //subsConfigDTO.setWakeupTimeInterval(instaPOCSrvcCfgDTO.getWakeupTriggerInterval());
                subsConfigDTO.setWakeupTimeInterval(convertInteger(loaderResponse.get(KnSubConfigConstants.WAKEUP_TIME_INTERVAL)));

                //subsConfigDTO.setXdmsSubscriptionTimer(xdmsDocSubPrxConfigDTO.getDocSubscriptionValidity());
                subsConfigDTO.setXdmsSubscriptionTimer(convertInteger(loaderResponse.get(KnSubConfigConstants.XDMS_SUBSCRIPTION_TIMER)));
                //subsConfigDTO.setNumOfWakeupTriggers(instaPOCSrvcCfgDTO.getNumWakeupMsgPerTrigger());
                subsConfigDTO.setNumOfWakeupTriggers(convertInteger(loaderResponse.get(KnSubConfigConstants.NUM_OF_WAKEUP_TRIGGERS)));
                subsConfigDTO.setNumOfBurstPerTrigger(convertInteger(loaderResponse.get(KnSubConfigConstants.NUM_OF_BURST_PER_TRIGGER)));
                subsConfigDTO.setRoamingAllowed(KnProvConstants.TRUE);

            }
            knLogger.debug(methodName, " getSubscriberConfigDoc with DTO ", subscriberDTO);
//Version 5.x & 6.x version
            if (subsConfigDTO.getProtocolVersion() != null && subsConfigDTO.getProtocolVersion().matches(KnSubConfigConstants.PROTOCOL_VERSION_5_X)
                    || subsConfigDTO.getProtocolVersion().matches(KnSubConfigConstants.PROTOCOL_VERSION_6_X)) {
                knLogger.info(methodName, " Version  5 or 6  ..", subsConfigDTO.getProtocolVersion());
                subsConfigDTO.setRoamingAllowed(KnProvConstants.TRUE);
                subsConfigDTO.setLtekap(getValues(loaderResponse.get(KnSubConfigConstants.LTE_PRECALL_NUM_KA_MEDIA_PACKETS), loaderResponse.get(KnSubConfigConstants.LTE_KA_PACKET_SIZE),
                        loaderResponse.get(KnSubConfigConstants.LTE_PRECALL_KA_INTERVAL), loaderResponse.get(KnSubConfigConstants.LTE_INCALL_KA_INTERVAL),
                        loaderResponse.get(KnSubConfigConstants.LTE_PRECALL_KA_DURATION)));

                subsConfigDTO.setWfkap(getValues(loaderResponse.get(KnSubConfigConstants.WF_PRECALL_NUM_KA_MEDIA_PACKETS), loaderResponse.get(KnSubConfigConstants.WF_KA_PACKET_SIZE),
                        loaderResponse.get(KnSubConfigConstants.WF_PRECALL_KA_INTERVAL), loaderResponse.get(KnSubConfigConstants.WF_INCALL_KA_INTERVAL),
                        loaderResponse.get(KnSubConfigConstants.WF_PRECALL_KA_DURATION)));

                subsConfigDTO.setUmtskap(getValues(loaderResponse.get(KnSubConfigConstants.UMTS_PRECALL_NUM_KA_MEDIA_PACKETS), loaderResponse.get(KnSubConfigConstants.UMTS_KA_PACKET_SIZE),
                        loaderResponse.get(KnSubConfigConstants.UMTS_PRECALL_KA_INTERVAL), loaderResponse.get(KnSubConfigConstants.UMTS_INCALL_KA_INTERVAL),
                        loaderResponse.get(KnSubConfigConstants.UMTS_PRECALL_KA_DURATION)));
                subsConfigDTO.setMwpt(convertInteger(loaderResponse.get(KnSubConfigConstants.M_WP_T)));
                subsConfigDTO.setWpgt(convertInteger(loaderResponse.get(KnSubConfigConstants.WP_G_T)));
                subsConfigDTO.setSrpi(convertInteger(loaderResponse.get(KnSubConfigConstants.S_RP_I)));
                subsConfigDTO.setStci(convertInteger(loaderResponse.get(KnSubConfigConstants.ST_C_I)));
                subsConfigDTO.setLgsrvuri(convertLgsrv(loaderResponse.get(KnSubConfigConstants.LG_CLIENTURI), loaderResponse.get(KnSubConfigConstants.LogServerWSContext)));
                subsConfigDTO.setScgbm(convertInteger(loaderResponse.get(KnSubConfigConstants.S_CG_BM)));
                subsConfigDTO.setSpgag(convertInteger(loaderResponse.get(KnSubConfigConstants.S_PG_AG)));
                subsConfigDTO.setSnv(convertInteger(loaderResponse.get(KnSubConfigConstants.S_N_V)));
                subsConfigDTO.setCgpu(loaderResponse.get(KnSubConfigConstants.C_GP_U));
                //7.7.1
                if ((subsConfigDTO.getProtocolVersion().matches(KnSubConfigConstants.PROTOCOL_VERSION_5_6_7_X) &&
                        (loaderResponse.get(KnSubConfigConstants.T_FLT_CON_ODL) == null || loaderResponse.get(KnSubConfigConstants.T_FLT_SNAP_ODL) == null ||
                                loaderResponse.get(KnSubConfigConstants.G_ODL_REQ) == null))) {

                    knLogger.info(methodName, " getSubscriberConfigDoc with DTO ", subscriberDTO);
                    knLogger.info(methodName, " T_FLT_CON_ODL:", loaderResponse.get(KnSubConfigConstants.T_FLT_CON_ODL) + " T_FLT_SNAP_ODL:" + loaderResponse.get(KnSubConfigConstants.T_FLT_SNAP_ODL) + " G_ODL_REQ:" + loaderResponse.get(KnSubConfigConstants.G_ODL_REQ));
                    throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Mandatory Params  are not configured for PV>=5 ");
                } else if (subsConfigDTO.getProtocolVersion().matches(KnSubConfigConstants.PROTOCOL_VERSION_5_6_7_X)) {
                    subsConfigDTO.setOdlfreq(comaValues(loaderResponse.get(KnSubConfigConstants.ODL_FREQ1), loaderResponse.get(KnSubConfigConstants.ODL_FREQ2)));
                    subsConfigDTO.setOdldur(comaValues(loaderResponse.get(KnSubConfigConstants.ODL_DUR1), loaderResponse.get(KnSubConfigConstants.ODL_DUR2)));
                    subsConfigDTO.setTfltconodl(convertInteger(loaderResponse.get(KnSubConfigConstants.T_FLT_CON_ODL)));
                    subsConfigDTO.setTfltsnapodl(convertInteger(loaderResponse.get(KnSubConfigConstants.T_FLT_SNAP_ODL)));
                    subsConfigDTO.setGodlreq(convertInteger(loaderResponse.get(KnSubConfigConstants.G_ODL_REQ)));
                }
            }

            // Version Only  6.x params
            if (subsConfigDTO.getProtocolVersion().matches(KnSubConfigConstants.PROTOCOL_VERSION_6_X)) {
                if (loaderResponse.get(KnSubConfigConstants.TP_T_S) == null || loaderResponse.get(KnSubConfigConstants.TP_T_M) == null
                        || loaderResponse.get(KnSubConfigConstants.IP_V) == null || loaderResponse.get(KnSubConfigConstants.M_PRT_V6) == null
                        || loaderResponse.get(KnSubConfigConstants.G_PP_R) == null || loaderResponse.get(KnSubConfigConstants.PPR_W) == null
                        || loaderResponse.get(KnSubConfigConstants.GPPR_W) == null || loaderResponse.get(KnSubConfigConstants.P_CRU_V6) == null
                        || loaderResponse.get(KnSubConfigConstants.P_CG_T) == null || loaderResponse.get(KnSubConfigConstants.TCP_KTMC_V6) == null
                        || loaderResponse.get(KnSubConfigConstants.M_SSRT_V6) == null || loaderResponse.get(KnSubConfigConstants.XCAP_ROOT_URI_WIFI) == null
                        || loaderResponse.get(KnSubConfigConstants.A_SN_CHG) == null) {
                    knLogger.info(methodName, " getSubscriberConfigDoc with DTO ", subscriberDTO);
                    knLogger.info(methodName, " TP_T_S:", loaderResponse.get(KnSubConfigConstants.TP_T_S) + " TP_T_M:" + loaderResponse.get(KnSubConfigConstants.TP_T_M) + " IP_V:" + loaderResponse.get(KnSubConfigConstants.IP_V)
                            + " M_PRT_V6:", loaderResponse.get(KnSubConfigConstants.M_PRT_V6) + " G_PP_R:" + loaderResponse.get(KnSubConfigConstants.G_PP_R) + " PPR_W:" + loaderResponse.get(KnSubConfigConstants.PPR_W)
                            + " GPPR_W:" + loaderResponse.get(KnSubConfigConstants.GPPR_W) + " P_CRU_V6:" + loaderResponse.get(KnSubConfigConstants.P_CRU_V6) + " P_CG_T:" + loaderResponse.get(KnSubConfigConstants.P_CG_T)
                            + " TCP_KTMC_V6:" + loaderResponse.get(KnSubConfigConstants.TCP_KTMC_V6) + " M_SSRT_V6:" + loaderResponse.get(KnSubConfigConstants.M_SSRT_V6) + " XCAP_ROOT_URI_WIFI:" + loaderResponse.get(KnSubConfigConstants.XCAP_ROOT_URI_WIFI)
                            + " A_SN_CHG" + loaderResponse.get(KnSubConfigConstants.A_SN_CHG));
                    throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Mandatory Params  are not configured for PV>=6");
                } else {
                    subsConfigDTO.setMediaPortRefreshTimer(convertInteger(loaderResponse.get(KnSubConfigConstants.MEDIA_PORT_REFRESH_TIMER)));
                    knLogger.info(methodName, " Version  Only 6  ..", subsConfigDTO.getProtocolVersion());
                    subsConfigDTO.setTpts(convertInteger(loaderResponse.get(KnSubConfigConstants.TP_T_S)));
                    subsConfigDTO.setTptm(convertInteger(loaderResponse.get(KnSubConfigConstants.TP_T_M)));
                    subsConfigDTO.setIpv(loaderResponse.get(KnSubConfigConstants.IP_V));
                    subsConfigDTO.setTcpktmcv6(convertInteger(loaderResponse.get(KnSubConfigConstants.TCP_KTMC_V6)));
                    subsConfigDTO.setMssrtv6(convertInteger(loaderResponse.get(KnSubConfigConstants.M_SSRT_V6)));
                    subsConfigDTO.setMprtv6(convertInteger(loaderResponse.get(KnSubConfigConstants.M_PRT_V6)));
                    subsConfigDTO.setGppr(loaderResponse.get(KnSubConfigConstants.G_PP_R));
                    subsConfigDTO.setPprw(loaderResponse.get(KnSubConfigConstants.PPR_W));
                    subsConfigDTO.setGpprw(loaderResponse.get(KnSubConfigConstants.GPPR_W));
                    subsConfigDTO.setLgsrvuriw(convertLgsrvwifi(loaderResponse.get(KnSubConfigConstants.LG_CLIENTURI_WIFI), loaderResponse.get(KnSubConfigConstants.LogServerWSContext)));
                    subsConfigDTO.setPcruv6(convertInteger(loaderResponse.get(KnSubConfigConstants.P_CRU_V6)));
                    subsConfigDTO.setPcgt(convertInteger(loaderResponse.get(KnSubConfigConstants.P_CG_T)));
                    subsConfigDTO.setXcaprooturiwifi(loaderResponse.get(KnSubConfigConstants.XCAP_ROOT_URI_WIFI) + com.kodiak.common.resources.KnConstants.XCAP_ROOT_CONTEXT);

                    if (subsConfigDTO.getProtocolVersion().matches(KnSubConfigConstants.PROTOCOL_VERSION_13_X)) {
                        subsConfigDTO.setXcaprooturiwifi(loaderResponse.get(KnSubConfigConstants.XCAP_ROOT_URI_WIFI) + com.kodiak.common.resources.KnConstants.OIDC_XCAP_ROOT_CONTEXT);
                    }

                    //self name enhancements ..
                    int selfNameBitNumber = provInfoUtil.getMappedSubscriptionType(subsConfigDTO.getPubSubscriptionType(), subsConfigDTO.getCorpSubscriptionType());
                    boolean allowSelfName = featureSetUtil.getFeatureBitValue(KnGeneralUtil.convertLongToHexString(convertLong(loaderResponse.get(KnSubConfigConstants.A_SN_CHG))), selfNameBitNumber);
                    subsConfigDTO.setAsnchg(allowSelfName);
                    subsConfigDTO.setApnname(apnName);
                }
            }
            //7.10
            if (subsConfigDTO.getProtocolVersion().matches(KnSubConfigConstants.PROTOCOL_VERSION_7_X)) {
                subsConfigDTO.setMscl(convertInteger(loaderResponse.get(KnSubConfigConstants.MSCL)));
                subsConfigDTO.setUprio(convertInteger(loaderResponse.get(KnSubConfigConstants.UPRIO)));
                subsConfigDTO.setEscl("true");
                subsConfigDTO.setAmrfpp(convertInteger(loaderResponse.get(KnSubConfigConstants.AMRFP)));
                if (subsProfileRespDTO.getCorporateSubscriptionType() == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
                    subsConfigDTO.setCorpId(extCorpId);
                    subsConfigDTO.setCorpName(corpProfileInfoDTO.getCorporateName());
                }
                subsConfigDTO.setSuirpi(convertInteger(loaderResponse.get(KnSubConfigConstants.SUI_RP_I)));
                subsConfigDTO.setIpvc(loaderResponse.get(KnSubConfigConstants.IP_V_C));
                subsConfigDTO.setIpvprefc(loaderResponse.get(KnSubConfigConstants.IP_V_PREF_C));
                subsConfigDTO.setIpvprefw(loaderResponse.get(KnSubConfigConstants.IP_V_PREF_W));

            }

            //8.0 params
            if (subsConfigDTO.getProtocolVersion().matches(KnSubConfigConstants.PROTOCOL_VERSION_8_X)) {
                subsConfigDTO.setPwsuriw(loaderResponse.get(KnSubConfigConstants.P_WS_URI_W));
                subsConfigDTO.setGwsuriw(loaderResponse.get(KnSubConfigConstants.G_WS_URI_W));
                subsConfigDTO.setTrice(loaderResponse.get(KnSubConfigConstants.TR_ICE));
                //8.1 param
                subsConfigDTO.setDispListRr(convertInteger(loaderResponse.get(KnSubConfigConstants.DISP_LIST_RR)));
                subsConfigDTO.setpWsUri(loaderResponse.get(KnSubConfigConstants.P_WS_URI));
                subsConfigDTO.setgWsUri(loaderResponse.get(KnSubConfigConstants.G_WS_URI));
                subsConfigDTO.setWsCaeT(convertInteger(loaderResponse.get(KnSubConfigConstants.WS_CAE_T)));
            }

            if (subsConfigDTO.getProtocolVersion().matches(KnSubConfigConstants.PROTOCOL_VERSION_9_X)) {
                subsConfigDTO.setNegCI(loaderResponse.get(KnSubConfigConstants.NEG_C_I));
                String vocoderMap = loaderResponse.get(KnSubConfigConstants.CLIENTCAP);
                subsConfigDTO.setClientCap(clientFS2 + ":" + vocoderMap);
                subsConfigDTO.setCtl(loaderResponse.get(KnSubConfigConstants.C_T_L));
                subsConfigDTO.setCtlIntl(loaderResponse.get(KnSubConfigConstants.C_T_L_INL));
                subsConfigDTO.setIpaATtl(loaderResponse.get(KnSubConfigConstants.IPA__A_TTL));
                subsConfigDTO.setRadChLS(loaderResponse.get(KnSubConfigConstants.RAD_CH_L_S));
                subsConfigDTO.setRadScLS(loaderResponse.get(KnSubConfigConstants.RAD_SC_L_S));
            }

            if (subsConfigDTO.getProtocolVersion().matches(KnSubConfigConstants.PROTOCOL_VERSION_10_X)) {
                knLogger.debug(methodName, "protocol version - ", subsConfigDTO.getProtocolVersion());
                //8.1.2 PV - 10, couch-sync-mobile
                subsConfigDTO.setMaxTextMessageSize(loaderResponse.get(KnSubConfigConstants.M_T_MSG_S));
                subsConfigDTO.setMaxMultimediaMsgSizeOverCellular(loaderResponse.get(KnSubConfigConstants.M_MM_MSG_S_C));
                subsConfigDTO.setMaxMultimediaMsgSizeOverWifi(loaderResponse.get(KnSubConfigConstants.M_MM_MSG_S_W));
                subsConfigDTO.setPush2MessageDeliveryReceiptEnabled(loaderResponse.get(KnSubConfigConstants.P2M_DR));
                subsConfigDTO.setPush2MessageReadReceiptEnabled(loaderResponse.get(KnSubConfigConstants.P2M_RC));
                subsConfigDTO.setPush2MessageFleetMemberGeoTag(loaderResponse.get(KnSubConfigConstants.P2M_FT_GT));
                subsConfigDTO.setMaxUserPredefinedMessages(loaderResponse.get(KnSubConfigConstants.M_UDP_MSG));
                subsConfigDTO.setGeoFenceDistanceMeasurementUnit(loaderResponse.get(KnSubConfigConstants.GF_FN_MU));
                subsConfigDTO.setGeoFencePeriodicLocationUpdateInterval(msSvcConfigDocMap.get(DEFAULT_FENCE_LOC_INTERVAL) + KnSubConfigConstants.SEMI_COLON + msSvcConfigDocMap.get(MIN_LOC_INTERVAL) + KnSubConfigConstants.SEMI_COLON + msSvcConfigDocMap.get(MAX_LOC_INTERVAL));
                subsConfigDTO.setGeoFencingPeriod(msSvcConfigDocMap.get(DEFAULT_FENCING_DURATION) + KnSubConfigConstants.SEMI_COLON + msSvcConfigDocMap.get(MIN_FENCING_DURATION) + KnSubConfigConstants.SEMI_COLON + msSvcConfigDocMap.get(MAX_FENCING_DURATION));
                subsConfigDTO.setGeoFenceDist4Client(msSvcConfigDocMap.get(DEFAULT_GEOFENCE_DIST) + KnSubConfigConstants.SEMI_COLON + msSvcConfigDocMap.get(MIN_GEOFENCE_DIST) + KnSubConfigConstants.SEMI_COLON + msSvcConfigDocMap.get(MAX_GEOFENCE_DIST));
                subsConfigDTO.setOnCallLocationUpdateEnabled(loaderResponse.get(KnSubConfigConstants.ON_LOC_E));
                subsConfigDTO.setOnCallLocationUpdateInterval(loaderResponse.get(KnSubConfigConstants.ON_LOC_U_I));
                subsConfigDTO.setSgwRootUriCellular(loaderResponse.get(KnSubConfigConstants.SGW_R_URI_C));
                subsConfigDTO.setSgwRootUriWifi(loaderResponse.get(KnSubConfigConstants.SGW_R_URI_W));
                subsConfigDTO.setAuthUriCellular(loaderResponse.get(KnSubConfigConstants.XCAP_ROOT_URI) + com.kodiak.common.resources.KnConstants.MS_CBAUTH_ROOT_CONTEXT);
                subsConfigDTO.setAuthUriWifi(loaderResponse.get(KnSubConfigConstants.XCAP_ROOT_URI_WIFI) + com.kodiak.common.resources.KnConstants.MS_CBAUTH_ROOT_CONTEXT);
                subsConfigDTO.setCbBukInfo(loaderResponse.get(KnSubConfigConstants.CB_B_I));
                subsConfigDTO.setSgwAuthMec(loaderResponse.get(KnSubConfigConstants.SGW_A_M));
                subsConfigDTO.setSgwHbInterval(loaderResponse.get(KnSubConfigConstants.SGW_HB_I));
                subsConfigDTO.setMinVoiceMsgFallBackLen(loaderResponse.get(KnSubConfigConstants.M_VM_FBL));
                subsConfigDTO.setMapProviderId(Integer.parseInt(loaderResponse.get(KnSubConfigConstants.MAP_ID)));
                subsConfigDTO.setMapsUriC(loaderResponse.get(KnSubConfigConstants.M_URI_C));
                subsConfigDTO.setMapsUriW(loaderResponse.get(KnSubConfigConstants.M_URI_W));
                subsConfigDTO.setMapsGeoUriC(loaderResponse.get(KnSubConfigConstants.M_G_URI_C));
                subsConfigDTO.setMapsGeoUriW(loaderResponse.get(KnSubConfigConstants.M_G_URI_W));
                subsConfigDTO.setgApiKey(loaderResponse.get(KnSubConfigConstants.G_API_KEY));
                subsConfigDTO.setSgmLocUriCell(loaderResponse.get(KnSubConfigConstants.SGW_L_URI_C));
                subsConfigDTO.setSgmLocUriWifi(loaderResponse.get(KnSubConfigConstants.SGW_L_URI_W));
                subsConfigDTO.setMapStatsReportIntvl(loaderResponse.get(KnSubConfigConstants.M_S_RP_I));
                subsConfigDTO.setLocExpTimeIntrvl(loaderResponse.get(KnSubConfigConstants.L_E_I));
            }
            if (subsConfigDTO.getProtocolVersion().matches(KnSubConfigConstants.PROTOCOL_VERSION_11_X)) {
                knLogger.debug(methodName, "protocol version - ", subsConfigDTO.getProtocolVersion());
                if (null != loaderResponse.get(KnSubConfigConstants.C_R_L) && !loaderResponse.get(KnSubConfigConstants.C_R_L).isEmpty()) {
                    subsConfigDTO.setClientRecLen(Integer.valueOf(loaderResponse.get(KnSubConfigConstants.C_R_L)));
                }
                if (null != loaderResponse.get(KnSubConfigConstants.PTTR_GRP_HT) && !loaderResponse.get(KnSubConfigConstants.PTTR_GRP_HT).isEmpty()) {
                    subsConfigDTO.setPttRadioClientGrpht(Integer.valueOf(loaderResponse.get(KnSubConfigConstants.PTTR_GRP_HT)));
                }
                if (null != loaderResponse.get(KnSubConfigConstants.PTTR_NGRP_HT) && !loaderResponse.get(KnSubConfigConstants.PTTR_NGRP_HT).isEmpty()) {
                    subsConfigDTO.setPttradioClientNongrpht(Integer.valueOf(loaderResponse.get(KnSubConfigConstants.PTTR_NGRP_HT)));
                }
                if (null != loaderResponse.get(KnSubConfigConstants.ACTIVE_GEO_FENCE_SIZE) && !(loaderResponse.get(KnSubConfigConstants.ACTIVE_GEO_FENCE_SIZE).isEmpty())) {
                    Double val = new Double(loaderResponse.get(KnSubConfigConstants.ACTIVE_GEO_FENCE_SIZE));
                    int intValue = val.intValue();
                    subsConfigDTO.setActiveGeoFencGrpSize(String.valueOf(intValue));
                }
                if (null != loaderResponse.get(KnSubConfigConstants.CQI_P) && !loaderResponse.get(KnSubConfigConstants.CQI_P).isEmpty()) {
                    subsConfigDTO.setCqiP(loaderResponse.get(KnSubConfigConstants.CQI_P));
                }
                if (null != loaderResponse.get(KnSubConfigConstants.CQI_T) && !loaderResponse.get(KnSubConfigConstants.CQI_T).isEmpty()) {
                    subsConfigDTO.setCqiT(loaderResponse.get(KnSubConfigConstants.CQI_T));
                }
                if (null != loaderResponse.get(KnSubConfigConstants.CQI_PW_T) && !(loaderResponse.get(KnSubConfigConstants.CQI_PW_T).isEmpty())) {
                    subsConfigDTO.setCqiPwT(Integer.valueOf(loaderResponse.get(KnSubConfigConstants.CQI_PW_T)));
                }
                if (null != loaderResponse.get(KnSubConfigConstants.CQI_MR) && !(loaderResponse.get(KnSubConfigConstants.CQI_MR).isEmpty())) {
                    subsConfigDTO.setCqiMR(Integer.valueOf(loaderResponse.get(KnSubConfigConstants.CQI_MR)));
                }
                if (null != loaderResponse.get(KnSubConfigConstants.CQI_DISP) && !(loaderResponse.get(KnSubConfigConstants.CQI_DISP).isEmpty())) {
                    subsConfigDTO.setCqiDisp(loaderResponse.get(KnSubConfigConstants.CQI_DISP));
                }
                String drxcSubs = loaderResponse.get(KnSubConfigConstants.DRX_C_SUBS);
                String drxcCorp = loaderResponse.get(KnSubConfigConstants.DRX_C_CORP);
                String drxcSys = loaderResponse.get(KnSubConfigConstants.DRX_C_SYSTEM);
                String drxcFinal = null;
                // if (!drxcSubs.startsWith(com.kodiak.common.resources.KnConstants.NULL) && !(drxcSubs.isEmpty())){
                if (!(drxcSubs.startsWith(com.kodiak.common.resources.KnConstants.NULL) || drxcSubs.startsWith("0")) && !(drxcSubs.isEmpty())) {
                    drxcFinal = drxcSubs.replaceAll(com.kodiak.common.resources.KnConstants.NULL, "");
                } else if (!drxcCorp.startsWith(com.kodiak.common.resources.KnConstants.NULL) && !(drxcCorp.isEmpty())) {
                    drxcFinal = drxcCorp.replaceAll(com.kodiak.common.resources.KnConstants.NULL, "");
                } else if (!drxcSys.startsWith(com.kodiak.common.resources.KnConstants.NULL) && !(drxcSys.isEmpty())) {
                    drxcFinal = drxcSys + com.kodiak.common.resources.KnConstants.COMMA + com.kodiak.common.resources.KnConstants.COMMA;
                }
                if (drxcFinal != null) {
                    String[] str = drxcFinal.split(com.kodiak.common.resources.KnConstants.COMMA);
                    Long fixedStart;
                    if (str.length > 2 && !str[2].isEmpty()) {
                        fixedStart = Long.valueOf(str[2]);
                        DateFormat dateFormat = new SimpleDateFormat(com.kodiak.common.resources.KnConstants.DATFORMAT);//yyyy-mm-dd'T'HH:mm:ssZ
                        String format = dateFormat.format(fixedStart);
                        String newFormat = format.substring(0, format.length() - 2) + com.kodiak.common.resources.KnConstants.COLON + format.substring(format.length() - 2);
                        str[2] = newFormat;
                        drxcFinal = str[0] + com.kodiak.common.resources.KnConstants.COMMA + str[1] + com.kodiak.common.resources.KnConstants.COMMA + newFormat + com.kodiak.common.resources.KnConstants.COMMA;
                    }
                    if (str.length > 3 && !str[3].isEmpty()) {
                        fixedStart = Long.valueOf(str[3]);
                        DateFormat dateFormat = new SimpleDateFormat(com.kodiak.common.resources.KnConstants.DATFORMAT);//yyyy-mm-dd'T'HH:mm:ssZ
                        String format = dateFormat.format(fixedStart);
                        String newFormat = format.substring(0, format.length() - 2) + com.kodiak.common.resources.KnConstants.COLON + format.substring(format.length() - 2);
                        str[3] = newFormat;
                        drxcFinal = str[0] + com.kodiak.common.resources.KnConstants.COMMA + str[1] + com.kodiak.common.resources.KnConstants.COMMA + str[2] + com.kodiak.common.resources.KnConstants.COMMA + str[3];
                    }
                    subsConfigDTO.setDrxC(drxcFinal);
                }
                if (loaderResponse.get(KnSubConfigConstants.WEBDISP_MAP_STATS_REPORT_INTVL) != null) {
                    subsConfigDTO.setWebDispMapStatReportIntvl(Integer.parseInt(loaderResponse.get(KnSubConfigConstants.WEBDISP_MAP_STATS_REPORT_INTVL)));
                }
                if (loaderResponse.get(KnSubConfigConstants.WEBDISP_UI_STATS_REPORT_INTVL) != null) {
                    subsConfigDTO.setWebDispUiStatReportIntvl(Integer.parseInt(loaderResponse.get(KnSubConfigConstants.WEBDISP_UI_STATS_REPORT_INTVL)));
                }
            }

            if (subsConfigDTO.getProtocolVersion().matches(KnSubConfigConstants.PROTOCOL_VERSION_12_X)) {
                int idenIpteropFlag = Integer.parseInt(loaderResponse.get(KnSubConfigConstants.IDEN_INTEROP));
                subsConfigDTO.setIdenIpteropFlag(idenIpteropFlag);
                if (subsProfileRespDTO.getUfmi() != null) {
                    subsConfigDTO.setUfmi(subsProfileRespDTO.getUfmi());
                }

            }

            if (subsConfigDTO.getProtocolVersion().matches(KnSubConfigConstants.PROTOCOL_VERSION_13_X)) {
                // Config framework always return as Double. So convering from Double to int. INT-25015
                subsConfigDTO.setMabg(new Double(loaderResponse.get(KnSubConfigConstants.M_AB_G)).intValue());
                subsConfigDTO.setMlabg(new Double(loaderResponse.get(KnSubConfigConstants.M_LAB_G)).intValue());
                subsConfigDTO.setMlabu(new Double(loaderResponse.get(KnSubConfigConstants.M_LAB_U)).intValue());
                subsConfigDTO.setNtgch(new Double(loaderResponse.get(KnSubConfigConstants.N_TG_CH)).intValue());
                subsConfigDTO.setNchzn(new Double(loaderResponse.get(KnSubConfigConstants.N_CH_ZN)).intValue());
                subsConfigDTO.setClientCap(clientFS2);
                subsConfigDTO.setRpn(loaderResponse.get(KnSubConfigConstants.R_P_N));
                subsConfigDTO.setRpe(loaderResponse.get(KnSubConfigConstants.R_P_E));
                int QPPPCRFProfileId = 0;
                if (null != loaderResponse.get(KnSubConfigConstants.QPPPackId_NZERO)) {
                    QPPPCRFProfileId = Integer.parseInt(loaderResponse.get(KnSubConfigConstants.QPPPackId_NZERO));
                }
                if (QPPPackId == 0 && null != loaderResponse.get(KnSubConfigConstants.QPPPackId_ZERO)) {
                    QPPPCRFProfileId = Integer.parseInt(loaderResponse.get(KnSubConfigConstants.QPPPackId_ZERO));
                }
                String rpe = genInfoUtil.getEmergencyResourcePriority(QPPPCRFProfileId, apnId, true, persisterTxn);
                if (rpe != null)
                    subsConfigDTO.setRpe(rpe);

                //INT-27124
                String logServerUriOidc = subsConfigDTO.getLgsrvuri().replace("logservice", "oidclogservice");
                subsConfigDTO.setLgsrvuri(logServerUriOidc);

                //INT-27124
                String logServerUriWifiOidc = subsConfigDTO.getLgsrvuriw().replace("logservice", "oidclogservice");
                subsConfigDTO.setLgsrvuriw(logServerUriWifiOidc);

                subsConfigDTO.setAbdgUriC(loaderResponse.get(KnSubConfigConstants.ABDG_URI_C) + "/abdg");
                subsConfigDTO.setAbdgUriW(loaderResponse.get(KnSubConfigConstants.ABDG_URI_W) + "/abdg");
                subsConfigDTO.setIntCorpId(subsProfileRespDTO.getCorpId());
                if (subsProfileRespDTO.getPublicSubscriptionType() == 1 && subsProfileRespDTO.getCorporateSubscriptionType() == 0) {
                    subsConfigDTO.setIntCorpId(0);
                }

                subsConfigDTO.setKuidPrefix(loaderResponse.get(KnSubConfigConstants.KUID_PREFIX));
                if (loaderResponse.get(KnSubConfigConstants.R_E_A_IND) != null) {
                    subsConfigDTO.setReaInd(Integer.parseInt(loaderResponse.get(KnSubConfigConstants.R_E_A_IND)));
                }
                if (loaderResponse.get(KnSubConfigConstants.E_TG_S_MODE) != null) {
                    subsConfigDTO.setEtgsMode(Integer.parseInt(loaderResponse.get(KnSubConfigConstants.E_TG_S_MODE)));
                }
                subsConfigDTO.setTpts(null);
                if (paramNameValueMap.get(KnConstants.MCPTT_CELLULAR_SIGNALING_TRANSPORT) != null) {
                    subsConfigDTO.setTpts(Integer.valueOf(paramNameValueMap.get(KnConstants.MCPTT_CELLULAR_SIGNALING_TRANSPORT)));
                }
                if (loaderResponse.get(KnSubConfigConstants.M_SM_DST) != null) {
                    subsConfigDTO.setMsmdst(Integer.parseInt(loaderResponse.get(KnSubConfigConstants.M_SM_DST)));
                }
                if (loaderResponse.get(KnSubConfigConstants.M_SM_LEN) != null) {
                    subsConfigDTO.setMsmlen(Integer.parseInt(loaderResponse.get(KnSubConfigConstants.M_SM_LEN)));
                }
                if (loaderResponse.get(KnSubConfigConstants.M_SM_CNT) != null) {
                    subsConfigDTO.setMsmcnt(Integer.parseInt(loaderResponse.get(KnSubConfigConstants.M_SM_CNT)));
                }
            }

            if (subsConfigDTO.getProtocolVersion().matches(KnSubConfigConstants.PROTOCOL_VERSION_14_X)) {
                if (loaderResponse.get(KnSubConfigConstants.M_DY_SS) != null) {
                    subsConfigDTO.setMdyss(Integer.parseInt(loaderResponse.get(KnSubConfigConstants.M_DY_SS).trim()));
                }
                if (loaderResponse.get(KnSubConfigConstants.M_DD_SS) != null) {
                    subsConfigDTO.setMddss(Integer.parseInt(loaderResponse.get(KnSubConfigConstants.M_DD_SS)));
                }
            }

            if (subsConfigDTO.getProtocolVersion().matches(KnSubConfigConstants.PROTOCOL_VERSION_29_X)) {
                subsConfigDTO.setIpvmulti(loaderResponse.get(KnSubConfigConstants.IP_V_MULTI));
                subsConfigDTO.setIpvprefmulti(loaderResponse.get(KnSubConfigConstants.IP_V_PREF_MULTI));
            }

            if (subsConfigDTO.getProtocolVersion().matches(KnSubConfigConstants.PROTOCOL_VERSION_16_X)) {
                subsConfigDTO.setEsriMapsCellularUri(loaderResponse.get(KnSubConfigConstants.ESRI_C_URI));
                subsConfigDTO.setEsriMapsGeoCellularUri(loaderResponse.get(KnSubConfigConstants.ESRI_G_C_URI));
                subsConfigDTO.setEsriMapsWifiUri(loaderResponse.get(KnSubConfigConstants.ESRI_W_URI));
                subsConfigDTO.setEsriMapsGeoWifiUri(loaderResponse.get(KnSubConfigConstants.ESRI_G_W_URI));
                subsConfigDTO.setEsriMapsClientId(loaderResponse.get(KnSubConfigConstants.ESRI_CL_ID));
                subsConfigDTO.setEsriMapsSecretKey(loaderResponse.get(KnSubConfigConstants.ESRI_SEC_KEY));
                subsConfigDTO.setFdsUriC(loaderResponse.get(KnSubConfigConstants.FDS_URI_C));
                subsConfigDTO.setFdsUriW(loaderResponse.get(KnSubConfigConstants.FDS_URI_W));
                if (loaderResponse.get(KnSubConfigConstants.OSM_F_M_L) != null) {
                    subsConfigDTO.setOsmfml(Integer.parseInt(loaderResponse.get(KnSubConfigConstants.OSM_F_M_L)));
                }
                if (loaderResponse.get(KnSubConfigConstants.V_M_FLR_IDLE_TIME) != null) {
                    subsConfigDTO.setVmflrIdleTime(
                            Integer.parseInt(loaderResponse.get(KnSubConfigConstants.V_M_FLR_IDLE_TIME)));
                }
                if (loaderResponse.get(KnSubConfigConstants.V_M_FLR_HLD_TIME) != null) {
                    subsConfigDTO.setVmflrHldTime(
                            Integer.parseInt(loaderResponse.get(KnSubConfigConstants.V_M_FLR_HLD_TIME)));
                }
                if (loaderResponse.get(KnSubConfigConstants.IS_PDS_E) != null) {
                    subsConfigDTO.setIspdse(Integer.parseInt(loaderResponse.get(KnSubConfigConstants.IS_PDS_E)));
                }
                if (loaderResponse.get(KnSubConfigConstants.PDS_M_C_L) != null) {
                    subsConfigDTO.setPdsmcl(Integer.parseInt(loaderResponse.get(KnSubConfigConstants.PDS_M_C_L)));
                }
                if (loaderResponse.get(KnSubConfigConstants.PDS_M_P_S) != null) {
                    subsConfigDTO.setPdsmps(Integer.parseInt(loaderResponse.get(KnSubConfigConstants.PDS_M_P_S)));
                }
                if (loaderResponse.get(KnSubConfigConstants.M_FL_TTL) != null) {
                    subsConfigDTO.setMflttl(Integer.parseInt(loaderResponse.get(KnSubConfigConstants.M_FL_TTL)));
                }
                if (loaderResponse.get(KnSubConfigConstants.M_SDS_S) != null) {
                    subsConfigDTO.setMsdss(Double.valueOf(loaderResponse.get(KnSubConfigConstants.M_SDS_S)).intValue());
                }
                if (loaderResponse.get(KnSubConfigConstants.M_FL_S) != null) {
                    subsConfigDTO.setMfls(Double.valueOf(loaderResponse.get(KnSubConfigConstants.M_FL_S)).intValue());
                }
                if (loaderResponse.get(KnSubConfigConstants.M_A_FL_S) != null) {
                    subsConfigDTO
                            .setMafls(Double.valueOf(loaderResponse.get(KnSubConfigConstants.M_A_FL_S)).intValue());
                }
                if (loaderResponse.get(KnSubConfigConstants.D_FL_TTL) != null) {
                    subsConfigDTO
                            .setDflttl(Double.valueOf(loaderResponse.get(KnSubConfigConstants.D_FL_TTL)).intValue());
                }
                if (loaderResponse.get(KnSubConfigConstants.M_MSG_TTL) != null) {
                    subsConfigDTO
                            .setMmsgttl(Double.valueOf(loaderResponse.get(KnSubConfigConstants.M_MSG_TTL)).intValue());
                }
                if (loaderResponse.get(KnSubConfigConstants.OSM_L_E_I) != null) {
                    subsConfigDTO
                            .setOsmlei(Double.valueOf(loaderResponse.get(KnSubConfigConstants.OSM_L_E_I)).intValue());
                }


            }

            if (subsConfigDTO.getProtocolVersion().matches(KnSubConfigConstants.PROTOCOL_VERSION_17_X)) {
                if (subsProfileRespDTO.getPublicSubscriptionType() == ONE
                        && subsProfileRespDTO.getCorporateSubscriptionType() == ZERO) {
                    if (loaderResponse.get(KnSubConfigConstants.A_CRTE_PC) != null) {
                        BitSet bitSet = BitSet.valueOf(loaderResponse.get(KnSubConfigConstants.A_CRTE_PC).getBytes());
                        if (bitSet.get(ZERO)) {
                            subsConfigDTO.setAcrtepc(ONE);
                        } else {
                            subsConfigDTO.setAcrtepc(ZERO);
                        }
                    }
                    if (loaderResponse.get(KnSubConfigConstants.A_CRTE_PG) != null) {
                        BitSet bitSet = BitSet.valueOf(loaderResponse.get(KnSubConfigConstants.A_CRTE_PG).getBytes());
                        if (bitSet.get(ZERO)) {
                            subsConfigDTO.setAcrtepg(ONE);
                        } else {
                            subsConfigDTO.setAcrtepg(ZERO);
                        }
                    }
                    if (loaderResponse.get(KnSubConfigConstants.A_CRTE_AG) != null) {
                        BitSet bitSet = BitSet.valueOf(loaderResponse.get(KnSubConfigConstants.A_CRTE_AG).getBytes());
                        if (bitSet.get(ZERO)) {
                            subsConfigDTO.setAcrteag(ONE);
                        } else {
                            subsConfigDTO.setAcrteag(ZERO);
                        }
                    }
                } else if (subsProfileRespDTO.getPublicSubscriptionType() == ZERO
                        && subsProfileRespDTO.getCorporateSubscriptionType() == ONE) {
                    if (loaderResponse.get(KnSubConfigConstants.A_CRTE_PC) != null) {
                        BitSet bitSet = BitSet.valueOf(loaderResponse.get(KnSubConfigConstants.A_CRTE_PC).getBytes());
                        if (bitSet.get(ONE)) {
                            subsConfigDTO.setAcrtepc(ONE);
                        } else {
                            subsConfigDTO.setAcrtepc(ZERO);
                        }
                    }
                    if (loaderResponse.get(KnSubConfigConstants.A_CRTE_PG) != null) {
                        BitSet bitSet = BitSet.valueOf(loaderResponse.get(KnSubConfigConstants.A_CRTE_PG).getBytes());
                        if (bitSet.get(ONE)) {
                            subsConfigDTO.setAcrtepg(ONE);
                        } else {
                            subsConfigDTO.setAcrtepg(ZERO);
                        }
                    }
                    if (loaderResponse.get(KnSubConfigConstants.A_CRTE_AG) != null) {
                        BitSet bitSet = BitSet.valueOf(loaderResponse.get(KnSubConfigConstants.A_CRTE_AG).getBytes());
                        if (bitSet.get(ONE)) {
                            subsConfigDTO.setAcrteag(ONE);
                        } else {
                            subsConfigDTO.setAcrteag(ZERO);
                        }
                    }
                } else if (subsProfileRespDTO.getPublicSubscriptionType() == ONE
                        && subsProfileRespDTO.getCorporateSubscriptionType() == ONE) {
                    if (loaderResponse.get(KnSubConfigConstants.A_CRTE_PC) != null) {
                        BitSet bitSet = BitSet.valueOf(loaderResponse.get(KnSubConfigConstants.A_CRTE_PC).getBytes());
                        if (bitSet.get(TWO)) {
                            subsConfigDTO.setAcrtepc(ONE);
                        } else {
                            subsConfigDTO.setAcrtepc(ZERO);
                        }
                    }
                    if (loaderResponse.get(KnSubConfigConstants.A_CRTE_PG) != null) {
                        BitSet bitSet = BitSet.valueOf(loaderResponse.get(KnSubConfigConstants.A_CRTE_PG).getBytes());
                        if (bitSet.get(TWO)) {
                            subsConfigDTO.setAcrtepg(ONE);
                        } else {
                            subsConfigDTO.setAcrtepg(ZERO);
                        }
                    }
                    if (loaderResponse.get(KnSubConfigConstants.A_CRTE_AG) != null) {
                        BitSet bitSet = BitSet.valueOf(loaderResponse.get(KnSubConfigConstants.A_CRTE_AG).getBytes());
                        if (bitSet.get(TWO)) {
                            subsConfigDTO.setAcrteag(ONE);
                        } else {
                            subsConfigDTO.setAcrteag(ZERO);
                        }
                    }
                }

            }

            if (subsConfigDTO.getProtocolVersion().matches(KnSubConfigConstants.PROTOCOL_VERSION_19_X)) {
                String cskUpladInterval = "240";
                String cskUploadDbValue = loaderResponse.get(KnSubConfigConstants.CSK_UPLOAD_INTERVAL);
                knLogger.info(methodName, "cskUploadDbValue", cskUploadDbValue);
                if (cskUploadDbValue != null && !cskUploadDbValue.equals("0")) {
                    cskUpladInterval = cskUploadDbValue;
                }
                subsConfigDTO.setCskUploadInterval(Integer.parseInt(cskUpladInterval));
                String activeFs2 = subsConfigDTO.getActiveFS2();
                boolean remotePushNotificationBit = KnGeneralUtil.getFeatureBitValue(activeFs2, FEATURE_SET.REMOTEPUSHNOTIFICATION.value());
                if (remotePushNotificationBit) {
                    String cskValidityInterval = "10080";
                    String cskValidityValue = loaderResponse.get(KnSubConfigConstants.CSK_VALIDITY_IOS);
                    knLogger.info(methodName, "cskValidityValue", cskValidityValue);
                    if (cskValidityValue != null && !cskValidityValue.equals("0")) {
                        cskValidityInterval = cskValidityValue;
                    }
                    subsConfigDTO.setCskUploadInterval(Integer.parseInt(cskValidityInterval));
                }

            }

            if (subsConfigDTO.getProtocolVersion().matches(KnSubConfigConstants.PROTOCOL_VERSION_19_X)) {
                String gmkOvlapInterval = "900";
                String gmkOvlapDbValue = loaderResponse.get(KnSubConfigConstants.GMK_OL_I);
                knLogger.info(methodName, "gmkOvlapDbValue", gmkOvlapDbValue);
                if (gmkOvlapDbValue != null && !gmkOvlapDbValue.equals("0")) {
                    gmkOvlapInterval = gmkOvlapDbValue;
                }
                subsConfigDTO.setGmkoli(Integer.parseInt(gmkOvlapInterval));
            }

            if (subsConfigDTO.getProtocolVersion().matches(KnSubConfigConstants.PROTOCOL_VERSION_20_X)) {
                knLogger.debug(methodName, "Inside PV:20");
                if (loaderResponse.get(KnSubConfigConstants.USR_KEY_MAT_OL_P) != null) {
                    subsConfigDTO.setUsrkeymatolp(Integer.parseInt(loaderResponse.get(KnSubConfigConstants.USR_KEY_MAT_OL_P)));
                }
                if (loaderResponse.get(KnSubConfigConstants.CSK_SKEW) != null) {
                    subsConfigDTO.setCskskew(Integer.parseInt(loaderResponse.get(KnSubConfigConstants.CSK_SKEW)));
                }
                if (loaderResponse.get(KnSubConfigConstants.PCK_SKEW) != null) {
                    subsConfigDTO.setPckskew(Integer.parseInt(loaderResponse.get(KnSubConfigConstants.PCK_SKEW)));
                }
                if (loaderResponse.get(KnSubConfigConstants.GMK_SKEW) != null) {
                    subsConfigDTO.setGmkskew(Integer.parseInt(loaderResponse.get(KnSubConfigConstants.GMK_SKEW)));
                }
                if (loaderResponse.get(KnSubConfigConstants.STR_ENCRYPT) != null) {
                    subsConfigDTO.setStrencrypt(Integer.parseInt(loaderResponse.get(KnSubConfigConstants.STR_ENCRYPT)));
                }
                if (loaderResponse.get(KnSubConfigConstants.MDSI_URI) != null) {
                    subsConfigDTO.setMdsiUri(loaderResponse.get(KnSubConfigConstants.MDSI_URI));
                }
                if (loaderResponse.get(KnSubConfigConstants.GMS_URI) != null) {
                    subsConfigDTO.setGmsUri(loaderResponse.get(KnSubConfigConstants.GMS_URI));
                }
                if (loaderResponse.get(KnSubConfigConstants.MSGSTORE_URI_C) != null) {
                    subsConfigDTO.setMsgstoreuric(loaderResponse.get(KnSubConfigConstants.MSGSTORE_URI_C) + MESSAGE_STORE_API);
                }
                if (loaderResponse.get(KnSubConfigConstants.MSGSTORE_URI_W) != null) {
                    subsConfigDTO.setMsgstoreuriw(loaderResponse.get(KnSubConfigConstants.MSGSTORE_URI_W) + MESSAGE_STORE_API);
                }
                if (loaderResponse.get(KnSubConfigConstants.M_SIMUL_SDS_TXNS) != null) {
                    subsConfigDTO.setMsimulsdstxns(Integer.parseInt(loaderResponse.get(KnSubConfigConstants.M_SIMUL_SDS_TXNS)));
                }
                if (loaderResponse.get(KnSubConfigConstants.M_SIMUL_FD_TXNS) != null) {
                    subsConfigDTO.setMsimulfdtxns(Integer.parseInt(loaderResponse.get(KnSubConfigConstants.M_SIMUL_FD_TXNS)));
                }
                if (loaderResponse.get(KnSubConfigConstants.M_SRCH_ENTRIES) != null) {
                    subsConfigDTO.setMsrchentries(Integer.parseInt(loaderResponse.get(KnSubConfigConstants.M_SRCH_ENTRIES)));
                }

            }
            if (subsConfigDTO.getProtocolVersion().matches(KnSubConfigConstants.PROTOCOL_VERSION_21_X)) {
                    knLogger.debug(methodName, "Inside PV:21");
                    if (loaderResponse.get(KnSubConfigConstants.RECORDING_STATUS) != null) {
                        subsConfigDTO.setRecordingStatus(Integer.parseInt(loaderResponse.get(KnSubConfigConstants.RECORDING_STATUS)));
                    }
            }

            if (subsConfigDTO.getProtocolVersion().matches(KnSubConfigConstants.PROTOCOL_VERSION_23_X)) {
                knLogger.debug(methodName, "Inside PV:23");
                if (loaderResponse.get(KnSubConfigConstants.MAX_VIDEO_SESSIONS) != null) {
                    subsConfigDTO.setMaxVideoSessions(Integer.parseInt(loaderResponse.get(KnSubConfigConstants.MAX_VIDEO_SESSIONS)));
                }
            }

            if (subsConfigDTO.getProtocolVersion().matches(KnSubConfigConstants.PROTOCOL_VERSION_24_X)) {
                knLogger.debug(methodName, "Inside PV:24");
                if (loaderResponse.get(KnSubConfigConstants.MAX_VIDEO_SESSIONS) != null) {
                    subsConfigDTO.setMaxVideoSessions(Integer.parseInt(loaderResponse.get(KnSubConfigConstants.MAX_VIDEO_SESSIONS)));
                }
                if (loaderResponse.get(KnSubConfigConstants.M_MEM_USR_REGRP) != null) {
                    subsConfigDTO.setmMemUsrRegrp(Integer.parseInt(loaderResponse.get(KnSubConfigConstants.M_MEM_USR_REGRP)));
                }
                if (loaderResponse.get(KnSubConfigConstants.M_USR_REGRPS) != null) {
                    subsConfigDTO.setmUsrRegrps(Integer.parseInt(loaderResponse.get(KnSubConfigConstants.M_USR_REGRPS)));
                }
                String btfDurationDefault = loaderResponse.get(KnSubConfigConstants.BTF_DURATION_DEFAULT);
                String btfDurationMin = loaderResponse.get(KnSubConfigConstants.BTF_DURATION_MIN);
                String btfDurationMax = loaderResponse.get(KnSubConfigConstants.BTF_DURATION_MAX);
                StringBuilder btfDuration = new StringBuilder();
                btfDuration.append(btfDurationDefault).
                        append(com.kodiak.common.resources.KnConstants.SEMICOLON).
                        append(btfDurationMin).
                        append(com.kodiak.common.resources.KnConstants.SEMICOLON).
                        append(btfDurationMax);
                knLogger.debug(methodName,"btfDuration:",btfDuration);
                if (btfDuration!= null) {
                    subsConfigDTO.setBtfDuration(btfDuration.toString());
                }
                if (loaderResponse.get(KnSubConfigConstants.BTF_TONE_LIST) != null) {
                    subsConfigDTO.setBtfToneList(loaderResponse.get(KnSubConfigConstants.BTF_TONE_LIST));
                }
                if (loaderResponse.get(KnSubConfigConstants.BTF_TONE_PAUSE_INTERVALS) != null) {
                    subsConfigDTO.setBtfTonePauseIntervals(loaderResponse.get(KnSubConfigConstants.BTF_TONE_PAUSE_INTERVALS));
                }
                if (loaderResponse.get(KnSubConfigConstants.M_BTF_PER_OWNER) != null) {
                    subsConfigDTO.setmBtfPerOwner(Integer.parseInt(loaderResponse.get(KnSubConfigConstants.M_BTF_PER_OWNER)));
                }
                if (loaderResponse.get(KnSubConfigConstants.GMS_SERV_ID) != null) {
                    subsConfigDTO.setGmsServId(loaderResponse.get(KnSubConfigConstants.GMS_SERV_ID));
                }
                if (loaderResponse.get(KnSubConfigConstants.M_GRP_REGRPS) != null) {
                    subsConfigDTO.setmGrpRegrps(Integer.parseInt(loaderResponse.get(KnSubConfigConstants.M_GRP_REGRPS)));
                }
            }
            if (subsConfigDTO.getProtocolVersion().matches(KnSubConfigConstants.PROTOCOL_VERSION_25_X)) {
                knLogger.debug(methodName, "Inside PV:25");
                if (loaderResponse.get(KnSubConfigConstants.ALTITUDE_FLAG) != null) {
                    subsConfigDTO.setAltitudeFlag(Integer.parseInt(loaderResponse.get(KnSubConfigConstants.ALTITUDE_FLAG)));
                }
                if (loaderResponse.get(KnSubConfigConstants.VERTICALACCURACY_FLAG) != null) {
                    subsConfigDTO.setVerticalAccuracyFlag(Integer.parseInt(loaderResponse.get(KnSubConfigConstants.VERTICALACCURACY_FLAG)));
                }
                if (loaderResponse.get(KnSubConfigConstants.MEDIA_MISSING_TIMER) != null) {
                    subsConfigDTO.setMediaMissingTimer(Integer.parseInt(loaderResponse.get(KnSubConfigConstants.MEDIA_MISSING_TIMER)));
                }
                if (loaderResponse.get(KnSubConfigConstants.MEDIA_MISSING_TIMER) != null) {
                    subsConfigDTO.setMediaMissingTimer(Integer.parseInt(loaderResponse.get(KnSubConfigConstants.MEDIA_MISSING_TIMER)));
                }
                //genInfoUtil.getXCAPRootURI(mdn, persisterTxn)

                KnPocSubsAddlInfoDTO addlDetails = xdmServerDAO.getSubsAddlDetails(mdn, persisterTxn);
                String pttSettingId = addlDetails.getPttSettingDocId();
               // subsConfigDTO.set
            }

            if (subsConfigDTO.getProtocolVersion().matches(KnSubConfigConstants.PROTOCOL_VERSION_29_X)) {
                knLogger.debug(methodName, "Inside PV:29+ (MBMS multicast-ka-interval)");
                if (loaderResponse.get(KnSubConfigConstants.MULTICAST_KA_INTERVAL) != null && !loaderResponse.get(KnSubConfigConstants.MULTICAST_KA_INTERVAL).isEmpty()) {
                    try {
                        subsConfigDTO.setMulticastKaInterval(Integer.parseInt(loaderResponse.get(KnSubConfigConstants.MULTICAST_KA_INTERVAL)));
                    } catch (NumberFormatException e) {
                        knLogger.error(methodName, "Invalid value for MULTICAST_KA_INTERVAL: ", loaderResponse.get(KnSubConfigConstants.MULTICAST_KA_INTERVAL));
                    }
                }
            }

//common keysgetSubscriberConfigDoc(ISubscriberDTO, KnPersisterTxn )
          if(subsConfigDTO.getProtocolVersion().matches(KnSubConfigConstants.PROTOCOL_VERSION_13_X)) {
              subsConfigDTO.setXcapRootUri(loaderResponse.get(KnSubConfigConstants.XCAP_ROOT_URI) + com.kodiak.common.resources.KnConstants.OIDC_XCAP_ROOT_CONTEXT);
          }else{
              subsConfigDTO.setXcapRootUri(loaderResponse.get(KnSubConfigConstants.XCAP_ROOT_URI) + com.kodiak.common.resources.KnConstants.XCAP_ROOT_CONTEXT);
          }
            //subsConfigDTO.setMaxPublicContacts(xdmsSvcConfigDTO.getMaxPublicContactsPerSubs());
            subsConfigDTO.setMaxPublicContacts(convertInteger(loaderResponse.get(KnSubConfigConstants.MAX_PUBLIC_CONTACTS)));
            // subsConfigDTO.setMaxCorporateContacts(xdmsSvcConfigDTO.getMaxCorpContactsPerSubs());
            //newMaxCorpContacts added for XDM-4635
            Map<String, String> microServicesParamNameValueMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
            Integer commonContactListSize = Integer.parseInt(microServicesParamNameValueMap.get("COMMON_CONTACTLIST_SIZE"));
            knLogger.debug(methodName,"commonContactListSize: ",commonContactListSize);
            boolean ifCommonContactListEnabled = KnGeneralUtil.getFeatureBitValue(activeFS2, FEATURE_SET.COMMON_CONTACT_LIST.value());
            knLogger.debug(methodName,"ifCommonContactListEnabled: ",ifCommonContactListEnabled);
            if(ifCommonContactListEnabled){
                Integer newMaxCorpContacts = convertInteger(loaderResponse.get(KnSubConfigConstants.MAX_CORPORATE_CONTACTS)) + commonContactListSize;
                knLogger.debug(methodName,"newMaxCorpContacts: ",newMaxCorpContacts);
                subsConfigDTO.setMaxCorporateContacts(newMaxCorpContacts);
            }
            else{
                subsConfigDTO.setMaxCorporateContacts(convertInteger(loaderResponse.get(KnSubConfigConstants.MAX_CORPORATE_CONTACTS)));
            }
            //subsConfigDTO.setMaxPublicGroups(xdmsSvcConfigDTO.getMaxPublicPOCGrpsPerSubs());
            subsConfigDTO.setMaxPublicGroups(convertInteger(loaderResponse.get(KnSubConfigConstants.MAX_PUBLIC_GROUPS)));
            //subsConfigDTO.setMaxCorporateGroups(xdmsSvcConfigDTO.getMaxCorpGrpsPerSubs());
            boolean largeDispatchBitEnabled = KnGeneralUtil.getFeatureBitValue(corpAdminFS2, FEATURE_SET.LARGE_AGENCY_DISPATCH.value());
            subsConfigDTO.setMaxCorporateGroups(convertInteger(loaderResponse.get(KnSubConfigConstants.MAX_CORPORATE_GROUPS)));
            if (largeDispatchBitEnabled && (null != loaderResponse.get(KnSubConfigConstants.MAX_CORPORATE_GROUPS_LARGE_DISPATCH))) {
                subsConfigDTO.setMaxCorporateGroups(convertInteger(loaderResponse.get(KnSubConfigConstants.MAX_CORPORATE_GROUPS_LARGE_DISPATCH)));
            }
            //subsConfigDTO.setMaxMembersPerPublicGroup(xdmsSvcConfigDTO.getMaxMembersPerPublicPOCGrp());
            subsConfigDTO.setMaxMembersPerPublicGroup(convertInteger(loaderResponse.get(KnSubConfigConstants.MAX_MEMBERS_PER_PUBLIC_GROUP)));
            //subsConfigDTO.setMaxAdhocGroupSize(pocSvcConfigDTO.getMaxLegsInAdhocGrpCall());
            subsConfigDTO.setMaxAdhocGroupSize(convertInteger(loaderResponse.get(KnSubConfigConstants.MAX_ADHOC_GROUP_SIZE)));
            //subsConfigDTO.setConferenceFactoryUri(pocSvcConfigDTO.getPOC_ConfFactoryURI());
            subsConfigDTO.setConferenceFactoryUri(loaderResponse.get(KnSubConfigConstants.CONFERENCE_FACTORY_URI));
            //subsConfigDTO.setTbcpReleaseTimer(pocSvcConfigDTO.getTBCPReqTimeout());
            subsConfigDTO.setTbcpReleaseTimer(convertInteger(loaderResponse.get(KnSubConfigConstants.TBCP_REQUEST_TIMER)));
//            subsConfigDTO.setTbcpRequestTimer(pocSvcConfigDTO.getTBCPReqTimeout());
            subsConfigDTO.setTbcpRequestTimer(convertInteger(loaderResponse.get(KnSubConfigConstants.TBCP_REQUEST_TIMER)));
            //subsConfigDTO.setMediaEndTimer(pocSvcConfigDTO.getMediaIdleTimer());
            subsConfigDTO.setMediaEndTimer(convertInteger(loaderResponse.get(KnSubConfigConstants.MEDIA_END_TIMER)));
            //subsConfigDTO.setMediaIdleTimer(pocSvcConfigDTO.getFloorIdleDetectionTimer());
            subsConfigDTO.setMediaIdleTimer(convertInteger(loaderResponse.get(KnSubConfigConstants.MEDIA_IDLE_TIMER)));
            //subsConfigDTO.setNumKaMediaPackets(pocSvcConfigDTO.getNumMediaPortKAMsgs());
            subsConfigDTO.setNumKaMediaPackets(convertInteger(loaderResponse.get(KnSubConfigConstants.NUM_KA_MEDIA_PACKETS)));
            //subsConfigDTO.setMediaPayloadLength(pocSvcConfigDTO.getMediaPortKAMsgSize());
            subsConfigDTO.setMediaPayloadLength(convertInteger(loaderResponse.get(KnSubConfigConstants.MEDIA_PAYLOAD_LENGTH)));
            //subsConfigDTO.setKaPacketSize(pocSvcConfigDTO.getMediaPortKAMsgSize());
            subsConfigDTO.setKaPacketSize(convertInteger(loaderResponse.get(KnSubConfigConstants.KA_PACKET_SIZE)));
            //subsConfigDTO.setNumOfTbcpRetries(pocSvcConfigDTO.getNumTBCPRetriesByClient());
            subsConfigDTO.setNumOfTbcpRetries(convertInteger(loaderResponse.get(KnSubConfigConstants.NUMBER_OF_TBCP_RETRIES)));
            //subsConfigDTO.setPresencePublishThrottleTimer(preServConfigDTO.getPresencePublishThrottleTimer());
            subsConfigDTO.setPresencePublishThrottleTimer(convertInteger(loaderResponse.get(KnSubConfigConstants.PRESENCE_PUBLISH_THROTTLE_TIMER)));
            //subsConfigDTO.setOctetSize(instaPOCSrvcCfgDTO.getNumOctetsPerWakeupMsg());
            subsConfigDTO.setOctetSize(convertInteger(loaderResponse.get(KnSubConfigConstants.OCTET_SIZE)));
            //subsConfigDTO.setNumOfBurstPerTrigger(instaPOCSrvcCfgDTO.getNumWakeupMsgsPerBurst());
            subsConfigDTO.setNumOfBurstPerTrigger(convertInteger(loaderResponse.get(KnSubConfigConstants.NUM_OF_BURST_PER_TRIGGER)));
            //subsConfigDTO.setXcapRootUri(xdmsSvcConfigDTO.getXcapRootUri());

            subsConfigDTO.setMediaIntraburstInterval(KnProvConstants.DEFAULT_MEDIA_INTRABURST_INTERVAL);


            //location timer
            //subsConfigDTO.setLocationDebouncingTimer(pocRegistrarSrvcConfigDTO.getLocationDebounceTimer());
            subsConfigDTO.setLocationDebouncingTimer(convertInteger(loaderResponse.get(KnSubConfigConstants.LOCATION_DEBOUNCING_TIMER)));
            // getting doc config info from XDMS Service Config
            //     KnXDMSServiceConfigDTO xdmsSvcConfigDTO = genInfoUtil.retrieveXDMSServiceConfig(xdmPttServerId, persisterTxn);
            //conference uri template
            // subsConfigDTO.setConferenceUriTemplate(xdmsSvcConfigDTO.getPublicPocGrpConfURITemplate());
            subsConfigDTO.setConferenceUriTemplate(loaderResponse.get(KnSubConfigConstants.CONFERENCE_URI_TEMPLATE));
            subsConfigDTO.setNumOfRetries(KnProvConstants.DEFAULT_NUM_OF_RETRIES);

            //subsConfigDTO.setPrimaryPresenceUri(loaderResponse.get(KnSubConfigConstants.PRIMARY_RLS_ROUTE));
            //  subsConfigDTO.setGeoPresenceUri(loaderResponse.get(KnSubConfigConstants.GEO_RLS_ROUTE));

            KnPOCSvcConfigDTO pocSvcConfigDTO = provInfoUtil.retrievePOCSvcConfig(pocPttServerId);

            //subsConfigDTO.setPrimaryIpaUri(provInfoUtil.updateConfigRouteUri(pocSvcConfigDTO.getPrimaryPOCServerURI()));
            subsConfigDTO.setPrimaryIpaUri(provInfoUtil.updateConfigRouteUri(loaderResponse.get(KnSubConfigConstants.PRIMARY_SESSION_ROUTE)));
            //subsConfigDTO.setMaxTalkBurstDuration(pocSvcConfigDTO.getMaxFloorHoldDuration());
            subsConfigDTO.setMaxTalkBurstDuration(convertInteger(loaderResponse.get(KnSubConfigConstants.MAX_TALKBURST_DURATION)));
            if (pocSvcConfigDTO.getEnableInstaPOC() == 0) {
                subsConfigDTO.setInstaPoc(KnProvConstants.FALSE);
            } else {
                subsConfigDTO.setInstaPoc(KnProvConstants.TRUE);
            }

            //subsConfigDTO.setSessionRecoveryTimer1(pocSvcConfigDTO.getSipRetryTimer());
            subsConfigDTO.setSessionRecoveryTimer1((convertInteger(loaderResponse.get(KnSubConfigConstants.SESSION_RECOVERY_TIMER))));
            if (pocSvcConfigDTO.getSipRetryTimerBackOff() == 1) {
                /* subsConfigDTO.setSessionRecoveryTimer2(2 * pocSvcConfigDTO.getSipRetryTimer());
              subsConfigDTO.setSessionRecoveryTimer3(4 * pocSvcConfigDTO.getSipRetryTimer());
              subsConfigDTO.setSessionRecoveryTimer4(8 * pocSvcConfigDTO.getSipRetryTimer());
              subsConfigDTO.setSessionRecoveryTimer5(16 * pocSvcConfigDTO.getSipRetryTimer());
              subsConfigDTO.setSessionRecoveryTimer6(32 * pocSvcConfigDTO.getSipRetryTimer());*/
                subsConfigDTO.setSessionRecoveryTimer2(2 * convertInteger(loaderResponse.get(KnSubConfigConstants.SESSION_RECOVERY_TIMER)));
                subsConfigDTO.setSessionRecoveryTimer3(4 * convertInteger(loaderResponse.get(KnSubConfigConstants.SESSION_RECOVERY_TIMER)));
                subsConfigDTO.setSessionRecoveryTimer4(8 * convertInteger(loaderResponse.get(KnSubConfigConstants.SESSION_RECOVERY_TIMER)));
                subsConfigDTO.setSessionRecoveryTimer5(16 * convertInteger(loaderResponse.get(KnSubConfigConstants.SESSION_RECOVERY_TIMER)));
                subsConfigDTO.setSessionRecoveryTimer6(32 * convertInteger(loaderResponse.get(KnSubConfigConstants.SESSION_RECOVERY_TIMER)));
            }else{
                subsConfigDTO.setSessionRecoveryTimer2(convertInteger(loaderResponse.get(KnSubConfigConstants.SESSION_RECOVERY_TIMER2)));
                subsConfigDTO.setSessionRecoveryTimer3(convertInteger(loaderResponse.get(KnSubConfigConstants.SESSION_RECOVERY_TIMER3)));
                subsConfigDTO.setSessionRecoveryTimer4(convertInteger(loaderResponse.get(KnSubConfigConstants.SESSION_RECOVERY_TIMER4)));
                subsConfigDTO.setSessionRecoveryTimer5(convertInteger(loaderResponse.get(KnSubConfigConstants.SESSION_RECOVERY_TIMER5)));
                subsConfigDTO.setSessionRecoveryTimer6(convertInteger(loaderResponse.get(KnSubConfigConstants.SESSION_RECOVERY_TIMER6)));
            }
            // subsConfigDTO.setIpDebouncerTimer(pocSvcConfigDTO.getIpDebounceTimer());
            subsConfigDTO.setIpDebouncerTimer(convertInteger(loaderResponse.get(KnSubConfigConstants.IP_DEBOUNCE_TIMER)));
            //subsConfigDTO.setTbcpRequestTimer(pocSvcConfigDTO.getClient_TBCPFloorReqRetryTimer());
            subsConfigDTO.setTbcpRequestTimer(convertInteger(loaderResponse.get(KnSubConfigConstants.TBCP_REQUEST_RETRY_TIMER)));
            subsConfigDTO.setTbcpRequestRetryTimer(convertInteger(loaderResponse.get(KnSubConfigConstants.TBCP_REQUEST_RETRY_TIMER)));
            //subsConfigDTO.setTbcpReleaseRetryTimer(pocSvcConfigDTO.getClient_TBCPRelReqRetryTimer());
            subsConfigDTO.setTbcpReleaseRetryTimer(convertInteger(loaderResponse.get(KnSubConfigConstants.TBCP_RELEASE_RETRY_TIMER)));
            subsConfigDTO.setIpDebouncerTimer(pocSvcConfigDTO.getIpDebounceTimer());
            /* subsConfigDTO.setTbcpRequestTimer(pocSvcConfigDTO.getClient_TBCPFloorReqRetryTimer());
            subsConfigDTO.setTbcpReleaseRetryTimer(pocSvcConfigDTO.getClient_TBCPRelReqRetryTimer());*/
            // int floorRecoveryTimer = pocSvcConfigDTO.getMaxFloorHoldDuration() + pocSvcConfigDTO.getMaxTalkBurstGraceTime();
            // subsConfigDTO.setFloorRecoveryTimer(floorRecoveryTimer);
            subsConfigDTO.setFloorRecoveryTimer(convertInteger(loaderResponse.get(KnSubConfigConstants.FLOOR_RECOVERY_TIMER)));

//=== common end ..
            //deprecated in 5.x ..
            if (subsConfigDTO.getProtocolVersion() != null && !subsConfigDTO.getProtocolVersion().matches(KnSubConfigConstants.PROTOCOL_VERSION_5_X) &&
                    !subsConfigDTO.getProtocolVersion().matches(KnSubConfigConstants.PROTOCOL_VERSION_6_X)) {
                knLogger.info(methodName, " Version ..", subsConfigDTO.getProtocolVersion());
                //subsConfigDTO.setPrimaryIpaUri(provInfoUtil.updateConfigRouteUri(pocSvcConfigDTO.getPrimaryPOCServerURI()));
                subsConfigDTO.setPrimaryIpaUri(provInfoUtil.updateConfigRouteUri(loaderResponse.get(KnSubConfigConstants.PRIMARY_IPA_ROUTE)));
                //  subsConfigDTO.setGeoPresenceUri(KnProvConstants.DUMMY + provInfoUtil.updateConfigRouteUri(preServConfigDTO.getGeoPresenceServerURI()));
                subsConfigDTO.setPrimaryRlsUri(provInfoUtil.updateConfigRouteUri(loaderResponse.get(KnSubConfigConstants.PRIMARY_RLS_ROUTE)));
                //subsConfigDTO.setPrimaryRegistrarUri(provInfoUtil.updateConfigRouteUri(pocRegistrarSrvcConfigDTO.getPrimaryRegistrarURI()));
                subsConfigDTO.setPrimaryRegistrarUri(provInfoUtil.updateConfigRouteUri(loaderResponse.get(KnSubConfigConstants.PRIMARY_REGISTRAR_ROUTE)));
                //subsConfigDTO.setPrimaryPocSettingUri(provInfoUtil.updateConfigRouteUri(pocSvcConfigDTO.getPrimaryPOCServerURI()));
                subsConfigDTO.setPrimaryPocSettingUri(provInfoUtil.updateConfigRouteUri(loaderResponse.get(KnSubConfigConstants.PRIMARY_SESSION_ROUTE)));
                //subsConfigDTO.setPreCallNumKaMediaPackets(pocSvcConfigDTO.getPreCallNumMediaPortKAMsgs());
                subsConfigDTO.setPreCallNumKaMediaPackets(convertInteger(loaderResponse.get(KnSubConfigConstants.PRECALL_NUM_KA_MEDIA_PACKETS)));
                //subsConfigDTO.setPreCallKaPacketSize(pocSvcConfigDTO.getPrecallMediaPortKAMsgSize());
                subsConfigDTO.setPreCallKaPacketSize(convertInteger(loaderResponse.get(KnSubConfigConstants.PRECALL_KA_PACKET_SIZE)));
                //subsConfigDTO.setPreCallKaInterval(pocSvcConfigDTO.getPreCallMediaPortKAInterval());
                subsConfigDTO.setPreCallKaInterval(convertInteger(loaderResponse.get(KnSubConfigConstants.PRECALL_KA_INTERVAL)));
                //subsConfigDTO.setKaInterval(pocSvcConfigDTO.getMediaPortKAInterval());
                subsConfigDTO.setKaInterval(convertInteger(loaderResponse.get(KnSubConfigConstants.INCALL_KA_INTERVAL)));
                // subsConfigDTO.setPreCallKaDuration(pocSvcConfigDTO.getPreCallMediaKADuration());
                subsConfigDTO.setPreCallKaDuration(convertInteger(loaderResponse.get(KnSubConfigConstants.PRECALL_KA_DURATION)));
                KnLocationServiceConfigDTO locationServiceDTO = provInfoUtil.retrieveLocationServiceConfig(presencePttServerId);
                int loc_svc_enabled = locationServiceDTO.getLocation_Svc_Enabled();
                if (loc_svc_enabled == 1 && subsProfileRespDTO.getDispatchGroupMember() == 1) {
                    subsConfigDTO.setLocationPublishInterval(locationServiceDTO.getLocationPublishInterval());
                } else {
                    subsConfigDTO.setLocationPublishInterval(0);
                }
                subsConfigDTO.setRoamingAllowed(KnProvConstants.TRUE);
            }
            subsConfigDTO.setMediaSecureSessionRefreshIntvl(pocSvcConfigDTO.getMediaSecureSesRefIntvl());
            subsConfigDTO.setClientInCallSuspendTimer(pocSvcConfigDTO.getClientInCallSusTimer());
            subsConfigDTO.setRegisterTimer(convertInteger(loaderResponse.get(KnSubConfigConstants.REGISTER_TIMER)));
            // subsConfigDTO.setRegisterTimer(pocRegistrarSrvcConfigDTO.getMaxRegisterExpiryTimeDuration());
            //subsConfigDTO.setTuDownTimer(pocRegistrarSrvcConfigDTO.getTuDownTimer());
            subsConfigDTO.setTuDownTimer(Long.parseLong(loaderResponse.get(KnSubConfigConstants.TU_DOWN_TIMER)));
            //subsConfigDTO.setTuUpTimerStartVal(pocRegistrarSrvcConfigDTO.getTuUpTimerStartVal());
            subsConfigDTO.setTuUpTimerStartVal(Long.parseLong(loaderResponse.get(KnSubConfigConstants.TU_UP_TIMER_START_VAL)));
            //subsConfigDTO.setTuUpTimerMaxVal(pocRegistrarSrvcConfigDTO.getTuUpTimerMaxVal());
            subsConfigDTO.setTuUpTimerMaxVal(Long.parseLong(loaderResponse.get(KnSubConfigConstants.TU_UP_TIMER_MAX_VAL)));
            //subsConfigDTO.setTuUpTimerRampDownPeriod(pocRegistrarSrvcConfigDTO.getTuUpTimerRampDownPeriod());
            subsConfigDTO.setTuUpTimerRampDownPeriod(Long.parseLong(loaderResponse.get(KnSubConfigConstants.TU_UP_TIMER_RAMP_DOWN_PERIOD)));
            //subsConfigDTO.setTuForceOnlineMaxWaitTimer(pocRegistrarSrvcConfigDTO.getTuForceOnlineMaxWaitTimer());
            subsConfigDTO.setTuForceOnlineMaxWaitTimer(Long.parseLong(loaderResponse.get(KnSubConfigConstants.TU_FORCE_ONLINE_MAX_WAIT_TIMER)));
            //backward compatibility
            // gettting doc config info from   DG.FEATUREACCESSNUMBERINFO
            //as per subscriberConfigParamaeter xls document  FEATUREACCESSINDEX is 52
            //  KnFeatureAccessNumberInfoDTO featureAccessNumberInfoDTO = provInfoUtil.retrieveFeatureAccessNumberInfo(KnConstants.FEATUREACCESSINDEX);
            //subsConfigDTO.setTuSmsAddress(featureAccessNumberInfoDTO.getAccessNumber());
            subsConfigDTO.setTuSmsAddress(loaderResponse.get(KnSubConfigConstants.TU_SMS_ADDRESS));
            //   KnFeatureAccessInfoDTO featureAccessInfoDTO = provInfoUtil.retrieveFeatureAccessInfo(KnConstants.FEATUREACCESSINDEX);
            //subsConfigDTO.setTuSmsAddressTon(featureAccessInfoDTO.getTon());
            subsConfigDTO.setTuSmsAddressTon(convertInteger(loaderResponse.get(KnSubConfigConstants.TU_SMS_ADDRESS_TON)));
            //subsConfigDTO.setMaxMembersPerCorpGroup(xdmsSvcConfigDTO.getMaxMembersPerCorpPOCGrp());
            subsConfigDTO.setMaxMembersPerCorpGroup(convertInteger(loaderResponse.get(KnSubConfigConstants.MAX_MEMBERS_PER_CORP_GROUP)));
            // getting doc config info from Dial Plan Info
            // getting doc config info from Dial Plan Info
            INormalizeIntf normalize = KnNormalizeImpl.getInstance();
            KnDialPlanConfigDTO dialPlanInfoDTO = normalize.getDialPlanInfo(mdn);
            String countryCode = null;
            if (dialPlanInfoDTO.getCountryCode() != null) {
                countryCode = dialPlanInfoDTO.getCountryCode().trim();
            }

            String nationalPrefix = null;
            if (dialPlanInfoDTO.getNatlPrefix() != null) {
                nationalPrefix = dialPlanInfoDTO.getNatlPrefix().trim();
            }

            String internationalPrefix = null;
            if (dialPlanInfoDTO.getIntlPrefix() != null) {
                internationalPrefix = dialPlanInfoDTO.getIntlPrefix().trim();
            }
            subsConfigDTO.setDialPlanInfo(internationalPrefix + ";" + nationalPrefix + ";" + countryCode);

            // getting doc config info from XDMS Doc SubscriberPrefix Config
            KnXDMSDocSubPrxConfigDTO xdmsDocSubPrxConfigDTO = provInfoUtil.retrieveXDMSDocSubPrxConfig(presencePttServerId);
            subsConfigDTO.setPrimarySubscriptionProxyUri(xdmsDocSubPrxConfigDTO.getPrimaryDocSubPrxURI());
            // getting doc config info from Insta POC service config
            //  KnInstaPOCSrvcCfgDTO instaPOCSrvcCfgDTO = provInfoUtil.retrieveInstaPOCSrvcCfg(pocPttServerId);
            knLogger.info(methodName, "Before Version", subsConfigDTO.getProtocolVersion());
            if (subsConfigDTO.getProtocolVersion() != null && !(subsConfigDTO.getProtocolVersion().matches(KnSubConfigConstants.PROTOCOL_VERSION_1_X)
                    || subsConfigDTO.getProtocolVersion().matches(KnSubConfigConstants.PROTOCOL_VERSION_2_X))) {
                knLogger.info(methodName, "inside Version", subsConfigDTO.getProtocolVersion());
                // getting sip svc config info from SIP Proxy SVC Config
                KnSIPProxySvcConfigDTO sipProxySvcConfigDTO = provInfoUtil.retrieveSIPProxySvcConfig(pocPttServerId);
                //subsConfigDTO.setSipProxyURI(sipProxySvcConfigDTO.getSipProxyURI()) ;

                if (subsConfigDTO.getProtocolVersion().matches(KnSubConfigConstants.PROTOCOL_VERSION_3_4_5_X)) {
                    subsConfigDTO.setSipProxyURI(sipProxySvcConfigDTO.getSipProxyURIForBCS());
                } else {
                    subsConfigDTO.setSipProxyURI(loaderResponse.get(KnSubConfigConstants.PRIMARY_PROXY_ROUTE));
                }

                subsConfigDTO.setClientConnRetryInterval(sipProxySvcConfigDTO.getClientConnRetryInterval());
                subsConfigDTO.setMaxClientConnRtyAttempts(sipProxySvcConfigDTO.getMaxClientConnRtyAttempts());
                subsConfigDTO.setClientConnSecurityLevel(sipProxySvcConfigDTO.getClientConnSecurityLevel());
                subsConfigDTO.setClientSipTxnTimeout(sipProxySvcConfigDTO.getClientSipTxnTimeout());
                subsConfigDTO.setClientSipReferTxnTimeout(sipProxySvcConfigDTO.getClientSipReferTxnTimeout());
                //if Poc wifi enabled
                //   knLogger.debug(methodName, "pocwifi enabled :", pocSvcConfigDTO.getEnablePocWifi());
                // if (pocSvcConfigDTO.getEnablePocWifi() == KnProvConstants.ENABLED_POC_WIFI) {
                subsConfigDTO.setMinTcpKaTimerOnWifi(sipProxySvcConfigDTO.getMinTcpKaTimerOnWifi());
                subsConfigDTO.setWifiTcpKaTimerIncrVal(sipProxySvcConfigDTO.getWifiTcpKaTimerIncrVal());
                subsConfigDTO.setMaxTcpKaTimerOnWifi(sipProxySvcConfigDTO.getMaxTcpKaTimerOnWifi());
                subsConfigDTO.setWifiSsidTimeoutMapSize(sipProxySvcConfigDTO.getWifiSsidTimeoutMapSize());
                subsConfigDTO.setTcpKaTimerOnMacroCellular(sipProxySvcConfigDTO.getTcpKaTimerOnMacroCellular());
                subsConfigDTO.setDetectWifiNatTcpTimeout(sipProxySvcConfigDTO.getDetectWifiNatTcpTimeout());
                //}
                // getting software package config info from sw installation package
                List<KnSWPkgConfigDTO> swPkgConfigDTOs = provInfoUtil.retrieveSWPkgConfig();
                for (KnSWPkgConfigDTO swPkgConfigDTO : swPkgConfigDTOs) {
                    knLogger.debug(methodName, "SW Pkg Config:");
                    if (subsProfileRespDTO.getSubsClientType().equals(KnProvConstants.SUBS_CLIENT_TYPE.DESKTOP.value())) {
                        if (swPkgConfigDTO.getSwPkgId() == KnProvConstants.DESKTOP_SWPKG_ID) {
                            if (swPkgConfigDTO.getParamName().equals(KnProvConstants.WIN_DSKTP_UPGRADE_CHK_INTVL)) {
                                String qryInterval = swPkgConfigDTO.getParamValue();
                                if (qryInterval != null && !qryInterval.equals("")) {
                                    subsConfigDTO.setSwUpdateQryInterval(Integer.toString(Integer.parseInt(qryInterval.trim()) * KnProvConstants.HOUR_IN_SECONDS));
                                }
                            } else if (swPkgConfigDTO.getParamName().equals(KnProvConstants.WIN_DSKTP_UPGRADE_INFO_URL)) {
                                subsConfigDTO.setSwUpdateInfoUrl(swPkgConfigDTO.getParamValue());
                            } else if (swPkgConfigDTO.getParamName().equals(KnProvConstants.WIN_DSKTP_UPGRADE_PKG_URL)) {
                                subsConfigDTO.setSwUpdatePkgUrl(swPkgConfigDTO.getParamValue());
                            }
                        }
                    } else if (subsProfileRespDTO.getSubsClientType().equals(KnProvConstants.SUBS_CLIENT_TYPE.DISPATCH_CLIENT.value())) {
                        if (swPkgConfigDTO.getSwPkgId() == KnProvConstants.DISPATCH_SWPKG_ID) {
                            if (swPkgConfigDTO.getParamName().equals(KnProvConstants.WIN_DISPATCH_UPGRADE_CHK_INTVL)) {
                                String qryInterval = swPkgConfigDTO.getParamValue();
                                if (qryInterval != null && !qryInterval.equals("")) {
                                    subsConfigDTO.setSwUpdateQryInterval(Integer.toString(Integer.parseInt(qryInterval.trim()) * KnProvConstants.HOUR_IN_SECONDS));
                                }
                            } else if (swPkgConfigDTO.getParamName().equals(KnProvConstants.WIN_DISPATCH_UPGRADE_INFO_URL)) {
                                subsConfigDTO.setSwUpdateInfoUrl(swPkgConfigDTO.getParamValue());
                            } else if (swPkgConfigDTO.getParamName().equals(KnProvConstants.WIN_DISPATCH_UPGRADE_PKG_URL)) {
                                subsConfigDTO.setSwUpdatePkgUrl(swPkgConfigDTO.getParamValue());
                            }
                        }
                    }
                }
            }
            subsConfigDTO.setActiveFSUpdated(isActiveFS1updated);
            //MC DEVICE
            boolean mcDeviceBit = KnGeneralUtil.getFeatureBitValue(activeFS2, FEATURE_SET.MCDEVICE.value());
            KnXDMDeviceProvDTO deviceProfile = xdmServerDAO.selectDeviceProfile(mdn, true, persisterTxn);
            knLogger.debug(methodName,"mcDeviceBit :",mcDeviceBit
                    ," deviceProfile :",deviceProfile);
            if(!mcDeviceBit
                    && deviceProfile!=null&&deviceProfile.getDeviceType()==DEVICE_TYPE.MC_DEVICE.Value()){
                KnDeviceInfoPersistDTO deviceInfoPersistDTO=new KnDeviceInfoPersistDTO();
                deviceInfoPersistDTO.setDeviceId(mdn);
                deviceInfoPersistDTO.setDeviceType(DEVICE_TYPE.RADIO_NEXT_DEVICE.Value());
                xdmServerDAO.updateDeviceType(deviceInfoPersistDTO, persisterTxn);
                //subsConfigDTO.setDeleteDeviceNotify(true);
            }
            subsConfigDTO.setMdnUpmFsMap(mdnUpmFsMap);
            if(mdnUpmFsMap!=null&&mdnUpmFsMap.size()>0)
            {
            	List <String> profileMdnList= new ArrayList<String>( mdnUpmFsMap.keySet());
    			IProvXDMServerDAO xdmServerDAONew = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB)
    					.createProvXDMServerDAO();
    			Map<String, Collection<KnDocChangeListDTO>> profileMdnEtagMap = xdmServerDAONew
    					.profileMdnEtagUpdate(profileMdnList, persisterTxn);
    			subsConfigDTO.setProfileMdnEtagMap(profileMdnEtagMap);
    			if (profileMdnEtagMap != null && !profileMdnEtagMap.isEmpty()) {
    				subsConfigDTO.setMcsXcapRootUriMap(
                            genInfoUtil.getXCAPRootURI(new ArrayList<>(profileMdnEtagMap.keySet()), true, persisterTxn, true));
                }
            }
            boolean pttSettingBit = KnGeneralUtil.getFeatureBitValue(activeFS2, FEATURE_SET.PTT_SETTINGS_CONTROL.value());
            knLogger.debug(methodName, "PTT setting feature bit -",pttSettingBit);
            if (subsConfigDTO.getProtocolVersion().matches(KnSubConfigConstants.PROTOCOL_VERSION_29_X) && pttSettingBit) {
                knLogger.debug(methodName, "started PTT Setting config doc -");
                String xcapRootUri = loaderResponse.get(KnSubConfigConstants.XCAP_ROOT_URI);
                String templateDocId = null;
                KnOPSubsAddlInfoProfileDTO addlProfileDto = provXDMServerDAO.retrieveSubscrAddlInfo(mdn, persisterTxn);
                if (addlProfileDto != null)
                    templateDocId = genInfoUtil.getDefaultPTTSettingDocValue(corpProfileInfoDTO.getCorpId(),subsProfileRespDTO.getHierarchyId(),addlProfileDto.getPttSettingDocId(),persisterTxn);
                StringBuffer resourceListUri = new StringBuffer(100);
                knLogger.debug(methodName, "xcaprootURI - ", xcapRootUri, " ,templateDocId - " + templateDocId);
                boolean httpsEnabled = KnGeneralUtil.getFeatureBitValue(subsProfileRespDTO.getActiveFS2(), HTTPSSUPPORT.value());
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
                knLogger.debug(methodName, "PttSetting URL - ", resourceListUri.toString());
                subsConfigDTO.setPttSettingUri(resourceListUri.toString());
            }
            boolean delayKpiControlBitEnabled = KnGeneralUtil.getFeatureBitValue(subsConfigDTO.getActiveFS2(), FEATURE_SET.DELAY_KPI_CONTROL.value());
            knLogger.debug(methodName, "Delay KPI control feature bit -", delayKpiControlBitEnabled);
            if(null != subsConfigDTO.getProtocolVersion() && subsConfigDTO.getProtocolVersion().matches(KnSubConfigConstants.PROTOCOL_VERSION_29_X) && delayKpiControlBitEnabled){
                subsConfigDTO.setKpiRepAudInterval(Integer.valueOf(loaderResponse.get(KnSubConfigConstants.KPI_REP_AUD_INTERVAL)));
                subsConfigDTO.setKpiRepUpRand(Integer.valueOf(loaderResponse.get(KnSubConfigConstants.KPI_REP_UP_RAND)));
                subsConfigDTO.setKpiRepMcs(Integer.valueOf(loaderResponse.get(KnSubConfigConstants.KPI_REP_MAX_CHUNK_SIZE)));
            }
            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
        } catch (KnDBConnectionException ex) {
            knLogger.error(methodName, "DAO DBConnection Exception occured :", ex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);
        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occurred : ", ex);
            knLogger.error(methodName, ex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            if (com.kodiak.xdms.server.common.resources.KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_INFO_NOT_FOUND, "Subscriber config doc doesn't exists");
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception occurred :", e);
            knLogger.error(methodName, e);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while get Subscriber Config Doc");
            knLogger.error(methodName, e);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                    "Exception occurred while retrieving Subscriber config  doc info", e);
        }
        knLogger.debug(methodName, "EXIT: getSubscriberConfigDoc operation - ", subsConfigDTO);
        return subsConfigDTO;

    }

    /**
     * method to update the Service auth status of the Subscriber
     * Re-activate or De-activate the subscriber.
     *
     * @param subsProvInfoDTO KnIPSubsProvInfoDTO
     * @param persisterTxn    KnPersisterTxn
     * @return KnOPProvDTO
     * @throws KnProvBOException BO Entity Exception
     * @throws KnFWException     Validation Exception
     */

    public KnOPChgAuthStatusRespDTO changeServiceAuthStatus(KnIPSubsProvInfoDTO subsProvInfoDTO, KnPersisterTxn persisterTxn)
            throws KnProvBOException, KnFWException {
        String methodName = "changeServiceAuthStatus(KnIPSubscriberInfoDTO, KnPersisterTxn)";
        boolean ownedTxn = false;
        knLogger.info(methodName, "ENTRY: change Service Auth status request with DTO - ", subsProvInfoDTO,
                " with Txn - ", persisterTxn);
        KnOPChgAuthStatusRespDTO responseDTO = new KnOPChgAuthStatusRespDTO();
        KnSubsProfilePersistDTO responsesubsProfilePersistDTO = new KnSubsProfilePersistDTO();

        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }
            try {
                xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
            } catch (KnBOException e) {
                knLogger.error(methodName, "failed to retrieve xdm Ptt Sever Id");
                knLogger.error(methodName, e);
                if (ownedTxn) {
                    rollback(persisterTxn);
                }
                throw new KnProvBOException(KnErrorCodes.BOEntity.XDMS_PTT_ID_NOT_FOUND, "Failed to retrieve XDM PTT Server ID", e);
            }

            String mdn = subsProvInfoDTO.getMdn();
            Integer inputSvcAuthStatus = subsProvInfoDTO.getServiceAuthStatus();
            //retrieve the Service Auth Status of the MDN or Subscriber
            KnOPSubsProfileInfoDTO subsProfileInfoDTO = provInfoUtil.retrieveSubscriberInfo(mdn, persisterTxn);
            Integer svcStatusAuthUser = subsProfileInfoDTO.getServiceStatusAuthUser();
            Integer serviceAuthStatus = subsProfileInfoDTO.getServiceAuthStatus();
            //check if the mdn is a pseudo mdn then restrict the operation
            if (subsProfileInfoDTO.getPamAccId() != null && subsProfileInfoDTO.getPamAccId() != 0) {
                if ((subsProvInfoDTO.getClientType() != com.kodiak.common.resources.KnConstants.CLIENT_TYPE_CAT_UI)
                        && (subsProvInfoDTO.getClientType() != com.kodiak.common.resources.KnConstants.CLIENT_TYPE_REST)) {

                    knLogger.error(methodName, "Mdn is present as a pseudo number,operation not allowed");
                    throw new KnProvBOException(KnErrorCodes.BOEntity.MDN_PRESENT_AS_PSEUDOMDN_IN_POCSUBSCRINFO, "Mdn is present as a pseudo mdn in POCSUBSCRINFO");
                }
            }

            long etag = subsProvInfoDTO.getIfMatch();
            // Get current etag
            long currentEtag = subsProfileInfoDTO.getLastProfileUpdateTime();
            if (etag > 0 && currentEtag != etag) {
                knLogger.error(methodName, "Etag mismatch", etag);
                throw new KnProvBOException(KnErrorCodes.BOEntity.MISMATCH_IN_SUBS_DOC_ETAG, "Etag mismatch");
            }

            //   int existingSvcAuthStatus = subsProfileInfoDTO.getServiceAuthStatus();

            KnSubsProfilePersistDTO validateProfilePersistDTO = new KnSubsProfilePersistDTO();
            validateProfilePersistDTO.setInputDTO(subsProvInfoDTO);
            validateProfilePersistDTO.setServiceAuthStatus(inputSvcAuthStatus);

            //validate the service auth Status
            validatorFwk.validate(validateProfilePersistDTO);
            knLogger.debug(methodName, "Validations are successful");

            String newClientPassword = subsProfileInfoDTO.getClientPassword();
            String newUserAgent = subsProfileInfoDTO.getUserAgent();
            if (inputSvcAuthStatus == KnConstants.SERVICE_AUTH_STATUS.PROVISIONED.value()) {
                newUserAgent = null;
                newClientPassword = null;
            }

            //populate the persist DTO for the DB update.

            //Get All profile MDN' for given Real MDN
            List<String> allMdnList= getMdnForUPM(mdn,persisterTxn);
            //profileMdns
            List<String> profileMdnList=new ArrayList<>(allMdnList);
            profileMdnList.remove(mdn);
            //
            KnSubsProfilePersistDTO subsProfilePersistDTO = new KnSubsProfilePersistDTO();
            subsProfilePersistDTO.setMdn(mdn);
            //new rules to calculate svcAuthStatus 9.0 rqPOC_AmbDis_ProvEnhn_2.1.1
            subsProfilePersistDTO.setServiceAuthStatus(KnGenInfoUtil.calculateServiceAuthStatus(inputSvcAuthStatus,svcStatusAuthUser));
            subsProfilePersistDTO.setClientPassword(newClientPassword);
            subsProfilePersistDTO.setUserAgent(newUserAgent);
            subsProfilePersistDTO.setServiceStatusOp(inputSvcAuthStatus);
            long lastProfileUpdateTime = Calendar.getInstance().getTimeInMillis();
            subsProfilePersistDTO.setLastProfileUpdateTime(lastProfileUpdateTime);
            IProvXDMServerDAO xdmServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();

            if (subsProfileInfoDTO.getCorpId() > 0) {
                long corpProfileUpdateTime = Calendar.getInstance().getTimeInMillis();
                xdmServerDAO.updateCorpProfileLastUpdateTime(subsProfileInfoDTO.getCorpId(), corpProfileUpdateTime, persisterTxn);
                knLogger.debug(methodName, "updated corp profile last update time successfully");
            }

            boolean isSubsrUpdateRequired = true;
            if (subsProfilePersistDTO.getServiceAuthStatus() == serviceAuthStatus) {
                // Even when the computed effective SERVICEAUTHSTATUS is unchanged, SERVICE_STATUS_OP
                // must still be persisted if it has changed (e.g. Unstun while User-Disabled).
                // Skipping the update here leaves SERVICE_STADG_017001OP stale (=3/stunned) in DB, which
                // causes a subsequent Dispatcher Enable to compute final status as 3 instead of 2.
                Integer existingServiceStatusOp = subsProfileInfoDTO.getServiceStatusOp();
                if (existingServiceStatusOp == null || subsProfilePersistDTO.getServiceStatusOp() != existingServiceStatusOp) {
                    knLogger.info(methodName, "Effective Service Auth Status unchanged but SERVICE_STATUS_OP changed ("
                            + existingServiceStatusOp + " -> " + subsProfilePersistDTO.getServiceStatusOp()
                            + "), updating DB to persist new OP status");
                } else {
                    isSubsrUpdateRequired = false;
                    responsesubsProfilePersistDTO = subsProfilePersistDTO;
                    knLogger.info(methodName, "Service Auth Status is same as existing, so no update required");
                }
            } else {
                knLogger.info(methodName, "Updating service auth status ");
                if (subsProfilePersistDTO.getServiceAuthStatus() == 3) {
                    subsProfilePersistDTO.setPreviousServiceAuthStatusToStore(serviceAuthStatus);
                } else {
                    subsProfilePersistDTO.setPreviousServiceAuthStatusToStore(0);
                }


                int prevServiceAuthStatusFromDB;
                if (subsProfileInfoDTO.getPreviousServiceAuthStatus() != null) {
                    prevServiceAuthStatusFromDB = subsProfileInfoDTO.getPreviousServiceAuthStatus();
                } else {
                    prevServiceAuthStatusFromDB = subsProfilePersistDTO.getServiceAuthStatus();
                }

                if (subsProfilePersistDTO.getServiceAuthStatus() == 2) {
                    subsProfilePersistDTO.setServiceAuthStatus(prevServiceAuthStatusFromDB);
                    subsProfilePersistDTO.setServiceStatusOp(prevServiceAuthStatusFromDB);
                }
            }

            subsProfilePersistDTO.setMdnList(allMdnList);
            //KnSubsProfilePersistDTO responsesubsProfilePersistDTO = xdmServerDAO.updateServiceAuthStatus(subsProfilePersistDTO, persisterTxn);
            if (isSubsrUpdateRequired) {
                responsesubsProfilePersistDTO = xdmServerDAO.updateServiceAuthStatusForUPM(subsProfilePersistDTO, persisterTxn);
            }
            knLogger.debug("responsesubsProfilePersistDTO:",responsesubsProfilePersistDTO);
            boolean bitEnabled = KnGeneralUtil.getFeatureBitValue(subsProfileInfoDTO.getSubsFS2(), FEATURE_SET.MCDEVICE.value());
            IXDMServerDAO createXDMServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
			if (((KnConstants.MCSCOMPLIANCE== subsProvInfoDTO.getMcpttCompliance()) || bitEnabled) && subsProfilePersistDTO.getServiceAuthStatus() == 2) {
			    KnDeviceInfoPersistDTO deviceInfoPersistDTO = new KnDeviceInfoPersistDTO();
				deviceInfoPersistDTO.setDeviceId(mdn);
				deviceInfoPersistDTO.setDeviceStatus(KnConstants.DEVICE_STATUS_OP.ACTIVATED.value());
				createXDMServerDAO.updateDeviceInfoStatusAndPassword(deviceInfoPersistDTO, persisterTxn);

			} else if (((KnConstants.MCSCOMPLIANCE== subsProvInfoDTO.getMcpttCompliance()) || bitEnabled) && subsProfilePersistDTO.getServiceAuthStatus() == 3) {
				KnDeviceInfoPersistDTO deviceInfoPersistDTO = new KnDeviceInfoPersistDTO();
				deviceInfoPersistDTO.setDeviceId(mdn);
				deviceInfoPersistDTO.setDeviceStatus(KnConstants.DEVICE_STATUS_OP.DEACTIVATED.value());
				deviceInfoPersistDTO.setDeviceDigestPassword(null);
				responseDTO.setDeleteDeviceNotify(true);
				createXDMServerDAO.updateDeviceInfoStatusAndPassword(deviceInfoPersistDTO, persisterTxn);
			}

            IXDMServerDAO commonXDMServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
            //retrieve the previous etag of dir doc
            int previousEtag = commonXDMServerDAO.getCurrentEtagForDirDoc(mdn, persisterTxn);

            //update the dir doc etag
            commonXDMServerDAO.updateEtagForDirDocOfMdnList(allMdnList, persisterTxn);
            knLogger.debug(methodName, "Successfully updated the xdm directory");

            String xcapRootUri = genInfoUtil.getXCAPRootURI(mdn, persisterTxn);

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }

            //populate the response DTO
            //checking if the request is to deactivate or Provisioned subscriber
            //if the request is deactivate subscriber then populate the deactivation notification dto

            /*if (inputSvcAuthStatus == KnConstants.SERVICE_AUTH_STATUS.DEACTIVATED.value() ||
                    inputSvcAuthStatus == KnConstants.SERVICE_AUTH_STATUS.PROVISIONED.value()) {*/
            responseDTO.setMdn(subsProfileInfoDTO.getMdn());
            responseDTO.setPocServerHome(subsProfileInfoDTO.getPoCHome());
            responseDTO.setPresenceServerHome(subsProfileInfoDTO.getPresenceHome());
           /* }*/

            responseDTO.setResponseMessage(KnProvConstants.UPDATE_AUTH_STATUS_SUCCESS);
            responseDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.SUCCESS.value());
            responseDTO.setEtag(lastProfileUpdateTime);
            responseDTO.setSubsClientType(subsProfileInfoDTO.getSubsClientType());

            List<KnOPDirChgDTO> dirChangeDtos=new ArrayList<>();
            //populating the Subscriber config doc DTO
            if (subsProfileInfoDTO.getSubsClientType() != KnProvConstants.SUBS_CLIENT_TYPE.POC_NNI_Alias_MDN.value() && subsProfileInfoDTO.getSubsClientType() != KnProvConstants.SUBS_CLIENT_TYPE.POC_NNI_Group_MDN.value()) {
                for (String mdns : allMdnList) {
                    KnOPDocChgDTO docChgDTO = new KnOPDocChgDTO();
                    docChgDTO.setDocumentChgType(KnConstants.DOC_CHANGE_TYPE.REPLACE.value());
                    String subsConfigDocUri = provInfoUtil.generateSubsConfigSelUri(mdns);
                    docChgDTO.setDocUri(subsConfigDocUri);
                    docChgDTO.setNewEtag(String.valueOf(lastProfileUpdateTime));
                    Collection<KnOPDocChgDTO> chgDocList = new ArrayList<KnOPDocChgDTO>();
                    chgDocList.add(docChgDTO);

                    //populating the XDM Directory DTO
                    KnOPDirChgDTO dirChgDTO = new KnOPDirChgDTO();
                    dirChgDTO.setXcapRootURI(xcapRootUri);
                    dirChgDTO.setPocHome(subsProfileInfoDTO.getPoCHome());
                    dirChgDTO.setPresenceHome(subsProfileInfoDTO.getPresenceHome());
                    dirChgDTO.setDocChgDTO(chgDocList);
                    String dirDocUri = genInfoUtil.generateDirDocUri(mdns);
                    dirChgDTO.setDirUri(dirDocUri);
                    dirChgDTO.setDirPrevEtag(String.valueOf(previousEtag));
                    int newEtag = previousEtag + 1;
                    dirChgDTO.setDirNewEtag(String.valueOf(newEtag));
                    dirChgDTO.setProtoVersion(String.valueOf(subsProfileInfoDTO.getClientPVmajorVer()));
                    dirChgDTO.setClientType(subsProfileInfoDTO.getSubsClientType());
                    dirChgDTO.setMdn(mdns);
                    dirChangeDtos.add(dirChgDTO);
                }
                //populating the Dir chg DTO to response
                responseDTO.setDirChgDTOs(dirChangeDtos);
            }
            // code for new RestApi call
            // setting the activeFS ,mdn ,issyncdisabled
            responseDTO.setMdn(subsProfileInfoDTO.getMdn());
            responseDTO.setActiveFs2(subsProfileInfoDTO.getActiveFS2());
            responseDTO.setCorpid(subsProfileInfoDTO.getCorpId());
            responseDTO.setAuthStatus(subsProfilePersistDTO.getServiceAuthStatus());
            responseDTO.setSyncDisabled(true);
            responseDTO.setProfileMdnList(profileMdnList);
            responseDTO.setSubsFS2(subsProfileInfoDTO.getSubsFS2());
            responseDTO.setMcId(subsProfileInfoDTO.getMcId());
            responseDTO.setMcDataId(subsProfileInfoDTO.getMcDataId());
            responseDTO.setMcPttId(subsProfileInfoDTO.getMcpttId());
            responseDTO.setMcVideoId(subsProfileInfoDTO.getMcVideoId());
            responseDTO.setNetworkName(subsProfileInfoDTO.getNetworkName());

            if(subsProfilePersistDTO.getServiceAuthStatus() == KnConstants.SERVICE_AUTH_STATUS.ACTIVATED.value()){
                    responseDTO.setSyncDisabled(false);
            }
            knLogger.debug(methodName, "Response DTO - ", responseDTO.getActiveFs2()," subsProfileInfoDTO: ",subsProfileInfoDTO);
            knLogger.debug(methodName, "directoryChange DTO - ", dirChangeDtos);

            // IDM user profile change
            String idmFqdn = genInfoUtil.getIDMInternalFqdn(persisterTxn);
            String appId = subsProfileInfoDTO.getDispatchType() == DISPATCH_TYPE_WEB ? APP_ID.DISPATCHER.value() : APP_ID.HANDSET_STANDARD.value();
            if (subsProfileInfoDTO.getClientPVmajorVer() >= PROTOCOL_VERSION_13) {
                KnXDMSubsAliasDetailsReqDTO oidcSubsDto = new KnXDMSubsAliasDetailsReqDTO();
                KnXDMSubsAliasDetailsRespDTO oidcRespDto = null;
                oidcSubsDto.setUserid(mdn);
                if(subsProfilePersistDTO.getServiceAuthStatus() == KnConstants.SERVICE_STATUS_AUTHUSER.PROVISIONED.value()){
                    oidcSubsDto.setGeneratepwd(Boolean.TRUE);
                    oidcSubsDto.setTemppwd(Boolean.FALSE);
                    oidcRespDto = KnManageSyncUserProfileUtil.getInstance()
                            .notifyIDMIntfForOIDCMgmt(oidcSubsDto, appId, idmFqdn, UPDATE_USER_PASSWORD, HttpMethod.PUT);
                } else {
                    Map<String, Object> attributes = new HashMap<>();
                    attributes.put(ACCOUNT_STATUS, String.valueOf(subsProfilePersistDTO.getServiceAuthStatus()));
                    // In case of provisioned or deactivate account state will diable. It will be enable only in activate state
                    int state = IDM_ACCOUNT_STATUS.DISABLE.value();
                    if (subsProfilePersistDTO.getServiceAuthStatus() == KnConstants.SERVICE_STATUS_AUTHUSER.ACTIVATED.value()) {
                        state = IDM_ACCOUNT_STATUS.ENABLE.value();
                    }
                    attributes.put(ACCOUNT_STATUS, String.valueOf(state));
                    oidcSubsDto.setAttributes(attributes);
                    oidcRespDto = KnManageSyncUserProfileUtil.getInstance()
                            .notifyIDMIntfForOIDCMgmt(oidcSubsDto, appId, idmFqdn, CHANGE_SERVICE_AUTH_STATUS, HttpMethod.PUT);
                }
                if (oidcRespDto != null && oidcRespDto.getStatus() == KnConstants.RESPONSE_STATUS.FAILURE.value()) {
                    knLogger.error(methodName, "Error for updating Updating pwd profile in OidcIDM - ", oidcRespDto.getStatus());
                    throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred while Updating password of Subscriber");
                }
            }

            //updating LASTPROFILEUPDATETIME in POCSUBSCRINFO profile mdn notiy
            Map<String, Collection<KnDocChangeListDTO>> profileMdnEtagMap = xdmServerDAO.profileMdnEtagUpdate(profileMdnList, persisterTxn);
            responseDTO.setProfileMdnEtagMap(profileMdnEtagMap);
            if(profileMdnEtagMap != null && !profileMdnEtagMap.isEmpty()){
                responseDTO.setMcsXcapRootUriMap(genInfoUtil.getXCAPRootURI(new ArrayList<>(profileMdnEtagMap.keySet()), persisterTxn, true));
            }
            responseDTO.setMdnList(allMdnList);
            knLogger.debug(methodName, "Response DTO - ", responseDTO);
            knLogger.info(methodName, "EXIT: change service auth status of Subscriber operation ");
            return responseDTO;
        } catch (KnDBConnectionException ex) {
            knLogger.error(methodName, "DAO DBConnection Exception occured :", ex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);
        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occurred : ", ex);
            knLogger.error(methodName, ex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            if (com.kodiak.xdms.server.common.resources.KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_INFO_NOT_FOUND, "Subscriber already exists");
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception occurred :", e);
            knLogger.error(methodName, e);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw e;
        } catch (KnValidationException vex) {
            knLogger.error(methodName, "Validation Exception occurred :", vex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw vex;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while change service auth status of Subscriber");
            knLogger.error(methodName, e);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred while change service auth status of Subscriber", e);
        }

    }

    /**
     * method to retrieve the Default Subscriber Profile
     *
     * @param subscriberInfoDTO KnIPSubscriberInfoDTO
     * @param persisterTxn      KnPersisterTxn
     * @return KnOPSubsProfileInfoDTO
     * @throws KnProvBOException BO Exception
     */
    public KnOPSubsProfileInfoDTO getDefaultSubscriberProfile(KnIPSubscriberInfoDTO subscriberInfoDTO, KnPersisterTxn persisterTxn)
            throws KnProvBOException {
        String methodName = "getDefaultSubscriberProfile(KnIPSubscriberInfoDTO, KnPersistexTxn)";
        KnOPSubsProfileInfoDTO subsProfileInfoDTO = null;

        knLogger.debug(methodName, "NO IMPLEMENTATION PERFORMED");

        return subsProfileInfoDTO;

    }

    /**
     * method to delete the corporate profile
     * this method will first verify if there is any subscribers associated with the corporate profile before
     * deleting the corporate profile
     *
     * @param corpProfileInfoDTO KnIPCorpProfileInfoDTO
     * @param persisterTxn       KnPersisterTxn
     * @return KnOPProvDTO
     * @throws KnProvBOException
     */
    public KnOPProvDTO deleteCorpProfile(KnIPCorpProfileInfoDTO corpProfileInfoDTO, KnPersisterTxn persisterTxn)
            throws KnProvBOException {
        String methodName = "deleteCorpProfile(KnIPCorpProfileInfoDTO, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY: Delete Corp profile with corpID - ", corpProfileInfoDTO.getCorpId());
        boolean ownedTxn = false;
        KnOPProvDTO responseDTO = null;
        try {

            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();

            //deleting the Corp profile
            int corpId = corpProfileInfoDTO.getCorpId();
            int corpSubsCount = provXDMServerDAO.retrieveCorpSubscriberWithProfileCnt(corpId, persisterTxn);
            knLogger.debug(methodName, "corp Subscriber count - ", corpSubsCount);
            if (corpSubsCount == 0) {
                provXDMServerDAO.deleteCorporateProfile(corpId, persisterTxn);
                knLogger.debug(methodName, "Deleted corporate Profile ");
            } else {
                //TODO this has to be handled with proper flow
                knLogger.warn(methodName, "NOT Deleting CORP PROFILE");
            }

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }

            //sending the response
            responseDTO = new KnOPProvDTO();
            responseDTO.setResponseMessage(KnProvConstants.DELETE_CORP_PROFILE_SUCCESS);
            responseDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.SUCCESS.value());

            knLogger.info(methodName, "EXIT: Delete Corp profile operation - ", responseDTO.getResponseStatus());

            return responseDTO;

        } catch (KnDBConnectionException ex) {
            knLogger.error(methodName, "DAO DBConnection Exception occured :", ex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);
        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occurred : ", ex);
            knLogger.error(methodName, ex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                knLogger.warn(methodName, "Not corporate profile found");
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception occurred :", e);
            knLogger.error(methodName, e);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while removal of corp profile");
            knLogger.error(methodName, e);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred while removal of " +
                    " old Subscriber Info", e);
        }

    }

    /**
     * method to update Ext Corp ID
     * this method will first verify if there is any subscribers associated with the ext corporate ID before
     * deleting the corporate profile
     *
     * @param corpProfileInfoDTO KnIPCorpProfileInfoDTO
     * @param persisterTxn       KnPersisterTxn
     * @return KnOPProvDTO
     * @throws KnProvBOException
     */
    public KnOPProvDTO updateExtCorpID(KnIPCorpProfileInfoDTO corpProfileInfoDTO, KnPersisterTxn persisterTxn)
            throws KnProvBOException {
        String methodName = "updateExtCorpID(KnIPCorpProfileInfoDTO, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY: ExtCorpId", corpProfileInfoDTO.getExtCorpId(), " CorpID - ", corpProfileInfoDTO.getCorpId());
        boolean ownedTxn = false;
        KnOPProvDTO responseDTO = null;
        try {

            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            String extCorpId = corpProfileInfoDTO.getExtCorpId();
            IProvXDMServerDAO xdmServerDAO = KnProvFactorySelector.getProvDAOFactory(KnFactorySelector.DB).createProvXDMServerDAO();

            KnOPCorpProfileInfoDTO existingCorpProfileInfoDTO = xdmServerDAO.retrieveCorporateProfile(extCorpId, persisterTxn);
            int existingCorpId = existingCorpProfileInfoDTO.getCorpId();
            if (existingCorpId > 0) {
                knLogger.error(methodName, "Existing external Coprporate ID ");
                throw new KnProvBOException(KnErrorCodes.BOEntity.EXT_CORP_ID_EXISTING, "external corp id is existing");
            }


            long profileUpdateTime = Calendar.getInstance().getTimeInMillis();

            KnCorpProfilePersistDTO corpProfilePersistDTO = new KnCorpProfilePersistDTO();
            corpProfilePersistDTO.setCorpId(corpProfileInfoDTO.getCorpId());
            corpProfilePersistDTO.setExtCorpId(extCorpId);
            corpProfilePersistDTO.setLastProfileUpdateTime(profileUpdateTime);

            xdmServerDAO.updateExternalCorpID(corpProfilePersistDTO, persisterTxn);
            knLogger.debug(methodName, "Update Ext corporate ID ");

            xdmServerDAO.updateAccountIdForSubscr(extCorpId, corpProfileInfoDTO.getCorpId(), persisterTxn);
            knLogger.info(methodName, "Updated AccountId for subscribers ");
            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }

            //sending the response
            responseDTO = new KnOPProvDTO();
            responseDTO.setResponseMessage("Updated external Corp ID");
            responseDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.SUCCESS.value());

            knLogger.info(methodName, "EXIT: update ext corp ID - ", responseDTO.getResponseMessage());

            return responseDTO;
        } catch (KnDBConnectionException ex) {
            knLogger.error(methodName, "DAO DBConnection Exception occured :", ex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);

        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occurred : ", ex);
            knLogger.error(methodName, ex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                knLogger.warn(methodName, "Not corporate profile found");
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception occurred :", e);
            knLogger.error(methodName, e);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while updating ext corp ID");
            knLogger.error(methodName, e);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred while update of " +
                    " ext corp ID", e);
        }

    }

    /**
     * This method validates the subsClientType provided as of parameter & sends the Welcome sms
     *
     * @param subsClientType Client type of the subscriber (HandSet,Desktop,Dispatch ,Interop)
     * @param mdn
     * @param messageId
     */
    private void sendSMSNotification(Integer subsClientType, String mdn, int messageId) {
        String methodName = "sendSMSNotification()";
            knLogger.info(methodName, "Sending WELCOME SMS NOTIFICATION ", subsClientType, KnGDPRTemplate.mdn(mdn), messageId);
            KnProvSMSDTO provSMSDTO = new KnProvSMSDTO();
            provSMSDTO.setMdn(mdn);
            provSMSDTO.setMsgNotificationId(messageId);
            boolean status = provSMSUtil.sendProvSMS(provSMSDTO, null);
            knLogger.info(methodName, "Sent WELCOME SMS NOTIFICATION status ", status);
    }


    public KnOPProvDTO sendConfigDocNotification(String mdn, KnPersisterTxn persisterTxn) throws KnProvBOException {
        String methodName = "sendConfigDocNotification(String)";
        KnOPUpdateSubsInfoDTO responseDTO1;
        try {
            long lastProfileUpdateTime = Calendar.getInstance().getTimeInMillis();
            KnSubsProfilePersistDTO subsProfilePersistDTO = new KnSubsProfilePersistDTO();
            subsProfilePersistDTO.setMdn(mdn);
            subsProfilePersistDTO.setLastProfileUpdateTime(lastProfileUpdateTime);
            KnOPProvDTO responseDTO = new KnOPProvDTO();
            IProvXDMServerDAO xdmServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            xdmServerDAO.updateLastProfileUpdateTime(subsProfilePersistDTO, persisterTxn);
            knLogger.debug(methodName, "updated subscriber profile successfully");

            responseDTO.setResponseMessage(KnProvConstants.UPDATE_SUBS_PROFILE_SUCCESS);
            responseDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.SUCCESS.value());
            responseDTO.setEtag(lastProfileUpdateTime);
            //populating the Subs Config document change DTO
            KnOPDocChgDTO docChgDTO = new KnOPDocChgDTO();
            docChgDTO.setDocumentChgType(KnConstants.DOC_CHANGE_TYPE.REPLACE.value());
            String subsConfigDocUri = provInfoUtil.generateSubsConfigSelUri(mdn);
            docChgDTO.setDocUri(subsConfigDocUri);
            docChgDTO.setNewEtag(String.valueOf(lastProfileUpdateTime));

            Collection<KnOPDocChgDTO> docChgList = new ArrayList<KnOPDocChgDTO>();
            docChgList.add(docChgDTO);
            KnOPSubsProfileInfoDTO existingSubsProfileDTO = provInfoUtil.retrieveSubscriberInfo(mdn, persisterTxn);
            //populating Dir Document change DTO
            KnOPDirChgDTO dirChgDTO = new KnOPDirChgDTO();
            String pocPttId = existingSubsProfileDTO.getPoCHome();
            String presencePttId = existingSubsProfileDTO.getPresenceHome();
            String xdmsPttId = existingSubsProfileDTO.getXDMSHome();
            //KnXDMSServiceConfigDTO xdmsServiceConfigDTO = genInfoUtil.retrieveXDMSServiceConfig(xdmsPttId, persisterTxn);
            IXDMServerDAO commonXDMServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmsPttId);
            //retrieve the previous etag of dir doc
            int previousEtag = commonXDMServerDAO.getCurrentEtagForDirDoc(mdn, persisterTxn);
            //update the dir doc etag
            commonXDMServerDAO.updateEtagForDirDoc(mdn, persisterTxn);
            knLogger.debug(methodName, "Successfully updated the xdm directory");
            //dirChgDTO.setXcapRootURI(xdmsServiceConfigDTO.getXcapRootUri());
            dirChgDTO.setXcapRootURI(genInfoUtil.getXCAPRootURI(mdn, persisterTxn));
            dirChgDTO.setPocHome(pocPttId);
            dirChgDTO.setPresenceHome(presencePttId);
            dirChgDTO.setDocChgDTO(docChgList);
            String dirDocUri = genInfoUtil.generateDirDocUri(mdn);
            dirChgDTO.setDirUri(dirDocUri);
            dirChgDTO.setDirPrevEtag(String.valueOf(previousEtag));
            int newEtag = previousEtag + 1;
            dirChgDTO.setDirNewEtag(String.valueOf(newEtag));
            //setting the Document Change DTO the response
            responseDTO.setDirChgDTO(dirChgDTO);
            responseDTO.setEtag(lastProfileUpdateTime);
            knLogger.info(methodName, " etag ", responseDTO.getEtag());
            return responseDTO;
        } catch (KnDBConnectionException ex) {
            knLogger.error(methodName, "DAO DBConnection Exception occured :", ex);
            throw new KnProvBOException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);
        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occurred : ", ex);
            knLogger.error(methodName, ex);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception occurred :", e);
            knLogger.error(methodName, e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while sendConfigDocNotification");
            knLogger.error(methodName, e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred while sendConfigDocNotification", e);
        }

    }


    /**
     * returns the concatination of all the values .
     *
     * @param value1
     * @param value2
     * @param value3
     * @param value4
     * @param value5
     * @return
     */

    public String getValues(String value1, String value2, String value3, String value4, String value5) {
        if (null == value1) {
            value1 = "";
        }
        if (null == value2) {
            value2 = "";
        }
        if (null == value3) {
            value3 = "";
        }
        if (null == value4) {
            value4 = "";
        }
        if (null == value5) {
            value5 = "";
        }

        return value1 + "," + value2 + "," + value3 + "," + value4 + "," + value5;
    }

    public Integer convertInteger(String input) {
        if (null == input) {
            return null;
        } else {
            return Integer.parseInt(input);
        }

    }

    public Long convertLong(String input) {
        if (null == input) {

            return null;
        } else {
            return Long.parseLong(input);
        }
    }

    public String convertLgsrv(String ClientLogURI, String LogServerWSContext) {

        return new StringBuilder().append(ClientLogURI).append("/").append(LogServerWSContext).toString();
    }


    public String convertLgsrvwifi(String ClientLogURI, String LogServerWSContext) {
        return new StringBuilder().append(ClientLogURI).append("/").append(LogServerWSContext).toString();
    }


    public String comaValues(String value1, String value2) {
        if (null == value1) {
            value1 = "";
        }
        if (null == value2) {
            value2 = "";
        }
        return new StringBuilder().append(value1).append(",").append(value2).toString();
    }


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


    private Boolean cleanUpTGSData(String activeFS, String existingActiveFS) {
        boolean cleanUpTGSData = false;
        //get the 20th and 31st bit from the existing DB activefs
        boolean talkGrpSelServerExstActFS = featureSetUtil.getFeatureBitValue(existingActiveFS, com.kodiak.common.resources.KnConstants.FEATURE_SET.TLKGRPSELSERVER.value());
        boolean talkGrpSelClientExstActFS = featureSetUtil.getFeatureBitValue(existingActiveFS, com.kodiak.common.resources.KnConstants.FEATURE_SET.TLKGRPSELCLIENT.value());

        //get the 20th,28th and 31st bit from the recalculated activefs
        boolean talkGrpScanServerActFS = featureSetUtil.getFeatureBitValue(activeFS, com.kodiak.common.resources.KnConstants.FEATURE_SET.TLKGRPSELSERVER.value());
        boolean talkGrpScanClientActFS = featureSetUtil.getFeatureBitValue(activeFS, com.kodiak.common.resources.KnConstants.FEATURE_SET.TLKGRPSCANCLIENT.value());
        boolean talkGrpSelClientActFS = featureSetUtil.getFeatureBitValue(activeFS, com.kodiak.common.resources.KnConstants.FEATURE_SET.TLKGRPSELCLIENT.value());

        if (!talkGrpScanServerActFS && !talkGrpScanClientActFS) {
            if (talkGrpSelClientActFS) {
                if ((talkGrpSelServerExstActFS && !talkGrpSelClientExstActFS) || (!talkGrpSelServerExstActFS && !talkGrpSelClientExstActFS)) {
                    cleanUpTGSData = true;
                }
            } else {
                if ((talkGrpSelServerExstActFS && !talkGrpSelClientExstActFS) || (!talkGrpSelServerExstActFS && talkGrpSelClientExstActFS)) {
                    cleanUpTGSData = true;
                }
            }
        }


        return cleanUpTGSData;
    }

    /**
     * -->Get extSubsInfoDTO in the request
     * -->convert the extSubsInfoDTO to extSubsPersistDTO by calling populateExtSubscriberPersistDTO method
     * -->call the createExtSubscriber method by passing extSubsPersistDTO
     *
     * @param extSubsInfoDTO
     * @param persisterTxn
     * @return opProvDTO
     * @throws KnProvBOException
     */

    public KnOPProvDTO createExtSubscriber(KnExtSubscriberInfoDTO extSubsInfoDTO, KnPersisterTxn persisterTxn) throws KnProvBOException {
        String methodName = "createExtSubscriber(KnExtSubscriberInfoDTO,KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY:Create External Subscriber Profile");
        KnOPProvDTO opProvDTO = null;

        try {
            KnExtSubsPersistDTO extSubsPersistDTO = populateExtSubscriberPersistDTO(extSubsInfoDTO);
            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();


            /*  ArrayList<String> mdnList=new ArrayList<>();
                        for(KnExtSubscriberDTO extSubscriberDTO:extSubsInfoDTO.getExtSubs()){
                            mdnList.add(extSubscriberDTO.getMdn());
                        }
                        KnExtSubsPersistDTO extSubsPersistResponseDTO=provXDMServerDAO.getExtSubscriberInfo(mdnList,persisterTxn);
                        if(!extSubsPersistResponseDTO.getExtSubs().isEmpty()){

                        }
            */
            provXDMServerDAO.createExtSubscriber(extSubsPersistDTO, persisterTxn);

            opProvDTO = new KnOPProvDTO();
            opProvDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.SUCCESS.value());
            opProvDTO.setResponseMessage(KnProvConstants.CREATE_EXT_SUBSCRIBER_SUCCESS);
            knLogger.debug(methodName, "Create External Subscriber Profile With Response DTO", opProvDTO);
        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occurred : ", ex);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
        } /*catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception occurred :", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while Creating External Subscriber Profile");
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred while Creating External Subscriber Profile", e);
        }*/
        return opProvDTO;
    }

    /**
     * -->Get extSubsInfoDTO in the request
     * -->convert the extSubsInfoDTO to extSubsPersistDTO by calling populateExtSubscriberPersistDTO method
     * -->call the deleteExtSubscriber method by passing extSubsPersistDTO
     *
     * @param extSubsInfoDTO
     * @param persisterTxn
     * @return opProvDTO
     * @throws KnProvBOException
     */

    public KnOPProvDTO deleteExtSubscriber(KnExtSubscriberInfoDTO extSubsInfoDTO, KnPersisterTxn persisterTxn) throws KnProvBOException {
        String methodName = "deleteExtSubscriber(KnExtSubscriberInfoDTO,KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY:Delete External Subscriber Profile");
        KnOPProvDTO opProvDTO = null;

        try {
            KnExtSubsPersistDTO extSubsPersistDTO = populateExtSubscriberPersistDTO(extSubsInfoDTO);
            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            provXDMServerDAO.deleteExtSubscriber(extSubsPersistDTO, persisterTxn);

            opProvDTO = new KnOPProvDTO();
            opProvDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.SUCCESS.value());
            opProvDTO.setResponseMessage(KnProvConstants.DELETE_EXT_SUBSCRIBER_SUCCESS);
            knLogger.debug(methodName, "Delete External Subscriber Profile With Response DTO", opProvDTO);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred : ", e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", e);
        }
        return opProvDTO;
    }

    /**
     * -->Get extSubsInfoDTO in the request
     * -->convert the extSubsInfoDTO to extSubsPersistDTO by calling populateExtSubscriberPersistDTO method
     * -->call the updateExtSubscriber method by passing extSubsPersistDTO
     *
     * @param extSubsInfoDTO
     * @param persisterTxn
     * @return KnOPProvDTO
     * @throws KnProvBOException
     */
    //  @Override
    public KnOPProvDTO updateExtSubscriber(KnExtSubscriberInfoDTO extSubsInfoDTO, KnPersisterTxn persisterTxn) throws KnProvBOException {
        String methodName = "updateExtSubscriber(KnExtSubscriberInfoDTO,KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY:Update External Subscriber Profile");
        KnOPProvDTO opProvDTO = null;

        try {
            KnExtSubsPersistDTO extSubsPersistDTO = populateExtSubscriberPersistDTO(extSubsInfoDTO);
            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            provXDMServerDAO.updateExtSubscriber(extSubsPersistDTO, persisterTxn);

            opProvDTO = new KnOPProvDTO();
            opProvDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.SUCCESS.value());
            opProvDTO.setResponseMessage(KnProvConstants.UPDATE_EXT_SUBSCRIBER_SUCCESS);
            knLogger.info(methodName, "Update External Subscriber Profile With Response DTO", opProvDTO);

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred : ", e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", e);
        }
        return opProvDTO;
    }

    /**
     * -->Get KnExtSubscriberInfoDTO in  the request
     * -->Get the list of mdns from the request and save them in a list
     * -->pass the extMdnList to the getExtSubscriberInfo method
     * -->Get the result as extSubsPersistResponseDTO.
     * -->Return the response as extSubscriberInfoDTO by getting values from extSubsPersistResponseDTO
     *
     * @param extSubsInfoDTO
     * @param persisterTxn
     * @return extSubscriberInfoDTO
     * @throws KnProvBOException
     */
    @Override
    public KnExtSubscriberInfoDTO getExtSubscriberInfo(KnExtSubscriberInfoDTO extSubsInfoDTO, KnPersisterTxn persisterTxn) throws KnProvBOException {
        String methodName = "getExtSubscriberInfo(KnExtSubscriberInfoDTO,KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY:Get External Subscriber Profile ");
        KnExtSubscriberInfoDTO extSubscriberInfoDTO = new KnExtSubscriberInfoDTO();
        KnExtSubsPersistDTO extSubsPersistResponseDTO = null;
        ArrayList<String> extMdnList = new ArrayList<>();
        for (KnExtSubscriberDTO extSubscriberDTO : extSubsInfoDTO.getExtSubs()) {
            extMdnList.add(extSubscriberDTO.getMdn());
        }
        try {
            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            extSubsPersistResponseDTO = provXDMServerDAO.getExtSubscriberInfo(extMdnList, persisterTxn);
            extSubscriberInfoDTO.setExtSubs(extSubsPersistResponseDTO.getExtSubs());
            knLogger.debug(methodName, "Get External Subscriber Profile With Response DTO", extSubscriberInfoDTO);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred : ", e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", e);
        }
        return extSubscriberInfoDTO;
    }

    /**
     * -->Get the mdn in the request for which list of corpid is to be retrieved
     * -->Get the result and save in a list by calling the getCorpIdsForExtSubscriber
     * -->return the listOfCorpids
     *
     * @param mdn
     * @param persisterTxn
     * @return listOfCorpId
     * @throws KnProvBOException
     */

    public List<Integer> getCorpIdsForExtSubscriber(String mdn, KnPersisterTxn persisterTxn) throws KnProvBOException {
        String methodName = "getCorpIdsForExtSubscriber(String,KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY:Get CorpIds Of External Subscriber Profile For MDN - ", KnGDPRTemplate.mdn(mdn));
        List<Integer> listOfCorpId = null;
        try {
            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            listOfCorpId = provXDMServerDAO.getCorpIdsForExtSubscriber(mdn, persisterTxn);
            knLogger.debug(methodName, "Get CorpIds For External Subscriber Profile With CorpIdList", listOfCorpId);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred : ", e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", e);
        }
        return listOfCorpId;
    }

    /**
     * method to add or modify the External Subscriber Profile.
     * --> Get the list of External Subscribers present in the DB
     * --> Check the server result with the request
     * --> save the External subscribers which are not present in the DB as listAddReuestDTOs
     * --> save the External subscribers which are not present in the DB as listUpdateRequestDTOs
     * --> call the createExtSubscriber method for listAddReuestDTOs
     * --> call the updateExtSubscriber method for listUpdateRequestDTOs
     * -->Return the response
     *
     * @param extSubsInfoDTO
     * @param persisterTxn
     * @return responseDTO
     * @throws KnProvBOException
     */
    @Override
    public KnOPProvDTO addOrModifyExtSubscribers(KnExtSubscriberInfoDTO extSubsInfoDTO, KnPersisterTxn persisterTxn) throws KnProvBOException {
        String methodName = "addOrModifyExtSubscribers(KnExtSubscriberInfoDTO,KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY:AddOrModify External Subscriber Profile");
        KnOPProvDTO responseDTO = null;
        KnExtSubsPersistDTO extGetResponsePersistDTO = null;

        KnExtSubsPersistDTO extAddRequestPersistDTO = new KnExtSubsPersistDTO();
        KnExtSubsPersistDTO extUpdateRequestPersistDTO = new KnExtSubsPersistDTO();

        ArrayList<String> extMdnList = new ArrayList<>();
        for (KnExtSubscriberDTO extSubscriberDTO : extSubsInfoDTO.getExtSubs()) {
            extMdnList.add(extSubscriberDTO.getMdn());
        }
        try {
            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            extGetResponsePersistDTO = provXDMServerDAO.getExtSubscriberInfo(extMdnList, persisterTxn);
            //save the list of ext subscribers into listGetResponseDTOs
            List<KnExtSubscriberDTO> listGetResponseDTOs = extGetResponsePersistDTO.getExtSubs();

            //create the list of ext subscribers to be added and to be updated
            List<KnExtSubscriberDTO> listAddReuestDTOs = extSubsInfoDTO.getExtSubs();
            List<KnExtSubscriberDTO> listUpdateRequestDTOs = new ArrayList<>();
            //Collections.copy(listUpdateRequestDTOs,listAddReuestDTOs);
            for (KnExtSubscriberDTO e : extSubsInfoDTO.getExtSubs()) {
                listUpdateRequestDTOs.add(e);
            }
            listAddReuestDTOs.removeAll(listGetResponseDTOs);
            listUpdateRequestDTOs.retainAll(listGetResponseDTOs);
            knLogger.debug(methodName, "List Of External subscribers to be Added", listAddReuestDTOs);
            knLogger.debug(methodName, "List Of External subscribers to be Updated", listUpdateRequestDTOs);

            //set the respective list to their dto to pass to the next level
            extAddRequestPersistDTO.setExtSubs(listAddReuestDTOs);
            extUpdateRequestPersistDTO.setExtSubs(listUpdateRequestDTOs);

            //call the createExtSubscriber and updateExtSubscriber based on the condition
            if (!listAddReuestDTOs.isEmpty()) {
                provXDMServerDAO.createExtSubscriber(extAddRequestPersistDTO, persisterTxn);
            }
            if (!listUpdateRequestDTOs.isEmpty()) {
                provXDMServerDAO.updateExtSubscriber(extUpdateRequestPersistDTO, persisterTxn);
            }

            responseDTO = new KnOPProvDTO();
            responseDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.SUCCESS.value());
            responseDTO.setResponseMessage(KnProvConstants.ADD_MODIFY_EXT_SUBSCRIBER_SUCCESS);


        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred : ", e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", e);
        }
        return responseDTO;
    }

    /**
     * @param mdn
     * @param persisterTxn
     * @return
     * @throws KnProvBOException
     */
    @Override
    public KnOPProvDTO createSubscrRoamingProfiles(String mdn, KnPersisterTxn persisterTxn) throws KnProvBOException {
        String methodName = "createSubscrRoamingProfiles(mdn,KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY:create subscriber roaming profile for mdn ", KnGDPRTemplate.mdn(mdn));
        KnOPProvDTO responseDTO = null;
        //retrieve all the clusterids for international roaming type
        ArrayList<Integer> clusterIds = (ArrayList<Integer>) provInfoUtil.getRoamingTypeClusterIds(ROAMING_TYPE.INTERNATIONAL.value(), persisterTxn);
        knLogger.debug(methodName, "ClusterId list from ROAMINGMCCMNCINFO table - ", clusterIds);
        try {
            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            //before inserting inside RoamingSubscrProfile table we need to check whether RoamingClusterId is present for the same mdn in the
            //table or not.If it prsent then don't insert and if it is prsent then insert
            ArrayList<Integer> roamingClusterIdList = provXDMServerDAO.retrieveSubscrRoamingProfile(mdn, persisterTxn);
            knLogger.debug(methodName, "Roaming Cluster Ids for the subscriber  ", roamingClusterIdList);
            clusterIds.removeAll(roamingClusterIdList);
            knLogger.debug(methodName, "After filtering the clusterId list ", clusterIds);
            //after filtering check the clusterIds list, if it is empty means all the clusterid alreadu present else not
            knLogger.debug(methodName, "After filtering clusterIds is empty ", clusterIds.isEmpty());
            if (!clusterIds.isEmpty()) {
                provXDMServerDAO.createSubscrRoamingProfile(mdn, clusterIds, persisterTxn);
            }


            responseDTO = new KnOPProvDTO();
            responseDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.SUCCESS.value());
            responseDTO.setResponseMessage(KnProvConstants.CREATE_SUBSCR_ROAMING_PROFILE_SUCCESS);
            responseDTO.setResponseCode(KnProvConstants.SUCCESS_CODE);
            knLogger.info(methodName, "EXIT:created subscriber roaming profile with response ", responseDTO);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred : ", e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", e);
        }
        return responseDTO;
    }

    @Override
    public KnOPProvDTO deleteSubscrRoamingProfiles(String mdn, KnPersisterTxn persisterTxn) throws KnProvBOException {
        String methodName = "deleteSubscrRoamingProfiles(mdn,KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY:Delete subscriber roaming profile for mdn ", KnGDPRTemplate.mdn(mdn));
        KnOPProvDTO responseDTO = null;
        ArrayList<Integer> clusterIds = (ArrayList<Integer>) provInfoUtil.getRoamingTypeClusterIds(ROAMING_TYPE.INTERNATIONAL.value(), persisterTxn);
        try {
            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            provXDMServerDAO.deleteSubscrRoamingProfile(mdn, clusterIds, persisterTxn);
            responseDTO = new KnOPProvDTO();
            responseDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.SUCCESS.value());
            responseDTO.setResponseMessage(KnProvConstants.DELETE_SUBSCR_ROAMING_PROFILE_SUCCESS);
            responseDTO.setResponseCode(KnProvConstants.SUCCESS_CODE);
            knLogger.info(methodName, "EXIT:Deleted subscriber roaming profile with response ", responseDTO);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred : ", e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", e);
        }
        return responseDTO;
    }


    /**
     * --> Get  KnExtSubscriberInfoDTO in the request
     * --> Create KnExtSubsPersistDTO and set its value by fetching from KnExtSubscriberInfoDTO
     * --> Return KnExtSubsPersistDTO as the response
     *
     * @param extSubscriberInfoDTO
     * @return extSubsPersistDTO
     */
    protected KnExtSubsPersistDTO populateExtSubscriberPersistDTO(KnExtSubscriberInfoDTO extSubscriberInfoDTO) {
        String methodName = "populateExtSubscriberPersistDTO(KnExtSubscriberInfoDTO)";
        KnExtSubsPersistDTO extSubsPersistDTO = new KnExtSubsPersistDTO();
        extSubsPersistDTO.setOperationType(extSubscriberInfoDTO.getOperationType());
        extSubsPersistDTO.setEntityId(extSubscriberInfoDTO.getEntityId());
        extSubsPersistDTO.setExtSubs(extSubscriberInfoDTO.getExtSubs());
        knLogger.debug(methodName, "XDM Ext Subs PersistDTO With Response DTO", extSubsPersistDTO);
        return extSubsPersistDTO;
    }

    /**
     * this method will create the corp profile
     * if the corporate already exist then throw exception
     *
     * @param corpProfileInfoDTO
     * @param persisterTxn
     * @return
     */
    public KnOPCorpProfileInfoDTO createCorpProfile(KnIPCorpProfileInfoDTO corpProfileInfoDTO, KnPersisterTxn persisterTxn) throws KnProvBOException {
        //This method will create a new corporate profile . Id already exists then throw error.TODO
        final String methodName = "createCorpProfile(KnIPCorpProfileInfoDTO)";
        knLogger.debug(methodName, "ENTRY:corpProfileInfoDTO", corpProfileInfoDTO);
        IProvXDMServerDAO provXDMServerDAO = null;
        int corpId = 0;
        String pocPttServerId = corpProfileInfoDTO.getPocHome();
        // getting doc config info from Registrar Service Config
        KnPOCSvcConfigDTO knPOCSvcConfigDTO = provInfoUtil.retrievePOCSvcConfig(pocPttServerId);
        KnOPCorpProfileInfoDTO response = new KnOPCorpProfileInfoDTO();
        try {
            provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            KnCorpProfilePersistDTO corpProfilePersistDTO = new KnCorpProfilePersistDTO();
            long profileCreationTime = System.currentTimeMillis();
            corpProfilePersistDTO.setCorpId(corpProfileInfoDTO.getCorpId());
            corpProfilePersistDTO.setXDMSHome(corpProfileInfoDTO.getXDMSHome());
            corpProfilePersistDTO.setPocHome(corpProfileInfoDTO.getPocHome());
            corpProfilePersistDTO.setCorporateName(corpProfileInfoDTO.getCorporateName());
            //corpProfilePersistDTO.setPairedContactListId(corpProfileInfoDTO.getPairedContactListId());
            //corpProfilePersistDTO.setMaxSubscribers(corpProfileInfoDTO.getMaxSubscribers());
            corpProfilePersistDTO.setExtCorpId(corpProfileInfoDTO.getExtCorpId());
            corpProfilePersistDTO.setCorpFS2(corpProfileInfoDTO.getCorpFS2());
            corpProfilePersistDTO.setOpsCorpFS2(corpProfileInfoDTO.getOpsCorpFS2());
            corpProfilePersistDTO.setProfileCreationTime(profileCreationTime);
            corpProfilePersistDTO.setLastProfileUpdateTime(profileCreationTime);
            //added new  parameter for dynamic Qos
            corpProfilePersistDTO.setDynamicQosFlag(knPOCSvcConfigDTO.getDynamicQosFlag());
            corpProfilePersistDTO.setHierarchyType(corpProfileInfoDTO.getHierarchyType());
            String xdmCorpFS2Set = KnGeneralUtil.getDefaultXDMCorpFS2Set(XDMCORPFS2_SET.EMERGENCY_CONF_TIMER_FEATURE.value(), com.kodiak.common.resources.KnConstants.TRUE);
            xdmCorpFS2Set = featureSetUtil.calculateXDMCorpFS2(xdmCorpFS2Set, XDMCORPFS2_SET.EMERGENCY_CONF_TIMER_FEATURE.value(), com.kodiak.common.resources.KnConstants.TRUE);
            corpProfilePersistDTO.setXdmCorpFS2Set(xdmCorpFS2Set);

            corpId = provXDMServerDAO.createCorporateProfile(corpProfilePersistDTO, persisterTxn);

            response.setCorpId(corpId);
            response.setResponseStatus(KnConstants.RESPONSE_STATUS.SUCCESS.value());
            response.setResponseMessage(KnProvConstants.CREATE_CORP_PROFILE_SUCCESS);
            response.setResponseCode(KnProvConstants.SUCCESS_CODE);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred : ", e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", e);
        }
        knLogger.debug(methodName, "EXIT:", response);
        return response;
    }

    @Override
    public KnOPCorpProfileInfoDTO retrieveCorporateProfile(String extCorpId, KnPersisterTxn persisterTxn) throws KnProvBOException {
        final String methodName = "retrieveCorporateProfile(String)";
        knLogger.info(methodName, "ENTRY :  extCorpId-", extCorpId);
        KnOPCorpProfileInfoDTO response = null;
        response = provInfoUtil.retrieveCorpProfile(extCorpId, persisterTxn);
        knLogger.info(methodName, "EXIT :  response-", response);
        return response;
    }

  /*  public KnOPProvDTO updateLastProfileUpdateTime(KnXDMSubsProvInfoDTO subsProvInfoDTO, KnPersisterTxn persisterTxn) throws KnProvBOException {
        final String methodName = "updateLastProfileUpdateTime(KnXDMSubsProvInfoDTO";
        knLogger.debug(methodName, "mdn", subsProvInfoDTO.getMdn(),);
        KnOPProvDTO responseDTO = null;
        try {
            KnSubsProfilePersistDTO subsProfilePersistDTO = new KnSubsProfilePersistDTO();
            subsProfilePersistDTO.setMdn(subsProvInfoDTO.getMdn());
            subsProfilePersistDTO.setLastProfileUpdateTime(subsProvInfoDTO.getLastProfileUpdateTime());
            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            provXDMServerDAO.updateLastProfileUpdateTime(subsProfilePersistDTO, persisterTxn);
            responseDTO = new KnOPProvDTO();
            //

        } catch (KnDAOException e) {
            e.printStackTrace();
        }
        knLogger.debug(methodName, "EXIT", responseDTO);
        return responseDTO;
    }*/


    /**
     * this method will accept the internal corpid and update the last profile update time for the corp
     *
     * @param corpId
     * @param persisterTxn
     * @return
     * @throws KnProvBOException
     */
    public KnOPProvDTO updateCorpProfileLastUpdateTime(int corpId, KnPersisterTxn persisterTxn) throws KnProvBOException {
        final String methodName = "updateCorpProfileLastUpdateTime(int,long)";
        knLogger.info(methodName, "ENTRY : corpId", corpId);
        KnOPProvDTO response = null;
        long lastProfileUpdateTime = System.currentTimeMillis();
        knLogger.info(methodName, "lastProfileUpdateTime", lastProfileUpdateTime);
        try {
            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            provXDMServerDAO.updateCorpProfileLastUpdateTime(corpId, lastProfileUpdateTime, persisterTxn);
            response = new KnOPProvDTO();
            response.setResponseStatus(KnConstants.RESPONSE_STATUS.SUCCESS.value());
            response.setResponseCode(KnProvConstants.SUCCESS_CODE);
            response.setResponseMessage(KnProvConstants.UPDATE_CORP_PROFILELASTUPDATE_TIME_SUCCESS);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred : ", e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", e);
        } /*catch (KnProvBOException e) {
            knLogger.error(methodName, "DAO Exception occurred : ", e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", e);
        }*/
        knLogger.info(methodName, "EXIT : ", response);
        return response;

    }

    public KnOPProvDTO updateEtagForNNISubscr(String extCorpId, KnPersisterTxn persisterTxn) throws KnProvBOException {
        final String methodName = "updateEtagForNNISubscr(String)";
        knLogger.debug(methodName, "ENTRY : extCorpId-", extCorpId);
        KnOPProvDTO response = new KnOPProvDTO();
        long lastProfileUpdateTime = System.currentTimeMillis();
        IProvXDMServerDAO provXDMServerDAO = null;
        try {
            provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            KnOPCorpProfileInfoDTO corpProfileInfoDTO = provXDMServerDAO.retrieveCorporateProfile(extCorpId, persisterTxn);
            int corpId = corpProfileInfoDTO.getCorpId();
            if (corpId != 0) {
                knLogger.debug(methodName, "Corporate Exists");
                List<Integer> clientTypeList = new ArrayList<>();
                Collections.addAll(clientTypeList, com.kodiak.common.resources.KnConstants.SUBS_CLIENT_TYPE.ALIASMDN.value(),
                        com.kodiak.common.resources.KnConstants.SUBS_CLIENT_TYPE.GROUPMDN.value(), com.kodiak.common.resources.KnConstants.SUBS_CLIENT_TYPE.SGMDNPATCH.value());
                int aliasNGroupMdnCount = provXDMServerDAO.getSubsCountOfClientTypeForCorp(corpId, clientTypeList, persisterTxn);
                if (aliasNGroupMdnCount == 0) {
                    knLogger.debug(methodName, "Alias/Group mdn are not present in the corp with extCorp =", extCorpId, " ,Nullify LinkedGwKey ");
                    KnCorpProfilePersistDTO corpProfilePersistDTO = new KnCorpProfilePersistDTO();
                    corpProfilePersistDTO.setExtCorpId(extCorpId);
                    provXDMServerDAO.updateLinkedGwKeyOfCorp(corpProfilePersistDTO, persisterTxn);
                    knLogger.debug(methodName, "LinkedGwKey is nullified in corpInfo table");
                } else {
                    knLogger.debug(methodName, "Alias/Group mdn are  present in the corp with extCorp =", extCorpId);
                    if (corpProfileInfoDTO.getLinkedGwKey() != null) {
                        knLogger.debug(methodName, "Link exists for corpinfo table");
                        String linkedGwKey = corpProfileInfoDTO.getLinkedGwKey();
                        KnCorpGwLinkedAccInfoDTO corpGwLinkedAccInfoDTO = new KnCorpGwLinkedAccInfoDTO();
                        corpGwLinkedAccInfoDTO.setCorpNNIRefId(linkedGwKey);
                        corpGwLinkedAccInfoDTO.setLastUpdateTime(lastProfileUpdateTime);
                        knLogger.debug(methodName, "updating the etag for CorpGwLinkedAccountInfo table for corNniRefId = ", linkedGwKey);
                        provXDMServerDAO.updateEtag4corpNNIRefId(corpGwLinkedAccInfoDTO, persisterTxn);
                        knLogger.debug(methodName, "updated the etag for CorpGwLinkedAccountInfo table");
                    } else {
                        knLogger.info(methodName, "LinkedGwKey for the Corporate is null");
                    }
                }
            } else {
                knLogger.debug(methodName, "Corporate doesn't exist");
            }


            response = new KnOPProvDTO();
            response.setResponseStatus(KnConstants.RESPONSE_STATUS.SUCCESS.value());
            response.setResponseMessage(KnProvConstants.UPDATE_ETAG_NNI_SUBSCR);
            response.setResponseCode(KnProvConstants.SUCCESS_CODE);

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred : ", e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", e);
        }
        knLogger.debug(methodName, "EXIT ", response);
        return response;

    }

    /**
     * this method will check if the mdn is present in pseudo no pool and pamaccountinfo table or not.
     *
     * @param mdn
     * @param persisterTxn
     * @return
     * @throws KnProvBOException
     */
    public boolean isMdnExistsInPseudoPoolNPamAccInfo(String mdn, KnPersisterTxn persisterTxn) throws KnProvBOException {
        final String methodName = "isMdnExistsInPseudoPoolNPamAccInfo(mdn,persisterTxn)";
        knLogger.debug(methodName, "ENTRY :  mdn ", KnGDPRTemplate.mdn(mdn));
        IProvXDMServerDAO provXDMServerDAO = null;
        boolean isMdnExistsInPseudoPoolNPamAccInfo = false;
        try {
            provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            boolean isMdnPresentInPamAccInfoTable = provXDMServerDAO.isMdnPresentInPamAccInfo(mdn, persisterTxn);
            knLogger.debug(methodName, "isMdnPresentInPamAccInfoTable", isMdnPresentInPamAccInfoTable);
            if (isMdnPresentInPamAccInfoTable) {
                isMdnExistsInPseudoPoolNPamAccInfo = true;
                knLogger.debug(methodName, "Mdn is present in PamAccInfo table,isMdnExistsInPseudoPoolNPamAccInfo=", isMdnExistsInPseudoPoolNPamAccInfo);
            }
        } catch (KnDBConnectionException ex) {
            knLogger.error(methodName, "DAO DBConnection Exception occured :", ex);
            throw new KnProvBOException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred : ", e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", e);
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred : ", e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred : ", e);
        }
        knLogger.debug(methodName, "EXIT : isMdnExistsInPseudoPoolNPamAccInfo ", isMdnExistsInPseudoPoolNPamAccInfo);
        return isMdnExistsInPseudoPoolNPamAccInfo;

    }

    public KnOPUpdateSubsInfoDTO updateAutoPairing(KnIPSubsProvInfoDTO subsProvInfoInputDTO, KnPersisterTxn persisterTxn) throws KnProvBOException {
        final String methodName = "updateAutoPairing(KnIPSubsProvInfoDTO, KnPersisterTxn)";
        knLogger.entry(methodName, subsProvInfoInputDTO);
        KnOPUpdateSubsInfoDTO updateSubsInfoDTO = new KnOPUpdateSubsInfoDTO();
        try {
            KnOPCorpProfileInfoDTO corpProfileInfoDTO = this.retrieveCorporateProfile(subsProvInfoInputDTO.getExtCorpId(), persisterTxn);
            if (corpProfileInfoDTO.getExtCorpId() != null) {
                knLogger.debug(methodName, "Corp Exist check for pairing indicator");
                Map<String, String> paramNameValueMap = genInfoUtil.retrieveRTXConfigValues(persisterTxn);
                knLogger.debug(methodName, "param value map", paramNameValueMap);
                boolean reqpairingInd = subsProvInfoInputDTO.getPairingInd();
                int corpContactListID = corpProfileInfoDTO.getPairedContactListId();
                if (reqpairingInd) {
                    //check if request is for autpopairing enable and corp autopairing is already enabled.
                    if (corpContactListID > 0) {
                        knLogger.debug(methodName, "corp is already autopaired.");
                        throw new KnProvBOException(KnErrorCodes.BOEntity.NO_CHANGE_IN_AUTO_PAIR_INDICATOR, "Autopairing is already enabled for this corp");
                    } else {
                        // validation before calling auto pair enable
                        String paringSize = paramNameValueMap.get(KnConstants.CORP_AUTO_PAIRING_SIZE);
                        if (paringSize == null || paringSize.isEmpty()) {
                            knLogger.error(methodName, "Auto pairing size is not configured in DB RTX table");
                            throw new KnProvBOException(KnErrorCodes.BOEntity.MANDATORY_PARAMETERS_ARE_MISSING, "Mandatory parameters are missing");
                        }
                        int corpAutoPairingCnt = Integer.valueOf(paringSize);
                        int corpSubsCount = provInfoUtil.retrieveCorpSubsCount(corpProfileInfoDTO.getCorpId(), persisterTxn);
                        if (corpSubsCount > corpAutoPairingCnt) {
                            knLogger.debug(methodName, "corp auto pair limit is reached.");
                            throw new KnProvBOException(KnErrorCodes.BOEntity.CORP_AUTO_PAIR_LIMIT_REACHED, "corp auto pair limit is reached");
                        }
                        updateSubsInfoDTO.setCorpAutoPairing(true);
                    }
                } else {
                    knLogger.debug(methodName, "request is for autopair disable.. check auto pair is already disabled.");
                    if (corpContactListID <= 0) {
                        knLogger.debug(methodName, "corp is already unpaired.");
                        throw new KnProvBOException(KnErrorCodes.BOEntity.NO_CHANGE_IN_AUTO_PAIR_INDICATOR, "Autopairing is already disabled for this corp");
                    }
                    updateSubsInfoDTO.setCorpAutoPairing(false);
                }
            } else {
                knLogger.error(methodName, "Corp-ID Not Exist");
                throw new KnProvBOException(KnErrorCodes.BOEntity.EXT_CORP_ID_DOES_NOT_EXIST, "Corp-ID doesn't exists");
            }
            updateSubsInfoDTO.setCorpId(corpProfileInfoDTO.getCorpId());

        } catch (KnBOException ex) {
            knLogger.error(methodName, "BO Exception occurred : ", ex);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "BO exception occurred : ", ex);
        }

        knLogger.exit(methodName, updateSubsInfoDTO.getCorpAutoPairing());
        return updateSubsInfoDTO;
    }

    public KnOPSubsProfileInfoDTO createTPUser(KnTPUserInfoDTO tpUserInfoDTO, KnPersisterTxn persisterTxn) throws KnProvBOException {
        final String methodName = "createTPUser(KnTPUserInfoDTO, KnPersisterTxn)";
        knLogger.entry(methodName, tpUserInfoDTO);
        KnOPSubsProfileInfoDTO subsProfileRespDTO;
        try {
            String billingMDN = tpUserInfoDTO.getBillingMdn();

            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            //check vendor details are exist or not
            KnTPVendorDetailsDTO vendorDetails = provXDMServerDAO.retrieveTPVendorDetails(tpUserInfoDTO.getVendorId(), persisterTxn);
            if (vendorDetails == null) {
            	knLogger.error(methodName, "Vendor details not exist. throwing exception");
        		throw new KnProvBOException(KnErrorCodes.BOEntity.TP_VENDOR_NOT_EXIST, "TP Vendor details not exist");
            }

            //check user is already mapped
            KnTPUserAccountDTO userAccountDTO = provXDMServerDAO.getTPUserDetails(tpUserInfoDTO.getTpUser(), persisterTxn);
            if (userAccountDTO != null ) {
            	knLogger.error(methodName, "TP User already Exists");
        		throw new KnProvBOException(KnErrorCodes.BOEntity.TP_USER_ALREADY_EXIST, "TP User already Exists");
            }

            KnOPPAMAccInfoDTO existBillingAcc = provXDMServerDAO.retrieveLicensePackInfo(billingMDN, persisterTxn);

            if (existBillingAcc != null) {
            	knLogger.info(methodName, "its Billing MDN");
            	int pamAccState = existBillingAcc.getPamAccState();

            	if (KnProvConstants.PAM_ACCOUNT_STATE.SUSPEND.value() == pamAccState) {
            		knLogger.error(methodName, "PAM Account is in suspended state. throwing exception");
            		throw new KnProvBOException(KnErrorCodes.BOEntity.BILLINGMDN_IS_SUSPEND, "Billing MDN is in suspended state");

            	} else if (KnProvConstants.PAM_ACCOUNT_STATE.DELETE_IN_PROGRESS.value() == pamAccState) {
            		knLogger.error(methodName, "PAM Account is in Delete in Progress state. throwing exception");
            		throw new KnProvBOException(KnErrorCodes.BOEntity.PAMACCOUNT_DELETE_IN_PROGRESS, "Billing MDN is in Delete in Progress state");
            	}

            	KnPAMAccPoolUsageDTO pamAccPoolUsageDTO = provXDMServerDAO.getFirstUnusedMdnFromPAMAccPoolUsage(billingMDN, persisterTxn);
            	if (pamAccPoolUsageDTO == null) {
            		knLogger.error(methodName, "No free pseudo MDN in pool, throwing exception");
            		throw new KnProvBOException(KnErrorCodes.BOEntity.NO_FREE_PSEUDOMDN_IN_POOL, "No free pseudo MDN in pool");
            	}
            	String mdn = pamAccPoolUsageDTO.getMdn();

            	KnTPUserPersistDTO tpUserPersistDTO = new KnTPUserPersistDTO();
            	tpUserPersistDTO.setTpUser(tpUserInfoDTO.getTpUser());
            	tpUserPersistDTO.setTpAccountId(vendorDetails.getTpAccountId());
            	tpUserPersistDTO.setMdn(mdn);

            	//creating TP account
            	provXDMServerDAO.createTPUserMDNMap(tpUserPersistDTO, persisterTxn);

            	//update pseudo mdn as used(2)
            	IXDMServerDAO commonXDMServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(this.getXDMPttServerId());
            	List<String> mdnList = new ArrayList<>();
            	mdnList.add(mdn);
            	commonXDMServerDAO.updateUsageByMDNs(mdnList, KnConstants.PAMACCOUNT_POOL_USAGE.IN_USE.value(), persisterTxn);

            	//getting subscriber details
            	subsProfileRespDTO = provInfoUtil.retrieveSubscriberInfo(mdn, persisterTxn);

            } else {
            	String mdn = tpUserInfoDTO.getBillingMdn();
            	subsProfileRespDTO = provInfoUtil.retrieveSubscriberInfo(mdn, persisterTxn);

            	if (subsProfileRespDTO.getPamAccId() != 0) {
            		knLogger.error(methodName, "Invalid MDN passed");
            		throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_INFO_NOT_FOUND, "Invalid MDN passed");
            	}

            	knLogger.info(methodName, "Its a real MDN");
            	int clientType =subsProfileRespDTO.getSubsClientType();
            	knLogger.info(methodName, "subs client type -",clientType);

            	if (clientType != KnConstants.SUBSCRIBERS_CLIENT_TYPE.THIRDPARTYPOCCLIENT.value() &&
            			clientType != KnConstants.SUBSCRIBERS_CLIENT_TYPE.THIRDPARTYDISPATCHERCLIENT.value() &&
            			clientType != KnConstants.SUBSCRIBERS_CLIENT_TYPE.MOBILE_CLIENT.value()) {

            		knLogger.error(methodName, "Invalid client type");
            		throw new KnProvBOException(KnErrorCodes.Validator.INVALID_SUBS_CLIENT_TYPE, "Invalid subscriber client type");
            	}

            	if (KnProvConstants.PAM_ACCOUNT_STATE.SUSPEND.value() == subsProfileRespDTO.getServiceAuthStatus()) {
            		knLogger.error(methodName, "MDN is in suspended state. throwing exception");
            		throw new KnProvBOException(KnErrorCodes.BOEntity.BILLINGMDN_IS_SUSPEND, "MDN is in suspended state");
            	}

            	KnTPUserPersistDTO tpUserPersistDTO = new KnTPUserPersistDTO();
            	tpUserPersistDTO.setTpUser(tpUserInfoDTO.getTpUser());
            	tpUserPersistDTO.setTpAccountId(vendorDetails.getTpAccountId());
            	tpUserPersistDTO.setMdn(mdn);

            	//creating TP account
            	provXDMServerDAO.createTPUserMDNMap(tpUserPersistDTO, persisterTxn);
            }

        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occurred : ", ex);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
        }

        knLogger.exit(methodName, "Subscriber details-", subsProfileRespDTO);
        return subsProfileRespDTO;
    }

    @Override
    public KnOPSubsProfileInfoDTO updateTPUser(KnTPUserInfoDTO tpUserInfoDTO, KnPersisterTxn persisterTxn) throws KnProvBOException {
        final String methodName = "updateTPUser(KnTPUserInfoDTO, KnPersisterTxn)";
        knLogger.entry(methodName, tpUserInfoDTO);
        KnOPSubsProfileInfoDTO subsProfileRespDTO;
        try {
            String mdn = tpUserInfoDTO.getMdn();
            subsProfileRespDTO = provInfoUtil.retrieveSubscriberInfo(mdn, persisterTxn);
            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();

            KnTPVendorDetailsDTO vendorDetails = provXDMServerDAO.retrieveTPVendorDetails(tpUserInfoDTO.getVendorId(), persisterTxn);
            if (vendorDetails == null) {
            	knLogger.error(methodName, "Vendor details not exist. throwing exception");
        		throw new KnProvBOException(KnErrorCodes.BOEntity.TP_VENDOR_NOT_EXIST, "TP Vendor details not exist");
            }

            //check user is already mapped
            KnTPUserAccountDTO userAccountDTO = provXDMServerDAO.getTPUserDetails(tpUserInfoDTO.getTpUser(), persisterTxn);
            if (userAccountDTO != null ) {
            	knLogger.error(methodName, "TP User already Exists");
        		throw new KnProvBOException(KnErrorCodes.BOEntity.TP_USER_ALREADY_EXIST, "TP User already Exists");
            }

            if (subsProfileRespDTO.getPamAccId() != 0) {
            	knLogger.info(methodName, "its a Pseudo number");
            	int usage = provXDMServerDAO.retrievePAMAccPoolUsageForMDN(mdn, persisterTxn);

            	if (usage == KnConstants.PAMACCOUNT_POOL_USAGE.MDN_NOT_EXIST.value()) {
            		knLogger.error(methodName, "MDN not exist");
            		throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_INFO_NOT_FOUND, "MDN not exist");

            	} else if (usage == KnConstants.PAMACCOUNT_POOL_USAGE.NOT_IN_USE.value()) {
            		knLogger.error(methodName, "MDN is in not in use state");
            		throw new KnProvBOException(KnErrorCodes.BOEntity.MDN_IS_IN_NOT_USED_STATE, "MDN is in not in use state");
            	}

            } else  {
            	knLogger.info(methodName, "its a real number");
            	int clientType =subsProfileRespDTO.getSubsClientType();

            	if (clientType != KnConstants.SUBSCRIBERS_CLIENT_TYPE.THIRDPARTYPOCCLIENT.value() &&
            			clientType != KnConstants.SUBSCRIBERS_CLIENT_TYPE.THIRDPARTYDISPATCHERCLIENT.value() &&
            			clientType != KnConstants.SUBSCRIBERS_CLIENT_TYPE.MOBILE_CLIENT.value()) {
            		knLogger.error(methodName, "Invalid client type");
            		throw new KnProvBOException(KnErrorCodes.Validator.INVALID_SUBS_CLIENT_TYPE, "Invalid subscriber client type");
            	}
            }

            //getting subscriber details
            subsProfileRespDTO = provInfoUtil.retrieveSubscriberInfo(mdn, persisterTxn);
            if (KnConstants.SERVICE_AUTH_STATUS.DEACTIVATED.value() == subsProfileRespDTO.getServiceAuthStatus()) {
                knLogger.error(methodName, "MDN is in suspended state. throwing exception");
                throw new KnProvBOException(KnErrorCodes.BOEntity.SUBS_IS_DEACTIVATED, "MDN is in suspended state");
            }

            //Retrieve activation code for MDN
            IXDMServerDAO commonXDMServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(this.getXDMPttServerId());
            String activationCode = commonXDMServerDAO.retrieveActivationCode(mdn, persisterTxn);

            if (!tpUserInfoDTO.getActivationCode().equals(activationCode)) {
                knLogger.error(methodName, "MDN and activationCode are not associated");
                throw new KnProvBOException(KnErrorCodes.BOEntity.MDN_ACTIVATION_CODE_NOT_ASSOCIATED, "MDN and activationCode are not associated");
            }

            KnTPUserPersistDTO tpUserPersistDTO = new KnTPUserPersistDTO();
            tpUserPersistDTO.setTpUser(tpUserInfoDTO.getTpUser());
            tpUserPersistDTO.setTpAccountId(vendorDetails.getTpAccountId());
            tpUserPersistDTO.setMdn(mdn);

            //retrieve TPUser account for MDN
            KnTPUserAccountDTO tpUserAccountDTO = provXDMServerDAO.retrieveTPUserAccountForMDN(mdn, persisterTxn);

            if (tpUserAccountDTO == null) {
                //creating TP account
                knLogger.debug(methodName, "TP Account not exist.hence creating");
                provXDMServerDAO.createTPUserMDNMap(tpUserPersistDTO, persisterTxn);

            } else {
                provXDMServerDAO.updateTPUserMDNMapByMDN(tpUserPersistDTO, persisterTxn);
            }

        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occurred : ", ex);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
        }

        knLogger.exit(methodName, "Subscriber details-", subsProfileRespDTO);
        return subsProfileRespDTO;
    }

    @Override
    public KnOPProvDTO deleteTPUser(KnTPUserInfoDTO tpUserInfoDTO, KnPersisterTxn persisterTxn) throws KnProvBOException {
        final String methodName = "deleteTPUser(KnTPUserInfoDTO, KnPersisterTxn)";
        knLogger.entry(methodName, tpUserInfoDTO);
        KnOPProvDTO responseDTO;
        try {
            String mdn = tpUserInfoDTO.getMdn();

            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();

            //Remove activation code
            IXDMServerDAO commonXDMServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(this.getXDMPttServerId());
            commonXDMServerDAO.removeActivationCode(mdn, persisterTxn);

            KnTPUserPersistDTO tpUserPersistDTO = new KnTPUserPersistDTO();
            tpUserPersistDTO.setMdn(mdn);

            //deleteTPUser for pseudoMDN
            provXDMServerDAO.deleteTPUserMDNMap(tpUserPersistDTO, persisterTxn);

            //update usage to not in use for pseudo mdn
            List<String> mdnList = new ArrayList<>();
            mdnList.add(mdn);
            commonXDMServerDAO.updateUsageByMDNs(mdnList, KnConstants.PAMACCOUNT_POOL_USAGE.NOT_IN_USE.value(), persisterTxn);

           //update pocsubsinfo CORPCONTACTLISTID as null
            provXDMServerDAO.updateSubsContactListID(0, mdn, persisterTxn);

            responseDTO = new KnOPProvDTO();
            responseDTO.setResponseMessage(KnProvConstants.DELETE_TP_ACCOUNT_SUCCESS);
            responseDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.SUCCESS.value());

        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occurred : ", ex);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
        }

        knLogger.exit(methodName, responseDTO);
        return responseDTO;
    }

    @Override
    public void verifyTPAccount(KnTPUserInfoDTO tpUserInfoDTO, KnPersisterTxn persisterTxn) throws KnProvBOException {
    	  final String methodName = "generateTPActivationCode(KnTPUserInfoDTO, KnPersisterTxn)";
          knLogger.entry(methodName, tpUserInfoDTO);
          try {

              IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
              KnTPVendorDetailsDTO vendorDetails = provXDMServerDAO.retrieveTPVendorDetails(tpUserInfoDTO.getVendorId(), persisterTxn);
              if (vendorDetails == null) {
              	knLogger.error(methodName, "Vendor details not exist. throwing exception");
          		throw new KnProvBOException(KnErrorCodes.BOEntity.TP_VENDOR_NOT_EXIST, "TP Vendor details not exist");
              }

              //check user is already mapped
              String tpUser = tpUserInfoDTO.getTpUser();
              KnTPUserAccountDTO userAccountDTO = null;
              if (tpUser != null) {
            	  userAccountDTO = provXDMServerDAO.getTPUserDetails(tpUserInfoDTO.getTpUser(), persisterTxn);
            	  if (userAccountDTO == null ) {
            		  knLogger.error(methodName, "TP User not Exist");
            		  throw new KnProvBOException(KnErrorCodes.BOEntity.TP_USER_NOT_EXIST, "TP User not Exist");
            	  }

            	  if (userAccountDTO.getTpAccId() != vendorDetails.getTpAccountId()) {
            		  knLogger.error(methodName, "VendorId and userId are not Associated");
            		  throw new KnProvBOException(KnErrorCodes.BOEntity.TP_USERID_VENDORID_NOT_ASSOSIATED, "VendorId and userId are not Associated");
            	  }

            	  if (tpUserInfoDTO.getMdn() !=null && !tpUserInfoDTO.getMdn().equals(userAccountDTO.getMdn())) {
            		  knLogger.error(methodName, "userId and mdn are not Associated");
            		  throw new KnProvBOException(KnErrorCodes.BOEntity.TP_USERID_MDN_NOT_ASSOSIATED, "userId and mdn are not Associated");
            	  }

              }

              if (tpUserInfoDTO.getMdn() !=null) {
            	  userAccountDTO = provXDMServerDAO.retrieveTPUserAccountForMDN(tpUserInfoDTO.getMdn(), persisterTxn);
            	  if (userAccountDTO == null ) {
            		  knLogger.error(methodName, "mdn not Exist");
            		  throw new KnProvBOException(KnErrorCodes.BOEntity.TP_MDN_NOT_EXIST, "mdn not Exist");
            	  }

            	  if (userAccountDTO.getTpAccId() != vendorDetails.getTpAccountId()) {
            		  knLogger.error(methodName, "VendorId and mdn are not Associated");
            		  throw new KnProvBOException(KnErrorCodes.BOEntity.TP_MDN_VENDORID_NOT_ASSOSIATED, "VendorId and mdn are not Associated");
            	  }
              } else {
            	  tpUserInfoDTO.setMdn(userAccountDTO.getMdn());
              }

              } catch (KnDAOException ex) {
                  knLogger.error(methodName, "DAO Exception occurred : ", ex);
                  throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
              }
    }

    private String getXDMPttServerId() throws KnProvBOException {
        final String methodName = "getXDMPttServerId";
        String xdmPttServerId;
        try {
            xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();

        } catch (KnBOException e) {
            knLogger.error(methodName, "failed to retrieve xdm Ptt Sever Id", e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.XDMS_PTT_ID_NOT_FOUND, "Failed to retrieve XDM PTT Server ID", e);
        }
        knLogger.exit(methodName, xdmPttServerId);
        return xdmPttServerId;
    }

    private int createClientSuppVocoder(List<KnClientVocoderProfilePersistDTO> mdnInfoDTO, KnPersisterTxn persisterTxn)throws KnProvBOException{
        final String methodName = "createClientSuppVocoder(KnIPActivateMDNInfoDTO, KnPersisterTxn)";
        Map<Integer, KnSuppVocoderProfileDTO> serverSuppVocoders;
        int finalSelectedVocoderId = 0;
        try {
            serverSuppVocoders = genInfoUtil.retrieveSuppVocoderProfile(persisterTxn);
            knLogger.debug(methodName," retrieve Supp Vocoder file :",serverSuppVocoders);
            Map<Integer,Integer> clientVocoderId = new TreeMap<>();
            for (KnClientVocoderProfilePersistDTO vocoderProfilePersistDTO : mdnInfoDTO){
                // vocoder id is now profileId variable name kept same
                clientVocoderId.put(vocoderProfilePersistDTO.getPriority(),vocoderProfilePersistDTO.getVocoderId());
            }
            knLogger.debug(methodName," client sent profile id map :",clientVocoderId);
            for(Map.Entry<Integer, Integer> entry : clientVocoderId.entrySet())
            {
                if( serverSuppVocoders.containsKey(entry.getValue())){
                finalSelectedVocoderId =  serverSuppVocoders.get(entry.getValue()).getProfileId();
                break;
                }

            }
            if (finalSelectedVocoderId == 0){
                throw new KnProvBOException(KnErrorCodes.BOEntity.VOCODERID_MISMATCH, "Vocoder ID Mismatch");
            }
            String mdn = mdnInfoDTO.get(0).getMdn();
            List<KnClientVocoderProfilePersistDTO> clientVocoderProfilePersistDTOs = new ArrayList<>();
            for (Integer priority : clientVocoderId.keySet()){
                KnClientVocoderProfilePersistDTO clientVocoderProfilePersistDTO = new KnClientVocoderProfilePersistDTO();
                clientVocoderProfilePersistDTO.setVocoderId(clientVocoderId.get(priority));
                clientVocoderProfilePersistDTO.setPriority(priority);
                clientVocoderProfilePersistDTO.setMdn(mdn);
                clientVocoderProfilePersistDTOs.add(clientVocoderProfilePersistDTO);
            }

            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            provXDMServerDAO.deleteClientSuppVocoder(mdn,persisterTxn);
            knLogger.debug(methodName,"Deleted existing vocoder from clientSuppVocoder DB");
            provXDMServerDAO.createClientSuppVocoder(clientVocoderProfilePersistDTOs,persisterTxn);
            knLogger.debug(methodName,"Created vocoder from clientSuppVocoder in DB for mdn:",KnGDPRTemplate.mdn(mdn)," List :",clientVocoderId);
            knLogger.debug(methodName," final selected vocoder ID :",finalSelectedVocoderId);

        }catch (KnDAOException ex){
            knLogger.error(methodName, "DAO Exception occurred : ", ex);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
        }catch (KnBOException e){
            knLogger.error(methodName,"Vocoder ID sent by client not matched");
            throw new KnProvBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return finalSelectedVocoderId;

    }

    /**
     * method to retrieve the bulk Subscriber Details
     *
     * @param subscriberDTO KnIPSubscriberInfoDTO
     * @param persisterTxn  KnPersisterTxn
     * @return KnOPSubsProfileInfoDTO Object
     * @throws KnProvBOException
     */
    public KnOPSubsProfileInfoDTO getSubsDetails(KnIPSubscriberInfoDTO subscriberDTO, boolean readOnly, KnPersisterTxn persisterTxn) throws KnProvBOException {
        String methodName = "getSubsDetails(KnIPSubscriberInfoDTO,boolean, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY: getSubscriberDetails with DTO ", subscriberDTO, " readOnly :", readOnly);
        KnOPSubsProfileInfoDTO subsProfileRespDTO = null;
        Map<String, KnSubsCameraInfo>  subsCameraInfoMap = null;
        Map<String,List<KnSubsAliasInfoDTO>> subsAliasIdInfoMap = null;
        try {
            xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
            IXDMServerDAO commonXDMServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
            List<String> mcDataIds = subscriberDTO.getMcDataIds();
            if(mcDataIds != null && !mcDataIds.isEmpty()){
                IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
                List<KnOPSubsProfileInfoDTO> subsProfileInfoDTOList = provXDMServerDAO.selectSubscriberProfileByMCDataIds(mcDataIds, persisterTxn);
                List<String> mdnList = subscriberDTO.getMdnList();
                if(mdnList != null) {
                    if(subsProfileInfoDTOList != null) mdnList.addAll(subsProfileInfoDTOList.stream().map(KnOPSubsProfileInfoDTO::getMdn)
                            .collect(Collectors.toList()));
                } else {
                    subscriberDTO.setMdnList(subsProfileInfoDTOList != null ? subsProfileInfoDTOList.stream().map(KnOPSubsProfileInfoDTO::getMdn)
                            .collect(Collectors.toList()) : null);
                }
            }

            List<String> userIds = subscriberDTO.getUserIds();
            if(userIds != null && !userIds.isEmpty()){
                IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
                List<KnOPSubsProfileInfoDTO> subsProfileInfoDTOList = provXDMServerDAO.selectSubscriberProfileByUserIds(userIds, persisterTxn);
                List<String> mdnList = subscriberDTO.getMdnList();
                if(mdnList != null) {
                    if(subsProfileInfoDTOList != null) mdnList.addAll(subsProfileInfoDTOList.stream().map(KnOPSubsProfileInfoDTO::getMdn)
                            .collect(Collectors.toList()));
                } else {
                    subscriberDTO.setMdnList(subsProfileInfoDTOList != null ? subsProfileInfoDTOList.stream().map(KnOPSubsProfileInfoDTO::getMdn)
                            .collect(Collectors.toList()) : null);
                }
            }

            if(subscriberDTO.getAliasMdnList() != null && !subscriberDTO.getAliasMdnList().isEmpty()){
                IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
                List<KnOPSubsProfileInfoDTO> subsProfileInfoDTOList = provXDMServerDAO.selectSubscriberProfileByAliasMdn(subscriberDTO.getAliasMdnList(), persisterTxn);
                List<String> mdnList = subscriberDTO.getMdnList();
                if(mdnList != null) {
                    if(subsProfileInfoDTOList != null) mdnList.addAll(subsProfileInfoDTOList.stream().map(KnOPSubsProfileInfoDTO::getMdn)
                            .collect(Collectors.toList()));
                } else {
                    subscriberDTO.setMdnList(subsProfileInfoDTOList != null ? subsProfileInfoDTOList.stream().map(KnOPSubsProfileInfoDTO::getMdn)
                            .collect(Collectors.toList()) : null);
                }
            }

            //If input alaiasIdInfo is passed then get coressponding mdns based on alaiasId & alaiasId issuer
            //Input will either of mdns,mcIds,alaiasMdns or alaiasInfo hence repalcing mdnList directly with the data that recived from db for alasIdInfo
            List<KnSubsAliasInfoDTO> subsAliasInfoList = subscriberDTO.getSubsAliasInfoList();
            if(subsAliasInfoList != null && !subsAliasInfoList.isEmpty()){
                IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
                Map<String,String> aliasIdIPMap = subsAliasInfoList.stream().collect(Collectors.toMap(KnSubsAliasInfoDTO::getAliasId,KnSubsAliasInfoDTO::getAliasIdIssuer));
                subsAliasIdInfoMap = provXDMServerDAO.selectSubsAliasIdInfo(aliasIdIPMap, readOnly, persisterTxn);
                if(subsAliasIdInfoMap!=null && !subsAliasIdInfoMap.isEmpty())
                    subscriberDTO.setMdnList(new ArrayList<>(subsAliasIdInfoMap.keySet()));

            }

            if(subscriberDTO.getAliasMdnList() != null && !subscriberDTO.getAliasMdnList().isEmpty()){
                IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
                List<KnOPSubsProfileInfoDTO> subsProfileInfoDTOList = provXDMServerDAO.selectSubscriberProfileByAliasMdn(subscriberDTO.getAliasMdnList(), persisterTxn);
                List<String> mdnList = subscriberDTO.getMdnList();
                knLogger.debug(methodName, "AliasMdnsMdnList::", KnGDPRTemplate.mdnList(mdnList));
                if(mdnList != null) {
                    if(subsProfileInfoDTOList != null) mdnList.addAll(subsProfileInfoDTOList.stream().map(KnOPSubsProfileInfoDTO::getMdn)
                            .collect(Collectors.toList()));
                } else {
                    subscriberDTO.setMdnList(subsProfileInfoDTOList != null ? subsProfileInfoDTOList.stream().map(KnOPSubsProfileInfoDTO::getMdn)
                            .collect(Collectors.toList()) : null);
                }
            }

            if (subscriberDTO.getMdnList() != null && !subscriberDTO.getMdnList().isEmpty()) {
                Map<String, Integer> apn = commonXDMServerDAO.retrieveAPNInfo(true, persisterTxn);
                Map<Integer, String> apnInfo = new HashMap<>();
                if (apn != null) {
                    for (Map.Entry<String, Integer> Entry : apn.entrySet()) {
                        apnInfo.put(Entry.getValue(), Entry.getKey());
                    }
                }
                subsProfileRespDTO = provInfoUtil.selectSubsProfileInfo(subscriberDTO.getMdnList(), readOnly, persisterTxn);
                Map<String, KnSubsProfileDTO> subsProfileDTOMap = subsProfileRespDTO.getSubsRespMap();
                if (subsProfileDTOMap != null && !subsProfileDTOMap.isEmpty()) {
                 List<String> finalMdnList = new ArrayList<>(subsProfileDTOMap.keySet());
                 knLogger.debug(methodName, "finalMdnList::", KnGDPRTemplate.mdnList(finalMdnList));
                 //here isMcxNotify is true, because this is used by getSubscriberDetails through XDMDataIntf and xcapRootUri is used by xcapsn.
                Map<String, String> xcapRootUriMap = genInfoUtil.getXCAPRootURI(finalMdnList, persisterTxn, true);
                IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
                    Map<String, Integer> apnProfile = provXDMServerDAO.selectSubApn(finalMdnList, readOnly, persisterTxn);
                //retrieve tier pkg & addon pkg
                    Map<String, KnSubsAddlInfoDTO> subsAddlInfoProfileDTOMap = provXDMServerDAO.retrieveSubscrAddlInfo(finalMdnList, readOnly, persisterTxn);
                //fetch cameraInfo
                    subsCameraInfoMap = provXDMServerDAO.getSubscriberCameraInfo(finalMdnList, readOnly, persisterTxn);
                //fetch alaisIdInfo if the call is happening via mdnList,aliasMDN or mc_id's
                if(subsAliasIdInfoMap==null){
                    subsAliasIdInfoMap = provXDMServerDAO.selectSubsAliasIdInfoByMdn(finalMdnList, persisterTxn);
                }

                Map<String, String> profileMdnListwithBaseMdns = provInfoUtil.getBaseMdnListForRequestingMdnList(finalMdnList, persisterTxn);

                Map<String,List <String>> addOnPkgIdsMap = provXDMServerDAO.selectSubAddOnPkgs(finalMdnList, persisterTxn);
                    for (Map.Entry<String, KnSubsProfileDTO> entry : subsProfileDTOMap.entrySet()) {
                    	String mdn=entry.getKey();
                        KnSubsProfileDTO subsProfileDTO = entry.getValue();
                        int apnId = apnProfile.get(mdn);
                        subsProfileDTO.setApnName(apnInfo.get(apnId));
                        Map<String, Map<String,Integer>> pkgIdMap = new HashMap<>();
						Map<String, Integer> pkgIds = new HashMap<>();
						if (subsAddlInfoProfileDTOMap != null && !subsAddlInfoProfileDTOMap.isEmpty()) {
							KnSubsAddlInfoDTO subsAddlInfoDTO = subsAddlInfoProfileDTOMap.get(mdn);
							if (subsAddlInfoDTO != null && subsAddlInfoDTO.getTierPkgCode() != null) {
								pkgIds.put(subsAddlInfoDTO.getTierPkgCode(), KnConstants.TIER_PKG_TYPE);
							}
						}
						if (addOnPkgIdsMap != null && !addOnPkgIdsMap.isEmpty()) {
							List<String> addonPkgs = addOnPkgIdsMap.get(mdn.trim());
							if (addonPkgs != null && !addonPkgs.isEmpty()) {
								for (String addonPkgCode : addonPkgs) {
									pkgIds.put(addonPkgCode, KnConstants.ADDON_PKG_TYPE);
								}
							}
						}
						pkgIdMap.put(KnConstants.ADD_ACTION, pkgIds);
						subsProfileDTO.setPkgIdMap(pkgIdMap);
                        subsProfileDTO.setXcapRootUri(xcapRootUriMap.get(mdn));

						if (subsProfileDTO.getUserProfileIndex() != 0) {
                            String profileName = null;
                            profileName = commonXDMServerDAO.selectUserProfileName(mdn, readOnly, persisterTxn);
                            if (profileName == null){
                                try {
                                    profileName = provInfoUtil.getUserProfileName(subsProfileDTO.getCorpId(),subsProfileDTO.getUserProfileIndex());
                                    commonXDMServerDAO.updateUserProfileName(profileName,mdn,persisterTxn);
                                }catch (KnProvBOException ex) {
                                    knLogger.error(methodName, "CBS connection error" + ex);
                                    throw new KnProvBOException(ex.getErrorCode(), ex.getMessage());
                                }}
                            subsProfileDTO.setUserProfileName(profileName);}

                            if(subsCameraInfoMap.get(mdn)!=null) {
                                subsProfileDTO.setCameraInfo(subsCameraInfoMap.get(mdn));
                            }

                            if(subsAliasIdInfoMap.get(mdn)!=null){
                                subsProfileDTO.setAliasInfoList(subsAliasIdInfoMap.get(mdn));
                            }
                        if (null != profileMdnListwithBaseMdns.get(mdn)) {
                            subsProfileDTO.setCustMcpttId(TELURI.concat(profileMdnListwithBaseMdns.get(mdn)));
                            subsProfileDTO.setCustMcVideoId(TELURI.concat(profileMdnListwithBaseMdns.get(mdn)));
                            subsProfileDTO.setCustMcDataId(TELURI.concat(profileMdnListwithBaseMdns.get(mdn)));
                        }
                    }
                }
            } else {
                knLogger.error(methodName, "Subscriber doesn't exists");
                throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_INFO_NOT_FOUND, "Subscriber doesn't exists");
            }
        } catch (KnBOException e) {
            knLogger.error(methodName, "failed to retrieve xdm Ptt Sever Id", e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.XDMS_PTT_ID_NOT_FOUND, "Failed to retrieve XDM PTT Server ID", e);
        } catch (KnDBConnectionException ex) {
            knLogger.error(methodName, "DAO DBConnection Exception occured :" + ex);
            throw new KnProvBOException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);
        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occurred : " + ex);
            if (com.kodiak.xdms.server.common.resources.KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_INFO_NOT_FOUND, "Subscriber doesn't exists");
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception occurred :" + e.getErrorCode());
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while get Subscriber details" + e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred while get Subscriber details", e);
        }
        knLogger.debug(methodName, "EXIT:  getSubscriberDetails - ", subsProfileRespDTO);
        return subsProfileRespDTO;
    }

    /**
     * Method to Switch the client profile, here in this method we are not doing validation. Only business implementation.
     * @param subsProvInputDTO      KnIPSubsProvInfoDTO
     * @param persisterTxn          KnPersisterTxn
     * @return KnOPProvDTO          Response DTO
     * @throws KnProvBOException    BO Entity Exception
     */
    public KnOPUpdateSubsInfoDTO switchConvergedClient(KnIPSubsProvInfoDTO subsProvInputDTO, KnPersisterTxn persisterTxn) throws KnProvBOException {
        String methodName = "switchConvergedClient(KnIPSubsProvInfoDTO, KnPersisterTxn)";
        KnOPUpdateSubsInfoDTO responseDTO = new KnOPUpdateSubsInfoDTO();
        KnSubsProfilePersistDTO subsProfilePersistDTO;
        knLogger.info(methodName, "ENTRY: Update Subscriber with DTO - ", subsProvInputDTO, ", Txn - ", persisterTxn);
        Collection<Integer> successPegs = new ArrayList<Integer>();
        try {
        	Map<String, String> paramNameValueMap = genInfoUtil.retrieveRTXConfigValues(persisterTxn);
            xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
            String mdn = subsProvInputDTO.getMdn();
            boolean enabledPttRadio = subsProvInputDTO.isEnablePttRadio();
            //retrieve the Subscriber Profile.
            //retrieved Profile will used further for validation input data of the against to the input DTO
            KnOPSubsProfileInfoDTO existingSubsProfileDTO = provInfoUtil.retrieveSubscriberInfo(mdn, persisterTxn);
            if(KnConstants.MCSCOMPLIANCE==existingSubsProfileDTO.getMcpttCompliance()) {
            	throw new KnProvBOException(KnErrorCodes.BOEntity.SWITCH_NOT_ALLOWED, "switchConvergedClient is not allowed for MCS client types");
            }
            IProvXDMServerDAO xdmServerDAO = KnProvFactorySelector.getProvDAOFactory(KnFactorySelector.DB).createProvXDMServerDAO();
			// retrieve tier pkg & addon pkg
			Map<String, Integer> exsitingPkgIds = new HashMap<String, Integer>();
			String existingTierPkg = null;
			KnOPSubsAddlInfoProfileDTO addlProfileDto = xdmServerDAO.retrieveSubscrAddlInfo(subsProvInputDTO.getMdn(),
					persisterTxn);
			if (addlProfileDto != null)
				existingTierPkg = addlProfileDto.getTierPkgCode();
			if (existingTierPkg != null)
				exsitingPkgIds.put(existingTierPkg, KnConstants.TIER_PKG_TYPE);
			List<String> existingAddonPkgs = xdmServerDAO.selectSubAddOnPkgs(subsProvInputDTO.getMdn(), persisterTxn);
			if (existingAddonPkgs != null) {
				for (String pkgCode : existingAddonPkgs) {
					exsitingPkgIds.put(pkgCode, KnConstants.ADDON_PKG_TYPE);
				}
			}

			knLogger.info(methodName, "exsitingPkgIds ", exsitingPkgIds);
			Map<String, Integer> exsitingSubsPkgId = new HashMap<String, Integer>(exsitingPkgIds);
			knLogger.info(methodName, "exsitingSubsPkgId ", exsitingSubsPkgId);
            Map<Integer, Integer> clientTypeMap = new HashMap<>();
            clientTypeMap.put(KnConstants.SUBSCRIBERS_CLIENT_TYPE.HANDSET.value(), KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value());
            clientTypeMap.put(KnConstants.SUBSCRIBERS_CLIENT_TYPE.CROSS_CARRIER_PTT_CLIENT.value(), KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value());
            clientTypeMap.put(KnConstants.SUBSCRIBERS_CLIENT_TYPE.WIFIONLY.value(), KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value());
            clientTypeMap.put(KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value(), KnConstants.SUBSCRIBERS_CLIENT_TYPE.HANDSET.value());
            clientTypeMap.put(KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value(), KnConstants.SUBSCRIBERS_CLIENT_TYPE.CROSS_CARRIER_PTT_CLIENT.value());
            clientTypeMap.put(KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value(), KnConstants.SUBSCRIBERS_CLIENT_TYPE.WIFIONLY.value());
            //variables to identify the profile level changes
            boolean isActiveFSUpdated = false;
            boolean isClientTypeUpdated = false;

            int oldSubsClientType = existingSubsProfileDTO.getSubsClientType();
            int newSubsClientType = clientTypeMap.get(oldSubsClientType);
            String pocPttId = existingSubsProfileDTO.getPoCHome();
            String presencePttId = existingSubsProfileDTO.getPresenceHome();
            String xdmsPttId = existingSubsProfileDTO.getXDMSHome();

            BitSet subsFS2BiSet = featureSetUtil.convertHexStringToBitSet(existingSubsProfileDTO.getSubsFS2());
            knLogger.debug(methodName, "Is USER_PROFILE_MGMT_BIT enabled", subsFS2BiSet.get(USER_PROFILE_MGMT_BIT));
            knLogger.debug(methodName, "Is MCPTT_COMPLAIANCE_BIT enabled", subsFS2BiSet.get(MCPTT_COMPLAIANCE_BIT));
            HIERARCHY_TYPE hierarchyValue = existingSubsProfileDTO.getHierarchyType();
            String firstNetIndicator = existingSubsProfileDTO.getFirstNetIndicator();
            knLogger.debug(methodName, "hierarchyValue: ", hierarchyValue, "firstNetIndicator: ", firstNetIndicator);
            Map<String, Integer> oldSubsClientTypeMap = new HashMap<>();
            oldSubsClientTypeMap.put(KnConstants.SUBSCRIBERS_CLIENT_TYPE.HANDSET.name(), KnConstants.SUBSCRIBERS_CLIENT_TYPE.HANDSET.value());
            oldSubsClientTypeMap.put(KnConstants.SUBSCRIBERS_CLIENT_TYPE.WIFIONLY.name(), KnConstants.SUBSCRIBERS_CLIENT_TYPE.WIFIONLY.value());
            oldSubsClientTypeMap.put(KnConstants.SUBSCRIBERS_CLIENT_TYPE.CROSS_CARRIER_PTT_CLIENT.name(), KnConstants.SUBSCRIBERS_CLIENT_TYPE.CROSS_CARRIER_PTT_CLIENT.value());
            Map<String, Integer> newSubsClientTypeMap = new HashMap<>();
            newSubsClientTypeMap.put(KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.name(), KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value());
            newSubsClientTypeMap.put(KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.name(), KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value());
            newSubsClientTypeMap.put(KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.name(), KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value());
            if (!oldSubsClientTypeMap.containsValue(oldSubsClientType) && !newSubsClientTypeMap.containsValue(newSubsClientType)) {
                if ((hierarchyValue.equals(HIERARCHY_TYPE.HIERARCHY) && null != firstNetIndicator) && subsFS2BiSet.get(USER_PROFILE_MGMT_BIT)) {
                    throw new KnProvBOException(KnErrorCodes.BOEntity.SWITCH_NOT_ALLOWED, "switchConvergedClient is not allowed when FNRR enabled/ UPM feature bit is enabled");
                }
            }
            if (!enabledPttRadio && subsFS2BiSet.get(MCPTT_COMPLAIANCE_BIT)) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.SWITCH_NOT_ALLOWED,
                        "switchConvergedClient is not allowed when pttRadio is disabled and MCPTT User feature bit is enabled");
            }

            if (oldSubsClientType != newSubsClientType) {
                if (oldSubsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.CROSS_CARRIER_PTT_CLIENT.value()) {
                    successPegs.add(KnOMConstants.XDM_NUM_CROSS_CARRIER_PTT_CLIENTS_DELETED);
                } else if (oldSubsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value()) {
                    successPegs.add(KnOMConstants.XDM_NUM_PTTRADIO_HANDSET_CLIENTS_DELETED);
                } else if (oldSubsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value()) {
                    successPegs.add(KnOMConstants.XDM_NUM_PTTRADIO_CROSSCARRIER_CLIENTS_DELETED);
                } else if (oldSubsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value()) {
                    successPegs.add(KnOMConstants.XDM_NUM_PTTRADIO_WIFIONLY_CLIENTS_DELETED);
                } else if (oldSubsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.SGMDNPATCH.value()) {
                    successPegs.add(KnOMConstants.XDM_NUM_SG_MDN_PATCH_CLIENTS_DELETED);
                }
                if (newSubsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.CROSS_CARRIER_PTT_CLIENT.value()) {
                    successPegs.add(KnOMConstants.XDM_NUM_CROSS_CARRIER_PTT_CLIENTS_CREATED);
                } else if (newSubsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value()) {
                    successPegs.add(KnOMConstants.XDM_NUM_PTTRADIO_HANDSET_CLIENTS_CREATED);
                } else if (newSubsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value()) {
                    successPegs.add(KnOMConstants.XDM_NUM_PTTRADIO_CROSSCARRIER_CLIENTS_CREATED);
                } else if (newSubsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value()) {
                    successPegs.add(KnOMConstants.XDM_NUM_PTTRADIO_WIFIONLY_CLIENTS_CREATED);
                } else if (newSubsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.SGMDNPATCH.value()) {
                    successPegs.add(KnOMConstants.XDM_NUM_SG_MDN_PATCH_CLIENTS_CREATED);
                }

                isClientTypeUpdated = true;
                knLogger.debug(methodName, "Client Type has been changed from [", oldSubsClientType, " ] to",
                        " [", newSubsClientType, "]");
            }

            int newDispatchGrpMember = existingSubsProfileDTO.getDispatchGroupMember();
            String newSubsFS2 = existingSubsProfileDTO.getSubsFS2();
            int corpType = existingSubsProfileDTO.getCorporateSubscriptionType();
            int publicType = existingSubsProfileDTO.getPublicSubscriptionType();
            String clientFS2 = existingSubsProfileDTO.getClientFS2();
            String extCorpId = existingSubsProfileDTO.getAccountId().trim();
            int clientPVMajorVersion = existingSubsProfileDTO.getClientPVmajorVer();
            String provFS2=existingSubsProfileDTO.getSubsFS2();
            String provFS2BitMask=existingSubsProfileDTO.getSubsFS2();
            String corpFS2 = null;
            KnOPCorpProfileInfoDTO corpProfileInfoDTO;
            if (corpType == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
                corpProfileInfoDTO = xdmServerDAO.retrieveCorporateProfile(extCorpId, persisterTxn);
                corpFS2 = corpProfileInfoDTO.getCorpFS2();
            }
            if (isClientTypeUpdated) {

				BitSet finalFSBitSet = new BitSet(Long.SIZE);
				String basePkgCode = paramNameValueMap.get(KnConstants.BASE_PKGCODE);
				String basePkgCodeFS = featureSetUtil.getDefSubsFeatureSetForBasePkg(publicType, corpType,
						newSubsClientType, basePkgCode, xdmPttServerId);
				knLogger.debug(methodName, "base pkg is applied - ", basePkgCodeFS);
				BitSet basePkgCodeBiSet = featureSetUtil.convertHexStringToBitSet(basePkgCodeFS);
				finalFSBitSet.or(basePkgCodeBiSet);
				String pkgCodeFS = featureSetUtil.getDefSubsFeatureSetForPkgCodes(publicType, corpType, newSubsClientType,
						exsitingPkgIds, xdmPttServerId);
				knLogger.debug(methodName, "addPkgIds is present with subscriberFS - ", pkgCodeFS);
				BitSet pkgCodeFSBiSet = featureSetUtil.convertHexStringToBitSet(pkgCodeFS);
				finalFSBitSet.or(pkgCodeFSBiSet);
				int oldIntropBitStatus=findInterOPBitStatus(existingSubsProfileDTO, exsitingPkgIds, persisterTxn);

				if (finalFSBitSet.get(LMR_BIT) && LMR_BIT_STATUS.MANUALLY_DISABLED.Value()==oldIntropBitStatus) {
					finalFSBitSet.clear(LMR_BIT);
				} else if (!finalFSBitSet.get(LMR_BIT) && LMR_BIT_STATUS.MANUALLY_ENABLED.Value()==oldIntropBitStatus) {
					finalFSBitSet.set(LMR_BIT);
				}
				newSubsFS2 = featureSetUtil.convertBitSetToHexString(finalFSBitSet);
                provFS2 = newSubsFS2;
                provFS2BitMask = newSubsFS2;
                knLogger.debug(methodName, "clientType or subsType changed.. got default SubsFS :", newSubsFS2);
            }

            String subsFS2 = featureSetUtil.generateSubsFeatureSet(newSubsFS2, provFS2, provFS2BitMask);
            String xdmsFs2=existingSubsProfileDTO.getXdmsFS2();
            //if dispatch group member flag changed then set/clear on demand location bit in subsFS
            if (newDispatchGrpMember == KnConstants.BIT_TRUE) {
            	xdmsFs2 = featureSetUtil.getSetFeatureSetBits(xdmsFs2, new int[]{com.kodiak.common.resources.KnConstants.FEATURE_SET.ONDEMLOCATION.value()});
                knLogger.debug(methodName, "on demand location bit set in xdmsFs:", xdmsFs2);
            } else {
            	xdmsFs2 = featureSetUtil.getClearFeatureSetBits(xdmsFs2, new int[]{com.kodiak.common.resources.KnConstants.FEATURE_SET.ONDEMLOCATION.value()});
                knLogger.debug(methodName, "on demand location bit clear in xdmsFs:", xdmsFs2);
            }

            if (newSubsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value() || newSubsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value() || newSubsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value()) {
            	clientFS2 = featureSetUtil.getSetFeatureSetBits(clientFS2, new int[]{com.kodiak.common.resources.KnConstants.FEATURE_SET.TLKGRPSELSERVER.value()});
            	clientFS2 = featureSetUtil.getClearFeatureSetBits(clientFS2, new int[]{com.kodiak.common.resources.KnConstants.FEATURE_SET.TLKGRPSELCLIENT.value()});
            	clientFS2 = featureSetUtil.getClearFeatureSetBits(clientFS2, new int[]{com.kodiak.common.resources.KnConstants.FEATURE_SET.TLKGRPSCANCLIENT.value()});
                knLogger.debug(methodName, "setting talk grp server bit since client bit is disable in clientFS & clearing talk grp server bit in clientFS fpr PTT Radio clients :",clientFS2);
            }
            String clientCapOverrideBitMask = featureSetUtil.getClientCapabilityBitMask(xdmPttServerId, clientPVMajorVersion);


            String opsFS2 = existingSubsProfileDTO.getOpsFS2();
            String activeFS2;
            String corpAdminFS2 = existingSubsProfileDTO.getCorpAdminFS2();
            String userProfileFS2 = existingSubsProfileDTO.getUserProfileFS2();
            if(userProfileFS2==null)
            {
            	userProfileFS2=featureSetUtil.getDefFinalUserProfileFS();
            }
            boolean cleanUpTGSData = false;
            if (corpType == KnConstants.CORP_SUBSCRIPTION_TYPE.NONE.value()) {
            	activeFS2 = featureSetUtil.generateActiveFeatBitSet(pocPttId, presencePttId, xdmsPttId,
            			clientFS2, subsFS2, opsFS2, clientCapOverrideBitMask,xdmsFs2,userProfileFS2);
            } else {
            	activeFS2 = featureSetUtil.generateActiveFeatBitSet(pocPttId, presencePttId, xdmsPttId,
                        clientFS2, subsFS2, corpFS2, opsFS2, corpAdminFS2, clientCapOverrideBitMask,xdmsFs2,userProfileFS2);
            }

            if (!activeFS2.equals(existingSubsProfileDTO.getActiveFS2())) {
                isActiveFSUpdated = true;
                cleanUpTGSData = cleanUpTGSData(activeFS2, existingSubsProfileDTO.getActiveFS2());
            }
            

            long lastProfileUpdateTime = Calendar.getInstance().getTimeInMillis();
            String clientPassword = null;
            subsProfilePersistDTO = new KnSubsProfilePersistDTO();
            if (clientPVMajorVersion > KnConstants.PROTOCOL_VERSION_9_X) {
                clientPassword = existingSubsProfileDTO.getClientPassword();
                subsProfilePersistDTO.setClientPassword(clientPassword);
            } else {
                subsProfilePersistDTO.setClientPassword(clientPassword);
                clientPassword = KnConstants.JUNK_PASSWORD;
            }
            subsProfilePersistDTO.setMdn(subsProvInputDTO.getMdn());
            subsProfilePersistDTO.setLastProfileUpdateTime(lastProfileUpdateTime);
            subsProfilePersistDTO.setSubsClientType(newSubsClientType);
            subsProfilePersistDTO.setSubsFS2(subsFS2);;
            subsProfilePersistDTO.setActiveFS2(activeFS2);
            subsProfilePersistDTO.setClientFS2(clientFS2);
            subsProfilePersistDTO.setXdmsFS2(xdmsFs2);
            
            Map<String, KnOPSubsProfileInfoDTO> mdnUpmFsMap=null;
            if (isClientTypeUpdated) {
                knLogger.debug(methodName, "Updated Subscriber Profile");
                xdmServerDAO.updateConvergedClientProfile(subsProfilePersistDTO, persisterTxn);
                knLogger.debug(methodName, "updated the corp profile etag for corp id - ", existingSubsProfileDTO.getCorpId());
                xdmServerDAO.updateCorpProfileLastUpdateTime(existingSubsProfileDTO.getCorpId(), lastProfileUpdateTime, persisterTxn);
                
              //updating fileds for profileMdn.
                mdnUpmFsMap = xdmServerDAO.getProfileMdnNupmfsByBaseMdn(mdn, persisterTxn);
                mdnUpmFsMap.remove(mdn);
                Map<String, String> profileMdnActiveFsMap=null;
    			if (mdnUpmFsMap.size() > 0) {
    				profileMdnActiveFsMap = provInfoUtil.calculateActiveFS2WithUPMFS(
    						existingSubsProfileDTO.getCorporateSubscriptionType(), pocPttId, presencePttId, xdmsPttId,
    						clientFS2, subsFS2, corpFS2, opsFS2, corpAdminFS2, clientCapOverrideBitMask, 
    						mdnUpmFsMap);
    				subsProfilePersistDTO.setNetworkName(existingSubsProfileDTO.getNetworkName());
    				subsProfilePersistDTO.setCorporateSubscriptionType(existingSubsProfileDTO.getCorporateSubscriptionType());
    				subsProfilePersistDTO.setPublicSubscriptionType(existingSubsProfileDTO.getPublicSubscriptionType());
    				subsProfilePersistDTO.setServiceAuthStatus(existingSubsProfileDTO.getServiceAuthStatus());
    				subsProfilePersistDTO.setIMEI(existingSubsProfileDTO.getIMEI());
    				subsProfilePersistDTO.setUserAgent(existingSubsProfileDTO.getUserAgent());
    				subsProfilePersistDTO.setVocoderId(existingSubsProfileDTO.getVocoderId());
    				subsProfilePersistDTO.setClientPVmajorVer(existingSubsProfileDTO.getClientPVmajorVer());
    				subsProfilePersistDTO.setClientPVminorVer(existingSubsProfileDTO.getClientPVminorVer());
    				subsProfilePersistDTO.setLastActivationTime(existingSubsProfileDTO.getLastActivationTime());
    				subsProfilePersistDTO.setSwType(existingSubsProfileDTO.getSwType());
    				subsProfilePersistDTO.setPlatformType(existingSubsProfileDTO.getPlatformType());
    				subsProfilePersistDTO.setDynamicQosFlag(existingSubsProfileDTO.getDynamicQosFlag());
                    if (existingSubsProfileDTO.getDerivedKey() != null) {
                        subsProfilePersistDTO.setDerivedKey(KnGeneralUtil.convertHexToAscii(existingSubsProfileDTO.getDerivedKey()));
                    }
    				subsProfilePersistDTO.setServiceStatusOp(existingSubsProfileDTO.getServiceStatusOp());
    				subsProfilePersistDTO.setOpsFS2(opsFS2);
    				subsProfilePersistDTO.setCorpAdminFS2(corpAdminFS2);
    				subsProfilePersistDTO.setLicenseType(existingSubsProfileDTO.getLicenseType());
    				xdmServerDAO.updateMdnFiledsNActiveFS(subsProfilePersistDTO, profileMdnActiveFsMap, persisterTxn);
    				responseDTO.setProfileMdnActivsFsMap(profileMdnActiveFsMap);
    			}
                
                int isAffiliationEnabled;
                boolean bitEnabled = KnGeneralUtil.getFeatureBitValue(subsProfilePersistDTO.getActiveFS2(), com.kodiak.common.resources.KnConstants.FEATURE_SET.AFFILIATIONFEATURE.value());
    			if (subsProfilePersistDTO.getClientPVmajorVer() >= com.kodiak.xdms.server.common.resources.KnConstants.PROTOCOL_VERSION_18
    					&& bitEnabled && checkClientType(subsProfilePersistDTO.getSubsClientType(),existingSubsProfileDTO.getMcpttCompliance())) 
    			{
                    isAffiliationEnabled = ENABLED;
                } else {
                    isAffiliationEnabled = DISABLED;
                }

    			IXDMServerDAO xdmServerDao = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
    			xdmServerDao.updateAffForCorpGrpMemList(subsProfilePersistDTO.getMdn(),isAffiliationEnabled,persisterTxn);
                
            }
            // --> update XDM directory.
            IXDMServerDAO commonXDMServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
            int previousEtag = commonXDMServerDAO.getCurrentEtagForDirDoc(mdn, persisterTxn);
            commonXDMServerDAO.updateEtagForDirDoc(mdn, persisterTxn);
            String xcapRootUri = genInfoUtil.getXCAPRootURI(mdn, persisterTxn);
            responseDTO.setSubsClientType(oldSubsClientType);
            responseDTO.setNewSubsClientType(newSubsClientType);
            responseDTO.setCorpId(subsProfilePersistDTO.getCorpId());
            responseDTO.setActiveFSChanged(isActiveFSUpdated);
            responseDTO.setClientTypeChanged(isClientTypeUpdated);
            responseDTO.setClientPvMajorVersion(existingSubsProfileDTO.getClientPVmajorVer());
            responseDTO.setClientPvMinorVersion(existingSubsProfileDTO.getClientPVminorVer());
            responseDTO.setActiveFS2(activeFS2);
            responseDTO.setOldActiveFS2(existingSubsProfileDTO.getActiveFS2());
            responseDTO.setServiceAuthStatus(existingSubsProfileDTO.getServiceAuthStatus());
            responseDTO.setClientPassword(clientPassword);
            responseDTO.setCleanUpTGSData(cleanUpTGSData);
            responseDTO.setPocPttId(pocPttId);
            responseDTO.setPresPttId(presencePttId);
            responseDTO.setLastProfileUpdateTime(lastProfileUpdateTime);
            responseDTO.setMdnUpmFsMap(mdnUpmFsMap);
            if (isClientTypeUpdated) {
                responseDTO.setResponseMessage(KnProvConstants.UPDATE_SUBS_PROFILE_SUCCESS);
                responseDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.SUCCESS.value());
                responseDTO.setEtag(lastProfileUpdateTime);
                //populating the Subs Config document change DTO
                KnOPDocChgDTO docChgDTO = new KnOPDocChgDTO();
                docChgDTO.setDocumentChgType(KnConstants.DOC_CHANGE_TYPE.REPLACE.value());
                String subsConfigDocUri = provInfoUtil.generateSubsConfigSelUri(mdn);
                docChgDTO.setDocUri(subsConfigDocUri);
                docChgDTO.setNewEtag(String.valueOf(lastProfileUpdateTime));
                Collection<KnOPDocChgDTO> docChgList = new ArrayList<KnOPDocChgDTO>();
                docChgList.add(docChgDTO);
                //populating Dir Document change DTO
                KnOPDirChgDTO dirChgDTO = new KnOPDirChgDTO();
                dirChgDTO.setXcapRootURI(xcapRootUri);
                dirChgDTO.setPocHome(pocPttId);
                dirChgDTO.setPresenceHome(presencePttId);
                dirChgDTO.setDocChgDTO(docChgList);
                String dirDocUri = genInfoUtil.generateDirDocUri(mdn);
                dirChgDTO.setDirUri(dirDocUri);
                dirChgDTO.setDirPrevEtag(String.valueOf(previousEtag));
                int newEtag = previousEtag + 1;
                dirChgDTO.setDirNewEtag(String.valueOf(newEtag));
                dirChgDTO.setNtfyOnAnyMDN(NOTIFY_ON_ANY_MDN.BASE_MDN.value());
                responseDTO.setDirChgDTO(dirChgDTO);
            }
            
            //updating LASTPROFILEUPDATETIME in POCSUBSCRINFO profile mdn notiy
            if(mdnUpmFsMap!=null&&mdnUpmFsMap.size()>0)
            {
 			IProvXDMServerDAO xdmServerDAONew = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB)
 					.createProvXDMServerDAO();
 			ArrayList<String> userProfileMdns= new ArrayList<String>(mdnUpmFsMap.keySet());
 			Map<String, Collection<KnDocChangeListDTO>> profileMdnEtagMap = xdmServerDAONew
 					.profileMdnEtagUpdate(userProfileMdns, persisterTxn);
 			responseDTO.setProfileMdnEtagMap(profileMdnEtagMap);
 			if (profileMdnEtagMap != null && !profileMdnEtagMap.isEmpty()) {
 				responseDTO.setMcsXcapRootUriMap(
 						genInfoUtil.getXCAPRootURI(new ArrayList<>(profileMdnEtagMap.keySet()), persisterTxn, true));
 			}
            }
            knLogger.debug(methodName, "responseDTO from Controller :", responseDTO.getServiceAuthStatus());
            //updating the success pegs
            responseDTO.setSuccessPegs(successPegs);
            knLogger.debug(methodName,"-[ responseDTO ]-",responseDTO ,"[ subsProvInputDTO  ]",subsProvInputDTO);
            knLogger.info(methodName, "EXIT: Update Subscriber operation ");
            return responseDTO;
        } catch (KnFeatureSetException ex) {
            knLogger.error(methodName, "Feature Set Exception occured :", ex);
            throw new KnProvBOException(ex.getErrorCode(), ex.getMessage(), ex);
        } catch (KnDBConnectionException ex) {
            knLogger.error(methodName, "DAO DBConnection Exception occured :", ex);
            throw new KnProvBOException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);
        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception occurred :", e);
            knLogger.error(methodName, e);
            throw new KnProvBOException(e.getErrorCode(), e.getMessage(), e);
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while Update Subscriber");
            knLogger.error(methodName, e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred while update Subscriber", e);
        }
    }



    /**
     * method to retrieve the Subscriber Details
     *
     * @param mdnList KnIPSubscriberInfoDTO
     * @param persisterTxn  KnPersisterTxn
     * @return KnOPSubsProfileInfoDTO Object
     * @throws KnProvBOException
     */
    public Map<String,String> fetchActiveFSForBulkMdns( List<String>mdnList, KnPersisterTxn persisterTxn) throws KnProvBOException {
        String methodName = "getSubscriberDetails(List<String>, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY: getSubscriberDetails for MDN list ", KnGDPRTemplate.mdnList(mdnList));
        Map<String,String> activeFsMap= new HashMap<String,String>();

        try {
                activeFsMap = provInfoUtil.fetchActiveFSForBulkMdns(mdnList, persisterTxn);
            } catch (KnProvBOException e) {
                knLogger.error(methodName, "BO Exception occurred :" + e.getErrorCode());
                throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while get Subscriber details" + e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred while get Subscriber details", e);
        }
        knLogger.info(methodName, "EXIT:  activeFsMap - ", activeFsMap);
        return activeFsMap;
    }

    /**
     * This method will return system level configuration
     * This method will return the following information based on the country code in request
     * ->Return roaming type
     * ->Return client type
     * ->Subscription type
     * @param subsProvInputDTO KnIPSubsProfileInputDTO
     * @return KnProvRespDTO Object
     */

    public KnSysConfigRespDTO getSysConfig(KnIPSubsProvInfoDTO subsProvInputDTO, KnPersisterTxn persisterTxn) throws KnProvBOException, KnValidationException {

        String methodName = "getSysConfig(KnIPSubscriberInfoDTO, KnPersisterTxn)";
        boolean ownedTxn = false;
        knLogger.info(methodName, "ENTRY: getSysConfig with DTO ",subsProvInputDTO);
        KnSysConfigRespDTO sysConfigRespDTO = null;
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            try {
                xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
            } catch (KnBOException e) {
                knLogger.error(methodName, "failed to retrieve xdm Ptt Sever Id");
                knLogger.error(methodName, e);
                if (ownedTxn) {
                    rollback(persisterTxn);
                }
                throw new KnProvBOException(KnErrorCodes.BOEntity.XDMS_PTT_ID_NOT_FOUND, "Failed to retrieve XDM PTT Server ID", e);
            }

            sysConfigRespDTO = new KnSysConfigRespDTO();
            List<String> roamingClusterIdList = new ArrayList<>();

            Map<Integer,String> supportedRoamingClusterInfo = provInfoUtil.retrieveSupportedRoamingList();

            for (int roamingID : supportedRoamingClusterInfo.keySet()) {
                roamingClusterIdList.add(supportedRoamingClusterInfo.get(roamingID));
            }
            knLogger.debug(methodName, "Roaming type configured are -", roamingClusterIdList);
            sysConfigRespDTO.setRoamingType(roamingClusterIdList);

            int clusterId = Integer.parseInt(System.getenv(CLUSTERID_ENV_NAME));

            // Fetch the microservices common config for deriving the xcap mobile sync flag
            Map<String, String> msCommonConfigMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);

            sysConfigRespDTO.setIdmServiceFlag(msCommonConfigMap.get(com.kodiak.common.resources.KnConstants.IDM_SERVICE_FLAG));

        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occurred : " + ex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            if (com.kodiak.xdms.server.common.resources.KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_INFO_NOT_FOUND, "Subscriber doesn't exists");
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception occurred :" + e.getErrorCode());
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while get Subscriber details" + e);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred while get Subscriber details", e);
        }
        knLogger.info(methodName, "EXIT:  getSysConfig - ");
        return sysConfigRespDTO;
    }


    /**
     * This  method validate if the request feature bit is allowd
     * feature bit configured in
     * KnConstants
     * @param inputMap
     * @return
     */
 private static boolean isProvFSBitAllowed(Map<Integer, Integer> inputMap){
        String methodName="isProvFSBitAllowed(Map<String, String>";
        knLogger.debug("Entered ", methodName, " inPutMap :", inputMap);
        boolean isProvFSBitValid= true;

        List<com.kodiak.common.resources.KnConstants.PROV_FS_BIT> declaredProvFSList = Arrays.asList(com.kodiak.common.resources.KnConstants.PROV_FS_BIT.values());
        if(inputMap != null) {
        for(Map.Entry<Integer,Integer> reqProvEntry:inputMap.entrySet()){
            for(com.kodiak.common.resources.KnConstants.PROV_FS_BIT declaredProvFS: declaredProvFSList)
            {
                if(!reqProvEntry.getKey().equals(declaredProvFS.value())){
                    isProvFSBitValid= false;
                    break;
                }
            }
        }
        }
        knLogger.debug( methodName, " If correct isProvFSBitValid should be true :", isProvFSBitValid);
        return isProvFSBitValid;
    }


    private void setMCPTTFeatureDisable(String mdn, String activeFS1, KnPersisterTxn persisterTxn, IProvXDMServerDAO xdmServerDAO)throws KnDAOException{

        String methodName = "setMCPTTFeatureDisable(mdn activefs persisterTxn)";
        knLogger.debug(methodName, "activeFS1 for MCPTT long value -",activeFS1);
        boolean ambientListenerBit = featureSetUtil.getFeatureBitValue(activeFS1, FEATURE_SET.AMBIENTLISTENING.value());
        boolean discreetListenerBit = featureSetUtil.getFeatureBitValue(activeFS1, FEATURE_SET.DISCRETELISTENING.value());
        boolean userCheckBit = featureSetUtil.getFeatureBitValue(activeFS1, FEATURE_SET.USERCHECK.value());
        boolean userEnableDisableBit = featureSetUtil.getFeatureBitValue(activeFS1, FEATURE_SET.USERENABLEDISABLE.value());

        List<KnMCPTTPermInfoDTO> mcpttPermInfoDTOS = xdmServerDAO.getMCPTTPermInfo(mdn,persisterTxn);
        for(KnMCPTTPermInfoDTO permInfoDTO : mcpttPermInfoDTOS){
            if(permInfoDTO.getPermBitset().equals(0L)){
                knLogger.debug(methodName,"All MCPTT bits are disable deleting entry for mdn -",KnGDPRTemplate.mdn(mdn) ,"and for target- ",KnGDPRTemplate.mdn(permInfoDTO.getTargetMdn()));
                xdmServerDAO.deleteTargetEntry(mdn, permInfoDTO.getTargetMdn(), persisterTxn);
            }
        }

        /*knLogger.debug(methodName,"MCPTT Bit values : ambient listener -",ambientListenerBit," discreet listener -",discreetListenerBit," user check bit -",userCheckBit," user anable disable -",userEnableDisableBit);
        if (!ambientListenerBit && !discreetListenerBit && !userCheckBit && !userEnableDisableBit){
            knLogger.debug(methodName,"All MCPTT bits are disable deleting entry for mdn -",mdn);
            xdmServerDAO.deleteAuthorizationDocProfile(mdn,persisterTxn);
            knLogger.debug(methodName,"MDN deleted from  AUTHORIZATION_DOC");
            xdmServerDAO.deleteMCPTTProfile(mdn,persisterTxn);
            knLogger.debug(methodName,"MDN deleted from  MCPTT_PROFILE");

        }*/
        BitSet permBit = new BitSet();
        permBit.set(0,ambientListenerBit);
        permBit.set(1,discreetListenerBit);
        permBit.set(2,userCheckBit);
        permBit.set(3,userEnableDisableBit);
        Long permBitSet = 0l;
        /*if (!ambientListenerBit){
            knLogger.debug(methodName,"Ambient listener bit is disable for mdn -",mdn," hence setting perm bit false in MCPTT profile");
            permBit.set(0,false);
            permBitSet = featureSetUtil.convertBitSetToLong(permBit);
            xdmServerDAO.updatePermBit(mdn,permBitSet,persisterTxn);
            knLogger.debug(methodName,"Bit updated successfully");
        }*/

        if (!discreetListenerBit){
            knLogger.debug(methodName,"Discreet listener bit is disabled hence disabling discreet_enable in DG.MCPTT_PROFILE for mdn -",KnGDPRTemplate.mdn(mdn));
            xdmServerDAO.disableDiscreetEnabled(mdn,persisterTxn);
            knLogger.debug(methodName,"Disabled Success");
            long lastProfileUpdateTime = Calendar.getInstance().getTimeInMillis();
            xdmServerDAO.updateAuthorizationDocEtag(mdn,lastProfileUpdateTime,persisterTxn);
            knLogger.debug(methodName,"Auth doc Etag updated for mdn");
            /*knLogger.debug(methodName,"Ambient listener bit is disable for mdn -",mdn," hence setting perm bit false in MCPTT profile");
            permBit.set(1,false);
            permBitSet = featureSetUtil.convertBitSetToLong(permBit);
            xdmServerDAO.updatePermBit(mdn,permBitSet,persisterTxn);
            knLogger.debug(methodName,"Bit updated successfully");*/
        }

        /*if (!userCheckBit){

            knLogger.debug(methodName,"Ambient listener bit is disable for mdn -",mdn," hence setting perm bit false in MCPTT profile");
            permBit.set(2,false);
            permBitSet = featureSetUtil.convertBitSetToLong(permBit);
            xdmServerDAO.updatePermBit(mdn,permBitSet,persisterTxn);
            knLogger.debug(methodName,"Bit updated successfully");
        }

        if (!userEnableDisableBit){

            knLogger.debug(methodName,"Ambient listener bit is disable for mdn -",mdn," hence setting perm bit false in MCPTT profile");
            permBit.set(3,false);
            permBitSet = featureSetUtil.convertBitSetToLong(permBit);
            xdmServerDAO.updatePermBit(mdn,permBitSet,persisterTxn);
            knLogger.debug(methodName,"Bit updated successfully");
        }*/
    }

    private int findInterOPBitStatus(KnOPSubsProfileInfoDTO existingSubsProfileDTO,	Map<String, Integer> existingPkgMap, KnPersisterTxn persisterTxn)
			throws KnFeatureSetException, KnBOException {
		String methodName = "findInterOPBitStatus(KnOPSubsProfileInfoDTO , long ,Map<String,Integer> )";
		knLogger.debug(methodName, "Entry ", existingSubsProfileDTO, "existing map ", existingPkgMap,
				"existing subscriberfs ", existingSubsProfileDTO.getSubsFS2());
		BitSet existingFSBitSet = featureSetUtil.convertHexStringToBitSet(existingSubsProfileDTO.getSubsFS2());
		knLogger.debug(methodName, "existing subsFsBitSet - ", existingFSBitSet.toString());

		Map<String, String> paramNameValueMap = genInfoUtil.retrieveRTXConfigValues(persisterTxn);
		String basePkgCode = paramNameValueMap.get(KnConstants.BASE_PKGCODE);
		String basePkgCodeFS = featureSetUtil.getDefSubsFeatureSetForBasePkg(
				existingSubsProfileDTO.getPublicSubscriptionType(),
				existingSubsProfileDTO.getCorporateSubscriptionType(), existingSubsProfileDTO.getSubsClientType(),
				basePkgCode, xdmPttServerId);
		BitSet finalFSBitSet = new BitSet(Long.SIZE);
		BitSet basePkgCodeBiSet = featureSetUtil.convertHexStringToBitSet(basePkgCodeFS);
		finalFSBitSet.or(basePkgCodeBiSet);
		knLogger.info(methodName, "base pkg bitset - ", finalFSBitSet.toString());
		if (!existingPkgMap.isEmpty()) {
			String pkgCodeFS = featureSetUtil.getDefSubsFeatureSetForPkgCodes(
					existingSubsProfileDTO.getPublicSubscriptionType(),
					existingSubsProfileDTO.getCorporateSubscriptionType(), existingSubsProfileDTO.getSubsClientType(),
					existingPkgMap, xdmPttServerId);
			BitSet pkgCodeBiSet = featureSetUtil.convertHexStringToBitSet(pkgCodeFS);
			knLogger.debug(methodName, "PkgIds bitset - ", pkgCodeBiSet);
			finalFSBitSet.or(pkgCodeBiSet);
		}
		knLogger.debug(methodName, "final PkgIds bitset  - ", finalFSBitSet.toString());

		int status = LMR_BIT_STATUS.NO_CHANGE.Value();
		if (existingFSBitSet.get(LMR_BIT) && !finalFSBitSet.get(LMR_BIT)) {
			status = LMR_BIT_STATUS.MANUALLY_ENABLED.Value();
		} else if (!existingFSBitSet.get(LMR_BIT) && finalFSBitSet.get(LMR_BIT)) {
			status = LMR_BIT_STATUS.MANUALLY_DISABLED.Value();
		}

		knLogger.info(methodName, "LMR bit status :", status);
		return status;
	}

    private String generateSubsFS2(KnOPSubsProfileInfoDTO existingSubsProfileDTO, KnPersisterTxn persisterTxn)
			throws  KnBOException, KnFeatureSetException,  KnDAOException, KnProvBOException {
		String methodName = "generateSubsFS2(KnOPSubsProfileInfoDTO,KnPersisterTxn)";
		IProvXDMServerDAO xdmServerDAO = KnProvFactorySelector.getProvDAOFactory(KnFactorySelector.DB)
				.createProvXDMServerDAO();
		// retrieve tier pkg & addon pkg
		Map<String, Integer> exsitingPkgIds = new HashMap<String, Integer>();
		String existingTierPkg = null;
		KnOPSubsAddlInfoProfileDTO allProfileDto = xdmServerDAO.retrieveSubscrAddlInfo(existingSubsProfileDTO.getMdn(),
				persisterTxn);
		if (allProfileDto != null)
			existingTierPkg = allProfileDto.getTierPkgCode();
		if (existingTierPkg != null)
			exsitingPkgIds.put(existingTierPkg, KnConstants.TIER_PKG_TYPE);
		List<String> existingAddonPkgs = xdmServerDAO.selectSubAddOnPkgs(existingSubsProfileDTO.getMdn(), persisterTxn);
		if (existingAddonPkgs != null) {
			for (String pkgCode : existingAddonPkgs) {
				exsitingPkgIds.put(pkgCode, KnConstants.ADDON_PKG_TYPE);
			}
		}
		knLogger.info(methodName, "PkgIdsMap ",
				existingSubsProfileDTO.getPkgIdMap() + " exsitingPkgIds " + exsitingPkgIds);

		BitSet finalFSBitSet = new BitSet(Long.SIZE);
		Map<String, String> paramNameValueMap = genInfoUtil.retrieveRTXConfigValues(persisterTxn);
		String basePkgCode = paramNameValueMap.get(KnConstants.BASE_PKGCODE);
		String basePkgDefFS = featureSetUtil.getDefSubsFeatureSetForBasePkg(
				existingSubsProfileDTO.getPublicSubscriptionType(),
				existingSubsProfileDTO.getCorporateSubscriptionType(), existingSubsProfileDTO.getSubsClientType(),
				basePkgCode, xdmPttServerId);
		knLogger.debug(methodName, "base pkg is applied - ", basePkgDefFS);
		BitSet basePkgDefFSBitSet = featureSetUtil.convertHexStringToBitSet(basePkgDefFS);
		finalFSBitSet.or(basePkgDefFSBitSet);
		if (!exsitingPkgIds.isEmpty()) {
			String pkgCodesFS = featureSetUtil.getDefSubsFeatureSetForPkgCodes(
					existingSubsProfileDTO.getPublicSubscriptionType(),
					existingSubsProfileDTO.getCorporateSubscriptionType(), existingSubsProfileDTO.getSubsClientType(),
					exsitingPkgIds, xdmPttServerId);
			knLogger.debug(methodName, "addPkgIds is present with subscriberFS - ", pkgCodesFS);
			BitSet pkgCodes_BiSet = featureSetUtil.convertHexStringToBitSet(pkgCodesFS);
			finalFSBitSet.or(pkgCodes_BiSet);
		}
		int oldIntropBitStatus=findInterOPBitStatus(existingSubsProfileDTO, exsitingPkgIds, persisterTxn);

		if (finalFSBitSet.get(LMR_BIT) && LMR_BIT_STATUS.MANUALLY_DISABLED.Value()==oldIntropBitStatus) {
			finalFSBitSet.clear(LMR_BIT);
		} else if (!finalFSBitSet.get(LMR_BIT) && LMR_BIT_STATUS.MANUALLY_ENABLED.Value()==oldIntropBitStatus) {
			finalFSBitSet.set(LMR_BIT);
		}
		String subsFeatureSet2 = featureSetUtil.convertBitSetToHexString(finalFSBitSet);
		String provFS2 = subsFeatureSet2;
		String provFS2BitMask = subsFeatureSet2;

		String subsFS2 = featureSetUtil.generateSubsFeatureSet(subsFeatureSet2, provFS2, provFS2BitMask);
		knLogger.info(methodName, "final SubsFS - ", subsFS2);
		return subsFS2;
	}

    /**
     *
     * @param mdn
     * @param newActiveFS
     * @param oldActiveFS
     * @param persisterTxn
     * @return
     * @throws KnProvBOException
     *
     *

  -    get old and new simulSessNewActFS from activeFS
    if both are not matching then
    get the current simulSessNewActFS from new activeFS then
    if (simulSessNewActFS enabled) then
    createTGSSDoc(mdn,lastProfileUpdateTime, persisterTxn);
    else
    delete deleteTGSSProfile(mdn, persisterTxn)

    getCurrentEtagsForDirDoc
    updateEtagForDirDoc
    construct dirChg DTO with Remove or replace based on TGSS doc created or deleted.
     */

    @Override
    public KnOPProvDTO actionOnTGSSDoc(String mdn, String newActiveFS, String oldActiveFS, KnPersisterTxn persisterTxn) throws KnProvBOException {

        String methodName = "actionOnTGSSDoc(String,long,long,KnPersisterTxn)";

        knLogger.info(methodName, "Entering into method with mdn: " + KnGDPRTemplate.mdn(mdn));

        boolean mssEnableBitNew = Boolean.FALSE;
        boolean mssEnableBitOld = Boolean.FALSE;

        KnOPProvDTO responseDTO = new KnOPProvDTO();
        KnOPDirChgDTO dirChgDTO = new KnOPDirChgDTO();

        long lastProfileUpdateTime = Calendar.getInstance().getTimeInMillis();

        mssEnableBitNew = featureSetUtil.getFeatureBitValue(newActiveFS, FEATURE_SET.MULTISIMULTANEOUSSESSION.value());
        mssEnableBitOld = featureSetUtil.getFeatureBitValue(oldActiveFS, FEATURE_SET.MULTISIMULTANEOUSSESSION.value());

        if (mssEnableBitNew != mssEnableBitOld) {

            long etag = provInfoUtil.getTGSSDocEtag(mdn,persisterTxn );
            long newEtag = etag+1;
            boolean creDelFlag = false;
            knLogger.info(methodName, "Simultainous Session feature bit is Changed for OldActiveFs and new ActiveFs : " + mssEnableBitNew);
            if (mssEnableBitNew && newEtag == 0) {
                knLogger.info(methodName, "Simultainous Session feature bit is enabled create Talk Group Simultanous Session doc: ");
                provInfoUtil.createTGSSDoc(mdn, newEtag, persisterTxn);
                creDelFlag = true;
            } else if(!mssEnableBitNew) {
                knLogger.info(methodName, "Simultainous Session feature bit is disabled delete Talk Group Simultanous Session doc: ");
                provInfoUtil.deleteTGSSProfile(mdn, persisterTxn);
                creDelFlag = true;
            }
            if (creDelFlag) {
                IXDMServerDAO commonXDMServerDAO = null;
                //retrieve the previous etag of dir doc
                try {
                    commonXDMServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
                    int previousDirDocEtag = commonXDMServerDAO.getCurrentEtagForDirDoc(mdn, persisterTxn);
                    commonXDMServerDAO.updateEtagForDirDoc(mdn, persisterTxn);
                    knLogger.debug(methodName, "Successfully updated the xdm directory");

                    List<KnOPDirChgDTO> dirChgDTOs = new ArrayList<KnOPDirChgDTO>();
                    KnOPDocChgDTO docChgDTO = new KnOPDocChgDTO();

                    KnOPSubsProfileInfoDTO subsProfileInfoDTO = null;
                    int i = 0;

                    //retrieve the Service Auth Status of the MDN or Subscriber
                    subsProfileInfoDTO = provInfoUtil.retrieveSubscriberInfo(mdn, persisterTxn);

                    docChgDTO = new KnOPDocChgDTO();
                    if (mssEnableBitNew == Boolean.TRUE) {
                        docChgDTO.setDocumentChgType(KnConstants.DOC_CHANGE_TYPE.REPLACE.value());
                    } else {
                        docChgDTO.setDocumentChgType(KnConstants.DOC_CHANGE_TYPE.REMOVE.value());
                    }
                    String tgssListSelUri = provInfoUtil.generateTGSSListSelUri(mdn, mssEnableBitNew);
                    docChgDTO.setDocUri(tgssListSelUri);
                    Collection<KnOPDocChgDTO> chgDocList = new ArrayList<KnOPDocChgDTO>();
                    chgDocList.add(docChgDTO);

                    //populating the XDM Directory DTO
                    dirChgDTO.setXcapRootURI(genInfoUtil.getXCAPRootURI(mdn, persisterTxn));
                    dirChgDTO.setPocHome(subsProfileInfoDTO.getPoCHome());
                    dirChgDTO.setPresenceHome(subsProfileInfoDTO.getPresenceHome());
                    dirChgDTO.setDocChgDTO(chgDocList);
                    String dirDocUri = genInfoUtil.generateDirDocUri(mdn);
                    dirChgDTO.setDirUri(dirDocUri);
                    dirChgDTO.setDirPrevEtag(String.valueOf(previousDirDocEtag));
                    int newDirDocEtag = previousDirDocEtag + 1;
                    dirChgDTO.setDirNewEtag(String.valueOf(newDirDocEtag));
                    if (mssEnableBitNew == Boolean.TRUE) {
                        docChgDTO.setNewEtag(String.valueOf(newEtag));
                    }
                    dirChgDTO.setProtoVersion(subsProfileInfoDTO.getClientPVmajorVer() + "." + subsProfileInfoDTO.getClientPVminorVer());
                    dirChgDTO.setClientType(subsProfileInfoDTO.getSubsClientType());
                } catch (KnDBConnectionException ex) {
                    knLogger.error(methodName, "DAO DBConnection Exception occured :", ex);
                    throw new KnProvBOException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);

                } catch (KnDAOException ex) {
                    knLogger.error(methodName, "DAO Exception occurred : ", ex);
                    knLogger.error(methodName, ex);
                    if (com.kodiak.xdms.server.common.resources.KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                        throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_INFO_NOT_FOUND, "Subscriber info not found");
                    }
                    throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
                } catch (KnProvBOException e) {
                    knLogger.error(methodName, "BO Exception occurred :", e);
                    knLogger.error(methodName, e);
                    throw e;
                } catch (Exception e) {
                    knLogger.error(methodName, "Exception occurred while actioning on Talk Group Simultainous Session Subscriber");
                    knLogger.error(methodName, e);
                    throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred while actioning on Talk Group Simultainous Session Subscriber", e);
                }
                responseDTO.setDirChgDTO(dirChgDTO);
            }
        }
        knLogger.debug(methodName, "responseDTO: " + responseDTO);
        knLogger.info(methodName, "Exit into method with mdn: " +KnGDPRTemplate.mdn(mdn));
        return responseDTO;
    }

    @Override
    public KnOPProvDTO actionOnTGSSDocCust(String mdn, String newActiveFS, String oldActiveFS, KnPersisterTxn persisterTxn) throws KnProvBOException {

        String methodName = "actionOnTGSSDocCust(String,long,long,KnPersisterTxn)";

        knLogger.info(methodName, "Entering into method with mdn: " + KnGDPRTemplate.mdn(mdn));

        boolean mssEnableBitNew = Boolean.FALSE;
        boolean mssEnableBitOld = Boolean.FALSE;

        KnOPProvDTO responseDTO = new KnOPProvDTO();
        KnOPDirChgDTO dirChgDTO = new KnOPDirChgDTO();

        long lastProfileUpdateTime = Calendar.getInstance().getTimeInMillis();

        mssEnableBitNew = featureSetUtil.getFeatureBitValue(newActiveFS, FEATURE_SET.MULTISIMULTANEOUSSESSION.value());
        mssEnableBitOld = featureSetUtil.getFeatureBitValue(oldActiveFS, FEATURE_SET.MULTISIMULTANEOUSSESSION.value());
        knLogger.info(methodName, "mssEnableBitNew: " + mssEnableBitNew, "mssEnableBitOld: "+ mssEnableBitOld);

        if (mssEnableBitNew != mssEnableBitOld || !mssEnableBitOld) {

            long etag = provInfoUtil.getTGSSDocEtag(mdn,persisterTxn );
            long newEtag = etag+1;
            boolean creDelFlag = false;
            knLogger.info(methodName, "newEtag: " + newEtag, "oldActiveFS: "+ oldActiveFS);
            knLogger.info(methodName, "Simultainous Session feature bit is Changed for OldActiveFs and new ActiveFs : " + mssEnableBitNew);
            if (mssEnableBitNew && newEtag == 0) {
                knLogger.info(methodName, "Simultainous Session feature bit is enabled create Talk Group Simultanous Session doc: ");
                provInfoUtil.createTGSSDoc(mdn, (newEtag+1), persisterTxn);
                creDelFlag = true;
            } else if (!mssEnableBitNew) {
                knLogger.info(methodName, "Simultainous Session feature bit is disabled delete Talk Group Simultanous Session doc: ");
                provInfoUtil.deleteTGSSProfile(mdn, persisterTxn);
                creDelFlag = true;
            }
            if (creDelFlag) {
                IXDMServerDAO commonXDMServerDAO = null;
                //retrieve the previous etag of dir doc
                try {
                    xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
                    knLogger.debug(methodName, "xdmPttServerId::" + xdmPttServerId);
                    commonXDMServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
                    int previousDirDocEtag = commonXDMServerDAO.getCurrentEtagForDirDoc(mdn, persisterTxn);
                    commonXDMServerDAO.updateEtagForDirDoc(mdn, persisterTxn);
                    knLogger.debug(methodName, "Successfully updated the xdm directory");

                    List<KnOPDirChgDTO> dirChgDTOs = new ArrayList<KnOPDirChgDTO>();
                    KnOPDocChgDTO docChgDTO = new KnOPDocChgDTO();

                    KnOPSubsProfileInfoDTO subsProfileInfoDTO = null;
                    int i = 0;

                    //retrieve the Service Auth Status of the MDN or Subscriber
                    subsProfileInfoDTO = provInfoUtil.retrieveSubscriberInfo(mdn, persisterTxn);

                    docChgDTO = new KnOPDocChgDTO();
                    if (mssEnableBitNew == Boolean.TRUE) {
                        docChgDTO.setDocumentChgType(KnConstants.DOC_CHANGE_TYPE.REPLACE.value());
                    } else {
                        docChgDTO.setDocumentChgType(KnConstants.DOC_CHANGE_TYPE.REMOVE.value());
                    }
                    String tgssListSelUri = provInfoUtil.generateTGSSListSelUri(mdn, mssEnableBitNew);
                    docChgDTO.setDocUri(tgssListSelUri);
                    Collection<KnOPDocChgDTO> chgDocList = new ArrayList<KnOPDocChgDTO>();
                    chgDocList.add(docChgDTO);

                    //populating the XDM Directory DTO
                    dirChgDTO.setXcapRootURI(genInfoUtil.getXCAPRootURI(mdn, persisterTxn));
                    dirChgDTO.setPocHome(subsProfileInfoDTO.getPoCHome());
                    dirChgDTO.setPresenceHome(subsProfileInfoDTO.getPresenceHome());
                    dirChgDTO.setDocChgDTO(chgDocList);
                    String dirDocUri = genInfoUtil.generateDirDocUri(mdn);
                    dirChgDTO.setDirUri(dirDocUri);
                    dirChgDTO.setDirPrevEtag(String.valueOf(previousDirDocEtag));
                    if (mssEnableBitNew == Boolean.TRUE) {
                        int newDirDocEtag = previousDirDocEtag + 1;
                        dirChgDTO.setDirNewEtag(String.valueOf(newDirDocEtag));
                        docChgDTO.setNewEtag(String.valueOf(newEtag));
                    }
                    dirChgDTO.setProtoVersion(subsProfileInfoDTO.getClientPVmajorVer() + "." + subsProfileInfoDTO.getClientPVminorVer());
                    dirChgDTO.setClientType(subsProfileInfoDTO.getSubsClientType());
                } catch (KnDBConnectionException ex) {
                    knLogger.error(methodName, "DAO DBConnection Exception occured :", ex);
                    throw new KnProvBOException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);

                } catch (KnDAOException ex) {
                    knLogger.error(methodName, "DAO Exception occurred : ", ex);
                    knLogger.error(methodName, ex);
                    if (com.kodiak.xdms.server.common.resources.KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                        throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_INFO_NOT_FOUND, "Subscriber info not found");
                    }
                    throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
                } catch (KnProvBOException e) {
                    knLogger.error(methodName, "BO Exception occurred :", e);
                    knLogger.error(methodName, e);
                    throw e;
                } catch (Exception e) {
                    knLogger.error(methodName, "Exception occurred while actioning on Talk Group Simultainous Session Subscriber");
                    knLogger.error(methodName, e);
                    throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred while actioning on Talk Group Simultainous Session Subscriber", e);
                }
                responseDTO.setDirChgDTO(dirChgDTO);
            }
        }
        knLogger.debug(methodName, "responseDTO: " + responseDTO);
        knLogger.info(methodName, "Exit into method with mdn: " +KnGDPRTemplate.mdn(mdn));
        return responseDTO;
    }

    public KnOPSubsProfileInfoDTO searchCorpAddressBook(KnIPSubscriberInfoDTO subscriberDTO, KnPersisterTxn persisterTxn) throws KnProvBOException {
    	String methodName = "searchCorpAddressBook(KnIPSubscriberInfoDTO, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY: searchCorpAddressBook with DTO ", subscriberDTO);
        KnOPSubsProfileInfoDTO subsProfileRespDTO = null;
        try {
                xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
			// retrieving the Subscriber Info
			IProvXDMServerDAO provXdmServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB)
					.createProvXDMServerDAO();
			KnOPSubsProfileInfoDTO existingProfileInfoDTO = provInfoUtil.retrieveSubscriberInfo(subscriberDTO.getMdn(), persisterTxn);
	            knLogger.info(methodName, "existingProfileInfoDTO :",existingProfileInfoDTO);
			
			  String mcpttId_DB = existingProfileInfoDTO.getMcpttId();
			  String mcpttId_Input = subscriberDTO.getMcPttId();
	            knLogger.debug( methodName, "mcpttId_DB::"+KnGDPRTemplate.mcpttId(mcpttId_DB));
	            knLogger.debug( methodName, "mcpttId_Input::"+KnGDPRTemplate.mcpttId(mcpttId_Input));

			  
				if (mcpttId_Input != null && mcpttId_DB != null)
			{
				if (existingProfileInfoDTO.getMcpttCompliance() == 0) {
					if (!mcpttId_Input.isEmpty() && !mcpttId_DB.isEmpty()
							&& !(mcpttId_Input.trim().equals(mcpttId_DB.substring(5).trim()) || mcpttId_Input.trim().equals(mcpttId_DB.trim()))) {
						knLogger.debug(methodName, "MCPTTD is not matched with the MDN");
						throw new KnProvBOException(KnErrorCodes.Validator.INVALID_TOKEN_MCPTTID,
								"MCPTTD is not matched with the token");
					}
				} else if (existingProfileInfoDTO.getMcpttCompliance() == 1) {
					if (!mcpttId_Input.isEmpty() && !mcpttId_DB.isEmpty() && !mcpttId_Input.trim().equals(mcpttId_DB.trim())) {
						 knLogger.debug(methodName, "MCPTTD is not matched with the token");
						throw new KnProvBOException(KnErrorCodes.Validator.INVALID_TOKEN_MCPTTID,
								"MCPTTD is not matched with the token");
					}
				}
			}
	          
			subsProfileRespDTO = provXdmServerDAO.searchCorpAddressBook(subscriberDTO, persisterTxn);

        } catch (KnDBConnectionException ex) {
            knLogger.error(methodName, "DAO DBConnection Exception occured :" + ex);
            throw new KnProvBOException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);

        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occurred : " + ex);
            if (com.kodiak.xdms.server.common.resources.KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_INFO_NOT_FOUND, "searchCorpAddressBook doesn't exists");
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception occurred :" + e.getErrorCode());
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while searchCorpAddressBook" + e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred while searchCorpAddressBook", e);
        }
        knLogger.info(methodName, "EXIT:  searchCorpAddressBook - ", subsProfileRespDTO);

        return subsProfileRespDTO;
    }

    public KnOPSubsProfileInfoDTO updatePrivacyOptStatus(KnIPSubscriberInfoDTO subscriberDTO, KnPersisterTxn persisterTxn) throws KnProvBOException {
        String methodName = "updatePrivacyOptStatus(KnIPSubscriberInfoDTO, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY: privacyOptStatus with DTO ", subscriberDTO);
        Integer privacyFlagToExecute=0;
        KnOPSubsProfileInfoDTO existingProfileInfoDTO=null;
        IProvXDMServerDAO provXdmServerDAO=null;
        Integer privacyAmbDiscListenFromXDMS_SVC_CONFIG=null;
        IXDMServerDAO commonXDMServerDAO = null;
        String mcpttId_Input = subscriberDTO.getMcPttId();
        try {
            xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
            knLogger.debug( methodName, "xdmPttServerId::"+xdmPttServerId);
            // retrieving the Subscriber Info
             provXdmServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB)
                    .createProvXDMServerDAO();

            //4 validations
            String mdn = subscriberDTO.getMdn();
           // existingProfileInfoDTO = provInfoUtil.retrieveSubscriberInfoUU(mdn, persisterTxn);
            knLogger.debug( methodName, "mdn::"+KnGDPRTemplate.mdn(mdn));
            Integer optStatusValue=0;
            if(subscriberDTO.getOptStatusValue()!=null){
                optStatusValue=Integer.parseInt(subscriberDTO.getOptStatusValue());
            }

            existingProfileInfoDTO = provInfoUtil.retrieveSubscriberInfo(mdn, persisterTxn);
            knLogger.info(methodName, "existingProfileInfoDTO :",existingProfileInfoDTO);

            knLogger.debug( methodName, "ENTERS VALIDATION_1: ");
            //validation:: 2:
            if (existingProfileInfoDTO.getServiceAuthStatus() != KnConstants.SERVICE_AUTH_STATUS.ACTIVATED.value()) {
                knLogger.debug( methodName, "ENTERS VALIDATION_2: existingProfileInfoDTO.getServiceAuthStatus()::"+ existingProfileInfoDTO.getServiceAuthStatus());
                knLogger.debug(methodName, "Subscriber is not activated");
                throw new KnProvBOException(KnErrorCodes.BOEntity.SUBS_IS_NOT_ACTIVATED,
                        "Subscriber is not activated");
            }
            
            String mcpttId_DB = existingProfileInfoDTO.getMcpttId();
            if (mcpttId_Input != null && mcpttId_DB != null)
			{
				if (existingProfileInfoDTO.getMcpttCompliance() == 0) {
					if (!mcpttId_Input.isEmpty() && !mcpttId_DB.isEmpty()
							&& !(mcpttId_Input.equals(mcpttId_DB.substring(5).trim()) || mcpttId_Input.equals(mcpttId_DB.trim()))) {
						 knLogger.debug(methodName, "MCPTTD is not matched with the MDN");
						throw new KnProvBOException(KnErrorCodes.Validator.INVALID_TOKEN_MCPTTID,
								"MCPTTD is not matched with the token");
					}
				} else if (existingProfileInfoDTO.getMcpttCompliance() == 1) {
					if (!mcpttId_Input.isEmpty() && !mcpttId_DB.isEmpty() && !mcpttId_Input.equals(mcpttId_DB.trim())) {
						 knLogger.debug(methodName, "MCPTTD is not matched with the token");
						throw new KnProvBOException(KnErrorCodes.Validator.INVALID_TOKEN_MCPTTID,
								"MCPTTD is not matched with the token");
					}
				}
			}
	          
            
            
                //validation:: 3:
                knLogger.debug( methodName, "ENTERS VALIDATION_3: existingProfileInfoDTO.getClientPVmajorVer()::"+ existingProfileInfoDTO.getClientPVmajorVer());

                if(existingProfileInfoDTO.getClientPVmajorVer() < PROTOCOL_VERSION_16 )
                {
                    knLogger.debug(methodName, "Protocol version is lesser than 16");
                    throw new KnProvBOException(KnErrorCodes.BOEntity.INVALID_CLIENT,
                            "Protocol version is lesser than 16");
                }

                    //validation:: 4:
            // boolean privacyAmbDiscListenFlag = false;
            Integer privacyAmbDiscListen;
                    //if the subscriber is a corp or corp-public type then we will retirve the extcorpid of the corp
                    int corpIdFromSubscr = existingProfileInfoDTO.getCorpId();
                    knLogger.debug( methodName, "corpIdFromSubscr::"+corpIdFromSubscr);
                    if (corpIdFromSubscr != 0) {
                        knLogger.debug(methodName, "Subscriber is corp or corp-public type,Retrive the extCorpId for the corp=", corpIdFromSubscr);
                        //try {
                privacyAmbDiscListen = provXdmServerDAO.retrievePrivacyAmbDiscListenFlag(corpIdFromSubscr, persisterTxn);
                knLogger.debug(methodName, "privacyAmbDiscListenFlag::" + privacyAmbDiscListen);
                //getting all profile mdn of base mdn
                List<String> profileMdnList = provXdmServerDAO.getProfileMdnListByBaseMdn(mdn, persisterTxn);
                Map<String,Integer> mapListForPrivacy=new HashMap<>();
                mapListForPrivacy.put(mdn,optStatusValue);
                for(String profileMdn:profileMdnList){
                            mapListForPrivacy.put(profileMdn,optStatusValue);
                }

                if (privacyAmbDiscListen == null) {
                    //try {
                    KnXDMSServiceConfigDTO xdmsServiceConfigDTO = genInfoUtil.retrieveXDMSServiceConfig(xdmPttServerId, persisterTxn);
                    knLogger.debug(methodName, "xdmsServiceConfigDTO::" + xdmsServiceConfigDTO + " xdmPttServerId::" + xdmPttServerId);

                    privacyAmbDiscListenFromXDMS_SVC_CONFIG = xdmsServiceConfigDTO.getPrivacyAmbDiscListen();
                    knLogger.debug(methodName, "privacyAmbDiscListenFromXDMS_SVC_CONFIG::" + privacyAmbDiscListenFromXDMS_SVC_CONFIG);
                    privacyFlagToExecute = privacyAmbDiscListenFromXDMS_SVC_CONFIG;

                    if (privacyAmbDiscListenFromXDMS_SVC_CONFIG != null && privacyAmbDiscListenFromXDMS_SVC_CONFIG == 1) {
                        //do things after success- update the requested opt-in status in DG.POCSUBSCRIBERINFO
                        knLogger.debug(methodName, " IF ENTERS VALIDATION_4: ");
                        //provXdmServerDAO.updatePrivacyOptStatus(subscriberDTO, persisterTxn);
                        provXdmServerDAO.updateToPrivacyOptStatus(mapListForPrivacy,persisterTxn);

                        //
                        knLogger.debug(methodName, "commonXDMServerDAO::" + commonXDMServerDAO + " mdn::" +KnGDPRTemplate.mdn(mdn));

                        //retrieve the previous etag of dir doc
                        commonXDMServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
                        int previousDirDocEtag = commonXDMServerDAO.getCurrentEtagForDirDoc(mdn, persisterTxn);
                        //MINT-27448
                        //commonXDMServerDAO.updateEtagForDirDoc(mdn, persisterTxn);
                        knLogger.debug(methodName, "previousDirDocEtag::" + previousDirDocEtag);

                        //knLogger.debug(methodName, "Successfully updated the xdm directory");
                        //


                        //construct notify after success
                        /*List<KnOPDirChgDTO> dirChgDTOs = new ArrayList<KnOPDirChgDTO>();
                        KnOPDocChgDTO docChgDTO;
                        KnOPDirChgDTO dirChgDTO;
                        int i = 0;

                        dirChgDTO = new KnOPDirChgDTO();
                        docChgDTO = new KnOPDocChgDTO();
                        Collection<KnOPDocChgDTO> chgDocList = new ArrayList<KnOPDocChgDTO>();
                        // KnSubsProfileDTO targetSubsProfile = commonInfoUtil.getProfileDetails(mdn, PUBLIC_PROFILE, false, persisterTxn);

                        docChgDTO.setDocumentChgType(com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REPLACE.value());
                        // String subsConfigDocUri = commonInfoUtil.generateSubsConfigSelUri(mdn);
                        String subsConfigDocUri = provInfoUtil.generateSubsConfigSelUri(mdn);
                        docChgDTO.setDocUri(subsConfigDocUri);
                        docChgDTO.setNewEtag(String.valueOf(Calendar.getInstance().getTimeInMillis()));
                        chgDocList.add(docChgDTO);
                        //populating the XDM Directory DTO
                        dirChgDTO.setDocChgDTO(chgDocList);
                        dirChgDTO.setXcapRootURI(genInfoUtil.getXCAPRootURI(mdn, persisterTxn));
                        dirChgDTO.setPocHome(existingProfileInfoDTO.getPoCHome());
                        dirChgDTO.setPresenceHome(existingProfileInfoDTO.getPresenceHome());
                        String dirDocUri = genInfoUtil.generateDirDocUri(mdn);
                        dirChgDTO.setDirUri(dirDocUri);
                        // dirChgDTO.setDirPrevEtag(String.valueOf(previousEtags.get(i)));
                        // int newEtag = previousEtags.get(i) + 1;
                        //dirChgDTO.setDirNewEtag(String.valueOf(newEtag));

                        //
                        dirChgDTO.setDirPrevEtag(String.valueOf(previousDirDocEtag));
                        int newEtag = previousDirDocEtag + 1;
                        dirChgDTO.setDirNewEtag(String.valueOf(newEtag));
                        //
                        dirChgDTO.setProtoVersion(String.valueOf(existingProfileInfoDTO.getClientPVmajorVer()));
                        dirChgDTO.setClientType(existingProfileInfoDTO.getSubsClientType());
                        //populating the Dir chg DTO to response
                        dirChgDTOs.add(dirChgDTO);
                        i++;
                        knLogger.debug(methodName, "dirChgDTO::" + dirChgDTO + "dirChgDTOs::" + dirChgDTOs);

                        existingProfileInfoDTO.setDirChgtDTO(dirChgDTO);*/

                        //set response after notification success
                        existingProfileInfoDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.SUCCESS.value());
                        existingProfileInfoDTO.setResponseCode(KnProvConstants.SUCCESS_CODE);
                        existingProfileInfoDTO.setResponseMessage(KnProvConstants.UPDATE_PRIVACY_OPT_STATUS_SUCCESS);
                        knLogger.debug(methodName, "after response set : existingProfileInfoDTO::" + existingProfileInfoDTO);

                    } else {
                        knLogger.debug(methodName, "If mdn exists in DG.POCSUBSCRINFO table and is in activated state and PV>=16 BUT if the DG.POCCORPINFO:PRIVACY_AMB_DISC_LISTEN is not set to true AND DG.XDMS_SVC_CONFIG:PRIVACY_AMB_DISC_LISTEN is not set to true");
                        throw new KnProvBOException(KnErrorCodes.BOEntity.XDMS_DOC_SUB_PRX_CONFIG_NOT_FOUND,
                                "Both DG.POCCORPINFO:PRIVACY_AMB_DISC_LISTEN and DG.XDMS_SVC_CONFIG:PRIVACY_AMB_DISC_LISTEN is not set to true");
                    }

                } else {

                    privacyFlagToExecute = privacyAmbDiscListen;

                    //do things after success- update the requested opt-in status in DG.POCSUBSCRIBERINFO
                    knLogger.debug(methodName, "Else ENTERS VALIDATION_4: ");
                    if (privacyFlagToExecute == 1) {
                        //provXdmServerDAO.updatePrivacyOptStatus(subscriberDTO, persisterTxn);
                        provXdmServerDAO.updateToPrivacyOptStatus(mapListForPrivacy,persisterTxn);

                        //construct notify after success
                        //retrieve the previous etag of dir doc

                        commonXDMServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
                        int previousDirDocEtag = commonXDMServerDAO.getCurrentEtagForDirDoc(mdn, persisterTxn);
                        //MINT-27448
                        //commonXDMServerDAO.updateEtagForDirDoc(mdn, persisterTxn);

                        //knLogger.debug(methodName, "Successfully updated the xdm directory");
                        //


                        //construct notify after success
                        List<KnOPDirChgDTO> dirChgDTOs = new ArrayList<KnOPDirChgDTO>();
                        KnOPDocChgDTO docChgDTO;
                        KnOPDirChgDTO dirChgDTO;
                        int i = 0;

                        dirChgDTO = new KnOPDirChgDTO();
                        docChgDTO = new KnOPDocChgDTO();
                        Collection<KnOPDocChgDTO> chgDocList = new ArrayList<KnOPDocChgDTO>();
                        // KnSubsProfileDTO targetSubsProfile = commonInfoUtil.getProfileDetails(mdn, PUBLIC_PROFILE, false, persisterTxn);

                        docChgDTO.setDocumentChgType(com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REPLACE.value());
                        // String subsConfigDocUri = commonInfoUtil.generateSubsConfigSelUri(mdn);
                        String subsConfigDocUri = provInfoUtil.generateSubsConfigSelUri(mdn);
                        docChgDTO.setDocUri(subsConfigDocUri);
                        docChgDTO.setNewEtag(String.valueOf(Calendar.getInstance().getTimeInMillis()));
                        chgDocList.add(docChgDTO);
                        //populating the XDM Directory DTO
                        dirChgDTO.setDocChgDTO(chgDocList);
                        dirChgDTO.setXcapRootURI(genInfoUtil.getXCAPRootURI(mdn, persisterTxn));
                        dirChgDTO.setPocHome(existingProfileInfoDTO.getPoCHome());
                        dirChgDTO.setPresenceHome(existingProfileInfoDTO.getPresenceHome());
                        String dirDocUri = genInfoUtil.generateDirDocUri(mdn);
                        dirChgDTO.setDirUri(dirDocUri);
                        // dirChgDTO.setDirPrevEtag(String.valueOf(previousEtags.get(i)));
                        // int newEtag = previousEtags.get(i) + 1;
                        //dirChgDTO.setDirNewEtag(String.valueOf(newEtag));

                        //
                        dirChgDTO.setDirPrevEtag(String.valueOf(previousDirDocEtag));
                        int newEtag = previousDirDocEtag + 1;
                        dirChgDTO.setDirNewEtag(String.valueOf(newEtag));
                        //
                        dirChgDTO.setProtoVersion(String.valueOf(existingProfileInfoDTO.getClientPVmajorVer()));
                        dirChgDTO.setClientType(existingProfileInfoDTO.getSubsClientType());
                        //populating the Dir chg DTO to response
                        dirChgDTOs.add(dirChgDTO);
                        i++;

                        existingProfileInfoDTO.setDirChgtDTO(dirChgDTO);

                        //set response after notification success
                        existingProfileInfoDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.SUCCESS.value());
                        existingProfileInfoDTO.setResponseCode(KnProvConstants.SUCCESS_CODE);
                        existingProfileInfoDTO.setResponseMessage(KnProvConstants.UPDATE_PRIVACY_OPT_STATUS_SUCCESS);
                        knLogger.debug(methodName, "after response set : existingProfileInfoDTO::" + existingProfileInfoDTO);

                    }
                }
            }
        }
         catch (KnDBConnectionException ex) {
            knLogger.error(methodName, "DAO DBConnection Exception occured :" + ex);
            throw new KnProvBOException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);

        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occurred : " + ex);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception occurred :" + e.getErrorCode());
            throw e;
            //
            //validation:: 1:
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while privacyOptStatus" + e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred while privacyOptStatus", e);
        }
        knLogger.info(methodName, "EXIT:  privacyOptStatus dto - ", existingProfileInfoDTO);

        existingProfileInfoDTO.setPrivacyValueToExecute(privacyFlagToExecute);
        return existingProfileInfoDTO;
    }


    public KnXDMProfileIdMdnMapRespDTO getMdnProfileIdsForMcPttIds(KnIPSubscriberInfoDTO subscriberDTO, boolean readOnly,
                                                                   KnPersisterTxn persisterTxn) throws KnDAOException, KnProvBOException {
        String methodName = "getMdnProfileIdsForMcPttIds(KnIPSubscriberInfoDTO,boolean, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY: getMdnProfileIdsForMcPttIds with McpttIds ", subscriberDTO.getMcPttIds());
        KnXDMProfileIdMdnMapRespDTO subsProfileIdRespDTO = new KnXDMProfileIdMdnMapRespDTO();
        subsProfileIdRespDTO.setProfileIdMdnMap(provInfoUtil.retrieveProfileIdMDNsByMcpttIds(subscriberDTO.getMcPttIds(), readOnly, persisterTxn));
        knLogger.info(methodName, "EXIT:  subsProfileIdRespDTO:", subsProfileIdRespDTO);
        return subsProfileIdRespDTO;
    }

    public KnOPActivationInfoDTO selectProfileMdn(KnIPSelectProfileMdnDTO selectProfileMdnDTO, KnPersisterTxn persisterTxn)
            throws KnProvBOException, KnValidationException {
        String methodName = "selectProfileMdn(KnIPSelectProfileMdnDTO)";

        boolean ownedTxn = false;
        KnOPActivationInfoDTO responseDTO=new KnOPActivationInfoDTO();
        try{
            knLogger.debug(methodName,"selectProfileMdnDTO ",selectProfileMdnDTO);
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            try {
                xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
            } catch (KnBOException e) {
                knLogger.error(methodName, "failed to retrieve xdm Ptt Sever Id");
                knLogger.error(methodName, e);
                if (ownedTxn) {
                    rollback(persisterTxn);
                }
                throw new KnProvBOException(KnErrorCodes.BOEntity.XDMS_PTT_ID_NOT_FOUND, "Failed to retrieve XDM PTT Server ID", e);
            }

            String mdn=selectProfileMdnDTO.getMdn();
            String profileMdn=selectProfileMdnDTO.getProfileMdn();

            KnSubsProfilePersistDTO subsProfilePersistDTO=new KnSubsProfilePersistDTO();
            KnOPSubsProfileInfoDTO baseMdnInfo=null;
			if (!mdn.startsWith(TELURI)) {
				baseMdnInfo = provInfoUtil.retrieveSubscriberInfo(mdn, persisterTxn);
			} else {
				baseMdnInfo = provInfoUtil.retrieveBaseMdnByMcpttId(mdn, persisterTxn);
			}
            KnOPSubsProfileInfoDTO profileMdnInfo = provInfoUtil.retrieveSubscriberInfo(profileMdn, persisterTxn);

            subsProfilePersistDTO.setProfileMdnMcpttId(baseMdnInfo.getMcpttId());
            subsProfilePersistDTO.setBaseMdnMcpttId(profileMdnInfo.getMcpttId());
            subsProfilePersistDTO.setInputDTO(selectProfileMdnDTO);

            knLogger.debug(methodName, "before Validation the data ",subsProfilePersistDTO);
            validatorFwk.validate(subsProfilePersistDTO);
            knLogger.debug(methodName, "Successfully Validated the data ");

            KnOPSubsProfileInfoDTO baseMdnRespInfo =provInfoUtil.retrieveBaseMdnByMcpttId(profileMdnInfo.getMcpttId(), persisterTxn);
            if(baseMdnRespInfo==null){
                throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_INFO_NOT_FOUND,
                        "Subscriber Profile does not exist");
            }
            responseDTO.setEmail(baseMdnRespInfo.getEmailAddress());
            responseDTO.setMdn(baseMdnRespInfo.getMdn());
            responseDTO.setPassword(baseMdnRespInfo.getClientPassword());

        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occurred : ", ex);
            knLogger.error(methodName, ex);
            if (com.kodiak.xdms.server.common.resources.KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_INFO_NOT_FOUND, "Subscriber Profile does not exist");
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception occurred :", e);
            knLogger.error(methodName, e);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw e;
        } catch (KnValidationException vex) {
            knLogger.error(methodName, "Validation Exception occurred :", vex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw vex;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while activate Subscriber");
            knLogger.error(methodName, e);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred while activate Subscriber", e);
        }
        knLogger.info(methodName, "EXIT: responseDTO - ");
        return responseDTO;
    }

    @Override
    public String getMdnForUnassign(String baseMdn, String userProfileId, KnPersisterTxn persisterTxn) throws KnProvBOException {
        String methodName = "getMdnForUnassign(String,String, KnPersisterTxn)";
        boolean ownedTxn = false;
        //KnSubsProfilePersistDTO subsProfilePersistDTO = new KnSubsProfilePersistDTO();
        knLogger.info(methodName, "ENTRY: getMdnForUnassign for baseMdn:", KnGDPRTemplate.mdn(baseMdn));
        String mdn=null;
        try {
            xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
            // retrieving the Subscriber Info
            IProvXDMServerDAO provXdmServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB)
                    .createProvXDMServerDAO();

            mdn = provXdmServerDAO.getMdnForUnassign(baseMdn, userProfileId, persisterTxn);

        } catch (KnDBConnectionException ex) {
            knLogger.error(methodName, "DAO DBConnection Exception occured :" + ex);
            throw new KnProvBOException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);

        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occurred : " + ex);
            if (com.kodiak.xdms.server.common.resources.KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_INFO_NOT_FOUND, "getMdnForUnassign doesn't exists");
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception occurred :" + e.getErrorCode());
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while getMdnForUnassign" + e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred while getMdnForUnassign", e);
        }
        knLogger.info(methodName, "EXIT:  getMdnForUnassign - ", KnGDPRTemplate.mdn(mdn));

        return mdn;
    }

    public List<String> getMdnForUPM(String baseMdn, KnPersisterTxn persisterTxn) throws KnProvBOException {
        return getMdnForUPM(baseMdn, false, persisterTxn);
    }

    public List<String> getMdnForUPM(String baseMdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnProvBOException {
        String methodName = "getMdnForUPM(String, boolean, KnPersisterTxn)";
        boolean ownedTxn = false;
        //KnSubsProfilePersistDTO subsProfilePersistDTO = new KnSubsProfilePersistDTO();
        knLogger.debug(methodName, "ENTRY: getMdnForUPM for baseMdn:", KnGDPRTemplate.mdn(baseMdn));
        List<String> mdnList=new ArrayList<>();
        Map<String,KnOPSubsProfileInfoDTO> mdnUpmFsMap = new HashMap<>();
        try {
            xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
            // retrieving the Subscriber Info
            IProvXDMServerDAO provXdmServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB)
                    .createProvXDMServerDAO();

            mdnUpmFsMap = provXdmServerDAO.getProfileMdnNupmfsByBaseMdn(baseMdn, readOnly, persisterTxn);
            mdnList.addAll(mdnUpmFsMap.keySet());
        } catch (KnDBConnectionException ex) {
            knLogger.error(methodName, "DAO DBConnection Exception occured :" + ex);
            throw new KnProvBOException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);

        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occurred : " + ex);
            if (com.kodiak.xdms.server.common.resources.KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_INFO_NOT_FOUND, "getMdnForUPM doesn't exists");
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception occurred :" + e.getErrorCode());
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while getMdnForUPM" + e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred while getMdnForUPM", e);
        }
        knLogger.debug(methodName, "EXIT:  getMdnForUPM - ", KnGDPRTemplate.mdnList(mdnList));

        return mdnList;
    }

    @Override
    public KnOPCreateSubsInfoDTO createSubscriberForAssignUserProfile(KnIPSubsProvInfoDTO subsProvInputDTO, KnPersisterTxn persisterTxn) throws KnProvBOException, KnValidationException {
        String methodName = "createSubscriberForAssignUserProfile(KnSubsProfilePersistDTO, KnPersisterTxn)";
        KnSubsProfilePersistDTO subsProvPersistDTO;
        boolean ownedTxn = false;
        KnOPCreateSubsInfoDTO respDTO = new KnOPCreateSubsInfoDTO();
        knLogger.info(methodName, "ENTRY: Input DTO - ", subsProvInputDTO, " Txn - ", persisterTxn);
        String xdmPttServerId;
        Collection<Integer> successPegs = new ArrayList<Integer>();
        List<Integer> clientTypeList = new ArrayList<Integer>();
        KnExtSubsPersistDTO extSubsPersistResponseDTO;
        BitSet bitset = new BitSet();
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening transaction ");
                persisterTxn.open();
                ownedTxn = true;
            }
            try {
                xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
            } catch (KnBOException e) {
                knLogger.error(methodName, "failed to retrieve xdm Ptt Sever Id");
                knLogger.error(methodName, e);
                if (ownedTxn) {
                    rollback(persisterTxn);

                }
                throw new KnProvBOException(KnErrorCodes.BOEntity.XDMS_PTT_ID_NOT_FOUND, "Failed to retrieve XDM PTT Server ID", e);
            }
            String mdn = subsProvInputDTO.getMdn();
            String ufmi=subsProvInputDTO.getUfmi();

            String tierPackageId = null;
            Map<String, Integer> addonPackageIds = new HashMap<>();
            Map<String, Integer> addPkgIds = new HashMap<String, Integer>();
            if (subsProvInputDTO.getPkgIdMap() != null
                    && subsProvInputDTO.getPkgIdMap().get(KnConstants.ADD_ACTION) != null) {
                addPkgIds = subsProvInputDTO.getPkgIdMap().get(KnConstants.ADD_ACTION);
                for (Entry<String, Integer> entry : addPkgIds.entrySet()) {
                    if (entry.getValue().intValue() == KnConstants.TIER_PKG_TYPE.intValue()) {
                        tierPackageId = entry.getKey();
                    } else if (entry.getValue().intValue() == KnConstants.ADDON_PKG_TYPE.intValue()) {
                        addonPackageIds.put(entry.getKey(), entry.getValue());
                    }
                }
            }
            knLogger.info(methodName, "addPkgIds map " ,addPkgIds);
            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();

            KnOPSubsProfileInfoDTO subsProfile = null;
            try {
                subsProfile = provXDMServerDAO.selectSubscriberProfileByAliasMdnForAssignUserProfile(mdn, persisterTxn);
            } catch (KnDAOException e) {
                knLogger.error(methodName, e);
            }
            if (subsProfile != null) {
                knLogger.error(methodName, "Mdn is present as aliasMdn");
                throw new KnProvBOException(KnErrorCodes.BOEntity.MDN_PRESENT_AS_ALIASMDN,
                        "Mdn is present as aliasMdn");
            }

            //Authenticate the request
            knLogger.debug(methodName, "No Authorisation is being performed");

            //populate the Subscriber Prov Persist DTO.
            subsProvPersistDTO = new KnSubsProfilePersistDTO();
            subsProvPersistDTO.setInputDTO(subsProvInputDTO);
            subsProvPersistDTO.setHierarchyType(subsProvInputDTO.getHierarchyType());
            subsProvPersistDTO.setMdn(mdn);
            subsProvPersistDTO.setXDMSHome(xdmPttServerId);
            subsProvPersistDTO.setIMEI(subsProvInputDTO.getIMEI());
            subsProvPersistDTO.setProvFSMap(subsProvInputDTO.getProvFSMap());
            subsProvPersistDTO.setFirstNetIndicator(subsProvInputDTO.getFirstNetIndicator());

            String networkName = subsProvInputDTO.getNetworkName();
            Integer subsClientType = subsProvInputDTO.getSubsClientType();
            knLogger.debug(methodName,"subsProvInputDTO :",subsProvInputDTO);

            Map<String, String> paramNameValueMap = genInfoUtil.retrieveRTXConfigValues(persisterTxn);
            if (null == networkName || networkName.isEmpty()) {
                // as per FRS
                if (KnProvConstants.SUBS_CLIENT_TYPE.POC_NNI_Group_MDN.value() == subsClientType)
                    networkName=paramNameValueMap.get(KnConstants.XDMS_LMR_SUB_DEFAULT_NAME);
                else
                    networkName = mdn; //baseMdn value
            }

            knLogger.debug(methodName,"network name :: ", KnGDPRTemplate.name(networkName));

            subsProvPersistDTO.setNetworkName(networkName);

            knLogger.debug(methodName, "subsProvPersistDTO :: ", subsProvPersistDTO );

            knLogger.debug(methodName,"subsClientType :",subsClientType);
            knLogger.debug(methodName,"subsProvInputDTO :",subsProvInputDTO);

            //9.0 onwards as per User check, discreet and ambient listening feature new params serviced_status_op/authUser are introduced
            //these new param need to set there default values
            //Default value for service status op - provisioned (0)
            // service auth user - activated(2)

            int serviceStatusOp = 0;
            int serviceStatusAuthUser = 2;
            KnDefaultMCSClientInfo defaultMCSClientInfo=null;

            //serviceauth status set to ACTIVATED for subscriber 7,8 & 17
            if (KnProvConstants.SUBS_CLIENT_TYPE.POC_NNI_Alias_MDN.value() == subsClientType ||
                    KnProvConstants.SUBS_CLIENT_TYPE.POC_NNI_Group_MDN.value() == subsClientType
                    || subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.SGMDNPATCH.value()) {
                // subsProvPersistDTO.setServiceAuthStatus(KnConstants.SERVICE_AUTH_STATUS.ACTIVATED.value());
                //SERVICE_STATUS_OP for client type 7, 8 & 17 is set to ACTIVATED
                serviceStatusOp = KnConstants.SERVICE_STATUS_OP.ACTIVATED.value();
             //   subsProvPersistDTO.setServiceStatusOp(serviceStatusOp);
            }else {
                //while subscriber is created the service auth status is always set to Provisioned - 0 for subscriber other than 7 & 8
                // 9.0 onwards service auth status is set based on service_status_op and service_status_authuser value
                //  subsProvPersistDTO.setServiceAuthStatus(KnConstants.SERVICE_AUTH_STATUS.PROVISIONED.value());
                if (KnConstants.MCSCOMPLIANCE == subsProvInputDTO.getMcsCompliance()
                        && (subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.POC_WIFIONLY.value()
                        || subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.CROSS_CARRIER_PTT_CLIENT.value())) {
                    serviceStatusOp = KnConstants.SERVICE_STATUS_OP.ACTIVATED.value();
                   // subsProvPersistDTO.setServiceStatusOp(serviceStatusOp);
                    defaultMCSClientInfo = KnGeneralCacheUtil.getInstance().retrieveDefaultMCSClientInfo();
                } else {
                    serviceStatusOp = KnConstants.SERVICE_STATUS_OP.PROVISIONED.value();
                  //  subsProvPersistDTO.setServiceStatusOp(serviceStatusOp);
                }
            }

            serviceStatusAuthUser = KnConstants.SERVICE_STATUS_AUTHUSER.ACTIVATED.value();
          //  subsProvPersistDTO.setServiceStatusAuthUser(serviceStatusAuthUser);
            //calculating servicece auuth status as per new rule User check ambient feature
          //  subsProvPersistDTO.setServiceAuthStatus(genInfoUtil.calculateServiceAuthStatus(serviceStatusOp, serviceStatusAuthUser));

            int publicSubscriptionType = subsProvInputDTO.getPublicSubscriptionType();
            subsProvPersistDTO.setPublicSubscriptionType(publicSubscriptionType);
            int corporateSubscriptionType = subsProvInputDTO.getCorporateSubscriptionType();
            subsProvPersistDTO.setCorporateSubscriptionType(corporateSubscriptionType);

            subsProvPersistDTO.setAccountId(subsProvInputDTO.getAccountId());
            subsProvPersistDTO.setAffiliateId(subsProvInputDTO.getAffiliateId());
            subsProvPersistDTO.setPayType(subsProvInputDTO.getPayType());
            subsProvPersistDTO.setRoamingTypes(subsProvInputDTO.getRoamingTypes());

            int pocDonorRadioSupport = genInfoUtil.retrieveXDMSServiceConfig(xdmPttServerId, persisterTxn).getEnablePocDonorRadioSupport();
            int thirdPartyPocClientSupport = genInfoUtil.retrieveXDMSServiceConfig(xdmPttServerId, persisterTxn).getEnable3rdPartyPocClientSupport();
            int iDenInterOp = genInfoUtil.retrieveXDMSServiceConfig(xdmPttServerId, persisterTxn).getiDenInterOp();

            subsProvPersistDTO.setUfmi(subsProvInputDTO.getUfmi());
            subsProvPersistDTO.setiDenUserName(subsProvInputDTO.getiDenUserName());
            subsProvPersistDTO.setiDenPassword(subsProvInputDTO.getiDenPassword());
            subsProvPersistDTO.setiDenBusUnitId(subsProvInputDTO.getiDenBusUnitId());
            subsProvPersistDTO.setSubsClientType(subsClientType);
            subsProvPersistDTO.setEmailAddress(subsProvInputDTO.getEmailAddress());
            subsProvPersistDTO.setSubsDefPttRadio(subsProvInputDTO.getSubsDefPttRadio());
            //Converged Client  changes starts
            int corpId = -1;
            KnOPCorpProfileInfoDTO corpProfileInfoDTO = null;
            String extCorpId = subsProvInputDTO.getExtCorpId();
            String poCPttServerId = null;
            boolean updateCorpHome = false;
            String presencePttServerId = null;
            if (corporateSubscriptionType == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
                knLogger.debug(methodName, "verifying if corporate profile already exists");
                corpProfileInfoDTO = provXDMServerDAO.retrieveCorporateProfile(extCorpId, persisterTxn);
                corpId = corpProfileInfoDTO.getCorpId();
                knLogger.debug(methodName, "Corporate Id Retrieved - ", corpId);
                corpProfileInfoDTO.setExtCorpId(extCorpId);

                if(subsProvPersistDTO.getSubsDefPttRadio() != com.kodiak.common.resources.KnConstants.SUBSCR_DEF_PTTRADIO_ENABLED ){
                    knLogger.debug(methodName, "SUBSCR_DEF_PTTRADIO is disabled proceeding to check system & corp level ");
                    int subsDef=genInfoUtil.retrieveXDMSServiceConfig(xdmPttServerId, persisterTxn).getSubsDefPttRadio();
                }
                if(subsProvPersistDTO.getSubsDefPttRadio() == com.kodiak.common.resources.KnConstants.SUBSCR_DEF_PTTRADIO_ENABLED ){

                    knLogger.debug(methodName, "Converting regular client type ",subsProvPersistDTO.getSubsClientType()," to  PTTRADIO Client type");
                    switch(subsProvPersistDTO.getSubsClientType()){
                        case 1:
                            subsClientType=KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value();
                            break;
                        case 5:
                            subsClientType=KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value();
                            break;
                        case 10:
                            subsClientType=KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value();
                            break;
                        default:
                            break;
                    }
                }
            }
            //Converged Client  changes ends
            subsProvPersistDTO.setDispatchGroupMember(KnProvConstants.DISPATCH_GROUP_MEMBER.NOT_MEMBER.value());
            subsProvPersistDTO.setSubsClientType(subsClientType);
            subsProvPersistDTO.setLicenseType(subsProvInputDTO.getLicenseType());
            //Validate the Persist DTO
            // validatorFwk.validate(subsProvPersistDTO);
            knLogger.debug(methodName, "Successfully validated the data");
            int clusterId = Integer.parseInt(System.getenv(CLUSTERID_ENV_NAME));
            Map<String, String> microServicesParamNameValueMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
            String deviceSharingFlag = microServicesParamNameValueMap.get(DEVICE_SHARING_FEATURE_FLAG);
            knLogger.info(methodName, "deviceSharingFlag",  deviceSharingFlag);
            int deviceSharewifiFlag=Integer.parseInt(microServicesParamNameValueMap.get(AUTO_DEVICESHARE_WIFI_CC));
            knLogger.info(methodName, "deviceSharewifiFlag-->",  deviceSharewifiFlag);
            if ((subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.POC_WIFIONLY.value()
                    || subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.CROSS_CARRIER_PTT_CLIENT.value()
                    || subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value()
                    || subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value()) && Integer.parseInt(deviceSharingFlag) == ENABLED
                    && deviceSharewifiFlag == ENABLED){
                knLogger.debug(methodName, "license type for wifi , crosscarrier and its ptt ");
                subsProvPersistDTO.setLicenseType(KnConstants.USER_LICENSE_TYPE);
            }else{
                knLogger.debug("setting the else val-->"+subsProvPersistDTO.getLicenseType());
                subsProvPersistDTO.setLicenseType(subsProvInputDTO.getLicenseType());
            }

            IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);


            ArrayList<String> mdnList = new ArrayList<>();
            mdnList.add(mdn);
            knLogger.debug(methodName, "Getting  External subscriber Profile");
            extSubsPersistResponseDTO = provXDMServerDAO.getExtSubscriberInfo(mdnList, persisterTxn);

            // changes for Corp Account Anchoring
            //Always getting pocHome from basemdn for profile mdn,to avoid MAX limit for all the POC Home has been reached.
            //poCPttServerId = provInfoUtil.getSubscriberPoCHome(mdn, persisterTxn);
            poCPttServerId =subsProvInputDTO.getPocPttServerId();
            knLogger.info(methodName, "poCPttServerId :", poCPttServerId);


            //presencePttServerId = provInfoUtil.getSubsPresenceHome(mdn, poCPttServerId, persisterTxn);
            presencePttServerId =subsProvInputDTO.getPresPttServerId();
            knLogger.info(methodName, "presencePttServerId :", presencePttServerId);

            subsProvPersistDTO.setPoCHome(poCPttServerId);
            subsProvPersistDTO.setPresenceHome(presencePttServerId);

            //validate the received Feature set with the License.
            //check if the received subsFeatureSet1 is null or 0 then apply the default subsFeatureSet1.

            String corpFS2 = null;
            String opsCorpFS2 = null;
            String userProfileFS2=null;
            int existingPairInd = -1;
            if (corporateSubscriptionType == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
                // -->. creating Corporate Profile
                knLogger.debug(methodName, "verifying if corporate profile already exists");
                corpId = corpProfileInfoDTO.getCorpId();
                knLogger.debug(methodName, "Corporate Id Retrieved - ", corpId);

                int svcDispatchType = genInfoUtil.retrieveXDMSServiceConfig(xdmPttServerId, persisterTxn).getWebDispatchEnabled();
                int corpDispatchType = corpProfileInfoDTO.getWebDispatchEnabled();
                knLogger.debug(methodName, "svcDispatchType & corpDispatchType - ", svcDispatchType, corpDispatchType);

                if (corpId > 0) {
                    knLogger.debug(methodName, "corporate profile already exists with corp id [", corpId, "] ", "for extCorpId ", extCorpId);
                    existingPairInd = corpProfileInfoDTO.getPairedContactListId();

                    if (existingPairInd > 0) {
                        paramNameValueMap = genInfoUtil.retrieveRTXConfigValues(persisterTxn);
                        knLogger.debug(methodName, "param value map", paramNameValueMap);
                        int corpAutoPairingCnt = Integer.valueOf(paramNameValueMap.get(KnConstants.CORP_AUTO_PAIRING_SIZE));
                    }

                    Collections.addAll(clientTypeList, com.kodiak.common.resources.KnConstants.SUBS_CLIENT_TYPE.ALIASMDN.value(),
                            com.kodiak.common.resources.KnConstants.SUBS_CLIENT_TYPE.GROUPMDN.value(), com.kodiak.common.resources.KnConstants.SUBS_CLIENT_TYPE.SGMDNPATCH.value());
                    long corpProfileUpdateTime = Calendar.getInstance().getTimeInMillis();

                    subsProvPersistDTO.setCorpId(corpId);
                    opsCorpFS2 = corpProfileInfoDTO.getOpsCorpFS2();
                    corpFS2 = corpProfileInfoDTO.getCorpFS2();

                    if((KnProvConstants.SUBS_CLIENT_TYPE.DISPATCH_CLIENT.value() == subsClientType) &&
                            (KnProvConstants.WEB_DISPATCH_ENABLED == svcDispatchType || KnProvConstants.WEB_DISPATCH_ENABLED == corpDispatchType)){
                        subsProvPersistDTO.setDispatchType(KnProvConstants.WEB_DISPATCH_ENABLED);
                    }

                } else {
                    knLogger.debug(methodName, "creating the corporate Profile");

                    paramNameValueMap = genInfoUtil.retrieveRTXConfigValues(persisterTxn);
                    knLogger.debug(methodName, "param value map", paramNameValueMap);

                    //get final CorpFeatureSet
                    corpFS2 = featureSetUtil.getDefFinalCorpFS();
                    knLogger.debug(methodName, "final corpFS :", corpFS2);

                    //get final OpsCorpFeatureSet
                    opsCorpFS2 = featureSetUtil.getDefFinalOpsCorpFS();
                    //userProfileFS2=featureSetUtil.getDefFinalUserProfileFS();
                    knLogger.debug(methodName, "final opsCorpFS :", opsCorpFS2);

                    KnCorpProfilePersistDTO corpProfilePersistDTO = populateCorpProfileInfoDTO(xdmPttServerId, subsProvPersistDTO, persisterTxn);
                    corpProfilePersistDTO.setCorporateName(subsProvInputDTO.getCorporateName());
                    corpProfilePersistDTO.setCorpFS2(corpFS2);
                    corpProfilePersistDTO.setOpsCorpFS2(opsCorpFS2);
                    // corpProfilePersistDTO.setUserProfileFS2(userProfileFS2);

                    // getting doc config info from Registrar Service Config
                    KnPOCSvcConfigDTO knPOCSvcConfigDTO = provInfoUtil.retrievePOCSvcConfig(poCPttServerId);
                    corpProfilePersistDTO.setDynamicQosFlag(knPOCSvcConfigDTO.getDynamicQosFlag());

                    //added new  parameter for dynamic Qos
                    corpProfilePersistDTO.setDynamicQosFlag(knPOCSvcConfigDTO.getDynamicQosFlag());
                    subsProvPersistDTO.setCorpId(corpId);
                    if((KnProvConstants.SUBS_CLIENT_TYPE.DISPATCH_CLIENT.value() == subsClientType) &&
                            (KnProvConstants.WEB_DISPATCH_ENABLED == svcDispatchType)){
                        subsProvPersistDTO.setDispatchType(KnProvConstants.WEB_DISPATCH_ENABLED);
                    }
                }
            }

            //check if the received subsFeatureSet1 is null or 0 then apply the default subsFeatureSet1.
            String subsFS2 = subsProvInputDTO.getSubsFS2();
            if (subsFS2 == null) {
                BitSet finalFSBitSet = new BitSet(Long.SIZE);
                String basePkgCode = paramNameValueMap.get(KnConstants.BASE_PKGCODE);
                String basePkgDefFS = featureSetUtil.getDefSubsFeatureSetForBasePkg(publicSubscriptionType,
                        corporateSubscriptionType, subsClientType, basePkgCode, xdmPttServerId);
                knLogger.debug(methodName, "base pkg is applied - ", basePkgDefFS);
                BitSet basePkgDefFSBitSet = featureSetUtil.convertHexStringToBitSet(basePkgDefFS);
                finalFSBitSet.or(basePkgDefFSBitSet);
                if (!addPkgIds.isEmpty()) {
                    String pkgCodesFS = featureSetUtil.getDefSubsFeatureSetForPkgCodes(publicSubscriptionType,
                            corporateSubscriptionType, subsClientType, addPkgIds, xdmPttServerId);
                    knLogger.debug(methodName, "addPkgIds is present with subscriberFS - ", pkgCodesFS);
                    BitSet pkgCodes_BiSet = featureSetUtil.convertHexStringToBitSet(pkgCodesFS);
                    finalFSBitSet.or(pkgCodes_BiSet);
                }
                subsFS2 = featureSetUtil.convertBitSetToHexString(finalFSBitSet);
            }
            //Here we need to calculate the subscriberFS based on value i got
            Map<Integer, Integer> provFSMap = subsProvPersistDTO.getProvFSMap();
            knLogger.debug(methodName," subsProvPersistDTO's   ProvFS :", provFSMap);
            //same values of provFS1 and provFS1BitMask is used for calculation final subsFS after oring with bitmask
            String provFS2 = subsFS2;
            String provFS2BitMask = subsFS2;

            knLogger.debug(methodName," createSubscriber's   ProvFS :", provFSMap);
            knLogger.debug(methodName,"subsFS2 :",subsFS2,"provFS2 :",provFS2,"provFS2BitMask :",provFS2BitMask);
            //Generate the SubsFeatureSet by performing the BitMask with License SubsFSBITMASK
            subsFS2 = featureSetUtil.generateSubsFeatureSet(subsFS2, provFS2, provFS2BitMask);
            knLogger.debug(methodName, "final subsFS : ", subsFS2);

            //get final ClientFeatureSet
            int defaultClientPVMajorVersion = subsProvInputDTO.getClientPVmajorVer()==0?1:subsProvInputDTO.getClientPVmajorVer();
            String clientCapOverrideBitMask = featureSetUtil.getClientCapabilityBitMask(xdmPttServerId, defaultClientPVMajorVersion);

            String clientFS2 = featureSetUtil.getDefFinalClientFS(clientCapOverrideBitMask);

            if (KnConstants.MCSCOMPLIANCE == subsProvInputDTO.getMcsCompliance()) {
                clientFS2 = defaultMCSClientInfo.getClientFS2();
            }
            String opsFS2=null;
            String corpAdminFS2 = featureSetUtil.getDefFinalCorpAdminFS();
            userProfileFS2=subsProvInputDTO.getUserProfileFS2();
            knLogger.debug(methodName," userProfileFS2 :-",userProfileFS2);
            knLogger.debug(methodName, "defaultClientPVMajorVersion ", defaultClientPVMajorVersion, " ,clientCapOverrideBitMask=", clientCapOverrideBitMask);
            //self dnd
            String systemSelfDndFeature = microServicesParamNameValueMap.get(SELF_DND_FEATURE);
            boolean corpSelfDndPrivilege = KnGeneralUtil.getFeatureBitValue(corpProfileInfoDTO.getCorpFS2(), com.kodiak.common.resources.KnConstants.FEATURE_SET.SELF_DND_PRIVILEGE.value());
            if (systemSelfDndFeature != null && systemSelfDndFeature.equals(ENABLED_STRING)
                    && corpSelfDndPrivilege && subsProvInputDTO.getSelfDnDPrivilege() != null) {
                if (subsProvInputDTO.getSelfDnDPrivilege().equals(DISABLED_STRING)) {
                    Map<Integer, Integer> selfDndBit = new HashMap<>();
                    selfDndBit.put(com.kodiak.common.resources.KnConstants.FEATURE_SET.SELF_DND_PRIVILEGE.value(), DISABLED);
                    subsProvInputDTO.setUserProfileFS2(KnGeneralUtil.updateFeatureBit(userProfileFS2, selfDndBit));
                } else {
                    Map<Integer, Integer> selfDndBit = new HashMap<>();
                    selfDndBit.put(com.kodiak.common.resources.KnConstants.FEATURE_SET.SELF_DND_PRIVILEGE.value(), ENABLE);
                    subsProvInputDTO.setUserProfileFS2(KnGeneralUtil.updateFeatureBit(userProfileFS2, selfDndBit));
                }
            }

            //Generate the ActiveFeatureSet
			String activeFS2 = null;
			String xdmsFs2 = featureSetUtil.getDefFinalXdmsFS();
			if (corporateSubscriptionType == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
				opsFS2 = opsCorpFS2;
				activeFS2 = featureSetUtil.generateActiveFeatBitSet(poCPttServerId, presencePttServerId, xdmPttServerId,
						subsProvInputDTO.getClientFS2(), subsProvInputDTO.getSubsFS2(), opsFS2, opsCorpFS2, subsProvInputDTO.getCorpAdminFS2(), clientCapOverrideBitMask, subsProvInputDTO.getXdmsFS2(),
						subsProvInputDTO.getUserProfileFS2());
			} else {
				// get final OpsFeatureSet opsFS2 = featureSetUtil.getDefFinalOpsFS();
				knLogger.debug(methodName, "final opsFS : ", opsFS2);

				activeFS2 = featureSetUtil.generateActiveFeatBitSet(poCPttServerId, presencePttServerId, xdmPttServerId,
						subsProvInputDTO.getClientFS2(), subsProvInputDTO.getSubsFS2(), subsProvInputDTO.getOpsFS2(), clientCapOverrideBitMask, subsProvInputDTO.getXdmsFS2(), subsProvInputDTO.getUserProfileFS2());
			}

            validateMCXIdsUriFormat(subsProvInputDTO, persisterTxn);
            validateIfUserIdAlredyExistsInSystem(subsProvInputDTO.getUserId(), persisterTxn);
            Set<String> mdnSet = new HashSet<>();
            if (subsProvInputDTO.getMcId() != null && subsProvInputDTO.getMcId().startsWith(TELURI)) {
                mdnSet.add(subsProvInputDTO.getMcId().substring(TELURI.length()));
            }
            if (subsProvInputDTO.getMcpttId() != null && subsProvInputDTO.getMcpttId().startsWith(TELURI)) {
                mdnSet.add(subsProvInputDTO.getMcpttId().substring(TELURI.length()));
            }
            if (subsProvInputDTO.getMcVideoId() != null && subsProvInputDTO.getMcVideoId().startsWith(TELURI)) {
                mdnSet.add(subsProvInputDTO.getMcVideoId().substring(TELURI.length()));
            }
            if (subsProvInputDTO.getMcDataId() != null && subsProvInputDTO.getMcDataId().startsWith(TELURI)) {
                mdnSet.add(subsProvInputDTO.getMcDataId().substring(TELURI.length()));
            }
            List<String> mcsIdList = new ArrayList<String>();
            mcsIdList.addAll(mdnSet);
            // valiadteIfMCXIdsExistsAsMdnOrAliasMdn(mcsIdList, persisterTxn);

            // Adding input MDN along with mcsId list to check if it exist in KUIDPOOL
            mcsIdList.add(subsProvInputDTO.getMdn());
            // validateIfMdnOrMcidsPresentInKUIDPOOL(mcsIdList);

            Set<String> mcsIdSet = new HashSet<>();
            mcsIdSet.add(TELURI + subsProvInputDTO.getMdn());
            if (subsProvInputDTO.getMcId() != null) {
                mcsIdSet.add(subsProvInputDTO.getMcId());
            }
            if (subsProvInputDTO.getMcpttId() != null) {
                mcsIdSet.add(subsProvInputDTO.getMcpttId());
            }
            if (subsProvInputDTO.getMcVideoId() != null) {
                mcsIdSet.add(subsProvInputDTO.getMcVideoId());
            }
            if (subsProvInputDTO.getMcDataId() != null) {
                mcsIdSet.add(subsProvInputDTO.getMcDataId());
            }
            List<String> mcsIds = new ArrayList<String>();
            mcsIds.addAll(mcsIdSet);
            populateDefaultMCSIds(subsProvInputDTO);

            int maxUserProfileIndex = provXDMServerDAO.getMaxUserProfileIndex(subsProvInputDTO.getMdn(),persisterTxn);
            int newmaxUserProfileIndex = maxUserProfileIndex + 1; //for XDM-3851
            subsProvPersistDTO.setUserId(subsProvInputDTO.getUserId());
            subsProvPersistDTO.setMcId(subsProvInputDTO.getMcId());
            subsProvPersistDTO.setMcpttId(subsProvInputDTO.getMcpttId());
            subsProvPersistDTO.setMcVideoId(subsProvInputDTO.getMcVideoId());
            subsProvPersistDTO.setMcDataId(subsProvInputDTO.getMcDataId());
            subsProvPersistDTO.setSubsFS2(subsProvInputDTO.getSubsFS2());;
            subsProvPersistDTO.setClientFS2(subsProvInputDTO.getClientFS2());
            subsProvPersistDTO.setOpsFS2(subsProvInputDTO.getOpsFS2());
            subsProvPersistDTO.setActiveFS2(activeFS2);
            subsProvPersistDTO.setCorpAdminFS2(subsProvInputDTO.getCorpAdminFS2());
            subsProvPersistDTO.setUserProfileFS2(subsProvInputDTO.getUserProfileFS2());
            subsProvPersistDTO.setXdmsFS2(xdmsFs2);
            //During create subscriber will not belong to any profile hence default profile flag is set to 1 and profile index to 0
            subsProvPersistDTO.setIsDefaultProfile(subsProvInputDTO.getIsDefaultProfile()); //here isDefaultProfile yet not added for UI request
            subsProvPersistDTO.setUserProfileIndex(newmaxUserProfileIndex);
            subsProvPersistDTO.setUserProfileId(subsProvInputDTO.getUserProfileId()); //API request
            subsProvPersistDTO.setUserAgent(subsProvInputDTO.getUserAgent());

            knLogger.debug(methodName, "Subscriber client type ", subsClientType);
            long profileCreationTime = Calendar.getInstance().getTimeInMillis();
            subsProvPersistDTO.setProfileCreationTime(profileCreationTime);
            //since creation time is same as last profile time while create
            subsProvPersistDTO.setLastProfileUpdateTime(profileCreationTime);
            subsProvPersistDTO.setClientPVmajorVer(subsProvInputDTO.getClientPVmajorVer());
            subsProvPersistDTO.setClientPVminorVer(subsProvInputDTO.getClientPVminorVer());
            subsProvPersistDTO.setServiceAuthStatus(subsProvInputDTO.getServiceAuthStatus());
            subsProvPersistDTO.setServiceStatusAuthUser(subsProvInputDTO.getServiceStatusAuthUser());
            subsProvPersistDTO.setPreviousServiceAuthStatusToStore(subsProvInputDTO.getServiceAuthStatus());
            subsProvPersistDTO.setServiceStatusOp(subsProvInputDTO.getServiceStatusOp());
            subsProvPersistDTO.setIMEI(subsProvInputDTO.getIMEI());
            //subsProvPersistDTO.setLicenseType(subsProvInputDTO.getLicenseType());
            subsProvPersistDTO.setSwType(subsProvInputDTO.getSwType());
            if(subsProvInputDTO.getDerivedKey()!=null)
                subsProvPersistDTO.setDerivedKey(KnGeneralUtil.convertHexToAscii(subsProvInputDTO.getDerivedKey()));
            subsProvPersistDTO.setDynamicQosFlag(subsProvInputDTO.getDynamicQosFlag());
            subsProvPersistDTO.setPlatformType(subsProvInputDTO.getPlatformType());
            subsProvPersistDTO.setMcsCompliance(subsProvInputDTO.getMcsCompliance());
            subsProvPersistDTO.setLastActivationTime(subsProvInputDTO.getLastActivationTime());
            subsProvPersistDTO.setFeatureRelVersion(subsProvInputDTO.getFeatureRelVersion());
            subsProvPersistDTO.setClientPassword(subsProvInputDTO.getClientPassword());

            Integer qppPkgId = null;
            Integer profileId = null;
            Integer dataPkgId = null;
            if (!addonPackageIds.isEmpty()) {
                for (String addonPkgCode : addonPackageIds.keySet()) {
                    profileId = featureSetUtil.getAddProfIdForPkg(addonPkgCode, xdmPttServerId);
                    if (profileId != null) {
                        qppPkgId = genInfoUtil.getDataPkgId(KnConstants.QPP_DATA_PKG_TYPE, persisterTxn).get(profileId);
                        if (qppPkgId != null) {
                            dataPkgId = qppPkgId;
                            break;
                        } else if (dataPkgId == null)
                            dataPkgId = genInfoUtil.getDataPkgId(KnConstants.ADDON_DATA_PKG_TYPE, persisterTxn)
                                    .get(profileId);

                    }
                }
            }
            if (qppPkgId == null)
                qppPkgId = KnConstants.DEFAULT_QPP_ID;
            if (dataPkgId == null)
                dataPkgId = KnConstants.DEFAULT_DATAPKG_ID;
            if (profileId == null)
                profileId = KnConstants.DEFAULT_PROFILE_ID;

            subsProvPersistDTO.setQppPkgId(qppPkgId);
            knLogger.debug(methodName, "Subscriber Prov Persist DTO - ", subsProvPersistDTO);
            String aliasMdnForAssign = KnKUIDGenerator.getInstance().getKodiakUserIDs(KnKUIDConstants.COUNTRYCODE, 1).get(0);
            knLogger.debug(methodName,"aliasMdnForAssign :",aliasMdnForAssign);
            subsProvPersistDTO.setAliasMdn(aliasMdnForAssign);
            knLogger.debug(methodName, "adding an entry into Subscriber Info");
            subsProvPersistDTO.setMdn(aliasMdnForAssign);
            if(subsProvPersistDTO.getIsDefaultProfile()== ENABLE){
                knLogger.debug(methodName," resetting other default profile");
                provXDMServerDAO.updateDefaultProfileFlag(subsProvPersistDTO.getMcId()
                        ,subsProvPersistDTO.getCorpId()
                        ,DISABLE,persisterTxn);
            }
            //creating subscriber
            //XMSSTP-1552 setting some bits enabled by default for profile MDN
            subsProvPersistDTO.setCorpAdminFS2(KnGeneralUtil.getProfileMdnCorpAdminFS(subsProvPersistDTO.getCorpAdminFS2()));
            if(subsProvInputDTO.getEmergConfigTimer() != null) {
                subsProvPersistDTO.setEmergConfigTimer(subsProvInputDTO.getEmergConfigTimer());
            }
            if (subsProvInputDTO.getClusterId() != null) {
                subsProvPersistDTO.setClusterId(subsProvInputDTO.getClusterId());
            }
            if (subsProvInputDTO.getHierarchyId() != null) {
                subsProvPersistDTO.setHierarchyId(subsProvInputDTO.getHierarchyId());
            }
            if (subsProvInputDTO.getHierarchyRoot() != null) {
                subsProvPersistDTO.setHierarchyRoot(subsProvInputDTO.getHierarchyRoot());
            }
            provXDMServerDAO.createSubscrProfile(subsProvPersistDTO,Boolean.TRUE, persisterTxn);
            knLogger.debug(methodName, "adding an entry into DG.XDMS_TGSC for userProfile");
            //xdmServerDAO.
            if (KnConstants.MCSCOMPLIANCE == subsProvInputDTO.getMcsCompliance()) {
                KnDeviceInfoPersistDTO deviceInfoPersistDTO = new KnDeviceInfoPersistDTO();
                deviceInfoPersistDTO.setDeviceId(mdn);
                deviceInfoPersistDTO.setDeviceStatus(KnConstants.DEVICE_STATUS_OP.ACTIVATED.value());
                deviceInfoPersistDTO.setDeviceActTimeStamp(profileCreationTime);
                deviceInfoPersistDTO.setDeviceLastUsed(profileCreationTime);
                KnXDMSServiceConfigDTO xdmsServiceConfigDTO = genInfoUtil.retrieveXDMSServiceConfig(xdmPttServerId,
                        persisterTxn);
                String realm = xdmsServiceConfigDTO.getAuthRealm();
                String password = pwdUtil.generatePassword(com.kodiak.common.resources.KnConstants.MAX_OIDC_PASSWORD_LENGTH);;
                String clientPassword = provInfoUtil.generateHA1(KnConstants.TEL_URI_TEMPLATE + mdn, realm, password);
                deviceInfoPersistDTO.setDeviceDigestPassword(clientPassword);
                deviceInfoPersistDTO.setDeviceCreatedAs(KnConstants.DEVICECREATEDAS);
                deviceInfoPersistDTO.setDeviceClientId(mdn);
                deviceInfoPersistDTO.setDeviceshared(KnConstants.DEVICESHARED);
                deviceInfoPersistDTO.setDeviceIMPI(KnConstants.TEL_URI_TEMPLATE + mdn);

                KnDeviceImpiInfoPersistDTO deviceImpiInfo = new KnDeviceImpiInfoPersistDTO();
                deviceImpiInfo.setDeviceImpi(KnConstants.TEL_URI_TEMPLATE + mdn);
                deviceImpiInfo.setDeviceImpu(KnConstants.TEL_URI_TEMPLATE + mdn);

                // create OIDC profile

                String idmFqdn = genInfoUtil.getIDMInternalFqdn(persisterTxn);
                String appId = APP_ID.USERMCSCLIENTS.value();
                KnXDMSubsAliasDetailsRespDTO oidcResponseDTO = null;
                KnOPSubsProfileInfoDTO subsProfileInfoDTO = provInfoUtil.retrieveSubscriberInfo(mdn, persisterTxn);
                String pwdExpiry = microServicesParamNameValueMap.get(MCS_TEMP_PASSWORD_EXPIRY);
                knLogger.info(methodName, "deviceSharingFlag", deviceSharingFlag);
                String oidcUserId = null;
                if (aliasMdnForAssign != null) {
                    oidcUserId = aliasMdnForAssign;
                } else {
                    oidcUserId = subsProfileInfoDTO.getMdn();
                }

                int deviceSharing = 0;
                int passwordExpiry = 0;
                if (deviceSharingFlag != null)
                    deviceSharing = Integer.parseInt(deviceSharingFlag);
                if (pwdExpiry != null)
                    passwordExpiry = Integer.parseInt(pwdExpiry);
                long currentTimeInMilliSecond = System.currentTimeMillis();
                // long pwdExpiryInMilli = currentTimeInMilliSecond + (1000 * 60 * passwordExpiry);
                KnXDMSubsAliasDetailsReqDTO subsAliasDetailsReqDTO = new KnXDMSubsAliasDetailsReqDTO();
                subsAliasDetailsReqDTO.setUserid(oidcUserId);
                subsAliasDetailsReqDTO.setTemppwd(Boolean.FALSE);

                xdmPttServerId = KnDbUtil.getDBConfigInfo().getLocalPttId();
                knLogger.debug(methodName, "realm ", realm);
                Map<String, Object> oidcAttributes = new HashMap<>();
                oidcAttributes.put(ACTIONS, TMP_PWD_MODE.PASSWORD_INFO_MAIL.value());
                oidcAttributes.put(MCPTT_ID_OIDC, subsProfileInfoDTO.getMcpttId());
                oidcAttributes.put(MCVIDEO_ID_OIDC, subsProfileInfoDTO.getMcVideoId());
                oidcAttributes.put(MCDATA_ID_OIDC, subsProfileInfoDTO.getMcDataId());
                oidcAttributes.put(MC_ID_OIDC, subsProfileInfoDTO.getMcId());
                oidcAttributes.put(NETWORK_NAME, subsProfileInfoDTO.getNetworkName());
                oidcAttributes.put(DIGEST_PWD_OIDC, password);
                oidcAttributes.put(DEVICEIMPL_OIDC, KnConstants.TEL_URI_TEMPLATE + mdn);
                oidcAttributes.put(DEVICEIMPU_OIDC, KnConstants.TEL_URI_TEMPLATE + mdn);
                // oidcAttributes.put(MCS_SCOPES_OIDC,	KnGeneralUtil.populateOidcScopes(subsProfileInfoDTO.getActiveFS2()));
                subsAliasDetailsReqDTO.setAttributes(oidcAttributes);
                subsAliasDetailsReqDTO.setEmail(subsProfileInfoDTO.getUserId());
                // oidcResponseDTO = KnManageSyncUserProfileUtil.getInstance().createIDMUserForOIDCMgmt(subsAliasDetailsReqDTO, appId, idmFqdn, false, null);

                knLogger.debug(methodName, "oidcResponseDTO ", oidcResponseDTO);
            }


            if (KnProvConstants.SUBS_CLIENT_TYPE.POC_NNI_Alias_MDN.value() == subsClientType ||
                    KnProvConstants.SUBS_CLIENT_TYPE.POC_NNI_Group_MDN.value() == subsClientType) {
                provInfoUtil.createNNISubscriberProfile(mdnList,subsClientType,persisterTxn);
            }
            // APN changes ....
            Map<String, Integer> apnMdnMap = provXDMServerDAO.selectSubApn(mdnList, false, persisterTxn);
            Integer apnId = apnMdnMap.get(mdn);
            knLogger.info(methodName, " APNID ", apnId);

            //adding apn from base mdn.
            provXDMServerDAO.addSubApn(aliasMdnForAssign, apnId, persisterTxn);
            knLogger.debug(methodName, "Subscriber client type ", subsClientType);

            //Checking whether subscriber client type is applicable for roaming or not
            String paramValue = (String) paramNameValueMap.get(KnConstants.ROAM_NA_CLIENT_TYPES);
            List<String> roamNAClientTypes = new ArrayList<>();
            if (null != paramValue) {
                roamNAClientTypes = Arrays.asList(paramValue.split(","));
                knLogger.debug(methodName, "Roaming not applicable client type ", roamNAClientTypes);
            }
            ArrayList<Integer> roamingClusterIdList = new ArrayList<Integer>();
            //Integration Fix: retrieving the roaming clusterId for the clusterName received as request
            if (roamNAClientTypes.contains(String.valueOf(subsClientType))) {
                roamingClusterIdList.add(KnProvConstants.DEFAULT_ROAMING_CLUSTER_ID);
            } else {
                if (subsProvInputDTO.getRoamingTypes() != null && !subsProvInputDTO.getRoamingTypes().isEmpty()) {
                    HashMap<Integer, String> supportedRoamingClusterInfo = (HashMap<Integer, String>) provInfoUtil.retrieveSupportedRoamingList();
                    for (int clusterName : subsProvInputDTO.getRoamingTypes()) {
                        for (int roamingID : supportedRoamingClusterInfo.keySet()) {
                            if (String.valueOf(clusterName).equalsIgnoreCase(supportedRoamingClusterInfo.get(roamingID)) && !roamingClusterIdList.contains(roamingID)) {
                                roamingClusterIdList.add(roamingID);
                                break;
                            }
                        }
                    }
                } else {
                    roamingClusterIdList.add(KnProvConstants.DEFAULT_ROAMING_CLUSTER_ID);
                }
            }
            knLogger.debug(methodName, "adding an entry into Subscriber Roaming Profile");
            provXDMServerDAO.createSubscrRoamingProfile(aliasMdnForAssign, roamingClusterIdList, persisterTxn);

            if (corporateSubscriptionType == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
                // -->. creating entry into the XDM_CorpResourceListIndex Doc
                knLogger.debug(methodName, "Adding an entry into the Corp resource List Index Doc");
                xdmServerDAO.addMdnToCorpResourceListIndexDoc(aliasMdnForAssign, persisterTxn);

            }
            // -->. creating entry into the XDM_ContactListDocMap
            int contactListId = -1;
            knLogger.debug(methodName, "adding an entry into XDM Contact List Doc Map ");
            KnContactListPersistDTO contactListDocPersistDTO = new KnContactListPersistDTO();
            contactListDocPersistDTO.setMdn(aliasMdnForAssign);
            contactListId = xdmServerDAO.addMdnToXDMContactListDocMap(contactListDocPersistDTO, persisterTxn);

            // -->. creating entry into the XDM_ContactList
            knLogger.debug(methodName, "adding an entry into XDM Contact List ");
            xdmServerDAO.addMdnToXDMContactList(aliasMdnForAssign, contactListId, persisterTxn);

            // -->. Creating entry into the XDM_Directory
            knLogger.debug(methodName, "adding an entry into XDM Directory");
            xdmServerDAO.addMdnToXDMDirectory(aliasMdnForAssign, persisterTxn);

            // -->. Creating entry into the addon package
            if (!addonPackageIds.isEmpty()) {
                provXDMServerDAO.createSubAddOnPkgs(aliasMdnForAssign, new ArrayList<>(addonPackageIds.keySet()), persisterTxn);
            }

            // -->. Creating entry into the pocsubsaddlinfo
            KnSubsAddlInfoPersistDTO subsProfilePersistDTO = new KnSubsAddlInfoPersistDTO();
            subsProfilePersistDTO.setMdn(aliasMdnForAssign);
            subsProfilePersistDTO.setTimeSlotType(KnConstants.TIME_SLOT_TYPE);
            subsProfilePersistDTO.setTierPkgCode(tierPackageId);
            subsProfilePersistDTO.setDataPkgId(dataPkgId);
            subsProfilePersistDTO.setUserProfileName(subsProvInputDTO.getUserProfileName());
            provXDMServerDAO.createSubscrPkgAddlInfo(subsProfilePersistDTO, persisterTxn);
            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction ");
                persisterTxn.save();
            }

            if (subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.DESKTOP.value()) {
                successPegs.add(KnOMConstants.XDM_NUM_DESKTOP_CLIENTS_CREATED);
            } else if (subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.DISPATCH.value()) {
                successPegs.add(KnOMConstants.XDM_NUM_DISPATCH_CLIENTS_CREATED);
            } else if (subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.POCDONORRADIO.value()) {
                successPegs.add(KnOMConstants.XDM_NUM_LMR_INTEROP_CLIENTS_CREATED);
            } else if (subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.THIRDPARTYPOCCLIENT.value()) {
                successPegs.add(KnOMConstants.XDM_NUM_3RD_PARTY_POC_CLIENTS_CREATED);
            } else if (subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.MOBILE_CLIENT.value()) {
                successPegs.add(KnOMConstants.XDM_NUM_MOBILE_API_CLIENTS_CREATED);
            } else if (subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.THIRDPARTYDISPATCHERCLIENT.value()) {
                successPegs.add(KnOMConstants.XDM_NUM_3RDPARTY_DISPATCHER_CLIENTS_CREATED);
            } else if (subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.POC_NNI_Alias_MDN.value()){
                successPegs.add(KnOMConstants.XDM_NUM_NNI_ALIAS_SUBSCRIBERS_CREATED);
            } else if (subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.POC_NNI_Group_MDN.value()){
                successPegs.add(KnOMConstants.XDM_NUM_NNI_GROUP_SUBSCRIBERS_CREATED);
            } else if (subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.CROSS_CARRIER_PTT_CLIENT.value()) {
                successPegs.add(KnOMConstants.XDM_NUM_CROSS_CARRIER_PTT_CLIENTS_CREATED);
            } else if (subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value()){
                successPegs.add(KnOMConstants.XDM_NUM_PTTRADIO_CROSSCARRIER_CLIENTS_CREATED);
            } else if (subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value()){
                successPegs.add(KnOMConstants.XDM_NUM_PTTRADIO_HANDSET_CLIENTS_CREATED);
            } else if (subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value()) {
                successPegs.add(KnOMConstants.XDM_NUM_PTTRADIO_WIFIONLY_CLIENTS_CREATED);
            } else if (subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.SGMDNPATCH.value()) {
                successPegs.add(KnOMConstants.XDM_NUM_SG_MDN_PATCH_CLIENTS_CREATED);
            }
            respDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.SUCCESS.value());
            respDTO.setResponseMessage(KnProvConstants.CREATE_SUBSCRIBER_SUCCESS);
            if (corporateSubscriptionType == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
                respDTO.setCorpId(corpId);
            }
            respDTO.setSuccessPegs(successPegs);

            respDTO.setAliasMdnForAssign(aliasMdnForAssign);
            respDTO.setLastUpdateprofileTime(profileCreationTime);
            respDTO.setActiveFS2(activeFS2);

            knLogger.exit(methodName, "Create Subscriber operation-", respDTO);

            return respDTO;
        } catch (KnFeatureSetException ex) {
            knLogger.error(methodName, "Feature Set Exception occured :", ex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw new KnProvBOException(ex.getErrorCode(), ex.getMessage(), ex);
        } catch (KnDBConnectionException ex) {
            knLogger.error(methodName, "DAO DBConnection Exception occured :", ex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);

        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occurred : ", ex);
            knLogger.error(methodName, ex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            if (com.kodiak.xdms.server.common.resources.KnErrorCodes.DAO.ROW_ALREADY_EXISTS.equals(ex.getErrorCode())) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_ALREADY_EXISTS, "Subscriber already exists");
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception occurred :", e);
            knLogger.error(methodName, e);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while Create Subscriber");
            knLogger.error(methodName, e);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred while create Subscriber", e);
        }
    }

    @Override
    public KnOPUpdateSubsInfoDTO updateSubscriberUserProfileFS(KnIPSubsProvInfoDTO subsProfileInputDTO, KnPersisterTxn persisterTxn) throws KnProvBOException {

        String methodName = "updateSubscriberUserProfileFS(subsProfileInputDTO, KnPersisterTxn )";
        String xdmPttServerId;
        KnOPUpdateSubsInfoDTO responseDTO = new KnOPUpdateSubsInfoDTO();
        try{
            knLogger.debug(methodName,"Entry subsProfileInputDTO",subsProfileInputDTO);
            IProvXDMServerDAO xdmServerDAO = KnProvFactorySelector.getProvDAOFactory(KnFactorySelector.DB).createProvXDMServerDAO();

            xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
            KnUserProfileFSProvDTO ipUserProfileFS2 = subsProfileInputDTO.getUserProfileFSProvDTO();
            String userProfileFS2=null;
            if (ipUserProfileFS2!= null) {
                String defaultUpmFs = featureSetUtil.getDefFinalUserProfileFS();
                String finalUpmFsUsingDefUpmFs = provInfoUtil.getUpmFsBasedOnDefUpmFs(defaultUpmFs, ipUserProfileFS2);
				String userProfileFeatureSet = featureSetUtil
						.generateUserProfileFeatureSet(finalUpmFsUsingDefUpmFs);
				knLogger.debug(methodName, "userProfileFeatureSet-->UserProfileFS", userProfileFeatureSet);
				userProfileFS2=userProfileFeatureSet;
			}else{
                //if UPMFS null in request setting defaultUpmFs
                String defaultUpmFs = featureSetUtil.getDefFinalUserProfileFS();
                String userProfileFeatureSet = featureSetUtil.generateUserProfileFeatureSet(defaultUpmFs);
                knLogger.debug(methodName, "defaultUPMFS-->", userProfileFeatureSet);
                userProfileFS2=userProfileFeatureSet;
            }

            String profileMdn = subsProfileInputDTO.getMdn();
            knLogger.debug(methodName,"profileMdn ",KnGDPRTemplate.mdn(profileMdn),"userProfileFS2 ",userProfileFS2,"xdmPttServerId ",xdmPttServerId);
            KnOPSubsProfileInfoDTO subsProfile = provInfoUtil.retrieveSubscriberInfo(profileMdn, persisterTxn);
            IXDMServerDAO commonXDMServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
            KnOPSubsProfileInfoDTO existingSubsProfileDTO = provInfoUtil.retrieveSubscriberInfo(profileMdn, persisterTxn);
            knLogger.debug(methodName,"subsProfile ",subsProfile);

            String poCPttServerId = subsProfile.getPoCHome();
            String presencePttServerId = subsProfile.getPresenceHome();
            int clientPVMajorVersion = subsProfile.getClientPVmajorVer();
            String clientCapOverrideBitMask = featureSetUtil.getClientCapabilityBitMask(xdmPttServerId, clientPVMajorVersion);
            int corpSubscriptionType = subsProfile.getCorporateSubscriptionType();
            String clientFS2 = subsProfile.getClientFS2();
            String subsFS2=subsProfile.getSubsFS2();
            String opsFS2=subsProfile.getOpsFS2();
            String corpAdminFS2=subsProfile.getCorpAdminFS2();
            String xdmsFS2=subsProfile.getXdmsFS2();
            IProvXDMServerDAO provXdmServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            String extCorpId = provXdmServerDAO.retrieveExtCorporationId(subsProfile.getCorpId(), persisterTxn);
            KnOPCorpProfileInfoDTO corpProfileInfoDTO = provInfoUtil.retrieveCorpProfile(extCorpId, persisterTxn);
            String corpFS2 = corpProfileInfoDTO.getCorpFS2();

            String activeFS2=null;
            if (corpSubscriptionType == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
                activeFS2 = featureSetUtil.generateActiveFeatBitSet(poCPttServerId, presencePttServerId, xdmPttServerId,
                        clientFS2, subsFS2, corpFS2, opsFS2, corpAdminFS2, clientCapOverrideBitMask,xdmsFS2,userProfileFS2);
            } else {
                activeFS2 = featureSetUtil.generateActiveFeatBitSet(poCPttServerId, presencePttServerId, xdmPttServerId,
                        clientFS2, subsFS2, opsFS2, clientCapOverrideBitMask,xdmsFS2,userProfileFS2);
            }

            boolean veryLargeGroupBit = KnGeneralUtil.getFeatureBitValue(activeFS2, VERY_LARGE_GROUP);

            String userProfileId = subsProfile.getUserProfileId();
            List<String> userProfileIdList=new ArrayList<>();
            userProfileIdList.add(userProfileId);
            Map<String,List<Integer>> upmIdGroupMap=provInfoUtil.retriveUpmIdGroupMapByUserProfileId(userProfileIdList,persisterTxn);
            List<Integer> mcxGroups = upmIdGroupMap.get(userProfileId);
            knLogger.debug(methodName," mcxGroups :",mcxGroups
                    ," userProfileId :",userProfileId
                    ," upmIdGroupMap :",upmIdGroupMap
                    ," veryLargeGroupBit :",veryLargeGroupBit
                    ," profileMdn :",profileMdn);
            if(!veryLargeGroupBit&&mcxGroups!=null&&!mcxGroups.isEmpty()){
                //adding to mcxGroup clean up job on profile mdn 81 bit disable
                com.kodiak.xdms.server.common.util.KnGeneralUtil.veryLargeGroupBitChanged(subsProfile.getActiveFS2()
                        ,activeFS2
                        ,profileMdn
                        ,String.valueOf(subsProfile.getCorpId()),true);
            }

            KnSubsProfilePersistDTO subsProfilePersistDTO=new KnSubsProfilePersistDTO();
            subsProfilePersistDTO.setMdn(profileMdn);
            subsProfilePersistDTO.setActiveFS2(activeFS2);
            subsProfilePersistDTO.setUserProfileFS2(userProfileFS2);
            long lastProfileUpdateTime = Calendar.getInstance().getTimeInMillis();
            subsProfilePersistDTO.setLastProfileUpdateTime(lastProfileUpdateTime);
            xdmServerDAO.updateSubscrUserProfileFS(subsProfilePersistDTO, persisterTxn);

            //notification
            responseDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.SUCCESS.value());
            responseDTO.setEtag(lastProfileUpdateTime);
            responseDTO.setLastProfileUpdateTime(lastProfileUpdateTime);
            responseDTO.setClientPvMajorVersion(subsProfile.getClientPVmajorVer());
            if(!subsProfile.getActiveFS2().equals(activeFS2)){
                responseDTO.setActiveFSChanged(true);
                responseDTO.setActiveFS2(activeFS2);
            }

            int previousEtag = commonXDMServerDAO.getCurrentEtagForDirDoc(profileMdn, persisterTxn);
            commonXDMServerDAO.updateEtagForDirDoc(profileMdn, persisterTxn);
            String xcapRooturi = genInfoUtil.getXCAPRootURI(profileMdn, persisterTxn);
            String pocPttId = existingSubsProfileDTO.getPoCHome();
            String presencePttId = existingSubsProfileDTO.getPresenceHome();
            //populating the Subs Config document change DTO
            KnOPDocChgDTO docChgDTO = new KnOPDocChgDTO();
            docChgDTO.setDocumentChgType(KnConstants.DOC_CHANGE_TYPE.REPLACE.value());
            String subsConfigDocUri = provInfoUtil.generateSubsConfigSelUri(profileMdn);
            docChgDTO.setDocUri(subsConfigDocUri);
            docChgDTO.setNewEtag(String.valueOf(lastProfileUpdateTime));

            Collection<KnOPDocChgDTO> docChgList = new ArrayList<>();
            docChgList.add(docChgDTO);
            //populating Dir Document change DTO
            KnOPDirChgDTO dirChgDTO = new KnOPDirChgDTO();
            dirChgDTO.setXcapRootURI(xcapRooturi);
            dirChgDTO.setPocHome(pocPttId);
            dirChgDTO.setPresenceHome(presencePttId);
            dirChgDTO.setDocChgDTO(docChgList);
            String dirDocUri = genInfoUtil.generateDirDocUri(profileMdn);
            dirChgDTO.setDirUri(dirDocUri);
            dirChgDTO.setDirPrevEtag(String.valueOf(previousEtag));
            int newEtag = previousEtag + 1;
            dirChgDTO.setDirNewEtag(String.valueOf(newEtag));

            //setting the Document Change DTO the response
            responseDTO.setDirChgDTO(dirChgDTO);

        } catch (KnFeatureSetException ex) {
            knLogger.error(methodName, "Feature Set Exception occured :", ex);
            throw new KnProvBOException(ex.getErrorCode(), ex.getMessage(), ex);
        } catch (KnDBConnectionException ex) {
            knLogger.error(methodName, "DAO DBConnection Exception occured :", ex);
            throw new KnProvBOException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);
        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occurred : ", ex);
            if (com.kodiak.xdms.server.common.resources.KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_INFO_NOT_FOUND, "Subscriber does not exists");
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception occurred :", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while Update Subscriber");
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred while update Subscriber", e);
        }
        return responseDTO;
    }

    @Override
    public KnOPUpdateSubsInfoDTO updateSubscrTS(KnIPSubsProvInfoDTO subsProfileInputDTO, KnPersisterTxn persisterTxn) throws KnProvBOException {

        String methodName = "updateSubscriberUserProfileFS(subsProfileInputDTO, KnPersisterTxn )";
        String xdmPttServerId;
        try{
            IProvXDMServerDAO xdmServerDAO = KnProvFactorySelector.getProvDAOFactory(KnFactorySelector.DB).createProvXDMServerDAO();
            xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
            String profileMdn = subsProfileInputDTO.getMdn();
            knLogger.debug(methodName,"profileMdn ",KnGDPRTemplate.mdn(profileMdn),"xdmPttServerId ",xdmPttServerId);

            List<String> profileMdnList=new ArrayList<>();
            profileMdnList.add(profileMdn);
            xdmServerDAO.updateSubscrTS(profileMdnList, persisterTxn);

        } catch (KnDBConnectionException ex) {
            knLogger.error(methodName, "DAO DBConnection Exception occured :", ex);
            throw new KnProvBOException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);
        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occurred : ", ex);
            if (com.kodiak.xdms.server.common.resources.KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_INFO_NOT_FOUND, "Subscriber does not exists");
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception occurred :", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while Update Subscriber");
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred while update Subscriber", e);
        }
        return null;
    }

    @Override
    public KnOPCreateSubsInfoDTO createDevice(KnXDMDeviceProvInfoDTO deviceProfileInfoInputDTO, KnPersisterTxn persisterTxn)
            throws KnDAOException, KnProvException {
        String methodName = "createDevice(KnXDMDeviceProvInfoDTO, KnPersisterTxn)";
        KnOPCreateSubsInfoDTO respDTO = new KnOPCreateSubsInfoDTO();
        knLogger.info(methodName, "ENTRY: Input DTO - ", deviceProfileInfoInputDTO, " Txn - ", persisterTxn);
        String xdmPttServerId;
        try {

            try {
                xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
            } catch (KnBOException e) {
                knLogger.error(methodName, "failed to retrieve xdm Ptt Sever Id");
                knLogger.error(methodName, e);

                throw new KnProvBOException(KnErrorCodes.BOEntity.XDMS_PTT_ID_NOT_FOUND, "Failed to retrieve XDM PTT Server ID", e);
            }

            //Validating the  Corporate profile

            if (KnGeneralProfileUtil.getCorporateDetails(deviceProfileInfoInputDTO.getAccountId()) == null) {
                knLogger.error(methodName, "Invalid corp ext Id : ",
                        deviceProfileInfoInputDTO.getAccountId());
                throw new KnXDMServerException(
                        com.kodiak.xdms.server.common.resources.KnErrorCodes.BOEntity.INVALID_CORPORATE_PROFILE,
                        "Invalid Corp profile passed");
            }

            IXDMServerDAO xdmDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);

            KnXDMDeviceProvDTO deviceProfile = xdmDAO.selectDeviceProfile(deviceProfileInfoInputDTO.getDeviceId().trim(), persisterTxn);

            if (deviceProfile != null) {
                knLogger.error(methodName, "device is already present");
                throw new KnProvBOException(KnErrorCodes.BOEntity.DEVICE_ALREADY_PRESENT,
                        "Device is already present");
            }

            if (null != deviceProfileInfoInputDTO.getDeviceSubscrMdn() ) {
                Integer deviceCount = xdmDAO.getDeviceProfile(deviceProfileInfoInputDTO.getDeviceSubscrMdn(), null, persisterTxn);
                if (null != deviceCount && deviceCount > 0) {
                    knLogger.error(methodName, "SubscriberMdn already has a device mapped");
                    throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_ALREADY_HAS_DEVICE,
                            "SubscriberMdn already has a device mapped");
                }

                com.kodiak.xdms.server.common.dto.persistdat.KnSubsProfilePersistDTO subscriberProfile = xdmDAO.getSubscriberProfile(deviceProfileInfoInputDTO.getDeviceSubscrMdn(), KnConstants.FALSE, persisterTxn);
                if (null != deviceProfileInfoInputDTO.getAccountId() && null != subscriberProfile.getAccountId()
                        && !deviceProfileInfoInputDTO.getAccountId().trim().equalsIgnoreCase(subscriberProfile.getAccountId().trim())) {
                    knLogger.error(methodName, "SubscriberMdn and Device are not part of same corporate");
                    throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_AND_DEVICE_NOT_PART_OF_SAME_CORPORATE,
                            "SubscriberMdn and Device are not part of same corporate");
                }
            }

            KnDeviceInfoPersistDTO deviceInfoPersistDTO = new KnDeviceInfoPersistDTO();
            Long profileCreationTime = System.currentTimeMillis();
            String kuid = KnKUIDGenerator.getInstance().getKodiakUserID(KnKUIDConstants.COUNTRYCODE);
            deviceInfoPersistDTO.setDeviceId(kuid);
            deviceInfoPersistDTO.setDeviceStatus(KnConstants.DEVICE_STATUS_OP.ACTIVATED.value());
            deviceInfoPersistDTO.setDeviceActTimeStamp(profileCreationTime);
            deviceInfoPersistDTO.setDeviceLastUsed(profileCreationTime);

            KnXDMSServiceConfigDTO xdmsServiceConfigDTO = genInfoUtil.retrieveXDMSServiceConfig(xdmPttServerId, persisterTxn);
            String realm = xdmsServiceConfigDTO.getAuthRealm();
            String deviceDigestPassword =null;
            if (deviceProfileInfoInputDTO.getDevicePassword() != null && !deviceProfileInfoInputDTO.getDevicePassword().isEmpty()) {
                deviceDigestPassword = provInfoUtil.generateHA1(kuid, realm, deviceProfileInfoInputDTO.getDevicePassword().trim());
            } else {
                String randomPwd = pwdUtil.generatePassword(com.kodiak.common.resources.KnConstants.DEFAULT_DEVICE_PASSWORD_LENGTH);
                deviceDigestPassword = provInfoUtil.generateHA1(kuid, realm, randomPwd);
            }
            deviceInfoPersistDTO.setDeviceDigestPassword(deviceDigestPassword);

            deviceInfoPersistDTO.setDeviceType(deviceProfileInfoInputDTO.getDeviceType());
            String deviceName = null;
            if (deviceProfileInfoInputDTO.getDeviceName() != null) {
                deviceName = deviceProfileInfoInputDTO.getDeviceName().trim();
            }
            deviceInfoPersistDTO.setDeviceName(deviceName);

            String deviceIMPI = null;
            if ((deviceProfileInfoInputDTO.getDeviceIMPI() == null
                    || deviceProfileInfoInputDTO.getDeviceIMPI().isEmpty())
                    && deviceProfileInfoInputDTO.getDeviceType() != KnConstants.DEVICE_TYPE.DEFAULT_DEVICE.Value()) {
                deviceIMPI = KnConstants.TEL_URI_TEMPLATE + kuid;
            } else {
                deviceIMPI = deviceProfileInfoInputDTO.getDeviceIMPI().trim();
            }

            deviceInfoPersistDTO.setDeviceIMPI(deviceIMPI);

            Integer deviceshared = null;
            if (deviceProfileInfoInputDTO.getDeviceShared() == null && deviceProfileInfoInputDTO.getDeviceType() == KnConstants.DEVICE_TYPE.RADIO_NEXT_DEVICE.Value()) {
                deviceshared = 1;
            } else {
                deviceshared = deviceProfileInfoInputDTO.getDeviceShared();
            }

            deviceInfoPersistDTO.setDeviceshared(deviceshared);
            String deviceClientId = null;
            if (deviceProfileInfoInputDTO.getDeviceClientId() != null) {
                deviceClientId = deviceProfileInfoInputDTO.getDeviceClientId().trim();
            }
            deviceInfoPersistDTO.setDeviceClientId(deviceClientId);
            String deviceId = null;
            if (deviceProfileInfoInputDTO.getDeviceId() != null) {
                deviceId = deviceProfileInfoInputDTO.getDeviceId().trim();
            }
            deviceInfoPersistDTO.setReqDeviceId(deviceId);
            int corpId=Integer.valueOf(KnGeneralProfileUtil.getCorporateDetails(deviceProfileInfoInputDTO.getAccountId()).getCorpId());
            deviceInfoPersistDTO.setCorpId(corpId);
            deviceInfoPersistDTO.setDeviceCreatedAs(KnConstants.DEVICECREATEDAS);

            String deviceSubscriberMdn = null;
            if (null != deviceProfileInfoInputDTO.getDeviceSubscrMdn()) {
                deviceSubscriberMdn = deviceProfileInfoInputDTO.getDeviceSubscrMdn().trim();
            }
            deviceInfoPersistDTO.setDeviceSubscriberMdn(deviceSubscriberMdn);

            knLogger.debug(methodName, "device info Persist DTO - ", deviceInfoPersistDTO);
            KnDeviceAddlInfoPersistDTO deviceAddlInfoPersistDTO = getKnDeviceAddlInfoPersistDTO(deviceProfileInfoInputDTO, kuid);

            xdmDAO.createDeviceInfo(deviceInfoPersistDTO, persisterTxn);
            xdmDAO.insertAdditionalDeviceInfo(deviceAddlInfoPersistDTO, persisterTxn);

            KnDeviceImpiInfoPersistDTO deviceImpiInfo = new KnDeviceImpiInfoPersistDTO();
            deviceImpiInfo.setDeviceImpi(deviceIMPI);
            if (deviceProfileInfoInputDTO.getDeviceIMPU() != null) {
                deviceImpiInfo.setDeviceImpu(deviceProfileInfoInputDTO.getDeviceIMPU().trim());
            } else {
                deviceImpiInfo.setDeviceImpu(deviceIMPI);
            }
            knLogger.debug(methodName, "deviceIMPI info Persist DTO - ", deviceImpiInfo);
            if(!(deviceImpiInfo.getDeviceImpi()==null && deviceImpiInfo.getDeviceImpu()== null))
                xdmDAO.createDeviceImpiInfo(deviceImpiInfo, persisterTxn);
            respDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.SUCCESS.value());
            respDTO.setResponseMessage(KnProvConstants.CREATE_DEVICE_SUCCESS);
            respDTO.setInternalDeviceId(kuid);
            respDTO.setCorpId(corpId);
            respDTO.setLastUpdateprofileTime(profileCreationTime);
            knLogger.exit(methodName, "Create device operation-", respDTO);

            return respDTO;
        } catch (KnDBConnectionException ex) {
            knLogger.error(methodName, "DAO DBConnection Exception occured :", ex);
            throw new KnProvBOException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);

        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception occurred :", e);
            knLogger.error(methodName, e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while Create device");
            knLogger.error(methodName, e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred while Create device", e);
        }
    }

	@Override
	public KnXDMDeviceProvDTO getDeviceInfo(String deviceId, KnPersisterTxn persisterTxn)
			throws KnDAOException, KnProvException {
		String methodName = "getDeviceInfo(String, KnPersisterTxn)";
		knLogger.info(methodName, "ENTRY: getDeviceInfo with DTO ", deviceId);
		KnXDMDeviceProvDTO deviceProfileRespDTO = new KnXDMDeviceProvDTO();
		try {

			try {
				xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
			} catch (KnBOException e) {
				knLogger.error(methodName, "failed to retrieve xdm Ptt Sever Id");
				knLogger.error(methodName, e);
				throw new KnProvBOException(KnErrorCodes.BOEntity.XDMS_PTT_ID_NOT_FOUND,
						"Failed to retrieve XDM PTT Server ID", e);
			}

			IXDMServerDAO xdmDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB)
					.createXDMServerDAO(xdmPttServerId);
			deviceProfileRespDTO = xdmDAO.selectDeviceProfile(deviceId.trim(), persisterTxn);

            if (null != deviceProfileRespDTO) {
                if (deviceProfileRespDTO.getDeviceAddlInfo() != null && deviceProfileRespDTO.getDeviceAddlInfo().getDeviceInfo() != null) {
                    String decodedHexDeviceInfo = KnGeneralUtil.decodeHexDeviceInfo(deviceProfileRespDTO.getDeviceAddlInfo().
                            getDeviceInfo().trim(), MAX_ATTEMPT_TO_DECODE);
                    deviceProfileRespDTO.getDeviceAddlInfo().setDeviceInfo(decodedHexDeviceInfo);
                }
                deviceProfileRespDTO.setAccountId(KnGeneralProfileUtil.getCorporateDetails(deviceProfileRespDTO.getCorpId()).getExtCorpId().trim());
                String deviceIMPU = xdmDAO.getDeviceImpuInfo(deviceProfileRespDTO.getDeviceIMPI(), persisterTxn);
                deviceProfileRespDTO.setDeviceIMPU(deviceIMPU);
            } else {
                throw new KnProvBOException(KnErrorCodes.BOEntity.DEVICE_PROFILE_NOT_FOUND, "device profile not found");
            }

			knLogger.exit(methodName, "get device operation-", deviceProfileRespDTO);
		} catch (KnDBConnectionException ex) {
			knLogger.error(methodName, "DAO DBConnection Exception occured :" + ex);
			throw new KnProvBOException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);

		} catch (KnDAOException ex) {
			knLogger.error(methodName, "DAO Exception occurred : " + ex);
			throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
		} catch (KnProvBOException e) {
			knLogger.error(methodName, "BO Exception occurred :" + e.getErrorCode());
			throw e;
		} catch (Exception e) {
			knLogger.error(methodName, "Exception occurred while get device details" + e);

			throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
					"Exception occurred while get device details", e);
		}
        knLogger.info(methodName, "EXIT:  getDeviceInfo - ");

		return deviceProfileRespDTO;
	}

	@Override
	public void deleteDeviceInfo(KnXDMDeviceProvDTO deviceId, KnPersisterTxn persisterTxn)
			throws KnDAOException, KnProvException {
		String methodName = "deleteDeviceInfo(String, KnPersisterTxn)";
		knLogger.info(methodName, "ENTRY: delete device with device  - ", deviceId);
		try {

			try {
				xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
			} catch (KnBOException e) {
				knLogger.error(methodName, "failed to retrieve xdm Ptt Sever Id");
				knLogger.error(methodName, e);
				throw new KnProvBOException(KnErrorCodes.BOEntity.XDMS_PTT_ID_NOT_FOUND,
						"Failed to retrieve XDM PTT Server ID", e);
			}
			IXDMServerDAO xdmDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB)
					.createXDMServerDAO(xdmPttServerId);
			KnDeviceInfoPersistDTO deviceInfo = new KnDeviceInfoPersistDTO();
			deviceInfo.setDeviceId(deviceId.getDeviceId());
			xdmDAO.deleteDeviceImpiInfo(deviceInfo, persisterTxn);
			xdmDAO.deleteDeviceInfo(deviceInfo, persisterTxn);
            xdmDAO.deleteDeviceAddlInfo(deviceInfo,persisterTxn);
			knLogger.info(methodName, "EXIT: delete device operation  sucessfully");
		} catch (KnDBConnectionException ex) {
			knLogger.error(methodName, "DAO DBConnection Exception occured :", ex);
			throw new KnProvBOException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);
		} catch (KnDAOException ex) {
			knLogger.error(methodName, "DAO Exception occurred : ", ex);
			throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
		} catch (KnProvBOException e) {
			knLogger.error(methodName, "BO Exception occurred :", e);
			throw e;
		} catch (Exception e) {
			knLogger.error(methodName, "Exception occurred while delete Subscriber", e);
			throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
					"Exception occurred while delete Subscriber", e);
		}

	}

	@Override
    public KnOPUpdateSubsInfoDTO modifyDevice(KnXDMDeviceProvInfoDTO deviceProfileInfoInputDTO, KnPersisterTxn persisterTxn)
            throws KnDAOException, KnProvException {
        String methodName = "modifyDevice(KnXDMDeviceProvInfoDTO, KnPersisterTxn)";
        KnOPUpdateSubsInfoDTO respDTO = new KnOPUpdateSubsInfoDTO();
        knLogger.info(methodName, "ENTRY: Input DTO - ", deviceProfileInfoInputDTO, " Txn - ", persisterTxn);
        String xdmPttServerId;
        try {

            try {
                xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
            } catch (KnBOException e) {
                knLogger.error(methodName, "failed to retrieve xdm Ptt Sever Id");
                knLogger.error(methodName, e);

                throw new KnProvBOException(KnErrorCodes.BOEntity.XDMS_PTT_ID_NOT_FOUND, "Failed to retrieve XDM PTT Server ID", e);
            }

            IXDMServerDAO xdmDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);

            KnXDMDeviceProvDTO deviceProfile = xdmDAO.selectDeviceProfileByDeviceId(deviceProfileInfoInputDTO.getDeviceId().trim(), persisterTxn);

            //Validating the  Corporate profile
            if (null == KnGeneralProfileUtil.getCorporateDetails(deviceProfile.getCorpId())) {
                knLogger.error(methodName, "Invalid corp ext Id : ",
                        deviceProfile.getCorpId());
                throw new KnXDMServerException(
                        com.kodiak.xdms.server.common.resources.KnErrorCodes.BOEntity.INVALID_CORPORATE_PROFILE,
                        "Invalid Corp profile passed");
            }

            if (null == deviceProfile) {
                knLogger.error(methodName, "Received intDeviceId is Invalid or not present");
                throw new KnProvBOException(KnErrorCodes.BOEntity.DEVICE_NOT_PRESENT,
                        "Received intDeviceId is Invalid or not present");
            }

            Integer deviceProfileCount = xdmDAO.selectOtherDeviceProfile(deviceProfileInfoInputDTO.getDeviceId(), deviceProfileInfoInputDTO.getReqDeviceId().trim(), false, persisterTxn);
            if (null != deviceProfileCount && deviceProfileCount > 1) {
                knLogger.error(methodName, "Received deviceId is already tagged to intDeviceId");
                throw new KnProvBOException(KnErrorCodes.BOEntity.DEVICE_ALREADY_TAGGED,
                        "Received deviceId is already tagged to intDeviceId");
            }

            if (null != deviceProfileInfoInputDTO.getDeviceSubscrMdn()) {
                Integer deviceCount = xdmDAO.getDeviceProfile(deviceProfileInfoInputDTO.getDeviceSubscrMdn(), deviceProfileInfoInputDTO.getDeviceId(), persisterTxn);
                if (null != deviceCount && deviceCount > 0) {
                    knLogger.error(methodName, "SubscriberMdn already has a device mapped");
                    throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_ALREADY_HAS_DEVICE,
                            "SubscriberMdn already has a device mapped");
                }

                com.kodiak.xdms.server.common.dto.persistdat.KnSubsProfilePersistDTO subscriberProfile = xdmDAO.getSubscriberProfile(deviceProfileInfoInputDTO.getDeviceSubscrMdn(), KnConstants.FALSE, persisterTxn);
                if (null != deviceProfile.getAccountId() && null != subscriberProfile.getAccountId()
                        && !deviceProfile.getAccountId().trim().equalsIgnoreCase(subscriberProfile.getAccountId().trim())) {
                    knLogger.error(methodName, "SubscriberMdn and Device are not part of same corporate");
                    throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_AND_DEVICE_NOT_PART_OF_SAME_CORPORATE,
                            "SubscriberMdn and Device are not part of same corporate");
                }
            }

            KnDeviceInfoPersistDTO deviceInfoPersistDTO = new KnDeviceInfoPersistDTO();
            Long profileCreationTime = System.currentTimeMillis();
            deviceInfoPersistDTO.setDeviceLastUsed(profileCreationTime);
            deviceInfoPersistDTO.setDeviceId(deviceProfileInfoInputDTO.getDeviceId());
            deviceInfoPersistDTO.setDeviceType(deviceProfileInfoInputDTO.getDeviceType());

            String reqDeviceId = null;
            if (null != deviceProfileInfoInputDTO.getReqDeviceId()) {
                reqDeviceId = deviceProfileInfoInputDTO.getReqDeviceId().trim();
            }
            deviceInfoPersistDTO.setReqDeviceId(reqDeviceId);

            String deviceSubscriberMdn = null;
            if (null != deviceProfileInfoInputDTO.getDeviceSubscrMdn()) {
                deviceSubscriberMdn = deviceProfileInfoInputDTO.getDeviceSubscrMdn().trim();
            } else {
                deviceSubscriberMdn = deviceProfile.getDeviceSubscrMdn();
            }
            deviceInfoPersistDTO.setDeviceSubscriberMdn(deviceSubscriberMdn);

            knLogger.debug(methodName, "device info Persist DTO - ", deviceInfoPersistDTO);
            KnDeviceAddlInfoPersistDTO deviceAddlInfoPersistDTO = getKnDeviceAddlInfoPersistDTO(deviceProfileInfoInputDTO, deviceProfile.getDeviceId());

            KnXDMDeviceProvDTO deviceProfileInfo = xdmDAO.selectDeviceProfile(deviceProfile.getReqDeviceId(), persisterTxn);
            if (null != deviceProfileInfo) {
                xdmDAO.modifyDeviceInfo(deviceInfoPersistDTO, persisterTxn);
                xdmDAO.updateAdditionalDeviceInfo(deviceAddlInfoPersistDTO, deviceProfileInfo, persisterTxn);
            } else {
                knLogger.error(methodName, "Received intDeviceId is Invalid or not present");
                throw new KnProvBOException(KnErrorCodes.BOEntity.DEVICE_NOT_PRESENT,
                        "Received intDeviceId is Invalid or not present");
            }
            respDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.SUCCESS.value());
            respDTO.setResponseMessage(KnProvConstants.MODIFY_DEVICE_SUCCESS);
            respDTO.setCorpId(deviceProfile.getCorpId());
            respDTO.setLastProfileUpdateTime(profileCreationTime);
            knLogger.exit(methodName, "Modify device operation-", respDTO);

            return respDTO;
        } catch (KnDBConnectionException ex) {
            knLogger.error(methodName, "DAO DBConnection Exception occurred :", ex);
            throw new KnProvBOException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);

        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception occurred :", e);
            knLogger.error(methodName, e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while Modify device");
            knLogger.error(methodName, e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred while Modify device", e);
        }
    }

	@Override
	public void loginNotifyEvent(KnXDMLoginNotifyEventReqDTO loginNotifyEventReqDTO, KnPersisterTxn persisterTxn)
			throws KnProvException, KnFWException {
		String methodName = "loginNotifyEvent(KnXDMLoginNotifyEventReqDTO, KnPersisterTxn)";
		knLogger.info(methodName, "ENTRY: login Notify Event");
		KnOPProvDTO responseDTO;
		try {
			KnSubsProfilePersistDTO subsProfilePersistDTO = new KnSubsProfilePersistDTO();
            String id = loginNotifyEventReqDTO.getId();
            String profileMdn = KnGeneralUtil.getSplitString(id);
            subsProfilePersistDTO.setProfileMdn(profileMdn);
            subsProfilePersistDTO.setMdn(loginNotifyEventReqDTO.getMcServiceBaseMDN().trim());
			// SUBSCRIBERFS2 76 UserProfileMgmt bit is enabled
			KnOPSubsProfileInfoDTO subscProfile = provInfoUtil
					.retrieveSubscriberInfo(loginNotifyEventReqDTO.getMcServiceBaseMDN().trim(), persisterTxn);
			String activeFs2 = subscProfile.getActiveFS2();
			boolean upmBit = KnGeneralUtil.getFeatureBitValue(activeFs2, USER_PROFILE_MGMT_BIT);
			knLogger.debug("upmBit ", upmBit, "activeFs2 ", activeFs2);
			responseDTO = new KnOPProvDTO();

			if (upmBit) {
				IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB)
						.createProvXDMServerDAO();
				if (loginNotifyEventReqDTO.getOnlineStatus().equalsIgnoreCase("ON")) {
					Map<String, String> userProfileMdnMap = provXDMServerDAO
							.getUserProfileMdnMap(loginNotifyEventReqDTO.getMcServiceBaseMDN().trim(), persisterTxn);
					if (userProfileMdnMap.isEmpty()) {
                        userProfileMdnMap=provXDMServerDAO.createUserProfileMdnMap(subsProfilePersistDTO, persisterTxn);
						knLogger.debug(methodName, "created User Profile Mdn Map for Mdn ",
								KnGDPRTemplate.mdn(loginNotifyEventReqDTO.getMcServiceBaseMDN()));
					}

                        if ((userProfileMdnMap.get(loginNotifyEventReqDTO.getMcServiceBaseMDN().trim())!=null) && (loginNotifyEventReqDTO.getDeviceMdn().trim() != userProfileMdnMap
                                .get(loginNotifyEventReqDTO.getMcServiceBaseMDN().trim()).trim())) {
                            provXDMServerDAO.updateUserProfileMdnMap(subsProfilePersistDTO, persisterTxn);
                        } else {
                            knLogger.info(" user Profile Mdn Map already exist for MDN--> ",
                                    KnGDPRTemplate.mdn(loginNotifyEventReqDTO.getMcServiceBaseMDN()), "userProfileMdnMap ", KnGDPRTemplate.mdnMap(userProfileMdnMap));
                        }

				} else if (loginNotifyEventReqDTO.getOnlineStatus().equalsIgnoreCase("OFF")) {
					provXDMServerDAO.deleteUserProfileMdnMap(subsProfilePersistDTO.getMdn(), persisterTxn);
					knLogger.debug(methodName, "Deleted User Profile Mdn Map for Mdn ",
							KnGDPRTemplate.mdn(loginNotifyEventReqDTO.getMcServiceBaseMDN()));
				}
			} else {
				knLogger.info(" UPM bit is diabaled--> ", upmBit, "activeFs2 ", activeFs2);
			}
			knLogger.debug(methodName, "Response DTO - ", responseDTO);
			knLogger.info(methodName, "EXIT: login Notify Event operation ");

		} catch (KnDBConnectionException ex) {
			knLogger.error(methodName, "DAO DBConnection Exception occured :", ex);
			throw new KnProvBOException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);

		} catch (KnDAOException ex) {
			knLogger.error(methodName, "DAO Exception occurred : ", ex);
			knLogger.error(methodName, ex);

			throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
		} catch (KnProvBOException e) {
			knLogger.error(methodName, "BO Exception occurred :", e);
			knLogger.error(methodName, e);
			throw e;
		} catch (Exception e) {
			knLogger.error(methodName, "Exception occurred while login Notify Event");
			knLogger.error(methodName, e);
			throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
					"Exception occurred while login Notify Event" + e);
		}
	}

    public KnOPMCPTTPermissionDTO getAuthorizedUserList(KnIPMCPTTPermissionDTO tuInfo, KnPersisterTxn persisterTxn)
            throws KnProvBOException, KnValidationException {
        String methodName = "controller.getAuthorizedUserList()";

        KnOPMCPTTPermissionDTO responseDTO=new KnOPMCPTTPermissionDTO();
        try{
            IProvXDMServerDAO provXdmServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            //String mdn=tuInfo.getMdn();
            String targetUser=tuInfo.getTargetUser();
            knLogger.debug(methodName,"targetUser :",KnGDPRTemplate.mdn(targetUser));
            KnSubsProfilePersistDTO subsProfilePersistDTO=new KnSubsProfilePersistDTO();
            KnOPSubsProfileInfoDTO targetMdnInfo = provInfoUtil.retrieveSubscriberInfo(targetUser, persisterTxn);
            subsProfilePersistDTO.setInputDTO(tuInfo);

            if(targetMdnInfo==null){
                throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_INFO_NOT_FOUND,
                        "Subscriber Profile does not exist");
            }
            
            String mcpttId_Input = tuInfo.getMcptt_id();
            String mcpttId_DB = targetMdnInfo.getMcpttId();
            knLogger.debug(methodName,"mcpttId_Input",KnGDPRTemplate.mcpttId(mcpttId_Input),"Tragetuser :",KnGDPRTemplate.mdn(targetUser),"mcpttId_db"
            		 ,KnGDPRTemplate.mcpttId(mcpttId_DB));

            if (mcpttId_Input != null && mcpttId_DB != null)
			{
				if (targetMdnInfo.getMcpttCompliance() == 0) {
					if (!mcpttId_Input.isEmpty() && !mcpttId_DB.isEmpty()
							&& !mcpttId_Input.equals(mcpttId_DB.substring(5).trim())) {
						 knLogger.debug(methodName, "MCPTTD is not matched with the token");
						throw new KnProvBOException(KnErrorCodes.Validator.INVALID_TOKEN_MCPTTID,
								"MCPTTD is not matched with the token");
					}
				} else if (targetMdnInfo.getMcpttCompliance() == 1) {
					if (!mcpttId_Input.isEmpty() && !mcpttId_DB.isEmpty() && !mcpttId_Input.equals(mcpttId_DB.trim())) {
						 knLogger.debug(methodName, "MCPTTD is not matched with the token");
						throw new KnProvBOException(KnErrorCodes.Validator.INVALID_TOKEN_MCPTTID,
								"MCPTTD is not matched with the token");
					}
				}
			}

            List<String> onlineAUList =new ArrayList<>();
            List<KnMCPTTPermInfoDTO> auMcpttPermInfoDTOS = provXdmServerDAO.getMCPTTPermInfoForTargetMdn(targetUser, persisterTxn);
            Set<String> auSet = auMcpttPermInfoDTOS.stream().map(KnMCPTTPermInfoDTO::getAuthorizedMdn).collect(Collectors.toSet());
            Map<String,String> onlineAUMap = KnGeneralCacheUtil.getInstance().getOnlineSubcribersListByMdn(new ArrayList<>(auSet));
            //getting mdn where base mdn is null in case of CRI.
            List<String> nullBaseMDN = onlineAUMap.entrySet().stream().filter(q->q.getValue()==null).map(q->q.getKey()).collect(Collectors.toList());
            if(!nullBaseMDN.isEmpty()){
                onlineAUList.addAll(provXdmServerDAO.getBaseMdnByProfileMdn(nullBaseMDN, persisterTxn));
            }
            onlineAUList.addAll(onlineAUMap.entrySet().stream().filter(q->q.getValue()!=null).map(q->q.getValue()).collect(Collectors.toList()));
            //removing self Mdn from the list,As profile mdn of the same base can be AU to Tu basemdn
            List<String> targetUserList=new ArrayList<>();
            targetUserList.add(targetUser);
            List<String> selfMdn = provXdmServerDAO.getBaseMdnByProfileMdn(targetUserList, persisterTxn);
            onlineAUList.removeAll(selfMdn);

            responseDTO.setAutorizedMdnList(onlineAUList);

        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occurred : ", ex);
            knLogger.error(methodName, ex);
            if (com.kodiak.xdms.server.common.resources.KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_INFO_NOT_FOUND, "Subscriber Profile does not exist");
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception occurred :", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred :",e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred:", e);
        }
        knLogger.info(methodName, "EXIT: responseDTO - ");
        return responseDTO;
    }

    /**
     *
     * @param subscriberDTO
     * @param persisterTxn
     * @return
     * @throws KnProvBOException
     */
    @Override
    public KnOPSubsProfileInfoDTO getUserprofileidsByProfileMdns(KnIPSubscriberInfoDTO subscriberDTO, KnPersisterTxn persisterTxn) throws KnProvBOException {
        String methodName = "getUserprofileidsByProfileMdns(KnIPSubscriberInfoDTO, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY: Input DTO ", subscriberDTO);
        KnOPSubsProfileInfoDTO subsProfileRespDTO = null;
        try {
            xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
            if (subscriberDTO.getMdnList() != null && !subscriberDTO.getMdnList().isEmpty()) {
                subsProfileRespDTO = provInfoUtil.selectUserProfileIdsByProfileMdns(subscriberDTO.getMdnList(), persisterTxn);
            }
        } catch (KnBOException e) {
            knLogger.error(methodName, "failed to retrieve xdm Ptt Sever Id", e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.XDMS_PTT_ID_NOT_FOUND, "Failed to retrieve XDM PTT Server ID", e);
        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception occurred :" + e.getErrorCode());
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while get Subscriber details" + e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred while get Subscriber details", e);
        }
        knLogger.debug(methodName, "EXIT:  getUserprofileidsByProfileMdns - ", subsProfileRespDTO);
        return subsProfileRespDTO;

    }

    @Override
    public KnXDMSubsProfileRespDTO getSubscrClientSettings(KnIPSubsProvInfoDTO subClientSettingsDTO, KnPersisterTxn persisterTxn) throws KnProvBOException, KnValidationException {
        String methodName = "getSubscrClientSettings(subClientSettingsDTO, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: InputDTO ", subClientSettingsDTO);
        KnXDMSubsProfileRespDTO respDTO = null;
        try {
            xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
            if (subClientSettingsDTO.getMdn() != null && !subClientSettingsDTO.getMdn().isEmpty()) {
                respDTO = provInfoUtil.getSubscrClientSettings(subClientSettingsDTO.getMdn(), true, persisterTxn);
            }
        }catch (KnBOException e) {
            knLogger.error(methodName, "failed to retrieve xdm Ptt Sever Id", e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.XDMS_PTT_ID_NOT_FOUND, "Failed to retrieve XDM PTT Server ID", e);
        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception occurred :" + e.getErrorCode());
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while get Subscriber details" + e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred while get Subscriber details", e);
        }
        knLogger.debug(methodName, "EXIT:  getSubscrClientSettings - ", respDTO);
        return respDTO;
    }

    /**
     * method to update the Subscriber Profile.
     * --> populate the Subs profile persist DTO
     * --> Validate the input data
     * --> send the data to the DAO layer for updation of profile
     * --> on success populate the document DTO (for notification)
     * --> populate the response DTO.
     *
     * @param subsProvInputDTO KnIPSubsProvInfoDTO
     * @param persisterTxn     KnPersisterTxn
     * @return KnOPProvDTO response DTO
     * @throws KnProvBOException     BO Entity Exception
     * @throws KnValidationException Validation level exception
     */
    public KnOPUpdateSubsInfoDTO setSubscrClientSettings(KnIPSubsProvInfoDTO subsProvInputDTO, KnPersisterTxn persisterTxn) throws KnProvBOException {
        String methodName = "setSubscrClientSettings(KnIPSubsProvInfoDTO, KnPersisterTxn)";
        boolean ownedTxn = false;
        KnOPUpdateSubsInfoDTO responseDTO = new KnOPUpdateSubsInfoDTO();
        KnSubsProfilePersistDTO subsProfilePersistDTO;
        Integer isprovFSEnabled= PROVFS_ENABLE.DISABLED.value();
        Map<String, String> activeFSMap = new HashMap<>();
        Collection<KnSubsProfileDTO> subsSpecificDetails = null;
        Map<String, Integer> clientTypeMap = new HashMap<>();
        //same values of provFS1 and provFS1BitMask is used for calculation final subsFS after oring with bitmask
        knLogger.info(methodName, "ENTRY: setSubscrClientSettings with DTO - ", subsProvInputDTO, ", Txn - ", persisterTxn
        );

        Collection<Integer> successPegs = new ArrayList<Integer>();
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            try {
                xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
            } catch (KnBOException e) {
                knLogger.error(methodName, "failed to retrieve xdm Ptt Sever Id");
                knLogger.error(methodName, e);
                if (ownedTxn) {
                    rollback(persisterTxn);
                }
                throw new KnProvBOException(KnErrorCodes.BOEntity.XDMS_PTT_ID_NOT_FOUND, "Failed to retrieve XDM PTT Server ID", e);
            }

            //KnSubsProfilePersistDTO validatePersistDTO = new KnSubsProfilePersistDTO();
            KnOPSubsProfileInfoDTO profileResponse;
            profileResponse=provInfoUtil.selectSubsProfileInfo(subsProvInputDTO.getMdnList(),false, persisterTxn);

            IProvXDMServerDAO xdmServerDAO = KnProvFactorySelector.getProvDAOFactory(KnFactorySelector.DB).createProvXDMServerDAO();
            subsSpecificDetails = xdmServerDAO.fetchSubsSpecificDetailsForBulkMdns(subsProvInputDTO.getMdnList(), false, persisterTxn);
            if(subsSpecificDetails != null && !subsSpecificDetails.isEmpty()){
                subsSpecificDetails.forEach(subsDTO -> {
                    activeFSMap.put(subsDTO.getMdn(), subsDTO.getActiveFS2());
                    clientTypeMap.put(subsDTO.getMdn(), subsDTO.getSubsClientType());
                });
            }
            knLogger.debug(methodName, "activeFSMap and clientTypeMap :",  KnGDPRTemplate.mapKeyMdn(activeFSMap), KnGDPRTemplate.mapKeyMdn(clientTypeMap));

            if (subsProvInputDTO.getCorpId() > 0) {
                long corpProfileUpdateTime = Calendar.getInstance().getTimeInMillis();
                xdmServerDAO.updateCorpProfileLastUpdateTime(subsProvInputDTO.getCorpId(), corpProfileUpdateTime, persisterTxn);
            }

            KnSubsAddlInfoPersistDTO subsAddlProfilePersistDTO = new KnSubsAddlInfoPersistDTO();
            long lastProfileUpdateTime = Calendar.getInstance().getTimeInMillis();
            subsAddlProfilePersistDTO.setMdns(subsProvInputDTO.getMdnList());

            knLogger.debug( methodName, "DTO for settingvclient::"+subsAddlProfilePersistDTO.toString());
            subsAddlProfilePersistDTO.setRecodingStatus(subsProvInputDTO.getRecordingStatus());
            provInfoUtil.setSubscrClientAddlInfo(subsAddlProfilePersistDTO, persisterTxn);
            IXDMServerDAO commonXDMServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
            //retrieve the previous etag of dir doc
            List<Integer> previousEtags = commonXDMServerDAO.getCurrentEtagsForDirDoc(subsProvInputDTO.getMdnList(), persisterTxn);

            //update the dir doc etag
            commonXDMServerDAO.updateEtagForDirDoc(subsProvInputDTO.getMdnList(), persisterTxn);
            knLogger.debug(methodName, "Successfully updated the xdm directory");
            //populate the response DTO
            //checking if the request is to deactivate or Provisioned subscriber
            //if the request is deactivate subscriber then populate the deactivation notification dto

            responseDTO.setResponseMessage(KnProvConstants.SET_UPDATE_CLIENTSETTINGS_SUCCESS);
            responseDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.SUCCESS.value());
            responseDTO.setEtag(lastProfileUpdateTime);
            responseDTO.setCorpId(subsProvInputDTO.getCorpId());

            knLogger.debug(methodName, "responseDTO after setting corpId :",responseDTO);

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
            //populating the Subscriber config doc DTO

            List<KnOPDirChgDTO> dirChgDTOs = new ArrayList<KnOPDirChgDTO>();
            KnOPDocChgDTO docChgDTO;
            KnOPDirChgDTO dirChgDTO;
            int i = 0;
            genInfoUtil.retrieveXDMSServiceConfig(xdmPttServerId, persisterTxn);

            for (String mdns : subsProvInputDTO.getMdnList()) {
                docChgDTO = new KnOPDocChgDTO();
                docChgDTO.setDocumentChgType(KnConstants.DOC_CHANGE_TYPE.REPLACE.value());
                String subsConfigDocUri = provInfoUtil.generateSubsConfigSelUri(mdns);
                docChgDTO.setDocUri(subsConfigDocUri);
                docChgDTO.setNewEtag(String.valueOf(lastProfileUpdateTime));

                Collection<KnOPDocChgDTO> chgDocList = new ArrayList<KnOPDocChgDTO>();
                chgDocList.add(docChgDTO);

                //populating the XDM Directory DTO
                dirChgDTO = new KnOPDirChgDTO();
                dirChgDTO.setXcapRootURI(genInfoUtil.getXCAPRootURI(mdns, persisterTxn));
                dirChgDTO.setDocChgDTO(chgDocList);
                String dirDocUri = genInfoUtil.generateDirDocUri(mdns);
                dirChgDTO.setDirUri(dirDocUri);
                dirChgDTO.setDirPrevEtag(String.valueOf(previousEtags.get(i)));
                int newEtag = previousEtags.get(i) + 1;
                dirChgDTO.setDirNewEtag(String.valueOf(newEtag));
                dirChgDTO.setProtoVersion(profileResponse.getSubsRespMap().get(mdns).getClientPVmajorVer() + "." + profileResponse.getSubsRespMap().get(mdns).getClientPVminorVer());
                dirChgDTO.setClientType(profileResponse.getSubsRespMap().get(mdns).getSubsClientType());
                dirChgDTO.setPocHome(profileResponse.getSubsRespMap().get(mdns).getPoCHome());
                dirChgDTO.setPresenceHome(profileResponse.getSubsRespMap().get(mdns).getPresenceHome());
                //populating the Dir chg DTO to response
                dirChgDTOs.add(dirChgDTO);
                i++;
            }

            responseDTO.setDirChgDTOs(dirChgDTOs);

            knLogger.debug(methodName, "responseDTO from Controller :",responseDTO.getServiceAuthStatus());
            //updating the success pegs
            responseDTO.setSuccessPegs(successPegs);

            knLogger.debug(methodName,"-[ responseDTO ]-",responseDTO ,"[ subsProvInputDTO  ]",subsProvInputDTO);

            knLogger.info(methodName, "EXIT: Update Subscriber operation ");

            return responseDTO;
        } catch (KnDBConnectionException ex) {
            knLogger.error(methodName, "DAO DBConnection Exception occured :", ex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);

        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occurred : ", ex);
            knLogger.error(methodName, ex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            if (com.kodiak.xdms.server.common.resources.KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_INFO_NOT_FOUND, "Subscriber does not exists");
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception occurred :", e);
            knLogger.error(methodName, e);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw e;
        } /*catch (KnValidationException vex) {
            knLogger.error(methodName, "Validation Exception occurred :", vex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw vex;
        }*/ catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while Update Subscriber");
            knLogger.error(methodName, e);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred while update Subscriber", e);
        }

    }

    private KnDeviceAddlInfoPersistDTO getKnDeviceAddlInfoPersistDTO(KnXDMDeviceProvInfoDTO deviceProfileInfoInputDTO, String kuid) {
        KnDeviceAddlInfoPersistDTO deviceAddlInfoPersistDTO = new KnDeviceAddlInfoPersistDTO();
        deviceAddlInfoPersistDTO.setDeviceId(kuid);
        if (null != deviceProfileInfoInputDTO.getDeviceAddlInfo()) {
            KnXDMDeviceAddlInfoDTO deviceAddlInfoDTO = deviceProfileInfoInputDTO.getDeviceAddlInfo();
            knLogger.debug("getKnDeviceAddlInfoPersistDTO(KnXDMDeviceProvInfoDTO, String) ", "deviceAddlInfoDTO :", deviceAddlInfoDTO.toString());
            String deviceSerialNo = null;
            if (null != deviceAddlInfoDTO.getDeviceSerialNo()) {
                deviceSerialNo = deviceAddlInfoDTO.getDeviceSerialNo().trim();
            }
            deviceAddlInfoPersistDTO.setDeviceSerialNo(deviceSerialNo);

            String deviceIMEI1 = null;
            if (null != deviceAddlInfoDTO.getDeviceIMEI1()) {
                deviceIMEI1 = deviceAddlInfoDTO.getDeviceIMEI1().trim();
            }
            deviceAddlInfoPersistDTO.setDeviceIMEI1(deviceIMEI1);

            String deviceIMEI2 = null;
            if (null != deviceAddlInfoDTO.getDeviceIMEI2()) {
                deviceIMEI2 = deviceAddlInfoDTO.getDeviceIMEI2().trim();
            }
            deviceAddlInfoPersistDTO.setDeviceIMEI2(deviceIMEI2);

            String deviceInfo = null;
            if (null != deviceAddlInfoDTO.getDeviceInfo()) {
                deviceInfo = deviceAddlInfoDTO.getDeviceInfo().trim();
            }
            deviceAddlInfoPersistDTO.setDeviceInfo(deviceInfo);

            String deviceVersion = null;
            if (null != deviceAddlInfoDTO.getDeviceVersion()) {
                deviceVersion = deviceAddlInfoDTO.getDeviceVersion().trim();
            }
            deviceAddlInfoPersistDTO.setDeviceVersion(deviceVersion);
        }
        return deviceAddlInfoPersistDTO;
    }

    @Override
    public KnOPSubsProfileInfoDTO getSubscriberIfExist(String mdn, KnPersisterTxn persisterTxn)
            throws KnDAOException, KnProvException {
        String methodName = "getSubscriberIfExist(String, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY: ", KnGDPRTemplate.mdn(mdn));
        KnOPSubsProfileInfoDTO subsProfileInfoDTO = new KnOPSubsProfileInfoDTO();
        try {
            try {
                xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
            } catch (KnBOException e) {
                knLogger.error(methodName, "failed to retrieve xdm Ptt Sever Id");
                knLogger.error(methodName, e);
                throw new KnProvBOException(KnErrorCodes.BOEntity.XDMS_PTT_ID_NOT_FOUND,
                        "Failed to retrieve XDM PTT Server ID", e);
            }
            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            subsProfileInfoDTO = provXDMServerDAO.getSubscriberProfileIfExist(mdn.trim(), persisterTxn);
        } catch (KnDBConnectionException ex) {
            knLogger.error(methodName, "DAO DBConnection Exception occured :" + ex);
            throw new KnProvBOException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);
        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occurred : " + ex);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception occurred :" + e.getErrorCode());
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while get subscriber details" + e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                    "Exception occurred while get subscriber details", e);
        }
        knLogger.info(methodName, "EXIT:  getSubscriberIfExist - ", subsProfileInfoDTO);

        return subsProfileInfoDTO;
    }

    @Override
    public KnOPCreateSubsInfoDTO createCorpAccount(KnXDMCorpProfileInfoDTO corpProfileInfoDTO, KnPersisterTxn persisterTxn)
            throws KnProvException {
        String methodName = "createCorpAccount()";
        KnOPCreateSubsInfoDTO respDTO = new KnOPCreateSubsInfoDTO();
        knLogger.info(methodName, "ENTRY: Input DTO - ", corpProfileInfoDTO);
        String xdmPttServerId;
        try {

            try {
                xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
            } catch (KnBOException e) {
                knLogger.error(methodName, "failed to retrieve xdm Ptt Sever Id", e);
                throw new KnProvBOException(KnErrorCodes.BOEntity.XDMS_PTT_ID_NOT_FOUND, "Failed to retrieve XDM PTT Server ID", e);
            }
            String poCPttServerId = provInfoUtil.getSubsPoCHome(persisterTxn);
            //Validating the  Corporate profile exists by accountID.
            KnXDMCorpInfo corpProfile = KnGeneralProfileUtil.getCorporateDetails(corpProfileInfoDTO.getAccountId());
            if ( null != corpProfile) {
                knLogger.error(methodName, "Ext Corporate account exits ",
                        corpProfileInfoDTO.getAccountId());
                throw new KnProvBOException(
                        com.kodiak.xdms.server.common.resources.KnErrorCodes.BOEntity.EXTCORP_ACCOUNT_EXISTS,
                        "Ext Corporate account exits ");
            }

            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB)
                    .createProvXDMServerDAO();
            String corpFS2 = featureSetUtil.getDefFinalCorpFS();
            String xdmCorpFS2Set = null;
            KnPOCSvcConfigDTO knPOCSvcConfigDTO = provInfoUtil.retrievePOCSvcConfig(poCPttServerId);
            String opsCorpFS2 = featureSetUtil.getDefFinalOpsCorpFS();
            provUtil.validateCorpProfile(corpProfileInfoDTO,xdmPttServerId,persisterTxn);
            KnCorpProfilePersistDTO corpProfilePersistDTO = new KnCorpProfilePersistDTO();
            String extCorpId = corpProfileInfoDTO.getAccountId();
            corpProfilePersistDTO.setExtCorpId(extCorpId);
            corpProfilePersistDTO.setCorporateName(corpProfileInfoDTO.getCorporateName());
            if (null != corpProfileInfoDTO.getMaxSubscribers()) {
                corpProfilePersistDTO.setMaxSubscribers(Integer.parseInt(corpProfileInfoDTO.getMaxSubscribers()));
            }
            if (null != corpProfileInfoDTO.getMaxMemPerCorpGroup()) {
                corpProfilePersistDTO.setMaxMembersPerCorpGroup(Integer.parseInt(corpProfileInfoDTO.getMaxMemPerCorpGroup()));
            }
            if (null != corpProfileInfoDTO.getMaxCorpLists()) {
                corpProfilePersistDTO.setMaxCorpLists(Integer.parseInt(corpProfileInfoDTO.getMaxCorpLists()));
            }
            if (null != corpProfileInfoDTO.getMaxCorpGroups()) {
                corpProfilePersistDTO.setMaxCorpGroups(Integer.parseInt(corpProfileInfoDTO.getMaxCorpGroups()));
            }
            if (null != corpProfileInfoDTO.getMaxDispatchGroups()) {
                corpProfilePersistDTO.setMaxDispatchGroups(Integer.parseInt(corpProfileInfoDTO.getMaxDispatchGroups()));
            }
            if (null != corpProfileInfoDTO.getMaxMemPerDispatchGroup()){
                corpProfilePersistDTO.setMaxMemPerDispatchGroup(Integer.parseInt(corpProfileInfoDTO.getMaxMemPerDispatchGroup()));
            }
            if (null != corpProfileInfoDTO.getDispatchEnabled()) {
                Boolean isDispatchEnabled = Boolean.FALSE;
                if (corpProfileInfoDTO.getDispatchEnabled().trim().equalsIgnoreCase(String.valueOf(Boolean.TRUE))) {
                    isDispatchEnabled = Boolean.TRUE;
                }
                corpProfilePersistDTO.setDispatchEnabled(isDispatchEnabled);
            }
            if (null != corpProfileInfoDTO.getIsInterOpEnabled()) {
                Boolean isInterOpEnabled = Boolean.FALSE;
                if (corpProfileInfoDTO.getIsInterOpEnabled().trim().equalsIgnoreCase(String.valueOf(Boolean.TRUE))) {
                    isInterOpEnabled = Boolean.TRUE;
                }
                corpProfilePersistDTO.setIsInterOpEnabled(isInterOpEnabled);
            }
            if (null != corpProfileInfoDTO.getMaxExtSubsPerCorp()) {
                corpProfilePersistDTO.setMaxExtSubsPerCorp(Integer.parseInt(corpProfileInfoDTO.getMaxExtSubsPerCorp()));
            }
            if (null != corpProfileInfoDTO.getPttRadioChannelListSize()) {
                corpProfilePersistDTO.setMaxRadioChannels(Integer.parseInt(corpProfileInfoDTO.getPttRadioChannelListSize()));
            }
            if (null != corpProfileInfoDTO.getMaxZoneAllowed()) {
                corpProfilePersistDTO.setMaxZones(Integer.parseInt(corpProfileInfoDTO.getMaxZoneAllowed()));
            }
            if (null != corpProfileInfoDTO.getMaxChannelsPerZone()) {
                corpProfilePersistDTO.setMaxChannelsPerZone(Integer.parseInt(corpProfileInfoDTO.getMaxChannelsPerZone()));
            }
            if (null != corpProfileInfoDTO.getMaxLgrGrp()) {
                corpProfilePersistDTO.setMaxLargeTalkGroup(Integer.parseInt(corpProfileInfoDTO.getMaxLgrGrp()));
            }
            if (null != corpProfileInfoDTO.getMaxGrpProfiles()) {
                corpProfilePersistDTO.setMaxGroupProfile(Integer.parseInt(corpProfileInfoDTO.getMaxGrpProfiles()));
            }
            if (null != corpProfileInfoDTO.getMaxUserProfiles()) {
                corpProfilePersistDTO.setMaxUserProfile(Integer.parseInt(corpProfileInfoDTO.getMaxUserProfiles()));
            }
            if (null != corpProfileInfoDTO.getMaxAssignProfiles()) {
                corpProfilePersistDTO.setMaxAssignProfiles(Integer.parseInt(corpProfileInfoDTO.getMaxAssignProfiles()));
            }
            int isLargeAgencyDispatchFeature = 0;
            if (null != corpProfileInfoDTO.getLargeAgencyDispatchFeature()) {
                if (corpProfileInfoDTO.getLargeAgencyDispatchFeature().trim().equalsIgnoreCase(String.valueOf(Boolean.TRUE))) {
                    isLargeAgencyDispatchFeature = 1;
                }
            }
            Map<Integer, Integer> largeAgencyDispatchFeature = new HashMap<>();
            Boolean locationEnabled = corpProfileInfoDTO.isLocationEnabled();
            largeAgencyDispatchFeature.put(com.kodiak.common.resources.KnConstants.FEATURE_SET.LARGE_AGENCY_DISPATCH.value(), isLargeAgencyDispatchFeature);
            corpFS2 = KnGeneralUtil.updateFeatureBit(corpFS2, largeAgencyDispatchFeature);
            if (null != locationEnabled) {
                String initialXDMCorpFS2Set = KnGeneralUtil.getDefaultXDMCorpFS2Set(XDMCORPFS2_SET.LOCATION_ENABLED.value(), com.kodiak.common.resources.KnConstants.TRUE);
                xdmCorpFS2Set = featureSetUtil.calculateXDMCorpFS2(initialXDMCorpFS2Set, XDMCORPFS2_SET.LOCATION_ENABLED.value(), locationEnabled);
            }
            String initialXDMEmeCorpFS2Set = (xdmCorpFS2Set == null) ? KnGeneralUtil.getDefaultXDMCorpFS2Set(XDMCORPFS2_SET.EMERGENCY_CONF_TIMER_FEATURE.value(), com.kodiak.common.resources.KnConstants.TRUE) : xdmCorpFS2Set;
            xdmCorpFS2Set = featureSetUtil.calculateXDMCorpFS2(initialXDMEmeCorpFS2Set, XDMCORPFS2_SET.EMERGENCY_CONF_TIMER_FEATURE.value(), com.kodiak.common.resources.KnConstants.TRUE);
            corpProfilePersistDTO.setCorpFS2(corpFS2);
            corpProfilePersistDTO.setDynamicQosFlag(knPOCSvcConfigDTO.getDynamicQosFlag());
            corpProfilePersistDTO.setFeatureRelVersion(currentFsVersion);
            corpProfilePersistDTO.setOpsCorpFS2(opsCorpFS2);
            corpProfilePersistDTO.setXdmCorpFS2Set(xdmCorpFS2Set);
            String pocHome = provInfoUtil.getSubsPoCHome(persisterTxn);
            corpProfilePersistDTO.setPocHome(pocHome);
            corpProfilePersistDTO.setXDMSHome(xdmPttServerId);
            //insert corp profile
            int corpId = provXDMServerDAO.createCorporateAccount(corpProfilePersistDTO, persisterTxn);
            respDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.SUCCESS.value());
            respDTO.setResponseMessage(KnProvConstants.CREATE_CORP_SUCCESS);
            respDTO.setCorpId(corpId);
            respDTO.setExtCorpId(extCorpId);
            knLogger.info(methodName, " Exit Create corp account operation-", respDTO);
            return respDTO;
        } catch (KnDBConnectionException ex) {
            knLogger.error(methodName, "DAO DBConnection Exception occured :", ex);
            throw new KnProvBOException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);
        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception occurred :", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while Create corp account :", e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred while Create corp account ", e);
        }
    }

    @Override
    public KnOPCreateSubsInfoDTO updateCorpAccount(KnXDMCorpProfileInfoDTO corpProfileInfoDTO, KnPersisterTxn persisterTxn) throws KnDAOException, KnProvException {
        String methodName = "updateCorpAccount()";
        KnOPCreateSubsInfoDTO respDTO = new KnOPCreateSubsInfoDTO();
        knLogger.info(methodName, "ENTRY: Input DTO - ", corpProfileInfoDTO);
        String xdmPttServerId;
        try {

            try {
                xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
            } catch (KnBOException e) {
                knLogger.error(methodName, "failed to retrieve xdm Ptt Sever Id", e);
                throw new KnProvBOException(KnErrorCodes.BOEntity.XDMS_PTT_ID_NOT_FOUND, "Failed to retrieve XDM PTT Server ID", e);
            }

            //Validating the  Corporate profile exists by accountID.
            KnXDMCorpInfo corpProfile = KnGeneralProfileUtil.getCorporateDetails(corpProfileInfoDTO.getAccountId());
            if (null == corpProfile) {

                    knLogger.error(methodName, "Ext Corporate account  doesn't exits ",
                            corpProfileInfoDTO.getAccountId());
                    throw new KnProvBOException(
                            com.kodiak.xdms.server.common.resources.KnErrorCodes.BOEntity.EXTCORP_ACCOUNT_NOT_EXISTS,
                            "Ext Corporate account doesn't exits ");
                }
                IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB)
                        .createProvXDMServerDAO();
                provUtil.validateCorpProfile(corpProfileInfoDTO, xdmPttServerId, persisterTxn);
                KnCorpProfilePersistDTO corpProfilePersistDTO = new KnCorpProfilePersistDTO();
                String extCorpId = corpProfileInfoDTO.getAccountId();
                corpProfilePersistDTO.setCorpId(Integer.parseInt(corpProfile.getCorpId()));
                corpProfilePersistDTO.setExtCorpId(extCorpId);
                corpProfilePersistDTO.setCorporateName(corpProfileInfoDTO.getCorporateName());
                if (null != corpProfileInfoDTO.getMaxSubscribers() && !corpProfileInfoDTO.getMaxSubscribers().isEmpty()) {
                    corpProfilePersistDTO.setMaxSubscribers(Integer.parseInt(corpProfileInfoDTO.getMaxSubscribers()));
                }
                if (null != corpProfileInfoDTO.getMaxMemPerCorpGroup() && !corpProfileInfoDTO.getMaxMemPerCorpGroup().isEmpty()) {
                    corpProfilePersistDTO.setMaxMembersPerCorpGroup(Integer.parseInt(corpProfileInfoDTO.getMaxMemPerCorpGroup()));
                }
                if (null != corpProfileInfoDTO.getMaxCorpLists() && !corpProfileInfoDTO.getMaxCorpLists().isEmpty()) {
                    corpProfilePersistDTO.setMaxCorpLists(Integer.parseInt(corpProfileInfoDTO.getMaxCorpLists()));
                }
                if (null != corpProfileInfoDTO.getMaxMemPerCorpList() && !corpProfileInfoDTO.getMaxMemPerCorpList().isEmpty()) {
                    corpProfilePersistDTO.setMaxMembersPerCorpList(Integer.parseInt(corpProfileInfoDTO.getMaxMemPerCorpList()));
                }
                if (null != corpProfileInfoDTO.getMaxCorpGroups() && !corpProfileInfoDTO.getMaxCorpGroups().isEmpty()) {
                    corpProfilePersistDTO.setMaxCorpGroups(Integer.parseInt(corpProfileInfoDTO.getMaxCorpGroups()));
                }
                if (null != corpProfileInfoDTO.getMaxDispatchGroups() && !corpProfileInfoDTO.getMaxDispatchGroups().isEmpty()) {
                    corpProfilePersistDTO.setMaxDispatchGroups(Integer.parseInt(corpProfileInfoDTO.getMaxDispatchGroups()));
                }
                if (null != corpProfileInfoDTO.getMaxMemPerDispatchGroup() && !corpProfileInfoDTO.getMaxMemPerDispatchGroup().isEmpty()) {
                    corpProfilePersistDTO.setMaxMemPerDispatchGroup(Integer.parseInt(corpProfileInfoDTO.getMaxMemPerDispatchGroup()));
                }
                if (null != corpProfileInfoDTO.getDispatchEnabled()) {
                    Boolean isDispatchEnabled = Boolean.FALSE;
                    if (corpProfileInfoDTO.getDispatchEnabled().trim().equalsIgnoreCase(String.valueOf(Boolean.TRUE))) {
                        isDispatchEnabled = Boolean.TRUE;
                    }
                    corpProfilePersistDTO.setDispatchEnabled(isDispatchEnabled);
                }
                if (null != corpProfileInfoDTO.getIsInterOpEnabled()) {
                    Boolean isInterOpEnabled = Boolean.FALSE;
                    if (corpProfileInfoDTO.getIsInterOpEnabled().trim().equalsIgnoreCase(String.valueOf(Boolean.TRUE))) {
                        isInterOpEnabled = Boolean.TRUE;
                    }
                    corpProfilePersistDTO.setIsInterOpEnabled(isInterOpEnabled);
                }
                if (null != corpProfileInfoDTO.getMaxExtSubsPerCorp() && !corpProfileInfoDTO.getMaxExtSubsPerCorp().isEmpty()) {
                    corpProfilePersistDTO.setMaxExtSubsPerCorp(Integer.parseInt(corpProfileInfoDTO.getMaxExtSubsPerCorp()));
                }
                if (null != corpProfileInfoDTO.getMaxMemPerBCGrp() && !corpProfileInfoDTO.getMaxMemPerBCGrp().isEmpty()) {
                    corpProfilePersistDTO.setMaxMemPerBCGrp(Integer.parseInt(corpProfileInfoDTO.getMaxMemPerBCGrp()));
                }
                if (null != corpProfileInfoDTO.getMaxChannelAllowed() && !corpProfileInfoDTO.getMaxChannelAllowed().isEmpty()) {
                    corpProfilePersistDTO.setMaxRadioChannels(Integer.parseInt(corpProfileInfoDTO.getMaxChannelAllowed()));
                }
                if (null != corpProfileInfoDTO.getMaxZoneAllowed() && !corpProfileInfoDTO.getMaxZoneAllowed().isEmpty()) {
                    corpProfilePersistDTO.setMaxZones(Integer.parseInt(corpProfileInfoDTO.getMaxZoneAllowed()));
                }
                if (null != corpProfileInfoDTO.getMaxChannelsPerZone() && !corpProfileInfoDTO.getMaxChannelsPerZone().isEmpty()) {
                    corpProfilePersistDTO.setMaxChannelsPerZone(Integer.parseInt(corpProfileInfoDTO.getMaxChannelsPerZone()));
                }
                if (null != corpProfileInfoDTO.getMaxLgrGrp() && !corpProfileInfoDTO.getMaxLgrGrp().isEmpty()) {
                    corpProfilePersistDTO.setMaxLargeTalkGroup(Integer.parseInt(corpProfileInfoDTO.getMaxLgrGrp()));
                }
                if (null != corpProfileInfoDTO.getMaxGrpProfiles() && !corpProfileInfoDTO.getMaxGrpProfiles().isEmpty()) {
                    corpProfilePersistDTO.setMaxGroupProfile(Integer.parseInt(corpProfileInfoDTO.getMaxGrpProfiles()));
                }
                if (null != corpProfileInfoDTO.getMaxUserProfiles() && !corpProfileInfoDTO.getMaxUserProfiles().isEmpty()) {
                    corpProfilePersistDTO.setMaxUserProfile(Integer.parseInt(corpProfileInfoDTO.getMaxUserProfiles()));
                }
                if (null != corpProfileInfoDTO.getMaxAssignProfiles() && !corpProfileInfoDTO.getMaxAssignProfiles().isEmpty()) {
                    corpProfilePersistDTO.setMaxAssignProfiles(Integer.parseInt(corpProfileInfoDTO.getMaxAssignProfiles()));
                }
                if (null != corpProfileInfoDTO.getLargeAgencyDispatchFeature()) {
                    int isLargeAgencyDispatchFeature = 0;
                    if (corpProfileInfoDTO.getLargeAgencyDispatchFeature().trim().equalsIgnoreCase(String.valueOf(Boolean.TRUE))) {
                        isLargeAgencyDispatchFeature = 1;
                    }
                    String corpFS2 = corpProfile.getCorpFS2();
                    Map<Integer, Integer> largeAgencyDispatchFeature = new HashMap<>();
                    largeAgencyDispatchFeature.put(com.kodiak.common.resources.KnConstants.FEATURE_SET.LARGE_AGENCY_DISPATCH.value(), isLargeAgencyDispatchFeature);
                    corpFS2 = KnGeneralUtil.updateFeatureBit(corpFS2, largeAgencyDispatchFeature);
                    corpProfilePersistDTO.setCorpFS2(corpFS2);
                }
            String xdmCorpFS2value = corpProfile.getXdmCorpFS2Set();
            String xdmCorpFS2final = xdmCorpFS2value;
            if (null == xdmCorpFS2value || xdmCorpFS2value.isEmpty()) {
                xdmCorpFS2value = KnGeneralUtil.getDefaultXDMCorpFS2Set(XDMCORPFS2_SET.LOCATION_ENABLED.value(), com.kodiak.common.resources.KnConstants.TRUE);
                xdmCorpFS2final = corpProfileInfoDTO.isLocationEnabled() != null
                        ? featureSetUtil.calculateXDMCorpFS2(xdmCorpFS2value, XDMCORPFS2_SET.LOCATION_ENABLED.value(), corpProfileInfoDTO.isLocationEnabled())
                        : xdmCorpFS2value;
            }
            knLogger.debug(methodName,"xdmCorpFS2value::",xdmCorpFS2value,"xdmCorpFS2final::",xdmCorpFS2final);
            if (null != corpProfileInfoDTO.isLocationEnabled()) {
                if (!corpProfileInfoDTO.isLocationEnabled() && null != corpProfile.getXdmCorpFS2Set()) {
                    knLogger.debug(methodName, "Disabling the location in XDMCORPFS2");
                    //MINT-28914
                    //throw new KnProvBOException(KnErrorCodes.BOEntity.LOCATION_ENABLE_TO_DISABLE_NOT_ALLOWED, "Disabling the Location on agency level is not supported");
                    String xdmCorpFS2Set = corpProfile.getXdmCorpFS2Set();
                    Map<Integer, Integer> xdmCorpFS2SetMap = new HashMap<>();
                    xdmCorpFS2SetMap.put(XDMCORPFS2_SET.LOCATION_ENABLED.value(), 0);
                    xdmCorpFS2Set = KnGeneralUtil.updateFeatureBit(xdmCorpFS2Set, xdmCorpFS2SetMap);
                    corpProfilePersistDTO.setXdmCorpFS2Set(xdmCorpFS2Set);
                }
                if (corpProfileInfoDTO.isLocationEnabled() && null == corpProfile.getXdmCorpFS2Set()) {
                    String initialXDMCorpFS2Set = KnGeneralUtil.getDefaultXDMCorpFS2Set(XDMCORPFS2_SET.LOCATION_ENABLED.value(), com.kodiak.common.resources.KnConstants.TRUE);
                    String xdmCorpFS2Set = null;
                    xdmCorpFS2Set = featureSetUtil.calculateXDMCorpFS2(initialXDMCorpFS2Set, XDMCORPFS2_SET.LOCATION_ENABLED.value(), corpProfileInfoDTO.isLocationEnabled());
                    corpProfilePersistDTO.setXdmCorpFS2Set(xdmCorpFS2Set);
                    KnGeneralUtil.insertIntoAsyncJobTaskUtil(corpProfile.getCorpId());
                } else if (!KnGeneralUtil.getFeatureBitValue(xdmCorpFS2final, XDMCORPFS2_SET.LOCATION_ENABLED.value()) && corpProfileInfoDTO.isLocationEnabled()) {
                    knLogger.debug(methodName, "InsertJob");
                    Boolean locationEnabled = corpProfileInfoDTO.isLocationEnabled();
                    String xdmCorpFS2Set = xdmCorpFS2final;
                    Map<Integer, Integer> xdmCorpFS2SetMap = new HashMap<>();
                    xdmCorpFS2SetMap.put(XDMCORPFS2_SET.LOCATION_ENABLED.value(), locationEnabled ? 1 : 0);
                    xdmCorpFS2Set = KnGeneralUtil.updateFeatureBit(xdmCorpFS2Set, xdmCorpFS2SetMap);
                    corpProfilePersistDTO.setXdmCorpFS2Set(xdmCorpFS2Set);
                    KnGeneralUtil.insertIntoAsyncJobTaskUtil(corpProfile.getCorpId());
                }
            }
                //update corp profile
                int corpId = provXDMServerDAO.updateCorporateAccount(corpProfilePersistDTO, persisterTxn);
                respDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.SUCCESS.value());
                respDTO.setResponseMessage(KnProvConstants.UPDATE_CORP_SUCCESS);
                respDTO.setCorpId(corpId);
                respDTO.setExtCorpId(extCorpId);
                knLogger.info(methodName, " Exit Update corp account operation-", respDTO);
                return respDTO;
        } catch (KnDBConnectionException ex) {
            knLogger.error(methodName, "DAO DBConnection Exception occured :", ex);
            throw new KnProvBOException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);
        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception occurred :", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while Update corp account :", e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred while Create corp account ", e);
        }
    }

    @Override
    public KnOPDeleteSubsRespDTO deleteCorpAccount(KnXDMCorpInfoDTO corpProfileInfoDTO, KnPersisterTxn persisterTxn)
            throws KnDAOException, KnProvException {
        String methodName = "deleteCorpAccount()";
        KnOPDeleteSubsRespDTO respDTO = new KnOPDeleteSubsRespDTO();
        knLogger.info(methodName, "ENTRY: Input DTO - ", corpProfileInfoDTO);
        String xdmPttServerId;
        try {

            try {
                xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
            } catch (KnBOException e) {
                knLogger.error(methodName, "failed to retrieve xdm Ptt Sever Id");
                knLogger.error(methodName, e);
                throw new KnProvBOException(KnErrorCodes.BOEntity.XDMS_PTT_ID_NOT_FOUND, "Failed to retrieve XDM PTT Server ID", e);
            }
            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB)
                    .createProvXDMServerDAO();
            String extCorpid = corpProfileInfoDTO.getAccountId();
            int corpId = provXDMServerDAO.getCorpId(extCorpid, persisterTxn);
            provXDMServerDAO.deleteCorporateAccount(extCorpid,corpId, persisterTxn);
            knLogger.debug(methodName, "Deleted corporate Account ");
            respDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.SUCCESS.value());
            respDTO.setResponseMessage(KnProvConstants.DELETE_CORP_ACCOUNT_SUCCESS);
            respDTO.setCorpId(corpId);
            knLogger.exit(methodName, "delete Corporate Account operation-", respDTO);

            return respDTO;
        } catch (KnDBConnectionException ex) {
            knLogger.error(methodName, "DAO DBConnection Exception occured :", ex);
            throw new KnProvBOException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);

        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception occurred :", e);
            knLogger.error(methodName, e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while deleting the corpAccount");
            knLogger.error(methodName, e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred while deleting the CorpAccount", e);
        }
    }

    public KnCorporateProfilepersistDTO1 getCorporateAccountDetails(KnXDMDeviceProvInfoDTO xdmRequestDTO, KnPersisterTxn persisterTxn) throws KnDAOException, KnProvBOException, KnValidationException {
        String methodName = "getCorporateAccountDetails(knXDMDeviceProvInfoDTO, KnPersisterTxn)";
        boolean ownedTxn = false;
        KnCorporateProfilepersistDTO1 profilepersistDTO = new KnCorporateProfilepersistDTO1();
        String extCorpId = (xdmRequestDTO.getAccountId());
        String txtId = (xdmRequestDTO.getTransactionId());
        knLogger.debug(methodName, "xdmRequestDTO: ", xdmRequestDTO);
        try {
            xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
            profilepersistDTO = provInfoUtil.selectCorpID(extCorpId, persisterTxn);
            knLogger.debug(methodName, "got repsonse from DAO ", profilepersistDTO);
            int CorpId = profilepersistDTO.getCorpId();
            if (CorpId != 0) {
                profilepersistDTO = provInfoUtil.selectCorporateAccountDetails(CorpId, persisterTxn);
                knLogger.debug(methodName, "got repsonse from dao and server confog table  ", profilepersistDTO);
                KnXDMSServiceConfigDTO xdmsConfigDTO = genInfoUtil.retrieveXDMSServiceConfig(xdmPttServerId, persisterTxn);
                if (null == profilepersistDTO.getMaxCorpGroups()) {
                    profilepersistDTO.setMaxCorpGroups(String.valueOf(xdmsConfigDTO.getMaxPOCGrpsPerCorp()));
                }
                if (null == profilepersistDTO.getMaxMemPerCorpGroup()) {
                    profilepersistDTO.setMaxMemPerCorpGroup(String.valueOf(xdmsConfigDTO.getMaxMembersPerCorpPOCGrp()));
                }
                if (null == profilepersistDTO.getMaxDispatchGroups()) {
                    profilepersistDTO.setMaxDispatchGroups(String.valueOf(xdmsConfigDTO.getMaxDispatchGroup()));
                }
                if (null == profilepersistDTO.getMaxMemPerBCGrp()) {
                    profilepersistDTO.setMaxMemPerBCGrp(String.valueOf(xdmsConfigDTO.getMaxMemPerBCGrp()));
                }
                if (null == profilepersistDTO.getMaxCorpLists()) {
                    profilepersistDTO.setMaxCorpLists(String.valueOf(xdmsConfigDTO.getMaxSublistsPerCorp()));
                }
                if (null == profilepersistDTO.getMaxMemPerCorpList()) {
                    profilepersistDTO.setMaxMemPerCorpList(String.valueOf(xdmsConfigDTO.getMaxMembersPerCorpSublist()));
                }
                if (null == profilepersistDTO.getMaxSubscribers()) {
                    profilepersistDTO.setMaxSubscribers(String.valueOf(xdmsConfigDTO.getMaxSubscrPerCorp()));
                }
                if (null == profilepersistDTO.getMaxExtSubsPerCorp()) {
                    profilepersistDTO.setMaxExtSubsPerCorp(String.valueOf(xdmsConfigDTO.getMaxExtSubsPerCorp()));
                }
                if (null == profilepersistDTO.getMaxContactsPerSubs()) {
                    profilepersistDTO.setMaxContactsPerSubs(String.valueOf(xdmsConfigDTO.getMaxCorpContactsPerSubs()));
                }
                if (null == profilepersistDTO.getMaxExtContactsPerCorp()) {
                    profilepersistDTO.setMaxExtContactsPerCorp(String.valueOf(xdmsConfigDTO.getMaxExtContactsPerCorp()));
                }
                if (null == profilepersistDTO.getMaxMemPerDispatchGroup()) {
                    profilepersistDTO.setMaxMemPerDispatchGroup(String.valueOf(xdmsConfigDTO.getMaxMembersPerDispatchGroup()));
                }
                if (null == profilepersistDTO.getMaxContactsPerRequest()) {
                    profilepersistDTO.setMaxContactsPerRequest(String.valueOf(xdmsConfigDTO.getMaxContactPerRequest()));
                }
                if (null == profilepersistDTO.getPttRadioDefScanMode()) {
                    profilepersistDTO.setPttRadioDefScanMode(String.valueOf(xdmsConfigDTO.getPttRadioDefScanMode()));
                }
                if (null == profilepersistDTO.getPttRadioChannelListSize()) {
                    profilepersistDTO.setPttRadioChannelListSize(String.valueOf(xdmsConfigDTO.getPttRadioChannelListSize()));
                }
                if (null == profilepersistDTO.getPttRadioScanListSize()) {
                    profilepersistDTO.setPttRadioScanListSize(String.valueOf(xdmsConfigDTO.getPttRadioScanListSize()));
                }
                if (null == profilepersistDTO.getMaxZoneAllowed()) {
                    profilepersistDTO.setMaxZoneAllowed(String.valueOf(xdmsConfigDTO.getMaxZones()));
                }
                if (null == profilepersistDTO.getMaxChannelsPerZone()) {
                    profilepersistDTO.setMaxChannelsPerZone(String.valueOf(xdmsConfigDTO.getMaxChannelsPerZone()));
                }
                if (null == profilepersistDTO.getMaxContactsPerSubs()) {
                    profilepersistDTO.setMaxContactsPerSubs(String.valueOf(xdmsConfigDTO.getMaxCorpContactsPerSubs()));
                }

                profilepersistDTO.setSupervisoryOverrideEnabled((xdmsConfigDTO.getSupervisoryEnabled()));

                if (null == profilepersistDTO.getMaxSGPerGrp()) {
                    profilepersistDTO.setMaxSGPerGrp(String.valueOf(xdmsConfigDTO.getMaxSGMdnsPerGroup()));
                }
                if (null == profilepersistDTO.getMaxSGPatchPerGrp()) {
                    profilepersistDTO.setMaxSGPatchPerGrp(String.valueOf(xdmsConfigDTO.getMaxSGPatchPerGrp()));
                }
                if (null == profilepersistDTO.getMaxMemPerLrgGrp()) {
                    profilepersistDTO.setMaxMemPerLrgGrp(String.valueOf(xdmsConfigDTO.getMaxMemPerLrgGrp()));
                }
                if (null == profilepersistDTO.getMaxMemPerLrgBCGrp()) {
                    profilepersistDTO.setMaxMemPerLrgBCGrp(String.valueOf(xdmsConfigDTO.getMaxMemPerLrgBGrp()));
                }
                if (null == profilepersistDTO.getMaxLgrGrp()) {
                    profilepersistDTO.setMaxLgrGrp(String.valueOf(xdmsConfigDTO.getMaxLrgGrpPerCorp()));
                }
                if (null == profilepersistDTO.getMaxLgrBCGrp()) {
                    profilepersistDTO.setMaxLgrBCGrp(String.valueOf(xdmsConfigDTO.getMaxLrgBGrpPerCorp()));
                }
                if (null == profilepersistDTO.getPriorityRange()) {
                    profilepersistDTO.setPriorityRange(String.valueOf(xdmsConfigDTO.getMaxPriority()));
                }
                if (null == profilepersistDTO.getIsInterOpEnabled()) {
                    profilepersistDTO.setIsInterOpEnabled(xdmsConfigDTO.getInteropLicenceType() > 0);
                }
                if (null == profilepersistDTO.getMaxScanListSize()) {
                    profilepersistDTO.setMaxScanListSize(String.valueOf(xdmsConfigDTO.getMaxCampedGroups()));
                }
                profilepersistDTO.setIsBCGrpEnabled((xdmsConfigDTO.getEnableBCGrpFeature()));
                if (null == profilepersistDTO.getDispatchEnabled()) {
                    profilepersistDTO.setDispatchEnabled(xdmsConfigDTO.getWebDispatchEnabled() > 0);
                }
                if (null == profilepersistDTO.getMaxDispatcherPerDispGrp()) {
                    profilepersistDTO.setMaxDispatcherPerDispGrp(String.valueOf(xdmsConfigDTO.getMaxDispatchMembersPerDispatchGroup()));
                }
                int maxRadioChannels = xdmsConfigDTO.getMaxRadioChannels();
                knLogger.debug(methodName, "maxRadioChannels  ", xdmsConfigDTO.getMaxRadioChannels());
                knLogger.debug(methodName, "getMaxChannelAllowed() from server table  ", profilepersistDTO.getMaxChannelAllowed());
                if (null == profilepersistDTO.getMaxChannelAllowed()) {
                    profilepersistDTO.setMaxChannelAllowed(String.valueOf(maxRadioChannels));
                }
                int clusterId = Integer.parseInt(System.getenv(CLUSTERID_ENV_NAME));
                Map<String, String> microServicesParamNameValueMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
                final String maxStatusMsgPerOsmList = microServicesParamNameValueMap.get(MAX_STATUS_MSG_PER_OSM_LIST);
                final String maxStatusShortTextLength = microServicesParamNameValueMap.get(MAX_STATUS_SHORT_TEXT_LENGTH);
                final String maxStatusMsgLength = microServicesParamNameValueMap.get(MAX_STATUS_MSG_LENGTH);
                final String maxCommonContactLisSize = microServicesParamNameValueMap.get(COMMON_CONTACTLIST_SIZE);
                final String maxCommonContactlistPerSub = microServicesParamNameValueMap.get(COMMON_CONTACTLIST_PERSUB);
                String maxUserProfiles = microServicesParamNameValueMap.get(MAX_USER_PROFILES);
                String maxMemNonAPSubList = microServicesParamNameValueMap.get(MAXMEM_NONAP_SUBLIST);
                String bulkExtContAllowed = microServicesParamNameValueMap.get(BLK_EXT_CONT_LMT);
                String bulkLimitCorpAdminFs = microServicesParamNameValueMap.get(BLK_EXT_CONT_LMT_ALLOWED);
                String maxUserProfilesPerSub = microServicesParamNameValueMap.get(MAX_USERPROFILES_PERSUB1);
                String maxGroupProfiles = microServicesParamNameValueMap.get(KnProvConstants.MAX_GROUP_PROFILES);
                if (null == profilepersistDTO.getMaxStatusMsgPerOsmList()) {
                    profilepersistDTO.setMaxStatusMsgPerOsmList(maxStatusMsgPerOsmList);
                }
                if (null == profilepersistDTO.getMaxStatusShortTextLength()) {
                    profilepersistDTO.setMaxStatusShortTextLength(maxStatusShortTextLength);
                }
                if (null == profilepersistDTO.getMaxStatusMsgLength()) {
                        profilepersistDTO.setMaxStatusMsgLength(maxStatusMsgLength);
                }
                if (null == profilepersistDTO.getMaxCommonContactLisSize()) {
                    profilepersistDTO.setMaxCommonContactLisSize(maxCommonContactLisSize);
                }
                if (null == profilepersistDTO.getMaxCommonContactlistPerSub()) {
                    profilepersistDTO.setMaxCommonContactlistPerSub(maxCommonContactlistPerSub);
                }
                if (null == profilepersistDTO.getMaxUserProfiles()) {
                    profilepersistDTO.setMaxUserProfiles(maxUserProfiles);
                }
                if (null == profilepersistDTO.getMaxMemNonAPSubList()) {
                    profilepersistDTO.setMaxMemNonAPSubList(maxMemNonAPSubList);
                }
                if (null == profilepersistDTO.getAddExtContLimit()) {
                    profilepersistDTO.setAddExtContLimit(bulkExtContAllowed);
                }
                if (null == profilepersistDTO.getUpdateCorpAdminFsLimit()) {
                    profilepersistDTO.setUpdateCorpAdminFsLimit(bulkLimitCorpAdminFs);
                }
                if (null == profilepersistDTO.getMaxAssignProfiles()) {
                    profilepersistDTO.setMaxAssignProfiles(maxUserProfilesPerSub);
                }
                if (null == profilepersistDTO.getMaxGrpProfiles()) {
                    profilepersistDTO.setMaxGrpProfiles(maxGroupProfiles);
                }
                boolean locationEnabled = false;
                if (null != profilepersistDTO.getXdmCorpFS2Set()) {
                    locationEnabled = KnGeneralUtil.getFeatureBitValue(profilepersistDTO.getXdmCorpFS2Set(), com.kodiak.common.resources.KnConstants.XDMCORPFS2_SET.LOCATION_ENABLED.value());
                }
                profilepersistDTO.setLocationEnabled(String.valueOf(locationEnabled));
            }
            knLogger.debug(methodName, "data passing to mediator  :", profilepersistDTO);
        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception occurred :", e);
            knLogger.error(methodName, e);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INVALID_CORP_PROFILE,
                    "Corp Profile info not found", e);
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while getCorpAccountDetails");
            knLogger.error(methodName, e);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INVALID_CORP_PROFILE, "Exception occurred while update Subscriber", e);
        }

        return profilepersistDTO;
    }

    public KnXDMCorpAccountsListDTO retrieveCorporationAccountsList(KnPersisterTxn persisterTxn,String fetchSize, String nextToken) throws KnDAOException, KnProvException {
        String methodName = "retrieveCorporationAccountsList(KnPersisterTxn)";
        boolean ownedTxn = false;
        KnXDMCorpAccountsListDTO responseDTO = new KnXDMCorpAccountsListDTO();
        knLogger.debug(methodName, "retrieveCorporationAccountsList req ");
        try {
            xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
            responseDTO = provInfoUtil.retrieveCorporationAccountsList(persisterTxn,fetchSize,nextToken);
        } catch (KnBOException e) {
            knLogger.error(methodName, "failed to retrieve xdm Ptt Sever Id", e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.XDMS_PTT_ID_NOT_FOUND, "Failed to retrieve XDM PTT Server ID", e);
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while getCorporateAccountDetails" + e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred while getCorporateAccountDetails", e);
        }
        knLogger.debug(methodName, "EXIT:  getCorporateAccountDetails - ", responseDTO);
        return responseDTO;
    }

    public String getBaseMdnByProfileMdn(String mdn, KnPersisterTxn persisterTxn) throws KnProvBOException {
        String methodName = "getBaseMdnByProfileMdn(String, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY:Get BaseMdn for profile- ", KnGDPRTemplate.mdn(mdn));
        List<String> listOfBaseMdn = null;
        try {
            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            List<String> profileMdnList = new ArrayList<>();
            profileMdnList.add(mdn);
            listOfBaseMdn = provXDMServerDAO.getBaseMdnByProfileMdn(profileMdnList, persisterTxn);
            knLogger.debug(methodName, "Get BaseMdn for profile-", listOfBaseMdn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred : ", e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", e);
        }
        return listOfBaseMdn.get(0);
    }

    /**
     * method to prepare the userLogin response for Lock Request denied error
     *
     * @param KnOPSubsProfileInfoDTO subscriberDTO
     * @param KnXDMActivateInfoDTO   activateInfoDTO
     * @return KnUserLoginResponseDTO
     * @throws KnProvException entity Exception
     */
    @Override
    public KnUserLoginResponseDTO lockRequestErrorProcessor(KnOPSubsProfileInfoDTO subscriberDTO, KnXDMActivateInfoDTO activateInfoDTO,KnPersisterTxn persisterTxn) throws KnProvException {
        String methodName = "lockRequestErrorProcessor()";
        knLogger.info(methodName, "ENTRY: - " ,"subsProfileInfoDTO" ,KnGDPRTemplate.mdn(subscriberDTO.getMdn()),"activateInfoDTO",KnGDPRTemplate.mdn(activateInfoDTO.getMdn()));
        KnUserLoginResponseDTO responseDTO = new KnUserLoginResponseDTO();
        String apnName = subscriberDTO.getApnName();
        String mdn = subscriberDTO.getMdn();
        String cbPtxUri = null;
        String authServUri = null;
        String authServWifiUri = null;
        int clientPVMajorVersion = 0;
        String jwtToken = null;
        String protocolVersion = "";
        String xcapRootUri=null;
        try {
            xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
            // fetch XDM service config for deriving the XCAPURI
            KnXDMSServiceConfigDTO xdmSvcConfigDTO = genInfoUtil.retrieveXDMSServiceConfig(xdmPttServerId, null);
            if (apnName == null || apnName.equalsIgnoreCase("UNKNOWN")) {
                apnName = genInfoUtil.getDefaultAPNName(persisterTxn);
            }
            // verify apn exists or not
            Integer apnId = genInfoUtil.getAPNId(apnName, persisterTxn);
            knLogger.info(methodName, " APNID ", apnId, "APNNAME:", subscriberDTO.getApnName());
            if (apnId == null) {
                knLogger.error(methodName, " APN not exists , APN_INFO_NOT_FOUND ");
                throw new KnProvBOException(KnErrorCodes.BOEntity.APN_INFO_NOT_FOUND, " APN_INFO_NOT_FOUND ");
            }
            // retriev the APN COntiguration paramters
            KnAPNConfigDTO knAPNConfigDTO = genInfoUtil.retrieveAPNInfoConfig(persisterTxn, apnId);
            int clusterId = Integer.parseInt(System.getenv(com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME));
            // fetch the microservices cluster config for deriving the PTT and PTX bucket URIs
            KnMicroSvcsClusterInfo clusterInfo = genInfoUtil.retrieveMSSvcsClusterConfig(clusterId, persisterTxn);
            // Fetch the microservices common config for deriving the xcap mobile sync flag
            Map<String, String> msCommonConfigMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
            if (clusterInfo != null) {
                cbPtxUri = clusterInfo.getPtxBucketUriwifi();
                xcapRootUri = genInfoUtil.getXCAPRootURI(subscriberDTO.getMdn(), persisterTxn);
                authServUri = xcapRootUri.replace(com.kodiak.common.resources.KnConstants.XCAP_ROOT_CONTEXT, com.kodiak.common.resources.KnConstants.MS_CBAUTH_ROOT_CONTEXT);
                if (clientPVMajorVersion >= KnConstants.PROTOCOL_VERSION_13_X) {
                    authServUri = xcapRootUri.replace(com.kodiak.common.resources.KnConstants.OIDC_XCAP_ROOT_CONTEXT, com.kodiak.common.resources.KnConstants.MS_CBAUTH_ROOT_CONTEXT);
                }
                authServWifiUri = xdmSvcConfigDTO.getXcapRootUri_Wifi().replace(com.kodiak.common.resources.KnConstants.XCAP_ROOT_CONTEXT, com.kodiak.common.resources.KnConstants.MS_CBAUTH_ROOT_CONTEXT);
            }
            Map<String, String> paramNameValueMap = genInfoUtil.retrieveRTXConfigValues(null);

            knLogger.debug(methodName, "jwtUtil instance ", jwtUtil, "if not null generate the token");
            if (jwtUtil != null) {
                //rqPOC_MapServer_Deployment_19 : XDMS token generation
                KnTokenInfoDTO tokenInfoDTO = new KnTokenInfoDTO();
                tokenInfoDTO.setSubject(activateInfoDTO.getMdn());
                tokenInfoDTO.setUserName(activateInfoDTO.getMdn());
                tokenInfoDTO.setIssuer(JWT_ISSUER);
                tokenInfoDTO.setServiceType(JWT_SERVICE_TYPE);
                tokenInfoDTO.setExp(new Date(System.currentTimeMillis() + (jwtexpiryTime * MILLI_SECONDS)));
                jwtToken = jwtUtil.generateJWEToken(tokenInfoDTO);
                knLogger.debug(methodName, "token generated for LRT -", jwtToken);
            }
            IXDMServerDAO xdmDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
            KnXDMSServiceConfigDTO xdmServiceConfig = xdmDAO.retrieveXDMSServiceConfig(null);
            KnAPNProfileInfoDTO apnProfileInfoDTO = xdmDAO.retrieveAPNProfileInfo(xdmPttServerId, apnId, persisterTxn);
            KnUserAgentDTO userAgentDTO = activateInfoDTO.getUserAgentDTO();
            knLogger.debug(methodName, "userAgentDTO :", userAgentDTO);
            clientPVMajorVersion = 0;
            if (userAgentDTO != null) {
                protocolVersion = userAgentDTO.getProtocolVersion();
                if (protocolVersion != null) {
                    String[] pv = protocolVersion.split("\\.");
                    if (pv != null && pv[0] != null) {
                        subscriberDTO.setClientPVmajorVer(Integer.parseInt(pv[0]));
                        clientPVMajorVersion = Integer.parseInt(pv[0]);
                    }

                    if (pv != null && pv[1] != null) {
                        subscriberDTO.setClientPVminorVer(Integer.parseInt(pv[1]));
                    }
                }
            }

            // sending the response to the client
            responseDTO.setMdn(subscriberDTO.getMdn());
            responseDTO.setXcapRootUri(xcapRootUri);
            responseDTO.setXcapRootUriWifi(xdmSvcConfigDTO.getXcapRootUri_Wifi().replace(com.kodiak.common.resources.KnConstants.XCAP_ROOT_CONTEXT, com.kodiak.common.resources.KnConstants.OIDC_XCAP_ROOT_CONTEXT));
            responseDTO.setXui(provInfoUtil.generateXUI(subscriberDTO.getMdn()));
            responseDTO.setActiveFS1(KnGeneralUtil.convertHexStringToLong(subscriberDTO.getActiveFS2()));
            responseDTO.setActiveFS2(subscriberDTO.getActiveFS2());
            responseDTO.setApn(apnName);
            responseDTO.setSgwRUriC(knAPNConfigDTO.getPtxBucketUri());
            responseDTO.setSgwRUriW(cbPtxUri);
            responseDTO.setAuthUriC(authServUri);
            responseDTO.setAuthUriW(authServWifiUri);
            responseDTO.setCbBI(msCommonConfigMap.get(com.kodiak.common.resources.KnConstants.SYNCBUCKETINFO));
            responseDTO.setSgwAM(msCommonConfigMap.get(com.kodiak.common.resources.KnConstants.SGWAUTHMETHOD));
            responseDTO.setIpVC(Integer.parseInt(paramNameValueMap.get(KnConstants.IP_VER_CELLULAR)));
            responseDTO.setIpVPrefC(Integer.parseInt(paramNameValueMap.get(KnConstants.IP_PREF_ON_CELL_INTF)));
            responseDTO.setIpVPrefW(Integer.parseInt(paramNameValueMap.get(KnConstants.IP_PREF_ON_WIFI_INTF)));
            if (clientPVMajorVersion >= com.kodiak.common.resources.KnConstants.PROTOCOL_VERSION_29) {
                responseDTO.setIpVMulti(Integer.parseInt(paramNameValueMap.get(KnConstants.IP_VER_MULTICAST)));
                responseDTO.setIpVPrefMulti(Integer.parseInt(paramNameValueMap.get(KnConstants.IP_PREF_MULTICAST)));
            }
            responseDTO.setSgwLUriC(knAPNConfigDTO.getLocDataUriCellular());
            responseDTO.setSgwLUriW(clusterInfo.getLocDataUriWifi());
            responseDTO.setToken(jwtToken);
            responseDTO.setSubsClientType(subscriberDTO.getSubsClientType());
            responseDTO.setKmsUri(apnProfileInfoDTO.getKmsUri());
            responseDTO.setKmsUriWifi(xdmServiceConfig.getKmsUriWifi());
            responseDTO.setMcsXui(subscriberDTO.getMcId());
            if (clientPVMajorVersion >= KnConstants.PROTOCOL_VERSION_18_X) {
                responseDTO.setMcsXcapRootUri(apnProfileInfoDTO.getMcsXcapRootUri() + MCSXCAP_XCAP_ROOT_CONTEXT);
                responseDTO.setMcsXcapRootUriWifi(xdmServiceConfig.getMcsXcapRootUriWifi() + MCSXCAP_XCAP_ROOT_CONTEXT);
                responseDTO.setKmsUri(apnProfileInfoDTO.getKmsUri());
                responseDTO.setKmsUriWifi(xdmServiceConfig.getKmsUriWifi());
                responseDTO.setMcsXui(subscriberDTO.getMcId());
            }
            if (clientPVMajorVersion >= KnConstants.PROTOCOL_VERSION_20) {
                responseDTO.setMdsiUri(msCommonConfigMap.get(com.kodiak.common.resources.KnConstants.MCXDOMAINID));
                responseDTO.setGmsUri(msCommonConfigMap.get(com.kodiak.common.resources.KnConstants.GMSDOMAINID));
            }

        } catch (KnBOException e) {
            knLogger.error(methodName, "BO Exception occurred :", e);
            throw new KnProvBOException(e.getErrorCode(), e.getErrorMessage(), e);
        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occurred : ", ex);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while prepare response for Lock Request Error");
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred while while prepare response for LRT", e);
        }
        knLogger.info(methodName, "EXIT: lockRequestErrorProcessor ", responseDTO);
        return responseDTO;
    }

    public String processLocationEnabledCorporateJob(KnLocationEnabledCorporateSubscriberDetails subsProfileInfoDTO) throws KnFeatureSetException {
        String methodName = "processLocationEnabledJobController";
        String newactiveFS2 = null;
        try {
            KnGenInfoUtil genInfoUtil = KnGenInfoUtil.getInstance();
            String xdmsFS2FromDB = subsProfileInfoDTO.getXdmFS2();
            String clientFS2 = subsProfileInfoDTO.getClientFS2();
            String subsFS2 = subsProfileInfoDTO.getSubscriberFS2();
            String corpFS2 = genInfoUtil.getCorpFS2ForCorpId(subsProfileInfoDTO.getCorpId());
            String opsFS2 = subsProfileInfoDTO.getOpsFS2();
            String corpAdminFS2 = subsProfileInfoDTO.getCorpAdminFS2();
            String userProfileFS2 = subsProfileInfoDTO.getUserProfileFS2();
            String xdmServerId = genInfoUtil.retrieveLocalXDMPttServerId();
            int protocolVersion = subsProfileInfoDTO.getProtocolVersion();
            String clientCapOverrideBitMask = featureSetUtil.getClientCapabilityBitMask(xdmServerId, protocolVersion);
            String updatedXDMFS2 = null;
            Map<Integer, Integer> enableLocationBitMap = new HashMap<>();
            enableLocationBitMap.put(com.kodiak.common.resources.KnConstants.FEATURE_SET.ONDEMLOCATION.value(), 1);
            updatedXDMFS2 = KnGeneralUtil.updateFeatureBit(xdmsFS2FromDB, enableLocationBitMap);
            if (!xdmsFS2FromDB.equals(updatedXDMFS2)) {
                knLogger.debug(methodName, "XDMFS2 differs");
                newactiveFS2 = featureSetUtil.generateActiveFeatBitSet(subsProfileInfoDTO.getPocHome(), subsProfileInfoDTO.getPresenceHome(), xdmServerId,
                        clientFS2, subsFS2, corpFS2, opsFS2, corpAdminFS2, clientCapOverrideBitMask, updatedXDMFS2, userProfileFS2);
                genInfoUtil.updateActiveFS2andXDMSFS2InDB(subsProfileInfoDTO.getMdn(), newactiveFS2, updatedXDMFS2);
            }
        } catch (Exception e) {
            knLogger.debug("Exception thrown:", e);
        }
        return newactiveFS2;
    }

    public KnLocationEnabledCorporateSubscriberDetails getLocationEnabledCorporateSubscriberDetails(String mdn, KnPersisterTxn persisterTxn) {
        String methodName = "getLocationEnabledSubsDetails";
        KnLocationEnabledCorporateSubscriberDetails subsDetails = new KnLocationEnabledCorporateSubscriberDetails();
        KnGenInfoUtil genInfoUtil = KnGenInfoUtil.getInstance();
        try {
            subsDetails = genInfoUtil.getLocationEnabledCorporateSubscriberDetails(mdn, persisterTxn);
        } catch (Exception e) {
            knLogger.debug(methodName, "Exception in getOrchestrateSubsDetails::", e);
        }
        return subsDetails;
    }


    @Override
    public Map<String, Integer> getSubscriberServiceAuthStatus(List<String> mdns, KnPersisterTxn persisterTxn) throws KnDAOException, KnProvException {
        String methodName = "getSubscriberServiceAuthStatus(String, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY:Get Subscriber servicre auth status for mdn - ", mdns != null ? KnGDPRTemplate.mdnList(mdns).size() : 0);
        Map<String, Integer> stringIntegerMap;
        try {
            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            stringIntegerMap = provXDMServerDAO.getSubscriberServiceAuthStatus(mdns, persisterTxn);
            knLogger.debug(methodName, "Get service auth status for mdn-", mdns);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred : ", e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", e);
        }
        return stringIntegerMap;
    }

    @Override
    public int getSubscriberServiceAuthStatusByUserId(String userId, KnPersisterTxn persisterTxn) throws KnDAOException, KnProvException {
        String methodName = "getSubscriberServiceAuthStatusByUserId(String, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY:Get Subscriber servicre auth status for userId - ", KnGDPRTemplate.userId(userId));
        int stringIntegerMap;
        try {
            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            stringIntegerMap = provXDMServerDAO.retrieveMdnServiceAuthStatusByUserId(userId, persisterTxn);
            knLogger.debug(methodName, "Get service auth status for userId-", userId);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred : ", e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", e);
        }
        return stringIntegerMap;
    }

    private String retrieveXdmPttServerId(String methodName) throws KnProvBOException {
        try {
            return genInfoUtil.retrieveLocalXDMPttServerId();
        } catch (KnBOException ex) {
            knLogger.error(methodName, "Failed to retrieve XDM PTT Server ID.", ex);
            throw new KnProvBOException(KnErrorCodes.BOEntity.XDMS_PTT_ID_NOT_FOUND,
                    "Failed to retrieve XDM PTT Server ID.", ex);
        }
    }
    public KnXDMExtGWProfileListDTO retrieveExtGWProfileList() throws KnDAOException, KnProvException {
        String methodName = "retrieveExtGWProfileList()";
        boolean ownedTxn = false;
        KnXDMExtGWProfileListDTO responseDTO = new KnXDMExtGWProfileListDTO();
        knLogger.debug(methodName, "retrieveExtGWProfileList req ");
        try {
            xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
            responseDTO = provInfoUtil.retrieveExtGWProfileList();
        } catch (KnBOException e) {
            knLogger.error(methodName, "failed to retrieve xdm Ptt Sever Id", e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.XDMS_PTT_ID_NOT_FOUND, "Failed to retrieve XDM PTT Server ID", e);
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while retrieveExtGWProfileList" + e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred while retrieveExtGWProfileList", e);
        }
        knLogger.debug(methodName, "EXIT:  retrieveExtGWProfileList - ", responseDTO);
        return responseDTO;
    }

}