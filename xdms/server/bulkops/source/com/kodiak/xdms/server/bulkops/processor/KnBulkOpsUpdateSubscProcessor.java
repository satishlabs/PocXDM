package com.kodiak.xdms.server.bulkops.processor;

import com.kodiak.common.commdto.common.KnBulkSubscriberEntry;
import com.kodiak.common.commdto.request.IXDMRequestDTO;
import com.kodiak.common.commdto.request.KnXDMBulkSubsProvInfoDTO;
import com.kodiak.common.commdto.response.IXDMResponseDTO;
import com.kodiak.common.commdto.response.KnXDMBulkOpsRespDTO;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnDbUtil;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.frameworks.statisticalmgr.KnOMConstants;
import com.kodiak.frameworks.statisticalmgr.KnStatisticsManagerImpl;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.bulkops.KnBulkOpsException;
import com.kodiak.xdms.server.bulkops.dao.KnBulkOpsCorpProfileDAO;
import com.kodiak.xdms.server.bulkops.dao.KnBulkOpsXDMServerDAO;
import com.kodiak.xdms.server.bulkops.dao.KnXDMDirectoryDAO;
import com.kodiak.xdms.server.bulkops.dao.KnPOCSubscrInfoDAO;
import com.kodiak.xdms.server.common.dto.common.KnDocChangeListDTO;
import com.kodiak.common.commdto.common.KnBulkSubsProfileDTO;
import com.kodiak.xdms.server.bulkops.resources.KnBulkOpsConstants;
import com.kodiak.xdms.server.bulkops.resources.KnBulkOpsErrorCodes;
import com.kodiak.xdms.server.bulkops.utils.KnBulkOpsDBUtil;
import com.kodiak.xdms.server.bulkops.utils.KnBulkOpsInfoUtil;
import com.kodiak.xdms.server.bulkops.utils.KnBulkOpsNotifyUtil;
import com.kodiak.xdms.server.bulkops.dto.common.KnSubscrEXDMSNotifyDto;
import com.kodiak.xdms.server.subsmgmt.business.helper.KnProvSMSUtil;
import com.kodiak.xdms.server.subsmgmt.dto.common.KnProvSMSDTO;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvConstants;
import com.kodiak.xdms.server.common.dto.common.KnCorporateExdmsNotifyDto;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDirChgDTO;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDocChgDTO;
import com.kodiak.xdms.notificationmgr.beans.KnXcapDiffDocDTO;
import com.kodiak.xdms.notificationmgr.beans.KnXcapDiffNotifyDTO;
import com.kodiak.common.commdto.common.KnNotificationParamDTO;
import com.kodiak.xdms.notificationmgr.beans.KnProfileNotifyDTO;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpCommonInfoUtil;
import com.kodiak.xdms.server.common.dto.common.KnCorpProfileDTO;
import com.kodiak.xdms.server.subsmgmt.business.helper.KnProvInfoUtil;
import com.kodiak.utilities.featureset.KnFeatureSetUtil;
import com.kodiak.utilities.featureset.KnFeatureSetException;
import com.kodiak.xdms.notificationmgr.IXcapDiffNotifierIntf;
import com.kodiak.xdms.notificationmgr.impl.KnXcapDiffNotifierImpl;

import static com.kodiak.common.resources.KnConstants.MICROSERVICES_EVENT_TYPE.MODIFY_SUBSCRIBER;
import static com.kodiak.common.resources.KnConstants.MICROSERVICES_EVENT_TYPE.MODIFY_CORP;
import static com.kodiak.common.resources.KnConstants.MICROSERVICES_EVENT_TYPE.FEATURE_BIT_CHANGE;
import static com.kodiak.common.resources.KnConstants.MICROSERVICE_NOTIFY_DOC_VER;
import static com.kodiak.common.resources.KnConstants.ENABLED;
import static com.kodiak.common.resources.KnConstants.PROTOCOL_VERSION_REGEX;
import static com.kodiak.common.resources.KnConstants.FEATURE_SET;
import static com.kodiak.common.resources.KnConstants.PROTOCOL_VERSION_18;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Processor class for Bulk Subscriber Update Operations.
 *
 * This processor handles bulk update of subscriber profiles with LIMITED fields:
 * - MDN list (mdn and networkName from KnBulkSubscriberEntry)
 * - PkgIdMap (package ID mappings for subscription tier changes)
 * - extCorpId (external corporate ID)
 * - transactionId (for tracking)
 * - corporateName
 * - accountId
 *
 * NOT applicable (will be null):
 * - hierarchyType - No hierarchy validation
 * - subscriberClientType - No client type validation
 * - operationType - No auth status validation
 * - subscriptionTypes - No subscription type changes
 * - featureSets - No feature set changes
 * - autoPair, licenseType, firstNetIndicator - Not provided
 */
public class KnBulkOpsUpdateSubscProcessor {
    private static final KnLogger knLogger = KnLogger.getLogger(KnBulkOpsUpdateSubscProcessor.class);
    private static KnBulkOpsUpdateSubscProcessor instance = null;

    private KnBulkOpsInfoUtil bulkOpsInfoUtil;
    private KnBulkOpsNotifyUtil bulkOpsNotifyUtil;
    private KnProvSMSUtil provSMSUtil;
    private KnCorpCommonInfoUtil corpCommonInfoUtil;
    private KnGenInfoUtil genInfoUtil;
    private IXcapDiffNotifierIntf notifier;
    private KnProvInfoUtil provInfoUtil;

    public KnBulkOpsUpdateSubscProcessor() {
        bulkOpsInfoUtil = KnBulkOpsInfoUtil.getInstance();
        bulkOpsNotifyUtil = KnBulkOpsNotifyUtil.getInstance();
        provSMSUtil = new KnProvSMSUtil();
        corpCommonInfoUtil = new KnCorpCommonInfoUtil();
        genInfoUtil = KnGenInfoUtil.getInstance();
        notifier = new KnXcapDiffNotifierImpl();
        provInfoUtil = new KnProvInfoUtil();
    }

    public static synchronized KnBulkOpsUpdateSubscProcessor getInstance() {
        if (instance == null) {
            instance = new KnBulkOpsUpdateSubscProcessor();
        }
        return instance;
    }

    /**
     * Updates multiple subscribers in bulk within a single database transaction.
     *
     * Available fields in KnXDMBulkSubsProvInfoDTO for this operation:
     * - subscriberList: List of KnBulkSubscriberEntry (mdn, networkName)
     * - pkgIdMap: Package ID mappings
     * - extCorpId: External corporate ID
     * - transactionId: Transaction tracking ID
     * - corporateName: Corporate name
     * - accountId: Account ID
     *
     * Flow:
     * 1. VALIDATION PHASE - Validate input DTO, subscriber list not empty
     * 2. DATA RETRIEVAL PHASE - Fetch existing subscriber profiles in bulk
     * 3. TRANSACTION PHASE - Open transaction, perform bulk update, commit/rollback
     * 4. NOTIFICATION PHASE - TODO: Will be implemented later
     * 5. RESPONSE PHASE - Build and return response with success/failure per MDN
     *
     * @param requestDTO The bulk subscriber update request DTO
     * @return IXDMResponseDTO containing results for each MDN (success/failure)
     */
    public IXDMResponseDTO updateBulkSubscriber(IXDMRequestDTO requestDTO) {
        String methodName = "updateBulkSubscriber(IXDMRequestDTO)";
        knLogger.info(methodName, "ENTRY: Received Request DTO - ", requestDTO);

        long startTime = System.currentTimeMillis();
        KnPersisterTxn persisterTxn = null;
        KnXDMBulkSubsProvInfoDTO bulkSubsProvInfoDTO = null;
        IXDMResponseDTO responseDTO = null;

        try {
            // ==================== VALIDATION PHASE ====================

            // 1. Validate input DTO type
            if (requestDTO instanceof KnXDMBulkSubsProvInfoDTO) {
                bulkSubsProvInfoDTO = (KnXDMBulkSubsProvInfoDTO) requestDTO;
                knLogger.debug(methodName, "received DTO for update Bulk Subscriber - ", bulkSubsProvInfoDTO);
            } else {
                knLogger.error(methodName, "received an Invalid DTO for update Bulk subscriber op - ", requestDTO);
                responseDTO = new KnXDMBulkOpsRespDTO();
                responseDTO.setResponseMessage("Invalid DTO is passed");
                responseDTO.setResponseCode(KnBulkOpsErrorCodes.BOEntity.ERROR_CODE_INVALID_DTO_PASSED);
                responseDTO.setResponseStatus(KnBulkOpsConstants.RESPONSE_STATUS.FAILURE.value());
                //TODO: Add Statistics for BulkUpdate invalid DTO
                KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_BULK_SUBSCR_UPDATE_FAILURE);
                return responseDTO;
            }

            // 2. Validate subscriber list is not empty
            if (bulkSubsProvInfoDTO.getSubscriberList() == null || bulkSubsProvInfoDTO.getSubscriberList().isEmpty()) {
                knLogger.error(methodName, "Subscriber list is null or empty");
                responseDTO = new KnXDMBulkOpsRespDTO();
                responseDTO.setResponseMessage("Subscriber list is empty");
                responseDTO.setResponseCode(KnBulkOpsErrorCodes.BOEntity.BULK_SUBSCRIBER_VALIDATION_FAILED);
                responseDTO.setResponseStatus(KnBulkOpsConstants.RESPONSE_STATUS.FAILURE.value());
                //TODO: Add Statistics for BulkUpdate invalid DTO
                KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_BULK_SUBSCR_UPDATE_FAILURE);
                return responseDTO;
            }

            // Log the available fields for this bulk operation
            knLogger.info(methodName, "Bulk Update - extCorpId: ", bulkSubsProvInfoDTO.getExtCorpId(),
                    ", transactionId: ", bulkSubsProvInfoDTO.getTransactionId(),
                    ", corporateName: ", bulkSubsProvInfoDTO.getCorporateName(),
                    ", accountId: ", bulkSubsProvInfoDTO.getAccountId(),
                    ", pkgIdMap: ", bulkSubsProvInfoDTO.getPkgIdMap(),
                    ", nameChangeAllowed: ", bulkSubsProvInfoDTO.getNameChangeAllowed(),
                    ", subscriberCount: ", bulkSubsProvInfoDTO.getSubscriberList().size());

            // Initialize DAO
            KnBulkOpsXDMServerDAO bulkOpsXDMServerDAO = new KnBulkOpsXDMServerDAO();

            // Initialize partial response for tracking individual MDN results
            KnXDMBulkOpsRespDTO partialRespDTO = bulkOpsInfoUtil.buildPartialResponse(bulkSubsProvInfoDTO);

            // Extract MDN list for data retrieval (filters out null/empty MDNs)
            List<String> validMdnList = bulkOpsInfoUtil.fetchMDNFromSubscriberList(bulkSubsProvInfoDTO.getSubscriberList());
            int originalMdnCount = validMdnList.size();
            partialRespDTO.setTotalCount(originalMdnCount);
            knLogger.info(methodName, "MDNs for update: ", originalMdnCount);

            // Build MDN to input NetworkName map only if nameChangeAllowed is true
            // This will be refined later after fetching existing profiles to detect actual changes
            boolean nameChangeAllowed = Boolean.TRUE.equals(bulkSubsProvInfoDTO.getNameChangeAllowed());
            Map<String, String> inputNetworkNameMap = new HashMap<>();
            if (nameChangeAllowed) {
                inputNetworkNameMap = buildInputNetworkNameMap(bulkSubsProvInfoDTO.getSubscriberList());
            } else {
                knLogger.info(methodName, "nameChangeAllowed is false, skipping networkName comparison");
            }

            // Get XDM PTT Server ID for FeatureSet recalculation
            String xdmPttServerId = KnBulkOpsDBUtil.getXdmPttServerId();

