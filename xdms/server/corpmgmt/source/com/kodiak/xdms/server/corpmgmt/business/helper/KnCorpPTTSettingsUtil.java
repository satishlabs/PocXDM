package com.kodiak.xdms.server.corpmgmt.business.helper;


import com.kodiak.common.commdto.common.KnPTTSettingDocInfoDTO;
import com.kodiak.common.commdto.common.KnXDMMdnInfoDTO;
import com.kodiak.common.commdto.common.KnXDMPTTSettingHierarchyListDTO;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;

import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dao.persister.db.KnQueryMapper;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.corpmgmt.business.KnCorpBOException;
import com.kodiak.xdms.server.corpmgmt.dao.persister.ICorpXdmDAO;
import com.kodiak.xdms.server.corpmgmt.dao.persister.db.KnCorpXdmDAO;
import com.kodiak.xdms.server.corpmgmt.dao.persister.db.tables.KnCorpDBTablesRegistry;
import com.kodiak.xdms.server.corpmgmt.dao.persister.db.tables.xdm.KnCorpPTTSettingDocDAO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpPTTSettingDTO;
import com.couchbase.client.core.error.DocumentNotFoundException;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpPTTSettingDocRespDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnDAOSourceTypes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.*;

import static com.kodiak.common.resources.KnErrorCodes.DAO.ROW_NOT_FOUND;
import static com.kodiak.xdms.server.common.resources.KnConstants.SYSTEM_DEFAULT_PTTSETTINGDOCID;
import static com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants.RESET_ALL_DEFAULT_PTT_SETTING_DOCS;
import static com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.Validator.PTT_TEMPLATE_ALREADY_ASSIGNED_TO_CORP;
import static com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.Validator.PTT_TEMPLATE_ASSIGNED_TO_SUBSCRIBERS;

public class KnCorpPTTSettingsUtil {

