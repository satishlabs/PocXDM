/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *****************************************************************************
 * File name:   ISubsProvController.java
 * Subsystem:   Subscriber Provisioning Library
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
package com.kodiak.xdms.server.subsmgmt.business;

import com.kodiak.common.commdto.common.KnXDMDeviceProvDTO;
import com.kodiak.common.commdto.request.KnXDMCorpInfoDTO;
import com.kodiak.common.commdto.request.KnXDMCorpProfileInfoDTO;
import com.kodiak.common.commdto.request.KnXDMDeviceProvInfoDTO;
import com.kodiak.common.commdto.request.KnXDMLoginNotifyEventReqDTO;
import com.kodiak.common.commdto.response.*;
import com.kodiak.common.commdto.request.KnXDMActivateInfoDTO;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.exception.KnException;
import com.kodiak.xdms.server.common.dto.clientdat.KnIPChangeMDNInfoDTO;
import com.kodiak.xdms.server.common.framework.KnFWException;
import com.kodiak.xdms.server.subsmgmt.KnProvException;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.*;
import com.kodiak.xdms.server.subsmgmt.dto.persistdat.KnSubsProfilePersistDTO;

import java.util.List;
import java.util.Map;

public interface ISubsProvController {
    /**
     * method to create Subscriber Profile
     *
     * @param subsProfileInputDTO KnIPSubsProvInfoDTO
     * @param persisterTxn        KnPersisterTxn
     * @return KnOPCreateSubsInfoDTO
     * @throws KnProvBOException BO entity Exception
     * @throws KnFWException     Framework Exception
     */
    public KnOPCreateSubsInfoDTO createSubscriber(KnIPSubsProvInfoDTO subsProfileInputDTO, KnPersisterTxn persisterTxn) throws KnProvBOException, KnFWException;

    public KnOPUpdateSubsInfoDTO updateSubscriberFSAndPkgCodes(KnIPSubsProvInfoDTO subsProvInputDTO, KnPersisterTxn persisterTxn) throws KnProvBOException, KnFWException;

    /**
     * method to update the Subscriber Profile
     *
     * @param subsProfileInputDTO KnIPSubsProvInfoDTO
     * @param persisterTxn        KnPersisterTxn
     * @return KnOPCreateSubsInfoDTO
     * @throws KnProvBOException BO entity Exception
     * @throws KnFWException     Framework Exception
     */
    public KnOPUpdateSubsInfoDTO updateSubscriber(KnIPSubsProvInfoDTO subsProfileInputDTO, KnPersisterTxn persisterTxn) throws KnProvBOException, KnFWException;


    public KnOPUpdateSubsInfoDTO updateSubscriberUserAgent(KnIPSubsProvInfoDTO subsProfileInputDTO, KnPersisterTxn persisterTxn) throws KnProvBOException, KnFWException;

    /**
     * method to delete the Subscriber Profile
     *
     * @param subscriberDTO KnIPSubscriberInfoDTO
     * @param persisterTxn  KnPersisterTxn
     * @return KnOPProvDTO
     * @throws KnProvBOException BO entity Exception
     */
    public KnOPDeleteSubsRespDTO deleteSubscriber(KnIPSubscriberInfoDTO subscriberDTO, KnPersisterTxn persisterTxn) throws KnProvBOException;

    /**
     * method to get the Subscriber Profile
     *
     * @param subscriberDTO KnIPSubscriberInfoDTO
     * @param persisterTxn  KnPersisterTxn
     * @return KnOPSubsProfileInfoDTO
     * @throws KnProvBOException BO entity Exception
     */
    public KnOPSubsProfileInfoDTO getSubscriberDetails(KnIPSubscriberInfoDTO subscriberDTO, KnPersisterTxn persisterTxn) throws KnProvBOException;

    /**
     * method to get the Subscriber Profile
     *
     * @param subscriberDTO KnIPSubscriberInfoDTO
     * @param persisterTxn  KnPersisterTxn
     * @return KnOPSubsProfileInfoDTO
     * @throws KnProvBOException BO entity Exception
     */
    public KnOPSubsProfileInfoDTO getSubscriberDetailsBasic(KnIPSubscriberInfoDTO subscriberDTO, KnPersisterTxn persisterTxn) throws KnProvBOException;

