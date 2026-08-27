/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.mediator.resources.jobs;

import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.statusmgr.KnStatusManagerClient;
import com.kodiak.xdms.mediator.helper.KnXDMPamResponseHandler;
import com.kodiak.xdms.server.common.dto.common.KnBulkOrderInfoDTO;


/**
 * *********************************************************************
 * File name:   KnBulkResponse.java
 * Subsystem:   PAM Account Management
 * <p/>
 * Name                  Date         Release
 * -----------------    -----------   -------
 * Ajit Kumar           Mar 06 2013    7.4
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
 * *************************************************************************
 */
public class KnBulkResponse implements Runnable {
	private static final KnLogger knLogger = KnLogger.getLogger(KnBulkResponse.class);
	private KnBulkOrderInfoDTO bulkOrderInfoDTO;


    public KnBulkResponse(KnBulkOrderInfoDTO bulkOrderInfoDTO) {
    	this.bulkOrderInfoDTO = bulkOrderInfoDTO;
    }

    public void run() {
    	 final String methodName="run()";
   	  	 knLogger.entry(methodName, "Current State of a card:", KnStatusManagerClient.getCurrentState());
    	 try {
			
    		 KnXDMPamResponseHandler pamResponseHandler = KnXDMPamResponseHandler.getInstance();
    		 knLogger.debug(methodName, "calling sendXDMPAMResponse - " + bulkOrderInfoDTO);
    		 pamResponseHandler.sendXDMPAMResponse(bulkOrderInfoDTO);
    		 knLogger.exit(methodName);
		} catch (Exception e) {
			knLogger.error(methodName, "unexpected Exception occured - ", e);
   	  	  }
    }


}