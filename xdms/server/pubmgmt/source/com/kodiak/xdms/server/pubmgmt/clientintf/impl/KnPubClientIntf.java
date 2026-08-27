/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.clientintf.impl;

import com.kodiak.xdms.server.pubmgmt.dto.common.KnMCSXCAPRespDTO;
import com.kodiak.xdms.server.pubmgmt.clientintf.*;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.*;
import com.kodiak.xdms.server.pubmgmt.dto.common.KnPubGroupInfoDTO;
import com.kodiak.xdms.server.pubmgmt.dto.common.KnPubContactInfoDTO;
import com.kodiak.xdms.server.pubmgmt.dto.impl.KnPubContactDTO;
import com.kodiak.xdms.server.pubmgmt.dto.impl.KnPubGroupDTO;
import com.kodiak.xdms.server.pubmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.common.configuration.manager.KnConfigurationsManager;
import com.kodiak.xdms.server.common.resources.KnConstants;
import com.kodiak.xdms.server.common.framework.KnFWException;
import com.kodiak.xdms.server.common.KnXDMServerException;
import com.kodiak.xdms.server.common.dto.clientdat.KnIPChangeMDNInfoDTO;
import com.kodiak.logger.KnLogger;
import com.kodiak.common.error.KnSystemError;
import com.kodiak.common.dao.KnPersisterTxn;

import java.util.Collection;
import java.util.List;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnPubClientIntf.java
 * Subsystem:  pubmgmt
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * SureshKumar G        Jan 7, 2011        7.0
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

public class KnPubClientIntf implements IPubClientIntf {
	private static final KnLogger knLogger = KnLogger.getLogger(KnPubClientIntf.class);


    private IPubContactManager contactManager;
    private IPubGroupManager groupManager;
    private IPubManager genericManager;
    private IpubAuthManger authManger;
    private IpubTGSSListManger tgssListManger;
	private IPubMCPTTManager pubMCPTTManager;
    private IPubMCDATAManager mcdataManager;
    private IPubMCVideoManager mcvideoManager;
    private IPubMCSGroupDocManager mcsGroupDocManager;
    private IPubMCSUserDirManager mcsUserDirManager;

    /**
     * initialize the Logger and configuration manager.
     */
    private static boolean isInitialized = false;
    private static Exception exception;

    //System property name that needs to be set at start-up
    //This is the file-name (full path) that contains the information
    //required for initializing the logger
    private static String LOG_INIT_FILE = "log4jInitFile";
    public static final String BG_INIT_FILE = "libraryInitFile";

    static {
        try {
            //Initialize the Logger first, as other initialization
            //information will be written to logs
            //NB: The Logger initialization is handled as a special
            //case. All other initialization is done by the Initializer
            //module within the configuration manager
//            KnLogger.init();

            //Now, initialize the configuration manager
            knLogger.debug( "static", "Initializing config manager!");
            KnConfigurationsManager configManager = KnConfigurationsManager.getInstance(KnConstants.LIBRARY_NAME_PUB_MGMT);
            knLogger.debug( "static", "Completed Initializing config manager!");

            isInitialized = configManager.isInitialized();

        } catch (Exception e) {
            knLogger.fatal( "static",
                    "Initialization failed");
            exception = e;
        }
    }//end of static block//


    public KnPubClientIntf() {
        if (!isInitialized) {
            throw new KnSystemError(KnErrorCodes.Initializer.PUB_INIT_FAILED, "Pub Mgmt Library Initalization Failed - " + exception,
                    exception);
        }
    }


    /**
     *
     * @return
     */
    public IPubContactManager getContactListManager() {//throws KnFWException {
        if (this.contactManager == null) {
            this.contactManager = new KnPubContactManager();
        }
        return this.contactManager;
    }


    /**
     *
     * @return
     */
    public IPubGroupManager getGroupManager() {//throws KnFWException {
        if (this.groupManager == null) {
            this.groupManager = new KnPubGroupManager();
        }
        return this.groupManager;
    }

