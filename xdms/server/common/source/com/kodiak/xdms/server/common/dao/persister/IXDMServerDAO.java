/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *****************************************************************************
 * File name:   IXDMServerDAO.java
 * Subsystem:   Server Common
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ravi Shanker .P       Dec 16, 2010       7.0
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
package com.kodiak.xdms.server.common.dao.persister;

import com.kodiak.common.commdto.common.KnPTTSettingDocInfoDTO;
import com.kodiak.common.commdto.common.KnXDMDeviceProvDTO;
import com.kodiak.common.commdto.request.KnXDMOSMInfoRequestDTO;
import com.kodiak.common.commdto.request.KnXDMTalkGroupInfoDTO;
import com.kodiak.common.commdto.response.KnXDMSubsProfileRespDTO;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
//import com.kodiak.frameworks.messaging.common.dto.KnMqServiceConfig;
import com.kodiak.common.dto.KnPendingTxnInfoDTO;
import com.kodiak.frameworks.messaging.common.dto.KnMqServiceConfig;
import com.kodiak.xdms.server.common.dto.common.*;
import com.kodiak.xdms.server.common.dto.persistdat.*;
import com.kodiak.xdms.server.common.dto.common.KnCorpUserProfileMCPTTConfig;
import com.kodiak.xdms.server.common.dto.common.KnAddlTGInfoDTO;

import java.util.*;

public interface IXDMServerDAO extends ICorpRecordingTargetInfoDAO {

