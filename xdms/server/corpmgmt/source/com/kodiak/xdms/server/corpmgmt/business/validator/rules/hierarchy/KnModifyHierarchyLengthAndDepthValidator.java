/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnResourceNotModifiedValidationRule.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Tapas ku. Rout      April 23, 2026      15.0
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
 * ************************************************************************
 */

package com.kodiak.xdms.server.corpmgmt.business.validator.rules.hierarchy;


import com.kodiak.common.commdto.common.KnAddedChildRelationDTO;
import com.kodiak.common.commdto.common.KnIdDetailsDTO;
import com.kodiak.common.commdto.common.KnIdDetailsListDTO;
import com.kodiak.common.commdto.common.KnModifiedIdDetailsListDTO;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpHierarchyDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpHierarchyPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.List;
import java.util.Map;

public class KnModifyHierarchyLengthAndDepthValidator extends KnValidatorRule {


    private static final KnLogger knLogger = KnLogger.getLogger(KnModifyHierarchyLengthAndDepthValidator.class);

    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName = "KnModifyHierarchyLengthAndDepthValidator: validate()";
        knLogger.info(methodName, "ENTRY.");

        KnCorpHierarchyPersistDTO hierarchyPersistDTO = (KnCorpHierarchyPersistDTO) getDTO();

        KnIPCorpHierarchyDTO inputDto = (KnIPCorpHierarchyDTO) hierarchyPersistDTO.getInputDTO();

        if (inputDto == null) {
            knLogger.debug("validate", "Input DTO is null. Skipping parent validation.");
            return;
        }
        Map<String, Integer> maxHierarchyHorizonatlMap = inputDto.getMaxHierarchyHorizonatlMap();
        Map<String, Integer> maxHierarchyDepthMap = inputDto.getMaxHierarchyVerticalMap();
        Map<String, Object> customParamMap = inputDto.getCustomParamMap();

        if ((maxHierarchyHorizonatlMap == null || maxHierarchyHorizonatlMap.isEmpty()) && (maxHierarchyDepthMap == null || maxHierarchyDepthMap.isEmpty())) {
            knLogger.debug("validate", "No hierarchy length or depth details provided. Skipping length and depth validation.");
            return;
        }
        Map<String, Integer> childHierarchyDepthMap = inputDto.getChildHierarchyDepthMap();
        List<KnModifiedIdDetailsListDTO> modifiedIdDetails = inputDto.getModifiedIdDetails();

        int maxHierarchyHorizontalLevel = hierarchyPersistDTO.getMaxHierarchyHorozontalLevel();
        int maxHierarchyVerticalLevel = hierarchyPersistDTO.getMaxHierarchyVerticalLevel();

