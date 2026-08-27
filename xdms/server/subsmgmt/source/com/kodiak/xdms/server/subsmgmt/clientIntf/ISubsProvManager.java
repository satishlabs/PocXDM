/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *****************************************************************************
 * File name:   ISubsProvManager.java
 * Subsystem:
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ravi Shanker .P       12/25/10       7.0
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
 * *******************************************************************************
 */

package com.kodiak.xdms.server.subsmgmt.clientIntf;

import com.kodiak.common.commdto.common.KnXDMDeviceProvDTO;
import com.kodiak.common.commdto.request.*;
import com.kodiak.common.commdto.response.*;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.exception.KnException;
import com.kodiak.common.resources.KnConstants.HIERARCHY_TYPE;
import com.kodiak.xdms.server.common.dto.clientdat.KnIPChangeMDNInfoDTO;
import com.kodiak.xdms.server.common.framework.KnFWException;
import com.kodiak.xdms.server.subsmgmt.KnProvException;
import com.kodiak.xdms.server.subsmgmt.business.KnProvBOException;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.*;
import com.kodiak.xdms.server.subsmgmt.dto.persistdat.KnSubsProfilePersistDTO;

import java.util.List;
import java.util.Map;

public interface ISubsProvManager {

    public KnOPCreateSubsInfoDTO createSubscriber(KnIPSubsProvInfoDTO subsProvInfoInputDTO,
                                                  KnPersisterTxn persisterTxn) throws KnProvException, KnFWException;

    public KnOPUpdateSubsInfoDTO updateSubscriber(KnIPSubsProvInfoDTO subsProfileInputDTO,
                                                  KnPersisterTxn persisterTxn) throws KnProvException, KnFWException;
    public KnOPUpdateSubsInfoDTO updateSubscriberFSAndPkgCodes(KnIPSubsProvInfoDTO subsProfileInputDTO,
            KnPersisterTxn persisterTxn) throws KnProvException, KnFWException;

    public KnOPUpdateSubsInfoDTO updateSubscriberUserAgent(KnIPSubsProvInfoDTO subsProfileInputDTO,
                                                           KnPersisterTxn persisterTxn) throws KnProvException, KnFWException;


    public KnOPDeleteSubsRespDTO deleteSubscriber(KnIPSubscriberInfoDTO subscriberDTO,
                                                  KnPersisterTxn persisterTxn) throws KnProvException;

    public KnOPSubsProfileInfoDTO getSubscriberDetails(KnIPSubscriberInfoDTO subscriberDTO,
                                                       KnPersisterTxn persisterTxn) throws KnProvException;

    public KnOPSubsProfileInfoDTO getSubscriberDetailsBasic(KnIPSubscriberInfoDTO subscriberDTO,
                                                            KnPersisterTxn persisterTxn) throws KnProvException;

