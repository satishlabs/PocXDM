/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnCorpXDMTablesRegistry.java
 * Subsystem:  POC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Namita.P.Nair        01-02-2011      7.0
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
package com.kodiak.xdms.server.corpmgmt.dao.persister.db.tables.xdm;


public class KnCorpXDMTablesRegistry {

    //Contact Tables//

    public KnXDMCorpContactCountDAO createXDMCorpContactCountDAO(String xdmsHome) {
        return new KnXDMCorpContactCountDAO(xdmsHome);
    }

    public KnXDMExtPoCSubscriberDAO createXDMExtPoCSubscriberDAO(String xdmsHome) {
        return new KnXDMExtPoCSubscriberDAO(xdmsHome);
    }

    public KnXDMCorpListInfoDAO createXDMCorpListInfoDAO(String xdmsHome) {
        return new KnXDMCorpListInfoDAO(xdmsHome);
    }

    public KnCorpGroupInfoDAO createCorpGroupInfoDAO(String xdmsHome) {
        return new KnCorpGroupInfoDAO(xdmsHome);
    }

    public KnCorpGroupDistInfoDAO createCorpGroupDistInfoDAO(String xdmsHome) {
        return new KnCorpGroupDistInfoDAO(xdmsHome);
    }

    public KnCorpGroupListRefDAO createCorpGroupListRefDAO(String xdmsHome) {
        return new KnCorpGroupListRefDAO(xdmsHome);
    }

   /* public KnXDMCorpGroupDocDAO createCorpGroupDocDAO(String xdmsHome) {
        return new KnXDMCorpGroupDocDAO(xdmsHome);
    }*/

    //
    public KnXDMSubscriberInfoDAO createXDMSubscriberInfoDAO(String xdmsHome) {
        return new KnXDMSubscriberInfoDAO(xdmsHome);
    }
    public KnCorpDeviceDAO createXDMDeviceDAO(String xdmsHome) {
        return new KnCorpDeviceDAO(xdmsHome);
    }

    public KnCorpStatsDAO createXDMStatsDAO(String xdmsHome) {
        return new KnCorpStatsDAO(xdmsHome);
    }

    public KnXDMThirdPartyAccountDAO createXDMThirdPartyAccountDAO(String xdmsHome) {
        return new KnXDMThirdPartyAccountDAO(xdmsHome);
    }

    public KnXDMCorpListMemberDAO createXDMCorpListMemberDAO(String xdmsHome) {
        return new KnXDMCorpListMemberDAO(xdmsHome);
    }

    public KnXDMDirectoryDAO createXDMDirectoryDAO(String xdmHome) {
        return new KnXDMDirectoryDAO(xdmHome);
    }

    public KnXDMCorpResourceListIndexDocDAO createXDMCorpResourceListIndexDocDAO(String xdmHome) {
        return new KnXDMCorpResourceListIndexDocDAO(xdmHome);
    }

    public KnXDMCorpListDistInfoDAO createXDMCorpListDistInfoDAO(String xdmHome) {
        return new KnXDMCorpListDistInfoDAO(xdmHome);
    }

    public KnXDMCorpGroupListRefDAO createXDMCorpCorpGroupListRefDAO(String xdmHome) {
        return new KnXDMCorpGroupListRefDAO(xdmHome);
    }

   /* public KnXDMCorpGroupDocDAO createXDMCorpGroupDocDAO(String xdmHome) {
        return new KnXDMCorpGroupDocDAO(xdmHome);
    }*/

    public KnXDMCorpGroupListRefDAO createXDMCorpGroupListRefDAO(String xdmHome) {
        return new KnXDMCorpGroupListRefDAO(xdmHome);
    }

    public KnXDMCorpGroupInfoDAO createXDMCorpGroupInfoDAO(String xdmHome) {
        return new KnXDMCorpGroupInfoDAO(xdmHome);
    }

    public KnXDMCorpInfoDAO createXDMCorpInfoDAO(String xdmHome) {
        return new KnXDMCorpInfoDAO(xdmHome);
    }

    public KnXDMCorpGroupDistInfoDAO createXDMCorpGroupDistInfoDAO(String xdmHome) {
        return new KnXDMCorpGroupDistInfoDAO(xdmHome);
    }

    public KnXDMCorpGroupMemberListDAO createXDMCorpGroupMemberListDAO(String pttServerId) {
        return new KnXDMCorpGroupMemberListDAO(pttServerId);
    }

    public KnXDMCorpContactListDAO createXDMCorpContactListDAO(String pttServerId) {
        return new KnXDMCorpContactListDAO(pttServerId);
    }

    public KnXDMCorpGroupMemberCountDAO createXDMCorpGroupMemberCountDAO(String pttServerId) {
        return new KnXDMCorpGroupMemberCountDAO(pttServerId);
    }

    public KnXDMCorpActivationDAO createXDMCorpActivationDAO(String pttServerId) {
        return new KnXDMCorpActivationDAO(pttServerId);
    }

    public KnXDMCorpCampGrpDOA createXDMCampedGrpDAO(String pttServerId) {
        return new KnXDMCorpCampGrpDOA(pttServerId);
    }

