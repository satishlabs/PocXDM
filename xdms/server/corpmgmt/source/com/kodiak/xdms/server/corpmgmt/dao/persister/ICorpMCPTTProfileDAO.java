/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dao.persister;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDirChgDTO;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDocChgDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPAuthUserPermissionInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnDocChangeListDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnSubsDestEmergencyAttributes;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnMcpttPermissionDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnSubsEmergencyAttributes;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * ************************************************************************
 * <p>
 * File name:  ICorpMCPTTProfileDAO.java
 * Subsystem:  PoC
 * <p>
 * Name                         Date                     Release
 * --------------------     ----------------        ------------------
 * Saurabh Kumar             Dec 07, 2017                9.0
 * <p>
 * <p>
 * KODIAK, 9th Floor, 'MFar Manyata Tech Park'
 * Greenheart Phase IV, Nagawara
 * Bangalore - 560 045
 * www.kodiakptt.com
 * All Rights Reserved.
 * <p>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */

public interface ICorpMCPTTProfileDAO {

    public Map<String, Long> getTargetUserPermissions(KnIPAuthUserPermissionInfoDTO ipAuthUserPermissionInfoDTO, boolean readOnly,
                                                    KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, KnMcpttPermissionDTO> getAuthUserPermissions(String authMdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, KnMcpttPermissionDTO> getTargUserPermissions(String targetMdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void insertIntoMcpttPermInfo(Collection<KnMcpttPermissionDTO> mcpttMappingDto, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateToMcpttPermInfo(Collection<KnMcpttPermissionDTO> mcpttMappingDto, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateToMcpttPermInfoForPrivacyStatus(Map<String,Integer> mapListForPrivacy, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteFromMcpttPermInfoAuthMdn(String authMdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteFromMcpttPermInfoAuthMdn(List<String> authMdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteFromMcpttPermInfoTargetMdn(String targetMdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteFromMcpttPermInfoTargetMdn(List<String> targetMdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, Long> seleteFromAuthDoc(Collection<String> authMdnList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void insertIntoAuthDoc(Collection<String> authMdnList, Map<String, Long> authEtagMap, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteFromMcpttPermInfo(String authMdn, Collection<String> targetMdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteFromMcpttPermInfo(Map<String, String> authTargetMap, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteFromMcpttPerm(Map<String, Collection<String>> authTargetMap, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, Integer> getAuthMdnList(KnIPAuthUserPermissionInfoDTO ipAuthUserPermissionInfoDTO, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Collection<KnSubsDestEmergencyAttributes> getEmergDestAttributes(String mdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, Collection<String>> getEmergUserDestMap(Collection<String> emergUserList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, Collection<String>> getEmergDestUserMap(Collection<String> emergDestList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Collection<KnSubsDestEmergencyAttributes> getEmergDestAttributesForDestination(String destination, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnSubsEmergencyAttributes getEmergSubsAttributes(String mdn, int corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteFromEmergSubsDestInfo(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteFromEmergSubsDestInfo(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteFromEmergInfoForDest(String emergDest, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteFromEmergInfoForDest(List<String> emergDest, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateToEmergSubsDestInfo(KnSubsEmergencyAttributes subsEmergencyAttributes, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateToEmergSubsDestAddInfo(KnSubsEmergencyAttributes subsEmergencyAttributes, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void insertIntoEmergSubsAttributes(Collection<KnSubsDestEmergencyAttributes> destEmergencyAttributes, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateEmergSubsAttributes(Collection<KnSubsDestEmergencyAttributes> destEmergencyAttributes, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteFromEmergDoc(Collection<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, Long> seleteFromEmergDoc(Collection<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void insertIntoEmergDoc(Collection<String> mdnList, Map<String, Long> emergEtagMap, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, KnOPDirChgDTO> updateEtag(Collection<String> mdnList, KnPersisterTxn persisterTxn, Map<String, KnOPDirChgDTO> etagMap) throws KnDAOException;


    public Map<String, KnOPDirChgDTO> updateEtagForUpm(Collection<String> mdnList, KnPersisterTxn persisterTxn, Map<String, KnOPDirChgDTO> etagMap) throws KnDAOException;

    public Map<String, KnOPDirChgDTO> selectEtag(Collection<String> mdnList, KnPersisterTxn persisterTxn, Map<String, KnOPDirChgDTO> etagMap) throws KnDAOException;


    public Map<String, KnOPDocChgDTO> insertOrUpdateEmergencyInfo(Collection<String> authMdnList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, Integer> getAuthUserMappingCount(Collection<String> mdnList, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Collection<String> getAuthMdnListFromTarget(Collection<String> mdnList, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, KnOPDocChgDTO> deleteFromAuthorizationInfo(Collection<String> authMdnList, Map<String, KnOPDocChgDTO> authorizationMap,
                                                                   KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, KnOPDocChgDTO> insertOrUpdateAuthorizationInfo(Collection<String> authMdnList, Map<String, KnOPDocChgDTO> authorizationMap,
                                                                       KnPersisterTxn persisterTxn) throws KnDAOException;

    public boolean isEmergencyDestExists(String emergDest, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, Collection<KnDocChangeListDTO>> updateSubsTS(Set<String> mdnList,String exists, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, Collection<KnDocChangeListDTO>> updateSubsTSandDirecEtag(Set<String> mdnList,String exists, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, List<String>> getMapOfProfileMdnByBaseMdn(List<String> baseMdnList, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, Integer> getPrivacyOptStatus(Collection<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void insertIntoMcpttPermInfoForProfileMdns(Collection<KnMcpttPermissionDTO> mcpttMappingDto,List<String> profilemdnList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateToMcpttPermInfoForProfileMdns(Collection<KnMcpttPermissionDTO> mcpttMappingDto,List<String> profilemdnList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteFromMcpttPermInfoForProfileMdns(List<String> authMdns, Collection<String> targetMdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, Integer> getCommonContactInfoFromMcpttPerm(String authMdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String,List<String>> getAuAndCommonTuMapping(List<String> authMdns, List<String> targetMdns, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, List<KnMcpttPermissionDTO>> getTargetMdnListPermissions(List<String> targetMdns, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<String> getProfileMdns(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, Integer> getPaginatedAuthUserList(List<String> targetMdnList,int startIndex, int endIndex, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String,Integer> getPaginatedDestinationOwnerMdns(List<String> destinationMdnList, int startIndex, int endIndex, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;}
