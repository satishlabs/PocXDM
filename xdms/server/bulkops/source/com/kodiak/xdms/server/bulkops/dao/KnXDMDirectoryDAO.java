/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/******************************************************************************
 * File name:   KnXDMDirectoryDAO.java
 * Subsystem:   Server Common
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ravi Shanker .P       Dec 15, 2010       7.0
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
 * *******************************************************************************/
package com.kodiak.xdms.server.bulkops.dao;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnDbUtil;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.bulkops.KnBulkOpsException;
import com.kodiak.xdms.server.bulkops.utils.KnBulkOpsDBUtil;
import com.kodiak.xdms.server.common.resources.KnDAOSourceTypes;

import java.util.*;

public class KnXDMDirectoryDAO {
    private static final KnLogger knLogger = KnLogger.getLogger(KnXDMDirectoryDAO.class);
    private final KnBulkOpsDBUtil bulkOpsDBUtil;

    public KnXDMDirectoryDAO() throws KnBulkOpsException {
        bulkOpsDBUtil = KnBulkOpsDBUtil.getInstance();
    }

    public static final String QRY_SELECT_ETAG = "SELECT MDN, ETAG FROM DG.XDM_DIRECTORY WHERE MDN IN ";
    public static final String QRY_UPDATE_LIST_OF_MDN_ETAG = "UPDATE DG.XDM_DIRECTORY SET ETAG = ETAG + 1 WHERE MDN = ?";


    /**
     * Bulk fetch current directory document etags for the given MDNs using BulkOps DB util.
     * Executes one IN-clause query and returns a map of MDN -> etag.
     *
     * @param mdns         List of MDNs
     * @param persisterTxn Transaction (not used; util manages its own txn)
     * @return Map of MDN to current etag
     * @throws KnDAOException if database operation fails
     */
    public Map<String, Integer> getCurrentEtagsForDirDoc(List<String> mdns, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getCurrentEtagsForDirDoc(List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : mdns -> ", KnGDPRTemplate.mdnList(mdns));
        Map<String, Integer> etagMap = new HashMap<>();
        if (mdns == null || mdns.isEmpty()) {
            return etagMap;
        }

        bulkOpsDBUtil.executeInClauseQuery(
                QRY_SELECT_ETAG,
                mdns,
                rs -> {
                    while (rs.next()) {
                        String mdn = rs.getString(1);
                        int etag = rs.getInt(2);
                        etagMap.put(mdn, etag);
                    }
                    return null;
                },
                persisterTxn,
                KnDBConst.DataStores.XDM_SHARED_DATA

        );
        knLogger.debug(methodName, "EXIT : etagMap size -> ", etagMap.size());
        return etagMap;
    }

    /**
     * Bulk update etags for directory documents of given MDNs using BulkOps DB util.
     * Increments ETAG by 1 for each MDN in batch operation.
     *
     * @param mdnList      List of MDNs to update
     * @param persisterTxn Database transaction
     * @throws KnDAOException if database operation fails
     */
    public void updateEtagForDirDocOfMdnList(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateEtagForDirDocOfMdnList(List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : mdnList: ", KnGDPRTemplate.mdnList(mdnList));

        if (mdnList == null || mdnList.isEmpty()) {
            knLogger.debug(methodName, "No MDNs to update");
            return;
        }

        try {
            int[] results = bulkOpsDBUtil.executeBatchUpdate(
                    QRY_UPDATE_LIST_OF_MDN_ETAG,
                    mdnList,
                    (pStatement, mdn) -> pStatement.setString(1, mdn),
                    persisterTxn,
                    KnDBConst.DataStores.XDM_SHARED_DATA
            );

            knLogger.debug(methodName, "Batch update completed with ", results.length, " results");
            int updatedCount = 0;
            for (int result : results) {
                if (result > 0 || result == java.sql.Statement.SUCCESS_NO_INFO) {
                    updatedCount++;
                }
            }

            knLogger.info(methodName, "Updated Etag for ", updatedCount, " MDNs: ", KnGDPRTemplate.mdnList(mdnList));
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception during bulk update - ", e);
            throw KnDbUtil.processException(e, "Failed to update Etag for mdn - " + e.getMessage(),
                    null, KnDAOSourceTypes.XDM_DIRECTORY, QRY_UPDATE_LIST_OF_MDN_ETAG);
        }
    }

}