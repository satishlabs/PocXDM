package com.kodiak.xdms.server.bulkops.utils;

import com.kodiak.common.commdto.common.KnBulkSubsProfileDTO;
import com.kodiak.common.commdto.common.KnBulkSubscriberEntry;
import com.kodiak.common.commdto.request.KnXDMBulkSubsProvInfoDTO;
import com.kodiak.common.commdto.response.IXDMResponseDTO;
import com.kodiak.common.commdto.response.KnBulkOpsErrorDetail;
import com.kodiak.common.commdto.response.KnXDMBulkOpsRespDTO;
import com.kodiak.common.dao.KnConnectionException;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersistenceException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.exception.KnException;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnGeneralCacheUtil;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.featureset.KnFeatureSetUtil;
import com.kodiak.xdms.server.bulkops.KnBulkOpsException;
import com.kodiak.xdms.server.bulkops.dao.KnBulkOpsCorpProfileDAO;
import com.kodiak.xdms.server.bulkops.dao.KnBulkOpsSubsProfileDAO;
import com.kodiak.xdms.server.bulkops.dao.KnBulkOpsXDMServerDAO;
import com.kodiak.xdms.server.bulkops.dao.KnPOCSubscrInfoDAO;
import com.kodiak.xdms.server.bulkops.dto.common.KnBulkOpsPocConfigDTO;
import com.kodiak.xdms.server.bulkops.dto.common.KnMDNValidationResult;
import com.kodiak.xdms.server.bulkops.resources.KnBulkOpsConstants;
import com.kodiak.xdms.server.bulkops.resources.KnBulkOpsErrorCodes;
import com.kodiak.xdms.server.common.KnXDMServerException;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.configuration.cache.ICacheManager;
import com.kodiak.xdms.server.common.configuration.manager.KnConfigurationsManager;
import com.kodiak.xdms.server.common.dto.common.KnAPNConfigDTO;
import com.kodiak.xdms.server.common.dto.common.KnSubsProfileDTO;
import com.kodiak.xdms.server.common.resources.KnCacheKeys;
import com.kodiak.xdms.server.common.resources.KnErrorCodes;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dto.common.KnClientTypeConfigDTO;
import com.kodiak.xdms.server.corpmgmt.dao.persister.db.tables.KnCorpDBTablesRegistry;
import com.kodiak.xdms.server.corpmgmt.dao.persister.db.tables.xdm.KnXDMCorpListMemberDAO;
import com.kodiak.xdms.server.pubmgmt.clientintf.IPubClientIntf;
import com.kodiak.xdms.server.pubmgmt.clientintf.impl.KnPubClientIntf;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnIPPubSubsDTO;
import com.kodiak.xdms.server.subsmgmt.KnProvException;
import com.kodiak.xdms.server.subsmgmt.business.KnProvBOException;
import com.kodiak.xdms.server.subsmgmt.clientIntf.IProvClientIntf;
import com.kodiak.xdms.server.subsmgmt.clientIntf.impl.KnProvClientImpl;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.kodiak.xdms.server.bulkops.resources.KnBulkOpsErrorCodes.BOEntity.INACTIVE_SUBSCRIBER_DELETE_IN_PROGRESS;
import static com.kodiak.xdms.server.common.resources.KnConstants.*;
import static com.kodiak.xdms.server.common.resources.KnConstants.DIR_DOC_NAME;
import static com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME;

public class KnBulkOpsInfoUtil {
    private static final KnLogger knLogger = KnLogger.getLogger(KnBulkOpsInfoUtil.class);
    private static KnBulkOpsInfoUtil instance = null;
    private KnGeneralCacheUtil generalCacheUtil = null;

    IProvClientIntf provClientIntf = KnProvClientImpl.getInstance();
    IPubClientIntf pubClientIntf = new KnPubClientIntf();
    KnGenInfoUtil genInfoUtil = KnGenInfoUtil.getInstance();
    private KnBulkOpsErrorDetail knBulkOpsErrorDetail = new KnBulkOpsErrorDetail();

    public KnBulkOpsInfoUtil() {
        generalCacheUtil = KnGeneralCacheUtil.getInstance();
    }

    public static synchronized KnBulkOpsInfoUtil getInstance() {
        if (instance == null) {
            instance = new KnBulkOpsInfoUtil();
        }
        return instance;
    }


    /**
     * Extract MDN list from bulk subscriber entries
     *
     * @param subscriberList List of KnBulkSubscriberEntry objects
     * @return List of MDN strings
     */
    public List<String> fetchMDNFromSubscriberList(List<KnBulkSubscriberEntry> subscriberList) {
        String methodName = "fetchMDNFromSubscriberList(List<KnBulkSubscriberEntry>)";
        knLogger.debug(methodName, "Fetching MDN from subscriber list");
        List<String> mdnList = subscriberList.parallelStream()
                .map(KnBulkSubscriberEntry::getMdn)
                .filter(mdn -> mdn != null && !mdn.trim().isEmpty())
                .map(String::trim)
                .collect(Collectors.toList());
        knLogger.debug(methodName, "Fetched MDN List: ", mdnList);
        return mdnList;
    }

    /**
     * Generate MDN list with "tel:+" prefix
     *
     * @param subscriberList List of KnBulkSubscriberEntry objects
     * @return List of MDN strings with "tel:+" prefix
     */
    public List<String> mdnWithTelPrefix(List<KnBulkSubscriberEntry> subscriberList) {
        String methodName = "mdnWithTelPrefix(List<KnBulkSubscriberEntry>)";
        knLogger.debug(methodName, "Generating MDN list with tel:+ prefix");
        List<String> mdnListWithPrefix = subscriberList.parallelStream()
                .map(KnBulkSubscriberEntry::getMdn)
                .map(mdn -> {
                    if (mdn.toLowerCase().startsWith(KnBulkOpsConstants.TELURI)) {
                        return mdn;
                    }
                    return KnBulkOpsConstants.TELURI + mdn;
                })
                .collect(Collectors.toList());
        knLogger.debug(methodName, "Generated MDN List With Prefix: ", mdnListWithPrefix);
        return mdnListWithPrefix;
    }

    /**
     * Extract and add invalid MDNs from validation result to response DTO
     *
     * @param validationResult MDN validation result containing invalid MDN details
     * @param responseDTO Response DTO to populate with failures
     */
    public void addInvalidMdnsToResponse(KnMDNValidationResult validationResult, KnXDMBulkOpsRespDTO responseDTO) {
        String methodName = "addInvalidMdnsToResponse(KnMDNValidationResult, KnXDMBulkOpsRespDTO)";

        if (validationResult == null || responseDTO == null) {
            knLogger.warn(methodName, "Validation result or response DTO is null");
            return;
        }

        List<KnMDNValidationResult.InvalidMdnDetail> invalidMdns = validationResult.getInvalidMdns();
        if (invalidMdns == null || invalidMdns.isEmpty()) {
            knLogger.debug(methodName, "No invalid MDNs to add");
            return;
        }

        for (KnMDNValidationResult.InvalidMdnDetail invalidMdn : invalidMdns) {
            responseDTO.addFailure(invalidMdn.getMdn(), invalidMdn.getErrorCode(), invalidMdn.getErrorMessage());
        }

        knLogger.debug(methodName, "Added ", invalidMdns.size(), " invalid MDNs to response");
    }

    /**
     * Filter subscriber list to contain only valid MDNs
     *
     * @param bulkSubsProvInfoDTO Request DTO containing subscriber list to filter
     * @param validMdnSet Set of valid MDNs for fast O(1) lookup
     * @return Number of subscribers filtered (valid count)
     */
    public int filterSubscriberListByValidMdns(KnXDMBulkSubsProvInfoDTO bulkSubsProvInfoDTO, Set<String> validMdnSet) {
        String methodName = "filterSubscriberListByValidMdns(KnXDMBulkSubsProvInfoDTO, Set<String>)";

        if (bulkSubsProvInfoDTO == null || bulkSubsProvInfoDTO.getSubscriberList() == null) {
            knLogger.warn(methodName, "DTO or subscriber list is null");
            return 0;
        }

        if (validMdnSet == null || validMdnSet.isEmpty()) {
            knLogger.debug(methodName, "Valid MDN set is empty, clearing subscriber list");
            int originalSize = bulkSubsProvInfoDTO.getSubscriberList().size();
            bulkSubsProvInfoDTO.getSubscriberList().clear();
            return originalSize;
        }

        List<KnBulkSubscriberEntry> originalList = bulkSubsProvInfoDTO.getSubscriberList();
        int originalSize = originalList.size();

        List<KnBulkSubscriberEntry> filteredList = originalList.parallelStream()
                .filter(subscriber -> validMdnSet.contains(subscriber.getMdn()))
                .collect(Collectors.toList());

        bulkSubsProvInfoDTO.setSubscriberList(filteredList);

        int filteredSize = filteredList.size();
        int removedCount = originalSize - filteredSize;

        knLogger.info(methodName, "Filtered subscriber list - Original: ", originalSize,
                ", Valid: ", filteredSize, ", Removed: ", removedCount);

        return filteredSize;
    }

    /**
     * Validate subscriber client type against system configuration
     *
     * @param subscriberClientType Subscriber client type to validate
     * @param persisterTxn Database transaction
     * @throws KnBOException if business rule violation occurs
     * @throws KnBulkOpsException if client type is disabled
     */
    public void validateSubsClientType(Integer subscriberClientType,
                                       KnGenInfoUtil genInfoUtil,
                                       KnPersisterTxn persisterTxn) throws KnBOException, KnBulkOpsException {
        String methodName = "validateSubsClientType(Integer, KnPersisterTxn)";
        knLogger.debug(methodName, "Validating subscriber client type: ", subscriberClientType);
        if (subscriberClientType == KnBulkOpsConstants.SUBSCRIBERS_CLIENT_TYPE.MOBILE_CLIENT.value()
                || subscriberClientType == KnBulkOpsConstants.SUBSCRIBERS_CLIENT_TYPE.THIRDPARTYDISPATCHERCLIENT.value()
                || subscriberClientType == KnBulkOpsConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value()
                || subscriberClientType == KnBulkOpsConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value()
                || subscriberClientType == KnBulkOpsConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value()
                || subscriberClientType == KnBulkOpsConstants.SUBSCRIBERS_CLIENT_TYPE.SGMDNPATCH.value()) {
            KnClientTypeConfigDTO clientTypeConfigDTO = genInfoUtil.getClientTypeConfig(subscriberClientType, persisterTxn);
            if (clientTypeConfigDTO.getIsEnable() != KnBulkOpsConstants.CLIENT_TYPE_CONFIGURATION.ENABLED.value()) {
                knLogger.error(methodName, "Client type is disabled");
                throw new KnBulkOpsException(KnBulkOpsErrorCodes.BOEntity.CLIENT_TYPE_DISABLED, "Subscriber Client Type is disabled");
            }
        }

        int pocDonorRadioSupport = genInfoUtil.retrieveXDMSServiceConfig(KnBulkOpsDBUtil.getXdmPttServerId(),
                persisterTxn).getEnablePocDonorRadioSupport();
        if (pocDonorRadioSupport == KnBulkOpsConstants.POC_DONOR_RADIO.DISABLED.value() &&
                subscriberClientType == KnBulkOpsConstants.SUBSCRIBERS_CLIENT_TYPE.POCDONORRADIO.value()) {
            knLogger.error(methodName, "POC_DONOR_RADIO is disabled");
            throw new KnBulkOpsException(KnBulkOpsErrorCodes.BOEntity.POC_DONOR_RADIO_DISABLED, "Poc Donor Radio is disabled");
        }

        int thirdPartyPocClientSupport = genInfoUtil.retrieveXDMSServiceConfig(KnBulkOpsDBUtil.getXdmPttServerId(),
                persisterTxn).getEnable3rdPartyPocClientSupport();
        if (thirdPartyPocClientSupport == KnBulkOpsConstants.THIRD_PARTY_POC_CLIENT.DISABLED.value() &&
                subscriberClientType == KnBulkOpsConstants.SUBSCRIBERS_CLIENT_TYPE.THIRDPARTYPOCCLIENT.value()) {
            knLogger.error(methodName, "3rd Party Poc Client is disabled");
            throw new KnBulkOpsException(KnBulkOpsErrorCodes.BOEntity.THIRD_PARTY_POC_CLIENT_DISABLED, "3rd Party Poc Client is disabled");
        }

        // Removed IdenInterop validation as UFMI is always null for createBulkSubscriber
    }