    private static final KnLogger knLogger = KnLogger.getLogger(KnCorpPTTSettingsUtil.class);
    private KnGenInfoUtil genInfoUtil;
    String xdmsHome = null;
    public KnCorpPTTSettingsUtil(){
        try {
            genInfoUtil = KnGenInfoUtil.getInstance();
            xdmsHome = genInfoUtil.retrieveLocalXDMPttServerId();
        } catch (Exception e) {
            knLogger.error("Failed to get local PttServer Id");
        }
    }
    public void createPTTSettingDoc(String pttSettingId, KnCorpPTTSettingDTO pttSettingDoc, KnPersisterTxn persisterTxn)throws KnCorpBOException {
        final String methodName="createPTTSettingDoc()";
        knLogger.debug(methodName, "ENTRY pttSettingDoc:",pttSettingId);
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.createPTTSettingDoc(pttSettingId,pttSettingDoc, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }
    public String getPTTSettingDocName(String pttSettingName, KnPersisterTxn persisterTxn)throws KnCorpBOException {
        final String methodName="getPTTSettingDocName()";
        knLogger.debug(methodName, "ENTRY pttSettingName:",pttSettingName);
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            return xdmDAO.getPTTSettingDocName(pttSettingName, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }

    }
    /*public Collection<KnCorpPTTSettingDTO> getAllPTTSettingDoc(String docType, String xdmsHome, KnPersisterTxn persisterTxn)throws KnCorpBOException {
        final String methodName="getAllPTTSettingDoc()";
        knLogger.debug(methodName, "ENTRY getAllPTTSettingDoc:",docType);
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            return xdmDAO.getAllPTTSettingDocs(docType, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }*/

    public Set<KnCorpPTTSettingDocRespDTO> getPTTSettingDocIds(int corpId, String hierarchyId, String xdmsHome, KnPersisterTxn persisterTxn)throws KnCorpBOException {
        final String methodName="getPTTSettingDocIds()";
        knLogger.debug(methodName, "ENTRY getPTTSettingDocIds:",corpId);
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            return xdmDAO.getPTTSettingDocIdsForCorp(corpId,hierarchyId, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public List<KnPTTSettingDocInfoDTO> getAllPTTSettingDocList(Set<KnCorpPTTSettingDocRespDTO> pttIdList, int corpId, String xdmsHome, KnPersisterTxn persisterTxn)throws KnCorpBOException {
        final String methodName="getAllPTTSettingDocList()";
        knLogger.debug(methodName, "ENTRY getAllPTTSettingDocList:",pttIdList);
        List<KnPTTSettingDocInfoDTO> pttSettingList = new ArrayList<>();
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            for(KnCorpPTTSettingDocRespDTO settingDocRespDTO: pttIdList) {
                KnCorpPTTSettingDTO pttSettingDoc = xdmDAO.getPTTSettingDoc(settingDocRespDTO.getPttSettingId(),persisterTxn);
                KnPTTSettingDocInfoDTO docInfoDTO = new KnPTTSettingDocInfoDTO();
                docInfoDTO.setDocId(pttSettingDoc.get_id());
                docInfoDTO.setDocName(pttSettingDoc.getTemplateName());
                //get MDN Count
                int mdnCount =getMDNCountForPttSettingDocID(settingDocRespDTO.getPttSettingId(), corpId, xdmsHome, persisterTxn);
                docInfoDTO.setMdnCount(mdnCount);
                docInfoDTO.setIsDefault(settingDocRespDTO.getIsDefault());
                pttSettingList.add(docInfoDTO);
            }
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        } catch (KnBOException e) {
            throw new RuntimeException(e);
        }
        return pttSettingList;
    }

    public KnPTTSettingDocInfoDTO getSystemDefaultTemplateDetails(String pttSettingDocId,String xdmsHome, KnPersisterTxn persisterTxn){
        final String methodName = "getSystemDefaultTemplateDetails()";
        knLogger.debug(methodName, "ENTRY System Default Template:", pttSettingDocId);
        if (pttSettingDocId == null || pttSettingDocId.trim().isEmpty()) {
            knLogger.debug(methodName, "No system default template configured; skipping.");
            return null;
        }
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            KnCorpPTTSettingDTO pttSettingDoc = xdmDAO.getPTTSettingDoc(pttSettingDocId, persisterTxn);
            if (pttSettingDoc == null || pttSettingDoc.get_id() == null) {
                knLogger.debug(methodName, "System default template doc not found for id:", pttSettingDocId);
                return null;
            }
            KnPTTSettingDocInfoDTO docInfoDTO = new KnPTTSettingDocInfoDTO();
            docInfoDTO.setDocId(pttSettingDoc.get_id());
            docInfoDTO.setDocName(pttSettingDoc.getTemplateName());
            docInfoDTO.setMdnCount(0);
            docInfoDTO.setIsSystemDefault(1);
            return docInfoDTO;
        }catch (Exception e){
            knLogger.error(methodName, "Failed to get System Default Template:", e);
            return null;
        }
    }

    public KnCorpPTTSettingDTO getPTTSettingDoc(String docId,  KnPersisterTxn persisterTxn)throws KnCorpBOException {
        final String methodName = "getPTTSettingDoc()";
        knLogger.debug(methodName, "ENTRY getPTTSettingDoc:", docId);
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            return xdmDAO.getPTTSettingDoc(docId, persisterTxn);

        } catch (DocumentNotFoundException ex) {
            throw new KnCorpBOException(ROW_NOT_FOUND, ex.getMessage(), ex);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }
    public void setDefaultPttSettingDoc(String docId, int corpId, String hierarchyId, String xdmsHome, KnPersisterTxn persisterTxn)throws KnCorpBOException {
        final String methodName="setDefaultPttSettingDoc()";
        knLogger.debug(methodName, "ENTRY: docId - ", docId, ", corpId - ", corpId, ", hierarchyId - ", hierarchyId);
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.setDefaultPttSettingDoc(docId,corpId, hierarchyId,persisterTxn);
            knLogger.info(methodName, "Successfully set PTT Setting Doc as default. docId - ", docId, ", corpId - ", corpId, ", hierarchyId - ", hierarchyId);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "Error setting PTT Setting Doc as default. docId - ", docId, ", corpId - ", corpId, ", hierarchyId - ", hierarchyId, ", Error: ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void deletePTTSettingDocFromCB(String docId, KnPersisterTxn persisterTxn)throws KnCorpBOException {
        final String methodName="deletePTTSettingDocFromCB()";
        knLogger.debug(methodName, "ENTRY deletePTTSettingDoc:",docId);
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
             xdmDAO.deletePTTSettingDocFromCB(docId, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public List<KnPTTSettingDocInfoDTO> getAllPTTSettingDocs(String docType, KnPersisterTxn persisterTxn)throws KnCorpBOException {
        final String methodName="getAllPTTSettingDocs()";
        knLogger.debug(methodName, "ENTRY getAllPTTSettingDocs:", docType);
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            return xdmDAO.getAllPTTSettingDocs(docType, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }
    public void assignPttSettingToHierarchy(List<KnXDMPTTSettingHierarchyListDTO> pttSettingDocList, int corpId,String sysDefaultTemplate, String xdmsHome, KnPersisterTxn persisterTxn)throws KnCorpBOException {
        final String methodName="assignPttSettingToHierarchy()";
        knLogger.debug(methodName, "ENTRY pttSettingDocList:", pttSettingDocList);
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            boolean hasAnyNewAssignment = false;

            for(KnXDMPTTSettingHierarchyListDTO pttSettingHierarchy : pttSettingDocList) {
                Set<KnCorpPTTSettingDocRespDTO> pttSettingDocIds = getPTTSettingDocIds(corpId, pttSettingHierarchy.getHierarchyId() ,xdmsHome, persisterTxn);
                // Filter out already assigned PTT Setting IDs
                if (pttSettingDocIds != null && pttSettingHierarchy.getPttSettingDocList() != null) {
                    // Extract existing PTT Setting IDs from the response DTOs
                    Set<String> existingPttSettingIds = pttSettingDocIds.stream()
                            .map(KnCorpPTTSettingDocRespDTO::getPttSettingId)
                            .collect(java.util.stream.Collectors.toSet());

                    // Filter out already assigned PTT Settings
                    List<String> newPttSettingList = pttSettingHierarchy.getPttSettingDocList().stream()
                            .filter(pttSettingId -> !existingPttSettingIds.contains(pttSettingId))
                            .collect(java.util.stream.Collectors.toList());

                    if (newPttSettingList.isEmpty()) {
                        knLogger.debug(methodName, "All PTT Templates are already assigned for hierarchyId:", pttSettingHierarchy.getHierarchyId(), ", skipping this hierarchy");
                        continue;
                    }

                    // Update the hierarchy object with filtered list
                    pttSettingHierarchy.setPttSettingDocList(newPttSettingList);
                    knLogger.debug(methodName, "Filtered out already assigned PTT Settings. Assigning only new ones:", newPttSettingList);
                }

                xdmDAO.assignPttSettingToHierarchy(pttSettingHierarchy, corpId, sysDefaultTemplate,persisterTxn);
                hasAnyNewAssignment = true;
            }

            // Throw exception if no new assignments were made across all hierarchies
            if (!hasAnyNewAssignment) {
                knLogger.error(methodName, "All PTT Templates are already assigned to corp/Hierarchy across all hierarchies");
                throw new KnCorpBOException(PTT_TEMPLATE_ALREADY_ASSIGNED_TO_CORP, "All the PTT Template's are already assigned to Hierarchy");
            }
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void unassignPttSettingToHierarchy(List<KnXDMPTTSettingHierarchyListDTO> pttSettingDocList, int corpId, String xdmsHome, KnPersisterTxn persisterTxn)throws KnCorpBOException {
        final String methodName="unassignPttSettingToHierarchy()";
        knLogger.debug(methodName, "ENTRY pttSettingDocList:", pttSettingDocList);
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            for(KnXDMPTTSettingHierarchyListDTO pttSettingHierarchy : pttSettingDocList) {
                xdmDAO.unassignPttSettingToHierarchy(pttSettingHierarchy, corpId, persisterTxn);
            }
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }


    public void assignPttSettingDocToMdns(List<String> mdnList, String pttSettingId, int corpId, String hierarchyId, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName="assignPttSettingDocToMdns()";
        knLogger.debug(methodName, "ENTRY mdnList:", mdnList, "pttSettingId:", pttSettingId);
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.assignPttSettingDocToMdns(mdnList, pttSettingId, corpId, hierarchyId, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void unassignPttSettingDocToMdns(List<String> mdnList, int corpId, String hierarchyId, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName="unassignPttSettingDocToMdns()";
        knLogger.debug(methodName, "ENTRY mdnList:", mdnList);
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.unassignPttSettingDocToMdns(mdnList, corpId, hierarchyId, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void updateLastProfileUpdateTimeForMdns(List<String> mdnList, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "updateLastProfileUpdateTimeForMdns()";
        knLogger.debug(methodName, "ENTRY mdnList:", mdnList);
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.updateLastProfileUpdateTimeForMdns(mdnList, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public List<KnXDMMdnInfoDTO> getPttSettingDocMdnList(String pttSettingId, String hierarchyId, int corpId, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName="getPttSettingDocMdnList()";
        knLogger.debug(methodName, "ENTRY corpId:", corpId," pttSettingId:", pttSettingId, " hierarchyId:", hierarchyId);
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            return xdmDAO.getPttSettingDocMdnList(pttSettingId, hierarchyId, corpId, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public int getMDNCountForPttSettingDocID(String pttSettingID, int corpID, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName="getMDNCountForPttSettingDocID()";
        knLogger.debug(methodName, "ENTRY pttSettingID:", pttSettingID, "corpID:", corpID);
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            return xdmDAO.getMDNCountForPttSettingDocID(pttSettingID, corpID, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void updateSubscriberPttSettingDocId(String mdn, String newPttSettingDocId, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "updateSubscriberPttSettingDocId()";
        knLogger.debug(methodName, "ENTRY pttSettingID:", newPttSettingDocId, "MDN:", mdn);
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.updateSubscriberPttSettingDocId(newPttSettingDocId, mdn, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void assignPttSettingToCorp(String pttSettingId, int corpId, String xdmsHome, KnPersisterTxn persisterTxn)throws KnCorpBOException {
        final String methodName="assignPttSettingToHierarchy()";
        knLogger.debug(methodName, "ENTRY pttSettingId:", pttSettingId);
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.assignPttSettingToCorp(pttSettingId, corpId, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void unassignPttSettingToCorp(String pttSettingId, int corpId, String xdmsHome, KnPersisterTxn persisterTxn)throws KnCorpBOException {
        final String methodName="unassignPttSettingToCorp()";
        knLogger.debug(methodName, "ENTRY pttSettingId:", pttSettingId);
        int mdnCount = 0;
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            mdnCount = getMDNCountForPttSettingDocID(pttSettingId, corpId, xdmsHome, persisterTxn);
            if(mdnCount > 0) {
                knLogger.error(methodName, "Cannot unassign PTT Setting Doc from corp as it is assigned to ", mdnCount, " MDNs");
                throw new KnCorpBOException(PTT_TEMPLATE_ASSIGNED_TO_SUBSCRIBERS,"PTT Setting is assigned to Subscribers");
            }
            xdmDAO.unassignPttSettingToCorp(pttSettingId, corpId, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public  boolean validatePttSettingCorpMapping(String pttSettingId,KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "validatePttSettingDocCorpMapping(String)";
        boolean isValid = false;
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            List<KnCorpPTTSettingDocRespDTO> respDTOList = xdmDAO.getCorpPTTSettingDocInfoForTemplateId(pttSettingId, persisterTxn);
            if (null != respDTOList && !respDTOList.isEmpty()) {
                isValid = true;
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception Occurred - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return isValid;
    }

    public  boolean isPttSettingDocAlreadyDefault(String pttSettingId,int corpId,String hierarchyId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "validatePttSettingDocCorpMapping(String)";
        boolean isValid = false;
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            isValid = xdmDAO.isPttSettingDocAlreadyDefault(pttSettingId,corpId,hierarchyId, persisterTxn);

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception Occurred - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return isValid;
    }
    public  void resetDefaultSettingDoc(int corpId,String hierarchyId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "resetDefaultSettingDoc(String)";
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.resetDefaultSettingDoc(corpId,hierarchyId, persisterTxn);

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception Occurred - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }

    }

    public void modifyPTTSettingTemplate(String pttSettingId, KnCorpPTTSettingDTO pttSettingDoc, KnPersisterTxn persisterTxn)throws KnCorpBOException {
        final String methodName="modifyPTTSettingTemplate()";
        pttSettingId = pttSettingDoc.get_id();
        knLogger.debug(methodName, "ENTRY pttSettingDocId:",pttSettingId);
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.modifyPTTSettingTemplate(pttSettingId,pttSettingDoc, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }
}
