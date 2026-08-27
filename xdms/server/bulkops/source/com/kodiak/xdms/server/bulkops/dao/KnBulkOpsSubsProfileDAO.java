package com.kodiak.xdms.server.bulkops.dao;

import com.kodiak.common.commdto.common.KnBulkSubscriberEntry;
import com.kodiak.common.commdto.request.KnXDMBulkSubsProvInfoDTO;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.bulkops.KnBulkOpsException;
import com.kodiak.xdms.server.bulkops.dto.common.KnMDNValidationResult;
import com.kodiak.xdms.server.bulkops.resources.KnBulkOpsConstants;
import com.kodiak.xdms.server.bulkops.resources.KnBulkOpsDAOSourceTypes;
import com.kodiak.xdms.server.bulkops.resources.KnBulkOpsErrorCodes;
import com.kodiak.xdms.server.bulkops.utils.KnBulkOpsDBUtil;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class KnBulkOpsSubsProfileDAO {
    private static final KnLogger knLogger = KnLogger.getLogger(KnBulkOpsSubsProfileDAO.class);

    private KnBulkOpsDBUtil bulkOpsDBUtil = null;

    public KnBulkOpsSubsProfileDAO() throws KnBulkOpsException {
        bulkOpsDBUtil = KnBulkOpsDBUtil.getInstance();
    }

    /**
     * Check which MDNs are present as Alias MDNs
     *
     * @param mdnList       List of MDNs to check
     * @param persisterTxn Database transaction
     * @return List of MDNs present as Alias MDNs
     * @throws KnDAOException if database operation fails
     */
    public List<String> mdnPresentAsAlias (List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "mdnPresentAsAlias(List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "Checking Alias Info for ", mdnList.size(), " MDNs");

        String queryPrefix = "SELECT ALIAS_MDN FROM " + KnBulkOpsDAOSourceTypes.POCSUBSCRINFO + " WHERE ALIAS_MDN IN ";

        List<String> presentMdns = bulkOpsDBUtil.executeInClauseQuery(
                queryPrefix,
                mdnList,
                rs -> {
                    List<String> resultList = new ArrayList<>();
                    while (rs.next()) {
                        String mdn = rs.getString("ALIAS_MDN");
                        resultList.add(mdn != null ? mdn.trim() : mdn);
                    }
                    return resultList;
                },
                persisterTxn,
                KnDBConst.DataStores.XDM_SHARED_DATA
        );

        knLogger.debug(methodName, "Found ", presentMdns.size(), " MDNs in Alias Info");
        return presentMdns;
    }

    /**
     * Retrieve subscriber profiles by MCS IDs (MC_ID, MC_PTTID, MC_VIDEOID, MC_DATAID)
     * Returns subscriber data including MDN, subscription types, account ID, and client type
     *
     * @param mcsIdList     List of MCS IDs to check (will be converted to bytes for querying)
     * @param persisterTxn  Database transaction
     * @return List of KnXDMBulkSubsProvInfoDTO containing subscriber profile data for matching MCS IDs
     * @throws KnDAOException if database operation fails
     */
    public List<KnXDMBulkSubsProvInfoDTO> selectSubscriberProfileByMCSIds(List<String> mcsIdList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "selectSubscriberProfileByMCSIds(List<String>, KnPersisterTxn)";

        if (mcsIdList == null || mcsIdList.isEmpty()) {
            knLogger.debug(methodName, "MCS ID list is empty, returning empty result");
            return new ArrayList<>();
        }

        knLogger.debug(methodName, "ENTRY: Querying subscribers by ", mcsIdList.size(), " MCS IDs");

        // Build placeholders for the IN clause (shared by each UNION branch)
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < mcsIdList.size(); i++) {
            placeholders.append(i == 0 ? "?" : ",?");
        }

        final String selectClause = "SELECT MDN, PUBLICSUBSCRIPTIONTYPE, CORPSUBSCRIPTIONTYPE, ACCOUNT_ID, CLIENT_TYPE FROM "
                + KnBulkOpsDAOSourceTypes.POCSUBSCRINFO + " WHERE ";

        // Build the query using UNION across all MCS ID columns
        StringBuilder query = new StringBuilder();
        query.append(selectClause).append("MC_ID IN (").append(placeholders).append(")");
        query.append(" UNION ");
        query.append(selectClause).append("MC_PTTID IN (").append(placeholders).append(")");
        query.append(" UNION ");
        query.append(selectClause).append("MC_VIDEOID IN (").append(placeholders).append(")");
        query.append(" UNION ");
        query.append(selectClause).append("MC_DATAID IN (").append(placeholders).append(")");

        // Prepare parameters - each MCS ID needs to be converted to bytes for all 4 columns
        Object[] params = new Object[mcsIdList.size() * 4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < mcsIdList.size(); j++) {
                String mcsId = mcsIdList.get(j);
                params[i * mcsIdList.size() + j] = mcsId != null ? mcsId.getBytes(StandardCharsets.UTF_8) : null;
            }
        }

        List<KnXDMBulkSubsProvInfoDTO> resultList = bulkOpsDBUtil.executeQuery(query.toString(), params, rs -> {
            List<KnXDMBulkSubsProvInfoDTO> results = new ArrayList<>();
            while (rs.next()) {
                KnXDMBulkSubsProvInfoDTO dto = new KnXDMBulkSubsProvInfoDTO();
                String mdn = rs.getString("MDN");
                KnBulkSubscriberEntry entry = new KnBulkSubscriberEntry();
                entry.setMdn(mdn != null ? mdn.trim() : null);
                dto.setSubscriberList(Collections.singletonList(entry));
                dto.setPublicSubscriptionType(rs.getInt("PUBLICSUBSCRIPTIONTYPE"));
                dto.setCorporateSubscriptionType(rs.getInt("CORPSUBSCRIPTIONTYPE"));
                dto.setAccountId(rs.getString("ACCOUNT_ID") != null ? rs.getString("ACCOUNT_ID").trim() : null);
                dto.setSubscriberClientType(rs.getInt("CLIENT_TYPE"));
                results.add(dto);
            }
            return results;
        }, persisterTxn, KnDBConst.DataStores.XDM_SHARED_DATA);

        knLogger.debug(methodName, "EXIT: Found ", resultList.size(), " subscribers matching MCS IDs");
        return resultList;
    }


    /**
     * Retrieve the total subscriber count from POCSUBSCRINFO table
     *
     * @param persisterTxn Database transaction
     * @return Total subscriber count
     * @throws KnDAOException if database operation fails
     */
    public int retrieveSubscriberCount(KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "retrieveSubscriberCount(KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: Retrieving the Subscriber Count");

        String query = "SELECT COUNT(MDN) FROM " + KnBulkOpsDAOSourceTypes.POCSUBSCRINFO + " WHERE nvl(USERPROFILEINDEX,0) = 0";

        Integer subscriberCount = bulkOpsDBUtil.executeQuery(query, new Object[]{}, rs -> {
            if (rs.next()) {
                return rs.getInt(1); // COUNT result is in first column
            }
            return 0;
        }, persisterTxn, KnDBConst.DataStores.XDM_SHARED_DATA);

        knLogger.debug(methodName, "EXIT: Subscriber Count = ", subscriberCount);
        return subscriberCount;
    }

    /**
     * Retrieve the total corporate subscriber count from POCSUBSCRINFO table for a given corpId
     *
     * @param corpId        Corporate ID
     * @param persisterTxn Database transaction
     * @return Total corporate subscriber count
     * @throws KnDAOException if database operation fails
     */
    public int retrieveCorpSubscriberCount(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "retrieveCorpSubsCount(int, KnPersisterTxn)";
        knLogger.debug(methodName, "Retrieving Corporate Subscriber Count for corpId: ", corpId);

        String query = "SELECT COUNT(MDN) FROM " + KnBulkOpsDAOSourceTypes.POCSUBSCRINFO + " WHERE CORPID = ?";
        Object[] params = new Object[]{corpId};

        Integer corpSubsCount = bulkOpsDBUtil.executeQuery(query, params, rs -> {
            if (rs.next()) {
                return rs.getInt(1); // COUNT result is in first column
            }
            return 0;
        }, persisterTxn, KnDBConst.DataStores.XDM_SHARED_DATA);

        knLogger.debug(methodName, "Corporate Subscriber Count retrieved: ", corpSubsCount);
        return corpSubsCount;
    }

    public int retrieveCorpSubsCntNotMarkedForDeleteion(int corpId, KnPersisterTxn  persisterTxn) throws KnDAOException {
        String methodName =  "retrieveCorpSubsCntNotMarkedForDeleteion(int, KnPersisterTxn)";
        knLogger.debug(methodName, "Retrieving Corporate Subscriber Count which are not marked for deletion for corpId: ", corpId);
        String query = "SELECT COUNT(MDN) FROM " + KnBulkOpsDAOSourceTypes.POCSUBSCRINFO + " WHERE CORPID = ? AND SERVICEAUTHSTATUS != 10";
        Object[] params = new Object[]{corpId};

        Integer corpSubsCount = bulkOpsDBUtil.executeQuery(query, params, rs -> {
            if (rs.next()) {
                return rs.getInt(1); // COUNT result is in first column
            }
            return 0;
        }, persisterTxn, KnDBConst.DataStores.XDM_SHARED_DATA);

        knLogger.debug(methodName, "Corporate Subscriber Count retrieved: ", corpSubsCount);
        return corpSubsCount;

    }

    /**
     * Bulk insert subscriber profiles into DG.POCSUBSCRINFO table
     * This method performs a batch insert for multiple subscribers in a single database operation
     * Uses dynamic field list with conditional logic matching single insert implementation
     *
     * @param bulkSubsProvInfoDTO Bulk subscriber provisioning info DTO containing all subscriber data
     * @param persisterTxn Database transaction
     * @throws KnDAOException if database operation fails
     */
    public void createBulkSubscriberProfiles(
            KnXDMBulkSubsProvInfoDTO bulkSubsProvInfoDTO,
            KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "createBulkSubscriberProfiles(KnXDMBulkSubsProvInfoDTO, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY: Creating bulk subscriber profiles for ",
                      bulkSubsProvInfoDTO.getSubscriberList().size(), " subscribers");

        // Extract common values to determine which fields to include
        final int corpSubscriptionType = bulkSubsProvInfoDTO.getCorporateSubscriptionType();
        final Boolean pairingInd = bulkSubsProvInfoDTO.getPairingInd();
        final int payType = bulkSubsProvInfoDTO.getPayType();
        final String affiliateId = bulkSubsProvInfoDTO.getAffiliateId();
        final String imei = bulkSubsProvInfoDTO.getIMEI();
        final String extGatewayId = bulkSubsProvInfoDTO.getExtGatewayId();

        // Build dynamic field list based on conditions
        ArrayList<String> queryFields = new ArrayList<>();
        queryFields.add("MDN");
        queryFields.add("POCHOME");
        queryFields.add("PRESENCEHOME");
        queryFields.add("XDMSHOME");
        queryFields.add("SUBSCRCREATIONTIME");
        queryFields.add("LASTPROFILEUPDATETIME");
        queryFields.add("SUBSCRNAME"); // Always include for bulk (will use MDN if null)
        queryFields.add("SERVICEAUTHSTATUS");
        queryFields.add("PUBLICSUBSCRIPTIONTYPE");
        queryFields.add("CORPSUBSCRIPTIONTYPE");

        // Conditional fields
        if (pairingInd != null) {
            queryFields.add("CORPCONTACTPAIRINGIND");
        }
        if (payType != -1) {
            queryFields.add("PAYTYPE");
        }
        if (affiliateId != null) {
            queryFields.add("AFFLIATEID");
        }
        if (corpSubscriptionType == KnBulkOpsConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
            queryFields.add("CORPID");
        }
        if (imei != null) {
            queryFields.add("IMEI");
        }
        queryFields.add("EMAIL");


        // Always included fields
        queryFields.add("CLIENT_TYPE");
        queryFields.add("DISPATCH_GRP_MEMBER");
        queryFields.add("ACCOUNT_ID");
        queryFields.add("ADDLINFO"); // Hierarchy Type
        queryFields.add("DISPATCH_TYPE");
        queryFields.add("SERVICE_STATUS_OP");
        queryFields.add("SERVICE_STATUS_AUTHUSER");
        queryFields.add("UFMI");
        queryFields.add("iDI_USERNAME");
        queryFields.add("iDI_E_PASSWORD");
        queryFields.add("iDI_BUID");
        queryFields.add("LICENSE_TYPE");
        queryFields.add("QPPPACKID");
        queryFields.add("SEGMENT_INDICATOR");

        // Feature Sets (FS1 and FS2)
        queryFields.add("SUBSCRIBERFS1");
        queryFields.add("CLIENTFS1");
        queryFields.add("ACTIVEFS1");
        queryFields.add("OPSFS1");
        queryFields.add("CORPADMINFS1");
        queryFields.add("XDMSFS1");
        queryFields.add("SUBSCRIBERFS2");
        queryFields.add("CLIENTFS2");
        queryFields.add("ACTIVEFS2");
        queryFields.add("OPSFS2");
        queryFields.add("CORPADMINFS2");
        queryFields.add("XDMSFS2");

        // MCS IDs
        queryFields.add("USER_ID");
        queryFields.add("MC_ID");
        queryFields.add("MC_PTTID");
        queryFields.add("MC_VIDEOID");
        queryFields.add("MC_DATAID");

        // Profile fields
        queryFields.add("ISDEFAULTPROFILE");
        queryFields.add("USERPROFILEINDEX");
        queryFields.add("USERPROFILEID");
        queryFields.add("ALIAS_MDN");
        queryFields.add("FEATURE_REL_VERSION");
        queryFields.add("USERPROFILEFS2");
        queryFields.add("USERAGENT");
        queryFields.add("PREVSERVICEAUTHSTATUS");

        if (extGatewayId != null) {
            queryFields.add("GW_ID");
        }

        // Define key columns for MERGE operation (MDN is the primary key)
        List<String> keyColumns = Collections.singletonList("MDN");

        // Define specific columns to update on MERGE MATCHED
        // Only these columns should be updated when record already exists
        List<String> updateColumns = Arrays.asList(
            "LASTPROFILEUPDATETIME", "SUBSCRNAME", "SUBSCRIBERFS1", "CLIENTFS1", "ACTIVEFS1", "OPSFS1",
            "CORPADMINFS1", "ADDLINFO", "XDMSFS1", "LICENSE_TYPE", "QPPPACKID", "SUBSCRIBERFS2", "CLIENTFS2",
            "ACTIVEFS2", "OPSFS2", "CORPADMINFS2", "XDMSFS2", "USERPROFILEFS2"
        );

        // Generate MERGE query for upsert operation (INSERT if not exists, UPDATE if exists)
        String query = KnBulkOpsDBUtil.getMergeQuery(KnBulkOpsDAOSourceTypes.POCSUBSCRINFO, keyColumns, queryFields, updateColumns);
        knLogger.debug(methodName, "Merge query: ", query);

        try {
            // Extract common values (used for all subscribers)
            final long profileCreationTime = bulkSubsProvInfoDTO.getProfileCreationTime();
            final long lastProfileUpdateTime = bulkSubsProvInfoDTO.getLastProfileUpdateTime();
            final int serviceAuthStatus = bulkSubsProvInfoDTO.getServiceAuthStatus().value();
            final int publicSubscriptionType = bulkSubsProvInfoDTO.getPublicSubscriptionType();
            final int corpId = (corpSubscriptionType == KnBulkOpsConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value())
                    ? Integer.parseInt(bulkSubsProvInfoDTO.getCorpId()) : -1;
            final int pairingIndValue = Boolean.TRUE.equals(pairingInd) ? 1 : 0;
            final int clientType = bulkSubsProvInfoDTO.getSubscriberClientType();
            final int dispatchGroupMember = bulkSubsProvInfoDTO.getDispatchGroupMember();
            final String accountId = bulkSubsProvInfoDTO.getAccountId();
            final int hierarchyType = bulkSubsProvInfoDTO.getHierarchyType().value();
            final int serviceStatusOp = bulkSubsProvInfoDTO.getServiceStatusOp();
            final int serviceStatusAuthUser = bulkSubsProvInfoDTO.getServiceStatusAuthUser();
            final String ufmi = bulkSubsProvInfoDTO.getUfmi();
            final String iDenUserName = bulkSubsProvInfoDTO.getiDenUserName();
            final String iDenPassword = bulkSubsProvInfoDTO.getiDenPassword();
            final String iDenBusUnitId = bulkSubsProvInfoDTO.getiDenBusUnitId();
            final int licenseType = bulkSubsProvInfoDTO.getLicenseType();
            final int qppPkgId = bulkSubsProvInfoDTO.getQppPkgId();
            final String firstNetIndicator = bulkSubsProvInfoDTO.getFirstNetIndicator();
            final String subsFS2 = bulkSubsProvInfoDTO.getSubsFS2();
            final String clientFS2 = bulkSubsProvInfoDTO.getClientFS2();
            final String opsFS2 = bulkSubsProvInfoDTO.getOpsFS2();
            final String corpAdminFS2 = bulkSubsProvInfoDTO.getCorpAdminFS2();
            final String xdmsFS2 = bulkSubsProvInfoDTO.getXdmsFS2();
            final int isDefaultProfile = bulkSubsProvInfoDTO.getIsDefaultProfile();
            final int userProfileIndex = bulkSubsProvInfoDTO.getUserProfileIndex();
            final String userProfileId = bulkSubsProvInfoDTO.getUserProfileId();
            final String aliasMdn = null; // Not applicable for bulk create
            final String featureRelVersion = bulkSubsProvInfoDTO.getFeatureRelVersion();
            final String userProfileFS2 = bulkSubsProvInfoDTO.getUserProfileFS2();
            final String userAgent = bulkSubsProvInfoDTO.getUserAgent();
            final int previousServiceAuthStatus = bulkSubsProvInfoDTO.getPreviousServiceAuthStatusToStore();

            // Prepare batch parameters
            List<Object[]> batchParams = new ArrayList<>();

            // Build parameter array for each subscriber
            for (var subscriberEntry : bulkSubsProvInfoDTO.getSubscriberList()) {
                final String mdn = subscriberEntry.getMdn();
                final String pocHome = subscriberEntry.getPocHome();
                final String presenceHome = subscriberEntry.getPresenceHome();
                final String email = subscriberEntry.getEmail();
                final String userId = subscriberEntry.getUserId();
                final int dispatchType = subscriberEntry.getDispatchType();
                final String xdmsHome = KnBulkOpsDBUtil.getXdmPttServerId();

                String networkName = (subscriberEntry.getNetworkName() != null && !subscriberEntry.getNetworkName().isEmpty())
                    ? subscriberEntry.getNetworkName() : mdn;
                // Multilingual support - convert from UTF-8 to ISO-8859-1 encoding
                networkName = new String(networkName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);

                final String activeFS2 = subscriberEntry.getActiveFS2();
                final String mcId = subscriberEntry.getMcId();
                final String mcpttId = subscriberEntry.getMcpttId();
                final String mcVideoId = subscriberEntry.getMcVideoId();
                final String mcDataId = subscriberEntry.getMcDataId();

                // Build parameter list matching query field order
                List<Object> params = new ArrayList<>();

                // Mandatory fields
                params.add(mdn);
                params.add(pocHome);
                params.add(presenceHome);
                params.add(xdmsHome);
                params.add(profileCreationTime);
                params.add(lastProfileUpdateTime);
                params.add(networkName);
                params.add(serviceAuthStatus);
                params.add(publicSubscriptionType);
                params.add(corpSubscriptionType);

                // Conditional fields (must match order in queryFields)
                if (pairingInd != null) {
                    params.add(pairingIndValue);
                }
                if (payType != -1) {
                    params.add(payType);
                }
                if (affiliateId != null) {
                    params.add(affiliateId);
                }
                if (corpSubscriptionType == KnBulkOpsConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
                    params.add(corpId);
                }
                if (imei != null) {
                    params.add(imei);
                }
                params.add(email);

                // Always included fields
                params.add(clientType);
                params.add(dispatchGroupMember);
                params.add(accountId);
                params.add(hierarchyType);
                params.add(dispatchType);
                params.add(serviceStatusOp);
                params.add(serviceStatusAuthUser);
                params.add(ufmi);
                params.add(iDenUserName);
                params.add(iDenPassword);
                params.add(iDenBusUnitId);
                params.add(licenseType);
                params.add(qppPkgId);
                params.add(firstNetIndicator);

                // Feature Sets (both long and string representations)
                params.add(KnGeneralUtil.convertHexStringToLong(subsFS2));
                params.add(KnGeneralUtil.convertHexStringToLong(clientFS2));
                params.add(KnGeneralUtil.convertHexStringToLong(activeFS2));
                params.add(KnGeneralUtil.convertHexStringToLong(opsFS2));
                params.add(KnGeneralUtil.convertHexStringToLong(corpAdminFS2));
                params.add(KnGeneralUtil.convertHexStringToLong(xdmsFS2));
                params.add(KnGeneralUtil.getFeatureSet(subsFS2));
                params.add(KnGeneralUtil.getFeatureSet(clientFS2));
                params.add(KnGeneralUtil.getFeatureSet(activeFS2));
                params.add(KnGeneralUtil.getFeatureSet(opsFS2));
                params.add(KnGeneralUtil.getFeatureSet(corpAdminFS2));
                params.add(KnGeneralUtil.getFeatureSet(xdmsFS2));

                // MCS IDs (stored as bytes in UTF-8)
                params.add(userId);
                params.add(mcId.getBytes(StandardCharsets.UTF_8));
                params.add(mcpttId.getBytes(StandardCharsets.UTF_8));
                params.add(mcVideoId.getBytes(StandardCharsets.UTF_8));
                params.add(mcDataId.getBytes(StandardCharsets.UTF_8));

                // Profile related fields
                params.add(isDefaultProfile);
                params.add(userProfileIndex);
                params.add(userProfileId);
                params.add(aliasMdn);
                params.add(featureRelVersion);
                params.add(KnGeneralUtil.getFeatureSet(userProfileFS2));
                params.add(userAgent);
                params.add(previousServiceAuthStatus);

                if (extGatewayId != null) {
                    params.add(extGatewayId);
                }

                batchParams.add(params.toArray());
            }

            // Use common utility for batch MERGE execution (INSERT if not exists, UPDATE if exists)
            // Note: SUBSCRCREATIONTIME is excluded from update columns to preserve original creation time
            bulkOpsDBUtil.executeBatchMerge(
                    KnBulkOpsDAOSourceTypes.POCSUBSCRINFO,
                    keyColumns,
                    queryFields,
                    updateColumns,
                    batchParams,
                    persisterTxn,
                    KnDBConst.DataStores.XDM_SHARED_DATA
            );

            knLogger.info(methodName, "Successfully merged ", batchParams.size(), " subscriber profiles");

        } catch (Exception e) {
            knLogger.error(methodName, "Exception during bulk subscriber profile creation: ", e);
            throw KnDbUtil.processException(e, "Failed to create bulk subscriber profiles: " + e.getMessage(), KnBulkOpsDBUtil.getXdmPttServerId(),
                    KnBulkOpsDAOSourceTypes.POCSUBSCRINFO, query);
        }

        knLogger.info(methodName, "EXIT: Bulk subscriber profile creation completed");
    }

    /**
     * Retrieve subscriber count of specific client types for a corporation
     * Matches the query logic from KnProvXDMServerDAO.getSubsCountOfClientTypeForCorp()
     *
     * @param corpId Corporate ID
     * @param clientTypes List of client type IDs to filter
     * @param persisterTxn Database transaction
     * @return Count of subscribers matching the criteria
     * @throws KnDAOException if database operation fails
     */
    public int getSubsCountOfClientTypeForCorp(int corpId, List<Integer> clientTypes, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubsCountOfClientTypeForCorp(int, List<Integer>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: Retrieve Subs Count for corpId=", corpId, ", clientTypes=", clientTypes);

        // Validate input - early return if no client types provided
        if (clientTypes == null || clientTypes.isEmpty()) {
            knLogger.debug(methodName, "No client types provided, returning count as 0");
            return 0;
        }

        // Convert Integer list to String list for executeInClauseQuery
        final List<String> clientTypeStrings = new ArrayList<>(clientTypes.size());
        for (Integer clientType : clientTypes) {
            clientTypeStrings.add(String.valueOf(clientType));
        }

        // Build query prefix with USERPROFILEINDEX check (matching original implementation)
        final String queryPrefix =
            "SELECT COUNT(MDN) FROM " + KnBulkOpsDAOSourceTypes.POCSUBSCRINFO + " WHERE CORPID = " + corpId +
            " AND (USERPROFILEINDEX = 0 OR USERPROFILEINDEX IS NULL) " +
            "AND CLIENT_TYPE IN ";

        knLogger.debug(methodName, "queryPrefix- ", queryPrefix);

        // Use executeInClauseQuery for optimized IN clause handling (consistent with all KnBulkOpsXDMServerDAO methods)
        Integer subsCount = bulkOpsDBUtil.executeInClauseQuery(
            queryPrefix,
            clientTypeStrings,
            rs -> {
                if (rs.next()) {
                    return rs.getInt(1); // COUNT result is in first column
                }
                return 0;
            },
            persisterTxn,
            KnDBConst.DataStores.XDM_SHARED_DATA
        );

        knLogger.debug(methodName, "EXIT: Subscriber Count = ", subsCount);
        return subsCount;
    }

    /**
     * Validates MDN hierarchy and returns detailed results for each MDN
     * MDNs that match the hierarchy or don't exist in DB are considered valid
     * MDNs with hierarchy mismatch are considered invalid
     *
     * @param mdnList       List of MDNs to validate
     * @param hierarchyType Expected hierarchy type
     * @param persisterTxn Database transaction
     * @return KnMDNValidationResult containing lists of valid and invalid MDNs with error details
     * @throws KnDAOException if database operation fails
     */
    public KnMDNValidationResult validateMDNCCAndHierarchy(List<String> mdnList, KnConstants.HIERARCHY_TYPE hierarchyType, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "validateMDNCCAndHierarchy(List<String>, KnConstants.HIERARCHY_TYPE)";
        knLogger.debug(methodName, "mdnList size: ", mdnList.size(), "hierarchyType:", hierarchyType.value());

        KnMDNValidationResult result = new KnMDNValidationResult();

        if (mdnList == null || mdnList.isEmpty()) {
            knLogger.debug(methodName, "No MDNs to validate");
            return result;
        }

        String queryPrefix = "SELECT MDN, ADDLINFO FROM " + KnBulkOpsDAOSourceTypes.POCSUBSCRINFO + " WHERE MDN IN ";

        Set<String> mdnSet = new HashSet<>(mdnList);

        bulkOpsDBUtil.executeInClauseQuery(
                queryPrefix,
                mdnList,
                rs -> {
                    while (rs.next()) {
                        String mdn = rs.getString("MDN");
                        mdn = (mdn != null) ? mdn.trim() : mdn;
                        KnConstants.HIERARCHY_TYPE type = KnConstants.HIERARCHY_TYPE.validate(rs.getInt("ADDLINFO"));

                        mdnSet.remove(mdn); // Remove from set to track which MDNs were found

                        if (hierarchyType.equals(type)) {
                            result.addValidMdn(mdn);
                            knLogger.debug(methodName, "MDN hierarchy validated successfully: ", mdn);
                        } else {
                            result.addInvalidMdn(mdn,
                                    KnBulkOpsErrorCodes.BOEntity.INVALID_HIERARCHY_REQUEST,
                                    "Hierarchy mismatch - Expected: " + hierarchyType + ", Found: " + type);
                            knLogger.debug(methodName, "Hierarchy mismatch for MDN: ", mdn,
                                    ", Expected: ", hierarchyType, ", Found: ", type);
                        }
                    }
                    return null;
                },
                persisterTxn,
                KnDBConst.DataStores.XDM_SHARED_DATA
        );

        // MDNs not found in DB are considered valid (assuming valid subscriber)
        for (String mdn : mdnSet) {
            result.addValidMdn(mdn);
            knLogger.debug(methodName, "MDN not found in DB, considering valid: ", mdn);
        }

        knLogger.debug(methodName, "Validation completed - Valid: ", result.getValidMdns().size(),
                ", Invalid: ", result.getInvalidMdns().size());

        return result;
    }

    /**
     * Get subscriber service authentication status for multiple MDNs
     *
     * @param mdnList       List of MDNs to check
     * @param persisterTxn Database transaction
     * @return Map of MDN to service auth status (Integer)
     * @throws KnDAOException if database operation fails
     */
    public Map<String, Integer> getSubscriberServiceAuthStatus(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubscriberServiceAuthStatus(List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "Fetching service auth status for ", mdnList.size(), " MDNs");

        String queryPrefix = "SELECT MDN, SERVICEAUTHSTATUS FROM " + KnBulkOpsDAOSourceTypes.POCSUBSCRINFO + " WHERE MDN IN ";

        Map<String, Integer> serviceAuthStatusMap = bulkOpsDBUtil.executeInClauseQuery(
                queryPrefix,
                mdnList,
                rs -> {
                    Map<String, Integer> resultMap = new HashMap<>();
                    while (rs.next()) {
                        String mdn = rs.getString("MDN");
                        mdn = (mdn != null) ? mdn.trim() : mdn;
                        int authStatus = rs.getInt("SERVICEAUTHSTATUS");
                        resultMap.put(mdn, authStatus);
                    }
                    return resultMap;
                },
                persisterTxn,
                KnDBConst.DataStores.XDM_SHARED_DATA
        );

        knLogger.debug(methodName, "Fetched service auth status for ", serviceAuthStatusMap.size(), " MDNs");
        return serviceAuthStatusMap;
    }
}