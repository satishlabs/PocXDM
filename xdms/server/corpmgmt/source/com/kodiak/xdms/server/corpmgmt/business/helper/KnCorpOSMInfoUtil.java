/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.helper;

import com.kodiak.common.commdto.request.KnXDMOSMInfoRequestDTO;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.corpmgmt.business.KnCorpBOException;
import com.kodiak.xdms.server.corpmgmt.dao.persister.ICorpXdmDAO;
import com.kodiak.xdms.server.corpmgmt.dao.persister.db.KnCorpXdmDAO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpOSMGroupListRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpOSMInfoListDetailsRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpOSMInfoListRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpOSMPersistDTO;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class KnCorpOSMInfoUtil{

    private static final KnLogger knLogger = KnLogger.getLogger(KnCorpOSMInfoUtil.class);

    public void createOSMList(KnCorpOSMPersistDTO corpOSMPersistDTO, String xdmsHome, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        String methodName = "createOSMList()";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.createOSMList(corpOSMPersistDTO, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void updateOSMList(KnCorpOSMPersistDTO corpOSMPersistDTO, String xdmsHome, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        String methodName = "updateOSMList()";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.updateOSMList(corpOSMPersistDTO, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void deleteOSMList(String corpId,String osmListId, String xdmsHome, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        String methodName = "deleteOSMList()";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.deleteOSMList(corpId,osmListId, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<Integer,Integer> getOSMListIdAndDefaultMap(String corpId, String xdmsHome, KnPersisterTxn persisterTxn)throws KnCorpBOException {
        knLogger.info("Inside KnCorpOSMInfoUtil class getOSMListIdAndDefaultMap method ");
        return getOSMListIdAndDefaultMap(corpId, xdmsHome, false, persisterTxn);
    }

    public Map<Integer,Integer> getOSMListIdAndDefaultMapByHierarchyId(String corpId, String xdmsHome, KnPersisterTxn persisterTxn,String hierarchyId)throws KnCorpBOException {
        knLogger.info("Inside KnCorpOSMInfoUtil class getOSMListIdAndDefaultMapByHierarchyId method ");
        return getOSMListIdAndDefaultMapByHierarchyId(corpId, xdmsHome, false, persisterTxn,hierarchyId);
    }

    public Map<Integer,Integer> getOSMListIdAndDefaultMap(String corpId, String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn)throws KnCorpBOException {
        String methodName = "getOSMListIdAndDefaultMap(String, String, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        Map<Integer,Integer> map =null;
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            map = xdmDAO.getOSMListIdAndDefaultMap(corpId, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        knLogger.debug(methodName, "EXIT : count",map);
        return map;
    }

    public Map<Integer,Integer> getOSMListIdAndDefaultMapByHierarchyId(String corpId, String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn,String hierarchyId)throws KnCorpBOException {
        String methodName = "getOSMListIdAndDefaultMapByHierarchyId(String, String, boolean, KnPersisterTxn, String)";
        knLogger.debug(methodName, "ENTRY :");
        Map<Integer,Integer> map =null;
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            map = xdmDAO.getOSMListIdAndDefaultMapByHierarchyId(corpId, readOnly, persisterTxn,hierarchyId);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        knLogger.debug(methodName, "EXIT : count",map);
        return map;
    }

    public List<KnXDMOSMInfoRequestDTO> getUniqueFlieldsOSMInfoList(String osmListId, String xdmsHome, KnPersisterTxn persisterTxn)throws KnCorpBOException {
        String methodName = "getUniqueFlieldsOSMInfoList()";
        knLogger.debug(methodName, "ENTRY :");
        List<KnXDMOSMInfoRequestDTO>osmList =null;
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            osmList = xdmDAO.getUniqueFlieldsOSMInfoList(osmListId, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        knLogger.debug(methodName, "EXIT : osmList",osmList);
        return osmList;
    }

    public KnCorpOSMInfoListRespDTO getOSMListByCorp(String corpId, String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn)throws KnCorpBOException {
        String methodName = "getOSMListByCorp()";
        knLogger.debug(methodName, "ENTRY :");
        KnCorpOSMInfoListRespDTO osmInfoList =null;
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            osmInfoList = xdmDAO.getOSMListByCorp(corpId, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        knLogger.debug(methodName, "EXIT : osmInfoList",osmInfoList);
        return osmInfoList;
    }

    public KnCorpOSMInfoListRespDTO getOSMListByCorpAndHierarchyId(String corpId, String hierarchyId, String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn)throws KnCorpBOException {
        String methodName = "getOSMListByCorp()";
        knLogger.debug(methodName, "ENTRY :");
        KnCorpOSMInfoListRespDTO osmInfoList =null;
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            osmInfoList = xdmDAO.getOSMListByCorpAndHierarchyId(corpId,hierarchyId, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        knLogger.debug(methodName, "EXIT : osmInfoList",osmInfoList);
        return osmInfoList;
    }

    public KnCorpOSMGroupListRespDTO getOSMGroupListByCorpAndOSMId(String corpId, Collection<String> OSMListIds ,
                                                                   String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn)throws KnCorpBOException {
        String methodName = "getOSMGroupListByCorpAndOSMId(String,Collection<String>,String,boolean,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        KnCorpOSMGroupListRespDTO osmInfoList =null;
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            osmInfoList = xdmDAO.getOSMGroupListByCorpAndOSMId(corpId, OSMListIds, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        knLogger.debug(methodName, "EXIT : osmInfoList",osmInfoList);
        return osmInfoList;
    }

    public KnCorpOSMInfoListDetailsRespDTO getOSMListDetailsByListIdReadonly(String corpId, String osmListId, String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "getOSMListDetailsByListIdReadonly()";
        KnCorpOSMInfoListDetailsRespDTO osmInfoList = null;
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            osmInfoList = xdmDAO.getOSMListDetailsByListId(corpId, osmListId, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName,"Error occured :",e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return osmInfoList;
    }

    public KnCorpOSMInfoListDetailsRespDTO getOSMListDetailsByListId(String corpId, String osmListId, String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "getOSMListDetailsByListId()";
        KnCorpOSMInfoListDetailsRespDTO osmInfoList =null;
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            osmInfoList = xdmDAO.getOSMListDetailsByListId(corpId, osmListId, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return osmInfoList;
    }

    public void assignOSMIdToGroup(String corpId, String osmListId,
                                          Collection<String> assignedOSMIdToGroupIds,
                                          Collection<String> removedOSMIdFromGroupIds,
                                          String xdmsHome, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        String methodName = "assignOSMIdToGroup()";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.assignOSMIdToGroup(corpId,osmListId,assignedOSMIdToGroupIds,removedOSMIdFromGroupIds,xdmsHome, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * Method to delete all OSM lists of a corporate account using corpId.
     * @param corpId
     * @param xdmsHome
     * @param persisterTxn
     * @throws KnCorpBOException
     */
    public void deleteAllOSMLists(Integer corpId,String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "deleteAllOSMLists(corpId,xdmsHome,persisterTxn)";
        knLogger.debug(methodName, "ENTRY :",corpId);
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.deleteOSMListOnDeleteCorpProfile(corpId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName," Exception occurued while deleting OSM lists of a corporate account.");
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        knLogger.debug(methodName, " OSM lists deleted successfully.");
    }
}
