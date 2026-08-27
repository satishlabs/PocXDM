/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  IXDMCorpMediatorIntf.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Jan 28, 2011      7.0
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
package com.kodiak.xdms.mediator;

import com.kodiak.common.commdto.request.IXDMRequestDTO;
import com.kodiak.common.commdto.response.IXDMResponseDTO;
import com.kodiak.frameworks.messaging.common.dto.KnMessage;

public interface IXDMCorpMediatorIntf {

//Generic APIs//

    public IXDMResponseDTO authenticate(IXDMRequestDTO authRequestDTO);

    //Corp Contact Management APIs//

    public IXDMResponseDTO getCorpMasterList(IXDMRequestDTO contactRequestDTO);

    public IXDMResponseDTO getCorpSubscContactList(IXDMRequestDTO contactRequestDTO);

    public IXDMResponseDTO modifyCorpSubscContacts(KnMessage message);

    public IXDMResponseDTO pushSublists(IXDMRequestDTO sublistRequestDTO);

    public IXDMResponseDTO removeSublist(KnMessage message);

    public IXDMResponseDTO addCorpContacts(KnMessage message);

    public IXDMResponseDTO modifyCorpContacts(KnMessage message);

    public IXDMResponseDTO removeCorpContacts(KnMessage message);

    public IXDMResponseDTO getCorpContactDetails(IXDMRequestDTO conactRequestDTO);

//    public IXDMResponseDTO getDistributionList(IXDMRequestDTO contactRequestDTO);

    //Corp Sublist Management APIs//

    public IXDMResponseDTO createSublist(KnMessage message);

    public IXDMResponseDTO modifySublist(KnMessage message);

    public IXDMResponseDTO deleteSublist(KnMessage message);

    public IXDMResponseDTO getSublistDetails(IXDMRequestDTO sublistReqDto);

    public IXDMResponseDTO getAllSublist(IXDMRequestDTO corpInfoDto);

    public IXDMResponseDTO getDistributionList(IXDMRequestDTO distributionInfoDto);

    //Corp Group Management APIs//

    public IXDMResponseDTO createCorpGroup(KnMessage message, Boolean isWithProfileReq);

    public IXDMResponseDTO modifyCorpGroup(KnMessage message);

    public IXDMResponseDTO deleteCorpGroup(KnMessage message);

    public IXDMResponseDTO getCorpGroupDetails(IXDMRequestDTO groupInfoDTO);

    public IXDMResponseDTO getCorpGroupList(IXDMRequestDTO corpInfo);

    public IXDMResponseDTO getCorpSubscriberGroupList(IXDMRequestDTO subscriberCorpInfo);

