/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ***************************************************************************
 * File name:   Kn.java
 * Subsystem:
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ravi Shanker .P       Jan 11, 2011        7.0
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
 * you entered into with Kodiak Networks.nn
 * ******************************************************************************
 */
package com.kodiak.xdms.mediator;

import com.kodiak.common.commdto.request.IXDMRequestDTO;
import com.kodiak.common.commdto.response.IXDMResponseDTO;
import com.kodiak.frameworks.messaging.common.dto.KnMessage;
import com.kodiak.xdms.server.common.KnXDMServerException;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;

public interface IXDMMediatorIntf extends IXDMCorpMediatorIntf {
    /**
     * create Subscriber
     *
     * @param requestDTO IXDMRequestDTO
     * @return IXDMResponse DTO Object
     */
    public IXDMResponseDTO createSubscriber(IXDMRequestDTO requestDTO);

    /**
     * update Subscriber
     *
     * @param requestDTO IXDMRequestDTO
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO updateSubscriber(KnMessage message);

    /**
     * retrieve Subscriber details
     *
     * @param requestDTO IXDMRequestDTO
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO getSubscriberDetails(IXDMRequestDTO requestDTO);

    /**
     * retrieve  Subscriber configuration document
     *
     * @param requestDTO IXDMRequestDTO
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO getSubscriberConfigDocument(KnMessage message);

    /**
     * activate Subscriber
     *
     * @param message KnMessage
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO activateSubscriber(KnMessage message);

    /**
     * to re-activate & de-activate Subscriber
     *
     * @param requestDTO IXDMRequestDTO
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO changeServiceAuthStatus(KnMessage message);

    /**
     * force sync the data of subscriber
     *
     * @param requestDTO IXDMRequestDTO
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO forceSync(IXDMRequestDTO requestDTO);

    /**
     * delete Subscriber
     *
     * @param requestDTO IXDMRequestDTO
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO deleteSubscriber(KnMessage message);

    /**
     * method to update the network name of the subscriber
     *
     * @param requestDTO IXDMRequestDTO
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO updateSubscriberName(KnMessage message);

    /**
     * @param requestDTO
     * @return
     */
    public IXDMResponseDTO updateCampedGroup(KnMessage message);

    /**
     * method to retrieve the Default Subscriber Profile
     *
     * @param requestDTO IXDMRequestDTO
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO getDefaultSubscriberProfile(IXDMRequestDTO requestDTO);

    /**
     * method to update the client Feature Set 1
     *
     * @param requestDTO IXDMRequestDTO
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO updateClientFS1(KnMessage message);

    /**
     * method to update the user agent
     *
     * @param requestDTO IXDMRequestDTO
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO updateUserAgent(IXDMRequestDTO requestDTO);


    /**
     * method to delete Scan list
     *
     * @param requestDTO IXDMRequestDTO
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO deleteScanList(IXDMRequestDTO requestDTO) ;
    /**
     * method to create/modify Scan list
     *
     * @param requestDTO IXDMRequestDTO
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO updateScanList(IXDMRequestDTO requestDTO);
    /**
     * @param requestDTO
     */

    public IXDMResponseDTO getSubscriberScanList(IXDMRequestDTO requestDTO);


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
    public void getAllContactLists(KnMessage message);

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
     * @param message
     */
    public void getGroupDetails(KnMessage message);

    /**
     * @param message
     */
    public void getGroupDocDetails(KnMessage message);

    /**
     * @param message
     */
    public IXDMResponseDTO getPubGroupDetails(KnMessage message);

    /**
     *
     * @param message
     */
//    public void selectGroupList(KnMessage message);

    /**
     * @param message
     */
    public void getDirectory(KnMessage message);


    /**
     * @param message
     */
    public void getRlsDoc(KnMessage message);

    /**
     * @param message
     */
    public void getGroupList(KnMessage message);

    /**
     * @param message
     */
    public IXDMResponseDTO getPubGroupList(KnMessage message);