   /**
     * method to get the Subscriber Profile
     *
     * @param subscriberDTO KnIPSubscriberInfoDTO
     * @param persisterTxn  KnPersisterTxn
     * @return KnOPSubsProfileInfoDTO
     * @throws KnProvBOException BO entity Exception
     */
   public KnOPSubsProfileInfoDTO getSubsDetails(KnIPSubscriberInfoDTO subscriberDTO, boolean readOnly, KnPersisterTxn persisterTxn) throws KnProvBOException;

    /**
     * method to perform migration of one MDN to another MDN
     *
     * @param changeMDNDTO KnIPChangeMDNInfoDTO
     * @param persisterTxn KnPersisterTxn
     * @return KnOPCreateSubsInfoDTO
     * @throws KnProvBOException BO entity Exception
     * @throws KnFWException     Framework Exception
     */
    public KnOPSubsProfileInfoDTO changeMdn(KnIPChangeMDNInfoDTO changeMDNDTO,boolean isAsyncCall, KnPersisterTxn persisterTxn) throws KnProvBOException, KnFWException;

    /**
     * method to force synv the Subscriber Profile
     *
     * @param subscriberDTO KnIPSubscriberInfoDTO
     * @param persisterTxn  KnPersisterTxn
     * @return KnOPSubsProfileInfoDTO
     * @throws KnProvBOException BO entity Exception
     * @throws KnFWException     Framework Exception
     */
    public KnOPSubsProfileInfoDTO forceSync(KnIPSubscriberInfoDTO subscriberDTO, KnPersisterTxn persisterTxn) throws KnProvBOException, KnFWException;

    /**
     * method to perform activation of the subscriber
     *
     * @param activateMdnInfoDTO KnIPActivateMDNInfoDTO
     * @param persisterTxn       KnPersisterTxn
     * @return KnOPActivationInfoDTO
     * @throws KnProvBOException BO entity Exception
     * @throws KnFWException     Framework Exception
     */
    public KnOPActivationInfoDTO activateSubscriber(KnIPActivateMDNInfoDTO activateMdnInfoDTO, KnPersisterTxn persisterTxn) throws KnProvBOException, KnFWException;

    public KnOPActivationInfoDTO selectProfileMdn(KnIPSelectProfileMdnDTO selectProfileMdnDTO, KnPersisterTxn persisterTxn) throws KnProvBOException, KnFWException;


    /**
     * method to get the Subscriber config Document
     *
     * @param subscriberDTO ISubscriberDTO
     * @param persisterTxn  KnPersisterTxn
     * @return KnOPSubsConfigDocInfoDTO
     * @throws KnProvBOException BO entity Exception
     */

    public KnOPSubsConfigDocInfoDTO getSubscriberConfigDoc(KnIPSubscriberInfoDTO subscriberDTO, KnPersisterTxn persisterTxn) throws KnProvBOException;

    /**
     * method to perform re-activation and de-activation of the subscriber
     *
     * @param subsProvInfoDTO KnIPSubsProvInfoDTO
     * @param persisterTxn    KnPersisterTxn
     * @return KnOPProvDTO
     * @throws KnProvBOException BO entity Exception
     * @throws KnFWException     Framework Exception
     */
    public KnOPChgAuthStatusRespDTO changeServiceAuthStatus(KnIPSubsProvInfoDTO subsProvInfoDTO, KnPersisterTxn persisterTxn) throws KnProvBOException, KnFWException;

    /**
     * method to remove the data from subscriber info table
     * will be used while the change MDN operation of the XDM Mediator
     *
     * @param changeMDNInfoDTO KnIPChangeMDNInfoDTO
     * @param persisterTxn     KnPersisterTxn
     * @return KnOPProvDTO
     * @throws KnProvBOException BO Entiry Exception
     */
    public KnOPProvDTO removeSubsInfo(KnIPChangeMDNInfoDTO changeMDNInfoDTO, KnPersisterTxn persisterTxn) throws KnProvBOException;

