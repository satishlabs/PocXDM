/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.ggcache;

import com.kodiak.common.ggcache.dao.*;
import com.kodiak.common.ggcache.dto.KnOidcTmpPwdDTO;
import com.kodiak.logger.KnLogger;
import org.springframework.stereotype.Component;

import java.sql.Connection;

/**
 * *****************************************************************************
 * File name:   KnGGCache
 * Subsystem:   Utility
 * Description: KnGGCache
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ajit Kumar           04/05/18        9.0
 * <p/>
 * <p/>
 * Copyright (c) 2018  Kodiak , A Motorola Solutions Company
 * 9th floor, MFar, Manayata Tech Park,
 * Greenheart Phase IV, Nagawara,
 * Bengaluru, Karnataka 560045
 * www.kodiakptt.com
 * All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Kodiak,A Motorola Solutions Company
 * You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak,A Motorola Solutions Company.
 * *******************************************************************************
 */

@Component
public final class KnGGCache {
    private static final KnLogger knLogger = KnLogger.getLogger(KnGGCache.class);

    public static KnCorpServiceMapDAO corpServiceMapDAO = new KnCorpServiceMapDAO();
    public static KnSystemServiceMapDAO systemServiceMapDAO = new KnSystemServiceMapDAO();
    public static KnUnUpgradedPoCServerListDAO unUpgradedPoCServerListDAO = new KnUnUpgradedPoCServerListDAO();
    public static KnOidcClientIdSecretDAO oidcClientIdSecretDAO = new KnOidcClientIdSecretDAO();
    public static KnSubsAddlInfoDAO subscriberAddlInfoDAO = new KnSubsAddlInfoDAO();
    public static KnKodUidConfigDAO kodUidConfigDAO = new KnKodUidConfigDAO();
    public static KnMCSClientInfoDAO  knMCSClientInfoDAO= new KnMCSClientInfoDAO();
    public static KnAsyncJobNotifyDAO knAsyncJobNotifyDAO = new KnAsyncJobNotifyDAO();
    public static KnAsyncJobTaskDAO knAsyncJobTaskDAO = new KnAsyncJobTaskDAO();
    public static KnCatAsyncTxnInfoDAO knCatAsyncTxnInfoDAO = new KnCatAsyncTxnInfoDAO();
    public static KnSIPRegistrationStatusDAO sipRegistrationStatusDAO = new KnSIPRegistrationStatusDAO();
    public static KnPUBContactAddonAliasDAO addonAliasDAO = new KnPUBContactAddonAliasDAO();
    public static KnCorpSharedTrustMatrixDAO trustMatrixDAO = new KnCorpSharedTrustMatrixDAO();
    public static KnSharedTrustMatrixHierarchyDAO sharedTrustMatrixHierarchyDAO = new KnSharedTrustMatrixHierarchyDAO();
    public static KnPreActivatedClientInfoDAO activatedClientInfoDAO = new KnPreActivatedClientInfoDAO();
    public static KnUserProfileInfoDAO userProfileInfoDAO = new KnUserProfileInfoDAO();
    public static KnCorpGrpLmrExtnDAO corpGrpLmrExtnDAO = new KnCorpGrpLmrExtnDAO();
    public static KnFeatureBitRollBackInfoDao knFeatureBitRollBackInfoDAO = new KnFeatureBitRollBackInfoDao();

    private KnGGCache() {
        try {
            //intialize gridgain on service start and make connection pool
            Connection conn = KnGGConnection.getDBConnection();
            knLogger.info("KnGGCache", "KnGGCache()");
            conn.close();
        } catch (Exception e) {
            knLogger.error("KnGGCache", "Exception" + e);
        }
    }

}
