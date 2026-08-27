package com.kodiak.xdms.server.corpmgmt.business;

import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpHierarchyDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpResponseDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnRegionsCorpRespDTO;

public interface ICorpHierarchyController {

    KnCorpResponseDTO createHierarchy(KnIPCorpHierarchyDTO knIPCorpHierarchyDTO);

    KnCorpResponseDTO modifyHierarchy(KnIPCorpHierarchyDTO knIPCorpHierarchyDTO);

    KnRegionsCorpRespDTO getRegions(KnIPCorpHierarchyDTO knIPCorpHierarchyDTO);

}
