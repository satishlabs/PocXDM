/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.mediator.resources.jobs;

import com.kodiak.logger.KnLogger;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.xdms.mediator.helper.KnXDMCommonMediator;
import com.kodiak.xdms.notificationmgr.IXcapDiffNotifierIntf;
import com.kodiak.xdms.notificationmgr.beans.KnXcapDiffDirChgNotifyDTO;
import com.kodiak.xdms.notificationmgr.impl.KnXcapDiffNotifierImpl;
import com.kodiak.xdms.server.corpmgmt.clientintf.ICorpClientIntf;
import com.kodiak.xdms.server.corpmgmt.clientintf.impl.KnCorpClientImpl;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpResponseDTO;

import java.util.Collection;
import java.util.List;

import static com.kodiak.common.dao.KnDbUtil.rollback;


/**
 * ************************************************************************
 * <p/>
 * File name:  KnEtagMgmtThread.java
 * Subsystem:  PoCXDM
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Saurabh Kumar      Feb 22, 2018      9.0
 * <p/>
 * <p/>
 * KODIAK, 9th Floor, 'MFar Manyata Tech Park'
 * Greenheart Phase IV, Nagawara
 * Bangalore - 560 045
 * www.kodiakptt.com
 * All Rights Reserved.
 * <p>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */

public class KnEtagMgmtThread implements Runnable {
    private static final KnLogger knLogger = KnLogger.getLogger(KnEtagMgmtThread.class);
    private static String reqMdn = null;
    private String operationType;
    private ICorpClientIntf corpClientIntf;
    private IXcapDiffNotifierIntf notifier;
    private KnXDMCommonMediator commonMediator = null;

    public KnEtagMgmtThread(String mdn) {
        init(mdn);
    }
    public KnEtagMgmtThread(String mdn,String operationType) {
        init(mdn);
        this.operationType=operationType;
    }

    private void init(String mdn) {
        String methodName = "init()";
        reqMdn = mdn;
        corpClientIntf = new KnCorpClientImpl();
        notifier = new KnXcapDiffNotifierImpl();
        commonMediator = KnXDMCommonMediator.getInstance();
        knLogger.info(methodName, "KnEtagMgmtThread Initialization Done");
    }

    @Override
    public void run() {
        String methodName = "run() KnEtagMgmtThread";
        knLogger.entry(methodName);
      //  KnPersisterTxn persisterTxn = null;
        try {
           // persisterTxn = KnPersisterTxn.getPersisterTxn();
          //  knLogger.debug(methodName, "Opening the Transaction");
          //  persisterTxn.open();
            corpClientIntf.subsEtagUpdate(reqMdn,operationType);
           // persisterTxn.save();
            // Step : prepare notification and send to notification mgr
           /* Collection<KnXcapDiffDirChgNotifyDTO> xcapDiffList = commonMediator.prepareNotification(respDto);
            knLogger.debug(methodName, "Sending notifications - ", xcapDiffList);
            notifier.setMaxNotfnsPerJob(2);
            boolean isNotified = notifier.sendXcapDiffNotifications(xcapDiffList, persisterTxn);
            knLogger.debug(methodName, "Notification status - ", isNotified);*/
            //knLogger.debug(methodName, "Transaction Saved Successfully");
        } catch (Exception eg) {
            knLogger.error(methodName, "Failed to start the etagMgmt Thread", eg);
           // rollback(persisterTxn);
        }
    }
}