    /**
     * method to perform the Health check for the XDM
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
     * method to migrate from one MDN to another MDN
     *
     * @param requestDTO IXDMRequestDTO
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO changeMdn(KnMessage message);

    /**
     * create PAM Account
     *
     * @param message KnMessage
     * @return IXDMResponse DTO Object
     */
    public IXDMResponseDTO createLicensePack(KnMessage message);
    
    /**
     * Get License Pack Profile
     *
     * @param message KnMessage
     * @return IXDMResponse DTO Object
     */
    public IXDMResponseDTO getLicensePackProfile(KnMessage message);

    /**
     * delete PAM Account
     *
     * @param message KnMessage
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO deleteLicensePack(KnMessage message);

    /**
     * to re-activate & de-activate bulk Subscriber
     *
     * @param message KnMessage
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO changePAMServiceAuthStatus(KnMessage message);

    /**
     * to get PAM account details (list of Pseudomdn & its service auth status)
     *
     * @param message KnMessage
     * @return IXDMResponseDTO Object
     */
    
    public IXDMResponseDTO getPAMAccountMDNsDetails(KnMessage message);

    /**
     * update PAM Account
     *
     * @param message KnMessage
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

    public IXDMResponseDTO deleteExternalSubscriber(KnMessage message);
    
    /**
     * Migrate PAM Account
     *
     * @param message KnMessage
     * @return IXDMResponse DTO Object
     */
    public IXDMResponseDTO changeBillingNumber(KnMessage message);
    
    
    /**
     * upgrade PAM Account/Rateplan
     *
     * @param message KnMessage
     * @return IXDMResponse DTO Object
     */
    public IXDMResponseDTO upgradeLicensePack(KnMessage message);

    /**
     * update Auto Pairing
     *
     * @param requestDTO IXDMRequestDTO
     * @return IXDMResponse DTO Object
     */
    public IXDMResponseDTO updateAutoPairing(IXDMRequestDTO requestDTO);
    
