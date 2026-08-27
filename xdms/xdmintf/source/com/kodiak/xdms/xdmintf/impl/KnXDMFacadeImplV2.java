/*
 * **************************************************************************************************
 *  * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 *  * All Rights Reserved                                                                             *
 *  * Motorola Solutions Confidential Restricted                                                      *
 *  *************************************************************************************************
 */
package com.kodiak.xdms.xdmintf.impl;

import com.kodiak.common.commdto.request.IXDMRequestDTO;
import com.kodiak.common.commdto.response.IXDMResponseDTO;
import com.kodiak.frameworks.messaging.common.dto.KnMessage;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.processinvoker.IProcessInvokerIntf;
import com.kodiak.utilities.processinvoker.KnProcessInvokerException;
import com.kodiak.utilities.processinvoker.impl.KnProcessInvokerImpl;
import com.kodiak.xdms.mediator.IXDMMediatorIntf;
import com.kodiak.xdms.mediator.impl.KnXDMMediatorV2;
import com.kodiak.xdms.xdmintf.IXDMFacadeIntf;

/**
 * Facade Layer Impl class for the all the API's provided by the XDM Server
 * The request is received from the call back mechanism of the Messaging FW.
 * After receiving the request it is sent to the mediation layer for
 * further processing of the Operation.
 */
public class KnXDMFacadeImplV2 implements IXDMFacadeIntf {
    private static final KnLogger knLogger = KnLogger.getLogger(KnXDMFacadeImplV2.class);

    private IXDMMediatorIntf xdmMediator;
    private IProcessInvokerIntf processInvokerIntf;


    public KnXDMFacadeImplV2() {
        xdmMediator = KnXDMMediatorV2.getInstance();
        try {
            processInvokerIntf = KnProcessInvokerImpl.getInstance();
            knLogger.debug("Constructor - ", "processInvokerIntf - ", processInvokerIntf);
        } catch (KnProcessInvokerException e) {
            knLogger.warn("KnXDMFacadeImpl()", "Failed to get the Process Invoker instance");
        }
    }

    @Override
    public IXDMResponseDTO createSubscriber(IXDMRequestDTO requestDTO) {
        return xdmMediator.createSubscriber(requestDTO);
    }

    @Override
    public IXDMResponseDTO updateSubscriber(KnMessage message) {
        String methodName = "updateSubscriber(KnMessage)";
        return xdmMediator.updateSubscriber(message);
    }

    @Override
    public IXDMResponseDTO getSubscriberDetails(IXDMRequestDTO requestDTO) {
        return null;
    }

    public IXDMResponseDTO deleteSubscriber(KnMessage message) {
        String methodName = "deleteSubscriber(KnMessage)";
        return xdmMediator.deleteSubscriber(message);
    }

