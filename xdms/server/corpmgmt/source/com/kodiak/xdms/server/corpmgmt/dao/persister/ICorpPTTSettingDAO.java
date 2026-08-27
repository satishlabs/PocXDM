package com.kodiak.xdms.server.corpmgmt.dao.persister;

import com.kodiak.common.commdto.common.KnPTTSettingDocInfoDTO;
import com.kodiak.common.commdto.common.KnXDMPTTSettingHierarchyListDTO;
import com.kodiak.common.commdto.common.KnXDMMdnInfoDTO;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpPTTSettingDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpPTTSettingDocRespDTO;

import java.util.List;
import java.util.Set;

public interface ICorpPTTSettingDAO {

    public void createPTTSettingDoc(String pttSettingId, KnCorpPTTSettingDTO pttSettingDoc, KnPersisterTxn persisterTxn) throws KnDAOException;


    public List<KnPTTSettingDocInfoDTO> getPTTSettingListById(List<KnCorpPTTSettingDocRespDTO> pttIdList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Set<KnCorpPTTSettingDocRespDTO> getPTTSettingDocIdsForCorp(int corpId, String hierarchyId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnCorpPTTSettingDTO getPTTSettingDoc(String docId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public String getPTTSettingDocName(String pttSettingName, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void setDefaultPttSettingDoc(String docId, int corpId, String hierarchyId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deletePTTSettingDocFromCB(String docId,KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<KnPTTSettingDocInfoDTO> getAllPTTSettingDocs(String docType, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void assignPttSettingToHierarchy(KnXDMPTTSettingHierarchyListDTO pttSettingDocList, int corpId,String sysDefaultTemplate, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void unassignPttSettingToHierarchy(KnXDMPTTSettingHierarchyListDTO pttSettingDocList, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void assignPttSettingDocToMdns(List<String> mdnList, String pttSettingId, int corpId, String hierarchyId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void unassignPttSettingDocToMdns(List<String> mdnList, int corpId, String hierarchyId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateLastProfileUpdateTimeForMdns(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<KnXDMMdnInfoDTO> getPttSettingDocMdnList(String pttSettingId, String hierarchyId, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public int getMDNCountForPttSettingDocID(String pttSettingID, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateSubscriberPttSettingDocId(String newPttSettingDocId, String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void assignPttSettingToCorp(String pttSettingDocList, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void unassignPttSettingToCorp(String pttSettingDocList, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<KnCorpPTTSettingDocRespDTO> getCorpPTTSettingDocInfoForTemplateId(String pttSettingID, KnPersisterTxn persisterTxn) throws KnDAOException;

    public boolean isPttSettingDocAlreadyDefault(String pttSettingID,int corpId,String hierarchyId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void resetDefaultSettingDoc(int corpId, String hierarchyId ,KnPersisterTxn persisterTxn) throws KnDAOException ;

     public void modifyPTTSettingTemplate(String pttSettingId, KnCorpPTTSettingDTO pttSettingDoc, KnPersisterTxn persisterTxn) throws KnDAOException;

}