    public void validateSubscriptionType(KnXDMBulkSubsProvInfoDTO bulkSubsProvInfoDTO) throws KnBulkOpsException {
        String methodName = "validateSubscriptionType(KnXDMBulkSubsProvInfoDTO)";
        knLogger.debug(methodName, "Validating subscription types in request");

        int publicSubscriptionType = bulkSubsProvInfoDTO.getPublicSubscriptionType();
        int corporateSubscriptionType = bulkSubsProvInfoDTO.getCorporateSubscriptionType();

        if(publicSubscriptionType == KnBulkOpsConstants.PUBLIC_SUBSCRIPTION_TYPE.NONE.value()
            && corporateSubscriptionType == KnBulkOpsConstants.CORP_SUBSCRIPTION_TYPE.NONE.value()) {
            knLogger.error(methodName, "Either Public or Corporate Subscription Types are required to be enabled");
            throw new KnBulkOpsException(KnBulkOpsErrorCodes.BOEntity.INVALID_SUBSCRIPTION_TYPE, "Invalid public or Corp Subscription types passed");
        }

        if(publicSubscriptionType == -1 && corporateSubscriptionType == -1) {
            knLogger.error(methodName, "Either Public or Corporate Subscription Types are required to be enabled");
            throw new KnBulkOpsException(KnBulkOpsErrorCodes.BOEntity.INVALID_SUBSCRIPTION_TYPE, "Invalid public and Corp Subscription types passed");
        }

        //validate if dispatch client is set for Public Subscription Type
        if(corporateSubscriptionType == KnBulkOpsConstants.CORP_SUBSCRIPTION_TYPE.NONE.value()){
            if(bulkSubsProvInfoDTO.getSubscriberClientType() != KnBulkOpsConstants.SUBSCRIBERS_CLIENT_TYPE.HANDSET.value()){
                knLogger.error(methodName, "Invalid client type for  public subscriber");
                throw new KnBulkOpsException(KnBulkOpsErrorCodes.BOEntity.INVALID_CLIENT_TYPE_FOR_PUBLIC_SUBS,
                        "Invalid client type set for public subscriber");
            }
        }
    }

    /**
     * Validate corporate and MDN hierarchy for bulk operations
     *
     * @param bulkSubsProvInfoDTO Request DTO containing subscriber list and hierarchy type
     * @param bulkOpsSubsProfileDAO DAO instance for subscriber profile operations
     * @param corpProfileDAO DAO instance for corporate profile operations
     * @param partialRespDTO Response DTO to populate with failures
     * @param persisterTxn Database transaction
     * @throws KnDAOException if database operation fails
     * @throws KnBulkOpsException if hierarchy validation fails
     */
    public void validateHierarchy(
            KnXDMBulkSubsProvInfoDTO bulkSubsProvInfoDTO,
            KnBulkOpsSubsProfileDAO bulkOpsSubsProfileDAO,
            KnBulkOpsCorpProfileDAO corpProfileDAO,
            KnXDMBulkOpsRespDTO partialRespDTO,
            KnPersisterTxn persisterTxn) throws KnDAOException, KnBulkOpsException {

        String methodName = "validateHierarchy(KnXDMBulkSubsProvInfoDTO, KnBulkOpsXDMServerDAO, KnXDMBulkOpsRespDTO, KnPersisterTxn)";
        knLogger.debug(methodName, "Performing hierarchy validations");

        // Validating Corporate Hierarchy
        if(bulkSubsProvInfoDTO.getExtCorpId() != null
                && !corpProfileDAO.validateExtCorpCCAndHierarchy(
                    bulkSubsProvInfoDTO.getExtCorpId(),
                    bulkSubsProvInfoDTO.getHierarchyType(),
                    persisterTxn
                )
        ) {
            knLogger.error(methodName, "Hierarchy flag not valid for the corp : ", bulkSubsProvInfoDTO.getHierarchyType());
            throw new KnBulkOpsException(KnBulkOpsErrorCodes.BOEntity.INVALID_HIERARCHY_REQUEST,
                    "Invalid Corp hierarchy passed");
        }

        // Validating MDN Hierarchy with detailed results
        KnMDNValidationResult mdnValidationResult =
            bulkOpsSubsProfileDAO.validateMDNCCAndHierarchy(
                fetchMDNFromSubscriberList(bulkSubsProvInfoDTO.getSubscriberList()),
                bulkSubsProvInfoDTO.getHierarchyType(),
                persisterTxn
            );
        knLogger.debug(methodName, "MDN Hierarchy Validation Results - Valid: ", mdnValidationResult.getValidMdns().size(),
                      ", Invalid: ", mdnValidationResult.getInvalidMdns().size());
        // Add invalid MDNs to failure list
        addInvalidMdnsToResponse(mdnValidationResult, partialRespDTO);
        // Filter out invalid MDNs from the subscriber list came in request
//        int validMdnCount = filterSubscriberListByValidMdns(bulkSubsProvInfoDTO, mdnValidationResult.getValidMdns());
//        knLogger.info(methodName, "Processing ", validMdnCount, " valid MDNs after filtering");
    }

    /**
     * Validate subscriber service authorization status for bulk operations
     *
     * @param bulkSubsProvInfoDTO Request DTO containing subscriber list
     * @param bulkOpsSubsProfileDAO DAO instance for subscriber profile operations
     * @param partialRespDTO Response DTO to populate with failures
     * @param persisterTxn Database transaction
     * @throws KnDAOException if database operation fails
     */
    public void validateSubAuthStatus(KnXDMBulkSubsProvInfoDTO bulkSubsProvInfoDTO,
                                      KnBulkOpsSubsProfileDAO bulkOpsSubsProfileDAO,
                                      KnXDMBulkOpsRespDTO partialRespDTO,
                                      KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "validateSubAuthStatus(KnXDMBulkSubsProvInfoDTO, KnBulkOpsXDMServerDAO, KnXDMBulkOpsRespDTO, KnPersisterTxn)";

        // Check if operationType is null or empty
        if (bulkSubsProvInfoDTO.getOperationType() == null || bulkSubsProvInfoDTO.getOperationType().isEmpty()) {
            knLogger.debug(methodName, "Operation type is null or empty, skipping subscriber auth status validation");
            return;
        }

        List<String> mdnList = fetchMDNFromSubscriberList(bulkSubsProvInfoDTO.getSubscriberList());
        // Check if MDN list is valid
        if (mdnList == null || mdnList.isEmpty()) {
            knLogger.debug(methodName, "MDN list is null or empty, skipping subscriber auth status validation");
            return;
        }

        knLogger.debug(methodName, "Validating subscriber service auth status for ", mdnList.size(), " MDNs");

        // Fetch the map containing MDN and service auth status
        Map<String, Integer> subscriberServiceAuthStatusMap =
                bulkOpsSubsProfileDAO.getSubscriberServiceAuthStatus(mdnList, persisterTxn);

        if (subscriberServiceAuthStatusMap == null || subscriberServiceAuthStatusMap.isEmpty()) {
            knLogger.debug(methodName, "No service auth status found for MDNs, skipping validation");
            return;
        }

        KnMDNValidationResult authStatusValidationResult = new KnMDNValidationResult();

        // Validate the service auth status for each MDN in the request
        for (String mdn : mdnList) {
            // Check if MDN exists in the database
            if (!subscriberServiceAuthStatusMap.containsKey(mdn)) {
                // MDN not present in database - considered valid (no validation required)
                knLogger.debug(methodName, "MDN not present in database, considered valid: ", mdn);
                authStatusValidationResult.addValidMdn(mdn);
                continue;
            }

            Integer subscriberServiceAuthStatus = subscriberServiceAuthStatusMap.get(mdn);

            // Check if subscriber is marked for async deletion
            if (subscriberServiceAuthStatus != null
                    && KnBulkOpsConstants.SERVICE_AUTH_STATUS.MARKED_FOR_ASYNC_DELETION.value() == subscriberServiceAuthStatus) {

                knLogger.error(methodName, "Inactive subscriber deletion in-progress for MDN=", mdn,
                              " ServiceAuthStatus: ", subscriberServiceAuthStatus);

                // Add to invalid MDN list with error details
                authStatusValidationResult.addInvalidMdn(
                    mdn,
                    INACTIVE_SUBSCRIBER_DELETE_IN_PROGRESS,
                    "Inactive subscriber deletion in-progress for MDN: " + mdn
                );
            } else {
                authStatusValidationResult.addValidMdn(mdn);
            }
        }

        knLogger.debug(methodName, "Auth Status Validation Results - Valid: ", authStatusValidationResult.getValidMdns().size(),
                      ", Invalid: ", authStatusValidationResult.getInvalidMdns().size());
        // Reuse existing utility methods (same pattern as validateHierarchy)
        // Add invalid MDNs to failure list
        addInvalidMdnsToResponse(authStatusValidationResult, partialRespDTO);
        // Filter out invalid MDNs from the subscriber list
        int validMdnCount = filterSubscriberListByValidMdns(bulkSubsProvInfoDTO, authStatusValidationResult.getValidMdns());
        knLogger.info(methodName, "Processing ", validMdnCount, " valid MDNs after auth status filtering");
    }

    /**
     * Validate subscriber list for bulk operations
     * Checks for existing MDNs in PAM_ACCINFO, Alias, and External Subscribers
     *
     * @param requestType Bulk operation type (CREATE, UPDATE, DELETE)
     * @param bulkSubsProvInfoDTO Request DTO containing subscriber list
     * @param bulkOpsXDMServerDAO DAO instance for database operations
     * @param bulkOpsSubsProfileDAO DAO instance for subscriber profile operations
     * @param partialRespDTO Response DTO to populate with failures
     * @param persisterTxn Database transaction
     * @throws KnDAOException if database operation fails
     */
    public void validateSubscriberList(KnBulkOpsConstants.BulkOperations requestType,
                                        KnXDMBulkSubsProvInfoDTO bulkSubsProvInfoDTO,
                                        KnBulkOpsXDMServerDAO bulkOpsXDMServerDAO,
                                        KnBulkOpsSubsProfileDAO bulkOpsSubsProfileDAO,
                                        KnXDMBulkOpsRespDTO partialRespDTO,
                                        Map<String, KnBulkSubsProfileDTO> existingProfilesMap,
                                        KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "validateSubscriberList(KnBulkOpsConstants.BulkOperations, KnXDMBulkSubsProvInfoDTO, KnBulkOpsXDMServerDAO, KnXDMBulkOpsRespDTO, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY: Validating Bulk Subscriber List for ", requestType, " Operation");

        List<String> mdnList = fetchMDNFromSubscriberList(bulkSubsProvInfoDTO.getSubscriberList());
        List<String> mcsIds = mdnWithTelPrefix(bulkSubsProvInfoDTO.getSubscriberList());
        if (mdnList == null || mdnList.isEmpty()) {
            knLogger.debug(methodName, "MDN list is empty, skipping validation");
            return;
        }

        KnMDNValidationResult subscriberListValidationResult = new KnMDNValidationResult();