            // ==================== PRE-FETCH UTILITY DATA (BEFORE OPENING MAIN TRANSACTION) ====================
            // These utility methods (featureSetUtil, genInfoUtil) have internal save() calls
            // that release the DB connection. We must call them BEFORE opening the main transaction
            // to avoid connection null issues during bulk operations.

            KnBulkOpsXDMServerDAO.KnPreFetchedData preFetchedData = new KnBulkOpsXDMServerDAO.KnPreFetchedData();

            // Pre-fetch QPP package codes and data package mappings (these utils have internal save)
            if (bulkSubsProvInfoDTO.getPkgIdMap() != null && !bulkSubsProvInfoDTO.getPkgIdMap().isEmpty()) {
                try {
                    // Pre-fetch QPP package codes (featureSetUtil.getQPPPkgCodes has internal save)
                    KnFeatureSetUtil featureSetUtil = KnFeatureSetUtil.getInstance();
                    List<String> qppPkgCodes = featureSetUtil.getQPPPkgCodes(xdmPttServerId);
                    preFetchedData.setQppPkgCodes(qppPkgCodes);
                    knLogger.debug(methodName, "Pre-fetched QPP package codes: ", qppPkgCodes);

                    // Pre-fetch data package mappings using genInfoUtil (also has internal save)
                    // genInfoUtil.getDataPkgId() releases the connection via internal save()
                    com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil genInfoUtil =
                            com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil.getInstance();

                    // Open a temporary transaction for pre-fetching (will be released by util)
                    KnPersisterTxn preFetchTxn = KnPersisterTxn.getPersisterTxn();
                    preFetchTxn.open();

                    Map<Integer, Integer> qppDataPkgMap = genInfoUtil.getDataPkgId(
                            com.kodiak.xdms.server.common.resources.KnConstants.QPP_DATA_PKG_TYPE, preFetchTxn);
                    preFetchedData.setQppDataPkgMap(qppDataPkgMap);

                    // Re-open for next call (previous call released connection)
                    preFetchTxn = KnPersisterTxn.getPersisterTxn();
                    preFetchTxn.open();

                    Map<Integer, Integer> addonDataPkgMap = genInfoUtil.getDataPkgId(
                            com.kodiak.xdms.server.common.resources.KnConstants.ADDON_DATA_PKG_TYPE, preFetchTxn);
                    preFetchedData.setAddonDataPkgMap(addonDataPkgMap);

                    knLogger.debug(methodName, "Pre-fetched data package mappings - QPP: ",
                                   (qppDataPkgMap != null ? qppDataPkgMap.size() : 0),
                                   ", Addon: ", (addonDataPkgMap != null ? addonDataPkgMap.size() : 0));

                } catch (KnFeatureSetException e) {
                    knLogger.warn(methodName, "Failed to pre-fetch package data, continuing without: ", e.getMessage());
                    preFetchedData.setQppPkgCodes(new ArrayList<>());
                } catch (Exception e) {
                    knLogger.warn(methodName, "Failed to pre-fetch data package maps: ", e.getMessage());
                    // Continue without - will use defaults
                }
            }

            knLogger.info(methodName, "Pre-fetch completed. Now opening main transaction for bulk operations.");

            // ==================== OPEN MAIN TRANSACTION ====================
            // Open transaction AFTER all utility-based data fetching is complete
            // This transaction will be used consistently for all bulk DB operations
            persisterTxn = KnPersisterTxn.getPersisterTxn();
            knLogger.debug(methodName, "Opening the transaction");
            persisterTxn.open();

            // ==================== DATA RETRIEVAL PHASE (WITHIN MAIN TRANSACTION) ====================

            // 4. Fetch existing subscriber profiles in bulk for all MDNs
            //    This validates that all MDNs exist in the system
            //    Uses the main transaction opened above for consistency
            Map<String, KnBulkSubsProfileDTO> existingProfilesMap =
                bulkOpsXDMServerDAO.fetchSubscriberProfilesInBulk(validMdnList, persisterTxn);

