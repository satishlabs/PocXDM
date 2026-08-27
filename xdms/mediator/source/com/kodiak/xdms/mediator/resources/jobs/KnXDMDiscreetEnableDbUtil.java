/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.mediator.resources.jobs;

import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnDbUtil;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.xdms.server.common.dao.persister.db.KnQueryMapper;

import java.sql.*;
import java.util.*;

public class KnXDMDiscreetEnableDbUtil {

    private static final KnLogger knLogger = KnLogger.getLogger(KnXDMDiscreetEnableDbUtil.class);
    private final String className = KnXDMDiscreetEnableDbUtil.class.getName();
    private String pttServerId;
    private static final String TABLENAME = "DG.MCPTT_PERM_INFO";
    private static final String SELECT_QUERY = "SELECT TRIM(TARGET_MDN) , PERM_BITSET FROM " + TABLENAME + " WHERE DISCREET_ENABLED = 1";
    private static final String SELECT_TARGETMDN_QUERY = "SELECT TRIM(MDN) FROM DG.POCSUBSCRINFO WHERE DISCREET_ENABLED = 1";
    private static final String UPDATE_QUERY = "UPDATE DG.POCSUBSCRINFO SET DISCREET_ENABLED = 0 WHERE MDN IN (MDNLIST)";
    public static final String MDNLIST = "MDNLIST";

    public KnXDMDiscreetEnableDbUtil() {
        this.pttServerId = KnDbUtil.getDBConfigInfo().getLocalPttId();
    }

    public void disableDiscreetEnable(KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "disableDiscreetEnable(KnPersisterTxn)";

        Connection conn = null;
        PreparedStatement pStmt = null;
        PreparedStatement pStmt1 = null;
        ResultSet rs = null;
        ResultSet rs1 = null;
        Set<String> mdnList = new HashSet<>();
        Set<String> discreetEnableMdnList = new HashSet<>();

        knLogger.info(methodName, "ENTRY : SELECT MDN details");

        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pStmt = conn.prepareStatement(SELECT_QUERY);
            knLogger.debug(methodName, "QUERY: Executing", SELECT_QUERY);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "QUERY: Executed");

            while (rs.next()) {
                long permBitLongVal = rs.getLong(2);
                BitSet permBitSet = convertLongToBitSet(permBitLongVal);
                boolean discreetListener = permBitSet.get(1);
                if (discreetListener)
                    mdnList.add(rs.getString(1).trim());
            }
            knLogger.debug(methodName, " MDN List with discreet_enable = false and discreetListener is enable -", KnGDPRTemplate.mdnList(mdnList));


            knLogger.debug(methodName, "QUERY: Executing", SELECT_TARGETMDN_QUERY);
            pStmt1 = conn.prepareStatement(SELECT_TARGETMDN_QUERY);
            rs1 = pStmt1.executeQuery();
            knLogger.debug(methodName, "QUERY: Executed");
            while (rs1.next()) {
                    discreetEnableMdnList.add(rs1.getString(1).trim());
            }
            knLogger.debug(methodName, " MDN List with discreet_enable = true in pocsubscrinfo table -", KnGDPRTemplate.mdnSet(discreetEnableMdnList));

            discreetEnableMdnList.removeAll(mdnList);

            knLogger.info(methodName, " final list by removing mdnList-", KnGDPRTemplate.mdnSet(discreetEnableMdnList));


            if (!discreetEnableMdnList.isEmpty()) {
                String query = UPDATE_QUERY;
                int index = 1;
                //query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedIdList(discreetEnableMdnList));
                query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(discreetEnableMdnList,query,"MDNLIST");
                pStmt = conn.prepareStatement(query);
                for(String mdn : discreetEnableMdnList){
                    pStmt.setString(index++,mdn);
                }
                pStmt.executeUpdate();
                knLogger.debug(methodName, " update query", query);
            } else {
                knLogger.debug(methodName, " MDN List is empty");
            }


        } catch (KnDAOException dbConne) {
            knLogger.error(methodName, "DAO Exception occurred");
            throw dbConne;
        } catch (SQLException e) {
            knLogger.error(methodName, "SQL Exception occurred");
            throw KnDbUtil.processException(e, "Failed to select MDN details - "+  e.getMessage(), pttServerId, TABLENAME, SELECT_QUERY);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to select MDN details- " + e.getMessage(), pttServerId, TABLENAME, SELECT_QUERY);
        }
        finally {
            KnDbUtil.closeStatement(pStmt);
            KnDbUtil.closeStatement(pStmt1);
        }

        knLogger.info(methodName, "EXIT : Updated MDN discreet enable flag");


    }

    private BitSet convertLongToBitSet(long longValue) {
        String methodName = "convertLongToBitSet(long)";
        knLogger.debug(methodName, "ENTRY: Received long value to convert bit set is - ", longValue);

        long value = longValue;

        BitSet bitSet = new BitSet(Long.SIZE);
        int index = 0;
        while (value != 0) {
            if (value % 2L != 0) {
                bitSet.set(index);
            }
            ++index;
            value = value >>> 1;
        }
        knLogger.debug(methodName, "EXIT: Generated BitSet - ", bitSet);
        return bitSet;

    }

    public static String replaceContactWithValue(String str, String constant, String value) {
        String finalStr = "";
        if (str == null || str.trim().equals("")) {
            return finalStr;
        }
        finalStr = str.replaceAll(constant, value);
        return finalStr;
    }

    public static String formCommaSeperatedIdList(Collection<String> collectionStr) {
        StringBuffer buffer = new StringBuffer(200);
        for (String mdn : collectionStr) {
            buffer = buffer.append("'").append(mdn).append("',");
        }
        int indx = buffer.lastIndexOf(",");
        if (indx > 0) {
            buffer.deleteCharAt(indx);
        }
        return buffer.toString();
    }

}