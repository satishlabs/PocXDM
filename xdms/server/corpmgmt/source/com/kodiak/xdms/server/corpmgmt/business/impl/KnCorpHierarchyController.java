package com.kodiak.xdms.server.corpmgmt.business.impl;

import com.kodiak.common.commdto.common.KnIdDetailsDTO;
import com.kodiak.common.commdto.common.KnIdDetailsListDTO;
import com.kodiak.common.commdto.common.KnAddedChildRelationDTO;
import com.kodiak.common.commdto.common.KnModifiedIdDetailsListDTO;
import com.kodiak.common.dao.KnPersistenceException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.exception.KnException;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.processinvoker.impl.KnProcessInvokerImpl;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dto.common.KnCorpProfileDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorFramework;
import com.kodiak.xdms.server.corpmgmt.business.ICorpHierarchyController;
import com.kodiak.xdms.server.corpmgmt.business.KnCorpBOException;
import com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpCommonInfoUtil;
import com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpHierarchyInfoUtil;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpHierarchyDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnDeploySiteInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpHierarchyGeocodeMapDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnHierarchyDepthInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnHierarchyInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpResponseDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnRegionsCorpRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.hook.KnCorpHookIPDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.hook.KnCorpHookRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpHierarchyPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnActions;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.kodiak.common.resources.KnConstants.*;
import static com.kodiak.xdms.server.common.resources.KnConstants.LIBRARY_NAME_CORP_MGMT;
import static com.kodiak.xdms.server.common.resources.KnProfileTypes.CORP_PROFILE;
import static com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpCommonInfoUtil.populate;
import static com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpHierarchyInfoUtil.*;
import static com.kodiak.xdms.server.corpmgmt.resources.KnConstants.*;

public class KnCorpHierarchyController implements ICorpHierarchyController {
    private KnCorpCommonInfoUtil commonInfoUtil;
    private KnValidatorFramework validatorFW;
    private KnCorpHierarchyInfoUtil hierarchyInfoUtil;
    public KnCorpHierarchyController() {

        this.commonInfoUtil = new KnCorpCommonInfoUtil();
        validatorFW = KnValidatorFramework.getInstance(LIBRARY_NAME_CORP_MGMT);
        hierarchyInfoUtil = new KnCorpHierarchyInfoUtil();
    }

    private static final KnLogger knLogger = KnLogger.getLogger(KnCorpHierarchyController.class);

    @Override
    public KnCorpResponseDTO createHierarchy(KnIPCorpHierarchyDTO knIPCorpHierarchyDTO) {
        String methodName = "createHierarchy(KnCreateHirarchyRequestDTO ipTalkGroupDTO)";
        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        KnPersisterTxn persisterTxn = null;
        try {
            final KnGenInfoUtil genInfoUtil = KnGenInfoUtil.getInstance();
            knLogger.info(methodName, "Entry with params : corpId ", knIPCorpHierarchyDTO.getCorpId(), " TransactionId ", knIPCorpHierarchyDTO.getTransactionId());
            String transactionId = knIPCorpHierarchyDTO.getTransactionId();
            int corpId = knIPCorpHierarchyDTO.getCorpId();
            persisterTxn = KnPersisterTxn.getPersisterTxn();
            persisterTxn.open();
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails("" + corpId,	CORP_PROFILE, false, persisterTxn);
            String xdmsHome = corpProfile.getXdmsHome();
            //Hierarchy exist validation
            KnHierarchyDepthInfoDTO knHierarchyDepthInfoDTO = hierarchyInfoUtil.getRootNodeBasedOnCorpId(corpId, xdmsHome, persisterTxn);
            if (knHierarchyDepthInfoDTO != null
                    && knHierarchyDepthInfoDTO.getAncestorHierId() != null
                    && !knHierarchyDepthInfoDTO.getAncestorHierId().isEmpty()) {

                boolean isSameTransaction = transactionId != null
                        && !transactionId.isEmpty()
                        && transactionId.equals(knHierarchyDepthInfoDTO.getTransactionId());

                if (isSameTransaction) {
                    knLogger.debug(methodName, "Calling the cust path for corpId : ", corpId);
                    // Populate customParamMap with required context for the hook
                    Map<String, Object> customParams = new HashMap<>();
                    customParams.put(PERSISTER_TXN, persisterTxn);
                    customParams.put(PTT_SERVER_ID, xdmsHome);
                    knIPCorpHierarchyDTO.setCustomParamMap(customParams);

                    KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
                    hookIPDTO.setAction(KnActions.ACTIONS.GET_CORP_HIERARCHY);
                    hookIPDTO.setData(knIPCorpHierarchyDTO);
                    KnProcessInvokerImpl processInvoker = KnProcessInvokerImpl.getInstance();
                    Object hookResp = processInvoker.invokeHook(CUSTOM_CORP_USER_HOOK, hookIPDTO);
                    if (hookResp == null || !(hookResp instanceof KnCorpHookRespDTO)) {
                        throw new KnCorpBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                                "Invalid or null response received from hook for corpId : " + corpId);
                    }
                    KnCorpHookRespDTO responseDTO = (KnCorpHookRespDTO) hookResp;
                    if (responseDTO.getResponse() == null) {
                        throw new KnCorpBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                                "Null hierarchy data received from hook for corpId : " + corpId);
                    }
                    KnIdDetailsListDTO knIdDetailsListDTO = (KnIdDetailsListDTO) responseDTO.getResponse();
                    //form the same response and send back to the caller.
                    populateCreateHierarchyResponse(respDTO, knIdDetailsListDTO);
                    persisterTxn.save();
                    return respDTO;
                } else {
                    throw new KnCorpBOException(KnErrorCodes.BOEntity.HIERARCHY_EXISTS_IN_THE_CORPORATE,
                            "Hierarchy already exists for the corpId : " + corpId);
                }
            }


