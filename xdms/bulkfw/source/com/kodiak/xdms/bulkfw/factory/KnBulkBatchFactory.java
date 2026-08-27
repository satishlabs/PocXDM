/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**************************************************************************
 * <p/>
 * File name:  KnBulkBatchFactory.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Ravi Shanker P       21-sept-2012   7.4
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
 * ************************************************************************/
package com.kodiak.xdms.bulkfw.factory;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.bulkfw.dto.KnBulkOrderDTO;
import com.kodiak.xdms.bulkfw.dto.KnBulkOrderRespDTO;


public class KnBulkBatchFactory implements IBulkOrderFactory {
    private static final KnLogger knLogger = KnLogger.getLogger(KnBulkBatchFactory.class);

    private final String className = KnBulkBatchFactory.class.getName();


    public KnBulkOrderRespDTO performBulkOp(KnBulkOrderDTO bulkOrderDTO) {
        String methodName = "performBulkOp(KnBulkOrderDTO)";
        knLogger.fatal(methodName, "NOT IMPLEMENTED");
        return null;
    }

}