    public KnXDMLocationServiceConfigDAO createXDMLocationServiceConfigDAO(String pttServerId){
        return new KnXDMLocationServiceConfigDAO(pttServerId);
    }

    public KnExtSubscrInfoDAO createExtSubscrInfoDAO(String xdmsHome) {
        return new KnExtSubscrInfoDAO(xdmsHome);
    }

    public KnXDMLicensePackListInfoDAO createXDMLicensePackListInfoDAO(String pttServerId){
        return new KnXDMLicensePackListInfoDAO(pttServerId);
    }

    public KnXDMCorpGWLinkedAccountInfoDAO createXDMCorGWLinkedAccountInfoDAO(String pttServerId){
        return new KnXDMCorpGWLinkedAccountInfoDAO(pttServerId);
    }

    public KnClientTypeConfigurationInfoDAO createClientTypeConfigurationDAO(String pttServerId){
        return new KnClientTypeConfigurationInfoDAO(pttServerId);
    }

    public KnPAMSubscriberProfileInfoDAO createPAMSubscriberProfileInfoDAO(String pttServerId) {
        return new KnPAMSubscriberProfileInfoDAO(pttServerId);
    }

    public KnXDMCorpChannelGrpDAO createXDMChannelGrpDAO(String pttServerId) {
        return new KnXDMCorpChannelGrpDAO(pttServerId);
    }

    public KnXDMLISubscriberInfoDAO createXDMLISubscriberInfoDAO(String xdmsHome) {
        return new KnXDMLISubscriberInfoDAO(xdmsHome);
    }

    public KnXDMCorpMcpttInfoDAO createXDMCorpMcpttInfoDAO(String xdmsHome) {
        return new KnXDMCorpMcpttInfoDAO(xdmsHome);
    }

    public KnXDMCorpAddlTalkGrpDAO createXDMAddlTGInfoDAO(String xdmsHome) {
        return new KnXDMCorpAddlTalkGrpDAO(xdmsHome);
    }

    public KnXDMCorpTGSSGrpDAO createTGSSGrpDAO(String pttServerId){
        return new KnXDMCorpTGSSGrpDAO(pttServerId);
    }

    public KnCorpOsmDAO corpOsmDAO(String pttServerId) {
        return new KnCorpOsmDAO(pttServerId);
    }

    public KnSipProxySvcConfigDAO createSipProxySvcConfigInfoDAO(String xdmsHome) {
        return new KnSipProxySvcConfigDAO(xdmsHome);
    }

    public KnAPNProfileDAO createAPNProfileInfoDAO(String xdmsHome) {
        return new KnAPNProfileDAO(xdmsHome);
    }

    public KnXDMProfilGroupInfoDAO createXDMProfilGroupInfoDAO(String xdmsHome) {
        return new KnXDMProfilGroupInfoDAO(xdmsHome);
    }
    public KnCorpGroupProfileDAO createCorpGroupProfileDAO(String xdmsHome) {
        return new KnCorpGroupProfileDAO(xdmsHome);
    }
    public KnCorpGroupProfileSharedListDAO createCorpGroupProfileSharedListDAO(String xdmsHome) {
        return new KnCorpGroupProfileSharedListDAO(xdmsHome);
    }

    public KnCorpGroupSharedListDAO createCorpGroupSharedListDAO(String xdmsHome) {
        return new KnCorpGroupSharedListDAO(xdmsHome);
    }

    public KnXDMUserProfilHiearchyMapDAO createXDMUserProfilHiearchyMapDAO(String xdmsHome) {
        return new KnXDMUserProfilHiearchyMapDAO(xdmsHome);
    }
    public KnXDMGroupHiearchyMapDAO createXDMGroupHiearchyMapDAO(String xdmsHome) {
        return new KnXDMGroupHiearchyMapDAO(xdmsHome);
    }
    public KnUserprofileSharedlistDAO createXDMUserProfileSharedListDAO(String xdmsHome) {
        return new KnUserprofileSharedlistDAO(xdmsHome);
    }
    public KnCorpStatsDAO createXDMCorpStatsDAODAO(String xdmsHome) {
        return new KnCorpStatsDAO(xdmsHome);
    }

    public KnXdmFanDetailsDao createXdmFanDetailsDao(String pttServerId) {
        return new KnXdmFanDetailsDao(pttServerId);
    }

    public KnGroupHierarchyDAO createKnGroupHierrarchyDAO(String xdmsHome) {
        return new KnGroupHierarchyDAO(xdmsHome);
    }

    public KnXDMBanDetailsDAO createXdmBanDetailsDao(String pttServerId) {
        return new KnXDMBanDetailsDAO(pttServerId);
    }


    public KnXDMHierarchyDAO createXDMHierarchyDAO(String pttServerId) { return new KnXDMHierarchyDAO(pttServerId); }


    public KnCorpPTTSettingDocDAO createCorpPTTSettingDAO(String pttServerId) {return new KnCorpPTTSettingDocDAO(pttServerId);}

}
