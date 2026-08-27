/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.rules.groupsharing;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpBCGrpPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Validates whether a group operation is allowed for the currently logged-in hierarchy
 * based on the group's ownership and sharing configuration.
 *
 * <p>This rule supports both normal groups ({@link KnCorpGroupInfoPersistDTO}) and
 * broadcast groups ({@link KnCorpBCGrpPersistDTO}). For non-owner hierarchies, it ensures
 * the group is explicitly shared with the logged-in hierarchy before allowing the operation.</p>
 */
public class KnGroupSharingValidator extends KnValidatorRule {

    private static final KnLogger knLogger = KnLogger.getLogger(KnGroupSharingValidator.class);

    /**
     * Performs group-sharing validation for the current persistence DTO.
     *
     * <p>Validation flow:</p>
     * <ol>
     *   <li>Extract group-sharing data and hierarchy context from the DTO.</li>
     *   <li>Return early when required values are unavailable.</li>
     *   <li>Build the set of shared hierarchy IDs (normalized as 8-digit strings).</li>
     *   <li>If caller is not the owner hierarchy, verify caller exists in sharing map.</li>
     * </ol>
     *
     * <p>If validation fails, throws {@link KnCorpBOValidationException} with
     * {@code GROUP_DOES_NOT_EXIST} to prevent access from unshared hierarchies.</p>
     *
     * @throws KnValidationException if framework-level validation fails
     * @throws KnBOException if business-object validation fails
     */
    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName = "KnGroupSharingValidator.validate()";

        IPersistenceDTO persistDTO = getDTO();
        Set<String> sharedHierarchyIds = new HashSet<>();
        String loggedInHierarchyId = null;
        List<int[]> groupSharingMap = null;
        String ownerHierarchyId = null;
        int groupId = 0;

        if (persistDTO instanceof KnCorpGroupInfoPersistDTO groupInfoPersistDTO) {
            knLogger.info(methodName, "Validating normalGroup sharing for groupId: " + groupInfoPersistDTO.getGroupId());
            groupSharingMap = groupInfoPersistDTO.getGroupSharingMap();
            loggedInHierarchyId = groupInfoPersistDTO.getHierarchyId();
            groupId = groupInfoPersistDTO.getGroupId();
            ownerHierarchyId = groupInfoPersistDTO.getOwnerHierarchyId();
        } else if (persistDTO instanceof KnCorpBCGrpPersistDTO bcGroupPersistDTO) {
            knLogger.info(methodName, "Validating broadcastGroup sharing for groupId: " + bcGroupPersistDTO.getGroupId());
            groupSharingMap = bcGroupPersistDTO.getGroupSharingMap();
            loggedInHierarchyId = bcGroupPersistDTO.getHierarchyId();
            groupId = bcGroupPersistDTO.getGroupId();
            ownerHierarchyId = bcGroupPersistDTO.getOwnerHierarchyId();
        }

        knLogger.info(methodName, "Logged in hierarchyId: " + loggedInHierarchyId + ", groupSharingMap: " + groupSharingMap + ", " +
                "ownerHierarchyId: " + ownerHierarchyId);

        // Skip validation when required context is not available.
        if (null == loggedInHierarchyId || loggedInHierarchyId.isEmpty() || null == groupSharingMap
                || null == ownerHierarchyId || ownerHierarchyId.isEmpty()) {
            return;
        }

        // Convert sharing map hierarchy IDs into normalized 8-digit string identifiers.
        groupSharingMap.forEach(sharing -> {
            if (sharing.length >= 1) {
                sharedHierarchyIds.add(String.format("%08d", sharing[0]));
            }
        });

        knLogger.info(methodName, "sharedHierarchyIds: ", sharedHierarchyIds);

        String failedRuleValue = String.valueOf(groupId == 0 ? "" : groupId);
        boolean isOwnerHierarchy = ownerHierarchyId.equals(loggedInHierarchyId);
        // Only enforce share-membership for hierarchies that do not own the group.
        if (!isOwnerHierarchy) {
            // Group has sharing entries, but caller hierarchy is not one of them.
            if (!sharedHierarchyIds.isEmpty() && !sharedHierarchyIds.contains(loggedInHierarchyId)) {
                knLogger.error(methodName, "Caller hierarchyId: " + loggedInHierarchyId + " is not in the shared hierarchy for groupId: " + groupId);
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.GROUP_DOES_NOT_EXIST,
                        "Group does not exist in the shared hierarchy.",
                        getEntityId(), getOperationType(), getRuleId(), failedRuleValue, "");

            // Group is not shared at all; non-owner caller cannot access/modify it.
            } else if (sharedHierarchyIds.isEmpty()) {
                knLogger.error(methodName, "Caller hierarchyId: " + loggedInHierarchyId + " is not in the shared hierarchy for groupId: " + groupId);
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.GROUP_DOES_NOT_EXIST,
                        "Group does not exist in the shared hierarchy.",
                        getEntityId(), getOperationType(), getRuleId(), failedRuleValue, "");
            }
        }
    }
}