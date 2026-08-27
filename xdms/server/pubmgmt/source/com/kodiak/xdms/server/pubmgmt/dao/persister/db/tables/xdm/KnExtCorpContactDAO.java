/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.dao.persister.db.tables.xdm;

import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.pubmgmt.dto.common.KnMemberDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

public class KnExtCorpContactDAO  implements ITableDAO {

    private static final KnLogger knLogger = KnLogger.getLogger(KnCorporateInfoDAO.class);

    public static final String TABLENAME = "DG.EXTCORPCONTACT";
    public String pttServerId = null;

    public KnExtCorpContactDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    public static final String QRY_SELECT_CORP_EXT_CONTACT = "SELECT CONTACTMDN FROM " + TABLENAME +" WHERE CORPID = ? AND CONTACTMDN IN ";
    public static final String QRY_SELECT_CORP_EXT_CONTACT_LIST = "SELECT CORPID, CONTACTMDN, CONTACTNAME, CONTACTCORPID, CONTACT_TYPE, EXTCONTACTNAME FROM " + TABLENAME +" WHERE CORPID = ? AND CONTACTMDN IN (CONTACTMDNLIST)";

    public static final String IS_EXTERNAL_SUBSCRIBER_EXIST = "SELECT CONTACTMDN FROM DG.EXTCORPCONTACT WHERE CONTACTMDN = ? AND CORPID = ?";

    @Override
    public void insert(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {

    }

    @Override
    public void update(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {

    }

    @Override
    public void delete(IPersistenceDTO persistencDTO, KnPersisterTxn persistTxn) throws KnDAOException {

    }

    @Override
    public Collection select(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        return null;
    }

    public List<String> getExtContactList(List<String> mdnList, int corpid, KnPersisterTxn persistTxn) throws KnDAOException {

        String methodName = "getExtContactList(String, int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :-> ",KnGDPRTemplate.mdnList(mdnList) , ", corpId - ", corpid);
        Connection conn;
        PreparedStatement statement = null;
        ResultSet rs = null;
        String query = null;
        int index = 2;
        List<String> validExtContList = new ArrayList<>();
        try {
            //query = KnDbUtil.replaceValInQry(QRY_SELECT_CORP_EXT_CONTACT, corpid);
            query = QRY_SELECT_CORP_EXT_CONTACT;
            /* StringBuffer buffer = new StringBuffer(200);
            buffer.append(query).append(KnDbUtil.convertListToStringBuffer(mdnList));
            query = buffer.toString(); */
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(mdnList,query,"MDNLIST");
            statement = conn.prepareStatement(query);
            statement.setInt(1,corpid);
            for(String mdn : mdnList){
                statement.setString(index++,mdn);
            }
            knLogger.debug( methodName, "QUERY : Executing " , query );
            rs = statement.executeQuery();
            knLogger.debug( methodName, "QUERY : Excecuted.");
            if (rs.next()) {
                validExtContList.add(rs.getString(1).trim());
            }

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve count for dirdoc - " + e.getMessage(),
                    pttServerId, TABLENAME, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(statement);
        }
        knLogger.debug(methodName, "Returning mdnList - ", KnGDPRTemplate.mdnList(mdnList));
        return validExtContList;
    }

    public Map<String, KnMemberDTO> getExtContactListByContactMDN(List<String> extContactList, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getExtContactList(String, int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :-> ", extContactList, ", corpId - ", corpId);
        Connection conn;
        PreparedStatement statement = null;
        ResultSet rs = null;
        String query = null;
        int index = 2;
        Map<String, KnMemberDTO> memberDTOMap = new HashMap<>();
        try {
            /*query = KnDbUtil.replaceValInQry(QRY_SELECT_CORP_EXT_CONTACT_LIST, corpId);
            StringBuffer buffer = new StringBuffer(200);
            buffer.append(query).append(KnDbUtil.convertListToStringBuffer(extContactList));
            query = buffer.toString(); */
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            query = QRY_SELECT_CORP_EXT_CONTACT_LIST;
            query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(extContactList,query,"CONTACTMDNLIST");
            statement = conn.prepareStatement(query);
            statement.setInt(1,corpId);
            for(String extCont : extContactList){
                statement.setString(index++,extCont);
            }
            knLogger.debug( methodName, "QUERY : Executing " , query );
            rs = statement.executeQuery();
            knLogger.debug( methodName, "QUERY : Excecuted.");
            while (rs.next()) {
                KnMemberDTO memberDTO = new KnMemberDTO();
                memberDTO.setMemberMdn(rs.getString("CONTACTMDN").trim());
                memberDTO.setMemberName(
                        new String(rs.getString("CONTACTNAME").trim().getBytes("ISO-8859-1"), "UTF-8")
                );
                memberDTO.setCorpId(rs.getInt("CONTACTCORPID"));
                memberDTOMap.put(rs.getString("CONTACTMDN").trim(),memberDTO);
            }
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve count for dirdoc - " + e.getMessage(),
                    pttServerId, TABLENAME, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(statement);
        }
        knLogger.debug(methodName, "Returning memberDTOMap - ", memberDTOMap);
        return memberDTOMap;
    }

    public boolean isExternalContExist(String mdn, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "isExternalContExist(String, int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :-> mdn ", KnGDPRTemplate.contact(mdn), ", corpId - ", corpId);
        Connection conn;
        PreparedStatement statement = null;
        ResultSet rs = null;
        String query = null;
        boolean isexist = false;
        try {

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            query = IS_EXTERNAL_SUBSCRIBER_EXIST;

            statement = conn.prepareStatement(query);
            statement.setString(1, mdn);
            statement.setInt(2, corpId);
            knLogger.debug( methodName, "QUERY : Executing " , query );
            rs = statement.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            if (rs.next()) {
                isexist = true;
            }
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve isExternalContExist - " + e.getMessage(),
                    pttServerId, TABLENAME, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(statement);
        }
        knLogger.debug(methodName, "Returning isExternalContExist - ", isexist);
        return isexist;
    }
}