            if (existingProfilesMap == null || existingProfilesMap.isEmpty()) {
                knLogger.error(methodName, "No existing subscriber profiles found for the provided MDNs");
                KnDbUtil.rollback(persisterTxn);
                // Add all MDNs as failures since they don't exist
                for (String mdn : validMdnList) {
                    partialRespDTO.addFailure(mdn, KnBulkOpsErrorCodes.BOEntity.BULK_SUBSCRIBER_VALIDATION_FAILED,
                            "Subscriber not found: " + mdn);
                }
                bulkOpsInfoUtil.finalizePartialResponse(partialRespDTO, originalMdnCount, KnBulkOpsConstants.BulkOperations.UPDATEBULKSUBSCRIBER);
                partialRespDTO.setProcessingTimeMs(System.currentTimeMillis() - startTime);
                KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_BULK_SUBSCR_UPDATE_FAILURE);
                responseDTO = partialRespDTO;
                return responseDTO;
            }
            KnBulkSubsProfileDTO firstExistingProfile = existingProfilesMap.values().iterator().next();
            partialRespDTO.setCorpId(firstExistingProfile.getCorpId());

            // 5. Validate MDNs exist and mark non-existing ones as failures
            knLogger.info(methodName," Validating existing subscriber profiles for MDNs", existingProfilesMap.keySet());
            List<String> mdnsToUpdate = new ArrayList<>();
            for (String mdn : validMdnList) {
                if (existingProfilesMap.containsKey(mdn)) {
                    mdnsToUpdate.add(mdn);
                } else {
                    partialRespDTO.addFailure(mdn, KnBulkOpsErrorCodes.BOEntity.BULK_SUBSCRIBER_VALIDATION_FAILED,
                            "Subscriber not found: " + mdn);
                }
            }
            knLogger.info(methodName, "MDNs to be updated after validation: ", mdnsToUpdate);

            // 5a. Build MDN to NetworkName map by comparing input with existing DB values
            //     Only include MDNs where networkName is ACTUALLY DIFFERENT from existing value
            Map<String, String> mdnNetworkNameMap = buildMdnNetworkNameMap(inputNetworkNameMap, existingProfilesMap);
            knLogger.info(methodName, "MDNs with actual networkName changes: ", mdnNetworkNameMap.size());

            // ==================== BUSINESS VALIDATIONS (Aligned with Single MDN flow) ====================

            // 6. Validate Account ID cannot change (Single MDN: line 2289-2293)
            //    If accountId is provided in request, it must match existing DB value
            String requestAccountId = bulkSubsProvInfoDTO.getAccountId();
            if (requestAccountId != null && !requestAccountId.trim().isEmpty()) {
                List<String> accountIdMismatchMdns = validateAccountIdNotChanged(
                        requestAccountId.trim(), existingProfilesMap, mdnsToUpdate);
                if (!accountIdMismatchMdns.isEmpty()) {
                    knLogger.error(methodName, "Account ID mismatch for MDNs: ", accountIdMismatchMdns);
                    for (String mdn : accountIdMismatchMdns) {
                        partialRespDTO.addFailure(mdn, KnBulkOpsErrorCodes.BOEntity.ACCOUNT_ID_CHANGE_NOT_ALLOWED,
                                "Account ID change not allowed");
                    }
                    mdnsToUpdate.removeAll(accountIdMismatchMdns);
                }
            }

            // 7. Validate ExtCorpId matches existing corporate associations (Single MDN: line 2438-2511)
            //    If extCorpId is provided, all MDNs must belong to same corporate
            //    Uses KnBulkOpsCorpProfileDAO for corporate profile operations
            KnBulkOpsCorpProfileDAO corpProfileDAO = new KnBulkOpsCorpProfileDAO();
            String requestExtCorpId = bulkSubsProvInfoDTO.getExtCorpId();
            if (requestExtCorpId != null && !requestExtCorpId.trim().isEmpty()) {
                List<String> extCorpIdMismatchMdns = validateExtCorpIdMatches(
                        requestExtCorpId.trim(), existingProfilesMap, mdnsToUpdate, corpProfileDAO, persisterTxn);
                if (!extCorpIdMismatchMdns.isEmpty()) {
                    knLogger.error(methodName, "ExtCorpId mismatch for MDNs: ", extCorpIdMismatchMdns);
                    for (String mdn : extCorpIdMismatchMdns) {
                        partialRespDTO.addFailure(mdn, KnBulkOpsErrorCodes.BOEntity.EXTCORPID_MISMATCH,
                                "ExtCorpId does not match subscriber's corporate association");
                    }
                    mdnsToUpdate.removeAll(extCorpIdMismatchMdns);
                }
            }

            // 8. Validate Client Type Restrictions (Single MDN: line 1927-1938)
            //    PDV client type (11) is not supported
            //    Standalone camera restrictions apply
            List<String> restrictedClientTypeMdns = validateClientTypeRestrictions(existingProfilesMap, mdnsToUpdate);
            if (!restrictedClientTypeMdns.isEmpty()) {
                knLogger.error(methodName, "Restricted client type for MDNs: ", restrictedClientTypeMdns);
                for (String mdn : restrictedClientTypeMdns) {
                    partialRespDTO.addFailure(mdn, KnBulkOpsErrorCodes.BOEntity.RESTRICTED_CLIENT_TYPE,
                            "Client type not supported for bulk update");
                }
                mdnsToUpdate.removeAll(restrictedClientTypeMdns);
            }

            // 9. Detect actual changes - only process MDNs with real changes (Single MDN: line 2893-2896)
            //    Check if networkName or pkgIdMap would cause actual changes
            Map<String, Boolean> mdnChangeMap = detectActualChanges(
                    mdnsToUpdate, mdnNetworkNameMap, bulkSubsProvInfoDTO.getPkgIdMap(), existingProfilesMap);
            List<String> noChangeMdns = new ArrayList<>();
            for (Map.Entry<String, Boolean> entry : mdnChangeMap.entrySet()) {
                if (!entry.getValue()) {
                    noChangeMdns.add(entry.getKey());
                }
            }
            // Note: Unlike Single MDN which throws error for no change, Bulk should still process
            // as other MDNs may have changes. Log warning but don't fail.
            if (!noChangeMdns.isEmpty()) {
                knLogger.warn(methodName, "No changes detected for MDNs (will still update profile time): ", noChangeMdns);
            }

            knLogger.info(methodName, "MDNs to be updated after all validations: ", mdnsToUpdate.size());

            // Filter mdnNetworkNameMap to only include MDNs that passed all validations.
            // mdnNetworkNameMap was built before validation phase (line 306) and may contain
            // entries for MDNs that failed validation (e.g., accountId mismatch, extCorpId mismatch).
            // Without this filter, network names for validation-failed MDNs could still be updated by the DAO.
            if (!mdnNetworkNameMap.isEmpty()) {
                int beforeFilterSize = mdnNetworkNameMap.size();
                mdnNetworkNameMap.keySet().retainAll(mdnsToUpdate);
                if (mdnNetworkNameMap.size() < beforeFilterSize) {
                    knLogger.info(methodName, "Filtered mdnNetworkNameMap from ", beforeFilterSize,
                            " to ", mdnNetworkNameMap.size(), " after removing validation-failed MDNs");
                }
            }

            if (mdnsToUpdate.isEmpty()) {
                knLogger.error(methodName, "No valid MDNs to update after profile verification");
                KnDbUtil.rollback(persisterTxn);
                bulkOpsInfoUtil.finalizePartialResponse(partialRespDTO, originalMdnCount, KnBulkOpsConstants.BulkOperations.UPDATEBULKSUBSCRIBER);
                partialRespDTO.setProcessingTimeMs(System.currentTimeMillis() - startTime);
                KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_BULK_SUBSCR_UPDATE_FAILURE);
                responseDTO = partialRespDTO;
                return responseDTO;
            }

            // ==================== TRANSACTION PHASE (UPDATE OPERATIONS) ====================

            try {
                // 6. Perform bulk update using DAO directly
                //    Pass pre-fetched data to avoid utility calls during transaction
                //    The DAO handles batch operations efficiently:
                //    - MDNs with different networkName: Update SUBSCRNAME + LASTPROFILEUPDATETIME
                //    - MDNs with networkName == MDN: Only update LASTPROFILEUPDATETIME (keep existing)
                //    - pkgIdMap: Bulk update SUBSCRIBER_ADDLINFO for tier/addon packages
                //    - FeatureSet: Recalculate subsFS2 and activeFS2 when pkgIdMap changes
                //
                KnBulkOpsXDMServerDAO.KnBulkUpdateResult bulkUpdateResult = bulkOpsXDMServerDAO.updateSubscribersInBulk(
                        mdnsToUpdate,
                        mdnNetworkNameMap,
                        bulkSubsProvInfoDTO.getPkgIdMap(),
                        existingProfilesMap,
                        xdmPttServerId,
                        preFetchedData,
                        persisterTxn);

                knLogger.debug(methodName, "Bulk update result from DAO - ", bulkUpdateResult);

                // 7. Process update response and populate success/failure for each MDN
                if (bulkUpdateResult != null && bulkUpdateResult.isSuccess()) {
                    // All MDNs in the batch succeeded
                    List<KnBulkSubscriberEntry> successMdns = new ArrayList<>();
                    for (String mdn : mdnsToUpdate) {
                        KnBulkSubscriberEntry successEntry = new KnBulkSubscriberEntry();
                        successEntry.setMdn(mdn);
                        successMdns.add(successEntry);
                    }
                    partialRespDTO.setSuccessMdns(successMdns);

                    // ==================== POST-UPDATE OPERATIONS (Aligned with Single MDN) ====================

                    // 8. Update Corporate Profile if corporateName is provided (Single MDN: line 3044)
                    //    Uses KnBulkOpsCorpProfileDAO for corporate profile operations
                    String requestCorporateName = bulkSubsProvInfoDTO.getCorporateName();
                    if (requestCorporateName != null && !requestCorporateName.trim().isEmpty()) {
                        updateCorporateProfileIfNeeded(existingProfilesMap, mdnsToUpdate,
                                requestCorporateName.trim(), corpProfileDAO, persisterTxn);
                    }

                    // 9. Update XDM Directory ETags for all updated MDNs (Single MDN: line 3614-3615)
                    // Uses KnXDMDirectoryDAO.updateEtagForDirDocOfMdnList - same approach as KnBulkOpsUpdateAuthStatusProcessor
                    // Capture previous ETags for XCAP notification before update
                    Map<String, Integer> mdnToEtagMap = new HashMap<>();
                    try {
                        KnXDMDirectoryDAO knXDMDirectoryDAO = new KnXDMDirectoryDAO();
                        // Get current ETags before update (for XCAP notification)
                        mdnToEtagMap = knXDMDirectoryDAO.getCurrentEtagsForDirDoc(mdnsToUpdate, persisterTxn);
                        // Update ETags
                        knXDMDirectoryDAO.updateEtagForDirDocOfMdnList(mdnsToUpdate, persisterTxn);
                        knLogger.info(methodName, "Directory ETags updated for ", mdnsToUpdate.size(), " MDNs");
                    } catch (Exception e) {
                        knLogger.warn(methodName, "Failed to update directory ETags (non-critical): ", e.getMessage());
                        // Non-critical - continue with commit
                    }

                    // 10. Pre-fetch XCAP Root URIs for notifications BEFORE committing transaction
                    // This is needed because sendNotifications runs after commit and cannot use the same transaction
                    Map<String, String> xcapRootUriMap = new HashMap<>();
                    try {
                        xcapRootUriMap = genInfoUtil.getXCAPRootURI(mdnsToUpdate, persisterTxn, true);
                        knLogger.debug(methodName, "Pre-fetched XCAP Root URIs for ", xcapRootUriMap.size(), " MDNs");
                    } catch (Exception e) {
                        knLogger.warn(methodName, "Failed to pre-fetch XCAP Root URIs (non-critical): ", e.getMessage());
                        // Non-critical - notifications can proceed without XCAP Root URIs
                    }

                    // 11. Pre-fetch xcapMobileSync flag BEFORE committing transaction
                    // This flag is needed for MODIFY_SUBSCRIBER and FEATURE_BIT_CHANGE notifications
                    boolean xcapMobileSync = false;
                    try {
                        xcapMobileSync = bulkOpsInfoUtil.getXcapMobileSyncFlag(persisterTxn);
                        knLogger.debug(methodName, "Pre-fetched xcapMobileSync flag: ", xcapMobileSync);
                    } catch (Exception e) {
                        knLogger.warn(methodName, "Failed to get xcapMobileSync flag: ", e.getMessage());
                    }

                    // Commit the transaction
                    knLogger.debug(methodName, "Saving the transaction");
                    persisterTxn.save();

                    // Update success statistics
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_BULK_SUBSCR_UPDATED);

                    knLogger.info(methodName, "Bulk update successful - NetworkName updates: ",
                                 bulkUpdateResult.getNetworkNameUpdatedCount(),
                                 ", ProfileTime updates: ", bulkUpdateResult.getProfileTimeUpdatedCount(),
                                 ", PkgIdMap processed: ", bulkUpdateResult.isPkgIdMapProcessed(),
                                 ", FeatureSet updated: ", bulkUpdateResult.isFeatureSetUpdated());

                    // ==================== NOTIFICATION PHASE ====================
                    // Send notifications after successful commit (aligned with Single MDN and other bulk operations)
                    // Note: xcapRootUriMap and xcapMobileSync were pre-fetched above before commit since transaction is closed after save()
                    sendNotifications(mdnsToUpdate, existingProfilesMap, bulkUpdateResult, mdnToEtagMap, 
                            mdnNetworkNameMap, xcapRootUriMap, xcapMobileSync);

                } else {
                    // Bulk update failed - rollback and mark all as failed
                    knLogger.error(methodName, "Bulk update failed, rolling back transaction");
                    KnDbUtil.rollback(persisterTxn);

                    String errorCode = KnBulkOpsErrorCodes.Validator.ERROR_CODE_INTERNAL_ERROR;
                    String errorMsg = bulkUpdateResult != null ? bulkUpdateResult.getErrorMessage() : "Bulk subscriber update failed";

                    for (String mdn : mdnsToUpdate) {
                        partialRespDTO.addFailure(mdn, errorCode, errorMsg);
                    }

                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_BULK_SUBSCR_UPDATE_FAILURE);
                }

            } catch (Exception txnException) {
                knLogger.error(methodName, "Exception during transaction, rolling back", txnException);
                KnDbUtil.rollback(persisterTxn);

                // Extract clean error code and message without full stack trace
                String txnErrorCode = KnBulkOpsErrorCodes.Validator.ERROR_CODE_INTERNAL_ERROR;
                String txnErrorMsg = "Transaction failed";
                if (txnException instanceof com.kodiak.common.exception.KnException) {
                    com.kodiak.common.exception.KnException knEx = (com.kodiak.common.exception.KnException) txnException;
                    txnErrorCode = knEx.getErrorCode() != null ? knEx.getErrorCode() : txnErrorCode;
                    txnErrorMsg = knEx.getErrorMessage() != null ? knEx.getErrorMessage() : txnErrorMsg;
                }

                // Mark all MDNs in transaction as failed
                for (String mdn : mdnsToUpdate) {
                    partialRespDTO.addFailure(mdn, txnErrorCode, txnErrorMsg);
                }

                KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_BULK_SUBSCR_UPDATE_FAILURE);
            }

            // ==================== RESPONSE PHASE ====================

            // 8. Finalize and return response
            bulkOpsInfoUtil.finalizePartialResponse(partialRespDTO, originalMdnCount, KnBulkOpsConstants.BulkOperations.UPDATEBULKSUBSCRIBER);
            partialRespDTO.setProcessingTimeMs(System.currentTimeMillis() - startTime);
            responseDTO = partialRespDTO;

            knLogger.info(methodName, "EXIT: Bulk update completed - Success: ", partialRespDTO.getSuccessMdnCount(),
                    ", Failure: ", partialRespDTO.getFailureMdnCount(),
                    ", ProcessingTime: ", partialRespDTO.getProcessingTimeMs(), "ms");
        } catch (KnBulkOpsException e) {
            knLogger.error(methodName, "Bulk operations exception occurred ", e);
            KnDbUtil.rollback(persisterTxn);
            KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_BULK_SUBSCR_UPDATE_FAILURE);
            responseDTO = bulkOpsInfoUtil.buildFailureResponse(bulkSubsProvInfoDTO, e, startTime);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO exception occurred ", e);
            KnDbUtil.rollback(persisterTxn);
            KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_BULK_SUBSCR_UPDATE_FAILURE);
            responseDTO = bulkOpsInfoUtil.buildFailureResponse(bulkSubsProvInfoDTO, e, startTime);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected exception occurred ", e);
            KnDbUtil.rollback(persisterTxn);
            KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_BULK_SUBSCR_UPDATE_FAILURE);
            responseDTO = bulkOpsInfoUtil.buildFailureResponse(bulkSubsProvInfoDTO, e, startTime);
        }

        return responseDTO;
    }

    /**
     * Sends notifications for successfully updated subscribers.
     * Aligned with other bulk operations (KnBulkOpsUpdateAuthStatusProcessor, KnBulkOpsDeleteSubscProcessor).
     *
     * Notifications sent:
     * 1. Microservice notification (XCAP Mobile Sync) - When xcapMobileSync flag is enabled,
     *    sends KnSubscrEXDMSNotifyDto with event type MODIFY_SUBSCRIBER for each updated MDN.
     * 2. SMS notification - When client type is upgraded (via USER_PROFILE_MGMT_BIT from pkgIdMap),
     *    sends welcome SMS for PTT Radio clients (aligned with Single MDN flow).
     * 3. Corp MCS notification (LMR Interop) - When pkgIdMap enables INTEROPFEATURE or DATA_INTER_OP bit
     *    in subsFS2 and UGWINTEROP is not yet enabled for the corp (aligned with Single MDN flow).
     * 4. XCAP Diff notification with Profile Notification - Notifies clients about directory document changes.
     *    When networkName or activeFS2 changed, includes ProfileNotifyDTO to tell client what specifically changed
     *    (aligned with Single MDN flow KnXDMMediator.updateSubscriber).
     *
     * @param updatedMdns List of successfully updated MDNs
     * @param existingProfilesMap Map of MDN to existing profile data (used for notification payload)
     * @param bulkUpdateResult Result from bulk update containing updated FeatureSet values and client type upgrades
     * @param mdnToEtagMap Map of MDN to previous directory ETags (before update)
     * @param mdnNetworkNameMap Map of MDN to new networkName (to detect networkName changes)
     * @param xcapRootUriMap Map of MDN to XCAP Root URI (pre-fetched before commit)
     * @param xcapMobileSync Pre-fetched xcapMobileSync flag (fetched before commit)
     */
    private void sendNotifications(List<String> updatedMdns,
                                   Map<String, KnBulkSubsProfileDTO> existingProfilesMap,
                                   KnBulkOpsXDMServerDAO.KnBulkUpdateResult bulkUpdateResult,
                                   Map<String, Integer> mdnToEtagMap,
                                   Map<String, String> mdnNetworkNameMap,
                                   Map<String, String> xcapRootUriMap,
                                   boolean xcapMobileSync) {
        String methodName = "sendNotifications";
        knLogger.info(methodName, "ENTRY: Sending notifications for ", updatedMdns.size(), " updated MDNs");

        try {
            knLogger.debug(methodName, "xcapMobileSync flag: ", xcapMobileSync);

            // Build notification DTOs for each updated MDN (aligned with KnBulkOpsDeleteSubscProcessor pattern)
            List<KnSubscrEXDMSNotifyDto> notifyDtoList = new ArrayList<>();
            
            // XCAP Diff notification list (aligned with KnBulkOpsUpdateAuthStatusProcessor)
            List<KnXcapDiffNotifyDTO> xcapDiffList = new ArrayList<>();

            // Get updated FeatureSets from bulk update result (if any)
            Map<String, String> updatedSubsFS2Map = bulkUpdateResult != null ? bulkUpdateResult.getUpdatedSubsFS2Map() : null;
            Map<String, String> updatedActiveFS2Map = bulkUpdateResult != null ? bulkUpdateResult.getUpdatedActiveFS2Map() : null;
            
            // Get client type upgrades for SMS notifications
            Map<String, Integer> clientTypeUpgrades = bulkUpdateResult != null ? bulkUpdateResult.getClientTypeUpgrades() : null;
            
            // Track corpIds that need LMR Interop notification (to avoid duplicate notifications per corp)
            Set<Integer> corpIdsForLmrNotify = new HashSet<>();
            
            // Track MDNs with profile-level changes for Profile Notification (aligned with Single MDN)
            // isActiveFSChanged: when activeFS2 changes (from pkgIdMap)
            // isSubsNameChanged: when networkName changes
            Map<String, Boolean> mdnActiveFSChanged = new HashMap<>();
            Map<String, Boolean> mdnNameChanged = new HashMap<>();
            
            // Detect which MDNs had networkName changes
            // mdnNetworkNameMap already contains only MDNs with actual changes (compared against DB values)
            if (mdnNetworkNameMap != null && !mdnNetworkNameMap.isEmpty()) {
                for (String mdn : updatedMdns) {
                    if (mdnNetworkNameMap.containsKey(mdn)) {
                        mdnNameChanged.put(mdn, true);
                        knLogger.debug(methodName, "NetworkName changed for MDN ", mdn);
                    }
                }
            }
            
            // Detect which MDNs had activeFS2 changes (from pkgIdMap processing)
            if (updatedActiveFS2Map != null) {
                for (String mdn : updatedMdns) {
                    KnBulkSubsProfileDTO profile = existingProfilesMap.get(mdn);
                    if (profile != null && updatedActiveFS2Map.containsKey(mdn)) {
                        String newActiveFS2 = updatedActiveFS2Map.get(mdn);
                        String oldActiveFS2 = profile.getActiveFS2();
                        if (newActiveFS2 != null && !newActiveFS2.equals(oldActiveFS2)) {
                            mdnActiveFSChanged.put(mdn, true);
                            knLogger.debug(methodName, "ActiveFS2 changed for MDN ", mdn);
                        }
                    }
                }
            }
            
            knLogger.info(methodName, "Profile changes detected - NetworkName changed: ", mdnNameChanged.size(),
                    ", ActiveFS changed: ", mdnActiveFSChanged.size());
            
            // Current profile update time for doc ETag
            long lastProfileUpdateTime = System.currentTimeMillis();

            for (String mdn : updatedMdns) {
                KnBulkSubsProfileDTO profile = existingProfilesMap.get(mdn);
                if (profile == null) {
                    knLogger.warn(methodName, "No profile found for MDN ", mdn, ", skipping notification");
                    continue;
                }
                
                // Determine if this MDN had actual changes (for conditional notifications)
                boolean isActiveFSChanged = mdnActiveFSChanged.getOrDefault(mdn, false);
                boolean isNameChanged = mdnNameChanged.getOrDefault(mdn, false);
                boolean isClientTypeUpgraded = (clientTypeUpgrades != null && clientTypeUpgrades.containsKey(mdn));
                boolean hasActualChanges = isActiveFSChanged || isNameChanged || isClientTypeUpgraded;

                // MODIFY_SUBSCRIBER notification - only send when actual changes occurred
                // Aligned with Single MDN which sends this when subscriber profile is modified
                if (xcapMobileSync && hasActualChanges) {
                    KnSubscrEXDMSNotifyDto knSubscrEXDMSNotifyDto = new KnSubscrEXDMSNotifyDto();
                    
                    // Set standard fields (aligned with KnBulkOpsDeleteSubscProcessor)
                    knSubscrEXDMSNotifyDto.setCorpid(profile.getCorpId());
                    knSubscrEXDMSNotifyDto.setMdn(mdn);
                    knSubscrEXDMSNotifyDto.setId(MODIFY_SUBSCRIBER.value() + com.kodiak.xdms.server.common.resources.KnConstants.LINE_SAPERATOR + mdn);
                    knSubscrEXDMSNotifyDto.setType(MODIFY_SUBSCRIBER.value());
                    knSubscrEXDMSNotifyDto.setVer(MICROSERVICE_NOTIFY_DOC_VER);
                    knSubscrEXDMSNotifyDto.setNotifyEventType(com.kodiak.common.resources.KnConstants.MICROSERVICES_NOTIFY_EVENT_TYPE.USER_NOTIFY_EVENTS.value());
                    // Use upgraded client type if available, otherwise use existing
                    Integer clientType = isClientTypeUpgraded
                            ? clientTypeUpgrades.get(mdn) : profile.getSubsClientType();
                    knSubscrEXDMSNotifyDto.setClientType(clientType);
                    knSubscrEXDMSNotifyDto.setLastProfileUpdateTime(lastProfileUpdateTime);
                    knSubscrEXDMSNotifyDto.setBaseMdn(mdn);
                    knSubscrEXDMSNotifyDto.setMcpttId(profile.getMcpttId());

                    knSubscrEXDMSNotifyDto.setPocPttId(profile.getPocHome());
                    // Set FeatureSets - use updated values if available, otherwise use existing
                    String subsFS2 = (updatedSubsFS2Map != null && updatedSubsFS2Map.containsKey(mdn))
                            ? updatedSubsFS2Map.get(mdn) : profile.getSubsFS2();
                    knSubscrEXDMSNotifyDto.setSubsFS2(subsFS2);
                    knSubscrEXDMSNotifyDto.setMcId(profile.getMcId());
                    knSubscrEXDMSNotifyDto.setMcdataId(null); // mcDataId not available in KnBulkSubsProfileDTO
                    knSubscrEXDMSNotifyDto.setMcvideoId(null); // mcVideoId not available in KnBulkSubsProfileDTO
                    String notifyNetworkName = (mdnNetworkNameMap != null && mdnNetworkNameMap.containsKey(mdn))
                            ? mdnNetworkNameMap.get(mdn) : profile.getNetworkName();
                    knSubscrEXDMSNotifyDto.setNetworkName(notifyNetworkName);
                    knSubscrEXDMSNotifyDto.setDeviceId(mdn);
                    
                    notifyDtoList.add(knSubscrEXDMSNotifyDto);
                    knLogger.debug(methodName, "Added MODIFY_SUBSCRIBER notification for MDN ", mdn, 
                            " (activeFSChanged=", isActiveFSChanged, ", nameChanged=", isNameChanged, 
                            ", clientTypeUpgraded=", isClientTypeUpgraded, ")");
                }
                
                // Check if this MDN's updated subsFS2 requires Corp LMR Interop notification
                // Only check if FeatureSet was actually updated (pkgIdMap was processed)
                if (updatedSubsFS2Map != null && updatedSubsFS2Map.containsKey(mdn)) {
                    int corpId = profile.getCorpId();
                    if (corpId > 0 && !corpIdsForLmrNotify.contains(corpId)) {
                        corpIdsForLmrNotify.add(corpId);
                    }
                }
                
                // Build XCAP Diff notification for this MDN (aligned with KnBulkOpsUpdateAuthStatusProcessor)
                // Skip NNI Alias and Group MDN types as they don't need XCAP notifications
                // Only send when actual changes occurred (networkName or activeFS2 changed)
                if (profile.getSubsClientType() != KnBulkOpsConstants.SUBSCRIBERS_CLIENT_TYPE.POC_NNI_Alias_MDN.value() &&
                        profile.getSubsClientType() != KnBulkOpsConstants.SUBSCRIBERS_CLIENT_TYPE.POC_NNI_Group_MDN.value() &&
                        hasActualChanges) {
                    
                    // Use pre-fetched xcapRootUri from xcapRootUriMap (fetched before transaction commit)
                    String xcapRootUri = xcapRootUriMap != null ? xcapRootUriMap.get(mdn) : null;
                    KnXcapDiffNotifyDTO xcapDiffNotifyDTO = buildXcapDiffNotifyDTO(mdn, profile, mdnToEtagMap, 
                            lastProfileUpdateTime, isActiveFSChanged, isNameChanged, xcapRootUri);
                    if (xcapDiffNotifyDTO != null) {
                        xcapDiffList.add(xcapDiffNotifyDTO);
                    }
                }
            }

            // Send MODIFY_SUBSCRIBER notifications (only for MDNs with actual changes)
            if (!notifyDtoList.isEmpty()) {
                knLogger.info(methodName, "Publishing MODIFY_SUBSCRIBER notifications for ", notifyDtoList.size(), 
                        " MDNs with changes (out of ", updatedMdns.size(), " total updated)");
                try {
                    bulkOpsNotifyUtil.startNotifyMicroServicesJob(notifyDtoList);
                } catch (Exception e) {
                    knLogger.warn(methodName, "Failed to send MODIFY_SUBSCRIBER notifications: ", e.getMessage());
                }
            } else {
                knLogger.debug(methodName, "No MDNs had changes requiring MODIFY_SUBSCRIBER notification");
            }
            
            // Send XCAP Diff notifications (aligned with KnBulkOpsUpdateAuthStatusProcessor)
            if (!xcapDiffList.isEmpty()) {
                knLogger.info(methodName, "Sending XCAP Diff notifications for ", xcapDiffList.size(), " MDNs");
                KnNotificationParamDTO knNotificationParamDTO = new KnNotificationParamDTO();
                knNotificationParamDTO.setPriority(com.kodiak.xdms.server.common.resources.KnConstants.NOTIFICATION_PRIORITY.HIGH.value());
                boolean isNotified = notifier.sendXcapDiffNotifications(xcapDiffList, knNotificationParamDTO);
                knLogger.debug(methodName, "XCAP Diff notification status: ", isNotified);
            }
            
            // Send Corp MCS notification for LMR Interop if needed (aligned with Single MDN flow)
            // When pkgIdMap enables INTEROPFEATURE or DATA_INTER_OP bit in subsFS2
            // Note: Opens a new transaction internally since main transaction is committed
            sendCorpLmrInteropNotifications(corpIdsForLmrNotify, updatedSubsFS2Map, existingProfilesMap);
            
            // Send FEATURE_BIT_CHANGE notification for MDNs with activeFS2 changes (aligned with Single MDN flow)
            // This tells clients their features have changed and they need to refresh capabilities
            // Single MDN: KnXDMMediator.updateSubscriber lines 1023-1077
            sendFeatureBitChangeNotifications(mdnActiveFSChanged, updatedActiveFS2Map, existingProfilesMap, 
                    lastProfileUpdateTime, xcapMobileSync);
            
            // Send SMS notifications for client type upgrades (aligned with Single MDN flow)
            // Single MDN sends welcome SMS when client type changes to PTT Radio types
            // Note: Opens a new transaction internally since main transaction is committed
            sendSMSNotificationsForClientTypeUpgrades(clientTypeUpgrades);
            
            // Send Profile MDN notifications (aligned with Single MDN and KnBulkOpsUpdateAuthStatusProcessor)
            // When base MDN is updated and has profile MDNs linked (UPM feature), those profile MDNs need to be notified
            // Note: Opens a new transaction internally since main transaction is committed
            // This is triggered when pkgIdMap causes subsFS2/activeFS2 changes
            sendProfileMdnNotifications(updatedMdns, mdnActiveFSChanged, existingProfilesMap);

        } catch (Exception e) {
            // Notification failure should not fail the bulk update operation
            knLogger.warn(methodName, "Failed to send notifications (non-critical): ", e.getMessage());
            knLogger.debug(methodName, "Notification exception details: ", e);
        }

        knLogger.info(methodName, "EXIT: Notification phase completed");
    }
    
    /**
     * Sends FEATURE_BIT_CHANGE microservice notifications for MDNs that had activeFS2 changes.
     * Aligned with Single MDN flow (KnXDMMediator.updateSubscriber lines 1023-1077):
     * - When activeFS changes, send FEATURE_BIT_CHANGE event to notify clients
     * - Only sent for clients with XCAPCOUCHCLIENT bit enabled OR PV >= 18
     *
     * This is critical for package changes to take effect - without this notification,
     * clients won't know their features have changed and won't refresh their capabilities.
     *
     * @param mdnActiveFSChanged Map of MDN to boolean indicating if activeFS changed
     * @param updatedActiveFS2Map Map of MDN to new activeFS2 value
     * @param existingProfilesMap Map of MDN to profile data
     * @param lastProfileUpdateTime Profile update timestamp
     * @param xcapMobileSync Whether XCAP Mobile Sync is enabled
     */
    private void sendFeatureBitChangeNotifications(Map<String, Boolean> mdnActiveFSChanged,
                                                    Map<String, String> updatedActiveFS2Map,
                                                    Map<String, KnBulkSubsProfileDTO> existingProfilesMap,
                                                    long lastProfileUpdateTime,
                                                    boolean xcapMobileSync) {
        String methodName = "sendFeatureBitChangeNotifications";
        
        if (mdnActiveFSChanged == null || mdnActiveFSChanged.isEmpty()) {
            knLogger.debug(methodName, "No MDNs with activeFS changes, skipping FEATURE_BIT_CHANGE notification");
            return;
        }
        
        if (!xcapMobileSync) {
            knLogger.debug(methodName, "XCAP Mobile Sync disabled, skipping FEATURE_BIT_CHANGE notification");
            return;
        }
        
        knLogger.info(methodName, "Sending FEATURE_BIT_CHANGE notification for ", mdnActiveFSChanged.size(), " MDNs");
        
        List<KnSubscrEXDMSNotifyDto> featureBitChangeList = new ArrayList<>();
        
        for (Map.Entry<String, Boolean> entry : mdnActiveFSChanged.entrySet()) {
            String mdn = entry.getKey();
            Boolean isChanged = entry.getValue();
            
            if (isChanged == null || !isChanged) {
                continue;
            }
            
            KnBulkSubsProfileDTO profile = existingProfilesMap.get(mdn);
            if (profile == null) {
                knLogger.warn(methodName, "No profile found for MDN ", mdn, ", skipping");
                continue;
            }
            
            // Get updated activeFS2 value
            String newActiveFS2 = updatedActiveFS2Map != null ? updatedActiveFS2Map.get(mdn) : null;
            if (newActiveFS2 == null) {
                knLogger.warn(methodName, "No updated activeFS2 for MDN ", mdn, ", skipping");
                continue;
            }
            
            // Check if client supports FEATURE_BIT_CHANGE notification
            // Condition: xcapMobileSync AND (XCAPCOUCHCLIENT bit OR PV >= 18)
            boolean xcapCouchClientBit = KnGeneralUtil.getFeatureBitValue(newActiveFS2, FEATURE_SET.XCAPCOUCHCLIENT.value());
            int clientPVmajorVer = profile.getClientPVmajorVer();
            
            if (!(xcapCouchClientBit || clientPVmajorVer >= PROTOCOL_VERSION_18)) {
                knLogger.debug(methodName, "MDN ", mdn, " does not support FEATURE_BIT_CHANGE (xcapCouchClient=", 
                        xcapCouchClientBit, ", PV=", clientPVmajorVer, "), skipping");
                continue;
            }
            
            // Build FEATURE_BIT_CHANGE notification DTO (aligned with Single MDN)
            KnSubscrEXDMSNotifyDto subscrEXDMSNotifyDto = new KnSubscrEXDMSNotifyDto();
            subscrEXDMSNotifyDto.setMdn(mdn);
            subscrEXDMSNotifyDto.setCorpid(profile.getCorpId());
            subscrEXDMSNotifyDto.setActiveFS2(newActiveFS2);
            subscrEXDMSNotifyDto.setOldActiveFS(profile.getActiveFS2()); // Old value from profile
            subscrEXDMSNotifyDto.setPv(clientPVmajorVer + "." + profile.getClientPVminorVer());
            subscrEXDMSNotifyDto.setId(FEATURE_BIT_CHANGE.value() + com.kodiak.xdms.server.common.resources.KnConstants.LINE_SAPERATOR + mdn);
            subscrEXDMSNotifyDto.setType(FEATURE_BIT_CHANGE.value());
            subscrEXDMSNotifyDto.setVer(MICROSERVICE_NOTIFY_DOC_VER);
            subscrEXDMSNotifyDto.setNotifyEventType(com.kodiak.common.resources.KnConstants.MICROSERVICES_NOTIFY_EVENT_TYPE.USER_NOTIFY_EVENTS.value());
            subscrEXDMSNotifyDto.setLastProfileUpdateTime(lastProfileUpdateTime);
            
            featureBitChangeList.add(subscrEXDMSNotifyDto);
            knLogger.debug(methodName, "Added FEATURE_BIT_CHANGE notification for MDN ", mdn);
        }
        
        if (!featureBitChangeList.isEmpty()) {
            knLogger.info(methodName, "Publishing FEATURE_BIT_CHANGE notifications for ", featureBitChangeList.size(), " MDNs");
            try {
                bulkOpsNotifyUtil.startNotifyMicroServicesJob(featureBitChangeList);
                knLogger.debug(methodName, "FEATURE_BIT_CHANGE notifications sent");
            } catch (Exception e) {
                knLogger.warn(methodName, "Failed to send FEATURE_BIT_CHANGE notifications: ", e.getMessage());
            }
        }
    }
    
    /**
     * Builds XCAP Diff notification DTO for a single MDN.
     * Aligned with KnBulkOpsUpdateAuthStatusProcessor and Single MDN flow.
     * 
     * When isActiveFSChanged or isNameChanged is true, includes ProfileNotifyDTO to tell client
     * what specifically changed (aligned with Single MDN KnXDMMediator.updateSubscriber).
     * 
     * @param mdn The MDN to build notification for
     * @param profile The subscriber profile
     * @param mdnToEtagMap Map of MDN to previous directory ETags
     * @param lastProfileUpdateTime Profile update timestamp
     * @param isActiveFSChanged Whether activeFS2 changed for this MDN
     * @param isNameChanged Whether networkName changed for this MDN
     * @param xcapRootUri Pre-fetched XCAP Root URI for this MDN (fetched before transaction commit)
     * @return KnXcapDiffNotifyDTO or null if unable to build
     */
    private KnXcapDiffNotifyDTO buildXcapDiffNotifyDTO(String mdn, 
                                                        KnBulkSubsProfileDTO profile,
                                                        Map<String, Integer> mdnToEtagMap,
                                                        long lastProfileUpdateTime,
                                                        boolean isActiveFSChanged,
                                                        boolean isNameChanged,
                                                        String xcapRootUri) {
        String methodName = "buildXcapDiffNotifyDTO";
        
        try {
            // Build document change DTO (subs-config document)
            Collection<KnOPDocChgDTO> chgDocList = new ArrayList<>();
            KnOPDocChgDTO docChgDTO = new KnOPDocChgDTO();
            docChgDTO.setDocumentChgType(com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REPLACE.value());
            String subsConfigDocUri = provInfoUtil.generateSubsConfigSelUri(mdn);
            docChgDTO.setDocUri(subsConfigDocUri);
            docChgDTO.setNewEtag(String.valueOf(lastProfileUpdateTime));
            chgDocList.add(docChgDTO);
            
            // Build directory change DTO
            KnOPDirChgDTO dirChgDTO = new KnOPDirChgDTO();
            
            // Use pre-fetched XCAP Root URI (fetched before transaction commit)
            dirChgDTO.setXcapRootURI(xcapRootUri);
            
            dirChgDTO.setPocHome(profile.getPocHome());
            dirChgDTO.setPresenceHome(profile.getPresenceHome());
            dirChgDTO.setDocChgDTO(chgDocList);
            
            // Generate directory document URI
            String dirDocUri = genInfoUtil.generateDirDocUri(mdn);
            dirChgDTO.setDirUri(dirDocUri);
            
            // Set ETags (previous and new)
            int previousEtag = mdnToEtagMap != null ? mdnToEtagMap.getOrDefault(mdn, 0) : 0;
            dirChgDTO.setDirPrevEtag(String.valueOf(previousEtag));
            int newEtag = previousEtag + 1;
            dirChgDTO.setDirNewEtag(String.valueOf(newEtag));
            
            String protocolVersion = String.valueOf(profile.getClientPVmajorVer());
            dirChgDTO.setProtoVersion(protocolVersion);
            dirChgDTO.setClientType(profile.getSubsClientType());
            dirChgDTO.setMdn(mdn);
            
            // Build XCAP Diff Notify DTO
            KnXcapDiffNotifyDTO xcapDiffNotifyDTO = new KnXcapDiffNotifyDTO();
            
            // Build Profile Notification if activeFS or networkName changed (aligned with Single MDN flow)
            // Single MDN: KnXDMMediator.updateSubscriber lines 877-895
            // Condition: dirChgDTO != null && PV matches regex && (isActiveFSChanged || isSubsNameChanged || isSubsTypeChanged)
            if (protocolVersion.matches(PROTOCOL_VERSION_REGEX) && (isActiveFSChanged || isNameChanged)) {
                KnProfileNotifyDTO profileNotifyDTO = new KnProfileNotifyDTO();
                
                if (isActiveFSChanged) {
                    profileNotifyDTO.setActiveFeatureSetChange(mdn);
                    knLogger.debug(methodName, "Setting activeFeatureSetChange for MDN ", mdn);
                }
                if (isNameChanged) {
                    profileNotifyDTO.setSubscriberNameChange(mdn);
                    knLogger.debug(methodName, "Setting subscriberNameChange for MDN ", mdn);
                }
                
                profileNotifyDTO.setMdn(mdn);
                profileNotifyDTO.setPocHome(profile.getPocHome());
                profileNotifyDTO.setPresenceHome(profile.getPresenceHome());
                profileNotifyDTO.setAction(com.kodiak.xdms.server.common.resources.KnConstants.MESSAGE_TYPE.SUBSCR_PROFILE_CHANGE.value());
                profileNotifyDTO.setLastProfileUpdateTimestamp(String.valueOf(lastProfileUpdateTime));
                
                // Set profile notify flag and DTO
                xcapDiffNotifyDTO.setProfileNotify(true);
                xcapDiffNotifyDTO.setProfileNotifyDTO(profileNotifyDTO);
                
                knLogger.debug(methodName, "Profile notification included for MDN ", mdn, 
                        " - activeFSChanged: ", isActiveFSChanged, ", nameChanged: ", isNameChanged);
            }
            
            // Convert doc changes to XCAP diff format
            Collection<KnXcapDiffDocDTO> xcapDocList = new ArrayList<>();
            for (KnOPDocChgDTO chgDTO : chgDocList) {
                KnXcapDiffDocDTO xcapDiffDocDTO = new KnXcapDiffDocDTO();
                xcapDiffDocDTO.setDocChangeType(chgDTO.getDocumentChgType());
                xcapDiffDocDTO.setDocEtag(chgDTO.getNewEtag());
                xcapDiffDocDTO.setDocumentSelector(chgDTO.getDocUri());
                xcapDocList.add(xcapDiffDocDTO);
            }
            
            xcapDiffNotifyDTO.setDocDiffObj(xcapDocList);
            xcapDiffNotifyDTO.setDirNewEtag(dirChgDTO.getDirNewEtag());
            xcapDiffNotifyDTO.setDirPrevEtag(dirChgDTO.getDirPrevEtag());
            xcapDiffNotifyDTO.setDirURI(dirChgDTO.getDirUri());
            xcapDiffNotifyDTO.setXcapRootUri(dirChgDTO.getXcapRootURI());
            xcapDiffNotifyDTO.setProtocolVersion(protocolVersion);
            xcapDiffNotifyDTO.setPocHome(profile.getPocHome());
            xcapDiffNotifyDTO.setPresenceHome(profile.getPresenceHome());
            
            return xcapDiffNotifyDTO;
            
        } catch (Exception e) {
            knLogger.warn(methodName, "Failed to build XCAP Diff DTO for MDN ", mdn, ": ", e.getMessage());
            return null;
        }
    }
    
    /**
     * Sends Corp MCS notifications for LMR Interop when pkgIdMap enables INTEROPFEATURE or DATA_INTER_OP bit.
     * Aligned with Single MDN flow (KnXDMMediator.updateSubscriber):
     * - Checks isRequiredCorpNotified() for each unique corpId
     * - If true, updates UGWINTEROP flag and sends MODIFY_CORP notification
     *
     * Note: Opens its own transaction since the main transaction is committed before notification phase.
     *
     * @param corpIds Set of corpIds to check for LMR Interop notification
     * @param updatedSubsFS2Map Map of MDN to updated subsFS2
     * @param existingProfilesMap Map of MDN to profile (for corpId lookup)
     */
    private void sendCorpLmrInteropNotifications(Set<Integer> corpIds,
                                                  Map<String, String> updatedSubsFS2Map,
                                                  Map<String, KnBulkSubsProfileDTO> existingProfilesMap) {
        String methodName = "sendCorpLmrInteropNotifications";
        
        if (corpIds == null || corpIds.isEmpty() || updatedSubsFS2Map == null || updatedSubsFS2Map.isEmpty()) {
            knLogger.debug(methodName, "No corps to check for LMR Interop notification");
            return;
        }
        
        knLogger.info(methodName, "Checking LMR Interop notification for ", corpIds.size(), " corps");
        
        List<KnCorporateExdmsNotifyDto> corpNotifyList = new ArrayList<>();
        KnPersisterTxn notifyTxn = null;
        
        try {
            // Open a new transaction for notification-related DB operations
            notifyTxn = KnPersisterTxn.getPersisterTxn();
            notifyTxn.open();
            
            for (Integer corpId : corpIds) {
                try {
                    // Find any MDN from this corp with updated subsFS2 to check the LMR flag
                    String subsFS2ForCorp = null;
                    for (Map.Entry<String, String> entry : updatedSubsFS2Map.entrySet()) {
                        String mdn = entry.getKey();
                        KnBulkSubsProfileDTO profile = existingProfilesMap.get(mdn);
                        if (profile != null && profile.getCorpId() == corpId) {
                            subsFS2ForCorp = entry.getValue();
                            break;
                        }
                    }
                    
                    if (subsFS2ForCorp == null) {
                        continue;
                    }
                    
                    // Check if LMR Interop notification is required (aligned with Single MDN flow)
                    boolean isRequiredCorpNotified = corpCommonInfoUtil.isRequiredCorpNotified(subsFS2ForCorp, corpId, notifyTxn);
                    
                    if (isRequiredCorpNotified) {
                        knLogger.info(methodName, "LMR Interop notification required for corpId: ", corpId);
                        
                        // Get corp profile
                        KnCorpProfileDTO corpProfile = corpCommonInfoUtil.getProfileDetails(
                                String.valueOf(corpId), 
                                com.kodiak.xdms.server.common.resources.KnProfileTypes.CORP_PROFILE, 
                                true, 
                                notifyTxn);
                        
                        // Update UGWINTEROP flag (aligned with Single MDN flow)
                        corpCommonInfoUtil.updateLmrInterOpInDB(corpId, ENABLED, notifyTxn);
                        
                        // Form Corp MCS notification payload
                        KnCorporateExdmsNotifyDto corpNotifyDto = corpCommonInfoUtil.formCorpNotifyPayload(
                                MODIFY_CORP.value(),
                                corpProfile,
                                ENABLED,
                                com.kodiak.common.resources.KnConstants.MICROSERVICES_NOTIFY_EVENT_TYPE.CORPORATE_EVENT.value());
                        
                        corpNotifyList.add(corpNotifyDto);
                        knLogger.info(methodName, "Corp LMR Interop notification prepared for corpId: ", corpId);
                    }
                    
                } catch (Exception e) {
                    knLogger.warn(methodName, "Failed to check/send LMR Interop notification for corpId ", corpId, ": ", e.getMessage());
                }
            }
            
            // Save the transaction (commit DB updates like UGWINTEROP flag)
            notifyTxn.save();
            
        } catch (Exception e) {
            knLogger.warn(methodName, "Failed to process LMR Interop notifications: ", e.getMessage());
            KnDbUtil.rollback(notifyTxn);
        }
        
        // Send all corp notifications in batch
        if (!corpNotifyList.isEmpty()) {
            try {
                knLogger.info(methodName, "Publishing Corp MCS notify for LMR Interop - count: ", corpNotifyList.size());
                bulkOpsNotifyUtil.startNotifyMicroServicesJob(corpNotifyList);
            } catch (Exception e) {
                knLogger.warn(methodName, "Failed to send Corp LMR Interop notifications: ", e.getMessage());
            }
        }
    }
    
    /**
     * Sends SMS notifications for MDNs that had their client type upgraded.
     * Aligned with Single MDN flow (KnSubsProvController.updateSubscriber):
     * - When client type changes to PTTRADIOHANDSETCLIENT, PTTRADIOCROSSCARRIERCLIENT, or PTTRADIOWIFIONLYCLIENT
     * - Sends WELCOME_SMS_ID_PTTRADIO
     *
     * Note: Opens its own transaction since the main transaction is committed before notification phase.
     *
     * @param clientTypeUpgrades Map of MDN to new (upgraded) client type
     */
    private void sendSMSNotificationsForClientTypeUpgrades(Map<String, Integer> clientTypeUpgrades) {
        String methodName = "sendSMSNotificationsForClientTypeUpgrades";
        
        if (clientTypeUpgrades == null || clientTypeUpgrades.isEmpty()) {
            knLogger.debug(methodName, "No client type upgrades, skipping SMS notifications");
            return;
        }
        
        KnPersisterTxn smsTxn = null;
        try {
            // Open a new transaction for reading One Message config
            smsTxn = KnPersisterTxn.getPersisterTxn();
            smsTxn.open();
            
            // Check if One Message is enabled (aligned with Single MDN flow)
            String clusterIdEnv = System.getenv(com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME);
            if (clusterIdEnv == null || clusterIdEnv.trim().isEmpty()) {
                knLogger.warn(methodName, "CLUSTERID environment variable is not set, skipping SMS notifications");
                smsTxn.save();
                return;
            }
            int clusterId = Integer.parseInt(clusterIdEnv.trim());
            Map<String, String> smsConfig = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, smsTxn);
            
            // Close the transaction - we only needed it for the config read
            smsTxn.save();
            
            String oneMessageEnabled = (smsConfig != null)
                    ? smsConfig.get(com.kodiak.xdms.server.common.resources.KnConstants.ONE_MESSAGE_SERVICE_ENABLED)
                    : null;
            
            if (oneMessageEnabled == null || !oneMessageEnabled.equals(com.kodiak.xdms.server.common.resources.KnConstants.ONE_MSG_STATUS.ENABLED.value())) {
                knLogger.debug(methodName, "One Message is not enabled, skipping SMS notifications");
                return;
            }
            
            knLogger.info(methodName, "Sending SMS notifications for ", clientTypeUpgrades.size(), " client type upgrades");
            
            final int PTTRADIOHANDSETCLIENT = KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value();
            final int PTTRADIOWIFIONLYCLIENT = KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value();
            final int PTTRADIOCROSSCARRIERCLIENT = KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value();
            
            for (Map.Entry<String, Integer> entry : clientTypeUpgrades.entrySet()) {
                String mdn = entry.getKey();
                Integer newClientType = entry.getValue();
                
                // Send welcome SMS for PTT Radio client types (aligned with Single MDN flow)
                if (newClientType == PTTRADIOHANDSETCLIENT || 
                    newClientType == PTTRADIOCROSSCARRIERCLIENT || 
                    newClientType == PTTRADIOWIFIONLYCLIENT) {
                    
                    sendSMSNotification(newClientType, mdn, KnProvConstants.WELCOME_SMS_ID_PTTRADIO);
                }
            }
            
        } catch (Exception e) {
            knLogger.warn(methodName, "Failed to send SMS notifications (non-critical): ", e.getMessage());
            KnDbUtil.rollback(smsTxn);
        }
    }
    
    /**
     * Sends SMS notification for a single MDN.
     * Aligned with Single MDN flow (KnSubsProvController.sendSMSNotification).
     *
     * @param subsClientType Client type of the subscriber
     * @param mdn MDN
     * @param messageId SMS message ID
     */
    private void sendSMSNotification(Integer subsClientType, String mdn, int messageId) {
        String methodName = "sendSMSNotification()";
        knLogger.info(methodName, "Sending WELCOME SMS NOTIFICATION ", subsClientType, " MDN: ", mdn, " messageId: ", messageId);
        KnProvSMSDTO provSMSDTO = new KnProvSMSDTO();
        provSMSDTO.setMdn(mdn);
        provSMSDTO.setMsgNotificationId(messageId);
        boolean status = provSMSUtil.sendProvSMS(provSMSDTO, null);
        knLogger.info(methodName, "Sent WELCOME SMS NOTIFICATION status ", status);
    }
    
    /**
     * Sends Profile MDN notifications when base MDNs are updated.
     * Aligned with Single MDN flow (KnSubsProvController.updateSubscriber) and KnBulkOpsUpdateAuthStatusProcessor:
     * - When a base MDN is updated and has profile MDNs linked (UPM feature), those profile MDNs need to be notified
     * - Uses getProfileMdnsByBaseMdns to find profile MDNs for each base MDN
     * - Uses profileMdnEtagUpdate to update LASTPROFILEUPDATETIME for profile MDNs
     * - Sends MCS notification via prepareMcxNotifyForProfileMdns
     *
     * This notification is important because:
     * 1. Profile MDNs inherit FeatureSets from their base MDN
     * 2. When base MDN's subsFS2/activeFS2 changes (via pkgIdMap), profile MDNs need to know
     * 3. Without this notification, profile MDN clients won't refresh their capabilities
     *
     * Note: Opens its own transaction since the main transaction is committed before notification phase.
     *
     * @param updatedMdns List of successfully updated base MDNs
     * @param mdnActiveFSChanged Map of MDN to boolean indicating if activeFS changed
     * @param existingProfilesMap Map of MDN to profile data (for userProfileIndex check)
     */
    private void sendProfileMdnNotifications(List<String> updatedMdns,
                                              Map<String, Boolean> mdnActiveFSChanged,
                                              Map<String, KnBulkSubsProfileDTO> existingProfilesMap) {
        String methodName = "sendProfileMdnNotifications";
        
        if (updatedMdns == null || updatedMdns.isEmpty()) {
            knLogger.debug(methodName, "No updated MDNs, skipping profile MDN notifications");
            return;
        }
        
        // Only process base MDNs that had activeFS2 changes
        // Profile MDNs inherit FeatureSets from base MDN, so only notify when FeatureSet changed
        if (mdnActiveFSChanged == null || mdnActiveFSChanged.isEmpty()) {
            knLogger.debug(methodName, "No MDNs with activeFS changes, skipping profile MDN notifications");
            return;
        }
        
        // Filter to only base MDNs (userProfileIndex == null or 0) that had activeFS2 changes
        // Profile MDNs (userProfileIndex > 0) should not trigger notifications for other profile MDNs
        List<String> baseMdns = new ArrayList<>();
        for (String mdn : updatedMdns) {
            // Only include if activeFS2 actually changed for this MDN
            if (!mdnActiveFSChanged.getOrDefault(mdn, false)) {
                continue;
            }
            
            KnBulkSubsProfileDTO profile = existingProfilesMap.get(mdn);
            if (profile != null) {
                Integer userProfileIndex = profile.getUserProfileIndex();
                // userProfileIndex == null or 0 means it's a base MDN
                if (userProfileIndex == null || userProfileIndex == 0) {
                    baseMdns.add(mdn);
                }
            }
        }
        
        if (baseMdns.isEmpty()) {
            knLogger.debug(methodName, "No base MDNs with activeFS changes found, skipping profile MDN notifications");
            return;
        }
        
        knLogger.info(methodName, "Checking profile MDNs for ", baseMdns.size(), " base MDNs with activeFS changes");
        
        KnPersisterTxn profileTxn = null;
        try {
            // Open a new transaction for profile MDN operations
            profileTxn = KnPersisterTxn.getPersisterTxn();
            profileTxn.open();
            
            // Get profile MDNs for each base MDN (aligned with KnBulkOpsUpdateAuthStatusProcessor)
            KnPOCSubscrInfoDAO knPOCSubscrInfoDAO = new KnPOCSubscrInfoDAO();
            Map<String, List<String>> profileMdnsMap = knPOCSubscrInfoDAO.getProfileMdnsByBaseMdns(baseMdns, profileTxn);
            
            if (profileMdnsMap == null || profileMdnsMap.isEmpty()) {
                knLogger.debug(methodName, "No profile MDN mappings found");
                profileTxn.save();
                return;
            }
            
            // Build map of base MDN to profile MDNs (excluding the base MDN itself)
            Map<String, List<String>> profileMdnEtagMap = new HashMap<>();
            int totalProfileMdns = 0;
            
            for (String baseMdn : baseMdns) {
                List<String> allMdnList = profileMdnsMap.get(baseMdn);
                if (allMdnList != null && !allMdnList.isEmpty()) {
                    // Create a copy and remove the base MDN itself
                    List<String> profileMdnList = new ArrayList<>(allMdnList);
                    profileMdnList.remove(baseMdn);
                    
                    if (!profileMdnList.isEmpty()) {
                        profileMdnEtagMap.put(baseMdn, profileMdnList);
                        totalProfileMdns += profileMdnList.size();
                        knLogger.debug(methodName, "Base MDN ", baseMdn, " has ", profileMdnList.size(), " profile MDNs");
                    }
                }
            }
            
            if (profileMdnEtagMap.isEmpty()) {
                knLogger.debug(methodName, "No profile MDNs need notification (all base MDNs have no associated profile MDNs)");
                profileTxn.save();
                return;
            }
            
            knLogger.info(methodName, "Processing notifications for ", totalProfileMdns, " profile MDNs across ", 
                    profileMdnEtagMap.size(), " base MDNs");
            
            // Update profile MDN ETags and get document changes (aligned with KnBulkOpsUpdateAuthStatusProcessor)
            Map<String, Collection<KnDocChangeListDTO>> profileMdnEtagUpdateResult = 
                    knPOCSubscrInfoDAO.profileMdnEtagUpdate(profileMdnEtagMap, profileTxn);
            
            if (profileMdnEtagUpdateResult == null || profileMdnEtagUpdateResult.isEmpty()) {
                knLogger.debug(methodName, "No profile MDN ETag updates resulted");
                profileTxn.save();
                return;
            }
            
            // Get XCAP Root URIs for all profile MDNs
            List<String> allProfileMdns = new ArrayList<>();
            profileMdnEtagMap.values().forEach(allProfileMdns::addAll);
            
            Map<String, String> mcsXcapRootUriMap = null;
            if (!allProfileMdns.isEmpty()) {
                mcsXcapRootUriMap = genInfoUtil.getXCAPRootURI(allProfileMdns, profileTxn, true);
            }
            
            // Save the transaction (commit ETag updates)
            profileTxn.save();
            
            // Send MCS notification for profile MDNs (aligned with KnBulkOpsNotifyUtil.prepareMcxNotifyForProfileMdns)
            boolean isNotifySent = sendMcxNotifyForProfileMdns(profileMdnEtagUpdateResult, mcsXcapRootUriMap);
            knLogger.info(methodName, "Profile MDN notification sent status: ", isNotifySent);
            
        } catch (Exception e) {
            knLogger.warn(methodName, "Failed to send profile MDN notifications (non-critical): ", e.getMessage());
            knLogger.debug(methodName, "Profile MDN notification exception: ", e);
            KnDbUtil.rollback(profileTxn);
        }
    }
    
    /**
     * Sends MCS notification for profile MDNs about document changes.
     * Aligned with KnBulkOpsNotifyUtil.prepareMcxNotifyForProfileMdns.
     *
     * @param profileMdnEtagMap Map of MDN to document changes
     * @param mcsXcapRootUriMap Map of MDN to XCAP Root URI
     * @return true if notification was sent successfully
     */
    private boolean sendMcxNotifyForProfileMdns(Map<String, Collection<KnDocChangeListDTO>> profileMdnEtagMap,
                                                 Map<String, String> mcsXcapRootUriMap) {
        String methodName = "sendMcxNotifyForProfileMdns";
        
        if (profileMdnEtagMap == null || profileMdnEtagMap.isEmpty()) {
            return false;
        }
        
        try {
            // Build MCS Notification DTO (aligned with KnBulkOpsNotifyUtil.prepareMcxNotifyForProfileMdns)
            com.kodiak.xdms.mcsnotifymgr.beans.KnMCSNotifyDTO mcsNotifyDTO = 
                    new com.kodiak.xdms.mcsnotifymgr.beans.KnMCSNotifyDTO();
            mcsNotifyDTO.setNotifyType(com.kodiak.xdms.mcsnotifymgr.resources.KnMCSNotifyConstants.NOTIFYTYPE.DOCUMENT_CHANGE.value());
            
            List<com.kodiak.xdms.mcsnotifymgr.beans.KnDocumentChangeDTO> documentChanges = new ArrayList<>();
            
            for (Map.Entry<String, Collection<KnDocChangeListDTO>> entry : profileMdnEtagMap.entrySet()) {
                String mdn = entry.getKey();
                Collection<KnDocChangeListDTO> docChanges = entry.getValue();
                
                if (docChanges == null || docChanges.isEmpty()) {
                    continue;
                }
                
                String xcapRootUri = mcsXcapRootUriMap != null ? mcsXcapRootUriMap.get(mdn) : null;
                
                List<com.kodiak.xdms.mcsnotifymgr.beans.KnDocChangeListDto> docChangeList = new ArrayList<>();
                for (KnDocChangeListDTO doc : docChanges) {
                    com.kodiak.xdms.mcsnotifymgr.beans.KnDocChangeListDto docDto = 
                            new com.kodiak.xdms.mcsnotifymgr.beans.KnDocChangeListDto(
                                    doc.getDocUri(), doc.getNewEtag(), doc.getPreviousEtag(), doc.getExists());
                    docChangeList.add(docDto);
                }
                
                com.kodiak.xdms.mcsnotifymgr.beans.KnDocumentChangeDTO documentChangeDTO = 
                        new com.kodiak.xdms.mcsnotifymgr.beans.KnDocumentChangeDTO(
                                com.kodiak.xdms.mcsnotifymgr.resources.KnMCSNotifyConstants.DOCTYPE.MDN.value(),
                                mdn, xcapRootUri, docChangeList);
                documentChanges.add(documentChangeDTO);
            }
            
            mcsNotifyDTO.setDocumentChange(documentChanges);
            
            knLogger.debug(methodName, "Sending MCS notification for ", documentChanges.size(), " profile MDNs");
            
            // Send via MCS Document Change Notifier
            boolean isNotifySent = com.kodiak.xdms.mcsnotifymgr.KnMCSDocChangeNotifier.getInstance()
                    .generateMCSNotification(mcsNotifyDTO);
            
            return isNotifySent;
            
        } catch (Exception e) {
            knLogger.warn(methodName, "Failed to send MCS notification for profile MDNs: ", e.getMessage());
            return false;
        }
    }

    /**
     * Builds a map of MDN to NetworkName from the subscriber entry list.
     *
     * Logic:
     * Extracts network names from input request (raw input without comparison to DB).
     *
     * @param subscriberList List of bulk subscriber entries
     * @return Map with MDN as key and NetworkName as value (all MDNs with non-null networkName in input)
     */
    private Map<String, String> buildInputNetworkNameMap(List<KnBulkSubscriberEntry> subscriberList) {
        String methodName = "buildInputNetworkNameMap";

        if (subscriberList == null || subscriberList.isEmpty()) {
            return new HashMap<>();
        }

        Map<String, String> networkNameMap = new HashMap<>();

        for (KnBulkSubscriberEntry entry : subscriberList) {
            String mdn = entry.getMdn() != null ? entry.getMdn().trim() : null;
            String networkName = entry.getNetworkName() != null ? entry.getNetworkName().trim() : null;

            if (mdn == null || mdn.isEmpty()) {
                continue;
            }

            // Include all MDNs with non-null, non-empty networkName in input
            if (networkName != null && !networkName.isEmpty()) {
                networkNameMap.put(mdn, networkName);
            }
        }

        knLogger.debug(methodName, "Input networkNames extracted for ", networkNameMap.size(), " MDNs");
        return networkNameMap;
    }

    /**
     * Builds a map of MDN to NetworkName by comparing input values with existing DB values.
     * Only includes MDNs where the input networkName is ACTUALLY DIFFERENT from the existing DB value.
     *
     * Logic:
     * - If input networkName is null/empty → Skip (keep existing networkName in DB)
     * - If input networkName equals existing DB networkName → Skip (no actual change)
     * - If input networkName is different from existing DB networkName → Include (actual change detected)
     *
     * @param inputNetworkNameMap Map of MDN to input networkName from request
     * @param existingProfilesMap Map of MDN to existing profile from database
     * @return Map with MDN as key and new NetworkName as value (only for MDNs with actual networkName changes)
     */
    private Map<String, String> buildMdnNetworkNameMap(Map<String, String> inputNetworkNameMap,
                                                        Map<String, KnBulkSubsProfileDTO> existingProfilesMap) {
        String methodName = "buildMdnNetworkNameMap";

        if (inputNetworkNameMap == null || inputNetworkNameMap.isEmpty()) {
            knLogger.debug(methodName, "No input networkNames provided, returning empty map");
            return new HashMap<>();
        }

        if (existingProfilesMap == null || existingProfilesMap.isEmpty()) {
            knLogger.debug(methodName, "No existing profiles found, returning empty map");
            return new HashMap<>();
        }

        Map<String, String> networkNameChangesMap = new HashMap<>();

        for (Map.Entry<String, String> entry : inputNetworkNameMap.entrySet()) {
            String mdn = entry.getKey();
            String inputNetworkName = entry.getValue();

            // Skip if input networkName is null or empty
            if (inputNetworkName == null || inputNetworkName.isEmpty()) {
                continue;
            }

            // Get existing profile
            KnBulkSubsProfileDTO existingProfile = existingProfilesMap.get(mdn);
            if (existingProfile == null) {
                // MDN doesn't exist in DB - will be handled by validation phase
                continue;
            }

            // Get existing networkName from DB (trim for comparison)
            String existingNetworkName = existingProfile.getNetworkName();
            if (existingNetworkName != null) {
                existingNetworkName = existingNetworkName.trim();
            }

            // Compare: only include if actually different
            boolean isChanged = false;
            if (existingNetworkName == null || existingNetworkName.isEmpty()) {
                // Existing is null/empty, any non-empty input is a change
                isChanged = true;
            } else if (!existingNetworkName.equals(inputNetworkName)) {
                // Both have values but they're different
                isChanged = true;
            }

            if (isChanged) {
                networkNameChangesMap.put(mdn, inputNetworkName);
                knLogger.debug(methodName, "NetworkName change detected for MDN ", mdn, 
                        ": existing='", existingNetworkName, "' -> new='", inputNetworkName, "'");
            } else {
                knLogger.debug(methodName, "No networkName change for MDN ", mdn, 
                        " (existing='", existingNetworkName, "', input='", inputNetworkName, "')");
            }
        }

        knLogger.info(methodName, "MDNs with actual networkName changes: ", networkNameChangesMap.size(),
                     " out of ", inputNetworkNameMap.size(), " MDNs with input networkNames");

        return networkNameChangesMap;
    }

    // ==================== POST-UPDATE HELPER METHODS (Aligned with Single MDN flow) ====================

    /**
     * Updates corporate profile if corporateName is provided and changed.
     * Aligned with Single MDN flow (KnSubsProvController.updateSubscriber lines 2628-2634, 3030-3044):
     * <pre>
     * String newCorpName = subsProvInputDTO.getCorporateName();
     * if (newCorpName != null && !newCorpName.equals(oldCorpName)) {
     *     isCorpNameUpdated = true;
     * }
     * ...
     * xdmServerDAO.updateCorporateProfile(corpProfilePersistDTO, persisterTxn);
     * </pre>
     *
     * @param existingProfilesMap Map of MDN to existing profile
     * @param mdnsUpdated List of MDNs that were updated
     * @param newCorporateName New corporate name from request
     * @param corpProfileDAO DAO for corporate profile database operations
     * @param persisterTxn Database transaction
     */
    private void updateCorporateProfileIfNeeded(
            Map<String, KnBulkSubsProfileDTO> existingProfilesMap,
            List<String> mdnsUpdated,
            String newCorporateName,
            KnBulkOpsCorpProfileDAO corpProfileDAO,
            KnPersisterTxn persisterTxn) {

        String methodName = "updateCorporateProfileIfNeeded";

        // Find unique corpIds from updated MDNs
        Set<Integer> corpIds = new HashSet<>();
        for (String mdn : mdnsUpdated) {
            KnBulkSubsProfileDTO profile = existingProfilesMap.get(mdn);
            if (profile != null && profile.getCorpId() > 0) {
                corpIds.add(profile.getCorpId());
            }
        }

        if (corpIds.isEmpty()) {
            knLogger.debug(methodName, "No corporate subscribers in update list, skipping corporate profile update");
            return;
        }

        // Update each corporate profile
        // Note: In bulk, all MDNs should belong to same corporate (validated earlier)
        // So typically there will be only one corpId
        for (Integer corpId : corpIds) {
            try {
                boolean updated = corpProfileDAO.updateCorporateProfileSync(corpId, newCorporateName, persisterTxn);
                if (updated) {
                    knLogger.info(methodName, "Corporate profile updated for corpId ", corpId,
                            " with new name: ", newCorporateName);
                }
            } catch (KnDAOException e) {
                knLogger.warn(methodName, "Failed to update corporate profile for corpId ", corpId, ": ", e.getMessage());
                // Non-critical - continue with other operations
            }
        }
    }

    // ==================== VALIDATION HELPER METHODS (Aligned with Single MDN flow) ====================

    /**
     * Validates that accountId in request matches existing DB value for all MDNs.
     * Aligned with Single MDN flow (KnSubsProvController.updateSubscriber lines 2289-2293):
     * <pre>
     * if (newAccountId != null && (!newAccountId.equalsIgnoreCase(oldAccountId))) {
     *     throw new KnProvBOException(KnErrorCodes.BOEntity.ACCOUNT_ID_CHANGE_NOT_ALLOWED, ...);
     * }
     * </pre>
     *
     * @param requestAccountId Account ID from request
     * @param existingProfilesMap Map of MDN to existing profile
     * @param mdnsToValidate List of MDNs to validate
     * @return List of MDNs that have account ID mismatch
     */
    private List<String> validateAccountIdNotChanged(
            String requestAccountId,
            Map<String, KnBulkSubsProfileDTO> existingProfilesMap,
            List<String> mdnsToValidate) {

        String methodName = "validateAccountIdNotChanged";
        List<String> mismatchMdns = new ArrayList<>();

        for (String mdn : mdnsToValidate) {
            KnBulkSubsProfileDTO profile = existingProfilesMap.get(mdn);
            if (profile == null) continue;

            String existingAccountId = profile.getAccountId();
            if (existingAccountId != null) {
                existingAccountId = existingAccountId.trim();
            }

            // Account ID change is not allowed
            if (existingAccountId != null && !existingAccountId.isEmpty()) {
                if (!requestAccountId.equalsIgnoreCase(existingAccountId)) {
                    knLogger.debug(methodName, "Account ID mismatch for MDN ", mdn,
                            ": request=", requestAccountId, ", existing=", existingAccountId);
                    mismatchMdns.add(mdn);
                }
            }
        }

        return mismatchMdns;
    }

    /**
     * Validates that extCorpId in request matches existing corporate association for all MDNs.
     * Aligned with Single MDN flow (KnSubsProvController.updateSubscriber lines 2438-2511):
     * - Retrieves existing extCorpId from DB
     * - Validates that new extCorpId matches or corporate profile exists
     *
     * @param requestExtCorpId ExtCorpId from request
     * @param existingProfilesMap Map of MDN to existing profile
     * @param mdnsToValidate List of MDNs to validate
     * @param corpProfileDAO DAO for corporate profile database operations
     * @param persisterTxn Database transaction
     * @return List of MDNs that have extCorpId mismatch
     */
    private List<String> validateExtCorpIdMatches(
            String requestExtCorpId,
            Map<String, KnBulkSubsProfileDTO> existingProfilesMap,
            List<String> mdnsToValidate,
            KnBulkOpsCorpProfileDAO corpProfileDAO,
            KnPersisterTxn persisterTxn) {

        String methodName = "validateExtCorpIdMatches";
        List<String> mismatchMdns = new ArrayList<>();

        // Collect unique corpIds to fetch extCorpIds in bulk
        Set<Integer> corpIds = new HashSet<>();
        for (String mdn : mdnsToValidate) {
            KnBulkSubsProfileDTO profile = existingProfilesMap.get(mdn);
            if (profile != null && profile.getCorpId() > 0) {
                corpIds.add(profile.getCorpId());
            }
        }

        // Fetch extCorpIds for all corpIds
        Map<Integer, String> corpIdToExtCorpIdMap = new HashMap<>();
        if (!corpIds.isEmpty()) {
            try {
                corpIdToExtCorpIdMap = corpProfileDAO.getExtCorpIdsForCorpIds(
                        new ArrayList<>(corpIds), persisterTxn);
            } catch (KnDAOException e) {
                knLogger.error(methodName, "Failed to fetch extCorpIds: ", e.getMessage());
            }
        }

        // Validate each MDN
        for (String mdn : mdnsToValidate) {
            KnBulkSubsProfileDTO profile = existingProfilesMap.get(mdn);
            if (profile == null) continue;

            int corpId = profile.getCorpId();
            if (corpId <= 0) {
                // Non-corporate subscriber - extCorpId should not be provided
                // or subscriber should be corporate type
                if (profile.getCorporateSubscriptionType() == com.kodiak.xdms.server.common.resources.KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
                    knLogger.debug(methodName, "MDN ", mdn, " is corporate but has no corpId");
                    mismatchMdns.add(mdn);
                }
                continue;
            }

            String existingExtCorpId = corpIdToExtCorpIdMap.get(corpId);
            if (existingExtCorpId != null) {
                existingExtCorpId = existingExtCorpId.trim();
            }

            // ExtCorpId must match
            if (existingExtCorpId != null && !existingExtCorpId.isEmpty()) {
                if (!requestExtCorpId.equalsIgnoreCase(existingExtCorpId)) {
                    knLogger.debug(methodName, "ExtCorpId mismatch for MDN ", mdn,
                            ": request=", requestExtCorpId, ", existing=", existingExtCorpId);
                    mismatchMdns.add(mdn);
                }
            }
        }

        return mismatchMdns;
    }

    /**
     * Validates client type restrictions for bulk update.
     * Aligned with Single MDN flow (KnSubsProvController.updateSubscriber lines 1927-1938):
     * - PDV client type (11) is not supported
     * - Standalone camera client has special restrictions
     *
     * @param existingProfilesMap Map of MDN to existing profile
     * @param mdnsToValidate List of MDNs to validate
     * @return List of MDNs that have restricted client types
     */
    private List<String> validateClientTypeRestrictions(
            Map<String, KnBulkSubsProfileDTO> existingProfilesMap,
            List<String> mdnsToValidate) {

        String methodName = "validateClientTypeRestrictions";
        List<String> restrictedMdns = new ArrayList<>();

        // Client type 11 = PDV (not supported in single MDN flow)
        final int PDV_CLIENT_TYPE = 11;
        // Client type for standalone camera (defined in KnProvConstants, value = 19)
        final int STANDALONECAMERA_CLIENT_TYPE = 19;

        for (String mdn : mdnsToValidate) {
            KnBulkSubsProfileDTO profile = existingProfilesMap.get(mdn);
            if (profile == null) continue;

            int clientType = profile.getSubsClientType();

            // PDV client type is not supported (Single MDN: line 1927-1930)
            if (clientType == PDV_CLIENT_TYPE) {
                knLogger.debug(methodName, "MDN ", mdn, " has PDV client type which is not supported");
                restrictedMdns.add(mdn);
                continue;
            }

            // Standalone camera has special restrictions (Single MDN: line 1934-1938)
            // For bulk update, we don't support camera info changes, so standalone cameras
            // can only have networkName and pkgIdMap updates
            // This is allowed, but log for awareness
            if (clientType == STANDALONECAMERA_CLIENT_TYPE) {
                knLogger.debug(methodName, "MDN ", mdn, " is standalone camera - limited updates allowed");
                // Don't add to restricted list - allow limited updates
            }
        }

        return restrictedMdns;
    }

    /**
     * Detects if actual changes would occur for each MDN.
     * Aligned with Single MDN flow which tracks various change flags:
     * - isNameUpdated (line 2186-2192)
     * - isPkgCodeUpdated (line 2140-2143)
     *
     * @param mdnsToValidate List of MDNs to check
     * @param networkNameMap Map of MDN to new networkName
     * @param pkgIdMap Package ID map from request
     * @param existingProfilesMap Map of MDN to existing profile
     * @return Map of MDN to boolean indicating if changes detected
     */
    private Map<String, Boolean> detectActualChanges(
            List<String> mdnsToValidate,
            Map<String, String> networkNameMap,
            Map<String, Map<String, Integer>> pkgIdMap,
            Map<String, KnBulkSubsProfileDTO> existingProfilesMap) {

        String methodName = "detectActualChanges";
        Map<String, Boolean> changeMap = new HashMap<>();

        boolean hasPkgIdMapChanges = pkgIdMap != null && !pkgIdMap.isEmpty();

        for (String mdn : mdnsToValidate) {
            boolean hasChanges = false;

            // Check networkName change - networkNameMap already contains only MDNs with actual changes
            // (compared against DB in buildMdnNetworkNameMap, gated by nameChangeAllowed flag)
            if (networkNameMap != null && networkNameMap.containsKey(mdn)) {
                hasChanges = true;
                knLogger.debug(methodName, "NetworkName change detected for MDN ", mdn);
                KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_BULK_SUBSCR_NAME_CHANGE);
            }

            // Check pkgIdMap changes (applies to all MDNs if provided)
            if (hasPkgIdMapChanges) {
                hasChanges = true;
                knLogger.debug(methodName, "PkgIdMap changes will be applied to MDN ", mdn);
            }

            changeMap.put(mdn, hasChanges);
        }

        return changeMap;
    }
}

