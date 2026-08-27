/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.auditjobs.common;

import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.statusmgr.KnStatusManagerClient;
import com.kodiak.utilities.statusmgr.utils.KnStatusMgrConstants;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;

import java.util.*;

public class KnXDMSegIndUpdateThread implements Runnable {
	private static final KnLogger knLogger = KnLogger.getLogger(KnXDMSegIndUpdateThread.class);
	private KnGenInfoUtil genInfoUtil= KnGenInfoUtil.getInstance();
	private KnGeneralUtil generalUtil = new KnGeneralUtil();
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		String methodName = "run";
		KnPersisterTxn persisterTxn = null;
		knLogger.info(methodName, "Status :", KnStatusManagerClient.getCurrentState());
		Map<String,String> mdnSegmentIndicator = new HashMap<String, String>();
		List<String> mdns = new ArrayList<String>();

	try {
	
		int CORP_FLAG= Integer.parseInt(generalUtil.retrieveMSCommonConfig(new ArrayList<String>(Collections.singleton("CORP_HIERARCHY"))).get("CORP_HIERARCHY"));
		knLogger.info(methodName, "CORP_HIERARCHY :"+com.kodiak.common.resources.KnConstants.HIERARCHY_TYPE.HIERARCHY.value());

		if (KnStatusManagerClient.getCurrentState() == KnStatusMgrConstants.CARD_STATES.ACTIVE && CORP_FLAG ==com.kodiak.common.resources.KnConstants.HIERARCHY_TYPE.HIERARCHY.value()) {
		persisterTxn = KnPersisterTxn.getPersisterTxn();
		persisterTxn.open();
		mdnSegmentIndicator = genInfoUtil.getSegIndEnableSubs(persisterTxn);
		persisterTxn.save();
		knLogger.info(methodName, "MDN SegmnetIndicator :", mdnSegmentIndicator);
		mdns.addAll(mdnSegmentIndicator.keySet());
		int mdnSize = mdns.size();
		int fromIndex = 0;
		int toIndex = 0;
		while(mdnSize!=0) {
			List<String> mdnsSeg = new ArrayList<String>();
			persisterTxn.open();
			if(mdnSize >=1000) {
				fromIndex = toIndex;
				toIndex = toIndex+1000;
				mdnsSeg = mdns.subList(fromIndex, toIndex);
				genInfoUtil.updateSegmentIndicator(persisterTxn,mdnsSeg,mdnSegmentIndicator);
				mdnSize = mdnSize-1000;
			}
			else {
				fromIndex = toIndex;
				toIndex = mdns.size();
				mdnsSeg = mdns.subList(fromIndex, toIndex);
				genInfoUtil.updateSegmentIndicator(persisterTxn,mdnsSeg,mdnSegmentIndicator);
				mdnSize = 0;
			}
			persisterTxn.save();
			Thread.sleep(5000);
		}
	}
		
		
	}catch(Exception e) {
		
		knLogger.error(methodName, "Exception occured during KnXDMSegIndUpdateThread ", e);
		rollback(persisterTxn);
	}
	}


	private void rollback(KnPersisterTxn txn) {
		// TODO Auto-generated method stub
		try {
            txn.rollback();
        } catch (Exception e) {
            knLogger.error("rollback(txn)", "Failed to rollback the transaction.");
        }
	}

}
