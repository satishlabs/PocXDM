/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  IXDMFacadeIntf.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Harsha             Dec 29, 2010  7.0
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
package com.kodiak.xdms.xdmintf;

import com.kodiak.common.commdto.request.IXDMRequestDTO;
import com.kodiak.common.commdto.response.IXDMResponseDTO;
import com.kodiak.frameworks.messaging.common.dto.KnMessage;

/**
 * declare all the APIs of the services Provisioning, public and corporate
 * each API will implemented by the KnXDMMediator
 */
public interface IXDMFacadeIntf extends IXDMCorpFacadeIntf {

    /**
     * Method call for Create Subscriber
     *
     * @param requestDTO IXDMRequestDTO
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO createSubscriber(IXDMRequestDTO requestDTO);

    /**
     * method to perform the updation of the subscriber Profile
     *
     * @param message KnMessage
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO updateSubscriber(KnMessage message);

    /**
     * Method to retrieve the subscriber Profile Information
     *
     * @param requestDTO IXDMRequestDTO
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO getSubscriberDetails(IXDMRequestDTO requestDTO);

    /**
     * method to perform the deletion of the subscriber
     *
     * @param message KnMessage
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO deleteSubscriber(KnMessage message);

    /**
     * method to perform the re-activation and the deactivation of the Subscriber
     *
     * @param message KnMessage
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO changeServiceAuthStatus(KnMessage message);

    /**
     * method to perform the activation of the Subscriber
     *
     * @param message KnMessage
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO activateSubscriber(KnMessage message);

    /**
     * method to retrieve the Subscriber Config Document
     *
     * @param message KnMessage
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO getSubscriberConfigDocument(KnMessage message);

    /**
     * method to perform migration of old MDN to the new MDN
     *
     * @param message KnMessage
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO changeMdn(KnMessage message);

    /**
     * method to perform the Force Sync of the data on to the client
     *
     * @param requestDTO IXDMRequestDTO
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO forceSync(IXDMRequestDTO requestDTO);

    /**
     * method to perfor the Update of the network name of the Subscriber
     *
     * @param message KnMessage
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO updateSubscriberName(KnMessage message);

    /**
     * @param message KnMessage
     * @return
     */
    public IXDMResponseDTO updateCampedGroup(KnMessage message);

    /**
     * method to retrieve the Default Subscriber Profile
     *
     * @param message KnMessage
     * @return IXDMResponse DTO Object
     */
    public IXDMResponseDTO getDefaultSubscriberProfile(IXDMRequestDTO message);

    /**
     * @param message
     */
    public void addContacts(KnMessage message);

    /**
     * @param message
     */
    public void deleteContacts(KnMessage message);

    /**
     * @param message
     */
    public void modifyContacts(KnMessage message);

    /**
     * @param message
     */
    public void getContactList(KnMessage message);

    /**
     * @param message
     */
    public void createGroup(KnMessage message);

    /**
     * @param message
     */
    public void modifyGroupName(KnMessage message);

    /**
     * @param message
     */
    public void modifyGroupMember(KnMessage message);

    /**
     * @param message
     */
    public void deleteGroupMember(KnMessage message);

    /**
     * @param message
     */
    public void addGroupMember(KnMessage message);

    /**
     * @param message
     */
    public void deleteGroup(KnMessage message);

    /**
     * Get group details - SOAP interface
     *
     * @param message
     */
    public void getGroupDetails(KnMessage message);

    /**
     * Get group document details - XCAP interface
     *
     * @param message
     */
    public void getGroupDocDetails(KnMessage message);

    /**
     * Get group document details - PTX interface
     *
     * @param message
     */
    public IXDMResponseDTO getPubGroupDetails(KnMessage message);
    /**
     * @param message
     */
    public void getAllContactList(KnMessage message);

    /**
     * @param message
     */
    public void getDirectory(KnMessage message);


    /**
     * @param message
     */
    public void getRLSDoc(KnMessage message);

