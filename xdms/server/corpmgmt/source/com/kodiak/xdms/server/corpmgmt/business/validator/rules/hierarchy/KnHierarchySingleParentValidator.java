// java
package com.kodiak.xdms.server.corpmgmt.business.validator.rules.hierarchy;

import com.kodiak.common.commdto.common.KnIdDetailsDTO;
import com.kodiak.common.commdto.common.KnIdDetailsListDTO;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.business.validator.rules.groupprofile.KnCorpGroupProfileValidator;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpHierarchyDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpHierarchyPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class KnHierarchySingleParentValidator extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnCorpGroupProfileValidator.class);

    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName = "KnHierarchySingleParentValidator";
        knLogger.debug(methodName, "ENTRY.");
        IPersistenceDTO persistDTO = getDTO();

        if (!(persistDTO instanceof KnCorpHierarchyPersistDTO)) {
            knLogger.debug(methodName, "Not a hierarchy persist DTO. Skipping.");
            return;
        }

        KnCorpHierarchyPersistDTO hierarchyPersistDTO = (KnCorpHierarchyPersistDTO) persistDTO;
        KnIPCorpHierarchyDTO knIPCorpHierarchyDTO = (KnIPCorpHierarchyDTO) hierarchyPersistDTO.getInputDTO();
        if (knIPCorpHierarchyDTO == null) {
            knLogger.debug(methodName, "Input DTO is null. Skipping.");
            return;
        }

        KnIdDetailsListDTO rootList = knIPCorpHierarchyDTO.getIdDetailsListDTO();
        if (rootList == null || rootList.getIdDetailsDto() == null) {
            knLogger.debug(methodName, "Root idDetailsListDTO is empty. Skipping.");
            return;
        }

        KnIdDetailsDTO[] parentDTOs = rootList.getIdDetailsDto();
        if (parentDTOs == null || parentDTOs.length == 0) {
            knLogger.debug(methodName, "No parents found. Skipping.");
            return;
        }

        Map<String, Set<String>> childToParents = new HashMap<>();
        Set<String> violatingChildren = new HashSet<>();

        // Traverse all levels starting from the top-level parents
        for (KnIdDetailsDTO parent : parentDTOs) {
            if (parent == null) continue;
            String parentId = safeId(parent);
            if (parentId == null) continue;
            traverseChildren(parentId, parent.getIdDetailsListDto(), childToParents, violatingChildren);
        }

        if (!violatingChildren.isEmpty()) {
            String duplicates = violatingChildren.toString();
            knLogger.error(methodName, "Single-parent rule violated for children(extId): " + duplicates);
            // TODO: Replace the error code below with an existing constant in KnErrorCodes.Validator
            throw new KnCorpBOValidationException(
                    KnErrorCodes.Validator.MULTIPLE_PARENTS_NODES_NOT_ALLOWED,
                    "Children having multiple parents(extId): " + duplicates,
                    getEntityId(),
                    getOperationType(),
                    getRuleId(),
                    duplicates,
                    ""
            );
        }

        knLogger.debug(methodName, "EXIT.");
    }

    // Recursively traverse child lists and record parent-child relationships using extId
    private void traverseChildren(String parentId,
                                  KnIdDetailsListDTO childrenList,
                                  Map<String, Set<String>> childToParents,
                                  Set<String> violatingChildren) {
        if (childrenList == null || childrenList.getIdDetailsDto() == null) return;

        for (KnIdDetailsDTO childDto : childrenList.getIdDetailsDto()) {
            knLogger.debug(" one childDto :: ", childDto);
            if (childDto == null) continue;

            String childId = safeId(childDto);
            if (childId == null) continue;

            // Record parent association for the child
            Set<String> parents = childToParents.computeIfAbsent(childId, k -> new HashSet<>());
            parents.add(parentId);

            // If more than one unique parent, mark violation
            if (parents.size() > 1) {
                violatingChildren.add(childId);
            }

            // Recurse into the child's own children
            traverseChildren(childId, childDto.getIdDetailsListDto(), childToParents, violatingChildren);
        }
    }

    // Extract extId from KnIdDetailsDTO; returns null if missing
    private String safeId(KnIdDetailsDTO dto) {
        // Assuming dto.getExtId() returns a String unique identifier
        String extId = dto.getExtId();
        return (extId == null || extId.trim().isEmpty()) ? null : extId.trim();
    }
}