    public IpubAuthManger getAuthListManager() {
        if (this.authManger == null) {
            this.authManger = new KnPubAuthListManager();
        }
        return this.authManger;
    }
    /**
     *
     * @return
     */
    public IPubManager getGenericManager() {
        if (this.genericManager == null) {
            this.genericManager = new KnPubManager();
        }
        return this.genericManager;
    }


    public IpubTGSSListManger getTgssListManger(){
        if (this.tgssListManger == null) {
            this.tgssListManger = new KnPubTGSSListManager();
        }
        return this.tgssListManger;
    }
	
	    public IPubMCPTTManager getPubMCPTTManager(){
        if (this.pubMCPTTManager == null) {
            this.pubMCPTTManager = new KnPubMCPTTManager();
        }
        return this.pubMCPTTManager;
    }

    public IPubMCDATAManager getMcdataManager() {
        if (this.mcdataManager == null) {
            this.mcdataManager = new KnPubMCDATAManager();
        }
        return this.mcdataManager;
    }

    public IPubMCVideoManager getMcVideoManager() {
        if (this.mcvideoManager == null) {
            this.mcvideoManager = new KnPubMCVideoManager();
        }
        return this.mcvideoManager;
    }

    public IPubMCSGroupDocManager getMcsGroupDocManager() {
        if (this.mcsGroupDocManager== null) {
            this.mcsGroupDocManager = new KnPubMCSGroupDocManager();
        }
        return this.mcsGroupDocManager;
    }

    public IPubMCSUserDirManager getMCSUserDirManager() {
        if (this.mcsUserDirManager== null) {
            this.mcsUserDirManager = new KnPubMCSUserDirManager();
        }
        return this.mcsUserDirManager;
    }

    /**
     *
     * @param contactInfo
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public KnOpPubResponse addContacts(KnIPPubContactInfoDTO contactInfo, KnPersisterTxn persisterTxn) throws KnXDMServerException { //, KnFWException  {

        String methodName = "addContacts";
        knLogger.debug( methodName, "Entry : ");
        return getContactListManager().addContacts(contactInfo, persisterTxn);
    }


    /**
     *
     * @param contactInfo
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public KnOpPubResponse modifyContacts(KnIPPubContactInfoDTO contactInfo, KnPersisterTxn persisterTxn) throws KnXDMServerException { //, KnFWException  {

        String methodName = "modifyContacts";
        knLogger.debug( methodName, "Entry : ");
        return getContactListManager().modifyContacts(contactInfo, persisterTxn);
    }


    /**
     *
     * @param contactInfo
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public KnOpPubResponse deleteContacts(KnIPPubContactInfoDTO contactInfo, KnPersisterTxn persisterTxn) throws KnXDMServerException { //, KnFWException  {

        String methodName = "deleteContacts";
        knLogger.debug( methodName, "Entry : ");
        return getContactListManager().deleteContacts(contactInfo, persisterTxn);
    }


    /**
     *
     * @param contactInfo
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public KnPubContactDTO getContactListDetails(KnIPPubContactDTO contactInfo, KnPersisterTxn persisterTxn) throws KnXDMServerException { //, KnFWException  {

        String methodName = "getContactListDetails";
        knLogger.debug( methodName, "Entry : ");
        return getContactListManager().getContactListDetails(contactInfo, persisterTxn);
    }


    /**
     *
     * @param contactInfo
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public Collection<KnPubContactInfoDTO> getAllContactLists(KnIPPubContactDTO contactInfo, KnPersisterTxn persisterTxn) throws KnXDMServerException { //, KnFWException  {

        String methodName = "getAllContactLists";
        knLogger.debug( methodName, "Entry : ");
        return getContactListManager().getAllContactLists(contactInfo, persisterTxn);
    }

    /**
     *
     * @param contactDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public KnOPPubDirResponse getIndexDetails(KnIPPubContactDTO contactDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {//, KnFWException {

        String methodName = "getIndexDetails";
        knLogger.debug( methodName, "Entry : ");
        return getContactListManager().getIndexDetails(contactDTO, persisterTxn);
    }


    /**
     *
     * @param groupInfo
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     * @throws KnFWException
     */
    public KnOpPubResponse createGroup(KnIPPubGroupInfoDTO groupInfo, KnPersisterTxn persisterTxn) throws KnXDMServerException { //, KnFWException {

        String methodName = "createGroup";
        knLogger.debug( methodName, "Entry : ");
        return getGroupManager().createGroup(groupInfo, persisterTxn);
    }

