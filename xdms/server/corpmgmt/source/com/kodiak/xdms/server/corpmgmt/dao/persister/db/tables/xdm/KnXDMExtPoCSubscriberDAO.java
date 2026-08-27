/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
\ * <p/>
 * File name:  KnXDMExtPoCSubscriberDAO.java
 * Subsystem:  POC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Namita.P.Nair        26-01-2011      7.0
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

import com.kodiak.common.dao.KnConnectionException;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dao.persister.db.KnDBPersistenceException;
import com.kodiak.xdms.server.common.dao.persister.db.KnQueryMapper;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.common.KnSubscriberDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpContactDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpContactListDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpContactDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnExtSubsDetailsDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpContactListRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpResponseDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpSubscContactListRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupMemPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil;
import com.kodiak.xdms.server.corpmgmt.resources.KnDAOSourceTypes;

import java.io.UnsupportedEncodingException;
import java.sql.*;
import java.util.*;

import static com.kodiak.xdms.server.common.resources.KnConstants.BULK_UPDATE_SIZE;
import static com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants.*;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.formCommaSeperatedIdList;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.replaceContactWithValue;

public class KnXDMExtPoCSubscriberDAO implements ITableDAO {
    private static final KnLogger knLogger = KnLogger.getLogger(KnXDMExtPoCSubscriberDAO.class);

    public String pttServerId = null;

