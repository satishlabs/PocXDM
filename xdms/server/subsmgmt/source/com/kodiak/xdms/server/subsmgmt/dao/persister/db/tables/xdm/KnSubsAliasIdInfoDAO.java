/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.subsmgmt.dao.persister.db.tables.xdm;

import com.kodiak.common.dao.*;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.subsmgmt.dto.common.KnSubsAliasInfoDTO;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvDAOSourceTypes;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnSubsAliasIdInfoDAO.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Venkata Sudhakar     May 7, 2021      11.3
 * <p/>
 * <p/>
 * KODIAK, 9th Floor, MFar Greenheart Phase IV
 * Manyata Tech Park, Nagawara
 * Bangalore - 560 045
 * www.kodiakptt.com
 * All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */


public class KnSubsAliasIdInfoDAO implements ITableDAO {

    private static final KnLogger knLogger = KnLogger.getLogger(KnSubsAliasIdInfoDAO.class);
    private String pttServerId;

    public static final String TABLE_NAME = "DG.POCSUBSCR_ALIASIDLIST";
    public static final String MDNLIST = "MDNLIST";
    public static final String ALIASIDINFOLIST = "ALIASIDINFOLIST";
    public static final String ALIASISSUERLIST = "ALIASISSUERLIST";
    public static final String MDN = "MDN";
    public static final String ALIASID = "ALIASID";
    public static final String ALIAS_ISSUER = "ALIAS_ISSUER";
    public static final String ALIASID_TYPE = "ALIASID_TYPE";
    public static final String QRY_GET_SUBS_ALIASID_INFO = "SELECT MDN,ALIASID,ALIAS_ISSUER,ALIASID_TYPE FROM "+TABLE_NAME + "" +
            " WHERE ALIASID IN ("+ALIASIDINFOLIST+") AND ALIAS_ISSUER IN ("+ALIASISSUERLIST+")";
    public static final String QRY_GET_ALL_SUBS_ALIASID_INFO_BY_ALIASID = "SELECT MDN,ALIASID,ALIAS_ISSUER,ALIASID_TYPE FROM "+TABLE_NAME + "" +
           " WHERE ALIASID IN ("+ALIASIDINFOLIST+") ";
    public static final String QRY_GET_SUBS_ALIASID_INFO_BY_MDN = "SELECT MDN,ALIASID,ALIAS_ISSUER,ALIASID_TYPE FROM "+TABLE_NAME + "" +
            " WHERE MDN IN ("+MDNLIST+")";
    public static final String QRY_INSERT_SUBS_ALIASID_INFO="INSERT INTO "+TABLE_NAME+"(MDN,ALIASID,ALIAS_ISSUER,ALIASID_TYPE) "+ "" +
            " VALUES(?,?,?,?)";
    public static final String QRY_DELETE_SUBS_ALIASID_INFO="DELETE FROM "+TABLE_NAME+" WHERE MDN = ? ";
    public static final String QRY_DELETE_SUBS_ALIASID_INFO_BY_ALIASID="DELETE FROM "+TABLE_NAME+" WHERE MDN = ? AND ALIASID IN ("+ALIASIDINFOLIST+") ";

    public KnSubsAliasIdInfoDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }

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

    public Map<String, List<KnSubsAliasInfoDTO>> selectSubsAliasIdInfo(Map<String, String> aliasIdIssuerMap, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnDAOException {

        final String methodName = "selectSubsAliasIdInfo(Map<String, String>,boolean, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "ENTRY: aliasIdIssuerMap - ", aliasIdIssuerMap, " readOnly :", readOnly);
        Map<String, List<KnSubsAliasInfoDTO>> subsAliasInfoMap = new HashMap<>();
        Connection conn;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            String query = QRY_GET_SUBS_ALIASID_INFO;
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            String aliasIdStr = KnGeneralUtil.formCommaSeperatedIdList(aliasIdIssuerMap.keySet());
            String aliasIdIsStr = KnGeneralUtil.formCommaSeperatedIdList(aliasIdIssuerMap.values());

            query = KnGeneralUtil.replaceContactWithValue(query, ALIASIDINFOLIST, aliasIdStr);
            query = KnGeneralUtil.replaceContactWithValue(query,ALIASISSUERLIST,aliasIdIsStr);

            stmt = conn.createStatement();
            knLogger.debug(methodName, "QUERY: Executing the Query - ", query);

            rs = stmt.executeQuery(query);
            while (rs.next()){
                String mdn = rs.getString(MDN).trim();
                KnSubsAliasInfoDTO aliasIdInfo = new KnSubsAliasInfoDTO();
                aliasIdInfo.setMdn(mdn);
                if(rs.getBytes(ALIASID)!=null) {
                    aliasIdInfo.setAliasId(new String(rs.getBytes(ALIASID), StandardCharsets.UTF_8));
                }
                aliasIdInfo.setAliasIdIssuer(rs.getString(ALIAS_ISSUER));
                aliasIdInfo.setAliasIdType((Integer)rs.getObject(ALIASID_TYPE));

                if(subsAliasInfoMap.get(mdn)!=null){
                    subsAliasInfoMap.get(mdn).add(aliasIdInfo);
                }else {
                    List<KnSubsAliasInfoDTO> aliasInfoDTOList = new ArrayList<KnSubsAliasInfoDTO>();
                    aliasInfoDTOList.add(aliasIdInfo);
                    subsAliasInfoMap.put(mdn,aliasInfoDTOList);
                }

            }
            knLogger.debug(methodName, "Query: Executed :", subsAliasInfoMap.size());

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to get subs camera info - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCR_ALIASIDLIST
                    , QRY_GET_SUBS_ALIASID_INFO);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
        }

        return subsAliasInfoMap;
    }

    /**
     *
     * @param mdnList
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<String, List<KnSubsAliasInfoDTO>> selectSubsAliasIdInfoByMdn(Collection<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException{
        final String methodName = "selectSubsAliasIdInfoByMdn(Collection<String>, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "ENTRY: mdnList - ", KnGDPRTemplate.mdnList(mdnList));
        Map<String,List<KnSubsAliasInfoDTO>> subsAliasInfoMap = new HashMap<>();
        Connection conn;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int index = 1;
        try {
            String query = QRY_GET_SUBS_ALIASID_INFO_BY_MDN;
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            //String mdnStr = KnGeneralUtil.formCommaSeperatedIdList(mdnList);
            //query = KnGeneralUtil.replaceContactWithValue(query, MDNLIST, mdnStr);
            query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(mdnList,query,"MDNLIST");
            stmt = conn.prepareStatement(query);
            knLogger.debug(methodName, "QUERY: Executing the Query - ", query);
            for(String mdn : mdnList){
                stmt.setString(index++, mdn);
            }
            rs = stmt.executeQuery();
            while (rs.next()){
                String mdn = rs.getString(MDN).trim();
                KnSubsAliasInfoDTO aliasIdInfo = new KnSubsAliasInfoDTO();
                aliasIdInfo.setMdn(mdn);
                if(rs.getBytes(ALIASID)!=null) {
                    aliasIdInfo.setAliasId(new String(rs.getBytes(ALIASID), StandardCharsets.UTF_8));
                }
                aliasIdInfo.setAliasIdIssuer(rs.getString(ALIAS_ISSUER));
                aliasIdInfo.setAliasIdType(rs.getInt(ALIASID_TYPE));

                if(subsAliasInfoMap.get(mdn)!=null){
                    subsAliasInfoMap.get(mdn).add(aliasIdInfo);
                }else {
                    List<KnSubsAliasInfoDTO> aliasInfoDTOList = new ArrayList<KnSubsAliasInfoDTO>();
                    aliasInfoDTOList.add(aliasIdInfo);
                    subsAliasInfoMap.put(mdn,aliasInfoDTOList);
                }

            }
            knLogger.debug(methodName, "Query: Executed :", subsAliasInfoMap.size());

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to get subs camera info - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCR_ALIASIDLIST
                    , QRY_GET_SUBS_ALIASID_INFO_BY_MDN);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            knLogger.debug(methodName, "EXIT ");
        }
        knLogger.debug(methodName,"EXIT : ");
        return subsAliasInfoMap;
    }

    public void insertIntoSubsAliasId(List<KnSubsAliasInfoDTO> aliasInfoList,String mdn,KnPersisterTxn persisterTxn)throws KnDAOException{
        final String methodName = "insertIntoSubsAliasId(List<KnSubsAliasInfoDTO>, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "ENTRY: aliasInfoList - ", aliasInfoList);
        Connection conn;
        PreparedStatement pstmt = null;
        try {
            String query = QRY_INSERT_SUBS_ALIASID_INFO;
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            knLogger.debug(methodName, "QUERY: Executing the Query - ", query);
            pstmt = conn.prepareStatement(query);
            for(KnSubsAliasInfoDTO aliasInfo : aliasInfoList){
                knLogger.debug(methodName, "obj ", aliasInfo);
                pstmt.setString(1, mdn);
                pstmt.setBytes(2, aliasInfo.getAliasId().getBytes());
                pstmt.setString(3, aliasInfo.getAliasIdIssuer());
                pstmt.setInt(4, aliasInfo.getAliasIdType());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            knLogger.debug(methodName, "Query: Executed : Successfully");

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to insert into alias info - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCR_ALIASIDLIST
                    , QRY_INSERT_SUBS_ALIASID_INFO);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    /**
     *
     * @param mdn
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void deleteSubsAliasId(String mdn, KnPersisterTxn persisterTxn)throws KnDAOException{
        final String methodName = "deleteSubsAliasId(String, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "ENTRY: mdn - ", KnGDPRTemplate.mdn(mdn));
        Connection conn;
        PreparedStatement pstmt = null;
        try {
            String query = QRY_DELETE_SUBS_ALIASID_INFO;
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            knLogger.debug(methodName, "QUERY: Executing the Query - ", query);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1,mdn);
            int count = pstmt.executeUpdate();
            knLogger.debug(methodName, "Query: Executed : Successfully ",count);

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to delete from alias info - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCR_ALIASIDLIST
                    , QRY_DELETE_SUBS_ALIASID_INFO);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public void deleteSubsAliasId(List<String> mdnList, KnPersisterTxn persisterTxn)throws KnDAOException{
        final String methodName = "deleteSubsAliasId(List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: mdn - ", KnGDPRTemplate.mdnList(mdnList));
        Connection conn;
        PreparedStatement pstmt = null;
        try {
            String query = "DELETE FROM DG.POCSUBSCR_ALIASIDLIST WHERE MDN = ?";
            //query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(mdnList,query,"MDNLIST");
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            knLogger.debug(methodName, "QUERY: Executing the Query - ", query);
            pstmt = conn.prepareStatement(query);
            for(String mdn : mdnList){
                pstmt.setString(1, mdn);
                pstmt.addBatch();
            }
            int count = pstmt.executeBatch().length;
            knLogger.debug(methodName, "Query: Executed : Successfully ",count);

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to delete from alias info - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCR_ALIASIDLIST
                    , QRY_DELETE_SUBS_ALIASID_INFO);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }
    public void deleteSubsAliasId(String mdn,List<KnSubsAliasInfoDTO> aliasList, KnPersisterTxn persisterTxn)throws KnDAOException{
        final String methodName = "deleteSubsAliasId(String, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "ENTRY: mdn - ", KnGDPRTemplate.mdn(mdn));
        Connection conn;
        PreparedStatement pstmt = null;
        try {
            String query = QRY_DELETE_SUBS_ALIASID_INFO_BY_ALIASID;
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            String aliasIdStr = formCommaSeperatedAliasIdList(aliasList);

            query = KnGeneralUtil.replaceContactWithValue(query, ALIASIDINFOLIST, aliasIdStr);

            knLogger.debug(methodName, "QUERY: Executing the Query - ", query);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1,mdn);
            //pstmt.set
            int count = pstmt.executeUpdate();
            knLogger.debug(methodName, "Query: Executed : Successfully ",count);

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to delete from alias info - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCR_ALIASIDLIST
                    , QRY_GET_ALL_SUBS_ALIASID_INFO_BY_ALIASID);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    private String formCommaSeperatedAliasIdList(List<KnSubsAliasInfoDTO> aliasList) {
        StringBuffer buffer = new StringBuffer(200);
        for (KnSubsAliasInfoDTO aliasId : aliasList) {
            buffer = buffer.append("'").append(aliasId.getAliasId()).append("',");
        }
        int indx = buffer.lastIndexOf(",");
        if (indx > 0) {
            buffer.deleteCharAt(indx);
        }
        return buffer.toString();
    }

    public  Map<String,List<KnSubsAliasInfoDTO>> selectSubsAliasIdInfoByAliasId(Map<String, String> aliasIdIssuerMap, KnPersisterTxn persisterTxn)
            throws KnDAOException {

        final String methodName = " selectSubsAliasIdInfoByAliasId(Map<String, String> aliasIdIssuerMap, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "ENTRY: aliasIdList - ", aliasIdIssuerMap.keySet());
        Map<String,List<KnSubsAliasInfoDTO>> aliasIdInfoMDNMap=new HashMap<>();
        Connection conn;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            String query = QRY_GET_ALL_SUBS_ALIASID_INFO_BY_ALIASID;
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            String aliasIdStr = KnGeneralUtil.formCommaSeperatedIdList(aliasIdIssuerMap.keySet());
            query = KnGeneralUtil.replaceContactWithValue(query, ALIASIDINFOLIST, aliasIdStr);
            stmt = conn.createStatement();
            knLogger.debug(methodName, "QUERY: Executing the Query - ", query);
            rs =  stmt.executeQuery(query);
            while(rs.next())
            {
                KnSubsAliasInfoDTO aliasIdInfo =new KnSubsAliasInfoDTO();
                String mdn= rs.getString(MDN).trim();
                if(rs.getBytes(ALIASID)!=null) {
                    aliasIdInfo.setAliasId(new String(rs.getBytes(ALIASID), StandardCharsets.UTF_8));
                }
                aliasIdInfo.setAliasIdIssuer(rs.getString(ALIAS_ISSUER));
                aliasIdInfo.setAliasIdType((Integer)rs.getObject(ALIASID_TYPE));

                if(aliasIdInfoMDNMap.get(mdn)!=null){
                    aliasIdInfoMDNMap.get(mdn).add(aliasIdInfo);
                }else {
                    List<KnSubsAliasInfoDTO> aliasInfoDTOList = new ArrayList<KnSubsAliasInfoDTO>();
                    aliasInfoDTOList.add(aliasIdInfo);
                    aliasIdInfoMDNMap.put(mdn,aliasInfoDTOList);
                }

            }
            knLogger.debug(methodName, "Query: Executed :",rs.getFetchSize());

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to get AliasID info - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCR_ALIASIDLIST
                    , QRY_GET_ALL_SUBS_ALIASID_INFO_BY_ALIASID);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
        }

        return aliasIdInfoMDNMap;
    }

}