    /**
     *
     * @param groupInfo
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     * @throws KnFWException
     */
    public KnOpPubResponse addGroupMember(KnIPPubGroupInfoDTO groupInfo, KnPersisterTxn persisterTxn) throws KnXDMServerException { //, KnFWException {

        String methodName = "addGroupMember";
        knLogger.debug( methodName, "Entry : ");
        return getGroupManager().addGroupMember(groupInfo, persisterTxn);
    }

    /**
     *
     * @param groupInfo
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     * @throws KnFWException
     */
    public KnOpPubResponse modifyGroupMember(KnIPPubGroupInfoDTO groupInfo, KnPersisterTxn persisterTxn) throws KnXDMServerException { //, KnFWException {

        String methodName = "modifyGroupMember";
        knLogger.debug( methodName, "Entry : ");
        return getGroupManager().modifyGroupMember(groupInfo, persisterTxn);
    }

    /**
     *
     * @param groupInfo
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     * @throws KnFWException
     */
    public KnOpPubResponse modifyGroupName(KnIPPubGroupInfoDTO groupInfo, KnPersisterTxn persisterTxn) throws KnXDMServerException { //, KnFWException {

        String methodName = "modifyGroupName";
        knLogger.debug( methodName, "Entry : ");
        return getGroupManager().modifyGroupName(groupInfo, persisterTxn);
    }

    /**
     *
     * @param groupInfo
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     * @throws KnFWException
     */
    public KnOpPubResponse deleteGroupMember(KnIPPubGroupInfoDTO groupInfo, KnPersisterTxn persisterTxn) throws KnXDMServerException { //, KnFWException {

        String methodName = "deleteGroupMember";
        knLogger.debug( methodName, "Entry : ");
        return getGroupManager().deleteGroupMember(groupInfo, persisterTxn);
    }

    /**
     *
     * @param groupInfo
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     * @throws KnFWException
     */
    public KnOpPubResponse deleteGroup(KnIPPubGroupInfoDTO groupInfo, KnPersisterTxn persisterTxn) throws KnXDMServerException { //, KnFWException {

        String methodName = "deleteGroup";
        knLogger.debug( methodName, "Entry : ");
        return getGroupManager().deleteGroup(groupInfo, persisterTxn);
    }

    /**
     *
     * @param groupInfo
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     * @throws KnFWException
     */
    public KnPubGroupInfoDTO getGroupDocDetails(KnIPPubGroupDTO groupInfo, KnPersisterTxn persisterTxn) throws KnXDMServerException { //, KnFWException {

        String methodName = "getGroupDocDetails";
        knLogger.debug( methodName, "Entry : ");
        return getGroupManager().getGroupDocDetails(groupInfo, persisterTxn);
    }

    /**
     *
     * @param groupInfo
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     * @throws KnFWException
     */
    public KnPubGroupInfoDTO getPubGroupDetails(KnIPPubGroupDTO groupInfo, KnPersisterTxn persisterTxn) throws KnXDMServerException { //, KnFWException {
        String methodName = "getPubGroupDetails";
        knLogger.debug( methodName, "Entry : ");
        return getGroupManager().getPubGroupDetails(groupInfo, persisterTxn);
    }


    /**
     *
     * @param groupInfo
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     * @throws KnFWException
     */
    public KnPubGroupInfoDTO getGroupDetails(KnIPPubGroupDTO groupInfo, KnPersisterTxn persisterTxn) throws KnXDMServerException { //, KnFWException {

        String methodName = "getGroupDetails";
        knLogger.debug( methodName, "Entry : ");
        return getGroupManager().getGroupDetails(groupInfo, persisterTxn);
    }

