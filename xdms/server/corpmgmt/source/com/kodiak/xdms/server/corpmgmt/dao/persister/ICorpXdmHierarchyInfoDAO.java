package com.kodiak.xdms.server.corpmgmt.dao.persister;

import com.kodiak.common.commdto.common.KnModifiedIdDetailsListDTO;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpHierarchyGeocodeMapDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnDeploySiteInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnHierarchyDepthInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnHierarchyInfoDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ICorpXdmHierarchyInfoDAO {

    public void insertIntoHierarchyDetails(List<KnHierarchyInfoDTO> hierarchyInfoDTOS, KnPersisterTxn persisterTxn) throws KnDAOException;


    public void updateIntoHierarchyDetails(List<KnHierarchyInfoDTO> hierarchyInfoDTOS, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void insertOrUpdateIntoHierarchyDetails(List<KnHierarchyInfoDTO> hierarchyInfoDTOS, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;


    public void insertIntoHierarchyDepth(List<KnHierarchyDepthInfoDTO> hierarchyInfoDTOS, KnPersisterTxn persisterTxn) throws KnDAOException;


    public void updateIntoHierarchyDepth(List<KnHierarchyDepthInfoDTO> hierarchyInfoDTOS, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void insertOrUpdateIntoHierarchyDepth(List<KnHierarchyDepthInfoDTO> hierarchyInfoDTOS, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<KnDeploySiteInfoDTO> getDeploySiteInfo(KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<String> getExtIdNameInfo(List<String> hierarchyInfoDTOS, Integer corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void insertIntoHierarchyGeocodeMap(List<KnCorpHierarchyGeocodeMapDTO> geocodeMapDTOS, KnPersisterTxn persisterTxn) throws KnDAOException;

    List<String> getDescendantExtIds(String extId, KnPersisterTxn persisterTxn) throws KnDAOException;

    void deleteFromHierarchyDetails(List<String> descendantExtIds, KnPersisterTxn persisterTxn) throws KnDAOException;

    void deleteFromHierarchyDepth(List<String> descendantExtIds, String parentHierarchyId, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    void updateHierarchyAttributes(KnModifiedIdDetailsListDTO modifiedDetail, boolean isAliasUpdateRequest ,KnPersisterTxn persisterTxn) throws KnDAOException;

    Set<String> fetchExistingDescendantHierarchyIds(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException, SQLException;

    void insertOrUpdateHierarchyGeoCode(Map<String,List<String>> addOrUpdateGeoCodes, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    void removeGeocodeMapping(int corpId, Map<String,List<String>> removeGeocodes, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnHierarchyDepthInfoDTO getRootNodeBasedOnCorpId(Integer corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, KnHierarchyDepthInfoDTO> getChileNodesBasedOnHierarchyIds(List<String> hierarchyIds, KnPersisterTxn persisterTxn) throws KnDAOException;

    Map<String,String> getHierarchyIdNameList(Integer corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    Map<String,String> getHierarchyIdNameAndIdList(Integer corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    /**
     * Fetches the maximum hierarchy ID from the database.
     * Returns the largest numeric hierarchy ID value stored in the hierarchy_details table.
     * Used for initializing the sequential ID counter on application startup.
     *
     * @param persisterTxn Database transaction context
     * @return Maximum hierarchy ID found in database, or null if table is empty
     * @throws KnDAOException if database query fails
     */
    Long getMaxHierarchyId(KnPersisterTxn persisterTxn) throws KnDAOException;

    /**
     * Fetches all hierarchy IDs with their corresponding depths from the database.
     * Used for ancestor-descendant relationship queries.
     *
     * @param corpId Corporate ID to filter by
     * @param descendantId Specific descendant hierarchy ID to query
     * @param persisterTxn Database transaction context
     * @return Map of hierarchy IDs to their depths
     * @throws KnDAOException if database query fails
     */
    Map<String, Integer> fetchAllHierarchyWithDepth(String corpId, String descendantId, KnPersisterTxn persisterTxn) throws KnDAOException;

    /**
     * Retrieves geocode list for a specific hierarchy within a corporation.
     * Queries CORP_HIERARCHY_GEOCODE_MAP table.
     *
     * @param corpId The corporate ID
     * @param hierarchyId The hierarchy ID
     * @param persisterTxn The transaction context
     * @return List of geocode strings for the specific hierarchy
     * @throws KnDAOException if database operation fails
     */
    List<String> getRegionsByHierarchyId(int corpId, String hierarchyId, KnPersisterTxn persisterTxn) throws KnDAOException;

    /**
     * Retrieves all regions from DEPLOY_SITE_INFO table.
     * Used when hierarchyId is not provided.
     *
     * @param corpId The corporate ID (used for logging)
     * @param persisterTxn The transaction context
     * @return List of all geocode strings
     * @throws KnDAOException if database operation fails
     */
    List<String> getAllRegions(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    Map<String,Map<String,Integer>> fetchMaxChildLengthAndDepth(List<String> hierarchyId, String corpId, KnPersisterTxn persistenceTxn) throws KnDAOException;

    List<KnHierarchyDepthInfoDTO> getChildHierarchyDepthInfo(String ancestorHierarchyId, String corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    Map<String,Integer> fetchMaxChildDepth(List<String> hierarchyId, String corpId, KnPersisterTxn persistenceTxn) throws KnDAOException;

    Map<String,String> getRootHierarchyName(Integer corpId, KnPersisterTxn persistDb) throws KnDAOException;

    Map<String, Map<Integer, Integer>> fetchClusterBasedSubscriberCount(Set<String> hierarchyIdList, int corpId, KnPersisterTxn persistDb) throws KnDAOException;
}