    /**
     * @param message
     */
    public void getGroupList(KnMessage message);

    /**
     * @param message
     */
    public IXDMResponseDTO getPubGroupList(KnMessage message);

    /**
     * Method which is used for health check of the XDM
     *
     * @param message
     */
    public void isAlive(KnMessage message);


    /**
     * Method which response to the Client when found an invalid operation type is being passed
     *
     * @param requestDTO IXDMRequestDTO
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO unKnownOperation(IXDMRequestDTO requestDTO);

    /**
     * method for Custom Prov operations
     *
     * @param requestDTO IXDMRequestDTO
     * @return IXDMResponseDTO
     */
    public IXDMResponseDTO customProvOp(IXDMRequestDTO requestDTO);

    /**
     * method for updating client Feature set
     *
     * @param message KnMessage
     * @return
     */
    public IXDMResponseDTO updateClientFS1(KnMessage message);


    /**
     * method for updating usesr agent
     *
     * @param requestDTO
     * @return
     */
    public IXDMResponseDTO updateUserAgent(IXDMRequestDTO requestDTO);

    /**
     * Method call for Create PAM Account
     *
     * @param message IXDMRequestDTO
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO createLicensePack(KnMessage message);
    
    /**
     * Method call for Get License Pack Profile
     *
     * @param message IXDMRequestDTO
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO getLicensePackProfile(KnMessage message);

    /**
     * Method call for delete  PAM Account
     *
     * @param message IXDMRequestDTO
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO deleteLicensePack(KnMessage message);

    /**
     * method to perform the re-activation and the deactivation of the PAM Account
     *
     * @param message IXDMRequestDTO
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO changePAMServiceAuthStatus(KnMessage message);

    /**
     * method to perform the update of PAM Billing mDN
     *
     * @param message IXDMRequestDTO
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO changeBillingNumber(KnMessage message);
    
    /**
     * method to perform the upgrade of PAM License pack
     *
     * @param message IXDMRequestDTO
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO upgradeLicensePack(KnMessage message);
    
    /**
     * method to retrieve PAM Account MDN Details(list of Pseudomdn & its service auth status)
     * for the Billing MDN/Ext PAM Account ID
     *
     * @param message KnMessage
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO getPAMAccountMDNsDetails(KnMessage message);

    /**
     * Method call for update PAM Account
     *
     * @param message IXDMRequestDTO
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO updateLicensePack(KnMessage message);

    /**
     * Method call for DowngradeRatePlan
     *
     * @param message IXDMRequestDTO
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO downgradeLicensePack(KnMessage message);
    
    /**
     * Method call for modifySubscCorpFeature
     * @param requestDTO
     * @return IXDMResponseDTO
     */
    public IXDMResponseDTO modifySubscCorpFeature(IXDMRequestDTO requestDTO);
    
    /**
     * Method call for modifySubscriberScanList
     * @param message KnMessage
     * @return IXDMResponseDTO
     */
    public IXDMResponseDTO modifySubscriberScanList(KnMessage message);
    