    /**
     *
     * @param groupInfo
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     * @throws KnFWException
     */
    public Collection<KnPubGroupDTO> getGroupList(KnIPPubGroupDTO groupInfo, KnPersisterTxn persisterTxn) throws KnXDMServerException { //, KnFWException {

        String methodName = "getGroupList";
        knLogger.debug( methodName, "Entry : ");
        return getGroupManager().getGroupList(groupInfo, persisterTxn);
    }

    /**
     *
     * @param groupInfo
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     * @throws KnFWException
     */
    public Collection<KnPubGroupDTO> getPubGroupList(KnIPPubGroupDTO groupInfo, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getPubGroupList";
        knLogger.debug( methodName, "Entry : ");
        return getGroupManager().getPubGroupList(groupInfo, persisterTxn);
    }


    /**
     *
     * @param subsInfoDTO
     * @param persisterTxn
     * @throws KnXDMServerException
     */
    public void forceSync(KnIPPubSubsDTO subsInfoDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "forceSync";
        knLogger.debug( methodName, "Entry :");
        getGenericManager().forceSync(subsInfoDTO, persisterTxn);
        return;
    }


    /**
     *
     * @param subsInfoDTO
     * @param persisterTxn
     * @throws KnXDMServerException
     */
    public void deleteAllContactsAndGroups(KnIPPubSubsDTO subsInfoDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "deleteAllContactsAndGroups";
        knLogger.debug( methodName, "Entry :");
        getGenericManager().deleteAllContactsAndGroups(subsInfoDTO, persisterTxn);
        return;
    }


    /**
     *
     * @param changeMdnDTO
     * @param persisterTxn
     * @throws KnXDMServerException
     */
    public void changeMdn(KnIPChangeMDNInfoDTO changeMdnDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "changeMdn";
        knLogger.debug( methodName, "Entry : ");
        getGenericManager().changeMdn(changeMdnDTO, persisterTxn);
        return;
    }

    /**
     *
     * @param subsInfoDTO
     * @param persisterTxn
     * @throws KnXDMServerException
     */
    public void deleteAllContactsAndGroups4ListOfMdns(KnIPPubSubsDTO subsInfoDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "deleteAllContactsAndGroups4ListOfMdns";
        knLogger.debug( methodName, "Entry :");
        getGenericManager().deleteAllContactsAndGroups4ListOfMdns(subsInfoDTO, persisterTxn);
        return;
    }


    @Override
    public KnIPPubAuthListDTO getAuthorizationList(KnIPPubAuthListDTO authListDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getAuthorizationList(KnIPPubAuthListDTO, KnPersisterTxn)";
        knLogger.debug( methodName, "Entry :");
        return getAuthListManager().getAuthorizationList(authListDTO,persisterTxn);
    }

    @Override
    public KnOpPubResponse updateAuthorizationList(KnIPPubAuthListDTO authListDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "updateAuthorizationList(KnIPPubAuthListDTO, KnPersisterTxn)";
        knLogger.debug( methodName, "Entry :");
        return getAuthListManager().updateAuthorizationList(authListDTO, persisterTxn);
    }

    @Override
    public KnEmergencyConfigDocDTO getEmergencyConfigDoc(KnIPPubAuthListDTO authListDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getEmergencyConfigDoc(KnIPPubAuthListDTO, KnPersisterTxn)";
        knLogger.debug( methodName, "Entry :", authListDTO);
        return getAuthListManager().getEmergencyConfigDoc(authListDTO, persisterTxn);
    }