    /**
     * method to create XDM Directory for Mdn
     *
     * @param mdn          String
     * @param persisterTxn KnPersisterTxn
     * @throws KnDAOException DB Layer Exception
     */
    public void addMdnToXDMDirectory(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;


    /**
     * method to create Contact List Doc Map fro Mdn
     *
     * @param contactListPersistDTO KnContactListPersistDTO
     * @param persisterTxn          KnPersisterTxn
     * @return int contact list Id
     * @throws KnDAOException DB Layer Exception
     */
    public int addMdnToXDMContactListDocMap(KnContactListPersistDTO contactListPersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

    /**
     * method to create Contact list for Mdn
     *
     * @param mdn           String
     * @param contactListId int contact list Id of contact list doc map
     * @param persisterTxn  KnPersisterTxn
     * @throws KnDAOException DB Layer Exception
     */
    public void addMdnToXDMContactList(String mdn, int contactListId, KnPersisterTxn persisterTxn) throws KnDAOException;

    /**
     * method to create corp resource List Index for Mdn
     *
     * @param mdn          String
     * @param persisterTxn KnPersisterTxn
     * @throws KnDAOException DB Layer Exception
     */
    public void addMdnToCorpResourceListIndexDoc(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    /**
     * method to retrieve XDM Service Configuration
     *
     * @param persisterTxn KnPersisterTxn
     * @return KnXDMServiceConfig DTO
     * @throws KnDAOException DB layer Exception
     */
    public KnXDMSServiceConfigDTO retrieveXDMSServiceConfig(KnPersisterTxn persisterTxn) throws KnDAOException;

    /**
     * method to retrieve the XDM Subsystem configuration
     *
     * @param persisterTxn KnPersisterTxn
     * @return Map
     * @throws KnDAOException DB Layer Exception
     */
    public Map<String, String> retrieveXDMSSubsSysConfig(KnPersisterTxn persisterTxn) throws KnDAOException;

    /**
     * @param corpId
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public KnCorpProfilePersistDTO getCorporateProfile(String corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Integer getQPPPCRFProfileId(Integer apnId, Integer QpppackId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Integer getQPPPCRFProfileIdForApnId(Integer apnId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnQPPProfileInfoDTO getQPPDetails(Integer apnId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<Integer> getCorpGroupId(String corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public String getGroupName(String groupId, KnPersisterTxn persisterTxn) throws KnDAOException;


    /**
     * @param mdn
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public KnSubsProfilePersistDTO getSubscriberProfile(String mdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, KnSubsProfilePersistDTO> getSubscriberProfileDetails(List<String> mdnList, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public int getSubscriberPV(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    /**
     * @param mdn
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void updateEtagForDirDoc(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    /**
     * method to delete the Contact List for the Mdn
     *
     * @param mdn          String
     * @param persisterTxn KnPersisterTxn
     * @throws KnDAOException DB Layer Exception
     */
    public void deleteContactListForMdn(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;


    public Integer selectContactListIdForMdn(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    /**
     * method to delete the Contact List Doc Map for Mdn
     *
     * @param mdn          String
     * @param persisterTxn KnPersisterTxn
     * @throws KnDAOException DB Layer Exception
     */
    public void deleteContactListDocMapForMdn(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;


    /**
     * method to delete the Corporate Resource List Index Doc Index For MDN
     *
     * @param mdn          String
     * @param persisterTxn KnPersisterTxn
     * @throws KnDAOException DB Layer Exception
     */
    public void deleteCorpResourceListIndexDoc(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;


    /**
     * @param mdn
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public int getCurrentEtagForDirDoc(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String,Integer> getCurrentEtagForDirDoc(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException;

    /**
     * @param mdn
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public int getCurrentDirEtagForUpdate(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;


    /**
     * method to delete the XDM Directory for the MDN
     *
     * @param mdn          String
     * @param persisterTxn KnPersisterTxn
     * @throws KnDAOException DB Layer Exception
     */
    public void deleteXDMDirectoryForMdn(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    /**
     * method to update the old Mdn of the Directory doc to the New Mdn
     *
     * @param oldMdn       String
     * @param newMdn       String
     * @param persisterTxn KnPersisterTxn
     * @throws KnDAOException DB Layer Exception
     */
    public void updateDirDocMdn(String oldMdn, String newMdn, KnPersisterTxn persisterTxn) throws KnDAOException;


    /**
     * method to update the old mdn contact list to the new Mdn
     *
     * @param oldMdn       String
     * @param newMdn       String
     * @param persisterTxn KnPersisterTxn
     * @throws KnDAOException DB Layer Exception
     */
    public void updateContactListMdn(String oldMdn, String newMdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    /**
     * method to update the old mdn of the contact list doc map to the new Mdn
     *
     * @param oldMdn       String
     * @param newMdn       String
     * @param persisterTxn KnPersisterTxn
     * @throws KnDAOException DB Layer Exception
     */
    public void updateContactListDocMapMdn(String oldMdn, String newMdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    /**
     * method to update the old mdn of the Corp resource List Index Doc to the new Mdn
     *
     * @param oldMdn       String
     * @param newMdn       String
     * @param persisterTxn KnPersisterTxn
     * @throws KnDAOException DB Layer Exception
     */
    public void updateCorpResourceListIndexDocMdn(String oldMdn, String newMdn, KnPersisterTxn persisterTxn)
            throws KnDAOException;


    /**
     * method to retrieve POC supported Devices
     *
     * @param persisterTxn KnPersisterTxn
     * @return KnPOCSuppDevicesDTO
     * @throws KnDAOException DB layer Exception
     */
    public Map<String, KnPOCSuppDevicesDTO> retrievePOCSuppDevices(KnPersisterTxn persisterTxn) throws KnDAOException;

    /**
     * method to create corp resource List Index for Mdns
     *
     * @param mdns         String
     * @param persisterTxn KnPersisterTxn
     * @throws KnDAOException DB Layer Exception
     */
    public void addMdnToCorpResourceListIndexDoc(List<String> mdns, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void addMdnToXDMDirectory(List<String> mdns, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<Integer> addMdnToXDMContactListDocMap(List<KnContactListPersistDTO> contactListPersistDTOs, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void addMdnToXDMContactList(List<String> mdns, List<Integer> contactListIds, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateEtagForDirDoc(List<String> mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteContactListForMdn(List<String> mdns, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteContactListDocMapForMdn(List<String> mdns, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteCorpResourceListIndexDoc(List<String> mdns, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteXDMDirectoryForMdn(List<String> mdns, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<KnBulkOrderInfoDTO> getCompletedBulkOrdersInfo(long expiredtime, KnPersisterTxn persisterTxn) throws KnDAOException;

    public int getBulkOrderCount(int bulkOrderId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnBulkOrderInfoDTO getCompletedBulkOrderDetail(int bulkOrderID, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteBulkOrders(List<Integer> bulkOrders, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateBulkOrders(List<Integer> bulkOrders, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnPAMServiceConfigDTO retrievePAMServiceConfig(KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<Integer> getCurrentEtagsForDirDoc(List<String> mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    /**
     * This method is to retrieve the external subscriber profiles
     *
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<Integer, KnExtProfileDetails> getExtProfileDetailsMap(KnPersisterTxn persisterTxn) throws KnDAOException;


    public Map<Integer, String> retrieveXCAPRootURIs(KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, Integer> retrieveAPNInfo(boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, Integer> getSubsApnId(List<String> mdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Integer getSubsApnId(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public String selectDefaultAPNName(boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<Integer, KnPOCBlackListDevicesDTO> retrievePOCBlackListDevices(KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, KnActivationCodeConfigDTO> retrieveActivationCodeConfig(KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, String> retrieveRTXConfig(List<String> keyList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public int updateUsageByMDNs(List<String> mdns, int usage, KnPersisterTxn persisterTxn) throws KnDAOException;

    public String retrieveActivationCode(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void removeActivationCode(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, KnTPUserAccountDTO> retrieveTPUserAccountForMDNs(List<String> mdns, KnPersisterTxn persistTxn) throws KnDAOException;

    public Map<String, KnTPUserAccountDTO> retrieveTPUserAccountForMDNs(List<String> mdns, boolean readOnly, KnPersisterTxn persistTxn) throws KnDAOException;

    public Map<Integer, KnClientTypeConfigDTO> retrieveClientTypeConfig(KnPersisterTxn persistTxn) throws KnDAOException;

    public Map<Integer, KnAPNConfigDTO> retrieveAPNInfoConfig(KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<Integer, KnSuppVocoderProfileDTO> retrieveSuppVocoderProfile(KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<Integer, KnMqServiceConfig> retrieveMqConfigDetails(KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, String> retrieveNotifyRoutingKeys(int clusterId, int sigCardType, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<KnMicroSvcsCommonConfig> retrieveMSSvcsCommonConfig(KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnMicroSvcsClusterInfo retrieveMSSvcsClusterConfig(KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<KnMicroSvcsServiceConfig> retrieveMSSvcsServiceConfig(KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<Integer, String> retrieveSericeFqdnInfo(KnPersisterTxn persisterTxn) throws KnDAOException;

    public void insertCBTxnFailLog(KnFailedCBTxnLogDTO cbTxnLogDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteCBTxnFailLog(KnFailedCBTxnLogDTO cbTxnLogDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<Integer, KnDataPkgInfoDTO> retrieveDataPkgInfo(KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, Integer> getSubscribersPV(List<String> mdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<Integer, Map<Integer, Integer>> retrieveAddlProfileInfoByPkgType(KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<KnQPPpcrfProfileDTO> retriveQPPpcrfProfile(boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnPocSubsAddlInfoDTO getSubsAddlDetails(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnEmergencyInfoDTO getEmergencySubsDestInfo(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnSubsProfilePersistDTO getProfileDetailsByMcpttID(String mcpttId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnSubsProfilePersistDTO getProfileDetailsByMcIDAndUpmIndex(String mcId,String upmIndex, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<KnSubsProfileDTO> getProfileDetailsListByMcID(String mcId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnCorpUserProfileDTO getUserProfileNameByindex(int corpId, int upmIndex, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnCorpUserProfileDTO getUserProfileById(String userProfileId)throws KnDAOException;

    public KnAPNProfileInfoDTO retrieveAPNProfileInfo(String xdmPttServerId, Integer apnId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void createDeviceInfo(KnDeviceInfoPersistDTO deviceInfo, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void insertAdditionalDeviceInfo(KnDeviceAddlInfoPersistDTO deviceAddlInfo, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateAdditionalDeviceInfo(KnDeviceAddlInfoPersistDTO deviceAddlInfo, KnXDMDeviceProvDTO deviceProfileInfo, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void modifyDeviceInfo(KnDeviceInfoPersistDTO deviceInfo, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void createDeviceImpiInfo(KnDeviceImpiInfoPersistDTO deviceImpiInfo, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteDeviceInfo(KnDeviceInfoPersistDTO deviceInfo, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteDeviceAddlInfo(KnDeviceInfoPersistDTO deviceInfo, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteDeviceImpiInfo(KnDeviceInfoPersistDTO deviceInfo, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateDigestPwd(String deviceId, String pwd, KnPersisterTxn persisterTxn) throws KnDAOException;

    public int getDeviceCountForCorpId(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public int getDeviceCountByMdnAndCorpId(String mdn, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public String getDeviceImpuInfo(String deviceImpl, KnPersisterTxn persisterTxn) throws KnDAOException;

    public boolean checkDeviceExsists(String deviceId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String,Boolean> getGroupMemberList(int groupId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String,Boolean> getGroupMemberListWithBC(int groupId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<KnCorpGpInfoDTO> getCorpGroupInfoDetails(List<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnCorpGpInfoDTO getCorpGroupInfoList(int groupId, KnPersisterTxn persisterTxn) throws KnDAOException;
    public Integer getMemberCountFromMemberList(int groupId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public String getSharedCorpGroup(int groupId, KnPersisterTxn persisterTxn) throws KnDAOException;

    List<KnSubsProfileDTO> retrieveBulkSubscribersInfo(List<String> mdns, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateSubscriberProfile(KnSubsProfileDTO subsProfileDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<KnXDMOSMInfoRequestDTO> getUniqueFieldsOSMInfoList(int corpGroupId,KnPersisterTxn persisterTxn) throws KnDAOException;

    public Integer getPOCCallTable(String db, String pocHome,KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnSIPProxySvcConfigDTO selectSIPProxySvcConfig(String pocPttServerId, KnPersisterTxn persistTxn) throws KnDAOException;

    public void updateAffForCorpGrpMemList(String mdn, int isAffiliationEnabled, KnPersisterTxn persistTxn) throws KnDAOException;

    public Map<String, List<Integer>> selectCorpGroupId(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<Integer> selectOwnerPreConfigCorpGroupId(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<Integer,Integer> getZoneChannelMap(int groupId,String mdn, KnPersisterTxn persistTxn) throws KnDAOException;

    public KnXDMTalkGroupInfoDTO getGroupPriority(int groupId,String mdn ,KnPersisterTxn persistTxn) throws KnDAOException;

    public List<String> getRealMdns(List<String> mdns, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String,Set<String>> getBaseMdnsMap(List<String> mdns, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;


	public KnXDMDeviceProvDTO selectDeviceProfile(String deviceId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnXDMDeviceProvDTO selectDeviceProfile(String deviceId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Integer selectOtherDeviceProfile(String deviceId, String reqDeviceId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Integer getDeviceProfile(String deviceSubscriberMdn, String intDeviceId, KnPersisterTxn persisterTxn) throws KnDAOException;

	public KnXDMSubsProfileRespDTO selectSubsProfileInfo(List<String> mdnList, KnPersisterTxn persistTxn) throws KnDAOException;

    public LinkedHashSet<String> selectSubDetails(List<String> mdnList, KnPersisterTxn persistTxn) throws KnDAOException;

	public List<String> getMcsIdNullMdnList(KnPersisterTxn persistTxn) throws KnDAOException;
	
	public void updateMcsIdForMdnList(List<String> mdnList,KnPersisterTxn persistTxn) throws KnDAOException;

    public Map<String, String> getProfileMdnBaseMdnMap(List<String> mdns, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, List<String>> getProfileMdnListByBaseMdnsList(List<String> baseMdnList, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;
    
    public void updateDeviceInfo(KnDeviceInfoPersistDTO deviceInfo, KnPersisterTxn persisterTxn) throws KnDAOException;
    
    public void updateDeviceType(KnDeviceInfoPersistDTO deviceInfo, KnPersisterTxn persisterTxn) throws KnDAOException;
    
    public void updateDeviceInfoStatusAndPassword(KnDeviceInfoPersistDTO deviceInfo, KnPersisterTxn persisterTxn) throws KnDAOException;
    
    public KnXDMDeviceProvDTO selectDeviceProfileByDeviceId(String deviceId, KnPersisterTxn persisterTxn) throws KnDAOException;
    
	public Map<String, Collection<KnDocChangeListDTO>> updateSubsTS(Set<String> baseMdnList, String exists,
			KnPersisterTxn persisterTxn) throws KnDAOException;
	
	public Map<String, Integer> getAndUpdateDirectoryEtag(Set<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, Integer> getDirectoryEtag(Set<String> mdnList) throws KnDAOException;

    public void updateEtagForDirDocOfMdnList(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException;

	public Set<KnCorpGroupListInfoDTO> retriveGroupProfileInfoByProfileId(String userProfileId, KnPersisterTxn persisterTxn) throws KnDAOException ;

	public Map<Integer, Integer> retrivePreConfigGroupProfileInfoByProfileId(String userProfileId, KnPersisterTxn persisterTxn) throws KnDAOException ;

	public List<String>  getSharedGroupMemberBySharedAndOwnCorpids(Integer ownedCorpId,List<Integer> sharedCorpId, KnPersisterTxn persisterTxn) throws KnDAOException ;

    public KnTalkGrpScanModeDTO getSubsTalkGrpScanMode(String mdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void insertSubsTalkGrpScanMode(KnTalkGrpScanModeDTO talkGrpScanModeDTO, KnPersisterTxn persisterTxn) throws KnDAOException;
    
    public void updateAffForCorpGrpMemList(Map<String, String> mdnIsAffiliationEnabledMap, KnPersisterTxn persistTxn) throws KnDAOException;

    public void updateSubsTalkGrpScanMode(List<String> mdnList,int tgscMode, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String,Map<Integer,String>> selectProfileIdMDNsByMcpttIds(List<String> mcpttIds, KnPersisterTxn persisterTxn) throws KnDAOException;

    public String selectUserProfileName(String mdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateUserProfileName(String userProfileName,String mdn, KnPersisterTxn persisterTxn)throws KnDAOException;

    public void updateSegmentIndicator(KnPersisterTxn persistTxn, List<String> mdns, Map<String, String> mdnSegmentIndicator) throws KnDAOException;


    public Map<String, String> getSegIndEnableSubs(KnPersisterTxn persistTxn) throws KnDAOException;


    public void deleteSubscriberCameraInfo(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteSubscriberCameraInfo(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String,List<Integer>> retriveUpmIdGroupMapByUserProfileId( List<String> userProfileIdList, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException ;

    public Map<Integer,List<Integer>> selectGroupSharedCorpId(int ownedCorpId, Collection<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException ;
    public Map<Integer,List<Integer>> selectOwnerCorpIdAndPreConfigGrps(int sharedCorpId, KnPersisterTxn persisterTxn) throws KnDAOException ;
    public List<Integer> getUserProfileGroupIdBySharedCorpId(int sharedCorpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public String selectSubscriberMdnByMCSId(List<String> mcsIds, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnCorpConfigInfoDto selectCorpConfigureInfo(int corpId, String paramName, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnCorpConfigInfoDto selectCorpConfigureInfo(int corpId, String paramName, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void insertCorpConfigureInfo(KnCorpConfigInfoDto corpConfigInfoDto, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateCorpConfigureInfo(KnCorpConfigInfoDto corpConfigInfoDto, KnPersisterTxn persisterTxn) throws KnDAOException;


    public void deleteCorpConfigureInfo(KnCorpConfigInfoDto corpConfigInfoDto, KnPersisterTxn persisterTxn) throws KnDAOException;
    public String getDeviceId(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnXDMDeviceProvDTO getDeviceImuiInfo(String deviceId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<Integer> getSubsAbdgGroupList(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Collection<KnCorpGpInfoDTO> getSubscriberGroupList(String mdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Boolean validateExtCorpGroup(String userProfileId, int subscCorpID, int groupOwnerCorpId, int groupId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public String getDeviceInfo(String DeviceId, KnPersisterTxn persisterTxn) throws KnDAOException;
    public List<String> getDeviceInfo(List<String> DeviceId, KnPersisterTxn persisterTxn) throws KnDAOException;
    public Map<String, String> getDeviceCreatedAsMap(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException;
    public void deleteRecordingInfoByCorpId(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateSipRecordingFlag(Integer sipRecordingFlag, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public int getAllSubscribersCount(int corpId) throws KnDAOException;

    Map<String, String> getProfileMDNswithMCID(int corpId, int start, int end);

    List<String> getProfileMDNswithNoBaseMDN(Map<String, String> profileMDNsWithMCID);
    List<String> getAllProfileMDNs(KnPersisterTxn persisterTxn);

    Map<String, List<String>> getInconsistenGroupId(String profileMdn, List<String> groupIds);

    Map<String, List<String>> getSublistIdContactMdnMap(String sublistId);
    List<String> ifSublistExistsinTT(List<String> sublistIds, KnPersisterTxn persisterTxn);

    Map<String, List<String>> getProfileMdnsForUPMId(String upmId);

    Map<String, List<String>> getContactMdnByProfileMdn(String profileMdn);
    String getFeatureBSForProfileMdnFromTT(String profileMdn);

    Map<String, KnCorpUserProfileMCPTTConfig> getProfileMdnPerm(String profileMdn);

    List<String> getAllCorpIds();
    List<String> getGroupIdsForProfileMdn(String profileMdnItr);
    KnEmergencyInfoDTO getEmergencyInfoForProfileMdn(String profileMdnItr);

    Map<String, Map<Integer,Integer>> getSublistDetailByProfileMdns(List<String> profileMdn, KnPersisterTxn persisterTxn);

    Map<Integer, List<String>> getSublistMemberBySublistId(List<Integer> subLists, KnPersisterTxn persisterTxn);

    public List<String> getTgssGroupExtM(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public int getSubsClientType(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public String getSubscriberName(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;


    public boolean isAnyProfileMdnCrossingIndexLimit(String mcId, int upmIndexLimit, KnPersisterTxn persisterTxn);

    public void recoverImpactedMdns(String impactedMdn, KnPersisterTxn persisterTxn);

    public String selectXDMCorpFS(int corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public boolean IsMdnPresentAsGroupMember(String groupMember, KnPersisterTxn persisterTxn) throws KnDAOException;

    public boolean IsMdnPresentAsContact(String contactMdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public boolean IsMdnPresentAsTarget(String targetMdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public boolean IsMdnPresentAsDestination(String destinationMdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void insertIntoAsyncTable(KnPendingTxnInfoDTO notifyDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

    public int getMdnCount(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public String getAddDeviceInfo(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<Integer> getBroadcasterGroupIds(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException ;

    public Map<String, Integer> getZoneAndChannerConfigValues(String extCorpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, List<Map<Integer, String>>> getGroupInfo(List<Integer> corpGroupIds, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void insertSubsAddlTGList(Collection<KnAddlTGInfoDTO> corpAddlTGInfoDTOS, KnPersisterTxn persisterTxn) throws KnDAOException;
    public List<KnAddlTGInfoDTO> getSubsAddlTGList(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Set<KnPTTSettingDocInfoDTO> getAllPTTSettingDocList(int corpId, String hierarchyId, KnPersisterTxn persisterTxn) throws KnDAOException;


    Map<String,String> getClusterId(KnPersisterTxn knPersisterTxn) throws KnDAOException;

}