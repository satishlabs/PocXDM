/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.subsmgmt.dao.persister.db.tables.xdm;


import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.logger.KnLogger;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.utilities.featuresetupgrade.util.KnUpgardeFSConfig;
import com.kodiak.xdms.server.common.dao.persister.db.KnDBPersistenceException;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.subsmgmt.dto.common.KnPAMAccInfoDTO;
import com.kodiak.xdms.server.subsmgmt.dto.common.KnPAMSubsProfInfoDTO;
import com.kodiak.xdms.server.subsmgmt.dto.persistdat.KnPAMAccPersistDTO;
import com.kodiak.xdms.server.subsmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvDAOSourceTypes;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * *****************************************************************************
 * File name:   KnPAMSubscrProfInfoDAO
 * Subsystem:
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ajit Kumar            08/02/13       7.4
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
 * *******************************************************************************
 */

public class KnPAMSubscrProfInfoDAO implements ITableDAO {
	private static final KnLogger knLogger = KnLogger.getLogger(KnPAMSubscrProfInfoDAO.class);

    private String pttServerId;

    private static final String TABLE_NAME = "DG.PAMSUBSCRPROFILEINFO";

    private static final String PAMACCID = "PAMACCID";
    private static final String PROFILEID = "PROFILEID";
    private static final String PROFILENAME = "PROFILENAME";
    private static final String SUBSCRIBERFS = "SUBSCRIBERFS";
    private static final String SUBSCRIBERFS2 = "SUBSCRIBERFS2";
    private static final String PUBLICSUBSCRIPTIONTYPE = "PUBLICSUBSCRIPTIONTYPE";
    private static final String CORPSUBSCRIPTIONTYPE = "CORPSUBSCRIPTIONTYPE";
    private static final String CLIENT_TYPE = "CLIENT_TYPE";
    private static final String EXTCORPID = "EXTCORPID";
    private static final String CORPNAME = "CORPNAME";
    private static final String CREATIONTIME = "CREATIONTIME";
    private static final String LASTUPDATETIME = "LASTUPDATETIME";
    private static final String CORPID = "CORPID";
    private static final String IMEI = "IMEI";
    private static final String EMAIL = "EMAIL";
    private static final String TIERPKGCODE ="TIER_PKG_CODE"; 
    private static final String DATAPKGID ="DATA_PKG_ID"; 
    private static final String GET_PAM_ACCID_FOR_CORPID="SELECT PAMACCID FROM DG.PAMSUBSCRPROFILEINFO WHERE CORPID=?";//TODO
    private static final String UPDATE_PAMSUBSCR_CORPID="UPDATE DG.PAMSUBSCRPROFILEINFO SET CORPID=? WHERE CORPID=?";
    private static final String LICENSETYPE = "LICENSE_TYPE";
    private static final String SEGMENT_INDICATOR = "SEGMENT_INDICATOR";
    private static final String FEATURE_REL_VERSION = "FEATURE_REL_VERSION";
    private String currentFsVersion;
    public KnPAMSubscrProfInfoDAO(String pttServerId) {
        this.pttServerId = pttServerId;
        currentFsVersion= KnUpgardeFSConfig.getFsCurrentVersion();
    }