    /**
     * Interface to add/modify/remove subscribers dynamic contacts
     * @param inputDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    @Override
    public KnOpPubResponse modifyDynamicContacts(KnIPPubContactInfoDTO inputDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "modifyDynamicContacts";
        knLogger.debug( methodName, "Entry : ");
        return getContactListManager().modifyDynamicContacts(inputDTO, persisterTxn);
    }

    /**
     * Interface to delete third party clients' dynamic contacts
     *
     * @param inputDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    @Override
    public KnPubContactDTO deleteDynamicContacts(KnIPPubContactInfoDTO inputDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "deleteDynamicContacts";
        knLogger.debug( methodName, "Entry : ");
        return getContactListManager().deleteDynamicContacts(inputDTO, persisterTxn);
    }

    /**
     * Interface to get the dynamic contact list for a third party client.
     *
     * @param inputDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    @Override
    public KnPubContactDTO getDynamicContacts(KnIPPubContactInfoDTO inputDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getDynamicContacts";
        knLogger.debug( methodName, "Entry : ");
        return getContactListManager().getDynamicContacts(inputDTO, persisterTxn);
    }

    /**
     * Interface to retrieve the dynamic group details
     *
     * @param groupInfo
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    @Override
    public KnPubGroupInfoDTO getDynamicGroupDetails(KnIPPubGroupInfoDTO groupInfo, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getDynamicGroupDetails";
        knLogger.debug( methodName, "Entry : ");
        return getGroupManager().getDynamicGroupDetails(groupInfo, persisterTxn);
    }

    /**
     * Interface to retrieve the list of groups associated with an owner MDN.
     *
     * @param groupInfo
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    @Override
    public List<KnPubGroupDTO> getDynamicNonSharedGrpList(KnIPPubGroupInfoDTO groupInfo, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getDynamicNonSharedGrpList";
        knLogger.debug( methodName, "Entry : ");
        return getGroupManager().getDynamicNonSharedGrpList(groupInfo, persisterTxn);
    }

    /**
     * Interface to delete the non-shared group
     *
     * @param groupInfo
     * @param persisterTxn
     * @return
     */
    @Override
    public KnOpPubResponse deleteDynamicNonSharedGrp(KnIPPubGroupInfoDTO groupInfo, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "deleteDynamicNonSharedGrp";
        knLogger.debug( methodName, "Entry : ");
        return getGroupManager().deleteDynamicNonSharedGrp(groupInfo, persisterTxn);
    }

    /**
     * createNonSharedGroup
     *
     * @param groupInfo
     * @param persisterTxn
     * @return
     */
    @Override
    public KnOpPubResponse createNonSharedGroup(KnIPPubGroupInfoDTO groupInfo, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "deleteDynamicNonSharedGrp";
        knLogger.debug( methodName, "Entry : ");
        return getGroupManager().createNonSharedGroup(groupInfo, persisterTxn);
    }

