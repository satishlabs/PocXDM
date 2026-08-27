package com.kodiak.xdms.server.bulkops.dao;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.commdto.common.KnXDMDeviceProvDTO;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.bulkops.KnBulkOpsException;
import com.kodiak.xdms.server.bulkops.resources.KnBulkOpsDAOSourceTypes;
import com.kodiak.xdms.server.bulkops.utils.KnBulkOpsDBUtil;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * DAO for bulk device operations - handles device info and device IMPI info operations for multiple MDNs
 */
public class KnBulkOpsDeviceInfoDAO {
    private static final KnLogger knLogger = KnLogger.getLogger(KnBulkOpsDeviceInfoDAO.class);
    private final KnBulkOpsDBUtil bulkOpsDBUtil;

    public KnBulkOpsDeviceInfoDAO() throws KnBulkOpsException {
        bulkOpsDBUtil = KnBulkOpsDBUtil.getInstance();
    }

    /**
     * Select device profiles by device IDs (MDNs) in bulk
     *
     * @param mdnList      List of MDNs (device IDs)
     * @param persisterTxn Database transaction
     * @return Map of MDN to KnXDMDeviceProvDTO
     * @throws KnDAOException if database operation fails
     */
    public Map<String, KnXDMDeviceProvDTO> selectDeviceProfilesByDeviceIds(
            List<String> mdnList,
            KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "selectDeviceProfilesByDeviceIds(List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: Retrieving device profiles for ", mdnList.size(), " MDNs");

        String queryPrefix = "SELECT DEVICEID, DEVICESTATUS, DEVICEACTTS, DEVICELASTUSED, " +
                "DEVICEDIGESTPASSWD, DEVICE_CREATED_AS, DEVICE_CLIENTID, DEVICE_SHARED, " +
                "DEVICE_IMPI, DEVICE_TYPE, DEVICE_NAME, REQ_DEVICEID, CORPID, DEVICE_SUBSCR_MDN " +
                "FROM " + KnBulkOpsDAOSourceTypes.DEVICEINFO + " WHERE DEVICEID IN ";

        Map<String, KnXDMDeviceProvDTO> deviceProfileMap = bulkOpsDBUtil.executeInClauseQuery(
                queryPrefix,
                mdnList,
                rs -> {
                    Map<String, KnXDMDeviceProvDTO> resultMap = new LinkedHashMap<>();
                    while (rs.next()) {
                        KnXDMDeviceProvDTO deviceProfile = new KnXDMDeviceProvDTO();
                        deviceProfile.setDeviceId(rs.getString("DEVICEID"));
                        deviceProfile.setDeviceStatus(rs.getInt("DEVICESTATUS"));
                        deviceProfile.setDeviceActTimeStamp(rs.getLong("DEVICEACTTS"));
                        deviceProfile.setDeviceLastUsed(rs.getLong("DEVICELASTUSED"));
                        deviceProfile.setDevicePassword(rs.getString("DEVICEDIGESTPASSWD"));
                        deviceProfile.setDeviceCreatedAs(rs.getInt("DEVICE_CREATED_AS"));
                        deviceProfile.setDeviceClientId(rs.getString("DEVICE_CLIENTID"));
                        deviceProfile.setDeviceShared(rs.getInt("DEVICE_SHARED"));
                        deviceProfile.setDeviceIMPI(rs.getString("DEVICE_IMPI"));
                        deviceProfile.setDeviceType(rs.getInt("DEVICE_TYPE"));
                        deviceProfile.setDeviceName(rs.getString("DEVICE_NAME"));
                        deviceProfile.setReqDeviceId(rs.getString("REQ_DEVICEID"));
                        deviceProfile.setCorpId(rs.getInt("CORPID"));
                        deviceProfile.setDeviceSubscrMdn(rs.getString("DEVICE_SUBSCR_MDN"));
                        resultMap.put(deviceProfile.getDeviceId(), deviceProfile);
                    }
                    return resultMap;
                },
                persisterTxn,
                KnDBConst.DataStores.XDM_SHARED_DATA
        );