            List<KnDeploySiteInfoDTO> knDeploySiteInfoDTOList = hierarchyInfoUtil.getDeploySiteInfo(xdmsHome, persisterTxn);
            knLogger.debug(methodName, "Deploy Site Info  ", knDeploySiteInfoDTOList);
            List<String> systemGeoCodeList = knDeploySiteInfoDTOList.stream().map(KnDeploySiteInfoDTO::getGeoCode).toList();

            knLogger.debug(methodName, "System Geo Code List : ", systemGeoCodeList);
            KnCorpHierarchyPersistDTO persistDTO = new KnCorpHierarchyPersistDTO();
            persistDTO.setInputDTO(knIPCorpHierarchyDTO);
            persistDTO.setGeoCodeList(systemGeoCodeList);

            int clusterId = Integer.parseInt(System.getenv(CLUSTERID_ENV_NAME));
            Map<String, String> paramNameValueMapCommon = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
            String maxHorizontalLength = paramNameValueMapCommon.get(HIERARCHY_SUPPORT_HORIZONTAL);
            String maxVerticalLength = paramNameValueMapCommon.get(HIERARCHY_SUPPORT_VERTICAL);
            knLogger.debug(methodName, "Max vertical length from config: ", maxVerticalLength, " Max horizontal length from config: ", maxHorizontalLength);
            if(maxHorizontalLength != null && !"0".equals(maxHorizontalLength.trim())){
                persistDTO.setMaxHierarchyHorozontalLevel(Integer.parseInt(maxHorizontalLength));
            } else {
                knLogger.debug(methodName, "Default max horizontal level is set to 15 ");
                persistDTO.setMaxHierarchyHorozontalLevel(DEFAULT_MAX_HIERARCHY_HORIZONTAL_LENGTH);
            }
            if(maxVerticalLength != null && !"0".equals(maxVerticalLength.trim())){
                persistDTO.setMaxHierarchyVerticalLevel(Integer.parseInt(maxVerticalLength));
            } else {
                knLogger.debug(methodName, "Default max vertical level is set to 10 ");
                persistDTO.setMaxHierarchyVerticalLevel(DEFAULT_MAX_HIERARCHY_VERTICAL_LENGTH);
            }


            //unique hierarchy id checker across corp
            List<String> inputHierarchyIdNames = hierarchyInfoUtil.extractIdName(knIPCorpHierarchyDTO.getIdDetailsListDTO());
            List<String> corpHierarchyIds = hierarchyInfoUtil.getExtIdNameInfo(inputHierarchyIdNames, corpId, xdmsHome, persisterTxn);
            persistDTO.setInputHierarchyNameList(inputHierarchyIdNames);
            persistDTO.setCorpHierarchyNameList(corpHierarchyIds);