    /**
     * Method call for getSubscriberScanList
     * @param requestDTO
     * @return IXDMResponseDTO
     */
    public IXDMResponseDTO getSubscriberScanList(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO deleteExternalSubscriber(KnMessage message);

    /**
     * Method call for update Auto Pairing
     *
     * @param requestDTO IXDMRequestDTO
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO updateAutoPairing(IXDMRequestDTO requestDTO);

    /**
     * Method call for create TPUser
     *
     * @param requestDTO IXDMRequestDTO
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO createTPUser(IXDMRequestDTO requestDTO) ;
    
    /**
     * Method call for update TPUser
     *
     * @param requestDTO IXDMRequestDTO
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO updateTPUser(IXDMRequestDTO requestDTO) ;
    
    /**
     * Method call for delete TPUser
     *
     * @param message KnMessage
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO deleteTPUser(KnMessage message) ;
    
    /**
     * Method call for generate Activation code for TPUser
     *
     * @param requestDTO IXDMRequestDTO
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO generateTPActivationCode(IXDMRequestDTO requestDTO) ;

    /**
     * method to retrieve the Subscriber Config Document
     *
     * @param requestDTO IXDMRequestDTO
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO getSysConfig(IXDMRequestDTO requestDTO);

    /**
     * Interface to modify dynamic contact list
     * @param message
     */
    void modifyDynamicContacts(KnMessage message);

    /**
     * Interface to delete all dynamic contact list
     * @param msgObj
     */
    void deleteDynamicContacts(KnMessage msgObj);

    /**
     * Interface to retrieve the dynamic contact list.
     * @param msgObj
     */
    void getDynamicContacts(KnMessage msgObj);

    /**
     * Interface to retrieve the dynamic group details
     * @param msgObj
     */
    void getDynamicNonSharedGrpDetails(KnMessage msgObj);

    /**
     * Interface to retrieve the list of groups owned by MDN.
     * @param msgObj
     */
    void getDynamicNonSharedGrpList(KnMessage msgObj);

    /**
     * Interface to delete dynamic non-shared group
     * @param message
     */
    void deleteDynamicNonSharedGrp(KnMessage message);

    /**
     * Interface to create non-sharded dynamic group
     * @param msgObj
     */
    void createNonSharedGroup(KnMessage msgObj);

    /**
     * Interface to modify the non-shared dynamic group
     * @param msgObj
     */
    void modifyNonSharedGroup(KnMessage msgObj);

    public IXDMResponseDTO getAuthorizationList(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO updateAuthorizationList(KnMessage message);

    public IXDMResponseDTO getGroupUsageListDoc(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO getEmergencyConfigDoc(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO getPubContactList(KnMessage message);
    /**
     * Interface for device activation API
     * @param inputDTO
     * @return
     */
    IXDMResponseDTO deviceActivation(IXDMRequestDTO inputDTO);

    IXDMResponseDTO radioDeviceActivation(IXDMRequestDTO inputDTO);

    /**
     * Interface for user login API
     * @param message KnMessage
     * @return
     */
    IXDMResponseDTO userLogin(KnMessage message);

    public IXDMResponseDTO upgradeOrDowngradLicensePack(KnMessage message);

    public IXDMResponseDTO getSubsEmergencyDetails(IXDMRequestDTO inputDTO);

    public IXDMResponseDTO getSubscriberCorpGroupList(IXDMRequestDTO inputDTO);

    public IXDMResponseDTO getTGSSList(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO updateTGSSList(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO deleteTGSSList(IXDMRequestDTO requestDTO);
    
    public IXDMResponseDTO searchCorpAddressBook(IXDMRequestDTO requestDTO);
    
    public IXDMResponseDTO updateMCSIds(IXDMRequestDTO requestDTO);
    
    public IXDMResponseDTO updateUserId(IXDMRequestDTO requestDTO);
        
    public IXDMResponseDTO updateLicensePackSubsMCSIds(IXDMRequestDTO requestDTO);
    	    
    public IXDMResponseDTO updateLicensePackSubsUserId(IXDMRequestDTO requestDTO);
	
	public IXDMResponseDTO updatePrivacyOptStatus(KnMessage message);

    /**
     *  Interface for MCS MCDATA XCAP Microservice
     *
     */
	public IXDMResponseDTO getMCPTTUEConfig(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO getMCPTTUserProfile(KnMessage message);

    public IXDMResponseDTO getMCPTTServiceConfig(IXDMRequestDTO requestDTO);
	 
    public IXDMResponseDTO getMCDATAUEConfig(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO getMCDATAUserProfile(KnMessage message);

    public IXDMResponseDTO getMCDATAServiceConfig(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO getMCVideoUEConfig(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO getMCVideoUserProfile(KnMessage message);

    public IXDMResponseDTO getMCVideoServiceConfig(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO getMCSGroupDoc(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO getMdnProfileIdsForMcPttIds(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO getMCSUserDir(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO selectProfileMdn(IXDMRequestDTO inputDTO);
    
    public IXDMResponseDTO createDevice(IXDMRequestDTO requestDTO);
    
    public IXDMResponseDTO getDeviceInfo(IXDMRequestDTO requestDTO);
    
    public IXDMResponseDTO deleteDeviceInfo(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO modifyDevice(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO loginNotifyEvent(IXDMRequestDTO inputDTO);

    public IXDMResponseDTO getSubsGroupMembershipDetails(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO getAuthorizedUserList(IXDMRequestDTO inputDTO);

    IXDMResponseDTO getAsyncOpStatus(IXDMRequestDTO requestDTO);

    IXDMResponseDTO getUserprofileidsByProfileMdns(IXDMRequestDTO requestDTO);


    IXDMResponseDTO getSubscrClientSettings(IXDMRequestDTO requestDTO);

    /**
     * method to perform the updation of the subscriber Client recording settings
     *
     * @param message KnMessage
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO setSubscrClientSettings(KnMessage message);

    public IXDMResponseDTO getMdnAuthorizationForGroupId(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO modifyGroupsUGWConfig(KnMessage message);

    public IXDMResponseDTO getGroupsUGWConfig(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO assignCommonContactList(KnMessage message);

    public IXDMResponseDTO unAssignCommonContactList(KnMessage message);

    public IXDMResponseDTO getDeviceList(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO getDeviceDetails(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO getGroupStats(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO getSubscriberStats(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO getDeviceStats(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO getCorporateFS(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO updateCorporateFS(IXDMRequestDTO requestDTO);
    public IXDMResponseDTO createCorpAccount(IXDMRequestDTO requestDTO);
    public IXDMResponseDTO deleteCorpAccount(IXDMRequestDTO requestDTO);
    public IXDMResponseDTO getCorporateAccountDetails(IXDMRequestDTO requestDTO);
    public IXDMResponseDTO getCorporateAccountsList(IXDMRequestDTO requestDTO);
    public IXDMResponseDTO updateCorpAccount(IXDMRequestDTO requestDTO);
    public IXDMResponseDTO getGroupsDetailsWithoutMembers(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO setCATAccessPermission(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO getExtGWProfileList(IXDMRequestDTO requestDTO);


    public IXDMResponseDTO createHierarchy(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO modifyHierarchy(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO createPTTSettingDoc(IXDMRequestDTO requestDTO);
    public IXDMResponseDTO getPTTSettingDocList(IXDMRequestDTO requestDTO);
    public IXDMResponseDTO getPTTSettingDoc(IXDMRequestDTO requestDTO);
    public IXDMResponseDTO setDefaultPttSettingDoc(IXDMRequestDTO requestDTO);
    public IXDMResponseDTO deletePTTSettingDoc(IXDMRequestDTO requestDTO);
    public IXDMResponseDTO assignPttSettingToHierarchy(IXDMRequestDTO requestDTO);
    public IXDMResponseDTO unassignPttSettingToHierarchy(IXDMRequestDTO requestDTO);
    public IXDMResponseDTO assignPttSettingDocToMdns(IXDMRequestDTO requestDTO);
    public IXDMResponseDTO unassignPttSettingDocToMdns(IXDMRequestDTO requestDTO);
    public IXDMResponseDTO getPttSettingDocMdnList(IXDMRequestDTO requestDTO);
    public IXDMResponseDTO getMDNCountForPttSettingDocID(IXDMRequestDTO requestDTO);
    public IXDMResponseDTO assignPttSettingToCorp(IXDMRequestDTO requestDTO);
    public IXDMResponseDTO unassignPttSettingToCorp(IXDMRequestDTO requestDTO);
    public IXDMResponseDTO modifyPTTSettingTemplate(IXDMRequestDTO requestDTO);

}