        knLogger.debug(methodName, "EXIT: Retrieved ", deviceProfileMap.size(), " device profiles");
        return deviceProfileMap;
    }

    /**
     * Delete device info records in bulk
     *
     * @param mdnList      List of MDNs (device IDs) to delete
     * @param persisterTxn Database transaction
     * @throws KnDAOException if database operation fails
     */
    public void deleteDeviceInfoBulk(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteDeviceInfoBulk(List<String>, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY: Deleting device info for ", mdnList.size(), " MDNs");

        String deleteQuery = "DELETE FROM " + KnBulkOpsDAOSourceTypes.DEVICEINFO + " WHERE DEVICEID IN ";

        bulkOpsDBUtil.executeInClauseQuery(
                deleteQuery,
                mdnList,
                rs -> null, // No result set for DELETE
                persisterTxn,
                KnDBConst.DataStores.XDM_SHARED_DATA
        );

        knLogger.info(methodName, "EXIT: Deleted device info for ", mdnList.size(), " MDNs");
    }

    /**
     * Delete device IMPI info records in bulk
     *
     * @param mdnList List of MDNs (device IDs) to delete IMPI info for
     * @param persisterTxn Database transaction
     * @throws KnDAOException if database operation fails
     */
    public void deleteDeviceImpiInfoBulk(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteDeviceImpiInfoBulk(List<String>, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY: Deleting device IMPI info for ", mdnList.size(), " devices");

        // Build the IN clause placeholders for the subquery
        // Delete DEVICEIMPIINFO records by finding DEVICE_IMPI values from DEVICE_INFO table
        // where DEVICEID (MDN) matches the provided list
        int size = mdnList.size();
        String placeholders = String.join(",", java.util.Collections.nCopies(size, "?"));

        String deleteQuery = "DELETE FROM " + KnBulkOpsDAOSourceTypes.DEVICEIMPIINFO + " WHERE DEVICE_IMPI IN " +
                "( SELECT DEVICE_IMPI FROM " + KnBulkOpsDAOSourceTypes.DEVICEINFO + " WHERE DEVICEID IN (" + placeholders + ") )";

        // Convert list to Object array for executeQuery
        Object[] params = mdnList.toArray();

        bulkOpsDBUtil.executeQuery(
                deleteQuery,
                params,
                rs -> null, // No result set for DELETE
                persisterTxn,
                KnDBConst.DataStores.XDM_SHARED_DATA
        );

        knLogger.info(methodName, "EXIT: Deleted device IMPI info for ", mdnList.size(), " devices");
    }

    /**
     * Create device info records in bulk using batch insert
     *
     * @param deviceInfoList List of device info DTOs to insert
     * @param persisterTxn   Database transaction
     * @throws KnDAOException if database operation fails
     */
    public void createDeviceInfoBulk(List<KnBulkDeviceInfoDTO> deviceInfoList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "createDeviceInfoBulk(List<KnBulkDeviceInfoDTO>, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY: Creating device info for ", deviceInfoList.size(), " devices");

        if (deviceInfoList == null || deviceInfoList.isEmpty()) {
            knLogger.debug(methodName, "No device info to create, returning");
            return;
        }

        ArrayList<String> queryFields = new ArrayList<>();
        queryFields.add("DEVICEID");
        queryFields.add("DEVICESTATUS");
        queryFields.add("DEVICEACTTS");
        queryFields.add("DEVICELASTUSED");
        queryFields.add("DEVICEDIGESTPASSWD");
        queryFields.add("DEVICE_CREATED_AS");
        queryFields.add("DEVICE_CLIENTID");
        queryFields.add("DEVICE_SHARED");
        queryFields.add("DEVICE_IMPI");
        queryFields.add("DEVICE_TYPE");
        queryFields.add("DEVICE_NAME");
        queryFields.add("REQ_DEVICEID");
        queryFields.add("CORPID");
        queryFields.add("DEVICE_SUBSCR_MDN");

        String query = KnDbUtil.getInsertQuery(KnBulkOpsDAOSourceTypes.DEVICEINFO, queryFields);
        knLogger.debug(methodName, "Insert query: ", query);

        try {
            List<Object[]> batchParams = new ArrayList<>();

            for (KnBulkDeviceInfoDTO deviceInfo : deviceInfoList) {
                List<Object> params = new ArrayList<>();
                params.add(deviceInfo.getDeviceId());
                params.add(deviceInfo.getDeviceStatus());
                params.add(deviceInfo.getDeviceActTimeStamp());
                params.add(deviceInfo.getDeviceLastUsed());
                params.add(deviceInfo.getDeviceDigestPassword());
                params.add(deviceInfo.getDeviceCreatedAs());
                params.add(deviceInfo.getDeviceClientId());
                params.add(deviceInfo.getDeviceShared());

                if (deviceInfo.getDeviceIMPI() != null) {
                    params.add(deviceInfo.getDeviceIMPI().getBytes(StandardCharsets.UTF_8));
                } else {
                    params.add(null);
                }

                params.add(deviceInfo.getDeviceType());

                if (deviceInfo.getDeviceName() != null) {
                    params.add(deviceInfo.getDeviceName().getBytes(StandardCharsets.UTF_8));
                } else {
                    params.add(null);
                }

                params.add(deviceInfo.getReqDeviceId());

                if (deviceInfo.getCorpId() != null && deviceInfo.getCorpId() != 0) {
                    params.add(deviceInfo.getCorpId());
                } else {
                    params.add(null);
                }

                if (deviceInfo.getDeviceSubscriberMdn() != null) {
                    params.add(deviceInfo.getDeviceSubscriberMdn().trim());
                } else {
                    params.add(null);
                }

                batchParams.add(params.toArray());
            }

            // Use common utility for batch execution
            bulkOpsDBUtil.executeBatchUpdate(query, batchParams, persisterTxn, KnDBConst.DataStores.XDM_SHARED_DATA);

            knLogger.info(methodName, "Successfully inserted ", batchParams.size(), " device info records");

        } catch (Exception e) {
            knLogger.error(methodName, "Exception during bulk device info creation: ", e);
            throw KnDbUtil.processException(e, "Failed to create bulk device info: " + e.getMessage(), KnBulkOpsDBUtil.getXdmPttServerId(),
                    KnBulkOpsDAOSourceTypes.DEVICEINFO, query);
        }
        knLogger.info(methodName, "EXIT: Bulk device info creation completed");
    }

    /**
     * Create device IMPI info records in bulk using batch insert
     *
     * @param deviceImpiInfoList List of device IMPI info DTOs to insert
     * @param persisterTxn       Database transaction
     * @throws KnDAOException if database operation fails
     */
    public void createDeviceImpiInfoBulk(List<KnBulkDeviceImpiInfoDTO> deviceImpiInfoList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "createDeviceImpiInfoBulk(List<KnBulkDeviceImpiInfoDTO>, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY: Creating device IMPI info for ", deviceImpiInfoList.size(), " devices");

        if (deviceImpiInfoList == null || deviceImpiInfoList.isEmpty()) {
            knLogger.debug(methodName, "No device IMPI info to create, returning");
            return;
        }

        String query = "INSERT INTO " + KnBulkOpsDAOSourceTypes.DEVICEIMPIINFO + " (DEVICE_IMPI, DEVICE_IMPU) VALUES (?, ?)";
        knLogger.debug(methodName, "Insert query: ", query);

        try {
            // Prepare batch parameters
            List<Object[]> batchParams = new ArrayList<>();
            for (KnBulkDeviceImpiInfoDTO deviceImpiInfo : deviceImpiInfoList) {
                batchParams.add(new Object[]{
                    deviceImpiInfo.getDeviceImpi().getBytes(StandardCharsets.UTF_8),
                    deviceImpiInfo.getDeviceImpu().getBytes(StandardCharsets.UTF_8)
                });
            }

            // Use common utility for batch execution
            bulkOpsDBUtil.executeBatchUpdate(query, batchParams, persisterTxn, KnDBConst.DataStores.XDM_SHARED_DATA);

            knLogger.info(methodName, "Successfully inserted ", batchParams.size(), " device IMPI info records");

        } catch (Exception e) {
            knLogger.error(methodName, "Exception during bulk device IMPI info creation: ", e);
            throw KnDbUtil.processException(e, "Failed to create bulk device IMPI info: " + e.getMessage(), KnBulkOpsDBUtil.getXdmPttServerId(),
                    KnBulkOpsDAOSourceTypes.DEVICEIMPIINFO, query);
        }

        knLogger.info(methodName, "EXIT: Bulk device IMPI info creation completed");
    }

    /**
     * DTO for bulk device info operations
     */
    public static class KnBulkDeviceInfoDTO {
        private String deviceId;
        private Integer deviceStatus;
        private Long deviceActTimeStamp;
        private Long deviceLastUsed;
        private String deviceDigestPassword;
        private Integer deviceCreatedAs;
        private String deviceClientId;
        private Integer deviceShared;
        private String deviceIMPI;
        private Integer deviceType;
        private String deviceName;
        private String reqDeviceId;
        private Integer corpId;
        private String deviceSubscriberMdn;

        // Getters and Setters
        public String getDeviceId() { return deviceId; }
        public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

        public Integer getDeviceStatus() { return deviceStatus; }
        public void setDeviceStatus(Integer deviceStatus) { this.deviceStatus = deviceStatus; }

        public Long getDeviceActTimeStamp() { return deviceActTimeStamp; }
        public void setDeviceActTimeStamp(Long deviceActTimeStamp) { this.deviceActTimeStamp = deviceActTimeStamp; }

        public Long getDeviceLastUsed() { return deviceLastUsed; }
        public void setDeviceLastUsed(Long deviceLastUsed) { this.deviceLastUsed = deviceLastUsed; }

        public String getDeviceDigestPassword() { return deviceDigestPassword; }
        public void setDeviceDigestPassword(String deviceDigestPassword) { this.deviceDigestPassword = deviceDigestPassword; }

        public Integer getDeviceCreatedAs() { return deviceCreatedAs; }
        public void setDeviceCreatedAs(Integer deviceCreatedAs) { this.deviceCreatedAs = deviceCreatedAs; }

        public String getDeviceClientId() { return deviceClientId; }
        public void setDeviceClientId(String deviceClientId) { this.deviceClientId = deviceClientId; }

        public Integer getDeviceShared() { return deviceShared; }
        public void setDeviceShared(Integer deviceShared) { this.deviceShared = deviceShared; }

        public String getDeviceIMPI() { return deviceIMPI; }
        public void setDeviceIMPI(String deviceIMPI) { this.deviceIMPI = deviceIMPI; }

        public Integer getDeviceType() { return deviceType; }
        public void setDeviceType(Integer deviceType) { this.deviceType = deviceType; }

        public String getDeviceName() { return deviceName; }
        public void setDeviceName(String deviceName) { this.deviceName = deviceName; }

        public String getReqDeviceId() { return reqDeviceId; }
        public void setReqDeviceId(String reqDeviceId) { this.reqDeviceId = reqDeviceId; }

        public Integer getCorpId() { return corpId; }
        public void setCorpId(Integer corpId) { this.corpId = corpId; }

        public String getDeviceSubscriberMdn() { return deviceSubscriberMdn; }
        public void setDeviceSubscriberMdn(String deviceSubscriberMdn) { this.deviceSubscriberMdn = deviceSubscriberMdn; }

        @Override
        public String toString() {
            return "KnBulkDeviceInfoDTO{" +
                    "deviceId='" + deviceId + '\'' +
                    ", deviceStatus=" + deviceStatus +
                    ", deviceType=" + deviceType +
                    ", corpId=" + corpId +
                    '}';
        }
    }

    /**
     * DTO for bulk device IMPI info operations
     */
    public static class KnBulkDeviceImpiInfoDTO {
        private String deviceImpi;
        private String deviceImpu;

        // Getters and Setters
        public String getDeviceImpi() { return deviceImpi; }
        public void setDeviceImpi(String deviceImpi) { this.deviceImpi = deviceImpi; }

        public String getDeviceImpu() { return deviceImpu; }
        public void setDeviceImpu(String deviceImpu) { this.deviceImpu = deviceImpu; }

        @Override
        public String toString() {
            return "KnBulkDeviceImpiInfoDTO{" +
                    "deviceImpi='" + deviceImpi + '\'' +
                    ", deviceImpu='" + deviceImpu + '\'' +
                    '}';
        }
    }
}

