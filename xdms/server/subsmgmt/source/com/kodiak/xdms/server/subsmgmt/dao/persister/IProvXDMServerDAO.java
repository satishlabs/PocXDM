/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *****************************************************************************
 * File name:   IProvXDMServerDAO.java
 * Subsystem:    Provisioning Lib
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ravi Shanker .P       Dec 15, 2010       7.0
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
package com.kodiak.xdms.server.subsmgmt.dao.persister;

import com.kodiak.common.commdto.common.KnExtGatewayInfoDTO;
import com.kodiak.common.commdto.common.KnSubsCameraInfo;
import com.kodiak.common.commdto.response.KnCorporateProfilepersistDTO1;
import com.kodiak.common.commdto.response.KnXDMCorpAccountsListDTO;
import com.kodiak.common.commdto.response.KnXDMExtGWProfileListDTO;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnTriePrefixUtility;
import com.kodiak.xdms.server.common.dto.common.*;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.dto.common.KnDocChangeListDTO;
import com.kodiak.xdms.server.subsmgmt.business.KnProvBOException;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.*;
import com.kodiak.xdms.server.subsmgmt.dto.common.*;
import com.kodiak.xdms.server.subsmgmt.dto.common.KnSubsProfileDTO;
import com.kodiak.xdms.server.subsmgmt.dto.persistdat.*;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvConstants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface IProvXDMServerDAO {

    public KnTriePrefixUtility<String> retrieveSubsPoCServerMap(KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnTriePrefixUtility<String> retrieveSubsPresenceServerMap(KnPersisterTxn persisterTxn) throws KnDAOException;

	public void createSubscrProfile(KnSubsProfilePersistDTO subsProfilePersistDTO,Boolean userProfileCreate,KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateSubscrProfile(KnSubsProfilePersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateSubsFS(KnSubsProfilePersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateUserAgnet(KnSubsProfilePersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void createSubscrRoamingProfile(String mdn, ArrayList<Integer> roamingClusterId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public int createCorporateProfile(KnCorpProfilePersistDTO corpProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

    public int retrieveCorporationId(String extCorpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public String retrieveExtCorporationId(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Integer retrievePrivacyAmbDiscListenFlag(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnOPCorpProfileInfoDTO retrieveCorporateProfile(String extCorpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnOPCorpProfileInfoDTO retrieveCorporateProfile(String extCorpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public int retrieveCorpSubscriberCount(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public int retrieveCorpProfileCleanUp(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<Integer, String> retrieveRoamingClusterId(KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnOPSubsProfileInfoDTO retrieveSubscriberInfo(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, com.kodiak.xdms.server.common.dto.common.KnSubsProfileDTO> retrieveSubscriberInfo(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnOPSubsProfileInfoDTO retrieveBaseMdnByMcpttId(String mcpttId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnOPSubsAddlInfoProfileDTO retrieveSubscrAddlInfo(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public int retrieveMdnServiceAuthStatusByUserId(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void activateSubscriber(KnSubsProfilePersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

    public ArrayList<Integer> retrieveSubscrRoamingProfile(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteSubscrRoamingProfile(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    //public void updateServiceAuthStatus(KnSubsProfilePersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException;
   // Modified the retuntype of the updateSericeAuthStatus
    public KnSubsProfilePersistDTO updateServiceAuthStatus(KnSubsProfilePersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnSubsProfilePersistDTO updateServiceAuthStatusForUPM(KnSubsProfilePersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateLastProfileUpdateTime(KnSubsProfilePersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteCorporateProfile(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteSubscriberProfile(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnPresenceServiceConfigDTO retrievePresenceServiceConfig(String presencePttServerId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnPOCSvcConfigDTO retrievePOCSvcConfig(String pocPttServerId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnPOCRegistrarSrvcConfigDTO retrievePOCregistrarSrvcConfig(String pocPttServerId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnXDMSSvcConfigDTO retrieveXDMSSvcConfig(KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnDialPlanInfoDTO retrieveDialPlanInfo(KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnXDMSDocSubPrxConfigDTO retrieveXDMSDocSubPrxConfig(String presPttServerId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnInstaPOCSrvcCfgDTO retrieveInstaPOCSrvcCfg(String pocPttServerId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateCorpProfileLastUpdateTime(int corpId, long lastProfileUpdateTime, KnPersisterTxn persisterTxn) throws KnDAOException;

   // public void createNewSubscrProfile(KnSubsProfilePersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException;
  // changed the returnType of the method such that its return will be used for further filteration
    public KnSubsProfilePersistDTO createNewSubscrProfile(KnSubsProfilePersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnSubsProfilePersistDTO updateSubscriberUserID(KnSubsProfilePersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void createNewSubscrAddlProfile(KnSubsAddlInfoPersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

    public String retrieveClientPassword(String oldMDN, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateSubscrRoamingProfileMdn(String oldMdn, String newMdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public int retrieveSubscriberCount(KnPersisterTxn persisterTxn) throws KnDAOException;

    public String retrieveSignalingCardName(String pttServerId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnLocationServiceConfigDTO retrieveLocationSrvcConfig(String presencePttId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateActiveFS(KnSubsProfilePersistDTO subsProfilePersistDTO, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateCorporateProfile(KnCorpProfilePersistDTO corpProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateExternalCorpID(KnCorpProfilePersistDTO corpProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateAccountIdForSubscr(String accountId, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public ArrayList<KnSWPkgConfigDTO> retrieveSWPkgConfig(KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnFeatureAccessNumberInfoDTO retrieveFeatureAccessNumberInfo(int featureAccIndex, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnFeatureAccessInfoDTO retrieveFeatureAccessInfo(int featureAccIndex, KnPersisterTxn persisterTxn) throws KnDAOException;


    public KnSIPProxySvcConfigDTO retrieveSIPProxySvcConfig(String pocPttServerId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public int createPAMAccountInfo(KnPAMAccPersistDTO pamAccPersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updatePAMAccountInfo(KnPAMAccPersistDTO pamAccPersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updatePAMBillingMDN(KnPAMAccPersistDTO pamAccPersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deletePAMAccountInfo(KnPAMAccPersistDTO pamAccPersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

    public int retrievePAMAccId(String extPAMAccId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void createPAMSubsProfile(KnPAMAccPersistDTO pamAccPersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updatePAMSubsProfile(KnPAMAccPersistDTO pamAccPersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deletePAMSubsProfile(KnPAMAccPersistDTO pamAccPersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnPAMSvcConfigDTO retrievePAMSvcConfig(KnPersisterTxn persisterTxn) throws KnDAOException;

    public void createSubscrProfile(KnBulkSubsProfilePersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateSubscrProfile(KnBulkSubsProfilePersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteSubscriberProfile(List<String> mdns, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void createSubscrRoamingProfile(List<String> mdns, List<Integer> roamingClusterId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateSubscrRoamingProfileMdn(List<String> oldMdns, List<String> newMdns, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteSubscrRoamingProfile(List<String> mdns, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteSubscrRoamingProfile(String mdn, ArrayList<Integer> roamingClusterId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnOPPAMAccInfoDTO retrievePAMAccInfo(String extPAMAccId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnOPPAMAccInfoDTO retrievePAMAccInfoFromId(int pamAccId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnPAMSubsProfInfoDTO getPAMSubsProfInfo(int pamAccId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnPAMSubsProfInfoDTO retrievePAMSubsProfInfo(int pamAccId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public int retrieveSubsCountforPAM(int pamAccId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updatePAMAccState(KnPAMAccPersistDTO pamAccPersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateServiceAuthStatus(KnBulkSubsProfilePersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<String> retrievePAMAccountMDNs(int pamAccId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<String> getPAMAccountMdnsByInsertionTime(int pamAccId, long insertionTime, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<KnOPSubsProfileInfoDTO> retrievePAMAccountMDNsDetails(int pamAccId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<String> retrievePAMAccountMDNs(int pamAccId, int fetchSize, String listMdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<String> retrievePAMAccountMDNs(int pamAccId, String startMdn, String endMdn, int fetchSize, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnSubsPartitionConfigDTO retrievePartitionConfig(KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, List<String>> retrievePttServerIds(KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, List<String>> retrievePttServerIdsByClusterId(int clusterId,KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, Integer> getSubsCount(List<String> strings, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Boolean isPrInPocEnabled(KnPersisterTxn persisterTxn) throws KnDAOException;

    public String getPreAssignCorpHome(String extCorpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, KnPocSubsCapConfigDTO> getPocSubsCapacityConfig(List<String> pttServerIds, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateCorpName(String extCorpId, String corpName, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateLastProfileUpdateTimeForPamAccId(int PamAccId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updatePAMAccMaxSub(KnPAMAccPersistDTO pamAccPersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updatePAMAccName(KnPAMAccPersistDTO pamAccPersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, KnOPSubsProfileInfoDTO> retrieveNotificationDetails4mdns(List mdns, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateServerCapacityUtil(List<String> pttServerIds, KnProvConstants.COUNT delimiter, int count, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<String> retrievePAMAccountProvMDNs(int pamAccId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<String> getPamAccLastSequenceMdns(int pamAccId, int start, int end, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<KnOPSubsDispatcherDTO> retrieveBulkSubscribers(List<String> mdns, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateBulkSubscrProfile(List<KnOPSubsDispatcherDTO> subsProfilePersistDTOs, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteSubApn(String mdn, KnPersisterTxn persistTxn) throws KnDAOException;

    public void updateSubApn(String mdn, int apnId, KnPersisterTxn persistTxn) throws KnDAOException;

    public Map<String, Integer> selectSubApn(List<String> mdnList, boolean readOnly, KnPersisterTxn persistTxn) throws KnDAOException;

    public void addSubApn(String mdn, int apnId, KnPersisterTxn persistTxn) throws KnDAOException;

    public void createExtSubscriber(KnExtSubsPersistDTO extSubsPersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteExtSubscriber(KnExtSubsPersistDTO extSubsPersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateExtSubscriber(KnExtSubsPersistDTO extSubsPersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnExtSubsPersistDTO getExtSubscriberInfo(ArrayList<String> extMdnList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<Integer> getCorpIdsForExtSubscriber(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Integer getAPNId(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteSubApn(List<String> mdnList, KnPersisterTxn persistTxn) throws KnDAOException;

    public void addSubApn(List<String> mdn, int apnId, KnPersisterTxn persistTxn) throws KnDAOException;

    public void updatePAMAccLastProfileUpdatedTime(int pamAccId, long lastProfileUpdatetime, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<Integer, List<Integer>> retrieveRoamingTypeClusterIds(KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<Integer> retrievePAMAccIdForCorpId(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateBulkSubscrCorpId(KnBulkSubsProfilePersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updatePAMSubsProfCorpId(KnPAMAccPersistDTO pamAccPersistDTO, int oldCorpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateBulkSubsLastProfileUpdateTime(KnBulkSubsProfilePersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

    public int getSubsCountOfClientTypeForCorp(int corpId, List<Integer> clientTypeList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateLinkedGwKeyOfCorp(KnCorpProfilePersistDTO corpProfilePersistDTO, KnPersisterTxn persistTxn) throws KnDAOException;

    public void updateEtag4corpNNIRefId(KnCorpGwLinkedAccInfoDTO corpGwLinkedAccInfoDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void createNNISubscrProfile(KnBulkNNISubsProfilePersistDTO nniSubsProfilePersistDTO, KnPersisterTxn persistTxn) throws KnDAOException;

    public int getProfileIdForNNISubscriber(int profileType, KnPersisterTxn persistTxn) throws KnDAOException;

    public void deleteNNISubscrProfile(List<String> mdns, KnPersisterTxn persistTxn) throws KnDAOException;

    public boolean isMdnPresentInPamAccInfo(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public boolean isMdnExistInPocSubsInfo(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnPAMAccPoolUsageDTO getFirstUnusedMdnFromPAMAccPoolUsage(String billingMDN, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void createTPUserMDNMap(KnTPUserPersistDTO tpUserPersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnTPUserAccountDTO retrieveTPUserAccountForMDN(String mdn, KnPersisterTxn persistTxn) throws KnDAOException;

    public int retrievePAMAccPoolUsageForMDN(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateTPUserMDNMapByMDN(KnTPUserPersistDTO tpUserPersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteTPUserMDNMap(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException;

    public void createPAMAccPoolUsage(KnPAMAccPoolPersistDTO pamAccPoolPersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deletePAMAccPoolUsage(List<String> mdns, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateSubsContactListID(int contactListID, String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void createClientSuppVocoder(List<KnClientVocoderProfilePersistDTO> clientVocoderProfilePersistDTOs, KnPersisterTxn persisterTxn)throws KnDAOException;

    public void deleteClientSuppVocoder(String mdn, KnPersisterTxn persisterTxn)throws KnDAOException;

    public void deleteClientSuppVocoder(List<String> mdnList, KnPersisterTxn persisterTxn)throws KnDAOException;

    public void deleteSubsAddlInfo(String mdn, KnPersisterTxn persisterTxn)throws KnDAOException;

    public  Map<Integer, Integer> retrieveClientSuppVocoder(String mdn, boolean readOnly, KnPersisterTxn persisterTxn)throws KnDAOException;

    public void updateSubscVocoderId(String mdn, Integer vocoderId, boolean readOnly, KnPersisterTxn persisterTxn)throws KnDAOException;

    public KnOPPAMAccInfoDTO retrieveLicensePackInfo(String extPAMAccId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnOPSubsProfileInfoDTO selectSubsProfileInfo(List<String> mdnList, boolean readOnly, KnPersisterTxn persistTxn) throws KnDAOException;

    public Collection<KnSubsProfileDTO> fetchSubsSpecificDetailsForBulkMdns(List<String> mdns, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateConvergedClientProfile(KnSubsProfilePersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnOPSubsProfileInfoDTO retrieveSubscriberInfoForUFMI(String ufmi, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteMCPTTProfile(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteTargetEntry(String authMdn,String targetMdn, KnPersisterTxn persistTxn) throws KnDAOException;

    public void deleteAuthorizationDocProfile(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<String> fetchMCPTTAuthorizedMdn(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<KnMCPTTPermInfoDTO> getMCPTTPermInfo(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<KnMCPTTPermInfoDTO> getMCPTTPermInfoForTargetMdn(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void createNewMdnInAuthrizationDoc(String mdn, Long etag, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateAuthorizationDocEtag(String mdn, Long etag, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateMCPTTTargetMdn(String oldMdn, String newMdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateMCPTTAuthorizedMdn(String oldMdn, String newMdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void disableDiscreetEnabled(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updatePermBit(String mdn, long permBit, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void createSubscrPkgAddlInfo(KnSubsAddlInfoPersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException ;

    public void updateSubscrPkgAddlInfo(KnSubsAddlInfoPersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException ;

    public void updateSubscrPkgAddlInfoforProfileMdn(KnSubsAddlInfoPersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateSubscrOnBoardingMail(KnSubsAddlInfoPersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException ;

    public void createBulkSubscrPkgAddlInfo(KnSubsAddlInfoPersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException ;

    public void updateBulkSubscrPkgAddlInfo(KnSubsAddlInfoPersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException ;

    public void createSubAddOnPkgs(String mdn, List<String> addOnPkgs,  KnPersisterTxn persisterTxn) throws KnDAOException ;

    public void createBulkSubAddOnPkgs(List<String> mdn, List<String> addOnPkgs,  KnPersisterTxn persisterTxn) throws KnDAOException ;

    public List<String> selectSubAddOnPkgs(String mdn, KnPersisterTxn persistTxn) throws KnDAOException ;

    public void deleteSubAddlOnPkgs(String mdn, KnPersisterTxn persistTxn) throws KnDAOException ;

    public void deleteSubAddlOnPkgs(List<String> mdnList, KnPersisterTxn persistTxn) throws KnDAOException ;

    public void deleteBulkSubAddlOnPkgs(List<String> mdns, KnPersisterTxn persistTxn) throws KnDAOException ;

    public void createPAMSubAddOnPkgs(int pamAccId, List<String> addOnPkgs, KnPersisterTxn persistTxn) throws KnDAOException ;

    public List<String> selectPAMSubAddOnPkgs(int pamAccId, KnPersisterTxn persistTxn) throws KnDAOException;

    public void deletePAMSubAddlOnPkgs(int pamAccId, KnPersisterTxn persistTxn) throws KnDAOException ;

    public Map<String, KnSubsAddlInfoDTO> retrieveSubscrAddlInfo(List<String> mdns, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String,List<String>> selectSubAddOnPkgs(List<String> mdns, KnPersisterTxn persistTxn) throws KnDAOException ;

	public KnOPSubsProfileInfoDTO selectSubscriberProfileByUserId(String userId, KnPersisterTxn persisterTxn) throws KnDAOException;

	public KnOPSubsProfileInfoDTO selectSubscriberProfileByAliasMdn(String aliasMdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnOPSubsProfileInfoDTO selectSubscriberProfileByMcpttId(String mcpttId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, Map<Integer, String>> selectProfileIdMDNsByMcpttIds(List<String> mcpttIds, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnTPVendorDetailsDTO retrieveTPVendorDetails(String vendorID, KnPersisterTxn persisterTxn) throws KnDAOException;

	public KnTPUserAccountDTO getTPUserDetails(String userName, KnPersisterTxn persistTxn) throws KnDAOException;

    public long getTGSSDocEtag(String mdn,  KnPersisterTxn persisterTxn) throws KnDAOException;

    public void createTGSSDoc(String mdn, long etag,KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteTGSSDoc(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteTGSSDoc(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void createSSChannelGrpInfo(String mdn,List<Integer>groupIds, KnPersisterTxn persisterTxn) throws KnDAOException ;

    public List<Integer> selectSSChannelGrpInfo(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteSSChannelGrpInfo(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteSSChannelGrpInfo(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnOPSubsProfileInfoDTO searchCorpAddressBook(KnIPSubscriberInfoDTO subscriberDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<KnOPSubsProfileInfoDTO> selectSubscriberProfileByMCSIds(List<String> mcsIds, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<KnOPSubsProfileInfoDTO> selectSubscriberProfileByMDNorAliasMdn(List<String> mdns, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<KnOPSubsProfileInfoDTO> selectSubscriberProfileByAliasMdn(List<String> mdns, KnPersisterTxn persisterTxn) throws KnDAOException;

   public List<KnOPSubsProfileInfoDTO> selectSubscriberProfileByMCDataIds(List<String> mcsIds, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<KnOPSubsProfileInfoDTO> selectSubscriberProfileByUserIds(List<String> userIds, KnPersisterTxn persisterTxn) throws KnDAOException;

	public void updatePrivacyOptStatus(KnIPSubscriberInfoDTO subscriberDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

	public void updateMCSIds(KnSubsProfilePersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

    public String getMdnForUnassign(String baseMdn,String userProfileId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String,KnOPSubsProfileInfoDTO> getProfileMdnNupmfsByBaseMdn(String baseMdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String,KnOPSubsProfileInfoDTO> getProfileMdnNupmfsByBaseMdn(String baseMdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<String> getMdnForUPMList(List<String> baseMdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateSubscrUserProfileFS(KnSubsProfilePersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateSubscrTS(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnOPSubsProfileInfoDTO retrieveSubscriberInfoForUFMIForAssignUserProfile(String ufmi, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnOPSubsProfileInfoDTO selectSubscriberProfileByAliasMdnForAssignUserProfile(String aliasMdn, KnPersisterTxn persisterTxn) throws KnDAOException;
    
	public void updateMCSIdsForUserProfiles(KnSubsProfilePersistDTO subsProfilePersistDTO, String mcId, String mcDataId,
			String mcVideoId, String mcPttId, KnPersisterTxn persisterTxn) throws KnDAOException;
	
    public String getProfileName(int corpId, Integer userProfileIndex) throws KnDAOException;

    public void updateMdnFiledsNActiveFS(KnSubsProfilePersistDTO subsProfilePersistDTO,
                                       Map<String, String> mdnActivsFsMap, KnPersisterTxn persisterTxn) throws KnDAOException;
    
    public void updateProfileMdnDetails(KnSubsProfilePersistDTO subsProfilePersistDTO,List<String> UserProfileMdns, KnPersisterTxn persisterTxn) throws KnDAOException;
    
    public Map<String,String> createUserProfileMdnMap(KnSubsProfilePersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException;
    
    public void deleteUserProfileMdnMap(String basMdn, KnPersisterTxn persisterTxn) throws KnDAOException;
    
    public Map<String, String> getUserProfileMdnMap(String baseMdn, KnPersisterTxn persisterTxn) throws KnDAOException;
    
    public void updateUserProfileMdnMap(KnSubsProfilePersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, Collection<KnDocChangeListDTO>> profileMdnEtagUpdate(List<String> profileMdnList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateSubApnIdForMdnList(List<String> mdnList, int apnId, boolean readOnly, KnPersisterTxn persistTxn) throws KnDAOException;

    public List<String> getBaseMdnByProfileMdn(List<String> profileMdnList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnOPSubsProfileInfoDTO selectUserProfileIdsByProfileMdns(List<String> mdnList, KnPersisterTxn persisterTxn)throws KnDAOException;

    public void updateUserProfileName(KnSubsAddlInfoPersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn)throws KnDAOException;

    public String selectUserProfileName(String mdn, KnPersisterTxn persisterTxn)throws KnDAOException;

    public Map<String,String> selectUserProfileNameList(List<String> mdnList, KnPersisterTxn persisterTxn)throws KnDAOException;

    public String getSubClientSettings(String mdn,KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnOPSubsProfileInfoDTO getSubscrClientSettings(String mdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnOPSubsAddlInfoProfileDTO setSubscrClientAddlInfo(KnSubsAddlInfoPersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException ;

    public List<String> getProfileMdnListByBaseMdn(String baseMdn, KnPersisterTxn persisterTxn) throws KnDAOException;
    public void clearUserProfileAssignment(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException;
    public void updateToPrivacyOptStatus(Map<String,Integer> mapListForPrivacy, KnPersisterTxn persisterTxn) throws KnDAOException;

 	public void createSubscriberCameraInfo(KnSubsProfilePersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException ;

    public Map<String, KnSubsCameraInfo> getSubscriberCameraInfo(Collection<String> mdns, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, List<KnSubsAliasInfoDTO>> selectSubsAliasIdInfo(Map<String, String> aliasIdIssuerMap, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public  Map<String, List<KnSubsAliasInfoDTO>> selectSubsAliasIdInfoByMdn(Collection<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public  void insertIntoSubsAliasId(List<KnSubsAliasInfoDTO> aliasInfoList,String mdn,KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateSubscriberCameraInfo(String mdn,KnSubsCameraInfo cameraInfo, KnPersisterTxn persisterTxn) throws KnDAOException;

    public  void deleteSubsAliasId(String mdn,KnPersisterTxn persisterTxn) throws KnDAOException;

    public  void deleteSubsAliasId(List<String> mdnList,KnPersisterTxn persisterTxn) throws KnDAOException;

    public  Map<String,List<KnSubsAliasInfoDTO>>  selectSubsAliasIdInfoByAliasId(Map<String, String> aliasIdIssuerMap, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteSubscriberCameraInfo(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

   public void removeSubsAliasId(String mdn, List<KnSubsAliasInfoDTO> removeAliasInfoList, KnPersisterTxn persisterTxn) throws KnDAOException;

   public void updateDefaultProfileFlag(String mcId,int corpId,int defaultProfile,KnPersisterTxn persisterTxn) throws KnDAOException;
   public int getMaxUserProfileIndex(String baseMdn, KnPersisterTxn persisterTxn) throws KnDAOException;

   public Map<String, String> getBaseMdnListForRequestingMdnList(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException;

   public KnOPSubsProfileInfoDTO getSubscriberProfileIfExist(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;
   public int createCorporateAccount(KnCorpProfilePersistDTO corpProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException;
   public int updateCorporateAccount(KnCorpProfilePersistDTO corpProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException;
   public int getCorpId(String extCorpId, KnPersisterTxn persisterTxn) throws KnDAOException, KnProvBOException;
   public int fetchCorpSubscriberCount(String extCorpId, KnPersisterTxn persisterTxn) throws KnDAOException;
   public void deleteCorporateAccount(String extCorpId, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;
   public KnCorporateProfilepersistDTO1 getCorpID(String extCorpId, KnPersisterTxn persisterTxn) throws KnDAOException, KnProvBOException;
   public KnCorporateProfilepersistDTO1 getCorporateAccountDetails(int CorpId, KnPersisterTxn persisterTxn) throws KnDAOException, KnProvBOException;
   public KnXDMCorpAccountsListDTO retrieveCorporationAccountsList(KnPersisterTxn persisterTxn,String fetchSize, String nextToken) throws KnDAOException;

   public int retrieveCorpSubscriberWithProfileCnt(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

   public KnExtGatewayInfoDTO getExtGatewayDetails(String extGatewayId, KnPersisterTxn persisterTxn) throws KnDAOException;
   public Map<String, Integer> getSubscriberServiceAuthStatus(List<String> mdns, KnPersisterTxn persisterTxn) throws KnDAOException;
    public KnXDMExtGWProfileListDTO retrieveExtGWProfileList() throws KnDAOException;

    public List<Integer> getGroupIdsList(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<Integer> getSharedGroupList(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, List<Map<Integer, String>>> getGroupInfo(List<Integer> corpGroupIds, KnPersisterTxn persisterTxn) throws KnDAOException;

    public String getAddDeviceInfo(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, Integer> getZoneAndChannerConfigValues(String extCorpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<Integer> getBroadcasterGroupIds(String corpGroupIds, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void insertSubsAddlTGList(Collection<KnSubsAddlTGInfoDTO> subsAddlTGInfoDTOS, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateEmergencyDocEtag(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

}