    @Override
    public IXDMResponseDTO changeServiceAuthStatus(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO activateSubscriber(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO getSubscriberConfigDocument(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO changeMdn(KnMessage message) {
        return xdmMediator.changeMdn(message);
    }

    @Override
    public IXDMResponseDTO forceSync(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO updateSubscriberName(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO updateCampedGroup(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO getDefaultSubscriberProfile(IXDMRequestDTO message) {
        return null;
    }

    @Override
    public void addContacts(KnMessage message) {

    }

    @Override
    public void deleteContacts(KnMessage message) {

    }

    @Override
    public void modifyContacts(KnMessage message) {

    }

    @Override
    public void getContactList(KnMessage message) {

    }

    @Override
    public void createGroup(KnMessage message) {

    }

    @Override
    public void modifyGroupName(KnMessage message) {

    }

    @Override
    public void modifyGroupMember(KnMessage message) {

    }

    @Override
    public void deleteGroupMember(KnMessage message) {

    }

    @Override
    public void addGroupMember(KnMessage message) {

    }

    @Override
    public void deleteGroup(KnMessage message) {

    }

    @Override
    public void getGroupDetails(KnMessage message) {

    }

    @Override
    public void getGroupDocDetails(KnMessage message) {

    }

    @Override
    public IXDMResponseDTO getPubGroupDetails(KnMessage message) {
        return null;
    }

    @Override
    public void getAllContactList(KnMessage message) {

    }

    @Override
    public void getDirectory(KnMessage message) {

    }

    @Override
    public void getRLSDoc(KnMessage message) {

    }

    @Override
    public void getGroupList(KnMessage message) {

    }

    @Override
    public IXDMResponseDTO getPubGroupList(KnMessage message) {
        return null;
    }

    @Override
    public void isAlive(KnMessage message) {

    }

    @Override
    public IXDMResponseDTO unKnownOperation(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO customProvOp(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO updateClientFS1(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO updateUserAgent(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO createLicensePack(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO getLicensePackProfile(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO deleteLicensePack(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO changePAMServiceAuthStatus(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO changeBillingNumber(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO upgradeLicensePack(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO getPAMAccountMDNsDetails(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO updateLicensePack(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO downgradeLicensePack(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO modifySubscCorpFeature(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO modifySubscriberScanList(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO getSubscriberScanList(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO deleteExternalSubscriber(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO updateAutoPairing(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO createTPUser(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO updateTPUser(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO deleteTPUser(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO generateTPActivationCode(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getSysConfig(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public void modifyDynamicContacts(KnMessage message) {

    }

    @Override
    public void deleteDynamicContacts(KnMessage msgObj) {

    }

    @Override
    public void getDynamicContacts(KnMessage msgObj) {

    }

    @Override
    public void getDynamicNonSharedGrpDetails(KnMessage msgObj) {

    }

    @Override
    public void getDynamicNonSharedGrpList(KnMessage msgObj) {

    }

    @Override
    public void deleteDynamicNonSharedGrp(KnMessage message) {

    }

    @Override
    public void createNonSharedGroup(KnMessage msgObj) {

    }

    @Override
    public void modifyNonSharedGroup(KnMessage msgObj) {

    }

    @Override
    public IXDMResponseDTO getAuthorizationList(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO updateAuthorizationList(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO getGroupUsageListDoc(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getEmergencyConfigDoc(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getPubContactList(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO deviceActivation(IXDMRequestDTO inputDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO radioDeviceActivation(IXDMRequestDTO inputDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO userLogin(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO upgradeOrDowngradLicensePack(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO getSubsEmergencyDetails(IXDMRequestDTO inputDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getSubscriberCorpGroupList(IXDMRequestDTO inputDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getTGSSList(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO updateTGSSList(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO deleteTGSSList(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO searchCorpAddressBook(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO updateMCSIds(IXDMRequestDTO requestDTO) {
        String methodName = "updateMCSIds(IXDMRequestDTO)";
        return xdmMediator.updateMCSIds(requestDTO);
    }

    @Override
    public IXDMResponseDTO updateUserId(IXDMRequestDTO requestDTO) {
        String methodName = "updateUserId(IXDMRequestDTO)";
        return xdmMediator.updateUserId(requestDTO);
    }

    @Override
    public IXDMResponseDTO updateLicensePackSubsMCSIds(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO updateLicensePackSubsUserId(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO updatePrivacyOptStatus(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO getMCPTTUEConfig(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getMCPTTUserProfile(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO getMCPTTServiceConfig(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getMCDATAUEConfig(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getMCDATAUserProfile(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO getMCDATAServiceConfig(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getMCVideoUEConfig(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getMCVideoUserProfile(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO getMCVideoServiceConfig(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getMCSGroupDoc(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getMdnProfileIdsForMcPttIds(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getMCSUserDir(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO selectProfileMdn(IXDMRequestDTO inputDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO createDevice(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getDeviceInfo(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO deleteDeviceInfo(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO modifyDevice(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO loginNotifyEvent(IXDMRequestDTO inputDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getSubsGroupMembershipDetails(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getAuthorizedUserList(IXDMRequestDTO inputDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getAsyncOpStatus(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getUserprofileidsByProfileMdns(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getSubscrClientSettings(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO setSubscrClientSettings(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO getMdnAuthorizationForGroupId(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO modifyGroupsUGWConfig(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO getGroupsUGWConfig(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO assignCommonContactList(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO unAssignCommonContactList(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO getDeviceList(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getDeviceDetails(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getGroupStats(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getSubscriberStats(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getDeviceStats(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getCorporateFS(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO updateCorporateFS(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO createCorpAccount(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO deleteCorpAccount(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getCorporateAccountDetails(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getCorporateAccountsList(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO updateCorpAccount(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getGroupsDetailsWithoutMembers(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO setCATAccessPermission(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getExtGWProfileList(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO authenticate(IXDMRequestDTO authRequestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getCorpMasterList(IXDMRequestDTO contactRequestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getCorpSubscContactList(IXDMRequestDTO contactRequestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO modifyCorpSubscContacts(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO pushSublists(IXDMRequestDTO sublistRequestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO removeSublist(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO addCorpContacts(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO modifyCorpContacts(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO removeCorpContacts(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO getCorpContactDetails(IXDMRequestDTO conactRequestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO createSublist(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO modifySublist(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO deleteSublist(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO getSublistDetails(IXDMRequestDTO sublistReqDto) {
        return null;
    }

    @Override
    public IXDMResponseDTO getAllSublist(IXDMRequestDTO corpInfoDto) {
        return null;
    }

    @Override
    public IXDMResponseDTO getDistributionList(IXDMRequestDTO distributionInfoDto) {
        return null;
    }

    @Override
    public IXDMResponseDTO createCorpGroup(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO modifyCorpGroup(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO deleteCorpGroup(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO getCorpGroupDetails(IXDMRequestDTO groupInfoDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getCorpGroupList(IXDMRequestDTO corpInfo) {
        return null;
    }

    @Override
    public IXDMResponseDTO getCorpGroupDetailsList(IXDMRequestDTO corpGroupListInfo) {
        return null;
    }

    @Override
    public IXDMResponseDTO getCorpSubscriberGroupList(IXDMRequestDTO poccorpInfo) {
        return null;
    }

    @Override
    public IXDMResponseDTO getPocLinkedGroupList(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getSubscriberEmailId(IXDMRequestDTO activationCodeInfo) {
        return null;
    }

    @Override
    public IXDMResponseDTO saveActivationCode(IXDMRequestDTO activationCodeInfo) {
        return null;
    }

    @Override
    public IXDMResponseDTO generateActivationCodes(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO getMailInfo(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO sendActivationMail(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO sendMail(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO customCorpOperation(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO updateScanList(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getScanList(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO deleteScanList(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getAllBillingMdns(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getLicenseSubs(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO markSubsForDeletion(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO updateBillingName(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO licenseAuthenticate(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getSubscrReverseContacts(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getSubscrSublists(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO removeSubscribersContacts(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO removeSubscribersAllSublist(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO removeSubscribersAllGroups(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO updateCorpAdminFS(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO getAllCorpSubscrFeatureSets(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getActivationCode(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getCorpSubscriberDetails(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getCorpExtContactDetails(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getCorporateProfile(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO switchConvergedClient(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO getSubscrActivationCode(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO generateOTP(IXDMRequestDTO clientActReqDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO validateOTP(IXDMRequestDTO clientActReqDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getCorpBanFanList(IXDMRequestDTO clientActReqDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO updateCorpSubscriber(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO generateActivationCodeIDMIntf(IXDMRequestDTO clientActReqDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getLITargetInfo(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getCorpSubsUserProfile(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO resetCorpSubsUserPassword(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO resendCorpSubsVerificationEmail(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO setTargetPermissions(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO getTargetPermissions(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO getAuthorizedMdnList(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO setSubsEmergencyAttributes(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO getSubsEmergencyAttributes(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO updateSubsAliasEntities(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO generateTempPassword(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO getUserEmergDest(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO modifySubscriberTGList(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO getSubscriberTGList(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO deleteTGList(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO getUserProfile(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO sendSMS(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO sendTempPassword(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO addBulkGroupsToSubscriber(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO createSubsATGScanList(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO createOSMList(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO updateOSMList(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO deleteOSMList(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO getOSMList(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getOSMListDetails(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO assignOSMIdToGroup(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO getOSMGroupList(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getCorpProfileByEntities(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO updateCorpSubscriberMCSIds(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO getPoCConfig(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getMobileSyncLocSupervisors(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO createUserProfile(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO updateUserProfile(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO deleteUserProfile(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getUserProfileDetails(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getUserProfileList(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getUserProfileListByName(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getSubscriberUserProfileList(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO assignUserProfile(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO unassignUserProfile(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO getUserProfileSubscriberList(IXDMRequestDTO inputDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO updateDefaultprofile(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO createBulkCorpGroup(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getProfileGroupList(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO createGroupProfile(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getGroupProfileList(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getGroupProfileDetails(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO searchGroupProfile(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO createCorpGroupWithProfile(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO modifyGroupProfile(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO deleteBulkCorpGroup(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO deleteGroupProfile(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getSharedCorpTrustMatrix(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO updateCorpTrustMatrix(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO deleteCorpTrustMatrix(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO sendTrkMaterial(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO modifyBulkGroupProperties(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO cloneContactsGroupsAndFeatures(KnMessage message) {
        return null;
    }


    @Override
    public IXDMResponseDTO deleteHierarchy(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO createHierarchy(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO modifyHierarchy(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO allocateSubs(IXDMRequestDTO requestDTO) {
        return null;
    }

    public IXDMResponseDTO unAllocateSubs(IXDMRequestDTO requestDTO) {
        return null;
    }


    @Override
    public IXDMResponseDTO createPTTSettingDoc(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getPTTSettingDocList(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getPTTSettingDoc(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO setDefaultPttSettingDoc(IXDMRequestDTO requestDTO) {return null; }

    @Override
    public IXDMResponseDTO deletePTTSettingDoc(IXDMRequestDTO requestDTO) {return null; }

    @Override
    public IXDMResponseDTO assignPttSettingToHierarchy(IXDMRequestDTO requestDTO) {return null; }
    @Override
    public IXDMResponseDTO unassignPttSettingToHierarchy(IXDMRequestDTO requestDTO) {return null; }

    @Override
    public IXDMResponseDTO assignPttSettingDocToMdns(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO unassignPttSettingDocToMdns(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getPttSettingDocMdnList(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO getMDNCountForPttSettingDocID(IXDMRequestDTO requestDTO) {
        return null;
    }

    @Override
    public IXDMResponseDTO assignPttSettingToCorp(IXDMRequestDTO requestDTO) {return null; }
    @Override
    public IXDMResponseDTO unassignPttSettingToCorp(IXDMRequestDTO requestDTO) {return null; }

    @Override
    public IXDMResponseDTO groupRehome(KnMessage message) {
        return null;
    }

    @Override
    public IXDMResponseDTO modifyPTTSettingTemplate(IXDMRequestDTO requestDTO) {
        return null;
    }


    @Override
    public IXDMResponseDTO getRegions(IXDMRequestDTO requestDTO) {
        return null;
    }
}