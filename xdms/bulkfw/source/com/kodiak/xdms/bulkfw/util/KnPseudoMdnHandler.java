/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.bulkfw.util;
/**
 * ************************************************************************
 * <p/>
 * File name:  KnPseudoMdnHandler.java
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KnPseudoMdnHandler {
    private static final KnLogger knLogger = KnLogger.getLogger(KnPseudoMdnHandler.class);
    private static KnPseudoMdnHandler instance;
    private Map<Integer, List<String>> mdnList;

    private KnPseudoMdnHandler() {
        mdnList = new HashMap<>();
    }

    public static synchronized KnPseudoMdnHandler getInstance() {
        String methodName = "getInstance()";
        if (instance == null) {
            instance = new KnPseudoMdnHandler();
            knLogger.debug(methodName, "Created instance for KnPseudoMdnHandler");
        }
        return instance;
    }

    public List<String> getMdnList(int bulkOrderId) {
        if (mdnList.containsKey(bulkOrderId))
            return mdnList.get(bulkOrderId);
        else
            return null;
    }

    public void insertPseudoList(int bulkOrderId, List<String> pseudoList) {
        String methodName = "insertPseudoList(int,List)";
        knLogger.debug(methodName, "INIT", bulkOrderId, pseudoList, mdnList == null ? mdnList : KnGDPRTemplate.mapMdnAsListValue(mdnList));
        if (!mdnList.containsKey(bulkOrderId)) {
            knLogger.debug(methodName, "Inserting mdnlist", bulkOrderId, pseudoList);
            mdnList.put(bulkOrderId, pseudoList);
        }
    }

    public boolean deletePseudoList(int bulkOrderId) {
        String methodName = "deletePseudoList(int)";
        if (mdnList.containsKey(bulkOrderId)) {
            mdnList.remove(bulkOrderId);
            knLogger.debug(methodName, "Removed mdnlist for", bulkOrderId);
            return true;
        }
        return false;
    }
}