    /**
     * method to insert records for the table DG.PAMSUBSCRPROFILEINFO
     *
     * @param persistenceDTO IPersistenceDTO
     * @param persisterTxn   KnPersisterTxn
     * @throws KnDAOException
     *          DB Layer Exception
     */
    public void insert(IPersistenceDTO persistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "insert(IPersistenceDTO, KnPersiterTxn)";
        knLogger.entry(methodName, persistenceDTO, persisterTxn);
        Connection conn;
        PreparedStatement pStmt = null;
        String query = null;

        try {

            KnPAMAccInfoDTO pamAccInfoDTO = (KnPAMAccInfoDTO) persistenceDTO;
            KnPAMSubsProfInfoDTO pamSubsProfInfoDTO = pamAccInfoDTO.getProfileDetails();

            int pamAccId = pamSubsProfInfoDTO.getPamAccId();
            int profileId = pamSubsProfInfoDTO.getProfileId();
            String profileName = pamSubsProfInfoDTO.getProfileName();
            int pubSubsType = pamSubsProfInfoDTO.getPubSubsType();
            int corpSubsType = pamSubsProfInfoDTO.getCorpSubsType();
            int clientType = pamSubsProfInfoDTO.getClient_Type();
          //  String extCorpId = pamSubsProfInfoDTO.getExtCorpId();
           // String corpName = pamSubsProfInfoDTO.getCorpName();
            long creationTime = pamSubsProfInfoDTO.getCreationTime();
            long lastUpdateTime = pamSubsProfInfoDTO.getLastUpdateTime();
            int corpID = pamSubsProfInfoDTO.getCorpID();
            String imei = pamSubsProfInfoDTO.getImei();
            String email = pamSubsProfInfoDTO.getEmail();
            String tierPkg=pamSubsProfInfoDTO.getTierPkgCode();
            Integer dataPkgId=pamSubsProfInfoDTO.getDataPkgId();
            Integer licenseType=pamSubsProfInfoDTO.getLicenseType();
            String firstNetIndicator=pamSubsProfInfoDTO.getFirstNetIndicator();

            ArrayList<String> queryFields = new ArrayList<String>();

            queryFields.add(PAMACCID);
            queryFields.add(PROFILEID);
            queryFields.add(PROFILENAME);
            queryFields.add(SUBSCRIBERFS);
            queryFields.add(SUBSCRIBERFS2);
            queryFields.add(PUBLICSUBSCRIPTIONTYPE);
            queryFields.add(CORPSUBSCRIPTIONTYPE);
            queryFields.add(CLIENT_TYPE);
            queryFields.add(CORPID);
            queryFields.add(IMEI);
            queryFields.add(EMAIL);
            //queryFields.add(EXTCORPID);
          //  queryFields.add(CORPNAME);
            queryFields.add(CREATIONTIME);
            queryFields.add(LASTUPDATETIME);
            queryFields.add(TIERPKGCODE);
            queryFields.add(DATAPKGID);
            queryFields.add(LICENSETYPE);
            queryFields.add(SEGMENT_INDICATOR);
            queryFields.add(FEATURE_REL_VERSION);

            query = KnDbUtil.getInsertQuery(TABLE_NAME, queryFields);


            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);
            int columnIndex = 0;
            pStmt.setInt(++columnIndex, pamAccId);
            pStmt.setInt(++columnIndex, profileId);
            pStmt.setString(++columnIndex, profileName);
            pStmt.setLong(++columnIndex, KnGeneralUtil.convertHexStringToLong(pamSubsProfInfoDTO.getSubscriberFS2()));
            pStmt.setString(++columnIndex, KnGeneralUtil.getFeatureSet(pamSubsProfInfoDTO.getSubscriberFS2()) );
            pStmt.setInt(++columnIndex, pubSubsType);
            pStmt.setInt(++columnIndex, corpSubsType);
            pStmt.setInt(++columnIndex, clientType);
           // pStmt.setString(++columnIndex, extCorpId);
            //pStmt.setString(++columnIndex, corpName);
            pStmt.setInt(++columnIndex, corpID);
            pStmt.setString(++columnIndex, imei);
            pStmt.setString(++columnIndex, email);
            pStmt.setLong(++columnIndex, creationTime);
            pStmt.setLong(++columnIndex, lastUpdateTime);
            pStmt.setString(++columnIndex, tierPkg);
            pStmt.setInt(++columnIndex, dataPkgId);
            pStmt.setInt(++columnIndex, licenseType);
            pStmt.setString(++columnIndex, firstNetIndicator);
            pStmt.setString(++columnIndex, currentFsVersion);


            knLogger.debug( methodName, "QUERY: Executing - ", query, " with DTO - ", pamSubsProfInfoDTO
            );
            int result = pStmt.executeUpdate();
            knLogger.debug( methodName, "QUERY: Executed - ", result);

        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQL Exception - ", sqlE);
            throw KnDbUtil.processException(sqlE, "Failed to create PAM Subs Profile Info - " + sqlE.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.PAMSUBSCRPROFILEINFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }
        
        knLogger.exit(methodName);
    }

