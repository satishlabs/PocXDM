/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:
 * Subsystem:  POC
 * <p/>
 * Name                   Date         Release
 * -------------------- ------------ -------------------------------------
 * Sanjiv K Acharyya     11/4/14         7.7.0
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
package com.kodiak.xdms.server.common.dao.persister.db.tables.xdm;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.common.KnExtProfileDetails;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.resources.KnDAOSourceTypes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * This DAo class is used for any operation with DG.ExtSubscrProfileInfo table.
 */
public class KnExtSubscrProfileInfoDAO implements ITableDAO {
    private static final KnLogger knLogger = KnLogger.getLogger(KnExtSubscrProfileInfoDAO.class);

    private String pttServerId;

    public KnExtSubscrProfileInfoDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    @Override
    public void insert(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "insert(IPesistenceDTO, KnPesisterTxn)";
        knLogger.debug( methodName, "Method not Implemented");
    }

    @Override
    public void update(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "update(IPesistenceDTO, KnPesisterTxn)";
        knLogger.debug( methodName, "Method not Implemented");
    }

    @Override
    public void delete(IPersistenceDTO persistencDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "delete(IPesistenceDTO, KnPesisterTxn)";
        knLogger.debug( methodName, "Method not Implemented");
    }

    @Override
    public Collection select(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "select(IPesistenceDTO, KnPesisterTxn)";
        knLogger.debug( methodName, "Method not Implemented");
        return null;
    }

    public Map<Integer, KnExtProfileDetails> getExtProfileDetailsMap(KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getExtProfileDetailsMap(persisterTxn)";
        knLogger.debug( methodName, "ENTRY :");
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        Map<Integer, KnExtProfileDetails> extProfileDetailsMap = new HashMap<Integer, KnExtProfileDetails>(5);
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, false);
            query = "SELECT PROFILE_ID, DEFAULT_PROF, PROFILE_TYPE, PROFILE_NAME, EXT_SUBS_FEATURES, INIT_PR_STATE FROM DG.ExtSubscrProfileInfo;";
            pstmt = conn.prepareStatement(query);
            knLogger.debug( methodName, "Executing query - " + query);
            rs = pstmt.executeQuery();
            knLogger.debug( methodName, "Successfully Executed query - ");
            while (rs.next()) {
                KnExtProfileDetails extProfileDetails = new KnExtProfileDetails();
                int profileId = KnGeneralUtil.getSubsType(rs.getInt(1));
                extProfileDetails.setProfileId(profileId);
                int isDefault = rs.getInt(2);
                if(isDefault == 1){
                    extProfileDetails.setDefaultProfile(Boolean.TRUE);
                }
                else {
                    extProfileDetails.setDefaultProfile(Boolean.FALSE);
                }
                extProfileDetails.setProfileType(rs.getInt(3));
                extProfileDetails.setProfileName(rs.getString(4));
                Map<Integer, Boolean> featureBitmap = prepareFeatureSetMap(rs.getLong(5));
                extProfileDetails.setFeatureSetMap(featureBitmap);
                extProfileDetailsMap.put(profileId, extProfileDetails);
            }
            knLogger.info( methodName, "External Profile found - " + extProfileDetailsMap);
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "SQLException occured - ", pttServerId,
                    KnDAOSourceTypes.XDM_EXTSUBSCR_PROFILE, query);

        } catch (Exception e) {
            throw KnDbUtil.processException(e, "Unexpected Exception occured  - ", pttServerId,
                    KnDAOSourceTypes.XDM_EXTSUBSCR_PROFILE, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        return extProfileDetailsMap;
    }

    private Map<Integer, Boolean> prepareFeatureSetMap(long featureBit){
        String methodName = "prepareFeatureSetMap(long)";
        knLogger.debug( methodName, "ENTRY - ", featureBit);
        List<Byte> bitList = new ArrayList<Byte>(6);
        bitList.add((byte)5);
        bitList.add((byte)9);
        bitList.add((byte)14);
        bitList.add((byte)15);
        bitList.add((byte)22);
        bitList.add((byte)23);
        bitList.add((byte)24);
        bitList.add((byte)26);
        Map<Integer, Boolean> bitMap = new HashMap<Integer, Boolean>(6);
        for (Byte i : bitList){
        	boolean flag = KnGeneralUtil.getFeatureBitValue(KnGeneralUtil.convertLongToHexString(featureBit), i);
            bitMap.put(Integer.valueOf(i), flag);
        }
        knLogger.debug( methodName, "bitMap - ", bitMap);
       return bitMap;
    }
}