        if(requestType.equals(KnBulkOpsConstants.BulkOperations.CREATEBULKSUBSCRIBER)) {
            List<String> mdnInPamAccInfoList = bulkOpsXDMServerDAO.mdnPresentInPamAccInfo(mdnList, persisterTxn);
            List<String> mdnPresentAsAlias = bulkOpsSubsProfileDAO.mdnPresentAsAlias(mdnList, persisterTxn);
            List<String> mdnPresentAsExtSubs = bulkOpsXDMServerDAO.mdnPresentAsExtSubs(mdnList, persisterTxn);
            List<KnXDMBulkSubsProvInfoDTO> subsProfileResult = bulkOpsSubsProfileDAO.selectSubscriberProfileByMCSIds(mcsIds, persisterTxn);

            // Convert Lists to Sets for O(1) lookup instead of O(n)
            final Set<String> pamAccInfoSet = new HashSet<>(mdnInPamAccInfoList);
            final Set<String> aliasSet = new HashSet<>(mdnPresentAsAlias);
            final Set<String> extSubsSet = new HashSet<>(mdnPresentAsExtSubs);
            final String kuidPrefix = generalCacheUtil.retrieveKUIDPrefix();

            // Build MCS ID validation map inline (thread-safe using ConcurrentHashMap)
            // Key: MDN, Value: Error details (errorCode, errorMessage)
            final Map<String, String[]> mcsIdErrorMap = new ConcurrentHashMap<>();

            if (subsProfileResult != null && !subsProfileResult.isEmpty()) {
                final int expectedPublicSubsType = bulkSubsProvInfoDTO.getPublicSubscriptionType();
                final int expectedCorpSubsType = bulkSubsProvInfoDTO.getCorporateSubscriptionType();
                final String expectedAccountId = bulkSubsProvInfoDTO.getAccountId();
                final Integer expectedClientType = bulkSubsProvInfoDTO.getSubscriberClientType();

                for (KnXDMBulkSubsProvInfoDTO existingProfile : subsProfileResult) {
                    if (existingProfile.getSubscriberList() == null || existingProfile.getSubscriberList().isEmpty()) {
                        continue;
                    }
                    String mdn = existingProfile.getSubscriberList().get(0).getMdn();
                    if (mdn == null) continue;

                    // Check subscription type mismatch (public or corp - single error)
                    if (existingProfile.getPublicSubscriptionType() != expectedPublicSubsType ||
                        existingProfile.getCorporateSubscriptionType() != expectedCorpSubsType) {
                        mcsIdErrorMap.put(mdn, new String[]{
                            KnBulkOpsErrorCodes.BOEntity.MCSID_SUBSCRIPTION_TYPE_MISMATCH,
                            "Subscription type mismatch for the subscriber"
                        });
                        continue;
                    }

                    // Check account ID mismatch
                    String existingAccountId = existingProfile.getAccountId();
                    if ((expectedAccountId == null && existingAccountId != null) ||
                        (expectedAccountId != null && !expectedAccountId.trim().equals(
                            existingAccountId != null ? existingAccountId.trim() : ""))) {
                        mcsIdErrorMap.put(mdn, new String[]{
                            KnBulkOpsErrorCodes.BOEntity.MCSID_ACCOUNT_ID_MISMATCH,
                            "Account ID mismatch for the subscriber"
                        });
                        continue;
                    }

                    // Check client type mismatch
                    Integer existingClientType = existingProfile.getSubscriberClientType();
                    if (expectedClientType != null && existingClientType != null &&
                        !expectedClientType.equals(existingClientType)) {
                        mcsIdErrorMap.put(mdn, new String[]{
                            KnBulkOpsErrorCodes.BOEntity.MCSID_CLIENT_TYPE_MISMATCH,
                            "Client type mismatch for the subscriber"
                        });
                        continue;
                    }

                    // No mismatch found - subscriber data matches, allow the upsert operation
                    // Do not add to error map, MDN will be processed for upsert
                }
            }

            knLogger.debug(methodName, "Existing MDNs - PAM: ", pamAccInfoSet.size(),
                          ", Alias: ", aliasSet.size(), ", ExtSubs: ", extSubsSet.size(),
                          ", MCS ID conflicts: ", mcsIdErrorMap.size());

            mdnList.parallelStream().forEach(mdn -> {
                if (pamAccInfoSet.contains(mdn)) {
                    subscriberListValidationResult.addInvalidMdn(mdn,
                            KnBulkOpsErrorCodes.BOEntity.MDN_ALREADY_EXISTS_IN_PAM_ACCINFO,
                            "Subscriber already exists in PAM account");
                } else if (aliasSet.contains(mdn)) {
                    subscriberListValidationResult.addInvalidMdn(mdn,
                            KnBulkOpsErrorCodes.BOEntity.MDN_ALREADY_EXISTS_AS_ALIAS,
                            "Subscriber already exists as an alias");
                } else if (extSubsSet.contains(mdn)) {
                    subscriberListValidationResult.addInvalidMdn(mdn,
                            KnBulkOpsErrorCodes.BOEntity.MDN_ALREADY_EXISTS_AS_EXT_SUBS,
                            "Subscriber already exists as an external subscriber");
                } else if (kuidPrefix != null && mdn.startsWith(kuidPrefix)) {
                    subscriberListValidationResult.addInvalidMdn(mdn,
                            KnBulkOpsErrorCodes.BOEntity.MDN_PRESENT_IN_KUIDPPOOL,
                            "Subscriber identifier conflicts with reserved prefix");
                } else if (mcsIdErrorMap.containsKey(mdn)) {
                    String[] errorDetails = mcsIdErrorMap.get(mdn);
                    subscriberListValidationResult.addInvalidMdn(mdn, errorDetails[0], errorDetails[1]);
                } else {
                    subscriberListValidationResult.addValidMdn(mdn);
                }
            });

        }
        else if(requestType.equals(KnBulkOpsConstants.BulkOperations.DELETEBULKSUBSCRIBER)) {
            mdnList.parallelStream().forEach(mdn -> {
                KnBulkSubsProfileDTO profile = existingProfilesMap.get(mdn);

                if (profile == null) {
                    subscriberListValidationResult.addInvalidMdn(mdn,
                            KnBulkOpsErrorCodes.BOEntity.BULK_SUBSCRIBER_NOT_FOUND,
                            "Subscriber not found");
                } else if (bulkSubsProvInfoDTO.getAccountId() != null && !Objects.equals(profile.getAccountId(), bulkSubsProvInfoDTO.getAccountId())) {
                    subscriberListValidationResult.addInvalidMdn(mdn,
                            KnBulkOpsErrorCodes.BOEntity.BULK_SUBSCRIBER_VALIDATION_FAILED,
                            "Account ID mismatch");
                } else if (bulkSubsProvInfoDTO.getExtCorpId() != null && !Objects.equals(profile.getExtCorpId(), bulkSubsProvInfoDTO.getExtCorpId())) {
                    subscriberListValidationResult.addInvalidMdn(mdn,
                            KnBulkOpsErrorCodes.BOEntity.BULK_SUBSCRIBER_VALIDATION_FAILED,
                            "Corporate ID mismatch");
                } else if (profile.getPamAccId() != null && profile.getPamAccId() != 0) {
                    subscriberListValidationResult.addInvalidMdn(mdn,
                            KnBulkOpsErrorCodes.BOEntity.BULK_SUBSCRIBER_VALIDATION_FAILED,
                            "Mdn is present as a pseudo mdn, cannot delete");
                } else {
                    subscriberListValidationResult.addValidMdn(mdn);
                }
            });
        }

        knLogger.debug(methodName, "Subscriber List Validation Results - Valid: ",
                subscriberListValidationResult.getValidMdns().size(),
                ", Invalid: ", subscriberListValidationResult.getInvalidMdns().size());

        // Add invalid MDNs to failure list
        addInvalidMdnsToResponse(subscriberListValidationResult, partialRespDTO);

        // Filter out invalid MDNs from the subscriber list
        int validMdnCount = filterSubscriberListByValidMdns(bulkSubsProvInfoDTO, subscriberListValidationResult.getValidMdns());
        knLogger.info(methodName, "Processing ", validMdnCount, " valid MDNs after subscriber list filtering");


