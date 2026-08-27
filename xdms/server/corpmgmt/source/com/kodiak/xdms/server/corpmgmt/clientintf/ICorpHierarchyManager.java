package com.kodiak.xdms.server.corpmgmt.clientintf;

import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpHierarchyDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpResponseDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnRegionsCorpRespDTO;

public interface ICorpHierarchyManager {

    public KnCorpResponseDTO createHierarchy(KnIPCorpHierarchyDTO knIPCorpHierarchyDTO);

    public KnCorpResponseDTO modifyHierarchy(KnIPCorpHierarchyDTO knIPCorpHierarchyDTO);

    public KnRegionsCorpRespDTO getRegions(KnIPCorpHierarchyDTO knIPCorpHierarchyDTO);

}
