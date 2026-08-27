/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.bulkfw.processing.pam;
/**
 * ************************************************************************
 * <p/>
 * File name:  KnCreatePAMProcessor.java
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
import com.kodiak.xdms.bulkfw.util.KnPseudoMdnHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KnCreatePAMProcessor extends KnPAMProcessor {
    private static final KnLogger knLogger = KnLogger.getLogger(KnCreatePAMProcessor.class);

    public Map<Long, List<String>> createProcess(long startMdn, long endMdn, long cycleMdn, int bulkOrderId, int batchSize) {
        String methodname = "createProcess(long startMdn, long endMdn, long cycleMdn, int bulkOrderId)";
        long cycleEndMdn;
        List<String> mdnList = new ArrayList<>(batchSize);
        Map<Long, List<String>> processValue = new HashMap<>();
        if (cycleMdn == 0)
            cycleMdn = startMdn;
        knLogger.debug(methodname, "Cyclemdn", cycleMdn);
        //Get mdnlist from Pseudo Mdn handler
        KnPseudoMdnHandler handler = KnPseudoMdnHandler.getInstance();
        List<String> pseudoMdns = handler.getMdnList(bulkOrderId);
        knLogger.debug(methodname, "Fetched pseudo mdn list", KnGDPRTemplate.mdnList(pseudoMdns));
        int pseudoMdnSize = pseudoMdns.size();
        int startIndex = pseudoMdns.indexOf(String.valueOf(cycleMdn));
        int endIndex = startIndex + batchSize;
        if (endIndex >= pseudoMdns.size()) {    //Last batch
            endIndex = pseudoMdnSize;
            cycleEndMdn = endMdn;
        } else {
            cycleEndMdn = Long.parseLong(pseudoMdns.get(endIndex).trim());
        }
        knLogger.debug(methodname, "startIndex", startIndex, "endIndex", endIndex);
        for (int i = startIndex; i < endIndex; i++) {
            mdnList.add(pseudoMdns.get(i));
        }
        processValue.put(cycleEndMdn, mdnList);
        knLogger.info(methodname, "mdnlist", KnGDPRTemplate.mdnList(mdnList), "new cycle mdn", cycleEndMdn);
        return processValue;
    }
}