    /**
     * method to retrieve the Default Subscriber Profile
     *
     * @param subscriberInfoDTO KnIPSubscriberInfoDTO
     * @param persisterTxn      KnPersisterTxn
     * @return KnOPSubsProfileInfoDTO
     * @throws KnProvBOException BO Exception
     */
    public KnOPSubsProfileInfoDTO getDefaultSubscriberProfile(KnIPSubscriberInfoDTO subscriberInfoDTO, KnPersisterTxn persisterTxn)
            throws KnProvBOException;

    /**
     * method to delete the Corporate Profile
     *
     * @param corpProfileInfoDTO KnIPCorpProfileInfoDTO
     * @param persisterTxn       KnPersisterTxn
     * @return KnOPProvDTO
     * @throws KnProvBOException Prov BO Entity Exception
     */
    KnOPProvDTO deleteCorpProfile(KnIPCorpProfileInfoDTO corpProfileInfoDTO, KnPersisterTxn persisterTxn)
            throws KnProvBOException;

    /**
     * method to update Ext Corporate ID
     *
     * @param corpProfileInfoDTO KnIPCorpProfileInfoDTO
     * @param persisterTxn       KnPersisterTxn
     * @return KnOPProvDTO
     * @throws KnProvBOException Prov BO Entity Exception
     */
    KnOPProvDTO updateExtCorpID(KnIPCorpProfileInfoDTO corpProfileInfoDTO, KnPersisterTxn persisterTxn)
            throws KnProvBOException;

    /**
     * @param mdn
     * @param persisterTxn
     * @return
     * @throws KnProvBOException
     */
    KnOPProvDTO sendConfigDocNotification(String mdn, KnPersisterTxn persisterTxn) throws KnProvBOException;


    public KnOPProvDTO createExtSubscriber(KnExtSubscriberInfoDTO extSubsInfoDTO, KnPersisterTxn persisterTxn) throws KnProvBOException;

    public KnOPProvDTO deleteExtSubscriber(KnExtSubscriberInfoDTO extSubsInfoDTO, KnPersisterTxn persisterTxn) throws KnProvBOException;

    public KnOPProvDTO updateExtSubscriber(KnExtSubscriberInfoDTO extSubsInfoDTO, KnPersisterTxn persisterTxn) throws KnProvBOException;

    public KnExtSubscriberInfoDTO getExtSubscriberInfo(KnExtSubscriberInfoDTO extSubsInfoDTO, KnPersisterTxn persisterTxn) throws KnProvBOException;

    public List<Integer> getCorpIdsForExtSubscriber(String mdn, KnPersisterTxn persisterTxn) throws KnProvBOException;

    public KnOPProvDTO addOrModifyExtSubscribers(KnExtSubscriberInfoDTO extSubsInfoDTO, KnPersisterTxn persisterTxn) throws KnProvBOException;

    public KnOPProvDTO createSubscrRoamingProfiles(String mdn, KnPersisterTxn persisterTxn) throws KnProvBOException;

    public KnOPProvDTO deleteSubscrRoamingProfiles(String mdn, KnPersisterTxn persisterTxn) throws KnProvBOException;

    public KnOPCorpProfileInfoDTO retrieveCorporateProfile(String extCorpId, KnPersisterTxn persisterTxn) throws KnProvBOException;

    public KnOPCorpProfileInfoDTO createCorpProfile(KnIPCorpProfileInfoDTO corpProfileInfoDTO, KnPersisterTxn persisterTxn) throws KnProvBOException;

    public KnOPProvDTO updateCorpProfileLastUpdateTime(int corpId, KnPersisterTxn persisterTxn) throws KnProvBOException;

    public boolean isMdnExistsInPseudoPoolNPamAccInfo(String mdn, KnPersisterTxn persisterTxn) throws KnProvBOException;

    public KnOPUpdateSubsInfoDTO updateAutoPairing(KnIPSubsProvInfoDTO subsProvInfoInputDTO,KnPersisterTxn persisterTxn) throws KnProvBOException;

