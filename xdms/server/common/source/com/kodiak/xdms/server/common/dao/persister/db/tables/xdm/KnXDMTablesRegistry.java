/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/

/******************************************************************************
 * File name:   Kn.java
 * Subsystem:
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ravi Shanker .P       Dec 16, 2010       7.0
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
 * *******************************************************************************/

package com.kodiak.xdms.server.common.dao.persister.db.tables.xdm;

import com.kodiak.xdms.server.common.dto.common.KnQPPProfileInfoDTO;

public final class KnXDMTablesRegistry {

    public KnXDMContactListDAO createXDMContactListDAO(String pttServerId) {
        return new KnXDMContactListDAO(pttServerId);
    }

    public KnXDMContactListDocMapDAO createXDMContactListDoCMapDAO(String pttServerId) {
        return new KnXDMContactListDocMapDAO(pttServerId);
    }

    public KnXDMCorpResourceListIndexDocDAO createXDMCorpResourceListIndexDocDAO(String pttServerId) {
        return new KnXDMCorpResourceListIndexDocDAO(pttServerId);
    }

    public KnXDMDirectoryDAO createXDMDirectoryDAO(String pttServerId) {
        return new KnXDMDirectoryDAO(pttServerId);
    }

    public KnXDMSubscriberInfoDAO createXDMSubscribreInfoDAO(String pttServerId) {
        return new KnXDMSubscriberInfoDAO(pttServerId);
    }

    public KnXDMCorpInfoDAO createXDMCorpInfoDAO(String pttServerId) {
        return new KnXDMCorpInfoDAO(pttServerId);
    }

    public KnXDMBulkOrderInfoDAO createXDMBulkOrderInfoDAO(String pttServerId) {
        return new KnXDMBulkOrderInfoDAO(pttServerId);
    }

    public KnExtSubscrProfileInfoDAO createExtSubscrProfileInfoDAO(String pttServerId) {
        return new KnExtSubscrProfileInfoDAO(pttServerId);
    }

    public KnTmpVASSubscrKeyInfoDAO createTmpVASSubscriptionKeyInfoDAO(String pttServerId) {
        return new KnTmpVASSubscrKeyInfoDAO(pttServerId);
    }

    public KnQPPProfileInfoDAO createQppProfileInfoDAO(String pttServerId) {
        return new KnQPPProfileInfoDAO(pttServerId);
    }

    public KnXDMCorpGroupMemberListDAO createCorpGroupMemberListDAO(String pttServerId) {
        return new KnXDMCorpGroupMemberListDAO(pttServerId);
    }

    public KnCorpConfigInfoDAO createXDMCorpConfigInfoDAO(String pttServerId) {
        return new KnCorpConfigInfoDAO(pttServerId);
    }
    public KnRecordingInfoDAO creteRecordingInfoDAO(String pttServerId){
       return new KnRecordingInfoDAO(pttServerId);
    }

    public KnRecordingInfoDAO createRecordingInfoDAO(String pttServerId) {
        return new KnRecordingInfoDAO(pttServerId);
    }
}
