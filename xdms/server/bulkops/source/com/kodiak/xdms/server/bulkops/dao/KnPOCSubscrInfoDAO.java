package com.kodiak.xdms.server.bulkops.dao;

import com.kodiak.common.commdto.response.KnBulkOpsErrorDetail;
import com.kodiak.common.dao.*;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.bulkops.KnBulkOpsException;
import com.kodiak.xdms.server.bulkops.dto.common.KnBulkUpdateStatusDTO;
import com.kodiak.xdms.server.bulkops.dto.common.KnOPSubsProfileInfoDTO;
import com.kodiak.xdms.server.bulkops.dto.common.KnSubsProfileDTO;
import com.kodiak.xdms.server.bulkops.dto.common.KnSubsProfilePersistDTO;
import com.kodiak.xdms.server.bulkops.resources.KnBulkOpsDAOSourceTypes;
import com.kodiak.xdms.server.bulkops.utils.KnBulkOpsDBUtil;
import com.kodiak.xdms.server.common.dto.common.KnDocChangeListDTO;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvDAOSourceTypes;


import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;

import static com.kodiak.xdms.server.common.resources.KnConstants.PROTOCOL_VERSION_18_X;
import static com.kodiak.xdms.server.common.resources.KnConstants.USER_PROFILE_MGMT_BIT;
import static com.kodiak.xdms.server.common.util.KnGeneralUtil.buildMCSDOC;

public class KnPOCSubscrInfoDAO {
    KnBulkOpsDBUtil bulkOpsDBUtil =null;
    private static final KnLogger knLogger = KnLogger.getLogger(KnPOCSubscrInfoDAO.class);
    private String pttServerId;
    //query prefixes
    private static final String SELECT_SERVICE_AUTH_STATUS_IN_PREFIX = "SELECT  MDN, SERVICEAUTHSTATUS FROM " +
            " DG.POCSUBSCRINFO WHERE MDN IN ";
    private static final String SELECT_SUBSCRIBER_PROFILES_BY_MDNS_PREFIX = "SELECT MDN, POCHOME, PRESENCEHOME," +
            " XDMSHOME, SERVICEAUTHSTATUS, PUBLICSUBSCRIPTIONTYPE, CORPSUBSCRIPTIONTYPE, CORPID, " +
            "CORPCONTACTPAIRINGIND, IMEI, AFFLIATEID, PAYTYPE, SUBSCRCREATIONTIME, LASTPROFILEUPDATETIME," +
            " CORPCONTACTLISTID, USERAGENT, CLIENT_PASSWORD, EMAIL, CLIENT_TYPE, DISPATCH_GRP_MEMBER, SUBSCRIBERFS1," +
            " CLIENTFS1, ACTIVEFS1, OPSFS1, CLIENTPV_MAJORVERSION, CLIENTPV_MINORVERSION, ACCOUNT_ID," +
            " PAMACCID, CORPADMINFS1, VOCODERID, ADDLINFO, LAST_ACTIVATION_TIME, DISPATCH_TYPE, USER_ID, " +
            "DERIVED_KEY, SUBSCRNAME, UFMI, iDI_USERNAME, iDI_E_PASSWORD, iDI_BUID, SERVICE_STATUS_OP," +
            " SERVICE_STATUS_AUTHUSER, XDMSFS1, LICENSE_TYPE, ALIAS_MDN, QPPPACKID, SEGMENT_INDICATOR, MCPTT_COMPLIANCE, " +
            "SUBSCRIBERFS2, CLIENTFS2, ACTIVEFS2, OPSFS2, CORPADMINFS2, XDMSFS2, MC_ID, MC_PTTID, MC_VIDEOID, " +
            "MC_DATAID, USERPROFILEINDEX, ISDEFAULTPROFILE, FEATURE_REL_VERSION, CLIENT_SW_INFO, " +
            "CLIENT_PLATFORM_TYPE, DYNAMICQOSFLAG, DERIVED_KEY, USERPROFILEFS2, USERPROFILEID, CAMERA_TYPE, " +
            "PRIVACY_OPT_STATUS, PREVSERVICEAUTHSTATUS, GW_ID FROM DG.POCSUBSCRINFO WHERE MDN IN ";

    private static final String SELECT_MDNS_BY_BASE_MDNS = "SELECT A.MDN as BASE_MDN, B.MDN as ASSOC_MDN FROM" +
            " DG.POCSUBSCRINFO A, DG.POCSUBSCRINFO B WHERE A.MC_ID = B.MC_ID AND A.MDN IN ";

    private static final String UPDATE_SERVICE_AUTH_STATUS_SQL =
            "UPDATE DG.POCSUBSCRINFO SET " +
                    "SERVICEAUTHSTATUS = ?, " +
                    "CLIENT_PASSWORD = ?, " +
                    "USERAGENT = ?, " +
                    "LASTPROFILEUPDATETIME = ?, " +
                    "SERVICE_STATUS_OP = ?, " +
                    "PREVSERVICEAUTHSTATUS = ?, " +
                    "PREVAUTHSTATUSUPDATETIME = ? " +
                    "WHERE MDN = ?";
    private static final String QRY_SELECT_SUBSCRIBERS_PV = "SELECT MDN, CLIENTPV_MAJORVERSION FROM DG.POCSUBSCRINFO" +
            " WHERE MDN IN ";