        if (modifiedIdDetails != null && !modifiedIdDetails.isEmpty()) {
            for (KnModifiedIdDetailsListDTO modifiedIdDetailsListDTO : modifiedIdDetails) {

                String idKey = modifiedIdDetailsListDTO.getIdKey();
                String idName = modifiedIdDetailsListDTO.getIdName();
                knLogger.debug(methodName, "Validating hierarchy length and depth for modified ID: ", idKey);

                KnAddedChildRelationDTO addedChildRelation = modifiedIdDetailsListDTO.getAddedChildRelation();
                if (addedChildRelation == null || addedChildRelation.getAddedChildDetailsList() == null) {
                    knLogger.debug("validate", "No added child relation details for modified ID: " + modifiedIdDetailsListDTO.getIdKey());
                    continue;
                }
                KnIdDetailsDTO[] idDetailsDto = addedChildRelation.getAddedChildDetailsList().getIdDetailsDto();
                if (idDetailsDto == null || idDetailsDto.length == 0) {
                    knLogger.debug("validate", "No child details for added child relation of modified ID: " + modifiedIdDetailsListDTO.getIdKey());
                    continue;
                }
                int currentHorizontalLength = idDetailsDto.length;
                knLogger.debug(methodName, "Current horizontal length for modified ID ", idKey, ": ", currentHorizontalLength);
                if (maxHierarchyHorizonatlMap != null && maxHierarchyHorizonatlMap.containsKey(idKey)) {
                    int horizontalLevel = maxHierarchyHorizonatlMap.get(idKey);

                    int sumOfLength = Math.addExact(currentHorizontalLength, horizontalLevel);
                    knLogger.debug(methodName, "MaxLength:", maxHierarchyHorizontalLevel, ", Sum of current horizontal length and horizontal level for modified ID ", idKey, ": ", sumOfLength);

                    if (sumOfLength > maxHierarchyHorizontalLevel) {
                        knLogger.error("validate", "Hierarchy with ID " + idKey + " exceeds maximum horizontal level." + " Current horizontal length: " + Math.addExact(currentHorizontalLength, horizontalLevel));
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.HIERARCHY_LENGTH_EXCEEDED, "Max children exceeded at root Organization '" + idName + "' level. Allowed max children per parent is" + maxHierarchyHorizontalLevel + " but found " + sumOfLength,
                                getEntityId(), getOperationType(), getRuleId(), "Allowed max children per parent is " + maxHierarchyHorizontalLevel + " but found " + sumOfLength, "");
                    } else {
                        recursivelyCheckChildLength(idDetailsDto, modifiedIdDetailsListDTO.getIdName(),maxHierarchyHorizontalLevel);
                    }
                } else {
                    recursivelyCheckChildLength(idDetailsDto,modifiedIdDetailsListDTO.getIdName(),maxHierarchyHorizontalLevel);
                }
                if (maxHierarchyDepthMap != null && maxHierarchyDepthMap.containsKey(idKey)) {
                    if (null != customParamMap) {
                        idName = (String) customParamMap.getOrDefault(KnConstants.ROOT_HIERARCHY_NAME, idName);
                    }
                    int verticalLevel = maxHierarchyDepthMap.get(idKey);
                    int requestedHierarchyDepth = getRequestedHierarchyDepth(childHierarchyDepthMap, idDetailsDto, 1, Boolean.FALSE);
                    int totalLength = Math.addExact(requestedHierarchyDepth, verticalLevel);
                    knLogger.debug(methodName, "Requested hierarchy depth for modified ID ", idKey, ": ", requestedHierarchyDepth);
                    knLogger.debug(methodName, "Total vertical level (requested depth + vertical level) for modified ID ", idKey, ": ", totalLength);
                    if (totalLength > maxHierarchyVerticalLevel) {
                        knLogger.error("validate", "Hierarchy with ID " + idKey + " exceeds maximum vertical level.");
                        String msg = "Hierarchy depth exceeded at root Organization: " + idName + " Allowed: " + maxHierarchyVerticalLevel + ", Found: " + totalLength;
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.HIERARCHY_DEPTH_EXCEEDED,
                                msg, getEntityId(), getOperationType(), getRuleId(), "Allowed: " + maxHierarchyVerticalLevel + ", Found: " + totalLength, "");
                    }
                } else {
                    knLogger.debug(methodName, "No maximum vertical level specified for modified ID ", idKey, ". Skipping vertical depth validation.");
                }
                knLogger.debug(methodName,"End of validation for modified ID: ", idKey);
            }
        }
    }

    int getRequestedHierarchyDepth(Map<String,Integer> childHierarchyDepthMap,KnIdDetailsDTO[] idDetailsDto, int currentDepth, boolean isChildPresentInParent) {
        String methodName = "getRequestedHierarchyDepth";
        knLogger.info(methodName, "ENTRY. Current depth: ", currentDepth);
        if (idDetailsDto == null) {
            return currentDepth;
        }
        for (KnIdDetailsDTO child : idDetailsDto) {
            Integer childDepth = childHierarchyDepthMap.get(child.getHierarchyId());
            knLogger.debug(methodName, "Checking child hierarchy ID: ", child.getHierarchyId(), " with current depth: ", currentDepth);
            if (childDepth != null) {
                isChildPresentInParent = true;
                currentDepth = Math.max(currentDepth, childDepth);
            } else {
                KnIdDetailsListDTO idDetailsListDto = child.getIdDetailsListDto();
                if (idDetailsListDto != null && idDetailsListDto.getIdDetailsDto() != null) {
                    KnIdDetailsDTO[] idDetailsDto1 = idDetailsListDto.getIdDetailsDto();
                    if (idDetailsDto1 != null && idDetailsDto1.length > 0) {
                        int depth = getRequestedHierarchyDepth(childHierarchyDepthMap, idDetailsDto1, currentDepth + 1, isChildPresentInParent);
                        currentDepth = Math.max(currentDepth, depth);
                    }
                }
            }
        }
        knLogger.debug(methodName, "Exit::: Calculated depth for current hierarchy: ", currentDepth);
        return isChildPresentInParent ? 1 + currentDepth : currentDepth;
    }

    private void recursivelyCheckChildLength(KnIdDetailsDTO[] idDetailsDto, String hierarchyName,int maxHierarchyHorizontalLevel) throws KnCorpBOValidationException {
        final String methodName = "recursivelyCheckChildLength";
        knLogger.info(methodName, "ENTRY. Max horizontal level: ", maxHierarchyHorizontalLevel, "for Hierarchy ", hierarchyName);
        int currentHorizontalLength = idDetailsDto.length;
        if (currentHorizontalLength > maxHierarchyHorizontalLevel) {
            knLogger.error(methodName, "Hierarchy Horizonal length exceeded:: Current Length::", currentHorizontalLength, "MaxHorizontalLength::", maxHierarchyHorizontalLevel);
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.HIERARCHY_LENGTH_EXCEEDED, "Max children exceeded at Organization '" + hierarchyName + "' level. Allowed max children per parent is " + maxHierarchyHorizontalLevel + " but found " + currentHorizontalLength,
                    getEntityId(), getOperationType(), getRuleId(), "Allowed max children per parent is " + maxHierarchyHorizontalLevel + " but found " + currentHorizontalLength, "");
        }
        for (KnIdDetailsDTO child : idDetailsDto) {
            knLogger.debug("recursivelyCheckChildLength", "Current horizontal length for child ID ", child.getHierarchyId(), ": ", currentHorizontalLength);
            KnIdDetailsListDTO idDetailsListDto = child.getIdDetailsListDto();
            if (idDetailsListDto != null) {
                KnIdDetailsDTO[] childIdDetailsDto = idDetailsListDto.getIdDetailsDto();
                recursivelyCheckChildLength(childIdDetailsDto, child.getIdName(), maxHierarchyHorizontalLevel);
            }
            knLogger.info(methodName, "Exit. for Hierarchy ", hierarchyName);
        }

    }
}
