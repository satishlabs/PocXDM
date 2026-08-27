/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.mediator.util;

import java.util.Map;


public interface ITxnManagerIntrf {

    /**
     * Method  to store Transaction id
     *
     * @param transactionId String value to be stored.
     * @return
     */
    public boolean storeTxnId(String transactionId, Map<String, Object> customMap);

    /**
     * Method uses to remove txn id ...
     *
     * @param transactionId transaction id
     * @return
     */
    Object removeTxId(String transactionId);
}