    /**
     * method to update records for the table DG.PAMSUBSCRPROFILEINFO
     *
     * @param persistenceDTO IPersistenceDTO
     * @param persisterTxn   KnPersisterTxn
     * @throws KnDAOException
     *          DB Layer Exception
     */
    public void update(IPersistenceDTO persistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "update(IPersistenceDTO, KnPersisterTxn)";
        knLogger.entry(methodName, persistenceDTO);
        Connection conn;
        PreparedStatement pStmt = null;
        String query = null;

        try {
            KnPAMAccInfoDTO pamAccInfoDTO = (KnPAMAccInfoDTO) persistenceDTO;
            KnPAMSubsProfInfoDTO pamSubsProfInfoDTO = pamAccInfoDTO.getProfileDetails();

            int pamAccId = pamSubsProfInfoDTO.getPamAccId();
            int profileId = pamSubsProfInfoDTO.getProfileId();
            String profileName = pamSubsProfInfoDTO.getProfileName();
            int pubSubsType = pamSubsProfInfoDTO.getPubSubsType();
            int corpSubsType = pamSubsProfInfoDTO.getCorpSubsType();
            int clientType = pamSubsProfInfoDTO.getClient_Type();
            String imei = pamSubsProfInfoDTO.getImei();
            String email = pamSubsProfInfoDTO.getEmail();
            long lastUpdateTime = pamSubsProfInfoDTO.getLastUpdateTime();
            String tierPkg=pamSubsProfInfoDTO.getTierPkgCode();
            Integer dataPkgId=pamSubsProfInfoDTO.getDataPkgId();
            String firstNetIndicator=pamSubsProfInfoDTO.getFirstNetIndicator();
            StringBuilder queryBuffer = new StringBuilder();
            queryBuffer.append("UPDATE ").append(TABLE_NAME).append(" SET ");

            queryBuffer.append(PROFILENAME).append("=?, ");
            queryBuffer.append(SUBSCRIBERFS).append("=?, ");
            queryBuffer.append(SUBSCRIBERFS2).append("=?, ");
            queryBuffer.append(PUBLICSUBSCRIPTIONTYPE).append("=?, ");
            queryBuffer.append(CORPSUBSCRIPTIONTYPE).append("=?, ");
            queryBuffer.append(CLIENT_TYPE).append("=?, ");
            if (imei != null) {
            	queryBuffer.append(IMEI).append("=?, ");            	
            }
            if (email != null) {
            	queryBuffer.append(EMAIL).append("=?, ");            	
            }
            if (tierPkg != null) {
            queryBuffer.append(TIERPKGCODE).append("=?, "); 
            }
            if (dataPkgId != null) {
            queryBuffer.append(DATAPKGID).append("=?, ");
            }
            if (firstNetIndicator != null) {
            queryBuffer.append(SEGMENT_INDICATOR).append("=?, ");
            }
            queryBuffer.append(LASTUPDATETIME).append("=?");
            queryBuffer.append(" WHERE ").append(PAMACCID).append("=? AND ").append(PROFILEID).append("=?");

            query = queryBuffer.toString();

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);
            int columnIndex = 0;
            if (profileName != null) {
                pStmt.setString(++columnIndex, profileName);
            } else {
                pStmt.setNull(++columnIndex, Types.VARCHAR);
            }
            pStmt.setLong(++columnIndex, KnGeneralUtil.convertHexStringToLong(pamSubsProfInfoDTO.getSubscriberFS2()));
            pStmt.setString(++columnIndex, KnGeneralUtil.getFeatureSet(pamSubsProfInfoDTO.getSubscriberFS2()));
            pStmt.setInt(++columnIndex, pubSubsType);
            pStmt.setInt(++columnIndex, corpSubsType);
            pStmt.setInt(++columnIndex, clientType);
            if (imei != null) {            	
            	pStmt.setString(++columnIndex, imei);
            }
            if (email != null) {
            	pStmt.setString(++columnIndex, email);            	
            }
			if (tierPkg != null) {
				if (!tierPkg.isEmpty()) {
					pStmt.setString(++columnIndex, tierPkg);
				}
				else {
					pStmt.setNull(++columnIndex, Types.VARCHAR);
				}
			}
            if (dataPkgId != null) {
            pStmt.setInt(++columnIndex, dataPkgId);
            }
            if (firstNetIndicator != null) {
            pStmt.setString(++columnIndex, firstNetIndicator);
            }
            pStmt.setLong(++columnIndex, lastUpdateTime);

            pStmt.setInt(++columnIndex, pamAccId);
            pStmt.setInt(++columnIndex, profileId);

            knLogger.debug( methodName, "QUERY: Executing - ", query, " with DTO - ", pamSubsProfInfoDTO
            );
            int result = pStmt.executeUpdate();
            knLogger.debug( methodName, "QUERY: Executed - ", result);

            knLogger.exit(methodName);

        } catch (SQLException sqlE) {
            knLogger.error( methodName, "SQL Exception - ", sqlE);
            throw KnDbUtil.processException(sqlE, "Failed to update PAM Subs Profile Info - " + sqlE.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.PAMSUBSCRPROFILEINFO, query);
        }  finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }
    }

    /**
     * method to delete records for the table DG.PAMSUBSCRPROFILEINFO
     *
     * @param persistenceDTO IPersistenceDTO
     * @param persisterTxn   KnPersisterTxn
     * @throws KnDAOException
     *          DB Layer Exception
     */
    public void delete(IPersistenceDTO persistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "delete(IPersistenceDTO, KnPersisterTxn)";
        knLogger.entry(methodName, persistenceDTO);
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        try {
            KnPAMAccInfoDTO pamAccInfoDTO = (KnPAMAccInfoDTO) persistenceDTO;
            KnPAMSubsProfInfoDTO pamSubsProfInfoDTO = pamAccInfoDTO.getProfileDetails();

            int pamAccId = pamSubsProfInfoDTO.getPamAccId();

            int profileId = pamSubsProfInfoDTO.getProfileId();

            query = "DELETE FROM " + TABLE_NAME + " WHERE " + PAMACCID + "=? AND " + PROFILEID + "=?  ";
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);
            pStmt.setInt(1, pamAccId);
            pStmt.setInt(2, profileId);

            knLogger.debug( methodName, "QUERY: Executing ", query, ", pamAccId - ", pamAccId, " profileId -", profileId);
            int result = pStmt.executeUpdate();
            knLogger.debug( methodName, "QUERY: Executed - ", result);

            knLogger.exit(methodName);
        } catch (SQLException sqlE) {
            knLogger.error( methodName, "SQL Exception - ", sqlE);
            throw KnDbUtil.processException(sqlE, "Failed to delete PAM Subs Profile Info - " + sqlE.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.PAMSUBSCRPROFILEINFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }
    }

    public Collection select(IPersistenceDTO persistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "select(IPersistenceDTO, KnPersisterTxn)";
        knLogger.info( methodName, "NOT IMPLEMENTED");
        return null;
    }

    /**
     * method to retrieve the DG.PAMSUBSCRPROFILEINFO
     *
     * @param pamAccId     String
     * @param persisterTxn KnPersisterTxn
     * @return KnIdsSubsProfileInfoDTO
     * @throws KnDAOException
     *          DB Exception
     */
    public KnPAMSubsProfInfoDTO getPAMSubsProfInfo(int pamAccId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getPAMSubsProfInfo(int, KnPersisterTxn";
        knLogger.entry(methodName, pamAccId);
        Connection conn;
        PreparedStatement pStmt = null;
        String query = null;
        ResultSet rs = null;
        KnPAMSubsProfInfoDTO pamSubsProfInfoDTO = null;
        try {
            StringBuilder queryBuffer = new StringBuilder();

            queryBuffer.append("SELECT ");
            queryBuffer.append(PAMACCID).append(", ")
                    .append(PROFILEID).append(", ")
                    .append(PROFILENAME).append(", ")
                    .append(SUBSCRIBERFS).append(", ")
                    .append(SUBSCRIBERFS2).append(", ")
                    .append(PUBLICSUBSCRIPTIONTYPE).append(", ")
                    .append(CORPSUBSCRIPTIONTYPE).append(", ")
                    .append(CLIENT_TYPE).append(", ")
                    .append(CORPID).append(", ")
                    .append(IMEI).append(", ")
                    .append(EMAIL).append(", ")
                    .append(CREATIONTIME).append(", ")
                    .append(TIERPKGCODE).append(", ")
                    .append(DATAPKGID).append(", ")
                    .append(LASTUPDATETIME).append(", ")
                    .append(LICENSETYPE).append(", ")
                    .append(SEGMENT_INDICATOR);
            queryBuffer.append(" FROM ").append(TABLE_NAME).append(" WHERE ").append(PAMACCID).append("=?");

            query = queryBuffer.toString();

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);
            pStmt.setInt(1, pamAccId);

            knLogger.debug( methodName, "QUERY: Executing - ", query, " with pamAccId - ", pamAccId);
            rs = pStmt.executeQuery();
            knLogger.debug( methodName, "QUERY: Executed - ", rs);
            if (rs.next()) {
                pamSubsProfInfoDTO = new KnPAMSubsProfInfoDTO();
                pamSubsProfInfoDTO.setPamAccId(rs.getInt(PAMACCID));
                pamSubsProfInfoDTO.setProfileId(rs.getInt(PROFILEID));
                pamSubsProfInfoDTO.setProfileName(rs.getString(PROFILENAME));
                String subsFs=rs.getString(SUBSCRIBERFS2)!=null?rs.getString(SUBSCRIBERFS2):KnGeneralUtil.convertLongToHexString(rs.getLong(SUBSCRIBERFS));
                pamSubsProfInfoDTO.setSubscriberFS2(subsFs);
                pamSubsProfInfoDTO.setPubSubsType(rs.getInt(PUBLICSUBSCRIPTIONTYPE));
                pamSubsProfInfoDTO.setCorpSubsType(rs.getInt(CORPSUBSCRIPTIONTYPE));
                pamSubsProfInfoDTO.setClient_Type(rs.getInt(CLIENT_TYPE));
              //  pamSubsProfInfoDTO.setExtCorpId(rs.getString(EXTCORPID));
               // pamSubsProfInfoDTO.setCorpName(rs.getString(CORPNAME));
                pamSubsProfInfoDTO.setCorpID(rs.getInt(CORPID));
                pamSubsProfInfoDTO.setImei(rs.getString(IMEI));
                pamSubsProfInfoDTO.setEmail(rs.getString(EMAIL));
                pamSubsProfInfoDTO.setCreationTime(rs.getLong(CREATIONTIME));
                pamSubsProfInfoDTO.setLastUpdateTime(rs.getLong(LASTUPDATETIME));
                pamSubsProfInfoDTO.setTierPkgCode(rs.getString(TIERPKGCODE));
                pamSubsProfInfoDTO.setDataPkgId(rs.getInt(DATAPKGID));
                pamSubsProfInfoDTO.setLicenseType(rs.getInt(LICENSETYPE));
                pamSubsProfInfoDTO.setFirstNetIndicator(rs.getString(SEGMENT_INDICATOR));
            } else {
                knLogger.debug( methodName, "PAM subs Profile does not Exist");
                throw new KnDBPersistenceException(KnErrorCodes.DAO.ROW_NOT_FOUND, "PAM Subs profile  Doesnt exist",
                        pttServerId, KnProvDAOSourceTypes.PAMSUBSCRPROFILEINFO, query);
            }


        } catch (SQLException sqlE) {
            knLogger.error( methodName, "SQL Exception - ", sqlE);
            throw KnDbUtil.processException(sqlE, "Failed to retrieve PAM Subs Profile Info - " + sqlE.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.PAMSUBSCRPROFILEINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStmt);
        }
        knLogger.exit(methodName, pamSubsProfInfoDTO);
        return pamSubsProfInfoDTO;
    }
    
    /**
     * method to retrieve the DG.PAMSUBSCRPROFILEINFO
     *
     * @param pamAccId     String
     * @param persisterTxn KnPersisterTxn
     * @return KnIdsSubsProfileInfoDTO
     * @throws KnDAOException
     *          DB Exception
     */
    public KnPAMSubsProfInfoDTO retrievePAMSubsProfInfo(int pamAccId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "retrievePAMSubsProfInfo(int, KnPersisterTxn";
        knLogger.entry(methodName, pamAccId);
        Connection conn;
        PreparedStatement pStmt = null;
        String query = null;
        ResultSet rs = null;
        KnPAMSubsProfInfoDTO pamSubsProfInfoDTO = null;
        try {
            StringBuilder queryBuffer = new StringBuilder();

            queryBuffer.append("SELECT ");
            queryBuffer.append(PAMACCID).append(", ")
                    .append(PROFILEID).append(", ")
                    .append(PROFILENAME).append(", ")
                    .append(SUBSCRIBERFS).append(", ")
                    .append(SUBSCRIBERFS2).append(", ")
                    .append(PUBLICSUBSCRIPTIONTYPE).append(", ")
                    .append(CORPSUBSCRIPTIONTYPE).append(", ")
                    .append(CLIENT_TYPE).append(", ")
                    .append(CORPID).append(", ")
                    .append(IMEI).append(", ")
                    .append(EMAIL).append(", ")
                    .append(CREATIONTIME).append(", ")
                    .append(TIERPKGCODE).append(", ")
                    .append(DATAPKGID).append(", ")
                    .append(LASTUPDATETIME).append(", ")
                    .append(LICENSETYPE).append(", ")
                    .append(SEGMENT_INDICATOR);
            queryBuffer.append(" FROM ").append(TABLE_NAME).append(" WHERE ").append(PAMACCID).append("=?");

            query = queryBuffer.toString();

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);
            pStmt.setInt(1, pamAccId);

            knLogger.debug( methodName, "QUERY: Executing - ", query, " with pamAccId - ", pamAccId);
            rs = pStmt.executeQuery();
            knLogger.debug( methodName, "QUERY: Executed - ", rs);
            if (rs.next()) {
                pamSubsProfInfoDTO = new KnPAMSubsProfInfoDTO();
                pamSubsProfInfoDTO.setPamAccId(rs.getInt(PAMACCID));
                pamSubsProfInfoDTO.setProfileId(rs.getInt(PROFILEID));
                pamSubsProfInfoDTO.setProfileName(rs.getString(PROFILENAME));
                String subsFs=rs.getString(SUBSCRIBERFS2)!=null?rs.getString(SUBSCRIBERFS2):KnGeneralUtil.convertLongToHexString(rs.getLong(SUBSCRIBERFS));
                pamSubsProfInfoDTO.setSubscriberFS2(subsFs);
                pamSubsProfInfoDTO.setPubSubsType(rs.getInt(PUBLICSUBSCRIPTIONTYPE));
                pamSubsProfInfoDTO.setCorpSubsType(rs.getInt(CORPSUBSCRIPTIONTYPE));
                pamSubsProfInfoDTO.setClient_Type(rs.getInt(CLIENT_TYPE));
              //  pamSubsProfInfoDTO.setExtCorpId(rs.getString(EXTCORPID));
               // pamSubsProfInfoDTO.setCorpName(rs.getString(CORPNAME));
                pamSubsProfInfoDTO.setCorpID(rs.getInt(CORPID));
                pamSubsProfInfoDTO.setImei(rs.getString(IMEI));
                pamSubsProfInfoDTO.setEmail(rs.getString(EMAIL));
                pamSubsProfInfoDTO.setCreationTime(rs.getLong(CREATIONTIME));
                pamSubsProfInfoDTO.setTierPkgCode(rs.getString(TIERPKGCODE));
                pamSubsProfInfoDTO.setDataPkgId(rs.getInt(DATAPKGID));
                pamSubsProfInfoDTO.setLastUpdateTime(rs.getLong(LASTUPDATETIME));
                pamSubsProfInfoDTO.setLicenseType(rs.getInt(LICENSETYPE));
                pamSubsProfInfoDTO.setFirstNetIndicator(rs.getString(SEGMENT_INDICATOR));
            } 

        } catch (SQLException sqlE) {
            knLogger.error( methodName, "SQL Exception - ", sqlE);
            throw KnDbUtil.processException(sqlE, "Failed to retrieve PAM Subs Profile Info - " + sqlE.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.PAMSUBSCRPROFILEINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStmt);
        }
        knLogger.exit(methodName, pamSubsProfInfoDTO);
        return pamSubsProfInfoDTO;
    }

    public void updatePAMSubsProfCorpId(IPersistenceDTO persistenceDTO,int oldCorpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName="updatePAMSubsProfCorpId(IPersistenceDTO,int)";
        knLogger.info(methodName,"ENTRY : ",persistenceDTO," ,oldCorpId",oldCorpId);
        KnPAMAccPersistDTO pamAccPersistDTO = (KnPAMAccPersistDTO) persistenceDTO;
        int newCorpId = pamAccPersistDTO.getProfileDetails().getCorpID();
        Connection conn;
        PreparedStatement pStmt = null;
        String query = null;
        ResultSet rs = null;
        int isSuccess=0;
        try {
            query = UPDATE_PAMSUBSCR_CORPID;
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);
            pStmt.setInt(1, newCorpId);
            pStmt.setInt(2,oldCorpId);
            knLogger.debug( methodName, "QUERY: Executing - ", query);
            isSuccess=pStmt.executeUpdate();
            knLogger.debug( methodName, "QUERY: Executed - ", rs);

        } catch (SQLException sqlE) {
            knLogger.error( methodName, "SQL Exception - ", sqlE);
            throw KnDbUtil.processException(sqlE, "Failed to update pam sub corpid - " + sqlE.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.PAMSUBSCRPROFILEINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStmt);
        }
        knLogger.debug(methodName, "EXIT",isSuccess);
    }

    public List<Integer> retrievePAMAccIdForCorpId(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "retrievePAMAccIdForCorpId(int)";
        knLogger.info(methodName, "corpId", corpId);
        Connection conn;
        PreparedStatement pStmt = null;
        String query = null;
        ResultSet rs = null;
        List<Integer> pamAccIdList=new ArrayList<>();
        try {
            query = GET_PAM_ACCID_FOR_CORPID;
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);
            pStmt.setInt(1, corpId);
            knLogger.debug( methodName, "QUERY: Executing - ", query);
            rs = pStmt.executeQuery();
            knLogger.debug( methodName, "QUERY: Executed - ", rs);
            while (rs.next()) {
                pamAccIdList.add(rs.getInt(PAMACCID));
            }
        } catch (SQLException sqlE) {
            knLogger.error( methodName, "SQL Exception - ", sqlE);
            throw KnDbUtil.processException(sqlE, "Failed to retrieve PAM Acc List for corpid - " + sqlE.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.PAMSUBSCRPROFILEINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStmt);
        }
        knLogger.debug(methodName, "EXIT",pamAccIdList);
        return pamAccIdList;
    }

}
