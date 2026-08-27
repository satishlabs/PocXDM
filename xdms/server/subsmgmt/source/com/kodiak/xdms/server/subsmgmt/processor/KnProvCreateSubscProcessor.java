/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.subsmgmt.processor;

import com.kodiak.common.commdto.common.KnXDMDeviceProvDTO;
import com.kodiak.common.commdto.request.KnXDMSubsAliasDetailsReqDTO;
import com.kodiak.common.commdto.response.KnXDMSubsAliasDetailsRespDTO;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnDbUtil;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.ggcache.dto.KnDefaultMCSClientInfo;
import com.kodiak.common.resources.*;
import com.kodiak.frameworks.confignotifier.watcher.KnConfigWatcherUtil;
import com.kodiak.frameworks.statisticalmgr.KnOMConstants;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.featureset.KnFeatureSetException;
import com.kodiak.utilities.featureset.KnFeatureSetUtil;
import com.kodiak.utilities.featuresetupgrade.util.KnUpgardeFS;
import com.kodiak.utilities.featuresetupgrade.util.KnUpgardeFSConfig;
import com.kodiak.utilities.generatealarmutil.KnAlarmConstants;
import com.kodiak.utilities.generatealarmutil.KnAlarmGeneratorUtil;
import com.kodiak.utilities.syncgateway.KnManageSyncUserProfileUtil;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dao.KnFactorySelector;
import com.kodiak.xdms.server.common.dao.persister.IXDMServerDAO;
import com.kodiak.xdms.server.common.dao.persister.db.KnDBConnectionException;
import com.kodiak.xdms.server.common.dto.common.KnPOCSvcConfigDTO;
import com.kodiak.xdms.server.common.dto.common.KnXDMSServiceConfigDTO;
import com.kodiak.xdms.server.common.dto.persistdat.KnContactListPersistDTO;
import com.kodiak.xdms.server.common.dto.persistdat.KnDeviceImpiInfoPersistDTO;
import com.kodiak.xdms.server.common.dto.persistdat.KnDeviceInfoPersistDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorFramework;
import com.kodiak.xdms.server.common.resources.KnConstants;
import com.kodiak.xdms.server.subsmgmt.business.KnProvBOException;
import com.kodiak.xdms.server.subsmgmt.business.helper.KnProvInfoUtil;
import com.kodiak.xdms.server.subsmgmt.business.helper.KnProvSMSUtil;
import com.kodiak.xdms.server.subsmgmt.business.impl.KnSubsProvController;
import com.kodiak.xdms.server.subsmgmt.dao.KnProvFactorySelector;
import com.kodiak.xdms.server.subsmgmt.dao.persister.IProvXDMServerDAO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.*;
import com.kodiak.xdms.server.subsmgmt.dto.common.KnProvSMSDTO;
import com.kodiak.xdms.server.subsmgmt.dto.common.KnSubsAliasInfoDTO;
import com.kodiak.xdms.server.subsmgmt.dto.persistdat.*;
import com.kodiak.xdms.server.subsmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvConstants;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvUtil;
import com.kodiak.ems.base.itf.KnEMSConst;
import com.kodiak.ems.base.utils.KnLicenseInfo;

import static com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

import static com.kodiak.common.dao.KnDbUtil.rollback;
import static com.kodiak.common.resources.KnConstants.*;
import static com.kodiak.xdms.server.common.dao.persister.db.tables.xdm.KnXDMCBUserProfileMgmtDAO.knLogger;
import static com.kodiak.xdms.server.common.resources.KnConstants.*;
import static com.kodiak.xdms.server.common.resources.KnConstants.AWARE_CLIENTS;
import static com.kodiak.xdms.server.subsmgmt.resources.KnSubConfigConstants.POCHOME_AUTOASSIGN_FLAG;

public class KnProvCreateSubscProcessor {

    private static final KnLogger knLogger = KnLogger.getLogger(KnProvCreateSubscProcessor.class);
    private KnGenInfoUtil genInfoUtil = KnGenInfoUtil.getInstance();
    private KnFeatureSetUtil featureSetUtil = KnFeatureSetUtil.getInstance();

    private KnValidatorFramework validatorFwk = KnValidatorFramework.getInstance(KnProvConstants.LIBRARY_NAME);

    private KnProvInfoUtil provInfoUtil = new KnProvInfoUtil();

    private static final String currentFsVersion= KnUpgardeFSConfig.getFsCurrentVersion();

    KnProvSMSUtil provSMSUtil = new KnProvSMSUtil();

    private KnGeneralCacheUtil  generalCacheUtil = KnGeneralCacheUtil.getInstance();

