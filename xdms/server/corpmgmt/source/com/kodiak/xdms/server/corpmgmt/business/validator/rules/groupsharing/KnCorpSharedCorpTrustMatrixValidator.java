/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.rules.groupsharing;

import com.kodiak.common.commdto.common.KnHierarchyMappingInfo;
import com.kodiak.common.commdto.common.KnXDMHierarchyInfo;
import com.kodiak.common.ggcache.dto.KnCorpTrustMatrixDTO;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSharedCorpInfo;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpBCGrpPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupProfilePersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class KnCorpSharedCorpTrustMatrixValidator extends KnValidatorRule {

    private static final KnLogger knLogger = KnLogger.getLogger(KnCorpSharedCorpTrustMatrixValidator.class);
    private static final int GROUPSHARING = 1;
    private static final int USERPROFILESHARING = 2;
    private static final int GROUPANDUSERPROFILESHARING = 3;


    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY.");
        IPersistenceDTO persistDTO = getDTO();
        Set<String> groupsShared = new HashSet<>();
        Set<String> deniedHierarchyIds = new HashSet<>();
        if (persistDTO instanceof KnCorpGroupProfilePersistDTO) {
            KnCorpGroupProfilePersistDTO grpGersistDTO = (KnCorpGroupProfilePersistDTO) persistDTO;
            knLogger.debug(methodName,"trustMatrixDTOList - ",grpGersistDTO.getTrustMatrixDTOList()," sharedCorpInfoList - ",grpGersistDTO.getCorpSharedCorpInfoList());
            int ownerCorpId = grpGersistDTO.getCorpId() != null ? grpGersistDTO.getCorpId() : 0;
            checkSharedCorps(grpGersistDTO.getCorpSharedCorpInfoList(), grpGersistDTO.getTrustMatrixDTOList(), ownerCorpId, groupsShared, deniedHierarchyIds);
        } else if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
            knLogger.debug(methodName,"Bean is instance of KnCorpGroupInfoPersistDTO");
            KnCorpGroupInfoPersistDTO groupPersistDTO = (KnCorpGroupInfoPersistDTO) persistDTO;
            knLogger.debug(methodName,"trustMatrixDTOList - ",groupPersistDTO.getTrustMatrixDTOList()," sharedCorpInfoList - ",groupPersistDTO.getCorpSharedCorpInfoList());
            checkSharedCorps(groupPersistDTO.getCorpSharedCorpInfoList(), groupPersistDTO.getTrustMatrixDTOList(), groupPersistDTO.getCorpId(), groupsShared, deniedHierarchyIds);
        } else if (persistDTO instanceof KnCorpBCGrpPersistDTO) {
            knLogger.debug(methodName,"Bean is instance of KnCorpBCGrpPersistDTO");
            KnCorpBCGrpPersistDTO groupPersistDTO = (KnCorpBCGrpPersistDTO) persistDTO;
            knLogger.debug(methodName,"trustMatrixDTOList - ",groupPersistDTO.getTrustMatrixDTOList()," sharedCorpInfoList - ",groupPersistDTO.getCorpSharedCorpInfoList());
            checkSharedCorps(groupPersistDTO.getCorpSharedCorpInfoList(), groupPersistDTO.getTrustMatrixDTOList(), groupPersistDTO.getCorpId(), groupsShared, deniedHierarchyIds);
        }
        if (deniedHierarchyIds != null && !deniedHierarchyIds.isEmpty()) {
            knLogger.error(methodName, "Groups are not allowed to be shared with hierarchy(s) ", deniedHierarchyIds);
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.GROUPS_NOT_ALLOWED_TOBE_SHARED_WITH_HIERARCHY,
                    "Groups are not allowed to be shared with hierarchy(s)", getEntityId(), getOperationType(), getRuleId(), deniedHierarchyIds.toString(), "");
        }
        if (groupsShared != null && !groupsShared.isEmpty()) {
            knLogger.error(methodName, "Groups are not allowed be shared with Corp ");
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.GROUPS_NOT_ALLOWED_TOBE_SHARED,
                    "Groups are not allowed be shared with Corp", getEntityId(), getOperationType(), getRuleId(), groupsShared.toString(), "");
        }
    }

    /**
     * P6-1: Two-Stage Trust Matrix Validator helper.
     * Stage 1: checks typeOfSharingFeatureAllowed is 1 or 3.
     * Stage 2: checks hierarchy scope using NEW_SHARED_TRUST_MATRIX_HIERARCHY (only when recId != null).
     * TC-TML-020: same-corp entries (sharedCorpInfo.corpId == ownerCorpId) are allowed immediately.
     * Populates denied (extCorpIds) for Stage 1 failures and deniedHierarchyIds (sharedHierarchyId) for Stage 2 failures.
     */
    private void checkSharedCorps(List<KnCorpSharedCorpInfo> sharedCorpInfoList, List<KnCorpTrustMatrixDTO> trustMatrixDTOList, int ownerCorpId, Set<String> denied, Set<String> deniedHierarchyIds) {
        final String methodName = "checkSharedCorps()";
        if (sharedCorpInfoList == null || sharedCorpInfoList.isEmpty()) return;

        // Build extCorpId → trustMatrixDTO map for efficient lookup
        Map<String, KnCorpTrustMatrixDTO> tmByExtCorpId = new HashMap<>();
        if (trustMatrixDTOList != null) {
            for (KnCorpTrustMatrixDTO dto : trustMatrixDTOList) {
                if (dto.getSharedExtCorpId() != null) {
                    tmByExtCorpId.put(dto.getSharedExtCorpId().trim(), dto);
                }
            }
        }

        for (KnCorpSharedCorpInfo sharedCorpInfo : sharedCorpInfoList) {
            String extCorpId = sharedCorpInfo.getExtCorpId();

            // TC-TML-020: same-corp fast path — trust matrix lookup entirely skipped
            if (sharedCorpInfo.getCorpId() != null && sharedCorpInfo.getCorpId() == ownerCorpId) {
                knLogger.debug(methodName, "TC-TML-020: same-corp sharing for extCorpId=", extCorpId, " — allowing immediately");
                continue;
            }

            KnCorpTrustMatrixDTO trustMatrixDTO = extCorpId != null ? tmByExtCorpId.get(extCorpId.trim()) : null;

            // No trust matrix entry → deny
            if (trustMatrixDTO == null) {
                knLogger.debug(methodName, "No trust matrix entry for extCorpId=", extCorpId, " — denying");
                denied.add(extCorpId);
                continue;
            }

            // Stage 1: type check
            int type = trustMatrixDTO.getTypeOfSharingFeatureAllowed();
            if (type != GROUPSHARING && type != GROUPANDUSERPROFILESHARING) {
                knLogger.debug(methodName, "Stage 1 failed for extCorpId=", extCorpId, " type=", type);
                denied.add(extCorpId);
                continue;
            }

            // Legacy entry (no recId) → allow (skip Stage 2)
            if (trustMatrixDTO.getRecId() == null) {
                knLogger.debug(methodName, "Legacy trust matrix (no recId) for extCorpId=", extCorpId, " — allowing");
                continue;
            }

            // Stage 2: hierarchy scope check using hierarchyList.
            // Uses pre-fetched hierarchyMappings from trustMatrixDTO (populated by controller).
            //   - null/empty mappings → flat sharing (all hierarchies allowed).
            //   - Non-empty          → (ownerHierarchyId, sharedHierarchyId) pair must be in the allowed set.
            // ownerHierarchyId is sourced from sharedCorpInfo.sourceHierarchyId (set by controller
            // from the group's hierarchy context before validation — authoritative, not client-supplied).
            List<KnXDMHierarchyInfo> hierarchyList = sharedCorpInfo.getHierarchyList();
            if (hierarchyList == null || hierarchyList.isEmpty()) {
                // No hierarchy list provided → allow (no hierarchy-level constraint)
                knLogger.debug(methodName, "Stage 2 bypass (no hierarchyList) for extCorpId=", extCorpId, " — allowing");
                continue;
            }

            List<KnHierarchyMappingInfo> allMappings = trustMatrixDTO.getHierarchyMappings();

            if (allMappings == null || allMappings.isEmpty()) {
                // No child rows in hierarchy table → flat group sharing, all hierarchies allowed
                knLogger.debug(methodName, "Stage 2: flat corp (no hierarchy rows) for recId=",
                        trustMatrixDTO.getRecId(), " extCorpId=", extCorpId, " — allowing all");
                continue;
            }

            // Build ownerHierarchyId → allowed sharedHierarchyIds map from trust matrix rows
            Map<String, Set<String>> ownerToAllowed = new HashMap<>();
            for (KnHierarchyMappingInfo mapping : allMappings) {
                if (mapping.getOwnerHierarchyId() != null && mapping.getSharedHierarchyId() != null) {
                    ownerToAllowed.computeIfAbsent(mapping.getOwnerHierarchyId(), k -> new HashSet<>())
                                  .add(mapping.getSharedHierarchyId());
                }
            }
            knLogger.debug(methodName, "Stage 2: ownerToAllowed=", ownerToAllowed,
                    " for recId=", trustMatrixDTO.getRecId(), " extCorpId=", extCorpId);

            // Effective owner hierarchy comes from the group's hierarchy context (set by controller)
            String effectiveOwnerHierarchyId = sharedCorpInfo.getSourceHierarchyId();
            knLogger.debug(methodName, "Stage 2: effectiveOwnerHierarchyId=", effectiveOwnerHierarchyId,
                    " extCorpId=", extCorpId);

            // Validate each (ownerHierarchyId, sharedHierarchyId) pair from the request hierarchyList
            for (KnXDMHierarchyInfo hierEntry : hierarchyList) {
                String sharedHierarchyId = hierEntry.getSharedHierarchyId();
                if (sharedHierarchyId == null || sharedHierarchyId.isEmpty()) {
                    continue; // skip null/empty entries
                }
                if (effectiveOwnerHierarchyId != null && !effectiveOwnerHierarchyId.isEmpty()) {
                    // Validate the (ownerHierarchyId, sharedHierarchyId) pair
                    Set<String> allowedForOwner = ownerToAllowed.get(effectiveOwnerHierarchyId);
                    if (allowedForOwner == null || !allowedForOwner.contains(sharedHierarchyId)) {
                        knLogger.debug(methodName, "Stage 2 failed: ownerHierarchyId=", effectiveOwnerHierarchyId,
                                " sharedHierarchyId=", sharedHierarchyId, " not an allowed pair for extCorpId=", extCorpId);
                        deniedHierarchyIds.add(sharedHierarchyId);
                    }
                } else {
                    // No owner hierarchy context available — check if sharedHierarchyId is allowed for any owner
                    boolean found = ownerToAllowed.values().stream().anyMatch(s -> s.contains(sharedHierarchyId));
                    if (!found) {
                        knLogger.debug(methodName, "Stage 2 failed (no owner context): sharedHierarchyId=",
                                sharedHierarchyId, " not in any allowed set for extCorpId=", extCorpId);
                        deniedHierarchyIds.add(sharedHierarchyId);
                    }
                }
            }
        }
    }
}
