/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.mediator.resources.jobs;

import com.kodiak.frameworks.jobscheduler.KnAbstractJob;
import com.kodiak.frameworks.jobscheduler.resources.KnJobSchedulerException;
import com.kodiak.logger.KnLogger;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnDbUtil;
import com.kodiak.common.dao.KnPersisterTxn;

public class KnXDMDiscreetEnableAudit extends KnAbstractJob {

    private static final KnLogger knLogger = KnLogger.getLogger(KnXDMDiscreetEnableAudit.class);

    private static final long serialVersionUID = -126534597129196184L;
    private final String className = KnXDMDiscreetEnableAudit.class.getName();
    KnXDMDiscreetEnableDbUtil dbUtil;

    KnXDMDiscreetEnableAudit() {
        dbUtil = new KnXDMDiscreetEnableDbUtil();
    }

    @Override
    public boolean executeTask() throws KnJobSchedulerException {
        String methodName = "executeTask()";
        knLogger.info(methodName, "ENTRY: Start of XDMS Audit fo discreet enable flag ");
        KnPersisterTxn persisterTxn = null;

        try {
            persisterTxn = KnPersisterTxn.getPersisterTxn();
            knLogger.debug(methodName, "Opening the transaction");
            persisterTxn.open();

            knLogger.debug(methodName, "Disable pocsubscr discreet_enable flag ");

            dbUtil.disableDiscreetEnable(persisterTxn);

            knLogger.debug(methodName, " disable discreet_enable flag in pocsubscr success");

            knLogger.debug(methodName, "saving the transaction");
            persisterTxn.save();

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred");
            knLogger.error(methodName, e);
            KnDbUtil.rollback(persisterTxn);

        } catch (Exception e) {
            knLogger.error(methodName, "DAO Exception occurred");
            knLogger.error(methodName, e);
            KnDbUtil.rollback(persisterTxn);

        }

        return false;
    }

    public String getCronExpression() {
        String methodName = "getCronExpression()";
        knLogger.info(methodName, "ENTRY: Get Cron Expression - ");
        int cacheClearTime = 30; //if not config in DB take default 30 mins
        try {
            //  try {
            /*    Collection<String> keyList = new ArrayList<String>();
           keyList.add(KnConstants.CACHE_CLEANUP_TIMER);
           generalUtil = new KnGeneralUtil();
           Map<String, String> configMap = generalUtil.retrieveConfig(keyList);
           if (configMap != null) {
               cacheClearTime = Integer.parseInt(configMap.get(KnConstants.CACHE_CLEANUP_TIMER));
           } */
            cacheClearTime = Integer.parseInt(System.getenv("XDM_DM_CONFIGCACHEREFRESHTIMER"));
        } catch (NumberFormatException ne) {
            knLogger.warn(methodName, "Exception occured while getting refersh timer ", ne);
            knLogger.info(methodName, "Taking default refersh timer as ", cacheClearTime);
        }
        knLogger.debug(methodName, " time for CACHE_CLEANUP_TIMER :", cacheClearTime);
        String cronExpression = "0 */" + cacheClearTime + " * ? * *";
        knLogger.info(methodName, "EXIT: Cron Expression - ", cronExpression);
        return cronExpression;

        /* } catch (KnDAOException e) {
          knLogger.error( methodName, "Exception occurred- "  e);
      }  */
        //return null;
    }
}