    public KnOPSubsProfileInfoDTO getSubscriberIfExist(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException, KnProvException;

    public KnOPSubsProfileInfoDTO getSubsDetails(KnIPSubscriberInfoDTO subscriberDTO, boolean readOnly,
                                                 KnPersisterTxn persisterTxn) throws KnProvException;

    public KnOPSubsProfileInfoDTO changeMDN(KnIPChangeMDNInfoDTO changeMDNInfoDTO,
                                            boolean isAsyncCall, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException;

    public KnOPSubsProfileInfoDTO forceSync(KnIPSubscriberInfoDTO subscriberInfoDTO,
                                            KnPersisterTxn persisterTxn) throws KnProvException, KnFWException;

    public KnOPActivationInfoDTO activateSubscriber(KnIPActivateMDNInfoDTO activateMdnInfoDTO,
                                                    KnPersisterTxn persisterTxn) throws KnProvException, KnFWException;

    public KnOPActivationInfoDTO selectProfileMdn(KnIPSelectProfileMdnDTO selectProfileMdnDTO,
                                                  KnPersisterTxn persisterTxn) throws KnProvException, KnFWException;

    public KnOPProvDTO sendConfigDocNotification(String mdn, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException;

    public KnOPSubsConfigDocInfoDTO getSubscriberConfigDocument(KnIPSubscriberInfoDTO subscriberDTO,
                                                                KnPersisterTxn persisterTxn) throws KnProvException, KnFWException;

    public KnOPChgAuthStatusRespDTO changeServiceAuthStatus(KnIPSubsProvInfoDTO subsProvInfoDTO,
                                                            KnPersisterTxn persisterTxn) throws KnProvException, KnFWException;

    public KnOPProvDTO removeSubsInfo(KnIPChangeMDNInfoDTO changeMDNInfoDTO,
                                      KnPersisterTxn persisterTxn) throws KnProvException;

    public KnOPSubsProfileInfoDTO getDefaultSubscriberProfile(KnIPSubscriberInfoDTO subscriberInfoDTO,
                                                              KnPersisterTxn persisterTxn) throws KnProvException;

    public KnOPProvDTO deleteCorporateProfile(KnIPCorpProfileInfoDTO corpProfileInfoDTO, KnPersisterTxn persisterTxn)
            throws KnProvException;

    public KnOPProvDTO updateExtCorporateID(KnIPCorpProfileInfoDTO corpProfileInfoDTO, KnPersisterTxn persisterTxn)
            throws KnProvException;

    public KnOPCreatePAMAccountDTO createPAMAccount(KnIPPAMAccInfoDTO pamAccInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException;

    public void validateUpgradePAMAccount(int maxSub, KnPersisterTxn persisterTxn) throws KnProvException;

    public KnOPDeletePAMAccountDTO deletePAMAccount(KnIPPAMAccInfoDTO pamAccInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException;

    public KnOPProvDTO createPAMSubsProfile(KnIPPAMAccInfoDTO pamAccInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException;

    public KnOPProvDTO updatePAMSubsProfile(KnIPPAMAccInfoDTO pamAccInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException;

    public KnOPProvDTO deletePAMSubsProfile(KnIPPAMAccInfoDTO pamAccInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException;

    public KnOPPAMAccInfoDTO getPAMSubsProfile(KnIPPAMAccInfoDTO pamAccInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException;

    public KnOPPAMAccInfoDTO retrievePAMSubsProfInfo(KnIPPAMAccInfoDTO pamAccInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException;

    public KnOPBulkDeleteSubsRespDTO deleteSubscribers(KnIPBulkSubsProvInfoDTO bulkSubsProvInfoDTO, boolean isLastMDN, KnPersisterTxn persisterTxn) throws KnProvException;

    public KnOPUpdateSubsInfoDTO updateSubscribers(KnIPBulkSubsProvInfoDTO bulkSubsProvInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException;

    public KnOPCreateSubsInfoDTO createSubscribers(KnIPBulkSubsProvInfoDTO bulkSubsProvInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException;

    public KnOPBulkChgAuthStatusRespDTO changeServiceAuthStatuses(KnIPBulkSubsProvInfoDTO bulkSubsProvInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException;

    public List<String> retrievePAMAccountMDNs(int pamAccId, KnPersisterTxn persisterTxn) throws KnProvException;

    public List<String> getPAMAccountMdnsByInsertionTime(int pamAccId, long insertionTime, KnPersisterTxn persisterTxn) throws KnProvException;

    public List<KnOPSubsProfileInfoDTO> retrievePAMAccountMDNsDetails(int pamAccId, KnPersisterTxn persisterTxn) throws KnProvException;

    public List<String> retrievePAMAccountMDNs(int pamAccId, int fetchSize, String listMdn, KnPersisterTxn persisterTxn) throws KnProvException;

    public List<String> retrievePAMAccountMDNs(int pamAccId, String startMdn, String endMdn, int fetchSize, KnPersisterTxn persisterTxn) throws KnProvException;

    public KnOPUpdatePAMAccountDTO updatePAMAccState(KnIPPAMAccInfoDTO pamAccInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException;

    public KnOPCreateSubsInfoDTO validateCreateSubscriber(KnIPBulkSubsProvInfoDTO bulkSubsProvInfoDTO, int subsCount,
                                                          KnPersisterTxn persisterTxn) throws KnProvException, KnFWException;

    public KnOPPAMAccInfoDTO getPAMAccountInfo(String extPamAccid, KnPersisterTxn persisterTxn) throws KnProvException;

    public KnOPPAMAccInfoDTO getPAMAccInfoFromId(int pamAccid, boolean readOnly, KnPersisterTxn persisterTxn) throws KnProvException;

    public KnOPUpdatePAMAccountDTO updateCorpName(String extCorpId, String corpName, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException;

    public KnOPUpdateSubsInfoDTO validateUpdateSubscriber(KnIPBulkSubsProvInfoDTO bulkSubsProvInfoDTO,
                                                          KnPersisterTxn persisterTxn) throws KnProvException, KnFWException;

    public KnOPUpdatePAMAccountDTO updatePAMAccMaxSub(KnIPPAMAccInfoDTO pamAccInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException;

    public KnOPUpdatePAMAccountDTO updatePAMAccName(KnIPPAMAccInfoDTO pamAccInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException;

    public List<String> retrievePAMAccountProvMDNs(int pamAccId, KnPersisterTxn persisterTxn) throws KnProvException;

    public List<String> getPamAccLastSequenceMdns(int pamAccId, int start, int end, KnPersisterTxn persisterTxn) throws KnProvException;

    public KnOPBulkRespDTO updateDispForSubscribers(Map<String, Integer> subscribers, int bitNo, KnPersisterTxn persisterTxn) throws KnProvException;

    public KnOPProvDTO createExtSubscriber(KnExtSubscriberInfoDTO extSubsInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException;

    public KnOPProvDTO deleteExtSubscriber(KnExtSubscriberInfoDTO extSubsInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException;

    public KnOPProvDTO updateExtSubscriber(KnExtSubscriberInfoDTO extSubsInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException;

    public KnExtSubscriberInfoDTO getExtSubscriberInfo(KnExtSubscriberInfoDTO extSubsInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException;

    public List<Integer> getCorpIdsForExtSubscriber(String mdn, KnPersisterTxn persisterTxn) throws KnProvException;

    public KnOPProvDTO addOrModifyExtSubscribers(KnExtSubscriberInfoDTO extSubsInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException;

    public KnOPUpdatePAMAccountDTO migratePAMAccount(KnIPPAMAccInfoDTO pamAccInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException;

    public int retrieveCorporationId(String extCorpId, KnPersisterTxn persisterTxn) throws KnProvException;

    public KnOPUpdatePAMAccountDTO validateUpdatePamAccount(KnIPPAMAccInfoDTO pamAccInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException;

    public void updatePAMEtag(int pamAccID, KnPersisterTxn persisterTxn) throws KnProvException;

    public KnOPProvDTO createSubscrRoamingProfiles(String mdn, KnPersisterTxn persisterTxn) throws KnProvException;

    public KnOPProvDTO deleteSubscrRoamingProfiles(String mdn, KnPersisterTxn persisterTxn) throws KnProvException;

    public KnOPCorpProfileInfoDTO retrieveCorporateProfile(String extCorpId, KnPersisterTxn persisterTxn) throws KnProvException;

    public KnOPCorpProfileInfoDTO createCorpProfile(KnIPCorpProfileInfoDTO corpProfileInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException;

    public List<Integer> retrievePAMAccIdForCorpId(int corpId, KnPersisterTxn persisterTxn) throws KnProvException;

    public List<KnOPUpdateSubsInfoDTO> updateHierarchy(KnIPBulkSubsProvInfoDTO bulkSubsProvInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException;

    /* public List<KnOPProvDTO> sendBulkConfigDocNotification(List<String> mdnList,KnPersisterTxn persisterTxn)throws KnProvException;//TODO
 */
    public boolean validateCorpSubsLimitAndPairLimit(int corpId, int subsCount, KnPersisterTxn persisterTxn) throws KnProvException;

    public List<String> retrieveBanMDNs(int banId, HIERARCHY_TYPE hierarchyType, KnPersisterTxn persisterTxn) throws KnProvException;

    public KnOPProvDTO updatePAMSubsProfCorpId(KnIPBulkSubsProvInfoDTO bulkSubsProvInfoDTO, int oldCorpId, KnPersisterTxn persisterTxn) throws KnProvException;

    public KnOPProvDTO updateCorpProfileLastUpdateTime(int corpId, KnPersisterTxn persisterTxn) throws KnProvException;

    public KnOPProvDTO updateEtagForNNISubscr(String extCorpId, int clientType, KnPersisterTxn persisterTxn) throws KnProvException;

    public boolean isMdnExistsInPseudoPoolNPamAccInfo(String mdn, KnPersisterTxn persisterTxn) throws KnProvException;

    public KnOPUpdateSubsInfoDTO updateAutoPairing(KnIPSubsProvInfoDTO subsProfileInputDTO,  KnPersisterTxn persisterTxn) throws KnProvException;

    public KnOPSubsProfileInfoDTO createTPUser(KnTPUserInfoDTO tpUserInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException;

    public KnOPSubsProfileInfoDTO updateTPUser(KnTPUserInfoDTO tpUserInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException;

    public KnOPProvDTO deleteTPUser(KnTPUserInfoDTO tpUserInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException;

    public Map<String, String> fetchActiveFSForBulkMdns(List<String> mdns, KnPersisterTxn persisterTxn)throws KnProvException;

    public KnOPUpdateSubsInfoDTO switchConvergedClient(KnIPSubsProvInfoDTO subsProvInputDTO, KnPersisterTxn persisterTxn) throws KnProvException;

    public KnSysConfigRespDTO getSysConfig(KnIPSubsProvInfoDTO subsProvInfoInputDTO,
                                           KnPersisterTxn persisterTxn) throws KnProvException, KnFWException;

    public KnOPBulkRespDTO updateBulkSubsFSAndPkgIds(KnIPBulkSubsProvInfoDTO bulkSubsProvInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException ;

    public void verifyTPAccount(KnTPUserInfoDTO tpUserInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException;

    public KnOPProvDTO actionOnTGSSDoc(String mdn, String newActiveFS, String oldActiveFS,KnPersisterTxn persisterTxn) throws KnProvException;

    public KnOPProvDTO actionOnTGSSDocCust(String mdn, String newActiveFS, String oldActiveFS,KnPersisterTxn persisterTxn) throws KnProvException;
    
    public KnOPSubsProfileInfoDTO searchCorpAddressBook(KnIPSubscriberInfoDTO subscriberDTO,KnPersisterTxn persisterTxn) throws KnProvException;

    public KnOPSubsProfileInfoDTO updatePrivacyOptStatus(KnIPSubscriberInfoDTO subscriberDTO,KnPersisterTxn persisterTxn) throws KnProvException;
    
    public void updateMCSIds(KnPersisterTxn persisterTxn, KnSubsProfilePersistDTO subsProfilePersistDTO) throws KnDAOException, KnProvBOException, KnProvException;

    public KnXDMProfileIdMdnMapRespDTO getMdnProfileIdsForMcPttIds(KnIPSubscriberInfoDTO subscriberDTO, boolean readOnly,
                                                                   KnPersisterTxn persisterTxn) throws KnDAOException, KnProvException;
    public KnOPCreateSubsInfoDTO createDevice(KnXDMDeviceProvInfoDTO deviceProfileInfoInputDTO, KnPersisterTxn persisterTxn)throws KnDAOException,KnProvException;

    public KnXDMDeviceProvDTO getDeviceInfo(String deviceId, KnPersisterTxn persisterTxn)throws KnDAOException,KnProvException;

    public void deleteDeviceInfo(KnXDMDeviceProvDTO deviceId, KnPersisterTxn persisterTxn)throws KnDAOException,KnProvException;

    public KnOPUpdateSubsInfoDTO modifyDevice(KnXDMDeviceProvInfoDTO deviceProfileInfoInputDTO, KnPersisterTxn persisterTxn)throws KnDAOException,KnProvException;

    public String getMdnForUnassign(String baseMdn,String userProfileId, KnPersisterTxn persisterTxn) throws KnProvException;

    public KnOPCreateSubsInfoDTO createSubscriberForAssignUserProfile(KnIPSubsProvInfoDTO subsProvInfoInputDTO,
                                                                      KnPersisterTxn persisterTxn) throws KnProvException, KnFWException;
    public KnOPUpdateSubsInfoDTO updateSubscriberUserProfileFS(KnIPSubsProvInfoDTO subsProfileInputDTO,
                                                               KnPersisterTxn persisterTxn) throws KnProvException, KnFWException;

    public KnOPUpdateSubsInfoDTO updateSubscrTS(KnIPSubsProvInfoDTO subsProfileInputDTO,
                                                               KnPersisterTxn persisterTxn) throws KnProvException, KnFWException;
    public List<String> getMdnForUPM(String baseMdn, KnPersisterTxn persisterTxn) throws KnProvBOException, KnProvException;
    
	public void loginNotifyEvent(KnXDMLoginNotifyEventReqDTO loginNotifyEventReqDTO, KnPersisterTxn persisterTxn)
			throws KnProvException, KnFWException;

    public KnOPMCPTTPermissionDTO getAuthorizedUserList(KnIPMCPTTPermissionDTO tuInfo,
                                                  KnPersisterTxn persisterTxn) throws KnProvException, KnFWException;

    public KnOPSubsProfileInfoDTO getUserprofileidsByProfileMdns(KnIPSubscriberInfoDTO subscriberDTO,
                                                 KnPersisterTxn persisterTxn) throws KnProvException;

    public KnXDMSubsProfileRespDTO getSubscrClientSettings(KnIPSubsProvInfoDTO subscrClientSettings, KnPersisterTxn persisterTxn) throws KnProvException,KnFWException;

    public KnOPUpdateSubsInfoDTO setSubscrClientSettings(KnIPSubsProvInfoDTO subsProfileInputDTO,
                                                         KnPersisterTxn persisterTxn) throws KnProvException, KnFWException;

    public KnOPCreateSubsInfoDTO createCorpAccount(KnXDMCorpProfileInfoDTO corpProfileInfoDTO, KnPersisterTxn persisterTxn)
            throws KnDAOException, KnProvException;
    public KnOPCreateSubsInfoDTO updateCorpAccount(KnXDMCorpProfileInfoDTO corpProfileInfoDTO, KnPersisterTxn persisterTxn)
            throws KnDAOException, KnProvException;

    public KnOPDeleteSubsRespDTO deleteCorpAccount(KnXDMCorpInfoDTO corpProfileInfoDTO, KnPersisterTxn persisterTxn)
            throws KnDAOException, KnProvException;
    public KnCorporateProfilepersistDTO1 getCorporateAccountDetails(KnXDMDeviceProvInfoDTO xdmRequestDTO, KnPersisterTxn persisterTxn) throws KnException;
    public KnXDMCorpAccountsListDTO retrieveCorporationAccountsList(KnPersisterTxn persisterTxn,String fetchSize, String nextToken) throws KnDAOException, KnProvException;

    public String getBaseMdnByProfileMdn(String mdn, KnPersisterTxn persisterTxn) throws KnProvException;

    public KnUserLoginResponseDTO lockRequestErrorProcessor(KnOPSubsProfileInfoDTO subscriberDTO,KnXDMActivateInfoDTO activateInfoDTO,KnPersisterTxn persisterTxn) throws KnProvException;

    public Map<String, Integer> getSubscriberServiceAuthStatus(List<String> mdn, KnPersisterTxn persisterTxn) throws KnDAOException, KnProvException;
    public KnXDMExtGWProfileListDTO retrieveExtGWProfileList() throws KnDAOException, KnProvException;
    public int getSubscriberServiceAuthStatusByUserId(String UserId, KnPersisterTxn persisterTxn) throws KnDAOException, KnProvException;

}
