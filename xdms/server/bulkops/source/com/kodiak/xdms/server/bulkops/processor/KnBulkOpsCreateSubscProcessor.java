package com.kodiak.xdms.server.bulkops.processor;

import com.kodiak.common.commdto.common.KnXDMDeviceProvDTO;
import com.kodiak.common.commdto.request.IXDMRequestDTO;
import com.kodiak.common.commdto.request.KnXDMBulkSubsProvInfoDTO;
import com.kodiak.common.commdto.response.IXDMResponseDTO;
import com.kodiak.common.commdto.response.KnXDMBulkOpsRespDTO;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnGeneralPasswordUtil;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.frameworks.statisticalmgr.KnOMConstants;
import com.kodiak.frameworks.statisticalmgr.KnStatisticsManagerImpl;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.featureset.KnFeatureSetUtil;
import com.kodiak.utilities.featuresetupgrade.util.KnUpgardeFSConfig;
import com.kodiak.xdms.server.bulkops.KnBulkOpsException;
import com.kodiak.xdms.server.bulkops.dao.KnBulkOpsCorpProfileDAO;
import com.kodiak.xdms.server.bulkops.dao.KnBulkOpsDeviceInfoDAO;
import com.kodiak.xdms.server.bulkops.dao.KnBulkOpsSubsProfileDAO;
import com.kodiak.xdms.server.bulkops.dao.KnBulkOpsXDMServerDAO;
import com.kodiak.xdms.server.bulkops.dto.common.KnBulkOpsNNISubsDTO;
import com.kodiak.xdms.server.bulkops.resources.KnBulkOpsConstants;
import com.kodiak.xdms.server.bulkops.resources.KnBulkOpsErrorCodes;
import com.kodiak.xdms.server.bulkops.utils.KnBulkOpsDBUtil;
import com.kodiak.xdms.server.bulkops.utils.KnBulkOpsInfoUtil;
import com.kodiak.xdms.server.bulkops.utils.KnBulkOpsNotifyUtil;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dto.common.KnCorpProfileDTO;
import com.kodiak.xdms.server.common.dto.common.KnCorporateExdmsNotifyDto;
import com.kodiak.xdms.server.common.dto.common.KnXDMSServiceConfigDTO;
import com.kodiak.ems.base.itf.KnEMSConst;
import com.kodiak.ems.base.utils.KnLicenseInfo;
import com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpCommonInfoUtil;
import com.kodiak.xdms.server.subsmgmt.business.helper.KnProvInfoUtil;

import static com.kodiak.common.dao.KnDbUtil.rollback;
import static com.kodiak.common.resources.KnConstants.MICROSERVICES_EVENT_TYPE.CREATE_CORP;
import static com.kodiak.xdms.server.common.resources.KnProfileTypes.CORP_PROFILE;
import static com.kodiak.common.resources.KnConstants.ENABLED_STRING;
import static com.kodiak.xdms.server.common.resources.KnConstants.MASS_LOCATION_ENABLED;
import static com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


public class KnBulkOpsCreateSubscProcessor {
    private static final KnLogger knLogger = KnLogger.getLogger(KnBulkOpsCreateSubscProcessor.class);
    private static KnBulkOpsCreateSubscProcessor instance = null;
    private KnBulkOpsInfoUtil bulkOpsInfoUtil = null;
    private KnGenInfoUtil genInfoUtil = null;
    private KnLicenseInfo licenseInfo = null;
    private KnFeatureSetUtil featureSetUtil = null;
    private KnCorpCommonInfoUtil commonInfoUtil = new KnCorpCommonInfoUtil();
    private KnBulkOpsNotifyUtil bulkOpsNotifyUtil = null;
    KnProvInfoUtil provInfoUtil = new KnProvInfoUtil();

    private static String currentFsVersion;
    static {
        try {
            currentFsVersion = KnUpgardeFSConfig.getFsCurrentVersion();
        } catch (Exception e) {
            knLogger.error("static-init", "Failed to initialize currentFsVersion from KnUpgardeFSConfig", e);
            currentFsVersion = null;
        }
    }

    public KnBulkOpsCreateSubscProcessor() {
        bulkOpsInfoUtil = KnBulkOpsInfoUtil.getInstance();
        genInfoUtil = KnGenInfoUtil.getInstance();
        licenseInfo = KnEMSConst.getLicenseInfo();
        featureSetUtil = KnFeatureSetUtil.getInstance();
        bulkOpsNotifyUtil = KnBulkOpsNotifyUtil.getInstance();
    }

    public static synchronized KnBulkOpsCreateSubscProcessor getInstance() {
        if (instance == null) {
            instance = new KnBulkOpsCreateSubscProcessor();
        }
        return instance;
    }

    public IXDMResponseDTO createBulkSubscriber(IXDMRequestDTO requestDTO){
        String methodName = "createBulkSubscriber(IXDMRequestDTO)";
        long startTime = System.currentTimeMillis();

        knLogger.info(methodName, "ENTRY: Received Request DTO - ", requestDTO, " at ", new Date(startTime));
        boolean ownedTxn = false;
        KnPersisterTxn persisterTxn = null;
        KnXDMBulkSubsProvInfoDTO bulkSubsProvInfoDTO = null;
        IXDMResponseDTO responseDTO = null;
        try{
            // Validate and prepare request DTO
            if (requestDTO instanceof KnXDMBulkSubsProvInfoDTO) {
                bulkSubsProvInfoDTO = (KnXDMBulkSubsProvInfoDTO) requestDTO;
                knLogger.debug(methodName, "received DTO for create Bulk Subscriber - ", bulkSubsProvInfoDTO);
                bulkOpsInfoUtil.sortSubscriberListByMdn(bulkSubsProvInfoDTO);
            } else {
                knLogger.error(methodName, "received and Invalid DTO for create Bulk subscriber op - ", requestDTO);
                responseDTO = new KnXDMBulkOpsRespDTO();
                responseDTO.setResponseMessage("Invalid DTO is passed");
                responseDTO.setResponseCode(KnBulkOpsErrorCodes.BOEntity.ERROR_CODE_INVALID_DTO_PASSED);
                responseDTO.setResponseStatus(KnBulkOpsConstants.RESPONSE_STATUS.FAILURE.value());
                KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_BULK_SUBSCR_CREATION_FAILURE);
                return responseDTO;
            }
            // TODO: Increment Stats for XDM_NUM_MCS_CLIENTS_CREATED_REQ
            // Open Transaction
            persisterTxn = KnPersisterTxn.getPersisterTxn();
            knLogger.debug(methodName, "Opening transaction ");
            persisterTxn.open();
            ownedTxn = true;
            // Initialize configurations
            int initialTotalMDNCount = bulkSubsProvInfoDTO.getSubscriberList().size();
            Map<String, String> paramNameValueMap = genInfoUtil.retrieveRTXConfigValues(persisterTxn);
            int clusterId = Integer.parseInt(System.getenv(KnBulkOpsConstants.CLUSTERID_ENV_NAME));
            Map<String, String> microServicesParamNameValueMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
            String apnName = genInfoUtil.getDefaultAPNName(persisterTxn);
            Integer apnId = genInfoUtil.getAPNId(apnName, persisterTxn);
            knLogger.info(methodName, " APNID ", apnId, "APN NAME  :", apnName);

            KnConstants.HIERARCHY_TYPE hierarchyType = KnGeneralUtil
                    .getHierarchyInterfaceType(KnConstants.SOFTWARE_PKG.POC_SOAP_CSR);
            knLogger.debug(methodName, "hierarchyInterfaceType for POC_SOAP_CSR ", hierarchyType);
            bulkSubsProvInfoDTO.setHierarchyType(hierarchyType);

            // Initialize DAOs and response
            KnXDMBulkOpsRespDTO partialRespDTO = bulkOpsInfoUtil.buildPartialResponse(bulkSubsProvInfoDTO);
            KnBulkOpsXDMServerDAO bulkOpsXDMServerDAO = new KnBulkOpsXDMServerDAO();
            KnBulkOpsCorpProfileDAO bulkOpsCorpProfileDAO = new KnBulkOpsCorpProfileDAO();
            KnBulkOpsSubsProfileDAO bulkOpsSubsProfileDAO = new KnBulkOpsSubsProfileDAO();

            if (bulkSubsProvInfoDTO.getSubscriberClientType() == KnBulkOpsConstants.SUBSCRIBERS_CLIENT_TYPE.DATAGROUPMDN.value()) {
                // TODO: Whether 100 pegs to be incremented or only 1 peg needs to be discussed
                KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_DG_MDN_CREATED_REQ);
            }

            // Retrieve Corporate Profile and update PTT Radio client type if needed
            KnBulkOpsCorpProfileDAO.KnCorpProfileInfo corpProfileInfoDTO =
                retrieveCorpProfileAndApplyPttRadio(bulkSubsProvInfoDTO, bulkOpsCorpProfileDAO, bulkOpsSubsProfileDAO, persisterTxn);

            // Validate Create Bulk Subscriber Request
            validateCreateBulkSubscriber(bulkSubsProvInfoDTO, partialRespDTO, bulkOpsXDMServerDAO,
                    bulkOpsSubsProfileDAO, bulkOpsCorpProfileDAO, paramNameValueMap, apnId, persisterTxn);

            // Removed populateSubsProvInfoDTO instead using the same requestDTO. It was used for using it in validatorFwk
            // Removed setting default subsClientType value here and moved to subsmgmt.
            // Removed setting entityId
            // Removed setting operationType as it is already assigned in requestDTO
            // Removed Duplicate UFMI check as it is not needed for create BulkSubscriber operation
            // Removed creating addPkgIds Map seems redundant as already proper things are there in requestDTO, will check later if required then i will add it
            // Removed Duplicate SubsAliasInfo check as it is not needed for create BulkSubscriber operation

            // Configure service status and license type
            configureServiceStatusAndLicense(bulkSubsProvInfoDTO, partialRespDTO, microServicesParamNameValueMap);