    public KnOPSubsProfileInfoDTO createTPUser(KnTPUserInfoDTO tpUserInfoDTO, KnPersisterTxn persisterTxn) throws KnProvBOException;

    public KnOPSubsProfileInfoDTO updateTPUser(KnTPUserInfoDTO tpUserInfoDTO, KnPersisterTxn persisterTxn) throws KnProvBOException;

    public KnOPProvDTO deleteTPUser(KnTPUserInfoDTO tpUserInfoDTO, KnPersisterTxn persisterTxn) throws KnProvBOException;

    public void verifyTPAccount(KnTPUserInfoDTO tpUserInfoDTO, KnPersisterTxn persisterTxn) throws KnProvBOException;



    /**
     * method to get the Subscriber Profile
     *
     * @param mdnsList KnIPSubscriberInfoDTO
     * @param persisterTxn  KnPersisterTxn
     * @return KnOPSubsProfileInfoDTO
     * @throws KnProvBOException BO entity Exception
     */
    public Map<String,String> fetchActiveFSForBulkMdns(List<String> mdnsList, KnPersisterTxn persisterTxn) throws KnProvBOException;

    public KnOPUpdateSubsInfoDTO switchConvergedClient(KnIPSubsProvInfoDTO subsProvInputDTO, KnPersisterTxn persisterTxn) throws KnProvBOException;

    public KnSysConfigRespDTO getSysConfig(KnIPSubsProvInfoDTO subsProfileInputDTO, KnPersisterTxn persisterTxn) throws KnProvBOException, KnFWException;

    public KnOPProvDTO actionOnTGSSDoc(String mdn, String newActiveFS, String oldActiveFS,KnPersisterTxn persisterTxn) throws KnProvBOException;

    public KnOPProvDTO actionOnTGSSDocCust(String mdn, String newActiveFS, String oldActiveFS,KnPersisterTxn persisterTxn) throws KnProvBOException;
    
    public KnOPSubsProfileInfoDTO searchCorpAddressBook(KnIPSubscriberInfoDTO subscriberDTO, KnPersisterTxn persisterTxn) throws KnProvBOException;

    public KnOPSubsProfileInfoDTO updatePrivacyOptStatus(KnIPSubscriberInfoDTO subscriberDTO, KnPersisterTxn persisterTxn) throws KnProvBOException;
    
	public void updateMCSIds(KnPersisterTxn persisterTxn, KnSubsProfilePersistDTO subsProfilePersistDTO) throws KnDAOException, KnProvBOException;