        knLogger.info(methodName, "EXIT: Completed Validation of Bulk Subscriber List for ", requestType, " Operation");
    }

    /**
     * Validate and get corporate anchor POC home for bulk subscribers
     * Uses SINGLE database query to fetch partition config + POC capacity together
     * No shared mutable state, uses immutable DTOs
     *
     * ALL USE CASES COVERED:
     * 1. Existing Corp + LOAD_BASED + Anchoring ON → Validate/fallback to load factor
     * 2. Existing Corp + MDN_BASED + Anchoring ON → Strict validation
     * 3. Existing Corp + Anchoring OFF → Partition logic
     * 4. New Corp + Pre-assigned Home → Validate capacity
     * 5. New Corp + No Pre-assigned → Partition logic
     *
     * ERROR HANDLING:
     * - Global/system errors (config not found, no servers) → throw exception
     * - MDN-level errors (individual prefix not found) → add to partial response
     *
     * @param mdnList List of MDNs to process
     * @param corpProfileDAO DAO containing corporate profile info
     * @param bulkOpsXDMServerDAO DAO for database operations
     * @param bulkSubsProvInfoDTO Request DTO containing subscriber list to filter
     * @param partialRespDTO Partial response DTO for MDN-level failures
     * @param persisterTxn Database transaction
     * @return Map with MDN to POC home mapping and updateCorpHome flag
     * @throws KnBulkOpsException if global/system-level validation fails
     */
    public Map<String, Object> validateAndGetCorpAnchorPocHome(
            List<String> mdnList,
            KnBulkOpsCorpProfileDAO.KnCorpProfileInfo corpProfileDAO,
            KnBulkOpsXDMServerDAO bulkOpsXDMServerDAO,
            KnXDMBulkSubsProvInfoDTO bulkSubsProvInfoDTO,
            KnXDMBulkOpsRespDTO partialRespDTO,
            KnPersisterTxn persisterTxn) throws KnBulkOpsException {

        String methodName = "validateAndGetCorpAnchorPocHome(...)";
        knLogger.debug(methodName, "Processing ", mdnList.size(), " MDNs");

        // Use LinkedHashMap to maintain sorted order of MDNs (request is pre-sorted)
        Map<String, String> mdnToPocMap;
        boolean updateCorpHome = false;
        final int subsCount = mdnList.size();
        final String corpPoCHome = corpProfileDAO.getPocHome();
        final String extCorpId = corpProfileDAO.getExtCorpId();
        final long corpId = corpProfileDAO.getCorpId();

        try {
            final KnBulkOpsPocConfigDTO config = bulkOpsXDMServerDAO.getPartitionAndCapacityConfig(
                corpId > 0 ? corpPoCHome : null, persisterTxn);

            // ========== EXISTING CORPORATION ==========
            if (corpId > 0) {
                final int enableCorpAnch = config.getEnableCorpAccAnch();
                final int partitionType = config.getMdnPartitionTypePOC();

                // Check if corp anchoring is enabled
                if (enableCorpAnch == KnBulkOpsConstants.CORP_ACC_ANCHORING.ENABLED.value()) {

                    // Validate corp home capacity (data already fetched in single query above)
                    if (!config.hasCapacityConfig()) {
                        throw new KnBulkOpsException(KnBulkOpsErrorCodes.BOEntity.MAX_SUBS_LIMIT_REACHED_FOR_POC,
                            "POC capacity config not found for: " + corpPoCHome);
                    }

                    final int currentCount = config.getSubscriberCount();
                    final boolean capacityAvailable = (currentCount + subsCount) < config.getMaxSubsLimit() &&
                                                      config.getAllowProv() == KnBulkOpsConstants.ALLOW_POC_PROV.ALLOW_PROV.value();

                    // LOAD_BASED: Can fallback to new POC(s) if limit reached
                    if (partitionType == KnBulkOpsConstants.PARTITION_TYPE.LOAD_BASED.value()) {
                        if (capacityAvailable) {
                            // Corp home has capacity - use it for all MDNs (maintain order)
                            mdnToPocMap = new LinkedHashMap<>(mdnList.size());
                            for (String mdn : mdnList) {
                                mdnToPocMap.put(mdn, corpPoCHome);
                            }
                        } else {
                            // Corp home at capacity - use bulk allocation to distribute across available POCs
                            knLogger.debug(methodName, "Corp home at capacity, using bulk allocation for ",
                                          mdnList.size(), " MDNs");
                            mdnToPocMap = allocatePocHomesForBulkMdns(mdnList, bulkOpsXDMServerDAO, persisterTxn);
                            updateCorpHome = true;
                        }
                    }
                    // MDN_BASED: Strict - throw error if limit reached
                    else {
                        if (!capacityAvailable) {
                            throw new KnBulkOpsException(KnBulkOpsErrorCodes.BOEntity.MAX_SUBS_LIMIT_REACHED_FOR_POC,
                                "MAX limit reached for MDN_BASED POC home");
                        }
                        mdnToPocMap = new LinkedHashMap<>(mdnList.size());
                        for (String mdn : mdnList) {
                            mdnToPocMap.put(mdn, corpPoCHome);
                        }
                    }
                } else {
                    // Corp anchoring disabled - use partitioning (can have multiple POC homes)
                    mdnToPocMap = findPocHomeByPartitionType(mdnList, config, bulkOpsXDMServerDAO, bulkSubsProvInfoDTO, partialRespDTO, persisterTxn);
                }

            // ========== NEW CORPORATION ==========
            } else {
                // Check for pre-assigned home
                final String preAssignHome = bulkOpsXDMServerDAO.getPreAssignCorpHome(extCorpId, persisterTxn);

                if (preAssignHome != null) {
                    // Validate pre-assigned home capacity
                    String singlePocHome = validateAndUsePocHome(preAssignHome, subsCount, bulkOpsXDMServerDAO, persisterTxn);

                    // All MDNs go to pre-assigned POC home (maintain order)
                    mdnToPocMap = new LinkedHashMap<>(mdnList.size());
                    for (String mdn : mdnList) {
                        mdnToPocMap.put(mdn, singlePocHome);
                    }
                } else {
                    // No pre-assigned home - use partitioning (can have multiple POC homes)
                    mdnToPocMap = findPocHomeByPartitionType(mdnList, config, bulkOpsXDMServerDAO, bulkSubsProvInfoDTO, partialRespDTO, persisterTxn);
                }
            }

            // Return result map with MDN->POC mapping and updateCorpHome flag (maintain order)
            final Map<String, Object> result = new LinkedHashMap<>(2);
            result.put(KnBulkOpsConstants.MDN_POC_MAP_KEY, mdnToPocMap);
            result.put(KnBulkOpsConstants.UPDATE_CORP_HOME, updateCorpHome);

            knLogger.info(methodName, "Mapped ", mdnToPocMap.size(), " MDNs to POC homes, updateCorpHome: ", updateCorpHome);
            return result;

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO error: ", e.getMessage());
            throw new KnBulkOpsException(e.getErrorCode(), "Failed to get POC config", e);
        } catch (KnBulkOpsException e) {
            knLogger.error(methodName, "Validation error: ", e.getMessage());
            throw e;
        }
    }

    /**
     * Validate and use specific POC home
     */
    private String validateAndUsePocHome(String pocHome, int subsCount,
                                        KnBulkOpsXDMServerDAO bulkOpsXDMServerDAO, KnPersisterTxn persisterTxn)
                                        throws KnBulkOpsException, KnDAOException {
        List<String> pocList = new ArrayList<>(1);
        pocList.add(pocHome);

        Map<String, KnBulkOpsPocConfigDTO> capacityMap = bulkOpsXDMServerDAO.getPocCapacityInfo(pocList, persisterTxn);
        KnBulkOpsPocConfigDTO capacity = capacityMap.get(pocHome);

        if (capacity == null || !capacity.hasCapacityConfig()) {
            throw new KnBulkOpsException(KnBulkOpsErrorCodes.BOEntity.MAX_SUBS_LIMIT_REACHED_FOR_POC,
                "POC capacity config not found");
        }

        if (capacity.getSubscriberCount() + subsCount >= capacity.getMaxSubsLimit() ||
            capacity.getAllowProv() != KnBulkOpsConstants.ALLOW_POC_PROV.ALLOW_PROV.value()) {
            throw new KnBulkOpsException(KnBulkOpsErrorCodes.BOEntity.MAX_SUBS_LIMIT_REACHED_FOR_POC,
                "POC home at max capacity");
        }

        return pocHome;
    }

    /**
     * Find POC home by partition type
     * For MDN_BASED: Returns Map of MDN -> POC home (supports different POC homes per MDN)
     * For LOAD_BASED: Uses greedy capacity-filling algorithm to optimally distribute MDNs across servers
     *
     * @param mdnList List of MDNs to validate
     * @param config Partition configuration
     * @param bulkOpsXDMServerDAO DAO instance
     * @param bulkSubsProvInfoDTO Request DTO containing subscriber list to filter
     * @param partialRespDTO Partial response DTO for MDN-level failures
     * @param persisterTxn Database transaction
     * @return Map of MDN to POC home (only valid mappings)
     * @throws KnBulkOpsException if global validation fails
     * @throws KnDAOException if database operation fails
     */
    public Map<String, String> findPocHomeByPartitionType(List<String> mdnList, KnBulkOpsPocConfigDTO config,
                                                           KnBulkOpsXDMServerDAO bulkOpsXDMServerDAO,
                                                           KnXDMBulkSubsProvInfoDTO bulkSubsProvInfoDTO,
                                                           KnXDMBulkOpsRespDTO partialRespDTO,
                                                           KnPersisterTxn persisterTxn)
                                                           throws KnBulkOpsException, KnDAOException {
        if (config.getMdnPartitionTypePOC() == KnBulkOpsConstants.PARTITION_TYPE.LOAD_BASED.value()) {
            // LOAD_BASED: Use greedy capacity-filling to optimally distribute MDNs
            return allocatePocHomesForBulkMdns(mdnList, bulkOpsXDMServerDAO, persisterTxn);
        } else {
            // MDN_BASED: Each MDN can have different POC home based on prefix
            return getPocHomeForAllMdns(mdnList, bulkOpsXDMServerDAO, bulkSubsProvInfoDTO, partialRespDTO, persisterTxn);
        }
    }

    /**
     * OPTIMIZED + THREAD-SAFE: Get POC home for each MDN (supports multiple POC homes)
     * For MDN_BASED partitioning, each MDN can map to different POC homes
     * Returns mapping of MDN -> POC home for all MDNs
     *
     * ERROR HANDLING:
     * - If MDN prefix not found: Add to partial response, continue with other MDNs
     * - Returns only successfully mapped MDNs
     *
     * @param mdnList List of MDNs to validate
     * @param bulkOpsXDMServerDAO DAO instance for database operations
     * @param bulkSubsProvInfoDTO Request DTO containing subscriber list to filter
     * @param partialRespDTO Partial response DTO for MDN-level failures
     * @param persisterTxn Database transaction
     * @return Map of MDN to its POC home (only valid mappings)
     * @throws KnBulkOpsException if global validation fails (capacity config issues)
     * @throws KnDAOException if database operation fails
     */
    private Map<String, String> getPocHomeForAllMdns(List<String> mdnList,
                                                     KnBulkOpsXDMServerDAO bulkOpsXDMServerDAO,
                                                     KnXDMBulkSubsProvInfoDTO bulkSubsProvInfoDTO,
                                                     KnXDMBulkOpsRespDTO partialRespDTO,
                                                     KnPersisterTxn persisterTxn)
                                                     throws KnBulkOpsException, KnDAOException {
        String methodName = "getPocHomeForAllMdns(...)";
        knLogger.debug(methodName, "Getting POC home for ", mdnList.size(), " MDNs");

        if (mdnList == null || mdnList.isEmpty()) {
            return new LinkedHashMap<>();
        }

        // Get POC home for ALL MDNs in a single batch call (maintains order)
        final Map<String, String> mdnToPocHomeMap = bulkOpsXDMServerDAO.getSubscriberPoCHomeForMdns(mdnList, persisterTxn);

        // Find MDNs that didn't get mapped (prefix not found) - MDN-level failures
        KnMDNValidationResult pocMappingResult = new KnMDNValidationResult();
        for (String mdn : mdnList) {
            if (mdnToPocHomeMap.containsKey(mdn)) {
                pocMappingResult.addValidMdn(mdn);
            } else {
                // MDN-level failure - prefix not found for this specific MDN
                pocMappingResult.addInvalidMdn(mdn,
                    KnBulkOpsErrorCodes.BOEntity.POC_SERVER_MAP_NOT_FOUND,
                    "POC server map not found for MDN: " + mdn);
            }
        }

        // Add failed MDNs to partial response instead of throwing exception
        if (!pocMappingResult.getInvalidMdns().isEmpty()) {
            knLogger.warn(methodName, "POC server map not found for ",
                         pocMappingResult.getInvalidMdns().size(), " MDN(s)");

            // CRUCIAL FUNCTION 1: Add invalid MDNs to failure list in partial response
            addInvalidMdnsToResponse(pocMappingResult, partialRespDTO);

            // CRUCIAL FUNCTION 2: Filter out invalid MDNs from the subscriber list
            int validMdnCount = filterSubscriberListByValidMdns(bulkSubsProvInfoDTO, pocMappingResult.getValidMdns());
            knLogger.info(methodName, "Filtered to ", validMdnCount, " valid MDNs in subscriber list");
        }

        // Get unique POC homes for capacity validation (only for successfully mapped MDNs)
        final Set<String> uniquePocHomes = new HashSet<>(mdnToPocHomeMap.values());

        knLogger.info(methodName, "Mapped ", mdnToPocHomeMap.size(), " MDNs to ", uniquePocHomes.size(),
                     " unique POC home(s)");

        // Validate capacity for each unique POC home (global validation - throws if fails)
        if (!mdnToPocHomeMap.isEmpty()) {
            validatePocCapacityForMdnGroups(mdnToPocHomeMap, uniquePocHomes, bulkOpsXDMServerDAO, persisterTxn);
        }

        return mdnToPocHomeMap;  // Returns only successfully mapped MDNs
    }

    /**
     * Validate POC capacity for all unique POC homes
     * Groups MDNs by POC home and validates each POC home has capacity for its MDNs
     *
     * @param mdnToPocHomeMap Map of MDN to POC home
     * @param uniquePocHomes Set of unique POC homes to validate
     * @param bulkOpsXDMServerDAO DAO instance
     * @param persisterTxn Database transaction
     * @throws KnBulkOpsException if capacity validation fails
     * @throws KnDAOException if database operation fails
     */
    private void validatePocCapacityForMdnGroups(Map<String, String> mdnToPocHomeMap,
                                                 Set<String> uniquePocHomes,
                                                 KnBulkOpsXDMServerDAO bulkOpsXDMServerDAO,
                                                 KnPersisterTxn persisterTxn)
                                                 throws KnBulkOpsException, KnDAOException {
        String methodName = "validatePocCapacityForMdnGroups(...)";

        // Fetch capacity info for all unique POC homes in single query
        final List<String> pocList = new ArrayList<>(uniquePocHomes);
        final Map<String, KnBulkOpsPocConfigDTO> capacityMap = bulkOpsXDMServerDAO.getPocCapacityInfo(pocList, persisterTxn);

        // Group MDNs by POC home and count
        final Map<String, Integer> pocHomeMdnCount = new HashMap<>();
        for (Map.Entry<String, String> entry : mdnToPocHomeMap.entrySet()) {
            String pocHome = entry.getValue();
            pocHomeMdnCount.put(pocHome, pocHomeMdnCount.getOrDefault(pocHome, 0) + 1);
        }

        // Validate capacity for each POC home
        for (Map.Entry<String, Integer> entry : pocHomeMdnCount.entrySet()) {
            String pocHome = entry.getKey();
            int mdnCount = entry.getValue();

            KnBulkOpsPocConfigDTO capacity = capacityMap.get(pocHome);

            if (capacity == null || !capacity.hasCapacityConfig()) {
                throw new KnBulkOpsException(KnBulkOpsErrorCodes.BOEntity.MAX_SUBS_LIMIT_REACHED_FOR_POC,
                    "POC capacity config not found for: " + pocHome);
            }

            int currentCount = capacity.getSubscriberCount();
            int maxLimit = capacity.getMaxSubsLimit();
            int allowProv = capacity.getAllowProv();

            if ((currentCount + mdnCount) >= maxLimit) {
                knLogger.error(methodName, "POC home capacity exceeded - POC: ", pocHome,
                              ", Current: ", currentCount, ", Requested: ", mdnCount, ", Max: ", maxLimit);
                throw new KnBulkOpsException(KnBulkOpsErrorCodes.BOEntity.MAX_SUBS_LIMIT_REACHED_FOR_POC,
                    String.format("POC home %s capacity exceeded. Current: %d, Requested: %d, Max: %d",
                                 pocHome, currentCount, mdnCount, maxLimit));
            }

            if (allowProv != KnBulkOpsConstants.ALLOW_POC_PROV.ALLOW_PROV.value()) {
                throw new KnBulkOpsException(KnBulkOpsErrorCodes.BOEntity.MAX_SUBS_LIMIT_REACHED_FOR_POC,
                    "Provisioning is disabled for POC home: " + pocHome);
            }

            knLogger.debug(methodName, "POC home ", pocHome, " validated - ", mdnCount,
                          " MDNs, Capacity: ", currentCount, "/", maxLimit);
        }

        knLogger.info(methodName, "All POC homes validated successfully");
    }

    /**
     * Allocate POC homes for bulk MDNs using greedy capacity-filling algorithm
     *
     * Algorithm:
     * 1. Fetch all POC servers with available capacity
     * 2. Sort servers by available capacity (highest first)
     * 3. Fill servers sequentially until capacity is reached
     * 4. Example: Server A has 50 slots → allocate 50 MDNs, Server B has 60 slots → allocate remaining 50 MDNs
     *
     * @param mdnList List of MDNs to allocate POC homes
     * @param bulkOpsXDMServerDAO DAO for database operations
     * @param persisterTxn Database transaction
     * @return Map of MDN -> POC Home allocation
     * @throws KnBulkOpsException if insufficient capacity across all POC homes
     * @throws KnDAOException if database operation fails
     */
    private Map<String, String> allocatePocHomesForBulkMdns(List<String> mdnList,
                                                            KnBulkOpsXDMServerDAO bulkOpsXDMServerDAO,
                                                            KnPersisterTxn persisterTxn)
                                                            throws KnBulkOpsException, KnDAOException {
        String methodName = "allocatePocHomesForBulkMdns(...)";
        knLogger.debug(methodName, "ENTRY: Allocating POC homes for ", mdnList.size(), " MDNs");

        // Single query gets ALL POC servers with ALLOW_PROV=1
        final Map<String, KnBulkOpsPocConfigDTO> allPocServers =
            bulkOpsXDMServerDAO.getAllPocServersWithCapacity(persisterTxn);

        if (allPocServers.isEmpty()) {
            throw new KnBulkOpsException(KnBulkOpsErrorCodes.BOEntity.MAX_SUBS_LIMIT_REACHED_FOR_POC,
                "No POC servers available for provisioning");
        }

        // Calculate available capacity for each server
        final List<KnBulkOpsPocConfigDTO.ServerCapacityInfo> serverCapacities = new ArrayList<>(allPocServers.size());

        for (Map.Entry<String, KnBulkOpsPocConfigDTO> entry : allPocServers.entrySet()) {
            final String serverId = entry.getKey();
            final KnBulkOpsPocConfigDTO info = entry.getValue();

            if (!info.hasCapacityConfig()) {
                knLogger.warn(methodName, "No capacity config for server: ", serverId);
                continue;
            }

            final int currentCount = info.getSubscriberCount();
            final int maxLimit = info.getMaxSubsLimit();
            final int available = maxLimit - currentCount;

            if (available > 0) {
                serverCapacities.add(new KnBulkOpsPocConfigDTO.ServerCapacityInfo(serverId, available, currentCount, maxLimit));
                knLogger.debug(methodName, "Server: ", serverId, ", Available: ", available,
                              ", Current: ", currentCount, "/", maxLimit);
            } else {
                knLogger.debug(methodName, "Server: ", serverId, " at full capacity");
            }
        }

        if (serverCapacities.isEmpty()) {
            throw new KnBulkOpsException(KnBulkOpsErrorCodes.BOEntity.MAX_SUBS_LIMIT_REACHED_FOR_POC,
                "All POC servers are at maximum capacity");
        }

        // Sort by available capacity (highest first) for optimal allocation
        serverCapacities.sort((a, b) -> Integer.compare(b.availableCapacity, a.availableCapacity));
        knLogger.debug(methodName, "Sorted server capacities by availability (descending)");

        // Check if total capacity is sufficient
        final int totalAvailable = serverCapacities.stream()
                .mapToInt(sc -> sc.availableCapacity)
                .sum();

        if (totalAvailable < mdnList.size()) {
            knLogger.error(methodName, "Insufficient total capacity. Required: ", mdnList.size(),
                          ", Available: ", totalAvailable);
            throw new KnBulkOpsException(
                KnBulkOpsErrorCodes.BOEntity.MAX_SUBS_LIMIT_REACHED_FOR_POC,
                String.format("Insufficient capacity across all POC Homes. Required: %d, Available: %d",
                             mdnList.size(), totalAvailable)
            );
        }

        // Allocate MDNs to servers using greedy fill approach
        final Map<String, String> mdnToPocHomeMap = new LinkedHashMap<>(mdnList.size());
        int mdnIndex = 0;

        for (KnBulkOpsPocConfigDTO.ServerCapacityInfo server : serverCapacities) {
            // Allocate as many MDNs as possible to current server (up to its available capacity)
            final int allocateCount = Math.min(server.availableCapacity, mdnList.size() - mdnIndex);

            for (int i = 0; i < allocateCount; i++) {
                final String mdn = mdnList.get(mdnIndex++);
                mdnToPocHomeMap.put(mdn, server.serverId);
            }

            knLogger.info(methodName, "Allocated ", allocateCount, " MDNs to server: ", server.serverId,
                         " (New load: ", (server.currentCount + allocateCount), "/", server.maxLimit, ")");

            // Exit early if all MDNs allocated
            if (mdnIndex >= mdnList.size()) {
                break;
            }
        }

        knLogger.info(methodName, "EXIT: Successfully allocated ", mdnToPocHomeMap.size(),
                     " MDNs across ", serverCapacities.size(), " POC servers");
        return mdnToPocHomeMap;
    }

    /**
     * OPTIMIZED: Fetch presence homes for bulk MDNs based on their POC homes
     *
     * Algorithm:
     * 1. Check if PR-in-POC (Presence in POC) is enabled globally
     * 2. If enabled, for each POC server check if it has EnablePRInPoC=1
     *    - If yes, use same POC server as Presence server
     *    - If no, lookup presence home by MDN prefix
     * 3. If PR-in-POC disabled globally, lookup all by MDN prefix
     * 4. For MDNs without presence home mapping, add to failure list in partial response
     *
     * Optimizations:
     * - Single query to check global PR-in-POC flag
     * - Batch query to fetch presence config for all unique POC servers
     * - Single query to fetch all presence prefix mappings
     * - In-memory prefix matching for all MDNs
     * - Uses partial response pattern (adds failures, continues with valid MDNs)
     *
     * @param mdnList List of MDNs to fetch presence homes for
     * @param mdnToPocHomeMap Map of MDN to POC home (already allocated)
     * @param bulkOpsXDMServerDAO DAO instance
     * @param bulkSubsProvInfoDTO Request DTO containing subscriber list to filter
     * @param partialRespDTO Partial response DTO to populate with failures
     * @param persisterTxn Database transaction
     * @return Map of MDN to Presence PTT Server ID (only valid mappings)
     * @throws KnDAOException if database operation fails
     */
    public Map<String, String> fetchSubsPresenceHome(List<String> mdnList,
                                                      Map<String, String> mdnToPocHomeMap,
                                                      KnBulkOpsXDMServerDAO bulkOpsXDMServerDAO,
                                                      KnXDMBulkSubsProvInfoDTO bulkSubsProvInfoDTO,
                                                      KnXDMBulkOpsRespDTO partialRespDTO,
                                                      KnPersisterTxn persisterTxn)
                                                      throws KnDAOException {
        String methodName = "fetchSubsPresenceHome(...)";
        knLogger.debug(methodName, "ENTRY: Fetching presence homes for ", mdnList.size(), " MDNs");

        if (mdnList == null || mdnList.isEmpty()) {
            return new LinkedHashMap<>();
        }

        Map<String, String> mdnToPresenceHomeMap = new LinkedHashMap<>(mdnList.size());

        try {
            // Step 1: Check if PR-in-POC is enabled globally
            final boolean isPrInPocEnabled = bulkOpsXDMServerDAO.isPrInPocEnabled(persisterTxn);
            knLogger.debug(methodName, "PR-in-POC enabled globally: ", isPrInPocEnabled);

            if (isPrInPocEnabled) {
                // Step 2: Get unique POC homes from the map
                final Set<String> uniquePocHomes = new HashSet<>(mdnToPocHomeMap.values());
                knLogger.debug(methodName, "Unique POC homes: ", uniquePocHomes.size());

                // Step 3: Batch fetch presence service config for all POC servers
                // OPTIMIZATION: Pass Set directly instead of converting to ArrayList
                final Map<String, Integer> pocToPresenceConfigMap =
                    bulkOpsXDMServerDAO.retrievePresenceServiceConfigBatch(
                        new ArrayList<>(uniquePocHomes), persisterTxn);

                // Step 4: For each MDN, determine presence home based on its POC home config
                // OPTIMIZATION: Use List instead of Map (we only need MDN keys, not values)
                final List<String> mdnsNeedingPrefixLookup = new ArrayList<>();
                int pocHomeMatchCount = 0;

                for (String mdn : mdnList) {
                    final String pocHome = mdnToPocHomeMap.get(mdn);

                    if (pocHome == null) {
                        knLogger.warn(methodName, "No POC home found for MDN: ", mdn);
                        // This MDN needs prefix lookup as fallback
                        mdnsNeedingPrefixLookup.add(mdn);
                        continue;
                    }

                    // Check if this POC server has EnablePRInPoC = 1
                    final Integer enablePrInPoc = pocToPresenceConfigMap.get(pocHome);

                    if (enablePrInPoc != null && enablePrInPoc == 1) {
                        // Use same POC server as Presence server
                        mdnToPresenceHomeMap.put(mdn, pocHome);
                        pocHomeMatchCount++;
                    } else {
                        // Need prefix lookup for this MDN
                        mdnsNeedingPrefixLookup.add(mdn);
                    }
                }

                // OPTIMIZATION: Single debug log instead of per-MDN logging
                knLogger.debug(methodName, "POC home matches: ", pocHomeMatchCount,
                              ", Prefix lookup needed: ", mdnsNeedingPrefixLookup.size());

                // Step 5: For MDNs needing prefix lookup, do batch lookup
                if (!mdnsNeedingPrefixLookup.isEmpty()) {
                    // OPTIMIZATION: Pass List directly without keySet() conversion
                    final Map<String, String> prefixLookupResults =
                        bulkOpsXDMServerDAO.getSubscriberPresenceHomeForMdns(
                            mdnsNeedingPrefixLookup, persisterTxn);

                    mdnToPresenceHomeMap.putAll(prefixLookupResults);
                }

//                knLogger.debug(methodName, "Setting Presence home to 0");
//                mdnToPresenceHomeMap = mdnList.parallelStream()
//                        .collect(Collectors.toMap(
//                                Function.identity(),
//                                mdn -> "0",
//                                (existing, replacement) -> existing,
//                                LinkedHashMap::new
//                        ));
            } else {
                // PR-in-POC disabled globally - all MDNs need prefix lookup
                knLogger.debug(methodName, "PR-in-POC disabled, using prefix lookup for all MDNs");

                mdnToPresenceHomeMap = bulkOpsXDMServerDAO.getSubscriberPresenceHomeForMdns(mdnList, persisterTxn);
            }

            // Step 6: Validate all MDNs got mapped and add failures to response
            // Find unmapped MDNs and add to failure list (partial response pattern)
            final int mappedCount = mdnToPresenceHomeMap.size();
            if (mappedCount != mdnList.size()) {
                // Build validation result for unmapped MDNs
                KnMDNValidationResult presenceValidationResult = new KnMDNValidationResult();

                for (String mdn : mdnList) {
                    if (mdnToPresenceHomeMap.containsKey(mdn)) {
                        presenceValidationResult.addValidMdn(mdn);
                    } else {
                        // Add to failure list with appropriate error
                        presenceValidationResult.addInvalidMdn(mdn,
                            KnBulkOpsErrorCodes.BOEntity.POC_SERVER_MAP_NOT_FOUND,
                            "Presence server map not found for MDN: " + mdn);
                    }
                }

                knLogger.warn(methodName, "Presence server map not found for ",
                             presenceValidationResult.getInvalidMdns().size(), " MDN(s)");

                // CRUCIAL FUNCTION 1: Add invalid MDNs to failure list in partial response
                addInvalidMdnsToResponse(presenceValidationResult, partialRespDTO);

                // CRUCIAL FUNCTION 2: Filter out invalid MDNs from the subscriber list
                int validMdnCount = filterSubscriberListByValidMdns(bulkSubsProvInfoDTO, presenceValidationResult.getValidMdns());

                // Return only valid mappings (partial success pattern)
                knLogger.info(methodName, "EXIT: Successfully mapped ", mappedCount,
                             " MDNs to presence homes, ", presenceValidationResult.getInvalidMdns().size(), " failed, ",
                             validMdnCount, " MDNs remaining in subscriber list");
            } else {
                knLogger.info(methodName, "EXIT: Successfully mapped all ", mappedCount, " MDNs to presence homes");
            }
            knLogger.debug("mdnToPresenceHomeMap::",mdnToPresenceHomeMap.size());
            return mdnToPresenceHomeMap;

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO error while fetching presence homes: ", e.getMessage());
            throw e;
        }
    }

    /**
     * Build failure response with common fields populated
     *
     * @param requestDTO Request DTO containing transaction and batch details
     * @param e Exception that caused the failure
     * @param startTime Processing start time in milliseconds
     * @return Populated failure response DTO
     */
    public IXDMResponseDTO buildFailureResponse(KnXDMBulkSubsProvInfoDTO requestDTO, Exception e, long startTime) {
        String methodName = "buildFailureResponse(KnXDMBulkSubsProvInfoDTO, Exception, long)";

        KnXDMBulkOpsRespDTO respDTO = new KnXDMBulkOpsRespDTO();

        // Set error details
        if (e instanceof KnException) {
            respDTO.setResponseCode(((KnException) e).getErrorCode());
            respDTO.setResponseMessage(((KnException) e).getErrorMessage());
        } else {
            respDTO.setResponseCode(KnBulkOpsErrorCodes.Validator.ERROR_CODE_INTERNAL_ERROR);
            respDTO.setResponseMessage(e.getMessage());
        }

        respDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.FAILURE.value());

        // Populate common fields from request
        if (requestDTO != null) {
            respDTO.setTransactionId(requestDTO.getTransactionId());
            respDTO.setBatchId(requestDTO.getBatchId());

            int totalCount = requestDTO.getSubscriberList() != null ? requestDTO.getSubscriberList().size() : 0;
            respDTO.setTotalCount(totalCount);
            respDTO.setFailureMdnCount(totalCount);
            respDTO.setSuccessMdnCount(0);
        }

        // Calculate processing time
        respDTO.setProcessingTimeMs(System.currentTimeMillis() - startTime);

        knLogger.debug(methodName, "Failure Response DTO - ", respDTO);
        return respDTO;
    }

    /**
     * Build partial response for mixed success/failure scenarios
     * Response status is determined by success/failure ratio
     *
     * @param requestDTO Request DTO containing transaction and batch details
     * @return Partially populated response DTO ready for MDN-level results
     */
    public KnXDMBulkOpsRespDTO buildPartialResponse(KnXDMBulkSubsProvInfoDTO requestDTO) throws KnBulkOpsException {
        String methodName = "buildPartialResponse(KnXDMBulkSubsProvInfoDTO, long)";

        KnXDMBulkOpsRespDTO respDTO = new KnXDMBulkOpsRespDTO();
        if (requestDTO != null) {
            respDTO.setTransactionId(requestDTO.getTransactionId());
            respDTO.setBatchId(requestDTO.getBatchId());

            int totalCount = requestDTO.getSubscriberList() != null ? requestDTO.getSubscriberList().size() : 0;
            respDTO.setTotalCount(totalCount);
        }

        // Setting XDMs Home
        respDTO.setXdmsHome(KnBulkOpsDBUtil.getXdmPttServerId());
        knLogger.debug(methodName, "Partial Response DTO initialized - ", respDTO);
        return respDTO;
    }

    public void finalizePartialResponse(KnXDMBulkOpsRespDTO partialRespDTO, int initialTotalCount, KnBulkOpsConstants.BulkOperations operationType) {
        String methodName = "finalizePartialResponse(KnXDMBulkOpsRespDTO, int, String)";

        if(partialRespDTO == null) {
            knLogger.warn(methodName, "Partial Response DTO is null, cannot finalize");
            return;
        }

        int failureCount = partialRespDTO.getFailureMdnCount();
        int successCount = initialTotalCount - failureCount;

        partialRespDTO.setSuccessMdnCount(successCount);

        // Determine overall response status
        if (successCount == 0) {
            partialRespDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.FAILURE.value());
            partialRespDTO.setResponseCode(KnBulkOpsErrorCodes.Validator.ERROR_CODE_INTERNAL_ERROR);
            partialRespDTO.setResponseMessage(getFailureMessage(operationType));
        } else if (failureCount == 0) {
            partialRespDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.SUCCESS.value());
            partialRespDTO.setResponseCode(KnBulkOpsConstants.SUCCESS_CODE);
            partialRespDTO.setResponseMessage(getSuccessMessage(operationType));
        } else {
            partialRespDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.PARTIAL_SUCCESS.value());
            partialRespDTO.setResponseCode(KnBulkOpsErrorCodes.Validator.ERROR_CODE_PARTIAL_SUCCESS);
            partialRespDTO.setResponseMessage(getPartialSuccessMessage(operationType));
        }

        knLogger.debug(methodName, "Finalized Partial Response DTO - ", partialRespDTO);
    }

    private String getSuccessMessage(KnBulkOpsConstants.BulkOperations operationType) {
        if (KnBulkOpsConstants.BulkOperations.CREATEBULKSUBSCRIBER.equals(operationType)) {
            return KnBulkOpsConstants.RESPONSE_MESSAGE.CREATE_BULK_SUBSCRIBER_SUCCESS.value();
        } else if (KnBulkOpsConstants.BulkOperations.UPDATEBULKSUBSCRIBER.equals(operationType)) {
            return KnBulkOpsConstants.RESPONSE_MESSAGE.UPDATE_BULK_SUBSCRIBER_SUCCESS.value();
        } else if (KnBulkOpsConstants.BulkOperations.DELETEBULKSUBSCRIBER.equals(operationType)) {
            return KnBulkOpsConstants.RESPONSE_MESSAGE.DELETE_BULK_SUBSCRIBER_SUCCESS.value();
        }
        return "Bulk operation completed successfully";
    }

    private String getFailureMessage(KnBulkOpsConstants.BulkOperations operationType) {
        if (KnBulkOpsConstants.BulkOperations.CREATEBULKSUBSCRIBER.equals(operationType)) {
            return KnBulkOpsConstants.RESPONSE_MESSAGE.CREATE_BULK_SUBSCRIBER_FAILED.value();
        } else if (KnBulkOpsConstants.BulkOperations.UPDATEBULKSUBSCRIBER.equals(operationType)) {
            return KnBulkOpsConstants.RESPONSE_MESSAGE.UPDATE_BULK_SUBSCRIBER_FAILED.value();
        } else if (KnBulkOpsConstants.BulkOperations.DELETEBULKSUBSCRIBER.equals(operationType)) {
            return KnBulkOpsConstants.RESPONSE_MESSAGE.DELETE_BULK_SUBSCRIBER_FAILED.value();
        }
        return "Bulk operation failed";
    }

    private String getPartialSuccessMessage(KnBulkOpsConstants.BulkOperations operationType) {
        if (KnBulkOpsConstants.BulkOperations.CREATEBULKSUBSCRIBER.equals(operationType)) {
            return KnBulkOpsConstants.RESPONSE_MESSAGE.CREATE_BULK_SUBSCRIBER_PARTIAL_SUCCESS.value();
        } else if (KnBulkOpsConstants.BulkOperations.UPDATEBULKSUBSCRIBER.equals(operationType)) {
            return KnBulkOpsConstants.RESPONSE_MESSAGE.UPDATE_BULK_SUBSCRIBER_PARTIAL_SUCCESS.value();
        } else if (KnBulkOpsConstants.BulkOperations.DELETEBULKSUBSCRIBER.equals(operationType)) {
            return KnBulkOpsConstants.RESPONSE_MESSAGE.DELETE_BULK_SUBSCRIBER_PARTIAL_SUCCESS.value();
        }
        return "Bulk operation partially completed";
    }


    /**
     * Retrieve POC Service Configurations for multiple POC homes in bulk
     *
     * Algorithm:
     * 1. Extract unique POC server IDs from the MDN->POC home map
     * 2. Perform a single batch query to fetch all POC service configurations
     * 3. Return a map of POC server ID to its service configuration DTO
     *
     * Optimizations:
     * - Eliminates redundant queries for duplicate POC homes
     * - Uses concurrent map for thread-safe access if needed
     *
     * @param mdnToPocHomeMap Map of MDN to POC home
     * @param bulkOpsXDMServerDAO DAO instance for database operations
     * @param persisterTxn Database transaction
     * @return Map of POC server ID to its KnPOCSvcConfigDTO
     * @throws KnDAOException if database operation fails
     */
