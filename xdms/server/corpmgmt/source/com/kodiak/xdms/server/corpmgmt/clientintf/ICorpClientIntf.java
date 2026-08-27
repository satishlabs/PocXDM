/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  ICorpClientIntf.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Jan 10, 2011      7.0
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
package com.kodiak.xdms.server.corpmgmt.clientintf;


import com.kodiak.xdms.server.corpmgmt.clientintf.impl.ICorpGroupProfileManager;

public interface ICorpClientIntf extends ICorpGenericManager, ICorpContactManager, ICorpGroupManager,
        ICorpSublistManager, ICorpActivationManager, ICorpLicenseManager, ICorpSubscrProfileManager,
        ICorpOsmManager,ICorpUserProfileManager, ICorpGroupProfileManager,ICorpDeviceManager, ICorpStatsManager,ICorpAssignUserProfileManager,
        ICorpAllCloningManager, ICorpHierarchyManager, ICorpPTTSettingManager {


}
