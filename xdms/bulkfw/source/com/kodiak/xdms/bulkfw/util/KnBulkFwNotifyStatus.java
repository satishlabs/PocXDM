/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.bulkfw.util;
/**
 * ************************************************************************
 * <p/>
 * File name:  KnBulkFwNotifyStatus.java
 * Subsystem:  XDMS-BulkFrameWork
 * <p/>
 * Name                  Date          Release
 * --------------------  ------------  -------------------------------------
 * Chandrashekar H S     08/07/2015    8.0
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
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.statusmgr.IStatusMgrNotifyIntf;
import com.kodiak.utilities.statusmgr.KnStatusManagerClient;
import com.kodiak.utilities.statusmgr.utils.KnStatusMgrConstants.CARD_STATES;

import java.util.ArrayList;
import java.util.List;

public final class KnBulkFwNotifyStatus implements IStatusMgrNotifyIntf {
    private static final KnLogger knLogger = KnLogger.getLogger(KnBulkFwNotifyStatus.class);

    private static KnBulkFwNotifyStatus instance = null;
    private CARD_STATES currentRedState = CARD_STATES.ACTIVE;

    private KnBulkFwNotifyStatus() {
    }

    public static synchronized KnBulkFwNotifyStatus getInstance() {
        if (instance == null) {
            instance = new KnBulkFwNotifyStatus();
            //registering for Redundancy Notify Status
            List<IStatusMgrNotifyIntf> list = new ArrayList<>();
            list.add(instance);
            KnStatusManagerClient.registerObjects(list);
            knLogger.debug("getInstance()", "registered for Status..");
        }

        return instance;
    }

    /*
      * (non-Javadoc)
      *
      * @see com.kodiak.utilities.statusmgr.IStatusMgrNotifyIntf#notify(
      * com.kodiak.utilities.statusmgr.utils.KnStatusMgrConstants.CARD_STATES,
      * com.kodiak.utilities.statusmgr.utils.KnStatusMgrConstants.CARD_STATES)
      */
    public void notify(CARD_STATES previousState, CARD_STATES currentState) {
        String methodName = "notify(CARD_STATES, CARD_STATES)";
        knLogger.debug(methodName, "ENTRY: Notify Redundancy status");
        this.currentRedState = currentState;

        knLogger.debug(methodName, "Current State ", currentState);

    }

    /**
     * method which gives redundancy state as TRUE if ACTIVE else false
     *
     * @return boolean
     */
    public CARD_STATES getCurrentRedundancyStatus() {
        String methodName = "getCurrentRedundancyStatus";
        knLogger.debug(methodName, "Current Card Redundancy Status ", this.currentRedState);
        return this.currentRedState;
    }
}
