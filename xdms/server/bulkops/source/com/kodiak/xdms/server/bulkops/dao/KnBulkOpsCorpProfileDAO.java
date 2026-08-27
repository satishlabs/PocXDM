package com.kodiak.xdms.server.bulkops.dao;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.bulkops.KnBulkOpsException;
import com.kodiak.xdms.server.bulkops.resources.KnBulkOpsConstants;
import com.kodiak.xdms.server.bulkops.resources.KnBulkOpsDAOSourceTypes;
import com.kodiak.xdms.server.bulkops.utils.KnBulkOpsDBUtil;
import com.kodiak.frameworks.dbfw.KnDbSyncFwConstants;
import com.kodiak.frameworks.dbfw.KnDbSyncFwConstants.EXECUTOR;
import com.kodiak.frameworks.dbfw.collectors.KnSqlJobCollector;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KnBulkOpsCorpProfileDAO {
    private static final KnLogger knLogger = KnLogger.getLogger(KnBulkOpsCorpProfileDAO.class);

    private KnBulkOpsDBUtil bulkOpsDBUtil = null;

    public KnBulkOpsCorpProfileDAO() throws KnBulkOpsException {
        bulkOpsDBUtil = KnBulkOpsDBUtil.getInstance();
    }

    /**
     * Retrieve Corporate Profile Information based on external corporate ID
     *
     * @param extCorpId   External Corporate ID
     * @param persisterTxn Database transaction
     * @return KnCorpProfileInfo containing corporate profile details
     * @throws KnDAOException if database operation fails
     */
    public KnCorpProfileInfo retrieveCorporateProfile(String extCorpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "retrieveCorporateProfile(String, KnPersisterTxn)";
        knLogger.debug(methodName, "Retrieving Corporate Profile for extCorpId: ", extCorpId);

        String query = "SELECT * FROM " + KnBulkOpsDAOSourceTypes.CORPINFO + " WHERE EXTCORPID = ?";
        Object[] params = new Object[]{extCorpId};

        KnCorpProfileInfo corpProfileInfoDTO = bulkOpsDBUtil.executeQuery(query, params, rs -> {
            if(rs != null){
                KnCorpProfileInfo respDTO = new KnCorpProfileInfo();
                while (rs.next()) {
                    respDTO.setCorpId(rs.getInt("CORPID"));
                    respDTO.setExtCorpId(rs.getString("EXTCORPID") != null ? rs.getString("EXTCORPID").trim() : null);
                    respDTO.setCorporateName(rs.getString("CORPNAME"));
                    respDTO.setPairedContactListId(rs.getInt("PAIREDCONTACTLISTID"));
                    respDTO.setMaxSubscribers(rs.getInt("MAXSUBSCRS"));
                    respDTO.setMaxCorpGroups(rs.getInt("MAXCORPGROUPS"));
                    respDTO.setMaxMembersPerCorpGroup(rs.getInt("MAXMEMBERSPERCORPGROUP"));
                    respDTO.setMaxCorpLists(rs.getInt("MAXCORPLISTS"));
                    respDTO.setMaxMembersPerCorpList(rs.getInt("MAXMEMBERSPERCORPLIST"));
                    respDTO.setPocHome(rs.getString("POCHOME") != null ? rs.getString("POCHOME").trim() : null);
                    String corpFs2=rs.getString("CORPFS2")!=null?rs.getString("CORPFS2").trim(): KnGeneralUtil.convertLongToHexString(rs.getLong("CORPFS1"));
                    respDTO.setCorpFS2(corpFs2);
                    String opsCorpFs2=rs.getString("OPSCORPFS2")!=null?rs.getString("OPSCORPFS2").trim():KnGeneralUtil.convertLongToHexString(rs.getLong("OPSCORPFS1"));
                    respDTO.setOpsCorpFS2(opsCorpFs2);
                    respDTO.setXDMSHome(rs.getString("XDMSHOME") != null ? rs.getString("XDMSHOME").trim() : null);
                    respDTO.setLinkedGwKey(rs.getString("LINKED_GW_KEY") != null ? rs.getString("LINKED_GW_KEY").trim() : null);
                    respDTO.setDynamicQosFlag(rs.getInt("DYNAMICQOSFLAG"));
                    respDTO.setHierarchyType(KnConstants.HIERARCHY_TYPE.validate(rs.getInt("CORP_HIERARCHY")));
                    respDTO.setWebDispatchEnabled(rs.getInt("WEB_DISPATCH_ENABLED"));
                    respDTO.setSubsDefPttRadio(rs.getInt("SUBSCR_DEF_PTTRADIO"));
                }
                return respDTO;
            }
            return null;
        }, persisterTxn, KnDBConst.DataStores.XDM_SHARED_DATA);

        if (corpProfileInfoDTO != null) {
            knLogger.debug(methodName, "Corporate Profile retrieved: ", corpProfileInfoDTO);
        } else {
            knLogger.debug(methodName, "No Corporate Profile found for extCorpId: ", extCorpId);
        }
        return corpProfileInfoDTO;
    }

    /**
     * Update Corporate Profile Information asynchronously
     *
     * @param corpProfileInfo Corporate profile information to update
     * @param persisterTxn    Database transaction
     * @throws KnDAOException if database operation fails
     */
    public void updateCorporateProfile(KnCorpProfileInfo corpProfileInfo, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateCorporateProfile(KnCorpProfileInfo, KnPersisterTxn)";
        String query = null;
        Connection conn = null;
        PreparedStatement pStmt = null;
        knLogger.debug(methodName, "ENTRY : update Corp Profile");
        try {
            conn = bulkOpsDBUtil.getDBConnection(persisterTxn, KnDBConst.DataStores.XDM_SHARED_DATA);
            int corpId = corpProfileInfo.getCorpId();
            if (null != corpProfileInfo.getCorporateName() && !corpProfileInfo.getCorporateName().isEmpty()) {
                query = "UPDATE DG.POCCORPINFO SET LASTPROFILEUPDATETIME = ? , CORPNAME = ? WHERE CORPID = ?";
                pStmt = conn.prepareStatement(query);
                pStmt.setLong(1, corpProfileInfo.getLastProfileUpdateTime());
                pStmt.setString(2, corpProfileInfo.getCorporateName());
                pStmt.setInt(3, corpId);
                pStmt.executeUpdate();
                knLogger.debug(methodName, "QUERY: Executed ");
            }
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to update Corp Profile - " + e.getMessage(), KnBulkOpsDBUtil.getXdmPttServerId(),
                    KnBulkOpsDAOSourceTypes.CORPINFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }
        knLogger.debug(methodName, "EXIT: update Corp Profile");
    }

    /**
     * Create Corporate Profile - INSERT into POCCORPINFO table
     *
     * Key Steps:
     * 1. Generate new Corp ID from sequence
     * 2. Set creation and update timestamps
     * 3. Build dynamic INSERT query based on non-null fields
     * 4. Execute INSERT with proper parameter binding
     * 5. Handle unique constraint violations (race condition)
     *
     * @param corpProfileInfo Corporate profile information to create
     * @param persisterTxn Database transaction
     * @return Generated Corp ID
     * @throws KnDAOException if database operation fails
     */
    public int createCorporateProfile(KnCorpProfileInfo corpProfileInfo, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "createCorporateProfile(KnCorpProfileInfo, KnPersisterTxn)";
        boolean ownedTxn = false;
        int corpId = -1;
        String query = null;
        knLogger.info(methodName, "ENTRY: Create Corporate Profile ", corpProfileInfo);

        try {
            // Transaction management
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            // Step 1: Generate new Corp ID from sequence table
            KnGenInfoUtil genInfoUtil = KnGenInfoUtil.getInstance();
            corpId = genInfoUtil.retrieveIdForTable(
                    KnBulkOpsDAOSourceTypes.CORPINFO,
                    KnBulkOpsDBUtil.getXdmPttServerId(),
                    "CORPID",
                    false,
                    KnDBConst.DataStores.XDM_SHARED_DATA
            );

            corpProfileInfo.setCorpId(corpId);

            // Step 2: Set timestamps (only lastProfileUpdateTime, profileCreationTime doesn't have setter)
            long profileCreationTime = Calendar.getInstance().getTimeInMillis();
            corpProfileInfo.setLastProfileUpdateTime(profileCreationTime);

            knLogger.debug(methodName, "Generated Corp ID: ", corpId, ", Creating profile");

            // Step 3: Extract fields from DTO
            String extCorpId = corpProfileInfo.getExtCorpId();
            String xdmsHome = corpProfileInfo.getXDMSHome();
            String corpName = corpProfileInfo.getCorporateName();
            String corpFS2 = corpProfileInfo.getCorpFS2();
            String opsCorpFS2 = corpProfileInfo.getOpsCorpFS2();
            int dynamicQosFlag = corpProfileInfo.getDynamicQosFlag();
            int corpHierarchyType = corpProfileInfo.getHierarchyType().value();
            String pocHome = corpProfileInfo.getPocHome();
            String featRelVersion = String.valueOf(corpProfileInfo.getFeatureRelVersion());
            String xdmCorpFS2Set = corpProfileInfo.getXdmCorpFS2Set();

            // Step 4: Build dynamic INSERT query (only for non-null fields)
            ArrayList<String> queryFields = new ArrayList<>();
            queryFields.add("CORPID");
            queryFields.add("EXTCORPID");
            queryFields.add("XDMSHOME");
            queryFields.add("PROFILECREATIONTIME");
            queryFields.add("LASTPROFILEUPDATETIME");
            queryFields.add("POCHOME");

            if (corpName != null) {
                queryFields.add("CORPNAME");
            }

            queryFields.add("CORPFS1");
            queryFields.add("OPSCORPFS1");
            queryFields.add("DYNAMICQOSFLAG");
            queryFields.add("CORP_HIERARCHY");
            queryFields.add("CORPFS2");
            queryFields.add("OPSCORPFS2");
            queryFields.add("FEATURE_REL_VERSION");
            queryFields.add("XDMCORPFS2_SET");

            // Generate INSERT query
            query = KnDbUtil.getInsertQuery(KnBulkOpsDAOSourceTypes.CORPINFO, queryFields);

            try (PreparedStatement pStmt = persisterTxn.getDBConnection(
                    KnBulkOpsDBUtil.getXdmPttServerId(),
                    KnDBConst.DataStores.XDM_SHARED_DATA,
                    false
            ).prepareStatement(query)) {

                // Step 5: Bind parameters
                int columnIndex = 0;
                pStmt.setInt(++columnIndex, corpId);
                pStmt.setString(++columnIndex, extCorpId);
                pStmt.setString(++columnIndex, xdmsHome);
                pStmt.setLong(++columnIndex, profileCreationTime);
                pStmt.setLong(++columnIndex, profileCreationTime); // lastProfileUpdateTime = creationTime
                pStmt.setString(++columnIndex, pocHome);

                if (corpName != null) {
                    pStmt.setString(++columnIndex, corpName);
                }

                pStmt.setLong(++columnIndex, KnGeneralUtil.convertHexStringToLong(corpFS2));
                pStmt.setLong(++columnIndex, KnGeneralUtil.convertHexStringToLong(opsCorpFS2));
                pStmt.setInt(++columnIndex, dynamicQosFlag);
                pStmt.setInt(++columnIndex, corpHierarchyType);
                pStmt.setString(++columnIndex, KnGeneralUtil.getFeatureSet(corpFS2));
                pStmt.setString(++columnIndex, KnGeneralUtil.getFeatureSet(opsCorpFS2));
                pStmt.setString(++columnIndex, featRelVersion);
                pStmt.setString(++columnIndex, KnGeneralUtil.getFeatureSet(xdmCorpFS2Set));

                knLogger.debug(methodName, "QUERY: Executing - ", query);
                pStmt.executeUpdate();
                knLogger.debug(methodName, "QUERY: Executed");
            }

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }

        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQL Exception Occurred: ", sqlE);

            // Handle unique constraint violation (race condition scenario)
            // Error code 1 = unique constraint violation in TimesTen/Oracle
            if (sqlE.getErrorCode() == 1) {
                knLogger.error(methodName, "Unique constraint exception occurred - Race condition detected", sqlE);
                if (ownedTxn) {
                    knLogger.debug(methodName, "Saving the transaction despite constraint violation");
                    persisterTxn.save();
                }
            } else {
                if (ownedTxn) {
                    persisterTxn.rollback();
                }
                throw KnDbUtil.processException(sqlE, "Failed to create Corp Profile - " + sqlE.getMessage(), KnBulkOpsDBUtil.getXdmPttServerId(),
                        KnBulkOpsDAOSourceTypes.CORPINFO, query);
            }
        } catch (Exception e) {
            knLogger.error(methodName, "Failed to create Corporate Profile: ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to create Corp Profile - " + e.getMessage(), KnBulkOpsDBUtil.getXdmPttServerId(),
                    KnBulkOpsDAOSourceTypes.CORPINFO, query);
        }

        knLogger.info(methodName, "EXIT: Corporate Profile created successfully with Corp ID: ", corpId);
        return corpId;
    }

    /**
     * Update LinkedGwKey to NULL for a corporate based on external corporate ID
     * This is used when no Alias/Group MDNs are present in the corporate
     *
     * @param extCorpId External Corporate ID
     * @param persisterTxn Database transaction
     * @throws KnDAOException if database operation fails
     */
    public void updateLinkedGwKeyOfCorp(String extCorpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateLinkedGwKeyOfCorp(String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: Nullifying LinkedGwKey for extCorpId: ", extCorpId);

        String query = "UPDATE " + KnBulkOpsDAOSourceTypes.CORPINFO + " SET LINKED_GW_KEY = ? WHERE EXTCORPID = ?";
        Object[] params = new Object[]{null, extCorpId};

        try {
            bulkOpsDBUtil.executeQuery(query, params, rs -> null, persisterTxn, KnDBConst.DataStores.XDM_SHARED_DATA);
            knLogger.info(methodName, "EXIT: Successfully nullified LinkedGwKey for extCorpId: ", extCorpId);
        } catch (Exception e) {
            knLogger.error(methodName, "Failed to update LinkedGwKey: ", e);
            throw KnDbUtil.processException(e, "Failed to nullify LinkedGwKey for extCorpId: " + extCorpId + e.getMessage(), KnBulkOpsDBUtil.getXdmPttServerId(),
                    KnBulkOpsDAOSourceTypes.CORPINFO, query);
        }
    }

    /**
     * Validates external corporate ID hierarchy
     *
     * @param extCorpId     External Corporate ID to validate
     * @param hierarchyType Expected hierarchy type
     * @param persisterTxn Database transaction
     * @return true if hierarchy matches or extCorpId doesn't exist in DB, false otherwise
     * @throws KnDAOException if database operation fails
     */
    public boolean validateExtCorpCCAndHierarchy(String extCorpId, KnConstants.HIERARCHY_TYPE hierarchyType, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "validateExtCorpCCAndHierarchy(String, KnConstants.HIERARCHY_TYPE)";
        knLogger.debug(methodName, "extCorpId : ", extCorpId, "hierarchyType:", hierarchyType.value());

        String query = "SELECT CORP_HIERARCHY FROM " + KnBulkOpsDAOSourceTypes.CORPINFO + " WHERE EXTCORPID = ?";
        Object[] params = new Object[]{extCorpId};

        KnConstants.HIERARCHY_TYPE corpHierarchyType = bulkOpsDBUtil.executeQuery(query, params, rs -> {
            if (rs.next()) {
                return KnConstants.HIERARCHY_TYPE.validate(rs.getInt("CORP_HIERARCHY"));
            }
            return null;
        }, persisterTxn, KnDBConst.DataStores.XDM_SHARED_DATA);

        if (corpHierarchyType == null) {
            // if ext corpid doesn't exist in DB then return true(assuming valid corporate)
            return true;
        }
        return hierarchyType.equals(corpHierarchyType);
    }

    /**
     * Retrieves external corporate IDs for a list of internal corporate IDs.
     * Used for extCorpId validation in bulk operations.
     *
     * @param corpIds List of internal corporate IDs
     * @param persisterTxn Database transaction
     * @return Map of corpId to extCorpId
     * @throws KnDAOException if database operation fails
     */
    public Map<Integer, String> getExtCorpIdsForCorpIds(List<Integer> corpIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getExtCorpIdsForCorpIds";
        knLogger.debug(methodName, "Fetching extCorpIds for ", corpIds.size(), " corpIds");

        if (corpIds == null || corpIds.isEmpty()) {
            return new HashMap<>();
        }

        // Convert Integer list to String list for IN clause
        List<String> corpIdStrings = new ArrayList<>();
        for (Integer corpId : corpIds) {
            corpIdStrings.add(String.valueOf(corpId));
        }

        String queryPrefix = "SELECT CORPID, EXTCORPID FROM " + KnBulkOpsDAOSourceTypes.CORPINFO + " WHERE CORPID IN ";

        Map<Integer, String> result = bulkOpsDBUtil.executeInClauseQuery(
                queryPrefix,
                corpIdStrings,
                rs -> {
                    Map<Integer, String> resultMap = new HashMap<>();
                    while (rs.next()) {
                        int corpId = rs.getInt("CORPID");
                        String extCorpId = rs.getString("EXTCORPID");
                        if (extCorpId != null) {
                            resultMap.put(corpId, extCorpId.trim());
                        }
                    }
                    return resultMap;
                },
                persisterTxn,
                KnDBConst.DataStores.XDM_SHARED_DATA
        );

        knLogger.debug(methodName, "Fetched ", result.size(), " extCorpIds");
        return result;
    }

    /**
     * Updates corporate profile last update time and optionally corporate name for a given corpId.
     * This is a synchronous update within the transaction, used for bulk operations.
     * Aligned with Single MDN flow (KnSubsProvController.updateSubscriber line 3044).
     *
     * @param corpId Corporate ID
     * @param corporateName New corporate name (can be null to skip name update)
     * @param persisterTxn Database transaction
     * @return true if updated successfully
     * @throws KnDAOException if database operation fails
     */
    public boolean updateCorporateProfileSync(int corpId, String corporateName, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateCorporateProfileSync";
        knLogger.debug(methodName, "Updating corporate profile for corpId: ", corpId);

        if (corpId <= 0) {
            return false;
        }

        long profileUpdateTime = System.currentTimeMillis();
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("UPDATE ").append(KnBulkOpsDAOSourceTypes.CORPINFO).append(" SET LASTPROFILEUPDATETIME = ?");

        if (corporateName != null && !corporateName.trim().isEmpty()) {
            queryBuilder.append(", CORPNAME = ?");
        }

        queryBuilder.append(" WHERE CORPID = ?");

        Connection conn = null;
        PreparedStatement pStmt = null;
        try {
            conn = bulkOpsDBUtil.getDBConnection(persisterTxn, KnDBConst.DataStores.XDM_SHARED_DATA);
            pStmt = conn.prepareStatement(queryBuilder.toString());

            int paramIndex = 1;
            pStmt.setLong(paramIndex++, profileUpdateTime);

            if (corporateName != null && !corporateName.trim().isEmpty()) {
                pStmt.setString(paramIndex++, corporateName.trim());
            }

            pStmt.setInt(paramIndex, corpId);

            int updated = pStmt.executeUpdate();
            knLogger.info(methodName, "Corporate profile updated for corpId ", corpId, ", rows affected: ", updated);
            return updated > 0;

        } catch (Exception e) {
            knLogger.error(methodName, "Exception updating corporate profile: ", e);
            throw KnDbUtil.processException(e, "Failed to update corporate profile: " + e.getMessage(),
                    KnBulkOpsDBUtil.getXdmPttServerId(), KnBulkOpsDAOSourceTypes.CORPINFO, queryBuilder.toString());
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }
    }

    /**
     * Data class representing Corporate Profile Information
     */
    public static class KnCorpProfileInfo {
        private String extCorpId;
        private int corpId;
        private String XDMSHome;
        private String corporateName;
        private int maxSubscribers;
        private int maxCorpLists;
        private int maxMembersPerCorpList;
        private int maxCorpGroups;
        private int maxMembersPerCorpGroup;
        private int pairedContactListId;
        private String pocHome;
        private String linkedGwKey;
        private int dynamicQosFlag;
        private KnConstants.HIERARCHY_TYPE hierarchyType;
        private int webDispatchEnabled;
        private int subsDefPttRadio;
        private String corpFS2;
        private String opsCorpFS2;
        private String featureRelVersion;
        private int maxDispatchGroups;
        private int maxMemPerDispatchGroup;
        private Boolean dispatchEnabled;
        private Boolean isInterOpEnabled;
        private int maxExtSubsPerCorp;
        private int maxMemPerBCGrp;
        private int maxRadioChannels;
        private int maxZones;
        private int maxChannelsPerZone;
        private int maxLargeTalkGroup;
        private int maxGroupProfile;
        private int maxUserProfile;
        private int maxAssignProfiles;
        private int maxCorpHierarchyLevel;
        private String xdmCorpFS2Set;
        private int dispatchType;
        private long lastProfileUpdateTime;

        // Getters and Setters
        public KnConstants.HIERARCHY_TYPE getHierarchyType() {
            return hierarchyType;
        }
        public void setHierarchyType(KnConstants.HIERARCHY_TYPE hierarchyType) {
            this.hierarchyType = hierarchyType;
        }

        public String getExtCorpId() {
            return extCorpId;
        }
        public void setExtCorpId(String extCorpId) {
            if (extCorpId != null) {
                extCorpId = extCorpId.trim();
                if (extCorpId.isEmpty()) {
                    extCorpId = null;
                }
            }
            this.extCorpId = extCorpId;
        }

        public int getCorpId() {
            return corpId;
        }
        public void setCorpId(int corpId) {
            this.corpId = corpId;
        }

        public String getXDMSHome() {
            return XDMSHome;
        }
        public void setXDMSHome(String XDMSHome) {
            if (XDMSHome != null) {
                XDMSHome = XDMSHome.trim();
                if (XDMSHome.isEmpty()) {
                    XDMSHome = null;
                }
            }
            this.XDMSHome = XDMSHome;
        }

        public String getCorporateName() {
            return corporateName;
        }
        public void setCorporateName(String corporateName) {
            this.corporateName = corporateName;
        }

        public int getMaxSubscribers() {
            return maxSubscribers;
        }
        public void setMaxSubscribers(int maxSubscribers) {
            this.maxSubscribers = maxSubscribers;
        }

        public int getMaxCorpLists() {
            return maxCorpLists;
        }
        public void setMaxCorpLists(int maxCorpLists) {
            this.maxCorpLists = maxCorpLists;
        }

        public int getMaxMembersPerCorpList() {
            return maxMembersPerCorpList;
        }
        public void setMaxMembersPerCorpList(int maxMembersPerCorpList) {
            this.maxMembersPerCorpList = maxMembersPerCorpList;
        }

        public int getMaxCorpGroups() {
            return maxCorpGroups;
        }
        public void setMaxCorpGroups(int maxCorpGroups) {
            this.maxCorpGroups = maxCorpGroups;
        }

        public int getMaxMembersPerCorpGroup() {
            return maxMembersPerCorpGroup;
        }
        public void setMaxMembersPerCorpGroup(int maxMembersPerCorpGroup) {
            this.maxMembersPerCorpGroup = maxMembersPerCorpGroup;
        }

        public int getPairedContactListId() {
            return pairedContactListId;
        }
        public void setPairedContactListId(int pairedContactListId) {
            this.pairedContactListId = pairedContactListId;
        }

        public String getPocHome() {
            return pocHome;
        }
        public void setPocHome(String pocHome) {
            if (pocHome != null) {
                pocHome = pocHome.trim();
                if (pocHome.isEmpty()){
                    pocHome = null;
                }
            }
            this.pocHome = pocHome;
        }

        public String getLinkedGwKey() {
            return linkedGwKey;
        }
        public void setLinkedGwKey(String linkedGwKey) {
            this.linkedGwKey = linkedGwKey;
        }

        public int getDynamicQosFlag() {
            return dynamicQosFlag;
        }
        public void setDynamicQosFlag(int dynamicQosFlag) {
            this.dynamicQosFlag = dynamicQosFlag;
        }

        public int getWebDispatchEnabled() {
            return webDispatchEnabled;
        }
        public void setWebDispatchEnabled(int webDispatchEnabled) {
            this.webDispatchEnabled = webDispatchEnabled;
        }

        public int getSubsDefPttRadio() {
            return subsDefPttRadio;
        }
        public void setSubsDefPttRadio(int subsDefPttRadio) {
            this.subsDefPttRadio = subsDefPttRadio;
        }

        public String getCorpFS2() {
            return corpFS2;
        }
        public void setCorpFS2(String corpFS2) {
            this.corpFS2 = corpFS2;
        }

        public String getOpsCorpFS2() {
            return opsCorpFS2;
        }
        public void setOpsCorpFS2(String opsCorpFS2) {
            this.opsCorpFS2 = opsCorpFS2;
        }

        public String getFeatureRelVersion() {
            return featureRelVersion;
        }
        public void setFeatureRelVersion(String featureRelVersion) {
            this.featureRelVersion = featureRelVersion;
        }

        public int getMaxDispatchGroups() {
            return maxDispatchGroups;
        }
        public void setMaxDispatchGroups(int maxDispatchGroups) {
            this.maxDispatchGroups = maxDispatchGroups;
        }

        public int getMaxMemPerDispatchGroup() {
            return maxMemPerDispatchGroup;
        }
        public void setMaxMemPerDispatchGroup(int maxMemPerDispatchGroup) {
            this.maxMemPerDispatchGroup = maxMemPerDispatchGroup;
        }

        public Boolean getDispatchEnabled() {
            return dispatchEnabled;
        }
        public void setDispatchEnabled(Boolean dispatchEnabled) {
            this.dispatchEnabled = dispatchEnabled;
        }

        public Boolean getIsInterOpEnabled() {
            return isInterOpEnabled;
        }
        public void setIsInterOpEnabled(Boolean isInterOpEnabled) {
            this.isInterOpEnabled = isInterOpEnabled;
        }

        public int getMaxExtSubsPerCorp() {
            return maxExtSubsPerCorp;
        }
        public void setMaxExtSubsPerCorp(int maxExtSubsPerCorp) {
            this.maxExtSubsPerCorp = maxExtSubsPerCorp;
        }

        public int getMaxMemPerBCGrp() {
            return maxMemPerBCGrp;
        }
        public void setMaxMemPerBCGrp(int maxMemPerBCGrp) {
            this.maxMemPerBCGrp = maxMemPerBCGrp;
        }

        public int getMaxRadioChannels() {
            return maxRadioChannels;
        }
        public void setMaxRadioChannels(int maxRadioChannels) {
            this.maxRadioChannels = maxRadioChannels;
        }

        public int getMaxZones() {
            return maxZones;
        }
        public void setMaxZones(int maxZones) {
            this.maxZones = maxZones;
        }

        public int getMaxChannelsPerZone() {
            return maxChannelsPerZone;
        }
        public void setMaxChannelsPerZone(int maxChannelsPerZone) {
            this.maxChannelsPerZone = maxChannelsPerZone;
        }

        public int getMaxLargeTalkGroup() {
            return maxLargeTalkGroup;
        }
        public void setMaxLargeTalkGroup(int maxLargeTalkGroup) {
            this.maxLargeTalkGroup = maxLargeTalkGroup;
        }

        public int getMaxGroupProfile() {
            return maxGroupProfile;
        }
        public void setMaxGroupProfile(int maxGroupProfile) {
            this.maxGroupProfile = maxGroupProfile;
        }

        public int getMaxUserProfile() {
            return maxUserProfile;
        }
        public void setMaxUserProfile(int maxUserProfile) {
            this.maxUserProfile = maxUserProfile;
        }

        public int getMaxAssignProfiles() {
            return maxAssignProfiles;
        }
        public void setMaxAssignProfiles(int maxAssignProfiles) {
            this.maxAssignProfiles = maxAssignProfiles;
        }

        public int getMaxCorpHierarchyLevel() {
            return maxCorpHierarchyLevel;
        }
        public void setMaxCorpHierarchyLevel(int maxCorpHierarchyLevel) {
            this.maxCorpHierarchyLevel = maxCorpHierarchyLevel;
        }

        public String getXdmCorpFS2Set() {
            return xdmCorpFS2Set;
        }
        public void setXdmCorpFS2Set(String xdmCorpFS2Set) {
            this.xdmCorpFS2Set = xdmCorpFS2Set;
        }

        public int getDispatchType() {
            return dispatchType;
        }
        public void setDispatchType(int dispatchType) {
            this.dispatchType = dispatchType;
        }

        public long getLastProfileUpdateTime() {
            return lastProfileUpdateTime;
        }
        public void setLastProfileUpdateTime(long lastProfileUpdateTime) {
            this.lastProfileUpdateTime = lastProfileUpdateTime;
        }

        @Override
        public String toString() {
            return "KnBulkOpsCorpProfileDAO{" +
                    "extCorpId='" + extCorpId + '\'' +
                    ", corpId=" + corpId +
                    ", XDMSHome='" + XDMSHome + '\'' +
                    ", corporateName='" + corporateName + '\'' +
                    ", maxSubscribers=" + maxSubscribers +
                    ", maxCorpLists=" + maxCorpLists +
                    ", maxMembersPerCorpList=" + maxMembersPerCorpList +
                    ", maxCorpGroups=" + maxCorpGroups +
                    ", maxMembersPerCorpGroup=" + maxMembersPerCorpGroup +
                    ", pairedContactListId=" + pairedContactListId +
                    ", pocHome='" + pocHome + '\'' +
                    ", linkedGwKey='" + linkedGwKey + '\'' +
                    ", dynamicQosFlag=" + dynamicQosFlag +
                    ", hierarchyType=" + hierarchyType +
                    ", webDispatchEnabled=" + webDispatchEnabled +
                    ", subsDefPttRadio=" + subsDefPttRadio +
                    ", corpFS2='" + corpFS2 + '\'' +
                    ", opsCorpFS2='" + opsCorpFS2 + '\'' +
                    ", featureRelVersion='" + featureRelVersion + '\'' +
                    ", maxDispatchGroups=" + maxDispatchGroups +
                    ", maxMemPerDispatchGroup=" + maxMemPerDispatchGroup +
                    ", maxExtSubsPerCorp=" + maxExtSubsPerCorp +
                    ", dispatchEnabled=" + dispatchEnabled +
                    ", isInterOpEnabled=" + isInterOpEnabled +
                    ", maxMemPerBCGrp=" + maxMemPerBCGrp +
                    ", maxRadioChannels=" + maxRadioChannels +
                    ", maxZones=" + maxZones +
                    ", maxChannelsPerZone=" + maxChannelsPerZone +
                    ", maxLargeTalkGroup=" + maxLargeTalkGroup +
                    ", maxGroupProfile=" + maxGroupProfile +
                    ", maxUserProfile=" + maxUserProfile +
                    ", maxAssignProfiles=" + maxAssignProfiles +
                    ", maxCorpHiearchyLevel=" + maxCorpHierarchyLevel +
                    ", xdmCorpFS2Set='" + xdmCorpFS2Set + '\'' +
                    ", dispatchType=" + dispatchType +
                    ", lastProfileUpdateTime=" + lastProfileUpdateTime +
                    '}';
        }

    }

}


