/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.auditjobs.common;

import java.util.List;

import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.statusmgr.KnStatusManagerClient;
import com.kodiak.utilities.statusmgr.utils.KnStatusMgrConstants;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;

public class KnXDMSMcsIdAuditThread implements Runnable {

    private static final KnLogger knLogger = KnLogger.getLogger(KnXDMSMcsIdAuditThread.class);
    private KnGenInfoUtil genInfoUtil = KnGenInfoUtil.getInstance();

    @Override
    public void run() {
        String methodName = "run";
        KnPersisterTxn persisterTxn = null;
        knLogger.info(methodName, "Status :", KnStatusManagerClient.getCurrentState());
        boolean mcsIdNullMdnExist = true;
        while (mcsIdNullMdnExist) {
            try {
                Thread.sleep(5000);
                knLogger.info(methodName, "After waiting 5 secs, status is :", KnStatusManagerClient.getCurrentState());
                if (KnStatusManagerClient.getCurrentState() == KnStatusMgrConstants.CARD_STATES.ACTIVE) {
                    persisterTxn = KnPersisterTxn.getPersisterTxn();
                    persisterTxn.open();
                    List<String> mcsIdNullMdnList = genInfoUtil.getMcsIdNullMdnList(persisterTxn);
                    knLogger.info(methodName, "MDN list size whose MCS Ids are null--> ", "MdnListSize-->",
                            mcsIdNullMdnList.size());
                    knLogger.debug(methodName, "Retrived MDNs whose MCS Ids are null--> ", "MdnListSize-->",
                            mcsIdNullMdnList.size(), "MDNs-->", mcsIdNullMdnList);
                    if (mcsIdNullMdnList != null && !mcsIdNullMdnList.isEmpty()) {
                        genInfoUtil.updateMcsIdForMdnList(mcsIdNullMdnList, persisterTxn);
                        knLogger.info(methodName, "McsIds updated successfully");
                    }
                    if (mcsIdNullMdnList.isEmpty()) {
                        mcsIdNullMdnExist = false;
                    }
                    persisterTxn.save();
                }
            } catch (Exception e) {
                knLogger.error(methodName, "Exception occured during KnXDMSMcsIdAudit ", e);
                rollback(persisterTxn);
            }
        }
        knLogger.info(methodName, "Exited KnXDMSMcsIdAuditThread SUCCESSFULLY");
    }

    private void rollback(KnPersisterTxn txn) {
        try {
            txn.rollback();
        } catch (Exception e) {
            knLogger.error("rollback(txn)", "Failed to rollback the transaction.");
        }
    }

}
