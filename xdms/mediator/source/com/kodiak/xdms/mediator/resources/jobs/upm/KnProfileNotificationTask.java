/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.mediator.resources.jobs.upm;

import com.kodiak.common.dao.KnPersistenceException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.mediator.helper.KnXDMCommonMediator;
import com.kodiak.xdms.server.corpmgmt.clientintf.ICorpClientIntf;
import com.kodiak.xdms.server.corpmgmt.clientintf.impl.KnCorpClientImpl;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPUserProfileDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpResponseDTO;

public class KnProfileNotificationTask extends KnAbstractTask {

    private static final KnLogger knLogger = KnLogger.getLogger(KnProfileNotificationTask.class);

    private String userProfileId;
    private String corpId;
    private KnPersisterTxn userProfileNotificationTxn;
    private ICorpClientIntf corpClientIntf;
    private KnXDMCommonMediator commonMediator;

    public KnProfileNotificationTask(String userProfileId, String corpId) {
        this.userProfileId = userProfileId;
        this.corpId = corpId;
        corpClientIntf = new KnCorpClientImpl();
        commonMediator = KnXDMCommonMediator.getInstance();
    }


    @Override
    public KnTaskResult executeTask() {
        final String methodName = "executeTask()";
        KnTaskResult taskResult=new KnTaskResult();
        try {
            knLogger.debug(methodName,"profile notification for userProfileId:",userProfileId," corpId:",corpId);
            KnIPUserProfileDTO ipUserProfileDTO = new KnIPUserProfileDTO();
            ipUserProfileDTO.setCorpId(corpId);
            ipUserProfileDTO.setProfileId(userProfileId);
            userProfileNotificationTxn = KnPersisterTxn.getPersisterTxn();
            userProfileNotificationTxn.open();

            KnCorpResponseDTO profileNotifyResp = corpClientIntf.userProfileNotfication(ipUserProfileDTO, userProfileNotificationTxn);
            knLogger.debug(methodName," profileNotifyResp:",profileNotifyResp);
            boolean status = commonMediator.prepareMcxNotifyForProfileMdns(profileNotifyResp);
            knLogger.debug(methodName, "sending Profile notification Status: ", status);
            userProfileNotificationTxn.save();

        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Failed to get the Transaction", e);
            rollback(userProfileNotificationTxn);
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected Exception occurred ", e);
            rollback(userProfileNotificationTxn);
        }
        return taskResult;
    }

    private void rollback(KnPersisterTxn txn) {
        try {
            knLogger.error("rollback()", "Rolling back transaction");
            if (txn != null) {
                txn.rollback();
            }
        } catch (Exception e) {
            knLogger.error("rollback(txn)",
                    "Failed to rollback the transaction.");
        }
    }
}
