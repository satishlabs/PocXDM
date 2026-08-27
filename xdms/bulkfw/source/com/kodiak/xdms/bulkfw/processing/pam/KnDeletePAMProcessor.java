/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.bulkfw.processing.pam;
/**
 * ************************************************************************
 * <p/>
 * File name:  KnDeletePAMProcessor.java
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
import com.kodiak.common.exception.KnException;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.kuidgenerator.KnKUIDConstants;
import com.kodiak.utilities.kuidgenerator.KnKUIDGenerator;
import com.kodiak.xdms.bulkfw.KnXDMBulkMediator;
import com.kodiak.xdms.bulkfw.resources.KnBulkFwConstants;
import com.kodiak.xdms.bulkfw.resources.KnBulkProvConstants.BATCH_EXEC_TYPE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KnDeletePAMProcessor extends KnPAMProcessor {

    private static final KnLogger knLogger = KnLogger.getLogger(KnDeletePAMProcessor.class);

    public Map<Long, List<String>> deleteProcess(int pamAccId, long cycleMdn, long endMdn, int batchExecType, int batchSize, int bulkOrderType,
    		String pttserverId, boolean isUpgrade, long bulkInsertionTime) throws KnException {
        String methodname = "deleteProcess()";
        Map<Long, List<String>> processValue = new HashMap<Long, List<String>>();
        List<String> mdnList = new ArrayList<String>(batchSize);
        long cycleEndMdn;
        knLogger.debug(methodname, "Entry ");
        KnKUIDGenerator kuidGenerator =KnKUIDGenerator.getInstance();
        KnXDMBulkMediator bulkMediator = KnXDMBulkMediator.getInstance();
        if ((bulkOrderType == KnBulkFwConstants.BULK_ORDER_TYPE.SUBS_PAM_DELETE.value()) ||(bulkOrderType == KnBulkFwConstants.BULK_ORDER_TYPE.SUBS_PAM_ROLLBACK.value())){
            batchSize = 0;
        }
        if (bulkOrderType == KnBulkFwConstants.BULK_ORDER_TYPE.SUBS_PAM_DOWNGRADE.value()) {
            mdnList = kuidGenerator.getKUIDByStatus(batchSize,pamAccId, KnKUIDConstants.KUID_MDN_STATUS.DOWNGRADE.valueOf());
            
        } else if ((batchExecType == BATCH_EXEC_TYPE.BATCH_EXEC_NORMAL.value())||(bulkOrderType == KnBulkFwConstants.BULK_ORDER_TYPE.SUBS_PAM_ROLLBACK.value())) {
        	//checking weather its a upgrade rollback
        	if (isUpgrade) {
        		knLogger.info(methodname, "getting mdns for upgarde rollback");
        		mdnList = bulkMediator.retrievePAMAccountMDNsByInsertionTime(pamAccId, bulkInsertionTime, null);
        	} else {
        		mdnList = bulkMediator.retrievePAMAccountMDNs(pamAccId, batchSize, String.valueOf(cycleMdn), null);
        	}
        }
        knLogger.debug(methodname, "delete/Downgrade mdns ", KnGDPRTemplate.mdnList(mdnList));
        knLogger.info(methodname, "delete/Downgrade mdns size ", mdnList.size());
        if (!mdnList.isEmpty()) {
            cycleEndMdn = Long.parseLong((mdnList.get(mdnList.size() - 1)).trim());
        } else {
            cycleEndMdn = endMdn + 1;
            mdnList.add(String.valueOf(cycleEndMdn));
        }
        knLogger.info(methodname, "delete/Downgrade mdns ", KnGDPRTemplate.mdnList(mdnList));
        processValue.put(cycleEndMdn, mdnList);
        return processValue;
    }
}
