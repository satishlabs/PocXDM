/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnCorpSublistInfoUtil.java
 * Subsystem:  POC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Namita.P.Nair        21-01-2011      7.0
 * <p/>
 * <p/>
 * #401, 4th Floor, 'Prestige Sigma'
 * No.3, Vittal Mallya Road
 * Bangalore - 560 001
 * www.kodiaknetworks.com
 * All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */
package com.kodiak.xdms.server.corpmgmt.business.helper;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.xdms.server.common.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.business.KnCorpBOException;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dao.persister.ICorpXdmDAO;
import com.kodiak.xdms.server.corpmgmt.dao.persister.ICorpXdmSublistDAO;
import com.kodiak.xdms.server.corpmgmt.dao.persister.db.KnCorpXdmDAO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpSublistInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpSublistSubscDistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSublistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpContactListRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpSublistDistributionRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpSublistRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpSublistListPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnSublistDetailsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.*;

import com.kodiak.logger.KnLogger;
import static com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.Validator.NON_FIRSTNET_FAN;

public class KnCorpSublistInfoUtil {
	private static final KnLogger knLogger = KnLogger.getLogger(KnCorpSublistInfoUtil.class);

    public KnSublistDetailsPersistDTO getPoCSublistIdInfo(Collection<Integer> sublistid, int corpId, String pttServerId,
                                                          KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getPoCSublistIdInfo(Collection<Integer>, int, String,KnPersisterTxn )";
        knLogger.debug( methodName, "ENTRY : ");
        try {
            Collection<Integer> validPoCSublists = new ArrayList<Integer>();
            if (!KnCorpUtil.isObjectNull(sublistid) && !sublistid.isEmpty()) {
                ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
                validPoCSublists = corpXdmDao.getPoCSublistIdInfo(sublistid, corpId, persisterTxn);
            }
            KnSublistDetailsPersistDTO persistDTO = new KnSublistDetailsPersistDTO();
            persistDTO.setSublistIds(validPoCSublists);
            return persistDTO;
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while getting poc sublist information." , e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Collection<Integer> getCommonContactListRejectForGrp(Collection<Integer> sublistid, int corpId,String pttServerId,
                                                          KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getCommonContactListRejectForGrp(Collection<Integer>, int, String,KnPersisterTxn )";
        knLogger.debug( methodName, "ENTRY : ");
        try {
            Collection<Integer> validPoCSublists = new ArrayList<Integer>();
            if (!KnCorpUtil.isObjectNull(sublistid) && !sublistid.isEmpty()) {
                ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
                validPoCSublists = corpXdmDao.getCommonContactListRejectForGrp(sublistid, corpId, persisterTxn);
            }
            /*KnSublistDetailsPersistDTO persistDTO = new KnSublistDetailsPersistDTO();
            persistDTO.setSublistIds(validPoCSublists);*/
            return validPoCSublists;
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while getting poc sublist information." , e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }


    public KnSublistDetailsPersistDTO getSubsMappedSublistId(KnCorpSublistListPersistDTO sublistListDTO,
                                                             String pttServerId, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "getSubsMappedSublistId(KnCorpSublistListPersistDTO, String, KnPersisterTxn)";
        knLogger.debug( methodName, "ENTRY : ");
        try {
            Collection<Integer> removedSublistIds = sublistListDTO.getRemovedSublistIds();
            Collection<Integer> validSubsSublistList = new ArrayList<Integer>();
            if (!KnCorpUtil.isObjectNull(removedSublistIds) && !removedSublistIds.isEmpty()) {
                ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
                validSubsSublistList = corpXdmDao.getSubsMappedSublistId(sublistListDTO, persisterTxn);
            }
            KnSublistDetailsPersistDTO persistDTO = new KnSublistDetailsPersistDTO();
            persistDTO.setSublistIds(validSubsSublistList);
            return persistDTO;
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while getting subscribers mapped sublist information." , e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void updateCorpPairedContListId(int corpId, int pairedContlistId, String xdmsHomePttId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "updateCorpPairedContListId(int, int, String, KnPersisterTxn)";
        knLogger.debug( methodName, "ENTRY :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            corpXdmDao.updateCorpPairedContListId(corpId, pairedContlistId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while updateSubcribersDirectory - " , e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }


    public  Map<Integer, Collection<String>> sublistPushedToSubscribersFrmList(KnIPCorpSublistSubscDistDTO distDTO, String pttServerId
            , KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "sublistPushedToSubscribersFrmList(KnIPCorpSublistSubscDistDTO, String, KnPersisterTxn)";
        knLogger.debug( methodName, "ENTRY :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            return corpXdmDao.sublistPushedToSubscribersFrmList(distDTO, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while retrieving sublist already pushed to subscribers." , e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public  LinkedList<Integer> sublistPushedToSubscribers(String MDN, int corpId, String pttServerId
            , KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "sublistPushedToSubscribers(String, int, String, KnPersisterTxn)";
        knLogger.debug( methodName, "ENTRY :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            return corpXdmDao.sublistPushedToSubscribers(MDN, corpId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while retrieving sublist already pushed to subscribers." , e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public  Map<Integer, Integer> sublistPushedToSubscribersAndCount(LinkedList<Integer> sublistIds, String pttServerId
            , KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "sublistPushedToSubscribers(String, int, String, KnPersisterTxn)";
        knLogger.debug( methodName, "ENTRY :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            return corpXdmDao.sublistPushedToSubscribersAndCount(sublistIds, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while retrieving sublist already pushed to subscribers." , e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, String> sublistPushedSublistCntForSubsc(KnIPCorpSublistSubscDistDTO subsRequestDTO,
                                                               String pttServerId, KnPersisterTxn
            persisterTxn) throws KnCorpBOException {
        final String methodName = "getSublistListContactCnt(KnIPCorpSublistSubscDistDTO, String)";
        try {
            knLogger.debug( methodName, "ENTRY : ");
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            return corpXdmDao.getSublistPushedSublistCntForSubsc(subsRequestDTO, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while retrieving sublist count " ,
                    "already pushed to subscribers." , e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void removeSubscribersSublist(KnIPCorpSublistSubscDistDTO subsRequestDTO,
                                         String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "removeSublistToSubscriberList(KnIPCorpSublistSubscDistDTO, String, KnPersisterTxn)";
        try {
            knLogger.debug( methodName, "ENTRY : ");
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            corpXdmDao.removeSubscribersSublist(subsRequestDTO, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while removing sublist for the subscribers." , e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }


    public Collection<String> getPoCSubscPrsntForSublistFrmMdnList(Collection<String> memberList,
                                                                   int corpListId, String pttServerId,
                                                                   KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getPoCSubscPrsntForSublistFrmMdnList(Collection<String>, int, String, KnPersisterTxn)";
        knLogger.debug( methodName, "ENTRY : ");
        try {
            Collection<String> mdnList = new ArrayList<String>();
            if (memberList != null && !memberList.isEmpty()) {
                ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
                mdnList = corpXdmDao.getPoCSubscPrsntForSublistFrmMdnList(memberList, corpListId, persisterTxn);
            }
            return mdnList;
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while retrieving subscribers present for the " ,
                    "sublist from the input list." , e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Collection<KnCorpSubscriberDTO> getSublistContactList(KnIPCorpSublistInfoDTO sublistInfoDTO,
                                                                 String pttServerId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getSublistContactList(KnIPCorpSublistInfoDTO, String,boolean, KnPersisterTxn)";
        knLogger.debug( methodName, "ENTRY Point");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            return corpXdmDao.getSublistContactList(sublistInfoDTO.getSublistId(), readOnly, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while removing sublist for the subscribers." , e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }

    }

    public KnCorpContactListRespDTO modifySublistDetails(KnIPCorpSublistInfoDTO sublistInfoDTO,
                                                         boolean modifyName, String pttServerId, KnPersisterTxn
            persisterTxn) throws KnCorpBOException {
        final String methodName = "getSublistContactList(KnIPCorpSublistInfoDTO, boolean, String, KnPersisterTxn)";
        knLogger.debug( methodName, "ENTRY :");
        //remove the members
        deleteSublistMembers(sublistInfoDTO.getRemovedMdnList(), sublistInfoDTO.getSublistId(), pttServerId, persisterTxn);

        //add the members
        if (sublistInfoDTO.getFinalMemberList() != null && !sublistInfoDTO.getFinalMemberList().isEmpty()) {
            addSublistMembers(sublistInfoDTO.getFinalMemberList(), sublistInfoDTO.getSublistId(), pttServerId, persisterTxn);
        }
        if (modifyName) {
            modifySublistName(sublistInfoDTO.getSublistName(), sublistInfoDTO.getSublistId(), pttServerId, persisterTxn);
        }
        return new KnCorpContactListRespDTO();

    }

    public KnCorpSublistDTO getSublistInfo(int sublistId, int corpId, String pttServerId, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        return getSublistInfo(sublistId, corpId, pttServerId, false, persisterTxn);
    }

    public KnCorpSublistDTO getSublistInfo(int sublistId, int corpId, String pttServerId, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "getSublistInfo(int, int,boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point");
        KnCorpSublistDTO corpSublistDTO;
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            corpSublistDTO = corpXdmDao.getSublistInfo(sublistId, corpId, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(e.getErrorCode())) {
                throw new KnCorpBOException(KnErrorCodes.BOEntity.SUBLIST_DOES_NOT_BELONG_TO_CORP, "Sublist Doesn't exist", e);
            }
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return corpSublistDTO;
    }

    public int getCommonContactListReject(int sublistId, int corpId, String pttServerId, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "getCommonContactRejectList(int, int, KnPersisterTxn)";
        knLogger.debug( methodName, "ENTRY Point");
        int corpSublistId;
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            corpSublistId = corpXdmDao.getCommonContactListReject(sublistId, corpId, persisterTxn);
        } catch (KnDAOException e) {
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(e.getErrorCode())) {
                throw new KnCorpBOException(KnErrorCodes.BOEntity.SUBLIST_DOES_NOT_BELONG_TO_CORP, "Sublist Doesn't exist", e);
            }
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return corpSublistId;
    }

    public Collection<String> getSubscribersDistToSublist(int sublistId, String pttServerId, KnPersisterTxn
            persisterTxn) throws KnCorpBOException {
        return getSubscribersDistToSublist(sublistId, pttServerId, false, persisterTxn);
    }

    public Collection<String> getSubscribersDistToSublist(int sublistId, String pttServerId, boolean readOnly, KnPersisterTxn
            persisterTxn) throws KnCorpBOException {
        final String methodName = "getSubscribersDistToSublist(int,String,boolean, KnPersisterTxn)";
        try {
            knLogger.debug(methodName, "ENTRY Point: ");
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            return corpXdmDao.getSubscribersDistToSublist(sublistId, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getting subscribers having sublist pushed to it.", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public LinkedHashSet<String> selectSubsDistributionToMultipleSublist(Collection<Integer> sublistIds, String MDN, String pttServerId, KnPersisterTxn
            persisterTxn) throws KnCorpBOException {
        final String methodName = "selectSubsDistributionToMultipleSublist(Collection<Integer>,String,String KnPersisterTxn)";
        try {
            knLogger.debug( methodName, "ENTRY Point: ");
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            return corpXdmDao.selectSubsDistributionToMultipleSublist(sublistIds, MDN, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while getting subscribers having sublist pushed to it." , e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

   public Set<Integer> SelectCorpListIdOnlyPushedViaPrivateGroupList(Collection<Integer> sublistIds, String mdn, String pttServerId, KnPersisterTxn
            persisterTxn) throws KnCorpBOException {
        final String methodName = "SelectCorpListIdOnlyPushedViaPrivateGroupList(Collection<Integer>,String,String KnPersisterTxn)";
        try {
            knLogger.debug( methodName, "ENTRY Point: ");
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            return corpXdmDao.SelectCorpListIdOnlyPushedViaPrivateGroupList(sublistIds, mdn, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while getting corplistID pushed via GroupPrivatelist." , e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }


    public void deleteSublistMembers(Collection<String> removeMembers, int sublistId, String pttServerId,
                                     KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "deleteSublistMembers(Collection<String>,int, String, KnPersisterTxn)";
        try {
            knLogger.debug( methodName, "ENTRY Point :");
            if (removeMembers != null && !removeMembers.isEmpty()) {
                ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
                //remove the members
                corpXdmDao.deleteSublistMembers(removeMembers, sublistId, persisterTxn);
            }
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while deleting the sublist members." , e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void addSublistMembers(Collection<KnCorpSubscriberDTO> addedMdns, int sublistId, String pttServerId,
                                  KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "addSublistMembers(Collection<KnCorpSubscriberDTO>,int,String, KnPersisterTxn)";
        try {
            knLogger.debug( methodName, "ENTRY Point :");
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            //add the members
            corpXdmDao.addSublistMembers(addedMdns, sublistId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while adding the sublist members." , e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void modifySublistName(String sublistName, int sublistId, String pttServerId,
                                  KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "modifySublistName(String,int,String, KnPersisterTxn)";
        try {
            knLogger.debug( methodName, "ENTRY Point :");
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            //add the members
            corpXdmDao.modifySublistName(sublistName, sublistId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while modifying the sublist name." , e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }


    public void createSubListDetails(Collection<KnCorpSubscriberDTO> members, KnCorpSublistDTO sublistDTO,
                                     String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "createSubListDetails(Collection<KnCorpSubscriberDTO>, KnCorpSublistDTO, String, KnPersisterTxn)";
        knLogger.debug( methodName, "ENTRY Point : ");
        createSublist(sublistDTO, pttServerId, persisterTxn);
        if (members != null && !members.isEmpty()) {
            addSublistMembers(members, sublistDTO.getSublistId(), pttServerId, persisterTxn);
        }

    }

    private void createSublist(KnCorpSublistDTO sublistDTO, String pttServerId, KnPersisterTxn
            persisterTxn) throws KnCorpBOException {
        String methodName = "createSublist(KnCorpSublistDTO, String, KnPersisterTxn)";
        knLogger.debug( methodName, "ENTRY Point :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            //add the members
            corpXdmDao.createSublist(sublistDTO, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while creating the sublist." , e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Collection<KnCorpGroupInfoPersistDTO> getGroupsMappedToSublist(int sublistId, String pttServerId, KnPersisterTxn
            persisterTxn) throws KnCorpBOException {
        String methodName = "getGroupsMappedToSublist(int, String, KnPersisterTxn)";
        knLogger.debug( methodName, "ENTRY Point :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            return corpXdmDao.getGroupsMappedToSublist(sublistId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while getting the groups having the sublist mapped to it." , e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public boolean isSublistExistInCorporate(int corpId, String sublistName, String pttServerId, KnPersisterTxn
            persisterTxn) throws KnCorpBOException {
        String methodName = "getSublistCountByName(int,String, String, KnPersisterTxn)";
        knLogger.debug( methodName, "ENTRY Point : ");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            return corpXdmDao.isSublistExistInCorporate(corpId, sublistName, persisterTxn);
        } catch (KnDAOException e) {
        	knLogger.error( methodName, "KnDAOException occured while getting the groups count by name." , e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }


    public int getCorpSublistCount(int corpId, int listType, String pttServerId, boolean readOnly, KnPersisterTxn persisterTxn,String hierarchyId)
            throws KnCorpBOException {
        String methodName = "getCorpSublistCount(int, int, String,boolean, KnPersisterTxn)";
        knLogger.debug( methodName, "ENTRY Point :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            return corpXdmDao.getCorpSublistCount(corpId, listType, readOnly, persisterTxn,hierarchyId);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while getting the corp sublist count." , e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Collection<KnCorpSublistDTO> getAllSharedCorpSublists(int corpId, int nextToken, int fetchSize, String pttServerId, boolean readOnly, KnPersisterTxn
            persisterTxn,String hierarchyId) throws KnCorpBOException {
        final String methodName = "getAllSharedCorpSublists(int, int, int, String,boolean, KnPersisterTxn)";
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            //shared listType - 1
            Collection<KnCorpSublistDTO> sublistList = corpXdmDao.getAllSublist(corpId, 1, nextToken, fetchSize, readOnly, persisterTxn,hierarchyId);
            if (sublistList == null || sublistList.isEmpty()) {
                knLogger.error(methodName, "No Sublist found for the corporate. Throwing CorpBOException.");
                throw new KnCorpBOException(KnErrorCodes.BOEntity.SUBLISTS_LIST_DOES_NOT_EXIST, "Sublist List Doesn't exist");
            }
            return sublistList;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while getting shared corp sublist.", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public KnCorpSublistRespDTO getSublistDetails(String operationType, int sublistId, int corpId, int maxContactLimit,
                                                  String pttServerId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getSublistDetails(int, int, int, String,boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            return corpXdmDao.getSublistDetails(operationType, sublistId, corpId, maxContactLimit, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void deleteSublist(int sublistId, int corpId, String pttServerId, KnPersisterTxn
            persisterTxn) throws KnCorpBOException {
        final String methodName = "deleteSublist(int, int, String, KnPersisterTxn)";
        knLogger.debug( methodName, "ENTRY Point : ");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            corpXdmDao.deleteSublist(sublistId, corpId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while deleting corp sublist." , e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void deleteSublist(List<Integer> sublistId, int corpId, List<String> mdnList, String pttServerId, KnPersisterTxn
            persisterTxn) throws KnCorpBOException {
        final String methodName = "deleteSublist(int, int, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : ");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            corpXdmDao.deleteSublist(sublistId, corpId, mdnList, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while deleting corp sublist.", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public KnCorpSublistDistributionRespDTO getDistributionList(int sublistId, int filterType,
                                                                int maxContactLimit,
                                                                int maxGroupMemberLimit, int corpId, String pttServerId, boolean readOnly,
                                                                KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getDistributionList(int, int, int, int, int, String,boolean, KnPersisterTxn)";
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            KnCorpSublistDistributionRespDTO response =
                    corpXdmDao.getDistributionList(sublistId, filterType, maxContactLimit, maxGroupMemberLimit, corpId, pttServerId, readOnly, persisterTxn);
            if ((response.getContactList() == null || response.getContactList().isEmpty()) &&
                    (response.getGroupList() == null || response.getGroupList().isEmpty())) {
                knLogger.error( methodName, "Not DistributionList found for the sublist. Throwing CorpBOException.");
                throw new KnCorpBOException(KnErrorCodes.BOEntity.NO_SUBLIST_DISTRIBUTION_FOUND, "Sublist LIST Doesn't exist");
            }
            return response;
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while retrieving corp sublist distribution list." , e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * This method returns the sulidt details pushed to the group
     * @param groupId
     * @param pttServerId
     * @param persisterTxn
     * @return
     * @throws KnCorpBOException
     */
    public List<KnCorpSublistDTO> getGroupsSublistList(int groupId, String pttServerId, boolean readOnly,
                                                       KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getGroupsSublistList(int,  String, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            return corpXdmDao.getGroupsSublistList(groupId, pttServerId, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<Integer, Integer> fetchAndUpdateSublistEtag(Collection<Integer> sublistIdList, String pttServerId, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "fetchAndUpdateSublistEtag(Collection<Integer>,  String, KnPersisterTxn)";
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            return corpXdmDao.fetchAndUpdateSublistEtag(sublistIdList, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while fetching and updating sublist etag." , e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void updateSublistsSubscribersContactCount(Collection<Integer> sublistIdList, int maxSubscContCount, int
            maxGrpSubscCount, int maxDispGrpSubscCount, String pttServerId, KnPersisterTxn persisterTxn, int maxBGMemCount)
            throws KnCorpBOException {
        final String methodName = "updateSublistsSubscribersContactCount(Collection<Integer>,  int, int, int, String, KnPersisterTxn)";
        knLogger.debug( methodName, "ENTRY Point :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            corpXdmDao.updateSublistsSubscribersContactCount(sublistIdList, maxSubscContCount, maxGrpSubscCount,
                    maxDispGrpSubscCount, persisterTxn, maxBGMemCount);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while updating the subcribers contact count." , e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public int getSublistCountByNameExcludingCurrentSublist(int corpId, String sublistName, int sublistId,
                                                            String xdmsHomePttId,
                                                            KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getSublistCountByNameExcludingCurrentSublist(int, String, int, String, KnPersisterTxn)";
        knLogger.debug( methodName, "ENTRY Point: ");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            return corpXdmDao.getSublistCountByNameExcludingCurrentSublist(corpId, sublistName, sublistId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while getSublistCountByNameExcludingCurrentSublist." , e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<Integer, Collection<String>> getSubcriberSublistMemberShipList(Collection<String> mdnList,
                                                                              int corpId, String xdmsHomePttId,
                                                                              KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getSubcriberSublistMemberShipList(Collection<String>, int, String, KnPersisterTxn)";
        knLogger.debug( methodName, "ENTRY Point :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            return corpXdmDao.getSubcriberSublistMemberShipList(mdnList, corpId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while getSubcriberSublistMemberShipList." , e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void deleteMembersFromAllSublist(Map<Integer, Collection<String>> sublistMemberMap,
                                            String xdmsHomePttId,
                                            KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "deleteMembersFromAllSublist(Map<Integer, Collection<String>>, String, KnPersisterTxn)";
        knLogger.debug( methodName, "ENTRY Point :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            corpXdmDao.deleteMembersFromAllSublist(sublistMemberMap, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while deleteMembersFromAllSublist." , e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void changeMDNImpactInSublist(Map<Integer, Collection<String>> sublistMemberMap,
                                         String newMDN, int corpId, String xdmsHomePttId,
                                         KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "changeMDNImpactInSublist(Map<Integer, Collection<String>>, String, int, String, String)";
        knLogger.debug( methodName, "Entry Point :");
        try {
            Collection<Integer> sublistIdList = sublistMemberMap.keySet();
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            //deleting old member from all sublist.
            corpXdmDao.deleteMembersFromAllSublist(sublistMemberMap, persisterTxn);
            //adding changed member (new mdn) in all sublists.
            Map<Integer, Collection<String>> memberToAdd = new HashMap<Integer, Collection<String>>();
            Collection<Integer> idList = sublistMemberMap.keySet();
            for (int id : idList) {
                Collection<String> newMdnList = new ArrayList<String>();
                newMdnList.add(newMDN);
                memberToAdd.put(id, newMdnList);
            }
            knLogger.debug( methodName, "memberToAdd - " , memberToAdd);
            corpXdmDao.insertMemberInAllSublists(memberToAdd, corpId, persisterTxn);
            //updating the sublist etag.
            corpXdmDao.fetchAndUpdateSublistEtag(sublistIdList, persisterTxn);

        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while modifying sublist for change mdn - " , e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Collection<Integer> getCorpSublistIdList(KnIPCorpInfoDTO corpInfoDTO, String xdmsHome, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "getCorpSublistIdList(KnIPCorpInfoDTO, String, KnPersisterTxn)";
        knLogger.debug( methodName, "ENTRY Point :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
            return corpXdmDao.getCorpSublistIdList(corpInfoDTO, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while getCorpSublistIdList." , e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void deleteAllSublist(Collection<Integer> sublistIdsList, String xdmsHome, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "deleteAllSublist(Collection<Integer>, String, KnPersisterTxn)";
        knLogger.debug( methodName, "ENTRY Point sublistIdsList:",sublistIdsList);
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
            corpXdmDao.deleteAllSublist(sublistIdsList, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while deleteAllSublist." , e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void deleteAllSublistMembers(int sublistId, String xdmsHomePttId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "deleteAllSublistMembers(int, String, KnPersisterTxn)";
        knLogger.debug( methodName, "ENTRY Point : ");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            corpXdmDao.deleteAllSublistMembers(sublistId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while deleteAllSublist." , e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void deleteCorpListDistReference(int sublistId, String xdmsHomePttId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "deleteCorpListDistReference(int, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : ");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            corpXdmDao.deleteCorpListDistReference(sublistId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while deleteCorpListDistReference.", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void deleteCorpListDistGroupReference(int sublistId, String xdmsHomePttId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "deleteCorpListDistGroupReference(int, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : ");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            corpXdmDao.deleteCorpListDistGroupReference(sublistId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while deleteCorpListDistGroupReference.", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void deleteSublistRefAndSublist(int subListId, String xdmsHomePttId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "deleteCorpListInfo(int, String, KnPersisterTxn)";
        knLogger.debug( methodName, "ENTRY Point : ");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            corpXdmDao.deleteSublistRefAndSublist(subListId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while deleteCorpListInfo." , e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Collection<Integer> getGroupsSublistListFromDB(int groupId, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getGroupsSublistListFromDB(int, String, KnPersisterTxn)";
        knLogger.debug( methodName, "ENTRY Point :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
            return corpXdmDao.getGroupsSublistListFromDB(groupId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while getGroupsSublistListFromDB." , e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public ArrayList<Integer> getSharedSublistFromList(Collection<Integer> sublistLists,
                                                       String xdmsHomePttId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getSharedSublistFromList(Collection<Integer>, String, KnPersisterTxn)";
        knLogger.debug( methodName, "ENTRY Point :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            return corpXdmDao.getSharedSublistFromList(sublistLists, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while getGroupsSublistListFromDB." , e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }
    
	public ArrayList<Integer> getNonSharedSublistFromList(Collection<Integer> sublistLists, String xdmsHomePttId,
			KnPersisterTxn persisterTxn) throws KnCorpBOException {
		final String methodName = "getNonSharedSublistFromList(Collection<Integer>, String, KnPersisterTxn)";
		knLogger.debug(methodName, "ENTRY Point :");
		try {
			ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
			return corpXdmDao.getNonSharedSublistFromList(sublistLists, persisterTxn);
		} catch (KnDAOException e) {
			knLogger.error(methodName, "KnDAOException occured while getNonSharedSublistFromList.", e);
			throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
		}
	}
    

    public ArrayList<Integer> getEmptySublistFrmList(ArrayList<Integer> sharedSublists, String xdmsHomePttId,
                                                     KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getSharedSublistFromList(ArrayList<Integer>, String, KnPersisterTxn)";
        knLogger.debug( methodName, "ENTRY Point :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            return corpXdmDao.getEmptySublistFrmList(sharedSublists, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while getEmptySublistFrmList." , e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void insertCorplistMembers(Map<Integer, Collection<KnCorpSubscriberDTO>> listMemberMap, String xdmHome,
                                      KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "insertCorplistMembers(Map<Integer, Collection<KnCorpSubscriberDTO>>, String, KnPersisterTxn)";
        knLogger.debug( methodName, "ENTRY Point : ");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmHome);
            corpXdmDao.insertCorplistMembers(listMemberMap, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while insertCorplistMembers." , e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

     public Map<Integer, Collection<String>> getSubcriberSublistMemberShipListForAllCorporate(Collection<String> mdnList,
            String xdmsHomePttId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getSubcriberSublistMemberShipListForAllCorporate(Collection<String>, String, KnPersisterTxn)";
        knLogger.debug( methodName, "ENTRY Point : ");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            return corpXdmDao.getSubcriberSublistMemberShipListForAllCorporate(mdnList, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while getSubcriberSublistMemberShipListForAllCorporate." , e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
     }
    public Map<Integer, Collection<String>> getSubcriberSublistMemberShipListAsExtContact(Collection<String> mdnList,
                                                                              int corpId, String xdmsHomePttId,
                                                                              KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getSubcriberSublistMemberShipListAsExtContact(Collection<String>, int, String, persisterTxn)";
        knLogger.debug( methodName, "ENTRY Point :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            return corpXdmDao.getSubcriberSublistMemberShipListAsExtContact(mdnList, corpId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while getSubcriberSublistMemberShipListAsExtContact." , e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * Method added to update the sublist etag
     * @param sublistId
     * @param etag
     * @param xdmsHomePttId
     * @param persisterTxn
     * @throws KnCorpBOException
     */
    public void updateSublistEtag(int sublistId, long etag, String xdmsHomePttId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
         final String methodName = "updateSublistEtag(int, long, String, KnPersisterTxn)";
        knLogger.debug( methodName, "ENTRY Point :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            corpXdmDao.updateSublistEtag(sublistId,etag,  persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while fetching and updating sublist etag." , e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * Method added to retrieve the external members of the corporate
     * @param corpId
     * @param xdmsHomePttId
     * @param persisterTxn
     * @return
     * @throws KnCorpBOException
     */
    public Map<String,KnCorpSubscriberDTO> getCorpExternalSubscriber(int corpId, String xdmsHomePttId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
         final String methodName = "getExternalConatctsList(int, String, KnPersisterTxn)";
        knLogger.debug( methodName, "ENTRY Point : ");
        try {
            ICorpXdmDAO customCorpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            return customCorpXdmDao.getCorpExternalSubscriber(corpId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while getIdListFrmDB - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * This method returns a Map of sublist id and members in mdnList
     * @param mdnList
     * @param xdmsHomePttId
     * @param persisterTxn
     * @return
     * @throws KnCorpBOException
     */
    public Map<Integer, List<String>> getSubsSublistList(List<String> mdnList, String xdmsHomePttId, KnPersisterTxn persisterTxn) throws KnCorpBOException{
        try {
            ICorpXdmDAO customCorpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            return customCorpXdmDao.getSubsSublistList(mdnList, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * This method deleted the sublist members in mdnList from dg.corplistmember table.
     * @param mdnList
     * @param pttServerId
     * @param persisterTxn
     * @throws KnCorpBOException
     */
    public void deleteCorpSublistMemList(List<String> mdnList, String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            corpXdmDao.deleteCorpSublistMemList(mdnList, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * This methods returns the empty sublists fromsublistIds.
     * @param sublistIds
     * @param pttServerId
     * @param persisterTxn
     * @return
     * @throws KnCorpBOException
     */
    public List<Integer> getEmptySublistIds(List<Integer> sublistIds, String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            return  corpXdmDao.getEmptySublistIds(sublistIds, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * This method deleted the Sublist distribution entries for sublistIds and mdnList.
     * @param sublistIds
     * @param mdnList
     * @param pttServerId
     * @param persisterTxn
     * @throws KnCorpBOException
     */
    public void deleteCorpListDistribution(List<Integer> sublistIds, List<String> mdnList, String pttServerId, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
        try {
            corpXdmDao.deleteCorpListDistribution(sublistIds, mdnList, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void deleteCorpListDistributionForMdn(List<String> mdnList, String xdmsHomePttId, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "deleteCorpListDistributionForMdn(List<String>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : mdnList - ", mdnList == null ? mdnList : KnGDPRTemplate.mdnList(mdnList));
        if (mdnList == null || mdnList.isEmpty()) {
            knLogger.debug(methodName, "mdnList is null or empty, skipping.");
            return;
        }
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            corpXdmDao.deleteCorpListDistForMdn(mdnList, persisterTxn);
            knLogger.debug(methodName, "EXIT");
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while deleteCorpListDistributionForMdn.", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * This method deleted sublist entries from corplistinfo table
     * @param sublistIds
     * @param pttServerId
     * @param persisterTxn
     * @throws KnCorpBOException
     */
    public void deleteSublistInfoList(List<Integer> sublistIds, String pttServerId, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
        try {
            corpXdmDao.deleteSublistInfoList(sublistIds, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * This method deletes sublists entries from corplistinfo table using corpId
     * @param corpId
     * @param pttServerId
     * @param persisterTxn
     * @throws KnCorpBOException
     */
    public void deleteAllSublistsInCorp(int corpId, String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "deleteAllSublistsInCorp(corpId, pttServerId, persisterTxn)";
        knLogger.debug(methodName, " Entry : ", corpId);
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
        try {
            corpXdmDao.deleteAllSubListsOfCorporate(corpId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName," Exception occured while deleting subLists.");
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public List<KnCorpSubscriberDTO> getSublistDistinctMembers(Collection<Integer> addedSublistIds, int corpId, String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
        try {
           return  corpXdmDao.getSublistDistinctMembers(addedSublistIds, corpId, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * This method is to get the valid sublist Ids in the corporation from a list of Ids.
     * @param sublistid
     * @param corpId
     * @param pttServerId
     * @param persisterTxn
     * @return
     * @throws KnCorpBOException
     */
    public List<Integer> getValidSublistIdInfo(Collection<Integer> sublistid, int corpId, String pttServerId,
           KnPersisterTxn persisterTxn) throws KnCorpBOException {
        try {
            Collection<Integer> validPoCSublists = new ArrayList<>();
            if (!KnCorpUtil.isObjectNull(sublistid) && !sublistid.isEmpty()) {
                ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
                validPoCSublists = corpXdmDao.getPoCSublistIdInfo(sublistid, corpId, persisterTxn);
            }
            return new ArrayList<>(validPoCSublists);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public List<String> selectGroupMemberList(int grpId, String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
        try {
            return corpXdmDao.selectGroupMemberList(grpId, false, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * Method to retrieve group member list for a requested group Id. This is read only method.
     */
    public List<String> selectGroupMemberList(int grpId, String pttServerId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
        try {
            return corpXdmDao.selectGroupMemberList(grpId, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * This method is the retrieve the list of sublist where MDN exist a member.
     * @param mdn
     * @param xdmsHomePttId
     * @param persisterTxn
     * @return
     * @throws KnCorpBOException
     */
    public List<Integer> getSubscrAllSublists(String mdn, String xdmsHomePttId, boolean readOnly, KnPersisterTxn
            persisterTxn) throws KnCorpBOException {
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
        try {
            return corpXdmDao.getSubscrAllSublists(mdn, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * This method is to filter out the list of sublist ids based on corpId and sublist type.
     * @param sublistIds
     * @param corpId
     * @param sublistType
     * @param xdmsHomePttId
     * @param persisterTxn
     * @return
     */
    public Map<Integer, KnCorpSublistDTO> filterSublists(List<Integer> sublistIds, int corpId, int sublistType, String
            xdmsHomePttId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
        try {
            return corpXdmDao.filterSublists(sublistIds, corpId, sublistType, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }


    public List<String> getSublistMembers(List<Integer> sublistIds, String xdmsHomePttId, KnPersisterTxn persisterTxn) throws KnCorpBOException{
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
        try {
            return  corpXdmDao.getSublistMembers(sublistIds, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void deleteSublistMembersForRequestMDN(Collection<Integer> sublistIds, String MDN, String pttServerId,
                                                  KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "deleteSublistMembersForRequestMDN(Collection<Integer>,String, String, KnPersisterTxn)";
        try {
            knLogger.debug( methodName, "ENTRY Point :");
            if (sublistIds != null && !sublistIds.isEmpty()) {
                ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
                //remove the members
                corpXdmDao.deleteSublistMembersForRequestMDN(sublistIds, MDN, persisterTxn);
            }
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while deleting the sublist members." , e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * This method is used to get the sublist details by name of the sublist
     * @param corpId
     * @param subPrefix
     * @param xdmsHomePttId
     * @param persisterTxn
     * @return
     */
    public KnCorpSublistDTO getSublistDetailsByName(int corpId, String subPrefix, String xdmsHomePttId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
        try {
            return  corpXdmDao.getSublistDetailsByName(corpId,subPrefix, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * Method added to retrieve the external members of the corporate
     * @param corpId
     * @param xdmsHomePttId
     * @param persisterTxn
     * @return
     * @throws KnCorpBOException
     */
    public List<KnCorpSubscriberDTO> getCorpExtContact(int corpId, String xdmsHomePttId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getCorpExtContact(int, String, KnPersisterTxn)";
        knLogger.debug( methodName, "ENTRY Point : ");
        try {
            ICorpXdmDAO customCorpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            return customCorpXdmDao.getCorpExtContact(corpId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while getting extCorpContact - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<Integer,Integer> getAllTypePoCSublistIdInfo(Collection<Integer> sublistid, int corpId, String pttServerId,
                                                          KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getAllTypePoCSublistIdInfo(Collection<Integer>, int, String,KnPersisterTxn )";
        knLogger.debug( methodName, "ENTRY : ");
        try {
            Map<Integer,Integer> validPoCSublistMap = new HashMap<>();
            if (!KnCorpUtil.isObjectNull(sublistid) && !sublistid.isEmpty()) {
                ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
                validPoCSublistMap = corpXdmDao.getAllTypePoCSublistIdInfo(sublistid, corpId, persisterTxn);
            }
           return validPoCSublistMap;
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while getting poc sublist information." , e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void createBulkSubListDetails(List<KnCorpSublistDTO> sublistDTOList, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException{
        String methodName = "createBulkSubListDetails()";
        knLogger.debug( methodName, "ENTRY Point :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
            corpXdmDao.createSublist(sublistDTOList, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while creating the sublist." , e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public int getSubListMemberCount(int subListId, String pttServerId, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
        try {
            return corpXdmDao.getSubListMemberCount(subListId, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }
    public Map<String, Integer> commContctListCntForSubsc(KnIPCorpSublistSubscDistDTO subsRequestDTO,
                                                               String pttServerId, KnPersisterTxn
                                                                       persisterTxn) throws KnCorpBOException {
        final String methodName = "commContctListCntForSubsc(KnIPCorpSublistSubscDistDTO, String)";
        try {
            knLogger.debug( methodName, "ENTRY : ");
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            return corpXdmDao.commContctListCntForSubsc(subsRequestDTO, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while retrieving sublist count " ,
                    "already pushed to subscribers." , e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public int getCorpIdFromCorpListInfo(int sublistId, String xdmsHome, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "getCorpSublistIdList(KnIPCorpInfoDTO, String, KnPersisterTxn)";
        knLogger.debug( methodName, "ENTRY Point :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
            return corpXdmDao.getCorpIdFromCorpListInfo(sublistId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while getCorpSublistIdList." , e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public List<String> getCommonContactListForMdns(String mdn, String xdmsHome, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "getCommonContactListForMdns(List<String>, String, KnPersisterTxn)";
        knLogger.debug( methodName, "ENTRY Point :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
            return corpXdmDao.getCommonContactListForMdns(mdn, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while getCorpSublistIdList." , e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public List<String> getAllSublistContactMdns(String mdn, String xdmsHome, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "getCommonContactListForMdns(List<String>, String, KnPersisterTxn)";
        knLogger.debug( methodName, "ENTRY Point :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
            return corpXdmDao.getAllSublistContactMdns(mdn, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while getCorpSublistIdList." , e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String,List<String>> getAuAndCommonTuMapping(List<String> authMdns,List<String> targetMdns, String xdmsHome, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "getAuAndCommonTuMapping(List<String>, List<String>, KnPersisterTxn)";
        knLogger.debug( methodName, "ENTRY Point :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
            return corpXdmDao.getAuAndCommonTuMapping(authMdns,targetMdns, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while getCorpSublistIdList." , e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public List<String> getProfileMdnByBaseMdns(List<String> baseMdns,String xdmsHome, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "getProfileMdnByBaseMdns(List<String>, List<String>, KnPersisterTxn)";
        knLogger.debug( methodName, "ENTRY Point :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
            return corpXdmDao.getProfileMdnByBaseMdns(baseMdns, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while getProfileMdnByBaseMdns." , e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void deleteFromMcpttPerm(Map<String, Collection<String>> authTargetMap,String xdmsHome, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "deleteFromMcpttPerm(List<String>, List<String>, KnPersisterTxn)";
        knLogger.debug( methodName, "ENTRY Point :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
            corpXdmDao.deleteFromMcpttPerm(authTargetMap, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while getCorpSublistIdList." , e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public List<String> getNonExisitingGroupMemberInPrivSublist(Integer groupId, String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
        try {
            return corpXdmDao.getNonExisitingGroupMemberInPrivSublist(groupId, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<Integer, List<String>> getNonExisitingGroupMemberInPrivSublistForGroups(Collection<Integer> groupIds, String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
        try {
            return corpXdmDao.getNonExisitingGroupMemberInPrivSublistForGroups(groupIds, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }
    public Map<Integer,Collection<Integer>> getBulkGroupsSublistListFromDB(Collection<Integer> groupIds, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getBulkGroupsSublistListFromDB(Collection<Integer>, String, KnPersisterTxn)";
        knLogger.debug( methodName, "ENTRY Point :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
            return corpXdmDao.getBulkGroupsSublistListFromDB(groupIds, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while getBulkGroupsSublistListFromDB." , e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void validateSublistParams(List<Integer> sublistIdList,Map<String, Object> customParams,String xdmsHome,KnPersisterTxn persisterTxn) throws Exception {
        ICorpXdmSublistDAO xdmServerDAO = new KnCorpXdmDAO(xdmsHome);
        String methodName = "validateSublistParams()";
        //This is for owner corporate incontext id validation.
        try {
            boolean isValid = xdmServerDAO.isSublistValidRequest(sublistIdList,customParams, persisterTxn);
            if (!isValid) {
                throw new KnCorpBOValidationException(NON_FIRSTNET_FAN,
                        "Invalid owner context ids", "", "", "", "DataType", "");
            }
        }catch (KnDAOException | KnCorpBOValidationException e) {
            knLogger.error(methodName, "KnDAOException occured while validating hirerchy check-  ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String,Integer> getPaginatedContatInfo(List<String> contactMdns, int startIndex, int endIndex, boolean readOnly, String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
        try {
            return corpXdmDao.getPaginatedContatInfo(contactMdns,startIndex,endIndex,readOnly, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Integer getUniqueContactListCount(String contactMdns, boolean readOnly, String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
        try {
            return corpXdmDao.getUniqueContactListCount(contactMdns,readOnly, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }
}