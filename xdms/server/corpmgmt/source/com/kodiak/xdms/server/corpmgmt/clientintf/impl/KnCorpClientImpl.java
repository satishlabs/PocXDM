/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnCorpClientImpl.java
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
package com.kodiak.xdms.server.corpmgmt.clientintf.impl;

import com.kodiak.common.commdto.request.KnIPCatPermissionSetDTO;
import com.kodiak.common.commdto.request.KnXDMCorpInfoDTO;
import com.kodiak.common.commdto.response.KnCorpGetCorpFSResponse;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.error.KnSystemError;
import com.kodiak.common.exception.KnException;
import com.kodiak.common.ggcache.dto.KnAsyncJobDTO;
import com.kodiak.xdms.server.common.KnXDMServerException;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.configuration.manager.KnConfigurationsManager;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDirChgDTO;
import com.kodiak.xdms.server.common.framework.KnFWException;
import com.kodiak.xdms.server.common.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.business.KnCorpBOException;
import com.kodiak.xdms.server.corpmgmt.clientintf.*;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.*;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGrpBasicInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpPTTSettingDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnDocChangeListDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnUserProfileAssignedDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.*;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;
import com.kodiak.logger.KnLogger;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class KnCorpClientImpl implements ICorpClientIntf {
	private static final KnLogger knLogger = KnLogger.getLogger(KnCorpClientImpl.class);

    private static String className = KnCorpClientImpl.class.getName();
    private volatile ICorpGenericManager genericManager;
    private volatile ICorpContactManager contactManager;
    private volatile ICorpGroupManager groupManager;
    private volatile ICorpSublistManager sublistManager;
    private volatile ICorpActivationManager activationManager;
    private volatile ICorpLicenseManager licenseManager;
    private volatile ICorpSubscrProfileManager subscrProfileManager;
    private volatile ICorpOsmManager osmManager;
    private volatile ICorpUserProfileManager userProfileManager;
    private volatile ICorpGroupProfileManager groupProfileManager;
    private volatile ICorpDeviceManager deviceManager;
    private volatile ICorpStatsManager statsManager;
    private volatile ICorpAssignUserProfileManager assignUserProfileManager;

    private volatile ICorpAllCloningManager contactCloningManager;

    private volatile ICorpHierarchyManager hierarchyManager;

    private volatile ICorpPTTSettingManager pttSettingManager;





    /**
     * initialize the Logger and configuration manager.
     */
    private static boolean isInitialized = false;
    private static Exception exception;

    //public static final String LIB_INIT_FILE = "libraryInitFile";

    static {
        try {
            //Now, initialize the configuration manager
            knLogger.info( "static", "Initializing config manager");
            KnConfigurationsManager configManager = KnConfigurationsManager.getInstance(KnConstants.LIBRARY_NAME_CORP_MGMT);
            knLogger.info( "static", "Config manager Initialized Successfully - " ,
                    configManager);

            isInitialized = configManager.isInitialized();

        } catch (Exception e) {
            knLogger.fatal( "static", "Initialization failed - " ,  e);
            exception = e;
        }
    }//end of static block//


    public KnCorpClientImpl() {
        if (!isInitialized) {
            knLogger.fatal( "KnCorpClientImpl()", "Corp Mgmt Lirary initalization failed - " ,
                    exception);
            throw new KnSystemError(KnErrorCodes.Initializer.CORP_INIT_FAILED, "Corp Mgmt Library " +
                    "Initalization Failed - " + exception, exception);
        }
    }

    /**
     * @return
     */
    public ICorpActivationManager getCorpActivationManager() {
        if (this.activationManager == null) {
            this.activationManager = new KnCorpActivationManager();
        }
        return this.activationManager;
    }

    /**
     * @return
     */
    public ICorpGenericManager getCorpGenericManager() {
        if (this.genericManager == null) {
            this.genericManager = new KnCorpGenericManager();
        }
        return this.genericManager;
    }


    public ICorpSubscrProfileManager getCorpSubscrProfileManager() {
        if (this.subscrProfileManager == null) {
            this.subscrProfileManager = new KnCorpSubscrProfileManager();
        }
        return this.subscrProfileManager;
    }

    /**
     * @return
     */
    public ICorpContactManager getCorpContactManager() {
        if (this.contactManager == null) {
            this.contactManager = new KnCorpContactManager();
        }
        return this.contactManager;
    }


    /**
     * @return
     */
    public ICorpGroupManager getCorpGroupManager() {
        if (this.groupManager == null) {
            this.groupManager = new KnCorpGroupManager();
        }
        return this.groupManager;
    }

    /**
     * @return
     */
    public ICorpSublistManager getCorpSublistManager() {
        if (this.sublistManager == null) {
            this.sublistManager = new KnCorpSublistManager();
        }
        return this.sublistManager;
    }

    /**
     * @return
     */
    public ICorpLicenseManager getCorpLicenseManager() {
        if (this.licenseManager == null) {
            this.licenseManager = new KnCorpLicenseManager();
        }
        return this.licenseManager;
    }

    public ICorpOsmManager getOsmManager() {
        if(this.osmManager==null){
            this.osmManager=new KnCorpOsmManager();
        }
        return osmManager;
    }

    public ICorpUserProfileManager getUserProfileManager() {
        if(this.userProfileManager==null){
            this.userProfileManager=new KnCorpUserProfileManager();
        }
        return userProfileManager;
    }

    public ICorpAllCloningManager getCorpCloningManager() {
        if (this.contactCloningManager == null) {
            this.contactCloningManager = new KnCorpAllCloningManager();
        }
        return this.contactCloningManager;
    }

    public ICorpGroupProfileManager getGroupProfileManager() {
        if(this.groupProfileManager==null){
            this.groupProfileManager=new KnCorpGroupProfileManager();
        }
        return groupProfileManager;
    }

    public ICorpDeviceManager getDeviceManager() {
        if(this.deviceManager==null){
            this.deviceManager=new KnCorpDeviceManager();
        }
        return deviceManager;
    }

    public ICorpStatsManager getStatsManager(){
        if(this.statsManager == null){
            this.statsManager = new KnCorpStatsManager();
        }
        return statsManager;
    }

    public ICorpAssignUserProfileManager getAssignUserProfileManager() {
        if (this.assignUserProfileManager == null) {
            this.assignUserProfileManager = new KnCorpAssignUserProfileManager();
        }
        return assignUserProfileManager;
    }



    public ICorpPTTSettingManager getPttSettingManager() {
        if(this.pttSettingManager==null){
            this.pttSettingManager=new KnCorpPTTSettingManager();
        }
        return pttSettingManager;
    }

    /**
     * @return
     */
    public ICorpHierarchyManager getHierarchyManager() {
        if (this.hierarchyManager == null) {
            this.hierarchyManager = new KnCorpHierarchyManager();
        }
        return this.hierarchyManager;
    }


    public KnCorpAuthInfoRespDTO authenticate(KnIPCorpAuthInfoDTO authInfoDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "authenticate(KnIPCorpAuthInfoDTO, KnPersisterTxn)", "ENTRY");
        KnCorpAuthInfoRespDTO respDto = getCorpGenericManager().authenticate(authInfoDTO, persisterTxn);
        knLogger.debug( "authenticate(KnIPCorpAuthInfoDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    public KnCorpDirInfoRespDTO getSubsDirectory(KnIPCorpContactDTO contactDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "getSubsDirectory(KnIPCorpContactDTO, KnPersisterTxn)", "ENTRY");
        KnCorpDirInfoRespDTO respDto = getCorpGenericManager().getSubsDirectory(contactDTO, persisterTxn);
        knLogger.debug( "getSubsDirectory(KnIPCorpContactDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    public KnCorpInfoResDTO updateSubscriber(KnIPCorpContactDTO contactDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "updateSubscriber(KnIPCorpContactDTO, KnPersisterTxn)", "ENTRY");
        KnCorpInfoResDTO respDto = getCorpGenericManager().updateSubscriber(contactDTO, persisterTxn);
        knLogger.debug( "updateSubscriber(KnIPCorpContactDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    public KnCorpResponseDTO pairCorpContact(KnIPCorpContactDTO contactDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "pairCorpContact(KnIPCorpContactDTO, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getCorpSublistManager().pairCorpContact(contactDTO, persisterTxn);
        knLogger.debug( "pairCorpContact(KnIPCorpContactDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    public KnCorpContactListRespDTO getCorpMasterList(KnIPCorpInfoDTO corpInfoDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "getCorpMasterList(KnIPCorpInfoDTO, KnPersisterTxn)", "ENTRY");
        KnCorpContactListRespDTO respDto = getCorpContactManager().getCorpMasterList(corpInfoDTO, persisterTxn);
        knLogger.debug( "getCorpMasterList(KnIPCorpInfoDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    public KnCorpSubscContactListRespDTO getCorpResourceList(KnIPCorpContactDTO contactDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "getCorpSubsResourceList(KnIPCorpContactDTO, KnPersisterTxn)", "ENTRY");
        KnCorpSubscContactListRespDTO respDto = getCorpContactManager().getCorpResourceList(contactDTO, persisterTxn);
        knLogger.debug( "getCorpSubsResourceList(KnIPCorpContactDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    public KnCorpSubscContactListRespDTO getCorpSubscContactList(KnIPCorpContactDTO contactDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "getCorpSubscContactList(KnIPCorpContactDTO, KnPersisterTxn)", "ENTRY");
        KnCorpSubscContactListRespDTO respDto = getCorpContactManager().getCorpSubscContactList(contactDTO, persisterTxn);
        knLogger.debug( "getCorpSubscContactList(KnIPCorpContactDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    public KnCorpResponseDTO modifyCorpSubscContacts(KnIPCorpSubscContactListDTO contactListDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "modifyCorpSubscContacts(KnIPCorpSubscContactListDTO, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getCorpContactManager().modifyCorpSubscContacts(contactListDTO, persisterTxn);
        knLogger.debug( "modifyCorpSubscContacts(KnIPCorpSubscContactListDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    public KnCorpResponseDTO cloneCorpSubscContacts(KnIPCorpSubscContactListDTO contactListDTO, KnPersisterTxn persisterTxn) {
        return getCorpContactManager().cloneCorpSubscContacts(contactListDTO, persisterTxn);
    }

    public KnCorpResponseDTO modifyCorpSubscContactsUpmCall(KnIPCorpSubscContactListDTO contactListDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "modifyCorpSubscContactsUpmCall(KnIPCorpSubscContactListDTO, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getCorpContactManager().modifyCorpSubscContactsUpmCall(contactListDTO, persisterTxn);
        knLogger.debug( "modifyCorpSubscContactsUpmCall(KnIPCorpSubscContactListDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    public KnCorpResponseDTO pushSublists(KnIPCorpSublistSubscDistDTO distDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "pushSublists(KnIPCorpSublistSubscDistDTO, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getCorpContactManager().pushSublists(distDTO, persisterTxn);
        knLogger.debug( "pushSublists(KnIPCorpSublistSubscDistDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    public KnCorpResponseDTO removeSublist(KnIPCorpSublistSubscDistDTO subsRequestDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "removeSublist(KnIPCorpSublistSubscDistDTO, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getCorpContactManager().removeSublist(subsRequestDTO, persisterTxn);
        knLogger.debug( "removeSublist(KnIPCorpSublistSubscDistDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    public KnCorpResponseDTO addExtContacts(KnIPCorpContactListDTO contactListDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "addExtContacts(KnIPCorpContactListDTO, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getCorpContactManager().addExtContacts(contactListDTO, persisterTxn);
        knLogger.debug( "addExtContacts(KnIPCorpContactListDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    public KnCorpResponseDTO modifyExtContacts(KnIPCorpContactDTO contactDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "modifyExtContacts(KnIPCorpContactDTO, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getCorpContactManager().modifyExtContacts(contactDTO, persisterTxn);
        knLogger.debug( "modifyExtContacts(KnIPCorpContactDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    public KnCorpResponseDTO removeExtContacts(KnIPCorpContactListDTO contactListDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "removeExtContacts(KnIPCorpContactListDTO, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getCorpContactManager().removeExtContacts(contactListDTO, persisterTxn);
        knLogger.debug( "removeExtContacts(KnIPCorpContactListDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    public KnCorpContactListRespDTO getExtContactDetails(KnIPCorpSubscContactListDTO contactListDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "getExtContactDetails(KnIPCorpSubscContactListDTO, KnPersisterTxn)", "ENTRY");
        KnCorpContactListRespDTO respDto = getCorpContactManager().getExtContactDetails(contactListDTO, persisterTxn);
        knLogger.debug( "getExtContactDetails(KnIPCorpSubscContactListDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    public KnCorpGroupInfoRespDTO createGroup(KnIPCorpGroupInfoDTO groupInfoDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "createGroup(KnIPCorpGroupInfoDTO, KnPersisterTxn)", "ENTRY");
        KnCorpGroupInfoRespDTO respDto = getCorpGroupManager().createGroup(groupInfoDTO, persisterTxn);
        knLogger.debug( "createGroup(KnIPCorpGroupInfoDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    public KnCorpResponseDTO modifyGroup(KnIPCorpGroupInfoDTO groupInfoDTO, KnCorpGrpBasicInfoDTO grpBasicInfo, KnPersisterTxn persisterTxn) {
        knLogger.debug( "modifyGroup(KnIPCorpGroupInfoDTO, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getCorpGroupManager().modifyGroup(groupInfoDTO, grpBasicInfo, persisterTxn);
        knLogger.debug( "modifyGroup(KnIPCorpGroupInfoDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    public KnCorpResponseDTO upmModifyGroup(KnIPCorpGroupInfoDTO groupInfoDTO, KnCorpGrpBasicInfoDTO grpBasicInfo, KnPersisterTxn persisterTxn) {
        knLogger.debug( "upmModifyGroup(KnIPCorpGroupInfoDTO, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getCorpGroupManager().upmModifyGroup(groupInfoDTO, grpBasicInfo, persisterTxn);
        knLogger.debug( "upmModifyGroup(KnIPCorpGroupInfoDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    public KnCorpResponseDTO deleteGroup(KnIPCorpGroupDTO groupInfoDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "deleteGroup(KnIPCorpGroupDTO, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getCorpGroupManager().deleteGroup(groupInfoDTO, persisterTxn);
        knLogger.debug( "deleteGroup(KnIPCorpGroupDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    public KnCorpGroupInfoRespDTO getGroupDetails(KnIPCorpGroupDTO groupInfoDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "getGroupDetails(KnIPCorpGroupDTO, KnPersisterTxn)", "ENTRY");
        KnCorpGroupInfoRespDTO respDto = getCorpGroupManager().getGroupDetails(groupInfoDTO, persisterTxn);
        knLogger.debug( "getGroupDetails(KnIPCorpGroupDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    public KnCorpGroupInfoRespDTO getSubsGroupDetails(KnIPCorpGroupDTO groupInfoDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "getSubsGroupDetails(KnIPCorpGroupDTO, KnPersisterTxn)", "ENTRY");
        KnCorpGroupInfoRespDTO respDto = getCorpGroupManager().getSubsGroupDetails(groupInfoDTO, persisterTxn);
        knLogger.debug( "getSubsGroupDetails(KnIPCorpGroupDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    public KnCorpGroupInfoRespDTO getSubsCorpGroupDetails(KnIPCorpGroupDTO groupInfoDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "getSubsCorpGroupDetails(KnIPCorpGroupDTO, KnPersisterTxn)", "ENTRY");
        KnCorpGroupInfoRespDTO respDto = getCorpGroupManager().getSubsCorpGroupDetails(groupInfoDTO, persisterTxn);
        knLogger.debug( "getSubsCorpGroupDetails(KnIPCorpGroupDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    public KnCorpGroupListRespDTO getGroupList(KnIPCorpInfoDTO corpInfoDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "getGroupList(KnIPCorpInfoDTO, KnPersisterTxn)", "ENTRY");
        KnCorpGroupListRespDTO respDto = getCorpGroupManager().getGroupList(corpInfoDTO, persisterTxn);
        knLogger.debug( "getGroupList(KnIPCorpInfoDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    public KnCorpGroupListRespDTO getSubscriberGroupList(KnIPCorpContactDTO subsCorpInfo, KnPersisterTxn persisterTxn) {
        knLogger.debug( "getSubscriberGroupList(KnIPCorpContactDTO, KnPersisterTxn)", "ENTRY");
        KnCorpGroupListRespDTO respDto = getCorpGroupManager().getSubscriberGroupList(subsCorpInfo, persisterTxn);
        knLogger.debug( "getSubscriberGroupList(KnIPCorpContactDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    public KnCorpGroupListRespDTO getSubscriberLocWatcherGroupList(KnIPCorpContactDTO subscriberCorpInfo, KnPersisterTxn persisterTxn) {
        knLogger.debug( "getSubscriberLocWatcherGroupList(KnIPCorpContactDTO, KnPersisterTxn)", "ENTRY");
        KnCorpGroupListRespDTO respDto = getCorpGroupManager().getSubscriberLocWatcherGroupList(subscriberCorpInfo, persisterTxn);
        knLogger.debug( "getSubscriberLocWatcherGroupList(KnIPCorpContactDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    public KnCorpSublistRespDTO createSublist(KnIPCorpSublistInfoDTO sublistInfoDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "createSublist(KnIPCorpSublistInfoDTO, KnPersisterTxn)", "ENTRY");
        KnCorpSublistRespDTO respDto = getCorpSublistManager().createSublist(sublistInfoDTO, persisterTxn);
        knLogger.debug( "createSublist(KnIPCorpSublistInfoDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    public KnCorpResponseDTO modifySublist(KnIPCorpSublistInfoDTO sublistInfoDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "modifySublist(KnIPCorpSublistInfoDTO, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getCorpSublistManager().modifySublist(sublistInfoDTO, persisterTxn);
        knLogger.debug( "modifySublist(KnIPCorpSublistInfoDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    public KnCorpResponseDTO deleteSublist(KnIPCorpSublistDTO sublistDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "deleteSublist(KnIPCorpSublistDTO, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getCorpSublistManager().deleteSublist(sublistDTO, persisterTxn);
        knLogger.debug( "deleteSublist(KnIPCorpSublistDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    public KnCorpSublistRespDTO getSublistDetails(KnIPCorpSublistDTO sublistReqDto, KnPersisterTxn persisterTxn) {
        knLogger.debug( "getSublistDetails(KnIPCorpSublistDTO, KnPersisterTxn)", "ENTRY");
        KnCorpSublistRespDTO respDto = getCorpSublistManager().getSublistDetails(sublistReqDto, persisterTxn);
        knLogger.debug( "getSublistDetails(KnIPCorpSublistDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    public KnCorpSublistListRespDTO getAllSublist(KnIPCorpInfoDTO corpInfoDto, KnPersisterTxn persisterTxn) {
        knLogger.debug( "getAllSublist(KnIPCorpInfoDTO, KnPersisterTxn)", "ENTRY");
        KnCorpSublistListRespDTO respDto = getCorpSublistManager().getAllSublist(corpInfoDto, persisterTxn);
        knLogger.debug( "getAllSublist(KnIPCorpInfoDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    public KnCorpSublistDistributionRespDTO getDistributionList(KnIPCorpSublistDistDTO distributionInfoDto, KnPersisterTxn persisterTxn) {
        knLogger.debug( "getDistributionList(KnIPCorpSublistDistDTO, KnPersisterTxn)", "ENTRY");
        KnCorpSublistDistributionRespDTO respDto = getCorpSublistManager().getDistributionList(distributionInfoDto, persisterTxn);
        knLogger.debug( "getDistributionList(KnIPCorpSublistDistDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    public KnCorpResponseDTO forceSync(KnIPCorpContactDTO contactDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "forceSync(KnIPCorpContactDTO, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getCorpGenericManager().forceSync(contactDTO, persisterTxn);
        knLogger.debug( "forceSyncContact(KnIPCorpContactDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    public KnCorpInfoResDTO deleteSubscriber(KnIPCorpContactDTO contactDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "deleteSubscriber(KnIPCorpContactDTO, KnPersisterTxn)", "ENTRY");
        KnCorpInfoResDTO respDto = getCorpGenericManager().deleteSubscriber(contactDTO, persisterTxn);
        knLogger.debug( "deleteSubscriber(KnIPCorpContactDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    public KnCorpResponseDTO changeMdn(KnIPCorpContactDTO contactDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "changeMdn(KnIPCorpContactDTO, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getCorpGenericManager().changeMdn(contactDTO, persisterTxn);
        knLogger.debug( "changeMdn(KnIPCorpContactDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    public KnCorpResponseDTO createSubscriber(KnIPCorpContactDTO contactDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "createSubscriber(KnIPCorpContactDTO, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getCorpGenericManager().createSubscriber(contactDTO, persisterTxn);
        knLogger.debug( "createSubscriber(KnIPCorpContactDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    public KnCorpActivationRespDTO getSubscriberEmailId(KnIPCorpActivationDTO clientActRequestDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "generateActivationCode(KnIPCorpActivationDTO, KnPersisterTxn)", "ENTRY");
        KnCorpActivationRespDTO respDto = getCorpActivationManager().getSubscriberEmailId(clientActRequestDTO, persisterTxn);
        knLogger.debug( "generateActivationCode(KnIPCorpActivationDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    public KnCorpActivationRespDTO generateActivationCodes(KnIPCorpActivationDTO clientActRequestDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "generateActivationCodes(KnIPCorpActivationDTO, KnPersisterTxn)", "ENTRY");
        KnCorpActivationRespDTO respDto = getCorpActivationManager().generateActivationCodes(clientActRequestDTO, persisterTxn);
        knLogger.debug( "generateActivationCodes(KnIPCorpActivationDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    public KnCorpActivationRespDTO saveClientActivationCode(KnIPCorpActivationDTO clientActRequestDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "saveClientActivationCode(KnIPCorpActivationDTO, KnPersisterTxn)", "ENTRY");
        KnCorpActivationRespDTO respDto = getCorpActivationManager().saveClientActivationCode(clientActRequestDTO, persisterTxn);
        knLogger.debug( "saveClientActivationCode(KnIPCorpActivationDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    public KnCorpActivationRespDTO getMailInfo(KnIPCorpActivationDTO clientActRequestDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "getMailInfo(KnIPCorpActivationDTO, KnPersisterTxn)", "ENTRY");
        KnCorpActivationRespDTO respDto = getCorpActivationManager().getMailInfo(clientActRequestDTO, persisterTxn);
        knLogger.debug( "getMailInfo(KnIPCorpActivationDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    public KnCorpDispatchGrpMemInfoRespDTO getSubscDetailsToUpdateIsDispatchMem(KnIPCorpDispatchGrpMemInfoDto groupInfoDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "getSubscDetailsToUpdateIsDispatchMem(KnIPCorpDispatchGrpMemInfoDto, KnPersisterTxn)", "ENTRY");
        KnCorpDispatchGrpMemInfoRespDTO respDto = getCorpGroupManager().getSubscDetailsToUpdateIsDispatchMem(groupInfoDTO, persisterTxn);
        knLogger.debug( "getSubscDetailsToUpdateIsDispatchMem(KnIPCorpDispatchGrpMemInfoDto, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    public KnCorpActivationRespDTO sendActivationMail(KnIPCorpActivationDTO clientActRequestDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "sendActivationMail(KnIPCorpActivationDTO, KnPersisterTxn)", "ENTRY");
        KnCorpActivationRespDTO respDto = getCorpActivationManager().sendActivationMail(clientActRequestDTO, persisterTxn);
        knLogger.debug( "sendActivationMail(KnIPCorpActivationDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    public KnCorpActivationRespDTO sendMail(KnIPCorpActivationDTO clientActRequestDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "sendMail(KnIPCorpActivationDTO, KnPersisterTxn)", "ENTRY");
        KnCorpActivationRespDTO respDto = getCorpActivationManager().sendMail(clientActRequestDTO, persisterTxn);
        knLogger.debug( "sendMail(KnIPCorpActivationDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    public KnCorpPAMSubsDTO getUnusedSubsList(KnIPCorpPAMSubsDTO pamSubsDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "getUnusedSubsList(KnIPCorpPAMSubsDTO, KnPersisterTxn)", "ENTRY");
        KnCorpPAMSubsDTO respDto = getCorpGenericManager().getUnusedSubsList(pamSubsDTO, persisterTxn);
        knLogger.debug( "getUnusedSubsList(KnIPCorpPAMSubsDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    public KnCorpResponseDTO cleanCorpData(KnIPCorpPAMSubsDTO pamSubsDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "cleanCorpData(KnIPCorpPAMSubsDTO, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getCorpGenericManager().cleanCorpData(pamSubsDTO, persisterTxn);
        knLogger.debug( "cleanCorpData(KnIPCorpPAMSubsDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

	@Override
	public KnCorpResponseDTO modifySubsCorpFeature(KnIPSubsProvInfoDTO subsProvInfoDTO, KnPersisterTxn persisterTxn) {
		knLogger.debug( "modifySubsCorpFeature(KnIPTalkGroupDTO, KnPersisterTxn)", "ENTRY");
		KnCorpResponseDTO respDto = getCorpGenericManager().modifySubsCorpFeature(subsProvInfoDTO, persisterTxn);
	    knLogger.debug( "modifySubsCorpFeature(KnIPTalkGroupDTO, KnPersisterTxn)", "EXIT");
	    return respDto;
	}

	@Override
	public KnCorpResponseDTO modifySubscriberScanList(KnIPTalkGroupDTO ipTalkGroupDTO, KnPersisterTxn persisterTxn) {
		knLogger.debug( "modifySubscriberScanList(KnIPTalkGroupDTO, KnPersisterTxn)", "ENTRY");
		KnCorpResponseDTO respDto = getCorpGroupManager().modifySubscriberScanList(ipTalkGroupDTO, persisterTxn);
	    knLogger.debug( "modifySubscriberScanList(KnIPTalkGroupDTO, KnPersisterTxn)", "EXIT");
	    return respDto;
	}

    @Override
    public KnCorpResponseDTO upmModifySubscriberScanList(KnIPTalkGroupDTO ipTalkGroupDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "upmModifySubscriberScanList(KnIPTalkGroupDTO, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getCorpGroupManager().upmModifySubscriberScanList(ipTalkGroupDTO, persisterTxn);
        knLogger.debug( "upmModifySubscriberScanList(KnIPTalkGroupDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    @Override
    public KnCorpResponseDTO upmModifyBulkSubscriberScanList(KnIPTalkGroupDTO ipTalkGroupDTO, KnPersisterTxn persisterTxn) {
        KnCorpResponseDTO respDto = getCorpGroupManager().upmModifyBulkSubscriberScanList(ipTalkGroupDTO, persisterTxn);
        return respDto;
    }

	@Override
	public KnXDMTalkGroupServerRespDTO getSubscriberScanList(KnIPTalkGroupDTO ipTalkGroupDTO, KnPersisterTxn persisterTxn) {
		knLogger.debug( "getSubscriberScanList(KnIPTalkGroupDTO, KnPersisterTxn)", "ENTRY");
		KnXDMTalkGroupServerRespDTO respDto = getCorpGroupManager().getSubscriberScanList(ipTalkGroupDTO, persisterTxn);
	    knLogger.debug( "getSubscriberScanList(KnIPTalkGroupDTO, KnPersisterTxn)", "EXIT");
	    return respDto;
	}

	@Override
	public KnCorpTalkGroupRespDTO cleanUpSubsCampedGrps(KnIPSubsDTO ipSubsDTO, KnPersisterTxn persisterTxn) {
		knLogger.debug( "cleanUpSubsCampedGrps(mdn, persisterTxn)", "ENTRY");
		KnCorpTalkGroupRespDTO respDto = getCorpGroupManager().cleanUpSubsCampedGrps(ipSubsDTO, persisterTxn);
	    knLogger.debug( "cleanUpSubsCampedGrps(mdn, persisterTxn)", "EXIT");
	    return respDto;
	}

    @Override
    public KnCorpTalkGroupRespDTO deleteSubsScanlist(KnIPSubsDTO ipSubsDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "deleteSubscriberScanList(KnIPSubsDTO, KnPersisterTxn)", "ENTRY");
        KnCorpTalkGroupRespDTO respDto = getCorpGroupManager().deleteSubsScanlist(ipSubsDTO, persisterTxn);
        knLogger.debug( "deleteSubscriberScanList(KnIPSubsDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    @Override
    public KnCorpResponseDTO deleteSubscriberScanList(KnIPTalkGroupDTO ipTalkGroupDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "deleteSubscriberScanList(KnIPTalkGroupDTO, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getCorpGroupManager().deleteSubscriberScanList(ipTalkGroupDTO, persisterTxn);
        knLogger.debug( "deleteSubscriberScanList(KnIPTalkGroupDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    @Override
	public KnCorpResponseDTO modifySubscriberScanListXcap(KnIPTalkGroupDTO ipTalkGroupDTO, KnPersisterTxn persisterTxn) {
		knLogger.debug( "modifySubscriberScanList(KnIPTalkGroupDTO, KnPersisterTxn)", "ENTRY");
		KnCorpResponseDTO respDto = getCorpGroupManager().modifySubscriberScanListXcap(ipTalkGroupDTO, persisterTxn);
	    knLogger.debug( "modifySubscriberScanList(KnIPTalkGroupDTO, KnPersisterTxn)", "EXIT");
	    return respDto;
	}
    @Override
    public KnXDMTalkGroupServerRespDTO getSubscriberScanListXcap(KnIPTalkGroupDTO ipTalkGroupDTO, KnPersisterTxn persisterTxn){
        knLogger.debug( "getSubscriberScanListXcap(KnIPTalkGroupDTO, KnPersisterTxn)", "ENTRY");
        KnXDMTalkGroupServerRespDTO respDto = getCorpGroupManager().getSubscriberScanListXcap(ipTalkGroupDTO, persisterTxn);
        knLogger.debug( "getSubscriberScanListXcap(KnIPTalkGroupDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    @Override
    public KnCorpResponseDTO modifySubscriberScanListXcapClients(KnIPTalkGroupDTO ipTalkGroupDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "modifySubscriberScanList(KnIPTalkGroupDTO, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getCorpGroupManager().modifySubscriberScanListXcapClients(ipTalkGroupDTO, persisterTxn);
        knLogger.debug( "modifySubscriberScanList(KnIPTalkGroupDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    @Override
    public KnCorpGroupInfoRespDTO createBCGroup(KnIPCorpGroupInfoDTO groupInfoDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "createBCGroup(KnIPCorpGroupInfoDTO, KnPersisterTxn)", "ENTRY");
        KnCorpGroupInfoRespDTO respDto = getCorpGroupManager().createBCGroup(groupInfoDTO, persisterTxn);
        knLogger.debug( "createBCGroup(KnIPCorpGroupInfoDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    @Override
    public KnCorpGrpBasicInfoRespDto getBasicGrpInfo(KnIPCorpGroupInfoDTO groupInfoDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "getBasicGrpInfo(KnIPCorpGroupInfoDTO, KnPersisterTxn)", "ENTRY");
        KnCorpGrpBasicInfoRespDto respDto = getCorpGroupManager().getBasicGrpInfo(groupInfoDTO, persisterTxn);
        knLogger.debug( "getBasicGrpInfo(KnIPCorpGroupInfoDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    public KnCorpResponseDTO modifyBCGroup(KnIPCorpGroupInfoDTO groupInfoDTO, KnCorpGrpBasicInfoDTO grpBasicInfo, KnPersisterTxn persisterTxn) {
        knLogger.debug( "modifyBCGroup(KnIPCorpGroupInfoDTO, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getCorpGroupManager().modifyBCGroup(groupInfoDTO, grpBasicInfo, persisterTxn);
        knLogger.debug( "modifyBCGroup(KnIPCorpGroupInfoDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    public KnCorpResponseDTO upmModifyBCGroup(KnIPCorpGroupInfoDTO groupInfoDTO, KnCorpGrpBasicInfoDTO grpBasicInfo, KnPersisterTxn persisterTxn) {
        knLogger.debug( "upmModifyBCGroup(KnIPCorpGroupInfoDTO, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getCorpGroupManager().upmModifyBCGroup(groupInfoDTO, grpBasicInfo, persisterTxn);
        knLogger.debug( "upmModifyBCGroup(KnIPCorpGroupInfoDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    public KnCorpResponseDTO updateGrpBroadcasters(String mdn, KnPersisterTxn persisterTxn) {
        knLogger.debug( "updateGrpBroadcasters(String, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getCorpGroupManager().updateGrpBroadcasters(mdn, persisterTxn);
        knLogger.debug( "updateGrpBroadcasters(String, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    @Override
    public KnLinkedGroupInfoRespDTO getPocLinkedGroupList(KnIPLinkedGroupInfoDTO pocGrpListDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "getPocLinkedGroupList(KnIPLinkedGroupInfoDTO, KnPersisterTxn)", "ENTRY");
        KnLinkedGroupInfoRespDTO respDto = getCorpGroupManager().getPocLinkedGroupList(pocGrpListDTO, persisterTxn);
        knLogger.debug( "getPocLinkedGroupList(KnIPLinkedGroupInfoDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    public KnCorpAuthInfoRespDTO licenseAuthenticate(KnIPCorpAuthInfoDTO authInfoDTO, KnPersisterTxn persisterTxn) {
        final String mName = "licenseAuthenticate(KnIPCorpAuthInfoDTO, KnPersisterTxn)";
        knLogger.entry(mName);
        KnCorpAuthInfoRespDTO respDto = getCorpLicenseManager().licenseAuthenticate(authInfoDTO, persisterTxn);
        knLogger.exit(mName);
        return respDto;
    }

    @Override
    public KnCorpLicensePackListRespDTO getAllBillingMdns(KnIPCorpInfoDTO corpInfoDTO, KnPersisterTxn persisterTxn) {
        final String mName = "getAllBillingMdns(KnIPCorpAuthInfoDTO, KnPersisterTxn)";
        knLogger.entry(mName);
        KnCorpLicensePackListRespDTO respDto = getCorpLicenseManager().getAllBillingMdns(corpInfoDTO, persisterTxn);
        knLogger.exit(mName);
        return respDto;
    }

    @Override
    public KnCorpLicenseSubsListRespDTO getLicenseSubscribers(KnIPLicenseSubsListDTO licenseSubsListDTO,
                                                              KnPersisterTxn persisterTxn) {
        final String mName = "getLicenseSubscribers(KnIPLicenseSubsListDTO, KnPersisterTxn)";
        knLogger.entry(mName);
        KnCorpLicenseSubsListRespDTO respDto = getCorpLicenseManager().getLicenseSubscribers(licenseSubsListDTO, persisterTxn);
        knLogger.exit(mName);
        return respDto;
    }

    @Override
	public KnCorpResponseDTO updateBillingName(KnIPLicenseSubsListDTO ipLicenseSubsListDTO, KnPersisterTxn persisterTxn) {
    	  final String mName = "updateBillingName(KnIPLicenseSubsListDTO, KnPersisterTxn)";
          knLogger.entry(mName);
          KnCorpResponseDTO respDto = getCorpLicenseManager().updateBillingName(ipLicenseSubsListDTO, persisterTxn);
          knLogger.exit(mName);
          return respDto;
	}

    @Override
    public KnCorpResponseDTO markForDelete(KnIPLicenseSubsListDTO licenseSubsListDTO, KnPersisterTxn persisterTxn) {
        final String mName = "markForDelete(KnIPLicenseSubsListDTO, KnPersisterTxn)";
        knLogger.entry(mName);
        KnCorpResponseDTO respDto = getCorpLicenseManager().markForDelete(licenseSubsListDTO, persisterTxn);
        knLogger.exit(mName);
        return respDto;
    }

    @Override
    public KnCorpLicenseSubsListRespDTO getLicenseSubscribersForCSR(KnIPLicenseSubsListDTO ipLicenseSubsListDTO, KnPersisterTxn persisterTxn) {
        final String mName = "getLicenseSubscribersForCSR(KnIPLicenseSubsListDTO, KnPersisterTxn)";
        knLogger.entry(mName);
        KnCorpLicenseSubsListRespDTO respDto = getCorpLicenseManager().getLicenseSubscribersForCSR(ipLicenseSubsListDTO, persisterTxn);
        knLogger.exit(mName);
        return respDto;
    }

    /**
     * Method to return the subscriber list where the request MDN exist as contact.
     * @param contactDTO
     * @param persisterTxn
     * @return KnReverseContactsRespDto
     */
    @Override
    public KnReverseContactsRespDto getSubscrReverseContacts(KnIPCorpContactDTO contactDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "getSubscrReverseContacts(KnIPCorpContactDTO, KnPersisterTxn)", "ENTRY");
        KnReverseContactsRespDto respDto = getCorpContactManager().getSubscrReverseContacts(contactDTO, persisterTxn);
        knLogger.debug( "getSubscrReverseContacts(KnIPCorpContactDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    /**
     * This method is to retrieve the list of sublists where the request MDN exist as member.
     * @param contactDTO
     * @param persisterTxn
     * @return KnCorpSublistListRespDTO
     */
    @Override
    public KnCorpSublistListRespDTO getSubscrSublists(KnIPCorpContactDTO contactDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "getSubscrSublists(KnIPCorpContactDTO, KnPersisterTxn)", "ENTRY");
        KnCorpSublistListRespDTO respDto = getCorpSublistManager().getSubscrSublists(contactDTO, persisterTxn);
        knLogger.debug( "getSubscrSublists(KnIPCorpContactDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    /**
     * Method to remove all the contacts for the subscribers
     * @param subscriberInfoDTO
     * @param persisterTxn
     * @return KnCorpResponseDTO
     */

    public KnCorpResponseDTO removeSubscribersContacts(KnIPCorpContactDTO subscriberInfoDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "removeSubscribersContacts(KnIPSubscriberInfoDTO, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getCorpContactManager().removeSubscribersContacts(subscriberInfoDTO, persisterTxn);
        knLogger.debug( "removeSubscribersContacts(KnIPSubscriberInfoDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    /**
     * Method to remove the subscriber from all the shared sublist where he is a member.
     * @param subscriberInfoDTO
     * @param persisterTxn
     * @return KnCorpResponseDTO
     */
    @Override
    public KnCorpResponseDTO removeSubscribersAllSublist(KnIPCorpContactDTO subscriberInfoDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "removeSubscribersAllSublist(KnIPSubscriberInfoDTO, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getCorpSublistManager().removeSubscribersAllSublist(subscriberInfoDTO, persisterTxn);
        knLogger.debug( "removeSubscribersAllSublist(KnIPSubscriberInfoDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    /**
     * Method to remove him from all the groups private list.
     * @param subscriberInfoDTO
     * @param persisterTxn
     * @return KnCorpResponseDTO
     */
    @Override
    public KnCorpResponseDTO removeSubscribersAllGroups(KnIPCorpContactDTO subscriberInfoDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "removeSubscribersAllGroups(KnIPSubscriberInfoDTO, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getCorpGroupManager().removeSubscribersAllGroups(subscriberInfoDTO, persisterTxn);
        knLogger.debug( "removeSubscribersAllGroups(KnIPSubscriberInfoDTO, KnPersisterTxn)", "EXIT");
        return respDto;

    }

    /**
     * This method is used to update the corporate auto pairing based on the parameter passed in the pairing indicator
     *
     * @param corpContactDTO
     * @param persisterTxn
     * @return
     */
    @Override
    public KnCorpAutoPairingResponse updateCorpAutoPairing(KnIPCorpInfoDTO corpContactDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "updateCorpAutoPairing(KnIPCorpContactDTO, KnPersisterTxn)", "ENTRY");
        KnCorpAutoPairingResponse respDto = getCorpSublistManager().updateCorpAutoPairing(corpContactDTO, persisterTxn);
        knLogger.debug( "updateCorpAutoPairing(KnIPCorpContactDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    /**
     * This method is use to add the subscriber to then paired list of the corporate.
     * @param contactDTO
     * @param persisterTxn
     * @return
     */
    @Override
    public KnCorpAutoPairingResponse addToPairingList(KnIPCorpContactDTO contactDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "addToPairingList(KnIPCorpContactDTO, KnPersisterTxn)", "ENTRY");
        KnCorpAutoPairingResponse respDto = getCorpSublistManager().addToPairingList(contactDTO, persisterTxn);
        knLogger.debug( "addToPairingList(KnIPCorpContactDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }


    @Override
    public KnSubscrFeatureSetRespDTO getAllCorpSubscrFeatureSets(KnIPCorpAuthInfoDTO ipCorpAuthInfoDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "getAllCorpSubscrFeatureSets(KnIPCorpAuthInfoDTO, KnPersisterTxn)", "ENTRY");
        KnSubscrFeatureSetRespDTO respDto = getCorpSubscrProfileManager().getAllCorpSubscrFeatureSets(ipCorpAuthInfoDTO, persisterTxn);
        knLogger.debug( "getAllCorpSubscrFeatureSets(KnIPCorpAuthInfoDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    @Override
    public KnCorpResponseDTO updateCorpAdminFS(KnIPSubscrFeatureInfoDTO ipSubscrFeatureInfoDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "updateCorpAdminFS(KnIPSubscrFeatureInfoDTO, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getCorpSubscrProfileManager().updateCorpAdminFS(ipSubscrFeatureInfoDTO, persisterTxn);
        knLogger.debug( "updateCorpAdminFS(KnIPSubscrFeatureInfoDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    @Override
    public KnCorpResponseDTO cloneCorpAdminFS(KnIPSubscrFeatureInfoDTO ipSubscrFeatureInfoDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "cloneCorpAdminFS(KnIPSubscrFeatureInfoDTO, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getCorpSubscrProfileManager().cloneCorpAdminFS(ipSubscrFeatureInfoDTO, persisterTxn);
        knLogger.debug( "cloneCorpAdminFS(KnIPSubscrFeatureInfoDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    @Override
    public KnCorpGetCorpFSResponse getCorporateFS(KnIPSubscriberInfoDTO ipSubscrFeatureInfoDTO, KnPersisterTxn persisterTxn) {
        return getCorpSubscrProfileManager().getCorporateFS(ipSubscrFeatureInfoDTO, persisterTxn);
    }

    @Override
    public KnCorpActivationRespDTO getActivationCode(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "getActivationCode(KnIPSubscriberInfoDTO, KnPersisterTxn)", "ENTRY");
        KnCorpActivationRespDTO respDto = getCorpSubscrProfileManager().getActivationCode(ipSubscriberInfoDTO, persisterTxn);
        knLogger.debug( "getActivationCode(KnIPSubscriberInfoDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    @Override
    public KnCorpResponseDTO getCorpSubscriberDetails(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, boolean readOnly, KnPersisterTxn persisterTxn) {
        knLogger.debug( "getCorpSubscriberDetails(KnIPSubscriberInfoDTO,boolean, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getCorpSubscrProfileManager().getCorpSubscriberDetails(ipSubscriberInfoDTO, readOnly, persisterTxn);
        knLogger.debug( "getCorpSubscriberDetails(KnIPSubscriberInfoDTO, boolean, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    public KnCorpContactListRespDTO getCorpExtContactDetails(KnIPCorpSubscContactListDTO contactListDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "getCorpExtContactDetails(KnIPCorpSubscContactListDTO, KnPersisterTxn)", "ENTRY");
        KnCorpContactListRespDTO respDto = getCorpContactManager().getCorpExtContactDetails(contactListDTO, persisterTxn);
        knLogger.debug( "getCorpExtContactDetails(KnIPCorpSubscContactListDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    @Override
    public KnCorpProfileInfoRespDTO getCorporateProfile(KnIPSubsProvInfoDTO subsProvInfoDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "getCorporateProfile(KnIPSubsProvInfoDTO, KnPersisterTxn)", "ENTRY");
        KnCorpProfileInfoRespDTO respDto = getCorpGenericManager().getCorporateProfile(subsProvInfoDTO, persisterTxn);
        knLogger.debug( "getCorporateProfile(KnIPSubsProvInfoDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    public KnCorpResponseDTO switchConvergedClient(KnIPSubscrFeatureInfoDTO ipSubscrFeatureInfoDTO, KnPersisterTxn persisterTxn){
        knLogger.debug( "switchConvergedClient(KnIPSubscrFeatureInfoDTO, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getCorpSubscrProfileManager().switchConvergedClient(ipSubscrFeatureInfoDTO, persisterTxn);
        knLogger.debug( "switchConvergedClient(KnIPSubscrFeatureInfoDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    @Override
    public KnCorpActivationRespDTO getSubscrActivationCode(KnIPCorpActivationDTO activationDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "getSubscrActivationCode(KnIPCorpActivationDTO, KnPersisterTxn)", "ENTRY");
        KnCorpActivationRespDTO respDto = getCorpActivationManager().getSubscrActivationCode(activationDTO, persisterTxn);
        knLogger.debug( "getSubscrActivationCode(KnIPCorpActivationDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    @Override
    public KnCorpActivationRespDTO getTempPwdForLegacy(KnIPCorpActivationDTO activationDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "getTempPwdForLegacy(KnIPCorpActivationDTO, KnPersisterTxn)", "ENTRY");
        KnCorpActivationRespDTO respDto = getCorpActivationManager().getTempPwdForLegacy(activationDTO, persisterTxn);
        knLogger.debug( "getTempPwdForLegacy(KnIPCorpActivationDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    @Override
    public KnCorpActivationRespDTO generateOTP(KnIPCorpActivationDTO activationDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "generateOTP(KnIPCorpActivationDTO, KnPersisterTxn)", "ENTRY");
        KnCorpActivationRespDTO respDto = getCorpActivationManager().generateOTP(activationDTO, persisterTxn);
        knLogger.debug( "generateOTP(KnIPCorpActivationDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    @Override
    public KnCorpActivationRespDTO validateOTP(KnIPCorpActivationDTO activationDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "validateOTP(KnIPCorpActivationDTO, KnPersisterTxn)", "ENTRY");
        KnCorpActivationRespDTO respDto = getCorpActivationManager().validateOTP(activationDTO, persisterTxn);
        knLogger.debug( "validateOTP(KnIPCorpActivationDTO, KnPersisterTxn)", "EXIT");
        return respDto;    }

    @Override
    public KnCorpActivationRespDTO getCorpBanFanDetails(KnIPCorpActivationDTO activationDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "getCorpBanFanDetails(KnIPCorpActivationDTO, KnPersisterTxn)", "ENTRY");
        KnCorpActivationRespDTO respDto = getCorpActivationManager().getCorpBanFanDetails(activationDTO, persisterTxn);
        knLogger.debug( "getCorpBanFanDetails(KnIPCorpActivationDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    public Long calculateFeatureBit(int corpid,KnPersisterTxn persisterTxn) throws  Exception{
        Long bitsLongVal = getCorpGenericManager().calculateFeatureBit(corpid,persisterTxn);
        return bitsLongVal;
    }

    @Override
    public KnCorpResponseDTO updateCorpSubscriber(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "updateCorpSubscriber(KnIPSubscriberInfoDTO, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getCorpSubscrProfileManager().updateCorpSubscriber(ipSubscriberInfoDTO, persisterTxn);
        knLogger.debug( "updateCorpSubscriber(KnIPSubscriberInfoDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    @Override
    public KnCorpResponseDTO validateSubscrClient(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn) throws KnException, KnFWException {

        knLogger.debug( "updateCorpSubscriber(KnIPSubscriberInfoDTO, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO knCorpResponseDTO =getCorpSubscrProfileManager().validateSubscrClient(ipSubscriberInfoDTO, persisterTxn);
        knLogger.debug( "updateCorpSubscriber(KnIPSubscriberInfoDTO, KnPersisterTxn)", "EXIT");
        return knCorpResponseDTO;
    }

    @Override
    public KnCorpResponseDTO validateGetSubscrClient(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn) throws KnFWException, KnException {
        knLogger.debug( "validateGetSubscrClient(KnIPSubscriberInfoDTO, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO knCorpResponseDTO =getCorpSubscrProfileManager().validateGetSubscrClient(ipSubscriberInfoDTO, persisterTxn);
        knLogger.debug( "validateGetSubscrClient(KnIPSubscriberInfoDTO, KnPersisterTxn)", "EXIT");
        return knCorpResponseDTO;
    }

    @Override
    public KnCorpActivationRespDTO generateActivationCodeIDMIntf(KnIPCorpActivationDTO activationDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "generateActivationCodeIDMIntf(KnIPCorpActivationDTO, KnPersisterTxn)", "ENTRY");
        KnCorpActivationRespDTO respDto = getCorpActivationManager().generateActivationCodeIDMIntf(activationDTO, persisterTxn);
        knLogger.debug( "generateActivationCodeIDMIntf(KnIPCorpActivationDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    @Override
    public KnCorpLITargetInfoRespDTO getLITargetInfo(KnPersisterTxn persisterTxn){
        knLogger.debug( "getLITargetInfo(KnPersisterTxn)", "ENTRY");
        KnCorpLITargetInfoRespDTO respDto = getCorpGenericManager().getLITargetInfo(persisterTxn);
        knLogger.debug( "getLITargetInfo(KnPersisterTxn)", "EXIT");
        return respDto;
    }

    public KnCorpResponseDTO getCorpSubsUserProfile(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn){
        knLogger.debug( "getCorpSubsUserProfile(KnIPSubscriberInfoDTO, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getCorpSubscrProfileManager().getCorpSubsUserProfile(ipSubscriberInfoDTO, persisterTxn);
        knLogger.debug( "getCorpSubsUserProfile(KnIPSubscriberInfoDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    public KnCorpResponseDTO resetCorpSubsUserPassword(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn){
        knLogger.debug( "resetCorpSubsUserPassword(KnIPSubscriberInfoDTO, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getCorpSubscrProfileManager().resetCorpSubsUserPassword(ipSubscriberInfoDTO, persisterTxn);
        knLogger.debug( "resetCorpSubsUserPassword(KnIPSubscriberInfoDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    public KnCorpResponseDTO resendCorpSubsVerificationEmail(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn){
        knLogger.debug( "resendCorpSubsVerificationEmail(KnIPSubscriberInfoDTO, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getCorpSubscrProfileManager().resendCorpSubsVerificationEmail(ipSubscriberInfoDTO, persisterTxn);
        knLogger.debug( "resendCorpSubsVerificationEmail(KnIPSubscriberInfoDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    @Override
    public KnCorpResponseDTO setTargetPermissions(KnIPAuthUserPermissionInfoDTO ipAuthUserPermissionInfoDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "setTargetPermissions(KnIPAuthUserPermissionInfoDTO, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getCorpSubscrProfileManager().setTargetPermissions(ipAuthUserPermissionInfoDTO, persisterTxn);
        knLogger.debug( "setTargetPermissions(KnIPAuthUserPermissionInfoDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    @Override
    public KnCorpResponseDTO upmSetTargetPermissions(KnIPAuthUserPermissionInfoDTO ipAuthUserPermissionInfoDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "upmSetTargetPermissions(KnIPAuthUserPermissionInfoDTO, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getCorpSubscrProfileManager().upmSetTargetPermissions(ipAuthUserPermissionInfoDTO, persisterTxn);
        knLogger.debug( "upmSetTargetPermissions(KnIPAuthUserPermissionInfoDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    @Override
    public KnCorpUserPermissionRespDTO getTargetPermissions(KnIPAuthUserPermissionInfoDTO ipAuthUserPermissionInfoDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "getTargetPermissions(KnIPAuthUserPermissionInfoDTO, KnPersisterTxn)", "ENTRY");
        KnCorpUserPermissionRespDTO respDto = getCorpSubscrProfileManager().getTargetPermissions(ipAuthUserPermissionInfoDTO, persisterTxn);
        knLogger.debug( "getTargetPermissions(KnIPAuthUserPermissionInfoDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    @Override
    public KnCorpUserPermissionRespDTO getSubsTargetPermissions(KnIPAuthUserPermissionInfoDTO ipAuthUserPermissionInfoDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "getSubsTargetPermissions(KnIPAuthUserPermissionInfoDTO, KnPersisterTxn)", "ENTRY");
        KnCorpUserPermissionRespDTO respDto = getCorpSubscrProfileManager().getSubsTargetPermissions(ipAuthUserPermissionInfoDTO, persisterTxn);
        knLogger.debug( "getSubsTargetPermissions(KnIPAuthUserPermissionInfoDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    @Override
    public KnCorpUserPermissionRespDTO getAuthorizedMdnList(KnIPAuthUserPermissionInfoDTO ipAuthUserPermissionInfoDTO, boolean readOnly, KnPersisterTxn persisterTxn) {
        knLogger.debug( "getAuthorizedMdnList(KnIPAuthUserPermissionInfoDTO, boolean, KnPersisterTxn)", "ENTRY");
        KnCorpUserPermissionRespDTO respDto = getCorpSubscrProfileManager().getAuthorizedMdnList(ipAuthUserPermissionInfoDTO, readOnly, persisterTxn);
        knLogger.debug( "getAuthorizedMdnList(KnIPAuthUserPermissionInfoDTO, boolean, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    @Override
    public KnCorpResponseDTO setSubsEmergencyAttributes(KnIPEmergencyInfoDTO ipEmergencyInfoDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "setSubsEmergencyAttributes(KnIPAuthUserPermissionInfoDTO, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getCorpSubscrProfileManager().setSubsEmergencyAttributes(ipEmergencyInfoDTO, persisterTxn);
        knLogger.debug( "setSubsEmergencyAttributes(KnIPAuthUserPermissionInfoDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    @Override
    public KnCorpResponseDTO upmSetSubsEmergencyAttributes(KnIPEmergencyInfoDTO ipEmergencyInfoDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "upmSetSubsEmergencyAttributes(KnIPAuthUserPermissionInfoDTO, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getCorpSubscrProfileManager().upmSetSubsEmergencyAttributes(ipEmergencyInfoDTO, persisterTxn);
        knLogger.debug( "upmSetSubsEmergencyAttributes(KnIPAuthUserPermissionInfoDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    @Override
    public KnCorpUserEmergencyAttributesRespDTO getSubsEmergencyAttributes(KnIPEmergencyInfoDTO ipEmergencyInfoDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "getSubsEmergencyAttributes(KnIPAuthUserPermissionInfoDTO, KnPersisterTxn)", "ENTRY");
        KnCorpUserEmergencyAttributesRespDTO respDto = getCorpSubscrProfileManager().getSubsEmergencyAttributes(ipEmergencyInfoDTO, persisterTxn);
        knLogger.debug( "getSubsEmergencyAttributes(KnIPAuthUserPermissionInfoDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    public KnEmergUserDestRespDTO getUserEmergDest(KnIPEmergencyInfoDTO ipEmergencyInfoDTO, KnPersisterTxn persisterTxn){
        knLogger.debug( "getUserEmergDest(KnIPAuthUserPermissionInfoDTO, KnPersisterTxn)", "ENTRY");
        KnEmergUserDestRespDTO respDto = getCorpSubscrProfileManager().getUserEmergDest(ipEmergencyInfoDTO, persisterTxn);
        knLogger.debug( "getUserEmergDest(KnIPAuthUserPermissionInfoDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    public KnCorpGroupListRespDTO getTpSubscriberGroupList(KnIPCorpContactDTO subscriberCorpInfo, KnPersisterTxn persisterTxn) {
        knLogger.debug( "getTpSubscriberGroupList(KnIPCorpContactDTO, KnPersisterTxn)", "ENTRY");
        KnCorpGroupListRespDTO respDto = getCorpGroupManager().getTpSubscriberGroupList(subscriberCorpInfo, persisterTxn);
        knLogger.debug( "getTpSubscriberGroupList(KnIPCorpContactDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    public KnCorpGroupInfoRespDTO getTpSubsCorpGroupDetails(KnIPCorpGroupDTO groupInfoDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "getTpSubsCorpGroupDetails(KnIPCorpGroupDTO, KnPersisterTxn)", "ENTRY");
        KnCorpGroupInfoRespDTO respDto = getCorpGroupManager().getTpSubsCorpGroupDetails(groupInfoDTO, persisterTxn);
        knLogger.debug( "getTpSubsCorpGroupDetails(KnIPCorpGroupDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    @Override
    public KnCorpGrpBasicInfoRespDto getBasicGrpInfoByGrpName(KnIPCorpGroupInfoDTO groupInfoDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "getBasicGrpInfoByGrpName(KnIPCorpGroupInfoDTO, KnPersisterTxn)", "ENTRY");
        KnCorpGrpBasicInfoRespDto respDto = getCorpGroupManager().getBasicGrpInfoByGrpName(groupInfoDTO, persisterTxn);
        knLogger.debug( "getBasicGrpInfoByGrpName(KnIPCorpGroupInfoDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    public void subsEtagUpdate(String mdn,String operationType) {
        knLogger.debug( "subsEtagUpdate(String, KnPersisterTxn)", "ENTRY");
        getCorpGenericManager().subsEtagUpdate(mdn,operationType);
        knLogger.debug( "subsEtagUpdate(String, KnPersisterTxn)", "EXIT");
    }

    @Override
    public KnCorpResponseDTO updateSubsAliasEntities(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "updateSubsAliasEntities(KnIPSubscriberInfoDTO, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getCorpSubscrProfileManager().updateSubsAliasEntities(ipSubscriberInfoDTO, persisterTxn);
        knLogger.debug( "updateSubsAliasEntities(KnIPSubscriberInfoDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    public KnCorpResponseDTO generateTempPassword(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn){
        knLogger.debug( "generateTempPassword(KnIPSubscriberInfoDTO, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getCorpSubscrProfileManager().generateTempPassword(ipSubscriberInfoDTO, persisterTxn);
        knLogger.debug( "generateTempPassword(KnIPSubscriberInfoDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    public KnCorpResponseDTO sendTempPassword(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn){
        knLogger.debug( "sendTempPassword(KnIPSubscriberInfoDTO, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getCorpSubscrProfileManager().sendTempPassword(ipSubscriberInfoDTO, persisterTxn);
        knLogger.debug( "sendTempPassword(KnIPSubscriberInfoDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    @Override
    public KnCorpResponseDTO modifySubscriberTGList(KnIPTalkGroupDTO ipTalkGroupDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "modifySubscriberTGList(KnIPTalkGroupDTO, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getCorpGroupManager().modifySubscriberTGList(ipTalkGroupDTO, persisterTxn);
        knLogger.debug( "modifySubscriberTGList(KnIPTalkGroupDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    @Override
    public KnCorpResponseDTO upmModifySubscriberTGList(KnIPTalkGroupDTO ipTalkGroupDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "upmModifySubscriberTGList(KnIPTalkGroupDTO, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getCorpGroupManager().upmModifySubscriberTGList(ipTalkGroupDTO, persisterTxn);
        knLogger.debug( "upmModifySubscriberTGList(KnIPTalkGroupDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    @Override
    public KnCorpResponseDTO modifyBulkSubscriberTGList(KnIPTalkGroupDTO ipTalkGroupDTO, KnPersisterTxn persisterTxn) {
        KnCorpResponseDTO respDto = getCorpGroupManager().modifySubscriberTGList(ipTalkGroupDTO, persisterTxn);
        return respDto;
    }

    @Override
    public KnXDMTalkGroupServerRespDTO getSubscriberTGList(KnIPTalkGroupDTO ipTalkGroupDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "getSubscriberTGList(KnIPTalkGroupDTO, KnPersisterTxn)", "ENTRY");
        KnXDMTalkGroupServerRespDTO respDto = getCorpGroupManager().getSubscriberTGList(ipTalkGroupDTO, persisterTxn);
        knLogger.debug( "getSubscriberTGList(KnIPTalkGroupDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    @Override
    public KnXDMTalkGroupServerRespDTO getSubsTGList(KnIPTalkGroupDTO ipTalkGroupDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "getSubsTGList(KnIPTalkGroupDTO, KnPersisterTxn)", "ENTRY");
        KnXDMTalkGroupServerRespDTO respDto = getCorpGroupManager().getSubsTGList(ipTalkGroupDTO, persisterTxn);
        knLogger.debug( "getSubsTGList(KnIPTalkGroupDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    @Override
    public KnCorpResponseDTO deleteTGList(KnIPTalkGroupDTO ipTalkGroupDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "deleteTGList(KnIPTalkGroupDTO, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getCorpGroupManager().deleteTGList(ipTalkGroupDTO, persisterTxn);
        knLogger.debug( "deleteTGList(KnIPTalkGroupDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    @Override
    public KnCorpResponseDTO getSubsEmergencyDetails(KnIPEmergencyInfoDTO ipEmergencyInfoDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "getSubsEmergencyDetails(KnIPAuthUserPermissionInfoDTO, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getCorpSubscrProfileManager().getSubsEmergencyDetails(ipEmergencyInfoDTO, persisterTxn);
        knLogger.debug( "getSubsEmergencyDetails(KnIPAuthUserPermissionInfoDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    @Override
    public KnCorpResponseDTO addBulkGroupsToSubscriber(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn){
        knLogger.debug( "addBulkGroupsToSubscriber(KnIPSubscriberInfoDTO, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getCorpSubscrProfileManager().addBulkGroupsToSubscriber(ipSubscriberInfoDTO, persisterTxn);
        knLogger.debug( "addBulkGroupsToSubscriber(KnIPSubscriberInfoDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    @Override
    public KnBulkGroupCloningDTO cloneBulkGroupsToSubscriberDataPrepration(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "cloneBulkGroupsToSubscriberDataPrepration(KnIPSubscriberInfoDTO, KnPersisterTxn)", "ENTRY");
        KnBulkGroupCloningDTO respDto = getCorpSubscrProfileManager().cloneBulkGroupsToSubscriberDataPrepration(ipSubscriberInfoDTO, persisterTxn);
        knLogger.debug( "cloneBulkGroupsToSubscriberDataPrepration(KnIPSubscriberInfoDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    @Override
    public KnCorpResponseDTO cloneBulkGroupsToSubscriberProcessing(KnBulkGroupCloningDTO bulkGroupCloningDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "cloneBulkGroupsToSubscriberDataPrepration(KnIPSubscriberInfoDTO, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getCorpSubscrProfileManager().cloneBulkGroupsToSubscriberProcessing(bulkGroupCloningDTO, persisterTxn);
        knLogger.debug( "cloneBulkGroupsToSubscriberDataPrepration(KnIPSubscriberInfoDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    @Override
    public KnCorpResponseDTO createSubsATGScanList(KnIPTalkGroupDTO ipTalkGroupDTO, KnPersisterTxn persisterTxn){
        knLogger.debug( "createSubsATGScanList(KnIPSubscriberInfoDTO, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getCorpSubscrProfileManager().createSubsATGScanList(ipTalkGroupDTO, persisterTxn);
        knLogger.debug( "createSubsATGScanList(KnIPSubscriberInfoDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    @Override
    public KnCorpBulkGroupJobRespDTO contactPairingForBulkGroupProcess(KnIPCorpDispatchGrpMemInfoDto groupInfoDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "contactPairingForBulkGroupProcess(KnIPCorpDispatchGrpMemInfoDto, KnPersisterTxn)", "ENTRY");
        KnCorpBulkGroupJobRespDTO respDto = getCorpSubscrProfileManager().contactPairingForBulkGroupProcess(groupInfoDTO, persisterTxn);
        knLogger.debug( "contactPairingForBulkGroupProcess(KnIPCorpDispatchGrpMemInfoDto, KnPersisterTxn)", "EXIT");
        return respDto;
    }
	
	@Override
	public KnCorpGroupListRespDTO getSubsGroupList(KnIPCorpContactDTO subscriberCorpInfo, KnPersisterTxn persisterTxn) {
		knLogger.debug( "getSubsGroupList(KnIPCorpContactDTO, KnPersisterTxn)", "ENTRY");
        KnCorpGroupListRespDTO respDto = getCorpGroupManager().getSubsGroupList(subscriberCorpInfo, persisterTxn);
        knLogger.debug( "getSubsGroupList(KnIPCorpContactDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    @Override
    public KnCorpResponseDTO createOSMList(KnIPOsmDTO ipOsmDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "createOSMList(KnIPOsmDTO, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getOsmManager().createOSMList(ipOsmDTO, persisterTxn);
        knLogger.debug( "createOSMList(KnIPOsmDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    @Override
    public KnCorpResponseDTO updateOSMList(KnIPOsmDTO ipOsmDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "updateOSMList(KnIPOsmDTO, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getOsmManager().updateOSMList(ipOsmDTO, persisterTxn);
        knLogger.debug( "updateOSMList(KnIPOsmDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    @Override
    public KnCorpResponseDTO deleteOSMList(KnIPOsmDTO ipOsmDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "deleteOSMList(KnIPOsmDTO, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getOsmManager().deleteOSMList(ipOsmDTO, persisterTxn);
        knLogger.debug( "deleteOSMList(KnIPOsmDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    @Override
    public KnCorpOSMInfoListRespDTO getOSMList(KnIPOsmDTO ipOsmDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "getOSMList(KnIPOsmDTO, KnPersisterTxn)", "ENTRY");
        KnCorpOSMInfoListRespDTO respDto = getOsmManager().getOSMList(ipOsmDTO, persisterTxn);
        knLogger.debug( "getOSMList(KnIPOsmDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    @Override
    public KnCorpOSMInfoListDetailsRespDTO getOSMListDetails(KnIPOsmDTO ipOsmDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "getOSMListDetails(KnIPOsmDTO, KnPersisterTxn)", "ENTRY");
        KnCorpOSMInfoListDetailsRespDTO respDto = getOsmManager().getOSMListDetails(ipOsmDTO, persisterTxn);
        knLogger.debug( "getOSMListDetails(KnIPOsmDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    @Override
    public KnCorpResponseDTO assignOSMIdToGroup(KnIPOsmDTO ipOsmDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "assignOSMIdToGroup(KnIPOsmDTO, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getOsmManager().assignOSMIdToGroup(ipOsmDTO, persisterTxn);
        knLogger.debug( "assignOSMIdToGroup(KnIPOsmDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    @Override
    public KnCorpOSMGroupListRespDTO getOSMGroupList(KnIPOsmDTO ipOsmDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "getOSMGroupList(KnIPOsmDTO, KnPersisterTxn)", "ENTRY");
        KnCorpOSMGroupListRespDTO respDto = getOsmManager().getOSMGroupList(ipOsmDTO, persisterTxn);
        knLogger.debug( "getOSMGroupList(KnIPOsmDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    public KnCorpAuthInfoRespDTO getCorpProfileByEntities(KnIPCorpAuthInfoDTO authInfoDTO, KnPersisterTxn persisterTxn) {
        String methodName="getCorpProfileByEntities()";
        knLogger.debug( methodName, "ENTRY");
        KnCorpAuthInfoRespDTO respDto = getCorpGenericManager().getCorpProfileByEntities(authInfoDTO, persisterTxn);
        knLogger.debug( methodName, "EXIT");
        return respDto;
    }

    @Override
    public KnCorpPoCSvcConfigRespDTO getPoCConfig(KnIPCorpInfoDTO corpInfoDTO, KnPersisterTxn persisterTxn){
        knLogger.debug( "getPoCConfig(KnIPCorpInfoDTO, KnPersisterTxn)", "ENTRY");
        KnCorpPoCSvcConfigRespDTO respDto = getCorpGenericManager().getPoCConfig(corpInfoDTO, persisterTxn);
        knLogger.debug( "getPoCConfig(KnIPCorpInfoDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    @Override
    public KnCorpGroupListRespDTO getMobileSyncLocSupervisors(KnIPSubscriberInfoDTO subsCorpInfo, KnPersisterTxn persisterTxn) {
        knLogger.debug( "getMobileSyncLocSupervisors(KnIPSubscriberInfoDTO, KnPersisterTxn)", "ENTRY");
        KnCorpGroupListRespDTO respDto = getCorpGroupManager().getMobileSyncLocSupervisors(subsCorpInfo, persisterTxn);
        knLogger.debug( "getMobileSyncLocSupervisors(KnIPSubscriberInfoDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    @Override
    public KnCorpResponseDTO createUserProfile(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "createUserProfile(KnIPUserProfileDTO, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getUserProfileManager().createUserProfile(ipUserProfileDTO, persisterTxn);
        knLogger.debug( "createUserProfile(KnIPUserProfileDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    @Override
    public KnCorpResponseDTO updateUserProfile(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "updateUserProfile(KnIPUserProfileDTO, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getUserProfileManager().updateUserProfile(ipUserProfileDTO, persisterTxn);
        knLogger.debug( "updateUserProfile(KnIPUserProfileDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    @Override
    public KnCorpResponseDTO deleteUserProfile(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "deleteUserProfile(KnIPUserProfileDTO, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getUserProfileManager().deleteUserProfile(ipUserProfileDTO, persisterTxn);
        knLogger.debug( "deleteUserProfile(KnIPUserProfileDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    @Override
    public KnCorpResponseDTO getUserProfileDetails(KnIPUserProfileDTO ipUserProfileDTO, boolean readOnly, KnPersisterTxn persisterTxn) {
        knLogger.debug( "getUserProfileDetails(KnIPOsmDTO, boolean, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getUserProfileManager().getUserProfileDetails(ipUserProfileDTO, readOnly, persisterTxn);
        knLogger.debug( "getUserProfileDetails(KnIPOsmDTO, boolean, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    @Override
    public KnCorpUserProfileListRespDTO getUserProfileList(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "getUserProfileList(KnIPOsmDTO, KnPersisterTxn)", "ENTRY");
        KnCorpUserProfileListRespDTO respDto = getUserProfileManager().getUserProfileList(ipUserProfileDTO, persisterTxn);
        knLogger.debug( "getUserProfileList(KnIPOsmDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    @Override
    public KnCorpUserProfileListRespDTO getUserProfileListByName(KnIPUserProfileDTO ipUserProfileDTO,
                                                                 KnPersisterTxn persisterTxn) {
        knLogger.debug( "getUserProfileListByName(KnIPOsmDTO, KnPersisterTxn)", "ENTRY");
        KnCorpUserProfileListRespDTO respDto = getUserProfileManager().getUserProfileListByName(ipUserProfileDTO, persisterTxn);
        knLogger.debug( "getUserProfileListByName(KnIPOsmDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    @Override
    public KnCorpUserProfileListRespDTO getSubscriberUserProfileList(KnIPUserProfileDTO ipUserProfileDTO,
                                                                     KnPersisterTxn persisterTxn) {
        knLogger.debug( "getSubscriberUserProfileList(KnIPOsmDTO, KnPersisterTxn)", "ENTRY");
        KnCorpUserProfileListRespDTO respDto = getUserProfileManager().getSubscriberUserProfileList(ipUserProfileDTO, persisterTxn);
        knLogger.debug( "getSubscriberUserProfileList(KnIPOsmDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    @Override
    public KnCorpResponseDTO assignUserProfile(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn) {
        KnCorpResponseDTO respDto = getUserProfileManager().assignUserProfile(ipUserProfileDTO, persisterTxn);
        return respDto;
    }

    @Override
    public Map<Integer,Integer> getUpIndexCountMap(Collection<Integer> userprofileIndexes, String corpId, KnPersisterTxn persisterTxn){
        knLogger.debug( "getUpIndexCountMap(userprofileIndexes,corpId, KnPersisterTxn)", "ENTRY");
        Map<Integer,Integer> getUpIndexCountMap = getCorpGroupManager().getUpIndexCountMap(userprofileIndexes,corpId, persisterTxn);
        knLogger.debug( "getUpIndexCountMap(userprofileIndexes, KnPersisterTxn)", "EXIT");
        return getUpIndexCountMap;
    }

    @Override
    public KnCorpUserProfileListRespDTO getUserProfileSubscriberList(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "getUserProfileSubscriberList(KnIPUserProfileDTO, KnPersisterTxn)", "ENTRY");
        KnCorpUserProfileListRespDTO respDto = getUserProfileManager().getUserProfileSubscriberList(ipUserProfileDTO, persisterTxn);
        knLogger.debug( "getUserProfileSubscriberList(KnIPUserProfileDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    @Override
    public KnCorpResponseDTO updateDefaultProfile(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "updateDefaultProfile(KnIPUserProfileDTO, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getUserProfileManager().updateDefaultProfile(ipUserProfileDTO, persisterTxn);
        knLogger.debug( "updateDefaultProfile(KnIPUserProfileDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    @Override
    public KnCorpResponseDTO getProfileMdnByUPId(KnIPUserProfileDTO ipUserProfileDTO, boolean readOnly, KnPersisterTxn persisterTxn) {
        knLogger.debug( "getProfileMdnByUPId(KnIPUserProfileDTO, boolean, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getUserProfileManager().getProfileMdnByUPId(ipUserProfileDTO, readOnly, persisterTxn);
        knLogger.debug( "getProfileMdnByUPId(KnIPUserProfileDTO, boolean, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    @Override
    public KnCorpResponseDTO modifyCBUserProfile(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "modifyCBUserProfile(KnIPUserProfileDTO, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getUserProfileManager().modifyCBUserProfile(ipUserProfileDTO, persisterTxn);
        knLogger.debug( "modifyCBUserProfile(KnIPUserProfileDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    @Override
    public KnCorpResponseDTO getProfileMdnEtag(List<String> userProfileIds, String corpId, KnPersisterTxn persisterTxn) {
        knLogger.debug( "getProfileMdnEtag(List<String>, String, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getUserProfileManager().getProfileMdnEtag(userProfileIds, corpId, persisterTxn);
        knLogger.debug( "getProfileMdnEtag(List<String>, String, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    @Override
    public KnCorpResponseDTO unassignUserProfile(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn) {
        KnCorpResponseDTO respDto = getUserProfileManager().unassignUserProfile(ipUserProfileDTO, persisterTxn);
        return respDto;
    }

    @Override
    public KnCorpResponseDTO getSubsGroupMemberShipDetails(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn) {
        KnCorpResponseDTO respDto = getCorpSubscrProfileManager().getSubsGroupMemberShipDetails(ipSubscriberInfoDTO, persisterTxn);
        return respDto;
    }

	@Override
	public KnCorpResponseDTO modifyMCXGroup(KnIPCorpGroupInfoDTO groupInfoDTO, KnCorpGrpBasicInfoDTO grpBasicInfo,
			KnPersisterTxn persisterTxn) {
		 knLogger.debug( "modifyMCXGroup(KnIPCorpGroupInfoDTO, KnPersisterTxn)", "ENTRY");
	        KnCorpResponseDTO respDto = getCorpGroupManager().modifyMCXGroup(groupInfoDTO, grpBasicInfo, persisterTxn);
	        knLogger.debug( "modifyMCXGroup(KnIPCorpGroupInfoDTO, KnPersisterTxn)", "EXIT");
	        return respDto;
	}


    @Override
    public KnCorpUserPermissionRespDTO getAllAuthorizedMdnList(KnIPAuthUserPermissionInfoDTO ipAuthUserPermissionInfoDTO, KnPersisterTxn persisterTxn, boolean upmFlag) {
        KnCorpUserPermissionRespDTO respDto = getCorpSubscrProfileManager().getAllAuthorizedMdnList(ipAuthUserPermissionInfoDTO, persisterTxn, upmFlag);
        return respDto;
    }

    @Override
    public KnCorpResponseDTO updateImpactedTuPerms(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn) {
        KnCorpResponseDTO respDto = getUserProfileManager().updateImpactedTuPerms(ipUserProfileDTO, persisterTxn);
        return respDto;
    }

    @Override
    public KnCorpGrpListInfoRespDto getListOfGroupInfo(KnIPCorpGroupInfoDTO groupInfoDTO, KnPersisterTxn persisterTxn) {
        return getCorpGroupManager().getListOfGroupInfo(groupInfoDTO, persisterTxn);
    }

    @Override
    public KnCorpGroupInfoRespDTO createBulkCorpGroup(KnIPCorpBulkGroupDTO bulkGroupIPDto, KnPersisterTxn persisterTxn) {
        knLogger.debug( "createBulkCorpGroup()", "ENTRY");
        KnCorpGroupInfoRespDTO respDto = getCorpGroupManager().createBulkCorpGroup(bulkGroupIPDto, persisterTxn);
        knLogger.debug( "createBulkCorpGroup()", "EXIT");
        return respDto;
    }

    @Override
    public KnCorpGroupListRespDTO getProfileGroupList(KnIPCorpGroupProfileDTO groupProfileDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "getProfileGroupList(KnIPCorpGroupProfileDTO, KnPersisterTxn)", "ENTRY");
        KnCorpGroupListRespDTO respDto = getCorpGroupManager().getProfileGroupList(groupProfileDTO, persisterTxn);
        knLogger.debug( "getProfileGroupList(KnIPCorpGroupProfileDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    @Override
    public KnCorpResponseDTO createGroupProfile(KnIPCorpGorupProfileDTO groupProfileIPDto, KnPersisterTxn persisterTxn) {
        knLogger.debug( "createGroupProfile()", "ENTRY");
        KnCorpResponseDTO respDto = getGroupProfileManager().createGroupProfile(groupProfileIPDto, persisterTxn);
        knLogger.debug( "createGroupProfile()", "EXIT");
        return respDto;
    }

    @Override
    public KnCorpGroupProfileResponseDTO getGroupProfileList(KnIPCorpGorupProfileDTO groupProfileIPDto,
                                                             KnPersisterTxn persisterTxn) {
        knLogger.debug( "getGroupProfileList()", "ENTRY");
        KnCorpGroupProfileResponseDTO respDto = getGroupProfileManager().getGroupProfileList(groupProfileIPDto, persisterTxn);
        knLogger.debug( "getGroupProfileList()", "EXIT");
        return respDto;
    }

    @Override
    public KnCorpGroupProfileResponseDTO getGroupProfileDetails(KnIPCorpGorupProfileDTO groupProfileIPDto,
                                                                KnPersisterTxn persisterTxn) {
        knLogger.debug( "getGroupProfileDetails()", "ENTRY");
        KnCorpGroupProfileResponseDTO respDto = getGroupProfileManager().getGroupProfileDetails(groupProfileIPDto, persisterTxn);
        knLogger.debug( "getGroupProfileDetails()", "EXIT");
        return respDto;
    }


    @Override
    public KnCorpGroupProfileResponseDTO searchGroupProfile(KnIPCorpGorupProfileDTO groupProfileIPDto, KnPersisterTxn persisterTxn) {
        knLogger.debug( "searchGroupProfile()", "ENTRY");
        KnCorpGroupProfileResponseDTO respDto = getGroupProfileManager().searchGroupProfile(groupProfileIPDto, persisterTxn);
        knLogger.debug( "searchGroupProfile()", "EXIT");
        return respDto;
    }

    @Override
    public KnCorpResponseDTO modifyGroupProfile(KnIPCorpGorupProfileDTO groupProfileIPDto, KnPersisterTxn persisterTxn) {
        knLogger.debug( "modifyGroupProfile()", "ENTRY");
        KnCorpResponseDTO respDto = getGroupProfileManager().modifyGroupProfile(groupProfileIPDto, persisterTxn);
        knLogger.debug( "modifyGroupProfile()", "EXIT");
        return respDto;
    }

    @Override
    public KnCorpResponseDTO deleteGrpProfileGroupList(KnIPDeleteBulkCorpGrpDTO groupProfileIPDto, KnPersisterTxn persisterTxn) {
        knLogger.debug( "deleteBulkCorpGroup()", "ENTRY");
        KnCorpResponseDTO respDto = getGroupProfileManager().deleteGrpProfileGroupList(groupProfileIPDto, persisterTxn);
        knLogger.debug( "deleteBulkCorpGroup()", "EXIT");
        return respDto;
    }

    @Override
    public KnCorpResponseDTO deleteGroupProfile(KnIPCorpGorupProfileDTO groupProfileIPDto, KnPersisterTxn persisterTxn) {
        knLogger.debug( "deleteGroupProfile()", "ENTRY");
        KnCorpResponseDTO respDto = getGroupProfileManager().deleteGroupProfile(groupProfileIPDto, persisterTxn);
        knLogger.debug( "deleteGroupProfile()", "EXIT");
        return respDto;
    }
    @Override
    public KnCorpResponseDTO userProfileNotfication(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "createUserProfile(KnIPUserProfileDTO, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getUserProfileManager().userProfileNotfication(ipUserProfileDTO, persisterTxn);
        knLogger.debug( "createUserProfile(KnIPUserProfileDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    @Override
    public KnCorpResponseDTO userProfileNotficationForUpm(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn) {
        KnCorpResponseDTO respDto = getUserProfileManager().userProfileNotficationForUpm(ipUserProfileDTO, persisterTxn);
        return respDto;
    }

    @Override
    public KnCorpSharedList getSharedCorpTrustMatrix(KnIPCorpAuthInfoDTO requestDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "getSharedCorpTrustMatrix(KnIPCorpAuthInfoDTO, KnPersisterTxn)", "ENTRY");
        KnCorpSharedList respDto = getCorpGenericManager().getSharedCorpTrustMatrix(requestDTO, persisterTxn);
        knLogger.debug( "getSharedCorpTrustMatrix(KnIPCorpAuthInfoDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    public KnCorpResponseDTO updateCorpTrustMatrix(KnIPCorpAuthInfoDTO requestDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug("updateCorpTrustMatrix(KnIPCorpAuthInfoDTO, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getCorpGenericManager().updateCorpTrustMatrix(requestDTO, persisterTxn);
        knLogger.debug("updateCorpTrustMatrix(KnIPCorpAuthInfoDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    public KnCorpResponseDTO deleteCorpTrustMatrix(KnIPCorpAuthInfoDTO requestDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug("deleteCorpTrustMatrix(KnIPCorpAuthInfoDTO, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getCorpGenericManager().deleteCorpTrustMatrix(requestDTO, persisterTxn);
        knLogger.debug("deleteCorpTrustMatrix(KnIPCorpAuthInfoDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    @Override
    public KnCorpResponseDTO sendTrkMaterial(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "sendTrkMaterial(subsInfoDTO, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getCorpSubscrProfileManager().sendTrkMaterial(ipSubscriberInfoDTO, persisterTxn);
        knLogger.debug( "sendTrkMaterial(KnIPCorpAuthInfoDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    @Override
    public KnCorpResponseDTO deleteMcpttPermConfig(KnIPUserProfileDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn) {
        KnCorpResponseDTO respDto = getUserProfileManager().deleteMcpttPermConfig(ipSubscriberInfoDTO, persisterTxn);
        return respDto;
    }

    @Override
    public KnCorpResponseDTO getAsyncOpStatus(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "getAsyncOpStatus(KnIPUserProfileDTO, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getUserProfileManager().getAsyncOpStatus(ipUserProfileDTO, persisterTxn);
        knLogger.debug( "getAsyncOpStatus(KnIPUserProfileDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    public KnCorpResponseDTO groupNotifyOnActiveFsChange(int corpId,String mdn,String newActiveFS, String oldActiveFS, KnPersisterTxn persisterTxn) {
        return getCorpGroupManager().groupNotifyOnActiveFsChange(corpId,mdn,newActiveFS,oldActiveFS, persisterTxn);
    }
    @Override
    public KnCorpGroupInfoRespDTO getCorpGrpLmrExtn(KnIPCorpGroupInfoDTO groupInfoDTO, KnPersisterTxn persisterTxn) {
        KnCorpGroupInfoRespDTO respDto = getCorpGroupManager().getCorpGrpLmrExtn(groupInfoDTO, persisterTxn);
        return respDto;
    }

    @Override
    public KnCorpResponseDTO modifyGroupsUGWConfig(KnIPCorpGroupInfoDTO groupInfoDTO, KnPersisterTxn persisterTxn) {
        KnCorpResponseDTO respDto = getCorpGroupManager().modifyGroupsUGWConfig(groupInfoDTO, persisterTxn);
        return respDto;
    }

    @Override
    public KnCorpGroupInfoRespDTO getMdnAuthorizationForGroupId(KnIPCorpContactDTO groupInfoDTO, KnPersisterTxn persisterTxn) {
        KnCorpGroupInfoRespDTO respDto = getCorpGroupManager().getMdnAuthorizationForGroupId(groupInfoDTO, persisterTxn);
        return respDto;
    }
    @Override
    public KnCorpResponseDTO assignCommonContactList(KnIPCorpSublistSubscDistDTO subscDistDTO, KnPersisterTxn persisterTxn) {
        KnCorpResponseDTO respDto = getCorpSublistManager().assignCommonContactList(subscDistDTO, persisterTxn);
        return respDto;
    }

    @Override
    public KnCorpResponseDTO unAssignCommonContactList(KnIPCorpSublistSubscDistDTO subscDistDTO, KnPersisterTxn persisterTxn) {
        KnCorpResponseDTO respDto = getCorpSublistManager().unAssignCommonContactList(subscDistDTO, persisterTxn);
        return respDto;
    }

    @Override
    public KnCorpResponseDTO unAssignCommonContactListForCloningContact(KnIPCorpSublistSubscDistDTO subscDistDTO, KnPersisterTxn persisterTxn) {
        KnCorpResponseDTO respDto = getCorpSublistManager().unAssignCommonContactListForCloningContact(subscDistDTO, persisterTxn);
        return respDto;
    }

    @Override
    public List<KnUserProfileAssignedDTO> getUserProfileSubsCount(Collection<String> userprofileIds, String corpId, boolean readOnly, KnPersisterTxn persisterTxn){
        String methodname = "getUserProfeilSubsCount(Collection<String> ,String, boolean, KnPersisterTxn)";
        knLogger.debug( methodname, "ENTRY");
        List<KnUserProfileAssignedDTO> userProfileAssignedDTOMap = getUserProfileManager().getUserProfileSubsCount(userprofileIds, corpId, readOnly, persisterTxn);
        knLogger.debug( methodname, "EXIT");
        return userProfileAssignedDTOMap;
    }

    @Override
    public KnCorpDeviceListRespDTO getDeviceList(KnIPCorpInfoDTO corpInfoDTO, KnPersisterTxn persisterTxn) {
        KnCorpDeviceListRespDTO respDto = getDeviceManager().getDeviceList(corpInfoDTO, persisterTxn);
        return respDto;
    }

    @Override
    public KnCorpDeviceInfoRespDTO getDeviceDetails(KnIPDeviceInfoDTO subscDistDTO, KnPersisterTxn persisterTxn) throws KnDAOException, KnCorpBOException {
        KnCorpDeviceInfoRespDTO respDto = getDeviceManager().getDeviceDetails(subscDistDTO, persisterTxn);
        return respDto;
    }

    @Override
    public KnGroupStatsRespDTO getGroupStats(KnIPCorpInfoDTO corpInfoDTO, KnPersisterTxn persisterTxn) {
        KnGroupStatsRespDTO respDto = getStatsManager().getGroupStats(corpInfoDTO, persisterTxn);
        return respDto;
    }

    @Override
    public KnCorpSubsStatsRespDTO getSubscriberStats(KnIPCorpInfoDTO subscDistDTO, KnPersisterTxn persisterTxn) {
        KnCorpSubsStatsRespDTO respDto = getStatsManager().getSubscriberStats(subscDistDTO, persisterTxn);
        return respDto;
    }

    @Override
    public KnCorpDeviceStatsRespDTO getDeviceStats(String corpId, KnPersisterTxn persisterTxn) {
        KnCorpDeviceStatsRespDTO respDto = getStatsManager().getDeviceStats(corpId, persisterTxn);
        return respDto;
    }

    public KnCorpResponseDTO updateCorporateFS(KnIPCorpInfoDTO authInfoDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "updateCorporateFS(KnIPCorpAuthInfoDTO, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getCorpGenericManager().updateCorporateFS(authInfoDTO, persisterTxn);
        knLogger.debug( "updateCorporateFS(KnIPCorpAuthInfoDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    public KnCorpResponseDTO setCATAccessPermission(KnIPCatPermissionSetDTO authInfoDTO, KnPersisterTxn persisterTxn) {
        KnCorpResponseDTO respDto = getCorpGenericManager().setCATAccessPermission(authInfoDTO, persisterTxn);
        return respDto;
    }

    @Override
    public KnCorpResponseDTO modifyBulkGroupProperties(KnIPCorpGroupInfoDTO groupInfoDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug("modifyBulkGroupProperties(KnIPCorpGroupDTO, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getCorpGroupManager().modifyBulkGroupProperties(groupInfoDTO, persisterTxn);
        knLogger.debug("modifyBulkGroupProperties(KnIPCorpGroupDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }
    @Override
    public KnCorpResponseDTO deleteCorporateData(KnXDMCorpInfoDTO contactDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug("deleteCorporateData(KnIPCorpAuthInfoDTO, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getCorpGenericManager().deleteCorporateData(contactDTO, persisterTxn);
        knLogger.debug("deleteCorporateData(KnIPCorpAuthInfoDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    @Override
    public KnCorpResponseDTO updateSelfEtag(KnCorpResponseDTO userProfileDetails, KnPersisterTxn persisterTxn) {
        return getCorpGenericManager().updateSelfEtag(userProfileDetails, persisterTxn);
    }

    @Override
    public KnCorpResponseDTO updateEtag(int corpId, String profileMdn, KnCorpResponseDTO userProfileDetails, KnPersisterTxn persisterTxn, Map<String, KnOPDirChgDTO> etagMap) {
        return getCorpGenericManager().updateEtag(corpId,profileMdn,userProfileDetails,persisterTxn,etagMap);
    }

    @Override
    public KnCorpResponseDTO deleteHierarchy(String corpId, List<String> removedHierarchy, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        return getCorpGenericManager().deleteHierarchy(corpId, removedHierarchy, persisterTxn);
    }

    @Override
    public void updateSelfEtagForClone(KnCorpResponseDTO assignGroupResp, String corpIdString, String mdn, KnPersisterTxn persisterTxn) {
        getCorpGenericManager().updateSelfEtagForClone(assignGroupResp, corpIdString, mdn, persisterTxn);
    }

    @Override
    public Map<String, Collection<KnDocChangeListDTO>> updateSubsTS(Set<String> mdnSet, String corpId, KnPersisterTxn persisterTxn) {
        return getCorpGenericManager().updateSubsTS(mdnSet, corpId, persisterTxn);
    }

    @Override
    public KnCorpGroupListRespDTO getGroupsDetailsWithoutMembers(KnIPCorpBulkGroupDTO bulkGroupDTO, KnPersisterTxn persisterTxn) {
        return getCorpGroupManager().getGroupsDetailsWithoutMembers(bulkGroupDTO, persisterTxn);
    }

    @Override
    public KnCorpResponseDTO modifyBulkCorpSubscContacts(KnIPCorpSubscContactListDTO contactListDTO, KnPersisterTxn persisterTxn) {
        KnCorpResponseDTO respDto = getCorpContactManager().modifyBulkCorpSubscContacts(contactListDTO, persisterTxn);
        return respDto;
    }

    @Override
    public KnCorpResponseDTO setBulkTargetPermissions(KnIPAuthUserPermissionInfoDTO ipAuthUserPermissionInfoDTO, KnPersisterTxn persisterTxn) {
        KnCorpResponseDTO respDto = getCorpSubscrProfileManager().setBulkTargetPermissions(ipAuthUserPermissionInfoDTO, persisterTxn);
        return respDto;
    }

    @Override
    public KnCorpResponseDTO setUserProfileEmergencyAttributes(KnIPEmergencyInfoDTO ipEmergencyInfoDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug("setUserProfileEmergencyAttributes()", "ENTRY: ");
        KnCorpResponseDTO respDto = getCorpSubscrProfileManager().setUserProfileEmergencyAttributes(ipEmergencyInfoDTO, persisterTxn);
        knLogger.debug("setUserProfileEmergencyAttributes()", "EXIT: ");
        return respDto;
    }

    @Override
    public KnCorpBulkGrpBasicInfoRespDto getBulkBasicGrpInfo(KnIPCorpBulkGroupDTO bulkGroupInfo, int clientType, String userProfileId, KnPersisterTxn persisterTxn) {
        String methodName = "getBulkBasicGrpInfo(KnIPCorpBulkGroupDTO ,int,  String, KnPersisterTxn)";
        knLogger.debug( methodName, "ENTRY");
        var respDto = getCorpGroupManager().getBulkBasicGrpInfo(bulkGroupInfo, clientType, userProfileId, persisterTxn);
        knLogger.debug( methodName, "EXIT");
        return respDto;
    }
    @Override
    public KnCorpResponseDTO modifyBulkMCXGroup(KnIPCorpBulkGroupDTO bulkGroupInfo, Map<Integer, KnCorpGrpBasicInfoDTO> vLargerGrpBasicInfoMap,
                                                KnPersisterTxn persisterTxn) {
        String methodName = "modifyBulkMCXGroup(KnIPCorpBulkGroupDTO,Map<Integer, KnCorpGrpBasicInfoDTO>,  KnPersisterTxn)" ;
        knLogger.debug( methodName, "ENTRY");
        KnCorpResponseDTO respDto = getCorpGroupManager().modifyBulkMCXGroup(bulkGroupInfo, vLargerGrpBasicInfoMap, persisterTxn);
        //  KnCorpResponseDTO respDto = getCorpGroupManager().modifyMCXGroup(groupInfoDTO, grpBasicInfo, persisterTxn);
        knLogger.debug( methodName, "EXIT");
        return respDto;
    }
    @Override
    public KnCorpResponseDTO upmBulkModifyBCGroup(KnIPCorpBulkGroupDTO bulkGroupInfo, Map<Integer, KnCorpGrpBasicInfoDTO> broadCastGrpBasicInfoMap, KnPersisterTxn persisterTxn) {
        String methodName = "upmBulkModifyBCGroup(KnIPCorpBulkGroupDTO, Map<Integer, KnCorpGrpBasicInfoDTO> , KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY");
        KnCorpResponseDTO respDto = getCorpGroupManager().upmBulkModifyBCGroup(bulkGroupInfo, broadCastGrpBasicInfoMap, persisterTxn);
        // KnCorpResponseDTO respDto = getCorpGroupManager().upmModifyBCGroup(groupInfoDTO, grpBasicInfo, persisterTxn);
        knLogger.debug( methodName, "EXIT");
        return respDto;
    }

    @Override
    public KnCorpResponseDTO upmBulkModifyGroup(KnIPCorpBulkGroupDTO bulkGroupInfo, Map<Integer, KnCorpGrpBasicInfoDTO> grpBasicInfoMap, KnPersisterTxn persisterTxn) {
        String methodName = "upmBulkModifyGroup(KnIPCorpBulkGroupDTO, Map<Integer, KnCorpGrpBasicInfoDTO>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY");
        KnCorpResponseDTO respDto = getCorpGroupManager().upmBulkModifyGroup(bulkGroupInfo, grpBasicInfoMap, persisterTxn);
        // KnCorpResponseDTO respDto = getCorpGroupManager().upmModifyGroup(groupInfoDTO, grpBasicInfo, persisterTxn);
        knLogger.debug( methodName, "EXIT");
        return respDto;
    }

    @Override
    public KnCorpResponseDTO cloneValidation(KnIPCorpSubscCloningListDTO contactListDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug( "cloneContactsValidation(KnIPCorpSubscContactListDTO, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getCorpCloningManager().cloneValidation(contactListDTO, persisterTxn);
        knLogger.debug( "cloneContactsValidation(KnIPCorpSubscContactListDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    @Override
    public int getCurrentEtag(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException, KnBOException {
        knLogger.debug( "cloneContactsValidation(KnIPCorpSubscContactListDTO, KnPersisterTxn)", "ENTRY");
        return getCorpCloningManager().getCurrentEtag(mdn, persisterTxn);
    }

    @Override
    public KnCorpResponseDTO groupWatcherNotifyForAddGroup(KnAsyncJobDTO asyncJobDTO) throws KnXDMServerException {
        KnCorpResponseDTO respDto = getCorpGroupManager().groupWatcherNotifyForAddGroup(asyncJobDTO);
        return respDto;
    }

    @Override
    public KnCorpResponseDTO groupWatcherNotifyForModifyGroup(KnAsyncJobDTO asyncJobDTO) throws KnXDMServerException {
        KnCorpResponseDTO respDto = getCorpGroupManager().groupWatcherNotifyForModifyGroup(asyncJobDTO);
        return respDto;
    }

    @Override
    public KnCorpResponseDTO groupWatcherNotifyforRemoveGroup(KnAsyncJobDTO asyncJobDTO) throws KnXDMServerException {
        KnCorpResponseDTO respDto = getCorpGroupManager().groupWatcherNotifyforRemoveGroup(asyncJobDTO);
        return respDto;
    }

    @Override
    public KnCorpResponseDTO upmModifyBulkSubscriberTGList(KnIPTalkGroupDTO ipTalkGroupDTO, KnPersisterTxn persisterTxn) {
        KnCorpResponseDTO respDto = getCorpGroupManager().modifyBulkSubscriberTGList(ipTalkGroupDTO, persisterTxn);
        return respDto;
    }

    @Override
    public Map<Integer, KnCorpGroupDTO> getGroupDetailsMap(List<Integer> groups, String xdmsHome, KnPersisterTxn persisterTxn) {
        return getCorpGroupManager().getGroupDetailsMap(groups, xdmsHome, persisterTxn);
    }

    @Override
    public KnCorpResponseDTO getLiEvents(KnIPCorpSubscContactListDTO contactListDTO, List<String> mdns, Integer dbSublistId, KnPersisterTxn persisterTxn) {
        KnCorpResponseDTO respDto = getCorpContactManager().getLiEvents(contactListDTO, mdns, dbSublistId, persisterTxn);
        return respDto;
    }

    @Override
    public KnCorpResponseDTO createPTTSettingDoc(KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO,KnPersisterTxn persisterTxn) {
        KnCorpResponseDTO respDto = getPttSettingManager().createPTTSettingDoc(ipCorpPTTSettingDTO, persisterTxn);
        return respDto;
    }
    @Override
    public KnCorpResponseDTO getPTTSettingDocList(KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO,KnPersisterTxn persisterTxn) {
        KnCorpResponseDTO respDto = getPttSettingManager().getPTTSettingDocList(ipCorpPTTSettingDTO, persisterTxn);
        return respDto;
    }
    @Override
    public KnCorpPTTSettingDTO getPTTSettingDoc(KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO, KnPersisterTxn persisterTxn) {
        KnCorpPTTSettingDTO respDto = getPttSettingManager().getPTTSettingDoc(ipCorpPTTSettingDTO, persisterTxn);
        return respDto;
    }
    @Override
    public KnCorpResponseDTO setDefaultPttSettingDoc(KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO,KnPersisterTxn persisterTxn) {
        KnCorpResponseDTO respDto = getPttSettingManager().setDefaultPttSettingDoc(ipCorpPTTSettingDTO, persisterTxn);
        return respDto;
    }
    @Override
    public KnCorpResponseDTO deletePTTSettingDoc(KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO,KnPersisterTxn persisterTxn) {
        return  getPttSettingManager().deletePTTSettingDoc(ipCorpPTTSettingDTO, persisterTxn);

    }
    @Override
    public KnCorpResponseDTO assignPttSettingToHierarchy(KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO,KnPersisterTxn persisterTxn) {
        return  getPttSettingManager().assignPttSettingToHierarchy(ipCorpPTTSettingDTO, persisterTxn);

    }
    @Override
    public KnCorpResponseDTO unassignPttSettingToHierarchy(KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO,KnPersisterTxn persisterTxn) {
        return  getPttSettingManager().unassignPttSettingToHierarchy(ipCorpPTTSettingDTO, persisterTxn);

    }

    @Override
    public KnCorpResponseDTO assignPttSettingDocToMdns(KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO, KnPersisterTxn persisterTxn) {
        return getPttSettingManager().assignPttSettingDocToMdns(ipCorpPTTSettingDTO, persisterTxn);
    }

    @Override
    public KnCorpResponseDTO unassignPttSettingDocToMdns(KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO, KnPersisterTxn persisterTxn) {
        return getPttSettingManager().unassignPttSettingDocToMdns(ipCorpPTTSettingDTO, persisterTxn);
    }

    @Override
    public KnCorpResponseDTO getPttSettingDocMdnList(KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO, KnPersisterTxn persisterTxn) {
        return getPttSettingManager().getPttSettingDocMdnList(ipCorpPTTSettingDTO, persisterTxn);
    }

    @Override
    public KnCorpResponseDTO getMDNCountForPttSettingDocID(KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO, KnPersisterTxn persisterTxn) {
        return getPttSettingManager().getMDNCountForPttSettingDocID(ipCorpPTTSettingDTO, persisterTxn);
    }
    @Override
    public KnCorpResponseDTO assignPttSettingToCorp(KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO,KnPersisterTxn persisterTxn) {
        return  getPttSettingManager().assignPttSettingToCorp(ipCorpPTTSettingDTO, persisterTxn);

    }
    @Override
    public KnCorpResponseDTO unassignPttSettingToCorp(KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO,KnPersisterTxn persisterTxn) {
        return  getPttSettingManager().unassignPttSettingToCorp(ipCorpPTTSettingDTO, persisterTxn);

    }


    @Override
    public KnCorpResponseDTO createHierarchy(KnIPCorpHierarchyDTO knIPCorpHierarchyDTO) {
        KnCorpResponseDTO respDto = getHierarchyManager().createHierarchy(knIPCorpHierarchyDTO);
        return respDto;
    }

    @Override
    public KnCorpResponseDTO modifyHierarchy(KnIPCorpHierarchyDTO knIPCorpHierarchyDTO) {
        KnCorpResponseDTO respDto = getHierarchyManager().modifyHierarchy(knIPCorpHierarchyDTO);
        return respDto;
    }

    @Override
    public KnRegionsCorpRespDTO getRegions(KnIPCorpHierarchyDTO knIPCorpHierarchyDTO) {
        final String methodName = "getRegions(KnIPCorpHierarchyDTO)";
        knLogger.debug(methodName, "ENTRY with hierarchyDTO - ", knIPCorpHierarchyDTO);
        KnRegionsCorpRespDTO respDto = getHierarchyManager().getRegions(knIPCorpHierarchyDTO);
        knLogger.debug(methodName, "EXIT with response - ", respDto);
        return respDto;
    }

    @Override
    public KnCorpResponseDTO allocateSubs(KnIPAllocateSubscriberDTO allocateSubscriberDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug("allocateSubs(KnIPAllocateSubscriberDTO, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getCorpSubscrProfileManager().allocateSubs(allocateSubscriberDTO, persisterTxn);
        knLogger.debug("allocateSubs(KnIPAllocateSubscriberDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    @Override
    public KnCorpResponseDTO unAllocateSubs(KnIPUnAllocateSubscriberDTO unAllocateSubscriberDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug("unAllocateSubs(KnIPUnAllocateSubscriberDTO, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getCorpSubscrProfileManager().unAllocateSubs(unAllocateSubscriberDTO, persisterTxn);
        knLogger.debug("unAllocateSubs(KnIPUnAllocateSubscriberDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    @Override
    public KnCorpResponseDTO groupRehome(KnIPCorpGroupInfoDTO groupInfoDTO, KnPersisterTxn persisterTxn) {
        knLogger.debug("groupRehome(KnIPCorpGroupInfoDTO, KnPersisterTxn)", "ENTRY");
        KnCorpResponseDTO respDto = getCorpGroupManager().groupRehome(groupInfoDTO, persisterTxn);
        knLogger.debug("groupRehome(KnIPCorpGroupInfoDTO, KnPersisterTxn)", "EXIT");
        return respDto;
    }

    @Override
    public KnCorpResponseDTO modifyPTTSettingTemplate(KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO,KnPersisterTxn persisterTxn) {
        KnCorpResponseDTO respDto = getPttSettingManager().modifyPTTSettingTemplate(ipCorpPTTSettingDTO, persisterTxn);
        return respDto;
    }

}