    /**
     * Method call for create TPUSer
     *
     * @param message IXDMRequestDTO
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO createTPUser(IXDMRequestDTO requestDTO) ;
    
    /**
     * Method call for update TPUSer
     *
     * @param message IXDMRequestDTO
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO updateTPUser(IXDMRequestDTO requestDTO) ;
    
    /**
     * Method call for delete TPUSer
     *
     * @param message IXDMRequestDTO
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO deleteTPUser(KnMessage message) ;
    
    /**
     * Method call for generate activation code for TPUSer
     *
     * @param message IXDMRequestDTO
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO generateTPActivationCode(IXDMRequestDTO requestDTO) ;

    /**
     * retrieve system level config
     *
     * @param requestDTO IXDMRequestDTO
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO getSysConfig(IXDMRequestDTO requestDTO);

    /**
     * Interface for adding/modifying/removing dynamic contacts.
     * @param message
     */
    void modifyDynamicContacts(KnMessage message);
    public IXDMResponseDTO getAuthorizationList(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO updateAuthorizationList(KnMessage message);

    public IXDMResponseDTO getEmergencyConfigDoc(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO getGroupUsageListDoc(IXDMRequestDTO requestDTO);

    /**
     * Create non-sharable groups
     * @param message
     */
    void createNonSharedGroup(KnMessage message);

    /**
     * Interface to delete the contact list for third party clients
     * @param message
     */
    void deleteDynamicContacts(KnMessage message);

    /**
     * Interface to retrieve the dynamic contact list for third party clients.
     * @param message
     */
    void getDynamicContacts(KnMessage message);

    /**
     * Interface to retrieve dynamic non-Shared group details based on owner MDN and display name
     * @param message
     */
    void getDynamicNonSharedGrpDetails(KnMessage message);

    /**
     * Interface to retrieve the list of groups associated with an owner MDN.
     * @param msgObj
     */
    void getDynamicNonSharedGrpList(KnMessage msgObj);

    /**
     * Interface to delete the non-shared group
     * @param message
     */
    void deleteDynamicNonSharedGrp(KnMessage message);

    /**
     * Interface to modify the non-shared dynamic group
     * @param msgObj
     */
    void modifyNonSharedGroup(KnMessage msgObj);

    public IXDMResponseDTO getPubContactList(KnMessage message);


    /**
     * Device activation
     * @param inputDTO
     * @return
     */
    public IXDMResponseDTO deviceActivation(IXDMRequestDTO inputDTO);

    public IXDMResponseDTO radioDeviceActivation(IXDMRequestDTO inputDTO);

    /**
     * User login
     * @param inputDTO
     * @return
     */
    IXDMResponseDTO userLogin(KnMessage message);
    
    public IXDMResponseDTO upgradeOrDowngradLicensePack(KnMessage message);

    public IXDMResponseDTO getSubsEmergencyDetails(IXDMRequestDTO inputDTO);
    public IXDMResponseDTO getSubscriberCorpGroupList(IXDMRequestDTO subscriberCorpInfo);

    public IXDMResponseDTO getTGSSList(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO updateTGSSList(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO deleteTGSSList(IXDMRequestDTO requestDTO);
    
    public IXDMResponseDTO searchCorpAddressBook(IXDMRequestDTO subscriberInfo);
    
    public IXDMResponseDTO updateMCSIds(IXDMRequestDTO requestDTO);
    
    public IXDMResponseDTO updateUserId(IXDMRequestDTO requestDTO);
        
    public IXDMResponseDTO updateLicensePackSubsMCSIds(IXDMRequestDTO requestDTO);
    	    
    public IXDMResponseDTO updateLicensePackSubsUserId(IXDMRequestDTO requestDTO);
	
	public IXDMResponseDTO updatePrivacyOptStatus(KnMessage message);
	
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

    IXDMResponseDTO createBulkCorpGroup(IXDMRequestDTO requestDTO);

    IXDMResponseDTO getProfileGroupList(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO createGroupProfile(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO getGroupProfileList(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO getGroupProfileDetails(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO searchGroupProfile(IXDMRequestDTO requestDTO);

    IXDMResponseDTO createCorpGroupWithProfile(KnMessage message);

    IXDMResponseDTO modifyGroupProfile(KnMessage message);

    IXDMResponseDTO deleteBulkCorpGroup(KnMessage message);

    IXDMResponseDTO deleteGroupProfile(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO getAuthorizedUserList(IXDMRequestDTO inputDTO);

    IXDMResponseDTO getSharedCorpTrustMatrix(IXDMRequestDTO requestDTO);

    IXDMResponseDTO updateCorpTrustMatrix(IXDMRequestDTO requestDTO);

    IXDMResponseDTO deleteCorpTrustMatrix(IXDMRequestDTO requestDTO);

    IXDMResponseDTO getUserprofileidsByProfileMdns(IXDMRequestDTO requestDTO);

    IXDMResponseDTO getSubscrClientSettings(IXDMRequestDTO requestDTO) ;

    /**
     * method to perform the updation of the subscriber Client recording settings
     *
     * @param requestDTO IXDMRequestDTO
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO setSubscrClientSettings(KnMessage message);

    public IXDMResponseDTO getMdnAuthorizationForGroupId(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO modifyGroupsUGWConfig(KnMessage message);

    /**
     * method to perform the updation of the getCorporateFS
     *
     * @param requestDTO IXDMRequestDTO
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO getCorporateFS(IXDMRequestDTO requestDTO);

    /**
     * method to perform the updation of the CorporateFS
     *
     * @param requestDTO IXDMRequestDTO
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO updateCorporateFS(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO getGroupsDetailsWithoutMembers(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO cloneContactsGroupsAndFeatures(KnMessage message);

    public IXDMResponseDTO setCATAccessPermission(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO getExtGWProfileList(IXDMRequestDTO requestDTO);


    IXDMResponseDTO createHierarchy(IXDMRequestDTO requestDTO);

    IXDMResponseDTO modifyHierarchy(IXDMRequestDTO requestDTO);


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