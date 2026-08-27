/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dao.persister;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.xdms.server.common.KnXDMServerException;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ICorpInfoDAO {

    Map<Integer, Integer> getCorpIdAndLargeGroupFlagMap(Collection<Integer> corpIds, KnPersisterTxn persisterTxn) throws KnDAOException;

    void getHierarchyDetails(String corpId, List<String> removedHierarchy, KnPersisterTxn persisterTxn) throws KnDAOException, KnXDMServerException;

    void getSubscriberCountForHierarchyDeletion(String corpId, List<String> removedHierarchy, KnPersisterTxn persisterTxn) throws KnDAOException, KnXDMServerException;

    void getGroupCountForHierarchyId(String corpId, List<String> removedHierarchy, KnPersisterTxn persisterTxn) throws KnDAOException, KnXDMServerException;

    Map<String, Integer> checkGroupProfilesForHierarchyDeletion(String corpId, List<String> hierarchyIds, KnPersisterTxn persisterTxn) throws KnDAOException;

    Map<String, Integer> checkSubListsForHierarchyDeletion(String corpId, List<String> hierarchyIds, KnPersisterTxn persisterTxn) throws KnDAOException;

    Map<String, Integer> checkOSMListInfoForHierarchyDeletion(List<String> hierarchyIds, KnPersisterTxn persisterTxn) throws KnDAOException;

    Map<String, Integer> checkOSMListForHierarchyDeletion(String corpId, List<String> hierarchyIds, KnPersisterTxn persisterTxn) throws KnDAOException;

    void deleteHierarchyDetails(String corpId, List<String> removedHierarchy, KnPersisterTxn persisterTxn) throws KnDAOException, KnXDMServerException;

    void deleteHierarchyDepthDetails(String corpId, List<String> removedHierarchy, KnPersisterTxn persisterTxn) throws KnDAOException, KnXDMServerException;

    void deleteAnchorPocInfo(String corpId, List<String> removedHierarchy, KnPersisterTxn persisterTxn) throws KnDAOException, KnXDMServerException;

    void deleteHierarchyGeocodeMapping(String corpId, List<String> removedHierarchy, KnPersisterTxn persisterTxn) throws KnDAOException, KnXDMServerException;
}