    private static final String GET_SUBS_MCID_UPDTS_BY_MDNS = "SELECT MDN,MC_ID,LASTPROFILEUPDATETIME,MCPTT_COMPLIANCE" +
            ",USERPROFILEINDEX,CLIENTPV_MAJORVERSION,ACTIVEFS2 FROM DG.POCSUBSCRINFO WHERE MDN IN ";
    private static final String UPDATE_SUBS_PROFILE_LAST_UPDATE_TIME = "UPDATE DG.POCSUBSCRINFO SET LASTPROFILEUPDATETIME = ? WHERE MDN  = ?";
    private static final String MDN = "MDN";
    private static final String POC_HOME = "POCHOME";
    private static final String PRESENCE_HOME = "PRESENCEHOME";
    private static final String XDMS_HOME = "XDMSHOME";
    private static final String SUBSCR_CREATION_TIME = "SUBSCRCREATIONTIME";
    private static final String LAST_PROFILE_UPDATE_TIME = "LASTPROFILEUPDATETIME";
    private static final String SUBSCR_NAME = "SUBSCRNAME";
    private static final String SERVICE_AUTH_STATUS = "SERVICEAUTHSTATUS";
    private static final String PUBLIC_SUBSCRIPTION_TYPE = "PUBLICSUBSCRIPTIONTYPE";
    private static final String CORP_SUBSCRIPTION_TYPE = "CORPSUBSCRIPTIONTYPE";
    private static final String CORP_ID = "CORPID";
    private static final String CORPCONTACTLISTID = "CORPCONTACTLISTID";
    private static final String CORP_CONTACT_PAIRING_IND = "CORPCONTACTPAIRINGIND";
    private static final String IMEI = "IMEI";
    private static final String CLIENT_PASSWORD = "CLIENT_PASSWORD";
    private static final String PAY_TYPE = "PAYTYPE";
    private static final String AFFILIATE_ID = "AFFLIATEID";
    private static final String USER_AGENT = "USERAGENT";
    private static final String EMAIL = "EMAIL";
    private static final String CLIENT_TYPE = "CLIENT_TYPE";
    private static final String DISPATCH_GRP_MEMBER = "DISPATCH_GRP_MEMBER";
    private static final String ACTIVE_FS1 = "ACTIVEFS1";
    private static final String CLIENT_FS1 = "CLIENTFS1";
    private static final String SUBS_FS1 = "SUBSCRIBERFS1";
    private static final String OPS_FS1 = "OPSFS1";
    private static final String CLIENTPV_MAJORVERSION = "CLIENTPV_MAJORVERSION";
    private static final String CLIENTPV_MINORVERSION = "CLIENTPV_MINORVERSION";
    private static final String DISPATCH_TYPE = "DISPATCH_TYPE";
    private static final String USER_ID = "USER_ID";
    private static final String DERIVED_KEY = "DERIVED_KEY";
    private static final String ACCOUNT_ID = "ACCOUNT_ID";
    private static final String PAMACC_ID = "PAMACCID";
    private static final String CORPADMIN_FS1 = "CORPADMINFS1";
    private static final String LAST_ACTIVATION_TIME = "LAST_ACTIVATION_TIME";
    private static final String CLIENT_SW_INF="CLIENT_SW_INFO";
    private static final String CLIENT_PLATFORM_TYPE="CLIENT_PLATFORM_TYPE";
    private static final String DYNAMIC_QOS_FLAG = "DYNAMICQOSFLAG";
    private static final String VOCODERID = "VOCODERID";
    private static final String ADDLINFO = "ADDLINFO";
    private static final String SERVICE_STATUS_OP = "SERVICE_STATUS_OP";
    private static final String SERVICE_STATUS_AUTHUSER = "SERVICE_STATUS_AUTHUSER";
    private static final String ALIAS_MDN = "ALIAS_MDN";
    private static final String LICENSE_TYPE = "LICENSE_TYPE";
    private static final String QPPPACKID = "QPPPACKID";
    private static final String SEGMENT_INDICATOR = "SEGMENT_INDICATOR";
    private static final String MCPTT_COMPLIANCE = "MCPTT_COMPLIANCE";
    private static final String MC_PTTID = "MC_PTTID";
    private static final String ACTIVE_FS2 = "ACTIVEFS2";
    private static final String CLIENT_FS2 = "CLIENTFS2";
    private static final String SUBS_FS2 = "SUBSCRIBERFS2";
    private static final String OPS_FS2 = "OPSFS2";
    private static final String CORPADMIN_FS2 = "CORPADMINFS2";
    private static final String USERPROFILEFS2 = "USERPROFILEFS2";
    private static final String XDMS_FS2 = "XDMSFS2";
    private static final String PRIVACY_OPT_STATUS = "PRIVACY_OPT_STATUS";
    private static final String  MC_VIDEOID = "MC_VIDEOID";
    private static final String  MC_DATAID = "MC_DATAID";
    private static final String  MC_ID = "MC_ID";
    private static final String  USERPROFILEINDEX = "USERPROFILEINDEX";
    private static final String  USERPROFILEID = "USERPROFILEID";
    private static final String  ISDEFAULTPROFILE = "ISDEFAULTPROFILE";
    private static final String FEATURE_REL_VERSION = "FEATURE_REL_VERSION";
    private static final String CAMERA_TYPE = "CAMERA_TYPE";
    private static final String EXT_GATEWAY_ID = "GW_ID";
    private static final String PREV_SERVICE_AUTH_STATUS = "PREVSERVICEAUTHSTATUS";
    private static final String UFMI = "UFMI";
    private static final String IDEN_USERNAME = "iDI_USERNAME";
    private static final String IDEN_PASSWORD = "iDI_E_PASSWORD";
    private static final String IDEN_BUSUNITID = "iDI_BUID";
    private static final String XDMS_FS1 = "XDMSFS1";
    private static final String DERIVEDKEY = "DERIVED_KEY";


    public KnPOCSubscrInfoDAO( ) throws KnBulkOpsException {
        bulkOpsDBUtil = KnBulkOpsDBUtil.getInstance();
    }

    /**
     * Retrieves the service authentication status for a batch of subscribers.
     *
     * @param mdns         List of MDNs (Mobile Directory Numbers) to validate.
     * @param persisterTxn Persistence transaction to use for the query.
     * @return Map keyed by MDN with its corresponding SERVICE_AUTH_STATUS value.
     * @throws KnDAOException if a database access error occurs.
     */
    public Map<String, Integer> getSubscriberServiceAuthStatus(List<String> mdns, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubscriberServiceAuthStatus(List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "Fetching service auth status for ", mdns.size(), " MDNs");

        Map<String, Integer> serviceAuthStatusMap = bulkOpsDBUtil.executeInClauseQuery(
                SELECT_SERVICE_AUTH_STATUS_IN_PREFIX,
                mdns,
                rs -> {
                    Map<String, Integer> resultMap = new HashMap<>();
                    while (rs.next()) {
                        String mdn = rs.getString(1);
                        int authStatus = rs.getInt(2);
                        resultMap.put(mdn, authStatus);
                    }
                    return resultMap;
                },
                persisterTxn,
                KnDBConst.DataStores.XDM_SHARED_DATA
        );

        knLogger.debug(methodName, "Retrieved service auth status for ", serviceAuthStatusMap.size(), " MDNs");
        return serviceAuthStatusMap;
    }