    private KnLicenseInfo licenseInfo = KnEMSConst.getLicenseInfo();

    private KnGeneralPasswordUtil pwdUtil = KnGeneralPasswordUtil.getInstance();


    public KnOPCreateSubsInfoDTO processCreateSub(KnIPSubsProvInfoDTO subsProvInputDTO, KnPersisterTxn persisterTxn) throws KnProvBOException, KnValidationException {
        String methodName = "createSubscriber(KnSubsProfilePersistDTO, KnPersisterTxn)";
        KnSubsProfilePersistDTO subsProvPersistDTO;
        boolean ownedTxn = false;
        KnOPCreateSubsInfoDTO respDTO = new KnOPCreateSubsInfoDTO();
        knLogger.info(methodName, "ENTRY: Txn - ", persisterTxn);
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
            String ufmi = subsProvInputDTO.getUfmi();
            Integer subsClientType = subsProvInputDTO.getSubsClientType();
            String userId = subsProvInputDTO.getUserId();
            respDTO.setXdmsHome(xdmPttServerId);

            if (ufmi != null) {
                KnOPSubsProfileInfoDTO subsProfileInfoDTO = provInfoUtil.retrieveSubscriberInfoForUFMI(ufmi, persisterTxn);
                if (subsProfileInfoDTO != null) {
                    throw new KnProvBOException(KnErrorCodes.BOEntity.DUPLICATE_UFMI, "UFMI already Exists");
                }
            }
            String tierPackageId = null;
            Map<String, Integer> addonPackageIds = new HashMap<>();
            Map<String, Integer> addPkgIds = new HashMap<String, Integer>();
            if (subsProvInputDTO.getPkgIdMap() != null
                    && subsProvInputDTO.getPkgIdMap().get(KnConstants.ADD_ACTION) != null) {
                addPkgIds = subsProvInputDTO.getPkgIdMap().get(KnConstants.ADD_ACTION);
                for (Map.Entry<String, Integer> entry : addPkgIds.entrySet()) {
                    if (entry.getValue().intValue() == KnConstants.TIER_PKG_TYPE.intValue()) {
                        tierPackageId = entry.getKey();
                    } else if (entry.getValue().intValue() == KnConstants.ADDON_PKG_TYPE.intValue()) {
                        addonPackageIds.put(entry.getKey(), entry.getValue());
                    }
                }
            }
            knLogger.info(methodName, "addPkgIds map ", addPkgIds);
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

            if (subsProvInputDTO.getAliasInfoList() != null) {
                List<KnSubsAliasInfoDTO> alisasInfoList = subsProvInputDTO.getAliasInfoList();
                Map<String, String> aliasIdIssuerMap = new HashMap<>();
                for (KnSubsAliasInfoDTO aliasInfoDTO : alisasInfoList) {
                    aliasIdIssuerMap.put(aliasInfoDTO.getAliasId(), aliasInfoDTO.getAliasIdIssuer());
                }
                Map<String, List<KnSubsAliasInfoDTO>> subsAliasIdInfoMap = provXDMServerDAO.selectSubsAliasIdInfo(aliasIdIssuerMap, false, persisterTxn);
                if (subsAliasIdInfoMap != null && !subsAliasIdInfoMap.isEmpty()) {
                    throw new KnProvBOException(KnErrorCodes.BOEntity.DUPLICATE_ALIAS_INFO_NOT_ALLOWED, "Duplicate info present");
                }
            }

            //Authenticate the request
            knLogger.debug(methodName, "No Authorisation is being performed");

            //populate the Subscriber Prov Persist DTO.
            subsProvPersistDTO = new KnSubsProfilePersistDTO();
            subsProvPersistDTO.setInputDTO(subsProvInputDTO);
            subsProvPersistDTO.setHierarchyType(subsProvInputDTO.getHierarchyType());
            subsProvPersistDTO.setExtGatewayId(subsProvInputDTO.getExtGatewayId());
            subsProvPersistDTO.setMdn(mdn);
            subsProvPersistDTO.setXDMSHome(xdmPttServerId);
            subsProvPersistDTO.setIMEI(subsProvInputDTO.getIMEI());
            subsProvPersistDTO.setProvFSMap(subsProvInputDTO.getProvFSMap());
            subsProvPersistDTO.setFirstNetIndicator(subsProvInputDTO.getFirstNetIndicator());
            String networkName = subsProvInputDTO.getNetworkName();
            knLogger.debug(methodName, "subsProvInputDTO :", subsProvInputDTO);

            Map<String, String> paramNameValueMap = genInfoUtil.retrieveRTXConfigValues(persisterTxn);
            if (null == networkName || networkName.isEmpty()) {
                // as per FRS
                if (KnProvConstants.SUBS_CLIENT_TYPE.POC_NNI_Group_MDN.value() == subsClientType)
                    networkName = paramNameValueMap.get(KnConstants.XDMS_LMR_SUB_DEFAULT_NAME);
                else
                    networkName = mdn;
            }

            knLogger.debug(methodName, "network name :: ", KnGDPRTemplate.name(networkName));

            subsProvPersistDTO.setNetworkName(networkName);

            knLogger.debug(methodName, "subsProvPersistDTO :: ", subsProvPersistDTO);

            knLogger.debug(methodName, "subsClientType :", subsClientType);
            knLogger.debug(methodName, "subsProvInputDTO :", subsProvInputDTO);

            //9.0 onwards as per User check, discreet and ambient listening feature new params serviced_status_op/authUser are introduced
            //these new param need to set there default values
            //Default value for service status op - provisioned (0)
            // service auth user - activated(2)

            int serviceStatusOp = 0;
            int serviceStatusAuthUser = 2;
            KnDefaultMCSClientInfo defaultMCSClientInfo = null;

            // Retrieve multisite deployment flag value to set service auth status for subscriber creation
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
            } else {
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
            //calculating service auth status as per new rule User check ambient feature
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
            if (iDenInterOp == KnProvConstants.IDEN_INTEROP.DISABLED.value() && ufmi != null) {
                knLogger.error(methodName, "IDenInterOp is disabled");
                throw new KnProvBOException(KnErrorCodes.BOEntity.IDEN_INTEROP_DISABLED, "iDen InterOp is disabled");
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

                if (subsProvPersistDTO.getSubsDefPttRadio() != com.kodiak.common.resources.KnConstants.SUBSCR_DEF_PTTRADIO_ENABLED) {
                    knLogger.debug(methodName, "SUBSCR_DEF_PTTRADIO is disabled proceeding to check system & corp level ");
                    int subsDef = genInfoUtil.retrieveXDMSServiceConfig(xdmPttServerId, persisterTxn).getSubsDefPttRadio();
                    //check system level SUBSCR_DEF_PTTRADIO value
                    if (subsDef == com.kodiak.common.resources.KnConstants.SUBSCR_DEF_PTTRADIO_ENABLED) {
                        knLogger.debug(methodName, "system level SUBSCR_DEF_PTTRADIO is enabled skipping corp level check");
                        subsProvPersistDTO.setSubsDefPttRadio(subsDef);
                    } else if (corpProfileInfoDTO.getCorpId() > 0 && corpProfileInfoDTO.getSubsDefPttRadio() == com.kodiak.common.resources.KnConstants.SUBSCR_DEF_PTTRADIO_ENABLED) {
                        knLogger.debug(methodName, "corp level SUBSCR_DEF_PTTRADIO is enabled");
                        subsProvPersistDTO.setSubsDefPttRadio(com.kodiak.common.resources.KnConstants.SUBSCR_DEF_PTTRADIO_ENABLED);
                    }
                }
                if (subsProvPersistDTO.getSubsDefPttRadio() == com.kodiak.common.resources.KnConstants.SUBSCR_DEF_PTTRADIO_ENABLED) {

                    knLogger.debug(methodName, "Converting regular client type ", subsProvPersistDTO.getSubsClientType(), " to  PTTRADIO Client type");
                    switch (subsProvPersistDTO.getSubsClientType()) {
                        case 1:
                            subsClientType = KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value();
                            break;
                        case 5:
                            subsClientType = KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value();
                            break;
                        case 10:
                            subsClientType = KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value();
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
            knLogger.info(methodName, "deviceSharingFlag", deviceSharingFlag);
            int deviceSharewifiFlag = Integer.parseInt(microServicesParamNameValueMap.get(AUTO_DEVICESHARE_WIFI_CC));
            int licenseType = 0;
            knLogger.info(methodName, "deviceSharewifiFlag-->", deviceSharewifiFlag);
            if ((subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.POC_WIFIONLY.value()
                    || subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.CROSS_CARRIER_PTT_CLIENT.value()
                    || subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value()
                    || subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value()) && Integer.parseInt(deviceSharingFlag) == ENABLED
                    && deviceSharewifiFlag == ENABLED) {
                knLogger.debug(methodName, "license type for wifi , crosscarrier and its ptt ");
                subsProvPersistDTO.setLicenseType(KnConstants.USER_LICENSE_TYPE);
            } else {
                knLogger.debug("setting the else val" + subsProvInputDTO.getLicenseType());
                subsProvPersistDTO.setLicenseType(subsProvInputDTO.getLicenseType());
            }

            licenseType = subsProvPersistDTO.getLicenseType();
            knLogger.debug(methodName, "licenseType -", licenseType);
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
                respDTO.setIsExistingCorp(corpId);
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
                    provXDMServerDAO.updateCorporateProfile(corpProfilePersistDTO,persisterTxn);

                    subsProvPersistDTO.setCorpId(corpId);
                    opsCorpFS2 = corpProfileInfoDTO.getOpsCorpFS2();
                    corpFS2 = corpProfileInfoDTO.getCorpFS2();

                    if ((KnProvConstants.SUBS_CLIENT_TYPE.DISPATCH_CLIENT.value() == subsClientType) &&
                            (KnProvConstants.WEB_DISPATCH_ENABLED == svcDispatchType || KnProvConstants.WEB_DISPATCH_ENABLED == corpDispatchType)) {
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
                    if(!KnProvConstants.POC_HOME_NOT_ASSIGNED.equals(poCPttServerId)){
                        KnPOCSvcConfigDTO knPOCSvcConfigDTO = provInfoUtil.retrievePOCSvcConfig(poCPttServerId);
                        corpProfilePersistDTO.setDynamicQosFlag(knPOCSvcConfigDTO.getDynamicQosFlag());
                    }else{
                        // if POC Home is not assigned set Dynamic Qos flag to 0
                        corpProfilePersistDTO.setDynamicQosFlag(0);
                    }

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
                    if ((KnProvConstants.SUBS_CLIENT_TYPE.DISPATCH_CLIENT.value() == subsClientType) &&
                            (KnProvConstants.WEB_DISPATCH_ENABLED == svcDispatchType)) {
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
            knLogger.debug(methodName, " subsProvPersistDTO's   ProvFS :", provFSMap);
            //same values of provFS1 and provFS1BitMask is used for calculation final subsFS after oring with bitmask
            String provFS2 = subsFS2;
            String provFS2BitMask = subsFS2;

            knLogger.debug(methodName, " createSubscriber's   ProvFS :", provFSMap);
            if (provFSMap != null && !provFSMap.isEmpty()) {
                if (isProvFSBitAllowed(provFSMap)) {
                    bitset = featureSetUtil.convertHexStringToBitSet(subsFS2);

                    for (Map.Entry<Integer, Integer> provEntry : provFSMap.entrySet()) {
                        if (provEntry.getValue() == com.kodiak.common.resources.KnConstants.PROVFS_ENABLE.ENABLED.value()) {
                            bitset.set(provEntry.getKey());
                        } else {
                            bitset.clear(provEntry.getKey());
                        }
                    }
                    subsFS2 = featureSetUtil.convertBitSetToHexString(bitset);

                    provFS2 = subsFS2;
                    knLogger.debug(methodName, "  provFS2 ", provFS2, "bitset :", bitset);
                    provFS2BitMask = provFS2;
                    knLogger.debug(methodName, "provFS2BitMask :", provFS2BitMask);
                } else {
                    knLogger.error(methodName, "Operation not allowed for feature bit(s)");
                    throw new KnProvBOException(KnErrorCodes.Validator.INVALID_PROV_FS, "Requested feature bit(s) is/are not allowed to enable/disable via create/update.");
                }
            }
            knLogger.debug(methodName, "subsFS2 :", subsFS2, "provFS2 :", provFS2, "provFS2BitMask :", provFS2BitMask);
            //Generate the SubsFeatureSet by performing the BitMask with License SubsFSBITMASK
            subsFS2 = featureSetUtil.generateSubsFeatureSet(subsFS2, provFS2, provFS2BitMask);
            knLogger.debug(methodName, "final subsFS : ", subsFS2);

            //get final ClientFeatureSet
            int defaultClientPVMajorVersion = 1;

            if (KnConstants.MCSCOMPLIANCE == subsProvInputDTO.getMcsCompliance()) {
                defaultClientPVMajorVersion = defaultMCSClientInfo.getPv();
            }

            String clientCapOverrideBitMask = featureSetUtil.getClientCapabilityBitMask(xdmPttServerId, defaultClientPVMajorVersion);
            knLogger.debug(methodName, "defaultClientPVMajorVersion ", defaultClientPVMajorVersion, " ,clientCapOverrideBitMask=", clientCapOverrideBitMask);
            String clientFS2 = featureSetUtil.getDefFinalClientFS(clientCapOverrideBitMask);

            if (KnConstants.MCSCOMPLIANCE == subsProvInputDTO.getMcsCompliance()) {
                clientFS2 = defaultMCSClientInfo.getClientFS2();
            }
            String opsFS2 = null;
            String corpAdminFS2 = featureSetUtil.getDefFinalCorpAdminFS();
            //Generate the ActiveFeatureSet
            String activeFS2 = null;
            String xdmsFs2 = featureSetUtil.getDefFinalXdmsFS();
            String userProfileFS2 = featureSetUtil.getDefFinalUserProfileFS();
            knLogger.debug("poCPttServerId::",poCPttServerId,"presencePttServerId::",presencePttServerId,"xdmPttServerId::",xdmPttServerId);
            if (corporateSubscriptionType == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
                opsFS2 = opsCorpFS2;
                if(poCPttServerId.equals(String.valueOf(0)) && presencePttServerId.equals(String.valueOf(0))){
                    poCPttServerId = xdmPttServerId;
                    presencePttServerId = xdmPttServerId;
                }
                activeFS2 = featureSetUtil.generateActiveFeatBitSet(poCPttServerId, presencePttServerId, xdmPttServerId,
                        clientFS2, subsFS2, opsFS2, opsCorpFS2, corpAdminFS2, clientCapOverrideBitMask, xdmsFs2, userProfileFS2);
            } else {
                //get final OpsFeatureSet
                opsFS2 = featureSetUtil.getDefFinalOpsFS();
                knLogger.debug(methodName, "final opsFS : ", opsFS2);
                if(poCPttServerId.equals(String.valueOf(0)) && presencePttServerId.equals(String.valueOf(0))){
                    poCPttServerId = xdmPttServerId;
                    presencePttServerId = xdmPttServerId;
                }
                activeFS2 = featureSetUtil.generateActiveFeatBitSet(poCPttServerId, presencePttServerId, xdmPttServerId,
                        clientFS2, subsFS2, opsFS2, clientCapOverrideBitMask, xdmsFs2, userProfileFS2);

            }
            //if MCPTT_COMPLAIANCE_BIT=81 is disabled then add to ASYNC_JOB_NOTIFY,if change is there
            if (KnConstants.MCSCOMPLIANCE != subsProvInputDTO.getMcsCompliance() && (subsProvInputDTO.getMcId() != null
                    || subsProvInputDTO.getMcDataId() != null || subsProvInputDTO.getMcpttId() != null
                    || subsProvInputDTO.getMcVideoId() != null)) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.MCXIDS_NOT_ALLOWED_FOR_KODIAK_CLIENTS,
                        "MCX Id(s) are not allowed for kodiak clients.");
            }
            if (subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.STANDALONECAMERA.value() && subsProvInputDTO.getCameraType() == com.kodiak.common.resources.KnConstants.CAMERA_TYPE.RE4.value()) {
                subsProvPersistDTO.setCameraType(subsProvInputDTO.getCameraType());
                String activatedClientFS2 = generalCacheUtil.getActivatedClientFS2(subsProvInputDTO.getCameraType(), subsProvInputDTO.getSubsClientType());
                if (activatedClientFS2 == null) {
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
                        clientFS2, subsFS2, opsFS2, clientCapOverrideBitMask, xdmsFs2, userProfileFS2);

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
            subsProvPersistDTO.setSubsFS2(subsFS2);

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
                corpLevelLocationFlag = KnGeneralUtil.getFeatureBitValue(xdmCorpFS2Set, XDMCORPFS2_SET.LOCATION_ENABLED.value());
            } else {
                knLogger.warn(methodName, "xdmCorpFS2Set is null");
            }
            String systemLevelFlag = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn).get(MASS_LOCATION_ENABLED);
            if(com.kodiak.common.resources.KnConstants.ENABLED_STRING.equals(systemLevelFlag)){
                updatedXdmsFs2 = featureSetUtil.getSetFeatureSetBits(updatedXdmsFs2, new int[]{com.kodiak.common.resources.KnConstants.FEATURE_SET.ONDEMLOCATION.value()});
                knLogger.info(methodName + " SystemLevelFlag Enabled", "updatedXdmsFs2: ", updatedXdmsFs2);
                subsProvPersistDTO.setXdmsFS2(updatedXdmsFs2);
            }
            else if (corpLevelLocationFlag) {
                //TO-DO --> update XDMSFS2
                BitSet xdmsFs2check = KnGeneralUtil.convertHexStringToBitSet(updatedXdmsFs2);
                knLogger.info(methodName, "xdmsFs2check: ", xdmsFs2check);
                xdmsFs2check.set(27, true);
                knLogger.info(methodName, "xdmsFs2check 01: ", xdmsFs2check);
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
            if ((String.valueOf(subsClientType)).matches(AWARE_CLIENTS)) {
                knLogger.debug(methodName, "Aware client, So not creating OIDC profile");
                awareClient = true;
            }
            subsProvPersistDTO.setFeatureRelVersion(currentFsVersion);

            knLogger.debug(methodName, "adding an entry into Subscriber Info");
            provXDMServerDAO.createSubscrProfile(subsProvPersistDTO, Boolean.FALSE, persisterTxn);
            if (subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.STANDALONECAMERA.value() && subsProvInputDTO.getCameraType() == CAMERA_TYPE.RE4.value() && subsProvInputDTO.getCameraInfo() != null) {
                subsProvPersistDTO.setCameraInfo(subsProvInputDTO.getCameraInfo());
                knLogger.debug(methodName, "adding an entry into subscriber Camera Info");
                provXDMServerDAO.createSubscriberCameraInfo(subsProvPersistDTO, persisterTxn);
            }
            if (subsProvInputDTO.getAliasInfoList() != null) {
                List<KnSubsAliasInfoDTO> aliasInfoList = subsProvInputDTO.getAliasInfoList();
                provXDMServerDAO.insertIntoSubsAliasId(aliasInfoList, mdn, persisterTxn);
            }
            boolean bitEnabled = KnGeneralUtil.getFeatureBitValue(subsProvPersistDTO.getSubsFS2(), FEATURE_SET.MCDEVICE.value());
            knLogger.debug(methodName, "awareClient :", awareClient
                    , " MCSCOMP: ", subsProvInputDTO.getMcsCompliance(), " licenseType:", licenseType, " bitEnabled: ", bitEnabled);
            //check for CRI and kodiak clients
            if ((KnConstants.MCSCOMPLIANCE == subsProvInputDTO.getMcsCompliance() && !awareClient)
                    || (KnConstants.KODIAK_CLIENT == subsProvInputDTO.getMcsCompliance() && KnConstants.USER_LICENSE_TYPE_DISABLED == licenseType && bitEnabled)) {

                KnXDMDeviceProvDTO deviceProfile = xdmServerDAO.selectDeviceProfileByDeviceId(mdn, persisterTxn);
                KnDeviceInfoPersistDTO deviceDTO = new KnDeviceInfoPersistDTO();
                deviceDTO.setDeviceId(mdn);
                xdmServerDAO.deleteDeviceImpiInfo(deviceDTO, persisterTxn);
                xdmServerDAO.deleteDeviceInfo(deviceDTO, persisterTxn);
                String appId = APP_ID.USERMCSCLIENTS.value();
                if ((bitEnabled || KnConstants.USER_LICENSE_TYPE == licenseType) && KnConstants.MCSCOMPLIANCE != subsProvInputDTO.getMcsCompliance()) {
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
                    deviceInfoPersistDTO.setCorpId(corpId);
                }
                //XMSSTP-873 - CRI client to support SIP URI format.
                if (KnConstants.MCSCOMPLIANCE == subsProvInputDTO.getMcsCompliance()) {
                    deviceInfoPersistDTO.setDeviceIMPI(subsProvInputDTO.getMcId());
                } else {
                    deviceInfoPersistDTO.setDeviceIMPI(KnConstants.TEL_URI_TEMPLATE + mdn);
                }
                if (bitEnabled && !(KnConstants.MCSCOMPLIANCE == subsProvInputDTO.getMcsCompliance())) {
                    if (deviceProfile != null) {
                        deviceInfoPersistDTO.setDeviceDigestPassword(deviceProfile.getDevicePassword());
                    } else {
                        deviceInfoPersistDTO.setDeviceDigestPassword(null);
                    }

                    deviceInfoPersistDTO.setDeviceType(DEVICE_TYPE.MC_DEVICE.Value());
                    deviceInfoPersistDTO.setDeviceName(null);
                    deviceInfoPersistDTO.setDeviceClientId(null);
                    deviceInfoPersistDTO.setDeviceshared(KnConstants.MCDEVICESHARED_TYPE.SHARED.Value());
                    deviceInfoPersistDTO.setReqDeviceId(mdn);
                } else {
                    if (deviceProfile != null) {
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
                if (KnConstants.MCSCOMPLIANCE == subsProvInputDTO.getMcsCompliance()) {
                    deviceImpiInfo.setDeviceImpi(subsProvInputDTO.getMcId());
                    deviceImpiInfo.setDeviceImpu(subsProvInputDTO.getMcId());
                } else {
                    deviceImpiInfo.setDeviceImpi(KnConstants.TEL_URI_TEMPLATE + mdn);
                    deviceImpiInfo.setDeviceImpu(KnConstants.TEL_URI_TEMPLATE + mdn);
                }
                xdmServerDAO.createDeviceImpiInfo(deviceImpiInfo, persisterTxn);
            }

                // create OIDC profile

            List<String> mdns = new ArrayList<>();
            mdns.add(mdn);
            if (KnProvConstants.SUBS_CLIENT_TYPE.POC_NNI_Alias_MDN.value() == subsClientType ||
                    KnProvConstants.SUBS_CLIENT_TYPE.POC_NNI_Group_MDN.value() == subsClientType) {
                provInfoUtil.createNNISubscriberProfile(mdns, subsClientType, persisterTxn);
            }

            if (extCorpId != null) {
                knLogger.debug(methodName, "Calling Update Etag for Corp = ", extCorpId);
                updateEtagForNNISubscr(extCorpId, persisterTxn);

            }


            // Interne APN changes ....
            String apnName = genInfoUtil.getDefaultAPNName(persisterTxn);
            if (KnConstants.MCSCOMPLIANCE == subsProvInputDTO.getMcsCompliance()) {
                apnName = KnConstants.DEFAULTAPNNAME;
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
                int mdnCount = xdmServerDAO.getMdnCount(mdn, persisterTxn);
                if (mdnCount < 1) {
                    // -->. creating entry into the XDM_CorpResourceListIndex Doc if not exists
                    knLogger.debug(methodName, "Adding an entry into the Corp resource List Index Doc");
                    xdmServerDAO.addMdnToCorpResourceListIndexDoc(mdn, persisterTxn);
                }

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
            } else if (subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.POC_NNI_Alias_MDN.value()) {
                successPegs.add(KnOMConstants.XDM_NUM_NNI_ALIAS_SUBSCRIBERS_CREATED);
            } else if (subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.POC_NNI_Group_MDN.value()) {
                successPegs.add(KnOMConstants.XDM_NUM_NNI_GROUP_SUBSCRIBERS_CREATED);
            } else if (subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.CROSS_CARRIER_PTT_CLIENT.value()) {
                successPegs.add(KnOMConstants.XDM_NUM_CROSS_CARRIER_PTT_CLIENTS_CREATED);
            } else if (subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value()) {
                successPegs.add(KnOMConstants.XDM_NUM_PTTRADIO_CROSSCARRIER_CLIENTS_CREATED);
            } else if (subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value()) {
                successPegs.add(KnOMConstants.XDM_NUM_PTTRADIO_HANDSET_CLIENTS_CREATED);
            } else if (subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value()) {
                successPegs.add(KnOMConstants.XDM_NUM_PTTRADIO_WIFIONLY_CLIENTS_CREATED);
            } else if (subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.SGMDNPATCH.value()) {
                successPegs.add(KnOMConstants.XDM_NUM_SG_MDN_PATCH_CLIENTS_CREATED);
            } else if (subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.DATAGROUPMDN.value()) {
                successPegs.add(KnOMConstants.XDM_NUM_SG_MDN_PATCH_CLIENTS_CREATED);
            }
            respDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.SUCCESS.value());
            respDTO.setResponseMessage(KnProvConstants.CREATE_SUBSCRIBER_SUCCESS);
            if (corporateSubscriptionType == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
                respDTO.setCorpId(corpId);
                respDTO.setCorpName(corpProfileInfoDTO.getCorporateName());
            }
            respDTO.setSuccessPegs(successPegs);
            if (activeFS2 != null) {
                respDTO.setActiveFS2(activeFS2);
                respDTO.setSubsFS2(subsFS2);
            }
            respDTO.setSubsFS2(subsProvPersistDTO.getSubsFS2());
            respDTO.setMcId(subsProvPersistDTO.getMcId());
            respDTO.setMcDataId(subsProvPersistDTO.getMcDataId());
            respDTO.setMcPttId(subsProvPersistDTO.getMcpttId());
            respDTO.setMcVideoId(subsProvPersistDTO.getMcVideoId());
            respDTO.setNetworkName(subsProvPersistDTO.getNetworkName());
            respDTO.setServiceAuthStatus(subsProvPersistDTO.getServiceAuthStatus());
            //welcome sms notification..
            //ptt client type -notification changes
            if (null != oneMessageEnabled && oneMessageEnabled.equals(ONE_MSG_STATUS.ENABLED.value())) {
                knLogger.debug(methodName, "One Message Is Disabled storing SMS into table");
                if (subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value() || subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value() ||
                        subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value()) {

                    sendSMSNotification(subsClientType, mdn, KnProvConstants.WELCOME_SMS_ID_PTTRADIO);
                } else if (subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.HANDSET.value() ||
                        subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.POCDONORRADIO.value()) {

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

    private void sendSMSNotification(Integer subsClientType, String mdn, int messageId) {
        String methodName = "sendSMSNotification()";
        knLogger.info(methodName, "Sending WELCOME SMS NOTIFICATION ", subsClientType, KnGDPRTemplate.mdn(mdn), messageId);
        KnProvSMSDTO provSMSDTO = new KnProvSMSDTO();
        provSMSDTO.setMdn(mdn);
        provSMSDTO.setMsgNotificationId(messageId);
        boolean status = provSMSUtil.sendProvSMS(provSMSDTO, null);
        knLogger.info(methodName, "Sent WELCOME SMS NOTIFICATION status ", status);
    }

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


    private static boolean isProvFSBitAllowed(Map<Integer, Integer> inputMap) {
        String methodName = "isProvFSBitAllowed(Map<String, String>";
        knLogger.debug("Entered ", methodName, " inPutMap :", inputMap);
        boolean isProvFSBitValid = true;

        List<com.kodiak.common.resources.KnConstants.PROV_FS_BIT> declaredProvFSList = Arrays.asList(com.kodiak.common.resources.KnConstants.PROV_FS_BIT.values());
        if (inputMap != null) {
            for (Map.Entry<Integer, Integer> reqProvEntry : inputMap.entrySet()) {
                for (com.kodiak.common.resources.KnConstants.PROV_FS_BIT declaredProvFS : declaredProvFSList) {
                    if (!reqProvEntry.getKey().equals(declaredProvFS.value())) {
                        isProvFSBitValid = false;
                        break;
                    }
                }
            }
        }
        knLogger.debug(methodName, " If correct isProvFSBitValid should be true :", isProvFSBitValid);
        return isProvFSBitValid;
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

    private void validateMcsId(String mcsID, List<String> mcxUriScemeList) throws KnProvBOException {
        Boolean flag = false;
        if (mcsID != null && !mcsID.isEmpty()) {
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

    public KnOPProvDTO updateEtagForNNISubscr(String extCorpId, KnPersisterTxn persisterTxn) throws KnProvBOException {
        final String methodName = "updateEtagForNNISubscr(String)";
        knLogger.debug(methodName, "ENTRY : extCorpId-", extCorpId);
        KnOPProvDTO response = new KnOPProvDTO();
        long lastProfileUpdateTime = System.currentTimeMillis();
        IProvXDMServerDAO provXDMServerDAO = null;
        int aliasNGroupMdnCount=0;
        KnCorpGwLinkedAccInfoDTO corpGwLinkedAccInfoDTO=null;
        try {
            provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            KnOPCorpProfileInfoDTO corpProfileInfoDTO = provXDMServerDAO.retrieveCorporateProfile(extCorpId, persisterTxn);
            int corpId = corpProfileInfoDTO.getCorpId();
            if (corpId != 0) {
                knLogger.debug(methodName, "Corporate Exists");
                List<Integer> clientTypeList = new ArrayList<>();
                Collections.addAll(clientTypeList, com.kodiak.common.resources.KnConstants.SUBS_CLIENT_TYPE.ALIASMDN.value(),
                        com.kodiak.common.resources.KnConstants.SUBS_CLIENT_TYPE.GROUPMDN.value(), com.kodiak.common.resources.KnConstants.SUBS_CLIENT_TYPE.SGMDNPATCH.value());
                aliasNGroupMdnCount = provXDMServerDAO.getSubsCountOfClientTypeForCorp(corpId, clientTypeList, persisterTxn);
                if (aliasNGroupMdnCount == 0) {
                    knLogger.debug(methodName, "Alias/Group mdn are not present in the corp with extCorp =", extCorpId, " ,Nullify LinkedGwKey ");
                    KnCorpProfilePersistDTO corpProfilePersistDTO = new KnCorpProfilePersistDTO();
                    corpProfilePersistDTO.setExtCorpId(extCorpId);
                    provXDMServerDAO.updateLinkedGwKeyOfCorp(corpProfilePersistDTO, persisterTxn); //updating linked Gateway key
                    knLogger.debug(methodName, "LinkedGwKey is nullified in corpInfo table");
                } else {
                    knLogger.debug(methodName, "Alias/Group mdn are  present in the corp with extCorp =", extCorpId);
                    if (corpProfileInfoDTO.getLinkedGwKey() != null) {
                        knLogger.debug(methodName, "Link exists for corpinfo table");
                        String linkedGwKey = corpProfileInfoDTO.getLinkedGwKey();
                         corpGwLinkedAccInfoDTO = new KnCorpGwLinkedAccInfoDTO();
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


}