            // Retrieve POC and Presence homes for all subscribers (returns both maps to avoid rebuilding)
            HomeMapping homeMapping = retrievePocAndPresenceHomes(
                bulkSubsProvInfoDTO, corpProfileInfoDTO, bulkOpsXDMServerDAO, partialRespDTO, microServicesParamNameValueMap, persisterTxn);
            Map<String, String> mdnToPocHomeMap = homeMapping.mdnToPocHomeMap;
            Map<String, String> mdnToPresenceHomeMap = homeMapping.mdnToPresenceHomeMap;
//            boolean updateCorpHome = homeMapping.updateCorpHome;

            // Configure corporate auto-pairing and dispatch settings
            configureCorporateAutoPairing(
                bulkSubsProvInfoDTO, corpProfileInfoDTO, bulkOpsSubsProfileDAO, partialRespDTO, paramNameValueMap, persisterTxn);

            // Create or update corporate profile
//            createOrUpdateCorporateProfile(
//                bulkSubsProvInfoDTO, corpProfileInfoDTO, mdnToPocHomeMap, updateCorpHome,
//                bulkOpsCorpProfileDAO, bulkOpsXDMServerDAO, bulkOpsSubsProfileDAO, persisterTxn);
            createOrUpdateCorporateProfile(
                bulkSubsProvInfoDTO, corpProfileInfoDTO, mdnToPocHomeMap,
                bulkOpsCorpProfileDAO, bulkOpsXDMServerDAO, persisterTxn);

            // Process packages and generate feature sets (pass mdnToPresenceHomeMap to avoid rebuilding)
            String tierPackageId = null;
            Map<String, Integer> addonPackageIds = new HashMap<>();
            Integer dataPkgId = null;
            long profileCreationTime = processPackagesAndFeatureSets(
                bulkSubsProvInfoDTO, mdnToPocHomeMap, mdnToPresenceHomeMap, corpProfileInfoDTO,
                paramNameValueMap, persisterTxn, tierPackageId, addonPackageIds);

            // Update tierPackageId, addonPackageIds, dataPkgId from processed values
            Map<String, Integer> addPkgIds = bulkSubsProvInfoDTO.getPkgIdMap() != null
                ? bulkSubsProvInfoDTO.getPkgIdMap().get(KnBulkOpsConstants.ADD_ACTION) : null;
            if (addPkgIds != null) {
                for (Map.Entry<String, Integer> entry : addPkgIds.entrySet()) {
                    if (entry.getValue().intValue() == KnBulkOpsConstants.TIER_PKG_TYPE.intValue()) {
                        tierPackageId = entry.getKey();
                    } else if (entry.getValue().intValue() == KnBulkOpsConstants.ADDON_PKG_TYPE.intValue()) {
                        addonPackageIds.put(entry.getKey(), entry.getValue());
                    }
                }
            }

            // Calculate data package ID and QPP package ID
            Integer profileId = null;
            Integer qppPkgId = null;
            if (!addonPackageIds.isEmpty()) {
                for (String addonPkgCode : addonPackageIds.keySet()) {
                    profileId = featureSetUtil.getAddProfIdForPkg(addonPkgCode, KnBulkOpsDBUtil.getXdmPttServerId());
                    if (profileId != null) {
                        qppPkgId = genInfoUtil.getDataPkgId(KnBulkOpsConstants.QPP_DATA_PKG_TYPE, persisterTxn).get(profileId);
                        if (qppPkgId != null) {
                            dataPkgId = qppPkgId;
                            break;
                        } else if (dataPkgId == null) {
                            dataPkgId = genInfoUtil.getDataPkgId(KnBulkOpsConstants.ADDON_DATA_PKG_TYPE, persisterTxn).get(profileId);
                        }
                    }
                }
            }
            if (qppPkgId == null)
                qppPkgId = KnBulkOpsConstants.DEFAULT_QPP_ID;
            if (dataPkgId == null) {
                dataPkgId = KnBulkOpsConstants.DEFAULT_DATAPKG_ID;
            }
            if (profileId == null)
                profileId = KnBulkOpsConstants.DEFAULT_PROFILE_ID;

            bulkSubsProvInfoDTO.setQppPkgId(qppPkgId);

            knLogger.debug(methodName, "Subscriber Prov Info DTO - ", bulkSubsProvInfoDTO);


