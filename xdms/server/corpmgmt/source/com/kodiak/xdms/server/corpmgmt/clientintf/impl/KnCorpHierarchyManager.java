package com.kodiak.xdms.server.corpmgmt.clientintf.impl;

import com.kodiak.xdms.server.common.resources.KnProfileTypes;
import com.kodiak.xdms.server.corpmgmt.business.ICorpHierarchyController;
import com.kodiak.xdms.server.corpmgmt.business.impl.KnCorpBORegistry;
import com.kodiak.xdms.server.corpmgmt.clientintf.ICorpHierarchyManager;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpHierarchyDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpResponseDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnRegionsCorpRespDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnEntityTypes;
import com.kodiak.xdms.server.corpmgmt.resources.KnOperationTypes;

public class KnCorpHierarchyManager implements ICorpHierarchyManager {
    private final ICorpHierarchyController hierarchyController;

    KnCorpHierarchyManager() {
        hierarchyController = KnCorpBORegistry.createCorpHierarchyController();
    }

    @Override
    public KnCorpResponseDTO createHierarchy(KnIPCorpHierarchyDTO knIPCorpHierarchyDTO) {
        knIPCorpHierarchyDTO.setEntityId(KnEntityTypes.CORP_HIERARCHY_MANAGER);
        knIPCorpHierarchyDTO.setOperationType(KnOperationTypes.CREATE_HIERARCHY);
        knIPCorpHierarchyDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return hierarchyController.createHierarchy(knIPCorpHierarchyDTO);
    }

    @Override
    public KnCorpResponseDTO modifyHierarchy(KnIPCorpHierarchyDTO knIPCorpHierarchyDTO) {
        knIPCorpHierarchyDTO.setEntityId(KnEntityTypes.CORP_HIERARCHY_MANAGER);
        knIPCorpHierarchyDTO.setOperationType(KnOperationTypes.MODIFY_HIERARCHY);
        knIPCorpHierarchyDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return hierarchyController.modifyHierarchy(knIPCorpHierarchyDTO);
    }

    @Override
    public KnRegionsCorpRespDTO getRegions(KnIPCorpHierarchyDTO knIPCorpHierarchyDTO) {
        knIPCorpHierarchyDTO.setEntityId(KnEntityTypes.CORP_HIERARCHY_MANAGER);
        knIPCorpHierarchyDTO.setOperationType(KnOperationTypes.GET_REGIONS);
        knIPCorpHierarchyDTO.setProfile(KnProfileTypes.CORP_PROFILE);
        return hierarchyController.getRegions(knIPCorpHierarchyDTO);
    }
}