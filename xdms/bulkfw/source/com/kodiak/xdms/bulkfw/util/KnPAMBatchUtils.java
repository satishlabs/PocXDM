/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.bulkfw.util;
/**
 * ************************************************************************
 * <p/>
 * File name:  KnPAMBatchUtils.java
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
import com.kodiak.common.commdto.request.KnXDMPAMAccInfoDTO;
import com.kodiak.common.commdto.request.KnXDMPAMSubsProfInfoDTO;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.exception.KnException;
import com.kodiak.frameworks.messaging.common.dto.KnMessage;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.kuidgenerator.KnKUIDConstants;
import com.kodiak.utilities.kuidgenerator.KnKUIDGenerator;
import com.kodiak.xdms.bulkfw.dao.KnXDMBulkOrderInfoDAO;
import com.kodiak.xdms.bulkfw.dto.KnBulkDTO;

import java.util.List;

public class KnPAMBatchUtils {
    private static final KnLogger knLogger = KnLogger.getLogger(KnPAMBatchUtils.class);

    private KnXDMBulkOrderInfoDAO bulkOrderDao;

    public KnPAMBatchUtils(String pttServerId) {
        bulkOrderDao = new KnXDMBulkOrderInfoDAO(pttServerId);
    }

    public List<String> getPseudoNumbers(int size) throws KnException {
        String methodName = "getMDNsFromIdGenerator(int)";
        knLogger.info(methodName, "ENTRY ", size);
        KnKUIDGenerator kuidGenerator = KnKUIDGenerator.getInstance();
        return kuidGenerator.getKodiakUserIDs(KnKUIDConstants.COUNTRYCODE, size);
    }
    
    public KnXDMPAMSubsProfInfoDTO getDefaultSubsProfile(int bulkOrderId, boolean isOldProfile, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodname = "getDefaultSubsProfile(int,boolean,KnPersisterTxn)";
        knLogger.info(methodname, "ENTRY", bulkOrderId, isOldProfile);
        KnBulkDTO bulkOrderDto = bulkOrderDao.getBulkOrderDetails(bulkOrderId, persisterTxn);
        KnMessage msg = bulkOrderDto.getBulkOrderReqObj();
        KnXDMPAMAccInfoDTO provReqObject;
        KnXDMPAMSubsProfInfoDTO profInfoDTO;
        provReqObject = (KnXDMPAMAccInfoDTO) msg.getPayLoad();
        knLogger.debug(methodname, "provReqObject", provReqObject);
        if (isOldProfile)
            profInfoDTO = provReqObject.getOldProfileDetails();
        else
            profInfoDTO = provReqObject.getProfileDetails();
        knLogger.debug(methodname, "profInfoDTO", profInfoDTO);

        return profInfoDTO;
    }
}