    private StringBuilder getSelectBulkSubsProfileQuery() {
        StringBuilder strBuffer = new StringBuilder();
        strBuffer.append("SELECT ");
        strBuffer.append(MDN).append(", ").append(POC_HOME).append(", ").append(PRESENCE_HOME).append(", ")
                .append(XDMS_HOME).append(", ").append(SERVICE_AUTH_STATUS).append(", ")
                .append(PUBLIC_SUBSCRIPTION_TYPE).append(", ").append(CORP_SUBSCRIPTION_TYPE).append(", ")
                .append(CORP_ID).append(", ").append(CORP_CONTACT_PAIRING_IND).append(", ").append(IMEI).append(", ")
                .append(AFFILIATE_ID).append(", ").append(PAY_TYPE).append(", ").append(SUBSCR_CREATION_TIME)
                .append(", ").append(LAST_PROFILE_UPDATE_TIME).append(", ").append(CORPCONTACTLISTID).append(", ")
                .append(USER_AGENT).append(", ").append(CLIENT_PASSWORD).append(", ").append(EMAIL).append(", ")
                .append(CLIENT_TYPE).append(", ").append(DISPATCH_GRP_MEMBER).append(", ").append(SUBS_FS1).append(", ")
                .append(CLIENT_FS1).append(", ").append(ACTIVE_FS1).append(", ").append(OPS_FS1).append(", ")
                .append(CLIENTPV_MAJORVERSION).append(", ").append(CLIENTPV_MINORVERSION).append(", ")
                .append(ACCOUNT_ID).append(", ").append(PAMACC_ID).append(",").append(CORPADMIN_FS1).append(" , ")
                .append(VOCODERID).append(" , ").append(ADDLINFO).append(" , ").append(LAST_ACTIVATION_TIME)
                .append(" , ").append(DISPATCH_TYPE).append(" , ").append(USER_ID).append(" , ").append(DERIVED_KEY).append(" , ").append(SUBSCR_NAME)
                .append(" , ").append(UFMI).append(" , ").append(IDEN_USERNAME).append(" , ").append(IDEN_PASSWORD)
                .append(" , ").append(IDEN_BUSUNITID).append(" , ").append(SERVICE_STATUS_OP).append(" , ")
                .append(SERVICE_STATUS_AUTHUSER).append(" , ").append(XDMS_FS1).append(" , ").append(LICENSE_TYPE)
                .append(" , ").append(ALIAS_MDN).append(" , ").append(QPPPACKID).append(" , ").append(SEGMENT_INDICATOR)
                .append(" , ").append(MCPTT_COMPLIANCE).append(" , ").append(SUBS_FS2).append(", ")
                .append(CLIENT_FS2).append(", ").append(ACTIVE_FS2).append(", ").append(OPS_FS2).append(",").append(CORPADMIN_FS2)
                .append(" , ").append(XDMS_FS2).append(" , ").append(MC_ID).append(" , ").append(MC_PTTID).append(" , ")
                .append(MC_VIDEOID).append(" , ").append(MC_DATAID).append(" , ").append(USERPROFILEINDEX).append(" , ").append(ISDEFAULTPROFILE).
                append(" , ").append(FEATURE_REL_VERSION).append(" , ").append(CLIENT_SW_INF).append(" , ").append(CLIENT_PLATFORM_TYPE).append(" , ").append(DYNAMIC_QOS_FLAG)
                .append(" , ").append(DERIVEDKEY).append(" , ").append(USERPROFILEFS2).append(" , ").append(USERPROFILEID).append(" , ").append(CAMERA_TYPE)
                .append(" , ").append(PRIVACY_OPT_STATUS).append(" , ").append(PREV_SERVICE_AUTH_STATUS).append(" , ").append(EXT_GATEWAY_ID);;
        strBuffer.append(" FROM ").append("DG.POCSUBSCRINFO").append(" WHERE ");

        return strBuffer;

    }

    /**
     * Retrieve subscriber profile information for a list of MDNs using IN clause batching
     *
     * @param mdns       List of MDNs to query
     * @param persistTxn Database transaction
     * @return Map of MDN to subscriber profile DTO
     * @throws KnDAOException if database operation fails
     */
    public Map<String, KnOPSubsProfileInfoDTO> selectSubscriberProfiles(List<String> mdns, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "selectSubscriberProfiles(List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "Fetching subscriber profiles for ", mdns.size(), " MDNs");

        Map<String, KnOPSubsProfileInfoDTO> profileMap = bulkOpsDBUtil.executeInClauseQuery(
                SELECT_SUBSCRIBER_PROFILES_BY_MDNS_PREFIX,
                mdns,
                rs -> {
                    Map<String, KnOPSubsProfileInfoDTO> resultMap = new HashMap<>();
                    while (rs.next()) {
                        try {
                            KnOPSubsProfileInfoDTO subsProfileInfoDto = new KnOPSubsProfileInfoDTO();
                            getSelectSubsProfileResult(subsProfileInfoDto, rs);
                            resultMap.put(subsProfileInfoDto.getMdn(), subsProfileInfoDto);
                        } catch (SQLException | UnsupportedEncodingException e) {
                            knLogger.error(methodName, "Error mapping subscriber profile result - ", e);
                            throw new RuntimeException("Failed to map subscriber profile", e);
                        }
                    }
                    return resultMap;
                },
                persistTxn,
                KnDBConst.DataStores.XDM_SHARED_DATA
        );

        if (profileMap.isEmpty()) {
            knLogger.warn(methodName, "No subscriber profiles found for MDN list");
        } else {
            knLogger.debug(methodName, "Retrieved profiles for ", profileMap.size(), " MDNs, requested ", mdns.size());
        }

        return profileMap;
    }

    /**
     * Retrieves MDN mappings for a list of base MDNs using IN clause batching
     *
     * @param baseMdns    List of base MDNs to query
     * @param persistTxn  Persistence transaction (optional)
     * @return Map of base MDN to associated MDNs
     * @throws KnDAOException if database operation fails
     */
    public Map<String, List<String>> getProfileMdnsByBaseMdns(List<String> baseMdns,
                                                              KnPersisterTxn persistTxn)
            throws KnDAOException {
        String methodName = "getProfileMdnsByBaseMdns(List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "Fetching profile MDNs for ", baseMdns.size(), " base MDNs");

        Map<String, List<String>> mdnMappings = bulkOpsDBUtil.executeInClauseQuery(
                SELECT_MDNS_BY_BASE_MDNS,
                baseMdns,
                rs -> {
                    Map<String, List<String>> resultMap = new HashMap<>();
                    while (rs.next()) {
                        String baseMdn = rs.getString(1).trim();
                        String assocMdn = rs.getString(2).trim();
                        resultMap.computeIfAbsent(baseMdn, k -> new ArrayList<>()).add(assocMdn);
                    }
                    return resultMap;
                },
                persistTxn,
                KnDBConst.DataStores.XDM_SHARED_DATA
        );

        knLogger.debug(methodName, "Retrieved associated MDNs for ", mdnMappings.size(), " base MDNs");
        return mdnMappings;
    }