    /**
     * Interface to modify non-shared group details - Add/modify/remove members are supported
     *
     * @param inputDTO
     * @param persisterTxn
     * @return
     */
    @Override
    public KnOpPubResponse modifyNonSharedGroup(KnIPPubGroupInfoDTO inputDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException{
        String methodName = "modifyNonSharedGroup";
        knLogger.debug( methodName, "Entry : ");
        return getGroupManager().modifyNonSharedGroup(inputDTO, persisterTxn);
    }

    @Override
    public void modifyGroupUriContext(KnIPPubGroupInfoDTO inputDTO,  String xdmpttserverId, KnPersisterTxn persisterTxn) throws KnXDMServerException{
        String methodName = "getGroupUsageListDoc(KnIPPubAuthListDTO, KnPersisterTxn)";
        knLogger.debug( methodName, "Entry :", inputDTO);
        getGroupManager().modifyGroupUriContext(inputDTO, xdmpttserverId, persisterTxn);
    }

    
    /**
     * {@inheritDoc}
     */
	@Override
	public KnPubContactDTO getXdmintfContactListDetails(KnIPPubContactDTO contactInfo,KnPersisterTxn persisterTxn) throws KnXDMServerException {
		String methodName = "getXdmintfContactListDetails()";
        knLogger.debug( methodName, "Entry : ");
        return getContactListManager().getXdmintfContactListDetails(contactInfo, persisterTxn);
	}

    @Override
    public KnIPTGSSListDTO getTGSSList(KnIPTGSSListDTO tgssList, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getTGSSList()";
        knLogger.debug( methodName, "Entry : ");
        return getTgssListManger().getTGSSList(tgssList,persisterTxn );
    }

    @Override
    public KnOpPubResponse updateTGSSList(KnIPTGSSListDTO tgssList, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "updateTGSSList()";
        knLogger.debug( methodName, "Entry : ");
        return getTgssListManger().updateTGSSList(tgssList, persisterTxn);
    }

    @Override
    public KnOpPubResponse deleteTGSSList(KnIPTGSSListDTO tgssList, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "deleteTGSSList()";
        knLogger.debug( methodName, "Entry : ");
        return getTgssListManger().deleteTGSSList(tgssList, persisterTxn);
    }
	
	@Override
    public KnMCSXCAPRespDTO getMCPTTUEConfig(KnIPMCSDTO ipmcsdto, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getMCPTTUEConfig()";
        knLogger.debug( methodName, "--->Entry : ");
        return getPubMCPTTManager().getMCPTTUEConfig(ipmcsdto, persisterTxn);
    }

    @Override
    public KnMCSXCAPRespDTO getMCPTTUserProfile(KnIPMCSDTO ipmcsdto, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getMCPTTUserProfile()";
        knLogger.debug( methodName, "--->Entry : ");
        return getPubMCPTTManager().getMCPTTUserProfile(ipmcsdto, persisterTxn);
    }

    @Override
    public KnMCSXCAPRespDTO getMCPTTServiceConfig(KnIPMCSDTO ipmcsdto, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getMCPTTServiceConfig()";
        knLogger.debug( methodName, "--->Entry : ");
        return getPubMCPTTManager().getMCPTTServiceConfig(ipmcsdto, persisterTxn);
    }

    @Override
    public KnMCSXCAPRespDTO getMCDataUEConfig(KnIPMCSDTO ipmcsdto, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getMCDataUEConfig()";
        knLogger.debug( methodName, "Entry : ");
        return getMcdataManager().getMCDataUEConfig(ipmcsdto,persisterTxn);
    }

    @Override
    public KnMCSXCAPRespDTO getMCDataUserProfile(KnIPMCSDTO ipmcsdto, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getMCDataUserProfile()";
        knLogger.debug( methodName, "Entry : ");
        return getMcdataManager().getMCDataUserProfile(ipmcsdto,persisterTxn);
    }

    @Override
    public KnMCSXCAPRespDTO getMCDataServiceConfig(KnIPMCSDTO ipmcsdto, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getMCDataServiceConfig()";
        knLogger.debug( methodName, "Entry : ");
        return getMcdataManager().getMCDataServiceConfig(ipmcsdto,persisterTxn);
    }

    @Override
    public KnMCSXCAPRespDTO getMCVideoUEConfig(KnIPMCSDTO ipmcsdto, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getMCVideoUEConfig()";
        knLogger.debug( methodName, "Entry : ");
        return getMcVideoManager().getMCVideoUEConfig(ipmcsdto,persisterTxn);
    }

    @Override
    public KnMCSXCAPRespDTO getMCVideoUserProfile(KnIPMCSDTO ipmcsdto, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getMCVideoUserProfile()";
        knLogger.debug( methodName, "Entry : ");
        return getMcVideoManager().getMCVideoUserProfile(ipmcsdto,persisterTxn);
    }

    @Override
    public KnMCSXCAPRespDTO getMCVideoServiceConfig(KnIPMCSDTO ipmcsdto, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getMCVideoServiceConfig()";
        knLogger.debug( methodName, "Entry : ");
        return getMcVideoManager().getMCVideoServiceConfig(ipmcsdto,persisterTxn);
    }

    @Override
    public KnMCSXCAPRespDTO getMCSGroupDoc(KnIPMCSDTO ipmcsdto, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getMCSGroupDoc()";
        knLogger.debug( methodName, "Entry : ");
        return getMcsGroupDocManager().getMCSGroupDoc(ipmcsdto,persisterTxn);
    }

    @Override
    public KnMCSXCAPRespDTO getMCSUserDir(KnIPMCSDTO ipmcsdto, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getMCSUserDir()";
        knLogger.debug( methodName, "Entry : ");
        return getMCSUserDirManager().getMCSUserDir(ipmcsdto,persisterTxn);
    }
}