    /**
     * This method is used to get the poc corporate Group MDNs group list
     * @param requestDTO
     * @return
     */
    public IXDMResponseDTO getPocLinkedGroupList(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO getSubscriberEmailId(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO saveClientActivationMail(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO generateActivationCodes(KnMessage message);

    public IXDMResponseDTO getMailInfo(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO sendActivationMail(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO sendMail(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO modifySubscCorpFeature(IXDMRequestDTO requestDTO);
    
    public IXDMResponseDTO modifySubscriberScanList(KnMessage message);
    
    public IXDMResponseDTO getSubscriberScanList(IXDMRequestDTO requestDTO);
    
    //PAM Subs Management APIs//

    public IXDMResponseDTO getAllBillingMdns(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO getLicenseSubs(IXDMRequestDTO requestDTO);
    
    public IXDMResponseDTO updateBillingName(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO markSubsForDeletion(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO licenseAuthenticate(IXDMRequestDTO requestDTO);

    //Reverse Contact APIs

    public IXDMResponseDTO getSubscrReverseContacts(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO getSubscrSublists(IXDMRequestDTO requestDTO);

    //Wipe Out APIs

    public IXDMResponseDTO removeSubscribersContacts(KnMessage message);

    public IXDMResponseDTO removeSubscribersAllSublist(KnMessage message);

    public IXDMResponseDTO removeSubscribersAllGroups(KnMessage message);

    public IXDMResponseDTO getAllCorpSubscrFeatureSets(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO updateCorpAdminFS(KnMessage message);

    public IXDMResponseDTO getActivationCode(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO getCorpSubscriberDetails(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO getCorpExtContactDetails(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO generateTPActivationCode(IXDMRequestDTO requestDTO) ;

    /**
     * Method to get the subscribers activation code.
     *
     * @param requestDTO IXDMRequestDTO
     * @return IXDMResponse DTO object
     */
    public IXDMResponseDTO getSubscrActivationCode(IXDMRequestDTO requestDTO);
    /**
     * Method to generate the OTP for the subscriber.
     *
     * @param requestDTO IXDMRequestDTO
     * @return IXDMResponse DTO object
     */
    public IXDMResponseDTO generateOTP(IXDMRequestDTO requestDTO);

    /**
     * This method is used to validate the OTP against the subscribers.
     * @param requestDTO
     * @return
     */
    public IXDMResponseDTO validateOTP(IXDMRequestDTO requestDTO);

    /**
     * This method to get the ban fan list for the corporate
     * @param requestDTO
     * @return
     */
    public IXDMResponseDTO getCorpBanFanList(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO getCorporateProfile(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO switchConvergedClient(KnMessage message);

    public IXDMResponseDTO updateCorpSubscriber(KnMessage message);

    public IXDMResponseDTO generateActivationCodeIDMIntf(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO getLITargetInfo(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO getCorpSubsUserProfile(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO resetCorpSubsUserPassword(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO resendCorpSubsVerificationEmail(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO setTargetPermissions(KnMessage message);

    public IXDMResponseDTO getTargetPermissions(KnMessage message);

    public IXDMResponseDTO getAuthorizedMdnList(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO setSubsEmergencyAttributes(KnMessage message);

    public IXDMResponseDTO getSubsEmergencyAttributes(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO updateSubsAliasEntities(KnMessage message);

    public IXDMResponseDTO generateTempPassword(KnMessage message);

    public IXDMResponseDTO getUserEmergDest(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO modifySubscriberTGList(KnMessage message);

    public IXDMResponseDTO getSubscriberTGList(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO deleteTGList(KnMessage message);

    public IXDMResponseDTO getUserProfile(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO sendSMS(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO sendTempPassword(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO addBulkGroupsToSubscriber(KnMessage message);

    public IXDMResponseDTO createSubsATGScanList(KnMessage message);

    public IXDMResponseDTO createOSMList(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO updateOSMList(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO deleteOSMList(KnMessage message);

    public IXDMResponseDTO getOSMList(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO getOSMListDetails(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO assignOSMIdToGroup(KnMessage message);

    public IXDMResponseDTO getOSMGroupList(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO getCorpProfileByEntities(IXDMRequestDTO authRequestDTO);

    public IXDMResponseDTO updateCorpSubscriberMCSIds(KnMessage message);

    public IXDMResponseDTO getPoCConfig(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO getMobileSyncLocSupervisors(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO createUserProfile(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO updateUserProfile(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO deleteUserProfile(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO getUserProfileDetails(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO getUserProfileList(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO getUserProfileListByName(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO getSubscriberUserProfileList(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO assignUserProfile(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO unassignUserProfile(KnMessage message);

    public IXDMResponseDTO getUserProfileSubscriberList(IXDMRequestDTO inputDTO);

    public IXDMResponseDTO updateDefaultprofile(IXDMRequestDTO requestDTO);

    IXDMResponseDTO sendTrkMaterial(IXDMRequestDTO requestDTO);

    IXDMResponseDTO getAsyncOpStatus(IXDMRequestDTO requestDTO);

    IXDMResponseDTO getGroupsUGWConfig(IXDMRequestDTO requestDTO);

    IXDMResponseDTO assignCommonContactList(KnMessage message);

    IXDMResponseDTO unAssignCommonContactList(KnMessage message);

    IXDMResponseDTO getDeviceDetails(IXDMRequestDTO requestDTO);

    IXDMResponseDTO getDeviceList(IXDMRequestDTO requestDTO);

    IXDMResponseDTO getGroupStats(IXDMRequestDTO requestDTO);

    IXDMResponseDTO getSubscriberStats(IXDMRequestDTO requestDTO);

    IXDMResponseDTO getDeviceStats(IXDMRequestDTO sublistReqDto);

    public IXDMResponseDTO modifyBulkGroupProperties(IXDMRequestDTO groupInfoDTO);

    public IXDMResponseDTO createCorpAccount(IXDMRequestDTO requestDTO);
    public IXDMResponseDTO updateCorpAccount(IXDMRequestDTO requestDTO);
    public IXDMResponseDTO deleteCorpAccount(IXDMRequestDTO requestDTO);
    public IXDMResponseDTO getCorporateAccountDetails(IXDMRequestDTO requestDTO);
    public IXDMResponseDTO getCorporateAccountsList(IXDMRequestDTO requestDTO);

    IXDMResponseDTO deleteHierarchy(IXDMRequestDTO requestDTO);
    public IXDMResponseDTO allocateSubs(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO unAllocateSubs(IXDMRequestDTO requestDTO);

    public IXDMResponseDTO groupRehome(KnMessage message);

    public IXDMResponseDTO getRegions(IXDMRequestDTO requestDTO);

}
