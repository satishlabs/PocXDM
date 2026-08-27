/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.mediator.util;

import com.kodiak.logger.KnLogger;

import java.util.HashMap;
import java.util.Map;

public class KnTxnManagerImpl implements ITxnManagerIntrf {
	private static final KnLogger knLogger = KnLogger.getLogger(KnTxnManagerImpl.class);
    private static String CLASS = KnTxnManagerImpl.class.getName();
    private static KnTxnManagerImpl instance = new KnTxnManagerImpl();

    private volatile static Map<String, Map<String, Object>> transMap = new HashMap<String, Map<String, Object>>();

    private KnTxnManagerImpl() {
    }

    public static KnTxnManagerImpl getInstance() {
        return instance;
    }

    /*
      Method  to store Transaction id
     @param : transactionId : String value to be stored.
    */
    public synchronized boolean storeTxnId(String transactionId, Map<String, Object> customMap) {
        String methodName = "storeTxnId(String, Map)";
        knLogger.debug( methodName, "Entry -> TxnId - " , transactionId);
        if (transactionId == null || transactionId.isEmpty()) {
            knLogger.debug( methodName, " Transaction id is empty or null");
            return false;
        } else {
            transMap.put(transactionId, customMap);
            knLogger.debug( methodName, "transaction id " , transactionId);
            return true;
        }
    }

    /*
      Method  that checks whether transaction id exists or not.
     @param : transactionId : String value that to be check
    */
    public synchronized boolean containsTxnId(String transactionId) {
        return transMap.containsKey(transactionId);
    }

    public synchronized Object removeTxId(String transactionId) {
        return transMap.remove(transactionId);
    }

    public synchronized Map<String, Object> getCustomMap(String transactionId) {
        return transMap.get(transactionId);
    }
}
