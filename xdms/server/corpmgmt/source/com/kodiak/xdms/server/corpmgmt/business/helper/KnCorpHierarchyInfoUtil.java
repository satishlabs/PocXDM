package com.kodiak.xdms.server.corpmgmt.business.helper;

import com.kodiak.common.commdto.common.KnIdDetailsDTO;
import com.kodiak.common.commdto.common.KnIdDetailsListDTO;
import com.kodiak.common.commdto.common.KnAddedChildRelationDTO;
import com.kodiak.common.commdto.common.KnModifiedIdDetailsListDTO;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.corpmgmt.business.KnCorpBOException;
import com.kodiak.xdms.server.corpmgmt.dao.persister.ICorpXdmDAO;
import com.kodiak.xdms.server.corpmgmt.dao.persister.db.KnCorpXdmDAO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpHierarchyGeocodeMapDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnDeploySiteInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnHierarchyDepthInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnHierarchyInfoDTO;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.kodiak.xdms.server.corpmgmt.resources.KnConstants.HIERARCHY_BATCH_INSERT_SIZE;

public class KnCorpHierarchyInfoUtil {
    private static final KnLogger knLogger = KnLogger.getLogger(KnCorpHierarchyInfoUtil.class);
    public static final String HIERARCHY_DETAILS_LIST = "hierarchyDetailsList";
    public static final String HIERARCHY_DEPTH_LIST = "hierarchyDepthList";
    public static final String HIERARCHY_GEO_CODE_LIST = "hierarchyGeoCodeList";

    // Single delegation point for sequential hierarchy id generation.
    private static final KnHierarchyIdGenerator HIERARCHY_ID_GENERATOR = KnHierarchyIdGenerator.getInstance();

    /**
     * Special constant to mark root nodes in the hierarchy depth table.
     * Root nodes will have this value as their ancestor_hier_id to indicate they have no parent.
     */
    public static final String ROOT_ANCESTOR_ID = "0000000000000000000000000000000000000000000000000000000000000000";

    /**
     * Delegates hierarchy ID generation to a dedicated generator class.
     */
    public String generateNextHierarchyId(String xdmsHome, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        return HIERARCHY_ID_GENERATOR.generateNextHierarchyId(xdmsHome, persisterTxn);
    }