    KnXDMExtPoCSubscriberDAO(String pttServerId) {
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

    public Collection<KnCorpSubscriberDTO> selectExternalPoCSubscriberInfo(Collection<KnCorpSubscriberDTO> contactList,
                                                                           int corpId,
                                                                           KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "selectExternalPoCSubscriberInfo(Collection<KnCorpSubscriberDTO> , int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : contactList - ", contactList, ", corpId - ", corpId);
        Connection conn;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String query = null;
        int index = 1;
        Collection<KnCorpSubscriberDTO> subscribersList = new ArrayList<KnCorpSubscriberDTO>();
        boolean ownedTxn = false;
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.GET_EXTERNAL_CONTACT);
            //conn = persisterTxn.getDBConnection(pttServerId, false);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            Map<String, KnCorpSubscriberDTO> subscInterMap = new HashMap<>();
            Collection<String> mdnList = new ArrayList<String>(contactList.size());
            for (KnCorpSubscriberDTO subscriberDTO : contactList) {
                mdnList.add(subscriberDTO.getMdn());
                subscInterMap.put(subscriberDTO.getMdn(), subscriberDTO);
            }
            /*StringBuffer mdnListBuffer = new StringBuffer(50);
            mdnListBuffer.append("'");
            for (KnCorpSubscriberDTO subscriberDTO : contactList) {
                mdnListBuffer.append(subscriberDTO.getMdn()).append("','");
            }
            int index = mdnListBuffer.lastIndexOf(",'");
            String mdnStr = "";
            if (index > 0) {
                mdnStr = mdnListBuffer.substring(0, index);
            }*/
            //query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedIdList(mdnList));
            //query = KnDbUtil.replaceValInQry(query, corpId);
            query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(mdnList,query,"MDNLIST");
            knLogger.debug(methodName, "Executing query - ", query);
            stmt = conn.prepareStatement(query);
            for(String mdn : mdnList){
                stmt.setString(index++,mdn);
            }
            stmt.setInt(index++,corpId);
            rs = stmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully.");
            while (rs.next()) {
                KnCorpSubscriberDTO subscriberDTO = new KnCorpSubscriberDTO();
                String mdn = rs.getString(1).trim();
                subscriberDTO.setMdn(mdn);
                int mdnCorpId = rs.getInt(2);
                subscriberDTO.setCorpId(mdnCorpId);
                String name = null;
                //multilingual revert changes
                if(null != rs.getString(3))
                {
                	try {
						name = new String(rs.getString(3).getBytes("8859_1"),"UTF-8");
					} catch (UnsupportedEncodingException e) {
						knLogger.debug(methodName, "UTF-8 encoding exception -  ", rs.getString(2),e);
					}
                }
                subscriberDTO.setName(name);
                subscriberDTO.setSubscriptionType(EXTERNAL_CONTACT_TYPE);
                int contact_type = rs.getInt(4);
                if(contact_type == KnConstants.CONTACT_TYPE_EXTERNAL_CONTACT){
                    subscriberDTO.setContact_type(KnConstants.XCAP_DIFF_CONTACT_TYPE_EXTERNAL_CONTACT);
                }else{
                    subscriberDTO.setContact_type(KnConstants.XCAP_DIFF_CONTACT_TYPE_EXTERNAL_SUBSCRIBER);
                }
                if (subscInterMap.get(mdn) != null) {
                    subscriberDTO.setClientType(subscInterMap.get(mdn).getClientType());
                    subscriberDTO.setClientPVmajorVer(subscInterMap.get(mdn).getClientPVmajorVer());
                    subscriberDTO.setSubsActiveFS2(subscInterMap.get(mdn).getSubsActiveFS2());
                    subscriberDTO.setUserAgent(subscInterMap.get(mdn).getUserAgent());
                }
                subscribersList.add(subscriberDTO);
            }
            knLogger.debug(methodName, "subscribersList size", subscribersList);
            if (ownedTxn) {
                knLogger.debug(methodName, "saving the transaction ");
                persisterTxn.save();
            }
            return subscribersList;

        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while fetching external contact details - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while fetching external contact details - ",
                    e);
            if(ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to retrieve Subscribers external PoC subscriber Info " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_EXT_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
        }
    }

    /**
     * This method returns the external contact in the corporation
     *
     * @param corpId
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<String, KnCorpSubscriberDTO> getCorpExternalSubscriber(int corpId, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "getCorpExternalSubscriber(int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : CorpId -  ", corpId);
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String query = null;
        Map<String, KnCorpSubscriberDTO> subscribersMap = new HashMap<String, KnCorpSubscriberDTO>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.GET_CORP_EXTERNAL_CONTACT_DETAILS);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);
            pStmt.setInt(1, corpId);
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Query executed succesfully");
            while (rs.next()) {
                KnCorpSubscriberDTO subscriberDTO = new KnCorpSubscriberDTO();
                String mdn = rs.getString(1).trim();
                subscriberDTO.setMdn(mdn);
                //multilingual revert change
                String name=null;
                if(null != rs.getString(2))
                {
                	try {
						name = new String(rs.getString(2).getBytes("8859_1"),"UTF-8");
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						knLogger.debug(methodName, "UTF-8 encoding exception -  ", rs.getString(2),e);
					}
                }
                subscriberDTO.setName(name);
                int mdnCorpId = rs.getInt(3);
                subscriberDTO.setCorpId(mdnCorpId);
                subscriberDTO.setSubscriptionType(EXTERNAL_CONTACT_TYPE);
                subscriberDTO.setMaxContactLimitFlag(LESS_THAN_LIMIT);
                subscriberDTO.setServiceAuthStatus(INVALID_SERVICE_AUTH_STATUS);
                subscribersMap.put(mdn, subscriberDTO);
            }
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to retrieve Corp external PoC subscriber Info ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_EXT_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
        }
        knLogger.debug(methodName, "EXIT : external contact details list size  - ", subscribersMap.size());
        return subscribersMap;
    }


    public KnCorpSubscContactListRespDTO insertExternalPoCMemberDetails(KnIPCorpContactListDTO contactListDTO
            , KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "insertExternalPoCMemberDetails(KnIPCorpContactListDTO, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : Input DTO passed contactListDTO - ", contactListDTO);

        Connection conn;
        PreparedStatement pstmt = null;
        String updateQuery = "";
        KnCorpSubscContactListRespDTO respDTO = new KnCorpSubscContactListRespDTO();
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            updateQuery = queryMapper.getQuery(INSERT_INTO_POC_EXT);
            pstmt = conn.prepareStatement(updateQuery);
            int corpId = contactListDTO.getCorpId();
            for (KnCorpSubscriberDTO subscriber : contactListDTO.getContactList()) {
                pstmt.setInt(1, corpId);
                int cont_type = 1;
                if (subscriber.getSubsType() != KnConstants.CONTACT_TYPE_EXTERNAL_CONTACT) {
                    cont_type = KnConstants.CONTACT_TYPE_EXTERNAL_SUBSCRIBER;
                }

                pstmt.setString(2, subscriber.getMdn());
                //multilingual revert change
                if(null != subscriber.getName())
                {
                	pstmt.setString(3, new String(subscriber.getName().getBytes("UTF-8"),"8859_1"));
                }
                int memberCorpId = subscriber.getCorpId();
                if (memberCorpId <= 0) {
                    pstmt.setNull(4, java.sql.Types.INTEGER);
                } else {
                    pstmt.setInt(4, memberCorpId);
                }
                pstmt.setInt(5, cont_type);
                pstmt.addBatch();
            }
            knLogger.debug(methodName, "Executing query- ", "'", updateQuery, "'");
            pstmt.executeBatch();
            knLogger.debug(methodName, "Input DTO passed contactListDTO - ", contactListDTO, "EXIT: Query executed successfully, Returing response - ", respDTO);
            return respDTO;

        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while inserting external subscriber details - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while inserting external subscriber details - "
                    + e);
            throw KnDbUtil.processException(e, "Failed  inserting external subscriber details " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_EXT_SUBSCRIBER_INFO, updateQuery);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public KnCorpResponseDTO updateExternalPoCMemberDetails(KnIPCorpContactDTO contactDTO,
                                                            KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateExternalPoCMemberDetails(KnIPCorpContactDTO, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :  Input DTO passed contactDTO - ", contactDTO);

        Connection conn;
        PreparedStatement pstmt = null;
        String updateQuery = "";
        KnCorpSubscContactListRespDTO respDTO = new KnCorpSubscContactListRespDTO();
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            updateQuery = queryMapper.getQuery(UPDATE_POC_EXT);
            pstmt = conn.prepareStatement(updateQuery);
            int corpId = contactDTO.getCorpId();
            //multilingual revert change
            if(null != contactDTO.getName())
            {
            	pstmt.setString(1, new String(contactDTO.getName().getBytes("UTF-8"),"8859_1"));
            }
            pstmt.setString(2, contactDTO.getMdn());
            pstmt.setInt(3, corpId);
            knLogger.debug(methodName, "Executing query - ", "'", updateQuery, "'");
            pstmt.executeQuery();
            knLogger.debug(methodName, "Input DTO passed contactDTO - ", contactDTO, "EXIT : Query executed successfully, Response returned - ", respDTO);
            return respDTO;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while updating external contact details - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while updating external contact details - ",
                    e);
            throw KnDbUtil.processException(e, "Failed to Update External Contact in DB" + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_EXT_SUBSCRIBER_INFO, updateQuery);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }

    }

    public Collection<String> getSubscMdnsHavingExtContact(KnIPCorpContactDTO contactDTO,
                                                           KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubscMdnsHavingExtContact(KnIPCorpContactDTO, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : Input DTO passed contactDTO - ", contactDTO);

        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Collection<String> mdnList = new ArrayList<String>();
        String updateQuery = "";
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            updateQuery = queryMapper.getQuery(SELECT_SUBS_HAVING_EXTN_CONTACT);
            pstmt = conn.prepareStatement(updateQuery);
            pstmt.setString(1, contactDTO.getMdn());
            pstmt.setInt(2, contactDTO.getCorpId());
            knLogger.debug(methodName, "Executing query- ", "'", updateQuery, "'");
            rs = pstmt.executeQuery();
            while (rs.next()) {
                mdnList.add(rs.getString(1).trim());
            }
            knLogger.debug(methodName, "Query executed successfully");
            return mdnList;

        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while fetching the subscribers having external", " contact in the contactList - " + e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while fetching the subscribers having external  ",
                    "contact in the contactList - " + e);
            throw KnDbUtil.processException(e, "Failed to fetch Subscribers Having External Contact - " + KnGDPRTemplate.mdn(contactDTO.getMdn()) + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_EXT_SUBSCRIBER_INFO, updateQuery);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            knLogger.debug(methodName, "Input DTO passed contactDTO - ", contactDTO, "EXIT : the subscriber List having the external contact - ",  KnGDPRTemplate.mdnList(mdnList));
        }
    }

    public Collection<KnCorpContactDTO> getExternalContactNames(Map<String, KnCorpContactDTO> extrenalContacts,
                                                                int corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getExternalContactNames(Map<String, KnCorpContactDTO>, int,boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :  Input DTO passed - ", extrenalContacts == null ? extrenalContacts : KnGDPRTemplate.mapKeyMdn(extrenalContacts), ", corpId - ", corpId);
        Connection conn;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
//            StringBuffer mdnListBuffer = new StringBuffer(50);
            Collection<String> mdnList = extrenalContacts.keySet();
            var mdnListArray = new ArrayList<>(mdnList);
            var subsLists = KnGeneralUtil.splitList(mdnListArray, BULK_UPDATE_SIZE);
            for (var subsList : subsLists) {
                query = queryMapper.getQuery(KnPersisterConstants.GET_EXTERNAL_CONTACT_NAME);
                //String mdnStr = formCommaSeperatedIdList(mdnList);
                //query = replaceContactWithValue(query, MDNLIST, mdnStr);
                query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(subsList, query, "MDNLIST");
                //query = KnDbUtil.replaceValInQry(query, corpId);
                knLogger.debug(methodName, "Executing query- ", query);
                int index = 1;
                stmt = conn.prepareStatement(query);
                for (String mdn : subsList) {
                    stmt.setString(index++, mdn);
                }
                stmt.setInt(index++, corpId);
                rs = stmt.executeQuery();
                knLogger.debug(methodName, "Query executed successfully.");
                while (rs.next()) {
                    KnCorpContactDTO contact = extrenalContacts.get(rs.getString(1).trim());
                    //multilingual revert change
                    if (null != rs.getString(2)) {
                        try {
                            contact.setName(new String(rs.getString(2).trim().getBytes("8859_1"), "UTF-8"));
                        } catch (UnsupportedEncodingException e) {
                            // TODO Auto-generated catch block
                            knLogger.error(methodName, "encoding exception - ", rs.getString(2), e);
                        }
                    }
                }
            }
            knLogger.debug(methodName, "External contact details fetched from DB :  ", extrenalContacts.values());
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to get external contact names -",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_EXT_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
        }
        return extrenalContacts.values();
    }

    public void selectExternalGroupMembersContactNames(Map<String, KnCorpGroupMemPersistDTO> externalContacts,
                                                       int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "selectExternalGroupMembersContactNames(Map<String, KnCorpGroupMemPersistDTO>, int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : InputDTO passed in request externalContacts- ", externalContacts == null ? externalContacts : KnGDPRTemplate.mapKeyMdn(externalContacts), ", corpId - ", corpId);

        Connection conn;
        Statement stmt = null;
        ResultSet rs = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            Collection<String> mdnList = externalContacts.keySet();
            var mdnListArray = new ArrayList<>(mdnList);
            var subsLists = KnGeneralUtil.splitList(mdnListArray, BULK_UPDATE_SIZE);
            for (var subsList : subsLists) {
                query = queryMapper.getQuery(KnPersisterConstants.GET_EXTERNAL_CONTACT_NAME);
                String mdnListStr = formCommaSeperatedIdList(subsList);
                query = replaceContactWithValue(query, MDNLIST, mdnListStr);
                query = KnDbUtil.replaceValInQry(query, corpId);
                knLogger.debug(methodName, "Executing query - ", query);
                stmt = conn.createStatement();
                rs = stmt.executeQuery(query);
                knLogger.debug(methodName, "Executed query successfully");
                while (rs.next()) {
                    KnCorpContactDTO contact = externalContacts.get(rs.getString(1).trim());
                    //multilingual revert change
                    if (null != rs.getString(2)) {
                        try {
                            contact.setName(new String(rs.getString(2).trim().getBytes("8859_1"), "UTF-8"));
                        } catch (UnsupportedEncodingException e) {
                            knLogger.error(methodName, "encoding exception - ", rs.getString(2), e);
                        }
                    }
                }
            }
            knLogger.debug(methodName, "Fetched the details from DB :  ", KnGDPRTemplate.mapKeyMdn(externalContacts));
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while fetching groups external contact name - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while fetching groups external contact name - ",
                    e);
            throw KnDbUtil.processException(e, "Failed while fetching groups external contact name -" + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_EXT_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            knLogger.debug(methodName, "InputDTO passed in request externalContacts- ", KnGDPRTemplate.mapKeyMdn(externalContacts), ", corpId - ", corpId, "EXIT. External contact details refernce changed - ", externalContacts);
        }
    }

    public KnCorpContactListRespDTO selectExternalContactDetails(Collection<KnIPCorpContactDTO> contactListDTO,
                                                                 int corpId, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "selectExternalContactDetails(Collection<KnIPCorpContactDTO>, int,boolean KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : contactListDTO - ", contactListDTO.size(), ", corpId - ", corpId);
        Connection conn;
        Statement stmt = null;
        ResultSet rs = null;
        String query = null;
        KnCorpContactListRespDTO response = new KnCorpContactListRespDTO();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.GET_ALL_EXTERNAL_CONTACTS);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            Collection<String> mdnList = new ArrayList<String>();
            for (KnIPCorpContactDTO contactDTO : contactListDTO) {
                mdnList.add(contactDTO.getMdn());
            }
            String mdnStr = formCommaSeperatedIdList(mdnList);
            query = replaceContactWithValue(query, MDNLIST, mdnStr);
            query = KnDbUtil.replaceValInQry(query, corpId);
            knLogger.debug(methodName, "Executing query - ", query);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            knLogger.debug(methodName, "Query executed successfully");
            List<KnCorpSubscriberDTO> subscList = new ArrayList<KnCorpSubscriberDTO>();
            Map<String, KnCorpSubscriberDTO> extSubsMap = new HashMap<>();
            while (rs.next()) {
                KnCorpSubscriberDTO subsc = new KnCorpSubscriberDTO();
                int contType = rs.getInt(4);
                String mdn = rs.getString(1).trim();
                String name=null;
                //multilingual revert change
                if(null != rs.getString(2))
                {
                	try {
						name=new String(rs.getString(2).getBytes("8859_1"),"UTF-8");
					} catch (UnsupportedEncodingException e) {
						knLogger.error(methodName, "UTF-8 encoding exception - ", rs.getString(2), e);
					}
                }
                if (contType == KnConstants.CONTACT_TYPE_EXTERNAL_CONTACT) {
                    subsc.setMdn(mdn);
                    subsc.setExternalContact(true);
                    subsc.setCorpId(rs.getInt(3));
                    subsc.setSubsType(1);
                    subsc.setName(name);
                    subscList.add(subsc);
                } else {
                    subsc.setMdn(mdn);
                    subsc.setExternalContact(true);
                    subsc.setCorpId(rs.getInt(3));
                    subsc.setName(name);
                    extSubsMap.put(mdn, subsc);
                }
            }
            response.setContactList(subscList);
            response.setExtSubsMap(extSubsMap);
            knLogger.debug(methodName, "Exit: SubsList", subscList, " extSubsMap ", KnGDPRTemplate.mapKeyMdn(extSubsMap));
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while retrieving external contact details -",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_EXT_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
        }
        return response;
    }


    public Collection<KnCorpSubscriberDTO> getExternalPoCSubscriberDetails(Collection<KnCorpSubscriberDTO> contactsList,
                                                                           int corpId, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "getExternalPoCSubscriberDetails(Collection<KnCorpSubscriberDTO>, int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : contactsList- ", contactsList, " , corpId - ", corpId);
        Connection conn;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String query = null;
        int index = 1;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.GET_POC_EXTERNAL_CONTACT_DETAILS);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            /*StringBuffer mdnListBuffer = new StringBuffer(50);
            mdnListBuffer.append("'");
            for (KnCorpSubscriberDTO subscriberDTO : contactsList) {
                mdnListBuffer.append(subscriberDTO.getMdn()).append("','");
            }
            int index = mdnListBuffer.lastIndexOf(",'");
            String mdnStr = "";
            if (index > 0) {
                mdnStr = mdnListBuffer.substring(0, index);
            }*/
            Collection<String> mdnList = new ArrayList<String>(contactsList.size());
            for (KnCorpSubscriberDTO subscriberDTO : contactsList) {
                mdnList.add(subscriberDTO.getMdn());
            }
            //query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedIdList(mdnList));
            //query = KnDbUtil.replaceValInQry(query, corpId);
            query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(mdnList,query,"MDNLIST");
            knLogger.debug(methodName, "Executing query - ", query);
            stmt = conn.prepareStatement(query);
            for(String mdn : mdnList){
                stmt.setString(index++,mdn);
            }
            stmt.setInt(index++,corpId);
            rs = stmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            Collection<KnCorpSubscriberDTO> subscList = new ArrayList<KnCorpSubscriberDTO>();
            while (rs.next()) {
                KnCorpSubscriberDTO subsc = new KnCorpSubscriberDTO();
                subsc.setMdn(rs.getString(1).trim());
                //multilingual revert change
                if(null != rs.getString(2))
                {
                	subsc.setName(new String(rs.getString(2).getBytes("8859_1"),"UTF-8"));
                }
                subsc.setExternalContact(true);
                subsc.setServiceAuthStatus(rs.getInt(3));
                subsc.setSubscriptionType(EXTERNAL_CONTACT_TYPE);
                subsc.setCorpId(rs.getInt(6));
                subsc.setMaxContactLimitFlag(LESS_THAN_LIMIT);
                if (rs.getInt(3) == 0 && rs.getInt(4) == 0 && rs.getInt(5) == 0) {
                    subsc.setServiceAuthStatus(INVALID_SERVICE_AUTH_STATUS);
                }
                subsc.setClientType(rs.getInt(7));
                subsc.setContact_type(rs.getInt(8));
                subscList.add(subsc);
            }
            knLogger.debug(methodName, "Fetched the details from DB list size is :  ", subscList.size());
            return subscList;

        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving external contact details - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while retrieving external contact details - ",
                    e);
            throw KnDbUtil.processException(e, "Failed while retrieving external contact details -" + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_EXT_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            knLogger.debug(methodName, "EXIT");
        }
    }

    public int getExternalContactCount(int corpId, int contactType, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getExternalContactCount(int, int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : corpId,- ", corpId);
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        int count = 0;
        try {

            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.GET_EXTERNAL_CONTACT_COUNT);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, corpId);
            pstmt.setInt(2, contactType);
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Executed query successfully");
            if (rs.next()) {
                count = rs.getInt(1);
            }
            knLogger.debug(methodName, "External contacts for contact type  ", contactType, " is :", count);
            return count;
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while fetching groups external contact name -",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_EXT_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            knLogger.debug(methodName, "EXIT.  No of external contacts for corp - ", count);
        }
    }

    public void deleteExtMember(Collection<String> mdnList, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteExtMember(Collection<String>, int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : InputDTO passed in request mdnList,- ", mdnList == null ? mdnList : KnGDPRTemplate.mdnList(mdnList), " , corpId - ", corpId);

        Connection conn;
        Statement stmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            var mdnListArray = new ArrayList<>(mdnList);
            var subsLists = KnGeneralUtil.splitList(mdnListArray, BULK_UPDATE_SIZE);
            for (var subsList : subsLists) {
                query = queryMapper.getQuery(KnPersisterConstants.DELETE_EXTERNAL_CONTACTS);
                query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedIdList(subsList));
                query = KnDbUtil.replaceValInQry(query, corpId);
                knLogger.debug(methodName, "Executing query - ", query);
                stmt = conn.createStatement();
                stmt.executeUpdate(query);
            }
            knLogger.debug(methodName, "EXIT.  External members deleted : Executed query successfully");
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while fetching deleting external contacts for corp  - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while fetching  deleting external contacts for corp - ",
                    e);
            throw KnDbUtil.processException(e, "Failed while fetching groups external contact name -" + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_EXT_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeStatement(stmt);
        }
    }

    public void deleteCorporateExternalMembers(KnIPCorpInfoDTO corpInfoDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteCorporateExternalMembers(KnIPCorpInfoDTO, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : InputDTO passed in request corpInfoDTO,- ", corpInfoDTO);

        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.DELETE_CORP_EXTERNAL_CONTACTS);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, corpInfoDTO.getCorpId());
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            pstmt.executeQuery();
            knLogger.debug(methodName, "EXIT: Executed query successfully  External members deleted.");
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while fetching deleting external contacts for corp  - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while fetching  deleting external contacts for corp - ", e);
            throw KnDbUtil.processException(e, "Failed while fetching groups external contact name -" + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_EXT_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public void nullifyContactCorpIdInExtTable(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "nullifyContactCorpIdInExtTable( int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : InputDTO passed in request corpId,- ", corpId);

        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.NULLIFY_CONTACT_CORPID_FOR_DELETED_CORP_MEMBERS);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, corpId);
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            pstmt.executeQuery();
            knLogger.debug(methodName, "EXIT: Executed query successfully, nullified the contact corp id .");
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while nulliying the contact corp id  - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while nulliying the contact corp id - ", e);
            throw KnDbUtil.processException(e, "Failed while nulliying the contact corp id -" + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_EXT_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public void updateExtContactCorpId(String corpId, String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateExtContactCorpId(String, String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : InputDTO passed in request corpId,- ", corpId, ", mdn - ", KnGDPRTemplate.mdn(mdn));

        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {

            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.UPDATE_EXT_CONTACT_CORPID);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            if (corpId == null) {
                pstmt.setNull(1, java.sql.Types.INTEGER);
            } else {
                pstmt.setInt(1, Integer.parseInt(corpId));
            }
            pstmt.setString(2, mdn);
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            pstmt.executeQuery();
            knLogger.debug(methodName, "EXIT: Executed query successfully, updated the contact corp id .");
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while updating the contact corp id  - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while updating the contact corp id - ", e);
            throw KnDbUtil.processException(e, "Failed while updating the contact corp id -" + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_EXT_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public void deleteSusbcribersFromExtContactTables(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteSusbcribersFromExtContactTables(String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : InputDTO passed in request mdn,- ", KnGDPRTemplate.mdn(mdn));

        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {

            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.DELETE_EXT_CONTACT_FOR_ALL_CORPID);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, mdn);
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            pstmt.executeQuery();
            knLogger.debug(methodName, "EXIT: Executed query successfully, updated the contact corp id .");
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while updating the contact corp id  - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while updating the contact corp id - ", e);
            throw KnDbUtil.processException(e, "Failed while updating the contact corp id -" + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_EXT_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public void updateSubscribersInExtContactTables(String oldMdn, String newMdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateSusbcribersInExtContactTables(String, String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : InputDTO passed in request newMdn,- ", KnGDPRTemplate.mdn(newMdn), ", oldMdn - ", KnGDPRTemplate.mdn(oldMdn));

        Connection conn;
        PreparedStatement pstmt = null;
//        ResultSet rs = null;
        String query = null;
        try {

            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.UPDATE_EXT_CONTACT_FOR_ALL_CORPID);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, newMdn);
            pstmt.setString(2, oldMdn);
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            pstmt.executeQuery();
            knLogger.debug(methodName, "EXIT: Executed query successfully, updated the contact corp id .");
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while updating the contact corp id  - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while updating the contact corp id - ", e);
            throw KnDbUtil.processException(e, "Failed while updating the contact corp id -" + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_EXT_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public Map<Integer, String> getCorpIdListWhereIsExternalContact(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getCorpIdListWhereIsExternalContact(String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : InputDTO passed in request mdn,- ", KnGDPRTemplate.mdn(mdn));

        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        Map<Integer, String> corpIdExtContactNameMap = new HashMap<Integer, String>();
        try {

            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.GET_ALL_CORPIDS_FOR_EXT_CONTACT);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, mdn);
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
            	//multilingual revert change
            	String name=null;
            	if(rs.getString(2) != null){
            		name=new String(rs.getString(2).getBytes("8859_1"),"UTF-8");
            	}
            	corpIdExtContactNameMap.put(rs.getInt(1), name);
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getCorpIdListWhereIsExternalContact  - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while getCorpIdListWhereIsExternalContact - ",
                    e);
            throw KnDbUtil.processException(e, "Failed while getCorpIdListWhereIsExternalContact -" + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_EXT_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        knLogger.debug(methodName, "EXIT: getCorpIdListWhereIsExternalContact .");
        return corpIdExtContactNameMap;
    }

    public Map<Integer, String> getCorpIdListWhereIsExternalContact(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getCorpIdListWhereIsExternalContact(String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : InputDTO passed in request mdn,- ", KnGDPRTemplate.mdnList(mdnList));
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        int index = 1;
        Map<Integer, String> corpIdExtContactNameMap = new HashMap<Integer, String>();
        try {
            query = "SELECT CORPID,CONTACTNAME FROM DG.EXTCORPCONTACT WHERE CONTACTMDN IN (MDNLIST)";
            query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(mdnList,query,"MDNLIST");
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            for (String mdn : mdnList) {
                pstmt.setString(index++, mdn);
            }
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                //multilingual revert change
                String name=null;
                if(rs.getString(2) != null){
                    name=new String(rs.getString(2).getBytes("8859_1"),"UTF-8");
                }
                corpIdExtContactNameMap.put(rs.getInt(1), name);
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getCorpIdListWhereIsExternalContact  - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while getCorpIdListWhereIsExternalContact - ",
                    e);
            throw KnDbUtil.processException(e, "Failed while getCorpIdListWhereIsExternalContact -" + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_EXT_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        knLogger.debug(methodName, "EXIT: getCorpIdListWhereIsExternalContact .");
        return corpIdExtContactNameMap;
    }

    public Map<Integer, KnCorpSubscriberDTO> getCorpIdContactTypeWhereIsExternalContact(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getCorpIdContactTypeWhereIsExternalContact(String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : InputDTO passed in request mdn,- ", KnGDPRTemplate.mdn(mdn));
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        KnCorpSubscriberDTO subscriberDTO = null;
        Map<Integer, KnCorpSubscriberDTO> corpIdExtContactNameMap = new HashMap<Integer, KnCorpSubscriberDTO>();
        try {

            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.GET_ALL_CORPIDS_CONTACTTYPE_FOR_EXT_CONTACT);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, mdn);
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            rs = pstmt.executeQuery();
            knLogger.debug( methodName, "Query executed successfully");
            while (rs.next()) { 
            	//multilingual revert change
            	String name=null;
            	if(rs.getString(2) != null){
            		name=new String(rs.getString(2).getBytes("8859_1"),"UTF-8");
            	}
            	subscriberDTO=new KnCorpSubscriberDTO(name,rs.getInt(3));
                corpIdExtContactNameMap.put(rs.getInt(1), subscriberDTO);
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getCorpIdListWhereIsExternalContact  - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while getCorpIdListWhereIsExternalContact - ", e);
            throw KnDbUtil.processException(e, "Failed while getCorpIdListWhereIsExternalContact -" + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_EXT_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            knLogger.debug(methodName, "Exit", corpIdExtContactNameMap.size());
        }
        return corpIdExtContactNameMap;
    }

    public void addExternalContactsInAllCorp(Map<Integer, KnCorpSubscriberDTO> corpIdExtContactNameMap, String newMdn, int corpId, KnPersisterTxn
            persisterTxn) throws KnDAOException {
        String methodName = "addExternalContactsInAllCorp(Map<Integer, String>, String, int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : InputDTO passed in request corpIdExtContactNameMap,- ", corpIdExtContactNameMap, "newMdn - ", KnGDPRTemplate.mdn(newMdn), ", corpId - ", corpId);
        Connection conn;
        PreparedStatement pstmt = null;
//        ResultSet rs = null;
        String query = null;
        try {

            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.INSERT_INTO_EXT_CONTACT_TABLE_FOR_ALL_CORPID);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            Collection<Integer> corpIdList = corpIdExtContactNameMap.keySet();
            for (int extCorpId : corpIdList) {
                pstmt.setInt(1, extCorpId);
                pstmt.setString(2, newMdn);
                //multilingual revert change
                String name=corpIdExtContactNameMap.get(extCorpId).getName();
                if(name != null){
            		 name= new String(name.getBytes("UTF-8"),"8859_1");
            		 pstmt.setString(3,name);
                }
                else{
                	 pstmt.setNull(3, java.sql.Types.VARCHAR);
                }
                if (corpId == 0) {
                    pstmt.setNull(4, java.sql.Types.INTEGER);
                } else {
                    pstmt.setInt(4, corpId);
                }
                pstmt.setInt(5, corpIdExtContactNameMap.get(extCorpId).getContact_type());
                pstmt.addBatch();
            }
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            pstmt.executeBatch();
            knLogger.debug(methodName, "EXIT: Executed query successfully, updated the contact corp id .");
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while updating the contact corp id  - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while updating the contact corp id - ", e);
            throw KnDbUtil.processException(e, "Failed while updating the contact corp id -" + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_EXT_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    /**
     * This method is used to get corporates where the subscriber is an external contact
     *
     * @param mdn
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Collection<Integer> getExtCorpForSub(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getExtCorpForSub(String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : InputDTO passed in request mdn,- ", KnGDPRTemplate.mdn(mdn));
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.GET_EXTERNAL_CORP_FOR_SUBSC);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, mdn);
            Collection<Integer> corpList = new ArrayList<Integer>();
            knLogger.debug(methodName, "Executing query - ", "'" + query, "'");
            rs = pstmt.executeQuery();
            while (rs.next()) {
                corpList.add(rs.getInt(1));
            }
            // pstmt.executeBatch();
            knLogger.debug(methodName, "EXIT: updated the contact corp id, Executed query successfully");
            return corpList;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving corporate where mdn is external - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while  retrieving corporate where mdn is external - ", e);
            throw KnDbUtil.processException(e, "Failed while updating the contact corp id -" + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_EXT_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
    }

    /**
     * This method query dg.extcorpxontact table and return the external contact details in the corporation.
     *
     * @param contactList
     * @param corpId
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Collection<KnCorpSubscriberDTO> selectExternalPoCSubscriberInfo(List<String> contactList, int
            corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "selectExternalPoCSubscriberInfo(List<String> , int,boolean KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : contactList - ", contactList, ", corpId - ", corpId, " readOnly :", readOnly);
        Connection conn;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String query = null;
        Collection<KnCorpSubscriberDTO> subscribersList = new ArrayList<KnCorpSubscriberDTO>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            //query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedIdList(contactList));
            //query = KnDbUtil.replaceValInQry(query, corpId);
            var mdnListArray = new ArrayList<>(contactList);
            var subsLists = KnGeneralUtil.splitList(mdnListArray, BULK_UPDATE_SIZE);
            for (var subsList : subsLists) {
                query = queryMapper.getQuery(KnPersisterConstants.GET_EXTERNAL_CONTACT_NAME_FOR_XCAP);
                query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(subsList, query, "MDNLIST");
                int index = 1;
                knLogger.debug(methodName, "Executing query - ", query);
                stmt = conn.prepareStatement(query);
                for (String cont : subsList) {
                    stmt.setString(index++, cont);
                }
                stmt.setInt(index++, corpId);
                rs = stmt.executeQuery();
                knLogger.debug(methodName, "Query executed successfully.");
                while (rs.next()) {
                    KnCorpSubscriberDTO subscriberDTO = new KnCorpSubscriberDTO();
                    String mdn = rs.getString(1).trim();
                    subscriberDTO.setMdn(mdn);
                    //multilingual revert change
                    if (rs.getString(2) != null) {
                        try {
                            subscriberDTO.setName(new String(rs.getString(2).trim().getBytes("8859_1"), "UTF-8"));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                    subscriberDTO.setContact_type(rs.getInt(3));
                    subscriberDTO.setCorpId(rs.getInt(4));
                    subscriberDTO.setSubscriptionType(EXTERNAL_CONTACT_TYPE);
                    subscriberDTO.setServiceAuthStatus(INVALID_SERVICE_AUTH_STATUS);
                    subscriberDTO.setExternalContact(true);
                    subscribersList.add(subscriberDTO);
                }
            }
            knLogger.debug(methodName, "subscribersList size", subscribersList.size());
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to retrieve external cont name ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_EXT_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
        }
        return subscribersList;
    }

    /**
     * This method returns a Map of external contact MDN and name from the dg.extcorpcontact table.
     *
     * @param contactList
     * @param corpId
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<String, KnSubscriberDTO> getExtContName(List<String> contactList, int corpId,boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getExtContName(List<String> , int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : contactList - ", contactList, ", corpId - ", corpId,readOnly);
        Connection conn;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String query = null;
        Map<String, KnSubscriberDTO> subsNameMap = new HashMap<>(contactList.size());
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            //query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedIdList(contactList));
            //query = KnDbUtil.replaceValInQry(query, corpId);
            var mdnListArray = new ArrayList<>(contactList);
            var subsLists = KnGeneralUtil.splitList(mdnListArray, BULK_UPDATE_SIZE);
            for (var subsList : subsLists) {
                query = queryMapper.getQuery(KnPersisterConstants.GET_EXTERNAL_CONTACT_NAME_FOR_XCAP);
                query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(subsList, query, "MDNLIST");
                int index = 1;
                knLogger.debug(methodName, "Executing query", query);
                stmt = conn.prepareStatement(query);
                for (String cont : subsList) {
                    stmt.setString(index++, cont);
                }
                stmt.setInt(index++, corpId);
                rs = stmt.executeQuery();
                knLogger.debug(methodName, "Query executed successfully.");
                KnSubscriberDTO subscriberDTO;
                while (rs.next()) {
                    subscriberDTO = new KnSubscriberDTO();
                    String mdn = rs.getString(1).trim();
                    subscriberDTO.setMdn(mdn);
                    //multilingual revert changes
                    if (rs.getString(2) != null) {
                        try {
                            subscriberDTO.setNetworkName(new String(rs.getString(2).getBytes("8859_1"), "UTF-8"));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                    if (rs.getString(4) == null) {
                        subscriberDTO.setContact_type(1);
                    }
                    subsNameMap.put(mdn, subscriberDTO);
                }
            }
            knLogger.debug(methodName, "subscribersList size", KnGDPRTemplate.mapKeyMdn(subsNameMap));
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to retrieve external cont name ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_EXT_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
        }
        return subsNameMap;
    }

    /**
     * This method query the dg.corpextcontact table and returns the List of  KnCorpGroupMemPersistDTO
     *
     * @param extlMemLst
     * @param corpId
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Collection<KnCorpGroupMemPersistDTO> getExtGrpMemDetails(List<String> extlMemLst, int corpId, boolean readOnly, KnPersisterTxn
            persisterTxn) throws KnDAOException {
        String methodName = "getExtGrpMemDetails(List, int, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : extlMemLst ", extlMemLst.size());
        Connection conn;
        Statement stmt = null;
        ResultSet rs = null;
        String query = null;
        String mdn;
        Collection<KnCorpGroupMemPersistDTO> grpMemList = new ArrayList<KnCorpGroupMemPersistDTO>(extlMemLst.size());
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            var mdnListArray = new ArrayList<>(extlMemLst);
            var subsLists = KnGeneralUtil.splitList(mdnListArray, BULK_UPDATE_SIZE);
            for (var subsList : subsLists) {
                query = queryMapper.getQuery(KnPersisterConstants.GET_EXTERNAL_CONTACT_NAME_FOR_XCAP);
                query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedIdList(subsList));
                query = KnDbUtil.replaceValInQry(query, corpId);
                knLogger.debug(methodName, "Executing query - ", query);
                stmt = conn.createStatement();
                rs = stmt.executeQuery(query);
                knLogger.debug(methodName, "Query executed successfully.");
                while (rs.next()) {
                    KnCorpGroupMemPersistDTO memberPersistDTO = new KnCorpGroupMemPersistDTO();
                    mdn = rs.getString(1).trim();
                    memberPersistDTO.setMdn(mdn);
                    //multilingual revert changes
                    if (rs.getString(2) != null) {
                        try {
                            memberPersistDTO.setName(new String(rs.getString(2).getBytes("8859_1"), "UTF-8"));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                    memberPersistDTO.setSubscriptionType(EXTERNAL_CONTACT_TYPE);
                    memberPersistDTO.setMaxContactLimitFlag(LESS_THAN_LIMIT);
                    memberPersistDTO.setServiceAuthStatus(INVALID_SERVICE_AUTH_STATUS);
                    memberPersistDTO.setCorpId(corpId);
                    memberPersistDTO.setExternalContact(Boolean.TRUE);
                    grpMemList.add(memberPersistDTO);
                }
            }
            knLogger.debug(methodName, "subscribersList size", grpMemList.size());
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to retrieve external cont name ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
        }
        return grpMemList;
    }

    /**
     * This method query the dg.corpextcontact table and returns the List of KnCorpContactDTO
     *
     * @param extlMemLst
     * @param corpId
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public List<KnCorpContactDTO> getExtSublistMemDetails(List<String> extlMemLst, int corpId, boolean readOnly, KnPersisterTxn
            persisterTxn) throws KnDAOException {
        String methodName = "getExtSublistMemDetails(List, int,boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : extlMemLst ", extlMemLst.size());
        Connection conn;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String query = null;
        String mdn;
        List<KnCorpContactDTO> sublistMemList = new ArrayList<KnCorpContactDTO>(extlMemLst.size());
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            //query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedIdList(extlMemLst));
            //query = KnDbUtil.replaceValInQry(query, corpId);
            var mdnListArray = new ArrayList<>(extlMemLst);
            var subsLists = KnGeneralUtil.splitList(mdnListArray, BULK_UPDATE_SIZE);
            for (var subsList : subsLists) {
                query = queryMapper.getQuery(KnPersisterConstants.GET_EXTERNAL_CONTACT_NAME_FOR_XCAP);
                query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(subsList, query, "MDNLIST");
                int index = 1;
                knLogger.debug(methodName, "Executing query - ", query);
                stmt = conn.prepareStatement(query);
                for (String extl : subsList) {
                    stmt.setString(index++, extl);
                }
                stmt.setInt(index++, corpId);
                rs = stmt.executeQuery();
                knLogger.debug(methodName, "Query executed successfully.");
                while (rs.next()) {
                    KnCorpContactDTO memberDTO = new KnCorpContactDTO();
                    mdn = rs.getString(1).trim();
                    memberDTO.setMdn(mdn);
                    //multilingual revert changes
                    if (rs.getString(2) != null) {
                        try {
                            memberDTO.setName(new String(rs.getString(2).getBytes("8859_1"), "UTF-8"));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                    memberDTO.setSubscriptionType(EXTERNAL_CONTACT_TYPE);
                    memberDTO.setMaxContactLimitFlag(LESS_THAN_LIMIT);
                    memberDTO.setServiceAuthStatus(INVALID_SERVICE_AUTH_STATUS);
                    memberDTO.setContact_type(rs.getInt(3));
                    sublistMemList.add(memberDTO);
                }
            }
            knLogger.debug(methodName, "subscribersList size", sublistMemList.size());
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to retrieve external cont name ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
        }
        return sublistMemList;
    }

    /**
     * This method deleted excernal contact entries from extcorpcontact table.
     *
     * @param mdnList
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void deleteSusbcribersFromExtContactTables(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteSusbcribersFromExtContactTables(List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : mdnList,- ", mdnList == null ? mdnList : KnGDPRTemplate.mdnList(mdnList));
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.DELETE_EXT_CONTACT_FOR_ALL_CORPID);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            for (String mdn : mdnList) {
                pstmt.setString(1, mdn);
                pstmt.addBatch();
            }
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt.executeBatch();
            knLogger.debug(methodName, "Exit:Executed query successfully");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while deleting ext contact -",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_EXT_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    /**
     * This method returns list od corporate Id where menList exist as external contact
     *
     * @param mdnList
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public List<Integer> getCorpIdList(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getCorpIdList(List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : mdnList,- ", mdnList == null ? mdnList : KnGDPRTemplate.mdnList(mdnList));
        Connection conn;
        Statement stmt = null;
        String query = null;
        ResultSet rs = null;
        Set<Integer> corpIdList = new HashSet<Integer>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.GET_EXTERNAL_CORP_FOR_MDNLIST);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedIdList(mdnList));
            stmt = conn.createStatement();
            knLogger.debug(methodName, "Executing query - ", query);
            rs = stmt.executeQuery(query);
            knLogger.debug(methodName, "Exit: Executed query successfully");
            while (rs.next()) {
                corpIdList.add(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while deleting ext contact -",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_EXT_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
        }
        knLogger.debug(methodName, "corpIdList" + corpIdList.size());
        return new ArrayList<Integer>(corpIdList);
    }

    /**
     * @param corpId       corporate id for which subscriber count need to fetch
     * @param contactType  1 if the subscriber is External Subscriber and 2 if the subscriber is NNI Subscriber
     * @param persisterTxn
     * @return count for External Subscriber OR NNI Subscriber depending on passed contactType
     * @throws KnDAOException
     */
    public int getExternalSubscriberCount(int corpId,
                                          int contactType, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getExternalSubscriberCount(int,int,boolean,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : corpId=", corpId, "contactType=" + contactType, " readOnly :", readOnly);
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        int count = 0;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_EXTERNAL_SUBSCRIBER_COUNT);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, corpId);
            pstmt.setInt(2, contactType);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to fetch external contact count",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        knLogger.debug(methodName, "EXIT : external contact count=", count);
        return count;
    }


    /**
     * @param corpId       corporate id
     * @param filterType   possible value 0/1/2/3 0[All], other then 0 with pagination
     * @param fetchSize    number of Row to fetch
     * @param nextToken    the page number to fetch
     * @param sortType     By [1]MDN OR By [2]NAME, 0 means no sorting
     * @param persisterTxn
     * @return List of External Subscriber
     * @throws KnDAOException
     */
    public List<KnExtSubsDetailsDTO> getExternalSubscriberList(int corpId, Integer filterType,
                                                               Integer fetchSize, Integer nextToken, Integer sortType, boolean readOnly,
                                                               KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getExternalSubscriberList(int,Integer,Integer,Integer, Integer,boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : CorpId -  ", corpId, " readOnly :", readOnly);
        //TODO :REFACT check for unused code and refactoring
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String query = null;
        List<KnExtSubsDetailsDTO> subscribersList = new ArrayList<KnExtSubsDetailsDTO>();
        conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();

            if (filterType != 0) {
                query = queryMapper.getQuery(GET_EXTERNAL_CONTACT_PAGINATED);
                if (sortType == 0) {
                    query = replaceContactWithValue(query, "ORDER BY SORT_TYPE", "");
                } else {
                    query = replaceContactWithValue(query, "SORT_TYPE", sortType == 1 ? "CAST(CONTACTMDN AS NUMBER)" : "CONTACTNAME");
                }
                pStmt = conn.prepareStatement(query);
                int startIndex = KnCorpUtil.getStartIndex(fetchSize, nextToken);
                int endIndex = KnCorpUtil.getEndIndex(fetchSize, nextToken);
                pStmt.setInt(1, startIndex);
                pStmt.setInt(2, endIndex);
                pStmt.setInt(3, corpId);
            } else {
                query = queryMapper.getQuery(KnPersisterConstants.GET_POC_EXTERNAL_CONTACT);
                pStmt = conn.prepareStatement(query);
                pStmt.setInt(1, corpId);
            }
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Query executed succesfully");
            while (rs.next()) {
                KnExtSubsDetailsDTO subscriberDTO = new KnExtSubsDetailsDTO();
                String mdn = rs.getString(1).trim();
                subscriberDTO.setMdn(mdn);
                //multilingual revert change
                if(null != rs.getString(2))
                {
                	try {
                		subscriberDTO.setName(new String(rs.getString(2).trim().getBytes("8859_1"),"UTF-8"));
					} catch (UnsupportedEncodingException e) {
						knLogger.error(methodName, "UTF-8 encoding exception - ", rs.getString(2), e);
					}
                }
                subscriberDTO.setType(com.kodiak.common.resources.KnConstants.EXTERNAL_CONTACT_SUBS_TYPE.KODIAK.value());
                subscribersList.add(subscriberDTO);
            }
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to retrieve Corp external PoC subscriber Info ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_EXT_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
        }
        knLogger.debug(methodName, "EXIT : external contact details list size  - ", subscribersList.size());
        return subscribersList;
    }

    /**
     * @param corpId       corporate id
     * @param filterType   possible value 0/1/2/3 0[All], other then 0 with pagination
     * @param fetchSize    number of Row to fetch
     * @param nextToken    the page number to fetch
     * @param sortType     By [1]MDN OR By [2]NAME, 0 means no sorting
     * @param persisterTxn
     * @return List of NNI Subscriber
     * @throws KnDAOException
     */
    public List<KnExtSubsDetailsDTO> getNniSubscriberList(int corpId, Integer filterType,
                                                          Integer fetchSize, Integer nextToken, Integer sortType, boolean readOnly,
                                                          KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getExternalSubscriberList(corpId, fetchSize, nextToken, sortType, maxContactsPerSubsc,readOnly,persisterTxn)";
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String query = null;
        List<KnExtSubsDetailsDTO> subscribersList = new ArrayList<KnExtSubsDetailsDTO>();
        conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            if (filterType != 0) {
                query = queryMapper.getQuery(GET_EXTERNAL_SUBSCRIBER_PAGINATED);
                if (sortType == 0) {
                    query = replaceContactWithValue(query, "ORDER BY SORT_TYPE", "");
                } else {
                    query = replaceContactWithValue(query, "SORT_TYPE", sortType == 1 ? "CAST(CONTACTMDN AS NUMBER)" : "CONTACTNAME");
                }
                pStmt = conn.prepareStatement(query);
                int startIndex = KnCorpUtil.getStartIndex(fetchSize, nextToken);
                int endIndex = KnCorpUtil.getEndIndex(fetchSize, nextToken);
                pStmt.setInt(1, startIndex);
                pStmt.setInt(2, endIndex);
                pStmt.setInt(3, corpId);
            } else {
                query = queryMapper.getQuery(KnPersisterConstants.GET_EXTERNAL_SUBSCRIBER);
                pStmt = conn.prepareStatement(query);
                pStmt.setInt(1, corpId);
            }
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                KnExtSubsDetailsDTO subscriberDTO = new KnExtSubsDetailsDTO();
                String mdn = rs.getString(1).trim();
                subscriberDTO.setMdn(mdn);
                //multilingual revert change
                if(rs.getString(2) != null){
                	try {
						subscriberDTO.setName(new String(rs.getString(2).getBytes("8859_1"),"UTF-8"));
					} catch (UnsupportedEncodingException e) {
						knLogger.error("UnsupportedEncodingException while parsing subscriber name ",e);
					}
                }
                int subsType = KnGeneralUtil.getSubsType(rs.getInt(3));
                subscriberDTO.setType(subsType);
                subscribersList.add(subscriberDTO);
            }
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to retrieve NNI subscriber Info ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_EXT_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
        }
        return subscribersList;
    }

    /**
     * This method return true is the request mdn exist as external contact in the corpid. This is read only method.
     *
     * @param mdn
     * @param corpid
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public boolean isExternalContExist(String mdn, int corpid, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "isExternalContExist()";
        knLogger.debug(methodName, "ENTRY : corpId=", corpid, "mdn=", KnGDPRTemplate.mdn(mdn));
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        boolean isexist = false;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(IS_EXTERNAL_SUBSCRIBER_EXIST);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, mdn);
            pstmt.setInt(2, corpid);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            if (rs.next()) {
                isexist = true;
            }
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to fetch external contact count",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        knLogger.debug(methodName, "EXIT : isexist ", isexist);
        return isexist;
    }

    /**
     * This method returns the external contact in the corporation
     *
     * @param corpId
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public List<KnCorpSubscriberDTO> getCorpExtContact(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getCorpExternalSubscriber(int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : CorpId -  ", corpId);
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String query = null;
        List<KnCorpSubscriberDTO> extCorpContactDetails = new ArrayList<>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.GET_CORP_EXT_CONTACT_DETAILS);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pStmt = conn.prepareStatement(query);
            pStmt.setInt(1, corpId);
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Query executed succesfully");
            while (rs.next()) {
                KnCorpSubscriberDTO subscriberDTO = new KnCorpSubscriberDTO();
                String mdn = rs.getString(1).trim();
                subscriberDTO.setMdn(mdn);
                //multilingual revert change
                String name = null;
                if(null != rs.getString(2))
                {
                    try {
                        name = new String(rs.getString(2).getBytes("8859_1"),"UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        knLogger.debug(methodName, "UTF-8 encoding exception -  ", rs.getString(2),e);
                    }
                }
                subscriberDTO.setName(name);
                subscriberDTO.setCorpId(rs.getInt(3));
                if(rs.getInt(4) == KnConstants.CONTACT_TYPE_EXTERNAL_CONTACT){
                    subscriberDTO.setContact_type(KnConstants.XCAP_DIFF_CONTACT_TYPE_EXTERNAL_CONTACT);
                }else{
                    subscriberDTO.setContact_type(KnConstants.XCAP_DIFF_CONTACT_TYPE_EXTERNAL_SUBSCRIBER);
                }
                extCorpContactDetails.add(subscriberDTO);
            }
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to retrieve Corp external PoC subscriber Info ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_EXT_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
        }
        knLogger.debug(methodName, "EXIT : external contact details list size  - ", extCorpContactDetails.size());
        return extCorpContactDetails;
    }
}