//    public Map<String, KnPOCSvcConfigDTO> retrievePOCSvcConfigsForBulk(
//            Map<String, String> mdnToPocHomeMap,
//            KnBulkOpsXDMServerDAO bulkOpsXDMServerDAO,
//            KnPersisterTxn persisterTxn) throws KnDAOException {
//
//        String methodName = "retrievePOCSvcConfigsForBulk(Map<String, String>, KnBulkOpsXDMServerDAO, KnPersisterTxn)";
//        knLogger.debug(methodName, "ENTRY: Retrieving POC Service Configs for bulk operation");
//
//        // Early return for null/empty input
//        if (mdnToPocHomeMap == null || mdnToPocHomeMap.isEmpty()) {
//            knLogger.debug(methodName, "No POC home mappings provided, returning empty map");
//            return new ConcurrentHashMap<>();
//        }
//
//        // Extract unique POC server IDs (eliminating duplicates efficiently)
//        // mdnToPocHomeMap.values() may have duplicate POC homes if multiple MDNs share same POC
//        final Set<String> uniquePocServerIds = new HashSet<>(mdnToPocHomeMap.values());
//
//        knLogger.info(methodName, "Found ", uniquePocServerIds.size(), " unique POC servers from ",
//                     mdnToPocHomeMap.size(), " MDN mappings");
//
//        // Fetch all POC service configs in single batch query
//        final Map<String, KnPOCSvcConfigDTO> pocConfigMap =
//            bulkOpsXDMServerDAO.retrievePOCSvcConfigForMultiplePocHomes(uniquePocServerIds, persisterTxn);
//
//        knLogger.debug(methodName, "EXIT: Retrieved ", pocConfigMap.size(), " POC service configurations");
//        return pocConfigMap;
//    }

    /**
     * Sort the subscriber list in the bulk subscription provisioning DTO by MDN in ascending order
     *
     * @param bulkSubsProvInfoDTO Bulk subscription provisioning info DTO
     */
    public void sortSubscriberListByMdn(KnXDMBulkSubsProvInfoDTO bulkSubsProvInfoDTO) {
        String methodName = "sortSubscriberListByMdn(KnXDMBulkSubsProvInfoDTO)";

        if (bulkSubsProvInfoDTO == null || bulkSubsProvInfoDTO.getSubscriberList() == null
            || bulkSubsProvInfoDTO.getSubscriberList().isEmpty()) {
            knLogger.debug(methodName, "No subscriber list to sort, skipping");
            return;
        }

        bulkSubsProvInfoDTO.getSubscriberList().sort(
            Comparator.comparing(KnBulkSubscriberEntry::getMdn,
                Comparator.nullsLast(Comparator.naturalOrder())
            )
        );

    }

    /**
     * Generate activeFS2 for each subscriber in bulk provisioning request
     *
     * Algorithm:
     * 1. Pre-fetch common values (XDM PTT server ID, corporate flags)
     * 2. For each subscriber:
     *    - Determine POC home (from entry, map, or fallback)
     *    - Determine Presence home (from entry, map, or fallback to POC)
     *    - Generate activeFS2 using feature set utility
     *    - Set activeFS2 in subscriber entry
     *
     * @param bulkSubsProvInfoDTO Bulk subscription provisioning info DTO
     * @param mdnToPocHomeMap Map of MDN to POC home
     * @param mdnToPresenceHomeMap Map of MDN to Presence home
     * @param corpProfileInfoDTO Corporate profile info DTO (if applicable)
     * @param clientFS2 Client feature set 2
     * @param subsFS2 Subscriber feature set 2
     * @param corpAdminFS2 Corporate admin feature set 2
     * @param clientCapOverrideBitMask Client capability override bitmask
     * @param xdmsFs2 XDMS feature set 2
     * @param userProfileFS2 User profile feature set 2
     * @param featureSetUtil Feature set utility instance
     * @throws KnBulkOpsException if activeFS2 generation fails
     */
    public void generateActiveFS2ForBulkSubscribers(
            KnXDMBulkSubsProvInfoDTO bulkSubsProvInfoDTO,
            Map<String, String> mdnToPocHomeMap,
            Map<String, String> mdnToPresenceHomeMap,
            KnBulkOpsCorpProfileDAO.KnCorpProfileInfo corpProfileInfoDTO,
            String clientFS2,
            String subsFS2,
            String corpAdminFS2,
            String clientCapOverrideBitMask,
            String xdmsFs2,
            String userProfileFS2,
            KnFeatureSetUtil featureSetUtil) throws KnBulkOpsException {

        String methodName = "generateActiveFS2ForBulkSubscribers(...)";

        final int subscriberCount = bulkSubsProvInfoDTO.getSubscriberList().size();
        knLogger.debug(methodName, "ENTRY: Generating activeFS2 for ", subscriberCount, " subscribers");

        try {
            // Pre-fetch common values to avoid repeated calls
            final String xdmPttServerId = KnBulkOpsDBUtil.getXdmPttServerId();
            final boolean isCorporate = bulkSubsProvInfoDTO.getCorporateSubscriptionType()
                    == KnBulkOpsConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value();
            final String opsCorpFS2 = (isCorporate && corpProfileInfoDTO != null)
                    ? corpProfileInfoDTO.getOpsCorpFS2() : null;

            String opsFS2;
            if(isCorporate) {
                opsFS2 = corpProfileInfoDTO.getOpsCorpFS2();
            } else {
                opsFS2 = featureSetUtil.getDefFinalOpsFS();
            }
            bulkSubsProvInfoDTO.setOpsFS2(opsFS2);

            for (var subscriberEntry : bulkSubsProvInfoDTO.getSubscriberList()) {
                final String mdn = subscriberEntry.getMdn();

                // Get POC home: Check subscriber entry first, then map, finally fallback
                String poCPttServerId = subscriberEntry.getPocHome();
                if (poCPttServerId == null && mdnToPocHomeMap != null) {
                    poCPttServerId = mdnToPocHomeMap.get(mdn);
                }
                if (poCPttServerId == null) {
                    poCPttServerId = xdmPttServerId;
                }

                // Get Presence home: Check subscriber entry first, then map, finally fallback to POC
                String presencePttServerId = subscriberEntry.getPresenceHome();
                if (presencePttServerId == null && mdnToPresenceHomeMap != null) {
                    presencePttServerId = mdnToPresenceHomeMap.get(mdn);
                }
                if (presencePttServerId == null) {
                    presencePttServerId = poCPttServerId;
                }

                // Generate activeFS2 based on subscription type
                String activeFS2;
                if (isCorporate) {
                    activeFS2 = featureSetUtil.generateActiveFeatBitSet(
                            poCPttServerId, presencePttServerId, xdmPttServerId, clientFS2, subsFS2, opsFS2,
                            opsCorpFS2, corpAdminFS2, clientCapOverrideBitMask, xdmsFs2, userProfileFS2
                    );
                } else {
                    activeFS2 = featureSetUtil.generateActiveFeatBitSet(
                            poCPttServerId, presencePttServerId, xdmPttServerId, clientFS2, subsFS2, opsFS2,
                            clientCapOverrideBitMask, xdmsFs2, userProfileFS2
                    );
                }

                // Set activeFS2 for this subscriber
                subscriberEntry.setActiveFS2(activeFS2);
            }

            knLogger.info(methodName, "EXIT: Successfully generated activeFS2 for all ",
                         subscriberCount, " subscribers");
        } catch (Exception e) {
            knLogger.error(methodName, "Failed to generate activeFS2: ", e);
            throw new KnBulkOpsException(KnBulkOpsErrorCodes.BOEntity.ACTIVE_FS2_GENERATION_FAILED,
                "Failed to generate activeFS2 for bulk subscribers", e);
        }
    }

    /**
     * Generate HA1 password for digest authentication
     *
     * @param userId   User ID (typically MDN with tel:+ prefix)
     * @param realm    Authentication realm
     * @param password Plain text password
     * @return HA1 digest password
     * @throws KnBulkOpsException if password generation fails
     */
    public String generateHA1(String userId, String realm, String password) throws KnBulkOpsException {
        String methodName = "generateHA1(String, String, String)";
        knLogger.debug(methodName, "ENTRY: Generating HA1 digest for userId: ", userId, ", realm: ", realm);

        try {
            String ha1str = userId + ":" + realm + ":" + password;
            String ha1Password = getMd5HashfromString(ha1str);
            knLogger.debug(methodName, "EXIT: Successfully generated HA1 password");
            return ha1Password;
        } catch (Exception e) {
            knLogger.error(methodName, "Failed to generate HA1 Password: ", e);
            throw new KnBulkOpsException(KnBulkOpsErrorCodes.BOEntity.BULK_DEVICE_CREATION_FAILED,
                    "Failed to generate HA1 password: " + e.getMessage(), e);
        }
    }

    /**
     * Generate MD5 hash from string
     *
     * @param text Input text to hash
     * @return MD5 hash as hex string
     * @throws Exception if hashing fails
     */
    private String getMd5HashfromString(String text) throws Exception {
        java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
        byte[] md5hash;
        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        md5hash = md.digest();
        return convertToHex(md5hash);
    }

    /**
     * Convert byte array to hex string
     *
     * @param data Byte array to convert
     * @return Hex string representation
     */
    private String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte aData : data) {
            int halfbyte = (aData >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9)) {
                    buf.append((char) ('0' + halfbyte));
                } else {
                    buf.append((char) ('a' + (halfbyte - 10)));
                }
                halfbyte = aData & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    /**
     * Update Etag for NNI Subscribers
     * This method manages the LinkedGwKey in POCCORPINFO and updates the etag in CORP_GW_LINKED_ACCOUNTINFO
     * based on whether Alias/Group/SGMDNPatch subscribers exist in the corporate
     *
     * Reuses the already fetched corporate profile DTO to avoid redundant database calls
     *
     * @param corpProfileInfoDTO Corporate profile DTO (already fetched - contains corpId, extCorpId, linkedGwKey)
     * @param  bulkOpsXDMServerDAO DAO instance for XDM server operations
     * @param bulkOpsCorpProfileDAO DAO instance for corporate profile operations
     * @param bulkOpsSubsProfileDAO DAO instance for subscriber profile operations
     * @param persisterTxn Database transaction
     * @throws KnDAOException if database operation fails
     */
    public void updateEtagForNNISubscribers(
            KnBulkOpsCorpProfileDAO.KnCorpProfileInfo corpProfileInfoDTO,
            KnBulkOpsXDMServerDAO bulkOpsXDMServerDAO,
            KnBulkOpsCorpProfileDAO bulkOpsCorpProfileDAO,
            KnBulkOpsSubsProfileDAO bulkOpsSubsProfileDAO,
            KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "updateEtagForNNISubscribers(KnCorpProfileInfo, KnBulkOpsCorpProfileDAO, KnBulkOpsSubsProfileDAO, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: Updating etag for NNI subscribers, extCorpId: ", corpProfileInfoDTO.getExtCorpId());

        try {
            int corpId = corpProfileInfoDTO.getCorpId();
            String extCorpId = corpProfileInfoDTO.getExtCorpId();

            if (corpId == 0) {
                knLogger.debug(methodName, "Corporate doesn't exist, nothing to update");
                return;
            }

            knLogger.debug(methodName, "Corporate exists with corpId: ", corpId);

            // Check if Alias/Group/SGMDNPatch MDNs exist in this corporate
            List<Integer> clientTypeList = new ArrayList<>();
            clientTypeList.add(KnConstants.SUBS_CLIENT_TYPE.ALIASMDN.value());
            clientTypeList.add(KnConstants.SUBS_CLIENT_TYPE.GROUPMDN.value());
            clientTypeList.add(KnConstants.SUBS_CLIENT_TYPE.SGMDNPATCH.value());

            int aliasNGroupMdnCount = bulkOpsSubsProfileDAO.getSubsCountOfClientTypeForCorp(corpId, clientTypeList, persisterTxn);
            knLogger.debug(methodName, "Alias/Group/SGMDNPatch MDN count: ", aliasNGroupMdnCount);

            if (aliasNGroupMdnCount == 0) {
                // No Alias/Group MDNs present - nullify LinkedGwKey
                knLogger.debug(methodName, "No Alias/Group MDNs present in corp, nullifying LinkedGwKey for extCorpId: ", extCorpId);
                bulkOpsCorpProfileDAO.updateLinkedGwKeyOfCorp(extCorpId, persisterTxn);
                knLogger.info(methodName, "LinkedGwKey nullified in POCCORPINFO table");
            } else {
                // Alias/Group MDNs present - check and update etag if LinkedGwKey exists
                knLogger.debug(methodName, "Alias/Group MDNs present in corp, extCorpId: ", extCorpId);

                String linkedGwKey = corpProfileInfoDTO.getLinkedGwKey();
                if (linkedGwKey != null && !linkedGwKey.isEmpty()) {
                    knLogger.debug(methodName, "LinkedGwKey exists: ", linkedGwKey, ", updating etag");

                    long lastProfileUpdateTime = System.currentTimeMillis();
                    bulkOpsXDMServerDAO.updateEtagForCorpNNIRefId(linkedGwKey, lastProfileUpdateTime, persisterTxn);

                    knLogger.info(methodName, "Etag updated in CORP_GW_LINKED_ACCOUNTINFO table for linkedGwKey: ", linkedGwKey);
                } else {
                    knLogger.info(methodName, "LinkedGwKey is null or empty, no etag update needed");
                }
            }

            knLogger.debug(methodName, "EXIT: Etag update completed successfully");

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred during etag update: ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected exception during etag update: ", e);
            throw new KnDAOException("ETAG_UPDATE_FAILED",
                    "Failed to update etag for NNI subscribers: " + e.getMessage(), e);
        }
    }

    public void validateSubAuthStatus(List<String> mdns, KnPersisterTxn persisterTxn) throws KnProvException, KnDAOException {
        String methodName = "validateSubAuthStatus";

        if (mdns != null && !mdns.isEmpty() && mdns.stream().anyMatch(Objects::nonNull)) {
            knLogger.debug(methodName, "Validating MDNs service auth status - ", mdns);

            // Fetch the map containing MDN and service auth status
            Map<String, Integer> subscriberServiceAuthStatusMap = provClientIntf.getSubscriberServiceAuthStatus(mdns, persisterTxn);

            for (Map.Entry<String, Integer> entry : subscriberServiceAuthStatusMap.entrySet()) {
                String currentMdn = entry.getKey();
                Integer subscriberServiceAuthStatus = entry.getValue();

                // Validate the service auth status for each MDN
                if (subscriberServiceAuthStatus != null
                        && com.kodiak.xdms.server.common.resources.KnConstants.SERVICE_AUTH_STATUS.MARKED_FOR_ASYNC_DELETION.value() == subscriberServiceAuthStatus) {
                    knLogger.error(methodName, "Inactive subscriber deletion in-progress for MDN=",currentMdn, " ServiceAuthStatus: ", subscriberServiceAuthStatus);
                    throw new KnProvBOException(com.kodiak.xdms.server.subsmgmt.resources.KnErrorCodes.Validator.INACTIVE_SUBSCRIBER_DELETE_IN_PROGRESS, "Inactive subscriber deletion in-progress for MDN: " + currentMdn
                    );
                }
            }
        }
    }

    public Map<String, KnBulkOpsErrorDetail> validateSubAuthStatusMap(List<String> mdns, KnPersisterTxn persisterTxn) throws KnBulkOpsException, KnDAOException {
        String methodName = "validateSubAuthStatus";
        Map<String, KnBulkOpsErrorDetail> invalidAuthStatusMdns = new HashMap<>();

        if (mdns != null && !mdns.isEmpty() && mdns.stream().anyMatch(Objects::nonNull)) {
            knLogger.debug(methodName, "Validating MDNs service auth status - ", mdns);

            // Fetch the map containing MDN and service auth status
            KnPOCSubscrInfoDAO knPOCSubscrInfoDAO = new KnPOCSubscrInfoDAO();
            Map<String, Integer> subscriberServiceAuthStatusMap = knPOCSubscrInfoDAO.getSubscriberServiceAuthStatus(mdns, persisterTxn);

            for (Map.Entry<String, Integer> entry : subscriberServiceAuthStatusMap.entrySet()) {
                String currentMdn = entry.getKey().replaceAll("\\s+", "");;
                Integer subscriberServiceAuthStatus = entry.getValue();

                // Validate the service auth status for each MDN
                if (subscriberServiceAuthStatus != null
                        && KnBulkOpsConstants.SERVICE_AUTH_STATUS.MARKED_FOR_ASYNC_DELETION.value() == subscriberServiceAuthStatus) {
                    knLogger.error(methodName, "Inactive subscriber deletion in-progress for MDN=", currentMdn, " ServiceAuthStatus: ", subscriberServiceAuthStatus);
                    knBulkOpsErrorDetail.setMdn(currentMdn);
                    knBulkOpsErrorDetail.setErrorCode(INACTIVE_SUBSCRIBER_DELETE_IN_PROGRESS);
                    knBulkOpsErrorDetail.setErrorMessage("Inactive subscriber deletion in-progress for MDN: " + currentMdn);
                    invalidAuthStatusMdns.put(currentMdn, knBulkOpsErrorDetail);
                }
            }
        }
        knLogger.info(methodName, "Mdns marked for async delete", invalidAuthStatusMdns.size());
        return invalidAuthStatusMdns;
    }

    public void deleteAllContactsAndGroups(List<String> mdnList,String xdmPttServerId, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "deleteAllContactsAndGroups";
        knLogger.debug( methodName, "ENTRY : Mdn: ", KnGDPRTemplate.mdnList(mdnList));
        KnIPPubSubsDTO inputDTO = new KnIPPubSubsDTO();
        inputDTO.setMdnList(mdnList);
        inputDTO.setPttServerId(xdmPttServerId);
        knLogger.debug( methodName, "Library Call : InputDTO:  ", inputDTO);
        pubClientIntf.deleteAllContactsAndGroups(inputDTO, persisterTxn);
        knLogger.debug( methodName, "Exit : ");
        return;
    }

    public boolean getXcapMobileSyncFlag(KnPersisterTxn persisterTxn) throws KnBOException {
        String methodName = "getXcapMobileSyncFlag()";
        knLogger.debug(methodName, "ENTRY");
        /*UCSPLATFORM-7936 : Removed the code below as the flag is now always considered enabled for sending the MCS notification.
          This change aligns with UCSPLATFORM-7936, where the flag dependency has been removed.*/
        return true;
    }

    /**
     * @param mdns
     * @param persisterTxn
     * @return
     * @throws KnBOException
     */
    public Map<String,String> getXCAPRootURI(List<String> mdns, KnPersisterTxn persisterTxn) throws KnBOException, KnBulkOpsException {
        final String methodName = "getXCAPRootURI(mdn, KnPersisterTxn)";
        knLogger.debug(methodName, KnGDPRTemplate.mdnList(mdns), persisterTxn);
        String xcapRootURI = null;
        Map<String,Integer> mdntoApnId;
        Map<String,Integer> mdntoPv;
        Map<String, String> mdntoXcapUri = new HashMap<>();
        KnBulkOpsXDMServerDAO xdmServerDAO = new KnBulkOpsXDMServerDAO();
        KnPOCSubscrInfoDAO pocSubscrInfoDAO = new KnPOCSubscrInfoDAO();
        Integer apnid;
        Integer pv;

        try {
            mdntoApnId = xdmServerDAO.getSubsApnIdsBulk(mdns, persisterTxn);
            //trim mdn keys and store in in same map
            Map<String, Integer> trimmedMdntoApnId = new HashMap<>();
            for (Map.Entry<String, Integer> entry : mdntoApnId.entrySet()) {

                String trimmedMdn = entry.getKey().replaceAll("\\s+", "");
                knLogger.debug(methodName, "retrieved   test - ", trimmedMdn, entry.getValue());
                trimmedMdntoApnId.put(trimmedMdn, entry.getValue());
            }
            mdntoApnId = trimmedMdntoApnId;
            mdntoPv  = pocSubscrInfoDAO.selectSubscribersPV(mdns, persisterTxn);
            knLogger.debug(methodName, "retrieved mdnapnid  test - ", mdntoApnId);
            knLogger.debug(methodName, "retrieved mdnpv  test - ", mdntoPv);



            for(String mdn : mdns){
                apnid =mdntoApnId.get(mdn);
                pv = mdntoPv.get(mdn);
                knLogger.debug(methodName, "retrieved apnid  test - ", apnid);
                knLogger.debug(methodName, "retrieved pv  test - ", pv);

                // Skip MDN if no APN ID found
                if (apnid == null) {
                    knLogger.warn(methodName, "No APN ID found for MDN: ", KnGDPRTemplate.mdn(mdn), " - skipping XCAP URI retrieval");
                    continue;
                }

                xcapRootURI = getXCAPRootURI(apnid, persisterTxn, Boolean.FALSE);
                knLogger.debug(methodName, "retrieved xcapRootUri  test - ", KnGDPRTemplate.mdnUriTemplate(xcapRootURI));

                if(xcapRootURI==null){
                    knLogger.debug(methodName, "retrieved xcapRootUri  is null - ", KnGDPRTemplate.mdnUriTemplate(xcapRootURI));
                    continue;
                }
                if(pv >= PROTOCOL_VERSION_13_X){
                    xcapRootURI = xcapRootURI + com.kodiak.common.resources.KnConstants.OIDC_XCAP_ROOT_CONTEXT;
                }else{
                    xcapRootURI = xcapRootURI + com.kodiak.common.resources.KnConstants.XCAP_ROOT_CONTEXT;

                }
                mdntoXcapUri.put(mdn, xcapRootURI);
            }
        } catch (KnConnectionException e) {
            knLogger.error(methodName, "Connection Exception  Occurred - ", e);
            throw new KnBOException(KnErrorCodes.DAO.CONNECTION_FAILED, "Operation failed due to connection error : ", e);
        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Persistence Exception  Occurred - ", e);
            String errorCode = e.getErrorCode();
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(errorCode)) {
                throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                        " xcap root uri  found", e);
            }
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred");
            throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to get  apn profile info  ", e);
        }
        knLogger.debug(methodName, "retrieved xcapRootUriMap  - ", mdntoXcapUri);

        return mdntoXcapUri;

    }


    private String getXCAPRootURI(Integer apnId, KnPersisterTxn persisterTxn, boolean isMcxNotify) throws KnBOException {
        final String methodName = "getXCAPRootURI(Integer, KnPersisterTxn)";
        knLogger.debug(methodName, apnId, persisterTxn);


        String xcapRootURI = null;
        Map<Integer, KnAPNConfigDTO> knAPNConfDTOMap = new HashMap<Integer, KnAPNConfigDTO>();
        KnAPNConfigDTO apnConfigDTO = new KnAPNConfigDTO();

        try {
            KnConfigurationsManager configManager = KnConfigurationsManager.getInstance();
            ICacheManager cacheManager = configManager.getCacheManager();
            knLogger.debug(methodName, "reading from Cache");
            knAPNConfDTOMap = (Map<Integer, KnAPNConfigDTO>) cacheManager.get(KnCacheKeys.APN_CONFIG_INFO);

            if (knAPNConfDTOMap == null || knAPNConfDTOMap.isEmpty()) {
                knLogger.debug(methodName, "Not found in cache hence reading from db");
                KnBulkOpsXDMServerDAO xdmServerDAO = new KnBulkOpsXDMServerDAO();
                knAPNConfDTOMap = xdmServerDAO.retrieveAPNInfoConfig(persisterTxn);
                cacheManager.put(KnCacheKeys.APN_CONFIG_INFO, knAPNConfDTOMap);
            }
            apnConfigDTO = (KnAPNConfigDTO) knAPNConfDTOMap.get(apnId);
            knLogger.debug(methodName, "APNconfigDTO  value -", apnConfigDTO);
            if (apnConfigDTO == null) {
                xcapRootURI = null;
            }
            if (!isMcxNotify && xcapRootURI!=null) {
                xcapRootURI = apnConfigDTO.getApnXCAPUri();
            } else {
                xcapRootURI = apnConfigDTO.getMcsXCAPUri();
            }
            knLogger.debug(methodName, "retrieved xcapRootUri  - ", KnGDPRTemplate.mdnUriTemplate(xcapRootURI));
        } catch (KnConnectionException e) {
            knLogger.error(methodName, "Connection Exception  Occurred - ", e);
            throw new KnBOException(KnErrorCodes.DAO.CONNECTION_FAILED, "Operation failed due to connection error : ", e);
        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Persistence Exception  Occurred - ", e);
            String errorCode = e.getErrorCode();
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(errorCode)) {
                throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                        " xcap root uri  found", e);
            }
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred", e);
            throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to get  apn profile info  ", e);
        }
        knLogger.debug(methodName, KnGDPRTemplate.mdnUriTemplate(xcapRootURI));
        return xcapRootURI;
    }


    public String generateDirDocUri(String mdn) {
        String methodName = "generateDirDocUri(String)";
        String documentURI;

        String appUId = DIR_DOC_AUID;
        String documentType = DIR_DOC_TYPE;
        String documentName = DIR_DOC_NAME;
        String xui = generateXUI(mdn);

        StringBuilder strBuffer = new StringBuilder(100);
        strBuffer.append(appUId).append("/");
        strBuffer.append(documentType).append("/");
        strBuffer.append(xui).append("/");
        strBuffer.append(documentName);

        documentURI = strBuffer.toString();

        knLogger.debug(methodName, "sel uri generated - " + KnGDPRTemplate.mdnUriTemplate(documentURI));
        return documentURI;
    }

    /**
     * method to generate the XUI
     *
     * @param mdn String
     * @return String XUI
     */
    public String generateXUI(String mdn) {
        String methodName = "generateXUI";
        knLogger.debug(methodName, "generating XUI for mdn - " + KnGDPRTemplate.mdn(mdn));
        String xui = TEL_URI_TEMPLATE + mdn;
        knLogger.debug(methodName, "XUI for the MDN - " + KnGDPRTemplate.mdnPart(xui));
        return xui;
    }

    public String generateSubsConfigSelUri(String mdn) {
        String methodName = "generateSubsConfigSelUri";

        knLogger.debug(methodName, "generating sel uri for Notification for mdn - ",  KnGDPRTemplate.mdn(mdn));
        StringBuffer strBuffer = new StringBuffer(150);
        strBuffer.append("xcap-directory/folder%5B@auid=%22kn-subscriber-config%22%5D/entry%5B@uri=%22kn-subscriber-config/users/tel:+");
        strBuffer.append(mdn);
        strBuffer.append("/index%22%5D/@etag");

        knLogger.debug(methodName, "generated sel uri for Notification for mdn - ",  KnGDPRTemplate.mdn(mdn), " is - ", KnGDPRTemplate.mdnUriTemplate(strBuffer.toString())
        );
        return strBuffer.toString();
    }

    /**
     * @param mdns
     * @param persisterTxn
     * @return
     * @throws KnBOException
     */
    public Map<String, String> getXCAPRootURI(List<String> mdns, KnPersisterTxn persisterTxn, boolean isMcxNotify) throws KnBOException {
        return getXCAPRootURI(mdns, false, persisterTxn, isMcxNotify);
    }

    public Map<String, String> getXCAPRootURI(List<String> mdns, boolean readOnly, KnPersisterTxn persisterTxn, boolean isMcxNotify) throws KnBOException {
        final String methodName = "getXCAPRootURI(List<String>, boolean, KnPersisterTxn, boolean)";
        Map<String, String> xcapRootUris = new HashMap<>();
        knLogger.entry(methodName, "ENTRY : mdns size ", mdns.size());
        if(mdns == null || mdns.isEmpty())
        {
            knLogger.exit(methodName, "EXIT : xcapRootUris size " , xcapRootUris);
            return xcapRootUris;
        }
        var mdnListArray = new ArrayList<>(mdns);
        var subsLists = KnGeneralUtil.splitList(mdnListArray, BULK_UPDATE_SIZE);
        for (var subsList : subsLists) {
            var subXCAPRootURI = getSubXCAPRootURI(subsList, readOnly, persisterTxn, isMcxNotify);
            if (subXCAPRootURI != null && !subXCAPRootURI.isEmpty())
            {
                xcapRootUris.putAll(subXCAPRootURI);
            }
        }
        knLogger.exit(methodName, "EXIT : xcapRootUris size " , xcapRootUris == null ? 0 : xcapRootUris.size());
        return xcapRootUris;

    }

    private Map<String, String> getSubXCAPRootURI(List<String> mdns, boolean readOnly, KnPersisterTxn persisterTxn, boolean isMcxNotify) throws KnBOException {
        final String methodName = "getSubXCAPRootURI(List<String>, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, KnGDPRTemplate.mdnList(mdns));
        Map<String, String> xcapRootUris = null;
        try {
            KnBulkOpsXDMServerDAO xdmServerDAO = new KnBulkOpsXDMServerDAO();
            KnPOCSubscrInfoDAO pocSubscrInfoDAO = new KnPOCSubscrInfoDAO();
            if (!(mdns == null || mdns.isEmpty())) {
                Map<String, Integer> subsApnInfo = xdmServerDAO.getSubsApnIdsBulk(mdns, persisterTxn);
                Map<String, Integer> pvs = pocSubscrInfoDAO.selectSubscribersPV(mdns, persisterTxn);
                xcapRootUris = new HashMap<String, String>();
                for (Map.Entry<String, Integer> mapSet : subsApnInfo.entrySet()) {
                    String mdn = mapSet.getKey();
                    Integer apnId = mapSet.getValue();
                    Integer pv = pvs.get(mdn);

                    // Skip if no protocol version found
                    if (pv == null) {
                        knLogger.warn(methodName, "No protocol version found for MDN: ", KnGDPRTemplate.mdn(mdn), " - skipping XCAP URI");
                        continue;
                    }

                    String xcapRootURI = getXCAPRootURI(apnId, persisterTxn, isMcxNotify);
                    if(pv >= PROTOCOL_VERSION_13_X && !isMcxNotify){
                        xcapRootURI = xcapRootURI + com.kodiak.common.resources.KnConstants.OIDC_XCAP_ROOT_CONTEXT;
                    } else if(pv > PROTOCOL_VERSION_18 && isMcxNotify){
                        xcapRootURI = xcapRootURI + com.kodiak.common.resources.KnConstants.MCSXCAP_XCAP_ROOT_CONTEXT;
                    } else {
                        xcapRootURI = xcapRootURI + com.kodiak.common.resources.KnConstants.XCAP_ROOT_CONTEXT;
                    }
                    xcapRootUris.put(mdn, xcapRootURI);
                }
            }

        } catch (KnConnectionException e) {
            knLogger.error(methodName, "Connection Exception  Occurred - ", e);
            throw new KnBOException(KnErrorCodes.DAO.CONNECTION_FAILED, "Operation failed due to connection error : ", e);
        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Persistence Exception  Occurred - ", e);
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(e.getErrorCode())) {
                throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                        " xcap root uri  found", e);
            }
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred");
            throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to get  apn profile info  ", e);
        }
        knLogger.debug(methodName,KnGDPRTemplate.mdnMap(xcapRootUris));
        return xcapRootUris;
    }

    public Map<String, KnSubsProfileDTO> getSubsRespMapCommon(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnBulkOpsException, KnDAOException {
        KnPOCSubscrInfoDAO pocSubscrInfoDAO = new KnPOCSubscrInfoDAO();
        return pocSubscrInfoDAO.getSubsProfileDTOMapBulk(mdnList, persisterTxn);
    }

    public List<String> validateIfSubscriberExist(List<String> validMdnList, int corpId, KnPersisterTxn persisterTxn) throws KnBulkOpsException {
        KnPOCSubscrInfoDAO pocSubscrInfoDAO = new KnPOCSubscrInfoDAO();
        List<String> mdnList = pocSubscrInfoDAO.checkIfMdnExist(validMdnList, corpId, persisterTxn);
        return mdnList;
    }

    public Map<Integer, List<String>> getMdnSublists(List<String> mdnList, String pttServerId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpListMemberDAO corpListMemberDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpListMemberDAO(pttServerId);
        return corpListMemberDAO.getSubsSublistList(mdnList, persisterTxn);
    }
}