    /**
     * Bulk update service authentication status for multiple subscriber profiles.
     * Processes update results and separates success/failure cases.
     *
     * @param dtoList      List of subscriber profile DTOs to update
     * @param persisterTxn Database transaction
     * @return Status DTO containing success and failure maps
     * @throws KnDAOException if database operation fails
     */
    public KnBulkUpdateStatusDTO bulkUpdateServiceAuthStatusForUPM(List<KnSubsProfilePersistDTO> dtoList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "bulkUpdateServiceAuthStatusForUPM(List<KnSubsProfilePersistDTO>, KnPersisterTxn)";
        knLogger.debug(methodName, "Bulk updating service auth status for ", dtoList != null ? dtoList.size() : 0, " DTOs");

        Map<String, KnSubsProfilePersistDTO> successResults = new HashMap<>();
        Map<String, KnBulkOpsErrorDetail> failureResults = new HashMap<>();

        if (dtoList == null || dtoList.isEmpty()) {
            knLogger.debug(methodName, "No DTOs to process, returning empty result");
            return new KnBulkUpdateStatusDTO(successResults, failureResults);
        }

        long now = System.currentTimeMillis();

        boolean ownedTxn = false;
        Connection conn = null;
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            conn = bulkOpsDBUtil.getDBConnection(persisterTxn, KnDBConst.DataStores.XDM_SHARED_DATA);

            for (KnSubsProfilePersistDTO dto : dtoList) {
                List<String> mdnList = dto.getMdnList();
                if (mdnList == null || mdnList.isEmpty()) {
                    mdnList = Collections.singletonList(dto.getMdn());
                }

                StringBuffer keys = KnDbUtil.convertListToStringBuffer(mdnList);
                String query = UPDATE_SERVICE_AUTH_STATUS_SQL.replace("WHERE MDN = ?", "WHERE MDN IN " + keys.toString());
                PreparedStatement pStmt = null;
                try {
                    pStmt = conn.prepareStatement(query);
                    int idx = 0;
                    pStmt.setInt(++idx, dto.getServiceAuthStatus());

                    if (dto.getClientPassword() == null) {
                        pStmt.setNull(++idx, java.sql.Types.CHAR);
                    } else {
                        pStmt.setString(++idx, dto.getClientPassword());
                    }

                    if (dto.getUserAgent() == null) {
                        pStmt.setNull(++idx, java.sql.Types.CHAR);
                    } else {
                        pStmt.setString(++idx, dto.getUserAgent());
                    }

                    pStmt.setLong(++idx, dto.getLastProfileUpdateTime());
                    pStmt.setInt(++idx, dto.getServiceStatusOp());

                    if (dto.getPreviousServiceAuthStatusToStore() != null) {
                        pStmt.setInt(++idx, dto.getPreviousServiceAuthStatusToStore());
                    } else {
                        pStmt.setNull(++idx, java.sql.Types.INTEGER);
                    }

                    pStmt.setLong(++idx, now);

                    int rowsAffected = pStmt.executeUpdate();
                    if (rowsAffected > 0) {
                        for (String mdn : mdnList) {
                            successResults.put(mdn, dto);
                        }
                        knLogger.debug(methodName, "Successfully updated ", rowsAffected, " rows for MDN list ", KnGDPRTemplate.mdnList(mdnList));
                    } else {
                        String msg = "Bulk update failed for MDN list " + mdnList + " with result code 0";
                        for (String mdn : mdnList) {
                            KnBulkOpsErrorDetail knBulkOpsErrorDetail = new KnBulkOpsErrorDetail();
                            knBulkOpsErrorDetail.setErrorCode("");
                            knBulkOpsErrorDetail.setErrorMessage(msg);
                            knBulkOpsErrorDetail.setMdn(mdn);
                            failureResults.put(mdn, knBulkOpsErrorDetail);
                        }
                        knLogger.error(methodName, msg);
                    }
                } finally {
                    KnDbUtil.closeStatement(pStmt);
                }
            }

            if (ownedTxn) {
                persisterTxn.save();
            }
        } catch (KnDAOException e) {
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to bulk update service auth status - " + e.getMessage(),
                    null, KnProvDAOSourceTypes.POCSUBSCRINFO, UPDATE_SERVICE_AUTH_STATUS_SQL);
        } finally {
            if (ownedTxn) {
                KnDbUtil.closeConnection(conn);
            }
        }

        knLogger.debug(methodName, "Bulk update completed - Success: ", successResults.size(), ", Failure: ", failureResults.size());
        return new KnBulkUpdateStatusDTO(successResults, failureResults);
    }

    /**
     * Retrieve client protocol version information for a list of MDNs
     *
     * @param mdns        List of MDNs to query
     * @param persisterTxn Database transaction (optional)
     * @return Map of MDN to Client Protocol Version (Major Version)
     * @throws KnDAOException if database operation fails
     */
    public Map<String, Integer> selectSubscribersPV(List<String> mdns, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "selectSubscribersPV(List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "Fetching protocol version for ", mdns.size(), " MDNs");

        Map<String, Integer> pvInfo = bulkOpsDBUtil.executeInClauseQuery(
                QRY_SELECT_SUBSCRIBERS_PV,
                mdns,
                rs -> {
                    Map<String, Integer> resultMap = new HashMap<>();
                    while (rs.next()) {
                        resultMap.put(rs.getString(1).trim(), rs.getInt(2));
                    }
                    return resultMap;
                },
                persisterTxn,
                KnDBConst.DataStores.XDM_SHARED_DATA
        );

        if (pvInfo.isEmpty()) {
            knLogger.info(methodName, "No subscriber protocol version info found");
        }

        knLogger.debug(methodName, "Retrieved protocol version for ", pvInfo.size(), " MDNs");
        return pvInfo;
    }

    /**
     * Update subscriber profile last update time and retrieve associated document changes for a map of MDN mappings.
     *
     * @param mdnMappingMap Map of base MDN to profile MDN list
     * @param persisterTxn  Database transaction
     * @return Map of MDN to collection of document changes
     * @throws KnDAOException if database operation fails
     */
    public Map<String, Collection<KnDocChangeListDTO>> profileMdnEtagUpdate(Map<String, List<String>> mdnMappingMap,
                                                                            KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "profileMdnEtagUpdate(Map<String, List<String>>, KnPersisterTxn)";
        knLogger.debug(methodName, "Processing profile updates for MDN map with ", mdnMappingMap.size(), " entries");

        Map<String, Collection<KnDocChangeListDTO>> mdnDocMap = new HashMap<>();

        if (mdnMappingMap == null || mdnMappingMap.isEmpty()) {
            knLogger.debug(methodName, "No MDN mappings to process, returning empty result");
            return mdnDocMap;
        }

        // Flatten the map to get all unique MDNs
        Set<String> allMdns = new HashSet<>();
        mdnMappingMap.forEach((baseMdn, profileMdns) -> {
            if (profileMdns != null) {
                allMdns.addAll(profileMdns);
            }
        });

        long lastProfileUpdateTime = System.currentTimeMillis();

        // Retrieve subscriber information for all MDNs
        Map<String, Map<String, Object>> subsInfoMap = bulkOpsDBUtil.executeInClauseQuery(
                GET_SUBS_MCID_UPDTS_BY_MDNS,
                new ArrayList<>(allMdns),
                rs -> {
                    Map<String, Map<String, Object>> resultMap = new HashMap<>();
                    while (rs.next()) {
                        Map<String, Object> subsData = new HashMap<>();
                        String mdn = rs.getString(1).trim();
                        subsData.put("MDN", mdn);
                        subsData.put("MC_ID_BYTES", rs.getBytes(2));
                        subsData.put("LASTPROFILEUPDATETIME", rs.getLong(3));
                        subsData.put("MCPTT_COMPLIANCE", rs.getInt(4));
                        subsData.put("USERPROFILEINDEX", rs.getInt(5));
                        subsData.put("CLIENTPV_MAJORVERSION", rs.getInt(6));
                        subsData.put("ACTIVEFS2", rs.getString(7));
                        resultMap.put(mdn, subsData);
                    }
                    return resultMap;
                },
                persisterTxn,
                KnDBConst.DataStores.XDM_SHARED_DATA
        );

        // Filter MDNs that need updating based on PV and UPM bit
        List<String> mdnsToUpdate = new ArrayList<>();
        for (String mdn : allMdns) {
            Map<String, Object> subsData = subsInfoMap.get(mdn);
            if (subsData == null) {
                knLogger.warn(methodName, "No subscriber data found for MDN: ", KnGDPRTemplate.mdn(mdn));
                continue;
            }

            String activeFs = (String) subsData.get("ACTIVEFS2");
            if (activeFs == null) {
                Integer userProfileIndex = (Integer) subsData.get("USERPROFILEINDEX");
                activeFs = KnGeneralUtil.convertLongToHexString(
                        userProfileIndex == null ? 0L : userProfileIndex.longValue());
            }

            boolean upmBit = KnGeneralUtil.getFeatureBitValue(activeFs, USER_PROFILE_MGMT_BIT);
            int majorPv = (Integer) subsData.get("CLIENTPV_MAJORVERSION");

            // Add to update list if PV > 18 or UPM bit enabled
            if (majorPv > PROTOCOL_VERSION_18_X || upmBit) {
                mdnsToUpdate.add(mdn);
            }
        }

        knLogger.debug(methodName, "MDNs eligible for update: ", mdnsToUpdate.size());

        // Execute batch update using bulk utility
        if (!mdnsToUpdate.isEmpty()) {
            try {
                int[] results = bulkOpsDBUtil.executeBatchUpdate(
                        UPDATE_SUBS_PROFILE_LAST_UPDATE_TIME,
                        mdnsToUpdate,
                        (pStmt, mdn) -> {
                            pStmt.setLong(1, lastProfileUpdateTime);
                            pStmt.setString(2, mdn);
                        },
                        persisterTxn,
                        KnDBConst.DataStores.XDM_SHARED_DATA
                );

                knLogger.debug(methodName, "Batch update executed with ", results.length, " results");

                // Process results and build document change list for successful updates
                for (int i = 0; i < results.length; i++) {
                    int r = results[i];
                    String mdn = mdnsToUpdate.get(i);

                    if (r > 0 || r == java.sql.Statement.SUCCESS_NO_INFO) {
                        Map<String, Object> subsData = subsInfoMap.get(mdn);
                        if (subsData != null) {
                            Collection<KnDocChangeListDTO> dbRecList = buildMCSDOC(
                                    new String((byte[]) subsData.get("MC_ID_BYTES"), StandardCharsets.UTF_8),
                                    mdn,
                                    String.valueOf(subsData.get("USERPROFILEINDEX")),
                                    String.valueOf(lastProfileUpdateTime),
                                    String.valueOf(subsData.get("LASTPROFILEUPDATETIME")),
                                    (Integer) subsData.get("MCPTT_COMPLIANCE"),
                                    null
                            );
                            mdnDocMap.put(mdn, dbRecList);
                        }
                    }
                }

                knLogger.debug(methodName, "Profile update completed - Document changes for ", mdnDocMap.size(), " MDNs");
            } catch (Exception e) {
                knLogger.error(methodName, "Exception occurred during batch update - ", e);
                throw KnDbUtil.processException(e, "Failed to update profile MDN etag - " + e.getMessage(),
                        KnBulkOpsDBUtil.getXdmPttServerId(), KnBulkOpsDAOSourceTypes.POCSUBSCRINFO, UPDATE_SUBS_PROFILE_LAST_UPDATE_TIME);
            }
        }

        return mdnDocMap;
    }

    private void getSelectSubsProfileResult(KnSubsProfileDTO subsProfileInfoDto, ResultSet rs)
            throws SQLException, UnsupportedEncodingException {
        subsProfileInfoDto.setMdn(rs.getString(MDN));
        subsProfileInfoDto.setPoCHome(rs.getString(POC_HOME));
        subsProfileInfoDto.setPresenceHome(rs.getString(PRESENCE_HOME));
        subsProfileInfoDto.setXDMSHome(rs.getString(XDMS_HOME));
        subsProfileInfoDto.setServiceAuthStatus(rs.getInt(SERVICE_AUTH_STATUS));
        subsProfileInfoDto.setPublicSubscriptionType(rs.getInt(PUBLIC_SUBSCRIPTION_TYPE));
        subsProfileInfoDto.setCorporateSubscriptionType(rs.getInt(CORP_SUBSCRIPTION_TYPE));
        subsProfileInfoDto.setCorpId(rs.getInt(CORP_ID));
        int corpPairingInd = rs.getInt(CORP_CONTACT_PAIRING_IND);
        if (corpPairingInd == 1) {
            subsProfileInfoDto.setPairingInd(true);
        } else {
            subsProfileInfoDto.setPairingInd(false);
        }
        String imei = rs.getString(IMEI);
        if (imei != null) {
            imei = imei.trim();
        }
        subsProfileInfoDto.setIMEI(imei);
        subsProfileInfoDto.setAffiliateId(rs.getString(AFFILIATE_ID));
        subsProfileInfoDto.setPayType(rs.getInt(PAY_TYPE));
        subsProfileInfoDto.setSubsCreationTime(rs.getLong(SUBSCR_CREATION_TIME));
        subsProfileInfoDto.setLastProfileUpdateTime(rs.getLong(LAST_PROFILE_UPDATE_TIME));
        subsProfileInfoDto.setCorpContactListId(rs.getInt(CORPCONTACTLISTID));
        subsProfileInfoDto.setUserAgent(rs.getString(USER_AGENT));
        subsProfileInfoDto.setClientPassword(rs.getString(CLIENT_PASSWORD));
        subsProfileInfoDto.setEmailAddress(rs.getString(EMAIL));
        subsProfileInfoDto.setSubsClientType(rs.getInt(CLIENT_TYPE));
        subsProfileInfoDto.setDispatchGroupMember(rs.getInt(DISPATCH_GRP_MEMBER));
        subsProfileInfoDto.setClientPVmajorVer(rs.getInt(CLIENTPV_MAJORVERSION));
        subsProfileInfoDto.setClientPVminorVer(rs.getInt(CLIENTPV_MINORVERSION));
        subsProfileInfoDto.setAccountId(rs.getString(ACCOUNT_ID));
        subsProfileInfoDto.setPamAccId(rs.getInt(PAMACC_ID));
        subsProfileInfoDto.setVocoderId(rs.getInt(VOCODERID));
        subsProfileInfoDto.setHierarchyType(KnConstants.HIERARCHY_TYPE.validate(rs.getInt(ADDLINFO)));
        subsProfileInfoDto.setLastActivationTime(rs.getLong(LAST_ACTIVATION_TIME));
        subsProfileInfoDto.setDispatchType(rs.getInt(DISPATCH_TYPE));
        String derKey = rs.getString(DERIVED_KEY);
        if (derKey != null && !(derKey.isEmpty())){
            subsProfileInfoDto.setDerivedKey(KnGeneralUtil.convertAsciiToHex(derKey));
        }
        subsProfileInfoDto.setUserId(rs.getString(USER_ID));
        if (rs.getString(SUBSCR_NAME) != null)
            subsProfileInfoDto.setNetworkName(new String(rs.getString(SUBSCR_NAME).getBytes("8859_1"), "UTF-8"));
        subsProfileInfoDto.setUfmi(rs.getString(UFMI));
        subsProfileInfoDto.setiDenUserName(rs.getString(IDEN_USERNAME));
        subsProfileInfoDto.setiDenPassword(rs.getString(IDEN_PASSWORD));
        subsProfileInfoDto.setiDenBusUnitId(rs.getString(IDEN_BUSUNITID));
        subsProfileInfoDto.setPoCStatusOP(rs.getInt(SERVICE_STATUS_OP));
        subsProfileInfoDto.setPoCStatusAU(rs.getInt(SERVICE_STATUS_AUTHUSER));
        subsProfileInfoDto.setServiceStatusOp(rs.getInt(SERVICE_STATUS_OP));
        subsProfileInfoDto.setServiceStatusAuthUser(rs.getInt(SERVICE_STATUS_AUTHUSER));
        subsProfileInfoDto.setLicenseType(rs.getInt(LICENSE_TYPE));
        if (rs.getString(PREV_SERVICE_AUTH_STATUS) != null) {
            subsProfileInfoDto.setPreviousServiceAuthStatus(rs.getInt(PREV_SERVICE_AUTH_STATUS));
        }
        if(rs.getString(ALIAS_MDN) != null) subsProfileInfoDto.setAliasMdn(rs.getString(ALIAS_MDN).trim());
        subsProfileInfoDto.setQppPkgId(rs.getInt(QPPPACKID));
        subsProfileInfoDto.setFirstNetIndicator(rs.getString(SEGMENT_INDICATOR));
        subsProfileInfoDto.setMcpttCompliance(rs.getInt(MCPTT_COMPLIANCE));
        String subsFs2=rs.getString(SUBS_FS2)!=null?rs.getString(SUBS_FS2):KnGeneralUtil.convertLongToHexString(rs.getLong(SUBS_FS1));
        subsProfileInfoDto.setSubsFS2(subsFs2);
        String clientsFs2=rs.getString(CLIENT_FS2)!=null?rs.getString(CLIENT_FS2):KnGeneralUtil.convertLongToHexString(rs.getLong(CLIENT_FS1));
        subsProfileInfoDto.setClientFS2(clientsFs2);
        String activeFs2=rs.getString(ACTIVE_FS2)!=null?rs.getString(ACTIVE_FS2):KnGeneralUtil.convertLongToHexString(rs.getLong(ACTIVE_FS1));
        subsProfileInfoDto.setActiveFS2(activeFs2);
        String opsFs2=rs.getString(OPS_FS2)!=null?rs.getString(OPS_FS2):KnGeneralUtil.convertLongToHexString(rs.getLong(OPS_FS1));
        subsProfileInfoDto.setOpsFS2(opsFs2);
        String corpAdminFs2=rs.getString(CORPADMIN_FS2)!=null?rs.getString(CORPADMIN_FS2):KnGeneralUtil.convertLongToHexString(rs.getLong(CORPADMIN_FS1));
        subsProfileInfoDto.setCorpAdminFS2(corpAdminFs2);
        String xdmsFs1=rs.getString(XDMS_FS2)!=null?rs.getString(XDMS_FS2):KnGeneralUtil.convertLongToHexString(rs.getLong(XDMS_FS1));
        subsProfileInfoDto.setXdmsFS2(xdmsFs1);
        if (null != rs.getString(MC_ID)) {
            subsProfileInfoDto.setMcId(new String(rs.getBytes(MC_ID), StandardCharsets.UTF_8));
        }
        if (null != rs.getString(MC_PTTID)) {
            subsProfileInfoDto.setMcpttId(new String(rs.getBytes(MC_PTTID), StandardCharsets.UTF_8));
        }
        if (null != rs.getString(MC_VIDEOID)) {
            subsProfileInfoDto.setMcVideoId(new String(rs.getBytes(MC_VIDEOID), StandardCharsets.UTF_8));
        }
        if (null != rs.getString(MC_DATAID)) {
            subsProfileInfoDto.setMcDataId(new String(rs.getBytes(MC_DATAID), StandardCharsets.UTF_8));
        }
        subsProfileInfoDto.setUserProfileIndex(rs.getInt(USERPROFILEINDEX));
        subsProfileInfoDto.setExtGatewayId(rs.getString(EXT_GATEWAY_ID));
        subsProfileInfoDto.setIsDefaultProfile(rs.getInt(ISDEFAULTPROFILE));
        subsProfileInfoDto.setFeatureRelVersion(rs.getString(FEATURE_REL_VERSION) != null ? rs.getString("FEATURE_REL_VERSION") : "0.0");
        subsProfileInfoDto.setSwType(rs.getInt(CLIENT_SW_INF));
        subsProfileInfoDto.setPlatformType(rs.getInt(CLIENT_PLATFORM_TYPE));
        subsProfileInfoDto.setDynamicQosFlag(rs.getInt(DYNAMIC_QOS_FLAG));
        if (rs.getString(USERPROFILEFS2) != null) {
            subsProfileInfoDto.setUserProfileFS2(rs.getString(USERPROFILEFS2));
        }
        if(rs.getString(USER_ID)!=null){
            subsProfileInfoDto.setUserId(rs.getString(USER_ID));
        }
        subsProfileInfoDto.setUserProfileId(rs.getString(USERPROFILEID));
        subsProfileInfoDto.setCameraType((Integer)rs.getObject(CAMERA_TYPE));
        subsProfileInfoDto.setPrivacyOptStatus(rs.getInt(PRIVACY_OPT_STATUS));
    }


    public Map<String, com.kodiak.xdms.server.common.dto.common.KnSubsProfileDTO> getSubsProfileDTOMapBulk(List<String> mdnList, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "selectSubscriberProfile(IPersistenceDTO, KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        Map<String, com.kodiak.xdms.server.common.dto.common.KnSubsProfileDTO> subsProfileInfoDto = new HashMap<>();
        knLogger.debug(methodName, "ENTRY: Select Subscriber Profile mdn", KnGDPRTemplate.mdnList(mdnList), "persistTxn ", persistTxn);
        int index = 1;
        try {
            StringBuilder strBuffer = getSelectBulkSubsProfileQuery();
            strBuffer.append(MDN).append(" IN ").append("(MDNLIST)");
            conn = bulkOpsDBUtil.getDBConnection(persistTxn, KnDBConst.DataStores.XDM_SHARED_DATA);
            query = strBuffer.toString();
            query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(mdnList, query, "MDNLIST");
            pStmt = conn.prepareStatement(query);
            for (String mdn : mdnList) {
                pStmt.setString(index++, mdn);
            }

            knLogger.debug(methodName, "Query: Executing - ", query, ", mdn - ", KnGDPRTemplate.mdnList(mdnList));
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Query: Executed ");
            while (rs.next()) {
                knLogger.debug("ContactID::",rs.getInt(CORPCONTACTLISTID));
                com.kodiak.xdms.server.common.dto.common.KnSubsProfileDTO subsProfileDTOCommon = getSelectSubsCommonProfileResult(rs);
                subsProfileInfoDto.put(rs.getString(MDN).trim(), subsProfileDTOCommon);
            }
            knLogger.debug("subsProfileInfoDto map size - ", subsProfileInfoDto);
            return subsProfileInfoDto;

        } catch (KnDAOException dbConne) {
            knLogger.error(methodName, "DAO Exception occurred", dbConne.getMessage());
            throw dbConne;
        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQL Exception occurred");
            throw com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil.processException(sqlE, "Failed to select Subscriber Profile - " + sqlE.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e.getMessage());
            throw com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil.processException(e, "Failed to select Subscriber Profile - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil.closeResultSet(rs);
            com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil.closeStatement(pStmt);
            knLogger.info(methodName, "EXIT : select Subscriber Profile");
        }
    }

    private com.kodiak.xdms.server.common.dto.common.KnSubsProfileDTO getSelectSubsCommonProfileResult(ResultSet rs) throws SQLException, UnsupportedEncodingException {
        com.kodiak.xdms.server.common.dto.common.KnSubsProfileDTO subsProfileInfoDto = new com.kodiak.xdms.server.common.dto.common.KnSubsProfileDTO();
        subsProfileInfoDto.setMdn(rs.getString(MDN));
        subsProfileInfoDto.setPocHome(rs.getString(POC_HOME));
        subsProfileInfoDto.setPresenceHome(rs.getString(PRESENCE_HOME));
        subsProfileInfoDto.setXdmsHome(rs.getString(XDMS_HOME));
        subsProfileInfoDto.setServiceAuthStatus(rs.getInt(SERVICE_AUTH_STATUS));
        subsProfileInfoDto.setPublicSubscriptionType(rs.getInt(PUBLIC_SUBSCRIPTION_TYPE));
        subsProfileInfoDto.setCorporateSubscriptionType(rs.getInt(CORP_SUBSCRIPTION_TYPE));
        subsProfileInfoDto.setCorpId(rs.getInt(CORP_ID));
        int corpPairingInd = rs.getInt(CORP_CONTACT_PAIRING_IND);
        if (corpPairingInd == 1) {
            subsProfileInfoDto.setPairingIndicator(true);
        } else {
            subsProfileInfoDto.setPairingIndicator(false);
        }
        String imei = rs.getString(IMEI);
        if (imei != null) {
            imei = imei.trim();
        }
        subsProfileInfoDto.setIMEI(imei);
        subsProfileInfoDto.setAffiliateId(rs.getString(AFFILIATE_ID));
        subsProfileInfoDto.setPayType(rs.getInt(PAY_TYPE));
        subsProfileInfoDto.setSubsCreationTime(rs.getLong(SUBSCR_CREATION_TIME));
        subsProfileInfoDto.setLastProfileUpdateTime(rs.getLong(LAST_PROFILE_UPDATE_TIME));
        subsProfileInfoDto.setCorpContactListId(rs.getInt(CORPCONTACTLISTID));
        subsProfileInfoDto.setUserAgent(rs.getString(USER_AGENT));
        subsProfileInfoDto.setClientPassword(rs.getString(CLIENT_PASSWORD));
        subsProfileInfoDto.setEmailAddress(rs.getString(EMAIL));
        subsProfileInfoDto.setSubsClientType(rs.getInt(CLIENT_TYPE));
        subsProfileInfoDto.setDispatchGroupMember(rs.getInt(DISPATCH_GRP_MEMBER));
        subsProfileInfoDto.setClientPVmajorVer(rs.getInt(CLIENTPV_MAJORVERSION));
        subsProfileInfoDto.setClientPVminorVer(rs.getInt(CLIENTPV_MINORVERSION));
        subsProfileInfoDto.setAccountId(rs.getString(ACCOUNT_ID));
        subsProfileInfoDto.setPamAccId(rs.getInt(PAMACC_ID));
        subsProfileInfoDto.setVocoderId(rs.getInt(VOCODERID));
        subsProfileInfoDto.setHierarchyType(KnConstants.HIERARCHY_TYPE.validate(rs.getInt(ADDLINFO)));
        subsProfileInfoDto.setLastActivationTime(rs.getLong(LAST_ACTIVATION_TIME));
        subsProfileInfoDto.setDispatchType(rs.getInt(DISPATCH_TYPE));
        String derKey = rs.getString(DERIVED_KEY);
        if (derKey != null && !(derKey.isEmpty())) {
            subsProfileInfoDto.setDerivedKey(KnGeneralUtil.convertAsciiToHex(derKey));
        }
        subsProfileInfoDto.setUserId(rs.getString(USER_ID));
        if (rs.getString(SUBSCR_NAME) != null)
            subsProfileInfoDto.setNetworkName(new String(rs.getString(SUBSCR_NAME).getBytes("8859_1"), "UTF-8"));
        subsProfileInfoDto.setUfmi(rs.getString(UFMI));
        subsProfileInfoDto.setiDenUserName(rs.getString(IDEN_USERNAME));
        subsProfileInfoDto.setiDenPassword(rs.getString(IDEN_PASSWORD));
        subsProfileInfoDto.setiDenBusUnitId(rs.getString(IDEN_BUSUNITID));
        subsProfileInfoDto.setPoCStatusOP(rs.getInt(SERVICE_STATUS_OP));
        subsProfileInfoDto.setPoCStatusAU(rs.getInt(SERVICE_STATUS_AUTHUSER));
        subsProfileInfoDto.setServiceStatusOp(rs.getInt(SERVICE_STATUS_OP));
        subsProfileInfoDto.setServiceStatusAuthUser(rs.getInt(SERVICE_STATUS_AUTHUSER));
        subsProfileInfoDto.setLicenseType(rs.getInt(LICENSE_TYPE));
        if (rs.getString(PREV_SERVICE_AUTH_STATUS) != null) {
            subsProfileInfoDto.setPreviousServiceAuthStatus(rs.getInt(PREV_SERVICE_AUTH_STATUS));
        }
        if (rs.getString(ALIAS_MDN) != null) subsProfileInfoDto.setAliasMdn(rs.getString(ALIAS_MDN).trim());
        subsProfileInfoDto.setQppPkgId(rs.getInt(QPPPACKID));
        subsProfileInfoDto.setFirstNetIndicator(rs.getString(SEGMENT_INDICATOR));
        subsProfileInfoDto.setMcpttCompliance(rs.getInt(MCPTT_COMPLIANCE));
        String subsFs2 = rs.getString(SUBS_FS2) != null ? rs.getString(SUBS_FS2) : KnGeneralUtil.convertLongToHexString(rs.getLong(SUBS_FS1));
        subsProfileInfoDto.setSubsFS2(subsFs2);
        String clientsFs2 = rs.getString(CLIENT_FS2) != null ? rs.getString(CLIENT_FS2) : KnGeneralUtil.convertLongToHexString(rs.getLong(CLIENT_FS1));
        subsProfileInfoDto.setClientFS2(clientsFs2);
        String activeFs2 = rs.getString(ACTIVE_FS2) != null ? rs.getString(ACTIVE_FS2) : KnGeneralUtil.convertLongToHexString(rs.getLong(ACTIVE_FS1));
        subsProfileInfoDto.setActiveFS2(activeFs2);
        String opsFs2 = rs.getString(OPS_FS2) != null ? rs.getString(OPS_FS2) : KnGeneralUtil.convertLongToHexString(rs.getLong(OPS_FS1));
        subsProfileInfoDto.setOpsFS2(opsFs2);
        String corpAdminFs2 = rs.getString(CORPADMIN_FS2) != null ? rs.getString(CORPADMIN_FS2) : KnGeneralUtil.convertLongToHexString(rs.getLong(CORPADMIN_FS1));
        subsProfileInfoDto.setCorpAdminFS2(corpAdminFs2);
        String xdmsFs1 = rs.getString(XDMS_FS2) != null ? rs.getString(XDMS_FS2) : KnGeneralUtil.convertLongToHexString(rs.getLong(XDMS_FS1));
        subsProfileInfoDto.setXdmsFS2(xdmsFs1);
        if (null != rs.getString(MC_ID)) {
            subsProfileInfoDto.setMcId(new String(rs.getBytes(MC_ID), StandardCharsets.UTF_8));
        }
        if (null != rs.getString(MC_PTTID)) {
            subsProfileInfoDto.setMcpttId(new String(rs.getBytes(MC_PTTID), StandardCharsets.UTF_8));
        }
        if (null != rs.getString(MC_VIDEOID)) {
            subsProfileInfoDto.setMcVideoId(new String(rs.getBytes(MC_VIDEOID), StandardCharsets.UTF_8));
        }
        if (null != rs.getString(MC_DATAID)) {
            subsProfileInfoDto.setMcDataId(new String(rs.getBytes(MC_DATAID), StandardCharsets.UTF_8));
        }
        subsProfileInfoDto.setUserProfileIndex(rs.getInt(USERPROFILEINDEX));
        subsProfileInfoDto.setExtGatewayId(rs.getString(EXT_GATEWAY_ID));
        subsProfileInfoDto.setIsDefaultProfile(rs.getInt(ISDEFAULTPROFILE));
        subsProfileInfoDto.setFeatureRelVersion(rs.getString(FEATURE_REL_VERSION) != null ? rs.getString("FEATURE_REL_VERSION") : "0.0");
        subsProfileInfoDto.setSwType(rs.getInt(CLIENT_SW_INF));
        subsProfileInfoDto.setPlatformType(rs.getInt(CLIENT_PLATFORM_TYPE));
        subsProfileInfoDto.setDynamicQosFlag(rs.getInt(DYNAMIC_QOS_FLAG));
        if (rs.getString(USERPROFILEFS2) != null) {
            subsProfileInfoDto.setUserProfileFS2(rs.getString(USERPROFILEFS2));
        }
        if (rs.getString(USER_ID) != null) {
            subsProfileInfoDto.setUserId(rs.getString(USER_ID));
        }
        subsProfileInfoDto.setUserProfileId(rs.getString(USERPROFILEID));
        subsProfileInfoDto.setCameraType((Integer) rs.getObject(CAMERA_TYPE));
        subsProfileInfoDto.setPrivacyOptStatus(rs.getInt(PRIVACY_OPT_STATUS));
        knLogger.debug("subsProfileInfoDto::", subsProfileInfoDto);
        return subsProfileInfoDto;
    }

    public List<String> checkIfMdnExist(List<String> validMdnList, int corpId, KnPersisterTxn persisterTxn) {
        String methodName = "checkIfMdnExist()";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        int index = 1;
        List<String> mdnsInDb = new ArrayList<>();
        try {
            query = "SELECT MDN FROM DG.POCSUBSCRINFO WHERE MDN IN (MDNLIST) AND CORPID = ?";
            conn = bulkOpsDBUtil.getDBConnection(persisterTxn, KnDBConst.DataStores.XDM_SHARED_DATA);
            query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(validMdnList, query, "MDNLIST");
            pStmt = conn.prepareStatement(query);
            for (String mdn : validMdnList) {
                pStmt.setString(index++, mdn);
            }
            pStmt.setInt(index, corpId);
            rs = pStmt.executeQuery();
            while (rs.next()) {
                mdnsInDb.add(rs.getString(MDN).trim());
            }
        } catch (KnConnectionException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            knLogger.debug(methodName, "Exception Happenend::", e);
        }
        knLogger.debug("MDNs found in DB: ", mdnsInDb.size());
        return mdnsInDb;
    }
}


