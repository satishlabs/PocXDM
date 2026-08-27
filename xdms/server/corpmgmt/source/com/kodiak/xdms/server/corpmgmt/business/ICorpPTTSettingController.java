package com.kodiak.xdms.server.corpmgmt.business;

import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpPTTSettingDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpPTTSettingDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpResponseDTO;

public interface ICorpPTTSettingController {


    public KnCorpResponseDTO createPTTSettingDoc(KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO getAllPTTSettingDocList(KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO, KnPersisterTxn persisterTxn);

    public KnCorpPTTSettingDTO getPTTSettingDoc(KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO setDefaultPttSettingDoc(KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO deletePTTSettingDoc(KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO, KnPersisterTxn persisterTxn);
    public KnCorpResponseDTO assignPttSettingToHierarchy(KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO unassignPttSettingToHierarchy(KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO assignPttSettingDocToMdns(KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO unassignPttSettingDocToMdns(KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO getPttSettingDocMdnList(KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO getMDNCountForPttSettingDocID(KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO, KnPersisterTxn persisterTxn);
    public KnCorpResponseDTO assignPttSettingToCorp(KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO unassignPttSettingToCorp(KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO, KnPersisterTxn persisterTxn);
    public KnCorpResponseDTO modifyPTTSettingTemplate(KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO, KnPersisterTxn persisterTxn);
}
