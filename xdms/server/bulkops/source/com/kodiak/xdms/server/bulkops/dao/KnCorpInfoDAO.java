/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *****************************************************************************
 * File name:   KnCorpInfoDAO.java
 * Subsystem:   Provisioning Library
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ravi Shanker .P       Dec 15, 2010       7.0
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
 * *******************************************************************************
 */
package com.kodiak.xdms.server.bulkops.dao;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.frameworks.dbfw.KnDbSyncFwConstants;
import com.kodiak.frameworks.dbfw.collectors.KnSqlJobCollector;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.bulkops.dto.common.KnCorpProfilePersistDTO;
import com.kodiak.xdms.server.bulkops.utils.KnBulkOpsDBUtil;


import java.util.*;

public class KnCorpInfoDAO {
    private static final KnLogger knLogger = KnLogger.getLogger(KnCorpInfoDAO.class);

    public static final String className = KnCorpInfoDAO.class.getName();
    private String pttServerId = KnBulkOpsDBUtil.getXdmPttServerId();


    public void bulkUpdateLastProfileTime(List<KnCorpProfilePersistDTO> corpProfileDTOList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "bulkUpdateLastProfileTime(List<KnCorpProfilePersistDTO>, KnPersisterTxn)";
        knLogger.debug(methodName, "Request to bulk update Profiles with data - ", corpProfileDTOList);

        KnSqlJobCollector collector = KnSqlJobCollector.getInstance();
        int serviceType = KnDbSyncFwConstants.EXECUTOR.ETAG_UPDATE.value();

        for (KnCorpProfilePersistDTO corpInfoDto : corpProfileDTOList) {
            int corpId = corpInfoDto.getCorpId();
            Map<String, String> asyncInput = new HashMap<>();
            asyncInput.put(KnDbSyncFwConstants.PTTSERVER_ID, pttServerId);
            asyncInput.put(KnDbSyncFwConstants.CORP_ID, String.valueOf(corpId));
            knLogger.debug(methodName, "Async inputs", asyncInput);
            collector.collect(serviceType, asyncInput);
        }
        knLogger.info(methodName, "Bulk updated to collector", serviceType);
    }

}