            // Enabling 27th bit in XDMSFS2 based on system-level MASS_LOCATION_ENABLED flag
            String systemLevelFlag = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn).get(MASS_LOCATION_ENABLED);
            if (ENABLED_STRING.equals(systemLevelFlag)) {
                String currentXdmsFs2 = bulkSubsProvInfoDTO.getXdmsFS2();
                String updatedXdmsFs2 = featureSetUtil.getSetFeatureSetBits(currentXdmsFs2,
                        new int[]{com.kodiak.common.resources.KnConstants.FEATURE_SET.ONDEMLOCATION.value()});
                bulkSubsProvInfoDTO.setXdmsFS2(updatedXdmsFs2);
            }

            // Create Subscriber Profiles in bulk
            knLogger.info(methodName, "Creating subscriber profiles for ", bulkSubsProvInfoDTO.getSubscriberList().size(), " subscribers");
            bulkOpsSubsProfileDAO.createBulkSubscriberProfiles(bulkSubsProvInfoDTO, persisterTxn);
            knLogger.info(methodName, "Successfully created subscriber profiles");

            //Create Device in bulk
            boolean awareClient = false;
            if ((String.valueOf(bulkSubsProvInfoDTO.getSubscriberClientType())).matches(KnBulkOpsConstants.AWARE_CLIENTS)) {
                knLogger.debug(methodName, "Aware client, So not creating OIDC profile");
                awareClient = true;
            }
            boolean bitEnabled = KnGeneralUtil.getFeatureBitValue(bulkSubsProvInfoDTO.getSubsFS2(), KnBulkOpsConstants.FEATURE_SET.MCDEVICE.value());
            partialRespDTO.setBitEnabled(bitEnabled);
            knLogger.debug(methodName, "awareClient :", awareClient,
                    " MCSCOMP: ", bulkSubsProvInfoDTO.getMcsCompliance(),
                    " licenseType:", bulkSubsProvInfoDTO.getLicenseType(),
                    " bitEnabled: ", bitEnabled);
            if ((KnBulkOpsConstants.MCSCOMPLIANCE == bulkSubsProvInfoDTO.getMcsCompliance() && !awareClient)
                    || (KnBulkOpsConstants.KODIAK_CLIENT == bulkSubsProvInfoDTO.getMcsCompliance()
                    && KnBulkOpsConstants.USER_LICENSE_TYPE_DISABLED == bulkSubsProvInfoDTO.getLicenseType() && bitEnabled)) {
                knLogger.info(methodName, "Creating device info for bulk subscribers");
                createBulkDeviceInfo(bulkSubsProvInfoDTO, bitEnabled, profileCreationTime, persisterTxn);
                knLogger.info(methodName, "Successfully created device info for bulk subscribers");
            }

            // Create NNI Subscriber Profile in bulk
            if (KnBulkOpsConstants.SUBSCRIBERS_CLIENT_TYPE.POC_NNI_Alias_MDN.value() == bulkSubsProvInfoDTO.getSubscriberClientType()
                || KnBulkOpsConstants.SUBSCRIBERS_CLIENT_TYPE.POC_NNI_Group_MDN.value() == bulkSubsProvInfoDTO.getSubscriberClientType()) {
                knLogger.info(methodName, "Creating NNI subscriber profiles for ", bulkSubsProvInfoDTO.getSubscriberList().size(), " subscribers");
                createBulkNniSubscriberProfiles(bulkSubsProvInfoDTO, bulkOpsXDMServerDAO, persisterTxn);
                knLogger.info(methodName, "Successfully created NNI subscriber profiles");
            }

            // Update Etag for Corp (for NNI subscribers)
            if (bulkSubsProvInfoDTO.getExtCorpId() != null && corpProfileInfoDTO != null) {
                knLogger.info(methodName, "Updating Etag for NNI subscribers, extCorpId: ", bulkSubsProvInfoDTO.getExtCorpId());
                bulkOpsInfoUtil.updateEtagForNNISubscribers(corpProfileInfoDTO, bulkOpsXDMServerDAO,
                        bulkOpsCorpProfileDAO, bulkOpsSubsProfileDAO, persisterTxn);
                knLogger.info(methodName, "Successfully updated Etag for NNI subscribers");
            }

            // Create Subscriber APN Info
            knLogger.info(methodName, "Adding APN profile for bulk subscribers");
            bulkOpsXDMServerDAO.addSubApnForBulk(
                    bulkOpsInfoUtil.fetchMDNFromSubscriberList(bulkSubsProvInfoDTO.getSubscriberList()),
                    apnId, persisterTxn);
            knLogger.info(methodName, "Successfully added APN profile for bulk subscribers");

            // Create Subscriber Roaming Profile
            List<String> roamNAClientTypes = new ArrayList<>();
            roamNAClientTypes = Arrays.asList(((String) paramNameValueMap.get(KnBulkOpsConstants.ROAM_NA_CLIENT_TYPES)).split(","));
            knLogger.debug(methodName, "Roaming not applicable client type ", roamNAClientTypes);
            ArrayList<Integer> roamingClusterIdList = new ArrayList<Integer>();
            // Removed code for fetching roamingType from request as it is not present in create Bulk Subscriber API
            // Setting default roaming cluster id
            roamingClusterIdList.add(KnBulkOpsConstants.DEFAULT_ROAMING_CLUSTER_ID);
            knLogger.debug(methodName, "adding an entry into Subscriber Roaming Profile");
            bulkOpsXDMServerDAO.createSubscrRoamingProfileForBulk(
                    bulkOpsInfoUtil.fetchMDNFromSubscriberList(bulkSubsProvInfoDTO.getSubscriberList()),
                    roamingClusterIdList, persisterTxn);

            // Add MDNs to Corp Resource List Index Doc for corporate subscribers
            if (bulkSubsProvInfoDTO.getCorporateSubscriptionType() == KnBulkOpsConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
                knLogger.info(methodName, "Adding MDNs to Corp Resource List Index Doc for corporate subscribers");
                bulkOpsXDMServerDAO.addMdnToCorpResourceListIndexDocForBulk(
                        bulkOpsInfoUtil.fetchMDNFromSubscriberList(bulkSubsProvInfoDTO.getSubscriberList()),
                        persisterTxn);
                knLogger.info(methodName, "Successfully added MDNs to Corp Resource List Index Doc");
            }

            // Create entries into XDM_ContactListDocMap
            knLogger.info(methodName, "Adding entries into XDM Contact List Doc Map");
            List<String> mdnList = bulkOpsInfoUtil.fetchMDNFromSubscriberList(bulkSubsProvInfoDTO.getSubscriberList());
            List<Integer> contactListIds = bulkOpsXDMServerDAO.addMdnToXDMContactListDocMapForBulk(mdnList, persisterTxn);
            knLogger.info(methodName, "Successfully added entries into XDM Contact List Doc Map");

            // Create entries into XDM_ContactList
            knLogger.info(methodName, "Adding entries into XDM Contact List");
            bulkOpsXDMServerDAO.addMdnToXDMContactListForBulk(mdnList, contactListIds, persisterTxn);
            knLogger.info(methodName, "Successfully added entries into XDM Contact List");

            // Create entries into XDM_Directory
            knLogger.info(methodName, "Adding entries into XDM Directory");
            bulkOpsXDMServerDAO.addMdnToXDMDirectoryForBulk(mdnList, persisterTxn);
            knLogger.info(methodName, "Successfully added entries into XDM Directory");

            // Create entries for addon packages
            if (!addonPackageIds.isEmpty()) {
                knLogger.info(methodName, "Creating addon packages for bulk subscribers");
                List<String> addonPackageCodes = new ArrayList<>(addonPackageIds.keySet());
                bulkOpsXDMServerDAO.createSubAddOnPkgsForBulk(mdnList, addonPackageCodes, persisterTxn);
                knLogger.info(methodName, "Successfully created addon packages");
            }

            // Create entries into POCSUBSCR_ADDLINFO
            knLogger.info(methodName, "Creating subscriber package additional info for bulk subscribers");
            bulkOpsXDMServerDAO.createSubscrPkgAddlInfoForBulk(
                    mdnList,
                    tierPackageId,
                    dataPkgId,
                    bulkSubsProvInfoDTO.getOnBoardingMailReq(),
                    persisterTxn
            );
            knLogger.info(methodName, "Successfully created subscriber package additional info");

            // Removed code for STANDALONECAMERA
            // Removed code for validation of McId, McpttId, McVideoId, McDataId
            // Removed code for validation of validateIfUserIdAlredyExistsInSystem
            // Removed code for validation of valiadteIfMCXIdsExistsAsMdnOrAliasMdn

            // Removed code for createSubscriberCameraInfo
            // Removed code for insertIntoSubsAliasId


            // OIDC Profile is required for Create Bulk Subscriber operation
            partialRespDTO.setOIDCApplicable(Boolean.TRUE);

            // Finalize the response based on success/failure counts
            int currentTotalMdnCount = bulkSubsProvInfoDTO.getSubscriberList().size();
            bulkOpsInfoUtil.finalizePartialResponse(partialRespDTO, initialTotalMDNCount, KnBulkOpsConstants.BulkOperations.CREATEBULKSUBSCRIBER);
            partialRespDTO.setFailureMdnCount(initialTotalMDNCount - currentTotalMdnCount);
            buildCreateBulkSubscriberResponse(bulkSubsProvInfoDTO, partialRespDTO, paramNameValueMap);
            partialRespDTO.setProcessingTimeMs(System.currentTimeMillis() - startTime);
            responseDTO = partialRespDTO;

            // Notifications
            if (bulkSubsProvInfoDTO.getCorporateSubscriptionType() == KnBulkOpsConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
                KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(partialRespDTO.getCorpId()), CORP_PROFILE, true, persisterTxn);
                knLogger.debug(methodName, "Corporate Profile - ", corpProfile);
                KnCorporateExdmsNotifyDto corporateExdmsNotifyDto = commonInfoUtil.formCorpNotifyPayload(CREATE_CORP.value(), corpProfile, KnConstants.ENABLED, KnConstants.MICROSERVICES_NOTIFY_EVENT_TYPE.CORPORATE_EVENT.value());
                knLogger.info(methodName, "Publishing micro service notify for corp- ", corporateExdmsNotifyDto);
                bulkOpsNotifyUtil.startNotifyMicroServicesJob(List.of(corporateExdmsNotifyDto));
            }

            // Commit Transaction
            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction ");
                persisterTxn.save();
            }

            KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_BULK_SUBSCR_CREATED);
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected Exception occurred ", e);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            responseDTO = bulkOpsInfoUtil.buildFailureResponse(bulkSubsProvInfoDTO, e, startTime);
            KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_BULK_SUBSCR_CREATION_FAILURE);
        }

        return responseDTO;
    }

    public void buildCreateBulkSubscriberResponse(
            KnXDMBulkSubsProvInfoDTO bulkSubsProvInfoDTO,
            KnXDMBulkOpsRespDTO partialRespDTO,
            Map<String, String> paramNameValueMap) {

        String methodName = "buildCreateBulkSubscriberResponse()";
        knLogger.debug(methodName, "ENTRY: Building Create Bulk Subscriber Response");

        partialRespDTO.setResponseMessage(KnBulkOpsConstants.CREATE_BULK_SUBSCRIBER_SUCCESS);
        if (bulkSubsProvInfoDTO.getCorporateSubscriptionType() == KnBulkOpsConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
            partialRespDTO.setCorpId(Integer.parseInt(bulkSubsProvInfoDTO.getCorpId()));
        }
        partialRespDTO.setSuccessMdns(bulkSubsProvInfoDTO.getSubscriberList());
        partialRespDTO.setSubsFS2(bulkSubsProvInfoDTO.getSubsFS2());
        partialRespDTO.setSubscriberClientType(bulkSubsProvInfoDTO.getSubscriberClientType());
        partialRespDTO.setLicenseType(bulkSubsProvInfoDTO.getLicenseType());
        partialRespDTO.setMcsCompliance(bulkSubsProvInfoDTO.getMcsCompliance());

        String pwdExpiry = paramNameValueMap.get(KnBulkOpsConstants.MCS_TEMP_PASSWORD_EXPIRY);
        int passwordExpiry = 0;
        if (pwdExpiry != null)
            passwordExpiry = Integer.parseInt(pwdExpiry);
        partialRespDTO.setExpiry(passwordExpiry);

        knLogger.debug(methodName, "EXIT: Built Create Bulk Subscriber Response - ", partialRespDTO);
    }

    public void validateCreateBulkSubscriber(KnXDMBulkSubsProvInfoDTO bulkSubsProvInfoDTO,
                                             KnXDMBulkOpsRespDTO partialRespDTO,
                                             KnBulkOpsXDMServerDAO bulkOpsXDMServerDAO,
                                             KnBulkOpsSubsProfileDAO bulkOpsSubsProfileDAO,
                                             KnBulkOpsCorpProfileDAO bulkOpsCorpProfileDAO,
                                             Map<String, String> paramNameValueMap,
                                             Integer apnId,
                                             KnPersisterTxn persisterTxn)
            throws KnBulkOpsException, KnBOException, KnDAOException {
        String methodName = "validateCreateBulkSubscriber()";
        knLogger.debug(methodName, "ENTRY: Validating Create Bulk Subscriber Request - ", bulkSubsProvInfoDTO);

        // Validate Server Configuration for Bulk Subscriber Creation
        if (paramNameValueMap.get(KnBulkOpsConstants.ROAM_NA_CLIENT_TYPES) == null) {
            throw new KnBulkOpsException(KnBulkOpsErrorCodes.BOEntity.SERVER_CONFIGURATION_FAILURE, "ROAM_NA_CLIENT_TYPE parameter not found");
        }

        // Validate APN Info
        if (apnId == null) {
            knLogger.error(methodName, " Default APN not exists , APN_INFO_NOT_FOUND ");
            throw new KnBulkOpsException(KnBulkOpsErrorCodes.BOEntity.APN_INFO_NOT_FOUND, " APN_INFO_NOT_FOUND ");
        }

        // Subscriber Client Type Validations
        bulkOpsInfoUtil.validateSubsClientType(bulkSubsProvInfoDTO.getSubscriberClientType(), genInfoUtil, persisterTxn);
        // Susbscription Type Validations
        bulkOpsInfoUtil.validateSubscriptionType(bulkSubsProvInfoDTO);
        // Hierarchy Validations
        bulkOpsInfoUtil.validateHierarchy(bulkSubsProvInfoDTO, bulkOpsSubsProfileDAO, bulkOpsCorpProfileDAO, partialRespDTO, persisterTxn);
        // Subscriber AuthStatus Validations
        bulkOpsInfoUtil.validateSubAuthStatus(bulkSubsProvInfoDTO, bulkOpsSubsProfileDAO, partialRespDTO, persisterTxn);
        // Subscriber List Validations
        bulkOpsInfoUtil.validateSubscriberList(KnBulkOpsConstants.BulkOperations.CREATEBULKSUBSCRIBER,
                bulkSubsProvInfoDTO, bulkOpsXDMServerDAO, bulkOpsSubsProfileDAO, partialRespDTO, null, persisterTxn);

        // Validate Max Subscriber limit from license
        int totalSubsLimit = Integer.parseInt(licenseInfo.getNoOfSubs());
        knLogger.debug(methodName, "Max subs count for license - ", totalSubsLimit);
        int subscriberCount = bulkOpsSubsProfileDAO.retrieveSubscriberCount(persisterTxn);
        if ((subscriberCount + bulkSubsProvInfoDTO.getSubscriberList().size()) > totalSubsLimit) {
            knLogger.error(methodName, "Max subscriber limit reached for license ");
            throw new KnBulkOpsException(KnBulkOpsErrorCodes.BOEntity.MAX_SUBS_LIMIT_REACHED_FOR_LICENSE, "Max Subscriber limit reached for license");
        }

        knLogger.debug(methodName, "EXIT: Create Bulk Subscriber Request validation successful");
    }

    /**
     * Holder class to return both POC and Presence home maps together with updateCorpHome flag
     * This avoids rebuilding the maps multiple times
     */
    private static class HomeMapping {
        final Map<String, String> mdnToPocHomeMap;
        final Map<String, String> mdnToPresenceHomeMap;
//        final boolean updateCorpHome;

//        HomeMapping(Map<String, String> mdnToPocHomeMap, Map<String, String> mdnToPresenceHomeMap, boolean updateCorpHome) {
//            this.mdnToPocHomeMap = mdnToPocHomeMap;
//            this.mdnToPresenceHomeMap = mdnToPresenceHomeMap;
//            this.updateCorpHome = updateCorpHome;
//        }

        HomeMapping(Map<String, String> mdnToPocHomeMap, Map<String, String> mdnToPresenceHomeMap) {
            this.mdnToPocHomeMap = mdnToPocHomeMap;
            this.mdnToPresenceHomeMap = mdnToPresenceHomeMap;
        }
    }

    /**
     * Create bulk device info for MCS compliance and Kodiak clients
     * This method handles device creation for multiple MDNs in one operation
     *
     * @param bulkSubsProvInfoDTO Bulk subscriber provisioning info DTO
     * @param bitEnabled Whether MC device bit is enabled
     * @param profileCreationTime Profile creation timestamp
     * @param persisterTxn Database transaction
     * @throws KnBulkOpsException if device creation fails
     */
    private void createBulkDeviceInfo(
            KnXDMBulkSubsProvInfoDTO bulkSubsProvInfoDTO,
            boolean bitEnabled,
            long profileCreationTime,
            KnPersisterTxn persisterTxn) throws KnBulkOpsException {

        String methodName = "createBulkDeviceInfo(KnXDMBulkSubsProvInfoDTO, boolean, long, KnPersisterTxn)";
        int subscriberCount = bulkSubsProvInfoDTO.getSubscriberList().size();
        knLogger.info(methodName, "ENTRY: Creating bulk device info for ", subscriberCount, " MDNs");

        try {
            KnBulkOpsDeviceInfoDAO bulkOpsDeviceInfoDAO = new KnBulkOpsDeviceInfoDAO();

            // Extract MDN list
            List<String> mdnList = bulkOpsInfoUtil.fetchMDNFromSubscriberList(bulkSubsProvInfoDTO.getSubscriberList());

            // Fetch existing device profiles for all MDNs
            Map<String, KnXDMDeviceProvDTO> existingDeviceProfiles =
                bulkOpsDeviceInfoDAO.selectDeviceProfilesByDeviceIds(mdnList, persisterTxn);
            knLogger.debug(methodName, "Found ", existingDeviceProfiles.size(), " existing device profiles");

            // Delete existing device info and IMPI info for all MDNs
            if (!mdnList.isEmpty()) {
                bulkOpsDeviceInfoDAO.deleteDeviceImpiInfoBulk(mdnList, persisterTxn);
                bulkOpsDeviceInfoDAO.deleteDeviceInfoBulk(mdnList, persisterTxn);
            }

            // Cache frequently accessed values
            KnXDMSServiceConfigDTO xdmsServiceConfigDTO =
                genInfoUtil.retrieveXDMSServiceConfig(KnBulkOpsDBUtil.getXdmPttServerId(), persisterTxn);
            final String realm = xdmsServiceConfigDTO.getAuthRealm();
            final KnGeneralPasswordUtil pwdUtil = KnGeneralPasswordUtil.getInstance();
            final boolean isMcsCompliant = KnBulkOpsConstants.MCSCOMPLIANCE == bulkSubsProvInfoDTO.getMcsCompliance();
            final int corporateSubscriptionType = bulkSubsProvInfoDTO.getCorporateSubscriptionType();
            final Integer corpId = (corporateSubscriptionType == KnBulkOpsConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value())
                    ? Integer.parseInt(bulkSubsProvInfoDTO.getCorpId()) : null;
            final int deviceStatusActivated = KnBulkOpsConstants.DEVICE_STATUS_OP.ACTIVATED.value();
            final boolean isMcDeviceScenario = bitEnabled && !isMcsCompliant;

            // Pre-size lists for optimal performance
            List<KnBulkOpsDeviceInfoDAO.KnBulkDeviceInfoDTO> deviceInfoList = new ArrayList<>(subscriberCount);
            List<KnBulkOpsDeviceInfoDAO.KnBulkDeviceImpiInfoDTO> deviceImpiInfoList = new ArrayList<>(subscriberCount);

            // Process each subscriber - optimized loop
            for (var subscriberEntry : bulkSubsProvInfoDTO.getSubscriberList()) {
                final String mdn = subscriberEntry.getMdn();
                final String telUriMdn = KnBulkOpsConstants.TELURI + mdn;
                final KnXDMDeviceProvDTO existingDeviceProfile = existingDeviceProfiles.get(mdn);

                // Create device info DTO
                KnBulkOpsDeviceInfoDAO.KnBulkDeviceInfoDTO deviceInfo =
                    new KnBulkOpsDeviceInfoDAO.KnBulkDeviceInfoDTO();

                deviceInfo.setDeviceId(mdn);
                deviceInfo.setDeviceStatus(deviceStatusActivated);
                deviceInfo.setDeviceActTimeStamp(profileCreationTime);
                deviceInfo.setDeviceLastUsed(profileCreationTime);
                deviceInfo.setDeviceCreatedAs(KnBulkOpsConstants.IMPLICIT_DEVICE);

                if (corpId != null) {
                    deviceInfo.setCorpId(corpId);
                }

                // Set device IMPI - optimized condition check
                deviceInfo.setDeviceIMPI(isMcsCompliant ? subscriberEntry.getMcId() : telUriMdn);

                // Handle device type and password - optimized with reduced branching
                if (isMcDeviceScenario) {
                    deviceInfo.setDeviceDigestPassword(existingDeviceProfile != null
                            ? existingDeviceProfile.getDevicePassword() : null);
                    deviceInfo.setDeviceType(KnBulkOpsConstants.DEVICE_TYPE.MC_DEVICE.Value());
                    deviceInfo.setDeviceName(null);
                    deviceInfo.setDeviceClientId(null);
                    deviceInfo.setDeviceShared(KnBulkOpsConstants.MCDEVICESHARED_TYPE.SHARED.Value());
                    deviceInfo.setReqDeviceId(mdn);
                } else {
                    // Standard device handling
                    if (existingDeviceProfile != null) {
                        deviceInfo.setDeviceDigestPassword(existingDeviceProfile.getDevicePassword());
                    } else {
                        // Generate password and HA1 digest for new devices
                        String password = pwdUtil.generatePassword(KnBulkOpsConstants.MAX_OIDC_PASSWORD_LENGTH);
                        String clientPassword = bulkOpsInfoUtil.generateHA1(telUriMdn, realm, password);
                        deviceInfo.setDeviceDigestPassword(clientPassword);
                    }
                    deviceInfo.setDeviceClientId(mdn);
                    deviceInfo.setDeviceShared(KnBulkOpsConstants.DEVICESHARED);
                }

                deviceInfoList.add(deviceInfo);

                // Create device IMPI info DTO - optimized
                KnBulkOpsDeviceInfoDAO.KnBulkDeviceImpiInfoDTO deviceImpiInfo =
                    new KnBulkOpsDeviceInfoDAO.KnBulkDeviceImpiInfoDTO();

                if (isMcsCompliant) {
                    String mcId = subscriberEntry.getMcId();
                    deviceImpiInfo.setDeviceImpi(mcId);
                    deviceImpiInfo.setDeviceImpu(mcId);
                } else {
                    deviceImpiInfo.setDeviceImpi(telUriMdn);
                    deviceImpiInfo.setDeviceImpu(telUriMdn);
                }

                deviceImpiInfoList.add(deviceImpiInfo);
            }

            // Bulk insert device info and device IMPI info
            bulkOpsDeviceInfoDAO.createDeviceInfoBulk(deviceInfoList, persisterTxn);
            bulkOpsDeviceInfoDAO.createDeviceImpiInfoBulk(deviceImpiInfoList, persisterTxn);

            knLogger.info(methodName, "EXIT: Successfully created device info for ", deviceInfoList.size(), " MDNs");

        } catch (Exception e) {
            knLogger.error(methodName, "Failed to create bulk device info: ", e);
            throw new KnBulkOpsException(KnBulkOpsErrorCodes.BOEntity.BULK_DEVICE_CREATION_FAILED,
                "Failed to create bulk device info: " + e.getMessage(), e);
        }
    }

    /**
     * Create NNI Subscriber Profiles in bulk for multiple MDNs
     * This method creates Network-to-Network Interface subscriber profiles for POC_NNI_Alias_MDN and POC_NNI_Group_MDN client types
     *
     * Implementation includes:
     * 1. Feature set calculation (NNI Subscriber FS, POC System FS, NNI Gateway FS)
     * 2. Generation of NNI Active Feature Set by combining all feature sets
     * 3. Profile ID retrieval from database
     * 4. Bulk insertion into POC_NNISUBSCR_INFO table
     *
     * @param bulkSubsProvInfoDTO Request DTO containing subscriber list and configuration
     * @param bulkOpsXDMServerDAO DAO instance for database operations
     * @param persisterTxn Database transaction
     * @throws KnBulkOpsException if NNI profile creation fails
     */
    private void createBulkNniSubscriberProfiles(KnXDMBulkSubsProvInfoDTO bulkSubsProvInfoDTO,
                                                 KnBulkOpsXDMServerDAO bulkOpsXDMServerDAO,
                                                 KnPersisterTxn persisterTxn) throws KnBulkOpsException {
        String methodName = "createBulkNniSubscriberProfiles(KnXDMBulkSubsProvInfoDTO, KnBulkOpsXDMServerDAO, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: Creating NNI subscriber profiles for bulk operation");

        try {
            // Extract MDN list from subscriber list
            List<String> mdnList = bulkOpsInfoUtil.fetchMDNFromSubscriberList(bulkSubsProvInfoDTO.getSubscriberList());

            if (mdnList == null || mdnList.isEmpty()) {
                knLogger.warn(methodName, "No MDNs found for NNI subscriber profile creation");
                return;
            }

            knLogger.info(methodName, "Creating NNI profiles for ", mdnList.size(), " MDNs, clientType: ", bulkSubsProvInfoDTO.getSubscriberClientType());

            int profileType = 2;

            long nniSubScrFs = featureSetUtil.retrieveNNISubScrFs(profileType);
            knLogger.info(methodName, "Retrieved nniSubScrFs: ", nniSubScrFs);
            long pocSystemFs = featureSetUtil.retrievePocSystemFs();
            knLogger.info(methodName, "Retrieved pocSystemFs: ", pocSystemFs);
            long nniGwFs = featureSetUtil.retrieveNNIGwFs();
            knLogger.info(methodName, "Retrieved nniGwFs: ", nniGwFs);
            long nniActiveFs = featureSetUtil.generateNNIActiveFeatureBitSet(nniSubScrFs, pocSystemFs, nniGwFs);
            knLogger.info(methodName, "Generated nniActiveFs: ", nniActiveFs);

            int profileId = bulkOpsXDMServerDAO.getProfileIdForNNISubscriber(profileType, persisterTxn);
            knLogger.debug(methodName, "Retrieved profileId: ", profileId, " for profileType: ", profileType);

            KnBulkOpsNNISubsDTO nniSubsDTO = new KnBulkOpsNNISubsDTO(mdnList, profileId, nniActiveFs);
            knLogger.debug(methodName, "Prepared NNI DTO with profileId: ", profileId, ", nniActiveFs: ", nniActiveFs);
            bulkOpsXDMServerDAO.createNNISubscrProfile(nniSubsDTO, persisterTxn);

            knLogger.info(methodName, "EXIT: Successfully created NNI subscriber profiles for ", mdnList.size(), " MDNs");

        } catch (Exception e) {
            knLogger.error(methodName, "Failed to create NNI subscriber profiles: ", e);
            throw new KnBulkOpsException(KnBulkOpsErrorCodes.BOEntity.BULK_NNI_PROFILE_CREATION_FAILED,
                "Failed to create NNI subscriber profiles: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieve Corporate Profile and update PTT Radio client type if needed
     */
    private KnBulkOpsCorpProfileDAO.KnCorpProfileInfo retrieveCorpProfileAndApplyPttRadio(
            KnXDMBulkSubsProvInfoDTO bulkSubsProvInfoDTO,
            KnBulkOpsCorpProfileDAO bulkOpsCorpProfileDAO,
            KnBulkOpsSubsProfileDAO bulkOpsSubsProfileDAO,
            KnPersisterTxn persisterTxn) throws Exception {
        String methodName = "retrieveCorpProfileAndApplyPttRadio";

        KnBulkOpsCorpProfileDAO.KnCorpProfileInfo corpProfileInfoDTO = new KnBulkOpsCorpProfileDAO.KnCorpProfileInfo();
        corpProfileInfoDTO.setCorpId(-1);
        if (bulkSubsProvInfoDTO.getCorporateSubscriptionType() == KnBulkOpsConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
            knLogger.debug(methodName, "verifying if corporate profile already exists");
            corpProfileInfoDTO = bulkOpsCorpProfileDAO.retrieveCorporateProfile(bulkSubsProvInfoDTO.getExtCorpId(), persisterTxn);
            knLogger.debug(methodName, "Corporate Id Retrieved - ", corpProfileInfoDTO.getCorpId());
            corpProfileInfoDTO.setExtCorpId(bulkSubsProvInfoDTO.getExtCorpId());

            if(bulkSubsProvInfoDTO.getSubsDefPttRadio() != KnBulkOpsConstants.SUBSCR_DEF_PTTRADIO_ENABLED) {
                knLogger.debug(methodName, "SUBSCR_DEF_PTTRADIO is disabled proceeding to check system & corp level ");
                int subsDef = genInfoUtil.retrieveXDMSServiceConfig(KnBulkOpsDBUtil.getXdmPttServerId(), persisterTxn).getSubsDefPttRadio();
                if (subsDef == KnBulkOpsConstants.SUBSCR_DEF_PTTRADIO_ENABLED) {
                    knLogger.debug(methodName, "system level SUBSCR_DEF_PTTRADIO is enabled skipping corp level check");
                    bulkSubsProvInfoDTO.setSubsDefPttRadio(subsDef);
                } else if(corpProfileInfoDTO.getCorpId() > 0
                        && corpProfileInfoDTO.getSubsDefPttRadio() == KnBulkOpsConstants.SUBSCR_DEF_PTTRADIO_ENABLED) {
                    knLogger.debug(methodName, "corp level SUBSCR_DEF_PTTRADIO is enabled");
                    bulkSubsProvInfoDTO.setSubsDefPttRadio(KnBulkOpsConstants.SUBSCR_DEF_PTTRADIO_ENABLED);
                }
            }

            if(bulkSubsProvInfoDTO.getSubsDefPttRadio() == KnBulkOpsConstants.SUBSCR_DEF_PTTRADIO_ENABLED) {
                knLogger.debug(methodName, "Converting regular client type ", bulkSubsProvInfoDTO.getSubscriberClientType(), " to  PTTRADIO Client type");
                switch (bulkSubsProvInfoDTO.getSubscriberClientType()) {
                    case 1:
                        bulkSubsProvInfoDTO.setSubscriberClientType(KnBulkOpsConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value());
                        break;
                    case 5:
                        bulkSubsProvInfoDTO.setSubscriberClientType(KnBulkOpsConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value());
                        break;
                    case 10:
                        bulkSubsProvInfoDTO.setSubscriberClientType(KnBulkOpsConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value());
                        break;
                    default:
                        break;
                }
            }

            int maxSubsPerCorp = 0;
            knLogger.info( methodName, "validating if Max Corp Subscriber count reached");

            int maxSubsCorporateLimit = corpProfileInfoDTO.getMaxSubscribers();
            int maxSubsPerCorpSystemLevel = genInfoUtil.retrieveXDMSServiceConfig(KnBulkOpsDBUtil.getXdmPttServerId(), persisterTxn).getMaxSubscrPerCorp();
            if(maxSubsCorporateLimit > 0) {
                maxSubsPerCorp = maxSubsPerCorpSystemLevel > maxSubsCorporateLimit ? maxSubsCorporateLimit : maxSubsPerCorpSystemLevel;
            }else{
                maxSubsPerCorp = maxSubsPerCorpSystemLevel;
            }
            knLogger.debug(methodName, "maxSubsPerCorpSystemLevel: ", maxSubsPerCorpSystemLevel, " ,maxSubsCorporateLimit: ",
                    maxSubsCorporateLimit);
            if (corpProfileInfoDTO.getCorpId() > 0) {
                knLogger.debug( methodName, "Retrieving the corporation [" + corpProfileInfoDTO.getCorpId() + "] current Subscriber count ");
                int corpSubscriberCount = bulkOpsSubsProfileDAO.retrieveCorpSubscriberCount(corpProfileInfoDTO.getCorpId(), persisterTxn);
                knLogger.debug( methodName, "Retrieved Corp subscriber count - " + corpSubscriberCount);
                if (corpSubscriberCount + 1 > maxSubsPerCorp) {
                    throw new KnBulkOpsException(KnBulkOpsErrorCodes.BOEntity.MAX_SUBS_LIMIT_REACHED_FOR_CORP,
                            "Validation failed, Max limit for subscriber is reached for Account");
                }
            }
            knLogger.info( methodName, "Max subscriber per corp validation  is successful");
        }

        return corpProfileInfoDTO;
    }

    /**
     * Configure service status and license type based on subscriber client type
     */
    private void configureServiceStatusAndLicense(
            KnXDMBulkSubsProvInfoDTO bulkSubsProvInfoDTO,
            KnXDMBulkOpsRespDTO partialRespDTO,
            Map<String, String> microServicesParamNameValueMap) {
        String methodName = "configureServiceStatusAndLicense";

        // Setting serviceStatusOp and serviceStatusAuthUser based on subsClientType
        int serviceStatusOp = 0;
        if ( bulkSubsProvInfoDTO.getSubscriberClientType() == KnBulkOpsConstants.SUBSCRIBERS_CLIENT_TYPE.POC_NNI_Alias_MDN.value()
                || bulkSubsProvInfoDTO.getSubscriberClientType() == KnBulkOpsConstants.SUBSCRIBERS_CLIENT_TYPE.POC_NNI_Group_MDN.value()
                || bulkSubsProvInfoDTO.getSubscriberClientType() == KnBulkOpsConstants.SUBSCRIBERS_CLIENT_TYPE.SGMDNPATCH.value()
                || bulkSubsProvInfoDTO.getSubscriberClientType() == KnBulkOpsConstants.SUBSCRIBERS_CLIENT_TYPE.DATAGROUPMDN.value()
                || bulkSubsProvInfoDTO.getSubscriberClientType() == KnBulkOpsConstants.SUBSCRIBERS_CLIENT_TYPE.STANDALONECAMERA.value()) {
            serviceStatusOp = KnBulkOpsConstants.SERVICE_AUTH_STATUS.ACTIVATED.value();
        } else {
            // Removed Logic for MCS Compliance as the parameter is not there in the create Bulk Subscriber API request
            serviceStatusOp = KnBulkOpsConstants.SERVICE_AUTH_STATUS.PROVISIONED.value();
        }
        bulkSubsProvInfoDTO.setServiceStatusOp(serviceStatusOp);

        int serviceStatusAuthUser = KnBulkOpsConstants.SERVICE_STATUS_AUTHUSER.ACTIVATED.value();
        bulkSubsProvInfoDTO.setServiceStatusAuthUser(serviceStatusAuthUser);

        // Storing AuthStatus as PRE_PROVISIONED state if POCHOME_AUTOASSIGN_FLAG FLAG is disabled.
        String pocHomeAutoAssignFlag = microServicesParamNameValueMap.get(KnBulkOpsConstants.POCHOME_AUTOASSIGN_FLAG);
        knLogger.info(methodName, "pocHomeAutoAssignFlag :", pocHomeAutoAssignFlag);
        if (pocHomeAutoAssignFlag != null && Integer.parseInt(pocHomeAutoAssignFlag) == KnBulkOpsConstants.ENABLED) {
            //calculating servicece auuth status as per new rule User check ambient feature
            knLogger.info(methodName, "pocHomeAutoAssignFlag is enabled, setting serviceAuthStatus for all MDNs, old BAU");
            bulkSubsProvInfoDTO.setServiceAuthStatus(
                    KnConstants.SERVICE_AUTH_STATUS.fromValue(
                            genInfoUtil.calculateServiceAuthStatus(serviceStatusOp, serviceStatusAuthUser)
                    )
            );
        } else {
            knLogger.info(methodName, "pocHomeAutoAssignFlag is disabled, setting serviceAuthStatus to PRE-PROVISIONED for all MDNs");
            bulkSubsProvInfoDTO.setServiceAuthStatus(
                    KnConstants.SERVICE_AUTH_STATUS.fromValue(
                            KnBulkOpsConstants.SERVICE_AUTH_STATUS.PRE_PROVISIONED.value()
                    )
            );
        }
        bulkSubsProvInfoDTO.setPreviousServiceAuthStatusToStore(bulkSubsProvInfoDTO.getServiceAuthStatus().value());

        // Removing Validation Logic for ExtCorpId cannot be null as it is already validated in subsmgmt
        // Removing Validation of validatorFwk

        // setting License Type
        String deviceSharingFlag = microServicesParamNameValueMap.get(KnBulkOpsConstants.DEVICE_SHARING_FEATURE_FLAG);
        partialRespDTO.setDeviceSharing(Integer.parseInt(deviceSharingFlag));
        knLogger.info(methodName, "deviceSharingFlag", deviceSharingFlag);
        int deviceSharewifiFlag = Integer.parseInt(microServicesParamNameValueMap.get(KnBulkOpsConstants.AUTO_DEVICESHARE_WIFI_CC));
        knLogger.info(methodName, "deviceSharewifiFlag-->", deviceSharewifiFlag);
        if((bulkSubsProvInfoDTO.getSubscriberClientType() == KnBulkOpsConstants.SUBSCRIBERS_CLIENT_TYPE.WIFIONLY.value()
                || bulkSubsProvInfoDTO.getSubscriberClientType() == KnBulkOpsConstants.SUBSCRIBERS_CLIENT_TYPE.CROSS_CARRIER_PTT_CLIENT.value()
                || bulkSubsProvInfoDTO.getSubscriberClientType() == KnBulkOpsConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value()
                || bulkSubsProvInfoDTO.getSubscriberClientType() == KnBulkOpsConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value()
            )
            && Integer.parseInt(deviceSharingFlag) == KnBulkOpsConstants.ENABLED
            && deviceSharewifiFlag == KnBulkOpsConstants.ENABLED
        ) {
            knLogger.debug(methodName, "license type for wifi , crosscarrier and its ptt ");
            bulkSubsProvInfoDTO.setLicenseType(KnBulkOpsConstants.USER_LICENSE_TYPE);
        }
    }

    /**
     * Retrieve POC and Presence homes for all subscribers
     * Returns HomeMapping containing both mdnToPocHomeMap and mdnToPresenceHomeMap and updateCorpHome flag
     * This avoids rebuilding the presence map in downstream methods
     */
    private HomeMapping retrievePocAndPresenceHomes(
            KnXDMBulkSubsProvInfoDTO bulkSubsProvInfoDTO,
            KnBulkOpsCorpProfileDAO.KnCorpProfileInfo corpProfileInfoDTO,
            KnBulkOpsXDMServerDAO bulkOpsXDMServerDAO,
            KnXDMBulkOpsRespDTO partialRespDTO,
            Map<String, String> microServicesParamNameValueMap,
            KnPersisterTxn persisterTxn) throws Exception {
        String methodName = "retrievePocAndPresenceHomes";

        // Retrieving POC Home for Subscribers
        Map<String, String> mdnToPocHomeMap = new HashMap<>();
        Map<String, String> mdnToPresenceHomeMap = new HashMap<>();
//        boolean updateCorpHome = false;

//        if (bulkSubsProvInfoDTO.getCorporateSubscriptionType() == KnBulkOpsConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
//            // Validate And Get Corp Anchor POC Home for bulk subscribers (supports multiple POC homes)
//            if (corpProfileInfoDTO != null){
//                Map<String, Object> corpAnchorMap = bulkOpsInfoUtil.validateAndGetCorpAnchorPocHome(
//                        bulkOpsInfoUtil.fetchMDNFromSubscriberList(bulkSubsProvInfoDTO.getSubscriberList()),
//                        corpProfileInfoDTO,
//                        bulkOpsXDMServerDAO,
//                        bulkSubsProvInfoDTO,
//                        partialRespDTO,
//                        persisterTxn
//                );
//                // Extract MDN->POC home mapping and updateCorpHome flag
//                mdnToPocHomeMap = (Map<String, String>) corpAnchorMap.get(KnBulkOpsConstants.MDN_POC_MAP_KEY);
//                updateCorpHome = (Boolean) corpAnchorMap.get(KnBulkOpsConstants.UPDATE_CORP_HOME);
//
//                knLogger.debug(methodName, "mdnToPocHomeMap - ", mdnToPocHomeMap, " updateCorpHome: ", updateCorpHome);
//            }
//        }
//        if (mdnToPocHomeMap == null) {
//            KnBulkOpsPocConfigDTO config = bulkOpsXDMServerDAO.getPartitionAndCapacityConfig(null, persisterTxn);
//            mdnToPocHomeMap = bulkOpsInfoUtil.findPocHomeByPartitionType(
//                    bulkOpsInfoUtil.fetchMDNFromSubscriberList(bulkSubsProvInfoDTO.getSubscriberList()),
//                    config,
//                    bulkOpsXDMServerDAO,
//                    bulkSubsProvInfoDTO,
//                    partialRespDTO,
//                    persisterTxn
//            );
//        }

        knLogger.debug(methodName, "Setting POC home for bulk subscribers");
        List<String> mdnList = bulkOpsInfoUtil.fetchMDNFromSubscriberList(bulkSubsProvInfoDTO.getSubscriberList());

        String pocHomeAutoAssignFlag = microServicesParamNameValueMap.get(KnBulkOpsConstants.POCHOME_AUTOASSIGN_FLAG);
        knLogger.info(methodName, "pocHomeAutoAssignFlag :", pocHomeAutoAssignFlag);
        if (pocHomeAutoAssignFlag != null && Integer.parseInt(pocHomeAutoAssignFlag) == KnBulkOpsConstants.ENABLED) {
            knLogger.debug(methodName, "pocHomeAutoAssignFlag is enabled, retrieving POC home for all MDNs, old BAU");
            mdnToPocHomeMap = provInfoUtil.getSubsPoCHome(mdnList, persisterTxn);
            mdnToPresenceHomeMap = bulkOpsInfoUtil.fetchSubsPresenceHome(
                    bulkOpsInfoUtil.fetchMDNFromSubscriberList(bulkSubsProvInfoDTO.getSubscriberList()),
                    mdnToPocHomeMap,
                    bulkOpsXDMServerDAO,
                    bulkSubsProvInfoDTO,
                    partialRespDTO,
                    persisterTxn
            );
        } else {
            knLogger.info(methodName, "pocHomeAutoAssignFlag is disabled, setting pocHome & presenseHome as 0 for all MDNs");
            mdnToPocHomeMap = new LinkedHashMap<>();
            for (String mdn : mdnList) {
                mdnToPocHomeMap.put(mdn, String.valueOf(0));
                mdnToPresenceHomeMap.put(mdn, String.valueOf(0));
            }
        }


        // Populate POC Home, Presence Home, McId, McpttId, McVideoId, McDataId to each subscriber entry in subscriberList
        if (bulkSubsProvInfoDTO.getSubscriberList() != null && !bulkSubsProvInfoDTO.getSubscriberList().isEmpty()) {
            for (var subscriberEntry : bulkSubsProvInfoDTO.getSubscriberList()) {
                final String mdn = subscriberEntry.getMdn();

                // Set POC Home
                if (mdnToPocHomeMap != null) {
                    final String pocHome = mdnToPocHomeMap.get(mdn);
                    if (pocHome != null) {
                        subscriberEntry.setPocHome(pocHome);
                    }
                }

                // Set Presence Home
                if (mdnToPresenceHomeMap != null) {
                    final String presenceHome = mdnToPresenceHomeMap.get(mdn);
                    if (presenceHome != null) {
                        subscriberEntry.setPresenceHome(presenceHome);
                    }
                }

                // Populating default MCS IDs
                subscriberEntry.setMcId(KnBulkOpsConstants.TELURI+mdn);
                subscriberEntry.setMcpttId(KnBulkOpsConstants.TELURI+mdn);
                subscriberEntry.setMcVideoId(KnBulkOpsConstants.TELURI+mdn);
                subscriberEntry.setMcDataId(KnBulkOpsConstants.TELURI+mdn);
            }
            knLogger.debug(methodName, "Set POC home and Presence home for ",
                          bulkSubsProvInfoDTO.getSubscriberList().size(), " subscribers");
        }

        // Return both maps and updateCorpHome flag wrapped in HomeMapping to avoid rebuilding mdnToPresenceHomeMap later
//        return new HomeMapping(mdnToPocHomeMap, mdnToPresenceHomeMap, updateCorpHome);
        return new HomeMapping(mdnToPocHomeMap, mdnToPresenceHomeMap);
    }

    /**
     * Configure corporate auto-pairing and dispatch settings
     */
    private void configureCorporateAutoPairing(
            KnXDMBulkSubsProvInfoDTO bulkSubsProvInfoDTO,
            KnBulkOpsCorpProfileDAO.KnCorpProfileInfo corpProfileInfoDTO,
            KnBulkOpsSubsProfileDAO bulkOpsSubsProfileDAO,
            KnXDMBulkOpsRespDTO partialRespDTO,
            Map<String, String> paramNameValueMap,
            KnPersisterTxn persisterTxn) throws Exception {
        String methodName = "configureCorporateAutoPairing";

        // Corporate Auto Pairing and Setting Dispatch Type
        bulkSubsProvInfoDTO.setAutoPair(bulkSubsProvInfoDTO.getPairingInd());
        bulkSubsProvInfoDTO.setPairingInd(Boolean.FALSE);
        bulkSubsProvInfoDTO.setDispatchGroupMember(KnBulkOpsConstants.DISPATCH_GROUP_MEMBER.NOT_MEMBER.value());

        if (bulkSubsProvInfoDTO.getCorporateSubscriptionType() == KnBulkOpsConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()){
            partialRespDTO.setIsExistingCorp(corpProfileInfoDTO.getCorpId());

            if (corpProfileInfoDTO.getCorpId() > 0) {
                knLogger.debug(methodName, "corporate profile already exists with corp id [", corpProfileInfoDTO.getCorpId(), "] ", "for extCorpId ", bulkSubsProvInfoDTO.getExtCorpId());
                if (corpProfileInfoDTO.getPairedContactListId() > 0) {
                    int corpAutoPairingCnt = Integer.parseInt(paramNameValueMap.get(KnBulkOpsConstants.CORP_AUTO_PAIRING_SIZE));
                    int corpSubsCount = bulkOpsSubsProfileDAO.retrieveCorpSubscriberCount(corpProfileInfoDTO.getCorpId(), persisterTxn);
                    knLogger.info(methodName, "existing subs count - ", corpSubsCount, "autopair limit - ", corpAutoPairingCnt);
                    if(corpSubsCount >= corpAutoPairingCnt){
                        // to disable auto pairing
                        partialRespDTO.setCorpAutoPairing(false);
                        partialRespDTO.setIsOldCorp(false);
                    } else {
                        // to enable auto pairing
                        if (KnBulkOpsConstants.SUBSCRIBERS_CLIENT_TYPE.HANDSET.value() == bulkSubsProvInfoDTO.getSubscriberClientType()){
                            partialRespDTO.setCorpAutoPairing(true);
                            partialRespDTO.setIsOldCorp(true);
                        }
                    }
                }
            } else {
                int enableAutoPair = Integer.parseInt(paramNameValueMap.get(KnBulkOpsConstants.ENABLE_CORP_AUTO_PAIRING));
                if (enableAutoPair == KnBulkOpsConstants.AUTO_PAIR_ENABLED && bulkSubsProvInfoDTO.isAutoPair()) {
                    knLogger.info(methodName, "System flag corp auto pairing is enabled and CBE is also configured.");
                    if (KnBulkOpsConstants.SUBSCRIBERS_CLIENT_TYPE.HANDSET.value() == bulkSubsProvInfoDTO.getSubscriberClientType()) {
                        // to enable auto pairing
                        partialRespDTO.setCorpAutoPairing(true);
                        partialRespDTO.setIsOldCorp(false);
                    }
                }
            }
        }
    }

    /**
     * Create or update corporate profile
     */
    private void createOrUpdateCorporateProfile(
            KnXDMBulkSubsProvInfoDTO bulkSubsProvInfoDTO,
            KnBulkOpsCorpProfileDAO.KnCorpProfileInfo corpProfileInfoDTO,
            Map<String, String> mdnToPocHomeMap,
//            boolean updateCorpHome,
            KnBulkOpsCorpProfileDAO bulkOpsCorpProfileDAO,
            KnBulkOpsXDMServerDAO bulkOpsXDMServerDAO,
//            KnBulkOpsSubsProfileDAO bulkOpsSubsProfileDAO,
            KnPersisterTxn persisterTxn) throws Exception {
        String methodName = "createOrUpdateCorporateProfile";

        if (bulkSubsProvInfoDTO.getCorporateSubscriptionType() == KnBulkOpsConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
            corpProfileInfoDTO.setXDMSHome(KnBulkOpsDBUtil.getXdmPttServerId());
            corpProfileInfoDTO.setHierarchyType(bulkSubsProvInfoDTO.getHierarchyType());

            // Finding Last MDN Poc Home to set Corp Home
            String poCPttServerId = null;
            if (mdnToPocHomeMap != null && !mdnToPocHomeMap.isEmpty()
                    && bulkSubsProvInfoDTO.getSubscriberList() != null
                    && !bulkSubsProvInfoDTO.getSubscriberList().isEmpty()) {
                // Since subscriber list is pre-sorted and LinkedHashMap maintains order,
                // the last subscriber's MDN maps to the last POC home
                String lastMdn = bulkSubsProvInfoDTO.getSubscriberList()
                        .get(bulkSubsProvInfoDTO.getSubscriberList().size() - 1)
                        .getMdn();
                poCPttServerId = mdnToPocHomeMap.get(lastMdn);
            }

            int svcDispatchType = genInfoUtil.retrieveXDMSServiceConfig(KnBulkOpsDBUtil.getXdmPttServerId(), persisterTxn).getWebDispatchEnabled();
            int corpDispatchType = corpProfileInfoDTO.getWebDispatchEnabled();
            knLogger.debug(methodName, "svcDispatchType & corpDispatchType - ", svcDispatchType, corpDispatchType);

            if(corpProfileInfoDTO.getCorpId() > 0) {
                knLogger.debug(methodName, "corporate profile already exists with corp id [", corpProfileInfoDTO.getCorpId(), "] ", "for extCorpId ", bulkSubsProvInfoDTO.getExtCorpId());
                final List<Integer> clientTypeList = List.of(
                    KnBulkOpsConstants.SUBSCRIBERS_CLIENT_TYPE.POC_NNI_Alias_MDN.value(),
                    KnBulkOpsConstants.SUBSCRIBERS_CLIENT_TYPE.POC_NNI_Group_MDN.value(),
                    KnBulkOpsConstants.SUBSCRIBERS_CLIENT_TYPE.SGMDNPATCH.value()
                );
                if (bulkSubsProvInfoDTO.getCorporateName() != null) {
                    corpProfileInfoDTO.setCorporateName(bulkSubsProvInfoDTO.getCorporateName());
                }

                // update the Corp home if poc is full and subscriber moving to new poc (for load based partioning)
//                if (updateCorpHome) {
//                    // checks if count for su/sg client is greater than 0 then we will go for alarm generation.
//                    if (bulkOpsSubsProfileDAO.getSubsCountOfClientTypeForCorp(
//                            corpProfileInfoDTO.getCorpId(),
//                            clientTypeList,
//                            persisterTxn) > 0) {
//                        knLogger.debug(methodName, "Generating Alarm for Corp Home Change as there are existing SU/SG clients under the corp");
//                        String serviceName = bulkSubsProvInfoDTO.getExtCorpId() + ":" + corpProfileInfoDTO.getPocHome() + ":" + poCPttServerId;
//                        knLogger.info(methodName, "serviceName", serviceName, "Alarm Id 17608");
//                        KnAlarmGeneratorUtil.generateAlarm2(KnAlarmConstants.ALARM_CORPORATE_POC_HOME_RESET, KnAlarmConstants.SEVERITY_MAJOR, serviceName);
//                    }
//                    knLogger.info(methodName, "update new poc home for corp ");
//                    corpProfileInfoDTO.setPocHome(poCPttServerId);
//                } else {
//                    corpProfileInfoDTO.setPocHome(null);
//                }
                long corpProfileUpdateTime = Calendar.getInstance().getTimeInMillis();
                corpProfileInfoDTO.setLastProfileUpdateTime(corpProfileUpdateTime);

                // Update corporate profile in database
                bulkOpsCorpProfileDAO.updateCorporateProfile(corpProfileInfoDTO, persisterTxn);
                knLogger.info(methodName, "Corporate profile updated successfully for corpId: ", corpProfileInfoDTO.getCorpId());

                // Set dispatch type for DISPATCH client type when web dispatch is enabled at service or corp level
                final int subscriberClientType = bulkSubsProvInfoDTO.getSubscriberClientType();
                final boolean isDispatchClient = (KnBulkOpsConstants.SUBSCRIBERS_CLIENT_TYPE.DISPATCH.value() == subscriberClientType);
                final boolean isWebDispatchEnabled = (KnBulkOpsConstants.WEB_DISPATCH_ENABLED == svcDispatchType
                        || KnBulkOpsConstants.WEB_DISPATCH_ENABLED == corpDispatchType);

                if (isDispatchClient && isWebDispatchEnabled) {
                    final var subscriberList = bulkSubsProvInfoDTO.getSubscriberList();
                    if (subscriberList != null && !subscriberList.isEmpty()) {
                        subscriberList.forEach(subscriberEntry ->
                            subscriberEntry.setDispatchType(KnBulkOpsConstants.WEB_DISPATCH_ENABLED));
                    }
                }
            } else {
                knLogger.debug(methodName, "creating the corporate Profile");
                corpProfileInfoDTO.setCorporateName(bulkSubsProvInfoDTO.getCorporateName());
                corpProfileInfoDTO.setCorpFS2(featureSetUtil.getDefFinalCorpFS());
                corpProfileInfoDTO.setOpsCorpFS2(featureSetUtil.getDefFinalOpsCorpFS());

//                KnPOCSvcConfigDTO pocSvcConfig = bulkOpsXDMServerDAO.retrievePOCSvcConfig(poCPttServerId, persisterTxn);
//                knLogger.info(methodName, "Retrieved POC Service Config for POC home: ", poCPttServerId);
                corpProfileInfoDTO.setDynamicQosFlag(0);
                corpProfileInfoDTO.setFeatureRelVersion(currentFsVersion);

                String xdmCorpFS2Set = KnGeneralUtil.getDefaultXDMCorpFS2Set(KnBulkOpsConstants.XDMCORPFS2_SET.EMERGENCY_CONF_TIMER_FEATURE.value(), KnBulkOpsConstants.TRUE);
                xdmCorpFS2Set = featureSetUtil.calculateXDMCorpFS2(xdmCorpFS2Set, KnBulkOpsConstants.XDMCORPFS2_SET.EMERGENCY_CONF_TIMER_FEATURE.value(), KnBulkOpsConstants.TRUE);
                corpProfileInfoDTO.setXdmCorpFS2Set(xdmCorpFS2Set);

                // Set POC home for new corporate
                corpProfileInfoDTO.setPocHome(poCPttServerId);

                // Create Corporate Profile in database
                int createdCorpId = bulkOpsCorpProfileDAO.createCorporateProfile(corpProfileInfoDTO, persisterTxn);
                knLogger.info(methodName, "Corporate profile created successfully with corpId: ", createdCorpId);
                // TODO: Add Success Pegs
                corpProfileInfoDTO = bulkOpsCorpProfileDAO.retrieveCorporateProfile(bulkSubsProvInfoDTO.getExtCorpId(), persisterTxn);

                final int subscriberClientType = bulkSubsProvInfoDTO.getSubscriberClientType();
                final boolean isDispatchClient = (KnBulkOpsConstants.SUBSCRIBERS_CLIENT_TYPE.DISPATCH.value() == subscriberClientType);
                final boolean isWebDispatchEnabled = (KnBulkOpsConstants.WEB_DISPATCH_ENABLED == svcDispatchType);

                if (isDispatchClient && isWebDispatchEnabled) {
                    final var subscriberList = bulkSubsProvInfoDTO.getSubscriberList();
                    if (subscriberList != null && !subscriberList.isEmpty()) {
                        subscriberList.forEach(subscriberEntry ->
                                subscriberEntry.setDispatchType(KnBulkOpsConstants.WEB_DISPATCH_ENABLED));
                    }
                }
            }
            bulkSubsProvInfoDTO.setCorpId(String.valueOf(corpProfileInfoDTO.getCorpId()));
        }
    }

    /**
     * Process packages and generate all feature sets
     * Returns profileCreationTime
     * @param mdnToPresenceHomeMap Pre-built map to avoid rebuilding (performance optimization)
     */
    private long processPackagesAndFeatureSets(
            KnXDMBulkSubsProvInfoDTO bulkSubsProvInfoDTO,
            Map<String, String> mdnToPocHomeMap,
            Map<String, String> mdnToPresenceHomeMap,
            KnBulkOpsCorpProfileDAO.KnCorpProfileInfo corpProfileInfoDTO,
            Map<String, String> paramNameValueMap,
            KnPersisterTxn persisterTxn,
            String tierPackageId,
            Map<String, Integer> addonPackageIds) throws Exception {
        String methodName = "processPackagesAndFeatureSets";

        Map<String, Integer> addPkgIds = new HashMap<String, Integer>();
        if (bulkSubsProvInfoDTO.getPkgIdMap() != null
                && bulkSubsProvInfoDTO.getPkgIdMap().get(KnBulkOpsConstants.ADD_ACTION) != null) {
            addPkgIds = bulkSubsProvInfoDTO.getPkgIdMap().get(KnBulkOpsConstants.ADD_ACTION);
            for (Map.Entry<String, Integer> entry : addPkgIds.entrySet()) {
                if (entry.getValue().intValue() == KnBulkOpsConstants.TIER_PKG_TYPE.intValue()) {
                    tierPackageId = entry.getKey();
                } else if (entry.getValue().intValue() == KnBulkOpsConstants.ADDON_PKG_TYPE.intValue()) {
                    addonPackageIds.put(entry.getKey(), entry.getValue());
                }
            }
        }
        knLogger.info(methodName, "addPkgIds map ", addPkgIds);

        //check if the received subsFeatureSet1 is null or 0 then apply the default subsFeatureSet1.
        if (bulkSubsProvInfoDTO.getSubsFS2() == null) {
            BitSet finalFSBitSet = new BitSet(Long.SIZE);
            String basePkgCode = paramNameValueMap.get(KnBulkOpsConstants.BASE_PKGCODE);
            String basePkgDefFS = featureSetUtil.getDefSubsFeatureSetForBasePkg(bulkSubsProvInfoDTO.getPublicSubscriptionType(),
                    bulkSubsProvInfoDTO.getCorporateSubscriptionType(), bulkSubsProvInfoDTO.getSubscriberClientType(),
                    basePkgCode, KnBulkOpsDBUtil.getXdmPttServerId());
            knLogger.debug(methodName, "base pkg is applied - ", basePkgDefFS);
            BitSet basePkgDefFSBitSet = featureSetUtil.convertHexStringToBitSet(basePkgDefFS);
            finalFSBitSet.or(basePkgDefFSBitSet);

            if (!addPkgIds.isEmpty()) {
                String pkgCodesFS = featureSetUtil.getDefSubsFeatureSetForPkgCodes(bulkSubsProvInfoDTO.getPublicSubscriptionType(),
                        bulkSubsProvInfoDTO.getCorporateSubscriptionType(),
                        bulkSubsProvInfoDTO.getSubscriberClientType(),
                        addPkgIds,
                        KnBulkOpsDBUtil.getXdmPttServerId());
                knLogger.debug(methodName, "addPkgIds is present with subscriberFS - ", pkgCodesFS);
                BitSet pkgCodes_BiSet = featureSetUtil.convertHexStringToBitSet(pkgCodesFS);
                finalFSBitSet.or(pkgCodes_BiSet);
            }
            bulkSubsProvInfoDTO.setSubsFS2(featureSetUtil.convertBitSetToHexString(finalFSBitSet));
        }

        // Removed calculating subsFS as we are not having that in the request getProvFSMap

        //Generate the SubsFeatureSet by performing the BitMask with License SubsFSBITMASK
        String subsFS2 = bulkSubsProvInfoDTO.getSubsFS2();
        String provFS2 = subsFS2;
        String provFS2BitMask = subsFS2;
        knLogger.debug(methodName, "subsFS2 :", subsFS2, "provFS2 :", provFS2, "provFS2BitMask :", provFS2BitMask);
        bulkSubsProvInfoDTO.setSubsFS2(featureSetUtil.generateSubsFeatureSet(subsFS2, provFS2, provFS2BitMask));
        knLogger.debug(methodName, "final subsFS : ", subsFS2);

        // Removed code for setting ClientFS based upon MCS Compliance as the parameter is not there in the create Bulk Subscriber API request

        int defaultClientPVMajorVersion = 1;
        String clientCapOverrideBitMask = featureSetUtil.getClientCapabilityBitMask(KnBulkOpsDBUtil.getXdmPttServerId(), defaultClientPVMajorVersion);
        knLogger.debug(methodName, "defaultClientPVMajorVersion ", defaultClientPVMajorVersion, " ,clientCapOverrideBitMask=", clientCapOverrideBitMask);
        String clientFS2 = featureSetUtil.getDefFinalClientFS(clientCapOverrideBitMask);
        String corpAdminFS2 = featureSetUtil.getDefFinalCorpAdminFS();
        String xdmsFs2 = featureSetUtil.getDefFinalXdmsFS();
        String userProfileFS2 = featureSetUtil.getDefFinalUserProfileFS();

        bulkSubsProvInfoDTO.setClientFS2(clientFS2);
        bulkSubsProvInfoDTO.setCorpAdminFS2(corpAdminFS2);
        bulkSubsProvInfoDTO.setUserProfileFS2(userProfileFS2);

        // Use pre-built mdnToPresenceHomeMap (passed as parameter) - no need to rebuild it
        // This avoids an extra loop through subscriber list improving performance

        //Generate the ActiveFeatureSet
        bulkOpsInfoUtil.generateActiveFS2ForBulkSubscribers(
            bulkSubsProvInfoDTO, mdnToPocHomeMap, mdnToPresenceHomeMap, corpProfileInfoDTO, clientFS2, subsFS2,
            corpAdminFS2, clientCapOverrideBitMask, xdmsFs2, userProfileFS2, featureSetUtil
        );

        // Update XDMS Feature Set 2 based on Corporate Level Location Feature
        String updatedXdmsFs2 = xdmsFs2;
        String xdmCorpFS2Set = genInfoUtil.selectXDMCorpFS(corpProfileInfoDTO.getCorpId(), true, persisterTxn);
        knLogger.info(methodName, "xdmCorpFS2Set: ", xdmCorpFS2Set);
        boolean corpLevelLocationFlag = false;
        if (xdmCorpFS2Set != null) {
            corpLevelLocationFlag = KnGeneralUtil.getFeatureBitValue(xdmCorpFS2Set, KnBulkOpsConstants.XDMCORPFS2_SET.LOCATION_ENABLED.value());
        } else {
            knLogger.warn(methodName, "xdmCorpFS2Set is null");
        }
        if (corpLevelLocationFlag) {
            BitSet xdmsFs2check = KnGeneralUtil.convertHexStringToBitSet(xdmsFs2);
            xdmsFs2check.set(KnBulkOpsConstants.FEATURE_SET.ONDEMLOCATION.value(), true);
            updatedXdmsFs2 = KnGeneralUtil.convertBitSetToHexString(xdmsFs2check);
            knLogger.info(methodName, "updatedXdmsFs2: ", updatedXdmsFs2);
            bulkSubsProvInfoDTO.setXdmsFS2(updatedXdmsFs2);
        }
        bulkSubsProvInfoDTO.setXdmsFS2(updatedXdmsFs2);

        //During create subscriber will not belong to any profile hence default profile flag is set to 1 and profile index to 0
        bulkSubsProvInfoDTO.setIsDefaultProfile(KnBulkOpsConstants.IS_DEFAULT_PROFILE);
        bulkSubsProvInfoDTO.setUserProfileIndex(KnBulkOpsConstants.USER_PROFILE_INDEX);
        long profileCreationTime = Calendar.getInstance().getTimeInMillis();
        bulkSubsProvInfoDTO.setProfileCreationTime(profileCreationTime);
        //since creation time is same as last profile time while create
        bulkSubsProvInfoDTO.setLastProfileUpdateTime(profileCreationTime);

        bulkSubsProvInfoDTO.setFeatureRelVersion(currentFsVersion);

        return profileCreationTime;
    }
}
