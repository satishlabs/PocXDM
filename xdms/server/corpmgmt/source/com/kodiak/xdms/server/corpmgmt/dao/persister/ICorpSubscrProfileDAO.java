/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dao.persister;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.xdms.server.common.dto.common.KnSubsProfileDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPSubscriberInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpLITargetProfile;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpUserProfileDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnSubscrFeatureSetRespDTO;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by asanjiv on 11/2/2016.
 */
public interface ICorpSubscrProfileDAO {


    public KnSubscrFeatureSetRespDTO getSubscriberFeatureSets(int corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String,KnCorpSubscriberDTO> getSubscrFeatureBitDetails(Set<String> reqMdnList, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateSubscrProfiles(Map<String, KnCorpSubscriberDTO> updateSubscrMap, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateCorpSubscriber(KnIPSubscriberInfoDTO corpSubscriberDTO,List<String> profileMdns, KnPersisterTxn persisterTxn) throws KnDAOException;

    public String getUserIdProfile(KnIPSubscriberInfoDTO corpSubscriberDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Collection<KnCorpLITargetProfile> getLITargetInfo(KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnCorpSubscriberDTO getUserProfile(KnIPSubscriberInfoDTO corpSubscriberDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

    public String getAliasMdnProfile(KnIPSubscriberInfoDTO corpSubscriberDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateSubsEntities(KnIPSubscriberInfoDTO corpSubscriberDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateSubscriberServiceAuthStatus(KnIPSubscriberInfoDTO corpSubscriberDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, KnCorpSubscriberDTO> getSubscriberAdditionalDetails(Collection<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException;
    
    public void updateSubscriberMCSIds(KnSubsProfileDTO subscProfile, KnPersisterTxn persisterTxn) throws KnDAOException;
    
    public  Map<Integer, Integer> getSubscriberUserProfileList(String mdn,int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public  Map<String,Integer> getSubscriberUserProfileList(String mdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<String> getProfileMdnByBaseMdn(String baseMdn, KnPersisterTxn persisterTxn) throws KnDAOException;
    
    public List<String> getProfileMdnByBaseMdns(List<String> baseMdn,KnPersisterTxn persisterTxn) throws KnDAOException;
    
    public Set<String> getUniqueMcpttIds(Collection<String> mdns, KnPersisterTxn persisterTxn) throws KnDAOException;
    
    public Map<String,String> getProfileMdnsByCorpId(String corpId,  KnPersisterTxn persisterTxn) throws KnDAOException;

    public Set<String> getBaseMdnByProfileMdns(List<String> profileMdns, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;
    
    public void updateProfileMdnDetails(KnCorpSubscriberDTO subsProfilePersistDTO, Map<String, String> mdnActivsFsMap,  Map<String, KnCorpSubscriberDTO> mdnUpmFsMap, KnPersisterTxn persisterTxn) throws KnDAOException;
    
    public Map<String, KnCorpSubscriberDTO> getProfileMdnAndUpmfsByBaseMdn(String baseMdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, String> getProfileMdnBaseMdnMap(List<String> profileMdns, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String,String> getMdnMcpttIdMap(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Set<String> getMDNListByFanIds(Set<Integer> fanIds, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public boolean isCommonContactList(Collection<Integer> sublistIds, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String,Map<String,Integer>> getMdnsFanBanInfo(Collection<String> mdns, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<Integer> getDistinctFanInfo(Collection<String> mdns, KnPersisterTxn persisterTxn, boolean readOnly) throws KnDAOException;

    public List<Integer> getDistinctBanInfo(Collection<String> mdns, KnPersisterTxn persisterTxn, boolean readOnly) throws KnDAOException;

    public Collection<String> getMcidsByMdnList(Collection<String> membersList, KnPersisterTxn persisterTxn, boolean readOnly) throws KnDAOException;

    public Map<Integer,String> getFanDetailsByCorporateId(int corpId, KnPersisterTxn persisterTxn, boolean readOnly) throws KnDAOException;

    public Map<Integer,String> getBanDetailsByCorporateId(int corpId, KnPersisterTxn persisterTxn, boolean readOnly) throws KnDAOException;

}