            knLogger.debug(" persistDTO :: ", persistDTO);
            validatorFW.validate(persistDTO);

            KnIdDetailsListDTO idDetailsListDTO = knIPCorpHierarchyDTO.getIdDetailsListDTO();
            Map<String, List<?>> mapInfo = hierarchyInfoUtil.prepareHierarchyDetailsDTOListWithExtIdMap(
                    idDetailsListDTO, String.valueOf(corpId), transactionId, xdmsHome, persisterTxn);

            hierarchyInfoUtil.batchInsertIntoHierarchyDetails((List<KnHierarchyInfoDTO>) mapInfo.get(HIERARCHY_DETAILS_LIST), xdmsHome, persisterTxn);
            hierarchyInfoUtil.batchInsertIntoHierarchyDepth((List<KnHierarchyDepthInfoDTO>) mapInfo.get(HIERARCHY_DEPTH_LIST), xdmsHome, persisterTxn);
            hierarchyInfoUtil.batchInsertIntoHierarchyGeocodeMap((List<KnCorpHierarchyGeocodeMapDTO>) mapInfo.get(HIERARCHY_GEO_CODE_LIST), xdmsHome, persisterTxn);

            populateCreateHierarchyResponse(respDTO, idDetailsListDTO);
            persisterTxn.save();
        } catch (KnCorpBOException e) {
            knLogger.error(methodName, "KnCorpBOException occured while createHierarchy - ", e);
            populate(respDTO, e);
            try {
                if (persisterTxn != null) {
                    persisterTxn.rollback();
                }
            } catch (KnPersistenceException ex) {
                knLogger.error(methodName, "Rollback failed while handling KnCorpBOException", ex);
            }
        } catch (KnValidationException e) {
            knLogger.error(methodName, "KnValidationException occured createHierarchy - ", e);
            populate(respDTO, e);
            try {
                if (persisterTxn != null) {
                    persisterTxn.rollback();
                }
            } catch (KnPersistenceException ex) {
                knLogger.error(methodName, "Rollback failed while handling KnValidationException", ex);
            }
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured createHierarchy - ", new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                    e.getMessage(), e));
            populate(respDTO, e);

            try {
                if (persisterTxn != null) {
                    persisterTxn.rollback();
                }
            } catch (KnPersistenceException ex) {
                knLogger.error(methodName, "Rollback failed while handling unexpected exception", ex);
            }

        }
        knLogger.info(methodName, "Exit ");
        return respDTO;
    }


    @Override
    @SuppressWarnings("unchecked")
    public KnCorpResponseDTO modifyHierarchy(KnIPCorpHierarchyDTO knIPCorpHierarchyDTO) {
        String methodName = "modifyHierarchy(KnIPCorpHierarchyDTO)";
        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        KnPersisterTxn persisterTxn = null;
        Map<String, Object> customParams = new HashMap<>();
        Map<String,String> hierarchyIdNameMap = new HashMap<>();
        boolean isAliasUpdateRequest = true;
        try {
            knLogger.info(methodName, "Entry with params: corpId ", knIPCorpHierarchyDTO.getCorpId());

            int corpId = knIPCorpHierarchyDTO.getCorpId();
            persisterTxn = KnPersisterTxn.getPersisterTxn();
            persisterTxn.open();

            // Fetch profile details
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(corpId), CORP_PROFILE, false, persisterTxn);
            String xdmsHome = corpProfile.getXdmsHome();
            List<KnModifiedIdDetailsListDTO> modifiedIdDetails = knIPCorpHierarchyDTO.getModifiedIdDetails();
            if(modifiedIdDetails == null || modifiedIdDetails.isEmpty()) {
                knLogger.warn(methodName, "No modified hierarchy details provided in the request.");
                throw new KnCorpBOException(com.kodiak.xdms.server.common.resources.KnErrorCodes.BOEntity.MODIFIED_LIST_MISSING, "No modified hierarchy details provided.");
            }

            //Business validation-flow
            validation(xdmsHome,corpId,knIPCorpHierarchyDTO,persisterTxn);
            knLogger.info(methodName,"validate successfully");

            // Process modified hierarchy details
            Map<String,List<String>> addedGeoCodeMap = new ConcurrentHashMap<>();
            Map<String,List<String>> removedGeoCodeMap = new ConcurrentHashMap<>();

            for (KnModifiedIdDetailsListDTO modifiedDetail : modifiedIdDetails) {
                String idKey = modifiedDetail.getIdKey();
                if (idKey != null && !idKey.isEmpty()) {
                    hierarchyIdNameMap.putIfAbsent(modifiedDetail.getIdName(), idKey);
                }

                // Handle removed child relations
                if (modifiedDetail.getRemovedChildRelation() != null && !modifiedDetail.getRemovedChildRelation().isEmpty()) {
                    isAliasUpdateRequest = false;
                    List<String> removedChildRelation = modifiedDetail.getRemovedChildRelation();
                    hierarchyInfoUtil.removeHierarchyRelations(removedChildRelation, xdmsHome, idKey, corpId, persisterTxn);
                    knLogger.info(methodName, " Removed child relations for ID Key: ", idKey, " Relations: ", removedChildRelation != null ? removedChildRelation.size() : 0);
                }

                // Handle added child relations
                if (modifiedDetail.getAddedChildRelation() != null) {
                    isAliasUpdateRequest = false;
                    KnAddedChildRelationDTO addedChildRelation = modifiedDetail.getAddedChildRelation();

                    Set<String> allExistingDescendantIDs = hierarchyInfoUtil.getAllExistingDescendantIDs(xdmsHome, corpId, persisterTxn);
                    knLogger.debug(methodName, " All existing descendant IDs in the system: ", allExistingDescendantIDs.toString());

                     Map<String, Integer> ancestorIdWithDepth = hierarchyInfoUtil.fetchAllHierarchyDepth(String.valueOf(corpId), idKey,xdmsHome, persisterTxn);
                     knLogger.debug(methodName, " Ancestor IDs with their depths: ", ancestorIdWithDepth.toString());

                    Map<String, List<?>> hierarchyMap = hierarchyInfoUtil.prepareHierarchyDetailsDTOListWithExtIdMap(addedChildRelation, String.valueOf(corpId),
                            modifiedDetail.getIdKey(), allExistingDescendantIDs,ancestorIdWithDepth, addedGeoCodeMap, hierarchyIdNameMap, removedGeoCodeMap
                            , xdmsHome, persisterTxn);

                    hierarchyInfoUtil.insertOrUpdateIntoHierarchyDetails((List<KnHierarchyInfoDTO>) hierarchyMap.get(HIERARCHY_DETAILS_LIST), xdmsHome, corpId, persisterTxn);
                    hierarchyInfoUtil.insertOrUpdateIntoHierarchyDepth((List<KnHierarchyDepthInfoDTO>) hierarchyMap.get(HIERARCHY_DEPTH_LIST), xdmsHome, corpId, persisterTxn);
                }

                // Handle removed geo codes
                if (modifiedDetail.getRemovedGeoCode() != null && !modifiedDetail.getRemovedGeoCode().isEmpty()) {
                    List<String> removedGeoCodes = modifiedDetail.getRemovedGeoCode();
                    knLogger.info(methodName, " Removing geo codes for ID Key: ", modifiedDetail.getIdKey(), " Geo Codes: ", removedGeoCodes);
                    removedGeoCodeMap.putIfAbsent(modifiedDetail.getIdKey(), removedGeoCodes);
                }

                // Handle added geo codes
                if (modifiedDetail.getAddedGeoCode() != null && !modifiedDetail.getAddedGeoCode().isEmpty()) {
                    List<String> addedGeoCodes = modifiedDetail.getAddedGeoCode();
                    addedGeoCodeMap.put(modifiedDetail.getIdKey(), addedGeoCodes);
                    knLogger.info(methodName, " Queued geo codes for addition for ID Key: ", modifiedDetail.getIdKey(), " Geo Codes: ", addedGeoCodes);
                }

                // Update hierarchy attributes
                if (modifiedDetail.getIdKey() != null) {
                    hierarchyInfoUtil.updateHierarchyAttributes(modifiedDetail, xdmsHome, isAliasUpdateRequest, persisterTxn);
                    knLogger.info(methodName, " Updated attributes for ID Key: ", idKey);
                }

            }
            // After processing all modifications, handle accumulated geoCode additions
            if (!addedGeoCodeMap.isEmpty()) {
                knLogger.debug(methodName, " Final map of geo codes to be added: ", addedGeoCodeMap.toString());
                hierarchyInfoUtil.addGeoCodesToHierarchy(corpId, addedGeoCodeMap, xdmsHome, persisterTxn);
            }
            // After processing all modifications, handle accumulated geoCode removals
            if (!removedGeoCodeMap.isEmpty()) {
                knLogger.debug(methodName, " Final map of geo codes to be removed: ", removedGeoCodeMap.toString());
                hierarchyInfoUtil.removeGeoCodesFromHierarchy(corpId, removedGeoCodeMap, xdmsHome, persisterTxn);
             }
            // Populate customParamMap with required context for the hook
            if (!hierarchyIdNameMap.isEmpty()) {
                customParams.putIfAbsent(KnConstants.HIERARCHY_NAME_MAP, hierarchyIdNameMap);
                knLogger.debug(methodName, " Final hierarchy ID-Name map to be passed to hook: ", hierarchyIdNameMap.toString());
                respDTO.setAdditionalInfo(customParams);
            }

            persisterTxn.save();
            populate(respDTO);

        } catch (KnCorpBOException e) {
            knLogger.error(methodName, "KnCorpBOException occurred while modifying hierarchy: ", e);
            populate(respDTO, e);
            try{
                persisterTxn.rollback();
            } catch (KnPersistenceException ex) {
                populate(respDTO, e);
            }
        } catch (KnValidationException e) {
            knLogger.error(methodName, "KnValidationException occurred while modifying hierarchy: ", e);
            populate(respDTO, e);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected exception occurred while modifying hierarchy: ", new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDTO, e);
            try{
                assert persisterTxn != null;
                persisterTxn.rollback();
            } catch (KnPersistenceException ex) {
                populate(respDTO, e);
            }
        }

        knLogger.info(methodName, "Exit");
        return respDTO;
    }

    public void populateCreateHierarchyResponse(KnCorpResponseDTO respDTO, KnIdDetailsListDTO idDetailsListDTO){
        Map<String, Object> respCustMap = new HashMap<String, Object>();
        respCustMap.put(IDDETAILSLIST, idDetailsListDTO);
        respDTO.setResponseMap(respCustMap);
        populate(respDTO);
    }

    private void validation(String xdmsHome,int corpId , KnIPCorpHierarchyDTO knIPCorpHierarchyDTO,KnPersisterTxn persisterTxn) throws KnBOException, KnValidationException {
        final String methodName = "validatingRequest(String XDMSHome,String corpId , KnPersisterTxn persisterTxn)";
        final KnGenInfoUtil genInfoUtil = KnGenInfoUtil.getInstance();
        Map<String, Integer> systemGeoClusterId = null;
        Map<String, Map<Integer, Integer>> clusterAndSubscriberCount = null;
        Map<String, List<String>> removedGeoCodeMap = new ConcurrentHashMap<>();
        Set<String> inputHierarchyIdSet = new HashSet<>();

        // Fetch deployment site inf
        List<KnDeploySiteInfoDTO> knDeploySiteInfoDTOList = hierarchyInfoUtil.getDeploySiteInfo(xdmsHome, persisterTxn);
        knLogger.debug(methodName, "Deploy Site Info  ", knDeploySiteInfoDTOList);
        List<String> systemGeoCodeList = knDeploySiteInfoDTOList.stream().map(KnDeploySiteInfoDTO::getGeoCode).toList();
        knLogger.debug(methodName, "System Geo Code List : ", systemGeoCodeList);
        Map<String,String> corpHierarchyIds = hierarchyInfoUtil.getHierarchyNameAndIdList(corpId, xdmsHome, persisterTxn);
        Collection<String> values = corpHierarchyIds.values();
        knLogger.debug(methodName, "Existing hierarchy ID-Name map for corpId ", corpId, " is : ", corpHierarchyIds);
        List<String> inputHierarchyIds = hierarchyInfoUtil.extractHierarchyIdName(knIPCorpHierarchyDTO.getModifiedIdDetails(),corpHierarchyIds);
        Map<String, String> rootHierarchyDetails = hierarchyInfoUtil.getRootHierarchyName(corpId, xdmsHome, persisterTxn);

        List<KnModifiedIdDetailsListDTO> modifiedIdDetails1 = knIPCorpHierarchyDTO.getModifiedIdDetails();

        for (KnModifiedIdDetailsListDTO modifiedIdDetails : modifiedIdDetails1) {
            if (modifiedIdDetails.getIdKey() != null && !modifiedIdDetails.getIdKey().isEmpty()) {
                inputHierarchyIdSet.add(modifiedIdDetails.getIdKey());
            }
            if (modifiedIdDetails.getAddedChildRelation() != null && modifiedIdDetails.getAddedChildRelation().getAddedChildDetailsList() != null) {
                KnIdDetailsDTO[] addedChildDetails = modifiedIdDetails.getAddedChildRelation().getAddedChildDetailsList().getIdDetailsDto();
                fetchRemovedGeocode(addedChildDetails, removedGeoCodeMap);
            }
            if (null != modifiedIdDetails.getRemovedGeoCode() && !modifiedIdDetails.getRemovedGeoCode().isEmpty()) {
                removedGeoCodeMap.put(modifiedIdDetails.getIdKey(), modifiedIdDetails.getRemovedGeoCode());
            }
        }
        Set<String> removedGeoCodeList = new HashSet<>();
        Set<String> hierarchyIds = removedGeoCodeMap.keySet();
        inputHierarchyIdSet.addAll(hierarchyIds);
        removedGeoCodeMap.forEach((key, value) -> removedGeoCodeList.addAll(value));
        knLogger.debug(methodName, "Removed Geo Code List : ", removedGeoCodeList);
        if (!removedGeoCodeList.isEmpty() && !inputHierarchyIdSet.isEmpty()) {
            systemGeoClusterId = commonInfoUtil.getClusterId(removedGeoCodeList, xdmsHome, persisterTxn);
            clusterAndSubscriberCount = hierarchyInfoUtil.getClusterAndSubscriberCount(inputHierarchyIdSet, corpId, xdmsHome, persisterTxn);
        }

        int clusterId = Integer.parseInt(System.getenv(CLUSTERID_ENV_NAME));
        Map<String, String> paramNameValueMapCommon = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);

        String maxHorizontalLength = paramNameValueMapCommon.get(HIERARCHY_SUPPORT_HORIZONTAL);
        String maxVerticalLength = paramNameValueMapCommon.get(HIERARCHY_SUPPORT_VERTICAL);

        KnCorpHierarchyPersistDTO persistDTO = new KnCorpHierarchyPersistDTO();
        persistDTO.setGeoCodeList(systemGeoCodeList);
        persistDTO.setInputHierarchyNameList(inputHierarchyIds);
        persistDTO.setSystemClusterId(systemGeoClusterId);
        persistDTO.setCorpHierarchyNameList(new ArrayList<>(values));
        if (maxHorizontalLength != null && !"0".equals(maxHorizontalLength.trim())) {
            persistDTO.setMaxHierarchyHorozontalLevel(Integer.parseInt(maxHorizontalLength));
        } else {
            knLogger.debug(methodName, "Default max horizontal level is set to 15 ");
            persistDTO.setMaxHierarchyHorozontalLevel(DEFAULT_MAX_HIERARCHY_HORIZONTAL_LENGTH);
        }
        if (maxVerticalLength != null && !"0".equals(maxVerticalLength.trim())) {
            persistDTO.setMaxHierarchyVerticalLevel(Integer.parseInt(maxVerticalLength));
        } else {
            knLogger.debug(methodName, "Default max vertical level is set to 10 ");
            persistDTO.setMaxHierarchyVerticalLevel(DEFAULT_MAX_HIERARCHY_VERTICAL_LENGTH);
        }
        knLogger.debug(" persistDTO :: ", persistDTO);

        // Validation flow.......!
        if (knIPCorpHierarchyDTO.getModifiedIdDetails() != null && !knIPCorpHierarchyDTO.getModifiedIdDetails().isEmpty()) {
            List<KnModifiedIdDetailsListDTO> modifiedIdDetails = knIPCorpHierarchyDTO.getModifiedIdDetails();
            KnIdDetailsDTO[] idDetailsDto = new KnIdDetailsDTO[modifiedIdDetails.size()];
            KnIPCorpHierarchyDTO knIPCorpDTO = new KnIPCorpHierarchyDTO();
            Set<String> parentHierarchyIdSet = new HashSet<>();
            Set<String> childHierarchyIdSet = new HashSet<>();
            int index = 0;
            AtomicBoolean isRemovingChild = new AtomicBoolean();
            AtomicBoolean isAddingChild = new AtomicBoolean();
            for (KnModifiedIdDetailsListDTO validationDTO : modifiedIdDetails) {
                KnAddedChildRelationDTO addedChildRelation = validationDTO.getAddedChildRelation();
                isRemovingChild.set(validationDTO.getRemovedChildRelation() != null && !validationDTO.getRemovedChildRelation().isEmpty());
                if (addedChildRelation!= null && addedChildRelation.getAddedChildDetailsList() != null
                        && addedChildRelation.getAddedChildDetailsList().getIdDetailsDto() != null
                        && addedChildRelation.getAddedChildDetailsList().getIdDetailsDto().length > 0) {
                    isAddingChild.set(Boolean.TRUE);
                    String idKey = validationDTO.getIdKey();
                    parentHierarchyIdSet.add(idKey);

                    for (KnIdDetailsDTO childDetails : addedChildRelation.getAddedChildDetailsList().getIdDetailsDto()) {
                        childHierarchyIdSet.add(childDetails.getHierarchyId());
                    }
                }
                KnIdDetailsDTO inputIdDetails = getKnIdDetailsDTO(corpId, validationDTO);
                idDetailsDto[index++] = inputIdDetails;
            }
            Map<String, Map<String, Integer>> stringMapMap = hierarchyInfoUtil.fetchMaxChildLengthAndDepth(new LinkedList<>(parentHierarchyIdSet), String.valueOf(corpId), xdmsHome, persisterTxn);
            Map<String, Integer> childDepthMap = hierarchyInfoUtil.fetchMaxChildDepth(new LinkedList<>(childHierarchyIdSet), String.valueOf(corpId), xdmsHome, persisterTxn);
            knLogger.debug(methodName, "Fetched max child length and depth for hierarchy IDs: ", stringMapMap.toString());
            knLogger.debug(methodName, "Fetched max child depth for child hierarchy IDs: ", childDepthMap);

            Map<String, Integer> maxHierarchyHorizontalLength = stringMapMap.get(KnPersisterConstants.MAX_HORIZONTAL_LENGTH);
            Map<String, Integer> maxHierarchyDepth = stringMapMap.get(KnPersisterConstants.MAX_VERTICAL_DEPTH);

            knIPCorpDTO.setMaxHierarchyHorizonatlMap(maxHierarchyHorizontalLength);
            knIPCorpDTO.setMaxHierarchyVerticalMap(maxHierarchyDepth);
            knIPCorpDTO.setChildHierarchyDepthMap(childDepthMap);

            knIPCorpDTO.setEntityId(knIPCorpHierarchyDTO.getEntityId());
            knIPCorpDTO.setOperationType(knIPCorpHierarchyDTO.getOperationType());
            knIPCorpDTO.setProfile(knIPCorpHierarchyDTO.getProfile());


            KnIdDetailsListDTO knIdDetailsListDTO = new KnIdDetailsListDTO();
            knIdDetailsListDTO.setIdDetailsDto(idDetailsDto);
            knIPCorpDTO.setIdDetailsListDTO(knIdDetailsListDTO);
            knIPCorpDTO.setModifiedIdDetails(knIPCorpHierarchyDTO.getModifiedIdDetails());
            if (knIPCorpDTO.getCustomParamMap() == null) {
                knIPCorpDTO.setCustomParamMap(new HashMap<>());
            }
            knIPCorpDTO.getCustomParamMap().putAll(rootHierarchyDetails);
            knIPCorpDTO.getCustomParamMap().put(IS_ADDING_CHILD, isAddingChild.get());
            knIPCorpDTO.getCustomParamMap().put(IS_REMOVING_CHILD, isRemovingChild.get());
            knIPCorpDTO.setClusterIdSubscriberMap(clusterAndSubscriberCount);
            knIPCorpDTO.setHierarchyIdNameMap(corpHierarchyIds);
            knLogger.info(methodName, "Input DTO for validation: ", knIPCorpDTO);
            persistDTO.setInputDTO(knIPCorpDTO);
            validatorFW.validate(persistDTO);
        }
    }

    private static KnIdDetailsDTO getKnIdDetailsDTO(int corpId, KnModifiedIdDetailsListDTO validationDTO) {
        KnIdDetailsDTO inputIdDetails = new KnIdDetailsDTO();

        inputIdDetails.setCorpId(corpId);
        inputIdDetails.setHierarchyId(validationDTO.getIdKey());
        inputIdDetails.setIdName(validationDTO.getIdName());
        inputIdDetails.setAddedGeoCode(validationDTO.getAddedGeoCode());
        inputIdDetails.setRemovedGeoCode(validationDTO.getRemovedGeoCode());

        KnAddedChildRelationDTO addedChildRelation = validationDTO.getAddedChildRelation();

        if (addedChildRelation != null ) {
            KnIdDetailsListDTO childDetailsList = addedChildRelation.getAddedChildDetailsList();
            inputIdDetails.setIdDetailsListDto(childDetailsList);

        }
        return inputIdDetails;
    }

    @Override
    public KnRegionsCorpRespDTO getRegions(KnIPCorpHierarchyDTO knIPCorpHierarchyDTO) {
        final String methodName = "getRegions(KnIPCorpHierarchyDTO)";
        knLogger.info(methodName, "Entry with corpId: ", knIPCorpHierarchyDTO.getCorpId(),
                ", hierarchyId: ", knIPCorpHierarchyDTO.getHierarchyId());

        KnRegionsCorpRespDTO respDTO = new KnRegionsCorpRespDTO();
        KnPersisterTxn persisterTxn = null;

        try {
            int corpId = knIPCorpHierarchyDTO.getCorpId();
            String hierarchyId = knIPCorpHierarchyDTO.getHierarchyId();

            persisterTxn = KnPersisterTxn.getPersisterTxn();
            persisterTxn.open();

            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(corpId), CORP_PROFILE, false, persisterTxn);
            String xdmsHome = corpProfile.getXdmsHome();

            boolean isHierarchyIdProvided = (hierarchyId != null && !hierarchyId.trim().isEmpty());
            knLogger.debug(methodName, "xdmsHome: ", xdmsHome, ", isHierarchyIdProvided: ", isHierarchyIdProvided);

            List<String> geoCodeList;
            if (isHierarchyIdProvided) {
                geoCodeList = hierarchyInfoUtil.getRegionsByHierarchyId(corpId, hierarchyId, xdmsHome, persisterTxn);
            } else {
                geoCodeList = hierarchyInfoUtil.getAllRegions(corpId, xdmsHome, persisterTxn);
            }

            respDTO.setGeoCodeList(geoCodeList != null ? geoCodeList : new ArrayList<>());
            populate(respDTO);
            knLogger.info(methodName, "Exit. Retrieved ", (geoCodeList != null ? geoCodeList.size() : 0), " geocodes");
            persisterTxn.save();
        } catch (KnCorpBOException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnCorpBOException in getRegions: ", e);
            try{
                assert persisterTxn != null;
                persisterTxn.rollback();
            } catch (KnPersistenceException ex) {
                populate(respDTO, e);
            }
        } catch (Exception e) {
            populate(respDTO, e);
            knLogger.error(methodName, "Unexpected exception in getRegions: ",
                    new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            try{
                if(persisterTxn != null)persisterTxn.rollback();
            } catch (KnPersistenceException ex) {
                populate(respDTO, e);
            }
        }

        return respDTO;
    }

    private void fetchRemovedGeocode(KnIdDetailsDTO[] idDetailsDTOS, Map<String, List<String>> removedGeoCodeMap) {
        if (idDetailsDTOS == null) {
            return;
        }
        for (KnIdDetailsDTO idDetailsDTO : idDetailsDTOS) {
            if (idDetailsDTO.getRemovedGeoCode() != null && !idDetailsDTO.getRemovedGeoCode().isEmpty()
                    && idDetailsDTO.getHierarchyId() != null && !idDetailsDTO.getHierarchyId().isEmpty()) {
                removedGeoCodeMap.put(idDetailsDTO.getHierarchyId(), idDetailsDTO.getRemovedGeoCode());
            }
            if (idDetailsDTO.getIdDetailsListDto() != null && idDetailsDTO.getIdDetailsListDto().getIdDetailsDto() != null) {
                fetchRemovedGeocode(idDetailsDTO.getIdDetailsListDto().getIdDetailsDto(), removedGeoCodeMap);
            }
        }

    }
}
