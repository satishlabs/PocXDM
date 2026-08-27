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
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class KnHierarchyParentValidator extends KnValidatorRule {

    private static final KnLogger knLogger = KnLogger.getLogger(KnHierarchyParentValidator.class);

    @Override
    public void validate() throws KnValidationException, KnBOException {

        final String methodName = "KnHierarchyParentValidator: validate()";
        knLogger.info(methodName, "ENTRY.");
        KnCorpHierarchyPersistDTO hierarchyPersistDTO = (KnCorpHierarchyPersistDTO) getDTO();

        KnIPCorpHierarchyDTO inputDto = (KnIPCorpHierarchyDTO) hierarchyPersistDTO.getInputDTO();

        if (inputDto == null) {
            knLogger.debug("validate", "Input DTO is null. Skipping parent validation.");
            return;
        }
        List<KnModifiedIdDetailsListDTO> modifiedIdDetails = inputDto.getModifiedIdDetails();
        if (modifiedIdDetails == null || modifiedIdDetails.isEmpty()) {
            knLogger.debug("validate", "No modified ID details provided. Skipping parent validation.");
            return;
        }
        Set<String> childIdKey = new HashSet<>();

        for (KnModifiedIdDetailsListDTO modifiedIdDetailsListDTO : modifiedIdDetails) {
            KnAddedChildRelationDTO addedChildRelation = modifiedIdDetailsListDTO.getAddedChildRelation();
            if (addedChildRelation == null || addedChildRelation.getAddedChildDetailsList() == null) {
                knLogger.debug("validate", "No added child relation details for modified ID: " + modifiedIdDetailsListDTO.getIdKey());
                continue;
            }
            String parentIdKey = modifiedIdDetailsListDTO.getIdKey();
            knLogger.debug(methodName, "Validating parent-child relationship for parent ID: ", parentIdKey);
            String idName = modifiedIdDetailsListDTO.getIdName();
            fetchChildIds(addedChildRelation.getAddedChildDetailsList(), childIdKey);
            knLogger.debug(methodName, "Collected child IDs for parent ID ", parentIdKey, ": ", childIdKey);
            if (childIdKey.contains(parentIdKey)) {
                knLogger.error(methodName, "Hierarchy with ID " + parentIdKey + " cannot be a parent and child at the same time.");
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.PARENT_CHILD_HIERARCHY_NOT_SAME,
                        String.format("Hierarchy '%s' cannot be assigned as its own parent. Please select a different parent.", idName), getEntityId(), getOperationType(), getRuleId(),
                        idName, "");
            }
        }
        knLogger.info(methodName, "EXIT.");

    }

    private void fetchChildIds(KnIdDetailsListDTO addedChildDetailsList, Set<String> childIdKey) {
        if (addedChildDetailsList == null || addedChildDetailsList.getIdDetailsDto() == null) {
            return;
        }
        for (KnIdDetailsDTO child : addedChildDetailsList.getIdDetailsDto()) {
            if (child == null) continue;
            String childKey = child.getHierarchyId();
            childIdKey.add(childKey);
            if (child.getIdDetailsListDto() != null && child.getIdDetailsListDto().getIdDetailsDto() != null) {
                fetchChildIds(child.getIdDetailsListDto(), childIdKey);
            }
        }
    }
}
