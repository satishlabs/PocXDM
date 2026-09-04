/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMCorpActivationDAO.java
 * Subsystem:  POC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Sanjiv Acharyya        30-11-2011      7.2
 * <p/>
 * <p/>
 * #401, 4th Floor, 'Prestige Sigma'
 * No.3, Vittal Mallya Road
 * Bangalore - 560 001
 * www.kodiaknetworks.com
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
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dao.persister.db.KnQueryMapper;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.resources.KnErrorCodes;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpActivationDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil;
import com.kodiak.xdms.server.corpmgmt.resources.KnDAOSourceTypes;

import java.sql.*;
import java.util.*;

import static com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants.*;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.formCommaSeperatedIdList;


public class KnXDMCorpActivationDAO implements ITableDAO {
	private static final KnLogger knLogger = KnLogger.getLogger(KnXDMCorpActivationDAO.class);

    private static final String CLASS = KnXDMCorpActivationDAO.class.getName();


    private String pttServerId;

    KnXDMCorpActivationDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }


    private String getServiceName(int clientType) {
        String serviceName = "";
        switch (clientType) {
            case CLIENT_TYPE_DESKTOP:
                serviceName = ACTIVATION_SERVICE_NAME_DESKTOP;
                break;
            case CLIENT_TYPE_DISPATCH:
                serviceName = ACTIVATION_SERVICE_NAME_DISPATCH;
                break;
            case CLIENT_TYPE_HANDSET:
                serviceName = ACTIVATION_SERVICE_NAME_HANDSET;
                break;
            case CLIENT_TYPE_INTEROP:
                serviceName = ACTIVATION_SERVICE_NAME_INTEROP;
                break;
            case CLIENT_TYPE_WIFI:
                serviceName = ACTIVATION_SERVICE_NAME_WIFI;
                break;
            case CLIENT_TYPE_3RD_PARTYPOC:
                serviceName = ACTIVATION_SERVICE_NAME_3RDPARTYPOC;
                break;
            case CROSS_CARRIER_PTT_CLIENT:
                serviceName = ACTIVATION_SERVICE_NAME_CROSS_CARRIER;
                break;
            case CLIENT_TYPE_PDV_CONNECT:
                serviceName = ACTIVATION_SERVICE_NAME_PDV_CONNECT;
                break;
            case MOBILE_API:
                serviceName = ACTIVATION_SERVICE_NAME_3RDPARTYPOC;
                break;
            case THIRD_PARTY_DISPATCHER:
                serviceName = ACTIVATION_SERVICE_NAME_DISPATCH;
                break;
            case PTT_RADIO_HANDEST_CLIENT:
                serviceName = ACTIVATION_SERVICE_NAME_PTT_RADIO;
                break;
            case PTT_RADIO_CROSSCARRIER_CLIENT:
                serviceName = ACTIVATION_SERVICE_NAME_PTT_RADIO;
                break;
            case PTT_RADIO_WIFIONLY_CLIENT:
                serviceName = ACTIVATION_SERVICE_NAME_PTT_RADIO;
                break;
        }
        return serviceName;
    }

    public Collection<KnCorpSubscriberDTO> insertActivationCode(Collection<KnCorpSubscriberDTO> subscList, String serviceName, KnPersisterTxn
            persisterTxn) throws KnDAOException {
        String methodName = "insertActivationCode(Collection, KnPersisterTxn)";
        Collection<KnCorpSubscriberDTO> failedMdnList = new ArrayList<KnCorpSubscriberDTO>();
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(INSERT_ACTIVATION_CODE_INTO_TEMP_VAS_TABLE);
            conn = persisterTxn.getDBConnection(pttServerId, false);
            pstmt = conn.prepareStatement(query);
            for (KnCorpSubscriberDTO subs : subscList) {
                try {
                    if(serviceName == null || serviceName.isEmpty()) {
                        serviceName = getServiceName(subs.getClientType());
                    }
                    pstmt.setString(1, subs.getMdn());
                    pstmt.setString(2, serviceName);
                    pstmt.setString(3, subs.getActivationCode());
                    pstmt.setTimestamp(4, subs.getActivationTimestamp(), Calendar.getInstance(TimeZone.getTimeZone("UTC")));
                    pstmt.setTimestamp(5, subs.getExpiryTime(), Calendar.getInstance(TimeZone.getTimeZone("UTC")));
                    knLogger.debug(methodName, "Executing query - ", query);
                    pstmt.executeQuery();
                } catch (SQLException sqe) {
                    knLogger.error(methodName, "SQLException occured - ", sqe);
                    KnDAOException daoExc = KnDbUtil.processException(sqe, "QLException", null, KnDAOSourceTypes.TMPVASSUBSCRIPTIONKEYINFO, query);
                    if (KnErrorCodes.DAO.ROW_ALREADY_EXISTS.equals(daoExc.getErrorCode())) {
                        failedMdnList.add(subs);
                    } else {
                        knLogger.error(methodName, "SQLException occured - ", sqe);
                        throw daoExc;
                    }

                }
            }
            knLogger.debug(methodName, "EXIT: Query executed successfully");
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while deleteing the corp group member count entry - ", e);
            throw e;
        } catch (Exception e) {
            throw KnDbUtil.processException(e, "Failed while inserting the activation code - " + e,
                    pttServerId, KnDAOSourceTypes.TMPVASSUBSCRIPTIONKEYINFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
        return failedMdnList;
    }

    public void insetActivationCode(String mdn, String activationCode, Timestamp expiryTime, KnPersisterTxn
            persisterTxn, int clientType, Timestamp currentTimeInUTC) throws KnDAOException {
        String methodName = "insetActivationCode(String, String,  String, KnPersisterTxn)";
        knLogger.debug( methodName, "ENTRY : mdn - ", KnGDPRTemplate.mdn(mdn), ", activationCode - "
                , activationCode, ", expiryTime - ", expiryTime, ", clientType - ", clientType, ", currentTimeInUTC - ", currentTimeInUTC);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            String serviceName = getServiceName(clientType);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(INSERT_ACTIVATION_CODE_INTO_TEMP_VAS_TABLE);
            conn = persisterTxn.getDBConnection(pttServerId, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, mdn);
            pstmt.setString(2, serviceName);
            pstmt.setString(3, activationCode);
            pstmt.setTimestamp(4, currentTimeInUTC, Calendar.getInstance(TimeZone.getTimeZone("UTC")));
            pstmt.setTimestamp(5, expiryTime, Calendar.getInstance(TimeZone.getTimeZone("UTC")));
            knLogger.debug( methodName, "Executing query - ", "'", query, "'");
            pstmt.executeQuery();
            knLogger.debug( methodName, "EXIT: Query executed successfully");
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while deleteing the corp group member count entry - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while inserting the activation code - ", e);
            throw KnDbUtil.processException(e, "Failed while deleteing the corp group member count entry - " + e,
                    pttServerId, KnDAOSourceTypes.TMPVASSUBSCRIPTIONKEYINFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public boolean isActivationCodeExist(String mdn, KnPersisterTxn persisterTxn, int clientType) throws KnDAOException {

        String methodName = "isActivationCodeExist(String, KnPersisterTxn, int)";
        knLogger.debug(methodName, "ENTRY : mdn - ", KnGDPRTemplate.mdn(mdn), ", clientType - ", clientType);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean isExist = false;
        String query = null;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_ACTIVATION_CODE_FROM_TEMP_VAS_TABLE);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, mdn);
            String serviceName = getServiceName(clientType);
            pstmt.setString(2, serviceName);
            knLogger.debug( methodName, "Executing query -", query);
            rs = pstmt.executeQuery();
            knLogger.debug( methodName, "Query executed successfully.");
            if (rs.next()) {
                knLogger.debug( methodName, "Activation code exist.");
                isExist = true;
            }
            knLogger.debug( methodName, "isExist 111 - ", isExist);

        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while checking is exist -  ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while checking is exist - ",
                    e);
            throw KnDbUtil.processException(e, "Failed to get Param value " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_TEMP_VAT_TABLE, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.debug( methodName, "EXIT : isExist - ", isExist);
        }
        return isExist;
    }

    public Collection<KnCorpSubscriberDTO> isActivationCodeExistForMDN(Collection<KnCorpSubscriberDTO> subsList, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "isActivationCodeExistForMDN(Collection, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        knLogger.debug( methodName, "subsList - ", subsList);
        Collection<KnCorpSubscriberDTO> mdnList = new ArrayList<KnCorpSubscriberDTO>();
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, readOnly);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_ACTIVATION_CODE_FROM_TEMP_VAS_TABLE);
            pstmt = conn.prepareStatement(query);
            for (KnCorpSubscriberDTO subs : subsList) {
                pstmt.setString(1, subs.getMdn());
                String serviceName = getServiceName(subs.getClientType());
                pstmt.setString(2, serviceName);
                rs = pstmt.executeQuery();
                while (rs.next()) {
                    mdnList.add(subs);
                }
                rs.close();
            }
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to get Param value " + e,
                    pttServerId, KnDAOSourceTypes.TMPVASSUBSCRIPTIONKEYINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        knLogger.debug( methodName, "EXIT : mdnList - ", mdnList.size());
        return mdnList;
    }

    public Set<String> isActivationCodeExistInDB(Set<String> activationCodeSet, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "isActivationCodeExistInDB(Set, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : activationCodeSet - ", activationCodeSet);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Set<String> actList = new HashSet<String>();
        String query = null;
        try {

            Connection conn = persisterTxn.getDBConnection(pttServerId, readOnly);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(IS_ACTIVATION_CODE_EXIST);
            knLogger.debug( methodName, "ENTRY : query - ", query);
            pstmt = conn.prepareStatement(query);
            for (String actCode : activationCodeSet) {
                pstmt.setString(1, actCode);
                rs = pstmt.executeQuery();
                while (rs.next()) {
                    actList.add(rs.getString(1));
                }
                rs.close();
            }
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to get Param value " + e,
                    pttServerId, KnDAOSourceTypes.TMPVASSUBSCRIPTIONKEYINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        knLogger.debug( methodName, "EXIT : existing act code size - ", actList.size());
        return actList;
    }


    public Collection<KnCorpSubscriberDTO> updateActivationCode(Collection<KnCorpSubscriberDTO> subsList, KnPersisterTxn
            persisterTxn) throws KnDAOException {
        String methodName = "updateActivationCode(Collection, KnPersisterTxn)";
        knLogger.debug( methodName, "ENTRY : subsList - ",subsList);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        Collection<KnCorpSubscriberDTO> failedMdnList = new ArrayList<KnCorpSubscriberDTO>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(UPDATE_ACTIVATION_CODE_INTO_TEMP_VAS_TABLE);
            conn = persisterTxn.getDBConnection(pttServerId, false);
            pstmt = conn.prepareStatement(query);


            for (KnCorpSubscriberDTO corpSubscriberDTO : subsList) {
                try {
                	String serviceName = new String(getServiceName(corpSubscriberDTO.getClientType()));
                    pstmt.setString(1, corpSubscriberDTO.getActivationCode());
                    pstmt.setTimestamp(2, corpSubscriberDTO.getActivationTimestamp(), Calendar.getInstance(TimeZone.getTimeZone("UTC")));
                    pstmt.setTimestamp(3, corpSubscriberDTO.getExpiryTime(), Calendar.getInstance(TimeZone.getTimeZone("UTC")));
                    pstmt.setString(4, corpSubscriberDTO.getMdn());
                    pstmt.setString(5, serviceName);
                    knLogger.debug( methodName, "pstmt - ", pstmt);
                    knLogger.debug( methodName, "Executing query - ", query);
                    pstmt.executeQuery();
                } catch (SQLException sqe) {
                    knLogger.error( methodName, "SQLException occured - ", sqe);
                    KnDAOException daoExc = KnDbUtil.processException(sqe, "QLException", null, KnDAOSourceTypes.TMPVASSUBSCRIPTIONKEYINFO, query);
                    if (KnErrorCodes.DAO.ROW_ALREADY_EXISTS.equals(daoExc.getErrorCode())) {
                        failedMdnList.add(corpSubscriberDTO);
                    } else {
                        throw daoExc;
                    }
                }
            }
        } catch (KnDAOException e) {
            throw e;
        } catch (Exception e) {
            throw KnDbUtil.processException(e, "Failed while deleteing the corp group member count entry - " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_TEMP_VAT_TABLE, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
        knLogger.debug( methodName, "EXIT: Query executed successfully, FailedMdnList - ", failedMdnList.size());
        return failedMdnList;
    }


    public void updateActivationCode(String mdn, String activationCode, Timestamp expiryTime, KnPersisterTxn
            persisterTxn, int clientType, Timestamp currentTimeInUTC) throws KnDAOException {
        String methodName = "updateActivationCode(String, String,  String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : mdn - ", KnGDPRTemplate.mdn(mdn), ", activationCode - ",
                activationCode, ", expiryTime - ", expiryTime, ", clientType - ",
                clientType, ", currentTimeInUTC - ", currentTimeInUTC);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            String serviceName = getServiceName(clientType);
            query = queryMapper.getQuery(UPDATE_ACTIVATION_CODE_INTO_TEMP_VAS_TABLE);
            conn = persisterTxn.getDBConnection(pttServerId, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, activationCode);
            pstmt.setTimestamp(2, currentTimeInUTC, Calendar.getInstance(TimeZone.getTimeZone("UTC")));
            pstmt.setTimestamp(3, expiryTime, Calendar.getInstance(TimeZone.getTimeZone("UTC")));
            pstmt.setString(4, mdn);
            pstmt.setString(5, serviceName);
            knLogger.debug( methodName, "Executing query - ", "'", query, "'");
            pstmt.executeQuery();
            knLogger.debug( methodName, "EXIT: Query executed successfully");
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while updateActivationCode- ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while updateActivationCode - ",
                    e);
            throw KnDbUtil.processException(e, "Failed while deleteing the corp group member count entry - " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_TEMP_VAT_TABLE, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public void insert(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        knLogger.warn( "insert", "Unimplemented Methods");
    }

    public void update(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        knLogger.warn( "update", "Unimplemented Methods");
    }

    public void delete(IPersistenceDTO persistencDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        knLogger.warn( "delete", "Unimplemented Methods");
    }

    public Collection select(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        knLogger.warn( "select", "Unimplemented Methods");
        return null;
    }

    /**
     * This method returns the activation code.
     * @param mdnList
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<String, KnIPCorpActivationDTO> getActivationCodeForCorpoateSubscriber(Collection<String> mdnList, boolean readOnly
            , KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "getActivationCodeForCorpoateSubscriber(Collection<String>,boolean,KnPersisterTxn)";
        knLogger.debug(methodName, " readOnly :", readOnly);
        Connection conn;
        String query = null;
        Map<String, KnIPCorpActivationDTO> subscriberActlist = new HashMap<String, KnIPCorpActivationDTO>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_ACTIVATION_CODE_FOR_SUBSC);
            List<String> subsList = new ArrayList<String>();
            subsList.addAll(mdnList);
            conn = persisterTxn.getDBConnection(pttServerId, readOnly);
            Collection<Collection<String>> collList = KnDbUtil.getCollectionList(subsList, 1000);
            for (Collection<String> list : collList) {
                getSubsActCodeMap(list, conn, subscriberActlist, query);
            }
            knLogger.debug( methodName, "List Size Returned - ", subscriberActlist.size());
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while retrieving activation code for subscribers - ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_TEMP_VAT_TABLE, query);
        }
         return subscriberActlist;
    }

    private void getSubsActCodeMap(Collection<String> MdnList, Connection
            conn, Map<String, KnIPCorpActivationDTO> subscriberActlist, String query) throws SQLException {
        String methodName = "getSubsActCodeMap()";
        PreparedStatement pStmt= null;
        ResultSet rs = null;
        int index = 1;
        try {
            String newQuery = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(MdnList,query,"MDNLIST");
            pStmt = conn.prepareStatement(newQuery);
            knLogger.debug( methodName, "Executing query - ", newQuery);
            
            for(String mdn : MdnList)
            	pStmt.setString(index++,mdn);
            
            rs = pStmt.executeQuery();
            knLogger.debug( methodName, "Query executed successfully");
            while (rs.next()) {
                String mdn = rs.getString(1).trim();
                KnIPCorpActivationDTO activationDTO = new KnIPCorpActivationDTO();
                activationDTO.setMdn(mdn);
                activationDTO.setActivationCode(rs.getString(2));
                activationDTO.setActivationTimestamp(rs.getTimestamp(3));
                activationDTO.setExpiryTimestamp(rs.getTimestamp(4));
                subscriberActlist.put(mdn, activationDTO);
            }
           knLogger.debug( methodName, "subscriberActlist",KnGDPRTemplate.mapKeyMdn(subscriberActlist));
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
        }
    }

    /**
     * This method is to return the activation code generation and expiry time for a subscribers.
     * @param mdn
     * @param clientType
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public KnCorpSubscriberDTO getActCodeExtTime(String mdn, int clientType, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getActCodeExtTime(String, int,boolean, KnPersisterTxn)";
        knLogger.debug( methodName, "ENTRY : mdn - ", KnGDPRTemplate.mdn(mdn), "clientType - ", clientType);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean isExist = false;
        String query = null;
        KnCorpSubscriberDTO  subscriberDTO= null;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, readOnly);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_ACTIVATION_CODE_TIMESTAMP);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, mdn);
            String serviceName = getServiceName(clientType);
            pstmt.setString(2, serviceName);
            knLogger.debug( methodName, "Executing query -", query);
            rs = pstmt.executeQuery();
            knLogger.debug( methodName, "Query executed successfully.");
            if (rs.next()) {
                subscriberDTO= new KnCorpSubscriberDTO();
                subscriberDTO.setExpiryTime(rs.getTimestamp(1));
                subscriberDTO.setActivationTimestamp(rs.getTimestamp(2));
                subscriberDTO.setActivationCode(rs.getString(3));
            }
            knLogger.debug( methodName, "subscriberDTO", subscriberDTO);

        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to get Param value ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_TEMP_VAT_TABLE, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        return subscriberDTO;
    }

    /**
     * This method is to return the List mdns whose otp is already present in the DB
     *
     * @param mdnList
     * @param serviceName
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Collection<String> activationCodeExistForMDNs(Collection<String> mdnList, String serviceName, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "activationCodeExistForMDNs(Collection,String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        List<String> presentMdns = new ArrayList<>();
        knLogger.debug(methodName, "mdnList - ", mdnList == null ? mdnList : KnGDPRTemplate.mdnList(mdnList)," serviceName -",serviceName);
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_ACTIVATION_CODE_FROM_TEMP_VAS_TABLE);
            pstmt = conn.prepareStatement(query);
            for (String mdn : mdnList) {
                pstmt.setString(1, mdn);
                pstmt.setString(2, serviceName);
                rs = pstmt.executeQuery();
                if (rs.next()) {
                    presentMdns.add(mdn);
                }
                rs.close();
            }
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to get Param value " + e,
                    pttServerId, KnDAOSourceTypes.TMPVASSUBSCRIPTIONKEYINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        knLogger.debug(methodName, "EXIT : presentMdns - ", presentMdns.size());
        return presentMdns;
    }

    public void deleteActivationCode(Collection<String> mdns, String serviceName, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteActivationCode(Collection,String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        knLogger.debug(methodName, "mdns - ", mdns == null ? mdns: KnGDPRTemplate.mdnList(mdns));
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(DELETE_ACTIVATION_CODE_FROM_TEMP_VAS_TABLE);
            pstmt = conn.prepareStatement(query);
            for (String mdn : mdns) {
                pstmt.setString(1, mdn);
                pstmt.setString(2, serviceName);
                rs = pstmt.executeQuery();
                KnDbUtil.closeResultSet(rs);
            }
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to get Param value " + e,
                    pttServerId, KnDAOSourceTypes.TMPVASSUBSCRIPTIONKEYINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public String getSubscribersOTP(String mdn, String serviceName, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "activationCodeExistForMDNs(Collection,String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        String otp = null;
        knLogger.debug(methodName, "mdn - ", KnGDPRTemplate.mdn(mdn));
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_ACTIVATION_CODE_FROM_TEMP_VAS_TABLE);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1,mdn);
            pstmt.setString(2, serviceName);
            rs = pstmt.executeQuery();
            if(rs.next()){
                //here dataType is varchar that's why trim is not required.
                otp = rs.getString(1);
            }
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to get Param value " + e,
                    pttServerId, KnDAOSourceTypes.TMPVASSUBSCRIPTIONKEYINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        knLogger.debug(methodName, "EXIT : otp - ", otp);
        return otp;
    }

    public void deleteActivationCodeForMDN(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteActivationCodeForMDN(String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        knLogger.debug(methodName, "mdn - ", KnGDPRTemplate.mdn(mdn));
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(DELETE_ACTIVATION_CODE_FROM_TEMP_VAS_TABLE_MDN);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, mdn);
            rs = pstmt.executeQuery();
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to get Param value " + e,
                    pttServerId, KnDAOSourceTypes.TMPVASSUBSCRIPTIONKEYINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public void deleteActivationCodeForMDN(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteActivationCodeForMDN(String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        knLogger.debug(methodName, "mdn - ", KnGDPRTemplate.mdnList(mdnList));
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, false);
            query = "DELETE FROM DG.TMPVASSUBSCRIPTIONKEYINFO WHERE MDN = ?";
            //query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(mdnList,query,"MDNLIST");
            pstmt = conn.prepareStatement(query);
            for(String mdn : mdnList){
                pstmt.setString(1, mdn);
                pstmt.addBatch();
            }
            int[] results = pstmt.executeBatch();
            boolean status = Arrays.stream(results).allMatch(result -> result >= 0);
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to get Param value " + e,
                    pttServerId, KnDAOSourceTypes.TMPVASSUBSCRIPTIONKEYINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }
}