 public KnXDMProfileIdMdnMapRespDTO getMdnProfileIdsForMcPttIds(KnIPSubscriberInfoDTO subscriberDTO, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException, KnProvBOException;

    public String getMdnForUnassign(String baseMdn,String userProfileId, KnPersisterTxn persisterTxn) throws KnProvBOException;

    public KnOPCreateSubsInfoDTO createSubscriberForAssignUserProfile(KnIPSubsProvInfoDTO subsProfileInputDTO, KnPersisterTxn persisterTxn) throws KnProvBOException, KnFWException;

    public KnOPUpdateSubsInfoDTO updateSubscriberUserProfileFS(KnIPSubsProvInfoDTO subsProfileInputDTO, KnPersisterTxn persisterTxn) throws KnProvBOException;

    public KnOPUpdateSubsInfoDTO updateSubscrTS(KnIPSubsProvInfoDTO subsProfileInputDTO, KnPersisterTxn persisterTxn) throws KnProvBOException;
	
	public KnOPCreateSubsInfoDTO createDevice(KnXDMDeviceProvInfoDTO deviceProfileInfoInputDTO, KnPersisterTxn persisterTxn)throws KnDAOException,KnProvException;

    public KnXDMDeviceProvDTO getDeviceInfo(String deviceId, KnPersisterTxn persisterTxn)throws KnDAOException,KnProvException;

    public void deleteDeviceInfo(KnXDMDeviceProvDTO deviceId, KnPersisterTxn persisterTxn)throws KnDAOException,KnProvException;

    public KnOPUpdateSubsInfoDTO modifyDevice(KnXDMDeviceProvInfoDTO deviceProfileInfoInputDTO, KnPersisterTxn persisterTxn)throws KnDAOException,KnProvException;

    public List<String> getMdnForUPM(String baseMdn, KnPersisterTxn persisterTxn) throws KnProvBOException;

    public void loginNotifyEvent(KnXDMLoginNotifyEventReqDTO loginNotifyEventReqDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException;

    public KnOPMCPTTPermissionDTO getAuthorizedUserList(KnIPMCPTTPermissionDTO tuInfo, KnPersisterTxn persisterTxn) throws KnProvBOException, KnFWException;

    KnOPSubsProfileInfoDTO getUserprofileidsByProfileMdns(KnIPSubscriberInfoDTO subscriberDTO, KnPersisterTxn persisterTxn) throws KnProvBOException;

    public KnXDMSubsProfileRespDTO getSubscrClientSettings(KnIPSubsProvInfoDTO subClientSettingsDTO, KnPersisterTxn persisterTxn) throws KnProvBOException, KnFWException;

    /**
     * method to set the Subscriber client settings
     *
     * @param KnIPSubsProvInfoDTO KnIPSubsProvInfoDTO
     * @param persisterTxn        KnPersisterTxn
     * @return KnOPUpdateSubsInfoDTO
     * @throws KnProvBOException BO entity Exception
     * @throws KnFWException     Framework Exception
     */
    public KnOPUpdateSubsInfoDTO setSubscrClientSettings(KnIPSubsProvInfoDTO subsProfileInputDTO, KnPersisterTxn persisterTxn) throws KnProvBOException, KnFWException;

    public KnOPSubsProfileInfoDTO getSubscriberIfExist(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException, KnProvException;
    public KnOPCreateSubsInfoDTO createCorpAccount(KnXDMCorpProfileInfoDTO corpProfileInfoDTO, KnPersisterTxn persisterTxn)throws KnDAOException,KnProvException;
    public KnOPCreateSubsInfoDTO updateCorpAccount(KnXDMCorpProfileInfoDTO corpProfileInfoDTO, KnPersisterTxn persisterTxn)throws KnDAOException,KnProvException;
    public KnOPDeleteSubsRespDTO deleteCorpAccount(KnXDMCorpInfoDTO corpProfileInfoDTO, KnPersisterTxn persisterTxn)throws KnDAOException,KnProvException;
    public KnCorporateProfilepersistDTO1 getCorporateAccountDetails(KnXDMDeviceProvInfoDTO xdmRequestDTO, KnPersisterTxn persisterTxn) throws KnProvBOException, KnFWException, KnException;
    public KnXDMCorpAccountsListDTO retrieveCorporationAccountsList(KnPersisterTxn persisterTxn,String fetchSize, String nextToken) throws KnDAOException, KnProvException;

    public String getBaseMdnByProfileMdn(String mdn, KnPersisterTxn persisterTxn) throws KnProvBOException;

    /**
     * method to prepare the userLogin response for Lock Request denied error
     *
     * @param KnOPSubsProfileInfoDTO subscriberDTO
     * @param KnXDMActivateInfoDTO   activateInfoDTO
     * @return KnUserLoginResponseDTO
     * @throws KnProvException entity Exception
     */
    public KnUserLoginResponseDTO lockRequestErrorProcessor(KnOPSubsProfileInfoDTO subscriberDTO, KnXDMActivateInfoDTO activateInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException;

    public Map<String, Integer> getSubscriberServiceAuthStatus(List<String> mdns, KnPersisterTxn persisterTxn) throws KnDAOException, KnProvException;
    public KnXDMExtGWProfileListDTO retrieveExtGWProfileList() throws KnDAOException, KnProvException;
    public int getSubscriberServiceAuthStatusByUserId(String UserId, KnPersisterTxn persisterTxn) throws KnDAOException, KnProvException;

}