    public void insertIntoHierarchyDetails(List<KnHierarchyInfoDTO> hierarchyInfoDTOS, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "insertIntoHierarchyDetails(Map<String, Long>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.insertIntoHierarchyDetails(hierarchyInfoDTOS, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void updateIntoHierarchyDetails(List<KnHierarchyInfoDTO> hierarchyInfoDTOS, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "insertIntoHierarchyDetails(Map<String, Long>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.updateIntoHierarchyDetails(hierarchyInfoDTOS, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void insertOrUpdateIntoHierarchyDetails(List<KnHierarchyInfoDTO> hierarchyInfoDTOS, String xdmsHome, int corpId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "insertIntoHierarchyDetails(Map<String, Long>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.insertOrUpdateIntoHierarchyDetails(hierarchyInfoDTOS, corpId, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void insertIntoHierarchyDepth(List<KnHierarchyDepthInfoDTO> hierarchyInfoDTOS, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "insertIntoHierarchyDepth(Map<String, Long>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.insertIntoHierarchyDepth(hierarchyInfoDTOS, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void updateIntoHierarchyDepth(List<KnHierarchyDepthInfoDTO> hierarchyInfoDTOS, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "updateIntoHierarchyDepth(Map<String, Long>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.insertIntoHierarchyDepth(hierarchyInfoDTOS, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void insertOrUpdateIntoHierarchyDepth(List<KnHierarchyDepthInfoDTO> hierarchyInfoDTOS, String xdmsHome, int corpId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "updateIntoHierarchyDepth(Map<String, Long>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.insertOrUpdateIntoHierarchyDepth(hierarchyInfoDTOS, corpId, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * Update hierarchy attributes like name, alias etc.
     */
    public void updateHierarchyAttributes(KnModifiedIdDetailsListDTO modifiedDetail, String xdmsHome, boolean isAliasUpdateRequest , KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "updateIntoHierarchyDepth(Map<String, Long>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.updateHierarchyAttributes(modifiedDetail, isAliasUpdateRequest, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public List<KnDeploySiteInfoDTO> getDeploySiteInfo(String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "getDeploySiteInfo(int, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            return xdmDAO.getDeploySiteInfo(persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * Prepares a map of hierarchy details and depth information for the given added child relation.
     * This method processes the hierarchy tree structure and generates two lists:
     * 1. A list of hierarchy information DTOs for the branches.
     * 2. A list of hierarchy depth information DTOs for the new branches.
     * <p>
     * The result is returned as a map containing these two lists.
     *
     * @param addedChildRelation          The added child relation containing the hierarchy tree structure to process.
     * @param corpId                      The corporate ID associated with the hierarchy.
     * @param parentAncestorID            The hierarchy ID of the parent ancestor node.
     * @param getAllExistingDescendantIDs A set of existing descendant hierarchy IDs to validate against.
     * @param ancestorIdWithDepth         A map to track ancestor IDs and their corresponding depths.
     * @return A map containing two lists:
     * - `HIERARCHY_DETAILS_LIST`: List of `KnHierarchyInfoDTO` objects representing the hierarchy details.
     * - `HIERARCHY_DEPTH_LIST`: List of `KnHierarchyDepthInfoDTO` objects representing the hierarchy depth information.
     * @throws UnsupportedEncodingException If an encoding error occurs while processing the hierarchy.
     * @throws NoSuchAlgorithmException     If a required cryptographic algorithm is not available.
     */
    public Map<String, List<?>> prepareHierarchyDetailsDTOListWithExtIdMap(KnAddedChildRelationDTO addedChildRelation, String corpId, String parentAncestorID, Set<String> getAllExistingDescendantIDs,
                                                                           Map<String, Integer> ancestorIdWithDepth, Map<String,List<String>> addedGeoCodeMap,

                                                                           Map<String, String> hierarchyIdNameMap, Map<String,List<String>> removedGeoCodeMap ,String xdmsHome, KnPersisterTxn persisterTxn) throws UnsupportedEncodingException, NoSuchAlgorithmException, KnCorpBOException, KnDAOException {
        String methodName = "prepareHierarchyDetailsDTOListWithExtIdMap(KnAddedChildRelationDTO, String)";
        knLogger.info(methodName, "ENTRY :");
        List<KnHierarchyInfoDTO> hierarchyInfoDTOS = new ArrayList<>();
        List<KnHierarchyDepthInfoDTO> depthList = new ArrayList<>();
        Map<String, KnHierarchyInfoDTO> extIdToDtoMap = new HashMap<>();
        long currentTime = System.currentTimeMillis();

        // Traverse and prepare hierarchy info for branches only
        if (addedChildRelation != null && addedChildRelation.getAddedChildDetailsList() != null) {
            traverseAndPrepareHierarchyInfoForBranches(addedChildRelation.getAddedChildDetailsList(), hierarchyInfoDTOS, extIdToDtoMap,

                    currentTime, corpId, getAllExistingDescendantIDs,addedGeoCodeMap, hierarchyIdNameMap,removedGeoCodeMap, xdmsHome, persisterTxn);


        }

        // Prepare depth list for the new branches
        if (addedChildRelation != null) {
            depthList = prepareModifyHierarchyDepthList(addedChildRelation.getAddedChildDetailsList(), extIdToDtoMap, currentTime,
                    parentAncestorID, ancestorIdWithDepth, corpId, xdmsHome, persisterTxn);
        }

        // Prepare result map
        Map<String, List<?>> result = new ConcurrentHashMap<>();
        result.put(HIERARCHY_DETAILS_LIST, hierarchyInfoDTOS);
        result.put(HIERARCHY_DEPTH_LIST, depthList);

        knLogger.info(methodName, "Exit :");

        return result;
    }

    /*
        * Traverses the hierarchy tree and prepares KnHierarchyInfoDTO for branches only.
        * New hierarchy IDs are generated for new nodes (not present in allExistingDescendantIDs).
     */
    private void traverseAndPrepareHierarchyInfoForBranches(KnIdDetailsListDTO nodeList, List<KnHierarchyInfoDTO> hierarchyInfoDTOS,
                                                            Map<String, KnHierarchyInfoDTO> extIdToDtoMap, long currentTime,
                                                            String corpId, Set<String> allExistingDescendantIDs, Map<String,List<String>> addedGeoCodeMap,

                                                            Map<String, String> hierarchyIdNameMap, Map<String,List<String>> removedGeoCodeMap, String xdmsHome, KnPersisterTxn persisterTxn) throws UnsupportedEncodingException, NoSuchAlgorithmException, KnCorpBOException {

        final String methodName = "traverseAndPrepareHierarchyInfoForBranches";
        knLogger.debug(methodName, "ENTRY :");
        if (nodeList == null || nodeList.getIdDetailsDto() == null) return;

        for (KnIdDetailsDTO node : nodeList.getIdDetailsDto()) {
            KnHierarchyInfoDTO hierarchyInfo = new KnHierarchyInfoDTO();
            String hierarchyId = node.getHierarchyId();
            if (!allExistingDescendantIDs.contains(hierarchyId)) {
                knLogger.info(methodName, "New node detected with extId: " + node.getExtId() + ", generating new hierarchyId.");
                if (!isEightDigitNumericId(hierarchyId)) {
                    hierarchyId = generateNextHierarchyId(xdmsHome, persisterTxn);
                }
                String exId = node.getExtId();
                if (node.getExtId() == null || node.getExtId().isEmpty()) {
                    exId = hierarchyId; // Fallback to idName if extId is not provided, or you can choose to generate a new exId as needed
                }
                hierarchyInfo.setExternalHierarchyId(exId);
                allExistingDescendantIDs.add(hierarchyId);
                node.setHierarchyId(hierarchyId);
            } else {
                hierarchyInfo.setExternalHierarchyId(node.getExtId());
            }
            hierarchyInfo.setCorpId(corpId);
            hierarchyInfo.setHierarchyId(hierarchyId);
            hierarchyInfo.setHierarchyName(node.getIdName());
            hierarchyInfo.setHierarchyAlias(node.getAlias());
            hierarchyInfo.setCreationTime(currentTime);
            hierarchyInfo.setUpdateTime(currentTime);

            hierarchyInfoDTOS.add(hierarchyInfo);
            hierarchyIdNameMap.putIfAbsent(node.getIdName(), hierarchyId);
            extIdToDtoMap.put(node.getHierarchyId(), hierarchyInfo);
            // Handle added geo codes for the node
            if (node.getAddedGeoCode() != null && !node.getAddedGeoCode().isEmpty()) {
                addedGeoCodeMap.computeIfAbsent(hierarchyInfo.getHierarchyId(), k -> new ArrayList<>()).addAll(node.getAddedGeoCode());
            }
            // Handle removed geo codes for the node
            if (node.getRemovedGeoCode() != null && !node.getRemovedGeoCode().isEmpty()) {
                removedGeoCodeMap.computeIfAbsent(hierarchyInfo.getHierarchyId(), k -> new ArrayList<>()).addAll(node.getRemovedGeoCode());
            }

            // Recursively process child nodes

            traverseAndPrepareHierarchyInfoForBranches(node.getIdDetailsListDto(), hierarchyInfoDTOS, extIdToDtoMap, currentTime, corpId, allExistingDescendantIDs,addedGeoCodeMap,hierarchyIdNameMap
                   , removedGeoCodeMap, xdmsHome, persisterTxn);

        }
    }

    public void insertIntoHierarchyGeocodeMap(List<KnCorpHierarchyGeocodeMapDTO> geocodeMapDTOS,String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "getDeploySiteInfo(String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.insertIntoHierarchyGeocodeMap(geocodeMapDTOS, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /*
        * Fetches all existing descendant hierarchy IDs from the database.
     */
    public Set<String> getAllExistingDescendantIDs(String XDMsHome, int corpId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "getAllExistingDescendantIDs(String XDMsHome, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(XDMsHome);
            return xdmDAO.fetchExistingDescendantHierarchyIds(corpId, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, Integer> fetchAllHierarchyDepth(String corpId, String descendantId, String xdmHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmHome);
            return xdmDAO.fetchAllHierarchyWithDepth(corpId, descendantId, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, List<?>> prepareHierarchyDetailsDTOListWithExtIdMap(KnIdDetailsListDTO idDetailsListDTO, String corpId,
                                                                           String xdmsHome, KnPersisterTxn persisterTxn)
            throws UnsupportedEncodingException, NoSuchAlgorithmException, KnCorpBOException {
        return prepareHierarchyDetailsDTOListWithExtIdMap(idDetailsListDTO, corpId, null, xdmsHome, persisterTxn);
    }

    public Map<String, List<?>> prepareHierarchyDetailsDTOListWithExtIdMap(KnIdDetailsListDTO idDetailsListDTO, String corpId,
                                                                           String transactionId, String xdmsHome, KnPersisterTxn persisterTxn)
            throws UnsupportedEncodingException, NoSuchAlgorithmException, KnCorpBOException {
        String methodName = "prepareHierarchyDetailsDTOListWithExtIdMap(KnIdDetailsListDTO, String)";
        List<KnHierarchyInfoDTO> hierarchyInfoDTOS = new ArrayList<>();
        List<KnCorpHierarchyGeocodeMapDTO> geoCodeMapDTOS = new ArrayList<>();
        Map<java.lang.String, KnHierarchyInfoDTO> extIdToDtoMap = new HashMap<>();
        long currentTime = System.currentTimeMillis();

        traverseAndPrepareHierarchyInfo(idDetailsListDTO, hierarchyInfoDTOS, geoCodeMapDTOS, extIdToDtoMap,
                currentTime, corpId, xdmsHome, persisterTxn);

        Map<java.lang.String, List<?>> result = new HashMap<>();
        String rootNodeHierarchyId = generateNextHierarchyId(xdmsHome, persisterTxn);
        List<KnHierarchyDepthInfoDTO> depthList = prepareHierarchyDepthList(idDetailsListDTO, extIdToDtoMap, currentTime, corpId, rootNodeHierarchyId, transactionId);
        prepareRootEntryForHierarchyDetails(rootNodeHierarchyId, hierarchyInfoDTOS, corpId, currentTime);
        result.put(HIERARCHY_DETAILS_LIST, hierarchyInfoDTOS);
        result.put(HIERARCHY_DEPTH_LIST, depthList);
        result.put(HIERARCHY_GEO_CODE_LIST, geoCodeMapDTOS);
        return result;
    }

    /**
     * Prepares a root entry for the hierarchy details table.
     * This entry represents a special root ancestor node that serves as the parent for all root-level nodes.
     * It ensures that the rootNodeHierarchyId referenced in the depth table exists in the details table.
     *
     * @param rootNodeHierarchyId   The unique hierarchy ID for the root ancestor node.
     * @param hierarchyInfoDTOS     The list to which the root entry will be added.
     * @param corpId                The corporate ID associated with the hierarchy.
     * @param currentTime           The current timestamp to set creation and update times.
     */
    private void prepareRootEntryForHierarchyDetails(String rootNodeHierarchyId, List<KnHierarchyInfoDTO> hierarchyInfoDTOS,
                                                     String corpId, long currentTime) {
        String methodName = "prepareRootEntryForHierarchyDetails";
        knLogger.info(methodName, "Adding root ancestor entry to hierarchy details for corpId: " + corpId + " with hierarchyId: " + rootNodeHierarchyId);

        KnHierarchyInfoDTO rootHierarchyNode = new KnHierarchyInfoDTO();
        rootHierarchyNode.setCorpId(corpId);
        rootHierarchyNode.setHierarchyId(rootNodeHierarchyId);
        rootHierarchyNode.setExternalHierarchyId(rootNodeHierarchyId);
        rootHierarchyNode.setHierarchyName("ROOT_ANCESTOR"+corpId);
        rootHierarchyNode.setHierarchyAlias("ROOT_ANCESTOR"+corpId);
        rootHierarchyNode.setCreationTime(currentTime);
        rootHierarchyNode.setUpdateTime(currentTime);

        hierarchyInfoDTOS.add(rootHierarchyNode);
        knLogger.info(methodName, "Root ancestor entry added successfully to hierarchy details", rootHierarchyNode);
    }

    private void traverseAndPrepareHierarchyInfo(
            KnIdDetailsListDTO nodeList,
            List<KnHierarchyInfoDTO> hierarchyInfoDTOS,
            List<KnCorpHierarchyGeocodeMapDTO> geoCodeMapDTOS, Map<String, KnHierarchyInfoDTO> extIdToDtoMap,
            long currentTime,
            String corpId,
            String xdmsHome,
            KnPersisterTxn persisterTxn) throws UnsupportedEncodingException, NoSuchAlgorithmException, KnCorpBOException {
        if (nodeList == null || nodeList.getIdDetailsDto() == null) return;
        for (KnIdDetailsDTO node : nodeList.getIdDetailsDto()) {
            KnHierarchyInfoDTO hierarchyInfo = new KnHierarchyInfoDTO();
            String hierarchyId = generateNextHierarchyId(xdmsHome, persisterTxn);
            hierarchyInfo.setCorpId(corpId);
            hierarchyInfo.setHierarchyId(hierarchyId);
            node.setHierarchyId(hierarchyId);
            String extIdKey;
            if (node.getExtId() != null && !node.getExtId().isEmpty()) {
                hierarchyInfo.setExternalHierarchyId(node.getExtId());
                extIdKey = node.getExtId();
            } else {
                knLogger.debug("setting here ", hierarchyId);
                hierarchyInfo.setExternalHierarchyId(hierarchyId);
                extIdKey = hierarchyId;
                node.setExtId(hierarchyId); // FIX: Store generated ID back to node
            }
            hierarchyInfo.setHierarchyName(node.getIdName());
            hierarchyInfo.setHierarchyAlias(node.getAlias()); // or node.getAlias() if available
            //hierarchyInfo.setHierarchyType(node.getIdType() != null ? Byte.parseByte(node.getIdType()) : null);
            hierarchyInfo.setSegmentIndicator(node.getSegmentIndicator());
            //hierarchyInfo.setSubScrDefPttRadio(node.getSubScrDefPttRadio());
            //hierarchyInfo.setFeatureVersion(node.getFeatureVersion());
            hierarchyInfo.setCreationTime(currentTime);
            hierarchyInfo.setUpdateTime(currentTime);
            //hierarchyInfo.setStatus(node.getStatus());
            //hierarchyInfo.setCustomField1(node.getCustomField1());
            //hierarchyInfo.setCustomField2(node.getCustomField2());

            hierarchyInfoDTOS.add(hierarchyInfo);
            extIdToDtoMap.put(extIdKey, hierarchyInfo);
            List<String> geoCodes = node.getGeoCode();
            if (geoCodes != null && !geoCodes.isEmpty()) {
                for (String geoCode : geoCodes) {
                    KnCorpHierarchyGeocodeMapDTO geoCodeMapDTO = new KnCorpHierarchyGeocodeMapDTO();
                    geoCodeMapDTO.setCorpId(Integer.parseInt(corpId));
                    geoCodeMapDTO.setHierarchyId(hierarchyInfo.getHierarchyId());
                    geoCodeMapDTO.setGeocode(geoCode);
                    geoCodeMapDTO.setCreationTime(currentTime);
                    geoCodeMapDTO.setUpdateTime(currentTime);
                    geoCodeMapDTOS.add(geoCodeMapDTO);
                }
            }
            traverseAndPrepareHierarchyInfo(node.getIdDetailsListDto(), hierarchyInfoDTOS, geoCodeMapDTOS, extIdToDtoMap,
                    currentTime, corpId, xdmsHome, persisterTxn);
        }
    }

    private boolean isEightDigitNumericId(String hierarchyId) {
        return hierarchyId != null && hierarchyId.matches("\\d{8}");
    }

    public List<KnHierarchyDepthInfoDTO> prepareHierarchyDepthList(
            KnIdDetailsListDTO idDetailsListDTO,
            Map<String, KnHierarchyInfoDTO> extIdToDtoMap,
            long currentTime,
            String corpId,
            String rootNodeHierarchyId) {
        return prepareHierarchyDepthList(idDetailsListDTO, extIdToDtoMap, currentTime, corpId, rootNodeHierarchyId, null);
    }

    public List<KnHierarchyDepthInfoDTO> prepareHierarchyDepthList(
            KnIdDetailsListDTO idDetailsListDTO,
            Map<String, KnHierarchyInfoDTO> extIdToDtoMap,
            long currentTime,
            String corpId,
            String rootNodeHierarchyId,
            String transactionId) {
        List<KnHierarchyDepthInfoDTO> depthList = new ArrayList<>();

        // First, add root node entries (with ROOT_ANCESTOR_ID)
        addRootNodeEntries(idDetailsListDTO, extIdToDtoMap, depthList, currentTime, corpId, rootNodeHierarchyId, transactionId);

        // Then traverse and create ancestor-descendant relationships
        traverseForDepth(idDetailsListDTO, new ArrayList<>(), extIdToDtoMap, depthList, currentTime, corpId);

        knLogger.info(" extIdToDtoMap ", extIdToDtoMap);
        knLogger.info(" depthList ", depthList);
        knLogger.info("Total depth entries created: " + depthList.size());
        return depthList;
    }

    /**
     * Prepares a list of hierarchy depth information for modified nodes.
     * This method processes a hierarchy tree structure and updates the depth list
     * with entries for each node, starting from the given parent ancestor node.
     *
     * @param idDetailsListDTO    The hierarchy tree structure containing nodes to process.
     * @param extIdToDtoMap       A map of hierarchy IDs to their corresponding DTOs.
     * @param currentTime         The current timestamp to set creation and update times.
     * @param parentAncestorID    The hierarchy ID of the parent ancestor node for the current level.
     * @param ancestorIdWithDepth A map to track ancestor IDs and their corresponding depths.
     * @return A list of `KnHierarchyDepthInfoDTO` objects representing the updated hierarchy depth information.
     */
    public List<KnHierarchyDepthInfoDTO> prepareModifyHierarchyDepthList(KnIdDetailsListDTO idDetailsListDTO, Map<String, KnHierarchyInfoDTO> extIdToDtoMap,
                                                                         long currentTime, String parentAncestorID, Map<String, Integer> ancestorIdWithDepth,
                                                                         String corpId ,String xDmsHome, KnPersisterTxn presisterTxn) throws KnDAOException {
        final String methodName = "prepareModifyHierarchyDepthList";
        knLogger.info(methodName, " ENTRY : ");
        List<KnHierarchyDepthInfoDTO> depthList = new ArrayList<>();

        // First, add node entries (with ROOT_ANCESTOR_ID)
        updateRootPathEntries(idDetailsListDTO, extIdToDtoMap, depthList, parentAncestorID, currentTime, ancestorIdWithDepth,
                corpId, xDmsHome, presisterTxn);

        knLogger.info(methodName, " DepthList ", depthList.toString());
        knLogger.info(methodName, "Total depth entries created: " + depthList.size());
        knLogger.info(methodName, " EXIT : ");

        return depthList;
    }

    /**
     * Updates the root path entries in the hierarchy depth list.
     * This method processes a hierarchy tree structure and creates depth entries for each node,
     * starting from the given ancestor node. It recursively traverses the tree and updates the depth list.
     *
     * @param idDetailsListDTO    The hierarchy tree structure containing nodes to process.
     * @param extIdToDtoMap       A map of hierarchy IDs to their corresponding DTOs.
     * @param depthList           The list to which depth entries will be added.
     * @param ancestorHierarchyId The hierarchy ID of the ancestor node for the current level.
     * @param currentTime         The current timestamp to set creation and update times.
     * @param ancestorIdWithDepth A map to track ancestor IDs and their corresponding depths.
     */
    private void updateRootPathEntries(KnIdDetailsListDTO idDetailsListDTO, Map<String, KnHierarchyInfoDTO> extIdToDtoMap, List<KnHierarchyDepthInfoDTO> depthList,
                                       String ancestorHierarchyId, long currentTime, Map<String, Integer> ancestorIdWithDepth,
                                       String corpId ,String xDmsHome, KnPersisterTxn presisterTxn) throws KnDAOException {
        final String methodName = "addModifyRootNodeEntries";

        // Check if the input hierarchy tree is null or empty, and return if so.
        if (idDetailsListDTO == null || idDetailsListDTO.getIdDetailsDto() == null || idDetailsListDTO.getIdDetailsDto().length == 0) {
            return;
        }

        // Retrieve the list of nodes at the current level.
        KnIdDetailsDTO[] idDetailsDto = idDetailsListDTO.getIdDetailsDto();
        knLogger.debug(methodName, "Processing level with ancestorHierarchyId: " + ancestorHierarchyId + " and number of nodes: ",idDetailsDto);
        // Iterate through each node in the current level.
        for (KnIdDetailsDTO idDto : idDetailsDto) {
            Map<String, Integer> updatedAncestorIdWithDepth = new HashMap<>(ancestorIdWithDepth);
            // Retrieve the corresponding DTO for the current node using its hierarchy ID.
            KnHierarchyInfoDTO knHierarchyInfoDTO = extIdToDtoMap.get(idDto.getHierarchyId());
            knLogger.debug(methodName, "Processing node with hierarchyId: " + idDto.getHierarchyId() + ", extId: " + idDto.getExtId() + ", retrieved DTO: ", knHierarchyInfoDTO);
            if (knHierarchyInfoDTO == null) {
                // Log a warning if no DTO is found for the current node.
                knLogger.warn("prepareChildDepthInfoDTO", "No DTO found for child node extId: " + idDto.getExtId());
                continue;
            }

            // Create a new depth entry for the current node with the given ancestorHierarchyId.
            KnHierarchyDepthInfoDTO depthInfo = new KnHierarchyDepthInfoDTO();
            depthInfo.setCorpId(knHierarchyInfoDTO.getCorpId());
            depthInfo.setAncestorHierId(ancestorHierarchyId);
            depthInfo.setDescendantHierId(knHierarchyInfoDTO.getHierarchyId());
            depthInfo.setDepth(1); // Depth is set to 1 for direct descendants.
            depthInfo.setCreationTime(currentTime);
            depthInfo.setUpdateTime(currentTime);
            depthList.add(depthInfo);

            // Prepare depth entries for child nodes of the current node.
            prepareChildDepthInfoDTO(knHierarchyInfoDTO.getCorpId(), knHierarchyInfoDTO.getHierarchyId(), ancestorHierarchyId, currentTime, depthList, ancestorIdWithDepth);

            // Update the ancestorIdWithDepth map for the current node.
            updatedAncestorIdWithDepth.put(depthInfo.getDescendantHierId(), 0);
            updatedAncestorIdWithDepth.putIfAbsent(ancestorHierarchyId, 1);

            Map<String, Integer> ancestorIdWithDepthMap = new HashMap<>(updatedAncestorIdWithDepth);
            // Log the addition of the root entry for the current node.
            knLogger.info(methodName, "Added root entry for: " + idDto.getExtId() + " (hierarchyId: " + knHierarchyInfoDTO.getHierarchyId() + ")");

            knLogger.debug(methodName, "Current ancestorIdWithDepth ", ancestorIdWithDepth, "for HierarchyId:::", knHierarchyInfoDTO.getHierarchyId());
            // If the current node has child nodes, recursively process them.
            if (idDto.getIdDetailsListDto() != null) {
                recursivelyChildDepth(idDto.getIdDetailsListDto(), extIdToDtoMap, depthList, currentTime, knHierarchyInfoDTO.getHierarchyId(),ancestorIdWithDepthMap);
            }
            // Process depth calculation for child nodes of the current node. This will create depth entries for all descendants of the current node.
            processDepthCalculationForChild(knHierarchyInfoDTO.getHierarchyId(), depthList, idDto.getHierarchyId(),ancestorIdWithDepth,
                    corpId, ancestorHierarchyId, xDmsHome, presisterTxn);
        }

        // Log the completion of processing for the current level.
        knLogger.info(methodName, " Exit : With depthList size:", depthList.size());
    }


    private void recursivelyChildDepth(KnIdDetailsListDTO idDetailsListDTO, Map<String, KnHierarchyInfoDTO> extIdToDtoMap,
                                       List<KnHierarchyDepthInfoDTO> depthList, long currentTime, String ancestorId,Map<String, Integer> ancestorIdWithDepth) {
        final String methodName = "recursivelyChildDepth";
        knLogger.debug(methodName, "ENTRY : for ancestorId: " + ancestorId);

        if (idDetailsListDTO == null || idDetailsListDTO.getIdDetailsDto() == null) {
            return;
        }

        // Keep recursion branch-local state to avoid contaminating sibling traversal.
        Map<String, Integer> levelAncestorMap = new HashMap<>(ancestorIdWithDepth);
        levelAncestorMap.replaceAll((entryKey, entryValue) -> entryValue + 1);
        knLogger.debug(methodName, "Updated ancestorIdWithDepth for ancestorId: " + ancestorId, levelAncestorMap);

        for (KnIdDetailsDTO idDto : idDetailsListDTO.getIdDetailsDto()) {
            KnHierarchyInfoDTO knHierarchyInfoDTO = extIdToDtoMap.get(idDto.getHierarchyId());
            if (knHierarchyInfoDTO == null) {
                knLogger.warn("addRootNodeEntries", "No DTO found for node extId: " + idDto.getExtId());
                continue;
            }

            KnHierarchyDepthInfoDTO depthInfo = new KnHierarchyDepthInfoDTO();
            depthInfo.setCorpId(knHierarchyInfoDTO.getCorpId());
            depthInfo.setAncestorHierId(ancestorId);
            depthInfo.setDescendantHierId(knHierarchyInfoDTO.getHierarchyId());
            depthInfo.setDepth(1);
            depthInfo.setCreationTime(currentTime);
            depthInfo.setUpdateTime(currentTime);
            depthList.add(depthInfo);

            processChildDepthInfoDTO(knHierarchyInfoDTO.getCorpId(), depthInfo.getDescendantHierId(), ancestorId, currentTime, depthList, levelAncestorMap);

            // Log the addition of the root entry for the current node.
            knLogger.info("addRootNodeEntries", "Added root entry for: " + idDto.getExtId() + " (hierarchyId: " + knHierarchyInfoDTO.getHierarchyId() + ")");

            // Recursively add root entries for child nodes with isolated map state.
            if (idDto.getIdDetailsListDto() != null) {
                Map<String, Integer> childAncestorMap = new HashMap<>(levelAncestorMap);
                childAncestorMap.put(depthInfo.getDescendantHierId(), 0);
                childAncestorMap.putIfAbsent(ancestorId, 1);
                recursivelyChildDepth(idDto.getIdDetailsListDto(), extIdToDtoMap, depthList, currentTime, depthInfo.getDescendantHierId(), childAncestorMap);
            }
        }
        knLogger.info(methodName, " Exit : for ancestorId: " + ancestorId);
    }

    private int normalizeAncestorDepth(Integer depth) {
        // Keep the computed traversal distance; only guard null/invalid values.
        return (depth == null || depth < 1) ? 1 : depth;
    }

    private void processChildDepthInfoDTO(String corpId, String descendantId, String ancestorHierarchyId, long currentTime,
                                          List<KnHierarchyDepthInfoDTO> depthList, Map<String, Integer> ancestorIdWithDepth) {
        // Return early if the ancestorIdWithDepth map is null or empty.
        if (ancestorIdWithDepth == null || ancestorIdWithDepth.isEmpty()) {
            return;
        }

        // Iterate through each entry in the ancestorIdWithDepth map.
        for (Map.Entry<String, Integer> entry : ancestorIdWithDepth.entrySet()) {
            // Skip if the ancestor is the same as the current ancestorHierarchyId,
            // as that relationship is already added.
            if (entry.getKey().equals(ancestorHierarchyId)) {
                continue;
            }

            // Create a new depth entry for the current ancestor.
            KnHierarchyDepthInfoDTO depthInfo = new KnHierarchyDepthInfoDTO();
            depthInfo.setCorpId(corpId);
            depthInfo.setAncestorHierId(entry.getKey());
            depthInfo.setDescendantHierId(descendantId);
            depthInfo.setDepth(normalizeAncestorDepth(entry.getValue()));
            depthInfo.setCreationTime(currentTime);
            depthInfo.setUpdateTime(currentTime);

            // Add the depth entry to the depthList.
            depthList.add(depthInfo);
        }
    }

    /**
     * Processes depth calculation for child nodes in a hierarchy.
     * This method calculates and adds depth entries for child nodes based on their ancestor relationships.
     *
     * @param mostCurrentAncestorId The hierarchy ID of the most current ancestor node.
     * @param depthList             The list to which new depth entries will be added.
     * @param hierarchyId           The hierarchy ID of the current node being processed.
     * @param ancestorIdWithDepth   A map containing ancestor hierarchy IDs and their corresponding depths.
     * @param corpId                The corporate ID associated with the hierarchy.
     * @param xDmsHome              The PTT server ID.
     * @param presisterTxn          The transaction context for database operations.
     * @throws KnDAOException If an error occurs while fetching child hierarchy depth information.
     */
    private void processDepthCalculationForChild(String mostCurrentAncestorId,List<KnHierarchyDepthInfoDTO> depthList, String hierarchyId, Map<String, Integer> ancestorIdWithDepth,
                                                 String corpId, String ancestorHierarchyId, String xDmsHome, KnPersisterTxn presisterTxn) throws KnDAOException {
        String methodName = "processDepthCalculationForChild";
        knLogger.info(methodName, " ENTRY : for hierarchyId: " + hierarchyId ," with mostCurrentAncestorId: " + mostCurrentAncestorId);
        ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xDmsHome);
        List<KnHierarchyDepthInfoDTO> childHierarchyDepthInfo = xdmDAO.getChildHierarchyDepthInfo(hierarchyId, corpId, presisterTxn);
        knLogger.debug(methodName, "ChildDepthInfo", childHierarchyDepthInfo);
        knLogger.debug(methodName, "AncestorIdWithDepth", ancestorIdWithDepth);
        if (childHierarchyDepthInfo != null && !childHierarchyDepthInfo.isEmpty()) {
            Map<String, Integer> normalizedAncestorIdWithDepth = new HashMap<>(ancestorIdWithDepth);
            // Ensure the current ancestorHierarchyId is included in the map with a depth of 0, and the most current ancestor is included with a depth of 1 if not already present.
            normalizedAncestorIdWithDepth.putIfAbsent(ancestorHierarchyId, 0);
            normalizedAncestorIdWithDepth.putIfAbsent(hierarchyId, 1);
            for (Map.Entry<String, Integer> entry : normalizedAncestorIdWithDepth.entrySet()) {
                String ancestorId = entry.getKey();
                Integer depth = entry.getValue();
                knLogger.info(methodName, "Processing ancestorId: " + ancestorId + " with depth: " + depth);
                if (ancestorId.equals(mostCurrentAncestorId)) {
                    knLogger.info(methodName, "Skipping ancestorId: " + ancestorId + " as it is the same as ancestorHierarchyId: " + mostCurrentAncestorId);
                    continue;
                }
                for (KnHierarchyDepthInfoDTO childDepthInfo : childHierarchyDepthInfo) {
                    KnHierarchyDepthInfoDTO newDepthInfo = new KnHierarchyDepthInfoDTO();
                    newDepthInfo.setCorpId(corpId);
                    newDepthInfo.setAncestorHierId(ancestorId);
                    newDepthInfo.setDescendantHierId(childDepthInfo.getDescendantHierId());
                    /* The new depth is calculated as the depth from the ancestor to the current node (entry.getValue()) plus the depth from the current node to the child (childDepthInfo.getDepth()),
                    plus 1 to account for the direct relationship between the ancestor and the current node.
                     */
                    newDepthInfo.setDepth((entry.getValue() + 1 )+ childDepthInfo.getDepth());
                    newDepthInfo.setCreationTime(System.currentTimeMillis());
                    newDepthInfo.setUpdateTime(System.currentTimeMillis());
                    knLogger.debug(methodName, "Adding new depth entry: " + newDepthInfo);
                    depthList.add(newDepthInfo);
                }
            }

        }
        knLogger.info(methodName, "Exit: Completed depth calculation for child nodes of hierarchyId: " + hierarchyId);
    }

    /**
     * Prepares child depth information for a given descendant node.
     * This method iterates through the ancestorIdWithDepth map and creates depth entries
     * for each ancestor, excluding the current ancestorHierarchyId. The depth entries
     * are added to the provided depthList.
     *
     * @param corpId              The corporate ID associated with the hierarchy.
     * @param descendantId        The hierarchy ID of the descendant node.
     * @param currentTime         The current timestamp to set creation and update times.
     * @param depthList           The list to which depth entries will be added.
     * @param ancestorIdWithDepth A map containing ancestor hierarchy IDs and their corresponding depths.
     */
    private void prepareChildDepthInfoDTO(String corpId, String descendantId, String ancestorHierarchyId, long currentTime,
                                          List<KnHierarchyDepthInfoDTO> depthList, Map<String, Integer> ancestorIdWithDepth) {
        // Return early if the ancestorIdWithDepth map is null or empty.
        if (ancestorIdWithDepth == null || ancestorIdWithDepth.isEmpty()) {
            return;
        }

        // Iterate through each entry in the ancestorIdWithDepth map.
        for (Map.Entry<String, Integer> entry : ancestorIdWithDepth.entrySet()) {
            // Skip if the ancestor is the same as the current ancestorHierarchyId,
            // as that relationship is already added.
            if (entry.getKey().equals(ancestorHierarchyId)) {
                continue;
            }

            // Create a new depth entry for the current ancestor.
            KnHierarchyDepthInfoDTO depthInfo = new KnHierarchyDepthInfoDTO();
            depthInfo.setCorpId(corpId);
            depthInfo.setAncestorHierId(entry.getKey());
            depthInfo.setDescendantHierId(descendantId);
            depthInfo.setDepth(entry.getValue()+1);
            depthInfo.setCreationTime(currentTime);
            depthInfo.setUpdateTime(currentTime);

            // Add the depth entry to the depthList.
            depthList.add(depthInfo);
        }
    }

    /**
     * Add depth entries for root nodes with ancestorHierId = ROOT_ANCESTOR_ID
     * This helps identify which nodes are at the root of the hierarchy
     * ROOT_ANCESTOR_ID is a special constant (all zeros) that marks nodes with no parent
     */
    private void addRootNodeEntries(
            KnIdDetailsListDTO idDetailsListDTO,
            Map<String, KnHierarchyInfoDTO> extIdToDtoMap,
            List<KnHierarchyDepthInfoDTO> depthList,
            long currentTime,
            String corpId,
            String rootNodeHierarchyId,
            String transactionId) {
        if (idDetailsListDTO == null || idDetailsListDTO.getIdDetailsDto() == null) {
            knLogger.debug("addRootNodeEntries", "No root nodes found");
            return;
        }

        KnIdDetailsDTO[] rootNodes = idDetailsListDTO.getIdDetailsDto();
        knLogger.info("addRootNodeEntries", "Adding root node entries for " + rootNodes.length + " root node(s)");

        for (KnIdDetailsDTO rootNode : rootNodes) {
            String rootExtId = rootNode.getExtId();
            KnHierarchyInfoDTO rootDto = extIdToDtoMap.get(rootExtId);

            if (rootDto == null) {
                knLogger.warn("addRootNodeEntries", "No DTO found for root node extId: " + rootExtId);
                continue;
            }

            // Create depth entry with ROOT_ANCESTOR_ID to mark this as a root node
            KnHierarchyDepthInfoDTO rootDepthInfo = new KnHierarchyDepthInfoDTO();
            rootDepthInfo.setCorpId(corpId);
            rootDepthInfo.setAncestorHierId(rootNodeHierarchyId); // Special constant indicates this is a root node
            rootDepthInfo.setDescendantHierId(rootDto.getHierarchyId());
            rootDepthInfo.setDepth(0); // Root node has depth 0
            rootDepthInfo.setCreationTime(currentTime);
            rootDepthInfo.setUpdateTime(currentTime);
            // Store transactionId in customField1 so it can be retrieved later for duplicate request detection
            if (transactionId != null && !transactionId.isEmpty()) {
                rootDepthInfo.setCustomField1(transactionId);
            }
            depthList.add(rootDepthInfo);

            knLogger.debug("addRootNodeEntries", "Added root entry for: " + rootExtId + " (hierarchyId: " + rootDto.getHierarchyId() + ")", rootDepthInfo);
        }
    }

    private void traverseForDepth(
            KnIdDetailsListDTO nodeList,
            List<String> ancestorExtIds,
            Map<String, KnHierarchyInfoDTO> extIdToDtoMap,
            List<KnHierarchyDepthInfoDTO> depthList,
            long currentTime,
            String corpId
    ) {
        if (nodeList == null || nodeList.getIdDetailsDto() == null) {
            knLogger.debug("traverseForDepth", "nodeList is null or empty");
            return;
        }
        for (KnIdDetailsDTO node : nodeList.getIdDetailsDto()) {
            String currentExtId = node.getExtId();
            KnHierarchyInfoDTO currentDto = extIdToDtoMap.get(currentExtId);
            if (currentDto == null) {
                knLogger.warn("traverseForDepth", "No DTO found for extId: " + currentExtId);
                continue;
            }
            // For each ancestor, create a depth entry
            for (int i = 0; i < ancestorExtIds.size(); i++) {
                String ancestorExtId = ancestorExtIds.get(i);
                KnHierarchyInfoDTO ancestorDto = extIdToDtoMap.get(ancestorExtId);
                if (ancestorDto == null) {
                    knLogger.warn("traverseForDepth", "No DTO found for ancestor extId: " + ancestorExtId);
                    continue;
                }

                int depth = ancestorExtIds.size() - i;
                KnHierarchyDepthInfoDTO depthInfo = new KnHierarchyDepthInfoDTO();
                depthInfo.setCorpId(corpId);
                depthInfo.setAncestorHierId(ancestorDto.getHierarchyId());
                depthInfo.setDescendantHierId(currentDto.getHierarchyId());
                depthInfo.setDepth(depth); // depth = distance from ancestor
                depthInfo.setCreationTime(currentTime);
                depthInfo.setUpdateTime(currentTime);
                depthList.add(depthInfo);
            }

            // Check if node has children
            KnIdDetailsListDTO childList = node.getIdDetailsListDto();
            if (childList != null && childList.getIdDetailsDto() != null && childList.getIdDetailsDto().length > 0) {
                knLogger.debug("traverseForDepth", "Node " + currentExtId + " has " + childList.getIdDetailsDto().length + " children");
            } else {
                knLogger.debug("traverseForDepth", "Node " + currentExtId + " has no children");
            }

            // Recurse for children
            ancestorExtIds.add(currentExtId);
            traverseForDepth(node.getIdDetailsListDto(), ancestorExtIds, extIdToDtoMap, depthList, currentTime, corpId);
            ancestorExtIds.remove(ancestorExtIds.size() - 1);
        }
    }

    public List<String> getExtIdNameInfo(List<String> extIdList, Integer corpId, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "getExtIdNameInfo(List<String>, Integer, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            return xdmDAO.getExtIdNameInfo(extIdList, corpId, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }


    public Map<String,String> getHierarchyNameList(Integer corpId, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName= "getHierarchyNameList(Integer, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            return xdmDAO.getHierarchyIdNameList(corpId, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String,String> getHierarchyNameAndIdList(Integer corpId, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName= "getHierarchyNameList(Integer, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            return xdmDAO.getHierarchyIdNameAndIdList(corpId, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String,String> getRootHierarchyName(Integer corpId, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "getHierarchyNameList(Integer, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            return xdmDAO.getRootHierarchyName(corpId, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, Map<Integer, Integer>> getClusterAndSubscriberCount(Set<String> hierarchyIdList, int corpId, String xdmsHome,
                                                                           KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "getClusterAndSubscriberCount(hierarchyIdList, xdmsHome, persisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            Map<String, Map<Integer, Integer>> stringMapMap = xdmDAO.fetchClusterBasedSubscriberCount(hierarchyIdList, corpId, persisterTxn);
            knLogger.debug(methodName, "Exit ", stringMapMap);
            return stringMapMap;
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public KnHierarchyDepthInfoDTO getRootNodeBasedOnCorpId(Integer corpId, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "getRootNodeBasedOnCorpId(Integer, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            return xdmDAO.getRootNodeBasedOnCorpId(corpId, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, KnHierarchyDepthInfoDTO> getChileNodesBasedOnHierarchyIds(List<String> hierarchyIds, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "getChileNodesBasedOnHierarchyIds(List<String>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            return xdmDAO.getChileNodesBasedOnHierarchyIds(hierarchyIds, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public List<String> getDescendantExtIds(String extId, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "getDescendantExtIds(Integer, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            return xdmDAO.getDescendantExtIds(extId, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * Extracts all external IDs (extId) from the entire hierarchy tree.
     * This method traverses the complete tree structure recursively and collects
     * all extId values into a flat list.
     * <p>
     * Use case: Validation to check if any of these IDs already exist in the system
     * before creating the hierarchy.
     *
     * @param idDetailsListDTO The hierarchy tree structure containing all nodes
     * @return List of all external IDs found in the tree (in traversal order)
     */
    public List<String> extractIdName(KnIdDetailsListDTO idDetailsListDTO) {
        String methodName = "extractIdName(KnIdDetailsListDTO)";
        knLogger.debug(methodName, "ENTRY");
        List<String> extIdNameList = new ArrayList<>();
        if (idDetailsListDTO == null || idDetailsListDTO.getIdDetailsDto() == null) {
            knLogger.warn(methodName, "Input idDetailsListDTO is null or empty");
            return extIdNameList;
        }
        collectExtIdName(idDetailsListDTO, extIdNameList);
        knLogger.debug(methodName, "idName List: " + extIdNameList);
        return extIdNameList;
    }
    /**
     * Extracts all external IDs (extId) from the entire hierarchy tree.
     * This method traverses the complete tree structure recursively and collects
     * all extId values into a flat list.
     * <p>
     * Use case: Validation to check if any of these IDs already exist in the system
     * before creating the hierarchy.
     *
     * @param idDetailsListDTO The hierarchy tree structure containing all nodes
     * @return List of all external IDs found in the tree (in traversal order)
     */
    public List<String> extractAllExtIds(KnIdDetailsListDTO idDetailsListDTO) {
        String methodName = "extractAllExtIds(KnIdDetailsListDTO)";
        knLogger.debug(methodName, "ENTRY");

        List<String> extIdList = new ArrayList<>();

        if (idDetailsListDTO == null || idDetailsListDTO.getIdDetailsDto() == null) {
            knLogger.warn(methodName, "Input idDetailsListDTO is null or empty");
            return extIdList;
        }

        // Recursively collect all extIds
        collectExtIds(idDetailsListDTO, extIdList);

        knLogger.info(methodName, "Extracted " + extIdList.size() + " external IDs from hierarchy tree");
        knLogger.debug(methodName, "ExtId List: " + extIdList);

        return extIdList;
    }
    public List<String> extractAllExtIds(List<KnModifiedIdDetailsListDTO> modifiedIdDetails) {
        String methodName = "extractAllExtIds(List<KnModifiedIdDetailsListDTO>)";
        knLogger.debug(methodName, "ENTRY");

        List<String> extIdList = new ArrayList<>();

        if (modifiedIdDetails == null || modifiedIdDetails.isEmpty()) {
            knLogger.warn(methodName, "Input modifiedIdDetails is null or empty");
            return extIdList;
        }

        for (KnModifiedIdDetailsListDTO modifiedDetail : modifiedIdDetails) {
            if (modifiedDetail.getAddedChildRelation() != null) {
                KnIdDetailsListDTO addedChildDetailsList = modifiedDetail.getAddedChildRelation().getAddedChildDetailsList();
                if (addedChildDetailsList != null) {
                    extIdList.addAll(extractAllExtIds(addedChildDetailsList));
                }
            }
        }

        knLogger.info(methodName, "Extracted " + extIdList.size() + " external IDs from modified hierarchy details");
        knLogger.debug(methodName, "ExtId List: " + extIdList);

        return extIdList;
    }

    public List<String> extractHierarchyIdName(List<KnModifiedIdDetailsListDTO> modifiedIdDetails,Map<String,String> corpHierarchyIds) {
        final String methodName = "extractHierarchyIdName(List<KnModifiedIdDetailsListDTO>)";
        knLogger.info(methodName, "ENTRY");
        List<String> idNameList = new ArrayList<>();
        if (modifiedIdDetails == null || modifiedIdDetails.isEmpty()) {
            return idNameList;
        }
        for (KnModifiedIdDetailsListDTO modifiedDetail : modifiedIdDetails) {
            String idName = modifiedDetail.getIdName();
            String idKey = modifiedDetail.getIdKey();
            if (idName !=null && !idName.isEmpty()){
                String hierarchyName = corpHierarchyIds.get(idKey);
                if (idKey == null || idKey.trim().isEmpty() || !hierarchyName.equalsIgnoreCase(idName)) {
                    idNameList.add(idName);
                }
            }
            if (modifiedDetail.getAddedChildRelation() != null) {
                KnIdDetailsListDTO addedChildDetailsList = modifiedDetail.getAddedChildRelation().getAddedChildDetailsList();
                if (addedChildDetailsList != null) {
                    extractHierarchyIdName(addedChildDetailsList, idNameList,corpHierarchyIds);
                }
            }
        }
        knLogger.debug(methodName, "idName List: ", idNameList);
        knLogger.info(methodName, "EXIT");
        return idNameList;
    }

    private void extractHierarchyIdName(KnIdDetailsListDTO idDetailsListDTO, List<String> idNameList,Map<String,String> corpHierarchyIds) {
        if (idDetailsListDTO == null || idDetailsListDTO.getIdDetailsDto() == null) {
            return;
        }
        for (KnIdDetailsDTO node : idDetailsListDTO.getIdDetailsDto()) {
            if (node.getIdName() != null && !node.getIdName().trim().isEmpty()) {
                /*
                    If hierarchyId is null or empty, it means this node is being created for the first time,
                    so we should check if idName already exists in the system for any hierarchy.
                 */
                if ((node.getHierarchyId() == null || node.getHierarchyId().trim().isEmpty())) {
                    idNameList.add(node.getIdName());
                }
                /*
                    If hierarchyId is not null, it means this node already exists in the system, so we should check if idName has been changed.
                    If corpHierarchyIds contains the idName but with a different hierarchyId, it means the idName is being changed to one that already exists in another hierarchy, which should be flagged.
                 */
                else if (corpHierarchyIds.get(node.getHierarchyId()) != null && !corpHierarchyIds.get(node.getHierarchyId()).equalsIgnoreCase(node.getIdName())) {
                    idNameList.add(node.getIdName());
                }
            }
            if (node.getIdDetailsListDto() != null) {
                extractHierarchyIdName(node.getIdDetailsListDto(), idNameList, corpHierarchyIds);
            }
        }
    }

    /**
     * Helper method to recursively collect all extIds from the hierarchy tree.
     *
     * @param nodeList  Current level of nodes to process
     * @param extIdNameList List to accumulate all extIds
     */
    private void collectExtIdName(KnIdDetailsListDTO nodeList, List<String> extIdNameList) {
        if (nodeList == null || nodeList.getIdDetailsDto() == null) {
            return;
        }

        for (KnIdDetailsDTO node : nodeList.getIdDetailsDto()) {
            // Add current node's extId
            if (node.getIdName() != null && !node.getIdName().trim().isEmpty()) {
                extIdNameList.add(node.getIdName());
            }

            // Recursively process children
            if (node.getIdDetailsListDto() != null) {
                collectExtIdName(node.getIdDetailsListDto(), extIdNameList);
            }
        }
    }
    /**
     * Helper method to recursively collect all extIds from the hierarchy tree.
     *
     * @param nodeList  Current level of nodes to process
     * @param extIdList List to accumulate all extIds
     */
    private void collectExtIds(KnIdDetailsListDTO nodeList, List<String> extIdList) {
        if (nodeList == null || nodeList.getIdDetailsDto() == null) {
            return;
        }

        for (KnIdDetailsDTO node : nodeList.getIdDetailsDto()) {
            // Add current node's extId
            if (node.getExtId() != null && !node.getExtId().trim().isEmpty()) {
                extIdList.add(node.getExtId());
            }

            // Recursively process children
            if (node.getIdDetailsListDto() != null) {
                collectExtIds(node.getIdDetailsListDto(), extIdList);
            }
        }
    }
    public void removeHierarchyRelations(List<String> removedChildRelation,  String xdmsHome, String parentHierarchyId, int corpId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "removeHierarchyRelations(List<String>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: Removing nodes and their descendants for extIds: " + removedChildRelation);
        try {
            if (removedChildRelation == null || removedChildRelation.isEmpty()) {
                knLogger.warn(methodName, "No nodes provided for removal.");
                return;
            }

            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.deleteFromHierarchyDepth(removedChildRelation, parentHierarchyId, corpId, persisterTxn);

            knLogger.debug(methodName, "Successfully removed nodes and their descendants.");
        } catch (KnDAOException e) {
            knLogger.error(methodName, "Error while removing hierarchy relations: ",e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }

    }

    public void addGeoCodesToHierarchy(int corpId, Map<String,List<String>> addedGeoCodes, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "addGeoCodesToHierarchy(String, int, List<String>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.insertOrUpdateHierarchyGeoCode(addedGeoCodes, corpId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "Error while removing geocode mappings: ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void removeGeoCodesFromHierarchy(int corpId,Map<String,List<String>> removeGeocodes, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "removeGeoCodesFromHierarchy(String, int, List<String>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.removeGeocodeMapping(corpId, removeGeocodes, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "Error while removing geocode mappings: ",e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * Batch insert hierarchy details by splitting the list into smaller batches
     * of size {@link com.kodiak.xdms.server.corpmgmt.resources.KnConstants#HIERARCHY_BATCH_INSERT_SIZE}
     * and delegating each batch to {@link #insertIntoHierarchyDetails}.
     */
    public void batchInsertIntoHierarchyDetails(List<KnHierarchyInfoDTO> hierarchyInfoDTOS, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "batchInsertIntoHierarchyDetails(List, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : list size ", hierarchyInfoDTOS == null ? 0 : hierarchyInfoDTOS.size());
        if (hierarchyInfoDTOS == null || hierarchyInfoDTOS.isEmpty()) {
            return;
        }
        List<List<KnHierarchyInfoDTO>> batches = KnGeneralUtil.splitList(new ArrayList<>(hierarchyInfoDTOS), HIERARCHY_BATCH_INSERT_SIZE);
        for (List<KnHierarchyInfoDTO> batch : batches) {
            insertIntoHierarchyDetails(batch, xdmsHome, persisterTxn);
        }
    }

    /**
     * Batch insert hierarchy depth records by splitting the list into smaller batches
     * of size {@link com.kodiak.xdms.server.corpmgmt.resources.KnConstants#HIERARCHY_BATCH_INSERT_SIZE}
     * and delegating each batch to {@link #insertIntoHierarchyDepth}.
     */
    public void batchInsertIntoHierarchyDepth(List<KnHierarchyDepthInfoDTO> hierarchyDepthDTOS, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "batchInsertIntoHierarchyDepth(List, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : list size ", hierarchyDepthDTOS == null ? 0 : hierarchyDepthDTOS.size());
        if (hierarchyDepthDTOS == null || hierarchyDepthDTOS.isEmpty()) {
            return;
        }
        List<List<KnHierarchyDepthInfoDTO>> batches = KnGeneralUtil.splitList(new ArrayList<>(hierarchyDepthDTOS), HIERARCHY_BATCH_INSERT_SIZE);
        for (List<KnHierarchyDepthInfoDTO> batch : batches) {
            insertIntoHierarchyDepth(batch, xdmsHome, persisterTxn);
        }
    }

    /**
     * Batch insert hierarchy geocode map records by splitting the list into smaller batches
     * of size {@link com.kodiak.xdms.server.corpmgmt.resources.KnConstants#HIERARCHY_BATCH_INSERT_SIZE}
     * and delegating each batch to {@link #insertIntoHierarchyGeocodeMap}.
     */
    public void batchInsertIntoHierarchyGeocodeMap(List<KnCorpHierarchyGeocodeMapDTO> geocodeMapDTOS, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "batchInsertIntoHierarchyGeocodeMap(List, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : list size ", geocodeMapDTOS == null ? 0 : geocodeMapDTOS.size());
        if (geocodeMapDTOS == null || geocodeMapDTOS.isEmpty()) {
            return;
        }
        List<List<KnCorpHierarchyGeocodeMapDTO>> batches = KnGeneralUtil.splitList(new ArrayList<>(geocodeMapDTOS), HIERARCHY_BATCH_INSERT_SIZE);
        for (List<KnCorpHierarchyGeocodeMapDTO> batch : batches) {
            insertIntoHierarchyGeocodeMap(batch, xdmsHome, persisterTxn);
        }
    }

    /**
     * Retrieves geocode list for a specific hierarchy.
     * Delegates to DAO layer for database access (queries CORP_HIERARCHY_GEOCODE_MAPPING).
     *
     * @param corpId The corporate ID
     * @param hierarchyId The hierarchy ID
     * @param xdmsHome The PTT server ID
     * @param persisterTxn The transaction context
     * @return List of geocode strings for the specific hierarchy
     * @throws KnCorpBOException if database operation fails
     */
    public List<String> getRegionsByHierarchyId(int corpId, String hierarchyId, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getRegionsByHierarchyId(String, String, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY with corpId: ", corpId, ", hierarchyId: ", hierarchyId);
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            List<String> geoCodeList = xdmDAO.getRegionsByHierarchyId(corpId, hierarchyId, persisterTxn);
            knLogger.debug(methodName, "EXIT with ", (geoCodeList != null ? geoCodeList.size() : 0), " geocodes");
            return geoCodeList;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "Error while fetching regions: ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * Retrieves all regions from DEPLOY_SITE_INFO table.
     * Delegates to DAO layer for database access.
     *
     * @param corpId The corporate ID (used for logging)
     * @param xdmsHome The PTT server ID
     * @param persisterTxn The transaction context
     * @return List of all geocode strings
     * @throws KnCorpBOException if database operation fails
     */
    public List<String> getAllRegions(int corpId, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getAllRegions(String, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY with corpId: ", corpId);
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            List<String> geoCodeList = xdmDAO.getAllRegions(corpId, persisterTxn);
            knLogger.debug(methodName, "EXIT with ", (geoCodeList != null ? geoCodeList.size() : 0), " total regions");
            return geoCodeList;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "Error while fetching all regions: ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, Map<String,Integer>> fetchMaxChildLengthAndDepth(List<String> hierarchyId, String corpId, String xDmsHome, KnPersisterTxn persistenceTxn) throws KnCorpBOException {
        String methodName = "fetchMaxChildLengthAndDepth(Integer, String, persistenceTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xDmsHome);
            Map<String, Map<String, Integer>> stringMapMap = xdmDAO.fetchMaxChildLengthAndDepth(hierarchyId, corpId, persistenceTxn);
            knLogger.debug(methodName, "Exit : stringMapMap size: ", stringMapMap != null ? stringMapMap.size() : 0);
            return stringMapMap;
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public  Map<String,Integer> fetchMaxChildDepth(List<String> hierarchyId, String corpId, String xDmsHome, KnPersisterTxn persistenceTxn) throws KnCorpBOException {
        String methodName = "fetchMaxChildDepth(Integer, String, persistenceTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xDmsHome);
            Map<String, Integer> stringMapMap = xdmDAO.fetchMaxChildDepth(hierarchyId, corpId, persistenceTxn);
            knLogger.debug(methodName, "Exit : stringMapMap size: ", stringMapMap != null ? stringMapMap.size() : 0);
            return stringMapMap;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "Error while fetching max child depth: ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }
}

