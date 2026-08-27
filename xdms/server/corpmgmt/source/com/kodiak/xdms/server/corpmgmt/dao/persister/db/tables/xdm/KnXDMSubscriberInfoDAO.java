/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMSubscriberInfoDAO.java
 * Subsystem:  POC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Namita.P.Nair        25-01-2011      7.0
 * <p/>
 * <p/>
 * #401, 4th Floor, 'Prestige Sigma'
 * No.3, Vittal Mallya Road
 * Bangalore - 560 001
 * www.kodiaknetworks.compublic
 }
 * All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */
package com.kodiak.xdms.server.corpmgmt.dao.persister.db.tables.xdm;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.dbmgr.KnDBManager;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dao.persister.db.KnDBPersistenceException;
import com.kodiak.xdms.server.common.dao.persister.db.KnQueryMapper;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.common.KnSubsProfileDTO;
import com.kodiak.xdms.server.common.dto.common.KnSubscriberDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.resources.KnErrorCodes;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpContactDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPSubscriberInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.*;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpSubscrInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnSubscrFeatureSetRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupMemPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpMdnListPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnMdnDetailsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnDAOSourceTypes;
import com.kodiak.xdms.server.corpmgmt.resources.KnOperationTypes;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.kodiak.common.resources.KnConstants.*;
import static com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil.replaceValInQry;
import static com.kodiak.xdms.server.common.resources.KnConstants.BULK_UPDATE_SIZE;
import static com.kodiak.xdms.server.common.resources.KnConstants.SERVICE_AUTH_STATUS.MARKED_FOR_ASYNC_DELETION;
import static com.kodiak.xdms.server.common.resources.KnConstants.SERVICE_AUTH_STATUS.PROVISIONED;
import static com.kodiak.xdms.server.common.resources.KnConstants.SUBSCRIBERS_CLIENT_TYPE.*;
import static com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants.*;
import static com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants.GET_DISTINCT_MDN_FAN_INFO;
import static com.kodiak.xdms.server.corpmgmt.resources.KnConstants.SUBSCRIPTION_TYPE_PUBLIC_CORP;
import static com.kodiak.xdms.server.corpmgmt.resources.KnConstants.NORMAL_CONTACT_COUNT;
import static com.kodiak.xdms.server.corpmgmt.resources.KnConstants.COMMON_CONTACT_COUNT;
import static com.kodiak.xdms.server.corpmgmt.resources.KnConstants.DIST_POLICY_COMMON_CONTACT_LIST_SUPPORT;

import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.*;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.formCommaSeperatedIdList;

public class KnXDMSubscriberInfoDAO implements ITableDAO {
    private static final KnLogger knLogger = KnLogger.getLogger(KnXDMSubscriberInfoDAO.class);

    private static final String TABLENAME = "DG.POCSUBSCRINFO";

    private static final String UPDATE_QRY = "UPDATE " + TABLENAME + " SET ";
    private static final String LAST_PROFILE_UPDATE_TIME = "LASTPROFILEUPDATETIME";
    private static final String SERVICE_AUTH_STATUS = "SERVICEAUTHSTATUS";
    private static final String IMEI = "IMEI";
    private static final String CLIENT_PASSWORD = "CLIENT_PASSWORD";
    private static final String USER_AGENT = "USERAGENT";
    private static final String CLIENT_TYPE = "CLIENT_TYPE";
    private static final String ACTIVE_FS1 = "ACTIVEFS1";
    private static final String CLIENT_FS1 = "CLIENTFS1";
    private static final String SUBS_FS1 = "SUBSCRIBERFS1";
    private static final String OPS_FS1 = "OPSFS1";
    private static final String CLIENTPV_MAJORVERSION = "CLIENTPV_MAJORVERSION";
    private static final String CLIENTPV_MINORVERSION = "CLIENTPV_MINORVERSION";
    private static final String LAST_ACTIVATION_TIME = "LAST_ACTIVATION_TIME";
    private static final String CLIENT_SW_INF = "CLIENT_SW_INFO";
    private static final String CLIENT_PLATFORM_TYPE = "CLIENT_PLATFORM_TYPE";
    private static final String DYNAMIC_QOS_FLAG = "DYNAMICQOSFLAG";
    private static final String VOCODERID = "VOCODERID";
    private static final String SERVICE_STATUS_OP = "SERVICE_STATUS_OP";
    private static final String XDMS_FS1 = "XDMSFS1";
    private static final String DERIVEDKEY = "DERIVED_KEY";
    private static final String LICENSE_TYPE = "LICENSE_TYPE";
    private static final String ACTIVE_FS2 = "ACTIVEFS2";
    private static final String CLIENT_FS2 = "CLIENTFS2";
    private static final String SUBS_FS2 = "SUBSCRIBERFS2";
    private static final String OPS_FS2 = "OPSFS2";
    private static final String CORPADMIN_FS2 = "CORPADMINFS2";
    private static final String CORPADMIN_FS1 = "CORPADMINFS1";
    private static final String XDMS_FS2 = "XDMSFS2";
    private static final String MDN = "MDN";
    private static final String USERPROFILEFS2 = "USERPROFILEFS2";
    private static final String GET_MDN_FOR_UPM = "Select B.MDN,B.USERPROFILEFS2,B.XDMSFS2,B.ACTIVEFS2, B.CORPADMINFS2 from DG.POCSUBSCRINFO A, DG.POCSUBSCRINFO B where A.MC_ID = B.MC_ID and A.MDN = ?";
    private static final String USER_ID = "USER_ID";
    private static final String MC_ID = "MC_ID";
    private static final String MCPTT_COMPLIANCE = "MCPTT_COMPLIANCE";
    public static final String FAN_ID = "FAN_ID";
    public static final String BAN_ID = "BAN_ID";
    public static final String EXTERNAL_FAN_ID = "EXTERNAL_FAN_ID";
    public static final String EXTERNAL_BAN_ID = "EXTERNAL_BAN_ID";


    public String pttServerId = null;

    KnXDMSubscriberInfoDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }


    public void insert(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        knLogger.warn("insert", "Unimplemented Methods");
    }

    public void update(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        knLogger.warn("update", "Unimplemented Methods");
    }

    public void delete(IPersistenceDTO persistencDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        knLogger.warn("delete", "Unimplemented Methods");
    }

    public Collection select(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        knLogger.warn("select", "Unimplemented Methods");
        return null;
    }

    public Collection<KnCorpSubscriberDTO> selectPocSubscriberInfo(IPersistenceDTO persistenceDTO, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "selectPocSubscriberInfo(IPersistenceDTO, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : Input DTO passed persistenceDTO - ", persistenceDTO);

        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String query = null;
        Collection<KnCorpSubscriberDTO> subscribersList = new ArrayList<KnCorpSubscriberDTO>();
        Map<String, KnCorpSubscriberDTO> extContClientMap = new HashMap<>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_CORP_POC_SUBSCRIBER);
            KnCorpMdnListPersistDTO contactMdnListDTO = new KnCorpMdnListPersistDTO();
            if (persistenceDTO instanceof KnCorpMdnListPersistDTO) {
                contactMdnListDTO = (KnCorpMdnListPersistDTO) persistenceDTO;
            }
            int corpId = contactMdnListDTO.getCorpId();
            //conn = persisterTxn.getDBConnection(pttServerId, false);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            List<String> extMdnList = new ArrayList<>(contactMdnListDTO.getAddedMdnList());
            query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedQuesMarks(contactMdnListDTO.getAddedMdnList()));
            knLogger.debug(methodName, "Executing query-", query);
            pStmt = conn.prepareStatement(query);
            int paramIndex = 1;
            for (String mdn : contactMdnListDTO.getAddedMdnList()) {
                pStmt.setString(paramIndex++, mdn);
            }
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                String rawMdn = rs.getString(2);
                if (rawMdn == null) {
                    knLogger.warn(methodName, "Skipping subscriber row with null MDN in corp ", corpId);
                    continue;
                }
                String mdn = rawMdn.trim();
                int mdnCorpId = rs.getInt(1);
                KnCorpSubscriberDTO subscriberDTO = new KnCorpSubscriberDTO();
                subscriberDTO.setMdn(mdn);
                subscriberDTO.setCorpId(mdnCorpId);
                subscriberDTO.setClientType(rs.getInt(6));
                subscriberDTO.setClientPVmajorVer(rs.getInt(10));
                subscriberDTO.setUserAgent(rs.getString(11));
                subscriberDTO.setServiceAuthStatus(rs.getInt(16));
                String activeFS = rs.getString(17) != null ? rs.getString(17) : KnGeneralUtil.convertLongToHexString(rs.getLong(9));
                subscriberDTO.setSubsActiveFS2(activeFS);
                String userProfileId = rs.getString(19);
                if (corpId != mdnCorpId && userProfileId == null) {
                    subscriberDTO.setExternalContact(true);
                    extContClientMap.put(mdn, subscriberDTO);
                    continue;
                } else {
                    subscriberDTO.setExternalContact(false);
                    subscriberDTO.setContact_type(KnConstants.INTERNAL_SUBSC_CLIENT_TYPE);
                    extMdnList.remove(mdn);
                }
                subscriberDTO.setNotfnCapabiliy(KnGeneralUtil.getFeatureBitValue(activeFS, 15));
                //multilingual revert changes
                if (rs.getString(3) != null) {
                    try {
                        subscriberDTO.setName(new String(rs.getString(3).getBytes("8859_1"), "UTF-8").trim());
                    } catch (UnsupportedEncodingException e) {
                        knLogger.error(methodName, " Exception in encoding Subscriber Name for MDN - ", KnGDPRTemplate.mdn(mdn), e);
                    }
                }
                subscriberDTO.setPocHome(rs.getString(7));
                subscriberDTO.setPresenceHome(rs.getString(8));
                subscriberDTO.setSubscriptionType(getMappedSubscriptionType(rs.getInt(4), rs.getInt(5)));
                subscriberDTO.setLicenseType(rs.getInt(13));
                subscriberDTO.setAliasMdn(rs.getString(14));
                subscriberDTO.setUserId(rs.getString(15));
                String subsFS = rs.getString(18) != null ? rs.getString(18) : KnGeneralUtil.convertLongToHexString(rs.getLong(12));
                subscriberDTO.setSubscriberFs2(subsFS);
                subscriberDTO.setUserProfileId(rs.getString(19));
                subscriberDTO.setMcpttCompliance(rs.getInt(20));
                String corpAdminFs1 = KnGeneralUtil.convertLongToHexString(rs.getLong(21));
                subscriberDTO.setCorpAdminFS1(corpAdminFs1);
                subscriberDTO.setCorpAdminFS2(rs.getString(22));
                if (null == subscriberDTO.getCorpAdminFS2()) {
                    subscriberDTO.setCorpAdminFS2(corpAdminFs1);
                }
                subscribersList.add(subscriberDTO);
            }
            knLogger.debug(methodName, "extContClientMap --", extContClientMap);

            List<String> invalidMDNList = new ArrayList<>(extMdnList);
            var mdnListArray = new ArrayList<>(extMdnList);
            var subsLists = KnGeneralUtil.splitList(mdnListArray, BULK_UPDATE_SIZE);
            for (var subsList : subsLists) {
                query = queryMapper.getQuery(GET_EXTERNAL_CONTACT);
                query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedQuesMarks(subsList));
                pStmt = conn.prepareStatement(query);
                paramIndex = 1;
                for (String mdn : subsList) {
                    pStmt.setString(paramIndex++, mdn);
                }
                pStmt.setInt(paramIndex++, corpId);
                knLogger.debug(methodName, "Executing query- ", query);
                rs = pStmt.executeQuery();
                knLogger.debug(methodName, "Query executed successfully");
                while (rs.next()) {
                    KnCorpSubscriberDTO subscriberDTO = new KnCorpSubscriberDTO();
                    String rawExtMdn = rs.getString(1);
                    if (rawExtMdn == null) {
                        knLogger.warn(methodName, "Skipping external contact row with null MDN for corpId ", corpId);
                        continue;
                    }
                    String mdn = rawExtMdn.trim();
                    invalidMDNList.remove(mdn);
                    subscriberDTO.setMdn(mdn);
                    subscriberDTO.setExternalContact(true);
                    subscriberDTO.setCorpId(rs.getInt(2));
                    //multilingual revert changes
                    if (rs.getString(3) != null) {
                        subscriberDTO.setName(new String(rs.getString(3).getBytes("8859_1"), "UTF-8"));
                    }
                    subscriberDTO.setContact_type(rs.getInt(4));
                    knLogger.debug(methodName, "mdn in loop --", KnGDPRTemplate.mdn(mdn));
                    knLogger.debug(methodName, "extContClientMap --", extContClientMap.containsKey(mdn));
                    if (extContClientMap.containsKey(mdn)) {
                        knLogger.debug(methodName, "extContClientMap --", extContClientMap.get(mdn));
                        subscriberDTO.setClientType(extContClientMap.get(mdn).getClientType());
                        subscriberDTO.setContact_type(KnConstants.XCAP_DIFF_CONTACT_TYPE_EXTERNAL_CONTACT);
                        subscriberDTO.setSubsActiveFS2(extContClientMap.get(mdn).getSubsActiveFS2());
                        subscriberDTO.setUserAgent(extContClientMap.get(mdn).getUserAgent());
                        subscriberDTO.setClientPVmajorVer(extContClientMap.get(mdn).getClientPVmajorVer());
                        subscriberDTO.setServiceAuthStatus(extContClientMap.get(mdn).getServiceAuthStatus());
                        knLogger.debug(methodName, "subscriberDTO", subscriberDTO);
                    }
                    if (subscriberDTO.getCorpId() == 0) {
                        subscriberDTO.setContact_type(KnConstants.XCAP_DIFF_CONTACT_TYPE_EXTERNAL_SUBSCRIBER);
                    }
                    subscribersList.add(subscriberDTO);
                }
            }
            //Adding invalid subscribers present in request in response object.
            for (String mdn : invalidMDNList) {
                KnCorpSubscriberDTO subscriberDTO = new KnCorpSubscriberDTO();
                subscriberDTO.setMdn(mdn);
                subscriberDTO.setExternalContact(true);
                subscriberDTO.setContact_type(KnConstants.XCAP_DIFF_CONTACT_TYPE_EXTERNAL_SUBSCRIBER);
                subscribersList.add(subscriberDTO);
            }

            return subscribersList;

        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while fetching the PoC subcribers List - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while fetching the PoC subcribers List - ",
                    e);
            throw KnDbUtil.processException(e, "Failed while fetching the PoC subcribers List ." + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            knLogger.debug(methodName, "EXIT : PoC subscribers list returned with no of members - ", subscribersList.size());
        }
    }

    public void updateSubscCorpListId(String mdn, int privateCorpListId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateSubscCorpListId(String, int,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : Input Data passed - mdn ~ ", KnGDPRTemplate.mdn(mdn), ", privateCorpListId ~ ", privateCorpListId);
        Connection conn;
        PreparedStatement pstmt = null;
        String updateQuery = null;
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            updateQuery = queryMapper.getQuery(UPDATE_SUBSCRIBER_CORP_ID);
            pstmt = conn.prepareStatement(updateQuery);
            if (privateCorpListId == 0) {
                pstmt.setNull(1, Types.INTEGER);
            } else {
                pstmt.setInt(1, privateCorpListId);
            }
            pstmt.setString(2, mdn);
            knLogger.debug(methodName, "Executing query - ", "'", updateQuery, "'");
            pstmt.executeQuery();
            knLogger.debug(methodName, "EXIT: Query executed successfully.");
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while subcribers private List Id - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while subcribers private List Id - ",
                    e);
            throw KnDbUtil.processException(e, "Failed to updateSubscribers Private List Id." + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, updateQuery);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public Collection<String> getCorporateSpecificPoCSubscribersFrmList(Collection<String> mdnList,
                                                                        int corpId,
                                                                        KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getCorporateSpecificPoCSubscribersFrmList(Collection<String>, int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : Input Dto passed  mdnList ~ ", mdnList == null ? mdnList : KnGDPRTemplate.mdnList(mdnList), ", corpId ~ ", corpId);
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        int index = 1;
        Collection<String> subscribersList = new ArrayList<String>();
        String query = null;
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_POC_SUBSCRIBER_FOR_CORP_FRM_MDN_LIST);
            query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedQuesMarks(mdnList));
            knLogger.debug(methodName, "query", query);
            pStmt = conn.prepareStatement(query);
            for (String mdn : mdnList) {
                pStmt.setString(index++, mdn);
            }
            pStmt.setInt(index, corpId);
            rs = pStmt.executeQuery();
            while (rs.next()) {
                String mdn = rs.getString(1).trim();
                subscribersList.add(mdn);
            }
            knLogger.debug(methodName, "Query executed successfully Fetched list size is :  ", subscribersList.size());
            return subscribersList;

        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving the PoC subscriber from the DB",
                    " present in passed mdnList - " + e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while retrieving the PoC subscriber from the DB",
                    " present in passed mdnList- " + e);
            throw KnDbUtil.processException(e, "Failed  while retrieving the PoC subscriber from the DB" +
                            " present in passed mdnList- " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            knLogger.debug(methodName, "EXIT : Subscriber List size found in DB - ", subscribersList.size());
        }
    }

    /**
     * This method retrieve all subscribers in the corporation and returns a Map of all subscribers and
     * deactivated subscribers
     *
     * @param corpId
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public KnCorpContactBean getInternalSubscriberList(int corpId, Integer filterType, Integer fetchSize, Integer nextToken, Integer sortType
            , boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getInternalSubscriberList(int,Integer,Integer,Integer,Integer,boolean,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : CorpId", corpId, "filterType", filterType, "fetchSize", fetchSize, "nextToken", nextToken, "sortType", sortType);
        KnCorpContactBean corpContactBean = new KnCorpContactBean();
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            knLogger.debug(methodName, "Query Mapper - ", queryMapper);
            if (filterType != 0) {
                query = queryMapper.getQuery(GET_INTERNAL_SUBSCRIBER_PAGINATED);
                if (sortType == 0) {
                    query = replaceContactWithValue(query, "ORDER BY SORT_TYPE", "");
                } else {
                    query = replaceContactWithValue(query, "SORT_TYPE", sortType == 1 ? "CAST(MDN AS NUMBER)" : "SUBSCRNAME");
                }
                int startIndex = getStartIndex(fetchSize, nextToken);
                int endIndex = getEndIndex(fetchSize, nextToken);
                pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, startIndex);
                pstmt.setInt(2, endIndex);
                pstmt.setInt(3, corpId);
            } else {
                query = queryMapper.getQuery(GET_INTERNAL_SUBSCRIBER);
                pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, corpId);
            }

            Map<String, KnCorpSubscriberDTO> subscriberDTOMap = new LinkedHashMap<String, KnCorpSubscriberDTO>();
            List<String> mdnList = new ArrayList<String>();
            List<String> provisionedMdnList = new ArrayList<String>();
            List<String> thirdPartyMdns = new ArrayList<String>();
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                KnCorpSubscriberDTO subsDTO = new KnCorpSubscriberDTO();
                String mdn = rs.getString(1).trim();
                subsDTO.setMdn(mdn);
                //multilingual revert changes
                if (rs.getString(2) != null) {
                    try {
                        subsDTO.setName(new String(rs.getString(2).getBytes("8859_1"), "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        knLogger.error(methodName, " Exception in encoding Subscriber Name for MDN - ", KnGDPRTemplate.mdn(mdn), e);
                    }
                }
                subsDTO.setCorpId(corpId);
                subsDTO.setServiceAuthStatus(rs.getInt(3));
                subsDTO.setSubscriptionType(getMappedSubscriptionType(rs.getInt(4), rs.getInt(5)));
                Integer clientType = rs.getInt(6);
                subsDTO.setClientType(clientType);
                if (clientType == THIRDPARTYPOCCLIENT.value()
                        || clientType == MOBILE_CLIENT.value()
                        || clientType == THIRDPARTYDISPATCHERCLIENT.value()) {
                    thirdPartyMdns.add(mdn);
                }
                subsDTO.setUserId(rs.getString(8));
                subsDTO.setDispatchType(rs.getInt(9));
                subsDTO.setAliasMdn(rs.getString(10));
                subsDTO.setLicenseType(rs.getInt(11));
                String activeFS = rs.getString(12) != null ? rs.getString(12) : KnGeneralUtil.convertLongToHexString(rs.getLong(7));
                subsDTO.setSubsActiveFS2(activeFS);
                subsDTO.setMcpttCompliance(rs.getInt(13));
                subsDTO.setCameraType((Integer) rs.getObject(14));
                subsDTO.setSubscriberFs2(rs.getString(15));
                subsDTO.setExtGatewayId(rs.getString(16));

                subscriberDTOMap.put(mdn, subsDTO);
                mdnList.add(mdn);
                if (rs.getInt(3) == 0) {
                    provisionedMdnList.add(mdn);
                }
            }
            corpContactBean.setCorpSubscriberDTOMap(subscriberDTOMap);
            corpContactBean.setProvisionedMdnList(provisionedMdnList);
            corpContactBean.setThirdPartyUsers(thirdPartyMdns);
            knLogger.debug(methodName, "Exit: subscriberDTOMap", subscriberDTOMap.size(), "provisionedMdnList", provisionedMdnList.size(), "thirdPartyMdns", thirdPartyMdns.size());
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while retrieving the corporate subscribers ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        return corpContactBean;
    }

    public Map<String, Map<String, Object>> getInternalSubscriberPackageMap(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getInternalSubscriberPackageMap(List<String>,Integer,Integer,Integer,Integer,boolean,KnPersisterTxn)";
        knLogger.entry(methodName, "ENTRY : mdnList size ", mdnList.size());
        Connection conn;
        PreparedStatement pstmt = null;
        PreparedStatement pstmt2 = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        String query = null;
        String query2 = null;
        Map<String, Map<String, Object>> SubscriberPackageMap = new LinkedHashMap<>();
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            var subsLists = KnGeneralUtil.splitList(mdnList, BULK_UPDATE_SIZE);
            for (var subsList : subsLists) {
                int index = 1;
                int index2 = 1;
                query = "SELECT MDN, ADDON_PKGCODE FROM DG.SUBSCR_ADDON_PKGINFO where MDN IN (" + MDNLIST + ")";
                query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedQuesMarks(subsList));
                pstmt = conn.prepareStatement(query);
                query2 = "SELECT MDN, TIER_PKG_CODE FROM DG.POCSUBSCR_ADDLINFO where MDN IN (" + MDNLIST + ")";
                query2 = replaceContactWithValue(query2, MDNLIST, formCommaSeperatedQuesMarks(subsList));
                pstmt2 = conn.prepareStatement(query2);
                for (String mdn : subsList) {
                    pstmt.setString(index++, mdn);
                    pstmt2.setString(index2++, mdn);
                }
                knLogger.debug(methodName, "Executing query - ", query);
                rs = pstmt.executeQuery();
                knLogger.debug(methodName, "Executing query - ", query2);
                rs2 = pstmt2.executeQuery();
                knLogger.debug(methodName, "Query executed successfully");
                List<String> addOnPackageList;
                while (rs2.next()) {
                    String mdn = rs2.getString(1).trim();
                    String tierPackage = rs2.getString(2);
                    Map<String, Object> packageMap = new HashMap<>();
                    packageMap.put("tierPackage", tierPackage);
                    packageMap.put("addOnPackage", new ArrayList<>());
                    SubscriberPackageMap.put(mdn, packageMap);
                }
                while (rs.next()) {
                    String mdn = rs.getString(1).trim();
                    String addOnPackage = rs.getString(2);
                    Map<String, Object> packageMap = SubscriberPackageMap.get(mdn);
                    if (packageMap.containsKey(mdn)) {
                        addOnPackageList = (List<String>) packageMap.get(mdn);
                        addOnPackageList.add(addOnPackage);
                    } else {
                        addOnPackageList = new ArrayList<>();
                        addOnPackageList.add(addOnPackage);
                    }
                    packageMap.put("addOnPackage", addOnPackageList);
                    SubscriberPackageMap.put(mdn, packageMap);
                }
            }
            knLogger.debug(methodName, "Exit: SubscriberPackageMap size", SubscriberPackageMap.size());
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while retrieving the corporate subscribers ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeResultSet(rs2);
            KnDbUtil.closePreparedStatement(pstmt);
            KnDbUtil.closePreparedStatement(pstmt2);
        }
        return SubscriberPackageMap;
    }

    /**
     * This method retrieve all Internal Interop subscribers in the corporation
     *
     * @param corpId
     * @param persisterTxn
     * @return KnCorpContactBean
     * @throws KnDAOException
     */
    public KnCorpContactBean getInternalInteropSubscriberList(int corpId, Integer filterType, Integer fetchSize, Integer nextToken, Integer sortType, boolean readOnly
            , KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getInternalInteropSubscriberList(int,Integer,Integer,Integer,Integer,boolean,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : CorpId", corpId, "filterType", filterType, "fetchSize", fetchSize, "nextToken", nextToken, "sortType", sortType);
        KnCorpContactBean corpContactBean = new KnCorpContactBean();
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            knLogger.debug(methodName, "Query Mapper - ", queryMapper);
            query = queryMapper.getQuery(GET_INTERNAL_INTEROP_SUBSCRIBER_PAGINATED);
            if (sortType == 0) {
                query = replaceContactWithValue(query, "ORDER BY SORT_TYPE", "");
            } else {
                query = replaceContactWithValue(query, "SORT_TYPE", sortType == 1 ? "CAST(MDN AS NUMBER)" : "SUBSCRNAME");
            }
            int startIndex = getStartIndex(fetchSize, nextToken);
            int endIndex = getEndIndex(fetchSize, nextToken);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, startIndex);
            pstmt.setInt(2, endIndex);
            pstmt.setInt(3, corpId);

            Map<String, KnCorpSubscriberDTO> subscriberDTOMap = new LinkedHashMap<>();
            List<String> mdnList = new ArrayList<>();
            List<String> provisionedMdnList = new ArrayList<>();
            List<String> thirdPartyMdns = new ArrayList<>();
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                KnCorpSubscriberDTO subsDTO = new KnCorpSubscriberDTO();
                String mdn = rs.getString(1).trim();
                subsDTO.setMdn(mdn);
                //multilingual revert changes
                if (rs.getString(2) != null) {
                    try {
                        subsDTO.setName(new String(rs.getString(2).getBytes("8859_1"), "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        knLogger.error(methodName, " Exception in encoding Subscriber Name for MDN - ", KnGDPRTemplate.mdn(mdn), e);
                    }
                }
                subsDTO.setCorpId(corpId);
                subsDTO.setServiceAuthStatus(rs.getInt(3));
                subsDTO.setSubscriptionType(getMappedSubscriptionType(rs.getInt(4), rs.getInt(5)));
                Integer clientType = rs.getInt(6);
                subsDTO.setClientType(clientType);
                if (clientType == THIRDPARTYPOCCLIENT.value()
                        || clientType == MOBILE_CLIENT.value()
                        || clientType == THIRDPARTYDISPATCHERCLIENT.value()) {
                    thirdPartyMdns.add(mdn);
                }
                subsDTO.setUserId(rs.getString(8));
                subsDTO.setDispatchType(rs.getInt(9));
                subsDTO.setAliasMdn(rs.getString(10));
                subsDTO.setLicenseType(rs.getInt(11));
                String activeFS = rs.getString(12) != null ? rs.getString(12) : KnGeneralUtil.convertLongToHexString(rs.getLong(7));
                subsDTO.setSubsActiveFS2(activeFS);
                subsDTO.setMcpttCompliance(rs.getInt(13));
                subsDTO.setCameraType((Integer) rs.getObject(14));
                subsDTO.setMcpttId(rs.getBytes(15) != null ? new String(rs.getBytes(15), StandardCharsets.UTF_8) : null);
                subsDTO.setSubscriberFs2(rs.getString(16));
                subsDTO.setDeviceId(rs.getString(17));

                subscriberDTOMap.put(mdn, subsDTO);
                mdnList.add(mdn);
                if (rs.getInt(3) == 0) {
                    provisionedMdnList.add(mdn);
                }
            }
            corpContactBean.setCorpSubscriberDTOMap(subscriberDTOMap);
            corpContactBean.setProvisionedMdnList(provisionedMdnList);
            corpContactBean.setThirdPartyUsers(thirdPartyMdns);
            knLogger.debug(methodName, "Exit: subscriberDTOMap", subscriberDTOMap.size(), "provisionedMdnList", provisionedMdnList.size(), "thirdPartyMdns", thirdPartyMdns.size());
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while retrieving the corporate subscribers ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        return corpContactBean;
    }

    public Map<String, Map<String, KnCorpSubscriberDTO>> getCorpAllSubscribers(int corpId, int maxAllowedContactCount
            , KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getCorpAllSubscribers(int, int, Collection<KnCorpSubscriberDTO>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : CorpId -  ", corpId);
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        Map<String, Map<String, KnCorpSubscriberDTO>> subscriberDeActMap = new HashMap<String, Map<String, KnCorpSubscriberDTO>>();
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            knLogger.debug(methodName, "Query Mapper - ", queryMapper);
            query = queryMapper.getQuery(GET_CORP_CONTACT_DETAILS);
            Map<String, KnCorpSubscriberDTO> subscriberDTOMap = new HashMap<String, KnCorpSubscriberDTO>();
            Map<String, KnCorpSubscriberDTO> deActSubsMap = new HashMap<String, KnCorpSubscriberDTO>();
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, corpId);
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                String mdn = rs.getString(1).trim();
                //multilingual revert changes
                String contactName = rs.getString(2);
                if (contactName != null) {
                    try {
                        contactName = new String(contactName.getBytes("8859_1"), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        knLogger.error(methodName, " Exception in encoding Subscriber Name - ", KnGDPRTemplate.mdn(mdn), e);
                    }
                }
                int contactCorpId = rs.getInt(6);
                KnCorpSubscriberDTO subsDTO = new KnCorpSubscriberDTO(mdn, contactName, contactCorpId);
                subsDTO.setServiceAuthStatus(rs.getInt(3));
                int publicType = rs.getInt(4);
                int corpType = rs.getInt(5);
                int finalSubscriptionType = getMappedSubscriptionType(publicType, corpType);
                int contactCount = rs.getInt(7);
                if (contactCount > maxAllowedContactCount) {
                    subsDTO.setMaxContactLimitFlag(GREATER_THAN_LIMIT);
                } else if (contactCount == maxAllowedContactCount) {
                    subsDTO.setMaxContactLimitFlag(EQUAL_TO_LIMIT);
                } else if (contactCount < maxAllowedContactCount) {
                    subsDTO.setMaxContactLimitFlag(LESS_THAN_LIMIT);
                }
                subsDTO.setContactCount(contactCount);
                subsDTO.setSubscriptionType(finalSubscriptionType);
                subsDTO.setClientType(rs.getInt(8));
                String activeFS = rs.getString(10) != null ? rs.getString(10) : KnGeneralUtil.convertLongToHexString(rs.getLong(9));
                subsDTO.setSubsActiveFS2(activeFS);
                subsDTO.setUserId(rs.getString(11));
                subsDTO.setDispatchType(rs.getInt(12));
                subsDTO.setAliasMdn(rs.getString(13));
                subsDTO.setLicenseType(rs.getInt(14));
                subsDTO.setPamAccId(rs.getInt(15));
                String subsFS= rs.getString(17) != null ? rs.getString(17) : KnGeneralUtil.convertLongToHexString(rs.getLong(16));
                subsDTO.setSubscriberFs2(subsFS);
                subscriberDTOMap.put(mdn, subsDTO);
                if (subsDTO.getServiceAuthStatus() == 0) {
                    deActSubsMap.put(mdn, subsDTO);
                }
            }
            subscriberDeActMap.put(CORPORATE_SUBSRIBERS, subscriberDTOMap);
            subscriberDeActMap.put(CORP_DEACTIVARED_LIST, deActSubsMap);
            knLogger.debug(methodName, "Exit: subscriberDTOMap :  ", subscriberDTOMap.size(), "deActSubsMap", deActSubsMap.size());
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while retrieving the corporate subscribers ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        return subscriberDeActMap;
    }

    /**
     * This method retrieve all subscribers in the corporation and returns a Map of all subscribers and
     * deactivated subscribers
     *
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */


    public Map<String, Collection<KnCorpSubscriberDTO>> getSubscPrivateInternalExternalContacts(
            KnIPCorpContactDTO contactDTO,
            int corpListId,
            int maxContactLimit, boolean readOnly,
            KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubscPrivateInternalExternalContacts(KnIPCorpContactDTO, int, int,boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY. contactDTO - ", contactDTO, ", corpListId - ", corpListId
                , ", maxContactLimit - ", maxContactLimit, " ,persisterTxn -", persisterTxn);
        Connection conn;
        PreparedStatement pstmt = null;
        PreparedStatement pstmtFinal = null;
        ResultSet rs = null;
        ResultSet resultSet = null;
        Map<String, Collection<KnCorpSubscriberDTO>> subscContactMap = new HashMap<String, Collection<KnCorpSubscriberDTO>>();
        String query = null;
        try {
            int corpId = contactDTO.getCorpId();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_SUBS_PRIVATE_CONTACTS);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, corpListId);
            rs = pstmt.executeQuery();
            Collection<String> mdnList = new ArrayList<String>();
            Collection<String> externalMdnList = new ArrayList<String>();
            while (rs.next()) {
                String mdn = rs.getString(1);
                mdnList.add(mdn);
                if (rs.getInt(2) != corpId) {
                    externalMdnList.add(mdn);
                }
            }
            Collection<KnCorpSubscriberDTO> internalContacts = new ArrayList<KnCorpSubscriberDTO>();
            Collection<KnCorpSubscriberDTO> externalContacts = new ArrayList<KnCorpSubscriberDTO>();
            Collection<String> dbExternalContacts = new ArrayList<String>();
            var mdnListArray = new ArrayList<>(mdnList);
            var subsLists = KnGeneralUtil.splitList(mdnListArray, BULK_UPDATE_SIZE);
            knLogger.debug(methodName,"SubLists getSubscPrivateInternalExternalContacts",subsLists);
            for (var subsList : subsLists) {
                query = queryMapper.getQuery(GET_SUBS_INT_EXT_CONTACTS);
                query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedQuesMarks(subsList));
                knLogger.debug(methodName, "query", query);
                int index = 1;
                pstmtFinal = conn.prepareStatement(query);
                for (String mdn : subsList) {
                    pstmtFinal.setString(index++, mdn);
                }
                resultSet = pstmtFinal.executeQuery();
                knLogger.debug(methodName, "Query executed successfully");
                List<String> extSubscrList = new ArrayList<>(subsList);
                while (resultSet.next()) {
                    //filtering async deletion mdns
                    if(resultSet.getInt(3) == MARKED_FOR_ASYNC_DELETION.value()){
                        continue;
                    }
                    String mdn = resultSet.getString(1).trim();
                    extSubscrList.remove(mdn);
                    String contactName = resultSet.getString(2);
                    //multilingual revert changes
                    if (contactName != null) {
                        try {
                            contactName = new String(contactName.getBytes("8859_1"), "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            knLogger.error(methodName, " Exception in encoding Subscriber Name - ", KnGDPRTemplate.mdn(mdn), e);
                        }
                    }
                    int contactCorpId = resultSet.getInt(6);
                    int finalSubscriptionType =
                            getMappedSubscriptionType(resultSet.getInt(4), resultSet.getInt(5));
                    int contactCount = resultSet.getInt(7);
                    KnCorpSubscriberDTO subsDTO = new KnCorpSubscriberDTO(mdn, contactName, contactCorpId);
                    subsDTO.setServiceAuthStatus(resultSet.getInt(3));
                    subsDTO.setAliasMdn(resultSet.getString(9));
                    subsDTO.setUserId(resultSet.getString(10));
                    String activeFS = resultSet.getString(12) != null ? resultSet.getString(12) : KnGeneralUtil.convertLongToHexString(resultSet.getLong(11));
                    subsDTO.setSubsActiveFS2(activeFS);
                    if (corpId == contactCorpId) {
                        if (contactCount > maxContactLimit) {
                            subsDTO.setMaxContactLimitFlag(GREATER_THAN_LIMIT);
                        } else if (contactCount == maxContactLimit) {
                            subsDTO.setMaxContactLimitFlag(EQUAL_TO_LIMIT);
                        } else if (contactCount < maxContactLimit) {
                            subsDTO.setMaxContactLimitFlag(LESS_THAN_LIMIT);
                        }
                        subsDTO.setSubscriptionType(finalSubscriptionType);
                        internalContacts.add(subsDTO);
                        subsDTO.setContactCount(contactCount);
                        subsDTO.setClientType(resultSet.getInt(8));
                        subsDTO.setClientPVmajorVer(resultSet.getInt(13));
                    } else {
                        subsDTO.setMaxContactLimitFlag(LESS_THAN_LIMIT);
                        subsDTO.setSubscriptionType(EXTERNAL_CONTACT_TYPE);
                        externalContacts.add(subsDTO);
                        dbExternalContacts.add(mdn);
                    }
                }
                if (!internalContacts.isEmpty()) {
                    subscContactMap.put(INTERNAL, internalContacts);
                }
                if (!extSubscrList.isEmpty()) {
                    for (String extSubscrMdn : extSubscrList) {
                        KnCorpSubscriberDTO subscriberDTO = new KnCorpSubscriberDTO();
                        subscriberDTO.setMdn(extSubscrMdn);
                        externalContacts.add(subscriberDTO);
                    }
                }
                if (!externalContacts.isEmpty()) {
                    subscContactMap.put(EXTERNAL, externalContacts);
                }
                if (!externalContacts.isEmpty()) {
                    subscContactMap.put(EXTERNAL, externalContacts);
                }
            }
            knLogger.debug(methodName, "Exit: Map size is - ", subscContactMap.size());
            return subscContactMap;

        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while retrieving the private conatcts for the subscriber",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeResultSet(resultSet);
            KnDbUtil.closePreparedStatement(pstmt);
            KnDbUtil.closePreparedStatement(pstmtFinal);
        }
    }

    public Map<String, KnCorpSubscriberDTO> getSubsribersCorporateDetails(Collection<String> mdnList,
                                                                          KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "getSubsribersCorporateDetails(Collection<String>, KnPersisterTxn)";

        knLogger.entry(methodName, "ENTRY : mdnList size ", mdnList.size());
        Map<String, KnCorpSubscriberDTO> subscribersMap = new HashMap<String, KnCorpSubscriberDTO>();
        if (mdnList == null || mdnList.isEmpty()) {
            knLogger.exit(methodName, "EXIT : returnList size ", subscribersMap);
            return subscribersMap;
        }

        var mdnListArray = new ArrayList<>(mdnList);
        var subsLists = KnGeneralUtil.splitList(mdnListArray, BULK_UPDATE_SIZE);
        for (var subsList : subsLists) {
            var subSubsribersCorporateDetails = getSubSubsribersCorporateDetails(subsList, persisterTxn);
            if (subSubsribersCorporateDetails != null && !subSubsribersCorporateDetails.isEmpty()){
                subscribersMap.putAll(subSubsribersCorporateDetails);
            }
        }
        knLogger.exit(methodName, "EXIT : subscribersMap size ", subscribersMap == null ? 0 : subscribersMap.size());
        return subscribersMap;
    }

    private Map<String, KnCorpSubscriberDTO> getSubSubsribersCorporateDetails(Collection<String> mdnList,
                                                                              KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "getSubSubsribersCorporateDetails(Collection<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :MdnList - ", mdnList == null ? mdnList : KnGDPRTemplate.mdnList(mdnList));

        Connection conn = null;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String query = null;
        int index = 1;
        boolean ownedTxn = false;
        Map<String, KnCorpSubscriberDTO> subscribersMap = new HashMap<String, KnCorpSubscriberDTO>();
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_POC_SUBSCRIBER);
            //conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            if (persisterTxn != null) {
                knLogger.debug(methodName, " Data Store before - ", KnDBConst.DataStores.XDM_SHARED_DATA);
                conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
                knLogger.debug(methodName, "if block");
            } else {
                conn = KnDBManager.getDBManagerInstance().getConnection(KnGenInfoUtil.getSecDBPttServerId(pttServerId), true);
                ownedTxn = true;
                knLogger.debug(methodName, "else block");
                knLogger.debug(methodName, "conn", conn);
            }
            query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedQuesMarks(mdnList));
            knLogger.debug(methodName, "query", query);
            pStmt = conn.prepareStatement(query);
            for (String mdn : mdnList) {
                pStmt.setString(index++, mdn);
            }
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                KnCorpSubscriberDTO subscriberDTO = new KnCorpSubscriberDTO();
                String mdn = rs.getString(2).trim();
                subscriberDTO.setMdn(mdn);
                int mdnCorpId = rs.getInt(1);
                subscriberDTO.setCorpId(mdnCorpId);
                //multilingual revert changes
                if (rs.getString(3) != null) {
                    try {
                        subscriberDTO.setName(new String(rs.getString(3).getBytes("8859_1"), "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        knLogger.error(methodName, " Exception in encoding Subscriber Name for MDN - ", rs.getString(2), e);
                    }
                }
                int publicType = rs.getInt(4);
                int corpType = rs.getInt(5);
                subscriberDTO.setSubscriptionType(getMappedSubscriptionType(publicType, corpType));
                subscriberDTO.setClientType(rs.getInt(6));
                subscriberDTO.setPocHome(rs.getString(7));
                subscriberDTO.setPresenceHome(rs.getString(8));
                subscriberDTO.setNotfnCapabiliy(KnGeneralUtil.getFeatureBitValue(rs.getString(10), 15));
                String activeFS = rs.getString(10) != null ? rs.getString(10) : KnGeneralUtil.convertLongToHexString(rs.getLong(9));
                subscriberDTO.setSubsActiveFS2(activeFS);
                subscriberDTO.setClientPVmajorVer(rs.getInt(11));
                subscriberDTO.setServiceAuthStatusAU(rs.getInt(13));
                String subsFS = rs.getString(15) != null ? rs.getString(15) : KnGeneralUtil.convertLongToHexString(rs.getLong(14));
                subscriberDTO.setSubscriberFs2(subsFS);
                subscriberDTO.setAliasMdn(rs.getString(16));
                subscriberDTO.setUserId(rs.getString(17));
                subscriberDTO.setMcpttCompliance(rs.getInt(18));
                subscriberDTO.setUserProfileIndex(rs.getInt(19));
                subscribersMap.put(mdn, subscriberDTO);
            }
            if (ownedTxn) {
                knLogger.debug(methodName, "saving the transaction ");
                persisterTxn.save();
            }
            // Collection<String> keySet = subscribersMap.keySet();
            knLogger.debug(methodName, "Fetched map size is  :  ", subscribersMap.size());
            return subscribersMap;

        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getting corp subscriber details from the mdn list passed - "
                    + e);
            if(ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while getting corp subscriber details ",
                    "from the mdn list passed -", e);
            throw KnDbUtil.processException(e, "Failed while getting corp subscriber details from the mdn list passed" + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            if (ownedTxn) {
                KnDbUtil.closeConnection(conn);
            }
            knLogger.debug(methodName, "EXIT :Fetched map size is  :", subscribersMap.size());
        }
    }

    public KnMdnDetailsPersistDTO getPoCSubscriberDetails(Collection<String> mdnList, boolean readOnly,
                                                          KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "getPoCSubscriberDetails(Collection<String>,boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :MdnList - ", mdnList == null ? mdnList : KnGDPRTemplate.mdnList(mdnList), " readOnly :", readOnly);

        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String query = null;
        int index = 1;
        KnMdnDetailsPersistDTO subscribersMap = new KnMdnDetailsPersistDTO();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            Map<String, Integer> mdnCliMap = new HashMap<>();
            query = queryMapper.getQuery(GET_POC_SUBSCRIBER);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedQuesMarks(mdnList));
            knLogger.debug(methodName, "query", query);
            pStmt = conn.prepareStatement(query);
            for (String mdn : mdnList) {
                pStmt.setString(index++, mdn);
            }
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                mdnCliMap.put(rs.getString(2).trim(), rs.getInt(6));
            }   // Collection<String> keySet = subscribersMap.keySet();
            subscribersMap.setMdnClientTypeMap(mdnCliMap);
            knLogger.debug(methodName, "Fetched map  is  :  ", subscribersMap.getMdnClientTypeMap());
            return subscribersMap;

        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getting corp subscriber details from the mdn list passed - "
                    + e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while getting corp subscriber details ",
                    "from the mdn list passed -", e);
            throw KnDbUtil.processException(e, "Failed while getting corp subscriber details from the mdn list passed" + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            knLogger.debug(methodName, "EXIT :Fetched map size is  :", subscribersMap);
        }
    }

    public Collection<KnCorpSubscriberDTO> selectDistinctSublistMembers(Collection<KnCorpSubscriberDTO> addedMemList, Collection<Integer>
            addedSublistIds, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "selectDistinctSublistMembers(Collection<KnCorpSubscriberDTO>, Collection<Integer>, int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : addedMdnList - ", addedMemList, ", addedSublistIds - ", addedSublistIds);

        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        ResultSet selectRs = null;
        String query = null;
        String querye = null;
        KnCorpSubscriberDTO contactDto = null;
        int index = 1;
        Collection<KnCorpSubscriberDTO> memberList = new ArrayList<KnCorpSubscriberDTO>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(SELECT_DISTINCT_SUBLIST_MEMBERS);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            Collection<String> extMemberList = new ArrayList<String>();
            if (addedSublistIds != null && !addedSublistIds.isEmpty()) {
                query = replaceContactWithValue(query, SUBLISTID, formCommaSeperatedIntegerQuesMarks(addedSublistIds));
                knLogger.debug(methodName, "query", query);
                pStmt = conn.prepareStatement(query);
                for (Integer mdn : addedSublistIds) {
                    pStmt.setInt(index++, mdn);
                }
                rs = pStmt.executeQuery();
                knLogger.debug(methodName, "Query executed successfully.");
                while (rs.next()) {
                    String mdn = rs.getString(1).trim();
                    int memCorpId = rs.getInt(2);
                    //multilingual revert changes
                    if (rs.getString(3) != null) {
                        try {
                            contactDto = new KnCorpSubscriberDTO(mdn, new String(rs.getString(3).getBytes("8859_1"), "UTF-8"), memCorpId);
                        } catch (UnsupportedEncodingException e) {
                            knLogger.error(methodName, " Exception in encoding Subscriber Name - ", KnGDPRTemplate.mdn(mdn), e);
                        }
                    } else {
                        contactDto = new KnCorpSubscriberDTO(mdn, rs.getString(3), memCorpId);
                    }
                    if (corpId != memCorpId) {
                        extMemberList.add(mdn);
                    }
                    if (addedMemList != null) {
                        if (addedMemList.contains(contactDto)) {
                            continue;
                        }
                    }
                    if (!memberList.contains(contactDto)) {
                        memberList.add(contactDto);
                    }
                }
            }
            if (!extMemberList.isEmpty()) {
                var mdnListArray = new ArrayList<>(extMemberList);
                var subsLists = KnGeneralUtil.splitList(mdnListArray, BULK_UPDATE_SIZE);
                for (var subsList : subsLists) {
                    String selectQuery = queryMapper.getQuery(GET_EXTERNAL_CONTACT);
                    index = 1;
                    querye = replaceContactWithValue(selectQuery, MDNLIST, formCommaSeperatedQuesMarks(subsList));
                    knLogger.debug(methodName, "query", querye);
                    pStmt = conn.prepareStatement(querye);
                    for (String mdn : subsList) {
                        pStmt.setString(index++, mdn);
                    }
                    pStmt.setInt(index, corpId);
                    selectRs = pStmt.executeQuery();
                    while (selectRs.next()) {
                        String contactMdn = selectRs.getString(1).trim();
                        String name = selectRs.getString(3);
                        for (KnCorpSubscriberDTO subsc : memberList) {
                            if (subsc.getMdn().equals(contactMdn)) {
                                //multilingual revert changes
                                if (name != null) {
                                    try {
                                        name = new String(name.getBytes("8859_1"), "UTF-8");
                                    } catch (UnsupportedEncodingException e) {
                                        knLogger.error(methodName, " Exception in encoding Contact Name - ", e);
                                    }
                                }
                                subsc.setName(name);
                            }
                        }
                    }
                }
            }
            if (addedMemList != null) {
                memberList.addAll(addedMemList);
            }
            return memberList;

        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while fetching the distinct members from DB - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while fetching the distinct Sublist members from DB- ",
                    e);
            throw KnDbUtil.processException(e, "Failed while fetching the distinct Sublist members from DB" + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeResultSet(selectRs);
            KnDbUtil.closeStatement(pStmt);
            knLogger.debug(methodName, "EXIT : No of members  - ", memberList.size());
        }

    }


    public Map<String, Collection<KnCorpContactDTO>> getContactDetails(Collection<KnCorpSubscriberDTO> membersList,
                                                                       int corpId, int maxContactLimit,
                                                                       KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getContactDetails(Collection<KnCorpSubscriberDTO>, int, int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :MembersList ~ ", membersList, ", corpId ~ ", corpId, ", maxContactLimit ~ ", maxContactLimit);

        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String query = null;

        Collection<KnCorpContactDTO> inetrnalContacts = new ArrayList<KnCorpContactDTO>();
        Collection<KnCorpContactDTO> externalContacts = new ArrayList<KnCorpContactDTO>();
        Map<String, Collection<KnCorpContactDTO>> completeContactList = new HashMap<String, Collection<KnCorpContactDTO>>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            StringBuilder builder = new StringBuilder(500);
            builder.append("'");
            Collection<String> externalMdnList = new ArrayList<String>();
            List<String> mdnList = new ArrayList<>();
            for (KnCorpSubscriberDTO subscriber : membersList) {
                String mdn = subscriber.getMdn();
                mdnList.add(subscriber.getMdn());
                builder.append(mdn).append("','");
                if (subscriber.getCorpId() != corpId) {
                    externalMdnList.add(mdn);
                }
            }
            String mdnStr = "";
            if (builder.lastIndexOf(",") > 0) {
                mdnStr = builder.substring(0, builder.lastIndexOf(",'"));
            }
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = queryMapper.getQuery(GET_SUBSCRIBERS_DETAILS);
            int index = 1;
            query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedQuesMarks(mdnList));
            knLogger.debug(methodName, "query", query);
            pStmt = conn.prepareStatement(query);
            for (String mdn : mdnList) {
                pStmt.setString(index++, mdn);
            }
            rs = pStmt.executeQuery();
            Collection<String> dbExternalMdnList = new ArrayList<String>();
            while (rs.next()) {
                KnCorpContactDTO contact = new KnCorpContactDTO();
                String mdn = rs.getString(1).trim();
                contact.setMdn(mdn);
                String name = rs.getString(2);
                //multilingual revert changes
                if (name != null) {
                    try {
                        name = new String(name.getBytes("8859_1"), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        knLogger.error(methodName, " Exception in encoding Contact Name - ", e);
                    }
                }
                contact.setName(name);
                contact.setServiceAuthStatus(rs.getInt(3));
                contact.setCorpId(rs.getInt(6));
                int corpType = rs.getInt(4);
                int publicType = rs.getInt(5);
                int finalSubscriptionType = getMappedSubscriptionType(publicType, corpType);
                if (corpId != contact.getCorpId()) {
                    finalSubscriptionType = EXTERNAL_CONTACT_TYPE;
                    contact.setExternalContact(true);
                }
                contact.setSubscriptionType(finalSubscriptionType);
                if (contact.isExternalContact()) {
                    contact.setMaxContactLimitFlag(LESS_THAN_LIMIT);
                    externalContacts.add(contact);
                    dbExternalMdnList.add(mdn);
                } else {
                    int contactCount = rs.getInt(7);
                    if (contactCount < maxContactLimit) {
                        contact.setMaxContactLimitFlag(LESS_THAN_LIMIT);
                    } else if (contactCount == maxContactLimit) {
                        contact.setMaxContactLimitFlag(EQUAL_TO_LIMIT);
                    } else if (contactCount > maxContactLimit) {
                        contact.setMaxContactLimitFlag(GREATER_THAN_LIMIT);
                    }
                    contact.setContactCount(contactCount);
                    contact.setClientType(rs.getInt(8));
                    inetrnalContacts.add(contact);
                }
            }
            for (String mdn : externalMdnList) {
                if (!dbExternalMdnList.contains(mdn)) {
                    KnCorpContactDTO contact = new KnCorpContactDTO();
                    contact.setMaxContactLimitFlag(LESS_THAN_LIMIT);
                    contact.setMdn(mdn);
                    contact.setSubscriptionType(EXTERNAL_CONTACT_TYPE);
                    contact.setServiceAuthStatus(INVALID_SERVICE_AUTH_STATUS);
                    externalContacts.add(contact);
                }

            }
            completeContactList.put(INTERNAL, inetrnalContacts);
            completeContactList.put(EXTERNAL, externalContacts);

            knLogger.debug(methodName, "Fetched map  size from DB :  ", completeContactList.size());
            return completeContactList;

        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving contact details - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception  occured while retrieving contact details- ", e);
            throw KnDbUtil.processException(e, "Failed while retrieving contact details " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
        }
    }

    public Map<String, Collection<KnCorpContactDTO>> getSublistSubscribersDistList(int sublistId, int maxContactLimit, int
            corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSublistSubscribersDistList(int, int, int,boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : sublistId -  ", sublistId, " , maxContactLimit - ", maxContactLimit, " , corpId",
                corpId);
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        String mdn;
        int corpType;
        int publicType;
        int finalSubscriptionType;
        int contactCount = 0;
        KnCorpContactDTO contact;
        Collection<KnCorpContactDTO> inetrnalContacts = new ArrayList<KnCorpContactDTO>();
        Collection<KnCorpContactDTO> externalContacts = new ArrayList<KnCorpContactDTO>();
        Map<String, Collection<KnCorpContactDTO>> completeContactList = new HashMap<String, Collection<KnCorpContactDTO>>();
        Map<String, Integer> mdnContactCount = new HashMap<String, Integer>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            query = queryMapper.getQuery(GET_CONTACT_COUNT_OF_CORP_MDN_LIST);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, sublistId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                mdnContactCount.put(rs.getString(1).trim(), rs.getInt(2));
            }
            query = queryMapper.getQuery(GET_SUBLIST_SUBSC_DIST_LIST);
            KnDbUtil.closeResultSet(rs);
            rs = null;
            KnDbUtil.closeStatement(pstmt);
            pstmt = null;
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, sublistId);
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");

            while (rs.next()) {
                contact = new KnCorpContactDTO();
                mdn = rs.getString(1).trim();
                contact.setMdn(mdn);
                //multilingual revert changes
                if (rs.getString(2) != null) {
                    try {
                        contact.setName(new String(rs.getString(2).getBytes("8859_1"), "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        knLogger.error(methodName, " Exception in encoding Subscriber Name for MDN - ", KnGDPRTemplate.mdn(mdn), e);
                    }
                }
                contact.setServiceAuthStatus(rs.getInt(3));
                contact.setCorpId(rs.getInt(6));
                contact.setDistributionType(0);
                corpType = rs.getInt(4);
                publicType = rs.getInt(5);
                finalSubscriptionType = getMappedSubscriptionType(publicType, corpType);
                if (corpId != contact.getCorpId()) {
                    finalSubscriptionType = EXTERNAL_CONTACT_TYPE;
                    contact.setExternalContact(true);
                    if (corpId > 0) {
                        contact.setContact_type(KnConstants.CONTACT_TYPE_EXTERNAL_CONTACT);
                    } else {
                        contact.setContact_type(KnConstants.CONTACT_TYPE_EXTERNAL_SUBSCRIBER);
                    }
                }
                contact.setSubscriptionType(finalSubscriptionType);
                if (contact.isExternalContact()) {
                    contact.setMaxContactLimitFlag(LESS_THAN_LIMIT);
                    externalContacts.add(contact);
                } else {
                    if (null != mdnContactCount.get(mdn)) {
                        contactCount = mdnContactCount.get(mdn);
                    }
                    if (contactCount < maxContactLimit) {
                        contact.setMaxContactLimitFlag(LESS_THAN_LIMIT);
                    } else if (contactCount == maxContactLimit) {
                        contact.setMaxContactLimitFlag(EQUAL_TO_LIMIT);
                    } else if (contactCount > maxContactLimit) {
                        contact.setMaxContactLimitFlag(GREATER_THAN_LIMIT);
                    }
                    contact.setContactCount(contactCount);
                    contact.setClientType(rs.getInt(7));
                    contact.setAliasMdn(rs.getString(8));
                    contact.setUserId(rs.getString(9));
                    String activeFS = rs.getString(11) != null ? rs.getString(11) : KnGeneralUtil.convertLongToHexString(rs.getLong(10));
                    contact.setSubsActiveFS2(activeFS);
                    contact.setUserProfileIndex(rs.getInt(12));
                    inetrnalContacts.add(contact);
                }
            }
            completeContactList.put(INTERNAL, inetrnalContacts);
            completeContactList.put(EXTERNAL, externalContacts);
            knLogger.debug(methodName, "Exit ");

        } catch (SQLException e) {
            knLogger.error(methodName, "Unexpected Exception occured while sublist distribution list- ", e);
            throw KnDbUtil.processException(e, "Failed to getSublistSubscribersDistList " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        return completeContactList;
    }

    public int selectSubscribersCount(String subscriberMdn, int corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "selectSubscribersCount(String, int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : subscriberMdn -  ", KnGDPRTemplate.mdn(subscriberMdn), " , corpId - ", corpId, " readOnly :", readOnly);

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        int count = 0;
        boolean ownedTxn = false;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            //conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            if (persisterTxn != null) {
                knLogger.debug(methodName, " Data Store before - ", KnDBConst.DataStores.XDM_SHARED_DATA);
                conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
                knLogger.debug(methodName, "if block");
            } else {
                conn = KnDBManager.getDBManagerInstance().getConnection(KnGenInfoUtil.getSecDBPttServerId(pttServerId), true);
                ownedTxn = true;
                knLogger.debug(methodName, "else block");
                knLogger.debug(methodName, "conn", conn);
            }
            query = queryMapper.getQuery(GET_SUBSCRIBER_COUNT);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, subscriberMdn);
            pstmt.setInt(2, corpId);
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            if (rs.next()) {
                count = rs.getInt(1);
            }
            return count;
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while getting the subscriber count " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            if (ownedTxn) {
                KnDbUtil.closeConnection(conn);
            }
            knLogger.debug(methodName, "subscriberMdn -  ", KnGDPRTemplate.mdn(subscriberMdn), " , corpId - ", corpId, "No of subcribers found :  ", count);

        }
    }

    /**
     * This method queries the DG.XDM_CORPRESOURCELISTINDEXDOC table for subscriber etag. This is read only method.
     *
     * @param mdn
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public int getSubscriberResourceListEtag(String mdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubscriberResourceListEtag(String, boolean, KnPersisterTxn)";
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        int count = 0;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            conn = persisterTxn.getDBConnection(pttServerId, readOnly);
            query = queryMapper.getQuery(SELECT_SUBSCRIBER_ETAG);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, mdn);
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            if (rs.next()) {
                count = rs.getInt(1);
            }
            knLogger.debug(methodName, "Fetched the details from DB . No of subcribers found :  ", count);
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while getting the subscribers contact count ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        return count;
    }

    public Map<String, Integer> getFinalMemberContactCount(Collection<Integer> finalSublistListInDB,
                                                           Collection<String> finalMdnInPrivateList,
                                                           String subscriberMdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getFinalMemberContactCount(Collection<Integer>, Collection<String>, persisterTxn)";
        knLogger.info(methodName, "Entry : finalSublistListInDB -  ", finalSublistListInDB, " , finalMdnInPrivateList", finalMdnInPrivateList == null ? finalMdnInPrivateList : KnGDPRTemplate.mdnList(finalMdnInPrivateList));
        ResultSet rs = null;
        String query = null;
        int index = 1;
        PreparedStatement pstmt = null;
        //int count = 0;
        Map<String,Integer> countInfo = new HashMap<>();
        try {
            Connection conn;
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            subscriberMdn = subscriberMdn.trim();
            query = queryMapper.getQuery(GET_UNIQUE_MDN_FRM_COM_NORM_SUBLIST);
            query = replaceContactWithValue(query, SUBLISTID, formCommaSeperatedIntegerQuesMarks(finalSublistListInDB));
            knLogger.debug(methodName, "query", query);
            pstmt = conn.prepareStatement(query);
            for (Integer mdn : finalSublistListInDB) {
                pstmt.setInt(index++, mdn);
            }
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            Set<String> finalNormalMemberList = new HashSet<>();
            if (!isObjectNull(finalMdnInPrivateList) && !finalMdnInPrivateList.isEmpty()) {
                finalNormalMemberList.addAll(finalMdnInPrivateList);
            }
            Set<String> commonContactMemberList = new HashSet<>();
            while (rs.next()) {
                String mdn = rs.getString(1).trim();
                int listDistPolicy = rs.getInt(2);
                if (mdn.equals(subscriberMdn)) {
                    continue;
                }
                if (listDistPolicy == DIST_POLICY_COMMON_CONTACT_LIST_SUPPORT){ //common
                    commonContactMemberList.add(mdn);
                } else {
                    finalNormalMemberList.add(mdn);
                }
            }
            countInfo.put(NORMAL_CONTACT_COUNT,finalNormalMemberList.size());
            countInfo.put(COMMON_CONTACT_COUNT,commonContactMemberList.size());
            return countInfo;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getting the final subscribers contact count - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while getting the final subscribers contact count- ", e);
            throw KnDbUtil.processException(e, "Failed while getting the subscribers contact count " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            knLogger.info(methodName, " EXIT :Fetched the details from DB . No of contacts found :  ", countInfo);
        }
    }

    public Collection<String> getDistinctMembers(Collection<String> mdnList,
                                                 Collection<Integer> groupIdLst,
                                                 KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getDistinctMembers(Collection<String>, Collection<Integer> , KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : mdnList -  ", mdnList == null ? mdnList : KnGDPRTemplate.mdnList(mdnList), " , groupIdLst", groupIdLst);

        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        int index = 1;
        boolean ownedTxn = false;
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = queryMapper.getQuery(SELECT_GROUP_DISTINCT_MEMBERS);
            Collection<String> memberList = new ArrayList<String>();
            if (!isObjectNull(groupIdLst) && !groupIdLst.isEmpty()) {
                query = replaceContactWithValue(query, GROUPIDS, formCommaSeperatedIntegerQuesMarks(groupIdLst));
                knLogger.debug(methodName, "query", query);
                pstmt = conn.prepareStatement(query);
                for (Integer mdn : groupIdLst) {
                    pstmt.setInt(index++, mdn);
                }
                rs = pstmt.executeQuery();
                while (rs.next()) {
                    String mdn = rs.getString(1).trim();
                    if (!memberList.contains(mdn)) {
                        memberList.add(mdn);
                    }
                }
            }
            knLogger.debug(methodName, "Query executed successfully");

            if (!isObjectNull(mdnList) && !mdnList.isEmpty()) {
                for (String mdn : mdnList) {
                    if (!memberList.contains(mdn)) {
                        memberList.add(mdn);
                    }
                }
            }
            if (ownedTxn) {
                knLogger.debug(methodName, "saving the transaction ");
                persisterTxn.save();
            }
            knLogger.debug(methodName, "memberList size :  ", memberList.size());
            return memberList;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getting the distinct subscribers  - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while getting the distinct subscribers - ", e);
            if(ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed while getting the distinct subscribers  " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            knLogger.debug(methodName, "EXIT: Query executed successfully");
        }
    }

    public Collection<String> getContactMDNs(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getContactMDNs(String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : Input Dto passed  mdn - ", KnGDPRTemplate.mdn(mdn));

        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Collection<String> subsList = new ArrayList<String>();
        String query = null;
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(SELECT_CONTACT_MDNS);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, mdn);
            knLogger.debug(methodName, "Executing query- ", "'", query, "'");
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                String contactMdn = rs.getString(1).trim();
                if (!subsList.contains(contactMdn)) {
                    subsList.add(contactMdn);
                }
            }
            knLogger.debug(methodName, "The list size retrieved from DB:  ", subsList.size());
            return subsList;

        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving contact mdns from the DB",
                    " present in passed mdnList - " + e);
            throw e;

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while retrieving contact mdns from the DB",
                    " present in passed mdn- " + e);
            throw KnDbUtil.processException(e, "Failed  while retrieving the contact mdns from the DB" +
                    " present in passed mdn- " + e, pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);

        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            knLogger.debug(methodName, "EXIT :The list size retrieved from DB:", subsList.size());
        }
    }

    public int getCorpSubscriberCount(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getCorpSubscriberCount(int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : Input Dto passed  corpId - ", corpId);

        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int count = 0;
        String query = null;
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(SELECT_CORP_SUBSC_COUNT);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, corpId);
            knLogger.debug(methodName, "Executing query- ", "'", query, "'");
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            if (rs.next()) {
                count = rs.getInt(1);
            }
            return count;

        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving contact count for the corporate - ", e);
            throw e;

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while retrieving contact count for the corporate - ", e);
            throw KnDbUtil.processException(e, " while retrieving contact count for the corporate"
                    + e, pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);

        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            knLogger.debug(methodName, "Input Dto passed  corpId - ", corpId, "EXIT :Fetched the details from DB :  ", count);
        }
    }

    public Map<String, KnCorpSubscriberDTO> getPoCSubscriberExistMap(Collection<String> mdnList, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "getPoCSubscriberExistMap(Collection<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: MdnList - ", mdnList == null ? mdnList : KnGDPRTemplate.mdnList(mdnList));

        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        Map<String, KnCorpSubscriberDTO> subscribersMap = new HashMap<String, KnCorpSubscriberDTO>();
        try {

            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            var mdnListArray = new ArrayList<>(mdnList);
            var subsLists = KnGeneralUtil.splitList(mdnListArray, BULK_UPDATE_SIZE);
            for (var subsList : subsLists) {
                query = queryMapper.getQuery(GET_POC_SUBSCRIBER);
                query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedQuesMarks(subsList));
                knLogger.debug(methodName, "query", query);
                int index = 1;
                pstmt = conn.prepareStatement(query);
                for (String mdn : subsList) {
                    pstmt.setString(index++, mdn);
                }
                rs = pstmt.executeQuery();
                knLogger.debug(methodName, "Query executed successfully");
                while (rs.next()) {
                    KnCorpSubscriberDTO subscriberDTO = new KnCorpSubscriberDTO();
                    String mdn = rs.getString(2).trim();
                    subscriberDTO.setMdn(mdn);
                    int mdnCorpId = rs.getInt(1);
                    subscriberDTO.setCorpId(mdnCorpId);
                    //Multilingual revert change
                    if (rs.getString(3) != null) {
                        try {
                            subscriberDTO.setName(new String(rs.getString(3).getBytes("8859_1"), "UTF-8"));
                        } catch (UnsupportedEncodingException e) {
                            knLogger.error(methodName, " Exception in encoding Subscriber Name for MDN- ", rs.getString(3), e);
                        }
                    }
                    int publicType = rs.getInt(4);
                    int corpType = rs.getInt(5);
                    subscriberDTO.setSubscriptionType(getMappedSubscriptionType(publicType, corpType));
                    subscriberDTO.setClientType(rs.getInt(6));
                    String activeFS = rs.getString(10) != null ? rs.getString(10) : KnGeneralUtil.convertLongToHexString(rs.getLong(9));
                    subscriberDTO.setSubsActiveFS2(activeFS);
                    subscriberDTO.setClientPVmajorVer(rs.getInt(11));
                    subscriberDTO.setUserAgent(rs.getString(12));
                    String subscriberFS = rs.getString(15) != null ? rs.getString(15) : KnGeneralUtil.convertLongToHexString(rs.getLong(14));
                    subscriberDTO.setSubscriberFs2(subscriberFS);
                    subscriberDTO.setAliasMdn(rs.getString(16));
                    subscriberDTO.setUserId(rs.getString(17));
                    subscribersMap.put(mdn, subscriberDTO);
                }
            }
            knLogger.debug(methodName, "Fetched map size :  ", subscribersMap.size());
            return subscribersMap;

        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getting corp subscriber details from the mdn list passed (getPoCSubscriberExistMap)- "
                    + e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while getting corp subscriber details ",
                    "from the mdn list passed -", e);
            throw KnDbUtil.processException(e, "Failed while getting corp subscriber details from the mdn list passed (getPoCSubscriberExistMap)- " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            knLogger.debug(methodName, "EXIT :");
        }
    }

    /**
     * This method returns a Map of MDN and private contact list id of each MDN.
     *
     * @param completeMdnList
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<String, Integer> getSubscribersPrivateListId(Collection<String> completeMdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubscribersPrivateListId(Collection<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : completeMdnList - ", completeMdnList == null ? completeMdnList : KnGDPRTemplate.mdnList(completeMdnList));

        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        int index = 1;
        Map<String, Integer> subscribersPvtListMap = new HashMap<String, Integer>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_POC_SUBSCRIBER_PRIVATE_LIST);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedQuesMarks(completeMdnList));
            knLogger.debug(methodName, "query", query);
            pstmt = conn.prepareStatement(query);
            for (String mdn : completeMdnList) {
                pstmt.setString(index++, mdn);
            }
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                String mdn = rs.getString(1).trim();
                int listId = rs.getInt(2);
                subscribersPvtListMap.put(mdn, listId);
            }
            knLogger.debug(methodName, "Fetched list size from DB :  ", subscribersPvtListMap.size());
            return subscribersPvtListMap;
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while getting getting corp subscriber private List ids- ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public void updateSubsAuthStatusToProvisioningStat(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateSubsAuthStatusToProvisioningStat(String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : mdn - ", KnGDPRTemplate.mdn(mdn));
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        int serviceAuthState = PROVISIONED.value();
        knLogger.debug(methodName, "serviceAuthState - ", serviceAuthState);
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(UPDATE_SUBSRIBERS_AUTH_STATE);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, serviceAuthState);
            pstmt.setString(2, mdn);
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            pstmt.executeQuery();
            knLogger.debug(methodName, "EXIT: Query executed successfully");
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while updating subs auth state- ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while updating subs auth state - ",
                    e);
            throw KnDbUtil.processException(e, "Failed while updating subs auth state - " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }


    public void invalidatePocUserPassword(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "invalidatePocUserPassword(String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : mdn - ", KnGDPRTemplate.mdn(mdn));
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;

        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(INVALIDATE_SUBSCRIBER_PASSWORD);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, mdn);
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            pstmt.executeQuery();
            knLogger.debug(methodName, "EXIT: Query executed successfully");
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while invalidatePocUserPassword - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while invalidatePocUserPassword - ",
                    e);
            throw KnDbUtil.processException(e, "Failed while invalidatePocUserPassword - " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    /**
     * This method returns a Map of subscribers and subscribers name.
     *
     * @param mdnList
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<String, String> getSubscribersName(Set<String> mdnList, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubscribersName(Set<String>, boolean, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY : mdnList size - ", mdnList == null ? mdnList : mdnList.size(), " readOnly :", readOnly);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        Map<String, String> subscNameMap = new HashMap<String, String>();
        int index = 1;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(SELECT_SUBSC_NAME);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedQuesMarks(mdnList));
            knLogger.debug(methodName, "query", query);
            pstmt = conn.prepareStatement(query);
            for (String mdn : mdnList) {
                pstmt.setString(index++, mdn);
            }
            rs = pstmt.executeQuery();

            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                //multilingual revert change
                String subsName = rs.getString(2);
                if (subsName != null) {
                    try {
                        subsName = new String(subsName.getBytes("8859_1"), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        knLogger.error(methodName, " Exception in encoding Subscriber Name for MDN- ", rs.getString(2), e);
                    }
                }
                subscNameMap.put(rs.getString(1).trim(), subsName);
            }
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while getSubscribersName - ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        knLogger.info(methodName, "EXIT", subscNameMap.size());
        return subscNameMap;
    }

    public Collection<String> getDeActNonHandsetSubsc(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getDeActNonHandsetSubsc(int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : corpId - ", corpId);

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        Collection<String> deActNonHansetSubsc = new ArrayList<String>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_DET_ACT_NON_HANDSET_SUBSC);
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, corpId);
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                deActNonHansetSubsc.add(rs.getString(1).trim());
            }
            return deActNonHansetSubsc;

        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while fetching the DeActNonHandsetSubsc - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while fetching the DeActNonHandsetSubsc - ",
                    e);
            throw KnDbUtil.processException(e, "Failed while fetching the DeActNonHandsetSubsc ." + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            knLogger.debug(methodName, "EXIT : PoC subscribers list returned with no of members - ", deActNonHansetSubsc.size());
        }
    }

    public Collection<KnCorpSubscriberDTO> selectPocSubscriberInfo(Collection<String> addedMdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "selectPocSubscriberInfo(Collection<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : Input DTO passed persistenceDTO - ", addedMdnList == null ? addedMdnList : KnGDPRTemplate.mdnList(addedMdnList));

        Connection conn;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String query = null;
        Collection<KnCorpSubscriberDTO> subscribersList = new ArrayList<KnCorpSubscriberDTO>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            var mdnListArray = new ArrayList<>(addedMdnList);
            var subsLists = KnGeneralUtil.splitList(mdnListArray, BULK_UPDATE_SIZE);
            for (var subsList : subsLists) {
                query = queryMapper.getQuery(GET_POC_SUBSCRIBER_INFO);
                query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedQuesMarks(subsList));
                int index = 1;
                knLogger.debug(methodName, "query", query);
                stmt = conn.prepareStatement(query);
                for (String mdn : subsList) {
                    stmt.setString(index++, mdn);
                }
                rs = stmt.executeQuery();
                knLogger.debug(methodName, "Query executed successfully");
                while (rs.next()) {
                    KnCorpSubscriberDTO subscriberDTO = new KnCorpSubscriberDTO();
                    String mdn = rs.getString(2).trim();
                    subscriberDTO.setMdn(mdn);
                    int mdnCorpId = rs.getInt(1);
                    subscriberDTO.setCorpId(mdnCorpId);
                    subscribersList.add(subscriberDTO);
                }
            }
            return subscribersList;

        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while fetching the PoC subcribers List - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while fetching the PoC subcribers List - ",
                    e);
            throw KnDbUtil.processException(e, "Failed while fetching the PoC subcribers List ." + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            knLogger.debug(methodName, "EXIT : PoC subscribers list returned with no of members - ", subscribersList.size());
        }
    }

    public Map<String, KnCorpSubscriberDTO> getSubscIsMemOfDispGrpDetails(Collection<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubscIsMemOfDispGrpDetails(Collection<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : mdnList - ", mdnList == null ? mdnList : KnGDPRTemplate.mdnList(mdnList));
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        int index = 1;
        Map<String, KnCorpSubscriberDTO> subscIsMemOfDispGrpDetails = new HashMap<String, KnCorpSubscriberDTO>();

        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(SELECT_SUBSC_IS_DISPATCH_GRP_MEMBER_DETAILS);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedQuesMarks(mdnList));
            knLogger.debug(methodName, "query", query);
            pstmt = conn.prepareStatement(query);
            for (String mdn : mdnList) {
                pstmt.setString(index++, mdn);
            }
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                KnCorpSubscriberDTO subsc = new KnCorpSubscriberDTO();
                subsc.setMdn(rs.getString(1).trim());
                subsc.setDispatchGrpmember(rs.getInt(2));
                subsc.setPocHome(rs.getString(3).trim());
                subscIsMemOfDispGrpDetails.put(subsc.getMdn(), subsc);
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getSubscIsMemOfDispGrpDetails - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while getSubscIsMemOfDispGrpDetails - ",
                    e);
            throw KnDbUtil.processException(e, "Failed while getSubscIsMemOfDispGrpDetails - " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            knLogger.debug(methodName, "EXIT");
        }
        return subscIsMemOfDispGrpDetails;
    }

    public Collection<String> getPocSubscribersDetails(Collection<String> contactMDNList, int corpId, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "getPocSubscribersDetails(Collection<String>, int,  KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : Input DTO passed  contact mdn list  - ", contactMDNList == null ? contactMDNList : KnGDPRTemplate.mdnList(contactMDNList));
        boolean ownedTxn = false;
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        int index = 1;
        Collection<String> subscribersList = new ArrayList<String>();
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the Transaction");
                persisterTxn.open();
                ownedTxn = true;
            }
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_SUBSCRIBER_DETAILS);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedQuesMarks(contactMDNList));
            knLogger.debug(methodName, "query", query);
            pstmt = conn.prepareStatement(query);
            for (String mdn : contactMDNList) {
                pstmt.setString(index++, mdn);
            }
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                String mdn = rs.getString(2).trim();
                int mdnCorpId = rs.getInt(1);
                if (corpId == mdnCorpId) {
                    subscribersList.add(mdn);
                }
            }
            if (ownedTxn) {
                persisterTxn.save();
            }
            return subscribersList;

        } catch (KnDAOException e) {
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            knLogger.error(methodName, "KnDAOException occured while fetching the PoC subcribers List - ", e);
            throw e;
        } catch (Exception e) {
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            knLogger.error(methodName, "Unexpected Exception occured while fetching the PoC subcribers List - ",
                    e);
            throw KnDbUtil.processException(e, "Failed while fetching the PoC subcribers List ." + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            knLogger.debug(methodName, "EXIT : PoC subscribers list returned with no of members - ", subscribersList.size());
        }
    }

    public Map<String,Integer> getMdnCorpIdMapping(Collection<String> mdnList, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "getMdnCorpIdMapping(Collection<String>, int,  KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : Input DTO passed   mdn list  - ", mdnList == null ? mdnList : KnGDPRTemplate.mdnList(mdnList));
        boolean ownedTxn = false;
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        int index = 1;
        Map<String,Integer> result = new HashMap<>();
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the Transaction");
                persisterTxn.open();
                ownedTxn = true;
            }
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_SUBSCRIBER_DETAILS);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedQuesMarks(mdnList));
            knLogger.debug(methodName, "query", query);
            pstmt = conn.prepareStatement(query);
            for (String mdn : mdnList) {
                pstmt.setString(index++, mdn);
            }
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                String mdn = rs.getString(2).trim();
                int mdnCorpId = rs.getInt(1);
                result.put(mdn, mdnCorpId);
            }
            if (ownedTxn) {
                persisterTxn.save();
            }
            return result;

        } catch (KnDAOException e) {
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            knLogger.error(methodName, "KnDAOException occured while fetching the PoC subcribers List - ", e);
            throw e;
        } catch (Exception e) {
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            knLogger.error(methodName, "Unexpected Exception occured while fetching the PoC subcribers List - ",
                    e);
            throw KnDbUtil.processException(e, "Failed while fetching the PoC subcribers List ." + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            knLogger.debug(methodName, "EXIT : PoC subscribers list returned with no of members - ", result.size());
        }
    }

    public Collection<KnCorpSubscriberDTO> getSubscriberProfileInfo(Collection<String> mdnList, int corpId, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "getSubscriberProfileInfo(Collection<String>, int, boolean,  KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : Input DTO passed  contact mdn list  - ", mdnList == null ? mdnList : KnGDPRTemplate.mdnList(mdnList));

        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        Collection<KnCorpSubscriberDTO> subscribersList = new ArrayList<KnCorpSubscriberDTO>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_POC_SUSBCR_DETAILS);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            pstmt = conn.prepareStatement(query);
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");

            for (String mdn : mdnList) {
                KnCorpSubscriberDTO subscriberDTO = new KnCorpSubscriberDTO();

                pstmt.setString(1, mdn);
                rs = pstmt.executeQuery();
                knLogger.debug(methodName, "Query executed successfully");
                while (rs.next()) {
                    int memCorpId = rs.getInt(1);

                    if (memCorpId == corpId) {
                        int serviceAuthStatus = rs.getInt(2);
                        int clientType = rs.getInt(3);
                        subscriberDTO.setServiceAuthStatus(serviceAuthStatus);
                        subscriberDTO.setClientType(clientType);
                        subscriberDTO.setMdn(mdn);
                        subscriberDTO.setDispatchType(rs.getInt(8));
                        subscriberDTO.setLicenseType(rs.getInt(9));
                        subscriberDTO.setClientPVmajorVer(rs.getInt(10));
                        String activeFs2 = rs.getString(11) != null ? rs.getString(11) : KnGeneralUtil.convertLongToHexString(rs.getLong(7));
                        subscriberDTO.setSubsActiveFS2(activeFs2);
                        subscribersList.add(subscriberDTO);
                    }
                }
            }
            return subscribersList;

        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while fetching the PoC subcribers List - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while fetching the PoC subcribers List - ",
                    e);
            throw KnDbUtil.processException(e, "Failed while fetching the PoC subcribers List ." + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            knLogger.debug(methodName, "EXIT : PoC subscribers list returned with no of members - ", subscribersList.size());
        }
    }

    public Collection<KnCorpSubscriberDTO> getSubscriberProfileDetails(Collection<String> mdnList, int corpId, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "getSubscriberProfileDetails(Collection<String>, int,boolean,  KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : Input DTO passed  contact mdn list  - ", mdnList == null ? mdnList : KnGDPRTemplate.mdnList(mdnList));

        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        Collection<KnCorpSubscriberDTO> subscribersList = new ArrayList<KnCorpSubscriberDTO>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_POC_SUSBCR_DETAILS);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            pstmt = conn.prepareStatement(query);
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");

            for (String mdn : mdnList) {
                KnCorpSubscriberDTO subscriberDTO = new KnCorpSubscriberDTO();

                pstmt.setString(1, mdn);
                rs = pstmt.executeQuery();
                knLogger.debug(methodName, "Query executed successfully");
                while (rs.next()) {
                    subscriberDTO.setCorpId(rs.getInt(1));
                    int serviceAuthStatus = rs.getInt(2);
                    int clientType = rs.getInt(3);
                    String email = rs.getString(4);
                    //multilingual revert change
                    String subscrName = rs.getString(5);
                    if (subscrName != null) {
                        try {
                            subscrName = new String(subscrName.getBytes("8859_1"), "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            knLogger.error(methodName, " Exception in encoding Subscriber Name  ", KnGDPRTemplate.name(subscrName), e);
                        }
                    }
                    subscriberDTO.setServiceAuthStatus(serviceAuthStatus);
                    subscriberDTO.setClientType(clientType);
                    subscriberDTO.setMdn(mdn);
                    subscriberDTO.setSubscEmail(email);
                    subscriberDTO.setName(subscrName);
                    subscriberDTO.setPamAccId(rs.getInt(6));
                    if (rs.getString(USER_ID) != null) {
                        subscriberDTO.setUserId(rs.getString(USER_ID));
                    }
                    if (null != rs.getString(MC_ID)) {
                        subscriberDTO.setMcId(new String(rs.getBytes(MC_ID), StandardCharsets.UTF_8));
                    }
                    subscriberDTO.setMcpttCompliance(rs.getInt(MCPTT_COMPLIANCE));
                    subscribersList.add(subscriberDTO);


                }
            }
            knLogger.debug(methodName, "EXIT : PoC subscribers list returned with no of members - subscribersList", subscribersList.toString());
            return subscribersList;

        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while fetching the PoC subcribers List - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while fetching the PoC subcribers List - ",
                    e);
            throw KnDbUtil.processException(e, "Failed while fetching the PoC subcribers List ." + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
    }

    /**
     * This method returns a Map of subscribers and subscriber name from dg.pocsubscrinfo table.
     *
     * @param mdnList
     * @param corpid
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<String, KnSubscriberDTO> getSubscribersNameForCorp(List<String> mdnList, int corpid,boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubscribersNameForCorp(Set<String>, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY : mdnList - ", mdnList == null ? mdnList : mdnList.size(),readOnly);
        Connection conn;
        PreparedStatement pstmt = null;
        PreparedStatement pStmt = null;
        String query = null;
        ResultSet rs = null;
        Map<String, KnSubscriberDTO> subscNameMap;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            var mdnListArray = new ArrayList<>(mdnList);
            var subsLists = KnGeneralUtil.splitList(mdnListArray, BULK_UPDATE_SIZE);
            subscNameMap = new HashMap<>();
            List<String> extMdnList = new ArrayList<>(mdnList);
            for (var subsList : subsLists) {
                query = queryMapper.getQuery(SELECT_SUBSC_NAME_FOR_CORP);
                query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedQuesMarks(subsList));
                int index = 1;
                knLogger.debug(methodName, "query", query);
                pstmt = conn.prepareStatement(query);
                for (String mdn : subsList) {
                    pstmt.setString(index++, mdn);
                }
                rs = pstmt.executeQuery();
                knLogger.debug(methodName, "Query executed successfully");
                KnSubscriberDTO subscriberDTO;
                while (rs.next()) {
                    subscriberDTO = new KnSubscriberDTO();
                    String mdn = rs.getString(1).trim();
                    subscriberDTO.setNetworkName(rs.getString(2));
                    int clientType = rs.getInt(3);
                    if (corpid != rs.getInt(4)) {
                        subscriberDTO.setContact_type(2);
                    } else {
                        extMdnList.remove(mdn);
                    }
                    subscriberDTO.setClientType(clientType);
                    subscriberDTO.setUserAgent(rs.getString(5));
                    subscriberDTO.setClientPVMajorVersion(rs.getInt(6));
                    String activeFS = rs.getString(10) != null ? rs.getString(10) : KnGeneralUtil.convertLongToHexString(rs.getLong(7));
                    subscriberDTO.setActiveFS2(activeFS);
                    subscriberDTO.setLicenseType(rs.getInt(11));
                    //multilingual revert changes
                    String name = rs.getString(2);
                    if (name != null) {
                        try {
                            name = new String(name.getBytes("8859_1"), "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            knLogger.error(methodName, " Exception in encoding Subscriber Name for MDN- ", rs.getString(2), e);
                        }
                    }
                    subscriberDTO.setNetworkName(name);
                    subscNameMap.put(mdn, subscriberDTO);
                }
                KnDbUtil.closeResultSet(rs);
                KnDbUtil.closeStatement(pstmt);
                rs = null; pstmt = null;
            }
            //Setting the externalContacts data
//            var mdnListArray = new ArrayList<>(mdnList);
//            var subsLists = KnGeneralUtil.splitList(mdnListArray, BULK_UPDATE_SIZE);
            for (var subsList : subsLists) {
                query = queryMapper.getQuery(GET_EXTERNAL_CONTACT);
                int index1 = 1;
                query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedQuesMarks(subsList));
                knLogger.debug(methodName, "query", query);
                pStmt = conn.prepareStatement(query);
                for (String mdn : subsList) {
                    pStmt.setString(index1++, mdn);
                }
                pStmt.setInt(index1, corpid);
                rs = pStmt.executeQuery();
                knLogger.debug(methodName, "Query executed successfully");
                while (rs.next()) {
                    String mdn = rs.getString(1).trim();
                    if (subscNameMap.get(mdn) != null) {
                        KnSubscriberDTO subscDTO = subscNameMap.get(mdn);
                        //multilingual revert changes
                        String name = rs.getString(3);
                        if (name != null) {
                            try {
                                name = new String(name.getBytes("8859_1"), "UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                knLogger.error(methodName, " Exception in encoding Subscriber Name for MDN- ", rs.getString(3), e);
                            }
                        }
                        subscDTO.setNetworkName(name);
                    }
                }
                KnDbUtil.closeResultSet(rs);
                KnDbUtil.closeStatement(pStmt);
                rs = null; pStmt = null;
            }
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while getSubscribersName - ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            KnDbUtil.closeStatement(pStmt);
        }
        knLogger.debug(methodName, "EXIT subscNameMap size", KnGDPRTemplate.mapKeyMdn(subscNameMap));
        return subscNameMap;
    }

    /**
     * This method returns a Map of subscriber MDN and subscriber DTO from dg.pocsubscrinfo table.
     *
     * @param mdnList
     * @param corpid
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<String, KnCorpSubscriberDTO> getSubscriberDto(List<String> mdnList, int corpid, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubscriberDto(List<String>, int, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : mdnList - ", mdnList == null ? mdnList : KnGDPRTemplate.mdnList(mdnList));
        var mdnListArray = new ArrayList<>(mdnList);
        Map<String, KnCorpSubscriberDTO> subscMap = new HashMap<>();
        var subsLists = KnGeneralUtil.splitList(mdnListArray, BULK_UPDATE_SIZE);
        for (var subsList : subsLists) {
            var subSubsribersCorporateDetails = getSubscribersDto(subsList, corpid, readOnly, persisterTxn);
            if (subSubsribersCorporateDetails != null && !subSubsribersCorporateDetails.isEmpty()) {
                subscMap.putAll(subSubsribersCorporateDetails);
            }
        }
        return subscMap;
    }
        /**
         * This method returns a Map of subscriber MDN and subscriber DTO from dg.pocsubscrinfo table.
         *
         * @param mdnList
         * @param corpid
         * @param persisterTxn
         * @return
         * @throws KnDAOException
         */
        public Map<String, KnCorpSubscriberDTO> getSubscribersDto(List<String> mdnList, int corpid, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
            String methodName = "getSubscriberDto(List<String>, int, boolean, KnPersisterTxn)";
            knLogger.debug(methodName, "ENTRY : mdnList - ", mdnList == null ? mdnList : KnGDPRTemplate.mdnList(mdnList));
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        int index = 1;
        Map<String, KnCorpSubscriberDTO> subscMap;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(SELECT_CONTACTS_FOR_CORP);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedQuesMarks(mdnList));
            knLogger.debug(methodName, "query", query);
            pstmt = conn.prepareStatement(query);
            for (String mdn : mdnList) {
                pstmt.setString(index++, mdn);
            }
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            subscMap = new HashMap<String, KnCorpSubscriberDTO>();
            while (rs.next()) {
                KnCorpSubscriberDTO subscriberDTO = new KnCorpSubscriberDTO();
                subscriberDTO.setMdn(rs.getString(1).trim());
                //multilingual revert changes
                if (null != rs.getString(2)) {
                    try {
                        subscriberDTO.setName(new String(rs.getString(2).getBytes("8859_1"), "UTF-8").trim());
                    } catch (UnsupportedEncodingException e) {
                        knLogger.error(methodName, " Exception in encoding Subscriber Name for MDN- ", rs.getString(2), e);
                    }
                }
                subscriberDTO.setClientType(rs.getInt(3));
                subscriberDTO.setCorpId(rs.getInt(4));
                subscriberDTO.setUserAgent(rs.getString(5));
                subscriberDTO.setClientPVmajorVer(rs.getInt(6));
                subscriberDTO.setAliasMdn(rs.getString(8));
                subscriberDTO.setUserId(rs.getString(9));
                String activeFs2 = rs.getString(10) != null ? rs.getString(10) : KnGeneralUtil.convertLongToHexString(rs.getLong(7));
                subscriberDTO.setSubsActiveFS2(activeFs2);
                subscriberDTO.setCameraType(rs.getInt(12));
                subscriberDTO.setServiceAuthStatus(rs.getInt(13));
                subscMap.put(subscriberDTO.getMdn(), subscriberDTO);
            }
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while getSubscribersName - ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        knLogger.debug(methodName, "EXIT subscNameMap size", subscMap.size());
        return subscMap;
    }


    public Collection<KnCorpGroupMemPersistDTO> getGrpMemDetails(List<String> internalMemLst, Map<String, Integer>
            contCountmap, int corpId, int maxContact, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        Collection<KnCorpGroupMemPersistDTO> grpMemList = new ArrayList<KnCorpGroupMemPersistDTO>(contCountmap.size());
        String methodName = "getGrpMemDetails(List, List, int, int, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        var mdnListArray = new ArrayList<>(internalMemLst);
        var subsLists = KnGeneralUtil.splitList(mdnListArray, BULK_UPDATE_SIZE);
        for (var subsList : subsLists) {
            var subSubsribersCorporateDetails = getGrpMemsDetails(subsList, contCountmap, corpId, maxContact, readOnly, persisterTxn);
            if (subSubsribersCorporateDetails != null && !subSubsribersCorporateDetails.isEmpty()) {
                grpMemList.addAll(subSubsribersCorporateDetails);
            }
        }
        return grpMemList;
    }

    public List<String> getGrpMemDetails(List<String> internalMemLst, KnPersisterTxn persisterTxn) throws KnDAOException {
        List<String> grpMemList = new ArrayList<>();
        var mdnListArray = new ArrayList<>(internalMemLst);
        var subsLists = KnGeneralUtil.splitList(mdnListArray, BULK_UPDATE_SIZE);
        for (var subsList : subsLists) {
            var subSubsribersCorporateDetails = getGrpMemsDetails(subsList, persisterTxn);
            if (!subSubsribersCorporateDetails.isEmpty()) {
                grpMemList.addAll(subSubsribersCorporateDetails);
            }
        }
        return grpMemList;
    }

    public List<String> getMdnsLessThanThirteenPv(List<String> mdns, KnPersisterTxn persisterTxn) throws KnDAOException {
        List<String> grpMdnList = new ArrayList<>();
        var mdnListArray = new ArrayList<>(mdns);
        var mdnLists = KnGeneralUtil.splitList(mdnListArray, BULK_UPDATE_SIZE);
        for (var mdnList : mdnLists) {
            var listOfMdns = selectMdnsLessThanThirteenPv(mdnList, persisterTxn);
            if (!listOfMdns.isEmpty()) {
                grpMdnList.addAll(listOfMdns);
            }
        }
        return grpMdnList;
    }

    /**
     * This method returns the group internal members details.
     *
     * @param internalMemLst
     * @param contCountmap
     * @param corpId
     * @param maxContact
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    private Collection<KnCorpGroupMemPersistDTO> getGrpMemsDetails(List<String> internalMemLst, Map<String, Integer>
            contCountmap, int corpId, int maxContact, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getGrpMemsDetails(List, List, int, int, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : mdnList ", internalMemLst, contCountmap.size());
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        String mdn;
        int index=1;
        Collection<KnCorpGroupMemPersistDTO> grpMemList = new ArrayList<KnCorpGroupMemPersistDTO>(contCountmap.size());
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_GROUP_MEMBERS_LIST_ALL_CORP);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedQuesMarks(internalMemLst));
            knLogger.debug(methodName, "query", query);
            pstmt = conn.prepareStatement(query);
            for(String imdn:internalMemLst){
                pstmt.setString(index++, imdn);
            }
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully.");
            while (rs.next()) {
                KnCorpGroupMemPersistDTO memberPersistDTO = new KnCorpGroupMemPersistDTO();
                mdn = rs.getString(1).trim();
                memberPersistDTO.setMdn(mdn);
                //multilingual revert changes
                if (null != rs.getString(2)) {
                    try {
                        memberPersistDTO.setName(new String(rs.getString(2).getBytes("8859_1"), "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        knLogger.error(methodName, " Exception in encoding Subscriber Name for MDN- ", rs.getString(2), e);
                    }
                }
                memberPersistDTO.setSubscriptionType(getMappedSubscriptionType(rs.getInt(3), rs.getInt(4)));
                if (null != contCountmap.get(mdn)) {
                    int contactCount = contCountmap.get(mdn);
                    memberPersistDTO.setSubscContactsCount(contactCount);
                    if (contactCount == maxContact) {
                        memberPersistDTO.setMaxContactLimitFlag(EQUAL_TO_LIMIT);
                    } else if (contactCount < maxContact) {
                        memberPersistDTO.setMaxContactLimitFlag(LESS_THAN_LIMIT);
                    } else if (contactCount > maxContact) {
                        memberPersistDTO.setMaxContactLimitFlag(GREATER_THAN_LIMIT);
                    }
                    memberPersistDTO.setContactCount(contactCount);
                }
                memberPersistDTO.setServiceAuthStatus(rs.getInt(5));
                memberPersistDTO.setClientType(rs.getInt(6));
                memberPersistDTO.setCorpId(rs.getInt(12));
                grpMemList.add(memberPersistDTO);
            }
            knLogger.debug(methodName, "subscribersList size", grpMemList.size()," grpMemList :",grpMemList);
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to retrieve external cont name ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        return grpMemList;
    }

    /**
     * This method returns the subscribers details querying dg.pocsubscrinfo table.
     *
     * @param operationType
     * @param mdnList
     * @param corpId
     * @param maxContactLimit
     * @param contCountMap
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public List<KnCorpContactDTO> getSubsDetails(String operationType, List<String> mdnList, int corpId, int maxContactLimit, Map<String, Integer>
            contCountMap, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubsDetails(List, int, int,boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : mdnList - ", mdnList.size(), ", corpId - ", corpId);
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        List<KnCorpContactDTO> memberList = new ArrayList<KnCorpContactDTO>(mdnList.size());
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_GROUP_MEMBERS_LIST);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            var mdnListArray = new ArrayList<>(mdnList);
            var subsLists = KnGeneralUtil.splitList(mdnListArray, BULK_UPDATE_SIZE);
            for (var subsList : subsLists) {
                query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedQuesMarks(subsList));
                knLogger.debug(methodName, "query", query);
                int index=1;
                pstmt = conn.prepareStatement(query);
                for (String mdn : subsList) {
                    pstmt.setString(index++, mdn);
                }
                pstmt.setInt(index, corpId);
                rs = pstmt.executeQuery();
                while (rs.next()) {
                    // exclude the async delete mdns for getSublistDetails operationType
                    if( KnOperationTypes.GET_SUBLIST_DETAILS.equals(operationType) && MARKED_FOR_ASYNC_DELETION.value() ==rs.getInt(5) ){
                        continue;
                    }
                    KnCorpContactDTO memDto = new KnCorpContactDTO();
                    String mdn = rs.getString(1).trim();
                    memDto.setMdn(mdn);
                    //multilingual revert change
                    if (null != rs.getString(2)) {
                        try {
                            memDto.setName(new String(rs.getString(2).getBytes("8859_1"), "UTF-8"));
                        } catch (UnsupportedEncodingException e) {
                            knLogger.error(methodName, " Exception in encoding Subscriber Name for MDN- ", rs.getString(2), e);
                        }
                    }
                    memDto.setSubscriptionType(getMappedSubscriptionType(rs.getInt(3), rs.getInt(4)));
                    Integer contactCount = contCountMap.get(mdn);
                    if (null != contactCount) {
                        memDto.setContactCount(contactCount);
                        if (contactCount == maxContactLimit) {
                            memDto.setMaxContactLimitFlag(EQUAL_TO_LIMIT);
                        } else if (contactCount < maxContactLimit) {
                            memDto.setMaxContactLimitFlag(LESS_THAN_LIMIT);
                        } else if (contactCount > maxContactLimit) {
                            memDto.setMaxContactLimitFlag(GREATER_THAN_LIMIT);
                        }
                    }
                    memDto.setClientType(rs.getInt(6));
                    memDto.setServiceAuthStatus(rs.getInt(5));
                    memDto.setAliasMdn(rs.getString(7));
                    memDto.setUserId(rs.getString(8));
                    String activeFs2 = rs.getString(10) != null ? rs.getString(10) : KnGeneralUtil.convertLongToHexString(rs.getLong(9));
                    memDto.setSubsActiveFS2(activeFs2);
                    memDto.setClientPVmajorVer(rs.getInt(11));
                    memberList.add(memDto);
                }
            }
            knLogger.debug(methodName, "EXIT : MemberList list size  - ", memberList.size());
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to retrieve group member list ",
                    pttServerId, KnDAOSourceTypes.GRPMEMBERDETAILS, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        return memberList;
    }

    public KnCorpSubscriberDTO selectPocSubscriberInfo(String mdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "selectPocSubscriberInfo(String,boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : mdn - ", KnGDPRTemplate.mdn(mdn));
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        KnCorpSubscriberDTO subscriberDTO = new KnCorpSubscriberDTO();
        boolean ownedTxn = false;
        try {
            if (persisterTxn != null) {
                knLogger.debug(methodName, " Data Store before - ", KnDBConst.DataStores.XDM_SHARED_DATA);
                conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
                knLogger.debug(methodName, "if block");
            } else {
                conn = KnDBManager.getDBManagerInstance().getConnection(KnGenInfoUtil.getSecDBPttServerId(pttServerId), true);
                ownedTxn = true;
                knLogger.debug(methodName, "else block");
                knLogger.debug(methodName, "conn", conn);
            }
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_SUBSCRIBER_INFO);
           // conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, mdn);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            if (rs.next()) {
                subscriberDTO.setCorpId(rs.getInt(1));
                subscriberDTO.setMdn(rs.getString(2).trim());
                subscriberDTO.setClientType(rs.getInt(3));
                subscriberDTO.setPocHome(rs.getString(5));
                subscriberDTO.setPresenceHome(rs.getString(6));
                subscriberDTO.setClientPVmajorVer(rs.getInt(7));
                String activeFs2=rs.getString(8)!=null?rs.getString(8):KnGeneralUtil.convertLongToHexString(rs.getLong(4));
                subscriberDTO.setSubsActiveFS2(activeFs2);
                subscriberDTO.setUserProfileId(rs.getString(9));
                subscriberDTO.setMcpttCompliance(rs.getInt(10));
                subscriberDTO.setMcpttId(rs.getBytes(11)!=null?new String(rs.getBytes(11),StandardCharsets.UTF_8):null);
            } else {
                throw new KnDBPersistenceException(KnErrorCodes.DAO.ROW_NOT_FOUND, "Subscriber info not found.",
                        pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
            }
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while fetching the PoC subcribers info .",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            if (ownedTxn) {
                KnDbUtil.closeConnection(conn);
            }
        }
        knLogger.debug(methodName, "inputMdn - ", KnGDPRTemplate.mdn(mdn), "EXIT : PoC subscribers", subscriberDTO);
        return subscriberDTO;
    }

    public void modifySubsCorpFeatureSet(KnCorpSubscriberDTO corpSubscriberDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "modifySubsCorpFeatureSet(KnIPSubsProvInfoDTO subsProvInfoDTO, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "corpSubscriberDTO", corpSubscriberDTO.toString());
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(MODIFY_SUBS_CORP_FEATURE_SET);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            pstmt.setLong(1, KnGeneralUtil.convertHexStringToLong(corpSubscriberDTO.getCorpAdminFS2()));
            pstmt.setLong(2, KnGeneralUtil.convertHexStringToLong(corpSubscriberDTO.getSubsActiveFS2()));
            pstmt.setLong(3, corpSubscriberDTO.getLastProfileUpdateTime());
            pstmt.setString(4, KnGeneralUtil.getFeatureSet(corpSubscriberDTO.getCorpAdminFS2()));
            pstmt.setString(5, KnGeneralUtil.getFeatureSet(corpSubscriberDTO.getSubsActiveFS2()));
            pstmt.setString(6, corpSubscriberDTO.getMdn());
            pstmt.executeQuery();
            knLogger.debug(methodName, "Exit: Query executed successfully");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while modifySubsCorpFeatureSet",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
        knLogger.debug(methodName);
    }


    public KnCorpSubscriberDTO getSubscriberDetail(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubscriberDetail(String, int,  KnPersisterTxn)";
        knLogger.debug(methodName, "mdn", KnGDPRTemplate.mdn(mdn));
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        KnCorpSubscriberDTO corpSubscriberDTO = new KnCorpSubscriberDTO();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_POC_SUSBCR_DETAIL);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, mdn);
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            if (rs.next()) {
                corpSubscriberDTO.setPocHome(rs.getString(1));
                corpSubscriberDTO.setPresenceHome(rs.getString(2));
                corpSubscriberDTO.setXdmsHome(rs.getString(3));
                corpSubscriberDTO.setClientType(rs.getInt(9));
                corpSubscriberDTO.setClientPVmajorVer(rs.getInt(10));
                corpSubscriberDTO.setCorpId(rs.getInt(11));
                String clientFS=rs.getString(13)!=null?rs.getString(13):KnGeneralUtil.convertLongToHexString(rs.getLong(4));
                corpSubscriberDTO.setClientFs2(clientFS);
                String subsFS=rs.getString(14)!=null?rs.getString(14):KnGeneralUtil.convertLongToHexString(rs.getLong(5));
                corpSubscriberDTO.setSubscriberFs2(subsFS);
                String opsFS=rs.getString(15)!=null?rs.getString(15):KnGeneralUtil.convertLongToHexString(rs.getLong(6));
                corpSubscriberDTO.setOpsFs2(opsFS);
                String corpAdminFS=rs.getString(16)!=null?rs.getString(16):KnGeneralUtil.convertLongToHexString(rs.getLong(7));
                corpSubscriberDTO.setCorpAdminFS2(corpAdminFS);
                String activeFS=rs.getString(17)!=null?rs.getString(17):KnGeneralUtil.convertLongToHexString(rs.getLong(8));
                corpSubscriberDTO.setSubsActiveFS2(activeFS);
                String xdmsFS=rs.getString(18)!=null?rs.getString(18):KnGeneralUtil.convertLongToHexString(rs.getLong(12));
                corpSubscriberDTO.setXdmsFs2(xdmsFS);
                if(rs.getString(19)!=null)
                {
                    corpSubscriberDTO.setUserProfileFS2(rs.getString(19));
                }
            } else {
                throw new KnDBPersistenceException(KnErrorCodes.DAO.ROW_NOT_FOUND, "Subscriber info not found.",
                        pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
            }
            return corpSubscriberDTO;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while fetching the PoC subcribers List - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while fetching the PoC subcribers List - ", e);
            throw KnDbUtil.processException(e, "Failed while fetching the PoC subcribers List ." + e, pttServerId,
                    KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            knLogger.debug(methodName, "EXIT : PoC subscribers list returned ", corpSubscriberDTO.toString());
        }
    }


    public boolean isExternalSubscriber(String mdn, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "isExternalSubscriber(mdn, corpId,  KnPersisterTxn)";
        knLogger.debug(methodName, "mdn", KnGDPRTemplate.mdn(mdn), "corpId", corpId);
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        boolean corpSubscriberDTO = false;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(IS_EXTERNAL_CONTACT);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, mdn);
            pstmt.setInt(2, corpId);
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            if (rs.next()) {
                corpSubscriberDTO = true;
            }
            return corpSubscriberDTO;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while checking External Subscriber", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while checking External Subscriber", e);
            throw KnDbUtil.processException(e, "Failed while fetching the PoC subcribers List ." + e, pttServerId,
                    KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            knLogger.debug(methodName, "EXIT : PoC subscribers list returned ", corpSubscriberDTO);
        }
    }

    public boolean isDispatchMemberPresent(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "isDispatchMemberPresent(List<String>, KnPersisterTxn)";
        boolean isDispatchMember = false;
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;

        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            Collection<Collection<String>> compList = KnDbUtil.getCollectionList(mdnList, BULK_UPDATE_SIZE);
            for (Collection<String> subMdnList : compList) {
                int index=1;
                query = queryMapper.getQuery(GET_DISPATCH_CORP_MEMBERS);
                query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedQuesMarks(subMdnList));
                knLogger.debug(methodName, "query", query);
                pstmt = conn.prepareStatement(query);
                for(String mdn:subMdnList){
                    pstmt.setString(index++, mdn);
                }
                rs = pstmt.executeQuery();
                if (rs.next()) {
                    isDispatchMember = true;
                    break;
                }
            }
            knLogger.debug(methodName, "Exit: Query executed successfully");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while isDispatchMemberPresent",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        return isDispatchMember;
    }


    public ArrayList<String> getInternalNonDispatchMember(Collection<String> mdnList, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getInternalNonDispatchMember(Collection<String>, int, KnPersisterTxn)";
        ArrayList<String> intNonDispMembrs = new ArrayList<String>();
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            ArrayList<String> mdnArrList = new ArrayList<String>();
            if (mdnList != null) {
                mdnArrList.addAll(mdnList);
            }
            Collection<Collection<String>> compList = KnDbUtil.getCollectionList(mdnArrList, BULK_UPDATE_SIZE);
            for (Collection<String> subMdnList : compList) {
                query = queryMapper.getQuery(GET_NON_DISPATCH_INTERNAL_MEMBERS);
                query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedIdList(subMdnList));
                knLogger.debug(methodName, "Executing query -", query);
                pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, corpId);
                rs = pstmt.executeQuery();
                while (rs.next()) {
                    intNonDispMembrs.add(rs.getString(1).trim());
                }
            }
            knLogger.debug(methodName, "Exit: Query executed successfully  Data returned -", intNonDispMembrs);
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while getInternalNonDispatchMember",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        return intNonDispMembrs;
    }

    /**
     * @param corpId       corporate id
     * @param persisterTxn
     * @return count of subscriber in a corporate, return 0 if there is no subscriber in a corporate
     * @throws KnDAOException
     */
    public int getInternalSubscriberCount(int corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getInternalSubscriberCount(String,boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : corpId=", corpId, " readOnly :", readOnly);
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        int count = 0;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_INTERNAL_CONTACT_COUNT);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            knLogger.debug(methodName, "Executing query:", query);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, corpId);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to fetch internal contact count",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        knLogger.debug(methodName, "EXIT : Internal contact count=", count);
        return count;
    }

    /**
     * @param keySet       list of subscriber which contact count need to fetch
     * @param persisterTxn
     * @return map of subscriber and its contact count
     * @throws KnDAOException
     */
    public Map<String, Integer> getSubscriberContactCount(Set<String> keySet, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "getSubscriberContactCount(String,boolean,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : keySet - ", KnGDPRTemplate.mdnSet(keySet));
        Map<String, Integer> subsCntCountMap = new HashMap<String, Integer>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        List<String> mdnList = new ArrayList<String>();
        mdnList.addAll(keySet);
        Collection<Collection<String>> collList = KnDbUtil.getCollectionList(mdnList, BULK_UPDATE_SIZE);
        conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
        KnQueryMapper queryMapper = KnQueryMapper.getInstance();

        for (Collection<String> splitMdnList : collList) {
            try {
                int index=1;
                query = queryMapper.getQuery(GET_SUBSCRIBER_CONTACT_COUNT);
                query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedQuesMarks(splitMdnList));
                knLogger.debug(methodName, "query", query);
                pstmt = conn.prepareStatement(query);
                for(String mdn:splitMdnList){
                    pstmt.setString(index++, mdn);
                }
                rs = pstmt.executeQuery();

                knLogger.debug(methodName, "Query executed successfully");
                while (rs.next()) {
                    subsCntCountMap.put(rs.getString(1).trim(), rs.getInt(2));
                }
            } catch (SQLException e) {
                throw KnDbUtil.processException(e,
                        "Failed to fetch subscriber contact count",
                        pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO,
                        query);
            } finally {
                KnDbUtil.closeResultSet(rs);
                KnDbUtil.closeStatement(pstmt);
            }
        }
        knLogger.debug(methodName, "EXIT");
        return subsCntCountMap;
    }


    public Map<String, KnCorpSubscriberDTO> getSubsribersCorporateDetails(Collection<String> mdnList, int corpId,
                                                                          KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "getSubsribersCorporateDetails(Collection<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :MdnList - ", mdnList == null ? mdnList : KnGDPRTemplate.mdnList(mdnList));

        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String query = null;
        Map<String, KnCorpSubscriberDTO> subscribersMap = new HashMap<String, KnCorpSubscriberDTO>();
        List<String> nniMdnList = new ArrayList<>(mdnList);
        List<String> nniMdnListUpdated = new CopyOnWriteArrayList<>(mdnList);
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            Collection<Collection<String>> compList = KnDbUtil.getCollectionList(nniMdnList, BULK_UPDATE_SIZE);
            for (Collection<String> subMdnList : compList) {
                query = queryMapper.getQuery(GET_POC_SUBSCRIBER);
                int index = 1;
                query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedQuesMarks(subMdnList));
                knLogger.debug(methodName, "query", query);
                pStmt = conn.prepareStatement(query);
                for (String mdn : subMdnList) {
                    pStmt.setString(index++, mdn);
                }
                rs = pStmt.executeQuery();
                knLogger.debug(methodName, "Query executed successfully");
                while (rs.next()) {
                    KnCorpSubscriberDTO subscriberDTO = new KnCorpSubscriberDTO();
                    String mdn = rs.getString(2).trim();
                    nniMdnListUpdated.remove(mdn);
                    subscriberDTO.setMdn(mdn);
                    int mdnCorpId = rs.getInt(1);
                    if (corpId != mdnCorpId) {
                        if (mdnCorpId > 0) {
                            subscriberDTO.setContact_type(KnConstants.XCAP_DIFF_CONTACT_TYPE_EXTERNAL_CONTACT);
                        } else {
                            subscriberDTO.setContact_type(KnConstants.XCAP_DIFF_CONTACT_TYPE_EXTERNAL_SUBSCRIBER);
                        }
                        subscriberDTO.setExternalContact(true);
                    }
                    subscriberDTO.setCorpId(mdnCorpId);
                    //multilingual revert change
                    if (null != rs.getString(3)) {
                        try {
                            subscriberDTO.setName(new String(rs.getString(3).getBytes("8859_1"), "UTF-8"));
                        } catch (UnsupportedEncodingException e) {
                            knLogger.error(methodName, " Exception in encoding Subscriber Name for MDN- ", rs.getString(2), e);
                        }
                    }
                    int publicType = rs.getInt(4);
                    int corpType = rs.getInt(5);
                    subscriberDTO.setSubscriptionType(getMappedSubscriptionType(publicType, corpType));
                    subscriberDTO.setClientType(rs.getInt(6));
                    subscriberDTO.setPocHome(rs.getString(7));
                    subscriberDTO.setPresenceHome(rs.getString(8));
                    String activeFs = rs.getString(10) != null ? rs.getString(10) : KnGeneralUtil.convertLongToHexString(rs.getLong(9));
                    subscriberDTO.setSubsActiveFS2(activeFs);
                    subscriberDTO.setNotfnCapabiliy(KnGeneralUtil.getFeatureBitValue(activeFs, 15));
                    subscriberDTO.setClientPVmajorVer(rs.getInt(11));
                    subscriberDTO.setUserAgent(rs.getString(12));
                    String subsFs = rs.getString(15) != null ? rs.getString(15) : KnGeneralUtil.convertLongToHexString(rs.getLong(14));
                    subscriberDTO.setSubscriberFs2(subsFs);
                    subscriberDTO.setMcpttCompliance(rs.getInt(18));
                    subscriberDTO.setUserProfileIndex(rs.getInt("USERPROFILEINDEX"));
                    subscribersMap.put(mdn, subscriberDTO);
                }
                for (String nniMdn : nniMdnListUpdated) {
                    KnCorpSubscriberDTO subscriberDTO = new KnCorpSubscriberDTO();
                    subscriberDTO.setMdn(nniMdn);
                    subscriberDTO.setExternalContact(true);
                    subscriberDTO.setContact_type(KnConstants.XCAP_DIFF_CONTACT_TYPE_EXTERNAL_SUBSCRIBER);
                    subscribersMap.put(nniMdn, subscriberDTO);

                }
                // Collection<String> keySet = subscribersMap.keySet();
            }
            knLogger.debug(methodName, "Fetched map size is  :  ", KnGDPRTemplate.mapKeyMdn(subscribersMap));
            return subscribersMap;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getting corp subscriber details from the mdn list passed - "
                    + e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while getting corp subscriber details ",
                    "from the mdn list passed -", e);
            throw KnDbUtil.processException(e, "Failed while getting corp subscriber details from the mdn list passed" + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            knLogger.debug(methodName, "EXIT :Fetched map size is  :  ", subscribersMap.size());
        }
    }


    /**
     * This method returns the internal subscribers belongs to the corporation.
     *
     * @param mdnList
     * @param corpid
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public List<KnCorpSubscriberDTO> getCorpSubscrDetails(List<String> mdnList, int corpid, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getCorpSubscrDetails(List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String query = null;
        List<KnCorpSubscriberDTO> subscrList = new ArrayList<>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            Collection<Collection<String>> compList = KnDbUtil.getCollectionList(mdnList, BULK_UPDATE_SIZE);
            for (Collection<String> subMdnList : compList) {
                int index = 1;
                query = queryMapper.getQuery(SELECT_SUBSCRINFO_NAME_FOR_CORP);
                query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedQuesMarks(subMdnList));
                knLogger.debug(methodName, "query", query);
                pStmt = conn.prepareStatement(query);
                for (String mdn : subMdnList) {
                    pStmt.setString(index++, mdn);
                }
                pStmt.setInt(index, corpid);
                rs = pStmt.executeQuery();
                knLogger.debug(methodName, "Executing query - ", query);
                List<String> extList = new ArrayList<>(subMdnList);

                knLogger.debug(methodName, "Query executed successfully");
                while (rs.next()) {
                    KnCorpSubscriberDTO subscriberDTO = new KnCorpSubscriberDTO();
                    String mdn = rs.getString(1).trim();
                    extList.remove(mdn);
                    subscriberDTO.setMdn(mdn);
                    //multilingual revert changes
                    if (null != rs.getString(2)) {
                        try {
                            subscriberDTO.setName(new String(rs.getString(2).getBytes("8859_1"), "UTF-8"));
                        } catch (UnsupportedEncodingException e) {
                            knLogger.error(methodName, " Exception in encoding Subscriber Name for MDN- ", rs.getString(1), e);
                        }
                    }
                    subscriberDTO.setCorpId(corpid);
                    subscriberDTO.setExternalContact(Boolean.FALSE);
                    subscriberDTO.setClientType(rs.getInt(3));
                    subscrList.add(subscriberDTO);
                }
                KnDbUtil.closeResultSet(rs);
                KnDbUtil.closeStatement(pStmt);
                rs = null; pStmt = null;

                var mdnListArray = new ArrayList<>(extList);
                var subsLists = KnGeneralUtil.splitList(mdnListArray, BULK_UPDATE_SIZE);
                for (var subsList : subsLists) {
                    query = queryMapper.getQuery(GET_EXTERNAL_CONTACT);
                    // query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedIdList(extList));
                    query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedQuesMarks(subsList));
                    knLogger.debug(methodName, "query", query);
                    pStmt = conn.prepareStatement(query);
                    index = 1;
                    for (String mdn : subsList) {
                        pStmt.setString(index++, mdn);
                    }
                    pStmt.setInt(index, corpid);
                    rs = pStmt.executeQuery();
                    knLogger.debug(methodName, "Query executed successfully");
                    while (rs.next()) {
                        KnCorpSubscriberDTO subscriberDTO = new KnCorpSubscriberDTO();
                        String mdn = rs.getString(1).trim();
                        extList.remove(mdn);
                        subscriberDTO.setMdn(mdn);
                        subscriberDTO.setExternalContact(true);
                        subscriberDTO.setCorpId(rs.getInt(2));
                        //multtilingual revert changes
                        if (rs.getString(3) != null) {
                            try {
                                subscriberDTO.setName(new String(rs.getString(3).getBytes("8859_1"), "UTF-8"));
                            } catch (UnsupportedEncodingException e) {
                                knLogger.error("UnsupportedEncodingException while parsing subsc name ", e);
                            }
                        }
                        subscriberDTO.setContact_type(rs.getInt(4));
                        subscrList.add(subscriberDTO);
                    }
                    KnDbUtil.closeResultSet(rs);
                    KnDbUtil.closeStatement(pStmt);
                    rs = null; pStmt = null;
                }
                if (!extList.isEmpty()) {
                    query = queryMapper.getQuery(GET_SUBS_BASIC_DETAILS);
                    conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
                    query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedQuesMarks(extList));
                    knLogger.debug(methodName, "query", query);
                    pStmt = conn.prepareStatement(query);
                    index = 1;
                    for (String mdn : extList) {
                        pStmt.setString(index++, mdn);
                    }
                    rs = pStmt.executeQuery();
                    knLogger.debug(methodName, "Executing query - ", query);
                    knLogger.debug(methodName, "Query executed successfully");
                    while (rs.next()) {
                        KnCorpSubscriberDTO subscriberDTO = new KnCorpSubscriberDTO();
                        String mdn = rs.getString(1).trim();
                        subscriberDTO.setMdn(mdn);
                        //multilingual revert changes
                        if (null != rs.getString(2)) {
                            try {
                                subscriberDTO.setName(new String(rs.getString(2).getBytes("8859_1"), "UTF-8"));
                            } catch (UnsupportedEncodingException e) {
                                knLogger.error(methodName, " Exception in encoding Subscriber Name for MDN- ", rs.getString(1), e);
                            }
                        }
                        subscriberDTO.setCorpId(corpid);
                        subscriberDTO.setExternalContact(Boolean.FALSE);
                        subscriberDTO.setClientType(rs.getInt(3));
                        subscrList.add(subscriberDTO);
                    }
                    KnDbUtil.closeResultSet(rs);
                    KnDbUtil.closeStatement(pStmt);
                    rs = null; pStmt = null;

                }
            }
            knLogger.debug(methodName, "Fetched map size is  :  ", subscrList.size());
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while getting corp subscriber details from the mdn list passed" + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
        }
        return subscrList;
    }

    /**
     * Method to get the subscribers broadcast group feature bit from subscribers activetfs1 value.
     *
     * @param mdnList
     * @param corpid
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<String, Boolean> getSubscrBCGrpBit(List<String> mdnList, int corpid, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubscrBCGrpBit(List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String query = null;
        int index =1;
        Map<String, Boolean> brdstrBitMap = new HashMap<>(mdnList.size());
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = "SELECT MDN, SUBSCRIBERFS2 FROM DG.POCSUBSCRINFO WHERE MDN IN (MDNLIST) AND CORPID = ?";
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedQuesMarks(mdnList));
            knLogger.debug(methodName, "query", query);
                pStmt = conn.prepareStatement(query);
                for(String mdn:mdnList){
                    pStmt.setString(index++, mdn);
                }
            pStmt.setInt(index, corpid);
                rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                String mdn = rs.getString(1).trim();
                String subsFS = rs.getString(2) != null ? rs.getString(2) : KnGeneralUtil.convertBitSetToHexString(KnGeneralUtil.convertLongToBitSet(rs.getLong(2)));
                brdstrBitMap.put(mdn, KnGeneralUtil.getFeatureBitValue(subsFS, 5));
            }
            knLogger.debug(methodName, "Fetched map size is  :  ", brdstrBitMap.size());
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while getting corp subscriber details from the mdn list passed" + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
        }
        return brdstrBitMap;
    }

    /**
     * Method to retrieve the subscriber name from dg.pocsubscrinfo table.
     *
     * @param sublistIds
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public List<KnMDNInfoDto> getSubscrReverseContacts(ArrayList<Integer> sublistIds, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubscrReverseContacts(ArrayList<Integer>, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : sublistIds - ", sublistIds);
        List<KnMDNInfoDto> mdnInfoDtoList = new ArrayList<>(sublistIds.size());
        Connection conn;
        String query;
        List<List<Integer>> collList = KnDbUtil.getLists(sublistIds, 1000);
        conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
        KnQueryMapper queryMapper = KnQueryMapper.getInstance();
        query = queryMapper.getQuery(GET_SUBSCR_REVERSE_CONTACTS);
        for (List<Integer> splitMdnList : collList) {
            try {
                getMDNInLoop(splitMdnList, conn, mdnInfoDtoList, query);
            } catch (SQLException e) {
                throw KnDbUtil.processException(e, "Failed ", pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
            }
        }
        knLogger.debug(methodName, "EXIT", mdnInfoDtoList.toString());
        return mdnInfoDtoList;
    }

    /**
     * Method to retrieve the subscriber name from dg.pocsubscrinfo table and set the detail in mdnInfoDtoList request argument
     *
     * @param mdnList
     * @param conn
     * @param mdnInfoDtoList
     * @param query
     * @throws KnDAOException
     */
    private void getMDNInLoop(List<Integer> mdnList, Connection conn, List<KnMDNInfoDto> mdnInfoDtoList, String
            query) throws SQLException {
        String methodName = "getMDNInLoop()";
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        int index=1;
        try {
            query = replaceContactWithValue(query, SUBLISTID, formCommaSeperatedIntegerQuesMarks(mdnList));
            knLogger.debug(methodName, "query", query);
                pStmt = conn.prepareStatement(query);
                for(Integer mdn:mdnList){
                    pStmt.setInt(index++, mdn);
                }
                rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                KnMDNInfoDto mdnInfoDto = new KnMDNInfoDto();
                mdnInfoDto.setMdn(rs.getString(1));
                //multilingual revert changes
                if (null != rs.getString(2)) {
                    try {
                        mdnInfoDto.setName(new String(rs.getString(2).getBytes("8859_1"), "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        knLogger.error(methodName, " Exception in encoding Subscriber Name for MDN - ", rs.getString(1), e);
                    }
                }
                mdnInfoDtoList.add(mdnInfoDto);
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
        }
    }

    public List<String> getGroupMdnInList(Collection<String> mdnList, int corpId,boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getGroupMdnInList(Collection<String>, int, boolean)";
        knLogger.entry(methodName, "ENTRY : mdnList size " , mdnList.size());
        var returnList = new ArrayList<String>();
        var mdnListArray = new ArrayList<>(mdnList);
        var subsLists = KnGeneralUtil.splitList(mdnListArray, BULK_UPDATE_SIZE);
        for (var subsList : subsLists) {
            var groupMdnList = getGroupMdnInSubList(subsList, corpId,readOnly, persisterTxn);
            if(groupMdnList != null && !groupMdnList.isEmpty())
                returnList.addAll(groupMdnList);
        }
        knLogger.exit(methodName, "EXIT : returnList size " , returnList.size());
        return returnList;
    }
    private List<String> getGroupMdnInSubList(Collection<String> mdnList, int corpId,boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "getGroupMdnInSubList(Collection<String>, int, boolean)";
        knLogger.debug(methodName, "ENTRY : mdnList ---> ", mdnList == null ? mdnList : KnGDPRTemplate.mdnList(mdnList), "corpId --- >", corpId, " readOnly :", readOnly);
        PreparedStatement pStmt=null;
        Connection conn;
        String query;
        int index=1;
        List<String> groupMdnList = new ArrayList<String>();
        ResultSet rs = null;
        conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
        KnQueryMapper queryMapper = KnQueryMapper.getInstance();
        query = queryMapper.getQuery(GET_GROUP_MDN_FROM_LIST);
        query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedQuesMarks(mdnList));
        knLogger.debug(methodName, "query", query);
        try {
            pStmt = conn.prepareStatement(query);
            for(String mdn:mdnList){
                pStmt.setString(index++, mdn);
            }
            pStmt.setInt(index, corpId);
            rs = pStmt.executeQuery();
            while (rs.next()) {
                String mdn = rs.getString(1).trim();
                knLogger.debug(methodName, "mdn-", KnGDPRTemplate.mdn(mdn));
                groupMdnList.add(mdn);
            }
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while getting group mdns from the DB" + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
        }
        knLogger.debug(methodName, "EXIT", KnGDPRTemplate.mdnList(groupMdnList));
        return groupMdnList;
    }

    public List<KnCorpSubscriberDTO> getGroupMdnInListDTO(Collection<String> mdnList, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getGroupMdnInListDTO(Collection<String>, int)";
        knLogger.debug(methodName, "ENTRY : mdnList ---> ", mdnList== null ? mdnList : KnGDPRTemplate.mdnList(mdnList), "corpId --- >", corpId);
        PreparedStatement pStmt = null;
        Connection conn;
        String query = null;
        List<KnCorpSubscriberDTO> groupMdnList = new ArrayList<>();
        int index=1;
        ResultSet rs = null;
        conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
        KnQueryMapper queryMapper = KnQueryMapper.getInstance();


        try {
            Collection<Collection<String>> compList = KnDbUtil.getCollectionList(new ArrayList<>(mdnList), BULK_UPDATE_SIZE);
            for (Collection<String> subMdnList : compList) {
                query = queryMapper.getQuery(GET_GROUP_MDN_FROM_LIST);
                query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedQuesMarks(subMdnList));
                knLogger.debug(methodName, "query", query);
                pStmt = conn.prepareStatement(query);
                for (String mdn : mdnList) {
                    pStmt.setString(index++, mdn);
                }
                pStmt.setInt(index, corpId);
                rs = pStmt.executeQuery();
                KnCorpSubscriberDTO sgMdnDTO = null;
                while (rs.next()) {
                    sgMdnDTO = new KnCorpSubscriberDTO();
                    sgMdnDTO.setMdn(rs.getString(1).trim());
                    sgMdnDTO.setClientType(rs.getInt(2));
                    groupMdnList.add(sgMdnDTO);
                }
            }
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while getting group mdns from the DB" + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
        }
        knLogger.debug(methodName, "EXIT", groupMdnList.toString());
        return groupMdnList;
    }

    /**
     * This is used to get the TGSC etag for subscribers.
     *
     * @param mdnList
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<String, Boolean> getTgscFeatureBit(Set<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getTgscFeatureBit(Set<String>, int)";
        knLogger.debug(methodName, "ENTRY :");
        Map<String, Boolean> tgscMap = new HashMap<String, Boolean>();
        KnQueryMapper queryMapper = KnQueryMapper.getInstance();
        String query = queryMapper.getQuery(GET_MDNS_ACTIVEFS);
        knLogger.debug(methodName, "query :" + query);
        Collection<Collection<String>> compList = KnDbUtil.getCollectionList(new ArrayList<String>(mdnList), BULK_UPDATE_SIZE);
        for (Collection<String> mdnSplitList : compList) {
            tgscMap.putAll(getTgscMap(mdnSplitList, query, persisterTxn));
        }
        knLogger.debug(methodName, "EXIT",KnGDPRTemplate.mapKeyMdn(tgscMap));
        return tgscMap;
    }

    /**
     * This method will be called in a loop to get the TGSC etag for the subscribers.
     *
     * @param mdnSplitList
     * @param query
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    private Map<String, Boolean> getTgscMap(Collection<String> mdnSplitList, String query, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getTgscMap()";
        PreparedStatement pStmt = null;
        Connection conn;
        ResultSet rs = null;
        int index=1;
        conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
        Map<String, Boolean> tgscMap = new HashMap<String, Boolean>();
        try {
            query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedQuesMarks(mdnSplitList));
            knLogger.debug(methodName, "query", query);
                pStmt = conn.prepareStatement(query);
                for(String mdn:mdnSplitList){
                    pStmt.setString(index++, mdn);
                }
                rs = pStmt.executeQuery();
            while (rs.next()) {
                String mdn = rs.getString(1).trim();
                String activeFs=rs.getString(3)!=null?rs.getString(3): KnGeneralUtil.convertBitSetToHexString(KnGeneralUtil.convertLongToBitSet(rs.getLong(2)));
                tgscMap.put(mdn, (KnGeneralUtil.getFeatureBitValue(activeFs, 28) || KnGeneralUtil.getFeatureBitValue(activeFs, 44)));
            }
            knLogger.debug(methodName, "tgscMap", KnGDPRTemplate.mapKeyMdn(tgscMap));

        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while getting subscribers scan bit" + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
        }
        return tgscMap;
    }

    public Collection<String> getMdnSpecificToClient(int corpId, int client_type, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "getGroupMDNfromDB()";
        knLogger.debug(methodName, "ENTRY : corpId=", corpId, "client_type=", client_type);
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        Collection<String> groupMdnList = new ArrayList<String>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_GROUP_MDN_FROM_DB);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, corpId);
            pstmt.setInt(2, client_type);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                String mdn = rs.getString(1).trim();
                knLogger.debug(methodName, "mdn- ", KnGDPRTemplate.mdn(mdn));
                groupMdnList.add(mdn);
            }
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while getting subscriber details from the corpid and client_type passed" + e,
                    pttServerId, KnDAOSourceTypes.SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        knLogger.debug(methodName, "EXIT", groupMdnList);
        return groupMdnList;
    }

    public Collection<String> getMdnsSpecificToClients(int corpId, Collection<Integer> client_types, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "getMdnsSpecificToClients(int,Collection<Integer>,boolean,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : corpId=", corpId, "client_types=", client_types);
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        Collection<String> specificMdns = new ArrayList<String>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_SPECIFIC_MDNS_FROM_DB);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            String clientTypes = formIntegerCommaSeperatedIdList(client_types);
            query = replaceContactWithValue(query, CLIENTTYPES, clientTypes);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, corpId);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                String mdn = rs.getString(1).trim();
                knLogger.debug(methodName, "mdn- ", KnGDPRTemplate.mdn(mdn));
                specificMdns.add(mdn);
            }
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while getting subscriber details from the corpid and client_type passed" + e,
                    pttServerId, KnDAOSourceTypes.SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        knLogger.debug(methodName, "EXIT", KnGDPRTemplate.mdnList(specificMdns));
        return specificMdns;
    }

    /**
     * This method is used to get the information if there are any SU or SG MDN in the corporate.
     *
     * @param corpId
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public boolean getIsGWEnabledForCorp(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getIsGWEnabledForCorp(corpId, persisterTxn)";
        knLogger.debug(methodName, "ENTRY : corpId=", corpId);
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        boolean gwIndicator = Boolean.FALSE;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_GW_INDICATOR);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, corpId);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                String mdn = rs.getString(1).trim();
                knLogger.debug(methodName, "mdn- ", KnGDPRTemplate.mdn(mdn));
                gwIndicator = Boolean.TRUE;
            }
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while getting subscriber details from the corpid and client_type passed" + e,
                    pttServerId, KnDAOSourceTypes.SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        knLogger.debug(methodName, "EXIT gwIndicator is", gwIndicator);
        return gwIndicator;
    }

    public KnSubscrFeatureSetRespDTO getSubscriberFeatureSets(int corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubscriberFeatureSets(corpId,boolean, persisterTxn)";
        knLogger.debug(methodName, "ENTRY : corpId =", corpId);
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        KnSubscrFeatureSetRespDTO subscrFeatureSetRespDTO = new KnSubscrFeatureSetRespDTO();
        boolean ownedTxn = false;
        try {
            if (persisterTxn != null) {
                knLogger.debug(methodName, " Data Store before - ", KnDBConst.DataStores.XDM_SHARED_DATA);
                conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            } else {
                conn = KnDBManager.getDBManagerInstance().getConnection(KnGenInfoUtil.getSecDBPttServerId(pttServerId), true);
                ownedTxn = true;
            }
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(SELECT_SUBSCRIBER_FEATURE_SET);
            //conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, corpId);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            List<KnCorpSubscrInfoDTO> subscrInfoDTOList = new ArrayList<>();
            while (rs.next()) {
                KnCorpSubscrInfoDTO knCorpSubscrInfoDTO = new KnCorpSubscrInfoDTO();
                knCorpSubscrInfoDTO.setMdn(rs.getString(1).trim());
                String clientFS=rs.getString(4)!=null?rs.getString(4):KnGeneralUtil.convertLongToHexString(rs.getLong(2));
                knCorpSubscrInfoDTO.setClientFS2(clientFS);
                String corpAdminFS=rs.getString(5)!=null?rs.getString(5):KnGeneralUtil.convertLongToHexString(rs.getLong(3));
                knCorpSubscrInfoDTO.setCorpAdminFS2(corpAdminFS);
                subscrInfoDTOList.add(knCorpSubscrInfoDTO);

            }
            subscrFeatureSetRespDTO.setSubscrInfoDTOList(subscrInfoDTOList);
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while getting subscriber details feature sets " + e,
                    pttServerId, KnDAOSourceTypes.SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            if (ownedTxn) {
                KnDbUtil.closeConnection(conn);
            }
        }
        knLogger.debug(methodName, "EXIT ", subscrFeatureSetRespDTO);
        return subscrFeatureSetRespDTO;
    }

    public Map<String, KnCorpSubscriberDTO> getSubscrFeatureBitDetails(Set<String> reqMdnList, int corpId, KnPersisterTxn
            persisterTxn) throws KnDAOException {
        String methodName = "getSubscrFeatureBitDetails()";
        knLogger.debug(methodName, "ENTRY : corpId =", corpId);
        Connection conn = null;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String query = null;
        int index =1;
        boolean ownedTxn = false;
        Map<String, KnCorpSubscriberDTO> subscriberDTOMap = new HashMap<>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(SELECT_SUBSCRIBER_FEATURE_SET_FOR_UPDATE);
            //conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            if (persisterTxn != null) {
                conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            } else {
                conn = KnDBManager.getDBManagerInstance().getConnection(KnGenInfoUtil.getSecDBPttServerId(pttServerId), true);
                ownedTxn = true;
            }
            knLogger.debug(methodName, "Statement ");
            query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedQuesMarks(reqMdnList));
            knLogger.debug(methodName, "query", query);
                pStmt = conn.prepareStatement(query);
                for(String mdn:reqMdnList){
                    pStmt.setString(index++, mdn);
                }
            pStmt.setInt(index, corpId);
                rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                KnCorpSubscriberDTO subscriberDTO = new KnCorpSubscriberDTO();
                String mdn = rs.getString(1).trim();
                subscriberDTO.setMdn(mdn);
                subscriberDTO.setPocHome(rs.getString(2));
                subscriberDTO.setPresenceHome(rs.getString(3));
                subscriberDTO.setXdmsHome(rs.getString(4));
                subscriberDTO.setClientType(rs.getInt(10));
                subscriberDTO.setClientPVmajorVer(rs.getInt(11));
                String clientFS=rs.getString(13)!=null?rs.getString(13):KnGeneralUtil.convertLongToHexString(rs.getLong(5));
                subscriberDTO.setClientFs2(clientFS);
                String subsFS=rs.getString(14)!=null?rs.getString(14):KnGeneralUtil.convertLongToHexString(rs.getLong(6));
                subscriberDTO.setSubscriberFs2(subsFS);
                String opsFS=rs.getString(15)!=null?rs.getString(15):KnGeneralUtil.convertLongToHexString(rs.getLong(7));
                subscriberDTO.setOpsFs2(opsFS);
                String corpAdminFS=rs.getString(16)!=null?rs.getString(16):KnGeneralUtil.convertLongToHexString(rs.getLong(8));
                subscriberDTO.setCorpAdminFS2(corpAdminFS);
                String activeFS=rs.getString(17)!=null?rs.getString(17):KnGeneralUtil.convertLongToHexString(rs.getLong(9));
                subscriberDTO.setSubsActiveFS2(activeFS);
                String xdmsFS=rs.getString(18)!=null?rs.getString(18):KnGeneralUtil.convertLongToHexString(rs.getLong(12));
                subscriberDTO.setXdmsFs2(xdmsFS);
                subscriberDTO.setLastProfileUpdateTime(rs.getLong(19));
                if (rs.getString(20) != null) {
                    subscriberDTO.setUserProfileFS2(rs.getString(20));
                }
                subscriberDTO.setServiceAuthStatus(rs.getInt(21));
                subscriberDTO.setUserAgent(rs.getString(22));
                subscriberDTO.setClientPVminorVer(rs.getInt(23));
                subscriberDTO.setLastActivationTime(rs.getLong(24));
                String imei = rs.getString(25);
                if (imei != null) {
                    imei = imei.trim();
                }
                subscriberDTO.setIMEI(imei);
                subscriberDTO.setClientPassword(rs.getString(26));
                subscriberDTO.setVocoderId(rs.getInt(27));
                subscriberDTO.setSwType(rs.getInt(28));
                subscriberDTO.setPlatformType(rs.getInt(29));
                subscriberDTO.setDynamicQosFlag(rs.getInt(30));
                String derKey = rs.getString(31);
                if (derKey != null && !(derKey.isEmpty())){
                    subscriberDTO.setDerivedKey(KnGeneralUtil.convertAsciiToHex(derKey));
                }
                subscriberDTO.setServiceStatusOp(rs.getInt(32));
                subscriberDTO.setLicenseType(rs.getInt(33));
                subscriberDTOMap.put(mdn, subscriberDTO);
            }
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while getting subscriber details feature sets " + e,
                    pttServerId, KnDAOSourceTypes.SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            if (ownedTxn) {
                KnDbUtil.closeConnection(conn);
            }
        }
        knLogger.debug(methodName, "EXIT ", KnGDPRTemplate.mapKeyMdn(subscriberDTOMap));
        return subscriberDTOMap;
    }

    public void updateSubscrProfiles(Map<String, KnCorpSubscriberDTO> updateSubscrMap, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "updateSubscrProfiles()";
        knLogger.info(methodName, "ENTRY");
        knLogger.debug(methodName, "updateSubscrMap", updateSubscrMap == null ? updateSubscrMap : KnGDPRTemplate.mapKeyMdn(updateSubscrMap));
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(MODIFY_SUBS_CORP_FEATURE_SET);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            for (Map.Entry<String, KnCorpSubscriberDTO> entry : updateSubscrMap.entrySet()) {
                KnCorpSubscriberDTO subscriberDTO = entry.getValue();
                pstmt.setLong(1, KnGeneralUtil.convertHexStringToLong(subscriberDTO.getCorpAdminFS2()));
                pstmt.setLong(2, KnGeneralUtil.convertHexStringToLong(subscriberDTO.getSubsActiveFS2()));
                pstmt.setLong(3, subscriberDTO.getLastProfileUpdateTime());
                pstmt.setString(4, KnGeneralUtil.getFeatureSet(subscriberDTO.getCorpAdminFS2()) );
                pstmt.setString(5, KnGeneralUtil.getFeatureSet(subscriberDTO.getSubsActiveFS2()));
                pstmt.setString(6, subscriberDTO.getMdn());
                pstmt.addBatch();
            }
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt.executeBatch();
            knLogger.debug(methodName, "EXit: Query executed successfully");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while updateSubscrProfiles",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
        knLogger.debug(methodName);
    }

    public void updateCorpSubscriber(KnIPSubscriberInfoDTO corpSubscriberDTO,List<String> profileMdns, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateCorpSubscriber()";
        knLogger.info(methodName, "ENTRY");
        knLogger.debug(methodName, "updateCorpSubscriber", corpSubscriberDTO);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        int publicSubscriptionType = 0;
        int corpSubscriptionType = 1;
        try {
            if (corpSubscriberDTO.getSubscriptionType() == SUBSCRIPTION_TYPE_PUBLIC_CORP) {
                publicSubscriptionType = 1;
            }
            int corpId = Integer.parseInt(corpSubscriberDTO.getCorpId());
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(MODIFY_CORP_SUBSCRIBER);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            if(profileMdns==null)
            {
            pstmt.setInt(1, publicSubscriptionType);
            pstmt.setInt(2, corpSubscriptionType);
            //multilingual revert changes
            if (corpSubscriberDTO.getSubscrName() != null) {
                try {
                    pstmt.setString(3, new String(corpSubscriberDTO.getSubscrName().getBytes("UTF-8"), "8859_1"));
                } catch (UnsupportedEncodingException e) {
                    knLogger.error("UnsupportedEncodingException while parsing subsc name ", e);
                }
            } else {
                pstmt.setNull(3, Types.VARCHAR);
            }

            pstmt.setString(4, corpSubscriberDTO.getSubscriberEmail());
            pstmt.setLong(5, corpSubscriberDTO.getLastProfileUpdateTime());
            pstmt.setInt(6, Integer.parseInt(corpSubscriberDTO.getDispatchType()));
            pstmt.setString(7, corpSubscriberDTO.getUserId());
            pstmt.setString(8, corpSubscriberDTO.getClientPassword());
            pstmt.setInt(9, corpSubscriberDTO.getServiceAuthStatusAU());
            pstmt.setInt(10, corpSubscriberDTO.getServiceAuthStatus());
            pstmt.setString(11, corpSubscriberDTO.getAliasMdn());
            pstmt.setString(12, corpSubscriberDTO.getMdn());
            pstmt.setInt(13, corpId);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt.executeQuery();
            knLogger.debug(methodName, "EXit: Query executed successfully");
            } else {

                query = queryMapper.getQuery(MODIFY_CORP_SUBSCRIBER_FOR_PROFILEMDN);
                conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
                knLogger.debug(methodName, "Executing query - ", query);
                pstmt = conn.prepareStatement(query);

                for (String profileMdn : profileMdns) {
                    pstmt.setInt(1, publicSubscriptionType);
                    pstmt.setInt(2, corpSubscriptionType);
                    // multilingual revert changes
                    if (corpSubscriberDTO.getSubscrName() != null) {
                        try {
                            pstmt.setString(3,
                                    new String(corpSubscriberDTO.getSubscrName().getBytes("UTF-8"), "8859_1"));
                        } catch (UnsupportedEncodingException e) {
                            knLogger.error("UnsupportedEncodingException while parsing subsc name ", e);
                        }
                    } else {
                        pstmt.setNull(3, Types.VARCHAR);
                    }
                    pstmt.setString(4, corpSubscriberDTO.getSubscriberEmail());
                    pstmt.setLong(5, corpSubscriberDTO.getLastProfileUpdateTime());
                    pstmt.setInt(6, Integer.parseInt(corpSubscriberDTO.getDispatchType()));
                    pstmt.setString(7, corpSubscriberDTO.getClientPassword());
                    pstmt.setInt(8, corpSubscriberDTO.getServiceAuthStatusAU());
                    pstmt.setInt(9, corpSubscriberDTO.getServiceAuthStatus());
                    pstmt.setString(10, profileMdn);
                    pstmt.setInt(11, corpId);
                    knLogger.debug(methodName, "Executing query - ", query);
                    pstmt.addBatch();
                    knLogger.debug(methodName, "EXit: Query executed successfully");
                }
                pstmt.executeBatch();
            }
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while updateCorpSubscriber",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
        knLogger.debug(methodName);
    }

    public void updateSubscriberServiceAuthStatus(KnIPSubscriberInfoDTO corpSubscriberDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateSubscriberServiceAuthStatus()";
        knLogger.info(methodName, "ENTRY");
        knLogger.debug(methodName, "updateSubscriberServiceAuthStatus", corpSubscriberDTO);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(UPDATE_SERVICE_AUTH_STATUS);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            pstmt.setLong(1, System.currentTimeMillis());
            pstmt.setInt(2, corpSubscriberDTO.getServiceAuthStatus());
            pstmt.setInt(3, corpSubscriberDTO.getServiceAuthStatusAU());
            pstmt.setInt(4, corpSubscriberDTO.getServiceAuthStatus());
            pstmt.setString(5, corpSubscriberDTO.getMdn());
            pstmt.setInt(6, Integer.parseInt(corpSubscriberDTO.getCorpId()));
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt.executeQuery();
            knLogger.debug(methodName, "EXit: Query executed successfully");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while updateSubscriberServiceAuthStatus",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
        knLogger.debug(methodName);
    }

    public String getUserIdProfile(KnIPSubscriberInfoDTO corpSubscriberDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getUserIdProfile()";
        knLogger.info(methodName, "ENTRY");
        knLogger.debug(methodName, "getUserIdProfile", corpSubscriberDTO);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        String mdn = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_USERID_PROFILE);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, corpSubscriberDTO.getUserId());
            pstmt.setString(2, corpSubscriberDTO.getMdn());
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                mdn = rs.getString(1);
            }
            knLogger.debug(methodName, "EXit: Query executed successfully");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while getUserIdProfile",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        knLogger.debug(methodName);
        return mdn;
    }

    public KnCorpSubscriberDTO getUserProfile(KnIPSubscriberInfoDTO corpSubscriberDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getUserProfile()";
        knLogger.info(methodName, "ENTRY");
        knLogger.debug(methodName, "getUserIdProfile", corpSubscriberDTO);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        KnCorpSubscriberDTO subsProfile = new KnCorpSubscriberDTO();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_USER_PROFILE);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, corpSubscriberDTO.getUserId());
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                subsProfile.setMdn(rs.getString(1));
                subsProfile.setCorpId(rs.getInt(2));
                subsProfile.setServiceAuthStatus(rs.getInt(3));
                subsProfile.setClientType(rs.getInt(4));
                subsProfile.setUserId(rs.getString(5));
                subsProfile.setName(rs.getString(6));
                subsProfile.setMcpttCompliance(rs.getInt(7));
                subsProfile.setMcId(rs.getBytes(8)!=null?new String(rs.getBytes(8),StandardCharsets.UTF_8):null);
                subsProfile.setMcpttId(rs.getBytes(9)!=null?new String(rs.getBytes(9),StandardCharsets.UTF_8):null);
                subsProfile.setMcVideoId(rs.getBytes(10)!=null?new String(rs.getBytes(10),StandardCharsets.UTF_8):null);
                subsProfile.setMcDataId(rs.getBytes(11)!=null?new String(rs.getBytes(11),StandardCharsets.UTF_8):null);
                subsProfile.setAliasMdn(rs.getString(12));
            }
            knLogger.debug(methodName, "EXit: Query executed successfully");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while getUserProfile",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        knLogger.debug(methodName);
        return subsProfile;
    }

    public Map<String, Integer> getGroupMembersClientTypeMap(Collection<String> mdnList, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getGroupMembersClientTypeMap(Collection<String>, int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : mdnList ", mdnList.size(), corpId);
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String query = null;
        Map<String, Integer> subsMap = new HashMap<>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            var mdnListArray = new ArrayList<>(mdnList);
            var subsLists = KnGeneralUtil.splitList(mdnListArray, BULK_UPDATE_SIZE);
            for (var subsList : subsLists) {
                query = queryMapper.getQuery(GET_GROUP_MEMBERS_LIST);
                query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedQuesMarks(subsList));
                int index=1;
                knLogger.debug(methodName, "query", query);
                pStmt = conn.prepareStatement(query);
                for (String mdn : subsList) {
                    pStmt.setString(index++, mdn);
                }
                pStmt.setInt(index, corpId);
                rs = pStmt.executeQuery();
                knLogger.debug(methodName, "Query executed successfully.");
                while (rs.next()) {
                    subsMap.put(rs.getString(1).trim(), rs.getInt(6));
                }
            }
            knLogger.debug(methodName, "subscribersList size", subsMap.size());
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to retrieve Subs info ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
        }
        return subsMap;
    }

    public Map<String, Integer> getGroupMembersClientType(Collection<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getGroupMembersClientType(Collection<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : mdnList ", mdnList.size());
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String query = null;
        Map<String, Integer> subsMap = new HashMap<>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            var mdnListArray = new ArrayList<>(mdnList);
            var subsLists = KnGeneralUtil.splitList(mdnListArray, BULK_UPDATE_SIZE);
            for (var subsList : subsLists) {
                query = queryMapper.getQuery(GET_POC_SUBSCRIBER_INFO);
                query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedQuesMarks(subsList));
                int index=1;
                knLogger.debug(methodName, "query", query);
                pStmt = conn.prepareStatement(query);
                for (String mdn : subsList) {
                    pStmt.setString(index++, mdn);
                }
                rs = pStmt.executeQuery();
                knLogger.debug(methodName, "Query executed successfully.");
                while (rs.next()) {
                    subsMap.put(rs.getString(2).trim(), rs.getInt(4));
                }
            }
            knLogger.debug(methodName, "subscribersList size", subsMap.size());
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to retrieve Subs info ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
        }
        return subsMap;
    }

    public String getAliasMdnProfile(KnIPSubscriberInfoDTO corpSubscriberDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getAliasMdnProfile()";
        knLogger.info(methodName, "ENTRY");
        knLogger.debug(methodName, "getAliasMdnProfile", corpSubscriberDTO);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        String mdn = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_ALIASID_PROFILE);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, corpSubscriberDTO.getAliasMdn());
            pstmt.setString(2, corpSubscriberDTO.getMdn());
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                mdn = rs.getString(1);
            }
            knLogger.debug(methodName, "EXit: Query executed successfully");
        } catch (Exception e) {
            throw KnDbUtil.processException(e, "Failed while getAliasMdnProfile",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        knLogger.debug(methodName);
        return mdn;
    }

    public KnCorpSubscriberDTO selectSubsInfo(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "selectSubsInfo(String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : mdn - ", KnGDPRTemplate.mdn(mdn));
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        KnCorpSubscriberDTO subscriberDTO = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_SUBSCRIBER_INFO);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, mdn);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            if (rs.next()) {
                subscriberDTO = new KnCorpSubscriberDTO();
                subscriberDTO.setCorpId(rs.getInt(1));
                subscriberDTO.setMdn(rs.getString(2).trim());
                subscriberDTO.setClientType(rs.getInt(3));
                subscriberDTO.setPocHome(rs.getString(5));
                subscriberDTO.setPresenceHome(rs.getString(6));
                String activeFs=rs.getString(8)!=null?rs.getString(8):KnGeneralUtil.convertLongToHexString(rs.getLong(4));
                subscriberDTO.setSubsActiveFS2(activeFs);
            }
        } catch (Exception e) {
            throw KnDbUtil.processException(e, "Failed while fetching the PoC subcribers info .",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        knLogger.debug(methodName, "inputMdn - ", KnGDPRTemplate.mdn(mdn), "EXIT : PoC subscribers", subscriberDTO);
        return subscriberDTO;
    }

    public void updateSubsEntities(KnIPSubscriberInfoDTO corpSubscriberDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateSubsEntities()";
        knLogger.info(methodName, "ENTRY");
        knLogger.debug(methodName, "updateSubsEntities", corpSubscriberDTO);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            int corpId = Integer.parseInt(corpSubscriberDTO.getCorpId());
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(UPDATE_SUBS_ENTITIES);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, corpSubscriberDTO.getUserId());
            pstmt.setString(2, corpSubscriberDTO.getAliasMdn());
            pstmt.setString(3, corpSubscriberDTO.getMdn());
            pstmt.setInt(4, corpId);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt.executeQuery();
            knLogger.debug(methodName, "EXit: Query executed successfully");
        } catch (Exception e) {
            throw KnDbUtil.processException(e, "Failed while updateSubsEntities",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
        knLogger.debug(methodName);
    }


    public Map<String, Integer> getSubscriberServiceAuthStatus(Collection<String> mdnList, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubscriberServiceAuthStatus(Collection<String>,)";
        knLogger.info(methodName, "ENTRY : mdnList - ", mdnList == null ? 0 : mdnList.size(), readOnly);

        Map<String, Integer> serviceAuthStatusMap = new HashMap<>();
        if (mdnList == null || mdnList.isEmpty()) {
            return serviceAuthStatusMap;
        }
        List<String> mdnListArray = new ArrayList<>(mdnList);
        List<List<String>> batchLists = KnGeneralUtil.splitList(mdnListArray, BULK_UPDATE_SIZE);
        Connection conn;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String query = null;

        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            for (List<String> batch : batchLists) {
                query = queryMapper.getQuery(GET_SUBS_SERVICE_AUTH_STATUS);
                query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedQuesMarks(batch));
                stmt = conn.prepareStatement(query);
                int index = 1;
                for (String mdn : batch) {
                    stmt.setString(index++, mdn);
                }
                rs = stmt.executeQuery();
                knLogger.debug(methodName, "Query executed successfully");
                while (rs.next()) {
                    serviceAuthStatusMap.put(rs.getString(MDN), rs.getInt(SERVICE_AUTH_STATUS));
                }
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while fetching subscriber service auth status - ", e);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occurred while fetching subscriber service auth status - ", e);
            throw KnDbUtil.processException(e, "Failed while fetching subscriber service auth status.",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            knLogger.debug(methodName, "EXIT : Returning service auth status for ", serviceAuthStatusMap.size(), " records");
        }

        return serviceAuthStatusMap;
    }


    public Map<String, KnCorpSubsEntitiesDTO> getSubsEntitiesDetails(List<String> mdnList,boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubsEntitiesDetails(String mdnList, KnPersisterTxn persisterTxn)";
        knLogger.info(methodName, "ENTRY : mdnList - ", mdnList == null ? mdnList : mdnList.size(),readOnly);
        Map<String, KnCorpSubsEntitiesDTO> subsEntitiesMap = new HashMap<>();
        Connection conn = null;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String query = null;

        KnCorpSubsEntitiesDTO corpSubsEntitiesDTO = null;
        Collection<Collection<String>> collList = KnDbUtil.getCollectionList(mdnList, BULK_UPDATE_SIZE);
        conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
        KnQueryMapper queryMapper = KnQueryMapper.getInstance();

        for (Collection<String> splitMdnList : collList) {
            try {
                //String finalQuery = KnCorpUtil.replaceContactWithValue(query, MDNLIST, formCommaSeperatedIdList(splitMdnList));
                //knLogger.debug(methodName, "Executing query - ", finalQuery);
                int index=1;
                query = queryMapper.getQuery(GET_SUBS_ENTITIES);
                query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedQuesMarks(splitMdnList));
                knLogger.debug(methodName, "query", query);
                pStmt = conn.prepareStatement(query);
                for(String mdn:splitMdnList){
                    pStmt.setString(index++, mdn);
                }
                rs = pStmt.executeQuery();
                knLogger.debug(methodName, "Query executed successfully");
                while (rs.next()) {
                    corpSubsEntitiesDTO = new KnCorpSubsEntitiesDTO();
                    corpSubsEntitiesDTO.setCorpId(rs.getInt(2));
                    corpSubsEntitiesDTO.setAliasMdn(rs.getString(3) != null ? rs.getString(3).trim() : null);
                    corpSubsEntitiesDTO.setUserId(rs.getString(4) != null ? rs.getString(4).trim() : null);
                    corpSubsEntitiesDTO.setActiveFs1(KnGeneralUtil.convertLongToHexString(rs.getLong(5)));
                    corpSubsEntitiesDTO.setClientType(rs.getInt(6));
                    corpSubsEntitiesDTO.setNetworkName(rs.getString(7));
                    String activeFs=rs.getString(8)!=null?rs.getString(8):KnGeneralUtil.convertLongToHexString(rs.getLong(5));
                    corpSubsEntitiesDTO.setMcpttCompliance(rs.getInt(9));
                    corpSubsEntitiesDTO.setActiveFs2(activeFs);
                    corpSubsEntitiesDTO.setCameraType(rs.getInt(10));
                    corpSubsEntitiesDTO.setClientPVmajorVer(rs.getInt(11));
                    subsEntitiesMap.put(rs.getString(1).trim(), corpSubsEntitiesDTO);
                }
            } catch (Exception e) {
                throw KnDbUtil.processException(e, "Failed to fetch subscriber UFMI details",
                        pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
            } finally {
                KnDbUtil.closeResultSet(rs);
                KnDbUtil.closeStatement(pStmt);
            }
        }
        knLogger.debug(methodName, "EXIT", KnGDPRTemplate.mapKeyMdn(subsEntitiesMap));
        return subsEntitiesMap;
    }

    public Map<String, String> getMdnByUsingAliasMdn(Collection<String> mdnList, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getMdnByUsingAliasMdn(Collection<String>,boolean KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : mdnList ", mdnList.size());
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String query = null;
        int index=1;
        Map<String, String> subsMap = new HashMap<>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_SUBS_ENTITIES_BY_ALIAS_MDN);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedQuesMarks(mdnList));
            knLogger.debug(methodName, "query", query);
                pStmt = conn.prepareStatement(query);
                for(String mdn:mdnList){
                    pStmt.setString(index++, mdn);
                }
                rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully.");
            while (rs.next()) {
                subsMap.put(rs.getString(3).trim(), rs.getString(1).trim());
            }
            knLogger.debug(methodName, "aliasMdn & Mdn Map size", subsMap.size());
        } catch (Exception e) {
            throw KnDbUtil.processException(e, "Failed to retrieve Subs info ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
        }
        return subsMap;
    }

    public Map<String, String> getMdnByUsingUserId(Collection<String> userIds, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getMdnByUsingUserId(Collection<String>,boolean KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : mdnList ", userIds.size());
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String query = null;
        int index=1;
        Map<String, String> subsMap = new HashMap<>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_SUBS_ENTITIES_BY_USER_ID);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            query = replaceContactWithValue(query, USERIDS, formCommaSeperatedQuesMarks(userIds));
            knLogger.debug(methodName, "query", query);
            pStmt = conn.prepareStatement(query);
            for(String mdn:userIds){
                pStmt.setString(index++, mdn);
            }
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully.");
            while (rs.next()) {
                subsMap.put(rs.getString(4).trim(), rs.getString(1).trim());
            }
            knLogger.debug(methodName, "userId & Mdn Map size", subsMap.size());
        } catch (Exception e) {
            throw KnDbUtil.processException(e, "Failed to retrieve Subs info ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
        }
        return subsMap;
    }

    public Map<String, KnCorpSubscriberDTO> getSubscriberAdditionalDetails(Collection<String> mdnList, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "getSubscriberAdditionalDetails(Collection<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :MdnList - ", mdnList == null ? mdnList : KnGDPRTemplate.mdnList(mdnList));
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String query = null;
        int index=1;
        Map<String, KnCorpSubscriberDTO> subscribersMap = new HashMap<String, KnCorpSubscriberDTO>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_POC_SUBSCRIBER_ADDL_DETAILS);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedQuesMarks(mdnList));
            knLogger.debug(methodName, "query", query);
                pStmt = conn.prepareStatement(query);
                for(String mdn:mdnList){
                    pStmt.setString(index++, mdn);
                }
                rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                KnCorpSubscriberDTO subscriberDTO = new KnCorpSubscriberDTO();
                String mdn = rs.getString(1).trim();
                subscriberDTO.setMdn(mdn);
                if(rs.getString(2) != null) subscriberDTO.setCommandPackageCode(rs.getString(2).trim());
                subscribersMap.put(mdn, subscriberDTO);
            }
            knLogger.debug(methodName, "Fetched map size is  :  ", subscribersMap.size());
            return subscribersMap;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while getting corp subscriber Addl details ",
                    "from the mdn list passed -", e);
            throw KnDbUtil.processException(e, "Failed while getting corp subscriber addl details from the mdn list passed" + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_ADDL_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            knLogger.debug(methodName, "EXIT :Fetched map size is  :", subscribersMap.size());
        }
    }
    /**
     * This is used to get the TGSS etag for subscribers.
     *
     * @param mdnList
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<String, Boolean> getTgssFeatureBit(Set<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getTgssFeatureBit(Set<String>, int)";
        knLogger.debug(methodName, "ENTRY : mdn",mdnList == null ? mdnList : KnGDPRTemplate.mdnList(mdnList));
        Map<String, Boolean> tgssMap = new HashMap<String, Boolean>();
        KnQueryMapper queryMapper = KnQueryMapper.getInstance();
        String query = queryMapper.getQuery(GET_MDNS_ACTIVEFS);
        knLogger.debug(methodName, "query :" + query);
        Collection<Collection<String>> compList = KnDbUtil.getCollectionList(new ArrayList<String>(mdnList), BULK_UPDATE_SIZE);
        for (Collection<String> mdnSplitList : compList) {
            tgssMap.putAll(getTgssMap(mdnSplitList, query, persisterTxn));
        }
        knLogger.debug(methodName, "EXIT", KnGDPRTemplate.mapKeyMdn(tgssMap));
        return tgssMap;
    }

    /**
     * This method will be called in a loop to get the TGSS etag for the subscribers.
     *
     * @param mdnSplitList
     * @param query
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    private Map<String, Boolean> getTgssMap(Collection<String> mdnSplitList, String query, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getTgssMap()";
        PreparedStatement pStmt = null;
        Connection conn;
        ResultSet rs = null;
        int index=1;
        conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
        Map<String, Boolean> tgssMap = new HashMap<String, Boolean>();
        knLogger.debug(methodName,"Entry:",mdnSplitList == null ? mdnSplitList : KnGDPRTemplate.mdnList(mdnSplitList));
        try {
            query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedQuesMarks(mdnSplitList));
            knLogger.debug(methodName, "query", query);
                pStmt = conn.prepareStatement(query);
                for(String mdn:mdnSplitList){
                    pStmt.setString(index++, mdn);
                }
                rs = pStmt.executeQuery();
            while (rs.next()) {
                String mdn = rs.getString(1).trim();
                String activeFs=rs.getString(3)!=null?rs.getString(3): KnGeneralUtil.convertBitSetToHexString(KnGeneralUtil.convertLongToBitSet(rs.getLong(2)));
                tgssMap.put(mdn, (KnGeneralUtil.getFeatureBitValue(activeFs,48)));
            }
            knLogger.debug(methodName, "tgssMap", KnGDPRTemplate.mapKeyMdn(tgssMap));

        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while getting subscribers simultainous session bit" + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
        }
        knLogger.debug(methodName,"EXIT:fetched tgss map size:",tgssMap.size());
        return tgssMap;
    }

    public void updateSubsProfileLastUpdateTime(KnCorpSubscriberDTO corpSubscriberDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateSubsProfileLastUpdateTime(KnCorpSubscriberDTO subsProvInfoDTO, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "corpSubscriberDTO", corpSubscriberDTO.toString());
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(UPDATE_SUBS_PROFILE_LAST_UPDATE_TIME);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            pstmt.setLong(1, corpSubscriberDTO.getLastProfileUpdateTime());
            pstmt.setString(2, corpSubscriberDTO.getMdn());
            pstmt.executeQuery();
            knLogger.debug(methodName, "Exit: Query executed successfully");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while updateSubsProfileLastUpdateTime",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
        knLogger.debug(methodName);
    }

    public void updateSubscriberMCSIds(KnSubsProfileDTO subscProfile, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "updateSubscriberMCSIds()";
        knLogger.info(methodName, "ENTRY");
        knLogger.debug(methodName, "KnSubsProfileDTO", subscProfile);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(UPDATE_MCS_IDS);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            int columnIndex = 0;
            pstmt.setBytes(++columnIndex, subscProfile.getMcId().getBytes(StandardCharsets.UTF_8));
            pstmt.setBytes(++columnIndex, subscProfile.getMcpttId().getBytes(StandardCharsets.UTF_8));
            pstmt.setBytes(++columnIndex, subscProfile.getMcVideoId().getBytes(StandardCharsets.UTF_8));
            pstmt.setBytes(++columnIndex, subscProfile.getMcDataId().getBytes(StandardCharsets.UTF_8));
            pstmt.setString(++columnIndex, subscProfile.getMdn());
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt.executeUpdate();
            knLogger.debug(methodName, "EXit: Query executed successfully");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while updateSubscriberMCSIds",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
        knLogger.debug(methodName);
    }

    /**
     *
     * @param userprofileIndexes
     * @param corpId
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<Integer, Integer> getUpIndexCountMap(Collection<Integer> userprofileIndexes, Integer corpId, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "getUpIndexCountMap()";
        knLogger.debug(methodName, "Entry ", corpId, userprofileIndexes);
        PreparedStatement pstmt = null;
        ResultSet result = null;
        String query = null;
        Map<Integer, Integer> countMap = new HashMap<>();
        Integer userProfileIndex = 0;
        Integer count = 0;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_MDNCOUNT_FOR_USERPROFILEINDEX);
            query = replaceContactWithValue(query, USERPROFILEINDEXES, formIntegerCommaSeperatedIdList(userprofileIndexes));
            knLogger.debug(methodName, "Executing GET_MDNCOUNT_FOR_USERPROFILEINDEX query -", query);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, corpId);
            result = pstmt.executeQuery();

            while (result.next()) {
                userProfileIndex = result.getInt(1);
                count = result.getInt(2);
                countMap.put(userProfileIndex, count);
            }
            knLogger.debug(methodName, "EXIT: Query executed successfully", countMap);
            return countMap;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getUpIndexCountMap  - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "SQLException occured while getUpIndexCountMap - ", e);
            throw KnDbUtil.processException(e, "Failed while getUpIndexCountMap  " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_USERPROFILE, query);
        } finally {
            KnDbUtil.closeResultSet(result);
            KnDbUtil.closeStatement(pstmt);
            knLogger.debug(methodName, "userprofileIndexes -  ", userprofileIndexes, " , corpId - ", corpId, "countMap found :  ", countMap);

        }
    }


    public Map<Integer, Integer> getSubscriberUserProfileList(String mcId,int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubscriberUserProfileList(String,int, KnPersisterTxn)";
        knLogger.debug(methodName, "mcId", KnGDPRTemplate.mcId(mcId), "corpId", corpId);
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        Collection<KnCorpUserProfileDTO> subscribersList = new ArrayList<KnCorpUserProfileDTO>();
        Map<Integer, Integer> subscribersMap = new HashMap<Integer, Integer>();

        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_SUBSCRIBER_USER_PROFILE_LIST);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setBytes(1, mcId.getBytes(StandardCharsets.UTF_8));
            pstmt.setInt(2, corpId);
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                Integer userProfileIndex = rs.getInt(1);
                Integer isDefaultProfile = rs.getInt(2);
                subscribersMap.put(userProfileIndex, isDefaultProfile);
            }
            return subscribersMap;

        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getSubscriberUserProfileList", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while getSubscriberUserProfileList", e);
            throw KnDbUtil.processException(e, "Failed while fetching the PoC subcribers List ." + e, pttServerId,
                    KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            knLogger.debug(methodName, "EXIT : PoC subscribers list returned ", subscribersList.toString());
        }

        }
    public Map<String,Integer> getSubscriberUserProfileList(String mcId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubscriberUserProfileList(String, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "mcId", KnGDPRTemplate.mcId(mcId));
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        Map<String,Integer> result = new HashMap<>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_SUBSCRIBER_USER_PROFILE_LIST_WITHOUT_CORPID);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            pstmt = conn.prepareStatement(query);
            pstmt.setBytes(1, mcId.getBytes(StandardCharsets.UTF_8));
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                result.put(rs.getString(1),rs.getInt(2));
            }
            return result;

        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getSubscriberUserProfileList", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while getSubscriberUserProfileList", e);
            throw KnDbUtil.processException(e, "Failed while fetching the PoC ids List ." + e, pttServerId,
                    KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            knLogger.debug(methodName, "EXIT : PoC userprofileid list returned ", result);
        }

        }

    public List<String> getProfileMdnByBaseMdn(String baseMdn, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "getProfileMdnByBaseMdn(String, KnPersisterTxn)";
        knLogger.debug(methodName, "baseMdn", KnGDPRTemplate.mdn(baseMdn));
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        boolean ownedTxn = false;
        List<String> profileMdnList=new ArrayList<>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(PROFILE_MDN_COUNT_BY_BASE_MDN);
            if (persisterTxn != null) {
                knLogger.debug(methodName, " Data Store before - ", KnDBConst.DataStores.XDM_SHARED_DATA);
                conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            } else {
                conn = KnDBManager.getDBManagerInstance().getConnection(KnGenInfoUtil.getSecDBPttServerId(pttServerId), true);
                ownedTxn = true;
            }
            //conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, baseMdn);
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            rs = pstmt.executeQuery();
            while (rs.next()) {
                profileMdnList.add(rs.getString("MDN").trim());
            }
            return profileMdnList;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured", e);
            throw KnDbUtil.processException(e, "Failed while fetching profile mdn count ." + e, pttServerId,
                    KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            if (ownedTxn) {
                KnDbUtil.closeConnection(conn);
            }
            knLogger.debug(methodName, "EXIT : profile mdn count", KnGDPRTemplate.mdnList(profileMdnList));
        }
    }

    public List<String> getProfileMdnByBaseMdns(List<String> baseMdns, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "getProfileMdnByBaseMdns(List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "baseMdns ", baseMdns == null ? baseMdns : KnGDPRTemplate.mdnList(baseMdns));
        List<String> profileMdnList = new ArrayList<>();
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        boolean ownedTxn = false;
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();

            Collection<Collection<String>> collList = KnDbUtil.getCollectionList(baseMdns, BULK_UPDATE_SIZE);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            for (Collection<String> splitMdnList : collList) {
                int index = 1;
                query = queryMapper.getQuery(GET_PROFILE_MDN_BY_BASE_MDN);
                query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedQuesMarks(splitMdnList));
                knLogger.debug(methodName, "query", query);
                pstmt = conn.prepareStatement(query);
                for (String mdn : splitMdnList) {
                    pstmt.setString(index++, mdn);
                }
                rs = pstmt.executeQuery();
                knLogger.debug(methodName, "Query executed successfully");
                while (rs.next()) {
                    profileMdnList.add(rs.getString("MDN").trim());
                }
            }
            if (ownedTxn) {
                knLogger.debug(methodName, "saving the transaction ");
                persisterTxn.save();
            }
            return profileMdnList;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured", e);
            throw e;
        } catch (Exception e) {
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            knLogger.error(methodName, "Unexpected Exception occured", e);
            throw KnDbUtil.processException(e, "Failed while fetching profile mdn count ." + e, pttServerId,
                    KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            knLogger.debug(methodName, "EXIT : profile mdn count", KnGDPRTemplate.mdnList(profileMdnList));
        }
    }

    /**
     *
     * @param corpId
     * @param userProfileId
     * @param startIndex
     * @param fetchSize
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Collection<KnMDNInfoDto> getUserProfileSubscriberList(int corpId,String userProfileId,int startIndex,int fetchSize, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getUserProfileSubscriberList(int, String, String, String, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "userProfileId", userProfileId, "corpId", corpId);
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        List<KnMDNInfoDto> subscribersList = new ArrayList<>();
        int lastIndex = startIndex+fetchSize;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            if(startIndex>0 && fetchSize >0){
                query = queryMapper.getQuery(GET_USER_PROFILE_SUBSCRIBER_LIST_PAGINATED);
                pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, startIndex);
                pstmt.setInt(2, lastIndex);
                pstmt.setString(3, userProfileId);
                pstmt.setInt(4, corpId);
            }else {
                query = queryMapper.getQuery(GET_USER_PROFILE_SUBSCRIBER_LIST);
                pstmt = conn.prepareStatement(query);
                pstmt.setString(1, userProfileId);
                pstmt.setInt(2, corpId);
            }
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                KnMDNInfoDto mdnInfoDto = new KnMDNInfoDto();
                mdnInfoDto.setMdn(rs.getString(1).trim());
                mdnInfoDto.setName(rs.getString(2));
                subscribersList.add(mdnInfoDto);

            }
            return subscribersList;

        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while getUserProfileSubscriberList", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while getUserProfileSubscriberList", e);
            throw KnDbUtil.processException(e, "Failed while fetching the PoC subcribers List ." + e, pttServerId,
                    KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.debug(methodName, "EXIT : PoC subscribers list returned for given user profile Id", subscribersList.toString());
        }

    }

    public List<String> getProfileMdnByUPId(String corpId, String userProfileId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getProfileMdnByUPId(String,String,boolean,KnPersisterTxn)";
        knLogger.debug(methodName, "userProfileId ", userProfileId,"corpId ",corpId);
        Connection conn = null;
        boolean ownedTxn = false;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        List<String> subscribersList = new ArrayList<>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            if (persisterTxn != null) {
                conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            } else {
                conn = KnDBManager.getDBManagerInstance().getConnection(KnGenInfoUtil.getSecDBPttServerId(pttServerId), true);
                ownedTxn = true;
            }
            query = queryMapper.getQuery(GET_PROFILE_MDN_BY_UP_ID);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, userProfileId);
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                subscribersList.add(rs.getString(1).trim());
            }
            return subscribersList;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured", e);
            throw KnDbUtil.processException(e, "Failed while fetching the PoC subcribers List ." + e, pttServerId,
                    KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            if (ownedTxn) {
                KnDbUtil.closeConnection(conn);
            }
            knLogger.debug(methodName, "EXIT : PoC subscribers list returned for given user profile Id", KnGDPRTemplate.mdnList(subscribersList));
        }
    }

    /**
     *
     * @param corpId
     * @param profileId
     * @param mdns
     * @param defaultProfile
     * @param persisterTxn
     * @throws KnDAOException
     */

    public void updateDefaultProfile(String corpId, String profileId, List<String>mdns, int defaultProfile, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateDefaultProfile(int,String,String,String KnPersisterTxn)";
        knLogger.debug(methodName, "userProfileId", profileId, "corpId", corpId);
        Connection conn = null;
        PreparedStatement pstmt = null;
        int cnt = 0;
        String query = null;
        int index=2;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(UPDATE_DEFAULT_USERPROFILE);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = replaceContactWithValue(query, MDNS, formCommaSeperatedQuesMarks(mdns));
            knLogger.debug(methodName, "query", query);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, defaultProfile);
            for(String mdn:mdns){
                pstmt.setString(index++, mdn);
            }
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            cnt = pstmt.executeUpdate();
            knLogger.debug(methodName, "Query executed successfully", cnt);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while getUserProfileSubscriberList", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while updateDefaultProfile", e);
            throw KnDbUtil.processException(e, "Failed while updateDefaultProfile in PoC subscribers List ." + e, pttServerId,
                    KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.debug(methodName, "EXIT : updateDefaultProfile for given input", profileId, "mdns", KnGDPRTemplate.mdnList(mdns));
        }
    }

    /**
     *
     * @param mdn
     * @param isDefaultProfile
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public String getDefaultProfileMdnByMdn(String mdn, int isDefaultProfile, KnPersisterTxn persisterTxn) throws KnDAOException{
        String methodName = "getDefaultProfileMdnByMdn(String,int,KnPersisterTxn)";
        knLogger.debug(methodName, "mdn ", KnGDPRTemplate.mdn(mdn));
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        String defaultProfileMdn = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = queryMapper.getQuery(GET_DEFAULT_PROFILEMDN_BYMDN);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, mdn);
            pstmt.setInt(2,isDefaultProfile);

            knLogger.debug(methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                defaultProfileMdn = rs.getString(1).trim();
            }
            return defaultProfileMdn;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured", e);
            throw KnDbUtil.processException(e, "Failed while fetching the Default profile MDN ." + e, pttServerId,
                    KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.debug(methodName, "EXIT : Default profile MDN for given Input", KnGDPRTemplate.mdn(defaultProfileMdn));
        }

    }

    /**
     *
     * @param profileId
     * @param mdn
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public String getProfileMdnByMdnUPID(String profileId,String mdn, KnPersisterTxn persisterTxn) throws KnDAOException{
        String methodName = "getDefaultProfileMdn(String,String,KnPersisterTxn)";
        knLogger.debug(methodName, "profileId - ", profileId,"mdn -",KnGDPRTemplate.mdn(mdn));
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        String defaultProfileMdn = null;
        boolean ownedTxn = false;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            if (persisterTxn != null) {
                knLogger.debug(methodName, " Data Store before - ", KnDBConst.DataStores.XDM_SHARED_DATA);
                conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            } else {
                conn = KnDBManager.getDBManagerInstance().getConnection(KnGenInfoUtil.getSecDBPttServerId(pttServerId), true);
                ownedTxn = true;
            }
            //conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = queryMapper.getQuery(GET_PROFILEMDN_BY_MDNUPID);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, profileId);
            pstmt.setString(2, mdn);
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                defaultProfileMdn = rs.getString(1).trim();
            }
            return defaultProfileMdn;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured", e);
            throw KnDbUtil.processException(e, "Failed while fetching the Default profile MDN ." + e, pttServerId,
                    KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            if (ownedTxn) {
                KnDbUtil.closeConnection(conn);
            }
            knLogger.debug(methodName, "EXIT : Default profile MDN for given Input", KnGDPRTemplate.mdn(defaultProfileMdn));
        }

    }

    public Set<String> getUniqueMcpttIds(Collection<String> mdns, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getUniqueMcpttIds(Collection<String>,KnPersisterTxn)";
        knLogger.debug(methodName, "mdns ", mdns == null ? mdns : KnGDPRTemplate.mdnList(mdns));
        knLogger.info(methodName, "mdns size", mdns == null ? 0 : mdns.size());
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        Set<String> uniqueMcpttIds = new HashSet<String>();
        try {
            if(mdns == null)
            {
                knLogger.warn(methodName, "mdns is null, returning empty set");
                return uniqueMcpttIds;
            }
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            Collection<Collection<String>> collList = KnDbUtil.getCollectionList((List<String>) mdns, BULK_UPDATE_SIZE);
            for (Collection<String> splitMdnList : collList) {
                int index = 1;
                query = queryMapper.getQuery(GET_UNIQUE_MCPTTIDS_BY_MDNS);
                query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedQuesMarks(splitMdnList));
                knLogger.debug(methodName, "query", query);
                pstmt = conn.prepareStatement(query);
                for (String mdn : splitMdnList) {
                    pstmt.setString(index++, mdn);
                }
                rs = pstmt.executeQuery();
                while (rs.next()) {
                    if (null != rs.getBytes(1)) {
                        uniqueMcpttIds.add(new String(rs.getBytes(1), StandardCharsets.UTF_8));
                    }
                }
                knLogger.info(methodName, "splitMdnList.size()", splitMdnList.size(), "index", index);
            }
            return uniqueMcpttIds;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured", e);
            throw KnDbUtil.processException(e, "Failed while fetching the PoC subcribers List ." + e, pttServerId,
                    KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.debug(methodName, "EXIT : getUniqueMcpttIds", KnGDPRTemplate.mcPttIdSetList(uniqueMcpttIds));
        }
    }

    public Map<String,String> getProfileMdnsByCorpId(String corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getProfileMdnsByCorpId()";
        knLogger.debug(methodName, "corpId ",corpId);
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        Map<String,String> profileMdnMap = new HashMap<String, String>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = queryMapper.getQuery(GET_PROFILE_MDN_BY_CORP_ID);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, Integer.parseInt(corpId));
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                profileMdnMap.put(rs.getString(1) !=null ? rs.getString(1).trim() : rs.getString(1), rs.getString(2) != null ? rs.getString(2).trim() : rs.getString(2));
            }
            return profileMdnMap;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured", e);
            throw KnDbUtil.processException(e, "Failed while fetching ProfileMdnsByCorpId " + e, pttServerId,
                    KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.debug(methodName, "EXIT : ", KnGDPRTemplate.mdnMap(profileMdnMap));
        }
    }

    /**
     *
     * @param corpId
     * @param userProfileId
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public List<KnCorpSubscriberDTO> getProfileMdnInfoByUPId(String corpId,String userProfileId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getProfileMdnInfoByUPId(String,String,KnPersisterTxn)";
        knLogger.debug(methodName, "userProfileId ", userProfileId,"corpId ",corpId);
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        List<KnCorpSubscriberDTO> subscribersList = new ArrayList<>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = queryMapper.getQuery(GET_PROFILE_MDNINFO_BY_UP_ID);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, userProfileId);
            pstmt.setInt(2, Integer.parseInt(corpId));
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                KnCorpSubscriberDTO subscriberDTO = new KnCorpSubscriberDTO();
                subscriberDTO.setMdn(rs.getString(1).trim());
                String subseFS=rs.getString(3)!=null?rs.getString(3): KnGeneralUtil.convertLongToHexString(rs.getLong(2));
                subscriberDTO.setSubscriberFs2(subseFS);
                String activeFS=rs.getString(5)!=null?rs.getString(5): KnGeneralUtil.convertLongToHexString(rs.getLong(4));
                subscriberDTO.setSubsActiveFS2(activeFS);
                subscribersList.add(subscriberDTO);
            }
            return subscribersList;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured", e);
            throw KnDbUtil.processException(e, "Failed while fetching the PoC subcribers List ." + e, pttServerId,
                    KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.debug(methodName, "EXIT : PoC subscribers list returned for given user profile Id", subscribersList);
        }
    }

    public Set<String> getBaseMdnByProfileMdns(List<String> profileMdns, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "getBaseMdnByProfileMdns(List<String>,boolean,KnPersisterTxn)";
        knLogger.debug(methodName, "profileMdns ", profileMdns == null ? profileMdns : KnGDPRTemplate.mdnList(profileMdns));
        Set<String> baseMdnList=new HashSet<>();
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;

        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();

            Collection<Collection<String>> collList = KnDbUtil.getCollectionList(profileMdns, BULK_UPDATE_SIZE);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            for (Collection<String> splitMdnList : collList) {
                int index=1;
                query = queryMapper.getQuery(GET_BASE_MDN_BY_PROFILE_MDN);
                query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedQuesMarks(splitMdnList));
                knLogger.debug(methodName, "query", query);
                pstmt = conn.prepareStatement(query);
                for(String mdn:splitMdnList){
                    pstmt.setString(index++, mdn);
                }
                rs = pstmt.executeQuery();
                knLogger.debug(methodName, "Query executed successfully");
                while (rs.next()) {
                    baseMdnList.add(rs.getString("MDN").trim());
                }
            }
            return baseMdnList;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured", e);
            throw KnDbUtil.processException(e, "Failed while fetching " + e, pttServerId,
                    KnDAOSourceTypes.XDM_POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            knLogger.debug(methodName,"baseMdnList :",KnGDPRTemplate.mdnList(baseMdnList));
        }
    }

    public Map<String, String> getProfileMdnBaseMdnMap(List<String> profileMdns, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "getProfileMdnBaseMdnMap(List<String>,boolean,KnPersisterTxn)";
        knLogger.debug(methodName, "profileMdns ", profileMdns == null ? profileMdns : KnGDPRTemplate.mdnList(profileMdns));
        Map<String,String> baseMdnProfileMdnMap=new HashMap<String, String>();
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_BASEMDN_PROFILEMDN_MAP);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            pstmt = conn.prepareStatement(query);
            for (String profileMdn : profileMdns) {
                pstmt.setString(1, profileMdn);
                rs = pstmt.executeQuery();
                knLogger.debug(methodName, "Query executed successfully");
                while (rs.next()) {
                    baseMdnProfileMdnMap.put(profileMdn, rs.getString(1).trim());
                }
            }
            return baseMdnProfileMdnMap;
        }catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured", e);
            throw KnDbUtil.processException(e, "Failed while fetching " + e, pttServerId,
                    KnDAOSourceTypes.XDM_POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            knLogger.debug(methodName,"baseMdnProfileMdnMap :",KnGDPRTemplate.mdnMap(baseMdnProfileMdnMap));
        }
    }

    public void updateProfileMdnDetails(KnCorpSubscriberDTO persistenceDTO
            , Map<String, String> mdnActivsFsMap,  Map<String, KnCorpSubscriberDTO> mdnUpmFsMap, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "updateProfileMdnDetails()";
        Connection conn;
        PreparedStatement pStmt = null;
        String query = null;
        try {
            KnCorpSubscriberDTO subsInfoPersistDTO =  persistenceDTO;
            knLogger.debug(methodName," subsInfoPersistDTO :",subsInfoPersistDTO.toString() ," mdnActivsFsMap :", mdnActivsFsMap == null ? mdnActivsFsMap: KnGDPRTemplate.mdnMap(mdnActivsFsMap));
            int serviceAuthStatus = subsInfoPersistDTO.getServiceAuthStatus();
            String imei = subsInfoPersistDTO.getIMEI();
            String clientPassword = subsInfoPersistDTO.getClientPassword();
            long lastProfileUpdateTime = subsInfoPersistDTO.getLastProfileUpdateTime();
            String userAgent = subsInfoPersistDTO.getUserAgent();
            int pvMajorVersion = subsInfoPersistDTO.getClientPVmajorVer();
            int pvMinorVersion = subsInfoPersistDTO.getClientPVminorVer();
            int clientSWType=subsInfoPersistDTO.getSwType();
            int clientPlatformType=subsInfoPersistDTO.getPlatformType();
            int dynamicQosFlag=subsInfoPersistDTO.getDynamicQosFlag();
            Long lastActivationTime = subsInfoPersistDTO.getLastActivationTime();

            //vocoderId for PV=9
            int vocoderId = subsInfoPersistDTO.getVocoderId();

            String derivedKey = subsInfoPersistDTO.getDerivedKey();

            int serviceAuthStatusOP = subsInfoPersistDTO.getServiceStatusOp();

            int licenseType = subsInfoPersistDTO.getLicenseType();

            StringBuilder strBuffer = new StringBuilder();
            strBuffer.append(UPDATE_QRY);
            strBuffer.append(SERVICE_AUTH_STATUS).append("=?");
            strBuffer.append(", ");
            strBuffer.append(LAST_PROFILE_UPDATE_TIME).append("=?");
            if (imei != null) {
                strBuffer.append(", ");
                strBuffer.append(IMEI).append("=?");
            }
            if (clientPassword != null) {
                strBuffer.append(", ");
                strBuffer.append(CLIENT_PASSWORD).append("=?");
            }
            if (userAgent != null) {
                strBuffer.append(", ");
                strBuffer.append(USER_AGENT).append("=?");
            }
            if (vocoderId != 0){
                strBuffer.append(", ");
                strBuffer.append(VOCODERID).append("=?");
            }
            strBuffer.append(", ");
            strBuffer.append(CLIENTPV_MAJORVERSION).append("=?");
            strBuffer.append(", ");
            strBuffer.append(CLIENTPV_MINORVERSION).append("=?");
            if(lastActivationTime!=null)
            {
            strBuffer.append(" , ");
            strBuffer.append(LAST_ACTIVATION_TIME).append("=?");
            }

            strBuffer.append(", ");
            strBuffer.append(CLIENT_SW_INF).append("=?");
            strBuffer.append(" , ");
            strBuffer.append(CLIENT_PLATFORM_TYPE).append("=?");
            strBuffer.append(" , ");
            strBuffer.append(DYNAMIC_QOS_FLAG).append("=?");
            if (derivedKey != null) {
                strBuffer.append(", ");
                strBuffer.append(DERIVEDKEY).append("=?");
            }
            strBuffer.append(", ");
            strBuffer.append(SERVICE_STATUS_OP).append("=?");
            strBuffer.append(", ");
            strBuffer.append(CLIENT_TYPE).append("=?");
            strBuffer.append(", ");
            strBuffer.append(SUBS_FS1).append("=?");
            strBuffer.append(", ");
            strBuffer.append(CLIENT_FS1).append("=?");
            strBuffer.append(", ");
            strBuffer.append(ACTIVE_FS1).append("=?");
            strBuffer.append(", ");
            strBuffer.append(OPS_FS1).append("=?");
            strBuffer.append(", ");
            strBuffer.append(CORPADMIN_FS1).append("=?");
            strBuffer.append(", ");
            strBuffer.append(SUBS_FS2).append("=?");
            strBuffer.append(", ");
            strBuffer.append(CLIENT_FS2).append("=?");
            strBuffer.append(", ");
            strBuffer.append(ACTIVE_FS2).append("=?");
            strBuffer.append(", ");
            strBuffer.append(OPS_FS2).append("=?");
            strBuffer.append(", ");
            strBuffer.append(CORPADMIN_FS2).append("=?");

            if (licenseType != 0){
                strBuffer.append(", ").append(LICENSE_TYPE).append("=?");
            }

            //Privacy Opt Status changes end
            strBuffer.append(" WHERE ").append(MDN).append("=?");

            query = strBuffer.toString();
            knLogger.debug(methodName," query :",query);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);
            int columnIndex = 0;
            for(Map.Entry mdnInfo:mdnActivsFsMap.entrySet()){
                pStmt.setInt(++columnIndex, serviceAuthStatus);
                pStmt.setLong(++columnIndex, lastProfileUpdateTime);
                if (imei != null) {
                    pStmt.setString(++columnIndex, imei);
                }
                if (clientPassword != null) {
                    pStmt.setString(++columnIndex, clientPassword);
                }
                if (userAgent != null) {
                    pStmt.setString(++columnIndex, userAgent);
                }
                if (vocoderId != 0){
                    pStmt.setInt(++columnIndex, vocoderId);
                }
                pStmt.setInt(++columnIndex, pvMajorVersion);
                pStmt.setInt(++columnIndex, pvMinorVersion);
                if(lastActivationTime!=null)
                {
                pStmt.setLong(++columnIndex, lastActivationTime);
                }
                pStmt.setInt(++columnIndex, clientSWType);
                pStmt.setInt(++columnIndex, clientPlatformType);

                pStmt.setInt(++columnIndex, dynamicQosFlag);
                if (derivedKey != null) {
                    pStmt.setString(++columnIndex, derivedKey);
                }
                String corpAdminFS = subsInfoPersistDTO.getCorpAdminFS2();
                if (mdnUpmFsMap.get(mdnInfo.getKey().toString()) != null && mdnUpmFsMap.get(mdnInfo.getKey().toString()).getCorpAdminFS2() != null) {
                    corpAdminFS = mdnUpmFsMap.get(mdnInfo.getKey().toString()).getCorpAdminFS2();
                }
                pStmt.setInt(++columnIndex, serviceAuthStatusOP);
                pStmt.setInt(++columnIndex, subsInfoPersistDTO.getClientType());
                pStmt.setLong(++columnIndex, KnGeneralUtil.convertHexStringToLong(subsInfoPersistDTO.getSubscriberFs2()));
                pStmt.setLong(++columnIndex, KnGeneralUtil.convertHexStringToLong(subsInfoPersistDTO.getClientFs2()));
                pStmt.setLong(++columnIndex, KnGeneralUtil.convertHexStringToLong(mdnInfo.getValue().toString()));
                pStmt.setLong(++columnIndex, KnGeneralUtil.convertHexStringToLong(subsInfoPersistDTO.getOpsFs2()));
                pStmt.setLong(++columnIndex, KnGeneralUtil.convertHexStringToLong(corpAdminFS));
                pStmt.setString(++columnIndex, KnGeneralUtil.getFeatureSet(subsInfoPersistDTO.getSubscriberFs2()));
                pStmt.setString(++columnIndex, KnGeneralUtil.getFeatureSet(subsInfoPersistDTO.getClientFs2()));
                pStmt.setString(++columnIndex, KnGeneralUtil.getFeatureSet(mdnInfo.getValue().toString()));
                pStmt.setString(++columnIndex, KnGeneralUtil.getFeatureSet(subsInfoPersistDTO.getOpsFs2()));
                pStmt.setString(++columnIndex, KnGeneralUtil.getFeatureSet(corpAdminFS));
                if (licenseType != 0){
                    pStmt.setInt(++columnIndex, licenseType);
                }

                pStmt.setString(++columnIndex, mdnInfo.getKey().toString());
                knLogger.info(methodName, "pStmt- ", pStmt);
                pStmt.addBatch();
                columnIndex = 0;
            }
            pStmt.executeBatch();
            knLogger.debug(methodName, "QUERY: Executed - ");

        } catch (SQLException sqlE) {
            throw KnDbUtil.processException(sqlE, "Failed to updateProfileMdnDetails - " + sqlE.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCSUBSCRINFO, query);
        } catch (Exception e) {
            throw KnDbUtil.processException(e, "Failed to updateProfileMdnDetails - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeStatement(pStmt);
        }
        knLogger.exit(methodName);
    }

    public Map<String,KnCorpSubscriberDTO> getProfileMdnAndUpmfsByBaseMdn(String baseMdn,KnPersisterTxn persistTxn)
            throws KnDAOException {
        String methodName = "getProfileMdnAndUpmfsByBaseMdn(String, KnPersisterTxn)";
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String query = null;
        String mdn=null;
        Map<String,KnCorpSubscriberDTO> mdnNupmfsMap=new HashMap<>();

        knLogger.debug(methodName, "ENTRY: getMdnForUPM");
        try {
            query = GET_MDN_FOR_UPM;

            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pStmt = conn.prepareStatement(query);
            pStmt.setString(1, baseMdn);
            knLogger.debug(methodName, "Query: Executing - ", query);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Query: Executed ");
            while (rs.next()) {
                KnCorpSubscriberDTO knCorpSubscriberDTO = new KnCorpSubscriberDTO();
                knCorpSubscriberDTO.setUserProfileFS2(rs.getString(USERPROFILEFS2));
                knCorpSubscriberDTO.setXdmsFs2(rs.getString(XDMS_FS2));
                knCorpSubscriberDTO.setOldActiveFs(rs.getString(ACTIVE_FS2));
                knCorpSubscriberDTO.setCorpAdminFS2(rs.getString(CORPADMIN_FS2));
                mdnNupmfsMap.put(rs.getString(1).trim(),knCorpSubscriberDTO);
            }
        } catch (KnDAOException dbConne) {
            knLogger.error(methodName, "DAO Exception occurred");

            throw dbConne;
        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQL Exception occurred");

            throw KnDbUtil.processException(sqlE, "Failed to getProfileMdnAndUpmfsByBaseMdn - " + sqlE.getMessage(), pttServerId, KnDAOSourceTypes.XDM_POCSUBSCRINFO, query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);

            throw KnDbUtil.processException(e, "Failed to getProfileMdnAndUpmfsByBaseMdn - " + e.getMessage(), pttServerId, KnDAOSourceTypes.XDM_POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStmt);
            knLogger.debug(methodName, "EXIT : getProfileMdnAndUpmfsByBaseMdn mdnNupmfsMap:",KnGDPRTemplate.mapKeyMdn(mdnNupmfsMap));
        }
        return mdnNupmfsMap;
    }

    public Map<String,String> getMdnMcpttIdMap(List<String> mdnList, KnPersisterTxn
            persisterTxn) throws KnDAOException {
        String methodName = "getMdnMcpttIdMap()";
        knLogger.debug(methodName, "ENTRY : mdnList =",mdnList.size());
        Connection conn = null;
        boolean ownedTxn = false;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        int index=1;
        Map<String,String> mdnMcpttMap = new HashMap<>();
        try {
            if(mdnList!=null&&!mdnList.isEmpty()){
                KnQueryMapper queryMapper = KnQueryMapper.getInstance();
                query = queryMapper.getQuery(GET_MDN_MCPPTT_MAP);
                if (persisterTxn != null) {
                    conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
                } else {
                    conn = KnDBManager.getDBManagerInstance().getConnection(KnGenInfoUtil.getSecDBPttServerId(pttServerId), true);
                    ownedTxn = true;
                }
                query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedQuesMarks(mdnList));
                knLogger.debug(methodName, "query", query);
                pstmt = conn.prepareStatement(query);
                for(String mdn:mdnList){
                    pstmt.setString(index++, mdn);
                }
                rs = pstmt.executeQuery();
                while (rs.next()) {
                    mdnMcpttMap.put(rs.getString(1).trim(),new String(rs.getBytes(2), StandardCharsets.UTF_8));
                }
            }
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while getting " + e,
                    pttServerId, KnDAOSourceTypes.SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            if (ownedTxn) {
                KnDbUtil.closeConnection(conn);
            }
        }
        knLogger.debug(methodName, "EXIT ",mdnMcpttMap.size());
        return mdnMcpttMap;
    }

    public Set<String> getMDNListByFanIds(Set<Integer> fanIds, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getMDNListByFanIds()";
        knLogger.debug( methodName, "Entry : fanIds - " , fanIds);
        Statement stmt = null;
        ResultSet rs = null;
        String MDNS_BY_FANIDS = "SELECT MDN FROM DG.SUBSCRIBER_ADDLINFO WHERE FAN_ID IN (FANLIST);";
        Set<String> mdnList = new HashSet<>();
        try {
            if(fanIds!=null){
                Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
                MDNS_BY_FANIDS = replaceContactWithValue(MDNS_BY_FANIDS, "FANLIST", formIntegerCommaSeperatedIdList(fanIds));
                stmt = conn.createStatement();
                knLogger.debug( methodName, "Executing query - " ,MDNS_BY_FANIDS);
                rs = stmt.executeQuery(MDNS_BY_FANIDS);
                while (rs.next()) {
                    mdnList.add(rs.getString(1).trim());
                }
            }
            knLogger.debug( methodName, "mdnList- " , mdnList );
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while getFanDetails -",
                    pttServerId, "SUBSCRIBER_ADDLINFO", MDNS_BY_FANIDS);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
        }
        return mdnList;
    }

    public List<String> getOwnCorpRegroupMembers(int corpId, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "getOwnCorpRegroupMembers(KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : corpId ", corpId);

        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String query = null;
        final int GROUP_REGROUP=111;
        List<String> mdnList=new ArrayList<>();

        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_PRECONFIG_GROUP_CORP_MEMBER);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pStmt = conn.prepareStatement(query);
            pStmt.setInt(1, corpId);
            knLogger.debug(methodName, "Query: Executing - ", query);
            rs = pStmt.executeQuery();
            while (rs.next()) {
                String activeFS=rs.getString(2)!=null?rs.getString(2):KnGeneralUtil.convertLongToHexString(rs.getLong(3));
                boolean groupRegroupEnabled = KnGeneralUtil.getFeatureBitValue(activeFS, GROUP_REGROUP);
                if(groupRegroupEnabled){
                    mdnList.add(rs.getString(1).trim());
                }
            }
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, " query failed ",
                    pttServerId, "POCSUBSCRINFO", GET_PRECONFIG_GROUP_CORP_MEMBER);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
        }
        knLogger.debug(methodName," EXIT :",mdnList);
        return mdnList;
    }

    /**
     *
     * @param userProfileId
     * @param startIndex
     * @param fetchSize
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Collection<KnMDNInfoDto> getUserProfileAllSubscriberList(String userProfileId, int startIndex, int fetchSize, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getUserProfileAllSubscriberList(int, String, String, String, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "userProfileId", userProfileId);
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        List<KnMDNInfoDto> subscribersList = new ArrayList<>();
        int lastIndex = startIndex+fetchSize;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            if(startIndex>0 && fetchSize >0){
                query = queryMapper.getQuery(GET_ALL_USER_PROFILE_SUBSCRIBER_LIST_PAGINATED);
                pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, startIndex);
                pstmt.setInt(2, lastIndex);
                pstmt.setString(3, userProfileId);

            }else {
                query = queryMapper.getQuery(GET_ALL_USER_PROFILE_SUBSCRIBER_LIST);
                pstmt = conn.prepareStatement(query);
                pstmt.setString(1, userProfileId);

            }
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                KnMDNInfoDto mdnInfoDto = new KnMDNInfoDto();
                mdnInfoDto.setMdn(rs.getString(1).trim());
                mdnInfoDto.setName(rs.getString(2));
                mdnInfoDto.setCorpID(rs.getInt(3));
                subscribersList.add(mdnInfoDto);

            }
            return subscribersList;

        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while getUserProfileSubscriberList", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while getUserProfileSubscriberList", e);
            throw KnDbUtil.processException(e, "Failed while fetching the PoC subcribers List ." + e, pttServerId,
                    KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.debug(methodName, "EXIT : PoC subscribers list returned for given user profile Id", subscribersList.toString());
        }

    }

    /**
     *
     * @param userProfileId
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public List<KnCorpContactDTO> getUserProfileAllSubscriberListDetails(String userProfileId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getUserProfileAllSubscriberListDetails(int,String,String,String KnPersisterTxn)";
        knLogger.info(methodName, "userProfileId", userProfileId);
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        List<KnCorpContactDTO> subscribersList = new ArrayList<>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            //This query will select all the mdns under the profile
            //more fields can be added in the query as per use case
            query = queryMapper.getQuery(GET_ALL_USER_PROFILE_SUBSCRIBER_LIST_DETAILS);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, userProfileId);
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                KnCorpContactDTO mdnInfoDto = new KnCorpContactDTO();
                mdnInfoDto.setMdn(rs.getString(1).trim());
                mdnInfoDto.setName(rs.getString(2));
                mdnInfoDto.setCorpId(rs.getInt(3));
                subscribersList.add(mdnInfoDto);

            }
            return subscribersList;

        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while getUserProfileSubscriberList", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while getUserProfileSubscriberList", e);
            throw KnDbUtil.processException(e, "Failed while fetching the PoC subcribers List ." + e, pttServerId,
                    KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.debug(methodName, "EXIT : PoC subscribers list returned for given user profile Id", subscribersList.toString());
        }

    }

    /**
     *
     * @param mdnList
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Set<Integer> getCorpIdFromMdnList(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getCorpIdFromMdnList(List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, " mdnList ", mdnList);
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        Set<Integer> corpIds = new HashSet<>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            Collection<Collection<String>> compList = KnDbUtil.getCollectionList(mdnList, BULK_UPDATE_SIZE);
            for (Collection<String> subMdnList : compList) {
                query = queryMapper.getQuery(GET_CORP_ID_FROM_POCSUBSCRINFO);
                String mdnListString = formCommaSeperatedIdList(subMdnList);
                query = replaceContactWithValue(query, MDNLIST, mdnListString);
                pstmt = conn.prepareStatement(query);
                knLogger.debug(methodName, "Executing query - ", "'", query, "'");
                rs = pstmt.executeQuery();
                knLogger.debug(methodName, "Query executed successfully");
                while (rs.next()) {
                    corpIds.add(rs.getInt(1));
                }
            }
            return corpIds;

        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while getCorpIdFromMdnList", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while getCorpIdFromMdnList", e);
            throw KnDbUtil.processException(e, "Failed while fetching the PoC corpIds  ." + e, pttServerId,
                    KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.debug(methodName, "EXIT : the PoC corpIds ", corpIds);
        }

    }

    public boolean ifMdnisSGMDN(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "ifMdnisSGMDN(mdn,  KnPersisterTxn)";
        knLogger.debug(methodName, "mdn", KnGDPRTemplate.mdn(mdn));
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        int client_type = 0;
        boolean ifMdnisSGMDN = false;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_SGMDN_CLIENT_TYPE);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, mdn);
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            if (rs.next()) {
                client_type = rs.getInt(1);
            }
            knLogger.debug("client_Type from DB :",client_type);
            if(client_type == 8 || client_type == 17)
                ifMdnisSGMDN = true;
            else
                ifMdnisSGMDN = false;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while checking ifMdnisSGMDN", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while checking ifMdnisSGMDN", e);
            throw KnDbUtil.processException(e, "Failed while fetching ifMdnisSGMDN" + e, pttServerId,
                    KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        knLogger.debug("ifMdnisSGMDN true/false: ",ifMdnisSGMDN);
        return ifMdnisSGMDN;
    }

    public Set<Integer> getPamAccId(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getPamAccId(int,  KnPersisterTxn)";
        knLogger.debug(methodName, corpId);
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        Set<Integer> pamList = new HashSet<Integer>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_PAMACCID);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, corpId);
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully.");
            while (rs.next()) {
                pamList.add(rs.getInt(1));
            }
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while retrieving the pamAccId - ", e);
            throw KnDbUtil.processException(e, "Failed while retrieving the pamAccId -  " + e,
                    pttServerId, KnDAOSourceTypes.XDM_POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        knLogger.debug(methodName, "pamAccId: ",pamList);
        return pamList;

    }

    /**
     * This method returns the group members details.
     *
     * @param memberList
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    private List<String> getGrpMemsDetails(List<String> memberList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getGrpMemsDetails(List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : mdnList ", KnGDPRTemplate.mdnList(memberList));
        Connection conn;
        boolean ownedTxn = false;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        String mdn;
        int index = 1;
        List<String> grpMemList = new ArrayList<>();
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the Transaction");
                persisterTxn.open();
                ownedTxn = true;
            }
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_GROUP_MEMBERS_LIST_PV);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedQuesMarks(memberList));
            knLogger.debug(methodName, "query", query);
            pstmt = conn.prepareStatement(query);
            for (String imdn : memberList) {
                pstmt.setString(index++, imdn);
            }
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully.");
            while (rs.next()) {
                grpMemList.add(rs.getString(1).trim());
            }
            if (ownedTxn) {
                persisterTxn.save();
            }
            knLogger.debug(methodName, "subscribersList size", grpMemList.size());
            return grpMemList;
        } catch (SQLException e) {
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to retrieve external cont name ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } catch (Exception e) {
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to retrieve external cont name ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
    }

    /**
     * This method returns the mdnList whose PV is less than 13.
     *
     * @param mdnList
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    private List<String> selectMdnsLessThanThirteenPv(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "selectMdnsLessThanThirteenPv(List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : mdnList ", KnGDPRTemplate.mdnList(mdnList));
        Connection conn;
        boolean ownedTxn = false;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        String mdn;
        int index = 1;
        List<String> grpMdnList = new ArrayList<>();
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the Transaction");
                persisterTxn.open();
                ownedTxn = true;
            }
            query = "SELECT MDN FROM DG.POCSUBSCRINFO WHERE MDN IN (MDNLIST) AND CLIENTPV_MAJORVERSION < 13;";
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedQuesMarks(mdnList));
            knLogger.debug(methodName, "query", query);
            pstmt = conn.prepareStatement(query);
            for (String imdn : mdnList) {
                pstmt.setString(index++, imdn);
            }
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully.");
            while (rs.next()) {
                mdn = rs.getString(1).trim();
                grpMdnList.add(mdn);
            }
            if (ownedTxn) {
                persisterTxn.save();
            }
            knLogger.debug(methodName, "subscribersList size", grpMdnList.size());
            return grpMdnList;
        } catch (SQLException e) {
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to retrieve Mdns Less Than Thirteen Pv ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } catch (Exception e) {
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to retrieve Mdns Less Than Thirteen Pv ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public Map<String, Map<String, Object>> getCorpAllInternalSubscribers(Collection<Integer> idListExistInDB, int idType, int corpId, int maxAllowedContactCount
            , int fetchSize, int nextToken, int sortType, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getCorpAllInternalSubscribers(Collection<Integer>, int, int, int, int, Collection<KnCorpSubscriberDTO>, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY : CorpId -  ", corpId, "idListExistInDB: ", idListExistInDB, "idType: ",
                idType, "maxAllowedContactCount: ", maxAllowedContactCount, "fetchSize: ", fetchSize, "nextToken: ",
                nextToken, "sortType: ", sortType);
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        Map<String, Map<String, Object>> subscriberDeActMap = new HashMap<>();

        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            knLogger.debug(methodName, "Query Mapper - ", queryMapper);
            query = queryMapper.getQuery(GET_CORP_INTERNAL_CONTACT_DETAILS);
            query = replaceContactWithValue(query, "LIST", formIntegerCommaSeperatedIdList(idListExistInDB));
            if (1 == idType) {
                query = replaceContactWithValue(query, "SAI.FAN_ID IN", "SAI.BAN_ID IN");
            }
            if (KnConstants.SORT_TYPE_BY_COLUMN.NONE.value() == sortType) {
                query = replaceContactWithValue(query, "ORDER BY SORT_TYPE", "");
            } else {
                query = replaceContactWithValue(query, "SORT_TYPE", sortType == KnConstants.SORT_TYPE_BY_COLUMN.MDN.value() ? "CAST(SUBSC.MDN AS NUMBER)" : "SUBSCRNAME");
            }
            Map<String, Object> subscriberDTOMap = new HashMap<>();
            Map<String, Object> deActSubsMap = new HashMap<>();
            Map<String, Object> mdnParentIdMap = new HashMap<>();
            int startIndex = getStartIndex(fetchSize, nextToken);
            int endIndex = getEndIndex(fetchSize, nextToken);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, startIndex);
            pstmt.setInt(2, endIndex);
            pstmt.setInt(3, corpId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                String mdn = rs.getString(1).trim();
                //multilingual revert changes
                String contactName = rs.getString(2);
                if (contactName != null) {
                    try {
                        contactName = new String(contactName.getBytes("8859_1"), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        knLogger.error(methodName, " Exception in encoding Subscriber Name - ", KnGDPRTemplate.mdn(mdn), e);
                    }
                }
                int contactCorpId = rs.getInt(6);
                KnCorpSubscriberDTO subsDTO = new KnCorpSubscriberDTO(mdn, contactName, contactCorpId);
                subsDTO.setServiceAuthStatus(rs.getInt(3));
                int publicType = rs.getInt(4);
                int corpType = rs.getInt(5);
                int finalSubscriptionType = getMappedSubscriptionType(publicType, corpType);
                int contactCount = rs.getInt(7);
                if (contactCount > maxAllowedContactCount) {
                    subsDTO.setMaxContactLimitFlag(GREATER_THAN_LIMIT);
                } else if (contactCount == maxAllowedContactCount) {
                    subsDTO.setMaxContactLimitFlag(EQUAL_TO_LIMIT);
                } else {
                    subsDTO.setMaxContactLimitFlag(LESS_THAN_LIMIT);
                }
                subsDTO.setContactCount(contactCount);
                subsDTO.setSubscriptionType(finalSubscriptionType);
                subsDTO.setClientType(rs.getInt(8));
                String activeFS = rs.getString(10) != null ? rs.getString(10) : KnGeneralUtil.convertLongToHexString(rs.getLong(9));
                subsDTO.setSubsActiveFS2(activeFS);
                subsDTO.setUserId(rs.getString(11));
                subsDTO.setDispatchType(rs.getInt(12));
                subsDTO.setAliasMdn(rs.getString(13));
                subsDTO.setLicenseType(rs.getInt(14));
                subsDTO.setPamAccId(rs.getInt(15));
                String subsFS = rs.getString(17) != null ? rs.getString(17) : KnGeneralUtil.convertLongToHexString(rs.getLong(16));
                subsDTO.setSubscriberFs2(subsFS);
                subscriberDTOMap.put(mdn, subsDTO);
                if (subsDTO.getServiceAuthStatus() == 0) {
                    deActSubsMap.put(mdn, subsDTO);
                }
                Map<String, Object> fanBanMap = new HashMap<>();
                fanBanMap.put(KnConstants.INT_BAN_ID, rs.getString(18));
                fanBanMap.put(KnConstants.INT_FAN_ID, rs.getString(19));
                mdnParentIdMap.put(mdn, fanBanMap);
            }
            subscriberDeActMap.put(CORPORATE_SUBSRIBERS, subscriberDTOMap);
            subscriberDeActMap.put(CORP_DEACTIVARED_LIST, deActSubsMap);
            subscriberDeActMap.put(INTERNAL_SUBSCRIBERS, mdnParentIdMap);
            knLogger.info(methodName, "Exit: subscriberDTOMap:  ", subscriberDTOMap.size(), "deActSubsMap: ", deActSubsMap.size(),
                    "mdnParentIdMap: ", mdnParentIdMap.size());
        } catch (SQLException e) {
            knLogger.error(methodName, "Failed while retrieving the corporate subscribers ");
            throw KnDbUtil.processException(e, "Failed while retrieving the corporate subscribers ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        return subscriberDeActMap;
    }

    public int getCorpAllInternalSubscribersCount(Collection<Integer> idListExistInDB, int idType, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getCorpAllInternalSubscribers(Collection<Integer>, int, int, Collection<KnCorpSubscriberDTO>, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY : CorpId -  ", corpId, "idType : ", idType, "idListExistInDB : ", idListExistInDB);
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        int count = 0;
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_CORP_INTERNAL_CONTACT_COUNT);
            query = replaceContactWithValue(query, "LIST", formIntegerCommaSeperatedIdList(idListExistInDB));
            if (1 == idType) {
                query = replaceContactWithValue(query, "SAI.FAN_ID IN", "SAI.BAN_ID IN");
            }
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, corpId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            knLogger.error(methodName, "Failed while retrieving the corporate subscribers count");
            throw KnDbUtil.processException(e, "Failed while retrieving the corporate subscribers count",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            knLogger.info(methodName, "EXIT : count -  ", count);
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        return count;
    }

    public Map<String,Map<String,Integer>> getMdnsFanBanInfo(Collection<String> mdns, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getMdnsFanBanInfo()";
        knLogger.info(methodName, "Entry : mdns - ", mdns.size());
        ResultSet rs = null;
        String query = null;
        PreparedStatement pstmt = null;
        Connection conn = null;
        int index = 1;
        Map<String, Map<String, Integer>> mdnFanBanInfo = new HashMap<>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_MDN_ADDL_INFO);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedQuesMarks(mdns));
            knLogger.debug(methodName, "query", query);
            pstmt = conn.prepareStatement(query);
            for (String mdn : mdns) {
                pstmt.setString(index++, mdn);
            }
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Map<String, Integer> fanBanInfo = new HashMap<>();
                fanBanInfo.put(FAN_ID, rs.getInt(FAN_ID));
                fanBanInfo.put(BAN_ID, rs.getInt(BAN_ID));
                mdnFanBanInfo.put(rs.getString(MDN).trim(), fanBanInfo);
            }
        } catch (SQLException e) {
            knLogger.error(methodName, " Failed while retrieving the mdnsFanBAN info ", e);
            throw KnDbUtil.processException(e, "Failed while getMdnsFanBanInfo -",
                    pttServerId, "SUBSCRIBER_ADDLINFO", query);
        } finally {
            knLogger.info(methodName, "EXIT: mdnFanBanInfo ", mdnFanBanInfo.size());
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        return mdnFanBanInfo;
    }

    public List<Integer> getDistinctFanInfo(Collection<String> mdns, KnPersisterTxn persisterTxn, boolean readOnly) throws KnDAOException {
        String methodName = "getDistinctFanInfo(Collection<String>, KnPersisterTxn, Boolean)";
        knLogger.info(methodName, "Entry : mdns - ", mdns.size());
        ResultSet rs = null;
        String query = null;
        PreparedStatement pstmt = null;
        Connection conn = null;
        int index = 1;
        List<Integer> distFanInfo = new ArrayList<>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_DISTINCT_MDN_FAN_INFO);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedQuesMarks(mdns));
            knLogger.debug(methodName, "query", query);
            pstmt = conn.prepareStatement(query);
            for (String mdn : mdns) {
                pstmt.setString(index++, mdn);
            }
            rs = pstmt.executeQuery();
            while (rs.next()) {
                distFanInfo.add(rs.getInt(FAN_ID));
            }
        } catch (SQLException e) {
            knLogger.error(methodName, " Failed while retrieving the distinct FAN info ", e);
            throw KnDbUtil.processException(e, "Failed while getMdnsFanInfo -",
                    pttServerId, "SUBSCRIBER_ADDLINFO", query);
        } finally {
            knLogger.info(methodName, "EXIT: fanInfo ", distFanInfo.size());
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        return distFanInfo;
    }

    public List<Integer> getDistinctBanInfo(Collection<String> mdns, KnPersisterTxn persisterTxn, boolean readOnly) throws KnDAOException {
        String methodName = "getDistinctBanInfo(Collection<String>, KnPersisterTxn, Boolean)";
        knLogger.info(methodName, "Entry : mdns - ", mdns.size());
        ResultSet rs = null;
        String query = null;
        PreparedStatement pstmt = null;
        Connection conn = null;
        int index = 1;
        List<Integer> distBanInfo = new ArrayList<>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_DISTINCT_MDN_BAN_INFO);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedQuesMarks(mdns));
            knLogger.debug(methodName, "query", query);
            pstmt = conn.prepareStatement(query);
            for (String mdn : mdns) {
                pstmt.setString(index++, mdn);
            }
            rs = pstmt.executeQuery();
            while (rs.next()) {
                distBanInfo.add(rs.getInt(BAN_ID));
            }
        } catch (SQLException e) {
            knLogger.error(methodName, " Failed while retrieving the distinct BAN info ", e);
            throw KnDbUtil.processException(e, "Failed while getMdnsBanInfo -",
                    pttServerId, "SUBSCRIBER_ADDLINFO", query);
        } finally {
            knLogger.info(methodName, "EXIT: BanInfo ", distBanInfo.size());
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        return distBanInfo;
    }

    public Collection<KnCorpGroupMemberDTO> getMemDetsils(int groupId, String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getMemDetsils()";
        knLogger.info(methodName, "Entry : groupId - ", groupId, "mdn", mdn);
        ResultSet rs = null;
        String query = "SELECT CORPGROUPID, MEMBERMDN, IS_SUPERVISOR, IS_LOCWATCHER, IS_OSMAUTHORIZED, " +
                "CALL_INITIATE_PERMISSION, CALL_RECEIVE_PERMISSION, INCALL_PERMISSION, IS_BROADCASTER " +
                "FROM DG.CORPGROUPMEMBERLIST WHERE CORPGROUPID = ? AND MEMBERMDN = ?";
        PreparedStatement pstmt = null;
        Connection conn = null;
        boolean ownedTxn = false;
        int index = 1;
        Collection<KnCorpGroupMemberDTO> MemDetails = new ArrayList<>();
        try {
            if (persisterTxn != null) {
                conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            } else {
                conn = KnDBManager.getDBManagerInstance().getConnection(KnGenInfoUtil.getSecDBPttServerId(pttServerId), true);
                ownedTxn = true;
            }
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, groupId);
            pstmt.setString(2, mdn);
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            rs = pstmt.executeQuery();
            while (rs.next()) {
                KnCorpGroupMemberDTO knCorpGroupMemberDTO = new KnCorpGroupMemberDTO();
                knCorpGroupMemberDTO.setGroupId(rs.getInt(1));
                knCorpGroupMemberDTO.setMdn(rs.getString(2));
                knCorpGroupMemberDTO.setSupervisory(rs.getInt(3));
                knCorpGroupMemberDTO.setLocWatcher(rs.getInt(4));
                knCorpGroupMemberDTO.setIsOSMAuthorize(rs.getInt(5));
                knCorpGroupMemberDTO.setCallInitiatePermission(rs.getInt(6));
                knCorpGroupMemberDTO.setCallReceivePermission(rs.getInt(7));
                knCorpGroupMemberDTO.setInCallPermission(rs.getInt(8));
                knCorpGroupMemberDTO.setBroadcaster(rs.getInt(9));
                MemDetails.add(knCorpGroupMemberDTO);
            }
        } catch (SQLException e) {
            knLogger.error(methodName, " Failed while retrieving the distinct BAN info ", e);
            throw KnDbUtil.processException(e, "Failed while getMdnsBanInfo -",
                    pttServerId, "CORPGROUPINFO", query);
        } finally {
            knLogger.info(methodName, "EXIT: MemDetails ", MemDetails.size());
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            if (ownedTxn) {
                KnDbUtil.closeConnection(conn);
            }
        }
        return MemDetails;
    }

    public boolean checkValidHierarchySubs(List<String> mdnList, Map<String, Object> customParams, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "isValidHierarchySubs(Collection<String>,customParams KnPersisterTxn)";
        boolean isValid = false;
        int count = 0;
        String query;
        long sizeExcludingNull = mdnList.stream().filter(Objects::nonNull).count();
        int idType = Integer.parseInt((String) customParams.get(IDTYPE));
        Collection<String> idList = (Collection<String>) customParams.get(IDLIST);
        if (idType == 1) {
            query = "select count(1) from DG.SUBSCRIBER_ADDLINFO where mdn IN (MDNLIST) and BAN_ID in(IDLIST)";
        } else {
            query = "select count(1) from DG.SUBSCRIBER_ADDLINFO where mdn IN (MDNLIST) and FAN_ID in(IDLIST)";
        }
        var subsLists = KnGeneralUtil.splitList(mdnList, BULK_UPDATE_SIZE);
        for (var subsList : subsLists) {
            count += checkValidHierarchySubsCount(subsList, query, idList, persisterTxn);
        }
        if (count == sizeExcludingNull) {
            isValid = true;
        }
        knLogger.debug(methodName, "isValidHierarchySubs", isValid);
        return isValid;
    }
    private int checkValidHierarchySubsCount(List<String> mdnList, String query, Collection<String> idList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "isValidHierarchySubs(Collection<String>,customParams KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : mdnList ", mdnList);
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        int index = 1;
        int count = 0;
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            query = replaceContactWithValue(query, "MDNLIST", formCommaSeperatedQuesMarks(mdnList));
            query = replaceContactWithValue(query, IDLIST, formCommaSeperatedQuesMarks(idList));
            knLogger.debug(methodName, "query", query);
            pStmt = conn.prepareStatement(query);
            //pStmt.setString(index++, mdn);
            for (String mdn : mdnList) {
                pStmt.setString(index++, mdn);
            }
            for (String id : idList) {
                pStmt.setString(index++, id);
            }
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully.");
            while (rs.next()) {
                count = rs.getInt(1);
            }
            knLogger.debug(methodName, "userId & Mdn Map size ", count);
        } catch (Exception e) {
            throw KnDbUtil.processException(e, "Failed to retrieve Subs info ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
        }
        return count;
    }

    public Collection<String> getMcidsByMdnList(Collection<String> mdnList, KnPersisterTxn persisterTxn, boolean readOnly) throws KnDAOException {
        String methodName = "getMcidsByMdnList()";
        knLogger.debug(methodName, "Entry : mdnList - ", mdnList.size());
        ResultSet rs = null;
        String query = null;
        PreparedStatement pstmt = null;
        Connection conn = null;
        int index = 1;
        Collection<String> groupMdnList = new ArrayList<>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_DISTINCT_MC_IDS_FROM_MDNLIST);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedQuesMarks(mdnList));
            knLogger.debug(methodName, "query", query);
            pstmt = conn.prepareStatement(query);
            for (String mdn : mdnList) {
                pstmt.setString(index++, mdn);
            }
            rs = pstmt.executeQuery();
            while (rs.next()) {
                groupMdnList.add(rs.getString(MC_ID));
            }
        } catch (SQLException e) {
            knLogger.error(methodName, " Failed while retrieving the distinct MC_IDs info ", e);
            throw KnDbUtil.processException(e, "Failed while getMcidsByMdnList -",
                    pttServerId, "POCSUBSCRINFO", query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        return groupMdnList;
    }

    public Map<Integer, String> getFanDetailsByCorporateId(int corpId, KnPersisterTxn persisterTxn, boolean readOnly) throws KnDAOException {
        String methodName = "getFanDetailsByCorporateId(corpId, persisterTxn)";
        knLogger.debug(methodName, "Entry : corpId - ", corpId);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = "SELECT FAN_ID,EXTERNAL_FAN_ID FROM DG.FAN_DETAILS WHERE CORPID=?";
        Map<Integer, String> fanDetails = new HashMap<Integer, String>();
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, corpId);
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                int fanId = rs.getInt(FAN_ID);
                fanDetails.put(fanId, rs.getString(EXTERNAL_FAN_ID));
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getFanDetailsByCorporateId  - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occurred while getFanDetailsByCorporateId - ", e);
            throw KnDbUtil.processException(e, "Failed while getFanDetailsByCorporateId -" + e,
                    pttServerId, "", query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.debug(methodName, "EXIT. fanDetails size returned is - ", fanDetails.size());
        }
        return fanDetails;
    }

    public Map<Integer, String> getBanDetailsByCorporateId(int corpId, KnPersisterTxn persisterTxn, boolean readOnly) throws KnDAOException {
        String methodName = "getBanDetailsByCorporateId(corpId, persisterTxn)";
        knLogger.debug(methodName, "Entry : corpId - ", corpId);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = "SELECT BAN.BAN_ID, BAN.EXTERNAL_BAN_ID FROM DG.BAN_DETAILS BAN INNER JOIN DG.FAN_DETAILS FAN ON BAN.FAN_ID = FAN.FAN_ID WHERE FAN.CORPID = ?;";
        Map<Integer, String> banDetails = new HashMap<Integer, String>();
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, corpId);
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                int banId = rs.getInt(BAN_ID);
                banDetails.put(banId, rs.getString(EXTERNAL_BAN_ID));
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getBanDetailsByCorporateId  - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occurred while getBanDetailsByCorporateId - ", e);
            throw KnDbUtil.processException(e, "Failed while getBanDetailsByCorporateId -" + e,
                    pttServerId, "", query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.debug(methodName, "EXIT. banDetails size returned is - ", banDetails.size());
        }
        return banDetails;
    }

    public Map<String, KnCorpSubscriberDTO> getSubscriberDetails(List<String> mdnList, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubscriberDetails(List<String>, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : mdnList - ", mdnList == null ? mdnList : KnGDPRTemplate.mdnList(mdnList));
        var mdnListArray = new ArrayList<>(mdnList);
        Map<String, KnCorpSubscriberDTO> subscMap = new HashMap<>();
        var subsLists = KnGeneralUtil.splitList(mdnListArray, BULK_UPDATE_SIZE);
        for (var subsList : subsLists) {
            var subscriberCorporateDetails = getSubscriberDetail(subsList, readOnly, persisterTxn);
            if (subscriberCorporateDetails != null && !subscriberCorporateDetails.isEmpty()) {
                subscMap.putAll(subscriberCorporateDetails);
            }
        }
        return subscMap;
    }

    public Map<String, KnCorpSubscriberDTO> getSubscriberDetail(List<String> mdnList, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubscriberDetails(List<String>, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : mdnList - ", mdnList == null ? mdnList : KnGDPRTemplate.mdnList(mdnList));
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        int index = 1;
        Map<String, KnCorpSubscriberDTO> subscMap;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(SELECT_POCSUBSCRIBERS);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedQuesMarks(mdnList));
            knLogger.debug(methodName, "query", query);
            pstmt = conn.prepareStatement(query);
            for (String mdn : mdnList) {
                pstmt.setString(index++, mdn);
            }
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            subscMap = new HashMap<String, KnCorpSubscriberDTO>();
            while (rs.next()) {
                KnCorpSubscriberDTO subscriberDTO = new KnCorpSubscriberDTO();
                subscriberDTO.setMdn(rs.getString(1).trim());
                subscriberDTO.setClientType(rs.getInt(2));
                subscriberDTO.setCorpId(rs.getInt(3));
                subscriberDTO.setUserAgent(rs.getString(4));
                subscriberDTO.setClientPVmajorVer(rs.getInt(5));
                subscriberDTO.setServiceAuthStatus(rs.getInt(6));
                subscriberDTO.setPresenceHome(rs.getString(7));
                subscriberDTO.setXdmsHome(rs.getString(8));
                subscriberDTO.setSubscriptionType(rs.getInt(9));
                subscriberDTO.setSubscriberFs2(rs.getString(10));
                subscriberDTO.setClientFs2(rs.getString(11));
                subscriberDTO.setOpsFs2(rs.getString(12));
                subscriberDTO.setCorpAdminFS2(rs.getString(13));
                subscriberDTO.setXdmsFs2(rs.getString(14));
                subscriberDTO.setUserProfileFS2(rs.getString(15));
                subscriberDTO.setHierarchyId(rs.getString(16)); // HIERARCHY_ID from POCSUBSCRINFO table
                Object clusterIdObj = rs.getObject(17);
                if (clusterIdObj != null) {
                    subscriberDTO.setClusterId((Integer) clusterIdObj);
                } else {
                    subscriberDTO.setClusterId(null);
                }
                subscriberDTO.setPocHome(rs.getString(18));
                subscriberDTO.setMcpttCompliance(rs.getInt(19));
                subscMap.put(subscriberDTO.getMdn(), subscriberDTO);
            }
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while getSubscribersName - ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        knLogger.debug(methodName, "EXIT subscriberMap size", subscMap.size());
        return subscMap;
    }

}
