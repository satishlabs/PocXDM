/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dao.persister;

import com.kodiak.common.commdto.request.KnXDMOSMInfoRequestDTO;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpOSMGroupListRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpOSMInfoListDetailsRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpOSMInfoListRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpOSMPersistDTO;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ICorpOSMDAO {

    public void createOSMList(KnCorpOSMPersistDTO corpOSMPersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException;
    public void updateOSMList(KnCorpOSMPersistDTO corpOSMPersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException;
    public void deleteOSMList(String corpId,String osmListId, KnPersisterTxn persisterTxn) throws KnDAOException;
    public void deleteOSMListOnDeleteCorpProfile(int corpId,KnPersisterTxn persisterTxn) throws KnDAOException;
    public Map<Integer,Integer> getOSMListIdAndDefaultMap(String corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;
    public Map<Integer,Integer> getOSMListIdAndDefaultMapByHierarchyId(String corpId, boolean readOnly, KnPersisterTxn persisterTxn,String hierarchyId) throws KnDAOException;
    public List<KnXDMOSMInfoRequestDTO> getUniqueFlieldsOSMInfoList(String osmListId, KnPersisterTxn persisterTxn) throws KnDAOException;
    public KnCorpOSMInfoListRespDTO getOSMListByCorp(String corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;
    public KnCorpOSMInfoListRespDTO getOSMListByCorpAndHierarchyId(String corpId, String hierarchyId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;
    public KnCorpOSMInfoListDetailsRespDTO getOSMListDetailsByListId(String corpId, String osmListId,boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;
    public void assignOSMIdToGroup(String corpId, String osmListId,Collection<String> assignedOSMIdToGroupIds,Collection<String> removedOSMIdFromGroupIds,String xdmsHome, KnPersisterTxn persisterTxn) throws KnDAOException;
    public KnCorpOSMGroupListRespDTO getOSMGroupListByCorpAndOSMId(String corpId, Collection<String> OSMListIds, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;
}

