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
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpHierarchyDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpHierarchyPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.HashSet;
import java.util.Set;

public class KnHierarchyMaxDepthValidator extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnHierarchyMaxDepthValidator.class);

    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY.");
        IPersistenceDTO persistDTO = getDTO();

        if (persistDTO instanceof KnCorpHierarchyPersistDTO) {
            KnCorpHierarchyPersistDTO hierarchyPersistDTO = (KnCorpHierarchyPersistDTO) persistDTO;
            int maxDepth = hierarchyPersistDTO.getMaxHierarchyVerticalLevel();
            KnIPCorpHierarchyDTO knIPCorpHierarchyDTO = (KnIPCorpHierarchyDTO) hierarchyPersistDTO.getInputDTO();
            KnIdDetailsListDTO rootList = knIPCorpHierarchyDTO.getIdDetailsListDTO();
            KnIdDetailsDTO[] parentDTOs = rootList.getIdDetailsDto();
            // DFS over each top-level node; depth starts at 1 for a single node
            for (KnIdDetailsDTO parent : parentDTOs) {
                if (parent == null) continue;
                Set<String> path = new HashSet<>();
                String rootId = safeExtId(parent);
                if (rootId != null) {
                    path.add(rootId);
                }
                int depth = computeDepth(parent, path);
                if (depth > maxDepth) {
                    String msg = "Hierarchy depth exceeded. Max allowed: " + maxDepth + ", found: " + depth + " at root(extId): " + rootId;
                    knLogger.error(methodName, msg);
                    throw new KnCorpBOValidationException(
                            KnErrorCodes.Validator.HIERARCHY_DEPTH_EXCEEDED,
                            msg,
                            getEntityId(),
                            getOperationType(),
                            getRuleId(),
                            String.valueOf("Allowed: " + maxDepth + ", Found: " + depth),
                            ""
                    );
                }
            }
        }
        knLogger.debug(methodName, "EXIT.");
    }

    // Returns the maximum depth reachable from the given node
    private int computeDepth(KnIdDetailsDTO node, Set<String> pathExtIds) {
        if (node == null) return 0;

        KnIdDetailsListDTO childrenList = node.getIdDetailsListDto();
        KnIdDetailsDTO[] children = (childrenList == null) ? null : childrenList.getIdDetailsDto();

        // Leaf node depth is 1 (the node itself)
        if (children == null || children.length == 0) {
            return 0;
        }

        int maxChildDepth = 0;
        for (KnIdDetailsDTO child : children) {
            if (child == null) continue;

            String childId = safeExtId(child);
            // Detect cycles; treat as stop to prevent infinite loop
            if (childId != null && pathExtIds.contains(childId)) {
                // Count current edge but do not go deeper into the cycle
                maxChildDepth = Math.max(maxChildDepth, 1);
                continue;
            }

            boolean added = false;
            if (childId != null) {
                added = pathExtIds.add(childId);
            }

            int childDepth = computeDepth(child, pathExtIds);
            maxChildDepth = Math.max(maxChildDepth, childDepth);

            if (added) {
                pathExtIds.remove(childId);
            }
        }

        // Depth of current node is 1 + max depth of its children
        return 1 + maxChildDepth;
    }

    private String safeExtId(KnIdDetailsDTO dto) {
        if (dto == null) return null;
        String extId = dto.getExtId();
        if (extId == null && dto.getIdName() == null) {
            return null;
        } else {
            return (extId != null) ? extId.trim() : dto.getIdName().trim();
        }
    }
}
