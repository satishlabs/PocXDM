/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.bulkfw.processing.pam;
/**
 * ************************************************************************
 * <p/>
 * File name:  KnChangeAuthStatusPAMProcessor.java
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
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.bulkfw.KnXDMBulkMediator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KnChangeAuthStatusPAMProcessor extends KnPAMProcessor {
    private static final KnLogger knLogger = KnLogger.getLogger(KnChangeAuthStatusPAMProcessor.class);
    public Map<Long, List<String>> changeStatusProcess(int pamAccId, long cycleMdn, long endMdn, int batchSize) throws Exception {
        final String methodname = "write(List)";
        knLogger.debug(methodname,"Entry");
        Map<Long, List<String>> processValue = new HashMap<Long, List<String>>();
        List<String> mdnList;
        KnXDMBulkMediator bulkMediator = KnXDMBulkMediator.getInstance();
        long cycleEndMdn;
        mdnList = bulkMediator.retrievePAMAccountMDNs(pamAccId, String.valueOf(cycleMdn), String.valueOf(endMdn), batchSize, null);

        if (!mdnList.isEmpty()) {
            cycleEndMdn = Long.parseLong((mdnList.get(mdnList.size() - 1)).trim());
        } else {
            cycleEndMdn = endMdn + 1;
            mdnList.add(String.valueOf(cycleEndMdn));
        }
        knLogger.debug(methodname,"Exit", KnGDPRTemplate.mdnList(mdnList));
        processValue.put(cycleEndMdn, mdnList);
        return processValue;
    }
}
